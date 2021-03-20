package com.velocitypowered.proxy.plugin.omnibus;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginManager;
import org.apache.logging.log4j.LogManager;
import space.arim.omnibus.events.AsyncEvent;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.events.ListenerPriorities;
import space.arim.omnibus.events.RegisteredListener;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class OmnibusEventManager implements EventManager {

  private final PluginManager pluginManager;
  private final EventBus eventBus;
  private final ExportAssistant exportAssistant;

  private final ConcurrentMap<PluginContainer, Map<Listener, Set<RegisteredListener>>> listeners = new ConcurrentHashMap<>();

  public OmnibusEventManager(PluginManager pluginManager, EventBus eventBus, ExportAssistant exportAssistant) {
    this.pluginManager = pluginManager;
    this.eventBus = eventBus;
    this.exportAssistant = exportAssistant;
  }

  private PluginContainer getPluginContainer(Object plugin) {
    return pluginManager.fromInstance(plugin).orElseThrow(() -> new IllegalArgumentException("No plugin found"));
  }

  private static byte priorityFromPostOrder(PostOrder postOrder) {
    switch (postOrder) {
    case FIRST:
      return ListenerPriorities.LOWER;
    case EARLY:
      return ListenerPriorities.LOW;
    case NORMAL:
      return ListenerPriorities.NORMAL;
    case LATE:
      return ListenerPriorities.HIGH;
    case LAST:
      return ListenerPriorities.HIGHER;
    default:
      throw new IllegalArgumentException("Unknown PostOrder " + postOrder);
    }
  }

  /*
   * Registering listeners
   */

  private void addToRegisteredListeners(PluginContainer plugin, Listener listener,
                                        Consumer<Set<RegisteredListener>> action) {
    listeners.compute(plugin, (p, listenersMap) -> {
      if (listenersMap == null) {
        listenersMap = new HashMap<>();
      }
      Set<RegisteredListener> registeredListeners = listenersMap.computeIfAbsent(listener, (l) -> new HashSet<>());
      action.accept(registeredListeners);

      if (registeredListeners.isEmpty()) {
        listenersMap.remove(listener); // Self cleaning
      }
      return listenersMap.isEmpty() ? null : listenersMap; // Self cleaning
    });
  }

  private void checkAccessible(Object listener) {
    Class<?> listenerClass = listener.getClass();
    if (!Modifier.isPublic(listenerClass.getModifiers())) {
      throw new IllegalArgumentException("Listener " + listener + " must be publicly accessible");
    }
    Module ourModule = getClass().getModule();
    exportAssistant.exportClassIfPossible(listenerClass, ourModule);
    if (!listenerClass.getModule().isExported(listenerClass.getPackageName(), ourModule)) {
      throw new IllegalArgumentException("Listener " + listener + " must be exported to com.velocitypowered.proxy");
    }
  }

  private <E> RegisteredListener registerAnnotatedMethod(Object listenerObject, Class<E> eventClass,
                                                         Method method, Subscribe subscribe) {
    MethodHandle methodHandle;
    try {
      methodHandle = MethodHandles.lookup().unreflect(method);
    } catch (IllegalAccessException ex) {
      throw new RuntimeException("Unable to create EventExecutor for method " + method, ex);
    }
    byte priority = priorityFromPostOrder(subscribe.order());
    Consumer<E> eventConsumer = new MethodHandleEventConsumer<>(methodHandle.bindTo(listenerObject));
    return eventBus.getDriver().registerListener(eventClass, priority, eventConsumer);
  }

  @Override
  public void register(Object pluginObject, Object listenerObject) {
    PluginContainer plugin = getPluginContainer(pluginObject);
    checkAccessible(listenerObject);
    AnnotatedListener annotatedListener = new AnnotatedListener(listenerObject);
    MethodScan methodScan = new MethodScan(listenerObject);

    addToRegisteredListeners(plugin, annotatedListener, (registeredListeners) -> {
      methodScan.scan((method, subscribe) -> {
        Class<?> eventClass = method.getParameterTypes()[0];
        if (!eventClass.getModule().isExported(eventClass.getPackageName())) {
          throw new IllegalArgumentException("Event class " + eventClass + " must be unconditionally exported");
        }
        registeredListeners.add(registerAnnotatedMethod(listenerObject, eventClass, method, subscribe));
      });
    });
  }

  @Override
  public <E> void register(Object pluginObject, Class<E> eventClass, PostOrder postOrder, EventHandler<E> handler) {
    PluginContainer plugin = getPluginContainer(pluginObject);
    byte priority = priorityFromPostOrder(postOrder);
    Consumer<E> eventConsumer = new EventHandlerEventConsumer<>(handler);
    EventHandlerListener handlerListener = new EventHandlerListener(handler);

    addToRegisteredListeners(plugin, handlerListener, (registeredListeners) -> {
      registeredListeners.add(eventBus.getDriver().registerListener(eventClass, priority, eventConsumer));
    });
  }

  /*
   * Unregistering listeners
   */

  @Override
  public void unregisterListeners(Object pluginObject) {
    PluginContainer plugin = getPluginContainer(pluginObject);

    Map<Listener, Set<RegisteredListener>> listenersMap = listeners.remove(plugin);
    if (listenersMap == null) {
      return;
    }
    listenersMap.values().forEach((listenersSet) -> {
      listenersSet.forEach(eventBus::unregisterListener);
    });
  }

  @Override
  public void unregisterListener(Object pluginObject, Object listener) {
    PluginContainer plugin = getPluginContainer(pluginObject);
    unregisterOne(plugin, new AnnotatedListener(listener));
  }

  @Override
  public <E> void unregister(Object pluginObject, EventHandler<E> handler) {
    PluginContainer plugin = getPluginContainer(pluginObject);
    unregisterOne(plugin, new EventHandlerListener(handler));
  }

  private void unregisterOne(PluginContainer plugin, Listener listener) {
    listeners.computeIfPresent(plugin, (p, existingListenersMap) -> {
      Set<RegisteredListener> listenersSet = existingListenersMap.remove(listener);
      if (listenersSet == null) {
        return existingListenersMap;
      }
      listenersSet.forEach(eventBus::unregisterListener);
      return existingListenersMap.isEmpty() ? null : existingListenersMap; // Self cleaning
    });
  }

  /*
   * Firing events
   */

  private static final int TIMEOUT_SECONDS = 10;

  @Override
  public <E> CompletableFuture<E> fire(E event) {
    if (event instanceof AsyncEvent) {
      @SuppressWarnings("unchecked")
      CompletableFuture<E> future = (CompletableFuture<E>) eventBus.fireAsyncEvent((AsyncEvent) event);
      return future.orTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    eventBus.getDriver().fireEvent(event);
    return CompletableFuture.completedFuture(event);
  }

  @Override
  public void fireAndForget(Object event) {
    if (event instanceof AsyncEvent) {
      eventBus.fireAsyncEvent((AsyncEvent) event).orTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS).exceptionally((ex) -> {
        var logger = LogManager.getLogger(getClass());
        logger.error("Timeout reached while firing event {}", event, ex);
        return null;
      });
      return;
    }
    eventBus.getDriver().fireEvent(event);
  }
}

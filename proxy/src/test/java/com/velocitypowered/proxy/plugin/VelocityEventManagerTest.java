package com.velocitypowered.proxy.plugin;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.proxy.testutil.FakePluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static com.velocitypowered.proxy.testutil.FakePluginManager.PLUGIN_A;
import static com.velocitypowered.proxy.testutil.FakePluginManager.PLUGIN_B;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class VelocityEventManagerTest {

  private EventManager eventManager;

  @BeforeEach
  public final void setup() {
    resetEventManager();
  }

  private void resetEventManager() {
    eventManager = createEventManager(new FakePluginManager());
  }

  protected EventManager createEventManager(PluginManager pluginManager) {
    return new VelocityEventManager(pluginManager);
  }

  // Must be public in order for kyori-asm to generate a method calling it
  public static class SimpleEvent {
    int value;
  }

  public static class SimpleAsyncEvent extends SimpleEvent implements space.arim.omnibus.events.AsyncEvent { }

  public static class HandlerListener implements EventHandler<SimpleEvent> {

    @Override
    public void execute(SimpleEvent event) {
      event.value++;
    }
  }

  public static class AnnotatedListener {

    @Subscribe
    public void increment(SimpleEvent event) {
      event.value++;
    }
  }

  private interface EventGenerator {

    void assertFiredEventValue(int value);
  }

  private interface TestFunction {

    void runTest(boolean annotated, EventGenerator generator);
  }

  private Stream<DynamicNode> composeTests(TestFunction testFunction) {
    Set<DynamicNode> tests = new HashSet<>();
    boolean[] trueAndFalse = new boolean[] {true, false};
    for (boolean annotated : trueAndFalse) {
      for (boolean async : trueAndFalse) {

        EventGenerator generator = (value) -> {
          SimpleEvent simpleEvent = (async) ? new SimpleAsyncEvent() : new SimpleEvent();
          SimpleEvent shouldBeSameEvent = eventManager.fire(simpleEvent).join();
          assertSame(simpleEvent, shouldBeSameEvent);
          assertEquals(value, simpleEvent.value);
        };
        tests.add(DynamicTest.dynamicTest("Annotated : " + annotated + ". Async: " + async, () -> {
          testFunction.runTest(annotated, generator);
          resetEventManager();
        }));
      }
    }
    return tests.stream();
  }

  @TestFactory
  public Stream<DynamicNode> simpleRegisterAndUnregister() {
    return composeTests((annotated, generator) -> {
      if (annotated) {
        eventManager.register(PLUGIN_A, new AnnotatedListener());
      } else {
        eventManager.register(PLUGIN_A, SimpleEvent.class, new HandlerListener());
      }
      generator.assertFiredEventValue(1);
      eventManager.unregisterListeners(PLUGIN_A);
      generator.assertFiredEventValue(0);
      assertDoesNotThrow(() -> eventManager.unregisterListeners(PLUGIN_A), "Extra unregister is a no-op");
    });
  }

  @TestFactory
  public Stream<DynamicNode> doubleRegisterListener() {
    return composeTests((annotated, generator) -> {
      if (annotated) {
        Object annotatedListener = new AnnotatedListener();
        eventManager.register(PLUGIN_A, annotatedListener);
        eventManager.register(PLUGIN_A, annotatedListener);
      } else {
        EventHandler<SimpleEvent> handler = new HandlerListener();
        eventManager.register(PLUGIN_A, SimpleEvent.class, handler);
        eventManager.register(PLUGIN_A, SimpleEvent.class, handler);
      }
      generator.assertFiredEventValue(2);
    });
  }

  @TestFactory
  public Stream<DynamicNode> doubleRegisterListenerDifferentPlugins() {
    return composeTests((annotated, generator) -> {
      if (annotated) {
        Object annotatedListener = new AnnotatedListener();
        eventManager.register(PLUGIN_A, annotatedListener);
        eventManager.register(PLUGIN_B, annotatedListener);
      } else {
        EventHandler<SimpleEvent> handler = new HandlerListener();
        eventManager.register(PLUGIN_A, SimpleEvent.class, handler);
        eventManager.register(PLUGIN_B, SimpleEvent.class, handler);
      }
      generator.assertFiredEventValue(2);
    });
  }

  @TestFactory
  public Stream<DynamicNode> doubleRegisterListenerThenUnregister() {
    return composeTests((annotated, generator) -> {
      if (annotated) {
        Object annotatedListener = new AnnotatedListener();
        eventManager.register(PLUGIN_A, annotatedListener);
        eventManager.register(PLUGIN_A, annotatedListener);
        eventManager.unregisterListener(PLUGIN_A, annotatedListener);
      } else {
        EventHandler<SimpleEvent> handler = new HandlerListener();
        eventManager.register(PLUGIN_A, SimpleEvent.class, handler);
        eventManager.register(PLUGIN_A, SimpleEvent.class, handler);
        eventManager.unregister(PLUGIN_A, handler);
      }
      generator.assertFiredEventValue(0);
    });
  }

  @TestFactory
  public Stream<DynamicNode> doubleUnregisterListener() {
    return composeTests((annotated, generator) -> {
      if (annotated) {
        Object annotatedListener = new AnnotatedListener();
        eventManager.register(PLUGIN_A, annotatedListener);
        eventManager.unregisterListener(PLUGIN_A, annotatedListener);
        assertDoesNotThrow(() -> eventManager.unregisterListener(PLUGIN_A, annotatedListener), "Extra unregister is a no-op");
      } else {
        EventHandler<SimpleEvent> handler = new HandlerListener();
        eventManager.register(PLUGIN_A, SimpleEvent.class, handler);
        eventManager.unregister(PLUGIN_A, handler);
        assertDoesNotThrow(() -> eventManager.unregister(PLUGIN_A, handler), "Extra unregister is a no-op");
      }
      generator.assertFiredEventValue(0);
    });
  }

}

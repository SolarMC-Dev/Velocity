package com.velocitypowered.proxy.plugin;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.proxy.testutil.FakePluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.velocitypowered.proxy.testutil.FakePluginManager.PLUGIN_A;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VelocityEventManagerTest {

  private EventManager eventManager;

  @BeforeEach
  public void setup() {
    eventManager = createEventManager();
  }

  protected EventManager createEventManager() {
    return new VelocityEventManager(new FakePluginManager());
  }

  // Must be public in order for kyori-asm to generate a method calling it
  public static class SimpleEvent {
    int value;
  }

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

  private void assertFiredEventValue(int value) {
    SimpleEvent se = new SimpleEvent();
    SimpleEvent shouldBeSameEvent = eventManager.fire(se).join();
    assertSame(se, shouldBeSameEvent);
    assertEquals(value, se.value);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  public void simpleRegisterUnregister(boolean annotated) {
    if (annotated) {
      eventManager.register(PLUGIN_A, new AnnotatedListener());
    } else {
      eventManager.register(PLUGIN_A, SimpleEvent.class, new HandlerListener());
    }
    assertFiredEventValue(1);
    eventManager.unregisterListeners(PLUGIN_A);
    assertFiredEventValue(0);
    assertDoesNotThrow(() -> eventManager.unregisterListeners(PLUGIN_A), "Extra unregister is a no-op");
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  public void doubleRegisterListener(boolean annotated) {
    if (annotated) {
      Object annotatedListener = new AnnotatedListener();
      eventManager.register(PLUGIN_A, annotatedListener);
      eventManager.register(PLUGIN_A, annotatedListener);
    } else {
      EventHandler<SimpleEvent> handler = new HandlerListener();
      eventManager.register(PLUGIN_A, SimpleEvent.class, handler);
      eventManager.register(PLUGIN_A, SimpleEvent.class, handler);
    }
    assertFiredEventValue(2);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  public void doubleRegisterListenerThenUnregister(boolean annotated) {
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
    assertFiredEventValue(0);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  public void doubleUnregisterListener(boolean annotated) {
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
    assertFiredEventValue(0);
  }

}

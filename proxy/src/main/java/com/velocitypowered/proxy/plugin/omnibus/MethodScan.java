package com.velocitypowered.proxy.plugin.omnibus;

import com.velocitypowered.api.event.Subscribe;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.BiConsumer;

class MethodScan {

  private final Object listener;

  MethodScan(Object listener) {
    this.listener = Objects.requireNonNull(listener, "listener");
  }

  void scan(BiConsumer<Method, Subscribe> action) {
    /*
     * This behavior is tailored to match that of kyori's SimpleMethodSubscriptionAdapter
     */
    for (Method method : listener.getClass().getDeclaredMethods()) {
      Subscribe subscribe = method.getAnnotation(Subscribe.class);
      if (subscribe == null) {
        continue;
      }
      if (method.getParameterCount() > 1) {
        throw new IllegalArgumentException("Method " + method + " must have only one parameter.");
      }
      action.accept(method, subscribe);
    }
  }
}

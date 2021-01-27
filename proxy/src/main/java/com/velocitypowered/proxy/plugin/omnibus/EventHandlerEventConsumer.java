package com.velocitypowered.proxy.plugin.omnibus;

import com.velocitypowered.api.event.EventHandler;

import java.util.Objects;
import java.util.function.Consumer;

class EventHandlerEventConsumer<E> implements Consumer<E> {

  private final EventHandler<E> eventHandler;

  EventHandlerEventConsumer(EventHandler<E> eventHandler) {
    this.eventHandler = Objects.requireNonNull(eventHandler);
  }

  @Override
  public void accept(E event) {
    eventHandler.execute(event);
  }
}

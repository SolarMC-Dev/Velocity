package com.velocitypowered.proxy.plugin.omnibus;

import com.velocitypowered.api.event.EventHandler;

import java.util.Objects;

class EventHandlerListener implements Listener {

  private final EventHandler<?> eventHandler;

  EventHandlerListener(EventHandler<?> eventHandler) {
    this.eventHandler = Objects.requireNonNull(eventHandler, "handler");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EventHandlerListener that = (EventHandlerListener) o;
    return eventHandler == that.eventHandler;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(eventHandler);
  }
}

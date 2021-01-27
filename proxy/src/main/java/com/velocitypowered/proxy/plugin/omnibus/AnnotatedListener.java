package com.velocitypowered.proxy.plugin.omnibus;

import java.util.Objects;

class AnnotatedListener implements Listener {

  private final Object listenerObject;

  AnnotatedListener(Object listenerObject) {
    this.listenerObject = Objects.requireNonNull(listenerObject, "listener");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AnnotatedListener that = (AnnotatedListener) o;
    return listenerObject == that.listenerObject;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(listenerObject);
  }
}

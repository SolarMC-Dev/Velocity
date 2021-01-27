package com.velocitypowered.proxy.plugin.omnibus;

import java.lang.invoke.MethodHandle;
import java.util.function.Consumer;

class MethodHandleEventConsumer<E> implements Consumer<E> {

  private final MethodHandle methodHandle;

  MethodHandleEventConsumer(MethodHandle methodHandle) {
    this.methodHandle = methodHandle;
  }

  @Override
  public void accept(E e) {
    try {
      methodHandle.invoke(e);
    } catch (RuntimeException | Error ex) {
      throw ex;
    } catch (Throwable ex) {
      throw new RuntimeException(
              "Exception invoking " + methodHandle,
              ex);
    }
  }
}

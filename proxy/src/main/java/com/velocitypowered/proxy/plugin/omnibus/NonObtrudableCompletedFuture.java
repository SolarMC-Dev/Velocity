package com.velocitypowered.proxy.plugin.omnibus;

import java.util.concurrent.CompletableFuture;

final class NonObtrudableCompletedFuture<T> extends CompletableFuture<T> {

  private static final NonObtrudableCompletedFuture<?> INSTANCE;

  static {
    NonObtrudableCompletedFuture<?> instance = new NonObtrudableCompletedFuture<>();
    instance.complete(null);
    INSTANCE = instance;
  }

  private NonObtrudableCompletedFuture() {}

  @SuppressWarnings("unchecked")
  static <T> CompletableFuture<T> instance() {
    return (CompletableFuture<T>) INSTANCE;
  }

  @Override
  public void obtrudeValue(T value) {
    throw new UnsupportedOperationException("Not obtrudable");
  }

  @Override
  public void obtrudeException(Throwable ex) {
    throw new UnsupportedOperationException("Not obtrudable");
  }
}

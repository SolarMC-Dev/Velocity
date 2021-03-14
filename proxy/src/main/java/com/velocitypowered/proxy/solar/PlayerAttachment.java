package com.velocitypowered.proxy.solar;

import com.velocitypowered.api.proxy.player.Authenticatable;
import gg.solarmc.loader.OnlineSolarPlayer;

import java.util.concurrent.atomic.AtomicReference;

public final class PlayerAttachment implements Authenticatable {

  private final AtomicReference<AuthState> authState = new AtomicReference<>();
  private OnlineSolarPlayer solarPlayer;

  @Override
  public AuthState currentState() {
    return authState.get();
  }

  @Override
  public AuthState switchState(AuthState expected, AuthState nextState) {
    return authState.compareAndExchange(expected, nextState);
  }

  public OnlineSolarPlayer solarPlayer() {
    /*
     * It is okay that there is no volatile read here. The only way for a thread
     * to read the solarPlayer field is through this method. To read a stale value,
     * it must read a null value, in which case, the thread will receive
     * IllegalStateException and nothing will work anyway
     */
    OnlineSolarPlayer solarPlayer = this.solarPlayer;
    if (solarPlayer == null) {
      throw new IllegalStateException("Solar player not available because player is not authenticated");
    }
    return solarPlayer;
  }

  public void insertSolarPlayer(OnlineSolarPlayer solarPlayer) {
    assert authState.get() == AuthState.AUTHENTICATED : "Not authenticated yet";
    this.solarPlayer = solarPlayer;
    authState.set(AuthState.AUTHENTICATED); // Publish changes
  }
}

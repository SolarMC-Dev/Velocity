package com.velocitypowered.api.proxy.player;

/**
 * Authentication information for a player, used by the authentication provider
 *
 */
public interface AuthState {

  /**
   * Determines whether the player is authenticated
   *
   * @return true if authenticated and ready to play, false otherwise
   */
  boolean isAuthenticated();
}

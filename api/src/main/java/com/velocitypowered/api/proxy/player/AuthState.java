/*
 * Copyright (C) 2018 Velocity Contributors
 *
 * The Velocity API is licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in the api top-level directory.
 */

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

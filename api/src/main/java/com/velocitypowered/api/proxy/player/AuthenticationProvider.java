/*
 * Copyright (C) 2018 Velocity Contributors
 *
 * The Velocity API is licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in the api top-level directory.
 */

package com.velocitypowered.api.proxy.player;

import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.Player;
import gg.solarmc.loader.OnlineSolarPlayer;

/**
 * A provider of authentication control for the proxy
 *
 * @param <A> the auth state subclass used by the provider
 */
public interface AuthenticationProvider<A extends AuthState> {

  /**
   * Creates an auth state for a new unauthenticated user
   *
   * @param username the user's username
   * @return the auth state
   */
  A createAuthState(String username);

  /**
   * Service provider which is loaded and used to create the auth provider
   *
   */
  interface ProviderFactory {

    /**
     * Creates the authentication provider. Called after plugins are loaded and enabled
     *
     * @param pluginManager the plugin manager
     * @param dataLoadController the data loader controller
     * @return the auth provider
     */
    AuthenticationProvider<?> createProvider(PluginManager pluginManager,
                                             DataLoadController dataLoadController);

  }

  /**
   * Privileged operations given to the auth provider
   *
   */
  interface DataLoadController {

    /**
     * Attaches the solar player to the given player
     *
     * @param player the player
     * @param solarPlayer the solar player
     */
    void addSolarPlayer(Player player, OnlineSolarPlayer solarPlayer);

  }
}

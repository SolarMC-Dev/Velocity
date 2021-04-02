/*
 * Copyright (C) 2018 Velocity Contributors
 *
 * The Velocity API is licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in the api top-level directory.
 */

package com.velocitypowered.api.proxy;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.player.AuthState;
import com.velocitypowered.api.proxy.player.AuthenticationProvider;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * Represents an incoming connection to the proxy.
 */
public interface InboundConnection {

  /**
   * Returns the player's IP address.
   *
   * @return the player's IP
   */
  InetSocketAddress getRemoteAddress();

  /**
   * Returns the hostname that the user entered into the client, if applicable.
   *
   * @return the hostname from the client
   */
  Optional<InetSocketAddress> getVirtualHost();

  /**
   * Determine whether or not the player remains online.
   *
   * @return whether or not the player active
   */
  boolean isActive();

  /**
   * Returns the current protocol version this connection uses.
   *
   * @return the protocol version the connection uses
   */
  ProtocolVersion getProtocolVersion();

  // Solar start
  /**
   * Gets the authentication state of the player. Used by the auth plugin
   *
   * @param authProvider the auth provider must pass an instance of itself
   * @return the authentication state
   */
  <A extends AuthState> A getAuthState(AuthenticationProvider<A> authProvider);
  // Solar end
}

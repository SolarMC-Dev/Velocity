/*
 * Copyright (C) 2018 Velocity Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.velocitypowered.proxy.solar;

import com.velocitypowered.api.proxy.player.AuthState;
import com.velocitypowered.api.proxy.player.AuthenticationProvider;

/**
 * Wrapper which both ensures the passed auth provider is correct
 * and allows creating the auth state at any later time
 *
 * @param <A> the auth state type
 */
public final class AuthStateHolder<A extends AuthState> {

  private final AuthenticationProvider<A> authProvider;
  private A authState;

  public AuthStateHolder(AuthenticationProvider<A> authProvider) {
    this.authProvider = authProvider;
  }

  public void addAuthState(String username) {
    authState = authProvider.createAuthState(username);
  }

  public <AA extends AuthState> AA getAuthState(AuthenticationProvider<AA> authProvider) {
    if (this.authProvider != authProvider) {
      throw new IllegalArgumentException("Unknown auth provider " + authProvider);
    }
    @SuppressWarnings("unchecked")
    AA authState = (AA) this.authState;
    if (authState == null) {
      throw new IllegalStateException("No auth state yet present");
    }
    return authState;
  }
}

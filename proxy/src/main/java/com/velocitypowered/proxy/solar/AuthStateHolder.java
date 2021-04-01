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

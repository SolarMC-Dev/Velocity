package com.velocitypowered.api.proxy.player;

/**
 * A user who may not yet be authenticated
 *
 */
public interface Authenticatable {

  /**
   * Reads the current state
   *
   * @return the state
   */
  AuthState currentState();

  /**
   * Attempts to transition the state to the next state. Performs a CAS
   *
   * @param expected the expected state
   * @param nextState the new state
   * @return the witnessed state, which will be the same as the expected if successful
   */
  AuthState switchState(AuthState expected, AuthState nextState);

  enum AuthState {
    /**
     * Player has recently joined the proxy but needs to issue a login command
     */
    NOT_AUTHENTICATED,
    /**
     * User is in the process of authenticating. For example, the user has issued
     * a login command but the command has not been completed yet. <br>
     * <br>
     * This mode indicates that some  actor is responsible for completing authentication,
     * either by transitioning the auth state of the player to {@code AUTHENTICATED}, or disconnecting
     * the player.
     *
     */
    AUTHENTICATING,
    /**
     * The player is fully authenticated and ready to play
     */
    AUTHENTICATED
  }
}

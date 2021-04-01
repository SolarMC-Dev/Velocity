package com.velocitypowered.proxy.connection.client;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.player.AuthState;
import com.velocitypowered.api.proxy.player.AuthenticationProvider;
import com.velocitypowered.proxy.connection.MinecraftConnection;
import com.velocitypowered.proxy.connection.MinecraftConnectionAssociation;
import com.velocitypowered.proxy.protocol.packet.Disconnect;
import com.velocitypowered.proxy.protocol.packet.Handshake;
import java.net.InetSocketAddress;
import java.util.Optional;

import com.velocitypowered.proxy.solar.AuthStateHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class InitialInboundConnection implements InboundConnection,
    MinecraftConnectionAssociation {

  private static final Logger logger = LogManager.getLogger(InitialInboundConnection.class);

  private final MinecraftConnection connection;
  private final String cleanedAddress;
  private final Handshake handshake;
  // Solar start - add AuthStateHolder
  private final AuthStateHolder<?> authStateHolder;

  InitialInboundConnection(MinecraftConnection connection, String cleanedAddress,
                           Handshake handshake, AuthStateHolder<?> authStateHolder) {
    this.connection = connection;
    this.cleanedAddress = cleanedAddress;
    this.handshake = handshake;
    this.authStateHolder = authStateHolder;
  }
  // Solar end

  @Override
  public InetSocketAddress getRemoteAddress() {
    return (InetSocketAddress) connection.getRemoteAddress();
  }

  @Override
  public Optional<InetSocketAddress> getVirtualHost() {
    return Optional.of(InetSocketAddress.createUnresolved(cleanedAddress, handshake.getPort()));
  }

  @Override
  public boolean isActive() {
    return connection.getChannel().isActive();
  }

  @Override
  public ProtocolVersion getProtocolVersion() {
    return connection.getProtocolVersion();
  }

  @Override
  public String toString() {
    return "[initial connection] " + connection.getRemoteAddress().toString();
  }

  /**
   * Disconnects the connection from the server.
   * @param reason the reason for disconnecting
   */
  public void disconnect(Component reason) {
    logger.info("{} has disconnected: {}", this,
        LegacyComponentSerializer.legacySection().serialize(reason));
    connection.closeWith(Disconnect.create(reason, getProtocolVersion()));
  }

  /**
   * Disconnects the connection from the server silently.
   * @param reason the reason for disconnecting
   */
  public void disconnectQuietly(Component reason) {
    connection.closeWith(Disconnect.create(reason, getProtocolVersion()));
  }

  // Solar start
  public AuthStateHolder<?> getAuthStateHolder() {
    return authStateHolder;
  }

  @Override
  public <A extends AuthState> A getAuthState(AuthenticationProvider<A> authProvider) {
    return authStateHolder.getAuthState(authProvider);
  }
  // Solar end
}

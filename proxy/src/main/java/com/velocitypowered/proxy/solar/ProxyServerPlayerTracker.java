package com.velocitypowered.proxy.solar;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.solarmc.loader.OnlineSolarPlayer;
import gg.solarmc.loader.impl.PlayerTracker;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class ProxyServerPlayerTracker implements PlayerTracker {

  private final ProxyServer server;

  public ProxyServerPlayerTracker(ProxyServer server) {
    this.server = Objects.requireNonNull(server);
  }

  @Override
  public Optional<OnlineSolarPlayer> getOnlinePlayerForUuid(UUID uuid) {
    return server.getPlayer(uuid).map(Player::getSolarPlayer);
  }

  @Override
  public Optional<OnlineSolarPlayer> getOnlinePlayerForName(String name) {
    return server.getPlayer(name).map(Player::getSolarPlayer);
  }
}

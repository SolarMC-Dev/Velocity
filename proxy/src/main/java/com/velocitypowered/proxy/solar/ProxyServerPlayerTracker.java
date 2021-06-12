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

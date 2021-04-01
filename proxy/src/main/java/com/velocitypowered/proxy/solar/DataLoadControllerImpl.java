package com.velocitypowered.proxy.solar;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.player.AuthenticationProvider;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import gg.solarmc.loader.OnlineSolarPlayer;

public class DataLoadControllerImpl implements AuthenticationProvider.DataLoadController {

  @Override
  public void addSolarPlayer(Player player, OnlineSolarPlayer solarPlayer) {
    ((ConnectedPlayer) player).insertSolarPlayer(solarPlayer);
  }

}

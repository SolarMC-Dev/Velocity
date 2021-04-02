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

import com.velocitypowered.api.proxy.ProxyServer;
import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.impl.Icarus;
import gg.solarmc.loader.impl.IcarusLauncher;
import gg.solarmc.loader.impl.LoginHandler;
import gg.solarmc.loader.impl.PlayerTracker;
import gg.solarmc.loader.impl.SimpleDataCenter;
import gg.solarmc.loader.impl.SolarDataConfig;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.registry.RegistryPriorities;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;
import space.arim.omnibus.util.concurrent.impl.IndifferentFactoryOfTheFuture;

import java.nio.file.Path;
import java.util.concurrent.Executors;

public class DataCenterLauncher {

  private final Omnibus omnibus;
  private final Path configDirectory;

  public DataCenterLauncher(Omnibus omnibus, Path configDirectory) {
    this.omnibus = omnibus;
    this.configDirectory = configDirectory;
  }

  public DataCenter launch(ProxyServer server) {
    FactoryOfTheFuture futuresFactory = new IndifferentFactoryOfTheFuture();
    IcarusLauncher icarusLauncher = new IcarusLauncher(
            configDirectory.resolve("dataloader"),
            futuresFactory,
            omnibus,
            Executors::newFixedThreadPool);
    SolarDataConfig config = icarusLauncher.loadConfig();
    Icarus icarus = icarusLauncher.launch(config.databaseCredentials());

    PlayerTracker playerTracker = new ProxyServerPlayerTracker(server);
    LoginHandler loginHandler = icarus.loginHandlerBuilder(config.logins()).build(playerTracker);

    DataCenter dataCenter = new SimpleDataCenter(futuresFactory, icarus, playerTracker, loginHandler);

    omnibus.getRegistry().register(FactoryOfTheFuture.class, RegistryPriorities.LOWEST,
            futuresFactory, "Solar-Velocity IndifferentFactoryOfTheFuture");
    omnibus.getRegistry().register(DataCenter.class, RegistryPriorities.LOWEST,
            dataCenter, "Solar-Velocity Data Center");

    return dataCenter;
  }
}

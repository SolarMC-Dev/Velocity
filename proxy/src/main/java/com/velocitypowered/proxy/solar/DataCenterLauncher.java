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

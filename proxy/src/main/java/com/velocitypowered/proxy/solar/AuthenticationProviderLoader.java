package com.velocitypowered.proxy.solar;

import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.player.AuthenticationProvider;

import java.util.ServiceLoader;

public final class AuthenticationProviderLoader {

  public AuthenticationProvider<?> load(PluginManager pluginManager,
                                        AuthenticationProvider.DataLoadController dataLoadController) {
    ModuleLayer moduleLayer = getClass().getModule().getLayer();
    if (moduleLayer == null) {
      throw new IllegalStateException("Velocity started in an unnamed module");
    }
    return ServiceLoader.load(moduleLayer, AuthenticationProvider.ProviderFactory.class)
            .findFirst().orElseThrow(() -> new IllegalStateException("No authentication provider found"))
            .createProvider(pluginManager, dataLoadController);
  }
}

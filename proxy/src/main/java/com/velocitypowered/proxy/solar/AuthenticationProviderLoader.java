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

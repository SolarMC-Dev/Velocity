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

package com.velocitypowered.proxy.plugin.omnibus;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.proxy.plugin.VelocityEventManagerTest;
import space.arim.omnibus.DefaultOmnibus;

public class OmnibusEventManagerTest extends VelocityEventManagerTest {

  @Override
  protected EventManager createEventManager(PluginManager pluginManager) {
    return new OmnibusEventManager(
            pluginManager,
            new DefaultOmnibus().getEventBus(),
            new ExportAssistant.NoOpImpl());
  }

}

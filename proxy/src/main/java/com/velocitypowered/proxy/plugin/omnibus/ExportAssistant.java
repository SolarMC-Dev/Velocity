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

public interface ExportAssistant {

  void exportClassIfPossible(Class<?> clazz, Module targetModule);

  class NoOpImpl implements ExportAssistant {

    @Override
    public void exportClassIfPossible(Class<?> clazz, Module targetModule) {

    }
  }

  class ModuleLayerControllerImpl implements ExportAssistant {

    private final ModuleLayer.Controller controller;

    public ModuleLayerControllerImpl(ModuleLayer.Controller controller) {
      this.controller = controller;
    }

    @Override
    public void exportClassIfPossible(Class<?> clazz, Module targetModule) {
      Module theirModule = clazz.getModule();
      if (controller.layer().modules().contains(theirModule)) {
        controller.addExports(theirModule, clazz.getPackageName(), targetModule);
      }
    }
  }
}

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

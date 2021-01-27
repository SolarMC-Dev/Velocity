package com.velocitypowered.proxy.plugin.omnibus;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.proxy.plugin.VelocityEventManagerTest;
import com.velocitypowered.proxy.testutil.FakePluginManager;
import space.arim.omnibus.DefaultOmnibus;

public class OmnibusEventManagerTest extends VelocityEventManagerTest {

  @Override
  protected EventManager createEventManager() {
    return new OmnibusEventManager(new FakePluginManager(), new DefaultOmnibus().getEventBus());
  }

}

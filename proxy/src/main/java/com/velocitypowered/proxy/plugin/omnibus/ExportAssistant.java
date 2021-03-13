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

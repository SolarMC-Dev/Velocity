package com.velocitypowered.proxy.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.text.Component;

public class MockCommandSource implements CommandSource {

  public static final CommandSource INSTANCE = new MockCommandSource();

  @Override
  public void sendMessage(final Component component) {

  }

  @Override
  public void sendMessage(Identity identity, net.kyori.adventure.text.Component component, MessageType type) {

  }

  @Override
  public Tristate getPermissionValue(final String permission) {
    return Tristate.UNDEFINED;
  }
}

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

import com.velocitypowered.api.event.Subscribe;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.function.BiConsumer;

class MethodScan {

  private final Object listener;

  MethodScan(Object listener) {
    this.listener = Objects.requireNonNull(listener, "listener");
  }

  void scan(BiConsumer<Method, Subscribe> action) {
    /*
     * This behavior is tailored to match that of kyori's SimpleMethodSubscriptionAdapter
     */
    for (Method method : listener.getClass().getDeclaredMethods()) {
      Subscribe subscribe = method.getAnnotation(Subscribe.class);
      if (subscribe == null) {
        continue;
      }
      if (method.getParameterCount() > 1) {
        throw new IllegalArgumentException("Method " + method + " must have only one parameter.");
      }
      if (!Modifier.isPublic(method.getModifiers())) {
        throw new IllegalArgumentException("Method '" + method + "' must be public");
      }
      action.accept(method, subscribe);
    }
  }
}

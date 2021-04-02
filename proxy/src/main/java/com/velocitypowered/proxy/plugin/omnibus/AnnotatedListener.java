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

import java.util.Objects;

class AnnotatedListener implements Listener {

  private final Object listenerObject;

  AnnotatedListener(Object listenerObject) {
    this.listenerObject = Objects.requireNonNull(listenerObject, "listener");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AnnotatedListener that = (AnnotatedListener) o;
    return listenerObject == that.listenerObject;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(listenerObject);
  }
}

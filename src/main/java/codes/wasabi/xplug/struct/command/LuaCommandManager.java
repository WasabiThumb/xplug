package codes.wasabi.xplug.struct.command;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface LuaCommandManager {

    LuaCommand create(String name, LuaArgumentType... argumentTypes) throws IllegalArgumentException;

    @Nullable LuaCommand get(String name);

    default LuaCommand getOrCreate(String name, LuaArgumentType... argumentTypes) {
        LuaCommand ret = get(name);
        if (ret == null) ret = create(name, argumentTypes);
        return ret;
    }

    default boolean exists(String name) {
        return get(name) != null;
    }

    Collection<LuaCommand> getAll();

    void remove(String name);

}

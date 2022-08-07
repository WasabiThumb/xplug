package codes.wasabi.xplug.platform.spigot.base.command;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.command.LuaArgumentType;
import codes.wasabi.xplug.struct.command.LuaCommand;
import codes.wasabi.xplug.struct.command.LuaCommandManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SpigotLuaCommandManager implements LuaCommandManager {

    private final Map<String, SpigotLuaCommand> map = new HashMap<>();

    @Override
    public LuaCommand create(String name, LuaArgumentType... argumentTypes) throws IllegalArgumentException {
        if (map.containsKey(name)) throw new IllegalArgumentException("A command already exists with this name");
        SpigotLuaCommand cmd = new SpigotLuaCommand(name, argumentTypes);
        map.put(name, cmd);
        return cmd;
    }

    @Override
    public @Nullable LuaCommand get(String name) {
        return map.get(name);
    }

    @Override
    public boolean exists(String name) {
        return map.containsKey(name);
    }

    @Override
    public Collection<LuaCommand> getAll() {
        return Collections.unmodifiableCollection(map.values());
    }

    @Override
    public void remove(String name) {
        SpigotLuaCommand slc = map.remove(name);
        if (slc != null) slc.unregister();
    }

}

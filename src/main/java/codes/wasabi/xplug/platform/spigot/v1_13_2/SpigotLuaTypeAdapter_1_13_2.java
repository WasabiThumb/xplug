package codes.wasabi.xplug.platform.spigot.v1_13_2;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.platform.spigot.base.SpigotLuaTypeAdapter;
import codes.wasabi.xplug.platform.spigot.base.world.SpigotLuaChunk;
import codes.wasabi.xplug.platform.spigot.base.world.SpigotLuaWorld;
import codes.wasabi.xplug.platform.spigot.v1_13_2.world.SpigotLuaChunk_1_13_2;
import codes.wasabi.xplug.platform.spigot.v1_8.world.SpigotLuaWorld_1_8;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class SpigotLuaTypeAdapter_1_13_2 extends SpigotLuaTypeAdapter {

    @Override
    protected SpigotLuaWorld createWorld(World world) {
        return new SpigotLuaWorld_1_8(world);
    }

    @Override
    public @NotNull SpigotLuaChunk convertChunk(Chunk chunk) {
        return new SpigotLuaChunk_1_13_2(chunk);
    }

}

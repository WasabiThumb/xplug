package codes.wasabi.xplug.platform.spigot.base.world;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.struct.block.LuaBlock;
import codes.wasabi.xplug.struct.entity.LuaEntity;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import codes.wasabi.xplug.struct.world.LuaChunk;
import codes.wasabi.xplug.struct.world.LuaWorld;
import codes.wasabi.xplug.util.func.GetterFunction;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;

import java.util.Collection;
import java.util.stream.Collectors;

public abstract class SpigotLuaWorld implements LuaWorld {

    private final World bukkitWorld;

    public SpigotLuaWorld(World world) {
        this.bukkitWorld = world;
    }

    public World getBukkitWorld() {
        return bukkitWorld;
    }

    @Override
    public String getName() {
        return bukkitWorld.getName();
    }

    @Override
    public String getUUID() {
        return bukkitWorld.getUID().toString();
    }

    @Override
    public LuaChunk getChunk(int x, int z) {
        return SpigotLuaToolkit.getAdapter().convertChunk(bukkitWorld.getChunkAt(x, z));
    }

    @Override
    public Collection<LuaEntity> getEntities() {
        return bukkitWorld.getEntities().stream().map((Entity e) -> SpigotLuaToolkit.getAdapter().convertEntity(e)).collect(Collectors.toList());
    }

    @Override
    public Collection<LuaPlayer> getPlayers() {
        return bukkitWorld.getPlayers().stream().map((Player ply) -> SpigotLuaToolkit.getAdapter().convertPlayer(ply)).collect(Collectors.toList());
    }

    @Override
    public int getMaxHeight() {
        return getBukkitWorld().getMaxHeight();
    }

    @Override
    public LuaBlock getBlock(int x, int y, int z) {
        return SpigotLuaToolkit.getAdapter().convertBlock(bukkitWorld.getBlockAt(x, y, z));
    }

    @Override
    public LuaTable getLuaValue() {
        LuaTable lt = LuaWorld.super.getLuaValue();
        lt.set("GetHandle", new GetterFunction(() -> new LuaUserdata(bukkitWorld)));
        return lt;
    }

}

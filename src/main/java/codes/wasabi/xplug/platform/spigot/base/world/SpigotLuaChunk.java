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
import codes.wasabi.xplug.struct.world.LuaChunk;
import codes.wasabi.xplug.struct.world.LuaWorld;
import codes.wasabi.xplug.util.func.GetterFunction;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Range;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class SpigotLuaChunk implements LuaChunk {

    private final Chunk bukkitChunk;

    public SpigotLuaChunk(Chunk chunk) {
        this.bukkitChunk = chunk;
    }

    public Chunk getBukkitChunk() {
        return bukkitChunk;
    }

    @Override
    public LuaWorld getWorld() {
        return SpigotLuaToolkit.getAdapter().convertWorld(bukkitChunk.getWorld());
    }

    @Override
    public int getX() {
        return bukkitChunk.getX();
    }

    @Override
    public int getZ() {
        return bukkitChunk.getZ();
    }

    @Override
    public Collection<LuaEntity> getEntities() {
        return Arrays.stream(bukkitChunk.getEntities()).map((Entity e) -> SpigotLuaToolkit.getAdapter().convertEntity(e)).collect(Collectors.toList());
    }

    @Override
    public boolean isLoaded() {
        return bukkitChunk.isLoaded();
    }

    @Override
    public LuaBlock getBlock(@Range(from = 0L, to = 15L) int x, int y, @Range(from = 0L, to = 15L) int z) {
        return SpigotLuaToolkit.getAdapter().convertBlock(bukkitChunk.getBlock(x, y, z));
    }

    @Override
    public LuaTable getLuaValue() {
        LuaTable lt = LuaChunk.super.getLuaValue();
        lt.set("GetHandle", new GetterFunction(() -> new LuaUserdata(bukkitChunk)));
        return lt;
    }

}

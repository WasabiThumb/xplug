package codes.wasabi.xplug.platform.spigot.base.block;
/*
  XPlug | A LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import xyz.wasabicodes.matlib.MaterialLib;
import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.platform.spigot.base.material.SpigotLuaMaterial;
import codes.wasabi.xplug.struct.block.LuaBlock;
import codes.wasabi.xplug.struct.data.LuaLocation;
import codes.wasabi.xplug.struct.material.LuaMaterial;
import codes.wasabi.xplug.struct.world.LuaChunk;
import codes.wasabi.xplug.struct.world.LuaWorld;
import codes.wasabi.xplug.util.func.GetterFunction;
import org.bukkit.block.Block;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class SpigotLuaBlock implements LuaBlock {

    private final Block block;
    public SpigotLuaBlock(Block bukkitBlock) {
        block = bukkitBlock;
    }

    public Block getBukkitBlock() {
        return block;
    }

    @Override
    public LuaWorld getWorld() {
        return SpigotLuaToolkit.getAdapter().convertWorld(block.getWorld());
    }

    @Override
    public LuaChunk getChunk() {
        return SpigotLuaToolkit.getAdapter().convertChunk(block.getChunk());
    }

    @Override
    public LuaLocation getLocation() {
        return SpigotLuaToolkit.getAdapter().convertLocation(block.getLocation());
    }

    @Override
    public int getX() {
        return block.getX();
    }

    @Override
    public int getY() {
        return block.getY();
    }

    @Override
    public int getZ() {
        return block.getZ();
    }

    @Override
    public LuaMaterial getMaterial() {
        return SpigotLuaMaterial.fromBlock(block);
    }

    @Override
    public void setMaterial(LuaMaterial material) {
        SpigotLuaMaterial slm = SpigotLuaToolkit.getAdapter().convertMaterial(material);
        if (slm == null) {
            MaterialLib.getMaterial("AIR").apply(block);
        } else {
            slm.getMetaMaterial().apply(block);
        }
    }

    @Override
    public LuaTable getLuaValue() {
        LuaTable lt = LuaBlock.super.getLuaValue();
        lt.set("GetHandle", new GetterFunction(() -> LuaValue.userdataOf(block)));
        return lt;
    }

}

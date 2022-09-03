package codes.wasabi.xplug.platform.spigot.base.material;
/*
  XPlug | A LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import xyz.wasabicodes.matlib.MaterialLib;
import xyz.wasabicodes.matlib.struct.MetaMaterial;
import codes.wasabi.xplug.struct.material.LuaMaterial;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class SpigotLuaMaterial implements LuaMaterial {

    public static SpigotLuaMaterial fromBukkitMaterial(Material material) {
        return new SpigotLuaMaterial(material.name(), new MetaMaterial(material));
    }

    public static SpigotLuaMaterial fromBlock(Block block) {
        MetaMaterial mm = MaterialLib.getMaterial(block);
        if (mm == null) mm = new MetaMaterial(block.getType());
        return new SpigotLuaMaterial(mm.getName(), mm);
    }

    public static SpigotLuaMaterial fromItemStack(ItemStack item) {
        MetaMaterial mm = MaterialLib.getMaterial(item);
        if (mm == null) mm = new MetaMaterial(item.getType());
        return new SpigotLuaMaterial(mm.getName(), mm);
    }

    private final String name;
    private final MetaMaterial material;
    public SpigotLuaMaterial(@NotNull String name, @NotNull MetaMaterial material) {
        this.name = name;
        this.material = material;
    }

    private static MetaMaterial getMaterialAssert(String name) throws IllegalArgumentException {
        MetaMaterial mm = MaterialLib.getMaterial(name);
        if (mm == null) throw new IllegalArgumentException("No material could be found named \"" + name + "\"");
        return mm;
    }

    public SpigotLuaMaterial(@NotNull String name) throws IllegalArgumentException {
        this(name, getMaterialAssert(name));
    }

    public MetaMaterial getMetaMaterial() {
        return material;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LuaTable getLuaValue() {
        LuaTable lt = LuaMaterial.super.getLuaValue();
        lt.set("GetMetaMaterial", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.userdataOf(material);
            }
        });
        return lt;
    }

}

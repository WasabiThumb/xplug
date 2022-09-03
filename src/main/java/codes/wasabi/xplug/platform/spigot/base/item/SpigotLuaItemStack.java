package codes.wasabi.xplug.platform.spigot.base.item;
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
import codes.wasabi.xplug.struct.item.LuaItemStack;
import codes.wasabi.xplug.struct.material.LuaMaterial;
import codes.wasabi.xplug.util.func.GetterFunction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SpigotLuaItemStack implements LuaItemStack {

    private final ItemStack is;
    public SpigotLuaItemStack(ItemStack is) {
        this.is = is;
    }

    public ItemStack getBukkitItemStack() {
        return is;
    }

    @Override
    public LuaMaterial getMaterial() {
        return SpigotLuaMaterial.fromItemStack(is);
    }

    @Override
    public int getCount() {
        return is.getAmount();
    }

    @Override
    public void setMaterial(LuaMaterial material) {
        SpigotLuaMaterial slm = SpigotLuaToolkit.getAdapter().convertMaterial(material);
        if (slm == null) {
            MaterialLib.getMaterial("AIR").apply(is);
        } else {
            slm.getMetaMaterial().apply(is);
        }
    }

    @Override
    public void setCount(int count) {
        is.setAmount(count);
    }

    @Override
    public int getStackSize() {
        return is.getMaxStackSize();
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (BukkitObjectOutputStream boos = new BukkitObjectOutputStream(bos)) {
            boos.writeObject(is);
        } catch (IOException ignored) { }
        return bos.toByteArray();
    }

    @Override
    public LuaTable getLuaValue() {
        LuaTable lt = LuaItemStack.super.getLuaValue();
        lt.set("GetHandle", new GetterFunction(() -> LuaValue.userdataOf(is)));
        return lt;
    }

}

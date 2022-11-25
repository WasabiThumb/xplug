package codes.wasabi.xplug.platform.spigot.base.inventory;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.struct.inventory.LuaEntityEquipment;
import codes.wasabi.xplug.struct.inventory.LuaItemStack;
import io.papermc.lib.PaperLib;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

// This file is rather ugly... Could refactor, but I doubt this will be an issue in the future.
public class SpigotLuaEntityEquipment implements LuaEntityEquipment {

    private final EntityEquipment eq;
    public SpigotLuaEntityEquipment(EntityEquipment eq) {
        this.eq = eq;
    }

    @Override
    public LuaItemStack getHead() {
        return SpigotLuaToolkit.getAdapter().convertItemStack(eq.getHelmet());
    }

    @Override
    public void setHead(LuaItemStack helmet) {
        if (PaperLib.isVersion(16, 5)) {
            eq.setHelmet(SpigotLuaToolkit.getAdapter().convertItemStack(helmet), true);
        } else {
            eq.setHelmet(SpigotLuaToolkit.getAdapter().convertItemStack(helmet));
        }
    }

    @Override
    public LuaItemStack getChest() {
        return SpigotLuaToolkit.getAdapter().convertItemStack(eq.getChestplate());
    }

    @Override
    public void setChest(LuaItemStack chest) {
        if (PaperLib.isVersion(16, 5)) {
            eq.setChestplate(SpigotLuaToolkit.getAdapter().convertItemStack(chest), true);
        } else {
            eq.setChestplate(SpigotLuaToolkit.getAdapter().convertItemStack(chest));
        }
    }

    @Override
    public LuaItemStack getLegs() {
        return SpigotLuaToolkit.getAdapter().convertItemStack(eq.getLeggings());
    }

    @Override
    public void setLegs(LuaItemStack legs) {
        if (PaperLib.isVersion(16, 5)) {
            eq.setLeggings(SpigotLuaToolkit.getAdapter().convertItemStack(legs), true);
        } else {
            eq.setLeggings(SpigotLuaToolkit.getAdapter().convertItemStack(legs));
        }
    }

    @Override
    public LuaItemStack getFeet() {
        return SpigotLuaToolkit.getAdapter().convertItemStack(eq.getBoots());
    }

    @Override
    public void setFeet(LuaItemStack feet) {
        if (PaperLib.isVersion(16, 5)) {
            eq.setBoots(SpigotLuaToolkit.getAdapter().convertItemStack(feet), true);
        } else {
            eq.setBoots(SpigotLuaToolkit.getAdapter().convertItemStack(feet));
        }
    }

    @Override
    public LuaItemStack getHand() {
        ItemStack is;
        if (PaperLib.isVersion(9)) {
            is = eq.getItemInMainHand();
        } else {
            try {
                Class<? extends EntityEquipment> clazz = eq.getClass();
                Method m = clazz.getMethod("getItemInHand");
                Object ob = m.invoke(eq);
                is = (ItemStack) ob;
            } catch (ReflectiveOperationException | ClassCastException e) {
                throw new IllegalStateException(e);
            }
        }
        return SpigotLuaToolkit.getAdapter().convertItemStack(is);
    }

    @Override
    public void setHand(LuaItemStack hand) {
        ItemStack is = SpigotLuaToolkit.getAdapter().convertItemStack(hand);
        if (PaperLib.isVersion(16, 5)) {
            eq.setItemInMainHand(is, true);
        } else if (PaperLib.isVersion(9)) {
            eq.setItemInMainHand(is);
        } else {
            try {
                Class<? extends EntityEquipment> clazz = eq.getClass();
                Method m = clazz.getMethod("setItemInHand", ItemStack.class);
                m.invoke(eq, is);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public LuaItemStack getOffhand() {
        if (PaperLib.isVersion(9)) {
            return SpigotLuaToolkit.getAdapter().convertItemStack(eq.getItemInOffHand());
        } else {
            return null;
        }
    }

    @Override
    public void setOffhand(LuaItemStack offhand) {
        if (PaperLib.isVersion(16, 5)) {
            eq.setItemInOffHand(SpigotLuaToolkit.getAdapter().convertItemStack(offhand), true);
        } else if (PaperLib.isVersion(9)) {
            eq.setItemInOffHand(SpigotLuaToolkit.getAdapter().convertItemStack(offhand));
        }
    }

    @Override
    public boolean supportsOffhand() {
        return PaperLib.isVersion(9);
    }

}

package codes.wasabi.xplug.platform.spigot.base;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import xyz.wasabicodes.matlib.MaterialLib;
import xyz.wasabicodes.matlib.struct.MetaMaterial;
import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.platform.spigot.base.command.SpigotLuaCommandManager;
import codes.wasabi.xplug.platform.spigot.base.command.SpigotLuaCommandSender;
import codes.wasabi.xplug.platform.spigot.base.entity.SpigotLuaPlayer;
import codes.wasabi.xplug.platform.spigot.base.item.SpigotLuaItemStack;
import codes.wasabi.xplug.platform.spigot.base.material.SpigotLuaMaterial;
import codes.wasabi.xplug.struct.LuaToolkit;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import codes.wasabi.xplug.struct.item.LuaItemStack;
import codes.wasabi.xplug.struct.material.LuaMaterial;
import codes.wasabi.xplug.struct.world.LuaWorld;
import codes.wasabi.xplug.util.LuaBridge;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaValue;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class SpigotLuaToolkit implements LuaToolkit {

    public static SpigotLuaToolkit getInstance() {
        return (SpigotLuaToolkit) XPlug.getToolkit();
    }

    public static SpigotLuaTypeAdapter getAdapter() {
        return getInstance().getTypeAdapter();
    }

    public abstract SpigotLuaTypeAdapter getTypeAdapter();

    // START WORLD FUNCTIONS //
    @Override
    public @Nullable LuaWorld getWorld(String name) {
        World world = Bukkit.getWorld(name);
        if (world == null) return null;
        return getTypeAdapter().convertWorld(world);
    }

    @Override
    public @Nullable LuaWorld getWorld(UUID uuid) {
        World world = Bukkit.getWorld(uuid);
        if (world == null) return null;
        return getTypeAdapter().convertWorld(world);
    }

    @Override
    public Collection<LuaWorld> getWorlds() {
        return Bukkit.getWorlds().stream().map((World w) -> getTypeAdapter().convertWorld(w)).collect(Collectors.toList());
    }
    // END WORLD FUNCTIONS //

    @Override
    public int getVersion() {
        return PaperLib.getMinecraftVersion();
    }

    @Override
    public int getPatchVersion() {
        return PaperLib.getMinecraftPatchVersion();
    }

    private final SpigotLuaParticleTools pt = new SpigotLuaParticleTools();
    @Override
    public SpigotLuaParticleTools getParticleTools() {
        return pt;
    }

    @Override
    public SpigotLuaCommandSender getConsole() {
        return getTypeAdapter().convertCommandSender(Bukkit.getConsoleSender());
    }

    @Override
    public Collection<LuaPlayer> getPlayers() {
        return Bukkit.getOnlinePlayers().stream().map(SpigotLuaPlayer::new).collect(Collectors.toList());
    }

    private final SpigotLuaTimers timers = new SpigotLuaTimers();
    @Override
    public SpigotLuaTimers getTimers() {
        return timers;
    }

    @Override
    public void synchronize(Runnable runnable) {
        Bukkit.getScheduler().runTask(XPlug.getInstance(), runnable);
    }

    private final SpigotLuaEvents events = new SpigotLuaEvents(this);
    @Override
    public SpigotLuaEvents getEvents() {
        return events;
    }

    private final SpigotLuaCommandManager commandManager = new SpigotLuaCommandManager();
    @Override
    public SpigotLuaCommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public @Nullable SpigotLuaMaterial parseMaterial(LuaValue value, boolean exact) {
        String name = LuaBridge.extractName(value);
        MetaMaterial mm = MaterialLib.getMaterial(name);
        MetaMaterial proposed = LuaBridge.extractUserdata(value, "GetMetaMaterial", MetaMaterial.class);
        if (proposed != null) {
            mm = proposed;
        } else {
            if (mm == null) return null;
            if (exact) {
                if (!mm.isExactMatch()) return null;
            }
        }
        return new SpigotLuaMaterial(name, mm);
    }

    @Override
    public @NotNull LuaItemStack createItemStack(LuaMaterial lm, int count) {
        SpigotLuaMaterial slm = getTypeAdapter().convertMaterial(lm);
        ItemStack is;
        if (slm == null) {
            is = MaterialLib.getMaterial("STONE").createItemStack(count);
        } else {
            is = slm.getMetaMaterial().createItemStack(count);
        }
        return new SpigotLuaItemStack(is);
    }

}

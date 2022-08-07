package codes.wasabi.xplug.platform.spigot.base;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.platform.spigot.base.command.SpigotLuaCommandManager;
import codes.wasabi.xplug.platform.spigot.base.command.SpigotLuaCommandSender;
import codes.wasabi.xplug.platform.spigot.base.entity.SpigotLuaPlayer;
import codes.wasabi.xplug.struct.LuaToolkit;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import codes.wasabi.xplug.struct.world.LuaWorld;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

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

}

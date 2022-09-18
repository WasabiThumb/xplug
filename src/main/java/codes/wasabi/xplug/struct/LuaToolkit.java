package codes.wasabi.xplug.struct;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.command.LuaCommandManager;
import codes.wasabi.xplug.struct.command.LuaCommandSender;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import codes.wasabi.xplug.struct.inventory.LuaItemStack;
import codes.wasabi.xplug.struct.material.LuaMaterial;
import codes.wasabi.xplug.struct.text.LuaBossBar;
import codes.wasabi.xplug.struct.world.LuaWorld;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaValue;

import java.util.Collection;
import java.util.UUID;

public interface LuaToolkit {

    // START WORLD FUNCTIONS //
    @Nullable LuaWorld getWorld(String name);

    @Nullable LuaWorld getWorld(UUID uuid);

    Collection<LuaWorld> getWorlds();
    // END WORLD FUNCTIONS //

    int getVersion();

    int getPatchVersion();

    @Contract(" -> !null")
    LuaParticleTools getParticleTools();

    LuaCommandSender getConsole();

    Collection<LuaPlayer> getPlayers();

    LuaTimers getTimers();

    void synchronize(Runnable runnable);

    LuaEvents getEvents();

    LuaCommandManager getCommandManager();

    @Nullable LuaMaterial parseMaterial(LuaValue value, boolean exact);

    @NotNull LuaItemStack createItemStack(LuaMaterial lm, int count);

    @NotNull LuaBossBar createBossBar(String title, int color, int style, boolean createFog, boolean darkenSky, boolean bossMusic);

    @Nullable LuaPlayer parsePlayer(LuaValue value);

    @Nullable LuaItemStack parseItemStack(LuaValue value);

}

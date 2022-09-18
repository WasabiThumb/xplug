package codes.wasabi.xplug.platform.spigot.v1_17;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.platform.spigot.base.text.SpigotLuaBossBar;
import codes.wasabi.xplug.platform.spigot.v1_9.text.SpigotLuaBossBar_1_9;
import org.jetbrains.annotations.NotNull;

public class SpigotLuaToolkit_1_17 extends SpigotLuaToolkit {

    private final SpigotLuaTypeAdapter_1_17 typeAdapter = new SpigotLuaTypeAdapter_1_17();
    @Override
    public SpigotLuaTypeAdapter_1_17 getTypeAdapter() {
        return typeAdapter;
    }

    @Override
    public @NotNull SpigotLuaBossBar createBossBar(String title, int color, int style, boolean createFog, boolean darkenSky, boolean bossMusic) {
        return new SpigotLuaBossBar_1_9(title, color, style, createFog, darkenSky, bossMusic);
    }

}

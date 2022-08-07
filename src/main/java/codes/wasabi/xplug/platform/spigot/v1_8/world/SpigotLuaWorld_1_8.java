package codes.wasabi.xplug.platform.spigot.v1_8.world;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import static codes.wasabi.xplug.library.enums.*;
import codes.wasabi.xplug.platform.spigot.base.world.SpigotLuaWorld;
import org.bukkit.World;

public class SpigotLuaWorld_1_8 extends SpigotLuaWorld {

    public SpigotLuaWorld_1_8(World world) {
        super(world);
    }

    @Override
    public int getMinHeight() {
        return 0;
    }

    @Override
    public int getEnvironment() {
        switch (getBukkitWorld().getEnvironment()) {
            case NORMAL:
                return ENV_NORMAL;
            case NETHER:
                return ENV_NETHER;
            case THE_END:
                return ENV_THE_END;
            default:
                return ENV_CUSTOM;
        }
    }

}

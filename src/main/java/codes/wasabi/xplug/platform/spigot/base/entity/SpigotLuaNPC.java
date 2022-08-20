package codes.wasabi.xplug.platform.spigot.base.entity;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.entity.LuaNPC;
import org.bukkit.entity.Entity;

public class SpigotLuaNPC extends LuaNPC implements SpigotLuaEntity {

    private final Entity entity;
    public SpigotLuaNPC(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Entity getBukkitEntity() {
        return entity;
    }

}

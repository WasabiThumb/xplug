package codes.wasabi.xplug.platform.spigot.base.entity;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.entity.LuaNPC;
import codes.wasabi.xplug.util.func.GetterFunction;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class SpigotLuaNPC implements LuaNPC, SpigotLuaEntity {

    private final Entity entity;
    public SpigotLuaNPC(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Entity getBukkitEntity() {
        return entity;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public boolean hasHealth() {
        return (entity instanceof Damageable);
    }

    @Override
    public LuaTable getLuaValue() {
        LuaTable lt = LuaNPC.super.getLuaValue();
        lt.set("GetHandle", new GetterFunction(() -> LuaValue.userdataOf(entity)));
        return lt;
    }

}

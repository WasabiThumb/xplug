package codes.wasabi.xplug.struct.entity;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import org.luaj.vm2.LuaTable;

public abstract class LuaNPC implements LuaEntity {

    @Override
    public LuaTable getLuaValue() {
        LuaTable lt = new LuaTable();
        LuaEntity.fillMeta(lt, this);
        return lt;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

}

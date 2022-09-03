package codes.wasabi.xplug.struct.material;
/*
  XPlug | A LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.LuaValueHolder;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public interface LuaMaterial extends LuaValueHolder {

    String getName();

    @Override
    default LuaTable getLuaValue() {
        LuaTable lt = LuaValue.tableOf();
        lt.set("GetName", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(getName());
            }
        });
        return lt;
    }

}

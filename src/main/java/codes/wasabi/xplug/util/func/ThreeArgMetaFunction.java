package codes.wasabi.xplug.util.func;
/*
  XPlug | A LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public abstract class ThreeArgMetaFunction extends VarArgMetaFunction {

    @Override
    protected Varargs call(LuaTable self, Varargs args) {
        LuaValue lv = call(self, args.arg(1), args.arg(2), args.arg(3));
        return LuaValue.varargsOf(new LuaValue[]{ lv });
    }

    protected abstract LuaValue call(LuaTable self, LuaValue arg1, LuaValue arg2, LuaValue arg3);

}

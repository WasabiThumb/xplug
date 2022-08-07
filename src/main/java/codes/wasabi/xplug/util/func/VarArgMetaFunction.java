package codes.wasabi.xplug.util.func;
/*
  XPlug | A LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public abstract class VarArgMetaFunction extends VarArgFunction {

    @Override
    public Varargs invoke(Varargs args) {
        if (args.narg() < 1) throw new LuaError("Metatable function has no \"self\" argument");
        LuaTable self = args.arg(1).checktable();
        Varargs sub;
        if (args.narg() > 1) {
            sub = args.subargs(2);
        } else {
            sub = LuaValue.varargsOf(new LuaValue[0]);
        }
        return call(self, sub);
    }

    protected abstract Varargs call(LuaTable self, Varargs args);

}

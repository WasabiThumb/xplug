package codes.wasabi.xplug.util.func;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class NilFunction extends VarArgFunction {
    @Override
    public Varargs invoke(Varargs args) {
        return varargsOf(new LuaValue[]{ LuaValue.NIL });
    }
}

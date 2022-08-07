package codes.wasabi.xplug.util.func;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.util.LuaBridge;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.function.Supplier;

public class GetterFunction extends ZeroArgFunction {

    private final Supplier<LuaValue> supplier;

    public GetterFunction(LuaValue value) {
        this.supplier = () -> value;
    }

    public GetterFunction(Object o) {
        this(LuaBridge.toLua(o));
    }

    public GetterFunction(Supplier<Object> supplier) {
        this.supplier = () -> LuaBridge.toLua(supplier.get());
    }

    @Override
    public LuaValue call() {
        return supplier.get();
    }

}

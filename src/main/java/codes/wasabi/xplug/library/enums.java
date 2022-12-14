package codes.wasabi.xplug.library;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.util.LuaBridge;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@SuppressWarnings("unused")
public class enums extends TwoArgFunction {

    @NotNull
    public LuaValue call(LuaValue modname, LuaValue env) {
        Class<? extends enums> clazz = getClass();
        for (Field f : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) continue;
            String name = f.getName();
            Object value = null;
            try {
                value = f.get(null);
            } catch (ReflectiveOperationException ignored) { }
            env.set(name, LuaBridge.toLua(value));
        }
        return LuaValue.NIL;
    }

    public static final int ENV_UNKNOWN = 0;
    public static final int ENV_NORMAL = 1;
    public static final int ENV_NETHER = 2;
    public static final int ENV_THE_END = 3;
    public static final int ENV_CUSTOM = 4;

    public static final int P_NORMAL = 0;
    public static final int P_LOW = 1;
    public static final int P_LOWEST = 2;
    public static final int P_HIGH = 3;
    public static final int P_HIGHEST = 4;
    public static final int P_MONITOR = 5;

    public static final int AT_STRING = 0;
    public static final int AT_INTEGER = 1;
    public static final int AT_POSITIVE_INTEGER = 2;
    public static final int AT_DECIMAL = 3;
    public static final int AT_POSITIVE_DECIMAL = 4;
    public static final int AT_BOOLEAN = 5;
    public static final int AT_PLAYER = 6;
    public static final int AT_WORLD = 7;

    public static final int LEVEL_INFO = 0;
    public static final int LEVEL_WARN = 1;
    public static final int LEVEL_ERROR = 2;

    public static final int BC_BLUE = 0;
    public static final int BC_GREEN = 1;
    public static final int BC_PINK = 2;
    public static final int BC_PURPLE = 3;
    public static final int BC_RED = 4;
    public static final int BC_WHITE = 5;
    public static final int BC_YELLOW = 6;

    public static final int BS_SOLID = 1;
    public static final int BS_SIX_SEGMENT = 6;
    public static final int BS_TEN_SEGMENT = 10;
    public static final int BS_TWELVE_SEGMENT = 12;
    public static final int BS_TWENTY_SEGMENT = 20;

    public static final int GM_SURVIVAL = 0;
    public static final int GM_CREATIVE = 1;
    public static final int GM_ADVENTURE = 2;
    public static final int GM_SPECTATOR = 3;

}

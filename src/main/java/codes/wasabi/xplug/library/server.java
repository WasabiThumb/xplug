package codes.wasabi.xplug.library;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaToolkit;
import codes.wasabi.xplug.struct.world.LuaWorld;
import codes.wasabi.xplug.util.LuaBridge;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.UUID;

public class server extends TwoArgFunction {

    private static LuaToolkit ltk;

    public server() {
        ltk = XPlug.getToolkit();
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("GetWorlds", new GetWorlds());
        library.set("GetWorld", new GetWorld());
        library.set("GetVersion", new GetVersion());
        library.set("GetConsole", new GetConsole());
        library.set("GetPlayers", new GetPlayers());
        env.set("server", library);
        return library;
    }

    static class GetWorlds extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return listOf(ltk.getWorlds().stream().map(LuaWorld::getLuaValue).toArray(LuaValue[]::new));
        }
    }

    static class GetWorld extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String s = arg.checkjstring();
            try {
                UUID uuid = UUID.fromString(s);
                LuaWorld lw = ltk.getWorld(uuid);
                if (lw != null) return lw.getLuaValue();
            } catch (IllegalArgumentException ignored) { }
            LuaWorld lw = ltk.getWorld(s);
            if (lw != null) return lw.getLuaValue();
            return null;
        }
    }

    static class GetVersion extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return varargsOf(new LuaValue[]{ valueOf(ltk.getVersion()), valueOf(ltk.getPatchVersion()) });
        }
    }

    static class GetConsole extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return ltk.getConsole().getLuaValue();
        }
    }

    static class GetPlayers extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaBridge.toLua(ltk.getPlayers());
        }
    }

}

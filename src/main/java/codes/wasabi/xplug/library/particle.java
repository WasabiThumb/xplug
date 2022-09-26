package codes.wasabi.xplug.library;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaParticleTools;
import codes.wasabi.xplug.struct.LuaToolkit;
import codes.wasabi.xplug.struct.data.LuaLocation;
import codes.wasabi.xplug.struct.material.LuaMaterial;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.*;

import java.awt.Color;

public class particle extends TwoArgFunction {

    private static LuaToolkit tk;
    private static LuaParticleTools pt;

    public particle() {
        tk = XPlug.getToolkit();
        pt = tk.getParticleTools();
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("Start", new Start());
        library.set("SetAmount", new SetAmount());
        library.set("SetColor", new SetColor());
        library.set("SetOffsetX", new SetOffsetX());
        library.set("SetOffsetY", new SetOffsetY());
        library.set("SetOffsetZ", new SetOffsetZ());
        library.set("SetOffset", new SetOffset());
        library.set("SetSpeed", new SetSpeed());
        library.set("SetMaterial", new SetMaterial());
        library.set("End", new End());
        env.set("particle", library);
        return library;
    }

    static class Start extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            String particleName = arg1.checkjstring();
            LuaLocation loc = LuaLocation.fromLuaValueAssert(arg2);
            try {
                pt.start(particleName, loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
            } catch (IllegalArgumentException e) {
                throw new LuaError("Invalid particle \"" + particleName + "\"");
            }
            return LuaValue.NIL;
        }
    }

    static class SetAmount extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            pt.setAmount(arg.checkint());
            return LuaValue.NIL;
        }
    }

    static class SetColor extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int narg = args.narg();
            int color;
            if (narg == 1) {
                color = args.arg(1).checkint() & 0xffffff;
            } else if (narg == 3) {
                int r = args.arg(1).checkint() & 0xff;
                int g = args.arg(2).checkint() & 0xff;
                int b = args.arg(3).checkint() & 0xff;
                color = (r << 16) | (g << 8) | b;
            } else {
                throw new LuaError("This method takes 1 or 3 arguments");
            }
            pt.setColor(new Color(color));
            return LuaValue.NIL;
        }
    }

    static class SetOffsetX extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            pt.setOffsetX(arg.checknumber().tofloat());
            return LuaValue.NIL;
        }
    }

    static class SetOffsetY extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            pt.setOffsetY(arg.checknumber().tofloat());
            return LuaValue.NIL;
        }
    }

    static class SetOffsetZ extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            pt.setOffsetZ(arg.checknumber().tofloat());
            return LuaValue.NIL;
        }
    }

    static class SetOffset extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            pt.setOffset(
                    arg1.checknumber().tofloat(),
                    arg2.checknumber().tofloat(),
                    arg3.checknumber().tofloat()
            );
            return LuaValue.NIL;
        }
    }

    static class SetSpeed extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            pt.setSpeed(arg.checknumber().tofloat());
            return LuaValue.NIL;
        }
    }

    static class SetMaterial extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            LuaMaterial lm = tk.parseMaterial(arg, false);
            pt.setMaterial(lm);
            return LuaValue.NIL;
        }
    }

    static class End extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            pt.end();
            return LuaValue.NIL;
        }
    }

}

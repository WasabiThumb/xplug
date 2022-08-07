package codes.wasabi.xplug.library;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaTimers;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class timer extends TwoArgFunction {

    private static LuaTimers timers;

    public timer() {
        timers = XPlug.getToolkit().getTimers();
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("Simple", new Simple());
        library.set("Create", new Create());
        library.set("Adjust", new Adjust());
        library.set("Exists", new Exists());
        library.set("Pause", new Pause());
        library.set("UnPause", new UnPause());
        library.set("Remove", new Remove());
        library.set("RepsLeft", new RepsLeft());
        library.set("Start", new Start());
        library.set("Stop", new Stop());
        library.set("TimeLeft", new TimeLeft());
        library.set("Toggle", new Toggle());
        env.set("timer", library);
        return library;
    }

    static class Simple extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            timers.simple(arg1.checkdouble(), arg2.checkfunction());
            return LuaValue.NIL;
        }
    }

    static class Create extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if (args.narg() != 4) throw new LuaError("This method takes 4 arguments");
            String id = args.checkjstring(1);
            double delay = args.todouble(2);
            int reps = args.toint(3);
            LuaFunction func = args.checkfunction(4);
            timers.create(id, delay, reps, func);
            return LuaValue.NIL;
        }
    }

    static class Adjust extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            String id = args.checkjstring(1);
            double delay = args.checkdouble(2);
            Integer reps = null;
            LuaFunction func = null;
            int narg = args.narg();
            if (narg > 2) {
                LuaValue lv = args.arg(3);
                if (lv.isint()) reps = lv.toint();
                if (narg > 3) {
                    lv = args.arg(4);
                    if (lv.isfunction()) func = lv.checkfunction();
                    if (narg > 4) {
                        throw new LuaError("This method takes 2-4 arguments");
                    }
                }
            }
            return valueOf(timers.adjust(id, delay, reps, func));
        }
    }

    static class Exists extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            return valueOf(timers.exists(arg1.checkjstring()));
        }
    }

    static class Pause extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            return valueOf(timers.pause(arg1.checkjstring()));
        }
    }

    static class UnPause extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            return valueOf(timers.unpause(arg1.checkjstring()));
        }
    }

    static class Remove extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            timers.remove(arg1.checkjstring());
            return LuaValue.NIL;
        }
    }

    static class RepsLeft extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            return valueOf(timers.repsLeft(arg1.checkjstring()));
        }
    }

    static class Start extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            return valueOf(timers.start(arg1.checkjstring()));
        }
    }

    static class Stop extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            return valueOf(timers.stop(arg1.checkjstring()));
        }
    }

    static class TimeLeft extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            return valueOf(timers.timeLeft(arg1.checkjstring()));
        }
    }

    static class Toggle extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            return valueOf(timers.toggle(arg1.checkjstring()));
        }
    }

}

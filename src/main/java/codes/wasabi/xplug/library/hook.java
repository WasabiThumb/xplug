package codes.wasabi.xplug.library;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaEvents;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import static codes.wasabi.xplug.library.enums.*;

public class hook extends TwoArgFunction {

    private final LuaEvents events = XPlug.getToolkit().getEvents();

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaFunction logError = XPlug.getSandbox().getLoggerFunction(env, LEVEL_ERROR);
        LuaTable library = tableOf();
        library.set("Add", new Add(events, logError));
        library.set("GetTable", new GetTable(events));
        library.set("Remove", new Remove(events));
        library.set("Run", new Run(events));
        env.set("hook", library);
        return library;
    }

    static class Add extends VarArgFunction {
        private final LuaEvents events;
        private final LuaFunction err;
        Add(LuaEvents events, LuaFunction err) {
            this.events = events;
            this.err = err;
        }

        @Override
        public Varargs invoke(Varargs args) {
            String hookName = args.checkjstring(1);
            String identifier = args.checkjstring(2);
            LuaFunction func = args.checkfunction(3);
            LuaEvents.Priority p = null;
            if (args.narg() > 3) {
                LuaValue value = args.arg(4);
                if (value.isint()) {
                    switch (value.toint()) {
                        case P_LOW:
                            p = LuaEvents.Priority.LOW;
                            break;
                        case P_LOWEST:
                            p = LuaEvents.Priority.LOWEST;
                            break;
                        case P_HIGH:
                            p = LuaEvents.Priority.HIGH;
                            break;
                        case P_HIGHEST:
                            p = LuaEvents.Priority.HIGHEST;
                            break;
                        case P_MONITOR:
                            p = LuaEvents.Priority.MONITOR;
                            break;
                        default:
                            p = LuaEvents.Priority.NORMAL;
                    }
                }
            }
            events.addHook(hookName, identifier, new VarArgFunction() {
                @Override
                public Varargs invoke(Varargs args) {
                    LuaValue ret = LuaValue.NIL;
                    try {
                        ret = func.invoke(args).arg(1);
                    } catch (LuaError e) {
                        err.call(LuaValue.valueOf("LUA Error in event callback " + hookName + " with identifier " + identifier + ", see details below"));
                        err.call(LuaValue.valueOf(e.getMessage()));
                    }
                    return ret;
                }
            }, p);
            return LuaValue.NIL;
        }
    }

    static class GetTable extends ZeroArgFunction {
        private final LuaEvents events;
        GetTable(LuaEvents events) {
            this.events = events;
        }

        @Override
        public LuaValue call() {
            return events.getTable();
        }
    }

    static class Remove extends TwoArgFunction {
        private final LuaEvents events;
        Remove(LuaEvents events) {
            this.events = events;
        }

        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            events.removeHook(arg1.checkjstring(), arg2.checkjstring());
            return LuaValue.NIL;
        }
    }

    static class Run extends VarArgFunction {
        private final LuaEvents events;
        Run(LuaEvents events) {
            this.events = events;
        }

        @Override
        public Varargs invoke(Varargs args) {
            String eventName = args.checkjstring(1);
            Varargs arg;
            if (args.narg() > 1) {
                arg = args.subargs(2);
            } else {
                arg = LuaValue.varargsOf(new LuaValue[0]);
            }
            Boolean ret = events.runHook(eventName, arg);
            if (ret == null) return LuaValue.NIL;
            return LuaValue.valueOf(ret);
        }
    }

}

package codes.wasabi.xplug.library;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.command.LuaArgumentType;
import codes.wasabi.xplug.struct.command.LuaCommand;
import codes.wasabi.xplug.struct.command.LuaCommandManager;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class command extends TwoArgFunction {

    private static LuaCommandManager mgr;
    public command() {
        mgr = XPlug.getToolkit().getCommandManager();
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable table = tableOf();
        table.set("Create", new Create());
        table.set("GetOrCreate", new GetOrCreate());
        table.set("Get", new Get());
        table.set("Remove", new Remove());
        table.set("Exists", new Exists());
        table.set("GetAll", new GetAll());
        env.set("command", table);
        return table;
    }

    private static class CreationParameters {
        String name;
        LuaArgumentType[] args;

        LuaCommand execute(boolean get) throws LuaError {
            if (get) return mgr.getOrCreate(name, args);
            try {
                return mgr.create(name, args);
            } catch (IllegalArgumentException e) {
                throw new LuaError("There is already a command with that name!");
            }
        }
    }

    private static CreationParameters extract(Varargs varargs) throws LuaError {
        String name = varargs.checkjstring(1);
        int narg = varargs.narg();
        LuaArgumentType[] arg = new LuaArgumentType[narg - 1];
        if (narg > 1) {
            for (int i=0; i < arg.length; i++) {
                LuaArgumentType type = LuaArgumentType.fromEnumIndex(varargs.arg(i + 2).toint());
                if (type == null) {
                    throw new LuaError("Invalid type for argument #" + (i + 1));
                }
                arg[i] = type;
            }
        }
        CreationParameters ret = new CreationParameters();
        ret.name = name;
        ret.args = arg;
        return ret;
    }

    static class Create extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            CreationParameters param = extract(args);
            return LuaValue.varargsOf(new LuaValue[]{ param.execute(false).getLuaValue() });
        }
    }

    static class GetOrCreate extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return LuaValue.varargsOf(new LuaValue[]{ extract(args).execute(true).getLuaValue() });
        }
    }

    static class Get extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String name = arg.checkjstring();
            LuaCommand cmd = mgr.get(name);
            if (cmd == null) return LuaValue.NIL;
            return cmd.getLuaValue();
        }
    }

    static class Remove extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String name = arg.checkjstring();
            mgr.remove(name);
            return LuaValue.NIL;
        }
    }

    static class Exists extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String name = arg.checkjstring();
            return LuaValue.valueOf(mgr.exists(name));
        }
    }

    static class GetAll extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            Object[] arr = mgr.getAll().toArray();
            LuaValue[] values = new LuaValue[arr.length];
            for (int i=0; i < arr.length; i++) {
                values[i] = ((LuaCommand) arr[i]).getLuaValue();
            }
            return listOf(values);
        }
    }

}

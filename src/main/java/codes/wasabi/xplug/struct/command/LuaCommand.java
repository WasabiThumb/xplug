package codes.wasabi.xplug.struct.command;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.LuaValueHolder;
import codes.wasabi.xplug.util.func.GetterFunction;
import codes.wasabi.xplug.util.func.OneArgMetaFunction;
import codes.wasabi.xplug.util.func.VarArgMetaFunction;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.function.Consumer;

public interface LuaCommand extends LuaValueHolder {

    String getName();

    String getDescription();

    // This has to return Object instead of void, otherwise it clashes with setDescription from BukkitCommand
    Object setDescription(String description);

    LuaArgumentType[] getArguments();

    default int getArgumentCount() {
        return getArguments().length;
    }

    void call(LuaCommandSender sender, String... args);

    void setExecutor(@Nullable Consumer<Varargs> executor);

    @Override
    default LuaTable getLuaValue() {
        LuaTable ret = LuaValue.tableOf();
        ret.set("GetName", new GetterFunction(this::getName));
        ret.set("GetDescription", new GetterFunction(this::getDescription));
        ret.set("SetDescription", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setDescription(arg.checkjstring());
                return LuaValue.NIL;
            }
        });
        ret.set("GetArguments", new VarArgMetaFunction() {
            @Override
            protected Varargs call(LuaTable self, Varargs args) {
                LuaArgumentType[] types = getArguments();
                LuaValue[] values = new LuaValue[types.length];
                for (int i = 0; i < types.length; i++) {
                    values[i] = LuaValue.valueOf(types[i].enumIndex());
                }
                return LuaValue.varargsOf(values);
            }
        });
        ret.set("GetArgumentCount", new GetterFunction(this::getArgumentCount));
        ret.set("Call", new VarArgMetaFunction() {
            @Override
            protected Varargs call(LuaTable self, Varargs args) {
                int narg = args.narg();
                String[] strings = new String[narg];
                for (int i=0; i < narg; i++) {
                    strings[i] = args.arg(i + 1).checkjstring();
                }
                LuaCommand.this.call(LuaVoidCommandSender.INSTANCE, strings);
                return LuaValue.NIL;
            }
        });
        ret.set("SetExecutor", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                LuaFunction lf = arg.checkfunction();
                setExecutor(lf::invoke);
                return LuaValue.NIL;
            }
        });
        return ret;
    }

}

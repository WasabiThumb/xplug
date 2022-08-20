package codes.wasabi.xplug.struct.text;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.LuaValueHolder;
import codes.wasabi.xplug.util.func.OneArgMetaFunction;
import codes.wasabi.xplug.util.func.VarArgMetaFunction;
import org.luaj.vm2.*;

public interface LuaAudience extends LuaValueHolder {

     void sendMessage(String message);

     void sendActionBar(String actionBar);

     void playSound(String soundName) throws IllegalArgumentException;

     void playSound(String soundName, double x, double y, double z) throws IllegalArgumentException;

     void stopSound(String soundName) throws IllegalArgumentException;

     void showTitle(String title, String subtitle);

    @Override
    default LuaTable getLuaValue() {
        LuaTable lt = new LuaTable();
        lt.set("SendMessage", new OneArgMetaFunction() {
            @Override
            public LuaValue call(LuaTable self, LuaValue arg) {
                sendMessage(arg.checkjstring());
                return LuaValue.NIL;
            }
        });
        lt.set("SendActionBar", new OneArgMetaFunction() {
            @Override
            public LuaValue call(LuaTable self, LuaValue arg) {
                sendActionBar(arg.checkjstring());
                return LuaValue.NIL;
            }
        });
        lt.set("PlaySound", new VarArgMetaFunction() {
            @Override
            public Varargs call(LuaTable self, Varargs args) {
                String soundName = args.arg(1).checkjstring();
                int narg = args.narg();
                try {
                    if (narg == 4) {
                        playSound(soundName, args.arg(2).checkdouble(), args.arg(3).checkdouble(), args.arg(4).checkdouble());
                    } else if (narg == 1) {
                        playSound(soundName);
                    } else {
                        throw new LuaError("This method takes 1 or 4 arguments");
                    }
                } catch (IllegalArgumentException e) {
                    throw new LuaError("Invalid sound name");
                }
                return LuaValue.NIL;
            }
        });
        lt.set("StopSound", new OneArgMetaFunction() {
            @Override
            public LuaValue call(LuaTable self, LuaValue arg) {
                try {
                    stopSound(arg.checkjstring());
                } catch (IllegalArgumentException e) {
                    throw new LuaError("Invalid sound name");
                }
                return LuaValue.NIL;
            }
        });
        lt.set("ShowTitle", new VarArgMetaFunction() {
            @Override
            public Varargs call(LuaTable self, Varargs args) {
                int narg = args.narg();
                if (narg < 1 || narg > 2) throw new LuaError("This method takes 1-2 arguments");
                String title = args.arg(1).checkjstring();
                String subtitle = "";
                if (narg == 2) subtitle = args.arg(2).checkjstring();
                showTitle(title, subtitle);
                return LuaValue.NIL;
            }
        });
        return lt;
    }

}

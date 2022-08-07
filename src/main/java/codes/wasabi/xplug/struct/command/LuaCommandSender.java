package codes.wasabi.xplug.struct.command;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.entity.LuaPlayer;
import codes.wasabi.xplug.struct.text.LuaAudience;
import codes.wasabi.xplug.util.func.GetterFunction;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;

public abstract class LuaCommandSender extends LuaAudience {

    public abstract boolean isConsole();

    public abstract boolean isPlayer();

    public abstract LuaPlayer toPlayer() throws UnsupportedOperationException;

    @Override
    public LuaTable getLuaValue() {
        LuaTable lt = super.getLuaValue();
        lt.set("IsConsole", new GetterFunction(this::isConsole));
        lt.set("IsPlayer", new GetterFunction(this::isPlayer));
        lt.set("ToPlayer", new GetterFunction(() -> {
            try {
                return toPlayer();
            } catch (UnsupportedOperationException e) {
                throw new LuaError("This command sender is not a player, use IsPlayer to check");
            }
        }));
        return lt;
    }
}

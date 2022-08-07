package codes.wasabi.xplug.struct.entity;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.text.LuaAudience;
import codes.wasabi.xplug.util.func.GetterFunction;
import codes.wasabi.xplug.util.func.OneArgMetaFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public abstract class LuaPlayer extends LuaAudience implements LuaEntity {

    public abstract @NotNull String getName();

    public abstract @Nullable String getDisplayName();

    public abstract @Nullable String getDisplayNameStripped();

    public abstract boolean isOp();

    public abstract boolean hasPermission(String permission);

    public abstract void kick(@Nullable String message);

    @Override
    public LuaTable getLuaValue() {
        LuaTable lt = super.getLuaValue();
        LuaEntity.fillMeta(lt, this);
        lt.set("GetName", new GetterFunction(this::getName));
        lt.set("GetDisplayName", new GetterFunction(this::getDisplayName));
        lt.set("GetDisplayNameStripped", new GetterFunction(this::getDisplayNameStripped));
        lt.set("IsOp", new GetterFunction(this::isOp));
        lt.set("HasPermission", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                return LuaValue.valueOf(hasPermission(arg.tojstring()));
            }
        });
        lt.set("Kick", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                String msg = null;
                if (arg.isstring()) {
                    msg = arg.tojstring();
                }
                kick(msg);
                return LuaValue.NIL;
            }
        });
        return lt;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public boolean hasHealth() {
        return true;
    }

}

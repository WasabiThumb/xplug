package codes.wasabi.xplug.struct.entity;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.command.LuaCommandSender;
import codes.wasabi.xplug.struct.inventory.LuaPlayerInventory;
import codes.wasabi.xplug.util.func.GetterFunction;
import codes.wasabi.xplug.util.func.OneArgMetaFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public interface LuaPlayer extends LuaEntity, LuaCommandSender {

    @NotNull String getName();

    @Nullable String getDisplayName();

    @Nullable String getDisplayNameStripped();

    boolean isOp();

    boolean hasPermission(String permission);

    void kick(@Nullable String message);

    LuaPlayerInventory getInventory();

    int getGameMode();

    void setGameMode(int mode);

    void hidePlayer(LuaPlayer player);

    void showPlayer(LuaPlayer player);

    @Override
    default LuaTable getLuaValue() {
        LuaTable lt = LuaCommandSender.super.getLuaValue();
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
        lt.set("GetInventory", new GetterFunction(this::getInventory));
        lt.set("GetGameMode", new GetterFunction(this::getGameMode));
        lt.set("SetGameMode", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setGameMode(arg.checkint());
                return LuaValue.NIL;
            }
        });
        lt.set("HidePlayer", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                hidePlayer(XPlug.getToolkit().parsePlayer(arg));
                return LuaValue.NIL;
            }
        });
        lt.set("ShowPlayer", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                showPlayer(XPlug.getToolkit().parsePlayer(arg));
                return LuaValue.NIL;
            }
        });
        return lt;
    }

    @Override
    default boolean isPlayer() {
        return true;
    }

    @Override
    default boolean hasHealth() {
        return true;
    }

}

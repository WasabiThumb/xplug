package codes.wasabi.xplug.struct.text;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/


import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaValueHolder;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import codes.wasabi.xplug.util.func.GetterFunction;
import codes.wasabi.xplug.util.func.OneArgMetaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface LuaBossBar extends LuaValueHolder {

    void setTitle(String title);

    String getTitle();

    void setVisible(boolean visible);

    boolean getVisible();

    void setColor(int color);

    int getColor();

    void setStyle(int style);

    int getStyle();

    void setProgress(double progress);

    double getProgress();

    Collection<LuaPlayer> getPlayers();

    void setPlayers(Collection<LuaPlayer> players);

    default void addPlayer(LuaPlayer player) {
        List<LuaPlayer> list = new ArrayList<>(getPlayers());
        list.add(player);
        setPlayers(list);
    }

    default void removePlayer(LuaPlayer player) {
        List<LuaPlayer> list = new ArrayList<>(getPlayers());
        String remove = player.getUUID();
        list.removeIf((LuaPlayer p) -> p.getUUID().equalsIgnoreCase(remove));
        setPlayers(list);
    }

    @Override
    default LuaTable getLuaValue() {
        LuaTable lt = LuaValue.tableOf();
        lt.set("SetTitle", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setTitle(arg.checkjstring());
                return LuaValue.NIL;
            }
        });
        lt.set("GetTitle", new GetterFunction(this::getTitle));
        lt.set("SetVisible", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setVisible(arg.checkboolean());
                return LuaValue.NIL;
            }
        });
        lt.set("GetVisible", new GetterFunction(this::getVisible));
        lt.set("SetColor", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setColor(arg.checkint());
                return LuaValue.NIL;
            }
        });
        lt.set("GetColor", new GetterFunction(this::getColor));
        lt.set("SetStyle", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setStyle(arg.checkint());
                return LuaValue.NIL;
            }
        });
        lt.set("GetStyle", new GetterFunction(this::getStyle));
        lt.set("SetProgress", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setProgress(arg.checkdouble());
                return LuaValue.NIL;
            }
        });
        lt.set("GetProgress", new GetterFunction(this::getProgress));
        lt.set("GetPlayers", new GetterFunction(this::getPlayers));
        lt.set("SetPlayers", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                LuaTable lt = arg.checktable();
                List<LuaPlayer> players = new ArrayList<>();
                for (LuaValue key : lt.keys()) {
                    LuaValue value = lt.get(key);
                    if (value.isnil()) continue;
                    LuaPlayer lp = XPlug.getToolkit().parsePlayer(value);
                    if (lp != null) players.add(lp);
                }
                setPlayers(players);
                return LuaValue.NIL;
            }
        });
        lt.set("AddPlayer", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                LuaPlayer lp = XPlug.getToolkit().parsePlayer(arg);
                if (lp != null) addPlayer(lp);
                return LuaValue.NIL;
            }
        });
        lt.set("RemovePlayer", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                LuaPlayer lp = XPlug.getToolkit().parsePlayer(arg);
                if (lp != null) removePlayer(lp);
                return LuaValue.NIL;
            }
        });
        return lt;
    }

}

package codes.wasabi.xplug.struct.world;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaValueHolder;
import codes.wasabi.xplug.struct.entity.LuaEntity;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import codes.wasabi.xplug.util.LuaBridge;
import codes.wasabi.xplug.util.func.GetterFunction;
import codes.wasabi.xplug.util.func.OneArgMetaFunction;
import codes.wasabi.xplug.util.func.TwoArgMetaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Collection;

public interface LuaWorld extends LuaValueHolder {

    /**
     * Get the name of the world
     * @return World name
     */
     String getName();

    /**
     * Get the environment of the world
     * @return An ENV enum index
     */
     int getEnvironment();

    /**
     * Gets the universally unique identifier for this world
     * @return The UUID
     */
     String getUUID();

    /**
     * Gets a chunk in the world by its X and Z coordinates
     * @param x The X coordinate
     * @param z The Z coordinate
     * @return The chunk
     */
     LuaChunk getChunk(int x, int z);

     Collection<LuaEntity> getEntities();

     Collection<LuaPlayer> getPlayers();

     int getMinHeight();

     int getMaxHeight();

    static boolean worldEquals(LuaWorld a, LuaWorld b) {
        if (a == null) {
            return b == null;
        } else {
            if (b == null) return false;
            return a.getName().equals(b.getName());
        }
    }

    @Override
    default LuaValue getLuaValue() {
        LuaTable lt = new LuaTable();
        lt.set("GetName", new GetterFunction(this::getName));
        lt.set("GetEnvironment", new GetterFunction(this::getEnvironment));
        lt.set("GetUUID", new GetterFunction(this::getUUID));
        lt.set("GetChunk", new TwoArgMetaFunction() {
            @Override
            public LuaValue call(LuaTable self, LuaValue arg1, LuaValue arg2) {
                return getChunk(arg1.checkint(), arg2.checkint()).getLuaValue();
            }
        });
        lt.set("GetEntities", new GetterFunction(this::getEntities));
        lt.set("GetPlayers", new GetterFunction(this::getPlayers));
        lt.set("GetMinHeight", new GetterFunction(this::getMinHeight));
        lt.set("GetMaxHeight", new GetterFunction(this::getMaxHeight));
        lt.set("Equals", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                return LuaValue.valueOf(worldEquals(LuaWorld.this, XPlug.getToolkit().getWorld(LuaBridge.extractName(arg))));
            }
        });
        return lt;
    }

}

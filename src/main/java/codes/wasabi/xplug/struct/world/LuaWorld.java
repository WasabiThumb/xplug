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
import codes.wasabi.xplug.struct.block.LuaBlock;
import codes.wasabi.xplug.struct.data.LuaLocation;
import codes.wasabi.xplug.struct.data.LuaRayTraceResult;
import codes.wasabi.xplug.struct.data.LuaVector;
import codes.wasabi.xplug.struct.entity.LuaEntity;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import codes.wasabi.xplug.util.LuaBridge;
import codes.wasabi.xplug.util.func.*;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

     LuaBlock getBlock(int x, int y, int z);

     @Nullable LuaEntity spawn(String type, LuaVector origin);

     Collection<LuaEntity> getNearbyEntities(String type, LuaVector origin, double radius);

     Collection<LuaPlayer> getNearbyPlayers(String type, LuaVector origin, double radius);

     @Nullable LuaRayTraceResult raytrace(LuaLocation start, LuaVector direction, double maxDistance, boolean hitEntities, boolean hitPassableBlocks, boolean hitWater);

    static boolean worldEquals(LuaWorld a, LuaWorld b) {
        if (a == null) {
            return b == null;
        } else {
            if (b == null) return false;
            return a.getName().equals(b.getName());
        }
    }

    @Override
    default LuaTable getLuaValue() {
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
        lt.set("GetBlock", new ThreeArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg1, LuaValue arg2, LuaValue arg3) {
                int x = arg1.toint();
                int y = arg2.toint();
                int z = arg3.toint();
                LuaBlock lb = getBlock(x, y, z);
                return lb.getLuaValue();
            }
        });
        lt.set("Spawn", new TwoArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg1, LuaValue arg2) {
                String et = arg1.tojstring();
                LuaVector pos = LuaVector.fromLuaValueAssert(arg2);
                LuaEntity le = spawn(et, pos);
                if (le == null) return LuaValue.NIL;
                return le.getLuaValue();
            }
        });
        lt.set("GetNearbyEntities", new ThreeArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg1, LuaValue arg2, LuaValue arg3) {
                String et = arg1.tojstring();
                LuaVector pos = LuaVector.fromLuaValueAssert(arg2);
                double radius = arg3.todouble();
                List<LuaEntity> list = new ArrayList<>(getNearbyEntities(et, pos, radius));
                LuaValue[] ret = new LuaValue[list.size()];
                for (int i=0; i < list.size(); i++) ret[i] = list.get(i).getLuaValue();
                return LuaValue.listOf(ret);
            }
        });
        lt.set("GetNearbyPlayers", new ThreeArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg1, LuaValue arg2, LuaValue arg3) {
                String et = arg1.tojstring();
                LuaVector pos = LuaVector.fromLuaValueAssert(arg2);
                double radius = arg3.todouble();
                List<LuaPlayer> list = new ArrayList<>(getNearbyPlayers(et, pos, radius));
                LuaValue[] ret = new LuaValue[list.size()];
                for (int i=0; i < list.size(); i++) ret[i] = list.get(i).getLuaValue();
                return LuaValue.listOf(ret);
            }
        });
        lt.set("Raytrace", new VarArgMetaFunction() {
            @Override
            protected Varargs call(LuaTable self, Varargs args) {
                LuaRayTraceResult res = raytrace(
                        LuaLocation.fromLuaValueAssert(args.arg(1)),
                        LuaVector.fromLuaValueAssert(args.arg(2)),
                        args.todouble(3),
                        (args.narg() < 4 || args.toboolean(4)),
                        (args.narg() >= 5 && args.toboolean(5)),
                        (args.narg() >= 6 && args.toboolean(6))
                );
                if (res == null) return LuaValue.varargsOf(new LuaValue[]{ LuaValue.NIL });
                return LuaValue.varargsOf(new LuaValue[]{ res.getLuaValue() });
            }
        });
        return lt;
    }

}

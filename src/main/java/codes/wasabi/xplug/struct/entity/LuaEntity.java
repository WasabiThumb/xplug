package codes.wasabi.xplug.struct.entity;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.LuaValueHolder;
import codes.wasabi.xplug.struct.data.LuaLocation;
import codes.wasabi.xplug.struct.data.LuaVector;
import codes.wasabi.xplug.struct.world.LuaWorld;
import codes.wasabi.xplug.util.LuaBridge;
import codes.wasabi.xplug.util.func.GetterFunction;
import codes.wasabi.xplug.util.func.OneArgMetaFunction;
import codes.wasabi.xplug.util.func.VarArgMetaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.ZeroArgFunction;

public interface LuaEntity extends LuaValueHolder {

    static void fillMeta(LuaTable lt, LuaEntity ent) {
        lt.set("GetUUID", new GetterFunction(ent::getUUID));
        lt.set("GetPos", new GetterFunction(ent::getLocation));
        lt.set("GetWorld", new GetterFunction(ent::getWorld));
        lt.set("GetX", new GetterFunction(ent::getX));
        lt.set("GetY", new GetterFunction(ent::getY));
        lt.set("GetZ", new GetterFunction(ent::getZ));
        lt.set("GetEntID", new GetterFunction(ent::getEntityID));
        lt.set("Remove", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                ent.remove();
                return LuaValue.NIL;
            }
        });
        lt.set("GetType", new GetterFunction(ent::getType));
        lt.set("Teleport", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                ent.teleport(LuaLocation.fromLuaValueAssert(arg));
                return LuaValue.NIL;
            }
        });
        lt.set("TeleportAsync", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                ent.teleportAsync(LuaLocation.fromLuaValueAssert(arg));
                return LuaValue.NIL;
            }
        });
        lt.set("IsPlayer", new GetterFunction(ent::isPlayer));
        lt.set("HasHealth", new GetterFunction(ent::hasHealth));
        lt.set("GetHealth", new GetterFunction(ent::getHealth));
        lt.set("Damage", new OneArgMetaFunction() {
            @Override
            public LuaValue call(LuaTable self, LuaValue arg) {
                ent.damage(arg.checkdouble());
                return LuaValue.NIL;
            }
        });
        lt.set("IsDead", new GetterFunction(ent::isDead));
        lt.set("GetVelocity", new GetterFunction(ent::getVelocity));
        lt.set("SetVelocity", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                ent.setVelocity(LuaVector.fromLuaValueAssert(arg));
                return LuaValue.NIL;
            }
        });
        lt.set("Unsafe", new VarArgMetaFunction() {
            @Override
            protected Varargs call(LuaTable self, Varargs args) {
                String member = args.tojstring(1);
                int argLen = args.narg() - 1;
                Object[] arg = new Object[argLen];
                for (int i=0; i < argLen; i++) {
                    LuaValue lv = args.arg(2 + i);
                    arg[i] = LuaBridge.fromLua(lv);
                }
                return LuaValue.valueOf(ent.unsafe(member, arg));
            }
        });
    }

    String getUUID();

    LuaLocation getLocation();

    LuaWorld getWorld();

    double getX();

    double getY();

    double getZ();

    int getEntityID();

    void remove();

    String getType();

    void teleport(LuaLocation location);

    void teleportAsync(LuaLocation location);

    boolean isPlayer();

    boolean hasHealth();

    void damage(double amount);

    double getHealth();

    boolean isDead();

    LuaVector getVelocity();

    void setVelocity(LuaVector vector);

    boolean unsafe(String member, Object... objects);

}

package codes.wasabi.xplug.struct.data;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaValueHolder;
import codes.wasabi.xplug.struct.world.LuaWorld;
import codes.wasabi.xplug.util.LuaBridge;
import codes.wasabi.xplug.util.func.GetterFunction;
import codes.wasabi.xplug.util.func.OneArgMetaFunction;
import codes.wasabi.xplug.util.func.VarArgMetaFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaLocation implements LuaValueHolder {

    private LuaWorld world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    public LuaLocation(@Nullable LuaWorld world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public LuaLocation(@Nullable LuaWorld world, double x, double y, double z) {
        this(world, x, y, z, 0f, 0f);
    }

    public static @Nullable LuaLocation fromLuaValue(LuaValue lv) {
        if (lv.istable()) {
            LuaWorld world = XPlug.getToolkit().getWorld(LuaBridge.extractName(lv.get(1)));
            double x = lv.get(2).todouble();
            double y = lv.get(3).todouble();
            double z = lv.get(4).todouble();
            float yaw = lv.get(5).tofloat();
            float pitch = lv.get(6).tofloat();
            return new LuaLocation(world, x, y, z, yaw, pitch);
        }
        return null;
    }

    public static @NotNull LuaLocation fromLuaValueAssert(LuaValue lv) throws LuaError {
        LuaLocation loc = fromLuaValue(lv);
        if (loc == null) throw new LuaError("Not a location");
        return loc;
    }

    public @Nullable LuaWorld getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public int getBlockX() {
        return (int) Math.floor(x);
    }

    public double getY() {
        return y;
    }

    public int getBlockY() {
        return (int) Math.floor(y);
    }

    public double getZ() {
        return z;
    }

    public int getBlockZ() {
        return (int) Math.floor(z);
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public LuaVector toVector() {
        return new LuaVector(x, y, z);
    }

    public LuaLocation toBlockLocation() {
        return new LuaLocation(world, getBlockX(), getBlockY(), getBlockZ(), yaw, pitch);
    }

    private static final double degToRad = Math.PI / 180d;
    private static final double twoUlp = (2 * Math.ulp(1.0d));
    public LuaVector getDirection() {
        // yaw
        // 0 = +z
        // 90 = -x
        // 180 = -z
        // 270 = +x

        // pitch
        // 0 = forward
        // 90 = -y
        // -90 = +y
        double radYaw = degToRad * yaw;
        double x = -Math.sin(radYaw);
        double z = Math.cos(radYaw);
        LuaVector fwd = new LuaVector(x, 0, z);
        if (pitch == 0) return fwd;
        LuaVector leftVector = new LuaVector(z, 0, -x);
        double radPitch = degToRad * pitch;
        double rpc = Math.cos(radPitch);
        double rps = Math.sin(radPitch);
        // rodrigues' formula
        fwd = fwd.cloneVector().multiply(rpc).add(leftVector.cross(fwd).multiply(rps)).add(leftVector.multiply(leftVector.dot(fwd)).multiply(1 - rpc));
        double mag = fwd.length();
        if (Math.abs(mag - 1d) > twoUlp) {
            fwd = fwd.divide(Math.sqrt(mag));
        }
        return fwd;
    }

    private static final double radToDeg = 180d / Math.PI;
    public LuaLocation setDirection(LuaVector direction) {
        LuaVector vec = direction.cloneVector();

        double x = vec.getX();
        double y = vec.getY();
        double z = vec.getZ();

        double yaw;
        if (x == 0 && z == 0) {
            yaw = 0;
        } else {
            yaw = (Math.atan2(z, x) * radToDeg) + 90;
            if (yaw < 0) yaw += 360d;
            if (yaw > 360) yaw -= 360d;
        }
        this.yaw = (float) yaw;

        double ay = Math.abs(y);
        double pitch;
        if (ay == 0) {
            pitch = 0;
        } else {
            double mag = vec.lengthSqr();
            if (Math.abs(mag - 1d) > twoUlp) {
                vec.divide(Math.sqrt(mag));
            }
            ay = Math.abs(y);
            pitch = Math.asin(ay) * radToDeg * (y < 0 ? 1 : -1);
        }
        this.pitch = (float) pitch;

        return this;
    }

    public LuaLocation setWorld(LuaWorld lw) {
        this.world = lw;
        return this;
    }

    public LuaLocation setX(double x) {
        this.x = x;
        return this;
    }

    public LuaLocation setY(double y) {
        this.y = y;
        return this;
    }

    public LuaLocation setZ(double z) {
        this.z = z;
        return this;
    }

    public LuaLocation setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public LuaLocation setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public LuaLocation zero() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.yaw = 0;
        this.pitch = 0;
        return this;
    }

    private void setTableParams(LuaTable lt) {
        lt.set(1, LuaBridge.toLua(world));
        lt.set(2, LuaValue.valueOf(x));
        lt.set(3, LuaValue.valueOf(y));
        lt.set(4, LuaValue.valueOf(z));
        lt.set(5, LuaValue.valueOf(yaw));
        lt.set(6, LuaValue.valueOf(pitch));
        lt.set("world", LuaBridge.toLua(world));
        lt.set("x", LuaValue.valueOf(x));
        lt.set("y", LuaValue.valueOf(y));
        lt.set("z", LuaValue.valueOf(z));
        lt.set("yaw", LuaValue.valueOf(yaw));
        lt.set("pitch", LuaValue.valueOf(pitch));
    }

    @Override
    public LuaValue getLuaValue() {
        LuaTable lt = LuaValue.tableOf();
        setTableParams(lt);
        lt.set("GetWorld", new GetterFunction(this::getWorld));
        lt.set("GetX", new GetterFunction(this::getX));
        lt.set("GetBlockX", new GetterFunction(this::getBlockX));
        lt.set("GetY", new GetterFunction(this::getY));
        lt.set("GetBlockY", new GetterFunction(this::getBlockY));
        lt.set("GetZ", new GetterFunction(this::getZ));
        lt.set("GetBlockZ", new GetterFunction(this::getBlockZ));
        lt.set("GetYaw", new GetterFunction(this::getYaw));
        lt.set("GetPitch", new GetterFunction(this::getPitch));
        lt.set("ToVector", new GetterFunction(this::toVector));
        lt.set("ToBlockLocation", new GetterFunction(this::toBlockLocation));
        lt.set("GetDirection", new GetterFunction(this::getDirection));
        lt.set("SetDirection", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setDirection(LuaVector.fromLuaValueAssert(arg));
                setTableParams(self);
                return self;
            }
        });
        lt.set("SetWorld", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setWorld(XPlug.getToolkit().getWorld(LuaBridge.extractName(arg)));
                setTableParams(self);
                return self;
            }
        });
        lt.set("SetX", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setX(arg.todouble());
                setTableParams(self);
                return self;
            }
        });
        lt.set("SetY", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setY(arg.todouble());
                setTableParams(self);
                return self;
            }
        });
        lt.set("SetZ", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setZ(arg.todouble());
                setTableParams(self);
                return self;
            }
        });
        lt.set("SetYaw", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setYaw(arg.tofloat());
                setTableParams(self);
                return self;
            }
        });
        lt.set("SetPitch", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setPitch(arg.tofloat());
                setTableParams(self);
                return self;
            }
        });
        lt.set("Zero", new VarArgMetaFunction() {
            @Override
            protected Varargs call(LuaTable self, Varargs args) {
                zero();
                setTableParams(self);
                return LuaValue.varargsOf(new LuaValue[]{ self });
            }
        });
        return lt;
    }

}

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

public class LuaVector implements LuaValueHolder {

    private double x;
    private double y;
    private double z;
    public LuaVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static @Nullable LuaVector fromLuaValue(LuaValue lv) {
        if (lv.istable()) {
            double x = lv.get(1).todouble();
            double y = lv.get(2).todouble();
            double z = lv.get(3).todouble();
            return new LuaVector(x, y, z);
        }
        return null;
    }

    public static @NotNull LuaVector fromLuaValueAssert(LuaValue lv) throws LuaError {
        LuaVector vec = fromLuaValue(lv);
        if (vec == null) throw new LuaError("Not a vector");
        return vec;
    }

    public double getX() {
        return x;
    }

    public LuaVector setX(double x){
        this.x = x;
        return this;
    }

    public int getBlockX() {
        return (int) Math.floor(x);
    }

    public double getY() {
        return y;
    }

    public LuaVector setY(double y){
        this.y = y;
        return this;
    }

    public int getBlockY() {
        return (int) Math.floor(y);
    }

    public double getZ() {
        return z;
    }
    public LuaVector setZ(double z){
        this.z = z;
        return this;
    }

    public int getBlockZ() {
        return (int) Math.floor(z);
    }

    public LuaVector cloneVector() {
        return new LuaVector(x, y, z);
    }

    public LuaVector copy(LuaVector other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    public LuaVector midpoint(LuaVector other) {
        double nx = (this.x + other.x) / 2d;
        double ny = (this.y + other.y) / 2d;
        double nz = (this.z + other.z) / 2d;
        this.x = nx;
        this.y = ny;
        this.z = nz;
        return this;
    }

    public LuaVector getMidpoint(LuaVector other) {
        double nx = (this.x + other.x) / 2d;
        double ny = (this.y + other.y) / 2d;
        double nz = (this.z + other.z) / 2d;
        return new LuaVector(nx, ny, nz);
    }

    public double lengthSqr() {
        return Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2);
    }

    public double length() {
        return Math.sqrt(lengthSqr());
    }

    public double distanceSqr(LuaVector other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double dz = other.z - this.z;
        return Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2);
    }

    public double distance(LuaVector other) {
        return Math.sqrt(distanceSqr(other));
    }

    public LuaVector add(double num) {
        this.x += num;
        this.y += num;
        this.z += num;
        return this;
    }

    public LuaVector add(LuaVector other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public LuaVector subtract(double num) {
        this.x -= num;
        this.y -= num;
        this.z -= num;
        return this;
    }

    public LuaVector subtract(LuaVector other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    public LuaVector multiply(double num) {
        this.x *= num;
        this.y *= num;
        this.z *= num;
        return this;
    }

    public LuaVector multiply(LuaVector other) {
        this.x *= other.x;
        this.y *= other.y;
        this.z *= other.z;
        return this;
    }

    public LuaVector divide(double num) {
        this.x /= num;
        this.y /= num;
        this.z /= num;
        return this;
    }

    public LuaVector divide(LuaVector other) {
        this.x /= other.x;
        this.y /= other.y;
        this.z /= other.z;
        return this;
    }

    public LuaVector zero() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        return this;
    }

    public double dot(LuaVector other) {
        return (this.x * other.x) + (this.y * other.y) + (this.z * other.z);
    }

    public LuaVector cross(LuaVector other) {
        double x = ((this.y * other.z) - (this.z * other.y));
        double y = ((this.z * other.x) - (this.x * other.z));
        double z = ((this.x * other.y) - (this.y * other.z));
        return new LuaVector(x, y, z);
    }

    public double angle(LuaVector other) {
        return Math.acos(dot(other) / (length() * other.length()));
    }

    public LuaVector toBlockVector() {
        return new LuaVector(getBlockX(), getBlockY(), getBlockZ());
    }

    public LuaLocation toLocation(LuaWorld world) {
        return new LuaLocation(world, x, y, z);
    }

    public LuaLocation toLocation(LuaWorld world, float yaw, float pitch) {
        return new LuaLocation(world, x, y, z, yaw, pitch);
    }

    private void setTableParams(LuaTable lt) {
        lt.set(1, LuaValue.valueOf(x));
        lt.set(2, LuaValue.valueOf(y));
        lt.set(3, LuaValue.valueOf(z));
        lt.set("x", x);
        lt.set("y", y);
        lt.set("z", z);
    }

    @Override
    public LuaTable getLuaValue() {
        LuaTable lt = LuaValue.tableOf();
        setTableParams(lt);
        lt.set("GetX", new GetterFunction(this::getX));
        lt.set("GetY", new GetterFunction(this::getY));
        lt.set("GetZ", new GetterFunction(this::getZ));
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
        lt.set("GetBlockX", new GetterFunction(this::getBlockX));
        lt.set("GetBlockY", new GetterFunction(this::getBlockY));
        lt.set("GetBlockZ", new GetterFunction(this::getBlockZ));
        lt.set("Clone", new GetterFunction(this::cloneVector));
        lt.set("Copy", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                LuaVector vec = fromLuaValueAssert(arg);
                copy(vec);
                setTableParams(self);
                return self;
            }
        });
        lt.set("Midpoint", new VarArgMetaFunction() {
            @Override
            protected Varargs call(LuaTable self, Varargs args) {
                midpoint(fromLuaValueAssert(args.arg(1)));
                setTableParams(self);
                return LuaValue.varargsOf(new LuaValue[]{ self });
            }
        });
        lt.set("GetMidpoint", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                return getMidpoint(fromLuaValueAssert(arg)).getLuaValue();
            }
        });
        lt.set("Length", new GetterFunction(this::length));
        lt.set("LengthSqr", new GetterFunction(this::lengthSqr));
        lt.set("Distance", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                return LuaValue.valueOf(distance(fromLuaValueAssert(arg)));
            }
        });
        lt.set("DistanceSqr", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                return LuaValue.valueOf(distance(fromLuaValueAssert(arg)));
            }
        });
        lt.set("Add", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                LuaVector.this.add(fromLuaValueAssert(arg));
                setTableParams(self);
                return self;
            }
        });
        lt.set("Subtract", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                LuaVector.this.subtract(fromLuaValueAssert(arg));
                setTableParams(self);
                return self;
            }
        });
        lt.set("Multiply", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                LuaVector.this.multiply(fromLuaValueAssert(arg));
                setTableParams(self);
                return self;
            }
        });
        lt.set("Divide", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                LuaVector.this.divide(fromLuaValueAssert(arg));
                setTableParams(self);
                return self;
            }
        });
        lt.set("Angle", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                return LuaValue.valueOf(angle(fromLuaValueAssert(arg)));
            }
        });
        lt.set("Dot", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                return LuaValue.valueOf(dot(fromLuaValueAssert(arg)));
            }
        });
        lt.set("Cross", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                return cross(fromLuaValueAssert(arg)).getLuaValue();
            }
        });
        lt.set("ToBlockVector", new GetterFunction(this::toBlockVector));
        lt.set("Zero", new VarArgMetaFunction() {
            @Override
            protected Varargs call(LuaTable self, Varargs args) {
                zero();
                setTableParams(self);
                return LuaValue.varargsOf(new LuaValue[]{ self });
            }
        });
        lt.set("ToLocation", new VarArgMetaFunction() {
            @Override
            protected Varargs call(LuaTable self, Varargs args) {
                LuaWorld world = XPlug.getToolkit().getWorld(LuaBridge.extractName(args.arg(1)));
                if (args.narg() == 1) {
                    return toLocation(world).getLuaValue();
                } else {
                    float yaw = args.tofloat(2);
                    float pitch = args.tofloat(3);
                    return toLocation(world, yaw, pitch).getLuaValue();
                }
            }
        });
        return lt;
    }

}

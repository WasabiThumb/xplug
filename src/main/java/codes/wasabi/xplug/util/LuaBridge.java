package codes.wasabi.xplug.util;
/*
  XPlug | A LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.LuaValueHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.*;

/**
 * A class designed to quickly port primitives and collections to LuaValue, as well as provide some utility methods for parsing inputs
 */
public class LuaBridge {

    public static LuaValue toLua(@Nullable Object o) {
        if (o == null) {
            return LuaValue.NIL;
        } else if (o instanceof LuaValue) {
            return (LuaValue) o;
        } else if (o instanceof LuaValueHolder) {
            return ((LuaValueHolder) o).getLuaValue();
        } else if (o instanceof JsonElement) {
            JsonElement el = (JsonElement) o;
            if (el.isJsonNull()) return LuaValue.NIL;
            if (el.isJsonArray()) {
                JsonArray arr = el.getAsJsonArray();
                int size = arr.size();
                LuaValue[] lvs = new LuaValue[size];
                for (int i=0; i < size; i++) {
                    lvs[i] = toLua(arr.get(i));
                }
                return LuaValue.listOf(lvs);
            } else if (el.isJsonObject()) {
                JsonObject ob = el.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entries = ob.entrySet();
                LuaValue[] lvs = new LuaValue[entries.size() * 2];
                int i = 0;
                for (Map.Entry<String, JsonElement> entry : entries) {
                    String s = entry.getKey();
                    try {
                        double d = Double.parseDouble(s);
                        lvs[i] = LuaValue.valueOf(d);
                    } catch (NumberFormatException ignored) {
                        lvs[i] = LuaValue.valueOf(s);
                    }
                    i++;
                    lvs[i] = toLua(entry.getValue());
                    i++;
                }
                return LuaValue.tableOf(lvs);
            } else {
                JsonPrimitive primitive = el.getAsJsonPrimitive();
                if (primitive.isBoolean()) return LuaValue.valueOf(primitive.getAsBoolean());
                if (primitive.isNumber()) return LuaValue.valueOf(primitive.getAsDouble());
                return LuaValue.valueOf(primitive.getAsString());
            }
        } else if (o instanceof Integer) {
            return LuaValue.valueOf((Integer) o);
        } else if (o instanceof Short) {
            return LuaValue.valueOf((Short) o);
        } else if (o instanceof Byte) {
            return LuaValue.valueOf((Byte) o);
        } else if (o instanceof Double) {
            return LuaValue.valueOf((Double) o);
        } else if (o instanceof Long) {
            return LuaValue.valueOf((Long) o);
        } else if (o instanceof String) {
            return LuaValue.valueOf((String) o);
        } else if (o instanceof Boolean) {
            return LuaValue.valueOf((Boolean) o);
        } else if (o instanceof byte[]) {
            return LuaValue.valueOf((byte[]) o);
        } else if (o instanceof Object[]) {
            Object[] arr = (Object[]) o;
            int len = arr.length;
            LuaValue[] newArray = new LuaValue[len];
            for (int i=0; i < len; i++) {
                newArray[i] = toLua(arr[i]);
            }
            return LuaValue.listOf(newArray);
        } else if (o instanceof Collection<?>) {
            Collection<?> coll = Collections.unmodifiableCollection((Collection<?>) o);
            int len = coll.size();
            LuaValue[] newArray = new LuaValue[len];
            int i = 0;
            for (Object b : coll) {
                newArray[i] = toLua(b);
                i++;
            }
            return LuaValue.listOf(newArray);
        } else if (o instanceof Map<?, ?>) {
            Map<?, ?> map = Collections.unmodifiableMap((Map<?, ?>) o);
            int len = map.size();
            LuaValue[] values = new LuaValue[len * 2];
            int i = 0;
            for (Map.Entry<?, ?> e : map.entrySet()) {
                values[i] = toLua(e.getKey());
                i++;
                values[i] = toLua(e.getValue());
                i++;
            }
            return LuaValue.tableOf(values);
        }
        return LuaValue.userdataOf(o);
    }

    public static @Nullable Object fromLua(LuaValue lv) {
        if (lv == null) return null;
        if (lv.isnil()) return null;
        if (lv.isboolean()) {
            return lv.toboolean();
        } else if (lv.isnumber()) {
            return lv.todouble();
        } else if (lv.isstring()) {
            return lv.tojstring();
        } else if (lv.istable()) {
            LuaTable lt = lv.checktable();
            LuaValue[] keys = lt.keys();
            if (keys.length == 0) return new HashMap<>();
            if (Arrays.stream(keys).allMatch(LuaValue::isint)) {
                int minKey = Integer.MAX_VALUE;
                int maxKey = Integer.MIN_VALUE;
                for (LuaValue val : keys) {
                    int iVal = val.toint();
                    minKey = Math.min(minKey, iVal);
                    maxKey = Math.max(maxKey, iVal);
                }
                if (minKey > 0) {
                    int range = (maxKey - minKey) + 1;
                    Object[] ret = new Object[range];
                    for (int i = 0; i < range; i++) {
                        ret[i] = fromLua(lt.get(minKey + i));
                    }
                    return Arrays.asList(ret);
                }
            }
            Map<Object, Object> map = new HashMap<>();
            for (LuaValue val : keys) {
                map.put(fromLua(val), fromLua(lt.get(val)));
            }
            return map;
        } else {
            return lv.touserdata();
        }
    }

    public static @NotNull String extractString(@NotNull LuaValue lv, @NotNull String methodName) {
        if (lv.isstring()) {
            return lv.checkjstring();
        }
        LuaValue value = lv.get(methodName);
        if (value.isnil()) return lv.tojstring();
        if (value.isstring()) return value.checkjstring();
        if (value.isfunction()) {
            LuaFunction lf = value.checkfunction();
            LuaValue ret = lf.call();
            return ret.tojstring();
        }
        return lv.tojstring();
    }

    public static @NotNull String extractName(LuaValue lv) {
        return extractString(lv, "GetName");
    }

    public static @NotNull UUID extractUUID(LuaValue lv) throws LuaError {
        String st = extractString(lv, "GetUUID");
        UUID ret;
        try {
            ret = UUID.fromString(st);
        } catch (IllegalArgumentException e) {
            throw new LuaError("String \"" + st + "\" is not a valid UUID");
        }
        return ret;
    }

    public static int extractInt(@NotNull LuaValue lv, @NotNull String methodName) {
        if (lv.isint()) {
            return lv.checkint();
        }
        LuaValue value = lv.get(methodName);
        if (value.isnil()) return lv.toint();
        if (value.isint()) return value.checkint();
        if (value.isfunction()) {
            LuaFunction lf = value.checkfunction();
            LuaValue ret = lf.call();
            return ret.toint();
        }
        return lv.toint();
    }

    public static double extractDouble(@NotNull LuaValue lv, @NotNull String methodName) {
        if (lv.isnumber()) {
            return lv.todouble();
        }
        LuaValue value = lv.get(methodName);
        if (value.isnil()) return lv.todouble();
        if (value.isnumber()) return value.todouble();
        if (value.isfunction()) {
            LuaFunction lf = value.checkfunction();
            LuaValue ret = lf.call();
            return ret.todouble();
        }
        return lv.todouble();
    }

    public static <T> @Nullable T extractUserdata(@NotNull LuaValue lv, @NotNull String methodName, @NotNull Class<? extends T> clazz) {
        LuaValue value;
        try {
            value = lv.get(methodName);
        } catch (LuaError e) {
            value = LuaValue.NIL;
        }
        if (value.isfunction()) {
            LuaValue lv1 = value.call();
            if (lv1.isuserdata()) {
                Object vud = lv1.touserdata();
                if (clazz.isInstance(vud)) return clazz.cast(vud);
            }
        } else if (value.isuserdata()) {
            Object vud = value.touserdata();
            if (clazz.isInstance(vud)) return clazz.cast(vud);
        }
        if (lv.isuserdata()) {
            Object ud = lv.touserdata();
            if (clazz.isInstance(ud)) return clazz.cast(ud);
        }
        return null;
    }

    public static <T> @Nullable T extractHandle(@NotNull LuaValue lv, @NotNull Class<? extends T> clazz) {
        return extractUserdata(lv, "GetHandle", clazz);
    }

}

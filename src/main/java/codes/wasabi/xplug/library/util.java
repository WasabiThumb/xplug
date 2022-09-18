package codes.wasabi.xplug.library;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.data.LuaLocation;
import codes.wasabi.xplug.struct.data.LuaVector;
import codes.wasabi.xplug.struct.inventory.LuaItemStack;
import codes.wasabi.xplug.struct.material.LuaMaterial;
import codes.wasabi.xplug.struct.text.LuaBossBar;
import codes.wasabi.xplug.struct.world.LuaWorld;
import codes.wasabi.xplug.util.LuaBridge;
import codes.wasabi.xplug.util.func.NilFunction;
import com.google.gson.*;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class util extends TwoArgFunction {

    private static final Gson gson = new Gson();
    private static final Gson gsonPretty = (new GsonBuilder()).setPrettyPrinting().create();

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable library = tableOf();
        library.set("JSONToTable", new JSONToTable());
        library.set("TableToJSON", new TableToJSON());
        library.set("Base64Encode", new Base64Encode());
        library.set("Base64Decode", new Base64Decode());
        library.set("Compress", new Compress());
        library.set("Decompress", new Decompress());
        library.set("CRC", new CRC());
        library.set("MD5", new MD5());
        library.set("SHA1", new SHA1());
        library.set("SHA256", new SHA256());
        env.set("util", library);
        env.set("PrintTable", new PrintTable(env.get("print")));
        env.set("Location", new Location());
        env.set("Vector", new Vector());
        env.set("Material", new Material());
        env.set("ItemStack", new ItemStack());
        env.set("BossBar", new BossBar());
        return library;
    }

    static class JSONToTable extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String json = arg.checkjstring();
            JsonElement je;
            try {
                je = gson.fromJson(json, JsonElement.class);
            } catch (JsonSyntaxException e) {
                return LuaValue.NIL;
            }
            LuaValue lv = LuaBridge.toLua(je);
            if (!lv.istable()) return LuaValue.NIL;
            return lv;
        }
    }

    static class TableToJSON extends VarArgFunction {
        private JsonElement objectToElement(Object obj) {
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                JsonArray arr = new JsonArray();
                for (Object ob : list) arr.add(objectToElement(ob));
                return arr;
            } else if (obj instanceof Map<?, ?>) {
                Map<?, ?> map = (Map<?, ?>) obj;
                JsonObject ret = new JsonObject();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    String s;
                    Object ob = entry.getKey();
                    if (ob instanceof String) {
                        s = (String) ob;
                    } else {
                        s = ob.toString();
                    }
                    ret.add(s, objectToElement(entry.getValue()));
                }
                return ret;
            } else if (obj instanceof Boolean) {
                return new JsonPrimitive((Boolean) obj);
            } else if (obj instanceof Double) {
                return new JsonPrimitive((Double) obj);
            } else if (obj instanceof String) {
                return new JsonPrimitive((String) obj);
            } else {
                return new JsonPrimitive(obj.toString());
            }
        }

        @Override
        public Varargs invoke(Varargs args) {
            int narg = args.narg();
            if (narg < 1 || narg > 2) throw new LuaError("This method takes 1-2 arguments");
            LuaTable table = args.checktable(1);
            Gson g = gson;
            if (narg == 2) {
                LuaValue arg2 = args.arg(2);
                if (arg2.isboolean()) {
                    if (arg2.toboolean()) g = gsonPretty;
                }
            }
            Object ob = LuaBridge.fromLua(table);
            JsonElement el = objectToElement(ob);
            return valueOf(g.toJson(el));
        }
    }

    static class Base64Encode extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int narg = args.narg();
            if (narg < 1 || narg > 2) throw new LuaError("This method takes 1-2 arguments");
            String utf8 = args.checkjstring(1);
            boolean inline = false;
            if (narg == 2) {
                LuaValue v = args.arg(2);
                if (v.isboolean()) inline = v.toboolean();
            }
            String encoded = new String(Base64.getEncoder().encode(utf8.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            if (inline || encoded.length() <= 76) return valueOf(encoded);
            StringBuilder out = new StringBuilder();
            int thisLine = 0;
            char[] chars = encoded.toCharArray();
            int len = chars.length;
            for (int i=0; i < len; i++) {
                char c = chars[i];
                out.append(c);
                thisLine++;
                if (thisLine >= 76 && (i < (len - 1))) {
                    out.append(System.lineSeparator());
                    thisLine = 0;
                }
            }
            return valueOf(out.toString());
        }
    }

    static class Base64Decode extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            try {
                String base64 = arg.checkjstring().replaceAll("(\\r)?\\n", "");
                String utf8 = new String(Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
                return valueOf(utf8);
            } catch (IllegalArgumentException e) {
                throw new LuaError("Not valid base64 format");
            }
        }
    }

    static class PrintTable extends OneArgFunction {

        private final LuaFunction print;

        PrintTable(LuaValue printfunc) {
            if (printfunc.isfunction()) {
                print = printfunc.checkfunction();
            } else {
                print = new NilFunction();
            }
        }

        @Override
        public LuaValue call(LuaValue arg) {
            if (!arg.istable()) {
                print.call(arg);
                return LuaValue.NIL;
            }
            LuaTable lt = arg.checktable();
            LuaValue[] keys = lt.keys();
            for (LuaValue lv : keys) {
                print.call(valueOf(lv.tojstring() + " = " + lt.get(lv).tojstring()));
            }
            return LuaValue.NIL;
        }

    }

    static class Compress extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String raw = arg.checkjstring();
            byte[] bytes = raw.getBytes(StandardCharsets.UTF_8);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (DeflaterOutputStream dfos = new DeflaterOutputStream(bos)) {
                dfos.write(bytes);
                dfos.flush();
            } catch (IOException ignored) { }
            return valueOf(new String(Base64.getEncoder().encode(bos.toByteArray()), StandardCharsets.UTF_8));
        }
    }

    static class Decompress extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String encoded = arg.checkjstring();
            byte[] bytes = Base64.getDecoder().decode(encoded.getBytes(StandardCharsets.UTF_8));
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                InflaterInputStream ifis = new InflaterInputStream(bis);
                InputStreamReader reader = new InputStreamReader(ifis, StandardCharsets.UTF_8);
                StringBuilder sb = new StringBuilder();
                CharBuffer cb = CharBuffer.allocate(64);
                int read;
                while ((read = reader.read(cb)) != -1) {
                    cb.position(0);
                    sb.append(cb.subSequence(0, read));
                }
                return valueOf(sb.toString());
            } catch (IOException e) {
                return LuaValue.NIL;
            }
        }
    }

    static class CRC extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String data = arg.checkjstring();
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            CRC32 crc = new CRC32();
            crc.update(bytes, 0, bytes.length);
            long l = crc.getValue();
            return valueOf(Long.toUnsignedString(l));
        }
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static String digest(String algorithm, String data) {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (Exception e) {
            return "";
        }
        byte[] digest = md.digest(bytes);
        return bytesToHex(digest);
    }

    static class MD5 extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String data = arg.checkjstring();
            return valueOf(digest("MD5", data));
        }
    }

    static class SHA1 extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String data = arg.checkjstring();
            return valueOf(digest("SHA-1", data));
        }
    }

    static class SHA256 extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String data = arg.checkjstring();
            return valueOf(digest("SHA-256", data));
        }
    }

    static class Location extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaWorld lw = XPlug.getToolkit().getWorld(LuaBridge.extractName(args.arg(1)));
            double x = args.todouble(2);
            double y = args.todouble(3);
            double z = args.todouble(4);
            float yaw = args.tofloat(5);
            float pitch = args.tofloat(6);
            return LuaValue.varargsOf(new LuaValue[]{ (new LuaLocation(lw, x, y, z, yaw, pitch)).getLuaValue() });
        }
    }

    static class Vector extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            return (new LuaVector(arg1.todouble(), arg2.todouble(), arg3.todouble())).getLuaValue();
        }
    }

    static class Material extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaValue obj = args.checkvalue(1);
            boolean exact = false;
            if (args.narg() > 1) {
                exact = args.toboolean(2);
            }
            LuaMaterial lm = XPlug.getToolkit().parseMaterial(obj, exact);
            if (lm == null) return LuaValue.NIL;
            return LuaValue.varargsOf(new LuaValue[]{ lm.getLuaValue() });
        }
    }

    static class ItemStack extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaValue mat = args.checkvalue(1);
            LuaMaterial lm = XPlug.getToolkit().parseMaterial(mat, true);
            if (lm == null) throw new LuaError("Invalid material");
            int count = 1;
            if (args.narg() > 1) {
                count = Math.max(args.toint(2), 0);
            }
            LuaItemStack lis = XPlug.getToolkit().createItemStack(lm, count);
            return lis.getLuaValue();
        }
    }

    static class BossBar extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            String title = args.optjstring(1, "");
            int color = args.optint(2, enums.BC_PURPLE);
            int style = args.optint(3, enums.BS_SOLID);
            boolean createFog = args.optboolean(4, false);
            boolean darkenSky = args.optboolean(5, false);
            boolean bossMusic = args.optboolean(6, false);
            LuaBossBar bar = XPlug.getToolkit().createBossBar(title, color, style, createFog, darkenSky, bossMusic);
            return bar.getLuaValue();
        }
    }

}

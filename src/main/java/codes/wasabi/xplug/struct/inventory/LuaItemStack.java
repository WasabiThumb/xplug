package codes.wasabi.xplug.struct.inventory;
/*
  XPlug | A LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaValueHolder;
import codes.wasabi.xplug.struct.material.LuaMaterial;
import codes.wasabi.xplug.util.func.GetterFunction;
import codes.wasabi.xplug.util.func.OneArgMetaFunction;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public interface LuaItemStack extends LuaValueHolder {

    LuaMaterial getMaterial();

    int getCount();

    void setMaterial(LuaMaterial material);

    void setCount(int count);

    int getStackSize();

    @Nullable String getDisplayName();

    @Nullable String getDisplayNameStripped();

    void setDisplayName(String displayName);

    byte[] toByteArray();

    @Override
    default LuaTable getLuaValue() {
        LuaTable lt = LuaValue.tableOf();
        lt.set("GetMaterial", new GetterFunction(this::getMaterial));
        lt.set("GetCount", new GetterFunction(this::getCount));
        lt.set("SetMaterial", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                LuaMaterial lm = XPlug.getToolkit().parseMaterial(arg, true);
                if (lm == null) throw new LuaError("Invalid material");
                setMaterial(lm);
                return LuaValue.NIL;
            }
        });
        lt.set("SetCount", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                int ct = arg.toint();
                setCount(Math.min(Math.max(ct, 0), getStackSize()));
                return LuaValue.NIL;
            }
        });
        lt.set("GetStackSize", new GetterFunction(this::getStackSize));
        lt.set("ToBytes", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                byte[] bytes = toByteArray();
                LuaValue[] lvs = new LuaValue[bytes.length];
                for (int i=0; i < bytes.length; i++) {
                    lvs[i] = LuaValue.valueOf(bytes[i]);
                }
                return LuaValue.listOf(lvs);
            }
        });
        lt.set("ToString", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                byte[] bytes = toByteArray();
                byte[] encoded = Base64.getEncoder().encode(bytes);
                String string = new String(encoded, StandardCharsets.UTF_8);
                return LuaValue.valueOf(string);
            }
        });
        lt.set("GetDisplayName", new GetterFunction(this::getDisplayName));
        lt.set("GetDisplayNameStripped", new GetterFunction(this::getDisplayNameStripped));
        lt.set("SetDisplayName", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setDisplayName(arg.isnil() ? null : arg.checkjstring());
                return LuaValue.NIL;
            }
        });
        return lt;
    }

}

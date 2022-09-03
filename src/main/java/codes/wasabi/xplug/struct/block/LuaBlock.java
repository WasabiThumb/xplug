package codes.wasabi.xplug.struct.block;
/*
  XPlug | A LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaValueHolder;
import codes.wasabi.xplug.struct.data.LuaLocation;
import codes.wasabi.xplug.struct.material.LuaMaterial;
import codes.wasabi.xplug.struct.world.LuaChunk;
import codes.wasabi.xplug.struct.world.LuaWorld;
import codes.wasabi.xplug.util.func.GetterFunction;
import codes.wasabi.xplug.util.func.OneArgMetaFunction;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public interface LuaBlock extends LuaValueHolder {

    LuaWorld getWorld();

    LuaChunk getChunk();

    LuaLocation getLocation();

    default int getX() {
        return getLocation().getBlockX();
    }

    default int getY() {
        return getLocation().getBlockY();
    }

    default int getZ() {
        return getLocation().getBlockZ();
    }

    LuaMaterial getMaterial();

    void setMaterial(LuaMaterial material);

    @Override
    default LuaTable getLuaValue() {
        LuaTable lt = LuaValue.tableOf();
        lt.set("GetWorld", new GetterFunction(this::getWorld));
        lt.set("GetChunk", new GetterFunction(this::getChunk));
        lt.set("GetLocation", new GetterFunction(this::getLocation));
        lt.set("GetX", new GetterFunction(this::getX));
        lt.set("GetY", new GetterFunction(this::getY));
        lt.set("GetZ", new GetterFunction(this::getZ));
        lt.set("GetMaterial", new GetterFunction(this::getMaterial));
        lt.set("SetMaterial", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                LuaMaterial lm = XPlug.getToolkit().parseMaterial(arg, true);
                if (lm == null) throw new LuaError("Invalid material");
                setMaterial(lm);
                return LuaValue.NIL;
            }
        });
        return lt;
    }

}

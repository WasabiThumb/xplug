package codes.wasabi.xplug.struct;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.material.LuaMaterial;
import codes.wasabi.xplug.struct.world.LuaWorld;
import java.awt.Color;

public interface LuaParticleTools {

    void start(String particle, LuaWorld world, double x, double y, double z) throws IllegalArgumentException;

    void setAmount(int amount);

    void setColor(Color color);

    void setOffsetX(float x);

    void setOffsetY(float y);

    void setOffsetZ(float z);

    default void setOffset(float x, float y, float z) {
        setOffsetX(x);
        setOffsetY(y);
        setOffsetZ(z);
    }

    void setSpeed(float speed);

    void setMaterial(LuaMaterial mat);

    void end();

}

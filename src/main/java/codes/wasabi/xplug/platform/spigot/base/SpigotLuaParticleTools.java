package codes.wasabi.xplug.platform.spigot.base;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.LuaParticleTools;
import codes.wasabi.xplug.struct.world.LuaWorld;
import org.bukkit.Location;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.awt.Color;

public class SpigotLuaParticleTools implements LuaParticleTools {

    private ParticleBuilder builder = null;

    @Override
    public void start(String particle, LuaWorld world, double x, double y, double z) throws IllegalArgumentException {
        builder = new ParticleBuilder(ParticleEffect.valueOf(particle), new Location(SpigotLuaToolkit.getAdapter().convertWorld(world), x, y, z));
    }

    @Override
    public void setAmount(int amount) {
        if (builder != null) builder.setAmount(amount);
    }

    @Override
    public void setColor(Color color) {
        if (builder != null) builder.setColor(color);
    }

    @Override
    public void setOffsetX(float x) {
        if (builder != null) builder.setOffsetX(x);
    }

    @Override
    public void setOffsetY(float y) {
        if (builder != null) builder.setOffsetY(y);
    }

    @Override
    public void setOffsetZ(float z) {
        if (builder != null) builder.setOffsetZ(z);
    }

    @Override
    public void setOffset(float x, float y, float z) {
        if (builder != null) builder.setOffset(x, y, z);
    }

    @Override
    public void setSpeed(float speed) {
        if (builder != null) builder.setSpeed(speed);
    }

    @Override
    public void end() {
        if (builder != null) builder.display();
        builder = null;
    }

}

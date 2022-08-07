package codes.wasabi.xplug.platform.spigot.base.entity;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.struct.data.LuaLocation;
import codes.wasabi.xplug.struct.data.LuaVector;
import codes.wasabi.xplug.struct.entity.LuaNPC;
import codes.wasabi.xplug.struct.world.LuaWorld;
import io.papermc.lib.PaperLib;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class SpigotLuaNPC extends LuaNPC {

    private final Entity entity;
    public SpigotLuaNPC(Entity entity) {
        this.entity = entity;
    }

    public Entity getBukkitEntity() {
        return entity;
    }

    @Override
    public String getUUID() {
        return entity.getUniqueId().toString();
    }

    @Override
    public LuaLocation getLocation() {
        return SpigotLuaToolkit.getAdapter().convertLocation(entity.getLocation());
    }

    @Override
    public LuaWorld getWorld() {
        return SpigotLuaToolkit.getAdapter().convertWorld(entity.getWorld());
    }

    @Override
    public double getX() {
        return entity.getLocation().getX();
    }

    @Override
    public double getY() {
        return entity.getLocation().getY();
    }

    @Override
    public double getZ() {
        return entity.getLocation().getZ();
    }

    @Override
    public int getEntityID() {
        return entity.getEntityId();
    }

    @Override
    public void remove() {
        entity.remove();
    }

    @Override
    public String getType() {
        return entity.getType().toString();
    }

    @Override
    public void teleport(LuaLocation loc) {
        entity.teleport(SpigotLuaToolkit.getAdapter().convertLocation(loc));
    }

    @Override
    public void teleportAsync(LuaLocation loc) {
        PaperLib.teleportAsync(entity, SpigotLuaToolkit.getAdapter().convertLocation(loc));
    }

    @Override
    public boolean hasHealth() {
        return (entity instanceof Damageable);
    }

    @Override
    public void damage(double amount) {
        if (entity instanceof Damageable) {
            ((Damageable) entity).damage(amount);
        }
    }

    @Override
    public double getHealth() {
        if (entity instanceof Damageable) {
            return ((Damageable) entity).getHealth();
        }
        return -1;
    }

    @Override
    public boolean isDead() {
        return entity.isDead();
    }

    @Override
    public LuaVector getVelocity() {
        Vector vel = entity.getVelocity();
        return SpigotLuaToolkit.getAdapter().convertVector(vel);
    }

}

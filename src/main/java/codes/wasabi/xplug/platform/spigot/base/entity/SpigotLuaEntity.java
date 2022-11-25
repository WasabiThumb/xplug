package codes.wasabi.xplug.platform.spigot.base.entity;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.platform.spigot.base.inventory.SpigotLuaEntityEquipment;
import codes.wasabi.xplug.struct.data.LuaLocation;
import codes.wasabi.xplug.struct.data.LuaVector;
import codes.wasabi.xplug.struct.entity.LuaEntity;
import codes.wasabi.xplug.struct.inventory.LuaEntityEquipment;
import codes.wasabi.xplug.struct.world.LuaWorld;
import io.papermc.lib.PaperLib;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaError;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public interface SpigotLuaEntity extends LuaEntity {

    Entity getBukkitEntity();

    @Override
    default String getUUID() {
        return getBukkitEntity().getUniqueId().toString();
    }

    @Override
    default LuaLocation getLocation() {
        return SpigotLuaToolkit.getAdapter().convertLocation(getBukkitEntity().getLocation());
    }

    @Override
    default LuaWorld getWorld() {
        return SpigotLuaToolkit.getAdapter().convertWorld(getBukkitEntity().getWorld());
    }

    @Override
    default double getX() {
        return getBukkitEntity().getLocation().getX();
    }

    @Override
    default double getY() {
        return getBukkitEntity().getLocation().getY();
    }

    @Override
    default double getZ() {
        return getBukkitEntity().getLocation().getZ();
    }

    @Override
    default int getEntityID() {
        return getBukkitEntity().getEntityId();
    }

    @Override
    default void remove() {
        getBukkitEntity().remove();
    }

    @Override
    default String getType() {
        return getBukkitEntity().getType().toString();
    }

    @Override
    default void teleport(LuaLocation location) {
        getBukkitEntity().teleport(SpigotLuaToolkit.getAdapter().convertLocation(location));
    }

    @Override
    default void teleportAsync(LuaLocation location) {
        PaperLib.teleportAsync(getBukkitEntity(), SpigotLuaToolkit.getAdapter().convertLocation(location));
    }

    @Override
    default void damage(double amount) {
        Entity entity = getBukkitEntity();
        if (entity instanceof Damageable) {
            ((Damageable) entity).damage(amount);
        }
    }

    @Override
    default double getHealth() {
        Entity entity = getBukkitEntity();
        if (entity instanceof Damageable) {
            return ((Damageable) entity).getHealth();
        }
        return -1;
    }

    @Override
    default boolean isDead() {
        return getBukkitEntity().isDead();
    }

    @Override
    default LuaVector getVelocity() {
        return SpigotLuaToolkit.getAdapter().convertVector(getBukkitEntity().getVelocity());
    }

    @Override
    default void setVelocity(LuaVector vector) {
        getBukkitEntity().setVelocity(SpigotLuaToolkit.getAdapter().convertVector(vector));
    }

    static PotionEffectType parseEffectType(String effectType) throws LuaError {
        PotionEffectType type = PotionEffectType.getByName(effectType);
        if (type == null) {
            try {
                Field f = PotionEffectType.class.getDeclaredField(effectType);
                if (Modifier.isStatic(f.getModifiers())) {
                    Object ob = f.get(null);
                    if (ob instanceof PotionEffectType) {
                        type = (PotionEffectType) ob;
                    }
                }
            } catch (ReflectiveOperationException ignored) { }
            if (type == null) {
                throw new LuaError("Unknown potion effect type \"" + effectType + "\"");
            }
        }
        return type;
    }

    @Override
    default boolean addPotionEffect(String effectType, int duration, int amplifier, boolean ambient, boolean particles, boolean icon) {
        Entity ent = getBukkitEntity();
        if (ent instanceof LivingEntity) {
            PotionEffect effect;
            if (PaperLib.isVersion(13)) {
                effect = new PotionEffect(parseEffectType(effectType), duration, amplifier, ambient, particles, icon);
            } else {
                effect = new PotionEffect(parseEffectType(effectType), duration, amplifier, ambient, particles);
            }
            return ((LivingEntity) ent).addPotionEffect(effect);
        }
        return false;
    }

    @Override
    default void removePotionEffect(String effectType) {
        Entity ent = getBukkitEntity();
        if (ent instanceof LivingEntity) {
            ((LivingEntity) ent).removePotionEffect(parseEffectType(effectType));
        }
    }

    @Override
    default @Nullable LuaEntityEquipment getEquipment() {
        Entity ent = getBukkitEntity();
        if (ent instanceof LivingEntity) {
            EntityEquipment equipment = ((LivingEntity) ent).getEquipment();
            if (equipment != null) return new SpigotLuaEntityEquipment(equipment);
        }
        return null;
    }

    @Override
    default boolean unsafe(String member, Object... objects) {
        Entity ent = getBukkitEntity();
        Class<? extends Entity> clazz = ent.getClass();
        boolean fieldSearch = true;
        for (Method m : clazz.getMethods()) {
            int mod = m.getModifiers();
            if (!Modifier.isPublic(mod)) continue;
            if (Modifier.isStatic(mod)) continue;
            if (m.getName().equalsIgnoreCase(member)) {
                fieldSearch = false;
                Class<?>[] param = m.getParameterTypes();
                if (param.length != objects.length) continue;
                boolean all = true;
                for (int i=0; i < param.length; i++) {
                    Object ob = objects[i];
                    if (ob == null) continue;
                    if (!param[i].isInstance(ob)) {
                        all = false;
                        break;
                    }
                }
                if (!all) continue;
                try {
                    m.invoke(ent, objects);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        if (!fieldSearch) return false;
        if (objects.length != 1) return false;
        Object ob = objects[0];
        Field f = null;
        for (Field field : clazz.getFields()) {
            int mod = field.getModifiers();
            if (!Modifier.isPublic(mod)) continue;
            if (Modifier.isStatic(mod)) continue;
            if (Modifier.isFinal(mod)) continue;
            String fName = field.getName();
            if (fName.equalsIgnoreCase(member)) {
                f = field;
                if (fName.equals(member)) break;
            }
        }
        if (f == null) return false;
        try {
            f.set(ent, ob);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

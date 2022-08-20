package codes.wasabi.xplug.platform.spigot.base;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.platform.spigot.base.command.SpigotLuaCommandSender;
import codes.wasabi.xplug.platform.spigot.base.entity.SpigotLuaEntity;
import codes.wasabi.xplug.platform.spigot.base.entity.SpigotLuaNPC;
import codes.wasabi.xplug.platform.spigot.base.entity.SpigotLuaPlayer;
import codes.wasabi.xplug.platform.spigot.base.text.SpigotLuaAudience;
import codes.wasabi.xplug.platform.spigot.base.world.SpigotLuaChunk;
import codes.wasabi.xplug.platform.spigot.base.world.SpigotLuaWorld;
import codes.wasabi.xplug.struct.command.LuaCommandSender;
import codes.wasabi.xplug.struct.command.LuaVoidCommandSender;
import codes.wasabi.xplug.struct.data.LuaLocation;
import codes.wasabi.xplug.struct.data.LuaVector;
import codes.wasabi.xplug.struct.entity.LuaEntity;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import codes.wasabi.xplug.struct.world.LuaChunk;
import codes.wasabi.xplug.struct.world.LuaWorld;
import codes.wasabi.xplug.util.LuaBridge;
import net.kyori.adventure.audience.Audience;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaValue;

import java.util.Set;
import java.util.UUID;

public abstract class SpigotLuaTypeAdapter {

    protected abstract SpigotLuaWorld createWorld(World world);

    public @NotNull LuaWorld convertWorld(World world) {
        return createWorld(world);
    }

    public World convertWorld(LuaWorld world) {
        if (world instanceof SpigotLuaWorld) {
            return ((SpigotLuaWorld) world).getBukkitWorld();
        } else {
            return Bukkit.getWorld(world.getName());
        }
    }

    public @Nullable World convertWorld(LuaValue lv) {
        String name = LuaBridge.extractName(lv);
        return Bukkit.getWorld(name);
    }

    public abstract @NotNull SpigotLuaChunk convertChunk(Chunk chunk);

    public Chunk convertChunk(LuaChunk chunk) {
        if (chunk instanceof SpigotLuaChunk) {
            return ((SpigotLuaChunk) chunk).getBukkitChunk();
        } else {
            World w = convertWorld(chunk.getWorld());
            if (w != null) {
                return w.getChunkAt(chunk.getX(), chunk.getZ());
            }
        }
        return null;
    }

    public @Nullable Chunk convertChunk(LuaValue lv) {
        World world = null;
        LuaValue getWorld = lv.get("GetWorld");
        if (getWorld.isfunction()) {
            LuaValue val = getWorld.checkfunction().call();
            world = convertWorld(val);
        }
        if (world != null) {
            int x = LuaBridge.extractInt(lv, "GetX");
            int z = LuaBridge.extractInt(lv, "GetZ");
            return world.getChunkAt(x, z);
        }
        return null;
    }

    public @NotNull LuaEntity convertEntity(@NotNull Entity e) {
        if (e instanceof Player) {
            return new SpigotLuaPlayer((Player) e);
        }
        return new SpigotLuaNPC(e);
    }

    public @Nullable Entity convertEntity(@NotNull LuaEntity e) {
        if (e instanceof SpigotLuaEntity) {
            return ((SpigotLuaEntity) e).getBukkitEntity();
        } else {
            UUID uuid = UUID.fromString(e.getUUID());
            for (World w : Bukkit.getWorlds()) {
                for (Entity ent : w.getEntities()) {
                    if (ent.getUniqueId().equals(uuid)) return ent;
                }
            }
            return null;
        }
    }

    public @Nullable Entity convertEntity(@NotNull LuaValue lv) {
        UUID uuid = LuaBridge.extractUUID(lv);
        for (World w : Bukkit.getWorlds()) {
            for (Entity ent : w.getEntities()) {
                if (ent.getUniqueId().equals(uuid)) return ent;
            }
        }
        return null;
    }

    public @NotNull LuaPlayer convertPlayer(@NotNull Player ply) {
        return new SpigotLuaPlayer(ply);
    }

    public @Nullable Player convertPlayer(@NotNull LuaEntity e) {
        if (e instanceof SpigotLuaPlayer) {
            return ((SpigotLuaPlayer) e).getBukkitPlayer();
        } else {
            UUID uuid = UUID.fromString(e.getUUID());
            return Bukkit.getPlayer(uuid);
        }
    }

    public @Nullable Player convertPlayer(@NotNull LuaValue lv) {
        UUID uuid = LuaBridge.extractUUID(lv);
        return Bukkit.getPlayer(uuid);
    }

    public @NotNull SpigotLuaAudience convertAudience(@NotNull Audience audience) {
        return new SpigotLuaAudience(audience);
    }

    public @NotNull SpigotLuaCommandSender convertCommandSender(@NotNull CommandSender sender) {
        return new SpigotLuaCommandSender(sender);
    }

    public @Nullable CommandSender convertCommandSender(@NotNull LuaCommandSender sender) {
        if (sender instanceof SpigotLuaCommandSender) {
            return ((SpigotLuaCommandSender) sender).getBukkitCommandSender();
        } else {
            if (sender.isConsole()) {
                return Bukkit.getConsoleSender();
            }
        }
        return null;
    }

    public @NotNull LuaVector convertVector(@NotNull Vector vector) {
        return new LuaVector(vector.getX(), vector.getY(), vector.getZ());
    }

    public @NotNull Vector convertVector(@NotNull LuaVector vec) {
        return new Vector(vec.getX(), vec.getY(), vec.getZ());
    }

    public @Nullable Vector convertVector(@NotNull LuaValue lv) {
        LuaVector vec = LuaVector.fromLuaValue(lv);
        if (vec == null) return null;
        return convertVector(vec);
    }

    public @NotNull LuaLocation convertLocation(@NotNull Location location) {
        return new LuaLocation(convertWorld(location.getWorld()), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public @NotNull Location convertLocation(@NotNull LuaLocation loc) {
        return new Location(convertWorld(loc.getWorld()), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public @Nullable Location convertLocation(@NotNull LuaValue lv) {
        LuaLocation loc = LuaLocation.fromLuaValue(lv);
        if (loc == null) return null;
        return convertLocation(loc);
    }

}

package codes.wasabi.xplug.platform.spigot.base;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.platform.spigot.base.block.SpigotLuaBlock;
import codes.wasabi.xplug.platform.spigot.base.command.SpigotLuaCommandSender;
import codes.wasabi.xplug.platform.spigot.base.entity.SpigotLuaEntity;
import codes.wasabi.xplug.platform.spigot.base.entity.SpigotLuaNPC;
import codes.wasabi.xplug.platform.spigot.base.entity.SpigotLuaPlayer;
import codes.wasabi.xplug.platform.spigot.base.inventory.SpigotLuaInventory;
import codes.wasabi.xplug.platform.spigot.base.inventory.SpigotLuaItemStack;
import codes.wasabi.xplug.platform.spigot.base.material.SpigotLuaMaterial;
import codes.wasabi.xplug.platform.spigot.base.text.SpigotLuaAudience;
import codes.wasabi.xplug.platform.spigot.base.world.SpigotLuaChunk;
import codes.wasabi.xplug.platform.spigot.base.world.SpigotLuaWorld;
import codes.wasabi.xplug.struct.command.LuaCommandSender;
import codes.wasabi.xplug.struct.data.LuaLocation;
import codes.wasabi.xplug.struct.data.LuaVector;
import codes.wasabi.xplug.struct.entity.LuaEntity;
import codes.wasabi.xplug.struct.inventory.LuaItemStack;
import codes.wasabi.xplug.struct.material.LuaMaterial;
import codes.wasabi.xplug.struct.world.LuaChunk;
import codes.wasabi.xplug.struct.world.LuaWorld;
import codes.wasabi.xplug.util.LuaBridge;
import net.kyori.adventure.audience.Audience;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaValue;
import xyz.wasabicodes.matlib.MaterialLib;
import xyz.wasabicodes.matlib.struct.MetaMaterial;

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
        World world = LuaBridge.extractHandle(lv, World.class);
        if (world != null) return world;
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
        Chunk chunk = LuaBridge.extractHandle(lv, Chunk.class);
        if (chunk != null) return chunk;
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
        Entity handle = LuaBridge.extractHandle(lv, Entity.class);
        if (handle != null) return handle;
        UUID uuid = LuaBridge.extractUUID(lv);
        for (World w : Bukkit.getWorlds()) {
            for (Entity ent : w.getEntities()) {
                if (ent.getUniqueId().equals(uuid)) return ent;
            }
        }
        return null;
    }

    public @NotNull SpigotLuaPlayer convertPlayer(@NotNull Player ply) {
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
        Player handle = LuaBridge.extractHandle(lv, Player.class);
        if (handle != null) return handle;
        UUID uuid = LuaBridge.extractUUID(lv);
        return Bukkit.getPlayer(uuid);
    }

    public @NotNull SpigotLuaAudience convertAudience(@NotNull Audience audience) {
        return new SpigotLuaAudience(audience);
    }

    public @NotNull SpigotLuaCommandSender convertCommandSender(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            return convertPlayer((Player) sender);
        } else {
            return new SpigotLuaCommandSender(sender);
        }
    }

    public @Nullable CommandSender convertCommandSender(@NotNull LuaCommandSender sender) {
        if (sender instanceof SpigotLuaPlayer) {
            return ((SpigotLuaPlayer) sender).getBukkitPlayer();
        } else if (sender instanceof SpigotLuaCommandSender) {
            return ((SpigotLuaCommandSender) sender).getBukkitCommandSender();
        } else {
            if (sender.isConsole()) {
                return Bukkit.getConsoleSender();
            } else if (sender.isPlayer()) {
                UUID uuid;
                try {
                    uuid = UUID.fromString(sender.toPlayer().getUUID());
                } catch (IllegalArgumentException e) {
                    return null;
                }
                return Bukkit.getPlayer(uuid);
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

    public @Nullable SpigotLuaMaterial convertMaterial(@NotNull LuaMaterial material) {
        SpigotLuaMaterial slm;
        if (material instanceof SpigotLuaMaterial) {
            slm = (SpigotLuaMaterial) material;
        } else {
            slm = SpigotLuaToolkit.getInstance().parseMaterial(material.getLuaValue(), true);
        }
        return slm;
    }

    public @NotNull SpigotLuaBlock convertBlock(@NotNull Block block) {
        return new SpigotLuaBlock(block);
    }

    public @Nullable ItemStack convertItemStack(@NotNull LuaValue luaValue) {
        if (luaValue.isnil()) return null;
        ItemStack handle = LuaBridge.extractHandle(luaValue, ItemStack.class);
        if (handle != null) return handle;
        LuaValue matFunc = luaValue.get("GetMaterial");
        LuaValue mat;
        if (matFunc.isnil()) {
            mat = LuaValue.valueOf("STONE");
        } else if (matFunc.isfunction()) {
            mat = matFunc.call();
        } else {
            mat = matFunc;
        }
        SpigotLuaMaterial material = SpigotLuaToolkit.getInstance().parseMaterial(mat, true);
        MetaMaterial mm;
        if (material != null) {
            mm = material.getMetaMaterial();
        } else {
            mm = MaterialLib.getMaterial("STONE");
        }
        int count = LuaBridge.extractInt(luaValue, "GetCount");
        ItemStack ret = new ItemStack(mm.getBukkitMaterial(), count);
        mm.apply(ret);
        return ret;
    }

    public @Nullable SpigotLuaItemStack convertItemStack(ItemStack itemStack) {
        if (itemStack == null) return null;
        return new SpigotLuaItemStack(itemStack);
    }

    public @Nullable ItemStack convertItemStack(LuaItemStack itemStack) {
        if (itemStack == null) return null;
        if (itemStack instanceof SpigotLuaItemStack) {
            return ((SpigotLuaItemStack) itemStack).getBukkitItemStack();
        } else {
            return convertItemStack(itemStack.getLuaValue());
        }
    }

    public abstract SpigotLuaInventory convertInventory(Inventory inventory);

}

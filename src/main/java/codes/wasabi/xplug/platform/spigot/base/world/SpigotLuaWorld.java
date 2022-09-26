package codes.wasabi.xplug.platform.spigot.base.world;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.platform.spigot.base.SpigotLuaTypeAdapter;
import codes.wasabi.xplug.struct.block.LuaBlock;
import codes.wasabi.xplug.struct.data.LuaLocation;
import codes.wasabi.xplug.struct.data.LuaRayTraceResult;
import codes.wasabi.xplug.struct.data.LuaVector;
import codes.wasabi.xplug.struct.entity.LuaEntity;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import codes.wasabi.xplug.struct.world.LuaChunk;
import codes.wasabi.xplug.struct.world.LuaWorld;
import codes.wasabi.xplug.util.func.GetterFunction;
import io.papermc.lib.PaperLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;

import java.util.*;
import java.util.stream.Collectors;

public abstract class SpigotLuaWorld implements LuaWorld {

    private final World bukkitWorld;

    public SpigotLuaWorld(World world) {
        this.bukkitWorld = world;
    }

    public World getBukkitWorld() {
        return bukkitWorld;
    }

    @Override
    public String getName() {
        return bukkitWorld.getName();
    }

    @Override
    public String getUUID() {
        return bukkitWorld.getUID().toString();
    }

    @Override
    public LuaChunk getChunk(int x, int z) {
        return SpigotLuaToolkit.getAdapter().convertChunk(bukkitWorld.getChunkAt(x, z));
    }

    @Override
    public Collection<LuaEntity> getEntities() {
        return bukkitWorld.getEntities().stream().map((Entity e) -> SpigotLuaToolkit.getAdapter().convertEntity(e)).collect(Collectors.toList());
    }

    @Override
    public Collection<LuaPlayer> getPlayers() {
        return bukkitWorld.getPlayers().stream().map((Player ply) -> SpigotLuaToolkit.getAdapter().convertPlayer(ply)).collect(Collectors.toList());
    }

    @Override
    public int getMaxHeight() {
        return getBukkitWorld().getMaxHeight();
    }

    @Override
    public LuaBlock getBlock(int x, int y, int z) {
        return SpigotLuaToolkit.getAdapter().convertBlock(bukkitWorld.getBlockAt(x, y, z));
    }

    @Override
    public @Nullable LuaEntity spawn(String type, LuaVector origin) {
        EntityType et;
        try {
            et = EntityType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
        Entity ent = bukkitWorld.spawnEntity(new Location(bukkitWorld, origin.getX(), origin.getY(), origin.getZ()), et);
        return SpigotLuaToolkit.getAdapter().convertEntity(ent);
    }

    private Collection<Entity> getNearbyEntities0(Location location, double x, double y, double z) {
        if (PaperLib.isVersion(8, 8)) {
            return bukkitWorld.getNearbyEntities(location, x, y, z);
        } else {
            List<Entity> list = new ArrayList<>();
            double minX = location.getX() - x;
            double maxX = location.getX() + x;
            double minY = location.getY() - y;
            double maxY = location.getY() + y;
            double minZ = location.getZ() - z;
            double maxZ = location.getZ() + z;
            for (Entity ent : bukkitWorld.getEntities()) {
                Location entLoc = ent.getLocation();
                if (entLoc.getX() < minX || entLoc.getX() > maxX) continue;
                if (entLoc.getY() < minY || entLoc.getY() > maxY) continue;
                if (entLoc.getZ() < minZ || entLoc.getZ() > maxZ) continue;
                list.add(ent);
            }
            return list;
        }
    }

    @Override
    public Collection<LuaEntity> getNearbyEntities(String type, LuaVector origin, double radius) {
        SpigotLuaTypeAdapter adapter = SpigotLuaToolkit.getAdapter();
        return getNearbyEntities0(
                new Location(bukkitWorld, origin.getX(), origin.getY(), origin.getZ()),
                radius, radius, radius
        ).stream().map(adapter::convertEntity).collect(Collectors.toList());
    }

    @Override
    public Collection<LuaPlayer> getNearbyPlayers(String type, LuaVector origin, double radius) {
        List<LuaPlayer> ret = new ArrayList<>();
        SpigotLuaTypeAdapter adapter = SpigotLuaToolkit.getAdapter();
        for (Entity ent : getNearbyEntities0(
                new Location(bukkitWorld, origin.getX(), origin.getY(), origin.getZ()),
                radius, radius, radius
        )) {
            if (ent instanceof Player) {
                ret.add(adapter.convertPlayer((Player) ent));
            }
        }
        return Collections.unmodifiableList(ret);
    }

    @Override
    public @Nullable LuaRayTraceResult raytrace(LuaLocation start, LuaVector direction, double maxDistance, boolean hitEntities, boolean hitPassableBlocks, boolean hitWater) {
        SpigotLuaTypeAdapter adapter = SpigotLuaToolkit.getAdapter();
        if (PaperLib.isVersion(13, 2)) {
            RayTraceResult res = bukkitWorld.rayTrace(
                    adapter.convertLocation(start),
                    adapter.convertVector(direction),
                    maxDistance,
                    (hitWater ? FluidCollisionMode.ALWAYS : FluidCollisionMode.NEVER),
                    (!hitPassableBlocks),
                    0.1d,
                    (Entity e) -> hitEntities
            );
            if (res == null) return null;
            Block block = res.getHitBlock();
            Entity ent = res.getHitEntity();
            return new LuaRayTraceResult(
                    adapter.convertVector(res.getHitPosition()),
                    block == null ? null : adapter.convertBlock(block),
                    ent == null ? null : adapter.convertEntity(ent)
            );
        } else {
            Location pos = adapter.convertLocation(start);
            double pc = (hitEntities ? 0.1d : 0.35d);
            Vector dir = adapter.convertVector(direction).normalize().multiply(pc);
            double dist = 0d;
            boolean hit = false;
            Block hitBlock = null;
            Entity hitEntity = null;
            EnumSet<Material> airTypes = EnumSet.of(Material.AIR);
            if (PaperLib.isVersion(13)) {
                airTypes.add(Material.CAVE_AIR);
                airTypes.add(Material.VOID_AIR);
            }
            while (dist <= maxDistance) {
                Chunk chunk = pos.getChunk();
                if (!chunk.isLoaded()) break;
                Block b = pos.getBlock();
                if (!airTypes.contains(b.getType())) {
                    boolean passable;
                    if (PaperLib.isVersion(13, 1)) {
                        passable = b.isPassable();
                    } else {
                        passable = !b.getType().isSolid();
                    }
                    if (b.isLiquid()) {
                        if (hitWater) {
                            hit = true;
                            hitBlock = b;
                            break;
                        }
                    } else if (passable) {
                        if (hitPassableBlocks) {
                            hit = true;
                            hitBlock = b;
                            break;
                        }
                    } else {
                        hit = true;
                        hitBlock = b;
                        break;
                    }
                }
                if (hitEntities) {
                    Entity found = null;
                    for (Entity ent : chunk.getEntities()) {
                        Location entLoc = ent.getLocation();
                        double hw;
                        double hh;
                        if (PaperLib.isVersion(11, 2)) {
                            hw = ent.getWidth() / 2d;
                            hh = ent.getHeight() / 2d;
                        } else {
                            hw = pc + 0.01;
                            hh = pc + 0.01;
                            if (ent instanceof LivingEntity) {
                                hh = (((LivingEntity) ent).getEyeHeight() / 2d) + (pc / 2d);
                            }
                        }
                        double minX = entLoc.getX() - hw;
                        double minY = entLoc.getY() - hh;
                        double minZ = entLoc.getZ() - hw;
                        double maxX = entLoc.getX() + hw;
                        double maxY = entLoc.getY() + hh;
                        double maxZ = entLoc.getZ() + hw;
                        if (
                                (pos.getX() >= minX && pos.getX() <= maxX)
                                && (pos.getY() >= minY && pos.getY() <= maxY)
                                && (pos.getZ() >= minZ && pos.getZ() <= maxZ)
                        ) {
                            found = ent;
                            break;
                        }
                    }
                    if (found != null) {
                        hit = true;
                        hitEntity = found;
                        break;
                    }
                }
                pos.add(dir);
                dist += pc;
            }
            if (!hit) return null;
            return new LuaRayTraceResult(
                    adapter.convertVector(pos.toVector()),
                    hitBlock == null ? null : adapter.convertBlock(hitBlock),
                    hitEntity == null ? null : adapter.convertEntity(hitEntity)
            );
        }
    }

    @Override
    public LuaTable getLuaValue() {
        LuaTable lt = LuaWorld.super.getLuaValue();
        lt.set("GetHandle", new GetterFunction(() -> new LuaUserdata(bukkitWorld)));
        return lt;
    }

}

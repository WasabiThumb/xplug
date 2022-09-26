package codes.wasabi.xplug.platform.spigot.base;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.platform.spigot.base.material.SpigotLuaMaterial;
import codes.wasabi.xplug.struct.LuaParticleTools;
import codes.wasabi.xplug.struct.material.LuaMaterial;
import codes.wasabi.xplug.struct.world.LuaWorld;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import xyz.wasabicodes.matlib.struct.MetaMaterial;
import xyz.wasabicodes.matlib.struct.applicator.block.BlockMaterialApplicator;
import xyz.wasabicodes.matlib.struct.applicator.block.DataBlockMaterialApplicator;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.PropertyType;
import xyz.xenondevs.particle.data.texture.BlockTexture;
import xyz.xenondevs.particle.data.texture.ItemTexture;

import java.awt.Color;
import java.util.function.Consumer;

public class SpigotLuaParticleTools implements LuaParticleTools {

    private ParticleBuilder builder = null;
    private ParticleEffect effect;

    @Override
    public void start(String particle, LuaWorld world, double x, double y, double z) throws IllegalArgumentException {
        effect = ParticleEffect.valueOf(particle);
        builder = new ParticleBuilder(effect, new Location(SpigotLuaToolkit.getAdapter().convertWorld(world), x, y, z));
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
    public void setMaterial(LuaMaterial mat) {
        if (builder != null) {
            SpigotLuaMaterial slm = SpigotLuaToolkit.getAdapter().convertMaterial(mat);
            if (slm == null) {
                builder.setParticleData(null);
                return;
            }
            MetaMaterial mm = slm.getMetaMaterial();
            if (effect.hasProperty(PropertyType.REQUIRES_BLOCK)) {
                Consumer<Block> applicator = mm.getBlockApplicator();
                if (applicator instanceof DataBlockMaterialApplicator) {
                    DataBlockMaterialApplicator qual = (DataBlockMaterialApplicator) applicator;
                    builder.setParticleData(new BlockTexture(qual.getMaterial(), qual.getData()));
                } else if (applicator instanceof BlockMaterialApplicator) {
                    BlockMaterialApplicator qual = (BlockMaterialApplicator) applicator;
                    builder.setParticleData(new BlockTexture(qual.getMaterial()));
                } else {
                    builder.setParticleData(new BlockTexture(mm.getBukkitMaterial()));
                }
            } else if (effect.hasProperty(PropertyType.REQUIRES_ITEM)) {
                ItemStack is = mm.createItemStack(1);
                builder.setParticleData(new ItemTexture(is));
            }
        }
    }

    @Override
    public void end() {
        if (builder != null) builder.display();
        builder = null;
    }

}

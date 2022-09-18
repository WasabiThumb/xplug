package codes.wasabi.xplug.platform.spigot.v1_9.text;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/


import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.platform.spigot.base.SpigotLuaTypeAdapter;
import codes.wasabi.xplug.platform.spigot.base.text.SpigotLuaBossBar;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import static codes.wasabi.xplug.library.enums.*;

public class SpigotLuaBossBar_1_9 extends SpigotLuaBossBar {

    private final BossBar bb;

    public SpigotLuaBossBar_1_9(String title, int color, int style, boolean createFog, boolean darkenSky, boolean bossMusic) {
        int flagCount = ((createFog ? 1 : 0) + (darkenSky ? 1 : 0) + (bossMusic ? 1 : 0));
        int i = 0;
        BarFlag[] flags = new BarFlag[flagCount];
        if (createFog) {
            flags[i] = BarFlag.CREATE_FOG;
            i++;
        }
        if (darkenSky) {
            flags[i] = BarFlag.DARKEN_SKY;
            i++;
        }
        if (bossMusic) {
            flags[i] = BarFlag.PLAY_BOSS_MUSIC;
        }
        this.bb = Bukkit.createBossBar(title, colorCodeToEnum(color), styleCodeToEnum(style), flags);
    }

    public BossBar getBukkitBossBar() {
        return bb;
    }

    public static BarColor colorCodeToEnum(int color) {
        switch (color) {
            case BC_BLUE:
                return BarColor.BLUE;
            case BC_GREEN:
                return BarColor.GREEN;
            case BC_PINK:
                return BarColor.PINK;
            case BC_PURPLE:
                return BarColor.PURPLE;
            case BC_RED:
                return BarColor.RED;
            case BC_WHITE:
                return BarColor.WHITE;
            case BC_YELLOW:
                return BarColor.YELLOW;
        }
        return BarColor.PURPLE;
    }

    public static BarStyle styleCodeToEnum(int style) {
        switch (style) {
            case BS_SIX_SEGMENT:
                return BarStyle.SEGMENTED_6;
            case BS_TEN_SEGMENT:
                return BarStyle.SEGMENTED_10;
            case BS_TWELVE_SEGMENT:
                return BarStyle.SEGMENTED_12;
            case BS_TWENTY_SEGMENT:
                return BarStyle.SEGMENTED_20;
        }
        return BarStyle.SOLID;
    }


    @Override
    public boolean isAdventure() {
        return false;
    }

    @Override
    public void setTitle(String title) {
        bb.setTitle(title);
    }

    @Override
    public String getTitle() {
        return bb.getTitle();
    }

    @Override
    public void setVisible(boolean visible) {
        bb.setVisible(visible);
    }

    @Override
    public boolean getVisible() {
        return bb.isVisible();
    }

    @Override
    public void setColor(int color) {
        bb.setColor(colorCodeToEnum(color));
    }

    @Override
    public int getColor() {
        switch (bb.getColor()) {
            case RED:
                return BC_RED;
            case BLUE:
                return BC_BLUE;
            case PINK:
                return BC_PINK;
            case GREEN:
                return BC_GREEN;
            case WHITE:
                return BC_WHITE;
            case PURPLE:
                return BC_PURPLE;
            case YELLOW:
                return BC_YELLOW;
        }
        return BC_PURPLE;
    }

    @Override
    public void setStyle(int style) {
        bb.setStyle(styleCodeToEnum(style));
    }

    @Override
    public int getStyle() {
        switch (bb.getStyle()) {
            case SEGMENTED_6:
                return BS_SIX_SEGMENT;
            case SEGMENTED_10:
                return BS_TEN_SEGMENT;
            case SEGMENTED_12:
                return BS_TWELVE_SEGMENT;
            case SEGMENTED_20:
                return BS_TWENTY_SEGMENT;
        }
        return BS_SOLID;
    }

    @Override
    public void setProgress(double progress) {
        bb.setProgress(progress);
    }

    @Override
    public double getProgress() {
        return bb.getProgress();
    }

    @Override
    public Collection<LuaPlayer> getPlayers() {
        SpigotLuaTypeAdapter adapter = SpigotLuaToolkit.getAdapter();
        return bb.getPlayers().stream().map(adapter::convertPlayer).collect(Collectors.toList());
    }

    @Override
    public void setPlayers(Collection<LuaPlayer> players) {
        SpigotLuaTypeAdapter adapter = SpigotLuaToolkit.getAdapter();
        bb.removeAll();
        players.stream().map(adapter::convertPlayer).filter(Objects::nonNull).forEach(bb::addPlayer);
    }

    @Override
    public void addPlayer(LuaPlayer player) {
        Player ply = SpigotLuaToolkit.getAdapter().convertPlayer(player);
        if (ply != null) bb.addPlayer(ply);
    }

    @Override
    public void removePlayer(LuaPlayer player) {
        Player ply = SpigotLuaToolkit.getAdapter().convertPlayer(player);
        if (ply != null) {
            bb.removePlayer(ply);
        }
    }

}

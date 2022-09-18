package codes.wasabi.xplug.platform.spigot.v1_8.text;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.platform.spigot.base.SpigotLuaTypeAdapter;
import codes.wasabi.xplug.platform.spigot.base.text.SpigotLuaBossBar;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

import static codes.wasabi.xplug.library.enums.*;

public class SpigotLuaBossBar_1_8 extends SpigotLuaBossBar {

    private final BossBar bb;

    public SpigotLuaBossBar_1_8(String title, int color, int style, boolean createFog, boolean darkenSky, boolean bossMusic) {
        Set<BossBar.Flag> flags = new HashSet<>();
        if (createFog) flags.add(BossBar.Flag.CREATE_WORLD_FOG);
        if (darkenSky) flags.add(BossBar.Flag.DARKEN_SCREEN);
        if (bossMusic) flags.add(BossBar.Flag.PLAY_BOSS_MUSIC);
        bb = BossBar.bossBar(
                MiniMessage.miniMessage().deserialize(title),
                1f,
                colorCodeToEnum(color),
                styleCodeToEnum(style),
                flags
        );
    }

    public static BossBar.Color colorCodeToEnum(int color) {
        switch (color) {
            case BC_BLUE:
                return BossBar.Color.BLUE;
            case BC_GREEN:
                return BossBar.Color.GREEN;
            case BC_PINK:
                return BossBar.Color.PINK;
            case BC_PURPLE:
                return BossBar.Color.PURPLE;
            case BC_RED:
                return BossBar.Color.RED;
            case BC_WHITE:
                return BossBar.Color.WHITE;
            case BC_YELLOW:
                return BossBar.Color.YELLOW;
        }
        return BossBar.Color.PURPLE;
    }

    public static BossBar.Overlay styleCodeToEnum(int style) {
        switch (style) {
            case BS_SIX_SEGMENT:
                return BossBar.Overlay.NOTCHED_6;
            case BS_TEN_SEGMENT:
                return BossBar.Overlay.NOTCHED_10;
            case BS_TWELVE_SEGMENT:
                return BossBar.Overlay.NOTCHED_12;
            case BS_TWENTY_SEGMENT:
                return BossBar.Overlay.NOTCHED_20;
        }
        return BossBar.Overlay.PROGRESS;
    }

    @Override
    public boolean isAdventure() {
        return true;
    }

    @Override
    public void setTitle(String title) {
        bb.name(MiniMessage.miniMessage().deserialize(title));
    }

    @Override
    public String getTitle() {
        return MiniMessage.miniMessage().serialize(bb.name());
    }

    @Override
    public void setVisible(boolean visible) {
    }

    @Override
    public boolean getVisible() {
        return true;
    }

    @Override
    public void setColor(int color) {
        bb.color(colorCodeToEnum(color));
    }

    @Override
    public int getColor() {
        switch (bb.color()) {
            case YELLOW:
                return BC_YELLOW;
            case PURPLE:
                return BC_PURPLE;
            case WHITE:
                return BC_WHITE;
            case GREEN:
                return BC_GREEN;
            case PINK:
                return BC_PINK;
            case BLUE:
                 return BC_BLUE;
            case RED:
                return BC_RED;
        }
        return BC_PURPLE;
    }

    @Override
    public void setStyle(int style) {
        bb.overlay(styleCodeToEnum(style));
    }

    @Override
    public int getStyle() {
        switch (bb.overlay()) {
            case NOTCHED_6:
                return BS_SIX_SEGMENT;
            case NOTCHED_10:
                return BS_TEN_SEGMENT;
            case NOTCHED_12:
                return BS_TWELVE_SEGMENT;
            case NOTCHED_20:
                return BS_TWENTY_SEGMENT;
        }
        return BS_SOLID;
    }

    @Override
    public void setProgress(double progress) {
        bb.progress((float) progress);
    }

    @Override
    public double getProgress() {
        return bb.progress();
    }

    private static Field barsField = null;
    private static boolean checkBarsField = true;
    private static boolean audienceHasBossBar(Audience audience, BossBar bb) {
        if (checkBarsField) {
            checkBarsField = false;
            try {
                Class<?> clazz = Class.forName("net.kyori.adventure.platform.facet.FacetAudience");
                Field f = clazz.getDeclaredField("bossBars");
                f.setAccessible(true);
                barsField = f;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (barsField == null) return false;
        try {
            Map<?, ?> map = (Map<?, ?>) barsField.get(audience);
            if (map.containsKey(bb)) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Collection<LuaPlayer> getPlayers() {
        SpigotLuaTypeAdapter adapter = SpigotLuaToolkit.getAdapter();
        List<LuaPlayer> ret = new ArrayList<>();
        for (Player ply : Bukkit.getOnlinePlayers()) {
            Audience audience = XPlug.getAdventure().player(ply);
            if (audienceHasBossBar(audience, bb)) ret.add(adapter.convertPlayer(ply));
        }
        return Collections.unmodifiableList(ret);
    }

    @Override
    public void setPlayers(Collection<LuaPlayer> players) {
        SpigotLuaTypeAdapter adapter = SpigotLuaToolkit.getAdapter();
        BukkitAudiences adventure = XPlug.getAdventure();
        for (Player ply : Bukkit.getOnlinePlayers()) {
            Audience audience = adventure.player(ply);
            audience.hideBossBar(bb);
        }
        for (LuaPlayer lp : players) {
            Player bp = adapter.convertPlayer(lp);
            if (bp != null) {
                Audience audience = adventure.player(bp);
                audience.showBossBar(bb);
            }
        }
    }

    @Override
    public void addPlayer(LuaPlayer player) {
        Player bp = SpigotLuaToolkit.getAdapter().convertPlayer(player);
        if (bp != null) XPlug.getAdventure().player(bp).showBossBar(bb);
    }

    @Override
    public void removePlayer(LuaPlayer player) {
        Player bp = SpigotLuaToolkit.getAdapter().convertPlayer(player);
        if (bp != null) {
            XPlug.getAdventure().player(bp).hideBossBar(bb);
        }
    }

}

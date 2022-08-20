package codes.wasabi.xplug.platform.spigot.base.entity;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpigotLuaPlayer extends LuaPlayer implements SpigotLuaEntity {

    private final Player entity;
    private final BukkitAudiences av = XPlug.getAdventure();
    private final MiniMessage mm = MiniMessage.miniMessage();
    public SpigotLuaPlayer(Player entity) {
        this.entity = entity;
    }

    @Override
    public Entity getBukkitEntity() {
        return entity;
    }

    public Player getBukkitPlayer() {
        return entity;
    }

    @Override
    public @NotNull String getName() {
        return entity.getName();
    }

    @Override
    public @Nullable String getDisplayName() {
        String name = entity.getDisplayName();
        Component c = LegacyComponentSerializer.legacySection().deserializeOrNull(name);
        return mm.serializeOrNull(c);
    }

    @Override
    public @Nullable String getDisplayNameStripped() {
        String name = entity.getDisplayName();
        Component c = LegacyComponentSerializer.legacySection().deserializeOrNull(name);
        return PlainTextComponentSerializer.plainText().serializeOrNull(c);
    }

    @Override
    public boolean isOp() {
        return entity.isOp();
    }

    @Override
    public boolean hasPermission(String permission) {
        return entity.isOp() || entity.hasPermission(permission);
    }

    @Override
    public void kick(@Nullable String message) {
        entity.kickPlayer(message);
    }

    @Override
    public void sendMessage(String message) {
        av.player(entity).sendMessage(mm.deserialize(message));
    }

    @Override
    public void sendActionBar(String actionBar) {
        av.player(entity).sendActionBar(mm.deserialize(actionBar));
    }

    private Sound parse(@Subst("minecraft:ui.button.click") String name) {
        Key key;
        try {
            key = Key.key(name);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException();
        }
        return Sound.sound(key, Sound.Source.MASTER, 1f, 1f);
    }

    @Override
    public void playSound(String soundName) throws IllegalArgumentException {
        av.player(entity).playSound(parse(soundName));
    }

    @Override
    public void playSound(String soundName, double x, double y, double z) throws IllegalArgumentException {
        av.player(entity).playSound(parse(soundName), x, y, z);
    }

    @Override
    public void stopSound(String soundName) throws IllegalArgumentException {
        av.player(entity).stopSound(parse(soundName));
    }

    @Override
    public void showTitle(String title, String subtitle) {
        av.player(entity).showTitle(Title.title(mm.deserialize(title), mm.deserialize(subtitle)));
    }

}

package codes.wasabi.xplug.platform.spigot.base.entity;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.platform.spigot.base.command.SpigotLuaCommandSender;
import codes.wasabi.xplug.platform.spigot.base.inventory.SpigotLuaPlayerInventory;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import codes.wasabi.xplug.util.func.GetterFunction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class SpigotLuaPlayer extends SpigotLuaCommandSender implements LuaPlayer, SpigotLuaEntity {

    private final Player entity;
    private final MiniMessage mm = MiniMessage.miniMessage();
    public SpigotLuaPlayer(Player entity) {
        super(entity);
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
    public SpigotLuaPlayerInventory getInventory() {
        return (SpigotLuaPlayerInventory) SpigotLuaToolkit.getAdapter().convertInventory(entity.getInventory());
    }

    @Override
    public int getGameMode() {
        return SpigotLuaToolkit.getAdapter().convertGameMode(entity.getGameMode());
    }

    @Override
    public void setGameMode(int mode) {
        entity.setGameMode(SpigotLuaToolkit.getAdapter().convertGameMode(mode));
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public LuaPlayer toPlayer() throws UnsupportedOperationException {
        return this;
    }

    @Override
    public LuaTable getLuaValue() {
        LuaTable lt = LuaPlayer.super.getLuaValue();
        lt.set("GetHandle", new GetterFunction(() -> LuaValue.userdataOf(entity)));
        return lt;
    }

}

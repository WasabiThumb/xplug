package codes.wasabi.xplug.platform.spigot.base.command;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.platform.spigot.base.entity.SpigotLuaPlayer;
import codes.wasabi.xplug.struct.command.LuaCommandSender;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Subst;

public class SpigotLuaCommandSender implements LuaCommandSender {

    private final CommandSender cs;
    private final Audience a;
    private final MiniMessage mm = MiniMessage.miniMessage();
    public SpigotLuaCommandSender(CommandSender cs) {
        this.cs = cs;
        this.a = XPlug.getAdventure().sender(cs);
    }

    public CommandSender getBukkitCommandSender() {
        return cs;
    }

    public Audience getBukkitAudience() {
        return a;
    }

    @Override
    public void sendMessage(String message) {
        a.sendMessage(mm.deserialize(message));
    }

    @Override
    public void sendActionBar(String actionBar) {
        a.sendActionBar(mm.deserialize(actionBar));
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
        a.playSound(parse(soundName));
    }

    @Override
    public void playSound(String soundName, double x, double y, double z) throws IllegalArgumentException {
        a.playSound(parse(soundName), x, y, z);
    }

    @Override
    public void stopSound(String soundName) throws IllegalArgumentException {
        a.stopSound(parse(soundName));
    }

    @Override
    public void showTitle(String title, String subtitle) {
        a.showTitle(Title.title(mm.deserialize(title), mm.deserialize(subtitle)));
    }

    @Override
    public boolean isConsole() {
        return (cs instanceof ConsoleCommandSender);
    }

    @Override
    public boolean isPlayer() {
        return (cs instanceof Player);
    }

    @Override
    public LuaPlayer toPlayer() throws UnsupportedOperationException {
        if (this instanceof SpigotLuaPlayer) {
            return ((SpigotLuaPlayer) this);
        }
        if (cs instanceof Player) {
            return SpigotLuaToolkit.getAdapter().convertPlayer((Player) cs);
        } else {
            throw new UnsupportedOperationException("This command sender is not a player");
        }
    }

}

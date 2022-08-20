package codes.wasabi.xplug.platform.spigot.base.text;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.text.LuaAudience;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.intellij.lang.annotations.Subst;

public class SpigotLuaAudience implements LuaAudience {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private final Audience a;

    public SpigotLuaAudience(Audience audience) {
        this.a = audience;
    }

    public Audience getAdventureAudience() {
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

}

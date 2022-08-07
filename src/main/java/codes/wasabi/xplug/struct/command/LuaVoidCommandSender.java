package codes.wasabi.xplug.struct.command;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.entity.LuaPlayer;

public class LuaVoidCommandSender extends LuaCommandSender {

    public static LuaVoidCommandSender INSTANCE = new LuaVoidCommandSender();

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public LuaPlayer toPlayer() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This is a void command sender, it does not represent a player");
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void sendActionBar(String actionBar) {

    }

    @Override
    public void playSound(String soundName) throws IllegalArgumentException {

    }

    @Override
    public void playSound(String soundName, double x, double y, double z) throws IllegalArgumentException {

    }

    @Override
    public void stopSound(String soundName) throws IllegalArgumentException {

    }

    @Override
    public void showTitle(String title, String subtitle) {

    }

}

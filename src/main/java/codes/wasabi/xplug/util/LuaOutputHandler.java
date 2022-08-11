package codes.wasabi.xplug.util;
/*
  XPlug | A LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/


import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public interface LuaOutputHandler {

    static LuaOutputHandler ofAudience(String prefix, Audience audience) {
        return new LuaOutputHandler() {
            private void base(String text, String type, TextColor typeColor, TextColor bodyColor) {
                audience.sendMessage(Component.empty()
                        .append(Component.text("[").color(NamedTextColor.WHITE))
                        .append(Component.text(prefix).color(NamedTextColor.GRAY))
                        .append(Component.text("] [").color(NamedTextColor.WHITE))
                        .append(Component.text(type).color(typeColor))
                        .append(Component.text("] ").color(NamedTextColor.WHITE))
                        .append(Component.text(text).color(bodyColor))
                );
            }

            @Override
            public void info(String text) {
                base(text, "INFO", NamedTextColor.GRAY, NamedTextColor.WHITE);
            }

            @Override
            public void warn(String text) {
                base(text, "WARN", NamedTextColor.GOLD, NamedTextColor.WHITE);
            }

            @Override
            public void error(String text) {
                base(text, "ERROR", NamedTextColor.DARK_RED, NamedTextColor.RED);
            }
        };
    }

    void info(String text);

    void warn(String text);

    void error(String text);

}

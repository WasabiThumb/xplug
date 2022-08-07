package codes.wasabi.xplug.struct.command;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaToolkit;
import codes.wasabi.xplug.struct.entity.LuaPlayer;
import codes.wasabi.xplug.struct.world.LuaWorld;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaValue;
import static codes.wasabi.xplug.library.enums.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum LuaArgumentType {
    STRING(AT_STRING, LuaValue::valueOf, Collections.singletonList("text")),
    INTEGER(AT_INTEGER, (String s) -> {
        int i;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not an integer");
        }
        return LuaValue.valueOf(i);
    }, Arrays.asList("0", "1")),
    POSITIVE_INTEGER(AT_POSITIVE_INTEGER, (String s) -> {
        int i;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not an integer");
        }
        if (i < 0) throw new IllegalArgumentException("Not a positive integer");
        return LuaValue.valueOf(i);
    }, Arrays.asList("0", "1")),
    DECIMAL(AT_DECIMAL, (String s) -> {
        double i;
        try {
            i = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a decimal");
        }
        return LuaValue.valueOf(i);
    }, Arrays.asList("0", "1")),
    POSITIVE_DECIMAL(AT_POSITIVE_DECIMAL, (String s) -> {
        double i;
        try {
            i = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a decimal");
        }
        if (i < 0) throw new IllegalArgumentException("Not a positive decimal");
        return LuaValue.valueOf(i);
    }, Arrays.asList("0", "1")),
    BOOLEAN(AT_BOOLEAN, (String s) -> {
        String lower = s.toLowerCase(Locale.ROOT);
        if (lower.equals("true")) return LuaValue.TRUE;
        if (lower.equals("false")) return LuaValue.FALSE;
        if (lower.equals("1")) return LuaValue.TRUE;
        if (lower.equals("0")) return LuaValue.FALSE;
        if (lower.equals("yes")) return LuaValue.TRUE;
        if (lower.equals("no")) return LuaValue.FALSE;
        throw new IllegalArgumentException("Not a boolean");
    }),
    PLAYER(AT_PLAYER, (String s) -> {
        LuaToolkit ltk = XPlug.getToolkit();
        LuaPlayer ignoreCase = null;
        for (LuaPlayer lp : ltk.getPlayers()) {
            String lpn = lp.getName();
            if (lpn.equals(s)) return lp.getLuaValue();
            if (lpn.equalsIgnoreCase(s)) ignoreCase = lp;
        }
        if (ignoreCase != null) return ignoreCase.getLuaValue();
        throw new IllegalArgumentException("No player with that name");
    }, () -> {
        LuaToolkit ltk = XPlug.getToolkit();
        return ltk.getPlayers().stream().map(LuaPlayer::getName).collect(Collectors.toList());
    }),
    WORLD(AT_WORLD, (String s) -> {
        LuaToolkit ltk = XPlug.getToolkit();
        LuaWorld lw = ltk.getWorld(s);
        if (lw == null) throw new IllegalArgumentException("No world with that name");
        return lw.getLuaValue();
    }, () -> {
        LuaToolkit ltk = XPlug.getToolkit();
        return ltk.getWorlds().stream().map(LuaWorld::getName).collect(Collectors.toList());
    });

    private final int num;
    private final Function<String, LuaValue> parse;
    private final Supplier<List<String>> complete;
    LuaArgumentType(int num, Function<String, LuaValue> parse, Supplier<List<String>> complete) {
        this.num = num;
        this.parse = parse;
        this.complete = complete;
    }

    LuaArgumentType(int num, Function<String, LuaValue> parse, List<String> complete) {
        this(num, parse, () -> complete);
    }

    LuaArgumentType(int num, Function<String, LuaValue> parse) {
        this(num, parse, Collections.emptyList());
    }

    public LuaValue parse(String input) throws IllegalArgumentException {
        return parse.apply(input);
    }

    public List<String> tabComplete() {
        return complete.get();
    }

    public int enumIndex() {
        return num;
    }

    private static Map<Integer, LuaArgumentType> map = null;
    public static @Nullable LuaArgumentType fromEnumIndex(int num) {
        if (map == null) {
            map = new HashMap<>();
            for (LuaArgumentType type : values()) {
                map.put(type.num, type);
            }
        }
        return map.get(num);
    }


}

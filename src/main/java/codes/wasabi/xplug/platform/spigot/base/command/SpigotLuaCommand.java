package codes.wasabi.xplug.platform.spigot.base.command;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.annotation.ConditionalOverride;
import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.struct.command.LuaArgumentType;
import codes.wasabi.xplug.struct.command.LuaCommand;
import codes.wasabi.xplug.struct.command.LuaCommandSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SpigotLuaCommand extends BukkitCommand implements LuaCommand, PluginIdentifiableCommand {

    private final LuaArgumentType[] args;
    private BiConsumer<LuaCommandSender, Varargs> executor = null;
    public SpigotLuaCommand(String name, LuaArgumentType... args) {
        super(name);
        this.setDescription("A LUA command");
        this.args = args;
        try {
            Field f;
            Server s = Bukkit.getServer();
            f = s.getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            CommandMap commandMap = (CommandMap) f.get(s);
            commandMap.register("xplug", this);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    void unregister() {
        try {
            Field f;
            Server s = Bukkit.getServer();
            f = s.getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            CommandMap commandMap = (CommandMap) f.get(s);
            unregister(commandMap);
            if (commandMap instanceof SimpleCommandMap) {
                SimpleCommandMap scm = (SimpleCommandMap) commandMap;
                Field f1 = SimpleCommandMap.class.getDeclaredField("knownCommands");
                f1.setAccessible(true);
                Map<?, ?> map = (Map<?, ?>) f1.get(scm);
                Object removeKey = null;
                for (Object key : map.keySet()) {
                    Object value = map.get(key);
                    if (value == null) continue;
                    if (value.equals(this)) {
                        removeKey = key;
                        break;
                    }
                }
                if (removeKey == null) return;
                map.remove(removeKey);
                f1.set(scm, map);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!XPlug.getInstance().isEnabled()) {
            XPlug.getAdventure().sender(sender).sendMessage(Component.text("* Can't run this LUA command, XPlug is unloaded").color(NamedTextColor.RED));
            return true;
        }
        call(SpigotLuaToolkit.getAdapter().convertCommandSender(sender), args);
        return true;
    }

    private @NotNull List<String> tabComplete(String[] args) {
        if (args.length == 0) return Collections.singletonList(getName());
        LuaArgumentType[] lArgs = this.args;
        int target = args.length - 1;
        if (target >= lArgs.length) return Collections.emptyList();
        LuaArgumentType selected = lArgs[target];
        return selected.tabComplete();
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return tabComplete(args);
    }

    @NotNull
    @ConditionalOverride(minorVersion = 10, patchVersion = 2)
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
        return tabComplete(args);
    }

    @Override
    public @NotNull String getName() {
        return super.getName();
    }

    @Override
    public @NotNull String getDescription() {
        return super.getDescription();
    }

    @Override
    public @NotNull Command setDescription(@NotNull String description) {
        super.setDescription(description);
        return this;
    }

    @Override
    public LuaArgumentType[] getArguments() {
        return Arrays.copyOf(args, args.length, LuaArgumentType[].class);
    }

    @Override
    public void call(LuaCommandSender sender, String... args) {
        if (executor == null) return;
        int argLen = this.args.length;
        LuaValue[] values = new LuaValue[argLen];
        for (int i=0; i < argLen; i++) {
            LuaArgumentType type = this.args[i];
            if (i >= args.length) {
                values[i] = LuaValue.NIL;
                continue;
            }
            try {
                values[i] = type.parse(args[i]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("<dark_red>Error parsing argument #" + (i + 1) + ":</dark_red> <red>" + e.getMessage() + "</red>");
                return;
            }
        }
        Varargs varargs = LuaValue.varargsOf(values);
        try {
            executor.accept(sender, varargs);
        } catch (LuaError e) {
            sender.sendMessage("<dark_red>A LUA error occured while executing your command:</dark_red> <red>" + e.getMessage() + "</red>");
        }
    }

    @Override
    public void setExecutor(@Nullable Consumer<Varargs> executor) {
        if (executor == null) {
            this.executor = null;
            return;
        }
        this.executor = (luaCommandSender, varargs) -> executor.accept(LuaValue.varargsOf(luaCommandSender.getLuaValue(), varargs));
    }

    public void setExecutor(@Nullable BiConsumer<LuaCommandSender, Varargs> executor) {
        this.executor = executor;
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return XPlug.getInstance();
    }

}

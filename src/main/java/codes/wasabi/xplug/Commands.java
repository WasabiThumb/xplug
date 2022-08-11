package codes.wasabi.xplug;
/*
  XPlug | A LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.util.LuaOutputHandler;
import codes.wasabi.xplug.util.LuaSandbox;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Executors;

public class Commands implements CommandExecutor, TabCompleter {

    public void register() {
        PluginCommand cmd = Bukkit.getPluginCommand("xplug");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
    }

    private final DecimalFormat format = new DecimalFormat("0.##");
    private int execIndex = 0;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Audience a = XPlug.getAdventure().sender(sender);
        String sub = "help";
        if (args.length >= 1) {
            sub = args[0].toLowerCase(Locale.ROOT);
        }
        switch (sub) {
            case "help":
                a.sendMessage(Component.empty()
                        .append(Component.text("= ").color(NamedTextColor.DARK_AQUA))
                        .append(Component.text("XPlug Help").color(NamedTextColor.AQUA))
                        .append(Component.text(" =").color(NamedTextColor.DARK_AQUA))
                        .append(Component.newline())
                        .append(Component.text("help").color(NamedTextColor.DARK_PURPLE))
                        .append(Component.text(" :: ").color(NamedTextColor.GOLD))
                        .append(Component.text("Provides a list of subcommands for XPlug").color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.newline())
                        .append(Component.text("info").color(NamedTextColor.DARK_PURPLE))
                        .append(Component.text(" :: ").color(NamedTextColor.GOLD))
                        .append(Component.text("Prints out basic info about XPlug").color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.newline())
                        .append(Component.text("run").color(NamedTextColor.DARK_PURPLE))
                        .append(Component.text(" :: ").color(NamedTextColor.GOLD))
                        .append(Component.text("Runs a LUA file from the scripts folder").color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.newline())
                        .append(Component.text("exec").color(NamedTextColor.DARK_PURPLE))
                        .append(Component.text(" :: ").color(NamedTextColor.GOLD))
                        .append(Component.text("Runs a given line of LUA code").color(NamedTextColor.LIGHT_PURPLE))
                );
                break;
            case "info":
                PluginDescriptionFile desc = XPlug.getInstance().getDescription();
                String mcVer = "1." + PaperLib.getMinecraftVersion() + "." + PaperLib.getMinecraftPatchVersion();
                String apiVersion = null;
                // getApiVersion was added in 1.13
                if (PaperLib.isVersion(13)) {
                    Class<?> clazz = desc.getClass();
                    try {
                        Method m = clazz.getMethod("getAPIVersion");
                        Object out = m.invoke(desc);
                        if (out != null) {
                            if (out instanceof String) {
                                apiVersion = (String) out;
                            }
                        }
                    } catch (ReflectiveOperationException ignored) { }
                }
                Component apiVersionComponent;
                if (apiVersion == null) {
                    apiVersionComponent = Component.empty()
                            .append(Component.text(mcVer).color(NamedTextColor.LIGHT_PURPLE))
                            .append(Component.text(" (?)").color(NamedTextColor.DARK_GRAY));
                } else {
                    apiVersionComponent = Component.text(apiVersion).color(NamedTextColor.LIGHT_PURPLE);
                }
                a.sendMessage(Component.empty()
                        .append(Component.text("= ").color(NamedTextColor.DARK_AQUA))
                        .append(Component.text("XPlug Info").color(NamedTextColor.AQUA))
                        .append(Component.text(" =").color(NamedTextColor.DARK_AQUA))
                        .append(Component.newline())
                        .append(Component.text("Version").color(NamedTextColor.DARK_PURPLE))
                        .append(Component.text(" :: ").color(NamedTextColor.GOLD))
                        .append(Component.text(desc.getVersion()).color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.newline())
                        .append(Component.text("API Version").color(NamedTextColor.DARK_PURPLE))
                        .append(Component.text(" :: ").color(NamedTextColor.GOLD))
                        .append(apiVersionComponent)
                        .append(Component.newline())
                        .append(Component.text("Minecraft Version").color(NamedTextColor.DARK_PURPLE))
                        .append(Component.text(" :: ").color(NamedTextColor.GOLD))
                        .append(Component.text(mcVer).color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.newline())
                        .append(Component.text("Toolkit").color(NamedTextColor.DARK_PURPLE))
                        .append(Component.text(" :: ").color(NamedTextColor.GOLD))
                        .append(Component.text(XPlug.getToolkit().getClass().getSimpleName()).color(NamedTextColor.LIGHT_PURPLE))
                );
                break;
            case "run":
                if (args.length == 2) {
                    String fn = args[1];
                    File sf = XPlug.getScriptFolder();
                    File target = new File(sf, fn);
                    String projectName = fn;
                    if (!target.exists()) {
                        boolean exists = false;
                        if (!fn.toLowerCase(Locale.ROOT).endsWith(".lua")) {
                            target = new File(sf, fn + ".lua");
                            exists = target.isFile();
                        }
                        if (!exists) {
                            a.sendMessage(Component.text("* File does not exist").color(NamedTextColor.RED));
                            break;
                        } else {
                            projectName = target.getName().replaceFirst("\\.lua$", "");
                        }
                    }
                    try {
                        if (!FileUtils.directoryContains(sf, target)) {
                            a.sendMessage(Component.text("* File is not contained in the scripts folder").color(NamedTextColor.RED));
                            break;
                        }
                    } catch (Exception e) {
                        a.sendMessage(Component.text("* Failed to verify if this file is contained in the scripts folder").color(NamedTextColor.RED));
                        break;
                    }
                    if (target.isDirectory()) {
                        projectName = target.getName();
                        target = new File(target, "index.lua");
                        if (!target.isFile()) {
                            a.sendMessage(Component.text("* Package does not contain an index.lua").color(NamedTextColor.RED));
                            break;
                        }
                    }
                    a.sendMessage(Component.text("* Loading script...").color(NamedTextColor.GREEN));
                    final File src = target;
                    final LuaSandbox sandbox = XPlug.getSandbox();
                    final String finalProjectName = sandbox.resolveProjectName(projectName);
                    Executors.newSingleThreadExecutor().execute(() -> {
                        BigDecimal bytes = new BigDecimal(FileUtils.sizeOfAsBigInteger(src));
                        BigDecimal readBytes = BigDecimal.ZERO;
                        BigDecimal tenThousand = BigDecimal.valueOf(10000L);
                        StringBuilder sb = new StringBuilder();
                        byte[] buffer = new byte[64];
                        try (FileInputStream fis = new FileInputStream(src)) {
                            int count;
                            while ((count = fis.read(buffer)) != -1) {
                                if (count == buffer.length) {
                                    sb.append(new String(buffer, StandardCharsets.UTF_8));
                                } else {
                                    byte[] trim = new byte[count];
                                    System.arraycopy(buffer, 0, trim, 0, count);
                                    sb.append(new String(trim, StandardCharsets.UTF_8));
                                }
                                readBytes = readBytes.add(BigDecimal.valueOf(count));
                                double pc = readBytes.multiply(tenThousand).divide(bytes, RoundingMode.HALF_DOWN).doubleValue() / 100d;
                                a.sendMessage(Component.text("* " + format.format(pc) + "%").color(NamedTextColor.GREEN));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            a.sendMessage(Component.text("* Failed, see console for details").color(NamedTextColor.RED));
                        }
                        a.sendMessage(Component.text("* Running").color(NamedTextColor.GREEN));
                        String script = sb.toString();
                        Bukkit.getScheduler().runTask(XPlug.getInstance(), () -> sandbox.run(script, finalProjectName, LuaOutputHandler.ofAudience(finalProjectName, a)));
                    });
                } else if (args.length > 2) {
                    a.sendMessage(Component.text("* Too many arguments!").color(NamedTextColor.RED));
                } else {
                    a.sendMessage(Component.text("* This command requires a LUA script to run").color(NamedTextColor.RED));
                }
                break;
            case "exec":
                StringBuilder sb = new StringBuilder();
                for (int i=1; i < args.length; i++) {
                    if (i > 1) sb.append(" ");
                    sb.append(args[i]);
                }
                String run = sb.toString();
                a.sendMessage(Component.text(run).color(NamedTextColor.GRAY));
                final LuaSandbox sandbox = XPlug.getSandbox();
                Bukkit.getScheduler().runTask(XPlug.getInstance(), () -> {
                    execIndex += 1;
                    String projectName = sandbox.resolveProjectName("exec-" + execIndex);
                    sandbox.run(run, projectName, LuaOutputHandler.ofAudience(projectName, a));
                });
                break;
            default:
                a.sendMessage(Component.empty()
                        .append(Component.text("* Unknown subcommand ").color(NamedTextColor.RED))
                        .append(Component.text(sub).color(NamedTextColor.YELLOW))
                );
                break;
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) return Arrays.asList("xplug", "xp");
        if (args.length == 1) return Arrays.asList("help", "info", "run", "exec");
        String sub = args[0];
        if (args.length == 2) {
            if (sub.equalsIgnoreCase("run")) {
                File f = XPlug.getScriptFolder();
                String[] dir = f.list(new OrFileFilter(new SuffixFileFilter(".lua"), DirectoryFileFilter.INSTANCE));
                if (dir == null) return null;
                List<String> list = Arrays.asList(dir);
                List<String> out = new ArrayList<>();
                for (String s : list) {
                    if (s.endsWith(".lua")) {
                        String trimmed = s.substring(0, s.length() - 4);
                        if (!list.contains(trimmed)) {
                            out.add(trimmed);
                            continue;
                        }
                    }
                    out.add(s);
                }
                return out;
            }
        }
        return null;
    }

}

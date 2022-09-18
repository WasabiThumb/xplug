package codes.wasabi.xplug;
/*
  XPlug | A LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.struct.LuaToolkit;
import codes.wasabi.xplug.util.LuaOutputHandler;
import codes.wasabi.xplug.util.LuaSandbox;
import codes.wasabi.xplug.util.ResourceUtil;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bukkit.plugin.java.JavaPlugin;
import org.luaj.vm2.LuaValue;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class XPlug extends JavaPlugin {

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        adventure = BukkitAudiences.create(this);
        logger.log(Level.INFO, "Preparing config");
        config = new Config(this);
        logger.log(Level.INFO, "Loading toolkit");
        cacheToolkit();
        logger.log(Level.INFO, "Preparing script directory");
        cacheScriptFolder();
        logger.log(Level.INFO, "Investigating autorun directory");
        cacheAutorunFolder();
        logger.log(Level.INFO, "Registering commands");
        registerCommands();
        logger.log(Level.INFO, "Done");
    }

    @Override
    public void onDisable() {
        toolkit.getEvents().runHook("Disable", LuaValue.varargsOf(new LuaValue[0]));
    }

    private static BukkitAudiences adventure;
    private static XPlug instance;
    private static Logger logger;
    private static LuaToolkit toolkit;
    private static LuaSandbox sandbox;
    private static File scriptFolder;
    private static File autorunFolder;
    private static Commands cmd;
    private static Config config;

    public static Config getMainConfig() { return config; }

    public static BukkitAudiences getAdventure() {
        return adventure;
    }

    public static XPlug getInstance() {
        return instance;
    }

    public static LuaToolkit getToolkit() {
        return toolkit;
    }

    public static LuaSandbox getSandbox() {
        return sandbox;
    }

    public static File getScriptFolder() {
        return scriptFolder;
    }

    public static File getAutorunFolder() {
        return autorunFolder;
    }

    public static Commands getCommandHandler() {
        return cmd;
    }

    private void cacheToolkit() {
        if (PaperLib.isVersion(17)) {
            toolkit = new codes.wasabi.xplug.platform.spigot.v1_17.SpigotLuaToolkit_1_17();
        } else if (PaperLib.isVersion(13, 2)) {
            toolkit = new codes.wasabi.xplug.platform.spigot.v1_13_2.SpigotLuaToolkit_1_13_2();
        } else if (PaperLib.isVersion(9)) {
            toolkit = new codes.wasabi.xplug.platform.spigot.v1_9.SpigotLuaToolkit_1_9();
        } else {
            toolkit = new codes.wasabi.xplug.platform.spigot.v1_8.SpigotLuaToolkit_1_8();
        }
        sandbox = new LuaSandbox();
    }

    private void cacheScriptFolder() {
        scriptFolder = new File(getDataFolder(), "scripts");
        boolean newlyCreated = false;
        if (scriptFolder.exists()) {
            if (!scriptFolder.isDirectory()) {
                try {
                    FileUtils.forceDelete(scriptFolder);
                    if (scriptFolder.mkdir()) newlyCreated = true;
                } catch (Exception ignored) { }
            }
        } else {
            if (scriptFolder.mkdir()) {
                newlyCreated = true;
            } else {
                logger.log(Level.WARNING, "Failed to make script folder");
            }
        }
        if (newlyCreated) {
            Set<String> files = ResourceUtil.dir("examples");
            for (String fn : files) {
                if (!fn.endsWith(".lua")) continue;
                File exampleFile = new File(scriptFolder, fn);
                boolean ready = true;
                if (!exampleFile.exists()) {
                    try {
                        ready = exampleFile.createNewFile();
                    } catch (IOException e) {
                        ready = false;
                    }
                }
                if (ready) {
                    try (FileOutputStream fos = new FileOutputStream(exampleFile)) {
                        try (InputStream is = Objects.requireNonNull(getResource("examples/" + fn))) {
                            IOUtils.copy(is, fos);
                        }
                        fos.flush();
                    } catch (IOException | SecurityException ignored) { }
                }
            }
        }
    }

    private void cacheAutorunFolder() {
        autorunFolder = new File(getDataFolder(), "autorun");
        boolean tryCreate = true;
        if (autorunFolder.exists()) {
            tryCreate = false;
            if (!autorunFolder.isDirectory()) {
                try {
                    FileUtils.forceDelete(autorunFolder);
                } catch (Exception ignored) { }
            }
        }
        if (tryCreate) {
            if (!autorunFolder.mkdir()) {
                logger.log(Level.WARNING, "Failed to create autorun folder");
            }
        } else {
            String[] dir = autorunFolder.list(new OrFileFilter(new SuffixFileFilter(".lua"), DirectoryFileFilter.INSTANCE));
            if (dir == null) return;
            if (dir.length > 0) {
                logger.log(Level.INFO, "Found " + dir.length + " scripts in autorun");
                for (String s : dir) {
                    File f = new File(autorunFolder, s);
                    if (f.isDirectory()) {
                        f = new File(f, "index.lua");
                        if (!f.exists()) {
                            logger.log(Level.INFO, "No index.lua exists for autorun package " + s);
                            continue;
                        }
                    }
                    String projectName = sandbox.resolveProjectName(f.getName().replaceFirst("\\.lua$", ""));
                    logger.log(Level.INFO, "Loading project " + projectName);
                    StringBuilder sb = new StringBuilder();
                    byte[] buffer = new byte[8192];
                    try (FileInputStream fis = new FileInputStream(f)) {
                        int read;
                        while ((read = fis.read(buffer)) > 0) {
                            byte[] bytes = new byte[read];
                            System.arraycopy(buffer, 0, bytes, 0, read);
                            sb.append(new String(bytes, StandardCharsets.UTF_8));
                        }
                    } catch (IOException ioException) {
                        logger.log(Level.WARNING, "IO Exception while reading project " + projectName + ", see details below");
                        ioException.printStackTrace();
                    }
                    logger.log(Level.INFO, "Running project " + projectName);
                    String code = sb.toString();
                    sandbox.run(code, projectName, LuaOutputHandler.ofAudience(projectName, adventure.console()));
                }
            }
        }
    }

    private void registerCommands() {
        cmd = new Commands();
        cmd.register();
    }

}

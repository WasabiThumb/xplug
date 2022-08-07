package codes.wasabi.xplug;
/*
  XPlug | A LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.library.*;
import codes.wasabi.xplug.struct.LuaToolkit;
import codes.wasabi.xplug.util.ResourceUtil;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        saveDefaultConfig();
        logger.log(Level.INFO, "Loading toolkit");
        cacheToolkit();
        logger.log(Level.INFO, "Preparing script directory");
        cacheScriptFolder();
        logger.log(Level.INFO, "Registering commands");
        registerCommands();
        logger.log(Level.INFO, "Done");
    }

    @Override
    public void onDisable() {

    }

    private static BukkitAudiences adventure;
    private static XPlug instance;
    private static Logger logger;
    private static LuaToolkit toolkit;
    private static Globals env;
    private static File scriptFolder;
    private static Commands cmd;

    public static BukkitAudiences getAdventure() {
        return adventure;
    }

    public static XPlug getInstance() {
        return instance;
    }

    public static LuaToolkit getToolkit() {
        return toolkit;
    }

    public static Globals getEnvironment() {
        return env;
    }

    public static File getScriptFolder() {
        return scriptFolder;
    }

    public static Commands getCommandHandler() {
        return cmd;
    }

    private void cacheToolkit() {
        if (PaperLib.isVersion(17)) {
            toolkit = new codes.wasabi.xplug.platform.spigot.v1_17.SpigotLuaToolkit_1_17();
        } else if (PaperLib.isVersion(13, 2)) {
            toolkit = new codes.wasabi.xplug.platform.spigot.v1_13_2.SpigotLuaToolkit_1_13_2();
        } else {
            toolkit = new codes.wasabi.xplug.platform.spigot.v1_8.SpigotLuaToolkit_1_8();
        }
        env = JsePlatform.standardGlobals();
        // load all non-standard libraries
        env.load(new server());
        env.load(new particle());
        env.load(new timer());
        env.load(new http());
        env.load(new util());
        env.load(new enums());
        env.load(new hook());
        env.load(new command());
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

    private void registerCommands() {
        cmd = new Commands();
        cmd.register();
    }

}

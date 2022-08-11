package codes.wasabi.xplug;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class Config {

    private final XPlug plugin;
    private final FileConfiguration cfg;
    Config(XPlug xPlug) {
        plugin = xPlug;
        cfg = xPlug.getConfig();
        load();
    }

    private final Map<String, Object> map = new HashMap<>();

    public Object get(String key) {
        return map.get(key);
    }

    public boolean getBoolean(String key, boolean def) {
        Object ob = get(key);
        if (ob == null) return def;
        return (ob instanceof Boolean ? ((Boolean) ob) : def);
    }

    public void set(String key, Object value) {
        map.put(key, value);
    }

    public void load() {
        plugin.saveDefaultConfig();
        for (String k : cfg.getKeys(true)) {
            map.put(k, cfg.get(k));
        }
    }

    public void save() {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            cfg.set(entry.getKey(), entry.getValue());
        }
        plugin.saveConfig();
    }

    public boolean getDebugMode() {
        return getBoolean("debug", false);
    }

    public void setDebugMode(boolean debug) {
        set("debug", debug);
    }

    public boolean getLogNil() {
        return getBoolean("logNil", false);
    }

    public void setLogNil(boolean logNil) {
        set("logNil", logNil);
    }

}

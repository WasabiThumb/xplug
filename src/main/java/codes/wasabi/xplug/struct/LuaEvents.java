package codes.wasabi.xplug.struct;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LuaEvents {

    private final Map<String, Map<String, Hook>> hooks = new HashMap<>();

    public LuaEvents() {

    }

    private static class Hook {
        private final String identifier;
        private final LuaFunction function;
        private final Priority priority;
        Hook(String identifier, LuaFunction function, Priority priority) {
            this.identifier = identifier;
            this.function = function;
            this.priority = priority;
        }

        String getIdentifier() {
            return identifier;
        }

        LuaFunction getFunction() {
            return function;
        }

        Priority getPriority() {
            return priority;
        }

        @Override
        public int hashCode() {
            return Objects.hash(function);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj instanceof Hook) {
                Hook other = (Hook) obj;
                if (!other.identifier.equals(identifier)) return false;
                return other.function == function;
            }
            return super.equals(obj);
        }
    }

    public enum Priority {
        HIGHEST, HIGH, NORMAL, LOW, LOWEST, MONITOR;

        public static final List<Priority> ORDER = Arrays.asList(LOWEST, LOW, NORMAL, HIGH, HIGHEST, MONITOR);
    }

    public void addHook(String eventName, String identifier, LuaFunction function, @Nullable Priority priority) {
        if (priority == null) priority = Priority.NORMAL;
        Map<String, Hook> hookMap = hooks.get(eventName);
        if (hookMap == null) hookMap = new HashMap<>();
        hookMap.put(identifier, new Hook(identifier, function, priority));
        hooks.put(eventName, hookMap);
    }

    public void removeHook(String eventName, String identifier) {
        Map<String, Hook> hookMap = hooks.get(eventName);
        if (hookMap == null) return;
        hookMap.remove(identifier);
        if (hookMap.size() == 0) {
            hooks.remove(eventName);
        } else {
            hooks.put(eventName, hookMap);
        }
    }

    public LuaTable getTable() {
        LuaTable ret = LuaValue.tableOf();
        for (Map.Entry<String, Map<String, Hook>> entry : hooks.entrySet()) {
            String key = entry.getKey();
            LuaTable table = LuaValue.tableOf();
            //
            for (Map.Entry<String, Hook> entry1 : entry.getValue().entrySet()) {
                table.set(entry1.getKey(), entry1.getValue().getFunction());
            }
            //
            ret.set(key, table);
        }
        return ret;
    }

    public @Nullable Boolean runHook(String eventName, Varargs args) {
        Map<String, Hook> hookMap = hooks.get(eventName);
        if (hookMap == null) return null;
        if (hookMap.size() == 0) return null;
        Map<Priority, List<Hook>> map = new HashMap<>();
        for (Hook h : hookMap.values()) {
            Priority p = h.getPriority();
            List<Hook> others = map.get(p);
            if (others == null) others = new ArrayList<>();
            others.add(h);
            map.put(p, others);
        }
        //
        for (Priority p : Priority.ORDER) {
            if (!map.containsKey(p)) continue;
            List<Hook> toRun = map.get(p);
            Logger logger = XPlug.getInstance().getLogger();
            for (Hook h : toRun) {
                LuaFunction lf = h.getFunction();
                LuaValue ret = LuaValue.NIL;
                try {
                    Varargs var = lf.invoke(args);
                    if (var.narg() > 0) ret = var.arg(1);
                } catch (LuaError e) {
                    logger.log(Level.WARNING, "LUA Error in event callback " + eventName + " with identifier " + h.getIdentifier() + ", see details below");
                    e.printStackTrace();
                }
                if (!ret.isnil()) {
                    return ret.toboolean();
                }
            }
        }
        return null;
    }

}

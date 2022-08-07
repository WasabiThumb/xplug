package codes.wasabi.xplug.platform.spigot.base;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaTimers;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpigotLuaTimers implements LuaTimers {

    private final XPlug instance = XPlug.getInstance();
    private final Logger logger = instance.getLogger();
    private final BukkitScheduler sch = Bukkit.getScheduler();

    @Override
    public void simple(double delay, LuaFunction lf) {
        long end = System.currentTimeMillis() + ((long) (delay * 1000d));
        AtomicReference<BukkitTask> atomic = new AtomicReference<>();
        atomic.set(sch.runTaskTimer(instance, () -> {
            long now = System.currentTimeMillis();
            if (now >= end) {
                try {
                    lf.call();
                } catch (LuaError le) {
                    logger.log(Level.WARNING, "LUA Error in simple timer, see details below");
                    le.printStackTrace();
                } catch (Throwable ignored) { }
                BukkitTask bt = atomic.get();
                if (bt != null) bt.cancel();
            }
        }, 0L, 1L));
    }

    private static class TimerData {
        double delay;
        long lastTick = System.currentTimeMillis();
        int totalReps;
        int completedReps = 0;
        LuaFunction callback;
        boolean paused = false;
    }

    private final Map<String, TimerData> timers = new HashMap<>();

    private boolean timerTaskRunning = false;
    private BukkitTask timerTask;

    private void updateTimerTask() {
        boolean shouldRun = timers.values().stream().anyMatch((TimerData td) -> !td.paused);
        if (shouldRun) {
            if (!timerTaskRunning) {
                timerTask = sch.runTaskTimer(instance, () -> {
                    long now = System.currentTimeMillis();
                    for (Map.Entry<String, TimerData> entry : Collections.unmodifiableMap(timers).entrySet()) {
                        String id = entry.getKey();
                        TimerData td = entry.getValue();
                        if (td.paused) continue;
                        if (td.totalReps > 0 && td.completedReps >= td.totalReps) {
                            remove(id);
                            continue;
                        }
                        long elapsedSince = now - td.lastTick;
                        int reps = 1;
                        if (td.delay > 0d) {
                            reps = (int) Math.floor((elapsedSince / 1000d) / td.delay);
                        }
                        if (reps > 0) {
                            td.completedReps += reps;
                            td.lastTick = now;
                            try {
                                td.callback.call();
                            } catch (LuaError le) {
                                logger.log(Level.WARNING, "LUA Error in timer \"" + id + "\", see details below");
                                le.printStackTrace();
                            } catch (Throwable ignored) { }
                        }
                    }
                }, 0L, 1L);
                timerTaskRunning = true;
            }
        } else if (timerTaskRunning) {
            timerTask.cancel();
            timerTask = null;
            timerTaskRunning = false;
        }
    }

    @Override
    public void create(String identifier, double delay, int repetitions, LuaFunction lf) {
        TimerData td = new TimerData();
        td.delay = Math.max(delay, 0d);
        td.totalReps = repetitions;
        td.callback = lf;
        timers.put(identifier, td);
        updateTimerTask();
    }

    @Override
    public boolean adjust(String identifier, double delay, @Nullable Integer repetitions, @Nullable LuaFunction lf) {
        TimerData td = timers.get(identifier);
        if (td != null) {
            td.delay = Math.max(delay, 0d);
            if (repetitions != null) td.totalReps = repetitions;
            if (lf != null) td.callback = lf;
            return true;
        }
        return false;
    }

    @Override
    public boolean exists(String identifier) {
        return timers.containsKey(identifier);
    }

    @Override
    public boolean pause(String identifier) {
        TimerData td = timers.get(identifier);
        if (td != null) {
            if (td.paused) return false;
            td.paused = true;
            updateTimerTask();
            return true;
        }
        return false;
    }

    @Override
    public void remove(String identifier) {
        timers.remove(identifier);
        updateTimerTask();
    }

    @Override
    public int repsLeft(String identifier) {
        TimerData td = timers.get(identifier);
        if (td != null) {
            if (td.totalReps > 0) {
                return td.totalReps - td.completedReps;
            } else {
                return Integer.MAX_VALUE;
            }
        }
        return 0;
    }

    @Override
    public boolean start(String identifier) {
        TimerData td = timers.get(identifier);
        if (td != null) {
            td.completedReps = 0;
            td.lastTick = System.currentTimeMillis();
            td.paused = false;
            updateTimerTask();
            return true;
        }
        return false;
    }

    @Override
    public boolean stop(String identifier) {
        TimerData td = timers.get(identifier);
        if (td != null) {
            td.completedReps = 0;
            td.lastTick = System.currentTimeMillis();
            td.paused = true;
            updateTimerTask();
            return true;
        }
        return false;
    }

    @Override
    public double timeLeft(String identifier) {
        TimerData td = timers.get(identifier);
        int repsLeft;
        double delay;
        if (td != null) {
            delay = td.delay;
            if (td.totalReps > 0) {
                repsLeft = td.totalReps - td.completedReps;
            } else {
                return Double.MAX_VALUE;
            }
        } else {
            return 0;
        }
        double offset = 0d;
        if (!td.paused) {
            offset = (System.currentTimeMillis() - td.lastTick) / 1000d;
        }
        double ret;
        if (delay > 0d) {
            ret = repsLeft * delay;
        } else {
            ret = repsLeft * (1 / 20d);
        }
        return Math.max(ret - offset, 0d);
    }

    @Override
    public boolean toggle(String identifier) {
        TimerData td = timers.get(identifier);
        if (td != null) {
            boolean paused = !td.paused;
            td.paused = paused;
            if (!paused) td.lastTick = System.currentTimeMillis();
            updateTimerTask();
            return paused;
        }
        return false;
    }

    @Override
    public boolean unpause(String identifier) {
        TimerData td = timers.get(identifier);
        if (td != null) {
            if (!td.paused) return false;
            td.paused = false;
            td.lastTick = System.currentTimeMillis();
            updateTimerTask();
            return true;
        }
        return false;
    }

}

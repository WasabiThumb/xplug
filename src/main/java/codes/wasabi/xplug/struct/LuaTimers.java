package codes.wasabi.xplug.struct;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaFunction;

public interface LuaTimers {

    void simple(double delay, LuaFunction lf);

    void create(String identifier, double delay, int repetitions, LuaFunction lf);

    boolean adjust(String identifier, double delay, @Nullable Integer repetitions, @Nullable LuaFunction lf);

    boolean exists(String identifier);

    boolean pause(String identifier);

    void remove(String identifier);

    int repsLeft(String identifier);

    boolean start(String identifier);

    boolean stop(String identifier);

    double timeLeft(String identifier);

    boolean toggle(String identifier);

    boolean unpause(String identifier);

}

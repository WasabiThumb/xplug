package codes.wasabi.xplug.struct;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import org.luaj.vm2.LuaValue;

public interface LuaValueHolder {

    /**
     * Turns this structure into a LuaValue
     * @return A LuaValue representing this structure
     */
    LuaValue getLuaValue();

}

package codes.wasabi.xplug.annotation;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import java.lang.annotation.*;

/**
 * Annotates a method that only overrides a superclass method after and including the specified minecraft version
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface ConditionalOverride {
    int minorVersion();
    int patchVersion() default 0;
}

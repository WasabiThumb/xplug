package codes.wasabi.xplug.util;
/*
  XPlug | A LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ResourceUtil {

    private static File jarFile = null;

    private static File getJarFile() {
        if (jarFile == null) {
            try {
                CodeSource cs = XPlug.class.getProtectionDomain().getCodeSource();
                URL location = cs.getLocation();
                if (location != null) {
                    jarFile = new File(location.toURI());
                } else {
                    String path = Objects.requireNonNull(XPlug.class.getResource("XPlug.class")).getPath();
                    String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
                    jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
                    jarFile = new File(jarFilePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jarFile;
    }

    public static Set<String> dir(String path) {
        if (!path.endsWith("/")) path += "/";
        int lenPath = path.length();
        File jarFile = getJarFile();
        Set<String> found = new HashSet<>();
        try (FileInputStream fis = new FileInputStream(jarFile)) {
            try (ZipInputStream zis = new ZipInputStream(fis)) {
                ZipEntry ze;
                while ((ze = zis.getNextEntry()) != null) {
                    String name = ze.getName();
                    if (name.startsWith(path) && name.length() > lenPath) {
                        String sub = name.substring(lenPath);
                        if (sub.equals(".") || sub.equals("..")) continue;
                        int io = sub.indexOf("/");
                        if (io != -1) sub = sub.substring(0, io);
                        found.add(sub);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return found;
    }

}

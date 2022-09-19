package codes.wasabi.xplug.util;
/*
  XPlug | A LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/


import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.library.*;
import static codes.wasabi.xplug.library.enums.*;

import codes.wasabi.xplug.util.func.NilFunction;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.lang.ref.SoftReference;
import java.util.*;

public class LuaSandbox {

    public LuaSandbox() {
    }

    public String resolveProjectName(String target) {
        int offset = 0;
        String _try = target;
        while (projectNames.contains(_try)) {
            offset++;
            _try = target + "-" + offset;
        }
        return _try;
    }

    public void run(String code, String projectName, LuaOutputHandler out) {
        Globals g = createGlobals(projectName, out);
        LuaValue lv;
        try {
            lv = g.load(code);
        } catch (LuaError e) {
            out.error("LUA Compilation Error: " + e.getMessage());
            return;
        }
        LuaValue ret;
        try {
            ret = lv.call();
        } catch (LuaError e) {
            out.error("LUA Error: " + e.getMessage());
            return;
        }
        if (ret.isnil() && (!XPlug.getMainConfig().getLogNil())) return;
        out.info("> " + ret.tojstring());
    }

    public Globals getEnvironment(String projectName) {
        for (SoftReference<Globals> g : new ArrayList<>(environmentList)) {
            Globals g1 = g.get();
            if (g1 != null) {
                LuaValue lv = g1.get("project");
                if (lv.istable()) {
                    LuaTable lt = lv.checktable();
                    LuaValue lv1 = lt.get("GetName");
                    if (lv1.isfunction()) {
                        if (lv1.checkfunction().call().tojstring().equals(projectName)) return g1;
                    }
                }
            }
        }
        return null;
    }

    public LuaFunction getLoggerFunction(LuaValue env, int level) {
        String name;
        switch (level) {
            case LEVEL_INFO:
                name = "LogInfo";
                break;
            case LEVEL_WARN:
                name = "LogWarn";
                break;
            case LEVEL_ERROR:
                name = "LogError";
                break;
            default:
                throw new IllegalStateException("Invalid log level");
        }
        LuaValue lv = env.get(name);
        if (lv.isfunction()) return lv.checkfunction();
        lv = env.get("print");
        if (lv.isfunction()) return lv.checkfunction();
        return new NilFunction();
    }

    public LuaFunction getLoggerFunction(LuaValue env) {
        return getLoggerFunction(env, LEVEL_INFO);
    }

    private final Map<String, LuaValue> exports = new HashMap<>();
    private final Map<String, List<LuaFunction>> importCallbacks = new HashMap<>();
    private final List<SoftReference<Globals>> environmentList = new ArrayList<>();
    private final Set<String> projectNames = new HashSet<>();

    private static class project extends TwoArgFunction {

        private final LuaSandbox sbox;
        private final String pName;
        private final LuaOutputHandler out;
        public project(LuaSandbox sandbox, String projectNmae, LuaOutputHandler out) {
            this.sbox = sandbox;
            this.pName = projectNmae;
            this.out = out;
        }

        @Override
        public LuaValue call(LuaValue modname, LuaValue env) {
            LuaTable library = tableOf();
            LuaFunction logErr = new LogSpecific(out, LEVEL_ERROR);
            library.set("GetName", new GetName(pName));
            library.set("Export", new Export(sbox));
            library.set("Import", new Import(sbox, logErr));
            library.set("Log", new Log(out));
            LuaFunction fn = new LogSpecific(out, LEVEL_INFO);
            library.set("LogInfo", fn);
            library.set("LogWarn", new LogSpecific(out, LEVEL_WARN));
            library.set("LogError", logErr);
            env.set("project", library);
            env.set("print", fn);
            return library;
        }

        static class GetName extends ZeroArgFunction {
            private final String name;
            public GetName(String name) {
                this.name = name;
            }

            @Override
            public LuaValue call() {
                return LuaValue.valueOf(name);
            }
        }

        static class Export extends TwoArgFunction {
            private final LuaSandbox sbox;
            public Export(LuaSandbox sbox) {
                this.sbox = sbox;
            }

            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                String name = arg1.tojstring();
                for (SoftReference<Globals> sr : new ArrayList<>(sbox.environmentList)) {
                    Globals g = sr.get();
                    if (g != null) {
                        g.set(name, arg2);
                    } else {
                        sbox.environmentList.remove(sr);
                    }
                }
                sbox.exports.put(name, arg2);
                List<LuaFunction> lfs = sbox.importCallbacks.get(name);
                if (lfs != null) {
                    for (LuaFunction lf : lfs) {
                        try {
                            lf.call(arg2);
                        } catch (LuaError ignored) { }
                    }
                    sbox.importCallbacks.remove(name);
                }
                return LuaValue.NIL;
            }
        }

        static class Import extends TwoArgFunction {
            private final LuaSandbox sbox;
            private final LuaFunction err;
            public Import(LuaSandbox sbox, LuaFunction logErr) {
                this.sbox = sbox;
                this.err = logErr;
            }

            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                String name = arg1.tojstring();
                LuaFunction lf = arg2.checkfunction();
                LuaValue value = sbox.exports.get(name);
                if (value != null) {
                    try {
                        lf.call(value);
                    } catch (LuaError e) {
                        err.call("LUA Error in hook.Import synchronous callback (" + name + "): " + e.getMessage());
                    }
                } else {
                    List<LuaFunction> lfs = sbox.importCallbacks.get(name);
                    if (lfs == null) lfs = new ArrayList<>();
                    lfs.add(new OneArgFunction() {
                        @Override
                        public LuaValue call(LuaValue arg) {
                            try {
                                lf.call(arg);
                            } catch (LuaError le) {
                                err.call("LUA Error in hook.Import callback (" + name + "): " + le.getMessage());
                            }
                            return LuaValue.NIL;
                        }
                    });
                    sbox.importCallbacks.put(name, lfs);
                }
                return LuaValue.NIL;
            }
        }

        static class Log extends VarArgFunction {
            private final LuaOutputHandler out;
            public Log(LuaOutputHandler out) {
                this.out = out;
            }

            @Override
            public Varargs invoke(Varargs args) {
                int level = args.checkint(1);
                Varargs sub;
                if (args.narg() > 1) {
                    sub = args.subargs(2);
                } else {
                    sub = varargsOf(new LuaValue[0]);
                }
                log(out, level, sub);
                return LuaValue.NIL;
            }
        }

        static class LogSpecific extends VarArgFunction {
            private final LuaOutputHandler out;
            private final int level;
            public LogSpecific(LuaOutputHandler out, int level) {
                this.out = out;
                this.level = level;
            }

            @Override
            public Varargs invoke(Varargs args) {
                log(out, level, args);
                return LuaValue.NIL;
            }
        }


        static void log(LuaOutputHandler out, int level, Varargs args) throws LuaError {
            StringBuilder sb = new StringBuilder();
            int nargs = args.narg();
            for (int i=0; i < nargs; i++) {
                if (i > 0) sb.append(" ");
                sb.append(args.arg(i + 1).tojstring());
            }
            String s = sb.toString();
            switch (level) {
                case LEVEL_INFO:
                    out.info(s);
                    break;
                case LEVEL_WARN:
                    out.warn(s);
                    break;
                case LEVEL_ERROR:
                    out.error(s);
                    break;
                default:
                    throw new LuaError("Invalid log level");
            }
        }

    }

    private Globals createGlobals(String projectName, LuaOutputHandler out) {
        Globals env = JsePlatform.standardGlobals();
        // load all non-standard libraries EXCEPT project
        environmentList.add(new SoftReference<>(env));
        projectNames.add(projectName);
        env.load(new project(this, projectName, out));
        env.load(new server());
        env.load(new particle());
        env.load(new timer());
        env.load(new http());
        env.load(new util()); // beware that util uses native print
        env.load(new enums());
        env.load(new hook());
        env.load(new command());
        for (Map.Entry<String, LuaValue> entry : exports.entrySet()) {
            env.set(entry.getKey(), entry.getValue());
        }
        return env;
    }

}

package codes.wasabi.xplug.library;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaToolkit;
import codes.wasabi.xplug.util.func.NilFunction;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

public class http extends TwoArgFunction {

    private final HTTP http = new HTTP();

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("Fetch", new Fetch(http));
        library.set("Post", new Post(http));
        env.set("http", library);
        env.set("HTTP", http);
        return library;
    }

    static class HTTP extends OneArgFunction {
        private final LuaToolkit ltk = XPlug.getToolkit();

        @Override
        public LuaValue call(LuaValue arg) {
            LuaValue proto;
            String uri = arg.get("url").checkjstring();
            proto = arg.get("method");
            String method = (proto.isstring() ? proto.tojstring().toUpperCase(Locale.ROOT) : "GET");
            proto = arg.get("success");
            LuaFunction successFunc = (proto.isfunction() ? proto.checkfunction() : new NilFunction());
            proto = arg.get("failed");
            LuaFunction failFunc = (proto.isfunction() ? proto.checkfunction() : new NilFunction());
            proto = arg.get("headers");
            LuaTable headers = (proto.istable() ? proto.checktable() : tableOf());
            proto = arg.get("timeout");
            int timeout = (proto.isint() ? proto.toint() : 60);
            //
            boolean hasBody = false;
            String body = "";
            String contentType = "";
            //
            proto = arg.get("body");
            if (proto.isstring()) {
                hasBody = true;
                body = proto.checkjstring();
                contentType = "text/plain; charset=utf-8";
            } else {
                proto = arg.get("parameters");
                if (proto.istable()) {
                    LuaTable lt = proto.checktable();
                    StringBuilder sb = new StringBuilder();
                    LuaValue[] keys = lt.keys();
                    int count = keys.length;
                    try {
                        for (int i = 0; i < count; i++) {
                            if (i > 0) sb.append("&");
                            LuaValue key = keys[i];
                            LuaValue value = lt.get(key);
                            sb.append(URLEncoder.encode(key.tojstring(), "UTF-8"));
                            sb.append("=");
                            sb.append(URLEncoder.encode(value.tojstring(), "UTF-8"));
                        }
                    } catch (UnsupportedEncodingException ignored) { }
                    hasBody = true;
                    body = sb.toString();
                    contentType = "x-www-form-urlencoded; charset=utf-8";
                }
            }
            //
            proto = arg.get("type");
            if (proto.isstring()) contentType = proto.tojstring();
            //
            boolean finalHasBody = hasBody;
            String finalContentType = contentType;
            String finalBody = body;
            Executors.newSingleThreadExecutor().execute(() -> {
                InputStream is = null;
                OutputStream os = null;
                try {
                    URL url = new URL(uri);
                    URLConnection connection = url.openConnection();
                    if (!(connection instanceof HttpURLConnection)) throw new IOException("Protocol is not HTTP");
                    HttpURLConnection con = (HttpURLConnection) connection;
                    con.setRequestMethod(method);
                    con.setConnectTimeout(timeout * 1000);
                    con.setDoOutput(finalHasBody);
                    con.setRequestProperty("Content-Type", finalContentType);
                    //
                    LuaValue[] keys = headers.keys();
                    for (LuaValue key : keys) {
                        LuaValue value = headers.get(key);
                        con.setRequestProperty(key.tojstring(), value.tojstring());
                    }
                    //
                    if (finalHasBody) {
                        os = con.getOutputStream();
                        os.write(finalBody.getBytes(StandardCharsets.UTF_8));
                        os.flush();
                    }
                    //
                    is = con.getInputStream();
                    StringBuilder sb = new StringBuilder();
                    byte[] buffer = new byte[2048];
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                        sb.append(new String(buffer, 0, read, StandardCharsets.UTF_8));
                    }
                    int respCode = con.getResponseCode();
                    String respBody = sb.toString();
                    LuaTable respHeaders = tableOf();
                    for (Map.Entry<String, List<String>> entry : con.getHeaderFields().entrySet()) {
                        String key = entry.getKey();
                        if (key == null) continue;
                        List<String> list = entry.getValue();
                        int listSize = list.size();
                        if (listSize < 1) continue;
                        StringBuilder sb1 = new StringBuilder();
                        for (int i=0; i < listSize; i++) {
                            if (i > 0) sb1.append(", ");
                            sb1.append(list.get(i));
                        }
                        respHeaders.set(key, sb1.toString());
                    }
                    //
                    ltk.synchronize(() -> successFunc.call(valueOf(respCode), valueOf(respBody), respHeaders));
                } catch (IOException e) {
                    ltk.synchronize(() -> failFunc.call(valueOf(e.getMessage())));
                } finally {
                    if (is != null) {
                        try { is.close(); } catch (Exception ignored) { }
                    }
                    if (os != null) {
                        try { os.close(); } catch (Exception ignored) { }
                    }
                }
            });
            //
            return LuaValue.NIL;
        }
    }

    private static LuaFunction transcode(LuaValue optFn) {
        return new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (optFn.isfunction()) {
                    LuaFunction lf = optFn.checkfunction();
                    //
                    int code = args.checkint(1);
                    String body = args.checkjstring(2);
                    LuaTable headers = args.checktable(3);
                    //
                    lf.invoke(varargsOf(new LuaValue[]{ valueOf(body), valueOf(body.length()), headers, valueOf(code) }));
                }
                return LuaValue.NIL;
            }
        };
    }

    static class Fetch extends VarArgFunction {

        private final HTTP http;
        Fetch(HTTP http) {
            this.http = http;
        }

        @Override
        public Varargs invoke(Varargs args) {
            http.call(tableOf(new LuaValue[]{
                    valueOf("method"), valueOf("GET"),
                    valueOf("url"), args.checkstring(1),
                    valueOf("success"), transcode(args.arg(2)),
                    valueOf("failed"), args.arg(3),
                    valueOf("headers"), args.arg(4)
            }));
            return LuaValue.NIL;
        }

    }

    static class Post extends VarArgFunction {

        private final HTTP http;
        Post(HTTP http) {
            this.http = http;
        }

        @Override
        public Varargs invoke(Varargs args) {
            http.call(tableOf(new LuaValue[]{
                    valueOf("method"), valueOf("POST"),
                    valueOf("url"), args.checkstring(1),
                    valueOf("parameters"), args.checktable(2),
                    valueOf("success"), transcode(args.arg(3)),
                    valueOf("failed"), args.arg(4),
                    valueOf("headers"), args.arg(5)
            }));
            return LuaValue.NIL;
        }

    }

}

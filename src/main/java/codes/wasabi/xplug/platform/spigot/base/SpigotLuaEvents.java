package codes.wasabi.xplug.platform.spigot.base;
/*
  XPlug | A  LUA platform for Spigot
  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaEvents;
import codes.wasabi.xplug.util.LuaBridge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class SpigotLuaEvents extends LuaEvents implements Listener {

    private final SpigotLuaToolkit tk;
    public SpigotLuaEvents(SpigotLuaToolkit tk) {
        super();
        this.tk = tk;
        Bukkit.getPluginManager().registerEvents(this, XPlug.getInstance());
        Bukkit.getScheduler().runTaskTimer(XPlug.getInstance(), this::onTick, 0L, 1L);
    }

    private SpigotLuaTypeAdapter adapter = null;
    private SpigotLuaTypeAdapter adapter() {
        if (adapter == null) {
            adapter = tk.getTypeAdapter();
        }
        return adapter;
    }

    private void helper(Event event, String hookName, LuaValue... args) {
        Boolean value = runHook(hookName, LuaValue.varargsOf(args));
        if (value != null) {
            if (event instanceof Cancellable) {
                ((Cancellable) event).setCancelled(!value);
            }
        }
    }

    private void helper(Event event, String hookName, Object... args) {
        LuaValue[] lvs = new LuaValue[args.length];
        for (int i=0; i < args.length; i++) {
            lvs[i] = LuaBridge.toLua(args[i]);
        }
        helper(event, hookName, lvs);
    }

    public void onTick() {
        runHook("Think", LuaValue.NIL);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        helper(event, "PlayerJoin", adapter().convertPlayer(event.getPlayer()), event.getJoinMessage());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        helper(event, "PlayerQuit", adapter().convertPlayer(event.getPlayer()), event.getQuitMessage());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        // As of some version both the location and worlds can theoretically be null
        if (to == null) to = from;
        World fromWorld = from.getWorld();
        if (fromWorld == null) fromWorld = event.getPlayer().getWorld();
        World toWorld = to.getWorld();
        if (toWorld == null) toWorld = fromWorld;
        if (!fromWorld.getName().equals(toWorld.getName())) {
            helper(event, "PlayerChangeWorld", adapter().convertPlayer(event.getPlayer()), adapter().convertWorld(fromWorld), adapter().convertWorld(toWorld));
        } else {
            helper(event, "PlayerMove", adapter().convertPlayer(event.getPlayer()), from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        helper(event, "PlayerChat", adapter().convertPlayer(event.getPlayer()), event.getMessage(), new OneArgFunction(){
            @Override
            public LuaValue call(LuaValue arg) {
                event.setMessage(arg.tojstring());
                return LuaValue.NIL;
            }
        }, event.isAsynchronous());
    }

}

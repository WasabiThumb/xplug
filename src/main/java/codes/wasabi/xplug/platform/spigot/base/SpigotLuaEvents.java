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
import io.papermc.lib.PaperLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.lang.reflect.Method;

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
        }
        helper(event, "PlayerMove", adapter().convertPlayer(event.getPlayer()), adapter().convertLocation(from), adapter().convertLocation(to));
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

    @EventHandler
    public void onChangeGameMode(PlayerGameModeChangeEvent event) {
        helper(
                event,
                "PlayerChangeGameMode",
                adapter().convertPlayer(event.getPlayer()),
                adapter().convertGameMode(event.getPlayer().getGameMode()),
                adapter().convertGameMode(event.getNewGameMode())
        );
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        helper(
                event,
                "PlayerPlaceBlock",
                adapter().convertPlayer(event.getPlayer()),
                adapter().convertBlock(event.getBlock()),
                adapter().convertBlock(event.getBlockAgainst()),
                adapter().convertItemStack(event.getItemInHand())
        );
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        helper(
                event,
                "PlayerBreakBlock",
                adapter().convertPlayer(event.getPlayer()),
                adapter().convertBlock(event.getBlock()),
                event.getExpToDrop(),
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue arg) {
                        event.setExpToDrop(arg.toint());
                        return LuaValue.NIL;
                    }
                }
        );
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Block b = event.getClickedBlock();
        if (action.equals(Action.PHYSICAL)) {
            helper(
                    event,
                    "PlayerInteractPhysical",
                    adapter().convertPlayer(event.getPlayer()),
                    b == null ? LuaValue.NIL : adapter().convertBlock(b)
            );
        } else {
            Location loc = event.getPlayer().getLocation();
            if (b != null) {
                loc = b.getLocation().add(0.5d, 0.5d, 0.5d);
                if (PaperLib.isVersion(13, 2)) {
                    loc.add(event.getBlockFace().getDirection().multiply(0.5d));
                } else {
                    switch (event.getBlockFace()) {
                        case UP:
                            loc.add(0d, 0.5d, 0d);
                            break;
                        case DOWN:
                            loc.add(0d, -0.5d, 0d);
                            break;
                        case EAST:
                            loc.add(0.5d, 0d, 0d);
                            break;
                        case WEST:
                            loc.add(-0.5d, 0d, 0d);
                            break;
                        case SOUTH:
                            loc.add(0d, 0d, 0.5d);
                            break;
                        case NORTH:
                            loc.add(0d, 0d, -0.5d);
                            break;
                        case NORTH_EAST:
                            loc.add(0.5d, 0d, -0.5d);
                            break;
                        case NORTH_WEST:
                            loc.add(-0.5d, 0d, -0.5d);
                            break;
                        case SOUTH_EAST:
                            loc.add(0.5d, 0d, 0.5d);
                            break;
                        case SOUTH_WEST:
                            loc.add(-0.5d, 0d, 0.5d);
                            break;
                    }
                }
            }
            ItemStack is = event.getItem();
            helper(
                    event,
                    "PlayerInteract",
                    adapter().convertPlayer(event.getPlayer()),
                    is == null ? LuaValue.NIL : adapter().convertItemStack(is),
                    action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK),
                    b == null ? LuaValue.NIL : adapter().convertBlock(b),
                    LuaValue.NIL,
                    loc
            );
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Location loc = event.getRightClicked().getLocation();
        if (event instanceof PlayerInteractAtEntityEvent) {
            loc = ((PlayerInteractAtEntityEvent) event).getClickedPosition().toLocation(event.getRightClicked().getWorld());
        }
        ItemStack is;
        if (PaperLib.isVersion(16, 1)) {
            is = event.getPlayer().getInventory().getItem(event.getHand());
        } else if (PaperLib.isVersion(9)) {
            if (event.getHand().equals(EquipmentSlot.HAND)) {
                is = event.getPlayer().getInventory().getItemInMainHand();
            } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
                is = event.getPlayer().getInventory().getItemInOffHand();
            } else {
                is = null;
            }
        } else {
            Player ply = event.getPlayer();
            Class<? extends Player> clazz = ply.getClass();
            try {
                Method m = clazz.getMethod("getItemInHand");
                is = (ItemStack) m.invoke(ply);
            } catch (Exception e) {
                e.printStackTrace();
                is = null;
            }
        }
        helper(
                event,
                "PlayerInteract",
                adapter().convertPlayer(event.getPlayer()),
                is == null ? LuaValue.NIL : adapter().convertItemStack(is),
                false,
                null,
                adapter().convertEntity(event.getRightClicked()),
                loc
        );
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        helper(
                event,
                "PlayerDropItem",
                adapter().convertPlayer(event.getPlayer()),
                adapter().convertEntity(event.getItemDrop()),
                adapter().convertItemStack(event.getItemDrop().getItemStack())
        );
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent byEntity = (EntityDamageByEntityEvent) event;
            helper(
                    event,
                    "EntityDamageByEntity",
                    adapter().convertEntity(byEntity.getEntity()),
                    adapter().convertEntity(byEntity.getDamager()),
                    event.getCause().toString(),
                    byEntity.getDamage(),
                    new OneArgFunction() {
                        @Override
                        public LuaValue call(LuaValue arg) {
                            byEntity.setDamage(arg.todouble());
                            return LuaValue.NIL;
                        }
                    }
            );
        } else if (event instanceof EntityDamageByBlockEvent) {
            EntityDamageByBlockEvent byBlock = (EntityDamageByBlockEvent) event;
            Block block = byBlock.getDamager();
            helper(
                    event,
                    "EntityDamageByBlock",
                    adapter().convertEntity(byBlock.getEntity()),
                    block == null ? LuaValue.NIL : adapter().convertBlock(block),
                    event.getCause().toString(),
                    byBlock.getDamage(),
                    new OneArgFunction() {
                        @Override
                        public LuaValue call(LuaValue arg) {
                            byBlock.setDamage(arg.todouble());
                            return LuaValue.NIL;
                        }
                    }
            );
        }
        if (event.isCancelled()) return;
        helper(
                event,
                "EntityDamage",
                adapter().convertEntity(event.getEntity()),
                event.getCause().toString(),
                event.getDamage(),
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue arg) {
                        event.setDamage(arg.todouble());
                        return LuaValue.NIL;
                    }
                }
        );
    }

}

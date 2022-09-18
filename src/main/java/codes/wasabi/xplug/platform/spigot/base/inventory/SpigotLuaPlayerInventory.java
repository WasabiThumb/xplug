package codes.wasabi.xplug.platform.spigot.base.inventory;

import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.struct.inventory.LuaItemStack;
import codes.wasabi.xplug.struct.inventory.LuaPlayerInventory;
import codes.wasabi.xplug.util.func.GetterFunction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.luaj.vm2.LuaTable;

public abstract class SpigotLuaPlayerInventory extends SpigotLuaInventory implements LuaPlayerInventory {

    private final PlayerInventory inventory;
    public SpigotLuaPlayerInventory(PlayerInventory inventory) {
        super(inventory);
        this.inventory = inventory;
    }

    @Override
    public PlayerInventory getBukkitInventory() {
        return inventory;
    }

    @Override
    public LuaTable getLuaValue() {
        LuaTable lt = LuaPlayerInventory.super.getLuaValue();
        lt.set("GetHandle", new GetterFunction(this::getBukkitInventory));
        return lt;
    }

    @Override
    public LuaItemStack[] getArmorContents() {
        ItemStack[] conts = inventory.getArmorContents();
        LuaItemStack[] ret = new LuaItemStack[conts.length];
        for (int i=0; i < conts.length; i++) {
            ret[i] = SpigotLuaToolkit.getAdapter().convertItemStack(conts[i]);
        }
        return ret;
    }

    @Override
    public void setArmorContents(LuaItemStack[] contents) {
        ItemStack[] conts = new ItemStack[contents.length];
        for (int i=0; i < contents.length; i++) {
            conts[i] = SpigotLuaToolkit.getAdapter().convertItemStack(contents[i]);
        }
        inventory.setArmorContents(conts);
    }

}

package codes.wasabi.xplug.platform.spigot.base.inventory;

import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.struct.inventory.LuaInventory;
import codes.wasabi.xplug.struct.inventory.LuaItemStack;
import codes.wasabi.xplug.util.func.GetterFunction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaTable;

public abstract class SpigotLuaInventory implements LuaInventory {

    private final Inventory inventory;
    public SpigotLuaInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getBukkitInventory() {
        return inventory;
    }

    @Override
    public int getSize() {
        return inventory.getSize();
    }

    @Override
    public LuaItemStack getItem(int index) {
        return SpigotLuaToolkit.getAdapter().convertItemStack(inventory.getItem(index));
    }

    @Override
    public void setItem(int index, LuaItemStack item) {
        inventory.setItem(index, SpigotLuaToolkit.getAdapter().convertItemStack(item));
    }

    @Override
    public int firstEmpty() {
        return inventory.firstEmpty();
    }

    @Override
    public LuaItemStack[] getContents() {
        ItemStack[] conts = inventory.getContents();
        LuaItemStack[] ret = new LuaItemStack[conts.length];
        for (int i=0; i < conts.length; i++) {
            ret[i] = SpigotLuaToolkit.getAdapter().convertItemStack(conts[i]);
        }
        return ret;
    }

    @Override
    public void setContents(LuaItemStack[] contents) {
        ItemStack[] conts = new ItemStack[contents.length];
        for (int i=0; i < contents.length; i++) {
            conts[i] = SpigotLuaToolkit.getAdapter().convertItemStack(contents[i]);
        }
        inventory.setContents(conts);
    }

    @Override
    public boolean contains(LuaItemStack itemStack) {
        return inventory.contains(SpigotLuaToolkit.getAdapter().convertItemStack(itemStack));
    }

    @Override
    public boolean containsAtLeast(LuaItemStack itemStack, int count) {
        return inventory.containsAtLeast(SpigotLuaToolkit.getAdapter().convertItemStack(itemStack), count);
    }

    @Override
    public int getMaxStackSize() {
        return inventory.getMaxStackSize();
    }

    @Override
    public LuaTable getLuaValue() {
        LuaTable lt = LuaInventory.super.getLuaValue();
        lt.set("GetHandle", new GetterFunction(this::getBukkitInventory));
        return lt;
    }

}

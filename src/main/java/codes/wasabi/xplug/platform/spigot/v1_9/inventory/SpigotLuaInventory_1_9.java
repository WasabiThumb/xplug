package codes.wasabi.xplug.platform.spigot.v1_9.inventory;

import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.platform.spigot.base.inventory.SpigotLuaInventory;
import codes.wasabi.xplug.struct.inventory.LuaItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SpigotLuaInventory_1_9 extends SpigotLuaInventory {

    public SpigotLuaInventory_1_9(Inventory inventory) {
        super(inventory);
    }

    @Override
    public LuaItemStack[] getStorageContents() {
        ItemStack[] conts = getBukkitInventory().getStorageContents();
        LuaItemStack[] ret = new LuaItemStack[conts.length];
        for (int i=0; i < conts.length; i++) {
            ret[i] = SpigotLuaToolkit.getAdapter().convertItemStack(conts[i]);
        }
        return ret;
    }

    @Override
    public void setStorageContents(LuaItemStack[] itemStacks) {
        ItemStack[] conts = new ItemStack[itemStacks.length];
        for (int i=0; i < itemStacks.length; i++) {
            conts[i] = SpigotLuaToolkit.getAdapter().convertItemStack(itemStacks[i]);
        }
        getBukkitInventory().setStorageContents(conts);
    }

}

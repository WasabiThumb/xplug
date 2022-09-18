package codes.wasabi.xplug.platform.spigot.v1_8.inventory;

import codes.wasabi.xplug.platform.spigot.base.inventory.SpigotLuaInventory;
import codes.wasabi.xplug.struct.inventory.LuaItemStack;
import org.bukkit.inventory.Inventory;

public class SpigotLuaInventory_1_8 extends SpigotLuaInventory {

    public SpigotLuaInventory_1_8(Inventory inventory) {
        super(inventory);
    }

    @Override
    public LuaItemStack[] getStorageContents() {
        return getContents();
    }

    @Override
    public void setStorageContents(LuaItemStack[] itemStacks) {
        setContents(itemStacks);
    }

}

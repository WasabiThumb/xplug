package codes.wasabi.xplug.platform.spigot.v1_8.inventory;

import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.platform.spigot.base.inventory.SpigotLuaPlayerInventory;
import codes.wasabi.xplug.struct.inventory.LuaItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SpigotLuaPlayerInventory_1_8 extends SpigotLuaPlayerInventory {

    public SpigotLuaPlayerInventory_1_8(PlayerInventory inventory) {
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

    @Override
    public LuaItemStack getItemInHand() {
        return SpigotLuaToolkit.getAdapter().convertItemStack(getBukkitInventory().getItemInHand());
    }

    @Override
    public void setItemInHand(LuaItemStack itemStack) {
        getBukkitInventory().setItemInHand(SpigotLuaToolkit.getAdapter().convertItemStack(itemStack));
    }

    @Override
    public LuaItemStack getItemInOffhand() {
        return null;
    }

    @Override
    public void setItemInOffhand(LuaItemStack itemStack) {
    }

    @Override
    public boolean supportsOffhand() {
        return false;
    }

}

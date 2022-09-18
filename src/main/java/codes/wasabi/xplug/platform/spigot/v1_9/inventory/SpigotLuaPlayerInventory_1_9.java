package codes.wasabi.xplug.platform.spigot.v1_9.inventory;

import codes.wasabi.xplug.platform.spigot.base.SpigotLuaToolkit;
import codes.wasabi.xplug.platform.spigot.base.inventory.SpigotLuaPlayerInventory;
import codes.wasabi.xplug.struct.inventory.LuaItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SpigotLuaPlayerInventory_1_9 extends SpigotLuaPlayerInventory {

    public SpigotLuaPlayerInventory_1_9(PlayerInventory inventory) {
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

    @Override
    public LuaItemStack getItemInHand() {
        return SpigotLuaToolkit.getAdapter().convertItemStack(getBukkitInventory().getItemInMainHand());
    }

    @Override
    public void setItemInHand(LuaItemStack itemStack) {
        getBukkitInventory().setItemInMainHand(SpigotLuaToolkit.getAdapter().convertItemStack(itemStack));
    }

    @Override
    public LuaItemStack getItemInOffhand() {
        return SpigotLuaToolkit.getAdapter().convertItemStack(getBukkitInventory().getItemInOffHand());
    }

    @Override
    public void setItemInOffhand(LuaItemStack itemStack) {
        getBukkitInventory().setItemInOffHand(SpigotLuaToolkit.getAdapter().convertItemStack(itemStack));
    }

    @Override
    public boolean supportsOffhand() {
        return true;
    }

}

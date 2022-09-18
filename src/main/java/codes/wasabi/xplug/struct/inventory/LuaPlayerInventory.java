package codes.wasabi.xplug.struct.inventory;

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.util.func.GetterFunction;
import codes.wasabi.xplug.util.func.OneArgMetaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public interface LuaPlayerInventory extends LuaInventory {

    LuaItemStack[] getArmorContents();

    void setArmorContents(LuaItemStack[] contents);

    LuaItemStack getItemInHand();

    void setItemInHand(LuaItemStack itemStack);

    LuaItemStack getItemInOffhand();

    void setItemInOffhand(LuaItemStack itemStack);

    boolean supportsOffhand();

    @Override
    default LuaTable getLuaValue() {
        LuaTable lt = LuaInventory.super.getLuaValue();
        lt.set("GetArmorContents", new GetterFunction(this::getArmorContents));
        lt.set("SetArmorContents", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                LuaTable lt = arg.checktable();
                int maxIndex = 1;
                for (LuaValue lv : lt.keys()) {
                    maxIndex = Math.max(maxIndex, lv.toint());
                }
                LuaItemStack[] contents = new LuaItemStack[maxIndex];
                for (int i=0; i < maxIndex; i++) {
                    LuaValue value = lt.get(i + 1);
                    if (value.isnil()) {
                        contents[i] = null;
                    } else {
                        contents[i] = XPlug.getToolkit().parseItemStack(value);
                    }
                }
                setArmorContents(contents);
                return LuaValue.NIL;
            }
        });
        lt.set("GetItemInHand", new GetterFunction(this::getItemInHand));
        lt.set("SetItemInHand", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setItemInHand(arg.isnil() ? null : XPlug.getToolkit().parseItemStack(arg));
                return LuaValue.NIL;
            }
        });
        lt.set("GetItemOffhand", new GetterFunction(this::getItemInOffhand));
        lt.set("SetItemOffhand", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                setItemInOffhand(arg.isnil() ? null : XPlug.getToolkit().parseItemStack(arg));
                return LuaValue.NIL;
            }
        });
        lt.set("SupportsOffhand", new GetterFunction(this::supportsOffhand));
        return lt;
    }

}

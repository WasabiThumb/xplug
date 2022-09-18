package codes.wasabi.xplug.struct.inventory;

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaValueHolder;
import codes.wasabi.xplug.util.LuaBridge;
import codes.wasabi.xplug.util.func.GetterFunction;
import codes.wasabi.xplug.util.func.OneArgMetaFunction;
import codes.wasabi.xplug.util.func.TwoArgMetaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public interface LuaInventory extends LuaValueHolder {

    int getSize();

    LuaItemStack getItem(int index);

    void setItem(int index, LuaItemStack item);

    int firstEmpty();

    LuaItemStack[] getContents();

    void setContents(LuaItemStack[] contents);

    boolean contains(LuaItemStack itemStack);

    boolean containsAtLeast(LuaItemStack itemStack, int count);

    int getMaxStackSize();

    LuaItemStack[] getStorageContents();

    void setStorageContents(LuaItemStack[] itemStacks);

    @Override
    default LuaTable getLuaValue() {
        LuaTable lt = LuaValue.tableOf();
        lt.set("GetSize", new GetterFunction(this::getSize));
        lt.set("GetItem", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                return LuaBridge.toLua(getItem(arg.checkint()));
            }
        });
        lt.set("SetItem", new TwoArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg1, LuaValue arg2) {
                setItem(arg1.checkint(), arg2.isnil() ? null : XPlug.getToolkit().parseItemStack(arg2));
                return LuaValue.NIL;
            }
        });
        lt.set("FirstEmpty", new GetterFunction(this::firstEmpty));
        lt.set("GetContents", new GetterFunction(this::getContents));
        lt.set("SetContents", new OneArgMetaFunction() {
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
                setContents(contents);
                return LuaValue.NIL;
            }
        });
        lt.set("Contains", new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                if (arg.isnil()) return LuaValue.FALSE;
                LuaItemStack is = XPlug.getToolkit().parseItemStack(arg);
                if (is == null) return LuaValue.FALSE;
                return LuaValue.valueOf(contains(is));
            }
        });
        lt.set("ContainsAtLeast", new TwoArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg1, LuaValue arg2) {
                if (arg1.isnil()) return LuaValue.FALSE;
                LuaItemStack is = XPlug.getToolkit().parseItemStack(arg1);
                if (is == null) return LuaValue.FALSE;
                return LuaValue.valueOf(containsAtLeast(is, arg2.toint()));
            }
        });
        lt.set("GetMaxStackSize", new GetterFunction(this::getMaxStackSize));
        lt.set("GetStorageContents", new GetterFunction(this::getStorageContents));
        lt.set("SetStorageContents", new OneArgMetaFunction() {
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
                setStorageContents(contents);
                return LuaValue.NIL;
            }
        });
        return lt;
    }

}

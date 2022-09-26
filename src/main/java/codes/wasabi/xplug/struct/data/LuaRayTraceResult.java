package codes.wasabi.xplug.struct.data;

import codes.wasabi.xplug.struct.LuaValueHolder;
import codes.wasabi.xplug.struct.block.LuaBlock;
import codes.wasabi.xplug.struct.entity.LuaEntity;
import codes.wasabi.xplug.util.LuaBridge;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class LuaRayTraceResult implements LuaValueHolder {

    private final LuaVector hitPos;
    private final LuaBlock hitBlock;
    private final LuaEntity hitEntity;
    public LuaRayTraceResult(LuaVector hitPos, LuaBlock hitBlock, LuaEntity hitEntity) {
        this.hitPos = hitPos;
        this.hitBlock = hitBlock;
        this.hitEntity = hitEntity;
    }

    @Override
    public LuaTable getLuaValue() {
        LuaTable ret = LuaValue.tableOf();
        ret.set("HitPos", LuaBridge.toLua(hitPos));
        ret.set("HitBlock", LuaBridge.toLua(hitBlock));
        ret.set("HitEntity", LuaBridge.toLua(hitEntity));
        return ret;
    }

}

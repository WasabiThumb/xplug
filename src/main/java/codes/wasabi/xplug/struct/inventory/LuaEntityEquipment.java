package codes.wasabi.xplug.struct.inventory;
/*
  XPlug | A  LUA platform for Spigot
  Copyright 2022 Wasabi Codes

  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

import codes.wasabi.xplug.XPlug;
import codes.wasabi.xplug.struct.LuaValueHolder;
import codes.wasabi.xplug.util.func.GetterFunction;
import codes.wasabi.xplug.util.func.OneArgMetaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface LuaEntityEquipment extends LuaValueHolder {

    LuaItemStack getHead();

    void setHead(LuaItemStack helmet);

    LuaItemStack getChest();

    void setChest(LuaItemStack chest);

    LuaItemStack getLegs();

    void setLegs(LuaItemStack legs);

    LuaItemStack getFeet();

    void setFeet(LuaItemStack feet);

    LuaItemStack getHand();

    void setHand(LuaItemStack hand);

    boolean supportsOffhand();

    LuaItemStack getOffhand();

    void setOffhand(LuaItemStack offhand);

    @Override
    default LuaTable getLuaValue() {
        LuaTable lt = LuaValue.tableOf();
        lt.set("GetHead", new GetterFunction(this::getHead));
        lt.set("GetChest", new GetterFunction(this::getChest));
        lt.set("GetLegs", new GetterFunction(this::getLegs));
        lt.set("GetFeet", new GetterFunction(this::getFeet));
        lt.set("GetHand", new GetterFunction(this::getHand));
        lt.set("GetOffhand", new GetterFunction(this::getOffhand));
        lt.set("SupportsOffhand", new GetterFunction(this::supportsOffhand));
        //
        BiConsumer<String, Consumer<LuaItemStack>> addSetter = (s, consumer) -> lt.set(s, new OneArgMetaFunction() {
            @Override
            protected LuaValue call(LuaTable self, LuaValue arg) {
                consumer.accept(XPlug.getToolkit().parseItemStack(arg));
                return LuaValue.NIL;
            }
        });
        addSetter.accept("SetHead", this::setHead);
        addSetter.accept("SetChest", this::setChest);
        addSetter.accept("SetLegs", this::setLegs);
        addSetter.accept("SetFeet", this::setFeet);
        addSetter.accept("SetHand", this::setHand);
        addSetter.accept("SetOffhand", this::setOffhand);
        return lt;

    }

}

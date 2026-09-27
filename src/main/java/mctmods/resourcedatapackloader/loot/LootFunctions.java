package mctmods.resourcedatapackloader.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LootFunctions {
    public static final String NAMESPACE = "rdpl";
    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> REGISTER = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, NAMESPACE);
    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<DropRoll>> DROP_ROLL = REGISTER.register("drop_roll", () -> DropRoll.CODEC);

    static { REGISTER.register("killed_name", () -> KilledName.CODEC); }

    private LootFunctions() {}
}

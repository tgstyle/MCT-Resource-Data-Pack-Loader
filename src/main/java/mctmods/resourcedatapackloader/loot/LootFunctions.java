package mctmods.resourcedatapackloader.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LootFunctions {
    public static final String NAMESPACE = "rdpl";
    public static final DeferredRegister<LootItemFunctionType<?>> REGISTER = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, NAMESPACE);
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<KilledName>> KILLED_NAME = REGISTER.register("killed_name", () -> new LootItemFunctionType<>(KilledName.CODEC));

    private LootFunctions() {}
}

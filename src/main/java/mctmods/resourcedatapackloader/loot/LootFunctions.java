package mctmods.resourcedatapackloader.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class LootFunctions {
    public static final String NAMESPACE = "rdpl";
    public static final DeferredRegister<LootItemFunctionType> REGISTER = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, NAMESPACE);
    public static final RegistryObject<LootItemFunctionType> KILLED_NAME = REGISTER.register("killed_name", () -> new LootItemFunctionType(new KilledName.Serializer()));

    private LootFunctions() {}
}

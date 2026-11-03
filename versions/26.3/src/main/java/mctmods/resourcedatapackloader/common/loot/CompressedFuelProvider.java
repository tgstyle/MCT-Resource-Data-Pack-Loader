package mctmods.resourcedatapackloader.common.loot;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import mctmods.resourcedatapackloader.common.blocks.compress.CompressedFuel;
import mctmods.resourcedatapackloader.common.pack.io.GeneratedResources;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.core.util.json.Json;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;
import net.neoforged.neoforge.common.loot.NeoForgeLootContextParams;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public record CompressedFuelProvider() implements ContextIntProvider {
    public static final MapCodec<CompressedFuelProvider> MAP_CODEC = MapCodec.unit(new CompressedFuelProvider());
    public static final ResourceKey<ContextIntProvider> KEY = ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, Identifier.fromNamespaceAndPath(LootFunctions.NAMESPACE, "cooking/compressed"));
    public static final DeferredRegister<MapCodec<? extends ContextIntProvider>> REGISTER = DeferredRegister.create(Registries.CONTEXT_INT_PROVIDER_TYPE, LootFunctions.NAMESPACE);
    public static final DeferredHolder<MapCodec<? extends ContextIntProvider>, MapCodec<CompressedFuelProvider>> TYPE = REGISTER.register("compressed_fuel", () -> MAP_CODEC);

    @Override @Nonnull
    public MapCodec<CompressedFuelProvider> codec() {
        return MAP_CODEC;
    }

    @Override
    public void validate(@Nonnull ValidationContext context) {
    }

    @Override
    public int getIntUnsafe(LootContext context) {
        ItemInstance queried = context.getOptional(NeoForgeLootContextParams.QUERIED_STACK);
        if (queried == null) { return 0; }
        ItemStack stack = queried instanceof ItemStackTemplate template ? template.create() : queried instanceof ItemStack held ? held : ItemStack.EMPTY;
        int time = CompressedFuel.burnTime(stack, base -> ResolvableInt.getFromItem(base, DataComponents.COOKING_FUEL, CookingFuel::burnTime, context, 0));
        return Math.max(time, 0);
    }

    public static void publish() {
        GeneratedResources.put(PackType.SERVER_DATA, LootFunctions.NAMESPACE, "context_int_provider/cooking/compressed" + PackManager.JSON_SUFFIX, Json.obj("type", LootFunctions.NAMESPACE + ":compressed_fuel").toString());
    }
}

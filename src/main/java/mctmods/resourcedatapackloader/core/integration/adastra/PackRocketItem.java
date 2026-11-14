package mctmods.resourcedatapackloader.core.integration.adastra;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;

import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import earth.terrarium.adastra.common.items.vehicles.RocketItem;
import earth.terrarium.adastra.common.registry.ModDataManagers;
import earth.terrarium.adastra.common.tags.ModFluidTags;
import earth.terrarium.common_storage_lib.context.ItemContext;
import earth.terrarium.common_storage_lib.context.impl.ModifyOnlyContext;
import earth.terrarium.common_storage_lib.fluid.impl.SimpleFluidStorage;
import earth.terrarium.common_storage_lib.resources.fluid.FluidResource;
import earth.terrarium.common_storage_lib.resources.fluid.util.FluidAmounts;
import earth.terrarium.common_storage_lib.storage.base.CommonStorage;
import earth.terrarium.common_storage_lib.storage.util.TransferUtil;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public final class PackRocketItem extends RocketItem {
    private static final Map<ResourceLocation, Item> PLACES = new HashMap<>();

    public PackRocketItem(ResourceLocation rocket, Item.Properties properties) {
        super(() -> ContentEntities.types().get(rocket), properties);
        PLACES.put(rocket, this);
    }

    @Nullable public static Item placing(ResourceLocation rocket) { return PLACES.get(rocket); }

    public static Item of(ResourceLocation rocket, Item.Properties properties) { return new PackRocketItem(rocket, properties.stacksTo(1).fireResistant()); }

    @Override @Nonnull public InteractionResult useOn(@Nonnull UseOnContext context) {
        EntityType<?> placed = type();
        if (placed == null) { return InteractionResult.PASS; }
        ItemStack stack = context.getItemInHand();
        int held = stack.getCount();
        boolean picked = AdAstraRockets.picked(stack);
        InteractionResult result = super.useOn(context);
        Rocket rocket = AdAstraRockets.placed(context.getLevel(), context.getClickedPos(), placed, picked);
        if (rocket == null || stack.getCount() != held) { return result; }
        CommonStorage<FluidResource> tank = getFluids(stack, new ModifyOnlyContext(stack));
        TransferUtil.move(tank, rocket.fluidContainer(), tank.getResource(0), tank.getAmount(0), false);
        stack.shrink(1);
        return InteractionResult.SUCCESS;
    }

    @Override public CommonStorage<FluidResource> getFluids(ItemStack stack, ItemContext context) { return new SimpleFluidStorage(context, ModDataManagers.FLUID_CONTENTS.componentType(), 1, FluidAmounts.toPlatformAmount(AdAstraRockets.tank(this))).filter(0, fluid -> fluid.is(ModFluidTags.FUEL)); }
}

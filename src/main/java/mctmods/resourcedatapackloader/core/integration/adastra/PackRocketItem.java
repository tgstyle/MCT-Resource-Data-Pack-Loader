package mctmods.resourcedatapackloader.core.integration.adastra;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;

import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import earth.terrarium.adastra.common.items.vehicles.RocketItem;
import earth.terrarium.adastra.common.tags.ModFluidTags;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.impl.SimpleFluidContainer;
import earth.terrarium.botarium.common.fluid.impl.WrappedItemFluidContainer;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
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

    @Override @Nonnull public InteractionResult useOn(UseOnContext context) {
        EntityType<?> placed = type();
        if (placed == null) { return InteractionResult.PASS; }
        ItemStack stack = context.getItemInHand();
        int held = stack.getCount();
        boolean picked = AdAstraRockets.picked(stack);
        Player player = context.getPlayer();
        InteractionResult result = super.useOn(context);
        Rocket rocket = AdAstraRockets.placed(context.getLevel(), context.getClickedPos(), placed, picked);
        if (rocket == null || stack.getCount() != held) { return result; }
        if (player == null || !player.getAbilities().instabuild) { stack.shrink(1); }
        return InteractionResult.SUCCESS;
    }

    @Override public WrappedItemFluidContainer getFluidContainer(ItemStack holder) { return new WrappedItemFluidContainer(holder, new SimpleFluidContainer(FluidConstants.fromMillibuckets(AdAstraRockets.tank(this)), 1, (tank, fluid) -> fluid.is(ModFluidTags.FUEL))); }
}

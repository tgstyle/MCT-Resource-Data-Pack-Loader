package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.util.compat.GcRockets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentItemRocket extends ContentItem {
    private static final Map<ResourceLocation, Item> PLACES = new HashMap<>();
    private final ResourceLocation rocket;

    public ContentItemRocket(ItemDef def) {
        super(def);
        this.rocket = new ResourceLocation(def.rocket);
        PLACES.put(rocket, this);
    }

    @Nullable public static Item placing(ResourceLocation rocket) { return PLACES.get(rocket); }

    @Override public int getItemStackLimit(@Nonnull ItemStack stack) { return 1; }

    @Override @Nonnull public EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) { return EnumActionResult.PASS; }
        return GcRockets.place(player.getHeldItem(hand), world, pos, player.capabilities.isCreativeMode, rocket);
    }
}

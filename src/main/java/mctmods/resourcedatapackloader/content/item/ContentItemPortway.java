package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.block.ContentBlockPortway;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.PortwayItemDef;
import mctmods.resourcedatapackloader.content.gui.PackGuiHandler;
import mctmods.resourcedatapackloader.content.util.PortwayBinding;
import mctmods.resourcedatapackloader.content.util.PortwayFx;
import mctmods.resourcedatapackloader.content.util.PortwayStop;
import mctmods.resourcedatapackloader.content.util.PortwayTravel;
import mctmods.resourcedatapackloader.content.util.Portways;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentItemPortway extends ContentItem {
    private final PortwayItemDef portway;

    public ContentItemPortway(ItemDef def) {
        super(def);
        this.portway = def.portway == null ? PortwayItemDef.PLAIN : def.portway;
    }

    public PortwayItemDef portway() { return portway; }

    public void used(EntityPlayer player, ItemStack held) {
        if (portway.consume && !player.capabilities.isCreativeMode) { held.shrink(1); }
    }

    @Override @Nonnull public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (world.isRemote || !(player instanceof EntityPlayerMP)) { return new ActionResult<>(EnumActionResult.SUCCESS, held); }
        if (PortwayTravel.turnsBack(player, world, portway.settings, String.valueOf(getRegistryName()))) { return new ActionResult<>(EnumActionResult.FAIL, held); }
        if (!portway.bind) {
            player.openGui(ResourceDataPackLoader.INSTANCE, hand == EnumHand.MAIN_HAND ? PackGuiHandler.PORTWAY_MAIN : PackGuiHandler.PORTWAY_OFF, world, 0, 0, 0);
            return new ActionResult<>(EnumActionResult.SUCCESS, held);
        }
        PortwayStop bound = PortwayBinding.read(held);
        if (bound == null) {
            PortwayFx.refused(player, "rdpl.portway.unbound");
            return new ActionResult<>(EnumActionResult.FAIL, held);
        }
        PortwayTravel.Result result = PortwayTravel.travel((EntityPlayerMP) player, null, bound, portway.settings);
        if (result == PortwayTravel.Result.GONE) { PortwayBinding.clear(held); }
        else if (result == PortwayTravel.Result.ARRIVED) { used(player, held); }
        return new ActionResult<>(EnumActionResult.SUCCESS, held);
    }

    @Override @Nonnull public EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!portway.bind || !player.isSneaking() || !(world.getBlockState(pos).getBlock() instanceof ContentBlockPortway)) { return EnumActionResult.PASS; }
        if (!world.isRemote && player instanceof EntityPlayerMP) { PortwayTravel.bind((EntityPlayerMP) player, world, pos, player.getHeldItem(hand), portway.settings); }
        return EnumActionResult.SUCCESS;
    }

    @Override public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
        PortwayStop bound = PortwayBinding.read(stack);
        if (bound != null) { tooltip.add(I18n.translateToLocalFormatted("rdpl.portway.bound", Portways.label(bound))); }
    }
}

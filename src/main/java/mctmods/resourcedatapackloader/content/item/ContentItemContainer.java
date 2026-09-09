package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.gui.PackGuiHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentItemContainer extends ContentItem {
    private static Boolean baubles;
    private final ContainerDef holds;

    public ContentItemContainer(ItemDef def) {
        super(def);
        this.holds = def.holds == null ? new ContainerDef(1, 9, "", false, null, null, 0, 0, "") : def.holds;
        setMaxStackSize(1);
    }

    public ContainerDef holds() { return holds; }

    public static boolean baubled() {
        if (baubles == null) { baubles = net.minecraftforge.fml.common.Loader.isModLoaded("baubles"); }
        return baubles;
    }

    @Override @Nullable public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable net.minecraft.nbt.NBTTagCompound nbt) {
        if (holds.bauble.isEmpty() || !baubled()) { return super.initCapabilities(stack, nbt); }
        return mctmods.resourcedatapackloader.content.compat.BaublesPouch.provider(holds.bauble);
    }

    @Override @Nonnull public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (world.isRemote) { return new ActionResult<>(EnumActionResult.SUCCESS, held); }
        player.openGui(ResourceDataPackLoader.INSTANCE, hand == EnumHand.MAIN_HAND ? PackGuiHandler.POUCH_MAIN : PackGuiHandler.POUCH_OFF,
                world, 0, 0, 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, held);
    }
}

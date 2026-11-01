package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.gui.PackGuiHandler;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ContentContainerItem extends ContentItem {
    private static Boolean baubles;
    private final ContainerDef holds;

    public ContentContainerItem(ItemDef def) {
        super(def);
        this.holds = def.holds == null ? new ContainerDef(1, 9, "", false, null, null, 0, 0) : def.holds;
        setMaxStackSize(1);
    }

    public ContainerDef holds() { return holds; }

    public static boolean baubled() {
        if (baubles == null) { baubles = net.minecraftforge.fml.common.Loader.isModLoaded("baubles"); }
        return baubles;
    }

    @Override @Nonnull public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (world.isRemote) { return new ActionResult<>(EnumActionResult.SUCCESS, held); }
        player.openGui(ResourceDataPackLoader.INSTANCE, hand == EnumHand.MAIN_HAND ? PackGuiHandler.POUCH_MAIN : PackGuiHandler.POUCH_OFF,
                world, 0, 0, 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, held);
    }
}

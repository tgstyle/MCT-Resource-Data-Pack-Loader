package mctmods.resourcedatapackloader.content.dialog;

import mctmods.resourcedatapackloader.content.def.DialogDef;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public final class DialogEvents {
    private DialogEvents() {}

    @SubscribeEvent(priority = EventPriority.LOW) public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.isCanceled() || event.getHand() != EnumHand.MAIN_HAND || event.getWorld().isRemote || !(event.getEntityPlayer() instanceof EntityPlayerMP) || event.getEntityPlayer().isSneaking()) { return; }
        EntityVariantDef variant = ContentEntities.variantOf(event.getTarget());
        if (variant == null || variant.dialog.isEmpty()) { return; }
        DialogDef def = ContentDialogs.find(variant.dialog);
        if (def == null) { return; }
        DialogTalk.open((EntityPlayerMP) event.getEntityPlayer(), event.getTarget(), variant.name, def);
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { DialogTalk.forget(event.player.getUniqueID()); }
}

package mctmods.resourcedatapackloader.content.dialog;

import mctmods.resourcedatapackloader.content.def.DialogDef;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public final class DialogEvents {
    private DialogEvents() {}

    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.isCanceled() || event.getHand() != InteractionHand.MAIN_HAND || !(event.getEntity() instanceof ServerPlayer player) || player.isShiftKeyDown()) { return; }
        EntityVariantDef variant = ContentEntities.def(event.getTarget());
        if (variant == null || variant.dialog().isEmpty()) { return; }
        DialogDef def = ContentDialogs.find(variant.dialog());
        if (def == null) { return; }
        DialogTalk.open(player, event.getTarget(), variant.name(), def);
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { DialogTalk.forget(event.getEntity().getUUID()); }
}

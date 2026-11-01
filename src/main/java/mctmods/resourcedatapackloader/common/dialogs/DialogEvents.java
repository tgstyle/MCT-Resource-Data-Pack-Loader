package mctmods.resourcedatapackloader.common.dialogs;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.EntityVariantDef;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.DialogDef;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

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

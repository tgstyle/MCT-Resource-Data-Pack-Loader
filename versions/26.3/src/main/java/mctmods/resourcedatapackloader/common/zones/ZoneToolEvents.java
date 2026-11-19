package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneTool;
import mctmods.resourcedatapackloader.common.util.ItemUseGate;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.DataMapHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class ZoneToolEvents {
    private ZoneToolEvents() {}

    public static void register() { NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ZoneToolEvents::onClick); }

    private static void onClick(PlayerInteractEvent.RightClickBlock event) {
        Holder<BlockTransformer> transformer = event.getItemStack().get(DataComponents.BLOCK_TRANSFORMER);
        if (transformer == null || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        ZoneTool tool = of(transformer);
        ZoneDef zone = tool == null ? null : ZoneTools.denying(player, event.getPos(), tool);
        if (zone == null || !transforms(transformer, tool, player, event.getHand(), player.level(), event.getPos(), event.getFace())) { return; }
        ItemUseGate.denyUse(event, true);
        ZoneGuard.tell(player, zone);
    }

    @Nullable private static ZoneTool of(Holder<BlockTransformer> transformer) {
        if (transformer.is(BlockTransformers.HOE)) { return ZoneTool.HOE_TILL; }
        if (transformer.is(BlockTransformers.AXE)) { return ZoneTool.AXE_STRIP; }
        return transformer.is(BlockTransformers.SHOVEL) ? ZoneTool.SHOVEL_FLATTEN : null;
    }

    private static boolean transforms(Holder<BlockTransformer> transformer, ZoneTool tool, ServerPlayer player, InteractionHand hand, Level level, BlockPos pos, Direction face) {
        if (hand == InteractionHand.MAIN_HAND && player.getOffhandItem().has(DataComponents.BLOCKS_ATTACKS) && !player.isSecondaryUseActive()) { return false; }
        for (BlockTransformer.BlockTransformData data : DataMapHooks.getAllTransformers(transformer)) {
            if (!data.disallowedFaces().contains(face) && data.blockStateProvider().value().getOptionalState(level, level.getRandom(), pos) != null) { return tool != ZoneTool.AXE_STRIP || data.sound().is(SoundEvents.AXE_STRIP.key()); }
        }
        return false;
    }
}

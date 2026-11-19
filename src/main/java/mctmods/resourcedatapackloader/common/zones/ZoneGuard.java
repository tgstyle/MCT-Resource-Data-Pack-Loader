package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimProtection;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneListDef;
import mctmods.resourcedatapackloader.common.util.ItemUseGate;
import mctmods.resourcedatapackloader.common.util.TellThrottle;
import mctmods.resourcedatapackloader.common.util.world.PlacePositions;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

public final class ZoneGuard {
    private static final int TELL_EVERY = 40;
    private static final TellThrottle TOLD = new TellThrottle(TELL_EVERY);

    private ZoneGuard() {}

    public static void onBreak(BreakBlockEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player) || ZoneRules.none(player.level())) { return; }
        ItemUseGate.cancel(event, refuse(player, refusing(player, event.getPos(), ZoneDef::breaks, list -> list.refusesBlock(event.getState()))));
    }

    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || ZoneRules.none(player.level())) { return; }
        BlockState placed = event.getPlacedBlock();
        ItemUseGate.cancel(event, refuse(player, refusing(player, event.getPos(), ZoneDef::places, list -> list.refusesBlock(placed) || sitsOff(list, player.level(), event))));
    }

    public static void onUseItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack held = event.getItemStack();
        if (held.isEmpty() || !(event.getEntity() instanceof ServerPlayer player) || ZoneRules.none(player.level())) { return; }
        ItemUseGate.cancel(event, refuse(player, refusing(player, player.blockPosition(), ZoneDef::uses, list -> list.refusesItem(held))));
    }

    public static void onClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || ZoneRules.none(player.level())) { return; }
        ItemStack held = event.getItemStack();
        BlockPos pos = event.getPos();
        boolean used = !held.isEmpty() && refuse(player, refusing(player, pos, ZoneDef::uses, list -> list.refusesItem(held)));
        ItemUseGate.denyUse(event, used);
        Level level = player.level();
        BlockState state = level.getBlockState(pos);
        if (holdsNothing(level, pos, state)) { return; }
        if (refuse(player, refusing(player, pos, ZoneDef::opens, list -> list.refusesBlock(state)))) { ClaimProtection.deny(event); }
    }

    static boolean holdsNothing(Level level, BlockPos pos, BlockState state) { return state.getMenuProvider(level, pos) == null && !(level.getBlockEntity(pos) instanceof MenuProvider); }

    static void tell(ServerPlayer player, ZoneDef zone) {
        long now = player.level().getGameTime();
        String name = player.getGameProfile().name();
        if (!TOLD.ready(name, now)) { return; }
        TOLD.mark(name, now);
        player.sendOverlayMessage(Component.translatable(zone.message()));
    }

    private static boolean refuse(ServerPlayer player, @Nullable ZoneDef zone) {
        if (zone == null) { return false; }
        tell(player, zone);
        return true;
    }

    @Nullable private static ZoneDef refusing(ServerPlayer player, BlockPos pos, Function<ZoneDef, ZoneListDef> part, Predicate<ZoneListDef> refuses) {
        return ZoneRules.covering(player, pos, zone -> {
            ZoneListDef list = part.apply(zone);
            return list != null && refuses.test(list);
        });
    }

    private static boolean sitsOff(ZoneListDef list, Level level, BlockEvent.EntityPlaceEvent event) {
        for (BlockPos pos : PlacePositions.of(event)) {
            if (list.sitsOff(level.getBlockState(pos.below()))) { return true; }
        }
        return false;
    }
}

package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimProtection;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneListDef;
import mctmods.resourcedatapackloader.common.util.ItemUseGate;
import mctmods.resourcedatapackloader.common.util.TellThrottle;
import mctmods.resourcedatapackloader.common.util.world.PlacePositions;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;

public final class ZoneGuard {
    private static final int TELL_EVERY = 40;
    private static final TellThrottle TOLD = new TellThrottle(TELL_EVERY);

    private ZoneGuard() {}

    public static void onBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (ZoneRules.none(player.level())) { return; }
        ZoneDef zone = refusing(player, event.getPos(), ZoneDef::breaks, list -> list.refusesBlock(event.getState()));
        if (zone != null) {
            event.setCanceled(true);
            tell(player, zone);
        }
    }

    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player player) || ZoneRules.none(event.getLevel())) { return; }
        BlockState placed = event.getPlacedBlock();
        ZoneDef zone = refusing(player, event.getPos(), ZoneDef::places, list -> list.refusesBlock(placed) || sitsOff(list, event));
        if (zone != null) {
            event.setCanceled(true);
            tell(player, zone);
        }
    }

    public static void onUseItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack held = event.getItemStack();
        if (held.isEmpty() || ZoneRules.none(player.level())) { return; }
        ZoneDef zone = refusing(player, player.blockPosition(), ZoneDef::uses, list -> list.refusesItem(held));
        if (zone != null) {
            event.setCanceled(true);
            tell(player, zone);
        }
    }

    public static void onClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (ZoneRules.none(player.level())) { return; }
        ItemStack held = event.getItemStack();
        ZoneDef used = held.isEmpty() ? null : refusing(player, event.getPos(), ZoneDef::uses, list -> list.refusesItem(held));
        if (used != null) {
            ItemUseGate.denyUse(event, true);
            tell(player, used);
        }
        BlockState state = player.level().getBlockState(event.getPos());
        if (holdsNothing(player.level(), event.getPos(), state)) { return; }
        ZoneDef opened = refusing(player, event.getPos(), ZoneDef::opens, list -> list.refusesBlock(state));
        if (opened != null) {
            ClaimProtection.deny(event);
            tell(player, opened);
        }
    }

    static void tell(Player player, ZoneDef zone) {
        if (!(player instanceof ServerPlayer target)) { return; }
        long now = target.level().getGameTime();
        if (!TOLD.ready(target.getScoreboardName(), now)) { return; }
        TOLD.mark(target.getScoreboardName(), now);
        target.displayClientMessage(Component.translatable(zone.message()), true);
    }

    private static ZoneDef refusing(Player player, BlockPos pos, Function<ZoneDef, ZoneListDef> part, Predicate<ZoneListDef> refuses) {
        return ZoneRules.covering(player, pos, zone -> {
            ZoneListDef list = part.apply(zone);
            return list != null && refuses.test(list);
        });
    }

    private static boolean sitsOff(ZoneListDef list, BlockEvent.EntityPlaceEvent event) {
        for (BlockPos pos : PlacePositions.of(event)) {
            if (list.sitsOff(event.getLevel().getBlockState(pos.below()))) { return true; }
        }
        return false;
    }

    static boolean holdsNothing(Level level, BlockPos pos, BlockState state) { return state.getMenuProvider(level, pos) == null && !(level.getBlockEntity(pos) instanceof MenuProvider); }
}

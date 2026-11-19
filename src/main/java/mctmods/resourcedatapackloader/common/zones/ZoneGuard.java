package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimProtection;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneListDef;
import mctmods.resourcedatapackloader.common.util.ItemUseGate;
import mctmods.resourcedatapackloader.common.util.TellThrottle;
import mctmods.resourcedatapackloader.common.util.world.PlacePositions;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;

public final class ZoneGuard {
    private static final int TELL_EVERY = 40;
    private static final TellThrottle TOLD = new TellThrottle(TELL_EVERY);

    private ZoneGuard() {}

    @SubscribeEvent public static void onBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if (ZoneRules.none(player.world)) { return; }
        ZoneDef zone = refusing(player, event.getPos(), zoneDef -> zoneDef.breaks, list -> list.refusesBlock(event.getState()));
        if (zone != null) {
            event.setCanceled(true);
            tell(player, zone);
        }
    }

    @SubscribeEvent public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer) || ZoneRules.none(event.getWorld())) { return; }
        EntityPlayer player = (EntityPlayer) event.getEntity();
        IBlockState placed = event.getPlacedBlock();
        ZoneDef zone = refusing(player, event.getPos(), zoneDef -> zoneDef.places, list -> list.refusesBlock(placed) || sitsOff(list, event));
        if (zone != null) {
            event.setCanceled(true);
            tell(player, zone);
        }
    }

    @SubscribeEvent public static void onUseItem(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack held = event.getItemStack();
        if (held.isEmpty() || ZoneRules.none(player.world)) { return; }
        ZoneDef zone = refusing(player, player.getPosition(), zoneDef -> zoneDef.uses, list -> list.refusesItem(held));
        if (zone != null) {
            event.setCanceled(true);
            tell(player, zone);
        }
    }

    @SubscribeEvent public static void onClickBlock(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        if (ZoneRules.none(player.world)) { return; }
        ItemStack held = event.getItemStack();
        ZoneDef used = held.isEmpty() ? null : refusing(player, event.getPos(), zoneDef -> zoneDef.uses, list -> list.refusesItem(held));
        if (used != null) {
            ItemUseGate.denyUse(event, true);
            tell(player, used);
        }
        IBlockState state = player.world.getBlockState(event.getPos());
        if (holdsNothing(player.world, event.getPos(), state.getBlock())) { return; }
        ZoneDef opened = refusing(player, event.getPos(), zoneDef -> zoneDef.opens, list -> list.refusesBlock(state));
        if (opened != null) {
            ClaimProtection.deny(event);
            tell(player, opened);
        }
    }

    static void tell(EntityPlayer player, ZoneDef zone) {
        long now = player.world.getTotalWorldTime();
        if (!TOLD.ready(player.getName(), now)) { return; }
        TOLD.mark(player.getName(), now);
        player.sendStatusMessage(new TextComponentTranslation(zone.message), true);
    }

    private static ZoneDef refusing(EntityPlayer player, BlockPos pos, Function<ZoneDef, ZoneListDef> part, Predicate<ZoneListDef> refuses) {
        return ZoneRules.covering(player, pos, zone -> {
            ZoneListDef list = part.apply(zone);
            return list != null && refuses.test(list);
        });
    }

    private static boolean sitsOff(ZoneListDef list, BlockEvent.EntityPlaceEvent event) {
        for (BlockPos pos : PlacePositions.of(event)) {
            if (list.sitsOff(event.getWorld().getBlockState(pos.down()))) { return true; }
        }
        return false;
    }

    static boolean holdsNothing(World world, BlockPos pos, Block block) {
        if (block instanceof BlockWorkbench || block instanceof BlockAnvil) { return false; }
        TileEntity tile = world.getTileEntity(pos);
        return !(tile instanceof IInventory || (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)));
    }
}

package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneTool;
import mctmods.resourcedatapackloader.common.util.ItemUseGate;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSpade;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ZoneTools {
    private ZoneTools() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST) public static void onHoe(UseHoeEvent event) {
        World world = event.getWorld();
        if (ZoneRules.none(world)) { return; }
        Block block = world.getBlockState(event.getPos()).getBlock();
        if ((block != Blocks.GRASS && block != Blocks.GRASS_PATH && block != Blocks.DIRT) || !world.isAirBlock(event.getPos().up())) { return; }
        ZoneDef zone = ZoneRules.covering(world, event.getEntityPlayer(), event.getPos(), held -> held.denyTools.contains(ZoneTool.HOE_TILL));
        if (zone != null) {
            event.setCanceled(true);
            ZoneGuard.tell(event.getEntityPlayer(), zone);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST) public static void onUse(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        if (!(event.getItemStack().getItem() instanceof ItemSpade) || event.getFace() == EnumFacing.DOWN || ZoneRules.none(world)) { return; }
        if (world.getBlockState(event.getPos()).getBlock() != Blocks.GRASS || world.getBlockState(event.getPos().up()).getMaterial() != Material.AIR) { return; }
        ZoneDef zone = ZoneRules.covering(world, event.getEntityPlayer(), event.getPos(), held -> held.denyTools.contains(ZoneTool.SHOVEL_FLATTEN));
        if (zone != null) {
            ItemUseGate.denyUse(event, true);
            ZoneGuard.tell(event.getEntityPlayer(), zone);
        }
    }
}

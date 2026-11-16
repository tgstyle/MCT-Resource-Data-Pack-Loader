package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import mctmods.resourcedatapackloader.common.util.world.BlockFlags;
import mctmods.resourcedatapackloader.common.util.worldgen.FillRules;
import mctmods.resourcedatapackloader.common.worldgen.feature.shape.ContentImprint;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.DungeonSites;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Room;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPopulation;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentMapFill;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import java.util.Collections;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.BlockRotationProcessor;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

final class DungeonTemplates {
    private static final int FLAGS = BlockFlags.UPDATE_CLIENTS;

    private DungeonTemplates() {}

    static void write(World world, DungeonPlan plan, DungeonSites.Site site, StructureBoundingBox window, int low, int high) {
        FillRules rules = new FillRules(site.def.key, Collections.emptyList(), null);
        StructureBoundingBox box = new StructureBoundingBox(window.minX, low, window.minZ, window.maxX, high, window.maxZ);
        for (Level level : plan.levels) {
            for (Room room : level.rooms) {
                if (room.templateId == null || !box.intersectsWith(room.x0, room.z0, room.x1, room.z1) || room.y1 < low || room.y0 > high) { continue; }
                place(world, plan, site, level, room, rules, box);
            }
        }
    }

    private static void place(World world, DungeonPlan plan, DungeonSites.Site site, Level level, Room room, FillRules rules, StructureBoundingBox box) {
        Template template = ContentImprint.template(world, room.templateId);
        if (template == null) {
            ContentLog.LOGGER.onceWarn("Dungeon {} names template '{}', which could not be loaded, so its room is left empty", site.def.key, room.templateId);
            return;
        }
        Rotation turn = Rotation.values()[room.turn];
        BlockPos span = template.transformedSize(turn);
        PlacementSettings settings = new PlacementSettings();
        settings.setRotation(turn);
        settings.setRandom(new Random(room.seed));
        settings.setBoundingBox(box);
        BlockPos placeAt = new BlockPos(room.x0 + ContentImprint.backX(turn, span), room.floorY, room.z0 + ContentImprint.backZ(turn, span));
        DungeonPopulation.Draw draw = DungeonPopulation.draw(plan, site.def, level, room);
        if (draw == null) { template.addBlocksToWorld(world, placeAt, DungeonWriter.gated(new BlockRotationProcessor(placeAt, settings)), settings, FLAGS); }
        else { template.addBlocksToWorld(world, placeAt, DungeonWriter.gated(filler(rules, draw, room)), settings, FLAGS); }
    }

    private static ITemplateProcessor filler(FillRules rules, DungeonPopulation.Draw draw, Room room) { return (placed, at, info) -> fill(rules, draw, room, at, info); }

    private static Template.BlockInfo fill(FillRules rules, DungeonPopulation.Draw draw, Room room, BlockPos at, Template.BlockInfo info) {
        Block block = info.blockState.getBlock();
        boolean chest = block instanceof BlockChest;
        if (!chest && block != Blocks.MOB_SPAWNER) { return info; }
        long seed = MathUtil.mix(room.seed, at.getX(), at.getY(), at.getZ());
        if (chest) {
            String table = draw.loot(seed);
            return table == null ? info : ContentMapFill.fill(rules, seed, at, info, null, new ResourceLocation(table));
        }
        String mob = draw.mob(seed);
        if (mob == null) { return info; }
        int brace = mob.indexOf('{');
        Template.BlockInfo filled = ContentMapFill.fill(rules, seed, at, info, brace < 0 ? mob : mob.substring(0, brace), null);
        if (filled != info && brace >= 0 && filled.tileentityData != null) { DungeonWriter.merge(filled.tileentityData, mob.substring(brace), mob); }
        return filled;
    }
}

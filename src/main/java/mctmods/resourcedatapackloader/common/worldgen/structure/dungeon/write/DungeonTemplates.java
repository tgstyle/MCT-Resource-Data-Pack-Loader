package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

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
import mctmods.resourcedatapackloader.core.util.math.Turns;

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
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.Template;

final class DungeonTemplates {
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
        Rotation turn = Turns.ALL[room.turn];
        DungeonPopulation.Draw draw = DungeonPopulation.draw(plan, site.def, level, room);
        ContentImprint.place(world, template, room.x0, room.z0, room.floorY, turn, new Random(room.seed), box, draw == null ? null : filler(rules, draw, room), DungeonWriter.gate());
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
        return DungeonWriter.spawner(rules, seed, at, info, mob);
    }
}

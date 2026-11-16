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
import mctmods.resourcedatapackloader.core.util.LineCompat;
import mctmods.resourcedatapackloader.core.util.math.Hashes;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

final class DungeonTemplates {
    private static final int FLAGS = BlockFlags.UPDATE_CLIENTS_KNOWN_SHAPE;

    private DungeonTemplates() {}

    static void write(WorldGenLevel level, DungeonPlan plan, DungeonSites.Site site, BoundingBox window, int low, int high) {
        BoundingBox box = new BoundingBox(window.minX(), low, window.minZ(), window.maxX(), high, window.maxZ());
        for (Level held : plan.levels) {
            for (Room room : held.rooms) {
                if (room.templateId == null || !box.intersects(room.x0, room.z0, room.x1, room.z1) || room.y1 < low || room.y0 > high) { continue; }
                place(level, plan, site, held, room, box);
            }
        }
    }

    @Nullable static StructureTemplate load(WorldGenLevel level, @Nullable String id) {
        Identifier key = id == null ? null : Identifier.tryParse(id);
        Optional<StructureTemplate> found = key == null ? Optional.empty() : LineCompat.templates(level.getLevel().getServer()).get(key);
        return found.orElse(null);
    }

    static void place(WorldGenLevel level, StructureTemplate template, BlockPos at, Rotation turn, BoundingBox box, @Nullable StructureProcessor first, long seed) {
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(turn).setBoundingBox(box).setIgnoreEntities(true);
        for (StructureProcessor processor : DungeonWriter.gated(first)) {
            settings.addProcessor(processor);
        }
        template.placeInWorld(level, at, at, settings, RandomSource.create(seed), FLAGS);
    }

    private static void place(WorldGenLevel level, DungeonPlan plan, DungeonSites.Site site, Level held, Room room, BoundingBox box) {
        StructureTemplate template = load(level, room.templateId);
        if (template == null) {
            ContentLog.LOGGER.onceWarn("Dungeon {} names template '{}', which could not be loaded, so its room is left empty", site.def.key(), room.templateId);
            return;
        }
        Rotation turn = Rotation.values()[room.turn];
        Vec3i span = template.getSize(turn);
        BlockPos placeAt = new BlockPos(room.x0 + ContentImprint.backX(turn, span), room.floorY, room.z0 + ContentImprint.backZ(turn, span));
        DungeonPopulation.Draw draw = DungeonPopulation.draw(plan, site.def, held, room);
        place(level, template, placeAt, turn, box, new DungeonFillProcessor(new FillRules(site.def.key(), List.of(), null), draw, room.seed), room.seed);
    }

    static StructureTemplate.StructureBlockInfo fill(FillRules rules, DungeonPopulation.Draw draw, long roomSeed, StructureTemplate.StructureBlockInfo info) {
        Block block = info.state().getBlock();
        boolean chest = block instanceof ChestBlock;
        if (!chest && block != Blocks.SPAWNER) { return info; }
        BlockPos at = info.pos();
        long seed = Hashes.mix(roomSeed, at.getX(), at.getY(), at.getZ());
        if (chest) {
            String table = draw.loot(seed);
            return table == null ? info : ContentMapFill.fill(rules, seed, info, null, Identifier.parse(table));
        }
        String mob = draw.mob(seed);
        if (mob == null) { return info; }
        int brace = mob.indexOf('{');
        StructureTemplate.StructureBlockInfo filled = ContentMapFill.fill(rules, seed, info, brace < 0 ? mob : mob.substring(0, brace), null);
        if (filled != info && brace >= 0 && filled.nbt() != null) { DungeonWriter.merge(filled.nbt(), mob.substring(brace), mob); }
        return filled;
    }
}

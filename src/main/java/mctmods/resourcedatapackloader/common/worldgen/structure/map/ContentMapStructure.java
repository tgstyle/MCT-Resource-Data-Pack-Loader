package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.VillageDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.respawn.RespawnInstance;
import mctmods.resourcedatapackloader.common.worldgen.feature.shape.ContentImprint;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentWorldShape;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.Hashes;
import mctmods.resourcedatapackloader.core.util.math.Turns;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class ContentMapStructure extends Structure {
    public static final MapCodec<ContentMapStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(settingsCodec(instance),
            Identifier.CODEC.fieldOf("map").forGetter(held -> held.map)).apply(instance, ContentMapStructure::new));
    public static final StructureType<ContentMapStructure> TYPE = () -> CODEC;
    private final Identifier map;

    public ContentMapStructure(StructureSettings settings, Identifier map) {
        super(settings);
        this.map = map;
    }

    @Override @Nonnull protected Optional<GenerationStub> findGenerationPoint(@Nonnull GenerationContext context) {
        StructureMapDef def = ContentStructureMaps.def(map);
        if (def == null) { return Optional.empty(); }
        int windowX = context.chunkPos().getMinBlockX();
        int windowZ = context.chunkPos().getMinBlockZ();
        List<Spot> spots = spots(context, def, windowX, windowZ);
        if (spots.isEmpty()) { return Optional.empty(); }
        BlockPos origin = new BlockPos(windowX, context.chunkGenerator().getSeaLevel(), windowZ);
        return Optional.of(new GenerationStub(origin, builder -> {
            for (Spot spot : spots) { pieces(context, def, spot, windowX, windowZ, true, def.respawn() == null ? null : ContentMapSite.of(RespawnInstance.MAP, def.key().toString(), def, spot.seed(), spot.turn(), spot.x(), spot.base(), spot.z()), builder); }
        }));
    }

    private static List<Spot> spots(GenerationContext context, StructureMapDef def, int windowX, int windowZ) {
        int window = StructureMapDef.window();
        int widest = def.widest();
        long salted = context.seed() ^ def.key().hashCode();
        List<Spot> spots = new ArrayList<>();
        if (def.at() != null && reaches(def.at()[0], def.at()[1], windowX, windowZ, window, widest)) { spots.add(spot(context, def, Hashes.mix(salted, def.at()[0], 0, def.at()[1]), def.at()[0], def.at()[1])); }
        if (def.spacing() <= 0) { return spots; }
        int pitch = def.spacing() * 16;
        int give = Math.max(1, pitch - widest);
        for (int gridX = Math.floorDiv(windowX - widest, pitch); gridX <= Math.floorDiv(windowX + window - 1, pitch); gridX++) {
            for (int gridZ = Math.floorDiv(windowZ - widest, pitch); gridZ <= Math.floorDiv(windowZ + window - 1, pitch); gridZ++) {
                long seed = Hashes.mix(salted, gridX, 1, gridZ);
                if (Math.floorMod(seed >>> 16, 100) >= def.chance()) { continue; }
                int originX = gridX * pitch + Math.floorMod(seed, give);
                int originZ = gridZ * pitch + Math.floorMod(seed >>> 40, give);
                if (reaches(originX, originZ, windowX, windowZ, window, widest)) { spots.add(spot(context, def, seed, originX, originZ)); }
            }
        }
        return spots;
    }

    private static boolean reaches(int originX, int originZ, int windowX, int windowZ, int window, int widest) { return originX + widest > windowX && originX < windowX + window && originZ + widest > windowZ && originZ < windowZ + window; }

    private static Spot spot(GenerationContext context, StructureMapDef def, long seed, int originX, int originZ) {
        Rotation turn = Turns.ALL[Math.floorMod(seed >>> 8, Turns.ALL.length)];
        boolean swapped = turn == Rotation.CLOCKWISE_90 || turn == Rotation.COUNTERCLOCKWISE_90;
        int wide = def.cellsWide() * def.cell();
        int deep = def.cellsDeep() * def.cell();
        int spanX = swapped ? deep : wide;
        int spanZ = swapped ? wide : deep;
        int anchor = ContentWorldShape.surfaceAt(context, originX + spanX / 2, originZ + spanZ / 2);
        return new Spot(seed, originX, originZ, turn, Math.max(def.baseY(anchor, seed), context.heightAccessor().getMinY() + 1));
    }

    public static void lay(GenerationContext context, StructureMapDef def, long seed, Rotation turn, int x, int z, int base, @Nullable VillageDef village, StructurePiecesBuilder builder) { pieces(context, def, new Spot(seed, x, z, turn, base), x, z, false, village == null || village.respawn() == null ? null : ContentMapSite.of(RespawnInstance.PLOT, village.key().toString(), def, seed, turn, x, base, z), builder); }

    private static void pieces(GenerationContext context, StructureMapDef def, Spot spot, int windowX, int windowZ, boolean windowed, @Nullable ContentMapSite site, StructurePiecesBuilder builder) {
        int window = StructureMapDef.window();
        int placed = ContentMapCells.each(def, spot.seed(), spot.turn(), spot.x(), spot.base(), spot.z(), (cornerX, cornerZ) -> !windowed || cornerX >= windowX && cornerX < windowX + window && cornerZ >= windowZ && cornerZ < windowZ + window, cell -> {
            Optional<StructureTemplate> piece = context.structureTemplateManager().get(cell.template());
            if (piece.isEmpty()) {
                ContentStructureMaps.missing(def, cell.named());
                return false;
            }
            Vec3i span = piece.get().getSize(spot.turn());
            if (Math.max(span.getX(), span.getZ()) > StructureMapDef.CELL_MOST) { ContentStructureMaps.oversize(def, cell.named(), Math.max(span.getX(), span.getZ())); }
            builder.addPiece(new ContentMapPiece(context.structureTemplateManager(), cell.template(), spot.turn(), new BlockPos(cell.cornerX() + ContentImprint.backX(spot.turn(), span), cell.y(), cell.cornerZ() + ContentImprint.backZ(spot.turn(), span)), def.key(), cell.seed(), site));
            return true;
        });
        if (placed > 0 && spot.x() >= windowX && spot.x() < windowX + window && spot.z() >= windowZ && spot.z() < windowZ + window) {
            int floor = spot.base() + def.ground() * def.cell();
            ContentLog.LOGGER.debug("Structure map {} builds at {}, {}, {}, turned {}, its ground layer floored at y {}", def.key(), spot.x(), floor, spot.z(), spot.turn(), floor);
        }
    }

    @Override @Nonnull public StructureType<?> type() { return TYPE; }

    public Identifier map() { return map; }

    private record Spot(long seed, int x, int z, Rotation turn, int base) {}
}

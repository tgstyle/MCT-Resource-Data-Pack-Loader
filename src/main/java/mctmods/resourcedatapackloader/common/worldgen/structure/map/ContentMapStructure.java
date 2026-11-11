package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.VillageDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.respawn.RespawnInstance;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentWorldShape;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.Hashes;
import mctmods.resourcedatapackloader.core.util.math.Turns;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public final class ContentMapStructure extends Structure {
    public static final Codec<ContentMapStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(settingsCodec(instance),
            ResourceLocation.CODEC.fieldOf("map").forGetter(held -> held.map)).apply(instance, ContentMapStructure::new));
    public static final StructureType<ContentMapStructure> TYPE = () -> CODEC;
    private final ResourceLocation map;

    public ContentMapStructure(StructureSettings settings, ResourceLocation map) {
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
            for (Spot spot : spots) { pieces(context, def, spot, windowX, windowZ, true, builder, null); }
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
        return new Spot(seed, originX, originZ, turn, Math.max(def.baseY(anchor, seed), context.heightAccessor().getMinBuildHeight() + 1));
    }

    public static void lay(GenerationContext context, StructureMapDef def, long seed, Rotation turn, int x, int z, int base, StructurePiecesBuilder builder, @Nullable VillageDef owner) { pieces(context, def, new Spot(seed, x, z, turn, base), x, z, false, builder, owner); }

    @Nullable private static ContentMapSite site(StructureMapDef def, Spot spot, @Nullable VillageDef owner) {
        if ((owner == null ? def.respawn() : owner.respawn()) == null) { return null; }
        BoundingBox area = ContentMapCells.box(def, spot.turn(), spot.x(), spot.base(), spot.z());
        return new ContentMapSite(owner == null ? RespawnInstance.MAP : RespawnInstance.PLOT, (owner == null ? def.key() : owner.key()).toString(), spot.seed(), new BlockPos(spot.x(), spot.base(), spot.z()), area);
    }

    private static void pieces(GenerationContext context, StructureMapDef def, Spot spot, int windowX, int windowZ, boolean windowed, StructurePiecesBuilder builder, @Nullable VillageDef owner) {
        int window = StructureMapDef.window();
        ContentMapSite site = site(def, spot, owner);
        BoundingBox corners = windowed ? new BoundingBox(windowX, 0, windowZ, windowX + window - 1, 0, windowZ + window - 1) : null;
        int placed = ContentMapCells.walk(context.structureTemplateManager(), def, spot.seed(), spot.turn(), spot.x(), spot.base(), spot.z(), corners, cell -> builder.addPiece(new ContentMapPiece(context.structureTemplateManager(), cell.template(), spot.turn(), cell.at(), def.key(), cell.seed(), site)));
        if (placed > 0 && spot.x() >= windowX && spot.x() < windowX + window && spot.z() >= windowZ && spot.z() < windowZ + window) {
            int floor = spot.base() + def.ground() * def.cell();
            ContentLog.LOGGER.debug("Structure map {} builds at {}, {}, {}, turned {}, its ground layer floored at y {}", def.key(), spot.x(), floor, spot.z(), spot.turn(), floor);
        }
    }

    @Override @Nonnull public StructureType<?> type() { return TYPE; }

    public ResourceLocation map() { return map; }

    private record Spot(long seed, int x, int z, Rotation turn, int base) {}
}

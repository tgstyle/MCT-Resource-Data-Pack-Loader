package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraftforge.common.world.PieceBeardifierModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import javax.annotation.Nonnull;

public final class ContentCityDeckPiece extends StructurePiece implements PieceBeardifierModifier {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityDeckPiece::new;
    private static final int REACH = 8;
    private static final String SEAT = "Seat";
    private static final String KEEP = "Keep";
    private static final String STRIP = "Strip";
    private static final String DECK = "Deck";
    private static final String DECKED = "Decked";
    private final int seat;
    private final int[] keep;
    private final int[] strip;
    private final int[] deck;
    private final byte[] decked;

    public ContentCityDeckPiece(int seat, int[] keep, int[] strip, int[] deck, byte[] decked, int head) {
        super(TYPE, 0, reach(seat, strip, deck, head));
        this.seat = seat;
        this.keep = keep;
        this.strip = strip;
        this.deck = deck;
        this.decked = decked;
    }

    public ContentCityDeckPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.seat = tag.getInt(SEAT);
        this.keep = tag.getIntArray(KEEP);
        this.strip = tag.getIntArray(STRIP);
        this.deck = tag.getIntArray(DECK);
        this.decked = tag.getByteArray(DECKED);
    }

    private static BoundingBox reach(int seat, int[] strip, int[] deck, int head) {
        int low = seat;
        int high = seat;
        for (int at = head; at < deck.length; at++) {
            if (deck[at] == Integer.MIN_VALUE) { continue; }
            low = Math.min(low, deck[at]);
            high = Math.max(high, deck[at]);
        }
        if (deck.length <= head) { return new BoundingBox(strip[0], low - REACH, strip[1], strip[2], high + REACH, strip[3]); }
        boolean alongX = deck[0] == 1;
        int first = deck[1];
        int last = first + deck.length - head - 1;
        return alongX ? new BoundingBox(first, low - REACH, strip[1], last, high + REACH, strip[3]) : new BoundingBox(strip[0], low - REACH, first, strip[2], high + REACH, last);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(SEAT, seat);
        tag.putIntArray(KEEP, keep);
        tag.putIntArray(STRIP, strip);
        tag.putIntArray(DECK, deck);
        tag.putByteArray(DECKED, decked);
    }

    @Override @Nonnull public BoundingBox getBeardifierBox() { return getBoundingBox(); }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return TerrainAdjustment.NONE; }

    @Override public int getGroundLevelDelta() { return 0; }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.within(level, box, () -> {
            CityPlotDeck.approach(level, box, seat, strip);
            CityPlotDeck.deck(level, box, strip, deck, decked, keep);
        });
    }
}

package mctmods.resourcedatapackloader.client.renderer;

import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.environment.SeasonState;
import mctmods.resourcedatapackloader.common.items.ContentSeasonClockItem;

import com.mojang.serialization.MapCodec;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;

public final class ContentSeasonClockProperty implements RangeSelectItemModelProperty {
    private static final ContentSeasonClockProperty YEAR = new ContentSeasonClockProperty(SeasonState::yearProgress);
    private static final ContentSeasonClockProperty SEASON = new ContentSeasonClockProperty(SeasonState::index);
    private static final MapCodec<ContentSeasonClockProperty> YEAR_CODEC = MapCodec.unit(YEAR);
    private static final MapCodec<ContentSeasonClockProperty> SEASON_CODEC = MapCodec.unit(SEASON);
    private final ToDoubleFunction<SeasonState> value;

    private ContentSeasonClockProperty(ToDoubleFunction<SeasonState> value) { this.value = value; }

    public static void register(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(Identifier.fromNamespaceAndPath("rdpl", "year"), YEAR_CODEC);
        event.register(Identifier.fromNamespaceAndPath("rdpl", "season"), SEASON_CODEC);
    }

    @Override public float get(@Nonnull ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        if (level == null) { return 0.0F; }
        Identifier own = level.dimension().identifier();
        Identifier dimension = stack.getItem() instanceof ContentSeasonClockItem item ? item.clock().dimension() : null;
        SeasonState state = ContentSeasons.state(dimension, own);
        return state == null ? 0.0F : (float) value.applyAsDouble(state);
    }

    @Override @Nonnull public MapCodec<ContentSeasonClockProperty> type() { return this == YEAR ? YEAR_CODEC : SEASON_CODEC; }
}

package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;

public class ContentLogBlock extends RotatedPillarBlock {
    private static final int DEFAULT_FLAMMABILITY = 5;
    private static final int DEFAULT_SPREAD = 5;
    private final BlockDef def;

    public ContentLogBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    public BlockDef getDef() { return def; }

    private int flammability() { return def.flammability() > 0 ? def.flammability() : "wood".equals(def.material()) ? DEFAULT_FLAMMABILITY : 0; }

    @Override public boolean isFlammable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return flammability() > 0; }

    @Override public int getFlammability(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return flammability(); }

    @Override public int getFireSpreadSpeed(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.fireSpread() > 0 ? def.fireSpread() : flammability() > 0 ? DEFAULT_SPREAD : 0; }
}

package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import javax.annotation.Nullable;

public final class VillageRule {
    public static final int ALWAYS = 100;
    private final IBlockState from;
    private final IBlockState to;
    private final int chance;
    private final IBlockState at;
    private final IBlockState under;

    public VillageRule(IBlockState from, IBlockState to, int chance, @Nullable IBlockState at, @Nullable IBlockState under) {
        this.from = from;
        this.to = to;
        this.chance = chance;
        this.at = at;
        this.under = under;
    }

    public boolean plain() { return chance >= ALWAYS && at == null && under == null; }

    public IBlockState replacement() { return to; }

    public IBlockState original() { return from; }

    @Nullable public IBlockState apply(World world, BlockPos pos, IBlockState laid) {
        if (differs(laid, from)) { return null; }
        if (at != null && differs(world.getBlockState(pos), at)) { return null; }
        if (under != null && differs(world.getBlockState(pos.down()), under)) { return null; }
        if (chance < ALWAYS && SeededRandom.at(world, pos.getX(), pos.getY(), pos.getZ()).nextInt(ALWAYS) >= chance) { return null; }
        return to;
    }

    private static boolean differs(IBlockState held, IBlockState wanted) {
        if (held == wanted) { return false; }
        return wanted != wanted.getBlock().getDefaultState() || held.getBlock() != wanted.getBlock();
    }
}

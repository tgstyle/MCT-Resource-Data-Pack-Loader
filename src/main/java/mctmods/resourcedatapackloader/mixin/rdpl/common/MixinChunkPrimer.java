package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.world.chunk.ChunkPrimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Arrays;

@Mixin(ChunkPrimer.class) public abstract class MixinChunkPrimer {
    @Unique private static final int RDPL_SLOTS = 32;
    @Unique private final IBlockState[] rdpl$states = new IBlockState[RDPL_SLOTS];
    @Unique private final int[] rdpl$ids = new int[RDPL_SLOTS];
    @Unique private final IBlockState[] rdpl$byValue = new IBlockState[RDPL_SLOTS];
    @Unique private final int[] rdpl$values = new int[RDPL_SLOTS];
    @Unique private boolean rdpl$valuesReady;

    @Redirect(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ObjectIntIdentityMap;get(Ljava/lang/Object;)I"))
    private int rdpl$idOfRun(ObjectIntIdentityMap<IBlockState> ids, Object key) {
        int slot = System.identityHashCode(key) & RDPL_SLOTS - 1;
        if (rdpl$states[slot] == key) { return rdpl$ids[slot]; }
        if (ContentChunkWatch.watching()) { ContentChunkWatch.primerLookup(); }
        int id = ids.get((IBlockState) key);
        rdpl$states[slot] = (IBlockState) key;
        rdpl$ids[slot] = id;
        return id;
    }

    @Redirect(method = "getBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ObjectIntIdentityMap;getByValue(I)Ljava/lang/Object;"))
    private Object rdpl$stateOfRun(ObjectIntIdentityMap<IBlockState> ids, int value) {
        if (!rdpl$valuesReady) {
            Arrays.fill(rdpl$values, -1);
            rdpl$valuesReady = true;
        }
        int slot = value & RDPL_SLOTS - 1;
        if (rdpl$values[slot] == value) { return rdpl$byValue[slot]; }
        if (ContentChunkWatch.watching()) { ContentChunkWatch.primerLookup(); }
        IBlockState found = ids.getByValue(value);
        rdpl$values[slot] = value;
        rdpl$byValue[slot] = found;
        return found;
    }
}

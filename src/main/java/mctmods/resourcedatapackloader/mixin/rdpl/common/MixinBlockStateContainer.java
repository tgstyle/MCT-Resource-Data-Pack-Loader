package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.world.chunk.BlockStateContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockStateContainer.class) public abstract class MixinBlockStateContainer {
    @Unique private static final int RDPL_SLOTS = 32;
    @Unique private final IBlockState[] rdpl$states = new IBlockState[RDPL_SLOTS];
    @Unique private final int[] rdpl$ids = new int[RDPL_SLOTS];

    @Redirect(method = "getDataForNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ObjectIntIdentityMap;get(Ljava/lang/Object;)I"))
    private int rdpl$idOfRun(ObjectIntIdentityMap<IBlockState> ids, Object key) {
        int slot = System.identityHashCode(key) & RDPL_SLOTS - 1;
        if (rdpl$states[slot] == key) { return rdpl$ids[slot]; }
        if (ContentChunkWatch.watching()) { ContentChunkWatch.blockIdLookup(); }
        int id = ids.get((IBlockState) key);
        rdpl$states[slot] = (IBlockState) key;
        rdpl$ids[slot] = id;
        return id;
    }
}

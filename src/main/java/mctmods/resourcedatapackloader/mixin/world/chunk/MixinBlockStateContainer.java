package mctmods.resourcedatapackloader.mixin.world.chunk;

import mctmods.resourcedatapackloader.common.pregen.ContentChunkWatch;
import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.util.worldgen.StateIdCache;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.world.chunk.BlockStateContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockStateContainer.class) public abstract class MixinBlockStateContainer {
    @Unique private final StateIdCache rdpl$ids = new StateIdCache();

    @Redirect(method = "getDataForNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ObjectIntIdentityMap;get(Ljava/lang/Object;)I")) private int rdpl$idOfRun(ObjectIntIdentityMap<IBlockState> ids, Object key) {
        IBlockState state = (IBlockState) key;
        int id = rdpl$ids.held(state);
        if (id != StateIdCache.MISS) { return id; }
        if (ContentChunkWatch.watching()) { ContentChunkWatch.blockIdLookup(); }
        return rdpl$ids.remember(state, ids.get(state));
    }

    @ModifyArg(method = "onResize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/BlockStateContainer;setBits(I)V"), index = 0) private int rdpl$growOnceNotFourTimes(int bits) { return Math.max(bits, 8); }

    @ModifyArg(method = "<init>()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/BlockStateContainer;setBits(I)V"), index = 0) private int rdpl$roomWhileMaking(int bits) { return ContentPregen.busy() && !ContentPregen.lightingOnly() ? Math.max(bits, 8) : bits; }
}

package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.interfaces.ILightAreaHolder;
import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;
import mctmods.resourcedatapackloader.content.worldgen.ContentLightArea;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.pathfinding.PathWorldListener;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.ServerWorldEventHandler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(World.class) public abstract class MixinWorldLight implements ILightAreaHolder {
    @Shadow protected List<IWorldEventListener> eventListeners;
    @Unique private ContentLightArea rdpl$area;
    @Unique private int rdpl$quietLight = -1;

    @Override public ContentLightArea rdpl$lightArea() { return rdpl$area; }

    @Override public void rdpl$setLightArea(ContentLightArea area) { rdpl$area = area; }

    @Unique private static final ThreadLocal<Long> rdpl$spreadStart = ThreadLocal.withInitial(() -> 0L);

    @Inject(method = "addEventListener", at = @At("RETURN")) private void rdpl$listenerAdded(IWorldEventListener listener, CallbackInfo ci) { rdpl$quietLight = -1; }

    @Inject(method = "removeEventListener", at = @At("RETURN")) private void rdpl$listenerRemoved(IWorldEventListener listener, CallbackInfo ci) { rdpl$quietLight = -1; }

    @Override public boolean rdpl$quietLight() { return rdpl$nobodyListening(); }

    @SuppressWarnings({"ConstantValue", "ConstantConditions"}) @Unique private boolean rdpl$nobodyListening() {
        int known = rdpl$quietLight;
        if (known >= 0) { return known == 1; }
        boolean quiet = rdpl$onlyTheGameListening();
        rdpl$quietLight = quiet ? 1 : 0;
        if (!quiet && !((World) (Object) this).isRemote) { rdpl$nameTheListeners(); }
        return quiet;
    }

    @Unique private boolean rdpl$onlyTheGameListening() {
        for (IWorldEventListener listener : eventListeners) {
            if (!rdpl$deaf(listener)) { return false; }
        }
        return true;
    }

    @Unique private static boolean rdpl$deaf(IWorldEventListener listener) {
        Class<?> kind = listener.getClass();
        return kind == ServerWorldEventHandler.class || kind == PathWorldListener.class || "vazkii.quark.world.world.event.RaveEventListener".equals(kind.getName());
    }

    @Unique private void rdpl$nameTheListeners() {
        StringBuilder who = new StringBuilder();
        for (IWorldEventListener listener : eventListeners) {
            if (who.length() > 0) { who.append(", "); }
            who.append(listener.getClass().getName());
        }
        ContentLog.LOGGER.debug("Light changes will keep being announced, something besides the game is listening: {}", who);
    }

    @Redirect(method = "setLightFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;notifyLightSet(Lnet/minecraft/util/math/BlockPos;)V"))
    private void rdpl$tellNobody(World world, BlockPos pos) {
        if (rdpl$nobodyListening()) { return; }
        world.notifyLightSet(pos);
    }

    @Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true) private void rdpl$lightAfterwards(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        World world = (World) (Object) this;
        if (world.isRemote) { return; }
        if (ContentPregen.quenches(world, pos.getX() >> 4, pos.getZ() >> 4)) {
            if (world.isBlockLoaded(pos)) { world.getChunk(pos).setLightPopulated(false); }
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "checkLightFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isAreaLoaded(Lnet/minecraft/util/math/BlockPos;IZ)Z"))
    private boolean rdpl$alreadyKnown(World world, BlockPos center, int radius, boolean allowEmpty) {
        int known = ContentLightArea.answer(world, center, radius);
        if (known != ContentLightArea.UNKNOWN) { return known == ContentLightArea.YES; }
        return world.isAreaLoaded(center, radius, allowEmpty);
    }

    @Redirect(method = {"checkLightFor", "getRawLight"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getLightFor(Lnet/minecraft/world/EnumSkyBlock;Lnet/minecraft/util/math/BlockPos;)I"))
    private int rdpl$lightNearby(World world, EnumSkyBlock type, BlockPos pos) {
        Chunk chunk = ContentLightArea.at(world, pos);
        if (chunk == null) { return world.getLightFor(type, pos); }
        if (pos.getY() < 0) { return chunk.getLightFor(type, new BlockPos(pos.getX(), 0, pos.getZ())); }
        if (pos.getY() >= 256) { return type.defaultLightValue; }
        return chunk.getLightFor(type, pos);
    }

    @Redirect(method = {"checkLightFor", "getRawLight"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;"))
    private IBlockState rdpl$stateNearby(World world, BlockPos pos) {
        Chunk chunk = ContentLightArea.at(world, pos);
        if (chunk == null || pos.getY() < 0 || pos.getY() >= 256) { return world.getBlockState(pos); }
        return chunk.getBlockState(pos);
    }

    @Redirect(method = "checkLightFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setLightFor(Lnet/minecraft/world/EnumSkyBlock;Lnet/minecraft/util/math/BlockPos;I)V"))
    private void rdpl$writeNearby(World world, EnumSkyBlock type, BlockPos pos, int lightValue) {
        Chunk chunk = ContentLightArea.at(world, pos);
        if (chunk == null || pos.getY() < 0 || pos.getY() >= 256) {
            world.setLightFor(type, pos, lightValue);
            return;
        }
        chunk.setLightFor(type, pos, lightValue);
        if (!rdpl$nobodyListening()) { world.notifyLightSet(pos); }
    }

    @Redirect(method = "getRawLight", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;canSeeSky(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean rdpl$skyNearby(World world, BlockPos pos) {
        Chunk chunk = ContentLightArea.at(world, pos);
        if (chunk == null) { return world.canSeeSky(pos); }
        return chunk.canSeeSky(pos);
    }

    @Redirect(method = {"checkLightFor", "getRawLight"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnumFacing;values()[Lnet/minecraft/util/EnumFacing;"))
    private EnumFacing[] rdpl$sidesWithoutCopying() { return EnumFacing.VALUES; }

    @Inject(method = "checkLightFor", at = @At("HEAD")) private void rdpl$startSpread(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        rdpl$spreadStart.set(ContentChunkWatch.timingThisOne() ? System.nanoTime() : 0L);
    }

    @Inject(method = "checkLightFor", at = @At("RETURN")) private void rdpl$endSpread(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ContentChunkWatch.spread(rdpl$spreadStart.get(), lightType == EnumSkyBlock.SKY);
    }
}

package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.entity.IRubicEntityTracker;
import mctmods.resourcedatapackloader.content.rubic.server.PlayerCubeMap;
import mctmods.resourcedatapackloader.content.rubic.server.interfaces.IRubicPlayerList;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class) @Implements(@Interface(iface = IRubicPlayerList.class, prefix = "list$")) public abstract class MixinPlayerList {
    @Shadow private int viewDistance;
    @Shadow @Final private MinecraftServer server;
    @Unique protected int rdpl$verticalViewDistance = -1;

    public int list$getVerticalViewDistance() { return rdpl$verticalViewDistance < 0 ? viewDistance : rdpl$verticalViewDistance; }

    public void list$setVerticalViewDistance(int dist) {
        this.rdpl$verticalViewDistance = dist;
        if (this.server.worlds != null) {
            for (WorldServer worldserver : this.server.worlds) {
                if (worldserver != null && ((IRubicWorld) worldserver).rdpl$isRubicWorld()) {
                    ((PlayerCubeMap) worldserver.getPlayerChunkMap()).setPlayerViewDistance(viewDistance, dist);
                    ((IRubicEntityTracker) worldserver.getEntityTracker()).setVertViewDistance(dist);
                }
            }
        }
    }

    @Redirect(method = "playerLoggedOut",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;markDirty()V", ordinal = 0),
            require = 1)
    private void setChunkModifiedOnPlayerLoggedOut(Chunk chunkIn, EntityPlayerMP playerIn) {
        IRubicWorldInternal world = (IRubicWorldInternal) playerIn.getServerWorld();
        if (world.rdpl$isRubicWorld()) { world.rdpl$getCubeFromCubeCoords(playerIn.chunkCoordX, playerIn.chunkCoordY, playerIn.chunkCoordZ).markDirty(); }
        else { ((World) world).getChunk(playerIn.chunkCoordX, playerIn.chunkCoordZ).markDirty(); }
    }

    @Inject(method = "recreatePlayerEntity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/gen/ChunkProviderServer;provideChunk(II)Lnet/minecraft/world/chunk/Chunk;"))
    private void createPlayerChunk(EntityPlayerMP playerIn, int dimension, boolean conqueredEnd, CallbackInfoReturnable<EntityPlayerMP> cir) {
        if (!((IRubicWorld) playerIn.world).rdpl$isRubicWorld()) { return; }
        for (int dCubeY = -8; dCubeY <= 8; dCubeY++) { ((IRubicWorld) playerIn.world).rdpl$getCubeFromBlockCoords(playerIn.getPosition().up(Coords.cubeToMinBlock(dCubeY))); }
    }

    @ModifyConstant(method = "recreatePlayerEntity",
            constant = @Constant(doubleValue = 256))
    private double rdpl$getMaxHeight(double _256, EntityPlayerMP playerIn, int dimension, boolean conqueredEnd) {
        if (!playerIn.world.isBlockLoaded(new BlockPos(playerIn))) { return Double.NEGATIVE_INFINITY; }
        return ((IRubicWorld) playerIn.world).rdpl$getMaxHeight();
    }
}

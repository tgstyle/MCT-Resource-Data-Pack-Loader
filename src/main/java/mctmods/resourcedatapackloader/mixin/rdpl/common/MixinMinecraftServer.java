package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.server.SpawnCubes;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnChunks;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class) public abstract class MixinMinecraftServer {
    @Unique private int rdpl$bootReach;

    @Redirect(method = "updateTimeLightAndEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Ljava/lang/String;)Z"))
    private boolean rdpl$holdTheSkyStill(GameRules rules, String name) {
        if ("doDaylightCycle".equals(name)) {
            for (WorldServer world : ((MinecraftServer) (Object) this).worlds) {
                if (ContentPregen.busyIn(world)) { return false; }
            }
        }
        return rules.getBoolean(name);
    }

    @Inject(method = "initialWorldChunkLoad", at = @At("HEAD")) private void onInitialSpawnLoad(CallbackInfo ci) {
        rdpl$bootReach = ContentSpawnChunks.radius(0) > 0 ? ContentSpawnChunks.chunks(0) + 4 : -1;
        World world = DimensionManager.getWorld(0);
        if (((IRubicWorld) world).rdpl$isRubicWorld()) {
            ((IRubicWorldInternal.IServer) world).rdpl$setSpawnArea(new SpawnCubes());
            ((IRubicWorldInternal.IServer) world).rdpl$getSpawnArea().update(world);
        }
    }

    @ModifyConstant(method = "initialWorldChunkLoad", constant = @Constant(intValue = -192)) private int rdpl$bootFrom(int was) { return -16 * rdpl$bootReach; }

    @ModifyConstant(method = "initialWorldChunkLoad", constant = @Constant(intValue = 192)) private int rdpl$bootTo(int was) { return 16 * rdpl$bootReach; }

    @ModifyConstant(method = "initialWorldChunkLoad", constant = @Constant(intValue = 625)) private int rdpl$bootCount(int was) { return rdpl$bootPrepared(); }

    @Inject(method = "initialWorldChunkLoad", at = @At("TAIL")) private void rdpl$bootDone(CallbackInfo ci) {
        if (rdpl$bootReach < 0) { ContentLog.LOGGER.info("Prepared no chunks around the spawn point as the world started, as spawnChunkRadius 0 asks"); }
        else { ContentLog.LOGGER.info("Prepared {} chunk(s) around the spawn point as the world started, {} each way from the spawn chunk", rdpl$bootPrepared(), rdpl$bootReach); }
    }

    @Unique private int rdpl$bootPrepared() {
        int across = 2 * rdpl$bootReach + 1;
        return rdpl$bootReach < 0 ? 0 : across * across;
    }

    @Shadow public WorldServer[] worlds;

    @Inject(method = "getBuildLimit", at = @At("HEAD"), cancellable = true) private void rubic$buildLimit(CallbackInfoReturnable<Integer> cir) {
        if (this.worlds == null || this.worlds.length == 0 || this.worlds[0] == null) { return; }
        if (((IRubicWorld) this.worlds[0]).rdpl$isRubicWorld()) { cir.setReturnValue(Rubic.MAX_SUPPORTED_BLOCK_Y); }
    }
}

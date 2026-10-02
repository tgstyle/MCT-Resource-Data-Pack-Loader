package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.server.SpawnCubes;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class) public abstract class MixinMinecraftServer {
    @Inject(method = "initialWorldChunkLoad", at = @At("HEAD")) private void onInitialSpawnLoad(CallbackInfo ci) {
        World world = DimensionManager.getWorld(0);
        if (((IRubicWorld) world).rdpl$isRubicWorld()) {
            ((IRubicWorldInternal.IServer) world).rdpl$setSpawnArea(new SpawnCubes());
            ((IRubicWorldInternal.IServer) world).rdpl$getSpawnArea().update(world);
        }
    }

    @Shadow public WorldServer[] worlds;

    @Inject(method = "getBuildLimit", at = @At("HEAD"), cancellable = true) private void rubic$buildLimit(CallbackInfoReturnable<Integer> cir) {
        if (this.worlds == null || this.worlds.length == 0 || this.worlds[0] == null) { return; }
        if (((IRubicWorld) this.worlds[0]).rdpl$isRubicWorld()) { cir.setReturnValue(Rubic.MAX_SUPPORTED_BLOCK_Y); }
    }
}

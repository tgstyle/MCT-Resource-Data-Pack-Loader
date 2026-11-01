package mctmods.resourcedatapackloader.mixin.world;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensionRules;
import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.rules.ContentRuleSwitch;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentTerrain;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class) public abstract class MixinWorldServerContent {
    @WrapOperation(method = "createChunkProvider", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;createChunkGenerator()Lnet/minecraft/world/gen/IChunkGenerator;")) private IChunkGenerator rdpl$existingGenerator(WorldProvider provider, Operation<IChunkGenerator> original) { return ContentDimensionRules.existingGenerator((World) (Object) this, () -> original.call(provider)); }

    @WrapOperation(method = "updateBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canDoLightning(Lnet/minecraft/world/chunk/Chunk;)Z", remap = false)) private boolean rdpl$existingLightning(WorldProvider provider, Chunk chunk, Operation<Boolean> original) { return ContentDimensionRules.existingLightning((World) (Object) this, original.call(provider, chunk)); }

    @WrapOperation(method = "updateBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canDoRainSnowIce(Lnet/minecraft/world/chunk/Chunk;)Z", remap = false)) private boolean rdpl$existingPrecipitation(WorldProvider provider, Chunk chunk, Operation<Boolean> original) { return ContentDimensionRules.existingPrecipitation((World) (Object) this, original.call(provider, chunk)); }

    @WrapOperation(method = "createSpawnPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canRespawnHere()Z")) private boolean rdpl$existingRespawn(WorldProvider provider, Operation<Boolean> original) { return ContentDimensionRules.existingRespawn(provider, original.call(provider)); }

    @WrapOperation(method = "createSpawnPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getAverageGroundLevel()I")) private int rdpl$existingGroundLevel(WorldProvider provider, Operation<Integer> original) { return ContentDimensionRules.existingGroundLevel((World) (Object) this, original.call(provider)); }

    @Inject(method = "createSpawnPosition", at = @At("RETURN")) private void rdpl$spawnWhereAsked(WorldSettings settings, CallbackInfo ci) {
        String wanted = ContentTerrain.worldSpawn();
        if (wanted.isEmpty()) { return; }

        WorldServer self = (WorldServer) (Object) this;
        BlockPos asked = ContentTerrain.spawnFrom(wanted, self.provider.getAverageGroundLevel());
        if (asked == null) { return; }

        self.getWorldInfo().setSpawn(asked);
        Summary.info("terrain.spawn", "Spawning every new world at " + asked.getX() + ", " + asked.getY() + ", " + asked.getZ() + ", which is what a pack asks for");
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;setWorldTime(J)V")) private void rdpl$holdTimeWhileMaking(WorldServer world, long time) {
        if (ContentPregen.busyIn(world)) { return; }
        world.setWorldTime(ContentRuleSwitch.woken(world, time));
    }

    @Inject(method = "updateWeather", at = @At("HEAD"), cancellable = true) private void rdpl$holdWeatherWhileMaking(CallbackInfo ci) {
        if (ContentPregen.busyIn((WorldServer) (Object) this)) { ci.cancel(); }
    }

    @Inject(method = "initialize", at = @At("RETURN")) private void rdpl$borderAsAsked(WorldSettings settings, CallbackInfo ci) {
        int wanted = ContentTerrain.worldBorder();
        if (wanted <= 0) { return; }
        if (wanted > Config.worldgen.worldBorderLimit) {
            ContentLog.LOGGER.error("A pack asks for a world border {} block(s) across, which is past the {} allowed by worldBorderLimit, so the border is left where the game puts it", wanted, Config.worldgen.worldBorderLimit);
            return;
        }
        WorldServer self = (WorldServer) (Object) this;
        self.getWorldInfo().setBorderSize(wanted);
        self.getWorldBorder().setTransition(wanted);
        Summary.info("terrain.border", "Standing the world border " + wanted + " block(s) across in every new world, which is what a pack asks for");
    }
}

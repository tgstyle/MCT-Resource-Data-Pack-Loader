package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class) public abstract class MixinWorldServerContent {
    @Inject(method = "createSpawnPosition", at = @At("RETURN")) private void rdpl$spawnWhereAsked(WorldSettings settings, CallbackInfo ci) {
        String wanted = ContentTerrain.worldSpawn();
        if (wanted.isEmpty()) { return; }

        WorldServer self = (WorldServer) (Object) this;
        BlockPos asked = ContentTerrain.spawnFrom(wanted, self.provider.getAverageGroundLevel());
        if (asked == null) { return; }

        self.getWorldInfo().setSpawn(asked);
        Summary.info("terrain.spawn", "Spawning every new world at " + asked.getX() + ", " + asked.getY() + ", " + asked.getZ() + ", which is what a pack asks for");
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;setWorldTime(J)V"))
    private void rdpl$holdTimeWhileMaking(WorldServer world, long time) {
        if (ContentPregen.busyIn(world)) { return; }
        world.setWorldTime(time);
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

package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public abstract class MixinWorldServerSpawn {
    @Inject(method = "createSpawnPosition", at = @At("RETURN"))
    private void rdpl$spawnWhereAsked(WorldSettings settings, CallbackInfo ci) {
        String wanted = ContentTerrain.worldSpawn();
        if (wanted.isEmpty()) { return; }

        WorldServer self = (WorldServer) (Object) this;
        BlockPos asked = ContentTerrain.spawnFrom(wanted, self.provider.getAverageGroundLevel());
        if (asked == null) { return; }

        self.getWorldInfo().setSpawn(asked);
        Summary.info("terrain.spawn", "Spawning every new world at " + asked.getX() + ", " + asked.getY() + ", " + asked.getZ() + ", which is what a pack asks for");
    }
}

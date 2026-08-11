package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(MapGenScatteredFeature.class)
public abstract class MixinMapGenScatteredFeature {
    @Shadow private int maxDistanceBetweenScatteredFeatures;
    @Shadow @Final private List<Biome.SpawnListEntry> monsters;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void rdpl$spacing(CallbackInfo ci) {
        int asked = ContentStructurePlacement.spacing(ContentStructurePlacement.TEMPLES, maxDistanceBetweenScatteredFeatures);
        if (asked < 9) {
            ContentLog.LOGGER.warn("structureSpacing asks for temples every {} chunk(s), but the game works out where a temple stands by counting back eight chunks, so anything under nine leaves it nothing to count. Nine is used instead", asked);
            asked = 9;
        }
        maxDistanceBetweenScatteredFeatures = asked;
        ContentStructurePlacement.spawns(ContentStructurePlacement.TEMPLES, monsters);
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true)
    private void rdpl$pinned(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (ContentStructurePlacement.pinned(ContentStructurePlacement.TEMPLES, chunkX, chunkZ)) { cir.setReturnValue(true); }
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("RETURN"), cancellable = true)
    private void rdpl$placement(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) { return; }

        World world = ((AccessorMapGenBase) this).rdpl$getWorld();
        if (!ContentStructurePlacement.allows(ContentStructurePlacement.TEMPLES, world, chunkX, chunkZ)) {
            cir.setReturnValue(false);
            return;
        }
        if (ContentBeard.wanted() && ContentBeard.roughGround(world, chunkX * 16 + 8, chunkZ * 16 + 8, rdpl$reach(world, chunkX, chunkZ), 6)) { cir.setReturnValue(false); }
    }

    @Unique private int rdpl$reach(World world, int chunkX, int chunkZ) {
        Biome biome = world.getBiomeProvider().getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8));
        if (biome == Biomes.DESERT || biome == Biomes.DESERT_HILLS) { return 10; }
        if (biome == Biomes.JUNGLE || biome == Biomes.JUNGLE_HILLS) { return 7; }

        return 4;
    }
}

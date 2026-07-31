package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorHell;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChunkGeneratorHell.class)
public abstract class MixinChunkGeneratorHell {
    @Shadow @Final private World world;

    @Inject(method = "getPossibleCreatures", at = @At("HEAD"), cancellable = true)
    private void rdpl$biomeOwnsSpawns(EnumCreatureType creatureType, BlockPos pos, CallbackInfoReturnable<List<Biome.SpawnListEntry>> cir) {
        if (creatureType != EnumCreatureType.MONSTER) { return; }
        if (!ContentStructures.blocks(world, "Fortress")) { return; }

        cir.setReturnValue(world.getBiome(pos).getSpawnableList(creatureType));
    }
}

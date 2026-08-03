package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;
import mctmods.resourcedatapackloader.content.worldgen.ContentFreezeCheck;

import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Random;

@Mixin(ChunkGeneratorOverworld.class)
public abstract class MixinChunkGeneratorOverworldIce {
    @Redirect(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/terraingen/TerrainGen;populate(Lnet/minecraft/world/gen/IChunkGenerator;Lnet/minecraft/world/World;Ljava/util/Random;IIZLnet/minecraftforge/event/terraingen/PopulateChunkEvent$Populate$EventType;)Z"), remap = false)
    private boolean rdpl$skipIceWhereNothingFreezes(IChunkGenerator chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated, PopulateChunkEvent.Populate.EventType type) {
        boolean wanted = TerrainGen.populate(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, type);
        if (!wanted || type != PopulateChunkEvent.Populate.EventType.ICE) { return wanted; }
        if (ContentFreezeCheck.couldFreeze(world, chunkX, chunkZ)) { return true; }

        ContentChunkWatch.warmChunk();

        return false;
    }
}

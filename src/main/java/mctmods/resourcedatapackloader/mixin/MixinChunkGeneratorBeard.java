package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;

import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.WoodlandMansion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGeneratorOverworld.class)
public abstract class MixinChunkGeneratorBeard {
    @Shadow @Final private World world;
    @Shadow @Final private boolean mapFeaturesEnabled;
    @Shadow @Final private double[] heightMap;
    @Shadow private MapGenVillage villageGenerator;
    @Shadow private MapGenStronghold strongholdGenerator;
    @Shadow private MapGenMineshaft mineshaftGenerator;
    @Shadow private StructureOceanMonument oceanMonumentGenerator;
    @Shadow private WoodlandMansion woodlandMansionGenerator;

    @Inject(method = "generateHeightmap", at = @At("RETURN"))
    private void rdpl$seatStructures(int x, int y, int z, CallbackInfo ci) {
        if (!mapFeaturesEnabled || !ContentBeard.wanted()) { return; }

        MapGenStructure[] generators = { villageGenerator, strongholdGenerator, mineshaftGenerator, oceanMonumentGenerator, woodlandMansionGenerator };
        String[] names = { "villages", "strongholds", "mineshafts", "monuments", "mansions" };
        ContentBeard.apply(world, (ChunkGeneratorOverworld) (Object) this, generators, names, heightMap, x / 4, z / 4);
    }
}

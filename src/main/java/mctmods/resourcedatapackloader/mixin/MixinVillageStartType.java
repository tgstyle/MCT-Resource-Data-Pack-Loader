package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import java.util.Random;

@Mixin(StructureVillagePieces.Start.class)
public abstract class MixinVillageStartType {
    @Inject(method = "<init>(Lnet/minecraft/world/biome/BiomeProvider;ILjava/util/Random;IILjava/util/List;I)V", at = @At("RETURN"))
    private void rdpl$packBiomeVillageType(BiomeProvider biomeProviderIn, int p_i2104_2_, Random rand, int p_i2104_4_, int p_i2104_5_, List<StructureVillagePieces.PieceWeight> p_i2104_6_, int p_i2104_7_, CallbackInfo ci) {
        Biome held = ((StructureVillagePieces.Start) (Object) this).biome;
        if (!(held instanceof ContentBiome)) { return; }

        int kind = ((ContentBiome) held).getDef().villageType;
        if (kind < 0) { return; }

        ((AccessorVillagePiece) this).rdpl$setStructureType(kind);
    }
}

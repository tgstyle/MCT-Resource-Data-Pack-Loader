package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.WoodlandMansion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(WoodlandMansion.class) public abstract class MixinWoodlandMansion {
    @Mutable @Shadow @Final public static List<Biome> ALLOWED_BIOMES;
    @Unique private static boolean rdpl$biomesFiltered;

    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD")) private void rdpl$widenBiomes(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (rdpl$biomesFiltered) { return; }
        rdpl$biomesFiltered = true;
        ALLOWED_BIOMES = ContentStructurePlacement.filtered(ContentStructurePlacement.MANSIONS, ALLOWED_BIOMES);
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true) private void rdpl$pinned(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (ContentStructurePlacement.pinned(ContentStructurePlacement.MANSIONS, chunkX, chunkZ)) { cir.setReturnValue(true); }
    }

    @ModifyConstant(method = "canSpawnStructureAtCoords", constant = @Constant(intValue = 80)) private int rdpl$spacing(int original) { return ContentStructurePlacement.spacing(ContentStructurePlacement.MANSIONS, original); }

    @ModifyConstant(method = "canSpawnStructureAtCoords", constant = @Constant(intValue = 79)) private int rdpl$offset(int original) { return ContentStructurePlacement.spacing(ContentStructurePlacement.MANSIONS, original + 1) - 1; }

    @ModifyConstant(method = "canSpawnStructureAtCoords", constant = @Constant(intValue = 60)) private int rdpl$separation(int original) { return ContentStructurePlacement.separation(ContentStructurePlacement.MANSIONS, original); }

    @ModifyConstant(method = "getNearestStructurePos", constant = @Constant(intValue = 80)) private int rdpl$locateSpacing(int original) { return ContentStructurePlacement.spacing(ContentStructurePlacement.MANSIONS, original); }

    @ModifyConstant(method = "getNearestStructurePos", constant = @Constant(intValue = 20)) private int rdpl$locateSeparation(int original) { return ContentStructurePlacement.separation(ContentStructurePlacement.MANSIONS, original); }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("RETURN"), cancellable = true) private void rdpl$placement(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) { return; }
        World world = ((IMapGenBase) this).rdpl$getWorld();
        if (!ContentStructurePlacement.allows(ContentStructurePlacement.MANSIONS, world, chunkX, chunkZ)) {
            cir.setReturnValue(false);
            return;
        }
        if (ContentBeard.wanted() && ContentBeard.roughGround(world, chunkX * 16 + 40, chunkZ * 16 + 40, 32, 6)) { cir.setReturnValue(false); }
    }
}

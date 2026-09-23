package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.village.CityLayout;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRailsFit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Random;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.List;

@Mixin(MapGenVillage.Start.class) public abstract class MixinVillageStart {
    @Inject(method = "<init>(Lnet/minecraft/world/World;Ljava/util/Random;III)V", at = @At("HEAD"))
    private static void rdpl$holdTheRoads(World worldIn, Random rand, int x, int z, int size, CallbackInfo ci) {
        if (ContentBeard.wanted() && CityLayout.wanted()) { CityLayout.laying(true); }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;Ljava/util/Random;III)V", at = @At("TAIL"))
    private void rdpl$foundAtBirth(World worldIn, Random rand, int x, int z, int size, CallbackInfo ci) {
        CityLayout.laying(false);
        if (ContentBeard.wanted()) { ContentBeard.foundAtBirth(worldIn, (StructureStart) (Object) this); }
    }

    @WrapOperation(method = "<init>(Lnet/minecraft/world/World;Ljava/util/Random;III)V", at = @At(value = "NEW", target = "(Lnet/minecraft/world/biome/BiomeProvider;ILjava/util/Random;IILjava/util/List;I)Lnet/minecraft/world/gen/structure/StructureVillagePieces$Start;"))
    private StructureVillagePieces.Start rdpl$wellOnPin(BiomeProvider biomeProviderIn, int p_i2104_2_, Random rand, int p_i2104_4_, int p_i2104_5_, List<StructureVillagePieces.PieceWeight> p_i2104_6_, int p_i2104_7_, Operation<StructureVillagePieces.Start> original, @Local(argsOnly = true, ordinal = 0) int x, @Local(argsOnly = true, ordinal = 1) int z) {
        long[] pin = ContentStructurePlacement.pinIn(ContentStructurePlacement.VILLAGES, x, z);
        if (pin != null) { return original.call(biomeProviderIn, p_i2104_2_, rand, (int) pin[0] - 2, (int) pin[1] - 2, p_i2104_6_, p_i2104_7_); }
        return original.call(biomeProviderIn, p_i2104_2_, rand, p_i2104_4_, p_i2104_5_, p_i2104_6_, p_i2104_7_);
    }

    @Redirect(method = "<init>(Lnet/minecraft/world/World;Ljava/util/Random;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/StructureVillagePieces$Start;buildComponent(Lnet/minecraft/world/gen/structure/StructureComponent;Ljava/util/List;Ljava/util/Random;)V"))
    private void rdpl$sizeThenBuild(StructureVillagePieces.Start start, StructureComponent componentIn, List<StructureComponent> listIn, Random building, World worldIn, Random rand, int x, int z, int size) {
        if (ContentBeard.wanted()) {
            ContentVillages.sizeBlock(worldIn, start);
            BeardRailsFit.found(worldIn, StructureStart.class.cast(this), start, rand);
        }
        start.buildComponent(componentIn, listIn, building);
    }

    @Redirect(method = "<init>(Lnet/minecraft/world/World;Ljava/util/Random;III)V", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(I)Ljava/lang/Object;"))
    private Object rdpl$nearestFirst(List<StructureComponent> pending, int index) {
        StructureStart self = StructureStart.class.cast(this);
        if (!ContentBeard.wanted() || self.getComponents().isEmpty()) { return pending.remove(index); }
        StructureBoundingBox well = self.getComponents().get(0).getBoundingBox();
        int wellX = (well.minX + well.maxX) / 2;
        int wellZ = (well.minZ + well.maxZ) / 2;
        int nearest = index;
        int closest = Integer.MAX_VALUE;
        for (int i = 0; i < pending.size(); i++) {
            StructureBoundingBox box = pending.get(i).getBoundingBox();
            int away = Math.abs((box.minX + box.maxX) / 2 - wellX) + Math.abs((box.minZ + box.maxZ) / 2 - wellZ);
            if (away < closest) {
                closest = away;
                nearest = i;
            }
        }
        return pending.remove(nearest);
    }
}

package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.village.CityLayout;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Random;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
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

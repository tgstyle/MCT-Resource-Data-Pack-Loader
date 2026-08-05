package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StructureVillagePieces.Well.class)
public abstract class MixinVillageWell extends StructureVillagePieces.Village {
    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/StructureVillagePieces$Well;getAverageGroundLevel(Lnet/minecraft/world/World;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)I"))
    private int rdpl$lowestGround(StructureVillagePieces.Well well, World worldIn, StructureBoundingBox structurebb) {
        int found = getAverageGroundLevel(worldIn, structurebb);
        if (!ContentBeard.wanted() || found < 0) { return found; }

        StructureBoundingBox box = getBoundingBox();
        int lowest = ContentBeard.lowestIn(worldIn, box.minX - 1, box.minZ - 1, box.maxX + 1, box.maxZ + 1, structurebb);
        if (lowest == Integer.MAX_VALUE) { return found; }

        ContentLog.LOGGER.debug("{} measured its ground at y {} and builds at y {}, so its rim sits flush with the lowest ground touching it", getClass().getSimpleName(), found, lowest - 1);
        return lowest - 1;
    }

}

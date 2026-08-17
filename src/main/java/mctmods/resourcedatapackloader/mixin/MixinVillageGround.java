package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureVillagePieces.Village.class)
public abstract class MixinVillageGround extends StructureComponent {
    @SuppressWarnings({"ConstantConditions"}) @Inject(method = "getAverageGroundLevel", at = @At("RETURN"), cancellable = true)
    private void rdpl$leanLow(World worldIn, StructureBoundingBox structurebb, CallbackInfoReturnable<Integer> cir) {
        if (!ContentBeard.wanted()) { return; }

        int found = cir.getReturnValueI();
        if (found < 0) { return; }

        StructureBoundingBox box = getBoundingBox();
        int average = ContentBeard.noiseAverage(worldIn, box);
        boolean wet = average != Integer.MIN_VALUE && average < worldIn.getSeaLevel();
        if (wet) { average = worldIn.getSeaLevel(); }
        if (average != Integer.MIN_VALUE) {
            if (average != found) { cir.setReturnValue(average); }
            ContentLog.LOGGER.debug("{} at {}, {} measures its ground at y {} from the noise surface{}", getClass().getSimpleName(), box.minX, box.minZ, average, average == found ? ", agreeing with the world" : " instead of y " + found);
            found = average;
        }
        if (!((Object) this instanceof StructureVillagePieces.Well)) {
            int grade = BeardRoads.roadGradeBeside(worldIn, box);
            if (grade != Integer.MIN_VALUE) {
                int seat = BeardPlots.waystone(this) ? grade : grade - 1;
                if (wet && seat < worldIn.getSeaLevel()) {
                    seat = worldIn.getSeaLevel();
                    ContentLog.LOGGER.debug("{} at {}, {} stands on water, so it is held up to the surface at y {} instead of sinking a course below the road", getClass().getSimpleName(), box.minX, box.minZ, seat);
                }
                if (seat != found) {
                    cir.setReturnValue(seat);
                    ContentLog.LOGGER.debug("{} at {}, {} stands at the grade of the road beside it, y {}, instead of y {}", getClass().getSimpleName(), box.minX, box.minZ, seat, found);
                }
                return;
            }
        }
        if ((Object) this instanceof StructureVillagePieces.Well || (Object) this instanceof StructureVillagePieces.Field1 || (Object) this instanceof StructureVillagePieces.Field2) { return; }

        int lowest = ContentBeard.lowestIn(worldIn, box.minX, box.minZ, box.maxX, box.maxZ, structurebb);
        if (lowest == Integer.MAX_VALUE || found <= lowest + 3) { return; }

        int leaned = rdpl$roadClamped(lowest + 3, box);
        if (found <= leaned) { return; }

        cir.setReturnValue(leaned);
        ContentLog.LOGGER.debug("{} at {}, {} leaned from y {} down to y {} over its low side", getClass().getSimpleName(), box.minX, box.minZ, found, leaned);
    }

    @Unique private int rdpl$roadClamped(int leaned, StructureBoundingBox box) {
        StructureStart start = ContentBeard.current();
        if (start == null) { return leaned; }

        StructureComponent self = this;
        for (StructureComponent other : start.getComponents()) {
            if (other == self || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            if (road.minX - 4 > box.maxX || box.minX - 4 > road.maxX || road.minZ - 4 > box.maxZ || box.minZ - 4 > road.maxZ) { continue; }
            if (road.minY > leaned) { leaned = road.minY; }
        }
        return leaned;
    }
}

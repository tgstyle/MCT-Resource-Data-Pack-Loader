package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Random;

@Mixin(StructureStart.class)
public abstract class MixinStructureStart {
    @Redirect(method = "generateStructure", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/StructureComponent;addComponentParts(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)Z"))
    private boolean rdpl$openFrontage(StructureComponent piece, World world, Random rand, StructureBoundingBox clip) {
        StructureStart self = StructureStart.class.cast(this);
        ContentBeard.building(self);
        boolean built;
        try { built = piece.addComponentParts(world, rand, clip); }
        finally { ContentBeard.building(null); }
        if (built && ContentBeard.wanted()) { ContentBeard.openAround(self, piece, world, clip); }

        return built;
    }
}

package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardKeep;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Random;

@Mixin(StructureStart.class)
public abstract class MixinStructureStart {
    @Redirect(method = "generateStructure", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/StructureComponent;addComponentParts(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)Z"))
    private boolean rdpl$openFrontage(StructureComponent piece, World world, Random rand, StructureBoundingBox clip) {
        StructureStart self = StructureStart.class.cast(this);
        ContentBeard.building(self);
        boolean built;
        try {
            if (ContentBeard.wanted()) {
                ContentBeard.attach(self, piece);
                ContentBeard.fellFor(self, piece, world, clip);
                BeardKeep.watch(world, piece, clip);
            }
            built = piece.addComponentParts(world, rand, clip);
            if (!built && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("{} at {}, {} told the game it is done while chunk {}, {} was being built, so the game now drops it and no later chunk will build or dress it again", piece.getClass().getSimpleName(), piece.getBoundingBox().minX, piece.getBoundingBox().minZ, clip.minX >> 4, clip.minZ >> 4); }
            if (ContentBeard.wanted()) { BeardKeep.learn(world); }
            if (built && ContentBeard.wanted()) { ContentBeard.openAround(self, piece, world, clip); }
        }
        finally { ContentBeard.building(null); }

        return built;
    }

    @Inject(method = "generateStructure", at = @At("RETURN"))
    private void rdpl$plaza(World worldIn, Random rand, StructureBoundingBox structurebb, CallbackInfo ci) {
        StructureStart self = StructureStart.class.cast(this);
        if (!ContentBeard.wanted() || self.getComponents().isEmpty()) { return; }

        StructureComponent well = self.getComponents().get(0);
        if (!(well instanceof StructureVillagePieces.Start)) { return; }

        ContentBeard.building(self);
        try { ContentBeard.wellPlaza(self, well, worldIn, structurebb); }
        finally { ContentBeard.building(null); }
    }
}

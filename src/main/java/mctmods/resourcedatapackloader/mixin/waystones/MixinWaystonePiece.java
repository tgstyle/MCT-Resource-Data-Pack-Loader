package mctmods.resourcedatapackloader.mixin.waystones;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.blay09.mods.waystones.worldgen.ComponentVillageWaystone;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ComponentVillageWaystone.class)
public abstract class MixinWaystonePiece extends StructureVillagePieces.Village {
    @Inject(method = "<init>(Lnet/minecraft/world/gen/structure/StructureVillagePieces$Start;ILnet/minecraft/world/gen/structure/StructureBoundingBox;Lnet/minecraft/util/EnumFacing;)V", at = @At("RETURN"))
    private void rdpl$trimToTemplate(StructureVillagePieces.Start start, int type, StructureBoundingBox boundingBox, EnumFacing facing, CallbackInfo ci) {
        if (!ContentBeard.wanted() || this.boundingBox == null || facing == null) { return; }

        int wide = this.boundingBox.maxX - this.boundingBox.minX + 1;
        int deep = this.boundingBox.maxZ - this.boundingBox.minZ + 1;
        if (wide <= 5 && deep <= 5) { return; }

        int wasMinX = this.boundingBox.minX;
        int wasMinZ = this.boundingBox.minZ;
        if (wide > 5) {
            if (facing == EnumFacing.WEST) { this.boundingBox.minX = this.boundingBox.maxX - 4; }
            else { this.boundingBox.maxX = this.boundingBox.minX + 4; }
        }
        if (deep > 5) {
            if (facing == EnumFacing.NORTH) { this.boundingBox.minZ = this.boundingBox.maxZ - 4; }
            else { this.boundingBox.maxZ = this.boundingBox.minZ + 4; }
        }
        ContentLog.LOGGER.debug("The waystone at {}, {} claims a {}x{} plot for a 5x5 template, so its box is trimmed to the template and held against the road it faces away from, now {}, {} to {}, {}", wasMinX, wasMinZ, wide, deep, this.boundingBox.minX, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.maxZ);
    }
}

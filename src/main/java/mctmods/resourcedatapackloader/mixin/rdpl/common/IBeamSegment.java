package mctmods.resourcedatapackloader.mixin.rdpl.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.tileentity.TileEntityBeacon;

@Mixin(TileEntityBeacon.BeamSegment.class) public interface IBeamSegment {
    @Accessor("height") int rdpl$getHeight();

    @Accessor("height") void rdpl$setHeight(int height);
}

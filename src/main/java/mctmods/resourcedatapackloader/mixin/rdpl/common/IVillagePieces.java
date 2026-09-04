package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

@Mixin(StructureVillagePieces.class) public interface IVillagePieces {
    @Nullable @Invoker("generateAndAddRoadPiece") static StructureComponent rdpl$roadPiece(StructureVillagePieces.Start start, List<StructureComponent> pieces, Random rand, int x, int y, int z, EnumFacing facing, int type) { throw new Error("IVillagePieces failed to apply"); }
}

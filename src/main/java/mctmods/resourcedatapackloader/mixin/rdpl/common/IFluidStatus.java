package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Aquifer.FluidStatus.class) public interface IFluidStatus {
    @Accessor("fluidLevel") int rdpl$getFluidLevel();

    @Accessor("fluidType") BlockState rdpl$getFluidType();
}

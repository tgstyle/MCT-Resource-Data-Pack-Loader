package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.BlockStateBase.class) public interface IBlockStateBase {
    @Accessor("destroySpeed") float rdpl$getDestroySpeed();

    @Accessor("destroySpeed") @Mutable void rdpl$setDestroySpeed(float speed);

    @Accessor("lightEmission") int rdpl$getLightEmission();

    @Accessor("lightEmission") @Mutable void rdpl$setLightEmission(int light);

    @Accessor("requiresCorrectToolForDrops") @Mutable void rdpl$setRequiresCorrectToolForDrops(boolean requires);
}

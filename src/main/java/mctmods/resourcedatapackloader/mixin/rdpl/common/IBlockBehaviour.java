package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.class) public interface IBlockBehaviour {
    @Accessor("explosionResistance") float rdpl$getExplosionResistance();

    @Accessor("explosionResistance") @Mutable void rdpl$setExplosionResistance(float resistance);

    @Accessor("friction") float rdpl$getFriction();

    @Accessor("friction") @Mutable void rdpl$setFriction(float friction);

    @Accessor("soundType") SoundType rdpl$getSoundType();

    @Accessor("soundType") @Mutable void rdpl$setSoundType(SoundType soundType);
}

package mctmods.resourcedatapackloader.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface AccessorBlock {
    @Accessor("blockHardness") float rdpl$getHardness();

    @Accessor("blockHardness") void rdpl$setHardness(float hardness);

    @Accessor("blockResistance") float rdpl$getResistance();

    @Accessor("blockResistance") void rdpl$setResistance(float resistance);

    @Accessor("lightValue") int rdpl$getLightValue();

    @Accessor("lightValue") void rdpl$setLightValue(int light);

    @Accessor("lightOpacity") int rdpl$getLightOpacity();

    @Accessor("slipperiness") float rdpl$getSlipperiness();

    @Accessor("slipperiness") void rdpl$setSlipperiness(float slipperiness);

    @Accessor("blockSoundType") SoundType rdpl$getSoundType();

    @Accessor("blockSoundType") void rdpl$setSoundType(SoundType soundType);

    @Accessor("material") @Mutable void rdpl$setMaterial(Material material);

    @Accessor("blockMapColor") @Mutable void rdpl$setMapColor(MapColor color);
}

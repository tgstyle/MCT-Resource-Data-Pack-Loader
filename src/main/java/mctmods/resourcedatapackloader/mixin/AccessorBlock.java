package mctmods.resourcedatapackloader.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface AccessorBlock {
    @Accessor("blockHardness") void rdpl$setHardness(float hardness);

    @Accessor("blockResistance") void rdpl$setResistance(float resistance);

    @Accessor("material") @Mutable void rdpl$setMaterial(Material material);

    @Accessor("blockMapColor") @Mutable void rdpl$setMapColor(MapColor color);
}

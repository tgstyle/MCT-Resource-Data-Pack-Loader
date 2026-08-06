package mctmods.resourcedatapackloader.mixin;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface AccessorBlock {
    @Accessor("blockHardness") void rdpl$setHardness(float hardness);

    @Accessor("blockResistance") void rdpl$setResistance(float resistance);
}

package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(BlockFire.class) public interface IBlockFire {
    @Accessor("encouragements") Map<Block, Integer> rdpl$getEncouragements();

    @Accessor("flammabilities") Map<Block, Integer> rdpl$getFlammabilities();
}

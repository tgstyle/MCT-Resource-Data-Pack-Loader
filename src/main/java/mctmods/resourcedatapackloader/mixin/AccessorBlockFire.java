package mctmods.resourcedatapackloader.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(BlockFire.class)
public interface AccessorBlockFire {
    @Accessor("encouragements") Map<Block, Integer> rdpl$getEncouragements();

    @Accessor("flammabilities") Map<Block, Integer> rdpl$getFlammabilities();
}

package mctmods.resourcedatapackloader.mixin.rdpl.common;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FireBlock.class) public interface IFireBlock {
    @Accessor("igniteOdds") Object2IntMap<Block> rdpl$getIgniteOdds();

    @Accessor("burnOdds") Object2IntMap<Block> rdpl$getBurnOdds();
}

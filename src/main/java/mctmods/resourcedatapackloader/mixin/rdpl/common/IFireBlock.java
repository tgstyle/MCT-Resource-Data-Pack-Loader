package mctmods.resourcedatapackloader.mixin.rdpl.common;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireBlock.class) public interface IFireBlock {
    @Accessor("igniteOdds") Object2IntMap<Block> rdpl$getIgniteOdds();

    @Accessor("burnOdds") Object2IntMap<Block> rdpl$getBurnOdds();

    @Invoker("setFlammable") void rdpl$setFlammable(Block block, int encouragement, int flammability);
}

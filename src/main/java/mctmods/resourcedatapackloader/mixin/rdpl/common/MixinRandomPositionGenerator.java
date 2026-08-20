package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(RandomPositionGenerator.class) public class MixinRandomPositionGenerator {
	/**
	 * @author tgstyle
	 * @reason Bound the climb above solid blocks by the world height and loaded blocks so it terminates on cube worlds.
	 */
	@Overwrite private static BlockPos moveAboveSolid(BlockPos pos, EntityCreature entity) {
		if (!entity.world.getBlockState(pos).getMaterial().isSolid()) { return pos; }
		BlockPos currentPos = pos.up();
		while (currentPos.getY() < entity.world.getHeight() && entity.world.isBlockLoaded(currentPos) && entity.world.getBlockState(currentPos).getMaterial().isSolid()) {
			currentPos = currentPos.up();
		}
		if (!entity.world.isBlockLoaded(currentPos)) { return pos; }
		return currentPos;
	}
}

package mctmods.resourcedatapackloader.mixin.galaxyspace;

import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;

import galaxyspace.core.events.GSEventHandler;
import micdoodle8.mods.galacticraft.core.util.OxygenUtil;
import net.minecraft.block.Block;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GSEventHandler.class, remap = false) public abstract class MixinGSEventHandler {
    @Redirect(method = "onTickBlock", at = @At(value = "INVOKE", target = "Lmicdoodle8/mods/galacticraft/core/util/OxygenUtil;isAABBInBreathableAirBlock(Lnet/minecraft/world/World;Lnet/minecraft/util/math/AxisAlignedBB;Z)Z")) private boolean rdpl$packPlantBreathes(World world, AxisAlignedBB bb, boolean testThermal) {
        Block block = world.getBlockState(new BlockPos(bb.minX, bb.minY, bb.minZ)).getBlock();
        if (!(block instanceof IContentBlock) || ((IContentBlock) block).getDef().needsOxygen) { return OxygenUtil.isAABBInBreathableAirBlock(world, bb, testThermal); }
        return true;
    }
}

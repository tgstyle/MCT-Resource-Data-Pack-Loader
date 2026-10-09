package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Entity.class) public abstract class MixinEntityRubic {
    @Shadow public World world;
    @Shadow public double posX;
    @Shadow public double posZ;
    @Shadow public double posY;
    @Unique private final BlockPos.MutableBlockPos rdpl$spawnPos = new BlockPos.MutableBlockPos();

    @Shadow public abstract float getEyeHeight();

    @ModifyConstant(method = "preparePlayerToSpawn", constant = @Constant(doubleValue = 0)) private double rdpl$getMinHeight(double zero) {
        if (!world.isBlockLoaded(rdpl$spawnPos.setPos(posX, posY, posZ))) { return Double.POSITIVE_INFINITY; }
        return GenHeights.floor(world, 0);
    }

    @ModifyConstant(method = "preparePlayerToSpawn", constant = @Constant(doubleValue = 256)) private double rdpl$getMaxHeight(double _256) { return GenHeights.ceiling(world, 256); }

    @ModifyArg(method = "getBrightnessForRender", index = 1, at = @At(target = "Lnet/minecraft/util/math/BlockPos$MutableBlockPos;<init>(III)V", value = "INVOKE")) public int rdpl$getModifiedYPos_getBrightnessForRender(int y) { return MathHelper.floor(this.posY + this.getEyeHeight()); }
}

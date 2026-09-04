package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class) public abstract class MixinEntity {
    @Shadow public World world;
    @Shadow public double posX;
    @Shadow public double posZ;
    @Unique private final BlockPos.MutableBlockPos rdpl$spawnPos = new BlockPos.MutableBlockPos();

    @ModifyConstant(method = "preparePlayerToSpawn",
            constant = @Constant(doubleValue = 0))
    private double rdpl$getMinHeight(double zero) {
        if (!world.isBlockLoaded(rdpl$spawnPos.setPos(posX, posY, posZ))) { return Double.POSITIVE_INFINITY; }
        return ((IRubicWorld) world).rdpl$getMinHeight();
    }

    @ModifyConstant(method = "preparePlayerToSpawn",
            constant = @Constant(doubleValue = 256))
    private double rdpl$getMaxHeight(double _256) { return ((IRubicWorld) world).rdpl$getMaxHeight(); }

    @Shadow public double posY;

    @Shadow public abstract float getEyeHeight();

    @ModifyArg(method = "getBrightnessForRender", index = 1, at = @At(target = "Lnet/minecraft/util/math/BlockPos$MutableBlockPos;<init>(III)V", value = "INVOKE"))
    public int getModifiedYPos_getBrightnessForRender(int y) { return MathHelper.floor(this.posY + this.getEyeHeight()); }
}

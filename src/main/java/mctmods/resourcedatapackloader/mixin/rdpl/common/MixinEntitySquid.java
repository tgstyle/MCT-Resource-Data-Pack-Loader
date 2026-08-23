package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.RubicWorldControl;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntitySquid.class) public class MixinEntitySquid extends EntityWaterMob {
    public MixinEntitySquid(World worldIn) { super(worldIn); }

    @ModifyConstant(method = "getCanSpawnHere", constant = @Constant(doubleValue = 45)) private double rdpl$spawnFloor(double orig) {
        return ((IRubicWorld) this.world).rdpl$isRubicWorld() ? orig + (RubicWorldControl.terrainOffsetCubes() << 4) : orig;
    }

    @Redirect(method = "getCanSpawnHere", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getSeaLevel()I")) private int rdpl$spawnCeiling(World world) {
        return ((IRubicWorld) world).rdpl$isRubicWorld() ? world.getSeaLevel() + (RubicWorldControl.terrainOffsetCubes() << 4) : world.getSeaLevel();
    }
}

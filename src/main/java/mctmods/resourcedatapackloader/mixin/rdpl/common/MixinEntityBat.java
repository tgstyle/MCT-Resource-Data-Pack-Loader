package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityBat.class) public class MixinEntityBat extends EntityAmbientCreature {
    public MixinEntityBat(World worldIn) { super(worldIn); }

    @ModifyConstant(method = "updateAITasks", constant = @Constant(intValue = 1, ordinal = 0)) private int updateAITasks_getMinSpawnPositionY(int originalY) {
        return ((IRubicWorld) this.world).rdpl$getMinHeight() + originalY;
    }
}

package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EntityFallingBlock.class) public abstract class MixinEntityFallingBlock extends Entity {
    public MixinEntityFallingBlock(World worldIn) { super(worldIn); }

    @Group(name = "onUpdateGetMinHeight", min = 1, max = 1) @ModifyConstant(
            method = "onUpdate",
            constant = @Constant(intValue = 1),
            slice = @Slice(
                    from = @At(value = "CONSTANT:ONE", args = "intValue=100"),
                    to = @At(value = "CONSTANT:FIRST", args = "stringValue=doEntityDrops")

            ))
    private int onUpdateGetMinHeight(int orig) { return ((IRubicWorld) world).rdpl$getMinHeight(); }

    @Group(name = "onUpdateGetMaxHeight", min = 1, max = 1) @ModifyConstant(
            method = "onUpdate",
            constant = @Constant(intValue = 256),
            slice = @Slice(
                    from = @At(value = "CONSTANT:ONE", args = "intValue=100"),
                    to = @At(value = "CONSTANT:LAST", args = "stringValue=doEntityDrops")
            ))
    private int onUpdateGetMaxHeight(int orig) { return ((IRubicWorld) world).rdpl$getMaxHeight(); }
}

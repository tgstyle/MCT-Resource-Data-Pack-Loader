package mctmods.resourcedatapackloader.mixin.client.gui.screens.inventory;

import mctmods.resourcedatapackloader.common.anvils.AnvilCosts;

import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilScreen.class) public abstract class AnvilScreenMixin {
    @ModifyConstant(method = "extractLabels", constant = @Constant(intValue = 40)) private int rdpl$tooExpensive(int vanilla) { return AnvilCosts.cap(true); }
}

package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.AnvilCosts;

import net.minecraft.client.gui.GuiRepair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiRepair.class) public abstract class MixinGuiRepair {
    @ModifyConstant(method = "drawGuiContainerForegroundLayer", constant = @Constant(intValue = 40)) private int rdpl$tooExpensive(int vanilla) { return AnvilCosts.cap(true); }
}

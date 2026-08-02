package mctmods.resourcedatapackloader.mixin.bop;

import mctmods.resourcedatapackloader.client.GuiPackShapesWorld;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;

import biomesoplenty.common.world.WorldTypeBOP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldTypeBOP.class, remap = false)
public abstract class MixinWorldTypeBOP {
    @Inject(method = "onCustomizeButton", at = @At("HEAD"), cancellable = true, remap = false)
    private void rdpl$packShapesIt(Minecraft mc, GuiCreateWorld guiCreateWorld, CallbackInfo ci) {
        String owner = ContentTerrain.owner();
        if (owner.isEmpty()) { return; }

        mc.displayGuiScreen(new GuiPackShapesWorld(guiCreateWorld, owner));
        ci.cancel();
    }
}

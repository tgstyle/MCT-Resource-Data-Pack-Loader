package mctmods.resourcedatapackloader.mixin.asmodeuscore;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.util.Colors;
import mctmods.resourcedatapackloader.util.compat.GsStarAura;

import asmodeuscore.core.astronomy.gui.screen.NewGuiCelestialSelection;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = NewGuiCelestialSelection.class, remap = false) public class MixinNewGuiCelestialSelectionAura {
    @WrapOperation(method = "drawCelestialBodies(Lorg/lwjgl/util/vector/Matrix4f;)Ljava/util/HashMap;", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", ordinal = 1)) private void rdpl$packAura(float red, float green, float blue, float alpha, Operation<Void> original, @Local(name = "body") CelestialBody body) {
        int rgb = GsStarAura.color(body);
        if (rgb == SkyLookDef.UNSET) { original.call(red, green, blue, alpha); }
        else { original.call(Colors.red(rgb) / 255.0F, Colors.green(rgb) / 255.0F, Colors.blue(rgb) / 255.0F, alpha); }
    }
}

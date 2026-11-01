package mctmods.resourcedatapackloader.mixin.integration.asmodeuscore;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyLookDef;
import mctmods.resourcedatapackloader.core.integration.galacticraftcore.GcNesting;
import mctmods.resourcedatapackloader.core.integration.galaxyspace.GsStarAura;
import mctmods.resourcedatapackloader.core.util.math.Colors;

import asmodeuscore.core.astronomy.gui.screen.NewGuiCelestialSelection;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = NewGuiCelestialSelection.class, remap = false) public class MixinNewGuiCelestialSelection {
    @WrapOperation(method = "drawCelestialBodies(Lorg/lwjgl/util/vector/Matrix4f;)Ljava/util/HashMap;", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", ordinal = 1)) private void rdpl$packAura(float red, float green, float blue, float alpha, Operation<Void> original, @Local(name = "body") CelestialBody body) {
        int rgb = GsStarAura.color(body);
        if (rgb == SkyLookDef.UNSET) { original.call(red, green, blue, alpha); }
        else { original.call(Colors.red(rgb) / 255.0F, Colors.green(rgb) / 255.0F, Colors.blue(rgb) / 255.0F, alpha); }
    }

    @ModifyArg(method = "getCelestialBodyPosition(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)Lorg/lwjgl/util/vector/Vector3f;", at = @At(value = "INVOKE", target = "Lasmodeuscore/core/astronomy/gui/screen/NewGuiCelestialSelection;getCelestialBodyPosition(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)Lorg/lwjgl/util/vector/Vector3f;")) private CelestialBody rdpl$nestPosition(CelestialBody center, @Local(argsOnly = true) CelestialBody body) { return GcNesting.center(body, center); }

    @ModifyArg(method = "drawCircles()V", at = @At(value = "INVOKE", target = "Lasmodeuscore/core/astronomy/gui/screen/NewGuiCelestialSelection;getCelestialBodyPosition(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)Lorg/lwjgl/util/vector/Vector3f;")) private CelestialBody rdpl$nestRing(CelestialBody center, @Local(name = "body") CelestialBody body) { return GcNesting.center(body, center); }

    @ModifyExpressionValue(method = "getAlpha(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)F", at = @At(value = "FIELD", target = "Lasmodeuscore/core/astronomy/gui/screen/NewGuiCelestialSelection;selectedBody:Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;", opcode = Opcodes.GETFIELD, ordinal = 1)) private CelestialBody rdpl$nestShown(CelestialBody selected, @Local(argsOnly = true) CelestialBody body) { return GcNesting.seen(body, selected); }

    @ModifyExpressionValue(method = "drawCelestialBodies(Lorg/lwjgl/util/vector/Matrix4f;)Ljava/util/HashMap;", at = @At(value = "INVOKE", target = "Lasmodeuscore/core/astronomy/gui/screen/NewGuiCelestialSelection;getAlpha(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)F")) private float rdpl$nestDrawn(float alpha, @Local(name = "body") CelestialBody body) { return GcNesting.drawn(body, alpha); }

    @ModifyArg(method = "getChildren(Ljava/lang/Object;II)Ljava/util/List;", at = @At(value = "INVOKE", target = "Ljava/util/Collections;sort(Ljava/util/List;)V")) private List<CelestialBody> rdpl$nestChildren(List<CelestialBody> list) { return GcNesting.unnest(list); }
}

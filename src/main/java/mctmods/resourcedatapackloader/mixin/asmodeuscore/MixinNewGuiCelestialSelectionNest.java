package mctmods.resourcedatapackloader.mixin.asmodeuscore;

import mctmods.resourcedatapackloader.util.compat.GcNesting;

import asmodeuscore.core.astronomy.gui.screen.NewGuiCelestialSelection;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import java.util.List;

@Mixin(value = NewGuiCelestialSelection.class, remap = false) public class MixinNewGuiCelestialSelectionNest {
    @ModifyArg(method = "getCelestialBodyPosition(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)Lorg/lwjgl/util/vector/Vector3f;", at = @At(value = "INVOKE", target = "Lasmodeuscore/core/astronomy/gui/screen/NewGuiCelestialSelection;getCelestialBodyPosition(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)Lorg/lwjgl/util/vector/Vector3f;")) private CelestialBody rdpl$nestPosition(CelestialBody center, @Local(argsOnly = true) CelestialBody body) { return GcNesting.center(body, center); }

    @ModifyArg(method = "drawCircles()V", at = @At(value = "INVOKE", target = "Lasmodeuscore/core/astronomy/gui/screen/NewGuiCelestialSelection;getCelestialBodyPosition(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)Lorg/lwjgl/util/vector/Vector3f;")) private CelestialBody rdpl$nestRing(CelestialBody center, @Local(name = "body") CelestialBody body) { return GcNesting.center(body, center); }

    @ModifyExpressionValue(method = "getAlpha(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)F", at = @At(value = "FIELD", target = "Lasmodeuscore/core/astronomy/gui/screen/NewGuiCelestialSelection;selectedBody:Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;", opcode = Opcodes.GETFIELD, ordinal = 1)) private CelestialBody rdpl$nestShown(CelestialBody selected, @Local(argsOnly = true) CelestialBody body) { return GcNesting.seen(body, selected); }

    @ModifyExpressionValue(method = "drawCelestialBodies(Lorg/lwjgl/util/vector/Matrix4f;)Ljava/util/HashMap;", at = @At(value = "INVOKE", target = "Lasmodeuscore/core/astronomy/gui/screen/NewGuiCelestialSelection;getAlpha(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)F")) private float rdpl$nestDrawn(float alpha, @Local(name = "body") CelestialBody body) { return GcNesting.drawn(body, alpha); }

    @ModifyArg(method = "getChildren(Ljava/lang/Object;II)Ljava/util/List;", at = @At(value = "INVOKE", target = "Ljava/util/Collections;sort(Ljava/util/List;)V")) private List<CelestialBody> rdpl$nestChildren(List<CelestialBody> list) { return GcNesting.unnest(list); }
}

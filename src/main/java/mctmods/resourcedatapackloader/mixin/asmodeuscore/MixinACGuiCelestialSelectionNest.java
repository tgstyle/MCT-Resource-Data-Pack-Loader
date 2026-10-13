package mctmods.resourcedatapackloader.mixin.asmodeuscore;

import mctmods.resourcedatapackloader.util.compat.GcNesting;

import asmodeuscore.core.astronomy.gui.screen.AC_GuiCelestialSelection;
import com.llamalad7.mixinextras.sugar.Local;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import java.util.List;

@Mixin(value = AC_GuiCelestialSelection.class, remap = false) public class MixinACGuiCelestialSelectionNest {
    @ModifyArg(method = "getCelestialBodyPosition(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)Lorg/lwjgl/util/vector/Vector3f;", at = @At(value = "INVOKE", target = "Lasmodeuscore/core/astronomy/gui/screen/AC_GuiCelestialSelection;getCelestialBodyPosition(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)Lorg/lwjgl/util/vector/Vector3f;")) private CelestialBody rdpl$nestPosition(CelestialBody center, @Local(argsOnly = true) CelestialBody body) { return GcNesting.center(body, center); }

    @ModifyArg(method = "drawCircles()V", at = @At(value = "INVOKE", target = "Lasmodeuscore/core/astronomy/gui/screen/AC_GuiCelestialSelection;getCelestialBodyPosition(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)Lorg/lwjgl/util/vector/Vector3f;")) private CelestialBody rdpl$nestRing(CelestialBody center, @Local(name = "body") CelestialBody body) { return GcNesting.center(body, center); }

    @ModifyArg(method = "getChildren(Ljava/lang/Object;IIZ)Ljava/util/List;", at = @At(value = "INVOKE", target = "Ljava/util/Collections;sort(Ljava/util/List;)V")) private List<CelestialBody> rdpl$nestChildren(List<CelestialBody> list) { return GcNesting.unnest(list); }
}

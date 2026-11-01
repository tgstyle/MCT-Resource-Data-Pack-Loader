package mctmods.resourcedatapackloader.mixin.integration.galacticraft;

import mctmods.resourcedatapackloader.core.integration.galacticraftcore.GcCelestial;
import mctmods.resourcedatapackloader.core.integration.galacticraftcore.GcNesting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiCelestialSelection.class, remap = false) public class MixinGuiCelestialSelection {
    @Shadow protected CelestialBody selectedBody;

    @ModifyArg(method = "getTranslationAdvanced(F)Lorg/lwjgl/util/vector/Vector2f;", at = @At(value = "INVOKE", target = "Lmicdoodle8/mods/galacticraft/core/client/gui/screen/GuiCelestialSelection;getCelestialBodyPosition(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)Lorg/lwjgl/util/vector/Vector3f;", ordinal = 0)) private CelestialBody rdpl$nestCentered(CelestialBody center) { return GcNesting.center(selectedBody, center); }

    @ModifyArg(method = "getCelestialBodyPosition(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)Lorg/lwjgl/util/vector/Vector3f;", at = @At(value = "INVOKE", target = "Lmicdoodle8/mods/galacticraft/core/client/gui/screen/GuiCelestialSelection;getCelestialBodyPosition(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)Lorg/lwjgl/util/vector/Vector3f;")) private CelestialBody rdpl$nestPosition(CelestialBody center, @Local(argsOnly = true) CelestialBody body) { return GcNesting.center(body, center); }

    @ModifyArg(method = "drawCircles()V", at = @At(value = "INVOKE", target = "Lmicdoodle8/mods/galacticraft/core/client/gui/screen/GuiCelestialSelection;getCelestialBodyPosition(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)Lorg/lwjgl/util/vector/Vector3f;")) private CelestialBody rdpl$nestRing(CelestialBody center, @Local(name = "body") CelestialBody body) { return GcNesting.center(body, center); }

    @ModifyExpressionValue(method = "getAlpha(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)F", at = @At(value = "FIELD", target = "Lmicdoodle8/mods/galacticraft/core/client/gui/screen/GuiCelestialSelection;selectedBody:Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;", opcode = Opcodes.GETFIELD, ordinal = 1)) private CelestialBody rdpl$nestShown(CelestialBody selected, @Local(argsOnly = true) CelestialBody body) { return GcNesting.seen(body, selected); }

    @ModifyArg(method = "getSiblings(Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Ljava/util/Collections;sort(Ljava/util/List;)V")) private List<CelestialBody> rdpl$nestSiblings(List<CelestialBody> list, @Local(argsOnly = true) CelestialBody body) { return GcNesting.siblings(body, list); }

    @ModifyArg(method = "getChildren(Ljava/lang/Object;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Ljava/util/Collections;sort(Ljava/util/List;)V")) private List<CelestialBody> rdpl$nestChildren(List<CelestialBody> list) { return GcNesting.unnest(list); }

    @Redirect(method = "getSatelliteParentID(Lmicdoodle8/mods/galacticraft/api/galaxies/Satellite;)I", at = @At(value = "INVOKE", target = "Lmicdoodle8/mods/galacticraft/api/galaxies/Satellite;getParentPlanet()Lmicdoodle8/mods/galacticraft/api/galaxies/Planet;")) private Planet rdpl$stationHome(Satellite station) { return GcCelestial.stationHome(station); }
}

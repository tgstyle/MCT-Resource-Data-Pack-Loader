package mctmods.resourcedatapackloader.mixin.betterf3;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.Coords;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import java.util.Locale;

@Mixin(targets = "com.worador.f3hud.RegionModule", remap = false) public abstract class MixinRegionModule {
    @Redirect(method = "getLines", at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"), remap = false)
    private String rdpl$cubeRegion(Locale locale, String pattern, Object[] parts) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null || !((IRubicWorld) mc.world).rdpl$isRubicWorld()) { return String.format(locale, pattern, parts); }
        int cubeX = Coords.blockToCube(MathHelper.floor(mc.player.posX));
        int cubeY = Coords.blockToCube(MathHelper.floor(mc.player.posY));
        int cubeZ = Coords.blockToCube(MathHelper.floor(mc.player.posZ));
        if (pattern.endsWith(".mca")) { return String.format(locale, "%d.%d.%d.3dr", cubeX >> 4, cubeY >> 4, cubeZ >> 4); }
        return String.format(locale, "Cube [%d, %d, %d] in Region", cubeX & 15, cubeY & 15, cubeZ & 15);
    }
}

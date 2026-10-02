package mctmods.resourcedatapackloader.mixin.betterf3;

import mctmods.resourcedatapackloader.content.rubic.regionlib.impl.RegionNames;
import mctmods.resourcedatapackloader.util.Coords;
import mctmods.resourcedatapackloader.util.world.GenHeights;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import java.util.Locale;

@SuppressWarnings("public-target") @Mixin(targets = "com.worador.f3hud.RegionModule", remap = false) public abstract class MixinRegionModule {
    @Redirect(method = "getLines", at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"), remap = false)
    private String rdpl$cubeRegion(Locale locale, String pattern, Object[] parts) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || !GenHeights.rubic(mc.world)) { return String.format(locale, pattern, parts); }
        int cubeX = Coords.blockToCube(MathHelper.floor(mc.player.posX));
        int cubeY = Coords.blockToCube(MathHelper.floor(mc.player.posY));
        int cubeZ = Coords.blockToCube(MathHelper.floor(mc.player.posZ));
        if (pattern.endsWith(".mca")) { return String.format(locale, "%d.%d.%d" + RegionNames.CUBES, cubeX >> 4, cubeY >> 4, cubeZ >> 4); }
        return String.format(locale, "Cube [%d, %d, %d] in Region", cubeX & 15, cubeY & 15, cubeZ & 15);
    }
}

package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentDimensionAmbience;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.AmbienceDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyLookDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.core.util.math.Colors;

import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class ContentDimensionRulesClient {
    private static final float BOSS_RED = 0.3F;
    private static final float BOSS_GREEN_BLUE = 0.4F;

    private ContentDimensionRulesClient() {}

    public static void lightmap(SkyLookDef look, float partialTicks, float sunBrightness, float skyLight, float blockLight, float[] colors) {
        float sky = skyLight * look.skyFactor;
        float skyRed = sky * (sunBrightness * 0.65F + 0.35F);
        float blockGreen = blockLight * ((blockLight * 0.6F + 0.4F) * 0.6F + 0.4F);
        float blockBlue = blockLight * (blockLight * blockLight * 0.6F + 0.4F);
        colors[0] = (skyRed * channel(look.lightSkyColor, Colors::red) + blockLight * channel(look.lightBlockColor, Colors::red)) * 0.96F + 0.03F;
        colors[1] = (skyRed * channel(look.lightSkyColor, Colors::green) + blockGreen * channel(look.lightBlockColor, Colors::green)) * 0.96F + 0.03F;
        colors[2] = (sky * channel(look.lightSkyColor, Colors::blue) + blockBlue * channel(look.lightBlockColor, Colors::blue)) * 0.96F + 0.03F;
        EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;
        float boss = renderer.bossColorModifierPrev + (renderer.bossColorModifier - renderer.bossColorModifierPrev) * partialTicks;
        if (boss <= 0.0F) { return; }
        colors[0] *= 1.0F - BOSS_RED * boss;
        colors[1] *= 1.0F - BOSS_GREEN_BLUE * boss;
        colors[2] *= 1.0F - BOSS_GREEN_BLUE * boss;
    }

    @Nullable public static MusicTicker.MusicType existingMusic(World world, @Nullable MusicTicker.MusicType own) {
        DimensionDef def = ContentDimensions.overlay(world);
        AmbienceDef ambience = def == null ? null : def.traits.ambience;
        return ambience == null || ambience.music == null ? own : ContentDimensionAmbience.music(ambience);
    }

    private static float channel(int rgb, IntUnaryOperator pick) { return rgb == SkyLookDef.UNSET ? 1.0F : pick.applyAsInt(rgb) / 255.0F; }
}

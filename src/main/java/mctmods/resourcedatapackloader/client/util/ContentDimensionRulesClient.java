package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentDimensionAmbience;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensionRules;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class ContentDimensionRulesClient {
    private static final float BOSS_RED = 0.3F;
    private static final float BOSS_GREEN_BLUE = 0.4F;
    private static final float SCALE = 0.96F;
    private static final float FLOOR = 0.03F;
    private static final int NIGHT_VISION_FULL = 200;
    private static final float NIGHT_VISION_BASE = 0.7F;
    private static final float NIGHT_VISION_SWING = 0.3F;
    private static final float NIGHT_VISION_RATE = 0.2F;

    private ContentDimensionRulesClient() {}

    public static void lightmap(SkyLookDef look, boolean dark, float partialTicks, float sunBrightness, float skyLight, float blockLight, float[] colors) {
        float scale = dark ? 1.0F : SCALE;
        float floor = dark ? 0.0F : FLOOR;
        float sky = skyLight * look.skyFactor;
        float skyRed = sky * (sunBrightness * 0.65F + 0.35F);
        float blockGreen = blockLight * ((blockLight * 0.6F + 0.4F) * 0.6F + 0.4F);
        float blockBlue = blockLight * (blockLight * blockLight * 0.6F + 0.4F);
        colors[0] = (skyRed * channel(look.lightSkyColor, Colors::red) + blockLight * channel(look.lightBlockColor, Colors::red)) * scale + floor;
        colors[1] = (skyRed * channel(look.lightSkyColor, Colors::green) + blockGreen * channel(look.lightBlockColor, Colors::green)) * scale + floor;
        colors[2] = (sky * channel(look.lightSkyColor, Colors::blue) + blockBlue * channel(look.lightBlockColor, Colors::blue)) * scale + floor;
        EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;
        float boss = renderer.bossColorModifierPrev + (renderer.bossColorModifier - renderer.bossColorModifierPrev) * partialTicks;
        if (boss <= 0.0F) { return; }
        colors[0] *= 1.0F - BOSS_RED * boss;
        colors[1] *= 1.0F - BOSS_GREEN_BLUE * boss;
        colors[2] *= 1.0F - BOSS_GREEN_BLUE * boss;
    }

    public static boolean tintsVision(World world) { return visionLook(world) != null; }

    @Nullable private static SkyLookDef visionLook(World world) {
        DimensionDef def = ContentDimensions.of(world);
        return def == null || def.look == null || !def.look.tintsVision() ? null : def.look;
    }

    public static Vec3d vision(World world, float partialTicks, Vec3d own) {
        SkyLookDef look = visionLook(world);
        if (look == null) { return own; }
        Entity view = Minecraft.getMinecraft().getRenderViewEntity();
        if (!(view instanceof EntityLivingBase)) { return own; }
        PotionEffect effect = ((EntityLivingBase) view).getActivePotionEffect(MobEffects.NIGHT_VISION);
        if (effect == null) { return own; }
        int duration = effect.getDuration();
        float strength = duration > NIGHT_VISION_FULL ? 1.0F : NIGHT_VISION_BASE + MathHelper.sin((duration - partialTicks) * (float) Math.PI * NIGHT_VISION_RATE) * NIGHT_VISION_SWING;
        return own.add(ContentDimensionRules.color(look.nightVisionColor).subtract(own).scale(strength));
    }

    @Nullable public static MusicTicker.MusicType existingMusic(World world, @Nullable MusicTicker.MusicType own) {
        DimensionDef def = ContentDimensions.overlay(world);
        AmbienceDef ambience = def == null ? null : def.traits.ambience;
        return ambience == null || ambience.music == null ? own : ContentDimensionAmbience.music(ambience);
    }

    private static float channel(int rgb, IntUnaryOperator pick) { return rgb == SkyLookDef.UNSET ? 1.0F : pick.applyAsInt(rgb) / 255.0F; }
}

package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidBehaviorDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidDef;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.joml.Vector3f;

public final class ContentFluidType extends FluidType {
    private static final double LAVA_MOTION = 0.0023333333333333335D;
    private final FluidDef def;

    public ContentFluidType(FluidDef def) {
        super(properties(def));
        this.def = def;
    }

    private static Properties properties(FluidDef def) {
        Properties properties = Properties.create()
                .descriptionId("fluid." + def.key().getNamespace() + "." + def.name())
                .lightLevel(def.luminosity())
                .density(def.gaseous() ? -Math.abs(def.density()) : def.density())
                .temperature(def.temperature())
                .viscosity(def.viscosity());
        if (def.waterMaterial()) { properties.fallDistanceModifier(0.0F).canExtinguish(true).supportsBoating(true).canHydrate(true); }
        else {
            properties.canSwim(false).canDrown(false).canPushEntity(false);
            if (def.lavaMaterial()) {
                properties.pathType(BlockPathTypes.LAVA).adjacentPathType(null).motionScale(LAVA_MOTION)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
            }
        }
        return keyed(properties, def.behavior().creatures());
    }

    private static Properties keyed(Properties properties, FluidBehaviorDef.Creatures keys) {
        Boolean swim = keys.swim();
        Boolean drown = keys.drown();
        Float push = keys.push();
        if (swim != null) {
            properties.canSwim(swim);
            if (swim) { properties.canExtinguish(true).fallDistanceModifier(0.0F); }
        }
        if (drown != null) { properties.canDrown(drown); }
        if (push != null) { properties.canPushEntity(true).motionScale(push); }
        return properties;
    }

    @Override public boolean isVaporizedOnPlacement(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull FluidStack stack) { return def.waterMaterial() && level.dimensionType().ultraWarm(); }

    @Override public void initializeClient(@Nonnull Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override @Nonnull public ResourceLocation getStillTexture() { return def.still(); }

            @Override @Nonnull public ResourceLocation getFlowingTexture() { return def.flowing(); }

            @Override public int getTintColor() { return def.tint(); }

            @Override public ResourceLocation getRenderOverlayTexture(Minecraft mc) { Boolean drown = def.behavior().creatures().drown(); return (drown == null ? def.waterMaterial() : drown) ? ResourceLocation.parse("textures/misc/underwater.png") : null; }

            @Override @Nonnull public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) { return new Vector3f(FastColor.ARGB32.red(def.color()) / 255.0F, FastColor.ARGB32.green(def.color()) / 255.0F, FastColor.ARGB32.blue(def.color()) / 255.0F); }

            @Override public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) { RenderSystem.setShaderFogStart(0.0F); RenderSystem.setShaderFogEnd(def.lavaMaterial() ? 1.25F : 20.0F); }
        });
    }
}

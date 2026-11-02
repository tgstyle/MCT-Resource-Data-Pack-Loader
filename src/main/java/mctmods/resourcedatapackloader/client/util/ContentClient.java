package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.client.gui.ContentContainerScreen;
import mctmods.resourcedatapackloader.client.gui.ContentPortwayScreen;
import mctmods.resourcedatapackloader.client.gui.EntityStorageScreen;
import mctmods.resourcedatapackloader.client.renderer.ContentBannerItemRenderer;
import mctmods.resourcedatapackloader.client.renderer.ContentBannerRenderer;
import mctmods.resourcedatapackloader.client.renderer.ContentBellRenderer;
import mctmods.resourcedatapackloader.client.renderer.ContentContainerRenderer;
import mctmods.resourcedatapackloader.client.renderer.ReturningThrowRenderer;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentDimensionEffects;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSeasonTint;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSnowTint;
import mctmods.resourcedatapackloader.common.banners.ContentBanners;
import mctmods.resourcedatapackloader.common.bells.ContentBells;
import mctmods.resourcedatapackloader.common.bells.IContentBell;
import mctmods.resourcedatapackloader.common.blocks.banner.logic.ContentBannerBlockEntity;
import mctmods.resourcedatapackloader.common.blocks.bell.logic.ContentBellBlockEntity;
import mctmods.resourcedatapackloader.common.blocks.container.logic.ContentContainerBlockEntity;
import mctmods.resourcedatapackloader.common.blocks.fluid.ContentFluids;
import mctmods.resourcedatapackloader.common.containers.ContentContainers;
import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.entities.ReturningThrow;
import mctmods.resourcedatapackloader.common.generated.ContentGeneratedItems;
import mctmods.resourcedatapackloader.common.gui.ContentContainerMenu;
import mctmods.resourcedatapackloader.common.gui.ContentPortwayMenu;
import mctmods.resourcedatapackloader.common.gui.EntityStorageMenu;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.tints.ContentTints;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.fluid.FluidTintSources;
import org.joml.Vector4f;

public final class ContentClient {
    private ContentClient() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ContentClient::setup);
        modBus.addListener(PouchKey::register);
        modBus.addListener(ContentClient::screens);
        modBus.addListener(ContentClient::fluidModels);
        modBus.addListener(ContentClient::fluidExtensions);
        modBus.addListener(ContentClient::blockColors);
        modBus.addListener(ContentClient::specialRenderers);
        modBus.addListener(ContentClient::renderers);
        modBus.addListener(ContentClient::bellModels);
        modBus.addListener(ContentDimensionEffects::register);
        modBus.addListener(EntityLook::register);
        modBus.addListener(ContentSnowTint::resolvers);
        modBus.addListener(ContentSnowTint::sources);
        modBus.addListener(ContentSeasonTint::resolvers);
        modBus.addListener(ContentSeasonTint::sources);
    }

    private static void bellModels(ModelEvent.RegisterStandalone event) {
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            if (entry.block() instanceof IContentBell bell && bell.swings()) { ContentBellRenderer.register(event, entry.id()); }
        }
    }

    private static void specialRenderers(RegisterSpecialModelRendererEvent event) { event.register(ContentGeneratedItems.BANNER_RENDERER, ContentBannerItemRenderer.Unbaked.MAP_CODEC); }

    private static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        BlockEntityType<ContentBannerBlockEntity> type = ContentBanners.TYPE.registeredType();
        if (type != null) { event.registerBlockEntityRenderer(type, ContentBannerRenderer::new); }
        BlockEntityType<ContentContainerBlockEntity> chests = ContentContainers.TYPE.registeredType();
        if (chests != null) { event.registerBlockEntityRenderer(chests, ContentContainerRenderer::new); }
        BlockEntityType<ContentBellBlockEntity> bells = ContentBells.TYPE.registeredType();
        if (bells != null) { event.registerBlockEntityRenderer(bells, ContentBellRenderer::new); }
        EntityType<ReturningThrow> returning = ContentEntities.returningThrow();
        if (returning != null) { event.registerEntityRenderer(returning, ReturningThrowRenderer::new); }
        Map<EntityType<?>, EntityRendererProvider<?>> providers = EntityRenderers.PROVIDERS;
        for (EntityType<?> variant : ContentEntities.types().values()) {
            EntityType<?> base = ContentEntities.base(variant);
            EntityRendererProvider<?> provider = base == null ? null : providers.get(base);
            if (provider == null) { ContentLog.LOGGER.error("Entity variant {} has no renderer to borrow from {}", variant, base); }
            else { event.registerEntityRenderer(variant, provider(provider)); }
        }
    }

    @SuppressWarnings("unchecked") private static EntityRendererProvider<Entity> provider(EntityRendererProvider<?> provider) { return (EntityRendererProvider<Entity>) provider; }

    private static void screens(RegisterMenuScreensEvent event) {
        MenuType<ContentContainerMenu> menu = ContentContainers.MENU.registeredType();
        if (menu != null) { event.register(menu, ContentContainerScreen::new); }
        MenuType<EntityStorageMenu> storage = ContentContainers.STORAGE_MENU.registeredType();
        if (storage != null) { event.register(storage, EntityStorageScreen::new); }
        MenuType<ContentPortwayMenu> portway = ContentContainers.PORTWAY_MENU.registeredType();
        if (portway != null) { event.register(portway, ContentPortwayScreen::new); }
    }

    private static void setup(FMLClientSetupEvent event) { event.enqueueWork(SplashDark::apply); }

    private static void fluidModels(RegisterFluidModelsEvent event) { for (ContentFluids.Made made : ContentFluids.made()) { event.register(new FluidModel.Unbaked(new Material(made.def.still(), true), new Material(made.def.flowing(), true), null, FluidTintSources.constant(made.def.tint())), made.still, made.flowing); } }

    private static void fluidExtensions(RegisterClientExtensionsEvent event) { for (ContentFluids.Made made : ContentFluids.made()) { event.registerFluidType(fluidExtension(made), made.type); } }

    private static IClientFluidTypeExtensions fluidExtension(ContentFluids.Made made) {
        return new IClientFluidTypeExtensions() {
            @Override public Identifier getRenderOverlayTexture(@Nonnull Minecraft mc) {
                Boolean drown = made.def.behavior().creatures().drown();
                return (drown == null ? made.def.waterMaterial() : drown) ? Identifier.withDefaultNamespace("textures/misc/underwater.png") : null;
            }

            @Override public void modifyFogColor(@Nonnull Camera camera, float partialTick, @Nonnull ClientLevel level, int renderDistance, float darkenWorldAmount, @Nonnull Vector4f fluidFogColor) {
                int color = made.def.color();
                fluidFogColor.set(ARGB.red(color) / 255.0F, ARGB.green(color) / 255.0F, ARGB.blue(color) / 255.0F, 1.0F);
            }

            @Override public void modifyFogRender(@Nonnull Camera camera, FogEnvironment environment, float renderDistance, float partialTick, @Nonnull FogData fogData) {
                fogData.environmentalStart = 0.0F;
                fogData.environmentalEnd = made.def.lavaMaterial() ? 1.25F : 20.0F;
            }
        };
    }

    private static void blockColors(RegisterColorHandlersEvent.BlockTintSources event) {
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            String tint = ContentTints.mode(entry.def().tint());
            if (tint.isEmpty()) { continue; }
            int fixed = ContentTints.fixed(tint, entry.id());
            BlockTintSource source = new BlockTintSource() {
                @Override public int color(@Nonnull BlockState state) { return ARGB.opaque(fixed >= 0 ? fixed : ContentTints.WHITE); }

                @Override public int colorInWorld(@Nonnull BlockState state, @Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos) { return ARGB.opaque(fixed >= 0 ? fixed : biome(tint, level, pos)); }
            };
            event.register(Collections.nCopies(ContentGeneratedItems.blockTintLayers(entry.id()), source), entry.block());
        }
    }

    private static int biome(String tint, BlockAndTintGetter level, BlockPos pos) {
        return switch (tint) {
            case ContentTints.GRASS -> BiomeColors.getAverageGrassColor(level, pos);
            case ContentTints.WATER -> BiomeColors.getAverageWaterColor(level, pos);
            default -> BiomeColors.getAverageFoliageColor(level, pos);
        };
    }
}

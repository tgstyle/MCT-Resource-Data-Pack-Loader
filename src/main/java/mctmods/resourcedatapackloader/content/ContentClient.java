package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.client.SplashDark;
import mctmods.resourcedatapackloader.client.PouchKey;
import mctmods.resourcedatapackloader.content.block.ContentContainerBlockEntity;
import mctmods.resourcedatapackloader.client.render.ContentBellRenderer;
import mctmods.resourcedatapackloader.client.render.ContentContainerRenderer;
import mctmods.resourcedatapackloader.client.render.ReturningThrowRenderer;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;
import mctmods.resourcedatapackloader.content.menu.EntityStorageMenu;
import mctmods.resourcedatapackloader.client.screen.ContentContainerScreen;
import mctmods.resourcedatapackloader.client.screen.EntityStorageScreen;
import mctmods.resourcedatapackloader.client.ContentDimensionEffects;
import mctmods.resourcedatapackloader.client.ContentSnowTint;
import mctmods.resourcedatapackloader.client.EntityLook;
import mctmods.resourcedatapackloader.content.block.ContentBannerBlockEntity;
import mctmods.resourcedatapackloader.content.block.ContentBellBlockEntity;
import mctmods.resourcedatapackloader.content.interfaces.IContentBell;
import mctmods.resourcedatapackloader.content.block.ContentFluids;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.entity.ReturningThrow;
import mctmods.resourcedatapackloader.content.util.ContentTints;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IEntityRenderers;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
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
import net.neoforged.neoforge.client.fluid.FluidTintSources;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;

public final class ContentClient {
    private ContentClient() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ContentClient::setup);
        modBus.addListener(PouchKey::register);
        modBus.addListener(ContentClient::screens);
        modBus.addListener(ContentClient::fluidModels);
        modBus.addListener(ContentClient::blockColors);
        modBus.addListener(ContentClient::specialRenderers);
        modBus.addListener(ContentClient::renderers);
        modBus.addListener(ContentClient::bellModels);
        modBus.addListener(ContentDimensionEffects::register);
        modBus.addListener(EntityLook::register);
        modBus.addListener(ContentSnowTint::resolvers);
        modBus.addListener(ContentSnowTint::sources);
    }

    private static void bellModels(ModelEvent.RegisterStandalone event) {
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            if (entry.block() instanceof IContentBell bell && bell.swings()) { ContentBellRenderer.register(event, entry.id()); }
        }
    }

    private static void specialRenderers(RegisterSpecialModelRendererEvent event) { event.register(ContentGeneratedItems.BANNER_RENDERER, ContentBannerItemRenderer.Unbaked.MAP_CODEC); }

    private static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        BlockEntityType<ContentBannerBlockEntity> type = ContentBanners.registered();
        if (type != null) { event.registerBlockEntityRenderer(type, ContentBannerRenderer::new); }
        BlockEntityType<ContentContainerBlockEntity> chests = ContentContainers.registeredType();
        if (chests != null) { event.registerBlockEntityRenderer(chests, ContentContainerRenderer::new); }
        BlockEntityType<ContentBellBlockEntity> bells = ContentBells.registeredType();
        if (bells != null) { event.registerBlockEntityRenderer(bells, ContentBellRenderer::new); }
        EntityType<ReturningThrow> returning = ContentEntities.returningThrow();
        if (returning != null) { event.registerEntityRenderer(returning, ReturningThrowRenderer::new); }
        Map<EntityType<?>, EntityRendererProvider<?>> providers = IEntityRenderers.rdpl$providers();
        for (EntityType<?> variant : ContentEntities.types().values()) {
            EntityType<?> base = ContentEntities.base(variant);
            EntityRendererProvider<?> provider = base == null ? null : providers.get(base);
            if (provider == null) { ContentLog.LOGGER.error("Entity variant {} has no renderer to borrow from {}", variant, base); }
            else { event.registerEntityRenderer(variant, provider(provider)); }
        }
    }

    @SuppressWarnings("unchecked") private static EntityRendererProvider<Entity> provider(EntityRendererProvider<?> provider) { return (EntityRendererProvider<Entity>) provider; }

    private static void screens(RegisterMenuScreensEvent event) {
        MenuType<ContentContainerMenu> menu = ContentContainers.registeredMenu();
        if (menu != null) { event.register(menu, ContentContainerScreen::new); }
        MenuType<EntityStorageMenu> storage = ContentContainers.registeredStorageMenu();
        if (storage != null) { event.register(storage, EntityStorageScreen::new); }
    }

    private static void setup(FMLClientSetupEvent event) { event.enqueueWork(SplashDark::apply); }

    private static void fluidModels(RegisterFluidModelsEvent event) {
        for (ContentFluids.Made made : ContentFluids.made()) { event.register(new FluidModel.Unbaked(new Material(made.def.still(), true), new Material(made.def.flowing(), true), null, FluidTintSources.constant(made.def.tint())), made.still, made.flowing); }
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

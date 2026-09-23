package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.client.SplashDark;
import mctmods.resourcedatapackloader.client.PouchKey;
import mctmods.resourcedatapackloader.content.block.ContentContainerBlockEntity;
import mctmods.resourcedatapackloader.client.render.ContentBellRenderer;
import mctmods.resourcedatapackloader.client.render.ContentContainerRenderer;
import mctmods.resourcedatapackloader.client.render.ReturningThrowRenderer;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;
import mctmods.resourcedatapackloader.client.screen.ContentContainerScreen;
import mctmods.resourcedatapackloader.client.ContentDimensionEffects;
import mctmods.resourcedatapackloader.content.block.ContentBannerBlockEntity;
import mctmods.resourcedatapackloader.content.block.ContentBellBlockEntity;
import mctmods.resourcedatapackloader.content.interfaces.IContentBell;
import mctmods.resourcedatapackloader.content.block.ContentFluids;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.entity.ReturningThrow;
import mctmods.resourcedatapackloader.content.item.ContentBannerItem;
import mctmods.resourcedatapackloader.content.item.ContentPotionItem;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IEntityRenderers;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.util.FastColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nonnull;

public final class ContentClient {
    private static final int WHITE = 0xFFFFFF;
    private static final String BIOME = "biome";
    private static final String FOLIAGE = "foliage";
    private static final String GRASS = "grass";
    private static final String WATER = "water";
    private static final String NONE = "none";

    private ContentClient() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ContentClient::setup);
        modBus.addListener(PouchKey::register);
        modBus.addListener(ContentClient::screens);
        modBus.addListener(ContentClient::extensions);
        modBus.addListener(ContentClient::blockColors);
        modBus.addListener(ContentClient::itemColors);
        modBus.addListener(ContentClient::renderers);
        modBus.addListener(ContentClient::bellModels);
        modBus.addListener(ContentDimensionEffects::register);
    }

    private static void bellModels(ModelEvent.RegisterAdditional event) {
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            if (entry.block() instanceof IContentBell bell && bell.swings()) { event.register(ContentBellRenderer.body(entry.id())); }
        }
    }

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
    }

    private static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            SplashDark.apply();
            for (ContentFluids.Made made : ContentFluids.made()) {
                ItemBlockRenderTypes.setRenderLayer(made.still, RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(made.flowing, RenderType.translucent());
            }
        });
    }

    private static void extensions(RegisterClientExtensionsEvent event) {
        for (ContentFluids.Made made : ContentFluids.made()) {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override @Nonnull public ResourceLocation getStillTexture() { return made.def.still(); }

                @Override @Nonnull public ResourceLocation getFlowingTexture() { return made.def.flowing(); }

                @Override public int getTintColor() { return made.def.tint(); }
            }, made.type);
        }
        List<Item> banners = new ArrayList<>();
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            if (entry.item() instanceof ContentBannerItem) { banners.add(entry.item()); }
        }
        if (banners.isEmpty()) { return; }
        event.registerItem(new IClientItemExtensions() {
            @Override @Nonnull public BlockEntityWithoutLevelRenderer getCustomRenderer() { return ContentBannerItemRenderer.get(); }
        }, banners.toArray(Item[]::new));
    }

    private static void blockColors(RegisterColorHandlersEvent.Block event) {
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            String tint = mode(entry.def().tint());
            if (tint.isEmpty()) { continue; }
            int fixed = fixed(tint, entry.id());
            event.register((state, level, pos, index) -> fixed >= 0 ? fixed : level == null || pos == null ? WHITE : biome(tint, level, pos), entry.block());
        }
    }

    private static void itemColors(RegisterColorHandlersEvent.Item event) {
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            if (entry.item() instanceof ContentPotionItem) { event.register(ContentClient::potionColor, entry.item()); }
            if (entry.block() == null) { continue; }
            String tint = mode(entry.block().def().tint());
            if (tint.isEmpty()) { continue; }
            int fixed = fixed(tint, entry.id());
            event.register((stack, index) -> fixed >= 0 ? fixed : WHITE, entry.item());
        }
        for (ContentFluids.Made made : ContentFluids.made()) {
            if (made.bucket != null) { event.register(new DynamicFluidContainerModel.Colors(), made.bucket); }
        }
    }

    private static int potionColor(ItemStack stack, int index) { return index > 0 ? -1 : FastColor.ARGB32.opaque(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor()); }

    private static String mode(String tint) { return tint == null ? "" : tint.trim().toLowerCase(Locale.ROOT); }

    private static int fixed(String tint, Object context) {
        return switch (tint) {
            case BIOME, FOLIAGE, GRASS, WATER -> -1;
            case NONE -> WHITE;
            default -> ContentParser.color(tint, context);
        };
    }

    private static int biome(String tint, BlockAndTintGetter level, BlockPos pos) {
        return switch (tint) {
            case GRASS -> BiomeColors.getAverageGrassColor(level, pos);
            case WATER -> BiomeColors.getAverageWaterColor(level, pos);
            default -> BiomeColors.getAverageFoliageColor(level, pos);
        };
    }
}

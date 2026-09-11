package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.client.SplashDark;
import mctmods.resourcedatapackloader.client.PouchKey;
import mctmods.resourcedatapackloader.content.block.ContentContainerBlockEntity;
import mctmods.resourcedatapackloader.client.render.ContentContainerRenderer;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;
import mctmods.resourcedatapackloader.client.screen.ContentContainerScreen;
import mctmods.resourcedatapackloader.client.ContentDimensionEffects;
import mctmods.resourcedatapackloader.content.block.ContentBannerBlockEntity;
import mctmods.resourcedatapackloader.content.block.ContentFluids;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IEntityRenderers;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import java.util.Locale;
import java.util.Map;

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
        modBus.addListener(ContentClient::blockColors);
        modBus.addListener(ContentClient::itemColors);
        modBus.addListener(ContentClient::renderers);
        modBus.addListener(ContentDimensionEffects::register);
    }

    private static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        BlockEntityType<ContentBannerBlockEntity> type = ContentBanners.registered();
        if (type != null) { event.registerBlockEntityRenderer(type, ContentBannerRenderer::new); }
        BlockEntityType<ContentContainerBlockEntity> chests = ContentContainers.registeredType();
        if (chests != null) { event.registerBlockEntityRenderer(chests, ContentContainerRenderer::new); }
        Map<EntityType<?>, EntityRendererProvider<?>> providers = IEntityRenderers.rdpl$providers();
        for (EntityType<Mob> variant : ContentEntities.types().values()) {
            EntityType<?> base = ContentEntities.base(variant);
            EntityRendererProvider<?> provider = base == null ? null : providers.get(base);
            if (provider == null) { ContentLog.LOGGER.error("Entity variant {} has no renderer to borrow from {}", variant, base); }
            else { event.registerEntityRenderer(variant, provider(provider)); }
        }
    }

    @SuppressWarnings("unchecked") private static EntityRendererProvider<Mob> provider(EntityRendererProvider<?> provider) { return (EntityRendererProvider<Mob>) provider; }

    private static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            SplashDark.apply();
            MenuType<ContentContainerMenu> menu = ContentContainers.registeredMenu();
            if (menu != null) { MenuScreens.register(menu, ContentContainerScreen::new); }
            for (ContentFluids.Made made : ContentFluids.made()) {
                ItemBlockRenderTypes.setRenderLayer(made.still, RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(made.flowing, RenderType.translucent());
            }
        });
    }

    private static void blockColors(RegisterColorHandlersEvent.Block event) {
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            String tint = mode(entry.def().tint());
            if (tint.isEmpty()) { continue; }
            int fixed = fixed(tint, entry.id());
            event.register((state, level, pos, index) -> fixed >= 0 ? fixed : level == null || pos == null ? defaultBiome(tint) : biome(tint, level, pos), entry.block());
        }
    }

    private static void itemColors(RegisterColorHandlersEvent.Item event) {
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            if (entry.block() == null) { continue; }
            String tint = mode(entry.block().def().tint());
            if (tint.isEmpty()) { continue; }
            int fixed = fixed(tint, entry.id());
            event.register((stack, index) -> fixed >= 0 ? fixed : defaultBiome(tint), entry.item());
        }
    }

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

    private static int defaultBiome(String tint) { return GRASS.equals(tint) ? GrassColor.getDefaultColor() : FoliageColor.getDefaultColor(); }
}

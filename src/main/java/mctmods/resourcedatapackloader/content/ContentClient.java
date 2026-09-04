package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.block.ContentFluids;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import java.util.Locale;

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
        modBus.addListener(ContentClient::blockColors);
        modBus.addListener(ContentClient::itemColors);
    }

    private static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
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

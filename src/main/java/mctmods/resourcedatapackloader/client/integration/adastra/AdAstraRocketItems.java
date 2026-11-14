package mctmods.resourcedatapackloader.client.integration.adastra;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.core.integration.adastra.PackRocketItem;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import earth.terrarium.adastra.client.forge.AdAstraClientForge;
import earth.terrarium.adastra.common.items.vehicles.RocketItem;
import java.util.Map;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public final class AdAstraRocketItems {
    private AdAstraRocketItems() {}

    public static void borrow() {
        Map<Item, BlockEntityWithoutLevelRenderer> renderers = AdAstraClientForge.ITEM_RENDERERS;
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            if (!(entry.item() instanceof PackRocketItem rocket)) { continue; }
            BlockEntityWithoutLevelRenderer renderer = renderer(renderers, ContentEntities.base(rocket.type()));
            if (renderer == null) { ContentLog.LOGGER.error("Rocket item {} has no Ad Astra item renderer to borrow", entry.id()); }
            else { renderers.put(rocket, renderer); }
        }
    }

    private static BlockEntityWithoutLevelRenderer renderer(Map<Item, BlockEntityWithoutLevelRenderer> renderers, EntityType<?> base) {
        for (Map.Entry<Item, BlockEntityWithoutLevelRenderer> entry : renderers.entrySet()) {
            if (entry.getKey() instanceof RocketItem item && !(item instanceof PackRocketItem) && item.type() == base) { return entry.getValue(); }
        }
        return null;
    }
}

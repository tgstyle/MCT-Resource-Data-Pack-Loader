package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.List;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.TierSortingRegistry;

final class TierRegistration {
    private TierRegistration() {}

    static void register(DerivedTier tier, Tiers base, ResourceLocation owner, String path) {
        String space = (owner.getNamespace() + "." + owner.getPath() + "." + path).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_.-]", "_");
        ResourceLocation name = ResourceLocation.fromNamespaceAndPath(space, base.name().toLowerCase(Locale.ROOT));
        if (TierSortingRegistry.byName(name) != null) {
            ContentLog.LOGGER.error("Tier name {} of derived item {} is already registered, leaving the copy of {} unsorted", name, owner, path);
            return;
        }
        TierSortingRegistry.registerTier(tier, name, lower(base), higher(base));
    }

    private static List<Object> lower(Tiers base) {
        return switch (base) {
            case WOOD -> List.of();
            case GOLD -> List.of("wood");
            case STONE -> List.of("wood", "gold");
            case IRON -> List.of("stone");
            case DIAMOND -> List.of("iron");
            case NETHERITE -> List.of("diamond");
        };
    }

    private static List<Object> higher(Tiers base) {
        return switch (base) {
            case WOOD -> List.of("gold", "stone");
            case GOLD -> List.of("stone");
            case STONE -> List.of("iron");
            case IRON -> List.of("diamond");
            case DIAMOND -> List.of("netherite");
            case NETHERITE -> List.of();
        };
    }
}

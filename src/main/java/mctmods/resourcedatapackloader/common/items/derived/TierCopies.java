package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;

final class TierCopies {
    private TierCopies() {}

    @Nullable static Object copy(Object value, ResourceLocation owner, String path) {
        if (value instanceof Tier tier) { return new DerivedTier(tier); }
        String name = (owner.getNamespace() + "_" + owner.getPath() + "_" + path).replaceAll("[^A-Za-z0-9_]", "_");
        ContentLog.LOGGER.error("Could not make a material named {} for derived item {}", name, owner);
        return null;
    }

    static boolean isTier(Object value) { return value instanceof Tier; }
}

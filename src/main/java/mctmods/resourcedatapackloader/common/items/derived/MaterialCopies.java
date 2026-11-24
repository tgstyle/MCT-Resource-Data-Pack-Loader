package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;

final class MaterialCopies {
    private MaterialCopies() {}

    @Nullable static Object copy(Object value, ResourceLocation owner, String path) {
        Object made = null;
        if (value instanceof Tiers tier) { made = tool(tier, owner, path); }
        else if (value instanceof ArmorMaterial material) { made = new CopiedArmorMaterial(material); }
        if (made == null) { ContentLog.LOGGER.error("Could not make a material for {} of derived item {}", path, owner); }
        return made;
    }

    static boolean isMaterial(Object value) { return value instanceof Tier || value instanceof ArmorMaterial; }

    @Nullable private static Object tool(Tiers base, ResourceLocation owner, String path) {
        TagKey<Block> tag = base.getTag();
        if (tag == null) { return null; }
        DerivedTier made = new DerivedTier(base, tag);
        TierRegistration.register(made, base, owner, path);
        return made;
    }
}

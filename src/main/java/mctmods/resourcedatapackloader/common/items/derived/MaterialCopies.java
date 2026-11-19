package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeTier;

final class MaterialCopies {
    private MaterialCopies() {}

    @Nullable static Object copy(Object value, ResourceLocation owner, String path) {
        Object made = null;
        if (value instanceof Tier tier) { made = tool(tier); }
        else if (value instanceof ArmorMaterial material) { made = new CopiedArmorMaterial(material); }
        if (made == null) { ContentLog.LOGGER.error("Could not make a material for {} of derived item {}", path, owner); }
        return made;
    }

    static boolean isMaterial(Object value) { return value instanceof Tier || value instanceof ArmorMaterial; }

    @Nullable @SuppressWarnings("deprecation") private static Object tool(Tier base) {
        TagKey<Block> tag = base.getTag();
        if (tag == null) { return null; }
        return new ForgeTier(base.getLevel(), base.getUses(), base.getSpeed(), base.getAttackDamageBonus(), base.getEnchantmentValue(), tag, base::getRepairIngredient);
    }
}

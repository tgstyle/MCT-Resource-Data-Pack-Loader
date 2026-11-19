package mctmods.resourcedatapackloader.common.items.derived;

import java.util.Map;

final class ItemAliases {
    private static final Map<String, String> SET = Map.of("toolMaterial", "tier");
    private static final Map<String, String> TIER = Map.of("enchantability", "enchantmentValue", "maxUses", "uses", "efficiency", "speed");
    private static final Map<String, String> RETURNS = Map.of("hasEffect", "isFoil(ItemStack)", "getItemEnchantability", "getEnchantmentValue()", "getContainerItem", "getCraftingRemainingItem(ItemStack)");

    private ItemAliases() {}

    static String set(String key) { return SET.getOrDefault(key, key); }

    static String enchantability(String key) { return TIER.getOrDefault(key, key); }

    static String field(Object holder, String key) { return holder instanceof DerivedTier ? enchantability(key) : set(key); }

    static String returns(String key) { return RETURNS.getOrDefault(key, key); }
}

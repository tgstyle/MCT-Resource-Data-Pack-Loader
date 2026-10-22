package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.extra.ContentTooltips;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import java.util.List;

public final class PackTooltips {
    private PackTooltips() {}

    private static Component text(String line) { return (Language.getInstance().has(line) ? Component.translatable(line) : Component.literal(line)).withStyle(ChatFormatting.GRAY); }

    private static void describe(ItemEnchantments enchantments, List<Component> tooltip) {
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            List<String> lines = entry.getKey().unwrapKey().map(key -> ContentTooltips.forEnchantment(key.identifier())).orElse(List.of());
            int at = lines.isEmpty() ? -1 : tooltip.indexOf(Enchantment.getFullname(entry.getKey(), entry.getIntValue()));
            if (at < 0) { continue; }
            for (String line : lines) { tooltip.add(++at, text(line)); }
        }
    }

    public static void onTooltip(ItemTooltipEvent event) {
        if (ContentTooltips.none()) { return; }
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        describe(stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY), tooltip);
        describe(stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY), tooltip);
        for (String line : ContentTooltips.forItem(stack)) { tooltip.add(text(line)); }
    }
}

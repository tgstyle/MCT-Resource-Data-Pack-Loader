package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.ContentAffixes;
import mctmods.resourcedatapackloader.content.extra.ContentTooltips;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import java.util.List;

public final class PackTooltips {
    private PackTooltips() {}

    private static Component text(String line) { return (I18n.exists(line) ? Component.translatable(line) : Component.literal(line)).withStyle(ChatFormatting.GRAY); }

    private static void describe(ItemEnchantments enchantments, List<Component> tooltip) {
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            List<String> lines = entry.getKey().unwrapKey().map(key -> ContentTooltips.forEnchantment(key.location())).orElse(List.of());
            int at = lines.isEmpty() ? -1 : tooltip.indexOf(Enchantment.getFullname(entry.getKey(), entry.getIntValue()));
            if (at < 0) { continue; }
            for (String line : lines) { tooltip.add(++at, text(line)); }
        }
    }

    private static String translated(String line) { return I18n.exists(line) ? I18n.get(line) : line; }

    public static void onTooltip(ItemTooltipEvent event) {
        affixes(event.getItemStack(), event.getToolTip());
        lines(event);
    }

    private static void affixes(ItemStack stack, List<Component> tooltip) {
        CompoundTag mark = ContentAffixes.mark(stack);
        if (mark == null) { return; }
        ListTag affixes = mark.getList(ContentAffixes.AFFIXES, Tag.TAG_COMPOUND);
        for (int at = 0; at < affixes.size(); at++) {
            String name = affixes.getCompound(at).getString(ContentAffixes.NAME);
            if (!name.isEmpty()) { tooltip.add(text(name)); }
        }
        ListTag sockets = mark.getList(ContentAffixes.SOCKETS, Tag.TAG_COMPOUND);
        for (int at = 0; at < sockets.size(); at++) {
            CompoundTag socket = sockets.getCompound(at);
            if (socket.getString(ContentAffixes.GEM).isEmpty()) { tooltip.add(text("rdpl.socket.empty")); }
            else { tooltip.add(Component.translatable("rdpl.socket.filled", translated(socket.getString(ContentAffixes.NAME))).withStyle(ChatFormatting.GRAY)); }
        }
    }

    private static void lines(ItemTooltipEvent event) {
        if (ContentTooltips.none()) { return; }
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        describe(stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY), tooltip);
        describe(stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY), tooltip);
        for (String line : ContentTooltips.forItem(stack)) { tooltip.add(text(line)); }
    }
}

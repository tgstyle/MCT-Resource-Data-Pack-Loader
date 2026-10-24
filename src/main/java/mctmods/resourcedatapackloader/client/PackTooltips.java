package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.ContentAffixes;
import mctmods.resourcedatapackloader.content.extra.ContentTooltips;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import java.util.List;
import java.util.Map;

public final class PackTooltips {
    private PackTooltips() {}

    private static Component text(String line) { return (I18n.exists(line) ? Component.translatable(line) : Component.literal(line)).withStyle(ChatFormatting.GRAY); }

    private static String translated(String line) { return I18n.exists(line) ? I18n.get(line) : line; }

    public static void onTooltip(ItemTooltipEvent event) {
        lines(event);
        affixes(event.getItemStack(), event.getToolTip());
    }

    private static void lines(ItemTooltipEvent event) {
        if (ContentTooltips.none()) { return; }
        List<Component> tooltip = event.getToolTip();
        for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(event.getItemStack()).entrySet()) {
            List<String> lines = ContentTooltips.forEnchantment(entry.getKey());
            int at = lines.isEmpty() ? -1 : tooltip.indexOf(entry.getKey().getFullname(entry.getValue()));
            if (at < 0) { continue; }
            for (String line : lines) { tooltip.add(++at, text(line)); }
        }
        for (String line : ContentTooltips.forItem(event.getItemStack())) { tooltip.add(text(line)); }
    }

    private static void affixes(ItemStack stack, List<Component> tooltip) {
        CompoundTag mark = stack.getTagElement(ContentAffixes.TAG);
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
}

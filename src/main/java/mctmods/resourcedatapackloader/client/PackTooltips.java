package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.ContentAffixes;
import mctmods.resourcedatapackloader.content.extra.ContentTooltips;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT) public final class PackTooltips {
    private static final int COMPOUND = 10;

    private PackTooltips() {}

    private static String translated(String line) { return I18n.hasKey(line) ? I18n.format(line) : line; }

    private static String text(String line) { return TextFormatting.GRAY + translated(line); }

    @SubscribeEvent public static void onTooltip(ItemTooltipEvent event) {
        if (ContentTooltips.none()) { return; }
        ItemStack stack = event.getItemStack();
        List<String> tooltip = event.getToolTip();
        for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
            List<String> lines = ContentTooltips.forEnchantment(entry.getKey());
            int at = lines.isEmpty() ? -1 : tooltip.indexOf(entry.getKey().getTranslatedName(entry.getValue()));
            if (at < 0) { continue; }
            for (String line : lines) { tooltip.add(++at, text(line)); }
        }
        for (String line : ContentTooltips.forItem(stack)) { tooltip.add(text(line)); }
        affixes(stack, tooltip);
    }

    private static void affixes(ItemStack stack, List<String> tooltip) {
        NBTTagCompound mark = stack.getSubCompound(ContentAffixes.TAG);
        if (mark == null) { return; }
        NBTTagList affixes = mark.getTagList(ContentAffixes.AFFIXES, COMPOUND);
        for (int at = 0; at < affixes.tagCount(); at++) {
            String name = affixes.getCompoundTagAt(at).getString(ContentAffixes.NAME);
            if (!name.isEmpty()) { tooltip.add(text(name)); }
        }
        NBTTagList sockets = mark.getTagList(ContentAffixes.SOCKETS, COMPOUND);
        for (int at = 0; at < sockets.tagCount(); at++) {
            NBTTagCompound socket = sockets.getCompoundTagAt(at);
            if (socket.getString(ContentAffixes.GEM).isEmpty()) { tooltip.add(text("rdpl.socket.empty")); }
            else { tooltip.add(TextFormatting.GRAY + I18n.format("rdpl.socket.filled", translated(socket.getString(ContentAffixes.NAME)))); }
        }
    }
}

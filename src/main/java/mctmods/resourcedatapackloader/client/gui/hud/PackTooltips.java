package mctmods.resourcedatapackloader.client.gui.hud;

import mctmods.resourcedatapackloader.client.util.ClientWeights;
import mctmods.resourcedatapackloader.common.affixes.ContentAffixes;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.tooltips.ContentTooltips;
import mctmods.resourcedatapackloader.common.weights.CarriedWeight;
import mctmods.resourcedatapackloader.common.weights.ContentWeights;
import mctmods.resourcedatapackloader.core.config.Config;

import java.util.List;
import java.util.Map;
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

@SideOnly(Side.CLIENT) public final class PackTooltips {
    private static final int COMPOUND = 10;

    private PackTooltips() {}

    private static String translated(String line) { return I18n.hasKey(line) ? I18n.format(line) : line; }

    private static String text(String line) { return TextFormatting.GRAY + translated(line); }

    private static void weight(ItemStack stack, List<String> tooltip) {
        WeightDef settings = ContentWeights.settings();
        if (Config.content.vanillaClients || !settings.tooltip || !ContentWeights.any() || stack.isEmpty()) { return; }
        long single = ContentWeights.of(stack);
        long held = CarriedWeight.single(stack, settings) - single;
        if (single > 0L) {
            String each = ClientWeights.format(single);
            tooltip.add(TextFormatting.GRAY + (stack.getCount() > 1 ? I18n.format("rdpl.weight.stack", each, ClientWeights.format((single + held) * stack.getCount())) : I18n.format("rdpl.weight.line", each)));
        }
        if (held > 0L) { tooltip.add(TextFormatting.GRAY + I18n.format("rdpl.weight.contents", ClientWeights.format(held))); }
    }

    @SubscribeEvent public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<String> tooltip = event.getToolTip();
        weight(stack, tooltip);
        if (ContentTooltips.none()) { return; }
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

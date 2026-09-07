package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentProspect;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class ProspectTooltip {
    private ProspectTooltip() {}

    @SubscribeEvent public static void onTooltip(ItemTooltipEvent event) {
        ContentProspect.Reads reads = ContentProspect.describe(event.getItemStack());
        if (reads == null) { return; }
        String line;
        if (reads.labels.isEmpty()) { line = I18n.format("rdpl.prospect.tooltipall"); }
        else { line = I18n.format(reads.blacklist ? "rdpl.prospect.tooltipnot" : "rdpl.prospect.tooltip", String.join(", ", reads.labels)); }
        event.getToolTip().add(TextFormatting.GRAY + line);
    }
}

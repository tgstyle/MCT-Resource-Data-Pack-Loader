package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentProspect;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public final class ProspectTooltip {
    private ProspectTooltip() {}

    public static void onTooltip(ItemTooltipEvent event) {
        ContentProspect.Reads reads = ContentProspect.describe(event.getItemStack());
        if (reads == null) { return; }
        Component line;
        if (reads.labels().isEmpty()) { line = Component.translatable("rdpl.prospect.tooltipall"); }
        else { line = Component.translatable(reads.blacklist() ? "rdpl.prospect.tooltipnot" : "rdpl.prospect.tooltip", String.join(", ", reads.labels())); }
        event.getToolTip().add(line.copy().withStyle(ChatFormatting.GRAY));
    }
}

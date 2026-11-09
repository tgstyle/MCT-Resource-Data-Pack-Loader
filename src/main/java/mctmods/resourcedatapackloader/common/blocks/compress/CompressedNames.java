package mctmods.resourcedatapackloader.common.blocks.compress;

import net.minecraft.item.ItemStack;

@SuppressWarnings("deprecation") public final class CompressedNames {
    private static final String FIRST = "rdpl.compressed";
    private static final String LAYERED = "rdpl.compressed.layered";

    private CompressedNames() {}

    public static String of(CompressedSet set, int layer, String ownKey) {
        if (net.minecraft.util.text.translation.I18n.canTranslate(ownKey)) { return net.minecraft.util.text.translation.I18n.translateToLocal(ownKey).trim(); }
        ItemStack base = set.source();
        String name = base.isEmpty() ? set.name : base.getDisplayName();
        int level = set.oreLevel(layer);
        return level <= 1 ? net.minecraft.util.text.translation.I18n.translateToLocalFormatted(FIRST, name) : net.minecraft.util.text.translation.I18n.translateToLocalFormatted(LAYERED, name, level);
    }
}

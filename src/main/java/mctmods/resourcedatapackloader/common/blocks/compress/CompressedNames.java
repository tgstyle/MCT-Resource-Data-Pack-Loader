package mctmods.resourcedatapackloader.common.blocks.compress;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class CompressedNames {
    private static final String FIRST = "rdpl.compressed";
    private static final String LAYERED = "rdpl.compressed.layered";

    private CompressedNames() {}

    public static Component of(CompressedSet set, int layer, String ownKey) {
        if (Language.getInstance().has(ownKey)) { return Component.translatable(ownKey); }
        ItemStack base = set.stack(0);
        Component name = base.isEmpty() ? Component.literal(set.name) : base.getHoverName();
        return layer <= 1 ? Component.translatable(FIRST, name) : Component.translatable(LAYERED, name, layer);
    }
}

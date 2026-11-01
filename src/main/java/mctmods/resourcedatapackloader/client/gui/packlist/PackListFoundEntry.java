package mctmods.resourcedatapackloader.client.gui.packlist;

import mctmods.resourcedatapackloader.common.pack.loading.PackListRules;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) final class PackListFoundEntry extends ResourcePackListEntryFound {
    private final GuiScreenResourcePacks screen;
    @Nullable private final PackListRules.Rule rule;

    PackListFoundEntry(GuiScreenResourcePacks screen, ResourcePackRepository.Entry entry) {
        super(screen, entry);
        this.screen = screen;
        this.rule = PackListRules.rule(entry.getResourcePackName());
    }

    @Override protected int getResourcePackFormat() { return rule != null && rule.compatible ? 3 : super.getResourcePackFormat(); }

    @Override @Nonnull protected String getResourcePackName() { return rule == null || rule.title == null ? super.getResourcePackName() : PackListText.shown(rule.title); }

    @Override @Nonnull protected String getResourcePackDescription() { return rule == null || rule.description == null ? super.getResourcePackDescription() : PackListText.shown(rule.description); }

    @Override protected boolean showHoverOverlay() { return (rule == null || !rule.fixed) && super.showHoverOverlay(); }

    @Override protected boolean canMoveLeft() { return (rule == null || !rule.alwaysOn()) && super.canMoveLeft(); }

    @Override public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        boolean entering = canMoveRight();
        boolean acted = super.mousePressed(slotIndex, mouseX, mouseY, mouseEvent, relativeX, relativeY);
        if (acted && entering) {
            if (getResourcePackFormat() == 3) { PackListEntries.seat(screen, this, rule); }
            else { PackListEntries.awaitConfirm(this); }
        }
        return acted;
    }
}

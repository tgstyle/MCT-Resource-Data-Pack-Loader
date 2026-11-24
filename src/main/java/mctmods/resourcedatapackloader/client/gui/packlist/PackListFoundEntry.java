package mctmods.resourcedatapackloader.client.gui.packlist;

import mctmods.resourcedatapackloader.client.util.ClientText;
import mctmods.resourcedatapackloader.client.util.RDPLResourcePack;
import mctmods.resourcedatapackloader.common.pack.loading.PackListRules;

import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) final class PackListFoundEntry extends ResourcePackListEntryFound {
    private final GuiScreenResourcePacks screen;
    private final PackListRules.Rule rule;

    PackListFoundEntry(GuiScreenResourcePacks screen, ResourcePackRepository.Entry entry) {
        super(screen, entry);
        this.screen = screen;
        this.rule = PackListRules.rule(entry.getResourcePackName());
    }

    @Override protected int getResourcePackFormat() { return rule.compatible ? RDPLResourcePack.PACK_FORMAT : super.getResourcePackFormat(); }

    @Override @Nonnull protected String getResourcePackName() { return ClientText.or(rule.title, super.getResourcePackName()); }

    @Override @Nonnull protected String getResourcePackDescription() { return ClientText.or(rule.description, super.getResourcePackDescription()); }

    @Override protected boolean showHoverOverlay() { return !rule.fixed && super.showHoverOverlay(); }

    @Override protected boolean canMoveLeft() { return !rule.alwaysOn() && super.canMoveLeft(); }

    @Override public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        boolean entering = canMoveRight();
        boolean acted = super.mousePressed(slotIndex, mouseX, mouseY, mouseEvent, relativeX, relativeY);
        if (acted && entering) {
            if (getResourcePackFormat() == RDPLResourcePack.PACK_FORMAT) { PackListEntries.seat(screen, this, rule); }
            else { PackListEntries.awaitConfirm(this); }
        }
        return acted;
    }
}

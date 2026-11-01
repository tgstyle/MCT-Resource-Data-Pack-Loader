package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.pack.PackListRules;

import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT) final class PackListDefaultEntry extends ResourcePackListEntryDefault {
    private final PackListRules.Rule rule;

    PackListDefaultEntry(GuiScreenResourcePacks screen, PackListRules.Rule rule) {
        super(screen);
        this.rule = rule;
    }

    @Override @Nonnull protected String getResourcePackName() { return rule.title == null ? super.getResourcePackName() : PackListText.shown(rule.title); }

    @Override @Nonnull protected String getResourcePackDescription() { return rule.description == null ? super.getResourcePackDescription() : PackListText.shown(rule.description); }
}

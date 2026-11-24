package mctmods.resourcedatapackloader.client.gui.packlist;

import mctmods.resourcedatapackloader.client.util.ClientText;
import mctmods.resourcedatapackloader.common.pack.loading.PackListRules;

import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) final class PackListDefaultEntry extends ResourcePackListEntryDefault {
    private final PackListRules.Rule rule;

    PackListDefaultEntry(GuiScreenResourcePacks screen, PackListRules.Rule rule) {
        super(screen);
        this.rule = rule;
    }

    @Override @Nonnull protected String getResourcePackName() { return ClientText.or(rule.title, super.getResourcePackName()); }

    @Override @Nonnull protected String getResourcePackDescription() { return ClientText.or(rule.description, super.getResourcePackDescription()); }
}

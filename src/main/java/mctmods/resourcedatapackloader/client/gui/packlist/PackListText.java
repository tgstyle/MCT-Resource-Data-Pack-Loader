package mctmods.resourcedatapackloader.client.gui.packlist;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) final class PackListText {
    private PackListText() {}

    static String shown(String text) { return I18n.hasKey(text) ? I18n.format(text) : text; }
}

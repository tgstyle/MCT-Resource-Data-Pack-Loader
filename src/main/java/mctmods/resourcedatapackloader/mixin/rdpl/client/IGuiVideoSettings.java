package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiVideoSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiVideoSettings.class) public interface IGuiVideoSettings extends IGuiScreen { @Accessor GuiListExtended getOptionsRowList(); }

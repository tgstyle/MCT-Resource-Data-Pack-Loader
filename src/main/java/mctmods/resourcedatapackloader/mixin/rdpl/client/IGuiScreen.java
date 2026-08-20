package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(GuiScreen.class) public interface IGuiScreen { @Accessor List<GuiButton> getButtonList(); }

package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.gui.GuiOptionsRowList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(GuiOptionsRowList.class) public interface IGuiOptionsRowList { @Accessor List<GuiOptionsRowList.Row> getOptions(); }

package mctmods.resourcedatapackloader.mixin;

import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiIngame.class)
public interface AccessorGuiIngame {
    @Accessor("titlesTimer") int rdpl$getTitlesTimer();
    @Accessor("displayedSubTitle") String rdpl$getDisplayedSubTitle();
}

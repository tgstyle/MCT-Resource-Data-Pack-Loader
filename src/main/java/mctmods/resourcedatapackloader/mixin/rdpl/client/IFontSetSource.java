package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.gui.font.FontSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FontSet.Source.class) public interface IFontSetSource {
    @Accessor("this$0") FontSet rdpl$fontSet();
}

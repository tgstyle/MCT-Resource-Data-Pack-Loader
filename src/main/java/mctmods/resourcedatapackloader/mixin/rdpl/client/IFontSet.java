package mctmods.resourcedatapackloader.mixin.rdpl.client;

import com.mojang.blaze3d.font.GlyphProvider;
import net.minecraft.client.gui.font.FontSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(FontSet.class) public interface IFontSet {
    @Accessor("activeProviders") List<GlyphProvider> rdpl$activeProviders();
}

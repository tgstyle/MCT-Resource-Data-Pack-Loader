package mctmods.resourcedatapackloader.mixin;

import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.ResourcePackListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(GuiScreenResourcePacks.class)
public interface AccessorGuiScreenResourcePacks {
    @Accessor("selectedResourcePacks") List<ResourcePackListEntry> rdpl$getSelectedResourcePacks();
}

package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.mixin.rdpl.client.IGuiScreenResourcePacks;
import mctmods.resourcedatapackloader.pack.PackInjector;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.RDPLResourcePack;

import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.List;

@SideOnly(Side.CLIENT) public final class PackListEntries {
    private PackListEntries() {}

    private static int aboveDefault(List<ResourcePackListEntry> entries) {
        for (int index = 0; index < entries.size(); index++) {
            if (entries.get(index) instanceof ResourcePackListEntryDefault) { return index; }
        }
        return entries.size();
    }

    public static final class Handler {
        @SubscribeEvent public void onInit(GuiScreenEvent.InitGuiEvent.Post event) {
            if (!(event.getGui() instanceof GuiScreenResourcePacks)) { return; }

            PackManager manager = PackManager.get();
            if (manager.isEmpty()) { return; }

            GuiScreenResourcePacks screen = (GuiScreenResourcePacks) event.getGui();
            List<ResourcePackListEntry> entries = ((IGuiScreenResourcePacks) screen).rdpl$getSelectedResourcePacks();
            if (entries == null) { return; }

            entries.removeIf(resourcePackListEntry -> resourcePackListEntry instanceof PackListEntry);
            RDPLResourcePack normal = PackInjector.pack(false);
            RDPLResourcePack override = PackInjector.pack(true);
            if (manager.hasTier(false) && normal != null) { entries.add(aboveDefault(entries), new PackListEntry(screen, normal, false)); }
            if (manager.hasTier(true) && override != null) { entries.add(0, new PackListEntry(screen, override, true)); }
        }
    }
}

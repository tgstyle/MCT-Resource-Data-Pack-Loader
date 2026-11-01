package mctmods.resourcedatapackloader.client.gui.packlist;

import mctmods.resourcedatapackloader.client.util.PackInjector;
import mctmods.resourcedatapackloader.client.util.RDPLResourcePack;
import mctmods.resourcedatapackloader.common.pack.loading.PackListRules;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class PackListEntries {
    private static final int DONE = 1;
    private static PackListFoundEntry awaiting;

    private PackListEntries() {}

    private static int aboveDefault(List<ResourcePackListEntry> entries) {
        for (int index = 0; index < entries.size(); index++) {
            if (entries.get(index) instanceof ResourcePackListEntryDefault) { return index; }
        }
        return entries.size();
    }

    private static int topSlot(List<ResourcePackListEntry> entries) { return !entries.isEmpty() && entries.get(0).isServerPack() ? 1 : 0; }

    static void awaitConfirm(PackListFoundEntry entry) { awaiting = entry; }

    public static final class Handler {
        @SubscribeEvent public void onInit(GuiScreenEvent.InitGuiEvent.Post event) {
            if (!(event.getGui() instanceof GuiScreenResourcePacks)) { return; }
            GuiScreenResourcePacks screen = (GuiScreenResourcePacks) event.getGui();
            List<ResourcePackListEntry> entries = screen.selectedResourcePacks;

            entries.removeIf(resourcePackListEntry -> resourcePackListEntry instanceof PackListEntry);
            if (!PackListRules.rules().isEmpty()) {
                wrap(screen, screen.getAvailableResourcePacks());
                wrap(screen, entries);
            }
            PackManager manager = PackManager.get();
            if (manager.isEmpty()) { return; }
            RDPLResourcePack normal = PackInjector.pack(false);
            RDPLResourcePack override = PackInjector.pack(true);
            if (manager.hasTier(false) && normal != null && visible(normal)) { entries.add(aboveDefault(entries), new PackListEntry(screen, normal, false)); }
            if (manager.hasTier(true) && override != null && visible(override)) { entries.add(0, new PackListEntry(screen, override, true)); }
            seatPinned(entries);
        }

        @SubscribeEvent public void onDone(GuiScreenEvent.ActionPerformedEvent.Pre event) {
            if (!(event.getGui() instanceof GuiScreenResourcePacks) || event.getButton().id != DONE) { return; }
            GuiScreenResourcePacks screen = (GuiScreenResourcePacks) event.getGui();
            List<ResourcePackRepository.Entry> found = Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries();
            for (int index = found.size() - 1; index >= 0; index--) {
                ResourcePackRepository.Entry entry = found.get(index);
                PackListRules.Rule rule = PackListRules.rule(entry.getResourcePackName());
                if (rule != null && rule.hidden) { screen.selectedResourcePacks.add(Boolean.TRUE.equals(rule.top) ? topSlot(screen.selectedResourcePacks) : aboveDefault(screen.selectedResourcePacks), new PackListFoundEntry(screen, entry)); }
            }
            seatPinned(screen.selectedResourcePacks);
        }

        @SubscribeEvent public void onDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
            if (awaiting == null || !(event.getGui() instanceof GuiScreenResourcePacks)) { return; }
            PackListFoundEntry entry = awaiting;
            awaiting = null;
            seat((GuiScreenResourcePacks) event.getGui(), entry, PackListRules.rule(entry.getResourcePackEntry().getResourcePackName()));
        }
    }

    static void seat(GuiScreenResourcePacks screen, ResourcePackListEntry entry, @Nullable PackListRules.Rule rule) {
        List<ResourcePackListEntry> entries = screen.selectedResourcePacks;
        if (rule != null && Boolean.FALSE.equals(rule.top) && entries.remove(entry)) { entries.add(aboveDefault(entries), entry); }
        seatPinned(entries);
    }

    private static void seatPinned(List<ResourcePackListEntry> entries) {
        List<ResourcePackListEntry> top = new ArrayList<>();
        List<ResourcePackListEntry> bottom = new ArrayList<>();
        for (ResourcePackListEntry entry : entries) {
            if (!(entry instanceof PackListFoundEntry)) { continue; }
            PackListRules.Rule rule = PackListRules.rule(((PackListFoundEntry) entry).getResourcePackEntry().getResourcePackName());
            if (rule != null && rule.fixed) { (Boolean.FALSE.equals(rule.top) ? bottom : top).add(entry); }
        }
        entries.removeAll(top);
        entries.removeAll(bottom);
        entries.addAll(topSlot(entries), top);
        entries.addAll(aboveDefault(entries), bottom);
    }

    private static void wrap(GuiScreenResourcePacks screen, List<ResourcePackListEntry> entries) {
        PackListRules.Rule vanilla = PackListRules.rule(PackListRules.VANILLA);
        for (int index = entries.size() - 1; index >= 0; index--) {
            ResourcePackListEntry entry = entries.get(index);
            if (entry instanceof ResourcePackListEntryDefault && !(entry instanceof PackListDefaultEntry) && vanilla != null) {
                if (vanilla.hidden) { entries.remove(index); }
                else { entries.set(index, new PackListDefaultEntry(screen, vanilla)); }
            }
            if (!(entry instanceof ResourcePackListEntryFound) || entry instanceof PackListFoundEntry) { continue; }
            ResourcePackRepository.Entry found = ((ResourcePackListEntryFound) entry).getResourcePackEntry();
            PackListRules.Rule rule = PackListRules.rule(found.getResourcePackName());
            if (rule != null && rule.hidden) { entries.remove(index); }
            else { entries.set(index, new PackListFoundEntry(screen, found)); }
        }
    }

    private static boolean visible(RDPLResourcePack pack) {
        PackListRules.Rule rule = PackListRules.rule(pack.getPackName());
        return rule == null || !rule.hidden;
    }
}

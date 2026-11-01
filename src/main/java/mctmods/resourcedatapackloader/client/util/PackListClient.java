package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.pack.loading.PackListRules;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class PackListClient {
    private static final List<ResourcePackRepository.Entry> KEPT = new ArrayList<>();
    private static boolean unsaved;

    private PackListClient() {}

    public static void settingsLoaded(GameSettings settings) {
        List<String> before = new ArrayList<>(settings.resourcePacks);
        if (PackListRules.firstRun(settings.resourcePacks.isEmpty())) {
            unsaved = !PackListRules.defaults().isEmpty();
            for (String name : PackListRules.defaults()) {
                if (!new File(Minecraft.getMinecraft().gameDir, "resourcepacks/" + name).exists()) { ContentLog.LOGGER.error("Pack list: default pack {} is not installed, so it stays off", name); }
            }
        }
        if (unsaved) { addAll(settings.resourcePacks, PackListRules.defaults()); }
        List<String> top = new ArrayList<>();
        List<String> bottom = new ArrayList<>();
        for (Map.Entry<String, PackListRules.Rule> entry : PackListRules.rules().entrySet()) {
            String id = entry.getKey();
            PackListRules.Rule rule = entry.getValue();
            if (rule.alwaysOn() && foreign(id) && !settings.resourcePacks.contains(id)) { settings.resourcePacks.add(id); }
            if ((rule.compatible || rule.alwaysOn()) && foreign(id) && !settings.incompatibleResourcePacks.contains(id)) {
                settings.incompatibleResourcePacks.add(id);
                ContentLog.LOGGER.info("Pack list: keeping {}, which the pack marks compatible or required", id);
            }
            if (rule.fixed && foreign(id)) { (Boolean.FALSE.equals(rule.top) ? bottom : top).add(id); }
        }
        settings.resourcePacks.removeAll(top);
        settings.resourcePacks.removeAll(bottom);
        List<String> low = new ArrayList<>();
        List<String> high = new ArrayList<>();
        int hidden = 0;
        for (String id : settings.resourcePacks) {
            PackListRules.Rule rule = PackListRules.rule(id);
            if (rule == null || rule.fixed || !foreign(id)) { continue; }
            if (rule.hidden && Boolean.TRUE.equals(rule.top)) { high.add(id); }
            else if (rule.hidden) { low.add(hidden++, id); }
            else if (Boolean.FALSE.equals(rule.top) && !before.contains(id)) { low.add(id); }
        }
        settings.resourcePacks.removeAll(low);
        settings.resourcePacks.removeAll(high);
        settings.resourcePacks.addAll(0, low);
        settings.resourcePacks.addAll(0, bottom);
        settings.resourcePacks.addAll(high);
        settings.resourcePacks.addAll(top);
    }

    public static void recover(IReloadableResourceManager manager, List<IResourcePack> list) {
        Minecraft mc = Minecraft.getMinecraft();
        ResourcePackRepository repository = mc.getResourcePackRepository();
        Set<String> wanted = new LinkedHashSet<>(PackListRules.afterFailure());
        for (Map.Entry<String, PackListRules.Rule> entry : PackListRules.rules().entrySet()) {
            if (entry.getValue().alwaysOn()) { wanted.add(entry.getKey()); }
        }
        List<ResourcePackRepository.Entry> fallback = new ArrayList<>();
        for (ResourcePackRepository.Entry entry : repository.getRepositoryEntriesAll()) {
            if (wanted.contains(entry.getResourcePackName())) { fallback.add(entry); }
        }
        if (fallback.isEmpty()) {
            manager.reloadResources(list);
            return;
        }
        int base = list.size();
        repository.setRepositories(fallback);
        for (ResourcePackRepository.Entry entry : fallback) { list.add(entry.getResourcePack()); }
        try {
            manager.reloadResources(list);
            KEPT.addAll(fallback);
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("Pack list: the packs kept after a failed reload failed too, so none stay on", ex);
            repository.setRepositories(Collections.emptyList());
            list.subList(base, list.size()).clear();
            manager.reloadResources(list);
        }
    }

    public static void keepFallback() {
        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        for (ResourcePackRepository.Entry entry : KEPT) {
            settings.resourcePacks.add(entry.getResourcePackName());
            if (entry.getPackFormat() != 3) { settings.incompatibleResourcePacks.add(entry.getResourcePackName()); }
        }
        KEPT.clear();
    }

    @SubscribeEvent public static void guiOpen(GuiOpenEvent event) {
        if (!unsaved || FMLClientHandler.instance().isLoading()) { return; }
        unsaved = false;
        Minecraft.getMinecraft().gameSettings.saveOptions();
    }

    private static boolean foreign(String id) { return !PackListRules.own(id) && !PackListRules.VANILLA.equals(id); }

    private static void addAll(List<String> into, List<String> names) {
        for (String name : names) {
            if (!into.contains(name)) { into.add(name); }
        }
    }
}

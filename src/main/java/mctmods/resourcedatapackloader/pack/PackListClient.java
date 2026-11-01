package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import java.util.List;

public final class PackListClient {
    private PackListClient() {}

    public static void firstRun() {
        Options options = Minecraft.getInstance().options;
        if (PackListRules.firstRun(options.resourcePacks.isEmpty())) {
            options.resourcePacks.addAll(PackListRules.defaults());
            PackFinder.land(PackListRules.defaults());
        }
    }

    public static void failed(Minecraft minecraft) {
        List<String> fallback = PackListRules.afterFailure();
        if (fallback.isEmpty()) { return; }
        minecraft.getResourcePackRepository().setSelected(fallback.stream().map(PackListClient::known).toList());
        options(minecraft, fallback);
    }

    public static boolean holdsAlwaysOn() { return PackListPacks.holdsAlwaysOn(Minecraft.getInstance().getResourcePackRepository().getSelectedPacks()); }

    public static boolean dropAfterFailedRecovery(Minecraft minecraft) {
        PackRepository repository = minecraft.getResourcePackRepository();
        if (repository.getSelectedPacks().stream().allMatch(Pack::isRequired) && !holdsAlwaysOn()) { return false; }
        ContentLog.LOGGER.error("Pack list: the packs kept after a failed reload failed too, so none stay on");
        PackListPacks.dropAlwaysOn();
        repository.reload();
        minecraft.clearResourcePacksOnError(new IllegalStateException("The packs kept after a failed reload failed too"), null, null);
        return true;
    }

    public static void settleDefaults(PackRepository repository) {
        if (!PackFinder.settleLanding()) { return; }
        Options options = Minecraft.getInstance().options;
        options.resourcePacks.clear();
        options.incompatibleResourcePacks.clear();
        for (Pack pack : repository.getSelectedPacks()) {
            if (pack.isFixedPosition() || pack.isHidden()) { continue; }
            options.resourcePacks.add(pack.getId());
            if (!pack.getCompatibility().isCompatible()) { options.incompatibleResourcePacks.add(pack.getId()); }
        }
        options.save();
    }

    private static String known(String id) { return Minecraft.getInstance().getResourcePackRepository().isAvailable(id) ? id : "file/" + id; }

    private static void options(Minecraft minecraft, List<String> ids) {
        for (String raw : ids) {
            String id = known(raw);
            minecraft.options.resourcePacks.add(id);
            Pack pack = minecraft.getResourcePackRepository().getPack(id);
            if (pack != null && !pack.getCompatibility().isCompatible()) { minecraft.options.incompatibleResourcePacks.add(id); }
        }
    }
}

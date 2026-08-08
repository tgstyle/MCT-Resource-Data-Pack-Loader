package mctmods.resourcedatapackloader.pack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public final class PackInjector {
    private static RDPLResourcePack normal;
    private static RDPLResourcePack override;

    private PackInjector() {}

    public static List<IResourcePack> insert(List<IResourcePack> list) {
        PackManager manager = PackManager.get();
        Path root = manager.getRoot();
        if (manager.isEmpty() || root == null) { return list; }

        List<IResourcePack> ordered = new ArrayList<>(list);
        ordered.remove(pack(root, false));
        ordered.remove(pack(root, true));
        if (manager.hasTier(false)) { ordered.add(beforeSelectedPacks(ordered), pack(root, false)); }
        if (manager.hasTier(true)) { ordered.add(pack(root, true)); }

        return ordered;
    }

    @Nullable public static RDPLResourcePack pack(boolean overriding) {
        Path root = PackManager.get().getRoot();
        return root == null ? null : pack(root, overriding);
    }

    private static RDPLResourcePack pack(Path root, boolean overriding) {
        if (overriding) {
            if (override == null) { override = new RDPLResourcePack(root.toFile(), true); }
            return override;
        }
        if (normal == null) { normal = new RDPLResourcePack(root.toFile(), false); }
        return normal;
    }

    private static int beforeSelectedPacks(List<IResourcePack> list) {
        Set<IResourcePack> selected = selectedPacks();
        for (int index = list.size() - 1; index >= 0; index--) {
            if (!selected.contains(list.get(index))) { return index + 1; }
        }
        return 0;
    }

    private static Set<IResourcePack> selectedPacks() {
        Set<IResourcePack> packs = Collections.newSetFromMap(new IdentityHashMap<>());
        ResourcePackRepository repository = Minecraft.getMinecraft().getResourcePackRepository();
        for (ResourcePackRepository.Entry entry : repository.getRepositoryEntries()) { packs.add(entry.getResourcePack()); }
        IResourcePack server = repository.getServerResourcePack();
        if (server != null) { packs.add(server); }

        return packs;
    }
}

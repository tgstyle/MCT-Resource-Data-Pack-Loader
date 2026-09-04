package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public final class PackFinder implements RepositorySource {
    private static final PackSource SOURCE = PackSource.create(name -> Component.translatable("pack.nameAndSource", name, Component.translatable("rdpl.pack.source")).withStyle(ChatFormatting.GRAY), true);
    private final PackType type;

    public PackFinder(PackType type) { this.type = type; }

    public static Path root() { return FMLPaths.GAMEDIR.get().resolve(Config.packs.rootDirectory()); }

    public static void ensureScanned() {
        PackManager manager = PackManager.get();
        if (manager.getRoot() != null) { return; }
        Path root = root();
        ContentLog.LOGGER.info("Pack root: {}", root);
        manager.scan(root);
        manager.report();
    }

    @Override public void loadPacks(@NotNull Consumer<Pack> out) {
        ensureScanned();
        PackManager manager = PackManager.get();
        if (manager.isEmpty()) { return; }
        if (!GeneratedResources.isEmpty()) { offerGenerated(out); }
        if (manager.hasTier(false)) { offer(out, false); }
        if (manager.hasTier(true)) { offer(out, true); }
    }

    private void offerGenerated(Consumer<Pack> out) {
        Pack.ResourcesSupplier supplier = packId -> new GeneratedPack(type);
        Pack.Info info = Pack.readPackInfo(GeneratedPack.ID, supplier);
        if (info == null) {
            ContentLog.LOGGER.error("The {} pack could not describe itself to the game, so it is not offered as a {} pack", GeneratedPack.ID, type.getDirectory());
            return;
        }
        out.accept(Pack.create(GeneratedPack.ID, Component.literal(GeneratedPack.ID), true, supplier, info, type, Pack.Position.BOTTOM, true, SOURCE));
    }

    private void offer(Consumer<Pack> out, boolean overriding) {
        String id = RDPLResourcePack.id(overriding);
        Pack.ResourcesSupplier supplier = packId -> new RDPLResourcePack(type, overriding);
        Pack.Info info = Pack.readPackInfo(id, supplier);
        if (info == null) {
            ContentLog.LOGGER.error("The {} pack could not describe itself to the game, so it is not offered as a {} pack", id, type.getDirectory());
            return;
        }
        out.accept(Pack.create(id, Component.literal(id), true, supplier, info, type, overriding ? Pack.Position.TOP : Pack.Position.BOTTOM, true, SOURCE));
    }

    public static List<Pack> seat(List<Pack> selected) {
        Pack generated = null;
        Pack normal = null;
        Pack overriding = null;
        List<Pack> seated = new ArrayList<>(selected.size());
        for (Pack pack : selected) {
            switch (pack.getId()) {
                case GeneratedPack.ID -> generated = pack;
                case RDPLResourcePack.ID -> normal = pack;
                case RDPLResourcePack.ID_OVERRIDING -> overriding = pack;
                default -> seated.add(pack);
            }
        }
        if (generated == null && normal == null && overriding == null) { return selected; }
        int at = lastBase(seated) + 1;
        if (generated != null) { seated.add(at++, generated); }
        if (normal != null) { seated.add(at, normal); }
        if (overriding != null) { seated.add(overriding); }
        return ImmutableList.copyOf(seated);
    }

    private static int lastBase(List<Pack> packs) {
        int last = -1;
        for (int i = 0; i < packs.size(); i++) {
            if (!userPicked(packs.get(i))) { last = i; }
        }
        return last;
    }

    private static boolean userPicked(@Nullable Pack pack) { return pack != null && (pack.getId().startsWith("file/") || pack.getPackSource() == PackSource.SERVER); }
}

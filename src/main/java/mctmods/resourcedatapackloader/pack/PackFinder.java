package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.neoforged.fml.loading.FMLPaths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;

public final class PackFinder implements RepositorySource {
    private static final String KUBEJS = "dev.latvian.mods.kubejs.";
    private static final PackSource SOURCE = PackSource.create(name -> Component.translatable("pack.nameAndSource", name, Component.translatable("rdpl.pack.source")).withStyle(ChatFormatting.GRAY), true);
    private static final PackSource PLAIN = PackSource.create(UnaryOperator.identity(), true);
    private static final Set<String> LANDING = new HashSet<>();
    private static final String MOD_PACK = "mod/";
    private static final Set<String> FLOOR = Set.of(PackListRules.VANILLA, "mod_resources", "mod_data", GeneratedPack.ID, RDPLResourcePack.ID, RDPLResourcePack.ID_OVERRIDING);
    private final PackType type;

    public PackFinder(PackType type) { this.type = type; }

    public static Path root() { return FMLPaths.GAMEDIR.get().resolve(Config.packs.rootDirectory()); }

    public static void ensureScanned() {
        PackManager manager = PackManager.get();
        if (manager.scanned()) { return; }
        Path root = root();
        ContentLog.LOGGER.info("Pack root: {}", root);
        StrayPacks.collect(FMLPaths.GAMEDIR.get(), root);
        manager.scan(root);
        manager.report();
    }

    @Override public void loadPacks(@Nonnull Consumer<Pack> out) {
        if (type == PackType.CLIENT_RESOURCES) { PackListClient.firstRun(); }
        ensureScanned();
        PackManager manager = PackManager.get();
        if (GeneratedResources.holds()) { offerGenerated(out); }
        if (manager.isEmpty()) { return; }
        if (manager.hasTier(false)) { offer(out, false); }
        if (manager.hasTier(true)) { offer(out, true); }
    }

    private void offerGenerated(Consumer<Pack> out) { offer(out, GeneratedPack.ID, GeneratedPack.supplier(type), UnaryOperator.identity(), Pack.Position.BOTTOM, SOURCE); }

    private void offer(Consumer<Pack> out, boolean overriding) { offer(out, RDPLResourcePack.id(overriding), RDPLResourcePack.supplier(type, overriding), meta -> described(meta, overriding), overriding ? Pack.Position.TOP : Pack.Position.BOTTOM, tierSource()); }

    private void offer(Consumer<Pack> out, String id, Pack.ResourcesSupplier supplier, UnaryOperator<Pack.Metadata> describe, Pack.Position position, PackSource source) {
        PackLocationInfo location = new PackLocationInfo(id, title(id), source, Optional.empty());
        Pack.Metadata meta = Pack.readPackMetadata(location, supplier, SharedConstants.getCurrentVersion().getPackVersion(type));
        if (meta == null) {
            ContentLog.LOGGER.error("The {} pack could not describe itself to the game, so it is not offered as a {} pack", id, type.getDirectory());
            return;
        }
        out.accept(new Pack(location, supplier, describe.apply(meta), new PackSelectionConfig(true, position, true)));
    }

    private PackSource tierSource() { return type == PackType.CLIENT_RESOURCES ? PLAIN : SOURCE; }

    private Component title(String id) {
        if (type != PackType.CLIENT_RESOURCES || GeneratedPack.ID.equals(id)) { return Component.literal(id); }
        return Component.translatable(RDPLResourcePack.ID_OVERRIDING.equals(id) ? "rdpl.gui.packList.override.title" : "rdpl.gui.packList.normal.title");
    }

    private Pack.Metadata described(Pack.Metadata meta, boolean overriding) {
        if (type != PackType.CLIENT_RESOURCES) { return meta; }
        return new Pack.Metadata(Component.translatable(overriding ? "rdpl.gui.packList.override" : "rdpl.gui.packList.normal"), meta.compatibility(), meta.requestedFeatures(), meta.overlays(), meta.isHidden());
    }

    public static boolean servesClient(Collection<RepositorySource> sources) {
        for (RepositorySource source : sources) {
            if (source instanceof PackFinder finder && finder.type == PackType.CLIENT_RESOURCES) { return true; }
        }
        return false;
    }

    public static List<Pack> seat(List<Pack> selected, boolean client) {
        Pack generated = null;
        Pack normal = null;
        Pack overriding = null;
        List<Pack> seated = new ArrayList<>(selected.size());
        for (Pack pack : selected) {
            if (GeneratedPack.ID.equals(pack.getId())) { generated = pack; }
            else if (RDPLResourcePack.ID.equals(pack.getId())) { normal = pack; }
            else if (RDPLResourcePack.ID_OVERRIDING.equals(pack.getId())) { overriding = pack; }
            else { seated.add(pack); }
        }
        List<Pack> low = new ArrayList<>();
        List<Pack> high = new ArrayList<>();
        if (client) { pickRuled(seated, low, high); }
        if (generated == null && normal == null && overriding == null && low.isEmpty() && high.isEmpty()) { return selected; }
        int at = lastOf(seated, client ? PackFinder::floor : PackFinder::base) + 1;
        if (generated != null) { seated.add(at++, generated); }
        if (normal != null) { seated.add(at++, normal); }
        if (overriding != null) { seated.add(overriding); }
        seated.addAll(at, low);
        for (Pack pack : high) { Pack.Position.TOP.insert(seated, pack, Pack::selectionConfig, false); }
        return ImmutableList.copyOf(seated);
    }

    static void land(Collection<String> ids) { LANDING.addAll(ids); }

    static boolean settleLanding() {
        boolean held = !LANDING.isEmpty();
        LANDING.clear();
        return held;
    }

    private static void pickRuled(List<Pack> seated, List<Pack> low, List<Pack> high) {
        int floor = lastOf(seated, PackFinder::floor);
        boolean held = !PackListPacks.dropped();
        int pinned = 0;
        int hidden = 0;
        List<Pack> loose = new ArrayList<>();
        for (int index = 0; index < seated.size(); index++) {
            Pack pack = seated.get(index);
            boolean landing = LANDING.contains(PackListRules.id(pack.getId()));
            PackListRules.Rule rule = PackListRules.rule(pack.getId());
            if (rule == null) {
                if (index < floor && !floor(pack)) { loose.add(pack); }
                continue;
            }
            if (PackListRules.VANILLA.equals(pack.getId()) || held && rule.fixed && !Boolean.FALSE.equals(rule.top)) { continue; }
            if (held && rule.fixed) { low.add(pinned++, pack); }
            else if (held && rule.hidden && Boolean.TRUE.equals(rule.top)) { high.add(pack); }
            else if (held && rule.hidden) { low.add(pinned + hidden++, pack); }
            else if (Boolean.FALSE.equals(rule.top) && (index < floor || landing)) { low.add(pack); }
            else if (index < floor && !floor(pack)) { loose.add(pack); }
        }
        low.addAll(loose);
        seated.removeAll(low);
        seated.removeAll(high);
    }

    public static List<PackResources> beforeOwn(List<PackResources> opened) {
        List<PackResources> scripted = new ArrayList<>();
        int own = -1;
        for (int i = 0; i < opened.size(); i++) {
            PackResources pack = opened.get(i);
            if (pack.getClass().getName().startsWith(KUBEJS)) { scripted.add(pack); }
            else if (own < 0 && pack instanceof RDPLPackResources) { own = i; }
        }
        if (scripted.isEmpty() || own < 0) { return opened; }
        List<PackResources> ordered = new ArrayList<>(opened);
        ordered.removeAll(scripted);
        int at = 0;
        while (at < ordered.size() && !(ordered.get(at) instanceof RDPLPackResources)) { at++; }
        ordered.addAll(at, scripted);
        return ordered;
    }

    public static void seatShown(List<Pack> shown) {
        List<Pack> seated = new ArrayList<>(seat(Lists.reverse(shown), true));
        Collections.reverse(seated);
        shown.clear();
        shown.addAll(seated);
    }

    public static boolean crossesBase(List<Pack> shown, Pack pack, int step) {
        int next = shown.indexOf(pack) + step;
        return next >= 0 && next < shown.size() && floor(pack) != floor(shown.get(next)) && (step > 0 || floor(pack));
    }

    private static int lastOf(List<Pack> packs, Predicate<Pack> holds) {
        int last = -1;
        for (int i = 0; i < packs.size(); i++) { if (holds.test(packs.get(i))) { last = i; } }
        return last;
    }

    private static boolean floor(Pack pack) { return FLOOR.contains(pack.getId()) || pack.getId().startsWith(MOD_PACK) || pack.getPackSource() == PackSource.FEATURE; }

    private static boolean base(Pack pack) { return !pack.getId().startsWith("file/") && pack.getPackSource() != PackSource.SERVER; }
}

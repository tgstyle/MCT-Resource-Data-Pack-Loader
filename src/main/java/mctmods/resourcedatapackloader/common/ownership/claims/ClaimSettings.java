package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.util.BlockSelection;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Enums;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public final class ClaimSettings {
    public enum Method { COMMAND, ITEM, BLOCK }

    public enum Overlap { REFUSE, SKIP }

    public enum LimitMode { FLAT, PERMEMBER, SCORE }

    public enum Orphan { LOCKED, OPEN }

    public enum FakePlayers { DENY, ALLOW }

    public enum Entities { KEEP, REMOVE }

    public enum FtbPolicy { INDEPENDENT, YIELD, EXCLUSIVE }

    private static final String GROUP = ContentControl.CLAIMS;
    private static boolean foreignAnnounced;
    private static final int MOST = 100000;
    private static volatile View view = build();

    private ClaimSettings() {}

    private static final class View {
        private Set<Method> methods;
        private OwnershipSettings.Who who;
        private int commandRadius;
        private int commandLevel;
        private int opLevel;
        private Set<ResourceLocation> item;
        private boolean itemConsume;
        private BlockSelection block;
        private int blockRadius;
        private Overlap blockOverlap;
        private int checkTicks;
        private LimitMode limitMode;
        private int limit;
        private int limitPerMember;
        private String limitScore;
        private boolean mustTouch;
        private int minDistance;
        private int spawnRadius;
        private Set<String> dimensions;
        private boolean dimensionsAreBlacklist;
        private Orphan orphanAccess;
        private FakePlayers fakePlayers;
        private boolean denyMessage;
        private boolean protectBreak;
        private boolean protectPlace;
        private boolean protectUse;
        private boolean protectExplosions;
        private boolean protectMobGriefing;
        private boolean protectFluids;
        private boolean protectPistons;
        private boolean protectFire;
        private boolean protectPvp;
        private BlockSelection useBlocks;
        private BlockSelection useExceptions;
        private boolean message;
        private boolean messageWilderness;
        private OwnershipSettings.Dissolve onDissolve;
        private boolean restore;
        private boolean regenerate;
        private int restorePerTick;
        private boolean movePlayers;
        private Entities entities;
        private FtbPolicy ftbPolicy;
    }

    public static void reload() { view = build(); }

    public static boolean allows(Method method) { return view.methods.contains(method); }

    public static boolean any() { return !view.methods.isEmpty(); }

    public static OwnershipSettings.Who who() { return view.who; }

    public static int commandRadius() { return view.commandRadius; }

    public static int commandLevel() { return view.commandLevel; }

    public static int opLevel() { return view.opLevel; }

    public static boolean isClaimItem(ItemStack stack) { return !stack.isEmpty() && view.item.contains(BuiltInRegistries.ITEM.getKey(stack.getItem())); }

    public static boolean itemConsume() { return view.itemConsume; }

    public static boolean isClaimBlock(Block block) { return view.block.matches(block); }

    public static int blockRadius() { return view.blockRadius; }

    public static Overlap blockOverlap() { return view.blockOverlap; }

    public static int checkTicks() { return view.checkTicks; }

    public static LimitMode limitMode() { return view.limitMode; }

    public static int limit() { return view.limit; }

    public static int limitPerMember() { return view.limitPerMember; }

    public static String limitScore() { return view.limitScore; }

    public static boolean mustTouch() { return view.mustTouch; }

    public static int minDistance() { return view.minDistance; }

    public static int spawnRadius() { return view.spawnRadius; }

    public static boolean dimensionBarred(ResourceKey<Level> dimension) {
        View held = view;
        return !held.dimensions.isEmpty() && held.dimensions.contains(dimension.location().toString()) == held.dimensionsAreBlacklist;
    }

    public static boolean orphansOpen() { return view.orphanAccess == Orphan.OPEN; }

    public static boolean fakePlayersAllowed() { return view.fakePlayers == FakePlayers.ALLOW; }

    public static boolean denyMessage() { return view.denyMessage; }

    public static boolean protectBreak() { return view.protectBreak; }

    public static boolean placeOpen() { return !view.protectPlace; }

    public static boolean protectUse() { return view.protectUse; }

    public static boolean protectExplosions() { return view.protectExplosions; }

    public static boolean protectMobGriefing() { return view.protectMobGriefing; }

    public static boolean fluidsOpen() { return !view.protectFluids; }

    public static boolean protectPistons() { return view.protectPistons; }

    public static boolean protectFire() { return view.protectFire; }

    public static boolean protectPvp() { return view.protectPvp; }

    public static boolean useProtects(Block block) {
        View held = view;
        return held.useBlocks.matches(block) && !held.useExceptions.matches(block);
    }

    public static boolean message() { return view.message; }

    public static boolean messageWilderness() { return view.messageWilderness; }

    public static OwnershipSettings.Dissolve dissolveMode() { return view.onDissolve; }

    public static boolean restore() { return view.restore; }

    public static boolean regenerate() { return view.regenerate; }

    public static int restorePerTick() { return view.restorePerTick; }

    public static boolean movePlayers() { return view.movePlayers; }

    public static boolean removeEntities() { return view.entities == Entities.REMOVE; }

    public static FtbPolicy ftbPolicy() { return view.ftbPolicy; }

    public static boolean yieldsToForeign() { return view.ftbPolicy != FtbPolicy.INDEPENDENT && ForeignClaimsHolder.get().isActive(); }

    public static void announceForeign() {
        FtbPolicy policy = view.ftbPolicy;
        if (foreignAnnounced || policy == FtbPolicy.INDEPENDENT || ForeignClaimsHolder.get().isActive()) { return; }
        foreignAnnounced = true;
        ContentLog.LOGGER.info("claims ftbPolicy is {} but no FTB claims are active, so claims act as independent", policy.name().toLowerCase(Locale.ROOT));
    }

    private static View build() {
        View built = new View();
        ResourceLocation key = ResourceDataPackLoader.id("claims");
        built.methods = methods(ContentControl.list(GROUP, "methods", Config.claims.methods()));
        built.who = ContentControl.choice(GROUP, "claimWho", Config.claims.claimWho(), OwnershipSettings.Who.TEAM);
        built.commandRadius = ContentControl.ranged(GROUP, "commandRadius", Config.claims.commandRadius(), 0, 64);
        built.commandLevel = ContentControl.ranged(GROUP, "commandLevel", Config.claims.commandLevel(), 0, 4);
        built.opLevel = ContentControl.ranged(GROUP, "opLevel", Config.claims.opLevel(), 0, 4);
        built.item = namedItem(ContentControl.text(GROUP, "item", Config.claims.item()), key);
        built.itemConsume = ContentControl.flag(GROUP, "itemConsume", Config.claims.itemConsume());
        built.block = namedBlock(ContentControl.text(GROUP, "block", Config.claims.block()), key);
        built.blockRadius = ContentControl.ranged(GROUP, "blockRadius", Config.claims.blockRadius(), 0, 64);
        built.blockOverlap = ContentControl.choice(GROUP, "blockOverlap", Config.claims.blockOverlap(), Overlap.REFUSE);
        built.checkTicks = ContentControl.ranged(GROUP, "claimCheckTicks", Config.claims.claimCheckTicks(), 1, 1200);
        built.limitMode = ContentControl.choice(GROUP, "limitMode", Config.claims.limitMode(), LimitMode.FLAT);
        built.limit = ContentControl.ranged(GROUP, "limit", Config.claims.limit(), 0, MOST);
        built.limitPerMember = ContentControl.ranged(GROUP, "limitPerMember", Config.claims.limitPerMember(), 0, MOST);
        built.limitScore = ContentControl.text(GROUP, "limitScore", Config.claims.limitScore()).trim();
        built.mustTouch = ContentControl.flag(GROUP, "mustTouch", Config.claims.mustTouch());
        built.minDistance = ContentControl.ranged(GROUP, "minDistance", Config.claims.minDistance(), 0, 64);
        built.spawnRadius = ContentControl.ranged(GROUP, "spawnRadius", Config.claims.spawnRadius(), 0, 64);
        built.dimensions = new HashSet<>();
        for (String dimension : ContentControl.list(GROUP, "dimensions", Config.claims.dimensions())) {
            if (!Settings.lower(dimension).isEmpty()) { built.dimensions.add(Settings.lower(dimension)); }
        }
        built.dimensionsAreBlacklist = ContentControl.flag(GROUP, "dimensionsAreBlacklist", Config.claims.dimensionsAreBlacklist());
        built.orphanAccess = ContentControl.choice(GROUP, "orphanAccess", Config.claims.orphanAccess(), Orphan.LOCKED);
        built.fakePlayers = ContentControl.choice(GROUP, "fakePlayers", Config.claims.fakePlayers(), FakePlayers.DENY);
        built.denyMessage = ContentControl.flag(GROUP, "denyMessage", Config.claims.denyMessage());
        built.protectBreak = ContentControl.flag(GROUP, "protectBreak", Config.claims.protectBreak());
        built.protectPlace = ContentControl.flag(GROUP, "protectPlace", Config.claims.protectPlace());
        built.protectUse = ContentControl.flag(GROUP, "protectUse", Config.claims.protectUse());
        built.protectExplosions = ContentControl.flag(GROUP, "protectExplosions", Config.claims.protectExplosions());
        built.protectMobGriefing = ContentControl.flag(GROUP, "protectMobGriefing", Config.claims.protectMobGriefing());
        built.protectFluids = ContentControl.flag(GROUP, "protectFluids", Config.claims.protectFluids());
        built.protectPistons = ContentControl.flag(GROUP, "protectPistons", Config.claims.protectPistons());
        built.protectFire = ContentControl.flag(GROUP, "protectFire", Config.claims.protectFire());
        built.protectPvp = ContentControl.flag(GROUP, "protectPvp", Config.claims.protectPvp());
        built.useBlocks = new BlockSelection("claims", "protects", ContentControl.list(GROUP, "useBlocks", Config.claims.useBlocks()), key);
        built.useExceptions = new BlockSelection("claims", "excepts", ContentControl.list(GROUP, "useExceptions", Config.claims.useExceptions()), key);
        built.message = ContentControl.flag(GROUP, "message", Config.claims.message());
        built.messageWilderness = ContentControl.flag(GROUP, "messageWilderness", Config.claims.messageWilderness());
        built.onDissolve = ContentControl.choice(GROUP, "claimOnDissolve", Config.claims.claimOnDissolve(), OwnershipSettings.Dissolve.KEEP);
        built.restore = ContentControl.flag(GROUP, "restore", Config.claims.restore());
        built.regenerate = ContentControl.flag(GROUP, "regenerate", Config.claims.regenerate());
        built.restorePerTick = ContentControl.ranged(GROUP, "restorePerTick", Config.claims.restorePerTick(), 1, MOST);
        built.movePlayers = ContentControl.flag(GROUP, "movePlayers", Config.claims.movePlayers());
        built.entities = ContentControl.choice(GROUP, "entities", Config.claims.entities(), Entities.KEEP);
        built.ftbPolicy = ContentControl.choice(GROUP, "ftbPolicy", "independent", FtbPolicy.INDEPENDENT);
        return built;
    }

    private static Set<Method> methods(List<String> raw) {
        Set<Method> found = EnumSet.noneOf(Method.class);
        for (String each : raw) {
            Method method = Enums.byName(Method.class, each);
            if (method != null) { found.add(method); }
            else { ContentLog.LOGGER.error("claims methods has '{}', which is not command, item or block, skipping it", each.toLowerCase(Locale.ROOT)); }
        }
        return Collections.unmodifiableSet(found);
    }

    private static Set<ResourceLocation> namedItem(String name, ResourceLocation key) {
        ItemSelection selection = new ItemSelection("claim item", "claims");
        selection.readNames(Collections.singletonList(name), key);
        selection.unknown();
        return selection.items();
    }

    private static BlockSelection namedBlock(String name, ResourceLocation key) {
        return new BlockSelection("claim block", "claims", Collections.singletonList(name), key);
    }
}

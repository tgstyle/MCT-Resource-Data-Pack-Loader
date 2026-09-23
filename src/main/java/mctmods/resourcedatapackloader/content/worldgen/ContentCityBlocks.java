package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Hashes;
import mctmods.resourcedatapackloader.util.Settings;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ContentCityBlocks extends StructureProcessor {
    private static final StructureProcessorType<ContentCityBlocks> TYPE = () -> Codec.unit(new ContentCityBlocks(100, false));
    private static final String AT = "at=";
    private static final String UNDER = "under=";
    private static final int ALWAYS = 100;
    private static final Set<String> WARNED = new LinkedHashSet<>();
    private static final List<Rule> RULES = new ArrayList<>();
    private static final ThreadLocal<List<CityRails.Laid>> BORES = ThreadLocal.withInitial(List::of);
    private static boolean read;
    private final int integrity;
    private final boolean ruled;

    private record Rule(ContentStates.Spec from, BlockState to, int chance, @Nullable ContentStates.Spec at, @Nullable ContentStates.Spec under) {
        boolean plain() { return chance >= ALWAYS && at == null && under == null; }
    }

    public ContentCityBlocks(int integrity, boolean ruled) {
        this.integrity = integrity;
        this.ruled = ruled;
    }

    public static int integrityOf(StructurePlaceSettings settings) {
        for (StructureProcessor processor : settings.getProcessors()) {
            if (processor instanceof ContentCityBlocks held) { return held.integrity; }
        }
        return 100;
    }

    public static void bored(List<CityRails.Laid> bores) { BORES.set(bores); }

    public static void unbored() { BORES.remove(); }

    public static void forget() {
        synchronized (RULES) {
            RULES.clear();
            read = false;
        }
    }

    private static void readRules() {
        synchronized (RULES) {
            if (read) { return; }
            read = true;
            for (String entry : ContentControl.list(ContentControl.STRUCTURES, "villageBlocks", Config.worldgen.villageBlocks())) {
                Rule rule = rule(entry);
                if (rule != null) { RULES.add(rule); }
            }
            long plain = RULES.stream().filter(Rule::plain).count();
            if (plain > 0) { ContentLog.LOGGER.info("Villages build with {} replaced block(s), whatever any other mod asks for", plain); }
            if (RULES.size() > plain) { ContentLog.LOGGER.info("Villages weather {} block(s) by rule as their pieces lay them", RULES.size() - plain); }
        }
    }

    private static int split(String text) {
        int depth = 0;
        for (int at = 0; at < text.length(); at++) {
            char held = text.charAt(at);
            if (held == '[' || held == '{') { depth++; }
            else if (held == ']' || held == '}') { depth--; }
            else if (held == '=' && depth == 0) { return at; }
        }
        return -1;
    }

    @Nullable private static Rule rule(String entry) {
        List<String> fields = Settings.entries(entry);
        int split = split(fields.get(0));
        String left = split < 0 ? "" : fields.get(0).substring(0, split).trim();
        String right = split < 0 ? "" : fields.get(0).substring(split + 1).trim();
        if (left.isEmpty() || right.isEmpty()) {
            if (WARNED.add(entry)) { ContentLog.LOGGER.error("villageBlocks entry '{}' is not written as original=replacement, ignoring it", entry); }
            return null;
        }
        ContentStates.Spec from = state(left, entry);
        ContentStates.Spec to = state(right, entry);
        if (from == null || to == null) { return null; }
        int chance = ALWAYS;
        ContentStates.Spec at = null;
        ContentStates.Spec under = null;
        for (int field = 1; field < fields.size(); field++) {
            String said = fields.get(field).trim();
            if (said.isEmpty()) { continue; }
            if (said.startsWith(AT)) {
                at = state(said.substring(AT.length()), entry);
                if (at == null) { return null; }
            }
            else if (said.startsWith(UNDER)) {
                under = state(said.substring(UNDER.length()), entry);
                if (under == null) { return null; }
            }
            else {
                chance = chance(said, entry);
                if (chance < 0) { return null; }
            }
        }
        return new Rule(from, to.state(), chance, at, under);
    }

    @Nullable private static ContentStates.Spec state(String written, String entry) {
        ContentStates.Spec found = ContentStates.spec(written, "villageBlocks entry '" + entry + "'");
        if (found == null && WARNED.add(entry + "|" + written)) { ContentLog.LOGGER.error("villageBlocks entry '{}' names block '{}', which is not registered, ignoring the entry", entry, written); }
        return found;
    }

    private static int chance(String said, String entry) {
        int asked;
        try { asked = Integer.parseInt(said); }
        catch (NumberFormatException wrong) {
            if (WARNED.add(entry)) { ContentLog.LOGGER.error("villageBlocks entry '{}' says '{}', which is neither a chance out of 100 nor an at= or under= block, ignoring the entry", entry, said); }
            return -1;
        }
        if (asked >= 1 && asked <= ALWAYS) { return asked; }
        if (WARNED.add(entry)) { ContentLog.LOGGER.error("villageBlocks entry '{}' asks for a chance of {}, which is not between 1 and 100, ignoring the entry", entry, asked); }
        return -1;
    }

    private static boolean differs(BlockState held, ContentStates.Spec wanted) { return wanted.exact() ? held != wanted.state() : !held.is(wanted.state().getBlock()); }

    static BlockState swapped(BlockState laid) {
        readRules();
        Rule found = null;
        for (Rule rule : RULES) {
            if (!rule.plain()) { continue; }
            if (rule.to() == laid) { return laid; }
            if (differs(laid, rule.from())) { continue; }
            if (found == null || rule.from().exact() || !found.from().exact()) { found = rule; }
        }
        return found == null ? laid : found.to();
    }

    public static BlockState ruled(LevelReader level, long seed, BlockPos pos, BlockState laid) {
        readRules();
        if (RULES.isEmpty()) { return laid; }
        BlockState held = swapped(laid);
        for (Rule rule : RULES) {
            if (rule.plain() || differs(held, rule.from())) { continue; }
            if (rule.at() != null && differs(level.getBlockState(pos), rule.at())) { continue; }
            if (rule.under() != null && differs(level.getBlockState(pos.below()), rule.under())) { continue; }
            if (rule.chance() < ALWAYS && Math.floorMod(Hashes.mix(seed, pos.getX(), pos.getY(), pos.getZ()), ALWAYS) >= rule.chance()) { continue; }
            return rule.to();
        }
        return held;
    }

    @Override @Nullable public StructureTemplate.StructureBlockInfo process(@Nonnull LevelReader level, @Nonnull BlockPos offset, @Nonnull BlockPos pos, @Nonnull StructureTemplate.StructureBlockInfo raw, @Nonnull StructureTemplate.StructureBlockInfo info, @Nonnull StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        BlockPos at = info.pos();
        if (integrity < 100 && Math.floorMod(mix(at), 100) >= integrity) { return null; }
        if (CityRails.insideBore(BORES.get(), at.getX(), at.getY(), at.getZ())) { return null; }
        if (!ruled) { return info; }
        BlockState changed = ruled(level, level instanceof WorldGenLevel held ? held.getSeed() : 0L, at, info.state());
        return changed == info.state() ? info : new StructureTemplate.StructureBlockInfo(at, changed, info.nbt());
    }

    public static long spot(long seed, int x, int z) { return Hashes.mix(seed, x, 0, z); }

    public static long spot(int x, int z) { return mix(new BlockPos(x, 0, z)); }

    private static long mix(BlockPos at) {
        long held = at.getX() * 0x2545F4914F6CDD1DL ^ at.getY() * 0x6C62272E07BB0142L ^ at.getZ() * 0xCBF29CE484222325L;
        held ^= held >>> 33;
        held *= 0xFF51AFD7ED558CCDL;
        return held ^ (held >>> 33);
    }

    @Override @Nonnull protected StructureProcessorType<?> getType() { return TYPE; }
}

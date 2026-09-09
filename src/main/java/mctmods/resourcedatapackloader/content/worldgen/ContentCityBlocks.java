package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ContentCityBlocks extends StructureProcessor {
    private static final StructureProcessorType<ContentCityBlocks> TYPE = () -> MapCodec.unit(new ContentCityBlocks(100));
    private static final Set<String> WARNED = new LinkedHashSet<>();
    private static final Map<String, Rule> RULES = new LinkedHashMap<>();
    private static boolean read;
    private final int integrity;

    public record Rule(BlockState state, int chance) {}

    public ContentCityBlocks(int integrity) { this.integrity = integrity; }

    public static int integrityOf(StructurePlaceSettings settings) {
        for (StructureProcessor processor : settings.getProcessors()) {
            if (processor instanceof ContentCityBlocks held) { return held.integrity; }
        }
        return 100;
    }

    public static void forget() {
        RULES.clear();
        read = false;
    }

    private static void readRules() {
        if (read) { return; }
        read = true;
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "villageBlocks", Config.worldgen.villageBlocks())) {
            String text = entry.trim().toLowerCase(Locale.ROOT);
            int at = text.indexOf('=');
            if (at <= 0) {
                if (WARNED.add(entry)) { ContentLog.LOGGER.error("villageBlocks entry '{}' is not written as original=replacement, so it is left out", entry); }
                continue;
            }
            String from = text.substring(0, at).trim();
            String rest = text.substring(at + 1).trim();
            int chance = 100;
            int comma = rest.indexOf(',');
            if (comma >= 0) {
                try { chance = Integer.parseInt(rest.substring(comma + 1).trim()); }
                catch (NumberFormatException notNumber) {
                    if (WARNED.add(entry)) { ContentLog.LOGGER.error("villageBlocks entry '{}' carries '{}' after the comma, which this line reads only as a chance out of 100, so the entry is left out", entry, rest.substring(comma + 1).trim()); }
                    continue;
                }
                rest = rest.substring(0, comma).trim();
            }
            Block found = Registered.find(BuiltInRegistries.BLOCK, ResourceLocation.tryParse(rest));
            if (found == null) {
                if (WARNED.add(entry)) { ContentLog.LOGGER.error("villageBlocks entry '{}' names block '{}', which is not registered, so it is left out", entry, rest); }
                continue;
            }
            RULES.put(from, new Rule(found.defaultBlockState(), Math.clamp(chance, 1, 100)));
        }
    }

    @Override @Nullable public StructureTemplate.StructureBlockInfo process(@Nonnull LevelReader level, @Nonnull BlockPos offset, @Nonnull BlockPos pos, @Nonnull StructureTemplate.StructureBlockInfo raw, @Nonnull StructureTemplate.StructureBlockInfo info, @Nonnull StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        BlockPos at = info.pos();
        if (integrity < 100 && Math.floorMod(mix(at), 100) >= integrity) { return null; }
        readRules();
        if (RULES.isEmpty()) { return info; }
        ResourceLocation named = BuiltInRegistries.BLOCK.getResourceKey(info.state().getBlock()).map(ResourceKey::location).orElse(null);
        Rule rule = named == null ? null : RULES.get(named.toString());
        if (rule == null) { return info; }
        if (rule.chance() < 100 && Math.floorMod(mix(at) >> 8, 100) >= rule.chance()) { return info; }
        return new StructureTemplate.StructureBlockInfo(at, rule.state(), info.nbt());
    }

    public static long spot(int x, int z) { return mix(new BlockPos(x, 0, z)); }

    private static long mix(BlockPos at) {
        long held = at.getX() * 0x2545F4914F6CDD1DL ^ at.getY() * 0x6C62272E07BB0142L ^ at.getZ() * 0xCBF29CE484222325L;
        held ^= held >>> 33;
        held *= 0xFF51AFD7ED558CCDL;
        return held ^ (held >>> 33);
    }

    @Override @Nonnull protected StructureProcessorType<?> getType() { return TYPE; }
}

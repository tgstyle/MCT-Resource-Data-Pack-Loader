package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.util.worldgen.FillRules;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.Hashes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;

public final class ContentMapFill {
    private static final Set<String> MISSING = new HashSet<>();
    private static final String PIG = "minecraft:pig";

    private ContentMapFill() {}

    public static StructureTemplate.StructureBlockInfo fill(FillRules rules, long seed, StructureTemplate.StructureBlockInfo info, @Nullable String mob, @Nullable ResourceLocation loot) {
        Block block = info.state().getBlock();
        if (block == Blocks.SPAWNER) { return spawner(rules, seed, info, mob); }
        if (block instanceof ChestBlock) { return chest(rules, seed, info, loot); }
        return info;
    }

    private static StructureTemplate.StructureBlockInfo spawner(FillRules rules, long seed, StructureTemplate.StructureBlockInfo info, @Nullable String mob) {
        CompoundTag saved = info.nbt();
        if (mob == null && rules.spawners().isEmpty() || keepsSpawner(saved)) { return info; }
        String named = mob;
        if (named == null) {
            List<PickDef> usable = new ArrayList<>();
            for (PickDef pick : rules.spawners()) {
                ResourceLocation id = ResourceLocation.tryParse(pick.name());
                if (id != null && ForgeRegistries.ENTITY_TYPES.containsKey(id)) { usable.add(pick); }
                else { ContentLog.LOGGER.errorOnce(MISSING, pick.name(), "Structure map {} gives spawners '{}', which nothing registers, so it is left out of the roll", rules.key(), pick.name()); }
            }
            named = PickDef.pick(usable, RandomSource.create(Hashes.mix(seed, info.pos().getX(), info.pos().getY(), info.pos().getZ())));
        }
        if (named == null) { return info; }
        CompoundTag copy = copyOrNew(saved);
        CompoundTag entity = new CompoundTag();
        entity.putString("id", ResourceLocation.parse(named).toString());
        CompoundTag data = new CompoundTag();
        data.put("entity", entity);
        copy.put("SpawnData", data);
        copy.remove("SpawnPotentials");
        return new StructureTemplate.StructureBlockInfo(info.pos(), info.state(), copy);
    }

    private static StructureTemplate.StructureBlockInfo chest(FillRules rules, long seed, StructureTemplate.StructureBlockInfo info, @Nullable ResourceLocation override) {
        CompoundTag saved = info.nbt();
        ResourceLocation loot = override != null ? override : rules.loot();
        if (loot == null || keepsChest(saved)) { return info; }
        return new StructureTemplate.StructureBlockInfo(info.pos(), info.state(), lootTag(saved, loot, seed, info.pos()));
    }

    public static CompoundTag lootTag(@Nullable CompoundTag saved, ResourceLocation table, long seed, BlockPos at) {
        CompoundTag copy = copyOrNew(saved);
        copy.putString("LootTable", table.toString());
        copy.putLong("LootTableSeed", new Random(Hashes.mix(seed, at.getX(), at.getY(), at.getZ())).nextLong());
        return copy;
    }

    private static CompoundTag copyOrNew(@Nullable CompoundTag saved) { return saved == null ? new CompoundTag() : saved.copy(); }

    public static boolean keepsSpawner(@Nullable CompoundTag saved) {
        if (saved == null) { return false; }
        if (keepsMob(saved.getCompound("SpawnData").getCompound("entity"))) { return true; }
        ListTag potentials = saved.getList("SpawnPotentials", Tag.TAG_COMPOUND);
        for (int at = 0; at < potentials.size(); at++) {
            if (keepsMob(potentials.getCompound(at).getCompound("data").getCompound("entity"))) { return true; }
        }
        return false;
    }

    private static boolean keepsMob(CompoundTag mob) {
        String id = mob.getString("id");
        return !id.isEmpty() && (mob.size() > 1 || !PIG.equals(id));
    }

    public static boolean keepsChest(@Nullable CompoundTag saved) { return saved != null && (saved.contains("LootTable", Tag.TAG_STRING) || !saved.getList("Items", Tag.TAG_COMPOUND).isEmpty()); }
}

package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.StructureMapDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Hashes;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class ContentMapFill {
    private static final Set<String> MISSING = new HashSet<>();
    private static final String PIG = "minecraft:pig";

    private ContentMapFill() {}

    static StructureTemplate.StructureBlockInfo fill(StructureMapDef def, long seed, StructureTemplate.StructureBlockInfo info) {
        Block block = info.state().getBlock();
        if (block == Blocks.SPAWNER) { return spawner(def, seed, info); }
        if (block instanceof ChestBlock) { return chest(def, info); }
        return info;
    }

    private static StructureTemplate.StructureBlockInfo spawner(StructureMapDef def, long seed, StructureTemplate.StructureBlockInfo info) {
        CompoundTag saved = info.nbt();
        if (def.spawners().isEmpty() || !blankSpawner(saved)) { return info; }
        List<PickDef> usable = new ArrayList<>();
        for (PickDef pick : def.spawners()) {
            ResourceLocation id = ResourceLocation.tryParse(pick.name());
            if (id != null && ForgeRegistries.ENTITY_TYPES.containsKey(id)) { usable.add(pick); }
            else { ContentLog.LOGGER.errorOnce(MISSING, pick.name(), "Structure map {} gives spawners '{}', which nothing registers, so it is left out of the roll", def.key(), pick.name()); }
        }
        String named = PickDef.pick(usable, RandomSource.create(Hashes.mix(seed, info.pos().getX(), info.pos().getY(), info.pos().getZ())));
        if (named == null) { return info; }
        CompoundTag copy = saved == null ? new CompoundTag() : saved.copy();
        CompoundTag mob = new CompoundTag();
        mob.putString("id", ResourceLocation.parse(named).toString());
        CompoundTag data = new CompoundTag();
        data.put("entity", mob);
        copy.put("SpawnData", data);
        copy.remove("SpawnPotentials");
        return new StructureTemplate.StructureBlockInfo(info.pos(), info.state(), copy);
    }

    private static StructureTemplate.StructureBlockInfo chest(StructureMapDef def, StructureTemplate.StructureBlockInfo info) {
        CompoundTag saved = info.nbt();
        if (def.loot() == null || !blankChest(saved)) { return info; }
        CompoundTag copy = saved == null ? new CompoundTag() : saved.copy();
        copy.putString("LootTable", def.loot().toString());
        return new StructureTemplate.StructureBlockInfo(info.pos(), info.state(), copy);
    }

    private static boolean blankSpawner(CompoundTag saved) {
        if (saved == null) { return true; }
        if (keepsMob(saved.getCompound("SpawnData").getCompound("entity"))) { return false; }
        ListTag potentials = saved.getList("SpawnPotentials", Tag.TAG_COMPOUND);
        for (int at = 0; at < potentials.size(); at++) {
            if (keepsMob(potentials.getCompound(at).getCompound("data").getCompound("entity"))) { return false; }
        }
        return true;
    }

    private static boolean keepsMob(CompoundTag mob) {
        String id = mob.getString("id");
        return !id.isEmpty() && (mob.size() > 1 || !PIG.equals(id));
    }

    private static boolean blankChest(CompoundTag saved) { return saved == null || !saved.contains("LootTable", Tag.TAG_STRING) && saved.getList("Items", Tag.TAG_COMPOUND).isEmpty(); }
}

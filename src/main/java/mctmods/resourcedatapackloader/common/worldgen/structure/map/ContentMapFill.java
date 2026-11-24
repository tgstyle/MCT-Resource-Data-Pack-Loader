package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.util.worldgen.FillRules;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class ContentMapFill {
    private static final Set<String> MISSING = new HashSet<>();
    private static final String PIG = "minecraft:pig";
    public static final String SPAWN_DATA = "SpawnData";
    public static final String SPAWN_POTENTIALS = "SpawnPotentials";
    public static final String ENTITY = "Entity";
    public static final String ID = "id";

    private ContentMapFill() {}

    public static Template.BlockInfo fill(FillRules rules, long seed, BlockPos at, Template.BlockInfo info, @Nullable String mob, @Nullable ResourceLocation loot) {
        Block block = info.blockState.getBlock();
        if (block == Blocks.MOB_SPAWNER) { return spawner(rules, seed, at, info, mob); }
        if (block instanceof BlockChest) { return chest(rules, seed, at, info, loot); }
        return info;
    }

    private static Template.BlockInfo spawner(FillRules rules, long seed, BlockPos at, Template.BlockInfo info, @Nullable String mob) {
        NBTTagCompound saved = info.tileentityData;
        if (mob == null && rules.spawners.isEmpty() || !blankSpawner(saved)) { return info; }
        String named = mob;
        if (named == null) {
            List<PickDef> usable = new ArrayList<>();
            for (PickDef pick : rules.spawners) {
                if (ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(pick.name))) { usable.add(pick); }
                else { ContentLog.LOGGER.errorOnce(MISSING, pick.name, "Structure map {} gives spawners '{}', which nothing registers, so it is left out of the roll", rules.key, pick.name); }
            }
            named = PickDef.pick(usable, new Random(MathUtil.mix(seed, at.getX(), at.getY(), at.getZ())), null);
        }
        if (named == null) { return info; }
        NBTTagCompound copy = copyOrNew(saved);
        NBTTagCompound spawnData = new NBTTagCompound();
        spawnData.setString(ID, new ResourceLocation(named).toString());
        copy.setTag(SPAWN_DATA, spawnData);
        copy.removeTag(SPAWN_POTENTIALS);
        return new Template.BlockInfo(at, info.blockState, copy);
    }

    private static Template.BlockInfo chest(FillRules rules, long seed, BlockPos at, Template.BlockInfo info, @Nullable ResourceLocation override) {
        NBTTagCompound saved = info.tileentityData;
        ResourceLocation loot = override != null ? override : rules.loot;
        if (loot == null || !blankChest(saved)) { return info; }
        return new Template.BlockInfo(at, info.blockState, lootTag(saved, loot, seed, at));
    }

    public static NBTTagCompound lootTag(@Nullable NBTTagCompound saved, ResourceLocation table, long seed, BlockPos at) {
        NBTTagCompound copy = copyOrNew(saved);
        copy.setString("LootTable", table.toString());
        copy.setLong("LootTableSeed", new Random(MathUtil.mix(seed, at.getX(), at.getY(), at.getZ())).nextLong());
        return copy;
    }

    private static NBTTagCompound copyOrNew(@Nullable NBTTagCompound saved) { return saved == null ? new NBTTagCompound() : saved.copy(); }

    private static boolean blankSpawner(NBTTagCompound saved) {
        if (saved == null) { return true; }
        if (saved.hasKey(SPAWN_DATA, 10) && keepsMob(saved.getCompoundTag(SPAWN_DATA))) { return false; }
        NBTTagList potentials = saved.getTagList(SPAWN_POTENTIALS, 10);
        for (int at = 0; at < potentials.tagCount(); at++) {
            if (keepsMob(potentials.getCompoundTagAt(at).getCompoundTag(ENTITY))) { return false; }
        }
        return true;
    }

    private static boolean keepsMob(NBTTagCompound mob) { return mob.getSize() > 1 || mob.getSize() == 1 && !PIG.equals(mob.getString(ID)); }

    private static boolean blankChest(NBTTagCompound saved) {
        if (saved == null) { return true; }
        return !saved.hasKey("LootTable", 8) && saved.getTagList("Items", 10).tagCount() == 0;
    }
}

package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.StructureMapDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.MathUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

final class ContentMapFill {
    private static final Set<String> MISSING = new HashSet<>();
    private static final String PIG = "minecraft:pig";

    private ContentMapFill() {}

    static Template.BlockInfo fill(StructureMapDef def, long seed, BlockPos at, Template.BlockInfo info) {
        Block block = info.blockState.getBlock();
        if (block == Blocks.MOB_SPAWNER) { return spawner(def, seed, at, info); }
        if (block instanceof BlockChest) { return chest(def, seed, at, info); }
        return info;
    }

    private static Template.BlockInfo spawner(StructureMapDef def, long seed, BlockPos at, Template.BlockInfo info) {
        NBTTagCompound saved = info.tileentityData;
        if (def.spawners.isEmpty() || !blankSpawner(saved)) { return info; }
        List<PickDef> usable = new ArrayList<>();
        for (PickDef pick : def.spawners) {
            if (ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(pick.name))) { usable.add(pick); }
            else { ContentLog.LOGGER.errorOnce(MISSING, pick.name, "Structure map {} gives spawners '{}', which nothing registers, so it is left out of the roll", def.key, pick.name); }
        }
        String named = PickDef.pick(usable, new Random(MathUtil.mix(seed, at.getX(), at.getY(), at.getZ())), null);
        if (named == null) { return info; }
        NBTTagCompound copy = saved == null ? new NBTTagCompound() : saved.copy();
        NBTTagCompound mob = new NBTTagCompound();
        mob.setString("id", new ResourceLocation(named).toString());
        copy.setTag("SpawnData", mob);
        copy.removeTag("SpawnPotentials");
        return new Template.BlockInfo(at, info.blockState, copy);
    }

    private static Template.BlockInfo chest(StructureMapDef def, long seed, BlockPos at, Template.BlockInfo info) {
        NBTTagCompound saved = info.tileentityData;
        if (def.loot == null || !blankChest(saved)) { return info; }
        NBTTagCompound copy = saved == null ? new NBTTagCompound() : saved.copy();
        copy.setString("LootTable", def.loot.toString());
        copy.setLong("LootTableSeed", new Random(MathUtil.mix(seed, at.getX(), at.getY(), at.getZ())).nextLong());
        return new Template.BlockInfo(at, info.blockState, copy);
    }

    private static boolean blankSpawner(NBTTagCompound saved) {
        if (saved == null) { return true; }
        if (saved.hasKey("SpawnData", 10) && keepsMob(saved.getCompoundTag("SpawnData"))) { return false; }
        NBTTagList potentials = saved.getTagList("SpawnPotentials", 10);
        for (int at = 0; at < potentials.tagCount(); at++) {
            if (keepsMob(potentials.getCompoundTagAt(at).getCompoundTag("Entity"))) { return false; }
        }
        return true;
    }

    private static boolean keepsMob(NBTTagCompound mob) { return mob.getSize() > 1 || mob.getSize() == 1 && !PIG.equals(mob.getString("id")); }

    private static boolean blankChest(NBTTagCompound saved) {
        if (saved == null) { return true; }
        return !saved.hasKey("LootTable", 8) && saved.getTagList("Items", 10).tagCount() == 0;
    }
}

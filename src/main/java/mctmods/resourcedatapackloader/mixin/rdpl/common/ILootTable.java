package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(LootTable.class) public interface ILootTable { @Accessor("pools") List<LootPool> rdpl$getPools(); }

package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MapGenStructure.class) public interface IMapGenStructureSpawn {
    @Invoker("canSpawnStructureAtCoords") boolean rdpl$canSpawnStructureAtCoords(int chunkX, int chunkZ);

    @Invoker("getStructureStart") StructureStart rdpl$getStructureStart(int chunkX, int chunkZ);
}

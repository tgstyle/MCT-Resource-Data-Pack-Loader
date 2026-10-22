package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnSwitch;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldEntitySpawner.class) public class MixinWorldEntitySpawnerCaps {
    @ModifyVariable(method = "findChunksForSpawning", name = "i", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;countEntities(Lnet/minecraft/entity/EnumCreatureType;Z)I", remap = false)) private int rdpl$units(int i) { return ContentSpawnSwitch.units(i); }

    @Redirect(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;countEntities(Lnet/minecraft/entity/EnumCreatureType;Z)I", remap = false)) private int rdpl$count(WorldServer world, EnumCreatureType type, boolean forSpawnCount) { return ContentSpawnSwitch.record(world, type, world.countEntities(type, forSpawnCount)); }

    @Redirect(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EnumCreatureType;getMaxNumberOfCreature()I")) private int rdpl$ceiling(EnumCreatureType type) { return ContentSpawnSwitch.ceiling(type); }
}

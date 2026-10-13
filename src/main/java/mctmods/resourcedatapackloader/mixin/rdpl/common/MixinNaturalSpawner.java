package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentSkyDecoration;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NaturalSpawner.class) public abstract class MixinNaturalSpawner {
    @Redirect(method = "getTopNonCollidingPos(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/world/entity/EntityType;II)Lnet/minecraft/core/BlockPos;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelReader;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I")) private static int rdpl$skyGround(LevelReader level, Heightmap.Types type, int x, int z) {
        int found = level.getHeight(type, x, z);
        return level instanceof WorldGenLevel region ? ContentSkyDecoration.surface(level, region.getLevel().getChunkSource().getGenerator(), type, x, z, found) : found;
    }
}

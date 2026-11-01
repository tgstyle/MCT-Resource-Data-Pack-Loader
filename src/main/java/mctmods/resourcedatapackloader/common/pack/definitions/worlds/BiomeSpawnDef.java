package mctmods.resourcedatapackloader.common.pack.definitions.worlds;

import net.minecraft.resources.Identifier;

public record BiomeSpawnDef(Identifier entity, String category, int weight, int min, int max) {}

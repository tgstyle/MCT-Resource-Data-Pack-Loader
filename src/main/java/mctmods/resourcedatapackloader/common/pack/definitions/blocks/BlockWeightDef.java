package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import net.minecraft.resources.Identifier;
import java.util.Map;

public record BlockWeightDef(Identifier block, int weight, Map<String, String> properties) {}

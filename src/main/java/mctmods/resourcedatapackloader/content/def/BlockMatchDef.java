package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.Map;

public record BlockMatchDef(Identifier block, Map<String, String> properties) {}

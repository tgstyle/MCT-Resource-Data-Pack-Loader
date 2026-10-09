package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;

public record VillagerDef(Identifier key, String jobSite, String workSound, List<String> requires) {}

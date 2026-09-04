package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record VillagerDef(ResourceLocation key, String texture, String zombieTexture, List<String> careers, String jobSite, String workSound, List<String> requires) {}

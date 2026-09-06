package mctmods.resourcedatapackloader.content.def;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public record WorldTemplateDef(ResourceLocation key, String name, @Nullable JsonObject settings, List<String> requires) {}

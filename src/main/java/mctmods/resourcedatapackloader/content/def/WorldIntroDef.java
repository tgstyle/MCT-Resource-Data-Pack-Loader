package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public record WorldIntroDef(ResourceLocation key, boolean once, @Nullable ResourceLocation music, List<IntroPageDef> pages, List<String> requires) {}

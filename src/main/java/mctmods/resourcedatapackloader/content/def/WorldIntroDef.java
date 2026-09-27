package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;
import javax.annotation.Nullable;

public record WorldIntroDef(Identifier key, boolean once, @Nullable Identifier music, List<IntroPageDef> pages, List<String> requires) {}

package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import javax.annotation.Nullable;

public record RainDef(Identifier particle, Identifier sound, float volume, int interval, int color, int snowColor, float angle, float heading, @Nullable WindDef wind) {}

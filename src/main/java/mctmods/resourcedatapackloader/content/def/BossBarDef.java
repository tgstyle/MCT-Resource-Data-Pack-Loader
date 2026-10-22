package mctmods.resourcedatapackloader.content.def;

import net.minecraft.world.BossEvent;
import javax.annotation.Nullable;

public record BossBarDef(String name, BossEvent.BossBarColor color, BossEvent.BossBarOverlay style, @Nullable BossBackgroundDef background) {}

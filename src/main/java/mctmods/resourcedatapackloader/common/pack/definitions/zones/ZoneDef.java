package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import java.util.EnumSet;
import javax.annotation.Nullable;

public record ZoneDef(String id, String dimension, @Nullable int[] box, boolean claim, boolean exceptCreative, String exceptTag, @Nullable ZoneListDef breaks, @Nullable ZoneListDef places, @Nullable ZoneListDef uses, @Nullable ZoneListDef opens, boolean noFly, String message, EnumSet<ZoneTool> denyTools, @Nullable ZoneDamageDef damage) {}

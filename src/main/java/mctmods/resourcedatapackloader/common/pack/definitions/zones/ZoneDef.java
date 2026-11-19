package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record ZoneDef(String id, String dimension, @Nullable BoundingBox box, boolean claim, boolean exceptCreative, String exceptTag, @Nullable ZoneListDef breaks, @Nullable ZoneListDef places, @Nullable ZoneListDef uses, @Nullable ZoneListDef opens, boolean noFly, String message, EnumSet<ZoneTool> denyTools, @Nullable ZoneDamageDef damage) {}

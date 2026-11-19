package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import java.util.EnumSet;
import javax.annotation.Nullable;

public final class ZoneDef {
    public final String id;
    public final String dimension;
    @Nullable public final int[] box;
    public final boolean claim;
    public final boolean exceptCreative;
    public final String exceptTag;
    @Nullable public final ZoneListDef breaks;
    @Nullable public final ZoneListDef places;
    @Nullable public final ZoneListDef uses;
    @Nullable public final ZoneListDef opens;
    public final boolean noFly;
    public final String message;
    public final EnumSet<ZoneTool> denyTools;
    @Nullable public final ZoneDamageDef damage;

    public ZoneDef(String id, String dimension, @Nullable int[] box, boolean claim, boolean exceptCreative, String exceptTag, @Nullable ZoneListDef breaks, @Nullable ZoneListDef places, @Nullable ZoneListDef uses, @Nullable ZoneListDef opens, boolean noFly, String message, EnumSet<ZoneTool> denyTools, @Nullable ZoneDamageDef damage) {
        this.denyTools = denyTools;
        this.damage = damage;
        this.id = id;
        this.dimension = dimension;
        this.box = box;
        this.claim = claim;
        this.exceptCreative = exceptCreative;
        this.exceptTag = exceptTag;
        this.breaks = breaks;
        this.places = places;
        this.uses = uses;
        this.opens = opens;
        this.noFly = noFly;
        this.message = message;
    }
}

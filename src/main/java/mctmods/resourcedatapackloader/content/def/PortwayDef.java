package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public record PortwayDef(String activateSound, String travelSound, List<String> names, List<String> allow, List<String> deny, boolean crossDimension, String gate, String network, String scope, boolean tall, boolean facing,
                         String activateParticle, String travelParticle, CarryDef carry, CostDef cost, PortwayMenu menu) {
    public static final PortwayDef PLAIN = new PortwayDef("minecraft:block.beacon.activate", "minecraft:block.portal.travel", List.of(), List.of(), List.of(), true, "", "", GateDef.PLAYER, false, false, "", "", CarryDef.NONE, CostDef.NONE, PortwayMenu.PLAIN);

    public boolean lists(String dimension) { return !deny.contains(dimension) && (allow.isEmpty() || allow.contains(dimension)); }

    public boolean global() { return GateDef.GLOBAL.equals(scope); }

    public boolean teamWide() { return GateDef.TEAM.equals(scope); }
}

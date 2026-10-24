package mctmods.resourcedatapackloader.content.def;

import java.util.Collections;
import java.util.List;

public final class PortwayDef {
    public static final PortwayDef PLAIN = new PortwayDef("minecraft:block.beacon.activate", "minecraft:block.portal.travel", Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), true, "", "", GateDef.PLAYER, false, false, "", "", CarryDef.NONE, PortwayCost.FREE, PortwayMenu.PLAIN);
    public final String activateSound;
    public final String travelSound;
    public final List<String> names;
    public final List<Integer> allow;
    public final List<Integer> deny;
    public final boolean crossDimension;
    public final String gate;
    public final String network;
    public final String scope;
    public final boolean tall;
    public final boolean facing;
    public final String activateParticle;
    public final String travelParticle;
    public final CarryDef carry;
    public final PortwayCost cost;
    public final PortwayMenu menu;

    public PortwayDef(String activateSound, String travelSound, List<String> names, List<Integer> allow, List<Integer> deny, boolean crossDimension, String gate, String network, String scope, boolean tall, boolean facing, String activateParticle, String travelParticle, CarryDef carry, PortwayCost cost, PortwayMenu menu) {
        this.activateSound = activateSound;
        this.travelSound = travelSound;
        this.names = names;
        this.allow = allow;
        this.deny = deny;
        this.crossDimension = crossDimension;
        this.gate = gate;
        this.network = network;
        this.scope = scope;
        this.tall = tall;
        this.facing = facing;
        this.activateParticle = activateParticle;
        this.travelParticle = travelParticle;
        this.carry = carry;
        this.cost = cost;
        this.menu = menu;
    }

    public boolean global() { return GateDef.GLOBAL.equals(scope); }

    public boolean teamWide() { return GateDef.TEAM.equals(scope); }

    public boolean bars(int dimension) { return deny.contains(dimension) || !allow.isEmpty() && !allow.contains(dimension); }
}

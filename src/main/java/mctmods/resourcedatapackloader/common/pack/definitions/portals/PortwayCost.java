package mctmods.resourcedatapackloader.common.pack.definitions.portals;

import mctmods.resourcedatapackloader.common.pack.definitions.effects.CostPart;

public final class PortwayCost {
    public static final PortwayCost FREE = new PortwayCost(CostPart.FREE, CostPart.FREE, CostPart.FREE, CostPart.FREE, 0);
    public final CostPart xp;
    public final CostPart item;
    public final CostPart health;
    public final CostPart hunger;
    public final int cooldown;

    public PortwayCost(CostPart xp, CostPart item, CostPart health, CostPart hunger, int cooldown) {
        this.xp = xp;
        this.item = item;
        this.health = health;
        this.hunger = hunger;
        this.cooldown = cooldown;
    }
}

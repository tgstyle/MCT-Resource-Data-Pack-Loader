package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import java.util.List;

public final class HurtRuleDef {
    public final List<String> attackers;
    public final List<String> victims;
    public final boolean cancel;
    public final String runs;

    public HurtRuleDef(List<String> attackers, List<String> victims, boolean cancel, String runs) {
        this.attackers = attackers;
        this.victims = victims;
        this.cancel = cancel;
        this.runs = runs;
    }
}

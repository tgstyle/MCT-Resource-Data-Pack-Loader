package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import java.util.List;

public record HurtRuleDef(List<String> attackers, List<String> victims, boolean cancel, String runs) {}

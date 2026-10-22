package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import java.util.Map;

public record MobSwitchDef(String owner, String name, List<String> entities, float health, float damage, float speed, Map<String, Gear> equipment) {
    public static final MobSwitchDef NONE = new MobSwitchDef("", "", List.of(), 1.0F, 1.0F, 1.0F, Map.of());

    public record Gear(String item, float chance) {}
}

package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class MobSwitchDef {
    public static final MobSwitchDef NONE = new MobSwitchDef("", "");
    public final String owner;
    public final String name;
    public final Set<Identifier> ids = new LinkedHashSet<>();
    public final Set<String> kinds = new LinkedHashSet<>();
    public final Map<EquipmentSlot, Gear> equipment = new LinkedHashMap<>();
    public float health = 1.0F;
    public float damage = 1.0F;
    public float speed = 1.0F;

    public MobSwitchDef(String owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    public record Gear(Identifier item, float chance) {}
}

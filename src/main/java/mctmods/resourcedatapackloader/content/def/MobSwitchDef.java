package mctmods.resourcedatapackloader.content.def;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class MobSwitchDef {
    public static final MobSwitchDef NONE = new MobSwitchDef("", "", Collections.emptyList(), 1.0F, 1.0F, 1.0F, Collections.emptyMap());
    public final String owner;
    public final String name;
    public final List<String> entities;
    public final float health;
    public final float damage;
    public final float speed;
    public final Map<String, Gear> equipment;

    public MobSwitchDef(String owner, String name, List<String> entities, float health, float damage, float speed, Map<String, Gear> equipment) {
        this.owner = owner;
        this.name = name;
        this.entities = Collections.unmodifiableList(entities);
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.equipment = Collections.unmodifiableMap(equipment);
    }

    public static final class Gear {
        public final String item;
        public final float chance;

        public Gear(String item, float chance) {
            this.item = item;
            this.chance = chance;
        }
    }
}

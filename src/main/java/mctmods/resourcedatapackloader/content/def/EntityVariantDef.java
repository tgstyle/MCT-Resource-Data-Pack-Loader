package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;

public record EntityVariantDef(ResourceLocation key, ResourceLocation base, String name, boolean showName, String texture, String lootTable, String profession, int career, float baby,
                               List<PickDef> becomes, Sounds sounds, List<String> immuneTo, Physics physics, int experience, float absorption, String creatureAttribute, Map<String, Integer> effects,
                               boolean despawns, int despawnTicks, Flags flags, float dropChance, float scale, float angryScale, float width, float height, Map<String, Float> pathPriorities,
                               Egg egg, Tracking tracking, Map<String, Double> attributes, boolean hostile, boolean passive, List<String> targets, int tint, List<String> tintParts,
                               Combat combat, int threatLeast, int threatHostile, Map<String, String> equipment, List<SpawnEntryDef> spawns, List<String> biomes, List<String> biomeTypes,
                               List<String> requires, List<TaskDef> tasks) {
    public static final String BODY = "body";
    public static final String ARMOR = "armor";
    public static final String HELD = "held";
    public static final List<String> PARTS = List.of(BODY, ARMOR, HELD);

    public record Sounds(String ambient, String hurt, String death, float volume, float pitch) {}

    public record Physics(float jumpMultiplier, float fallDamage, int maxFallHeight, float waterSlowdown, boolean breathesUnderwater, boolean swims, boolean amphibious) {}

    public record Flags(boolean noAI, boolean leftHanded, boolean fireproof, boolean invulnerable, boolean glowing, boolean invisible, boolean persistent, boolean silent, boolean picksUpLoot,
                        boolean hideArmor, boolean hideHeld, boolean leashable, boolean steerable, boolean ignoresSpawnRules) {}

    public record Egg(boolean wanted, int primary, int secondary) {}

    public record Tracking(int range, int frequency, boolean velocity) {}

    public record Combat(boolean explodes, float explosionPower, int explosionFuse, boolean explosionFire, boolean throwsItems, int throwReload, int throwRetreat, int throwAmmo, float throwPower,
                         float throwArc, boolean charges, boolean pounces, int sniffs, boolean sleepsByDay, int home, float fleesWhenHurt, boolean patrols, boolean swoops, boolean gusts, float gustPower) {
        public boolean any() { return explodes || throwsItems || charges || pounces || sniffs > 0 || fleesWhenHurt > 0.0F || patrols || swoops || gusts; }
    }

    public boolean keepsSize() { return scale == 1.0F && angryScale == scale; }
}

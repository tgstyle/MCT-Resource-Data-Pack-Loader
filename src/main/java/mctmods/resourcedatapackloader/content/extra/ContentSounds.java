package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentSounds {
    private static final Set<ResourceLocation> NAMES = new LinkedHashSet<>();
    private static final Map<String, String> RENAMED = Map.ofEntries(
            Map.entry("entity.enderdragon_fireball.explode", "entity.dragon_fireball.explode"), Map.entry("entity.small_slime.death", "entity.slime.death_small"), Map.entry("entity.small_slime.hurt", "entity.slime.hurt_small"),
            Map.entry("entity.small_slime.jump", "entity.slime.jump_small"), Map.entry("entity.small_slime.squish", "entity.slime.squish_small"), Map.entry("entity.small_magmacube.death", "entity.magma_cube.death_small"),
            Map.entry("entity.small_magmacube.hurt", "entity.magma_cube.hurt_small"), Map.entry("entity.small_magmacube.squish", "entity.magma_cube.squish_small"), Map.entry("entity.polar_bear.baby_ambient", "entity.polar_bear.ambient_baby"),
            Map.entry("entity.villager.trading", "entity.villager.trade"), Map.entry("entity.zombie.attack_door_wood", "entity.zombie.attack_wooden_door"), Map.entry("entity.zombie.break_door_wood", "entity.zombie.break_wooden_door"),
            Map.entry("entity.parrot.imitate.enderdragon", "entity.parrot.imitate.ender_dragon"), Map.entry("entity.parrot.imitate.evocation_illager", "entity.parrot.imitate.evoker"), Map.entry("entity.parrot.imitate.illusion_illager", "entity.parrot.imitate.illusioner"),
            Map.entry("entity.parrot.imitate.magmacube", "entity.parrot.imitate.magma_cube"), Map.entry("entity.parrot.imitate.vindication_illager", "entity.parrot.imitate.vindicator"), Map.entry("music.nether", "music.nether.nether_wastes"));
    private static final List<Map.Entry<String, String>> RENAMED_PREFIXES = List.of(
            Map.entry("block.cloth.", "block.wool."), Map.entry("block.enderchest.", "block.ender_chest."), Map.entry("block.metal_pressureplate.", "block.metal_pressure_plate."),
            Map.entry("block.note.", "block.note_block."), Map.entry("block.slime.", "block.slime_block."), Map.entry("block.stone_pressureplate.", "block.stone_pressure_plate."),
            Map.entry("block.waterlily.", "block.lily_pad."), Map.entry("block.wood_button.", "block.wooden_button."), Map.entry("block.wood_pressureplate.", "block.wooden_pressure_plate."),
            Map.entry("entity.armorstand.", "entity.armor_stand."), Map.entry("entity.bobber.", "entity.fishing_bobber."), Map.entry("entity.enderdragon.", "entity.ender_dragon."),
            Map.entry("entity.endereye.", "entity.ender_eye."), Map.entry("entity.endermen.", "entity.enderman."), Map.entry("entity.enderpearl.", "entity.ender_pearl."),
            Map.entry("entity.evocation_fangs.", "entity.evoker_fangs."), Map.entry("entity.evocation_illager.", "entity.evoker."), Map.entry("entity.firework.", "entity.firework_rocket."),
            Map.entry("entity.illusion_illager.", "entity.illusioner."), Map.entry("entity.irongolem.", "entity.iron_golem."), Map.entry("entity.itemframe.", "entity.item_frame."),
            Map.entry("entity.leashknot.", "entity.leash_knot."), Map.entry("entity.lightning.", "entity.lightning_bolt."), Map.entry("entity.lingeringpotion.", "entity.lingering_potion."),
            Map.entry("entity.magmacube.", "entity.magma_cube."), Map.entry("entity.snowman.", "entity.snow_golem."), Map.entry("entity.vindication_illager.", "entity.vindicator."),
            Map.entry("entity.zombie_pig.", "entity.zombified_piglin."), Map.entry("record.", "music_disc."));
    private static boolean loaded;

    private ContentSounds() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.contentOff() || !Config.content.sounds()) { return; }
        PackManager.get().forEach(PackManager.SOUNDS, PackManager.JSON, (namespace, path, contents) -> NAMES.add(ResourceLocation.fromNamespaceAndPath(namespace, path)));
    }

    public static void register(RegisterEvent.RegisterHelper<SoundEvent> helper) {
        load();
        int count = 0;
        for (ResourceLocation name : NAMES) {
            if (BuiltInRegistries.SOUND_EVENT.containsKey(name)) {
                ContentLog.LOGGER.warn("A sound named {} is already registered, skipping the pack entry", name);
                continue;
            }
            helper.register(name, SoundEvent.createVariableRangeEvent(name));
            count++;
        }
        if (count > 0) { Summary.info("sounds", "Registered " + count + " sound event(s) from packs"); }
    }

    @Nullable public static SoundEvent find(@Nullable String name) { return Registered.find(BuiltInRegistries.SOUND_EVENT, ContentParser.location(modern(name))); }

    private static String modern(@Nullable String name) {
        String named = name == null ? "" : name.trim();
        String path = named.startsWith("minecraft:") ? named.substring("minecraft:".length()) : named;
        if (path.indexOf(':') >= 0) { return named; }
        String renamed = RENAMED.get(path);
        if (renamed != null) { return "minecraft:" + renamed; }
        for (Map.Entry<String, String> prefix : RENAMED_PREFIXES) {
            if (path.startsWith(prefix.getKey())) { return "minecraft:" + prefix.getValue() + path.substring(prefix.getKey().length()); }
        }
        return named;
    }
}

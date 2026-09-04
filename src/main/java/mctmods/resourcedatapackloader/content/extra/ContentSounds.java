package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ContentSounds {
    private static final Set<ResourceLocation> NAMES = new LinkedHashSet<>();
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
            if (ForgeRegistries.SOUND_EVENTS.containsKey(name)) {
                ContentLog.LOGGER.warn("A sound named {} is already registered, skipping the pack entry", name);
                continue;
            }
            helper.register(name, SoundEvent.createVariableRangeEvent(name));
            count++;
        }
        if (count > 0) { Summary.info("sounds", "Registered " + count + " sound event(s) from packs"); }
    }
}

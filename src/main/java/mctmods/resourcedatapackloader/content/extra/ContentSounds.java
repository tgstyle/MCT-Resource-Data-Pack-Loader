package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.ContentOwners;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ContentSounds {
    private static final Set<ResourceLocation> NAMES = new LinkedHashSet<>();
    private static boolean loaded;

    private ContentSounds() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (!Config.content.sounds) { return; }
        PackManager.get().forEach(PackManager.SOUNDS, PackManager.JSON, (namespace, path, contents) ->
                NAMES.add(new ResourceLocation(namespace, path)));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        load();
        int count = 0;
        for (ResourceLocation name : NAMES) {
            if (ForgeRegistries.SOUND_EVENTS.containsKey(name)) {
                ContentLog.LOGGER.warn("A sound named {} is already registered, skipping the pack entry", name);
                continue;
            }
            ModContainer previous = Loader.instance().activeModContainer();
            try {
                Loader.instance().setActiveModContainer(ContentOwners.of(name.getNamespace()));
                SoundEvent sound = new SoundEvent(name);
                sound.setRegistryName(name);
                event.getRegistry().register(sound);
                count++;
            }
            finally { Loader.instance().setActiveModContainer(previous); }
        }
        if (count > 0) { Summary.info("sounds", "Registered " + count + " sound event(s) from packs"); }
    }
}

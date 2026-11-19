package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.pack.definitions.items.RecordDef;
import mctmods.resourcedatapackloader.common.sounds.ContentSounds;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.RecordItem;

public final class ContentRecordItem extends RecordItem {
    public ContentRecordItem(RecordDef disc, Properties properties) { super(disc.level(), sound(disc.sound()), properties, disc.length()); }

    private static Supplier<SoundEvent> sound(ResourceLocation name) {
        SoundEvent fallback = SoundEvent.createVariableRangeEvent(name);
        return () -> {
            SoundEvent found = ContentSounds.find(name);
            return found != null ? found : fallback;
        };
    }
}

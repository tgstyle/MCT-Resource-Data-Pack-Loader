package mctmods.resourcedatapackloader.core;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class CofhWorldContainer extends DummyModContainer {
    private static boolean active;

    public CofhWorldContainer() {
        super(metadata());
        active = true;
    }

    private static ModMetadata metadata() {
        ModMetadata metadata = new ModMetadata();
        metadata.modId = "cofhworld";
        metadata.name = "CoFH World (emulated)";
        metadata.version = "1.4.0.1";
        metadata.description = "Stand-in provided by Resource Data Pack Loader so mods that require CoFH World can load without it";
        return metadata;
    }

    @SuppressWarnings("UnstableApiUsage") @Override public boolean registerBus(EventBus bus, LoadController controller) { return true; }

    public static boolean emulated() { return active; }
}

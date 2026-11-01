package mctmods.resourcedatapackloader;

import mctmods.resourcedatapackloader.core.registration.ModSetup;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ResourceDataPackLoader.MOD_ID) public class ResourceDataPackLoader {
    public static final String MOD_ID = "resourcedatapackloader";
    public static final Logger LOGGER = LogManager.getLogger("RDPL");

    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(MOD_ID, path); }

    public ResourceDataPackLoader(IEventBus modBus, ModContainer container) { ModSetup.construct(modBus, container); }
}

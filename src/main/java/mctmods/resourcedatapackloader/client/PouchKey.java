package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = ResourceDataPackLoader.MOD_ID) public final class PouchKey {
    public static final String CATEGORY = "key.categories.resourcedatapackloader";
    private static KeyBinding open;

    private PouchKey() {}

    public static void register() {
        if (open != null) { return; }
        open = new KeyBinding("key.resourcedatapackloader.pouch", Keyboard.KEY_V, CATEGORY);
        ClientRegistry.registerKeyBinding(open);
    }

    public static int code() { return open == null ? Keyboard.KEY_NONE : open.getKeyCode(); }

    @SubscribeEvent public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || open == null) { return; }
        Minecraft game = Minecraft.getMinecraft();
        if (game.player == null || game.currentScreen != null) { return; }
        while (open.isPressed()) { RDPLNetwork.openWornPouch(); }
    }
}

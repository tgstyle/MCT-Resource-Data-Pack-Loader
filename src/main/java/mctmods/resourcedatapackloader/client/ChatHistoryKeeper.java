package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = ResourceDataPackLoader.MOD_ID)
public final class ChatHistoryKeeper {
    private static final int KEPT = 10;
    private static final List<String> typed = new ArrayList<>();
    private static boolean loaded = false;
    private static volatile String pending = null;

    private ChatHistoryKeeper() {}

    @SubscribeEvent public static void onChatOpened(GuiOpenEvent event) {
        if (!(event.getGui() instanceof GuiChat)) { return; }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.ingameGUI == null) { return; }

        if (!loaded) {
            loaded = true;
            typed.addAll(load(mc));
        }
        if (mc.ingameGUI.getChatGUI().getSentMessages().isEmpty() && !typed.isEmpty()) {
            for (String line : typed) { mc.ingameGUI.getChatGUI().addToSentMessages(line); }
            ContentLog.LOGGER.debug("The chat history keeper seeded {} kept line(s) into {} as the chat screen opened", typed.size(), mc.ingameGUI.getChatGUI().getClass().getSimpleName());
        }
    }

    public static void caught(String line) {
        if (line.isEmpty()) { return; }
        if (line.startsWith("/")) {
            pending = line;
            return;
        }
        commit(line);
    }

    public static void commandRan(String command) {
        String held = pending;
        if (held == null) { return; }

        String bare = command.startsWith("/") ? command.substring(1) : command;
        if (!held.substring(1).trim().equals(bare.trim())) { return; }

        pending = null;
        Minecraft.getMinecraft().addScheduledTask(() -> commit(held));
    }

    private static void commit(String line) {
        if (!typed.isEmpty() && typed.get(typed.size() - 1).equals(line)) { return; }

        typed.add(line);
        while (typed.size() > KEPT) { typed.remove(0); }
        save(Minecraft.getMinecraft());
    }

    private static List<String> load(Minecraft mc) {
        File kept = file(mc);
        if (!kept.isFile()) { return new ArrayList<>(); }

        try { return Files.readAllLines(kept.toPath(), StandardCharsets.UTF_8); }
        catch (IOException failed) {
            ContentLog.LOGGER.warn("Could not read the kept chat history from {}: {}", kept, failed.toString());
            return new ArrayList<>();
        }
    }

    private static void save(Minecraft mc) {
        File kept = file(mc);
        try { Files.write(kept.toPath(), typed, StandardCharsets.UTF_8); }
        catch (IOException failed) { ContentLog.LOGGER.warn("Could not keep the chat history in {}: {}", kept, failed.toString()); }
    }

    private static File file(Minecraft mc) { return new File(mc.gameDir, "config/rdpl-chat-history.txt"); }
}

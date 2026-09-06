package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraftforge.client.event.ScreenEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ChatHistoryKeeper {
    private static final int KEPT = 10;
    private static final String FILE = "config/rdpl-chat-history.txt";
    private static final List<String> TYPED = new ArrayList<>();
    private static boolean loaded;

    private ChatHistoryKeeper() {}

    public static void onOpening(ScreenEvent.Opening event) {
        if (!(event.getNewScreen() instanceof ChatScreen)) { return; }
        Minecraft mc = Minecraft.getInstance();
        if (!loaded) {
            loaded = true;
            TYPED.addAll(load(mc));
        }
        ChatComponent chat = mc.gui.getChat();
        if (!chat.getRecentChat().isEmpty() || TYPED.isEmpty()) { return; }
        for (String line : TYPED) { chat.addRecentChat(line); }
        ContentLog.LOGGER.debug("The chat history keeper seeded {} kept line(s) as the chat screen opened", TYPED.size());
    }

    public static void caught(String line) {
        if (line.isEmpty() || (!TYPED.isEmpty() && TYPED.get(TYPED.size() - 1).equals(line))) { return; }
        TYPED.add(line);
        while (TYPED.size() > KEPT) { TYPED.remove(0); }
        save(Minecraft.getInstance());
    }

    private static List<String> load(Minecraft mc) {
        Path kept = file(mc);
        if (!Files.isRegularFile(kept)) { return new ArrayList<>(); }
        try { return Files.readAllLines(kept, StandardCharsets.UTF_8); }
        catch (IOException failed) {
            ContentLog.LOGGER.warn("Could not read the kept chat history from {}: {}", kept, failed.toString());
            return new ArrayList<>();
        }
    }

    private static void save(Minecraft mc) {
        Path kept = file(mc);
        try { Files.write(kept, TYPED, StandardCharsets.UTF_8); }
        catch (IOException failed) { ContentLog.LOGGER.warn("Could not keep the chat history in {}: {}", kept, failed.toString()); }
    }

    private static Path file(Minecraft mc) { return mc.gameDirectory.toPath().resolve(FILE); }
}

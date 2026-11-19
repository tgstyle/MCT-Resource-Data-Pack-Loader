package mctmods.resourcedatapackloader.common.command.custom;

import mctmods.resourcedatapackloader.common.cards.CardWhen;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.CardParser;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.command.ICommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class CommandGuards {
    private static final Set<String> OWN = new HashSet<>(Arrays.asList("rdpl", "rdplserver"));
    private static final String DENIED = "commands.generic.permission";
    private static final List<Guard> GUARDS = new ArrayList<>();

    private CommandGuards() {}

    public static void load() {
        GUARDS.clear();
        Json.eachObject(PackManager.COMMAND_GUARDS, "command guard file", CommandGuards::read);
        if (!GUARDS.isEmpty()) { Summary.info("commandguards", "Loaded " + GUARDS.size() + " command guard(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        String name = Json.getString(json, "guard", "").trim().toLowerCase(Locale.ROOT);
        if (name.isEmpty()) {
            ContentLog.LOGGER.error("Command guard file {} names no command in guard, ignoring it", key);
            return;
        }
        if (OWN.contains(name)) {
            ContentLog.LOGGER.error("Command guard file {} guards /{}, which no guard may block, ignoring it", key, name);
            return;
        }
        CardWhen when = condition(json, "when", key);
        CardWhen unless = condition(json, "unless", key);
        if (when == null && unless == null) { ContentLog.LOGGER.warn("Command guard file {} has neither unless nor when, so it blocks /{} for everyone", key, name); }
        GUARDS.add(new Guard(key.toString(), name, when, unless, Json.getString(json, "message", "").trim()));
    }

    @Nullable private static CardWhen condition(JsonObject json, String member, ResourceLocation key) {
        if (!json.has(member)) { return null; }
        JsonObject object = Json.getJsonObject(json, member, new JsonObject());
        if (object.size() == 0) {
            ContentLog.LOGGER.error("Command guard file {} has an empty '{}', ignoring that key", key, member);
            return null;
        }
        return CardParser.when(object, key, "Command guard file");
    }

    @SubscribeEvent(priority = EventPriority.HIGH) public static void onCommand(CommandEvent event) {
        if (GUARDS.isEmpty()) { return; }
        Entity entity = event.getSender().getCommandSenderEntity();
        if (!(entity instanceof EntityPlayerMP)) { return; }
        ICommand command = event.getCommand();
        List<String> names = namesOf(command);
        if (!Collections.disjoint(names, OWN)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) entity;
        for (Guard guard : GUARDS) {
            if (!names.contains(guard.name) || !guard.blocks(player)) { continue; }
            event.setCanceled(true);
            player.sendMessage(guard.text());
            ContentLog.LOGGER.info("Command guard {} stopped /{} for {}", guard.file, guard.name, player.getName());
            return;
        }
    }

    private static List<String> namesOf(ICommand command) {
        List<String> names = new ArrayList<>();
        names.add(command.getName().toLowerCase(Locale.ROOT));
        for (String alias : orEmpty(command.getAliases())) { names.add(alias.toLowerCase(Locale.ROOT)); }
        return names;
    }

    private static List<String> orEmpty(@Nullable List<String> aliases) { return aliases == null ? Collections.emptyList() : aliases; }

    private static final class Guard {
        private final String file;
        private final String name;
        @Nullable private final CardWhen when;
        @Nullable private final CardWhen unless;
        private final String message;

        private Guard(String file, String name, @Nullable CardWhen when, @Nullable CardWhen unless, String message) {
            this.file = file;
            this.name = name;
            this.when = when;
            this.unless = unless;
            this.message = message;
        }

        private boolean blocks(EntityPlayerMP player) {
            if (when != null && when.fails(player)) { return false; }
            return unless == null || unless.fails(player);
        }

        private ITextComponent text() {
            String said = message.isEmpty() ? DENIED : message;
            boolean key = message.isEmpty() || said.indexOf(' ') < 0 && said.indexOf('.') >= 0;
            ITextComponent text = key ? new TextComponentTranslation(said) : new TextComponentString(said);
            text.getStyle().setColor(TextFormatting.RED);
            return text;
        }
    }
}

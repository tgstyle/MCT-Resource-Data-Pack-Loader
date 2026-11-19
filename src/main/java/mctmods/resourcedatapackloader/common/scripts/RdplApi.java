package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.cards.CardFire;
import mctmods.resourcedatapackloader.common.cards.CardRule;
import mctmods.resourcedatapackloader.common.cards.CardRules;
import mctmods.resourcedatapackloader.common.syncdata.SyncData;
import mctmods.resourcedatapackloader.common.syncdata.SyncDataRules;
import mctmods.resourcedatapackloader.common.syncdata.SyncValue;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.core.rhino.Function;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.registries.ForgeRegistries;

public final class RdplApi {
    private static final String SCRIPT = "A script";
    private static final Pattern PAIR = Pattern.compile("(\\w+)=(.*)");
    public final ServerLevel level;
    @Nullable public final Player player;
    public final RdplWorldApi world;

    public RdplApi(ServerLevel level, @Nullable Player player) {
        this.level = level;
        this.player = player;
        this.world = new RdplWorldApi(level);
    }

    public boolean runCommand(String command) { return Functions.command(level, player, command); }

    public boolean give(String item, int count) { return player != null && runCommand("give " + player.getGameProfile().getName() + " " + item + " " + count); }

    public void actionbar(String text) {
        if (player != null) { player.displayClientMessage(Component.literal(text), true); }
    }

    public void cooldown(String item, int ticks) {
        ResourceLocation id = ResourceLocation.tryParse(item);
        Item found = id == null ? null : ForgeRegistries.ITEMS.getValue(id);
        if (found == null || !ForgeRegistries.ITEMS.containsKey(id)) { throw new IllegalArgumentException("Unknown item: " + item); }
        if (player != null) { player.getCooldowns().addCooldown(found, ticks); }
    }

    public boolean card(String id, String... args) {
        CardRule rule = CardRules.find(id);
        if (rule == null || !(player instanceof ServerPlayer shown)) { return false; }
        Map<String, String> given = new LinkedHashMap<>();
        for (String arg : args) {
            Matcher pair = PAIR.matcher(arg);
            if (pair.matches()) { given.put(pair.group(1), pair.group(2)); }
        }
        CardFire.show(rule, shown, given);
        return true;
    }

    public void schedule(int ticks, Function fn) { ScriptScheduler.schedule(ticks, fn, fn.getParentScope(), player); }

    @Nullable public ScriptData.View data() {
        ScriptData data = ScriptData.get(level);
        return data == null ? null : data.world();
    }

    @Nullable public ScriptData.View data(Player who) {
        ScriptData data = ScriptData.get(level);
        return data == null ? null : data.player(who);
    }

    @Nullable public ScriptData.View teamData(String team) {
        ScriptData data = ScriptData.get(level);
        return data == null ? null : data.team(team);
    }

    public boolean sync(String name, Object value) { return SyncData.set(level, player, name, plain(name, value), SCRIPT); }

    public boolean syncTo(Player who, String name, Object value) { return SyncData.set(level, who, name, plain(name, value), SCRIPT); }

    public boolean syncAll(String name, Object value) { return SyncData.set(level, null, name, plain(name, value), SCRIPT); }

    public boolean unsync(String name) { return SyncData.remove(level, player, name); }

    @Nullable public Object synced(String name) {
        SyncValue found;
        if (player != null) { found = SyncData.read(player, name); }
        else {
            SyncData data = SyncData.peek(level);
            found = data == null ? null : data.worldValues().get(name);
            if (found == null) { found = SyncDataRules.defaultOf(name); }
        }
        return found == null ? null : found.raw();
    }

    private static Object plain(String name, Object value) {
        Object given = value instanceof CharSequence ? value.toString() : value;
        if (SyncValue.of(given) == null) {
            throw new IllegalArgumentException(SCRIPT + " sets the synced value " + name + " to a " + (given == null ? "null" : given.getClass().getSimpleName()) + "; synced data holds text, numbers and true/false only, so nothing is set");
        }
        return given;
    }

    public boolean hasStage(String tag) { return player != null && player.getTags().contains(tag); }

    public void addStage(String tag) {
        if (player != null) { player.addTag(tag); }
    }

    public void removeStage(String tag) {
        if (player != null) { player.removeTag(tag); }
    }

    public int score(String objective) {
        Scoreboard board = level.getScoreboard();
        Objective found = board.getObjective(objective);
        return player == null || found == null ? 0 : board.getOrCreatePlayerScore(player.getScoreboardName(), found).getScore();
    }

    public void setScore(String objective, int value) {
        Objective found = level.getScoreboard().getObjective(objective);
        if (found == null) { throw new IllegalArgumentException("Unknown objective: " + objective); }
        if (player != null) { level.getScoreboard().getOrCreatePlayerScore(player.getScoreboardName(), found).setScore(value); }
    }

    public boolean hasAdvancement(String id) {
        MinecraftServer server = level.getServer();
        ResourceLocation key = ResourceLocation.tryParse(id);
        if (key == null || !(player instanceof ServerPlayer serverPlayer)) { return false; }
        Advancement advancement = server.getAdvancements().getAdvancement(key);
        return advancement != null && serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone();
    }
}

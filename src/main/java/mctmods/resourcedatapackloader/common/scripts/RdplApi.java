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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;

public final class RdplApi {
    private static final String SCRIPT = "A script";
    private static final Pattern PAIR = Pattern.compile("(\\w+)=(.*)");
    public final WorldServer level;
    @Nullable public final EntityPlayer player;
    public final RdplWorldApi world;

    public RdplApi(WorldServer level, @Nullable EntityPlayer player) {
        this.level = level;
        this.player = player;
        this.world = new RdplWorldApi(level);
    }

    public boolean runCommand(String command) { return Functions.command(level, player, command); }

    public boolean give(String item, int count) { return player != null && runCommand("give " + player.getName() + " " + item + " " + count); }

    public void actionbar(String text) {
        if (player != null) { player.sendStatusMessage(new TextComponentString(text), true); }
    }

    public void cooldown(String item, int ticks) {
        Item found = Item.getByNameOrId(item);
        if (found == null) { throw new IllegalArgumentException("Unknown item: " + item); }
        if (player != null) { player.getCooldownTracker().setCooldown(found, ticks); }
    }

    public boolean card(String id, String... args) {
        CardRule rule = CardRules.find(id);
        if (rule == null || !(player instanceof EntityPlayerMP)) { return false; }
        Map<String, String> given = new LinkedHashMap<>();
        for (String arg : args) {
            Matcher pair = PAIR.matcher(arg);
            if (pair.matches()) { given.put(pair.group(1), pair.group(2)); }
        }
        CardFire.show(rule, (EntityPlayerMP) player, given);
        return true;
    }

    public void schedule(int ticks, Function fn) { ScriptScheduler.schedule(ticks, fn, fn.getParentScope(), player); }

    @Nullable public ScriptData.View data() {
        ScriptData data = ScriptData.get(level);
        return data == null ? null : data.world();
    }

    @Nullable public ScriptData.View data(EntityPlayer who) {
        ScriptData data = ScriptData.get(level);
        return data == null ? null : data.player(who);
    }

    @Nullable public ScriptData.View teamData(String team) {
        ScriptData data = ScriptData.get(level);
        return data == null ? null : data.team(team);
    }

    public boolean sync(String name, Object value) { return SyncData.set(level, player, name, plain(name, value), SCRIPT); }

    public boolean syncTo(EntityPlayer who, String name, Object value) { return SyncData.set(level, who, name, plain(name, value), SCRIPT); }

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
        ScoreObjective found = board.getObjective(objective);
        return player == null || found == null ? 0 : board.getOrCreateScore(player.getName(), found).getScorePoints();
    }

    public void setScore(String objective, int value) {
        ScoreObjective found = level.getScoreboard().getObjective(objective);
        if (found == null) { throw new IllegalArgumentException("Unknown objective: " + objective); }
        if (player != null) { level.getScoreboard().getOrCreateScore(player.getName(), found).setScorePoints(value); }
    }

    public boolean hasAdvancement(String id) {
        MinecraftServer server = level.getMinecraftServer();
        if (server == null || !(player instanceof EntityPlayerMP)) { return false; }
        Advancement advancement = server.getAdvancementManager().getAdvancement(new ResourceLocation(id));
        return advancement != null && ((EntityPlayerMP) player).getAdvancements().getProgress(advancement).isDone();
    }
}

package mctmods.resourcedatapackloader.common.cards;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class CardRule {
    static final String COMMAND = "command";
    static final String FIRST_JOIN = "first_join";
    public static final String DIMENSION_ENTER = "dimension_enter";
    public static final String BIOME_ENTER = "biome_enter";
    public static final String STRUCTURE_ENTER = "structure_enter";
    public static final String ADVANCEMENT = "advancement";
    static final String TIME_OF_DAY = "time_of_day";
    static final String DAY = "day";
    public static final String CRAFT = "craft";
    public static final String PICKUP = "pickup";
    public static final String KILL = "kill";
    static final String RESPAWN = "respawn";
    static final String DEATH = "death";
    public static final String Y_LEVEL = "y_level";
    public static final String PLAY_TIME = "play_time";
    public static final String SCORE = "score";
    static final String ROLL = "roll";
    static final String ORDER_DONE = "order_done";
    static final String DOWNED = "downed";
    static final String REVIVED = "revived";
    static final String EXPOSURE_LEVEL = "exposure_level";
    static final String ROUND_START = "round_start";
    static final String RAID_WON = "raid_won";
    static final String RAID_LOST = "raid_lost";
    static final String ENVIRONMENT_CHANGE = "environment_change";
    public static final String PORTWAY_ACTIVATE = "portway_activate";
    public static final String PORTWAY_TRAVEL = "portway_travel";
    public static final String PORTWAY_ARRIVE = "portway_arrive";
    public static final String PORTWAY_REFUSED = "portway_refused";
    public static final String PORTWAY_PAID = "portway_paid";
    public static final String PORTWAY_FORGET = "portway_forget";
    public static final String PORTWAY_BIND = "portway_bind";
    public static final String BUILTIN = "builtin";
    public static final List<String> TRIGGERS = List.of(COMMAND, FIRST_JOIN, DIMENSION_ENTER, BIOME_ENTER, STRUCTURE_ENTER, ADVANCEMENT,
            TIME_OF_DAY, DAY, CRAFT, PICKUP, KILL, RESPAWN, DEATH, Y_LEVEL, PLAY_TIME, SCORE, ROLL, ORDER_DONE, DOWNED, REVIVED, EXPOSURE_LEVEL, ROUND_START,
            ENVIRONMENT_CHANGE, PORTWAY_ACTIVATE, PORTWAY_TRAVEL, PORTWAY_ARRIVE, PORTWAY_REFUSED, PORTWAY_PAID, PORTWAY_FORGET, PORTWAY_BIND, RAID_WON, RAID_LOST);
    static final List<String> SCANNED = List.of(BIOME_ENTER, STRUCTURE_ENTER, Y_LEVEL, PLAY_TIME, SCORE);
    public static final String PLAYER = "player";
    static final String EVERYONE = "everyone";
    static final String DIMENSION = "dimension";
    static final String TEAM = "team";
    public static final List<String> AUDIENCES = List.of(PLAYER, EVERYONE, DIMENSION, TEAM);
    public static final String ALWAYS = "always";
    static final String ONCE_PER_PLAYER = "once_per_player";
    static final String ONCE_PER_WORLD = "once_per_world";
    static final String ONCE_PER_SESSION = "once_per_session";
    public static final List<String> REPEATS = List.of(ALWAYS, ONCE_PER_PLAYER, ONCE_PER_WORLD, ONCE_PER_SESSION);
    public final String key;
    public String trigger = "";
    public String dimension = "";
    public List<String> biomes = Collections.emptyList();
    public List<String> structures = Collections.emptyList();
    public int radius;
    public String advancement = "";
    public String item = "";
    public String entity = "";
    public int count = 1;
    @Nullable public Integer below;
    @Nullable public Integer above;
    public int time;
    public long day = -1L;
    public int minutes;
    public String objective = "";
    public int score;
    public String roll = "";
    public String order = "";
    public String exposure = "";
    public String environment = "";
    public String portway = "";
    public String raid = "";
    public String rank = "";
    public String network = "";
    public String reason = "";
    @Nullable public Integer level;
    @Nullable public Integer atLeast;
    @Nullable public Integer atMost;
    @Nullable public CardWhen when;
    @Nullable public String title;
    @Nullable public List<String> lines;
    @Nullable public String icon;
    @Nullable public String color;
    @Nullable public String image;
    @Nullable public Boolean background;
    @Nullable public String font;
    @Nullable public String style;
    public int ticks;
    public String audience = PLAYER;
    public String repeat = ALWAYS;
    public int cooldown;
    public String runs = "";
    public List<String> requires = Collections.emptyList();

    public CardRule(String key) { this.key = key; }

    public String key() { return key; }
}

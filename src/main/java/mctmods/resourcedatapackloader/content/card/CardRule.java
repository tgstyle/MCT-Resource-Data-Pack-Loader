package mctmods.resourcedatapackloader.content.card;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class CardRule {
    static final String COMMAND = "command";
    static final String FIRST_JOIN = "first_join";
    static final String DIMENSION_ENTER = "dimension_enter";
    static final String BIOME_ENTER = "biome_enter";
    static final String STRUCTURE_ENTER = "structure_enter";
    static final String ADVANCEMENT = "advancement";
    static final String TIME_OF_DAY = "time_of_day";
    static final String DAY = "day";
    static final String CRAFT = "craft";
    static final String PICKUP = "pickup";
    static final String KILL = "kill";
    static final String RESPAWN = "respawn";
    static final String DEATH = "death";
    static final String Y_LEVEL = "y_level";
    static final String PLAY_TIME = "play_time";
    static final String SCORE = "score";
    static final String BUILTIN = "builtin";
    static final List<String> TRIGGERS = Collections.unmodifiableList(Arrays.asList(COMMAND, FIRST_JOIN, DIMENSION_ENTER, BIOME_ENTER, STRUCTURE_ENTER, ADVANCEMENT,
            TIME_OF_DAY, DAY, CRAFT, PICKUP, KILL, RESPAWN, DEATH, Y_LEVEL, PLAY_TIME, SCORE));
    static final List<String> SCANNED = Collections.unmodifiableList(Arrays.asList(BIOME_ENTER, STRUCTURE_ENTER, Y_LEVEL, PLAY_TIME, SCORE));
    static final String PLAYER = "player";
    static final String EVERYONE = "everyone";
    static final String DIMENSION = "dimension";
    static final String TEAM = "team";
    static final List<String> AUDIENCES = Collections.unmodifiableList(Arrays.asList(PLAYER, EVERYONE, DIMENSION, TEAM));
    static final String ALWAYS = "always";
    static final String ONCE_PER_PLAYER = "once_per_player";
    static final String ONCE_PER_WORLD = "once_per_world";
    static final String ONCE_PER_SESSION = "once_per_session";
    static final List<String> REPEATS = Collections.unmodifiableList(Arrays.asList(ALWAYS, ONCE_PER_PLAYER, ONCE_PER_WORLD, ONCE_PER_SESSION));
    final String key;
    String trigger = "";
    String dimension = "";
    List<String> biomes = Collections.emptyList();
    List<String> structures = Collections.emptyList();
    int radius;
    String advancement = "";
    String item = "";
    String entity = "";
    int count = 1;
    @Nullable Integer below;
    @Nullable Integer above;
    int time;
    long day = -1L;
    int minutes;
    String objective = "";
    int score;
    @Nullable CardWhen when;
    @Nullable String title;
    @Nullable List<String> lines;
    @Nullable String icon;
    @Nullable String color;
    @Nullable String image;
    @Nullable Boolean background;
    @Nullable String font;
    @Nullable String style;
    int ticks;
    String audience = PLAYER;
    String repeat = ALWAYS;
    int cooldown;
    String runs = "";
    List<String> requires = Collections.emptyList();

    CardRule(String key) { this.key = key; }

    public String key() { return key; }

    public String trigger() { return trigger; }
}

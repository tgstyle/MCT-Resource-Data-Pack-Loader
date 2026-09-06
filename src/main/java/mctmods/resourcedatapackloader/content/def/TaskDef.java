package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import javax.annotation.Nullable;

public record TaskDef(String name, boolean remove, int priority, @Nullable Double speed, @Nullable Double nearSpeed, @Nullable Float distance, @Nullable Float near, @Nullable Float chance,
                      @Nullable Float leap, @Nullable Integer cooldown, String entity, List<String> items, boolean sight, boolean nearby, boolean help, boolean memory, boolean close, boolean nocturnal, boolean scared) {
    public static TaskDef removal(String name) { return new TaskDef(name, true, 0, null, null, null, null, null, null, null, "", List.of(), true, false, false, false, false, false, false); }

    public double speed(double fallback) { return speed == null ? fallback : speed; }

    public double nearSpeed(double fallback) { return nearSpeed == null ? fallback : nearSpeed; }

    public float distance(float fallback) { return distance == null ? fallback : distance; }

    public float near(float fallback) { return near == null ? fallback : near; }

    public float leap(float fallback) { return leap == null ? fallback : leap; }

    public int cooldown(int fallback) { return cooldown == null ? fallback : cooldown; }
}

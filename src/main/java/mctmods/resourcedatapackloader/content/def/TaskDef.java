package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import javax.annotation.Nullable;

public final class TaskDef {
    public final String name;
    public final boolean remove;
    public final int priority;
    @Nullable public final Double speed;
    @Nullable public final Double nearSpeed;
    @Nullable public final Float distance;
    @Nullable public final Float near;
    @Nullable public final Float chance;
    @Nullable public final Float leap;
    @Nullable public final Integer cooldown;
    public final String entity;
    public final List<String> items;
    public final boolean sight;
    public final boolean nearby;
    public final boolean help;
    public final boolean memory;
    public final boolean close;
    public final boolean nocturnal;
    public final boolean scared;

    public TaskDef(String name, boolean remove, int priority, @Nullable Double speed, @Nullable Double nearSpeed, @Nullable Float distance, @Nullable Float near, @Nullable Float chance, @Nullable Float leap, @Nullable Integer cooldown, String entity, List<String> items, boolean sight, boolean nearby, boolean help, boolean memory, boolean close, boolean nocturnal, boolean scared) {
        this.name = name;
        this.remove = remove;
        this.priority = priority;
        this.speed = speed;
        this.nearSpeed = nearSpeed;
        this.distance = distance;
        this.near = near;
        this.chance = chance;
        this.leap = leap;
        this.cooldown = cooldown;
        this.entity = entity;
        this.items = items;
        this.sight = sight;
        this.nearby = nearby;
        this.help = help;
        this.memory = memory;
        this.close = close;
        this.nocturnal = nocturnal;
        this.scared = scared;
    }

    public double speed(double fallback) { return speed == null ? fallback : speed; }

    public double nearSpeed(double fallback) { return nearSpeed == null ? fallback : nearSpeed; }

    public float distance(float fallback) { return distance == null ? fallback : distance; }

    public float near(float fallback) { return near == null ? fallback : near; }

    public float leap(float fallback) { return leap == null ? fallback : leap; }

    public int cooldown(int fallback) { return cooldown == null ? fallback : cooldown; }
}

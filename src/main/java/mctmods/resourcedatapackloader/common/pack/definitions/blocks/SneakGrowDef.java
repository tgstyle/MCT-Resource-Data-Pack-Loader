package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.common.util.ItemMatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.GameType;

public final class SneakGrowDef {
    public static final int BONEMEAL = 0;
    public static final int TICK = 1;
    public static final int AUTO = 2;
    public final float chance;
    public final int radius;
    public final int height;
    public final int most;
    public final int cooldown;
    public final int mode;
    public final int ticks;
    @Nullable public final List<BlockMatch> blocks;
    public final List<BlockMatch> except;
    public final float hunger;
    public final boolean particles;
    public final boolean sound;
    public final Set<GameType> gameModes;
    public final List<ItemMatch> items;
    public final Map<EquipmentSlot, ItemMatch> equipment;
    @Nullable public final String enchantment;
    public final int damage;
    public final boolean grass;
    public final boolean toggle;

    public static final class Builder {
        public float chance = 0.25F;
        public int radius = 3;
        public int height = 1;
        public int most = 64;
        public int cooldown = 10;
        public int mode = BONEMEAL;
        public int ticks = 1;
        @Nullable public List<BlockMatch> blocks;
        public List<BlockMatch> except = new ArrayList<>();
        public float hunger;
        public boolean particles = true;
        public boolean sound;
        public Set<GameType> gameModes = EnumSet.of(GameType.SURVIVAL, GameType.CREATIVE, GameType.ADVENTURE);
        public List<ItemMatch> items = new ArrayList<>();
        public Map<EquipmentSlot, ItemMatch> equipment = new EnumMap<>(EquipmentSlot.class);
        @Nullable public String enchantment;
        public int damage;
        public boolean grass;
        public boolean toggle;

        public SneakGrowDef build() { return new SneakGrowDef(this); }
    }

    private SneakGrowDef(Builder builder) {
        this.chance = builder.chance;
        this.radius = builder.radius;
        this.height = builder.height;
        this.most = builder.most;
        this.cooldown = builder.cooldown;
        this.mode = builder.mode;
        this.ticks = builder.ticks;
        this.blocks = builder.blocks == null ? null : Collections.unmodifiableList(builder.blocks);
        this.except = Collections.unmodifiableList(builder.except);
        this.hunger = builder.hunger;
        this.particles = builder.particles;
        this.sound = builder.sound;
        this.gameModes = Collections.unmodifiableSet(builder.gameModes);
        this.items = Collections.unmodifiableList(builder.items);
        this.equipment = Collections.unmodifiableMap(builder.equipment);
        this.enchantment = builder.enchantment;
        this.damage = builder.damage;
        this.grass = builder.grass;
        this.toggle = builder.toggle;
    }
}

package mctmods.resourcedatapackloader.common.pack.definitions.villages;

import mctmods.resourcedatapackloader.common.pack.definitions.entities.BossBarDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;
import mctmods.resourcedatapackloader.common.raids.RaidTarget;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfo;

public final class RaidDef {
    public final ResourceLocation registryName;
    public final String omen;
    public final String name;
    public final BossInfo.Color color;
    public final List<List<Group>> waves;
    public final int waveDelay;
    public final int spawnDistance;
    public final int reach;
    public final int timeout;
    public final String sound;
    public final String wins;
    public final String loses;
    public final List<String> bells;
    public final int points;
    public final String loot;
    public final boolean anywhere;
    @Nullable public final BossBarDef bossBar;
    public final String title;
    private final List<RaidTarget.Kind> target;
    @Nullable private final BaseDef base;

    public RaidDef(ResourceLocation registryName, String omen, String name, BossInfo.Color color, List<List<Group>> waves, int waveDelay, int spawnDistance, int reach, int timeout, String sound, String wins, String loses, List<String> bells, int points, String loot, boolean anywhere, @Nullable BossBarDef bossBar, List<RaidTarget.Kind> target, @Nullable BaseDef base) {
        this.registryName = registryName;
        this.omen = omen;
        this.name = name;
        this.color = color;
        this.waves = waves;
        this.waveDelay = waveDelay;
        this.spawnDistance = spawnDistance;
        this.reach = reach;
        this.timeout = timeout;
        this.sound = sound;
        this.wins = wins;
        this.loses = loses;
        this.bells = bells;
        this.points = points;
        this.loot = loot;
        this.anywhere = anywhere;
        this.bossBar = bossBar;
        this.title = bossBar != null && !bossBar.name.isEmpty() ? bossBar.name : name;
        this.target = target;
        this.base = base;
    }

    public List<RaidTarget.Kind> target() { return target; }

    @Nullable public BaseDef base() { return base; }

    public static final class Group {
        public final String entity;
        public final AmountDef count;

        public Group(String entity, AmountDef count) {
            this.entity = entity;
            this.count = count;
        }
    }
}

package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfo;
import java.util.List;

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

    public RaidDef(ResourceLocation registryName, String omen, String name, BossInfo.Color color, List<List<Group>> waves, int waveDelay, int spawnDistance, int reach, int timeout, String sound, String wins, String loses, List<String> bells) {
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
    }

    public static final class Group {
        public final String entity;
        public final AmountDef count;

        public Group(String entity, AmountDef count) {
            this.entity = entity;
            this.count = count;
        }
    }
}

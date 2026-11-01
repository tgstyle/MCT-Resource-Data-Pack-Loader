package mctmods.resourcedatapackloader.common.pack.definitions.villages;

import mctmods.resourcedatapackloader.common.pack.definitions.entities.BossBarDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import net.minecraft.world.BossEvent;

public record RaidDef(Identifier key, String omen, String name, BossEvent.BossBarColor color, List<List<Group>> waves, int waveDelay, int spawnDistance, int reach, int timeout,
        String sound, String wins, String loses, List<String> bells, int points, String loot, boolean anywhere, @Nullable BossBarDef bossBar) {
    public String title() { return bossBar != null && !bossBar.name().isEmpty() ? bossBar.name() : name; }

    public record Group(String entity, AmountDef count) {}
}

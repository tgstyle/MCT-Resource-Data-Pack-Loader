package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import java.util.List;

public record RaidDef(ResourceLocation key, String omen, String name, BossEvent.BossBarColor color, List<List<Group>> waves, int waveDelay, int spawnDistance, int reach, int timeout,
        String sound, String wins, String loses, List<String> bells) {
    public record Group(String entity, AmountDef count) {}
}

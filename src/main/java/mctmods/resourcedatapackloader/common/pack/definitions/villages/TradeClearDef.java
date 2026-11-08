package mctmods.resourcedatapackloader.common.pack.definitions.villages;

import java.util.List;
import net.minecraft.util.ResourceLocation;

public final class TradeClearDef {
    public static final int ALL_LEVELS = 0;
    public final ResourceLocation key;
    public final String profession;
    public final String career;
    public final int level;
    public final List<String> requires;

    public TradeClearDef(ResourceLocation key, String profession, String career, int level, List<String> requires) {
        this.key = key;
        this.profession = profession;
        this.career = career;
        this.level = level;
        this.requires = requires;
    }
}

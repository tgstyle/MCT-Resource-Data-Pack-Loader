package mctmods.resourcedatapackloader.content.def;

import net.minecraft.world.BossInfo;
import javax.annotation.Nullable;

public final class BossBarDef {
    public final String name;
    public final BossInfo.Color color;
    public final BossInfo.Overlay style;
    @Nullable public final BossBackgroundDef background;

    public BossBarDef(String name, BossInfo.Color color, BossInfo.Overlay style, @Nullable BossBackgroundDef background) {
        this.name = name;
        this.color = color;
        this.style = style;
        this.background = background;
    }
}

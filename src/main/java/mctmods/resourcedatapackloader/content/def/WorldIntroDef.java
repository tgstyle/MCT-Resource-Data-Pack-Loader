package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public final class WorldIntroDef {
    public final boolean once;
    @Nullable public final ResourceLocation music;
    public final List<IntroPageDef> pages;
    public final List<String> requires;

    public WorldIntroDef(boolean once, @Nullable ResourceLocation music, List<IntroPageDef> pages, List<String> requires) {
        this.once = once;
        this.music = music;
        this.pages = pages;
        this.requires = requires;
    }
}

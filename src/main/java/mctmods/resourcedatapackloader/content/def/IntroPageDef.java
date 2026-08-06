package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;

import java.util.List;

import javax.annotation.Nullable;

public final class IntroPageDef {
    public static final String SCROLL = "scroll";
    public static final String STATIC = "static";
    public static final String UP = "up";
    public static final String DOWN = "down";
    public static final float DERIVE = 0.0F;
    public final List<ResourceLocation> backgrounds;
    public final float interval;
    @Nullable public final ResourceLocation text;
    public final String mode;
    public final float time;
    public final String direction;
    public final float textScale;
    public final boolean settle;

    public IntroPageDef(List<ResourceLocation> backgrounds, float interval, @Nullable ResourceLocation text, String mode, float time, String direction, float textScale, boolean settle) {
        this.backgrounds = backgrounds;
        this.interval = interval;
        this.text = text;
        this.mode = mode;
        this.time = time;
        this.direction = direction;
        this.textScale = textScale;
        this.settle = settle;
    }

    public boolean still() { return STATIC.equals(mode); }

    public boolean up() { return UP.equals(direction); }

    public boolean cycles() { return backgrounds.size() > 1 && interval > 0.0F; }
}

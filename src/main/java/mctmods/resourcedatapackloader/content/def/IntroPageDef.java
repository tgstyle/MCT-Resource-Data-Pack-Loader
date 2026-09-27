package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;
import javax.annotation.Nullable;

public record IntroPageDef(List<Identifier> backgrounds, float interval, @Nullable Identifier text, String mode, float time, String direction, float textScale, boolean settle) {
    public static final String SCROLL = "scroll";
    public static final String STATIC = "static";
    public static final String UP = "up";
    public static final String DOWN = "down";
    public static final float DERIVE = 0.0F;

    public boolean still() { return STATIC.equals(mode); }

    public boolean up() { return UP.equals(direction); }

    public boolean cycles() { return backgrounds.size() > 1 && interval > 0.0F; }
}

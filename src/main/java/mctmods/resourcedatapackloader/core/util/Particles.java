package mctmods.resourcedatapackloader.core.util;

import javax.annotation.Nullable;
import net.minecraft.util.EnumParticleTypes;

public final class Particles {
    private Particles() {}

    @Nullable public static EnumParticleTypes simple(String name) {
        EnumParticleTypes type = EnumParticleTypes.getByName(name);
        return type != null && type.getArgumentCount() == 0 ? type : null;
    }
}

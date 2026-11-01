package mctmods.resourcedatapackloader.common.blocks;

import net.minecraft.world.entity.EntityType;
import javax.annotation.Nullable;

public interface IContentTnt {
    @Nullable EntityType<?> rdpl$lighter();

    void rdpl$lighter(@Nullable EntityType<?> lighter);
}

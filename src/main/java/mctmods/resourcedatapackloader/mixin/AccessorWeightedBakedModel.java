package mctmods.resourcedatapackloader.mixin;

import net.minecraft.client.renderer.block.model.WeightedBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(WeightedBakedModel.class)
public interface AccessorWeightedBakedModel {
    @Accessor("totalWeight") int rdpl$getTotalWeight();

    @Accessor("models") List<?> rdpl$getModels();
}

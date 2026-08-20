package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.renderer.block.model.WeightedBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(WeightedBakedModel.class) public interface IWeightedBakedModel {
    @Accessor("totalWeight") int rdpl$getTotalWeight();

    @Accessor("models") List<?> rdpl$getModels();
}

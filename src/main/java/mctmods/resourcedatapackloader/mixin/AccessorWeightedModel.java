package mctmods.resourcedatapackloader.mixin;

import net.minecraft.client.renderer.block.model.IBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.renderer.block.model.WeightedBakedModel$WeightedModel")
public interface AccessorWeightedModel {
    @Accessor("model") IBakedModel rdpl$getModel();
}

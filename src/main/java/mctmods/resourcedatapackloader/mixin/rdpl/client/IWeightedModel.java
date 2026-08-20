package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.renderer.block.model.IBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.renderer.block.model.WeightedBakedModel$WeightedModel") public interface IWeightedModel {
    @Accessor("model") IBakedModel rdpl$getModel();
}

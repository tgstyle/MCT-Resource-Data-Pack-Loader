package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.util.random.WeightedEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(WeightedBakedModel.class) public interface IWeightedBakedModel {
    @Accessor("totalWeight") int rdpl$getTotalWeight();

    @Accessor("list") List<WeightedEntry.Wrapper<BakedModel>> rdpl$getList();
}

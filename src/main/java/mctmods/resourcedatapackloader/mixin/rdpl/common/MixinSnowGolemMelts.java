package mctmods.resourcedatapackloader.mixin.rdpl.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@Mixin(SnowGolem.class) public abstract class MixinSnowGolemMelts {
    @Unique private static final List<ResourceKey<Biome>> rdpl$meltingNether = List.of(Biomes.BASALT_DELTAS, Biomes.CRIMSON_FOREST, Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.WARPED_FOREST);

    @ModifyExpressionValue(method = "aiStep()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;getValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;Lnet/minecraft/world/phys/Vec3;)Ljava/lang/Object;"))
    private Object rdpl$meltsByBiome(Object melts) {
        if (Boolean.TRUE.equals(melts)) { return Boolean.TRUE; }
        SnowGolem golem = (SnowGolem) (Object) this;
        for (ResourceKey<Biome> biome : rdpl$meltingNether) {
            if (golem.level().getBiome(golem.blockPosition()).is(biome)) { return Boolean.TRUE; }
        }
        return melts;
    }
}

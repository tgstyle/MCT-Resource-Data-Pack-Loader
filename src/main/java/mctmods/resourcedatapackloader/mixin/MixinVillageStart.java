package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Random;

@Mixin(MapGenVillage.Start.class)
public abstract class MixinVillageStart {
    @Inject(method = "<init>(Lnet/minecraft/world/World;Ljava/util/Random;III)V", at = @At("TAIL"))
    private void rdpl$foundAtBirth(World worldIn, Random rand, int x, int z, int size, CallbackInfo ci) {
        if (ContentBeard.wanted()) { ContentBeard.foundAtBirth((StructureStart) (Object) this); }
    }
}

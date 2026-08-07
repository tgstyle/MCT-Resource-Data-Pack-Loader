package mctmods.resourcedatapackloader.mixin;

import net.minecraft.world.gen.structure.MapGenVillage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MapGenVillage.class)
public interface AccessorMapGenVillage {
    @Accessor("distance") int rdpl$distance();
}

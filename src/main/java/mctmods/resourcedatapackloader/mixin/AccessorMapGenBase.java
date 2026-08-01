package mctmods.resourcedatapackloader.mixin;

import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MapGenBase.class)
public interface AccessorMapGenBase {
    @Accessor("world") World rdpl$getWorld();
}

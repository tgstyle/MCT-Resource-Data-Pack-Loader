package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Random;

@Mixin(MapGenBase.class) public interface IMapGenBase {
    @Accessor("world") World rdpl$getWorld();
    @Accessor("world") void rdpl$setWorld(World world);
    @Accessor("rand") Random rdpl$rand();
    @Accessor("range") void rdpl$setRange(int range);
}

package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NodeProcessor.class) public interface INodeProcessor {
    @Accessor("entity") EntityLiving rdpl$getEntity();

    @Accessor("entitySizeX") int rdpl$getSizeX();

    @Accessor("entitySizeY") int rdpl$getSizeY();

    @Accessor("entitySizeZ") int rdpl$getSizeZ();
}

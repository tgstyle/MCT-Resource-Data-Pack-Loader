package mctmods.resourcedatapackloader.mixin.rdpl.common;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructureTemplatePool.class) public interface IStructureTemplatePool {
    @Accessor("templates") ObjectArrayList<StructurePoolElement> rdpl$templates();
}

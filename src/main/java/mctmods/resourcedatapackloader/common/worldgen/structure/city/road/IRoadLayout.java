package mctmods.resourcedatapackloader.common.worldgen.structure.city.road;

import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public interface IRoadLayout {
    void rdpl$layout(BeardRoads.Grade grade);

    @Nullable BeardRoads.Grade rdpl$layout();

    void rdpl$repave(World world, StructureBoundingBox clip);
}

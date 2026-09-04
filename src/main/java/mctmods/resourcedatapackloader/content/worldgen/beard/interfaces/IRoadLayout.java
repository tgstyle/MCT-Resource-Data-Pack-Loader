package mctmods.resourcedatapackloader.content.worldgen.beard.interfaces;

import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import javax.annotation.Nullable;

public interface IRoadLayout {
    void rdpl$layout(BeardRoads.Grade grade);

    @Nullable BeardRoads.Grade rdpl$layout();

    void rdpl$repave(World world, StructureBoundingBox clip);
}

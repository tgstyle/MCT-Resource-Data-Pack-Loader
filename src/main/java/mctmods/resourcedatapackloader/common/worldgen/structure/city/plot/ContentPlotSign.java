package mctmods.resourcedatapackloader.common.worldgen.structure.city.plot;

import mctmods.resourcedatapackloader.common.entities.orders.ContentOrders;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.VillageDef;
import mctmods.resourcedatapackloader.core.util.LineCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class ContentPlotSign {
    private ContentPlotSign() {}

    public static void stand(WorldGenLevel level, VillageDef def, BoundingBox box, BlockPos origin, StructurePlaceSettings settings) {
        if (def.orderSign().isEmpty()) { return; }
        place(level, def, box, origin.offset(StructureTemplate.calculateRelativePosition(settings, new BlockPos(def.orderX(), def.orderY(), def.orderZ()))), settings.getRotation());
    }

    public static void place(WorldGenLevel level, VillageDef def, BoundingBox box, BlockPos at, Rotation turn) {
        if (!box.isInside(at)) { return; }
        level.setBlock(at, Blocks.OAK_SIGN.defaultBlockState().setValue(StandingSignBlock.ROTATION, 4 * turn.ordinal()), 2);
        if (!(level.getBlockEntity(at) instanceof SignBlockEntity sign)) { return; }
        LineCompat.loadSignWord(sign, level.registryAccess(), def.orderSign());
        sign.getPersistentData().putBoolean(ContentOrders.PLOT_MARK, true);
        ContentOrders.plotSign(level.getLevel(), at);
    }
}

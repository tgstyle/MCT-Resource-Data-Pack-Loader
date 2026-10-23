package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.content.entity.ContentOrders;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
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
        CompoundTag tag = new CompoundTag();
        SignText.DIRECT_CODEC.encodeStart(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), new SignText().setMessage(0, Component.literal(def.orderSign()))).result().ifPresent(text -> tag.put("front_text", text));
        sign.loadWithComponents(tag, level.registryAccess());
        sign.getPersistentData().putBoolean(ContentOrders.PLOT_MARK, true);
        ContentOrders.plotSign(level.getLevel(), at);
    }
}

package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentBanners;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ContentBannerBlockEntity extends BlockEntity {
    public ContentBannerBlockEntity(BlockPos pos, BlockState state) { super(ContentBanners.type(), pos, state); }
}

package mctmods.resourcedatapackloader.common.blocks.banner.logic;

import mctmods.resourcedatapackloader.common.banners.ContentBanners;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class ContentBannerBlockEntity extends BlockEntity {
    public ContentBannerBlockEntity(BlockPos pos, BlockState state) { super(ContentBanners.TYPE.get(), pos, state); }
}

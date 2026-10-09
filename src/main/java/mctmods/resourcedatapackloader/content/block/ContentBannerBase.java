package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.interfaces.IContentBanner;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

abstract class ContentBannerBase extends Block implements EntityBlock, IContentBanner {
    private final Identifier texture;
    private final boolean modeled;

    ContentBannerBase(Identifier id, String statePath, Properties properties) {
        super(properties);
        this.texture = IContentBanner.textureOf(id);
        this.modeled = IContentBanner.shipsBlockstate(id.getNamespace(), statePath);
    }

    @Override public Identifier texture() { return texture; }

    @Override public boolean modeled() { return modeled; }

    @Override @Nonnull public RenderShape getRenderShape(@Nonnull BlockState state) { return modeled ? RenderShape.MODEL : RenderShape.INVISIBLE; }

    @Override public boolean isPossibleToRespawnInThis(@Nonnull BlockState state) { return true; }

    @Override @Nullable public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) { return modeled ? null : new ContentBannerBlockEntity(pos, state); }
}

package mctmods.resourcedatapackloader.common.util.world;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.Template;

public final class TemplateFilters {
    private TemplateFilters() {}

    public static ITemplateProcessor chain(ITemplateProcessor first, @Nullable ITemplateProcessor second) {
        if (second == null) { return first; }
        return (world, at, info) -> {
            Template.BlockInfo made = first.processBlock(world, at, info);
            return made == null ? null : second.processBlock(world, at, made);
        };
    }

    public static ITemplateProcessor broken() {
        return (world, at, info) -> {
            IBlockState there = world.getBlockState(at);
            return there != info.blockState && there.getBlock().isReplaceable(world, at) ? info : null;
        };
    }
}

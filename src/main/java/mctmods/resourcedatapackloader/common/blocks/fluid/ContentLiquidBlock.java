package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidDef;
import mctmods.resourcedatapackloader.common.util.ContentEffects;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public final class ContentLiquidBlock extends LiquidBlock {
    private final FluidDef def;
    private final List<MobEffectInstance> effects = new ArrayList<>();

    public ContentLiquidBlock(FluidDef def, FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
        this.def = def;
        for (String value : def.potions()) {
            MobEffectInstance effect = ContentEffects.parse(def.key(), value);
            if (effect != null) { effects.add(effect); }
        }
    }

    public FluidDef getDef() { return def; }

    @Override protected void entityInside(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Entity entity, @Nonnull InsideBlockEffectApplier applier, boolean precise) {
        FluidEntities.touch(level, pos, state, entity, def);
        if (level.isClientSide()) { return; }
        if (def.lavaMaterial() && FluidEntities.lavaDefault(entity, def.behavior())) { FluidEntities.scorch(entity); }
        if (effects.isEmpty() || !(entity instanceof LivingEntity living)) { return; }
        for (MobEffectInstance effect : effects) { living.addEffect(ContentEffects.copy(effect)); }
    }

}

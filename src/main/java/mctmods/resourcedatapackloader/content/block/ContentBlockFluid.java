package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.FluidDef;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import java.util.List;
import javax.annotation.Nonnull;

public class ContentBlockFluid extends BlockFluidClassic {
    private List<PotionEffect> effects;

    public ContentBlockFluid(Fluid fluid, FluidDef def) {
        super(fluid, def.material);
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        if (def.quantaPerBlock > 0) { setQuantaPerBlock(def.quantaPerBlock); }
    }

    public void setEffects(List<PotionEffect> effects) { this.effects = effects; }

    @Override public void onEntityCollision(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
        if (world.isRemote) { return; }
        if (effects == null || effects.isEmpty()) { return; }
        if (!(entity instanceof EntityLivingBase)) { return; }
        for (PotionEffect effect : effects) {
            if (effect != null) { ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(effect)); }
        }
    }
}

package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidBehaviorDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidDef;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class ContentFluidBlock extends BlockFluidClassic {
    private final FluidBehaviorDef behavior;
    private final int fluidTicks;
    private final int fluidReach;
    private final boolean waterMaterial;
    private final boolean lavaBurnOff;
    private List<PotionEffect> effects;

    public ContentFluidBlock(Fluid fluid, FluidDef def) {
        super(fluid, def.material);
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        behavior = def.behavior;
        if (def.quantaPerBlock > 0) { setQuantaPerBlock(def.quantaPerBlock); }
        fluidReach = behavior.spread > 0 ? behavior.spread : quantaPerBlock - 1;
        if (behavior.maxSpread() > 0) { setQuantaPerBlock(1 + behavior.maxSpread()); }
        if (behavior.flowTicks > 0) { setTickRate(behavior.flowTicks); }
        fluidTicks = tickRate;
        canCreateSources = behavior.infinite;
        waterMaterial = def.material == Material.WATER;
        lavaBurnOff = def.material == Material.LAVA && (behavior.creatures.damage != null || behavior.creatures.fireSeconds != null || Boolean.FALSE.equals(behavior.items.burn) || !behavior.items.immune.isEmpty());
    }

    public void setEffects(List<PotionEffect> effects) { this.effects = effects; }

    public FluidBehaviorDef behavior() { return behavior; }

    public boolean lavaBurnOff() { return lavaBurnOff; }

    private void enter(World world) {
        if (behavior.byDimension.isEmpty()) { return; }
        FluidBehaviorDef.Row row = behavior.rowFor(world.provider.getDimension());
        tickRate = row != null && row.flowTicks > 0 ? row.flowTicks : fluidTicks;
        canCreateSources = row != null && row.infinite != null ? row.infinite : behavior.infinite;
    }

    private int reach(World world) {
        FluidBehaviorDef.Row row = behavior.rowFor(world.provider.getDimension());
        return row != null && row.spread > 0 ? row.spread : fluidReach;
    }

    @Override public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
        if (!world.isRemote) {
            enter(world);
            if (!behavior.reactions.isEmpty() && FluidReactions.check(world, pos, state, false)) { return; }
        }
        super.updateTick(world, pos, state, rand);
    }

    @Override public void randomTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random random) {
        enter(world);
        if (!behavior.reactions.isEmpty() && FluidReactions.check(world, pos, state, true)) { return; }
        super.updateTick(world, pos, state, random);
    }

    @Override public void onBlockAdded(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (!world.isRemote) { enter(world); }
        super.onBlockAdded(world, pos, state);
    }

    @Override public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos from) {
        if (!world.isRemote) { enter(world); }
        super.neighborChanged(state, world, pos, block, from);
    }

    @Override protected void flowIntoBlock(@Nonnull World world, @Nonnull BlockPos pos, int meta) {
        if (!behavior.byDimension.isEmpty() && meta > reach(world)) { return; }
        super.flowIntoBlock(world, pos, meta);
    }

    @Nullable @Override public Boolean isEntityInsideMaterial(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity, double yToTest, @Nonnull Material material, boolean testingHead) {
        Boolean key = testingHead ? behavior.creatures.drown : behavior.creatures.swim;
        if (material != Material.WATER || key == null || !(entity instanceof EntityLivingBase)) { return null; }
        if (!key) { return Boolean.FALSE; }
        return testingHead ? ForgeHooks.isInsideOfMaterial(material, entity, pos) : yToTest >= pos.getY() + 1 - BlockLiquid.getLiquidHeightPercent(state.getValue(LEVEL));
    }

    @Nullable @Override public Boolean isAABBInsideMaterial(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB box, @Nonnull Material material) { return lavaBurnOff && material == Material.LAVA ? Boolean.FALSE : null; }

    @Nonnull @Override public Vec3d modifyAcceleration(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Entity entity, @Nonnull Vec3d vec) {
        if (entity instanceof EntityLivingBase && (!waterMaterial || behavior.creatures.push != null)) { return vec; }
        return super.modifyAcceleration(world, pos, entity, vec);
    }

    @Override public void onEntityCollision(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
        FluidEntities.touch(world, pos, entity, this);
        if (world.isRemote) { return; }
        if (effects == null || effects.isEmpty()) { return; }
        if (!(entity instanceof EntityLivingBase)) { return; }
        for (PotionEffect effect : effects) {
            if (effect != null) { ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(effect)); }
        }
    }
}

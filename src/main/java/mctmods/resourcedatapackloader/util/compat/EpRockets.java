package mctmods.resourcedatapackloader.util.compat;

import com.mjr.extraplanets.api.prefabs.entity.EntityElectricAutoRocket;
import com.mjr.extraplanets.api.prefabs.entity.EntityElectricSpaceshipBase;
import com.mjr.extraplanets.blocks.ExtraPlanets_Blocks;
import com.mjr.extraplanets.tileEntities.blocks.TileEntityRocketChargingPad;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import javax.annotation.Nullable;

public final class EpRockets {
    private static final int CHARGING_PAD = 3;

    private EpRockets() {}

    public static boolean pad(IBlockState state) { return state.getBlock() == ExtraPlanets_Blocks.ADVANCED_LAUCHPAD_FULL && state.getBlock().getMetaFromState(state) == CHARGING_PAD; }

    public static boolean free(@Nullable TileEntity tile) { return tile instanceof TileEntityRocketChargingPad && ((TileEntityRocketChargingPad) tile).getDockedEntity() == null; }

    public static double offset(Entity rocket) { return ((EntityElectricSpaceshipBase) rocket).getOnPadYOffset(); }

    public static int charge(Entity rocket) { return rocket instanceof EntityElectricAutoRocket ? (int) ((EntityElectricAutoRocket) rocket).getCurrentPowerCapacity() : 0; }

    public static void charge(Entity rocket, int amount) {
        if (!(rocket instanceof EntityElectricAutoRocket)) { return; }
        EntityElectricAutoRocket charged = (EntityElectricAutoRocket) rocket;
        charged.setCurrentPowerCapacity(Math.min(amount, charged.getPowerMaxCapacity()));
    }
}

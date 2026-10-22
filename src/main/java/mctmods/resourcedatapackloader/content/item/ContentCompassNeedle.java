package mctmods.resourcedatapackloader.content.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCompassNeedle implements IItemPropertyGetter {
    @SideOnly(Side.CLIENT) private double rotation;
    @SideOnly(Side.CLIENT) private double speed;
    @SideOnly(Side.CLIENT) private long lastUpdateTick;

    @SideOnly(Side.CLIENT) @Override public float apply(@Nonnull ItemStack stack, @Nullable World world, @Nullable EntityLivingBase holder) {
        if (holder == null && !stack.isOnItemFrame()) { return 0.0F; }
        boolean held = holder != null;
        Entity entity = held ? holder : stack.getItemFrame();
        if (entity == null) { return 0.0F; }
        World where = world == null ? entity.world : world;
        NBTTagCompound target = stack.getSubCompound(ContentCompass.TAG);
        double turn;
        if (target != null && target.getInteger(ContentCompass.DIMENSION) == where.provider.getDimension()) {
            double facing = MathHelper.positiveModulo((held ? entity.rotationYaw : 0.0D) / 360.0D, 1.0D);
            double bearing = Math.atan2(target.getInteger(ContentCompass.Z) + 0.5D - entity.posZ, target.getInteger(ContentCompass.X) + 0.5D - entity.posX) / (Math.PI * 2.0D);
            turn = 0.5D - (facing - 0.25D - bearing);
        }
        else { turn = Math.random(); }
        if (held) { turn = wobble(where, turn); }
        return MathHelper.positiveModulo((float) turn, 1.0F);
    }

    @SideOnly(Side.CLIENT) private double wobble(World world, double wanted) {
        if (world.getTotalWorldTime() != lastUpdateTick) {
            lastUpdateTick = world.getTotalWorldTime();
            double gap = MathHelper.positiveModulo(wanted - rotation + 0.5D, 1.0D) - 0.5D;
            speed += gap * 0.1D;
            speed *= 0.8D;
            rotation = MathHelper.positiveModulo(rotation + speed, 1.0D);
        }
        return rotation;
    }
}

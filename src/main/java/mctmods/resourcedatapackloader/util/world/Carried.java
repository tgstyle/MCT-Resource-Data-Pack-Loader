package mctmods.resourcedatapackloader.util.world;

import mctmods.resourcedatapackloader.content.def.CarryDef;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayerMP;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class Carried {
    private static final double RANGE = 10.0D;
    @Nullable private final Entity mount;
    private final List<EntityLiving> leashed;
    private final List<EntityLiving> pets;

    private Carried(@Nullable Entity mount, List<EntityLiving> leashed, List<EntityLiving> pets) {
        this.mount = mount;
        this.leashed = leashed;
        this.pets = pets;
    }

    public static Carried gather(EntityPlayerMP player, CarryDef carry, boolean crossing) {
        List<EntityLiving> leashed = new ArrayList<>();
        List<EntityLiving> pets = new ArrayList<>();
        if (crossing && !carry.crossDimension) { return new Carried(null, leashed, pets); }
        Entity ridden = player.getRidingEntity();
        Entity mount = carry.mount && ridden != null && ridden.getControllingPassenger() == player ? ridden : null;
        if (carry.leashed || carry.pets) {
            for (EntityLiving near : player.world.getEntitiesWithinAABB(EntityLiving.class, player.getEntityBoundingBox().grow(RANGE))) {
                if (near == mount) { continue; }
                if (near.getLeashed()) {
                    if (carry.leashed && near.getLeashHolder() == player) { leashed.add(near); }
                }
                else if (carry.pets && near instanceof EntityTameable && ((EntityTameable) near).isTamed() && ((EntityTameable) near).isOwner(player) && !((EntityTameable) near).isSitting()) { pets.add(near); }
            }
        }
        return new Carried(mount, leashed, pets);
    }

    public void bring(EntityPlayerMP player, double x, double y, double z, float yaw) {
        int dimension = player.dimension;
        if (mount != null) {
            Entity moved = Travel.move(mount, dimension, x, y, z, yaw, 0.0F);
            if (moved != null) { player.startRiding(moved, true); }
        }
        for (EntityLiving follower : leashed) {
            Entity moved = Travel.move(follower, dimension, x, y, z, follower.rotationYaw, follower.rotationPitch);
            if (moved instanceof EntityLiving) { ((EntityLiving) moved).setLeashHolder(player, true); }
        }
        for (EntityLiving pet : pets) { Travel.move(pet, dimension, x, y, z, pet.rotationYaw, pet.rotationPitch); }
    }
}

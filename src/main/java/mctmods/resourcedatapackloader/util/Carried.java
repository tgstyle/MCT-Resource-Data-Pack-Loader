package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.def.CarryDef;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class Carried {
    private static final double RANGE = 10.0D;
    @Nullable private final Entity mount;
    private final List<Mob> leashed;
    private final List<Mob> pets;

    private Carried(@Nullable Entity mount, List<Mob> leashed, List<Mob> pets) {
        this.mount = mount;
        this.leashed = leashed;
        this.pets = pets;
    }

    public static Carried gather(ServerPlayer player, CarryDef carry, boolean crossing) {
        List<Mob> leashed = new ArrayList<>();
        List<Mob> pets = new ArrayList<>();
        if (crossing && !carry.crossDimension()) { return new Carried(null, leashed, pets); }
        Entity ridden = player.getVehicle();
        Entity mount = carry.mount() && ridden != null && ridden.getControllingPassenger() == player ? ridden : null;
        if (carry.leashed() || carry.pets()) {
            for (Mob near : player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(RANGE))) {
                if (near == mount) { continue; }
                if (near.isLeashed()) {
                    if (carry.leashed() && near.getLeashHolder() == player) { leashed.add(near); }
                }
                else if (carry.pets() && near instanceof TamableAnimal pet && pet.isTame() && pet.isOwnedBy(player) && !pet.isOrderedToSit()) { pets.add(near); }
            }
        }
        return new Carried(mount, leashed, pets);
    }

    public void bring(ServerPlayer player, ServerLevel level, double x, double y, double z, float yaw) {
        if (mount != null) {
            Entity moved = Travel.move(mount, level, x, y, z, yaw, 0.0F);
            if (moved != null) { player.startRiding(moved, true, false); }
        }
        for (Mob follower : leashed) {
            Entity moved = Travel.move(follower, level, x, y, z, follower.getYRot(), follower.getXRot());
            if (moved instanceof Mob mob) { mob.setLeashedTo(player, true); }
        }
        for (Mob pet : pets) { Travel.move(pet, level, x, y, z, pet.getYRot(), pet.getXRot()); }
    }
}

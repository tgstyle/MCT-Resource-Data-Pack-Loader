package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.WeaponDef;
import mctmods.resourcedatapackloader.content.item.ContentWeapons;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EntitySelectors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nullable;
import java.util.List;

@SideOnly(Side.CLIENT) public final class ContentReach {
    private static final double VANILLA_REACH = 3.0D;
    private static final double CREATIVE_SLACK = 3.0D;

    private ContentReach() {}

    public static void pick(float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity viewer = mc.getRenderViewEntity();
        if (viewer == null || mc.world == null || mc.player == null) { return; }
        WeaponDef weapon = ContentWeapons.weapon(mc.player.getHeldItemMainhand());
        if (weapon == null || !weapon.hasReach()) { return; }
        boolean creative = mc.playerController.extendedReach();
        double vanilla = creative ? VANILLA_REACH + CREATIVE_SLACK : VANILLA_REACH;
        double reach = creative ? weapon.attackReach + CREATIVE_SLACK : weapon.attackReach;
        if (reach == vanilla) { return; }
        double block = mc.playerController.getBlockReachDistance();
        Vec3d eye = viewer.getPositionEyes(partialTicks);
        RayTraceResult blockHit = viewer.rayTrace(block, partialTicks);
        RayTraceResult occluder = reach > block ? viewer.rayTrace(reach, partialTicks) : blockHit;
        double limit = occluder == null || occluder.typeOfHit != RayTraceResult.Type.BLOCK ? reach : occluder.hitVec.distanceTo(eye);
        Vec3d look = viewer.getLook(1.0F);
        Vec3d end = eye.add(look.x * reach, look.y * reach, look.z * reach);
        List<Entity> list = mc.world.getEntitiesInAABBexcluding(viewer, viewer.getEntityBoundingBox().expand(look.x * reach, look.y * reach, look.z * reach).grow(1.0D), other -> other != null && other.canBeCollidedWith() && EntitySelectors.NOT_SPECTATING.apply(other));
        Entity picked = null;
        Vec3d hit = null;
        double nearest = limit;
        for (Entity other : list) {
            AxisAlignedBB box = other.getEntityBoundingBox().grow(other.getCollisionBorderSize());
            RayTraceResult intercept = box.calculateIntercept(eye, end);
            if (box.contains(eye)) {
                if (nearest >= 0.0D) {
                    picked = other;
                    hit = intercept == null ? eye : intercept.hitVec;
                    nearest = 0.0D;
                }
            }
            else if (intercept != null) {
                double distance = eye.distanceTo(intercept.hitVec);
                if (distance < nearest || nearest == 0.0D) {
                    if (other.getLowestRidingEntity() != viewer.getLowestRidingEntity() || other.canRiderInteract()) {
                        picked = other;
                        hit = intercept.hitVec;
                        nearest = distance;
                    }
                    else if (nearest == 0.0D) {
                        picked = other;
                        hit = intercept.hitVec;
                    }
                }
            }
        }
        point(mc, blockHit, picked, hit, nearest < limit);
    }

    private static void point(Minecraft mc, @Nullable RayTraceResult blockHit, @Nullable Entity picked, @Nullable Vec3d hit, boolean nearer) {
        mc.pointedEntity = null;
        if (picked == null || hit == null || !nearer) {
            mc.objectMouseOver = blockHit;
            return;
        }
        mc.objectMouseOver = new RayTraceResult(picked, hit);
        if (picked instanceof EntityLivingBase || picked instanceof EntityItemFrame) { mc.pointedEntity = picked; }
    }
}

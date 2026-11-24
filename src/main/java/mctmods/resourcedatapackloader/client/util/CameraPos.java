package mctmods.resourcedatapackloader.client.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class CameraPos {
    private CameraPos() {}

    public static Vec3d viewAt(Entity view, float partialTicks) {
        return new Vec3d(
            view.lastTickPosX + (view.posX - view.lastTickPosX) * partialTicks,
            view.lastTickPosY + (view.posY - view.lastTickPosY) * partialTicks,
            view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * partialTicks);
    }
}

package mctmods.resourcedatapackloader.client.renderer;

import mctmods.resourcedatapackloader.core.network.FireworkBurstMessage;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class FireworkBurstParticle extends Particle {
    private static final float SIZE = 2.0F;
    private static final int LIFETIME = 30;
    private static final float GRAVITY = 0.05F;
    private static final float FRICTION = 0.92F;

    public FireworkBurstParticle(World world, double x, double y, double z, double vx, double vy, double vz, Item item) {
        super(world, x, y, z, 0, 0, 0);
        setParticleTexture(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item, 0));
        particleGravity = GRAVITY;
        particleScale = particleScale / 2.0F * SIZE;
        particleMaxAge = LIFETIME;
        motionX = motionX * 0.1F + vx;
        motionY = motionY * 0.1F + vy;
        motionZ = motionZ * 0.1F + vz;
    }

    @Override public int getFXLayer() { return 1; }

    @Override public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge) { setExpired(); }
        motionY -= 0.04 * particleGravity;
        move(motionX, motionY, motionZ);
        motionX *= FRICTION;
        motionY *= FRICTION;
        motionZ *= FRICTION;
        if (onGround) {
            motionX *= 0.7F;
            motionZ *= 0.7F;
        }
    }

    @Override public void renderParticle(@Nonnull BufferBuilder buffer, @Nonnull Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float u0 = particleTexture.getMinU();
        float u1 = particleTexture.getMaxU();
        float v0 = particleTexture.getMinV();
        float v1 = particleTexture.getMaxV();
        float size = 0.1F * particleScale;
        float px = (float) (MathUtil.lerp(partialTicks, prevPosX, posX) - interpPosX);
        float py = (float) (MathUtil.lerp(partialTicks, prevPosY, posY) - interpPosY);
        float pz = (float) (MathUtil.lerp(partialTicks, prevPosZ, posZ) - interpPosZ);
        int light = getBrightnessForRender(partialTicks);
        int lightU = light >> 16 & 65535;
        int lightV = light & 65535;
        buffer.pos(px - rotationX * size - rotationXY * size, py - rotationZ * size, pz - rotationYZ * size - rotationXZ * size).tex(u0, v1).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(lightU, lightV).endVertex();
        buffer.pos(px - rotationX * size + rotationXY * size, py + rotationZ * size, pz - rotationYZ * size + rotationXZ * size).tex(u0, v0).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(lightU, lightV).endVertex();
        buffer.pos(px + rotationX * size + rotationXY * size, py + rotationZ * size, pz + rotationYZ * size + rotationXZ * size).tex(u1, v0).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(lightU, lightV).endVertex();
        buffer.pos(px + rotationX * size - rotationXY * size, py - rotationZ * size, pz + rotationYZ * size - rotationXZ * size).tex(u1, v1).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(lightU, lightV).endVertex();
    }

    public static class Handler extends AbstractClientMessageHandler<FireworkBurstMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, FireworkBurstMessage message, MessageContext ctx) {
            Item item = Item.getItemById(message.itemId);
            float[] v = message.velocities;
            for (int i = 0; i + 2 < v.length; i += 3) {
                Minecraft.getMinecraft().effectRenderer.addEffect(new FireworkBurstParticle(world, message.x, message.y, message.z, v[i], v[i + 1], v[i + 2], item));
            }
        }
    }
}

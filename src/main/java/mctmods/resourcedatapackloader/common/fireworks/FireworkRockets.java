package mctmods.resourcedatapackloader.common.fireworks;

import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class FireworkRockets {
    public static final String TAG_SOURCE = "RdplFirework";

    private FireworkRockets() {}

    public static ItemStack stack(Item source, FireworkDef def) {
        ItemStack stack = new ItemStack(Items.FIREWORKS);
        NBTTagCompound fireworks = new NBTTagCompound();
        NBTTagList explosions = new NBTTagList();
        for (FireworkDef.Explosion explosion : def.explosions) {
            explosions.appendTag(explosionTag(explosion));
        }
        if (explosions.tagCount() > 0) {
            fireworks.setTag("Explosions", explosions);
        }
        fireworks.setByte("Flight", (byte) def.flight);
        NBTTagCompound root = new NBTTagCompound();
        root.setTag("Fireworks", fireworks);
        if (source.getRegistryName() != null) {
            root.setString(TAG_SOURCE, source.getRegistryName().toString());
        }
        stack.setTagCompound(root);
        return stack;
    }

    public static EntityFireworkRocket spawn(World world, double x, double y, double z, ItemStack stack, boolean now) {
        EntityFireworkRocket rocket = new EntityFireworkRocket(world, x, y, z, stack);
        if (now) {
            rocket.lifetime = 0;
        }
        world.spawnEntity(rocket);
        return rocket;
    }

    public static void dispense(World world, BlockPos dispenserPos, EnumFacing facing, ItemStack rocketStack, boolean now) {
        double x = dispenserPos.getX() + 0.5D + facing.getXOffset();
        double y = dispenserPos.getY() + 0.2F;
        double z = dispenserPos.getZ() + 0.5D + facing.getZOffset();
        spawn(world, x, y, z, rocketStack, now);
        world.playEvent(1004, dispenserPos, 0);
    }

    private static NBTTagCompound explosionTag(FireworkDef.Explosion explosion) {
        NBTTagCompound tag = new NBTTagCompound();
        if (explosion.twinkle) {
            tag.setBoolean("Flicker", true);
        }
        if (explosion.trail) {
            tag.setBoolean("Trail", true);
        }
        tag.setIntArray("Colors", explosion.colors);
        if (explosion.fade.length > 0) {
            tag.setIntArray("FadeColors", explosion.fade);
        }
        tag.setByte("Type", (byte) shapeType(explosion.shape));
        return tag;
    }

    private static int shapeType(String shape) {
        switch (shape) {
            case FireworkDef.SHAPE_LARGE_BALL:
                return 1;
            case FireworkDef.SHAPE_STAR:
                return 2;
            case FireworkDef.SHAPE_CREEPER:
                return 3;
            case FireworkDef.SHAPE_BURST:
                return 4;
            default:
                return 0;
        }
    }
}

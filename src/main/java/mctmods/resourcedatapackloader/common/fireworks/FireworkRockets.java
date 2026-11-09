package mctmods.resourcedatapackloader.common.fireworks;

import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public final class FireworkRockets {
    public static final String TAG_SOURCE = "RdplFirework";

    private FireworkRockets() {}

    public static ItemStack stack(Item source, FireworkDef def) {
        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag fireworks = new CompoundTag();
        ListTag explosions = new ListTag();
        for (FireworkDef.Explosion explosion : def.explosions()) {
            explosions.add(explosionTag(explosion));
        }
        if (!explosions.isEmpty()) {
            fireworks.put("Explosions", explosions);
        }
        fireworks.putByte("Flight", (byte) def.flight());
        CompoundTag root = new CompoundTag();
        root.put("Fireworks", fireworks);
        root.putString(TAG_SOURCE, String.valueOf(ForgeRegistries.ITEMS.getKey(source)));
        stack.setTag(root);
        return stack;
    }

    public static FireworkRocketEntity spawn(Level level, double x, double y, double z, ItemStack stack, boolean now) {
        FireworkRocketEntity rocket = new FireworkRocketEntity(level, x, y, z, stack);
        return add(level, rocket, now);
    }

    public static void dispense(Level level, BlockPos dispenserPos, Direction facing, ItemStack rocketStack, boolean now) {
        double cx = dispenserPos.getX() + 0.5D;
        double cy = dispenserPos.getY() + 0.5D;
        double cz = dispenserPos.getZ() + 0.5D;
        FireworkRocketEntity rocket = new FireworkRocketEntity(level, rocketStack, cx, cy, cz, true);
        rocket.setPos(
            cx + facing.getStepX() * (0.5000099999997474D - rocket.getBbWidth() / 2.0D),
            cy + facing.getStepY() * (0.5000099999997474D - rocket.getBbHeight() / 2.0D) - rocket.getBbHeight() / 2.0D,
            cz + facing.getStepZ() * (0.5000099999997474D - rocket.getBbWidth() / 2.0D)
        );
        rocket.shoot(facing.getStepX(), facing.getStepY(), facing.getStepZ(), 0.5F, 1.0F);
        add(level, rocket, now);
        level.levelEvent(1004, dispenserPos, 0);
    }

    private static FireworkRocketEntity add(Level level, FireworkRocketEntity rocket, boolean now) {
        if (now) {
            rocket.lifetime = 0;
        }
        level.addFreshEntity(rocket);
        return rocket;
    }

    private static CompoundTag explosionTag(FireworkDef.Explosion explosion) {
        CompoundTag tag = new CompoundTag();
        if (explosion.twinkle()) {
            tag.putBoolean("Flicker", true);
        }
        if (explosion.trail()) {
            tag.putBoolean("Trail", true);
        }
        tag.putIntArray("Colors", explosion.colors());
        if (explosion.fade().length > 0) {
            tag.putIntArray("FadeColors", explosion.fade());
        }
        tag.putByte("Type", (byte) shapeType(explosion.shape()));
        return tag;
    }

    private static int shapeType(String shape) {
        return switch (shape) {
            case FireworkDef.SHAPE_LARGE_BALL -> 1;
            case FireworkDef.SHAPE_STAR -> 2;
            case FireworkDef.SHAPE_CREEPER -> 3;
            case FireworkDef.SHAPE_BURST -> 4;
            default -> 0;
        };
    }
}

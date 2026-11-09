package mctmods.resourcedatapackloader.common.fireworks;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class FireworkRockets {
    public static final String TAG_SOURCE = "rdpl_firework";
    private static final float DISPENSE_POWER = 0.5F;
    private static final float DISPENSE_UNCERTAINTY = 1.0F;
    private static final double EDGE = 0.5000099999997474D;

    private FireworkRockets() {}

    public static ItemStack stack(Item source, FireworkDef def) {
        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
        List<FireworkExplosion> explosions = new ArrayList<>();
        for (FireworkDef.Explosion explosion : def.explosions()) {
            explosions.add(explosionOf(explosion));
        }
        stack.set(DataComponents.FIREWORKS, new Fireworks(def.flight(), explosions));
        CompoundTag root = new CompoundTag();
        root.putString(TAG_SOURCE, BuiltInRegistries.ITEM.getKey(source).toString());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        return stack;
    }

    public static FireworkRocketEntity spawn(Level level, double x, double y, double z, ItemStack stack, boolean now) {
        FireworkRocketEntity rocket = new FireworkRocketEntity(level, x, y, z, stack);
        if (now) {
            rocket.lifetime = 0;
        }
        level.addFreshEntity(rocket);
        return rocket;
    }

    public static void dispense(Level level, BlockPos dispenserPos, Direction facing, ItemStack rocketStack, boolean now) {
        Vec3 center = Vec3.atCenterOf(dispenserPos);
        double width = EntityType.FIREWORK_ROCKET.getWidth();
        double height = EntityType.FIREWORK_ROCKET.getHeight();
        double x = center.x + facing.getStepX() * (EDGE - width / 2.0D);
        double y = center.y + facing.getStepY() * (EDGE - height / 2.0D) - height / 2.0D;
        double z = center.z + facing.getStepZ() * (EDGE - width / 2.0D);
        FireworkRocketEntity rocket = new FireworkRocketEntity(level, rocketStack.copyWithCount(1), x, y, z, true);
        rocket.shoot(facing.getStepX(), facing.getStepY(), facing.getStepZ(), DISPENSE_POWER, DISPENSE_UNCERTAINTY);
        if (now) {
            rocket.lifetime = 0;
        }
        level.addFreshEntity(rocket);
        level.levelEvent(1004, dispenserPos, 0);
    }

    private static FireworkExplosion explosionOf(FireworkDef.Explosion explosion) {
        return new FireworkExplosion(FireworkExplosion.Shape.byId(shapeType(explosion.shape())), new IntArrayList(explosion.colors()), new IntArrayList(explosion.fade()), explosion.trail(), explosion.twinkle());
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

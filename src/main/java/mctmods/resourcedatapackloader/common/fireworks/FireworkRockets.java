package mctmods.resourcedatapackloader.common.fireworks;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.List;
import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class FireworkRockets {
    public static final String TAG_SOURCE = "rdpl_firework";
    private static final double DISPENSE_OFFSET = 0.5000099999997474D;
    private static final int DISPENSE_EVENT = 1004;
    private static final float DISPENSE_POWER = 0.5F;
    private static final float DISPENSE_UNCERTAINTY = 1.0F;

    private FireworkRockets() {}

    public static ItemStack stack(Item source, FireworkDef def) {
        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
        List<FireworkExplosion> explosions = new ArrayList<>();
        for (FireworkDef.Explosion explosion : def.explosions()) {
            explosions.add(explosion(explosion));
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

    private static FireworkRocketEntity shotRocket(Level level, double x, double y, double z, ItemStack stack, boolean now) {
        FireworkRocketEntity rocket = new FireworkRocketEntity(level, stack.copyWithCount(1), x, y, z, true);
        if (now) {
            rocket.lifetime = 0;
        }
        return rocket;
    }

    public static void dispense(Level level, BlockPos dispenserPos, Direction facing, ItemStack rocketStack, boolean now) {
        Vec3 center = Vec3.atCenterOf(dispenserPos);
        FireworkRocketEntity rocket = shotRocket(level, center.x + facing.getStepX() * DISPENSE_OFFSET, center.y + facing.getStepY() * DISPENSE_OFFSET, center.z + facing.getStepZ() * DISPENSE_OFFSET, rocketStack, now);
        rocket.shoot(facing.getStepX(), facing.getStepY(), facing.getStepZ(), DISPENSE_POWER, DISPENSE_UNCERTAINTY);
        level.addFreshEntity(rocket);
        level.levelEvent(DISPENSE_EVENT, dispenserPos, 0);
    }

    private static FireworkExplosion explosion(FireworkDef.Explosion explosion) {
        return new FireworkExplosion(shape(explosion.shape()), new IntArrayList(explosion.colors()), new IntArrayList(explosion.fade()), explosion.trail(), explosion.twinkle());
    }

    private static FireworkExplosion.Shape shape(String shape) {
        return switch (shape) {
            case FireworkDef.SHAPE_LARGE_BALL -> FireworkExplosion.Shape.LARGE_BALL;
            case FireworkDef.SHAPE_STAR -> FireworkExplosion.Shape.STAR;
            case FireworkDef.SHAPE_CREEPER -> FireworkExplosion.Shape.CREEPER;
            case FireworkDef.SHAPE_BURST -> FireworkExplosion.Shape.BURST;
            default -> FireworkExplosion.Shape.SMALL_BALL;
        };
    }
}

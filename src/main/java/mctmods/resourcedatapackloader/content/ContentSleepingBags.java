package mctmods.resourcedatapackloader.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentSleepingBags {
    private static final Map<UUID, BlockPos> SLEEPERS = new HashMap<>();

    private ContentSleepingBags() {}

    public static InteractionResult use(ServerPlayer player, BlockPos clicked, Direction face) {
        ServerLevel level = player.serverLevel();
        BlockPos target = clicked.above();
        if (face != Direction.UP || !level.getBlockState(target).canBeReplaced() || !level.getBlockState(clicked).isFaceSturdy(level, clicked, Direction.UP)) { return InteractionResult.PASS; }
        Player.BedSleepingProblem problem = problem(player, level, target);
        if (problem != null) {
            Component message = problem.getMessage();
            if (message != null) { player.displayClientMessage(message, true); }
            return InteractionResult.SUCCESS;
        }
        SLEEPERS.put(player.getUUID(), target);
        player.startSleeping(target);
        player.setPos(target.getX() + 0.5D, target.getY() + 0.05D, target.getZ() + 0.5D);
        level.updateSleepingPlayerList();
        return InteractionResult.SUCCESS;
    }

    @Nullable private static Player.BedSleepingProblem problem(ServerPlayer player, ServerLevel level, BlockPos target) {
        if (player.isSleeping() || !player.isAlive()) { return Player.BedSleepingProblem.OTHER_PROBLEM; }
        if (!level.dimensionType().natural()) { return Player.BedSleepingProblem.NOT_POSSIBLE_HERE; }
        if (level.isDay()) { return Player.BedSleepingProblem.NOT_POSSIBLE_NOW; }
        if (!player.isCreative()) {
            Vec3 center = Vec3.atBottomCenterOf(target);
            List<Monster> monsters = level.getEntitiesOfClass(Monster.class, new AABB(center.x() - 8.0D, center.y() - 5.0D, center.z() - 8.0D, center.x() + 8.0D, center.y() + 5.0D, center.z() + 8.0D), monster -> monster.isPreventingPlayerRest(player));
            if (!monsters.isEmpty()) { return Player.BedSleepingProblem.NOT_SAFE; }
        }
        return null;
    }

    public static void onLocationCheck(SleepingLocationCheckEvent event) {
        BlockPos at = SLEEPERS.get(event.getEntity().getUUID());
        if (at != null && at.equals(event.getSleepingLocation())) { event.setResult(Event.Result.ALLOW); }
    }

    public static void onWakeUp(PlayerWakeUpEvent event) { SLEEPERS.remove(event.getEntity().getUUID()); }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SLEEPERS.remove(event.getEntity().getUUID()); }
}

package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.compat.Compat;
import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.content.board.BoardGame;
import mctmods.resourcedatapackloader.content.board.BoardPiece;
import mctmods.resourcedatapackloader.content.board.BoardRules;
import mctmods.resourcedatapackloader.content.board.BoardState;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

final class ContentBoardPieces {
    static final String TAG = "rdpl_piece";
    private static final String BOARD = "rdplBoard";
    private static final String KIND = "rdplKind";
    private static final String SPAWN = "rdplSpawn";
    private static final int STRIKE_TICKS = 10;
    private static final Set<String> UNMADE = new HashSet<>();

    private ContentBoardPieces() {}

    static void squares(ServerLevel level, BoardGame game, BoardRules rules) {
        BlockState light = rules.light.isEmpty() ? null : ContentStates.parse(rules.light, "game " + rules.id);
        BlockState dark = rules.dark.isEmpty() ? null : ContentStates.parse(rules.dark, "game " + rules.id);
        for (int file = 0; file < rules.files; file++) {
            for (int rank = 0; rank < rules.ranks; rank++) {
                BlockState state = (file + rank) % 2 == 0 ? dark : light;
                if (state != null) { level.setBlock(new BlockPos(game.x + file, game.y - 1, game.z + rank), state, 2); }
            }
        }
    }

    static boolean stale(Entity entity, @Nullable BoardGame game) { return game == null || entity.getPersistentData().getLongOr(SPAWN, 0L) != game.spawn; }

    @Nullable static String boardOf(Entity entity) { return entity.entityTags().contains(TAG) ? entity.getPersistentData().getStringOr(BOARD, "") : null; }

    static int sideOf(Entity entity) { return key(entity) & 1; }

    static int square(BoardGame game, BoardRules rules, BlockPos pos) {
        int file = pos.getX() - game.x;
        int rank = pos.getZ() - game.z;
        boolean level = pos.getY() == game.y || pos.getY() == game.y - 1;
        return level && file >= 0 && rank >= 0 && file < rules.files && rank < rules.ranks ? rank * rules.files + file : -1;
    }

    static void clear(ServerLevel level, BoardGame game) {
        for (Entity piece : held(level, game)) { piece.discard(); }
    }

    static void strike(ServerLevel level, BoardGame game, BoardRules rules, int from, int victim) {
        Entity striker = standing(level, game, rules, from);
        Entity struck = standing(level, game, rules, victim);
        if (struck == null) {
            sync(level, game, rules);
            return;
        }
        if (striker instanceof LivingEntity living) { LineCompat.swing(living); }
        if (struck instanceof LivingEntity) { level.broadcastDamageEvent(struck, level.damageSources().generic()); }
        level.sendParticles(ParticleTypes.CRIT, struck.getX(), struck.getY() + struck.getBbHeight() * 0.5D, struck.getZ(), 12, 0.3D, 0.3D, 0.3D, 0.2D);
        level.playSound(null, struck.getX(), struck.getY(), struck.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, struck.getSoundSource(), 1.0F, 1.0F);
        game.strike = STRIKE_TICKS;
    }

    @Nullable private static Entity standing(ServerLevel level, BoardGame game, BoardRules rules, int square) {
        for (Entity piece : held(level, game)) {
            if (piece.distanceToSqr(game.x + square % rules.files + 0.5D, game.y, game.z + Math.floorDiv(square, rules.files) + 0.5D) < 0.01D) { return piece; }
        }
        return null;
    }

    static void sync(ServerLevel level, BoardGame game, BoardRules rules) {
        BoardState state = game.state;
        game.strike = 0;
        if (state == null) { return; }
        if (!load(level, game, rules)) {
            clear(level, game);
            game.spawn = level.getRandom().nextLong();
        }
        List<double[]> wanted = new ArrayList<>();
        for (int square = 0; square < state.size(); square++) {
            int cell = state.at(square);
            if (cell != 0) { wanted.add(new double[] {square % rules.files, Math.floorDiv(square, rules.files),BoardState.kind(cell) * 2 + BoardState.side(cell)}); }
        }
        for (int side = 0; side < 2; side++) {
            List<Integer> taken = state.taken.get(side);
            for (int at = 0; at < taken.size(); at++) {
                int column = at / rules.ranks;
                wanted.add(new double[] {side == 0 ? -2 - column : rules.files + 1 + column, at % rules.ranks, taken.get(at) * 2 + 1 - side});
            }
        }
        List<Entity> pool = held(level, game);
        List<double[]> open = new ArrayList<>();
        for (double[] spot : wanted) {
            Entity there = null;
            for (Entity piece : pool) {
                if (key(piece) == (int) spot[2] && piece.distanceToSqr(game.x + spot[0] + 0.5D, game.y, game.z + spot[1] + 0.5D) < 0.01D) { there = piece; }
            }
            if (there == null) { open.add(spot); }
            else { pool.remove(there); }
        }
        for (double[] spot : open) {
            Entity moved = null;
            for (Entity piece : pool) {
                if (key(piece) == (int) spot[2]) { moved = piece; }
            }
            if (moved == null) { spawn(level, game, rules, spot); }
            else {
                pool.remove(moved);
                moved.teleportTo(game.x + spot[0] + 0.5D, game.y, game.z + spot[1] + 0.5D);
            }
        }
        for (Entity piece : pool) { piece.discard(); }
    }

    private static boolean load(ServerLevel level, BoardGame game, BoardRules rules) {
        int span = rules.ranks;
        boolean ready = true;
        for (int cx = game.x - 2 - span >> 4; cx <= game.x + rules.files + 1 + span >> 4; cx++) {
            for (int cz = game.z >> 4; cz <= game.z + rules.ranks >> 4; cz++) {
                level.getChunk(cx, cz);
                ready &= level.areEntitiesLoaded(ChunkPos.pack(cx, cz));
            }
        }
        return ready;
    }

    private static int key(Entity piece) { return piece.getPersistentData().getIntOr(KIND, 0); }

    private static List<Entity> held(ServerLevel level, BoardGame game) {
        List<Entity> found = new ArrayList<>();
        for (Entity entity : level.getAllEntities()) {
            if (entity.isAlive() && game.name.equals(boardOf(entity)) && !stale(entity, game)) { found.add(entity); }
        }
        return found;
    }

    private static void spawn(ServerLevel level, BoardGame game, BoardRules rules, double[] spot) {
        int key = (int) spot[2];
        int side = key & 1;
        BoardPiece piece = rules.pieces.get(key >> 1);
        String mob = piece.mob(side);
        EntityType<?> type = Compat.entityType(mob);
        Entity made = type == null ? null : type.create(level, EntitySpawnReason.COMMAND);
        if (made == null) {
            if (UNMADE.add(rules.id + mob)) { ContentLog.LOGGER.error("Game {} plays {} as {}, which nothing registers, so that piece is not shown; this is said once", rules.id, piece.name, mob); }
            return;
        }
        float yaw = side == 0 ? 0.0F : 180.0F;
        made.snapTo(game.x + spot[0] + 0.5D, game.y, game.z + spot[1] + 0.5D, yaw, 0.0F);
        made.setYHeadRot(yaw);
        if (made instanceof Mob living) {
            living.setNoAi(true);
            living.setPersistenceRequired();
            living.yBodyRot = yaw;
        }
        made.setSilent(true);
        made.setNoGravity(true);
        LineCompat.makeInvulnerable(made);
        ChatFormatting color = Compat.teamColor(rules.colors[side]);
        made.setCustomName(Component.literal(rules.sides[side] + " " + piece.name).withStyle(color == null ? ChatFormatting.WHITE : color));
        made.addTag(TAG);
        CompoundTag data = made.getPersistentData();
        data.putString(BOARD, game.name);
        data.putInt(KIND, key);
        data.putLong(SPAWN, game.spawn);
        level.addFreshEntity(made);
    }
}

package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.board.BoardGame;
import mctmods.resourcedatapackloader.content.board.BoardPiece;
import mctmods.resourcedatapackloader.content.board.BoardRules;
import mctmods.resourcedatapackloader.content.board.BoardState;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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

    static void squares(World world, BoardGame game, BoardRules rules) {
        IBlockState light = rules.light.isEmpty() ? null : ContentStates.parse(rules.light, "game " + rules.id);
        IBlockState dark = rules.dark.isEmpty() ? null : ContentStates.parse(rules.dark, "game " + rules.id);
        for (int file = 0; file < rules.files; file++) {
            for (int rank = 0; rank < rules.ranks; rank++) {
                IBlockState state = (file + rank) % 2 == 0 ? dark : light;
                if (state != null) { world.setBlockState(new BlockPos(game.x + file, game.y - 1, game.z + rank), state, 2); }
            }
        }
    }

    static boolean stale(Entity entity, @Nullable BoardGame game) { return game == null || entity.getEntityData().getLong(SPAWN) != game.spawn; }

    @Nullable static String boardOf(Entity entity) { return entity.getTags().contains(TAG) ? entity.getEntityData().getString(BOARD) : null; }

    static int sideOf(Entity entity) { return entity.getEntityData().getInteger(KIND) & 1; }

    static int square(BoardGame game, BoardRules rules, BlockPos pos) {
        int file = pos.getX() - game.x;
        int rank = pos.getZ() - game.z;
        boolean level = pos.getY() == game.y || pos.getY() == game.y - 1;
        return level && file >= 0 && rank >= 0 && file < rules.files && rank < rules.ranks ? rank * rules.files + file : -1;
    }

    static void clear(World world, BoardGame game) {
        for (Entity piece : held(world, game)) { piece.setDead(); }
    }

    static void strike(World world, BoardGame game, BoardRules rules, int from, int victim) {
        Entity striker = standing(world, game, rules, from);
        Entity struck = standing(world, game, rules, victim);
        if (struck == null) {
            sync(world, game, rules);
            return;
        }
        if (striker instanceof EntityLivingBase) { ((EntityLivingBase) striker).swingArm(EnumHand.MAIN_HAND); }
        if (struck instanceof EntityLivingBase) { world.setEntityState(struck, (byte) 2); }
        if (world instanceof WorldServer) { ((WorldServer) world).spawnParticle(EnumParticleTypes.CRIT, struck.posX, struck.posY + struck.height * 0.5D, struck.posZ, 12, 0.3D, 0.3D, 0.3D, 0.2D); }
        world.playSound(null, struck.posX, struck.posY, struck.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, struck.getSoundCategory(), 1.0F, 1.0F);
        game.strike = STRIKE_TICKS;
    }

    @Nullable private static Entity standing(World world, BoardGame game, BoardRules rules, int square) {
        for (Entity piece : held(world, game)) {
            if (piece.getDistanceSq(game.x + square % rules.files + 0.5D, game.y, game.z + Math.floorDiv(square, rules.files) + 0.5D) < 0.01D) { return piece; }
        }
        return null;
    }

    static void sync(World world, BoardGame game, BoardRules rules) {
        BoardState state = game.state;
        game.strike = 0;
        if (state == null) { return; }
        load(world, game, rules);
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
        List<Entity> pool = held(world, game);
        List<double[]> open = new ArrayList<>();
        for (double[] spot : wanted) {
            Entity there = null;
            for (Entity piece : pool) {
                if (key(piece) == (int) spot[2] && piece.getDistanceSq(game.x + spot[0] + 0.5D, game.y, game.z + spot[1] + 0.5D) < 0.01D) { there = piece; }
            }
            if (there == null) { open.add(spot); }
            else { pool.remove(there); }
        }
        for (double[] spot : open) {
            Entity moved = null;
            for (Entity piece : pool) {
                if (key(piece) == (int) spot[2]) { moved = piece; }
            }
            if (moved == null) { spawn(world, game, rules, spot); }
            else {
                pool.remove(moved);
                moved.setPositionAndUpdate(game.x + spot[0] + 0.5D, game.y, game.z + spot[1] + 0.5D);
            }
        }
        for (Entity piece : pool) { piece.setDead(); }
    }

    private static void load(World world, BoardGame game, BoardRules rules) {
        int span = rules.ranks;
        for (int cx = game.x - 2 - span >> 4; cx <= game.x + rules.files + 1 + span >> 4; cx++) {
            for (int cz = game.z >> 4; cz <= game.z + rules.ranks >> 4; cz++) { world.getChunk(cx, cz); }
        }
    }

    private static int key(Entity piece) { return piece.getEntityData().getInteger(KIND); }

    private static List<Entity> held(World world, BoardGame game) {
        List<Entity> found = new ArrayList<>();
        for (Entity entity : world.loadedEntityList) {
            if (!entity.isDead && game.name.equals(boardOf(entity)) && !stale(entity, game)) { found.add(entity); }
        }
        return found;
    }

    private static void spawn(World world, BoardGame game, BoardRules rules, double[] spot) {
        int key = (int) spot[2];
        int side = key & 1;
        BoardPiece piece = rules.pieces.get(key >> 1);
        String mob = piece.mob(side);
        Entity made = EntityList.createEntityByIDFromName(new ResourceLocation(mob), world);
        if (made == null) {
            if (UNMADE.add(rules.id + mob)) { ContentLog.LOGGER.error("Game {} plays {} as {}, which nothing registers, so that piece is not shown; this is said once", rules.id, piece.name, mob); }
            return;
        }
        float yaw = side == 0 ? 0.0F : 180.0F;
        made.setLocationAndAngles(game.x + spot[0] + 0.5D, game.y, game.z + spot[1] + 0.5D, yaw, 0.0F);
        made.setRotationYawHead(yaw);
        if (made instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) made;
            living.setNoAI(true);
            living.enablePersistence();
            living.renderYawOffset = yaw;
        }
        made.setSilent(true);
        made.setNoGravity(true);
        made.setEntityInvulnerable(true);
        TextFormatting color = TextFormatting.getValueByName(rules.colors[side]);
        made.setCustomNameTag((color == null ? TextFormatting.WHITE : color) + rules.sides[side] + " " + piece.name);
        made.addTag(TAG);
        NBTTagCompound data = made.getEntityData();
        data.setString(BOARD, game.name);
        data.setInteger(KIND, key);
        data.setLong(SPAWN, game.spawn);
        world.spawnEntity(made);
    }
}

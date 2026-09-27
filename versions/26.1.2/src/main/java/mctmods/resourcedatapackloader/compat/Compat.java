package mctmods.resourcedatapackloader.compat;

import mctmods.resourcedatapackloader.util.SwoopMoveControl;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.scores.PlayerTeam;
import java.util.Locale;
import java.util.Optional;
import javax.annotation.Nullable;

public final class Compat {
    private Compat() {}

    public static Optional<EntityType<?>> lookupEntityType(Identifier id) { return EntityType.byString(id.toString()); }

    @Nullable public static EntityType<?> entityType(@Nullable Identifier id) { return id == null ? null : EntityType.byString(id.toString()).orElse(null); }

    @Nullable public static EntityType<?> entityType(String name) { return EntityType.byString(name).orElse(null); }

    public static EntityType<Player> player() { return EntityType.PLAYER; }

    public static EntityType<Villager> villager() { return EntityType.VILLAGER; }

    public static EntityType<ZombieVillager> zombieVillager() { return EntityType.ZOMBIE_VILLAGER; }

    public static EntityType<ItemEntity> item() { return EntityType.ITEM; }

    public static EntityType<ArmorStand> armorStand() { return EntityType.ARMOR_STAND; }

    public static boolean flies(Mob mob) { return mob instanceof FlyingAnimal; }

    public static boolean isSlime(Mob mob) { return mob instanceof Slime; }

    public static MoveControl moveControl(Mob mob) { return new MoveControl(mob); }

    public static MoveControl swoopMoveControl(Mob mob) { return new SwoopMoveControl(mob); }

    @SuppressWarnings("unused") public static void knockback(LivingEntity target, double power, double x, double z, DamageSource source, float damage) { target.knockback(power, x, z); }

    @Nullable public static ChatFormatting teamColor(String name) {
        ChatFormatting color = ChatFormatting.getByName(name.trim().toLowerCase(Locale.ROOT));
        return color != null && color.isColor() ? color : null;
    }

    public static void setTeamColor(PlayerTeam team, ChatFormatting color) { team.setColor(color); }

    public static Block orangeTerracotta() { return Blocks.ORANGE_TERRACOTTA; }

    public static TagKey<Block> saplings() { return BlockTags.SAPLINGS; }

    @SuppressWarnings("deprecation") public static LakeFeature.Configuration lake(Block fluid, Block barrier) { return new LakeFeature.Configuration(BlockStateProvider.simple(fluid), BlockStateProvider.simple(barrier)); }

    public static void markPostProcessing(ChunkAccess chunk, BlockPos pos) { chunk.markPosForPostprocessing(pos); }
}

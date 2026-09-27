package mctmods.resourcedatapackloader.compat;

import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.SwoopMoveControl;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.TeamColor;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import javax.annotation.Nullable;

public final class Compat {
    private Compat() {}

    public static Optional<EntityType<?>> lookupEntityType(Identifier id) { return BuiltInRegistries.ENTITY_TYPE.getOptional(id); }

    @Nullable public static EntityType<?> entityType(@Nullable Identifier id) { return Registered.find(BuiltInRegistries.ENTITY_TYPE, id); }

    @Nullable public static EntityType<?> entityType(String name) { return Registered.find(BuiltInRegistries.ENTITY_TYPE, Identifier.tryParse(name)); }

    public static EntityType<Player> player() { return EntityTypes.PLAYER; }

    public static EntityType<Villager> villager() { return EntityTypes.VILLAGER; }

    public static EntityType<ZombieVillager> zombieVillager() { return EntityTypes.ZOMBIE_VILLAGER; }

    public static EntityType<ItemEntity> item() { return EntityTypes.ITEM; }

    public static EntityType<ArmorStand> armorStand() { return EntityTypes.ARMOR_STAND; }

    public static boolean flies(Mob mob) { return mob.getNavigation() instanceof FlyingPathNavigation; }

    public static boolean isSlime(Mob mob) { return mob instanceof Slime; }

    public static MoveControl<Mob> moveControl(Mob mob) { return new MoveControl<>(mob); }

    public static MoveControl<Mob> swoopMoveControl(Mob mob) { return new SwoopMoveControl(mob); }

    public static void knockback(LivingEntity target, double power, double x, double z, DamageSource source, float damage) { target.knockback(power, x, z, source, damage); }

    @Nullable public static ChatFormatting teamColor(String name) {
        String asked = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
        return Arrays.stream(TeamColor.values()).filter(each -> each.getSerializedName().replace("_", "").equals(asked)).findFirst().map(each -> ChatFormatting.valueOf(each.name())).orElse(null);
    }

    public static void setTeamColor(PlayerTeam team, ChatFormatting color) { team.setColor(Optional.of(TeamColor.valueOf(color.name()))); }

    public static Block orangeTerracotta() { return Blocks.DYED_TERRACOTTA.pick(DyeColor.ORANGE); }

    public static TagKey<Block> saplings() { return BlockItemTags.SAPLINGS.block(); }

    @SuppressWarnings("deprecation") public static LakeFeature.Configuration lake(Block fluid, Block barrier) { return new LakeFeature.Configuration(BlockStateProvider.simple(fluid), BlockStateProvider.simple(barrier), BlockPredicate.alwaysTrue(), BlockPredicate.not(BlockPredicate.matchesTag(BlockTags.FEATURES_CANNOT_REPLACE)), BlockPredicate.not(BlockPredicate.matchesTag(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE))); }

    public static void markPostProcessing(ChunkAccess chunk, BlockPos pos) { chunk.markPosForPostProcessing(pos); }
}

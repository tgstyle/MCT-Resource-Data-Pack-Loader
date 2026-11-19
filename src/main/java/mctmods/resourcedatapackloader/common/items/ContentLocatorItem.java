package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.command.LocatorSearch;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.sounds.ContentSounds;

import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ContentLocatorItem extends Item {
    private final ItemDef def;

    public ContentLocatorItem(ItemDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    @Override @Nonnull public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer server) {
            if (!def.locator().sound().isEmpty()) { ContentSounds.play(server, def.locator().sound(), SoundSource.PLAYERS); }
            BlockPos found = LocatorSearch.find(server, def.locates());
            if (found == null || beyond(server, found)) { server.sendSystemMessage(Component.translatable("rdpl.command.gotonothing", def.locates()).withStyle(ChatFormatting.RED)); }
            else {
                point(stack, level, found);
                server.sendSystemMessage(Component.translatable("rdpl.compass.set", def.locates(), found.getX(), found.getZ()).withStyle(ChatFormatting.GREEN));
                if (def.locator().eye()) { throwEye(server, found); }
            }
        }
        ContentItemRuns.finish(level, player, hand, this, def);
        return InteractionResultHolder.success(stack);
    }

    private boolean beyond(ServerPlayer player, BlockPos found) {
        int range = def.locator().range();
        if (range <= 0) { return false; }
        double away = found.getX() + 0.5D - player.getX();
        double along = found.getZ() + 0.5D - player.getZ();
        return away * away + along * along > (double) range * range;
    }

    private static void throwEye(ServerPlayer player, BlockPos found) {
        EyeOfEnder eye = new EyeOfEnder(player.level(), player.getX(), player.getY(0.5D), player.getZ());
        eye.signalTo(found);
        player.level().addFreshEntity(eye);
    }

    private static void point(ItemStack stack, Level level, BlockPos found) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.put(CompassItem.TAG_LODESTONE_POS, NbtUtils.writeBlockPos(found));
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, level.dimension()).result().ifPresent(dimension -> tag.put(CompassItem.TAG_LODESTONE_DIMENSION, dimension));
        tag.putBoolean(CompassItem.TAG_LODESTONE_TRACKED, false);
    }
}

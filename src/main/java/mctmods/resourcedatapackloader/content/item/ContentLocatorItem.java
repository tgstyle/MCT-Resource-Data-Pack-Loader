package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.command.LocatorSearch;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.util.ContentEffects;

import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
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
            BlockPos found = LocatorSearch.find(server, def.locates());
            if (found == null) { server.sendSystemMessage(Component.translatable("rdpl.command.gotonothing", def.locates()).withStyle(ChatFormatting.RED)); }
            else {
                stack.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(GlobalPos.of(level.dimension(), found)), false));
                server.sendSystemMessage(Component.translatable("rdpl.compass.set", def.locates(), found.getX(), found.getZ()).withStyle(ChatFormatting.GREEN));
            }
        }
        ContentEffects.cooldown(player, this, def.cooldown());
        return InteractionResultHolder.success(stack);
    }
}

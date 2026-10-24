package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.block.ContentPortwayBlock;
import mctmods.resourcedatapackloader.content.def.PortwayItemDef;
import mctmods.resourcedatapackloader.content.menu.ContentPortwayMenu;
import mctmods.resourcedatapackloader.content.util.PortwayBinding;
import mctmods.resourcedatapackloader.content.util.PortwayFx;
import mctmods.resourcedatapackloader.content.util.PortwayStop;
import mctmods.resourcedatapackloader.content.util.PortwayTravel;
import mctmods.resourcedatapackloader.content.util.Portways;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public final class ContentPortwayItem extends Item {
    private final PortwayItemDef def;

    public ContentPortwayItem(PortwayItemDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    public static void spent(ServerPlayer player, InteractionHand hand, PortwayItemDef def) { if (def.consume() && !player.getAbilities().instabuild) { player.getItemInHand(hand).shrink(1); } }

    @Override @Nonnull public InteractionResult useOn(@Nonnull UseOnContext context) {
        if (!def.bind() || context.getPlayer() == null || !context.getPlayer().isShiftKeyDown() || !(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof ContentPortwayBlock)) { return InteractionResult.PASS; }
        if (context.getLevel() instanceof ServerLevel level && context.getPlayer() instanceof ServerPlayer player) { PortwayTravel.bind(player, level, context.getClickedPos(), context.getItemInHand(), def.settings()); }
        return InteractionResult.SUCCESS;
    }

    @Override @Nonnull public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer user) || level.isClientSide()) { return InteractionResult.SUCCESS; }
        if (!def.bind()) { user.openMenu(new SimpleMenuProvider((id, inventory, _) -> new ContentPortwayMenu(id, inventory, def, hand), stack.getHoverName())); }
        else {
            PortwayStop bound = PortwayBinding.read(stack);
            if (bound == null) { PortwayFx.refused(user, "rdpl.portway.unbound"); }
            else {
                PortwayTravel.Result result = PortwayTravel.travel(user, null, bound, def.settings());
                if (result == PortwayTravel.Result.GONE) { PortwayBinding.clear(stack); }
                else if (result == PortwayTravel.Result.ARRIVED) { spent(user, hand, def); }
            }
        }
        return InteractionResult.CONSUME;
    }

    @SuppressWarnings("deprecation") @Override public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull TooltipDisplay display, @Nonnull Consumer<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        PortwayStop bound = def.bind() ? PortwayBinding.read(stack) : null;
        if (bound != null) { tooltip.accept(Component.translatable("rdpl.portway.bound", Portways.label(bound)).withStyle(ChatFormatting.GRAY)); }
    }
}

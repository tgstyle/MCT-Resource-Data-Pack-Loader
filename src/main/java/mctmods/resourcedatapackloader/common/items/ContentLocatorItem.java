package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.command.LocatorSearch;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.LocatorDef;
import mctmods.resourcedatapackloader.common.sounds.ContentSounds;

import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class ContentLocatorItem extends Item {
    private final ItemDef def;

    public ContentLocatorItem(ItemDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    @Override @Nonnull public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer server) {
            LocatorDef locator = def.locator();
            if (!locator.sound().isEmpty()) { ContentSounds.play(server, locator.sound(), SoundSource.PLAYERS); }
            BlockPos found = LocatorSearch.find(server, locator.place());
            if (found == null || beyond(server, locator, found)) { server.sendSystemMessage(Component.translatable("rdpl.command.gotonothing", def.locates()).withStyle(ChatFormatting.RED)); }
            else {
                stack.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(GlobalPos.of(level.dimension(), found)), false));
                server.sendSystemMessage(Component.translatable("rdpl.compass.set", def.locates(), found.getX(), found.getZ()).withStyle(ChatFormatting.GREEN));
                if (locator.eye()) { throwEye(server, found); }
            }
        }
        return ItemRuns.use(level, player, hand, def);
    }

    private static boolean beyond(ServerPlayer player, LocatorDef locator, BlockPos found) {
        if (locator.range() <= 0) { return false; }
        double away = found.getX() + 0.5D - player.getX();
        double along = found.getZ() + 0.5D - player.getZ();
        return away * away + along * along > (double) locator.range() * locator.range();
    }

    private static void throwEye(ServerPlayer player, BlockPos found) {
        EyeOfEnder eye = new EyeOfEnder(player.level(), player.getX(), player.getY(0.5D), player.getZ());
        eye.setItem(new ItemStack(Items.ENDER_EYE));
        eye.signalTo(Vec3.atLowerCornerOf(found));
        player.level().addFreshEntity(eye);
    }
}

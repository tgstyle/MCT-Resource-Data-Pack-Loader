package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.pack.definitions.items.SeasonClockDef;

import javax.annotation.Nonnull;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ContentSeasonClockItem extends Item {
    private final SeasonClockDef clock;

    public ContentSeasonClockItem(SeasonClockDef clock, Properties properties) {
        super(properties);
        this.clock = clock;
    }

    @Override @Nonnull public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!clock.message()) { return super.use(level, player, hand); }
        if (!level.isClientSide()) { ContentSeasonClock.tell(clock, player); }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}

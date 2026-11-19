package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;

import javax.annotation.Nonnull;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ContentSeasonClockItem extends Item {
    private final ItemDef def;

    public ContentSeasonClockItem(ItemDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    @Override @Nonnull public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        boolean message = def.seasonClock() != null && def.seasonClock().message();
        if (!message && !def.acts()) { return super.use(level, player, hand); }
        if (message && !level.isClientSide()) { ContentSeasonClock.tell(def.seasonClock(), player); }
        if (def.acts()) { ContentItemRuns.finish(level, player, hand, this, def); }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}

package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.SeasonClockDef;
import mctmods.resourcedatapackloader.common.util.ContentEffects;

import javax.annotation.Nonnull;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ContentSeasonClockItem extends Item {
    private final ItemDef def;
    private final SeasonClockDef clock;

    public ContentSeasonClockItem(ItemDef def, SeasonClockDef clock, Properties properties) {
        super(properties);
        this.def = def;
        this.clock = clock;
    }

    @Override @Nonnull public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!level.isClientSide()) { ContentSeasonClock.tell(clock, player); }
        if (!def.runs().isEmpty() || def.script() != null) { ContentEffects.used(player, this, def, hand); }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}

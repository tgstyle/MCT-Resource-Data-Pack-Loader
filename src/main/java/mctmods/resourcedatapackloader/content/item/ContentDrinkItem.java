package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.util.ContentEffects;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentDrinkItem extends Item {
    private final ItemDef def;
    @Nullable private final MobEffectInstance effect;
    @Nullable private Item container;

    public ContentDrinkItem(ItemDef def, ItemVariant variant, Properties properties) {
        super(properties);
        this.def = def;
        this.effect = ContentEffects.parse(variant.id(), variant.potion());
    }

    public void resolveContainer(@Nullable Item item) { container = item; }

    @Override public int getUseDuration(@Nonnull ItemStack stack, @Nonnull LivingEntity entity) { return def.useDuration(); }

    @Override @Nonnull public UseAnim getUseAnimation(@Nonnull ItemStack stack) { return def.eat() ? UseAnim.EAT : UseAnim.DRINK; }

    @Override @Nonnull public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override @Nonnull public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity entity) {
        if (!level.isClientSide && effect != null) { entity.addEffect(ContentEffects.copy(effect)); }
        Player player = entity instanceof Player held ? held : null;
        if (player != null && def.cooldown() > 0) { player.getCooldowns().addCooldown(this, def.cooldown()); }
        if (player != null && player.getAbilities().instabuild) { return stack; }
        stack.shrink(1);
        if (container == null) { return stack; }
        ItemStack left = new ItemStack(container);
        if (stack.isEmpty()) { return left; }
        if (player != null && !player.getInventory().add(left)) { player.drop(left, false); }
        return stack;
    }

    @Override public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) { ContentEffects.tooltip(effect, tooltip); }
}

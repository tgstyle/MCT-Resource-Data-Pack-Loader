package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.def.ItemDef;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import javax.annotation.Nonnull;

public class ContentFoodItem extends Item {
    private final ItemDef def;

    public ContentFoodItem(ItemDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    public ItemDef getDef() { return def; }

    @Override @Nonnull public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity entity) {
        ItemStack left = super.finishUsingItem(stack, level, entity);
        if (def.cooldown() > 0 && entity instanceof Player player) { player.getCooldowns().addCooldown(this, def.cooldown()); }
        return left;
    }
}

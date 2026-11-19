package mctmods.resourcedatapackloader.common.blocks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;

public final class ContentToolOverrides {
    private static final Map<Item, Rule> RULES = new ConcurrentHashMap<>();

    private ContentToolOverrides() {}

    public record Rule(float speed, TagKey<Block> mines) {}

    public static void put(Item item, Rule rule) { RULES.put(item, rule); }

    public static void clear() { RULES.clear(); }

    @Nullable private static Rule matching(ItemStack stack, BlockState state) {
        if (RULES.isEmpty() || stack.isEmpty()) { return null; }
        Rule rule = RULES.get(stack.getItem());
        return rule != null && state.is(rule.mines()) ? rule : null;
    }

    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Rule rule = matching(event.getEntity().getMainHandItem(), event.getState());
        if (rule == null) { return; }
        float own = Math.max(1.0F, event.getEntity().getMainHandItem().getDestroySpeed(event.getState()));
        event.setNewSpeed(event.getOriginalSpeed() * (rule.speed() / own));
    }

    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        if (matching(event.getEntity().getMainHandItem(), event.getTargetBlock()) != null) { event.setCanHarvest(true); }
    }
}

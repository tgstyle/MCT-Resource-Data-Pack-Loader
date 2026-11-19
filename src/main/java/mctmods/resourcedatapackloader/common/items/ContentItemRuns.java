package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.scripts.RdplApi;
import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;
import mctmods.resourcedatapackloader.common.util.ContentEffects;
import mctmods.resourcedatapackloader.common.util.entity.Functions;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public final class ContentItemRuns {
    private ContentItemRuns() {}

    public static void finish(Level level, Player player, InteractionHand hand, Item item, ItemDef def) {
        if (!level.isClientSide() && !def.runs().isEmpty() && player instanceof ServerPlayer server) { Functions.runAs(server, def.runs(), "The item " + def.key()); }
        if (!cancelled(level, player, hand, def)) { ContentEffects.cooldown(player, item, def.cooldown()); }
    }

    private static boolean cancelled(Level level, Player player, InteractionHand hand, ItemDef def) {
        if (def.script() == null || !(level instanceof ServerLevel server)) { return false; }
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("rdpl", new RdplApi(server, player));
        bindings.put("item", player.getItemInHand(hand));
        bindings.put("hand", hand.name());
        return "cancel".equals(ScriptEngine.run(def.script(), bindings));
    }
}

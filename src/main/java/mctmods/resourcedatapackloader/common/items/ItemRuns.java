package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.scripts.RdplApi;
import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;
import mctmods.resourcedatapackloader.common.util.ContentEffects;
import mctmods.resourcedatapackloader.common.util.entity.Functions;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class ItemRuns {
    private ItemRuns() {}

    public static boolean run(Level level, Player player, InteractionHand hand, ItemDef def) {
        if (!(level instanceof ServerLevel server) || !(player instanceof ServerPlayer who)) { return true; }
        if (!def.runs().isEmpty()) { Functions.runAs(who, def.runs(), "The item " + def.key()); }
        if (def.script() == null) { return true; }
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("rdpl", new RdplApi(server, player));
        bindings.put("item", player.getItemInHand(hand));
        bindings.put("hand", hand.name());
        return !"cancel".equals(ScriptEngine.run(def.script(), bindings));
    }

    @Nonnull public static InteractionResult use(Level level, Player player, InteractionHand hand, ItemDef def) {
        if (run(level, player, hand, def)) { ContentEffects.cooldown(player, player.getItemInHand(hand), def.cooldown()); }
        return InteractionResult.SUCCESS;
    }
}

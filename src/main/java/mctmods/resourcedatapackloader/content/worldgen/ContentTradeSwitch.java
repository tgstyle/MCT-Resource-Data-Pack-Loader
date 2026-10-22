package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.TradeDef;
import mctmods.resourcedatapackloader.content.def.TradeSwitchDef;
import mctmods.resourcedatapackloader.content.extra.ContentTrade;
import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.content.interfaces.ITradeSwitchHolder;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentTradeSwitch {
    private static final String RECORD = "RDPLTradeSwitch";
    private static final Map<String, Integer> STAMPS = new HashMap<>();
    private static final Map<TradeSwitchDef, Map<String, List<ContentTrade.Listing>>> LISTINGS = new IdentityHashMap<>();

    public static final class Held {
        private static final Codec<Held> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("set").forGetter(held -> held.set),
                Codec.INT.fieldOf("stamp").forGetter(held -> held.stamp),
                Codec.STRING.optionalFieldOf("profession", "").forGetter(held -> held.profession),
                Codec.INT.fieldOf("level").forGetter(held -> held.level),
                MerchantOffers.CODEC.fieldOf("offers").forGetter(held -> held.offers))
                .apply(instance, Held::new));
        private final String set;
        private final int stamp;
        private final String profession;
        private int level;
        private final MerchantOffers offers;

        private Held(String set, int stamp, String profession, int level, MerchantOffers offers) {
            this.set = set;
            this.stamp = stamp;
            this.profession = profession;
            this.level = level;
            this.offers = offers;
        }
    }

    private ContentTradeSwitch() {}

    @Nullable static TradeSwitchDef parse(Identifier key, String owner, String set, JsonElement element) {
        if (!element.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives trades that is not a list of trades, ignoring it", owner);
            return null;
        }
        List<TradeDef> trades = new ArrayList<>();
        for (JsonElement listed : element.getAsJsonArray()) {
            if (!listed.isJsonObject()) {
                ContentLog.LOGGER.error("{} gives a trade that is not an object, skipping it", owner);
                continue;
            }
            TradeDef def = ContentVillagers.trade(key, listed.getAsJsonObject());
            if (def != null) { trades.add(def); }
        }
        return trades.isEmpty() ? null : new TradeSwitchDef(set, element.toString().hashCode(), trades);
    }

    static void start(Collection<TradeSwitchDef> defs) {
        forget();
        for (TradeSwitchDef def : defs) {
            Map<String, List<ContentTrade.Listing>> byProfession = new HashMap<>();
            for (TradeDef trade : def.trades()) {
                ContentTrade.Listing listing = ContentVillagers.known(trade) ? ContentVillagers.listing(trade) : null;
                if (listing != null) { byProfession.computeIfAbsent(trade.profession(), _ -> new ArrayList<>()).add(listing); }
            }
            LISTINGS.put(def, byProfession);
            STAMPS.put(def.set(), def.stamp());
        }
    }

    public static void forget() {
        LISTINGS.clear();
        STAMPS.clear();
    }

    @Nullable public static MerchantOffers offers(Villager villager) {
        if (!Config.content.villagers() || !(villager.level() instanceof ServerLevel level)) { return null; }
        TradeSwitchDef def = ContentEnvironments.trades(level, villager.blockPosition());
        Map<String, List<ContentTrade.Listing>> byProfession = LISTINGS.get(def);
        if (byProfession == null) { return null; }
        VillagerData data = villager.getVillagerData();
        List<ContentTrade.Listing> mine = data.profession().unwrapKey().map(key -> byProfession.get(key.identifier().toString())).orElse(null);
        if (mine == null) { return null; }
        Map<String, Held> held = ((ITradeSwitchHolder) villager).rdpl$held();
        String profession = data.profession().unwrapKey().map(key -> key.identifier().toString()).orElse("");
        Held current = held.get(def.set());
        if (current == null || current.stamp != def.stamp() || !current.profession.equals(profession)) {
            current = new Held(def.set(), def.stamp(), profession, 0, new MerchantOffers());
            held.put(def.set(), current);
        }
        for (int next = current.level + 1; next <= data.level(); next++) {
            for (ContentTrade.Listing listing : mine) {
                if (listing.def().level() == next) { current.offers.add(ContentTrade.offer(listing.def(), listing.buy(), listing.buySecondary(), listing.sell(), villager.getRandom())); }
            }
        }
        current.level = Math.max(current.level, data.level());
        return current.offers;
    }

    static void closeTrades(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            if (player.containerMenu instanceof MerchantMenu) { player.closeContainer(); }
        }
    }

    public static void write(ITradeSwitchHolder holder, ValueOutput output) {
        List<Held> kept = new ArrayList<>();
        for (Held held : holder.rdpl$held().values()) {
            Integer stamp = STAMPS.get(held.set);
            if (stamp != null && stamp == held.stamp) { kept.add(held); }
        }
        if (!kept.isEmpty()) { output.store(RECORD, Held.CODEC.listOf(), kept); }
    }

    public static void read(ITradeSwitchHolder holder, ValueInput input) {
        Map<String, Held> held = holder.rdpl$held();
        held.clear();
        for (Held saved : input.read(RECORD, Held.CODEC.listOf()).orElse(List.of())) { held.put(saved.set, saved); }
    }
}

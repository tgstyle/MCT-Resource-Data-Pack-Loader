package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.TradeDef;
import mctmods.resourcedatapackloader.content.def.TradeSwitchDef;
import mctmods.resourcedatapackloader.content.extra.ContentTrade;
import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.content.interfaces.ITradeSwitchHolder;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentTradeSwitch {
    private static final String RECORD = "RDPLTradeSwitch";
    private static final String STAMP = "Stamp";
    private static final String LEVEL = "Level";
    private static final String OFFERS = "Offers";
    private static final String PROFESSION = "Profession";
    private static final Map<String, Integer> STAMPS = new HashMap<>();
    private static final Map<TradeDef, ContentTrade> LISTINGS = new IdentityHashMap<>();

    public static final class Held {
        final int stamp;
        final String profession;
        int level;
        final MerchantOffers offers;

        Held(int stamp, String profession, int level, MerchantOffers offers) {
            this.stamp = stamp;
            this.profession = profession;
            this.level = level;
            this.offers = offers;
        }
    }

    private ContentTradeSwitch() {}

    @Nullable static TradeSwitchDef parse(ResourceLocation key, String owner, String set, @Nullable JsonElement element) {
        if (element == null) { return null; }
        if (!element.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives trades that is not a list, ignoring it", owner);
            return null;
        }
        List<TradeDef> trades = new ArrayList<>();
        for (JsonElement one : element.getAsJsonArray()) {
            if (!one.isJsonObject()) {
                ContentLog.LOGGER.error("{} gives a trade that is not an object, skipping it", owner);
                continue;
            }
            TradeDef def = ContentVillagers.trade(key, one.getAsJsonObject());
            if (def != null) { trades.add(def); }
        }
        return new TradeSwitchDef(set, element.toString().hashCode(), List.copyOf(trades));
    }

    static void start(Collection<TradeSwitchDef> defs) {
        forget();
        for (TradeSwitchDef def : defs) {
            STAMPS.put(def.set(), def.stamp());
            for (TradeDef trade : def.trades()) {
                if (!ContentVillagers.known(trade)) { continue; }
                ContentTrade listing = ContentVillagers.listing(trade);
                if (listing != null) { LISTINGS.put(trade, listing); }
            }
        }
    }

    public static void forget() {
        STAMPS.clear();
        LISTINGS.clear();
    }

    @Nullable public static MerchantOffers offers(Villager villager) {
        ITradeSwitchHolder holder = (ITradeSwitchHolder) villager;
        if (!(villager.level() instanceof ServerLevel world) || !Config.content.villagers() || holder.rdpl$own()) { return null; }
        TradeSwitchDef def = ContentEnvironments.trades(world, villager.blockPosition());
        if (def.trades().isEmpty()) { return null; }
        String profession = String.valueOf(BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession()));
        List<TradeDef> mine = new ArrayList<>();
        for (TradeDef trade : def.trades()) {
            if (trade.profession().equals(profession) && LISTINGS.containsKey(trade)) { mine.add(trade); }
        }
        if (mine.isEmpty()) { return null; }
        Held now = holder.rdpl$held().get(def.set());
        if (now == null || now.stamp != def.stamp() || !now.profession.equals(profession)) {
            now = new Held(def.stamp(), profession, 0, new MerchantOffers());
            holder.rdpl$held().put(def.set(), now);
        }
        int reached = villager.getVillagerData().getLevel();
        for (int step = now.level + 1; step <= reached; step++) {
            for (TradeDef trade : mine) {
                if (trade.level() != step) { continue; }
                MerchantOffer offer = LISTINGS.get(trade).getOffer(villager, villager.getRandom());
                if (offer != null) { now.offers.add(offer); }
            }
        }
        now.level = Math.max(now.level, reached);
        return now.offers;
    }

    static void closeTrades(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            if (player.containerMenu instanceof MerchantMenu) { player.closeContainer(); }
        }
    }

    public static void write(Villager villager, CompoundTag tag) {
        RegistryOps<Tag> ops = villager.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        CompoundTag record = new CompoundTag();
        for (Map.Entry<String, Held> entry : ((ITradeSwitchHolder) villager).rdpl$held().entrySet()) {
            Held held = entry.getValue();
            Integer stamp = STAMPS.get(entry.getKey());
            if (stamp == null || stamp != held.stamp) { continue; }
            CompoundTag one = new CompoundTag();
            one.putInt(STAMP, held.stamp);
            one.putString(PROFESSION, held.profession);
            one.putInt(LEVEL, held.level);
            one.put(OFFERS, MerchantOffers.CODEC.encodeStart(ops, held.offers).getOrThrow());
            record.put(entry.getKey(), one);
        }
        if (!record.isEmpty()) { tag.put(RECORD, record); }
    }

    public static void read(Villager villager, CompoundTag tag) {
        RegistryOps<Tag> ops = villager.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        Map<String, Held> held = ((ITradeSwitchHolder) villager).rdpl$held();
        held.clear();
        CompoundTag record = tag.getCompound(RECORD);
        for (String key : record.getAllKeys()) {
            CompoundTag one = record.getCompound(key);
            if (!one.contains(OFFERS)) { continue; }
            MerchantOffers.CODEC.parse(ops, one.get(OFFERS))
                    .resultOrPartial(message -> ContentLog.LOGGER.warn("Failed to load the trades kept for {}: {}", key, message))
                    .ifPresent(offers -> held.put(key, new Held(one.getInt(STAMP), one.getString(PROFESSION), one.getInt(LEVEL), offers)));
        }
    }
}

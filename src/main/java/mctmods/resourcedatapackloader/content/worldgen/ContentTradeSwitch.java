package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.TradeDef;
import mctmods.resourcedatapackloader.content.def.TradeSwitchDef;
import mctmods.resourcedatapackloader.content.extra.ContentTrade;
import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.content.interfaces.ITradeSwitchHolder;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.event.village.MerchantTradeOffersEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

public final class ContentTradeSwitch {
    private static final String RECORD = "RDPLTradeSwitch";
    private static final String STAMP = "Stamp";
    private static final String PROFESSION = "Profession";
    private static final String CAREER = "Career";
    private static final String LEVEL = "Level";
    private static final String OFFERS = "Offers";
    private static final Map<String, Integer> STAMPS = new HashMap<>();
    private static final Map<TradeSwitchDef, List<Listed>> LISTED = new IdentityHashMap<>();

    private ContentTradeSwitch() {}

    public static final class Held {
        final int stamp;
        final String profession;
        final String career;
        int level;
        final MerchantRecipeList offers;

        Held(int stamp, String profession, String career, int level, MerchantRecipeList offers) {
            this.stamp = stamp;
            this.profession = profession;
            this.career = career;
            this.level = level;
            this.offers = offers;
        }
    }

    private static final class Listed {
        final ResourceLocation profession;
        final String career;
        final int level;
        final ContentTrade trade;

        Listed(ResourceLocation profession, String career, int level, ContentTrade trade) {
            this.profession = profession;
            this.career = career;
            this.level = level;
            this.trade = trade;
        }
    }

    @Nullable static TradeSwitchDef parse(ResourceLocation key, String owner, String set, @Nullable JsonElement element) {
        if (element == null) { return null; }
        if (!element.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives trades that is not a list of trades, ignoring it", owner);
            return null;
        }
        List<TradeDef> trades = new ArrayList<>();
        for (JsonElement entry : element.getAsJsonArray()) {
            if (!entry.isJsonObject()) {
                ContentLog.LOGGER.error("{} gives a trade that is not an object, skipping it", owner);
                continue;
            }
            TradeDef def = ContentVillagers.trade(key, entry.getAsJsonObject());
            if (def != null) { trades.add(def); }
        }
        return new TradeSwitchDef(set, element.toString().hashCode(), trades);
    }

    public static void serverStarted() {
        STAMPS.clear();
        LISTED.clear();
        for (TradeSwitchDef def : ContentEnvironments.allTrades()) {
            STAMPS.put(def.set, def.stamp);
            List<Listed> found = new ArrayList<>();
            for (TradeDef trade : def.trades) {
                ContentTrade listing = ContentVillagers.known(trade) ? ContentVillagers.listing(trade) : null;
                if (listing != null) { found.add(new Listed(new ResourceLocation(trade.profession), trade.career, trade.level, listing)); }
            }
            found.sort(Comparator.comparingInt(listed -> listed.level));
            LISTED.put(def, found);
        }
    }

    public static void serverStopped() {
        STAMPS.clear();
        LISTED.clear();
    }

    @SubscribeEvent public static void onTradeOffers(MerchantTradeOffersEvent event) {
        if (!(event.getMerchant() instanceof EntityVillager) || !Config.content.villagers) { return; }
        EntityVillager villager = (EntityVillager) event.getMerchant();
        if (villager.world.isRemote) { return; }
        MerchantRecipeList live = offers(villager);
        if (live == null) { return; }
        MerchantRecipeList shown = new MerchantRecipeList();
        shown.addAll(live);
        event.setList(shown);
    }

    @Nullable private static MerchantRecipeList offers(EntityVillager villager) {
        TradeSwitchDef def = ContentEnvironments.trades(villager.world, new BlockPos(villager));
        List<Listed> all = LISTED.get(def);
        int career = villager.careerId;
        if (all == null || career == 0) { return null; }
        ResourceLocation profession = villager.getProfessionForge().getRegistryName();
        String name = villager.getProfessionForge().getCareer(career - 1).getName();
        List<Listed> mine = new ArrayList<>();
        for (Listed listed : all) {
            if (listed.career.equals(name) && listed.profession.equals(profession)) { mine.add(listed); }
        }
        if (mine.isEmpty()) { return null; }
        Map<String, Held> held = ((ITradeSwitchHolder) villager).rdpl$held();
        Held now = held.get(def.set);
        if (now == null || now.stamp != def.stamp || !now.profession.equals(profession.toString()) || !now.career.equals(name)) {
            now = new Held(def.stamp, profession.toString(), name, 0, new MerchantRecipeList());
            held.put(def.set, now);
        }
        int level = villager.careerLevel;
        if (now.level < level) {
            for (Listed listed : mine) {
                if (listed.level > now.level && listed.level <= level) { listed.trade.addMerchantRecipe(villager, now.offers, villager.getRNG()); }
            }
            now.level = level;
        }
        return now.offers;
    }

    public static void restock(EntityVillager villager) {
        TradeSwitchDef def = ContentEnvironments.trades(villager.world, new BlockPos(villager));
        Held now = ((ITradeSwitchHolder) villager).rdpl$held().get(def.set);
        if (now == null || now.stamp != def.stamp) { return; }
        Random rand = villager.getRNG();
        for (MerchantRecipe recipe : now.offers) {
            if (recipe.isRecipeDisabled()) { recipe.increaseMaxTradeUses(rand.nextInt(6) + rand.nextInt(6) + 2); }
        }
    }

    static void closeTrades(World world) {
        for (EntityPlayer player : world.playerEntities) {
            if (player.openContainer instanceof ContainerMerchant) { player.closeScreen(); }
        }
    }

    public static void write(EntityVillager villager, NBTTagCompound compound) {
        NBTTagCompound record = new NBTTagCompound();
        for (Map.Entry<String, Held> entry : ((ITradeSwitchHolder) villager).rdpl$held().entrySet()) {
            Integer stamp = STAMPS.get(entry.getKey());
            Held now = entry.getValue();
            if (stamp == null || stamp != now.stamp) { continue; }
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(STAMP, now.stamp);
            tag.setString(PROFESSION, now.profession);
            tag.setString(CAREER, now.career);
            tag.setInteger(LEVEL, now.level);
            tag.setTag(OFFERS, now.offers.getRecipiesAsTags());
            record.setTag(entry.getKey(), tag);
        }
        if (!record.isEmpty()) { compound.setTag(RECORD, record); }
    }

    public static void read(EntityVillager villager, NBTTagCompound compound) {
        Map<String, Held> held = ((ITradeSwitchHolder) villager).rdpl$held();
        held.clear();
        NBTTagCompound record = compound.getCompoundTag(RECORD);
        for (String key : record.getKeySet()) {
            NBTTagCompound tag = record.getCompoundTag(key);
            held.put(key, new Held(tag.getInteger(STAMP), tag.getString(PROFESSION), tag.getString(CAREER), tag.getInteger(LEVEL), new MerchantRecipeList(tag.getCompoundTag(OFFERS))));
        }
    }
}

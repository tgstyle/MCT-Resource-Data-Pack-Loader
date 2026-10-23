package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.compat.BaublesPouch;
import mctmods.resourcedatapackloader.content.def.AttributeDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.PotionEffectDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentItem;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ContentWorn {
    private static final ResourceLocation KEY = new ResourceLocation("rdpl", "worn");
    private static final int POLL = 20;
    private static final int LENGTH = 40;
    private static Map<Item, Worn> worn;
    private static List<Entry> everything;

    private ContentWorn() {}

    private static final class Entry {
        final IAttribute attribute;
        final UUID id;
        final AttributeModifier modifier;

        Entry(IAttribute attribute, UUID id, AttributeModifier modifier) {
            this.attribute = attribute;
            this.id = id;
            this.modifier = modifier;
        }
    }

    private static final class Effect {
        final Potion potion;
        final PotionEffectDef def;

        Effect(Potion potion, PotionEffectDef def) {
            this.potion = potion;
            this.def = def;
        }
    }

    private static final class Worn {
        final List<Effect> effects = new ArrayList<>();
        final List<Entry> entries = new ArrayList<>();
    }

    public static void register() {
        if (ContentItemContainer.baubled()) {
            MinecraftForge.EVENT_BUS.register(ContentWorn.class);
            return;
        }
        for (Map.Entry<ResourceLocation, Item> entry : ContentRegistry.registeredItems()) {
            ItemDef def = ContentRegistry.itemDef(entry.getKey());
            if (def != null && (!def.wornEffects.isEmpty() || !def.wornAttributes.isEmpty())) {
                ContentLog.LOGGER.info("Baubles is not installed, so the worn effects and attributes on {} are ignored", entry.getKey());
            }
        }
    }

    @SubscribeEvent public static void onAttach(AttachCapabilitiesEvent<ItemStack> event) {
        Item item = event.getObject().getItem();
        if (!(item instanceof IContentItem) || item.getRegistryName() == null) { return; }
        ItemDef def = ContentRegistry.itemDef(item.getRegistryName());
        if (def == null || def.bauble.isEmpty()) { return; }
        ICapabilityProvider provider = BaublesPouch.provider(def.bauble);
        if (provider != null) { event.addCapability(KEY, provider); }
    }

    @SubscribeEvent public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (event.phase != TickEvent.Phase.END || player == null || player.world == null || player.world.isRemote || player.ticksExisted % POLL != 0) { return; }
        if (worn == null) { build(); }
        if (worn.isEmpty()) { return; }
        Set<UUID> wanted = new HashSet<>();
        for (ItemStack stack : BaublesPouch.wornStacks(player)) {
            Worn held = worn.get(stack.getItem());
            if (held == null) { continue; }
            for (Effect effect : held.effects) { refresh(player, effect); }
            for (Entry entry : held.entries) {
                wanted.add(entry.id);
                IAttributeInstance instance = player.getEntityAttribute(entry.attribute);
                if (instance.getModifier(entry.id) == null) { instance.applyModifier(entry.modifier); }
            }
        }
        for (Entry entry : everything) {
            if (wanted.contains(entry.id)) { continue; }
            IAttributeInstance instance = player.getEntityAttribute(entry.attribute);
            if (instance.getModifier(entry.id) == null) { continue; }
            instance.removeModifier(entry.id);
            if (entry.attribute == SharedMonsterAttributes.MAX_HEALTH && player.getHealth() > player.getMaxHealth()) { player.setHealth(player.getMaxHealth()); }
        }
    }

    private static void refresh(EntityPlayer player, Effect effect) {
        PotionEffect active = player.getActivePotionEffect(effect.potion);
        if (active != null && active.getDuration() > POLL) { return; }
        player.addPotionEffect(new PotionEffect(effect.potion, LENGTH, effect.def.amplifier, effect.def.ambient, effect.def.showParticles));
    }

    private static void build() {
        worn = new HashMap<>();
        everything = new ArrayList<>();
        for (Map.Entry<ResourceLocation, Item> entry : ContentRegistry.registeredItems()) {
            ItemDef def = ContentRegistry.itemDef(entry.getKey());
            if (def == null) { continue; }
            if (!def.worn()) {
                if (!def.wornEffects.isEmpty() || !def.wornAttributes.isEmpty()) { ContentLog.LOGGER.info("{} has worn effects or attributes but no bauble slot, so they are ignored", entry.getKey()); }
                continue;
            }
            Worn held = new Worn();
            for (PotionEffectDef effect : def.wornEffects) {
                Potion potion = Potion.getPotionFromResourceLocation(effect.potion);
                if (potion == null) { ContentLog.LOGGER.error("Worn effect '{}' on {} is not a potion, skipping it", effect.potion, entry.getKey()); }
                else { held.effects.add(new Effect(potion, effect)); }
            }
            for (int index = 0; index < def.wornAttributes.size(); index++) {
                AttributeDef modifier = def.wornAttributes.get(index);
                IAttribute attribute = ContentAttributes.find(modifier.attribute, entry.getKey());
                if (attribute == null) { continue; }
                String name = "rdpl:worn:" + entry.getKey() + "/" + index;
                UUID id = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
                Entry made = new Entry(attribute, id, new AttributeModifier(id, name, modifier.amount, modifier.operation).setSaved(false));
                held.entries.add(made);
                everything.add(made);
            }
            worn.put(entry.getValue(), held);
        }
    }
}

package mctmods.resourcedatapackloader.common.pack.definitions.items;

import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortwayItemDef;
import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.types.ContentItemTypes;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class ItemDef {
    public final ResourceLocation registryName;
    public final String type;
    public final String creativeTab;
    public final boolean alwaysEdible;
    public final Map<Integer, ItemVariant> byMeta;
    public final List<ItemVariant> visible;
    public final List<String> requires;
    public final int useDuration;
    public final boolean eat;
    public final String container;
    @Nullable public final ContainerDef holds;
    public final String material;
    public final String toolClass;
    public final String slot;
    public final String crop;
    public final String soil;
    public final List<String> potionTypes;
    public final float attackSpeed;
    public final int cooldown;
    public final String rocket;
    public final String rolls;
    public final String rollStore;
    public final boolean passesTurn;
    public final String locates;
    public final LocatorDef locator;
    public final String bauble;
    public final List<PotionEffectDef> wornEffects;
    public final List<AttributeDef> wornAttributes;
    @Nullable public final WeaponDef weapon;
    @Nullable public final PortwayItemDef portway;
    public final boolean sleepingBag;
    @Nullable public final ContainerUpgradeDef upgrade;
    public final int weight;
    @Nullable public final FireworkDef firework;
    @Nullable public final SeasonClockDef seasonClock;
    public final String runs;
    @Nullable public final ScriptRef script;
    @Nullable public final RecordDef record;
    @Nullable public final DerivedDef derived;
    private ItemStack resolvedContainer = ItemStack.EMPTY;

    public ItemDef(ResourceLocation registryName, String type, String creativeTab, boolean alwaysEdible, Map<Integer, ItemVariant> byMeta, List<ItemVariant> visible, List<String> requires, int useDuration, boolean eat, String container, String material, String toolClass, String slot, String crop, String soil, List<String> potionTypes, float attackSpeed, int cooldown, @Nullable ContainerDef holds, String rocket, String rolls, String rollStore, boolean passesTurn, LocatorDef locator, String bauble, List<PotionEffectDef> wornEffects, List<AttributeDef> wornAttributes, @Nullable WeaponDef weapon, @Nullable PortwayItemDef portway, boolean sleepingBag, @Nullable ContainerUpgradeDef upgrade, int weight, @Nullable FireworkDef firework, @Nullable SeasonClockDef seasonClock, String runs, @Nullable ScriptRef script, @Nullable RecordDef record) {
        this.registryName = registryName;
        this.type = type;
        this.creativeTab = creativeTab;
        this.alwaysEdible = alwaysEdible;
        this.byMeta = byMeta;
        this.visible = visible;
        this.requires = requires;
        this.useDuration = useDuration;
        this.eat = eat;
        this.container = container;
        this.holds = holds;
        this.material = material;
        this.toolClass = toolClass;
        this.slot = slot;
        this.crop = crop;
        this.soil = soil;
        this.potionTypes = potionTypes;
        this.attackSpeed = attackSpeed;
        this.cooldown = cooldown;
        this.rocket = rocket;
        this.rolls = rolls;
        this.rollStore = rollStore;
        this.passesTurn = passesTurn;
        this.locates = locator.place;
        this.locator = locator;
        this.bauble = bauble;
        this.wornEffects = wornEffects;
        this.wornAttributes = wornAttributes;
        this.weapon = weapon;
        this.portway = portway;
        this.sleepingBag = sleepingBag;
        this.upgrade = upgrade;
        this.weight = weight;
        this.firework = firework;
        this.seasonClock = seasonClock;
        this.runs = runs;
        this.script = script;
        this.record = record;
        this.derived = null;
    }

    private ItemDef(ResourceLocation registryName, String creativeTab, List<String> requires, String bauble, List<PotionEffectDef> wornEffects, List<AttributeDef> wornAttributes, @Nullable WeaponDef weapon, int weight, @Nullable DerivedDef derived) {
        this.registryName = registryName;
        this.type = ContentItemTypes.DEFAULT;
        this.creativeTab = creativeTab;
        this.alwaysEdible = false;
        this.byMeta = Collections.emptyMap();
        this.visible = Collections.emptyList();
        this.requires = requires;
        this.useDuration = 32;
        this.eat = false;
        this.container = "";
        this.holds = null;
        this.material = "";
        this.toolClass = "";
        this.slot = "";
        this.crop = "";
        this.soil = "";
        this.potionTypes = Collections.emptyList();
        this.attackSpeed = Float.NaN;
        this.cooldown = 0;
        this.rocket = "";
        this.rolls = "";
        this.rollStore = "";
        this.passesTurn = false;
        this.locator = new LocatorDef("", false, "", 0);
        this.locates = locator.place;
        this.bauble = bauble;
        this.wornEffects = wornEffects;
        this.wornAttributes = wornAttributes;
        this.weapon = weapon;
        this.portway = null;
        this.sleepingBag = false;
        this.upgrade = null;
        this.weight = weight;
        this.firework = null;
        this.seasonClock = null;
        this.runs = "";
        this.script = null;
        this.record = null;
        this.derived = derived;
    }

    public static ItemDef derived(ResourceLocation registryName, String creativeTab, List<String> requires, String bauble, List<PotionEffectDef> wornEffects, List<AttributeDef> wornAttributes, @Nullable WeaponDef weapon, int weight, DerivedDef derived) {
        return new ItemDef(registryName, creativeTab, requires, bauble, wornEffects, wornAttributes, weapon, weight, derived);
    }

    public boolean hasWornEffects() { return !wornEffects.isEmpty() || !wornAttributes.isEmpty(); }

    public boolean worn() { return !bauble.isEmpty() && hasWornEffects(); }

    @Nullable public ItemVariant at(int meta) { return byMeta.get(meta); }

    public void resolveContainer(ItemStack stack) { this.resolvedContainer = stack; }

    public ItemStack getResolvedContainer() { return resolvedContainer; }
}

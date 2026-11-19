package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.board.ContentDice;
import mctmods.resourcedatapackloader.common.board.ContentTurns;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemVariant;
import mctmods.resourcedatapackloader.common.scripts.RdplApi;
import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;
import mctmods.resourcedatapackloader.common.sleeping.ContentSleepingBags;
import mctmods.resourcedatapackloader.common.util.entity.Functions;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

@SuppressWarnings("deprecation") public class ContentItem extends Item implements IContentItem {
    private final ItemDef def;

    public ContentItem(ItemDef def) {
        this.def = def;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        setHasSubtypes(true);
        setMaxDamage(0);
        if (!def.locates.isEmpty()) { addPropertyOverride(new ResourceLocation("angle"), new ContentCompassNeedle()); }
        if (def.seasonClock != null) {
            addPropertyOverride(new ResourceLocation("rdpl", "year"), ContentSeasonClock.year(def.seasonClock));
            addPropertyOverride(new ResourceLocation("rdpl", "season"), ContentSeasonClock.season(def.seasonClock));
        }
        ContentSetup.apply(this, def.creativeTab);
    }

    @Nullable protected ItemVariant variant(ItemStack stack) { return ItemVariants.at(def, stack); }

    @Override public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) { if (isInCreativeTab(tab)) { ItemVariants.subItems(this, def, list); } }

    @Override @Nonnull public String getTranslationKey(@Nonnull ItemStack stack) { return ItemVariants.key(this, def, stack); }

    @Override @Nonnull public EnumRarity getRarity(@Nonnull ItemStack stack) {
        ItemVariant value = variant(stack);
        return value == null ? EnumRarity.COMMON : value.rarity;
    }

    @Override public int getItemStackLimit(@Nonnull ItemStack stack) {
        ItemVariant value = variant(stack);
        return value == null ? 64 : value.maxSize;
    }

    @Override public int getMetadata(int damage) { return damage; }

    @Override @Nonnull public EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!def.sleepingBag || !def.locates.isEmpty() || !def.rolls.isEmpty() || def.passesTurn) { return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ); }
        EnumActionResult result = ContentSleepingBags.use(player, world, pos, facing);
        if (result == EnumActionResult.SUCCESS) {
            if (usedToCooldown(world, player, hand) && def.cooldown > 0) { player.getCooldownTracker().setCooldown(this, def.cooldown); }
        }
        return result;
    }

    @Override @Nonnull public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (def.rolls.isEmpty() && !def.passesTurn && def.locates.isEmpty()) {
            boolean clock = def.seasonClock != null && def.seasonClock.message;
            boolean acts = !def.runs.isEmpty() || def.script != null;
            if (!clock && !acts) { return super.onItemRightClick(world, player, hand); }
            if (clock && !world.isRemote) { ContentSeasonClock.tell(def.seasonClock, player); }
            if (acts && usedToCooldown(world, player, hand) && def.cooldown > 0) { player.getCooldownTracker().setCooldown(this, def.cooldown); }
            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }
        if (!world.isRemote && player instanceof EntityPlayerMP) {
            if (!def.locates.isEmpty()) { ContentCompass.use((EntityPlayerMP) player, hand, def.locator); }
            else if (def.passesTurn) { ContentTurns.byItem((EntityPlayerMP) player); }
            else { ContentDice.byItem((EntityPlayerMP) player, def.rolls, def.rollStore); }
        }
        if (usedToCooldown(world, player, hand) && def.cooldown > 0) { player.getCooldownTracker().setCooldown(this, def.cooldown); }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    private boolean usedToCooldown(World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) { return true; }
        if (!def.runs.isEmpty()) { Functions.runAs(player, def.runs, "The item " + def.registryName); }
        if (def.script == null || !(world instanceof WorldServer)) { return true; }
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("rdpl", new RdplApi((WorldServer) world, player));
        bindings.put("item", player.getHeldItem(hand));
        bindings.put("hand", hand.name());
        return !"cancel".equals(ScriptEngine.run(def.script, bindings));
    }
}

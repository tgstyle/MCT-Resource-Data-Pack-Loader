package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.command.ServerCommands;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeSearch;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import javax.annotation.Nullable;

public final class ContentCompass {
    static final String TAG = "RdplLocator";
    static final String X = "X";
    static final String Z = "Z";
    static final String DIMENSION = "Dimension";

    private ContentCompass() {}

    public static void use(EntityPlayerMP player, EnumHand hand, String place) {
        if (ContentStructureSearch.looking() || ContentBiomeSearch.looking()) {
            tell(player, TextFormatting.RED, Lang.tr(player, "rdpl.command.gotobusy"));
            return;
        }
        Item item = player.getHeldItem(hand).getItem();
        ServerCommands.point(player, place, found -> deliver(player, item, place, found));
    }

    private static void deliver(EntityPlayerMP player, Item item, String place, @Nullable BlockPos found) {
        if (found == null) {
            tell(player, TextFormatting.RED, Lang.tr(player, "rdpl.command.gotonothing", place));
            return;
        }
        ItemStack stack = held(player, item);
        if (stack.isEmpty()) { return; }
        NBTTagCompound target = stack.getOrCreateSubCompound(TAG);
        target.setInteger(X, found.getX());
        target.setInteger(Z, found.getZ());
        target.setInteger(DIMENSION, player.dimension);
        tell(player, TextFormatting.GREEN, Lang.tr(player, "rdpl.compass.set", place, found.getX(), found.getZ()));
    }

    private static ItemStack held(EntityPlayerMP player, Item item) {
        for (EnumHand hand : EnumHand.values()) {
            if (player.getHeldItem(hand).getItem() == item) { return player.getHeldItem(hand); }
        }
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack.getItem() == item) { return stack; }
        }
        return ItemStack.EMPTY;
    }

    private static void tell(EntityPlayerMP player, TextFormatting color, String message) { player.sendMessage(new TextComponentString(color + message)); }
}

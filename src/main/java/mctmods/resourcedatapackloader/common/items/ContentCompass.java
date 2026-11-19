package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.command.ServerCommands;
import mctmods.resourcedatapackloader.common.locate.ContentBiomeSearch;
import mctmods.resourcedatapackloader.common.locate.ContentStructureSearch;
import mctmods.resourcedatapackloader.common.pack.definitions.items.LocatorDef;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import javax.annotation.Nullable;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public final class ContentCompass {
    static final String TAG = "RdplLocator";
    static final String X = "X";
    static final String Z = "Z";
    static final String DIMENSION = "Dimension";

    private ContentCompass() {}

    public static void use(EntityPlayerMP player, EnumHand hand, LocatorDef locator) {
        if (ContentStructureSearch.looking() || ContentBiomeSearch.looking()) {
            tell(player, TextFormatting.RED, Lang.tr(player, "rdpl.command.gotobusy"));
            return;
        }
        Item item = player.getHeldItem(hand).getItem();
        if (!locator.sound.isEmpty()) { player.connection.sendPacket(new SPacketCustomSound(locator.sound, SoundCategory.PLAYERS, player.posX, player.posY, player.posZ, 1.0F, 1.0F)); }
        ServerCommands.point(player, locator.place, found -> deliver(player, item, locator, found));
    }

    private static boolean beyond(EntityPlayerMP player, LocatorDef locator, BlockPos found) {
        if (locator.range <= 0) { return false; }
        double away = found.getX() + 0.5D - player.posX;
        double along = found.getZ() + 0.5D - player.posZ;
        return away * away + along * along > (double) locator.range * locator.range;
    }

    private static void deliver(EntityPlayerMP player, Item item, LocatorDef locator, @Nullable BlockPos found) {
        String place = locator.place;
        if (found == null || beyond(player, locator, found)) {
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
        if (locator.eye) { throwEye(player, found); }
    }

    private static void throwEye(EntityPlayerMP player, BlockPos found) {
        EntityEnderEye eye = new EntityEnderEye(player.world, player.posX, player.posY + player.height / 2.0F, player.posZ);
        eye.moveTowards(found);
        player.world.spawnEntity(eye);
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

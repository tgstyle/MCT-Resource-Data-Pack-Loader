package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.util.world.SavedData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentDicePile extends WorldSavedData {
    private static final String NAME = "rdpl_dice";
    private static final String TAG = "rdplDecks";
    private NBTTagCompound piles = new NBTTagCompound();

    public ContentDicePile(String name) { super(name); }

    static int draw(World world, String deck, int size, Random random) {
        ContentDicePile data = get(world);
        List<Integer> pile = pile(data, deck, size);
        if (pile.isEmpty()) { return -1; }
        int card = pile.remove(random.nextInt(pile.size()));
        keep(data, deck, pile);
        return card;
    }

    static void shuffle(World world, String deck) {
        ContentDicePile data = get(world);
        if (data == null) { return; }
        data.piles.removeTag(deck);
        data.markDirty();
    }

    static int left(World world, String deck, int size) { return pile(get(world), deck, size).size(); }

    private static List<Integer> pile(@Nullable ContentDicePile data, String deck, int size) {
        List<Integer> pile = new ArrayList<>();
        if (data == null || !data.piles.hasKey(deck)) {
            for (int card = 0; card < size; card++) { pile.add(card); }
            return pile;
        }
        for (int card : data.piles.getIntArray(deck)) {
            if (card < size) { pile.add(card); }
        }
        return pile;
    }

    private static void keep(@Nullable ContentDicePile data, String deck, List<Integer> pile) {
        if (data == null) { return; }
        int[] held = new int[pile.size()];
        for (int at = 0; at < held.length; at++) { held[at] = pile.get(at); }
        data.piles.setIntArray(deck, held);
        data.markDirty();
    }

    @Nullable private static ContentDicePile get(World world) {
        MapStorage storage = world.getMapStorage();
        if (storage == null) { return null; }
        return SavedData.get(storage, ContentDicePile.class, NAME, ContentDicePile::new);
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) { piles = compound.getCompoundTag(TAG); }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setTag(TAG, piles);
        return compound;
    }
}

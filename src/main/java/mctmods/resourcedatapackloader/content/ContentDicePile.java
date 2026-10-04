package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.ArrayList;
import java.util.List;

public final class ContentDicePile extends SavedData {
    private static final String NAME = "rdpl_dice";
    private static final String TAG = "rdplDecks";
    private static final SavedDataType<ContentDicePile> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, NAME), ContentDicePile::new, CompoundTag.CODEC.xmap(ContentDicePile::read, ContentDicePile::write));
    private CompoundTag piles = new CompoundTag();

    private static ContentDicePile of(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(TYPE); }

    private static ContentDicePile read(CompoundTag tag) {
        ContentDicePile held = new ContentDicePile();
        held.piles = tag.getCompoundOrEmpty(TAG).copy();
        return held;
    }

    private CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.put(TAG, piles.copy());
        return tag;
    }

    static int draw(MinecraftServer server, String deck, int size, RandomSource random) {
        ContentDicePile data = of(server);
        List<Integer> pile = pile(data, deck, size);
        if (pile.isEmpty()) { return -1; }
        int card = pile.remove(random.nextInt(pile.size()));
        data.piles.putIntArray(deck, pile.stream().mapToInt(Integer::intValue).toArray());
        data.setDirty();
        return card;
    }

    static void shuffle(MinecraftServer server, String deck) {
        ContentDicePile data = of(server);
        data.piles.remove(deck);
        data.setDirty();
    }

    static int left(MinecraftServer server, String deck, int size) { return pile(of(server), deck, size).size(); }

    private static List<Integer> pile(ContentDicePile data, String deck, int size) {
        List<Integer> pile = new ArrayList<>();
        if (!data.piles.contains(deck)) {
            for (int card = 0; card < size; card++) { pile.add(card); }
            return pile;
        }
        for (int card : data.piles.getIntArray(deck).orElse(new int[0])) {
            if (card < size) { pile.add(card); }
        }
        return pile;
    }
}

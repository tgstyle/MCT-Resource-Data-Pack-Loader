package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ContentSites extends WorldSavedData {
    private static final String NAME = "RDPLSites";
    private final Map<Long, Long> chosen = new HashMap<>();
    private int spacing;

    public ContentSites() { super(NAME); }

    @SuppressWarnings("unused") public ContentSites(String name) { super(name); }

    public static ContentSites of(World world, int spacing) {
        ContentSites held = (ContentSites) world.getPerWorldStorage().getOrLoadData(ContentSites.class, NAME);
        if (held == null) {
            held = new ContentSites();
            world.getPerWorldStorage().setData(NAME, held);
        }
        if (held.spacing != spacing) {
            held.chosen.clear();
            held.spacing = spacing;
            held.markDirty();
        }
        return held;
    }

    public Long get(long cell) { return chosen.get(cell); }

    public void put(long cell, long site) {
        chosen.put(cell, site);
        markDirty();
    }

    @Override public void readFromNBT(NBTTagCompound nbt) {
        chosen.clear();
        spacing = nbt.getInteger("Spacing");
        int[] packed = nbt.getIntArray("Sites");
        for (int i = 0; i + 3 < packed.length; i += 4) { chosen.put(((long) packed[i] << 32) | (packed[i + 1] & 0xFFFFFFFFL), ((long) packed[i + 2] << 32) | (packed[i + 3] & 0xFFFFFFFFL)); }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
        int[] packed = new int[chosen.size() * 4];
        int at = 0;
        for (Map.Entry<Long, Long> entry : chosen.entrySet()) {
            long cell = entry.getKey();
            long site = entry.getValue();
            packed[at++] = (int) (cell >> 32);
            packed[at++] = (int) cell;
            packed[at++] = (int) (site >> 32);
            packed[at++] = (int) site;
        }
        nbt.setInteger("Spacing", spacing);
        nbt.setIntArray("Sites", packed);
        return nbt;
    }
}

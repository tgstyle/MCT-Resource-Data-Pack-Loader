package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class ContentSites extends WorldSavedData {
    private static final String NAME = "RDPLSites";
    private final Map<Long, Long> chosen = new HashMap<>();
    private int spacing;
    private boolean warned;

    public ContentSites() { super(NAME); }

    @SuppressWarnings("unused") public ContentSites(String name) { super(name); }

    public static ContentSites of(World world, int spacing) {
        ContentSites held = (ContentSites) world.getPerWorldStorage().getOrLoadData(ContentSites.class, NAME);
        if (held == null) {
            held = new ContentSites();
            world.getPerWorldStorage().setData(NAME, held);
        }
        int wanted = Math.max(9, spacing);
        if (held.spacing == 0) {
            held.spacing = wanted;
            held.markDirty();
            return held;
        }
        int stated = ContentStructurePlacement.spacing(ContentStructurePlacement.VILLAGES, -1);
        if (stated > 0 && Math.max(9, stated) != held.spacing) {
            ContentLog.LOGGER.info("The pack now asks for villages every {} chunk(s) where this world founded on every {}, so the founding record starts over on the new grid", Math.max(9, stated), held.spacing);
            held.chosen.clear();
            held.spacing = Math.max(9, stated);
            held.markDirty();
            return held;
        }
        if (wanted != held.spacing && !held.warned) {
            held.warned = true;
            ContentLog.LOGGER.debug("Village founding was asked with spacing {} while this world founded on every {} chunk(s). Another mod changed a village generator's distance in flight, so the world's own grid is used and the ask is ignored", wanted, held.spacing);
        }
        return held;
    }

    public int spacing() { return spacing; }

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

package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import java.util.IllegalFormatException;

public final class ContentPregenProgress {
    static final Config.Chunks SHIPPED = new Config.Chunks();

    public static String says(String key, String fallback) { return ContentControl.text(ContentControl.CHUNKS, key, fallback).trim(); }

    static String defaulted(String key, String fallback, String shipped, String langKey, EntityPlayerMP player) {
        String said = says(key, fallback);
        if (!said.equals(shipped.trim())) { return said; }
        return player == null ? Lang.tr(langKey) : Lang.tr(player, langKey);
    }

    static String sofar(ContentPregen worker) {
        if (!worker.lightOnly && worker.made == 0L && worker.done > 0L && worker.order.hasNext()) { return checking(worker); }
        String wording = worker.lightOnly ? defaulted("pregenRelightSays", Config.chunks.pregenRelightSays, SHIPPED.pregenRelightSays, "rdpl.pregen.relight", null) : defaulted("pregenRunningSays", Config.chunks.pregenRunningSays, SHIPPED.pregenRunningSays, "rdpl.pregen.running", null);
        if (wording.isEmpty()) { return ""; }
        long stepped = Math.min(100L, worker.done * 100L / Math.max(1L, worker.order.total()));
        try { return String.format(wording, worker.order.hasNext() ? stepped : 100L, dimensionName(worker)) + eta(worker); }
        catch (IllegalFormatException wrong) {
            ContentLog.LOGGER.error("A pack words the message about land being made as '{}', which is not something a number can be put into, so it is said as it stands", wording, wrong);
            return wording + eta(worker);
        }
    }

    static String checking(ContentPregen worker) {
        long stepped = Math.min(100L, worker.done * 100L / Math.max(1L, worker.order.total()));
        long spent = worker.begun == 0L ? 0L : (System.currentTimeMillis() - worker.begun) / 1000L;
        return Lang.tr("rdpl.pregen.checking", stepped) + Lang.tr("rdpl.pregen.checked", spent / 3600L, spent / 60L % 60L, spent % 60L);
    }

    static String eta(ContentPregen worker) {
        long total = worker.order.total();
        if (worker.begun == 0L || worker.done <= worker.resumedFrom || worker.done >= total) { return ""; }
        long metric = worker.lightOnly ? worker.done : worker.made;
        long now = System.currentTimeMillis();
        if (worker.etaFigured == 0L || now - worker.etaFigured >= 5000L) {
            if (worker.etaAt != 0L && now > worker.etaAt && metric > worker.etaMade) {
                double lately = (metric - worker.etaMade) * 1000.0D / (now - worker.etaAt);
                worker.etaRate = worker.etaRate == 0.0D ? lately : worker.etaRate * 0.9D + lately * 0.1D;
            }
            worker.etaAt = now;
            worker.etaMade = metric;
            worker.etaFigured = now;
            if (worker.etaRate > 0.0D) { worker.etaSeconds = (long) ((total - worker.done) / worker.etaRate); }
        }
        if (worker.etaRate <= 0.0D) { return ""; }
        long left = Math.max(0L, worker.etaSeconds - (now - worker.etaFigured) / 1000L);
        return Lang.tr("rdpl.pregen.eta", left / 3600L, left / 60L % 60L, left % 60L);
    }

    static String dimensionName(ContentPregen worker) {
        WorldServer world = DimensionManager.getWorld(worker.dimension);
        return world == null ? String.valueOf(worker.dimension) : world.provider.getDimensionType().getName();
    }

    static String report(ContentPregen worker) {
        long seconds = Math.max(1L, (System.currentTimeMillis() - worker.started) / 1000L);
        long rate = (worker.done - worker.resumedFrom) / seconds;
        boolean rubic = worker.rubicRun;
        if (worker.lightOnly) {
            if (rubic) {
                return String.format("Went over %d of %d column(s) in dimension %d, all of them lit and dressed cube by cube as they were made", worker.done, worker.order.total(), worker.dimension);
            }
            return String.format("Went over %d of %d chunk(s) in dimension %d looking for ones the light never reached, lighting %d and leaving %d, with %d never made in the first place%s",
                    worker.done, worker.order.total(), worker.dimension, worker.brightened, worker.dark + worker.darkAtEdge, worker.missing,
                    worker.undressed == 0L ? "" : ", and " + worker.undressed + " of them had never been dressed, " + worker.dressedLate + " of which were dressed on the spot and the rest left alone to be dressed when somebody comes to them");
        }
        if (rubic) {
            return String.format("Made %d of %d column(s) in dimension %d, %d of them new, at %d a second, holding %d and resting %d time(s) for the writing to catch up. Every cube was lit and dressed as it was made, keeping within %d ms a round",
                    worker.done, worker.order.total(), worker.dimension, worker.made, rate, worker.resident.size(), worker.paused, worker.slice);
        }
        return String.format("Made %d of %d chunk(s) in dimension %d, %d of them new, at %d a second, holding %d and resting %d time(s) for the writing to catch up. Light reached %d of them, %d were left for later and %d could never be lit, having been asked for at the very edge of what was wanted, keeping within %d ms a round",
                worker.done, worker.order.total(), worker.dimension, worker.made, rate, worker.resident.size(), worker.paused, worker.brightened, worker.dark, worker.darkAtEdge, worker.slice);
    }
}

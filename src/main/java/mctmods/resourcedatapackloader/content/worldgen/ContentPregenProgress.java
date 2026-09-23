package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.server.level.ServerPlayer;
import java.util.IllegalFormatException;
import javax.annotation.Nullable;

public final class ContentPregenProgress {
    private ContentPregenProgress() {}

    public static String says(String key, String fallback) { return ContentControl.text(ContentControl.CHUNKS, key, fallback).trim(); }

    static String defaulted(String key, String fallback, String shipped, String langKey, @Nullable ServerPlayer player) {
        String said = says(key, fallback);
        if (!said.equals(shipped.trim())) { return said; }
        return player == null ? Lang.tr(langKey) : Lang.tr(player, langKey);
    }

    static String sofar(ContentPregen worker) {
        if (worker.fresh == 0L && worker.done > 0L && (worker.order.hasNext() || !worker.flying.isEmpty())) { return checking(worker); }
        String wording = defaulted("pregenRunningSays", Config.chunks.pregenRunningSays(), Config.PREGEN_RUNNING, "rdpl.pregen.running", null);
        if (wording.isEmpty()) { return ""; }
        long stepped = Math.min(100L, worker.done * 100L / Math.max(1L, worker.order.total()));
        try { return String.format(wording, worker.order.hasNext() || !worker.flying.isEmpty() ? stepped : 100L, worker.dimension.location().getPath()) + eta(worker); }
        catch (IllegalFormatException wrong) {
            ContentLog.LOGGER.error("A pack words the message about land being made as '{}', which is not something a number can be put into, so it is said as it stands", wording, wrong);
            return wording + eta(worker);
        }
    }

    private static String checking(ContentPregen worker) {
        long stepped = Math.min(100L, worker.done * 100L / Math.max(1L, worker.order.total()));
        long spent = worker.begun == 0L ? 0L : (System.currentTimeMillis() - worker.begun) / 1000L;
        return Lang.tr("rdpl.pregen.checking", stepped) + Lang.tr("rdpl.pregen.checked", spent / 3600L, spent / 60L % 60L, spent % 60L);
    }

    private static String eta(ContentPregen worker) {
        long total = worker.order.total();
        if (worker.begun == 0L || worker.done <= worker.resumedFrom || worker.done >= total) { return ""; }
        long now = System.currentTimeMillis();
        if (worker.etaFigured == 0L || now - worker.etaFigured >= 5000L) {
            if (worker.etaAt != 0L && now > worker.etaAt && worker.done > worker.etaMade) {
                double lately = (worker.done - worker.etaMade) * 1000.0D / (now - worker.etaAt);
                worker.etaRate = worker.etaRate == 0.0D ? lately : worker.etaRate * 0.9D + lately * 0.1D;
            }
            worker.etaAt = now;
            worker.etaMade = worker.done;
            worker.etaFigured = now;
            if (worker.etaRate > 0.0D) { worker.etaSeconds = (long) ((total - worker.done) / Math.min(worker.etaRate, (worker.done - worker.resumedFrom) * 1000.0D / Math.max(1L, now - worker.begun))); }
        }
        if (worker.etaRate <= 0.0D) { return ""; }
        long left = Math.max(0L, worker.etaSeconds - (now - worker.etaFigured) / 1000L);
        return Lang.tr("rdpl.pregen.eta", left / 3600L, left / 60L % 60L, left % 60L);
    }

    static String report(ContentPregen worker) {
        long seconds = Math.max(1L, (System.currentTimeMillis() - worker.started) / 1000L);
        long rate = (worker.done - worker.resumedFrom) / seconds;
        return String.format("Made %d of %d chunk(s) in %s, %d of them new or loaded, %d refused, at %d a second with %d asked for at once", worker.done, worker.order.total(), worker.dimension.location(), worker.made, worker.failed, rate, worker.inFlight);
    }
}

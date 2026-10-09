package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Blocked;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.TemplateMemo;

import java.util.Collection;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;

final class ModBlocking {
    private final Blocked counts = new Blocked();
    private final TemplateMemo<Set<String>> whitelistMemo = new TemplateMemo<>();
    private final TemplateMemo<Set<String>> blockedMemo = new TemplateMemo<>();
    private final String whitelistKey;
    private final Supplier<String[]> whitelistDefault;
    private final String blockedKey;
    private final Supplier<String[]> blockedDefault;
    private final String flagKey;
    private final BooleanSupplier flagDefault;
    private final String reportKey;
    private final String what;

    ModBlocking(String whitelistKey, Supplier<String[]> whitelistDefault, String blockedKey, Supplier<String[]> blockedDefault, String flagKey, BooleanSupplier flagDefault, String reportKey, String what) {
        this.whitelistKey = whitelistKey;
        this.whitelistDefault = whitelistDefault;
        this.blockedKey = blockedKey;
        this.blockedDefault = blockedDefault;
        this.flagKey = flagKey;
        this.flagDefault = flagDefault;
        this.reportKey = reportKey;
        this.what = what;
    }

    void reset() {
        counts.clear();
        whitelistMemo.forget();
        blockedMemo.forget();
    }

    boolean disabled() {
        if (ContentControl.off(ContentControl.RECIPES)) { return true; }
        return listedOnly() && blocked().isEmpty();
    }

    boolean blocks(Collection<String> owners) {
        String reason = reason(owners);
        if (reason == null) { return false; }
        counts.count(reason);
        return true;
    }

    void report(String detail) {
        int total = counts.total();
        if (total == 0) { return; }
        Summary.info(reportKey, "Blocked " + total + " " + what + detail);
        if (ContentControl.flag(ContentControl.RECIPES, "logBlockedRecipes", Config.recipes.logBlockedRecipes)) { counts.report(what); }
    }

    private boolean listedOnly() { return !ContentControl.flag(ContentControl.RECIPES, flagKey, flagDefault.getAsBoolean()); }

    private Set<String> blocked() { return blockedMemo.get(() -> Settings.lower(ContentControl.list(ContentControl.RECIPES, blockedKey, blockedDefault.get()))); }

    private Set<String> whitelist() { return whitelistMemo.get(() -> Settings.lower(ContentControl.list(ContentControl.RECIPES, whitelistKey, whitelistDefault.get()))); }

    @Nullable private String reason(Collection<String> owners) {
        Set<String> blocked = blocked();
        for (String owner : owners) {
            if (blocked.contains(owner)) { return owner; }
        }
        if (listedOnly()) { return null; }
        Set<String> whitelist = whitelist();
        for (String owner : owners) {
            if (whitelist.contains(owner)) { return null; }
        }
        return owners.isEmpty() ? null : owners.iterator().next();
    }
}

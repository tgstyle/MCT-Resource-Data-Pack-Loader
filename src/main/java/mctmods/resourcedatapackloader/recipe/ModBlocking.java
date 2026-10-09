package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Blocked;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;

final class ModBlocking {
    private final Blocked counts = new Blocked();
    private final String whitelistKey;
    private final String blockedKey;
    private final String flagKey;
    private final String reportKey;
    private final String what;
    private Set<String> whitelist = Collections.emptySet();
    private Set<String> blocked = Collections.emptySet();
    private boolean blockAll;
    private boolean off;

    ModBlocking(String whitelistKey, String blockedKey, String flagKey, String reportKey, String what) {
        this.whitelistKey = whitelistKey;
        this.blockedKey = blockedKey;
        this.flagKey = flagKey;
        this.reportKey = reportKey;
        this.what = what;
    }

    void reload(List<String> whitelistDefault, List<String> blockedDefault, boolean blockAllDefault) {
        counts.clear();
        off = ContentControl.off(ContentControl.RECIPES);
        whitelist = Settings.lower(ContentControl.list(ContentControl.RECIPES, whitelistKey, whitelistDefault));
        blocked = Settings.lower(ContentControl.list(ContentControl.RECIPES, blockedKey, blockedDefault));
        blockAll = ContentControl.flag(ContentControl.RECIPES, flagKey, blockAllDefault);
    }

    boolean blocks(Supplier<? extends Collection<String>> owners) {
        if (off || (!blockAll && blocked.isEmpty())) { return false; }
        String reason = reason(owners.get());
        if (reason == null) { return false; }
        counts.count(reason);
        return true;
    }

    void report() {
        int total = counts.total();
        if (total == 0) { return; }
        Summary.info(reportKey, "Blocked " + total + " " + what);
        if (ContentControl.flag(ContentControl.RECIPES, "logBlockedRecipes", Config.recipes.logBlockedRecipes())) { counts.report(what); }
    }

    @Nullable private String reason(Collection<String> owners) {
        for (String owner : owners) {
            if (blocked.contains(owner)) { return owner; }
        }
        if (!blockAll) { return null; }
        for (String owner : owners) {
            if (whitelist.contains(owner)) { return null; }
        }
        return owners.isEmpty() ? null : owners.iterator().next();
    }
}

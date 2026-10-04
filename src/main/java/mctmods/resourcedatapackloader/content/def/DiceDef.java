package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DiceDef {
    public final String audience;
    public final Map<String, String> says;
    public final Map<String, Map<String, Integer>> dice;
    public final Map<String, List<String>> decks;
    public final Set<String> fixed;

    public DiceDef(String audience, Map<String, String> says, Map<String, Map<String, Integer>> dice, Map<String, List<String>> decks, Set<String> fixed) {
        this.audience = audience;
        this.says = says;
        this.dice = dice;
        this.decks = decks;
        this.fixed = fixed;
    }
}

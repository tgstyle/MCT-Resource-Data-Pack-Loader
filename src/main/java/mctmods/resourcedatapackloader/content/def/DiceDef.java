package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record DiceDef(String audience, Map<String, String> says, Map<String, Map<String, Integer>> dice, Map<String, List<String>> decks, Set<String> fixed) {}

package mctmods.resourcedatapackloader.pack.port;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

final class CommandStacks {
    private static final Pattern DAMAGE = Pattern.compile("Damage:(\\d+)[bsil]?");

    private CommandStacks() {}

    static final class Stack {
        final String id;
        final int meta;
        @Nullable final String nbt;

        Stack(String id, int meta, @Nullable String nbt) {
            this.id = id;
            this.meta = meta;
            this.nbt = nbt;
        }
    }

    static Stack item(String token, Ported pack) {
        String name = token;
        String nbt = null;
        int brace = token.indexOf('{');
        int bracket = token.indexOf('[');
        int meta = 0;
        if (bracket > 0 && (brace < 0 || bracket < brace)) {
            int close = token.lastIndexOf(']');
            name = token.substring(0, bracket);
            String components = token.substring(bracket + 1, close);
            if (close + 1 < token.length()) { nbt = token.substring(close + 1); }
            for (String component : CommandSelectors.splitTop(components)) {
                String key = component.indexOf('=') < 0 ? component : component.substring(0, component.indexOf('='));
                String value = component.indexOf('=') < 0 ? "" : component.substring(component.indexOf('=') + 1);
                if ("damage".equals(key) || "minecraft:damage".equals(key)) { meta = CommandSelectors.whole(value); }
                else if ("custom_data".equals(key) || "minecraft:custom_data".equals(key)) { nbt = value; }
                else if (!key.isEmpty()) { throw new Commands.Kept("the item component " + key + " has no twin on 1.12.2"); }
            }
        }
        else if (brace > 0) {
            name = token.substring(0, brace);
            nbt = token.substring(brace);
            Matcher damage = DAMAGE.matcher(nbt);
            if (damage.find()) { meta = Integer.parseInt(damage.group(1)); }
        }
        if (name.startsWith("#")) { throw new Commands.Kept("an item tag has no twin in a 1.12.2 command"); }
        Convert.Ref ref = Convert.item(name, pack, "a function");
        if (ref == null) { return new Stack(name, meta, nbt); }
        return new Stack(ref.id, meta > 0 ? meta : ref.meta, nbt);
    }

    static Stack block(String token, Ported pack) {
        String name = token;
        String nbt = null;
        int brace = token.indexOf('{');
        if (brace > 0) {
            name = token.substring(0, brace);
            nbt = token.substring(brace);
        }
        if (name.startsWith("#")) { throw new Commands.Kept("a block tag has no twin in a 1.12.2 command"); }
        Convert.Ref ref = Convert.block(name, Collections.emptyMap(), pack, "a function");
        if (ref == null) { throw new Commands.Kept("'" + name + "' is not a block"); }
        if (nbt != null && "minecraft:mob_spawner".equals(ref.id)) {
            try {
                Object parsed = Snbt.parse(nbt);
                Snbt.spawner(parsed);
                nbt = Snbt.write(parsed);
            }
            catch (IllegalArgumentException broken) { throw new Commands.Kept("its spawner nbt does not parse: " + broken.getMessage()); }
        }
        return new Stack(ref.id, ref.meta, nbt);
    }
}

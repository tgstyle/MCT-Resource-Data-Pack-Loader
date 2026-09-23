package mctmods.resourcedatapackloader.pack.port;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

final class Structures {
    static final int LEGACY_DATA_VERSION = 1343;
    private static final byte END = 0;
    private static final byte BYTE = 1;
    private static final byte SHORT = 2;
    private static final byte INT = 3;
    private static final byte LONG = 4;
    private static final byte FLOAT = 5;
    private static final byte DOUBLE = 6;
    private static final byte BYTE_ARRAY = 7;
    private static final byte STRING = 8;
    private static final byte LIST = 9;
    private static final byte COMPOUND = 10;
    private static final byte INT_ARRAY = 11;
    private static final byte LONG_ARRAY = 12;

    private Structures() {}

    static final class Tag {
        final byte type;
        Object value;

        Tag(byte type, Object value) {
            this.type = type;
            this.value = value;
        }

        @SuppressWarnings("unchecked") Map<String, Tag> compound() { return (Map<String, Tag>) value; }

        List<Tag> list() { return ((Listed) value).items; }

        String string() { return (String) value; }

        int number() { return ((Number) value).intValue(); }
    }

    static final class Listed {
        byte element;
        final List<Tag> items;

        Listed(byte element, List<Tag> items) {
            this.element = element;
            this.items = items;
        }
    }

    static byte[] convert(byte[] raw, String from, Ported pack) {
        try {
            Tag root;
            try (DataInputStream in = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(raw)))) {
                byte type = in.readByte();
                in.readUTF();
                root = read(in, type);
            }
            Map<String, Tag> structure = root.compound();
            Tag version = structure.get("DataVersion");
            if (version != null && version.number() <= LEGACY_DATA_VERSION) { return raw; }
            structure.put("DataVersion", new Tag(INT, LEGACY_DATA_VERSION));
            Tag palettes = structure.remove("palettes");
            if (!structure.containsKey("palette") && palettes != null && !palettes.list().isEmpty()) {
                structure.put("palette", palettes.list().get(0));
                pack.note("'" + from + "' holds " + palettes.list().size() + " palettes, and a 1.12.2 structure holds one, so the first is kept");
            }
            Set<String> lost = new LinkedHashSet<>();
            Tag palette = structure.get("palette");
            if (palette != null) {
                for (Tag entry : palette.list()) { state(entry.compound(), pack, lost); }
            }
            Tag blocks = structure.get("blocks");
            if (blocks != null) {
                for (Tag block : blocks.list()) {
                    Tag nbt = block.compound().get("nbt");
                    if (nbt == null || nbt.type != COMPOUND) { continue; }
                    spawner(nbt.compound());
                    items(nbt, pack, from);
                }
            }
            Tag entities = structure.get("entities");
            if (entities != null) {
                for (Tag entity : entities.list()) {
                    Tag nbt = entity.compound().get("nbt");
                    if (nbt == null) { continue; }
                    Tag id = nbt.compound().get("id");
                    if (id != null && id.type == STRING && Ids.vanilla(id.string())) { id.value = Ids.entity(id.string()); }
                    items(nbt, pack, from);
                }
            }
            if (!lost.isEmpty()) { pack.note("'" + from + "' places " + String.join(", ", lost) + ", which have no twin on 1.12.2 and are left as written, so 1.12.2 places air there"); }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(bytes))) {
                out.writeByte(COMPOUND);
                out.writeUTF("");
                write(out, root);
            }
            return bytes.toByteArray();
        }
        catch (IOException broken) { throw new UncheckedIOException(broken); }
    }

    private static void state(Map<String, Tag> entry, Ported pack, Set<String> lost) {
        Tag name = entry.get("Name");
        if (name == null || name.type != STRING) { return; }
        Map<String, String> properties = new LinkedHashMap<>();
        Tag held = entry.get("Properties");
        if (held != null && held.type == COMPOUND) {
            for (Map.Entry<String, Tag> property : held.compound().entrySet()) { properties.put(property.getKey(), String.valueOf(property.getValue().value)); }
        }
        String modern = Ids.namespaced(name.string());
        Ported.Own own = pack.ownBlock(modern);
        if (own != null) {
            name.value = "slab".equals(own.type) && "double".equals(properties.get("type")) ? own.id() + "_double" : own.id();
            String half = properties.remove("type");
            if ("slab".equals(own.type) && half != null && !"double".equals(half)) { properties.put("half", half); }
            if (own.variantProperty()) { properties.put("blocks", own.variant); }
            entry.put("Properties", strings(properties));
            return;
        }
        if (!Ids.vanilla(modern)) { return; }
        Ids.Legacy found = Ids.block(modern, properties);
        if (found == null) {
            if (Ids.unknownBlock(modern)) { lost.add(modern); }
            return;
        }
        name.value = found.id;
        if (found.legacyProperties.isEmpty()) { entry.remove("Properties"); }
        else { entry.put("Properties", strings(found.legacyProperties)); }
    }

    private static void spawner(Map<String, Tag> nbt) {
        Tag data = nbt.get("SpawnData");
        if (data != null && data.type == COMPOUND && data.compound().containsKey("entity")) { nbt.put("SpawnData", spawnedEntity(data.compound().get("entity"))); }
        Tag potentials = nbt.get("SpawnPotentials");
        if (potentials == null || potentials.type != LIST) { return; }
        for (Tag entry : potentials.list()) {
            if (entry.type != COMPOUND) { continue; }
            Map<String, Tag> held = entry.compound();
            Tag inner = held.remove("data");
            Tag weight = held.remove("weight");
            if (inner != null && inner.type == COMPOUND && inner.compound().containsKey("entity")) { held.put("Entity", spawnedEntity(inner.compound().get("entity"))); }
            if (weight != null) { held.put("Weight", new Tag(INT, weight.number())); }
        }
    }

    private static Tag spawnedEntity(Tag entity) {
        Tag id = entity.type == COMPOUND ? entity.compound().get("id") : null;
        if (id != null && id.type == STRING && Ids.vanilla(id.string())) { id.value = Ids.entity(id.string()); }
        return entity;
    }

    private static Tag strings(Map<String, String> values) {
        Map<String, Tag> out = new LinkedHashMap<>();
        for (Map.Entry<String, String> value : values.entrySet()) { out.put(value.getKey(), new Tag(STRING, value.getValue())); }
        return new Tag(COMPOUND, out);
    }

    private static void items(Tag holder, Ported pack, String from) {
        if (holder.type == LIST) {
            for (Tag inner : holder.list()) { items(inner, pack, from); }
            return;
        }
        if (holder.type != COMPOUND) { return; }
        Map<String, Tag> compound = holder.compound();
        Tag id = compound.get("id");
        Tag count = compound.containsKey("Count") ? compound.get("Count") : compound.get("count");
        if (id != null && id.type == STRING && count != null) {
            Convert.Ref ref = Convert.item(id.string(), pack, "'" + from + "'");
            if (ref != null) {
                id.value = ref.id;
                compound.remove("count");
                compound.put("Count", new Tag(BYTE, (byte) count.number()));
                if (!compound.containsKey("Damage")) { compound.put("Damage", new Tag(SHORT, (short) ref.meta)); }
                if (compound.remove("components") != null) { pack.note("'" + from + "' holds an item with components, which have no twin on 1.12.2, so the plain item is kept"); }
            }
        }
        for (Tag inner : new ArrayList<>(compound.values())) { items(inner, pack, from); }
    }

    private static Tag read(DataInputStream in, byte type) throws IOException {
        switch (type) {
            case BYTE: return new Tag(type, in.readByte());
            case SHORT: return new Tag(type, in.readShort());
            case INT: return new Tag(type, in.readInt());
            case LONG: return new Tag(type, in.readLong());
            case FLOAT: return new Tag(type, in.readFloat());
            case DOUBLE: return new Tag(type, in.readDouble());
            case BYTE_ARRAY: {
                byte[] held = new byte[in.readInt()];
                in.readFully(held);
                return new Tag(type, held);
            }
            case STRING: return new Tag(type, in.readUTF());
            case LIST: {
                byte element = in.readByte();
                int size = in.readInt();
                List<Tag> items = new ArrayList<>(Math.max(0, size));
                for (int i = 0; i < size; i++) { items.add(read(in, element)); }
                return new Tag(type, new Listed(element, items));
            }
            case COMPOUND: {
                Map<String, Tag> values = new LinkedHashMap<>();
                for (byte inner = in.readByte(); inner != END; inner = in.readByte()) { values.put(in.readUTF(), read(in, inner)); }
                return new Tag(type, values);
            }
            case INT_ARRAY: {
                int[] held = new int[in.readInt()];
                for (int i = 0; i < held.length; i++) { held[i] = in.readInt(); }
                return new Tag(type, held);
            }
            case LONG_ARRAY: {
                long[] held = new long[in.readInt()];
                for (int i = 0; i < held.length; i++) { held[i] = in.readLong(); }
                return new Tag(type, held);
            }
            default: throw new IOException("unknown nbt tag type " + type);
        }
    }

    private static void write(DataOutputStream out, Tag tag) throws IOException {
        switch (tag.type) {
            case BYTE: out.writeByte((Byte) tag.value); break;
            case SHORT: out.writeShort((Short) tag.value); break;
            case INT: out.writeInt((Integer) tag.value); break;
            case LONG: out.writeLong((Long) tag.value); break;
            case FLOAT: out.writeFloat((Float) tag.value); break;
            case DOUBLE: out.writeDouble((Double) tag.value); break;
            case BYTE_ARRAY: {
                byte[] held = (byte[]) tag.value;
                out.writeInt(held.length);
                out.write(held);
                break;
            }
            case STRING: out.writeUTF(tag.string()); break;
            case LIST: {
                Listed listed = (Listed) tag.value;
                byte element = listed.items.isEmpty() ? listed.element : listed.items.get(0).type;
                out.writeByte(element);
                out.writeInt(listed.items.size());
                for (Tag item : listed.items) { write(out, item); }
                break;
            }
            case COMPOUND: {
                for (Map.Entry<String, Tag> value : tag.compound().entrySet()) {
                    out.writeByte(value.getValue().type);
                    out.writeUTF(value.getKey());
                    write(out, value.getValue());
                }
                out.writeByte(END);
                break;
            }
            case INT_ARRAY: {
                int[] held = (int[]) tag.value;
                out.writeInt(held.length);
                for (int value : held) { out.writeInt(value); }
                break;
            }
            case LONG_ARRAY: {
                long[] held = (long[]) tag.value;
                out.writeInt(held.length);
                for (long value : held) { out.writeLong(value); }
                break;
            }
            default: throw new IOException("unknown nbt tag type " + tag.type);
        }
    }
}

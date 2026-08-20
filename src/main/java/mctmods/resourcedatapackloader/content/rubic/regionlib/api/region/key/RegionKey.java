package mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.key;


public class RegionKey {
    private final String name;

    public RegionKey(String name) {
        if (name == null) { throw new NullPointerException("name"); }
        this.name = name;
    }

    public String getName() { return name; }

    @Override public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        RegionKey regionKey = (RegionKey) o;
        return name.equals(regionKey.name);
    }

    @Override public int hashCode() { return name.hashCode(); }
}

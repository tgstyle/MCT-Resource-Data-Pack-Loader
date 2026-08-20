package mctmods.resourcedatapackloader.content.worldgen;


public final class ContentField {
    public static final int[] CHANCES = { 30, 30, 20, 20, 10, 10, 10, 10, 50 };
    public static final float SPREAD = 0.15F;
    public static final int CELL = 8;
    public static final int SEEDS = 1;
    public static final float REACH = 3.0F;
    public static final int ARMS = 0;
    public static final float ARM_REACH = 0.0F;
    private final int cell;
    private final int seeds;
    private final float reach;
    private final int arms;
    private final float armReach;
    private final float reachSq;
    private final float armReachSq;
    private final int stride;
    private final boolean speckled;
    private final int[] chances;
    private final float spread;
    private final int steps;
    private final ThreadLocal<Cell> held = new ThreadLocal<>();

    public ContentField(int[] chances, float spread) {
        this.speckled = true;
        this.chances = chances.length == 0 ? CHANCES : chances;
        this.spread = Math.max(0.0F, Math.min(1.0F, spread));
        this.steps = this.chances.length;
        this.cell = 1;
        this.seeds = 1;
        this.reach = 1.0F;
        this.arms = 0;
        this.armReach = 0.0F;
        this.reachSq = 1.0F;
        this.armReachSq = 0.0F;
        this.stride = 3;
    }

    public ContentField(int cell, int seeds, float reach, int arms, float armReach) {
        this.speckled = false;
        this.chances = CHANCES;
        this.spread = SPREAD;
        this.steps = CHANCES.length;
        this.cell = Math.max(1, cell);
        this.seeds = Math.max(1, Math.min(4, seeds));
        this.reach = Math.max(0.5F, reach);
        this.arms = Math.max(0, Math.min(6, arms));
        this.armReach = Math.max(0.0F, armReach);
        this.reachSq = this.reach * this.reach;
        this.armReachSq = this.armReach * this.armReach;
        this.stride = 3 + this.arms * 3;
    }

    public float strength(long salt, int x, int y, int z) {
        if (speckled) { return speckle(salt, x, y, z) / (float) steps; }
        int cellX = Math.floorDiv(x, cell);
        int cellY = Math.floorDiv(y, cell);
        int cellZ = Math.floorDiv(z, cell);
        Cell known = held.get();
        if (known == null) {
            known = new Cell(27 * seeds * stride);
            held.set(known);
        }
        if (!known.holds(salt, cellX, cellY, cellZ)) { build(known, salt, cellX, cellY, cellZ); }
        boolean reaching = arms > 0 && armReach > 0.0F;
        float best = 0.0F;
        float[] data = known.data;
        for (int index = 0; index < known.count; index++) {
            int base = index * stride;
            float offX = x - data[base];
            float offY = y - data[base + 1];
            float offZ = z - data[base + 2];
            float awaySq = offX * offX + offY * offY + offZ * offZ;
            if (awaySq >= reachSq && (!reaching || awaySq >= armReachSq)) { continue; }
            float away = (float) Math.sqrt(awaySq);
            if (awaySq < reachSq) {
                float found = 1.0F - away / reach;
                if (found > best) { best = found; }
                if (best >= 1.0F) { return 1.0F; }
            }
            if (!reaching || awaySq >= armReachSq || away <= 0.0001F) { continue; }
            float along = 1.0F - away / armReach;
            if (along <= best) { continue; }
            float unitX = offX / away;
            float unitY = offY / away;
            float unitZ = offZ / away;
            for (int arm = 0; arm < arms; arm++) {
                int at = base + 3 + arm * 3;
                float dot = unitX * data[at] + unitY * data[at + 1] + unitZ * data[at + 2];
                if (dot <= 0.0F) { continue; }
                float across = away * (1.0F - dot * dot);
                if (across >= reach) { continue; }
                float found = along * (1.0F - across / reach);
                if (found > best) { best = found; }
            }
        }
        return best;
    }

    private int speckle(long salt, int x, int y, int z) {
        int best = seedLevel(salt, x, y, z);
        for (int face = 0; face < 6; face++) {
            int offX = face == 0 ? 1 : face == 1 ? -1 : 0;
            int offY = face == 2 ? 1 : face == 3 ? -1 : 0;
            int offZ = face == 4 ? 1 : face == 5 ? -1 : 0;
            int level = seedLevel(salt, x + offX, y + offY, z + offZ);
            if (level <= 1) { continue; }
            long gate = mix(salt ^ 0x9E3779B97F4A7C15L, (x + offX) * 31 + offX, (y + offY) * 31 + offY, (z + offZ) * 31 + offZ);
            if ((((gate >>> 17) & 0xFFFFL) / 65536.0F) >= spread) { continue; }
            level -= 1 + (int) ((gate >>> 5) & 0xFFL) % 3;
            if (level > best) { best = level; }
        }
        return Math.max(best, 0);
    }

    private int seedLevel(long salt, int x, int y, int z) {
        int roll = (int) ((mix(salt, x, y, z) >>> 13) % 1000L);
        for (int level = 1; level <= steps; level++) {
            int chance = chances[level - 1];
            if (roll < chance) { return level; }
            roll -= chance;
        }
        return 0;
    }

    private void build(Cell known, long salt, int cellX, int cellY, int cellZ) {
        float[] data = known.data;
        int count = 0;
        for (int offX = -1; offX <= 1; offX++) {
            for (int offY = -1; offY <= 1; offY++) {
                for (int offZ = -1; offZ <= 1; offZ++) {
                    int atX = cellX + offX;
                    int atY = cellY + offY;
                    int atZ = cellZ + offZ;
                    long base = mix(salt, atX, atY, atZ);
                    for (int index = 0; index < seeds; index++) {
                        long seed = mix(base, index, index * 31, index * 131);
                        int at = count * stride;
                        data[at] = atX * (float) cell + fraction(seed) * cell;
                        data[at + 1] = atY * (float) cell + fraction(seed >>> 12) * cell;
                        data[at + 2] = atZ * (float) cell + fraction(seed >>> 24) * cell;
                        for (int arm = 0; arm < arms; arm++) {
                            long spun = mix(seed, arm * 7 + 3, arm * 17 + 5, arm * 37 + 11);
                            float armX = fraction(spun) * 2.0F - 1.0F;
                            float armY = fraction(spun >>> 12) * 2.0F - 1.0F;
                            float armZ = fraction(spun >>> 24) * 2.0F - 1.0F;
                            float span = (float) Math.sqrt(armX * armX + armY * armY + armZ * armZ);
                            if (span <= 0.0001F) { span = 1.0F; }
                            int slot = at + 3 + arm * 3;
                            data[slot] = armX / span;
                            data[slot + 1] = armY / span;
                            data[slot + 2] = armZ / span;
                        }
                        count++;
                    }
                }
            }
        }
        known.count = count;
        known.salt = salt;
        known.cellX = cellX;
        known.cellY = cellY;
        known.cellZ = cellZ;
        known.filled = true;
    }

    private static float fraction(long value) { return ((value >>> 11) & 0xFFF) / 4096.0F; }

    private static long mix(long salt, int x, int y, int z) {
        long value = salt;
        value = value * 6364136223846793005L + (x * 3129871L);
        value = value * 6364136223846793005L + (y * 116129781L);
        value = value * 6364136223846793005L + (z * 4295001919L);
        value ^= value >>> 29;
        value *= -7046029254386353131L;
        value ^= value >>> 32;
        return value;
    }

    private static final class Cell {
        private final float[] data;
        private int count;
        private long salt;
        private int cellX;
        private int cellY;
        private int cellZ;
        private boolean filled;

        private Cell(int size) { this.data = new float[size]; }

        private boolean holds(long salt, int cellX, int cellY, int cellZ) {
            return filled && this.salt == salt && this.cellX == cellX && this.cellY == cellY && this.cellZ == cellZ;
        }
    }
}

package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Line;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import javax.annotation.Nullable;

final class CityCourtSearch {
    static final int[] STEMS = { 1, 4, 7 };

    enum Verdict { SEATED, WET, CUT, FULL }

    record Seat(Verdict verdict, int radius, int stem, @Nullable BoundingBox box) {}

    interface Crowded { boolean at(int x, int z, int radius); }

    interface Blocked { boolean at(int x, int z, int radius, BoundingBox box); }

    private CityCourtSearch() {}

    static Seat seat(CityGround ground, Line line, boolean low, int level, @Nullable Crowded heartCrowded, Blocked blocked) {
        int end = low ? line.from() : line.to();
        int dir = low ? -1 : 1;
        int least = (line.width() + 1) / 2 + 1;
        boolean cut = false;
        for (int radius = (line.width() + 7) / 2; radius >= least; radius--) {
            int heart = end + dir * (radius + 1);
            if (heartCrowded != null && heartCrowded.at(line.alongX() ? heart : line.middle(), line.alongX() ? line.middle() : heart, radius)) { continue; }
            for (int stem : STEMS) {
                int spot = heart + dir * stem;
                int discX = line.alongX() ? spot : line.middle();
                int discZ = line.alongX() ? line.middle() : spot;
                BoundingBox box = ContentCityStructureEnds.court(line, end, dir, radius, stem);
                if (blocked.at(discX, discZ, radius, box)) { continue; }
                if (ContentCityStructure.steep(ground, discX, discZ, radius, level)) {
                    cut = true;
                    continue;
                }
                if (ContentCityStructure.dry(ground, box.minX(), box.minZ(), discX, discZ, radius)) { return new Seat(Verdict.SEATED, radius, stem, box); }
                return new Seat(Verdict.WET, 0, 0, null);
            }
        }
        return new Seat(cut ? Verdict.CUT : Verdict.FULL, 0, 0, null);
    }

    static boolean crowds(int minX, int minZ, int maxX, int maxZ, int discX, int discZ, int radius) {
        int nearX = Mth.clamp(discX, minX, maxX) - discX;
        int nearZ = Mth.clamp(discZ, minZ, maxZ) - discZ;
        return nearX * nearX + nearZ * nearZ <= (radius + 1) * (radius + 1);
    }
}

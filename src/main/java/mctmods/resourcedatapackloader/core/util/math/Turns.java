package mctmods.resourcedatapackloader.core.util.math;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.List;
import java.util.Random;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public final class Turns {
    public static final Rotation[] ALL = Rotation.values();

    private Turns() {}

    public static Rotation turn(List<PickDef> turns, Random random) {
        String named = PickDef.pick(turns, random, "");
        if (ShapeDef.NO_TURN.equals(named)) { return Rotation.NONE; }
        if (ShapeDef.QUARTER.equals(named)) { return Rotation.CLOCKWISE_90; }
        if (ShapeDef.HALF.equals(named)) { return Rotation.CLOCKWISE_180; }
        if (ShapeDef.THREEQUARTER.equals(named)) { return Rotation.COUNTERCLOCKWISE_90; }
        return pick(random);
    }

    public static Rotation pick(Random random) { return ALL[random.nextInt(ALL.length)]; }

    public static Mirror mirror(List<PickDef> mirrors, Random random) {
        String named = PickDef.pick(mirrors, random, "");
        if (ShapeDef.LEFTRIGHT.equals(named)) { return Mirror.LEFT_RIGHT; }
        if (ShapeDef.FRONTBACK.equals(named)) { return Mirror.FRONT_BACK; }
        return Mirror.NONE;
    }
}

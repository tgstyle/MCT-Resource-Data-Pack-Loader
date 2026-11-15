package mctmods.resourcedatapackloader.core.util.math;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

public final class Turns {
    public static final Rotation[] ALL = Rotation.values();

    private Turns() {}

    public static Rotation turn(List<PickDef> turns, RandomSource random) {
        String named = PickDef.pick(turns, random);
        if (ShapeDef.NO_TURN.equals(named)) { return Rotation.NONE; }
        if (ShapeDef.QUARTER.equals(named)) { return Rotation.CLOCKWISE_90; }
        if (ShapeDef.HALF.equals(named)) { return Rotation.CLOCKWISE_180; }
        if (ShapeDef.THREEQUARTER.equals(named)) { return Rotation.COUNTERCLOCKWISE_90; }
        return ALL[random.nextInt(ALL.length)];
    }

    public static Mirror mirror(List<PickDef> mirrors, RandomSource random) {
        String named = PickDef.pick(mirrors, random);
        if (ShapeDef.LEFTRIGHT.equals(named)) { return Mirror.LEFT_RIGHT; }
        if (ShapeDef.FRONTBACK.equals(named)) { return Mirror.FRONT_BACK; }
        return Mirror.NONE;
    }
}

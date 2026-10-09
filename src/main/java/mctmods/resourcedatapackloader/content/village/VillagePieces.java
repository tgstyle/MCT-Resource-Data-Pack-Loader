package mctmods.resourcedatapackloader.content.village;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureComponent;

public final class VillagePieces {
    public static final String PLOT = "RDPL:Plot";
    public static final String RECURRENT = "RdplRcVillage";

    private VillagePieces() {}

    public static void rails() {
        piece(RailPiece.class, RailPiece.ID);
        piece(MergePiece.class, MergePiece.ID);
    }

    public static void plots() { piece(ContentVillagePiece.class, PLOT); }

    public static void recurrent() { piece(RecurrentVillagePiece.class, RECURRENT); }

    private static void piece(Class<? extends StructureComponent> type, String id) { MapGenStructureIO.registerStructureComponent(type, id); }
}

package mctmods.resourcedatapackloader.mixin.reccomplex;

import mctmods.resourcedatapackloader.util.ContentLog;

import ivorius.ivtoolkit.maze.components.MazeComponentConnector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.LinkedList;
import java.util.concurrent.CancellationException;
import java.util.function.Predicate;

@SuppressWarnings("unused") @Mixin(value = MazeComponentConnector.class, remap = false) public abstract class MixinMazeComponentConnector {
    @Unique private static boolean rdpl$told;

    @Redirect(method = "connectMulti", at = @At(value = "INVOKE", target = "Ljava/util/LinkedList;size()I"), remap = false)
    private static int rdpl$roomsLeftToFill(LinkedList<?> exitStack) {
        rdpl$stopIfNobodyIsWaiting();
        return exitStack.size();
    }

    @Redirect(method = "connectMulti", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"), remap = false)
    private static boolean rdpl$couldThisOneGoHere(Predicate<Object> fits, Object component) {
        rdpl$stopIfNobodyIsWaiting();
        return fits.test(component);
    }

    @Unique private static void rdpl$stopIfNobodyIsWaiting() {
        if (!Thread.currentThread().isInterrupted()) { return; }
        if (!rdpl$told) {
            rdpl$told = true;
            ContentLog.LOGGER.info("Recurrent Complex stopped waiting on a maze and threw the answer away, but nothing ever told the solver, so it carried on filling rooms for a thread that had already gone. It is stopped at the next room it looks at instead, which hands the core back rather than leaving it turning over work no one will read");
        }
        throw new CancellationException("maze solving was given up on");
    }
}

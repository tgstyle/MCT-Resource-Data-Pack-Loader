package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.cube.BlankCube;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Collection;
import java.util.Collections;

@Mixin(EmptyChunk.class) @Implements(@Interface(iface = IColumn.class, prefix = "chunk$")) public abstract class MixinEmptyChunk {
    @Unique private Cube rdpl$blankCube;

    @Inject(method = "<init>", at = @At(value = "RETURN")) private void rubic_onConstruct(World worldIn, int x, int z, CallbackInfo cbi) {
        if (((IRubicWorld) worldIn).rdpl$isRubicWorld()) { rdpl$blankCube = new BlankCube((Chunk) (Object) this); }
    }

    public ICube chunk$getCube(int cubeY) { return rdpl$blankCube; }

    public ICube chunk$removeCube(int cubeY) { return rdpl$blankCube; }

    public void chunk$addCube(ICube cube) {
    }

    public Collection<ICube> chunk$getLoadedCubes() { return Collections.emptySet(); }

    public Iterable<ICube> chunk$getLoadedCubes(int startY, int endY) { return Collections.emptySet(); }
}

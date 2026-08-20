package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.Rubic;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;

public class BlankEntityContainer extends EntityContainer {
    public BlankEntityContainer() { this.entities = EntityContainer.EMPTY_ARR[0]; }

    @Override public void addEntity(@NotNull Entity entity) {
    }

    @Override public boolean remove(@NotNull Entity entity) { return false; }

    @Override public void clear() {
    }

    @Override @NotNull public Collection<Entity> getEntities() { return Collections.emptyList(); }

    @Override public int size() { return 0; }

    @Override public boolean needsSaving(boolean flag, long time, boolean isModified) { return false; }

    @Override public void markSaved(long time) {
    }

    @Override public void writeToNbt(@NotNull NBTTagCompound nbt, @NotNull String name, @NotNull Consumer<Entity> listener) {
    }

    @Override public void readFromNbt(@NotNull NBTTagCompound nbt, @NotNull String name, @NotNull World world, @NotNull Consumer<Entity> listener) {
    }

    public static final class BlankEntityMap extends ClassInheritanceMultiMap<Entity> {
        public BlankEntityMap() { super(Entity.class); }

        @Override public boolean add(Entity e) {
            Rubic.LOGGER.error("Attempted to add entity {} to a blank entity map", e, new Throwable());
            return false;
        }

        @Override public boolean remove(@NotNull Object o) { return false; }

        @Override public boolean contains(Object o) { return false; }

        @Override @NotNull public <S> Iterable<S> getByClass(@NotNull final Class<S> cl) { return Collections.emptyList(); }

        @Override @NotNull public Iterator<Entity> iterator() { return Collections.emptyIterator(); }

        @Override public int size() { return 0; }
    }
}

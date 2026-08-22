package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.Rubic;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.world.World;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;

public class BlankEntityContainer extends EntityContainer {
    public BlankEntityContainer() { this.entities = EntityContainer.EMPTY_ARR[0]; }

    @Override public void addEntity(@Nonnull Entity entity) {
    }

    @Override public boolean remove(@Nonnull Entity entity) { return false; }

    @Override public void clear() {
    }

    @Override @Nonnull public Collection<Entity> getEntities() { return Collections.emptyList(); }

    @Override public int size() { return 0; }

    @Override public boolean needsSaving(boolean flag, long time, boolean isModified) { return false; }

    @Override public void markSaved(long time) {
    }

    @Override public void writeToNbt(@Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull Consumer<Entity> listener) {
    }

    @Override public void readFromNbt(@Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull World world, @Nonnull Consumer<Entity> listener) {
    }

    public static final class BlankEntityMap extends ClassInheritanceMultiMap<Entity> {
        public BlankEntityMap() { super(Entity.class); }

        @Override public boolean add(Entity e) {
            Rubic.LOGGER.error("Attempted to add entity {} to a blank entity map", e, new Throwable());
            return false;
        }

        @Override public boolean remove(@Nonnull Object o) { return false; }

        @Override public boolean contains(Object o) { return false; }

        @Override @Nonnull public <S> Iterable<S> getByClass(@Nonnull final Class<S> cl) { return Collections.emptyList(); }

        @Override @Nonnull public Iterator<Entity> iterator() { return Collections.emptyIterator(); }

        @Override public int size() { return 0; }
    }
}

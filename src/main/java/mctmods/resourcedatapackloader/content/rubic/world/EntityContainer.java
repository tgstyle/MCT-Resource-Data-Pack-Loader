package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.Rubic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityContainer {
    @SuppressWarnings("unchecked") public static final ClassInheritanceMultiMap<Entity>[] EMPTY_ARR = new ClassInheritanceMultiMap[]{new BlankEntityContainer.BlankEntityMap()};

    @Nonnull protected ClassInheritanceMultiMap<Entity> entities;
    protected boolean hasActiveEntities;
    protected long lastSaveTime;

    public EntityContainer() {
        this.entities = new ClassInheritanceMultiMap<>(Entity.class);
        this.hasActiveEntities = false;
        this.lastSaveTime = 0;
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
        this.hasActiveEntities = true;
    }

    public boolean remove(Entity entity) { return this.entities.remove(entity); }

    public ClassInheritanceMultiMap<Entity> getEntitySet() { return this.entities; }

    public void clear() { this.entities.clear(); }

    public Collection<Entity> getEntities() { return Collections.unmodifiableCollection(this.entities); }

    public int size() { return this.entities.size(); }

    public boolean needsSaving(boolean flag, long time, boolean isModified) {
        if (flag) {
            if ((this.hasActiveEntities && time != lastSaveTime) || isModified) { return true; }
        }
        else if (this.hasActiveEntities && time >= this.lastSaveTime + 600) { return true; }
        return isModified;
    }

    public void markSaved(long time) { this.lastSaveTime = time; }

    public void writeToNbt(NBTTagCompound nbt, String name, Consumer<Entity> listener) {
        this.hasActiveEntities = false;
        NBTTagList nbtEntities = new NBTTagList();
        nbt.setTag(name, nbtEntities);
        for (Entity entity : this.entities) {
            NBTTagCompound nbtEntity = new NBTTagCompound();
            if (entity.writeToNBTOptional(nbtEntity)) {
                this.hasActiveEntities = true;
                nbtEntities.appendTag(nbtEntity);
                listener.accept(entity);
            }
        }
    }

    public void readFromNbt(NBTTagCompound nbt, String name, World world, Consumer<Entity> listener) {
        NBTTagList nbtEntities = nbt.getTagList(name, 10);
        for (int i = 0; i < nbtEntities.tagCount(); i++) {
            NBTTagCompound nbtEntity = nbtEntities.getCompoundTagAt(i);
            readEntity(nbtEntity, world, listener);
        }
    }

    @Nullable private Entity readEntity(NBTTagCompound nbtEntity, World world, Consumer<Entity> listener) {
        Entity entity = EntityList.createEntityFromNBT(nbtEntity, world);
        if (entity == null) { return null; }
        if (entity instanceof EntityPlayerMP) {
            Rubic.LOGGER.error("EntityPlayerMP is serialized in save file! Reading the entity would break world ticking, skipping");
            return null;
        }
        addEntity(entity);
        listener.accept(entity);
        if (nbtEntity.hasKey("Passengers", Constants.NBT.TAG_LIST)) {
            NBTTagList nbttaglist = nbtEntity.getTagList("Passengers", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                Entity entity1 = readEntity(nbttaglist.getCompoundTagAt(i), world, listener);
                if (entity1 != null) { entity1.startRiding(entity, true); }
            }
        }
        return entity;
    }
}

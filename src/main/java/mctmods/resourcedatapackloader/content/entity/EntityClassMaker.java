package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class EntityClassMaker {
    private static final String PREFIX = EntityClassMaker.class.getPackageName().replace('.', '/') + "/Variant_";
    private static final Map<EntityType<?>, Optional<MethodHandle>> MADE = new ConcurrentHashMap<>();

    private EntityClassMaker() {}

    @SuppressWarnings("unchecked") public static <T extends Entity> Entity make(EntityType.EntityFactory<T> factory, EntityType<?> type, Level level, EntityVariantDef def) {
        EntityType<T> own = (EntityType<T>) type;
        boolean stores = def.storage() != null;
        if (!def.hostile() && !stores) { return factory.create(own, level); }
        Optional<MethodHandle> made = MADE.get(type);
        if (made == null) {
            T probe = factory.create(own, level);
            boolean monster = def.hostile() && !(probe instanceof Enemy);
            made = monster || stores ? Optional.ofNullable(made(probe.getClass(), def, monster, stores)) : Optional.empty();
            MADE.put(type, made);
            if (made.isEmpty()) { return probe; }
        }
        if (made.isEmpty()) { return factory.create(own, level); }
        try { return (Entity) made.get().invoke(type, level); }
        catch (Throwable ex) {
            ContentLog.LOGGER.error("Entity variant {} could not be made as a class of its own, making it as {} instead", def.key(), def.base(), ex);
            MADE.put(type, Optional.empty());
            return factory.create(own, level);
        }
    }

    @Nullable private static MethodHandle made(Class<?> base, EntityVariantDef def, boolean monster, boolean stores) {
        Constructor<?> parent;
        try { parent = base.getDeclaredConstructor(EntityType.class, Level.class); }
        catch (NoSuchMethodException absent) { parent = null; }
        if (parent == null || Modifier.isFinal(base.getModifiers()) || !Modifier.isPublic(base.getModifiers()) || !(Modifier.isPublic(parent.getModifiers()) || Modifier.isProtected(parent.getModifiers()))) {
            if (monster) { ContentLog.LOGGER.error("Entity variant {} is hostile, but {} cannot be copied as a monster, so it keeps its base behavior", def.key(), base.getName()); }
            if (stores) { ContentLog.LOGGER.error("Entity variant {} has storage, but {} cannot be copied, so where it has an inventory of its own that one answers before the pack storage", def.key(), base.getName()); }
            return null;
        }
        String name = PREFIX + def.key().toString().replaceAll("[^A-Za-z0-9_]", "_");
        try {
            Class<?> made = MethodHandles.lookup().defineClass(write(name, base, monster, stores));
            ContentLog.LOGGER.debug("Made class {} for entity variant {}", made.getName(), def.key());
            return MethodHandles.lookup().findConstructor(made, MethodType.methodType(void.class, EntityType.class, Level.class));
        }
        catch (ReflectiveOperationException | LinkageError ex) {
            ContentLog.LOGGER.error("Could not make a class for entity variant {}", def.key(), ex);
            return null;
        }
    }

    private static byte[] write(String name, Class<?> base, boolean monster, boolean stores) {
        String parent = Type.getInternalName(base);
        String init = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(EntityType.class), Type.getType(Level.class));
        ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, name, null, parent, monster ? new String[] { Type.getInternalName(Enemy.class) } : null);
        if (stores) { storage(writer, parent); }
        MethodVisitor made = writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", init, null, null);
        made.visitCode();
        made.visitVarInsn(Opcodes.ALOAD, 0);
        made.visitVarInsn(Opcodes.ALOAD, 1);
        made.visitVarInsn(Opcodes.ALOAD, 2);
        made.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, "<init>", init, false);
        made.visitInsn(Opcodes.RETURN);
        made.visitMaxs(3, 3);
        made.visitEnd();
        writer.visitEnd();
        return writer.toByteArray();
    }

    private static void storage(ClassWriter writer, String parent) {
        Type held = Type.getType(LazyOptional.class);
        String asked = Type.getMethodDescriptor(held, Type.getType(Capability.class), Type.getType(Direction.class));
        MethodVisitor gets = writer.visitMethod(Opcodes.ACC_PUBLIC, "getCapability", asked, null, null);
        gets.visitCode();
        gets.visitVarInsn(Opcodes.ALOAD, 0);
        gets.visitVarInsn(Opcodes.ALOAD, 1);
        gets.visitVarInsn(Opcodes.ALOAD, 0);
        gets.visitVarInsn(Opcodes.ALOAD, 1);
        gets.visitVarInsn(Opcodes.ALOAD, 2);
        gets.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, "getCapability", asked, false);
        gets.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(EntityStorage.class), "capability", Type.getMethodDescriptor(held, Type.getType(Entity.class), Type.getType(Capability.class), held), false);
        gets.visitInsn(Opcodes.ARETURN);
        gets.visitMaxs(5, 3);
        gets.visitEnd();
    }
}

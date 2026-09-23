package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
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
    private static final String PREFIX = EntityClassMaker.class.getPackageName().replace('.', '/') + "/Enemy_";
    private static final Map<EntityType<?>, Optional<MethodHandle>> ENEMIES = new ConcurrentHashMap<>();

    private EntityClassMaker() {}

    @SuppressWarnings("unchecked") public static <T extends Entity> Entity make(EntityType.EntityFactory<T> factory, EntityType<?> type, Level level, EntityVariantDef def) {
        EntityType<T> own = (EntityType<T>) type;
        if (!def.hostile()) { return factory.create(own, level); }
        Optional<MethodHandle> enemy = ENEMIES.get(type);
        if (enemy == null) {
            T probe = factory.create(own, level);
            enemy = probe instanceof Enemy ? Optional.empty() : Optional.ofNullable(enemy(probe.getClass(), def));
            ENEMIES.put(type, enemy);
            if (enemy.isEmpty()) { return probe; }
        }
        if (enemy.isEmpty()) { return factory.create(own, level); }
        try { return (Entity) enemy.get().invoke(type, level); }
        catch (Throwable ex) {
            ContentLog.LOGGER.error("Entity variant {} could not be made as a monster, making it as {} instead", def.key(), def.base(), ex);
            ENEMIES.put(type, Optional.empty());
            return factory.create(own, level);
        }
    }

    @Nullable private static MethodHandle enemy(Class<?> base, EntityVariantDef def) {
        Constructor<?> parent;
        try { parent = base.getDeclaredConstructor(EntityType.class, Level.class); }
        catch (NoSuchMethodException absent) { parent = null; }
        if (parent == null || Modifier.isFinal(base.getModifiers()) || !Modifier.isPublic(base.getModifiers()) || !(Modifier.isPublic(parent.getModifiers()) || Modifier.isProtected(parent.getModifiers()))) {
            ContentLog.LOGGER.error("Entity variant {} is hostile, but {} cannot be copied as a monster, so it keeps its base behavior", def.key(), base.getName());
            return null;
        }
        String name = PREFIX + def.key().toString().replaceAll("[^A-Za-z0-9_]", "_");
        try {
            Class<?> made = MethodHandles.lookup().defineClass(write(name, base));
            ContentLog.LOGGER.debug("Made monster class {} for entity variant {}", made.getName(), def.key());
            return MethodHandles.lookup().findConstructor(made, MethodType.methodType(void.class, EntityType.class, Level.class));
        }
        catch (ReflectiveOperationException | LinkageError ex) {
            ContentLog.LOGGER.error("Could not make a monster class for entity variant {}", def.key(), ex);
            return null;
        }
    }

    private static byte[] write(String name, Class<?> base) {
        String parent = Type.getInternalName(base);
        String init = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(EntityType.class), Type.getType(Level.class));
        ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, name, null, parent, new String[] { Type.getInternalName(Enemy.class) });
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
}

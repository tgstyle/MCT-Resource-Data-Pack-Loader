package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import java.lang.reflect.Method;
import javax.annotation.Nullable;

public final class EntityClassMaker {
    private static final String PACKAGE = "mctmods.resourcedatapackloader.generated.";
    private static Method define;

    private EntityClassMaker() {}

    @Nullable public static Class<? extends Entity> make(Class<? extends Entity> base, String key) {
        try { base.getConstructor(World.class); }
        catch (NoSuchMethodException absent) {
            ContentLog.LOGGER.error("Entity variant {} is based on {}, which has no plain world constructor, so it cannot be copied", key, base.getName());
            return null;
        }

        String name = PACKAGE + key.replaceAll("[^A-Za-z0-9_]", "_");
        ClassLoader loader = EntityClassMaker.class.getClassLoader();
        try { return loaded(loader.loadClass(name)); }
        catch (ClassNotFoundException expected) { ContentLog.LOGGER.debug("Making a class for entity variant {}", key); }

        byte[] bytes = write(name, base);
        try { return loaded((Class<?>) definer().invoke(loader, name, bytes, 0, bytes.length)); }
        catch (ReflectiveOperationException ex) {
            ContentLog.LOGGER.error("Could not make a class for entity variant {}", key, ex);
            return null;
        }
    }

    @SuppressWarnings("unchecked") private static Class<? extends Entity> loaded(Class<?> type) { return (Class<? extends Entity>) type; }

    private static Method definer() throws ReflectiveOperationException {
        if (define == null) {
            define = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            define.setAccessible(true);
        }
        return define;
    }

    private static byte[] write(String name, Class<?> base) {
        String self = name.replace('.', '/');
        String parent = Type.getInternalName(base);
        String world = Type.getInternalName(World.class);

        ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, self, null, parent, null);
        MethodVisitor made = writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(L" + world + ";)V", null, null);
        made.visitCode();
        made.visitVarInsn(Opcodes.ALOAD, 0);
        made.visitVarInsn(Opcodes.ALOAD, 1);
        made.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, "<init>", "(L" + world + ";)V", false);
        made.visitInsn(Opcodes.RETURN);
        made.visitMaxs(2, 2);
        made.visitEnd();
        writer.visitEnd();
        return writer.toByteArray();
    }
}

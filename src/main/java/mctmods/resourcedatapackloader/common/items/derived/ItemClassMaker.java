package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public final class ItemClassMaker {
    private static final String CONSTANTS = Type.getInternalName(DerivedConstants.class);
    private static final String OBJECT = "Ljava/lang/Object;";

    private ItemClassMaker() {}

    public record Override(Method target, Object constant, int slot) {}

    @Nullable public static Class<?> make(Class<?> base, Identifier key, List<Override> overrides) {
        if (Modifier.isFinal(base.getModifiers()) || base.isSealed() || base.isRecord()) {
            ContentLog.LOGGER.error("Derived item {} is based on {}, a final or sealed class or a record, so it cannot be copied", key, base.getName());
            return null;
        }
        ClassLoader loader = base.getClassLoader();
        if (!sees(base)) {
            ContentLog.LOGGER.error("Derived item {} is based on {}, whose module cannot see RDPL, so it cannot be copied", key, base.getName());
            return null;
        }
        String full = base.getName();
        int dot = full.lastIndexOf('.');
        String name = (dot < 0 ? "" : full.substring(0, dot + 1)) + "RdplDerived_" + (key.getNamespace() + "_" + key.getPath()).replaceAll("[^A-Za-z0-9_]", "_");
        try { return Class.forName(name, false, loader); }
        catch (ClassNotFoundException expected) { ContentLog.LOGGER.debug("Making a class for derived item {}", key); }
        byte[] bytes = write(name, key, base, overrides);
        try { return MethodHandles.privateLookupIn(base, MethodHandles.lookup()).defineClass(bytes); }
        catch (ReflectiveOperationException | LinkageError | IllegalArgumentException e) {
            ContentLog.LOGGER.error("Could not make a class for derived item {}", key, e);
            return null;
        }
    }

    private static boolean sees(Class<?> base) {
        ClassLoader loader = base.getClassLoader();
        if (loader == null) { return false; }
        Module reader = base.getModule();
        Module own = DerivedConstants.class.getModule();
        if (!reader.canRead(own) || !own.isExported(DerivedConstants.class.getPackageName(), reader)) { return false; }
        try { return loader.loadClass(DerivedConstants.class.getName()) == DerivedConstants.class; }
        catch (ClassNotFoundException absent) { return false; }
    }

    private static byte[] write(String name, Identifier owner, Class<?> base, List<Override> overrides) {
        String parent = Type.getInternalName(base);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES) {
            @java.lang.Override protected String getCommonSuperClass(String first, String second) { return "java/lang/Object"; }
        };
        writer.visit(Opcodes.V21, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, name.replace('.', '/'), null, parent, null);
        for (Override each : overrides) { method(writer, parent, owner, each); }
        writer.visitEnd();
        return writer.toByteArray();
    }

    private static void method(ClassWriter writer, String parent, Identifier owner, Override each) {
        Method target = each.target();
        String descriptor = Type.getMethodDescriptor(target);
        Type returns = Type.getReturnType(target);
        int access = Modifier.isProtected(target.getModifiers()) ? Opcodes.ACC_PROTECTED : Opcodes.ACC_PUBLIC;
        MethodVisitor visitor = writer.visitMethod(access, target.getName(), descriptor, null, null);
        visitor.visitCode();
        Class<?> type = target.getReturnType();
        Object constant = each.constant();
        if (type.isPrimitive()) { literal(visitor, constant); }
        else if (type == String.class) { visitor.visitLdcInsn(constant); }
        else if (type.isEnum() && constant instanceof Enum) { visitor.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(((Enum<?>) constant).getDeclaringClass()), ((Enum<?>) constant).name(), Type.getDescriptor(((Enum<?>) constant).getDeclaringClass())); }
        else { stored(visitor, parent, owner, target, descriptor, returns, each.slot()); return; }
        visitor.visitInsn(returns.getOpcode(Opcodes.IRETURN));
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private static void literal(MethodVisitor visitor, Object constant) {
        if (constant instanceof Boolean) { visitor.visitInsn(((Boolean) constant) ? Opcodes.ICONST_1 : Opcodes.ICONST_0); }
        else if (constant instanceof Character) { visitor.visitLdcInsn((int) (Character) constant); }
        else if (constant instanceof Byte || constant instanceof Short) { visitor.visitLdcInsn(((Number) constant).intValue()); }
        else { visitor.visitLdcInsn(constant); }
    }

    private static void stored(MethodVisitor visitor, String parent, Identifier owner, Method target, String descriptor, Type returns, int slot) {
        Label found = new Label();
        visitor.visitLdcInsn(owner.toString());
        visitor.visitLdcInsn(slot);
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, CONSTANTS, "value", "(Ljava/lang/String;I)" + OBJECT, false);
        visitor.visitInsn(Opcodes.DUP);
        visitor.visitFieldInsn(Opcodes.GETSTATIC, CONSTANTS, "MISSING", OBJECT);
        visitor.visitJumpInsn(Opcodes.IF_ACMPNE, found);
        visitor.visitInsn(Opcodes.POP);
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        int index = 1;
        for (Type argument : Type.getArgumentTypes(descriptor)) {
            visitor.visitVarInsn(argument.getOpcode(Opcodes.ILOAD), index);
            index += argument.getSize();
        }
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, target.getName(), descriptor, false);
        visitor.visitInsn(Opcodes.ARETURN);
        visitor.visitLabel(found);
        visitor.visitTypeInsn(Opcodes.CHECKCAST, returns.getInternalName());
        visitor.visitInsn(Opcodes.ARETURN);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }
}

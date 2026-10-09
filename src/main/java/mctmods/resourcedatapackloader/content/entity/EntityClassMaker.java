package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.interfaces.IPackRocket;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Nullable;

public final class EntityClassMaker {
    private static final String PACKAGE = "mctmods.resourcedatapackloader.generated.";
    private static final String[] SPAWN_CHECK = {"func_70601_bi", "getCanSpawnHere"};
    private static final String[] REMOVAL = {"func_70106_y", "setDead"};
    private static final String TIERED_ROCKET = "micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket";
    private static final String AUTO_ROCKET = "micdoodle8/mods/galacticraft/api/prefab/entity/EntityAutoRocket";
    private static final String ROCKETS = "mctmods/resourcedatapackloader/util/compat/GcRockets";
    private static final String STORAGE = "mctmods/resourcedatapackloader/content/entity/EntityStorage";
    private static Method define;

    private EntityClassMaker() {}

    @Nullable public static Class<? extends Entity> make(Class<? extends Entity> base, String key, boolean ignoresSpawnRules, boolean hostile, boolean rocket, boolean storage) {
        try { base.getConstructor(World.class); }
        catch (NoSuchMethodException absent) {
            ContentLog.LOGGER.error("Entity variant {} is based on {}, which has no plain world constructor, so it cannot be copied", key, base.getName());
            return null;
        }
        String check = ignoresSpawnRules ? spawnCheck(base) : null;
        if (ignoresSpawnRules && check == null) { ContentLog.LOGGER.error("Entity variant {} sets ignoresSpawnRules but {} has no reachable spawn check, so the rules still apply", key, base.getName()); }
        String name = PACKAGE + key.replaceAll("[^A-Za-z0-9_]", "_");
        ClassLoader loader = EntityClassMaker.class.getClassLoader();
        try { return loaded(loader.loadClass(name)); }
        catch (ClassNotFoundException expected) { ContentLog.LOGGER.debug("Making a class for entity variant {}", key); }
        byte[] bytes = write(name, base, check, hostile && !IMob.class.isAssignableFrom(base), rocket, storage);
        try { return loaded((Class<?>) definer().invoke(loader, name, bytes, 0, bytes.length)); }
        catch (ReflectiveOperationException ex) {
            ContentLog.LOGGER.error("Could not make a class for entity variant {}", key, ex);
            return null;
        }
    }

    @SuppressWarnings("unchecked") private static Class<? extends Entity> loaded(Class<?> type) { return (Class<? extends Entity>) type; }

    @Nullable private static String spawnCheck(Class<?> base) { return named(base, SPAWN_CHECK); }

    @Nullable private static String named(Class<?> base, String[] candidates) {
        for (String candidate : candidates) {
            try { return base.getMethod(candidate).getName(); }
            catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }

    private static Method definer() throws ReflectiveOperationException {
        if (define == null) {
            define = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            define.setAccessible(true);
        }
        return define;
    }

    private static byte[] write(String name, Class<?> base, @Nullable String check, boolean monster, boolean rocket, boolean storage) {
        String self = name.replace('.', '/');
        String parent = Type.getInternalName(base);
        String world = Type.getInternalName(World.class);
        ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, self, null, parent, rocket ? new String[] { Type.getInternalName(IPackRocket.class) } : monster ? new String[] { Type.getInternalName(IMob.class) } : null);
        MethodVisitor made = open(writer, "<init>", "(L" + world + ";)V");
        load(made, 0, 1);
        made.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, "<init>", "(L" + world + ";)V", false);
        close(made, Opcodes.RETURN, 2, 2);
        if (check != null) {
            MethodVisitor spawn = open(writer, check, "()Z");
            spawn.visitInsn(Opcodes.ICONST_1);
            close(spawn, Opcodes.IRETURN, 1, 1);
        }
        if (rocket) { rocket(writer, parent); }
        if (storage) { storage(writer, parent); }
        String dies = storage && !rocket(base) && !EntityLivingBase.class.isAssignableFrom(base) ? named(base, REMOVAL) : null;
        if (dies != null) {
            MethodVisitor dead = open(writer, dies, "()V");
            load(dead, 0);
            dead.visitMethodInsn(Opcodes.INVOKESTATIC, STORAGE, "dying", "(L" + Type.getInternalName(Entity.class) + ";)V", false);
            load(dead, 0);
            dead.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, dies, "()V", false);
            close(dead, Opcodes.RETURN, 1, 1);
        }
        writer.visitEnd();
        return writer.toByteArray();
    }

    private static MethodVisitor open(ClassWriter writer, String name, String descriptor) {
        MethodVisitor method = writer.visitMethod(Opcodes.ACC_PUBLIC, name, descriptor, null, null);
        method.visitCode();
        return method;
    }

    private static void load(MethodVisitor method, int... slots) {
        for (int slot : slots) { method.visitVarInsn(Opcodes.ALOAD, slot); }
    }

    private static void close(MethodVisitor method, int returning, int stack, int locals) {
        method.visitInsn(returning);
        method.visitMaxs(stack, locals);
        method.visitEnd();
    }

    private static void storage(ClassWriter writer, String parent) {
        String entity = "L" + Type.getInternalName(Entity.class) + ";";
        String asked = "L" + Type.getInternalName(Capability.class) + ";L" + Type.getInternalName(EnumFacing.class) + ";";
        String capability = "L" + Type.getInternalName(Capability.class) + ";";
        String object = "L" + Type.getInternalName(Object.class) + ";";
        MethodVisitor gets = open(writer, "getCapability", "(" + asked + ")" + object);
        load(gets, 0, 1, 0, 1, 2);
        gets.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, "getCapability", "(" + asked + ")" + object, false);
        gets.visitMethodInsn(Opcodes.INVOKESTATIC, STORAGE, "capability", "(" + entity + capability + object + ")" + object, false);
        close(gets, Opcodes.ARETURN, 5, 3);
        MethodVisitor has = open(writer, "hasCapability", "(" + asked + ")Z");
        load(has, 0, 1, 0, 1, 2);
        has.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, "hasCapability", "(" + asked + ")Z", false);
        has.visitMethodInsn(Opcodes.INVOKESTATIC, STORAGE, "has", "(" + entity + capability + "Z)Z", false);
        close(has, Opcodes.IRETURN, 5, 3);
    }

    static boolean rocket(Class<?> base) {
        for (Class<?> type = base; type != null; type = type.getSuperclass()) {
            if (type.getName().equals(TIERED_ROCKET)) { return true; }
        }
        return false;
    }

    private static void rocket(ClassWriter writer, String parent) {
        String entity = "L" + Type.getInternalName(Entity.class) + ";";
        String stack = "L" + Type.getInternalName(ItemStack.class) + ";";
        String player = "L" + Type.getInternalName(EntityPlayerMP.class) + ";";
        String cargo = "L" + Type.getInternalName(NonNullList.class) + ";";
        String list = "L" + Type.getInternalName(List.class) + ";";
        for (String[] capped : new String[][] { { "getRocketTier", "tier" }, { "getFuelTankCapacity", "fuelTank" } }) {
            MethodVisitor size = open(writer, capped[0], "()I");
            load(size, 0, 0);
            size.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, capped[0], "()I", false);
            size.visitMethodInsn(Opcodes.INVOKESTATIC, ROCKETS, capped[1], "(" + entity + "I)I", false);
            close(size, Opcodes.IRETURN, 2, 1);
        }
        MethodVisitor drops = open(writer, "getItemsDropped", "(" + list + ")" + list);
        load(drops, 0, 1);
        drops.visitMethodInsn(Opcodes.INVOKESTATIC, ROCKETS, "dropped", "(" + entity + list + ")" + list, false);
        close(drops, Opcodes.ARETURN, 2, 2);
        MethodVisitor picked = open(writer, "getPickedResult", "(L" + Type.getInternalName(RayTraceResult.class) + ";)" + stack);
        load(picked, 0);
        picked.visitMethodInsn(Opcodes.INVOKESTATIC, ROCKETS, "picked", "(" + entity + ")" + stack, false);
        close(picked, Opcodes.ARETURN, 1, 2);
        MethodVisitor flown = open(writer, "onTeleport", "(" + player + ")V");
        load(flown, 0, 1);
        flown.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, "onTeleport", "(" + player + ")V", false);
        load(flown, 0, 1);
        flown.visitMethodInsn(Opcodes.INVOKESTATIC, ROCKETS, "teleported", "(" + entity + player + ")V", false);
        close(flown, Opcodes.RETURN, 2, 2);
        MethodVisitor loads = open(writer, "packCargo", "(" + cargo + ")V");
        load(loads, 0, 1);
        loads.visitFieldInsn(Opcodes.PUTFIELD, AUTO_ROCKET, "stacks", cargo);
        close(loads, Opcodes.RETURN, 2, 2);
    }
}

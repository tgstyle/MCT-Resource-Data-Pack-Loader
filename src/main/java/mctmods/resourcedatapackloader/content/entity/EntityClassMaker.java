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
        MethodVisitor made = writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(L" + world + ";)V", null, null);
        made.visitCode();
        made.visitVarInsn(Opcodes.ALOAD, 0);
        made.visitVarInsn(Opcodes.ALOAD, 1);
        made.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, "<init>", "(L" + world + ";)V", false);
        made.visitInsn(Opcodes.RETURN);
        made.visitMaxs(2, 2);
        made.visitEnd();
        if (check != null) {
            MethodVisitor spawn = writer.visitMethod(Opcodes.ACC_PUBLIC, check, "()Z", null, null);
            spawn.visitCode();
            spawn.visitInsn(Opcodes.ICONST_1);
            spawn.visitInsn(Opcodes.IRETURN);
            spawn.visitMaxs(1, 1);
            spawn.visitEnd();
        }
        if (rocket) { rocket(writer, parent); }
        if (storage) { storage(writer, parent); }
        String dies = storage && !rocket(base) && !EntityLivingBase.class.isAssignableFrom(base) ? named(base, REMOVAL) : null;
        if (dies != null) {
            MethodVisitor dead = writer.visitMethod(Opcodes.ACC_PUBLIC, dies, "()V", null, null);
            dead.visitCode();
            dead.visitVarInsn(Opcodes.ALOAD, 0);
            dead.visitMethodInsn(Opcodes.INVOKESTATIC, STORAGE, "dying", "(L" + Type.getInternalName(Entity.class) + ";)V", false);
            dead.visitVarInsn(Opcodes.ALOAD, 0);
            dead.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, dies, "()V", false);
            dead.visitInsn(Opcodes.RETURN);
            dead.visitMaxs(1, 1);
            dead.visitEnd();
        }
        writer.visitEnd();
        return writer.toByteArray();
    }

    private static void storage(ClassWriter writer, String parent) {
        String entity = "L" + Type.getInternalName(Entity.class) + ";";
        String asked = "L" + Type.getInternalName(Capability.class) + ";L" + Type.getInternalName(EnumFacing.class) + ";";
        String capability = "L" + Type.getInternalName(Capability.class) + ";";
        String object = "L" + Type.getInternalName(Object.class) + ";";
        MethodVisitor gets = writer.visitMethod(Opcodes.ACC_PUBLIC, "getCapability", "(" + asked + ")" + object, null, null);
        gets.visitCode();
        gets.visitVarInsn(Opcodes.ALOAD, 0);
        gets.visitVarInsn(Opcodes.ALOAD, 1);
        gets.visitVarInsn(Opcodes.ALOAD, 0);
        gets.visitVarInsn(Opcodes.ALOAD, 1);
        gets.visitVarInsn(Opcodes.ALOAD, 2);
        gets.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, "getCapability", "(" + asked + ")" + object, false);
        gets.visitMethodInsn(Opcodes.INVOKESTATIC, STORAGE, "capability", "(" + entity + capability + object + ")" + object, false);
        gets.visitInsn(Opcodes.ARETURN);
        gets.visitMaxs(5, 3);
        gets.visitEnd();
        MethodVisitor has = writer.visitMethod(Opcodes.ACC_PUBLIC, "hasCapability", "(" + asked + ")Z", null, null);
        has.visitCode();
        has.visitVarInsn(Opcodes.ALOAD, 0);
        has.visitVarInsn(Opcodes.ALOAD, 1);
        has.visitVarInsn(Opcodes.ALOAD, 0);
        has.visitVarInsn(Opcodes.ALOAD, 1);
        has.visitVarInsn(Opcodes.ALOAD, 2);
        has.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, "hasCapability", "(" + asked + ")Z", false);
        has.visitMethodInsn(Opcodes.INVOKESTATIC, STORAGE, "has", "(" + entity + capability + "Z)Z", false);
        has.visitInsn(Opcodes.IRETURN);
        has.visitMaxs(5, 3);
        has.visitEnd();
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
            MethodVisitor size = writer.visitMethod(Opcodes.ACC_PUBLIC, capped[0], "()I", null, null);
            size.visitCode();
            size.visitVarInsn(Opcodes.ALOAD, 0);
            size.visitVarInsn(Opcodes.ALOAD, 0);
            size.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, capped[0], "()I", false);
            size.visitMethodInsn(Opcodes.INVOKESTATIC, ROCKETS, capped[1], "(" + entity + "I)I", false);
            size.visitInsn(Opcodes.IRETURN);
            size.visitMaxs(2, 1);
            size.visitEnd();
        }
        MethodVisitor drops = writer.visitMethod(Opcodes.ACC_PUBLIC, "getItemsDropped", "(" + list + ")" + list, null, null);
        drops.visitCode();
        drops.visitVarInsn(Opcodes.ALOAD, 0);
        drops.visitVarInsn(Opcodes.ALOAD, 1);
        drops.visitMethodInsn(Opcodes.INVOKESTATIC, ROCKETS, "dropped", "(" + entity + list + ")" + list, false);
        drops.visitInsn(Opcodes.ARETURN);
        drops.visitMaxs(2, 2);
        drops.visitEnd();
        MethodVisitor picked = writer.visitMethod(Opcodes.ACC_PUBLIC, "getPickedResult", "(L" + Type.getInternalName(RayTraceResult.class) + ";)" + stack, null, null);
        picked.visitCode();
        picked.visitVarInsn(Opcodes.ALOAD, 0);
        picked.visitMethodInsn(Opcodes.INVOKESTATIC, ROCKETS, "picked", "(" + entity + ")" + stack, false);
        picked.visitInsn(Opcodes.ARETURN);
        picked.visitMaxs(1, 2);
        picked.visitEnd();
        MethodVisitor flown = writer.visitMethod(Opcodes.ACC_PUBLIC, "onTeleport", "(" + player + ")V", null, null);
        flown.visitCode();
        flown.visitVarInsn(Opcodes.ALOAD, 0);
        flown.visitVarInsn(Opcodes.ALOAD, 1);
        flown.visitMethodInsn(Opcodes.INVOKESPECIAL, parent, "onTeleport", "(" + player + ")V", false);
        flown.visitVarInsn(Opcodes.ALOAD, 0);
        flown.visitVarInsn(Opcodes.ALOAD, 1);
        flown.visitMethodInsn(Opcodes.INVOKESTATIC, ROCKETS, "teleported", "(" + entity + player + ")V", false);
        flown.visitInsn(Opcodes.RETURN);
        flown.visitMaxs(2, 2);
        flown.visitEnd();
        MethodVisitor loads = writer.visitMethod(Opcodes.ACC_PUBLIC, "packCargo", "(" + cargo + ")V", null, null);
        loads.visitCode();
        loads.visitVarInsn(Opcodes.ALOAD, 0);
        loads.visitVarInsn(Opcodes.ALOAD, 1);
        loads.visitFieldInsn(Opcodes.PUTFIELD, AUTO_ROCKET, "stacks", cargo);
        loads.visitInsn(Opcodes.RETURN);
        loads.visitMaxs(2, 2);
        loads.visitEnd();
    }
}

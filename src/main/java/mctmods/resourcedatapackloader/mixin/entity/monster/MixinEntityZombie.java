package mctmods.resourcedatapackloader.mixin.entity.monster;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.common.ForgeModContainer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityZombie.class) public abstract class MixinEntityZombie {
    @Redirect(method = "onInitialSpawn", at = @At(value = "FIELD", target = "Lnet/minecraftforge/common/ForgeModContainer;zombieBabyChance:F", opcode = Opcodes.GETSTATIC, remap = false)) private float rdpl$babyChance() { return ContentEntities.baseBabyChance((EntityZombie) (Object) this, ForgeModContainer.zombieBabyChance); }
}

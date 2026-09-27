package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import com.google.common.reflect.TypeToken;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import javax.annotation.Nullable;

public record EntityLook(@Nullable Identifier texture, int body, int armor, int held, boolean lying, boolean hidesArmor, boolean hidesHeld) {
    private static final ContextKey<EntityLook> KEY = new ContextKey<>(Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "entity_look"));
    private static final EntityLook PLAIN = new EntityLook(null, 0, 0, 0, false, false, false);

    public static void register(RegisterRenderStateModifiersEvent event) { event.registerEntityModifier(new TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>>() {}, EntityLook::extract); }

    private static void extract(LivingEntity entity, EntityRenderState state) {
        EntityLook look = new EntityLook(ContentEntities.texture(entity), ContentEntities.tint(entity, EntityVariantDef.BODY), ContentEntities.tint(entity, EntityVariantDef.ARMOR),
                ContentEntities.tint(entity, EntityVariantDef.HELD), ContentEntities.lyingAsleep(entity), ContentEntities.hidesArmor(entity), ContentEntities.hidesHeld(entity));
        state.setRenderData(KEY, PLAIN.equals(look) ? null : look);
    }

    public static EntityLook of(EntityRenderState state) {
        EntityLook look = state.getRenderData(KEY);
        return look == null ? PLAIN : look;
    }
}

package cc.hyperium.mixins.renderer;

import cc.hyperium.cosmetics.companions.dragon.CustomDragon;
import cc.hyperium.cosmetics.companions.dragon.RenderCustomDragon;
import cc.hyperium.cosmetics.companions.hamster.EntityHamster;
import cc.hyperium.cosmetics.companions.hamster.RenderHamster;
import cc.hyperium.mixinsimp.renderer.IMixinRenderManager;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RenderManager.class)
public class MixinRenderManger implements IMixinRenderManager {
    @Shadow
    private double renderPosX;

    @Shadow
    private double renderPosY;

    @Shadow
    private double renderPosZ;

    @Shadow
    private Map<String, RenderPlayer> skinMap;

    @Shadow private Map<Class<? extends Entity>, Render<? extends Entity>> entityRenderMap;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectEntities(TextureManager renderEngineIn, RenderItem itemRendererIn, CallbackInfo ci) {
        RenderManager thiz = (RenderManager) (Object) this;

        this.entityRenderMap.put(EntityHamster.class, new RenderHamster(thiz));
        this.entityRenderMap.put(CustomDragon.class, new RenderCustomDragon(thiz));
    }

    @Override
    public double getPosX() {
        return renderPosX;
    }

    @Override
    public double getPosY() {
        return renderPosY;
    }

    @Override
    public double getPosZ() {
        return renderPosZ;
    }

    @Override
    public Map<String, RenderPlayer> getSkinMap() {
        return skinMap;
    }
}

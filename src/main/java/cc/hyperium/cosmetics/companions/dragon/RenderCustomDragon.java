package cc.hyperium.cosmetics.companions.dragon;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderCustomDragon extends RenderLiving<CustomDragon> {
    private static final ResourceLocation enderDragonTextures = new ResourceLocation("textures/entity/enderdragon/dragon.png");
    /**
     * An instance of the dragon model in RenderDragon
     */
    protected ModelCustomDragon modelDragon;

    public RenderCustomDragon(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelCustomDragon(0.0F), 0.5F);
        this.modelDragon = (ModelCustomDragon) this.mainModel;
        this.addLayer(new LayerCustomDragonEyes(this));
    }

    protected void rotateCorpse(CustomDragon bat, float p_77043_2_, float p_77043_3_, float partialTicks) {
        float f = (float) bat.getMovementOffsets(7, partialTicks)[0];
        float f1 = (float) (bat.getMovementOffsets(5, partialTicks)[1] - bat.getMovementOffsets(10, partialTicks)[1]);
        GlStateManager.rotate(-f, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * 10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, 1.0F);

        if (bat.deathTime > 0) {
            float f2 = ((float) bat.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f2 = MathHelper.sqrt_float(f2);

            if (f2 > 1.0F) {
                f2 = 1.0F;
            }

            GlStateManager.rotate(f2 * this.getDeathMaxRotation(bat), 0.0F, 0.0F, 1.0F);
        }
    }

    @Override
    protected void preRenderCallback(CustomDragon entitylivingbaseIn, float partialTickTime) {
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);

        GlStateManager.scale(0.1, 0.1, 0.1);
    }

    /**
     * Renders the model in RenderLiving
     */
    protected void renderModel(CustomDragon entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
        this.bindEntityTexture(entitylivingbaseIn);
        this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);

        if (entitylivingbaseIn.hurtTime > 0) {
            GlStateManager.depthFunc(514);
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color(1.0F, 0.0F, 0.0F, 0.5F);
            this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.depthFunc(515);
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(CustomDragon entity) {
        return enderDragonTextures;
    }
}

class LayerCustomDragonEyes implements LayerRenderer<CustomDragon>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
    private final RenderCustomDragon dragonRenderer;

    public LayerCustomDragonEyes(RenderCustomDragon dragonRendererIn)
    {
        this.dragonRenderer = dragonRendererIn;
    }

    public void doRenderLayer(CustomDragon entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        this.dragonRenderer.bindTexture(TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(1, 1);
        GlStateManager.disableLighting();
        GlStateManager.depthFunc(514);
        int i = 61680;
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.dragonRenderer.getMainModel().render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
        this.dragonRenderer.func_177105_a(entitylivingbaseIn, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.depthFunc(515);
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
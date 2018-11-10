package cc.hyperium.gui;

import cc.hyperium.config.Settings;
import cc.hyperium.utils.HyperiumFontRenderer;
import net.minecraft.client.resources.I18n;

public class GuiHyperiumMultiplayer extends HyperiumGui {
    private static final HyperiumFontRenderer title = new HyperiumFontRenderer(Settings.GUI_FONT, 40F, 0, 1.0F);
    private static final HyperiumFontRenderer subHeading = new HyperiumFontRenderer(Settings.GUI_FONT, 30F, 0, 1.0F);
    private static final HyperiumFontRenderer font = new HyperiumFontRenderer(Settings.GUI_FONT, 25F, 0, 1.0F);

    @Override
    protected void pack() {
        setDrawAlpha(false);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground();
        title.drawString(I18n.format("menu.multiplayer"), 20, 10, 0xffffffff);
    }
}

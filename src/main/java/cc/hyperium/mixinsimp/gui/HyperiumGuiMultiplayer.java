package cc.hyperium.mixinsimp.gui;

import cc.hyperium.gui.GuiHyperiumMultiplayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;

public class HyperiumGuiMultiplayer {
    private GuiMultiplayer parent;

    public HyperiumGuiMultiplayer(GuiMultiplayer parent) {
        this.parent = parent;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiHyperiumMultiplayer());
    }
}

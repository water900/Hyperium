package cc.hyperium.mixins.gui;

import cc.hyperium.mixinsimp.gui.HyperiumGuiMultiplayer;
import cc.hyperium.mixinsimp.renderer.gui.IMixinGuiMultiplayer;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends GuiScreen implements IMixinGuiMultiplayer {

    private HyperiumGuiMultiplayer gui = new HyperiumGuiMultiplayer((GuiMultiplayer) (Object) this);

    @Shadow
    private boolean directConnect;

    @Shadow
    private ServerData selectedServer;

    @Shadow
    private GuiScreen parentScreen;

    @Shadow
    private ServerSelectionList serverListSelector;

    @Shadow
    protected abstract void connectToServer(ServerData server);

    @Override
    public void makeDirectConnect() {
        directConnect = true;
    }

    @Override
    public void setIp(ServerData ip) {
        this.selectedServer = ip;
    }

    /**
     * @author Cubxity
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        gui.drawScreen(mouseX, mouseY, partialTicks);
    }
}

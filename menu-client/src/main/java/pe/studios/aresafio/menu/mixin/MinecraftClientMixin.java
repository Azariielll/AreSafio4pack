package pe.studios.aresafio.menu.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pe.studios.aresafio.menu.AresafioTitleScreen;
import pe.studios.aresafio.menu.MenuTheme;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen aresafio$mainMenu(Screen screen) {
        return screen instanceof TitleScreen && MenuTheme.definition() != null
            ? new AresafioTitleScreen() : screen;
    }
}

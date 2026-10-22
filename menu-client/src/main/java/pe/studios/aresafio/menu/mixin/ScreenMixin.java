package pe.studios.aresafio.menu.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pe.studios.aresafio.menu.MenuBackground;
import pe.studios.aresafio.menu.MenuTheme;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow public int width;
    @Shadow public int height;

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void aresafio$background(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (MenuTheme.themed()) {
            MenuBackground.render(context, width, height, true);
            ci.cancel();
        }
    }
}

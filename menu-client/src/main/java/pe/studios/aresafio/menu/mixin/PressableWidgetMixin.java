package pe.studios.aresafio.menu.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pe.studios.aresafio.menu.MenuTheme;

@Mixin(PressableWidget.class)
public abstract class PressableWidgetMixin extends ClickableWidget {
    protected PressableWidgetMixin() { super(0, 0, 0, 0, Text.empty()); }

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void aresafio$button(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!MenuTheme.themed()) return;
        int color = !active ? 0xFF55535C : isSelected() ? 0xFFFFDB50 : 0xFFC85761;
        context.fill(getX(), getY(), getX() + width, getY() + height, 0xDE100C20);
        context.drawBorder(getX(), getY(), width, height, color);
        ((PressableWidget) (Object) this).drawMessage(context, MinecraftClient.getInstance().textRenderer,
                                                     active ? 0xFFFFFFFF : 0xFF888888);
        ci.cancel();
    }
}

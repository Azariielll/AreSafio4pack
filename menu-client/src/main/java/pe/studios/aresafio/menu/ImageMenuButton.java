package pe.studios.aresafio.menu;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class ImageMenuButton extends ButtonWidget {
    private final MenuDefinition.Sprite sprite;

    public ImageMenuButton(ReferenceLayout.Box box, MenuDefinition.MenuButton button, PressAction action) {
        super(box.x(), box.y(), box.width(), box.height(), Text.literal(button.label()), action,
              DEFAULT_NARRATION_SUPPLIER);
        sprite = button.sprite();
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // La textura original se recorta por UV; no se vuelve a dibujar ni se altera el PNG.
        double ratio = Math.min(width / (double) sprite.sourceWidth(), height / (double) sprite.sourceHeight());
        int w = Math.max(1, (int) Math.round(sprite.sourceWidth() * ratio));
        int h = Math.max(1, (int) Math.round(sprite.sourceHeight() * ratio));
        int x = getX() + (width - w) / 2, y = getY() + (height - h) / 2;
        MenuSprites.draw(context, sprite, x, y, w, h, active ? 1f : 0.35f);
        if (isSelected() && active) context.fill(x, getY() + height - 1, x + w, getY() + height, 0xCCFFFFFF);
    }
}

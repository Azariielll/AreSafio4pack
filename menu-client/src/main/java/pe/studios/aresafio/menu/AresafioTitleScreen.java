package pe.studios.aresafio.menu;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class AresafioTitleScreen extends Screen {
    public AresafioTitleScreen() { super(Text.literal("ARESAFIO 4")); }

    @Override
    protected void init() {
        var definition = MenuTheme.definition();
        if (definition == null) return;
        var layout = ReferenceLayout.fit(width, height, definition);
        for (var button : definition.buttons()) {
            var widget = new ImageMenuButton(layout.box(button.sprite()), button, pressed -> {
                switch (button.action()) {
                    case "singleplayer" -> client.setScreen(new SelectWorldScreen(this));
                    case "multiplayer" -> client.setScreen(client.options.skipMultiplayerWarning
                        ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this));
                    case "options" -> client.setScreen(new OptionsScreen(this, client.options));
                    case "quit" -> client.scheduleStop();
                    default -> throw new IllegalStateException("Acción desconocida");
                }
            });
            if (button.action().equals("multiplayer") && !client.isMultiplayerEnabled()) {
                widget.active = false;
                widget.setTooltip(Tooltip.of(Text.translatable("title.multiplayer.disabled")));
            }
            addDrawableChild(widget);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        var definition = MenuTheme.definition();
        if (definition == null) { client.setScreen(new TitleScreen()); return; }
        MenuBackground.render(context, width, height, false);
        var sprite = definition.logo();
        var box = ReferenceLayout.fit(width, height, definition).box(sprite);
        double ratio = Math.min(box.width() / (double) sprite.sourceWidth(), box.height() / (double) sprite.sourceHeight());
        int w = Math.max(1, (int) Math.round(sprite.sourceWidth() * ratio));
        int h = Math.max(1, (int) Math.round(sprite.sourceHeight() * ratio));
        context.drawTexture(Identifier.of(sprite.texture()), box.x() + (box.width() - w) / 2,
                            box.y() + (box.height() - h) / 2, w, h, (float) sprite.u(), (float) sprite.v(),
                            sprite.sourceWidth(), sprite.sourceHeight(), sprite.textureWidth(), sprite.textureHeight());
        super.render(context, mouseX, mouseY, delta);
    }

    @Override public boolean shouldCloseOnEsc() { return false; }
    @Override public boolean shouldPause() { return false; }

    // Screen.render llama a renderBackground antes de dibujar los hijos.
    // El fondo ya se dibujó antes del logo en render; no debe cubrirlo otra vez.
    @Override public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}
}

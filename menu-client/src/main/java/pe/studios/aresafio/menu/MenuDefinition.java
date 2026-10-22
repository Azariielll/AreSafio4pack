package pe.studios.aresafio.menu;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Datos del pack; no dependen de la escala de interfaz elegida por el jugador. */
public record MenuDefinition(int referenceWidth, int referenceHeight, Sprite logo,
                             List<MenuButton> buttons, Video video) {
    public record Sprite(String texture, int textureWidth, int textureHeight,
                         int u, int v, int sourceWidth, int sourceHeight,
                         double x, double y, double width, double height) {}
    public record MenuButton(String action, String label, Sprite sprite) {}
    public record Video(String prefix, String poster, int frames, int fps, int width, int height) {}

    public void validate() {
        if (referenceWidth <= 0 || referenceHeight <= 0 || buttons == null || buttons.size() != 4) {
            throw new IllegalArgumentException("El menú necesita cuatro botones y una referencia válida");
        }
        validateSprite(logo);
        Set<String> actions = new HashSet<>();
        for (MenuButton button : buttons) {
            if (!Set.of("singleplayer", "multiplayer", "options", "quit").contains(button.action())
                || !actions.add(button.action()) || button.label() == null) {
                throw new IllegalArgumentException("Acción de botón inválida o repetida");
            }
            validateSprite(button.sprite());
        }
        if (video == null || video.prefix() == null || video.poster() == null || video.frames() < 1 || video.frames() > 1800
            || video.fps() < 1 || video.fps() > 60 || video.width() <= 0 || video.height() <= 0) {
            throw new IllegalArgumentException("Secuencia de vídeo inválida");
        }
    }

    private static void validateSprite(Sprite s) {
        if (s == null || s.texture() == null || s.u() < 0 || s.v() < 0 || s.sourceWidth() <= 0
            || s.sourceHeight() <= 0 || s.u() + s.sourceWidth() > s.textureWidth()
            || s.v() + s.sourceHeight() > s.textureHeight() || !Double.isFinite(s.x())
            || !Double.isFinite(s.y()) || !Double.isFinite(s.width()) || !Double.isFinite(s.height())
            || s.width() <= 0 || s.height() <= 0) {
            throw new IllegalArgumentException("Imagen o coordenadas inválidas");
        }
    }
}

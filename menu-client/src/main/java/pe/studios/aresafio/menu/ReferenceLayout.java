package pe.studios.aresafio.menu;

/** Una única transformación para dibujar y pulsar: evita desalinear las zonas de clic. */
public record ReferenceLayout(double scale, double offsetX, double offsetY) {
    public record Box(int x, int y, int width, int height) {}

    public static ReferenceLayout fit(int width, int height, MenuDefinition definition) {
        double scale = Math.min(width / (double) definition.referenceWidth(),
                                height / (double) definition.referenceHeight());
        return new ReferenceLayout(scale, (width - definition.referenceWidth() * scale) / 2,
                                   (height - definition.referenceHeight() * scale) / 2);
    }

    public Box box(MenuDefinition.Sprite sprite) {
        return new Box((int) Math.round(offsetX + sprite.x() * scale),
                       (int) Math.round(offsetY + sprite.y() * scale),
                       Math.max(1, (int) Math.round(sprite.width() * scale)),
                       Math.max(1, (int) Math.round(sprite.height() * scale)));
    }
}

package pe.studios.aresafio.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Decodifica un solo fotograma por adelantado; no conserva el vídeo completo en RAM/VRAM. */
public final class MenuBackground {
    private static final Identifier DYNAMIC = Identifier.of("aresafio_menu", "video_frame");
    private static final ExecutorService DECODER = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "aresafio-menu-video"); t.setDaemon(true); return t;
    });
    private static CompletableFuture<NativeImage> pending;
    private static NativeImageBackedTexture texture;
    private static long started = System.nanoTime();
    private static int requestedFrame = -1;
    private static boolean failed;

    private MenuBackground() {}

    public static void reset() {
        if (pending != null) pending.thenAccept(image -> { if (image != null) image.close(); });
        pending = null;
        if (texture != null) MinecraftClient.getInstance().getTextureManager().destroyTexture(DYNAMIC);
        texture = null; requestedFrame = -1; failed = false; started = System.nanoTime();
    }

    public static void render(DrawContext context, int width, int height, boolean darken) {
        var definition = MenuTheme.definition();
        if (definition == null) return;
        var video = definition.video();
        if (pending != null && pending.isDone()) {
            NativeImage image = pending.join(); pending = null;
            if (image != null) {
                if (texture == null) {
                    texture = new NativeImageBackedTexture(image);
                    MinecraftClient.getInstance().getTextureManager().registerTexture(DYNAMIC, texture);
                } else { texture.setImage(image); texture.upload(); }
                texture.setFilter(true, false);
            } else { failed = true; }
        }
        int frame = (int) (((System.nanoTime() - started) / 1_000_000_000.0 * video.fps()) % video.frames());
        if (!failed && pending == null && frame != requestedFrame) {
            requestedFrame = frame;
            var manager = MinecraftClient.getInstance().getResourceManager();
            var id = Identifier.of(video.prefix() + String.format(java.util.Locale.ROOT, "%03d.jpg", frame));
            pending = CompletableFuture.supplyAsync(() -> {
                try (var in = manager.getResourceOrThrow(id).getInputStream()) {
                    return VideoFrameDecoder.read(in, video.width(), video.height());
                } catch (Exception e) {
                    MenuTheme.LOGGER.warn("No se pudo leer el vídeo del menú; se usará la imagen fija", e);
                    return null;
                }
            }, DECODER);
        }
        Identifier image = texture == null ? Identifier.of(video.poster()) : DYNAMIC;
        double scale = Math.max(width / (double) video.width(), height / (double) video.height());
        int drawWidth = (int) Math.ceil(video.width() * scale);
        int drawHeight = (int) Math.ceil(video.height() * scale);
        context.drawTexture(image, (width - drawWidth) / 2, (height - drawHeight) / 2,
                            drawWidth, drawHeight, 0f, 0f, video.width(), video.height(), video.width(), video.height());
        if (darken) context.fill(0, 0, width, height, 0x99070A16);
    }
}

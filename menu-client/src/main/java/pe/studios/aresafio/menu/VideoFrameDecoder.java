package pe.studios.aresafio.menu;

import net.minecraft.client.texture.NativeImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import pe.studios.aresafio.menu.mixin.NativeImageAccessor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/** STB ya forma parte de Minecraft; no se incorpora otra biblioteca de vídeo. */
public final class VideoFrameDecoder {
    private VideoFrameDecoder() {}

    public static NativeImage read(InputStream in, int expectedWidth, int expectedHeight) throws IOException {
        byte[] bytes = in.readAllBytes();
        ByteBuffer encoded = MemoryUtil.memAlloc(bytes.length);
        encoded.put(bytes).flip();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var width = stack.mallocInt(1); var height = stack.mallocInt(1); var channels = stack.mallocInt(1);
            ByteBuffer pixels = STBImage.stbi_load_from_memory(encoded, width, height, channels, 4);
            if (pixels == null) throw new IOException("Fotograma inválido: " + STBImage.stbi_failure_reason());
            try {
                if (width.get(0) != expectedWidth || height.get(0) != expectedHeight) {
                    throw new IOException("Dimensiones de fotograma distintas de las declaradas");
                }
                NativeImage image = new NativeImage(expectedWidth, expectedHeight, false);
                try {
                    MemoryUtil.memCopy(MemoryUtil.memAddress(pixels),
                        ((NativeImageAccessor) (Object) image).aresafio$pointer(), (long) expectedWidth * expectedHeight * 4);
                    return image;
                } catch (Throwable t) { image.close(); throw t; }
            } finally { STBImage.stbi_image_free(pixels); }
        } finally { MemoryUtil.memFree(encoded); }
    }
}

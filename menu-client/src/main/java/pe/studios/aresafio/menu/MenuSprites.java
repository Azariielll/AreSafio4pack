package pe.studios.aresafio.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import java.util.HashMap;
import java.util.Map;

/** PNG originales: transparencia real y reducción con mipmaps, sin alterar píxeles ni posiciones. */
public final class MenuSprites {
    private static final Map<Identifier, Integer> FILTERED = new HashMap<>();
    private MenuSprites() {}

    public static void reset() { FILTERED.clear(); }

    public static void draw(DrawContext context, MenuDefinition.Sprite sprite,
                            int x, int y, int width, int height, float alpha) {
        RenderSystem.assertOnRenderThread();
        Identifier id = Identifier.of(sprite.texture());
        var texture = MinecraftClient.getInstance().getTextureManager().getTexture(id);
        int glId = texture.getGlId();
        if (!Integer.valueOf(glId).equals(FILTERED.get(id))) {
            texture.bindTexture();
            int levels = 31 - Integer.numberOfLeadingZeros(Math.max(sprite.textureWidth(), sprite.textureHeight()));
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, levels);
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            texture.setFilter(true, true);
            FILTERED.put(id, glId);
        }
        // drawTexture no activa el blending por sí mismo: sin él, alfa 1/255 se veía opaco.
        context.setShaderColor(1f, 1f, 1f, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        try {
            context.drawTexture(id, x, y, width, height, (float) sprite.u(), (float) sprite.v(),
                                sprite.sourceWidth(), sprite.sourceHeight(), sprite.textureWidth(), sprite.textureHeight());
        } finally {
            context.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
        }
    }
}

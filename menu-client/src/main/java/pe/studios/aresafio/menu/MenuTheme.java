package pe.studios.aresafio.menu;

import com.google.gson.Gson;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class MenuTheme {
    public static final Logger LOGGER = LoggerFactory.getLogger("aresafio-menu");
    public static final Identifier DEFINITION = Identifier.of("aresafio_menu", "menu.json");
    private static volatile MenuDefinition definition;

    private MenuTheme() {}
    public static MenuDefinition definition() { return definition; }

    public static void reload(ResourceManager resources) {
        definition = null;
        var resource = resources.getResource(DEFINITION);
        if (resource.isEmpty()) return;
        try (var reader = new InputStreamReader(resource.get().getInputStream(), StandardCharsets.UTF_8)) {
            var loaded = new Gson().fromJson(reader, MenuDefinition.class);
            loaded.validate();
            require(resources, loaded.logo().texture());
            for (var button : loaded.buttons()) require(resources, button.sprite().texture());
            require(resources, loaded.video().prefix() + "000.jpg");
            require(resources, loaded.video().poster());
            require(resources, "aresafio_menu:sounds/music/menu.ogg");
            definition = loaded;
        } catch (Exception e) {
            LOGGER.warn("Pack de menús inválido; se conserva la interfaz vanilla", e);
        }
    }

    private static void require(ResourceManager resources, String path) {
        if (resources.getResource(Identifier.of(path)).isEmpty()) {
            throw new IllegalArgumentException("Falta el recurso " + path);
        }
    }

    public static boolean themed() {
        return definition != null
            && MinecraftClient.getInstance().currentScreen instanceof AresafioTitleScreen;
    }
}

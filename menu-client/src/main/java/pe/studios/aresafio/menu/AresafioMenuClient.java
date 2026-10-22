package pe.studios.aresafio.menu;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public final class AresafioMenuClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
            new SimpleSynchronousResourceReloadListener() {
                public Identifier getFabricId() { return Identifier.of("aresafio_menu", "reload"); }
                public java.util.Collection<Identifier> getFabricDependencies() {
                    return java.util.Set.of(ResourceReloadListenerKeys.TEXTURES, ResourceReloadListenerKeys.SOUNDS);
                }
                public void reload(ResourceManager manager) {
                    MenuSprites.reset();
                    MenuBackground.reset();
                    MenuMusic.stop();
                    MenuTheme.reload(manager);
                }
            });
        ClientTickEvents.END_CLIENT_TICK.register(client -> MenuMusic.tick());
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            MenuMusic.stop();
            MenuBackground.reset();
        });
    }
}

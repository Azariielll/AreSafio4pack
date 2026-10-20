package pe.studios.aresafio;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * Servidor Fabric 1.21.1: envia TAB y renueva su footer cada 5 s.
 * No contiene clases de cliente y no obliga a instalar un mod en el cliente.
 * La tipografia de los glifos se distribuye por resource pack.
 */
public final class AresafioGuiMod implements ModInitializer {
    private static final int UPDATE_TICKS = 100;
    private int ticks = 0;

    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            updateTab(handler.getPlayer(), server);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (++ticks >= UPDATE_TICKS) {
                ticks = 0;
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    updateTab(player, server);
                }
            }
        });
    }

    private static void updateTab(ServerPlayerEntity player, MinecraftServer server) {
        // TAB dibuja cada linea con separacion normal de texto, aunque el logo
        // sea grande: los saltos en blanco reservan espacio para el glifo.
        MutableText logo = Text.literal("\uE001")
            .setStyle(Style.EMPTY.withFont(Identifier.of("aresafio", "logo")));

        MutableText header = Text.literal("\n\n\n\n")
            .append(logo)
            .append(Text.literal("\n\n\n\n\n\n\n"));

        long day = (server.getOverworld().getTimeOfDay() / 24000L) + 1L;
        MutableText footer = Text.literal("Día " + day)
            .setStyle(Style.EMPTY.withFont(Identifier.ofVanilla("default")))
            .formatted(Formatting.WHITE);

        player.networkHandler.sendPacket(new PlayerListHeaderS2CPacket(header, footer));
    }
}

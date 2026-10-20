package pe.studios.aresafio.mixin;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Cambia solo la decoración del chat normal, conservando el mensaje firmado.
 * El tipo sincronizado al cliente separa ruleta, nombre y contenido.
 */
@Mixin(PlayerManager.class)
public abstract class PlayerManagerChatMixin {
    @Unique
    private static final RegistryKey<MessageType> ARESAFIO_VANILLA_CHAT = RegistryKey.of(
        RegistryKeys.MESSAGE_TYPE, Identifier.ofVanilla("chat"));
    @Unique
    private static final RegistryKey<MessageType> ARESAFIO_RULETTE_CHAT = RegistryKey.of(
        RegistryKeys.MESSAGE_TYPE, Identifier.of("aresafio", "roulette_chat"));

    @ModifyVariable(
        method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V",
        at = @At("HEAD"),
        argsOnly = true
    )
    private MessageType.Parameters aresafio$decorateChatName(
        MessageType.Parameters original, SignedMessage message,
        ServerPlayerEntity sender, MessageType.Parameters parameters
    ) {
        if (!original.type().matchesKey(ARESAFIO_VANILLA_CHAT)) {
            return original;
        }

        MutableText wheel = Text.literal("\uE000")
            .setStyle(Style.EMPTY.withFont(Identifier.of("aresafio", "icons")));
        MutableText name = original.name().copy();
        name.setStyle(name.getStyle().withFont(Identifier.ofVanilla("default")));

        // "target" es únicamente el icono; el nombre nunca hereda su fuente.
        // No se cancela ni se reenvía el mensaje: el envío firmado sigue intacto.
        return MessageType.params(ARESAFIO_RULETTE_CHAT, sender.getRegistryManager(), name)
            .withTargetName(wheel);
    }
}

package pe.studios.aresafio.mixin;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Solo cambia el NOMBRE mostrado en mensajes firmados del chat normal.
 * No toca el texto firmado, ni los equipos de scoreboard, ni los nicks del TAB.
 */
@Mixin(PlayerManager.class)
public abstract class PlayerManagerChatMixin {
    @ModifyVariable(
        method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V",
        at = @At("HEAD"),
        argsOnly = true
    )
    private MessageType.Parameters aresafio$decorateChatName(MessageType.Parameters original) {
        MutableText wheel = Text.literal("\uE000")
            .setStyle(Style.EMPTY.withFont(Identifier.of("aresafio", "icons")));
        MutableText decorated = wheel.append(Text.literal(" ")).append(original.name());
        return new MessageType.Parameters(original.type(), decorated, original.targetName());
    }
}

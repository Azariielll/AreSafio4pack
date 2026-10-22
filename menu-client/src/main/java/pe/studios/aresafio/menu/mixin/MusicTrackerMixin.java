package pe.studios.aresafio.menu.mixin;

import net.minecraft.client.sound.MusicTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pe.studios.aresafio.menu.MenuTheme;

@Mixin(MusicTracker.class)
public abstract class MusicTrackerMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void aresafio$menuMusic(CallbackInfo ci) {
        if (MenuTheme.themed()) ci.cancel();
    }
}

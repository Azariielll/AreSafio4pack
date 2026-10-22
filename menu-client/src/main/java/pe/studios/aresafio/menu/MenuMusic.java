package pe.studios.aresafio.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public final class MenuMusic {
    private static SoundInstance playing;
    private static int startGrace;
    private MenuMusic() {}

    public static void tick() {
        var client = MinecraftClient.getInstance();
        if (!MenuTheme.themed()) { stop(); return; }
        if (playing != null && !client.getSoundManager().isPlaying(playing)) {
            if (--startGrace > 0) return;
            client.getSoundManager().stop(playing);
            playing = null;
        } else if (playing != null) { startGrace = 40; }
        if (playing == null) {
            client.getMusicTracker().stop();
            playing = new Loop();
            client.getSoundManager().play(playing);
            startGrace = 40;
        }
    }

    public static void stop() {
        if (playing != null) MinecraftClient.getInstance().getSoundManager().stop(playing);
        playing = null;
        startGrace = 0;
    }

    private static final class Loop extends AbstractSoundInstance {
        Loop() {
            super(Identifier.of("aresafio_menu", "menu_music"), SoundCategory.MUSIC, Random.create());
            volume = 1f; pitch = 1f; repeat = true; repeatDelay = 0;
            relative = true; attenuationType = AttenuationType.NONE;
        }
    }
}

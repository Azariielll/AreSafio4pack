package pe.studios.aresafio.menu;

import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipFile;

/** Comprueba el pack entregado y las zonas de clic, sin introducir otra biblioteca de pruebas. */
public final class MenuLayoutTest {
    public static void main(String[] args) throws Exception {
        MenuDefinition definition;
        try (var zip = new ZipFile(args[0])) {
            var entry = zip.getEntry("assets/aresafio_menu/menu.json");
            definition = new Gson().fromJson(new String(zip.getInputStream(entry).readAllBytes(),
                                            StandardCharsets.UTF_8), MenuDefinition.class);
        }
        definition.validate();
        int[][] sizes = {{1919,904}, {1920,1080}, {2560,1080}, {480,270}, {320,240}, {854,480}, {640,480}};
        for (int[] size : sizes) {
            var layout = ReferenceLayout.fit(size[0], size[1], definition);
            int previousBottom = 0;
            for (var button : definition.buttons()) {
                var box = layout.box(button.sprite());
                check(box.x() >= 0 && box.y() >= previousBottom, "Botones fuera de orden o solapados");
                check(box.x() + box.width() <= size[0] && box.y() + box.height() <= size[1], "Botón fuera de pantalla");
                previousBottom = box.y() + box.height();
            }
        }
        var exact = ReferenceLayout.fit(1919, 904, definition);
        check(exact.scale() == 1 && exact.offsetX() == 0 && exact.offsetY() == 0, "Referencia desplazada");
        var actions = definition.buttons().stream().map(MenuDefinition.MenuButton::action).toList();
        check(actions.equals(java.util.List.of("singleplayer", "multiplayer", "options", "quit")), "Acciones alteradas");
        System.out.println("PASS: cuatro acciones; coordenadas y clics válidos en siete resoluciones/escalas");
    }
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}

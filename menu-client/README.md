# ARESAFIO — Menús beta para Minecraft Java 1.21.1

Esta ampliación es un módulo **de cliente** separado. El JAR de servidor, la ruleta del chat, sus mensajes firmados y el TAB conservan sus archivos y comportamiento. El pack V1 del servidor permanece intacto.

## Instalar la beta

1. Usa Minecraft **Java 1.21.1**, Java 21 y Fabric Loader 0.16.14 o compatible.
2. Instala Fabric API para 1.21.1 en el **cliente**.
3. Copia `menu-client/build/libs/aresafio-menu-client-1.0.0-beta.1.jar` a la carpeta `mods/` de tu instalación de Minecraft. Este JAR no se instala en el servidor.
4. Copia `ARESAFIO_GUI_MenuBeta_ResourcePack.zip` a `resourcepacks/` y actívalo desde Opciones → Paquetes de recursos. Ponlo por encima de otros packs que cambien menús.
5. Regresa al inicio. Si el pack no está activo o está incompleto, el módulo conserva el menú vanilla.

El servidor sigue usando `aresafio-gui-server-1.0.0.jar` y `ARESAFIO_GUI_V1_ResourcePack.zip`. Sus clientes no necesitan esta beta para que funcionen chat y TAB. El menú no se puede cambiar antes de conectarse con un pack descargado únicamente del servidor.

## Qué incluye

- Cuatro botones en el inicio: **Jugar**, **Multijugador**, **Opciones** y **Salir**, con los PNG originales suministrados. Usan las pantallas y acciones de Minecraft, incluidos sus avisos y restricciones de multijugador.
- Logo ARESAFIO original, incluido PE STUDIOS, conservando sus proporciones. Está centrado en la zona del logo de la referencia; su silueta y proporciones son las de ARESAFIO, no las del logotipo Dedsafio.
- Columna del inicio centrada en X=504 de la referencia de 1919×904. Las cuatro filas se sitúan a Y=540,620,700,780 (cajas de 50 px) para incorporar el cuarto botón solicitado. La referencia tenía tres filas, por lo que no puede mantener simultáneamente las tres separaciones originales y añadir una cuarta.
- Vídeo original en bucle de doce segundos, adaptado a 1280×682 y 30 fotogramas/s. Solo se decodifica un fotograma por adelantado, fuera del hilo de dibujo. El fondo cubre la ventana sin deformarse, con recorte según la relación de aspecto.
- Música original convertida de MP3 a OGG Vorbis, en streaming y en bucle; respeta el volumen de música de Minecraft y se detiene al volver al juego. No se mezclan dos pistas de música de menú.
- Fondo y estilo de botones en las pantallas de menú fuera del mundo; también en pausa, opciones y sus subpantallas, paquetes, estadísticas y avances. Las demás pantallas conservan sus posiciones y acciones vanilla. Las listas, deslizadores, campos e iconos especializados mantienen sus controles originales.
- Inventarios, HUD, chat, nombres, cabezas, indicadores de conexión y texturas de juego no se modifican.

No se copia el arte Dedsafio ni la marca EUFONIA de la captura: la captura es referencia de distribución y los recursos visibles son los aportados para ARESAFIO. El icono X superpuesto en la captura no se incorpora como un control de Minecraft.

## Actualizar los recursos

Los originales están en `menu-assets/originales/menu.zip`; la referencia está en `menu-assets/referencia/menu.png`. Las posiciones y regiones visibles se guardan en `assets/aresafio_menu/menu.json` dentro del pack generado. Se usa el mismo cálculo para dibujar el botón y para su zona de clic, también al cambiar resolución o escala de interfaz.

Para reconstruir la beta a partir de esos originales, instala Python 3, Pillow y FFmpeg y ejecuta desde la raíz:

```bash
python3 tools/build_menu_pack.py
```

El script no modifica el ZIP V1, sus fuentes ni sus PNG; genera un ZIP combinado separado y actualiza `RESOURCE_PACK_MENU_BETA_SHA1.txt`. Para verificarlo sin reconstruir:

```bash
python3 tools/build_menu_pack.py --check
```

## Compilar y comprobar

Desde la raíz, con Java 21 y Gradle 8.12:

```bash
gradle build
```

Esto compila servidor y cliente y ejecuta `:menu-client:verifyMenuLayout`, que comprueba cuatro acciones y zonas de clic en siete resoluciones/escalas. No requiere JUnit ni dependencias nuevas. GitHub Actions conserva el artefacto de servidor y publica por separado el JAR de cliente y el pack beta.

Para obtener un ZIP instalable con las carpetas `mods/` y `resourcepacks/`, ejecuta `gradle :menu-client:betaDistribution`. El resultado está en `build/distributions/ARESAFIO_MenuBeta_1.21.1.zip`.

La prueba de geometría y la validación del pack no sustituyen la comprobación en Minecraft: revisar inicio, multijugador, mundos, opciones/subpantallas, pausa, carga de packs, escalas de interfaz, navegación con teclado, música y vuelta al juego. También hay que comprobar en un servidor real que chat y TAB siguen funcionando.

## Verificación de esta beta

- `gradle build :menu-client:betaDistribution`: compilación y distribución correctas.
- Pack: CRC, SHA-1, JSON, 360 fotogramas y audio presentes; los PNG suministrados y todos los recursos de chat/TAB del pack V1 permanecen idénticos.
- Cliente real de desarrollo Fabric 1.21.1 en pantalla virtual: inicio, vídeo, reproducción de música mediante OpenAL, clics en Opciones, Multijugador y Jugar, creación de un mundo temporal, pausa dentro del mundo y regreso al juego sin tema ni música de menú. Cambio de ventana de 1919×904 a 1280×720 con clics correctos. Desactivar/reactivar el pack recupera respectivamente el menú vanilla y el personalizado. Nueve capturas en `menu-client/build/validation/` (salidas locales, no incluidas en el JAR).
- Regresión del servidor: carga del mixin y formato/fuentes del chat comprobados de nuevo bajo Fabric. Los archivos Java/JSON del servidor, su pack V1 y su SHA-1 no se modificaron.

Pendiente de prueba manual: escucha de la música en un dispositivo físico, navegación completa con teclado, todas las subpantallas de opciones y conexión con cuenta autenticada a un servidor remoto para comprobar allí chat firmado y TAB. La prueba de audio utilizó un dispositivo OpenAL virtual y no certifica cómo se escucha la mezcla.

# ARESAFIO — Menú principal beta 3 para Minecraft Java 1.21.1

Esta ampliación es un módulo **de cliente** separado y personaliza únicamente el menú principal. Opciones y sus subpantallas, Multijugador, selección de mundos, pausa y las demás interfaces conservan el aspecto y el comportamiento vanilla. El JAR de servidor, la ruleta del chat, sus mensajes firmados, el TAB y el pack V1 del servidor permanecen intactos.

## Instalar o actualizar a beta 3

Descargas directas de esta beta desde GitHub, sin enlaces al entorno de desarrollo:

- [JAR beta 3 del cliente](https://github.com/Azariielll/AreSafio4pack/raw/refs/heads/menu-beta3-parche/descargas/beta3/aresafio-menu-client-1.0.0-beta.3.jar) → `mods/` de tu instancia.
- [Resource Pack beta 3](https://github.com/Azariielll/AreSafio4pack/raw/refs/heads/menu-beta3-parche/ARESAFIO_GUI_MenuBeta_ResourcePack.zip) → `resourcepacks/` de tu instancia, sin descomprimir.

Si el repositorio requiere acceso, abre los enlaces con tu sesión de GitHub. El JAR publicado es una copia exacta del artefacto compilado y verificado de beta 3.

1. Usa Minecraft **Java 1.21.1**, Java 21 y Fabric Loader 0.16.14 o compatible.
2. Instala Fabric API para 1.21.1 en el **cliente**.
3. Cierra Minecraft. Retira de `mods/` del cliente tanto `aresafio-menu-client-1.0.0-beta.1.jar` como `aresafio-menu-client-1.0.0-beta.2.jar`, si están presentes. Copia allí `menu-client/build/libs/aresafio-menu-client-1.0.0-beta.3.jar`. Conserva una sola versión del módulo. Este JAR no se instala en el servidor.
4. Sustituye el ZIP anterior de `resourcepacks/` por el nuevo `ARESAFIO_GUI_MenuBeta_ResourcePack.zip`, sin descomprimirlo, y actívalo desde Opciones → Paquetes de recursos. Si conservaste otra copia con un nombre distinto, desactívala.
5. Regresa al inicio. Si el pack no está activo o está incompleto, el módulo conserva el menú vanilla.

El ZIP instalable `ARESAFIO_MenuBeta3_1.21.1.zip` incluye las carpetas `mods/`, `resourcepacks/` y las instrucciones. Si descargaste el artefacto completo de GitHub Actions, este ZIP está dentro de `build/distributions/`: extrae primero el ZIP instalable y copia cada archivo a la carpeta correspondiente de tu instancia. El ZIP del resource pack se deja comprimido.

El servidor sigue usando `aresafio-gui-server-1.0.0.jar` y `ARESAFIO_GUI_V1_ResourcePack.zip`. Sus clientes no necesitan esta beta para que funcionen chat y TAB. Un pack descargado únicamente al conectarse al servidor no puede cambiar el menú principal antes de la conexión.

## Cambios de beta 3

- Referencia de pantalla de **1920×1080**. El logo ARESAFIO original, incluido PE STUDIOS, mantiene su tamaño de 530×530 píxeles de dibujo y se desplaza 64 píxeles a la izquierda; la columna queda centrada en X=440.
- Los cuatro botones **Jugar**, **Multijugador**, **Opciones** y **Salir** quedan centrados debajo del logo y se reducen un 10 %. Sus cajas de 45 píxeles de alto comienzan en Y=630,5; 710,5; 790,5 y 870,5. Se conservan los PNG originales, sus proporciones, la mezcla alfa y los mipmaps.
- El vídeo original de 1920×1024 se adapta completo a 1920×1080 con Lanczos y un enfoque moderado de luminancia de 0,35. No se recortan los laterales, donde aparece PE STUDIOS. Conserva el bucle de doce segundos, 30 fotogramas/s y JPEG 4:4:4 con calidad de conversión `q=4`.
- Fondo, logo, botones y música personalizados se limitan al inicio. Al abrir otra pantalla se detiene la pista personalizada y vuelve a funcionar el sistema de música vanilla. Opciones, Multijugador, mundos, pausa y las demás pantallas conservan sus fondos, controles y distribución vanilla.

El vídeo suministrado ya está comprimido en H.264 a aproximadamente 3,6 Mbit/s. El ajuste de encuadre y el enfoque mejoran su presentación, pero no recuperan detalle que no exista en el archivo original ni equivalen a una fuente nativa de mayor calidad.

La corrección de transparencia de beta 2 se conserva: los píxeles casi transparentes del logo y los botones se mezclan con el fondo en lugar de aparecer como manchas opacas. No se regenera el logo ni se modifican los PNG originales. El vídeo se decodifica con un solo fotograma por adelantado, fuera del hilo de dibujo; no se guarda el bucle completo en RAM o VRAM.

## Actualizar los recursos

Los originales están en `menu-assets/originales/menu.zip`; la referencia inicial está en `menu-assets/referencia/menu.png`. Las posiciones y regiones visibles se guardan en `assets/aresafio_menu/menu.json` dentro del pack generado. El dibujo y las zonas de clic comparten el mismo cálculo, también al cambiar resolución o escala de interfaz.

Para reconstruir el pack a partir de los originales, instala Python 3, Pillow y FFmpeg y ejecuta desde la raíz:

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
gradle build :menu-client:betaDistribution
```

Esto compila servidor y cliente, ejecuta `:menu-client:verifyMenuLayout` y genera `build/distributions/ARESAFIO_MenuBeta3_1.21.1.zip`. La comprobación de geometría valida las cuatro acciones y las zonas de clic en siete resoluciones/escalas. No requiere JUnit ni dependencias nuevas. GitHub Actions conserva el artefacto de servidor y publica por separado el JAR de cliente y el pack beta.

## Verificación de beta 3

`gradle build :menu-client:betaDistribution` completado correctamente. La geometría actualizada pasó las comprobaciones en siete resoluciones/escalas: tamaño del logo conservado y botones centrados debajo. El pack pasó CRC, SHA-1, JSON, audio, 360 JPEG baseline y póster de 1920×1080, coincidencia de los PNG originales y conservación de todos los recursos V1. Se comprobó también que la distribución contiene el JAR beta 3 y el pack correctos, sin recursos que sustituyan globalmente la interfaz vanilla.

Prueba en Minecraft Fabric 1.21.1 real sobre pantalla virtual Xvfb/Mesa: inicio a 1920×1080, 1280×720 y 1919×904, clics, vídeo decodificado a 1920×1080, transparencia y mipmaps. Las capturas muestran PE STUDIOS completo en la esquina inferior izquierda, el logo del mismo tamaño desplazado a la izquierda y los botones reducidos y centrados. Opciones, Gráficos, Multijugador, selección de mundos y pausa dentro de un mundo temporal conservan el aspecto vanilla y no reproducen la pista personalizada. Volver al inicio recupera el tema y su música. Desactivar/reactivar el pack recupera correctamente los menús vanilla/personalizado. Diez capturas y el resultado están en `menu-client/build/beta3-validation/`, como salidas locales fuera del JAR.

Los resultados históricos siguientes corresponden a beta 2 y no certifican el parche beta 3.

## Histórico: verificación de beta 2

La beta 2 compiló con `gradle build :menu-client:betaDistribution` y pasó las comprobaciones de CRC, SHA-1, JSON, 360 fotogramas, audio y coincidencia de los PNG originales y recursos V1. Se probó en Minecraft Java 1.21.1 sobre Xvfb/Mesa: transparencia, mipmaps, vídeo a 1920×1024, tres resoluciones, clics en Opciones y recarga del pack. Las salidas locales de esa revisión están en `menu-client/build/quality-validation/`, fuera del JAR.

No se ha comprobado el parche beta 3 en un servidor remoto ni se ha escuchado la música en un dispositivo físico. Las pruebas históricas de audio utilizaron un dispositivo OpenAL virtual.

# ARESAFIO GUI — V1 (Fabric 1.21.1)

La beta opcional de menús tiene un [módulo de cliente independiente](menu-client/README.md) y un pack local `ARESAFIO_GUI_MenuBeta_ResourcePack.zip`. No cambia la arquitectura ni la instalación del mod de servidor descrito aquí.

Esta versión está diseñada para **servidor**: no es un mod visual de cliente.
Necesita también el ZIP del **Resource Pack**.

## Funcionalidades
- Al pulsar TAB: logo original suministrado por PE Studios y contador dinámico `Día X`, sin título de texto adicional ni contador de jugadores.
- En chat normal: `[RULETA] <NombreJugador> mensaje`, con la fuente personalizada solo en la ruleta y tipografía vanilla en el nombre, SIN cambiar el contenido firmado del mensaje.
- No cambia etiquetas sobre jugadores ni usa teams del scoreboard.
- Sin mods adicionales instalados en los clientes (sí se debe aceptar el pack).

## Compilar
Java **21**, conexión a internet y Gradle **8.12** (o Gradle compatible):

```bash
gradle build
```

Mod compilado: `build/libs/aresafio-gui-server-1.0.0.jar`.
También está incluido `.github/workflows/build.yml` para compilar en GitHub Actions sin instalar Gradle localmente. El proyecto fuente **no contiene un JAR ya compilado**.

## Instalar
1. Haz backup de tu servidor y apágalo.
2. Servidor: **Fabric 1.21.1** con Fabric API instalado. Copia solo el JAR compilado a `mods/`.
3. Sube `ARESAFIO_GUI_V1_ResourcePack.zip` a una dirección pública de **descarga directa**, que permita descargar el ZIP sin login, sin página HTML y sin redirecciones problemáticas.
4. En `server.properties`:

```properties
resource-pack=https://TU-DOMINIO/ARESAFIO_GUI_V1_ResourcePack.zip
resource-pack-sha1=REEMPLAZAR_CON_SHA1_DEL_ZIP
require-resource-pack=true
```

5. Reinicia el servidor, acepta el pack en Minecraft y prueba TAB y chat.
6. Si modificas el ZIP del resource pack, recalcula su SHA-1.

## Qué NO cubre esta V1
- No reemplaza el fondo geométrico de TAB (GUI vanilla no permite hacerlo mediante solo un pack).
- No fuerza 4 columnas: lo determina la cantidad de jugadores y el cliente.
- No cambia los anuncios automáticos de conexión/desconexión ni los mensajes de otros plugins.
- Es posible que otros mods de chat o TAB anulen estos cambios si reescriben los mismos paquetes.
- El contador "Día" usa el tiempo de juego del overworld, no el día de un evento externo.

## Recursos
Los PNG originales aportados por el usuario están en `recursos-originales/`. Los PNG procesados están en el ZIP del Resource Pack. No se utiliza una imagen de fondo inventada.

## Estado de pruebas
Compilación verificada con Java 21 y Gradle 8.12. Validado el Resource Pack V1 (JSON, PNG, compresión y SHA-1) y comprobados bajo Fabric 1.21.1 el formato del chat, las fuentes de sus componentes y la carga del mixin. La prueba de chat firmado entre jugadores y la comprobación visual del TAB en un servidor real siguen pendientes. Las comprobaciones de la beta de menús se documentan por separado en `menu-client/README.md`.

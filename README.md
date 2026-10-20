# ARESAFIO GUI — V1 (Fabric 1.21.1)

Esta versión está diseñada para **servidor**: no es un mod visual de cliente.
Necesita también el ZIP del **Resource Pack**.

## Funcionalidades
- Al pulsar TAB: logo suministrado por PE Studios como encabezado, título ARESAFIO 4, y contador de día/máximo jugadores.
- En chat normal: ruleta antes del nombre de quien escribe, SIN cambiar el contenido firmado del mensaje.
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
Validado el contenido y estructura del Resource Pack (JSON, PNG, compresión, SHA-1), pero **no se pudo compilar ni ejecutar el mod** en este entorno sin acceso a los repositorios de Fabric/Gradle. Es un proyecto fuente pendiente de compilación y prueba en servidor.

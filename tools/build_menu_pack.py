#!/usr/bin/env python3
"""Convierte los medios originales y crea un pack combinado sin modificar el pack V1."""
import argparse
import hashlib
import json
import pathlib
import shutil
import subprocess
import tempfile
import zipfile

ROOT = pathlib.Path(__file__).resolve().parents[1]
SOURCES = {
    "singleplayer": "JUGAR en neón rojo futurista.png",
    "multiplayer": "Logotipo neón futurista MULTIJUGADOR.png",
    "options": "Opciones_ Título Neón Futurista.png",
    "quit": "Palabra SALIR en neón verde футurista.png",
}


def dump(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def sprite(path, texture, x, y, width, height):
    from PIL import Image
    with Image.open(path) as image:
        tw, th = image.size
        # Solo coordenadas UV: los bytes del PNG original se conservan.
        alpha = image.getchannel("A").point(lambda a: 255 if a >= 128 else 0)
        box = alpha.getbbox()
        if box is None:
            raise ValueError(f"Imagen vacía: {path}")
    u, v, right, bottom = box
    return dict(texture=texture, textureWidth=tw, textureHeight=th, u=u, v=v,
                sourceWidth=right-u, sourceHeight=bottom-v, x=x, y=y, width=width, height=height)


def build():
    from PIL import Image
    output = ROOT / "ARESAFIO_GUI_MenuBeta_ResourcePack.zip"
    with tempfile.TemporaryDirectory(prefix="aresafio-menu-") as tmp:
        tmp = pathlib.Path(tmp)
        source = tmp / "originales"
        source.mkdir()
        with zipfile.ZipFile(ROOT / "menu-assets/originales/menu.zip") as archive:
            for info in archive.infolist():
                path = source / info.filename
                if not path.resolve().is_relative_to(source.resolve()):
                    raise ValueError("Ruta insegura dentro del ZIP")
                if not info.is_dir():
                    path.parent.mkdir(parents=True, exist_ok=True)
                    path.write_bytes(archive.read(info))
        pack = tmp / "pack"
        pack.mkdir()
        # El pack original es parte de la beta local, sin reescribir ninguna fuente/textura de TAB/chat.
        with zipfile.ZipFile(ROOT / "ARESAFIO_GUI_V1_ResourcePack.zip") as archive:
            for info in archive.infolist():
                path = pack / info.filename
                if not path.resolve().is_relative_to(pack.resolve()):
                    raise ValueError("Ruta insegura en el pack V1")
                if not info.is_dir():
                    path.parent.mkdir(parents=True, exist_ok=True)
                    path.write_bytes(archive.read(info))
        assets = pack / "assets/aresafio_menu"
        textures = assets / "textures/menu"
        backgrounds = textures / "background"
        backgrounds.mkdir(parents=True)
        (assets / "sounds/music").mkdir(parents=True)
        video_info = json.loads(subprocess.check_output([
            "ffprobe", "-v", "error", "-select_streams", "v:0", "-show_entries", "stream=width,height",
            "-of", "json", str(source / "Planeta_vivo_loop_12s.mp4")]))["streams"][0]
        video_width, video_height = video_info["width"], video_info["height"]
        shutil.copyfile(ROOT / "recursos-originales/logo_original.png", textures / "logo.png")
        buttons = []
        labels = ["Jugar", "Multijugador", "Opciones", "Salir"]
        # Misma columna que la referencia. Se añaden cuatro filas por petición del usuario.
        widths = [150, 270, 225, 135]
        for i, (action, filename) in enumerate(SOURCES.items()):
            target = textures / f"{action}.png"
            shutil.copyfile(source / filename, target)
            dump(target.with_suffix('.png.mcmeta'), {"texture": {"blur": True, "clamp": True}})
            width = widths[i]
            buttons.append(dict(action=action, label=labels[i], sprite=sprite(
                target, f"aresafio_menu:textures/menu/{action}.png",
                504-width/2, 540+i*80, width, 50)))
        subprocess.run(["ffmpeg", "-hide_banner", "-loglevel", "error", "-y", "-i",
                        str(source / "Planeta_vivo_loop_12s.mp4"), "-an", "-vf", "fps=30",
                        "-q:v", "4", "-pix_fmt", "yuvj444p", "-start_number", "0",
                        str(backgrounds / "%03d.jpg")], check=True)
        frames = len(list(backgrounds.glob("*.jpg")))
        if frames != 360:
            raise ValueError(f"Se esperaban 360 fotogramas, se obtuvieron {frames}")
        subprocess.run(["ffmpeg", "-hide_banner", "-loglevel", "error", "-y", "-i",
                        str(source / "Planeta_vivo_loop_12s.mp4"), "-frames:v", "1",
                        str(textures / "poster.png")], check=True)
        dump(textures / "poster.png.mcmeta", {"texture": {"blur": True, "clamp": True}})
        subprocess.run(["ffmpeg", "-hide_banner", "-loglevel", "error", "-y", "-i",
                        str(source / "MUSICA_SOLO_MENUS.mp3"), "-vn", "-c:a", "libvorbis", "-q:a", "5",
                        str(assets / "sounds/music/menu.ogg")], check=True)
        with Image.open(textures / "logo.png") as logo_image:
            logo_width, logo_height = logo_image.size
        dump(textures / "logo.png.mcmeta", {"texture": {"blur": True, "clamp": True}})
        definition = dict(referenceWidth=1919, referenceHeight=904,
                          logo=dict(texture="aresafio_menu:textures/menu/logo.png", textureWidth=logo_width,
                                    textureHeight=logo_height, u=0, v=0, sourceWidth=logo_width,
                                    sourceHeight=logo_height, x=104, y=0, width=800, height=530), buttons=buttons,
                          video=dict(prefix="aresafio_menu:textures/menu/background/",
                                     poster="aresafio_menu:textures/menu/poster.png", frames=frames,
                                     fps=30, width=video_width, height=video_height))
        dump(assets / "menu.json", definition)
        dump(assets / "sounds.json", {"menu_music": {"sounds": [
            {"name": "aresafio_menu:music/menu", "stream": True}]}})
        dump(pack / "pack.mcmeta", {"pack": {"pack_format": 34,
             "description": "ARESAFIO 4 — Menús beta 2 | Transparencias y vídeo nativo | 1.21.1"}})
        (pack / "LEEME_MENU_BETA.txt").write_text(
            "Activar localmente y usar aresafio-menu-client-1.0.0-beta.2.jar en el cliente Fabric 1.21.1.\n"
            "Retira el JAR beta.1 antes de instalar beta.2; conserva solo una versión del módulo.\n"
            "El servidor sigue usando el mod y pack V1; no necesita este módulo de menús.\n"
            "Posiciones editables en assets/aresafio_menu/menu.json.\n", encoding="utf-8")
        temporary_output = output.with_suffix(".zip.tmp")
        with zipfile.ZipFile(temporary_output, "w", zipfile.ZIP_DEFLATED, compresslevel=6) as archive:
            for path in sorted(pack.rglob("*")):
                if path.is_file():
                    info = zipfile.ZipInfo(path.relative_to(pack).as_posix(), (2026, 1, 1, 0, 0, 0))
                    info.compress_type = zipfile.ZIP_DEFLATED
                    archive.writestr(info, path.read_bytes())
        if temporary_output.stat().st_size >= 100 * 1024 * 1024:
            raise ValueError("El pack supera el límite por archivo de GitHub (100 MiB)")
        temporary_output.replace(output)
    sha1 = hashlib.sha1(output.read_bytes()).hexdigest()
    (ROOT / "RESOURCE_PACK_MENU_BETA_SHA1.txt").write_text(sha1 + "\n", encoding="ascii")
    print(f"Pack generado: {output.name} ({output.stat().st_size:,} bytes); SHA1 {sha1}")


def validate():
    output = ROOT / "ARESAFIO_GUI_MenuBeta_ResourcePack.zip"
    expected = (ROOT / "RESOURCE_PACK_MENU_BETA_SHA1.txt").read_text().strip()
    assert hashlib.sha1(output.read_bytes()).hexdigest() == expected, "SHA1 incorrecto"
    with zipfile.ZipFile(output) as archive, zipfile.ZipFile(ROOT / "ARESAFIO_GUI_V1_ResourcePack.zip") as original:
        assert archive.testzip() is None, "CRC incorrecto"
        assert json.loads(archive.read("pack.mcmeta"))["pack"]["pack_format"] == 34
        for info in archive.infolist():
            if info.filename.endswith((".json", ".mcmeta")):
                json.loads(archive.read(info))
        for name in original.namelist():
            if name.startswith("assets/"):
                assert archive.read(name) == original.read(name), f"Recurso V1 alterado: {name}"
        with zipfile.ZipFile(ROOT / "menu-assets/originales/menu.zip") as inputs:
            for action, name in SOURCES.items():
                assert archive.read(f"assets/aresafio_menu/textures/menu/{action}.png") == inputs.read(name)
        assert archive.read("assets/aresafio_menu/textures/menu/logo.png") == (ROOT / "recursos-originales/logo_original.png").read_bytes()
        definition = json.loads(archive.read("assets/aresafio_menu/menu.json"))
        assert len(definition["buttons"]) == 4
        assert definition["video"]["frames"] == 360
        assert (definition["video"]["width"], definition["video"]["height"]) == (1920, 1024)
        for i in range(360):
            assert f"assets/aresafio_menu/textures/menu/background/{i:03d}.jpg" in archive.namelist()
        assert "assets/aresafio_menu/sounds/music/menu.ogg" in archive.namelist()
    print("PASS: ZIP, SHA1, JSON, 360 fotogramas y música; imágenes originales y recursos V1 intactos")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true", help="Verificar el pack existente sin reconstruirlo")
    args = parser.parse_args()
    if not args.check:
        build()
    validate()

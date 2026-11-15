#!/usr/bin/env python3
"""
Генератор HD-текстуры и геометрии оригинального персонажа "Desert Rapper" (аватар концерта).

Один источник правды: список боксов BOXES ниже порождает И текстуру, И Java-код геометрии,
поэтому UV никогда не расходятся.

Запуск (из корня проекта):   python3 tools/generate_avatar.py
Нужно:  pip install pillow numpy
Выход:
  src/main/resources/assets/astroworld/textures/entity/cactus_avatar.png   (512x512)
  src/main/java/com/example/astroworld/client/AvatarGeometry.java

Логическая UV-сетка = 128x128 (в Java: TexturedModelData.of(data, 128, 128)).
Реальный PNG = 512x512, то есть 4 пикселя на 1 единицу модели (SCALE = 4).
Чтобы получить 256x256 — поставьте SCALE = 2, 1024x1024 — SCALE = 8. Java менять не нужно.
"""
import math
import os
import numpy as np
from PIL import Image, ImageDraw

GRID = 128
SCALE = 4
N = GRID * SCALE
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT_TEX = os.path.join(ROOT, "src/main/resources/assets/astroworld/textures/entity/cactus_avatar.png")
OUT_JAVA = os.path.join(ROOT, "src/main/java/com/example/astroworld/client/AvatarGeometry.java")

rng = np.random.default_rng(20261115)
img = np.zeros((N, N, 4), np.float32)

# ---------------------------------------------------------------- палитра (земляные тона)
SKIN = (104, 66, 44)
SKIN_D = (74, 45, 30)
SKIN_L = (130, 86, 58)
HAIR = (17, 13, 11)
HAIR_L = (52, 38, 28)
OLIVE = (86, 90, 58)
OLIVE_D = (62, 66, 42)
KHAKI = (122, 110, 78)
KHAKI_D = (92, 82, 58)
BEIGE = (204, 190, 160)
CREAM = (228, 216, 192)
BROWN = (92, 62, 40)
BROWN_D = (58, 38, 26)
BLACK = (24, 22, 20)
TAN = (156, 130, 90)
GOLD = (210, 172, 54)
GOLD_D = (130, 96, 22)


def clampc(c):
    return tuple(int(max(0, min(255, v))) for v in c)


# ---------------------------------------------------------------- геометрия: описание боксов
BOXES = []


def B(name, parent, origin, size, mat, dil=0.0, pivot=(0.0, 0.0, 0.0), rot=(0.0, 0.0, 0.0), outer=None, bead=False):
    BOXES.append(dict(name=name, parent=parent, origin=origin, size=size, mat=mat, dil=dil,
                      pivot=pivot, rot=rot, outer=outer, bead=bead))


def mirrored(b, newname, newparent):
    w = b["size"][0]
    ox, oy, oz = b["origin"]
    px, py, pz = b["pivot"]
    rx, ry, rz = b["rot"]
    flip = {"west": "east", "east": "west", None: None}
    BOXES.append(dict(name=newname, parent=newparent, origin=(-(ox + w), oy, oz), size=b["size"], mat=b["mat"],
                      dil=b["dil"], pivot=(-px, py, pz), rot=(rx, -ry, -rz), outer=flip[b["outer"]], bead=b["bead"]))


# --- Брейды (дочерние части головы: вращаются вместе с головой) ---
BRAID_NAMES = []
back_len = [8, 10, 9, 11, 9, 10, 8]
for i, L in enumerate(back_len):
    n = f"braid_back_{i}"
    BRAID_NAMES.append(n)
    B(n, "head", (-0.5, 0.0, -0.5), (1, L, 1), "braid",
      pivot=(-3.3 + i * 1.1, -4.0 + 0.4 * (i % 2), 4.4), rot=(0.10 + 0.03 * (i % 3), 0.0, (i - 3) * 0.015), bead=(i % 3 == 0))
for s, sx, roll in (("r", -4.55, 0.12), ("l", 4.55, -0.12)):
    for j, (z, L) in enumerate(((-1.2, 7), (0.6, 9), (2.4, 7))):
        n = f"braid_side_{s}_{j}"
        BRAID_NAMES.append(n)
        B(n, "head", (-0.5, 0.0, -0.5), (1, L, 1), "braid",
          pivot=(sx, -3.4 + 0.3 * j, z), rot=(0.04 * j, 0.0, roll), bead=(j == 1))
for s, sx, roll in (("r", -3.9, -0.04), ("l", 3.9, 0.04)):
    n = f"braid_front_{s}"
    BRAID_NAMES.append(n)
    B(n, "head", (-0.5, 0.0, -0.5), (1, 6, 1), "braid", pivot=(sx, -4.4, -4.55), rot=(-0.05, 0.0, roll), bead=True)
# серьги-гвоздики
B("stud_r", "head", (-0.5, -0.5, -0.5), (1, 1, 1), "gold", pivot=(-4.6, -2.6, 0.4))
B("stud_l", "head", (-0.5, -0.5, -0.5), (1, 1, 1), "gold", pivot=(4.6, -2.6, 0.4))

# --- Худи / жилет / цепи (дочерние части туловища) ---
B("hood", "body", (-3.5, -2.0, 2.2), (7, 5, 2), "hood", dil=0.3, rot=(-0.12, 0.0, 0.0))
B("hood_rim", "body", (-3.0, -0.4, -2.0), (6, 2, 4), "hood", dil=0.75)
B("cord_r", "body", (-0.5, 0.0, -0.5), (1, 5, 1), "cord", pivot=(-1.7, 1.6, -2.9))
B("cord_l", "body", (-0.5, 0.0, -0.5), (1, 4, 1), "cord", pivot=(1.7, 1.6, -2.9))
B("rig", "body", (-3.5, 1.0, -3.0), (7, 6, 1), "rig")
B("pouch_r", "body", (-3.4, 4.0, -4.0), (2, 3, 1), "pouch")
B("pouch_l", "body", (1.4, 4.0, -4.0), (2, 3, 1), "pouch")
B("strap_r", "body", (-3.5, -0.9, -2.5), (1, 1, 5), "strap", dil=0.1)
B("strap_l", "body", (2.5, -0.9, -2.5), (1, 1, 5), "strap", dil=0.1)
B("pocket", "body", (-3.5, 7.5, -3.2), (7, 3, 1), "fabric")
B("hem", "body", (-4.0, 10.0, -2.0), (8, 2, 4), "rib", dil=0.6)
B("chain_a", "body", (-3.0, 0.0, -3.2), (6, 1, 1), "chain")
B("chain_b", "body", (-2.0, 2.0, -3.3), (4, 1, 1), "chain")
B("pendant", "body", (-1.0, 3.2, -3.5), (2, 3, 1), "pendant")

# --- Руки: манжеты, браслеты, часы ---
_cuff = dict(name="cuff_r", parent="right_arm", origin=(-3.0, 6.0, -2.0), size=(4, 2, 4), mat="rib", dil=0.6)
B("cuff_r", "right_arm", (-3.0, 6.0, -2.0), (4, 2, 4), "rib", dil=0.6)
mirrored(BOXES[-1], "cuff_l", "left_arm")
B("bracelet_r", "right_arm", (-3.0, 8.0, -2.0), (4, 1, 4), "gold", dil=0.3)
B("watch_band_l", "left_arm", (-1.0, 8.0, -2.0), (4, 1, 4), "strap", dil=0.3)
B("watch_face_l", "left_arm", (3.3, 8.0, -1.0), (1, 1, 2), "watchface")

# --- Ноги: карго-карманы, ремни, сникеры (боксы правой ноги, затем зеркально для левой) ---
leg_start = len(BOXES)
B("cargo_r", "right_leg", (-3.5, 3.0, -1.5), (1, 4, 3), "cargo", outer="west")
B("cargo_flap_r", "right_leg", (-3.6, 2.6, -1.6), (1, 1, 3), "cargo", dil=0.1, outer="west")
B("knee_r", "right_leg", (-2.0, 5.0, -2.0), (4, 1, 4), "strap", dil=0.7)
B("anklecuff_r", "right_leg", (-2.0, 7.5, -2.0), (4, 1, 4), "rib", dil=0.7)
B("shoe_collar_r", "right_leg", (-2.0, 8.8, -2.0), (4, 1, 4), "collar", dil=0.4)
B("shoe_upper_r", "right_leg", (-2.0, 9.5, -3.0), (4, 2, 5), "shoe_upper", dil=0.3, outer="west")
B("shoe_sole_r", "right_leg", (-2.0, 11.4, -3.0), (4, 1, 5), "sole", dil=0.25, outer="west")
B("shoe_tongue_r", "right_leg", (-1.0, 0.0, -0.5), (2, 2, 1), "tongue", pivot=(0.0, 9.3, -2.4), rot=(-0.35, 0.0, 0.0))
B("shoe_heel_r", "right_leg", (-1.0, 9.0, 1.9), (2, 2, 1), "heelpatch")
for b in list(BOXES[leg_start:]):
    mirrored(b, b["name"][:-2] + "_l", "left_leg")

# ---------------------------------------------------------------- UV-упаковка дополнительных боксов
def box_dims(b):
    w, h, d = b["size"]
    return 2 * (w + d), h + d


FREE = [[64, 0, 128, 128], [0, 64, 64, 128]]  # свободные области логической сетки (x0,y0,x1,y1)
order = sorted(BOXES, key=lambda b: -box_dims(b)[1])
shelves = []  # (region_index, y, height, next_x)
for b in order:
    bw, bh = box_dims(b)
    placed = False
    for ri, (rx0, ry0, rx1, ry1) in enumerate(FREE):
        for sh in shelves:
            if sh["r"] == ri and sh["h"] >= bh and sh["x"] + bw <= rx1:
                b["uv"] = (sh["x"], sh["y"])
                sh["x"] += bw + 1
                placed = True
                break
        if placed:
            break
        # новая полка
        used_y = max([s["y"] + s["h"] + 1 for s in shelves if s["r"] == ri] + [ry0])
        if used_y + bh <= ry1 and rx0 + bw <= rx1:
            shelves.append(dict(r=ri, y=used_y, h=bh, x=rx0 + bw + 1))
            b["uv"] = (rx0, used_y)
            placed = True
            break
    if not placed:
        raise SystemExit("Не хватило места в UV-сетке для " + b["name"])


# ---------------------------------------------------------------- помощники рисования
def faces(u, v, w, h, d):
    f = {
        "top": (u + d, v, u + d + w, v + d),
        "bottom": (u + d + w, v, u + d + 2 * w, v + d),
        "west": (u, v + d, u + d, v + d + h),
        "front": (u + d, v + d, u + d + w, v + d + h),
        "east": (u + d + w, v + d, u + 2 * d + w, v + d + h),
        "back": (u + 2 * d + w, v + d, u + 2 * d + 2 * w, v + d + h),
    }
    return {k: tuple(c * SCALE for c in r) for k, r in f.items()}


def fill(face, color, noise=5.0, alpha=255, weave=0.0, diag=0.0):
    x0, y0, x1, y1 = face
    h, w = y1 - y0, x1 - x0
    arr = np.zeros((h, w, 4), np.float32)
    arr[..., :3] = color
    arr[..., 3] = alpha
    arr[..., :3] += rng.normal(0, noise, (h, w, 1))
    yy, xx = np.mgrid[0:h, 0:w]
    if weave:
        arr[..., :3] += ((((xx // 2) + (yy // 2)) % 2) * weave - weave / 2)[..., None]
    if diag:
        arr[..., :3] += ((((xx + yy) // 3) % 2) * diag - diag / 2)[..., None]
    img[y0:y1, x0:x1] = arr


def grad(face, top, bottom):
    x0, y0, x1, y1 = face
    h = y1 - y0
    m = np.linspace(top, bottom, h, dtype=np.float32)[:, None, None]
    reg = img[y0:y1, x0:x1]
    reg[..., :3] *= m


def tile_of(face):
    x0, y0, x1, y1 = face
    return Image.fromarray(np.clip(img[y0:y1, x0:x1], 0, 255).astype(np.uint8), "RGBA")


def put(face, tile):
    x0, y0, x1, y1 = face
    img[y0:y1, x0:x1] = np.array(tile, np.float32)


def draw(face, fn):
    """Рисует поверх грани без смешивания (заменяет пиксели, включая альфу)."""
    t = tile_of(face)
    fn(ImageDraw.Draw(t), t.size[0], t.size[1])
    put(face, t)


def over(face, fn):
    """Рисует полупрозрачным слоем (альфа-композитинг), не затирая основу."""
    t = tile_of(face)
    layer = Image.new("RGBA", t.size, (0, 0, 0, 0))
    fn(ImageDraw.Draw(layer), t.size[0], t.size[1])
    put(face, Image.alpha_composite(t, layer))


def clear(face, y_from=None):
    x0, y0, x1, y1 = face
    if y_from is not None:
        y0 = y0 + y_from
    img[y0:y1, x0:x1] = 0


def speckle(face, color, density, alpha=120):
    x0, y0, x1, y1 = face
    h, w = y1 - y0, x1 - x0
    mask = rng.random((h, w)) < density
    reg = img[y0:y1, x0:x1]
    for c in range(3):
        reg[..., c] = np.where(mask, reg[..., c] * (1 - alpha / 255) + color[c] * (alpha / 255), reg[..., c])


def all_faces(fc, fn):
    for k, f in fc.items():
        fn(k, f)


# ---------------------------------------------------------------- материалы дополнительных боксов
def paint_box(b):
    u, v = b["uv"]
    w, h, d = b["size"]
    fc = faces(u, v, w, h, d)
    mat = b["mat"]

    def base(color, noise=5.0, **kw):
        for f in fc.values():
            fill(f, color, noise=noise, **kw)

    if mat == "braid":
        base(HAIR, 4)
        for k, f in fc.items():
            # плетение: диагональные "косички" с коричневатым отливом
            def wv(dr, W, H):
                for y in range(H):
                    for x in range(W):
                        if ((x + y) // 2) % 3 == 0:
                            dr.point((x, y), fill=HAIR_L + (255,))
            draw(f, wv)
            if k in ("west", "east", "front", "back"):
                grad(f, 1.15, 0.8)
        if b["bead"]:
            for k in ("west", "east", "front", "back"):
                x0, y0, x1, y1 = fc[k]
                img[y1 - 3:y1, x0:x1] = (*GOLD, 255)
    elif mat in ("gold", "chain"):
        base(GOLD, 6, diag=26)
        if mat == "chain":
            for f in fc.values():
                def links(dr, W, H):
                    for x in range(0, W, 4):
                        dr.line([(x, 0), (x, H)], fill=GOLD_D + (255,))
                    for y in range(0, H, 4):
                        dr.line([(0, y), (W, y)], fill=GOLD_D + (255,))
                draw(f, links)
    elif mat == "pendant":
        base(GOLD, 5, diag=20)
        f = fc["front"]

        def cactus(dr, W, H):
            dr.rectangle([0, 0, W - 1, H - 1], outline=GOLD_D + (255,))
            cx = W // 2
            dr.rectangle([cx - 2, 8, cx + 1, H - 4], fill=OLIVE_D + (255,))
            dr.rectangle([cx - 6, 14, cx - 3, 17], fill=OLIVE_D + (255,))
            dr.rectangle([cx - 6, 8, cx - 4, 14], fill=OLIVE_D + (255,))
            dr.rectangle([cx + 2, 18, cx + 5, 21], fill=OLIVE_D + (255,))
            dr.rectangle([cx + 4, 12, cx + 6, 18], fill=OLIVE_D + (255,))
        draw(f, cactus)
    elif mat == "hood":
        base(OLIVE_D, 5, weave=10)
        for k, f in fc.items():
            grad(f, 1.1, 0.75)
            over(f, lambda dr, W, H: [dr.line([(x, 0), (x + 3, H)], fill=(0, 0, 0, 38)) for x in range(2, W, 7)])
        fill(fc["front"], (40, 42, 28), 4)
    elif mat == "cord":
        base(CREAM, 4)
        for f in fc.values():
            over(f, lambda dr, W, H: dr.rectangle([0, H - 4, W, H], fill=(60, 50, 40, 255)))
    elif mat == "fabric":
        base(OLIVE, 5, weave=8)
        for f in fc.values():
            over(f, lambda dr, W, H: dr.rectangle([0, 0, W - 1, H - 1], outline=(0, 0, 0, 70)))
        over(fc["front"], lambda dr, W, H: dr.line([(W // 2, 2), (W // 2, H - 2)], fill=(0, 0, 0, 60)))
    elif mat == "rib":
        base(OLIVE_D, 4)
        for f in fc.values():
            over(f, lambda dr, W, H: [dr.line([(x, 0), (x, H)], fill=(0, 0, 0, 55)) for x in range(1, W, 3)])
    elif mat == "rig":
        base((40, 34, 28), 4, weave=8)
        f = fc["front"]

        def webbing(dr, W, H):
            for y in range(3, H, 6):
                dr.line([(0, y), (W, y)], fill=(70, 62, 50, 255))
                dr.line([(0, y + 1), (W, y + 1)], fill=(18, 16, 14, 255))
        draw(f, webbing)
        for k, ff in fc.items():
            if k != "front":
                fill(ff, (30, 26, 22), 3)
    elif mat == "pouch":
        base(TAN, 5, weave=8)
        f = fc["front"]

        def flap(dr, W, H):
            dr.rectangle([0, 0, W - 1, H // 2], fill=(132, 108, 72, 255), outline=(60, 46, 30, 255))
            dr.rectangle([W // 2 - 1, H // 2 - 2, W // 2 + 1, H // 2 + 1], fill=GOLD + (255,))
            dr.line([(1, H - 2), (W - 2, H - 2)], fill=(80, 64, 44, 255))
        draw(f, flap)
    elif mat == "strap":
        base((26, 24, 22), 3, weave=6)
        for f in fc.values():
            over(f, lambda dr, W, H: [dr.point((x, H // 2), fill=(90, 84, 70, 255)) for x in range(0, W, 3)])
    elif mat == "cargo":
        base(KHAKI, 5, weave=7)
        for k, f in fc.items():
            over(f, lambda dr, W, H: dr.rectangle([1, 1, W - 2, H - 2], outline=(70, 60, 40, 110)))
        # пуговица на клапане/кармане (внешняя сторона)
        ok = b["outer"]
        if ok:
            f = fc[ok]
            draw(f, lambda dr, W, H: dr.ellipse([W // 2 - 2, H // 2 - 2, W // 2 + 2, H // 2 + 2], fill=BROWN_D + (255,)))
    elif mat == "collar":
        base(BROWN, 5, weave=6)
        for f in fc.values():
            grad(f, 1.15, 0.8)
    elif mat == "shoe_upper":
        base(CREAM, 4)
        for k, f in fc.items():
            grad(f, 1.05, 0.82)
        # верх: шнуровка
        def laces(dr, W, H):
            for y in range(3, H, 4):
                dr.line([(2, y), (W - 3, y)], fill=(60, 52, 40, 255))
        draw(fc["top"], laces)
        # перфорация носка
        def perf(dr, W, H):
            for y in range(3, H - 2, 4):
                for x in range(3, W - 2, 4):
                    dr.point((x, y), fill=(120, 106, 84, 255))
        draw(fc["front"], perf)
        # боковина: коричневый оверлей в виде "молнии" + бежевая вставка (оригинальный дизайн)
        side = fc[b["outer"] or "west"]

        def bolt(dr, W, H):
            dr.rectangle([0, 0, 5, H], fill=BROWN + (255,))  # колодка пятки
            dr.polygon([(W * 0.30, 1), (W * 0.62, 1), (W * 0.50, H * 0.45), (W * 0.78, H * 0.45),
                        (W * 0.40, H - 1), (W * 0.48, H * 0.58), (W * 0.22, H * 0.58)], fill=BROWN_D + (255,))
            dr.line([(0, H - 1), (W, H - 1)], fill=(150, 130, 100, 255))
        draw(side, bolt)
    elif mat == "sole":
        base((236, 230, 214), 3)
        fill(fc["bottom"], BROWN_D, 4)
        over(fc["bottom"], lambda dr, W, H: [dr.line([(0, y), (W, y)], fill=(0, 0, 0, 80)) for y in range(1, H, 3)])
        for k in ("west", "east", "front", "back"):
            over(fc[k], lambda dr, W, H: dr.line([(0, H - 2), (W, H - 2)], fill=(140, 120, 90, 160)))
    elif mat == "tongue":
        base(OLIVE, 4, weave=6)
        over(fc["front"], lambda dr, W, H: dr.rectangle([W // 2 - 3, 1, W // 2 + 2, 4], fill=(230, 220, 200, 255)))
    elif mat == "heelpatch":
        base(BROWN, 4, weave=5)
    elif mat == "watchface":
        base((200, 196, 184), 3)
        for k in ("west", "east", "top", "bottom", "front", "back"):
            over(fc[k], lambda dr, W, H: dr.rectangle([0, 0, W - 1, H - 1], outline=(30, 28, 26, 255)))
        over(fc["east"], lambda dr, W, H: (dr.line([(W // 2, 2), (W // 2, H // 2)], fill=(20, 20, 20, 255)),
                                           dr.line([(W // 2, H // 2), (W - 2, H // 2)], fill=(20, 20, 20, 255))))
    else:
        base((255, 0, 255), 0)  # неизвестный материал виден сразу


# ---------------------------------------------------------------- базовые слои (анатомия + одежда)
# классическая раскладка (в единицах сетки): u, v, w, h, d
HEAD = (0, 0, 8, 8, 8)
HAT = (32, 0, 8, 8, 8)
BODY = (16, 16, 8, 12, 4)
JACKET = (16, 32, 8, 12, 4)
R_ARM = (40, 16, 4, 12, 4)
R_SLEEVE = (40, 32, 4, 12, 4)
L_ARM = (32, 48, 4, 12, 4)
L_SLEEVE = (48, 48, 4, 12, 4)
R_LEG = (0, 16, 4, 12, 4)
R_PANTS = (0, 32, 4, 12, 4)
L_LEG = (16, 48, 4, 12, 4)
L_PANTS = (0, 48, 4, 12, 4)


def paint_head():
    fc = faces(*HEAD)
    for k, f in fc.items():
        fill(f, SKIN, 4)
        grad(f, 1.08, 0.9)
    # --- лицо (оригинальный стилизованный персонаж, не портрет реального человека) ---
    f = fc["front"]  # 32x32 px

    def face(dr, W, H):
        pass

    over(f, lambda dr, W, H: (
        dr.ellipse([3, 17, 9, 25], fill=(0, 0, 0, 46)), dr.ellipse([22, 17, 28, 25], fill=(0, 0, 0, 46)),   # впадины под скулами
        dr.rectangle([6, 16, 12, 16], fill=(0, 0, 0, 55)), dr.rectangle([19, 16, 25, 16], fill=(0, 0, 0, 55)),  # тени под глазами
        dr.rectangle([15, 13, 16, 20], fill=(255, 225, 190, 38)),                                                # блик на переносице
        dr.rectangle([12, 17, 13, 21], fill=(0, 0, 0, 45)), dr.rectangle([18, 17, 19, 21], fill=(0, 0, 0, 45)),  # боковины носа
        dr.rectangle([13, 23, 18, 23], fill=(0, 0, 0, 60)),                                                      # тень под носом
    ))

    def features(dr, W, H):
        brow = (20, 15, 12, 255)
        dr.rectangle([6, 9, 13, 10], fill=brow)
        dr.rectangle([18, 9, 25, 10], fill=brow)
        dr.point((6, 10), fill=brow)
        dr.point((25, 10), fill=brow)
        for ex in (7, 19):  # глаза
            dr.rectangle([ex, 12, ex + 5, 14], fill=(234, 228, 216, 255))
            dr.rectangle([ex + 2, 12, ex + 3, 14], fill=(52, 30, 20, 255))
            dr.point((ex + 2, 13), fill=(8, 6, 6, 255))
            dr.point((ex + 3, 12), fill=(255, 255, 255, 255))
            dr.line([(ex - 1, 11), (ex + 6, 11)], fill=(30, 20, 15, 255))
        dr.rectangle([13, 22, 14, 22], fill=(52, 28, 20, 255))  # ноздри
        dr.rectangle([17, 22, 18, 22], fill=(52, 28, 20, 255))
        dr.rectangle([11, 24, 20, 25], fill=(86, 48, 40, 255))   # верхняя губа
        dr.rectangle([11, 26, 20, 27], fill=(36, 10, 10, 255))   # рот
        dr.rectangle([12, 26, 19, 26], fill=GOLD + (255,))       # золотые грилзы
        for x in range(13, 19, 2):
            dr.point((x, 26), fill=GOLD_D + (255,))
        dr.rectangle([12, 28, 19, 29], fill=(80, 44, 36, 255))   # нижняя губа
    draw_keep = tile_of(f)
    # рисуем элементы лица с сохранением альфы: просто поверх
    t = tile_of(f)
    d = ImageDraw.Draw(t)
    features(d, 32, 32)
    put(f, t)
    # щетина и козлиная бородка
    x0, y0, x1, y1 = f
    reg = img[y0:y1, x0:x1]
    mask = np.zeros((32, 32), bool)
    mask[21:32, 5:27] = True
    mask[23:26, 8:12] = True
    mask[23:26, 20:24] = True
    mask[27:32, 12:20] = True
    dots = (rng.random((32, 32)) < 0.16) & mask
    for c in range(3):
        reg[..., c] = np.where(dots, reg[..., c] * 0.55, reg[..., c])
    over(f, lambda dr, W, H: dr.rectangle([12, 29, 19, 31], fill=(10, 6, 5, 90)))
    grad(f, 1.0, 0.92)
    # уши на боковых гранях
    for k in ("west", "east"):
        over(fc[k], lambda dr, W, H: (dr.ellipse([12, 12, 19, 20], fill=(70, 40, 28, 255)),
                                      dr.ellipse([14, 14, 17, 18], fill=(50, 28, 20, 255))))

    # --- слой волос (hat): отдельные пиксели прозрачны, чтобы лицо оставалось видно ---
    hc = faces(*HAT)
    for k, f in hc.items():
        fill(f, HAIR, 5, diag=14)
    # передняя грань: линия роста волос
    f = hc["front"]
    x0, y0, x1, y1 = f
    img[y0:y1, x0:x1, 3] = 0
    for x in range(32):
        depth = 5 + int(2 * math.sin(x * 0.7) + rng.integers(0, 2))
        img[y0:y0 + depth, x0 + x, 3] = 255
    img[y0:y0 + 15, x0:x0 + 3, 3] = 255       # баки
    img[y0:y0 + 15, x1 - x0 - 3 + x0:x1, 3] = 255
    # боковые грани: волосы сзади и сверху, ухо открыто
    for k in ("west", "east"):
        f = hc[k]
        x0, y0, x1, y1 = f
        img[y0:y1, x0:x1, 3] = 0
        for yy in range(32):
            for xx in range(32):
                gx = xx if k == "west" else 31 - xx  # gx=0 — затылок, 31 — лицо
                if yy < 10 or (gx < 11 and yy < 27) or (gx > 27 and yy < 15):
                    img[y0 + yy, x0 + xx, 3] = 255
    f = hc["back"]
    x0, y0, x1, y1 = f
    img[y0 + 27:y1, x0:x1, 3] = 0
    # низ слоя волос прозрачен
    x0, y0, x1, y1 = hc["bottom"]
    img[y0:y1, x0:x1, 3] = 0
    # блики волос (коричневатый отлив)
    for k in ("top", "back", "west", "east", "front"):
        speckle(hc[k], HAIR_L, 0.10, 150)


def paint_body():
    fc = faces(*BODY)
    for k, f in fc.items():
        fill(f, BEIGE, 4, weave=8)
        grad(f, 1.05, 0.85)
    f = fc["front"]  # 32x48
    draw(f, lambda dr, W, H: dr.rectangle([0, 0, W, 10], fill=SKIN + (255,)))      # шея/грудь
    over(f, lambda dr, W, H: (dr.rectangle([0, 10, W, 13], fill=(120, 100, 70, 255)),   # воротник футболки
                              dr.ellipse([12, 2, 20, 9], outline=SKIN_D + (255,))))   # оригинальный мини-татуаж-звезда
    over(f, lambda dr, W, H: (dr.line([(16, 1), (16, 9)], fill=SKIN_D + (200,)), dr.line([(12, 5), (20, 5)], fill=SKIN_D + (200,))))
    for k in ("west", "east"):
        draw(fc[k], lambda dr, W, H: dr.rectangle([0, 0, W, 6], fill=SKIN + (255,)))
    fill(fc["top"], SKIN, 4)

    jc = faces(*JACKET)
    for k, f in jc.items():
        fill(f, OLIVE, 5, weave=9)
    for k in ("front", "back", "west", "east"):
        f = jc[k]
        grad(f, 1.12, 0.8)
        over(f, lambda dr, W, H: [dr.line([(x, 0), (x + 2, H)], fill=(0, 0, 0, 30)) for x in range(4, W, 9)])
        over(f, lambda dr, W, H: [dr.line([(0, y), (W, y + 2)], fill=(255, 255, 255, 16)) for y in range(6, H, 11)])
    # вырез горловины спереди (V)
    f = jc["front"]
    x0, y0, x1, y1 = f
    for y in range(0, 12):
        half = 10 - y * 0.55
        xs = int(16 - half)
        xe = int(16 + half)
        img[y0 + y, x0 + max(xs, 0):x0 + min(xe, 32), :] = 0
    # логотип на спине: оригинальная эмблема "кактус + луна + звёзды"
    f = jc["back"]

    def emblem(dr, W, H):
        cx, cy = W // 2, 20
        ring = (214, 196, 160, 255)
        dr.ellipse([cx - 13, cy - 13, cx + 13, cy + 13], outline=ring, width=2)
        dr.ellipse([cx - 10, cy - 10, cx + 10, cy + 10], outline=(150, 130, 96, 255))
        dr.pieslice([cx - 8, cy - 9, cx + 8, cy + 7], 200, 340, fill=(36, 30, 24, 255))   # луна/диск
        dr.rectangle([cx - 2, cy - 7, cx + 1, cy + 10], fill=ring)                      # ствол
        dr.rectangle([cx - 7, cy - 1, cx - 3, cy + 1], fill=ring)
        dr.rectangle([cx - 7, cy - 6, cx - 5, cy - 1], fill=ring)                       # левая ветвь
        dr.rectangle([cx + 2, cy + 3, cx + 6, cy + 5], fill=ring)
        dr.rectangle([cx + 5, cy - 2, cx + 7, cy + 3], fill=ring)                       # правая ветвь
        for sx, sy in ((cx - 10, cy - 14), (cx + 9, cy - 12), (cx - 12, cy + 8), (cx + 12, cy + 6), (cx, cy - 15)):
            dr.line([(sx - 1, sy), (sx + 1, sy)], fill=ring)
            dr.line([(sx, sy - 1), (sx, sy + 1)], fill=ring)
        dr.rectangle([cx - 10, cy + 18, cx + 10, cy + 19], fill=ring)                   # "надпись" (линии)
        dr.rectangle([cx - 6, cy + 22, cx + 6, cy + 22], fill=(150, 130, 96, 255))
    draw_over = lambda: None
    t = tile_of(f)
    layer = Image.new("RGBA", t.size, (0, 0, 0, 0))
    emblem(ImageDraw.Draw(layer), *t.size)
    put(f, Image.alpha_composite(t, layer))
    # воротник капюшона уже боксом; нижний подгиб — рёбра
    for k in ("front", "back", "west", "east"):
        over(jc[k], lambda dr, W, H: [dr.line([(x, H - 8), (x, H)], fill=(0, 0, 0, 40)) for x in range(1, W, 3)])


def paint_arm(spec, sleeve_spec, left):
    fc = faces(*spec)
    for k, f in fc.items():
        fill(f, SKIN, 4)
        grad(f, 1.06, 0.9)
    # кисти: нижние 8 px граней (2 единицы) — открытая кожа с татуировками (оригинальные знаки)
    for k in ("front", "back", "west", "east"):
        f = fc[k]
        x0, y0, x1, y1 = f
        over(f, lambda dr, W, H: [dr.line([(x, H - 6), (x, H - 1)], fill=(20, 12, 8, 120)) for x in range(3, W, 4)])
        over(f, lambda dr, W, H: dr.rectangle([0, H - 8, W, H - 7], fill=(0, 0, 0, 40)))
    over(fc["back"], lambda dr, W, H: (dr.ellipse([5, H - 7, 10, H - 2], outline=(14, 10, 8, 200)),
                                       dr.line([(7, H - 7), (7, H - 2)], fill=(14, 10, 8, 200))))
    over(fc["front"], lambda dr, W, H: [dr.point((x, H - 6), fill=(14, 10, 8, 220)) for x in range(2, W, 3)])
    sc = faces(*sleeve_spec)
    for k, f in sc.items():
        fill(f, OLIVE, 5, weave=9)
        grad(f, 1.1, 0.82)
    for k in ("front", "back", "west", "east"):
        x0, y0, x1, y1 = sc[k]
        img[y1 - 8:y1, x0:x1] = 0  # кисть открыта
        over(sc[k], lambda dr, W, H: [dr.line([(x, 0), (x + 1, H)], fill=(0, 0, 0, 30)) for x in range(3, W, 7)])
    x0, y0, x1, y1 = sc["bottom"]
    img[y0:y1, x0:x1] = 0


def paint_leg(spec, pants_spec):
    fc = faces(*spec)
    for k, f in fc.items():
        fill(f, KHAKI_D, 4)
    for k in ("front", "back", "west", "east"):
        x0, y0, x1, y1 = fc[k]
        fill((x0, y1 - 12, x1, y1), (232, 230, 224), 3, weave=8)   # носки
        over((x0, y1 - 12, x1, y1), lambda dr, W, H: [dr.line([(x, 0), (x, H)], fill=(0, 0, 0, 28)) for x in range(1, W, 3)])
    pc = faces(*pants_spec)
    for k, f in pc.items():
        fill(f, KHAKI, 5, weave=9)
    for k in ("front", "back", "west", "east"):
        f = pc[k]
        x0, y0, x1, y1 = f
        grad(f, 1.1, 0.82)
        # потертости: светлые пятна на коленях и бёдрах
        over(f, lambda dr, W, H: (dr.ellipse([2, 18, W - 3, 30], fill=(255, 245, 210, 34)),
                                  dr.ellipse([3, 4, W - 4, 14], fill=(255, 245, 210, 22))))
        speckle(f, (210, 196, 150), 0.03, 140)
        # складки ткани
        over(f, lambda dr, W, H: [dr.line([(2, y), (W - 2, y + 3)], fill=(0, 0, 0, 34)) for y in (22, 27, 31)])
        # строчка вдоль швов
        over(f, lambda dr, W, H: [dr.point((1, y), fill=(60, 50, 30, 200)) for y in range(0, H, 3)])
        img[y1 - 8:y1, x0:x1] = 0   # низ штанин открыт — носки и кроссовки
    x0, y0, x1, y1 = pc["bottom"]
    img[y0:y1, x0:x1] = 0


def paint_all():
    paint_head()
    paint_body()
    paint_arm(R_ARM, R_SLEEVE, False)
    paint_arm(L_ARM, L_SLEEVE, True)
    paint_leg(R_LEG, R_PANTS)
    paint_leg(L_LEG, L_PANTS)
    for b in BOXES:
        paint_box(b)


# ---------------------------------------------------------------- генерация Java
def fl(x):
    s = f"{x:.3f}".rstrip("0").rstrip(".")
    if s in ("-0", ""):
        s = "0"
    return s + "f"


def write_java():
    lines = []
    lines.append("package com.example.astroworld.client;\n")
    lines.append("import net.minecraft.client.model.Dilation;")
    lines.append("import net.minecraft.client.model.ModelPartBuilder;")
    lines.append("import net.minecraft.client.model.ModelPartData;")
    lines.append("import net.minecraft.client.model.ModelTransform;\n")
    lines.append("/** АВТОГЕНЕРАЦИЯ: tools/generate_avatar.py. Не правьте вручную — правьте скрипт и запустите его заново. */")
    lines.append("final class AvatarGeometry {")
    lines.append(f"    static final int TEX_GRID = {GRID};")
    lines.append("    static final String[] BRAID_NAMES = {")
    lines.append("            " + ", ".join(f'"{n}"' for n in BRAID_NAMES))
    lines.append("    };\n")
    lines.append("    private AvatarGeometry() {\n    }\n")
    lines.append("    /** Добавляет дополнительные кубоиды как ДОЧЕРНИЕ части базовых — pivot-точки конечностей не меняются. */")
    lines.append("    static void addExtras(ModelPartData head, ModelPartData body, ModelPartData rightArm, ModelPartData leftArm,")
    lines.append("                          ModelPartData rightLeg, ModelPartData leftLeg) {")
    var = {"head": "head", "body": "body", "right_arm": "rightArm", "left_arm": "leftArm", "right_leg": "rightLeg", "left_leg": "leftLeg"}
    for b in BOXES:
        ox, oy, oz = b["origin"]
        w, h, d = b["size"]
        px, py, pz = b["pivot"]
        rx, ry, rz = b["rot"]
        u, v = b["uv"]
        if b["pivot"] == (0.0, 0.0, 0.0) and b["rot"] == (0.0, 0.0, 0.0):
            tr = "ModelTransform.NONE"
        else:
            tr = f"ModelTransform.of({fl(px)}, {fl(py)}, {fl(pz)}, {fl(rx)}, {fl(ry)}, {fl(rz)})"
        dil = f"new Dilation({fl(b['dil'])})" if b["dil"] else "Dilation.NONE"
        lines.append(f'        {var[b["parent"]]}.addChild("{b["name"]}", ModelPartBuilder.create().uv({u}, {v})'
                     f'.cuboid({fl(ox)}, {fl(oy)}, {fl(oz)}, {w}.0f, {h}.0f, {d}.0f, {dil}), {tr});')
    lines.append("    }")
    lines.append("}")
    with open(OUT_JAVA, "w", encoding="utf-8") as fh:
        fh.write("\n".join(lines) + "\n")


if __name__ == "__main__":
    paint_all()
    out = Image.fromarray(np.clip(img, 0, 255).astype(np.uint8), "RGBA")
    os.makedirs(os.path.dirname(OUT_TEX), exist_ok=True)
    out.save(OUT_TEX, optimize=True)
    write_java()
    print(f"texture: {OUT_TEX}  {out.size}")
    print(f"java:    {OUT_JAVA}  ({len(BOXES)} extra cuboids)")

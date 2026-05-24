# Packet Format Reference

Tüm multi-byte alanlar **big-endian** (Java `ByteBuffer` varsayılanı) sıralamasındadır.

---

## Genel Kurallar

### Standart Header (5 byte)

Her paketin başında bulunur.

```
[0]      PacketType ID   1 byte   uint8
[1..4]   BodySize        4 byte   int32  — sadece body byte sayısı, header dahil değil
```

`getPacketSize() = getHeaderSize() + getBodySize()`

### Encoding Katmanları

| Katman        | Metod                  | Açıklama                           |
|---------------|------------------------|------------------------------------|
| Tam paket     | `exportPacketToBuffer` | Header + Body                      |
| Sadece body   | `writeToPacketBody`    | Header yok — gömülü paketler için  |
| Sadece header | `writeToPacketHeader`  | Özel header'lar bunu override eder |

---

## PacketType ID Tablosu

| ID | Enum             | Sınıf                 |
|----|------------------|-----------------------|
| 0  | `UNKNOWN`        | —                     |
| 1  | `VECTOR2`        | `Vector2Packet`       |
| 2  | `LINE`           | `LinePacket`          |
| 3  | `BREAKABLE_LINE` | `BreakableLinePacket` |
| 4  | `POLYGON`        | `PolygonPacket`       |
| 5  | `NOTIFICATION`   | `NotificationPacket`  |
| 6  | `STRING`         | `StringPacket`        |
| 7  | `ARRAY`          | `ListPacket`          |
| 8  | `UNIFORM_ARRAY`  | `UniformListPacket`   |
| 9  | `ACTION`         | `ActionPacket`        |

---

## Vector2Packet

Toplam: **13 byte** (5 header + 8 body)

```
── Header (5 byte) ──────────────────────────────────
[0]      PacketType = 0x01          1 byte
[1..4]   BodySize   = 8             4 byte

── Body (8 byte) ────────────────────────────────────
[5..8]   x                          4 byte  float32
[9..12]  y                          4 byte  float32
```

> `BODY_DECODER` mevcuttur.
> `UniformListPacket<Vector2Packet>` içinde header yazılmaz, sadece 8 byte body yazılır.

---

## LinePacket

Toplam: **21 byte** (5 header + 16 body)

`LinePacket` içindeki `Vector2`'ler header'sız gömülüdür (`writeToPacketBody` çağrılır).

```
── Header (5 byte) ──────────────────────────────────
[0]      PacketType = 0x02          1 byte
[1..4]   BodySize   = 16            4 byte

── Body (16 byte) ───────────────────────────────────
  ── Point A (8 byte) ─────────────────────────────
  [5..8]   a.x                      4 byte  float32
  [9..12]  a.y                      4 byte  float32

  ── Point B (8 byte) ─────────────────────────────
  [13..16] b.x                      4 byte  float32
  [17..20] b.y                      4 byte  float32
```

> `BODY_DECODER` mevcuttur.
> `UniformListPacket<LinePacket>` içinde her öğe 16 byte body olarak yazılır.

---

## BreakableLinePacket

Toplam: **25 byte** (5 header + 20 body)

```
── Header (5 byte) ──────────────────────────────────
[0]      PacketType = 0x03          1 byte
[1..4]   BodySize   = 20            4 byte

── Body (20 byte) ───────────────────────────────────
[5..8]   health                     4 byte  int32

  ── Point A (8 byte) ─────────────────────────────
  [9..12]  a.x                      4 byte  float32
  [13..16] a.y                      4 byte  float32

  ── Point B (8 byte) ─────────────────────────────
  [17..20] b.x                      4 byte  float32
  [21..24] b.y                      4 byte  float32
```

> `BODY_DECODER` mevcuttur.
> `UniformListPacket<BreakableLinePacket>` içinde her öğe 20 byte body olarak yazılır.

---

## StringPacket

Toplam: **5 + N byte** (N = UTF-8 string uzunluğu)

```
── Header (5 byte) ──────────────────────────────────
[0]      PacketType = 0x06          1 byte
[1..4]   BodySize   = N             4 byte

── Body (N byte) ────────────────────────────────────
[5..5+N-1]  data (UTF-8)            N byte
```

> `BODY_DECODER` **yoktur** — boyut header'dan okunur, header'sız embed edilemez.
> `UniformListPacket<StringPacket>` desteklenmez.

---

## NotificationPacket

Toplam: **5 + sender_size + datetime_size + message_size byte**

Her alan tam `StringPacket` olarak (header dahil) gömülüdür.

```
── Header (5 byte) ──────────────────────────────────
[0]      PacketType = 0x05          1 byte
[1..4]   BodySize                   4 byte

── Body ─────────────────────────────────────────────
  ── sender   (tam StringPacket) ──────────────────
  [...]  PacketType = 0x06          1 byte
  [...]  BodySize                   4 byte
  [...]  data (UTF-8)               N byte

  ── datetime (tam StringPacket) ──────────────────
  [...]  PacketType = 0x06          1 byte
  [...]  BodySize                   4 byte
  [...]  data (UTF-8)               N byte

  ── message  (tam StringPacket) ──────────────────
  [...]  PacketType = 0x06          1 byte
  [...]  BodySize                   4 byte
  [...]  data (UTF-8)               N byte
```

> Yazma sırası: `sender → datetime → message`

---

## ListPacket

Toplam: **5 + Σ(öğe_paket_boyutları) byte**

Her öğe tam paket (header + body) olarak yazılır.

```
── Header (5 byte) ──────────────────────────────────
[0]      PacketType = 0x07          1 byte
[1..4]   BodySize                   4 byte

── Body ─────────────────────────────────────────────
  ── Item[0] (tam Packet) ─────────────────────────
  [...]  PacketType                 1 byte
  [...]  BodySize                   4 byte
  [...]  body                       N byte

  ── Item[1] (tam Packet) ─────────────────────────
  [...]  ...

  ── Item[n-1] (tam Packet) ───────────────────────
  [...]  ...
```

> Decoder, `BodySize` kadar byte okunana dek `PacketMapper.fromBuffer` çağırır.
> Öğe tipleri karışık olabilir.

---

## UniformListPacket

Toplam: **10 + (ItemCount × öğe_body_boyutu) byte**

Tüm öğeler aynı tipten olduğu için öğe header'ları yazılmaz — sadece body'ler yazılır.
Buna karşılık paket header'ına `ItemType` ve `ItemCount` eklenir.

```
── Header (10 byte) ─────────────────────────────────
[0]      PacketType = 0x08          1 byte
[1..4]   BodySize                   4 byte
[5]      ItemType                   1 byte  — öğelerin PacketType ID'si
[6..9]   ItemCount                  4 byte  int32

── Body (ItemCount × öğe_body_boyutu) ───────────────
  ── Item[0] body ─────────────────────────────────
  [...]  (header yok, sadece body)  N byte

  ── Item[1] body ─────────────────────────────────
  [...]  ...

  ── Item[n-1] body ───────────────────────────────
  [...]  ...
```

**Örnek — `UniformListPacket<Vector2Packet>` (3 öğe):**

```
[0]      0x08                       PacketType = UNIFORM_ARRAY
[1..4]   24                         BodySize = 3 × 8
[5]      0x01                       ItemType = VECTOR2
[6..9]   3                          ItemCount
[10..13] v0.x                       float32
[14..17] v0.y                       float32
[18..21] v1.x                       float32
[22..25] v1.y                       float32
[26..29] v2.x                       float32
[30..33] v2.y                       float32
```

> Sadece `BodyDecoder`'ı olan tipler öğe olabilir: `Vector2Packet`, `LinePacket`, `BreakableLinePacket`.

---

## PolygonPacket

Toplam: **5 + lines_paket_boyutu byte**

Body'de lines bir `ListPacket` veya `UniformListPacket` olarak tam yazılır.

```
── Header (5 byte) ──────────────────────────────────
[0]      PacketType = 0x04          1 byte
[1..4]   BodySize                   4 byte

── Body ─────────────────────────────────────────────
  ── lines (tam ListPacket veya UniformListPacket) ─
  [...]  PacketType (0x07 / 0x08)   1 byte
  [...]  BodySize                   4 byte
  [...]  [ItemType]                 1 byte  — sadece UniformList ise
  [...]  [ItemCount]                4 byte  — sadece UniformList ise
  [...]  line öğeleri               ...
```

---

## ActionPacket

Toplam: **9 + body byte**

Standart 5 byte header yerine 9 byte özel header kullanır.

```
── Header (9 byte) ──────────────────────────────────
[0]      PacketType = 0x09          1 byte
[1..4]   ActionMask                 4 byte  int32  — bit flag
[5..8]   BodySize                   4 byte  int32

── Body ─────────────────────────────────────────────
  Sadece body'si olan action'lar mask bit sırasıyla yazılır:
  FORCE(bit 1) → ROTATION(bit 2) → ATTACK(bit 4) → BOOST(bit 8)
```

### ActionMask Bit Tablosu

| Bit  | Decimal | ActionType | Body katkısı |
|------|---------|------------|--------------|
| 0x01 | 1       | `FORCE`    | 8 byte       |
| 0x02 | 2       | `ROTATION` | 4 byte       |
| 0x04 | 4       | `ATTACK`   | 0 byte       |
| 0x08 | 8       | `BOOST`    | 0 byte       |

---

### ForceAction Body

```
[0..3]   dx      4 byte  float32  — normalize yön vektörü X
[4..7]   dy      4 byte  float32  — normalize yön vektörü Y
```

---

### RotationAction Body

```
[0..3]   targetAngle    4 byte  float32  — radyan, hedef açı
```

---

### AttackAction Body

```
(boş — mask'te bit 1 olması yeterli)
```

---

### BoostAction Body

```
(boş — mask'te bit 1 olması yeterli)
```

---

### ActionPacket Örnekleri

**Yalnızca BOOST (mask = 0x08):**

```
[0]      0x09   PacketType = ACTION
[1..4]   0x08   ActionMask = BOOST
[5..8]   0      BodySize = 0
```

Toplam: **9 byte**

---

**FORCE + ROTATION (mask = 0x03):**

```
[0]      0x09        PacketType = ACTION
[1..4]   0x03        ActionMask = FORCE | ROTATION
[5..8]   12          BodySize = 8 + 4
[9..12]  dx          float32
[13..16] dy          float32
[17..20] targetAngle float32
```

Toplam: **21 byte**

---

**FORCE + ROTATION + ATTACK + BOOST (mask = 0x0F):**

```
[0]      0x09        PacketType = ACTION
[1..4]   0x0F        ActionMask = tüm bitler
[5..8]   12          BodySize = 8 + 4 + 0 + 0
[9..12]  dx          float32
[13..16] dy          float32
[17..20] targetAngle float32
```

Toplam: **21 byte** — ATTACK ve BOOST body yazmaz, maliyet sıfır.

---

## Decode Akışı

```
fromBuffer(buffer)
│
├── buffer.get()  →  PacketType ID
│
├── switch(type)
│     ├── VECTOR2        →  Vector2Packet.DECODER.decode(buffer)
│     │                        └── getInt() [bodySize]
│     │                            getFloat() [x]
│     │                            getFloat() [y]
│     │
│     ├── LINE           →  LinePacket.DECODER.decode(buffer)
│     │                        └── getInt() [bodySize]
│     │                            Vector2Packet.BODY_DECODER × 2
│     │
│     ├── BREAKABLE_LINE →  BreakableLinePacket.DECODER.decode(buffer)
│     │                        └── getInt() [bodySize]
│     │                            getInt() [health]
│     │                            Vector2Packet.BODY_DECODER × 2
│     │
│     ├── STRING         →  StringPacket.DECODER.decode(buffer)
│     │                        └── getInt() [bodySize=N]
│     │                            get(bytes[N])
│     │
│     ├── NOTIFICATION   →  NotificationPacket.DECODER.decode(buffer)
│     │                        └── getInt() [bodySize]
│     │                            fromBuffer() [sender]
│     │                            fromBuffer() [datetime]
│     │                            fromBuffer() [message]
│     │
│     ├── ARRAY          →  ListPacket.DECODER.decode(buffer)
│     │                        └── getInt() [bodySize]
│     │                            fromBuffer() × öğe sayısı
│     │
│     ├── UNIFORM_ARRAY  →  UniformListPacket.DECODER.decode(buffer)
│     │                        └── getInt()  [bodySize]
│     │                            get()     [itemType]
│     │                            getInt()  [itemCount]
│     │                            decodeBody(itemType) × itemCount
│     │
│     ├── POLYGON        →  PolygonPacket.DECODER.decode(buffer)
│     │                        └── getInt() [bodySize]
│     │                            fromBuffer() [lines: List veya UniformList]
│     │
│     └── ACTION         →  ActionPacket.DECODER.decode(buffer)
│                               └── getInt() [actionMask]
│                                   getInt() [bodySize]
│                                   mask & FORCE    → ForceAction.DECODER
│                                   mask & ROTATION → RotationAction.DECODER
│                                   mask & ATTACK   → AttackAction.DECODER
│                                   mask & BOOST    → BoostAction.DECODER
```
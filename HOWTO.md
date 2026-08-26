# Resource Data Pack Loader

**One folder that overrides anything Minecraft or a mod provides, defines new content from JSON, and controls what generates, in every world, on clients and servers, with nothing for players to switch on.**

**Одна папка. Переопределяет всё, что даёт Minecraft или мод, описывает новый контент в JSON и управляет генерацией мира — в любом мире, на клиенте и на сервере, и игроку ничего включать не надо.**

**Ein Ordner. Überschreibt alles, was Minecraft oder ein Mod mitbringt, beschreibt neuen Inhalt in JSON und steuert die Weltgenerierung – in jeder Welt, auf Client und Server, und der Spieler muss nichts einschalten.**

---

## Read the guide · Читать руководство · Handbuch lesen

| Language | Guide | |
| --- | --- | --- |
| **English** | **[HOWTO-en_us.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/howto_localized/HOWTO-en_us.md)** | Every folder, every key, every worldgen shape |
| **Русский** | **[HOWTO-ru_ru.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/howto_localized/HOWTO-ru_ru.md)** | Все папки, все ключи, все формы генерации |
| **Deutsch** | **[HOWTO-de_de.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/howto_localized/HOWTO-de_de.md)** | Jeder Ordner, jeder Schlüssel, jede Worldgen-Form |

This page is a signpost. Every language file holds the whole guide, not a summary.

Эта страница — только указатель. В каждом языковом файле лежит всё руководство целиком, а не пересказ.

Diese Seite ist nur ein Wegweiser. In jeder Sprachdatei steht das ganze Handbuch, keine Zusammenfassung.

**English is the source.** Translations follow it, so if something reads oddly or looks out of date, check the English file.

**Английский — исходный язык.** Переводы идут за ним, так что если что-то читается странно или выглядит устаревшим, сверьтесь с английским файлом.

**Englisch ist die Quelle.** Die Übersetzungen folgen ihr, wenn sich also etwas seltsam liest oder veraltet wirkt, sieh in der englischen Datei nach.

---

## Three working examples · Три готовых примера · Drei fertige Beispiele

Drop any of them straight into `rdploader` and look at how each file is written.

Положите любой прямо в `rdploader` и посмотрите, как написан каждый файл.

Leg eines davon direkt in `rdploader` und schau dir an, wie jede Datei geschrieben ist.

- **[RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip)** covers most features: blocks, items, biomes, a dimension, a world template and every worldgen shape.
  Покрывает большинство возможностей: блоки, предметы, биомы, измерение, шаблон мира и все формы генерации.
  Deckt die meisten Möglichkeiten ab: Blöcke, Items, Biome, eine Dimension, eine Weltvorlage und jede Worldgen-Form.
- **[RDPLExampleOrePackVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleOrePackVoid.zip)** makes the overworld an empty void with worldgen hanging in the air, one shape per height band, so each is easy to see on its own.
  Превращает обычный мир в пустоту, где генерация висит в воздухе по одной форме на полосу высоты — так каждую видно отдельно.
  Verwandelt die Oberwelt in eine leere Void-Welt, in der die Generierung frei in der Luft hängt, eine Form pro Höhenband – so ist jede einzeln gut zu sehen.
- **[RDPLExampleDeepWorld.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleDeepWorld.zip)** makes the overworld a rubic world with 256 blocks of generated world below the vanilla one and 128 above it: the deep stone blend, modern noise caves, ravines, banded ore veins, three cave regions to descend through, and floating islands overhead cut by the same noise.
  Превращает обычный мир в мир Rubic, где под ванильным лежит ещё 256 блоков сгенерированного мира, а над ним 128: переход к глубинному камню, современные шумовые пещеры, ущелья, полосчатые рудные жилы, три пещерных региона, сквозь которые спускаешься, и парящие острова наверху, вырезанные тем же шумом.
  Macht die Oberwelt zu einer Rubic-Welt mit 256 Blöcken generierter Welt unter der von Vanilla und 128 darüber: der Übergang zum Tiefenstein, moderne Rausch-Höhlen, Schluchten, gebänderte Erzadern, drei Höhlenregionen, durch die man absteigt, und schwebende Inseln darüber, die dasselbe Rauschen schneidet.

---

## Adding a language · Добавить язык · Sprache hinzufügen

Copy `howto_localized/HOWTO-en_us.md` to `howto_localized/HOWTO-<code>.md`, translate the prose, and add a row to the table above. Leave every JSON block, key name, folder name and command exactly as it is: those are what a pack author types.

Скопируйте `howto_localized/HOWTO-en_us.md` в `howto_localized/HOWTO-<код>.md`, переведите текст и добавьте строку в таблицу выше. Все блоки JSON, имена ключей, имена папок и команды оставьте как есть: именно их набирает автор пака.

Kopiere `howto_localized/HOWTO-en_us.md` nach `howto_localized/HOWTO-<code>.md`, übersetze den Text und füge der Tabelle oben eine Zeile hinzu. Alle JSON-Blöcke, Schlüsselnamen, Ordnernamen und Befehle bleiben genau so, wie sie sind: Genau die tippt ein Pack-Autor.

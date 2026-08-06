# Resource Data Pack Loader

**One folder that overrides anything Minecraft or a mod provides, defines new content from JSON, and controls what generates, in every world, on clients and servers, with nothing for players to switch on.**

**Одна папка. Переопределяет всё, что даёт Minecraft или мод, описывает новый контент в JSON и управляет генерацией мира — в любом мире, на клиенте и на сервере, и игроку ничего включать не надо.**

---

## Read the guide · Читать руководство

| Language | Guide | |
| --- | --- | --- |
| **English** | **[HOWTO-en_us.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/howto_localized/HOWTO-en_us.md)** | Every folder, every key, every worldgen shape |
| **Русский** | **[HOWTO-ru_ru.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/howto_localized/HOWTO-ru_ru.md)** | Все папки, все ключи, все формы генерации |

This page is a signpost. Every language file holds the whole guide, not a summary.

Эта страница — только указатель. В каждом языковом файле лежит всё руководство целиком, а не пересказ.

**English is the source.** Translations follow it, so if something reads oddly or looks out of date, check the English file.

**Английский — исходный язык.** Переводы идут за ним, так что если что-то читается странно или выглядит устаревшим, сверьтесь с английским файлом.

---

## Two working examples · Два готовых примера

Drop either straight into `rdploader` and look at how each file is written.

Положите любой прямо в `rdploader` и посмотрите, как написан каждый файл.

- **[RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip)** covers most features: blocks, items, biomes, a dimension, a world template and every worldgen shape.
  Покрывает большинство возможностей: блоки, предметы, биомы, измерение, шаблон мира и все формы генерации.
- **[RDPLExampleOrePackVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleOrePackVoid.zip)** makes the overworld an empty void with worldgen hanging in the air, one shape per height band, so each is easy to see on its own.
  Превращает обычный мир в пустоту, где генерация висит в воздухе по одной форме на полосу высоты — так каждую видно отдельно.

---

## Adding a language · Добавить язык

Copy `howto_localized/HOWTO-en_us.md` to `howto_localized/HOWTO-<code>.md`, translate the prose, and add a row to the table above. Leave every JSON block, key name, folder name and command exactly as it is: those are what a pack author types.

Скопируйте `howto_localized/HOWTO-en_us.md` в `howto_localized/HOWTO-<код>.md`, переведите текст и добавьте строку в таблицу выше. Все блоки JSON, имена ключей, имена папок и команды оставьте как есть: именно их набирает автор пака.

# Resource Data Pack Loader

**One folder that overrides anything Minecraft or a mod provides, defines new content from JSON, and controls what generates, in every world, on clients and servers, with nothing for players to switch on.**

**Одна папка. Переопределяет всё, что даёт Minecraft или мод, описывает новый контент в JSON и управляет генерацией мира — в любом мире, на клиенте и на сервере, и игроку ничего включать не надо.**

**Ein Ordner. Überschreibt alles, was Minecraft oder ein Mod mitbringt, beschreibt neuen Inhalt in JSON und steuert die Weltgenerierung – in jeder Welt, auf Client und Server, und der Spieler muss nichts einschalten.**

This is the guide for the 1.20.1 and 1.21.1 builds, which read the same packs.

Это руководство для сборок 1.20.1 и 1.21.1, которые читают одни и те же паки.

Dies ist das Handbuch für die Builds 1.20.1 und 1.21.1, die dieselben Packs lesen.

---

## Read the guide · Читать руководство · Handbuch lesen

| Language | Guide | |
| --- | --- | --- |
| **English** | **[HOWTO-en_us.md](howto_localized/HOWTO-en_us.md)** | Every folder, every key, every worldgen shape |
| **Русский** | **[HOWTO-ru_ru.md](howto_localized/HOWTO-ru_ru.md)** | Все папки, все ключи, все формы генерации |
| **Deutsch** | **[HOWTO-de_de.md](howto_localized/HOWTO-de_de.md)** | Jeder Ordner, jeder Schlüssel, jede Worldgen-Form |

This page is a signpost. Every language file holds the whole guide, not a summary.

Эта страница — только указатель. В каждом языковом файле лежит всё руководство целиком, а не пересказ.

Diese Seite ist nur ein Wegweiser. In jeder Sprachdatei steht das ganze Handbuch, keine Zusammenfassung.

**English is the source.** Translations follow it, so if something reads oddly or looks out of date, check the English file.

**Английский — исходный язык.** Переводы идут за ним, так что если что-то читается странно или выглядит устаревшим, сверьтесь с английским файлом.

**Englisch ist die Quelle.** Die Übersetzungen folgen ihr, wenn sich also etwas seltsam liest oder veraltet wirkt, sieh in der englischen Datei nach.

---

## One working example · Один готовый пример · Ein fertiges Beispiel

Drop it straight into `rdploader` and look at how each file is written.

Положите его прямо в `rdploader` и посмотрите, как написан каждый файл.

Leg es direkt in `rdploader` und schau dir an, wie jede Datei geschrieben ist.

- **[RDPLRubyExample.zip](example/RDPLRubyExample.zip)** uses every kind of file this version of the loader reads, all of it named for ruby: every block and item type, the things it changes about vanilla, the world it generates, the dimension under that world, and the screens the player sees on the way in. It ships no images at all: every texture is a pixel map drawn in JSON.
  Использует все виды файлов, которые читает эта версия загрузчика, и всё в нём названо в честь рубина: все типы блоков и предметов, то, что он меняет в ванили, мир, который он генерирует, измерение под этим миром и экраны, которые игрок видит на входе. В нём нет ни одной картинки: каждая текстура — пиксельная карта, нарисованная в JSON.
  Nutzt jede Art von Datei, die diese Version des Loaders liest, und alles darin ist nach dem Rubin benannt: jeder Block- und Item-Typ, das, was er an Vanilla ändert, die Welt, die er generiert, die Dimension unter dieser Welt und die Bildschirme, die der Spieler beim Betreten sieht. Er enthält kein einziges Bild: Jede Textur ist eine in JSON gezeichnete Pixelkarte.

---

## Adding a language · Добавить язык · Sprache hinzufügen

Copy `howto_localized/HOWTO-en_us.md` to `howto_localized/HOWTO-<code>.md`, translate the prose, and add a row to the table above. Leave every JSON block, key name, folder name and command exactly as it is: those are what a pack author types.

Скопируйте `howto_localized/HOWTO-en_us.md` в `howto_localized/HOWTO-<код>.md`, переведите текст и добавьте строку в таблицу выше. Все блоки JSON, имена ключей, имена папок и команды оставьте как есть: именно их набирает автор пака.

Kopiere `howto_localized/HOWTO-en_us.md` nach `howto_localized/HOWTO-<code>.md`, übersetze den Text und füge der Tabelle oben eine Zeile hinzu. Alle JSON-Blöcke, Schlüsselnamen, Ordnernamen und Befehle bleiben genau so, wie sie sind: Genau die tippt ein Pack-Autor.

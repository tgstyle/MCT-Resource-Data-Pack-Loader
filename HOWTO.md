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

## Eight working examples · Восемь готовых примеров · Acht fertige Beispiele

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
- **[RDPLExampleContainers.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleContainers.zip)** adds blocks and carried items that hold an inventory, in every size from three slots to the largest allowed, with a loot table, the chest model tinted out of the vanilla sheet, every texture drawn as a pixel map, and two pouches that can be worn in Baubles.
  Добавляет блоки и носимые предметы с инвентарём — всех размеров, от трёх ячеек до самого большого разрешённого, с таблицей добычи, моделью сундука, перекрашенной из ванильного листа, каждой текстурой, нарисованной пиксельной картой, и двумя сумками, которые можно носить в Baubles.
  Fügt Blöcke und getragene Gegenstände hinzu, die ein Inventar halten, in jeder Größe von drei Plätzen bis zur größten erlaubten, mit einer Beutetabelle, dem aus dem Vanilla-Blatt eingefärbten Truhenmodell, jeder Textur als Pixelkarte gezeichnet und zwei Beuteln, die sich in Baubles tragen lassen.
- **[RDPLExampleMegaCity32.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleMegaCity32.zip)** makes a superflat world holding one deliberately enormous village, grown to a thousand plots and pinned to the origin, with the streets dressed as concrete roads, sidewalks, dashed centers and lamp posts, and the buildings composed from structure maps in four sizes and three facades rather than from vanilla houses.
  Делает суперплоский мир с одной нарочно огромной деревней, выросшей до тысячи участков и закреплённой в начале координат: бетонные дороги, тротуары, пунктир по центру и фонари, а здания собраны из карт структур четырёх размеров и трёх фасадов, а не из ванильных домов.
  Erzeugt eine Superflat-Welt mit einem absichtlich riesigen Dorf, auf tausend Grundstücke gewachsen und am Ursprung festgesetzt, mit Straßen aus Beton, Gehwegen, gestrichelter Mitte und Laternen, und mit Gebäuden aus Strukturkarten in vier Größen und drei Fassaden statt aus Vanilla-Häusern.
- **[RDPLExampleMegaCity64.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleMegaCity64.zip)** is that same city on a rubic world with its ceiling at 512 and the clouds lifted to 384, so towers stand 256 blocks over the street, and every district rolls a block depth of 16, 32 or 64 so a coarse grid mixes with a fine one.
  Тот же город в мире Rubic с потолком на 512 и облаками на 384, так что башни поднимаются на 256 блоков над улицей, а каждый район разыгрывает глубину квартала 16, 32 или 64, и крупная сетка мешается с мелкой.
  Dieselbe Stadt auf einer Rubic-Welt mit Decke bei 512 und Wolken auf 384, sodass Türme 256 Blöcke über der Straße stehen, und jeder Bezirk würfelt eine Blocktiefe von 16, 32 oder 64, sodass sich ein grobes Raster mit einem feinen mischt.
- **[RDPLExampleCityCustomMap.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleCityCustomMap.zip)** draws that same city from a city map instead of rolling it: one grid of characters at 48 blocks a cell, with a palette naming streets, plazas, alleys and weighted picks of building, so the block plan is laid out by hand.
  Рисует тот же город по карте города, а не разыгрывает его: одна символьная сетка по 48 блоков на ячейку с палитрой улиц, площадей, переулков и взвешенного выбора зданий, так что план кварталов выложен вручную.
  Zeichnet dieselbe Stadt aus einer Stadtkarte, statt sie zu würfeln: ein Zeichenraster mit 48 Blöcken je Zelle und einer Palette für Straßen, Plätze, Gassen und gewichtete Gebäudeauswahlen, sodass der Blockplan von Hand gelegt ist.
- **[MCTKamikazeDemo.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/MCTKamikazeDemo.zip)** pits four factions against each other in a bedrock arena under permanent night: each side is a real vanilla scoreboard team its mobs join as they spawn, a side scores for every mob of another side it kills, and after ten minutes the standings come up as a card.
  Сталкивает четыре фракции на арене из бедрока под вечной ночью: каждая сторона — настоящая ванильная команда табло, в которую её мобы вступают при появлении, сторона получает очко за каждого убитого моба другой стороны, а через десять минут итоги выводятся карточкой.
  Lässt vier Fraktionen in einer Bedrock-Arena unter ewiger Nacht aufeinander los: jede Seite ist ein echtes Vanilla-Scoreboard-Team, dem ihre Mobs beim Spawnen beitreten, eine Seite punktet für jeden Mob einer anderen Seite, den sie tötet, und nach zehn Minuten erscheint der Stand als Karte.

---

## Adding a language · Добавить язык · Sprache hinzufügen

Copy `howto_localized/HOWTO-en_us.md` to `howto_localized/HOWTO-<code>.md`, translate the prose, and add a row to the table above. Leave every JSON block, key name, folder name and command exactly as it is: those are what a pack author types.

Скопируйте `howto_localized/HOWTO-en_us.md` в `howto_localized/HOWTO-<код>.md`, переведите текст и добавьте строку в таблицу выше. Все блоки JSON, имена ключей, имена папок и команды оставьте как есть: именно их набирает автор пака.

Kopiere `howto_localized/HOWTO-en_us.md` nach `howto_localized/HOWTO-<code>.md`, übersetze den Text und füge der Tabelle oben eine Zeile hinzu. Alle JSON-Blöcke, Schlüsselnamen, Ordnernamen und Befehle bleiben genau so, wie sie sind: Genau die tippt ein Pack-Autor.

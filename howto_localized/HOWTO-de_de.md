# Resource Data Pack Loader

**Ein Ordner, der alles überschreibt, was Minecraft oder ein Mod mitbringt, neuen Inhalt aus JSON beschreibt und steuert, was generiert wird – in jeder Welt, auf Client und Server, und ohne dass Spieler etwas einschalten müssen.**

Ein fertiges Beispiel. Leg es direkt in `rdploader` und schau dir an, wie jede Datei geschrieben ist.

- [RDPLRubyExample.zip](../example/RDPLRubyExample.zip) nutzt jede Art von Datei, die diese Version des Loaders liest, und alles darin ist nach dem Rubin benannt: jeder Block- und Item-Typ, das, was er an Vanilla ändert, die Welt, die er generiert, die Dimension unter dieser Welt und die Bildschirme, die der Spieler beim Betreten sieht. Er enthält kein einziges Bild: Jede Textur ist eine in JSON gezeichnete Pixelkarte.

Dieses Handbuch gilt für die Builds 1.20.1 und 1.21.1. Sie lesen dieselben Packs; die wenigen Stellen, an denen sich die beiden unterscheiden, sind mit **1.20.1** und **1.21.1** markiert.

---

## Inhalt

**Erste Schritte**
- [Was es ist](#was-es-ist)
- [Wo die Dateien liegen](#wo-die-dateien-liegen)
- [Die Tabellen lesen](#die-tabellen-lesen)
- [Die eine Regel](#die-eine-regel)
- [Packs organisieren](#packs-organisieren)
- [Ressourcenpakete: wer gewinnt](#ressourcenpakete-wer-gewinnt)
- [Mod-API](#mod-api)
- [Packs für 1.12.2](#packs-für-1122)
- [Packs nur auf dem Server](#packs-nur-auf-dem-server)

**Überschreiben**
- [Was du überschreiben kannst](#was-du-überschreiben-kannst)
- [Eigenschaften überschreiben](#eigenschaften-überschreiben)
- [Registry-Umbenennungen](#registry-umbenennungen)
- [Spielerbeute](#spielerbeute)

**Neuen Inhalt beschreiben**
- [Wie Definitionen funktionieren](#wie-definitionen-funktionieren)
- [Blöcke](#blöcke)
- [Modelle, Blockstates und Texturen](#modelle-blockstates-und-texturen)
- [Damit Vanilla deinen Block richtig behandelt](#damit-vanilla-deinen-block-richtig-behandelt)
- [Items](#items)
- [Flüssigkeiten](#flüssigkeiten)
- [Materialien, Tabs, Sounds, Tags](#materialien-tabs-sounds-tags)
- [Ofenrezepte und Brennstoffe](#ofenrezepte-und-brennstoffe)
- [Tränke, Trankarten und Brauen](#tränke-trankarten-und-brauen)
- [Expositionen](#expositionen)
- [Dorfbewohner und Handel](#dorfbewohner-und-handel)
- [Entity-Varianten](#entity-varianten)
- [Dorfgrundstücke](#dorfgrundstücke)
- [Biome](#biome)
- [Dimensionen](#dimensionen)
- [Behälter](#behälter)
- [Portale und Tore](#portale-und-tore)
- [Weltvorlagen](#weltvorlagen)
- [Die Tiefenwelt](#die-tiefenwelt)
- [Höhlenregionen](#höhlenregionen)
- [Welt-Intro](#welt-intro)
- [Spielregeln](#spielregeln)
- [Teams](#teams)
- [Wertung](#wertung)
- [Härtegruppen](#härtegruppen)

**Generieren**
- [Worldgen-Einträge](#worldgen-einträge)
- [Formen](#formen)
- [Strukturkarten](#strukturkarten)
- [Stadtpläne](#stadtpläne)
- [Verteilung](#verteilung)
- [Retrogen](#retrogen)
- [Vorgenerierung](#vorgenerierung)

**Steuerung**
- [Die Steuerungsebene](#die-steuerungsebene)
- [Was jede Gruppe macht](#was-jede-gruppe-macht)
- [Blast Plaster Integration](#blast-plaster-integration)

**Referenz**
- [Wertelisten](#wertelisten)
- [Ordnerliste](#ordnerliste)
- [Befehle](#befehle)
- [Gut zu wissen](#gut-zu-wissen)
- [Wenn etwas nicht funktioniert](#wenn-etwas-nicht-funktioniert)
- [Bonus: Vanilla-Tweaks](#bonus-vanilla-tweaks)

---

# Erste Schritte

## Was es ist

Der Resource Data Pack Loader (RDPL) liest einen einzigen Ordner, `rdploader`, und erledigt drei Aufgaben:

- **Überschreiben.** Eine Datei im Ordner ersetzt die, die das Spiel oder ein Mod geladen hätte. Kein Schalter, keine Einrichtung pro Welt, nichts, was Spieler aktivieren müssen.
- **Neuer Inhalt.** JSON-Definitionen registrieren Blöcke, Items, Flüssigkeiten, Biome, Dimensionen, Tränke und Dorfbewohner. Kein Java, kein Jar.
- **Steuerung.** Erz-, Biom-, Struktur- oder Rezeptgenerierung blockieren, Grundgestein glätten, Spawnraten setzen, die Oberwelt leeren, Weltvorgaben festlegen.

## Wo die Dateien liegen

Ein Pack hat zwei Wurzeln, dieselben zwei wie ein Vanilla-Pack. `assets/` hält, was der Client zeichnet und hört: Modelle, Blockstates, Texturen, Sprachdateien, Sounds und die Texte des Intros. `data/` hält alles andere: jede Definition, die dieser Mod liest, und die Vanilla-Datendateien, die ein Pack ersetzt. Jeder Pfad in diesem Handbuch ist ab dem Namespace geschrieben, `<namespace>/blocks/*.json` ist auf der Platte also `data/mypack/blocks/ruby_ore.json` für ein Pack mit dem Namespace `mypack`, und `<namespace>/models/` ist `assets/mypack/models/`. Jeder Abschnitt wiederholt seinen eigenen Pfad unter der Überschrift.

Unter `data/`:

| Pfad | Was darin liegt |
| --- | --- |
| `<namespace>/blocks/*.json` | Blockdefinitionen. [Blöcke](#blöcke) |
| `<namespace>/items/*.json` | Itemdefinitionen. [Items](#items) |
| `<namespace>/fluids/*.json` | Flüssigkeiten, mit Block und Eimer. [Flüssigkeiten](#flüssigkeiten) |
| `<namespace>/materials/*.json` | Werkzeug- und Rüstungsmaterialien. [Materialien, Tabs, Sounds, Tags](#materialien-tabs-sounds-tags) |
| `<namespace>/tabs/*.json` | Kreativtabs. [Materialien, Tabs, Sounds, Tags](#materialien-tabs-sounds-tags) |
| `<namespace>/sounds/*.json` | Sound-Events. [Materialien, Tabs, Sounds, Tags](#materialien-tabs-sounds-tags) |
| `<namespace>/biomes/*.json` | Biomdefinitionen. [Biome](#biome) |
| `<namespace>/worldgen/*.json` | Was generiert, und wo. [Worldgen-Einträge](#worldgen-einträge) |
| `<namespace>/caveregions/*.json` | Benannte Regionen, über den Untergrund gelegt. [Höhlenregionen](#höhlenregionen) |
| `<namespace>/dimensions/*.json` | Dimensionsdefinitionen. [Dimensionen](#dimensionen) |
| `<namespace>/worldtemplates/*.json` | Die Einstellungen einer ganzen Welt in einer Datei. [Weltvorlagen](#weltvorlagen) |
| `<namespace>/worldintro/*.json` | Seiten, die beim Betreten der Welt gezeigt werden. [Welt-Intro](#welt-intro) |
| `<namespace>/gates/*.json` | Bedingungen für Portale und Dimensionen. [Portale und Tore](#portale-und-tore) |
| `<namespace>/gamerules/*.json` | Spielregeln für neue Welten. [Spielregeln](#spielregeln) |
| `<namespace>/teams/*.json` | Seiten auf dem Vanilla-Scoreboard und wer ihnen beitritt. [Teams](#teams) |
| `<namespace>/scoring/*.json` | Ziele, Punkte und wie eine Partie endet. [Wertung](#wertung) |
| `<namespace>/entities/*.json` | Entity-Varianten, aufgebaut auf vorhandenen Entities. [Entity-Varianten](#entity-varianten) |
| `<namespace>/hardness/*.json` | Faktoren für Abbauzeit und Explosionswiderstand für Blockgruppen. [Härtegruppen](#härtegruppen) |
| `<namespace>/exposures/*.json` | Gefahren, denen Spieler nahe an oder beim Tragen benannter Blöcke und Items ausgesetzt sind. [Expositionen](#expositionen) |
| `<namespace>/overrides/<target>/<name>.json` | Eigenschaften vorhandener Blöcke, Items und Tranktypen, direkt geändert. [Eigenschaften überschreiben](#eigenschaften-überschreiben) |
| `<namespace>/villages/*.json` | Grundstücke, die eine Stadt oder ein Dorf bauen kann. [Dorfgrundstücke](#dorfgrundstücke) |
| `<namespace>/pathintersects/*.json` | Muster, die an Straßenkreuzungen gemalt werden. [Dorfgrundstücke](#dorfgrundstücke) |
| `<namespace>/structuremaps/*.json` | Vorlagen, auf einem Raster zu einem großen Bauwerk zusammengesetzt. [Strukturkarten](#strukturkarten) |
| `<namespace>/citymaps/*.json` | Ein gezeichneter Straßenplan, nach dem eine Stadt angelegt wird, statt einen zu würfeln. [Stadtpläne](#stadtpläne) |
| `<namespace>/portalframes/*.json` | Rahmen, die ein Spieler bauen und anzünden kann. [Portale und Tore](#portale-und-tore) |
| `<namespace>/blastplaster/*.json` | Was Blast Plaster nach einer Explosion tut, pro Dimension. [Blast Plaster Integration](#blast-plaster-integration) |
| `<namespace>/structures/*.nbt` | Vorlagen, für Setzlinge, `imprint`, Strukturkarten und Mod-Overrides. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/recipes/*.json` | Handwerksrezepte, hinzugefügt oder ersetzt. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/recipe_removals/*.json` | Rezepte, gelöscht nach Name, Namespace oder Ergebnis. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/furnace/*.json` | Ofenrezepte, hinzugefügt und entfernt. [Ofenrezepte und Brennstoffe](#ofenrezepte-und-brennstoffe) |
| `<namespace>/fuels/*.json` | Brenndauern. [Ofenrezepte und Brennstoffe](#ofenrezepte-und-brennstoffe) |
| `<namespace>/brewing/*.json` | Rezepte für den Braustand. [Tränke, Trankarten und Brauen](#tränke-trankarten-und-brauen) |
| `<namespace>/potions/*.json` | Trankeffekte. [Tränke, Trankarten und Brauen](#tränke-trankarten-und-brauen) |
| `<namespace>/potion_types/*.json` | Abgefüllte Tränke aus diesen Effekten. [Tränke, Trankarten und Brauen](#tränke-trankarten-und-brauen) |
| `<namespace>/villagers/*.json` | Berufe der Dorfbewohner. [Dorfbewohner und Handel](#dorfbewohner-und-handel) |
| `<namespace>/trades/*.json` | Was Berufe kaufen und verkaufen. [Dorfbewohner und Handel](#dorfbewohner-und-handel) |
| `<namespace>/loot_tables/*.json` | Beutetabellen, ersetzt. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/loot_injections/*.json` | Ein Pool, der zu einer bestehenden Tabelle dazukommt. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/player_loot/*.json` | Eine Beutetabelle, die beim Tod eines Spielers ausgewürfelt wird. [Spielerbeute](#spielerbeute) |
| `<namespace>/advancements/*.json` | Fortschritte. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/functions/*.mcfunction` | Funktionsdateien. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/tags/<kind>/*.json` | Tags, im Format des Spiels selbst. [Materialien, Tabs, Sounds, Tags](#materialien-tabs-sounds-tags) |
| `<namespace>/registry_remap/*.json` | Alte Namen, auf neue abgebildet. [Registry-Umbenennungen](#registry-umbenennungen) |

Unter `assets/`:

| Pfad | Was darin liegt |
| --- | --- |
| `<namespace>/models/`, `<namespace>/blockstates/`, `<namespace>/textures/`, `<namespace>/lang/` | Die üblichen Asset-Ordner. [Modelle, Blockstates und Texturen](#modelle-blockstates-und-texturen) |
| `<namespace>/sounds.json` | Der Sound-Index, den das Spiel liest, neben den `sounds/`-Definitionen unter `data/` |
| `<namespace>/texts/*.txt` | Reine Textdateien, genutzt vom Welt-Intro. [Welt-Intro](#welt-intro) |

**1.21.1** benennt die Vanilla-Datenordner in der Einzahl: `loot_table/`, `recipe/`, `advancement/`, `function/`, `structure/`, `tags/item/`, `tags/block/`. Ein Pack darf dort beide Schreibweisen verwenden; die Mehrzahlnamen oben werden als ihre Einzahl-Zwillinge gelesen, sodass ein Pack beide Builds bedient.

## Die Tabellen lesen

Jede Datei ist gewöhnliches JSON. Ein repräsentativer Worldgen-Eintrag:

```json
{
  "blocks": [
    { "block": "minecraft:magenta_wool", "weight": 80 },
    { "block": "mypack:ruby_ore", "weight": 20 }
  ],
  "size": { "min": 4, "max": 12 },
  "attempts": 12,
  "maxTemperature": 0.5,
  "sparse": true,
  "replace": ["minecraft:stone", "minecraft:andesite"],
  "dimensions": ["minecraft:overworld", "minecraft:the_nether"]
}
```

Die Schlüsseltabellen in diesem Dokument nennen, ob ein Schlüssel Pflicht ist, was er enthält und den Standardwert, wenn er fehlt. Werte, die der Parser nicht kennt, landen im Log und werden durch den Standard ersetzt; das Spiel stürzt daran nicht ab. Die durchgehend verwendeten Werttypen:

| Wenn in der Tabelle steht | Du schreibst |
| --- | --- |
| int | `8` |
| int, Ticks | `100` (20 Ticks = 1 Sekunde) |
| int oder Bereich | `8`, oder `{ "min": 4, "max": 12 }`, um dazwischen zu würfeln |
| 0 bis 15, 1 bis 100 und Ähnliches | ein Int in diesen Grenzen |
| float | `0.5` |
| boolean | `true` oder `false` |
| string | `"Wörter in Anführungszeichen"` |
| Blockname, Itemname | `"minecraft:stone"`. Ein Blockzustand ist der Name mit `properties` daneben: `{ "block": "minecraft:oak_log", "properties": { "axis": "x" } }` |
| `namespace:name` | `"mypack:ruby_ore"` |
| Biomname, Soundname, Tab-Name | dieselbe Form `namespace:name` in Anführungszeichen |
| Dimensions-ID | `"minecraft:overworld"`, `"minecraft:the_nether"`, `"minecraft:the_end"` oder die eigene eines Packs, `"mypack:verdant"`. Die 1.12.2-Zahlen `0`, `-1` und `1` werden weiterhin als diese drei gelesen |
| Hex-Farbe | sechs Hex-Ziffern, `"A0C8FF"`, `#` optional |
| Texturpfad | `"mypack:block/ruby_ore"` |
| Liste von Ints | `[4, 12]` |
| Liste von Blocknamen | `["minecraft:stone", "minecraft:andesite"]` |
| Liste von Biomnamen | `["minecraft:windswept_hills", "mypack:ruby_hills"]` |
| Liste von Biomtypen | `["mountain", "forest"]`, die Typwörter aus den [Wertelisten](#wertelisten), jedes steht für einen Biom-Tag |
| Liste von Mod-IDs oder Pack-Namespaces | `["quark", "mypack"]` |
| Liste von Objekten | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`, Schlüssel gemäß der eigenen Tabelle des Objekts |
| Objekt | `{ "type": "cluster" }`, Schlüssel gemäß eigener Tabelle |
| Objekt aus Rolle zu Biom, aus Variantenname zu Variante | Schlüssel sind das Erste, Werte das Zweite: `{ "ocean": "mypack:ruby_ocean" }` |

Die meisten Definitionen nehmen außerdem `requires` an, eine Liste von Mod-IDs oder Pack-Namespaces, die vorhanden sein müssen, sonst wird die Datei übersprungen.

## Die eine Regel

Öffne das Jar, such die Datei, die du ändern willst, und kopiere ihren Pfad ab `assets` oder `data`:

```
assets/minecraft/textures/block/iron_ore.png                 (im Minecraft-Jar)
rdploader/assets/minecraft/textures/block/iron_ore.png       (dein Override)

data/minecraft/loot_tables/blocks/iron_ore.json              (im Minecraft-Jar)
rdploader/data/minecraft/loot_tables/blocks/iron_ore.json    (dein Override)
```

Der Pfad nach `assets` oder `data` ist immer identisch mit dem Pfad im Jar. Nichts wird umbenannt oder verschoben.

## Packs organisieren

Lose Dateien funktionieren unter `rdploader/assets/<namespace>/` und `rdploader/data/<namespace>/`. Bündeln funktioniert auch, als Zip. Ein Ordner in `rdploader` ist nie ein Pack: er wird mit einer Warnung im Log übersprungen, also zippe ein Pack, bevor es dorthin kommt. Wähle beim Zippen den Inhalt aus und zippe diesen, nicht den Ordner, der ihn hält: ein Zip, dessen oberste Ebene ein einzelner Ordner um `assets` oder `data` ist, wird übersprungen, und das Log sagt es.

```
rdploader/assets/minecraft/textures/block/iron_ore.png
rdploader/MyTextures.zip
```

**Priorität.** Enthalten zwei Packs dieselbe Datei, stell den Namen `RDPL` und eine Zahl voran; höhere Zahlen laden später und gewinnen:

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

Groß-/Kleinschreibung ist egal; ein Leerzeichen, Bindestrich oder Unterstrich nach der Zahl ist optional; das Präfix wird im Anzeigenamen ausgeblendet. Ein Pack ohne Präfix lädt zuerst und verliert gegen jedes nummerierte. Die Priorität bestimmt auch die Reihenfolge der Worldgen-Einträge, was wichtig ist, wenn ein Pack Blöcke setzt, die ein anderes ersetzt.

**Ein Pack deaktivieren:** `.disabled` an den Namen anhängen.

Eine `pack.mcmeta` in der Wurzel des Zips ist willkommen, aber nicht nötig: Der Mod stellt jedes Pack dem Spiel unter einem einzigen eigenen Eintrag vor, mit dem Pack-Format, das das Spiel erwartet, sodass ein Pack nie an einer Formatnummer veraltet. Leg eine `pack.png` daneben, um dem Eintrag des Ordners ein Symbol zu geben.

## Ressourcenpakete: wer gewinnt

Standardmäßig liegen RDPL-Dateien über den Ressourcenpaketen, die ein Spieler auswählt; ein Ressourcenpaket kann sie also nicht überschreiben. `O` oder `N` nach dem `RDPL`-Präfix entscheidet das pro Pack:

```
rdploader/RDPLO Branding        gewinnt immer; Ressourcenpakete kommen nicht heran
rdploader/RDPLN BaseTextures    ein Ressourcenpaket darf es überschreiben
rdploader/RDPL1O Seasonal       Priorität und Override kombiniert
```

Packs ohne Buchstaben folgen der Config-Option `overrideResourcePacks`. `/rdpl list` markiert die überschreibenden Packs. Der Buchstabe muss das Präfix abschließen (gefolgt von Leerzeichen, Bindestrich, Unterstrich oder nichts), `RDPLOverhaul` ist also ein Pack namens `Overhaul`, kein `O`-Flag.

Dieselben Stufen gelten für Datenpakete. Ein mit `N` markiertes Pack liegt unter den Datenpaketen, die eine Welt in ihrem eigenen `datapacks`-Ordner trägt, und ein mit `O` markiertes darüber.

## Mod-API

Eine Mod kann RDPL-Inhalte in ihrer eigenen Jar mitbringen und braucht dafür kein eigenes Pack. Leg einen Ordner namens `rdploader` in die Wurzel der Jar und bau ihn genau wie ein Pack auf:

```
thatmod.jar
  META-INF/mods.toml                (1.21.1: META-INF/neoforge.mods.toml)
  rdploader/data/thatmod/blocks/ruby_ore.json
  rdploader/assets/thatmod/textures/block/ruby_ore.png
```

Was eine Mod mitbringt, ist eine Vorgabe, keine Überschreibung. Es lädt unter jedem Pack im Pack-Ordner, alles von einem Pack-Autor gewinnt also dagegen, und eine Mod darf nur Dateien unter einem Namespace liefern, den sie in ihrer eigenen Mods-Datei nennt. Dateien unter jedem anderen Namespace werden mit einer Warnung übergangen, ebenso ein verschachtelter `rdploader`-Ordner innerhalb eines Namespace, damit keine Mod stillschweigend die Inhalte einer anderen Mod oder eines Pack-Autors umschreibt.

Jede Mod, die so etwas mitbringt, bekommt beim ersten Erkennen einen Eintrag in `rdploader/config/mods.json`:

```json
{
  "thatmod": {
    "enabled": true,
    "priority": -1
  }
}
```

| Feld | Werte | Vorgabe | Was es tut |
| --- | --- | --- | --- |
| `enabled` | `true` oder `false` | `true` | Schaltet die Inhalte dieser Mod ab, so wie `.disabled` ein Pack abschaltet |
| `priority` | `-1` oder eine Zahl | `-1` | `-1` hält die Mod unter jedem Pack; jede andere Zahl setzt sie in die gewöhnliche [Prioritäts](#packs-organisieren)-Reihenfolge neben die nummerierten Packs |

Ein Mod-Pack kommt nie in die Überschreibungsstufe der Ressourcenpakete, egal was `overrideResourcePacks` sagt, denn darum kann nur ein Pack-Autor mit dem Buchstaben `O` bitten. Das Log kennzeichnet Mod-Packs und listet Packs mit dem niedrigsten zuerst, es lädt also nichts ungesehen.

## Packs für 1.12.2

Ein Pack, das für die 1.12.2-Linie gemacht wurde, lädt so, wie es ist. Der Loader erkennt eines an seinem `pack.mcmeta`-Format, an Definitionsordnern unter `assets/` ohne ein `data/` daneben oder an einer `.lang`-Datei, und trägt es nach vorn: Ein Zip wird als Pack dieser Version unter seinem eigenen Namen ausgeschrieben, mit allem Folgenden bereits erledigt, und das 1.12.2-Zip, aus dem es stammt, bleibt als `<name>_converted.zip.disabled` daneben liegen, sodass nichts verloren geht und das neue Pack deins ist, um es fertigzustellen und zu bearbeiten. Lose Dateien unter `rdploader/assets` werden nicht umgeschrieben; sie werden bei jedem Scannen des Ordners durch dieselbe Portierung gelesen.

- Definitionsordner wandern von `assets/<namespace>/` nach `data/<namespace>/`, und die Vanilla-Datenordner mit ihnen: Rezepte, Beutetabellen, Beute-Injektionen, Fortschritte, Funktionen und Strukturen.
- `textures/blocks/` und `textures/items/` werden als `textures/block/` und `textures/item/` ausgeliefert, in Modellen, in Pixelkarten und in den Dateien selbst. Ein Item-Modell unter `models/item/<datei>/<variante>.json` wird als `models/item/<variante>.json` ausgeliefert.
- Jede ID mit Metadaten, `minecraft:wool:14` oder `minecraft:dye:4`, läuft durch die Data-Fixer des Spiels selbst, denselben Code, der eine 1.12.2-Welt aktualisiert, und kommt als der Block oder das Item heraus, zu dem sie wurde: `minecraft:red_wool`, `minecraft:lapis_lazuli`. Ein Blockzustand, der das Flattening als Eigenschaft überlebt hat, `minecraft:log:1` zu `minecraft:oak_log` mit `axis=y`, kommt als `properties`-Objekt heraus. Die eigenen IDs des Packs lösen sich über seine eigenen Definitionen auf: `mypack:materials:5` wird zur Variante, deren `meta` 5 war, und `mypack:ruby_ore` zur ersten Variante der Datei, da hier jede Variante ein eigener Block ist. Entity- und Biomnamen werden genauso repariert, und Dimensionsnummern werden zu IDs.
- `variants` behalten ihre Schlüssel; `meta` fällt weg und `oreDict` wird zu `tags`, über die Abbildung des Ore Dictionary auf die Konventions-Tags. Eine `oredict/*.json`-Datei wird zu einer Item-Tag-Datei pro Namen, den sie ergänzt. Ein bloßes `creativeTab` bekommt den Namespace des Packs.
- Eine `.lang`-Datei wird als das `.json` ausgeliefert, das das Spiel liest, mit `tile.mypack:datei.variante.name` als `block.mypack.variante`, `item.` ebenso, `itemGroup.x` als `itemGroup.mypack.x`, `fluid.x` als beide Fluid-Schlüssel, und alles andere wie geschrieben.
- Ein 1.12.2-Blockstate wird gar nicht ausgeliefert. Stattdessen werden seine Texturen gelesen und unter den Namen ausgeliefert, nach denen der Generator sucht, `textures/block/<variante>.png` mit `_top` und `_bottom`, wo der Blockstate `end`, `top` oder `bottom` hatte, sodass Blockstate und Modelle für jede Variante generiert werden, wie sie es für ein hier geschriebenes Pack würden.
- Rezepte verlieren ihr `data` und bekommen geglättete IDs, `forge:ore_shaped` wird zu `minecraft:crafting_shaped` mit `ore`-Zutaten als `tag`, Beutetabellen verlieren `set_data` auf dieselbe Weise, und das `item` mit `data` eines Fortschritts wird zu `items`.
- Funktionen werden wie geschrieben ausgeliefert, da eine 1.12.2-Befehlszeile nichts ist, was eine Portierung umschreiben kann, und das Log sagt es. `block_drops` hat keinen Zwilling und bleibt außen vor; das erledigt hier eine Beutetabelle.

Das Log trägt eine Zusammenfassungszeile pro portiertem Pack und eine Zeile für jede Datei, die es verschoben, ausgelassen oder nicht tragen konnte, und jeder Schlüssel, den diese Version nicht mehr liest, wird weiterhin vom Parser genannt, der ihm begegnet. Die Portierung ist ein bestmöglicher Versuch, kein fertiges Pack: Öffne das geschriebene Zip, lies diese Zeilen und stell von Hand fertig, was sie nennen, angefangen bei den Funktionen und jeder Textur, für die sie keinen Namen finden konnte.

## Packs nur auf dem Server

Ein Pack kann allein auf dem Server liegen, mit Spielern auf reinen Vanilla-Clients, unter einer Bedingung: **nichts darin darf irgendetwas registrieren**. Der Mod akzeptiert jede Gegenstelle; das Pack entscheidet. Ein Vanilla-Client spielt mit den Registries, die er mitgebracht hat; ein Pack, das sie erweitert, muss also auf beide Seiten.

| Server allein genügt | Pack muss auch auf den Client |
| --- | --- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures`, `structuremaps`, `citymaps`, `villages`, `pathintersects`, `caveregions`, `biomes`, `dimensions` | `blocks`, `items`, `fluids`, `materials`, `containers` |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `tags` | `potions`, `potion_types`, `sounds`, `tabs` |
| `loot_tables`, `loot_injections`, `player_loot`, `advancements`, `functions` | `entities`, `villagers`, `portalframes` |
| `gates`, `trades`, `registry_remap`, `teams`, `scoring`, `hardness`, `exposures`, `blastplaster` | `models`, `blockstates`, `textures`, `lang`, `worldintro`, `overrides` (Client-Ordner: ohne Client weglassen) |
| die ganze Steuerungsebene, Einstellungen und Vorgenerierung | |

Die rechte Spalte ist eine harte Grenze: Blöcke, Items, Entity-Typen, Sounds und Trankeffekte, die ein Vanilla-Client nicht hat, lassen sich ihm nicht beschreiben, und das eigene Portal einer Dimension ist einer der Blöcke des Packs. Die linke Spalte funktioniert, weil alles darin entweder vollständig serverseitig läuft, den Client als Datenpaket-Einträge erreicht, die Vanilla ohnehin liest (Biome, Höhlenregionen, Dimensionstypen, die Schadensarten aus exposures), oder ihn über Pakete erreicht, die Vanilla ohnehin spricht (vom Server gefülltes Ergebnisfeld der Werkbank, gewöhnliche Fortschrittspakete, Statusmeldungen bei abgelehnten Toren, ein Vorgenerierungs-Halt aus Vanilla-Paketen für Spielmodus, Titel und Teleport).

Einrichtung:

1. Schalte `vanillaClients` in der Config ein (Kategorie `content`, braucht einen Neustart). Das erzwingt die rechte Spalte: Diese Ordner werden beim Laden übersprungen, jede übersprungene Datei steht namentlich im Log, aus einer durchgerutschten Blockdatei wird eine Logzeile statt einer abgelehnten Verbindung.
2. Halte Definitionen trotzdem aus den rechten Ordnern heraus; übersprungene Dateien sind totes Gewicht. Wo das Pack Items nennt (das `hold` eines Tors, `killedDrops`, Rezeptergebnisse, Handel), nenne nur Items, die Vanilla oder die anderen beidseitigen Mods des Servers mitbringen. Ein Biom, das die eigenen Bodenblöcke des Packs nennt, behält den Boden des Basisbioms, und eine Dimension, die sich über ihr eigenes Portal öffnet, braucht diesen Portalblock; schicke Spieler per Befehl dorthin.
3. Entity-Varianten sind auf dieser Version eigene Entity-Typen und gehören damit in die rechte Spalte: Mit `vanillaClients` werden sie übersprungen, ihre Spawns mit ihnen, und das Log nennt sie.
4. Auf dem Server installieren wie üblich, mit Blast Plaster, das der Mod voraussetzt und das ebenfalls nichts registriert. Auf Spielerrechnern landet nichts; `/rdpl` existiert dort nicht.
5. Mit einem einzigen sauberen Vanilla-Client derselben Version testen. Fehler sind laut: Die Verbindung wird an der Tür abgelehnt, nicht später still kaputt.
6. Zwei akzeptierte kosmetische Lücken: Server-Rezepte lassen sich craften, erscheinen aber nicht im Rezeptbuch, und der Halt, während Land gemacht wird, ist ein schlichter Zuschauer-Halt mit dem Fortschritt in der Aktionsleiste, ohne den Nebel und das Logo, die der eigene Client des Mods zeichnet.

# Überschreiben

## Was du überschreiben kannst

- **Alles im assets-Ordner eines Mods**: Texturen, Modelle, Blockstates, Sprachdateien, Sounds, Schriftarten, Splash-Texte, Handbücher, Anleitungen
- **Fortschritte, Beutetabellen, Tags und Funktionen**, serverseitig, sie funktionieren also auch auf dedizierten Servern
- **Rezepte**: das Rezept eines Mods ersetzen oder ein eigenes hinzufügen
- **Strukturvorlagen**: die `.nbt`-Dateien, die Mods für generierte Gebäude nutzen, unter `<namespace>/structures/`
- **Registry-Umbenennungen**: alte Welten am Leben halten, wenn ein Mod einen Block oder ein Item umbenennt
- **Rezept-Entfernungen**: ein Handwerksrezept nach Name, Namespace oder Ergebnis löschen
- **Beute-Injektionen**: einen Pool zu einer Beutetabelle hinzufügen, statt sie komplett zu ersetzen
- **Spielerbeute**: beim Tod eines Spielers eine Beutetabelle auswürfeln, zusätzlich zu dem, was er dabeihatte, oder an dessen Stelle
- **Eigenschaften vorhandener Blöcke, Items und Tränke**: Härte, Licht, Stapelgrößen, Essbarkeit für alles, die Effekte eines Tranks, siehe [Eigenschaften überschreiben](#eigenschaften-überschreiben)
- **Ofenrezepte, Brenndauern, Kreativtabs und Sound-Events**

Was ein Block fallen lässt, ist auf dieser Version seine Beutetabelle, es gibt also keine eigene Blockdrop-Datei: Um zu ändern, was Stein fallen lässt, liefere `data/minecraft/loot_tables/blocks/stone.json`, und um etwas hinzuzufügen, ohne sie zu ersetzen, eine Beute-Injektion.

RDPL eignet sich gut dafür, ein oder zwei Rezepte zu ersetzen, und Rezepte für eigenen Inhalt gehören mit in dasselbe Pack. Für volle Rezeptkontrolle über ein ganzes Modpack sind KubeJS und CraftTweaker die besseren Werkzeuge, und eine Datei hier ersetzt das Original weiterhin vollständig, um also eine einzelne Zutat zu ändern oder einen einzelnen Beuteeintrag zu streichen, nimm die beiden.

### Pack-Optionen

Alle Schlüssel, die eine Optionsdatei annimmt:

```json
{
  "hide": false,
  "enableTestingContent": true,
  "enableLoserBlocks": {
    "default": false,
    "hide": true,
    "description": "Registers the loser blocks"
  }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er tut |
| --- | --- | --- | --- | --- |
| ein Optionsname | ja | boolean oder ein Objekt | | `true` oder `false` ist der Standard der Option. Ein Objekt trägt die drei Schlüssel darunter |
| `hide` auf oberster Ebene | nein | boolean | `false` | Hält die Optionen dieses Packs komplett aus dem Optionsbildschirm und aus der erzeugten Datei heraus, während sie den Inhalt weiterhin mit ihren Standardwerten steuern |
| `default` | nein | boolean | `false` | Der Wert der Option, bis der Nutzer ihn ändert |
| `hide` innerhalb einer Option | nein | boolean | `false` | Versteckt nur diese eine Option, sie kann also nicht umgelegt werden und bleibt auf ihrem Standard |
| `description` | nein | String | keine | Wird im Optionsbildschirm unter dem Namen der Option angezeigt |

Ein Pack kann neben seinem `assets` und `data` einen `config`-Ordner tragen, mit JSON-Dateien voller true/false-Optionen und ihren Standardwerten:

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

Eine Datei mit `"hide": true` auf oberster Ebene hält die Optionen dieses Packs komplett aus dem Optionsmenü und aus der erzeugten Datei heraus, während die Optionen den Inhalt weiterhin mit ihren Standardwerten steuern. Zwei Dinge wollen das: Inhalt, der noch nicht fertig ist, und Vorlagen-Packs, bei denen die Optionen Maschinerie sind, die die Definitionen zusammenhält, und keine Entscheidung, die irgendwer treffen sollte. Zum Veröffentlichen entfernst du den Schlüssel wieder. Dasselbe geht pro Option: `"hide": true` im Objekt einer Option versteckt nur diese eine, ein fertiges Pack kann also einen Schalter für unfertigen Inhalt oder ein Vorlagen-Tor tragen, ohne dass eines davon auftaucht:

    { "enablePackB": { "default": false, "hide": true } }

Da sich eine versteckte Option nicht umlegen lässt, ist eine versteckte Option mit Standardwert true faktisch fest eingeschaltet, für Inhalt, der durch die Options-Maschinerie verdrahtet bleiben muss, aber keine Wahl ist.

Eine Option kann auch ein Objekt mit einer Beschreibung sein, die im Optionsmenü unter ihrem Namen steht:

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

Beim Start werden die Optionsdateien eines Packs zu einer echten Config-Datei, die dem Nutzer gehört, benannt nach dem Pack: `rdploader/config/PackA.json`. Sie wird mit den Standardwerten des Packs angelegt und bei Pack-Updates zusammengeführt, sodass neue Optionen ankommen, ohne anzurühren, was der Nutzer schon eingestellt hat. Änderungen greifen beim nächsten Spielstart, und der Bildschirm Pack-Optionen im Titelbildschirm ist der Ort, an dem ein Spieler sie umlegt. Optionen gehören nur benannten Packs, also Zips, weil die erzeugte Datei nach dem Pack benannt ist; lose Dateien unter `rdploader/assets` und `rdploader/data` haben keinen Pack-Namen und tragen keine Optionen, zippe losen Inhalt also zu einem benannten Pack, wenn er einen Schalter braucht.

Die `requires`-Liste jeder Definition kann dann mit einem `config:`-Eintrag eine Option nennen: `"requires": ["config:enableTestingContent"]` registriert diesen Inhalt nur, solange die Option true ist, genau wie ein fehlender Mod ihn überspringen ließe. Ein bloßer Name prüft die Datei jedes Packs, und jedes Pack, das ihn definiert, muss zustimmen; `"config:PackA:enableTestingContent"` nennt ein bestimmtes Pack. Eine Option, die kein Pack definiert, gilt als false und wird einmal im Log vermerkt.

Eine Option, die etwas steuert, womit eine Welt gemacht wurde, merkt sich diese Welt. Ändere sie und öffne die Welt wieder, und die Welt wird vorher gesichert, in den eigenen `backups`-Ordner des Spiels, genau wie es der Bildschirm Welt bearbeiten tut; der erste Spieler in der Oberwelt erfährt, welche Optionen sich geändert haben und dass die Kopie gemacht wurde.

Ein `file:`-Eintrag hängt daran, dass eine Datei oder ein Ordner im Spielordner existiert, um Inhalt an etwas außerhalb von RDPLs eigenen Packs zu koppeln, etwa an das Ressourcenpaket eines anderen Mods: `"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` registriert den Inhalt nur, solange genau diese Datei installiert ist. Der Pfad ist relativ zum Spielordner, immer mit Schrägstrichen, und darf kein `..` enthalten.

### Definitionen vererben

Eine Block- oder Item-Definition kann mit `"inherits"` von einer anderen derselben Art ausgehen, indem sie den Registry-Namen irgendeiner Variante nennt, und dann überschreiben, was abweicht:

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "hardness": 4.0 } } }

Das Kind kopiert jeden Wert aus der Datei des Elternteils und aus der genannten Variante, die Dateireihenfolge spielt nie eine Rolle, Ketten werden vom Elternteil abwärts aufgelöst, und ein Kreis oder ein fehlender Elternteil landet im Log und lässt das Kind so, wie es geschrieben steht. Felder, die das Kind schreibt, ersetzen den geerbten Wert; verschachtelte Varianteneigenschaften überschreiben einzeln, Listen wie `requires` dagegen komplett, schreib also die ganze Liste, die du haben willst. Blöcke erben nur von Blöcken und Items nur von Items.

### Block- und Item-Vorlagen

Ein Elternteil kann eine reine Vorlage sein, die nie ins Spiel kommt, denn die Vererbung liest die Definitionsdateien selbst und nicht das, was registriert wurde. Häng die Vorlage an eine versteckte Option, die fest aus ist, und sie registriert nichts, während ihre Werte vererbbar bleiben:

`config/options.json`

```json
{
  "templates": { "default": false, "hide": true, "description": "Never on, parents only" }
}
```

`data/jacksmod/blocks/ore_template.json`

```json
{
  "type": "ore",
  "material": "rock",
  "soundType": "stone",
  "harvestTool": "pickaxe",
  "harvestToolLevel": 2,
  "creativeTab": "jacksmod:tab",
  "expDrop": { "min": 2, "max": 5 },
  "requires": ["config:templates"],
  "variants": {
    "ore_template": { "hardness": 3.0, "resistance": 5.0 }
  }
}
```

`data/jacksmod/blocks/jacks_ore.json`

```json
{
  "inherits": "jacksmod:ore_template",
  "requires": [],
  "variants": {
    "jacks_ore": { "hardness": 4.0 }
  }
}
```

Die Vorlage registriert sich nie, während `jacks_ore` mit Material, Sound, Werkzeug, Tab, Erfahrungsdrops und Widerstand der Vorlage registriert wird und nur die Härte überschreibt. Das Kind muss sein eigenes `requires` schreiben, hier auf eine leere Liste gesetzt, weil es sonst das des Elternteils erbt und mit ihm verschwinden würde.

### Strukturen an genauen Stellen

Vanilla-Strukturen nagelst du mit `structureAt` in den `terrain`-Einstellungen an genaue Punkte, als `structure=x,z`-Einträge, einer pro Zeile: `"structureAt": ["villages=1000,-500"]`. **x und z sind Blockkoordinaten, keine Chunkkoordinaten**, und die Struktur generiert in dem Chunk, in dem dieser Block liegt. Ein Eintrag pro gewünschtem Exemplar. Ihr Abstand, ihre Trennung, ihr Mindestspawnabstand und die Prüfungen auf flachen Boden treten alle beiseite, die Stelle ist damit Sache des Packs, und zwei Pins näher als einen Chunk beieinander setzen zwei Strukturen in denselben Chunk. Einmal gegründet, setzt sich die Struktur an ihrem Chunk nach den üblichen Regeln in den Boden.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `structureAt` | Liste von `structure=x,z` | keine | Nagelt eine Vanilla-Struktur an eine genaue Stelle, ein Eintrag je gewünschtem Vorkommen. x und z sind Blockkoordinaten, und die Struktur generiert in dem Chunk, der diesen Block enthält; ihr Raster, ihr Abstand, ihre Mindestentfernung vom Spawn und ihre Prüfung auf ebenen Boden treten alle zurück |

Ein `imprint`-Eintrag nagelt genauso fest, mit `"at": [x, z]` in seiner Form, und setzt sie genau einmal an diesen Koordinaten an der Oberfläche, sobald dieser Chunk generiert, statt nach Zufall. Das lässt sich mit `locateAs` kombinieren, eine festgenagelte Struktur ist also auch auffindbar.

### Platzierte Strukturen finden

Ein `imprint`-Eintrag mit `"locateAs": "Crypt"` registriert jede Struktur, die er platziert, unter diesem Namen, und `/rdplserver locate Crypt` zeigt dann auf die nächste davon, mit dem Namen in der Tab-Vervollständigung; `/rdplserver goto Crypt` bringt dich hin. Finden lassen sich nur Strukturen, die schon generiert wurden, denn Pack-Strukturen werden beim Erzeugen der Chunks nach Zufall platziert und nicht auf einem Raster, das das Spiel vorhersagen könnte. Die Namen liegen im Spielstand der Welt, überstehen also Neustarts und funktionieren auf Servern. Ein so registrierter Name kann mit `gotoPlaceLevels` auch eine eigene Berechtigung bekommen, sodass ein Pack getrennt von den Vanilla-Strukturen entscheidet, wer zu seinen eigenen gebracht werden darf.

## Eigenschaften überschreiben

`<namespace>/overrides/<target>/<name>.json`

Der Pfad benennt das Ziel: Alles nach `overrides/` ist Namespace und Name des Blocks, Items oder Tranktyps, der geändert wird.

Alles andere in diesem Kapitel ersetzt eine Datei oder fügt eine hinzu. Ein Override tut keins von beidem: Es ändert die Eigenschaften eines Blocks, Items oder Tranktyps, den es schon gibt, Vanilla oder Mod, ohne eine seiner Dateien anzufassen. Der Pfad benennt das Ziel: `overrides/minecraft/stone.json` ändert `minecraft:stone`, und `overrides/tconstruct/<name>.json` ändert den Block dieses Mods auf dieselbe Weise.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht.

```json
{
  "requires": ["tconstruct"],
  "hardness": 0.1,
  "resistance": 3.0,
  "slipperiness": 0.98,
  "light": 10,
  "lightOpacity": 0,
  "soundType": "glass",
  "harvestTool": "pickaxe",
  "harvestToolLevel": 2,
  "flammability": 5,
  "fireSpread": 5,
  "maxStackSize": 16,
  "maxDamage": 250,
  "containerItem": "minecraft:bucket",
  "food": {
    "heal": 4,
    "saturation": 0.3,
    "alwaysEdible": true,
    "effects": [
      { "potion": "minecraft:speed", "duration": 200, "amplifier": 1, "ambient": false, "showParticles": true }
    ]
  },
  "effects": [
    { "potion": "minecraft:levitation", "duration": 200, "amplifier": 0, "ambient": false, "showParticles": true }
  ]
}
```

Jeder Schlüssel ist optional und eine Datei ändert nur, was sie benennt: Eine Datei unter `overrides/minecraft/stone.json` mit `hardness`, `light` und `soundType` allein lässt Stein fast sofort abbauen, leuchten und wie Glas klingen. Eine Datei trägt Block-, Item- und Trankschlüssel zusammen. Diese gelten, wenn das Ziel ein Block ist:

| Schlüssel | Wert | Was er tut |
| --- | --- | --- |
| `hardness` | Zahl | Abbauzeit, dieselbe Zahl wie in einer Blockdefinition |
| `resistance` | Zahl | Explosionswiderstand |
| `slipperiness` | Zahl | `0.6` ist normaler Boden, `0.98` ist Eis |
| `light` | `0` bis `15` | Abgegebenes Licht |
| `lightOpacity` | `0` bis `15` | Wie viel Licht der Block schluckt |
| `soundType` | einer der Klangtypen | Schritt-, Setz- und Abbaugeräusche |
| `harvestTool` | Werkzeugklasse | Womit er abgebaut wird, in die Werkzeug-Tags geschrieben; `harvestToolLevel`, Standard `0`, setzt die Stufe |
| `flammability` | Ganzzahl | Wie bereitwillig er verbrennt; `fireSpread`, Standard `5`, wie bereitwillig Feuer ihn erreicht |

Und diese, wenn das Ziel ein Item ist:

| Schlüssel | Wert | Was er tut |
| --- | --- | --- |
| `maxStackSize` | `1` bis `64` | Stapelgröße |
| `maxDamage` | Ganzzahl | Haltbarkeit |
| `containerItem` | Item-Name | Bleibt im Handwerksfeld zurück, wie ein Eimer |
| `food` | Objekt | Macht das Item essbar, siehe unten |

Ein Name, der zugleich Block und Item ist, und das ist das Item jedes setzbaren Blocks, nimmt beide Gruppen aus einer Datei:

```json
{
  "hardness": 0.2,
  "food": {
    "heal": 4,
    "saturation": 0.3,
    "alwaysEdible": true,
    "effects": [
      { "potion": "minecraft:speed", "duration": 200, "amplifier": 1 }
    ]
  }
}
```

Unter `overrides/minecraft/oak_planks.json` brechen Bretter damit ungefähr so schnell wie Erde und lassen sich essen. `food` nimmt `heal` (`1`), `saturation` (`0.6`), `alwaysEdible` (`false`; `true` erlaubt Essen bei voller Hungerleiste) und `effects`, dessen Einträge genauso geschrieben werden wie bei einem Tranktyp. Ein Item, das schon Essen ist, nimmt neue `heal`, `saturation` und `alwaysEdible`; `effects` darauf wird nicht unterstützt, und das Log sagt es. Setzt das essbare Item einen Block, ziel zum Essen in den Himmel, denn Zielen auf einen Block setzt ihn: Das ist Vanillas Benutzungsreihenfolge, kein Fehler.

`effects` auf der obersten Ebene der Datei schreibt die Effektliste eines Tranktyps komplett neu:

```json
{
  "effects": [
    { "potion": "minecraft:levitation", "duration": 200, "amplifier": 0 }
  ]
}
```

Mit `overrides/minecraft/swiftness.json` gibt der Trank der Schnelligkeit jetzt Schwebekraft. Jeder Eintrag nimmt `potion` (Pflicht), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) und `showParticles` (`true`), genau wie in `potion_types/`, und die Liste darf nicht leer sein.

Ein Ziel, das einem anderen Mod gehört, sollte diesen Mod in `requires` tragen, dann wird die Datei ohne ihn still übersprungen, statt als fehlendes Ziel gemeldet zu werden:

```json
{
  "requires": ["tconstruct"],
  "hardness": 1.0
}
```

Overrides sind live. Die ursprünglichen Werte werden vor der ersten Änderung festgehalten, also springt nach dem Deaktivieren des Packs und `/rdplserver reload` alles auf den alten Stand zurück, ganz ohne Neustart; dasselbe passiert bei jedem Betreten einer Welt. Eine Datei pro Ziel: Überschreiben zwei Packs dasselbe, ersetzt die Datei des späteren Packs die frühere komplett, und das Log sagt es.

Zwei Grenzen, die man kennen sollte. Ein Block oder Item, dessen eigener Code eine Eigenschaft berechnet, ignoriert das Feld dahinter: Das Override greift, ändert aber nichts; Vanilla macht das nur beim Explosionswiderstand von Treppen, Mods dürfen es überall. Und essbar gemachte Items funktionieren nur bei Items ohne eigenes Rechtsklick-Verhalten: Ein Item, das beim Benutzen schon etwas tut, tut das weiterhin.

Overrides brauchen das Pack auf Client und Server, denn Abbaugeschwindigkeit, Licht und Essen passieren auf dem Bildschirm des Spielers; für rein serverseitige Packs taugen sie nicht. `overrides` in der Config-Kategorie `content` schaltet den Ordner komplett ab.

## Registry-Umbenennungen

`<namespace>/registry_remap/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

Wenn ein Mod einen seiner Blöcke oder Items umbenennt, verlieren Welten, die vor der Umbenennung gespeichert wurden, sie. Leg hier eine Datei ab, die den alten Namen auf den neuen abbildet:

```json
{
  "registry": "minecraft:item",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

Die Registry ist die, zu der der Eintrag gehört, benannt, wie das Spiel sie benennt: `minecraft:item`, `minecraft:block`, `minecraft:entity_type` und so weiter. Umbenennungen verketten sich: Bildest du A auf B ab und später B auf C, geht A direkt auf C.

## Spielerbeute

`<namespace>/player_loot/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

Das Spiel gibt Spielern keine eigene Beutetabelle: Beim Tod fällt nur das Inventar, und es gibt keinen Tabellennamen, den ein Pack überschreiben könnte. RDPL ergänzt eine, die beim Tod eines Spielers ausgewürfelt wird:

```json
{
  "table": "mypack:entities/player",
  "mode": "add",
  "rollOnKeepInventory": false,
  "dropLoose": false
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er tut |
| --- | --- | --- | --- | --- |
| `table` | ja | Tabellenname | | Die Beutetabelle, die beim Tod eines Spielers ausgewürfelt wird |
| `mode` | nein | `add` oder `replace` | `add` | Ob die Items der Tabelle zum Inventar dazukommen oder an dessen Stelle treten |
| `rollOnKeepInventory` | nein | Boolean | `false` | Ob die Tabelle bei einem Tod überhaupt ausgewürfelt wird, der das Inventar behalten hat |
| `dropLoose` | nein | Boolean | `false` | Ob die Items direkt auf den Boden gelegt werden, statt zu den Todesdrops zu kommen |

`add` legt die Items der Tabelle neben die Inventardrops, die Wahl für Kopfgelder auf einen Kill. `replace` verwirft das Inventar, und es fällt nur, was die Tabelle auswürfelt.

Steht `rollOnKeepInventory` auf aus, würfeln Tode unter `keepInventory` (und Tode im Zuschauermodus, die das Inventar immer behalten) nichts. Angeschaltet hält es den Tod auch auf Keep-Inventory-Welten teuer.

Mehrere Dateien stapeln sich, jede wird für sich entschieden. Ist ein zutreffender Eintrag `replace`, wird das Inventar einmal geleert, bevor gewürfelt wird, ein `add`-Eintrag daneben landet trotzdem.

Die Tabelle ist eine gewöhnliche Beutetabelle, über ihren Namen gesucht: Sie kann im Pack unter `loot_tables/entities/player.json` liegen, eine beliebige Vanilla- oder Mod-Tabelle sein und wird von `loot_injections` erreicht. Beutekontext: Der sterbende Spieler ist die erbeutete Entity, der Töter (falls vorhanden) der tötende Spieler, die Schadensquelle ist gesetzt, `killed_by_player`, `entity_properties`, `random_chance_with_looting` und der Rest verhalten sich also normal.

Eine Beutefunktion bringt RDPL selbst mit, nutzbar in jeder Tabelle mit einem geplünderten Wesen: `rdpl:killed_name` benennt das Item nach dem Opfer. `format` formt den Anzeigenamen (`%s` ist das Opfer, ohne Angabe nur der Name), `tag` schreibt stattdessen den bloßen Namen in einen NBT-Schlüssel für Items, die ihn selbst auslesen.

```json
{ "item": "mypack:human_skull", "weight": 1,
  "functions": [ { "function": "rdpl:killed_name", "format": "%s's Skull" } ] }
```

**Grab-Mods.** Die gewürfelten Items kommen zu den normalen Todesdrops, bevor ein Grab-Mod sie liest, und landen darum mit allem anderen im Grab (`replace` legt den Tabelleninhalt statt des Inventars ins Grab). Keine Einrichtung nötig.

`dropLoose` umgeht die Dropliste vollständig: Die Items werden direkt in die Welt gesetzt, Grab-Mods sehen sie nie; das Inventar wandert ins Grab, die Items der Tabelle liegen für den Töter auf dem Boden. Die Einstellung für Beute, die dem Töter gehört statt dem Grab des Opfers. Ohne Grab-Mod ändert sie wenig. Vorbehalt: Die Items existieren, bevor irgendetwas nachgelagert die Drops noch abbrechen könnte, ein Eintrag, der einen abgebrochenen Tod nicht überleben darf, lässt sie besser aus.

Setz `playerLoot` in der Config-Kategorie `data` auf `false`, um den Ordner ganz abzuschalten.

---

# Neuen Inhalt beschreiben

## Wie Definitionen funktionieren

Neben den Ordnern, die Dateien überschreiben, gibt es Ordner, die neue Dinge beschreiben. Eine Definitionsdatei fasst unter `variants` ein oder mehrere Dinge einer Art zusammen, und jeder Schlüssel in `variants` ist ein Registry-Name: `data/mypack/blocks/ore.json` mit einer Variante namens `ruby_ore` registriert `mypack:ruby_ore`. Der Name der Datei selbst ist eine Gruppierung und nicht mehr; eine Datei kann einen Block enthalten oder ein Dutzend, die ihre Einstellungen teilen.

Registriert wird mit der niedrigsten Priorität, die der Loader anbietet: Registriert ein echter Mod denselben Namen, gewinnt der Mod, und deine Datei wird ignoriert. Nichts hier kann einen Mod ersetzen.

**Wo die Grenze liegt.** Alles, was eine eigene Block-Entity, einen Bildschirm, ein Inventar oder eigene Logik pro Tick braucht, braucht einen echten Mod, mit einer Ausnahme: dem Typ [container](#behälter), der ein Inventar und einen Bildschirm mitbringt. Alles darunter ist Freiwild.

### Dein Namespace ist dein Mod

Der Namespace, den du wählst, ist in jeder praktischen Hinsicht eine Mod-ID. Nichts davon wird als Mod geladen, und in der Modliste taucht es nie auf, aber alles, was eine Mod-ID liest, liest deine:

- Registry-Namen sind `mypack:ruby_ore`, genau wie die eines Mods, und sie werden in jede gespeicherte Welt geschrieben, die sie enthält.
- Die Whitelists für Erz, Biome und Rezepte in der Config gleichen damit ab, `oreWhitelist = mypack` behält also dein Erz und blockt das aller anderen.
- `/rdpl which`, `/rdplserver oregen` und die Berichte gruppieren danach.
- JEI, Tags und die Abfragen anderer Mods sehen ihn genauso.

Wähl also am Anfang einen Namen und ändere ihn nie wieder. Ein umbenannter Namespace macht alles zu Waisen, was schon in einer Welt liegt, genau wie ein Mod, der seine ID ändert, dafür ist `registry_remap` da.

Das gilt in beide Richtungen: `requires` nimmt einen Pack-Namespace genauso bereitwillig wie eine installierte Mod-ID, ein Pack kann also von einem anderen abhängen und übersprungen werden, wenn das nicht installiert ist.

Ein Mod oder ein Pack, das in `requires` genannt wird und nicht installiert ist, lässt die Definition aus: Eine Zeile geht nach `logs/rdpl.log` und nennt, was gefehlt hat, und das Spiel läuft weiter. Wenn ein Block, den du erwartet hast, nicht im Kreativtab liegt, ist diese Logzeile die erste Stelle zum Nachsehen.

`requires` nimmt nur bloße IDs. Es gibt keine Syntax für Versionsbereiche, es kann also sagen, dass ein Mod da sein muss, aber nicht, welche Version.

Die eigene ID des Mods, `resourcedatapackloader`, ist reserviert. Inhalt darunter zu definieren wird ignoriert und protokolliert, weil es Besitz an Dingen anmelden würde, die dieser Mod selbst registriert. Die Assets dieses Mods zu überschreiben ist weiterhin in Ordnung, nur Inhalt dort zu registrieren nicht.

Jede Tabelle unten folgt den Konventionen aus [Die Tabellen lesen](#die-tabellen-lesen).

Die meisten Definitionen nehmen außerdem `requires` an, eine Liste von Mod-IDs oder Pack-Namespaces, die vorhanden sein müssen, sonst wird die Datei übersprungen.

## Blöcke

`<namespace>/blocks/*.json`

Jeder Schlüssel in `variants` ist ein Block, registriert unter dem Namespace des Packs: Eine Datei mit `ruby_ore` und `deep_ruby_ore` registriert `mypack:ruby_ore` und `mypack:deep_ruby_ore`, die sich jede Einstellung teilen, die die Datei außerhalb von `variants` schreibt. Der Name der Datei selbst ist nur eine Gruppierung.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht. Ein Schlüssel, der für einen Typ vermerkt ist, wird nur von diesem Typ gelesen.

```json
{
  "inherits": "mypack:ore_template",
  "type": "ore",
  "material": "rock",
  "soundType": "stone",
  "mapColor": "red",
  "harvestTool": "pickaxe",
  "harvestToolLevel": 2,
  "silkHarvest": true,
  "opensWith": "mypack:ruby_key",
  "openSound": "block.chest.open",
  "expDrop": { "min": 3, "max": 7 },
  "creativeTab": "mypack:tab",
  "renderLayer": "solid",
  "opaque": true,
  "fullCube": true,
  "slipperiness": 0.6,
  "flammability": 0,
  "fireSpread": 0,
  "explosionResistanceDivisor": 1.0,
  "modelBlock": "minecraft:stone",
  "itemModel": "state",
  "tint": "biome",
  "plantTypes": ["plains", "crop"],
  "behavesAs": ["till", "path"],
  "bounds": [0.0, 0.0, 0.0, 1.0, 1.0, 1.0],
  "requires": ["mypack"],
  "particle": "colored",
  "particleColor": "C0304A",
  "smoke": true,
  "leafSapling": "mypack:ruby_sapling",
  "leafSaplingChance": 5,
  "seed": "mypack:ruby_seed",
  "produce": "mypack:ruby_fruit",
  "maxAge": 7,
  "growth": { "stages": 8, "growth": 10 },
  "sapling": { "log": "mypack:ruby_log", "leaves": "mypack:ruby_leaves" },
  "portal": { "dimension": "mypack:ruby_world" },
  "variants": {
    "ruby_ore": {
      "hardness": 3.0,
      "resistance": 5.0,
      "light": 0,
      "harvestLevel": 2,
      "rarity": "rare",
      "maxSize": 64,
      "tags": ["forge:ores/ruby", "forge:ores"],
      "drops": [
        { "block": "mypack:ruby", "amount": { "min": 1, "max": 2 }, "bonusChance": [1, 2] }
      ]
    },
    "deep_ruby_ore": {
      "hardness": 4.5,
      "resistance": 8.0,
      "light": 3
    }
  }
}
```

### Typen

| Typ | Was du bekommst |
| --- | --- |
| `basic` | Ein einfacher Block. Wird genommen, wenn `type` fehlt |
| `ore` | Droppt etwas anderes als sich selbst, mit Glück und Behutsamkeit |
| `falling` | Fällt wie Sand oder Kies |
| `slab` | Unten, oben und doppelt, und zwei davon verschmelzen in der Hand |
| `stairs` | Ecken und Steigungen werden dir abgenommen |
| `fence` | Verbindet sich mit seinen Nachbarn und mit Zäunen aus anderen Mods |
| `pane` | Verbindet sich wie Glasscheiben |
| `wall` | Verbindet sich wie Bruchsteinmauern, mit der Pfostenform |
| `door` | Zwei Blöcke hoch, öffnet sich per Hand und hört auf Redstone |
| `trapdoor` | Eine Klappe oben oder unten an einem Block, per Hand oder per Redstone zu öffnen |
| `fence_gate` | Ein Tor in einer Zaunreihe, per Hand oder per Redstone zu öffnen, und abgesenkt, wo es auf eine Mauer trifft |
| `banner` | Ein Banner auf einem Pfosten oder an einer Wand, sechzehn stehende Drehungen, mit deinem eigenen Muster |
| `ladder` | Kletterbar, an eine Wand gesetzt |
| `torch` | Wand- und Bodenplatzierung, mit Partikel |
| `log` | Dreht sich zu der Fläche, gegen die du ihn setzt |
| `leaves` | Verwelkt, lässt sich scheren, wird eingefärbt und droppt einen Setzling |
| `sapling` | Wächst zu einem Baum oder zu einer deiner Strukturen |
| `crop` | Wächst durch Stufen, droppt Saatgut und ein Ernte-Item |
| `flower` | Eine einblockige Pflanze auf Erde |
| `cane` | Wächst als Säule nach oben, wie Zuckerrohr oder Kaktus |
| `vine` | Klettert und hängt an den Seiten von Blöcken |
| `portal` | Schickt alles, was hineinläuft, in eine andere Dimension |
| `container` | Enthält ein Inventar, das ein Spieler öffnen kann, in beliebiger Größe, und kann sich beim ersten Öffnen selbst aus einer Beutetabelle füllen. Wird als gewöhnlicher Block oder als Truhe gezeichnet, je nachdem, was das Pack verlangt |

### Dateischlüssel

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `variants` | ja | Objekt aus Variantenname zu Variante | | Ein Block pro Eintrag. Der Schlüssel ist sein Registry-Name und benennt seinen Blockstate, seine Modelle, seine Texturen und seinen Sprachschlüssel |
| `type` | nein | einer der Typen oben | `basic` | Welche Form der Block annimmt |
| `material` | nein | eines der [Blockmaterialien](#wertelisten) | `rock` | Abbauverhalten, Kolben, Feuer und Flüssigkeiten |
| `soundType` | nein | einer der [Sound-Typen](#wertelisten) | vom Material | Schritte, Abbauen und Setzen |
| `mapColor` | nein | eine der [Kartenfarben](#wertelisten) | vom Material | Wie er auf einer Karte aussieht |
| `harvestTool` | nein | `pickaxe`, `axe`, `shovel`, `hoe` | `pickaxe` | Welches Werkzeug ihn abbaut, für dich in die `mineable`-Tags des Spiels geschrieben |
| `harvestToolLevel` | nein | 0 bis 4 | `0` | 0 Holz, 1 Stein, 2 Eisen, 3 Diamant, 4 Netherit, für dich in die `needs_*_tool`-Tags geschrieben |
| `silkHarvest` | nein | boolean | `true` | Ob Behutsamkeit den Block selbst zurückgibt |
| `opensWith` | nein | Item-Id | keine | Macht den Block zur Schatzkiste: Abbauen liefert den Block selbst, ein Rechtsklick mit dem genannten Item verbraucht eines, spielt den Abbau-Sound, schüttet die `drops`-Liste der Variante aus und entfernt den Block. Jeder andere Klick zeigt die Aktionsleisten-Zeile `block.<pack>.<block>.locked` aus den Sprachdateien |
| `openSound` | nein | Sound-Name | der Abbau-Sound | Was eine Schatzkiste beim Öffnen statt ihres Abbau-Sounds spielt |
| `expDrop` | nein | Objekt mit `min` und `max` | keines | Erfahrung beim Abbauen ohne Behutsamkeit |
| `creativeTab` | nein | Tab-Name | keiner | Der Tab, in dem er auftaucht |
| `renderLayer` | nein | `solid`, `cutout`, `cutout_mipped`, `translucent` | passend zum Typ | Wie er gezeichnet wird |
| `opaque` | nein | boolean | `true` | Ob er Sicht und Licht vollständig blockiert |
| `fullCube` | nein | boolean | wie `opaque` | Ob er seinen ganzen Raum ausfüllt |
| `slipperiness` | nein | float | `0.6` | Eis ist `0.98` |
| `flammability` | nein | int | `0` | Wie bereitwillig Feuer ihn verzehrt |
| `fireSpread` | nein | int | `0` | Wie bereitwillig Feuer von ihm überspringt |
| `explosionResistanceDivisor` | nein | float | `1.0` | Teilt den `resistance`-Wert jeder Variante gegenüber Explosionen |
| `modelBlock` | nein | Blockname | `minecraft:stone` | Block, dessen Modell geliehen wird, wenn deiner weder Textur noch eigenes Modell mitbringt |
| `itemModel` | nein | `state`, `item` | `state` | `state` zeichnet das Item als den gesetzten Block, `item` sucht deine eigene `models/item/<name>.json` |
| `tint` | nein | `biome`, `none` oder eine Hex-Farbe | keine | Braucht einen `tintindex` im Modell, um zu wirken |
| `plantTypes` | nein | Liste von [Pflanzentypen](#wertelisten) | keine | Was darauf gepflanzt werden kann |
| `behavesAs` | nein | Liste aus `till`, `path` | keine | Vanilla-Verhalten, das er übernimmt |
| `bounds` | nein | Liste aus sechs Zahlen, 0 bis 1 | ganzer Block | Die Kollisionsbox, als `[x1, y1, z1, x2, y2, z2]` |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |
| `particle` | nur `torch` | `none`, `flame`, `colored` | `flame` | Der Partikel über einer Fackel |
| `particleColor` | nur `torch` | Hex-Farbe | `FFFFFF` | Wird genutzt, wenn `particle` auf `colored` steht |
| `smoke` | nur `torch` | boolean | `true` | Ob sie raucht |
| `leafSapling` | nur `leaves` | Blockname | keiner | Der Setzling, den sie droppen |
| `leafSaplingChance` | nur `leaves` | int | `5` | Eines von N Blättern droppt einen |
| `seed` | nur `crop` | Itemname | keiner | Das Item, das sie pflanzt |
| `produce` | nur `crop` | Itemname | keiner | Was die Ernte bringt |
| `maxAge` | nur `crop` | int | `7` | Wie viele Wachstumsstufen |
| `growth` | nur Pflanzen | Objekt | keines | Siehe [Wachstum](#wachstum) |
| `sapling` | nur `sapling` | Objekt | keines | Siehe [Setzlinge](#setzlinge) |
| `portal` | nur `portal` | Objekt | keines | Siehe [Portale und Tore](#portale-und-tore) |
| `container` | nur `container` | Objekt | keines | Siehe [Behälter](#behälter) |

### Variantenschlüssel

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `hardness` | nein | float | `1.0` | Wie lange das Abbauen dauert. Obsidian ist `50`, `-1` ist unzerstörbar |
| `resistance` | nein | float | `5.0` | Explosionswiderstand |
| `light` | nein | 0 bis 15 | `0` | Abgegebenes Licht |
| `harvestLevel` | nein | 0 bis 4 | der Wert der Datei | Überschreibt die Werkzeugstufe für diese Variante |
| `rarity` | nein | `common`, `uncommon`, `rare`, `epic` | `common` | Farbe des Namens im Tooltip |
| `maxSize` | nein | 1 bis 64 | `64` | Stapelgröße |
| `tags` | nein | Liste von Tag-IDs | keine | Block- und Item-Tags, in die diese Variante geschrieben wird, etwa `forge:ores/ruby` auf 1.20.1 oder `c:ores/ruby` auf 1.21.1. Die Tag-Dateien werden für dich erzeugt |
| `drops` | nein | Liste von Drops | droppt sich selbst | Was das Abbauen bringt |

**Namen sind endgültig.** Der Schlüssel einer Variante wird in jede gespeicherte Welt geschrieben, die sie enthält. Wird er später umbenannt, werden gesetzte Blöcke zu Luft, es sei denn, eine [Registry-Umbenennung](#registry-umbenennungen) bildet den alten Namen auf den neuen ab. Eine Datei darf so viele Varianten enthalten, wie sie will; jede ist ein eigener Block, und ein `meta`-Schlüssel aus einem 1.12.2-Pack wird mit einer Notiz im Log ignoriert.

### Drops

```json
{
  "drops": [
    { "block": "mypack:ruby", "amount": { "min": 1, "max": 3 }, "chance": 100, "guaranteed": true, "bonusChance": [1, 2, 3] },
    { "block": "minecraft:coal", "amount": 1, "chance": 25 },
    { "block": "minecraft:diamond", "weight": 1 },
    { "block": "minecraft:emerald", "weight": 4 },
    { "entity": "minecraft:silverfish", "amount": { "min": 1, "max": 2 }, "chance": 15 }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `block` | eines von beiden | Block- oder Itemname | | Was gedroppt wird |
| `entity` | eines von beiden | Entity-Name | | Eine Entity, die beim Brechen des Blocks frei wird, statt eines Items |
| `amount` | nein | int oder Bereich | `1` | Wie viele |
| `chance` | nein | 0 bis 100 | `100`, bzw. `0` wenn `guaranteed` aus ist | Wie oft der Drop überhaupt kommt |
| `weight` | nein | int | `0` | Über null tritt der Eintrag einem Topf bei, aus dem genau ein Drop kommt. Siehe unten |
| `bonusChance` | nein | Liste von Ints | keine | Zusätzliche Drops pro Glücksstufe, ein Eintrag pro Stufe |
| `guaranteed` | nein | boolean | `true` | Altes Kürzel für `chance`. An heißt `100`, aus heißt `0` |

Jeder Eintrag ohne `weight` wird für sich entschieden, ein Block mit dreien kann also alle drei droppen oder keinen. Gibst du Einträgen ein `weight`, hören sie auf, unabhängig zu sein: Sie bilden einen Topf, aus dem bei jedem Brechen genau einer gezogen wird, mit den Gewichten als Verhältnis. Oben teilen sich Diamant und Smaragd einen Topf im Verhältnis eins zu vier, es kommt also immer einer von beiden heraus und in vier von fünf Fällen der Smaragd, während Rubin und Kohle getrennt entschieden werden und der Silberfisch wieder für sich steht. Items und Entities haben getrennte Töpfe, ein gewichtetes Item und eine gewichtete Entity konkurrieren also nicht.

Ein Eintrag mit `entity` lässt dort, wo der Block stand, eine frei, in eine zufällige Richtung gedreht, und ein Mob bekommt seine übliche Spawn-Behandlung für die Schwierigkeit vor Ort, kommt also mit der Ausrüstung und den Effekten an, die er sonst auch hätte. `amount` bestimmt wie viele, `chance` wie oft, `weight` steckt ihn in den Entity-Topf. Es passiert, während der Block bricht, ganz gleich wodurch, eine Explosion oder ein Kolben setzt sie also genauso frei wie eine Spitzhacke. `bonusChance` und Glück bedeuten einer Entity nichts und werden ignoriert.

Ein Drop, der sowohl `block` als auch `entity` nennt, nimmt die Entity und schreibt es ins Log.

Die Drops werden in eine erzeugte Beutetabelle geschrieben, `loot_tables/blocks/<name>.json` unter dem Namespace des Packs, es sei denn, das Pack liefert selbst eine unter diesem Pfad; dann ist die Datei des Packs das, was der Block nutzt, und `drops` wird nicht gelesen.

### Wachstum

Für `crop`, `flower`, `cane` und `vine`.

```json
{
  "growth": {
    "stages": 8,
    "growth": 10,
    "spread": 1,
    "maxHeight": 3,
    "soil": ["minecraft:sand", "minecraft:red_sand"],
    "drop": "mypack:reed",
    "dropCount": 1,
    "needsSky": false,
    "needsWater": true,
    "waterRange": 2,
    "damage": false,
    "damageAmount": 1.0,
    "breaksNeighbors": false
  }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `stages` | nein | int | `16` | Wachstumsstufen, bis es fertig ist |
| `growth` | nein | int | | Chance von eins zu N pro Random-Tick, eine Stufe weiterzukommen |
| `spread` | nein | int | `0` | Wie weit es sich auf Nachbarblöcke ausbreitet |
| `maxHeight` | nein | int | `3` | Nur `cane`. Wie hoch die Säule wächst |
| `soil` | nein | Liste von Blocknamen | das Übliche des Typs | Worauf es steht |
| `drop` | nein | Itemname | keiner | Was es beim Abbauen droppt |
| `dropCount` | nein | int | `1` | Wie viele |
| `needsSky` | nein | boolean | `false` | Wächst nur, wo der Himmel zu sehen ist |
| `needsWater` | nein | boolean | `false` | Wächst nur in Wassernähe |
| `waterRange` | nein | int | `1` | Wie weit dieses Wasser entfernt sein darf |
| `damage` | nein | boolean | `false` | Verletzt, was es berührt |
| `damageAmount` | nein | float, halbe Herzen | `1.0` | Wie sehr es verletzt |
| `breaksNeighbors` | nein | boolean | `false` | Zerstört Blöcke, die daneben gesetzt werden, wie ein Kaktus |

### Setzlinge

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht.

```json
{
  "sapling": {
    "soil": ["minecraft:grass_block", "minecraft:dirt"],
    "stages": 3,
    "chance": 5,
    "light": 9,
    "log": "mypack:ruby_log",
    "leaves": "mypack:ruby_leaves",
    "height": 5,
    "vines": false,
    "structure": "mypack:ruby_tree"
  }
}
```

Ein `structure` ersetzt den generierten Baum durch eine deiner Vorlagen, und das ist der Weg, etwas zu bauen, was ein Generator nicht hinbekommt; sonst muss nichts im Block stehen. Nennst du stattdessen mehrere unter `structures`, wählt der Setzling bei jedem Wachsen eine davon aus, damit ein Wald nicht immer derselbe Baum ist:

```json
{
  "sapling": { "structure": "mypack:ruby_tree" }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `soil` | nein | Liste von Blocknamen | keine | Worauf er wächst |
| `stages` | nein | int | `2` | Wachstumsstufen, bis er ein Baum wird |
| `chance` | nein | int | `7` | Eins zu N pro Random-Tick |
| `light` | nein | 0 bis 15 | `9` | Nötiges Lichtlevel |
| `log` | nein | Blockname | `minecraft:oak_log` | Stammblock |
| `leaves` | nein | Blockname | `minecraft:oak_leaves` | Blätterblock |
| `height` | nein | int | `4` | Stammhöhe |
| `vines` | nein | boolean | `false` | Ranken von den Blättern hängen lassen |
| `structure` | nein | `namespace:name` | keine | Wächst zu dieser Vorlage statt zu einem generierten Baum |
| `structures` | nein | Liste | keine | Mehrere Vorlagen, aus denen bei jedem Wachsen eine gewählt wird. Jeder Eintrag ist `{ "structure": "namespace:name", "weight": 3 }` oder ein bloßer Name für gleiche Chancen. Überschreibt `structure` |

## Modelle, Blockstates und Texturen

Einen Block oder ein Item zu definieren registriert es. Wie es *aussieht*, ist ein Satz Asset-Dateien in denselben Ordnern und demselben Format, die das Spiel nutzt, unter deinem eigenen Namespace, und auf dieser Version werden die meisten davon für dich geschrieben.

```
assets/mypack/textures/block/ruby_ore.png
assets/mypack/textures/item/ruby.png
assets/mypack/lang/en_us.json
```

**Liefere eine Textur, und der Rest wird erzeugt.** Für jeden Block, dessen Blockstate das Pack nicht mitbringt, schreibt der Mod den Blockstate und die Modelle, die der Typ braucht, mit Verweis auf `textures/block/<name>.png`, wobei `<name>` der Schlüssel der Variante ist, und für jedes Item ohne `models/item/<name>.json` ein Item-Modell mit Verweis auf `textures/item/<name>.png`. Ein Block, der weder eine Textur noch ein eigenes Modell hat, leiht sich das Aussehen von `modelBlock`, standardmäßig Stein, sodass nie etwas als lila-schwarzes Quadrat gezeichnet wird. Liefere einen eigenen `blockstates/<name>.json`, und der Mod erzeugt für diesen Block nichts und nimmt deinen; dasselbe gilt für `models/item/<name>.json`.

| Typ | Texturdateien, nach denen er sucht | Erzeugt aus |
| --- | --- | --- |
| `basic`, `ore`, `falling`, `flower`, `sapling`, `cane`, `leaves`, `container` ohne Truhe | `<name>` | `cube_all`, `cross` oder `leaves` |
| `log` | `<name>` für die Seite, `<name>_top` für die Enden | `cube_column` |
| `slab` | `<name>` | `slab`, `slab_top` und ein `cube_all` für die Doppelstufe |
| `stairs` | `<name>` | `stairs`, `inner_stairs`, `outer_stairs`, alle vierzig Zustände ausgeschrieben |
| `fence` | `<name>` | `fence_post` und `fence_side` als Multipart, und `fence_inventory` für die Hand |
| `wall` | `<name>` | die Mauer-Vorlagen für Pfosten und Seite als Multipart, und `wall_inventory` für die Hand |
| `pane` | `<name>` für die Scheibe, `<name>_top` für die Kante | die fünf Glasscheiben-Vorlagen als Multipart |
| `door` | `<name>_top` und `<name>_bottom`, oder `<name>` für beide | die acht Türmodelle und ihre zweiunddreißig Zustände |
| `trapdoor` | `<name>` | die drei drehbaren Falltürmodelle |
| `fence_gate` | `<name>` | die vier Tormodelle, geschlossen und offen, in einer Mauer und außerhalb |
| `ladder`, `vine`, `torch` | `<name>` | die eigene Vorlage des Spiels für jeden |
| `crop` | `<name>_stage0` bis `<name>_stage<maxAge>`, oder `<name>` für alle | ein `crop`-Modell pro Stufe, `age=0` bis `7` darauf abgebildet |
| `portal` | `<name>`, oder die des Netherportals | drei Portalplatten, eine je Achse |
| `banner` | sein eigenes Blatt, siehe [Banner](#banner) | das Bannermodell des Spiels |
| `container` mit `chestModel` | das in `chestModel` genannte Truhenblatt | das `pack_chest`-Modell des Mods |

Jede Textur wird unter `textures/block/` gesucht, und der Name ist der Schlüssel der Variante, ein als `ruby_ore` registrierter Block will also `textures/block/ruby_ore.png`, und sonst muss nichts geschrieben werden. Ein Block, dessen Item flach gezeichnet wird, eine Tür, eine Leiter, eine Fackel, ein Setzling, eine Blume, ein Rohr, eine Ranke oder eine Scheibe, nimmt für die Hand `textures/item/<name>.png`, wenn es sie gibt, und seine Blocktextur, wenn nicht.

**Items** nehmen `textures/item/<name>.png` und ein erzeugtes `item/generated`-Modell, oder `item/handheld` für ein Werkzeug. Liefere `models/item/<name>.json`, um es anders zu zeichnen.

**Flüssigkeiten** brauchen gar kein Modell; eines wird aus den Texturen `still` und `flow` erzeugt.

**Ein Block mit mehreren Varianten ist mehrere Blöcke.** Jeder Schlüssel unter `variants` wird für sich registriert, jeder hat also seinen eigenen Blockstate, seine eigenen Modelle und seine eigenen Texturen, nach dem Schlüssel benannt. Es gibt keinen geteilten Blockstate mit einer `blocks`-Eigenschaft, und nichts in einem Blockstate muss sagen, welche Variante es ist: `blockstates/ruby_ore.json` gehört dem Rubinerz, und `blockstates/deep_ruby_ore.json` dem tiefen.

### Selbst schreiben

Alles Erzeugte lässt sich ersetzen. Ein Blockstate, den das Pack mitbringt, wird so genommen, wie er ist, im eigenen Format des Spiels: die Vanilla-`variants`, geschlüsselt nach den Eigenschaften des Blocks, oder `multipart`. Die Eigenschaften sind die des Spiels für jeden Typ: `axis` an einem Stamm, `type` an einer Stufe, `facing`, `half` und `shape` an Treppen, `facing`, `half`, `hinge` und `open` an einer Tür, `facing`, `half` und `open` an einer Falltür, `facing`, `in_wall` und `open` an einem Tor, `age` an einer Feldfrucht und einem Rohr, `stage` an einem Setzling, `north`, `east`, `south`, `west` an einem Zaun oder einer Scheibe mit `up` dazu an einer Mauer und einer Ranke, `rotation` an einem stehenden Banner und `facing` an einem Wandbanner, `axis` an einem Portal. Ein `basic`-, `ore`-, `falling`-, `leaves`-, `flower`- oder `container`-Block hat einen Zustand, geschlüsselt `""`.

Zeig mit den Modellen auf die Eltern, die Texturen annehmen, nicht auf die fertigen Vanilla-Modelle: `cube_all` nimmt ein `all`; `cube_column` ein `end` und eine `side`; `cross` ein `cross`; die Treppeneltern `bottom`, `top` und `side`; `fence_post` und `fence_side` eine `texture`; `template_wall_post` und `template_wall_side` eine `wall`; die Glasscheiben-Vorlagen eine `pane` und eine `edge`; die Türeltern ein `top` und ein `bottom`; `template_orientable_trapdoor_*` und `template_fence_gate*` eine `texture`; `template_torch` eine `torch`; `crop` eine `crop`; `vine` und `ladder` ihren eigenen Namen. Ein Modell, das ein fertiges Vanilla-Modell wie `oak_door_bottom_left` nennt, erbt Vanillas Texturen mit, was auch immer der Blockstate sagt.

### Banner

Ein Banner ist der eine Typ, bei dem die Form des Blocks und die Form des Modells auseinandergehen, und darum lohnt es sich, das ganz auszubreiten.

**In den Augen des Spiels sind es zwei Blöcke, in deinen einer.** Eine Definition gibt dir das stehende Banner unter deinem eigenen Namen und das hängende daneben, so wie das Spiel jedes Banner paart; das Item setzt, was passt, stehend, wenn du auf die Oberseite eines Blocks klickst, und an die Wand, wenn du auf eine Seite klickst.

**Das Modell ist fast zwei Blöcke hoch.** Ein Banner belegt für Platzierung und Kollision einen Block, wird aber weit darüber hinaus gezeichnet. Der erzeugte Blockstate nutzt das Bannermodell des Spiels, gezeichnet vom Banner-Renderer aus dem Blatt unter `textures/entity/banner/<name>.png`, gib ihm also ein Blatt, das aufgebaut ist wie das des Vanilla-Banners. Sein Item will ein eigenes Modell, mit einem `display`-Block, der die Skalierung so weit herunternimmt, dass es in sein Feld passt.

**Farben und Muster gibt es daran nicht.** Das Muster ist die Textur, so wie das Aussehen einer Tür ihre Textur ist, und eine Definition ist ein Banner. Es zu färben und Muster darauf zu stapeln liegt außerhalb dessen, was ein Pack erreichen kann.

**Es nimmt das `material`, das du ihm gibst.** Ein steinernes Banner wird mit der Spitzhacke abgebaut, wie es der Stein verlangt, für den es sich ausgibt.

### Texturen als Pixelkarte

Eine Textur darf eine JSON-Datei statt einer PNG sein. Leg sie dorthin, wo die PNG gelandet wäre, und häng `.json` an den ganzen Namen: `textures/block/panel.png.json` beantwortet dann jede Anfrage nach `textures/block/panel.png`. Sonst ändert sich nichts: Modelle zeigen weiter auf `mypack:block/panel`, Atlas, Mipmaps und eine Animations-`.mcmeta` funktionieren wie gehabt, denn was das Spiel bekommt, ist nach wie vor eine PNG. Das Beispielpack liefert nicht eine einzige PNG; jede Textur darin ist eine Karte.

```json
{
  "extends": "mypack:textures/block/panel_template",
  "size": "16x16",
  "palette": { "s": "#EDE9E2", "d": "#C6C1B5", "e": "#9E988C", "p": "#F6F4EF" },
  "tint": { "from": "#626669", "to": "#DBDFE2" },
  "notes": {
    "s": "the flat surface",
    "d": "shadow inside the border",
    "e": "the outer edge",
    "p": "the raised panel"
  },
  "rows": [
    "eeeeeeeeeeeeeeee",
    "edddddddddddddde",
    "edssssssssssssde",
    "edspppppppppssde",
    "edspppppppppssde",
    "edspppppppppssde",
    "edspppppppppssde",
    "edssssssssssssde",
    "edssssssssssssde",
    "edspppppppppssde",
    "edspppppppppssde",
    "edspppppppppssde",
    "edspppppppppssde",
    "edssssssssssssde",
    "edddddddddddddde",
    "eeeeeeeeeeeeeeee"
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `size` | ja, oder geerbt | `breitexhöhe` | | Wie viele Pixel quer und hinunter |
| `rows` | ja, oder geerbt | Liste von Text | | Ein String je Pixelzeile, ein Zeichen je Pixel, von oben nach unten |
| `palette` | ja, oder geerbt | Objekt | | Ein Zeichen zu einer Farbe, `#RRGGBB` oder `#AARRGGBB` |
| `extends` | nein | eine andere Pixelkarte | | Die Karte, von der diese ausgeht |
| `tint` | nein | Objekt mit `from` und `to` | | Färbt alles Geerbte entlang einer Rampe zwischen zwei Farben um |
| `notes` | nein | Objekt | | Ein Zeichen zu einer Zeile, die sagt, wofür es da ist; wird vererbt und nie gezeichnet |

**Es gibt keinen Namen anzugeben.** Der Pfad der Datei ist ihr Name, genau wie bei einer PNG: Eine Karte unter `assets/mypack/textures/block/panel.png.json` heißt im Modell `mypack:block/panel`, eine unter `assets/mypack/textures/item/gem.png.json` heißt im Item-Modell `mypack:item/gem`. Nichts zeigt eigens auf eine Pixelkarte; ein Block oder ein Item nennt seine Textur wie eh und je und erfährt nie, welche von beiden es bekommen hat. Damit bleiben Block- und Item-Ordner auch getrennt wie bei PNGs: `textures/block/gem.png.json` und `textures/item/gem.png.json` sind zwei verschiedene Texturen und werden als zwei verschiedene Dateien zwischengespeichert.

**Jede Größe, die du willst**, bis 4096 je Seite, und die beiden Seiten müssen nicht gleich sein. `16x16` ist eine gewöhnliche Blockfläche, `16x32` der hohe Streifen, wie ihn eine Türhälfte oder eine Animation braucht. Die Größe wird geprüft und nicht erraten: Gib eine Zeile je Pixelzeile und ein Zeichen je Pixel quer, sonst wird die Karte abgelehnt und das Log nennt die Zeile und was es vorgefunden hat. Ein Zeichen ohne Farbe in der Palette bleibt frei, `.` oder ein Leerzeichen ist also ein Loch.

**Vorlagen sind der eigentliche Sinn.** `extends` nennt eine andere Pixelkarte, als `namespace:pfad` oder als bloßer Pfad im selben Pack, und die erbende Datei übernimmt deren `size`, `rows` und `palette`. Was sie selbst nennt, gewinnt, und sie muss nicht alles nennen, eine ganze Variante kann also aus einer Handvoll Farben bestehen:

```json
{
  "extends": "mypack:textures/block/panel.png",
  "palette": { "s": "#AA7EB1", "d": "#8B6292", "e": "#6B4A72", "p": "#C5A1CB" }
}
```

Das ist eine vollständige zweite Textur: dieselbe Form in Purpur, und wird die Form in der Vorlage je neu gezeichnet, ziehen alle Varianten mit. Eine Variante darf stattdessen eigene `rows` mitbringen und die Palette der Vorlage behalten, andersherum also: dieselben Farben in einem anderen Muster. Die Vererbung geht bis zu acht Stufen tief, eine Schleife wird erkannt und gemeldet, und eine Karte, die eine Vorlage nennt, die es nicht gibt, wird gemeldet statt leer gezeichnet.

**Welche von zwei Texturen die Vorlage ist**, entscheidet sich daran, welche mehr Unterschiede festhält, nicht daran, welche zuerst gezeichnet wurde. Eine Variante gibt jedem Zeichen eine Farbe, also wird jedes Pixel, das die Vorlage gleich benennt, in der Variante auch gleich. Ein Erz auf Stein kann die `rows` des Steins deshalb nicht erben: Der Stein nennt die Sprenkelstellen schlicht Stein, und nichts, was eine Variante schreiben kann, spaltet ein Zeichen in zwei. Andersherum geht es auf. Nimm das Erz als Vorlage, dann haben die Steintöne und die Erztöne je eigene Zeichen, und ein zweites Erz ist vier Farben:

```json
{
  "extends": "mypack:textures/block/ore_template",
  "palette": { "4": "#768291", "5": "#5E6977", "6": "#66717F", "7": "#848F9D" }
}
```

Eine Variante, die wirklich ein anderes Muster will, bringt wie oben eigene `rows` mit und erbt dann nur noch die Palette. Das lohnt, wenn es auf die Farben ankommt und die Form nebensächlich ist; kommt es auf die Form an, gehört die Form in die Vorlage und die Varianten nennen Farben.

**Eine Vorlage muss gar keine Textur sein.** Eine Karte wird dem Spiel nur gereicht, wenn ihr Pfad auf `.png` endet. Eine Vorlage unter `textures/block/ore_template.json` ist für das Spiel also unsichtbar und nur zum Erben da, während eine unter `textures/block/ore_template.png.json` auch Anfragen nach `ore_template.png` beantworten würde. Benenne eine geteilte Form ohne das `.png`, dann kann sie niemand versehentlich anfordern.

**Eine Vorlage darf auch ein echtes Bild sein.** Zeigt `extends` auf eine PNG, die irgendein Pack oder das Spiel selbst liefert, ändert die Palette ihre Bedeutung: Die Schlüssel sind dann die Farben, die im Bild schon stecken, die Werte die Farben, die an ihre Stelle treten. Nichts wird abgepaust und keine `rows` werden geschrieben, ein Pack kann eine Vanilla- oder Mod-Textur also an Ort und Stelle umfärben:

```json
{
  "extends": "minecraft:textures/block/coal_ore.png",
  "palette": {
    "#3F3F3F": "#C4353F",
    "#343434": "#8E2029",
    "#373737": "#A32A33",
    "#454545": "#DE5F68"
  }
}
```

Das ist ein Rubinerz in Vanillas eigenem Stein: Die vier Sprenkeltöne werden getauscht, jedes andere Pixel bleibt, wie es war. Eine Farbe, die das Bild nicht enthält, passt schlicht nie, und die Größe kommt aus dem Bild, sofern du keine nennst, dann muss sie übereinstimmen.

`extends` bevorzugt eine Pixelkarte: Es sucht zuerst die Karte unter diesem Pfad und weicht erst auf das Bild aus, wenn kein Pack eine liefert. Ein Name, der weder das eine noch das andere ist, wird gemeldet statt leer gezeichnet. Auf einem Bild aufzubauen ist Client-Arbeit, denn dabei werden die Ressourcen des Spiels gelesen, ein dedizierter Server tut es also nie.

**Eine Vorlage lässt sich auch einfärben, statt sie umzumalen.** `tint` nennt zwei Farben und färbt alles, was die Karte erbt, entlang der Rampe zwischen ihnen um. Die Helligkeit jeder geerbten Farbe ist ihr Platz auf dieser Rampe: Schwarz landet auf `from`, Weiß auf `to`, und jeder Ton dazwischen wird anteilig gemischt. Die Transparenz bleibt unangetastet. Damit sind eine graue Vorlage und zwei Farben eine vollständige Variante:

```json
{
  "extends": "mypack:textures/item/materials/ingot.png",
  "tint": { "from": "#626669", "to": "#DBDFE2" }
}
```

`from` darf fehlen, dann ist es Schwarz und der Tint wird zur gewöhnlichen Multiplikation, also zu dem, was ein `tintindex` beim Zeichnen tut. Der Unterschied: Dieser hier wird einmal in die PNG gezeichnet und zwischengespeichert, kostet also pro Bild nichts und erreicht auch eine Textur, die sonst niemand einfärbt. Dafür kann er einem Biom nicht folgen, wie `grass` oder `foliage` es können.

Die Vorlage bleibt eine gewöhnliche Karte: Öffne sie, sieh sie dir an, sie zeichnet sich als das Grau, das sie ist. Beide Farben nehmen `#RRGGBB`, `#AARRGGBB` oder ein vorangestelltes `0x`, und ein Wert, der nichts davon ist, lässt die Karte lieber ungezeichnet, als sie falsch zu färben. Ein Tint wird vererbt wie alles andere, und der erste die Kette hinunter gewinnt, der eigene Tint einer Variante schlägt also den der Vorlage. Auf einer Bildvorlage wirkt er ebenfalls, dort nach den Farbtauschen der Palette.

**Ein Tint ist eine Rampe zwischen zwei Farben** und taugt darum nur für eine Textur, deren Töne auf einer solchen liegen. Eine Form mit zwei zusammenhanglosen Bereichen, der Stein eines Erzes gegen seine Sprenkel, ist das nicht und will stattdessen ihre Palette ausgeschrieben.

**Zu wissen, was die Zeichen einer Vorlage bedeuten**, ist beim Erben das Unangenehme, und genau dafür ist der `notes`-Block oben da: ein Zeichen zu einer kurzen Zeile, vererbt wie die Palette und nie gezeichnet. Beschrifte die Zeichen einer Vorlage, und wer sie erbt, weiß, welche er überschreiben muss.

`/rdpl pixelmap <namespace:pfad>` berichtet dann, was eine Karte tatsächlich geworden ist, und das ist der verlässliche Weg, eine Variante zu schreiben, ohne jede Datei die Kette hinauf zu öffnen:

```
oretest:textures/block/ruby_ore.png is 16x16
  built from oretest:textures/block/ruby_ore.png.json
  built from oretest:textures/block/gem_ore.png.json
  rows come from oretest:textures/block/gem_ore.png.json
  1  #C4353F  17 pixel(s)  set by ruby_ore.png.json  ore body, most of every lump
  2  #8E2029   8 pixel(s)  set by ruby_ore.png.json  ore shadow, the darkest tone
  a  #747474  86 pixel(s)  set by gem_ore.png.json   stone, the commonest tone
```

Jedes Zeichen steht da mit seiner Farbe, wie viele Pixel es abdeckt, welche Datei der Kette es gesetzt hat und wofür diese Datei es hält. Der Pfad darf kurz angegeben werden, `mypack:block/panel`, oder vollständig. Ein Zeichen mit 0 Pixeln ist eines, das die Palette nennt und die Zeilen nie benutzen, meist ein Tippfehler in einer Zeile.

**Gezeichnete Bilder bleiben auf der Platte**, in `rdploader/pixelmap-cache`, in je einem Ordner pro Namespace und benannt nach der Textur mit einem Hash ihrer Quelle am Ende. Der Hash umfasst die ganze Kette, die Karte selbst und jede Vorlage darüber, das Bearbeiten einer Vorlage ändert also den Stempel jeder erbenden Variante, und alle werden neu gezeichnet. Wird eine Karte neu gezeichnet, werden ihre älteren Dateien weggeräumt.

Der Ordner wird außerdem bei jedem Durchsuchen der Packs durchgegangen, und jedes Bild, dessen Karte kein Pack mehr liefert, wird gelöscht, samt jedem leer gebliebenen Ordner. Benenn eine Textur um, lass ein Pack weg, lösch eine Karte, ihr Bild geht mit, statt für immer liegen zu bleiben. Den ganzen Ordner zu löschen kostet nichts außer der Zeit, sie erneut zu zeichnen, und beim Suchen nach Packs wird er übersprungen, also nie für ein Pack gehalten.

Eine PNG gewinnt immer. Gibt es sowohl `panel.png` als auch `panel.png.json`, wird die PNG ausgeliefert und die Karte nie gezeichnet; eine erzeugte Textur lässt sich später also durch eine gemalte ersetzen, ohne irgendetwas anzufassen, das darauf zeigt.

**Niemand muss diese Dateien von Hand schreiben.** Das Repository liefert in [`pixelmap/`](../pixelmap) Skripte für den ganzen Weg hin und zurück: `png_to_pixelmap.py` macht aus einer PNG eine Karte, `convert_pack.py` erledigt das für jede Textur eines Packs, und `verify_pack.py` zeichnet die Karten eines umgewandelten Packs und vergleicht sie mit den PNGs, aus denen sie stammen, damit man einer Umwandlung trauen kann, bevor man die Originale beiseitelegt.

### Fallen, die man kennen sollte

**Ein Modell, das ein fertiges Vanilla-Modell nennt, erbt auch Vanillas Texturen.** `torch`, `ladder`, `oak_door_bottom_left` und `wheat_stage0` bringen alle ihre eigenen Texturen mit, ein Modell, das auf eines davon zeigt, bekommt also Vanillas Aussehen, ganz gleich, was du daneben schreibst. Eltern-Modelle wie `cube_all`, `cross` und `crop` nehmen ihre Texturen aus dem Modell, das sie nennt, und verhalten sich wie erwartet, ebenso die Vorlagen für Tür, Falltür und Tor.

**Namen kommen aus der Sprachdatei.** Ein Block oder Item zeigt seinen rohen Schlüssel, bis `lang/en_us.json` ihm einen gibt, und die Schlüssel sind die des Spiels: `block.mypack.ruby_ore` für einen Block und das Item, das ihn setzt, `item.mypack.ruby` für ein Item, `itemGroup.mypack.tab` für einen Kreativtab, `fluid_type.mypack.molten_ruby` und `fluid.mypack.molten_ruby` für eine Flüssigkeit, `effect.mypack.ruby_sight` für einen Trankeffekt, `entity.mypack.angry_cow` für eine Entity-Variante, `biome.mypack.ruby_forest` für ein Biom. Ein Name je Ding; nichts auf dieser Version will einen Schlüssel doppelt.

## Damit Vanilla deinen Block richtig behandelt

Vanilla erkennt seine eigenen Blöcke an einem Dutzend Stellen an ihrer Identität, deshalb funktioniert ein Pack-Block, der offensichtlich funktionieren sollte, oft nicht. Zwei Schlüssel decken das ab.

```json
{
  "material": "ground",
  "plantTypes": ["plains", "crop"],
  "behavesAs": ["till", "path"],
  "variants": { "ruby_grass": { "hardness": 0.6 } }
}
```

**`plantTypes`** listet die Pflanzentypen auf, die dein Block unterstützt, damit Setzlinge, Feldfrüchte und Blumen darauf gepflanzt werden können: `plains`, `desert`, `beach`, `cave`, `water`, `nether` und `crop`. **1.21.1** kennt gar keine Pflanzentypen; dort entscheidet die eigene Bodenliste einer Pflanze, und der Schlüssel wird gelesen und ignoriert.

**`behavesAs`** lässt Vanilla deinen Block wie einen der eigenen behandeln:

| Wert | Was er macht |
| --- | --- |
| `till` | Eine Hacke macht Ackerboden daraus, oder das, was `hoeTillsInto` in der Config nennt |
| `path` | Eine Schaufel macht einen Trampelpfad daraus, oder das, was `shovelPathBecomes` nennt |

## Items

`<namespace>/items/*.json`

Jeder Schlüssel in `variants` ist ein Item, registriert unter dem Namespace des Packs, eine Datei mit `ruby_apple` und `dried_ruby_apple` registriert also `mypack:ruby_apple` und `mypack:dried_ruby_apple`; der Name der Datei selbst ist nur eine Gruppierung. Das Modell jedes Items wird aus `textures/item/<name>.png` erzeugt, sofern das Pack keine `models/item/<name>.json` mitbringt.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht. Ein Schlüssel, der für einen Typ vermerkt ist, wird nur von diesem Typ gelesen.

```json
{
  "inherits": "mypack:food_template",
  "type": "food",
  "creativeTab": "mypack:tab",
  "material": "mypack:ruby",
  "toolClass": "pickaxe",
  "slot": "head",
  "eat": true,
  "alwaysEdible": false,
  "useDuration": 32,
  "attackSpeed": -2.4,
  "cooldown": 40,
  "container": "minecraft:glass_bottle",
  "crop": "mypack:ruby_crop",
  "soil": "minecraft:farmland",
  "potionTypes": ["mypack:ruby_tonic"],
  "requires": ["mypack"],
  "variants": {
    "ruby_apple": {
      "maxSize": 64,
      "rarity": "rare",
      "healAmount": 6,
      "saturation": 0.8,
      "tags": ["forge:foods"],
      "potion": "minecraft:speed,600,1"
    },
    "dried_ruby_apple": { "healAmount": 3, "saturation": 0.4 }
  }
}
```

| Typ | Was du bekommst |
| --- | --- |
| `basic` | Ein einfaches Item. Wird genommen, wenn `type` fehlt |
| `food` | Wird gegessen, mit Hunger und Sättigung |
| `drink` | Wird getrunken statt gegessen und lässt ein leeres Behältnis zurück |
| `tool` | Spitzhacke, Axt, Schaufel, Hacke oder Schwert aus einem Material |
| `armor` | Helm, Brustpanzer, Beinschutz oder Stiefel aus einem Material |
| `seed` | Pflanzt eine deiner Feldfrüchte |
| `potion` | Wendet beim Benutzen deine Trankeffekte an |
| `potion_bottle` | Fasst deine Trankarten und zeigt sie in einem Kreativtab |
| `container` | Ein Beutel: ein Inventar, das in der Hand getragen oder angelegt wird, siehe [Behälter](#behälter) |

Ein `potion_bottle` listet mit `potionTypes` auf, was es fassen kann, als Array von Namen der Trankarten, etwa `["mypack:ruby_tonic"]`. Eines mit leerer Liste registriert nichts, und das Log sagt es.

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `variants` | ja | Objekt aus Variantenname zu Variante | | Ein Item pro Eintrag. Der Schlüssel ist sein Registry-Name und benennt sein Modell, seine Textur und seinen Sprachschlüssel |
| `type` | nein | einer der Typen oben | `basic` | Welchen Typ das Item annimmt |
| `creativeTab` | nein | Tab-Name | keiner | Der Tab, in dem es auftaucht |
| `material` | tool, armor | Materialname | keiner | Aus welchem deiner Materialien es gemacht ist |
| `toolClass` | tool | `pickaxe`, `axe`, `shovel`, `hoe`, `sword` | keiner | Welches Werkzeug es ist |
| `slot` | armor | `head`, `chest`, `legs`, `feet` | keiner | Wo es getragen wird. `helmet`, `chestplate`, `leggings` und `boots` gehen auch |
| `eat` | food | boolean | `false` | Nutzt die Ess-Animation |
| `alwaysEdible` | food | boolean | `false` | Lässt sich auch bei voller Hungerleiste essen |
| `useDuration` | nein | int, Ticks | `32` | Wie lange das Benutzen dauert |
| `attackSpeed` | nein | float | passend zur Werkzeugklasse | Für `tool` das Angriffstempo-Attribut, ein Schwert liegt bei `-2.4` |
| `cooldown` | nein | int, Ticks | `0` | Für `food`, `drink` und `potion`: wie lange das Item nach dem Verzehr die erneute Benutzung verweigert |
| `container` | drink | Itemname | keiner | Was übrig bleibt, etwa eine Flasche. An einem `container`-Item sind dieser Schlüssel stattdessen die Einstellungen des Beutels selbst, siehe [Behälter](#behälter) |
| `crop` | seed | Blockname | keiner | Die Feldfrucht, die es pflanzt |
| `soil` | seed | Blockname | `minecraft:farmland` | Worauf es gepflanzt werden kann |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

Variantenschlüssel:

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `maxSize` | nein | 1 bis 64 | `64` | Stapelgröße |
| `rarity` | nein | `common`, `uncommon`, `rare`, `epic` | `common` | Farbe des Namens im Tooltip |
| `healAmount` | food | int, halbe Hähnchenkeulen | `0` | Wiederhergestellter Hunger |
| `saturation` | food | float | `0.0` | Wiederhergestellte Sättigung |
| `tags` | nein | Liste von Tag-IDs | keine | Item-Tags, in die diese Variante geschrieben wird; die Tag-Dateien werden für dich erzeugt |
| `potion` | food, drink | `potion,duration,amplifier` | keiner | Ein Effekt, der beim Essen oder Trinken der Variante angewandt wird. Ein vierter Teil, `true`, macht ihn umgebend. Ein guter Effekt wird im Tooltip genannt |

## Flüssigkeiten

`<namespace>/fluids/*.json`

Der Pfad der Datei ist der Registry-Name der Flüssigkeit, sofern `name` ihn nicht überschreibt.

```json
{
  "name": "molten_ruby",
  "still": "mypack:block/molten_ruby_still",
  "flow": "mypack:block/molten_ruby_flow",
  "color": "C0304A",
  "bucket": true,
  "luminosity": 12,
  "density": 2000,
  "temperature": 1500,
  "viscosity": 4000,
  "gaseous": false,
  "creativeTab": "mypack:tab",
  "requires": ["mypack"],
  "block": {
    "material": "lava",
    "flammability": 0,
    "fireSpread": 0,
    "quantaPerBlock": 8,
    "potions": ["minecraft:wither,200,0"]
  }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `name` | nein | string | der Dateiname | Der Registry-Name der Flüssigkeit |
| `still` | nein | Texturpfad | Vanilla-Wasser, stehend | Textur für die stehende Flüssigkeit |
| `flow` | nein | Texturpfad | Vanilla-Wasser, fließend | Textur für die fließende Flüssigkeit |
| `color` | nein | Hex-Farbe | keine | Färbung, die auf diese Texturen gelegt wird |
| `bucket` | nein | boolean | `true` | Einen Eimer dafür registrieren |
| `luminosity` | nein | 0 bis 15 | `0` | Abgegebenes Licht |
| `density` | nein | int | `1000` | Negativ steigt nach oben, wie ein Gas |
| `temperature` | nein | int, Kelvin | `300` | Wasser ist 300, Lava 1300 |
| `viscosity` | nein | int | `1000` | Wie träge sie fließt. Wasser ist 1000, Lava 6000 |
| `gaseous` | nein | boolean | `false` | Wird als Gas behandelt |
| `creativeTab` | nein | Tab-Name | keiner | Der Tab, in dem der Eimer auftaucht |
| `block` | nein | Objekt | | Der Flüssigkeitsblock. `material` (`water`), `flammability` (`0`), `fireSpread` (`0`), `quantaPerBlock` (`0`), `potions` (keine, eine Liste von Effekten für alles, was darin steht, je geschrieben als `potion,duration,amplifier`, mit einem optionalen vierten Teil `true` für einen umgebenden) |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

## Materialien, Tabs, Sounds, Tags

`<namespace>/materials/*.json`

Der Pfad der Datei ist der Name des Materials, das ein Werkzeug- oder Rüstungsitem dann in `material` nennt.

```json
{
  "harvestLevel": 3,
  "durability": 1200,
  "efficiency": 9.0,
  "damage": 3.5,
  "enchantability": 18,
  "repairItem": "mypack:ruby",
  "reduction": [3, 6, 8, 3],
  "toughness": 2.0,
  "equipSound": "item.armor.equip_diamond",
  "armorTexture": "mypack:ruby"
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `harvestLevel` | nein | 0 bis 4 | `1` | Werkzeugstufe. 0 Holz, 1 Stein, 2 Eisen, 3 Diamant, 4 Netherit |
| `durability` | nein | int | `250` | Benutzungen, bis es kaputtgeht |
| `efficiency` | nein | float | `6.0` | Abbaugeschwindigkeit. Diamant ist 8 |
| `damage` | nein | float | `2.0` | Bonus auf den Angriffsschaden |
| `enchantability` | nein | int | `14` | Wie gut die Verzauberungen ausfallen. Gold ist 22 |
| `repairItem` | nein | Itemname | keiner | Was es im Amboss repariert |
| `reduction` | nein | Liste aus vier Ints | | Rüstungspunkte, in der Reihenfolge Füße, Beine, Brust, Kopf |
| `toughness` | nein | float | `0.0` | Rüstungshärte, wie Diamant sie hat |
| `equipSound` | nein | Soundname | `item.armor.equip_iron` | Sound beim Anlegen der Rüstung |
| `armorTexture` | nein | Texturpräfix | der Dateiname | Die Textur der getragenen Rüstung, gelesen aus `textures/models/armor/<name>_layer_1.png` und `_layer_2.png` unter diesem Namespace |

`<namespace>/tabs/*.json`

Der Pfad der Datei ist der Name des Tabs, sofern `label` ihn nicht überschreibt, und Blöcke und Items nennen ihn in `creativeTab` als `<namespace>:<label>`.

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `label` | nein | string | der Dateiname | Die Id des Tabs: Blöcke und Items nennen sie in `creativeTab`, der angezeigte Name kommt aus `itemGroup.<namespace>.<label>` in den Sprachdateien |
| `icon` | nein | Itemname | keiner | Das Item, das auf dem Tab abgebildet ist |

`<namespace>/sounds/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

Das Vanilla-Format von `sounds.json`, ein Pack kann also eigenes Audio mitbringen. Eine Datei hier registriert die Sound-Events; das Audio liest der Client nach wie vor über die packeigene `assets/<namespace>/sounds.json`, liefere also beides, den Index unter `assets` und die Events unter `data`.

`<namespace>/tags/<kind>/*.json`

Tags sind das Format des Spiels im Ordner des Spiels, und ein Pack liefert sie wie in einem Datenpaket: `tags/items/ores/ruby.json` (1.21.1: `tags/item/`) mit `{ "values": ["mypack:ruby_ore"] }` steckt das Erz in `mypack:ores/ruby`, und eine Datei unter `data/forge/tags/items/ores/ruby.json` (1.21.1: `data/c/...`) ergänzt den gemeinsamen Konventions-Tag, den jede Mod liest. Eigene Blöcke und Items eines Packs nennen ihre stattdessen im `tags` der Variante, und die Dateien werden für dich geschrieben; ein `harvestTool` und `harvestToolLevel` schreiben die Tags `mineable` und `needs_*_tool` auf dieselbe Weise.

Das Ore Dictionary von 1.12.2 ist das, was Tags abgelöst haben. Seine Namen bilden sich auf die Konventions-Tags ab: `oreRuby` ist `forge:ores/ruby` auf 1.20.1 und `c:ores/ruby` auf 1.21.1, `ingotCopper` ist `ingots/copper`, `gemRuby` ist `gems/ruby`, `dustX` ist `dusts/x`, `nuggetX` ist `nuggets/x`, `blockX` ist `storage_blocks/x`, und `logWood`, `plankWood` und `stickWood` sind die spieleigenen `minecraft:logs`, `minecraft:planks` und das Konventions-`rods/wooden`. Eine Datei, die ein Item aus einem Tag entfernt, gibt es nicht; ein Datenpaket-Tag mit `"replace": true` schreibt stattdessen den ganzen Tag neu.

## Ofenrezepte und Brennstoffe

`<namespace>/furnace/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

Fügt Schmelzrezepte hinzu und entfernt sie.

```json
{
  "remove": [
    "minecraft:iron_ingot",
    { "input": "minecraft:gold_ore" },
    { "input": "minecraft:iron_ore", "result": "minecraft:iron_ingot" }
  ],
  "add": [
    { "input": "mypack:ruby_ore", "output": "mypack:ruby", "count": 2, "experience": 1.0 }
  ]
}
```

Einträge unter `add`:

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `input` | ja | Itemname | keiner | Was hineinkommt |
| `output` | ja | Itemname | keiner | Was herauskommt |
| `count` | nein | int | `1` | Wie viele herauskommen |
| `experience` | nein | Zahl | `0.0` | Erfahrung pro Schmelzvorgang. Eisenerz gibt 0.7 |

Einträge unter `remove` sind entweder ein bloßer Itemname, der jedes Rezept entfernt, das ihn herstellt, oder ein Objekt, das mit `input`, `result` oder beidem eingrenzt. Eine Entfernung, die weder das eine noch das andere nennt, wird übersprungen, und das Log sagt es.

`<namespace>/fuels/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

```json
{
  "fuels": [
    { "item": "mypack:ruby_coal", "burnTime": 2400 },
    { "tag": "forge:gems/ruby", "burnTime": 800 }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `item` | eines von beiden | Itemname | keiner | Das Item, das brennt |
| `tag` | eines von beiden | Tag-Id | keiner | Alles in diesem Tag brennt |
| `burnTime` | ja | int, Ticks | `0` | Kohle ist 1600, ein Brett 300 |

## Tränke, Trankarten und Brauen

`<namespace>/potions/*.json`

Der Pfad der Datei ist der Registry-Name des Effekts, `mypack/potions/ruby_sight.json` registriert also `mypack:ruby_sight`, den ein Tranktyp dann nennt.

```json
{
  "name": "effect.mypack.ruby_sight",
  "color": "C0304A",
  "badEffect": false,
  "beneficial": true,
  "instant": false,
  "effectiveness": 0.5,
  "icon": { "x": 0, "y": 0 },
  "iconTexture": "mypack:textures/gui/effects.png",
  "attributes": [
    { "attribute": "minecraft:generic.movement_speed", "uuid": "91AEAA56-376B-4498-935B-2F7F68070635", "amount": 0.2, "operation": 2 }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `name` | nein | Übersetzungsschlüssel | `effect.<namespace>.<name>` | Was der Spieler sieht |
| `color` | nein | Hex-Farbe | `FFFFFF` | Farbe der Partikel |
| `badEffect` | nein | boolean | `false` | Gilt als schädlich, ein fermentiertes Spinnenauge kehrt ihn also um |
| `beneficial` | nein | boolean | `false` | Wird als guter Effekt angezeigt |
| `instant` | nein | boolean | `false` | Wirkt einmalig statt über die Zeit |
| `effectiveness` | nein | float | `0.5` | Wie hoch die Mob-KI ihn einschätzt |
| `icon` | nein | Objekt mit `x` und `y` | `0`, `0` | Wo das Symbol im Blatt sitzt |
| `iconTexture` | nein | Texturpfad | Vanilla-Blatt | Dein eigenes Symbolblatt |
| `attributes` | nein | Liste von Objekten | keine | `attribute` (die Id des Spiels, etwa `minecraft:generic.movement_speed`), `uuid`, `amount` (`0.0`), `operation` (`0`) |

`<namespace>/potion_types/*.json`

Der Pfad der Datei ist der Registry-Name des Tranktyps, den ein `potion_bottle`-Item dann in `potionTypes` nennt.

```json
{
  "baseName": "ruby_sight",
  "effects": [
    { "potion": "mypack:ruby_sight", "duration": 3600, "amplifier": 0, "ambient": false, "showParticles": true }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `baseName` | nein | string | Namespace und Name | Der Name, aus dem die Flasche gebaut wird |
| `effects` | ja | Liste von Objekten | | Siehe unten |

Jeder Effekt nimmt `potion` (Pflicht), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) und `showParticles` (`true`).

`<namespace>/brewing/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

```json
{
  "brewing": [
    { "input": "minecraft:potion", "ingredient": "mypack:ruby", "output": "mypack:ruby_potion", "requires": ["mypack"] },
    { "from": "minecraft:awkward", "ingredient": "mypack:ruby", "to": "mypack:ruby_tonic" }
  ]
}
```

Jeder Eintrag ist entweder `input`, `ingredient` und `output`, was ein Item zu einem anderen braut, oder `from`, `ingredient` und `to`, was einen Tranktyp in einen anderen verwandelt. `ingredient` ist in beiden Fällen Pflicht, und ein Eintrag nimmt auch `requires`, sodass ein einzelnes Rezept übersprungen werden kann, ohne dass es die Datei wird.

## Expositionen

`<namespace>/exposures/*.json`

Der Pfad der Datei ist der Name der Gefahr, und ihre Todesmeldung kommt aus dem Sprachschlüssel `death.attack.rdpl.<Dateiname>`.

Eine vom Pack definierte Gefahr: benannte Blöcke und Items belasten Spieler, die in der Nähe stehen oder sie bei sich tragen, in Stufen; jede Stufe bringt Effekte und wiederkehrenden Schaden. Eine Datei definiert eine Gefahr, mehrere laufen nebeneinander.

```json
{
  "blocks": [ "mypack:nuclear_waste=2", "mypack:uranium_ore" ],
  "items": [ "mypack:nuclear_waste" ],
  "immunity": "mypack:antirad",
  "scanInterval": 20,
  "range": 10,
  "sourcesForNextLevel": 4,
  "skipsCreative": true,
  "levels": [
    { "effect": "mypack:radiation_1", "damage": 4.0, "damageInterval": 160,
      "effects": [ { "potion": "minecraft:nausea", "duration": 0, "amplifier": 0, "ambient": false, "showParticles": false },
                   { "potion": "minecraft:hunger" } ] },
    { "effect": "mypack:radiation_2", "damage": 8.0, "damageInterval": 120,
      "effects": [ { "potion": "minecraft:nausea", "amplifier": 1 }, { "potion": "minecraft:hunger", "amplifier": 1 } ] }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `blocks` | eines von beiden | Liste aus `block` oder `block=stufe` | | Blöcke, die einen Spieler in der Nähe belasten. Ohne Stufe gilt 1 |
| `items` | eines von beiden | Liste aus `item` oder `item=stufe` | | Items, die einen Spieler belasten, der sie trägt oder anhat |
| `levels` | ja | Liste von Stufen | | Die Schwereleiter, der erste Eintrag ist Stufe 1. Ein Spieler bekommt die höchste Stufe, die irgendeine Quelle erreicht |
| `immunity` | nein | Trankname | keine | Ein Effekt, dessen Träger gar nicht belastet wird |
| `scanInterval` | nein | Ticks | `20` | Wie oft Umgebung und Inventar geprüft werden |
| `range` | nein | Blöcke | `10` | Wie weit ein Block wirkt, als Kugel |
| `sourcesForNextLevel` | nein | int | `0` | So viele Quellen einer Stufe in der Nähe heben sie um eine weitere an. `0` schaltet das ab |
| `skipsCreative` | nein | boolean | `true` | Kreativ- und Zuschauerspieler bleiben verschont |

Jede Stufe:

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `effect` | ja | Trankname | | Der Effekt, der die Stufe am Spieler markiert. Seine Anwesenheit steuert den Schaden, es sollte also einer sein, den das Pack dafür definiert |
| `damage` | nein | halbe Herzen | `0` | Schaden alle `damageInterval` Ticks, solange die Stufe anliegt. Er ignoriert Rüstung |
| `damageInterval` | nein | Ticks | `160` | Wie oft der Schaden fällt |
| `effects` | nein | Liste von Effekten | keine | Zusätzliche Effekte, gleiche Form wie bei Trankarten. Ohne `duration` folgen sie dem Prüfintervall |

Die Stufeneffekte halten etwas über die nächste Prüfung hinaus, Weggehen lässt sie also von selbst auslaufen. Der Tod durch den Schaden liest seine Meldung aus `death.attack.rdpl.<Dateiname>`, die die Sprachdateien des Packs liefern.

## Dorfbewohner und Handel

`<namespace>/villagers/*.json`

Der Pfad der Datei ist der Registry-Name des Berufs, `mypack/villagers/jeweller.json` registriert also `mypack:jeweller`, den ein Handel dann in `profession` nennt.

```json
{
  "jobSite": "mypack:gem_bench",
  "workSound": "minecraft:entity.villager.work_toolsmith"
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `jobSite` | nein | Blockname | keiner | Der Block, den ein Dorfbewohner beansprucht, um diesen Beruf zu ergreifen, so wie ein Schmiedetisch einen Werkzeugschmied macht |
| `workSound` | nein | Soundname | keiner | Was er beim Arbeiten an diesem Block abspielt |

Laufbahnen sind eine Idee aus 1.12.2, die das Spiel nicht mehr kennt: Ein Beruf ist ein Handelssatz, ein Pack mit zwei Laufbahnen liefert also zwei Dorfbewohner-Dateien. Wie der Dorfbewohner aussieht, ist eine gewöhnliche Textur, geliefert unter `assets/<namespace>/textures/entity/villager/profession/<name>.png` und `textures/entity/zombie_villager/profession/<name>.png`, genau dort, wo das Spiel seine eigenen hat.

`<namespace>/trades/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

```json
{
  "trades": [
    {
      "profession": "mypack:jeweller",
      "level": 1,
      "maxUses": 12,
      "xp": 2,
      "buy": { "item": "minecraft:emerald", "min": 2, "max": 4 },
      "sell": { "item": "mypack:ruby", "min": 1 }
    }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `profession` | ja | Berufsname | | Wessen Handel das ist |
| `level` | nein | int | `1` | Auf welcher Handelsstufe er auftaucht, 1 bis 5 |
| `maxUses` | nein | int | `12` | Wie oft er genutzt werden kann, bevor er sperrt |
| `xp` | nein | int | `2` | Erfahrung, die der Dorfbewohner pro Handel auf seine nächste Stufe hin sammelt |

Ein Stapel ist `item` mit `min` (`1`) und `max` (`min`), ein fester Preis ist also einfach nur `min`.

## Entity-Varianten

`<namespace>/entities/*.json`

Der Pfad der Datei ist der Registry-Name der Variante, `mypack/entities/angry_cow.json` registriert also `mypack:angry_cow`, und genau darauf beziehen sich `becomes`, das Spawn-Ei und der Weltspeicher.

Eine Datei hier macht aus einer vorhandenen Entity eine neue. Sie ist eine echte Entity für sich: eigener Registry-Name, eigener Name in der Welt, eigenes Spawn-Ei und eine eigene Beutetabelle, wenn du ihr eine gibst, aufgebaut auf dem Verhalten einer anderen Entity, statt sie zu ersetzen. An der Entity, die sie kopiert, ändert sich nichts.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht.

```json
{
  "entity": "minecraft:cow",
  "name": "Angry Cow",
  "showName": false,
  "texture": "mypack:textures/entity/angry_cow.png",
  "lootTable": "mypack:entities/angry_cow",
  "profession": "mypack:jeweller",
  "baby": 0.05,
  "becomes": [
    { "variant": "mypack:angry_cow", "weight": 95 },
    { "variant": "mypack:little_angry_cow", "weight": 5 }
  ],
  "sounds": { "ambient": "entity.cow.ambient", "hurt": "entity.cow.hurt", "death": "entity.cow.death", "target": "mypack:scream", "targetVaries": 3, "explode": "mypack:boom" },
  "soundVolume": 1.0,
  "soundPitch": 1.0,
  "immuneTo": ["fall", "drown", "explosion", "magic", "cactus", "lava", "wither", "starve", "in_wall"],
  "jumpMultiplier": 1.0,
  "fallDamage": 1.0,
  "maxFallHeight": 3,
  "breathesUnderwater": false,
  "swims": false,
  "amphibious": false,
  "waterSlowdown": 0.8,
  "absorption": 0,
  "experience": 3,
  "creatureAttribute": "undefined",
  "effects": [ { "potion": "minecraft:strength", "amplifier": 1 } ],
  "despawns": true,
  "despawnAfter": 600,
  "noAI": false,
  "leftHanded": false,
  "fireproof": false,
  "invulnerable": false,
  "glowing": false,
  "invisible": false,
  "dropChance": 0.085,
  "scale": 1.0,
  "angryScale": 1.2,
  "leashable": true,
  "steerable": false,
  "width": 0.9,
  "height": 1.4,
  "pathPriorities": { "WATER": 0.0, "LAVA": -1.0, "DANGER_FIRE": 8.0, "DOOR_WOOD_CLOSED": 0.0 },
  "egg": { "primary": "AABBCC", "secondary": "112233" },
  "attributes": {
    "maxHealth": 20,
    "movementSpeed": 0.32,
    "attackDamage": 4,
    "knockbackResistance": 0.0,
    "followRange": 32,
    "armor": 4
  },
  "hostile": true,
  "targets": ["minecraft:player"],
  "passive": false,
  "persistent": false,
  "silent": false,
  "picksUpLoot": false,
  "hideArmor": false,
  "hideHeld": false,
  "tint": "C0304A",
  "tintParts": ["body", "armor", "held"],
  "ignoresSpawnRules": false,
  "throws": true,
  "throwAmmo": 8,
  "throwReload": 3,
  "throwRetreat": 3,
  "throwPower": 1.0,
  "throwArc": 0.35,
  "explodes": false,
  "explosionPower": 3.0,
  "explosionFuse": 30,
  "explosionFire": false,
  "equipment": {
    "mainhand": "minecraft:tnt",
    "offhand": "minecraft:shield",
    "head": "minecraft:iron_helmet",
    "chest": "minecraft:iron_chestplate",
    "legs": "minecraft:iron_leggings",
    "feet": "minecraft:iron_boots"
  },
  "spawns": [
    { "creatureType": "creature", "weight": 4, "min": 1, "max": 2 }
  ],
  "biomes": ["minecraft:plains"],
  "biomeTypes": ["plains"],
  "trackingRange": 80,
  "trackVelocity": true,
  "trackingFrequency": 3,
  "requires": ["mypack"]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `entity` | ja | `namespace:name` | keine | Die Entity, auf der aufgebaut wird. Die jedes Mods, solange sie einen einfachen Welt-Konstruktor hat |
| `name` | nein | string | keiner | Der Name, den sie in der Welt, in Todesmeldungen und auf ihrem Ei trägt |
| `showName` | nein | boolean | `false` | Zeigt den Namen, ohne dass man sie ansieht |
| `texture` | nein | `namespace:textures/entity/<file>.png` | keine | Ein eigener Skin, genauso aufgeteilt wie der der kopierten Entity |
| `lootTable` | nein | `namespace:entities/<name>` | die der Basis | Was sie droppt. Ohne diesen Schlüssel droppt sie, was die kopierte Entity droppt |
| `profession` | nein | `namespace:name` | zufällig | Bei einem Dorfbewohner der Beruf, den er ausübt |
| `baby` | nein | boolean oder 0.0 bis 1.0 | `false` | Wie oft eines jung erscheint, und es bleibt dabei. `true` heißt immer, eine Zahl heißt dieser Anteil |
| `becomes` | nein | Liste | keine | Andere Varianten, zu denen diese beim Erscheinen werden kann, nach Gewicht. Siehe unten |
| `sounds` | nein | Objekt | die der Basis | `ambient`, `hurt` und `death`, jeweils ein registriertes Sound-Event. Zwei weitere, für die die Basis keinen Laut hat: `target` wird einmal gespielt, sooft sie ein Ziel fasst, und `explode` ist der Klang ihrer Explosion, ob sie sich mit `explodes` selbst sprengt oder mit `throws` TNT wirft. `targetVaries` verschiebt jedes Abspielen von `target` zufällig um bis zu so viele Halbtöne nach oben oder unten, `3` also bis zu einer Vierteloktave in beide Richtungen; `0` spielt ihn unverändert. Der Explosionsklang ersetzt den des Spiels |
| `soundVolume` | nein | Zahl | `1.0` | Wie laut diese Sounds sind |
| `soundPitch` | nein | Zahl | `1.0` | Wie hoch sie klingen. Unter 1 tiefer, über 1 quietschiger |
| `immuneTo` | nein | Liste von Schadensarten | keine | Schaden, der an ihr abprallt, nach den Schadensart-Namen des Spiels: `fall`, `drown`, `explosion`, `magic`, `cactus`, `lava`, `wither`, `starve`, `in_wall`, `freeze` und der Rest |
| `jumpMultiplier` | nein | float | `1.0` | Wie viel höher sie springt als die kopierte Entity |
| `fallDamage` | nein | float | `1.0` | Multipliziert den Sturzschaden. `0` nimmt ihn ganz weg |
| `maxFallHeight` | nein | int | der der Basis | Wie tief sie beim Wegfinden springt |
| `breathesUnderwater` | nein | boolean | `false` | Ertrinkt nie und sinkt zu Boden, um dort zu laufen, statt zur Oberfläche zu schwimmen. Sie findet ihren Weg weiterhin über den Boden, tiefes Wasser, aus dem sie nicht herauslaufen kann, hält sie also fest |
| `swims` | nein | boolean | `false` | Bewegt sich durchs Wasser wie ein Tintenfisch oder ein Wächter und ertrinkt nie. Sie findet ihren Weg durch Wasser statt über Land, gehört also ins Wasser und ist außerhalb gestrandet |
| `amphibious` | nein | boolean | `false` | Läuft an Land und schwimmt richtig im Wasser und wechselt die Art der Wegfindung beim Hinein- und Hinausgehen. Sie ertrinkt nie. Was sie verfolgt hat, vergisst sie am Wasserrand, sie zögert also jedes Mal kurz beim Übergang |
| `waterSlowdown` | nein | float | `0.8` | Wie stark Wasser sie bremst. Höher ist schneller |
| `absorption` | nein | float | `0` | Zusätzliche Herzen über ihrer Gesundheit |
| `experience` | nein | int | die der Basis | Wie viel Erfahrung sie droppt |
| `creatureAttribute` | nein | `undefined`, `undead`, `arthropod` oder `illager` | das der Basis | Als was sie zählt, damit Bann und Heiltränke sie entsprechend behandeln |
| `effects` | nein | Liste von Objekten | keine | Effekte, die sie immer hat: `{ "potion": "minecraft:strength", "amplifier": 1 }` |
| `despawns` | nein | boolean | `true` | Aus bleibt sie, auch wenn sie sonst entfernt würde |
| `despawnAfter` | nein | int, Sekunden | keine | Sie verschwindet still, sobald sie so lange in der Welt war, ganz gleich wie weit jemand entfernt ist |
| `noAI` | nein | boolean | `false` | Steht da, wo sie hingesetzt wurde, und tut nichts |
| `leftHanded` | nein | boolean | `false` | Hält ihre Waffe in der anderen Hand |
| `fireproof` | nein | boolean | `false` | Fängt überhaupt nie Feuer, nimmt also keinen Schaden durch Feuer oder Lava und brennt nicht im Tageslicht |
| `invulnerable` | nein | boolean | `false` | Nimmt von nichts Schaden außer von der Leere und vom Kreativmodus |
| `glowing` | nein | boolean | `false` | Durch Wände umrandet |
| `invisible` | nein | boolean | `false` | Wird nicht gezeichnet, ihre Ausrüstung aber schon |
| `dropChance` | nein | 0 bis 1 | `0` | Wie wahrscheinlich jedes Ausrüstungsstück droppt |
| `scale` | nein | float | `1.0` | Wie groß sie gezeichnet wird und wie groß ihre Hitbox ist |
| `angryScale` | nein | float | `scale` | Die Größe, auf die sie anschwillt, solange sie ein Ziel hat, und noch drei Sekunden danach |
| `leashable` | nein | boolean | `false` | Lässt sich an der Leine führen, auch wenn die kopierte Entity das nie konnte |
| `steerable` | nein | boolean | `false` | Lässt sich beim Reiten lenken |
| `width` | nein | float | die der Basis | Breite ihrer Hitbox, bevor `scale` angewendet wird |
| `height` | nein | float | die der Basis | Höhe ihrer Hitbox, bevor `scale` angewendet wird |
| `pathPriorities` | nein | Objekt | keines | Wodurch sie läuft, als `WATER`, `LAVA`, `DANGER_FIRE`, `DOOR_WOOD_CLOSED` und die übrigen Pfadtypen des Spiels, jeweils eine Zahl, wobei negativ „nie“ heißt |
| `egg` | nein | boolean oder Objekt | `true` | Ein Spawn-Ei, gefärbt wie das der kopierten Entity. `{ "primary": "AABBCC", "secondary": "112233" }` wählt eigene Farben, `false` lässt das Ei weg |
| `attributes` | nein | Objekt | keines | `maxHealth`, `movementSpeed`, `attackDamage`, `knockbackResistance`, `followRange`, `armor`. Ein Attribut, das die Entity normalerweise nicht hat, bekommt sie dazu |
| `hostile` | nein | boolean | `false` | Greift an, was sie erreicht, und wehrt sich, wenn sie verletzt wird. Eine feindliche Variante zählt für das Spiel als Monster, welche Basis sie auch hat, das Monsterlimit hält sie also und Friedlich räumt sie weg, und sie legt die Tieraufgaben ihrer Basis ab, Paaren, Anlocken, einem Elternteil, einem Besitzer oder Artgenossen folgen, Sitzen |
| `targets` | nein | Liste von Entity-Namen | der Spieler | Wonach sie sucht, solange sie feindselig ist. `minecraft:player` wird verstanden, obwohl der Spieler keine registrierte Entity ist |
| `passive` | nein | boolean | `false` | Hält sie davon ab, irgendetwas anzugreifen, egal wie sie sich sonst verhält |
| `persistent` | nein | boolean | `false` | Despawnt nie |
| `silent` | nein | boolean | `false` | Macht keinen Laut |
| `picksUpLoot` | nein | boolean | `false` | Hebt auf, worüber sie läuft |
| `hideArmor` | nein | boolean | `false` | Trägt ihre Rüstung, ohne dass sie gezeichnet wird |
| `hideHeld` | nein | boolean | `false` | Dasselbe für das, was sie in der Hand hält |
| `tint` | nein | Hex-Farbe | keine | Färbt die Entity beim Zeichnen ein |
| `tintParts` | nein | Liste aus `body`, `armor`, `held` | `["body"]` | Welche Teile die Färbung erreicht |
| `ignoresSpawnRules` | nein | boolean | `false` | Spawnt überall, wo sie hingesetzt wird, und ignoriert die geerbten Regeln |
| `throws` | nein | boolean | `false` | Wirft aus der Entfernung, was sie in der Hand hält, auf ihr Ziel, und zündet es an und zieht sich zurück, wenn das TNT ist. Braucht `hostile` |
| `throwAmmo` | nein | int | keine | Wie viele sie zu werfen hat. Weggelassen geht ihr nie etwas aus |
| `throwReload` | nein | int, Sekunden | `explosionFuse` | Wie lange die Hand leer bleibt, bis sie das nächste zieht |
| `throwRetreat` | nein | int, Sekunden | `explosionFuse` | Wie lange sie nach einem Wurf auf Abstand bleibt, ehe sie sich wieder umdreht |
| `throwPower` | nein | float | `1.0` | Wie kräftig sie wirft. Verdoppeln verdoppelt ungefähr die Weite |
| `throwArc` | nein | float | `0.35` | Wie steil der Wurfbogen ausfällt. Höher hängt länger, nahe null ist ein flacher Wurf, unter null wirft sie nach unten |
| `explodes` | nein | boolean | `false` | Sprengt sich neben ihrem Ziel in die Luft, wie ein Creeper. Braucht `hostile` |
| `explosionPower` | nein | Zahl | `3.0` | Wie groß die Explosion ist. Ein Creeper ist 3, TNT ist 4 |
| `explosionFuse` | nein | int, Ticks | `30` | Wie lange sie zischt, bevor es losgeht |
| `explosionFire` | nein | boolean | `false` | Lässt Feuer zurück |
| `charges` | nein | boolean | `false` | Stürmt aus der Entfernung auf ihr Ziel los und trifft beim Aufprall mit kräftigem Rückstoß, wie ein Verwüster, und ruht dann vor dem nächsten Anlauf. Braucht `hostile` |
| `pounces` | nein | boolean | `false` | Duckt sich, springt dann im Bogen auf ihr Ziel und schlägt beim Aufsetzen zu, wie ein Fuchs. Braucht `hostile` |
| `sniffs` | nein | int, Blöcke | `0` | Hört Spieler, die sich innerhalb so vieler Blöcke bewegen, durch Wände hindurch, und geht dorthin, wo sie sie gehört hat; ein schleichender oder stehender Spieler wird nicht gehört, und einen, den sie dann sieht, nimmt sie ins Ziel. `0` hört nicht. Braucht `hostile` |
| `fleesWhenHurt` | nein | 0.0 bis 1.0 | `0` | Bricht ab und läuft vor dem davon, mit dem sie kämpft, solange ihre Gesundheit unter diesem Anteil liegt, und kehrt zurück, sobald sie darüber ist. `0` flieht nie. Braucht `hostile` |
| `sleepsByDay` | nein | boolean | `false` | Sucht bei Tag Schatten und steht dort still bis zur Nacht oder bis etwas sie angreift |
| `home` | nein | int, Blöcke | `0` | Bleibt in so vielen Blöcken um die Stelle, an der sie zuerst stand, streift darin umher und geht zurück, wenn sie sich verläuft. `0` streift frei |
| `patrols` | nein | boolean | `false` | Zieht in langen Etappen über das Land, mit anderen ihrer Art, die einem Anführer folgen, wie eine Plünderer-Patrouille. Eine Gruppe, die zusammen erscheint, wählt einen Anführer; die anderen bleiben wenige Blöcke bei ihm, und nimmt der Anführer ein Ziel, nehmen es alle. Ein Gefolgsmann, der seinen Anführer verliert, übernimmt selbst die Führung. Braucht `hostile` |
| `swoops` | nein | boolean | `false` | Kreist über ihrem Ziel und stürzt hindurch, schlägt im Vorbeiflug zu, wie ein Phantom. Die Variante bekommt eine Flughilfe, fliegt also, solange sie jagt, und lässt sich im Leerlauf zu Boden; sie braucht eine Basis, die eine Kreatur ist, etwa einen Papagei, und eine Fledermaus ist keine. Braucht `hostile` |
| `gusts` | nein | boolean | `false` | Holt aus und lässt aus der Entfernung einen Windstoß auf ihr Ziel los, der alles nahe dem Ziel zurück und nach oben wirft, wie die Windkugel einer Brise. Braucht `hostile` |
| `gustPower` | nein | float | `1.5` | Wie hart ein Windstoß wirft. Ein Treffer eines Mobs ist 0.4, eine starke Rückstoß-Verzauberung etwa 1 |
| `threatLeast` | nein | int | `0` | Die niedrigste Bedrohungsstufe, in der ein Spieler oder anderer Träger im Umkreis von 128 Blöcken stehen muss, bevor die Variante natürlich spawnt. `0` spawnt wie gewohnt |
| `threatHostile` | nein | int | `0` | Die niedrigste Bedrohungsstufe, in der ein Spieler stehen muss, bevor die Variante von sich aus auf ihn losgeht. Darunter ist die Variante diesem Spieler gegenüber friedlich, wehrt sich aber weiterhin, wenn sie getroffen wird. `0` greift wie gewohnt an |
| `equipment` | nein | Objekt | keines | `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`, jeweils ein Itemname |
| `spawns` | nein | Liste von Objekten | keine | `creatureType`, `weight`, `min` und `max`, dieselbe Form, die ein Biom nutzt |
| `biomes` | nein | Liste von Biomnamen | jedes Biom | Wo diese Spawns hinzugefügt werden |
| `biomeTypes` | nein | Liste von Biomtypen | keine | Dasselbe, nach Typwort |
| `trackingRange` | nein | int | `80` | Aus welcher Entfernung der Client von ihr erfährt |
| `trackVelocity` | nein | boolean | `true` | Schickt neben der Position auch die Geschwindigkeit. Aus spart Traffic bei Dingen, die sich kaum bewegen |
| `trackingFrequency` | nein | int | `3` | Wie oft, in Ticks |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Variante bleibt weg, wenn nicht alle da sind |
| `tasks` | nein | Liste | keine | Jede Aufgabe, die das Spiel kennt, der Variante beim Namen und mit einer Priorität deiner Wahl hinzugefügt oder aus dem entfernt, was ihre Basis mitbringt. Die Liste unten |

**Eine Kreatur mit Haltbarkeitsdatum.** `despawnAfter` zählt in Sekunden ab dem Moment, in dem eine Kreatur zum ersten Mal in die Welt kommt, und nimmt sie still fort, wenn die Zeit um ist: kein Tod, kein Drop, kein Geräusch, genau als wäre sie weggewandert und weggeräumt worden. Die Uhr wird in die Kreatur selbst geschrieben, sie läuft also über Speichern und Laden hinweg weiter, statt jedes Mal neu zu beginnen, wenn ein Chunk zurückkommt.

Sie ist eine Sache für sich und kein Anstupsen der Regeln, über die `despawns` und `persistent` bestimmen. Die beiden entscheiden, ob das Spiel eine Kreatur wegräumen darf, weil sie weit von allen entfernt ist; diese hier ist ein Versprechen, dass sie zu einer festen Zeit geht, ganz gleich was sonst gilt. Eine Kreatur darf `persistent` sein und trotzdem ein Haltbarkeitsdatum haben, genau das willst du für etwas, das für einen Kampf oder ein Ereignis gerufen wurde und es nicht überdauern soll.

Die Uhr läuft nach der Weltzeit, sie pausiert also, wenn niemand spielt, und zählt die Minuten nicht mit, die ein Chunk entladen verbracht hat.

`scale` ändert Modell und Hitbox auf beiden Seiten, du triffst also das, was du siehst. Eine Kreatur, die ihre Größe selbst ändert, ein Tier, das heranwächst, oder ein Zombie, der ein Kind ist, wird um die Größe herum skaliert, die sie sich gewählt hat, damit sich beides nicht in die Quere kommt. `angryScale` lässt sie anschwellen, solange sie ein Ziel hat, und bringt sie auf `scale` zurück, sobald sie es verliert. Da dem Client nie mitgeteilt wird, was eine Kreatur jagt, trägt das Sprint-Flag diese Nachricht hinüber; es wird bei einer Variante gesetzt, die `angryScale` nutzt, und sonst bei keiner, ein Mod, der bei deinen Varianten das Sprinten ausliest, sieht es also wechseln. In eine niedrige Decke hineinzuwachsen ist möglich, genauso wie bei einem wachsenden Schleim, halte den Unterschied also im Rahmen.

Eine Variante droppt das, was die kopierte Entity droppt, weil die Beutetabelle im Code dieser Entity festgeschrieben ist und nicht über den Namen nachgeschlagen wird. `lootTable` zeigt auf eine eigene Tabelle, die du dann wie jede andere unter `loot_tables/entities/<name>.json` mitlieferst.

Eine `texture` wird anstelle der Textur eingebunden, die die Entity sonst nutzen würde, egal welchen Renderer sie erbt, sie funktioniert also bei Mod-Entities genauso wie bei Vanilla-Entities. Sie muss zu dem Modell passen, auf das sie gezeichnet wird, denn das Modell ist das der Basis-Entity: ein Skin, keine neue Form. Layer behalten ihre eigenen Texturen, Rüstung sieht auf einem umgeskinnten Zombie also weiter wie Rüstung aus.

Rüstung wird überhaupt nur auf einer Entity gezeichnet, deren Renderer einen Rüstungs-Layer hat, und das heißt: die humanoiden Mobs und die Dorfbewohner. Eine Variante einer Kuh oder einer Spinne kann Rüstung tragen und bekommt auch deren Schutz, nur zeichnet sie niemand, `armor` unter `attributes` ist deshalb meist der sauberere Weg, so eine Kreatur zäh zu machen. `hideArmor` ist für den anderen Fall: ein Humanoider, der die Rüstung in seinen Slots behalten soll, für den Schutz oder für einen Mod, der sie ausliest, ohne dass man sie sieht.

`hostile` nimmt der Kreatur auch das Verhalten weg, das sie hat weglaufen lassen: Ein Tier, das Spielern ausgewichen ist oder bei Verletzung in Panik geriet, tut beides nicht mehr, sobald es feindselig ist, sonst würde es vor dem fliehen, was es eigentlich angreifen soll. Es braucht eine Entity, die auf dem Boden läuft, weil es dasselbe Angriffsverhalten nutzt, das Vanilla seinen eigenen Mobs gibt. Eine fliegende oder schwimmende Basis wird protokolliert und in Ruhe gelassen. `passive` greift weiter, erreicht aber nur Verhalten, das so gebaut ist, wie Vanilla es baut: Einem Mod, dessen Feindseligkeit in seinem eigenen Tick- oder Schadenscode steht, kann ein Pack sie nicht ausreden.

Eine Variante ist eine eigene Klasse, eine Welt, die eine enthält, hängt also von dem Pack ab, das sie gemacht hat, genau wie von einem Mod. Nimm die Datei weg, und die Kreaturen in dieser Welt gehen mit.

**Werfen statt Stürmen.** `explodes` schickt eine Kreatur hinein, um sich selbst zu sprengen. `throws` ist das andere Temperament: Sie hält Abstand, wirft, was sie in der Haupthand hält, auf ihr Ziel, und wenn das gerade TNT ist, zündet sie es an, wirft es und zieht sich zurück, während es brennt.

```json
{
  "hostile": true,
  "throws": true,
  "explosionFuse": 50,
  "equipment": { "mainhand": "minecraft:tnt" }
}
```

Der Wurf leert die Hand, denn sie hat das Ding ja geworfen. Danach bleibt sie `throwRetreat` lang auf Abstand, zieht nach `throwReload` das nächste und dreht sich wieder zu ihrem Ziel: ein Kreislauf aus Werfen, Zurückweichen, Nachladen, Herangehen. Gibst du ihr ein `throwAmmo`, endet dieser Kreislauf, wenn die Zahl aufgebraucht ist, die Hand bleibt dann leer und ihr gewöhnlicher Angriff übernimmt. Lässt du `throwAmmo` weg, geht ihr nie etwas aus.

Die Zahl wird in die Kreatur geschrieben, sie füllt sich also nicht wieder auf, weil ein Chunk entladen und neu geladen wurde. Alles, was kein TNT ist, fliegt als Item und landet, ein Pionier, der Steine oder verrottetes Fleisch schleudert, ist so leicht gemacht wie einer mit Sprengstoff.

`explosionFuse` bleibt die Lunte am geworfenen TNT und springt für jeden der beiden Zeitwerte ein, den du weglässt, eine vor diesen Schlüsseln geschriebene Variante verhält sich also genau wie zuvor.

Wie der Wurf selbst fliegt, bestimmen `throwPower` und `throwArc`. Das erste ist ein Faktor auf den Schwung, und da der Schwung ohnehin mit der Entfernung wächst, verlängert ein höherer Wert die Weite, ohne zu ändern, wie lange der Wurf in der Luft hängt. Das zweite ist die Höhe und ändert die Form: hoch, und er segelt im Bogen über eine Mauer und lässt sich Zeit; nahe null, und er wird flach geschleudert und landet fast sofort; unter null, und er wird auf etwas darunter hinabgeworfen. Beide lassen die Lunte in Ruhe, eine im Bogen geworfene und eine flache Ladung gehen also gleich viele Sekunden nach dem Verlassen der Hand hoch, und das entscheidet, ob eine über den Köpfen zerplatzt oder erst landet und wartet. Wie weit sie wirft, sagt ihre `followRange`, und näher als drei Blöcke geht sie wie gewohnt zum Angriff über, sie ist also auf Distanz gefährlich und im Nahkampf gewöhnlich.

**Jede Aufgabe, die das Spiel kennt.** Die Schlüssel oben sind RDPLs eigene Verhaltensweisen. `tasks` greift darüber hinaus auf jede Aufgabe zu, die Vanilla selbst benutzt, auf jeder Basis: ein Eintrag ist ein Objekt, das die `task` und ihre `priority` nennt, dazu was diese Aufgabe sonst liest; ein Name hinter einem `-` entfernt jede Aufgabe dieser Art, die die Basis mitbrachte. Prioritäten laufen bei 0 zuerst, und Vanilla hält seine eigenen zwischen 1 und 8, also gewinnt eine Aufgabe bei 0 gegen alles, was die Basis tut, und eine bei 9 läuft nur, wenn sonst nichts will.

```json
{
  "entity": "minecraft:cow",
  "tasks": [
    "-wander",
    { "task": "avoidEntity", "priority": 3, "entity": "minecraft:player", "distance": 8, "speed": 1.0, "nearSpeed": 1.4 },
    { "task": "watchClosest", "priority": 6, "entity": "minecraft:wolf", "distance": 12 },
    { "task": "wanderAvoidWater", "priority": 7, "speed": 0.8 }
  ]
}
```

Die Liste wird angewendet, nachdem `hostile`, `passive` und die Verhaltensweisen oben ihre Arbeit getan haben, sie hat also das letzte Wort. Aufgaben, die den Körper bewegen, sperren einander aus: eine läuft nur, wenn nichts vor ihr in der Priorität die Kreatur bewegt, und der Angriff, den ein Monster mitbringt, sitzt auf 2, also braucht ein Sprung oder eine Flucht auf einem Zombie Priorität 1, sonst kommt sie nie dran; Spinne und Wolf halten ihren Sprung aus demselben Grund vor ihrem Angriff. Eine Aufgabe, die die Basis schon ausführt, wird ein zweites Mal hinzugefügt statt ersetzt; entferne die alte zuerst. Manche Aufgaben ergeben nur auf einer Basis Sinn, die hat, was sie steuern: ein Bogenkampf braucht eine Basis, die schießt, Sitzen braucht eine zähmbare Basis, und Handeln braucht einen Dorfbewohner. Verlangst du eine auf einer Basis, die sie nicht tragen kann, sagt das Log, welche Basis sie braucht, und die Variante kommt ohne sie aus. Ein Dorfbewohner läuft in dieser Version über das Gehirn des Spiels statt über Aufgaben, die Dorfbewohner-Zeilen unten erreichen also nur das, was das Gehirn den Aufgaben überlässt.

| Schlüssel | Typ | Standard | Was er tut |
| --- | --- | --- | --- |
| `priority` | int | Pflicht | Wo sie zwischen den Aufgaben der Basis sitzt. Niedriger läuft zuerst |
| `speed` | Zahl | das Übliche der Aufgabe | Wie schnell sie sich bewegt, solange die Aufgabe läuft, als Faktor auf ihre Gehgeschwindigkeit |
| `nearSpeed` | Zahl | `1.2` | `avoidEntity`: der Faktor, sobald das Gemiedene nah ist |
| `distance` | Zahl, Blöcke | das Übliche der Aufgabe | Wie weit sie schaut, folgt, schießt oder Abstand hält |
| `near` | Zahl, Blöcke | das Übliche der Aufgabe | `follow`, `followOwner`, `followOwnerFlying`: wie nah sie herankommt, bevor sie stehen bleibt |
| `chance` | Zahl | das Übliche der Aufgabe | `wander`: ein Wurf in so vielen Ticks; `wanderAvoidWater`: die Chance, 0 bis 1, die Deckung zu verlassen; `watchClosest`, `watchClosest2`: die Chance, 0 bis 1, in jedem Tick hinzusehen |
| `leap` | Zahl | `0.4` | `leapAtTarget`: wie hoch der Sprung geht |
| `cooldown` | int, Ticks | `20` | `attackRanged`, `attackRangedBow`: Ticks zwischen zwei Schüssen |
| `entity` | Entity-Name | keiner | Welche Entity die Aufgabe sucht, meidet, beobachtet oder mit der sie sich paart. `minecraft:player` wird verstanden |
| `items` | Liste von Item-Namen | keine | `tempt`: was ein Spieler hinhält |
| `sight` | boolean | `true` | `nearestAttackableTarget`, `targetNonTamed`: nur was sie sehen kann |
| `nearby` | boolean | `false` | `nearestAttackableTarget`: nur was in ihrer eigenen Folgereichweite ist |
| `help` | boolean | `false` | `hurtByTarget`: Artgenossen in der Nähe mischen mit |
| `memory` | boolean | `false` | `attackMelee`, `zombieAttack`: bleibt an einem Ziel, das sie aus den Augen verloren hat |
| `close` | boolean | `false` | `openDoor`: schließt die Tür hinter sich |
| `nocturnal` | boolean | `false` | `moveThroughVillage`: nur nachts |
| `scared` | boolean | `false` | `tempt`: ein Spieler, der sich zu schnell bewegt, bricht den Bann |

Die Spalte `Liste` sagt, wo die Aufgabe lebt. `tasks` ist, was die Kreatur tut; `targets` ist, wie sie sich aussucht, wen sie verfolgt, und eine Zielaufgabe ohne passenden Angriff tut für sich allein nichts.

| Aufgabe | Braucht | Liste | Liest | Was sie tut |
| --- | --- | --- | --- | --- |
| `attackMelee` | eine gehende Kreatur | `tasks` | `speed`, `memory` | Geht auf ihr Ziel zu und schlägt es |
| `attackRanged` | eine Basis, die schießt | `tasks` | `speed`, `cooldown`, `distance` | Hält Abstand und schießt, was ihre Basis schießt |
| `attackRangedBow` | ein Monster, das schießt | `tasks` | `speed`, `cooldown`, `distance` | Der Bogenkampf des Skeletts: seitwärts ausweichen, spannen und lösen |
| `avoidEntity` | eine gehende Kreatur | `tasks` | `entity`, `distance`, `speed`, `nearSpeed` | Läuft vor der genannten Entity weg, sobald sie innerhalb von `distance` kommt |
| `beg` | einen Wolf | `tasks` | `distance` | Bettelt bei einem Spieler, der Futter hinhält |
| `breakDoor` | jede Basis | `tasks` |  | Bricht die Holztüren auf ihrem Weg, auf schwer |
| `creeperSwell` | einen Creeper | `tasks` |  | Zischt und geht neben ihrem Ziel hoch |
| `defendVillage` | einen Eisengolem | `targets` |  | Verfolgt, wer einen Dorfbewohner angegriffen hat |
| `eatGrass` | jede Basis | `tasks` |  | Frisst Gras, wie ein Schaf |
| `findEntityNearest` | jede Basis | `targets` | `entity` | Nimmt die nächste der genannten Entity ins Visier, wie ein Schleim oder Ghast |
| `findEntityNearestPlayer` | jede Basis | `targets` |  | Nimmt den nächsten erreichbaren Spieler ins Visier |
| `fleeSun` | eine gehende Kreatur | `tasks` | `speed` | Sucht Schatten, wenn die Sonne auf sie scheint |
| `follow` | jede Basis | `tasks` | `speed`, `near`, `distance` | Folgt Artgenossen |
| `followGolem` | einen Dorfbewohner | `tasks` |  | Folgt einem Eisengolem, der eine Mohnblume hinhält |
| `followOwner` | eine zähmbare Basis | `tasks` | `speed`, `near`, `distance` | Folgt ihrem Besitzer und teleportiert sich hinterher, wenn sie weit zurückfällt |
| `followOwnerFlying` | eine zähmbare Basis | `tasks` | `speed`, `near`, `distance` | Dasselbe, fliegend |
| `followParent` | ein Tier | `tasks` | `speed` | Ein Kind bleibt nah bei einem Erwachsenen seiner Art |
| `harvestFarmland` | einen Dorfbewohner | `tasks` | `speed` | Erntet reife Pflanzen und sät nach |
| `hurtByTarget` | eine gehende Kreatur | `targets` | `help` | Wehrt sich gegen das, was sie getroffen hat |
| `landOnOwnersShoulder` | einen Papagei | `tasks` |  | Reitet auf der Schulter ihres Besitzers |
| `leapAtTarget` | jede Basis | `tasks` | `leap` | Springt ihr Ziel aus der Nähe an |
| `llamaFollowCaravan` | ein Lama | `tasks` | `speed` | Reiht sich hinter einem geführten Lama ein |
| `lookAtTradePlayer` | einen Dorfbewohner | `tasks` |  | Wendet sich dem Spieler zu, mit dem sie handelt |
| `lookAtVillager` | einen Eisengolem | `tasks` |  | Sieht Dorfbewohner an |
| `lookIdle` | jede Basis | `tasks` |  | Sieht sich ab und zu um |
| `mate` | ein Tier | `tasks` | `speed`, `entity` | Paart sich, wenn verliebt, mit ihresgleichen oder der genannten `entity` |
| `moveIndoors` | eine gehende Kreatur | `tasks` |  | Geht bei Einbruch der Nacht in ein Dorfhaus |
| `moveThroughVillage` | eine gehende Kreatur | `tasks` | `speed`, `nocturnal` | Geht die Dorfwege von Tür zu Tür |
| `moveTowardsRestriction` | eine gehende Kreatur | `tasks` | `speed` | Geht zurück zu ihrem Heimatpunkt, wenn sie sich entfernt |
| `moveTowardsTarget` | eine gehende Kreatur | `tasks` | `speed`, `distance` | Rückt an ein weit entferntes Ziel heran |
| `nearestAttackableTarget` | eine gehende Kreatur | `targets` | `entity`, `sight`, `nearby` | Nimmt die nächste der genannten Entity ins Visier |
| `ocelotAttack` | jede Basis | `tasks` |  | Das Anschleichen und Anspringen der Katze |
| `ocelotSit` | einen Ozelot | `tasks` | `speed` | Setzt sich auf Truhen, Betten und brennende Öfen |
| `openDoor` | jede Basis | `tasks` | `close` | Öffnet die Holztüren, durch die sie geht |
| `ownerHurtByTarget` | eine zähmbare Basis | `targets` |  | Verfolgt, was ihren Besitzer getroffen hat |
| `ownerHurtTarget` | eine zähmbare Basis | `targets` |  | Verfolgt, was ihr Besitzer getroffen hat |
| `panic` | eine gehende Kreatur | `tasks` | `speed` | Rennt, wenn sie verletzt ist oder brennt |
| `play` | einen Dorfbewohner | `tasks` | `speed` | Kinder spielen miteinander Fangen |
| `restrictOpenDoor` | eine gehende Kreatur | `tasks` |  | Bleibt nachts hinter den Dorftüren |
| `restrictSun` | eine gehende Kreatur | `tasks` |  | Bleibt tagsüber im Schatten |
| `runAroundLikeCrazy` | ein Pferd, einen Esel, ein Maultier oder ein Lama | `tasks` | `speed` | Wirft einen Reiter ab, dem sie noch nicht vertraut |
| `sit` | eine zähmbare Basis | `tasks` |  | Sitzt, wenn es ihr gesagt wird |
| `skeletonRiders` | ein Skelettpferd | `tasks` |  | Ruft Skelettreiter, wenn ein Spieler nahe kommt, das Fallenpferd |
| `swimming` | jede Basis | `tasks` |  | Hält den Kopf über Wasser |
| `targetNonTamed` | eine zähmbare Basis | `targets` | `entity`, `sight` | Nimmt die genannte Entity ins Visier, solange sie noch nicht gezähmt ist |
| `tempt` | eine gehende Kreatur | `tasks` | `items`, `speed`, `scared` | Folgt einem Spieler, der eines der `items` hinhält |
| `tradePlayer` | einen Dorfbewohner | `tasks` |  | Steht still, solange gehandelt wird |
| `villagerInteract` | einen Dorfbewohner | `tasks` |  | Plaudert mit anderen Dorfbewohnern |
| `villagerMate` | einen Dorfbewohner | `tasks` |  | Vermehrt sich, wenn das Dorf Platz hat |
| `wander` | eine gehende Kreatur | `tasks` | `speed`, `chance` | Streift umher |
| `wanderAvoidWater` | eine gehende Kreatur | `tasks` | `speed`, `chance` | Streift umher und meidet das Wasser |
| `wanderAvoidWaterFlying` | eine gehende Kreatur | `tasks` | `speed` | Streift durch die Luft und setzt sich in Bäume |
| `watchClosest` | jede Basis | `tasks` | `entity`, `distance`, `chance` | Sieht die nächste der genannten Entity an, den Spieler, wenn keine genannt ist |
| `watchClosest2` | jede Basis | `tasks` | `entity`, `distance`, `chance` | Dasselbe, auch während eine andere Aufgabe läuft |
| `zombieAttack` | einen Zombie | `tasks` | `speed`, `memory` | Der Angriff des Zombies mit erhobenen Armen |

**Ein Ei oder Spawner, der Verschiedenes liefert.** Eine Variante ist eine eigene Klasse, für sich allein erscheint sie also immer genau als das, was sie sagt. `becomes` bricht das auf: eine Liste von Varianten, zu denen diese beim Erscheinen werden kann, jede mit einem Gewicht, für jede Kreatur einzeln entschieden.

```json
{
  "becomes": [
    { "variant": "mypack:walker", "weight": 95 },
    { "variant": "mypack:little_walker", "weight": 5 }
  ]
}
```

Sich selbst zu nennen ist der Weg, so zu bleiben, wie man ist, und die Gewichte sind die Chancen. Setz das auf `mypack:walker`, und ein Ei, ein Spawner und ein Spawn-Eintrag liefern meist Walker mit gelegentlich einem kleinen, so wie ein Zombie-Ei ab und zu ein Baby liefert. Es geschieht, während die Kreatur in die Welt kommt, gilt also für Eier, Spawner, `/summon` und natürliches Spawnen gleichermaßen, und was ankommt, ist eine echte Kreatur der gewählten Variante mit allem, was diese Variante sagt. Eine so erreichte Variante wandelt sich nicht noch einmal, zwei Varianten dürfen sich also gegenseitig nennen, ohne sich im Kreis zu drehen.

**Wo `baby` hineinpasst.** Das Spiel hat keinen eigenen Baby-Zombie: Es gibt einen Zombie, der beim Erscheinen auswürfelt, ob er ein Kind ist. `baby` sagt, wie oft, `"baby": 0.05` ist also die Vanilla-Gewohnheit und `"baby": true` heißt immer. Beide sind zwei Wege zur selben Sache, und welchen du nimmst, hängt vom Unterschied ab, den du willst: `baby` allein gibt eine Variante, die manchmal jung ist, `becomes` gibt mehrere Varianten, die sich in allem unterscheiden dürfen, und beides zusammen ist auch in Ordnung.

## Dorfgrundstücke

`<namespace>/villages/*.json`

Der Pfad der Datei ist der Name des Grundstücks, den `villagePieces` dann nennen kann, um es zu behalten oder wegzulassen.

Eine Datei hier fügt ein Stück hinzu, das die Städte und Dörfer des Packs bauen können. Zwei Sorten, ausgewählt mit `type`.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht. Ein Schlüssel, der für einen Typ vermerkt ist, wird nur von diesem Typ gelesen.

```json
{
  "type": "farm",
  "weight": 3,
  "leastCount": 1,
  "mostCount": 4,
  "width": 7,
  "height": 4,
  "depth": 9,
  "apron": 2,
  "crops": ["simplecorn:corn", "minecraft:wheat"],
  "edge": "minecraft:oak_log",
  "soil": "minecraft:farmland",
  "water": true,
  "rowWidth": 2,
  "structure": "mypack:blacksmith_shed",
  "integrity": 100,
  "lootTable": "minecraft:chests/village/village_toolsmith",
  "villagers": 2,
  "villagerEntity": "mypack:jeweller",
  "villagerX": 1,
  "villagerY": 1,
  "villagerZ": 1,
  "ground": "minecraft:dirt",
  "requires": ["mypack"]
}
```

Ein `farm` ist ein Feld, beschrieben statt programmiert: ein Grundstück in der Größe, die du willst, mit einem Block eingefasst, gefüllt mit Reihen aus Erde, getrennt durch Wasserrinnen, bepflanzt mit einer Feldfrucht, die pro Block aus deiner Liste gezogen wird.

```json
{
  "type": "farm",
  "weight": 3,
  "width": 7,
  "depth": 9,
  "crops": ["simplecorn:corn"],
  "edge": "minecraft:oak_log",
  "water": true,
  "rowWidth": 2
}
```

Ein `template` setzt stattdessen eine deiner `.nbt`-Strukturen, gedreht zur Straße hin.

```json
{
  "type": "template",
  "weight": 2,
  "width": 9,
  "height": 6,
  "depth": 9,
  "structure": "mypack:blacksmith_shed"
}
```

Ein `template`, dessen `structure` eine deiner [Strukturkarten](#strukturkarten) nennt, setzt die ganze Komposition als Grundstück. Die Größe des Grundstücks kommt dann aus der Karte, ihre Grundfläche und die gestapelten Ebenen mal der Zelle, `width`, `height`, `depth` und `integrity` werden also nicht gelesen. Ebenen vor dem `ground` der Karte graben sich als Keller nach unten, und gewichtete Palettenzellen losen weiter pro Gebäude, zwei Türme aus derselben Karte können sich also unterscheiden.

```json
{
  "type": "template",
  "weight": 2,
  "structure": "mypack:castle",
  "villagers": 4
}
```

| Schlüssel | Genutzt von | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `type` | allen | `farm` oder `template` | `farm` | Welche Sorte Grundstück |
| `weight` | allen | int | `3` | Wie oft dieses Grundstück gegenüber den anderen des Packs gezogen wird |
| `leastCount` | allen | int | `1` | Wenigstens so viele pro Dorf, bevor die Dorfgröße dazukommt |
| `mostCount` | allen | int | `4` | Höchstens so viele pro Dorf, bevor die Dorfgröße dazukommt |
| `width` | allen | int | `7` | Größe quer zur Straße |
| `height` | allen | int | `4` | Höhe, die über dem Boden freigeräumt wird |
| `depth` | allen | int | `9` | Größe von der Straße weg |
| `apron` | allen | int | `2` | Wie weit der Boden unter dem Grundstück von der Straßenhöhe abweichen darf, bevor es abgelehnt oder an seiner Straße entlang verschoben wird: so viele Blöcke Auffüllung darunter oder Einschnitt in eine Erhebung darüber, und höchstens so viel zwischen seiner höchsten und tiefsten Ecke. Ein breites Grundstück im Hügelland braucht mehr. Hoch gesetzt terrassiert sich das Grundstück geradewegs in einen Hang, was an der falschen Stelle einen Berg auffrisst |
| `crops` | farm | Liste von Blocknamen | Weizen | Eine pro Block gepflanzt, in zufälliger Wachstumsstufe |
| `edge` | farm | Blockname | `minecraft:oak_log` | Der Rahmen um das Grundstück |
| `soil` | farm | Blockname | `minecraft:farmland` | Woraus die Reihen bestehen |
| `water` | farm | boolean | `true` | Eine Wasserrinne zwischen die Reihen legen |
| `rowWidth` | farm | int | `2` | Wie breit jede Erdreihe ist |
| `structure` | template | `namespace:name` | keine | Die Vorlage, die gesetzt wird, oder eine deiner Strukturkarten, die dann die Größe des Grundstücks bestimmt |
| `integrity` | template | 1 bis 100 | `100` | Prozentsatz der Blöcke der Vorlage, die erscheinen |
| `lootTable` | template | `namespace:pfad` | keine | Die Beutetabelle, aus der jede Truhe in der gesetzten Vorlage beim ersten Öffnen gefüllt wird. Ein Grundstück, das eine Strukturkarte nennt, bleibt unberührt |
| `villagers` | allen | int | `0` | Wie viele Leute das Grundstück spawnt |
| `villagerEntity` | allen | `namespace:name` | ein Dorfbewohner | Wer dort wohnt, etwa eine eigene Entity-Variante |
| `villagerX` | allen | int | `1` | Wo sie erscheinen, quer über das Grundstück |
| `villagerY` | allen | int | `1` | Wo sie erscheinen, über dem Boden |
| `villagerZ` | allen | int | `1` | Wo sie erscheinen, in das Grundstück hinein |
| `ground` | allen | Blockname | `minecraft:dirt` | Was am Hang darunter aufgefüllt wird |
| `requires` | allen | Liste von Mod-IDs oder Pack-Namespaces | keine | Das Grundstück bleibt weg, wenn nicht alle da sind |

Grundstücke sind das, was die packeigenen Städte entlang ihrer Straßen bauen; die Dörfer des Spiels selbst bleiben unverändert. `weight` entscheidet, welches deiner Grundstücke gezogen wird, sobald eine Straße nach einem fragt, und `villagePieces` in den `villages`-Einstellungen nennt die Grundstücke, die eine Vorlage behält, als `mypack:smithy`. Wie die Straßen selbst gelegt, ausgestattet, überbrückt, untertunnelt und mit Gleisen versehen werden, sind die `village*`-Einstellungen unter [Was jede Gruppe macht](#was-jede-gruppe-macht).

## Biome

`<namespace>/biomes/*.json`

Der Pfad der Datei ist der Registry-Name des Bioms, `mypack/biomes/ruby_forest.json` registriert also `mypack:ruby_forest`. `name` ist nur das, was der Spieler sieht, und `biome.mypack.ruby_forest` in den Sprachdateien sagt es in jeder Sprache.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht.

```json
{
  "name": "Ruby Forest",
  "types": ["forest", "dense", "wet"],
  "temperature": 0.7,
  "rainfall": 0.8,
  "rain": true,
  "snow": false,
  "topBlock": "mypack:ruby_grass",
  "fillerBlock": "minecraft:dirt",
  "stoneBlock": "mypack:ruby_stone",
  "baseBiome": "minecraft:forest",
  "waterColor": "8040A0",
  "grassColor": "6BA33C",
  "foliageColor": "4E8B2A",
  "decoration": {
    "trees": 10,
    "extratreechance": 10,
    "flowers": 4,
    "grass": 5,
    "deadbush": 0,
    "mushrooms": 1,
    "bigmushrooms": 0,
    "reeds": 10,
    "cacti": 0,
    "sand": 3,
    "gravel": 1,
    "clay": 1,
    "waterlily": 0,
    "falls": 1
  },
  "spawns": [
    { "entity": "minecraft:sheep", "type": "creature", "weight": 12, "min": 2, "max": 4 }
  ],
  "keepDefaultSpawns": false,
  "spawnChance": 0.1,
  "spawnRates": { "surfaceDay": 0.0, "surfaceNight": 0.5, "undergroundDay": 2.0, "undergroundNight": 2.0 },
  "placement": {
    "climate": "warm",
    "weight": 8,
    "villages": true,
    "strongholds": false,
    "playerSpawn": true
  },
  "villageType": "oak",
  "minHeight": 100,
  "maxHeight": 156,
  "replaces": ["minecraft:plains", "minecraft:forest"],
  "requires": ["mypack"]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `name` | nein | string | der Dateiname | Name, den der Spieler sieht |
| `temperature` | nein | float | `0.5` | Unter 0.15 schneit es, über 1.0 ist es wüstenheiß |
| `rainfall` | nein | float, 0 bis 1 | `0.5` | Wie feucht es ist |
| `rain` | nein | boolean | `true` | Ob es überhaupt Wetter gibt |
| `snow` | nein | boolean | `false` | Ob Regen als Schnee fällt |
| `topBlock` | nein | Blockname | Gras | Der Oberflächenblock |
| `fillerBlock` | nein | Blockname | Erde | Direkt unter der Oberfläche |
| `stoneBlock` | nein | Blockname | Stein | Die Masse des Untergrunds |
| `types` | nein | Liste von Biomtypen | keine | Schreibt das Biom in die Tags, für die diese Typwörter stehen, etwa `forest`, `cold`, `wet` oder `nether`, damit andere Mods es finden |
| `waterColor` | nein | Hex-Farbe | `FFFFFF` | Wasserfärbung |
| `grassColor` | nein | Hex-Farbe | aus dem Klima | Grasfärbung, anstelle der Farbe, die Temperatur und Niederschlag ergäben |
| `foliageColor` | nein | Hex-Farbe | aus dem Klima | Laubfärbung, auf dieselbe Weise |
| `baseBiome` | nein | Biomname | keiner | Ein vorhandenes Biom, von dem Einstellungen kopiert werden |
| `decoration` | nein | Objekt | die des Basisbioms | Anzahlen pro Chunk, die ändern, was das Basisbiom ohnehin setzt. Gelesen werden `trees`, `flowers`, `grass`, `deadbush`, `mushrooms`, `bigmushrooms`, `reeds`, `cacti`, `sand`, `gravel`, `clay` und `waterlily`, dazu `falls`, wo über null bedeutet, dass Seen und Quellen generieren, und `extratreechance`, eine prozentuale Chance auf einen Baum mehr. Eine Anzahl für eine Sorte, die das Basisbiom nicht setzt, fügt nichts hinzu; schreib dafür einen Worldgen-Eintrag. Jeder andere Name wird protokolliert und ignoriert |
| `spawns` | nein | Liste von Objekten | Vanilla-Liste | Siehe unten |
| `keepDefaultSpawns` | nein | boolean | `false` | Vanillas Liste neben deiner behalten |
| `spawnChance` | nein | float, unter 1 | `0.1` | Wie wahrscheinlich beim ersten Erzeugen des Landes eine weitere Herde gesetzt wird. Das Spiel würfelt weiter, solange es Erfolg hat, `1` hört also nie auf und füllt die Welt, bis kein Platz mehr ist. Alles ab 0.99 wird abgelehnt und durch 0.99 ersetzt |
| `spawnRates` | nein | Objekt aus `surfaceDay`, `surfaceNight`, `undergroundDay`, `undergroundNight` zu einem Faktor | keines | Wie oft feindliche Mobs hier spawnen, anstelle der globalen Einstellungen. Siehe unten |
| `placement` | nein | Objekt | keines | Wo es generiert. Siehe unten |
| `villageType` | nein | `oak`, `sandstone`, `acacia` oder `spruce` | keiner | Woraus ein Dorf hier gebaut wird: das Ebenen-, Wüsten-, Savannen- oder Taigadorf. Leer baut das Ebenendorf, wie auch ohne den Schlüssel |
| `minHeight` | nein | int | keiner | Unterste y, ab der dieses Biom als 3D-Biom übernimmt. Wird eine der beiden Höhen gesetzt, wird das Biom zu einem Band: außerhalb behält die Säule ihr eigenes Biom, innerhalb meldet jede 4 mal 4 mal 4 große Zelle der Welt dieses |
| `maxHeight` | nein | int | keiner | Oberste y dieses Bandes |
| `replaces` | nein | Liste von Biomnamen | jedes Biom | Beschränkt das Band auf Säulen, deren eigenes Biom hier genannt ist, ein Alpenband kann also über Bergen liegen und sonst nirgends |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

Ein Spawn-Eintrag nimmt `entity` (Pflicht), `type` (`creature`, einer von `monster`, `creature`, `ambient` oder `water`), `weight` (`10`), `min` (`1`) und `max` (`min`).

Bei `spawnRates` geht es ausschließlich um feindliche Mobs, um sonst nichts. Es nimmt vier Schlüssel und keine anderen: `surfaceDay` und `surfaceNight` für Orte, an denen der Himmel zu sehen ist, `undergroundDay` und `undergroundNight` für Orte, an denen er es nicht ist. Jeder ist ein Faktor darauf, wie oft ein feindlicher Mob erscheinen darf: `1` ist die gewöhnliche Rate, `0` unterbindet sie ganz, unter 1 weist einen Teil der Versuche ab, und über 1 lässt Versuche durch, die das Spiel sonst abgelehnt hätte, `2` sind also doppelt so viele. Ein weggelassener Schlüssel heißt, dass das Biom nicht entscheidet, und es gilt die globale Einstellung für diese Zeit und diesen Ort. Alles andere, was hier steht, ist kein Schlüssel und wird ignoriert, eine Rate mit dem Namen eines Kreaturtyps tut also gar nichts.

`placement`:

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `climate` | nein | `icy`, `cool`, `medium`, `warm` oder `desert` | keiner | Welchem Klimaband es beitritt, denselben fünf, nach denen die eigenen Biome der Oberwelt verteilt werden. Weggelassen wird das Biom registriert, aber nie gesetzt, es sei denn, die `roles` einer Vorlage oder das `biome` einer Dimension verlangen es |
| `weight` | nein | int | `10` | Wie oft es gegenüber seinen Nachbarn in diesem Band gezogen wird |
| `villages` | nein | boolean | `false` | Dörfer dürfen generieren |
| `strongholds` | nein | boolean | `false` | Festungen dürfen generieren |
| `playerSpawn` | nein | boolean | `false` | Der Weltspawn darf hier liegen |

Ein Biom ist in dieser Version ein Datenpaket-Eintrag, der für dich unter `worldgen/biome/` geschrieben wird, und das Gelände darunter gehört den Noise-Einstellungen und nicht dem Biom, weshalb es kein `baseHeight` und kein `heightVariation` gibt: Die Form des Landes ergibt sich daraus, wo das Klima das Biom hinsetzt, genau wie bei den Biomen des Spiels. Eine `id` aus 1.12.2 wird gelesen und ignoriert.

## Dimensionen

`<namespace>/dimensions/*.json`

Der Pfad der Datei ist die Id der Dimension, `mypack/dimensions/verdant.json` ist also `mypack:verdant`, und genau das nennen ein Portal, ein Tor, eine Spielregeldatei und `/execute in`. Eine numerische Id gibt es in dieser Version nicht, und eine `id` oder ein `suffix` aus 1.12.2 wird gelesen und ignoriert.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht.

```json
{
  "requires": ["mypack"],
  "terrain": {
    "type": "overworld",
    "minHeight": -64,
    "maxHeight": 320,
    "generatorOptions": { "seaLevel": 63, "useLavaOceans": false },
    "structures": false
  },
  "biomes": {
    "source": "single",
    "biome": "mypack:ruby_forest"
  },
  "sky": {
    "hasSkyLight": true,
    "surfaceWorld": true,
    "respawn": true,
    "respawnDimension": "minecraft:overworld",
    "spawning": true,
    "nether": false,
    "beds": true,
    "waterVaporizes": false,
    "cloudHeight": 160,
    "cloudColor": "5B3E6A",
    "groundLevel": 63,
    "movementFactor": 4.0,
    "fogColor": "20102A",
    "showFog": false,
    "skyColor": "3B1E4A",
    "fixedTime": 18000,
    "sunriseColors": true,
    "ambientLight": 0.1,
    "starBrightness": 0.8,
    "renderSky": true,
    "renderClouds": true,
    "renderWeather": true
  },
  "gameRules": { "doMobSpawning": "false" }
}
```

**Oberste Ebene**

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `gameRules` | nein | Objekt | keines | Regeln, die nur hier gelten |
| `portal` | nein | Objekt | keines | Ein Rahmen, der diese Dimension öffnet. Siehe [Eine Dimension über einen Rahmen öffnen](#eine-dimension-über-einen-rahmen-öffnen) |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

**`terrain`**

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `type` | nein | `overworld`, `flat`, `void`, `nether`, `end` | `overworld` | Welcher Generator des Spiels sie baut, mit seinen Noise-Einstellungen kopiert und durch die Schlüssel unten geändert |
| `minHeight` | nein | int, ein Vielfaches von 16 | der des Typs | Der Boden der Dimension. Tiefer als der des Typs macht eine Tiefenwelt unter dem Gelände, siehe [Die Tiefenwelt](#die-tiefenwelt) |
| `maxHeight` | nein | int, ein Vielfaches von 16 | der des Typs | Der Block über ihrer Decke |
| `generatorOptions` | nein | Objekt oder Liste | keine | Für `overworld` und die anderen ein Objekt aus `seaLevel` und `useLavaOceans`. Für `flat` die Schichten von unten nach oben, als `"minecraft:bedrock"`, `"59*minecraft:stone"`, `"3*minecraft:dirt"`, `"minecraft:grass_block"`, was zugleich der Standardboden ist |
| `structures` | nein | boolean | `true` | Ob Vanilla-Strukturen generieren |

**`biomes`**

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `source` | nein | `inherit`, `single` | `inherit` | `inherit` nutzt die eigene Biomkarte der Oberwelt, `single` überall ein einziges Biom |
| `biome` | bei `single` | Biomname | `minecraft:plains` | Welches Biom das ist |

**`sky`**

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `hasSkyLight` | nein | boolean | `true` | Ob Tageslicht sie erreicht |
| `surfaceWorld` | nein | boolean | `true` | Ob Karten und Kompasse sich wie in der Oberwelt verhalten |
| `respawn` | nein | boolean | `true` | Ob Spieler hier respawnen |
| `respawnDimension` | nein | Dimensions-Id | keine | Wo sie stattdessen respawnen |
| `spawning` | nein | boolean | `true` | Ob Mobs spawnen |
| `nether` | nein | boolean | `false` | Wird für Portale und Decken wie der Nether behandelt |
| `beds` | nein | boolean | `true` | Aus explodieren Betten |
| `waterVaporizes` | nein | boolean | `false` | Wasser verdampft |
| `cloudHeight` | nein | int | `128` | Wo die Wolken hängen |
| `cloudColor` | nein | Hex-Farbe | keine | Wolkenfärbung |
| `groundLevel` | nein | int | `63` | Meereshöhe, genutzt für den Horizont, die Spawnsuche und dafür, wo eine Ankunft durch ein Tor oder ein Sturz über der Leere landet |
| `movementFactor` | nein | float | `1.0` | Entfernungsverhältnis zur Oberwelt. Der Nether nutzt 8 |
| `fogColor` | nein | Hex-Farbe | keine | Nebelfärbung |
| `showFog` | nein | boolean | `false` | Dichter Nebel, wie im Nether |
| `skyColor` | nein | Hex-Farbe | keine | Himmelsfärbung |
| `fixedTime` | nein | int, Ticks | keine | Hält die Tageszeit fest |
| `sunriseColors` | nein | boolean | `true` | Ob Sonnenauf- und -untergang eingefärbt werden |
| `ambientLight` | nein | float, 0 bis 1 | `0.0` | Mindestlicht überall |
| `starBrightness` | nein | float, 0 bis 1 | keine | Wie hell die Sterne sind |
| `renderSky` | nein | boolean | `true` | Aus zeichnet weder Himmel noch Sonne, Mond oder Sterne, es bleibt die Nebelfarbe |
| `renderClouds` | nein | boolean | `true` | Aus werden keine Wolken gezeichnet |
| `renderWeather` | nein | boolean | `true` | Aus werden weder Regen noch Schnee gezeichnet |

Farben und die drei Render-Schalter sind alles, was geboten wird. Etwas Eigenes dort oben zu zeichnen, eine bemalte Kuppel, eine eigene Sonne und einen eigenen Mond, braucht weiterhin Java.

Eine Dimension ist in dieser Version ein Datenpaket-Eintrag: Der Dimensionstyp und die Noise-Einstellungen werden für dich unter dem Namespace des Packs geschrieben, ein Vanilla-Client erfährt also beim Beitreten von ihr und reist wie in jede andere dorthin. Die Dimension behält ihren eigenen Speicherordner unter der Welt, benannt nach ihrer Id, und ist geladen, solange jemand darin ist oder ein `forceload` einen Chunk hält.

## Behälter

`<namespace>/blocks/*.json`, `<namespace>/items/*.json`

```json
{
  "type": "container",
  "material": "wood",
  "creativeTab": "mypack:tab",
  "container": {
    "rows": 6,
    "columns": 9,
    "lootTable": "minecraft:chests/simple_dungeon",
    "chestModel": true
  },
  "variants": { "crate": { "hardness": 2.5 } }
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `rows` | int | `3` | Wie viele Reihen von Plätzen, 1 bis 9 |
| `columns` | int | `9` | Wie viele Plätze in einer Reihe, 1 bis 12 |
| `lootTable` | Text | leer | Eine Beutetabelle, die beim ersten Öffnen durch einen Spieler in den Block gewürfelt wird, genau wie sich eine Verliestruhe füllt. Leer lässt ihn leer beginnen |
| `chestModel` | boolean oder Text | `false` | Wird als Truhe mit sich öffnendem Deckel gezeichnet statt als gewöhnlicher Block aus deiner eigenen Textur. `true` nimmt das Vanilla-Truhenbild; ein Texturname wie `mypack:entity/chest/strongbox` nimmt stattdessen dein eigenes Truhenblatt, für den gesetzten Block und für das Item gleichermaßen. Ein Block mit Truhenmodell setzt außerdem `opaque` standardmäßig auf `false`, so wie es eine Vanilla-Truhe ist, damit das Licht am Block nicht abgeschnitten wird und die Truhe nicht dunkel gezeichnet wird |
| `guiTexture` | Text | leer | Dein eigenes Hintergrundbild für den Bildschirm. Leer zeichnet eines aus dem Vanilla-Truhenbildschirm in der Größe, die Reihen und Spalten brauchen |
| `guiWidth` | int | keiner | Wie breit dieses Bild ist, nötig zusammen mit `guiTexture` |
| `guiHeight` | int | keiner | Wie hoch dieses Bild ist, nötig zusammen mit `guiTexture` |
| `curioSlot` | Text | leer | Nur bei einem Item: der Curios-Slot, in dem es getragen werden kann, `back`, `belt`, `body`, `charm`, `head`, `necklace`, `ring` oder jeder Slot, den ein anderer Mod hinzufügt. Ein Rucksack nimmt meist `back`. Wird übergangen, wobei alles andere am Item weiter funktioniert, wenn Curios nicht installiert ist. Der 1.12.2-Schlüssel `bauble` wird als dieser gelesen, seine Baubles-Namen auf den nächstliegenden Curios-Slot abgebildet, und das Log sagt, was daraus wurde |

**Neun Reihen mal zwölf ist die Obergrenze**, das Meiste, was ein Bildschirm tragen kann. Ein Pack, das mehr verlangt, wird darauf gekürzt, mit einer Fehlerzeile, die es sagt. Eine Warnung zur höchsten: ein Bildschirm mit neun Reihen ist 276 Pixel hoch, ein 1080er Bildschirm bei GUI-Skalierung `auto` gibt 270, also werden oben und unten je drei Pixel abgeschnitten; Skalierung 3 zeigt ihn ganz.

**Der Bildschirm wird gezeichnet, nicht mitgeliefert.** Ein Behälter mit höchstens neun Spalten und sechs Reihen nutzt den Truhenbildschirm des Spiels unverändert und sieht damit genau wie eine Truhe dieser Größe aus. Alles Größere wird beim Zeichnen aus demselben Bild zusammengesetzt, die obere Kante, eine Reihe von Feldern so oft wiederholt, wie es passt, und der untere Teil mit dem Inventar des Spielers, so kann ein Pack Größen verlangen, die kein Bildschirm des Spiels abdeckt, ohne ein eigenes Bild mitzuliefern. `guiTexture` setzt das alles außer Kraft, wenn ein Pack sein eigenes Aussehen will; dann müssen `guiWidth` und `guiHeight` die Größe nennen, sonst wird der gezeichnete genommen und eine Fehlerzeile sagt es.

**Was der Block tut.** Er behält seinen Inhalt über Speichern und Neuladen, lässt ihn beim Abbauen fallen, antwortet einem Komparator nach Füllstand und lässt sich wie eine Truhe im Amboss umbenennen. `chestModel` gibt ihm zusätzlich den Öffnungston und die Deckelbewegung der Truhe; ohne das zeichnet der Block aus seiner eigenen Textur wie jeder andere Block, sodass Kiste, Fass oder Schrank alle möglich sind.

**Eine Truhe einfärben.** Das Truhenblatt ist eine gewöhnliche Textur, also kann eine Pixelkarte die Vanilla-Truhe umfärben, ohne dass ein Pixel gezeichnet wird: `extends` darauf, ein `tint` dazu, und diese Karte dann in `chestModel` nennen.

```json
{
  "extends": "minecraft:textures/entity/chest/normal",
  "tint": {
    "from": "#241A12",
    "to": "#D8BC80"
  }
}
```

**Ein Behälter-Item ist ein Beutel**, ein Item vom Typ `container`, das denselben `container`-Block mit `rows` und `columns` trägt; ein Rechtsklick öffnet es, und es behält seinen Inhalt, wenn es den Besitzer wechselt. Gib ihm `curioSlot`, und wo Curios installiert ist, sitzt es in diesem Slot und eine Taste öffnet es, ohne es abzulegen, standardmäßig `V`, in den Steuerungen unter Resource Data Pack Loader neu belegbar. Erneutes Drücken, während ein getragener Behälter offen ist, geht zum nächsten getragenen weiter und läuft dabei um, sodass mehrere zugleich getragene alle erreichbar sind. Die Taste erscheint nur, wenn Curios da ist, und alles andere am Item, der Rechtsklick und sein Inventar, funktioniert so oder so. Die Tragbarkeit des Beutels wird für dich in den Curios-Tag dieses Slots geschrieben.

**Die Beutetabelle füllt beim ersten Öffnen**, nicht beim Setzen, und genau das macht sie in einem Bauwerk nützlich: wer zuerst öffnet, bekommt den Wurf. Dieselbe Tabelle kann `lootTable` an einer Prägeform oder an einem Dorfgrundstück verwenden, sodass ein Pack diese Blöcke über die Weltgenerierung setzen und gleich bestücken kann.

## Portale und Tore

`<namespace>/blocks/*.json`

Ein Portal ist eine gewöhnliche Blockdefinition, es gilt also dieselbe Pfadregel, und jede Variante ist ein Portalblock.

Ein `portal`-Block trägt einen `portal`-Abschnitt:

```json
{
  "type": "portal",
  "material": "portal",
  "portal": {
    "dimension": "mypack:ruby_world",
    "returnDimension": "minecraft:overworld",
    "gate": "mypack:ruby_gate",
    "cooldown": 60,
    "platform": true,
    "platformBlock": "mypack:ruby_block",
    "sound": "block.portal.travel",
    "owned": true
  },
  "variants": { "ruby_portal": { "hardness": -1, "light": 11 } }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `dimension` | ja | Dimensions-Id | | Wohin es dich schickt |
| `returnDimension` | nein | Dimensions-Id | `minecraft:overworld` | Wohin es dich zurückschickt |
| `gate` | nein | Torname | keiner | Ein Tor, das offen sein muss, um durchzukommen |
| `cooldown` | nein | int, Ticks | `60` | Bis derselbe Spieler es wieder benutzen kann |
| `platform` | nein | boolean | `true` | Bei der Ankunft eine Landeplattform bauen |
| `platformBlock` | nein | Blockname | der eigene Rahmen des Portals | Woraus diese Plattform besteht |
| `sound` | nein | Soundname | keiner | Wird beim Durchgehen abgespielt |
| `owned` | nein | boolean | `true` | Nur wer es gebaut hat und wen er zulässt, darf es benutzen. Ein Portal mit Besitzer ist außerdem immun gegen Explosionen |
| `walkIn` | nein | boolean | `false` | Wer hineinläuft, reist, so wie bei einem Netherportal. Aus, wird es von Hand benutzt |

### Portalrahmen

`<namespace>/portalframes/*.json`

Der Pfad der Datei ist der Registry-Name des Rahmens, den eine Dimension dann in `frames` nennt.

Ein Rahmen ist ein Bild dessen, was ein Spieler bauen muss, und sonst nichts: Er sagt, welche Blöcke den Rand bilden und wo das Loch sitzt, und sagt nichts darüber, wohin das Portal führt. Das ist Absicht, denn eine Dimension beansprucht einen Rahmen, statt ihn zu besitzen, und zwei Dimensionen dürfen denselben beanspruchen.

```json
{
  "name": "Standing Gate",
  "axis": "vertical",
  "legend": { "q": "minecraft:quartz_block", "r": "mypack:ruby_block" },
  "rows": [
    "rqqqqr",
    "q....q",
    "*",
    "rqqqqr"
  ],
  "maxWidth": 6,
  "maxHeight": 9
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `name` | nein | string | der Dateiname | Der Name, der im Log erscheint |
| `axis` | nein | `vertical`, `horizontal` oder `both` | `vertical` | Ob er steht wie ein Netherportal, flach liegt wie ein Endportal oder beides darf |
| `legend` | ja | Objekt aus je einem Zeichen zu einem Block | keine | Die Blöcke, die die Zeilen verwenden dürfen. Ein Blockname mit Zuständen wird gelesen wie überall sonst |
| `rows` | ja | Liste von Strings | keine | Das Bild, oberste Zeile zuerst |
| `maxWidth` | nein | int | `21` | Breitestes Loch, bis zu dem ein `*` sich streckt |
| `maxHeight` | nein | int | `21` | Höchstes Loch, bis zu dem ein `*` sich streckt |

Drei Zeichen sind keine Blöcke. `.` ist das Loch, in dem das Portal steht, und ein Rahmen ohne eines wird abgelehnt. Ein Leerzeichen ist eine Zelle, die den Rahmen nicht kümmert, ein L-förmiger Rand entsteht also, indem man die Ecken leer lässt. `*` wiederholt: Eine Zeile, die nur aus `*` besteht, wiederholt die Zeile darüber so oft, wie der Spieler gebaut hat, und ein `*` mitten in einer Zeile wiederholt ebenso das Zeichen davor. Es darf auch gar nicht wiederholen: Das Bild mit jedem `*` gestrichen ist also das Kleinste, was zündet, und die Höchstwerte unten sind das Größte. Ein Bild ohne `*` ist genau, und der Spieler muss es so und nicht anders bauen.

Ein stehender Rahmen wird auf beiden waagerechten Achsen und in beiden Richtungen gefunden, es kommt also nicht darauf an, wie der Erbauer stand. Ein liegender wird in allen vier Drehungen gefunden.

**Wie groß er werden darf, sagt das Pack.** `maxWidth` und `maxHeight` sind das größte Loch, bis zu dem ein `*` sich streckt, und alles Kleinere bis zur Untergrenze wird angenommen, ein Pack entscheidet also selbst, ob sein Tor bei Vanillas 21 endet oder schon bei 4. Die Untergrenze ist ein Spieler: Ein stehender Rahmen wird abgelehnt, wenn sein Loch nicht mindestens 1 breit und 2 hoch werden kann, ein liegender, wenn nicht mindestens 1 mal 1, und ein Bild, das das nie erreicht, wird beim Laden mit einer Zeile im Log abgelehnt, statt ein Rahmen zu sein, durch den niemand geht.

**Ein Rahmen kostet umso mehr Suche, je mehr er sich strecken kann.** Ein Zeilen-`*` und ein Spalten-`*` zusammen heißt, dass jede Kombination bis zu beiden Höchstwerten versucht wird, ein Rahmen, der sich in beide Richtungen bis 21 streckt, sind also 441 Bilder. Die Suche gibt lieber auf, als hängen zu bleiben, und schreibt das ins Log, das Zeichen, einen Höchstwert zu senken oder eine der Streckungen zu streichen.

**Nichts verbietet einen Rahmen aus Obsidian mit Feuerzeug, doch er hat Vorrang.** Der Rahmen wird gesucht, bevor das Item selbst wirkt, ein solcher Rahmen öffnet also die Dimension des Packs dort, wo ein Netherportal gestanden hätte. Wähle einen anderen Block oder ein anderes Zündmittel, um Vanillas Portal in Ruhe zu lassen.

### Eine Dimension über einen Rahmen öffnen

`<namespace>/dimensions/*.json`

Eine Dimension öffnet sich über einen Rahmen, indem sie einen `portal`-Abschnitt trägt. Der Rahmen und das, womit er angezündet wird, wählen zusammen die Dimension, eine Rahmenform kann also je nach Zündmittel an mehrere Orte führen.

```json
{
  "portal": {
    "frames": ["mypack:standing_gate"],
    "ignitedBy": "minecraft:flint_and_steel",
    "color": "#C77DFF",
    "return": "built",
    "gate": "mypack:ruby_gate",
    "cooldown": 60,
    "platform": true,
    "sound": "block.portal.travel"
  }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `frames` | ja | Liste von Rahmennamen | keine | Die Rahmen, die diese Dimension öffnen |
| `ignitedBy` | nein | Itemname | `minecraft:flint_and_steel` | Was ein Spieler in der Hand hält, um einen anzuzünden |
| `color` | nein | Hex-Farbe | weiß | Die Farbe, in der das Portal gezeichnet wird |
| `return` | nein | `built`, `player` oder `none` | `built` | Ob ein Rückweg gestellt, vom Spieler gebaut oder gar nicht gewährt wird |
| `gate` | nein | Torname | keiner | Ein Tor, das offen sein muss, um durchzugehen |
| `cooldown` | nein | int, Ticks | `60` | Bevor derselbe Spieler wieder durchgehen darf |
| `platform` | nein | boolean | `true` | Bei der Ankunft eine Landeplattform bauen |
| `platformBlock` | nein | Blockname | Stein | Woraus diese Plattform besteht |
| `sound` | nein | Soundname | keiner | Wird beim Durchgehen gespielt |
| `owned` | nein | boolean | `false` | Nur wer es angezündet hat und wen er zulässt, darf es benutzen |

Den Block, der im Loch steht, schreibt das Pack nicht. Eine Dimension mit einem `portal`-Abschnitt bekommt einen eigenen, in der Portaltextur des Spiels unter `color` gezeichnet, hineinzulaufen statt von Hand zu benutzen, und unzerstörbar. Die Farbe multipliziert die Textur, so wie ein `tintindex` es tut: `#C77DFF` behält das Violett des Nethers, `#4CFFB0` macht es giftig. Wer ein Portal will, das gar nicht die Vanilla-Textur ist, schreibt einen gewöhnlichen eigenen `portal`-Block mit eigener Textur, auf Wunsch als [Pixelkarte](#texturen-als-pixelkarte), wo `tint` zwischen zwei Farben rampen kann.

`return` entscheidet, was auf der anderen Seite geschieht. `built` stellt denselben Rahmen auf, in der Größe, die der Spieler gebaut hat, und zündet ihn an, so wie Vanilla es macht. `player` baut nichts, lässt denselben Rahmen aber drüben anzünden, der Heimweg will also gefunden und gebaut werden. `none` lässt den Rahmen in jener Dimension gar nicht erst zünden, und die Reise geht nur hin.

**Ein Rahmen, mehrere Dimensionen.** Das Paar aus Rahmen und Zündmittel wählt die Dimension, dasselbe `standing_gate` mit Feuerzeug und mit dem eigenen Zünder eines Packs öffnet also zwei verschiedene Orte, jeden in seiner eigenen Farbe. Beanspruchen zwei Dimensionen denselben Rahmen *und* dasselbe Item, ist das ein Fehler im Pack: Die zweite wird abgelehnt und sagt es im Log, statt dass eine von beiden stillschweigend gewinnt.

Bricht ein Block des Rahmens weg, geht das Portal aus, wie in Vanilla.

`<namespace>/gates/*.json`

Der Pfad der Datei ist der Registry-Name des Tors, das ein Portal dann in `gate` nennt.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht.

```json
{
  "dimension": "mypack:ruby_world",
  "name": "The Ruby Gate",
  "scope": "player",
  "open": false,
  "unlock": {
    "hold": "mypack:ruby_key",
    "consume": "mypack:ruby",
    "consumeCount": 4,
    "craft": "mypack:ruby_pickaxe",
    "advancement": "mypack:story/ruby",
    "killed": "minecraft:wither",
    "killedCount": 2,
    "killedDrops": "mypack:ruby_key"
  },
  "unlockedMessage": "%dim% is now open",
  "blockedMessage": "You need %item% to enter %dim%",
  "safeReturn": true,
  "portalBlocks": ["mypack:ruby_portal"],
  "requires": ["mypack"]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `dimension` | ja | Dimensions-Id | | Die Dimension, die es bewacht |
| `name` | nein | string | der Dateiname | Wird dem Spieler angezeigt |
| `scope` | nein | `player`, `global` | `player` | Ein Spieler nach dem anderen oder die ganze Welt auf einmal |
| `open` | nein | boolean | `false` | Ob es offen startet |
| `unlock` | nein | Objekt | | Was es öffnet. Siehe unten |
| `unlockedMessage` | nein | string | `%dim% is now open` | Wird beim Öffnen angezeigt |
| `blockedMessage` | nein | string | `You need %item% to enter %dim%` | Wird bei der Abweisung angezeigt |
| `safeReturn` | nein | boolean | `false` | Ein abgewiesener Rückweg landet trotzdem sicher irgendwo, statt abgelehnt zu werden |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Das Tor wird übersprungen, wenn nicht alle da sind |
| `portalBlocks` | nein | Liste von Blocknamen | jedes Portal | Begrenzt das Tor auf diese Portalblöcke, eine Dimension kann also eine bewachte und eine offene Tür haben |

`unlock` nimmt `hold` (ein Item, das in der Hand sein muss), `consume` mit `consumeCount` (`1`), `craft` (ein Item, das gecraftet worden sein muss), `advancement` und `killed` (ein Entity-Name; das Tor öffnet sich für den, der eine davon erlegt, ein Boss kann also den Schlüssel zu einer Welt tragen) mit `killedCount` (`1`), wenn eine nicht reicht, gezählt pro Spieler oder für die ganze Welt, je nach `scope`. Mit `killedDrops` (ein Itemname) legen die gezählten Abschüsse stattdessen dieses Item dem Erleger vor die Füße, statt das Tor zu öffnen, und die Zählung beginnt von vorn, ein Schlüssel lässt sich also erneut verdienen und an jemanden weitergeben, der nie dafür gekämpft hat; sperr dann über `hold` oder `consume` desselben Items, um es zum Schlüssel zu machen. `%item%`, `%mob%` und `%dim%` werden für dich eingesetzt. Ein Schlüssel, den ein Mob droppt, braucht hier nichts Besonderes: Gib dem Mob den Drop und sperr über `hold` oder `consume`.

Tore bewachen auch die Dimensionen des Spiels selbst: Ein Tor, dessen `dimension` `minecraft:the_nether` ist, steht vor jedem Netherportal.

## Weltvorlagen

`<namespace>/worldtemplates/*.json`

Der Pfad der Datei ist der Name der Vorlage, die die Config-Option `worldTemplate` nennen kann, um sie direkt auszuwählen.

Fasst die Gestalt einer Welt in einer Datei zusammen, ein Pack liefert also eine ganze Welt auf einmal, statt vom Spieler ein Dutzend Config-Optionen zu verlangen.

```json
{
  "name": "Ruby World",
  "default": "void",
  "dimensions": ["minecraft:overworld"],
  "settings": {
    "voidWorld": true,
    "flatBedrock": true,
    "blockBiomes": true
  },
  "structures": {
    "villages": false,
    "mineshafts": false,
    "strongholds": true
  },
  "roles": { "ocean": "mypack:ruby_ocean" }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `name` | nein | string | der Dateiname | Wird im Log und in den Berichten angezeigt |
| `default` | nein | Biomname oder `void` | `void` | Was ein Biom füllt, das die Sperre entfernt hat. `fallback` ist derselbe Schlüssel unter anderem Namen |
| `roles` | nein | Objekt aus Rolle zu Biom | keines | Biome, die bestimmte Rollen füllen, etwa Ozean oder Fluss |
| `structures` | nein | Objekt aus [Strukturname](#wertelisten) zu boolean | keines | Vanilla-Strukturen, ein- oder ausgeschaltet |
| `settings` | nein | Objekt | keines | Config-Werte, die die Vorlage setzt |
| `dimensions` | nein | Liste von Dimensions-Ids | jede Dimension | Für welche Dimensionen sie gilt |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Vorlage wird übersprungen, wenn nicht alle da sind |

`settings` nutzt dieselben Schlüsselnamen wie die Config, es gibt also keine Übersetzungstabelle zu lernen.

Welche Vorlage aktiv ist, entscheidet die Config-Option `worldTemplate`. Steht sie auf `auto`, gewinnt das Pack mit der höchsten Priorität, das eine mitbringt, in derselben Reihenfolge, der alles andere auch folgt. Nennst du dort eine Vorlage, ist sie gesetzt.

**Ein Biom kann anders bauen.** Ein `biomes`-Objekt innerhalb von `settings` trägt eigene Dorfeinstellungen für ein benanntes Biom, ein Wüstendorf legt also Sandsteinstraßen, wo ein Ebenendorf Beton legt, ohne dass eines davon ein eigenes Pack wäre. Nenn ein Biom über seine Id, `minecraft:desert`, über eines der Typwörter, die dieser Mod auf Biom-Tags abbildet (`sandy`, `snowy`, `desert`, `forest`, `jungle`, `mountain`, `ocean`, `swamp`, `hot`, `cold` und die übrigen), oder über einen ausgeschriebenen Tag, `#minecraft:is_forest`; eine genaue Id wird vor den Typen angesehen, eine allgemeine Regel lässt sich also für ein Biom überschreiben. Alles, was in einem Abschnitt nicht genannt ist, fällt auf die einfache Einstellung darüber zurück.

```json
{
  "settings": {
    "villagePathBlock": "minecraft:black_concrete",
    "villageSubwayTunnelBlock": "minecraft:stone_bricks",
    "biomes": {
      "sandy": {
        "villagePathBlock": "minecraft:cut_sandstone",
        "villageSubwayTunnelBlock": "minecraft:sandstone"
      },
      "minecraft:snowy_plains": {
        "villageSubwayTunnelBlock": "minecraft:packed_ice"
      }
    }
  }
}
```

Jede Blockeinstellung, die eine Straße, eine Brücke, eine Bahn, eine U-Bahn, ein Bahnhof oder ein Abwasserkanal nimmt, hört darauf, und die Syntax der gewichteten Mischung funktioniert innerhalb eines Abschnitts wie außerhalb. Das Biom wird gelesen, während ein Stück gebaut wird, und die Blöcke werden überall dort neu gezogen, wo der Boden das Biom wechselt, eine Straße oder eine Bahn, die eine Wüste verlässt, wechselt das Material also genau an der Grenze. Eine Logzeile beim Laden der Welt sagt, wie viele Abschnitte ein Pack mitbringt, und nennt sie, und mit eingeschaltetem Debug sagt jedes Biom, welchen Abschnitt es genommen hat, oder dass es keinen genommen hat und worauf es gehört hätte.

## Die Tiefenwelt

Die Oberwelt kann höher oder tiefer sein, als das Spiel sie macht, und der Raum, der sich unter dem Gelände öffnet, wird mit eigener Generierung gefüllt. Vier `terrain`-Schlüssel tun das, im `settings`-Block einer Weltvorlage wie die übrigen; eine Pack-Dimension macht dasselbe mit `minHeight` und `maxHeight` in ihrem eigenen `terrain`.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldMinHeight": -320,
    "worldMaxHeight": 320,
    "deepStone": "mypack:slate",
    "noiseCaves": "deep"
  }
}
```

| Schlüssel | Wert | Standard | Was er macht |
| --- | --- | --- | --- |
| `worldMinHeight` | int, ein Vielfaches von 16, bis hinunter zu -2032 | `-64` | Der unterste Block der Oberwelt. Der eigene Boden des Spiels ist -64; tiefer macht eine Tiefenwelt unter dem Vanilla-Gelände, massiver Stein, bis die Worldgen-Ebene ihn schnitzt oder `noiseCaves` die Höhlen des Spiels hinunterträgt. Wird nur über das erzeugte Preset angewandt, eine vor dem Pack gemachte Welt behält also ihre Höhe |
| `worldMaxHeight` | int, ein Vielfaches von 16, bis 2032 und höchstens 4064 über dem Boden | `320` | Der Block über der Decke der Oberwelt. Die eigene Decke des Spiels ist 320; höher lässt offenen Himmel über dem Vanilla-Gelände |
| `deepStone` | Blockname | keiner | Der Block, aus dem die Welt unter dem Vanilla-Gelände besteht, wenn der Boden unter -64 geht, etwa der eigene Tiefenschiefer eines Packs. Er geht über die acht Schichten unter -64 in Tiefenschiefer über, so wie Tiefenschiefer in Stein übergeht. Leer behält Stein |
| `noiseCaves` | `off`, `deep` oder `world` | `off` | Wo die Höhlen, Tunnel, Nudeln und Aquifere des Spiels weitergehen, wenn der Boden unter -64 geht: `off` lässt die Welt unter dem Vanilla-Gelände als massiven Tiefenstein für die Worldgen-Ebene zum Schnitzen, `deep` trägt sie bis zum Boden hinunter, mit den Lavaseen in dessen unterste zehn Schichten verlegt, `world` bedeutet in dieser Version dasselbe, weil das Vanilla-Gelände sie ohnehin hat |

Die Tiefenwelt ist der Ort, an dem die eigenen Worldgen-Einträge, Höhlenregionen und Härtegruppen eines Packs ihre Arbeit tun: `minHeight` und `maxHeight` eines Eintrags reichen so weit hinunter, wie der Boden geht. Die Himmelsschlüssel aus 1.12.2, `deepRavines`, `oreVeins`, `terrainOffset` und die Rubic-Welt selbst haben hier keinen Zwilling, da die eigene Generierung dieser Engine ohnehin vom Boden bis zur Decke reicht.

## Höhlenregionen

`<namespace>/caveregions/*.json`

Der Pfad der Datei ist der Name der Region, den ein Worldgen-Eintrag dann in `caveRegions` nennt. Ein bloßer Name dort nimmt den Namespace dieses Eintrags.

Malt benannte Regionen über den Untergrund, das Pack-Gegenstück zu den Höhlenbiomen des Spiels. Der Untergrund wird in gerundete Zellen geteilt, `caveRegionCells` Blöcke breit und `caveRegionCellsY` hoch, beides `terrain`-Schlüssel, und jede Zelle würfelt nach Gewicht eine Region, oder keine. Alles, was eine Region tut, folgt deterministisch aus dem Seed, Chunks stimmen also überein, ohne je über eine Grenze zu schreiben.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `caveRegionCells` | int, Blöcke | `128` | Wie breit eine Regionszelle ist |
| `caveRegionCellsY` | int, Blöcke | `64` | Wie hoch eine Regionszelle ist |
| `caveRegionPlainWeight` | int | `4` | Das Gewicht des schlichten, regionslosen Untergrunds im Wurf jeder Zelle. Höher lässt mehr Untergrund ohne Region: Mit einer einzigen Region vom Gewicht 1 bekommt etwa ein Fünftel der Zellen die Region |

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht.

```json
{
  "weight": 3,
  "minHeight": -56,
  "maxHeight": 16,
  "dimensions": ["minecraft:overworld"],
  "biome": "minecraft:mushroom_fields",
  "floorCover": "minecraft:mycelium",
  "floorChance": 0.8,
  "ceilingCover": "minecraft:brown_mushroom_block",
  "ceilingChance": 0.3,
  "coverReplace": ["minecraft:stone", "mypack:slate"],
  "keepDefaultSpawns": false,
  "spawns": [
    { "entity": "minecraft:mooshroom", "type": "creature", "weight": 12, "min": 2, "max": 4 }
  ],
  "structures": [
    { "structure": "mypack:cave_shrine", "weight": 3 },
    "mypack:cave_well"
  ],
  "structureChance": 0.5,
  "structureLoot": "minecraft:chests/simple_dungeon"
}
```

| Schlüssel | Wert | Standard | Was er tut |
| --- | --- | --- | --- |
| `weight` | int | `1` | Anteil der Zellen, die diese Region gewinnt. `0` schaltet sie ab |
| `minHeight` | int | der Weltboden | Unterkante des Bandes, in dem die Region existiert |
| `maxHeight` | int | `48` | Oberkante des Bandes. Eine Zelle, deren Mitte außerhalb liegt, wählt die Region nie |
| `dimensions` | Liste von Dimensions-Ids | alle | In welchen Dimensionen die Region erscheint |
| `floorCover` | Block | keiner | Ersetzt den obersten Block von Höhlenböden innerhalb der Region |
| `floorChance` | 0.0 bis 1.0 | `1.0` | Wie viel vom Boden bedeckt wird |
| `ceilingCover` | Block | keiner | Ersetzt Höhlendeckenblöcke innerhalb der Region |
| `ceilingChance` | 0.0 bis 1.0 | `1.0` | Wie viel von der Decke |
| `coverReplace` | Liste von Blöcken | alles Steinartige | Was die Bedeckungen ersetzen dürfen |
| `spawns` | Liste | keine | Mobs, die innerhalb der Region spawnen, mit denselben Einträgen wie das `spawns` eines Bioms: `entity`, `type` (monster, creature, ambient oder water), `weight`, `min` und `max` für die Gruppengröße. Eine Stelle mit Himmelssicht bleibt dem Biom überlassen, wie bei den Bedeckungen |
| `keepDefaultSpawns` | boolean | `false` | Behält die Spawnliste des Bioms neben der der Region. Aus, ersetzt die Liste der Region sie innerhalb der Region vollständig |
| `structures` | Liste | keine | Ein Bauwerk, einmal pro Regionszelle gesetzt, im Herzen der Zelle, auf den nächsten Höhlenboden gesetzt, so wie das Spiel einem Höhlenbiom sein Wahrzeichen gibt. Einträge sind `namespace:name`-Vorlagen oder `{ "structure": "...", "weight": 3 }` zur Auswahl zwischen mehreren |
| `structureChance` | 0.0 bis 1.0 | `1.0` | Die Chance, mit der jede Zelle der Region ihr Bauwerk tatsächlich bekommt |
| `structureLoot` | `namespace:pfad` | keine | Die Beutetabelle, aus der jede Truhe in einem gesetzten Bauwerk beim ersten Öffnen gefüllt wird |
| `biome` | Biomname | keiner | Das Biom, das die Region in ihrem Raum meldet, als 3D-Biom geschrieben. Gibt der Region eigene Laub-, Gras- und Wasserfarben, eigene Musik und Umgebungsgeräusche, und Vanillas Spawn-Gewichtung liest es. Die Oberfläche darüber bleibt unberührt, da nur die Zellen geschrieben werden, die die Region einnimmt |
| `requires` | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Region wird übersprungen, wenn nicht alle da sind |

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "caveRegionCells": 128,
    "caveRegionCellsY": 64,
    "caveRegionPlainWeight": 4
  }
}
```

Wie viel vom Untergrund schlicht bleibt, bestimmt der `terrain`-Schlüssel `caveRegionPlainWeight`, Standard `4`: Mit einer einzigen Region vom Gewicht 1 bekommt etwa ein Fünftel der Zellen die Region. Bedeckungen greifen unter einem Dach, eine Region, die über den Boden hinausreicht, zeigt sich an der Oberfläche also nie. Bedeckungen wirken in jeder Höhle, egal welcher Generator sie geschnitzt hat. Das `waterLevel` aus 1.12.2 hat keinen Zwilling: Aquifere gehören auf dieser Engine den Noise-Einstellungen und lassen sich nicht pro Region festlegen.

Features docken über zwei Schlüssel gewöhnlicher [Worldgen-Einträge](#worldgen-einträge) an. `caveRegions` zählt die Regionen auf, in denen ein Eintrag generieren darf, geprüft an der gesetzten Position, sodass Pilze, Kristalle oder was auch immer nur innerhalb ihrer Region erscheinen. `snap` verschiebt jeden Versuch zuerst senkrecht zur nächsten Höhlenfläche: `floor` für Stehendes, `ceiling` für Hängendes. Eine Tropfstein-Region braucht keine neuen Formen:

```json
{
  "block": "mypack:stone_spike",
  "attempts": { "min": 4, "max": 8 },
  "minHeight": -60,
  "maxHeight": 40,
  "caveRegions": ["dripstone"],
  "snap": "ceiling",
  "replace": ["minecraft:air"],
  "shape": { "type": "spire", "radius": 1, "height": { "min": 2, "max": 6 }, "taper": "needle", "hanging": true }
}
```

Das `replace` mit `minecraft:air` ist wichtig: Was eine gesetzte Form überschreibt, wird gegen `replace` geprüft, dessen Standard Stein ist, alles, was in offenen Höhlenraum gebaut wird, braucht also Luft in der Liste. Derselbe Eintrag mit `"snap": "floor"` und ohne `hanging` lässt die passenden Stalagmiten wachsen. Der Regionsfilter funktioniert mit jeder gesetzten Form; `belt` und `field` setzen nach eigenen Regeln und ignorieren ihn.

## Welt-Intro

`<namespace>/worldintro/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner. Jedes Intro, das ein Pack mitbringt, läuft, in Pack-Reihenfolge.

Zeigt eine Folge von Seiten, wenn ein Spieler die Welt betritt, bevor er die Kontrolle bekommt. Laufender Text über einem Bild, eine Titelkarte, eine Diaschau, oder alles drei hintereinander.

```json
{
  "once": true,
  "music": "minecraft:music.credits",
  "requires": ["mypack"],
  "pages": [
    {
      "background": "mypack:textures/gui/sunrise.png",
      "text": "mypack:texts/opening.txt",
      "mode": "scroll",
      "time": 14.0,
      "direction": "up",
      "textScale": 3.0,
      "settle": true
    },
    {
      "backgrounds": [
        "mypack:textures/gui/logo_a.png",
        "mypack:textures/gui/logo_b.png"
      ],
      "interval": 4.0,
      "text": "mypack:texts/title.txt",
      "mode": "static",
      "textScale": 2.0
    }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `pages` | ja | Liste von Seiten | keine | Werden der Reihe nach gezeigt. Eine Datei ohne Seiten wird mit einem Fehler abgelehnt |
| `once` | nein | boolean | `false` | Einmal pro Spieler und Welt abspielen statt bei jedem Beitritt |
| `music` | nein | Name eines Sound-Events | keiner | Ein Stück für den ganzen Durchlauf, gestartet mit der ersten Seite |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Das Intro wird übersprungen, wenn nicht alle da sind |

Jeder Eintrag in `pages`:

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `mode` | nein | `scroll` oder `static` | `scroll` | Text, der läuft, oder Text, der steht, bis der Spieler weiterklickt |
| `text` | nein | Pfad zu einer `.txt`-Datei | keiner | Die Worte. Für eine Seite aus reinen Bildern lässt du ihn weg |
| `background` | nein | Texturpfad | der gekachelte Erdhintergrund | Ein Hintergrund |
| `backgrounds` | nein | Liste von Texturpfaden | keine | Mehrere, im Wechsel. Kommt zu `background` dazu, wenn du beides angibst |
| `interval` | nein | Sekunden | `5.0` | Wie lange jeder Hintergrund steht, wenn es mehr als einen gibt |
| `time` | nein | Sekunden | wird aus dem Text errechnet | Wie lange eine laufende Seite von Anfang bis Ende braucht |
| `direction` | nein | `up` oder `down` | `up` | In welche Richtung der laufende Text zieht |
| `textScale` | nein | Zahl | `1.0` | Multipliziert die Schriftgröße |
| `settle` | nein | boolean | `false` | Endet mit der letzten Zeile in der Mitte, statt ganz aus dem Bild zu laufen |

Textdateien liegen unter `assets/<namespace>/texts/*.txt`. Reiner Text, ein Absatz pro Zeile, und Leerzeilen bleiben Leerzeilen. `PLAYERNAME` wird durch den Namen des Spielers ersetzt, dieselbe Ersetzung, die auch das Vanilla-Endgedicht nutzt.

`time` legt fest, wie lange die Seite dauert, dieselbe Seite braucht also gleich lang, ob eine Zeile darauf steht oder zwanzig. Die Lesegeschwindigkeit stellst du darüber ein, wie viel du auf die Seite packst. Lässt du `time` weg, läuft die Seite so schnell wie der Vanilla-Abspann, wo mehr Text einfach länger dauert.

Eine laufende Seite geht zur nächsten über, wenn ihre Zeit um ist. Die letzte Seite geht nie von selbst weiter, sie wartet. Unten stehen **Next Page** und **Skip All**, auf der letzten Seite ein einzelnes **Continue to World**. Escape tut dasselbe wie Skip All. Statische Seiten zentrieren jede Zeile. Laufende Seiten halten sich an eine feste Spalte, so wie der Abspann.

Im Einzelspieler pausiert die Welt hinter dem Intro, es schleicht sich also nichts an den Spieler heran, während er liest. Die einzige Ausnahme ist Land, das beim Öffnen des Intros noch gemacht wird: dann läuft das Machen hinter den Seiten weiter, und der Spieler bleibt als Zuschauer festgehalten, bis er in die Welt weitergeht, auch wenn der Lauf vorher fertig wird. Auf einem Server läuft die Welt weiter, und ein Vanilla-Client sieht das Intro überhaupt nicht und tritt ganz normal bei. Der Willkommensgruß wartet, bis die Seiten geschlossen sind, damit er nicht hinter ihnen verloren geht.

`once` wird in den Spielerdaten gespeichert und übersteht den Tod. `/rdplserver intro` setzt es für den zurück, der ihn ausführt, das Intro läuft dann beim nächsten Beitritt wieder. Es wird nicht sofort noch einmal abgespielt, damit es kein Weg zurück in die Einstiegssequenz mitten im Spiel wird.

Hintergründe werden auf die Fenstergröße gezogen, ein 16:9-Bild passt also zu einem 16:9-Fenster, ein quadratisches sieht gestaucht aus. Schneid das Bild passend zu, statt dich auf die Anpassung zu verlassen. `music` nimmt jedes registrierte Sound-Event, von Vanilla oder aus deinem eigenen Pack über `sounds`. Es läuft nicht in Schleife, ein kurzes Stück ist also irgendwann zu Ende und lässt Stille zurück.

Bringt mehr als ein Pack ein Intro mit, laufen ihre Seiten in Pack-Reihenfolge hintereinander, statt dass eines gewinnt. Sperr sie mit `requires`, wenn du nur eines willst.

## Spielregeln

`<namespace>/gamerules/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

```json
{
  "minecraft:overworld": {
    "doFireTick": "false",
    "keepInventory": "true",
    "randomTickSpeed": "3"
  },
  "minecraft:the_nether": {
    "doFireTick": "true"
  }
}
```

Jeder Schlüssel ist die Id der Welt, zu der die Regeln gehören, `minecraft:overworld`, `minecraft:the_nether`, `minecraft:the_end`, eine packeigene oder was ein Mod nutzt; die Zahlen `0`, `-1` und `1` aus 1.12.2 werden weiterhin als die drei Vanilla-Welten verstanden. Werte sind Strings, so wie im Befehl `/gamerule`, also `"false"` statt `false`. Sie werden auf neue Welten angewendet. Eine Dimensionsdatei trägt dieselben Regeln stattdessen in einem `gameRules`-Block, der immer nur für diese eine Welt gilt.

## Teams

`<namespace>/teams/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich. Jede Datei ist eine Seite.

Eine Seite ist ein echtes Team auf dem Scoreboard des Spiels, also sieht `/team list` sie, sie behält ihre Mitglieder über Speichern und Neuladen, und ein Client ohne diesen Mod zeigt Farben und Namensschilder wie bei jedem Vanilla-Team. Die Mitgliedschaft läuft über den Namen, also kann alles mit Namen oder UUID auf einer Seite stehen: ein Spieler, ein Zombie, ein Dorfbewohner, ein Rüstungsständer.

```json
{
  "name": "red",
  "displayName": "Red Team",
  "color": "red",
  "friendlyFire": false,
  "joinable": false,
  "entities": ["mypack:zombie_a", "mypack:sapper_a"]
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `name` | Text | der Dateiname | Der Name des Teams auf dem Scoreboard, 1 bis 16 Zeichen. Damit arbeiten `/team` und die anderen Dateien |
| `displayName` | Text | der Name | Was Spielern statt des Namens gezeigt wird |
| `color` | Text | `white` | Eine der sechzehn Textfarben. Sie färbt das Namensschild und ist der Schlüssel für die Team-Sidebars |
| `prefix` | Text | leer | Wird vor den Namen eines Mitglieds gesetzt, nach der Farbe |
| `suffix` | Text | leer | Wird hinter den Namen eines Mitglieds gesetzt |
| `friendlyFire` | boolean | `false` | Ob Mitglieder einander verletzen können. Zugleich der Vorgabewert von `mobFriendlyFire` |
| `mobFriendlyFire` | boolean | `friendlyFire` | Ob die Mobs einer Seite ihre eigene Seite mit Explosionen und geworfenem TNT verletzen können, was das Spiel allein nie unterbindet. Aus bewahrt die Seite; an lässt es, wie das Spiel es hat |
| `seeFriendlyInvisibles` | boolean | `true` | Ob Mitglieder einander sehen, während sie unsichtbar sind |
| `nameTags` | Text | `always` | `always`, `never`, `hideForOtherTeams` oder `hideForOwnTeam` |
| `deathMessages` | Text | `always` | Dieselben vier Wörter, dafür wer erfährt, dass ein Mitglied stirbt |
| `collision` | Text | `always` | `always`, `never`, `pushOtherTeams` oder `pushOwnTeam` |
| `entities` | Liste | leer | Entity-Ids, deren Spawns dieser Seite beitreten, etwa `minecraft:zombie` oder eine eigene |
| `players` | Liste | leer | Spielernamen, die dieser Seite beim Einloggen beitreten |
| `spawnBox` | Liste | keiner | Sechs ganze Zahlen, x y z bis x y z. Alles, was darin spawnt, tritt bei, und die Ecken dürfen in beliebiger Reihenfolge stehen |
| `joinable` | boolean | `true` | Ob ein Spieler mit `/rdplserver team join` beitreten darf. Auf false für eine Seite, die nur für Mobs ist |
| `balance` | boolean | `false` | Ob `/rdplserver team join` ohne Namen einen Spieler hierher setzen darf. Unter den Seiten, die das erlauben, wird die mit den wenigsten Spielern gewählt |
| `scoreboard` | boolean | `true` | Ob die Seite als Team auf dem Scoreboard des Spiels steht. Aus stellt gar kein Team auf: Ihre Mobs tragen stattdessen die Farbe der Seite im Namen, nichts hält sie davon ab, einander anzugreifen, und es landen keine Punkte auf ihr, denn gewertet wird nach dem Team |
| `lead` | Text | `none` | Wie die Führung der Seite bestimmt wird: `none`, `topScore` für den Höchsten auf dem Ziel, das `leadOn` nennt, `appointed` für den Spieler, den `leadIs` nennt, `vote` für den, den die Mitglieder wählen, oder `claim` für den, der sie zuerst beansprucht. Eine Führung ist ein Etikett und eine Farbe und sonst nichts: sie verleiht keine Macht, also zerbricht nichts, wenn eine Führung sich ausloggt |
| `leadOn` | Text | leer | Bei `topScore` das Ziel, nach dem die Mitglieder geordnet werden. Es wird bei jedem Lesen neu ermittelt und folgt damit dem Punktestand |
| `leadIs` | Text | leer | Bei `appointed` der Spieler, der führt |

Drei Wege beizutreten, und eine Seite darf alle nutzen. `entities` nennt Entity-Ids, und alles dieser Art tritt beim Spawnen bei, so gibt ein Pack Mobs ihre Seite, ohne die Mobs anzufassen. `spawnBox` beansprucht eine Ecke der Welt, und alles, was darin spawnt, tritt bei, was zu einer Arena passt, in der beide Seiten denselben Mob nutzen. `players` nennt Spieler direkt. Darüber hinaus tritt ein Spieler mit `/rdplserver team join <name>` bei, sofern die Seite `joinable` nicht auf false setzt, und verlässt sie mit `/rdplserver team leave`.

Eine Seite wird nur aufgestellt, wo ein Pack danach fragt: ohne `teams`-Ordner legt der Mod kein Team an, lauscht auf nichts und bietet den Befehl nicht an. Ein Serveroperator, der eine Datei ändert, kann `/rdplserver reload` ausführen und die Änderung ohne Neustart in die laufende Welt bringen.

## Wertung

`<namespace>/scoring/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich. Jede Datei ist ein Ziel.

Ein Ziel ist ein echtes Ziel auf dem Scoreboard des Spiels, also liest `/scoreboard players list` es und es behält seine Punkte über das Speichern. `criterion` ist das, was das Spiel selbst zählt: `dummy` für einen Punktestand, den nur dieses Pack bewegt, oder `deathCount`, `playerKillCount`, `totalKillCount`, `health`, `air`, `armor`, `food`, `level`, `xp`, `trigger` oder jede Statistik, geschrieben, wie `/scoreboard` sie nimmt, etwa `minecraft.custom:minecraft.jump`.

```json
{
  "name": "kaboom",
  "displayName": "Kills",
  "criterion": "dummy",
  "display": "sidebar",
  "teamTotals": true,
  "points": {
    "kill": { "mypack:zombie_a": 1, "mypack:zombie_b": 1 },
    "death": -1
  },
  "ends": {
    "afterMinutes": 10
  },
  "results": {
    "card": true,
    "title": "Final standings",
    "icon": "minecraft:tnt",
    "seconds": 15
  }
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `name` | Text | der Dateiname | Der Name des Ziels auf dem Scoreboard, 1 bis 16 Zeichen |
| `displayName` | Text | der Name | Was Spielern statt des Namens gezeigt wird |
| `criterion` | Text | `dummy` | Was das Spiel selbst zählt. Ein unbekanntes wird mit einer Zeile abgelehnt, die das sagt |
| `display` | Text | leer | `sidebar`, `list`, `belowName` (`below_name` geht auch) oder `sidebar.team.<color>`. Leer zeigt es nirgends; einen Scoreboard-Bildschirm zum Öffnen gibt es nicht |
| `render` | Text | das des Kriteriums | `integer` oder `hearts` |
| `teamTotals` | boolean | `true` | Punkte landen auf einer Zeile mit dem Namen des Teams des Mitglieds |
| `individuals` | boolean | `false` | Punkte landen zusätzlich auf einer Zeile für das Mitglied selbst |
| `carries` | boolean | `false` | Das Ziel überlebt einen Kartenreset, statt mit ihm gelöscht zu werden. Eine Partiewertung der Rundensiege ist eines |
| `awardsTo` | Text | leer | Ein anderes Ziel, dem dieses beim Ende einen Punkt gibt, an die führende Seite. Bei Gleichstand gibt es nichts |
| `points.kill` | Objekt | leer | Entity-Id zu Punkten, der Seite des Tötenden gutgeschrieben. `minecraft:player` wertet einen Spielerkill |
| `points.death` | int | `0` | Punkte, wann immer ein Mitglied stirbt, gleich woran. Darf negativ sein |
| `points.ownKill` | int | `0` | Punkte für einen Kill der eigenen Seite, anstelle des `kill`-Werts. 0 wertet ihn nicht; eine negative Zahl ist eine Strafe |
| `ends.atScore` | int | `0` | Die Partie endet in dem Moment, in dem eine Seite dies erreicht. 0 endet nie über Punkte |
| `ends.afterMinutes` | int | `0` | Die Partie endet nach so vielen Minuten. 0 endet nie über die Zeit |
| `ends.afterRounds` | int | `0` | Für ein Ziel, dem ein anderes per `awardsTo` zuspielt: Die Partie endet, sobald insgesamt so viele Runden vergeben wurden, wer immer sie gewann. 0 endet nie über Runden |
| `ends.resets` | boolean | `false` | Das Rundenende setzt die Karte zurück, wie `resetSays` und die übrigen Reset-Einstellungen unter [Weltvorlagen](#weltvorlagen) es beschreiben, dann beginnt eine neue Runde |
| `ends.intermissionSeconds` | int | `10` | Wie lange die Wertung zwischen dem Ende und dem Reset steht |
| `ends.intermissionSays` | Text | `Round cooldown {seconds}` | Jede Sekunde der Pause nach einem Rundenende in der Aktionsleiste gezeigt, `{seconds}` zählt bis zum Reset herunter. Leer zeigt nichts |
| `ends.startsSays` | Text | `Round starting in {seconds}` | In der Aktionsleiste gezeigt, während die fünf Sekunden nach dem Reset die nächste Runde einleiten, `{seconds}` zählt herunter. Leer zeigt nichts |
| `ends.locksTeams` | boolean | `true` | Wer während einer laufenden Runde einer Seite beitritt, wartet bis zum Rundenende, damit niemand mitten in eine gewertete Runde fällt |
| `results.card` | boolean | `false` | Den Stand als Karte statt als Chat zeigen |
| `results.title` | Text | der Name und `results` | Die Überschrift der Karte |
| `results.icon` | Text | leer | Ein auf der Karte gezeichnetes Item, z. B. `minecraft:tnt` |
| `results.image` | Text | leer | Ein auf der Karte gezeichnetes Bild statt eines Items |
| `results.background` | Text | ein dunkles Schiefergrau | Die Hintergrundfarbe der Karte |
| `results.seconds` | int | `8` | Wie lange die Karte steht, mindestens eine Sekunde |

`points` ist das, was dieser Mod über das hinaus zählt, was das Spiel zählt, eingespeist in dasselbe Ziel, sodass `/scoreboard` es weiterhin liest. `kill` ist so viele Punkte je getöteter Entity-Id wert, der Seite des Tötenden gutgeschrieben; `death` ist so viele Punkte wert, wann immer ein Mitglied einer Seite stirbt, und darf negativ sein. Mit `teamTotals` landen die Punkte auf einer Zeile mit dem Namen des Teams, und genau das lässt die Sidebar vier Seiten zeigen statt einer Zeile je Mob. `individuals` fügt zusätzlich eine Zeile je Mitglied hinzu und ist standardmäßig aus, weil eine Zeile je Mob-UUID sich als Rauschen liest.

`ends` beendet die Partie, entweder in dem Moment, in dem eine Seite `atScore` erreicht, oder sobald `afterMinutes` vergangen sind. Der Stand wird dann gezeigt, vom Spiel selbst sortiert: als Chat oder als Karte, wenn `results` danach fragt. Einem Spieler ohne diesen Mod wird derselbe Stand als Chatzeilen gesagt, sodass niemand ohne Ergebnis bleibt. Mit `resets` ist dieses Ende das einer Runde: Die Wertung steht `intermissionSeconds` lang, während eine Abklingzeit in der Aktionsleiste herunterzählt, die Karte wird bis zur Begrüßung zurückgesetzt, und die nächste Runde beginnt nach fünf heruntergezählten Sekunden. `awardsTo` gibt die Runde der führenden Seite, auf einem Ziel, das den Reset mit `carries` überlebt. Ein mit `carries` bewahrtes Ziel kann von sich aus enden, `atScore` für ein Best-of, `afterRounds` für eine feste Zahl, und sein Stand wird beim Reset danach gelöscht, sodass eine neue Partie beginnt.

## Härtegruppen

`<namespace>/hardness/*.json`

Der Pfad der Datei benennt die Gruppe im Log, sonst liest ihn nichts, mehrere Dateien addieren sich also.

Gibt einer Gruppe von Blöcken einen Faktor für die Abbauzeit, der pro Blockposition gewürfelt wird. Der Block selbst wird nie verändert: Nichts wird registriert, nichts in die Welt geschrieben, und eine Welt ohne das Pack ist ganz gewöhnliches Vanilla.

```json
{
  "blocks": ["minecraft:stone"],
  "except": [{ "block": "minecraft:oak_log", "properties": { "axis": "y" } }],
  "miningTime": { "min": 1.0, "max": 20.0 },
  "blastResistance": { "min": 1.0, "max": 4.0 },
  "buckets": 10,
  "minHeight": -64,
  "maxHeight": 319,
  "field": { "type": "speckle", "spread": 0.15 },
  "requires": ["mypack"]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er tut |
| --- | --- | --- | --- | --- |
| `blocks` | ja | Liste von Blocknamen oder Objekten | | Die Gruppe. Dieselben Formen wie `replace` bei der Weltgenerierung |
| `except` | nein | Liste von Blocknamen oder Objekten | keine | Wieder aus der Gruppe genommen, was auch immer `blocks` sagt |
| `miningTime` | nein | Zahl oder Objekt mit `min` und `max` | `1.0` | Um wie viel länger der Block zum Abbauen braucht |
| `blastResistance` | nein | Zahl oder Objekt mit `min` und `max` | `1.0` | Multipliziert den Explosionswiderstand des Blocks |
| `buckets` | nein | 1 bis 256 | `10` | In wie viele Stufen die Spanne geteilt wird |
| `minHeight` | nein | int | der Weltboden | Darunter ist der Wurf die härteste Stufe |
| `maxHeight` | nein | int | die Weltdecke | Darüber ist der Wurf die härteste Stufe |
| `field` | nein | Objekt | siehe unten | Die Form, zu der sich der Wurf zusammenballt |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

Eine einzelne Zahl gibt jedem Block der Gruppe denselben Faktor, und nichts wird gewürfelt. Ein `min` und ein `max` würfeln pro Position: `max`, wo das Feld leer ist, `min` in der Mitte eines Nestes, und die Stufen dazwischen entscheidet `buckets`.

### Das Feld

Der Wurf geschieht nicht für jeden Block ganz allein, sonst wären hart und weich reines Rauschen ohne jede Form. `field` bestimmt, welche Form dabei herauskommt, und `type` wählt zwischen zwei Wegen dorthin.

```json
{
  "field": { "type": "speckle" }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er tut |
| --- | --- | --- | --- | --- |
| `type` | nein | `speckle` oder `seeded` | `speckle` | Welches der beiden unten genommen wird |

#### speckle

Jeder Block zieht seine eigene Stufe, und ein Block eine Fläche weiter kann eine schwächere Stufe an ihn weitergeben. Das gibt dichte, feine Sprenkel, meist einzelne Blöcke, mit hier und da einem größeren Nest, wo sie zusammentreffen.

```json
{
  "field": {
    "type": "speckle",
    "chances": [30, 30, 20, 20, 10, 10, 10, 10, 50],
    "spread": 0.15
  }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er tut |
| --- | --- | --- | --- | --- |
| `chances` | nein | Liste von Ints, je Tausend | `[30, 30, 20, 20, 10, 10, 10, 10, 50]` | Wie oft ein Block auf welcher Stufe anfängt, weichste zuletzt. Was übrig bleibt, ist die härteste Stufe |
| `spread` | nein | 0.0 bis 1.0 | `0.15` | Wie oft eine Stufe an den Nachbarblock weitergeht, eine bis drei Stufen schwächer |

Die Liste wird von hinten als weichste gelesen, der letzte Eintrag ist also die weichste Stufe und der erste liegt eine über der härtesten. Mit den Zahlen oben sind etwa sieben von zehn Blöcken die härteste Stufe, der Rest liegt verstreut dazwischen.

#### seeded

Saatpunkte sitzen auf einem Gitter, das sich aus der Welt und der Position ergibt, und die Stufe eines Blocks kommt daher, wie nah er am nächsten liegt. Das gibt weniger, größere, rundere Nester, die ineinanderlaufen, und es kann Arme treiben.

```json
{
  "field": {
    "type": "seeded",
    "cell": 8,
    "seeds": 1,
    "reach": 3.0,
    "arms": 0,
    "armReach": 0.0
  }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er tut |
| --- | --- | --- | --- | --- |
| `cell` | nein | int, Blöcke | `8` | Wie weit die Saatpunkte auseinanderliegen |
| `seeds` | nein | 1 bis 4 | `1` | Saatpunkte je Zelle |
| `reach` | nein | float, Blöcke | `3.0` | Wie weit ein Saatpunkt wirkt |
| `arms` | nein | 0 bis 6 | `0` | Arme, die von jedem Saatpunkt ausgehen |
| `armReach` | nein | float, Blöcke | `0.0` | Wie weit die Arme reichen |

Ohne `arms` sind die Nester rund. Gibt man einem Saatpunkt Arme, wird er zu einem Knoten mit Ranken, und Arme benachbarter Knoten strecken sich einander entgegen, das ist dann eine Ader statt eines Klumpens. Halte `reach` über der Hälfte von `cell`, sonst können die Nester einander nicht berühren und es bleiben einzelne Kugeln mit nichts dazwischen.

### Sichtbar machen

Der Faktor allein ist unsichtbar. Damit ein Spieler sieht, welche Blöcke zäh sind, gib dem Block einen Blockstate mit einer Variante je Stufe, alle mit gleichem Gewicht, die härteste zuerst:

```json
{
  "variants": {
    "": [
      { "model": "mypack:block/stone_step0", "weight": 1 },
      { "model": "mypack:block/stone_step1", "weight": 1 }
    ]
  }
}
```

Minecraft wählt eine Variante ohnehin schon anhand der Position eines Blocks, und eine Härtegruppe gibt ihm stattdessen die Stufe, so passen Textur und Faktor immer zusammen. Das Beispielpack macht genau das für seinen Rubinstein.

Drei Dinge müssen stimmen, und keines davon meldet sich, wenn es falsch ist.

**Genau `buckets` Einträge, alle gleich schwer.** Die Stufe wird als Platz in der Liste genommen, eine Liste anderer Länge oder mit unterschiedlichen Gewichten zeigt also stillschweigend auf die falsche Textur.

**Ein Modellname mit `block/` davor.** Ein Blockstate nennt in dieser Version die Modelldatei vollständig, `"model": "mypack:block/stone_step0"` liest also `models/block/stone_step0.json`; ein bloßes `mypack:stone_step0` sucht nach `models/stone_step0.json`, was es nicht gibt, und der Eintrag fällt kommentarlos weg.

**Derselbe Schlüssel, nach dem das Spiel fragt.** Ein Block mit einem einzigen Zustand hat den Schlüssel `""`, und Vanilla-Stein ist einer davon. Ein Block mit Eigenschaften hat als Schlüssel alle davon, eine Ersetzung für einen Stamm will also `axis=x`, `axis=y` und `axis=z`, jedes mit einer eigenen Liste.

Schalte `worldgenDebug` ein, dann wird jede Härtegruppe beim Betreten einer Welt gegen ihr gebackenes Modell geprüft: welcher Blockstate, wie viele Varianten übrig blieben, welche Textur jede davon bekam und welche Packs das Spiel dafür zusammengeführt hat. Das ist der schnellste Weg zu allen drei Punkten oben, und es warnt auch, wenn das Ersetzen eines geteilten Blockstates einen Zustand verändert hat, den die Gruppe nie genannt hat.

### Was nicht erreicht wird

Nur das Abbauen durch einen Spieler wird verändert. Maschinen, die Blöcke abbauen, lesen die Härte des Blocks direkt und merken nichts davon. Blöcke, die ein Spieler setzt, werden wie alle anderen gewürfelt, denn der Wurf gehört zum Ort und nicht zum Block, und ein anderswohin getragener Block nimmt an, was sein neuer Ort sagt.

# Generieren

## Worldgen-Einträge

`<namespace>/worldgen/*.json`

Der Pfad der Datei benennt den Eintrag, und die Formen `belt`, `field` und `vein` ziehen ihr Rauschen daraus, ein umbenannter Dateiname verschiebt also, was sie erzeugt.

Beschreibt etwas, das generiert. Jeder Eintrag ist eine **Form**, gesetzt von einer **Verteilung**, gefiltert danach, wo sie erlaubt ist.

```json
{
  "block": "mypack:ruby_ore",
  "blocks": [
    { "block": "mypack:ruby_ore", "weight": 80 },
    { "block": "minecraft:magenta_wool", "weight": 20 }
  ],
  "size": 8,
  "attempts": 12,
  "replace": ["minecraft:stone"],
  "adjacent": ["minecraft:air"],
  "minHeight": 8,
  "maxHeight": 48,
  "dimensions": ["minecraft:overworld"],
  "dimensionsAreBlacklist": false,
  "biomes": ["minecraft:windswept_hills"],
  "biomeTypes": ["mountain"],
  "biomesAreBlacklist": false,
  "minTemperature": -100.0,
  "maxTemperature": 100.0,
  "minRainfall": -100.0,
  "maxRainfall": 100.0,
  "minDistanceFromSpawn": 0,
  "sparse": false,
  "retrogen": false,
  "retrogenKey": "ruby_v1",
  "caveRegions": ["dripstone"],
  "snap": "floor",
  "snapDepth": 0,
  "indicators": ["mypack:iron_rock=3", "minecraft:gravel=1", "empty=4"],
  "indicatorCount": { "min": 1, "max": 3 },
  "indicatorSpread": 2,
  "then": ["mypack:quartz_halo=2", "empty=1", { "name": "mypack:side_branch", "weight": 1, "spread": 4, "depth": 0 }],
  "thenCount": 1,
  "thenSpread": 6,
  "thenDepth": { "min": -8, "max": -2 },
  "prospectAs": "Ruby",
  "requires": ["quark"],
  "shape": { "type": "cluster" },
  "spread": { "type": "even" }
}
```

Pflicht ist nur `block`, alles andere darf wegbleiben und nimmt seinen Standardwert. `blocks` ersetzt `block`, wenn einer nicht reicht, und hat weiter unten ein eigenes Beispiel.

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `block` | ja | Blockname | | Was gesetzt wird |
| `blocks` | nein | Liste von Objekten | keine | Eine gewichtete Liste, genutzt statt eines einzelnen Blocks. Siehe unten |
| `size` | nein | int oder Bereich | `8` | Wie viele Blöcke ein Versuch setzt, oder wie groß eine Form mit Radius ausfällt |
| `attempts` | nein | int oder Bereich | `8` | Wie oft es pro Chunk versucht wird |
| `replace` | nein | Liste von Blocknamen oder Objekten | `["minecraft:stone"]` | Was ersetzt werden darf. Siehe unten |
| `adjacent` | nein | Liste von Blocknamen oder Objekten | keine | Setzt nur dort, wo einer davon unter den 26 Blöcken steht, die die Stelle berühren. Dieselben Formen wie `replace` |
| `minHeight` | nein | int | `0` | Niedrigstes y, auf dem gesetzt wird |
| `maxHeight` | nein | int | `64` | Höchstes y, auf dem gesetzt wird |
| `dimensions` | nein | Liste von Dimensions-Ids | jede Dimension | In welchen Dimensionen es läuft |
| `dimensionsAreBlacklist` | nein | boolean | `false` | Macht aus dieser Liste die zu meidenden |
| `biomes` | nein | Liste von Biomnamen | jedes Biom | In welchen Biomen es läuft |
| `biomeTypes` | nein | Liste von Biomtypen | keine | Biome nach Typwort, etwa `forest` oder `nether` |
| `biomesAreBlacklist` | nein | boolean | `false` | Macht aus diesen Listen die zu meidenden |
| `minTemperature` | nein | float | `-100.0` | Kältestes Biom, in dem es generiert |
| `maxTemperature` | nein | float | `100.0` | Wärmstes Biom, in dem es generiert |
| `minRainfall` | nein | float | `-100.0` | Trockenstes Biom, in dem es generiert |
| `maxRainfall` | nein | float | `100.0` | Feuchtestes Biom, in dem es generiert |
| `minDistanceFromSpawn` | nein | int, Blöcke | `0` | Wie weit vom Weltspawn entfernt es losgeht |
| `sparse` | nein | boolean | `false` | Streut die Blöcke, statt sie zusammenzupacken |
| `retrogen` | nein | boolean | `false` | Generiert auch in Chunks, die es schon gibt |
| `retrogenKey` | nein | string | der Schlüssel aus der Config | Überschreibt den Retrogen-Schlüssel für diesen einen Eintrag |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Der Eintrag wird übersprungen, wenn nicht alle da sind |
| `shape` | nein | Objekt | `{ "type": "cluster" }` | Die Form, die es annimmt. Siehe [Formen](#formen) |
| `spread` | nein | Objekt | `{ "type": "even" }` | Wo es hingesetzt wird. Siehe [Verteilung](#verteilung) |
| `caveRegions` | nein | Liste von Regionsnamen | keine | Generiert nur innerhalb dieser [Höhlenregionen](#höhlenregionen) |
| `snap` | nein | `floor` oder `ceiling` | keiner | Verschiebt jeden Versuch erst senkrecht zum nächsten Höhlenboden oder zur nächsten Höhlendecke |
| `snapDepth` | nein | int | `0` | Wie weit `snap` danach über die Oberfläche hinaus geht, vom Boden nach unten und von der Decke nach oben. `0` bleibt im freien Raum an der Oberfläche, `1` ist der Oberflächenblock selbst, `2` der dahinter. Was überschrieben werden darf, regelt weiterhin `replace`, so legt ein Pack ein Band knapp unter den Boden statt darauf |
| `indicators` | nein | Liste von `block=gewicht` | keine | Blöcke, die über einer erzeugten Ader verstreut auf der Oberfläche liegen bleiben, damit ein Spieler ahnt, was unter dem Boden liegt; wähle sie passend zum Inhalt der Ader. `empty=gewicht` lässt eine Stelle leer |
| `indicatorCount` | nein | int oder Bereich | `1` | Wie viele Oberflächenstellen jede erzeugte Ader bekommt |
| `indicatorSpread` | nein | int, Blöcke | `0` | Wie weit über den Fußabdruck der Ader hinaus ein Hinweis landen darf |
| `then` | nein | Liste von `name=gewicht` oder Objekten | keine | Worldgen-Einträge, die direkt nach diesem aus ihm herauswachsen, an ihm angesetzt: der Ursprung des Nachfolgers liegt knapp außerhalb des Randes dieser Ader, in der Richtung, die `thenSpread` und `thenDepth` vorgeben, so dass sich beide berühren. Ein Eintrag ist `name=gewicht` oder ein Objekt mit `name`, `weight` und eigenem `spread` und `depth` (int oder Bereich), die für diesen Nachfolger allein die Werte der Ader ersetzen, so dass eine Liste eine Diamantspitze nach unten und einen Ast zur Seite schicken kann. Ein bloßer Name wird im Namespace dieses Packs gelesen, `empty=gewicht` reiht nichts ein. Ein Nachfolger behält seine eigene Form, Blöcke, Größe und `replace`, überspringt aber seine eigenen Versuche, Chance, Höhenband und Biomfilter, und darf selbst `then` tragen, so tief das Pack will; ein Eintrag, der in derselben Kette schon erzeugt wurde, beendet sie |
| `thenCount` | nein | int oder Bereich | `1` | Wie viele verschiedene Nachfolger pro erzeugter Ader aus dieser Liste gewählt werden, jeder Eintrag höchstens einmal, so dass eine Zahl gleich der Listenlänge alle wachsen lässt |
| `thenSpread` | nein | int, Blöcke | der Radius der Form | Wie weit die Richtung, in die ein Nachfolger wächst, seitlich kippen darf, gewürfelt von minus bis plus diesem Wert |
| `thenDepth` | nein | int oder Bereich | `0` | Wie weit die Richtung nach unten (negativ) oder oben kippt. `0` ohne seitliches Kippen hängt den Nachfolger gerade nach unten |
| `prospectAs` | nein | string | der Dateiname | Wie ein Schürfitem diesen Eintrag in seiner Lesung nennt, z. B. `Hematite` |

### Gewichtete Blöcke

`blocks` ersetzt `block`, wenn ein Eintrag nicht reicht. Die Gewichte sind relativ, 80 und 20 ist also vier zu eins.

```json
{
  "blocks": [
    { "block": "minecraft:magenta_wool", "weight": 80 },
    { "block": "minecraft:oak_log", "weight": 20, "properties": { "axis": "x" } }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `block` | ja | Blockname | | Was gesetzt wird |
| `weight` | nein | int | `1` | Wie oft dieser gegenüber den anderen gezogen wird |
| `properties` | nein | Objekt aus Eigenschaft zu Wert | keines | Blockstate-Eigenschaften nach Namen, für einen anderen Zustand als den Standard des Blocks |

`block` ist auch bei genutztem `blocks` weiterhin auf oberster Ebene der Datei Pflicht; der erste Eintrag ist ein guter Wert dafür.

### Ziele für `replace`

`replace` ist eine Liste, und jeder Eintrag hat eine von zwei Formen.

```json
{
  "replace": [
    "minecraft:stone",
    { "block": "minecraft:oak_log", "properties": { "axis": "y" } }
  ]
}
```

| Form | Beispiel | Worauf sie passt |
| --- | --- | --- |
| Name | `"minecraft:stone"` | Jeder Zustand dieses Blocks |
| Objekt | `{ "block": "minecraft:oak_log", "properties": { "axis": "y" } }` | Nur dieser Zustand |

Ein 1.12.2-Name mit Metadaten am Ende, `minecraft:stone:3`, passt auf jeden Zustand des Blocks und sagt das im Log, denn die Blöcke, die Metadaten trugen, sind jetzt eigene Blöcke: schreib `minecraft:diorite`. Nimm `"minecraft:air"`, um in offenem Raum zu generieren.

### Benachbarte Blöcke

`adjacent` nimmt dieselben Formen wie `replace` und legt eine zweite Bedingung darüber: Die Stelle wird nur genommen, wenn mindestens einer der 26 Blöcke, die sie berühren, also Flächen, Kanten und Ecken, auf die Liste passt. Ohne den Eintrag wird nichts geprüft.

```json
{
  "block": "mypack:sulfur_ore",
  "replace": ["minecraft:sandstone"],
  "adjacent": ["minecraft:air"]
}
```

Das setzt Schwefel nur dort in Sandstein, wo er ohnehin schon zu einer Höhle oder zur Oberfläche offen liegt, und lässt vergrabenen Sandstein in Ruhe. Nachbarn in Chunks, die es noch nicht gibt, gelten als nicht passend, statt gelesen zu werden, so löst die Prüfung nie die Generierung eines Chunks aus.

Jede Form hält sich daran, weil es dazugehört zu entscheiden, ob ein einzelner Block genommen werden darf. Eine `geode` nennt ihre Kruste und ihre Füllung getrennt, und diese beiden werden ohne die Prüfung gesetzt.

Ein Eintrag, der nur Blöcke nennt, die nicht registriert sind, wird mit einem Fehler übersprungen, statt überall zu generieren.

### Folgeeinträge

Ein Eintrag in der `then`-Liste eines Worldgen-Eintrags ist ein Name mit Gewicht, oder ein Objekt, wenn dieser Nachfolger eine eigene Richtung braucht.

```json
{
  "then": [
    "mypack:quartz_halo=2",
    "empty=1",
    { "name": "mypack:side_branch", "weight": 1, "spread": 4, "depth": 0 }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `name` | ja | Eintragsname | | Der Worldgen-Eintrag, der aus diesem herauswächst. Ein bloßer Name wird im Namespace dieses Packs gelesen |
| `weight` | nein | int | `1` | Wie oft dieser Nachfolger gegenüber den anderen der Liste gewählt wird |
| `spread` | nein | int, Blöcke | das `thenSpread` des Eintrags | Wie weit die Richtung dieses Nachfolgers seitlich ausschert, nur für diesen einen Eintrag |
| `depth` | nein | int oder Bereich | das `thenDepth` des Eintrags | Wie weit die Richtung nach unten, negativ, oder nach oben neigt, nur für diesen einen Eintrag |

`name=gewicht` ist die Kurzform eines Objekts mit nur diesen beiden, und `empty=gewicht` reiht nichts ein. Weil `spread` und `depth` pro Eintrag gelten, kann eine Liste eine Diamantspitze senkrecht nach unten und einen Zweig zur Seite aus derselben Ader schicken.

## Formen

Ein `shape`-Block mit einem `type`. Schlüssel, die bei einem Typ nicht aufgeführt sind, ignoriert er.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht. Ein Schlüssel, der für einen Typ vermerkt ist, wird nur von diesem Typ gelesen.

```json
{
  "shape": {
    "type": "geode",
    "radius": 6,
    "height": 8,
    "width": 12,
    "plane": "circle",
    "slim": false,
    "hanging": false,
    "taper": "needle",
    "outline": "minecraft:obsidian",
    "fill": "minecraft:glowstone",
    "surface": ["minecraft:grass_block"],
    "seeSky": true,
    "checkStay": true,
    "stackHeight": 1,
    "scatterX": 8,
    "scatterY": 4,
    "scatterZ": 8,
    "log": "mypack:ruby_log",
    "leaves": "mypack:ruby_leaves",
    "vines": false,
    "structure": "mypack:crypt",
    "structures": [
      { "structure": "mypack:crypt", "weight": 3 },
      "mypack:shrine"
    ],
    "integrity": 100,
    "lootTable": "minecraft:chests/simple_dungeon",
    "turns": ["none", { "turn": "half", "weight": 2 }],
    "mirrors": ["none", { "mirror": "leftright", "weight": 2 }],
    "at": [1000, -500],
    "locateAs": "Crypt",
    "field": { "type": "speckle", "spread": 0.15 },
    "threshold": 0.5,
    "fade": 0,
    "pattern": "banded",
    "density": 0.8,
    "rich": "mypack:rich_ruby_ore",
    "poor": "mypack:poor_ruby_ore",
    "rarity": 400,
    "rarityIsPerChunk": false
  }
}
```

```json
{
  "shape": { "type": "tree", "log": "mypack:ruby_log", "leaves": "mypack:ruby_leaves", "height": { "min": 4, "max": 7 }, "surface": ["minecraft:grass_block"] }
}
```

Ein `tree` ohne `log` oder `leaves` generiert nichts und sagt das im Log. Nennst du ein `structure` oder mehrere unter `structures`, wird an jeder Stelle diese Vorlage gesetzt, statt einen Baum zu generieren, und dann braucht es weder `log` noch `leaves`; ein Baum aus einer Vorlage liest `turns`, `mirrors`, `integrity`, `lootTable` und `locateAs` genau wie ein `imprint`.

| Typ | Was daraus wird |
| --- | --- |
| `cluster` | Der übliche Klumpen, eine Erzader. Nutzt `size` |
| `largevein` | Eine lange, mäandernde Ader mit Abzweigungen. Nutzt `size` |
| `plate` | Eine flache Scheibe |
| `geode` | Eine hohle Tasche mit Kruste |
| `decoration` | Streuung an der Oberfläche, etwa Blumen oder Pilze. Nutzt `size` |
| `tree` | Ein ganzer Baum |
| `vines` | Ranken an dem, was schon da ist. Nutzt `size` |
| `basin` | Eine Schüssel, die zur Mitte hin tiefer wird |
| `spire` | Eine sich verjüngende Säule |
| `nodule` | Eine grobe Kugel |
| `vent` | Eine schmale Säule, die aufhört, sobald sie auf etwas trifft |
| `imprint` | Eine deiner `.nbt`-Vorlagen. Eine, die in einen Chunk passt, wird so verschoben, dass sie ganz im gerade gebauten Chunk landet, statt in einen Nachbarn zu ragen, den es noch nicht gibt, egal, wie herum sie gedreht ist; eine, die größer als ein Chunk ist, wird nur dort gesetzt, wo der Boden ringsum schon existiert |
| `belt` | Ein Cluster über mehrere Chunks hinweg, für Gesteinsregionen |
| `field` | Adern, die für jeden Block auf einmal ermittelt werden, mit derselben Form wie Härtegruppen |
| `vein` | Eine Lagerstätte, als geseedetes Rauschfeld um einen Ursprung errechnet: jeder Chunk schreibt seine eigene Scheibe jeder Ader, deren Reichweite von 24 Blöcken ihn berührt, also kaskadiert nichts, und `/rdplserver vein` kann sagen, wo eine Ader liegen wird, bevor das Land gebaut ist. Nutzt `size`, `attempts`, `rarity` und das Höhenband; `pattern` wählt das Aussehen |

| Schlüssel | Genutzt von | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `type` | allen | eine der Formen oben | `cluster` | Welche Form |
| `radius` | plate, geode, basin, spire, nodule, vent | int oder Bereich | `6` | Wie breit sie ist |
| `height` | plate, geode, basin, spire, vent, tree | int oder Bereich | `1`, `8` bei geode, `5` bei tree | Wie hoch oder wie dick sie ist |
| `width` | geode | int oder Bereich | `12` | Die Gesamtspanne der Tasche |
| `plane` | plate, basin, spire, vent | `circle`, `square` | `circle` | Ihre Grundfläche |
| `slim` | plate, largevein, nodule | boolean | `false` | plate: eine Schicht dünner. largevein: Abzweigungen aus einzelnen Blöcken. nodule: hohle Schale |
| `hanging` | spire, vent | boolean | `false` | Wächst von einer Decke nach unten statt von einem Boden nach oben |
| `taper` | spire | `straight`, `bell`, `needle` | `straight` | Wie die Breite zur Spitze hin abnimmt. `straight` verjüngt gleichmäßig, `bell` bleibt unten breit und fällt dann ab, `needle` wird sofort dünn und läuft lang aus |
| `outline` | geode | Blockname | keiner | Der Block der Kruste |
| `fill` | geode | Blockname | keiner | Was die Mitte füllt. Weggelassen bleibt die Mitte hohl |
| `surface` | decoration, tree | Liste von Blocknamen | keine | Worauf sie sitzt |
| `seeSky` | decoration | boolean | `true` | Nur dort setzen, wo der Himmel zu sehen ist |
| `checkStay` | decoration | boolean | `true` | Nur dort setzen, wo der Block auch bestehen bliebe |
| `stackHeight` | decoration | int oder Bereich | `1` | Wie viele übereinandergestapelt werden |
| `scatterX` | decoration, tree | int | `8` | Wie weit sie seitlich streut |
| `scatterY` | decoration, tree | int | `4` | Wie weit sie senkrecht streut |
| `scatterZ` | decoration, tree | int | `8` | Wie weit sie seitlich streut |
| `log` | tree | Blockname | keiner | Der Stammblock |
| `leaves` | tree | Blockname | keiner | Der Blätterblock |
| `vines` | tree | boolean | `false` | Ranken von den Blättern hängen lassen |
| `structure` | imprint, tree | `namespace:name` | keine | Die Vorlage, die gesetzt wird |
| `integrity` | imprint, tree | 1 bis 100 | `100` | Prozentsatz der Blöcke der Vorlage, die tatsächlich erscheinen |
| `lootTable` | imprint, tree | `namespace:pfad` | keine | Die Beutetabelle, aus der jede Truhe in der gesetzten Vorlage beim ersten Öffnen gefüllt wird, und jeder andere Behälter, der eine annimmt, eine Shulkerkiste oder die Kiste eines Mods darunter. Gilt für `structure` und jeden Eintrag von `structures`; jede Truhe würfelt mit eigenem Seed |
| `structures` | imprint, tree | Liste | keine | Mehrere Vorlagen zur Auswahl, eine davon wird jedes Mal gesetzt. Jeder Eintrag ist `{ "structure": "namespace:name", "weight": 3 }` oder ein bloßer Name für gleiche Chancen. Überschreibt `structure` |
| `turns` | imprint, tree | Liste | beliebig | Wie herum sie gesetzt werden darf: `none`, `quarter`, `half`, `threequarter`. Einträge dürfen ein `weight` tragen. Weggelassen sind alle vier gleich wahrscheinlich |
| `mirrors` | imprint, tree | Liste | keine | Sie zusätzlich spiegeln: `none`, `leftright`, `frontback`, mit optionalem `weight`. Ein Eintrag mit eigenem Gewicht wird `{ "mirror": "leftright", "weight": 2 }` geschrieben, ein `turns`-Eintrag genauso mit `turn` |
| `at` | imprint | zwei Ints, x und z | keine | Genau einmal an diesen Blockkoordinaten an der Oberfläche setzen, wenn dieser Chunk generiert, statt nach Zufall. Siehe [Strukturen an genauen Stellen](#strukturen-an-genauen-stellen) |
| `locateAs` | imprint, tree | string | keiner | Jede Struktur, die dieser Eintrag setzt, unter diesem Namen eintragen, sodass `/rdplserver locate <name>` die nächste findet. Siehe [Gesetzte Strukturen finden](#platzierte-strukturen-finden) |
| `field` | field | Objekt | `{ "type": "speckle" }` | Wie das Feld errechnet wird. Dieselben Schlüssel wie das `field` einer Härtegruppe, beschrieben unter [Das Feld](#das-feld): `speckle` mit `chances` und `spread`, oder `seeded` mit `cell`, `seeds`, `reach`, `arms` und `armReach` |
| `threshold` | field, vein | 0.0 bis 1.0 | `0.5` (`0.4` bei vein) | Wie stark das Feld an einem Block sein muss, bevor dort gesetzt wird. Niedriger füllt mehr |
| `fade` | field | int | `0` | Lässt das Band oben ausfransen statt glatt zu enden: über die obersten so vielen Blöcke des Höhenbereichs sinkt die Chance jedes Blocks Stufe für Stufe, derselbe Look, den die Engine `deepStone` am Übergang zur Welt darüber gibt |
| `rarity` | alle | int | keiner (`400` für belt) | Eine Platzierung pro so viele Chunks. Bei einem belt bestimmt das den Abstand der Gürtel; bei jeder anderen Form lässt es nur einen Chunk von so vielen überhaupt seine `attempts` würfeln. `field` ignoriert es |
| `rarityIsPerChunk` | alle | boolean | `false` | Macht aus `rarity` stattdessen die Anzahl Platzierungen pro Chunk |
| `pattern` | vein | `default`, `banded` oder `tube` | `default` | Das Aussehen der Lagerstätte: ein verzerrter Klumpen, alle paar Blöcke gestapelte Schichten oder hohle Röhren, die sich durchs Gestein winden |
| `density` | vein | 0.0 bis 1.0 | `1.0` | Der Anteil der passenden Blöcke, die wirklich gesetzt werden, eine Münze pro Block |
| `rich` | vein | Blockname | keiner | Gesetzt im obersten Fünftel des Feldbereichs über `threshold`, dem Herz der Lagerstätte, statt der Blöcke des Eintrags |
| `poor` | vein | Blockname | keiner | Gesetzt in den unteren zwei Fünfteln dieses Bereichs, dem Rand, statt der Blöcke des Eintrags; die Mitte sind die Blöcke des Eintrags selbst. Eine weggelassene Stufe setzt dort die Blöcke des Eintrags |

Eine `field`-Ader ist die eine Form, die du beschreibst statt auswählst. Sie nutzt dasselbe Gitter wie die Härtegruppen: `seeded` mit ein paar Armen ergibt Knoten mit Ranken, die zu ihren Nachbarn hinüberreichen, also eine Ader statt eines Klumpens, und `threshold` entscheidet, wie viel davon fest genug zum Setzen ist:

```json
{
  "shape": {
    "type": "field",
    "threshold": 0.4,
    "field": { "type": "seeded", "cell": 10, "reach": 6.0, "arms": 3, "armReach": 5.0 }
  }
}
```

Diese Schlüssel kommen in ein eigenes `field`-Objekt, nicht neben `type`, denn `type` sagt an der Form ja bereits `field`.

Für eine Form, die kein eingebauter Typ abdeckt, ist `imprint` der Weg: Bau sie als `.nbt`-Vorlage und setz diese, mit `structures` zum Abwechseln, `turns` und `mirrors` zum Drehen und `integrity`, um sie rauer aufzulösen als die Datei, die du gezeichnet hast.

### Gürtel

Ein `belt` ist eine Kugel, die weit größer als ein Chunk ist, gedacht für Gesteinsregionen statt für Erzadern. Sein `radius` ist die Größe der Kugel, und jeder Chunk rechnet für sich selbst aus, wo die Kugeln in seiner Nähe anfangen, aus dem Welt-Seed und dem Namen des Eintrags, ein Gürtel kommt also vollständig heraus, egal in welcher Reihenfolge die Chunks generiert werden, und es wird nie etwas in einen Nachbar-Chunk geschrieben.

```json
{
  "shape": { "type": "belt", "radius": 32, "rarity": 400 }
}
```

Ein Gürtel ignoriert `attempts` und `spread`, weil er pro Chunk statt pro Versuch gesetzt wird. `minHeight` und `maxHeight` sind das Band, in dem die Mittelpunkte liegen, und die Kugel reicht um `radius` über dieses Band hinaus. `replace` entscheidet, was er frisst, und `biomes` sowie die Grenzen für Temperatur und Niederschlag werden am Mittelpunkt geprüft, ein Gürtel erscheint also entweder ganz oder gar nicht, statt an einer Biomgrenze abgeschnitten zu werden.

Der Aufwand wächst mit der dritten Potenz von `radius`, und ein niedriger `rarity`-Wert vervielfacht ihn, fang also bei den Standardwerten an und erhöhe den Radius langsam.

### Felder

Ein `field` setzt nichts an einem Punkt und alles auf einmal. Statt eine Stelle zu wählen und darum herum eine Form zu bauen, stellt es jedem Block im Chunk zwischen `minHeight` und `maxHeight` eine Frage und setzt dort, wo die Antwort mindestens `threshold` ist. Es ist dieselbe Frage, die Härtegruppen stellen, beide beschreiben also dieselben Adern, und ein Pack kann eine Gruppe und einen Eintrag bauen, die zusammenpassen.

```json
{
  "block": "mypack:sulfur_ore",
  "replace": ["minecraft:stone"],
  "minHeight": 8,
  "maxHeight": 48,
  "shape": {
    "type": "field",
    "threshold": 0.6,
    "field": { "type": "speckle", "spread": 0.15 }
  }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er tut |
| --- | --- | --- | --- | --- |
| `threshold` | nein | 0.0 bis 1.0 | `0.5` | Wie stark das Feld sein muss, bevor ein Block gesetzt wird |
| `field` | ja | Objekt | keiner | Dasselbe Objekt wie bei einer Härtegruppe, mit denselben Arten `speckle` und `seeded` |

Ein niedriger `threshold` nimmt fast das ganze Feld und gibt breite Bänder, ein hoher nimmt nur die Mitte jedes Nestes und gibt kleine verstreute Taschen. Mit `speckle` bekommst du viele feine Sprenkel, mit `seeded` rundere Nester oder, sobald es Arme hat, Knoten mit Ranken, die sich einander entgegenstrecken.

Wie ein Gürtel übergeht ein Feld `attempts` und `spread`, da es je Chunk statt je Versuch gefragt wird, und es schreibt nie in einen Nachbar-Chunk. Es ergibt sich aus dem Welt-Seed und dem eigenen Namen des Eintrags, derselbe Seed gibt also immer dieselben Adern, und zwei Einträge mit verschiedenen Namen decken sich nie. `replace`, `adjacent`, `biomes` und die Klimagrenzen gelten wie sonst auch.

## Strukturkarten

Eine Strukturkarte setzt Vorlagen auf einem Raster zu einem benannten Bauwerk zusammen, weit über die 48-Block-Grenze einer einzelnen `.nbt`-Datei hinaus. Jede Ebene wird als Zeilen einzelner Zeichen gezeichnet, ein Zeichen je Zelle, und stapelt sich eine Zellhöhe über die Ebene davor. Höchstens 8 Ebenen zu 8 mal 8 Zellen, was bei der Standardzelle von 32 eine Kantenlänge von 256 Blöcken ergibt.

`<namespace>/structuremaps/*.json`

```json
{
  "cell": 32,
  "ground": 0,
  "spacing": 64,
  "chance": 25,
  "layers": [
    {
      "palette": { "a": "mypack:keep_base", "b": ["mypack:wall=3", "mypack:wall_broken=1"] },
      "map": ["aba",
              "b.b",
              "aba"]
    },
    {
      "palette": { "a": "mypack:keep_top" },
      "map": [".a.",
              "...",
              ".a."]
    }
  ]
}
```

| Einstellung | Typ | Standard | Was sie bewirkt |
| --- | --- | --- | --- |
| `cell` | Zahl | `32` | Der Rasterabstand in Blöcken, bis 48. Eine Vorlage, die kleiner ist als die Zelle, sitzt in der Zellecke, sodass Stücke in voller Größe nahtlos aneinanderstoßen |
| `ground` | Zahl | `0` | Welche Ebene auf der Geländeoberfläche aufsetzt. Ebenen davor graben sich ein, so bekommt ein Bauwerk Keller |
| `at` | zwei Zahlen | keiner | Setzt eine Kopie an genaue Blockkoordinaten, so wie `structureAt` eine Struktur festlegt |
| `spacing` | Zahl | `0` | Verstreut Kopien auf einem Raster in diesem Chunkabstand, versetzt aus dem Weltseed. `0` verstreut keine, eine Karte nur mit `at` baut also genau einmal |
| `chance` | Zahl | `100` | Der Prozentanteil der Rasterplätze, die eine Kopie bauen |
| `dimensions` | Liste von Dimensions-Ids | alle | Wo die Karte bauen darf |
| `layers` | Liste | keine | Die Ebenen, von unten nach oben, jede mit `palette` und `map` |

Eine Palette nennt Vorlagen per Registry-Schlüssel aus dem `<namespace>/structures/` eines Packs.

| Wert | Was er bewirkt |
| --- | --- |
| `"a": "mypack:keep"` | Jede `a`-Zelle dieser Ebene setzt diese Vorlage |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | Jede `a`-Zelle lost die Liste nach Gewicht aus, aus dem Weltseed und dem Platz der Zelle, zwei Kopien des Bauwerks unterscheiden sich, aber dieselbe Welt baut immer dasselbe |
| `.` | Eine leere Zelle, nichts wird gesetzt |

Jede Kopie lost eine der vier Ausrichtungen aus dem Weltseed aus, und das ganze Bauwerk dreht sich gemeinsam, Vorlagen eingeschlossen, Mauern, die sich über Zellen hinweg treffen, treffen sich also weiterhin; eine Karte dreht sich, spiegelt sich aber nie. Die Bodenebene setzt auf der abgetasteten Geländeoberfläche unter der Mitte des Bauwerks auf, und die ganze Karte teilt sich diese eine Höhe. Eine verstreute Karte ist für das Spiel eine eigene Struktur, gesetzt über ein Structure-Set, das für dich geschrieben wird, jeder Chunk baut also nur seinen eigenen Ausschnitt des Rasters, und ein Bauwerk über viele Chunks entsteht ohne kaskadierende Generierung, in welcher Reihenfolge die Chunks auch laden. Ein [Dorfgrundstück](#dorfgrundstücke) vom Typ `template` kann in seinem `structure` ebenfalls eine Karte nennen, die Komposition wird dann zum Stadtgebäude.

## Stadtpläne

Ein Stadtplan zeichnet den Straßenplan einer Stadt auf ein Raster, ein Zeichen je Zelle, und die Stadt wird nach der Zeichnung angelegt, statt eine auszuwürfeln. Straßen, Plätze und Grundstücke werden dieselben Teile, die eine gewürfelte Stadt verwendet, also gelten jede Straßenoption, Brücke, Tunnel, U-Bahn, Abwasserkanal, Laterne und jedes Platzmittelstück unverändert. Die Weltvorlage nennt den Plan in `villageLayout`.

`<namespace>/citymaps/*.json`

```json
{
  "cell": 48,
  "palette": {
    "#": "street",
    "+": "plaza",
    "a": "alley",
    "T": ["mypack:tower_blue=1", "mypack:tower_gray=1"],
    "B": "mypack:block",
    "s": ["mypack:shop_blue=2", "mypack:shop_gray=1"],
    "g": "grow"
  },
  "map": [
    "sss#BBB#sss",
    "sgs#BgB#sgs",
    "###+###+###",
    "BBB#TTT#BBB",
    "BgB#TgT#BgB",
    "###+###+###",
    "sss#BBB#sss"
  ]
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `cell` | Zahl | `48` | Das Rastermaß in Blöcken, 8 bis 128. Straßen laufen in der Breite des Packs durch die Mitte ihrer Zellen, Grundstücke sitzen mittig in ihren, eine Zelle braucht also das breiteste Grundstück plus Raum zur Straße hin |
| `palette` | Objekt | keins | Was jedes Zeichen anlegt, unten aufgeführt |
| `map` | Liste | keine | Die Zeilen, bis zu 64 mal 64 Zellen. Eine kürzere Zeile ist hinter ihrem Ende offen |

| Wert | Was er tut |
| --- | --- |
| `"#": "street"` | Ein Lauf von Straßenzellen entlang einer Zeile oder Spalte wird eine Straße in der Breite des Packs. Wo ein Zeilenlauf einen Spaltenlauf kreuzt, wird die Kreuzung wie jede andere gestaltet. Eine einzelne Straßenzelle ohne Lauf in einer Achse wird als kurzer Stummel entlang der Zeile angelegt |
| `"+": "plaza"` | Ein Platz mit seinem Mittelstück. Läufe gehen durch Platzzellen hindurch, Straßen treffen sich also am Platz, und ein Platz auf einer Kreuzung stellt sein `villageWellStructure`-Mittelstück wie einen Kreisverkehr mitten auf die Kreuzung. Der erste Platz in der Datei ist das Zentrum der Stadt selbst, der den Plan dort festmacht, wo die Stadt gegründet wird; ein Plan ohne einen wird dort zentriert |
| `"a": "alley"` | Ein schmaler Lauf. Gebäude stehen daran, aber er verbindet nichts, die Gassenregel wie gewohnt |
| `"T": "mypack:tower"` | Eine Grundstückszelle, angelegt aus dieser Grundstücksdefinition, mittig in der Zelle und zur nächsten Straße gewandt |
| `"T": ["mypack:a=3", "mypack:b=1"]` | Dasselbe, nach Gewicht aus dem Weltseed und dem Platz der Zelle ausgelost, dieselbe Welt legt dort also immer dasselbe Grundstück an |
| `"g": "grow"` | Dem gewürfelten Plan überlassen, der solche Zellen füllt und sich vom Plan aus nach außen ausbreitet |
| `.` oder `open` | Offener Boden, nichts angelegt |

Jeder Plan lost eine der vier Richtungen aus dem Weltseed aus und dreht sich als Ganzes, ein Plan liest sich also von jeder Seite gleich. Straßen werden zuerst angelegt, ein Grundstück, das eine Straße oder ein anderes Grundstück überlappen würde, bleibt mit einer Zeile im Log offen, und ein Grundstücksname, den kein Pack liefert, lässt seine Zelle genauso offen. Der Plan ändert nicht, wie die Teile gestaltet werden: die Straßenschlüssel, `villageBlocks`, die Laternen und das Platzmittelstück gelten wie für eine gewürfelte Stadt.

## Verteilung

Ein `spread`-Block mit einem `type`.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht. Ein Schlüssel, der für einen Typ vermerkt ist, wird nur von diesem Typ gelesen.

```json
{
  "spread": {
    "type": "centered",
    "center": 32,
    "range": 12,
    "smoothness": 3,
    "veinHeight": 24,
    "veinDiameter": 12,
    "verticalDensity": 16,
    "horizontalDensity": 32,
    "offsetMin": 0,
    "offsetMax": 2,
    "ceiling": false
  }
}
```

| Typ | Wohin er die Dinge setzt |
| --- | --- |
| `even` | Irgendwo zwischen den Höhen, gleichmäßig. Der Standard |
| `centered` | Zu einer Höhe hin gewichtet, mit dem Abstand ausdünnend |
| `sprawl` | Fraktale Adern über einen Höhenbereich |
| `terrain` | Der Oberfläche folgend |
| `cavern` | Auf Höhlenböden oder an Höhlendecken |
| `submerged` | Unter Wasser oder einer anderen Flüssigkeit |

| Schlüssel | Genutzt von | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `type` | allen | eine der Verteilungen oben | `even` | Welche Verteilung |
| `center` | centered | int | Mitte des Höhenbereichs | Die Höhe, um die es sich sammelt |
| `range` | centered | int | halber Höhenbereich | Wie weit es von dieser Höhe reicht |
| `smoothness` | centered | 1 bis 8 | `2` | Wie viele Würfe gemittelt werden. Höher heißt engeres Band |
| `veinHeight` | sprawl | int | der Höhenbereich | Wie hoch eine Ader ist |
| `veinDiameter` | sprawl | int | `12` | Wie breit eine Ader ist |
| `verticalDensity` | sprawl | 1 bis 100 | `16` | Wie dicht sie senkrecht ist |
| `horizontalDensity` | sprawl | 1 bis 100 | `32` | Wie dicht sie waagerecht ist |
| `offsetMin` | terrain | int | `0` | Kleinster Abstand zur Oberfläche |
| `offsetMax` | terrain | int | `offsetMin` | Größter Abstand zur Oberfläche |
| `ceiling` | cavern | boolean | `false` | An die Höhlendecke hängen statt auf den Boden setzen |

## Retrogen

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "retrogen": true,
    "adoptExistingChunks": false
  }
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `retrogen` | boolean | `false` | Holt Chunks, die vor einem Eintrag gespeichert wurden, bei jedem mit `"retrogen": true` markierten Worldgen-Eintrag nach. Aus bleiben schon vorhandene Chunks unangetastet. Chunks werden so oder so beim Generieren markiert, ein späteres Einschalten berührt also nur Chunks, die älter sind als das Pack |
| `adoptExistingChunks` | boolean | `false` | Was beim ersten Sehen eines alten Chunks passiert: an wird er so gestempelt, als hätte dieses Pack ihn schon generiert, und nie nachgeholt; aus wird er wie jeder andere nachgeholt. Um eine bestehende Welt zu füllen, `retrogen` an und dies aus |

Ein Eintrag mit `"retrogen": true` wird auch in Chunks generiert, die gespeichert wurden, bevor du ihn hinzugefügt hast. Jeder Chunk merkt sich, was er schon bekommen hat, nichts wird also zweimal gemacht.

Das Flag am Eintrag markiert ihn nur als infrage kommend. Das Nachholen selbst schaltet die Einstellung `retrogen` ein, die ein Pack in seinem `settings`-Block oder ein Spieler in der Config setzen kann, und sie ist standardmäßig aus. Daneben entscheidet `adoptExistingChunks`, was beim ersten Sehen eines alten Chunks passiert: an wird der Chunk so gestempelt, als hätte dieses Pack ihn schon generiert, und nie nachgeholt; aus wird er wie jeder andere nachgeholt. `retrogen` einzuschalten, während auch `adoptExistingChunks` an ist, bewirkt nichts, weil jeder alte Chunk abgehakt wird, bevor er in die Warteschlange kommen kann. Um eine bestehende Welt zu füllen, setzt du `retrogen` an und `adoptExistingChunks` aus, beides zusammen. `retrogenChunksPerTick` in der Config, Standard `2`, ist die Zahl der alten Chunks, die je Tick nachgeholt werden.

```json
{
  "block": "mypack:ruby_ore",
  "size": 8,
  "attempts": 12,
  "minHeight": 8,
  "maxHeight": 48,
  "retrogen": true,
  "retrogenKey": "ruby_v1"
}
```

`retrogenKey` in der Config zu ändern macht jeden Chunk wieder infrage kommend, was die neuen Adern über die alten legt und die Dichte damit verdoppelt. Das ist Absicht, und genau deshalb wird der Schlüssel von Hand gesetzt.

---

## Vorgenerierung

Das Land einer Welt im Voraus bauen, damit niemand beim Spielen Chunks generiert: kein Chunk-Lag, eine bekannte Größe auf der Platte und ein einziges Warten am Anfang statt einer ruckelnden ersten Stunde.

Die ersten 12 Chunks um den Spawn werden immer in die Hand genommen, ganz gleich was ein Pack oder die Config sagt, denn genau so viel baut das Spiel selbst, bevor jemand beitritt. `pregenOnNewWorld` legt fest, wie viel weiter gereicht wird, und der Befehl startet einen Lauf von Hand.

`/rdplserver pregen <radius>` baut jeden Chunk innerhalb so vieler Chunks um die Stelle, an der er ausgeführt wird. `status` sagt, wie weit es ist, und `stop` beendet es. Das Land wird beim eigenen Chunk-System des Spiels angefordert, `pregenChunksInFlight` Chunks auf einmal in einer quadratischen Spirale von der Mitte aus, und kommt beleuchtet und fertig zurück, es gibt also keinen Beleuchtungsdurchgang, der danach zu laufen hätte.

Während ein Durchlauf läuft, wird jeder festgehalten: zum Zuschauer gemacht, an Ort und Stelle gehalten, mit einer pulsierenden Zeile mitten im Bild und dem Fortschritt in der Aktionsleiste, der Tag ringsum angehalten und ferne Kreaturen eingefroren. Der Modus, in dem jeder Spieler angekommen ist, wird beim Festhalten auf den Spieler geschrieben, ein Speicherstand mitten im Durchlauf, ein Absturz oder ein erneuter Beitritt lässt also nie jemanden als Zuschauer zurück; am Ende des Durchlaufs gibt er genau den Modus zurück, den er genommen hat, oder den `worldGameMode` des Packs, wenn einer gesetzt ist. Ein Client mit dem Mod sieht die Sicht während des Haltens vernebelt und danach das Logo einblenden; ein Vanilla-Client sieht das schlichte Halten. Wie weit jede Dimension gebaut wurde, wird in der Welt gespeichert, eine fertige Welt wird also nie noch einmal gebaut.

In einem Pack stehen diese im `settings`-Block einer [Weltvorlage](#weltvorlagen), wie jeder andere `chunks`-Schlüssel auch. Hier alle zusammen, mit `pregenBorderLimit` als einzigem Fehlenden, weil nur die Config ihn hält:

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "pregenOnNewWorld": 63,
    "pregenDimensions": ["minecraft:overworld", "minecraft:the_nether"],
    "pregenAllDimensions": false,
    "pregenDimensionsWhenEntered": ["minecraft:the_end"],
    "pregenToBorder": false,
    "pregenResume": true,
    "pregenChunksInFlight": 32,
    "pregenRunningSays": "Building your world, %d%% done",
    "pregenFinishedSays": "Your world is ready",
    "pregenStoppedSays": "World building stopped",
    "pregenSpectatingSays": "Spectating until the world is ready",
    "pregenLogo": "center",
    "pregenBackup": true,
    "pregenBackupSays": "Pack requested world backup",
    "resetSays": "Pack requested map reset",
    "resetSendsTo": "spawn",
    "resetRuns": "",
    "resetClearsEntities": true,
    "resetClearsScores": true,
    "spawnChunkRadius": 2,
    "welcomeSays": ["Welcome to Ruby World!", "minecraft:the_nether=Welcome to the Nether!"],
    "saysCard": true,
    "saysIcon": "minecraft:compass",
    "saysColor": "1E2630",
    "saysImage": "rubyworld:textures/gui/card.png"
  }
}
```

| Schlüssel | Was er macht | Warum du ihn setzen würdest |
| --- | --- | --- |
| `pregenOnNewWorld` | Radius in Chunks, der um den Spawn gebaut wird, bevor jemand spielt. 12 ist die Untergrenze, und 0 meint diese Untergrenze statt gar nichts, denn 12 Chunks um den Spawn baut das Spiel ohnehin von sich aus. Höher setzen, um weiter zu reichen als das Spiel | Legt fest, wie weit ein Pack über den Boden hinausreicht, den das Spiel ohnehin baut |
| `pregenDimensions` | Welche Dimensionen gebaut werden, nach Id, der Reihe nach, jede um ihren eigenen Spawn | Den Nether, das Ende oder deine eigenen Dimensionen dazunehmen |
| `pregenAllDimensions` | Jede Dimension, die der Server hält, statt einer Liste, die Oberwelt zuerst und der Rest in Id-Reihenfolge | Packs mit vielen Dimensionen. Die Dimensionen jedes Mods zählen mit, achte also auf die Größe |
| `pregenDimensionsWhenEntered` | Diese werden gebaut, wenn zum ersten Mal jemand einen Fuß hineinsetzt, und halten dabei wieder alle fest, bis es fertig ist | Dimensionen, die die meisten Spieler nie besuchen; wer nie hingeht, zahlt nichts |
| `pregenToBorder` | Füllt jede Dimension bis zu ihrer Weltgrenze statt bis zu einem Radius, zentriert auf die Grenze | Begrenzte Welten |
| `pregenBorderLimit` | Wie weit eine Grenze reichen darf, in Chunks je Richtung, bevor der Durchlauf abgelehnt wird. Nur Config, nie ein Pack-Schlüssel | Ein Schutz gegen einen ausufernden Durchlauf; erhöhe ihn nur, wenn du weißt, wie viel Zeit und Plattenplatz du damit erlaubst |
| `pregenResume` | Ein gestoppter oder unterbrochener Durchlauf macht dort weiter, wo er aufgehört hat. Dimension, Mittelpunkt und Radius des Durchlaufs werden beim Start in den Spielstand geschrieben und der bisherige Stand alle zehn Sekunden, ein Absturz, ein Stromausfall oder ein Beenden mitten im Durchlauf setzen beim nächsten Laden also auf etwa zehn Sekunden genau dort wieder an, wo sie gestorben sind. Ein absichtlich gestoppter Durchlauf, per Befehl oder durch den Stillstand-Watchdog, bleibt gestoppt | Lange Durchläufe auf Servern; kleine Durchläufe starten auch ohne das billig neu |
| `pregenChunksInFlight` | Wie viele Chunks der Durchlauf beim Spiel auf einmal anfordert. Mehr hält die Generierungs-Threads beschäftigter und den Server weniger ansprechbar für die, die gehalten zusehen | Auf einem leeren Server hoch, auf einem, auf dem Leute spielen, runter |
| `pregenRunningSays`, `pregenFinishedSays`, `pregenStoppedSays` | Die Nachrichten für die einzelnen Phasen. Die erste darf `%d` für den Prozentwert und dahinter `%s` für den Namen der Dimension enthalten, oder `%1$d` und `%2$s`, um sie in beliebiger Reihenfolge zu setzen. Auf ihren Standardwerten sprechen sie die Sprache jedes Spielers | Formulier sie im Ton deines Packs, nenne die Dimension, wenn mehrere gebaut werden, oder stell sie stumm |
| `pregenSpectatingSays` | Die Haltezeile mitten im Bild, während Land gebaut wird. Auf dem Standardwert spricht sie die Sprache jedes Spielers; leer zeigt nichts | Halte sie unter etwa fünfunddreißig Zeichen, sonst schneiden kleine Fenster sie ab |
| `pregenLogo` | Wo das Logo steht, wenn die Vorgenerierung fertig ist: `left`, `center` oder `right`, über dem Text in der Bildmitte, ein paar Sekunden lang, dann blendet es mit dem Nebel aus | Es wird immer gezeigt; ein unbekanntes Wort gilt als `center` |
| `pregenBackup` | Kopiert die Welt in eine unberührte Sicherung, sobald die Vorgenerierung fertig ist und die Spieler noch gehalten werden. Die Generierung wird damit einmal bezahlt: ein späterer Reset oder eine neue Welt mit demselben Pack und Seed stellt die Kopie wieder her, statt erneut zu generieren, was weit schneller ist als zweimal vorzugenerieren. Die Kopie liegt außerhalb des Spielstands, unter `rdpl-pristine/<welt>` daneben, damit die Sicherungen anderer Mods sie nicht mit einsammeln und sie nicht in einem von ihnen verwalteten Ordner auftaucht | `false` |
| `pregenBackupSays` | Die Zeile in Bildschirmmitte, die Spielern während dieser Kopie gezeigt wird, mit dem Prozentsatz dahinter. Leer zeigt nichts und die Kopie wird still gemacht | `Pack requested world backup` |
| `resetSays` | Die Zeile in Bildschirmmitte, die Spielern gezeigt wird, während `/rdplserver reset` oder ein Rundenende die Karte zurücksetzt. Leer setzt still zurück | `Pack requested map reset` |
| `resetSendsTo` | Wohin ein Reset die Spieler setzt: `spawn`, eine Position als `x,y,z`, oder `dimension:x,y,z`, um sie in eine andere Welt zu schicken, womit ein Reset alle in eine Lobby statt zurück in die Arena bringt | `spawn` |
| `resetRuns` | Eine Funktion, die läuft, nachdem ein Reset die Karte geräumt hat, benannt `namespace:pfad`. Sie baut die Arena wieder auf, denn ein Pack, das seine Karte aus einer Funktion gemacht hat, kann sie einfach ein zweites Mal laufen lassen. Leer führt nichts aus | leer |
| `resetClearsEntities` | Entfernt jede Entity, die kein Spieler ist. Mobs, liegende Items und Erfahrung verschwinden, was die Karte so zurücklässt, wie sie begann | `true` |
| `resetClearsScores` | Setzt jedes Ziel, das das Pack führt, wieder auf nichts, sodass eine neue Partie bei null beginnt. Die Teams selbst bleiben | `true` |
| `spawnChunkRadius` | Wie weit vom Spawnpunkt, in Chunks, Chunks geladen gehalten werden, ob ein Spieler da ist oder nicht. Der Standard ist `2`, das 25 Chunks hält; `-1` lässt den eigenen Wert des Spiels. Auf 1.20.1 setzt es das Spawn-Ticket, das der Server vom Start einer Welt an hält, anstelle der spieleigenen 11; auf 1.21.1 setzt es die Spielregel `spawnChunkRadius`, wenn eine Welt startet. So oder so wird auf beiden dieselbe Zahl Chunks gehalten | Eine Maschine oder eine Farm am Spawn am Laufen halten, oder die Spawn-Chunks mit `0` abschalten |
| `welcomeSays` | Die grüne Begrüßung, gezeigt bei jedem Login und nach der Vorgenerierung. Ein bloßer Eintrag ist die Zeile für überall; ein Eintrag `dimension=nachricht` überschreibt sie für diese Dimension und begrüßt außerdem jede Ankunft dort, z. B. `"minecraft:the_nether=Welcome to the Nether!"`. Eine leere Nachricht nach dem `=` stellt diese Dimension stumm; eine leere Liste zeigt nichts. Auf dem Standardwert spricht sie die Sprache jedes Spielers | Eine bloße Zeile nennt dein Pack; mit Dimensionszeilen gibst du jeder Welt ihr Thema. Halte die Zeilen unter etwa fünfunddreißig Zeichen |
| `saysCard` | Zeigt die Zeilen, die dieser Mod sagt, die Begrüßung, den Fortschritt der Vorgenerierung und die Bedrohungszeilen, als Karte unten rechts statt im Chat. Die Karte gleitet herein, bleibt acht Sekunden und verblasst, und erscheint auch über einem offenen Bildschirm | Schalte es ein, wenn der Chat voll ist oder die Zeilen wie ein Teil der Welt wirken sollen statt wie Geplauder |
| `saysIcon` | Ein Item, das auf der Karte gezeichnet wird, z. B. `minecraft:compass`. Leer zeichnet keines | Gib der Karte das Wappen deines Packs |
| `saysColor` | Die Hintergrundfarbe der Karte als Hex, z. B. `1E2630`. Leer nimmt ein dunkles Schiefergrau | Passe sie an die Palette deines Packs an |
| `saysImage` | Ein PNG aus den Client-Assets des Packs, z. B. `rubyworld:textures/gui/card.png`, über die Karte gestreckt als ihr Hintergrund und über die Farbe gezeichnet. Leer zeichnet keines | Gib der Karte eine gemalte Tafel; halte das Bild breit und flach, es wird auf das gestreckt, was der Text braucht |

Lass ihn vor der Auslieferung einmal selbst durchlaufen, mit dem Radius, den du ausliefern willst, von Anfang bis Ende. Die Zahl der Chunks wächst mit dem Quadrat des Radius: 63 in jede Richtung sind sechzehntausend Chunks, 500 sind über eine Million, der Region-Ordner deiner Testwelt und die tatsächlich verstrichene Zeit sind also die ehrlichen Zahlen, die du den Spielern nennen kannst. Liefere keinen Radius aus, der nie durchgelaufen ist.

# Steuerung

## Die Steuerungsebene

Alles, was Generierung unterbindet oder verändert, ist in Gruppen zusammengefasst, und jede Gruppe hat einen Schlüssel in der Config-Kategorie `control` mit drei Werten:

| Wert | Was er bedeutet |
| --- | --- |
| `default` | Das Pack entscheidet. Die Config-Werte sind der Rückfall |
| `global` | Die Config gewinnt. Pack-Abschnitte werden ignoriert |
| `off` | Die Gruppe ist ganz abgeschaltet, und kein Pack kann sie einschalten |

Die Gruppen sind `ores`, `biomes`, `structures`, `spawning`, `bedrock`, `voidWorld`, `recipes`, `terrain`, `replacements`, `villages`, `entities`, `chunks`, `blastPlaster` und `commands`.

Einstellungen lösen sich in der Reihenfolge **Biom-Abschnitt → Weltvorlage → Config** auf. Der `settings`-Block einer Weltvorlage nutzt dieselben Schlüsselnamen wie die Config, ein Pack setzt sie also genauso, wie du es tun würdest:

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "monsterCap": 40,
    "flatBedrock": true,
    "worldGameMode": "creative",
    "oreWhitelist": ["minecraft", "mypack"],
    "pregenOnNewWorld": 63
  }
}
```

Steht die Steuerung einer Gruppe auf `default`, gewinnen diese Werte, auf `global` werden sie ignoriert, und auf `off` tut die ganze Gruppe nichts, egal was ein Pack sagt. Ein Schlüssel, den eine Vorlage nennt und den nichts liest, wird einmal im Log angemahnt, ebenso ein Schlüssel in einem `biomes`-Abschnitt, der keine Dorfeinstellung ist.

Die Config-Datei ist `config/resourcedatapackloader-common.toml`. Jeder Schlüssel unten trägt dort denselben Namen unter seiner Kategorie, und eine Liste wird als die TOML-Liste geschrieben, die das Config-Format des Spiels nutzt.

## Was jede Gruppe macht

Jede Einstellung unten wird über ihre Gruppe gelesen, der `control`-Schlüssel der Gruppe entscheidet also, ob ein Pack oder die Config das letzte Wort hat. Eine Einstellung, die ein Pack setzen darf, steht im `settings`-Block einer Weltvorlage unter demselben Namen; eine mit **nur Config** wird allein aus der Config gelesen, und ein Pack, das sie schreibt, wird angemahnt und ignoriert. Standardwerte sind die der Config.

### Erze

`control.ores` entscheidet diese Gruppe. Erzgenerierung nach Mod und nach Erztyp sperren.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `blockOres` | boolean | `false` | Jeden Mod, und Minecraft selbst, daran hindern, Erze zu generieren. Nur die Mods in oreWhitelist generieren weiter. Ein Erz ist ein Placed Feature mit ore in seiner Id, was auf Minecraft und die meisten Mods zutrifft |
| `logBlockedOres` | boolean | `true` | Beim ersten Mal protokollieren, wenn ein Mod und ein Erztyp gesperrt werden, damit du siehst, was auf die Whitelist gehört |
| `oreWhitelist` | Liste | `["minecraft"]` | Mod-IDs, die bei eingeschaltetem blockOres Erze generieren dürfen. Erze, die ein Pack definiert, gehören zum Namespace dieses Packs |
| `prospectItems` | Liste | leer | Items, die nach Worldgen-Einträgen der Form vein schürfen, wenn ein schleichender Spieler damit einen Block abbaut, als item=eintrag\|eintrag[,radius in chunks] oder item=*[,radius], z. B. minecraft:compass=iron_vein\|coal_seam oder mypack:rod=*,12. Die Lesung nennt das Erz und eine Himmelsrichtung |
| `prospectItemsAreBlacklist` | boolean | `false` | An sind die hinter einem Item in prospectItems genannten Einträge die, die es NICHT liest, und jeder andere vein-förmige Eintrag wird gelesen |
| `prospectDrops` | boolean | `false` | Ob ein im Schürfmodus abgebauter Block etwas droppt. Aus wird die Probe zerstört: keine Drops, keine Erfahrung |
| `prospectSlow` | int, 1 bis 100 | `2` | Um wie viel langsamer ein Block im Schürfmodus bricht |
| `prospectWear` | int, 2 bis 1000 | `2` | Wie viel Haltbarkeit ein Schürfabbau das Item kostet, mindestens 2 |
| `oreTypes` | Liste | leer | Erztypen, für die das gilt, wer auch immer sie generiert und was die Whitelist auch sagt. Bekannte Typen: COAL, IRON, COPPER, GOLD, REDSTONE, DIAMOND, LAPIS, EMERALD, QUARTZ, DIRT, GRAVEL, DIORITE, GRANITE, ANDESITE, TUFF, CLAY, SILVERFISH, CUSTOM für jedes andere Erz |
| `oreTypesAreBlacklist` | boolean | `true` | An werden die oreTypes gesperrt. Aus generieren nur die oreTypes |
| `blockOreDimensions` | Liste | leer | Dimensionen, in denen die Erzsperre gilt, nach Id wie minecraft:the_nether; gelesen als die Biom-Tags von Oberwelt, Nether und Ende. Leer heißt jede Dimension |
| `blockOreDimensionsAreBlacklist` | boolean | `false` | blockOreDimensions stattdessen als die Dimensionen behandeln, die in Ruhe gelassen werden |

### Biome

`control.biomes` entscheidet diese Gruppe. Biome nach Mod und nach Name sperren, und was sie ersetzt.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `logBlockedBiomes` | boolean | `true` | Pro Mod protokollieren, wie viele Biome gesperrt wurden, damit du siehst, was auf die Whitelist gehört |
| `blockBiomes` | boolean | `false` | Jedes Biom am Generieren hindern, außer den Mods in biomeWhitelist. Gesperrte Biome werden zum Leere-Biom oder zu dem, was die roles und der fallback der Weltvorlage nennen. Jedes Biom zu sperren macht die Oberwelt zu einer Leere-Welt |
| `biomeWhitelist` | Liste | `["minecraft"]` | Mod-IDs, deren Biome bei eingeschaltetem blockBiomes weiter generieren. Ein Pack-Biom nutzt den Namespace des Packs |
| `biomeNames` | Liste | leer | Biome, für die das gilt, wem sie auch gehören und was die Whitelist auch sagt, nach Id wie minecraft:birch_forest |
| `biomeNamesAreBlacklist` | boolean | `true` | An werden die biomeNames gesperrt. Aus generieren nur die biomeNames |
| `blockBiomeDimensions` | Liste | `["minecraft:overworld"]` | Dimensionen, in denen die Biomsperre gilt, nach Id. Leer heißt jede Dimension, deren Biome nach Klima verteilt werden, die Oberwelt und der Nether |
| `blockBiomeDimensionsAreBlacklist` | boolean | `false` | An überspringt die Biomsperre diese Dimensionen. Aus gilt sie nur für sie |

### Ersetzungen

`control.replacements` entscheidet diese Gruppe. Blockersetzung in Chunks, die es schon gibt.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `blockReplacements` | Liste | leer | Blöcke, die beim Laden aus Chunks ausgetauscht werden, geschrieben als block=block mit optionalem Zustand auf beiden Seiten, etwa minecraft:andesite=minecraft:stone oder minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]. Jeder Chunk wird einmal bearbeitet, neue eingeschlossen |
| `blockReplacementDimensions` | Liste | leer | Dimensionen, in denen die Blockersetzung gilt, nach Id. Leer heißt jede Dimension |
| `blockReplacementDimensionsAreBlacklist` | boolean | `false` | An überspringt die Blockersetzung diese Dimensionen. Aus gilt sie nur für sie |
| `blockReplacementMinHeight` | int, -2032 bis 2031 | `-64` | Niedrigstes y, das die Blockersetzung ansieht |
| `blockReplacementMaxHeight` | int, -2032 bis 2031 | `319` | Höchstes y, das die Blockersetzung ansieht |
| `blockReplacementKey` | Text | `0000` | Ändern, damit jeder Chunk die Blockersetzung noch einmal durchläuft |
| `logBlockReplacements` | boolean | `true` | Beim ersten Mal jede Ersetzung protokollieren, und eine Summe, wenn eine Welt nachgeholt ist |

### Dörfer und Städte

`control.villages` entscheidet diese Gruppe. Die Stadt- und Dorfstraßen, die ein Pack anlegt: ihre Form, Ausstattung, Brücken, Tunnel, Gleise, Grundstücke und der Platz.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `villagePathBlock` | Text | leer | Der Block, mit dem Stadtstraßen gepflastert werden, wenn terrainAdaptation sie anlegt. Leer pflastert sie mit Trampelpfad |
| `villagePathExtraWidth` | int, 0 bis 16 | `0` | Zusätzliche Blöcke Straßenbreite auf jeder Seite über die üblichen 3 hinaus, wenn terrainAdaptation die Straßen anlegt. Verbreitert die Straßen selbst, die Blöcke dazwischen stehen also von breiten Straßen zurück |
| `villageBlockSizes` | Liste | leer | Wie tief die Blöcke zwischen den parallelen Straßen einer Stadt sind, ein gewichteter Eintrag je Zeile, geschrieben größe=gewicht wie 32=3, einmal je Viertel gewürfelt. Leer nimmt 32 |
| `villageCitySpacing` | int, 0 bis 256 | `16` | Wie weit auseinander Stadtviertel gesät werden, in Vierteln, deren Größe sich aus den Grundstücken ergibt (zweimal das größte Grundstück, dazu ein Platz und eine Straße je Seite, auf 16 Blöcke aufgerundet, mindestens 96): ein Viertel in jedem Quadrat aus so vielen trägt eine Stadt, an einer durch den Weltseed festgelegten Stelle, und bei 1 ist jedes Viertel eine, ein Platz mit dem Brunnen in der Mitte und Straßen heraus, die an die des nächsten Viertels anschließen. 0 sät keine |
| `villagePathAlleyBlock` | Text | leer | Der Block, mit dem Gassen angelegt werden. Eine Gasse ist eine Straße, die zu schmal für Linien und Gehwege ist. Leer legt Gassen mit dem Straßenblock an |
| `villagePathAlleyChance` | int, 0 bis 100 | `0` | Die Prozentchance, dass eine Straße als Gasse statt in voller Breite angelegt wird. 0 legt keine Gassen an |
| `villagePathMinimumWidth` | int, 0 bis 32 | `0` | Die schmalste erlaubte Straße. Eine Straße, die schmaler angelegt würde, wird gar nicht angelegt, und das Viertel legt sich um die Lücke herum an. 0 lehnt nie ab |
| `villagePathFlatRun` | int, 0 bis 64 | `6` | Straßen halten jede Höhe mindestens so viele Blöcke, bevor sie eine Stufe machen, an Weltkoordinaten verankert, damit Abschnitte über Teile hinweg übereinstimmen. 0 oder 1 lässt eine Straße jeden Block stufen |
| `villagePlotsLeast` | int, 0 bis 512 | `0` | Die wenigsten bebauten Grundstücke, mit denen sich ein Viertel zufriedengibt. Ein Viertel, das mit weniger herauskommt, wird gar nicht angelegt. 0 setzt keine Untergrenze |
| `villagePlotsMost` | int, 0 bis 512 | `0` | Die meisten bebauten Grundstücke, die ein Viertel haben darf. Hat ein Viertel so viele erreicht, werden keine weiteren angelegt. 0 setzt keine Obergrenze |
| `villagePlotsBackRow` | boolean | `true` | An setzt ein zweiter Durchgang hinter jedes Grundstück an einer Straße ein weiteres, ihm zugewandt, mit demselben Wurf und derselben Platzprüfung, damit das Innere eines Blocks zwischen zwei Straßen bebaut wird statt leer zu bleiben. Aus lässt die Grundstücke nur an den Straßenfronten |
| `villageLayout` | Text | leer | Ein Stadtplan, der angelegt wird, statt das Viertel zu planen, benannt wie mypack:downtown und aus dem citymaps-Ordner dieses Packs gelesen. Leer plant das Viertel wie gewohnt |
| `villagePathCenterBlock` | Text | leer | Der Block der Mittellinie einer Stadtstraße. Leer zeichnet keine Mittellinie |
| `villagePathCenterDash` | int, 0 bis 64 | `0` | Strichelt die Mittellinie: N Blöcke Linie, dann einer Straße, an Weltkoordinaten verankert, damit Abschnitte einander fortsetzen. 0 hält die Linie durchgezogen |
| `villagePathLineBlock` | Text | leer | Der Block der Randlinien zwischen Straße und Gehweg. Leer zeichnet keine Randlinien |
| `villagePathSidewalkBlock` | Text | leer | Der Block, mit dem Gehwege angelegt werden, auf Straßenhöhe, außerhalb der Randlinien. Leer legt keine Gehwege an |
| `villagePathSidewalkWidth` | int, 0 bis 16 | `2` | Wie viele Blöcke breit jeder Gehweg ist, wenn villagePathSidewalkBlock gesetzt ist. Eine Straße, die zu schmal für ihre Linien und Gehwege ist, wird stattdessen kahl angelegt |
| `villagePathLampBlock` | Text | `minecraft:oak_fence` | Der Block, aus dem eine Laterne an einer Straße gebaut wird, villagePathLampHeight hoch auf dem Bordstein gestapelt. Leer stellt keine Laternen auf |
| `villagePathLampHeight` | int, 1 bis 32 | `3` | Wie viele Blöcke hoch der Laternenpfahl vor seinem Kopf steht |
| `villagePathLampTopBlock` | Text | `minecraft:red_wool` | Der Kopf, der oben auf einer Laterne sitzt. Leer lässt den Pfahl kahl |
| `villagePathLampSideBlock` | Text | `minecraft:torch` | Das Licht, das an jeder Seite eines Laternenkopfs hängt. Leer hängt keines auf |
| `villagePathLampStructure` | Text | leer | Eine Strukturdatei, die als Laterne gesetzt wird, statt die Laternenblöcke zu stapeln, benannt wie mypack:street_lamp. Ihre unterste Ebene sitzt auf dem Bordstein. Leer stapelt die Laternenblöcke |
| `villagePathSupportBlock` | Text | leer | Der Block, der eine Ebene unter der Straßenoberfläche gelegt wird. Leer legt keinen |
| `villagePathBridgeBlock` | Text | leer | Der Block, mit dem eine Straße Wasser überquert. Leer deckt eine Brücke mit dem Straßenblock |
| `villagePathBridgeSidewalkBlock` | Text | leer | Der Block, mit dem Brückengehwege gedeckt werden, wo eine Straße Wasser überquert. Leer behält den normalen Gehwegblock auf Brücken |
| `villagePathBridgeBarrierBlock` | Text | leer | Der Block, aus dem Brückengeländer gebaut werden, entlang beider Deckkanten gestapelt. Leer baut keine Geländer |
| `villagePathBridgeBarrierHeight` | int, 1 bis 16 | `1` | Wie viele Blöcke hoch die Brückengeländer stehen |
| `villagePathBridgeDrop` | int, 0 bis 64 | `0` | Wie weit die Höhe einer Straße über dem Boden stehen muss, bevor der Abfall darunter überbrückt statt massiv aufgefüllt wird. 0 hält Straßen aus der Luft und überbrückt nur Wasser |
| `villagePathBridgeFrameBlock` | Text | leer | Der Block, aus dem ein Rahmen über einer langen Brücke gebaut wird: ein Pfosten an jeder Deckseite hoch und ein Balken oben quer. Leer baut keinen |
| `villagePathBridgeFrameTopBlock` | Text | leer | Der Block, aus dem der Balken oben auf diesem Rahmen besteht. Leer nimmt villagePathBridgeFrameBlock |
| `villagePathBridgeFrameHeight` | int, 1 bis 32 | `4` | Wie viele Blöcke freie Höhe der Rahmen über dem Deck lässt. Der Balken liegt einen Block darüber |
| `villagePathBridgeFrameRun` | int, 1 bis 256 | `24` | Wie viele Reihen auseinander die Rahmen stehen, wenn eine Brücke lang genug für mehrere ist. Sie werden symmetrisch um die Mitte der überbrückten Strecke verteilt |
| `villagePathBridgeFrameLeast` | int, 1 bis 256 | `24` | Die kürzeste überbrückte Strecke, die überhaupt einen Rahmen bekommt, in Reihen. Eine kürzere Brücke bleibt schlicht |
| `villagePathTunnelBlock` | Text | leer | Der Block, mit dem eine Straße ausgekleidet wird, wo sie durch einen Hügel bohrt, statt ihn aufzuschneiden: die Wände zu beiden Seiten des Bohrs und das Dach darüber. Leer bohrt keine Tunnel und lässt eine Straße den Hügel hinaufsteigen |
| `villagePathTunnelDepth` | int, 1 bis 128 | `10` | Wie viel Boden über der Straßenoberfläche stehen muss, bevor eine Strecke als Tunnel gebohrt statt erklommen wird. Ein so tiefer Anstieg irgendwo entlang der Strecke wird eben gehalten und durchbohrt. Braucht villagePathTunnelBlock |
| `villagePathTunnelLightBlock` | Text | leer | Der Block, der als Licht entlang der Mittellinie in ein Tunneldach gesetzt wird. Leer beleuchtet nichts |
| `villagePathTunnelLightRun` | int, 1 bis 64 | `8` | Wie viele Blöcke auseinander die Tunnellichter sitzen, an Weltkoordinaten verankert, damit Teile übereinstimmen |
| `villageRailLines` | int, 0 bis 16 | `0` | Wie viele Bahnlinien durch ein Viertel laufen, vor jeder Straße angelegt, damit die Stadt um sie herum wächst. 0 legt keine an |
| `villageRailSpacing` | int, 1 bis 256 | `48` | Die wenigsten Blöcke freien Bodens zwischen dem Gleisbett einer Bahnlinie und dem nächsten desselben Viertels. 1 legt sie einen Block auseinander, so baut ein Pack einen Rangierbahnhof aus parallelen Linien |
| `villageRailDirection` | Text | `any` | In welche Richtung die Bahnlinien eines Viertels laufen: ew für Ost nach West, ns für Nord nach Süd, any würfelt es je Viertel |
| `villageRailWidth` | int, 3 bis 32 | `3` | Das Mindestmaß des Gleisbetts in Blöcken. 3 trägt ein Gleis in der Mitte und 5 trägt zwei; ein Bett, das mehr Gleise tragen soll, als hineinpassen, wird verbreitert, und villageRailShoulderWidth kommt außen dazu |
| `villageRailBlock` | Text | leer | Der Gleisblock auf dem Bett. Leer legt Vanilla-Schienen, auf denen Loren fahren |
| `villageRailTrackSeat` | Text | `auto` | Wo das Gleis sitzt: auto setzt einen Schienenblock auf das Bett und jeden anderen Block in die Bettoberfläche, on legt es immer auf das Bett, in setzt es immer bündig ins Bett |
| `villageRailBedBlock` | Text | leer | Das Bett, auf dem das Gleis liegt. Leer legt Kies |
| `villageRailTieBlock` | Text | leer | Die Schwelle, die alle villageRailTieRun Reihen quer über das Bett gelegt wird. Leer legt Eichenbretter |
| `villageRailTieRun` | int, 1 bis 32 | `2` | Wie viele Reihen auseinander die Schwellen liegen |
| `villageRailTracks` | int, 0 bis 8 | `0` | Wie viele Gleise das eine Bett nebeneinander trägt, villageRailTrackGap auseinander. Das Bett wird verbreitert, um alle zu tragen. 0 legt ein Gleis auf ein Bett unter fünf Breite und zwei auf ein breiteres |
| `villageRailTrackGap` | int, 2 bis 16 | `2` | Wie viele Blöcke auseinander die Gleise auf einem Bett sitzen, Mitte zu Mitte. 2, das Mindestmaß, lässt einen Block Bett dazwischen, was sie davon abhält, ineinander zu kurven, wie sich berührende Schienen es tun |
| `villageRailShoulderBlock` | Text | leer | Der Block, mit dem die äußersten Spalten des Betts ausgekleidet werden, ein Wartungsweg neben dem Gleis, das Bahn-Gegenstück zum Gehweg einer Straße. Leer legt keinen und lässt dem Bett seine volle Breite |
| `villageRailShoulderWidth` | int, 0 bis 8 | `1` | Wie viele Spalten breit dieser Randstreifen auf jeder Seite ist, außerhalb von villageRailWidth dazugezählt. Braucht villageRailShoulderBlock |
| `villageRailPowerBlock` | Text | leer | Das Antriebsgleis, das alle villageRailPowerRun Reihen in die Linie gesetzt wird. Leer nimmt eine Vanilla-Antriebsschiene; ein Block, der keine Schiene ist, wird einfach dort gelegt |
| `villageRailPowerBase` | Text | leer | Was unter einem Antriebsgleis sitzt, um es zu speisen. Leer nimmt einen Redstone-Block |
| `villageRailPowerRun` | int, 0 bis 256 | `0` | Wie viele Reihen auseinander eine Antriebsschiene über ihrer Basis ins Gleis gesetzt wird, damit Loren weiterfahren. 0 treibt keine an |
| `villageRailClimb` | int, 1 bis 64 | `8` | Wie viele Reihen eine Bahnlinie eben läuft je Block, den sie steigt oder fällt. 1 macht sie so steil wie eine Straße |
| `villageRailTail` | int, 0 bis 48 | `48` | Wie weit eine Bahnlinie an beiden Enden über das Viertel hinausläuft. Auf 48 gehalten, so weit ein Strukturstart reicht |
| `villageRailSupportBlock` | Text | leer | Der Pfostenblock unter einer Trestle-Brücke, wo die Linie über Wasser oder einen Abfall läuft. Leer nimmt Eichenstämme |
| `villageRailDeckBlock` | Text | leer | Das Deck, auf dem eine Trestle-Brücke das Bett trägt. Leer nimmt Eichenbretter |
| `villageRailBarrierBlock` | Text | leer | Geländer entlang beider Kanten eines Trestle-Decks. Leer stellt keine auf |
| `villageRailBridgeFrameBlock` | Text | leer | Der Block, aus dem ein Rahmen über einer langen Trestle-Brücke gebaut wird: ein Pfosten an jeder Deckseite hoch und ein Balken oben quer. Leer baut keinen |
| `villageRailBridgeFrameTopBlock` | Text | leer | Der Block, aus dem der Balken oben auf diesem Rahmen besteht. Leer nimmt villageRailBridgeFrameBlock |
| `villageRailBridgeFrameHeight` | int, 1 bis 32 | `4` | Wie viele Blöcke freie Höhe der Rahmen über dem Deck lässt. Der Balken liegt einen Block darüber |
| `villageRailBridgeFrameRun` | int, 1 bis 256 | `24` | Wie viele Reihen auseinander die Rahmen stehen, wenn eine Trestle-Brücke lang genug für mehrere ist. Sie werden symmetrisch um die Mitte der Brücke verteilt |
| `villageRailBridgeFrameLeast` | int, 1 bis 256 | `24` | Die kürzeste Trestle-Brücke, die überhaupt einen Rahmen bekommt, in Reihen. Eine kürzere bleibt schlicht |
| `villageRailTunnelBlock` | Text | leer | Der Block, mit dem eine Bahnlinie ausgekleidet wird, wo sie durch einen Hügel bohrt, statt ihn zu erklimmen. Leer bohrt keine Tunnel |
| `villageRailTunnelDepth` | int, 1 bis 128 | `6` | Wie viel Boden über dem Bett stehen muss, bevor eine Strecke als Tunnel gebohrt statt erklommen wird. Braucht villageRailTunnelBlock |
| `villageRailTunnelLightBlock` | Text | leer | Der Block, der als Licht entlang der Mittellinie in ein Bahntunneldach gesetzt wird. Leer beleuchtet nichts |
| `villageRailTunnelLightRun` | int, 1 bis 64 | `8` | Wie viele Blöcke auseinander diese Tunnellichter sitzen, an Weltkoordinaten verankert, damit Teile übereinstimmen |
| `villageWellStructure` | Liste | leer | Strukturdateien, die als Platzmittelstück an der mittleren Kreuzung des Viertels gesetzt werden, ein gewichteter Eintrag je Zeile, geschrieben name=gewicht wie mypack:plaza_spire=3, einmal je Viertel gewürfelt. Ihre unterste Ebene sitzt auf dem Platzboden. Leer setzt keines |
| `villagePathDeadEnds` | Liste | leer | Wie eine Straße, die in einer Sackgasse endet, abgeschlossen wird, als Strukturnamen aus einem Pack, einer je Zeile, je Ende gewürfelt. Leer schließt jede Sackgasse stattdessen mit einem Wendehammer |
| `villagePathPiers` | Liste | leer | Pier-Stile für eine Straße, die über Wasser endet: der überbrückte Auslauf wird ein Pier statt einer Brücke ins Nichts. Die Stile sind railed, pilings und boardwalk; mehrere Einträge würfeln einen je Pier. Leer lässt so einen Auslauf eine schlichte Brücke |
| `villagePathPierCargo` | Liste | leer | Fracht, die innen entlang der Geländer eines Piers aufgestellt wird, als block=gewicht-Einträge, block=gewicht,höhe zum Stapeln, oder empty=gewicht für den Anteil, der frei bleibt. Jede zweite Reihe würfelt die Liste auf jeder Seite. Leer lässt Piere kahl |
| `villagePathPierLoot` | Text | `resourcedatapackloader:chests/pier_cargo` | Die Beutetabelle, aus der Frachtblöcke mit Inventar beim ersten Öffnen gefüllt werden. Ein Pack kann die eingebaute Tabelle ersetzen, indem es eine eigene Beutetabelle unter diesem Namen mitliefert. Leer lässt sie leer |
| `villagePathIntersects` | Liste | leer | Kreuzungsmuster, die dort gemalt werden, wo Straßen sich kreuzen, per Registry-Schlüssel aus dem pathintersects-Ordner eines Packs. Ein Eintrag malt jede Kreuzung gleich; mehrere würfeln eines je Kreuzung, gewichtet nach jedem Muster. Leer malt nichts |
| `villageDecor` | Liste | leer | Dekoration, die entlang der Stadtstraßen verstreut wird, als name=gewicht-Paare, die Worldgen aus einem Pack nennen, mypack:street_flowers=2. Der Name empty ist der Anteil der Stellen, die kahl bleiben. Jeder dritte Block Randstreifen auf jeder Straßenseite würfelt die Liste. Leer verstreut nichts |
| `villageSubwayLines` | int, 0 bis 32 | `0` | Wie viele U-Bahn-Linien eine Stadt gräbt. 0 gräbt keine und würfelt nichts, die Stadt wird also genau so angelegt wie ohne sie |
| `villageSubwayDepth` | int, 6 bis 192 | `24` | Wie weit unter der Oberfläche das Bett einer U-Bahn sitzt. Die Linie wird nach dem Boden darüber nivelliert, folgt dem Land also in dieser Tiefe, statt eben zu laufen |
| `villageSubwaySpacing` | int, 1 bis 512 | `64` | Wie weit auseinander die U-Bahn-Linien einer Stadt voneinander gehalten werden |
| `villageSubwayDirection` | Text | `any` | In welche Richtung U-Bahn-Linien laufen: x, z oder any, um es je Stadt zu würfeln |
| `villageSubwayWidth` | int, 3 bis 33 | `3` | Wie breit das Bett ist, vor den Randstreifen |
| `villageSubwayBlock` | Text | leer | Der Gleisblock. Leer legt Vanilla-Schienen |
| `villageSubwayTrackSeat` | Text | `auto` | Ob das Gleis auf dem Bett sitzt, darin, oder auto, damit der Block entscheidet |
| `villageSubwayBedBlock` | Text | leer | Der Block, aus dem das Bett besteht. Leer nimmt Kies |
| `villageSubwayTieBlock` | Text | leer | Der Block, der als Schwellen quer über das Bett gelegt wird. Leer nimmt Bretter |
| `villageSubwayTieRun` | int, 1 bis 64 | `2` | Wie viele Blöcke auseinander die Schwellen sitzen |
| `villageSubwayTracks` | int, 0 bis 16 | `0` | Wie viele parallele Gleise das Bett trägt. 0 nimmt so viele, wie die Breite erlaubt |
| `villageSubwayTrackGap` | int, 2 bis 16 | `2` | Wie weit auseinander parallele Gleise sitzen |
| `villageSubwayShoulderBlock` | Text | leer | Der Block zu beiden Seiten des Betts. Leer lässt keinen Randstreifen |
| `villageSubwayShoulderWidth` | int, 0 bis 16 | `1` | Wie breit dieser Randstreifen ist |
| `villageSubwayPowerBlock` | Text | leer | Der Antriebsgleis-Block. Leer nimmt eine Vanilla-Antriebsschiene |
| `villageSubwayPowerBase` | Text | leer | Der Block, der unter ein Antriebsgleis gesetzt wird, um es zu treiben. Leer nimmt einen Redstone-Block |
| `villageSubwayPowerRun` | int, 0 bis 256 | `0` | Wie viele Blöcke auseinander die Antriebsgleise sitzen. 0 legt keine |
| `villageSubwayTunnelBlock` | Text | leer | Der Block, mit dem der Bohr ausgekleidet wird: die Wände zu beiden Seiten und das Dach darüber. Leer gräbt gar keine U-Bahn, denn eine U-Bahn ist ein Bohr |
| `villageSubwayTunnelLightBlock` | Text | leer | Der Block, der als Licht ins Tunneldach gesetzt wird. Leer beleuchtet nichts |
| `villageSubwayTunnelLightRun` | int, 1 bis 128 | `8` | Wie viele Blöcke auseinander diese Lichter sitzen, an Weltkoordinaten verankert, damit Teile übereinstimmen |
| `villageSubwayClimb` | int, 1 bis 128 | `8` | Wie viele Blöcke eine Linie läuft, bevor sie einen Block hoch- oder hinabstufen darf |
| `villageSubwayTail` | int, 0 bis 256 | `48` | Wie weit über die eigenen Teile der Stadt hinaus eine Linie läuft, bevor sie endet |
| `villageSubwayStationLength` | int, 0 bis 128 | `0` | Wie viele Blöcke lang die Kammer einer U-Bahn-Station ist. 0 baut gar keine Stationen |
| `villageSubwayStationRun` | int, 0 bis 1024 | `0` | Wie viele Blöcke auseinander weitere Stationen entlang einer Linie sitzen, jenseits der dem Platz nächsten. 0 baut nur die am Platz |
| `villageSubwayPlatformWidth` | int, 0 bis 16 | `3` | Wie weit die Kammer zu beiden Seiten des Betts aufgeweitet wird, um einen Bahnsteig zu bilden |
| `villageSubwayPlatformBlock` | Text | leer | Der Block, mit dem der Bahnsteig gepflastert wird. Leer pflastert ihn mit der Tunnelauskleidung |
| `villageSubwayStairBlock` | Text | leer | Der Block, aus dem die Stufen hinauf zur Straßenseite bestehen. Leer nimmt die Tunnelauskleidung |
| `villageSubwayRailingBlock` | Text | `minecraft:iron_bars` | Der Block, der um den Kopf der Stationstreppe gesetzt wird, wo sie sich zur Straße öffnet, damit niemand in den Schacht läuft. Leer lässt den Kopf ohne Geländer |
| `villageSubwayBenchBlock` | Text | `minecraft:oak_stairs` | Die Sitzfläche der Bänke, die auf dem Bahnsteig einer Station und neben ihrem Treppenkopf stehen. Ein Treppenblock liest sich als Bank; jeder Block funktioniert. Leer lässt die Bänke weg |
| `villageSubwayBenchEndBlock` | Text | `minecraft:oak_log` | Die Armlehnen an beiden Enden einer Stationsbank. Leer lässt die Sitzfläche an beiden Enden kahl |
| `villageSubwayBenchLength` | int, 0 bis 32 | `5` | Wie lang eine Stationsbank ist, Armlehnen eingeschlossen. 0 lässt die Bänke weg |
| `villageSubwaySurfaces` | int, 0 bis 100 | `25` | Die Chance in Hundert, dass eine U-Bahn-Linie an einem Ende an die Oberfläche steigt und von dort als gewöhnliche Bahn weiterläuft, Tunnel hinter sich und offenes Gleis voraus. Der Aufstieg braucht villageSubwayClimb Reihen je Block, eine tiefe Linie braucht also eine lange Strecke nach oben. 0 hält jede U-Bahn auf ihrer ganzen Länge vergraben |
| `villageSubwayStation` | Text | leer | Eine Struktur aus dem structures-Ordner eines Packs, die als Station selbst genutzt wird, anstelle des ausgehobenen Treppenschachts. Hol eine mit #scripts/rdpl-grab-template.py aus einer von Hand gebauten Welt: ihre massiven Zellen werden gelegt und ihre Luftzellen ausgehoben, die Form ist also der Bau und keine Beschreibung davon. Leer hebt stattdessen den Treppenschacht aus |
| `villageSubwayEntrance` | Text | leer | Eine Struktur aus dem structures-Ordner eines Packs, die am Kopf der Stationstreppe gesetzt wird, damit der Eingang auf der Straße markiert ist. Leer lässt die Treppe kahl herauskommen |
| `villageSubwayStationFoot` | int, 0 bis 64 | `4` | Wie viele Ebenen am Fuß eines Stationsbaus einmal gelegt werden, vor dem Teil, der sich wiederholt. Der Boden und der Durchgang zum Bahnsteig liegen hier |
| `villageSubwayStationRepeat` | int, 0 bis 64 | `12` | Wie viele Ebenen eines Stationsbaus sich wiederholen, damit ein Bau jeder Tiefe dient: der Schacht wächst um ganze Kopien dieses Bandes, und der Gang nimmt auf, was übrig bleibt. Es muss eine ganze Treppenwindung sein, sonst schließen die Läufe nicht aneinander an. 0 lässt den Bau nie wachsen |
| `villageSewerBlock` | Text | leer | Der Block, mit dem ein Abwasserkanal unter den Straßen und Gassen einer Stadt ausgekleidet wird: Boden, Wände und Dach. Leer gräbt keine Kanäle |
| `villageSewerDepth` | int, 4 bis 128 | `8` | Wie weit unter der eigenen Oberfläche einer Straße der Kanalboden sitzt. Der Kanal folgt der Straße, eine steigende Straße trägt also einen steigenden Kanal. Braucht villageSewerBlock |
| `villageSewerHeight` | int, 2 bis 32 | `3` | Wie viele Blöcke freie Höhe über dem Kanalsteg stehen |
| `villageSewerWidth` | int, 3 bis 33 | `5` | Wie breit ein Kanal läuft, quer gezählt einschließlich seiner zwei Wände. Gerade Zahlen werden aufgerundet, damit die Rinne die Mitte behält |
| `villageSewerWaterBlock` | Text | `minecraft:water` | Der Block, der die Rinne in der Mitte eines Kanals füllt. Leer lässt die Rinne trocken |
| `villageSewerWalkBlock` | Text | leer | Der Block, mit dem die Stege zu beiden Seiten der Rinne belegt werden. Leer geht auf dem Auskleidungsblock |
| `villageSewerLightBlock` | Text | leer | Der Block, der als Licht über der Rinne in ein Kanaldach gesetzt wird. Leer beleuchtet nichts |
| `villageSewerLightRun` | int, 1 bis 128 | `8` | Wie viele Blöcke auseinander die Kanallichter sitzen, an Weltkoordinaten verankert, damit Teile übereinstimmen |
| `villageSewerLadderBlock` | Text | leer | Der Block, an dem ein Kanalschacht erklettert wird, den Schacht hinunter von der Straße bis zum Kanaldach gesetzt. Leer lässt den Schacht offen |
| `villageSewerCoverBlock` | Text | leer | Der Block, der einen Kanalschacht abdeckt, bündig in eine Ost-West-Straße gesetzt, wo eine Straße oder Gasse auf sie trifft, und auf dem Platz, wo diese Straße den Kanalring kreuzt. Leer lässt die Schachtöffnung offen |
| `villageSewerMossBlock` | Text | leer | Ein zweiter Block, der hier und da in die Kanalauskleidung gemischt wird, etwa bemooster Stein zwischen schlichtem. Leer kleidet den Kanal durchgehend mit einem Block aus |
| `villageSewerMossChance` | int, 0 bis 100 | `25` | Der Prozentsatz der Auskleidungsblöcke, die als villageSewerMossBlock herauskommen. Je Blockposition aus dem Weltseed gewürfelt, ein neues Pflastern legt also dasselbe Muster |
| `villageSewerVineBlock` | Text | leer | Ein Block, der hier und da an die Innenseite der Kanalwände gehängt wird, etwa Ranken. Leer hängt nichts auf |
| `villageSewerVineChance` | int, 0 bis 100 | `20` | Der Prozentsatz der Wandzellen, die villageSewerVineBlock tragen. Je Blockposition aus dem Weltseed gewürfelt, ein neues Pflastern hängt also dasselbe Muster |
| `villageSewerWellEntrance` | boolean | `true` | An bekommt eine Stadt mit Kanalisation einen Kanalring unter dem Platz um den Brunnen, durch den die Kanäle der dort zusammentreffenden Straßen laufen, und je einen Gullydeckel auf dem Platz hinab auf den Ring, wo eine Ost-West-Straße ihn kreuzt, sodass die Kanalisation ein zusammenhängendes System mit Einstieg in der Stadtmitte ist. Aus kreuzen sich die Straßenkanäle unter dem Brunnen, und der Platz hat keinen eigenen Abstieg |

### Strukturen

`control.structures` entscheidet diese Gruppe. Vanilla-Strukturen abschalten, ihr Abstand, ihre Trennung, Spawn-Entfernung, Biome, Spawns, Fixpunkte und Geländeanpassung.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `structureSpacing` | Liste | leer | Wie weit auseinander Vanilla-Strukturen gesät werden, in Chunks, als struktur=chunks-Einträge: die 1.12.2-Namen temples, monuments, mansions, mineshafts, strongholds, netherbridges, endcities und villages, oder jede Structure-Set-Id wie pillager_outposts. Bei mineshafts ist die Zahl ein Chunk in so vielen; bei strongholds ist es der Ringabstand |
| `structureSeparation` | Liste | leer | Wie nah zwei einer Struktur einander sein dürfen, in Chunks, als struktur=chunks-Einträge; bei strongholds ist es die Ringstreuung |
| `structureMost` | Liste | leer | Wie viele einer Struktur eine Dimension höchstens hält, als struktur=anzahl-Einträge wie villages=100: sind so viele gegründet, gründet kein Chunk eine weitere, mit structureAt festgelegte Chunks ausgenommen. 0 oder ein fehlender Eintrag setzt keine Obergrenze |
| `structureSpawners` | Liste | leer | Was der Mobspawner in einer Vanilla-Struktur spawnt, als struktur=namespace:entity-Einträge, kommagetrennt für eine zufällige Wahl. Nur dungeons, mineshafts, netherbridges und strongholds bauen einen; Spawner, die andere Mods setzen, bleiben unangetastet |
| `structureMinDistanceFromSpawn` | Liste | leer | Wie weit vom Weltspawn eine Struktur beginnt, in Blöcken, als struktur=blöcke-Einträge. Gemessen vom worldSpawn des Packs, wenn einer gesetzt ist, sonst vom Weltursprung, da die Platzierung entschieden wird, bevor ein Spawn existiert |
| `structureBiomes` | Liste | leer | Wo eine Struktur generieren darf, als struktur=biom,biom-Einträge mit Biom-Ids oder Biomtypen wie SANDY |
| `structureBiomesAreBlacklist` | Liste | leer | Richtung der Biomlisten, geschrieben als struktur=true oder struktur=false, eines je Zeile. True nimmt die gelisteten Biome weg, false macht sie zu den einzigen |
| `structureSpawns` | Liste | leer | Die Mobs, die eine Struktur spawnt, was das Biom auch sagt, als struktur=namespace:entity:gewicht:wenigstens:höchstens-Einträge, kommagetrennt; eine leere Liste hinter dem = spawnt nichts |
| `structureAt` | Liste | leer | Strukturen, die an genaue Stellen gepinnt sind, als struktur=x,z-Einträge in Blockkoordinaten, einer je gewünschter Instanz. Eine gepinnte Struktur generiert in diesem Chunk und nirgends sonst |
| `structureAdaptation` | Liste | leer | Wie sich das Gelände an eine Struktur anpasst, als struktur=modus-Einträge mit den Modi none, bury, beard_thin, beard_box und encapsulate |
| `terrainAdaptation` | boolean | `false` | RDPLs eigene Stadtstraßen anlegen, ins Gelände eingelassen statt auf Stelzen über jeder Senke, und die villagePath- und villageRail-Optionen mit ihnen lesen. Verändert das Gelände, eine damit gemachte Welt unterscheidet sich also von einer ohne. Diese Linie legt noch keine Straßen an, Einschalten sagt es also nur |
| `villagePieces` | Liste | leer | Hier genannte Dorfgrundstück-Definitionen, eine je Zeile, als volle Id einer villages-Datei wie mypack:smithy. Auf 1.12.2 waren das die Vanilla-Teilenamen, die diese Linie nicht hat |
| `villagePiecesAreBlacklist` | boolean | `true` | An werden die Grundstücke in villagePieces gesperrt. Aus werden nur diese Grundstücke gebaut |
| `villageBlocks` | Liste | leer | Blöcke, aus denen Dorfgrundstücke gebaut werden, als original=ersatz-Paare, minecraft:cobblestone=mypack:ruby_brick. Ein Paar darf eine Chance von Hundert tragen, minecraft:cobblestone=minecraft:mossy_cobblestone,20, gewürfelt, wo der Block gelegt wird. Straßen werden nie erfasst. Leer lässt jeden Block, wie ihn die eigene Struktur des Grundstücks hat |

### Spawnen

`control.spawning` entscheidet diese Gruppe. Mob-Spawn-Obergrenzen, feindliche Spawnraten und die Lichtgrenze.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `surfaceDayMonsterRate` | Zahl, 0.0 bis 4.0 | `1.0` | Wie oft feindliche Mobs tagsüber an der Oberfläche spawnen, wo der Himmel zu sehen ist. 0 unterbindet sie, 1 ist Vanilla, über 1 erzwingt Spawns, die Vanilla ablehnen würde |
| `surfaceNightMonsterRate` | Zahl, 0.0 bis 4.0 | `1.0` | Dasselbe für die Oberfläche bei Nacht |
| `undergroundDayMonsterRate` | Zahl, 0.0 bis 4.0 | `1.0` | Dasselbe für den Untergrund tagsüber, wo der Himmel nicht zu sehen ist |
| `undergroundNightMonsterRate` | Zahl, 0.0 bis 4.0 | `1.0` | Dasselbe für den Untergrund bei Nacht |
| `monsterCap` | int, -1 bis 1000 | `-1` | Wie viele feindliche Mobs weltweit auf einmal geladen sein dürfen, bevor die Zahl nach den geladenen Chunks skaliert wird. Vanilla ist 70. -1 lässt es in Ruhe |
| `creatureCap` | int, -1 bis 1000 | `-1` | Dieselbe Grenze für friedliche Tiere. Vanilla ist 10. -1 lässt es in Ruhe |
| `ambientCap` | int, -1 bis 1000 | `-1` | Dieselbe Grenze für Umgebungsmobs wie Fledermäuse. Vanilla ist 15. -1 lässt es in Ruhe |
| `waterCreatureCap` | int, -1 bis 1000 | `-1` | Dieselbe Grenze für Wassermobs wie Tintenfische. Vanilla ist 5. -1 lässt es in Ruhe |
| `monsterSpawnLight` | int, -1 bis 15 | `-1` | Das hellste Blocklicht, in dem ein feindlicher Mob noch spawnen darf, zusätzlich zu den Vanilla-Prüfungen. -1 lässt die Vanilla-Regel in Ruhe. Spawner sind nicht betroffen |
| `threatItems` | Liste | leer | Items, die die Bedrohungsstufe eines Spielers heben, als item=stufe,anzahl-Einträge mit optionalem ,each oder ,batch am Ende, z. B. minecraft:diamond_sword=5,1 oder minecraft:diamond=1,16,batch. Each, der Standard, zählt die Stufe für jedes gehaltene dazu, höchstens anzahl davon; batch zählt die Stufe einmal je anzahl gehaltene dazu. Eine Anzahl über der Stapelgröße des Items wird auf die Stapelgröße gekürzt. Jede geladene Entity, die Items hält, ist ein Träger: das Hauptinventar, die Rüstung und die Nebenhand eines Spielers, ein liegender Stapel, alles mit Iteminventar wie ein Truhen-Maultier oder eine Truhenlore, und die gehaltenen Items und die Rüstung anderer Mobs. Leer schaltet die Bedrohungsstufe ab |
| `threatLevels` | Liste | leer | Steigende Werte, die jede Bedrohungsstufe öffnen, z. B. 5, 15, 40 für drei Stufen. Ein Spieler unter der ersten ist in Stufe 0. Leer schaltet die Bedrohungsstufe ab |
| `threatMost` | int, -1 bis beliebig | `-1` | Der höchste Wert, den ein Träger erreichen kann, -1 für keine Grenze |
| `threatSpawnRate` | Zahl, 0.0 bis 100.0 | `1.0` | Wird in die feindliche Spawnrate nahe Trägern der höchsten Stufe eingerechnet, über die unteren Stufen abgestuft. 1.0 ändert nichts, 2.0 verdoppelt die Spawns oben |
| `threatNotice` | Zahl, 0.0 bis 256.0 | `0.0` | Wie viele Blöcke weiter feindliche Mobs einen Träger der höchsten Stufe bemerken, über die unteren Stufen abgestuft. 0 ändert nichts |
| `threatSays` | Liste | leer | Zeilen, die einem Spieler beim Eintritt in eine Stufe gesagt werden, als stufe=nachricht-Einträge |

### Grundgestein

`control.bedrock` entscheidet diese Gruppe. Flaches Grundgestein und seine Dimensions- und Biomlisten.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `flatBedrock` | boolean | `false` | Das zerklüftete Grundgestein am Boden der Welt durch flache Schichten ersetzen, über das erzeugte Preset, es formt also neue Welten, die damit gemacht werden |
| `flatBedrockDimensions` | Liste | `["minecraft:overworld"]` | Dimensionen, in denen das Grundgestein geglättet wird, nach Id wie minecraft:the_nether. Leer lassen für jede Dimension |
| `flatBedrockDimensionsAreBlacklist` | boolean | `false` | An überspringt das Glätten diese Dimensionen. Aus gilt es nur für sie |
| `bedrockLayers` | int, 1 bis 5 | `1` | Wie viele Schichten Grundgestein am Boden bleiben |
| `flatBedrockBiomes` | Liste | leer | Biome, in denen das Grundgestein geglättet wird, nach Id wie minecraft:birch_forest. Leer heißt jedes Biom; anderswo bleibt das Grundgestein, wie das Spiel es macht |
| `flatBedrockBiomesAreBlacklist` | boolean | `false` | An überspringt das Glätten diese Biome. Aus gilt es nur für sie |
| `flatBedrockRoof` | boolean | `false` | Auch die Grundgesteindecke glätten, wo eine Dimension eine hat, etwa das Netherdach |

### Langsames Ticken in der Ferne

`control.entities` entscheidet diese Gruppe. Das langsamere Tempo von Entities fern von jedem Spieler.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `slowDistantEntities` | boolean | `true` | Entities fern von jedem Spieler seltener ticken. Nichts bleibt je ungetickt, nur in langsamerem Tempo |
| `slowedKinds` | Liste | `["items", "experience"]` | Welche Sorten weniger Ticks bekommen: items, experience, projectiles. Alles, was selbst denkt, bekommt immer ein langsameres Tempo, ohne hier genannt zu werden, und Maschinen werden nie gebremst |
| `slowDistance` | int, 64 bis 4096 | `192` | Wie weit vom nächsten Spieler, in Blöcken, bevor ein Chunk gebremst wird. Das Spiel hört jenseits von 64 auf, einem Spieler von den meisten Entities zu erzählen, darunter also nichts |
| `slowRate` | int, 1 bis 20 | `4` | Ein Tick von so vielen wird einem gebremsten Chunk gegeben. 1 ist gar keine Bremsung, 20 ist einmal je Sekunde |
| `neverSlowed` | Liste | leer | Entities, die in Ruhe gelassen werden, wie weit sie auch entfernt sind, als namespace:name |
| `slowRecheck` | int, 1 bis 100 | `20` | Wie oft, in Ticks, die Entfernung zum nächsten Spieler neu ermittelt wird. Jeder Spieler zählt für sich, wer allein weit weg ist, hat also weiterhin seinen eigenen ruhigen Raum um sich |

### Land, Halten und was der Mod sagt

`control.chunks` entscheidet diese Gruppe. Die Willkommenszeilen und die Says-Karte, und der Rest der chunks-Gruppe, so wie er portiert ist.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `retrogen` | boolean | `false` | Vorhandene Chunks bei Worldgen-Einträgen mit \"retrogen\": true nachholen. Aus bleiben schon vorhandene Chunks unangetastet. Chunks werden so oder so beim Generieren markiert, ein späteres Einschalten berührt also nur Chunks, die älter sind als das Pack |
| `adoptExistingChunks` | boolean | `false` | Schon vorhandene Chunks behandeln, als hätte dieses Pack sie generiert, sie also markieren, statt sie dem Retrogen zu überlassen. Einschalten, wenn ein Mod ersetzt wird, der dasselbe Erz schon generiert hat, damit Retrogen es nie verdoppelt. Später hinzugefügte Worldgen-Einträge holen sie weiterhin nach |
| `saysCard` | boolean | `false` | Die Zeilen, die dieser Mod sagt, die Begrüßung und später den Fortschritt des Landbaus und die Bedrohungszeilen, als Karte unten rechts statt im Chat zeigen. Die Karte gleitet herein, bleibt acht Sekunden und verblasst, und erscheint auch über einem offenen Bildschirm |
| `saysIcon` | Text | leer | Ein Item, das auf der Karte gezeichnet wird, z. B. minecraft:compass. Leer zeichnet keines |
| `saysColor` | Text | leer | Die Hintergrundfarbe der Karte als Hex, z. B. 1E2630. Leer nimmt ein dunkles Schiefergrau |
| `saysImage` | Text | leer | Ein PNG aus den Client-Assets des Packs, über die Karte gestreckt als ihr Hintergrund, z. B. rubyworld:textures/gui/card.png, über die Farbe gezeichnet. Leer zeichnet keines |
| `pregenOnNewWorld` | int, 0 bis 8192 | `0` | Wie weit um den Spawn, in Chunks, das Land einer Welt gebaut wird, bevor jemand sie spielt. Das Spiel baut 12 Chunks um den Spawn von sich aus, 12 ist also die Untergrenze und 0 heißt nichts darüber hinaus. Höher setzen, um weiter zu reichen als das Spiel |
| `pregenToBorder` | boolean | `false` | Ob das Land einer neuen Welt bis zu ihrer Weltgrenze statt bis zu einer festen Zahl Chunks gebaut wird, zentriert auf die Grenze statt auf den Spawn. Eine Welt, deren Grenze nie hereingezogen wurde, hat keine Grenze zu erreichen und wird übergangen |
| `pregenAllDimensions` | boolean | `false` | Das Land jeder Dimension bauen, die der Server hält, Mod-Dimensionen eingeschlossen, die Oberwelt zuerst und der Rest in Id-Reihenfolge, statt nur die in pregenDimensions. In pregenDimensionsWhenEntered genannte bleiben weiterhin ihrem ersten Besucher überlassen |
| `pregenResume` | boolean | `false` | Ob ein gestoppter oder abgebrochener Durchlauf beim nächsten Laden der Welt dort weitermacht, wo er aufgehört hat, statt von vorn zu beginnen |
| `pregenChunksInFlight` | int, 1 bis 512 | `32` | Wie viele Chunks ein Landbau-Durchlauf beim Spiel auf einmal anfordert. Mehr hält die Generierungs-Threads beschäftigter und den Server weniger ansprechbar für die, die gehalten zusehen |
| `pregenBackup` | boolean | `false` | Die Welt in eine unberührte Sicherung kopieren, sobald die Vorgenerierung fertig ist und die Spieler noch gehalten werden. Die Kopie ist das, was ein Reset wiederherstellt |
| `resetClearsEntities` | boolean | `true` | Jede Entity entfernen, die kein Spieler ist, wenn die Karte zurückgesetzt wird |
| `resetClearsScores` | boolean | `true` | Jedes Ziel, das das Pack führt, beim Kartenreset auf nichts zurücksetzen, damit eine neue Partie bei null beginnt. Die Teams selbst bleiben |
| `spawnChunkRadius` | int, -1 bis 32 | `2` | Wie weit vom Spawnpunkt, in Chunks, Chunks geladen gehalten werden, ob ein Spieler da ist oder nicht. Der Standard 2 hält 25 Chunks, den Wert, den 1.21.1 selbst nutzt, und macht eine neue Welt auf 1.20.1 rund fünfmal schneller fertig als die spieleigenen 11. Auf 1.20.1 setzt es das Spawn-Ticket, das der Server vom Start einer Welt an hält; auf 1.21.1 setzt es die Spielregel spawnChunkRadius, wenn eine Welt startet. -1 lässt jedem Spiel seinen eigenen Wert, 441 Chunks auf 1.20.1 gegenüber 25 auf 1.21.1 |
| `welcomeSays` | Liste | `[WELCOME]` | Willkommenszeilen, bei jedem Login in Grün gezeigt. Ein bloßer Eintrag ist die Zeile für überall; ein dimension=nachricht-Eintrag überschreibt sie für diese Dimension und begrüßt außerdem jede Ankunft dort, z. B. minecraft:the_nether=Welcome to the Nether!. Eine leere Nachricht hinter dem = stellt diese Dimension stumm; eine leere Liste zeigt nichts. Auf diesem Standardwert spricht sie die Sprache jedes Spielers |
| `pregenBorderLimit` | int, 1 bis 1875000 | `8192` | Wie weit eine Grenze höchstens reichen darf, in Chunks je Richtung, bevor der Landbau bis dorthin abgelehnt wird. Das ist da, um einen Fehler nicht wochenlang laufen zu lassen, nicht zum Hochdrehen, und ein Pack kann es nicht setzen. Ein Quadrat von 8192 fasst 268 Millionen Chunks **Nur Config.** |
| `pregenDimensions` | Liste | `["minecraft:overworld"]` | In welchen Dimensionen eine neue Welt ihr Land gebaut bekommt, nach Id, in der angegebenen Reihenfolge, eine nach der anderen |
| `pregenDimensionsWhenEntered` | Liste | leer | Dimensionen, deren Land nicht vorab gebaut wird, sondern beim ersten Mal, wenn jemand einen Fuß hineinsetzt, bis zur selben Reichweite, und alle genauso festhält, bis es fertig ist. Eine hier und in pregenDimensions genannte wird einfach vorab gebaut |
| `pregenRunningSays` | Text | `World pregeneration running, %d%% done` | Die Fortschrittsnachricht, die Spieler sehen, während die Welt generiert, wobei %d der Prozentwert und ein zweites %s die Dimension ist. Leer sagt ihnen nichts. Auf diesem Standardwert spricht sie die Sprache jedes Spielers |
| `pregenFinishedSays` | Text | `World pregeneration finished` | Die Nachricht, die Spieler sehen, wenn die Generierung fertig ist. Leer sagt ihnen nichts. Auf diesem Standardwert spricht sie die Sprache jedes Spielers |
| `pregenStoppedSays` | Text | `World pregeneration stopped` | Die Nachricht, die Spieler sehen, wenn die Generierung vorzeitig gestoppt wird. Leer sagt ihnen nichts. Auf diesem Standardwert spricht sie die Sprache jedes Spielers |
| `pregenSpectatingSays` | Text | `Spectating until the world is ready` | Die Nachricht mitten im Bild, die Spieler sehen, während sie bei der Weltgenerierung als Zuschauer gehalten werden. Leer zeigt nichts. Auf diesem Standardwert spricht sie die Sprache jedes Spielers |
| `pregenLogo` | Text | `center` | Wo das Logo steht, wenn die Vorgenerierung fertig ist: left, center oder right, über dem Text in der Bildmitte. Es wird immer gezeigt; ein unbekanntes Wort gilt als center |
| `pregenBackupSays` | Text | `Pack requested world backup` | Die Nachricht mitten im Bild, die Spieler sehen, während diese Sicherung kopiert wird. Leer zeigt nichts |
| `resetSays` | Text | `Pack requested map reset` | Die Zeile mitten im Bild, die Spielern gezeigt wird, während /rdpl reset die Karte zurücksetzt. Leer setzt still zurück |
| `resetSendsTo` | Text | `spawn` | Wohin ein Reset die Spieler setzt: spawn, eine Position als x,y,z, oder dimension:x,y,z, um sie in eine andere Welt zu schicken |
| `resetRuns` | Text | leer | Eine Funktion, die läuft, nachdem ein Reset die Karte geräumt hat, benannt namespace:pfad. Leer führt nichts aus |

### Leere-Welt

`control.voidWorld` entscheidet diese Gruppe. Leere-Welt-Generierung und ihre Plattform.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `voidWorld` | boolean | `false` | Die gelisteten Dimensionen als leeren Raum mit einer Plattform am Spawnpunkt und nichts Lebendigem generieren, über das erzeugte Preset |
| `voidWorldDimensions` | Liste | `["minecraft:overworld"]` | Welche Dimensionen leer gemacht werden, nach Id. Leer heißt nur die Oberwelt |
| `voidWorldDimensionsAreBlacklist` | boolean | `false` | voidWorldDimensions stattdessen als die Dimensionen behandeln, die in Ruhe gelassen werden |
| `voidPlatformBlock` | Text | `minecraft:stone` | Der Block, aus dem die Plattform der Leere-Welt besteht |
| `voidPlatformHeight` | int, -2032 bis 2031 | `64` | Das y, auf dem die Plattform der Leere-Welt sitzt |
| `voidPlatformSize` | int, 1 bis 255 | `9` | Wie breit die Plattform der Leere-Welt ist, in Blöcken. Auf eine ungerade Zahl abgerundet, damit sie auf dem Spawnpunkt zentriert ist |
| `voidWorld` | Text | `default` | Leere-Welt-Generierung und ihre Plattform [default\|global\|off] |

### Terrain

`control.terrain` entscheidet diese Gruppe. Name, Seed und Spielmodus der Welt beim Erstellen, und der Rest der terrain-Gruppe, so wie er portiert ist.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `worldSeed` | Text | leer | Der Seed, mit dem jede neue Welt gemacht wird, was auch beim Erstellen getippt wurde, so geschrieben, wie man ihn tippen würde. Leer lässt die Wahl in Ruhe |
| `worldGameMode` | Text | leer | Wie jede neue Welt gestartet wird, eines von survival, hardcore, creative, adventure oder spectator. Hardcore ist Überleben, bei dem der Tod die Welt beendet, spielstandweit, dasselbe wie die Wahl auf dem Weltbildschirm. Leer lässt es, wie es der Ersteller der Welt gewählt hat |
| `worldName` | Text | leer | Wie eine neue Welt heißt, wenn der Bildschirm zum Erstellen sich öffnet. Leer lässt es, wie das Spiel sie benennt |
| `worldType` | Text | leer | Der Welttyp, auf dem die geformte Welt aufbaut, eines von default, largebiomes, amplified oder flat; flat ist eine Flachwelt-Oberwelt aus den Schichten in generatorOptions, mit den Städten des Packs darauf. Die Form unten (Höhen, Tiefenstein, Meereshöhe, Grundgestein, Leere) wird als eigenes Welt-Preset erzeugt, unter Welttyp auf dem Weltbildschirm gelistet und dort gewählt, was auch gewählt wurde. Leer baut auf default auf |
| `worldTypeExceptions` | Liste | `["flat", "debug_all_block_states"]` | Welttypen, die ein Spieler wählt und die das erzeugte Preset in Ruhe lässt, etwa flat oder debug_all_block_states. Leer heißt, jede Wahl wird ersetzt |
| `generatorOptions` | Text | leer | Bei worldType flat stattdessen die Schichten von unten nach oben, als der 1.12.2-Flachwelt-Text 3;minecraft:bedrock,59*minecraft:stone,4*minecraft:dirt,minecraft:grass_block;1;village oder als Liste von Schichten, wobei ein nach dem Biom genanntes village, mineshaft oder stronghold diese Vanilla-Struktur einschaltet. Sonst die Geländeeinstellungen der Oberwelt als JSON-Objekt, die Schlüssel, die der angepasste Welttyp von 1.12.2 schrieb. Gelesen werden hier: seaLevel und useLavaOceans. Nur beim Erstellen auf eine Welt angewandt. Leer lässt das Gelände, wie der Welttyp es macht |
| `worldMinHeight` | int, -2032 bis 2016 | `-64` | Der unterste Block der Oberwelt, ein Vielfaches von 16 bis hinunter zu -2032. Der eigene Boden des Spiels ist -64; tiefer macht eine Tiefenwelt unter dem Vanilla-Gelände, massiver Stein, bis die Worldgen-Ebene ihn schnitzt oder noiseCaves die Höhlen des Spiels hinunterträgt. Nur über das erzeugte Preset angewandt |
| `worldMaxHeight` | int, -2016 bis 2032 | `320` | Der Block über der Decke der Oberwelt, ein Vielfaches von 16 bis 2032, höchstens 4064 über worldMinHeight. Die eigene Decke des Spiels ist 320; höher lässt offenen Himmel über dem Vanilla-Gelände |
| `deepStone` | Text | leer | Der Block, aus dem die Welt unter dem Vanilla-Gelände besteht, wenn worldMinHeight unter -64 geht, etwa der eigene Tiefenschiefer eines Packs. Er geht über die acht Schichten unter -64 in Tiefenschiefer über, so wie Tiefenschiefer in Stein übergeht. Leer behält Stein |
| `noiseCaves` | Text | `off` | Wo die Höhlen, Tunnel, Nudeln und Aquifere des Spiels weitergehen, wenn worldMinHeight unter -64 geht: off lässt die Welt unter dem Vanilla-Gelände als massiven Tiefenstein für die Worldgen-Ebene zum Schnitzen, deep trägt sie bis zum Boden hinunter, mit den Lavaseen in dessen unterste zehn Schichten verlegt, world bedeutet in dieser Version dasselbe, weil das Vanilla-Gelände sie ohnehin hat |
| `worldSpawn` | Text | leer | Wo jede neue Welt spawnt, geschrieben als x,z oder x,y,z. Ohne y wird der Boden an dieser Stelle genommen. Nur beim Erstellen auf eine Welt angewandt. Leer überlässt die Wahl dem Spiel |
| `worldBorder` | int, 0 bis 60000000 | `0` | Wie breit, in Blöcken, die Weltgrenze in jeder neuen Welt steht. Nur beim Erstellen auf eine Welt angewandt. 0 lässt die Grenze, wo das Spiel sie setzt |
| `worldTime` | int, -1 bis 23999 | `-1` | Die Tageszeit der Oberwelt festhalten, in Ticks, dieselbe Zahl, die /time set nimmt, 18000 ist also Mitternacht. Die Uhr steht und bewegt sich nie. -1 lässt die Zeit laufen |
| `worldDifficulty` | Liste | leer | Den Schwierigkeitsgrad festhalten, einer von peaceful, easy, normal oder hard, für die ganze Welt. Der Schwierigkeitsgrad ist in dieser Version spielstandweit, ein als dimension=schwierigkeit geschriebener Eintrag wird also nur für die Oberwelt gelesen. Leer lässt es, wie gewählt |
| `caveRegionPlainWeight` | int, 0 bis 1000 | `4` | Das Gewicht des schlichten, regionslosen Untergrunds gegenüber den eigenen Gewichten der Höhlenregionen. Höher lässt mehr Untergrund ohne Region |
| `caveRegionCells` | int, 16 bis 4096 | `128` | Wie breit eine Höhlenregionszelle in Blöcken ist. Höhlenregionen aus Packs werden in Zellen etwa dieser Größe über den Untergrund gemalt |
| `caveRegionCellsY` | int, 16 bis 4096 | `64` | Wie hoch eine Höhlenregionszelle in Blöcken ist |
| `worldGravity` | Liste | leer | Schwerkraft skalieren, als Faktor auf Vanilla, wobei 1.0 unverändert und 0.17 mondartig ist, für Spieler und Mobs. Ein bloßer Wert gilt für jede Dimension, und ein als dimension=wert geschriebener Eintrag gilt für diese Dimension allein und gewinnt über den bloßen. Leer lässt die Schwerkraft in Ruhe |
| `worldFallDamage` | Liste | leer | Sturzschaden auf dieselbe Weise skalieren, 0.5 halbiert ihn und 2.0 verdoppelt ihn |
| `worldJumpStrength` | Liste | leer | Sprungkraft auf dieselbe Weise skalieren, 1.5 springt anderthalbmal so hoch |
| `worldTerminalVelocity` | Liste | leer | Die höchste Fallgeschwindigkeit eines Mobs oder Spielers auf dieselbe Weise skalieren, 0.5 fällt mit halber Vanilla-Höchstgeschwindigkeit |
| `cloudHeight` | Liste | leer | Das y, auf dem Wolken gezeichnet werden, als dimension=y-Einträge. Eine bloße Zahl gilt für jede Dimension. Leer behält die eigene Wolkenhöhe des Spiels, 192 in der Oberwelt |
| `worldBelow` | Liste | leer | Eine andere Dimension unter diese stapeln: wer aus dem Boden der Welt fällt, kommt in der genannten Dimension an, unter deren Decke bei demselben x und z, weiter fallend. Einträge werden als dimension=ziel geschrieben, etwa minecraft:overworld=minecraft:the_nether, um den Nether unter die Oberwelt zu hängen; eine bloße Id gilt für jede Dimension. Um durchzugraben, muss das Grundgestein des Bodens weggelassen sein, was worldSeamBedrock entscheidet. Leer heißt, der Boden bleibt der Boden |
| `worldAbove` | Liste | leer | Dasselbe für die Decke: wer über die Decke der Welt steigt, kommt in der genannten Dimension an, über deren Boden. Geschrieben wie worldBelow |
| `worldSeamEntities` | boolean | `true` | Ob liegende Items, Mobs und andere Entities die Weltnähte ebenfalls passieren, oder nur Spieler. Reiter und Reittiere wechseln eines nach dem anderen |
| `worldSeamBedrock` | boolean | `false` | Das Grundgestein an einer Nahtgrenze trotzdem behalten. Aus generiert eine Dimension, deren Boden oder Decke eine worldBelow- oder worldAbove-Naht trägt, dort kein Grundgestein, der Weg hindurch lässt sich also graben. Schon generierte Chunks behalten, was sie haben |

### Rezepte

`control.recipes` entscheidet diese Gruppe. Rezept- und Ofensperre und ihre Whitelists.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `blockRecipes` | boolean | `false` | Jedes Craftingrezept entfernen und nur die Mods in recipeWhitelist behalten. Nimm den Namespace deines Packs auf, um seine eigenen Rezepte zu behalten |
| `recipeWhitelist` | Liste | `["minecraft"]` | Mod-IDs, deren Craftingrezepte bei eingeschaltetem blockRecipes überleben. Nimm den Namespace deines Packs auf, um seine eigenen Rezepte zu behalten |
| `blockedRecipeMods` | Liste | leer | Mod-IDs, deren Craftingrezepte rundweg entfernt werden, wem sie auch gehören und was die Whitelist auch sagt |
| `recipeMatch` | Text | `recipe` | Woraus die Mod-ID beim Sperren von Craftingrezepten gelesen wird. 'recipe' nimmt den eigenen Namen des Rezepts, 'output' das Item, das es herstellt, 'both' sperrt, wenn eines passt, und verschont, wenn eines auf der Whitelist steht |
| `blockFurnaceRecipes` | boolean | `false` | Jedes Ofen-, Schmelzofen-, Räucherofen- und Lagerfeuerrezept entfernen und nur die Mods in furnaceWhitelist behalten. Die Mod wird aus dem erzeugten Item gelesen |
| `furnaceWhitelist` | Liste | `["minecraft"]` | Mod-IDs, deren Ofenrezepte bei eingeschaltetem blockFurnaceRecipes überleben. Nimm den Namespace deines Packs auf, um seine eigenen Rezepte zu behalten |
| `blockedFurnaceMods` | Liste | leer | Mod-IDs, deren Ofenrezepte rundweg entfernt werden, was die Whitelist auch sagt |
| `logBlockedRecipes` | boolean | `true` | Pro Mod protokollieren, wie viel gesperrt wurde, damit du siehst, was auf die Whitelist gehört |
| `furnace` | boolean | `true` | furnace/*.json-Dateien anwenden, die Ofenrezepte hinzufügen und entfernen **Nur Config.** |
| `removals` | boolean | `true` | recipe_removals/*.json-Dateien anwenden, die Rezepte nach Name, Namespace oder Ergebnis löschen **Nur Config.** |
| `skipMissingItems` | boolean | `true` | Rezepte überspringen, die ein nicht registriertes Item nutzen, statt sie scheitern zu lassen. Die Anzahl wird einmal protokolliert **Nur Config.** |

### Befehle

`control.commands` entscheidet diese Gruppe. Wer die eigenen Befehle des Mods ausführen darf: die goto-Berechtigungsstufen.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `gotoLevel` | int, 0 bis 4 | `3` | Die Berechtigungsstufe für /rdplserver goto <name>, der den Absender zur nächsten bringt. 3 ist ein Operator, die Stufe, auf der jeder andere Teil des Befehls sitzt. 2 lässt auch einen Befehlsblock ihn ausführen, ein Pack kann den Sprung also auf einen Knopf oder eine Druckplatte legen, ohne jemandem den Rest des Befehls zu geben. 0 lässt jeden Spieler ihn tippen. Die anderen Teile von /rdplserver bleiben bei 3, was auch hier steht |
| `gotoNextLevel` | int, 0 bis 4 | `3` | Die Berechtigungsstufe für /rdplserver goto <name> next, der die zuletzt angesteuerte überspringt und eine andere findet. Dieselbe Skala wie gotoLevel |
| `gotoBackLevel` | int, 0 bis 4 | `3` | Die Berechtigungsstufe für /rdplserver goto <name> back, der den Absender zur vorherigen zurückbringt. Dieselbe Skala wie gotoLevel |
| `gotoPlaceLevels` | Liste | leer | Berechtigungsstufen für einzelne Orte, als name=stufe-Einträge, einer je Zeile, die die drei Einstellungen oben für diesen Ort allein und in allen drei Formen überschreiben. Der Name ist das, was du hinter goto tippen würdest, ein Vanilla-Name wie Village oder Mansion oder ein Name, den ein Pack mit locateAs für seine eigenen Strukturen angemeldet hat. Dieselbe Skala: 3 ein Operator, 2 auch ein Befehlsblock, 0 jeder. Ein Pack kann so den Weg zu seinen eigenen Ruinen öffnen, während jede Vanilla-Struktur verschlossen bleibt, oder umgekehrt. Ein nirgends angemeldeter Name wird mit einer Notiz im Log ignoriert |

### Worldgen, nur Config

Diese `worldgen`-Schlüssel gehören zu keiner Gruppe und sind allein die der Config.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `worldgenDebug` | boolean | `false` | Die Debug-Zeilen, auf die andere Meldungen verweisen, nach logs/rdpl.log schreiben, etwa welches Pack eine Datei geliefert hat und was jeder Befehl getan hat. Sehr ausführlich |
| `worldTemplate` | Text | `auto` | Die Einstellungen welcher Weltvorlage gelten. Ein Pack fügt eine in worldtemplates/*.json hinzu, und du nennst sie hier als namespace:name. 'auto' nimmt die Vorlage aus dem Pack mit der höchsten Priorität. Leer nimmt keine |
| `tellWorldType` | boolean | `true` | Einem Spieler beim Beitritt zu einer mit dem erzeugten Preset gemachten Welt im Chat sagen, welche Vorlage sie geformt hat. Ein Pack kann das nicht setzen |
| `worldBorderLimit` | int, 1 bis 60000000 | `60000000` | Die breiteste Grenze, die ein Pack über worldBorder verlangen darf. Ein Pack, das mehr verlangt, wird abgewiesen, und die Grenze bleibt, wo das Spiel sie setzt. Ein Pack kann das nicht setzen |
| `retrogenKey` | Text | `0000` | Ändern, damit jeder Chunk für jeden Worldgen-Eintrag wieder für Retrogen infrage kommt. Neue Adern werden über das gelegt, was schon da ist |
| `retrogenChunksPerTick` | int, 1 bis 64 | `2` | Wie viele schon generierte Chunks je Tick nachgeholt werden. Höher ist schneller, ruckelt aber mehr |

### Die Kategorie `packs`

Wie Pack-Ordner gefunden und ausgeliefert werden. Nur Config.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `rootDirectory` | Text | `rdploader` | Ordner, aus dem Packs geladen werden, relativ zum .minecraft-Verzeichnis. Ein absoluter Pfad geht auch. Braucht einen Neustart |
| `overrideResourcePacks` | boolean | `true` | Das Asset-Pack über die vom Spieler gewählten Ressourcenpakete und die eigenen Datenpakete der Welt setzen. Ein Pack namens RDPLO... überschreibt immer, RDPLN... nie |
| `warnOnCaseMismatch` | boolean | `true` | Warnen, wenn eine Datei nur passt, weil das Dateisystem Groß- und Kleinschreibung nicht unterscheidet. Solche Packs brechen unter Linux |
| `logContents` | boolean | `false` | Jedes gefundene Pack protokollieren und wie viele Dateien es liefert |
| `traceUnresolvedVariables` | boolean | `false` | Beim ersten Mal einen Stacktrace protokollieren, wenn eine Datei mit einem '#' im Namen angefordert wird, mit dem, was danach gefragt hat |

### Die Kategorie `content`

Blöcke, Items, Flüssigkeiten und alles andere, was Packs definieren. Nur Config.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `load` | boolean | `true` | Die Blöcke, Items, Flüssigkeiten, Materialien und Kreativ-Tabs registrieren, die Packs definieren. Braucht einen Neustart |
| `vanillaClients` | boolean | `false` | Reine Vanilla-Clients bedienen: nichts aus einem Pack wird registriert, keine Blöcke, Items, Flüssigkeiten oder Kreativ-Tabs, sodass ein Client ohne den Mod beitreten kann. Alles, was nur auf dem Server lebt, gilt weiterhin. Braucht einen Neustart |
| `sounds` | boolean | `true` | Die in sounds/*.json genannten Sound-Events registrieren, damit Packs eigenes Audio mitbringen können |
| `fuels` | boolean | `true` | fuels/*.json-Dateien anwenden, die Items eine Brenndauer im Ofen geben |
| `potions` | boolean | `true` | Die Trankeffekte und Trankarten registrieren, die potions/*.json und potion_types/*.json in Packs beschreiben. Braucht einen Neustart |
| `brewing` | boolean | `true` | brewing/*.json-Dateien anwenden, die Braustand-Rezepte hinzufügen |
| `villagers` | boolean | `true` | Die Dorfbewohner-Berufe registrieren, die villagers/*.json beschreibt, und die Handel in trades/*.json anwenden. Braucht einen Neustart |
| `entities` | boolean | `true` | Die Entity-Varianten registrieren, die entities/*.json in Packs beschreibt. Braucht einen Neustart |
| `overrides` | boolean | `true` | overrides/<namespace>/<name>.json-Dateien anwenden, die Eigenschaften von Blöcken, Items und Trankarten ändern, die es schon gibt, Vanilla oder Mod |
| `hardness` | boolean | `true` | hardness/*.json-Dateien anwenden, die einer Gruppe von Blöcken einen Faktor für Abbauzeit und Explosionswiderstand geben, je Blockposition gewürfelt |
| `shovelPaths` | boolean | `true` | Eine Schaufel Blöcke mit behavesAs path in einen Pfad verwandeln lassen, und einen Pfad beim Schleichen zurückverwandeln |
| `shovelPathBecomes` | Text | leer | Wozu eine Schaufel diese Blöcke macht. Leer nimmt den Trampelpfad |
| `shovelPathReverts` | Text | leer | Wozu Schleichen mit einer Schaufel einen Pfad zurückverwandelt. Leer nimmt Erde |
| `hoeTilling` | boolean | `true` | Eine Hacke Blöcke mit behavesAs till pflügen lassen |
| `hoeTillsInto` | Text | leer | Wozu eine Hacke diese Blöcke macht. Leer nimmt Ackerboden |
| `caneMaxHeight` | int, 1 bis 255 | `3` | Wie hoch Vanilla-Zuckerrohr wächst. Vanilla ist 3. Vom Pack definierte Rohrblöcke nutzen ihren eigenen growth-Abschnitt und ignorieren das |
| `cactusMaxHeight` | int, 1 bis 255 | `3` | Dasselbe für Vanilla-Kakteen |

### Die Kategorie `data`

Beute und Registry-Namen. Nur Config.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `lootInjections` | boolean | `true` | loot_injections/*.json-Dateien anwenden, die Pools zu Beutetabellen hinzufügen, die es schon gibt, statt die ganze Tabelle zu ersetzen |
| `playerLoot` | boolean | `true` | player_loot/*.json-Dateien anwenden, die beim Tod eines Spielers eine Beutetabelle würfeln und das Ergebnis fallen lassen, zusätzlich zum oder anstelle des Inventars |
| `registryRemaps` | boolean | `true` | registry_remap-Dateien anwenden, die einen Registry-Eintrag umbenennen, damit vor der Umbenennung gespeicherte Welten ihre Blöcke und Items behalten, statt sie zu verlieren |

### Die Kategorie `tweaks`

Kleine Änderungen daran, wie Vanilla sich verhält. Nur Config; siehe [Bonus: Vanilla-Tweaks](#bonus-vanilla-tweaks).

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `promptLeafDecay` | boolean | `true` | Blätter, die ihren Baum verlieren, verwelken binnen einer Sekunde, statt auf Random-Ticks zu warten |
| `lenientPaths` | boolean | `true` | Pfade und gepflügter Boden lassen sich unter einem Block anlegen und bleiben liegen, wenn einer darübergesetzt wird |
| `unbreakableSpawners` | boolean | `false` | Mobspawner lassen sich weder abbauen noch sprengen. Der Kreativmodus entfernt sie weiterhin. Braucht einen Neustart |
| `experimentalWarning` | boolean | `false` | Die Warnung des Spiels vor experimentellen Einstellungen zeigen, wenn eine Welt erstellt oder geöffnet wird. Aus beantwortet sie, als hättest du auf Fortfahren geklickt |
| `privacy` | boolean | `true` | Telemetrie und Chat-Meldung des Spiels abschalten: kein Telemetrie-Ereignis wird gesendet oder protokolliert, der Client signiert keine Chatnachricht, der Server führt keine Chat-Sitzung und verlangt keine, keine Nachricht, die jemand sendet, lässt sich also melden. Ein Pack kann das nicht setzen. Greift beim nächsten Beitritt zu einer Welt oder einem Server |
| `darkSplash` | boolean | `true` | Zeichnet den Ladebildschirm dunkel mit dem Logo des Pack-Loaders statt dem des Spiels: das Logo wird beim Aufbau des Bildschirms getauscht, und die spieleigene Option Monochromes Logo wird eingeschaltet, wenn sie noch aus ist, was beim nächsten Start wirkt. Aus lässt die Option, wie sie ist |

## Blast Plaster Integration

`<namespace>/blastplaster/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

Blast Plaster kümmert sich um das Verhalten nach einer Explosion: Krater Block für Block heilen, baumbewusstes Fällen, Drop-Steuerung. Für sich allein liest es eine globale Config. Von einem Pack gesteuert antwortet es **pro Dimension**, und das Pack liefert die Entscheidung mit, statt Spieler eine Config bearbeiten zu lassen. Ohne Pack-Dateien, oder ohne installiertes Blast Plaster, tut hier nichts etwas, und der Ordner wird mit einer Zeile im Log übersprungen.

Schlüssel oben in der Datei gelten überall; ein `dimensions`-Block überschreibt sie für eine Dimension nach Id. Alles, was ein Pack nie nennt, behält, was Blast Plasters eigene Config sagt, ein Pack setzt also die Handvoll, die es interessiert, und lässt den Rest in Ruhe.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht.

```json
{
  "explosionMode": "EJECT_DROPS",
  "healCreepers": true,
  "healNonPlayerTNT": true,
  "healWither": true,
  "healAll": false,
  "processPlayerIgnitedTNT": false,
  "customEntitiesToHeal": ["icbmclassic:missile"],
  "healFullTrees": true,
  "maxTreeSize": 400,
  "minimumTicksBeforeHeal": 200,
  "randomTickVar": 20,
  "overrideBlocks": false,
  "enableFakeTossedBlocks": true,
  "enableExplosionFlash": true,
  "explosionFlashDuration": 10,
  "explosionFlashLightLevel": 15,
  "explosionFlashParticleCount": 40,
  "explosionFlashPulses": 2,
  "enableExplosionSmoke": true,
  "explosionSmokeDuration": 100,
  "explosionSmokeParticleCount": 30,
  "playerTNTAlwaysDrops": false,
  "playerTNTDropFullBlocks": false,
  "enableDropSuppression": true,
  "dtSpecialDrops": true,
  "preventMobDrops": false,
  "blockConversions": ["minecraft:stone=minecraft:cobblestone@0.75", "#minecraft:logs=minecraft:stripped_oak_log@0.5"],
  "dimensions": {
    "minecraft:the_nether": { "explosionMode": "HEAL", "minimumTicksBeforeHeal": 200 },
    "minecraft:the_end": { "enableExplosionSmoke": false }
  }
}
```

`explosionMode` ist der Hauptschalter: `HEAL` stellt den Krater mit der Zeit wieder her, `EJECT_DROPS` lässt das Loch und droppt etwa ein Drittel der Blöcke (Vanilla-Verhalten), `VISUAL_TOSS` lässt das Loch und droppt nichts. Von einem Pack gesteuert ist der Standard `EJECT_DROPS` (nicht Blast Plasters `HEAL`), eine unkonfigurierte Installation verhält sich also wie Vanilla.

| Schlüssel | Wert | Was er macht |
| --- | --- | --- |
| `explosionMode` | `HEAL`, `EJECT_DROPS`, `VISUAL_TOSS` | Was nach dem Knall passiert |
| `healCreepers`, `healNonPlayerTNT`, `healWither`, `healAll` | true oder false | Welche Explosionen überhaupt behandelt werden |
| `processPlayerIgnitedTNT` | true oder false | Ob TNT, das ein Spieler gezündet hat, mit den übrigen behandelt wird |
| `customEntitiesToHeal` | Liste von Entity-Namen | Explosionen anderer Mods, benannt als `modid:entity` |
| `healFullTrees` | true oder false | Ein von einer Explosion angeschnittener Baum wird ganz genommen oder ganz wiederhergestellt, statt durchgeschnitten zu werden |
| `maxTreeSize` | Zahl | Die meisten Blöcke, die ein Baum beanspruchen darf, bevor er in Ruhe gelassen wird |
| `minimumTicksBeforeHeal`, `randomTickVar` | Zahlen | Wie lange es dauert, bis das Heilen beginnt, und wie ungleichmäßig sein Tempo ist |
| `overrideBlocks` | true oder false | Ob das Heilen überschreibt, was seither in das Loch gebaut wurde |
| `enableFakeTossedBlocks` | true oder false | Die Trümmer, die aus der Explosion fliegen |
| `enableExplosionFlash` | true oder false | Der helle Blitz im Moment der Explosion |
| `explosionFlashDuration`, `explosionFlashLightLevel`, `explosionFlashParticleCount`, `explosionFlashPulses` | Zahlen | Wie lange der Blitz dauert, wie hell er brennt, wie viele Partikel er wirft und wie oft er pulsiert |
| `enableExplosionSmoke` | true oder false | Die Rauchsäule danach |
| `explosionSmokeDuration`, `explosionSmokeParticleCount` | Zahlen | Wie lange der Rauch bleibt und wie dicht er steht |
| `playerTNTAlwaysDrops`, `playerTNTDropFullBlocks` | true oder false | Was das eigene TNT eines Spielers zurücklässt |
| `enableDropSuppression`, `dtSpecialDrops` | true oder false | Drops innerhalb einer Explosion, und die eigenen Drops von Dynamic Trees |
| `preventMobDrops` | true oder false | Ob von einer Explosion getötete Mobs noch droppen |
| `blockConversions` | Liste von Regeln | Worin ein gesprengter Block verwandelt wird, statt unverändert zurückzukehren, sodass ein Bauwerk pro Explosion eine Stufe verfällt |

`blockConversions` bestimmt, worin ein gesprengter Block verwandelt wird, statt unverändert zurückzukehren. Eine Regel lautet `<source>=<result>[@chance]`: die Quelle ist eine Block-ID oder ein Block-Tag mit vorangestelltem `#`; das Ergebnis ist eine Block-ID oder `nothing`, damit die Stelle leer bleibt; die Chance reicht von 0.0 bis 1.0 und ist standardmäßig 1.0. Die erste passende Regel gewinnt, spezifische Regeln gehören also über die allgemeinen, und ein Block, der bereits das Ergebnis einer Regel ist, wird nie erneut umgewandelt — eine Mauer gibt pro Explosion eine Stufe nach, statt ganz zu verschwinden.

**Ganz wie Vanilla:** `EJECT_DROPS` plus `healFullTrees`, `enableFakeTossedBlocks`, `enableExplosionFlash`, `enableExplosionSmoke`, `preventMobDrops` und `playerTNTAlwaysDrops` alle aus. Jeder Schlüssel ist pro Dimension setzbar.

**Vanilla-Clients** sehen nichts Ungewöhnliches. Der Blitz ist das einzige Feature, das einen Block setzt, mit gesetztem `vanillaClients` wird er also zwangsweise abgeschaltet; alles andere sind Partikel und Items, die ein einfacher Client versteht.

Keine Pack-Schlüssel: Blast Plasters Debug-Protokollierung und seine Stamm-zu-Blättern-Zuordnung (die Baumerkennung muss spielweit eine Antwort sein). Beides bleibt in Blast Plasters eigener Config.

---

# Referenz

## Wertelisten

Das sind die Namen, die der Parser überall dort annimmt, wo die Tabellen oben „eines der Materialien“ und so weiter sagen. Alles Unerkannte wird protokolliert und durch den Standard ersetzt.

**Blockmaterialien.** `air`, `grass`, `ground`, `wood`, `rock`, `iron`, `anvil`, `water`, `lava`, `leaves`, `plants`, `vine`, `sponge`, `cloth`, `fire`, `sand`, `circuits`, `carpet`, `glass`, `redstone_light`, `tnt`, `coral`, `ice`, `packed_ice`, `snow`, `crafted_snow`, `cactus`, `clay`, `gourd`, `dragon_egg`, `portal`, `cake`, `web`, `piston`, `barrier`, `structure_void`. Das Spiel selbst hat keine Materialien mehr; jeder Name steht für die Kartenfarbe, den Sound, das Werkzeug und das Kolbenverhalten, die dieses Material hatte.

**Soundtypen.** `wood`, `ground`, `plant`, `stone`, `metal`, `glass`, `cloth`, `sand`, `snow`, `ladder`, `anvil`, `slime`.

**Kartenfarben.** `air`, `grass`, `sand`, `cloth`, `tnt`, `ice`, `iron`, `foliage`, `snow`, `clay`, `dirt`, `stone`, `water`, `wood`, `quartz`, `adobe`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `silver`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, `black`, `gold`, `diamond`, `lapis`, `emerald`, `obsidian`, `netherrack`.

**Render-Layer.** `solid`, `cutout`, `cutout_mipped`, `translucent`. Leer gelassen wählt der Block einen, der zu seinem Typ passt.

**Seltenheiten.** `common`, `uncommon`, `rare`, `epic`.

**Fackelpartikel.** `none`, `flame`, `colored`. `colored` nutzt `particleColor`.

**Werkzeugklassen.** `pickaxe`, `axe`, `shovel`, `hoe`, `sword`.

**Rüstungsslots.** `head` oder `helmet`, `chest` oder `chestplate`, `legs` oder `leggings`, `feet` oder `boots`.

**Färbungen.** `biome`, `none` oder eine sechsstellige Hex-Farbe. Farben überall in einer Definition sind Hex, mit oder ohne führendes `#`.

**Verhaltensweisen** für `behavesAs`. `till`, `path`.

**Pflanzentypen** für `plantTypes`. `plains`, `desert`, `beach`, `cave`, `water`, `nether`, `crop`. Nur 1.20.1; 1.21.1 liest den Schlüssel und ignoriert ihn.

**Biomtypen**, die Wörter, die überall dort für einen Biom-Tag stehen, wo eine Tabelle „Liste von Biomtypen“ sagt, in `biomeTypes`, den `types` eines Bioms, den `roles` einer Vorlage und einem `biomes`-Abschnitt: `ocean`, `deepocean`, `beach`, `river`, `mountain`, `mesa`, `hills`, `coniferous`, `jungle`, `forest`, `savanna`, `overworld`, `nether`, `end`, `hot`, `cold`, `sparse`, `dense`, `wet`, `dry`, `spooky`, `dead`, `lush`, `mushroom`, `magical`, `rare`, `plateau`, `modified`, `water`, `desert`, `plains`, `swamp`, `sandy`, `snowy`, `wasteland`, `void`. Die Vanilla-Wörter bilden sich auf `minecraft:is_*`-Tags ab und die übrigen auf die Konventions-Tags, `forge:is_*` auf 1.20.1 und `c:is_*` auf 1.21.1. Ein ausgeschriebener Tag, `minecraft:is_forest` oder `#minecraft:is_forest`, wird genommen, wie er ist. Groß- und Kleinschreibung spielt keine Rolle, das `FOREST` aus 1.12.2 liest sich also weiterhin.

**Rollen** für die `roles` einer Weltvorlage. Jedes Biomtyp-Wort oben: jedes nennt ein Biom, das die Biome mit diesem Tag füllt, sobald die Sperre sie entfernt hat, `"ocean": "mypack:ruby_ocean"` setzt den Rubinozean also überall dorthin, wo ein Ozean gesperrt wurde.

**Strukturen** für die `structures` einer Weltvorlage und die eigenen Listen der Gruppe `structures`: die 1.12.2-Namen `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges` und `endcities`, oder jedes Structure-Set, das das Spiel oder ein Mod mitbringt, etwa `pillager_outposts`, `ancient_cities`, `trail_ruins`, `shipwrecks`, `ocean_ruins`, `ruined_portals`, `nether_fossils`, `buried_treasures`, `desert_pyramids`, `jungle_temples`, `igloos`, `swamp_huts`, `woodland_mansions`, `ocean_monuments`, `nether_complexes`, `end_cities`. Ein 1.12.2-Name wird als die Sets gelesen, für die er stand, `temples` sind also die Pyramiden, die Dschungeltempel, die Iglus und die Sumpfhütten zusammen.

**Kreaturtypen** für Biom-Spawns und -Raten. `creature`, `monster`, `ambient`, `water`.

**Erztypen** für `oreTypes`. `COAL`, `IRON`, `COPPER`, `GOLD`, `REDSTONE`, `DIAMOND`, `LAPIS`, `EMERALD`, `QUARTZ`, `DIRT`, `GRAVEL`, `DIORITE`, `GRANITE`, `ANDESITE`, `TUFF`, `CLAY`, `SILVERFISH`, `CUSTOM` für jedes andere Erz.

**Schadensarten** für das `immuneTo` einer Entity-Variante. Die eigenen Schadensart-Namen des Spiels: `fall`, `drown`, `explosion`, `magic`, `cactus`, `lava`, `wither`, `starve`, `in_wall`, `freeze`, `lightning_bolt`, `fire`, `dragon_breath` und der Rest von `minecraft:damage_type/`.

## Ordnerliste

Jeder Ordner, mit vollem Pfad und einem Link zum Abschnitt, der ihn beschreibt, steht unter [Wo die Dateien liegen](#wo-die-dateien-liegen).

## Befehle

`/rdpl` läuft auf deinem eigenen Rechner und braucht keine Rechte, weil alles, was er anfasst, dir gehört. Ein Reload liest den Ordner neu ein, der dir gehört, und lädt deine eigenen Ressourcen neu; er erreicht keinen Server, die Kopie des Servers wird also stattdessen mit `/rdplserver reload` neu geladen. Im Einzelspieler sind beide dieselbe Maschine, `/rdpl reload` ist dort also auch das, was deine [Eigenschafts-Overrides](#eigenschaften-überschreiben) erneut anwendet und deine Teams neu aufstellt.

| Befehl | Stufe | Was er macht |
| --- | --- | --- |
| `/rdpl list` | keine | Jedes geladene Pack, seine Priorität und was es enthält. Klick ein Pack an, um eine Datei darin nachzuschlagen |
| `/rdpl which <namespace:path>` | keine | Welches Pack eine bestimmte Datei liefert und welche Packs es dabei verdeckt |
| `/rdpl reload` | keine | Den Ordner neu einlesen und alles neu laden, den eigenen Ressourcen-Reload des Spiels eingeschlossen |
| `/rdpl unused` | keine | Dateien in deinen Packs, nach denen noch nichts gefragt hat, meist ein Tippfehler im Pfad |
| `/rdpl config unused` | keine | Optionsdateien in `rdploader/config`, die kein installiertes Pack mehr definiert |
| `/rdpl config prune` | keine | Diese Dateien löschen |
| `/rdpl pixelmap <namespace:path>` | keine | Was aus einer [Pixelkarte](#texturen-als-pixelkarte) geworden ist, Zeichen für Zeichen |
| `/rdpl biome list [all]` | keine | Jedes Biom, das generieren kann, mit seiner Id; `all` nimmt die dazu, die nichts generieren kann |
| `/rdpl biome here` | keine | Das Biom, in dem du stehst |
| `/rdpl locate <name>`, `goto <name>`, `vein <eintrag> [radius]` | die des Servers | Verknüpft. Wird wortwörtlich an `/rdplserver` weitergereicht, der entscheidet, siehe die Tabelle unten |

**Welche Server-Unterbefehle verknüpft sind und warum die übrigen nicht.** `locate`, `goto` und `vein` können immer nur die des Servers meinen, denn nur der Server kennt die Welt, `/rdpl` gibt sie also weiter und bietet ihre Orte in der Tab-Vervollständigung an. Die übrigen, `reload`, `list`, `which`, `unused`, `config`, `pixelmap` und `biome`, behalten ihre eigene Bedeutung von deinen Packs und deinem Client. Die eigene Berechtigungsprüfung des Servers entscheidet über einen verknüpften Befehl, ein Client kann sie also weder umgehen noch eine erfundene Antwort bekommen.

Auf einem dedizierten Server macht `/rdplserver` dasselbe für die Kopie des Ordners auf dem Server. Die Spalte Stufe ist die Berechtigungsstufe, die ein Absender braucht: `3` ist ein Operator, `2` lässt auch Befehlsblöcke zu, `0` ist jeder Spieler, und `4` liegt über Operator und erreicht niemanden.

| Befehl | Stufe | Was er macht |
| --- | --- | --- |
| `/rdplserver reload` | 3 | Den Ordner des Servers neu einlesen und alles neu laden, dann die Teams und Ziele neu aufstellen |
| `/rdplserver list` | 3 | Jedes Pack, das der Server geladen hat, seine Priorität und was es enthält |
| `/rdplserver which <namespace:path>` | 3 | Welches Pack eine bestimmte Datei liefert und welche Packs es dabei verdeckt |
| `/rdplserver unused` | 3 | Dateien in den Packs des Servers, nach denen nichts gefragt hat |
| `/rdplserver config unused` | 3 | Optionsdateien in `rdploader/config`, die kein installiertes Pack mehr definiert |
| `/rdplserver config prune` | 3 | Diese Dateien löschen |
| `/rdplserver pixelmap <namespace:path>` | 3 | Was aus einer Pixelkarte geworden ist |
| `/rdplserver oregen` | 3 | Laufende Summen der blockierten Erzgenerierung, pro Mod und Typ |
| `/rdplserver biome list [all]` | 3 | Jedes Biom, das auf dem Server generieren kann, mit seiner Id; `all` nimmt die dazu, die nichts generieren kann |
| `/rdplserver biome here` | 3 | Das Biom, in dem du stehst |
| `/rdplserver dimensions` | 3 | Jede Dimension, auch die, die Packs hinzugefügt haben |
| `/rdplserver gate` | 3 | Jedes Tor, seine Dimension, seinen Geltungsbereich und ob es offen ist |
| `/rdplserver gate check <player>` | 3 | Welche Tore ein Spieler passiert hat |
| `/rdplserver gate grant <player> <gate>` | 3 | Ein Tor für einen Spieler öffnen |
| `/rdplserver gate revoke <player> <gate>` | 3 | Es wieder schließen |
| `/rdplserver pregen <radius>` | 3 | Jeden Chunk in so vielen Chunks Umkreis um die Stelle bauen, an der er ausgeführt wird. Siehe [Vorgenerierung](#vorgenerierung) |
| `/rdplserver pregen status` | 3 | Wie weit ein Lauf ist |
| `/rdplserver pregen stop` | 3 | Ihn beenden |
| `/rdplserver vein <eintrag> [radius]` | 3 | Wo ein Worldgen-Eintrag der Form `vein` seine Adern in so vielen Chunks (Standard 8) um die Stelle angelegt hat, an der er ausgeführt wird, die nächste zuerst, ob diese Chunks schon da sind oder nicht |
| `/rdplserver locate <name>` | 3 | Die nächste Struktur, die ein Pack unter diesem `locateAs`-Namen gesetzt hat |
| `/rdplserver intro` | 3 | Das Welt-Intro beim nächsten Beitritt noch einmal abspielen lassen. Er löscht immer nur dein eigenes |
| `/rdplserver team`, `team join [name]`, `team leave`, `team vote <player>`, `team claim` | 3 | Die Seiten, die ein Pack aufgestellt hat, und die Wege hinein und hinaus, siehe [Teams](#teams) |
| `/rdplserver reset` | 3 | Setzt die Karte zurück, wie es ein Rundenende tut: Alle werden festgehalten, die Entities weggefegt, die Punkte gelöscht, `resetRuns` ausgeführt, die Spieler nach `resetSendsTo` gesetzt und freigegeben, und eine Runde öffnet mit dem Startzähler, wie die Reset-Einstellungen unter [Vorgenerierung](#vorgenerierung) beschreiben |
| `/rdplserver goto <struktur>` | `gotoLevel`, `3` | Bringt dich zur nächsten, bei der noch niemand war, und sucht, ohne das Land auf dem Weg zu erzeugen |
| `/rdplserver goto <struktur> next` | `gotoNextLevel`, `3` | Bringt dich weiter zur nächstgelegenen, zu der du in dieser Sitzung noch nicht gebracht wurdest, ob schon einmal besucht oder nicht |
| `/rdplserver goto <struktur> back` | `gotoBackLevel`, `3` | Bringt dich zur vorherigen zurück und geht Schritt für Schritt durch das, wohin diese Sitzung dich geschickt hat |

**`goto` öffnen.** Jeder Teil von `/rdplserver` braucht einen Operator, Stufe 3, wie die Wurzel des Befehls. Die drei `goto`-Formen sind das eine, worüber ein Pack entscheidet: Jede trägt eine eigene Berechtigungsstufe, die ein Pack oder die Config senken darf, getrennt von den beiden anderen und vom Rest des Befehls. Ein Pack, das `intro`, `team` oder `reset` für Spieler erreichbar machen will, legt sie auf einen Befehlsblock oder in eine Funktion, die mit `goto` auf Stufe 2 läuft und für den Rest auf Stufe 3.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "gotoLevel": 3,
    "gotoNextLevel": 2,
    "gotoBackLevel": 3,
    "gotoPlaceLevels": ["Crypt=2", "Waystone=0", "Mansion=4"]
  }
}
```

| Einstellung | Wofür sie gilt |
| --- | --- |
| `gotoLevel` | `goto <struktur>` |
| `gotoNextLevel` | `goto <struktur> next` |
| `gotoBackLevel` | `goto <struktur> back` |
| `gotoPlaceLevels` | Ein einzelner benannter Ort, in allen drei Formen |

Der Wert ist die Berechtigungsstufe, die der Absender braucht. `3` (Operator) ist der Standard. `2` lässt auch Befehlsblöcke zu, ein Pack kann den Sprung also auf einen Knopf oder eine Druckplatte legen, ohne den Rest von `/rdplserver` freizugeben. `0` öffnet ihn für jeden Spieler. Die drei Einstellungen sind unabhängig: etwa `next` offen für Befehlsblöcke einer Stadt-Rundfahrt, während `back` bei den Operatoren bleibt.

`gotoPlaceLevels` überschreibt die drei Einstellungen für einzelne Orte, als `name=stufe`-Einträge wie im Beispiel oben. Der Name ist das, was du hinter `goto` tippen würdest: ein Vanilla-Name wie `village` oder `mansion`, oder ein mit `locateAs` an einem Imprint-Eintrag angemeldeter Name. Groß-/Kleinschreibung spielt keine Rolle. Stufe `4` liegt über Operator und verschließt den Ort für alle, der Weg, einen einzelnen Ort zu verstecken, während `goto` sonst offen ist.

Ein Eintrag setzt eine Stufe für alle drei Formen dieses Ortes. Ein nicht gelisteter Ort fällt auf die drei Einstellungen oben zurück, und ein nirgends angemeldeter Name passt nie. Die Tab-Vervollständigung hält sich an dieselben Regeln: Nach `goto` werden nur die Orte angeboten, zu denen der Absender auch wirklich gebracht werden kann.

Sie liegen in der Gruppe `commands`, also entscheidet `control.commands` in der Config, ob ein Pack sie überhaupt setzen darf, und `off` dort hält alles bei Operator, ganz gleich was ein Pack verlangt.

**Beim täglichen Arbeiten:** F3+T lädt Texturen, Modelle und Sprachdateien neu, und `/reload` die Daten des Servers. Nimm `/rdpl reload`, wenn du eine Datei *hinzufügst* oder *löschst*, weil sich damit ändert, was der Ordner enthält.

## Gut zu wissen

- KubeJS und CraftTweaker laufen nach RDPL, ihre Änderungen gewinnen also weiterhin.
- Rezepte, Beutetabellen, Fortschritte und Funktionen sind hier die eigenen Datendateien des Spiels, `/reload` nimmt also eine Änderung auf und `/rdpl reload` eine neue Datei.
- Eine Struktur, die schon generiert wurde, bleibt geladen, bis du die Welt verlässt.
- Groß- und Kleinschreibung im Dateinamen zählt. Passt die Schreibweise deiner Datei nicht zu dem, wonach das Spiel gefragt hat, lädt RDPL sie trotzdem, warnt dich aber, denn unter Linux würde sie überhaupt nicht gefunden.
- Leg eine `pack.png` in `rdploader`, um dem Pack ein Symbol zu geben.
- Der Ordner lässt sich mit der Option `rootDirectory` in `config/resourcedatapackloader-common.toml` verschieben oder umbenennen. Ein absoluter Pfad geht auch, und es braucht einen Neustart.
- Ein Modell, das ein fertiges Vanilla-Modell nennt, erbt auch Vanillas Texturen. Eltern-Modelle wie `cube_all` und `cross` nehmen ihre Texturen aus dem Modell, das sie nennt, und sind unproblematisch.
- Telemetrie und Chat-Meldung des Spiels sind aus, solange `privacy` in der Kategorie `tweaks` an ist, was der Standard ist: Nichts wird gesendet, keine Chatnachricht wird signiert, und ein Server mit dem Mod führt für niemanden eine Chat-Sitzung.
- Eine geänderte Pack-Option wird von der Welt gemerkt, die sie ändert, und die Welt wird in den `backups`-Ordner des Spiels gesichert, bevor sie wieder geöffnet wird.

## Wenn etwas nicht funktioniert

**Sieh zuerst in `logs/rdpl.log`.** Alles, was RDPL tut, landet dort statt im Hauptlog. Fortschritte, Beutetabellen, Rezepte, Funktionen, Strukturen und jeder Inhalt werden mit dem Pack protokolliert, aus dem sie kamen, und alles Fehlerhafte mit dem Grund.

**Bei Texturen und anderen Assets ist es anders.** Nach ihnen wird viel zu oft gefragt, um sie einzeln zu protokollieren, stattdessen listet `/rdpl unused` die Dateien in deinen Packs auf, nach denen nichts gefragt hat. Führ ihn aus, wenn das Spiel fertig geladen hat. Nach einer Datei mit dem richtigen Pfad wird immer gefragt, alles Aufgelistete ist also meist ein Tippfehler, bedenke aber, dass manche Dateien nur bei Bedarf laden, etwa andere Sprachen als die, in der du spielst.

**Eine Zip ohne `assets`- oder `data`-Verzeichnis darin wird übersprungen,** ebenso jeder Ordner in `rdploader`, und das Log sagt es. Eine Zip, deren oberste Ebene ein einzelner Ordner um sie herum ist, wird genauso übersprungen.

**`/rdpl which minecraft:textures/block/stone.png`** sagt dir genau, welches Pack eine Datei liefert und was es dabei verdeckt.

**Ein für 1.12.2 geschriebenes Pack wird über die Portierung gelesen.** Siehe [Packs für 1.12.2](#packs-für-1122); das Log nennt jede Datei, die es verschoben, weggelassen oder nicht übernehmen konnte.

## Bonus: Vanilla-Tweaks

Kleine Änderungen daran, wie Vanilla sich verhält, jede über die Config-Kategorie `tweaks` schaltbar.

| Option | Standard | Was sie macht |
| --- | --- | --- |
| `promptLeafDecay` | an | Blätter, die ihren Baum verlieren, verwelken binnen einer Sekunde, statt auf Random-Ticks zu warten |
| `lenientPaths` | an | Pfade und gepflügter Boden lassen sich unter einem Block anlegen und bleiben liegen, wenn einer darübergesetzt wird |
| `unbreakableSpawners` | aus | Mobspawner lassen sich weder abbauen noch sprengen. Der Kreativmodus entfernt sie weiterhin. Braucht einen Neustart |
| `experimentalWarning` | aus | Die Warnung des Spiels vor experimentellen Einstellungen zeigen, wenn eine Welt erstellt oder geöffnet wird. Aus beantwortet sie, als hättest du auf Fortfahren geklickt |
| `privacy` | an | Schaltet Telemetrie und Chat-Meldung des Spiels ab; siehe [Gut zu wissen](#gut-zu-wissen) |
| `darkSplash` | an | Dunkler Ladebildschirm mit dem Logo des Pack-Loaders; die Option Monochromes Logo des Spiels wird für den nächsten Start eingeschaltet |

Vier weitere sitzen in der Kategorie `content` statt in `tweaks`:

| Option | Standard | Was sie macht |
| --- | --- | --- |
| `cactusMaxHeight` | `3` | Wie hoch Vanilla-Kakteen wachsen |
| `caneMaxHeight` | `3` | Wie hoch Vanilla-Zuckerrohr wächst |
| `shovelPaths` | an | Eine Schaufel macht aus Blöcken mit `behavesAs` path einen Pfad, und Schleichen macht das rückgängig |
| `hoeTilling` | an | Eine Hacke pflügt Blöcke mit `behavesAs` till |

**Nichts davon erreicht ein Pack.** Diese Optionen ändern nur Minecrafts eigene Kakteen, Zuckerrohre, Blätter und Pfade. Ein Block, den dein Pack mit `"type": "cane"` definiert, bringt seinen eigenen `growth`-Abschnitt mit und wächst auf die Höhe, die du ihm gegeben hast, egal was sonst installiert ist.

### Unzerstörbare Spawner

`unbreakableSpawners` gibt dem Mobspawner-Block die Werte von Grundgestein: eine Härte, die sich nicht abbauen lässt, und einen Explosionswiderstand, den nichts übersteht. Ein Spieler bekommt keinen abgebaut, egal wie gut die Spitzhacke ist, und weder Creeper noch TNT noch eine Pack-Entity mit `explodes` reißen einen weg. Der Kreativmodus entfernt sie weiterhin, genau wie er Grundgestein weiterhin entfernt, ein Pack-Autor sperrt sich also nie aus dem eigenen Bau aus. Es braucht einen Neustart, weil die Werte des Blocks beim Registrieren gesetzt werden.

**Es geht um den Block, nicht um den einzelnen Spawner.** Es gibt keinen Schalter pro Spawner. Die Option ändert `minecraft:spawner` selbst, sie erreicht also jeden Spawner der Welt auf einmal: die Vanilla-Strukturen, die einen setzen, jeden, den ein Mod setzt, und jeden, den deine eigenen Packs setzen. Ein Spawner in einer deiner `.nbt`-Vorlagen, gesetzt von einem `imprint`-Eintrag, ist ein gewöhnlicher Spawnerblock mit seiner eigenen Block-Entity, er ist also abgedeckt, sobald die Option an ist.

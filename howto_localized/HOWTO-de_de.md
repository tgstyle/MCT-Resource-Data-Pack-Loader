# Resource Data Pack Loader

**Ein Ordner, der alles überschreibt, was Minecraft oder ein Mod mitbringt, neuen Inhalt aus JSON beschreibt und steuert, was generiert wird – in jeder Welt, auf Clients und Servern, ohne dass Spieler irgendetwas einschalten müssen.**

Zwei fertige Beispiele. Leg eines davon direkt in `rdploader` und schau dir an, wie jede Datei geschrieben ist.

- [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) deckt die meisten Möglichkeiten ab: Blöcke, Items, Biome, eine Dimension, eine Weltvorlage und jede Worldgen-Form.
- [RDPLExampleOrePackVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleOrePackVoid.zip) verwandelt die Oberwelt in eine leere Void-Welt, in der die Generierung frei in der Luft hängt, eine Form pro Höhenband, sodass jede einzeln gut zu sehen ist.

---

## Inhalt

**Erste Schritte**
- [Was es ist](#was-es-ist)
- [JSON schreiben](#json-schreiben)
- [Die eine Regel](#die-eine-regel)
- [Packs organisieren](#packs-organisieren)
- [Ressourcenpakete: wer gewinnt](#ressourcenpakete-wer-gewinnt)
- [Packs nur auf dem Server](#packs-nur-auf-dem-server)

**Überschreiben**
- [Was du überschreiben kannst](#was-du-überschreiben-kannst)
- [Registry-Umbenennungen](#registry-umbenennungen)
- [Spielerbeute](#spielerbeute)

**Neuen Inhalt beschreiben**
- [Wie Definitionen funktionieren](#wie-definitionen-funktionieren)
- [Blöcke](#blöcke)
- [Modelle, Blockstates und Texturen](#modelle-blockstates-und-texturen)
- [Damit Vanilla deinen Block richtig behandelt](#damit-vanilla-deinen-block-richtig-behandelt)
- [Items](#items)
- [Flüssigkeiten](#flüssigkeiten)
- [Materialien, Tabs, Sounds, Ore Dictionary](#materialien-tabs-sounds-ore-dictionary)
- [Ofenrezepte und Brennstoffe](#ofenrezepte-und-brennstoffe)
- [Tränke, Trankarten und Brauen](#tränke-trankarten-und-brauen)
- [Dorfbewohner und Handel](#dorfbewohner-und-handel)
- [Entity-Varianten](#entity-varianten)
- [Dorfgrundstücke](#dorfgrundstücke)
- [Biome](#biome)
- [Dimensionen](#dimensionen)
- [Portale und Tore](#portale-und-tore)
- [Weltvorlagen](#weltvorlagen)
- [Welt-Intro](#welt-intro)
- [Spielregeln](#spielregeln)
- [Härtegruppen](#härtegruppen)

**Generieren**
- [Worldgen-Einträge](#worldgen-einträge)
- [Formen](#formen)
- [Verteilung](#verteilung)
- [Retrogen](#retrogen)
- [Vorgenerierung](#vorgenerierung)

**Steuerung**
- [Die Steuerungsebene](#die-steuerungsebene)
- [Was jede Gruppe macht](#was-jede-gruppe-macht)
- [Universal Tweaks](#universal-tweaks)
- [Mo' Villages](#mo-villages)
- [CoFH World](#cofh-world)
- [Lost Cities](#lost-cities)
- [Blast Plaster](#blast-plaster-integration)
- [Grab-Mods](#grab-mods)

**Referenz**
- [Wertelisten](#wertelisten)
- [Ordnerliste](#ordnerliste)
- [Befehle](#befehle)
- [Gut zu wissen](#gut-zu-wissen)
- [Wenn etwas nicht funktioniert](#wenn-etwas-nicht-funktioniert)
- [Bonus: Vanilla-Tweaks](#bonus-vanilla-tweaks)
- [Bonus: JEI-Plugin-Konflikt beheben](#bonus-jei-plugin-konflikt-beheben)
- [Bonus: weniger Startfehler](#bonus-weniger-startfehler)

---

# Erste Schritte

## Was es ist

Der Resource Data Pack Loader (RDPL) legt einen einzigen Ordner in deine Instanz: `rdploader`. Der hat drei Aufgaben.

**Überschreiben.** Datei rein, und sie ersetzt die, die das Spiel oder ein Mod genommen hätte. Kein Schalter, keine Einrichtung pro Welt, nichts, was Spieler im Menü aktivieren müssen. Liegt die Datei im Ordner, ist sie das, was geladen wird.

**Neuer Inhalt.** Eine JSON-Datei, die einen Block, ein Item, eine Flüssigkeit, ein Biom, eine Dimension, einen Trank oder einen Dorfbewohner beschreibt, und das Ding ist registriert. Kein Java, kein Jar.

**Steuerung.** Erz, Biome, Strukturen oder Rezepte von der Generierung ausschließen, Grundgestein glätten, Spawnraten setzen oder die Oberwelt in eine Void-Welt verwandeln.

## JSON schreiben

Alle Dateien hier sind JSON. Hier ist eine, ein echter Worldgen-Eintrag, und sie enthält jede Form, die es in JSON gibt:

```json
{
  "blocks": [
    { "block": "minecraft:wool", "weight": 80, "properties": { "color": "magenta" } },
    { "block": "mypack:ruby_ore", "weight": 20 }
  ],
  "size": { "min": 4, "max": 12 },
  "attempts": 12,
  "maxTemperature": 0.5,
  "sparse": true,
  "replace": ["minecraft:stone", "minecraft:andesite"],
  "dimensions": [0, -1]
}
```

Zeile für Zeile gelesen:

- Die ganze Datei ist ein **Objekt**: Sie beginnt in der ersten Zeile mit `{`, endet in der letzten mit `}` und enthält Paare aus `"schlüssel": wert` mit einem Komma nach jedem Paar außer dem letzten.
- `"attempts": 12` ist eine **Zahl**, ohne Anführungszeichen geschrieben. `"maxTemperature": 0.5` ist dasselbe mit Nachkommastelle.
- `"sparse": true` ist ein **Wahrheitswert**, `true` oder `false`, ebenfalls ohne Anführungszeichen.
- `"block": "minecraft:wool"` ist **Text**, immer in doppelten Anführungszeichen.
- `"dimensions": [0, -1]` ist eine **Liste**: eckige Klammern, Kommas zwischen den Einträgen. In dieser stehen Zahlen.
- `"replace": ["minecraft:stone", "minecraft:andesite"]` ist dieselbe Listenform mit Text, deshalb steht jeder Eintrag in Anführungszeichen.
- `"size": { "min": 4, "max": 12 }` ist ein **Objekt als Wert**: geschweifte Klammern, verschachtelt in den Klammern der Datei selbst.
- `"blocks": [ { ... }, { ... } ]` ist eine **Liste von Objekten**: geschweifte Klammern in eckigen, ein Komma zwischen den beiden Objekten, und jedes Objekt hat seine eigenen Paare. `"properties"` im ersten davon ist ein Objekt in einem Objekt in einer Liste, und das verschachtelt sich so tief, wie eine Tabelle es verlangt.

Dieselben fünf Formen als Tabelle:

| Form | Wie geschrieben | Beispiel |
| --- | --- | --- |
| Text (String) | immer in doppelten Anführungszeichen | `"minecraft:stone"` |
| Zahl | ohne Anführungszeichen | `8`, `-1`, `0.5` |
| true oder false (Boolean) | ohne Anführungszeichen | `true` |
| Liste (Array) | eckige Klammern, Einträge durch Kommas getrennt | `[0, -1]` |
| Objekt | geschweifte Klammern mit Paaren `"schlüssel": wert`, durch Kommas getrennt | `{ "min": 4, "max": 12 }` |

Die Regeln, an denen Dateien scheitern, wenn man sie übersieht:

- Schlüssel stehen immer in doppelten Anführungszeichen. Werte nur dann, wenn sie Text sind: `"8"` ist Text, `8` ist eine Zahl, und ein Schlüssel, der eine Zahl erwartet, nimmt die Textform nicht an.
- Kommas stehen zwischen den Einträgen, nie nach dem letzten. Ein Komma hinter dem letzten Eintrag ist die häufigste kaputte Datei überhaupt.
- Eine Liste enthält Einträge einer Sorte, und die Tabellen sagen, welcher: eine Liste von Ints ist `[0, -1]`, eine Liste von Blocknamen ist `["minecraft:stone", "minecraft:andesite"]`, und auch eine Liste mit nur einem Eintrag braucht ihre Klammern: `[0]`.
- Objekte verschachteln sich in andere Objekte und in Listen, ein Wert kann also so tief liegen wie `"shape": { "type": "cluster" }` oder eine Liste von Objekten sein wie `[{ "block": "minecraft:wool", "weight": 80 }]`.

**Die Tabellen lesen.** In jeder Tabelle steht, ob ein Schlüssel Pflicht ist, was er enthalten darf und was passiert, wenn du ihn weglässt. Einen Wert, den der Parser nicht kennt, schreibt er ins Log und ersetzt ihn durch den Standardwert, statt das Spiel abstürzen zu lassen. Was die Wörter in der Wertespalte bedeuten, jeweils mit genau dem, was du tippst:

| Wenn in der Tabelle steht | Du schreibst |
| --- | --- |
| int | `8` |
| int, Ticks | `100`, zwanzig davon pro Sekunde |
| int oder Bereich | `8`, oder `{ "min": 4, "max": 12 }`, um dazwischen zu würfeln |
| 0 bis 15, 1 bis 100 und Ähnliches | ein Int in diesen Grenzen |
| float | `0.5` |
| boolean | `true` oder `false` |
| string | `"Wörter in Anführungszeichen"` |
| Blockname, Itemname | `"minecraft:stone"`, mit Metadaten als drittem Teil: `"minecraft:stone:3"` |
| `namespace:name` | `"mypack:ruby_ore"` |
| Biomname, Soundname, Tab-Name | dieselbe Form `namespace:name` in Anführungszeichen |
| Hex-Farbe | sechs Hex-Ziffern, `"A0C8FF"`, mit oder ohne führendes `#` |
| Texturpfad | `"mypack:blocks/ruby_ore"` |
| Liste von Ints | `[0, -1]` |
| Liste von Blocknamen | `["minecraft:stone", "minecraft:andesite"]` |
| Liste von Biomnamen | `["minecraft:extreme_hills", "mypack:ruby_hills"]` |
| Liste von Dictionary-Typen | `["MOUNTAIN", "FOREST"]` |
| Liste von Mod-IDs oder Pack-Namespaces | `["quark", "mypack"]` |
| Liste von Objekten | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`, die Schlüssel jedes Objekts stehen in seiner eigenen Tabelle |
| Objekt | `{ "type": "cluster" }`, seine Schlüssel stehen in seiner eigenen Tabelle |
| Objekt aus Rolle zu Biom, aus Variantenname zu Variante | ein Objekt, dessen Schlüssel das Erste und dessen Werte das Zweite sind: `{ "ocean": "mypack:ruby_ocean" }` |

Die meisten Definitionen nehmen außerdem `requires` an, eine Liste von Mod-IDs oder Pack-Namespaces, die vorhanden sein müssen, sonst wird die Datei übersprungen.

## Die eine Regel

Öffne das Jar des Mods, such die Datei, die du ändern willst, und kopiere ihren Pfad ab `assets`.

Die Textur des Eisenerzes liegt im Minecraft-Jar unter:

```
assets/minecraft/textures/blocks/iron_ore.png
```

Deine Version kommt also hierher:

```
rdploader/assets/minecraft/textures/blocks/iron_ore.png
```

Das ist das ganze System. Der Pfad nach `assets` ist immer identisch mit dem Pfad im Jar, es muss also nie etwas umbenannt oder verschoben werden.

## Packs organisieren

Lose Dateien funktionieren problemlos, du kannst sie aber auch bündeln, als Ordner oder als Zip:

```
rdploader/MyTextures/assets/...
rdploader/MyTextures.zip
```

Ordner sind angenehmer, solange du daran arbeitest. Zips sind angenehmer, wenn du sie jemandem gibst. Im Verhalten sind beide identisch.

**Steuern, welches Pack gewinnt.** Enthalten zwei Packs dieselbe Datei, stell dem Namen `RDPL` und eine Zahl voran. Höhere Zahlen laden später und gewinnen:

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

Groß- und Kleinschreibung sind egal, ein Leerzeichen, Bindestrich oder Unterstrich nach der Zahl ist optional, und das Präfix wird im Anzeigenamen des Packs ausgeblendet. Ein Pack ohne Präfix lädt zuerst und verliert damit gegen jedes nummerierte Pack.

Die Priorität bestimmt auch die Reihenfolge, in der Worldgen-Einträge generieren – wichtig, wenn ein Pack Blöcke setzt, die ein anderes Pack ersetzt.

**Ein Pack abschalten, ohne es zu löschen:** Häng `.disabled` an seinen Namen.

## Ressourcenpakete: wer gewinnt

Standardmäßig liegen deine Dateien *über* den Ressourcenpaketen, die ein Spieler im Optionsmenü auswählt, ein Ressourcenpaket kann sie also nicht überschreiben. Für ein Modpack-Logo ist das richtig, für Texturen, die andere umskinnen können sollen, falsch.

Häng `O` oder `N` an das `RDPL`-Präfix, um das pro Pack zu entscheiden:

```
rdploader/RDPLO Branding        gewinnt immer, Ressourcenpakete kommen nicht heran
rdploader/RDPLN BaseTextures    ein Ressourcenpaket darf es überschreiben
rdploader/RDPL1O Seasonal       Priorität und Override zugleich
```

Packs ohne Buchstaben folgen der Option `overrideResourcePacks` in der Config. `/rdpl list` markiert die, die überschreiben.

Der Buchstabe muss das Präfix abschließen, also braucht er danach ein Leerzeichen, einen Bindestrich, einen Unterstrich – oder gar nichts. Genau das verhindert, dass bei einem Pack namens `RDPLOverhaul` das `O` als Buchstabe gelesen wird und der Pack als `Overhaul` auftaucht.

---

## Packs nur auf dem Server

Ein Pack kann allein auf dem Server liegen, während jeder Spieler mit einem reinen Vanilla-Client verbunden ist – solange es auf der richtigen Seite einer einzigen Linie bleibt: **nichts darin darf irgendetwas registrieren**. Der Mod selbst verlangt nie, auf dem Client zu sein, seine beiden IDs akzeptieren jede Gegenstelle; entschieden wird es also vom Pack. Ein Vanilla-Client spielt mit den Block-, Item- und Soundlisten, die er mitgebracht hat; ein Pack, das diese Listen erweitert, muss auf beiden Seiten liegen, und das heißt, ein Modpack auszuliefern – womit dieser Abschnitt hinfällig ist.

Was auf der sicheren Seite bleibt und was nicht:

| Server allein genügt | Pack muss auch auf den Client |
| --- | --- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures` | `blocks`, `items`, `fluids`, `materials` |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `oredict` | `potions`, `potion_types`, `sounds`, `tabs` |
| `loot_tables`, `loot_injections`, `player_loot`, `advancements`, `functions` | `biomes`, `dimensions` |
| `gates`, `trades`, `registry_remap` | `villagers` |
| die ganze Steuerungsebene, Einstellungen und Vorgenerierung | `models`, `blockstates`, `textures`, `lang` (Client-Ordner; ohne Client lässt du sie weg) |

Die Registry-Ordner der rechten Spalte sind harte Grenzen, keine Vorlieben: Ein Vanilla-Client, den man in eine Dimension schickt, von der er nie gehört hat, fliegt auf der Stelle raus, und Blöcke, die er nicht kennt, lassen sich ihm nicht einmal beschreiben. Die linke Spalte funktioniert, weil alles darin entweder vollständig auf dem Server passiert – Generierung, Beute, Funktionen, Entfernungen, die Steuerungsebene – oder den Client über Pakete erreicht, die Vanilla ohnehin spricht. Das Ergebnisfeld der Werkbank füllt in dieser Version der Server, Fortschritte kommen über die gewöhnlichen Fortschrittspakete an, abgelehnte Tore sind schlichte Statusmeldungen, und das Festhalten während der Vorgenerierung besteht aus nichts als Vanilla-Paketen für Spielmodus, Titel und Teleport – ein Vanilla-Client wird also genauso festgehalten, gewarnt und begrüßt wie ein modifizierter.

Was zu tun ist, der Reihe nach:

1. Schalte `vanillaClients` in der Config ein, in der Kategorie `content`. Das macht aus der rechten Spalte eine Regel statt einer Disziplin: Alles daraus wird beim Laden übersprungen, die übersprungenen Dateien jedes Packs stehen namentlich im Log, und nichts registriert sich, sodass aus einer durchgerutschten Blockdatei eine Logzeile wird statt einer abgelehnten Verbindung. Es braucht einen Neustart, wie alles, was entscheidet, was registriert wird.
2. Halte trotzdem jede Definition aus den rechten Ordnern heraus: Der Schalter bewacht die Tür, aber Dateien, die nichts tun, sind totes Gewicht im Pack. Wo das Pack nach einem Item greift – das `hold` eines Tors, ein `killedDrops`, ein Rezeptergebnis, ein Handelsangebot – nenne nur Items, die Vanilla oder die anderen beidseitigen Mods des Servers mitbringen.
3. Entity-Varianten dürfen bleiben, mit einem offenen Auge: Ihre Attribute, Drops und Spawns setzt der Server, das Aussehen einer Variante malt aber der Client, ein Vanilla-Client sieht also die gewöhnliche Kreatur mit dem neuen Verhalten. Wenn es gerade um das Aussehen geht, ist das Pack nicht serverseitig.
4. Leg das Pack auf den Server wie immer, in den Pack-Ordner des Servers. Auf keinem anderen Rechner wird etwas installiert, und `/rdpl` gibt es dort nicht – der Befehl gehört zum Mod, nicht zum Spiel.
5. Beweise es, bevor die Spieler es tun: Verbinde dich einmal mit einem sauberen Vanilla-Client derselben Version. Ein Fehler dabei ist laut, nicht subtil – die Verbindung wird an der Tür abgelehnt oder getrennt, nicht still später kaputt –, ein einziger sauberer Verbindungsversuch ist also ein echter Test.
6. Rechne mit den zwei kosmetischen Lücken und entscheide, dass sie in Ordnung sind: Rezepte vom Server lassen sich normal craften, tauchen aber nicht im Rezeptbuch auf, und Entity-Varianten, die nur Verhalten ändern, tragen das Standardaussehen. Alles andere – die generierte Welt, die Regeln, die Beute, die gesperrten Dimensionen, die Vorgenerierung mit ihrem Festhalten und ihrer Begrüßung – ist dasselbe Erlebnis, das der modifizierte Client bekommt.

# Überschreiben

## Was du überschreiben kannst

- **Alles im assets-Ordner eines Mods**: Texturen, Modelle, Blockstates, Sprachdateien, Sounds, Schriftarten, Splash-Texte, Handbücher, Anleitungen
- **Fortschritte und Beutetabellen**, serverseitig, sie funktionieren also auch auf dedizierten Servern
- **Rezepte**: das Rezept eines Mods ersetzen oder ein eigenes hinzufügen
- **Strukturvorlagen**: die `.nbt`-Dateien, die Mods für generierte Gebäude nutzen, unter `structures/`
- **Funktionen**: die `.mcfunction`-Dateien unter `functions/`
- **Registry-Umbenennungen**: alte Welten am Leben halten, wenn ein Mod einen Block oder ein Item umbenennt
- **Rezept-Entfernungen**: ein Handwerksrezept nach Name, Namespace oder Ergebnis löschen
- **Beute-Injektionen**: einen Pool zu einer Beutetabelle hinzufügen, statt sie komplett zu ersetzen
- **Spielerbeute**: beim Tod eines Spielers eine Beutetabelle auswürfeln, zusätzlich zu dem, was er dabeihatte, oder an dessen Stelle
- **Ore-Dictionary-Namen, Ofenrezepte, Brenndauern, Kreativtabs und Sound-Events**

RDPL eignet sich gut dafür, ein oder zwei Rezepte zu ersetzen, und Rezepte für eigenen Inhalt gehören mit in dasselbe Pack. Für volle Rezeptkontrolle über ein ganzes Modpack sind CraftTweaker und GroovyScript die besseren Werkzeuge, und eine Datei hier ersetzt das Original weiterhin vollständig – um also eine einzelne Zutat zu ändern oder einen einzelnen Beuteeintrag zu streichen, nimm die beiden.

### Pack-Optionen

Ein Pack kann neben seinem `assets` einen `config`-Ordner tragen, mit JSON-Dateien voller true/false-Optionen und ihren Standardwerten:

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

Eine Datei mit `"hide": true` auf oberster Ebene hält die Optionen dieses Packs komplett aus dem Optionsmenü und aus der erzeugten Datei heraus, während die Optionen den Inhalt weiterhin mit ihren Standardwerten steuern. Zwei Dinge wollen das: Inhalt, der noch nicht fertig ist, und Vorlagen-Packs, bei denen die Optionen Maschinerie sind, die die Definitionen zusammenhält, und keine Entscheidung, die irgendwer treffen sollte. Zum Veröffentlichen entfernst du den Schlüssel wieder. Dasselbe geht pro Option: `"hide": true` im Objekt einer Option versteckt nur diese eine, ein fertiges Pack kann also einen Schalter für unfertigen Inhalt oder ein Vorlagen-Gate mitbringen, ohne dass eins davon auftaucht:

    { "enablePackB": { "default": false, "hide": true } }

Da sich eine versteckte Option nicht umlegen lässt, ist eine versteckte Option mit Standardwert true faktisch fest eingeschaltet – für Inhalt, der durch die Options-Maschinerie verdrahtet bleiben muss, aber keine Wahl ist.

Eine Option kann auch ein Objekt mit einer Beschreibung sein, die im Optionsmenü unter ihrem Namen steht:

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

Beim Start werden die Optionsdateien eines Packs zu einer echten Config-Datei, die dem Nutzer gehört, benannt nach dem Pack: `rdploader/config/PackA.json`. Sie wird mit den Standardwerten des Packs angelegt und bei Pack-Updates zusammengeführt, sodass neue Optionen ankommen, ohne anzurühren, was der Nutzer schon eingestellt hat. Änderungen greifen beim nächsten Spielstart. Optionen gehören nur benannten Packs, einem Ordner oder einer Zip, weil die erzeugte Datei nach dem Pack benannt ist; lose Dateien unter `rdploader/assets` haben keinen Pack-Namen und tragen keine Optionen – pack losen Inhalt also in einen benannten Ordner, wenn er einen Schalter braucht.

Die `requires`-Liste jeder Definition kann dann mit einem `config:`-Eintrag eine Option nennen: `"requires": ["config:enableTestingContent"]` registriert diesen Inhalt nur, solange die Option true ist, genau wie ein fehlender Mod ihn überspringen ließe. Ein bloßer Name prüft die Datei jedes Packs, und jedes Pack, das ihn definiert, muss zustimmen; `"config:PackA:enableTestingContent"` nennt ein bestimmtes Pack. Eine Option, die kein Pack definiert, gilt als false und wird einmal angemahnt.

Ein `file:`-Eintrag hängt daran, dass eine Datei oder ein Ordner im Spielordner existiert, um Inhalt an etwas außerhalb von RDPLs eigenen Packs zu koppeln, etwa an das Ressourcenpaket eines anderen Mods: `"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` registriert den Inhalt nur, solange genau diese Datei installiert ist. Der Pfad ist relativ zum Spielordner, immer mit Schrägstrichen, und darf kein `..` enthalten.

### Definitionen vererben

Eine Block- oder Item-Definition kann mit `"inherits"` von einer anderen derselben Art ausgehen, indem sie den Registry-Namen irgendeiner Variante nennt, und dann überschreiben, was abweicht:

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "meta": 0, "hardness": 4.0 } } }

Das Kind kopiert jeden Wert aus der Datei des Elternteils und aus der genannten Variante, die Dateireihenfolge spielt nie eine Rolle, Ketten werden vom Elternteil abwärts aufgelöst, und ein Kreis oder ein fehlender Elternteil landet im Log und lässt das Kind so, wie es geschrieben steht. Felder, die das Kind schreibt, ersetzen den geerbten Wert; verschachtelte Varianteneigenschaften überschreiben einzeln, Listen wie `requires` dagegen komplett – schreib also die ganze Liste, die du haben willst. Blöcke erben nur von Blöcken und Items nur von Items.

### Block- und Item-Vorlagen

Ein Elternteil kann eine reine Vorlage sein, die nie ins Spiel kommt, denn die Vererbung liest die Definitionsdateien selbst und nicht das, was registriert wurde. Häng die Vorlage an eine versteckte Option, die fest aus ist, und sie registriert nichts, während ihre Werte vererbbar bleiben:

`config/options.json`

```json
{
  "templates": { "default": false, "hide": true, "description": "Never on, parents only" }
}
```

`assets/jacksmod/blocks/ore_template.json`

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
    "ore_template": { "meta": 0, "hardness": 3.0, "resistance": 5.0 }
  }
}
```

`assets/jacksmod/blocks/jacks_ore.json`

```json
{
  "inherits": "jacksmod:ore_template",
  "requires": [],
  "variants": {
    "jacks_ore": { "meta": 0, "hardness": 4.0 }
  }
}
```

Die Vorlage registriert sich nie, während `jacks_ore` mit Material, Sound, Werkzeug, Tab, Erfahrungsdrops und Widerstand der Vorlage registriert wird und nur die Härte überschreibt. Das Kind muss sein eigenes `requires` schreiben, hier auf eine leere Liste gesetzt, weil es sonst das des Elternteils erbt und mit ihm verschwinden würde.

### Strukturen an genauen Stellen

Vanilla-Strukturen nagelst du mit `structureAt` in den `terrain`-Einstellungen an genaue Punkte, als `structure=x,z`-Einträge, einer pro Zeile: `"structureAt": ["villages=1000,-500"]`. **x und z sind Blockkoordinaten, keine Chunkkoordinaten**, und die Struktur generiert in dem Chunk, in dem dieser Block liegt. Ein Eintrag pro gewünschtem Exemplar. Ihr Abstand, ihre Trennung, ihr Mindestspawnabstand und die Prüfungen auf flachen Boden treten alle beiseite – die Stelle ist damit Sache des Packs, und zwei Pins näher als einen Chunk beieinander setzen zwei Strukturen in denselben Chunk. Einmal gesetzt, setzt sich die Struktur in ihrem Chunk nach den üblichen Regeln auf den Boden.

Ein `imprint`-Eintrag nagelt genauso fest, mit `"at": [x, z]` in seiner Form, und setzt sie genau einmal an diesen Koordinaten an der Oberfläche, sobald dieser Chunk generiert, statt nach Zufall. Das lässt sich mit `locateAs` kombinieren, eine festgenagelte Struktur ist also auch per /locate auffindbar.

### Platzierte Strukturen finden

Ein `imprint`-Eintrag mit `"locateAs": "Crypt"` registriert jede Struktur, die er platziert, unter diesem Namen, und `/locate Crypt` zeigt dann auf die nächste davon, mit dem Namen in der Tab-Vervollständigung. Finden lassen sich nur Strukturen, die schon generiert wurden, denn Pack-Strukturen werden beim Erzeugen der Chunks nach Zufall platziert und nicht auf einem Raster, das das Spiel vorhersagen könnte. Die Namen liegen im Spielstand der Welt, überstehen also Neustarts und funktionieren auf Servern.

## Registry-Umbenennungen

Wenn ein Mod einen seiner Blöcke oder Items umbenennt, verlieren Welten, die vor der Umbenennung gespeichert wurden, sie. Leg eine Datei in `registry_remap/`, die den alten Namen auf den neuen abbildet:

```json
{
  "registry": "minecraft:items",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

Die Registry ist die, zu der der Eintrag gehört, meist `minecraft:items` oder `minecraft:blocks`. Umbenennungen verketten sich: Bildest du A auf B ab und später B auf C, geht A direkt auf C.

## Spielerbeute

Spieler haben in dieser Version keine eigene Beutetabelle. Bei ihrem Tod fällt nichts außer dem Inventar, und es gibt keinen Namen, auf den ein Pack zeigen könnte, um das zu ändern. Eine Datei in `player_loot/` gibt ihnen eine:

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

`add` lässt den Tod, wie er ist, und legt die Items der Tabelle neben alles, was der Spieler dabeihatte – die richtige Wahl, wenn die Tabelle ein Kopfgeld auf den Kill sein soll und keine Strafe fürs Sterben. `replace` wirft das Inventar weg, und es fällt nur, was die Tabelle auswürfelt: So entscheidet ein Pack, was ein Tod kostet und was er übrig lässt, bis hinunter auf einen einzelnen Knochen.

`keepInventory` heißt normalerweise, dass gar nichts fällt, und ein Eintrag stellt sich dem nicht in den Weg: Steht `rollOnKeepInventory` auf aus, wird er bei solchen Toden überhaupt nicht ausgewürfelt. Wer im Zuschauermodus stirbt, behält sein Inventar ebenfalls, ganz gleich was die Spielregel sagt, und zählt hier als dieselbe Art Tod. Ihn anzuschalten ist der Weg, das Sterben auch auf einer Welt teuer zu halten, auf der Inventare erhalten bleiben – ein Wegzoll bei jedem Mal statt gleich die ganze Tasche.

Mehrere Dateien stapeln sich, und jede wird für sich entschieden: Ein Pack kann also einen Eintrag mitbringen, der immer würfelt, und einen zweiten, der nur zubeißt, wenn das Inventar wirklich verloren geht. Ist auch nur ein zutreffender Eintrag `replace`, wird das Inventar einmal geleert, bevor irgendetwas gewürfelt wird – ein `add`-Eintrag daneben landet also trotzdem.

Die Tabelle ist eine ganz gewöhnliche Beutetabelle, die wie jede andere über ihren Namen gesucht wird. Sie darf also in deinem Pack unter `loot_tables/entities/player.json` liegen, sie darf eine Tabelle von Vanilla oder einem Mod sein, die du nie geschrieben hast, und `loot_injections` erreichen sie wie jede andere Tabelle. Bedingungen bekommen den sterbenden Spieler als erbeutete Entity, den Töter als Spieler, wenn der Tod ein Kill war, und die Schadensquelle: `killed_by_player`, `entity_properties`, `random_chance_with_looting` und `looting_enchant` lesen also genau das, was man erwartet, und das Glück des Töters erreicht `quality`.

**Grab-Mods.** Die gewürfelten Items werden als ganz normale Todesdrops abgelegt, bevor irgendein Grab-Mod sie zu sehen bekommt. Ein Grab-Mod, der die Drops eines Spielers einsammelt, sammelt sie also mit ein: Sie liegen mit allem anderen im Grab statt lose daneben, und bei `replace` bekommt das Grab den Inhalt der Tabelle statt des Inventars. Das gilt für Gravestone, GraveStone Mod, Corail Tombstone und alles andere, was mit den Drops arbeitet, die der Tod erzeugt hat. Dafür muss nichts installiert und nichts eingestellt werden, und es gibt nichts anzuschalten.

`dropLoose` ist für die Fälle, in denen genau das die falsche Antwort ist. Die Items kommen gar nicht erst zu den Todesdrops dazu, sie werden für sich in die Welt gesetzt, und nichts, was diese Liste liest, bekommt sie je zu Gesicht: Das Inventar wandert ins Grab wie eh und je, und die Items der Tabelle liegen daneben auf dem Boden, für den, der den Kill gemacht hat. Das ist die Einstellung für Beute – ein Kopf, ein Herz, was der Körper eben zurücklassen soll –, die dem Töter gehört und nicht im Grab des Opfers eingeschlossen darauf wartet, dass der zurückläuft. Ohne Grab-Mod ändert sie fast nichts, die Items landen so oder so an derselben Stelle; sie entscheidet erst, wem sie gehören, wenn einer installiert ist. Sie bedeutet allerdings auch, dass die Items in der Welt sind, bevor irgendwas weiter hinten die Drops noch hätte aufhalten können – ein Eintrag, der einen abgebrochenen Tod nicht überleben darf, bleibt also besser aus.

Setz `playerLoot` in der Config-Kategorie `data` auf `false`, um den Ordner ganz abzuschalten.

---

# Neuen Inhalt beschreiben

## Wie Definitionen funktionieren

Neben den Ordnern, die Dateien überschreiben, gibt es Ordner, die neue Dinge beschreiben. Der Pfad ist die Identität: Eine Datei unter `assets/mypack/blocks/ruby_ore.json` registriert einen Block namens `mypack:ruby_ore`.

Registriert wird mit der niedrigsten Priorität, die Forge anbietet: Registriert ein echter Mod denselben Namen, gewinnt der Mod, und deine Datei wird ignoriert. Nichts hier kann einen Mod ersetzen.

**Wo die Grenze liegt.** Alles, was eine Tile Entity, eine GUI, ein Inventar oder eigene Logik pro Tick braucht, braucht einen echten Mod. Alles darunter ist Freiwild.

### Dein Namespace ist dein Mod

Der Namespace, den du wählst, ist in jeder praktischen Hinsicht eine Mod-ID. Nichts davon wird als Mod geladen, und in der Modliste taucht es nie auf, aber alles, was eine Mod-ID liest, liest deine:

- Registry-Namen sind `mypack:ruby_ore`, genau wie die eines Mods, und sie werden in jede gespeicherte Welt geschrieben, die sie enthält.
- Die Whitelists für Erz, Biome, Generatoren und Rezepte in der Config gleichen damit ab, `oreWhitelist = mypack` behält also dein Erz und blockt das aller anderen.
- `/rdpl which`, `/rdplserver oregen` und die Berichte gruppieren danach.
- JEI, das Ore Dictionary und die Abfragen anderer Mods sehen ihn genauso.

Wähl also am Anfang einen Namen und ändere ihn nie wieder. Ein umbenannter Namespace macht alles zu Waisen, was schon in einer Welt liegt, genau wie ein Mod, der seine ID ändert – dafür ist `registry_remap` da.

Das gilt in beide Richtungen: `requires` nimmt einen Pack-Namespace genauso bereitwillig wie eine installierte Mod-ID, ein Pack kann also von einem anderen abhängen und übersprungen werden, wenn das nicht installiert ist.

**Ein fehlender Mod hält das Spiel an, so wie es die eigene Abhängigkeit eines Mods tut.** Jede Mod-ID, die irgendwo in deinen Packs von einem `requires` genannt wird, meldet dieser Mod bei Forge als eigene Abhängigkeit an, bevor überhaupt etwas lädt. Fehlt eine, bekommst du den üblichen Missing-Mods-Bildschirm mit dem, was gebraucht wird, auf dem Client wie auf dem dedizierten Server, und in der Zwischenzeit generiert und registriert sich nichts.

Ein fehlendes *Pack* ist etwas anderes. Pack-Namespaces sind keine Mods, sie erreichen diese Prüfung also nie: Die Definition wird übersprungen, eine Zeile geht nach `logs/rdpl.log` und nennt, was gefehlt hat, und das Spiel läuft weiter. Wenn ein Block, den du erwartet hast, nicht im Kreativtab liegt, ist diese Logzeile die erste Stelle zum Nachsehen.

`requires` nimmt nur bloße IDs. Es gibt keine Syntax für Versionsbereiche, es kann also sagen, dass ein Mod da sein muss, aber nicht, welche Version.

Die beiden eigenen IDs des Mods, `resourcedatapackloader` und `resourcedatapackloader_mixin`, sind reserviert. Inhalt darunter zu definieren wird ignoriert und protokolliert, weil es Besitz an Dingen anmelden würde, die dieser Mod selbst registriert. Die Assets dieses Mods zu überschreiben ist weiterhin in Ordnung, nur Inhalt dort zu registrieren nicht.

Jede Tabelle unten folgt den Konventionen aus [JSON schreiben](#json-schreiben): ob ein Schlüssel Pflicht ist, was er enthalten darf und was passiert, wenn du ihn weglässt.

Die meisten Definitionen nehmen außerdem `requires` an, eine Liste von Mod-IDs oder Pack-Namespaces, die vorhanden sein müssen, sonst wird die Datei übersprungen.

## Blöcke

`blocks/*.json`

```json
{
  "type": "ore",
  "material": "rock",
  "soundType": "stone",
  "harvestTool": "pickaxe",
  "harvestToolLevel": 2,
  "creativeTab": "mypack:tab",
  "expDrop": { "min": 3, "max": 7 },
  "requires": ["mypack"],
  "variants": {
    "ruby_ore": {
      "meta": 0,
      "hardness": 3.0,
      "resistance": 5.0,
      "harvestLevel": 2,
      "drops": [
        { "block": "mypack:ruby", "amount": { "min": 1, "max": 2 }, "bonusChance": [1, 2] }
      ]
    },
    "deep_ruby_ore": {
      "meta": 1,
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
| `door` | Zwei Blöcke hoch, öffnet sich per Hand und hört auf Redstone. Nutzt eine einzige Variante, weil die übrigen Metadaten Scharnier, Ausrichtung und Offenstand tragen |
| `trapdoor` | Eine Klappe oben oder unten an einem Block, per Hand oder per Redstone zu öffnen. Eine Variante, die Metadaten tragen Ausrichtung, Hälfte und Offenstand |
| `fence_gate` | Ein Tor in einer Zaunreihe, per Hand oder per Redstone zu öffnen, und abgesenkt, wo es auf eine Mauer trifft. Eine Variante |
| `banner` | Ein Banner auf einem Pfosten oder an einer Wand, sechzehn stehende Drehungen, mit deinem eigenen Muster. Registriert für das hängende einen zweiten Block namens `<name>_wall` |
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

### Dateischlüssel

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `variants` | ja | Objekt aus Variantenname zu Variante |, | Ein Eintrag pro Metadatenwert. Der Schlüssel wird zum Registry-Namen |
| `type` | nein | einer der Typen oben | `basic` | Welche Form der Block annimmt |
| `material` | nein | eines der [Blockmaterialien](#wertelisten) | `rock` | Abbauverhalten, Kolben, Feuer und Flüssigkeiten |
| `soundType` | nein | einer der [Sound-Typen](#wertelisten) | vom Material | Schritte, Abbauen und Setzen |
| `mapColor` | nein | eine der [Kartenfarben](#wertelisten) | vom Material | Wie er auf einer Karte aussieht |
| `harvestTool` | nein | `pickaxe`, `axe`, `shovel` | `pickaxe` | Welches Werkzeug ihn abbaut |
| `harvestToolLevel` | nein | 0 bis 3 | `0` | 0 Holz, 1 Stein, 2 Eisen, 3 Diamant |
| `silkHarvest` | nein | boolean | `true` | Ob Behutsamkeit den Block selbst zurückgibt |
| `expDrop` | nein | Objekt mit `min` und `max` | keines | Erfahrung beim Abbauen ohne Behutsamkeit |
| `creativeTab` | nein | Tab-Name | keiner | Der Tab, in dem er auftaucht |
| `renderLayer` | nein | `solid`, `cutout`, `cutout_mipped`, `translucent` | passend zum Typ | Wie er gezeichnet wird |
| `opaque` | nein | boolean | `true` | Ob er Sicht und Licht vollständig blockiert |
| `fullCube` | nein | boolean | wie `opaque` | Ob er seinen ganzen Raum ausfüllt |
| `lightOpacity` | nein | 0 bis 255 | `255`, wenn opak, sonst `0` | Wie viel Licht er schluckt |
| `slipperiness` | nein | float | `0.6` | Eis ist `0.98` |
| `flammability` | nein | int | `0` | Wie bereitwillig Feuer ihn verzehrt |
| `fireSpread` | nein | int | `0` | Wie bereitwillig Feuer von ihm überspringt |
| `explosionResistanceDivisor` | nein | float | `1.0` | Teilt den `resistance`-Wert jeder Variante gegenüber Explosionen |
| `modelBlock` | nein | Blockname | `minecraft:stone` | Block, dessen Modell geliehen wird, wenn deiner keines hat |
| `modelMeta` | nein | int | `0` | Welche Variante dieses Modells |
| `itemModel` | nein | `state`, `item` | `state` | `state` folgt dem Blockstate, `item` sucht eine eigene Datei |
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

### Variantenschlüssel

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `meta` | ja | 0 bis 15 |, | Der Metadatenwert, den diese Variante beansprucht |
| `hardness` | nein | float | `1.0` | Wie lange das Abbauen dauert. Obsidian ist `50`, `-1` ist unzerstörbar |
| `resistance` | nein | float | `5.0` | Explosionswiderstand |
| `light` | nein | 0 bis 15 | `0` | Abgegebenes Licht |
| `harvestLevel` | nein | 0 bis 3 | der Wert der Datei | Überschreibt die Werkzeugstufe für diese Variante |
| `rarity` | nein | `common`, `uncommon`, `rare`, `epic` | `common` | Farbe des Namens im Tooltip |
| `maxSize` | nein | 1 bis 64 | `64` | Stapelgröße |
| `drops` | nein | Liste von Drops | droppt sich selbst | Was das Abbauen bringt |

**Metadaten sind endgültig.** Die Zahl, die eine Variante beansprucht, wird in jede gespeicherte Welt geschrieben, die sie enthält. Varianten später umzunummerieren oder umzusortieren macht aus gesetzten Blöcken etwas anderes. Häng neue Varianten hinten an und benutze eine Zahl nie ein zweites Mal.

Ein `basic`-Block fasst sechzehn Varianten, ein `slab` acht, `log` und `leaves` vier, weil Achse und Verwelk-Flag eigene Bits brauchen, und die Typen mit nur einem Zustand fassen eine.

### Drops

```json
{
  "drops": [
    { "block": "mypack:ruby", "amount": { "min": 1, "max": 3 }, "bonusChance": [1, 2, 3] },
    { "block": "minecraft:coal", "amount": 1, "chance": 25 },
    { "block": "minecraft:diamond", "weight": 1 },
    { "block": "minecraft:emerald", "weight": 4 },
    { "entity": "minecraft:silverfish", "amount": { "min": 1, "max": 2 }, "chance": 15 }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `block` | eines von beiden | Block- oder Itemname |, | Was gedroppt wird |
| `entity` | eines von beiden | Entity-Name |, | Eine Entity, die beim Brechen des Blocks frei wird, statt eines Items |
| `meta` | nein | int | `0` | Welche Variante davon |
| `amount` | nein | int oder Bereich | `1` | Wie viele |
| `chance` | nein | 0 bis 100 | `100`, bzw. `0` wenn `guaranteed` aus ist | Wie oft der Drop überhaupt kommt |
| `weight` | nein | int | `0` | Über null tritt der Eintrag einem Topf bei, aus dem genau ein Drop kommt. Siehe unten |
| `bonusChance` | nein | Liste von Ints | keine | Zusätzliche Drops pro Glücksstufe, ein Eintrag pro Stufe |
| `guaranteed` | nein | boolean | `true` | Altes Kürzel für `chance`. An heißt `100`, aus heißt `0` |

Jeder Eintrag ohne `weight` wird für sich entschieden, ein Block mit dreien kann also alle drei droppen oder keinen. Gibst du Einträgen ein `weight`, hören sie auf, unabhängig zu sein: Sie bilden einen Topf, aus dem bei jedem Brechen genau einer gezogen wird, mit den Gewichten als Verhältnis. Oben teilen sich Diamant und Smaragd einen Topf im Verhältnis eins zu vier, es kommt also immer einer von beiden heraus und in vier von fünf Fällen der Smaragd, während Rubin und Kohle getrennt entschieden werden und der Silberfisch wieder für sich steht. Items und Entities haben getrennte Töpfe, ein gewichtetes Item und eine gewichtete Entity treten also nicht gegeneinander an.

Ein Eintrag mit `entity` lässt dort, wo der Block stand, eine frei, in eine zufällige Richtung gedreht, und ein Mob bekommt seine übliche Spawn-Behandlung für die Schwierigkeit vor Ort, kommt also mit der Ausrüstung und den Effekten an, die er sonst auch hätte. `amount` bestimmt wie viele, `chance` wie oft, `weight` steckt ihn in den Entity-Topf. Es passiert, während der Block bricht, ganz gleich wodurch, eine Explosion oder ein Kolben setzt sie also genauso frei wie eine Spitzhacke. `meta`, `bonusChance` und Glück bedeuten einer Entity nichts und werden ignoriert.

Ein Drop, der sowohl `block` als auch `entity` nennt, nimmt die Entity und schreibt es ins Log.

### Wachstum

Für `crop`, `flower`, `cane` und `vine`.

```json
{
  "growth": {
    "stages": 8,
    "growth": 10,
    "maxHeight": 3,
    "needsWater": true,
    "waterRange": 2,
    "drop": "mypack:reed",
    "dropCount": 1
  }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `stages` | nein | int | `16` | Wachstumsstufen, bis es fertig ist |
| `growth` | nein | int |, | Chance von eins zu N pro Random-Tick, eine Stufe weiterzukommen |
| `spread` | nein | int | `0` | Wie weit es sich auf Nachbarblöcke ausbreitet |
| `maxHeight` | nein | int | `3` | Nur `cane`. Wie hoch die Säule wächst |
| `drop` | nein | Itemname | keiner | Was es beim Abbauen droppt |
| `dropCount` | nein | int | `1` | Wie viele |
| `needsSky` | nein | boolean | `false` | Wächst nur, wo der Himmel zu sehen ist |
| `needsWater` | nein | boolean | `false` | Wächst nur in Wassernähe |
| `waterRange` | nein | int | `1` | Wie weit dieses Wasser entfernt sein darf |
| `damage` | nein | boolean | `false` | Verletzt, was es berührt |
| `damageAmount` | nein | float, halbe Herzen | `1.0` | Wie sehr es verletzt |
| `breaksNeighbors` | nein | boolean | `false` | Zerstört Blöcke, die daneben gesetzt werden, wie ein Kaktus |

### Setzlinge

Entweder ein Baum aus Blöcken:

```json
{
  "sapling": {
    "soil": ["minecraft:grass", "minecraft:dirt"],
    "stages": 3,
    "chance": 5,
    "light": 9,
    "log": "mypack:ruby_log",
    "leaves": "mypack:ruby_leaves",
    "height": 5,
    "vines": false
  }
}
```

… oder eine deiner Strukturvorlagen, und das ist der Weg, etwas zu bauen, was ein Generator nicht hinbekommt:

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
| `log` | nein | Blockname | `minecraft:log` | Stammblock |
| `leaves` | nein | Blockname | `minecraft:leaves` | Blätterblock |
| `height` | nein | int | `4` | Stammhöhe |
| `vines` | nein | boolean | `false` | Ranken von den Blättern hängen lassen |
| `structure` | nein | `namespace:name` | keine | Wächst zu dieser Vorlage statt zu einem generierten Baum |

## Modelle, Blockstates und Texturen

Mit seiner Definition ist ein Block oder ein Item registriert. Wie er *aussieht*, ist weiterhin ein ganz gewöhnlicher Satz Asset-Dateien, in denselben Ordnern und demselben Format, das Minecraft ohnehin benutzt, nur unter deinem eigenen Namespace.

```
assets/mypack/blockstates/ruby_ore.json
assets/mypack/models/block/ruby_ore.json
assets/mypack/models/item/ruby/ruby.json
assets/mypack/textures/blocks/ruby_ore.png
assets/mypack/lang/en_us.lang
```

### Die Varianten benennen

Jeder Block mit mehr als einer Variante bekommt eine Eigenschaft namens `blocks`, und ihre Werte sind die Variantennamen aus der Definition. Eine Blockdatei, die `ruby_ore` und `deep_ruby_ore` registriert, braucht also einen Blockstate mit genau diesen beiden Varianten:

```json
{
  "variants": {
    "blocks=ruby_ore": { "model": "mypack:ruby_ore" },
    "blocks=deep_ruby_ore": { "model": "mypack:deep_ruby_ore" }
  }
}
```

Ein Block mit einer einzigen Variante und ohne weitere Eigenschaften nutzt stattdessen `normal`.

Hat der Block eigene Eigenschaften, werden sie mit Kommas verbunden, in der Reihenfolge, in der der Zustand sie auflistet: `blocks=ruby_log,axis=y`, `blocks=ruby_slab,half=bottom`, `blocks=ruby_stairs,facing=east,half=bottom,shape=straight`. Zwei bleiben mit Absicht weg: die eigene Varianteneigenschaft einer Mauer sowie `check_decay` und `decayable` eines Blätterblocks – Blätter brauchen also nur `blocks=ruby_leaves`. Ein Banner hat gar keine Varianteneigenschaft und wird stehend über `rotation=0` bis `15` und an der Wand über `facing=north` angesprochen, siehe [Banner](#banner).

### Item-Modelle

Standardmäßig nutzt das Item das, was der Blockstate für diese Variante hergibt, mehr ist also nicht nötig. Mit `"itemModel": "item"` am Block sucht es stattdessen seine eigene Datei, unter `models/item/<block>/<variant>.json`.

Items laufen immer über diesen zweiten Weg, weil jedes Pack-Item Untertypen hat:

```
assets/mypack/models/item/ruby/ruby.json
assets/mypack/models/item/ruby/polished_ruby.json
```

Der Pfad ist der Registry-Name des Items, dann der Variantenname.

Flüssigkeiten brauchen überhaupt kein Modell, es wird aus den Texturen `still` und `flow` erzeugt.

### Türen, Falltüren und Zauntore

Alle drei geben ihre gesamten Metadaten für die Form aus, die sie annehmen, jede ist also eine einzige Variante, und bei jeder gibt es ein paar Dinge, die man vor dem Schreiben der Dateien wissen sollte.

**Sie tragen keine `blocks`-Eigenschaft**, ihre Blockstates werden also allein über die Form angesprochen: `facing=east,half=lower,hinge=left,open=false` bei einer Tür, `facing=north,half=bottom,open=false` bei einer Falltür, `facing=south,in_wall=false,open=false` bei einem Tor. Das sind 32 Schlüssel, 16 und 16.

**`powered` bleibt bei Türen und Toren weg.** Beide haben es tatsächlich, und beide würden ihre Blockstates sonst für eine Achse verdoppeln, die nichts Sichtbares ändert. Es wird dir abgenommen, genau wie das Spiel es seinen eigenen Türen und Toren abnimmt, schreib die Schlüssel also ohne. Falltüren hatten es nie.

**Zeig mit den Modellen auf die Eltern, die Texturen annehmen**, nicht auf die fertigen von Vanilla:

| Typ | Eltern |
| --- | --- |
| `door` | `block/door_bottom`, `block/door_bottom_rh`, `block/door_top`, `block/door_top_rh` |
| `trapdoor` | `block/trapdoor_bottom`, `block/trapdoor_top`, `block/trapdoor_open` |
| `fence_gate` | `block/fence_gate_closed`, `block/fence_gate_open`, `block/wall_gate_closed`, `block/wall_gate_open` |

Eine Tür nimmt zwei Texturen, `bottom` und `top`; die anderen beiden nehmen eine, `texture`. Beide oberen Türmodelle greifen für ihre Oberkante nach `bottom`, gib also in allen vier Dateien beide an, auch wenn die oberen nur eine zu brauchen scheinen. Tor-Varianten wollen `"uvlock": true`, wie die spieleigenen auch.

**Ihre Texturen nutzen jedes Pixel, und das ist die Falle, in die man tappt.** Die breiten Flächen einer Tür sind mit `[0, 0, 16, 16]` belegt, dem ganzen Bild, und ihre schmalen Kanten samt Ober- und Unterseite kommen aus demselben Quadrat: Spalten 0 bis 3 für die Seiten, 13 bis 16 für oben und unten. Bei einer Falltür ist es dasselbe, die flachen Seiten das ganze Bild und die vier Ränder aus den Zeilen 13 bis 16.

Lass also keinen leeren Rand. Räumst du am Rand ein paar Spalten frei, weil du die Form für schmaler hältst als die Datei, schneidest du einen Schlitz mitten durch die Fläche und verlierst Ober- und Unterseite ganz. Zeichne stattdessen den Rahmen oder die Holme in diese Randpixel, dann lesen sie sich als Zierleiste auf den Kanten des Blocks.

**Ihre Items unterscheiden sich je nach Typ.** Das einer Tür ist ein flaches Sprite, `item/generated` über ihrem eigenen `textures/items/<name>.png`, denn eine Tür in der Hand wird als Bild gezeichnet und nicht als Form. Die von Falltür und Tor erben stattdessen ein Blockmodell, die untere Hälfte und das geschlossene Tor, so wie das Spiel es bei seinen eigenen macht.

Alle drei nehmen das `material`, das du ihnen gibst. Ein Tor setzt auf einem Block auf, der sich selbst auf Holz festlegt, deshalb setzt dieser Mod das Material beim Registrieren wieder auf deins, und ein steinernes Tor wird mit der Spitzhacke abgebaut, wie es der Stein verlangt, für den es sich ausgibt.

### Banner

Beim Banner gehen die Form des Blocks und die Form des Modells als Einziges getrennte Wege, deshalb sei es hier vollständig aufgeschrieben.

**Es registriert zwei Blöcke.** Eine Definition liefert dir das stehende Banner unter deinem eigenen Namen und einen zweiten Block namens `<name>_wall` für das hängende. Beide brauchen einen Blockstate; ein Item bekommt nur das stehende, und dieses Item entscheidet, welchen der beiden es setzt: das stehende, wenn du oben auf einen Block klickst, das hängende, wenn du auf eine Seite klickst. Den Wandblock setzt du nie selbst, und ein eigenes Item braucht er nicht.

**Das stehende braucht einen Forge-Blockstate.** Seine Eigenschaft heißt `rotation` und läuft von `0` bis `15`, denn ein Banner dreht sich in Sechzehnteln statt in Vierteln. Ein Vanilla-Blockstate kann das nicht ausdrücken: Sein `y` geht durch `ModelRotation`, das nur 0, 90, 180 und 270 annimmt und bei allem anderen abbricht. Forges Format nimmt jeden Winkel, die sechzehn Einträge werden also als Transform geschrieben:

```json
{
  "forge_marker": 1,
  "defaults": { "model": "meinpack:mein_banner" },
  "variants": {
    "rotation": {
      "0": { "transform": { "rotation": { "y": 0 } } },
      "1": { "transform": { "rotation": { "y": -22.5 } } }
    }
  }
}
```

…und so weiter bis `15`, jeder Eintrag `-22,5` Grad weiter herum. Das Vorzeichen entspricht den spieleigenen Bannern, die sich um minus die Drehung drehen. Bau das Modell nach Süden zeigend, denn dort landet ein Banner, das ein nach Süden blickender Spieler setzt. Der Wandblock ist ein ganz gewöhnlicher Vanilla-Blockstate mit den üblichen vier `facing`-Einträgen bei 0, 90, 180 und 270, an ihm ist nichts Krummes.

**Das Modell ist fast zwei Blöcke hoch.** Ein Banner belegt zum Setzen und für die Kollision einen Block, gezeichnet wird es aber weit darüber hinaus, und ein Modell, das oben an seinem eigenen Block endet, sieht gestutzt aus. Vanillas Maße, in Sechzehnteln eines Blocks, lohnen sich genau zu übernehmen:

| Teil | Von | Bis |
| --- | --- | --- |
| Pfosten | `0` | `28` |
| Querbalken | `28` | `29,33` |
| Tuch | `2,67` | `29,33` |
| Tuchbreite | `1,33` | `14,67` |
| Wandtuch | `-13` | `13,67` |

Ein stehendes Banner reicht also bis `29,33`, fast zwei Blöcke, und ein Wandbanner hängt dreizehn Sechzehntel *unter* dem Block, der es hält. Modellelemente dürfen von `-16` bis `32` laufen, beides passt also. Die Wandform hat weder Pfosten noch Querbalken, nur Tuch.

**Das Tuch ist doppelt so hoch wie breit, und deine Textur muss das auch sein.** Diese Fläche misst `13,33` auf `26,67`. Legst du eine quadratische Textur darauf, wird das Muster auf die halbe Höhe gequetscht. Blocktexturen selbst dürfen nicht doppelt so hoch wie breit sein, denn alles Nichtquadratische wird als Animation gelesen; der Ausweg ist ein größeres quadratisches Blatt, auf dem das Tuch nur einen Teil einnimmt: eine 32×32-Datei mit dem Tuch als 16×32-Bereich, angesprochen als `"uv": [0, 0, 8, 16]`, mit den Streifen für Pfosten und Querbalken im Platz daneben. UV-Koordinaten laufen immer von 0 bis 16, ganz gleich welche Auflösung die Datei hat, dieselben Zahlen gelten also in jeder Größe.

**Sein Item will ein eigenes Modell.** Ein Item, das ein so hohes Modell erbt, sprengt bei der üblichen Blockskalierung sein Feld, gib `models/item/<name>.json` also einen eigenen `display`-Block mit heruntergesetzter Skalierung und schieb das Ganze zurück in den Rahmen.

**Farben und Muster gibt es daran nicht.** Ein Pack-Banner hat keine Tile Entity, es trägt also nichts die Liste der Lagen, die Vanilla-Banner in ihrer führen. Das Muster ist die Textur, so wie das Aussehen einer Tür ihre Textur ist, und eine Definition ist ein Banner. Es zu färben und Muster darauf zu stapeln liegt außerhalb dessen, was ein Pack erreichen kann.

**Es nimmt das `material`, das du ihm gibst.** Der Block, auf dem es aufsetzt, legt sich selbst auf Holz fest, deshalb setzt dieser Mod das Material beim Registrieren wieder auf deins, und ein steinernes Banner wird mit der Spitzhacke abgebaut, wie es der Stein verlangt, für den es sich ausgibt.

### Texturen als Pixelkarte

Eine Textur darf eine JSON-Datei statt einer PNG sein. Leg sie dorthin, wo die PNG gelandet wäre, und häng `.json` an den ganzen Namen: `textures/blocks/panel.png.json` beantwortet dann jede Anfrage nach `textures/blocks/panel.png`. Sonst ändert sich nichts – Modelle zeigen weiter auf `meinpack:blocks/panel`, Atlas, Mipmaps und eine Animations-`.mcmeta` funktionieren wie gehabt, denn was das Spiel bekommt, ist nach wie vor eine PNG.

```json
{
  "size": "16x16",
  "palette": { "s": "#EDE9E2", "d": "#C6C1B5", "e": "#9E988C", "p": "#F6F4EF" },
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
| `rows` | ja, oder geerbt | Liste von Text | | Eine Zeichenkette je Pixelzeile, ein Zeichen je Pixel, von oben nach unten |
| `palette` | ja, oder geerbt | Objekt | | Ein Zeichen zu einer Farbe, `#RRGGBB` oder `#AARRGGBB` |
| `extends` | nein | eine andere Pixelkarte | | Die Karte, von der diese ausgeht |
| `notes` | nein | Objekt | | Ein Zeichen zu einer Zeile, die sagt, wofür es da ist; wird vererbt und nie gezeichnet |

**Es gibt keinen Namen anzugeben.** Der Pfad der Datei ist ihr Name, genau wie bei einer PNG: Eine Karte unter `assets/meinpack/textures/blocks/panel.png.json` heißt im Modell `meinpack:blocks/panel`, eine unter `assets/meinpack/textures/items/gem.png.json` heißt im Item-Modell `meinpack:items/gem`. Nichts zeigt eigens auf eine Pixelkarte; ein Block oder ein Item nennt seine Textur wie eh und je und erfährt nie, welche von beiden es bekommen hat. Damit bleiben Block- und Item-Ordner auch getrennt wie bei PNGs: `textures/blocks/gem.png.json` und `textures/items/gem.png.json` sind zwei verschiedene Texturen und werden als zwei verschiedene Dateien zwischengespeichert.

**Jede Größe, die du willst**, bis 4096 je Seite, und die beiden Seiten müssen nicht gleich sein. `16x16` ist eine gewöhnliche Blockfläche, `16x32` der hohe Streifen, wie ihn eine Türhälfte oder eine Animation braucht. Die Größe wird geprüft und nicht erraten: Gib eine Zeile je Pixelzeile und ein Zeichen je Pixel quer, sonst wird die Karte abgelehnt und das Log nennt die Zeile und was es vorgefunden hat. Ein Zeichen ohne Farbe in der Palette bleibt frei, `.` oder ein Leerzeichen ist also ein Loch.

**Vorlagen sind der eigentliche Sinn.** `extends` nennt eine andere Pixelkarte, als `namespace:pfad` oder als bloßer Pfad im selben Pack, und die erbende Datei übernimmt deren `size`, `rows` und `palette`. Was sie selbst nennt, gewinnt, und sie muss nicht alles nennen – eine ganze Variante kann also aus einer Handvoll Farben bestehen:

```json
{
  "extends": "meinpack:textures/blocks/panel.png",
  "palette": { "s": "#AA7EB1", "d": "#8B6292", "e": "#6B4A72", "p": "#C5A1CB" }
}
```

Das ist eine vollständige zweite Textur: dieselbe Form in Purpur, und wird die Form in der Vorlage je neu gezeichnet, ziehen alle Varianten mit. Eine Variante darf stattdessen eigene `rows` mitbringen und die Palette der Vorlage behalten – andersherum also: dieselben Farben in einem anderen Muster. Die Vererbung geht bis zu acht Stufen tief, eine Schleife wird erkannt und gemeldet, und eine Karte, die eine Vorlage nennt, die es nicht gibt, wird gemeldet statt leer gezeichnet.

**Eine Vorlage darf auch ein echtes Bild sein.** Zeigt `extends` auf eine PNG, die irgendein Pack oder das Spiel selbst liefert, ändert die Palette ihre Bedeutung: Die Schlüssel sind dann die Farben, die im Bild schon stecken, die Werte die Farben, die an ihre Stelle treten. Nichts wird abgepaust und keine `rows` werden geschrieben, ein Pack kann eine Vanilla- oder Mod-Textur also an Ort und Stelle umfärben:

```json
{
  "extends": "minecraft:textures/blocks/coal_ore.png",
  "palette": {
    "#3F3F3F": "#C4353F",
    "#343434": "#8E2029",
    "#373737": "#A32A33",
    "#454545": "#DE5F68"
  }
}
```

Das ist ein Rubinerz in Vanillas eigenem Stein: Die vier Sprenkeltöne werden getauscht, jedes andere Pixel bleibt, wie es war. Eine Farbe, die das Bild nicht enthält, passt schlicht nie, und die Größe kommt aus dem Bild, sofern du keine nennst – dann muss sie übereinstimmen.

`extends` bevorzugt eine Pixelkarte: Es sucht zuerst die Karte unter diesem Pfad und weicht erst auf das Bild aus, wenn kein Pack eine liefert. Ein Name, der weder das eine noch das andere ist, wird gemeldet statt leer gezeichnet. Auf einem Bild aufzubauen ist Client-Arbeit, denn dabei werden die Ressourcen des Spiels gelesen, ein dedizierter Server tut es also nie.

**Zu wissen, was die Zeichen einer Vorlage bedeuten**, ist beim Erben das Unangenehme, und genau dafür ist der `notes`-Block oben da: ein Zeichen zu einer kurzen Zeile, vererbt wie die Palette und nie gezeichnet. Beschrifte die Zeichen einer Vorlage, und wer sie erbt, weiß, welche er überschreiben muss.

`/rdpl pixelmap <namespace:pfad>` berichtet dann, was eine Karte tatsächlich geworden ist, und das ist der verlässliche Weg, eine Variante zu schreiben, ohne jede Datei die Kette hinauf zu öffnen:

```
oretest:textures/blocks/ruby_ore.png is 16x16
  built from oretest:textures/blocks/ruby_ore.png.json
  built from oretest:textures/blocks/gem_ore.png.json
  rows come from oretest:textures/blocks/gem_ore.png.json
  1  #C4353F  17 pixel(s)  set by ruby_ore.png.json  ore body, most of every lump
  2  #8E2029   8 pixel(s)  set by ruby_ore.png.json  ore shadow, the darkest tone
  a  #747474  86 pixel(s)  set by gem_ore.png.json   stone, the commonest tone
```

Jedes Zeichen steht da mit seiner Farbe, wie viele Pixel es abdeckt, welche Datei der Kette es gesetzt hat und wofür diese Datei es hält. Der Pfad darf kurz angegeben werden, `meinpack:blocks/panel`, oder vollständig. Ein Zeichen mit 0 Pixeln ist eines, das die Palette nennt und die Zeilen nie benutzen – meist ein Tippfehler in einer Zeile.

**Gezeichnete Bilder bleiben auf der Platte**, in `rdploader/pixelmap-cache`, in je einem Ordner pro Namespace und benannt nach der Textur mit einem Hash ihrer Quelle am Ende. Der Hash umfasst die ganze Kette, die Karte selbst und jede Vorlage darüber, das Bearbeiten einer Vorlage ändert also den Stempel jeder erbenden Variante, und alle werden neu gezeichnet. Wird eine Karte neu gezeichnet, werden ihre älteren Dateien weggeräumt.

Der Ordner wird außerdem bei jedem Durchsuchen der Packs durchgegangen, und jedes Bild, dessen Karte kein Pack mehr liefert, wird gelöscht, samt jedem leer gebliebenen Ordner. Benenn eine Textur um, lass ein Pack weg, lösch eine Karte – ihr Bild geht mit, statt für immer liegen zu bleiben. Den ganzen Ordner zu löschen kostet nichts außer der Zeit, sie erneut zu zeichnen, und beim Suchen nach Packs wird er übersprungen, also nie für ein Pack gehalten.

Eine PNG gewinnt immer. Gibt es sowohl `panel.png` als auch `panel.png.json`, wird die PNG ausgeliefert und die Karte nie gezeichnet; eine erzeugte Textur lässt sich später also durch eine gemalte ersetzen, ohne irgendetwas anzufassen, das darauf zeigt.

### Fallen, die man kennen sollte

**Ein Blockstate, der ein nacktes Vanilla-Modell nennt, erbt auch Vanillas Texturen.** `normal_torch`, `ladder`, `wooden_door_*` und `wheat_stage*` bringen alle ihre eigenen Texturen mit, ein Block, der auf eines davon zeigt, bekommt also Vanillas Aussehen, ganz gleich, was im Blockstate steht. Eltern-Modelle wie `cube_all`, `cross` und `block/crop` nehmen ihre Texturen aus dem Blockstate und verhalten sich wie erwartet, ebenso die Eltern für Tür, Falltür und Tor, die unter [Türen, Falltüren und Zauntore](#türen-falltüren-und-zauntore) stehen.

**`forge_marker: 1` unterstützt kein Multipart.** Ein Blockstate für Ranken muss reines Vanilla-Multipart sein, mit den Texturen im Modell selbst statt von außen übergeben.

**Namen kommen aus der Sprachdatei.** Ein Block oder Item zeigt seinen rohen Schlüssel, bis `lang/en_us.lang` ihm einen gibt, in der üblichen Form `tile.mypack:ruby_ore.name=Ruby Ore`.

**Typen mit nur einer Variante nennen sich doppelt.** Ein Block, der mehrere Varianten fassen kann, wird allein über seinen Registrierungsnamen angesprochen, wie oben. Ein Block, dessen gesamte Metadaten für seine Form draufgehen, hängt den Variantennamen dahinter: Eine Tür aus `blocks/my_door.json` mit einer Variante namens `my_door` heißt also `tile.mypack:my_door.my_door.name=My Door`. Das betrifft `door`, `trapdoor`, `fence_gate`, `banner`, `stairs`, `ladder`, `torch`, `crop`, `cane`, `sapling` und `vine`. Hat so ein Typ ein eigenes Item, wie Tür und Banner, will er denselben Schlüssel noch einmal unter `item.` statt unter `tile.`.

## Damit Vanilla deinen Block richtig behandelt

Vanilla erkennt seine eigenen Blöcke an einem Dutzend Stellen an ihrer Identität, deshalb funktioniert ein Pack-Block, der offensichtlich funktionieren sollte, oft nicht. Zwei Schlüssel decken das ab.

```json
{
  "material": "ground",
  "plantTypes": ["Plains", "Crop"],
  "behavesAs": ["till", "path"],
  "variants": { "ruby_grass": { "meta": 0, "hardness": 0.6 } }
}
```

**`plantTypes`** listet die Forge-Pflanzentypen auf, die dein Block unterstützt, damit Setzlinge, Feldfrüchte und Blumen darauf gepflanzt werden können.

**`behavesAs`** lässt Vanilla deinen Block wie einen der eigenen behandeln:

| Wert | Was er macht |
| --- | --- |
| `till` | Eine Hacke macht Ackerboden daraus |
| `path` | Eine Schaufel macht einen Trampelpfad daraus |

## Items

`items/*.json`

```json
{
  "type": "food",
  "creativeTab": "mypack:tab",
  "eat": true,
  "useDuration": 32,
  "alwaysEdible": false,
  "variants": {
    "ruby_apple": { "meta": 0, "healAmount": 6, "saturation": 0.8, "rarity": "rare" },
    "dried_ruby_apple": { "meta": 1, "healAmount": 3, "saturation": 0.4 }
  }
}
```

| Typ | Was du bekommst |
| --- | --- |
| `basic` | Ein einfaches Item. Wird genommen, wenn `type` fehlt |
| `food` | Wird gegessen, mit Hunger und Sättigung |
| `drink` | Wird getrunken statt gegessen und lässt ein leeres Behältnis zurück |
| `tool` | Spitzhacke, Axt, Schaufel oder Schwert aus einem Material |
| `armor` | Helm, Brustpanzer, Beinschutz oder Stiefel aus einem Material |
| `seed` | Pflanzt eine deiner Feldfrüchte |
| `potion` | Wendet beim Benutzen deine Trankeffekte an |
| `potion_bottle` | Fasst deine Trankarten und zeigt sie in einem Kreativtab |

Ein `potion_bottle` listet mit `potionTypes` auf, was es fassen kann, als Array von Namen der Trankarten, etwa `["mypack:ruby_tonic"]`. Eines mit leerer Liste registriert nichts, und das Log sagt es.

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `variants` | ja | Objekt aus Variantenname zu Variante |, | Ein Eintrag pro Metadatenwert. Der Schlüssel wird zum Registry-Namen |
| `type` | nein | einer der Typen oben | `basic` | Welchen Typ das Item annimmt |
| `creativeTab` | nein | Tab-Name | keiner | Der Tab, in dem es auftaucht |
| `material` | tool, armor | Materialname | keiner | Aus welchem deiner Materialien es gemacht ist |
| `toolClass` | tool | `pickaxe`, `axe`, `shovel`, `sword` | keiner | Welches Werkzeug es ist |
| `slot` | armor | `head`, `chest`, `legs`, `feet` | keiner | Wo es getragen wird. `helmet`, `chestplate`, `leggings` und `boots` gehen auch |
| `eat` | food | boolean | `false` | Nutzt die Ess-Animation |
| `alwaysEdible` | food | boolean | `false` | Lässt sich auch bei voller Hungerleiste essen |
| `useDuration` | nein | int, Ticks | `32` | Wie lange das Benutzen dauert |
| `container` | drink | Itemname | keiner | Was übrig bleibt, etwa eine Flasche |
| `crop` | seed | Blockname | keiner | Die Feldfrucht, die es pflanzt |
| `soil` | seed | Blockname | `minecraft:farmland` | Worauf es gepflanzt werden kann |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

Variantenschlüssel:

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `meta` | ja | 0 bis 15 |, | Der Metadatenwert, den diese Variante beansprucht |
| `maxSize` | nein | 1 bis 64 | `64` | Stapelgröße |
| `rarity` | nein | `common`, `uncommon`, `rare`, `epic` | `common` | Farbe des Namens im Tooltip |
| `healAmount` | food | int, halbe Hähnchenkeulen | `0` | Wiederhergestellter Hunger |
| `saturation` | food | float | `0.0` | Wiederhergestellte Sättigung |

## Flüssigkeiten

`fluids/*.json`

```json
{
  "name": "molten_ruby",
  "still": "mypack:blocks/molten_ruby_still",
  "flow": "mypack:blocks/molten_ruby_flow",
  "color": "C0304A",
  "luminosity": 12,
  "density": 2000,
  "temperature": 1500,
  "viscosity": 4000,
  "bucket": true,
  "creativeTab": "mypack:tab",
  "block": { "material": "lava", "quantaPerBlock": 8 }
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
| `block` | nein | Objekt |, | Der Flüssigkeitsblock. `material` (`water`), `flammability` (`0`), `fireSpread` (`0`), `quantaPerBlock` (`0`) |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

## Materialien, Tabs, Sounds, Ore Dictionary

`materials/*.json`

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
| `harvestLevel` | nein | 0 bis 3 | `1` | Werkzeugstufe. 0 Holz, 1 Stein, 2 Eisen, 3 Diamant |
| `durability` | nein | int | `250` | Benutzungen, bis es kaputtgeht |
| `efficiency` | nein | float | `6.0` | Abbaugeschwindigkeit. Diamant ist 8 |
| `damage` | nein | float | `2.0` | Bonus auf den Angriffsschaden |
| `enchantability` | nein | int | `14` | Wie gut die Verzauberungen ausfallen. Gold ist 22 |
| `repairItem` | nein | Itemname | keiner | Was es im Amboss repariert |
| `reduction` | nein | Liste aus vier Ints |, | Rüstungspunkte, in der Reihenfolge Füße, Beine, Brust, Kopf |
| `toughness` | nein | float | `0.0` | Rüstungshärte, wie Diamant sie hat |
| `equipSound` | nein | Soundname | `item.armor.equip_iron` | Sound beim Anlegen der Rüstung |
| `armorTexture` | nein | Texturpräfix | der Dateiname | Die Textur der getragenen Rüstung |

`tabs/*.json`

```json
{ "label": "Ruby Pack", "icon": "mypack:ruby" }
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `label` | nein | string | der Dateiname | Der Name des Tabs |
| `icon` | nein | Itemname | keiner | Das Item, das auf dem Tab abgebildet ist |

`sounds/*.json` ist das Vanilla-Format von `sounds.json`, ein Pack kann also eigenes Audio mitbringen. `oredict/*.json` fügt Ore-Dictionary-Namen zu Items hinzu, die es schon gibt.

## Ofenrezepte und Brennstoffe

`furnace/*.json` fügt Schmelzrezepte hinzu und entfernt sie.

```json
{
  "remove": [
    "minecraft:iron_ingot",
    { "input": "minecraft:gold_ore" }
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

`fuels/*.json`

```json
{
  "fuels": [
    { "item": "mypack:ruby_coal", "burnTime": 2400 },
    { "oreDict": "gemRuby", "burnTime": 800 }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `item` | eines von beiden | Itemname | keiner | Das Item, das brennt |
| `oreDict` | eines von beiden | Ore-Dictionary-Name | keiner | Alles unter diesem Namen brennt |
| `burnTime` | ja | int, Ticks | `0` | Kohle ist 1600, ein Brett 300 |

## Tränke, Trankarten und Brauen

`potions/*.json`

```json
{
  "name": "effect.mypack.ruby_sight",
  "color": "C0304A",
  "beneficial": true,
  "instant": false,
  "effectiveness": 0.5,
  "icon": { "x": 0, "y": 0 },
  "iconTexture": "mypack:textures/gui/effects.png",
  "attributes": [
    { "attribute": "generic.movementSpeed", "uuid": "91AEAA56-376B-4498-935B-2F7F68070635", "amount": 0.2, "operation": 2 }
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
| `attributes` | nein | Liste von Objekten | keine | `attribute`, `uuid`, `amount` (`0.0`), `operation` (`0`) |

`potion_types/*.json`

```json
{
  "baseName": "ruby_sight",
  "effects": [
    { "potion": "mypack:ruby_sight", "duration": 3600, "amplifier": 0, "showParticles": true }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `baseName` | nein | string | Namespace und Name | Der Name, aus dem die Flasche gebaut wird |
| `effects` | ja | Liste von Objekten |, | Siehe unten |

Jeder Effekt nimmt `potion` (Pflicht), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) und `showParticles` (`true`).

`brewing/*.json`

```json
{
  "brewing": [
    { "input": "minecraft:potion", "ingredient": "mypack:ruby", "output": "mypack:ruby_potion" }
  ]
}
```

Jeder Eintrag besteht entweder aus `input`, `ingredient` und `output` oder aus `from`, `ingredient` und `to`.

## Dorfbewohner und Handel

`villagers/*.json`

```json
{
  "careers": ["gem_cutter", "appraiser"],
  "texture": "mypack:textures/entity/villager/jeweller.png",
  "zombieTexture": "mypack:textures/entity/zombie_villager/jeweller.png"
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `careers` | ja | Liste von Namen | keine | Die Laufbahnen, die dieser Beruf anbietet. Ein Beruf ohne welche wird abgelehnt |
| `texture` | nein | Texturpfad | der Vanilla-Dorfbewohner | Wie der Dorfbewohner aussieht |
| `zombieTexture` | nein | Texturpfad | der Vanilla-Zombiedorfbewohner | Wie er als Zombie aussieht |

`trades/*.json`

```json
{
  "trades": [
    {
      "profession": "mypack:jeweller",
      "career": "gem_cutter",
      "level": 1,
      "maxUses": 12,
      "buy": { "item": "minecraft:emerald", "min": 2, "max": 4 },
      "sell": { "item": "mypack:ruby", "min": 1 }
    }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `profession` | ja | Berufsname |, | Wessen Handel das ist |
| `career` | ja | Laufbahnname |, | Welche Laufbahn darin |
| `level` | nein | int | `1` | Auf welcher Handelsstufe er auftaucht |
| `maxUses` | nein | int | `12` | Wie oft er genutzt werden kann, bevor er sperrt |

Ein Stapel ist `item` mit `min` (`1`) und `max` (`min`), ein fester Preis ist also einfach nur `min`.

## Entity-Varianten

Eine Datei in `assets/<modid>/entities/` macht aus einer vorhandenen Entity eine neue. Sie ist eine echte Entity für sich: eigener Registry-Name, eigener Name in der Welt, eigenes Spawn-Ei und eine eigene Beutetabelle, wenn du ihr eine gibst – aufgebaut auf dem Verhalten einer anderen Entity, statt sie zu ersetzen. An der Entity, die sie kopiert, ändert sich nichts.

```json
{
  "entity": "minecraft:cow",
  "name": "Angry Cow",
  "hostile": true,
  "targets": ["minecraft:player"],
  "attributes": {
    "maxHealth": 20,
    "movementSpeed": 0.32,
    "attackDamage": 4
  },
  "spawns": [
    { "creatureType": "creature", "weight": 4, "min": 1, "max": 2 }
  ],
  "biomeTypes": ["PLAINS"]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `entity` | ja | `namespace:name` | keine | Die Entity, auf der aufgebaut wird. Die jedes Mods, solange sie einen einfachen Welt-Konstruktor hat |
| `name` | nein | string | keiner | Der Name, den sie in der Welt, in Todesmeldungen und auf ihrem Ei trägt |
| `showName` | nein | boolean | `false` | Zeigt den Namen, ohne dass man sie ansieht |
| `texture` | nein | `namespace:textures/entity/<file>.png` | keine | Ein eigener Skin, genauso aufgeteilt wie der der kopierten Entity |
| `lootTable` | nein | `namespace:entities/<name>` | die der Basis | Was sie droppt. Ohne das droppt sie, was die kopierte Entity droppt |
| `profession` | nein | `namespace:name` | zufällig | Bei einem Dorfbewohner der Beruf, den er ausübt |
| `career` | nein | int | zufällig | Welche Laufbahn innerhalb dieses Berufs, ab 1 aufwärts |
| `baby` | nein | boolean | `false` | Bleibt jung, und zwar für immer |
| `sounds` | nein | Objekt | die der Basis | `ambient`, `hurt` und `death`, jeweils ein registriertes Sound-Event |
| `soundVolume` | nein | Zahl | `1.0` | Wie laut diese Sounds sind |
| `soundPitch` | nein | Zahl | `1.0` | Wie hoch sie klingen. Unter 1 tiefer, über 1 quietschiger |
| `immuneTo` | nein | Liste von Schadensarten | keine | Schaden, der an ihr abprallt: `fall`, `drown`, `explosion`, `magic`, `cactus`, `lava`, `wither`, `starve`, `anvil`, `inWall` und der Rest |
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
| `noAI` | nein | boolean | `false` | Steht da, wo sie hingesetzt wurde, und tut nichts |
| `leftHanded` | nein | boolean | `false` | Hält ihre Waffe in der anderen Hand |
| `fireproof` | nein | boolean | `false` | Fängt überhaupt nie Feuer, nimmt also keinen Schaden durch Feuer oder Lava und brennt nicht im Tageslicht |
| `invulnerable` | nein | boolean | `false` | Nimmt von nichts Schaden außer von der Leere und vom Kreativmodus |
| `glowing` | nein | boolean | `false` | Durch Wände umrandet |
| `invisible` | nein | boolean | `false` | Wird nicht gezeichnet, ihre Ausrüstung aber schon |
| `dropChance` | nein | 0 bis 1 | `0` | Wie wahrscheinlich jedes Ausrüstungsstück droppt |
| `scale` | nein | float | `1.0` | Wie groß sie gezeichnet wird und wie groß ihre Hitbox ist |
| `angryScale` | nein | float | `scale` | Die Größe, auf die sie anschwillt, solange sie ein Ziel hat |
| `leashable` | nein | boolean | `false` | Lässt sich an der Leine führen, auch wenn die kopierte Entity das nie konnte |
| `steerable` | nein | boolean | `false` | Lässt sich beim Reiten lenken |
| `width` | nein | float | die der Basis | Breite ihrer Hitbox, bevor `scale` angewendet wird |
| `height` | nein | float | die der Basis | Höhe ihrer Hitbox, bevor `scale` angewendet wird |
| `pathPriorities` | nein | Objekt | keines | Wodurch sie läuft, als `WATER`, `LAVA`, `DANGER_FIRE`, `DOOR_WOOD_CLOSED` und so weiter, jeweils eine Zahl, wobei negativ „nie“ heißt |
| `egg` | nein | boolean oder Objekt | `true` | Ein Spawn-Ei, gefärbt wie das der kopierten Entity. `{ "primary": "AABBCC", "secondary": "112233" }` wählt eigene Farben, `false` lässt das Ei weg |
| `attributes` | nein | Objekt | keines | `maxHealth`, `movementSpeed`, `attackDamage`, `knockbackResistance`, `followRange`, `armor`. Ein Attribut, das die Entity normalerweise nicht hat, bekommt sie dazu |
| `hostile` | nein | boolean | `false` | Greift an, was sie erreicht, und wehrt sich, wenn sie verletzt wird |
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
| `explodes` | nein | boolean | `false` | Sprengt sich neben ihrem Ziel in die Luft, wie ein Creeper. Braucht `hostile` |
| `explosionPower` | nein | Zahl | `3.0` | Wie groß die Explosion ist. Ein Creeper ist 3, TNT ist 4 |
| `explosionFuse` | nein | int, Ticks | `30` | Wie lange sie zischt, bevor es losgeht |
| `explosionFire` | nein | boolean | `false` | Lässt Feuer zurück |
| `equipment` | nein | Objekt | keines | `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`, jeweils ein Itemname |
| `spawns` | nein | Liste von Objekten | keine | `creatureType`, `weight`, `min` und `max`, dieselbe Form, die ein Biom nutzt |
| `biomes` | nein | Liste von Biomnamen | jedes Biom | Wo diese Spawns hinzugefügt werden |
| `biomeTypes` | nein | Liste von Dictionary-Typen | keine | Dasselbe, aber nach Typ |
| `trackingRange` | nein | int | `80` | Aus welcher Entfernung der Client von ihr erfährt |
| `trackVelocity` | nein | boolean | `true` | Schickt neben der Position auch die Geschwindigkeit. Aus spart Traffic bei Dingen, die sich kaum bewegen |
| `trackingFrequency` | nein | int | `3` | Wie oft, in Ticks |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Variante bleibt weg, wenn nicht alle da sind |

`scale` ändert Modell und Hitbox auf beiden Seiten, du triffst also das, was du siehst. Eine Kreatur, die ihre Größe selbst ändert – ein Tier, das heranwächst, oder ein Zombie, der ein Kind ist –, wird um die Größe herum skaliert, die sie sich gewählt hat, damit sich beides nicht in die Quere kommt. `angryScale` lässt sie anschwellen, solange sie ein Ziel hat, und bringt sie auf `scale` zurück, sobald sie es verliert. Da dem Client nie mitgeteilt wird, was eine Kreatur jagt, trägt das Sprint-Flag diese Nachricht hinüber; es wird bei einer Variante gesetzt, die `angryScale` nutzt, und sonst bei keiner – ein Mod, der bei deinen Varianten das Sprinten ausliest, sieht es also wechseln. In eine niedrige Decke hineinzuwachsen ist möglich, genauso wie bei einem wachsenden Schleim, halte den Unterschied also im Rahmen.

Eine Variante droppt das, was die kopierte Entity droppt, weil die Beutetabelle im Code dieser Entity festgeschrieben ist und nicht über den Namen nachgeschlagen wird. `lootTable` zeigt auf eine eigene Tabelle, die du dann wie jede andere unter `loot_tables/entities/<name>.json` mitlieferst.

Eine `texture` wird anstelle der Textur eingebunden, die die Entity sonst nutzen würde, egal welchen Renderer sie erbt, sie funktioniert also bei Mod-Entities genauso wie bei Vanilla-Entities. Sie muss zu dem Modell passen, auf das sie gezeichnet wird, denn das Modell ist das der Basis-Entity: ein Skin, keine neue Form. Layer behalten ihre eigenen Texturen, Rüstung sieht auf einem umgeskinnten Zombie also weiter wie Rüstung aus.

Rüstung wird überhaupt nur auf einer Entity gezeichnet, deren Renderer einen Rüstungs-Layer hat, und das heißt in dieser Version: die humanoiden Mobs und die Dorfbewohner. Eine Variante einer Kuh oder einer Spinne kann Rüstung tragen und bekommt auch deren Schutz, nur zeichnet sie niemand – `armor` unter `attributes` ist deshalb meist der sauberere Weg, so eine Kreatur zäh zu machen. `hideArmor` ist für den anderen Fall: ein Humanoider, der die Rüstung in seinen Slots behalten soll, für den Schutz oder für einen Mod, der sie ausliest, ohne dass man sie sieht.

`hostile` nimmt der Kreatur auch das Verhalten weg, das sie hat weglaufen lassen: Ein Tier, das Spielern ausgewichen ist oder bei Verletzung in Panik geriet, tut beides nicht mehr, sobald es feindselig ist – sonst würde es vor dem fliehen, was es eigentlich angreifen soll. Es braucht eine Entity, die auf dem Boden läuft, weil es dasselbe Angriffsverhalten nutzt, das Vanilla seinen eigenen Mobs gibt. Eine fliegende oder schwimmende Basis wird protokolliert und in Ruhe gelassen. `passive` greift weiter, erreicht aber nur Verhalten, das so gebaut ist, wie Vanilla es baut: Einem Mod, dessen Feindseligkeit in seinem eigenen Tick- oder Schadenscode steht, kann ein Pack sie nicht ausreden.

Eine Variante ist eine eigene Klasse, eine Welt, die eine enthält, hängt also von dem Pack ab, das sie gemacht hat, genau wie von einem Mod. Nimm die Datei weg, und die Kreaturen in dieser Welt gehen mit.

## Dorfgrundstücke

Eine Datei in `assets/<modid>/villages/` fügt ein Stück hinzu, das Dörfer bauen können, neben den Vanilla-Stücken. Zwei Sorten, ausgewählt mit `type`.

Ein `farm` ist Vanillas Feld, beschrieben statt programmiert: ein Grundstück in der Größe, die du willst, mit einem Block eingefasst, gefüllt mit Reihen aus Erde, getrennt durch Wasserrinnen, bepflanzt mit einer Feldfrucht, die pro Block aus deiner Liste gezogen wird.

```json
{
  "type": "farm",
  "weight": 3,
  "width": 7,
  "depth": 9,
  "crops": ["simplecorn:corn"],
  "edge": "minecraft:log",
  "water": true,
  "rowWidth": 2
}
```

Ein `template` setzt stattdessen eine deiner `.nbt`-Strukturen, gedreht zum Dorfweg hin.

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

| Schlüssel | Genutzt von | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `type` | allen | `farm` oder `template` | `farm` | Welche Sorte Grundstück |
| `weight` | allen | int | `3` | Wie oft dieses Grundstück gegenüber den anderen des Packs gezogen wird |
| `leastCount` | allen | int | `1` | Wenigstens so viele pro Dorf, bevor die Dorfgröße dazukommt |
| `mostCount` | allen | int | `4` | Höchstens so viele pro Dorf, bevor die Dorfgröße dazukommt |
| `width` | allen | int | `7` | Größe quer zum Weg |
| `height` | allen | int | `4` | Höhe, die über dem Boden freigeräumt wird |
| `depth` | allen | int | `9` | Größe vom Weg weg |
| `crops` | farm | Liste von Blocknamen | Weizen | Eine pro Block gepflanzt, in zufälliger Wachstumsstufe |
| `edge` | farm | Blockname | `minecraft:log` | Der Rahmen um das Grundstück |
| `soil` | farm | Blockname | `minecraft:farmland` | Woraus die Reihen bestehen |
| `water` | farm | boolean | `true` | Eine Wasserrinne zwischen die Reihen legen |
| `rowWidth` | farm | int | `2` | Wie breit jede Erdreihe ist |
| `structure` | template | `namespace:name` | keine | Die Vorlage, die gesetzt wird |
| `integrity` | template | 1 bis 100 | `100` | Prozentsatz der Blöcke der Vorlage, die erscheinen |
| `villagers` | allen | int | `0` | Wie viele Leute das Grundstück spawnt |
| `villagerEntity` | allen | `namespace:name` | ein Dorfbewohner | Wer dort wohnt, etwa eine eigene Entity-Variante |
| `villagerX` | allen | int | `1` | Wo sie erscheinen, quer über das Grundstück |
| `villagerY` | allen | int | `1` | Wo sie erscheinen, über dem Boden |
| `villagerZ` | allen | int | `1` | Wo sie erscheinen, in das Grundstück hinein |
| `ground` | allen | Blockname | `minecraft:dirt` | Was am Hang darunter aufgefüllt wird |
| `requires` | allen | Liste von Mod-IDs oder Pack-Namespaces | keine | Das Grundstück bleibt weg, wenn nicht alle da sind |

Jedes Pack-Grundstück wird den Dörfern als ein Eintrag angeboten, `weight` entscheidet also, welches deiner Grundstücke gezogen wird, sobald ein Dorf nach einem fragt. Welches Grundstück eine Platzierung genutzt hat, steht in den eigenen Daten des Dorfes, es baut sich beim Laden also korrekt wieder auf.

## Biome

`biomes/*.json`

```json
{
  "name": "Ruby Forest",
  "type": ["FOREST", "DENSE"],
  "temperature": 0.7,
  "rainfall": 0.8,
  "baseHeight": 0.15,
  "heightVariation": 0.25,
  "topBlock": "mypack:ruby_grass",
  "fillerBlock": "minecraft:dirt",
  "waterColor": "8040A0",
  "placement": { "climate": "warm", "weight": 8, "villages": true },
  "spawns": [
    { "entity": "minecraft:sheep", "type": "creature", "weight": 12, "min": 2, "max": 4 }
  ],
  "spawnRates": { "surfaceNight": 0.5, "undergroundDay": 2.0 }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `name` | nein | string | der Dateiname | Name, den der Spieler sieht |
| `id` | nein | int | wird vergeben | Feste Biom-ID. Setz sie nur, wenn du sie stabil brauchst |
| `type` | nein | Liste von Dictionary-Typen | keine | Etwa `FOREST`, `COLD`, `NETHER` |
| `temperature` | nein | float | `0.5` | Unter 0.15 schneit es, über 1.0 ist es wüstenheiß |
| `rainfall` | nein | float, 0 bis 1 | `0.5` | Wie feucht es ist |
| `rain` | nein | boolean | `true` | Ob es überhaupt Wetter gibt |
| `snow` | nein | boolean | `false` | Ob Regen als Schnee fällt |
| `baseHeight` | nein | float | `0.1` | Geländehöhe. Meereshöhe ist 0, Ebenen 0.125 |
| `heightVariation` | nein | float | `0.2` | Wie hügelig es ist |
| `topBlock` | nein | Blockname | Gras | Der Oberflächenblock |
| `fillerBlock` | nein | Blockname | Erde | Direkt unter der Oberfläche |
| `stoneBlock` | nein | Blockname | Stein | Die Masse des Untergrunds |
| `types` | nein | Liste von Dictionary-Typen | keine | Registriert das Biom unter diesen, etwa `FOREST` oder `WET`, damit andere Mods es finden |
| `waterColor` | nein | Hex-Farbe | `FFFFFF` | Wasserfärbung |
| `baseBiome` | nein | Biomname | keiner | Ein vorhandenes Biom, von dem Einstellungen kopiert werden |
| `decoration` | nein | Objekt | Vanilla-Anzahlen | Anzahlen pro Chunk für Bäume, Gras, Blumen, Zuckerrohr, Kakteen, Seen, Ton und den Rest |
| `spawns` | nein | Liste von Objekten | Vanilla-Liste | Siehe unten |
| `keepDefaultSpawns` | nein | boolean | `false` | Vanillas Liste neben deiner behalten |
| `spawnChance` | nein | float, unter 1 | `0.1` | Wie wahrscheinlich beim ersten Erzeugen des Landes eine weitere Herde gesetzt wird. Das Spiel würfelt weiter, solange es Erfolg hat, `1` hört also nie auf und füllt die Welt, bis kein Platz mehr ist. Alles ab 0.99 wird abgelehnt und durch 0.99 ersetzt |
| `spawnRates` | nein | Objekt aus `surfaceDay`, `surfaceNight`, `undergroundDay`, `undergroundNight` zu einem Faktor | keines | Wie oft feindliche Mobs hier spawnen, anstelle der globalen Einstellungen. Siehe unten |
| `placement` | nein | Objekt |, | Wo es generiert. Siehe unten |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

Ein Spawn-Eintrag nimmt `entity` (Pflicht), `type` (`creature`, einer der [Kreaturtypen](#wertelisten)), `weight` (`10`), `min` (`1`) und `max` (`min`).

Bei `spawnRates` geht es ausschließlich um feindliche Mobs, um sonst nichts. Es nimmt vier Schlüssel und keine anderen: `surfaceDay` und `surfaceNight` für Orte, an denen der Himmel zu sehen ist, `undergroundDay` und `undergroundNight` für Orte, an denen er es nicht ist. Jeder ist ein Faktor darauf, wie oft ein feindlicher Mob erscheinen darf: `1` ist die gewöhnliche Rate, `0` unterbindet sie ganz, unter 1 weist einen Teil der Versuche ab, und über 1 lässt Versuche durch, die das Spiel sonst abgelehnt hätte, `2` sind also doppelt so viele. Ein weggelassener Schlüssel heißt, dass das Biom nicht entscheidet, und es gilt die globale Einstellung für diese Zeit und diesen Ort. Alles andere, was hier steht, ist kein Schlüssel und wird ignoriert – eine Rate, die nach einem Kreaturtyp benannt ist, tut also überhaupt nichts.

`placement`:

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `climate` | nein | string | keiner | Welcher Vanilla-Klimagruppe es beitritt |
| `weight` | nein | int | `10` | Wie oft es gegenüber seinen Nachbarn gezogen wird |
| `villages` | nein | boolean | `false` | Dörfer dürfen generieren |
| `villageSpawn` | nein | boolean | `true` | Dorfbewohner dürfen darin spawnen |
| `strongholds` | nein | boolean | `false` | Festungen dürfen generieren |
| `playerSpawn` | nein | boolean | `false` | Der Weltspawn darf hier liegen |

## Dimensionen

`dimensions/*.json`

```json
{
  "id": 12,
  "suffix": "DIM_ruby",
  "keepLoaded": false,
  "terrain": { "type": "overworld", "structures": false },
  "biomes": { "source": "single", "biome": "mypack:ruby_forest" },
  "sky": {
    "skyColor": "3B1E4A",
    "fogColor": "20102A",
    "cloudHeight": 160,
    "hasSkyLight": true,
    "ambientLight": 0.1,
    "movementFactor": 4.0
  },
  "gameRules": { "doMobSpawning": "false" }
}
```

**Oberste Ebene**

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `id` | ja | int |, | Die Dimensions-ID. Darf mit keinem anderen Mod kollidieren |
| `suffix` | nein | string | `DIM_<name>` | Der Speicherordner |
| `keepLoaded` | nein | boolean | `false` | Geladen halten, auch wenn niemand darin ist |
| `gameRules` | nein | Objekt | keines | Regeln, die nur hier gelten |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

**`terrain`**

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `type` | nein | `overworld`, `flat`, `void`, `nether`, `end` | `overworld` | Welcher Generator sie baut |
| `generatorOptions` | nein | string | keiner | Der Generator-String, wie ihn eine Superflach-Vorlage nutzt |
| `structures` | nein | boolean | `true` | Ob Vanilla-Strukturen generieren |

**`biomes`**

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `source` | nein | `inherit`, `single` | `inherit` | `inherit` nutzt die normale Biomkarte, `single` überall ein einziges Biom |
| `biome` | bei `single` | Biomname | `minecraft:plains` | Welches Biom das ist |

**`sky`**

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `hasSkyLight` | nein | boolean | `true` | Ob Tageslicht sie erreicht |
| `surfaceWorld` | nein | boolean | `true` | Ob Karten und Kompasse sich wie in der Oberwelt verhalten |
| `respawn` | nein | boolean | `true` | Ob Spieler hier respawnen |
| `respawnDimension` | nein | int | keine | Wo sie stattdessen respawnen |
| `spawning` | nein | boolean | `true` | Ob Mobs spawnen |
| `nether` | nein | boolean | `false` | Wird für Portale und Decken wie der Nether behandelt |
| `beds` | nein | boolean | `true` | Aus explodieren Betten |
| `waterVaporizes` | nein | boolean | `false` | Wasser verdampft |
| `cloudHeight` | nein | int | `128` | Wo die Wolken hängen |
| `cloudColor` | nein | Hex-Farbe | keine | Wolkenfärbung |
| `groundLevel` | nein | int | `63` | Meereshöhe, genutzt für den Horizont und die Spawnsuche |
| `movementFactor` | nein | float | `1.0` | Entfernungsverhältnis zur Oberwelt. Der Nether nutzt 8 |
| `fogColor` | nein | Hex-Farbe | keine | Nebelfärbung |
| `showFog` | nein | boolean | `false` | Dichter Nebel, wie im Nether |
| `skyColor` | nein | Hex-Farbe | keine | Himmelsfärbung |
| `fixedTime` | nein | int, Ticks | keine | Hält die Tageszeit fest |
| `sunriseColors` | nein | boolean | `true` | Ob Sonnenauf- und -untergang eingefärbt werden |
| `ambientLight` | nein | float, 0 bis 1 | `0.0` | Mindestlicht überall |
| `starBrightness` | nein | float, 0 bis 1 | keine | Wie hell die Sterne sind |
| `renderSky` | nein | boolean | `true` | Aus zeichnet nichts Himmel, Sonne, Mond oder Sterne, es bleibt die Nebelfarbe |
| `renderClouds` | nein | boolean | `true` | Aus werden keine Wolken gezeichnet |
| `renderWeather` | nein | boolean | `true` | Aus werden weder Regen noch Schnee gezeichnet |

Farben und die drei Render-Schalter sind alles, was geboten wird. Etwas Eigenes dort oben zu zeichnen – eine bemalte Kuppel, eine eigene Sonne und einen eigenen Mond – braucht weiterhin Java.

## Portale und Tore

Ein `portal`-Block trägt einen `portal`-Abschnitt:

```json
{
  "type": "portal",
  "material": "portal",
  "portal": {
    "dimension": 12,
    "returnDimension": 0,
    "gate": "mypack:ruby_gate",
    "cooldown": 60,
    "platform": true,
    "platformBlock": "mypack:ruby_block",
    "owned": true
  },
  "variants": { "ruby_portal": { "meta": 0, "hardness": -1, "light": 11 } }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `dimension` | ja | int |, | Wohin es dich schickt |
| `returnDimension` | nein | int | `0` | Wohin es dich zurückschickt |
| `gate` | nein | Torname | keiner | Ein Tor, das offen sein muss, um durchzukommen |
| `cooldown` | nein | int, Ticks | `60` | Bis derselbe Spieler es wieder benutzen kann |
| `platform` | nein | boolean | `true` | Bei der Ankunft eine Landeplattform bauen |
| `platformBlock` | nein | Blockname | der eigene Rahmen des Portals | Woraus diese Plattform besteht |
| `sound` | nein | Soundname | keiner | Wird beim Durchgehen abgespielt |
| `owned` | nein | boolean | `true` | Nur wer es gebaut hat und wen er zulässt, darf es benutzen. Ein Portal mit Besitzer ist außerdem immun gegen Explosionen |

`gates/*.json`

```json
{
  "name": "The Ruby Gate",
  "scope": "player",
  "dimension": 12,
  "open": false,
  "unlock": { "consume": "mypack:ruby", "consumeCount": 4, "killed": "minecraft:wither" },
  "unlockedMessage": "%dim% is now open",
  "blockedMessage": "You need %item% to enter %dim%",
  "safeReturn": true
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `dimension` | ja | int |, | Die Dimension, die es bewacht |
| `name` | nein | string | der Dateiname | Wird dem Spieler angezeigt |
| `scope` | nein | `player`, `global` | `player` | Ein Spieler nach dem anderen oder die ganze Welt auf einmal |
| `open` | nein | boolean | `false` | Ob es offen startet |
| `unlock` | nein | Objekt |, | Was es öffnet. Siehe unten |
| `unlockedMessage` | nein | string | `%dim% is now open` | Wird beim Öffnen angezeigt |
| `blockedMessage` | nein | string | `You need %item% to enter %dim%` | Wird bei der Abweisung angezeigt |
| `safeReturn` | nein | boolean | `false` | Ein abgewiesener Rückweg landet trotzdem sicher irgendwo, statt abgelehnt zu werden |
| `portalBlocks` | nein | Liste von Blocknamen | jedes Portal | Begrenzt das Tor auf diese Portalblöcke, eine Dimension kann also eine bewachte und eine offene Tür haben |

`unlock` nimmt `hold` (ein Item, das in der Hand sein muss), `consume` mit `consumeCount` (`1`), `craft` (ein Item, das gecraftet worden sein muss), `advancement` und `killed` (ein Entity-Name; das Tor öffnet sich für den, der eine davon erlegt, ein Boss kann also den Schlüssel zu einer Welt tragen) mit `killedCount` (`1`), wenn eine nicht reicht, gezählt pro Spieler oder für die ganze Welt, je nach `scope`. Mit `killedDrops` (ein Itemname) legen die gezählten Abschüsse stattdessen dieses Item dem Erleger vor die Füße, statt das Tor zu öffnen, und die Zählung beginnt von vorn – ein Schlüssel lässt sich also erneut verdienen und an jemanden weitergeben, der nie dafür gekämpft hat; sperr dann über `hold` oder `consume` desselben Items, um es zum Schlüssel zu machen. `%item%`, `%mob%` und `%dim%` werden für dich eingesetzt. Ein Schlüssel, den ein Mob droppt, braucht hier nichts Besonderes: Gib dem Mob den Drop und sperr über `hold` oder `consume`.

## Weltvorlagen

`worldtemplates/*.json` fasst die Gestalt einer Welt in einer Datei zusammen, ein Pack liefert also eine ganze Welt auf einmal, statt vom Spieler ein Dutzend Config-Optionen zu verlangen.

```json
{
  "name": "Ruby World",
  "default": "void",
  "dimensions": [0],
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
| `default` | nein | Biomname oder `void` | `void` | Was ein Biom füllt, das die Sperre entfernt hat |
| `roles` | nein | Objekt aus Rolle zu Biom | keines | Biome, die bestimmte Rollen füllen, etwa Ozean oder Fluss |
| `structures` | nein | Objekt aus [Strukturname](#wertelisten) zu boolean | keines | Vanilla-Strukturen, ein- oder ausgeschaltet |
| `settings` | nein | Objekt | keines | Config-Werte, die die Vorlage setzt |
| `dimensions` | nein | Liste von Ints | jede Dimension | Für welche Dimensionen sie gilt |

`settings` nutzt dieselben Schlüsselnamen wie die Config, es gibt also keine Übersetzungstabelle zu lernen.

Welche Vorlage aktiv ist, entscheidet die Config-Option `worldTemplate`. Steht sie auf `auto`, gewinnt das Pack mit der höchsten Priorität, das eine mitbringt, in derselben Reihenfolge, der alles andere auch folgt. Nennst du dort eine Vorlage, ist sie gesetzt.

## Welt-Intro

`worldintro/*.json` zeigt eine Folge von Seiten, wenn ein Spieler die Welt betritt, bevor er die Kontrolle bekommt. Laufender Text über einem Bild, eine Titelkarte, eine Diaschau – oder alles drei hintereinander.

```json
{
  "once": true,
  "music": "minecraft:music.credits",
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

Textdateien liegen unter `assets/<namespace>/texts/<name>.txt`. Reiner Text, ein Absatz pro Zeile, und Leerzeilen bleiben Leerzeilen. `PLAYERNAME` wird durch den Namen des Spielers ersetzt, dieselbe Ersetzung, die auch das Vanilla-Endgedicht nutzt.

`time` legt fest, wie lange die Seite dauert, dieselbe Seite braucht also gleich lang, ob eine Zeile darauf steht oder zwanzig. Die Lesegeschwindigkeit stellst du darüber ein, wie viel du auf die Seite packst. Lässt du `time` weg, läuft die Seite so schnell wie der Vanilla-Abspann, wo mehr Text einfach länger dauert.

Eine laufende Seite geht zur nächsten über, wenn ihre Zeit um ist. Die letzte Seite geht nie von selbst weiter, sie wartet. Unten stehen **Next Page** und **Skip All**, auf der letzten Seite ein einzelnes **Continue to World**. Escape tut dasselbe wie Skip All. Statische Seiten zentrieren jede Zeile. Laufende Seiten halten sich an eine feste Spalte, so wie der Abspann.

Im Einzelspieler pausiert die Welt hinter dem Intro, es schleicht sich also nichts an den Spieler heran, während er liest. Auf einem Server läuft die Welt weiter, und ein Vanilla-Client sieht das Intro überhaupt nicht und tritt ganz normal bei.

`once` wird in den Spielerdaten gespeichert und übersteht den Tod. `/rdplserver intro` setzt es für den zurück, der ihn ausführt, das Intro läuft dann beim nächsten Beitritt wieder. Es wird nicht sofort noch einmal abgespielt, damit es kein Weg zurück in die Einstiegssequenz mitten im Spiel wird.

Hintergründe werden auf die Fenstergröße gezogen, ein 16:9-Bild passt also zu einem 16:9-Fenster, ein quadratisches sieht gestaucht aus. Schneid das Bild passend zu, statt dich auf die Anpassung zu verlassen. `music` nimmt jedes registrierte Sound-Event, von Vanilla oder aus deinem eigenen Pack über `sounds`. Es läuft nicht in Schleife, ein kurzes Stück ist also irgendwann zu Ende und lässt Stille zurück.

Bringt mehr als ein Pack ein Intro mit, laufen ihre Seiten in Pack-Reihenfolge hintereinander, statt dass eines gewinnt. Sperr sie mit `requires`, wenn du nur eines willst.

## Spielregeln

`gamerules/*.json`

```json
{
  "0": {
    "doFireTick": "false",
    "keepInventory": "true",
    "randomTickSpeed": "3"
  },
  "-1": {
    "doFireTick": "true"
  }
}
```

Jeder Schlüssel ist die ID der Welt, zu der die Regeln gehören: `0` für die Oberwelt, `-1` für den Nether, `1` für das Ende und was ein Mod für seine eigene nutzt. Werte sind Strings, so wie im Befehl `/gamerule`, also `"false"` statt `false`. Sie werden auf neue Welten angewendet. Eine Dimensionsdatei trägt dieselben Regeln stattdessen in einem `gameRules`-Block, der immer nur für diese eine Welt gilt.


## Härtegruppen

`hardness/*.json` gibt einer Gruppe von Blöcken einen Faktor für die Abbauzeit, der pro Blockposition gewürfelt wird. Der Block selbst wird nie verändert: Nichts wird registriert, nichts in die Welt geschrieben, und eine Welt ohne das Pack ist ganz gewöhnliches Vanilla.

```json
{
  "blocks": ["minecraft:stone:0"],
  "miningTime": { "min": 1.0, "max": 20.0 },
  "buckets": 10,
  "field": { "type": "speckle", "spread": 0.15 }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er tut |
| --- | --- | --- | --- | --- |
| `blocks` | ja | Liste von Blocknamen oder Objekten |, | Die Gruppe. Dieselben drei Formen wie `replace` bei der Weltgenerierung |
| `except` | nein | Liste von Blocknamen oder Objekten | keine | Wieder aus der Gruppe genommen, was auch immer `blocks` sagt |
| `miningTime` | nein | Zahl oder Objekt mit `min` und `max` | `1.0` | Um wie viel länger der Block zum Abbauen braucht |
| `blastResistance` | nein | Zahl oder Objekt mit `min` und `max` | `1.0` | Multipliziert den Explosionswiderstand des Blocks |
| `buckets` | nein | 1 bis 256 | `10` | In wie viele Stufen die Spanne geteilt wird |
| `minHeight` | nein | Ganzzahl | `0` | Darunter ist der Wurf die härteste Stufe |
| `maxHeight` | nein | Ganzzahl | `255` | Darüber ist der Wurf die härteste Stufe |
| `field` | nein | Objekt | siehe unten | Die Form, zu der sich der Wurf zusammenballt |
| `requires` | nein | Liste von Mod-Ids oder Pack-Namensräumen | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

Eine einzelne Zahl gibt jedem Block der Gruppe denselben Faktor, und nichts wird gewürfelt. Ein `min` und ein `max` würfeln pro Position: `max`, wo das Feld leer ist, `min` in der Mitte eines Nestes, und die Stufen dazwischen entscheidet `buckets`.

### Das Feld

Der Wurf geschieht nicht für jeden Block ganz allein, sonst wären hart und weich reines Rauschen ohne jede Form. `field` bestimmt, welche Form dabei herauskommt, und `type` wählt zwischen zwei Wegen dorthin.

| Schlüssel | Pflicht | Wert | Standard | Was er tut |
| --- | --- | --- | --- | --- |
| `type` | nein | `speckle` oder `seeded` | `speckle` | Welches der beiden unten genommen wird |

#### speckle

Jeder Block zieht seine eigene Stufe, und ein Block eine Fläche weiter kann eine schwächere Stufe an ihn weitergeben. Das gibt dichte, feine Sprenkel, meist einzelne Blöcke, mit hier und da einem größeren Nest, wo sie zusammentreffen. Von beiden kommt das dem Gefühl beim Abbauen in der Vorlage am nächsten.

| Schlüssel | Pflicht | Wert | Standard | Was er tut |
| --- | --- | --- | --- | --- |
| `chances` | nein | Liste von Ganzzahlen, je Tausend | `[30, 30, 20, 20, 10, 10, 10, 10, 50]` | Wie oft ein Block auf welcher Stufe anfängt, weichste zuletzt. Was übrig bleibt, ist die härteste Stufe |
| `spread` | nein | 0.0 bis 1.0 | `0.15` | Wie oft eine Stufe an den Nachbarblock weitergeht, eine bis drei Stufen schwächer |

Die Liste wird von hinten als weichste gelesen, der letzte Eintrag ist also die weichste Stufe und der erste liegt eine über der härtesten. Mit den Zahlen oben sind etwa sieben von zehn Blöcken die härteste Stufe, der Rest liegt verstreut dazwischen.

#### seeded

Saatpunkte sitzen auf einem Gitter, das sich aus der Welt und der Position ergibt, und die Stufe eines Blocks kommt daher, wie nah er am nächsten liegt. Das gibt weniger, größere, rundere Nester, die ineinanderlaufen, und es kann Arme treiben.

| Schlüssel | Pflicht | Wert | Standard | Was er tut |
| --- | --- | --- | --- | --- |
| `cell` | nein | Ganzzahl, Blöcke | `8` | Wie weit die Saatpunkte auseinanderliegen |
| `seeds` | nein | 1 bis 4 | `1` | Saatpunkte je Zelle |
| `reach` | nein | Kommazahl, Blöcke | `3.0` | Wie weit ein Saatpunkt wirkt |
| `arms` | nein | 0 bis 6 | `0` | Arme, die von jedem Saatpunkt ausgehen |
| `armReach` | nein | Kommazahl, Blöcke | `0.0` | Wie weit die Arme reichen |

Ohne `arms` sind die Nester rund. Gibt man einem Saatpunkt Arme, wird er zu einem Knoten mit Ranken, und Arme benachbarter Knoten strecken sich einander entgegen – das ist dann eine Ader statt eines Klumpens. Halte `reach` über der Hälfte von `cell`, sonst können die Nester einander nicht berühren und es bleiben einzelne Kugeln mit nichts dazwischen.

### Sichtbar machen

Der Faktor allein ist unsichtbar. Damit ein Spieler sieht, welche Blöcke zäh sind, gib dem Block einen Blockstate mit einer Variante je Stufe, alle mit gleichem Gewicht, die härteste zuerst:

```json
{
  "variants": {
    "normal": [
      { "model": "mypack:stone_step0", "weight": 1 },
      { "model": "mypack:stone_step1", "weight": 1 }
    ]
  }
}
```

Minecraft wählt eine Variante ohnehin schon anhand der Position eines Blocks, und eine Härtegruppe gibt ihm stattdessen die Stufe, so passen Textur und Faktor immer zusammen.

Drei Dinge müssen stimmen, und keines davon meldet sich, wenn es falsch ist.

**Genau `buckets` Einträge, alle gleich schwer.** Die Stufe wird als Platz in der Liste genommen, eine Liste anderer Länge oder mit unterschiedlichen Gewichten zeigt also stillschweigend auf die falsche Textur.

**Ein Modellname ohne `block/` davor.** Ein Blockstate setzt `block/` selbst davor, `"model": "mypack:step_stone"` liest also die Datei unter `models/block/step_stone.json`. Schreibt man `mypack:block/step_stone`, wird nach `models/block/block/step_stone.json` gesucht, was es nicht gibt, und der Eintrag fällt kommentarlos weg.

**Derselbe Schlüssel, nach dem das Spiel fragt.** Nicht jeder Block ist so verschlüsselt, wie seine Eigenschaften sich lesen. Vanilla-Stein legt alles unter `normal` ab, nicht unter `variant=stone`, eine Ersetzung, die nur `variant=stone` schreibt, wird also zusammengeführt und danach nie angesehen. Beide Schlüssel zu schreiben ist unbedenklich, denn zusammengeführt wird je Schlüssel, und ein Pack sticht, was vorher da war.

Schalte `worldgenDebug` ein, dann wird jede Härtegruppe beim Betreten einer Welt gegen ihr gebackenes Modell geprüft: welcher Blockstate, wie viele Varianten übrig blieben, welche Textur jede davon bekam und welche Packs das Spiel dafür zusammengeführt hat. Das ist der schnellste Weg zu allen drei Punkten oben, und es warnt auch, wenn das Ersetzen eines geteilten Blockstates einen Zustand verändert hat, den die Gruppe nie genannt hat.

### Was nicht erreicht wird

Nur das Abbauen durch einen Spieler wird verändert. Maschinen, die Blöcke abbauen, lesen die Härte des Blocks direkt und merken nichts davon. Blöcke, die ein Spieler setzt, werden wie alle anderen gewürfelt, denn der Wurf gehört zum Ort und nicht zum Block, und ein anderswohin getragener Block nimmt an, was sein neuer Ort sagt.

# Generieren

## Worldgen-Einträge

`worldgen/*.json` beschreibt etwas, das generiert. Jeder Eintrag ist eine **Form**, gesetzt von einer **Verteilung**, gefiltert danach, wo sie erlaubt ist.

```json
{
  "block": "mypack:ruby_ore",
  "meta": 0,
  "size": 8,
  "attempts": 12,
  "replace": ["minecraft:stone"],
  "dimensions": [0],
  "dimensionsAreBlacklist": false,
  "biomes": ["minecraft:extreme_hills"],
  "biomeTypes": ["MOUNTAIN"],
  "biomesAreBlacklist": false,
  "minTemperature": -100.0,
  "maxTemperature": 100.0,
  "minRainfall": -100.0,
  "maxRainfall": 100.0,
  "minHeight": 8,
  "maxHeight": 48,
  "minDistanceFromSpawn": 0,
  "sparse": false,
  "retrogen": false,
  "retrogenKey": "ruby_v1",
  "requires": ["quark"],
  "shape": { "type": "cluster" },
  "spread": { "type": "even" }
}
```

Pflicht ist nur `block`, alles andere darf wegbleiben und nimmt seinen Standardwert. `blocks` ersetzt `block`, wenn einer nicht reicht, und hat weiter unten ein eigenes Beispiel.

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `block` | ja | Blockname |, | Was gesetzt wird |
| `meta` | nein | int | `0` | Welche Variante dieses Blocks |
| `blocks` | nein | Liste von Objekten | keine | Eine gewichtete Liste, genutzt statt eines einzelnen Blocks. Siehe unten |
| `size` | nein | int oder Bereich | `8` | Wie viele Blöcke ein Versuch setzt, oder wie groß eine Form mit Radius ausfällt |
| `attempts` | nein | int oder Bereich | `1` | Wie oft es pro Chunk versucht wird |
| `replace` | nein | Liste von Blocknamen oder Objekten | `["minecraft:stone"]` | Was ersetzt werden darf. Siehe unten |
| `adjacent` | nein | Liste von Blocknamen oder Objekten | keine | Setzt nur dort, wo einer davon unter den 26 Blöcken steht, die die Stelle berühren. Dieselben drei Formen wie `replace` |
| `minHeight` | nein | int | `0` | Niedrigstes y, auf dem gesetzt wird |
| `maxHeight` | nein | int | `64` | Höchstes y, auf dem gesetzt wird |
| `dimensions` | nein | Liste von Ints | jede Dimension | In welchen Dimensionen es läuft |
| `dimensionsAreBlacklist` | nein | boolean | `false` | Macht aus dieser Liste die zu meidenden |
| `biomes` | nein | Liste von Biomnamen | jedes Biom | In welchen Biomen es läuft |
| `biomeTypes` | nein | Liste von Dictionary-Typen | keine | Biome nach Typ, etwa `FOREST` oder `NETHER` |
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

### Gewichtete Blöcke

`blocks` ersetzt `block`, wenn ein Eintrag nicht reicht. Die Gewichte sind relativ, 80 und 20 ist also vier zu eins.

```json
{
  "blocks": [
    { "block": "minecraft:wool", "weight": 80, "properties": { "color": "magenta" } },
    { "block": "minecraft:wool", "weight": 20, "properties": { "color": "lime" } }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `block` | ja | Blockname |, | Was gesetzt wird |
| `meta` | nein | int | `0` | Welche Variante |
| `weight` | nein | int | `1` | Wie oft dieser gegenüber den anderen gezogen wird |
| `properties` | nein | Objekt aus Eigenschaft zu Wert | keines | Blockstate-Eigenschaften nach Namen, für Zustände ohne eigene Metadaten |

`block` und `meta` sind auch bei genutztem `blocks` weiterhin auf oberster Ebene der Datei Pflicht; der erste Eintrag ist ein guter Wert dafür.

### Ziele für `replace`

`replace` ist eine Liste, und jeder Eintrag hat eine von drei Formen.

| Form | Beispiel | Worauf sie passt |
| --- | --- | --- |
| Name | `"minecraft:stone"` | Jeder Zustand dieses Blocks |
| Name mit Metadaten | `"minecraft:stone:3"` | Nur diese Metadaten, hier Diorit |
| Objekt | `{ "block": "minecraft:stone", "properties": { "variant": "andesite" } }` | Nur dieser Zustand |

Die Objektform nimmt statt `properties` auch `meta`, was dasselbe ist wie die Form mit Doppelpunkt. Nimm `"minecraft:air"`, um in offenem Raum zu generieren.

### Benachbarte Blöcke

`adjacent` nimmt dieselben drei Formen wie `replace` und legt eine zweite Bedingung darüber: Die Stelle wird nur genommen, wenn mindestens einer der 26 Blöcke, die sie berühren, also Flächen, Kanten und Ecken, auf die Liste passt. Ohne den Eintrag wird nichts geprüft.

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

## Formen

Ein `shape`-Block mit einem `type`. Schlüssel, die bei einem Typ nicht aufgeführt sind, ignoriert er.

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
| `imprint` | Eine deiner `.nbt`-Vorlagen. Eine, die in einen Chunk passt, wird so verschoben, dass sie ganz im gerade gebauten Chunk landet, statt in einen Nachbarn zu ragen, den es noch nicht gibt – egal, wie herum sie gedreht ist; eine, die größer als ein Chunk ist, wird nur dort gesetzt, wo der Boden ringsum schon existiert |
| `belt` | Ein Cluster über mehrere Chunks hinweg, für Gesteinsregionen |
| `field` | Adern, die für jeden Block auf einmal ermittelt werden, mit derselben Form wie Härtegruppen |

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
| `structure` | imprint | `namespace:name` | keine | Die Vorlage, die gesetzt wird |
| `integrity` | imprint | 1 bis 100 | `100` | Prozentsatz der Blöcke der Vorlage, die tatsächlich erscheinen |
| `structures` | imprint | Liste | keine | Mehrere Vorlagen zur Auswahl, eine davon wird jedes Mal gesetzt. Jeder Eintrag ist `{ "structure": "namespace:name", "weight": 3 }` oder ein bloßer Name für gleiche Chancen. Überschreibt `structure` |
| `turns` | imprint | Liste | beliebig | Wie herum sie gesetzt werden darf: `none`, `quarter`, `half`, `threequarter`. Einträge dürfen ein `weight` tragen. Weggelassen sind alle vier gleich wahrscheinlich |
| `mirrors` | imprint | Liste | keine | Sie zusätzlich spiegeln: `none`, `leftright`, `frontback`, mit optionalem `weight` |
| `field` | field | Objekt | `{ "type": "speckle" }` | Wie das Feld errechnet wird. Dieselben Schlüssel wie das `field` einer Härtegruppe, beschrieben unter [Das Feld](#das-feld): `speckle` mit `chances` und `spread`, oder `seeded` mit `cell`, `seeds`, `reach`, `arms` und `armReach` |
| `threshold` | field | 0,0 bis 1,0 | `0,5` | Wie stark das Feld an einem Block sein muss, bevor dort gesetzt wird. Niedriger füllt mehr |
| `rarity` | belt | int | `400` | Ein Cluster pro so vielen Chunks |
| `rarityIsPerChunk` | belt | boolean | `false` | Macht aus `rarity` stattdessen die Anzahl Cluster pro Chunk |

```json
{
  "shape": { "type": "geode", "radius": 6, "height": 8, "outline": "minecraft:obsidian", "fill": "minecraft:glowstone" }
}
```

```json
{
  "shape": { "type": "tree", "log": "mypack:ruby_log", "leaves": "mypack:ruby_leaves", "height": { "min": 4, "max": 7 }, "surface": ["minecraft:grass"] }
}
```

Ein `tree` ohne `log` oder `leaves` generiert nichts und sagt das im Log.

Ein `field`-Gang ist die eine Form, die du beschreibst statt auswählst. Er nutzt dasselbe Gitter wie die Härtegruppen: `seeded` mit ein paar Armen ergibt Knoten mit Ranken, die zu ihren Nachbarn hinüberreichen, also einen Gang statt eines Klumpens, und `threshold` entscheidet, wie viel davon fest genug zum Setzen ist:

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

Ein `belt` ist eine Kugel, die weit größer als ein Chunk ist, gedacht für Gesteinsregionen statt für Erzadern. Sein `radius` ist die Größe der Kugel, und jeder Chunk rechnet für sich selbst aus, wo die Kugeln in seiner Nähe anfangen, aus dem Welt-Seed und dem Namen des Eintrags – ein Gürtel kommt also vollständig heraus, egal in welcher Reihenfolge die Chunks generiert werden, und es wird nie etwas in einen Nachbar-Chunk geschrieben.

```json
{
  "shape": { "type": "belt", "radius": 32, "rarity": 400 }
}
```

Ein Gürtel ignoriert `attempts` und `spread`, weil er pro Chunk statt pro Versuch gesetzt wird. `minHeight` und `maxHeight` sind das Band, in dem die Mittelpunkte liegen, und die Kugel reicht um `radius` über dieses Band hinaus. `replace` entscheidet, was er frisst, und `biomes` sowie die Grenzen für Temperatur und Niederschlag werden am Mittelpunkt geprüft – ein Gürtel erscheint also entweder ganz oder gar nicht, statt an einer Biomgrenze abgeschnitten zu werden.

Der Aufwand wächst mit der dritten Potenz von `radius`, und ein niedriger `rarity`-Wert vervielfacht ihn, fang also bei den Standardwerten an und erhöhe den Radius langsam.

## Verteilung

Ein `spread`-Block mit einem `type`.

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

```json
{
  "spread": { "type": "centered", "center": 32, "range": 12, "smoothness": 3 }
}
```

## Retrogen

Ein Eintrag mit `"retrogen": true` wird auch in Chunks generiert, die gespeichert wurden, bevor du ihn hinzugefügt hast. Jeder Chunk merkt sich, was er schon bekommen hat, nichts wird also zweimal gemacht.

Das Flag am Eintrag markiert ihn nur als infrage kommend. Das Nachholen selbst schaltet die Einstellung `retrogen` ein, die ein Pack in seinem `settings`-Block oder ein Spieler in der Config setzen kann, und sie ist standardmäßig aus. Daneben entscheidet `adoptExistingChunks`, was beim ersten Sehen eines alten Chunks passiert: an wird der Chunk so gestempelt, als hätte dieses Pack ihn schon generiert, und nie nachgeholt; aus wird er wie jeder andere nachgeholt. `retrogen` einzuschalten, während auch `adoptExistingChunks` an ist, bewirkt nichts, weil jeder alte Chunk abgehakt wird, bevor er in die Warteschlange kommen kann. Um eine bestehende Welt zu füllen, setzt du `retrogen` an und `adoptExistingChunks` aus, beides zusammen.

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

Die ersten 12 Chunks um den Spawn werden immer in die Hand genommen, ganz gleich was ein Pack oder die Config sagt, denn genau so viel baut das Spiel selbst, bevor jemand beitritt. Sich selbst überlassen kommt dieser Boden unbeleuchtet an und wird Chunk für Chunk nachgezogen, während der Spieler darüber läuft; übernommen wird er in einem Zug fertig, und der Spieler landet auf Boden, der schon steht. `pregenOnNewWorld` legt fest, wie viel weiter gereicht wird, und der Befehl startet einen Lauf von Hand.

`/rdplserver pregen <radius>` baut jeden Chunk innerhalb so vieler Chunks um die Stelle, an der er ausgeführt wird. `status` sagt, wie weit es ist, `stop` beendet es, und `<radius> relight` lässt nur den Beleuchtungsdurchgang über Land laufen, das es schon gibt – es glättet die Nähte, die der Durchlauf nicht erreichen konnte, und lässt alles in Ruhe, was nie gebaut wurde.

Während ein Durchlauf läuft, wird jeder festgehalten: zum Zuschauer gemacht, an Ort und Stelle gehalten, mit einer pulsierenden Zeile mitten im Bild, die Welt ringsum pausiert. Der Modus, in dem jeder Spieler angekommen ist, wird beim Festhalten auf den Spieler geschrieben, ein Speicherstand mitten im Durchlauf, ein Absturz oder ein erneuter Beitritt lässt also nie jemanden als Zuschauer zurück; am Ende des Durchlaufs gibt er genau den Modus zurück, den er genommen hat, oder den `worldGameMode` des Packs, wenn einer gesetzt ist. Der Fortschritt wird jedes Zehntel des Weges gemeldet, jeder Durchlauf beleuchtet sein eigenes Quadrat, wenn er fertig ist, und wenn alles erledigt ist, werden die Spieler freigelassen und begrüßt. Wie weit jede Dimension gebaut wurde, wird in der Welt gespeichert, eine fertige Welt wird also nie noch einmal gebaut – es sei denn, eine der Dateien, in denen das Land einer Dimension liegt, verschwindet von der Platte, was bemerkt wird und diese eine noch einmal bauen lässt.

| Schlüssel | Was er macht | Warum du ihn setzen würdest |
| --- | --- | --- |
| `pregenOnNewWorld` | Radius in Chunks, der um den Spawn gebaut wird, bevor jemand spielt. 12 ist die Untergrenze, und 0 meint diese Untergrenze statt gar nichts, denn 12 Chunks um den Spawn baut das Spiel ohnehin von sich aus: Der Lauf übernimmt diesen Boden und beleuchtet ihn in einem Zug, statt ihn hinter dem Spieler her tröpfeln zu lassen. Höher setzen, um weiter zu reichen als das Spiel | Legt fest, wie weit ein Pack über den Boden hinausreicht, den das Spiel ohnehin baut |
| `pregenDimensions` | Welche Dimensionen gebaut werden, der Reihe nach, jede um ihren eigenen Spawn | Den Nether, das Ende oder deine eigenen Dimensionen dazunehmen |
| `pregenAllDimensions` | Jede registrierte Dimension statt einer Liste, die Oberwelt zuerst | Packs mit vielen Dimensionen. Die Dimensionen jedes Mods zählen mit, achte also auf die Größe |
| `pregenDimensionsWhenEntered` | Diese werden gebaut, wenn zum ersten Mal jemand einen Fuß hineinsetzt, und halten dabei wieder alle fest, bis es fertig ist | Dimensionen, die die meisten Spieler nie besuchen; wer nie hingeht, zahlt nichts |
| `pregenToBorder` | Füllt jede Dimension bis zu ihrer Weltgrenze statt bis zu einem Radius | Begrenzte Welten |
| `pregenBorderLimit` | Wie weit eine Grenze reichen darf, bevor der Durchlauf abgelehnt wird. Nur Config, nie ein Pack-Schlüssel | Ein Schutz gegen einen ausufernden Durchlauf; erhöhe ihn nur, wenn du weißt, wie viel Zeit und Plattenplatz du damit erlaubst |
| `pregenResume` | Ein gestoppter oder unterbrochener Durchlauf macht dort weiter, wo er aufgehört hat. Dimension, Mittelpunkt und Radius des Durchlaufs werden beim Start in den Spielstand geschrieben, ein Absturz, ein Stromausfall oder ein Beenden mitten im Durchlauf setzen beim nächsten Laden also auf etwa zehn Sekunden genau dort wieder an, wo sie gestorben sind. Ein absichtlich gestoppter Durchlauf, per Befehl oder durch den Watchdog, bleibt gestoppt | Lange Durchläufe auf Servern; kleine Durchläufe starten auch ohne das billig neu |
| `pregenKeepLoaded` | Chunks, die hinter dem Durchlauf geladen bleiben, damit die Nachbarn eines Chunks zur Hand sind, wenn er ausgeschmückt und beleuchtet wird | Erhöhe ihn, wenn die Beleuchtungsberichte viele auf später verschobene Chunks melden; kostet Speicher |
| `pregenPauseAbove` | Der Durchlauf legt eine Pause ein, wenn so viele Chunks aufs Schreiben warten | Senke ihn bei einer langsamen Platte |
| `pregenMillisPerRound` | Wie lange jeder Tick mit dem Bau von Land verbringen darf | Auf einer leeren Welt hoch, auf einem Server mit Spielern runter |
| `pregenRunningSays`, `pregenRelightSays`, `pregenFinishedSays`, `pregenStoppedSays` | Die Chatnachrichten für die einzelnen Phasen. Die ersten beiden dürfen `%d` für den Prozentwert und dahinter `%s` für den Namen der Dimension enthalten, oder `%1$d` und `%2$s`, um sie in beliebiger Reihenfolge zu setzen, und enden immer mit ` - ETA 00:00:00` für diesen Durchgang, was keine Einstellung ist. Fertig und gestoppt werden einmal gesagt, wenn alles Angeforderte erledigt ist, und enden mit ` - Total time 00:00:00` für das Ganze, was ebenfalls keine Einstellung ist | Formulier sie im Ton deines Packs, nenne die Dimension, wenn mehrere gebaut werden, oder stell sie stumm |
| `pregenSpectatingSays` | Die Haltezeile mitten im Bild, während Land gebaut wird. Auf dem Standardwert spricht sie die Sprache jedes Spielers; leer zeigt nichts | Halte sie unter etwa fünfunddreißig Zeichen, sonst schneiden kleine Fenster sie ab |
| `welcomeSays` | Die grüne Begrüßung, gezeigt bei jedem Login und nach dem Landbau. Ein bloßer Eintrag ist die Zeile für überall; ein Eintrag `dimension=nachricht` überschreibt sie für diese Dimension und begrüßt außerdem jede Ankunft dort, z. B. `"-1=Welcome to the Nether!"`. Eine leere Nachricht nach dem `=` stellt diese Dimension stumm; eine leere Liste zeigt nichts. Auf dem Standardwert spricht sie die Sprache jedes Spielers | Eine bloße Zeile nennt dein Pack; mit Dimensionszeilen gibst du jeder Welt ihr Thema. Halte die Zeilen unter etwa fünfunddreißig Zeichen |

In einem Pack stehen diese im `settings`-Block einer [Weltvorlage](#weltvorlagen), wie jeder andere `chunks`-Schlüssel auch. Hier alle zusammen, mit `pregenBorderLimit` als einzigem Fehlenden, weil nur die Config ihn hält:

```json
{
  "settings": {
    "pregenOnNewWorld": 63,
    "pregenDimensions": [0, -1],
    "pregenAllDimensions": false,
    "pregenDimensionsWhenEntered": [1],
    "pregenToBorder": false,
    "pregenResume": true,
    "pregenKeepLoaded": 2048,
    "pregenPauseAbove": 2000,
    "pregenMillisPerRound": 200,
    "pregenRunningSays": "Building your world, %d%% done",
    "pregenRelightSays": "Lighting your world, %d%% done",
    "pregenFinishedSays": "Your world is ready",
    "pregenStoppedSays": "World building stopped",
    "pregenSpectatingSays": "Spectating until the world is ready",
    "pregenWelcomeSays": "Welcome to Ruby World!"
  }
}
```

Lass ihn vor der Auslieferung einmal selbst durchlaufen, mit dem Radius, den du ausliefern willst, von Anfang bis Ende. Die Zahl der Chunks wächst mit dem Quadrat des Radius: 63 in jede Richtung sind sechzehntausend Chunks, 500 sind über eine Million, bei rund zehn Kilobyte pro Stück – der Region-Ordner deiner Testwelt und die Uhr an der Wand sind also die ehrlichen Zahlen, die du den Spielern nennen kannst. Liefere keinen Radius aus, der nie durchgelaufen ist.


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

Wie ein Gürtel übergeht ein Feld `attempts` und `spread`, da es je Chunk statt je Versuch gefragt wird, und es schreibt nie in einen Nachbar-Chunk. Es ergibt sich aus dem Weltsamen und dem eigenen Namen des Eintrags, derselbe Samen gibt also immer dieselben Adern, und zwei Einträge mit verschiedenen Namen decken sich nie. `replace`, `adjacent`, `biomes` und die Klimagrenzen gelten wie sonst auch.

### Wie es schnell bleibt

Alles Folgende ist das Wie, nicht das Was: die Technik, die einen Durchlauf schnell macht, festgehalten, damit sie nicht verloren geht. Ein Pack braucht nichts davon, um die Schlüssel oben zu nutzen.

Der Landbau hat einen eigenen schnellen Weg für die Beleuchtung, und er tritt beiseite, sobald eine Licht-Engine wie Alfheim oder Phosphor installiert ist, und überlässt ihr die Arbeit. So oder so bekommst du am Ende fertiges, vollständig beleuchtetes Land.

Das Spiel weigert sich, einen Chunk zu beleuchten, bevor alle acht ringsum existieren, und während Land gebaut wird, gibt es die vorausliegenden noch nicht – einen Chunk zu beleuchten, während er gebaut wird, klappt also fast nie. Stattdessen werden bei jedem gebauten Chunk die neun um diese Stelle betrachtet, und jeder, dessen eigener Ring jetzt vollständig ist und der noch gehalten wird, wird auf der Stelle beleuchtet.

Jedes Mal, wenn das Spiel beim Ausrechnen von Licht die sechs Seiten eines Blocks ansieht, fragt es nach der Liste der sechs Richtungen, und jede Frage liefert eine frische Kopie derselben sechs zurück. Über den Bau einer Welt sind das zig Millionen Kopien einer Liste, die sich nie ändert, und alle werden sofort weggeworfen. Stattdessen wird die eine Liste benutzt.

Wo der Ring der Chunks gemerkt wird, zählt genauso viel wie das Merken selbst: ihn für jede einzelne Abfrage neu nachzuschlagen, zig Millionen Mal, kostet mehr als manche der Abfragen. Er hängt an der Welt selbst, das ist ein einziger Feldzugriff.

Zu messen, wie lange das Licht braucht, ist selbst langsam genug, um ins Gewicht zu fallen: die Uhr für jede der achtzig Millionen Ausbreitungen zweimal zu fragen kostet mehr als ein Teil der Arbeit, die gemessen wird. Also wird jede Ausbreitung gezählt, aber nur eine von vierundsechzig gestoppt, und die Zeit wird daraus hochgerechnet. Die Zahlen sind exakt, die Zeiten nah dran.

Fast alles Licht, das das Spiel ausrechnet, ist Licht, das es schon kannte. Während ein Chunk gebaut wird, läuft es jede Säule vom Himmel abwärts, setzt volles Tageslicht, bis es auf festen Boden trifft, und darunter gar nichts mehr, und geht dann über jeden offenen Block darunter und fragt, wie hell es dort sein sollte. Unter der Erde und im Fels ist die Antwort immer „keins“, und es war schon „keins“ – von hundert dieser Fragen ändern also knapp zwei überhaupt etwas. Die einzigen, auf die es ankommt, liegen neben einem Höhleneingang oder unter einem Überhang, wo Licht von der Seite hereinkommt.

Also wird die Antwort vor dem Fragen direkt ausgerechnet: was der Block durchlässt und wie viel Tageslicht der hellste der sechs ringsum hält. Das ist genau das, was das Spiel selbst ausgerechnet hätte, und wo es mit dem übereinstimmt, was der Block ohnehin schon hält, gibt es nichts zu tun und die Frage entfällt. Zu dieser Antwort zu kommen kostet eine Handvoll Abfragen aus Chunks, die schon zur Hand sind, gegenüber dem weit längeren Weg, den das Spiel zum selben Ziel nimmt. Lampen und Feuer werden nach wie vor gefragt, und nichts wird am Ende anders beleuchtet.

Licht auszubreiten ist der langsamste Teil des Landbaus, und fast nichts davon kostet das Licht selbst. Jedes Mal, wenn das Spiel ausliest, wie hell ein Block ist, was er ist oder ob er den Himmel sieht, schlägt es den Chunk von vorn nach, und das etwa sechzehnmal für jeden Block, den es betrachtet. Über einen Durchlauf sind das weit über tausend Millionen Nachschläge auf neun Chunks, die sich nie ändern. Vor dem Ausbreiten des Lichts fragt das Spiel außerdem zweimal, ob der Boden um die Stelle herum vollständig da ist – und fragt es für jeden Block erneut.

Also wird der Ring der Chunks um den, der beleuchtet wird, einmal gesucht, wenn der Chunk in die Hand genommen wird, und jede Abfrage während dieses Durchgangs wird daraus bedient. Die Antworten sind dieselben, zu denen das Spiel gekommen wäre, nur ohne jedes Mal nachzufragen. Am Licht selbst ändert sich nichts.

Das heißt auch, dass das Licht nach den Bäumen, dem Erz und den Seen eingesetzt wird statt davor. Sich selbst überlassen beleuchtet das Spiel zuerst den nackten Boden und muss dann das meiste davon noch einmal ausrechnen, während die Ausschmückung dazukommt – doppelt verschwendet, auf Land, in dem noch niemand steht. Der erste Durchgang wird zurückgehalten und nur der spätere gemacht, egal ob Land in großen Mengen gebaut wird oder ein Spieler einfach hineinläuft: Das Spiel versucht es ohnehin jede Runde bei jedem unbeleuchteten Chunk erneut, ein zurückgehaltener Chunk wird also eine Runde später beleuchtet und dabei nur einmal ausgerechnet. Chunks, die der Durchlauf hält, bleiben außerdem von der Gewohnheit des Spiels ausgenommen, das Licht jede Runde bei jedem Chunk erneut zu versuchen, denn der Durchlauf weiß, wann jeder von ihnen so weit ist, und der Versuch kann bis dahin nur scheitern. Ein Chunk, der nie an die Reihe kommt, bleibt dunkel und beleuchtet sich selbst, sobald jemand zu ihm hingeht, und dafür gibt es zwei ganz verschiedene Gründe. Der eine: Er liegt ganz am Rand dessen, was angefordert wurde, sein Ring schließt also Boden ein, den niemand bauen ließ, und kann nie vollständig werden; das ist der äußere Rand des Quadrats, und kein Halten der Welt ändert daran etwas. Der andere: Er ist aus dem Gehaltenen herausgefallen, bevor seine Nachbarn gebaut waren, und mehr gehaltene Chunks beheben das sehr wohl. Die beiden werden getrennt gezählt, damit klar ist, welcher Fall vorliegt.

Eine Zeile sagt, wie oft jede dieser Abkürzungen gescheitert ist und der lange Weg genommen werden musste: ernsthaft nachgeschlagene Chunks, Blöcke, die gefragt wurden, woraus sie bestehen, Blöcke, die fürs Schreiben benannt wurden, und wie oft Quarks Steingenerator das Auslesen der Welt für Boden erspart blieb, der zu weit von der Mitte seines Clusters entfernt liegt, um genutzt zu werden. Gezählt werden nur die Fehlschläge, denn die Erfolge zu zählen würde mehr kosten, als die Erfolge einsparen.

# Steuerung

## Die Steuerungsebene

Alles, was Generierung unterbindet oder verändert, ist in Gruppen zusammengefasst, und jede Gruppe hat einen Schlüssel in der Config-Kategorie `control` mit drei Werten:

| Wert | Was er bedeutet |
| --- | --- |
| `default` | Das Pack entscheidet. Die Config-Werte sind der Rückfall |
| `global` | Die Config gewinnt. Pack-Abschnitte werden ignoriert |
| `off` | Die Gruppe ist ganz abgeschaltet, und kein Pack kann sie einschalten |

Die Gruppen sind `ores`, `biomes`, `generators`, `structures`, `spawning`, `bedrock`, `voidWorld`, `recipes`, `terrain`, `entities`, `chunks` und `commands`.

Einstellungen lösen sich in der Reihenfolge **Biom → Weltvorlage → Config** auf. Der `settings`-Block einer Weltvorlage nutzt dieselben Schlüsselnamen wie die Config, ein Pack setzt sie also genauso, wie du es tun würdest:

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

Steht die Steuerung einer Gruppe auf `default`, gewinnen diese Werte, auf `global` werden sie ignoriert, und auf `off` tut die ganze Gruppe nichts, egal was ein Pack sagt.

## Was jede Gruppe macht

### Erze

`blockOres` hindert jeden Mod und Minecraft daran, Erz zu generieren, außer den Mods in `oreWhitelist`. `oreTypes` nennt die Erztypen, für die das gilt, und `oreTypesAreBlacklist` entscheidet die Richtung: an werden die genannten Typen blockiert, aus generieren nur die genannten Typen. Erreichbar ist nur Generierung, die über Forges Ore-Generation-Event läuft, also Minecraft und die meisten, aber nicht alle Mods. `blockOreDimensions` beschränkt das Blockieren von Erz auf bestimmte Dimensionen – leer heißt jede –, und `blockOreDimensionsAreBlacklist` macht aus dieser Liste die Dimensionen, die in Ruhe gelassen werden. Eine Dimension außerhalb des Geltungsbereichs wird gar nicht angefasst, die Erze eines anderen Mods generieren dort also unbehelligt, während die Oberwelt blockiert bleibt.

### Biome

`blockBiomes` und `biomeWhitelist` arbeiten nach Mod, `biomeNames` mit `biomeNamesAreBlacklist` nach Namen. Blockierte Biome werden auf der fertigen Biomkarte ersetzt, und nur so kommt man an Ozeane, Pilzinseln, Mesa-Varianten, Dschungel, Hügel und Küsten heran: Die werden außerhalb der Listen ausgewählt, die ein Mod bearbeiten kann. Blockier jedes Biom, und die Oberwelt wird von selbst zur Void-Welt. `blockBiomeDimensions` beschränkt das Ganze auf bestimmte Dimensionen – leer heißt jede –, und `blockBiomeDimensionsAreBlacklist` macht aus dieser Liste einen Ausschluss.

### Generatoren

`blockWorldGenerators` hindert andere Mods daran, über ihre eigenen Weltgeneratoren zu generieren – so fügen Mods das hinzu, was Forges Events nie zu sehen bekommen: Schleiminseln, Höhlenkristalle und dergleichen. `generatorWhitelist` behält die genannten Mods, `blockedGenerators` nennt einzelne, und die Pack-Generierung dieses Mods wird nie blockiert. `blockGeneratorDimensions` beschränkt es auf bestimmte Dimensionen, `blockGeneratorDimensionsAreBlacklist` dreht die Liste um.

`generatorTypes` blockiert danach, was ein Generator macht, statt danach, welchem Mod er gehört: `ores`, `structures`, `flora`, `lakes`, `terrain` oder `unknown` für die, auf die nichts gepasst hat. `generatorTypesAreBlacklist` entscheidet die Richtung: an werden die genannten Typen blockiert, aus generieren nur die genannten Typen. Ein Typ blockiert unabhängig davon, was die Whitelist sagt, genau wie `oreTypes` – du kannst also jedem Mod das Erz abgewöhnen und seine Verliese und Bäume in Ruhe lassen.

Der Typ kommt aus dem Klassennamen des Generators, abgeglichen mit einer eingebauten Wortliste pro Typ. Das liest die meisten Mods richtig – `NetherOreGenerator` ist ores, `SlimeIslandGenerator` ist structures –, aber ein Generator, der nach nichts Bestimmtem benannt ist, etwa ProjectReds `SimpleGenHandler` oder Draconic Evolutions `DEWorldGenHandler`, kommt als `unknown` heraus. `generatorTypeMap` korrigiert die von Hand, ein `pattern=typ` pro Zeile, wobei das Muster eine Mod-ID oder ein Teil eines Generator-Klassennamens ist:

```
mrtjpcore=ores
deworldgenhandler=structures
```

Zugeordnete Einträge werden vor den eingebauten Wörtern geprüft, sie korrigieren also auch einen Generator, den die Wörter falsch gelesen haben. Schalte `logBlockedGenerators` ein, und jeder Generator wird beim ersten Blockieren mit dem Typ protokolliert, den er bekommen hat; `/rdplserver generators` zeigt die laufenden Summen nach Mod und Typ.

### Ersetzungen

`blockReplacements` tauscht Blöcke aus Chunks heraus, die es schon gibt, ein `block=block` pro Zeile, mit optionalen Metadaten auf beiden Seiten:

```
bigreactors:oreyellorite=minecraft:stone
mekanism:oreblock:0=minecraft:stone
tconstruct:ore:0=minecraft:netherrack
```

Jeder Chunk wird einmal bearbeitet, beim Laden von der Platte, und in seinen eigenen Daten markiert, damit es nie zweimal passiert. Ein Chunk, der zum ersten Mal generiert wird, wird erst beim nächsten Laden gesäubert und nicht sofort, weil Nachbar-Chunks während seiner Generierung noch in ihn hineinschreiben. Ein Chunk am Rand des erkundeten Landes wird gesäubert, aber nicht markiert, er wird also erneut gesäubert, sobald das Land um ihn herum existiert. `blockReplacementDimensions` und `blockReplacementDimensionsAreBlacklist` wählen das Wo, `blockReplacementMinHeight` und `blockReplacementMaxHeight` das Höhenband, das betrachtet wird, und `blockReplacementKey` ist ein String, den du änderst, damit jeder Chunk noch einmal durchläuft. Es läuft unabhängig davon, ob `retrogen` an ist, denn eine Welt, die aufgeräumt werden muss, ist meist eine, in die du keine neuen Adern legen willst. Es tauscht nur Blöcke: Etwas, das ein Mod als Struktur generiert hat, lässt sich so nicht wieder herausnehmen, weil das ersetzte Gelände nie festgehalten wurde.

### Dörfer

Dörfer nutzen dieselben `structure=wert`-Listen wie jede andere Struktur, unter dem Namen `villages`, `structureSpacing`, `structureMinDistanceFromSpawn`, `structureBiomes` und `structureBiomesAreBlacklist` erreichen sie also alle. Eine `structureBiomes`-Liste, die keine Blacklist ist, fügt außerdem jedes genannte Biom hinzu, das die eigene Liste der Struktur nie enthielt – so lassen sich Dörfer ins Gebirge schicken; nenne sie dafür beim Registry-Namen, denn nur Registry-Namen können hinzufügen. Ihr Abstand hat eine Untergrenze von 9, weil Vanilla 8 davon abzieht. `villagePieces` gehört zur selben Gruppe, ein Schalter deckt also alles darüber ab, wo Dörfer hinkommen und woraus sie gebaut sind, während die Gruppe `villages` nur die Grundstücke abdeckt, die ein Pack hinzufügt.

`villageBlocks` ist wie die übrige Dorfarbeit experimentell und greift nur, solange `terrainAdaptation` an ist. Es ersetzt die Blöcke, aus denen ein Dorf gebaut wird, als `original=ersatz`-Paare: `minecraft:cobblestone=meinpack:ruby_brick`. Es greift, nachdem jeder andere Mod sein Wort hatte, ein Pack setzt sich also immer durch, auch gegen Mods, die Dorfmaterialien je Biom austauschen. Beide Seiten akzeptieren einen einfachen Blocknamen oder einen Namen mit Zuständen. Wege werden getrennt über `villagePathBlock` und seine Geschwister benannt.

`villagePieces` nennt Vanilla-Dorfteile: `house1`, `house2`, `house3`, `house4garden`, `church`, `woodhut`, `hall`, `field1` und `field2`, und `villagePiecesAreBlacklist` entscheidet die Richtung – du kannst also Vanillas Weizenfelder streichen und die Häuser lassen oder nur die Teile auflisten, die du willst. Ein Pack-Grundstück wird über seine eigene Vorlage benannt: entweder mit dem vollen Namen, `meinpack:big_house`, oder einfach `big_house`, oder wahlweise über den Namen des Grundstücks selbst. Ein Pack kann also zehn Grundstücke mitbringen, und eine Weltvorlage lässt eines davon weg, ohne die anderen neun anzurühren. Teile aus anderen Mods ebenso wenig, etwa die Häuser von Tektopia oder die Grundstücke von Recurrent Complex: Eine Whitelist entfernt immer nur Vanillas eigene Teile, wer also die gewünschten Vanilla-Teile auflistet, löscht damit nicht stillschweigend fremde. Um einen Mod-Teil loszuwerden, nimm eine Blacklist und nenne ihn beim Namen, etwa `tekhouse2`.

#### Dorfwege

Alles Folgende ist wie die übrige Dorfarbeit experimentell und greift nur, solange `terrainAdaptation` an ist. Jede dieser Einstellungen ist standardmäßig leer oder null, was Vanillas Wege genau so lässt, wie sie waren.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `villagePathBlock` | Block | leer | Die Wegoberfläche. Leer behält den Block, den das Biom nehmen würde: Sandstein über Sand, gebrannter Ton in der Mesa, Trampelpfad über Erde |
| `villagePathSupportBlock` | Block | leer | Der Block unter der Oberfläche, und die Oberfläche selbst dort, wo der Boden blanker Fels ist. Leer behält Vanilla-Kies |
| `villagePathBridgeBlock` | Block | leer | Womit ein Weg Wasser überquert. Leer behält Vanilla-Bretter |
| `villagePathBridgeBarrierBlock` | Block | leer | Geländer, an beiden Kanten eines Brückendecks aufgestapelt. Leer baut keine |
| `villagePathBridgeBarrierHeight` | Zahl | `1` | Wie viele Blöcke hoch diese Geländer stehen |
| `villagePathBridgeSidewalkBlock` | Block | leer | Deckt den Gehweg dort, wo ein Weg Wasser überquert. Leer führt den normalen Gehwegblock hinüber |
| `villagePathCenterBlock` | Block | leer | Eine Mittellinie den Weg entlang. Leer zeichnet keine |
| `villagePathCenterDash` | Zahl | `0` | Strichelt diese Linie: N Blöcke Linie, dann einer Weg. An Weltkoordinaten verankert, sodass die Striche eines Wegstücks im nächsten weiterlaufen. `0` lässt sie durchgezogen |
| `villagePathLineBlock` | Block | leer | Randlinien zwischen Weg und Gehweg. Leer zeichnet keine |
| `villagePathSidewalkBlock` | Block | leer | Gehwege, auf Weghöhe außerhalb der Randlinien gelegt. Leer legt keine |
| `villagePathSidewalkWidth` | Zahl | `2` | Wie breit jeder Gehweg ist, sobald `villagePathSidewalkBlock` gesetzt ist |
| `villagePathExtraWidth` | Zahl | `0` | Zusätzliche Wegblöcke je Seite über Vanillas 3 hinaus. Verbreitert die Wegteile selbst, sodass Häuser von einer breiten Straße zurücktreten |
| `villagePathMinimumWidth` | Zahl | `0` | Der schmalste Weg, der sich noch lohnt. Ein Stück, das seinen vollen Ausbau nicht unterbringt, fällt auf eine schlichte 3 breite Gasse zurück; unterhalb dieser Breite wird es gar nicht gelegt und das Dorf ordnet sich darum an. `0` lehnt nie ab |
| `villagePathFlatRun` | Zahl | `6` | Wie viele Blöcke ein Weg eine Höhe hält, bevor er stuft. An Weltkoordinaten verankert, damit benachbarte Stücke übereinstimmen. `0` stuft jeden Block, wie Vanillas Hänge es tun |
| `villagePathIntersects` | Liste | keine | Muster, die an Kreuzungen gemalt werden, benannt nach Registrierungsschlüssel aus `pathintersects/` eines Packs. Ein Eintrag malt jede Kreuzung gleich; mehrere werden je Kreuzung nach Gewicht gewählt |

Ein Weg wird von der Mitte nach außen ausgebaut: Mittellinie, dann Weg, dann Randlinien, dann Gehwege. Breiten, die nicht passen, fallen zurück statt überzulaufen, ein schmales Stück verliert also still seinen Gehweg, bevor es seinen Weg verliert.

`villagePathBlock` und seine Geschwister gewinnen über `villageBlocks`. Ein benannter Wegblock wird genommen, wie er ist, während die Zuordnung nur das anfasst, was der Weg sonst selbst gewählt hätte. Lässt man sie leer, entscheidet die Zuordnung, und genau so behält ein Pack die biomgerechte Oberfläche und färbt sie trotzdem um.

### Blast Plaster

Was nach einer Explosion geschieht, aus `blastplaster/*.json`. `default` lässt Packs entscheiden, `global` übergeht Pack-Dateien und legt die Vorgaben dieses Mods über Blast Plasters Config, und `off` gibt Blast Plaster ganz an seine eigene Config zurück.

### Strukturen

Vanilla-Strukturen, nach Namen und pro Dimension abgeschaltet. Die Platzierung steuern vier Listen in der Form `structure=wert`, eine pro Zeile: `structureSpacing` für den Abstand, in dem sie gesät werden, `structureSeparation` dafür, wie nah zwei einander kommen dürfen, `structureMinDistanceFromSpawn` dafür, wie weit draußen sie anfangen, und `structureBiomes` mit `structureBiomesAreBlacklist` dafür, wo sie erlaubt sind.

```
temples=24
monuments=40
mineshafts=200
```

```
temples=minecraft:desert,SANDY
monuments=minecraft:deep_ocean
```

Nicht jede Struktur versteht jede Einstellung. Der Abstand erreicht Tempel, Monumente, Herrenhäuser, Endstädte und Festungen; bei `mineshafts` bedeutet die Zahl einer von so vielen Chunks statt eines Rasters, weil Vanilla sie so platziert. Die Trennung erreicht Monumente, Herrenhäuser, Endstädte und Festungen. Die Biome erreichen jede Struktur außer den Endstädten, weil das Ende in dieser Version ein einziges Biom ist und es nichts auszuwählen gibt. Endstädte suchen sich ihren Platz im Raster trotzdem selbst: Sie sitzen nur auf einer äußeren Insel, deren Oberfläche bis y60 reicht, ein größerer Abstand dünnt sie also aus, kann aber keine über die Leere setzen. Netherfestungen sitzen auf einem festen Raster, das Vanilla nicht offenlegt, sie erreichen also nur die Listen für Biome und Spawnabstand. Dörfer behalten ihre eigenen `villageSpacing`, `villageBiomes` und den Rest.

`structureSpawns` ersetzt die Mobs, die eine Struktur spawnt, ganz gleich, was das Biom ringsum sagt, geschrieben als `structure=namespace:entity:gewicht:mindestens:höchstens`, durch Kommas getrennt:

```
netherbridges=minecraft:blaze:10:2:3,minecraft:wither_skeleton:8:5:5
temples=minecraft:witch:1:1:1
monuments=
```

Nur Tempel, Monumente und Netherfestungen halten in dieser Version so eine Liste; Dörfer setzen ihre Bewohner aus den Bauteilen selbst, und Minen, Festungen und Endstädte nutzen stattdessen Spawner und gesetzte Mobs. Lässt du die Zeile nach dem Gleichheitszeichen leer, wie bei den Monumenten oben, spawnt diese Struktur nichts Eigenes mehr.

`structureSpawners` sagt, was der Mobspawner in einer Vanilla-Struktur spawnt, geschrieben als `structure=namespace:entity`, durch Kommas getrennt für eine zufällige Auswahl pro Spawner:

```
dungeons=minecraft:zombie,minecraft:husk
mineshafts=minecraft:cave_spider
netherbridges=minecraft:wither_skeleton
strongholds=minecraft:silverfish
```

Vier Vanilla-Strukturen setzen einen Spawner: der Verliesraum, der Minengang, der Thron der Netherfestung und der Portalraum der Festung. Jeder wird für sich erreicht, Spawner anderer Mods werden also nie angefasst. Verliese wählen normalerweise aus der Liste, zu der Mods über Forge beitragen, sie hier zu nennen übernimmt also auch diese Auswahl.

Der Abstand entscheidet, wo eine Struktur gesät wird, ihn in einer bestehenden Welt zu ändern lässt das Vorhandene also stehen und setzt neue auf ein anderes Raster.

### Spawnen

Spawnraten und Obergrenzen für Mobs, pro Biom. Das Spawnen feindlicher Mobs wird über `surfaceDayMonsterRate`, `surfaceNightMonsterRate`, `undergroundDayMonsterRate` und `undergroundNightMonsterRate` skaliert, jeweils ein Faktor, bei dem `1.0` Vanilla ist – Spawnen bei Tageslicht an der Oberfläche lässt sich also abschalten, ohne die Höhlen anzurühren. Die Obergrenzen sind `monsterCap`, `creatureCap` für friedliche Tiere, `ambientCap` für Fledermäuse und Ähnliches und `waterCreatureCap` für Tintenfische; Vanillas Werte sind 70, 10, 15 und 5, und `-1` lässt eine davon unangetastet.

### Strukturen aufsetzen

`structureAdaptation` entscheidet, an welche Strukturen sich das Gelände anpasst und wie, als `structure=modus`-Einträge, `"mansions=bury"`, `"monuments=none"`, für Dörfer, Festungen, Minen, Monumente und Herrenhäuser, mit den fünf Modi, die moderne Versionen nutzen: `none`, `bury`, `beard_thin`, `beard_box` und `encapsulate`. Dörfer sind `beard_thin`, wenn nichts anderes gesetzt ist, und alles andere ist `none`, solange es nicht genannt wird – genau das, was moderne Versionen für sich selbst wählen. Tempel lassen sich noch nicht nennen, weil sie sich erst beim Bauen selbst platzieren, es gibt also rechtzeitig nichts, woran das Gelände sich anpassen könnte.

### Dörfer aufsetzen

**Diese Einstellung ist experimentell und noch in Bewegung.** Die Nutzung erfolgt auf eigene Gefahr. Sie formt das Gelände schon beim Erzeugen der Welt um, alles, was sie legt, ist in diesem Spielstand also endgültig, und ein Fehler darin kann ein halb abgetragenes Dorf oder eine Straße auf einem Damm hinterlassen. Solange daran gearbeitet wird, ändert sich ihr Verhalten von Build zu Build. Zwei Welten aus demselben Seed, aber mit unterschiedlichen Mod-Versionen erzeugt, sehen deshalb nicht gleich aus, und ein Dorf aus einem älteren Build wird von einem neueren weder erneut besucht noch ausgebessert. Wenn dir eine Welt wichtig ist, lass die Einstellung aus oder leg eine Sicherung an und rechne damit, dass die Dörfer darin genau den Stand zeigen, den der Mod an dem Tag hatte, an dem diese Chunks entstanden sind.

`terrainAdaptation` arbeitet um, wie Dörfer ihren Boden wählen und darauf sitzen, dem Geist nach übernommen davon, wie moderne Versionen ihre Strukturen aufsetzen, und dann weitergetrieben. Ein Dorf wird nur auf einem Chunk gegründet, dessen Boden um höchstens zehn Blöcke schwankt, und nie näher als acht Chunks an einem anderen Dorf; Regionen, die keinen solchen Chunk hergeben, gründen gar nichts. Der Brunnen setzt sich auf den tiefsten Boden, den seine eigene Grundfläche berührt, und das ganze Dorf verschiebt sich mit ihm, sodass sich alles Übrige von dort aus einrichtet. Wege werden beim Legen abgezogen: Die Oberfläche folgt über die Wegbreite dem tiefsten natürlichen Boden, Buckel werden abgetragen, Senken gefüllt, das Gefälle überschreitet nie einen Block pro Schritt, und kurze Schluchten werden mit Brettern überbrückt. Die Wegoberfläche richtet sich nach dem Boden, über den sie führt: Trampelpfade auf Erde, Sandstein auf Sand, gebrannter Ton in der Mesa, Kies auf Stein und auf Kies, Bretter über Wasser. Ein Wüstendorf bekommt so Sandsteinstraßen statt eines Feldwegs, und Wege verschwinden nicht mehr dort, wo der Boden kein Gras ist. Wo zwei Wege sich kreuzen, treffen sie sich auf der niedrigeren der beiden Höhen, denn nur eine Höhe, die beide erreichen können, lässt keine Stufe zwischen ihnen. Jedes Gebäude sitzt einen Block über dem Weg, an dem es steht, abgelesen vom gelegten Weg oder vorhergesagt aus der Höhe, auf die der Weg den Boden abziehen wird, wenn er noch nicht gebaut ist – so ruhen seine Türstufen auf der Wegoberfläche und seine Tür sitzt dahinter. Ein Gebäude, unter dem irgendwo mehr als zwei Blöcke aufgeschütteter Boden nötig wären, wird dort nicht gebaut: Es rutscht bis zu zwölf Blöcke am Weg entlang auf der Suche nach dem flachsten Sitz und fällt ganz weg, wenn es keinen findet – Dörfer auf zerklüftetem Boden fallen so lockerer aus, statt auf Sockeln zu stehen. Der Ring um ein Gebäude wird bergab aufgeschüttet und bergauf abgetragen, einen Block flacher noch einen Ring weiter außen. Felder behalten Vanillas eigene Bodenhöhe. Laternenpfähle stehen auf der Höhe des Weges, den sie beleuchten, statt auf der Schulter daneben, mit aufgefülltem Boden darunter, wo der Weg über dem Randstreifen liegt, und Vanillas eigene Fackelpfosten bleiben aus dem Grundriss heraus, weil diese sie ersetzen. Unter jedem Gebäude wird bis zur nächsten tragenden Fläche mit demselben Material aufgefüllt, auf dem es steht, Wände und Türöffnungen werden aus Hängen herausgeschnitten, Erde wird von Dächern gehoben, und jeder Baum, der in einer Struktur steht, wird ganz gefällt, seine Blätter gehen mit seinem Holz, während jedes Blatt, das noch zu einem stehenden Ast gehört, in Ruhe gelassen wird. Herrenhäuser und die verstreuten Bauwerke (Tempel, Hütten, Iglus) müssen dieselbe Anforderung an flachen Boden erfüllen, bevor sie gesetzt werden dürfen. Es formt das Gelände selbst beim Entstehen um, eine Welt, die damit generiert wurde, unterscheidet sich also von einer, die ohne generiert wurde – dieselbe Warnung, die moderne Versionen mitbringen –, und es ist aus, solange ein Pack oder die Config nicht darum bittet.

### Grundgestein

`flatBedrock` ersetzt die zerklüftete Schicht durch flache, pro Dimension und pro Biom, mit einem Füllblock deiner Wahl. `flatBedrockRetrogen` macht das auch mit Chunks, die es schon gibt. Es lässt sich nicht rückgängig machen, das ursprüngliche Muster wird nirgends festgehalten. `bedrockLayers` legt fest, wie viele Schichten bleiben, `flatBedrockRoof` macht auch die Decke, wo eine Dimension eine hat, und `flatBedrockFiller` ist das, was das weggenommene Grundgestein ersetzt, leer gelassen wird pro Dimension gewählt, und `flatBedrockFillers` nennt stattdessen einen pro Dimension. Welche Dimensionen und Biome es erreicht, bestimmen `flatBedrockDimensions`, `flatBedrockBiomes` und `flatBedrockBiomeTypes`, wobei `flatBedrockDimensionsAreBlacklist` und `flatBedrockBiomesAreBlacklist` diese Listen zu Ausschlüssen machen.

### Langsameres Ticken in der Ferne

Entities kosten einen Server mehr als alles andere, und die meisten sind weit von jedem Spieler entfernt. `slowDistantEntities` gibt einem Chunk ohne Spieler innerhalb von `slowDistance` Blöcken nur einen von `slowRate` Ticks, was darin ist, bewegt sich also weiterhin, schwebt, brennt und despawnt, nur langsamer. Nichts bleibt je ungetickt.

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `slowDistantEntities` | nein | boolean | `true` | Ob überhaupt etwas verlangsamt wird |
| `slowedKinds` | nein | Liste aus `items`, `experience`, `projectiles` | `{items, experience}` | Welche Arten weniger Ticks bekommen. Alles, was für sich selbst denkt, wird stattdessen immer verlangsamt und steht hier nicht. Maschinen werden nie verlangsamt |
| `slowDistance` | nein | int, ab 64 | `192` | Wie weit vom nächsten Spieler entfernt, bevor ein Chunk verlangsamt wird |
| `slowRate` | nein | int, 1 bis 20 | `4` | Einer von so vielen Ticks geht an einen verlangsamten Chunk. `1` verlangsamt nichts |
| `neverSlowed` | nein | Liste von Entity-Namen | keine | Werden in Ruhe gelassen, egal wie weit weg |
| `slowRecheck` | nein | int, 1 bis 100 | `20` | Wie oft der Abstand zum nächsten Spieler neu bestimmt wird |

Alles, was für sich selbst denkt – jeder Mob, jedes Tier, jeder Dorfbewohner und jeder Golem, aus welchem Mod auch immer – wird anders behandelt als der Rest und steht in `slowedKinds` überhaupt nicht. Es bekommt nie weniger Ticks, weil ein Spieler ihm beim Laufen zusehen kann. Stattdessen tickt es weiter jeden Tick und denkt seltener: Der Teil seines Verstands, der entscheidet, was als Nächstes zu tun ist, und der zugleich der teure Teil ist, wird nur einmal pro `slowRate` Ticks gefragt statt jeden dritten Tick. Es bewegt sich, fällt, ertrinkt, brennt und findet seinen Weg genau wie sonst und ändert nur seltener seine Meinung, solange niemand in der Nähe ist. Zu sehen ist davon nichts, kein Stocken und kein Aufholen, und was ein Spieler antrifft, ist wieder ganz es selbst, bevor es in Sicht kommt. Weil es nicht auffallen kann, ist es auch keine Wahl: Es passiert überall dort, wo das Verlangsamen überhaupt an ist.

Was weniger Ticks bekommt, altert trotzdem im normalen Tempo. Ein fallengelassenes Item und eine Erfahrungskugel tragen jeweils ihren eigenen Zähler, der entscheidet, wann sie verschwinden, und an einem Tick, den ein verlangsamter Chunk nicht bekommt, wird dieser Zähler trotzdem weitergestellt. Ein Item liegt also weiterhin fünf Minuten am Boden und nicht zwanzig. Verringert wird nur, was es pro Tick tut, nie, wie lange es besteht.

Ein Chunk, den etwas absichtlich geladen hält, wird nie verlangsamt, egal wie weit weg er ist. Das sind die Chunks, die ein Chunkloader hält, und der ganze Sinn des Haltens ist, dass darin weiterläuft, was darin ist – eine Farm, die arbeitet, während ihr Besitzer woanders ist, läuft also in dem Tempo, für das sie gebaut wurde. Die Chunks um den Spawn einer Welt gehören nicht dazu, weil niemand um sie gebeten hat, sie werden also verlangsamt wie überall sonst.

Ein ganzer Chunk wird gemeinsam verlangsamt oder nicht, damit sich das, was darin ist, weiter richtig verhält: Items landen auf demselben Haufen, ein Mob folgt weiterhin dem neben ihm. Jeder Spieler zählt für sich, wer allein unterwegs ist, hat also überall ruhigen Raum um sich. Was geritten, benannt, gezähmt, angeleint, leuchtend, vom Despawnen ausgenommen, unter einem Effekt oder schon hinter einem Spieler her ist, wird in Ruhe gelassen, egal wie weit weg, und Maschinen ebenso. Es gilt für jede Welt, auch für die, die ein Mod hinzufügt.

### Chunk-Arbeit beobachten

Mit eingeschaltetem `worldgenDebug` sagt alle hundert Runden eine Zeile, wofür die Welt ihre Chunk-Arbeit ausgibt: wie viele Chunks frisch gebaut wurden, wie viele nach dem Loslassen zurückgeholt werden mussten, wie viele davon von der Platte kamen statt aus der Warteschlange, die noch aufs Schreiben wartet, wie viele Region-Dateien geöffnet und wie oft sie alle auf einmal geschlossen wurden, und die höchste Zahl gehaltener Chunks und ausstehender Schreibvorgänge zu irgendeinem Zeitpunkt. Sie ist dafür geschrieben, herauszufinden, ob das Erzeugen von Land Zeit in der Generierung oder im Zurückholen desselben Bodens kostet – es lohnt sich also, sie vor einer großen Vorgenerierung einzuschalten und danach wieder aus.

Drei weitere Zeilen folgen darauf: eine fürs Zurückschreiben der Chunks in den Speicher, eine fürs Beleuchten und eine, die den Bau des Landes selbst aufteilt in den Boden, die Ausschmückung, die das Spiel darauflegt, und die Ausschmückung, die jeder Mod darauflegt, die schlimmsten fünf namentlich. Eine langsame Welt lässt sich dann als vier getrennte Kosten lesen statt als eine, und der verantwortliche Mod ist benannt statt geraten.

### Land im Voraus bauen

Groß genug für einen eigenen Abschnitt, siehe [Vorgenerierung](#vorgenerierung).

### Blöcke, die auf ihren Zug warten

Wasser, das sich ausbreitet, Lava, die abkühlt, und Feldfrüchte, die wachsen, sind alle Blöcke, die eine Weile warten, bevor sie etwas tun, und das Spiel hält jeden einzelnen davon in einem einzigen Haufen. Jedes Mal, wenn ein Chunk geschrieben wird, läuft es diesen ganzen Haufen von vorn bis hinten ab und sucht die wenigen, die zu ihm gehören – je mehr davon eine Welt hat, desto langsamer wird also jeder Schreibvorgang, ob der geschriebene Chunk überhaupt welche hat oder nicht. Sie werden jetzt danach sortiert, in welchem Chunk sie sitzen, und die Sortierung wird verworfen und neu gemacht, sobald sich der Haufen ändert oder die Runde weitergeht, sodass ein Schreibvorgang nur die Handvoll um sich herum ansieht.

### Wachsender Platz für die Blöcke in einem Chunk

Ein Chunk wird in Scheiben gehalten, und jede Scheibe hält eine Liste der Blockarten in ihr, anfangs mit Platz für sechzehn. Über sechzehn hinaus heißt: eine größere Liste anlegen und jeden der viertausend Blöcke der Scheibe hinüberkopieren, und dann noch einmal bei zweiunddreißig und noch einmal bei vierundsechzig. Boden mit ein paar Sorten Stein und Erz darin überschreitet alle diese Grenzen, es wird also viermal gemacht, für ein bisschen Platz. Jetzt wird beim ersten Überlaufen direkt auf die größte dieser Größen gegangen, was ein Kopieren statt vier ist und ein paar Kilobyte pro Scheibe kostet, die ohnehin binnen Momenten gebraucht werden.

### Chunks fürs Schreiben vorbereiten

Bevor ein Chunk geschrieben werden kann, wird er in die Form gebracht, die auf die Platte geht, und dabei wird jeder seiner Blöcke durchlaufen und jeder einzeln in einer Tabelle nach Namen nachgeschlagen. Boden kommt in langen Läufen desselben Materials, dasselbe Nachschlagen passiert also tausendfach für denselben Stein – und die Antwort des letzten wird jetzt einfach behalten und wiederverwendet, wenn der nächste Block derselbe ist. Das lässt sich nicht abschalten, weil es nichts abzuwägen gibt: Die Antwort ist so oder so dieselbe.

### Chunks hinausschreiben

Das Spiel schreibt fertige Chunks auf einem eigenen Thread, einen nach dem anderen, und ruht nach jedem eine hundertstel Sekunde. Das hält es bei etwa hundert Chunks pro Sekunde, egal wie schnell die Platte ist, was beim Spielen reichlich und beim Bau von Land in großen Mengen bei Weitem nicht genug ist – die ungeschriebenen Chunks stapeln sich stattdessen im Speicher. `hurryWritesAbove` sagt, wie viele warten dürfen, bevor es aufhört zu ruhen und einfach so schnell schreibt, wie es kann. `100` ist der Standard und trifft den Punkt, an dem das Spiel selbst die Generierung zu bremsen beginnt; `0` lässt es immer ruhen, so wie das Spiel es tut. Solange die Zahl der Wartenden klein ist, ändert sich nichts, und das ist jeder gewöhnliche Moment des Spielens.

Jedes Mal, wenn das Aufräumen läuft, wird dafür eine Zeile geschrieben, während es passiert: welcher Sammler lief, wie lange er brauchte, was vorher und nachher gehalten wurde und wie viel Platz das Spiel zu diesem Zeitpunkt hatte. Ändert sich dieser Platz, wird das gesagt, denn der wachsende Platz ist selbst die Ursache der längsten dieser Pausen: Ein Spiel, das mit weniger Platz startet, als es am Ende braucht, hält an, um ihn zu vergrößern, immer wieder, in Momenten, die nichts mit dem zu tun haben, was es gerade tut. Es mit so viel Platz zu starten, wie es haben darf, vermeidet das vollständig.

Eine letzte Zeile sagt, wie viel Arbeitsabfall seit dem letzten Blick weggeworfen wurde, wie lange das Aufräumen davon dauerte und wie viele Durchgänge das waren, und wie viel des erlaubten Platzes das Spiel gerade hält. Land zu bauen wirft naturgemäß eine Menge weg, weil jeder Chunk vor dem Schreiben in frische Arrays umgewandelt wird, und dieses Aufräumen passiert zwischen den Runden statt während ihnen, es zeigt sich also als Hänger und nicht als Zeit in einer der Zahlen oben.

### Spawn-Chunks

Das Spiel hält die Chunks um den Spawnpunkt einer Welt geladen, ob jemand da ist oder nicht, damit Mods irgendwo etwas haben, das immer tickt. Das sind 128 Blöcke in jede Richtung, etwa 289 Chunks, und im Spiel lässt sich das nicht einstellen. `spawnChunkRadius` setzt diese Entfernung. `128` ist das, was das Spiel macht, und der Standard, eine kleinere Zahl hält einen kleineren Anker, und `0` hält gar keine, der Spawnbereich entlädt also wie überall sonst. `spawnChunkRadii` setzt einen Radius für einzelne Dimensionen, geschrieben als `dimension=blöcke`, einer pro Zeile, und überschreibt `spawnChunkRadius` für die genannten Dimensionen.

Nur eine Dimension, die dafür registriert wurde, ihren Spawn zu halten, hält einen, und das ist im Spiel selbst allein die Oberwelt – der Nether und das Ende hielten nie einen, das dafür zu setzen ändert also nichts. Eine Dimension, die ein Mod hinzufügt, hält nur dann einen, wenn dieser Mod darum gebeten hat, und ein Mod, der das getan hat, schleppt oft weitere 289 Chunks mit, die ein Pack nie wollte. Ob eine Welt überhaupt geladen bleibt, ist eine andere Sache, die das hier nicht berührt: Eine Dimension, die ein Mod als dauerhaft geladen markiert hat, bleibt auch bei `0` geladen, sie hält nur keine Chunks mehr. Die meisten Mods, die den Spawn als Anker nutzen, wollen dort irgendetwas haben und keine 289 Chunks davon, eine kleine Zahl hält sie also meist am Laufen, während eine `0` das nicht tut.

### Void-Welt

`voidWorld` generiert eine leere Welt mit einer Plattform am Spawnpunkt und unterbindet Mobs, Tiere, Strukturen und alles, was ein Mod dort sonst generieren würde. Block, Größe und Höhe der Plattform sind `voidPlatformBlock`, `voidPlatformSize` und `voidPlatformHeight`; die Größe wird auf eine ungerade Blockzahl abgerundet, damit die Plattform mittig auf dem Spawn sitzt. `voidWorldDimensions` wählt, welche Welten geleert werden, standardmäßig allein die Oberwelt, und `voidWorldDimensionsAreBlacklist` macht aus dieser Liste die, die in Ruhe gelassen werden. Der Nether und das Ende werden genauso geleert wie die Oberwelt, ob es die sind, die diese Version baut, oder solche, die ein Mod an ihre Stelle gesetzt hat. Nur die Oberwelt bekommt eine Plattform, einen Weg in einen geleerten Nether oder ein geleertes Ende liefert ein Pack also selbst. Ein geleertes Ende hat außerdem keinen Drachen, keine Kristalle und keinen Grundgestein-Brunnen, weil der Kampf, der sie baut, nie beginnt.

### Der Drache

`dragonFight` gehört zur Gruppe `structures` und entscheidet, ob das Ganze überhaupt stattfindet: der Drache, seine Leiste, die Kristalle, der Brunnen, auf dem er steht, und das Wiederbeleben, das ein Spieler mit Enderkristallen starten würde. Ein geleertes Ende lässt ihn weg, solange ein Pack nicht darum bittet, und ein gewöhnliches Ende hat ihn, solange ein Pack nicht etwas anderes sagt – `dragonFight` lohnt sich also in beide Richtungen.

### Gelände

`worldType` entscheidet, welche Art Welt eine neue Welt ist, ganz gleich, was im Bildschirm beim Erstellen ausgewählt wurde: `default`, `largebiomes`, `amplified`, `customized` oder ein Typ, den ein Mod hinzufügt, etwa `biomesop` oder `realistic`. Ein Pack, das um einen Welttyp herum gebaut ist, nennt ihn hier, und jede neue Welt wird so gebaut. Leer, der Standard, überlässt die Wahl dem, der die Welt erstellt. Eine Welt, die es schon gibt, behält den Typ, mit dem sie gebaut wurde, und ein Name, den niemand bereitstellt, wird protokolliert und ignoriert. `worldTypeExceptions` nennt die Auswahlen, die stehen bleiben, zunächst Superflach und die Debug-Welt, denn ein Pack, das einen Welttyp will, will jemandem beim Testen selten Superflach wegnehmen; und wer eine Welt erstellt, erfährt im Chat, sobald er drin ist, dass das Pack den Typ gewählt hat. Diese Meldung entscheidet die Config-Datei über `tellWorldType`, nicht ein Pack, wer spielt, kann sie also für sich abschalten, und kein Pack kann sie wieder einschalten. Einstellungen, mit denen die Welt erstellt wurde, fallen beim Wechsel des Typs weg, weil sie für den gewählten Typ geschrieben waren.

`worldSeed` entscheidet, mit welchem Seed jede neue Welt gebaut wird, ganz gleich, was im Erstellungsbildschirm eingetippt wurde. Er wird genauso geschrieben, wie er dort getippt würde: Eine Zahl wird genommen, wie sie ist, alles andere wird so in eine Zahl verwandelt, wie das Spiel ein Wort in eine verwandelt – `Hollow Ridge` und `-4172144997902289642` sind also beide erlaubt und ergeben beide immer dieselbe Welt. Leer, der Standard, überlässt die Wahl dem, der die Welt erstellt. Eine Welt, die es schon gibt, behält den Seed, mit dem sie gebaut wurde, das hier entscheidet also immer nur, was eine neue bekommt. Ein Pack, das um eine Karte herum gebaut ist, nennt hier ihren Seed, und jede Welt, die mit diesem Pack erstellt wird, ist diese Karte.

`generatorOptions` formt die Oberwelt selbst – Meereshöhe, Lavaozeane und jedes Geländerauschen –, im selben Format, das der Welttyp `customized` schreibt. Es wird auf eine Welt beim Erstellen angewendet und nie danach, eine Welt, die es schon gibt, bleibt also genau, wie sie war. Eine Welt, die schon eigene Optionen trägt, behält sie, und das Log nennt den String, der genutzt wurde.

Ein Welttyp, der eigene Einstellungen trägt und nie in die der Welt sieht, wie es Quarks realistischer tut, bekommt die Einstellungen des Packs in seine eigenen eingemischt, die Form, für die er gebaut wurde, bleibt also erhalten, solange ein Pack nicht um etwas anderes bittet.

`terrainWorldTypes` nennt die Welttypen, an die die Einstellungen überhaupt weitergegeben werden – `default`, `customized`, `biomesop`, `realistic` und so weiter –, und `terrainWorldTypesAreBlacklist` macht daraus die Liste derer, die in Ruhe gelassen werden. Leer, der Standard, heißt jeder Welttyp. Ein Pack, das die gewöhnliche Welt formt, aber den Welttyp eines Mods genau so lassen will, wie dieser Mod ihn gemacht hat, nennt ihn hier und ist fertig: Es wird nichts eingemischt, nichts weitergereicht, und der eigene Anpassungsbildschirm des Mods bleibt offen. Die Namen werden mit dem Welttyp abgeglichen, mit dem eine Welt gebaut wurde, einen zu nennen, den es hier nicht gibt, passt also einfach nie und kostet nichts.

Alles Folgende über Biomes O' Plenty passiert nur, wenn dieser Mod installiert ist, denn die Arbeit erledigt Kompatibilitätscode, der nur bei seiner Anwesenheit geladen wird. Ohne ihn gibt es keinen Welttyp `biomesop` zur Auswahl, und ein Pack, das einen nennt, bleibt bei dem Welttyp, mit dem die Welt tatsächlich gebaut wurde.

Auf einer Biomes-O'-Plenty-Welt werden dieselben Einstellungen in die Wörter übersetzt, die dieser Mod liest, ein Pack braucht also keine zweite Fassung davon. `biomeSize` wird zu einer seiner fünf Größen, die Einstellungen für Rauschen und Skalierung gehen unverändert durch, und was er nie liest, bleibt weg, mit einer Zeile im Log, die es sagt. Dieser Mod liest weit weniger als der Welttyp `customized` und liest Meereshöhe, Höhlen, Seen und die Strukturschalter überhaupt nie aus seinen Einstellungen, die werden ihm also direkt übergeben, und ein Pack setzt sie genauso wie für jede andere Welt.

Zwei Dinge entscheidet er selbst. Flüsse kommen aus seinen eigenen Schichten und haben keine Einstellung, `riverSize` bedeutet dort also nichts. Und wo Ozeane, Gebirge und Regionen tatsächlich liegen, sind ebenfalls seine Schichten, erreichbar nur über `landScheme`, `tempScheme`, `rainScheme` und `biomeSize` – ein Pack formt diese Welt also in den Begriffen dieses Mods statt in denen des Welttyps `customized`. Eine Welt aus einem einzigen Biom kann ein Pack trotzdem bauen: Blockier jedes Biom und nenne das gewünschte als `default` der Vorlage, was auf seinem Welttyp genauso funktioniert wie auf jedem anderen.

Alles andere, was ein Pack tut – Biome und Erze blockieren, Blöcke ersetzen, flaches Grundgestein, Strukturplatzierung, die eigene Weltgenerierung –, lief nie über diesen String und funktioniert auf jedem Welttyp gleich.

### Protokollierung

`logBlockedOres`, `logBlockedBiomes`, `logBlockedRecipes` und `logBlockReplacements` protokollieren jeweils das erste Mal, dass etwas abgewiesen wird, du siehst also, was eine Sperrregel tatsächlich erwischt hat, statt es aus dem zu erraten, was fehlt. Sie sind das Erste, was man einschaltet, wenn eine Regel nichts oder zu viel zu tun scheint.

### Rezepte

`blockRecipes` und `blockFurnaceRecipes` entfernen alles außer den Mods in ihren Whitelists. Nichts ist standardmäßig ausgenommen, trag also den Namespace deines eigenen Packs ein, um seine Rezepte zu behalten. Ergänzungen von CraftTweaker und GroovyScript überleben immer, egal was die Whitelist sagt. Die Whitelists sind `recipeWhitelist` und `furnaceWhitelist`; `blockedRecipeMods` und `blockedFurnaceMods` gehen in die andere Richtung und entfernen die Rezepte eines genannten Mods, egal was die Whitelist sagt. `recipeMatch` entscheidet, woher die Mod-ID gelesen wird, wenn Handwerksrezepte blockiert werden: aus dem Namen des Rezepts oder aus dem, was es herstellt.

## Universal Tweaks

Universal Tweaks ändert mehrere derselben Vanilla-Blöcke und -Verhalten wie dieser Mod. Wo sie sich überschneiden, tritt dieser Mod zurück und überlässt Universal Tweaks den Vortritt, statt dass beide dieselbe Methode bearbeiten und das Ergebnis dem überlassen, was zuletzt geladen wurde. Jedes Mal, wenn das passiert, sagt er es im Log und nennt, was weggelassen wurde.

| Was sich überschneidet | Wann dieser Mod zurücktritt |
| --- | --- |
| `promptLeafDecay` | Universal Tweaks hat `Fast Leaf Decay` an |
| `lenientPaths` | Universal Tweaks hat `Lenient Paths` an |
| `cactusMaxHeight` | Universal Tweaks ist installiert |
| `caneMaxHeight` | Universal Tweaks ist installiert |
| Rückweg durchs Netherportal | Universal Tweaks ist installiert |

Die ersten beiden lesen die eigenen Schalter von Universal Tweaks aus `config/Universal Tweaks - Tweaks.cfg`, einen dort abzuschalten gibt diese Aufgabe also hierher zurück. Das Höhenpaar hat keinen solchen Schalter zum Auslesen, nur `Cactus Size` und `Sugar Cane Size`, dieser Mod tritt also zurück, sobald Universal Tweaks überhaupt da ist, und du setzt die Höhe dort.

**Der Rückweg durchs Netherportal** ist der eine Punkt ohne Option auf dieser Seite. Ohne ihn landest du beim Zurückgehen durch ein Netherportal an irgendeinem Portal, das Vanillas Suche gerade findet, und nach genug Reiserei ist das oft nicht das, aus dem du kamst. Dieser Mod merkt sich, wo du den Nether betreten hast, und setzt dich dorthin zurück. Universal Tweaks hat dafür eine eigene Behandlung, das hier wird also komplett übersprungen, wenn es installiert ist.

**Nichts davon berührt ein Pack.** Alles oben betrifft Minecrafts eigene Kakteen, Zuckerrohre, Blätter, Pfade und Portale. Blöcke, die dein Pack definiert, bringen ihr eigenes Verhalten mit, und Pack-Portale unter `portals/*.json` sind ein eigenes System, das Universal Tweaks nie zu sehen bekommt.

## Mo' Villages

Mo' Villages setzt Dörfer in Biome, in die das Spiel sie nie setzen würde, und baut sie aus anderen Blöcken. Über beides hat auch dieser Mod eine Meinung, und anders als bei Universal Tweaks behält er hier das letzte Wort.

| Was sich überschneidet | Was passiert |
| --- | --- |
| `structureSpacing` für Dörfer | Mo' Villages setzt seinen eigenen Abstand aus `villageDistance`, nachdem dieser Mod schon gefragt hat. Hat ein Pack einen Abstand genannt, trägt dieser Mod seine Zahl wieder ein und sagt es einmal im Log |
| `villageBlocks` | Mo' Villages tauscht Dorfmaterialien je Biom aus und erklärt den Tausch für endgültig. Die Zuordnung eines Packs greift danach, also gewinnt das Pack |
| `structureBiomes` für Dörfer | Mo' Villages fügt seine Biome der spieleigenen Liste hinzu. Eine Positivliste im Pack entscheidet weiterhin, was übrig bleibt |

Hier muss nichts eingeschaltet werden. Nennt ein Pack weder Abstand noch Blockzuordnung, darf Mo' Villages ungestört machen, was es will.

Zwei Dinge sind gut zu wissen, wenn beide installiert sind. Mo' Villages setzt auch `minTownSeparation`, was in 1.12 überhaupt nichts bewirkt: Das Feld wird einmal geschrieben und nie gelesen, weder vom Spiel noch von diesem Mod. Und die Blöcke eines Dorfes legt Mo' Villages je Biom fest, bevor `villageBlocks` läuft. Wer sowohl den ursprünglichen Block als auch den von Mo' Villages eingetauschten zuordnet, erwischt ein Dorf in jedem Fall, also `minecraft:cobblestone=...` und `minecraft:brick_block=...` zusammen.

## CoFH World

Mods, die CoFH World voraussetzen, laden auch ohne ihn, die Voraussetzung wird automatisch entfernt – außer bei Mods, die seine API tatsächlich aufrufen und abstürzen würden.

Ihre eigene Generierung findet dann nicht statt, denn CoFH World ist das, was ihre `assets/<modid>/world/*.json` liest. Von einem Pack wird erwartet, dass es das abdeckt.

Andernfalls liest `readCofhWorldFiles` diese Dateien direkt aus den Mod-Jars und generiert sie über diesen Mod. Es ist standardmäßig aus, und es tritt zurück, wenn das echte CoFH World installiert ist, das dann ganz normal generiert. Jeder CoFH-Generator und jede Verteilung, die überhaupt etwas hervorbringt, wird umgesetzt und auf die Formen und Verteilungen oben abgebildet. Die Formen sind die eigene Geometrie dieses Mods, ein See oder eine Spitze sieht also nicht identisch aus. Gewichtete Strukturlisten, Dreh- und Spiegeltabellen, Listen ignorierter Blöcke und die Verjüngung von Stalagmiten kommen alle mit. Die Verjüngung wird über die Form nachgebildet und nicht über die Formel, der Umriss einer Spitze ist also nah dran, aber nicht identisch.

Die Dateien in ein Pack zu übersetzen ist der unterstützte Weg und die einzige Möglichkeit, zu ändern, was sie generieren.

## Lost Cities

Lost Cities ersetzt den Generator der Oberwelt durch einen eigenen, alles, was in den gewöhnlichen Generator eingebaut ist, würde auf seinen Welten also nicht mehr laufen. Kompatibilität, die nur bei installiertem Lost Cities geladen wird, trägt drei Dinge hinüber:

- `generatorOptions` formt das Land zwischen und unter den Städten. Lost Cities liest nur die Einstellungen für das Rauschen, Bodenhöhe, Wasserstand, Höhlen, Seen und die Strukturschalter kommen also aus seinen eigenen Profilen, und `seaLevel` tut auf seinen Welten nichts; die Zusammenfassung im Log sagt das auch. `terrainWorldTypes` begrenzt es wie jeden anderen Typ, unter dem Namen `lostcities`.
- Eine Void-Welt funktioniert, die eingeschlossen, die eine vollständig blockierte Biomliste mit sich bringt. Städte und Land sind beide weg, und Plattform und Spawn verhalten sich wie überall.
- Der `stoneBlock` eines Pack-Bioms ersetzt den Stein darunter, auf jedem Landschaftstyp von Lost Cities: normal, schwebend, Weltraum und Kaverne.

Die Städte selbst sind nicht Sache dieses Mods. Wie groß und wie häufig sie sind, woraus die Gebäude bestehen, Boden- und Wasserhöhe, all das liegt in Lost Cities' eigenen Profildateien unter `config/lostcities`, und sein Gebäude-JSON läuft über seine eigene `assets`-Einstellung am selben Ort. Ein Pack, das eine Lost-Cities-Welt ausliefert, legt diese Dateien daneben, genauso wie es die Config jedes anderen Mods beilegt.

`worldType` auf `lostcities` macht jede neue Welt zu einer Lost-Cities-Welt, genauso wie es eine zu `biomesop` oder `realistic` macht. Ein erzwungener Typ verwirft die Einstellungen, die die Welt getragen hätte, die Welt landet also auf dem Standardprofil von Lost Cities, und welches das ist, benennt `defaultProfile` in `config/lostcities/general.cfg`. Umgekehrt nimmt ein Pack, das einen anderen Typ erzwingt, einem Spieler Lost Cities weg, der es gewählt hat, ein Pack, das diese Wahl offenlassen will, trägt `lostcities` also in `worldTypeExceptions` ein.

Alles andere lief nie durch den Generator und funktioniert wie überall: Pack-Worldgen, das Blockieren von Erzen und Biomen, Strukturabstände und ihre Spawner, flacher Bedrock, Retrogen, Vorgenerierung, und seine zwei Truhen-Loot-Tabellen lassen sich überschreiben und ergänzen wie alle anderen.

## Blast Plaster Integration

Explosionen waren das Letzte, was ein Pack nicht beschreiben konnte. Alles andere am Aussehen einer Welt ist eine Datei in diesem Ordner, aber was ein Creeper hinterlässt, gab der Mod vor, dem es gerade gehörte. Die schwierige Hälfte davon hatte Blast Plaster längst gelöst: einen Krater Block für Block wieder zusammensetzen und dabei wissen, wo ein Baum aufhört und der nächste anfängt. Statt das ein zweites Mal zu schreiben, setzt dieser Mod darauf auf und liefert es als Abhängigkeit mit.

Was dabei herausspringt, ist eine Kontrolle, die es allein nicht hat. Blast Plaster liest eine Config für das ganze Spiel; aus einem Pack gesteuert antwortet es pro Dimension. Die Oberwelt darf ihre Narben behalten, während der Nether hinter dir zuwächst, und ein Pack liefert diese Entscheidung gleich mit, statt Spieler an die Config zu schicken. Dieselbe Arbeit zahlt sich an unerwarteter Stelle noch einmal aus: Das Fällen von Bäumen in Dörfern nutzt die Baumgeometrie von Blast Plaster, deshalb kommt ein Baum, der über eine neue Straße ragt, ganz herunter, statt an der Grenze abgeschnitten zu werden.

Allein installiert arbeitet Blast Plaster genau wie bisher aus seiner eigenen Config. Dieser Mod übernimmt das Steuer erst, wenn ein Pack darum bittet.

Die Dateien liegen unter `assets/<namespace>/blastplaster/*.json`. Was oben in der Datei steht, gilt überall; ein `dimensions`-Block überschreibt es für eine Dimension anhand ihrer Id. Alles, was ein Pack nie nennt, behält das, was Blast Plasters eigene Config sagt, ein Pack setzt also die Handvoll, um die es ihm geht, und lässt den Rest in Ruhe.

```json
{
  "explosionMode": "EJECT_DROPS",
  "healFullTrees": true,
  "maxTreeSize": 400,
  "dimensions": {
    "-1": { "explosionMode": "HEAL", "minimumTicksBeforeHeal": 200 },
    "1": { "enableExplosionSmoke": false }
  }
}
```

`explosionMode` gibt allem anderen seine Form. `HEAL` sprengt die Blöcke heraus und setzt die Welt danach wieder zusammen, `EJECT_DROPS` lässt das Loch stehen und wirft etwa ein Drittel dessen ab, was dort war, so wie es ein Creeper im unangetasteten Spiel tut, und `VISUAL_TOSS` lässt das Loch stehen und wirft nichts ab. Sobald dieser Mod steuert, ist die Vorgabe `EJECT_DROPS` und nicht Blast Plasters eigenes `HEAL`, ein Pack, das beides installiert und nichts schreibt, bekommt also Explosionen, die sich verhalten wie in dem Spiel, das seine Spieler kennen. Wer will, dass die Welt sich selbst flickt, verlangt `HEAL`, überall oder in einer Dimension.

| Schlüssel | Wert | Was er tut |
| --- | --- | --- |
| `explosionMode` | `HEAL`, `EJECT_DROPS`, `VISUAL_TOSS` | Was nach dem Knall geschieht |
| `healCreepers`, `healNonPlayerTNT`, `healWither`, `healAll` | true oder false | Welche Explosionen überhaupt behandelt werden |
| `processPlayerIgnitedTNT` | true oder false | Ob von Spielern gezündetes TNT mitbehandelt wird |
| `customEntitiesToHeal` | Liste von Entity-Namen | Explosionen aus anderen Mods, benannt als `modid:entity` |
| `healFullTrees` | true oder false | Ein angeschnittener Baum wird ganz mitgenommen oder ganz wiederhergestellt, statt durchtrennt zu werden |
| `maxTreeSize` | Zahl | Wie viele Blöcke ein Baum höchstens für sich beanspruchen darf, bevor er in Ruhe gelassen wird |
| `minimumTicksBeforeHeal`, `randomTickVar` | Zahlen | Wie lange es dauert, bis geflickt wird, und wie ungleichmäßig das geschieht |
| `overrideBlocks` | true oder false | Ob das Flicken überschreibt, was inzwischen ins Loch gebaut wurde |
| `enableFakeTossedBlocks` | true oder false | Der Schutt, der aus der Explosion fliegt |
| `enableExplosionFlash` | true oder false | Der helle Blitz im Moment der Explosion |
| `explosionFlashDuration`, `explosionFlashLightLevel`, `explosionFlashParticleCount`, `explosionFlashPulses` | Zahlen | Wie lange der Blitz dauert, wie hell er brennt, wie viele Partikel er wirft und wie oft er pulst |
| `enableExplosionSmoke` | true oder false | Die Rauchsäule danach |
| `explosionSmokeDuration`, `explosionSmokeParticleCount` | Zahlen | Wie lange der Rauch bleibt und wie dicht er steht |
| `playerTNTAlwaysDrops`, `playerTNTDropFullBlocks` | true oder false | Was das eigene TNT eines Spielers hinterlässt |
| `enableDropSuppression`, `dtSpecialDrops` | true oder false | Drops innerhalb einer Explosion, und die eigenen Drops von Dynamic Trees |
| `preventMobDrops` | true oder false | Ob von einer Explosion getötete Mobs noch etwas fallen lassen |

**Dem Auge nach Vanilla.** Ein Pack, dessen Explosionen niemand vom unangetasteten Spiel unterscheiden soll, schreibt `EJECT_DROPS` und schaltet `healFullTrees`, `enableFakeTossedBlocks`, `enableExplosionFlash`, `enableExplosionSmoke`, `preventMobDrops` und `playerTNTAlwaysDrops` ab. Alles andere ist Blast Plaster, das seine Karten zeigt, und jeder dieser Schlüssel lässt sich auch pro Dimension setzen, die Oberwelt kann also unangetastet aussehen, während eine andere Dimension sich selbst flickt.

**Spieler ohne den Mod** merken so oder so nichts Ungewöhnliches. Der Blitz ist das Einzige, was einen eigenen Block in die Welt setzt, wenn ein Pack `vanillaClients` setzt, wird er deshalb abgeschaltet, ganz gleich was in einer Datei steht, und der Rest sind Partikel und Items, die ein blanker Client ohnehin versteht.

Zwei Einstellungen von Blast Plaster sind keine Pack-Schlüssel: sein Debug-Log und die Liste, die jede Holzart mit ihrem Laub paart. Diese Paarung ist es, die dem Mod sagt, dass ein Baum ein Baum ist, hier wie dort, sie bleibt deshalb eine Antwort für das ganze Spiel statt einer je Dimension. Beides steht in Blast Plasters eigener Config.

## Grab-Mods

Hier muss nichts installiert, nichts eingestellt und nichts angeschaltet werden. Ein Grab-Mod und dieser hier teilen sich genau ein Stück Boden – die Beutetabelle, die ein Pack beim Tod eines Spielers auswürfelt –, und das ist im Voraus geklärt, damit keiner der beiden vom anderen wissen muss.

RDPL legt diese Items als ganz gewöhnliche Todesdrops ab, und zwar bevor irgendein Grab-Mod sich den Tod ansieht. Ein Grab-Mod arbeitet mit den Drops, die der Tod erzeugt hat, findet sie dort also mit allem anderen zusammen und legt sie ins Grab: Die Beute landet da, wo auch das Inventar gelandet ist, und genau das erwartet jemand, der sich einen Grab-Mod installiert hat. Gravestone, GraveStone Mod und Corail Tombstone arbeiten alle so, und alles andere, was auf denselben Drops aufsetzt, ebenfalls.

`dropLoose` in einer `player_loot`-Datei ist der Schalter für die andere Absicht, je Eintrag. Die Items gehen gar nicht erst durch die Drops, sondern werden für sich in die Welt gesetzt, kein Grab-Mod bekommt sie also zu sehen: Das Inventar wandert ins Grab wie immer, und die Beute liegt daneben auf dem Boden, für den, der den Kill gemacht hat. Das ist die Einstellung für Trophäen – einen Kopf, ein Herz –, die dem Töter gehören sollten, statt im Grab des Opfers eingeschlossen zu sein.

[Spielerbeute](#spielerbeute) hat die Schlüssel, das übrige Verhalten und den einen Vorbehalt, der mit `dropLoose` einhergeht.

---

# Referenz

## Wertelisten

Das sind die Namen, die der Parser überall dort annimmt, wo die Tabellen oben „eines der Materialien“ und Ähnliches sagen. Alles Unbekannte wird protokolliert und durch den Standardwert ersetzt.

**Blockmaterialien.** `air`, `grass`, `ground`, `wood`, `rock`, `iron`, `anvil`, `water`, `lava`, `leaves`, `plants`, `vine`, `sponge`, `cloth`, `fire`, `sand`, `circuits`, `carpet`, `glass`, `redstone_light`, `tnt`, `coral`, `ice`, `packed_ice`, `snow`, `crafted_snow`, `cactus`, `clay`, `gourd`, `dragon_egg`, `portal`, `cake`, `web`, `piston`, `barrier`, `structure_void`.

**Sound-Typen.** `wood`, `ground`, `plant`, `stone`, `metal`, `glass`, `cloth`, `sand`, `snow`, `ladder`, `anvil`, `slime`.

**Kartenfarben.** `air`, `grass`, `sand`, `cloth`, `tnt`, `ice`, `iron`, `foliage`, `snow`, `clay`, `dirt`, `stone`, `water`, `wood`, `quartz`, `adobe`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `silver`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, `black`, `gold`, `diamond`, `lapis`, `emerald`, `obsidian`, `netherrack`.

**Render-Layer.** `solid`, `cutout`, `cutout_mipped`, `translucent`. Leer gelassen sucht sich der Block einen passend zu seinem Typ.

**Seltenheiten.** `common`, `uncommon`, `rare`, `epic`.

**Fackelpartikel.** `none`, `flame`, `colored`. `colored` nutzt `particleColor`.

**Werkzeugklassen.** `pickaxe`, `axe`, `shovel`, `sword`.

**Rüstungsslots.** `head` oder `helmet`, `chest` oder `chestplate`, `legs` oder `leggings`, `feet` oder `boots`.

**Färbungen.** `biome`, `none` oder eine sechsstellige Hex-Farbe. Farben sind überall in einer Definition Hex-Werte, mit oder ohne führendes `#`.

**Verhalten** für `behavesAs`. `till`, `path`.

**Der Name einer neuen Welt** wird mit `worldName` in der Gruppe `terrain` gesetzt. Der Bildschirm zum Erstellen einer Welt öffnet sich mit diesem Namen bereits im Feld, und der Ordner, in dem die Welt gespeichert wird, folgt daraus wie immer. Er füllt das Feld nur, solange dort noch steht, was das Spiel hineingeschrieben hat, ein vom Spieler getippter Name wird also nie weggenommen; und anders als Seed und Spielmodus wird er hinterher nicht wieder gesetzt: Was beim Erstellen im Feld steht, ist der Name.

**Der Spielmodus** wird mit `worldGameMode` in der Gruppe `terrain` gesetzt, einer von `survival`, `creative`, `adventure` oder `spectator`. Jede Welt, die mit eingeschaltetem Pack erstellt wird, startet so, und `creative` schaltet außerdem Befehle frei, genau wie das Häkchen beim Erstellen von Hand. Es entscheidet nur, wie eine Welt beginnt; den Modus später in der Welt zu wechseln, bleibt unangetastet. Der Erstellungsbildschirm startet mit diesem Modus bereits ausgewählt und mit dem Seed, um den ein Pack bittet, bereits eingetragen – was dort zu sehen ist, ist also das, was passieren wird, und wer will, darf es vor dem Erstellen ändern, auch wenn das Pack es zurücksetzt. Abenteuer und Zuschauer werden auf diesem Bildschirm nicht angeboten, ein Pack, das einen davon will, lässt dort also stehen, was gewählt wurde, und setzt den Modus beim Erstellen der Welt.

**Wo eine neue Welt spawnt**, wird mit `worldSpawn` in der Gruppe `terrain` gesetzt, geschrieben als `x,z` oder `x,y,z`. Ohne y wird die übliche Bodenhöhe des Spiels für diesen Welttyp genommen, die Vanilla ohnehin speichert, und der Spieler wird dort an der Oberfläche abgesetzt. Es wird beim Erstellen der Welt angewendet, eine Welt, die es schon gibt, behält also den Spawn, mit dem sie geboren wurde, und ein Eintrag aus nicht ganzen Zahlen wird gemeldet und dem Spiel überlassen.

Das ist besonders bei flachen Welten wissenswert. Das Spiel sucht sich einen Spawn, indem es auf Meereshöhe nach Gras sucht, und auf einer Superflachwelt ist der Block über dem Schichtstapel immer Luft, diese Prüfung schlägt also nie an, und es wandert bis zu tausend Schritte weit auf der Suche. Eine flache Welt kann deshalb Hunderte Blöcke vom Ursprung entfernt öffnen, weit weg von dem, was ein Pack erwartet. `worldSpawn` zu nennen klärt das.

**Die Weltgrenze** wird mit `worldBorder` in der Gruppe `terrain` gesetzt, eine ganze Zahl Blöcke im Durchmesser, dieselbe Zahl, die `/worldborder set` nimmt. Sie wird beim Erstellen der Welt angewendet, eine bestehende Welt behält also ihre Grenze, und `0`, der Standard, lässt die Grenze dort, wo das Spiel sie setzt. Die Grenze ist dort zentriert, wo das Spiel sie zentriert, und lässt sich hinterher wie gewohnt per Befehl verschieben.

Ein Pack kann keine Grenze beliebiger Größe setzen. `worldBorderLimit` in der Config ist das Weiteste, worum ein Pack bitten darf, und ein Pack, das mehr will, wird rundheraus abgelehnt statt stillschweigend gekürzt: Der Grund wird protokolliert und die Grenze in Ruhe gelassen. Nur wer das Spiel betreibt, kann dieses Limit anheben, ein Pack kann einem Server also keine Grenze verpassen, der er nicht zugestimmt hat.

**Die Tageszeit** wird mit `worldTime` in der Gruppe `terrain` festgehalten, in Ticks, dieselbe Zahl, die `/time set` nimmt, `18000` ist also Mitternacht und `6000` Mittag. Die Uhr der Oberwelt bleibt dort stehen und bewegt sich nie, und alles, was ausliest, ob Tag ist – Mobspawn und Schlafen eingeschlossen –, bekommt die festgehaltene Zeit gesagt. `-1`, der Standard, lässt die Zeit laufen. Das ist die Oberwelt-Fassung des `fixedTime`, das eine eigene Dimension setzen kann, und anders als bei `doDaylightCycle` spielt es keine Rolle, was die Uhr beim Erstellen der Welt sagte.

**Strukturen** für eine Weltvorlage. `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges`, `endcities`, `caves`, `ravines`.

**Kreaturtypen** für Biom-Spawns und -Raten. `creature`, `monster`, `ambient`, `water_creature`.

## Ordnerliste

Unter `assets/<namespace>/`:

| Ordner | Was er macht |
| --- | --- |
| `blocks` | Blockdefinitionen |
| `items` | Itemdefinitionen |
| `fluids` | Flüssigkeiten, mit Block und Eimer |
| `materials` | Werkzeug- und Rüstungsmaterialien |
| `biomes` | Biomdefinitionen |
| `worldgen` | Was generiert, und wo |
| `dimensions` | Dimensionsdefinitionen |
| `worldtemplates` | Die Einstellungen einer ganzen Welt in einer Datei |
| `worldintro` | Seiten, die beim Betreten der Welt gezeigt werden |
| `gates` | Bedingungen für Portale und Dimensionen |
| `gamerules` | Spielregeln für neue Welten |
| `entities` | Entity-Varianten, aufgebaut auf vorhandenen Entities |
| `hardness` | Faktoren für Abbauzeit und Explosionswiderstand für Blockgruppen |
| `villages` | Grundstücke, die Dörfer bauen können |
| `blastplaster` | Was Blast Plaster nach einer Explosion tut, pro Dimension |
| `structures` | `.nbt`-Vorlagen, für Setzlinge, `imprint` und Mod-Overrides |
| `recipes` | Handwerksrezepte, hinzugefügt oder ersetzt |
| `recipe_removals` | Rezepte, gelöscht nach Name, Namespace oder Ergebnis |
| `furnace` | Ofenrezepte, hinzugefügt und entfernt |
| `fuels` | Brenndauern |
| `brewing` | Rezepte für den Braustand |
| `potions` | Trankeffekte |
| `potion_types` | Abgefüllte Tränke aus diesen Effekten |
| `villagers` | Berufe der Dorfbewohner |
| `trades` | Was Laufbahnen kaufen und verkaufen |
| `sounds` | Sound-Events |
| `oredict` | Ore-Dictionary-Namen |
| `loot_tables` | Beutetabellen, ersetzt |
| `loot_injections` | Ein Pool, der zu einer bestehenden Tabelle dazukommt |
| `player_loot` | Eine Beutetabelle, die beim Tod eines Spielers ausgewürfelt wird |
| `advancements` | Fortschritte |
| `functions` | `.mcfunction`-Dateien |
| `registry_remap` | Alte Namen, auf neue abgebildet |
| `tabs` | Kreativtabs |
| `texts` | Reine Textdateien, genutzt vom Welt-Intro |
| `models`, `blockstates`, `textures`, `lang` | Die üblichen Asset-Ordner |

## Befehle

`/rdpl` läuft auf deinem eigenen Rechner und braucht keine Rechte, weil er nur Dateien liest, die du ohnehin hast. Er funktioniert auf jedem Server, ob der Server den Mod hat oder nicht.

| Befehl | Was er macht |
| --- | --- |
| `/rdpl list` | Jedes geladene Pack, seine Priorität und was es enthält. Klick ein Pack an, um eine Datei darin nachzuschlagen |
| `/rdpl which <namespace:path>` | Welches Pack eine bestimmte Datei liefert und welche Packs es dabei verdeckt |
| `/rdpl reload` | Den Ordner neu einlesen und alles neu laden |
| `/rdpl reload <group>` | Nur eine Sorte neu laden: `textures`, `models`, `languages`, `sounds` oder `shaders` |
| `/rdpl unused` | Dateien in deinen Packs, nach denen noch nichts gefragt hat, meist ein Tippfehler im Pfad |
| `/rdpl biome list` | Jedes Biom, das generieren kann, mit seiner ID |
| `/rdpl biome here` | Das Biom, in dem du stehst |
| `/rdpl biome find <name>` | Die nächste Stelle, an der ein Biom generiert, ohne dafür Chunks zu erzeugen |

Auf einem dedizierten Server macht `/rdplserver` dasselbe für die Kopie des Ordners auf dem Server und braucht Operator-Rechte.

| Befehl | Was er macht |
| --- | --- |
| `/rdplserver reload` | Den Ordner des Servers neu einlesen und alles neu laden |
| `/rdplserver list` | Jedes Pack, das der Server geladen hat, seine Priorität und was es enthält |
| `/rdplserver which <namespace:path>` | Welches Pack eine bestimmte Datei liefert und welche Packs es dabei verdeckt |
| `/rdplserver unused` | Dateien in den Packs des Servers, nach denen nichts gefragt hat |
| `/rdplserver oregen` | Laufende Summen der blockierten Erzgenerierung, pro Mod und Typ |
| `/rdplserver biome` | Jedes Biom, das auf dem Server generieren kann |
| `/rdplserver dimensions` | Jede Dimension, auch die, die Packs hinzugefügt haben |
| `/rdplserver gate list` | Jedes Tor und ob es offen ist |
| `/rdplserver gate check <player>` | Welche Tore ein Spieler passiert hat |
| `/rdplserver gate grant <player> <gate>` | Ein Tor für einen Spieler öffnen |
| `/rdplserver gate revoke <player> <gate>` | Es wieder schließen |
| `/rdplserver intro` | Das Welt-Intro beim nächsten Beitritt noch einmal abspielen lassen |
| `/rdplserver goto <struktur>` | Bringt dich zur nächsten, bei der noch niemand war, und sucht, ohne das Land auf dem Weg zu erzeugen |
| `/rdplserver goto <struktur> next` | Bringt dich weiter zur nächstgelegenen, zu der du in dieser Sitzung noch nicht gebracht wurdest, ob schon einmal besucht oder nicht |
| `/rdplserver goto <struktur> back` | Bringt dich zur vorherigen zurück und geht Schritt für Schritt durch das, wohin diese Sitzung dich geschickt hat |

**`goto` öffnen.** Jeder Teil von `/rdplserver` braucht einen Operator, Stufe 3, und das bleibt so. Die drei `goto`-Formen sind die Ausnahme: Jede trägt eine eigene Berechtigungsstufe, die ein Pack oder die Config senken darf – getrennt von den beiden anderen und vom Rest des Befehls.

| Einstellung | Wofür sie gilt |
| --- | --- |
| `gotoLevel` | `goto <struktur>` |
| `gotoNextLevel` | `goto <struktur> next` |
| `gotoBackLevel` | `goto <struktur> back` |
| `gotoPlaceLevels` | Ein einzelner benannter Ort, in allen drei Formen |

Die Zahl ist die Berechtigungsstufe, die der Absender braucht. `3` ist ein Operator, das ist der Standard und dort bleibt der Rest des Befehls. `2` lässt auch einen Befehlsblock den Sprung ausführen, ein Pack kann ihn also auf einen Knopf, eine Druckplatte oder ein Ladenschild legen, ohne irgendwem den Rest von `/rdplserver` in die Hand zu geben. `0` lässt ihn jeden Spieler selbst tippen. Sie sind mit Absicht getrennt: Ein Pack kann `next` für einen Befehlsblock öffnen, der eine Rundfahrt von Dorf zu Dorf steuert, während `back` bei den Operatoren bleibt, oder den einfachen Sprung für Spieler öffnen und die beiden anderen zulassen.

Senkst du eine davon, kommt auch ein Nicht-Operator an den Befehl heran, deshalb prüft jeder andere Teil selbst auf einen Operator und verweigert mit einer Meldung, statt stillschweigend nichts zu tun. Die Tab-Vervollständigung zieht mit: Wer kein Operator ist, bekommt nur `goto` angeboten.

`gotoPlaceLevels` geht noch feiner und benennt einzelne Orte als `name=stufe`-Einträge, die die drei oben für genau diesen Ort überschreiben:

```json
{
  "settings": {
    "gotoLevel": 3,
    "gotoPlaceLevels": ["Crypt=2", "Waystone=0", "Mansion=4"]
  }
}
```

Der Name ist das, was du hinter `goto` tippen würdest: ein Vanilla-Name wie `Village` oder `Mansion`, oder ein Name, den dein eigenes Pack mit `locateAs` an einem Imprint-Eintrag angemeldet hat. Groß- und Kleinschreibung spielt dabei keine Rolle. Ein Pack kann also den Weg zu seinen eigenen Ruinen für einen Befehlsblock öffnen und seine Wegsteine für jeden Spieler, während `Village` und der Rest bei den Operatoren bleiben – oder andersherum die Vanilla-Strukturen für einen geführten Start öffnen und die eigenen Geheimnisse zulassen. Stufe `4` liegt über einem Operator und verschließt einen Ort für alle; so versteckst du einen einzelnen Ort, während `goto` sonst offen ist.

Ein Eintrag setzt eine Stufe für alle drei Formen dieses Ortes, denn ein Ort ist entweder einer, an den ein Spieler geschickt werden darf, oder nicht. Steht ein Ort nicht in der Liste, entscheiden die drei Einstellungen oben wie gewohnt, und ein Name, den nichts angemeldet hat, passt schlicht nie.

Die Tab-Vervollständigung hält sich an dieselben Regeln: Nach `goto` werden nur die Orte angeboten, zu denen der Absender auch wirklich gebracht werden kann.

Sie liegen in der Gruppe `commands`, also entscheidet `control.commands` in der Config, ob ein Pack sie überhaupt setzen darf, und `off` dort hält alles bei Operator, ganz gleich was ein Pack verlangt.


**`/rdpl` erreicht auch den Server-Befehl.** Alles, was `/rdpl` nicht selbst erledigt – `oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro` und `goto` –, wird unverändert an `/rdplserver` weitergereicht und in der Tab-Vervollständigung mit angeboten, im Einzelspieler gibt es also nur einen Befehl zu tippen. Weitergereicht wird Wort für Wort, und der Server entscheidet wie immer, Berechtigungen eingeschlossen; durch den kürzeren Namen wird also nichts geöffnet. Die Unterbefehle, die es doppelt gibt – `reload`, `list`, `which`, `unused`, `biome` und `config` –, bleiben bei `/rdpl` und meinen die Packs des Clients.

**Beim täglichen Arbeiten:** `/rdpl reload textures` ist in einem großen Modpack viel schneller als F3+T. F3+T funktioniert weiterhin und lädt alles neu. Nimm das schlichte `/rdpl reload`, wenn du eine Datei *hinzufügst* oder *löschst*, weil sich damit ändert, was der Ordner enthält.

## Gut zu wissen

- CraftTweaker und GroovyScript laufen nach RDPL, ihre Änderungen gewinnen also weiterhin.
- Rezepte laden nur beim Start, Rezeptänderungen brauchen also einen Neustart statt eines Reloads.
- Funktionen, die im Datenordner einer Welt liegen, schlagen weiterhin eine Funktion aus einem Pack, und die eigenen Fortschritte dieser Welt ebenso.
- Eine Struktur, die schon generiert wurde, bleibt geladen, bis du die Welt verlässt.
- Groß- und Kleinschreibung im Dateinamen zählt. Passt die Schreibweise deiner Datei nicht zu dem, wonach das Spiel gefragt hat, lädt RDPL sie trotzdem, warnt dich aber – denn unter Linux würde sie überhaupt nicht gefunden.
- Leg eine `pack.png` in `rdploader`, um dem Pack ein Symbol zu geben.
- Der Ordner lässt sich mit der Option `rootDirectory` in `config/mct_resourcedatapackloader_mixin.cfg` verschieben oder umbenennen. Ein absoluter Pfad geht auch, und es braucht einen Neustart.
- Blockstates, die ein nacktes Vanilla-Modell nennen, erben auch Vanillas Texturen. Eltern-Modelle wie `cube_all` und `cross` nehmen ihre Texturen aus dem Blockstate und sind unproblematisch.
- `forge_marker: 1` unterstützt kein Multipart, Blockstates für Ranken müssen also reines Vanilla-Multipart sein, mit den Texturen im Modell selbst.

## Wenn etwas nicht funktioniert

**Sieh zuerst in `logs/rdpl.log`.** Alles, was RDPL tut, landet dort statt im Hauptlog. Fortschritte, Beutetabellen, Rezepte, Funktionen, Strukturen und jeder Inhalt werden mit dem Pack protokolliert, aus dem sie kamen, und alles Fehlerhafte mit dem Grund.

**Bei Texturen und anderen Assets ist es anders.** Nach ihnen wird viel zu oft gefragt, um sie einzeln zu protokollieren – stattdessen listet `/rdpl unused` die Dateien in deinen Packs auf, nach denen nichts gefragt hat. Führ ihn aus, wenn das Spiel fertig geladen hat. Nach einer Datei mit dem richtigen Pfad wird immer gefragt, alles Aufgelistete ist also meist ein Tippfehler – bedenke aber, dass manche Dateien nur bei Bedarf laden, etwa andere Sprachen als die, in der du spielst.

**Ein Pack-Ordner oder eine Zip ohne `assets`-Verzeichnis darin wird übersprungen**, und das Log sagt es.

**`/rdpl which minecraft:textures/blocks/stone.png`** sagt dir genau, welches Pack eine Datei liefert und was es dabei verdeckt.

## Bonus: Vanilla-Tweaks

Kleine Änderungen daran, wie Vanilla sich verhält, jede über die Config-Kategorie `tweaks` schaltbar.

| Option | Standard | Was sie macht |
| --- | --- | --- |
| `promptLeafDecay` | an | Blätter, die ihren Baum verlieren, verwelken binnen einer Sekunde, statt auf Random-Ticks zu warten |
| `lenientPaths` | an | Trampelpfade lassen sich unter einem Block anlegen und bleiben liegen, wenn einer darübergesetzt wird |
| `unbreakableSpawners` | aus | Mobspawner lassen sich weder abbauen noch sprengen |

Drei weitere sitzen in der Kategorie `content` statt in `tweaks`:

| Option | Standard | Was sie macht |
| --- | --- | --- |
| `cactusMaxHeight` | `3` | Wie hoch Vanilla-Kakteen wachsen |
| `caneMaxHeight` | `3` | Wie hoch Vanilla-Zuckerrohr wächst |
| `shovelPaths` | an | Eine Schaufel macht aus Blöcken mit `behavesAs`-Pfad einen Pfad, und Schleichen macht das rückgängig |

**Diese treten hinter Universal Tweaks zurück**, das dieselben Vanilla-Blöcke ändert. Wann genau, steht unter [Universal Tweaks](#universal-tweaks).

**Nichts davon erreicht ein Pack.** Diese Optionen ändern nur Minecrafts eigene Kakteen, Zuckerrohre, Blätter und Pfade. Ein Block, den dein Pack mit `"type": "cane"` definiert, bringt seinen eigenen `growth`-Abschnitt mit und wächst auf die Höhe, die du ihm gegeben hast, egal was sonst installiert ist. `lenientPaths` hebt dieselbe Einschränkung auch für Pack-Blöcke auf, die `behavesAs` nutzen, was Universal Tweaks nicht anfasst – diese Hälfte bleibt also so oder so an.

### Unzerstörbare Spawner

`unbreakableSpawners` gibt dem Mobspawner-Block die Werte von Grundgestein: eine Härte, die sich nicht abbauen lässt, und einen Explosionswiderstand, den nichts übersteht. Ein Spieler bekommt keinen abgebaut, egal wie gut die Spitzhacke ist, und weder Creeper noch TNT noch eine Pack-Entity mit `explodes` reißen einen weg. Der Kreativmodus entfernt sie weiterhin, genau wie er Grundgestein weiterhin entfernt, ein Pack-Autor sperrt sich also nie aus dem eigenen Bau aus. Es braucht einen Neustart, weil die Werte einmal gesetzt werden, wenn das Spiel fertig lädt.

**Es geht um den Block, nicht um den einzelnen Spawner.** Es gibt keinen Schalter pro Spawner. Die Option ändert `minecraft:mob_spawner` selbst, sie erreicht also jeden Spawner der Welt auf einmal: die vier Vanilla-Strukturen, die einen setzen, jeden, den ein Mod setzt, und jeden, den deine eigenen Packs setzen.

Genau das ist die Antwort für eine eigene Struktur. Ein Spawner in einer deiner `.nbt`-Vorlagen, gesetzt von einem `imprint`-Eintrag, ist ein gewöhnlicher Mobspawner-Block mit seiner eigenen Tile Entity, er ist also abgedeckt, sobald die Option an ist. Bau die Struktur wie üblich mit einem Spawner darin, leg in den Tile-Entity-Daten der Vorlage fest, was er spawnt, schalte `unbreakableSpawners` an, und der in deinem Verlies ist genauso unzerstörbar wie der in Vanillas. Dafür kommt nichts ins Pack, und es gibt keine Möglichkeit, nur deine zu schützen und den Rest der Welt abbaubar zu lassen.

## Bonus: JEI-Plugin-Konflikt beheben

Manche Mods fragen JEIs Rezept-Registry ab, bevor die Mods, die sie füllen, mit ihrer Initialisierung fertig sind, was die Logs mit Hunderten harmloser, aber lauter Fehler flutet und die JEI-Anbindung eines Mods stillschweigend kaputtmachen kann. RDPL erkennt das automatisch und korrigiert die Reihenfolge der Benachrichtigungen. Es funktioniert mit Just Enough Items und mit Had Enough Items. Ist keines von beiden installiert, passiert nichts.

## Bonus: weniger Startfehler

- Rezepte, die auf ein Item verweisen, das kein Mod tatsächlich registriert hat – meist Inhalt, der in der Config eines Mods abgeschaltet ist –, werden übersprungen, statt einen Parse-Fehler zu werfen. Die Anzahl wird einmal protokolliert. (`skipMissingItems`)
- Fortschritte, die ein Rezept freischalten, das ein Skript inzwischen entfernt hat, laden trotzdem, statt zu scheitern. Sie schalten dieses Rezept nur nie frei, und das Ganze wird in einer Zeile zusammengefasst. (`tolerateMissingInAdvancements`)

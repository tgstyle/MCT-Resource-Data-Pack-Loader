# Resource Data Pack Loader

**Ein Ordner, der alles überschreibt, was Minecraft oder ein Mod mitbringt, neuen Inhalt aus JSON beschreibt und steuert, was generiert wird – in jeder Welt, auf Clients und Servern, ohne dass Spieler irgendetwas einschalten müssen.**

Drei fertige Beispiele. Leg eines davon direkt in `rdploader` und schau dir an, wie jede Datei geschrieben ist.

- [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) deckt die meisten Möglichkeiten ab: Blöcke, Items, Biome, eine Dimension, eine Weltvorlage und jede Worldgen-Form.
- [RDPLExampleOrePackVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleOrePackVoid.zip) verwandelt die Oberwelt in eine leere Void-Welt, in der die Generierung frei in der Luft hängt, eine Form pro Höhenband, sodass jede einzeln gut zu sehen ist.
- [RDPLExampleDeepWorld.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleDeepWorld.zip) macht die Oberwelt zu einer Rubic-Welt mit 256 Blöcken generierter Welt unter der von Vanilla und 128 darüber: der Übergang zum Tiefenstein, moderne Rausch-Höhlen, Schluchten, gebänderte Erzadern, drei Höhlenregionen, durch die man absteigt, und schwebende Inseln darüber, die dasselbe Rauschen schneidet.

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
- [Blockstates nach Typ](#blockstates-nach-typ)
- [Damit Vanilla deinen Block richtig behandelt](#damit-vanilla-deinen-block-richtig-behandelt)
- [Items](#items)
- [Flüssigkeiten](#flüssigkeiten)
- [Materialien, Tabs, Sounds, Ore Dictionary](#materialien-tabs-sounds-ore-dictionary)
- [Ofenrezepte und Brennstoffe](#ofenrezepte-und-brennstoffe)
- [Tränke, Trankarten und Brauen](#tränke-trankarten-und-brauen)
- [Expositionen](#expositionen)
- [Dorfbewohner und Handel](#dorfbewohner-und-handel)
- [Entity-Varianten](#entity-varianten)
- [Dorfgrundstücke](#dorfgrundstücke)
- [Biome](#biome)
- [Dimensionen](#dimensionen)
- [Portale und Tore](#portale-und-tore)
- [Weltvorlagen](#weltvorlagen)
- [Rubic-Welten](#rubic-welten)
- [Die Tiefenwelt](#die-tiefenwelt)
- [Höhlenregionen](#höhlenregionen)
- [Welt-Intro](#welt-intro)
- [Spielregeln](#spielregeln)
- [Härtegruppen](#härtegruppen)

**Generieren**
- [Worldgen-Einträge](#worldgen-einträge)
- [Formen](#formen)
- [Strukturkarten](#strukturkarten)
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
- [Blast Plaster Integration](#blast-plaster-integration)
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

Der Resource Data Pack Loader (RDPL) liest einen einzigen Ordner, `rdploader`, und erledigt drei Aufgaben:

- **Überschreiben.** Eine Datei im Ordner ersetzt die, die das Spiel oder ein Mod geladen hätte. Kein Schalter, keine Einrichtung pro Welt, nichts, was Spieler aktivieren müssen.
- **Neuer Inhalt.** JSON-Definitionen registrieren Blöcke, Items, Flüssigkeiten, Biome, Dimensionen, Tränke und Dorfbewohner. Kein Java, kein Jar.
- **Steuerung.** Erz-, Biom-, Struktur- oder Rezeptgenerierung blockieren, Grundgestein glätten, Spawnraten setzen, die Oberwelt leeren, Weltvorgaben festlegen.

## Wo die Dateien liegen

Jeder Pfad in diesem Handbuch ist ab `assets/` geschrieben, `<namespace>/blocks/*.json` ist auf der Platte also `assets/mypack/blocks/ruby_ore.json` für ein Pack mit dem Namespace `mypack`. Jeder Abschnitt wiederholt seinen eigenen Pfad unter der Überschrift, mit einer Notiz dazu, was aus diesem Pfad wird.

| Pfad | Was darin liegt |
| --- | --- |
| `<namespace>/blocks/*.json` | Blockdefinitionen. [Blöcke](#blöcke) |
| `<namespace>/items/*.json` | Itemdefinitionen. [Items](#items) |
| `<namespace>/fluids/*.json` | Flüssigkeiten, mit Block und Eimer. [Flüssigkeiten](#flüssigkeiten) |
| `<namespace>/materials/*.json` | Werkzeug- und Rüstungsmaterialien. [Materialien, Tabs, Sounds, Ore Dictionary](#materialien-tabs-sounds-ore-dictionary) |
| `<namespace>/tabs/*.json` | Kreativtabs. [Materialien, Tabs, Sounds, Ore Dictionary](#materialien-tabs-sounds-ore-dictionary) |
| `<namespace>/sounds/*.json` | Sound-Events. [Materialien, Tabs, Sounds, Ore Dictionary](#materialien-tabs-sounds-ore-dictionary) |
| `<namespace>/oredict/*.json` | Ore-Dictionary-Namen. [Materialien, Tabs, Sounds, Ore Dictionary](#materialien-tabs-sounds-ore-dictionary) |
| `<namespace>/biomes/*.json` | Biomdefinitionen. [Biome](#biome) |
| `<namespace>/worldgen/*.json` | Was generiert, und wo. [Worldgen-Einträge](#worldgen-einträge) |
| `<namespace>/caveregions/*.json` | Benannte Regionen, über den Untergrund gelegt. [Höhlenregionen](#höhlenregionen) |
| `<namespace>/dimensions/*.json` | Dimensionsdefinitionen. [Dimensionen](#dimensionen) |
| `<namespace>/worldtemplates/*.json` | Die Einstellungen einer ganzen Welt in einer Datei. [Weltvorlagen](#weltvorlagen) |
| `<namespace>/worldintro/*.json` | Seiten, die beim Betreten der Welt gezeigt werden. [Welt-Intro](#welt-intro) |
| `<namespace>/gates/*.json` | Bedingungen für Portale und Dimensionen. [Portale und Tore](#portale-und-tore) |
| `<namespace>/gamerules/*.json` | Spielregeln für neue Welten. [Spielregeln](#spielregeln) |
| `<namespace>/entities/*.json` | Entity-Varianten, aufgebaut auf vorhandenen Entities. [Entity-Varianten](#entity-varianten) |
| `<namespace>/hardness/*.json` | Faktoren für Abbauzeit und Explosionswiderstand für Blockgruppen. [Härtegruppen](#härtegruppen) |
| `<namespace>/exposures/*.json` | Gefahren, denen Spieler nahe an oder beim Tragen benannter Blöcke und Items ausgesetzt sind. [Expositionen](#expositionen) |
| `<namespace>/overrides/<target>/<name>.json` | Eigenschaften vorhandener Blöcke, Items und Tranktypen, direkt geändert. [Eigenschaften überschreiben](#eigenschaften-überschreiben) |
| `<namespace>/villages/*.json` | Grundstücke, die Dörfer bauen können. [Dorfgrundstücke](#dorfgrundstücke) |
| `<namespace>/pathintersects/*.json` | Muster, die an Kreuzungen von Dorfstraßen gemalt werden. [Dorfwege](#dorfwege) |
| `<namespace>/structuremaps/*.json` | Vorlagen, auf einem Raster zu einem großen Bauwerk zusammengesetzt. [Strukturkarten](#strukturkarten) |
| `<namespace>/citymaps/*.json` | Ein gezeichneter Straßenplan, nach dem ein Dorf angelegt wird, statt zu wachsen. [Stadtpläne](#stadtpläne) |
| `<namespace>/portalframes/*.json` | Rahmen, die ein Spieler bauen und anzünden kann. [Portalrahmen](#portalrahmen) |
| `<namespace>/blastplaster/*.json` | Was Blast Plaster nach einer Explosion tut, pro Dimension. [Blast Plaster Integration](#blast-plaster-integration) |
| `<namespace>/structures/*.nbt` | Vorlagen, für Setzlinge, `imprint` und Mod-Overrides. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/recipes/*.json` | Handwerksrezepte, hinzugefügt oder ersetzt. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/recipe_removals/*.json` | Rezepte, gelöscht nach Name, Namespace oder Ergebnis. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/furnace/*.json` | Ofenrezepte, hinzugefügt und entfernt. [Ofenrezepte und Brennstoffe](#ofenrezepte-und-brennstoffe) |
| `<namespace>/fuels/*.json` | Brenndauern. [Ofenrezepte und Brennstoffe](#ofenrezepte-und-brennstoffe) |
| `<namespace>/brewing/*.json` | Rezepte für den Braustand. [Tränke, Trankarten und Brauen](#tränke-trankarten-und-brauen) |
| `<namespace>/potions/*.json` | Trankeffekte. [Tränke, Trankarten und Brauen](#tränke-trankarten-und-brauen) |
| `<namespace>/potion_types/*.json` | Abgefüllte Tränke aus diesen Effekten. [Tränke, Trankarten und Brauen](#tränke-trankarten-und-brauen) |
| `<namespace>/villagers/*.json` | Berufe der Dorfbewohner. [Dorfbewohner und Handel](#dorfbewohner-und-handel) |
| `<namespace>/trades/*.json` | Was Laufbahnen kaufen und verkaufen. [Dorfbewohner und Handel](#dorfbewohner-und-handel) |
| `<namespace>/loot_tables/*.json` | Beutetabellen, ersetzt. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/loot_injections/*.json` | Ein Pool, der zu einer bestehenden Tabelle dazukommt. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/player_loot/*.json` | Eine Beutetabelle, die beim Tod eines Spielers ausgewürfelt wird. [Spielerbeute](#spielerbeute) |
| `<namespace>/advancements/*.json` | Fortschritte. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/functions/*.mcfunction` | Funktionsdateien. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/registry_remap/*.json` | Alte Namen, auf neue abgebildet. [Registry-Umbenennungen](#registry-umbenennungen) |
| `<namespace>/texts/*.txt` | Reine Textdateien, genutzt vom Welt-Intro. [Welt-Intro](#welt-intro) |
| `<namespace>/models/`, `<namespace>/blockstates/`, `<namespace>/textures/`, `<namespace>/lang/` | Die üblichen Asset-Ordner. [Modelle, Blockstates und Texturen](#modelle-blockstates-und-texturen) |

## Die Tabellen lesen

Jede Datei ist gewöhnliches JSON. Ein repräsentativer Worldgen-Eintrag:

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
| Blockname, Itemname | `"minecraft:stone"`, mit Metadaten als drittem Teil: `"minecraft:stone:3"` |
| `namespace:name` | `"mypack:ruby_ore"` |
| Biomname, Soundname, Tab-Name | dieselbe Form `namespace:name` in Anführungszeichen |
| Hex-Farbe | sechs Hex-Ziffern, `"A0C8FF"`, `#` optional |
| Texturpfad | `"mypack:blocks/ruby_ore"` |
| Liste von Ints | `[0, -1]` |
| Liste von Blocknamen | `["minecraft:stone", "minecraft:andesite"]` |
| Liste von Biomnamen | `["minecraft:extreme_hills", "mypack:ruby_hills"]` |
| Liste von Dictionary-Typen | `["MOUNTAIN", "FOREST"]` |
| Liste von Mod-IDs oder Pack-Namespaces | `["quark", "mypack"]` |
| Liste von Objekten | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`, Schlüssel gemäß der eigenen Tabelle des Objekts |
| Objekt | `{ "type": "cluster" }`, Schlüssel gemäß eigener Tabelle |
| Objekt aus Rolle zu Biom, aus Variantenname zu Variante | Schlüssel sind das Erste, Werte das Zweite: `{ "ocean": "mypack:ruby_ocean" }` |

Die meisten Definitionen nehmen außerdem `requires` an, eine Liste von Mod-IDs oder Pack-Namespaces, die vorhanden sein müssen, sonst wird die Datei übersprungen.

## Die eine Regel

Öffne das Jar, such die Datei, die du ändern willst, und kopiere ihren Pfad ab `assets`:

```
assets/minecraft/textures/blocks/iron_ore.png        (im Minecraft-Jar)
rdploader/assets/minecraft/textures/blocks/iron_ore.png    (dein Override)
```

Der Pfad nach `assets` ist immer identisch mit dem Pfad im Jar. Nichts wird umbenannt oder verschoben.

## Packs organisieren

Lose Dateien funktionieren. Bündeln funktioniert auch, als Ordner oder als Zip, und beide verhalten sich identisch:

```
rdploader/MyTextures/assets/...
rdploader/MyTextures.zip
```

**Priorität.** Enthalten zwei Packs dieselbe Datei, stell den Namen `RDPL` und eine Zahl voran – höhere Zahlen laden später und gewinnen:

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

Groß-/Kleinschreibung ist egal; ein Leerzeichen, Bindestrich oder Unterstrich nach der Zahl ist optional; das Präfix wird im Anzeigenamen ausgeblendet. Ein Pack ohne Präfix lädt zuerst und verliert gegen jedes nummerierte. Die Priorität bestimmt auch die Reihenfolge der Worldgen-Einträge – wichtig, wenn ein Pack Blöcke setzt, die ein anderes ersetzt.

**Ein Pack deaktivieren:** `.disabled` an den Namen anhängen.

## Ressourcenpakete: wer gewinnt

Standardmäßig liegen RDPL-Dateien über den Ressourcenpaketen, die ein Spieler auswählt; ein Ressourcenpaket kann sie also nicht überschreiben. `O` oder `N` nach dem `RDPL`-Präfix entscheidet das pro Pack:

```
rdploader/RDPLO Branding        gewinnt immer; Ressourcenpakete kommen nicht heran
rdploader/RDPLN BaseTextures    ein Ressourcenpaket darf es überschreiben
rdploader/RDPL1O Seasonal       Priorität und Override kombiniert
```

Packs ohne Buchstaben folgen der Config-Option `overrideResourcePacks`. `/rdpl list` markiert die überschreibenden Packs. Der Buchstabe muss das Präfix abschließen (gefolgt von Leerzeichen, Bindestrich, Unterstrich oder nichts) – `RDPLOverhaul` ist also ein Pack namens `Overhaul`, kein `O`-Flag.

## Mod-API

Eine Mod kann RDPL-Inhalte in ihrer eigenen Jar mitbringen und braucht dafür kein eigenes Pack. Leg einen Ordner namens `rdploader` in die Wurzel der Jar und bau ihn genau wie ein Pack auf:

```
diemod.jar
  mcmod.info
  rdploader/assets/diemod/blocks/rubinerz.json
```

Was eine Mod mitbringt, ist eine Vorgabe, keine Überschreibung. Es lädt unter jedem Pack im Pack-Ordner, alles von einem Pack-Autor gewinnt also dagegen, und eine Mod darf nur Dateien unter einem Namensraum liefern, den sie in ihrer eigenen `mcmod.info` nennt. Dateien unter jedem anderen Namensraum werden mit einer Warnung übergangen, ebenso ein verschachtelter `rdploader`-Ordner innerhalb eines Namensraums, damit keine Mod stillschweigend die Inhalte einer anderen Mod oder eines Pack-Autors umschreibt.

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
| `priority` | `-1` oder eine Zahl | `-1` | `-1` hält die Mod unter jedem Pack; jede andere Zahl setzt sie in die gewöhnliche [Vorrang](#packs-organisieren)-Reihenfolge neben die nummerierten Packs |

Ein Mod-Pack kommt nie in die Überschreibungsstufe der Ressourcenpakete, egal was `overrideResourcePacks` sagt, denn darum kann nur ein Pack-Autor mit dem Buchstaben `O` bitten. Das Log kennzeichnet Mod-Packs und listet Packs mit dem niedrigsten zuerst, es lädt also nichts ungesehen.

## Packs nur auf dem Server

Ein Pack kann allein auf dem Server liegen, mit Spielern auf reinen Vanilla-Clients, unter einer Bedingung: **nichts darin darf irgendetwas registrieren**. Beide Mod-IDs akzeptieren jede Gegenstelle; das Pack entscheidet. Ein Vanilla-Client spielt mit den Registries, die er mitgebracht hat; ein Pack, das sie erweitert, muss also auf beide Seiten.

| Server allein genügt | Pack muss auch auf den Client |
| --- | --- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures` | `blocks`, `items`, `fluids`, `materials` |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `oredict` | `potions`, `potion_types`, `sounds`, `tabs` |
| `loot_tables`, `loot_injections`, `player_loot`, `advancements`, `functions` | `biomes`, `dimensions` |
| `gates`, `trades`, `registry_remap` | `villagers` |
| die ganze Steuerungsebene, Einstellungen und Vorgenerierung | `models`, `blockstates`, `textures`, `lang` (Client-Ordner – ohne Client weglassen) |

Die rechte Spalte ist eine harte Grenze: Ein Vanilla-Client, der in eine unbekannte Dimension geschickt wird, fliegt sofort raus, und unbekannte Blöcke lassen sich ihm nicht beschreiben. Die linke Spalte funktioniert, weil alles darin entweder vollständig serverseitig läuft oder den Client über Pakete erreicht, die Vanilla ohnehin spricht (vom Server gefülltes Ergebnisfeld der Werkbank, gewöhnliche Fortschrittspakete, Statusmeldungen bei abgelehnten Toren, ein Vorgenerierungs-Halt aus Vanilla-Paketen für Spielmodus, Titel und Teleport).

`worldtemplates` liegt mit einer Ausnahme auf der Serverseite: **`rubicWorld` und `vanillaClients` schließen sich aus**. Eine Rubic-Welt besteht aus Würfeln, und einem Client ohne die Mod lassen sie sich nicht schicken – er flöge beim Anmelden raus oder sähe schlicht nichts. Sind beide gesetzt, entsteht eine gewöhnliche Welt statt einer Rubic-Welt, und das Log sagt warum; ein Server, der jeden Spieler abweist, entsteht so gar nicht erst. Nur ein Fall hält das Spiel wirklich an: `vanillaClients` einzuschalten für eine Welt, die bereits als Rubic-Welt angelegt wurde. Ihre Würfel liegen auf der Platte, sie als gewöhnliche Welt zu laden würde sie zerstören, also bleibt sie unangetastet und die Entscheidung bei dir.

Einrichtung:

1. Schalte `vanillaClients` in der Config ein (Kategorie `content`, braucht einen Neustart). Das erzwingt die rechte Spalte: Diese Ordner werden beim Laden übersprungen, jede übersprungene Datei steht namentlich im Log – aus einer durchgerutschten Blockdatei wird eine Logzeile statt einer abgelehnten Verbindung.
2. Halte Definitionen trotzdem aus den rechten Ordnern heraus; übersprungene Dateien sind totes Gewicht. Wo das Pack Items nennt (das `hold` eines Tors, `killedDrops`, Rezeptergebnisse, Handel), nenne nur Items, die Vanilla oder die anderen beidseitigen Mods des Servers mitbringen.
3. Entity-Varianten dürfen bleiben: Attribute, Drops und Spawns setzt der Server, das Aussehen rendert aber der Client – Vanilla-Clients sehen die Standardkreatur mit dem neuen Verhalten. Geht es um das Aussehen, ist das Pack nicht serverseitig.
4. Auf dem Server installieren wie üblich. Auf Spielerrechnern landet nichts; `/rdpl` existiert dort nicht.
5. Mit einem einzigen sauberen Vanilla-Client derselben Version testen. Fehler sind laut – die Verbindung wird an der Tür abgelehnt, nicht später still kaputt.
6. Zwei akzeptierte kosmetische Lücken: Server-Rezepte lassen sich craften, erscheinen aber nicht im Rezeptbuch, und rein verhaltensändernde Entity-Varianten tragen das Standardaussehen.

# Überschreiben

## Was du überschreiben kannst

- **Alles im assets-Ordner eines Mods**: Texturen, Modelle, Blockstates, Sprachdateien, Sounds, Schriftarten, Splash-Texte, Handbücher, Anleitungen
- **Fortschritte und Beutetabellen**, serverseitig, sie funktionieren also auch auf dedizierten Servern
- **Rezepte**: das Rezept eines Mods ersetzen oder ein eigenes hinzufügen
- **Strukturvorlagen**: die `.nbt`-Dateien, die Mods für generierte Gebäude nutzen, unter `<namespace>/structures/`
- **Funktionen**: die `.mcfunction`-Dateien unter `<namespace>/functions/`
- **Registry-Umbenennungen**: alte Welten am Leben halten, wenn ein Mod einen Block oder ein Item umbenennt
- **Rezept-Entfernungen**: ein Handwerksrezept nach Name, Namespace oder Ergebnis löschen
- **Beute-Injektionen**: einen Pool zu einer Beutetabelle hinzufügen, statt sie komplett zu ersetzen
- **Spielerbeute**: beim Tod eines Spielers eine Beutetabelle auswürfeln, zusätzlich zu dem, was er dabeihatte, oder an dessen Stelle
- **Eigenschaften vorhandener Blöcke, Items und Tränke**: Härte, Licht, Stapelgrößen, Essbarkeit für alles, die Effekte eines Tranks, siehe [Eigenschaften überschreiben](#eigenschaften-überschreiben)
- **Ore-Dictionary-Namen, Ofenrezepte, Brenndauern, Kreativtabs und Sound-Events**

RDPL eignet sich gut dafür, ein oder zwei Rezepte zu ersetzen, und Rezepte für eigenen Inhalt gehören mit in dasselbe Pack. Für volle Rezeptkontrolle über ein ganzes Modpack sind CraftTweaker und GroovyScript die besseren Werkzeuge, und eine Datei hier ersetzt das Original weiterhin vollständig – um also eine einzelne Zutat zu ändern oder einen einzelnen Beuteeintrag zu streichen, nimm die beiden.

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

Ein Pack kann neben seinem `assets` einen `config`-Ordner tragen, mit JSON-Dateien voller true/false-Optionen und ihren Standardwerten:

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

Eine Datei mit `"hide": true` auf oberster Ebene hält die Optionen dieses Packs komplett aus dem Optionsmenü und aus der erzeugten Datei heraus, während die Optionen den Inhalt weiterhin mit ihren Standardwerten steuern. Zwei Dinge wollen das: Inhalt, der noch nicht fertig ist, und Vorlagen-Packs, bei denen die Optionen Maschinerie sind, die die Definitionen zusammenhält, und keine Entscheidung, die irgendwer treffen sollte. Zum Veröffentlichen entfernst du den Schlüssel wieder. Dasselbe geht pro Option: `"hide": true` im Objekt einer Option versteckt nur diese eine, ein fertiges Pack kann also einen Schalter für unfertigen Inhalt oder ein Vorlagen-Gate mitbringen, ohne dass eins davon auftaucht:

    { "enablePackB": { "default": false, "hide": true } }

Da sich eine versteckte Option nicht umlegen lässt, ist eine versteckte Option mit Standardwert true faktisch fest eingeschaltet – für Inhalt, der durch die Options-Maschinerie verdrahtet bleiben muss, aber keine Wahl ist.

Eine Option kann auch ein Objekt mit einer Beschreibung sein, die im Optionsmenü unter ihrem Namen steht:

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

Beim Start werden die Optionsdateien eines Packs zu einer echten Config-Datei, die dem Nutzer gehört, benannt nach dem Pack: `rdploader/config/PackA.json`. Sie wird mit den Standardwerten des Packs angelegt und bei Pack-Updates zusammengeführt, sodass neue Optionen ankommen, ohne anzurühren, was der Nutzer schon eingestellt hat. Änderungen greifen beim nächsten Spielstart. Optionen gehören nur benannten Packs, einem Ordner oder einer Zip, weil die erzeugte Datei nach dem Pack benannt ist; lose Dateien unter `rdploader/assets` haben keinen Pack-Namen und tragen keine Optionen – pack losen Inhalt also in einen benannten Ordner, wenn er einen Schalter braucht.

Die `requires`-Liste jeder Definition kann dann mit einem `config:`-Eintrag eine Option nennen: `"requires": ["config:enableTestingContent"]` registriert diesen Inhalt nur, solange die Option true ist, genau wie ein fehlender Mod ihn überspringen ließe. Ein bloßer Name prüft die Datei jedes Packs, und jedes Pack, das ihn definiert, muss zustimmen; `"config:PackA:enableTestingContent"` nennt ein bestimmtes Pack. Eine Option, die kein Pack definiert, gilt als false und wird einmal im Log vermerkt.

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
| `lightOpacity` | `0` bis `255` | Wie viel Licht der Block schluckt |
| `soundType` | einer der Klangtypen | Schritt-, Setz- und Abbaugeräusche |
| `harvestTool` | Werkzeugklasse | Womit er abgebaut wird; `harvestToolLevel`, Standard `0`, setzt die Stufe |
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

Unter `overrides/minecraft/planks.json` brechen Bretter damit ungefähr so schnell wie Erde und lassen sich essen. `food` nimmt `heal` (`1`), `saturation` (`0.6`), `alwaysEdible` (`false`; `true` erlaubt Essen bei voller Hungerleiste) und `effects`, dessen Einträge genauso geschrieben werden wie bei einem Tranktyp. Ein Item, das schon Essen ist, nimmt neue `heal`, `saturation` und `alwaysEdible`; `effects` darauf wird nicht unterstützt, und das Log sagt es. Setzt das essbare Item einen Block, ziel zum Essen in den Himmel, denn Zielen auf einen Block setzt ihn: Das ist Vanillas Benutzungsreihenfolge, kein Fehler.

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

Overrides sind live. Die ursprünglichen Werte werden vor der ersten Änderung festgehalten, also springt nach dem Deaktivieren des Packs und `/rdpl reload` alles auf den alten Stand zurück, ganz ohne Neustart; dasselbe passiert bei jedem Betreten einer Welt. Eine Datei pro Ziel: Überschreiben zwei Packs dasselbe, ersetzt die Datei des späteren Packs die frühere komplett, und das Log sagt es.

Zwei Grenzen, die man kennen sollte. Ein Block oder Item, dessen eigener Code eine Eigenschaft berechnet, ignoriert das Feld dahinter: Das Override greift, ändert aber nichts; Vanilla macht das nur beim Explosionswiderstand von Treppen, Mods dürfen es überall. Und essbar gemachte Items funktionieren nur bei Items ohne eigenes Rechtsklick-Verhalten: Ein Item, das beim Benutzen schon etwas tut, tut das weiterhin.

Overrides brauchen das Pack auf Client und Server, denn Abbaugeschwindigkeit, Licht und Essen passieren auf dem Bildschirm des Spielers; für rein serverseitige Packs taugen sie nicht. `overrides` in der Config-Kategorie `content` schaltet den Ordner komplett ab.

## Registry-Umbenennungen

`<namespace>/registry_remap/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

Wenn ein Mod einen seiner Blöcke oder Items umbenennt, verlieren Welten, die vor der Umbenennung gespeichert wurden, sie. Leg hier eine Datei ab, die den alten Namen auf den neuen abbildet:

```json
{
  "registry": "minecraft:items",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

Die Registry ist die, zu der der Eintrag gehört, meist `minecraft:items` oder `minecraft:blocks`. Umbenennungen verketten sich: Bildest du A auf B ab und später B auf C, geht A direkt auf C.

## Spielerbeute

`<namespace>/player_loot/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

Vanilla 1.12 gibt Spielern keine Beutetabelle – beim Tod fällt nur das Inventar, und es gibt keinen Tabellennamen, den ein Pack überschreiben könnte. RDPL ergänzt eine, die beim Tod eines Spielers ausgewürfelt wird:

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

`add` legt die Items der Tabelle neben die Inventardrops – die Wahl für Kopfgelder auf einen Kill. `replace` verwirft das Inventar, und es fällt nur, was die Tabelle auswürfelt.

Steht `rollOnKeepInventory` auf aus, würfeln Tode unter `keepInventory` (und Tode im Zuschauermodus, die das Inventar immer behalten) nichts. Angeschaltet hält es den Tod auch auf Keep-Inventory-Welten teuer.

Mehrere Dateien stapeln sich, jede wird für sich entschieden. Ist ein zutreffender Eintrag `replace`, wird das Inventar einmal geleert, bevor gewürfelt wird – ein `add`-Eintrag daneben landet trotzdem.

Die Tabelle ist eine gewöhnliche Beutetabelle, über ihren Namen gesucht: Sie kann im Pack unter `loot_tables/entities/player.json` liegen, eine beliebige Vanilla- oder Mod-Tabelle sein und wird von `loot_injections` erreicht. Beutekontext: Der sterbende Spieler ist die erbeutete Entity, der Töter (falls vorhanden) der tötende Spieler, die Schadensquelle ist gesetzt – `killed_by_player`, `entity_properties`, `random_chance_with_looting`, `looting_enchant` und `quality` verhalten sich normal.

Eine Beutefunktion bringt RDPL selbst mit, nutzbar in jeder Tabelle mit einem geplünderten Wesen: `rdpl:killed_name` benennt das Item nach dem Opfer. `format` formt den Anzeigenamen (`%s` ist das Opfer, ohne Angabe nur der Name), `tag` schreibt stattdessen den bloßen Namen in einen NBT-Schlüssel für Items, die ihn selbst auslesen.

```json
{ "item": "mypack:human_skull", "weight": 1,
  "functions": [ { "function": "rdpl:killed_name", "format": "Schädel von %s" } ] }
```

**Grab-Mods.** Die gewürfelten Items kommen zu den normalen Todesdrops, bevor ein Grab-Mod sie liest, und landen darum mit allem anderen im Grab (`replace` legt den Tabelleninhalt statt des Inventars ins Grab). Gilt für Gravestone, GraveStone Mod, Corail Tombstone und alles andere, was mit der Dropliste des Todes arbeitet. Keine Einrichtung nötig.

`dropLoose` umgeht die Dropliste vollständig: Die Items werden direkt in die Welt gesetzt, Grab-Mods sehen sie nie – das Inventar wandert ins Grab, die Items der Tabelle liegen für den Töter auf dem Boden. Die Einstellung für Beute, die dem Töter gehört statt dem Grab des Opfers. Ohne Grab-Mod ändert sie wenig. Vorbehalt: Die Items existieren, bevor irgendetwas nachgelagert die Drops noch abbrechen könnte – ein Eintrag, der einen abgebrochenen Tod nicht überleben darf, lässt sie besser aus.

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

Jede Tabelle unten folgt den Konventionen aus [Die Tabellen lesen](#die-tabellen-lesen).

Die meisten Definitionen nehmen außerdem `requires` an, eine Liste von Mod-IDs oder Pack-Namespaces, die vorhanden sein müssen, sonst wird die Datei übersprungen.

## Blöcke

`<namespace>/blocks/*.json`

Der Pfad der Datei ist der Registry-Name des Blocks, `mypack/blocks/ruby_ore.json` registriert also `mypack:ruby_ore`. Die Schlüssel in `variants` benennen die Metadatenwerte dieses einen Blocks; eigene Blöcke sind sie nicht.

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
  "lightOpacity": 255,
  "slipperiness": 0.6,
  "flammability": 0,
  "fireSpread": 0,
  "explosionResistanceDivisor": 1.0,
  "modelBlock": "minecraft:stone",
  "modelMeta": 0,
  "itemModel": "state",
  "tint": "biome",
  "plantTypes": ["Plains", "Crop"],
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
  "portal": { "dimension": 12 },
  "variants": {
    "ruby_ore": {
      "meta": 0,
      "hardness": 3.0,
      "resistance": 5.0,
      "light": 0,
      "harvestLevel": 2,
      "rarity": "rare",
      "maxSize": 64,
      "oreDict": ["oreRuby"],
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
| `variants` | ja | Objekt aus Variantenname zu Variante |, | Ein Eintrag pro Metadatenwert. Der Schlüssel benennt diesen Wert im Blockstate, im Modellpfad und im Sprachschlüssel. Der Registry-Name kommt aus dem Pfad der Datei selbst |
| `type` | nein | einer der Typen oben | `basic` | Welche Form der Block annimmt |
| `material` | nein | eines der [Blockmaterialien](#wertelisten) | `rock` | Abbauverhalten, Kolben, Feuer und Flüssigkeiten |
| `soundType` | nein | einer der [Sound-Typen](#wertelisten) | vom Material | Schritte, Abbauen und Setzen |
| `mapColor` | nein | eine der [Kartenfarben](#wertelisten) | vom Material | Wie er auf einer Karte aussieht |
| `harvestTool` | nein | `pickaxe`, `axe`, `shovel` | `pickaxe` | Welches Werkzeug ihn abbaut |
| `harvestToolLevel` | nein | 0 bis 3 | `0` | 0 Holz, 1 Stein, 2 Eisen, 3 Diamant |
| `silkHarvest` | nein | boolean | `true` | Ob Behutsamkeit den Block selbst zurückgibt |
| `opensWith` | nein | Item-Id | keine | Macht den Block zur Schatzkiste: Abbauen liefert den Block selbst, ein Rechtsklick mit dem genannten Item verbraucht eines, spielt den Abbau-Sound, schüttet die `drops`-Liste der Variante aus und entfernt den Block. Jeder andere Klick zeigt die Aktionsleisten-Zeile `tile.<pack>:<block>.<variante>.locked` aus den Sprachdateien |
| `openSound` | nein | Sound-Name | der Abbau-Sound | Was eine Schatzkiste beim Öffnen statt ihres Abbau-Sounds spielt |
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
| `oreDict` | nein | Liste von Ore-Dictionary-Namen | keine | Ore-Dictionary-Namen, unter denen diese Variante eingetragen wird |
| `drops` | nein | Liste von Drops | droppt sich selbst | Was das Abbauen bringt |

**Metadaten sind endgültig.** Die Zahl, die eine Variante beansprucht, wird in jede gespeicherte Welt geschrieben, die sie enthält. Varianten später umzunummerieren oder umzusortieren macht aus gesetzten Blöcken etwas anderes. Häng neue Varianten hinten an und benutze eine Zahl nie ein zweites Mal.

Ein `basic`-Block fasst sechzehn Varianten, ein `slab` acht, `log` und `leaves` vier, weil Achse und Verwelk-Flag eigene Bits brauchen, und die Typen mit nur einem Zustand fassen eine.

### Drops

```json
{
  "drops": [
    { "block": "mypack:ruby", "meta": 0, "amount": { "min": 1, "max": 3 }, "chance": 100, "guaranteed": true, "bonusChance": [1, 2, 3] },
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
    "spread": 1,
    "maxHeight": 3,
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

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht.

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
    "vines": false,
    "structure": "mypack:ruby_tree"
  }
}
```

Ein `structure` ersetzt den generierten Baum durch eine deiner Vorlagen, und das ist der Weg, etwas zu bauen, was ein Generator nicht hinbekommt; sonst muss nichts im Block stehen:

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

Auch ein Block mit nur einer Variante behält die `blocks`-Eigenschaft, sein Schlüssel bleibt also `blocks=<name>`, aber nur bei den Typen, die diese Eigenschaft überhaupt haben. Elf Typen verbrauchen ihre gesamten Metadaten für ihre Form, halten eine Variante und tragen keine `blocks`-Eigenschaft, sie schlüsseln also allein über ihre eigenen Eigenschaften. [Blockstates nach Typ](#blockstates-nach-typ) sagt, welche welche sind.

Hat der Block eigene Eigenschaften, werden sie mit Kommas verbunden, in der Reihenfolge, in der der Zustand sie auflistet: `blocks=ruby_log,axis=y`, `blocks=ruby_slab,half=bottom`, `blocks=ruby_wall,up=true,north=true`. Eine Treppe hat keine `blocks`-Eigenschaft, sie wird also allein über `facing=east,half=bottom,shape=straight` angesprochen. Zwei Eigenschaften bleiben mit Absicht weg: die eigene Varianteneigenschaft einer Mauer sowie `check_decay` und `decayable` eines Blätterblocks – Blätter brauchen also nur `blocks=ruby_leaves`. Ein Banner hat gar keine Varianteneigenschaft und wird stehend über `rotation=0` bis `15` und an der Wand über `facing=north` angesprochen, siehe [Banner](#banner).

### Blockstates nach Typ

Zwei Dinge entscheiden, was in einer Blockstate-Datei stehen muss: ob der Typ die `blocks`-Eigenschaft trägt und welche Eigenschaften er selbst hat.

| Typ | Registriert | Blockstate-Eigenschaften | Varianten |
| --- | --- | --- | --- |
| `basic`, `ore`, `falling` | einen Block | `blocks` | 16 |
| `flower` | einen Block | `blocks` | 16 |
| `portal` | einen Block | `blocks` | 16 |
| `fence`, `pane` | einen Block | `blocks`, `north`, `east`, `south`, `west` | 16 |
| `wall` | einen Block | `blocks`, `up`, `north`, `east`, `south`, `west` | 16 |
| `slab` | zwei, `<name>` und `<name>_double` | die halbe Stufe `blocks` und `half`, die doppelte nur `blocks` | 8 |
| `log` | einen Block | `blocks`, `axis`, also `x`, `y`, `z` oder `none` | 4 |
| `leaves` | einen Block | `blocks` | 4 |
| `stairs` | einen Block | `facing`, `half`, `shape` | 1 |
| `door` | einen Block | `facing`, `half`, `hinge`, `open` | 1 |
| `trapdoor` | einen Block | `facing`, `half`, `open` | 1 |
| `fence_gate` | einen Block | `facing`, `in_wall`, `open` | 1 |
| `banner` | zwei, `<name>` und `<name>_wall` | stehend `rotation`, `0` bis `15`, an der Wand `facing` | 1 |
| `ladder`, `torch` | einen Block | `facing`, eine Fackel ergänzt `up` zu den vier Wänden | 1 |
| `crop` | einen Block | `age`, immer `0` bis `7`, egal was `maxAge` sagt | 1 |
| `cane` | einen Block | `age`, `0` bis `15` | 1 |
| `sapling` | einen Block | `stage`, `0` bis eins weniger als `stages` | 1 |
| `vine` | einen Block | `up`, `north`, `east`, `south`, `west`, und nur als Multipart | 1 |

Vier Eigenschaften werden dir abgenommen, schreib die Schlüssel also ohne sie: `powered` bei Türen und Toren, `variant` bei Mauern sowie `check_decay` und `decayable` bei Blättern.

Eine Feldfrucht behält Vanillas acht `age`-Werte, egal welchen `maxAge` sie hat, denn `maxAge` entscheidet nur, wie weit sie wächst; ihr Blockstate schreibt also immer `age=0` bis `age=7`.

Zwei Typen registrieren einen zweiten Block. Das `<name>_double` einer Stufe braucht einen eigenen Blockstate, geschlüsselt über `blocks` ohne `half`, und bekommt nie ein eigenes Item. Das `<name>_wall` eines Banners behandelt [Banner](#banner).

**Vine ist der eine Typ, der das Forge-Format nicht nutzen kann**, weil `forge_marker` kein Multipart unterstützt; sein Blockstate ist also eine gewöhnliche Vanilla-`multipart`-Liste mit der Textur im Modell.

**Das Forge-Format ist kürzer, und das Beispielpack nutzt genau dieses.** Ein Vanilla-Blockstate schreibt jede Kombination als eigenen Schlüssel aus, bei einer Treppe sind das vierzig. Mit `"forge_marker": 1` listet die Datei jede Eigenschaft einmal auf und das Spiel kombiniert sie, dieselben vierzig Zustände sind also elf Einträge:

```json
{
  "forge_marker": 1,
  "defaults": {
    "model": "stairs",
    "textures": {
      "bottom": "mypack:blocks/ruby_brick",
      "top": "mypack:blocks/ruby_brick",
      "side": "mypack:blocks/ruby_brick"
    },
    "uvlock": true
  },
  "variants": {
    "inventory": [{}],
    "facing": { "east": { "y": 0 }, "south": { "y": 90 }, "west": { "y": 180 }, "north": { "y": 270 } },
    "half": { "bottom": {}, "top": { "x": 180 } },
    "shape": {
      "straight": {},
      "inner_left": { "model": "inner_stairs" },
      "inner_right": { "model": "inner_stairs" },
      "outer_left": { "model": "outer_stairs" },
      "outer_right": { "model": "outer_stairs" }
    }
  }
}
```

`defaults` wird in jeden Eintrag hineingemischt, ein bloßer Modellname wie `stairs` bedeutet `minecraft:block/stairs`, und `inventory` ist das Modell für das Item in der Hand. Die drei Treppenvorlagen `stairs`, `inner_stairs` und `outer_stairs` nehmen die Texturen `bottom`, `top` und `side`.

**Die verbindenden Typen ergänzen pro Seite ein Submodell.** Ein Zaun, eine Scheibe oder eine Mauer hat einen Boolean je Richtung, und ein `true` klebt ein weiteres Modell an den Pfosten, statt ihn zu ersetzen:

```json
{
  "forge_marker": 1,
  "defaults": {
    "model": "fence_post",
    "textures": { "texture": "mypack:blocks/ruby_planks" },
    "uvlock": true
  },
  "variants": {
    "blocks": {
      "oak": { "textures": { "texture": "mypack:blocks/ruby_planks" } },
      "birch": { "textures": { "texture": "mypack:blocks/pale_planks" } }
    },
    "north": { "true": { "submodel": { "north": { "model": "fence_side", "uvlock": true } } }, "false": {} },
    "east": { "true": { "submodel": { "east": { "model": "fence_side", "y": 90, "uvlock": true } } }, "false": {} },
    "south": { "true": { "submodel": { "south": { "model": "fence_side", "y": 180, "uvlock": true } } }, "false": {} },
    "west": { "true": { "submodel": { "west": { "model": "fence_side", "y": 270, "uvlock": true } } }, "false": {} }
  }
}
```

Die Vorlagen sind `fence_post` und `fence_side` mit einer `texture`; `wall_post` und `wall_side` mit einer `wall`, dazu `block` für den Fall ohne Pfosten; und `pane_post`, `pane_side`, `pane_side_alt`, `pane_noside` und `pane_noside_alt` mit einer `pane` und einer `edge`. Alle wollen `"uvlock": true`.

Der Rest kommt mit einer einzigen Vorlage aus. `cube_all` nimmt ein `all` und ist das, was ein `basic`-, `ore`-, `falling`- oder `leaves`-Block will. `cube_column` nimmt ein `end` und ein `side`, das ist ein `log`, gedreht über `axis`. `cross` nimmt ein `cross` und ist das, was ein `flower`, `cane` oder `sapling` will; ein `crop` nutzt eigene Modelle je Stufe. Eine Stufe braucht zwei eigene Modelle, eine untere und eine obere Hälfte, da sie als Form statt als Würfel gezeichnet wird.

**Das Beispielpack ist die ausgearbeitete Referenz.** [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) bringt für jeden Typ in der Tabelle oben eine Definition, einen Blockstate und Modelle mit, im Vanilla- wie im Forge-Format, eine nicht offensichtliche Form ist also schneller kopiert als hergeleitet.

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
  "extends": "mypack:textures/blocks/panel_template",
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
| `rows` | ja, oder geerbt | Liste von Text | | Eine Zeichenkette je Pixelzeile, ein Zeichen je Pixel, von oben nach unten |
| `palette` | ja, oder geerbt | Objekt | | Ein Zeichen zu einer Farbe, `#RRGGBB` oder `#AARRGGBB` |
| `extends` | nein | eine andere Pixelkarte | | Die Karte, von der diese ausgeht |
| `tint` | nein | Objekt mit `from` und `to` | | Färbt alles Geerbte entlang einer Rampe zwischen zwei Farben um |
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

**Welche von zwei Texturen die Vorlage ist**, entscheidet sich daran, welche mehr Unterschiede festhält, nicht daran, welche zuerst gezeichnet wurde. Eine Variante gibt jedem Zeichen eine Farbe, also wird jedes Pixel, das die Vorlage gleich benennt, in der Variante auch gleich. Ein Erz auf Stein kann die `rows` des Steins deshalb nicht erben: Der Stein nennt die Sprenkelstellen schlicht Stein, und nichts, was eine Variante schreiben kann, spaltet ein Zeichen in zwei. Andersherum geht es auf. Nimm das Erz als Vorlage, dann haben Steintöne und Erztöne je eigene Zeichen, und ein zweites Erz sind vier Farben:

```json
{
  "extends": "meinpack:textures/blocks/ore_template",
  "palette": { "4": "#768291", "5": "#5E6977", "6": "#66717F", "7": "#848F9D" }
}
```

Eine Variante, die wirklich ein anderes Muster will, bringt wie oben eigene `rows` mit und erbt dann nur noch die Palette. Das lohnt, wenn es auf die Farben ankommt und die Form nebensächlich ist; kommt es auf die Form an, gehört die Form in die Vorlage und die Varianten nennen Farben.

**Eine Vorlage muss gar keine Textur sein.** Eine Karte wird dem Spiel nur gereicht, wenn ihr Pfad auf `.png` endet. Eine Vorlage unter `textures/blocks/ore_template.json` ist für das Spiel also unsichtbar und nur zum Erben da, während eine unter `textures/blocks/ore_template.png.json` auch Anfragen nach `ore_template.png` beantworten würde. Benenne eine geteilte Form ohne das `.png`, dann kann sie niemand versehentlich anfordern.

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

**Eine Vorlage lässt sich auch einfärben, statt sie umzumalen.** `tint` nennt zwei Farben und färbt alles, was die Karte erbt, entlang der Rampe zwischen ihnen um. Die Helligkeit jeder geerbten Farbe ist ihr Platz auf dieser Rampe: Schwarz landet auf `from`, Weiß auf `to`, und jeder Ton dazwischen wird anteilig gemischt. Die Transparenz bleibt unangetastet. Damit sind eine graue Vorlage und zwei Farben eine vollständige Variante:

```json
{
  "extends": "meinpack:textures/items/materials/ingot.png",
  "tint": { "from": "#626669", "to": "#DBDFE2" }
}
```

`from` darf fehlen, dann ist es Schwarz und der Tint wird zur gewöhnlichen Multiplikation, also zu dem, was ein `tintindex` beim Zeichnen tut. Der Unterschied: Dieser hier wird einmal in die PNG gezeichnet und zwischengespeichert, kostet also pro Bild nichts und erreicht auch eine Textur, die sonst niemand einfärbt. Dafür kann er einem Biom nicht folgen, wie `grass` oder `foliage` es können.

Die Vorlage bleibt eine gewöhnliche Karte: Öffne sie, sieh sie dir an, sie zeichnet sich als das Grau, das sie ist. Beide Farben nehmen `#RRGGBB`, `#AARRGGBB` oder ein vorangestelltes `0x`, und ein Wert, der nichts davon ist, lässt die Karte lieber ungezeichnet, als sie falsch zu färben. Ein Tint wird vererbt wie alles andere, und der erste die Kette hinunter gewinnt, der eigene Tint einer Variante schlägt also den der Vorlage. Auf einer Bildvorlage wirkt er ebenfalls, dort nach den Farbtauschen der Palette.

**Ein Tint ist eine Rampe zwischen zwei Farben** und taugt darum nur für eine Textur, deren Töne auf einer solchen liegen. Eine Form mit zwei zusammenhanglosen Bereichen, der Stein eines Erzes gegen seine Sprenkel, ist das nicht und will stattdessen ihre Palette ausgeschrieben.

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

**Niemand muss diese Dateien von Hand schreiben.** Das Repository liefert in [`pixelmap/`](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/tree/1.12.2-1.0-Release/pixelmap) Skripte für den ganzen Weg hin und zurück: `png_to_pixelmap.py` macht aus einer PNG eine Karte, `convert_pack.py` erledigt das für jede Textur eines Packs, und `verify_pack.py` zeichnet die Karten eines umgewandelten Packs und vergleicht sie mit den PNGs, aus denen sie stammen, damit man einer Umwandlung trauen kann, bevor man die Originale beiseitelegt.

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

`<namespace>/items/*.json`

Der Pfad der Datei ist der Registry-Name des Items, `mypack/items/ruby.json` registriert also `mypack:ruby`. Die Schlüssel in `variants` benennen die Metadatenwerte dieses einen Items, und das Modell zu jedem liegt unter `models/item/ruby/<Schlüssel>.json`.

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
      "meta": 0,
      "maxSize": 64,
      "rarity": "rare",
      "healAmount": 6,
      "saturation": 0.8,
      "oreDict": ["foodRuby"],
      "potion": "minecraft:speed,600,1"
    },
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
| `variants` | ja | Objekt aus Variantenname zu Variante |, | Ein Eintrag pro Metadatenwert. Der Schlüssel benennt diesen Wert im Blockstate, im Modellpfad und im Sprachschlüssel. Der Registry-Name kommt aus dem Pfad der Datei selbst |
| `type` | nein | einer der Typen oben | `basic` | Welchen Typ das Item annimmt |
| `creativeTab` | nein | Tab-Name | keiner | Der Tab, in dem es auftaucht |
| `material` | tool, armor | Materialname | keiner | Aus welchem deiner Materialien es gemacht ist |
| `toolClass` | tool | `pickaxe`, `axe`, `shovel`, `sword` | keiner | Welches Werkzeug es ist |
| `slot` | armor | `head`, `chest`, `legs`, `feet` | keiner | Wo es getragen wird. `helmet`, `chestplate`, `leggings` und `boots` gehen auch |
| `eat` | food | boolean | `false` | Nutzt die Ess-Animation |
| `alwaysEdible` | food | boolean | `false` | Lässt sich auch bei voller Hungerleiste essen |
| `useDuration` | nein | int, Ticks | `32` | Wie lange das Benutzen dauert |
| `attackSpeed` | nein | float | passend zur Werkzeugklasse | Für `tool` das Angriffstempo-Attribut, ein Schwert liegt bei `-2.4` |
| `cooldown` | nein | int, Ticks | `0` | Für `food`, `drink` und `potion`: wie lange das Item nach dem Verzehr die erneute Benutzung verweigert |
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
| `oreDict` | nein | Liste von Ore-Dictionary-Namen | keine | Ore-Dictionary-Namen, unter denen diese Variante eingetragen wird |
| `potion` | food, drink | `potion,duration,amplifier` | keiner | Ein Effekt, der beim Essen oder Trinken der Variante angewandt wird. Ein vierter Teil, `true`, macht ihn umgebend. Ein guter Effekt wird im Tooltip genannt |

## Flüssigkeiten

`<namespace>/fluids/*.json`

Der Pfad der Datei ist der Registry-Name der Flüssigkeit, sofern `name` ihn nicht überschreibt.

```json
{
  "name": "molten_ruby",
  "still": "mypack:blocks/molten_ruby_still",
  "flow": "mypack:blocks/molten_ruby_flow",
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
| `block` | nein | Objekt |, | Der Flüssigkeitsblock. `material` (`water`), `flammability` (`0`), `fireSpread` (`0`), `quantaPerBlock` (`0`), `potions` (keine, eine Liste von Effekten für alles, was darin steht, je geschrieben als `potion,duration,amplifier`, mit einem optionalen vierten Teil `true` für einen umgebenden) |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

## Materialien, Tabs, Sounds, Ore Dictionary

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

`<namespace>/tabs/*.json`

Der Pfad der Datei ist der Name des Tabs, sofern `label` ihn nicht überschreibt, und Blöcke und Items nennen ihn in `creativeTab`.

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `label` | nein | string | der Dateiname | Die Id des Tabs: Blöcke und Items nennen sie in `creativeTab`, der angezeigte Name kommt aus `itemGroup.<label>` in den Sprachdateien |
| `icon` | nein | Itemname | keiner | Das Item, das auf dem Tab abgebildet ist |

`<namespace>/sounds/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

Das Vanilla-Format von `sounds.json`, ein Pack kann also eigenes Audio mitbringen.

`<namespace>/oredict/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

Fügt Ore-Dictionary-Namen zu Items hinzu, die es schon gibt. Jeder Schlüssel ist ein Ore-Dictionary-Name und sein Wert die Items, die darunter eingetragen werden, eigene feste Schlüssel hat eine solche Datei also nicht. Eigene Blöcke und Items eines Packs nennen ihre stattdessen im `oreDict` der Variante.

```json
{
  "_note": "ruby equivalents",
  "gemRuby": ["mypack:ruby", "mypack:polished_ruby:1"],
  "oreRuby": ["mypack:ruby_ore", "minecraft:redstone_ore"]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er tut |
| --- | --- | --- | --- | --- |
| ein Ore-Dictionary-Name | ja | Liste von Itemnamen | | Die Items, die darunter eingetragen werden. Metadaten als dritter Teil, `"mypack:ruby:1"` |
| ein Name, der mit `_` beginnt | nein | beliebig | | Wird übersprungen, eine Datei kann also eine Notiz an sich selbst tragen |

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
| `effects` | ja | Liste von Objekten |, | Siehe unten |

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

Eine vom Pack definierte Gefahr: benannte Blöcke und Items belasten Spieler, die in der Nähe stehen oder sie bei sich tragen, in Stufen; jede Stufe bringt Effekte und wiederkehrenden Schaden. Eine Datei definiert eine Gefahr, mehrere laufen nebeneinander. Die Vorgabewerte der Schlüssel entsprechen der Strahlung von Immersive World.

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

| Schlüssel | Pflicht | Wert | Vorgabe | Wirkung |
| --- | --- | --- | --- | --- |
| `blocks` | eines von beiden | Liste aus `block` oder `block=stufe` | | Blöcke, die einen Spieler in der Nähe belasten. Ohne Stufe gilt 1 |
| `items` | eines von beiden | Liste aus `item` oder `item=stufe` | | Items, die einen Spieler belasten, der sie trägt |
| `levels` | ja | Liste von Stufen | | Die Schwereleiter, der erste Eintrag ist Stufe 1. Ein Spieler bekommt die höchste erreichte Stufe |
| `immunity` | nein | Trankname | keine | Ein Effekt, dessen Träger gar nicht belastet wird |
| `scanInterval` | nein | Ticks | `20` | Wie oft Umgebung und Inventar geprüft werden |
| `range` | nein | Blöcke | `10` | Wie weit ein Block wirkt, als Kugel |
| `sourcesForNextLevel` | nein | int | `0` | So viele Quellen einer Stufe in der Nähe heben sie um eine weitere an. `0` schaltet das ab |
| `skipsCreative` | nein | boolean | `true` | Kreativ- und Zuschauerspieler bleiben verschont |

Jede Stufe:

| Schlüssel | Pflicht | Wert | Vorgabe | Wirkung |
| --- | --- | --- | --- | --- |
| `effect` | ja | Trankname | | Der Effekt, der die Stufe am Spieler markiert. Seine Anwesenheit steuert den Schaden, es sollte also einer sein, den das Pack dafür definiert |
| `damage` | nein | halbe Herzen | `0` | Schaden alle `damageInterval` Ticks, solange die Stufe anliegt. Er ignoriert Rüstung |
| `damageInterval` | nein | Ticks | `160` | Wie oft der Schaden fällt |
| `effects` | nein | Liste von Effekten | keine | Zusätzliche Effekte, gleiche Form wie bei Trankarten. Ohne `duration` folgen sie dem Prüfintervall |

Die Stufeneffekte halten etwas über die nächste Prüfung hinaus, Weggehen lässt sie also von selbst auslaufen. Der Tod durch den Schaden liest seine Meldung aus `death.attack.rdpl.<dateiname>`, die die Sprachdateien des Packs liefern.

## Dorfbewohner und Handel

`<namespace>/villagers/*.json`

Der Pfad der Datei ist der Registry-Name des Berufs, `mypack/villagers/jeweller.json` registriert also `mypack:jeweller`, den ein Handel dann in `profession` nennt.

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

`<namespace>/trades/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

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

`<namespace>/entities/*.json`

Der Pfad der Datei ist der Registry-Name der Variante, `mypack/entities/angry_cow.json` registriert also `mypack:angry_cow`, und genau darauf beziehen sich `becomes`, das Spawn-Ei und der Weltspeicher.

Eine Datei hier macht aus einer vorhandenen Entity eine neue. Sie ist eine echte Entity für sich: eigener Registry-Name, eigener Name in der Welt, eigenes Spawn-Ei und eine eigene Beutetabelle, wenn du ihr eine gibst – aufgebaut auf dem Verhalten einer anderen Entity, statt sie zu ersetzen. An der Entity, die sie kopiert, ändert sich nichts.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht.

```json
{
  "entity": "minecraft:cow",
  "name": "Angry Cow",
  "showName": false,
  "texture": "mypack:textures/entity/angry_cow.png",
  "lootTable": "mypack:entities/angry_cow",
  "profession": "mypack:jeweller",
  "career": 1,
  "baby": 0.05,
  "becomes": [
    { "variant": "mypack:angry_cow", "weight": 95 },
    { "variant": "mypack:little_angry_cow", "weight": 5 }
  ],
  "sounds": { "ambient": "entity.cow.ambient", "hurt": "entity.cow.hurt", "death": "entity.cow.death" },
  "soundVolume": 1.0,
  "soundPitch": 1.0,
  "immuneTo": ["fall", "drown", "explosion", "magic", "cactus", "lava", "wither", "starve", "anvil", "inWall"],
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
  "biomeTypes": ["PLAINS"],
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
| `lootTable` | nein | `namespace:entities/<name>` | die der Basis | Was sie droppt. Ohne das droppt sie, was die kopierte Entity droppt |
| `profession` | nein | `namespace:name` | zufällig | Bei einem Dorfbewohner der Beruf, den er ausübt |
| `career` | nein | int | zufällig | Welche Laufbahn innerhalb dieses Berufs, ab 1 aufwärts |
| `baby` | nein | boolean oder 0,0 bis 1,0 | `false` | Wie oft eines jung erscheint, und es bleibt dabei. `true` heißt immer, eine Zahl heißt dieser Anteil |
| `becomes` | nein | Liste | keine | Andere Varianten, zu denen dieses beim Erscheinen werden kann, nach Gewicht. Siehe unten |
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
| `throws` | nein | boolean | `false` | Wirft aus der Entfernung, was es in der Hand hält, und zündet es an und zieht sich zurück, wenn das TNT ist. Braucht `hostile` |
| `throwAmmo` | nein | int | keine | Wie viele es zu werfen hat. Weggelassen geht ihm nie etwas aus |
| `throwReload` | nein | int, Sekunden | `explosionFuse` | Wie lange die Hand leer bleibt, bis es das nächste zieht |
| `throwRetreat` | nein | int, Sekunden | `explosionFuse` | Wie lange es nach einem Wurf auf Abstand bleibt, ehe es sich wieder umdreht |
| `throwPower` | nein | float | `1.0` | Wie kräftig es wirft. Verdoppeln verdoppelt ungefähr die Weite |
| `throwArc` | nein | float | `0.35` | Wie steil der Wurfbogen ausfällt. Höher hängt länger, nahe null ist ein flacher Wurf, unter null wirft es nach unten |
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

**Eine Kreatur mit Haltbarkeitsdatum.** `despawnAfter` zählt in Sekunden ab dem Moment, in dem eine Kreatur zum ersten Mal in die Welt kommt, und nimmt sie still fort, wenn die Zeit um ist: kein Tod, kein Drop, kein Geräusch, genau als wäre sie weggewandert und weggeräumt worden. Die Uhr wird in die Kreatur selbst geschrieben, sie läuft also über Speichern und Laden hinweg weiter, statt jedes Mal neu zu beginnen, wenn ein Chunk zurückkommt.

Sie ist eine Sache für sich und kein Anstupsen der Regeln, über die `despawns` und `persistent` bestimmen. Die beiden entscheiden, ob das Spiel eine Kreatur wegräumen darf, weil sie weit von allen entfernt ist; diese hier ist ein Versprechen, dass sie zu einer festen Zeit geht, ganz gleich was sonst gilt. Eine Kreatur darf `persistent` sein und trotzdem ein Haltbarkeitsdatum haben – genau das willst du für etwas, das für einen Kampf oder ein Ereignis gerufen wurde und es nicht überdauern soll.

Die Uhr läuft nach der Weltzeit, sie pausiert also, wenn niemand spielt, und zählt die Minuten nicht mit, die ein Chunk entladen verbracht hat.

`scale` ändert Modell und Hitbox auf beiden Seiten, du triffst also das, was du siehst. Eine Kreatur, die ihre Größe selbst ändert – ein Tier, das heranwächst, oder ein Zombie, der ein Kind ist –, wird um die Größe herum skaliert, die sie sich gewählt hat, damit sich beides nicht in die Quere kommt. `angryScale` lässt sie anschwellen, solange sie ein Ziel hat, und bringt sie auf `scale` zurück, sobald sie es verliert. Da dem Client nie mitgeteilt wird, was eine Kreatur jagt, trägt das Sprint-Flag diese Nachricht hinüber; es wird bei einer Variante gesetzt, die `angryScale` nutzt, und sonst bei keiner – ein Mod, der bei deinen Varianten das Sprinten ausliest, sieht es also wechseln. In eine niedrige Decke hineinzuwachsen ist möglich, genauso wie bei einem wachsenden Schleim, halte den Unterschied also im Rahmen.

Eine Variante droppt das, was die kopierte Entity droppt, weil die Beutetabelle im Code dieser Entity festgeschrieben ist und nicht über den Namen nachgeschlagen wird. `lootTable` zeigt auf eine eigene Tabelle, die du dann wie jede andere unter `loot_tables/entities/<name>.json` mitlieferst.

Eine `texture` wird anstelle der Textur eingebunden, die die Entity sonst nutzen würde, egal welchen Renderer sie erbt, sie funktioniert also bei Mod-Entities genauso wie bei Vanilla-Entities. Sie muss zu dem Modell passen, auf das sie gezeichnet wird, denn das Modell ist das der Basis-Entity: ein Skin, keine neue Form. Layer behalten ihre eigenen Texturen, Rüstung sieht auf einem umgeskinnten Zombie also weiter wie Rüstung aus.

Rüstung wird überhaupt nur auf einer Entity gezeichnet, deren Renderer einen Rüstungs-Layer hat, und das heißt in dieser Version: die humanoiden Mobs und die Dorfbewohner. Eine Variante einer Kuh oder einer Spinne kann Rüstung tragen und bekommt auch deren Schutz, nur zeichnet sie niemand – `armor` unter `attributes` ist deshalb meist der sauberere Weg, so eine Kreatur zäh zu machen. `hideArmor` ist für den anderen Fall: ein Humanoider, der die Rüstung in seinen Slots behalten soll, für den Schutz oder für einen Mod, der sie ausliest, ohne dass man sie sieht.

`hostile` nimmt der Kreatur auch das Verhalten weg, das sie hat weglaufen lassen: Ein Tier, das Spielern ausgewichen ist oder bei Verletzung in Panik geriet, tut beides nicht mehr, sobald es feindselig ist – sonst würde es vor dem fliehen, was es eigentlich angreifen soll. Es braucht eine Entity, die auf dem Boden läuft, weil es dasselbe Angriffsverhalten nutzt, das Vanilla seinen eigenen Mobs gibt. Eine fliegende oder schwimmende Basis wird protokolliert und in Ruhe gelassen. `passive` greift weiter, erreicht aber nur Verhalten, das so gebaut ist, wie Vanilla es baut: Einem Mod, dessen Feindseligkeit in seinem eigenen Tick- oder Schadenscode steht, kann ein Pack sie nicht ausreden.

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

Der Wurf leert die Hand, denn sie hat das Ding ja geworfen. Danach bleibt sie `throwRetreat` lang auf Abstand, zieht nach `throwReload` das nächste und dreht sich wieder zu ihrem Ziel: ein Kreislauf aus Werfen, Zurückweichen, Nachladen, Herangehen. Gibst du ihr ein `throwAmmo`, endet dieser Kreislauf, wenn die Zahl aufgebraucht ist – die Hand bleibt dann leer und ihr gewöhnlicher Angriff übernimmt. Lässt du `throwAmmo` weg, geht ihr nie etwas aus.

Die Zahl wird in die Kreatur geschrieben, sie füllt sich also nicht wieder auf, weil ein Chunk entladen und neu geladen wurde. Alles, was kein TNT ist, fliegt als Item und landet – ein Pionier, der Steine oder verrottetes Fleisch schleudert, ist so leicht gemacht wie einer mit Sprengstoff.

`explosionFuse` bleibt die Lunte am geworfenen TNT und springt für jeden der beiden Zeitwerte ein, den du weglässt, eine vor diesen Schlüsseln geschriebene Variante verhält sich also genau wie zuvor.

Wie der Wurf selbst fliegt, bestimmen `throwPower` und `throwArc`. Das erste ist ein Faktor auf den Schwung, und da der Schwung ohnehin mit der Entfernung wächst, verlängert ein höherer Wert die Weite, ohne zu ändern, wie lange der Wurf in der Luft hängt. Das zweite ist die Höhe und ändert die Form: hoch, und er segelt im Bogen über eine Mauer und lässt sich Zeit; nahe null, und er wird flach geschleudert und landet fast sofort; unter null, und er wird auf etwas darunter hinabgeworfen. Beide lassen die Lunte in Ruhe, eine im Bogen geworfene und eine flache Ladung gehen also gleich viele Sekunden nach dem Verlassen der Hand hoch – und das entscheidet, ob eine über den Köpfen zerplatzt oder erst landet und wartet. Wie weit sie wirft, sagt ihre `followRange`, und näher als drei Blöcke geht sie wie gewohnt zum Angriff über, sie ist also auf Distanz gefährlich und im Nahkampf gewöhnlich.

**Ein Ei oder Spawner, der Verschiedenes liefert.** Eine Variante ist eine eigene Klasse, für sich allein erscheint sie also immer genau als das, was sie sagt. `becomes` bricht das auf: eine Liste von Varianten, zu denen diese beim Erscheinen werden kann, jede mit einem Gewicht, für jede Kreatur einzeln entschieden.

```json
{
  "becomes": [
    { "variant": "meinpack:walker", "weight": 95 },
    { "variant": "meinpack:little_walker", "weight": 5 }
  ]
}
```

Sich selbst zu nennen ist der Weg, so zu bleiben, wie man ist, und die Gewichte sind die Chancen. Setz das auf `meinpack:walker`, und ein Ei, ein Spawner und ein Spawn-Eintrag liefern meist Walker mit gelegentlich einem kleinen – so wie ein Zombie-Ei ab und zu ein Baby liefert. Es geschieht, während die Kreatur in die Welt kommt, gilt also für Eier, Spawner, `/summon` und natürliches Spawnen gleichermaßen, und was ankommt, ist eine echte Kreatur der gewählten Variante mit allem, was diese Variante sagt. Eine so erreichte Variante wandelt sich nicht noch einmal, zwei Varianten dürfen sich also gegenseitig nennen, ohne sich im Kreis zu drehen.

**Wo `baby` hineinpasst.** Das Spiel hat keinen eigenen Baby-Zombie: Es gibt einen Zombie, der beim Erscheinen auswürfelt, ob er ein Kind ist. `baby` sagt, wie oft, `"baby": 0.05` ist also die Vanilla-Gewohnheit und `"baby": true` heißt immer. Beide sind zwei Wege zur selben Sache, und welchen du nimmst, hängt vom Unterschied ab, den du willst: `baby` allein gibt eine Variante, die manchmal jung ist, `becomes` gibt mehrere Varianten, die sich in allem unterscheiden dürfen, und beides zusammen ist auch in Ordnung.

## Dorfgrundstücke

`<namespace>/villages/*.json`

Der Pfad der Datei ist der Name des Grundstücks, den `villagePieces` dann nennen kann, um es zu behalten oder wegzulassen.

Eine Datei hier fügt ein Stück hinzu, das Dörfer bauen können, neben den Vanilla-Stücken. Zwei Sorten, ausgewählt mit `type`.

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
  "crops": ["simplecorn:corn", "minecraft:wheat"],
  "edge": "minecraft:log",
  "soil": "minecraft:farmland",
  "water": true,
  "rowWidth": 2,
  "structure": "mypack:blacksmith_shed",
  "integrity": 100,
  "villagers": 2,
  "villagerEntity": "mypack:jeweller",
  "villagerX": 1,
  "villagerY": 1,
  "villagerZ": 1,
  "ground": "minecraft:dirt",
  "requires": ["mypack"]
}
```

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
| `width` | allen | int | `7` | Größe quer zum Weg |
| `height` | allen | int | `4` | Höhe, die über dem Boden freigeräumt wird |
| `depth` | allen | int | `9` | Größe vom Weg weg |
| `crops` | farm | Liste von Blocknamen | Weizen | Eine pro Block gepflanzt, in zufälliger Wachstumsstufe |
| `edge` | farm | Blockname | `minecraft:log` | Der Rahmen um das Grundstück |
| `soil` | farm | Blockname | `minecraft:farmland` | Woraus die Reihen bestehen |
| `water` | farm | boolean | `true` | Eine Wasserrinne zwischen die Reihen legen |
| `rowWidth` | farm | int | `2` | Wie breit jede Erdreihe ist |
| `structure` | template | `namespace:name` | keine | Die Vorlage, die gesetzt wird, oder eine deiner Strukturkarten, die dann die Größe des Grundstücks bestimmt |
| `integrity` | template | 1 bis 100 | `100` | Prozentsatz der Blöcke der Vorlage, die erscheinen |
| `lootTable` | template | `namespace:pfad` | keine | Die Beutetabelle, aus der jede Truhe in der gesetzten Vorlage beim ersten Öffnen gefüllt wird. Ein Grundstück, das einen Strukturplan nennt, bleibt unberührt |
| `villagers` | allen | int | `0` | Wie viele Leute das Grundstück spawnt |
| `villagerEntity` | allen | `namespace:name` | ein Dorfbewohner | Wer dort wohnt, etwa eine eigene Entity-Variante |
| `villagerX` | allen | int | `1` | Wo sie erscheinen, quer über das Grundstück |
| `villagerY` | allen | int | `1` | Wo sie erscheinen, über dem Boden |
| `villagerZ` | allen | int | `1` | Wo sie erscheinen, in das Grundstück hinein |
| `ground` | allen | Blockname | `minecraft:dirt` | Was am Hang darunter aufgefüllt wird |
| `requires` | allen | Liste von Mod-IDs oder Pack-Namespaces | keine | Das Grundstück bleibt weg, wenn nicht alle da sind |

Jedes Pack-Grundstück wird den Dörfern als ein Eintrag angeboten, `weight` entscheidet also, welches deiner Grundstücke gezogen wird, sobald ein Dorf nach einem fragt. Welches Grundstück eine Platzierung genutzt hat, steht in den eigenen Daten des Dorfes, es baut sich beim Laden also korrekt wieder auf.

## Biome

`<namespace>/biomes/*.json`

Der Pfad der Datei ist der Registry-Name des Bioms, `mypack/biomes/ruby_forest.json` registriert also `mypack:ruby_forest`. `name` ist nur das, was der Spieler sieht.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht.

```json
{
  "name": "Ruby Forest",
  "id": 200,
  "types": ["FOREST", "DENSE", "WET"],
  "temperature": 0.7,
  "rainfall": 0.8,
  "rain": true,
  "snow": false,
  "baseHeight": 0.15,
  "heightVariation": 0.25,
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
    "villageSpawn": true,
    "strongholds": false,
    "playerSpawn": true
  },
  "villageType": "oak",
  "minHeight": 100,
  "maxHeight": 156,
  "replaces": ["minecraft:plains", "minecraft:forest"],
  "skyStone": "minecraft:end_stone",
  "skyIslands": 0.2,
  "skyThickness": 2.0,
  "requires": ["mypack"]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `name` | nein | string | der Dateiname | Name, den der Spieler sieht |
| `id` | nein | int | wird vergeben | Feste Biom-ID. Setz sie nur, wenn du sie stabil brauchst |
| `temperature` | nein | float | `0.5` | Unter 0.15 schneit es, über 1.0 ist es wüstenheiß |
| `rainfall` | nein | float, 0 bis 1 | `0.5` | Wie feucht es ist |
| `rain` | nein | boolean | `true` | Ob es überhaupt Wetter gibt |
| `snow` | nein | boolean | `false` | Ob Regen als Schnee fällt |
| `baseHeight` | nein | float | `0.1` | Geländehöhe. Meereshöhe ist 0, Ebenen 0.125 |
| `heightVariation` | nein | float | `0.2` | Wie hügelig es ist |
| `topBlock` | nein | Blockname | Gras | Der Oberflächenblock |
| `fillerBlock` | nein | Blockname | Erde | Direkt unter der Oberfläche |
| `stoneBlock` | nein | Blockname | Stein | Die Masse des Untergrunds |
| `types` | nein | Liste von Dictionary-Typen | keine | Registriert das Biom unter diesen, etwa `FOREST`, `COLD`, `WET` oder `NETHER`, damit andere Mods es finden |
| `waterColor` | nein | Hex-Farbe | `FFFFFF` | Wasserfärbung |
| `grassColor` | nein | Hex-Farbe | aus dem Klima | Grasfärbung, anstelle der Farbe, die Temperatur und Niederschlag ergäben |
| `foliageColor` | nein | Hex-Farbe | aus dem Klima | Laubfärbung, auf dieselbe Weise |
| `baseBiome` | nein | Biomname | keiner | Ein vorhandenes Biom, von dem Einstellungen kopiert werden |
| `decoration` | nein | Objekt | Vanilla-Anzahlen | Anzahlen pro Chunk. Gelesen werden `trees`, `flowers`, `grass`, `deadbush`, `mushrooms`, `bigmushrooms`, `reeds`, `cacti`, `sand`, `gravel`, `clay` und `waterlily`, dazu `falls`, wo über null bedeutet, dass Seen und Quellen generieren, und `extratreechance`, eine prozentuale Chance auf einen Baum mehr. Jeder andere Name wird protokolliert und ignoriert |
| `spawns` | nein | Liste von Objekten | Vanilla-Liste | Siehe unten |
| `keepDefaultSpawns` | nein | boolean | `false` | Vanillas Liste neben deiner behalten |
| `spawnChance` | nein | float, unter 1 | `0.1` | Wie wahrscheinlich beim ersten Erzeugen des Landes eine weitere Herde gesetzt wird. Das Spiel würfelt weiter, solange es Erfolg hat, `1` hört also nie auf und füllt die Welt, bis kein Platz mehr ist. Alles ab 0.99 wird abgelehnt und durch 0.99 ersetzt |
| `spawnRates` | nein | Objekt aus `surfaceDay`, `surfaceNight`, `undergroundDay`, `undergroundNight` zu einem Faktor | keines | Wie oft feindliche Mobs hier spawnen, anstelle der globalen Einstellungen. Siehe unten |
| `placement` | nein | Objekt | keines | Wo es generiert. Siehe unten |
| `villageType` | nein | `oak`, `sandstone`, `acacia` oder `spruce` | keiner | Woraus ein Dorf hier gebaut wird. Leer baut mit Eiche, wie auch ohne den Schlüssel |
| `minHeight` | nein | int | keiner | Unterste y, ab der dieses Biom als 3D-Biom übernimmt. Wird eine der beiden Höhen gesetzt, wird das Biom zu einem Band: außerhalb behält die Säule ihr eigenes Biom, innerhalb meldet jede 4 mal 4 mal 4 große Zelle der Welt dieses. Nur auf Rubic-Welten, und beim Erzeugen des Landes angewandt, vorhandenes Land behält also seines |
| `maxHeight` | nein | int | keiner | Oberste y dieses Bandes |
| `replaces` | nein | Liste von Biomnamen | jedes Biom | Beschränkt das Band auf Säulen, deren eigenes Biom hier genannt ist, ein Alpenband kann also über Bergen liegen und sonst nirgends |
| `skyStone` | nein | Blockname | die Welteinstellung | Der Block, aus dem Himmelsinseln unter ihrer Oberfläche bestehen, wo dieses Biom gilt. Bei einem Band malen `topBlock` und `fillerBlock` auch die Inseloberfläche, ein Band ist also der Weg zu einem eigenen Stück Himmel |
| `skyIslands` | nein | Zahl, `-1` bis `1` | die Welteinstellung | Die Inselschwelle, wo dieses Biom gilt. Niedriger sammelt mehr Land |
| `skyThickness` | nein | Zahl, `0` oder mehr | die Welteinstellung | Wie massiv die Inseln sind, wo dieses Biom gilt |
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

**Temperatur nach Höhe.** Ein Biom kühlt mit der Höhe ab, was den Schnee auf Berggipfel bringt und Regen oberhalb einer Linie beendet. Drei Schlüssel der Gruppe `terrain` verschieben diese Kurve, was auf einer Rubic-Welt zählt, wo der Boden weit über oder unter der Höhe liegen kann, die das Spiel annimmt. Die Standardwerte sind das, was das Spiel tut, ein Pack, das sie in Ruhe lässt, ändert also nichts.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "biomeTemperatureCenterY": 64,
    "biomeTemperatureHeightFactor": -0.001667,
    "biomeTemperatureScaleMaxY": 256
  }
}
```

| Schlüssel | Wert | Standard | Was er macht |
| --- | --- | --- | --- |
| `biomeTemperatureCenterY` | int | `64` | Die Höhe, ab der die Kurve gemessen wird. Auf ihr und darunter meldet ein Biom seine eigene `temperature` unverändert |
| `biomeTemperatureHeightFactor` | float | `-0.001667` | Wie stark sich die Temperatur je Block oberhalb dieser Höhe verschiebt, die spieleigenen 0,05 über 30 Blöcke. Negativ kühlt mit der Höhe ab, positiv wärmt |
| `biomeTemperatureScaleMaxY` | int | `256` | Die Höhe, bei der die Kurve endet, damit eine Welt, die höher ist als die des Spiels, nicht bis zur Decke weiter abkühlt |

## Dimensionen

`<namespace>/dimensions/*.json`

Der Pfad der Datei benennt die Dimension für `suffix`, dessen Standard `DIM_<name>` ist. Die Dimension selbst wird über ihre `id` gefunden, das ist also die Zahl, auf die sich alles andere bezieht.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht.

```json
{
  "id": 12,
  "suffix": "DIM_ruby",
  "keepLoaded": false,
  "requires": ["mypack"],
  "terrain": {
    "type": "overworld",
    "generatorOptions": "",
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
    "respawnDimension": 0,
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
| `renderSky` | nein | boolean | `true` | Aus zeichnet weder Himmel noch Sonne, Mond oder Sterne – es bleibt die Nebelfarbe |
| `renderClouds` | nein | boolean | `true` | Aus werden keine Wolken gezeichnet |
| `renderWeather` | nein | boolean | `true` | Aus werden weder Regen noch Schnee gezeichnet |

Farben und die drei Render-Schalter sind alles, was geboten wird. Etwas Eigenes dort oben zu zeichnen – eine bemalte Kuppel, eine eigene Sonne und einen eigenen Mond – braucht weiterhin Java.

## Portale und Tore

`<namespace>/blocks/*.json`

Ein Portal ist eine gewöhnliche Blockdefinition, es gilt also dieselbe Pfadregel und der Pfad der Datei ist der Registry-Name des Blocks.

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
    "sound": "block.portal.travel",
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
| `walkIn` | nein | boolean | `false` | Wer hineinläuft, reist, so wie bei einem Netherportal. Aus, wird es von Hand benutzt |

### Portalrahmen

`<namespace>/portalframes/*.json`

Der Pfad der Datei ist der Registry-Name des Rahmens, den eine Dimension dann in `frames` nennt.

Ein Rahmen ist ein Bild dessen, was ein Spieler bauen muss, und sonst nichts: Er sagt, welche Blöcke den Rand bilden und wo das Loch sitzt, und sagt nichts darüber, wohin das Portal führt. Das ist Absicht, denn eine Dimension beansprucht einen Rahmen, statt ihn zu besitzen, und zwei Dimensionen dürfen denselben beanspruchen.

```json
{
  "name": "Stehendes Tor",
  "axis": "vertical",
  "legend": { "q": "minecraft:quartz_block", "r": "meinpack:ruby_block" },
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

| Schlüssel | Pflicht | Wert | Standard | Was er bewirkt |
| --- | --- | --- | --- | --- |
| `name` | nein | Zeichenkette | der Dateiname | Der Name, der im Log erscheint |
| `axis` | nein | `vertical`, `horizontal` oder `both` | `vertical` | Ob er steht wie ein Netherportal, flach liegt wie ein Endportal oder beides darf |
| `legend` | ja | Objekt aus je einem Zeichen zu einem Block | keine | Die Blöcke, die die Zeilen verwenden dürfen. Ein Blockname mit Zuständen wird gelesen wie überall sonst |
| `rows` | ja | Liste von Zeichenketten | keine | Das Bild, oberste Zeile zuerst |
| `maxWidth` | nein | Ganzzahl | `21` | Breitestes Loch, bis zu dem ein `*` sich streckt |
| `maxHeight` | nein | Ganzzahl | `21` | Höchstes Loch, bis zu dem ein `*` sich streckt |

Drei Zeichen sind keine Blöcke. `.` ist das Loch, in dem das Portal steht, und ein Rahmen ohne eines wird abgelehnt. Ein Leerzeichen ist eine Zelle, die den Rahmen nicht kümmert, ein L-förmiger Rand entsteht also, indem man die Ecken leer lässt. `*` wiederholt: Eine Zeile, die nur aus `*` besteht, wiederholt die Zeile darüber so oft, wie der Spieler gebaut hat, und ein `*` mitten in einer Zeile wiederholt ebenso das Zeichen davor. Es darf auch gar nicht wiederholen: Das Bild mit jedem `*` gestrichen ist also das Kleinste, was zündet, und die Höchstwerte unten sind das Größte. Ein Bild ohne `*` ist genau, und der Spieler muss es so und nicht anders bauen.

Ein stehender Rahmen wird auf beiden waagerechten Achsen und in beiden Richtungen gefunden, es kommt also nicht darauf an, wie der Erbauer stand. Ein liegender wird in allen vier Drehungen gefunden.

**Wie groß er werden darf, sagt das Pack.** `maxWidth` und `maxHeight` sind das größte Loch, bis zu dem ein `*` sich streckt, und alles Kleinere bis zur Untergrenze wird angenommen – ein Pack entscheidet also selbst, ob sein Tor bei Vanillas 21 endet oder schon bei 4. Die Untergrenze ist ein Spieler: Ein stehender Rahmen wird abgelehnt, wenn sein Loch nicht mindestens 1 breit und 2 hoch werden kann, ein liegender, wenn nicht mindestens 1 mal 1, und ein Bild, das das nie erreicht, wird beim Laden mit einer Zeile im Log abgelehnt, statt ein Rahmen zu sein, durch den niemand geht.

**Ein Rahmen kostet umso mehr Suche, je mehr er sich strecken kann.** Ein Zeilen-`*` und ein Spalten-`*` zusammen heißt, dass jede Kombination bis zu beiden Höchstwerten versucht wird, ein Rahmen, der sich in beide Richtungen bis 21 streckt, sind also 441 Bilder. Die Suche gibt lieber auf, als hängen zu bleiben, und schreibt das ins Log – das Zeichen, einen Höchstwert zu senken oder eine der Streckungen zu streichen.

**Nichts verbietet einen Rahmen aus Obsidian mit Feuerzeug, doch er hat Vorrang.** Der Rahmen wird gesucht, bevor das Item selbst wirkt, ein solcher Rahmen öffnet also die Dimension des Packs dort, wo ein Netherportal gestanden hätte. Wähle einen anderen Block oder ein anderes Zündmittel, um Vanillas Portal in Ruhe zu lassen.

### Eine Dimension über einen Rahmen öffnen

`<namespace>/dimensions/*.json`

Eine Dimension öffnet sich über einen Rahmen, indem sie einen `portal`-Abschnitt trägt. Der Rahmen und das, womit er angezündet wird, wählen zusammen die Dimension – eine Rahmenform kann also je nach Zündmittel an mehrere Orte führen.

```json
{
  "id": 12,
  "portal": {
    "frames": ["meinpack:stehendes_tor"],
    "ignitedBy": "minecraft:flint_and_steel",
    "color": "#C77DFF",
    "return": "built",
    "gate": "meinpack:ruby_gate",
    "cooldown": 60,
    "platform": true,
    "sound": "block.portal.travel"
  }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er bewirkt |
| --- | --- | --- | --- | --- |
| `frames` | ja | Liste von Rahmennamen | keine | Die Rahmen, die diese Dimension öffnen |
| `ignitedBy` | nein | Itemname | `minecraft:flint_and_steel` | Was ein Spieler in der Hand hält, um einen anzuzünden |
| `color` | nein | Hexfarbe | weiß | Die Farbe, in der das Portal gezeichnet wird |
| `return` | nein | `built`, `player` oder `none` | `built` | Ob ein Rückweg gestellt, vom Spieler gebaut oder gar nicht gewährt wird |
| `gate` | nein | Torname | keiner | Ein Tor, das offen sein muss, um durchzugehen |
| `cooldown` | nein | Ganzzahl, Ticks | `60` | Bevor derselbe Spieler wieder durchgehen darf |
| `platform` | nein | boolean | `true` | Bei der Ankunft eine Landeplattform bauen |
| `platformBlock` | nein | Blockname | Stein | Woraus diese Plattform besteht |
| `sound` | nein | Soundname | keiner | Wird beim Durchgehen gespielt |
| `owned` | nein | boolean | `false` | Nur wer es angezündet hat und wen er zulässt, darf es benutzen |

Den Block, der im Loch steht, schreibt das Pack nicht. Eine Dimension mit einem `portal`-Abschnitt bekommt einen eigenen, benannt `<namespace>:portal_<dimension>`, in der Portaltextur des Spiels unter `color` gezeichnet, hineinzulaufen statt von Hand zu benutzen, und unzerstörbar. Die Farbe multipliziert die Textur, so wie ein `tintindex` es tut: `#C77DFF` behält das Violett des Nethers, `#4CFFB0` macht es giftig. Wer ein Portal will, das gar nicht die Vanilla-Textur ist, schreibt einen gewöhnlichen eigenen `portal`-Block mit eigenem Modell und einer Textur als [Pixelkarte](#texturen-als-pixelkarte), wo `tint` zwischen zwei Farben rampen kann.

`return` entscheidet, was auf der anderen Seite geschieht. `built` stellt denselben Rahmen auf, in der Größe, die der Spieler gebaut hat, und zündet ihn an, so wie Vanilla es macht. `player` baut nichts, lässt denselben Rahmen aber drüben anzünden, der Heimweg will also gefunden und gebaut werden. `none` lässt den Rahmen in jener Dimension gar nicht erst zünden, und die Reise geht nur hin.

**Ein Rahmen, mehrere Dimensionen.** Das Paar aus Rahmen und Zündmittel wählt die Dimension, dasselbe `stehendes_tor` mit Feuerzeug und mit dem eigenen Zünder eines Packs öffnet also zwei verschiedene Orte, jeden in seiner eigenen Farbe. Beanspruchen zwei Dimensionen denselben Rahmen *und* dasselbe Item, ist das ein Fehler im Pack: Die zweite wird abgelehnt und sagt es im Log, statt dass eine von beiden stillschweigend gewinnt.

Bricht ein Block des Rahmens weg, geht das Portal aus, wie in Vanilla.

`<namespace>/gates/*.json`

Der Pfad der Datei ist der Registry-Name des Tors, das ein Portal dann in `gate` nennt.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht.

```json
{
  "dimension": 12,
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
| `dimension` | ja | int |, | Die Dimension, die es bewacht |
| `name` | nein | string | der Dateiname | Wird dem Spieler angezeigt |
| `scope` | nein | `player`, `global` | `player` | Ein Spieler nach dem anderen oder die ganze Welt auf einmal |
| `open` | nein | boolean | `false` | Ob es offen startet |
| `unlock` | nein | Objekt |, | Was es öffnet. Siehe unten |
| `unlockedMessage` | nein | string | `%dim% is now open` | Wird beim Öffnen angezeigt |
| `blockedMessage` | nein | string | `You need %item% to enter %dim%` | Wird bei der Abweisung angezeigt |
| `safeReturn` | nein | boolean | `false` | Ein abgewiesener Rückweg landet trotzdem sicher irgendwo, statt abgelehnt zu werden |
| `requires` | nein | Liste von Mod-Ids oder Pack-Namensräumen | keine | Das Tor wird übersprungen, wenn nicht alle vorhanden sind |
| `portalBlocks` | nein | Liste von Blocknamen | jedes Portal | Begrenzt das Tor auf diese Portalblöcke, eine Dimension kann also eine bewachte und eine offene Tür haben |

`unlock` nimmt `hold` (ein Item, das in der Hand sein muss), `consume` mit `consumeCount` (`1`), `craft` (ein Item, das gecraftet worden sein muss), `advancement` und `killed` (ein Entity-Name; das Tor öffnet sich für den, der eine davon erlegt, ein Boss kann also den Schlüssel zu einer Welt tragen) mit `killedCount` (`1`), wenn eine nicht reicht, gezählt pro Spieler oder für die ganze Welt, je nach `scope`. Mit `killedDrops` (ein Itemname) legen die gezählten Abschüsse stattdessen dieses Item dem Erleger vor die Füße, statt das Tor zu öffnen, und die Zählung beginnt von vorn – ein Schlüssel lässt sich also erneut verdienen und an jemanden weitergeben, der nie dafür gekämpft hat; sperr dann über `hold` oder `consume` desselben Items, um es zum Schlüssel zu machen. `%item%`, `%mob%` und `%dim%` werden für dich eingesetzt. Ein Schlüssel, den ein Mob droppt, braucht hier nichts Besonderes: Gib dem Mob den Drop und sperr über `hold` oder `consume`.

## Weltvorlagen

`<namespace>/worldtemplates/*.json`

Der Pfad der Datei ist der Name der Vorlage, die die Config-Option `worldTemplate` nennen kann, um sie direkt auszuwählen.

Fasst die Gestalt einer Welt in einer Datei zusammen, ein Pack liefert also eine ganze Welt auf einmal, statt vom Spieler ein Dutzend Config-Optionen zu verlangen.

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

## Rubic-Welten

`rubicWorld` in den `terrain`-Einstellungen baut die Welt einer Dimension aus Würfeln von 16×16×16 Blöcken auf statt aus 256 Block hohen Säulen, sodass Boden und Decke sitzen können, wohin das Pack sie legt. Die Terrain-Generierung selbst ändert sich nicht – Vanillas Generator und die Weltgenerierung anderer Mods laufen wie gewohnt und liefern dasselbe Land; es gibt schlicht Welt darüber und darunter.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "rubicWorld": true,
    "worldMinHeight": -1024,
    "worldMaxHeight": 1024,
    "rubicWorldDimensions": [0, -1],
    "rubicWorldDimensionsAreBlacklist": false,
    "terrainOffset": 0
  }
}
```

Alle Schlüssel gehören zur Gruppe `terrain` und stehen wie die übrigen im `settings`-Block einer Weltvorlage:

| Schlüssel | Wert | Standard | Was er macht |
| --- | --- | --- | --- |
| `rubicWorld` | boolean | `false` | Schaltet Rubic-Welten ein |
| `worldMinHeight` | int, Vielfaches von 16 | `-64` | Der Boden der Welt |
| `worldMaxHeight` | int, Vielfaches von 16 | `320` | Die Decke der Welt |
| `rubicWorldDimensions` | Liste von ints | leer | Welche Dimensionen zu Rubic-Welten werden. Leer heißt jede |
| `rubicWorldDimensionsAreBlacklist` | boolean | `false` | Die Liste nennt stattdessen die Dimensionen, die in Ruhe gelassen werden |
| `terrainOffset` | int, nicht negatives Vielfaches von 16 | `0` | Verschiebt das ganze Vanilla-Terrainfenster nach oben. Für schlichte Schichten-Presets: Eine Flachwelt mit `272` hat ihre Oberfläche nahe y 275, über der Vanilla-Decke. Dekorationen und Strukturen, die ein Preset anfordert, generieren weiterhin auf ihren unverschobenen Höhen |

**Höhen.** `worldMinHeight` muss unter `worldMaxHeight` liegen, beide Vielfache von 16 und beide innerhalb der Reichweite, die `rubicHeightLimit` in der Config zulässt (standardmäßig `4096` Blöcke in jede Richtung; nur Config, nie ein Pack-Schlüssel). Alles andere wird mit einer Log-Zeile abgelehnt, und die Welt entsteht von `-64` bis `320`. Höhe kostet Platz: Alle 16 Blöcke sind ein weiterer Würfel in jeder Säule, Speicher, Platte und Vorgenerierungszeit wachsen also mit – die Zahlen dazu stehen im Config-Kommentar zu `rubicHeightLimit`.

**Das Terrainfenster.** Der Generator der Dimension behält seine eigene Höhe, in der Oberwelt 256 Blöcke, und dieses Fenster ist es, das `terrainOffset` verschiebt. `worldMinHeight` und `worldMaxHeight` schaffen Raum um das Fenster herum, nie darin. Eine höhere Decke hebt nicht das Land, sie fügt Himmel hinzu; ein tieferer Boden vertieft nicht die Höhlen, die der Generator geschnitten hat, er fügt Tiefenwelt hinzu. Auch der Meeresspiegel liegt im Fenster, er wandert also mit `terrainOffset` mit und stammt vom Welttyp, nicht von einem Rubic-Schlüssel. Um die Oberfläche höher in die Welt zu setzen, erhöhe `terrainOffset`. Um mehr Raum darüber oder darunter zu schaffen, verschiebe die Höhen. Jeder Würfel außerhalb des Fensters wird in jeder Säule trotzdem generiert und beleuchtet, eine höhere Decke kostet also Vorgenerierungszeit, ob sie nun gefüllt wird oder nicht, und obendrein Speicher und Platte, sobald `skyStone` sie füllt.

**Was ein verschobenes Fenster mit anderen Mods macht.** Das Populieren läuft auf der Säule, jeder Generator, den eine Mod anmeldet, läuft also weiterhin einmal je Chunk, ohne dass Koordinaten umgerechnet werden. Was sich ändert, ist der Ort, an dem seine eigene Rechnung landet. Ein Generator, der die Welt fragt, wo der Boden ist, über den obersten festen Block oder die Niederschlagshöhe, folgt dem verschobenen Terrain: Beide sind Rubic-bewusst, das deckt Bäume, Blumen und die meiste Dekoration ab. Ein Generator, der eine absolute Höhe berechnet, darunter das übliche Erzmuster mit einem zufälligen y unter 64, schreibt weiterhin auf dieser Höhe, und die ist nach einer Verschiebung Füllmaterial oder Tiefenwelt weit unter dem Land. Auch der Meeresspiegel wird nicht verschoben, ein Generator, der gegen ihn prüft, liest also den unverschobenen Wert. Diese Schreibvorgänge landen außerdem außerhalb der Würfel, die das Populieren geladen hält, und ziehen sich während des Populierens einer Säule eigene Würfel herein. Ein großer `terrainOffset` passt zu einem Pack, das seine eigene Generierung beschreibt, nicht zu einem, das auf der Weltgenerierung eines anderen Packs aufsitzt. Geht es nur um Platz, ist die Tiefe die günstigere Richtung: Unter dem Fenster liegt ein vollwertiger Generator mit eigenem Stein, Höhlen, Adern, Aquiferen und Verliesen, und er lässt die Oberfläche auf den Höhen, die jeder andere Generator voraussetzt, während der Raum über dem Fenster Kulisse ist, die ein Pack selbst ausstatten muss.

**Pro Spielstand entschieden, einmal.** Ob eine Dimension Rubic ist und welche Höhen sie hat, wird beim ersten Laden in ihren Spielstand geschrieben und gilt von da an: Eine Rubic-Welt bleibt Rubic, auch wenn das Pack verschwindet, und ihre Höhen lassen sich nachträglich nicht ändern. Andere Dimensionen als die Oberwelt übernehmen deren Höhen. Vorhandenes Anvil-Land wird nicht umgewandelt – Rubic hält sein Land in eigenen `region2d`-/`region3d`-Dateien, eine bereits als Anvil generierte Dimension fängt mit ihrem Terrain also von vorn an. Schalte es für neue Welten ein.

**Dimensionen ausnehmen.** Eine Dimension, die in `rubicWorldDimensions` fehlt, behält ihre gewöhnliche Anvil-Welt, im selben Spielstand – Rubic- und Anvil-Dimensionen mischen sich frei. Das ist die richtige Wahl für Dimensionen, deren Generatoren direkt in die Chunk-Interna schreiben, statt den gewöhnlichen Populate-Zyklus zu durchlaufen. Unabhängig von der Liste wird eine Welt, deren Server-Klassen eine andere Mod ersetzt hat, übersprungen, mit einer Log-Zeile, die es sagt.

**Raum außerhalb des Fensters.** Der Bereich des Generators behält seine gewohnte Gestalt, und der Raum, den eine Rubic-Welt darum herum schafft, wird mit dem Block gefüllt, in dem dieser Bereich endet: Stein unter der Oberwelt, Luft darüber. Eine Dimension, deren Obergrenze mit Bedrock versiegelt ist, allen voran der Nether, gilt als geschlossen, der Raum darüber bleibt also leer statt mit dem Netherrack unter ihrem Dach vollgepackt zu werden. Das Dach selbst bleibt unangetastet. `deepStone` benennt den Block für den Raum unter dem Fenster, `skyStone` den Block für den Raum darüber.

**CubicChunks.** Beide zusammen laufen nicht. Ist CubicChunks installiert und ein Pack verlangt `rubicWorld`, stoppt das Laden mit einer Meldung: CubicChunks herausnehmen, oder `rubicWorld` aus dem Pack nehmen und CubicChunks die Welten machen lassen.

**Cube-Streaming.** Vier Schlüssel der Gruppe `chunks` entscheiden, wie Cubes zu einem Spieler kommen und wann sie wieder losgelassen werden. Sie wirken nur auf Rubic-Welten, und die Standardwerte sind die Zahlen, mit denen das Subsystem abgestimmt wurde, sodass ein Pack, das sie in Ruhe lässt, nichts zahlt.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "verticalCubeLoadDistance": 8,
    "cubesSentPerTick": 649,
    "cubeGenMillisPerRound": 50,
    "cubeGCInterval": 200
  }
}
```

| Schlüssel | Wert | Standard | Was er macht |
| --- | --- | --- | --- |
| `verticalCubeLoadDistance` | int, Cubes | `8` | Wie viele Cubes über und unter einem Spieler ein Chunkloader-Ticket hält. Der gleichnamige Regler in den Grafikeinstellungen ist die Sichtweite des Klienten und wird von der spielenden Person gesetzt, nicht von einem Pack |
| `cubesSentPerTick` | int, Cubes | `649` | Wie viele Cubes ein Spieler in einem Tick geschickt bekommen darf. Höher füllt die Sichtblase schneller und macht die Pakete je Tick größer; ein Paket wird weiterhin bei 1024 Cubes oder 512 KB geteilt, je nachdem was zuerst kommt |
| `cubeGenMillisPerRound` | int, Millisekunden | `50` | Wie lange ein Tick Cubes generieren darf, auf die Spieler warten |
| `cubeGCInterval` | int, Ticks | `200` | Wie oft Cubes losgelassen werden, die niemand beobachtet |

**Client.** Die Grafikeinstellungen bekommen einen Regler für die vertikale Sichtweite, das vertikale Gegenstück zur Sichtweite (`verticalCubeLoadDistance` in der Config, die der spielenden Person gehört). Alles Übrige in der Gruppe `terrain` – Vorgenerierung, Weltphysik, Spawn, Weltgrenze – gilt auf Rubic-Welten unverändert.

## Die Tiefenwelt

Neun weitere `terrain`-Schlüssel füllen den Raum, den eine Rubic-Welt um das Vanilla-Terrainfenster herum öffnet, mit Generierung modernen Stils. Sie tun nur auf einer Rubic-Welt etwas:

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "rubicWorld": true,
    "worldMinHeight": -64,
    "worldMaxHeight": 1024,
    "deepStone": "mypack:slate",
    "skyStone": "minecraft:end_stone",
    "skyShape": "islands",
    "skyIslands": 0.05,
    "skyThickness": 3.0,
    "skyHeights": [400, 800],
    "noiseCaves": "world",
    "deepRavines": true,
    "oreVeins": ["minecraft:iron_ore,,mypack:slate@1,-56,20"]
  }
}
```

| Schlüssel | Wert | Standard | Was er tut |
| --- | --- | --- | --- |
| `deepStone` | `namespace:block`, Meta als `@meta` | keiner | Der Block, aus dem die Welt unter dem Fenster besteht, etwa der eigene Deepslate eines Packs. Er blendet über die untersten acht Schichten des Fensters in dessen Stein über, so wie moderne Versionen Deepslate überblenden |
| `skyStone` | `namespace:block`, Meta als `@meta` | keiner | Der Block, aus dem die Welt über dem Fenster unter ihrer Oberfläche besteht, von demselben Rauschen zu schwebendem Land geformt, das unten die Tiefenwelt aushöhlt: Was dort unten Höhle ist, ist hier oben Insel. Leer lässt den Raum über dem Fenster leer, so wie bisher. Das Land trägt die Oberfläche seiner eigenen Säule, der oberste Block und die drei darunter stammen aus dem Biom, eine Insel der Oberwelt liest sich also als Gras über Erde über diesem Block. Ein Biom oder eine Höhlenregion kann eigene `skyStone`, `skyIslands` und `skyThickness` nennen, ein Band oder eine Region trägt also eigene Inseln, je Säule aufgelöst, wobei die Region das Band schlägt und das Band das Biom. Die Inseln werden eigenständig geschmückt: Jeder Würfel über dem Fenster lässt die Merkmale des Bioms auf der Oberfläche in diesem Würfel laufen, Bäume, Gras, Blumen, Pilze, Zuckerrohr und Flecken landen also auf der Insel, statt wie bei Vanilla die Säule hinunter gestreut zu werden, und ein Biomband dort oben schmückt mit eigenen Zahlen. Auch die eigenen Zutaten eines Bioms laufen dort, nicht nur die gemeinsamen: Wüstenbrunnen, Dschungelmelonen, das dichte Dach und die Pilze eines Dunklen Waldes, Taiga-Findlinge, Eiszapfen in den eisigen Biomen und die hohen Blumen und Gräser, die jedes Biom setzt. Eine Insel besteht nie aus einem Block, der fällt: Wo die Oberfläche eines Bioms Sand oder Kies wäre, nimmt die Insel Sandstein, sonst ihren eigenen `skyStone`, denn in der Luft hält einen fallenden Block nichts. Herden werden genauso je Würfel gesetzt, Tiere stehen also schon auf den Inseln, sobald das Land entsteht. Die Oberflächentiefe schwankt mit dem Rauschen zwischen einem und vier Füllblöcken, ein Inselrand ist also keine gleichförmige Kruste, und die Kruste wird entlang der Neigung gemessen statt senkrecht, eine steile Flanke behält ihren Boden also, statt auszudünnen. Die Oberfläche folgt dem Biom, das der Himmel selbst meldet, zuerst die Höhlenregion, dann ein Höhenband, dann die Säule darunter. `minecraft:mesa` an einer Himmelsregion ergibt also Inseln aus gebändertem Ton in jeder Höhe, dieselben Bänder wie am Boden, und eine Wüste ergibt ihren Sand, zu Sandstein geworden, weil in der Luft nichts einen fallenden Block hält. Tiere siedeln sich darauf an, was `skyAnimals` in der Gruppe `spawning` abstellt. Wie viel Himmel zu Land wird, steuert `skyIslands`, und der Vorgabewert 0.5 ergibt einen Archipel: in einer erzeugten Welt bleiben rund sieben von acht Würfeln über dem Fenster leer und die dichteste Schicht liegt bei knapp einem Drittel, der Himmel wird also durchflogen und nicht begangen. Senkt man ihn Richtung 0.2, schließt sich das Band zu einer welligen Decke mit Hügeln darauf, in der Mitte etwa vier Fünftel gefüllt, worauf sich bauen lässt, was aber keine Inseln mehr sind. Inseln enden acht Blöcke unter `worldMaxHeight`, eine Spitze wird also nie flach an der Decke abgeschnitten und Bäume und Pflanzen haben Platz darüber; `caves` füllt weiterhin bis zur Decke. Jede Rubic-Dimension hat ihr eigenes Fenster, gefüllt wird also der Raum über jedem davon: im Nether, dessen Fenster 128 hoch ist, der Raum über der Bedrock-Decke, und eine Naht, die die Decke öffnet, räumt die Decke selbst weg |
| `skyShape` | `islands` oder `caves` | `islands` | Wozu die Welt über dem Fenster geformt wird. `islands` ist schwebendes Land. `caves` ist massives Gestein mit hindurchgeschnittenen Höhlen, die Behandlung der Tiefenwelt nach oben gekehrt, und kommt auf rund 86 Prozent Fülle heraus, dasselbe Verhältnis von Gestein zu Höhle wie in der Tiefenwelt. Geflutet wird in keinem Fall, da über dem Fenster kein Aquifer befragt wird. Wird nur gelesen, wenn `skyStone` einen Block nennt |
| `skyIslands` | Zahl, `-1` bis `1` | `0.5` | Wie bereitwillig sich der Himmel zu Inseln sammelt. Niedriger verteilt Insel über mehr Himmel und vertieft den Schatten darunter, höher lässt weniger und kleinere Stücke übrig. Die Vorgabe lässt rund sieben von acht Würfeln leer und liegt am dichtesten bei knapp einem Drittel; bei etwa `0.2` schließt sich das Band zu einer Decke mit Hügeln, in der Mitte etwa vier Fünftel gefüllt. Wird nur gelesen, wenn `skyStone` einen Block nennt und `skyShape` auf `islands` steht |
| `skyThickness` | Zahl, `0` oder mehr | `2.0` | Wie massiv eine Insel ist. Höher füllt die Inseln aus, niedriger höhlt sie aus und lässt ihre Ränder ins Nichts auslaufen. Wird nur gelesen, wenn `skyStone` einen Block nennt und `skyShape` auf `islands` steht |
| `skyHeights` | zwei Ints, unterster dann oberster | keine | Der unterste und der oberste Block, den eine Insel erreichen darf, gezählt vom Boden des Fensters, so wie die Höhen von `oreVeins`. Leer füllt die ganze Welt über dem Fenster, auf einer hohen Welt also sehr viel Himmel. Wird nur gelesen, wenn `skyStone` einen Block nennt |
| `noiseCaves` | `off`, `deep`, `world` | `off` | Noise-Höhlen modernen Stils: Käsekavernen, Spaghetti-Tunnel, Höhlenmünder nahe der Oberfläche und Säulen in den großen Räumen. `deep` schnitzt nur unter dem Fenster, `world` die ganze Welt |
| `deepRavines` | boolean | `false` | Schneidet Schluchten im Vanilla-Stil durch die Welt unter dem Fenster, lange steile Klüfte. Eine Schlucht übernimmt die Flüssigkeiten der Tiefenwelt dort, wo sie sie durchquert: unterhalb der Lavagrenze füllt sie sich mit Lava, darüber behält sie das Wasser eines Aquifers oder dessen Druckwand, sie lässt also nie ab, was sie anschneidet. Moderne Versionen schneiden ihre Schluchten nur innerhalb des Fensters, die Tiefe hat also keine, solange dies aus ist |
| `oreVeins` | Liste aus `ore,extra,filler,lowest,highest` | keine | Große gebänderte Erzadern, überwiegend der `filler`-Block mit dem `ore` darin verstreut und einer seltenen Chance auf das `extra`, das leer bleiben darf. Höhen zählen vom Boden des Fensters, Negative erreichen also die Tiefenwelt |

Wasser und Lava benehmen sich dort unten. Die untersten Schichten füllt Lava, und die Höhlen darüber tragen lokale Aquifere — dasselbe Schema aus Stützpunkten und Druck, das moderne Versionen verwenden, portiert aus 26.1.2 — Taschen stillen Wassers stehen also auf eigenen Höhen, mit Wänden aus dem Tiefengestein, die das Rauschen formt, wo zwei Höhen aufeinandertreffen oder Wasser auf Lava trifft. Unter Ozeanen fluten die Höhlen zum Meeresspiegel hin, so wie moderne Versionen ihre Aquifere an die Oberfläche binden.

**Je Dimension.** `deepStone`, `noiseCaves`, `skyStone`, `skyShape`, `deepRavines` und `oreVeins` nehmen entweder einen einzelnen Wert oder eine Liste, und ein Listeneintrag der Form `dimension=wert` gilt nur für diese Dimension. Sobald irgendein Eintrag eine Dimension nennt, entscheiden dort nur diese Einträge und die unbenannten werden ignoriert, `"1="` ohne etwas dahinter schaltet den Schlüssel für diese Dimension also ab. Ein Wert ohne Dimension erreicht jede Rubic-Dimension außer dem Ende, das leer bleibt, solange ein Pack es nicht ausdrücklich nennt: das Ende zu füllen wäre das Ende des Endes, und ein gefülltes Ende blendet zudem die Torsuche, die von 1024 Blöcken aus zurückläuft, solange sie auf Chunks mit Blöcken trifft. Nennt man es, bekommt man, was man verlangt hat.

Mit eingeschaltetem `noiseCaves` würfelt die Tiefe außerdem Monsterräume im modernen Stil — rund vier Versuche pro Chunk-Säule unterhalb des Fensters, keiner näher als sechs Blöcke am Weltboden —, sodass Dungeon-Spawner und ihre Kistenbeute in den tiefen Höhlen auftauchen wie in modernen Versionen.

Der Umfang `world` räumt außerdem zwei Vanilla-Überbleibsel ab, die sich mit den neuen Höhlen beißen würden. Die Lava, die Vanilla unterhalb von y 10 in seine Höhlen gießt, beurteilt stattdessen der Aquifer, das alte Lavafenster entfällt also, und Vanillas vergrabene Wasserseen — Oberflächenteiche eingeschlossen — entstehen nicht mehr, so wie moderne Versionen sie gestrichen haben; an ihre Stelle treten die Becken des Aquifers.

Der Umfang `deep` lässt das Vanilla-Band, wie es ist, samt Lavafenster, und dichtet nur die Naht ab, an der beide aufeinandertreffen. Lava oder Wasser auf der untersten Schicht des Fensters, unter der sich eine Tiefenhöhle öffnet, wird zu Tiefengestein, damit das Fenster nicht in die Höhlen darunter ablaufen kann.

## Höhlenregionen

`<namespace>/caveregions/*.json`

Der Pfad der Datei ist der Name der Region, den ein Worldgen-Eintrag dann in `caveRegions` nennt. Ein bloßer Name dort nimmt den Namespace dieses Eintrags.

Malt benannte Regionen über den Untergrund, das Pack-Gegenstück zu modernen Höhlenbiomen. Der Untergrund wird in gerundete Zellen geteilt — `caveRegionCells` Blöcke breit und `caveRegionCellsY` hoch, beides `terrain`-Schlüssel — und jede Zelle würfelt nach Gewicht eine Region, oder keine. Alles, was eine Region tut, folgt deterministisch aus dem Seed, Chunks stimmen also überein, ohne je über eine Grenze zu schreiben.

Alle Schlüssel auf einmal. Eine echte Datei schreibt nur die, die sie braucht.

```json
{
  "weight": 3,
  "minHeight": -56,
  "maxHeight": 16,
  "dimensions": [0],
  "biome": "minecraft:mushroom_island",
  "floorCover": "minecraft:mycelium",
  "floorChance": 0.8,
  "ceilingCover": "minecraft:brown_mushroom_block",
  "ceilingChance": 0.3,
  "coverReplace": ["minecraft:stone", "mypack:slate"],
  "waterLevel": -24,
  "keepDefaultSpawns": false,
  "spawns": [
    { "entity": "minecraft:mooshroom", "type": "creature", "weight": 12, "min": 2, "max": 4 }
  ],
  "structures": [
    { "structure": "mypack:cave_shrine", "weight": 3 },
    "mypack:cave_well"
  ],
  "structureChance": 0.5,
  "skyStone": "minecraft:sandstone",
  "skyIslands": 0.2,
  "skyThickness": 2.0
}
```

| Schlüssel | Wert | Standard | Was er tut |
| --- | --- | --- | --- |
| `weight` | int | `1` | Anteil der Zellen, die diese Region gewinnt. `0` schaltet sie ab |
| `minHeight` | int | der Weltboden | Unterkante des Bandes, in dem die Region existiert |
| `maxHeight` | int | `48` | Oberkante des Bandes. Eine Zelle, deren Mitte außerhalb liegt, wählt die Region nie |
| `dimensions` | Liste von ints | alle | In welchen Dimensionen die Region erscheint |
| `floorCover` | Block | keiner | Ersetzt den obersten Block von Höhlenböden innerhalb der Region |
| `floorChance` | 0.0 bis 1.0 | `1.0` | Wie viel vom Boden bedeckt wird |
| `ceilingCover` | Block | keiner | Ersetzt Höhlendeckenblöcke innerhalb der Region |
| `ceilingChance` | 0.0 bis 1.0 | `1.0` | Wie viel von der Decke |
| `coverReplace` | Liste von Blöcken | alles Steinartige | Was die Bedeckungen ersetzen dürfen |
| `waterLevel` | int | keiner | Legt die Wasserhöhe jedes Aquifer-Stützpunkts innerhalb der Region fest, ihre Höhlen fluten also bis zu dieser Höhe. Wände, wo die Region auf trockene Höhlen trifft, formt dasselbe Druckrauschen wie bei modernen Aquiferen, und Wasser berührt den Lavaboden nie. Braucht eingeschaltete `noiseCaves` |
| `spawns` | Liste | keine | Mobs, die innerhalb der Region spawnen, mit denselben Einträgen wie das `spawns` eines Bioms: `entity`, `type` (monster, creature, ambient oder water), `weight`, `min` und `max` für die Gruppengröße. Unter dem Terrainfenster bleibt eine Stelle mit Himmelssicht dem Biom überlassen, wie bei den Belägen; über dem Fenster, wo das einzige Land die Himmelsgeneration ist, gilt die Liste auch im Freien |
| `keepDefaultSpawns` | boolean | `false` | Behält die Spawnliste des Bioms neben der der Region. Aus, ersetzt die Liste der Region sie innerhalb der Region vollständig |
| `structures` | Liste | keine | Ein Bauwerk, einmal pro Regionszelle gesetzt, im Herzen der Zelle, auf den nächsten Höhlenboden gesetzt — so wie moderne Versionen einem Höhlenbiom sein Wahrzeichen geben. Einträge sind `namespace:name`-Vorlagen oder `{ "structure": "...", "weight": 3 }` zur Auswahl zwischen mehreren |
| `structureChance` | 0,0 bis 1,0 | `1.0` | Die Chance, mit der jede Zelle der Region ihr Bauwerk tatsächlich bekommt |
| `structureLoot` | `namespace:pfad` | keine | Die Beutetabelle, aus der jede Truhe in einem gesetzten Bauwerk beim ersten Öffnen gefüllt wird |
| `biome` | Biomname | keiner | Das Biom, das die Region in ihrem Raum meldet, als 3D-Biom in den Würfel geschrieben. Gibt der Region eigene Laub-, Gras- und Wasserfarben, eigene Musik und Umgebungsgeräusche, und Vanillas Spawn-Gewichtung liest es. Die Oberfläche darüber bleibt unberührt, da nur die Zellen geschrieben werden, die die Region einnimmt |
| `skyStone` | Block | die Welteinstellung | Der Block, aus dem Himmelsinseln innerhalb dieser Region unter ihrer Oberfläche bestehen, eine Region trägt also eigene Inseln |
| `skyIslands` | `-1` bis `1` | die Welteinstellung | Die Inselschwelle innerhalb der Region. Niedriger sammelt mehr Land |
| `skyThickness` | `0` oder mehr | die Welteinstellung | Wie massiv die Inseln der Region sind |

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

Wie viel vom Untergrund schlicht bleibt, bestimmt der `terrain`-Schlüssel `caveRegionPlainWeight`, Standard `4`: Mit einer einzigen Region vom Gewicht 1 bekommt etwa ein Fünftel der Zellen die Region. Bedeckungen greifen unter einem Dach, eine Region, die über den Boden hinausreicht, zeigt sich an der Oberfläche also nie; über dem Terrainfenster greifen sie auch im Freien, da dort alles Land aus der Himmelsgeneration stammt. Bedeckungen wirken in jeder Höhle, egal welcher Generator sie geschnitzt hat; `waterLevel` ist der eine Schlüssel, der die Noise-Höhlen braucht, weil die Flut beim Schnitzen gesetzt wird.

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

Das `replace` mit `minecraft:air` ist wichtig: Was eine gesetzte Form überschreibt, wird gegen `replace` geprüft, dessen Standard Stein ist — alles, was in offenen Höhlenraum gebaut wird, braucht also Luft in der Liste. Derselbe Eintrag mit `"snap": "floor"` und ohne `hanging` lässt die passenden Stalagmiten wachsen. Der Regionsfilter funktioniert mit jeder gesetzten Form; `belt` und `field` setzen nach eigenen Regeln und ignorieren ihn.

## Welt-Intro

`<namespace>/worldintro/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner. Jedes Intro, das ein Pack mitbringt, läuft, in Pack-Reihenfolge.

Zeigt eine Folge von Seiten, wenn ein Spieler die Welt betritt, bevor er die Kontrolle bekommt. Laufender Text über einem Bild, eine Titelkarte, eine Diaschau – oder alles drei hintereinander.

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

Textdateien liegen unter `<namespace>/texts/*.txt`. Reiner Text, ein Absatz pro Zeile, und Leerzeilen bleiben Leerzeilen. `PLAYERNAME` wird durch den Namen des Spielers ersetzt, dieselbe Ersetzung, die auch das Vanilla-Endgedicht nutzt.

`time` legt fest, wie lange die Seite dauert, dieselbe Seite braucht also gleich lang, ob eine Zeile darauf steht oder zwanzig. Die Lesegeschwindigkeit stellst du darüber ein, wie viel du auf die Seite packst. Lässt du `time` weg, läuft die Seite so schnell wie der Vanilla-Abspann, wo mehr Text einfach länger dauert.

Eine laufende Seite geht zur nächsten über, wenn ihre Zeit um ist. Die letzte Seite geht nie von selbst weiter, sie wartet. Unten stehen **Next Page** und **Skip All**, auf der letzten Seite ein einzelnes **Continue to World**. Escape tut dasselbe wie Skip All. Statische Seiten zentrieren jede Zeile. Laufende Seiten halten sich an eine feste Spalte, so wie der Abspann.

Im Einzelspieler pausiert die Welt hinter dem Intro, es schleicht sich also nichts an den Spieler heran, während er liest. Die einzige Ausnahme ist Land, das beim Öffnen des Intros noch gemacht wird: dann läuft das Machen hinter den Seiten weiter, und der Spieler bleibt als Zuschauer festgehalten, bis er in die Welt weitergeht, auch wenn der Lauf vorher fertig wird. Auf einem Server läuft die Welt weiter, und ein Vanilla-Client sieht das Intro überhaupt nicht und tritt ganz normal bei.

`once` wird in den Spielerdaten gespeichert und übersteht den Tod. `/rdplserver intro` setzt es für den zurück, der ihn ausführt, das Intro läuft dann beim nächsten Beitritt wieder. Es wird nicht sofort noch einmal abgespielt, damit es kein Weg zurück in die Einstiegssequenz mitten im Spiel wird.

Hintergründe werden auf die Fenstergröße gezogen, ein 16:9-Bild passt also zu einem 16:9-Fenster, ein quadratisches sieht gestaucht aus. Schneid das Bild passend zu, statt dich auf die Anpassung zu verlassen. `music` nimmt jedes registrierte Sound-Event, von Vanilla oder aus deinem eigenen Pack über `sounds`. Es läuft nicht in Schleife, ein kurzes Stück ist also irgendwann zu Ende und lässt Stille zurück.

Bringt mehr als ein Pack ein Intro mit, laufen ihre Seiten in Pack-Reihenfolge hintereinander, statt dass eines gewinnt. Sperr sie mit `requires`, wenn du nur eines willst.

## Spielregeln

`<namespace>/gamerules/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

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

`<namespace>/hardness/*.json`

Der Pfad der Datei benennt die Gruppe im Log, sonst liest ihn nichts, mehrere Dateien addieren sich also.

Gibt einer Gruppe von Blöcken einen Faktor für die Abbauzeit, der pro Blockposition gewürfelt wird. Der Block selbst wird nie verändert: Nichts wird registriert, nichts in die Welt geschrieben, und eine Welt ohne das Pack ist ganz gewöhnliches Vanilla.

```json
{
  "blocks": ["minecraft:stone:0"],
  "except": [{ "block": "minecraft:stone", "properties": { "variant": "andesite" } }],
  "miningTime": { "min": 1.0, "max": 20.0 },
  "blastResistance": { "min": 1.0, "max": 4.0 },
  "buckets": 10,
  "minHeight": 0,
  "maxHeight": 255,
  "field": { "type": "speckle", "spread": 0.15 },
  "requires": ["mypack"]
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

```json
{
  "field": { "type": "speckle" }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er tut |
| --- | --- | --- | --- | --- |
| `type` | nein | `speckle` oder `seeded` | `speckle` | Welches der beiden unten genommen wird |

#### speckle

Jeder Block zieht seine eigene Stufe, und ein Block eine Fläche weiter kann eine schwächere Stufe an ihn weitergeben. Das gibt dichte, feine Sprenkel, meist einzelne Blöcke, mit hier und da einem größeren Nest, wo sie zusammentreffen. Von beiden kommt das dem Gefühl beim Abbauen in der Vorlage am nächsten.

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
| `chances` | nein | Liste von Ganzzahlen, je Tausend | `[30, 30, 20, 20, 10, 10, 10, 10, 50]` | Wie oft ein Block auf welcher Stufe anfängt, weichste zuletzt. Was übrig bleibt, ist die härteste Stufe |
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

**Derselbe Schlüssel, nach dem das Spiel fragt.** Nicht jeder Block ist unter dem Schlüssel abgelegt, den seine Eigenschaften vermuten lassen. Vanilla-Stein legt alles unter `normal` ab, nicht unter `variant=stone`, eine Ersetzung, die nur `variant=stone` schreibt, wird also zusammengeführt und danach nie angesehen. Beide Schlüssel zu schreiben ist unbedenklich, denn zusammengeführt wird je Schlüssel, und ein Pack sticht, was vorher da war.

Schalte `worldgenDebug` ein, dann wird jede Härtegruppe beim Betreten einer Welt gegen ihr gebackenes Modell geprüft: welcher Blockstate, wie viele Varianten übrig blieben, welche Textur jede davon bekam und welche Packs das Spiel dafür zusammengeführt hat. Das ist der schnellste Weg zu allen drei Punkten oben, und es warnt auch, wenn das Ersetzen eines geteilten Blockstates einen Zustand verändert hat, den die Gruppe nie genannt hat.

### Was nicht erreicht wird

Nur das Abbauen durch einen Spieler wird verändert. Maschinen, die Blöcke abbauen, lesen die Härte des Blocks direkt und merken nichts davon. Blöcke, die ein Spieler setzt, werden wie alle anderen gewürfelt, denn der Wurf gehört zum Ort und nicht zum Block, und ein anderswohin getragener Block nimmt an, was sein neuer Ort sagt.

# Generieren

## Worldgen-Einträge

`<namespace>/worldgen/*.json`

Der Pfad der Datei benennt den Eintrag, und die Formen `belt` und `field` ziehen ihr Rauschen daraus, ein umbenannter Dateiname verschiebt also, was sie erzeugt.

Beschreibt etwas, das generiert. Jeder Eintrag ist eine **Form**, gesetzt von einer **Verteilung**, gefiltert danach, wo sie erlaubt ist.

```json
{
  "block": "mypack:ruby_ore",
  "meta": 0,
  "blocks": [
    { "block": "mypack:ruby_ore", "meta": 0, "weight": 80 },
    { "block": "minecraft:wool", "weight": 20, "properties": { "color": "magenta" } }
  ],
  "size": 8,
  "attempts": 12,
  "replace": ["minecraft:stone"],
  "adjacent": ["minecraft:air"],
  "minHeight": 8,
  "maxHeight": 48,
  "dimensions": [0],
  "dimensionsAreBlacklist": false,
  "biomes": ["minecraft:extreme_hills"],
  "biomeTypes": ["MOUNTAIN"],
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
| `caveRegions` | nein | Liste von Regionsnamen | keine | Generiert nur innerhalb dieser [Höhlenregionen](#höhlenregionen) |
| `snap` | nein | `floor` oder `ceiling` | keiner | Verschiebt jeden Versuch erst senkrecht zum nächsten Höhlenboden oder zur nächsten Höhlendecke |
| `snapDepth` | nein | int | `0` | Wie weit `snap` danach über die Oberfläche hinaus geht, vom Boden nach unten und von der Decke nach oben. `0` bleibt im freien Raum an der Oberfläche, `1` ist der Oberflächenblock selbst, `2` der dahinter. Was überschrieben werden darf, regelt weiterhin `replace`, so legt ein Pack ein Band knapp unter den Boden statt darauf |

### Gewichtete Blöcke

`blocks` ersetzt `block`, wenn ein Eintrag nicht reicht. Die Gewichte sind relativ, 80 und 20 ist also vier zu eins.

```json
{
  "blocks": [
    { "block": "minecraft:wool", "meta": 2, "weight": 80 },
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

```json
{
  "replace": [
    "minecraft:stone",
    "minecraft:stone:3",
    { "block": "minecraft:stone", "properties": { "variant": "andesite" } },
    { "block": "minecraft:stone", "meta": 5 }
  ]
}
```

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
    "surface": ["minecraft:grass"],
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
    "turns": ["none", { "turn": "half", "weight": 2 }],
    "mirrors": ["none", { "mirror": "leftright", "weight": 2 }],
    "at": [1000, -500],
    "locateAs": "Crypt",
    "field": { "type": "speckle", "spread": 0.15 },
    "threshold": 0.5,
    "fade": 0,
    "rarity": 400,
    "rarityIsPerChunk": false
  }
}
```

```json
{
  "shape": { "type": "tree", "log": "mypack:ruby_log", "leaves": "mypack:ruby_leaves", "height": { "min": 4, "max": 7 }, "surface": ["minecraft:grass"] }
}
```

Ein `tree` ohne `log` oder `leaves` generiert nichts und sagt das im Log.

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
| `lootTable` | imprint | `namespace:pfad` | keine | Die Beutetabelle, aus der jede Truhe in der gesetzten Vorlage beim ersten Öffnen gefüllt wird, und jeder andere Behälter, der eine annimmt, eine Shulkerkiste oder die Kiste eines Mods darunter. Gilt für `structure` und jeden Eintrag von `structures`; jede Truhe würfelt ihren eigenen Seed |
| `structures` | imprint | Liste | keine | Mehrere Vorlagen zur Auswahl, eine davon wird jedes Mal gesetzt. Jeder Eintrag ist `{ "structure": "namespace:name", "weight": 3 }` oder ein bloßer Name für gleiche Chancen. Überschreibt `structure` |
| `turns` | imprint | Liste | beliebig | Wie herum sie gesetzt werden darf: `none`, `quarter`, `half`, `threequarter`. Einträge dürfen ein `weight` tragen. Weggelassen sind alle vier gleich wahrscheinlich |
| `mirrors` | imprint | Liste | keine | Sie zusätzlich spiegeln: `none`, `leftright`, `frontback`, mit optionalem `weight`. Ein Eintrag mit eigenem Gewicht wird `{ "mirror": "leftright", "weight": 2 }` geschrieben, ein `turns`-Eintrag genauso mit `turn` |
| `at` | imprint | zwei Ints, x und z | keine | Genau einmal an diesen Blockkoordinaten an der Oberfläche setzen, wenn dieser Chunk generiert, statt nach Zufall. Siehe [Strukturen an genauen Stellen](#strukturen-an-genauen-stellen) |
| `locateAs` | imprint | String | keiner | Jede Struktur, die dieser Eintrag setzt, unter diesem Namen eintragen, sodass `/locate <Name>` die nächste findet. Siehe [Gesetzte Strukturen finden](#platzierte-strukturen-finden) |
| `field` | field | Objekt | `{ "type": "speckle" }` | Wie das Feld errechnet wird. Dieselben Schlüssel wie das `field` einer Härtegruppe, beschrieben unter [Das Feld](#das-feld): `speckle` mit `chances` und `spread`, oder `seeded` mit `cell`, `seeds`, `reach`, `arms` und `armReach` |
| `threshold` | field | 0,0 bis 1,0 | `0,5` | Wie stark das Feld an einem Block sein muss, bevor dort gesetzt wird. Niedriger füllt mehr |
| `fade` | field | int | `0` | Lässt das Band oben ausfransen statt glatt zu enden: über die obersten so vielen Blöcke des Höhenbereichs sinkt die Chance jedes Blocks Stufe für Stufe, derselbe Look, den die Engine `deepStone` am Übergang zur Welt darüber gibt |
| `rarity` | alle | int | keiner (`400` für belt) | Eine Platzierung pro so viele Chunks. Bei einem belt bestimmt das den Abstand der Gürtel; bei jeder anderen Form lässt es nur einen Chunk von so vielen überhaupt seine `attempts` würfeln. `field` ignoriert es |
| `rarityIsPerChunk` | alle | boolean | `false` | Macht aus `rarity` stattdessen die Anzahl Platzierungen pro Chunk |

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

## Strukturkarten

Eine Strukturkarte setzt Vorlagen auf einem Raster zu einem benannten Bauwerk zusammen, weit über die 32-Block-Grenze einer einzelnen `.nbt`-Datei hinaus. Jede Ebene wird als Zeilen einzelner Zeichen gezeichnet, ein Zeichen je Zelle, und stapelt sich eine Zellhöhe über die Ebene davor. Höchstens 8 Ebenen zu 8 mal 8 Zellen, was bei der Standardzelle von 32 eine Kantenlänge von 256 Blöcken ergibt, die Bauhöhe der Vanilla-Welt.

`<namespace>/structuremaps/*.json`

```json
{
  "name": "Castle",
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
| `name` | Text | der Dateiname | Wie die Karte in den Logs heißt |
| `cell` | Zahl | `32` | Der Rasterabstand in Blöcken, bis 48. Eine Vorlage, die kleiner ist als die Zelle, sitzt in der Zellecke, sodass Stücke in voller Größe nahtlos aneinanderstoßen |
| `ground` | Zahl | `0` | Welche Ebene auf der Geländeoberfläche aufsetzt. Ebenen davor graben sich ein, so bekommt ein Bauwerk Keller |
| `at` | zwei Zahlen | keiner | Setzt eine Kopie an genaue Blockkoordinaten, so wie `structureAt` ein Dorf festlegt |
| `spacing` | Zahl | `0` | Verstreut Kopien auf einem Raster in diesem Chunkabstand, versetzt aus dem Weltseed. `0` verstreut keine, eine Karte nur mit `at` baut also genau einmal |
| `chance` | Zahl | `100` | Der Prozentanteil der Rasterplätze, die eine Kopie bauen |
| `dimensions` | Liste | alle | Dimensions-IDs, in denen die Karte bauen darf |
| `layers` | Liste | keine | Die Ebenen, von unten nach oben, jede mit `palette` und `map` |

Eine Palette nennt Vorlagen per Registry-Schlüssel aus dem `<namespace>/structures/` eines Pakets.

| Wert | Was er bewirkt |
| --- | --- |
| `"a": "mypack:keep"` | Jede `a`-Zelle dieser Ebene setzt diese Vorlage |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | Jede `a`-Zelle lost die Liste nach Gewicht aus, aus dem Weltseed und dem Platz der Zelle – zwei Kopien des Bauwerks unterscheiden sich, aber dieselbe Welt baut immer dasselbe |
| `.` | Eine leere Zelle, nichts wird gesetzt |

Jede Kopie lost eine der vier Ausrichtungen aus dem Weltseed aus, und das ganze Bauwerk dreht sich gemeinsam, Vorlagen eingeschlossen – Mauern, die sich über Zellen hinweg treffen, treffen sich also weiterhin. Die Bodenebene setzt auf der abgetasteten Geländeoberfläche unter der Mitte des Bauwerks auf. Jeder Chunk baut nur seinen eigenen Ausschnitt des Rasters, ein Bauwerk über viele Chunks entsteht also ohne kaskadierende Generierung, in welcher Reihenfolge die Chunks auch laden. Ein [Dorfgrundstück](#dorfgrundstücke) vom Typ `template` kann in seinem `structure` ebenfalls eine Karte nennen, die Komposition wird dann zum Dorfgebäude.

## Stadtpläne

Ein Stadtplan zeichnet den Straßenplan eines Dorfes auf ein Raster, ein Zeichen je Zelle, und das Dorf wird nach der Zeichnung angelegt, statt zu wachsen. Straßen, Plätze und Grundstücke werden dieselben Teile, die ein gewachsenes Dorf verwendet, also gelten jede Straßenoption, Brücke, Steg, Sackgasse, Laterne, Wendehammer und Platzmittelpunkt unverändert. Die Weltvorlage nennt den Plan in `villageLayout`.

`<namespace>/citymaps/*.json`

```json
{
  "name": "Downtown",
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
| `name` | Text | der Dateiname | Wie der Plan in Protokollen heißt |
| `cell` | Zahl | `48` | Das Rastermaß in Blöcken, 8 bis 128. Straßen laufen in der Breite des Packs durch die Mitte ihrer Zellen, Grundstücke sitzen mittig in ihren, eine Zelle braucht also das breiteste Grundstück plus Raum zur Straße hin |
| `palette` | Objekt | keins | Was jedes Zeichen anlegt, unten aufgeführt |
| `map` | Liste | keine | Die Zeilen, bis zu 64 mal 64 Zellen. Eine kürzere Zeile ist hinter ihrem Ende offen |

| Wert | Was er tut |
| --- | --- |
| `"#": "street"` | Ein Lauf von Straßenzellen entlang einer Zeile oder Spalte wird ein Straßenkasten in der Breite des Packs. Wo ein Zeilenlauf einen Spaltenlauf kreuzt, wird die Kreuzung wie jede andere gestaltet. Eine einzelne Straßenzelle ohne Lauf in einer Achse wird als kurzer Stummel entlang der Zeile angelegt |
| `"+": "plaza"` | Ein Brunnen mit seinem Platzring. Läufe gehen durch Platzzellen hindurch, Straßen treffen sich also am Brunnen, und ein Platz auf einer Kreuzung stellt seinen Brunnen, oder sein `villageWellStructure`-Mittelstück, wie einen Kreisverkehr mitten auf die Kreuzung. Der erste Platz in der Datei ist der Brunnen des Dorfes selbst, der den Plan dort festmacht, wo das Dorf gegründet wird; ein Plan ohne einen wird dort zentriert |
| `"a": "alley"` | Ein schmaler Lauf. Gebäude stehen daran, aber er verbindet nichts, die Gassenregel wie gewohnt |
| `"T": "mypack:tower"` | Eine Grundstückszelle, angelegt aus dieser Grundstücksdefinition, mittig in der Zelle und zur nächsten Straße gewandt |
| `"T": ["mypack:a=3", "mypack:b=1"]` | Dasselbe, nach Gewicht aus dem Weltseed und dem Platz der Zelle ausgelost, dieselbe Welt legt dort also immer dasselbe Grundstück an |
| `"g": "grow"` | Dem Wachsen überlassen. Mit gesetztem `villagePlotsLeast` füllen die gewachsenen Viertel und die Straßennachfüllung solche Zellen und breiten sich vom Plan aus; ohne bleibt die Zelle offen |
| `.` | Offener Boden, nichts angelegt |

Jeder Plan lost eine der vier Richtungen aus dem Weltseed aus und dreht sich als Ganzes, ein Plan liest sich also von jeder Seite gleich. Straßen werden zuerst angelegt, ein Grundstück, das eine Straße oder ein anderes Grundstück überlappen würde, bleibt mit einer Zeile im Protokoll offen, und ein Grundstücksname, den kein Pack liefert, lässt seine Zelle genauso offen. Der Plan ändert nicht, wie die Teile gestaltet werden: die Straßenschlüssel, `villageBlocks`, die Laternen und der Brunnenersatz gelten wie für ein gewachsenes Dorf. Aus einem gezeichneten Plan wächst nichts heraus: neben seinen Straßen werden keine Gassen aufgefüllt, und seine Straßenenden bekommen ihre Wendeplätze, drei von vieren wie üblich, aber keine Häuser daran.

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

In einem Pack stehen diese im `settings`-Block einer [Weltvorlage](#weltvorlagen), wie jeder andere `chunks`-Schlüssel auch. Hier alle zusammen, mit `pregenBorderLimit` als einzigem Fehlenden, weil nur die Config ihn hält:

`<namespace>/worldtemplates/*.json`

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
    "welcomeSays": ["Welcome to Ruby World!", "-1=Welcome to the Nether!"]
  }
}
```

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

Der Landbau hat einen eigenen schnellen Weg für die Beleuchtung, und er tritt beiseite, sobald eine Licht-Engine wie Alfheim oder Phosphor installiert ist, und überlässt ihr die Arbeit. So oder so bekommst du am Ende fertiges, vollständig beleuchtetes Land.

Lass ihn vor der Auslieferung einmal selbst durchlaufen, mit dem Radius, den du ausliefern willst, von Anfang bis Ende. Die Zahl der Chunks wächst mit dem Quadrat des Radius: 63 in jede Richtung sind sechzehntausend Chunks, 500 sind über eine Million, bei rund zehn Kilobyte pro Stück – der Region-Ordner deiner Testwelt und die tatsächlich verstrichene Zeit sind also die ehrlichen Zahlen, die du den Spielern nennen kannst. Liefere keinen Radius aus, der nie durchgelaufen ist.

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

Steht die Steuerung einer Gruppe auf `default`, gewinnen diese Werte, auf `global` werden sie ignoriert, und auf `off` tut die ganze Gruppe nichts, egal was ein Pack sagt.

## Was jede Gruppe macht

### Erze

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockOres": true,
    "oreWhitelist": ["minecraft", "mypack"],
    "oreTypes": ["COAL", "IRON"],
    "oreTypesAreBlacklist": true,
    "blockOreDimensions": [0, -1],
    "blockOreDimensionsAreBlacklist": false
  }
}
```

`blockOres` hindert jeden Mod und Minecraft daran, Erz zu generieren, außer den Mods in `oreWhitelist`. `oreTypes` nennt die Erztypen, für die das gilt, und `oreTypesAreBlacklist` entscheidet die Richtung: an werden die genannten Typen blockiert, aus generieren nur die genannten Typen. Erreichbar ist nur Generierung, die über Forges Ore-Generation-Event läuft, also Minecraft und die meisten, aber nicht alle Mods. `blockOreDimensions` beschränkt das Blockieren von Erz auf bestimmte Dimensionen – leer heißt jede –, und `blockOreDimensionsAreBlacklist` macht aus dieser Liste die Dimensionen, die in Ruhe gelassen werden. Eine Dimension außerhalb des Geltungsbereichs wird gar nicht angefasst, die Erze eines anderen Mods generieren dort also unbehelligt, während die Oberwelt blockiert bleibt.

### Biome

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockBiomes": true,
    "biomeWhitelist": ["minecraft", "mypack"],
    "biomeNames": ["minecraft:mesa", "minecraft:mesa_rock"],
    "biomeNamesAreBlacklist": true,
    "blockBiomeDimensions": [0],
    "blockBiomeDimensionsAreBlacklist": false
  }
}
```

`blockBiomes` und `biomeWhitelist` arbeiten nach Mod, `biomeNames` mit `biomeNamesAreBlacklist` nach Namen. Blockierte Biome werden auf der fertigen Biomkarte ersetzt, und nur so kommt man an Ozeane, Pilzinseln, Mesa-Varianten, Dschungel, Hügel und Küsten heran: Die werden außerhalb der Listen ausgewählt, die ein Mod bearbeiten kann. Blockier jedes Biom, und die Oberwelt wird von selbst zur Void-Welt. `blockBiomeDimensions` beschränkt das Ganze auf bestimmte Dimensionen – leer heißt jede –, und `blockBiomeDimensionsAreBlacklist` macht aus dieser Liste einen Ausschluss.

### Generatoren

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockWorldGenerators": true,
    "generatorWhitelist": ["minecraft", "mypack"],
    "blockedGenerators": ["tconstruct"],
    "blockGeneratorDimensions": [0],
    "blockGeneratorDimensionsAreBlacklist": false,
    "generatorTypes": ["ores", "lakes"],
    "generatorTypesAreBlacklist": true,
    "generatorTypeMap": ["mrtjpcore=ores", "deworldgenhandler=structures"],
    "logBlockedGenerators": true
  }
}
```

`blockWorldGenerators` hindert andere Mods daran, über ihre eigenen Weltgeneratoren zu generieren – so fügen Mods das hinzu, was Forges Events nie zu sehen bekommen: Schleiminseln, Höhlenkristalle und dergleichen. `generatorWhitelist` behält die genannten Mods, `blockedGenerators` nennt einzelne, und die Pack-Generierung dieses Mods wird nie blockiert. `blockGeneratorDimensions` beschränkt es auf bestimmte Dimensionen, `blockGeneratorDimensionsAreBlacklist` dreht die Liste um.

`generatorTypes` blockiert danach, was ein Generator macht, statt danach, welchem Mod er gehört: `ores`, `structures`, `flora`, `lakes`, `terrain` oder `unknown` für die, auf die nichts gepasst hat. `generatorTypesAreBlacklist` entscheidet die Richtung: an werden die genannten Typen blockiert, aus generieren nur die genannten Typen. Ein Typ blockiert unabhängig davon, was die Whitelist sagt, genau wie `oreTypes` – du kannst also jedem Mod das Erz abgewöhnen und seine Verliese und Bäume in Ruhe lassen.

Der Typ kommt aus dem Klassennamen des Generators, abgeglichen mit einer eingebauten Wortliste pro Typ. Das liest die meisten Mods richtig – `NetherOreGenerator` ist ores, `SlimeIslandGenerator` ist structures –, aber ein Generator, der nach nichts Bestimmtem benannt ist, etwa ProjectReds `SimpleGenHandler` oder Draconic Evolutions `DEWorldGenHandler`, kommt als `unknown` heraus. `generatorTypeMap` korrigiert die von Hand, ein `pattern=typ` pro Zeile, wobei das Muster eine Mod-ID oder ein Teil eines Generator-Klassennamens ist:

```
mrtjpcore=ores
deworldgenhandler=structures
```

Zugeordnete Einträge werden vor den eingebauten Wörtern geprüft, sie korrigieren also auch einen Generator, den die Wörter falsch gelesen haben. Schalte `logBlockedGenerators` ein, und jeder Generator wird beim ersten Blockieren mit dem Typ protokolliert, den er bekommen hat; `/rdplserver generators` zeigt die laufenden Summen nach Mod und Typ.

### Ersetzungen

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockReplacements": [
      "bigreactors:oreyellorite=minecraft:stone",
      "mekanism:oreblock:0=minecraft:stone"
    ],
    "blockReplacementDimensions": [0],
    "blockReplacementDimensionsAreBlacklist": false,
    "blockReplacementMinHeight": 0,
    "blockReplacementMaxHeight": 255,
    "blockReplacementKey": "cleanup_v1"
  }
}
```

`blockReplacements` tauscht Blöcke aus Chunks heraus, die es schon gibt, ein `block=block` pro Zeile, mit optionalen Metadaten auf beiden Seiten:

```
bigreactors:oreyellorite=minecraft:stone
mekanism:oreblock:0=minecraft:stone
tconstruct:ore:0=minecraft:netherrack
```

Jeder Chunk wird einmal bearbeitet, beim Laden von der Platte, und in seinen eigenen Daten markiert, damit es nie zweimal passiert. Ein Chunk, der zum ersten Mal generiert wird, wird erst beim nächsten Laden gesäubert und nicht sofort, weil Nachbar-Chunks während seiner Generierung noch in ihn hineinschreiben. Ein Chunk am Rand des erkundeten Landes wird gesäubert, aber nicht markiert, er wird also erneut gesäubert, sobald das Land um ihn herum existiert. `blockReplacementDimensions` und `blockReplacementDimensionsAreBlacklist` wählen das Wo, `blockReplacementMinHeight` und `blockReplacementMaxHeight` das Höhenband, das betrachtet wird, und `blockReplacementKey` ist ein String, den du änderst, damit jeder Chunk noch einmal durchläuft. Es läuft unabhängig davon, ob `retrogen` an ist, denn eine Welt, die aufgeräumt werden muss, ist meist eine, in die du keine neuen Adern legen willst. Es tauscht nur Blöcke: Etwas, das ein Mod als Struktur generiert hat, lässt sich so nicht wieder herausnehmen, weil das ersetzte Gelände nie festgehalten wurde.

### Dörfer

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageBlocks": [
      "minecraft:cobblestone=mypack:ruby_brick",
      "minecraft:cobblestone=minecraft:mossy_cobblestone,20",
      "minecraft:planks=minecraft:sandstone,100,under=minecraft:sand"
    ],
    "villagePieces": ["field1", "field2"],
    "villagePiecesAreBlacklist": true,
    "villagePlotsLeast": 12,
    "villagePlotsMost": 30,
    "villageBlockSizes": ["32=3", "64=1"],
    "villageLayout": "mypack:downtown"
  }
}
```

Dörfer nutzen dieselben `structure=wert`-Listen wie jede andere Struktur, unter dem Namen `villages`, `structureSpacing`, `structureMinDistanceFromSpawn`, `structureBiomes` und `structureBiomesAreBlacklist` erreichen sie also alle. Eine `structureBiomes`-Liste, die keine Blacklist ist, fügt außerdem jedes genannte Biom hinzu, das die eigene Liste der Struktur nie enthielt – so lassen sich Dörfer ins Gebirge schicken; nenne sie dafür beim Registry-Namen, denn nur Registry-Namen können hinzufügen. Ihr Abstand hat eine Untergrenze von 9, weil Vanilla 8 davon abzieht. `villagePieces` gehört zur selben Gruppe, ein Schalter deckt also alles darüber ab, wo Dörfer hinkommen und woraus sie gebaut sind, während die Gruppe `villages` nur die Grundstücke abdeckt, die ein Pack hinzufügt.

`villageBlocks` ersetzt die Blöcke, aus denen ein Dorf gebaut wird, als `original=ersatz`-Paare: `minecraft:cobblestone=meinpack:ruby_brick`. Es greift, nachdem jeder andere Mod sein Wort hatte, ein Pack setzt sich also immer durch, auch gegen Mods, die Dorfmaterialien je Biom austauschen. Beide Seiten akzeptieren einen einfachen Blocknamen oder einen Namen mit Zuständen. Wege werden getrennt über `villagePathBlock` und seine Geschwister benannt.

Ein Paar darf eine Chance und eine Bedingung hinter sich tragen, als durch Kommas getrennte Felder, und ist dann eine Regel statt eines schlichten Tauschs. `minecraft:cobblestone=minecraft:mossy_cobblestone,20` verwittert ein Fünftel des Bruchsteins, den ein Dorf verlegt; `minecraft:planks=minecraft:sandstone,100,under=minecraft:sand` ändert den Boden nur dort, wo ein Haus auf Sand steht. Die Felder hinter dem Paar dürfen in beliebiger Reihenfolge stehen, und ein Eintrag, der ein Feld nennt, das sich nicht lesen lässt, wird ganz verworfen statt halb angewandt.

| Feld | Wert | Standard | Was es bewirkt |
| --- | --- | --- | --- |
| Chance | Ganzzahl, 1 bis 100 | `100` | Wie oft die Regel greift, von hundert |
| `at=` | Blockname | keiner | Nur dort, wo dieser Block bereits an der bebauten Stelle steht |
| `under=` | Blockname | keiner | Nur dort, wo dieser Block unmittelbar darunter liegt |

Ein schlichtes Paar wird dort beantwortet, wo ein Teil das Spiel fragt, woraus es bauen soll, es ändert also jede Wand aus diesem Block auf einmal. Eine Regel wird dort gewogen, wo der Block tatsächlich gesetzt wird, Stelle für Stelle – erst das gibt einer Chance und einer Bedingung überhaupt Sinn –, und sie sieht den Block so, wie er gleich gesetzt wird, also nachdem ein schlichtes Paar sein Wort hatte. Auf welche Stellen eine Chance fällt, ergibt sich aus dem Weltseed und der Stelle selbst; dieselbe Welt verwittert also immer dieselben Blöcke, so oft sie auch erzeugt wird.

Wege werden nie geregelt, damit Steigungen, Brücken und Kreuzungsmuster weiterhin den Weg lesen, den sie verlegt haben. Ein Vorlagen-Grundstück setzt seine eigene `.nbt`-Datei, statt auf die Art des Spiels zu bauen; Regeln reichen also nicht in eines hinein, seine Blöcke sind die der Datei. Schlichte Paare wie Regeln wirken, ob `terrainAdaptation` an ist oder nicht.

`villagePieces` nennt Vanilla-Dorfteile: `house1`, `house2`, `house3`, `house4garden`, `church`, `woodhut`, `hall`, `field1` und `field2`, und `villagePiecesAreBlacklist` entscheidet die Richtung – du kannst also Vanillas Weizenfelder streichen und die Häuser lassen oder nur die Teile auflisten, die du willst. Ein Pack-Grundstück wird über seine eigene Vorlage benannt: entweder mit dem vollen Namen, `meinpack:big_house`, oder einfach `big_house`, oder wahlweise über den Namen des Grundstücks selbst. Ein Pack kann also zehn Grundstücke mitbringen, und eine Weltvorlage lässt eines davon weg, ohne die anderen neun anzurühren. Teile aus anderen Mods ebenso wenig, etwa die Häuser von Tektopia oder die Grundstücke von Recurrent Complex: Eine Whitelist entfernt immer nur Vanillas eigene Teile, wer also die gewünschten Vanilla-Teile auflistet, löscht damit nicht stillschweigend fremde. Um einen Mod-Teil loszuwerden, nimm eine Blacklist und nenne ihn beim Namen, etwa `tekhouse2`.

`villagePlotsLeast` und `villagePlotsMost` begrenzen, mit wie vielen Grundstücken ein Dorf gebaut wird. Gezählt werden Häuser, Felder und Pack-Grundstücke, nie Straßen, Fackeln oder der Brunnen. Ein Dorf, das unter dem Minimum bleibt, wird in größerem Zuschnitt neu angelegt, einige Versuche lang, und der größte Entwurf gewinnt, auf engem Gelände kann es also trotzdem darunter bleiben. Beim Maximum hört das Dorf ganz auf zu wachsen: keine weiteren Gebäude und keine weiteren Straßen. `0` lässt das jeweilige Ende bei Vanilla.

`villageBlockSizes` legt fest, wie tief die Blöcke zwischen den parallelen Straßen einer Stadt sind, als gewichtete `größe=gewicht`-Einträge: `32=3` und `64=1` legen drei von vier Vierteln mit 32 tiefen Blöcken an und den Rest mit 64. Jedes Viertel würfelt seine Größe einmal aus der Lage seines Platzes, dieselbe Welt bekommt also immer dieselbe Mischung. Seine Straßen zweigen entlang ihrer Länge alle zwei Blöcke plus eine Straßenbreite Seitenstraßen ab, ein 16er Viertel ist also ein feines Raster und ein 64er ein grobes, und zwei parallele Straßen halten so viele Blöcke plus die des Nachbarviertels zueinander Abstand, ein 32er Viertel neben einem 64er lässt also 96 dazwischen. An den Straßen eines Viertels werden nur Grundstücke gebaut, die in die Tiefe passen, und unter denen, die passen, ist die Chance eines Grundstücks sein Gewicht mal seine Breite, tiefe Blöcke bevorzugen also die Gebäude, die sie ausfüllen, und ein 64 breites Grundstück steht nie an einem 32 tiefen Block. Straßenlänge und Platzabstand richten sich weiterhin nach dem größten Grundstück des Packs, denn das lässt jedes Viertel die Stadt erreichen. Leer bemisst jeden Block nach diesem größten Grundstück und zweigt Straßen wie bisher nur an ihren Enden ab.

`villageLayout` nennt einen [Stadtplan](#stadtpläne), nach dem das Dorf aus einem gezeichneten Straßenplan angelegt wird, statt zu wachsen; leer wächst es wie gewohnt.

#### Dorfwege

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villagePathBlock": "minecraft:stonebrick",
    "villagePathSupportBlock": "minecraft:gravel",
    "villagePathBridgeBlock": "minecraft:planks",
    "villagePathBridgeBarrierBlock": "minecraft:oak_fence",
    "villagePathBridgeBarrierHeight": 1,
    "villagePathBridgeSidewalkBlock": "minecraft:planks",
    "villagePathCenterBlock": "minecraft:quartz_block",
    "villagePathCenterDash": 2,
    "villagePathLineBlock": "minecraft:stone_slab",
    "villagePathSidewalkBlock": "minecraft:stonebrick",
    "villagePathSidewalkWidth": 2,
    "villagePathExtraWidth": 1,
    "villagePathMinimumWidth": 0,
    "villagePathAlleyBlock": "minecraft:gravel",
    "villagePathAlleyChance": 25,
    "villagePathFlatRun": 6,
    "villagePathIntersects": ["mypack:crosswalk"],
    "villagePathPiers": ["railed", "pilings", "boardwalk"],
    "villagePathDeadEnds": ["barrier", "sidewalk"],
    "villagePathLampBlock": "minecraft:iron_bars",
    "villagePathLampHeight": 3,
    "villagePathLampTopBlock": "minecraft:skull:1{SkullType:3}",
    "villagePathLampSideBlock": "",
    "villagePathLampStructure": "",
    "villageWellStructure": ["mypack:plaza_spire=3", "mypack:fountain=1", "empty=1"],
    "villagePathPierCargo": ["minecraft:chest=3", "mypack:crate=2,3", "empty=4"],
    "villagePathPierLoot": "resourcedatapackloader:chests/pier_cargo"
  }
}
```

Alles Folgende greift nur, solange `terrainAdaptation` an ist. Jede dieser Einstellungen ist standardmäßig leer oder null, was Vanillas Wege genau so lässt, wie sie waren.

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
| `villagePathAlleyBlock` | Block | leer | Der Belag einer Gasse, eines Weges, der für Linien und Gehwege zu schmal ist. Eine Gasse läuft zwischen den Gehwegen der Straßen, auf die sie trifft, und hat selbst keine; wo sie auf eine Straße trifft, wird kein Übergang gemalt. Leer legt Gassen mit dem Wegblock |
| `villagePathAlleyChance` | Zahl | `0` | Die Wahrscheinlichkeit in Prozent, dass ein Weg als Gasse gelegt wird, statt sich zur vollen Straße zu verbreitern. `0` legt eine Gasse nur dort, wo eine volle Straße nicht passt, in der Praxis also nur im gedrängten ersten Bezirk. Ein höherer Wert ändert, welche Wege gelegt werden, und formt damit das ganze Straßennetz um; bei 50 gemessen kostete er sieben weitere geteilte Kreuzungen, also erhöhen und das Ergebnis prüfen |
| `villagePathFlatRun` | Zahl | `6` | Wie viele Blöcke ein Weg eine Höhe hält, bevor er stuft. An Weltkoordinaten verankert, damit benachbarte Stücke übereinstimmen. `0` stuft jeden Block, wie Vanillas Hänge es tun |
| `villagePathIntersects` | Liste | keine | Muster, die an Kreuzungen gemalt werden, benannt nach Registrierungsschlüssel aus `<namespace>/pathintersects/` eines Packs. Ein Eintrag malt jede Kreuzung gleich; mehrere werden je Kreuzung nach Gewicht gewählt |
| `villagePathPiers` | Liste | keine | Stegformen für eine Straße, die über dem Wasser ins Leere endet, unten aufgeführt. Der überbrückte Auslauf wird zum Steg; mehrere Einträge losen je Steg eine Form aus. Leer bleibt eine solche Brücke eine schlichte Brücke |
| `villagePathDeadEnds` | Liste | keine | Wie eine Straße abgeschlossen wird, die tot endet und keine Wendeschleife bekommen hat, unten aufgeführt. Ein Eintrag schließt jedes tote Ende gleich, mehrere losen je Ende eines aus dem Weltseed. Leer lässt tote Enden offen |
| `villagePathLampBlock` | Block oder Block mit Daten | `minecraft:oak_fence` | Der Block, aus dem eine Laterne an der Straße gebaut wird, am Bordstein gestapelt. Leer stellt keine Laternen |
| `villagePathLampHeight` | Zahl | `3` | Wie viele Blöcke hoch der Mast bis zu seinem Kopf steht |
| `villagePathLampTopBlock` | Block oder Block mit Daten | `minecraft:wool:15` | Der Kopf oben auf dem Mast. Leer lässt ihn kahl |
| `villagePathLampSideBlock` | Block oder Block mit Daten | `minecraft:torch` | Das Licht, das an jeder Seite des Kopfes nach außen hängt. Leer hängt keines |
| `villagePathLampStructure` | Text | leer | Eine Strukturdatei, die als ganze Laterne gesetzt wird, statt die drei Laternenblöcke zu stapeln, benannt `mypack:street_lamp` und aus dem `structures`-Ordner dieses Packs gelesen. Sie wird auf den Laternenplatz zentriert, ihre unterste Lage auf dem Bordstein, und die gesetzten Blöcke werden gehalten, damit nichts sie überschreibt. Leer stapelt die Blöcke |
| `villageWellStructure` | Liste | keine | Strukturdateien, die als Mittelpunkt des Platzes statt des Brunnens gesetzt werden, als gewichtete `name=weight`-Einträge wie `mypack:plaza_spire=3`, aus dem `structures`-Ordner dieses Packs gelesen und einmal je Brunnen aus seiner Position ausgelost, derselbe Brunnen bekommt also immer dieselbe. Ein Eintrag `empty=weight` behält für diesen Anteil den Brunnen. Die gewählte wird auf die sechs mal sechs Grundfläche des Brunnens zentriert, ihre unterste Lage auf dem Platzboden, der Boden darunter wird gepflastert, und die gesetzten Blöcke werden festgehalten, damit die Platzgestaltung sie in Ruhe lässt. Eine breitere Struktur greift über den Ring des Platzes hinaus. Keine Einträge bauen den Brunnen |
| `villagePathPierCargo` | Liste | keine | Fracht, die innen an den Geländern eines Stegs steht, als gewichtete Einträge, unten aufgeführt. Jede zweite Reihe jedes Stegs lost die Liste auf beiden Seiten aus, die Gewichte entscheiden also, wie voll ein Steg wirkt. Leer bleibt ein Steg leer |
| `villagePathPierLoot` | Text | `resourcedatapackloader:chests/pier_cargo` | Die Beutetabelle, aus der Fracht mit Inventar gefüllt wird, ausgelost beim ersten Öffnen. Leer bleibt solche Fracht leer |

Ein Weg wird von der Mitte nach außen ausgebaut: Mittellinie, dann Weg, dann Randlinien, dann Gehwege. Breiten, die nicht passen, fallen zurück statt überzulaufen, ein schmales Stück verliert also still seinen Gehweg, bevor es seinen Weg verliert.

`villagePathBlock` und seine Geschwister gewinnen über `villageBlocks`. Ein benannter Wegblock wird genommen, wie er ist, während die Zuordnung nur das anfasst, was der Weg sonst selbst gewählt hätte. Lässt man sie leer, entscheidet die Zuordnung, und genau so behält ein Pack die biomgerechte Oberfläche und färbt sie trotzdem um.

**Laternenblöcke tragen Daten.** Die drei Laternenblöcke nehmen einen einfachen Namen, einen Namen mit Metadaten oder einen Namen mit Blockobjektdaten in geschweiften Klammern, `minecraft:skull:1{SkullType:3}`. Die Klammern werden als NBT gelesen und nach dem Setzen auf das Blockobjekt angewandt, womit eine Laterne aus einem anderen Mod die Einstellungen behält, die sie braucht. Fehlerhaftes NBT wird gemeldet und übergangen, statt den Bau der Laterne zu verhindern.

**Tote Enden.** Eine Straße, die tot endet und keine Wendeschleife bekommen hat, wird durch `villagePathDeadEnds` abgeschlossen, je Ende wird eine Art aus dem Weltseed ausgelost. Eine Art, deren Block nicht gesetzt ist, fällt aus der Auslosung, `barrier` schließt also nichts, solange `villagePathBridgeBarrierBlock` keinen Block nennt, und das Ende einer Gasse nimmt nie `sidewalk`.

| Wert | Was er bewirkt |
| --- | --- |
| `sidewalk` | Pflastert die Endreihe mit dem Gehwegblock |
| `barrier` | Stellt den Geländerblock entlang der Endreihe auf, `villagePathBridgeBarrierHeight` hoch |

**Stege.** Eine Straße, die aufs Wasser hinausläuft und auf nichts endet, wird zum Steg statt zur Brücke ins Nirgendwo, sobald `villagePathPiers` mindestens eine Form nennt. Mehrere Einträge losen je Steg eine Form aus, aus dem Weltseed und dem Stegende, dieselbe Welt baut also immer denselben Steg. Jeder Steg steht auf Pfählen aus dem Unterbaublock, an beiden Deckkanten in jeder vierten Reihe bis hinab zum Grund gerammt, ganz gleich welcher Form. Das Deck ist der Brückenblock, Geländer und Pfosten der Geländerblock, die Pfähle der Unterbaublock.

| Wert | Was er bewirkt |
| --- | --- |
| `railed` | Behält das volle Deck, schlicht ohne Linien und Gehwegband, und schließt das ferne Ende mit dem Geländerblock |
| `pilings` | Löst die seitlichen Geländer in Pfosten in jeder vierten Reihe auf, die genau über diesen Pfählen stehen |
| `boardwalk` | Verschmälert das Deck auf die Kernbreite der Straße |

**Stegfracht.** `villagePathPierCargo` stellt Fracht auf einen Steg. Jede zweite Reihe lost die Liste einmal je Seite aus, eine Spalte innerhalb der Geländer, damit die Mitte des Decks begehbar bleibt, die abgeschlossene Endreihe frei und nie zwei Frachtstücke nebeneinander stehen, denn zwei Truhen Seite an Seite würden zu einer Doppeltruhe zusammenfallen. Ein Stapel wird nur gesetzt, wo jeder seiner Blöcke Platz hat, und denselben Block zweimal mit verschiedenen Höhen zu nennen ist der Weg zu Stapeln unterschiedlicher Größe.

| Wert | Was er bewirkt |
| --- | --- |
| `<block>=<gewicht>` | Ein Block und sein Anteil an den Plätzen, einen Block hoch gestellt |
| `<block>=<gewicht>,<höhe>` | Derselbe, so viele Blöcke hoch gestapelt, von 1 bis 8 |
| `empty=<gewicht>` | Der Anteil des Decks, der frei bleibt |

Ein Block mit Beuteinventar, eine Truhe zum Beispiel, wird aus `villagePathPierLoot` gefüllt, ausgelost beim ersten Öffnen, wie es eine Vanilla-Truhe tut. Die eingebaute Tabelle ist leicht zu findendes Strandgut. Ein Pack ersetzt sie, indem es eine eigene `loot_tables/chests/pier_cargo.json` im Namensraum `resourcedatapackloader` mitliefert oder eine eigene Tabelle benennt.

**Kreuzungsmuster.** `villagePathIntersects` nennt Dateien, die ein Pack mitbringt, jede davon ein kleines Bild davon, was dort gemalt wird, wo zwei Straßen sich treffen, gezeichnet als Zeilen aus einzelnen Zeichen, ein Zeichen je Block.

`<namespace>/pathintersects/*.json`

Der Pfad der Datei ist der Registrierungsschlüssel des Musters, den `villagePathIntersects` dann nennt.

```json
{
  "name": "Crosswalk",
  "weight": 3,
  "legend": { "w": "minecraft:quartz_block", "y": "minecraft:wool@4" },
  "mouth": ["wwww", "....", "wwww"],
  "corner": ["yy.", "y..", "..."]
}
```

| Schlüssel | Wert | Standard | Was er macht |
| --- | --- | --- | --- |
| `name` | Text | der Dateiname | Der Name, der im Log steht |
| `weight` | int, ab 1 | `1` | Anteil der Kreuzungen, die dieses Muster gewinnt, wenn mehrere genannt sind |
| `legend` | Objekt aus einem Zeichen zu einem Block | keines | Die Zeichen, die die Zeilen über die Rollen unten hinaus nutzen dürfen. Ein Zeichen, das bereits eine Rolle ist, wird mit einer Logzeile abgelehnt |
| `mouth` | Liste aus Text | keine | Zeilen, die auf jeder Zufahrt außerhalb der kreuzenden Straße gemalt werden. Die erste Zeile liegt der Kreuzung am nächsten, die übrigen gehen nach außen. Die Zeichen laufen quer über die Straße und wiederholen sich, wo eine Zeile kürzer ist als die Straße breit |
| `corner` | Liste aus Text | keine | Zeilen, die in der Kreuzung selbst gemalt werden. Die erste Zeile liegt der Kante der kreuzenden Straße am nächsten, und in einer Zeile liegt das erste Zeichen der eigenen Straßenkante am nächsten, weiter nach innen. Eine Zelle, die das Bild nicht erreicht, bleibt unangetastet |

Fünf Zeichen sind Rollen statt Blöcke und folgen damit dem, womit die Straße ohnehin schon gedeckt ist: `r` ist die Straßenoberfläche, `l` die Randlinie, `s` der Gehweg, `.` lässt den Block genau so, wie er war, und `c` ist reserviert und malt die Straßenoberfläche. Eine Rolle, deren Block das Pack nie gesetzt hat, fällt auf die Straßenoberfläche zurück, und jedes andere Zeichen wird in der `legend` nachgeschlagen und fällt ebenfalls auf die Straßenoberfläche zurück.

Welches Muster eine Kreuzung bekommt, wird aus dem Weltseed und der Lage der Kreuzung berechnet, dieselbe Welt malt ihre Kreuzungen also immer gleich. Gemalt wird ein Muster nur dort, wo drei oder mehr Straßen zusammentreffen, an einer Kreuzung wie am Platz eines Brunnens; wo sich nur zwei Straßen treffen, entsteht eine schlichte Ecke.

#### Dorfschmuck

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageDecor": ["mypack:street_flowers=2", "mypack:street_tree=1", "empty=3"]
  }
}
```

`villageDecor` streut die eigene Weltgenerierung eines Packs an die Ränder der Dorfwege, und genau das nimmt einem Dorf den Eindruck, seine Häuser stünden in blankem Gras. Jeder Eintrag lautet `name=gewicht`: Der Name ist ein Registry-Schlüssel aus der Weltgenerierung, `meinpack:street_flowers`, das Gewicht ist der Anteil dieses Eintrags an den Plätzen. Der Name `empty` ist der Anteil der Plätze, die leer bleiben, und auf ihn kommt es an, denn eine Liste ohne ihn füllt jeden Platz an jedem Wegrand, und das Dorf gerät zur Gärtnerei statt zur Straße.

Jeder dritte Block entlang beider Wegseiten ist ein Platz, gezählt nach Weltkoordinaten, damit der Abstand von einem Wegstück ins nächste durchläuft. Ein Platz wird übergangen, wo er in ein Teil des Dorfes fällt, auf den Weg selbst, vor eine Tür, oder wo der Boden nicht offene Luft über etwas Festem ist. Was auf einem Platz wächst, ergibt sich aus dem Weltseed und dem Platz selbst, dieselbe Welt streut also immer gleich.

Der Name zeigt auf einen gewöhnlichen Weltgenerierungs-Eintrag aus `<namespace>/worldgen/*.json`, eine `decoration`, ein `tree` oder ein `imprint` taugen also alle und behalten ihre eigenen Blöcke, Größen und Streuung. Genutzt wird hier allein die Form dieses Eintrags: Seine Biome, Dimensionen, Höhen und Seltenheit sind der Weg, auf dem er sich von selbst über die Welt sät, und das Dorf zieht sie nicht zu Rate – ein Eintrag für den Wegrand schreibt sich daher am besten für nichts anderes. Ein Wegrand ist offene Luft über Boden, ein solcher Eintrag will `replace` also auf `minecraft:air` gesetzt haben; einer, der `replace` nie nennt, bekommt den üblichen Standard `minecraft:stone` und stellt hier stillschweigend nichts hin.

Solange `terrainAdaptation` an ist, wird gehalten, was auf einem Platz wächst, damit ein Baum am Wegrand nicht wieder fällt, sobald der nächste Chunk hergerichtet wird. Ist es aus, gibt es kein Herrichten, wogegen zu halten wäre, und gestreut wird genauso.

### Blast Plaster

Was nach einer Explosion geschieht, aus `<namespace>/blastplaster/*.json`. `default` lässt Packs entscheiden, `global` übergeht Pack-Dateien und legt die Vorgaben dieses Mods über Blast Plasters Config, und `off` gibt Blast Plaster ganz an seine eigene Config zurück.

### Strukturen

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "structureSpacing": ["temples=24", "monuments=40", "mineshafts=200"],
    "structureSeparation": ["monuments=12"],
    "structureMinDistanceFromSpawn": ["strongholds=1000"],
    "structureBiomes": ["temples=minecraft:desert,SANDY"],
    "structureBiomesAreBlacklist": false,
    "structureSpawns": ["temples=minecraft:witch:1:1:1", "monuments="],
    "structureSpawners": ["dungeons=minecraft:zombie,minecraft:husk"],
    "structureAt": ["villages=1000,-500"],
    "structureMost": ["villages=100"]
  }
}
```

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

Nicht jede Struktur versteht jede Einstellung. Der Abstand erreicht Tempel, Monumente, Herrenhäuser, Endstädte und Festungen; bei `mineshafts` bedeutet die Zahl einer von so vielen Chunks statt eines Rasters, weil Vanilla sie so platziert. Die Trennung erreicht Monumente, Herrenhäuser, Endstädte und Festungen, und Dörfer, für die sie die wenigsten Chunks zwischen einem Dorf und dem nächsten ist, was auch immer das Raster erlauben würde. `structureMost` deckelt, wie viele einer Struktur eine Dimension haben darf, `villages=100`: Sind so viele gegründet, wird kein weiteres gegründet, wohin das Raster es auch setzen würde, während ein mit `structureAt` festgenageltes Dorf trotzdem gegründet wird. Nur Dörfer lesen es. Die Biome erreichen jede Struktur außer den Endstädten, weil das Ende in dieser Version ein einziges Biom ist und es nichts auszuwählen gibt. Endstädte suchen sich ihren Platz im Raster trotzdem selbst: Sie sitzen nur auf einer äußeren Insel, deren Oberfläche bis y60 reicht, ein größerer Abstand dünnt sie also aus, kann aber keine über die Leere setzen. Netherfestungen sitzen auf einem festen Raster, das Vanilla nicht offenlegt, sie erreichen also nur die Listen für Biome und Spawnabstand. Dörfer behalten ihre eigenen `villageSpacing`, `villageBiomes` und den Rest.

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

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "surfaceDayMonsterRate": 0.0,
    "surfaceNightMonsterRate": 1.0,
    "undergroundDayMonsterRate": 1.0,
    "undergroundNightMonsterRate": 1.0,
    "monsterCap": 40,
    "creatureCap": 10,
    "ambientCap": 15,
    "waterCreatureCap": 5,
    "monsterSpawnLight": 0,
    "skyAnimals": false
  }
}
```

Spawnraten und Obergrenzen für Mobs, pro Biom. Das Spawnen feindlicher Mobs wird über `surfaceDayMonsterRate`, `surfaceNightMonsterRate`, `undergroundDayMonsterRate` und `undergroundNightMonsterRate` skaliert, jeweils ein Faktor, bei dem `1.0` Vanilla ist – Spawnen bei Tageslicht an der Oberfläche lässt sich also abschalten, ohne die Höhlen anzurühren. Die Obergrenzen sind `monsterCap`, `creatureCap` für friedliche Tiere, `ambientCap` für Fledermäuse und Ähnliches und `waterCreatureCap` für Tintenfische; Vanillas Werte sind 70, 10, 15 und 5, und `-1` lässt eine davon unangetastet. `monsterSpawnLight` begrenzt zusätzlich zu den Vanilla-Prüfungen das Blocklicht, das ein feindlicher Spawn verträgt: `0` ist die moderne Regel, bei der eine Fackel eine Höhle vollständig schützt, und `-1`, der Standard, behält Vanillas Würfeln bei. `skyAnimals` entscheidet, ob sich friedliche Mobs auf dem Land ansiedeln, das eine Rubic-Welt über ihrem Terrainfenster erzeugt, allen voran auf den schwebenden Inseln: `true`, der Standard, lässt Vanillas Herden dort, wo der oberste Block liegt, `false` hält Tiere und Fledermäuse auf dem Boden darunter. Spawner ignorieren beides.

### Strukturen aufsetzen

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "structureAdaptation": ["villages=beard_thin", "mansions=bury", "monuments=none"]
  }
}
```

`structureAdaptation` entscheidet, an welche Strukturen sich das Gelände anpasst und wie, als `structure=modus`-Einträge, `"mansions=bury"`, `"monuments=none"`, für Dörfer, Festungen, Minen, Monumente und Herrenhäuser, mit den fünf Modi, die moderne Versionen nutzen: `none`, `bury`, `beard_thin`, `beard_box` und `encapsulate`. Dörfer sind `beard_thin`, wenn nichts anderes gesetzt ist, und alles andere ist `none`, solange es nicht genannt wird – genau das, was moderne Versionen für sich selbst wählen. Tempel lassen sich noch nicht nennen, weil sie sich erst beim Bauen selbst platzieren, es gibt also rechtzeitig nichts, woran das Gelände sich anpassen könnte.

### Dörfer aufsetzen

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "terrainAdaptation": true
  }
}
```

**Was sie legt, ist endgültig.** Sie formt das Gelände schon beim Erzeugen der Welt um, alles, was sie in einen Spielstand setzt, bleibt dort. Ein Dorf aus einem älteren Build wird von einem neueren weder erneut besucht noch ausgebessert. Zwei Welten aus demselben Seed, aber mit unterschiedlichen Mod-Versionen erzeugt, sehen deshalb nicht gleich aus, und die Dörfer einer Welt zeigen den Stand des Tages, an dem diese Chunks entstanden sind.

`terrainAdaptation` arbeitet um, wie Dörfer ihren Boden wählen und darauf sitzen, dem Geist nach übernommen davon, wie moderne Versionen ihre Strukturen aufsetzen, und dann weitergetrieben. Ein Dorf wird nur auf einem Chunk gegründet, dessen Boden um höchstens zehn Blöcke schwankt, und nie näher als acht Chunks an einem anderen Dorf; Regionen, die keinen solchen Chunk hergeben, gründen gar nichts. Der Brunnen setzt sich auf den tiefsten Boden, den seine eigene Grundfläche berührt, und das ganze Dorf verschiebt sich mit ihm, sodass sich alles Übrige von dort aus einrichtet.

Wege werden beim Legen abgezogen: Die Oberfläche folgt über die Wegbreite dem tiefsten natürlichen Boden, Buckel werden abgetragen, Senken gefüllt, das Gefälle überschreitet nie einen Block pro Schritt, und kurze Schluchten werden mit Brettern überbrückt. Die Wegoberfläche richtet sich nach dem Boden, über den sie führt: Trampelpfade auf Erde, Sandstein auf Sand, gebrannter Ton in der Mesa, Kies auf Stein und auf Kies, Bretter über Wasser. Ein Wüstendorf bekommt so Sandsteinstraßen statt eines Feldwegs, und Wege verschwinden nicht mehr dort, wo der Boden kein Gras ist. Wo zwei Wege sich kreuzen, treffen sie sich auf der niedrigeren der beiden Höhen, denn nur eine Höhe, die beide erreichen können, lässt keine Stufe zwischen ihnen.

Jedes Gebäude sitzt einen Block über dem Weg, an dem es steht, abgelesen vom gelegten Weg oder vorhergesagt aus der Höhe, auf die der Weg den Boden abziehen wird, wenn er noch nicht gebaut ist – so ruhen seine Türstufen auf der Wegoberfläche und seine Tür sitzt dahinter. Ein Gebäude, unter dem irgendwo mehr als zwei Blöcke aufgeschütteter Boden nötig wären, wird dort nicht gebaut: Es rutscht bis zu zwölf Blöcke am Weg entlang auf der Suche nach dem flachsten Sitz und fällt ganz weg, wenn es keinen findet – Dörfer auf zerklüftetem Boden fallen so lockerer aus, statt auf Sockeln zu stehen. Der Ring um ein Gebäude wird bergab aufgeschüttet und bergauf abgetragen, einen Block flacher noch einen Ring weiter außen.

Felder behalten Vanillas eigene Bodenhöhe. Laternenpfähle stehen auf der Höhe des Weges, den sie beleuchten, statt auf der Schulter daneben, mit aufgefülltem Boden darunter, wo der Weg über dem Randstreifen liegt, und Vanillas eigene Fackelpfosten bleiben aus dem Grundriss heraus, weil diese sie ersetzen. Unter jedem Gebäude wird bis zur nächsten tragenden Fläche mit demselben Material aufgefüllt, auf dem es steht, Wände und Türöffnungen werden aus Hängen herausgeschnitten, Erde wird von Dächern gehoben, und jeder Baum, der in einer Struktur steht, wird ganz gefällt, seine Blätter gehen mit seinem Holz, während jedes Blatt, das noch zu einem stehenden Ast gehört, in Ruhe gelassen wird. Herrenhäuser und die verstreuten Bauwerke (Tempel, Hütten, Iglus) müssen dieselbe Anforderung an flachen Boden erfüllen, bevor sie gesetzt werden dürfen.

Es formt das Gelände selbst beim Entstehen um, eine Welt, die damit generiert wurde, unterscheidet sich also von einer, die ohne generiert wurde – dieselbe Warnung, die moderne Versionen mitbringen –, und es ist aus, solange ein Pack oder die Config nicht darum bittet.

### Grundgestein

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "flatBedrock": true,
    "flatBedrockRetrogen": false,
    "bedrockLayers": 1,
    "flatBedrockRoof": true,
    "flatBedrockFiller": "minecraft:stone",
    "flatBedrockFillers": ["-1=minecraft:netherrack", "1=minecraft:end_stone"],
    "flatBedrockDimensions": [0, -1],
    "flatBedrockDimensionsAreBlacklist": false,
    "flatBedrockBiomes": ["minecraft:plains"],
    "flatBedrockBiomeTypes": ["MOUNTAIN"],
    "flatBedrockBiomesAreBlacklist": true
  }
}
```

`flatBedrock` ersetzt die zerklüftete Schicht durch flache, pro Dimension und pro Biom, mit einem Füllblock deiner Wahl. `flatBedrockRetrogen` macht das auch mit Chunks, die es schon gibt. Es lässt sich nicht rückgängig machen, das ursprüngliche Muster wird nirgends festgehalten. `bedrockLayers` legt fest, wie viele Schichten bleiben, `flatBedrockRoof` macht auch die Decke, wo eine Dimension eine hat, und `flatBedrockFiller` ist das, was das weggenommene Grundgestein ersetzt, leer gelassen wird pro Dimension gewählt, und `flatBedrockFillers` nennt stattdessen einen pro Dimension. Welche Dimensionen und Biome es erreicht, bestimmen `flatBedrockDimensions`, `flatBedrockBiomes` und `flatBedrockBiomeTypes`, wobei `flatBedrockDimensionsAreBlacklist` und `flatBedrockBiomesAreBlacklist` diese Listen zu Ausschlüssen machen.

### Langsameres Ticken in der Ferne

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "slowDistantEntities": true,
    "slowedKinds": ["items", "experience", "projectiles"],
    "slowDistance": 192,
    "slowRate": 4,
    "neverSlowed": ["minecraft:armor_stand"],
    "slowRecheck": 20
  }
}
```

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

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "hurryWritesAbove": 100
  }
}
```

Das Spiel schreibt fertige Chunks auf einem eigenen Thread, einen nach dem anderen, und ruht nach jedem eine hundertstel Sekunde. Das hält es bei etwa hundert Chunks pro Sekunde, egal wie schnell die Platte ist, was beim Spielen reichlich und beim Bau von Land in großen Mengen bei Weitem nicht genug ist – die ungeschriebenen Chunks stapeln sich stattdessen im Speicher. `hurryWritesAbove` sagt, wie viele warten dürfen, bevor es aufhört zu ruhen und einfach so schnell schreibt, wie es kann. `100` ist der Standard und trifft den Punkt, an dem das Spiel selbst die Generierung zu bremsen beginnt; `0` lässt es immer ruhen, so wie das Spiel es tut. Solange die Zahl der Wartenden klein ist, ändert sich nichts, und das ist jeder gewöhnliche Moment des Spielens.

Jedes Mal, wenn das Aufräumen läuft, wird dafür eine Zeile geschrieben, während es passiert: welcher Sammler lief, wie lange er brauchte, was vorher und nachher gehalten wurde und wie viel Platz das Spiel zu diesem Zeitpunkt hatte. Ändert sich dieser Platz, wird das gesagt, denn der wachsende Platz ist selbst die Ursache der längsten dieser Pausen: Ein Spiel, das mit weniger Platz startet, als es am Ende braucht, hält an, um ihn zu vergrößern, immer wieder, in Momenten, die nichts mit dem zu tun haben, was es gerade tut. Es mit so viel Platz zu starten, wie es haben darf, vermeidet das vollständig.

Eine letzte Zeile sagt, wie viel Arbeitsabfall seit dem letzten Blick weggeworfen wurde, wie lange das Aufräumen davon dauerte und wie viele Durchgänge das waren, und wie viel des erlaubten Platzes das Spiel gerade hält. Land zu bauen wirft naturgemäß eine Menge weg, weil jeder Chunk vor dem Schreiben in frische Arrays umgewandelt wird, und dieses Aufräumen passiert zwischen den Runden statt während ihnen, es zeigt sich also als Hänger und nicht als Zeit in einer der Zahlen oben.

### Spawn-Chunks

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "spawnChunkRadius": 128,
    "spawnChunkRadii": ["0=64", "7=0"]
  }
}
```

Das Spiel hält die Chunks um den Spawnpunkt einer Welt geladen, ob jemand da ist oder nicht, damit Mods irgendwo etwas haben, das immer tickt. Das sind 128 Blöcke in jede Richtung, etwa 289 Chunks, und im Spiel lässt sich das nicht einstellen. `spawnChunkRadius` setzt diese Entfernung. `128` ist das, was das Spiel macht, und der Standard, eine kleinere Zahl hält einen kleineren Anker, und `0` hält gar keine, der Spawnbereich entlädt also wie überall sonst. `spawnChunkRadii` setzt einen Radius für einzelne Dimensionen, geschrieben als `dimension=blöcke`, einer pro Zeile, und überschreibt `spawnChunkRadius` für die genannten Dimensionen.

Nur eine Dimension, die dafür registriert wurde, ihren Spawn zu halten, hält einen, und das ist im Spiel selbst allein die Oberwelt – der Nether und das Ende hielten nie einen, das dafür zu setzen ändert also nichts. Eine Dimension, die ein Mod hinzufügt, hält nur dann einen, wenn dieser Mod darum gebeten hat, und ein Mod, der das getan hat, schleppt oft weitere 289 Chunks mit, die ein Pack nie wollte. Ob eine Welt überhaupt geladen bleibt, ist eine andere Sache, die das hier nicht berührt: Eine Dimension, die ein Mod als dauerhaft geladen markiert hat, bleibt auch bei `0` geladen, sie hält nur keine Chunks mehr. Die meisten Mods, die den Spawn als Anker nutzen, wollen dort irgendetwas haben und keine 289 Chunks davon, eine kleine Zahl hält sie also meist am Laufen, während eine `0` das nicht tut.

### Void-Welt

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "voidWorld": true,
    "voidPlatformBlock": "minecraft:stone",
    "voidPlatformSize": 5,
    "voidPlatformHeight": 64,
    "voidWorldDimensions": [0],
    "voidWorldDimensionsAreBlacklist": false
  }
}
```

`voidWorld` generiert eine leere Welt mit einer Plattform am Spawnpunkt und unterbindet Mobs, Tiere, Strukturen und alles, was ein Mod dort sonst generieren würde. Block, Größe und Höhe der Plattform sind `voidPlatformBlock`, `voidPlatformSize` und `voidPlatformHeight`; die Größe wird auf eine ungerade Blockzahl abgerundet, damit die Plattform mittig auf dem Spawn sitzt. `voidWorldDimensions` wählt, welche Welten geleert werden, standardmäßig allein die Oberwelt, und `voidWorldDimensionsAreBlacklist` macht aus dieser Liste die, die in Ruhe gelassen werden. Der Nether und das Ende werden genauso geleert wie die Oberwelt, ob es die sind, die diese Version baut, oder solche, die ein Mod an ihre Stelle gesetzt hat. Nur die Oberwelt bekommt eine Plattform, einen Weg in einen geleerten Nether oder ein geleertes Ende liefert ein Pack also selbst. Ein geleertes Ende hat außerdem keinen Drachen, keine Kristalle und keinen Grundgestein-Brunnen, weil der Kampf, der sie baut, nie beginnt.

### Der Drache

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "dragonFight": true
  }
}
```

`dragonFight` gehört zur Gruppe `structures` und entscheidet, ob das Ganze überhaupt stattfindet: der Drache, seine Leiste, die Kristalle, der Brunnen, auf dem er steht, und das Wiederbeleben, das ein Spieler mit Enderkristallen starten würde. Ein geleertes Ende lässt ihn weg, solange ein Pack nicht darum bittet, und ein gewöhnliches Ende hat ihn, solange ein Pack nicht etwas anderes sagt – `dragonFight` lohnt sich also in beide Richtungen.

### Gelände

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldType": "biomesop",
    "worldTypeExceptions": ["flat", "debug_all_block_states"],
    "worldSeed": "Hollow Ridge",
    "generatorOptions": "3;minecraft:bedrock,59*minecraft:stone,3*minecraft:dirt,minecraft:grass;1",
    "terrainWorldTypes": ["default", "customized"],
    "terrainWorldTypesAreBlacklist": false
  }
}
```

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

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "logBlockedOres": true,
    "logBlockedBiomes": true,
    "logBlockedGenerators": true,
    "logBlockedRecipes": true,
    "logBlockReplacements": true
  }
}
```

`logBlockedOres`, `logBlockedBiomes`, `logBlockedRecipes` und `logBlockReplacements` protokollieren jeweils das erste Mal, dass etwas abgewiesen wird, du siehst also, was eine Sperrregel tatsächlich erwischt hat, statt es aus dem zu erraten, was fehlt. Sie sind das Erste, was man einschaltet, wenn eine Regel nichts oder zu viel zu tun scheint.

### Rezepte

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockRecipes": true,
    "recipeWhitelist": ["minecraft", "mypack"],
    "blockedRecipeMods": ["tconstruct"],
    "blockFurnaceRecipes": true,
    "furnaceWhitelist": ["minecraft", "mypack"],
    "blockedFurnaceMods": ["tconstruct"],
    "recipeMatch": "recipe"
  }
}
```

`blockRecipes` und `blockFurnaceRecipes` entfernen alles außer den Mods in ihren Whitelists. Nichts ist standardmäßig ausgenommen, trag also den Namespace deines eigenen Packs ein, um seine Rezepte zu behalten. Ergänzungen von CraftTweaker und GroovyScript überleben immer, egal was die Whitelist sagt. Die Whitelists sind `recipeWhitelist` und `furnaceWhitelist`; `blockedRecipeMods` und `blockedFurnaceMods` gehen in die andere Richtung und entfernen die Rezepte eines genannten Mods, egal was die Whitelist sagt. `recipeMatch` entscheidet, woher die Mod-ID gelesen wird, wenn Handwerksrezepte blockiert werden: `recipe`, der Standard, nimmt den Namen des Rezepts, `output` nimmt das Item, das es herstellt, und `both` blockiert, wenn eines von beiden passt, und verschont, wenn eines von beiden auf der Whitelist steht.

## Universal Tweaks

Universal Tweaks überschneidet sich mit mehreren Vanilla-Tweaks dieses Mods. Wo das passiert, tritt dieser Mod zurück (jedes Mal im Log vermerkt, mit dem, was übersprungen wurde), statt dass zwei Mods dieselbe Methode bearbeiten.

| Was sich überschneidet | Wann dieser Mod zurücktritt |
| --- | --- |
| `promptLeafDecay` | Universal Tweaks hat `Fast Leaf Decay` an |
| `lenientPaths` | Universal Tweaks hat `Lenient Paths` an |
| `cactusMaxHeight` | Universal Tweaks ist installiert |
| `caneMaxHeight` | Universal Tweaks ist installiert |
| Rückweg durchs Netherportal | Universal Tweaks ist installiert |

Die ersten beiden lesen die eigenen Schalter von Universal Tweaks aus `config/Universal Tweaks - Tweaks.cfg`, einen dort abzuschalten gibt diese Aufgabe also hierher zurück. Das Höhenpaar hat keinen solchen Schalter zum Auslesen, nur `Cactus Size` und `Sugar Cane Size`, dieser Mod tritt also zurück, sobald Universal Tweaks überhaupt da ist, und du setzt die Höhe dort.

**Rückweg durchs Netherportal:** Dieser Mod merkt sich, wo du den Nether betreten hast, und setzt dich dorthin zurück, statt Vanillas Suche nach dem nächstgelegenen Portal zu überlassen. Universal Tweaks hat dafür eine eigene Behandlung, das hier wird also komplett übersprungen, wenn es installiert ist.

**Nichts davon berührt ein Pack.** Alles oben betrifft Minecrafts eigene Kakteen, Zuckerrohre, Blätter, Pfade und Portale. Blöcke, die dein Pack definiert, bringen ihr eigenes Verhalten mit, und Pack-Portale unter `portals/*.json` sind ein eigenes System, das Universal Tweaks nie zu sehen bekommt.

## Mo' Villages

Mo' Villages ergänzt Dorf-Biome und tauscht Dorfmaterialien – beides können auch Packs setzen. Anders als bei den Universal-Tweaks-Überschneidungen behält hier das Pack das letzte Wort.

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

`<namespace>/blastplaster/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

Blast Plaster (eine Abhängigkeit dieses Mods) behandelt, was nach einer Explosion passiert: Krater Block für Block heilen, baumbewusstes Fällen, Drop-Kontrolle. Allein liest es eine globale Config. Aus einem Pack gesteuert antwortet es **pro Dimension**, und das Pack liefert die Entscheidung mit, statt Spieler an die Config zu schicken. Das Fällen von Bäumen in Dörfern nutzt außerdem seine Baumgeometrie, weshalb ein Baum über einer neuen Straße ganz herunterkommt. Ohne Pack-Dateien verhält sich Blast Plaster genau so, als wäre es allein installiert.

Was oben in der Datei steht, gilt überall; ein `dimensions`-Block überschreibt es für eine Dimension anhand ihrer Id. Alles, was ein Pack nie nennt, behält das, was Blast Plasters eigene Config sagt, ein Pack setzt also die Handvoll, um die es ihm geht, und lässt den Rest in Ruhe.

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
  "dimensions": {
    "-1": { "explosionMode": "HEAL", "minimumTicksBeforeHeal": 200 },
    "1": { "enableExplosionSmoke": false }
  }
}
```

`explosionMode` ist der Hauptschalter: `HEAL` stellt den Krater mit der Zeit wieder her, `EJECT_DROPS` lässt das Loch stehen und wirft etwa ein Drittel der Blöcke ab (Vanilla-Verhalten), `VISUAL_TOSS` lässt das Loch stehen und wirft nichts ab. Von einem Pack gesteuert ist die Vorgabe `EJECT_DROPS` (nicht Blast Plasters `HEAL`), eine unkonfigurierte Installation verhält sich also wie Vanilla.

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

**Vollständig Vanilla-Optik:** `EJECT_DROPS` plus `healFullTrees`, `enableFakeTossedBlocks`, `enableExplosionFlash`, `enableExplosionSmoke`, `preventMobDrops` und `playerTNTAlwaysDrops` alle aus. Jeder Schlüssel ist pro Dimension setzbar.

**Vanilla-Clients** merken nichts Ungewöhnliches. Der Blitz ist das einzige Feature, das einen Block setzt; mit gesetztem `vanillaClients` wird er darum erzwungen abgeschaltet, der Rest sind Partikel und Items, die ein blanker Client versteht.

Keine Pack-Schlüssel: Blast Plasters Debug-Log und seine Holz-zu-Laub-Paarung (die Baumerkennung muss eine Antwort für das ganze Spiel sein). Beides bleibt in Blast Plasters eigener Config.

## Grab-Mods

Keine Einrichtung nötig. `player_loot`-Items kommen zu den normalen Todesdrops, bevor ein Grab-Mod sie liest, und landen darum mit dem Inventar im Grab – funktioniert mit Gravestone, GraveStone Mod, Corail Tombstone und allem anderen, was mit der Dropliste des Todes arbeitet. Pro Eintrag umgeht `dropLoose` die Dropliste, sodass die Items für den Töter auf dem Boden liegen, statt ins Grab zu wandern. Schlüssel und der `dropLoose`-Vorbehalt: [Spielerbeute](#spielerbeute).

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

Die `terrain`-Schlüssel unten, zusammen im `settings`-Block einer Weltvorlage:

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldName": "Ruby World",
    "worldGameMode": "creative",
    "worldSpawn": "0,72,0",
    "worldBorder": 4096,
    "worldTime": 6000,
    "worldDifficulty": ["normal", "-1=hard"],
    "weatherCeiling": ["0=128"],
    "cloudHeight": ["0=384"]
  }
}
```

**`worldName`** (Gruppe `terrain`) füllt das Namensfeld des Erstellungsbildschirms vor; der Speicherordner folgt daraus wie üblich. Es füllt das Feld nur, solange dort noch der Spielstandard steht – ein vom Spieler getippter Name wird nie überschrieben –, und wird anders als Seed und Spielmodus hinterher nicht erneut gesetzt: Was beim Erstellen im Feld steht, ist der Name.

**`worldGameMode`** (Gruppe `terrain`): `survival`, `hardcore`, `creative`, `adventure` oder `spectator`. Gilt nur beim Erstellen der Welt; bestehende Welten bleiben unberührt, spätere Moduswechsel ebenso. `hardcore` ist Überleben plus das Vanilla-Hardcore-Flag für den ganzen Spielstand; `creative` schaltet zusätzlich Befehle frei, wie das Häkchen des Erstellungsbildschirms. Der Bildschirm öffnet mit dem Modus (und dem Seed des Packs) vorausgewählt; der Spieler darf dort ändern, das Pack setzt es beim Erstellen zurück. `adventure` und `spectator` werden dort nicht angeboten und beim Erstellen der Welt angewendet.

**`worldSpawn`** (Gruppe `terrain`): `x,z` oder `x,y,z`. Gilt nur beim Erstellen. Ohne y wird die Oberfläche auf der Bodenhöhe des Welttyps genommen. Nicht-ganzzahlige Einträge werden gemeldet und ignoriert. Besonders relevant auf Superflach: Vanillas Spawnsuche sucht Gras auf Meereshöhe, findet auf einem Schichtstapel nie welches und kann Hunderte Blöcke abwandern – `worldSpawn` nagelt den Spawn fest.

**`worldBorder`** (Gruppe `terrain`): Durchmesser der Weltgrenze in Blöcken, dieselbe Zahl wie bei `/worldborder set`. Gilt beim Erstellen; `0` (Standard) lässt die Grenze in Ruhe; verschieben per Befehl geht weiterhin. `worldBorderLimit` in der Config deckelt, was ein Pack verlangen darf – ein Pack, das mehr will, wird abgelehnt und protokolliert, nicht gekürzt, ein Pack kann einem Server also keine Grenze verpassen, der der Betreiber nicht zugestimmt hat.

**`worldTime`** (Gruppe `terrain`): ein Tick-Wert wie bei `/time set` (`18000` Mitternacht, `6000` Mittag). Hält die Uhr der Oberwelt an; alles, was die Tageszeit liest (Mobspawn, Schlafen), sieht den festgehaltenen Wert. `-1` (Standard) lässt die Zeit laufen. Das Oberwelt-Gegenstück zum `fixedTime` einer eigenen Dimension, unabhängig von `doDaylightCycle`.

**`worldDifficulty`** (Gruppe `terrain`): `peaceful`, `easy`, `normal` oder `hard`. Ein bloßer Wert gilt für jede Dimension; Zeilen der Form `Dimension=Schwierigkeitsgrad` (`-1=hard`) überschreiben pro Dimension. Die Sperre hält dem Pausenmenü stand. Leer (Standard) überlässt den Schwierigkeitsgrad dem Spieler.

**`cloudHeight`** (Gruppe `terrain`): die y, auf der die Wolken gezeichnet werden. Ein bloßer Wert gilt für jede Dimension; Zeilen der Form `Dimension=y` (`0=384`) überschreiben pro Dimension. Ein Pack mit hohen Gebäuden setzt das, damit die Skyline unter den Wolken steht statt durch sie hindurch, und in einer Rubic-Welt ist es eine absolute y, eine angehobene Decke ist also der Platz, die Wolken darüber zu legen. Leer (Standard) behält die Höhe des Spiels, 128 in der Oberwelt, in einer Rubic-Welt um `terrainOffset` angehoben.

**`weatherCeiling`** (Gruppe `terrain`): die höchste y, die Regen und Schnee erreichen. Ein bloßer Wert gilt für jede Dimension; Zeilen der Form `Dimension=y` (`0=128`) überschreiben pro Dimension. Darüber fällt kein Regen, setzt sich kein Schnee ab, füllen sich keine Kessel, schlägt kein Blitz ein und wird kein Niederschlag gezeichnet; darunter bleibt das Wetter unverändert. Leer (Standard) heißt keine Grenze. Eis ist Temperatur und kein Niederschlag, bildet sich also weiterhin über der Linie.

**Weltphysik** – vier `terrain`-Schlüssel, jeder ein Multiplikator des Vanilla-Werts (`1.0` = unverändert), jeder mit einem bloßen Wert für alle Dimensionen oder `Dimension=Wert`-Überschreibungen:

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldGravity": ["0.17", "0=1.0"],
    "worldFallDamage": ["0.17"],
    "worldJumpStrength": ["1.0"],
    "worldTerminalVelocity": ["1.0"]
  }
}
```

| Einstellung | Skaliert | Anmerkungen |
| --- | --- | --- |
| `worldGravity` | Fallbeschleunigung von Spielern, Mobs, fallengelassenen Items, fallenden Blöcken, Pfeilen, Geworfenem, TNT und Erfahrungskugeln | `0.17` ist mondartig; Sprungbögen und Wurfweiten ziehen von selbst mit |
| `worldFallDamage` | Fallschaden | Eine Dimension mit wenig Schwerkraft will das meist passend gesetzt haben |
| `worldJumpStrength` | Sprunggeschwindigkeit | Wird zusätzlich zur Schwerkraftänderung angewendet |
| `worldTerminalVelocity` | Maximale Fallgeschwindigkeit, als Anteil der Vanilla-Obergrenze | Elytrenflug bleibt unberührt |

Alle vier leer (Standard) behalten die Vanilla-Physik. Auf Galacticraft-Dimensionen skaliert der Gravitationsschlüssel Galacticrafts eigene Schwerkraft.

**Weltnähte** — Dimensionen vertikal stapeln: wer eine Welt durch Boden oder Decke verlässt, gelangt bei gleichem x und z in die Dimension darunter oder darüber.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldBelow": ["0=-1"],
    "worldAbove": ["-1=0"],
    "worldSeamEntities": true,
    "worldSeamBedrock": false
  }
}
```

| Einstellung | Wert | Standard | Wirkung |
| --- | --- | --- | --- |
| `worldBelow` | Zeilen `dimension=ziel`, oder eine nackte Id für jede Dimension | keiner | Dimension, in die ein Sturz unter den Weltboden führt |
| `worldAbove` | dasselbe | keiner | Dimension, in die der Aufstieg über die erzeugte Obergrenze führt, also über das Netherdach und nicht über das Baulimit |
| `worldSeamEntities` | boolean | `true` | Ob Gegenstände, Mobs und andere Entitäten mitreisen oder nur Spieler |
| `worldSeamBedrock` | boolean | `false` | Bedrock an einer Nahtgrenze behalten. Aus, erzeugt die Grenze keinen, der Weg hindurch lässt sich also graben |

Beide Listen leer (Standard) halten jede Welt geschlossen. Die äußerste Blockschicht einer Welt ist ihre Tür: wer die unterste betritt, fährt hinunter, wer die oberste betritt, kommt zurück herauf. Ankünfte landen davon frei, drei Schichten tief nach unten und eine nach oben, damit niemand sofort zurückgeworfen wird. Nach unten wird zugleich alles über der Ankunft bis zur Tür hin freigeschlagen, der Weg hinein bleibt also von unten sichtbar und dient als Rückweg.

Schlägt man einen Block in einer Türschicht heraus, scheint die Welt dahinter hindurch: unter dem Boden der Himmel der Dimension darunter, über der Decke der Himmel der darüber. Das zeichnet allein der Client, innerhalb der Sichtweite, und an der Welt selbst ändert es nichts. Der Schwung bleibt erhalten.

Die Übergänge eines Spielers werden gemerkt. Der Weg nach unten markiert das Loch, und wer in dessen Nähe zurückkommt, landet dort, wo ihn dieses Loch beim letzten Mal abgesetzt hat, ein oft benutzter Schacht bringt einen also immer an dieselbe bekannte Stelle statt jedes Mal woandershin. Die erste Rückkehr ermittelt den Platz: diese Stelle, wenn dort fester Boden darunter liegt, sonst der nächste freie Stand, Höhe für Höhe von der Naht nach außen, und sonst eine Nische im Rand direkt neben dem Loch, denn ein glatt hinuntergegrabener Schacht hat noch keine eigene Kante. Wer ohne eigenes Loch in der Nähe nach oben wechselt, bekommt dort einfach einen neuen Platz. Kommt man von unten und ist nirgends fester Stand, bleibt die Oberfläche derselben Säule. Fuß- und Kopfhöhe werden freigehauen, falls die Stelle im Fels liegt, wobei diese Blöcke ordentlich abgebaut werden und fallen, Behälter eingeschlossen.

Ketten entstehen, indem jede Dimension eigene Zeilen bekommt; Reiter und Reittiere wechseln getrennt.

Tore gelten für Spieler. Wer das Ziel nicht freigeschaltet hat, bekommt die Meldung des Tors und wird auf den zuletzt betretenen festen Boden gesetzt, sonst auf einen Sims nahe der Naht; Nähte setzen keine Blöcke, ein verriegelter Schacht lässt sich also nicht durch Hineinspringen abfarmen. Gegenstände und Mobs haben kein eigenes Tor: bei eingeschaltetem `worldSeamEntities` reisen sie unabhängig davon, wer sie verloren hat, ausgeschaltet fallen sie bei offenem Boden an ihm vorbei und sind verloren wie in jedem Loch. `worldSeamBedrock` schließt stattdessen den Boden; ein Pack, das den Bedrock behält, stellt den Durchgang selbst bereit, üblicherweise mit einer [Eigenschaftsüberschreibung](#eigenschaften-überschreiben), die `minecraft:bedrock` eine positive `hardness` gibt. Vor der Naht erzeugte Chunks behalten ihren Bedrock.

**Rubic-Welten** – auch `rubicWorld`, `worldMinHeight`, `worldMaxHeight`, `rubicWorldDimensions`, `rubicWorldDimensionsAreBlacklist` und `terrainOffset` sind `terrain`-Schlüssel: siehe [Rubic-Welten](#rubic-welten).

**Strukturen** für eine Weltvorlage und für die Listen der Gruppe `structures` selbst. `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges`, `endcities`, `caves`, `ravines` und `reccomplex`, das alles abschaltet, was Recurrent Complex von sich aus erzeugt – seine natürlichen Strukturen und seine Dekorations-Stellvertreter –, während das, was schon in der Welt steht, unangetastet bleibt. Acht weitere benennen, was der Populate-Schritt setzt, statt eines Strukturgenerators: `dungeons`, `waterlakes`, `lavalakes`, `netherlava`, `fire`, `glowstone`, `ice` und `animals`.

**Kreaturtypen** für Biom-Spawns und -Raten. `creature`, `monster`, `ambient`, `water_creature`.

**Rollen** für `roles` einer Weltvorlage. `ocean`, `river`, `beach`, `mushroom`, `swamp`, `hills`, `mountain`, `jungle`, `forest`, `savanna`, `sandy`, `mesa`, `snowy`, `wasteland`, `plains`, `water`. Jede benennt ein Biom, das diese Rolle füllt, sobald die Blockierung die weggenommen hat, die es sonst getan hätten.

**Erztypen** für `oreTypes`. `COAL`, `IRON`, `GOLD`, `REDSTONE`, `DIAMOND`, `LAPIS`, `EMERALD`, `QUARTZ`, `DIRT`, `GRAVEL`, `DIORITE`, `GRANITE`, `ANDESITE`, `SILVERFISH`, `CUSTOM`.

## Ordnerliste

Jeder Ordner, mit vollem Pfad und einem Link zum Abschnitt, der ihn beschreibt, steht unter [Wo die Dateien liegen](#wo-die-dateien-liegen).

## Befehle

`/rdpl` läuft auf deinem eigenen Rechner und braucht keine Rechte, weil alles, was er anfasst, dir gehört. Ein Reload liest den Ordner neu ein, der dir gehört, wendet deine [Eigenschafts-Overrides](#eigenschaften-überschreiben) erneut auf deine eigene Kopie der Blöcke und Items an und lädt deine eigenen Ressourcen neu; er erreicht keinen Server, die Kopie des Servers wird also stattdessen mit `/rdplserver reload` neu geladen. In einem Einzelspielerspiel sind beide dieselbe Maschine, `/rdpl reload` lädt dort also auch Beutetabellen, Fortschritte und Funktionen des integrierten Servers neu, genau wie Vanillas eigener Reload. Er funktioniert auf jedem Server, ob der Server den Mod hat oder nicht.

| Befehl | Stufe | Was er macht |
| --- | --- | --- |
| `/rdpl list` | keine | Jedes geladene Pack, seine Priorität und was es enthält. Klick ein Pack an, um eine Datei darin nachzuschlagen |
| `/rdpl which <namespace:path>` | keine | Welches Pack eine bestimmte Datei liefert und welche Packs es dabei verdeckt |
| `/rdpl reload` | keine | Den Ordner neu einlesen und alles neu laden |
| `/rdpl reload <group>` | keine | Nur eine Sorte neu laden: `textures`, `models`, `languages`, `sounds` oder `shaders` |
| `/rdpl unused` | keine | Dateien in deinen Packs, nach denen noch nichts gefragt hat, meist ein Tippfehler im Pfad |
| `/rdpl config unused` | keine | Optionsdateien in `rdploader/config`, die kein installiertes Pack mehr definiert |
| `/rdpl config prune` | keine | Diese Dateien löschen |
| `/rdpl pixelmap <namespace:path>` | keine | Was aus einer [Pixelkarte](#texturen-als-pixelkarte) geworden ist, Zeichen für Zeichen |
| `/rdpl biome list` | keine | Jedes Biom, das generieren kann, mit seiner ID |
| `/rdpl biome here` | keine | Das Biom, in dem du stehst |
| `/rdpl biome find <name>` | die des Servers | Verknüpft. Geht an `/rdplserver biome find`, die einzige Seite, die den Seed kennt |
| `/rdpl oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro`, `goto` | die des Servers | Verknüpft. Wird wortwörtlich an `/rdplserver` weitergereicht, der entscheidet, siehe die Tabelle unten |

**Welche Server-Unterbefehle verknüpft sind und warum die übrigen nicht.** Ein Server-Unterbefehl bekommt genau dann eine Weiterreichung, wenn der Client für diesen Namen keine eigene Bedeutung hat: `oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro` und `goto` können immer nur die des Servers meinen, `/rdpl` gibt sie also weiter. Die sechs, die der Client ebenfalls hat, `reload`, `list`, `which`, `unused`, `config` und `biome`, behalten ihre eigene Bedeutung von deinen Packs und deinem Client, und ein Weiterreichen würde sie ihnen nehmen. `biome find` ist der eine Teil eines geteilten Namens, der ohnehin dem Server gehört, denn nur der Server kennt den Weltseed; diese eine Form wird also weitergereicht, während `biome list` und `biome here` bei dir bleiben. Damit ist auch die Berechtigung geklärt: Die Operator-Prüfung des Servers entscheidet, und ein Client kann sie weder umgehen noch eine erfundene Antwort bekommen.

Auf einem dedizierten Server macht `/rdplserver` dasselbe für die Kopie des Ordners auf dem Server. Die Spalte Stufe ist die Berechtigungsstufe, die ein Absender braucht: `3` ist ein Operator, `2` lässt auch Befehlsblöcke zu, `0` ist jeder Spieler, und `4` liegt über Operator und erreicht niemanden. Nur `intro` und die drei `goto`-Formen liegen unter Operator, und bewegen kann ein Pack davon `goto`.

| Befehl | Stufe | Was er macht |
| --- | --- | --- |
| `/rdplserver reload` | 3 | Den Ordner des Servers neu einlesen und alles neu laden |
| `/rdplserver list` | 3 | Jedes Pack, das der Server geladen hat, seine Priorität und was es enthält |
| `/rdplserver which <namespace:path>` | 3 | Welches Pack eine bestimmte Datei liefert und welche Packs es dabei verdeckt |
| `/rdplserver unused` | 3 | Dateien in den Packs des Servers, nach denen nichts gefragt hat |
| `/rdplserver config unused` | 3 | Optionsdateien in `rdploader/config`, die kein installiertes Pack mehr definiert |
| `/rdplserver config prune` | 3 | Diese Dateien löschen |
| `/rdplserver oregen` | 3 | Laufende Summen der blockierten Erzgenerierung, pro Mod und Typ |
| `/rdplserver generators` | 3 | Laufende Summen der blockierten Weltgeneratoren, pro Mod und Typ |
| `/rdplserver biome` | 3 | Jedes Biom, das auf dem Server generieren kann |
| `/rdplserver biome list [all]` | 3 | Dasselbe mit der ID jedes Bioms, und `all` nimmt die dazu, die nichts generieren kann |
| `/rdplserver biome here` | 3 | Das Biom, in dem du stehst. Die Konsole steht nirgends, von dort verlangt er also stattdessen einen Spieler |
| `/rdplserver biome here <player>` | 3 | Das Biom, in dem dieser Spieler steht, die Form für Konsole und Skripte |
| `/rdplserver biome find <name>` | 3 | Die nächste Stelle, an der ein Biom generiert, ohne dafür Chunks zu erzeugen |
| `/rdplserver dimensions` | 3 | Jede Dimension, auch die, die Packs hinzugefügt haben |
| `/rdplserver gate list` | 3 | Jedes Tor und ob es offen ist |
| `/rdplserver gate check <player>` | 3 | Welche Tore ein Spieler passiert hat |
| `/rdplserver gate grant <player> <gate>` | 3 | Ein Tor für einen Spieler öffnen |
| `/rdplserver gate revoke <player> <gate>` | 3 | Es wieder schließen |
| `/rdplserver pregen <radius>` | 3 | Jeden Chunk in so vielen Chunks Umkreis erzeugen. Siehe [Vorgenerierung](#vorgenerierung) |
| `/rdplserver pregen <radius> relight` | 3 | Nur den Lichtdurchlauf über bereits vorhandenes Land laufen lassen |
| `/rdplserver pregen status` | 3 | Wie weit ein Lauf ist |
| `/rdplserver pregen stop` | 3 | Ihn beenden |
| `/rdplserver intro` | 0 | Das Welt-Intro beim nächsten Beitritt noch einmal abspielen lassen. Jeder Spieler darf ihn ausführen, und er löscht immer nur sein eigenes |
| `/rdplserver goto <struktur>` | `gotoLevel`, `3` | Bringt dich zur nächsten, bei der noch niemand war, und sucht, ohne das Land auf dem Weg zu erzeugen |
| `/rdplserver goto <struktur> next` | `gotoNextLevel`, `3` | Bringt dich weiter zur nächstgelegenen, zu der du in dieser Sitzung noch nicht gebracht wurdest, ob schon einmal besucht oder nicht |
| `/rdplserver goto <struktur> back` | `gotoBackLevel`, `3` | Bringt dich zur vorherigen zurück und geht Schritt für Schritt durch das, wohin diese Sitzung dich geschickt hat |

**`goto` öffnen.** Jeder Teil von `/rdplserver` braucht einen Operator, Stufe 3, außer `intro`, das der eigene Befehl eines Spielers ist und immer auf Stufe 0 liegt. Die drei `goto`-Formen sind das eine, worüber ein Pack entscheidet: Jede trägt eine eigene Berechtigungsstufe, die ein Pack oder die Config senken darf – getrennt von den beiden anderen und vom Rest des Befehls.

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

Der Wert ist die Berechtigungsstufe, die der Absender braucht. `3` (Operator) ist der Standard. `2` lässt auch Befehlsblöcke zu, ein Pack kann den Sprung also auf einen Knopf oder eine Druckplatte legen, ohne den Rest von `/rdplserver` freizugeben. `0` öffnet ihn für jeden Spieler. Die drei Einstellungen sind unabhängig: etwa `next` offen für Befehlsblöcke einer Dorf-Rundfahrt, während `back` bei den Operatoren bleibt.

Weil `intro` für alle offen ist, kommt jeder Spieler an `/rdplserver` selbst heran, deshalb prüft jeder andere Unterbefehl für sich auf einen Operator und verweigert mit einer Meldung. Die Tab-Vervollständigung zieht mit: Ein Nicht-Operator bekommt `intro` angeboten, und `goto` dazu, sobald eine Stufe es ihm erlaubt.

`gotoPlaceLevels` überschreibt die drei Einstellungen für einzelne Orte, als `name=stufe`-Einträge wie im Beispiel oben. Der Name ist das, was du hinter `goto` tippen würdest: ein Vanilla-Name wie `Village` oder `Mansion`, oder ein mit `locateAs` an einem Imprint-Eintrag angemeldeter Name. Groß-/Kleinschreibung spielt keine Rolle. Stufe `4` liegt über Operator und verschließt den Ort für alle – der Weg, einen einzelnen Ort zu verstecken, während `goto` sonst offen ist.

Ein Eintrag setzt eine Stufe für alle drei Formen dieses Ortes. Ein nicht gelisteter Ort fällt auf die drei Einstellungen oben zurück, und ein nirgends angemeldeter Name passt nie.

Die Tab-Vervollständigung hält sich an dieselben Regeln: Nach `goto` werden nur die Orte angeboten, zu denen der Absender auch wirklich gebracht werden kann.

Sie liegen in der Gruppe `commands`, also entscheidet `control.commands` in der Config, ob ein Pack sie überhaupt setzen darf, und `off` dort hält alles bei Operator, ganz gleich was ein Pack verlangt.

**`/rdpl` erreicht auch den Server-Befehl.** Alles, was `/rdpl` nicht selbst erledigt – `oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro` und `goto` –, wird unverändert an `/rdplserver` weitergereicht und in der Tab-Vervollständigung mit angeboten, im Einzelspieler gibt es also nur einen Befehl zu tippen. Weitergereicht wird Wort für Wort, und der Server entscheidet wie immer, Berechtigungen eingeschlossen; durch den kürzeren Namen wird also nichts geöffnet. Die Unterbefehle, die es doppelt gibt – `reload`, `list`, `which`, `unused`, `biome` und `config` –, bleiben bei `/rdpl` und meinen die Packs des Clients. `biome find` ist die eine Ausnahme innerhalb eines geteilten Namens: Nur der Server kennt den Weltseed, diese Form wird also weitergereicht, während `biome list` und `biome here` von deinem eigenen Client beantwortet werden.

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

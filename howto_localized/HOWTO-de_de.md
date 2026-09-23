# Resource Data Pack Loader

**Ein Ordner, der alles überschreibt, was Minecraft oder ein Mod mitbringt, neuen Inhalt aus JSON beschreibt und steuert, was generiert wird – in jeder Welt, auf Clients und Servern, ohne dass Spieler irgendetwas einschalten müssen.**

Ein fertiges Beispiel. Leg es direkt in `rdploader` und schau dir an, wie jede Datei geschrieben ist.

- [RDPLExamplePack.zip](../example/RDPLExamplePack.zip) nutzt fast jede Art von Datei, die der Loader liest: Blöcke, Items, ein Fluid, ein Kreativ-Tab, Biome, eine Weltvorlage, eine Dimension hinter einem Tor, Worldgen, einen Trank und sein Brauen, einen Dorfbewohner mit Handel, Rezepte, Beute, Änderungen an Vanilla-Dingen, einen Sound, einen Fortschritt und eine Funktion. Seine readme sagt, was im Spiel zu prüfen ist.

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

**Wie Packs funktionieren**
- [Wie Definitionen funktionieren](#wie-definitionen-funktionieren)
- [Was du überschreiben kannst](#was-du-überschreiben-kannst)
- [Packs nur auf dem Server](#packs-nur-auf-dem-server)
- [Registry-Umbenennungen](#registry-umbenennungen)
- [Mod-API](#mod-api)
- [Packs für 1.12.2](#packs-für-1122)

**Blöcke und Items**
- [Blöcke](#blöcke)
- [Behälter](#behälter)
- [Glocken](#glocken)
- [Modelle, Blockstates und Texturen](#modelle-blockstates-und-texturen)
- [Damit Vanilla deinen Block richtig behandelt](#damit-vanilla-deinen-block-richtig-behandelt)
- [Items](#items)
- [Flüssigkeiten](#flüssigkeiten)
- [Materialien, Tabs, Sounds, Tags](#materialien-tabs-sounds-tags)
- [Eigenschaften überschreiben](#eigenschaften-überschreiben)
- [Härtegruppen](#härtegruppen)

**Herstellung, Beute und Handel**
- [Ofenrezepte und Brennstoffe](#ofenrezepte-und-brennstoffe)
- [Tränke, Trankarten und Brauen](#tränke-trankarten-und-brauen)
- [Ambosswerk](#ambosswerk)
- [Blockdrops](#blockdrops)
- [Spielerbeute](#spielerbeute)
- [Dorfbewohner und Handel](#dorfbewohner-und-handel)

**Kreaturen und Gefahren**
- [Entity-Varianten](#entity-varianten)
- [Expositionen](#expositionen)

**Die Welt**
- [Weltvorlagen](#weltvorlagen)
- [Spielregeln](#spielregeln)
- [Biome](#biome)
- [Dimensionen](#dimensionen)
- [Portale und Tore](#portale-und-tore)
- [Die Tiefenwelt](#die-tiefenwelt)
- [Höhlenregionen](#höhlenregionen)

**Die Welt generieren**
- [Worldgen-Einträge](#worldgen-einträge)
- [Formen](#formen)
- [Verteilung](#verteilung)
- [Strukturkarten](#strukturkarten)
- [Dorfgrundstücke](#dorfgrundstücke)
- [Stadtpläne](#stadtpläne)
- [Retrogen](#retrogen)
- [Vorgenerierung](#vorgenerierung)

**Spielmodi**
- [Welt-Intro](#welt-intro)
- [Teams](#teams)
- [Wertung](#wertung)
- [Raids](#raids)

**Steuerung**
- [Die Steuerungsebene](#die-steuerungsebene)
- [Was jede Gruppe macht](#was-jede-gruppe-macht)

**Andere Mods**
- [Blast Plaster Integration](#blast-plaster-integration)

**Referenz**
- [Wertelisten](#wertelisten)
- [Ordnerliste](#ordnerliste)
- [Befehle](#befehle)
- [Gut zu wissen](#gut-zu-wissen)
- [Wenn etwas nicht funktioniert](#wenn-etwas-nicht-funktioniert)
- [Bonus: Vanilla-Tweaks](#bonus-vanilla-tweaks)
- [Schlüssel, die nicht übernommen wurden](#schlüssel-die-nicht-übernommen-wurden)

---

# Erste Schritte

## Was es ist

*erste schritte*

Der Resource Data Pack Loader (RDPL) liest einen einzigen Ordner, `rdploader`, und erledigt drei Aufgaben:

- **Überschreiben.** Eine Datei im Ordner ersetzt die, die das Spiel oder ein Mod geladen hätte. Kein Schalter, keine Einrichtung pro Welt, nichts, was Spieler aktivieren müssen.
- **Neuer Inhalt.** JSON-Definitionen registrieren Blöcke, Items, Flüssigkeiten, Biome, Dimensionen, Tränke und Dorfbewohner. Kein Java, kein Jar.
- **Steuerung.** Erz-, Biom-, Struktur- oder Rezeptgenerierung blockieren, Grundgestein glätten, Spawnraten setzen, die Oberwelt leeren, Weltvorgaben festlegen.

## Wo die Dateien liegen

*erste schritte*

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
| `<namespace>/raids/*.json` | Wellen, die ein Dorf heimsuchen, wenn ein Spieler ein Omen hineinträgt. [Raids](#raids) |
| `<namespace>/entities/*.json` | Entity-Varianten, aufgebaut auf vorhandenen Entities. [Entity-Varianten](#entity-varianten) |
| `<namespace>/hardness/*.json` | Faktoren für Abbauzeit und Explosionswiderstand für Blockgruppen. [Härtegruppen](#härtegruppen) |
| `<namespace>/anvils/*.json` | Verzauberungen, die ein Amboss auf einen genannten Gegenstand legt, ein Fortschritt, den das einbringt, und eine Sperre bis dahin. [Ambosswerk](#ambosswerk) |
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
| `<namespace>/recipe_removals/*.json` | Craftingrezepte, gelöscht nach Name, Namespace oder Ergebnis. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/furnace/*.json` | Ofenrezepte, hinzugefügt und entfernt. [Ofenrezepte und Brennstoffe](#ofenrezepte-und-brennstoffe) |
| `<namespace>/fuels/*.json` | Brenndauern. [Ofenrezepte und Brennstoffe](#ofenrezepte-und-brennstoffe) |
| `<namespace>/brewing/*.json` | Rezepte für den Braustand. [Tränke, Trankarten und Brauen](#tränke-trankarten-und-brauen) |
| `<namespace>/potions/*.json` | Trankeffekte. [Tränke, Trankarten und Brauen](#tränke-trankarten-und-brauen) |
| `<namespace>/potion_types/*.json` | Abgefüllte Tränke aus diesen Effekten. [Tränke, Trankarten und Brauen](#tränke-trankarten-und-brauen) |
| `<namespace>/villagers/*.json` | Berufe der Dorfbewohner. [Dorfbewohner und Handel](#dorfbewohner-und-handel) |
| `<namespace>/trades/*.json` | Was Berufe kaufen und verkaufen. [Dorfbewohner und Handel](#dorfbewohner-und-handel) |
| `<namespace>/loot_tables/*.json` | Beutetabellen, ersetzt. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/loot_injections/*.json` | Ein Pool, der zu einer bestehenden Tabelle dazukommt. [Was du überschreiben kannst](#was-du-überschreiben-kannst) |
| `<namespace>/block_drops/*.json` | Zusätzliche oder ersetzende Drops, Erfahrung eingeschlossen, für Blöcke, die dem Pack nicht gehören. [Blockdrops](#blockdrops) |
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

*erste schritte*

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

*erste schritte*

Öffne das Jar, such die Datei, die du ändern willst, und kopiere ihren Pfad ab `assets` oder `data`:

```
assets/minecraft/textures/block/iron_ore.png                 (im Minecraft-Jar)
rdploader/assets/minecraft/textures/block/iron_ore.png       (dein Override)

data/minecraft/loot_tables/blocks/iron_ore.json              (im Minecraft-Jar)
rdploader/data/minecraft/loot_tables/blocks/iron_ore.json    (dein Override)
```

Der Pfad nach `assets` oder `data` ist immer identisch mit dem Pfad im Jar. Nichts wird umbenannt oder verschoben.

## Packs organisieren

*erste schritte*

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

Eine `pack.mcmeta` in der Wurzel des Zips ist willkommen, aber nicht nötig: Der Mod stellt jedes Pack dem Spiel unter einem einzigen eigenen Eintrag vor, mit dem Pack-Format, das das Spiel erwartet, sodass ein Pack nie an einer Formatnummer veraltet. Leg eine `pack.png` daneben, um dem Eintrag des Ordners ein Symbol zu geben. Ohne eine zeigt der Eintrag das RDPL-Symbol.

## Ressourcenpakete: wer gewinnt

*erste schritte*

Standardmäßig liegen RDPL-Dateien über den Ressourcenpaketen, die ein Spieler auswählt; ein Ressourcenpaket kann sie also nicht überschreiben. `O` oder `N` nach dem `RDPL`-Präfix entscheidet das pro Pack:

```
rdploader/RDPLO Branding        gewinnt immer; Ressourcenpakete kommen nicht heran
rdploader/RDPLN BaseTextures    ein Ressourcenpaket darf es überschreiben
rdploader/RDPL1O Seasonal       Priorität und Override kombiniert
```

Packs ohne Buchstaben folgen der Config-Option `overrideResourcePacks`. `/rdpl list` markiert die überschreibenden Packs. Der Buchstabe muss das Präfix abschließen (gefolgt von Leerzeichen, Bindestrich, Unterstrich oder nichts), `RDPLOverhaul` ist also ein Pack namens `Overhaul`, kein `O`-Flag.

Dieselben Stufen gelten für Datenpakete. Ein mit `N` markiertes Pack liegt unter den Datenpaketen, die eine Welt in ihrem eigenen `datapacks`-Ordner trägt, und ein mit `O` markiertes darüber.

---

# Wie Packs funktionieren

## Wie Definitionen funktionieren

*wie packs funktionieren*

Neben den Ordnern, die Dateien überschreiben, gibt es Ordner, die neue Dinge beschreiben. Eine Definitionsdatei fasst unter `variants` ein oder mehrere Dinge einer Art zusammen, und jeder Schlüssel in `variants` ist ein Registry-Name: `data/mypack/blocks/ore.json` mit einer Variante namens `ruby_ore` registriert `mypack:ruby_ore`. Der Name der Datei selbst ist eine Gruppierung und nicht mehr; eine Datei kann einen Block enthalten oder ein Dutzend, die ihre Einstellungen teilen.

Registriert wird mit der niedrigsten Priorität, die der Loader anbietet: Registriert ein echter Mod denselben Namen, gewinnt der Mod, und deine Datei wird ignoriert. Nichts hier kann einen Mod ersetzen.

**Wo die Grenze liegt.** Alles, was eine eigene Block-Entity, einen Bildschirm, ein Inventar oder eigene Logik pro Tick braucht, braucht einen echten Mod, mit einer Ausnahme: dem Typ [container](#behälter), der ein Inventar und einen Bildschirm mitbringt. Alles darunter ist Freiwild.

### Dein Namespace ist dein Mod

*wie definitionen funktionieren*

Der Namespace, den du wählst, ist in jeder praktischen Hinsicht eine Mod-ID. Nichts davon wird als Mod geladen, und in der Modliste taucht es nie auf, aber alles, was eine Mod-ID liest, liest deine:

- Registry-Namen sind `mypack:ruby_ore`, genau wie die eines Mods, und sie werden in jede gespeicherte Welt geschrieben, die sie enthält.
- Die Whitelists für Erz, Biome und Rezepte in der Config gleichen damit ab, `oreWhitelist = mypack` behält also dein Erz und blockt das aller anderen.
- `/rdpl which`, `/rdplserver oregen` und die Berichte gruppieren danach.
- JEI, Tags und die Abfragen anderer Mods sehen ihn genauso.

Wähl also am Anfang einen Namen und ändere ihn nie wieder. Ein umbenannter Namespace macht alles zu Waisen, was schon in einer Welt liegt, genau wie ein Mod, der seine ID ändert, dafür ist `registry_remap` da.

Das gilt in beide Richtungen: `requires` nimmt einen Pack-Namespace genauso bereitwillig wie eine installierte Mod-ID, ein Pack kann also von einem anderen abhängen und übersprungen werden, wenn das nicht installiert ist.

Fehlt ein Mod oder ein Pack, das in `requires` genannt wird, wird die Definition übersprungen: Eine Zeile geht nach `logs/rdpl.log` und nennt, was gefehlt hat, und das Spiel läuft weiter. Wenn ein Block, den du erwartet hast, nicht im Kreativtab liegt, ist diese Logzeile die erste Stelle zum Nachsehen.

`requires` nimmt nur bloße IDs. Es gibt keine Syntax für Versionsbereiche, es kann also sagen, dass ein Mod da sein muss, aber nicht, welche Version.

Die eigene ID des Mods, `resourcedatapackloader`, ist reserviert. Inhalt darunter zu definieren wird ignoriert und protokolliert, weil es Besitz an Dingen anmelden würde, die dieser Mod selbst registriert. Die Assets dieses Mods zu überschreiben ist weiterhin in Ordnung, nur Inhalt dort zu registrieren nicht.

Jede Tabelle unten folgt den Konventionen aus [Die Tabellen lesen](#die-tabellen-lesen).

Die meisten Definitionen nehmen außerdem `requires` an, eine Liste von Mod-IDs oder Pack-Namespaces, die vorhanden sein müssen, sonst wird die Datei übersprungen.

### Pack-Optionen

*wie definitionen funktionieren*

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

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| ein Optionsname | ja | boolean oder ein Objekt | | `true` oder `false` ist der Standard der Option. Ein Objekt trägt die drei Schlüssel darunter |
| `hide` auf oberster Ebene | nein | boolean | `false` | Hält die Optionen dieses Packs komplett aus dem Optionsbildschirm und aus der erzeugten Datei heraus, während sie den Inhalt weiterhin mit ihren Standardwerten steuern |
| `default` | ja | boolean | | Der Wert der Option, bis der Nutzer ihn ändert. Ein Objekt ohne booleschen `default` wird mit einer Warnung übergangen |
| `hide` innerhalb einer Option | nein | boolean | `false` | Versteckt nur diese eine Option, sie kann also nicht umgelegt werden und bleibt auf ihrem Standard |
| `description` | nein | String | keine | Wird im Optionsbildschirm unter dem Namen der Option angezeigt |

Ein Pack kann neben seinem `assets` und `data` einen `config`-Ordner tragen, mit JSON-Dateien voller true/false-Optionen und ihren Standardwerten:

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

Eine Datei mit `"hide": true` auf oberster Ebene hält die Optionen dieses Packs komplett aus dem Optionsmenü und aus der erzeugten Datei heraus, während die Optionen den Inhalt weiterhin mit ihren Standardwerten steuern. Zwei Dinge wollen das: Inhalt, der noch nicht fertig ist, und Vorlagen-Packs, bei denen die Optionen Maschinerie sind, die die Definitionen zusammenhält, und keine Entscheidung, die irgendwer treffen sollte. Zum Veröffentlichen entfernst du den Schlüssel wieder. Dasselbe geht pro Option: `"hide": true` im Objekt einer Option versteckt nur diese eine, ein fertiges Pack kann also einen Schalter für unfertigen Inhalt oder ein Vorlagen-Gate mitbringen, ohne dass eins davon auftaucht:

    { "enablePackB": { "default": false, "hide": true } }

Da sich eine versteckte Option nicht umlegen lässt, ist eine versteckte Option mit Standardwert true faktisch fest eingeschaltet, für Inhalt, der durch die Options-Maschinerie verdrahtet bleiben muss, aber keine Wahl ist.

Eine Option kann auch ein Objekt mit einer Beschreibung sein, die im Optionsmenü unter ihrem Namen steht:

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

Beim Start werden die Optionsdateien eines Packs zu einer echten Config-Datei, die dem Nutzer gehört, benannt nach dem Pack: `rdploader/config/PackA.json`. Sie wird mit den Standardwerten des Packs angelegt und bei Pack-Updates zusammengeführt, sodass neue Optionen ankommen, ohne anzurühren, was der Nutzer schon eingestellt hat. Änderungen greifen beim nächsten Spielstart, und die Schaltfläche Pack-Optionen auf den Bildschirmen Welt auswählen und Neue Welt erstellen ist der Ort, an dem ein Spieler sie umlegt. Optionen gehören nur benannten Packs, also Zips, weil die erzeugte Datei nach dem Pack benannt ist; lose Dateien unter `rdploader/assets` und `rdploader/data` haben keinen Pack-Namen und tragen keine Optionen, zippe losen Inhalt also zu einem benannten Pack, wenn er einen Schalter braucht.

Die `requires`-Liste jeder Definition kann dann mit einem `config:`-Eintrag eine Option nennen: `"requires": ["config:enableTestingContent"]` registriert diesen Inhalt nur, solange die Option true ist, genau wie ein fehlender Mod ihn überspringen ließe. Ein bloßer Name prüft die Datei jedes Packs, und jedes Pack, das ihn definiert, muss zustimmen; `"config:PackA:enableTestingContent"` nennt ein bestimmtes Pack. Eine Option, die kein Pack definiert, gilt als false und wird einmal im Log vermerkt.

Eine Option, die etwas steuert, womit eine Welt gemacht wurde, merkt sich diese Welt, und bei jedem Speichern der Welt wird das neu festgehalten. Ändere sie und öffne die Welt wieder, und wenn die Änderung Inhalte der Welt unregistriert zurücklässt, wird die Welt vorher gesichert, in den eigenen `backups`-Ordner des Spiels, genau wie es der Bildschirm Welt bearbeiten tut; schlägt diese Sicherung fehl, wird die Welt nicht geöffnet. Der erste Spieler in der Oberwelt erfährt, welche Optionen sich geändert haben, und, wenn eine Kopie gemacht wurde, auch das.

Ein `file:`-Eintrag hängt daran, dass eine Datei oder ein Ordner im Spielordner existiert, um Inhalt an etwas außerhalb von RDPLs eigenen Packs zu koppeln, etwa an das Ressourcenpaket eines anderen Mods: `"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` registriert den Inhalt nur, solange genau diese Datei installiert ist. Der Pfad ist relativ zum Spielordner, immer mit Schrägstrichen, und darf kein `..` enthalten.

### Definitionen vererben

*wie definitionen funktionieren*

Eine Block- oder Item-Definition kann mit `"inherits"` von einer anderen derselben Art ausgehen, indem sie den Registry-Namen irgendeiner Variante nennt, und dann überschreiben, was abweicht:

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "hardness": 4.0 } } }

Das Kind kopiert jeden Wert aus der Datei des Elternteils und aus der genannten Variante, die Dateireihenfolge spielt nie eine Rolle, Ketten werden vom Elternteil abwärts aufgelöst, und ein Kreis oder ein fehlender Elternteil landet im Log und lässt das Kind so, wie es geschrieben steht. Felder, die das Kind schreibt, ersetzen den geerbten Wert; verschachtelte Varianteneigenschaften überschreiben einzeln, Listen wie `requires` dagegen komplett, schreib also die ganze Liste, die du haben willst. Blöcke erben nur von Blöcken und Items nur von Items.

### Block- und Item-Vorlagen

*wie definitionen funktionieren*

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

## Was du überschreiben kannst

*wie packs funktionieren*

- **Alles im assets-Ordner eines Mods**: Texturen, Modelle, Blockstates, Sprachdateien, Sounds, Schriftarten, Splash-Texte, Handbücher, Anleitungen
- **Fortschritte, Beutetabellen, Tags und Funktionen**, serverseitig, sie funktionieren also auch auf dedizierten Servern
- **Rezepte**: das Rezept eines Mods ersetzen oder ein eigenes hinzufügen
- **Strukturvorlagen**: die `.nbt`-Dateien, die Mods für generierte Gebäude nutzen, unter `<namespace>/structures/`
- **Registry-Umbenennungen**: alte Welten am Leben halten, wenn ein Mod einen Block oder ein Item umbenennt
- **Rezept-Entfernungen**: ein Handwerksrezept nach Name, Namespace oder Ergebnis löschen
- **Beute-Injektionen**: einen Pool zu einer Beutetabelle hinzufügen, statt sie komplett zu ersetzen
- **Blockdrops**: dem Drop eines beliebigen Blocks beim Abbau etwas hinzufügen oder ihn ersetzen, Erfahrung eingeschlossen
- **Spielerbeute**: beim Tod eines Spielers eine Beutetabelle auswürfeln, zusätzlich zu dem, was er dabeihatte, oder an dessen Stelle
- **Eigenschaften vorhandener Blöcke, Items und Tränke**: Härte, Licht, Stapelgrößen, Essbarkeit für alles, die Effekte eines Tranks, siehe [Eigenschaften überschreiben](#eigenschaften-überschreiben)
- **Ofenrezepte, Brenndauern, Kreativtabs und Sound-Events**

Was ein Block fallen lässt, ist auf dieser Version seine Beutetabelle: Um zu ändern, was Stein fallen lässt, liefere `data/minecraft/loot_tables/blocks/stone.json`, und um etwas hinzuzufügen, ohne sie zu ersetzen, eine Beute-Injektion oder eine Regel unter [Blockdrops](#blockdrops), die auch Erfahrung geben kann.

RDPL eignet sich gut dafür, ein oder zwei Rezepte zu ersetzen, und Rezepte für eigenen Inhalt gehören mit in dasselbe Pack. Für volle Rezeptkontrolle über ein ganzes Modpack sind KubeJS und CraftTweaker die besseren Werkzeuge, und eine Datei hier ersetzt das Original weiterhin vollständig, um also eine einzelne Zutat zu ändern oder einen einzelnen Beuteeintrag zu streichen, nimm die beiden.

## Packs nur auf dem Server

*wie packs funktionieren*

Ein Pack kann allein auf dem Server liegen, mit Spielern auf reinen Vanilla-Clients, unter einer Bedingung: **nichts darin darf irgendetwas registrieren**. Der Mod akzeptiert jede Gegenstelle; das Pack entscheidet. Ein Vanilla-Client spielt mit den Registries, die er mitgebracht hat; ein Pack, das sie erweitert, muss also auf beide Seiten.

| Server allein genügt | Pack muss auch auf den Client |
| --- | --- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures`, `structuremaps`, `citymaps`, `villages`, `pathintersects`, `caveregions`, `biomes`, `dimensions` | `blocks`, `items`, `fluids`, `materials`, `containers` |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `anvils`, `tags` | `potions`, `potion_types`, `sounds`, `tabs`, `exposures` |
| `loot_tables`, `loot_injections`, `player_loot`, `advancements`, `functions` | `entities`, `villagers`, `portalframes` |
| `gates`, `trades`, `registry_remap`, `teams`, `scoring`, `raids`, `hardness`, `blastplaster` | `models`, `blockstates`, `textures`, `lang`, `worldintro`, `overrides` (Client-Ordner: ohne Client weglassen) |
| die ganze Steuerungsebene, Einstellungen und Vorgenerierung | |

Die rechte Spalte ist eine harte Grenze: Blöcke, Items, Entity-Typen, Sounds und Trankeffekte, die ein Vanilla-Client nicht hat, lassen sich ihm nicht beschreiben, und das eigene Portal einer Dimension ist einer der Blöcke des Packs; Expositionen werden nur zusammen mit diesen Inhalten geladen. Die linke Spalte funktioniert, weil alles darin entweder vollständig serverseitig läuft, den Client als Datenpaket-Einträge erreicht, die Vanilla ohnehin liest (Biome, Höhlenregionen, Dimensionstypen), oder ihn über Pakete erreicht, die Vanilla ohnehin spricht (vom Server gefülltes Ergebnisfeld der Werkbank, gewöhnliche Fortschrittspakete, Statusmeldungen bei abgelehnten Toren, ein Vorgenerierungs-Halt aus Vanilla-Paketen für Spielmodus, Titel und Teleport).

Einrichtung:

1. Schalte `vanillaClients` in der Config ein (Kategorie `content`, braucht einen Neustart). Das erzwingt die rechte Spalte: Diese Ordner werden beim Laden übersprungen, jede übersprungene Datei steht namentlich im Log, aus einer durchgerutschten Blockdatei wird eine Logzeile statt einer abgelehnten Verbindung.
2. Halte Definitionen trotzdem aus den rechten Ordnern heraus; übersprungene Dateien sind totes Gewicht. Wo das Pack Items nennt (das `hold` eines Tors, `killedDrops`, Rezeptergebnisse, Handel), nenne nur Items, die Vanilla oder die anderen beidseitigen Mods des Servers mitbringen. Ein Biom, das die eigenen Bodenblöcke des Packs nennt, behält den Boden des Basisbioms, und eine Dimension, die sich über ihr eigenes Portal öffnet, braucht diesen Portalblock; schicke Spieler per Befehl dorthin.
3. Entity-Varianten sind auf dieser Version eigene Entity-Typen und gehören damit in die rechte Spalte: Mit `vanillaClients` werden sie übersprungen, ihre Spawns mit ihnen, und das Log nennt sie.
4. Auf dem Server installieren wie üblich, mit Blast Plaster, das der Mod voraussetzt und das ebenfalls nichts registriert. Auf Spielerrechnern landet nichts; `/rdpl` existiert dort nicht.
5. Mit einem einzigen sauberen Vanilla-Client derselben Version testen. Fehler sind laut: Die Verbindung wird an der Tür abgelehnt, nicht später still kaputt.
6. Zwei akzeptierte kosmetische Lücken: Server-Rezepte lassen sich craften, erscheinen aber nicht im Rezeptbuch, und der Halt, während Land gemacht wird, ist ein schlichter Zuschauer-Halt mit dem Fortschritt in der Aktionsleiste, ohne den Nebel und das Logo, die der eigene Client des Mods zeichnet.

## Registry-Umbenennungen

*wie packs funktionieren*

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

## Mod-API

*wie packs funktionieren*

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
| `priority` | `-1` oder eine Zahl | `-1` | `-1` hält die Mod unter jedem Pack; jede andere Zahl setzt sie in die gewöhnliche [Vorrang](#packs-organisieren)-Reihenfolge neben die nummerierten Packs |

Ein Mod-Pack kommt nie in die Überschreibungsstufe der Ressourcenpakete, egal was `overrideResourcePacks` sagt, denn darum kann nur ein Pack-Autor mit dem Buchstaben `O` bitten. Das Log kennzeichnet Mod-Packs und listet Packs mit dem niedrigsten zuerst, es lädt also nichts ungesehen.

## Packs für 1.12.2

*wie packs funktionieren*

Ein Pack, das für die 1.12.2-Linie gemacht wurde, lädt so, wie es ist. Der Loader erkennt eines an seinem `pack.mcmeta`-Format, an Definitionsordnern unter `assets/` ohne ein `data/` daneben oder an einer `.lang`-Datei, und trägt es nach vorn: Ein Zip wird als Pack dieser Version unter seinem eigenen Namen ausgeschrieben, mit allem Folgenden bereits erledigt, und das 1.12.2-Zip, aus dem es stammt, bleibt als `<name>_converted.zip.disabled` daneben liegen, sodass nichts verloren geht und das neue Pack deins ist, um es fertigzustellen und zu bearbeiten. Lose Dateien unter `rdploader/assets` werden nicht umgeschrieben; sie werden bei jedem Scannen des Ordners durch dieselbe Portierung gelesen.

- Definitionsordner wandern von `assets/<namespace>/` nach `data/<namespace>/`, und die Vanilla-Datenordner mit ihnen: Rezepte, Beutetabellen, Beute-Injektionen, Fortschritte, Funktionen und Strukturen.
- `textures/blocks/` und `textures/items/` werden als `textures/block/` und `textures/item/` ausgeliefert, in Modellen, in Pixelkarten und in den Dateien selbst. Ein Item-Modell unter `models/item/<datei>/<variante>.json` wird als `models/item/<variante>.json` ausgeliefert.
- Jede ID mit Metadaten, `minecraft:wool:14` oder `minecraft:dye:4`, läuft durch die Data-Fixer des Spiels selbst, denselben Code, der eine 1.12.2-Welt aktualisiert, und kommt als der Block oder das Item heraus, zu dem sie wurde: `minecraft:red_wool`, `minecraft:lapis_lazuli`. Ein Blockzustand, der das Flattening als Eigenschaft überlebt hat, `minecraft:log:1` zu `minecraft:oak_log` mit `axis=y`, kommt als `properties`-Objekt heraus. Die eigenen IDs des Packs lösen sich über seine eigenen Definitionen auf: `mypack:materials:5` wird zur Variante, deren `meta` 5 war, und `mypack:ruby_ore` zur ersten Variante der Datei, da hier jede Variante ein eigener Block ist. Entity- und Biomnamen werden genauso repariert, und Dimensionsnummern werden zu IDs.
- `variants` behalten ihre Schlüssel; `meta` fällt weg und `oreDict` wird zu `tags`, über die Abbildung des Ore Dictionary auf die Konventions-Tags. Eine `oredict/*.json`-Datei wird zu einer Item-Tag-Datei pro Namen, den sie ergänzt oder aus dem sie entfernt: Eine Entfernung `-name` landet in der `remove`-Liste des Tags, und das Entfernen von `*` ersetzt den Tag. Ein bloßes `creativeTab` bekommt den Namespace des Packs, und ein Vanilla-Tab-Label aus 1.12.2 wie `misc` wird zum nächstliegenden Vanilla-Tab.
- Eine `.lang`-Datei wird als das `.json` ausgeliefert, das das Spiel liest, mit `tile.mypack:datei.variante.name` als `block.mypack.variante`, `item.` ebenso, `itemGroup.x` als `itemGroup.mypack.x`, `fluid.x` als beide Fluid-Schlüssel, und alles andere wie geschrieben.
- Ein 1.12.2-Blockstate wird gar nicht ausgeliefert. Stattdessen werden seine Texturen gelesen und unter den Namen ausgeliefert, nach denen der Generator sucht, `textures/block/<variante>.png` mit `_top` und `_bottom`, wo der Blockstate `end`, `top` oder `bottom` hatte, sodass Blockstate und Modelle für jede Variante generiert werden, wie sie es für ein hier geschriebenes Pack würden.
- Rezepte verlieren ihr `data` und bekommen geglättete IDs, `forge:ore_shaped` wird zu `minecraft:crafting_shaped` mit `ore`-Zutaten als `tag`, Beutetabellen verlieren `set_data` auf dieselbe Weise, und das `item` mit `data` eines Fortschritts wird zu `items`. Der `background` eines Fortschritts wandert von `textures/blocks/` nach `textures/block/`.
- Auch das Forge-Vokabular der Rezepte aus 1.12.2 kommt mit: Ein Zutatentyp `forge:ore_dict` oder `minecraft:item` fällt weg, eine Zutat `minecraft:item_nbt` wird zu `forge:nbt` (1.21.1: `neoforge:components`), ihr NBT durch die Datenfixer geschickt, `minecraft:item_exists` wird zu `forge:item_exists` (1.21.1: jede Bedingung bekommt ihren `neoforge`-Namen, unter `neoforge:conditions`), ein Item ohne Namespace bekommt den des Rezepts, und `data` 32767 wird zu einer Liste aller Varianten. Eine `#CONSTANT` aus der `_constants.json` einer Mod kann nicht mitkommen, und das Log nennt sie. Wo eine Liste Items nennt, wird `name:*` zu jeder Variante, die das Item hatte, und ein einzelner Wert nimmt die erste; das betrifft die `outputs` von `recipe_removals` und die Entfernungen in `furnace`, die ebenfalls als Items übertragen werden. Ein `item` oder `with` einer Amboss-Arbeit, als `name:*` geschrieben, wird zu einer Liste aller Varianten, und jede davon passt.
- Beutetabellen, die seit 1.12.2 umbenannt wurden, werden überall umbenannt, wo ein Pack eine nennt: das Ziel einer `loot_injections`-Datei, die Tabelle in `player_loot` und ein `loot_table`-Eintrag (1.21.1: sein `value`), sodass `minecraft:entities/zombie_pigman` zu `minecraft:entities/zombified_piglin` wird. `killed_by_player` mit `inverse` wird zu einer `inverted`-Bedingung, `entity_properties` mit `on_fire` zu einem `flags`-Prädikat, und Namen in `set_attributes` wie `generic.maxHealth` werden zu `generic.max_health` (1.21.1: die Operation bekommt ihren neuen Namen, und der `name` des Modifikators wird zu seiner `id`).
- Ein Override eines 1.12.2-Blocks, den das Flattening aufgeteilt hat, etwa `overrides/minecraft/wool.json`, wird als Override jedes Blocks gelesen, der daraus wurde, also aller sechzehn Wollen, weil 1.12.2 jede Variante auf einmal änderte.
- Das `gameLoopFunction` einer Spielregel-Datei wird zum Funktions-Tag `#minecraft:tick`, geschrieben als `data/minecraft/tags/functions/tick.json`, da es die Spielregel nicht mehr gibt. Die eigenen Dimensionsnummern des Packs werden über seine `dimensions`-Dateien gelesen, sodass `"id": 7` in `dimensions/verdant.json` die 7 überall, wo das Pack sie nennt, zu `mypack:verdant` macht. Beide Seiten eines `villageBlocks`-Paars werden repariert, die Chance bleibt, und eine `registry_remap`-Datei darf die Pluralnamen `minecraft:blocks` und `minecraft:items` aus 1.12.2 behalten.
- Eine Weltvorlage, die in einer Dimension jede Struktur abschaltet, die es in 1.12.2 gab, schaltet dort auch die Strukturen ab, die es nur in dieser Version gibt: `ancient_cities`, `buried_treasures`, `ocean_ruins`, `pillager_outposts`, `ruined_portals`, `shipwrecks` und `trail_ruins` in der Oberwelt und `nether_fossils` im Nether. Bleibt einer der 1.12.2-Namen an, bleiben sie unberührt. Die Schlüssel der Generatorsteuerung, `blockWorldGenerators` und seine Begleiter, werden mit einer Zeile im Log aus einer portierten Vorlage entfernt, da hier nichts sie liest. Die Blocknamen in den Schichten von `generatorOptions` einer flachen Vorlage werden genauso berichtigt, aus `minecraft:grass` wird also `minecraft:grass_block`.
- Funktionen werden Zeile für Zeile in die Befehlssyntax dieser Version umgeschrieben. Ids mit Datenwerten laufen durch dieselben Data Fixer, sodass `give @p minecraft:wool 1 14` zu `give @p minecraft:red_wool 1` wird und `give @p mypack:materials 1 5` die Variante gibt, deren `meta` 5 war, und NBT von Items, Entities und Blöcken wird repariert wie in einer Welt (1.21.1: das NBT eines Items wird zu seinen Komponenten). `testforblock`, `testfor` und `scoreboard players test` werden zu `execute if`, `execute <entity> <x> <y> <z> [detect ...]` wird zu `execute as ... at @s [positioned ...] [if block ...] run`, `effect` bekommt `give` und `clear`, `blockdata`, `entitydata` und `replaceitem` werden zu `data merge` und `item replace`, und `scoreboard teams` und `scoreboard players tag` werden zu `team` und `tag`. Selektoren tauschen `score_X_min` und `score_X` gegen `scores`, `r` und `rm` gegen `distance`, `l` und `lm` gegen `level`, `m` gegen `gamemode`, `c` gegen `limit` und `sort` sowie `rx` und `ry` gegen `x_rotation` und `y_rotation`. Verzauberungs- und Effektnummern, Partikel- und Soundnamen, Spielmodus- und Schwierigkeitsnummern, die Dauer von `weather` in Sekunden und ein relatives `tp` einer anderen Entity werden ebenfalls übertragen. Eine Zeile, die die Portierung nicht übertragen kann, bleibt wie geschrieben, und das Log nennt Datei, Zeile und Grund; eine Funktion mit einer solchen Zeile lädt erst, wenn die Zeile von Hand korrigiert ist. `block_drops` wird übernommen, wie es ist, sein `meta` im Blocknamen oder in `properties` aufgelöst.
- Der Boden einer flachen 1.12.2-Welt lag bei y 0, und diese Version legt eine flache Welt von ihrem unteren Rand bei y -64 an, also verschiebt die Portierung die Höhen mit. Ist das `worldType` einer Weltvorlage `flat` oder `superflat`, rücken ihr `worldSpawn` und `resetSendsTo` um 64 nach unten (auf ihre `worldMinHeight`, wenn sie eine nennt), ebenso die Höhen von `spawn`, `standIn` `at` und `spawnBox` eines Teams und das `opens.lobby` einer Wertungsdatei, wenn jede Weltvorlage des Packs flach ist. Eine Pack-Dimension mit `flat`-Terrain verschiebt ihr `groundLevel` und jede Position, die diese Dimension nennt, auf dieselbe Weise (um ihre `minHeight`, wenn sie eine hat). Funktionen sagen nicht, wo sie laufen, also rückt bei flacher Oberwelt jedes absolute y in den Funktionen des Packs um 64 nach unten, das `y` eines Selektors eingeschlossen, und das Log sagt es einmal; Höhen mit `~` und `^` bleiben unberührt. Eine Welt mit normalem Terrain behält jede Koordinate, da ihre Oberfläche auf Meereshöhe bleibt.

Das Log trägt eine Zusammenfassung je portiertem Pack und eine Zeile für jede Datei, die verschoben, weggelassen oder nicht getragen wurde, und jeden Schlüssel, den diese Version nicht mehr liest, nennt weiterhin der Parser, der auf ihn trifft. Die Portierung ist ein bester Versuch, kein fertiges Pack: Öffne das geschriebene Zip, lies diese Zeilen und stelle von Hand fertig, was sie nennen, zuerst jede Befehlszeile, die sie wie geschrieben gelassen hat, und jede Textur, für die sie keinen Namen finden konnte.

---

# Blöcke und Items

## Blöcke

*blöcke und items*

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
  "lightOpacity": 255,
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

*blöcke*

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
| `torch` | Wand- und Bodenplatzierung, mit Partikel. Leuchtet genau mit dem `light` der Variante, eine Fackel mit `0` gibt also kein Licht |
| `bell` | Eine Glocke wie die in den Dörfern des Spiels: läutet, wenn man sie an der Seite benutzt, durch Redstone oder wenn ein Geschoss sie trifft, schwingt in ihrem Gestell und lässt Angreifer in der Nähe leuchten. Ihr Blockstate trägt die Richtung und die Art, wie sie hängt |
| `log` | Dreht sich zu der Fläche, gegen die du ihn setzt, und trägt den Tag `minecraft:logs`, damit Baumfällen und Blast Plaster ihn als Stamm behandeln |
| `leaves` | Verwelkt, lässt sich scheren, wird eingefärbt und droppt einen Setzling, und trägt den Tag `minecraft:leaves`. Bleibt `opaque` gesetzt, wird es massiv gezeichnet wie schnelle Blätter; mit `"opaque": false` sieht man hindurch |
| `sapling` | Wächst zu einem Baum oder zu einer deiner Strukturen |
| `crop` | Wächst durch Stufen, droppt Saatgut und ein Ernte-Item, Weizenkörner und Weizen für das, was die Datei weglässt |
| `flower` | Eine einblockige Pflanze auf Erde |
| `cane` | Wächst als Säule nach oben, wie Zuckerrohr oder Kaktus |
| `vine` | Hängt an den Seiten von Blöcken. Mit `growth` wächst sie bis `maxHeight` nach unten und greift mit `spread` seitlich auf benachbarte Wände über; ohne bleibt sie, wie sie gesetzt wurde |
| `portal` | Schickt alles, was hineinläuft, in eine andere Dimension |
| `container` | Enthält ein Inventar, das ein Spieler öffnen kann, in beliebiger Größe, und kann sich beim ersten Öffnen selbst aus einer Beutetabelle füllen. Wird als gewöhnlicher Block oder als Truhe gezeichnet, je nachdem, was das Pack verlangt. Ohne `container`-Objekt fasst er drei Reihen zu je neun |

### Dateischlüssel

*blöcke*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `variants` | ja | Objekt aus Variantenname zu Variante | | Ein Block pro Eintrag. Der Schlüssel ist sein Registry-Name und benennt seinen Blockstate, seine Modelle, seine Texturen und seinen Sprachschlüssel |
| `type` | nein | einer der Typen oben | `basic` | Welche Form der Block annimmt |
| `material` | nein | eines der [Blockmaterialien](#wertelisten) | `rock` | Der Block tut, was dieses Material in 1.12.2 tat: ob er von Hand abgebaut etwas droppt, wie Kolben mit ihm umgehen, ob Lava ihn entzündet, ob fließende Flüssigkeit ihn wegspült und ob ein gesetzter Block ihn ersetzt. Ein `log` ist immer `wood`, `leaves` immer `leaves`, eine `vine` `vine`, `torch` und `ladder` sind `circuits`, ein `crop` ist `plants`, und `stairs` und `wall` verhalten sich wie ihr `modelBlock` |
| `soundType` | nein | einer der [Sound-Typen](#wertelisten) | `stone`; `wood` bei `log`, `plant` bei `leaves` und `crop`, der des `modelBlock` bei `stairs` und `wall` | Schritte, Abbauen und Setzen |
| `mapColor` | nein | eine der [Kartenfarben](#wertelisten) | vom Material | Wie er auf einer Karte aussieht |
| `harvestTool` | nein | `pickaxe`, `axe`, `shovel`, `hoe`, `sword` | `pickaxe` | Welches Werkzeug ihn abbaut, für dich in die `mineable`-Tags des Spiels geschrieben; `sword` kommt in `resourcedatapackloader:mineable/sword`, das die `sword`-Werkzeuge eines Packs abbauen. Für die Drops zählt das nur bei einem Material, das ein Werkzeug braucht. Wie in 1.12.2 baut eine Spitzhacke außerdem `rock`, `iron` und `anvil` mit voller Geschwindigkeit ab und eine Axt `wood`, `plants` und `vine`. Jeder andere Name, etwa `shears`, wird geloggt und weggelassen |
| `harvestToolLevel` | nein | 0 bis 4 | `0` | 0 Holz, 1 Stein, 2 Eisen, 3 Diamant, 4 Netherit, für dich in die `needs_*_tool`-Tags geschrieben. Ein Block vom Typ `basic`, `ore`, `container`, `portal`, `fence`, `pane`, `log`, `falling`, `slab` oder `wall` übergeht ihn und nimmt wie in 1.12.2 das `harvestLevel` der Variante |
| `silkHarvest` | nein | boolean | `true` | Ob Behutsamkeit den Block selbst zurückgibt |
| `opensWith` | nein | Item-Id | keine | Macht den Block zur Schatzkiste: Abbauen liefert den Block selbst, ein Rechtsklick mit dem genannten Item verbraucht eines, spielt den Abbau-Sound, schüttet die `drops`-Liste der Variante aus und entfernt den Block. Jeder andere Klick zeigt die Aktionsleisten-Zeile `block.<pack>.<block>.locked` aus den Sprachdateien |
| `openSound` | nein | Sound-Name | der Abbau-Sound | Was eine Schatzkiste beim Öffnen statt ihres Abbau-Sounds spielt. Ein 1.12.2-Name gilt weiterhin, siehe [Sound-Namen](#wertelisten) |
| `expDrop` | nein | Objekt mit `min` und `max` | keines | Erfahrung, wenn ein Spieler den Block abbaut oder ein Pack-Mob, der Erfahrung sammelt, ihn abgräbt; Kolben, Wasser und Explosionen geben keine. Behutsamkeit nimmt sie nur weg, wenn `silkHarvest` an ist |
| `creativeTab` | nein | Tab-Name | keiner | Der Tab, in dem er auftaucht, siehe [Kreativ-Tabs](#kreativ-tabs) |
| `renderLayer` | nein | `solid`, `cutout`, `cutout_mipped`, `translucent` | passend zum Typ | Wie er gezeichnet wird |
| `opaque` | nein | boolean | `true` | Ob er Sicht und Licht vollständig blockiert |
| `fullCube` | nein | boolean | wie `opaque` | Ob er seinen ganzen Raum ausfüllt |
| `lightOpacity` | nein | 0 bis 255 | `255`, wenn opak, sonst `0` | Wie viel Licht er schluckt: ab 15 alles, und `0` lässt Sonnenlicht ungehindert durch. Eine `slab` behält den Wert des Spiels, und ein Behälter mit Truhenmodell lässt Licht durch |
| `slipperiness` | nein | float | `0.6` | Eis ist `0.98` |
| `flammability` | nein | int | `0` | Wie bereitwillig Feuer ihn verzehrt |
| `fireSpread` | nein | int | `0` | Wie bereitwillig Feuer von ihm überspringt |
| `explosionResistanceDivisor` | nein | float | `1.0` | Teilt den `resistance`-Wert jeder Variante gegenüber Explosionen |
| `modelBlock` | nein | Blockname | `minecraft:stone` | Block, dessen Modell geliehen wird, wenn deiner weder Textur noch eigenes Modell mitbringt |
| `itemModel` | nein | `state`, `item` | `state` | `state` zeichnet das Item als den gesetzten Block, `item` sucht deine eigene `models/item/<name>.json` |
| `tint` | nein | `biome`, `none` oder eine Hex-Farbe | keine | Braucht einen `tintindex` im Modell, um zu wirken |
| `plantTypes` | nein | Liste von [Pflanzentypen](#wertelisten) | keine | Was darauf gepflanzt werden kann |
| `behavesAs` | nein | Liste aus `till`, `path`, `bush`, `animals` | keine | Vanilla-Verhalten, das er übernimmt |
| `bounds` | nein | Liste aus sechs Zahlen, 0 bis 1 | ganzer Block | Die Kollisionsbox, als `[x1, y1, z1, x2, y2, z2]` |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |
| `particle` | nur `torch` | `none`, `flame`, `colored` | `flame` | Der Partikel über einer Fackel |
| `particleColor` | nur `torch` | Hex-Farbe | `FFFFFF` | Wird genutzt, wenn `particle` auf `colored` steht |
| `smoke` | nur `torch` | boolean | `true` | Ob sie raucht |
| `leafSapling` | nur `leaves` | Blockname | keiner | Der Setzling, den sie droppen |
| `leafSaplingChance` | nur `leaves` | int | `5` | Eines von N Blättern droppt einen |
| `seed` | nur `crop` | Itemname | `minecraft:wheat_seeds` | Das Item, das sie pflanzt, und was eine unreife Pflanze droppt |
| `produce` | nur `crop` | Itemname | `minecraft:wheat` | Was die Ernte bringt |
| `maxAge` | nur `crop` | int | `7` | Wie viele Wachstumsstufen |
| `growth` | nur Pflanzen | Objekt | keines | Siehe [Wachstum](#wachstum) |
| `sapling` | nur `sapling` | Objekt | keines | Siehe [Setzlinge](#setzlinge) |
| `portal` | nur `portal` | Objekt | keines | Siehe [Portale und Tore](#portale-und-tore) |
| `container` | nur `container` | Objekt | keines | Siehe [Behälter](#behälter) |
| `bell` | nur `bell` | Objekt | keines | Siehe [Glocken](#glocken) |

### Variantenschlüssel

*blöcke*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `hardness` | nein | float | `1.0` | Wie lange das Abbauen dauert. Obsidian ist `50`, `-1` ist unzerstörbar |
| `resistance` | nein | float | `5.0` | Explosionswiderstand, so wie 1.12.2 ihn liest: Der Block behält drei Fünftel der Zahl, `10` ergibt also die `6` von Stein |
| `light` | nein | 0 bis 15 | `0` | Abgegebenes Licht |
| `harvestLevel` | nein | 0 bis 4 | `0` | Die Werkzeugstufe eines Blocks vom Typ `basic`, `ore`, `container`, `portal`, `fence`, `pane`, `log`, `falling`, `slab` oder `wall`, anstelle von `harvestToolLevel` aus der Datei. Die übrigen Typen richten sich nach dem Wert der Datei |
| `rarity` | nein | `common`, `uncommon`, `rare`, `epic` | `common` | Farbe des Namens im Tooltip |
| `maxSize` | nein | 1 bis 64 | `64` | Stapelgröße |
| `tags` | nein | Liste von Tag-IDs | keine | Block- und Item-Tags, in die diese Variante geschrieben wird, etwa `forge:ores/ruby` auf 1.20.1 oder `c:ores/ruby` auf 1.21.1. Die Tag-Dateien werden für dich erzeugt |
| `drops` | nein | Liste von Drops | droppt sich selbst | Was das Abbauen bringt |
| `portal` | nur `portal` | Objekt | das der Datei | Das eigene Portal dieser Variante statt dem der Datei, geschrieben wie unter [Portale und Tore](#portale-und-tore). Die Datei braucht trotzdem ein eigenes |

**Namen sind endgültig.** Der Schlüssel einer Variante wird in jede gespeicherte Welt geschrieben, die sie enthält. Wird er später umbenannt, werden gesetzte Blöcke zu Luft, es sei denn, eine [Registry-Umbenennung](#registry-umbenennungen) bildet den alten Namen auf den neuen ab. Eine Datei darf so viele Varianten enthalten, wie sie will; jede ist ein eigener Block, und ein `meta`-Schlüssel aus einem 1.12.2-Pack wird mit einer Notiz im Log ignoriert.

### Drops

*blöcke*

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

Jeder Eintrag ohne `weight` wird für sich entschieden, ein Block mit dreien kann also alle drei droppen oder keinen. Gibst du Einträgen ein `weight`, hören sie auf, unabhängig zu sein: Sie bilden einen Topf, aus dem bei jedem Brechen genau einer gezogen wird, mit den Gewichten als Verhältnis. Oben teilen sich Diamant und Smaragd einen Topf im Verhältnis eins zu vier, es kommt also immer einer von beiden heraus und in vier von fünf Fällen der Smaragd, während Rubin und Kohle getrennt entschieden werden und der Silberfisch wieder für sich steht. Items und Entities haben getrennte Töpfe, ein gewichtetes Item und eine gewichtete Entity treten also nicht gegeneinander an.

Ein Eintrag mit `entity` lässt dort, wo der Block stand, eine frei, in eine zufällige Richtung gedreht, und ein Mob bekommt seine übliche Spawn-Behandlung für die Schwierigkeit vor Ort, kommt also mit der Ausrüstung und den Effekten an, die er sonst auch hätte. `amount` bestimmt wie viele, `chance` wie oft, `weight` steckt ihn in den Entity-Topf. Es passiert, während der Block bricht, ganz gleich wodurch, eine Explosion oder ein Kolben setzt sie also genauso frei wie eine Spitzhacke. `bonusChance` und Glück bedeuten einer Entity nichts und werden ignoriert.

Ein Drop, der sowohl `block` als auch `entity` nennt, nimmt die Entity und schreibt es ins Log.

Die Drops werden in eine erzeugte Beutetabelle geschrieben, `loot_tables/blocks/<name>.json` unter dem Namespace des Packs, es sei denn, das Pack liefert selbst eine unter diesem Pfad; dann ist die Datei des Packs das, was der Block nutzt, und `drops` wird nicht gelesen.

### Wachstum

*blöcke*

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
| `stages` | nein | int | `16` | Wachstumsstufen, bis es fertig ist. Eine Ranke versucht bei einem von so vielen Random-Ticks zu wachsen |
| `growth` | nein | int | | Chance von eins zu N pro Random-Tick, eine Stufe weiterzukommen |
| `spread` | nein | int | `0` | Wie weit es sich auf Nachbarblöcke ausbreitet. Eine Ranke greift nicht mehr seitlich über, sobald so viele Ranken im Umkreis von zwei Blöcken stehen |
| `maxHeight` | nein | int | `3` | `cane` und `vine`. Wie hoch die Säule wächst oder wie weit eine Ranke herabhängt; eine Ranke mit `1` wächst und breitet sich nicht aus |
| `soil` | nein | Liste von Blocknamen | das Übliche des Typs | Worauf es steht |
| `drop` | nein | Itemname | keiner | `cane` und `vine`. Was es beim Abbauen droppt; eine Blume droppt sich selbst |
| `dropCount` | nein | int | `1` | `cane` und `vine`. Wie viele |
| `needsSky` | nein | boolean | `false` | Wächst nur, wo der Himmel zu sehen ist |
| `needsWater` | nein | boolean | `false` | Wächst nur in Wassernähe |
| `waterRange` | nein | int | `1` | Wie weit dieses Wasser entfernt sein darf |
| `damage` | nein | boolean | `false` | Verletzt, was es berührt |
| `damageAmount` | nein | float, halbe Herzen | `1.0` | Wie sehr es verletzt |
| `breaksNeighbors` | nein | boolean | `false` | Zerstört Blöcke, die daneben gesetzt werden, wie ein Kaktus |

### Setzlinge

*blöcke*

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

## Behälter

*blöcke und items*

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
| `rows` | Zahl | `3` | Wie viele Reihen von Plätzen, 1 bis 9 |
| `columns` | Zahl | `9` | Wie viele Plätze in einer Reihe, 1 bis 12 |
| `lootTable` | Text | leer | Eine Beutetabelle, die in den Block gewürfelt wird, sobald zum ersten Mal etwas an seinen Inhalt kommt, ein Spieler beim Öffnen, ein Trichter, ein Komparator oder das Abbauen, genau wie sich eine Verliestruhe füllt. Ein Behälter, den ein Spieler setzt, würfelt sie nie. Leer lässt ihn leer beginnen |
| `chestModel` | Wahrheitswert oder Text | `false` | Wird als Truhe mit sich öffnendem Deckel gezeichnet statt als gewöhnlicher Block aus deiner eigenen Textur. `true` nimmt das Vanilla-Truhenbild; ein Texturname wie `mypack:entity/chest/strongbox` nimmt stattdessen dein eigenes Truhenblatt, für den gesetzten Block und für den Gegenstand gleichermaßen. Ein Block mit Truhenmodell setzt außerdem `opaque` standardmäßig auf `false`, so wie es eine Vanilla-Truhe ist, damit das Licht am Block nicht abgeschnitten wird und die Truhe nicht dunkel gezeichnet wird |
| `guiTexture` | Text | leer | Dein eigenes Hintergrundbild für den Bildschirm. Leer zeichnet eines aus dem Vanilla-Truhenbildschirm in der Größe, die Reihen und Spalten brauchen |
| `guiWidth` | Zahl | keiner | Wie breit der Bildschirm ist, gezeichnet ab der oberen linken Ecke des Bildes, das als 256 mal 256 großes Blatt gelesen wird, nötig zusammen mit `guiTexture` |
| `guiHeight` | Zahl | keiner | Wie hoch der Bildschirm ist, nötig zusammen mit `guiTexture` |
| `curioSlot` | Text | leer | Nur bei einem Item: der Curios-Slot, in dem es getragen werden kann, `back`, `belt`, `body`, `charm`, `head`, `necklace`, `ring` oder jeder Slot, den ein anderer Mod hinzufügt. Ein Rucksack nimmt meist `back`. Wird übergangen, wobei alles andere am Item weiter funktioniert, wenn Curios nicht installiert ist. Der 1.12.2-Schlüssel `bauble` wird als dieser gelesen und nimmt die Baubles-Namen: `amulet` wird zu `necklace`, `ring` gibt zwei Ringslots, `belt`, `head`, `body` und `charm` behalten ihren Namen, und `trinket` passt in jeden dieser Slots. Jeder andere Name lässt das Item ungetragen, mit einer Fehlerzeile |

**Neun Reihen mal zwölf ist die Obergrenze**, das Meiste, was ein Bildschirm tragen kann. Ein Pack, das mehr verlangt, wird darauf gekürzt, mit einer Fehlerzeile, die es sagt. Eine Warnung zur höchsten: ein Bildschirm mit neun Reihen ist 276 Pixel hoch, ein 1080er Bildschirm bei GUI-Skalierung `auto` gibt 270, also werden oben und unten je drei Pixel abgeschnitten; Skalierung 3 zeigt ihn ganz.

**Der Bildschirm wird gezeichnet, nicht mitgeliefert.** Ein Behälter mit höchstens neun Spalten und sechs Reihen nutzt den Truhenbildschirm des Grundspiels unverändert und sieht damit genau wie eine Truhe dieser Größe aus. Alles Größere wird beim Zeichnen aus demselben Bild zusammengesetzt, die obere Kante, eine Reihe von Feldern so oft wiederholt, wie es passt, und der untere Teil mit dem Inventar des Spielers, so kann ein Pack Größen verlangen, die kein Bildschirm des Grundspiels abdeckt, ohne ein eigenes Bild mitzuliefern. `guiTexture` setzt das alles außer Kraft, wenn ein Pack sein eigenes Aussehen will; dann müssen `guiWidth` und `guiHeight` die Größe nennen, sonst wird der gezeichnete genommen und eine Fehlerzeile sagt es.

**Was der Block tut.** Er behält seinen Inhalt über Speichern und Neuladen, lässt ihn beim Abbauen fallen, antwortet einem Komparator nach Füllstand und behält die Reihen und Spalten, mit denen er entstand, sodass eine spätere Änderung im Pack die Behälter, die schon in einer Welt stehen, so lässt, wie sie waren. Ein Behälter-Item oder ein Behälterblock lässt sich nicht in einen Behälter legen. `chestModel` gibt ihm zusätzlich den Öffnungston und die Deckelbewegung der Truhe; ohne das zeichnet der Block aus seiner eigenen Textur wie jeder andere Block, sodass Kiste, Fass oder Schrank alle möglich sind.

**Eine Truhe einfärben.** Das Truhenbild ist eine gewöhnliche Textur, also kann eine Pixelkarte die Vanilla-Truhe umfärben, ohne dass ein Pixel gezeichnet wird: `extends` darauf, ein `tint` dazu, und diese Karte dann in `chestModel` nennen.

```json
{
  "extends": "minecraft:textures/entity/chest/normal",
  "tint": {
    "from": "#241A12",
    "to": "#D8BC80"
  }
}
```

**Ein Behälter-Item ist ein Beutel**, ein Item vom Typ `container`, das denselben `container`-Block mit `rows` und `columns` trägt; ein Rechtsklick öffnet es, und es behält seinen Inhalt, wenn es den Besitzer wechselt. Ohne das `container`-Objekt fasst es eine einzige Reihe zu neun. Gib ihm `curioSlot`, und wo Curios installiert ist, sitzt es in diesem Slot und eine Taste öffnet es, ohne es abzulegen, standardmäßig `V`, in den Steuerungen unter Resource Data Pack Loader neu belegbar. Erneutes Drücken, während ein getragener Behälter offen ist, geht zum nächsten getragenen weiter und läuft dabei um, sodass mehrere zugleich getragene alle erreichbar sind. Die Taste erscheint nur, wenn Curios da ist, und alles andere am Item, der Rechtsklick und sein Inventar, funktioniert so oder so. Die Tragbarkeit des Beutels wird für dich in den Curios-Tag dieses Slots geschrieben.

**Die Beutetabelle füllt beim ersten Zugriff**, nicht beim Setzen, und genau das macht sie in einem Bauwerk nützlich: wer zuerst öffnet, bekommt den Wurf, und ein Trichter oder Komparator, der zuerst hinkommt, würfelt sie ebenso. Dieselbe Tabelle kann `lootTable` an einer Prägeform oder an einem Dorfgrundstück verwenden, sodass ein Pack diese Blöcke über die Weltgenerierung setzen und gleich bestücken kann.

## Glocken

*blöcke*

`<namespace>/blocks/*.json`

```json
{
  "type": "bell",
  "material": "iron",
  "soundType": "metal",
  "renderLayer": "cutout",
  "creativeTab": "decorations",
  "bell": {
    "swing": true,
    "sound": "minecraft:block.note_block.bell",
    "resonateSound": "minecraft:block.note_block.chime"
  },
  "variants": { "village_bell": { "hardness": 5.0, "resistance": 30 } }
}
```

Und ihr Blockstate, `assets/mypack/blockstates/village_bell.json`, geschlüsselt nach `attachment` und `facing`:

```json
{
  "variants": {
    "attachment=floor,facing=north": { "model": "mypack:block/village_bell_floor" },
    "attachment=floor,facing=east": { "model": "mypack:block/village_bell_floor", "y": 90 },
    "attachment=floor,facing=south": { "model": "mypack:block/village_bell_floor", "y": 180 },
    "attachment=floor,facing=west": { "model": "mypack:block/village_bell_floor", "y": 270 },
    "attachment=ceiling,facing=north": { "model": "mypack:block/village_bell_ceiling" },
    "attachment=ceiling,facing=east": { "model": "mypack:block/village_bell_ceiling", "y": 90 },
    "attachment=ceiling,facing=south": { "model": "mypack:block/village_bell_ceiling", "y": 180 },
    "attachment=ceiling,facing=west": { "model": "mypack:block/village_bell_ceiling", "y": 270 },
    "attachment=single_wall,facing=east": { "model": "mypack:block/village_bell_wall" },
    "attachment=single_wall,facing=south": { "model": "mypack:block/village_bell_wall", "y": 90 },
    "attachment=single_wall,facing=west": { "model": "mypack:block/village_bell_wall", "y": 180 },
    "attachment=single_wall,facing=north": { "model": "mypack:block/village_bell_wall", "y": 270 },
    "attachment=double_wall,facing=east": { "model": "mypack:block/village_bell_between_walls" },
    "attachment=double_wall,facing=south": { "model": "mypack:block/village_bell_between_walls", "y": 90 },
    "attachment=double_wall,facing=west": { "model": "mypack:block/village_bell_between_walls", "y": 180 },
    "attachment=double_wall,facing=north": { "model": "mypack:block/village_bell_between_walls", "y": 270 }
  }
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `swing` | Boolean | `true` | Zeichnet den schwingenden Teil aus einer eigenen Modelldatei und lässt ihn beim Läuten schwingen. `false` zeichnet die ganze Glocke aus den Modellen des Blockstates, ohne Animation |
| `sound` | Soundname | `minecraft:block.note_block.bell` | Wird beim Läuten gespielt. Leer läutet lautlos |
| `resonateSound` | Soundname | `minecraft:block.note_block.chime` | Wird gespielt, wenn die Glocke nachklingt, weil Angreifer in der Nähe sind. Leer klingt lautlos nach |

**Sie hängt so, wie die Glocke des Spiels.** Auf einen Block gesetzt steht sie am Boden, in deine Blickrichtung gedreht; unter einem Block hängt sie von der Decke; an einer Wand hängt sie an dieser Wand, und zwischen zwei Wänden, wenn auch die Gegenseite fest ist. Sie fällt ab, sobald verschwindet, was sie hält, und eine Glocke zwischen zwei Wänden wird zur Glocke an einer Wand, wenn eine davon fehlt. Ihre Trefferbox folgt für jede der vier Arten der Vanilla-Glocke, `bounds` wird also nicht gelesen.

**Was sie läutet.** Die Seite des Glockenkörpers unterhalb des Balkens benutzen: bei einer Bodenglocke die beiden Flächen, über die ihr Balken läuft, bei einer Wandglocke die beiden Flächen neben der Wand, bei einer Deckenglocke jede Seite. Oben, unten und alles oberhalb des Körpers bewirkt nichts. Ein Redstone-Signal läutet sie einmal beim Einschalten, und ein Pfeil, ein Schneeball oder jedes andere Geschoss läutet sie, wenn es eine Seite trifft, an der es auch eine Hand könnte. Der Körper schwingt zweieinhalb Sekunden lang von der getroffenen Seite weg; bei Redstone schwingt er entlang ihrer Ausrichtung.

**Was ein Läuten bewirkt.** Dorfbewohner im Umkreis von 32 Blöcken hören sie und laufen nach Hause, wo sie sich fünfzehn Sekunden lang verstecken, genau wie bei der Glocke des Spiels. Ist ein Angreifer im Umkreis von 32 Blöcken, klingt die Glocke eine Viertelsekunde nach dem Läuten nach, und zwei Sekunden später leuchtet jeder Angreifer im Umkreis von 48 Blöcken drei Sekunden lang, mit farbigen Partikeln neben der Glocke auf der Seite, auf der er steht. Ein Angreifer ist alles, was ein [Raid](#raids) geschickt hat, dazu die Illager und Hexen des Spiels. Eine Glocke dieses Typs ist für jeden Raid eine Dorfglocke: Sie läutet bei jeder eintreffenden Welle, ohne im `bell` des Raids genannt zu sein.

**Die Modelle.** Eine Glocke wird nach `attachment` und `facing` geschlüsselt, sechzehn Zustände insgesamt, und `powered` bleibt aus den Schlüsseln heraus. Mit `swing` an zeichnen diese Modelle nur das Gestell, und der schwingende Teil ist eine eigene Modelldatei, `<namespace>:block/<name>_body`: Der Mod lädt sie über genau diesen Pfad und der Renderer zeichnet sie, der Name ist also nicht frei wählbar, und im Blockstate taucht sie nie auf. Sie wird im Blockraum dort modelliert, wo der Körper hängt, und kippt um den Punkt einen halben Block innen und einen dreiviertel Block hoch, wie die von Vanilla. Mit `swing` aus gibt es keine Körperdatei, und die sechzehn Gestellmodelle zeichnen die ganze Glocke. Das für die Hand geschriebene Item-Modell zeichnet Gestell und Körper zusammen, die Glocke ist in der Hand also ganz zu sehen; liefere `models/item/<name>.json`, um sie anders zu zeichnen.

**Das Schwingen zeichnet der Client.** Ein Läuten erreicht die Spieler als Blockereignis, ein dedizierter Server lässt die Glocke also für alle schwingen, die diese Mod haben, und ein Spieler ohne sie hört die Glocke nur. Sounds, Nachklingen und Leuchten laufen alle auf dem Server.

## Modelle, Blockstates und Texturen

*blöcke und items*

Einen Block oder ein Item zu definieren registriert es. Wie es *aussieht*, ist ein Satz Asset-Dateien in denselben Ordnern und demselben Format, die das Spiel nutzt, unter deinem eigenen Namespace, und auf dieser Version werden die meisten davon für dich geschrieben.

```
assets/mypack/textures/block/ruby_ore.png
assets/mypack/textures/item/ruby.png
assets/mypack/lang/en_us.json
```

**Liefere eine Textur, und der Rest wird erzeugt.** Für jeden Block, dessen Blockstate das Pack nicht mitbringt, schreibt der Mod den Blockstate und die Modelle, die der Typ braucht, mit Verweis auf `textures/block/<name>.png`, wobei `<name>` der Schlüssel der Variante ist, und für jedes Item ohne `models/item/<name>.json` ein Item-Modell mit Verweis auf `textures/item/<name>.png`. Ein Block, der weder eine Textur noch ein eigenes Modell hat, leiht sich das Aussehen von `modelBlock`, standardmäßig Stein, sodass nie etwas als lila-schwarzes Quadrat gezeichnet wird. Liefere einen eigenen `blockstates/<name>.json`, und der Mod erzeugt für diesen Block nichts und nimmt deinen; dasselbe gilt für `models/item/<name>.json`.

| Typ | Texturdateien, nach denen er sucht | Erzeugt aus |
| --- | --- | --- |
| `basic`, `ore`, `falling` | `<name>`, dazu `<name>_top` und `<name>_bottom` für Ober- und Unterseite, wenn das Pack sie mitbringt | `cube_all`, oder `cube_bottom_top`, wenn eine Ober- oder Unterseitentextur da ist |
| `flower`, `sapling`, `cane`, `leaves`, `container` ohne Truhe | `<name>` | `cube_all`, `cross` oder `leaves` |
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
| `bell` | `<name>` | die Glockengestelle des Spiels als `<name>_floor`, `<name>_ceiling`, `<name>_wall` und `<name>_between_walls`, das `pack_bell_body` des Mods als `<name>_body`, und darüber ein Blockstate mit sechzehn `attachment`- und `facing`-Zuständen |
| `crop` | `<name>_stage0` bis `<name>_stage<maxAge>`, oder `<name>` für alle | ein `crop`-Modell pro Stufe, `age=0` bis `7` darauf abgebildet |
| `portal` | `<name>`, oder die des Netherportals | `cube_all` für einen `fullCube`-Block, wie ein Portalblock in 1.12.2; drei Portalplatten, eine je Achse, für einen ohne, etwa das Rahmenportal einer Dimension |
| `banner` | sein eigenes Blatt, siehe [Banner](#banner) | das Bannermodell des Spiels |
| `container` mit `chestModel` | das in `chestModel` genannte Truhenblatt | das `pack_chest`-Modell des Mods |

Jede Textur wird unter `textures/block/` gesucht, und der Name ist der Schlüssel der Variante, ein als `ruby_ore` registrierter Block will also `textures/block/ruby_ore.png`, und sonst muss nichts geschrieben werden. Ein Block, dessen Item flach gezeichnet wird, eine Tür, eine Leiter, eine Fackel, ein Setzling, eine Blume, ein Rohr, eine Ranke oder eine Scheibe, nimmt für die Hand `textures/item/<name>.png`, wenn es sie gibt, und seine Blocktextur, wenn nicht.

**Items** nehmen `textures/item/<name>.png` und ein erzeugtes `item/generated`-Modell, oder `item/handheld` für ein Werkzeug. Liefere `models/item/<name>.json`, um es anders zu zeichnen.

**Flüssigkeiten** brauchen überhaupt kein Modell; es wird aus den Texturen `still` und `flow` erzeugt.

**Ein Block mit mehreren Varianten ist mehrere Blöcke.** Jeder Schlüssel unter `variants` wird für sich registriert, jeder hat also seinen eigenen Blockstate, seine eigenen Modelle und seine eigenen Texturen, nach dem Schlüssel benannt. Es gibt keinen geteilten Blockstate mit einer `blocks`-Eigenschaft, und nichts in einem Blockstate muss sagen, welche Variante es ist: `blockstates/ruby_ore.json` gehört dem Rubinerz, und `blockstates/deep_ruby_ore.json` dem tiefen.

### Selbst schreiben

*modelle, blockstates und texturen*

Alles Erzeugte lässt sich ersetzen. Ein Blockstate, den das Pack mitbringt, wird so genommen, wie er ist, im eigenen Format des Spiels: die Vanilla-`variants`, geschlüsselt nach den Eigenschaften des Blocks, oder `multipart`. Die Eigenschaften sind die des Spiels für jeden Typ: `axis` an einem Stamm, `type` an einer Stufe, `facing`, `half` und `shape` an Treppen, `facing`, `half`, `hinge` und `open` an einer Tür, `facing`, `half` und `open` an einer Falltür, `facing`, `in_wall` und `open` an einem Tor, `age` an einer Feldfrucht und einem Rohr, `stage` an einem Setzling, `north`, `east`, `south`, `west` an einem Zaun oder einer Scheibe mit `up` dazu an einer Mauer und einer Ranke, `rotation` an einem stehenden Banner und `facing` an einem Wandbanner, `axis` an einem Portal, `attachment` und `facing` an einer Glocke, deren `powered` aus den Schlüsseln herausbleibt. Ein `basic`-, `ore`-, `falling`-, `leaves`-, `flower`- oder `container`-Block hat einen Zustand, geschlüsselt `""`.

Zeig mit den Modellen auf die Eltern, die Texturen annehmen, nicht auf die fertigen Vanilla-Modelle: `cube_all` nimmt ein `all`; `cube_column` ein `end` und eine `side`; `cross` ein `cross`; die Treppeneltern `bottom`, `top` und `side`; `fence_post` und `fence_side` eine `texture`; `template_wall_post` und `template_wall_side` eine `wall`; die Glasscheiben-Vorlagen eine `pane` und eine `edge`; die Türeltern ein `top` und ein `bottom`; `template_orientable_trapdoor_*` und `template_fence_gate*` eine `texture`; `template_torch` eine `torch`; `crop` eine `crop`; `vine` und `ladder` ihren eigenen Namen. Ein Modell, das ein fertiges Vanilla-Modell wie `oak_door_bottom_left` nennt, erbt Vanillas Texturen mit, was auch immer der Blockstate sagt.

### Banner

*modelle, blockstates und texturen*

Beim Banner gehen die Form des Blocks und die Form des Modells als Einziges getrennte Wege, deshalb sei es hier vollständig aufgeschrieben.

**Es registriert zwei Blöcke.** Eine Definition liefert dir das stehende Banner unter deinem eigenen Namen und einen zweiten Block namens `<name>_wall` für das hängende. Beide brauchen einen Blockstate; ein Item bekommt nur das stehende, und dieses Item entscheidet, welchen der beiden es setzt: das stehende, wenn du oben auf einen Block klickst, das hängende, wenn du auf eine Seite klickst. Den Wandblock setzt du nie selbst, und ein eigenes Item braucht er nicht.

**Das stehende dreht sich in Sechzehnteln.** Seine Eigenschaft heißt `rotation` und läuft von `0` bis `15`, denn ein Banner dreht sich in Sechzehnteln statt in Vierteln. Das `y` eines Blockstates nimmt nur 0, 90, 180 und 270 an, deshalb zeigt jede Drehung auf ein kleines eigenes Modell, das dein Bannermodell als Elternmodell nimmt und es mit einem `transform` dreht:

```json
{
  "variants": {
    "rotation=0": { "model": "meinpack:block/mein_banner_rotation_0" },
    "rotation=1": { "model": "meinpack:block/mein_banner_rotation_1" }
  }
}
```

```json
{
  "parent": "meinpack:block/mein_banner",
  "transform": { "rotation": { "y": -22.5 }, "origin": "center" }
}
```

…und so weiter bis `15`, jeder Eintrag `-22,5` Grad weiter herum. Das Vorzeichen entspricht den spieleigenen Bannern, die sich um minus die Drehung drehen. Bau das Modell nach Süden zeigend, denn dort landet ein Banner, das ein nach Süden blickender Spieler setzt. Der Wandblock ist ein ganz gewöhnlicher Blockstate mit den üblichen vier `facing`-Einträgen bei 0, 90, 180 und 270, an ihm ist nichts Krummes. Der Forge-Blockstate eines 1.12.2-Packs wird beim Umwandeln des Packs genau hierzu.

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

**Ohne Blockstate wird es aus einem Blatt gezeichnet.** Ein Banner, zu dem sein Pack keinen Blockstate mitbringt, bekommt einen erzeugten, und der Banner-Renderer des Spiels zeichnet es in der Form des Vanilla-Banners aus dem Blatt unter `textures/entity/banner/<name>.png`, das aufgebaut ist wie das des Vanilla-Banners. Das kommt in dieser Version hinzu; der stehende und der Wandblock entscheiden es jeweils an ihrem eigenen Blockstate.

**Farben und Muster gibt es daran nicht.** Ein Pack-Banner trägt keine Liste von Lagen, wie Vanilla-Banner sie führen. Das Muster ist die Textur, so wie das Aussehen einer Tür ihre Textur ist, und eine Definition ist ein Banner. Es zu färben und Muster darauf zu stapeln liegt außerhalb dessen, was ein Pack erreichen kann.

**Es nimmt das `material`, das du ihm gibst.** Ein steinernes Banner wird mit der Spitzhacke abgebaut, wie es der Stein verlangt, für den es sich ausgibt.

### Texturen als Pixelkarte

*modelle, blockstates und texturen*

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

**Welche von zwei Texturen die Vorlage ist**, entscheidet sich daran, welche mehr Unterschiede festhält, nicht daran, welche zuerst gezeichnet wurde. Eine Variante gibt jedem Zeichen eine Farbe, also wird jedes Pixel, das die Vorlage gleich benennt, in der Variante auch gleich. Ein Erz auf Stein kann die `rows` des Steins deshalb nicht erben: Der Stein nennt die Sprenkelstellen schlicht Stein, und nichts, was eine Variante schreiben kann, spaltet ein Zeichen in zwei. Andersherum geht es auf. Nimm das Erz als Vorlage, dann haben Steintöne und Erztöne je eigene Zeichen, und ein zweites Erz sind vier Farben:

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

*modelle, blockstates und texturen*

**Ein Modell, das ein fertiges Vanilla-Modell nennt, erbt auch Vanillas Texturen.** `torch`, `ladder`, `oak_door_bottom_left` und `wheat_stage0` bringen alle ihre eigenen Texturen mit, ein Modell, das auf eines davon zeigt, bekommt also Vanillas Aussehen, ganz gleich, was du daneben schreibst. Eltern-Modelle wie `cube_all`, `cross` und `crop` nehmen ihre Texturen aus dem Modell, das sie nennt, und verhalten sich wie erwartet, ebenso die Vorlagen für Tür, Falltür und Tor.

**Namen kommen aus der Sprachdatei.** Ein Block oder Item zeigt seinen rohen Schlüssel, bis `lang/en_us.json` ihm einen gibt, und die Schlüssel sind die des Spiels: `block.mypack.ruby_ore` für einen Block und das Item, das ihn setzt, `item.mypack.ruby` für ein Item, `itemGroup.mypack.tab` für einen Kreativtab, `fluid_type.mypack.molten_ruby` und `fluid.mypack.molten_ruby` für eine Flüssigkeit, `effect.mypack.ruby_sight` für einen Trankeffekt, `entity.mypack.angry_cow` für eine Entity-Variante, `biome.mypack.ruby_forest` für ein Biom. Ein Name je Ding; nichts auf dieser Version will einen Schlüssel doppelt.

## Damit Vanilla deinen Block richtig behandelt

*blöcke und items*

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

| Wert | Was er bewirkt |
| --- | --- |
| `till` | Eine Hacke macht Ackerboden daraus, oder das, was `hoeTillsInto` in der Config nennt |
| `path` | Eine Schaufel macht einen Trampelpfad daraus, oder das, was `shovelPathBecomes` nennt |
| `bush` | Blumen, Gras und Setzlinge lassen sich darauf pflanzen und bleiben stehen, wie auf Erde. Dasselbe wie `plains` in `plantTypes` |
| `animals` | Tiere spawnen darauf im Hellen, wie auf Gras |

## Items

*blöcke und items*

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

### Item-Typen

*items*

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

### Item-Dateischlüssel

*items*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `variants` | ja | Objekt aus Variantenname zu Variante | | Ein Item pro Eintrag. Der Schlüssel ist sein Registry-Name und benennt sein Modell, seine Textur und seinen Sprachschlüssel |
| `type` | nein | einer der Typen oben | `basic` | Welchen Typ das Item annimmt |
| `creativeTab` | nein | Tab-Name | keiner | Der Tab, in dem es auftaucht, siehe [Kreativ-Tabs](#kreativ-tabs) |
| `material` | tool, armor | Materialname | keiner | Aus welchem deiner Materialien es gemacht ist |
| `toolClass` | tool | `pickaxe`, `axe`, `shovel`, `sword` | keiner | Welches Werkzeug es ist. Ein `sword` ist ein Werkzeug wie in 1.12.2, kein Vanilla-Schwert: Es trifft mit 3 plus dem `damage` des Materials, baut die Blöcke ab, deren `harvestTool` `sword` ist, nimmt Abbau-Verzauberungen an und macht weder Schwungangriffe, noch schneidet es Spinnweben |
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

### Item-Variantenschlüssel

*items*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `maxSize` | nein | 1 bis 64 | `64` | Stapelgröße |
| `rarity` | nein | `common`, `uncommon`, `rare`, `epic` | `common` | Farbe des Namens im Tooltip |
| `healAmount` | food | int, halbe Hähnchenkeulen | `0` | Wiederhergestellter Hunger |
| `saturation` | food | float | `0.0` | Wiederhergestellte Sättigung |
| `tags` | nein | Liste von Tag-IDs | keine | Item-Tags, in die diese Variante geschrieben wird; die Tag-Dateien werden für dich erzeugt |
| `potion` | food, drink | `potion,duration,amplifier` | keiner | Ein Effekt, der beim Essen oder Trinken der Variante angewandt wird. Ein vierter Teil, `true`, macht ihn umgebend. Ein guter Effekt wird im Tooltip grün genannt, gefolgt vom Verstärker in römischen Ziffern, wenn er über 0 liegt, und keiner seiner Effekte zeigt Partikel |

## Flüssigkeiten

*blöcke und items*

`<namespace>/fluids/*.json`

Der Pfad der Datei ist der Registry-Name des Flüssigkeitsblocks. `name` benennt die Flüssigkeit selbst, ihren Eimer und ihre Sprachschlüssel und ist der Pfad der Datei, sofern die Datei ihn nicht setzt.

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
| `name` | nein | string | der Dateiname | Der Registry-Name der Flüssigkeit und ihres Eimers (`<name>_bucket`); der Block behält den Pfad der Datei |
| `still` | nein | Texturpfad | Vanilla-Wasser, stehend | Textur für die stehende Flüssigkeit |
| `flow` | nein | Texturpfad | Vanilla-Wasser, fließend | Textur für die fließende Flüssigkeit |
| `color` | nein | Hex-Farbe | keine | Färbung, die auf diese Texturen gelegt wird. Auf den Standard-Wassertexturen wird sie mit dem Blau des Wassers aus 1.12.2 multipliziert, sodass eine für 1.12.2 gewählte Farbe hier gleich aussieht |
| `bucket` | nein | boolean | `true` | Einen Eimer dafür registrieren |
| `luminosity` | nein | 0 bis 15 | `0` | Abgegebenes Licht |
| `density` | nein | int | `1000` | Negativ steigt nach oben, wie ein Gas |
| `temperature` | nein | int, Kelvin | `300` | Wasser ist 300, Lava 1300 |
| `viscosity` | nein | int | `1000` | Wie träge sie fließt: Die Flüssigkeit rückt alle viscosity / 200 Ticks weiter. Wasser ist 1000, Lava 6000 |
| `gaseous` | nein | boolean | `false` | Wird als Gas behandelt |
| `creativeTab` | nein | Tab-Name | keiner | Der Tab, in dem der Eimer auftaucht |
| `block` | nein | Objekt | | Der Flüssigkeitsblock. `material` (`water`): In `water` kann man schwimmen und ertrinken, Boote schwimmen darauf, brennende Wesen werden gelöscht, Ackerland bleibt feucht, und im Nether verdampft sie beim Ausgießen; `lava` setzt alles in Brand, was darin steht, lässt weder Schwimmen noch Ertrinken zu und klingt im Eimer wie Lava; jedes andere Material tut nichts davon. `flammability` (`0`) und `fireSpread` (`0`): wie bereitwillig Feuer den Block verzehrt und sich von ihm ausbreitet. `quantaPerBlock` (`0`, gelesen als 8): wie weit sie von einer Quelle aus fließt, wie auf 1.12.2 einen Block weniger als diese Zahl; Flüssigkeiten reichen hier 1, 2, 3 oder 7 Blöcke weit, 4 und 5 fließen also 3 Blöcke und 6 und mehr 7. `potions` (keine, eine Liste von Effekten für alles, was darin steht, je geschrieben als `potion,duration,amplifier`, mit einem optionalen vierten Teil `true` für einen umgebenden) |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

## Materialien, Tabs, Sounds, Tags

*blöcke und items*

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
| `repairItem` | nein | Itemname | keiner | Was ein daraus gefertigtes Werkzeug im Amboss repariert. Rüstung daraus lässt sich so nicht reparieren, wie in 1.12.2 |
| `reduction` | nein | Liste aus vier Ints | | Rüstungspunkte, in der Reihenfolge Füße, Beine, Brust, Kopf |
| `toughness` | nein | float | `0.0` | Rüstungshärte, wie Diamant sie hat |
| `equipSound` | nein | Soundname | `item.armor.equip_iron` | Sound beim Anlegen der Rüstung |
| `armorTexture` | nein | Texturpräfix | der Dateiname | Die Textur der getragenen Rüstung, gelesen aus `textures/models/armor/<name>_layer_1.png` und `_layer_2.png` unter diesem Namespace |

### Kreativ-Tabs

*materialien, tabs, sounds, tags*

`<namespace>/tabs/*.json`

Blöcke, Items und Eimer nennen ihren Tab in `creativeTab`. Eine vollständige Id wie `mypack:rubypack` oder `minecraft:combat` gilt so, wie sie dasteht. Ein bloßer Name wird gelesen, wie 1.12.2 ein Tab-Label las: `buildingBlocks` landet in `minecraft:building_blocks`, `decorations` in `minecraft:functional_blocks`, `redstone` in `minecraft:redstone_blocks`, `transportation` und `tools` in `minecraft:tools_and_utilities`, `misc` und `materials` in `minecraft:ingredients`, `food` und `brewing` in `minecraft:food_and_drinks` und `combat` in `minecraft:combat`, und jeder andere bloße Name ist der Tab `<namespace>:<name>` im Namespace der Datei, die ihn nennt. Ein Tab, den keine Datei deklariert, wird für dich angelegt, betitelt über `itemGroup.<namespace>.<name>` und mit dem ersten Item darin als Symbol. Der Pfad einer Tab-Datei ist der Name des Tabs, sofern `label` ihn nicht überschreibt.

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `label` | nein | string | der Dateiname | Die Id des Tabs: Blöcke und Items nennen sie in `creativeTab`, der angezeigte Name kommt aus `itemGroup.<namespace>.<label>` in den Sprachdateien |
| `icon` | nein | Itemname | keiner | Das Item, das auf dem Tab abgebildet ist |

### Sounds

*materialien, tabs, sounds, tags*

`<namespace>/sounds/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

Das Vanilla-Format von `sounds.json`, ein Pack kann also eigenes Audio mitbringen. Eine Datei hier registriert die Sound-Events; das Audio liest der Client nach wie vor über die packeigene `assets/<namespace>/sounds.json`, liefere also beides, den Index unter `assets` und die Events unter `data`.

### Tags

*materialien, tabs, sounds, tags*

`<namespace>/tags/<kind>/*.json`

Tags sind das Format des Spiels im Ordner des Spiels, und ein Pack liefert sie wie in einem Datenpaket: `tags/items/ores/ruby.json` (1.21.1: `tags/item/`) mit `{ "values": ["mypack:ruby_ore"] }` steckt das Erz in `mypack:ores/ruby`, und eine Datei unter `data/forge/tags/items/ores/ruby.json` (1.21.1: `data/c/...`) ergänzt den gemeinsamen Konventions-Tag, den jede Mod liest. Eigene Blöcke und Items eines Packs nennen ihre stattdessen im `tags` der Variante, und die Dateien werden für dich geschrieben; ein `harvestTool` und `harvestToolLevel` schreiben die Tags `mineable` und `needs_*_tool` auf dieselbe Weise.

Das Ore Dictionary von 1.12.2 ist das, was Tags abgelöst haben. Seine Namen bilden sich auf die Konventions-Tags ab: `oreRuby` ist `forge:ores/ruby` auf 1.20.1 und `c:ores/ruby` auf 1.21.1, `ingotCopper` ist `ingots/copper`, `gemRuby` ist `gems/ruby`, `dustX` ist `dusts/x`, `nuggetX` ist `nuggets/x`, `blockX` ist `storage_blocks/x`, und `logWood`, `plankWood` und `stickWood` sind die spieleigenen `minecraft:logs`, `minecraft:planks` und das Konventions-`rods/wooden`. Eine Datei, die ein Item aus einem Tag entfernt, gibt es nicht; ein Datenpaket-Tag mit `"replace": true` schreibt stattdessen den ganzen Tag neu.

## Eigenschaften überschreiben

*blöcke und items*

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

### Blockeigenschaften

*eigenschaften überschreiben*

Jeder Schlüssel ist optional und eine Datei ändert nur, was sie benennt: Eine Datei unter `overrides/minecraft/stone.json` mit `hardness`, `light` und `soundType` allein lässt Stein fast sofort abbauen, leuchten und wie Glas klingen. Eine Datei trägt Block-, Item- und Trankschlüssel zusammen. Diese gelten, wenn das Ziel ein Block ist:

| Schlüssel | Wert | Was er tut |
| --- | --- | --- |
| `hardness` | Zahl | Abbauzeit, dieselbe Zahl wie in einer Blockdefinition. Ohne `resistance` hebt sie auch den Explosionswiderstand auf mindestens dieselbe Zahl, wie in 1.12.2 |
| `resistance` | Zahl | Explosionswiderstand, so wie 1.12.2 ihn liest: Der Block behält drei Fünftel der Zahl, `10` ergibt also die `6` von Stein |
| `slipperiness` | Zahl | `0.6` ist normaler Boden, `0.98` ist Eis |
| `light` | `0` bis `15` | Abgegebenes Licht |
| `lightOpacity` | `0` bis `15` | Wie viel Licht der Block schluckt |
| `soundType` | einer der Klangtypen | Schritt-, Setz- und Abbaugeräusche |
| `harvestTool` | `pickaxe`, `axe`, `shovel`, `hoe` oder `sword` | Womit er schnell abgebaut wird, in die Werkzeug-Tags geschrieben; `harvestToolLevel`, Standard `0`, setzt die Stufe: 1 Stein, 2 Eisen, 3 Diamant, ab 4 Netherit. Ob der Block ohne das richtige Werkzeug etwas fallen lässt, bleibt, wie der Block es festlegt |
| `flammability` | Ganzzahl | Wie bereitwillig er verbrennt; `fireSpread`, Standard `5`, wie bereitwillig Feuer ihn erreicht |

### Item-Eigenschaften

*eigenschaften überschreiben*

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

### Wirkungen von Trankarten

*eigenschaften überschreiben*

`effects` auf der obersten Ebene der Datei schreibt die Effektliste eines Tranktyps komplett neu:

```json
{
  "effects": [
    { "potion": "minecraft:levitation", "duration": 200, "amplifier": 0 }
  ]
}
```

Mit `overrides/minecraft/swiftness.json` gibt der Trank der Schnelligkeit jetzt Schwebekraft. Jeder Eintrag nimmt `potion` (Pflicht), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) und `showParticles` (`true`), genau wie in `potion_types/`, und die Liste darf nicht leer sein.

### Andere Mods, Neuladen und Grenzen

*eigenschaften überschreiben*

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

## Härtegruppen

*blöcke und items*

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
  "keeps": false,
  "adventure": { "tools": ["minecraft:iron_pickaxe"], "teams": ["red"], "players": [], "entities": ["mypack:digger"] },
  "advancement": "mypack:deep_miner",
  "becomes": { "advancement": "mypack:deep_miner", "block": "mypack:rich_ore" },
  "requires": ["mypack"]
}
```

### Abbauen und Sprengen

*härtegruppen*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `blocks` | ja | Liste von Blocknamen oder Objekten | | Die Gruppe. Dieselben Formen wie `replace` bei der Weltgenerierung |
| `except` | nein | Liste von Blocknamen oder Objekten | keine | Wieder aus der Gruppe genommen, was auch immer `blocks` sagt |
| `miningTime` | nein | Zahl oder Objekt mit `min` und `max` | `1.0` | Um wie viel länger der Block zum Abbauen braucht |
| `blastResistance` | nein | Zahl oder Objekt mit `min` und `max` | `1.0` | Multipliziert den Explosionswiderstand des Blocks |
| `buckets` | nein | 1 bis 256 | `10` | In wie viele Stufen die Spanne geteilt wird |
| `minHeight` | nein | Ganzzahl | der Boden der Welt | Darunter ist der Wurf die härteste Stufe |
| `maxHeight` | nein | Ganzzahl | die Obergrenze der Welt | Darüber ist der Wurf die härteste Stufe |
| `field` | nein | Objekt | siehe unten | Die Form, zu der sich der Wurf zusammenballt |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

Eine einzelne Zahl gibt jedem Block der Gruppe denselben Faktor, und nichts wird gewürfelt. Ein `min` und ein `max` würfeln pro Position: `max`, wo das Feld leer ist, `min` in der Mitte eines Nestes, und die Stufen dazwischen entscheidet `buckets`.

### Abbauen im Abenteuermodus und Freischaltungen

*härtegruppen*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `keeps` | nein | Wahrheitswert | `false` | Der Block bleibt stehen, wenn er abgebaut ist: Drops, Erfahrung, Werkzeugverschleiß und Bruchgeräusch geschehen alle, und der Block ist noch da, um ihn erneut abzubauen, sodass die Gruppe eine unerschöpfliche Ader ist, in dem Tempo, das `miningTime` vorgibt. Kreativ entfernt ihn wie immer |
| `adventure` | nein | Objekt | keins | Wer die Gruppe im Abenteuermodus abbauen darf, wo sonst nichts bricht. `tools` nennt die Gegenstände, von denen einer in der Hand sein muss, leer für alles, was gehalten wird; `teams`, `players` und `entities` sagen wer, ein Team über seinen Namen, ein Spieler über seinen Namen, ein Mob über seine Entity-Id für die `digs`-Aufgabe, und alle drei leer heißt jeder mit dem Werkzeug. Überleben und Kreativ bleiben unberührt |
| `advancement` | nein | `namespace:pfad` | keins | Die Gruppe gilt für einen Spieler erst, wenn er diesen Fortschritt hat. Zwei Gruppen dürfen denselben Block nennen, eine mit Fortschritt und eine ohne, und die freigeschaltete gewinnt; ein Spieler ohne ihn bekommt die schlichte Gruppe, oder Vanilla, wenn es keine gibt. Mobs haben keine Fortschritte, also erreicht eine gesperrte Gruppe nie eine `digs`-Aufgabe, und Explosionswiderstand und der Texturwurf, die keinem Spieler gehören, kommen aus der schlichten Gruppe |
| `becomes` | nein | Objekt | keins | Die Blöcke der Gruppe werden weltweit zu einem anderen Block, sobald irgendein Spieler `advancement` erreicht: `{ "advancement": "mypack:deep_miner", "block": "mypack:rich_ore" }`. Jeder geladene Chunk wird sofort durchgegangen, ein später geladener oder erzeugter, sobald er hereinkommt, sodass der alte Block für immer weg ist. Gib dem neuen Block eine eigene Gruppe, um sein Abbauen zu ändern |

### Das Feld

*härtegruppen*

Der Wurf geschieht nicht für jeden Block ganz allein, sonst wären hart und weich reines Rauschen ohne jede Form. `field` bestimmt, welche Form dabei herauskommt, und `type` wählt zwischen zwei Wegen dorthin.

```json
{
  "field": { "type": "speckle" }
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `type` | nein | `speckle` oder `seeded` | `speckle` | Welches der beiden unten genommen wird |

#### speckle

*das feld*

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

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `chances` | nein | Liste von Ganzzahlen, je Tausend | `[30, 30, 20, 20, 10, 10, 10, 10, 50]` | Wie oft ein Block auf welcher Stufe anfängt, weichste zuletzt. Was übrig bleibt, ist die härteste Stufe |
| `spread` | nein | 0.0 bis 1.0 | `0.15` | Wie oft eine Stufe an den Nachbarblock weitergeht, eine bis drei Stufen schwächer |

Die Liste wird von hinten als weichste gelesen, der letzte Eintrag ist also die weichste Stufe und der erste liegt eine über der härtesten. Mit den Zahlen oben sind etwa sieben von zehn Blöcken die härteste Stufe, der Rest liegt verstreut dazwischen.

#### seeded

*das feld*

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

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `cell` | nein | Ganzzahl, Blöcke | `8` | Wie weit die Saatpunkte auseinanderliegen |
| `seeds` | nein | 1 bis 4 | `1` | Saatpunkte je Zelle |
| `reach` | nein | Kommazahl, Blöcke | `3.0` | Wie weit ein Saatpunkt wirkt |
| `arms` | nein | 0 bis 6 | `0` | Arme, die von jedem Saatpunkt ausgehen |
| `armReach` | nein | Kommazahl, Blöcke | `0.0` | Wie weit die Arme reichen |

Ohne `arms` sind die Nester rund. Gibt man einem Saatpunkt Arme, wird er zu einem Knoten mit Ranken, und Arme benachbarter Knoten strecken sich einander entgegen, das ist dann eine Ader statt eines Klumpens. Halte `reach` über der Hälfte von `cell`, sonst können die Nester einander nicht berühren und es bleiben einzelne Kugeln mit nichts dazwischen.

### Sichtbar machen

*härtegruppen*

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

*härtegruppen*

Nur das Abbauen durch einen Spieler wird verändert. Maschinen, die Blöcke abbauen, lesen die Härte des Blocks direkt und merken nichts davon. Blöcke, die ein Spieler setzt, werden wie alle anderen gewürfelt, denn der Wurf gehört zum Ort und nicht zum Block, und ein anderswohin getragener Block nimmt an, was sein neuer Ort sagt.

---

# Herstellung, Beute und Handel

## Ofenrezepte und Brennstoffe

*herstellung, beute und handel*

`<namespace>/furnace/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

Fügt Ofenrezepte hinzu und entfernt sie. Eine Entfernung trifft auch die passenden Schmelzofen-, Räucherofen- und Lagerfeuerrezepte, da 1.12.2 alle Kochrezepte in der einen Ofenliste führte; eine Hinzufügung ist nur ein Ofenrezept.

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
| `experience` | nein | Zahl | `0.0` | Erfahrung je geschmolzenem Gegenstand, höchstens eins: ab 1.0 gibt es einen Punkt für jeden entnommenen Gegenstand, ein `count` von 2 gibt also zwei. Eisenerz gibt 0.7 |

Ein Zusatz, dessen Eingabe schon etwas schmilzt, wird ignoriert, und das Log nennt das Rezept, das im Weg steht, wie auf 1.12.2; entferne dieses Rezept in derselben oder einer früheren Datei, um es zu ersetzen.

Einträge unter `remove` sind entweder ein bloßer Itemname, der jedes Rezept entfernt, das ihn herstellt, oder ein Objekt, das mit `input`, `result` oder beidem eingrenzt. Eine Entfernung, die weder das eine noch das andere nennt, wird übersprungen, und das Log sagt es.

Die Dateien gelten in Ladereihenfolge, bei jeder Datei die Entfernungen vor den Zusätzen. Eine Entfernung in einer späteren Datei nimmt also auch einen Zusatz einer früheren Datei heraus, erreicht aber nie einen Zusatz, der nach ihr kommt. Ein Zusatz zählt als Ofenrezept der Mod, der sein Ergebnis gehört, `blockFurnaceRecipes` und `blockedFurnaceMods` sperren ihn also wie jedes andere.

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

*herstellung, beute und handel*

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
| `effectiveness` | nein | float | `0.5` | Wird gelesen, damit eine Datei aus 1.12.2 lädt; weder 1.12.2 noch diese Version macht etwas damit |
| `attributes` | nein | Liste von Objekten | keine | `attribute` (die Id des Spiels, etwa `minecraft:generic.movement_speed`), `uuid`, `amount` (`0.0`), `operation` (`0`) |
| `icon` | nein | Objekt | keins | `x` und `y`, Spalte und Zeile des Symbols auf dem Statusblatt von 1.12.2, je `0`, wenn weggelassen. Nur ohne `iconTexture` gelesen |
| `iconTexture` | nein | Texturpfad | das RDPL-Symbol, oder keins, wenn `icon` gesetzt ist | Ein Bild, das ein Pack mitbringt, etwa `mypack:textures/effect/rage.png`, ganz als Symbol gezeichnet |

Das Symbol des Effekts ist die Textur `assets/<namespace>/textures/mob_effect/<name>.png`, 18 mal 18 wie die des Spiels, und ein Pack, das sie dort mitbringt, hat immer Vorrang. Sonst nennt `iconTexture` ein Bild, das ein Pack mitbringt, und es wird ganz dorthin kopiert; `icon` allein wählt das Symbol eines Vanilla-Effekts nach seinem Platz auf dem Statusblatt von 1.12.2, `x` nach rechts und `y` nach unten ab 0, etwa `{ "x": 2, "y": 1 }` für Sprungkraft; ein Effekt, der keins von beiden nennt, zeigt das RDPL-Symbol.

### Trankarten

*tränke, trankarten und brauen*

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
| `baseName` | nein | string | Namespace und Name | Benennt den Trank: der Sprachschlüssel `item.minecraft.potion.effect.<baseName>`, für die anderen Formen mit `splash_potion`, `lingering_potion` oder `tipped_arrow` statt `potion`. Eine `potion_bottle`, die ihn enthält, zeigt denselben Namen, und die Schlüssel `potion.effect.<baseName>` eines Packs für 1.12.2 werden umgewandelt |
| `effects` | ja | Liste von Objekten | | Siehe unten |

Jeder Effekt nimmt `potion` (Pflicht), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) und `showParticles` (`true`).

### Brauen

*tränke, trankarten und brauen*

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

## Ambosswerk

*herstellung, beute und handel*

`<namespace>/anvils/*.json`

Der Dateiname ist deine Sache, nur der Ordner wird gelesen, und mehrere Dateien stapeln sich. Jede Datei ist ein Stück Arbeit.

Lege den genannten Gegenstand in den linken Platz eines Ambosses und seinen `with`-Gegenstand in den rechten, und der Amboss bietet den linken mit den aufgeführten Verzauberungen zurück, oder sein `result`, für die genannten Stufen; von beiden wird je einer verbraucht, sofern keine Anzahl mehr verlangt, und der Rest jedes Stapels bleibt im Amboss. Das Herausnehmen kann zugleich einen Fortschritt einbringen, und der Gegenstand kann bis zu diesem Fortschritt vom Gebrauch zurückgehalten werden: ein Schwert, das erst schwingt, wenn es bearbeitet wurde.

```json
{
  "item": "minecraft:iron_sword",
  "with": "minecraft:wooden_sword",
  "levels": 3,
  "enchantments": { "minecraft:sharpness": 2 },
  "grants": "mypack:sword_rite",
  "locks": true
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `item` | ja | Gegenstandsname, eine Liste davon oder `{ "item", "count" }` | | Was in den linken Platz kommt, bei einer Liste jeder ihrer Gegenstände, und wie viele davon eine Arbeit nimmt, standardmäßig einer; der Rest des Stapels bleibt für die nächste liegen. `{ "item": "minecraft:coal", "count": 8 }` mit einem Diamanten als `result` macht aus acht Kohle einen Diamanten |
| `with` | ja | Gegenstandsname, eine Liste davon oder `{ "item", "count" }` | | Was in den rechten Platz kommt, bei einer Liste jeder ihrer Gegenstände, und wie viele davon verbraucht werden, standardmäßig einer: `{ "item": "minecraft:coal", "count": 10 }` verlangt einen Stapel von mindestens zehn und nimmt zehn. Ein Amboss meldet sich nie für einen einzelnen Gegenstand, also ist jede Arbeit ein Paar |
| `result` | nein | Gegenstandsname oder `{ "item", "count" }` | der linke Gegenstand | Was statt des linken Gegenstands herauskommt und wie viele, standardmäßig einer, mit dessen Tags, sodass eine unzerbrechliche Eisenspitzhacke und zehn Kohle als unzerbrechliche Diamantspitzhacke zurückkommen können. Die Verzauberungen landen auf dem, was herauskommt |
| `levels` | nein | int | `1` | Die Erfahrungsstufen, die die Arbeit kostet, mindestens 1 |
| `enchantments` | nein | Objekt von Verzauberungsname zu Stufe | keine | Womit der Gegenstand zurückkommt. Eine Stufe, die er schon in dieser Höhe oder darüber hat, bleibt unberührt, und gibt es nichts zu erhöhen, bietet der Amboss nichts an, es sei denn, `grants` ist gesetzt |
| `grants` | nein | `namespace:pfad` | keiner | Ein Fortschritt, der beim Herausnehmen der Arbeit erreicht wird. Liefere ihn unter `advancements/` mit einem `impossible`-Kriterium, damit nichts anderes ihn erreicht |
| `locks` | nein | Wahrheitswert | `false` | Bis der Spieler `grants` hat, kann der Gegenstand nicht geschwungen, benutzt oder zum Graben genommen werden; ihm wird gesagt, worauf er wartet, sobald er ihn in die Hand nimmt. In den Amboss legen bleibt erlaubt, und so wird er freigeschaltet |

Die eigenen Reparaturen und Kombinationen des Ambosses bleiben unberührt: Das hier antwortet nur, wenn links ein genannter Gegenstand liegt und rechts sein `with`.

Ein Mob mit `collectsExperience` gibt hier ebenfalls seine Stufen aus. Solange er `item` in der Haupthand und `with` in der Nebenhand hält und `levels` bezahlen kann, geht er zu einem Amboss, angeschlagenen oder beschädigten Amboss im Umkreis von 16 Blöcken waagerecht und 4 nach oben oder unten und benutzt ihn, sobald er höchstens 3 Blöcke entfernt ist: Die Stufen gehen von seinen eigenen ab wie bei einem Spieler, `with` wird verbraucht, der Amboss nutzt sich ab wie unter einem Spieler, und das Ergebnis landet in seiner Haupthand. Läuft er über einen fallengelassenen Gegenstand, den ein Ambosswerk unter `with` nennt, hebt er ihn in die Nebenhand auf. `grants` und `locks` betreffen nur Spieler, also bringt `grants` einem Mob nichts ein, und keine Sperre hält ihn auf.

## Blockdrops

*herstellung, beute und handel*

`<namespace>/block_drops/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

Die Beutetabelle eines Blocks bestimmt, was er fallen lässt, doch wer eine liefert, übernimmt die ganze Tabelle, und eine Beutetabelle kann keine Erfahrung geben. Eine Regel hier nennt einen Block und das, was beim Abbau zusätzlich zu den üblichen Drops fällt, oder an deren Stelle, Erfahrung eingeschlossen, und lässt die Tabelle des Blocks unangetastet.

```json
{
  "block": "minecraft:stone",
  "replace": false,
  "advancement": "mypack:deep_miner",
  "drops": [
    { "item": "minecraft:diamond", "count": "1-2", "chance": 0.05, "fortune": 1, "silkTouch": "never" },
    { "item": "minecraft:emerald", "silkTouch": "only" },
    { "experience": "2-4", "chance": 0.5 }
  ]
}
```

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `block` | ja | Block-ID | | Der Block, den die Regel beobachtet |
| `properties` | nein | Objekt aus Eigenschaft zu Wert | keines | Nur die Zustände mit diesen Werten, etwa `{ "axis": "x" }` bei einem Stamm; ohne es jeder Zustand. Ein `meta` aus 1.12.2 wird nicht gelesen |
| `replace` | nein | boolean | `false` | Ob die üblichen Drops verworfen werden, bevor diese gewürfelt werden |
| `advancement` | nein | `namespace:pfad` | keins | Die Regel gilt nur für einen Spieler, der diesen Fortschritt hat, sodass derselbe Block vorher das eine und nachher das andere fallen lassen kann |
| `drops` | ja | Liste von Drops | | Jeder wird für sich gewürfelt, wenn der Block abgebaut wird |

Jeder Drop:

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `item` | ja, außer mit `experience` | Item-ID | | Was fällt |
| `experience` | nein | Zahl oder `niedrig-hoch` | | Statt eines Gegenstands so viel Erfahrung als Kugeln, gleichmäßig innerhalb der Spanne gewürfelt. `chance` und `silkTouch` gelten wie bei einem Gegenstand |
| `count` | nein | Zahl oder `min-max` | `1` | Wie viele, gleichmäßig innerhalb des Bereichs gewürfelt |
| `chance` | nein | float | `1.0` | Die Wahrscheinlichkeit, dass der Drop überhaupt fällt, `0.05` ist ein Abbau von zwanzig |
| `fortune` | nein | int | `0` | Bis zu so viele extra pro Stufe Glück auf dem Werkzeug |
| `silkTouch` | nein | `either`, `only` oder `never` | `either` | Ob der Drop einen Abbau mit Behutsamkeit braucht, einen ablehnt oder sich nicht darum kümmert. Das ist er, wenn ein Spieler mit einem Behutsamkeit-Werkzeug einen Block abbaut, der sich so ernten lässt, wie 1.12.2 es entschied: ein voller Block ohne Block-Entity, oder Glasscheiben, Eisengitter, Spinnweben und Endertruhen |

Regeln sehen jeden Abbau, bei dem der Block seine Beute fallen lässt, wie in 1.12.2: den durch einen Spieler, und Explosionen, Kolben, fließendes Wasser, Mobs und einen Pack-Mob, der sich durch den Block gräbt, die jede Regel ohne `advancement` würfeln. Dünnt eine Explosion die eigenen Drops des Blocks aus, übersteht jedes gewürfelte Item sie mit derselben Chance; Erfahrung wird nicht ausgedünnt. Mehrere Regeln für einen Block gelten alle, ein `replace` auf irgendeiner davon leert zuerst die üblichen Drops.

## Spielerbeute

*herstellung, beute und handel*

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

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
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

## Dorfbewohner und Handel

*herstellung, beute und handel*

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
| `jobSite` | nein | Blockname | keiner | Der Block, den ein Dorfbewohner beansprucht, um diesen Beruf zu ergreifen, so wie ein Schmiedetisch einen Werkzeugschmied macht. Ohne ihn vergibt kein Block den Beruf: Wie auf 1.12.2 bekommt ihn ein gespawnter oder gezüchteter Dorfbewohner per Zufall, behält ihn und füllt seine Angebote nie auf, weil er an keinem Block arbeitet |
| `workSound` | nein | Soundname | keiner | Was er beim Arbeiten an diesem Block abspielt |

Laufbahnen sind eine Idee aus 1.12.2, die das Spiel nicht mehr kennt: Ein Beruf ist ein Handelssatz, ein Pack mit zwei Laufbahnen liefert also zwei Dorfbewohner-Dateien. Wie der Dorfbewohner aussieht, ist eine gewöhnliche Textur, geliefert unter `assets/<namespace>/textures/entity/villager/profession/<name>.png` und `textures/entity/zombie_villager/profession/<name>.png`, genau dort, wo das Spiel seine eigenen hat. Ein Handel, der einen Vanilla-Beruf aus 1.12.2 zusammen mit seiner `career` nennt, etwa `minecraft:smith` mit `armor`, geht an den Beruf, zu dem diese Laufbahn geworden ist, hier `minecraft:armorer`. `texture` und `zombieTexture` einer 1.12.2-Datei, eine ganze Haut aus einem Pack, werden an diese beiden Pfade kopiert, wenn das Pack dort nichts hat, und die Haut wird über die eigene des Dorfbewohners gezeichnet.

### Handel

*dorfbewohner und handel*

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
| `profession` | ja | Berufsname | | Wessen Handel das ist. Ein Vanilla-Name aus 1.12.2 mit seiner `career` wird ebenfalls gelesen, siehe oben |
| `level` | nein | int | `1` | Auf welcher Handelsstufe er auftaucht, 1 bis 5. Eine höhere Stufe landet auf Stufe 5, der höchsten, die ein Dorfbewohner erreicht |
| `maxUses` | nein | int | `12` | Wie oft er genutzt werden kann, bevor er sperrt |
| `xp` | nein | int | `2` | Erfahrung, die der Dorfbewohner pro Handel auf seine nächste Stufe hin sammelt |

Ein Stapel ist `item` mit `min` (`1`) und `max` (`min`), ein fester Preis ist also einfach nur `min`.

---

# Kreaturen und Gefahren

## Entity-Varianten

*kreaturen und gefahren*

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
  "sounds": { "ambient": "entity.cow.ambient", "hurt": "entity.cow.hurt", "death": "entity.cow.death", "target": "mypack:scream", "targetVaries": 3, "explode": "mypack:boom", "throw": "mypack:whoosh" },
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

### Identität

*entity-varianten*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `entity` | ja | `namespace:name` | keine | Die Entity, auf der aufgebaut wird. Die jedes Mods, solange sie einen einfachen Welt-Konstruktor hat |
| `name` | nein | string | keiner | Der Name, den sie in der Welt, in Todesmeldungen und auf ihrem Ei trägt |
| `showName` | nein | boolean | `false` | Zeigt den Namen, ohne dass man sie ansieht |
| `profession` | nein | `namespace:name` | zufällig | Bei einem Dorfbewohner der Beruf, den er ausübt |
| `baby` | nein | boolean oder 0,0 bis 1,0 | `false` | Wie oft eines jung erscheint, und es bleibt dabei. `true` heißt immer, eine Zahl heißt dieser Anteil |
| `becomes` | nein | Liste | keine | Andere Varianten, zu denen dieses beim Erscheinen werden kann, nach Gewicht. Siehe unten |
| `egg` | nein | boolean oder Objekt | `true` | Ein Spawn-Ei, gefärbt wie das der kopierten Entity. `{ "primary": "AABBCC", "secondary": "112233" }` wählt eigene Farben, `false` lässt das Ei weg |
| `keepsBaseBaby` | nein | boolean | `false` | Ob der eigene Jung-Wurf der Basis zusätzlich läuft. Ohne ihn erscheint eine Variante auf Zombie-Basis nur so oft jung, wie `baby` sagt, ohne Kind aus dem eigenen Wurf des Zombies und ohne Hühnerjockey |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Variante bleibt weg, wenn nicht alle da sind |

Eine Variante ist eine eigene Klasse, eine Welt, die eine enthält, hängt also von dem Pack ab, das sie gemacht hat, genau wie von einem Mod. Nimm die Datei weg, und die Kreaturen in dieser Welt gehen mit.

**Ein Ei oder Spawner, der Verschiedenes liefert.** Eine Variante ist eine eigene Klasse, für sich allein erscheint sie also immer genau als das, was sie sagt. `becomes` bricht das auf: eine Liste von Varianten, zu denen diese beim Erscheinen werden kann, jede mit einem Gewicht, für jede Kreatur einzeln entschieden.

```json
{
  "becomes": [
    { "variant": "mypack:walker", "weight": 95 },
    { "variant": "mypack:little_walker", "weight": 5 }
  ]
}
```

Sich selbst zu nennen ist der Weg, so zu bleiben, wie man ist, und die Gewichte sind die Chancen. Setz das auf `mypack:walker`, und ein Ei und ein Spawn-Eintrag liefern meist Walker mit gelegentlich einem kleinen, so wie ein Zombie-Ei ab und zu ein Baby liefert. Es geschieht, während die Kreatur in die Welt kommt, gilt also für Eier, `/summon` und natürliches Spawnen gleichermaßen, und was ankommt, ist eine echte Kreatur der gewählten Variante mit allem, was diese Variante sagt. Ein Spawner ist strenger: Er würfelt nur unter den Varianten, die dieselbe Basis und dasselbe Team haben wie die Variante, auf die er eingestellt ist, ein Zombie-Spawner liefert also die Zombies dieses Teams und ihre Jungen und nie eine Kreatur anderer Art oder eines anderen Teams, so wie ein Vanilla-Zombie-Spawner ein Zombie-Spawner bleibt. Eine so erreichte Variante wandelt sich nicht noch einmal, zwei Varianten dürfen sich also gegenseitig nennen, ohne sich im Kreis zu drehen.

**Wo `baby` hineinpasst.** Das Spiel hat keinen eigenen Baby-Zombie: Es gibt einen Zombie, der beim Erscheinen auswürfelt, ob er ein Kind ist. `baby` sagt, wie oft, `"baby": 0.05` ist also die Vanilla-Gewohnheit und `"baby": true` heißt immer. Eine Variante bekommt den eigenen Wurf des Zombies nicht obendrauf, es taucht also kein Kind und kein Hühnerjockey auf, um das `baby` nicht gebeten hat; `keepsBaseBaby` gibt diesen Wurf zurück. Beide sind zwei Wege zur selben Sache, und welchen du nimmst, hängt vom Unterschied ab, den du willst: `baby` allein gibt eine Variante, die manchmal jung ist, `becomes` gibt mehrere Varianten, die sich in allem unterscheiden dürfen, und beides zusammen ist auch in Ordnung.

### Aussehen

*entity-varianten*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `texture` | nein | `namespace:textures/entity/<file>.png` | keine | Ein eigener Skin, genauso aufgeteilt wie der der kopierten Entity |
| `leftHanded` | nein | boolean | `false` | Hält ihre Waffe in der anderen Hand |
| `glowing` | nein | boolean | `false` | Durch Wände umrandet |
| `invisible` | nein | boolean | `false` | Wird nicht gezeichnet, ihre Ausrüstung aber schon |
| `scale` | nein | float | `1.0` | Wie groß sie gezeichnet wird und wie groß ihre Hitbox ist |
| `angryScale` | nein | float | `scale` | Die Größe, auf die sie anschwillt, solange sie ein Ziel hat, und noch drei Sekunden danach |
| `width` | nein | float | die der Basis | Breite ihrer Hitbox, bevor `scale` angewendet wird |
| `height` | nein | float | die der Basis | Höhe ihrer Hitbox, bevor `scale` angewendet wird |
| `bright` | nein | boolean | `false` | Wird voll beleuchtet gezeichnet, wie dunkel sie auch steht |
| `hideArmor` | nein | boolean | `false` | Trägt ihre Rüstung, ohne dass sie gezeichnet wird |
| `hideHeld` | nein | boolean | `false` | Dasselbe für das, was sie in der Hand hält |
| `tint` | nein | Hex-Farbe | keine | Färbt die Entity beim Zeichnen ein |
| `tintParts` | nein | Liste aus `body`, `armor`, `held` | `["body"]` | Welche Teile die Färbung erreicht |

`scale` ändert Modell und Hitbox auf beiden Seiten, du triffst also das, was du siehst. Eine Kreatur, die ihre Größe selbst ändert, ein Tier, das heranwächst, oder ein Zombie, der ein Kind ist, wird um die Größe herum skaliert, die sie sich gewählt hat, damit sich beides nicht in die Quere kommt. `angryScale` lässt sie anschwellen, solange sie ein Ziel hat, und bringt sie auf `scale` zurück, sobald sie es verliert. Da dem Client nie mitgeteilt wird, was eine Kreatur jagt, trägt das Sprint-Flag diese Nachricht hinüber; es wird bei einer Variante gesetzt, die `angryScale` nutzt, und sonst bei keiner, ein Mod, der bei deinen Varianten das Sprinten ausliest, sieht es also wechseln. In eine niedrige Decke hineinzuwachsen ist möglich, genauso wie bei einem wachsenden Schleim, halte den Unterschied also im Rahmen.

Eine `texture` wird anstelle der Textur eingebunden, die die Entity sonst nutzen würde, egal welchen Renderer sie erbt, sie funktioniert also bei Mod-Entities genauso wie bei Vanilla-Entities. Sie muss zu dem Modell passen, auf das sie gezeichnet wird, denn das Modell ist das der Basis-Entity: ein Skin, keine neue Form. Layer behalten ihre eigenen Texturen, Rüstung sieht auf einem umgeskinnten Zombie also weiter wie Rüstung aus.

Rüstung wird überhaupt nur auf einer Entity gezeichnet, deren Renderer einen Rüstungs-Layer hat, und das heißt: die humanoiden Mobs und die Dorfbewohner. Eine Variante einer Kuh oder einer Spinne kann Rüstung tragen und bekommt auch deren Schutz, nur zeichnet sie niemand, `armor` unter `attributes` ist deshalb meist der sauberere Weg, so eine Kreatur zäh zu machen. `hideArmor` ist für den anderen Fall: ein Humanoider, der die Rüstung in seinen Slots behalten soll, für den Schutz oder für einen Mod, der sie ausliest, ohne dass man sie sieht.

### Seine Geräusche

*entity-varianten*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `sounds` | nein | Objekt | die der Basis | `ambient`, `hurt` und `death`, jeweils ein registriertes Sound-Event, und ein 1.12.2-Name gilt weiterhin, siehe [Sound-Namen](#wertelisten). Drei weitere, für die die Basis keinen Laut hat: `target` wird einmal gespielt, sooft sie ein Ziel fasst, und `explode` ist der Klang ihrer Explosion, ob sie sich mit `explodes` selbst sprengt oder mit `throws` TNT wirft. `throw` spielt, sobald sie mit `throws` etwas wirft, anstelle des Schneeballwurfs oder, bei TNT, des Zischens der Lunte. `targetVaries` verschiebt jedes Abspielen von `target` zufällig um bis zu so viele Halbtöne nach oben oder unten, `3` also bis zu einer Vierteloktave in beide Richtungen; `0` spielt ihn unverändert. Auf dieser Version spielt das Spiel zusätzlich seinen eigenen Explosionsklang, weil ein 1.20.1-Client den Explosionsklang selbst wählt; auf 1.21.1 tritt `explode` an seine Stelle |
| `soundVolume` | nein | Zahl | `1.0` | Wie laut diese Sounds sind |
| `soundPitch` | nein | Zahl | `1.0` | Wie hoch sie klingen. Unter 1 tiefer, über 1 quietschiger |
| `silent` | nein | boolean | `false` | Macht keinen Laut |

### Gesundheit, Schaden und Effekte

*entity-varianten*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `immuneTo` | nein | Liste von Schadensarten | keine | Schaden, der an ihr abprallt, nach den Namen aus 1.12.2, `fall`, `drown`, `explosion`, `explosion.player`, `magic`, `indirectMagic`, `mob`, `player`, `inWall` und den übrigen, oder nach der ID einer Schadensart. Siehe [Schadensarten](#wertelisten) |
| `fallDamage` | nein | float | `1.0` | Multipliziert den Sturzschaden. `0` nimmt ihn ganz weg |
| `absorption` | nein | float | `0` | Zusätzliche Herzen über ihrer Gesundheit |
| `creatureAttribute` | nein | `undefined`, `undead`, `arthropod` oder `illager` | das der Basis | Als was sie zählt, damit Bann und Heiltränke sie entsprechend behandeln |
| `effects` | nein | Liste von Objekten | keine | Effekte, die sie immer hat: `{ "potion": "minecraft:strength", "amplifier": 1 }` |
| `fireproof` | nein | boolean | `false` | Fängt überhaupt nie Feuer, nimmt also keinen Schaden durch Feuer oder Lava und brennt nicht im Tageslicht |
| `invulnerable` | nein | boolean | `false` | Nimmt von nichts Schaden außer von der Leere und vom Kreativmodus |
| `attributes` | nein | Objekt | keines | `maxHealth`, `movementSpeed`, `attackDamage`, `attackSpeed`, `knockbackResistance`, `followRange`, `armor`. Ein Attribut, das die Entity normalerweise nicht hat, bekommt sie dazu. `attackSpeed` sind Schläge je Sekunde im Nahkampf, `1` wie im Spiel, `2` also doppelt so oft |
| `hurtResistance` | nein | int, Ticks | `20` des Spiels | Wie lange ein Treffer sie unverwundbar lässt. Niedriger lässt Schläge öfter landen, was ein Gedränge schneller tauschen lässt |
| `ignoresEffects` | nein | Liste von Effektnamen | keine | Effekte, die bei ihr nie greifen. `all` lehnt jeden ab; ein in `effects` genannter Effekt wird weiter angewandt |

### Bewegung

*entity-varianten*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `jumpMultiplier` | nein | float | `1.0` | Wie viel höher sie springt als die kopierte Entity |
| `maxFallHeight` | nein | int | der der Basis | Wie tief sie beim Wegfinden springt |
| `noAI` | nein | boolean | `false` | Steht da, wo sie hingesetzt wurde, und tut nichts |
| `leashable` | nein | boolean | `false` | Lässt sich an der Leine führen, auch wenn die kopierte Entity das nie konnte |
| `steerable` | nein | boolean | `false` | Lässt sich beim Reiten lenken |
| `pathPriorities` | nein | Objekt | keines | Wodurch sie läuft, als `WATER`, `LAVA`, `DANGER_FIRE`, `DOOR_WOOD_CLOSED` und die übrigen Pfadtypen des Spiels, jeweils eine Zahl, wobei negativ „nie“ heißt. Die 1.12.2-Namen `DANGER_CACTUS` und `DAMAGE_CACTUS` werden als `DANGER_OTHER` und `DAMAGE_OTHER` gelesen, unter denen diese Version Kakteen zusammen mit Süßbeerensträuchern führt |
| `stepHeight` | nein | float, Blöcke | wie die Basis | Wie hohe Stufen sie ohne Sprung hinaufgeht |
| `climbs` | nein | boolean | wie die Basis | An, klettert sie wie eine Spinne jede Wand hoch, gegen die sie läuft, egal welche Basis sie hat. Aus, klettert sie nirgends hoch, nicht einmal eine Leiter, und eine Spinne bleibt am Boden |
| `teleports` | nein | boolean | `true` | Bei einer Enderman- oder Shulker-Basis, ob sie sich überhaupt teleportieren darf |
| `walks` | nein | boolean | `false` | Ein Kaninchen läuft wie andere Tiere, statt zu hoppeln. Nur ein Kaninchen liest das |

### Wasser

*entity-varianten*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `breathesUnderwater` | nein | boolean | `false` | Ertrinkt nie und sinkt zu Boden, um dort zu laufen, statt zur Oberfläche zu schwimmen. Sie findet ihren Weg weiterhin über den Boden, tiefes Wasser, aus dem sie nicht herauslaufen kann, hält sie also fest |
| `swims` | nein | boolean | `false` | Bewegt sich durchs Wasser wie ein Tintenfisch oder ein Wächter und ertrinkt nie. Sie findet ihren Weg durch Wasser statt über Land, gehört also ins Wasser und ist außerhalb gestrandet |
| `amphibious` | nein | boolean | `false` | Läuft an Land und schwimmt richtig im Wasser und wechselt die Art der Wegfindung beim Hinein- und Hinausgehen. Sie ertrinkt nie. Was sie verfolgt hat, vergisst sie am Wasserrand, sie zögert also jedes Mal kurz beim Übergang |
| `waterSlowdown` | nein | float | `0.8` | Wie stark Wasser sie bremst. Höher ist schneller |

### Kampf

*entity-varianten*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `hostile` | nein | boolean | `false` | Greift an, was sie erreicht, und wehrt sich, wenn sie verletzt wird. Eine feindliche Variante zählt für das Spiel als Monster, welche Basis sie auch hat, das Monsterlimit hält sie also. Friedlich räumt sie aber nur weg, wenn ihre Basis ein Monster ist; jede andere Basis bleibt und kann einem Spieler dort nichts anhaben. Sie legt die Tieraufgaben ihrer Basis ab, Paaren, Anlocken, einem Elternteil, einem Besitzer oder Artgenossen folgen, Sitzen |
| `targets` | nein | Liste von Entity-Namen | der Spieler | Wonach sie sucht, solange sie feindselig ist. `minecraft:player` wird verstanden, obwohl der Spieler keine registrierte Entity ist |
| `attackReach` | nein | float, Blöcke | ihre Größe | Wie weit ein Nahkampfschlag reicht. Das Spiel reicht die doppelte Breite, weshalb eine vergrößerte Kreatur von weiter weg trifft; das hier setzt es direkt |
| `knockback` | nein | float | wie die Basis, `0.4` | Wie hart ihre Schläge stoßen. `0` stößt gar nicht |
| `hitEffects` | nein | boolean | `true` | Ob sie dem, was sie trifft, den Effekt der kopierten Entity anhängt: das Verdorren eines Witherskeletts, das Gift einer Höhlenspinne, den Hunger eines Husks. Aus trifft sie nur mit Schaden |
| `hitFire` | nein | boolean | `true` | Ob ihre Schläge entzünden, was die kopierte Entity entzünden würde. Aus tun sie es nie |
| `passive` | nein | boolean | `false` | Hält sie davon ab, irgendetwas anzugreifen, egal wie sie sich sonst verhält |
| `threatLeast` | nein | int | `0` | Die niedrigste Bedrohungsstufe, in der ein Spieler oder anderer Träger im Umkreis von 128 Blöcken stehen muss, bevor die Variante natürlich spawnt. `0` spawnt wie gewohnt |
| `threatHostile` | nein | int | `0` | Die niedrigste Bedrohungsstufe, in der ein Spieler stehen muss, bevor die Variante von sich aus auf ihn losgeht. Darunter ist die Variante diesem Spieler gegenüber friedlich, wehrt sich aber weiterhin, wenn sie getroffen wird. `0` greift wie gewohnt an |

`hostile` nimmt der Kreatur auch das Verhalten weg, das sie hat weglaufen lassen: Ein Tier, das Spielern ausgewichen ist oder bei Verletzung in Panik geriet, tut beides nicht mehr, sobald es feindselig ist, sonst würde es vor dem fliehen, was es eigentlich angreifen soll. Es braucht eine Entity, die auf dem Boden läuft, weil es dasselbe Angriffsverhalten nutzt, das Vanilla seinen eigenen Mobs gibt. Eine fliegende oder schwimmende Basis wird protokolliert und in Ruhe gelassen. `passive` greift weiter, erreicht aber nur Verhalten, das so gebaut ist, wie Vanilla es baut: Einem Mod, dessen Feindseligkeit in seinem eigenen Tick- oder Schadenscode steht, kann ein Pack sie nicht ausreden.

### Ausrüstung, Beute und Erfahrung

*entity-varianten*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `lootTable` | nein | `namespace:entities/<name>` | die der Basis | Was sie droppt. Ohne diesen Schlüssel droppt sie, was die kopierte Entity droppt |
| `experience` | nein | int | die der Basis | Wie viel Erfahrung sie droppt |
| `collectsExperience` | nein | boolean | `false` | Sammelt Erfahrung wie ein Spieler: Kugeln im Umkreis von acht Blöcken treiben zu ihm und werden bei Berührung genommen, Reparatur auf seiner Ausrüstung wird zuerst bedient, und die Punkte bauen Stufen auf der Kurve des Spielers auf, gespeichert am Mob über einen Speicherstand hinweg. Was es tötet, lässt seine Erfahrung fallen, als hätte ein Spieler getötet, ein Block, den seine `digs`-Aufgabe bricht, lässt dessen eigene Erfahrung fallen, und ein Erfahrungswurf aus `block_drops` fällt ebenfalls für es. Beim Tod lässt es sieben pro Stufe fallen, höchstens hundert, außer `keepInventory` ist an. Ziele mit dem Kriterium `xp` oder `level` führen seine Summe und Stufe in einer Zeile unter seiner UUID, sodass eine Funktion sie mit `execute if score` oder einem Selektor `scores={<Ziel>=N..}` liest. Es gibt seine Stufen für Ambosswerk aus wie ein Spieler, siehe [Ambosswerk](#ambosswerk) |
| `dropChance` | nein | 0 bis 1 | `0` | Wie wahrscheinlich jedes Ausrüstungsstück droppt |
| `picksUpLoot` | nein | boolean | `false` | Hebt auf, worüber sie läuft |
| `equipment` | nein | Objekt | keines | `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`, jeweils ein Itemname |

Eine Variante droppt das, was die kopierte Entity droppt, weil die Beutetabelle im Code dieser Entity festgeschrieben ist und nicht über den Namen nachgeschlagen wird. `lootTable` zeigt auf eine eigene Tabelle, die du dann wie jede andere unter `loot_tables/entities/<name>.json` mitlieferst.

### Besondere Verhaltensweisen

*entity-varianten*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `digs` | nein | boolean | `false` | Gräbt sich Block für Block zu einem unerreichbaren Ziel, mit dem Werkzeug in der Hand. Braucht `mobGriefing` und ein passendes Werkzeug |
| `throws` | nein | boolean | `false` | Wirft aus der Entfernung, was es in der Hand hält, und zündet es an und zieht sich zurück, wenn das TNT ist. Braucht `hostile` |
| `throwAmmo` | nein | int | keine | Wie viele es zu werfen hat. Weggelassen geht ihm nie etwas aus |
| `throwReload` | nein | int, Sekunden | `explosionFuse` | Wie lange die Hand leer bleibt, bis es das nächste zieht |
| `throwRetreat` | nein | int, Sekunden | `explosionFuse` | Wie lange es nach einem Wurf auf Abstand bleibt, ehe es sich wieder umdreht |
| `throwPower` | nein | float | `1.0` | Wie kräftig es wirft. Verdoppeln verdoppelt ungefähr die Weite |
| `throwArc` | nein | float | `0.35` | Wie steil der Wurfbogen ausfällt. Höher hängt länger, nahe null ist ein flacher Wurf, unter null wirft es nach unten |
| `throwReturns` | nein | boolean | `false` | Was es wirft, fliegt wie ein Dreizack: Es trifft mit dem `attackDamage` der Variante, bei einer Basis ohne diesen Wert mit 8, und fliegt dann zurück in seine Hand, so wie Treue einen Dreizack zurückbringt. Es wird nie verbraucht und zielt auf das Ziel, wie ein Skelett zielt, schneller mit `throwPower` und mit weniger Streuung auf schwereren Schwierigkeitsgraden, und der Werfer bleibt stehen, solange es fliegt, also gelten `throwAmmo`, `throwReload`, `throwRetreat` und `throwArc` dafür nicht. TNT wird geworfen wie immer |
| `explodes` | nein | boolean | `false` | Sprengt sich neben ihrem Ziel in die Luft, wie ein Creeper. Braucht `hostile` |
| `explosionPower` | nein | Zahl | `3.0` | Wie groß die Explosion ist. Ein Creeper ist 3, TNT ist 4. Bei einer Creeper- oder Ghast-Basis setzt dieser Schlüssel oder `explosionFuse` auch ohne `explodes` die eigene Explosion der Basis: Größe und Zündzeit eines Creepers, den Feuerball eines Ghasts, jeweils in ganzen Zahlen, ein Ghast mit nur `explosionFuse` explodiert also mit 3 statt mit seiner eigenen 1 |
| `explosionFuse` | nein | int, Ticks | `30` | Wie lange sie zischt, bevor es losgeht, bei einer Creeper-Basis auch dessen eigene Zündzeit |
| `explosionFire` | nein | boolean | `false` | Lässt Feuer zurück |
| `charges` | nein | boolean | `false` | Stürmt aus der Entfernung auf ihr Ziel los und trifft beim Aufprall mit kräftigem Rückstoß, wie ein Verwüster, und ruht dann vor dem nächsten Anlauf. Braucht `hostile` |
| `pounces` | nein | boolean | `false` | Duckt sich, springt dann im Bogen auf ihr Ziel und schlägt beim Aufsetzen zu, wie ein Fuchs. Braucht `hostile` |
| `sniffs` | nein | int, Blöcke | `0` | Hört Spieler, die sich innerhalb so vieler Blöcke bewegen, durch Wände hindurch, und geht dorthin, wo sie sie gehört hat; ein schleichender oder stehender Spieler wird nicht gehört, und einen, den sie dann sieht, nimmt sie ins Ziel. `0` hört nicht. Braucht `hostile` |
| `fleesWhenHurt` | nein | 0,0 bis 1,0 | `0` | Bricht ab und läuft vor dem davon, mit dem sie kämpft, solange ihre Gesundheit unter diesem Anteil liegt, und kehrt zurück, sobald sie darüber ist. `0` flieht nie. Braucht `hostile` |
| `sleepsByDay` | nein | boolean | `false` | Sucht bei Tag Schatten und steht dort still bis zur Nacht oder bis etwas sie angreift. Während sie ruht, liegt sie auf der Seite |
| `home` | nein | int, Blöcke | `0` | Bleibt in so vielen Blöcken um die Stelle, an der sie zuerst stand, streift darin umher und geht zurück, wenn sie sich verläuft. `0` streift frei |
| `patrols` | nein | boolean | `false` | Zieht in langen Etappen über das Land, mit anderen ihrer Art, die einem Anführer folgen, wie eine Plünderer-Patrouille. Eine Gruppe, die zusammen erscheint, wählt einen Anführer; die anderen bleiben wenige Blöcke bei ihm, und nimmt der Anführer ein Ziel, nehmen es alle. Ein Gefolgsmann, der seinen Anführer verliert, übernimmt selbst die Führung. Braucht `hostile` |
| `swoops` | nein | boolean | `false` | Kreist über ihrem Ziel und stürzt hindurch, schlägt im Vorbeiflug zu, wie ein Phantom. Die Variante bekommt eine Flughilfe, fliegt also, solange sie jagt, und lässt sich im Leerlauf zu Boden; sie braucht eine Basis, die eine Kreatur ist, etwa einen Papagei, und eine Fledermaus ist keine. Braucht `hostile` |
| `gusts` | nein | boolean | `false` | Holt aus und lässt aus der Entfernung einen Windstoß auf ihr Ziel los, der alles nahe dem Ziel zurück und nach oben wirft, wie die Windkugel einer Brise. Braucht `hostile` |
| `gustPower` | nein | float | `1.5` | Wie hart ein Windstoß wirft. Ein Treffer eines Mobs ist 0,4, eine starke Rückstoß-Verzauberung etwa 1 |

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

### Aufgaben

*entity-varianten*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `tasks` | nein | Liste | keine | Jede Aufgabe, die das Spiel kennt, der Variante beim Namen und mit einer Priorität deiner Wahl hinzugefügt oder aus dem entfernt, was ihre Basis mitbringt. Die Liste unten |

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
| `lookAtVillager` | einen Eisengolem | `tasks` |  | Hält einem Dorfbewohner ab und zu eine Mohnblume hin und sieht ihn dabei an |
| `lookIdle` | jede Basis | `tasks` |  | Sieht sich ab und zu um |
| `mate` | ein Tier | `tasks` | `speed`, `entity` | Paart sich, wenn verliebt, mit ihresgleichen oder der genannten `entity` |
| `moveIndoors` | eine gehende Kreatur | `tasks` |  | Geht bei Einbruch der Nacht in ein Dorfhaus |
| `moveThroughVillage` | eine gehende Kreatur | `tasks` | `speed`, `nocturnal` | Geht die Dorfwege von Tür zu Tür |
| `moveTowardsRestriction` | eine gehende Kreatur | `tasks` | `speed` | Geht zurück zu ihrem Heimatpunkt, wenn sie sich entfernt |
| `moveTowardsTarget` | eine gehende Kreatur | `tasks` | `speed`, `distance` | Rückt an ein weit entferntes Ziel heran |
| `nearestAttackableTarget` | eine gehende Kreatur | `targets` | `entity`, `sight`, `nearby` | Nimmt die nächste der genannten Entity ins Visier |
| `ocelotAttack` | jede Basis | `tasks` |  | Das Anschleichen und Anspringen der Katze |
| `ocelotSit` | eine Katze | `tasks` | `speed` | Setzt sich auf Truhen, Betten und brennende Öfen. Aus gezähmten Ozelots wurden Katzen, daher braucht dies eine Katzen-Basis |
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

### Spawnen und Verschwinden

*entity-varianten*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `despawns` | nein | boolean | `true` | Aus bleibt sie, auch wenn sie sonst entfernt würde |
| `despawnAfter` | nein | int, Sekunden | keine | Sie verschwindet still, sobald sie so lange in der Welt war, ganz gleich wie weit jemand entfernt ist |
| `persistent` | nein | boolean | `false` | Despawnt nie |
| `ignoresSpawnRules` | nein | boolean | `false` | Spawnt überall, wo sie hingesetzt wird, und ignoriert die geerbten Regeln |
| `spawns` | nein | Liste von Objekten | keine | `creatureType`, `weight`, `min` und `max`, dieselbe Form, die ein Biom nutzt. `creatureType` ist einer der [Kreaturtypen](#wertelisten), ohne Angabe `creature`, und bestimmt, in welche Spawnliste der Eintrag kommt; ein Eintrag mit einem Typ, den das Spiel nicht kennt, fügt nichts hinzu |
| `biomes` | nein | Liste von Biomnamen | jedes Biom | Wo diese Spawns hinzugefügt werden, nach Biom-ID oder nach dem Namen, den 1.12.2 für ein Vanilla-Biom anzeigte, etwa `Extreme Hills`. Ohne diese Liste und ohne `biomeTypes` bekommt jedes Biom sie, Nether und End eingeschlossen, und ein Biom, auf das beide Listen passen, bekommt jeden Spawn nur einmal |
| `biomeTypes` | nein | Liste von Biomtypen | keine | Dasselbe, aber nach Typwort |

**Eine Kreatur mit Haltbarkeitsdatum.** `despawnAfter` zählt in Sekunden ab dem Moment, in dem eine Kreatur zum ersten Mal in die Welt kommt, und nimmt sie still fort, wenn die Zeit um ist: kein Tod, kein Drop, kein Geräusch, genau als wäre sie weggewandert und weggeräumt worden. Die Uhr wird in die Kreatur selbst geschrieben, sie läuft also über Speichern und Laden hinweg weiter, statt jedes Mal neu zu beginnen, wenn ein Chunk zurückkommt.

Sie ist eine Sache für sich und kein Anstupsen der Regeln, über die `despawns` und `persistent` bestimmen. Die beiden entscheiden, ob das Spiel eine Kreatur wegräumen darf, weil sie weit von allen entfernt ist; diese hier ist ein Versprechen, dass sie zu einer festen Zeit geht, ganz gleich was sonst gilt. Eine Kreatur darf `persistent` sein und trotzdem ein Haltbarkeitsdatum haben, genau das willst du für etwas, das für einen Kampf oder ein Ereignis gerufen wurde und es nicht überdauern soll.

Die Uhr läuft nach der Weltzeit, sie pausiert also, wenn niemand spielt, und zählt die Minuten nicht mit, die ein Chunk entladen verbracht hat.

### Netzwerk

*entity-varianten*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `trackingRange` | nein | int | `80` | Aus welcher Entfernung der Client von ihr erfährt |
| `trackVelocity` | nein | boolean | `true` | Schickt neben der Position auch die Geschwindigkeit. Aus spart Traffic bei Dingen, die sich kaum bewegen |
| `trackingFrequency` | nein | int | `3` | Wie oft, in Ticks |

## Expositionen

*kreaturen und gefahren*

`<namespace>/exposures/*.json`

Der Pfad der Datei ist der Name der Gefahr, und ihre Todesmeldung kommt aus dem Sprachschlüssel `death.attack.rdpl.<Dateiname>`. Expositionen werden nur geladen, solange `load` an und `vanillaClients` aus ist.

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
| `items` | eines von beiden | Liste aus `item` oder `item=stufe` | | Items, die einen Spieler belasten, der sie trägt |
| `levels` | ja | Liste von Stufen | | Die Schwereleiter, der erste Eintrag ist Stufe 1. Ein Spieler bekommt die höchste erreichte Stufe |
| `immunity` | nein | Trankname | keine | Ein Effekt, dessen Träger gar nicht belastet wird |
| `scanInterval` | nein | Ticks | `20` | Wie oft Umgebung und Inventar geprüft werden |
| `range` | nein | Blöcke | `10` | Wie weit ein Block wirkt, als Kugel |
| `sourcesForNextLevel` | nein | int | `0` | So viele Quellen einer Stufe in der Nähe heben sie um eine weitere an. `0` schaltet das ab |
| `skipsCreative` | nein | boolean | `true` | Kreativ- und Zuschauerspieler bleiben verschont |

### Stufen

*expositionen*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `effect` | ja | Trankname | | Der Effekt, der die Stufe am Spieler markiert. Seine Anwesenheit steuert den Schaden, es sollte also einer sein, den das Pack dafür definiert |
| `damage` | nein | halbe Herzen | `0` | Schaden alle `damageInterval` Ticks, solange die Stufe anliegt. Er ignoriert Rüstung |
| `damageInterval` | nein | Ticks | `160` | Wie oft der Schaden fällt |
| `effects` | nein | Liste von Effekten | keine | Zusätzliche Effekte, gleiche Form wie bei Trankarten. Ohne `duration` folgen sie dem Prüfintervall |

Die Stufeneffekte halten etwas über die nächste Prüfung hinaus, Weggehen lässt sie also von selbst auslaufen. Der Tod durch den Schaden liest seine Meldung aus `death.attack.rdpl.<dateiname>`, die die Sprachdateien des Packs liefern.

---

# Die Welt

## Weltvorlagen

*die welt*

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
| `default` | nein | Biomname oder `void` | `void` | Was ein Biom füllt, das die Sperre entfernt hat. `void` lässt dort das Leere-Biom. Ein nicht registriertes Biom wird protokolliert, und die Leere steht an seiner Stelle. `fallback` ist derselbe Schlüssel unter anderem Namen |
| `roles` | nein | Objekt aus Rolle zu Biom | keines | Biome, die bestimmte Rollen füllen: `ocean`, `river`, `beach`, `mushroom`, `swamp`, `hills`, `mountain`, `jungle`, `forest`, `savanna`, `sandy`, `mesa`, `snowy`, `wasteland`, `plains` und `water`, in genau dieser Reihenfolge geprüft, egal in welcher sie in der Datei stehen; ein gesperrtes Biom, das zugleich Ozean und verschneit ist, nimmt also die Ozean-Rolle. Eine Rolle, die `void` oder ein nicht registriertes Biom nennt, fällt an die nächste weiter. Rollen gelten nur in den `dimensions` der Vorlage; anderswo wird ein gesperrtes Biom zur Leere |
| `structures` | nein | Objekt aus [Strukturname](#wertelisten) zu boolean | keines | Vanilla-Strukturen, ein- oder ausgeschaltet |
| `settings` | nein | Objekt | keines | Config-Werte, die die Vorlage setzt |
| `dimensions` | nein | Liste von Dimensions-Ids | jede Dimension | Für welche Dimensionen sie gilt |
| `requires` | nein | Liste von Mod-Ids oder Pack-Namespacesn | keine | Die Vorlage wird übersprungen, wenn nicht alle da sind |

`settings` nutzt dieselben Schlüsselnamen wie die Config, es gibt also keine Übersetzungstabelle zu lernen.

Welche Vorlage aktiv ist, entscheidet die Config-Option `worldTemplate`. Steht sie auf `auto`, gewinnt das Pack mit der höchsten Priorität, das eine mitbringt, in derselben Reihenfolge, der alles andere auch folgt; bringen mehrere Packs eine Vorlage mit, nennt das Log sie alle und die, die gilt, denn die übrigen bewirken nichts, samt ihren Einstellungen. Nennst du dort eine Vorlage, ist sie gesetzt. Fünf sind eingebaut und lassen sich so nennen: `void`, `vanilla` (Ozeane, Flüsse, Strände, Pilzfelder, Sümpfe und Hügel bleiben die des Spiels, überall sonst Ebenen), `ocean` (Flüsse und Strände bleiben, überall sonst Ozean), `plains` und `desert`. `auto` wählt nie eine eingebaute.

**Ein Biom kann anders bauen.** Ein `biomes`-Objekt in `settings` hält eigene Dorf-Einstellungen für ein benanntes Biom, sodass ein Wüstendorf Sandsteinstraßen legt, wo ein Ebenendorf Beton legt, ohne dass eines von beiden ein eigenes Pack wäre. Nenne ein Biom über seine Id, `minecraft:desert`, über eines der Typwörter, die dieser Mod auf Biom-Tags abbildet (`sandy`, `snowy`, `desert`, `forest`, `jungle`, `mountain`, `ocean`, `swamp`, `hot`, `cold` und die übrigen), oder über einen ausgeschriebenen Tag, `#minecraft:is_forest`; eine genaue Id wird vor den Typen angesehen, sodass eine allgemeine Regel für ein Biom übersteuert werden kann. Alles, was in einem Abschnitt nicht genannt ist, fällt auf die einfache Einstellung darüber zurück.

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

Jede Blockeinstellung, die eine Straße, eine Brücke, eine Bahn, eine U-Bahn, eine Station oder ein Abwassersystem nimmt, antwortet darauf, und die gewichtete Mischschreibweise funktioniert in einem Abschnitt wie außerhalb. Das Biom wird beim Bauen eines Teilstücks gelesen, und die Blöcke werden überall dort neu genommen, wo der Boden das Biom wechselt, sodass eine Straße oder eine Bahn, die aus einer Wüste hinausführt, genau an der Grenze das Material wechselt. Eine Logzeile beim Laden der Welt sagt, wie viele Abschnitte ein Pack mitbringt, und nennt sie; mit Debug sagt jedes Biom, welchen Abschnitt es genommen hat, oder dass es keinen genommen hat und worauf es geantwortet hätte.

## Spielregeln

*die welt*

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

Jeder Schlüssel ist die Id der Welt, zu der die Regeln gehören, `minecraft:overworld`, `minecraft:the_nether`, `minecraft:the_end`, eine packeigene oder was ein Mod nutzt; die Zahlen `0`, `-1` und `1` aus 1.12.2 werden weiterhin als die drei Vanilla-Welten verstanden. Werte sind Strings, so wie im Befehl `/gamerule`, also `"false"` statt `false`. Sie werden auf neue Welten angewendet. Eine Regel, die eine Datei auslässt, gilt in dieser Welt mit dem Standard des Spiels, nicht mit dem Wert, den der Rest des Spielstands nutzt, und ein Client mit dem Pack liest dieselben Regeln. Eine Dimensionsdatei trägt dieselben Regeln stattdessen in einem `gameRules`-Block, der immer nur für diese eine Welt gilt.

## Biome

*die welt*

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

### Das Biom

*biome*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `name` | nein | string | der Dateiname | Name, den der Spieler sieht |
| `types` | nein | Liste von Biomtypen | geschätzt | Schreibt das Biom in die Tags, für die diese Typwörter stehen, etwa `forest`, `cold`, `wet` oder `nether`, damit andere Mods es finden. Weggelassen werden die Typen aus dem Biom geschätzt, so wie das Spiel sie schätzte: `forest` oder `jungle` ab drei Bäumen, sonst `plains`, `hot`, `cold`, `wet` und `dry` aus Temperatur und Niederschlag, `sparse` oder `dense` aus der Baumzahl, `snowy` aus `snow` und `sandy`, `mushroom` oder `mesa` aus einem Boden aus Sand, Myzel oder Terrakotta |
| `baseBiome` | nein | Biomname | `minecraft:plains` | Ein vorhandenes Biom, von dem Einstellungen kopiert werden. Eines, das weder das Spiel noch eine Mod mitbringt, wird protokolliert, und die Ebene wird genommen |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

Ein Biom ist in dieser Version ein Datenpaket-Eintrag, der für dich unter `worldgen/biome/` geschrieben wird, und das Gelände darunter gehört den Noise-Einstellungen und nicht dem Biom, weshalb es kein `baseHeight` und kein `heightVariation` gibt: Die Form des Landes ergibt sich daraus, wo das Klima das Biom hinsetzt, genau wie bei den Biomen des Spiels. Eine `id` aus 1.12.2 wird gelesen und ignoriert.

### Klima

*biome*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `temperature` | nein | float | `0.5` | Unter 0.15 schneit es, über 1.0 ist es wüstenheiß |
| `rainfall` | nein | float, 0 bis 1 | `0.5` | Wie feucht es ist |
| `rain` | nein | boolean | `true` | Ob es überhaupt Wetter gibt |
| `snow` | nein | boolean | `false` | Ob Regen als Schnee fällt. Schnee fällt nur, wo die Temperatur unter 0.15 liegt, und das hier ändert die Temperatur nicht, in einem wärmeren Biom regnet es also weiter |

### Boden und Farben

*biome*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `topBlock` | nein | Blockname | Gras | Der Oberflächenblock |
| `fillerBlock` | nein | Blockname | Erde | Direkt unter der Oberfläche |
| `stoneBlock` | nein | Blockname | Stein | Die Masse des Untergrunds |
| `waterColor` | nein | Hex-Farbe | `FFFFFF` | Wasserfärbung |
| `grassColor` | nein | Hex-Farbe | aus dem Klima | Grasfärbung, anstelle der Farbe, die Temperatur und Niederschlag ergäben |
| `foliageColor` | nein | Hex-Farbe | aus dem Klima | Laubfärbung, auf dieselbe Weise |

### Dekoration und Spawns

*biome*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `decoration` | nein | Objekt | die des Basisbioms | Anzahlen pro Chunk, die ändern, was das Basisbiom ohnehin setzt. Gelesen werden `trees`, `flowers`, `grass`, `deadbush`, `mushrooms`, `bigmushrooms`, `reeds`, `cacti`, `sand`, `gravel`, `clay` und `waterlily`, dazu die Schalter `falls` (Seen und Quellen), `pumpkins`, `desertwells`, `ice` (Eisstacheln und Eisflächen), `fossils` und `rocks` (Felsbrocken im Wald), wobei jeder Wert über null die eigene Rate des Basisbioms beibehält und null oder weniger das jeweilige Element entfernt, und `extratreechance`, eine prozentuale Chance auf einen Baum mehr, wobei `0` auch den Extrabaum wegnimmt, den das Basisbiom auswürfelt. Eine Anzahl für eine Sorte, die das Basisbiom nicht setzt, fügt nichts hinzu; schreib dafür einen Worldgen-Eintrag. Jeder andere Name wird protokolliert und ignoriert |
| `spawns` | nein | Liste von Objekten | Vanilla-Liste | Siehe unten |
| `keepDefaultSpawns` | nein | boolean | `false` | Vanillas Liste neben deiner behalten |
| `spawnChance` | nein | float, unter 1 | `0.1` | Wie wahrscheinlich beim ersten Erzeugen des Landes eine weitere Herde gesetzt wird. Das Spiel würfelt weiter, solange es Erfolg hat, `1` hört also nie auf und füllt die Welt, bis kein Platz mehr ist. Alles ab 0.99 wird abgelehnt und durch 0.99 ersetzt |
| `spawnRates` | nein | Objekt aus `surfaceDay`, `surfaceNight`, `undergroundDay`, `undergroundNight` zu einem Faktor | keines | Wie oft feindliche Mobs hier spawnen, anstelle der globalen Einstellungen. Siehe unten |

Ein Spawn-Eintrag nimmt `entity` (Pflicht), `type` (`creature`, einer von `monster`, `creature`, `ambient` oder `water`, die Unterstriche in einem Namen wie `water_creature` nach Belieben), `weight` (`10`), `min` (`1`) und `max` (`min`).

Bei `spawnRates` geht es ausschließlich um feindliche Mobs, um sonst nichts. Es nimmt vier Schlüssel und keine anderen: `surfaceDay` und `surfaceNight` für Orte, an denen der Himmel zu sehen ist, `undergroundDay` und `undergroundNight` für Orte, an denen er es nicht ist. Jeder ist ein Faktor darauf, wie oft ein feindlicher Mob erscheinen darf: `1` ist die gewöhnliche Rate, `0` unterbindet sie ganz, unter 1 weist einen Teil der Versuche ab, und über 1 lässt Versuche durch, die das Spiel sonst abgelehnt hätte, `2` sind also doppelt so viele. Ein weggelassener Schlüssel heißt, dass das Biom nicht entscheidet, und es gilt die globale Einstellung für diese Zeit und diesen Ort. Alles andere, was hier steht, ist kein Schlüssel und wird ignoriert, eine Rate, die nach einem Kreaturtyp benannt ist, tut also überhaupt nichts.

### Wo es generiert

*biome*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `placement` | nein | Objekt | keines | Wo es generiert. Siehe unten |
| `villageType` | nein | `oak`, `sandstone`, `acacia` oder `spruce` | keiner | Woraus ein Dorf hier gebaut wird: das Ebenen-, Wüsten-, Savannen- oder Taigadorf. Leer baut das Ebenendorf, wie auch ohne den Schlüssel |

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `climate` | nein | `icy`, `cool`, `medium`, `warm` oder `desert` | keiner | Welchem Klimaband es beitritt, denselben fünf, nach denen die eigenen Biome der Oberwelt verteilt werden. Weggelassen, mit einem `weight` von 0 oder mit einem hier nicht aufgeführten Klima wird das Biom registriert, aber nie gesetzt, es sei denn, die `roles` einer Vorlage, das `biome` einer Dimension oder ein Höhenband verlangen es |
| `weight` | nein | int | `10` | Wie oft es gegenüber seinen Nachbarn in diesem Band gezogen wird |
| `villages` | nein | boolean | `false` | Dörfer dürfen generieren |
| `strongholds` | nein | boolean | `false` | Festungen dürfen generieren |
| `playerSpawn` | nein | boolean | `false` | Der Weltspawn darf hier liegen |

### Höhenbänder

*biome*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `minHeight` | nein | int | keiner | Unterste y, ab der dieses Biom als 3D-Biom übernimmt. Wird eine der beiden Höhen gesetzt, wird das Biom zu einem Band: außerhalb behält die Säule ihr eigenes Biom, innerhalb meldet jede 4 mal 4 mal 4 große Zelle der Welt dieses |
| `maxHeight` | nein | int | keiner | Oberste y dieses Bandes |
| `replaces` | nein | Liste von Biomnamen | jedes Biom | Beschränkt das Band auf Säulen, deren eigenes Biom hier genannt ist, ein Alpenband kann also über Bergen liegen und sonst nirgends. Das eigene Biom einer Säule ist das an ihrer Oberfläche. Ohne `minHeight` oder `maxHeight` bewirkt es nichts |

### Temperatur nach Höhe

*biome*

**Temperatur nach Höhe.** Ein Biom kühlt mit der Höhe ab, was den Schnee auf Berggipfel bringt und Regen oberhalb einer Linie beendet. Drei Schlüssel der Gruppe `terrain` verschieben diese Kurve, was in einer Dimension zählt, deren Boden weit über oder unter der Höhe liegt, die das Spiel annimmt. Ohne sie bleibt die spieleigene Kurve, ein Pack, das sie in Ruhe lässt, ändert also nichts.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "biomeTemperatureCenterY": 80,
    "biomeTemperatureHeightFactor": -0.00125,
    "biomeTemperatureScaleMaxY": 320
  }
}
```

| Schlüssel | Wert | Standard | Was er macht |
| --- | --- | --- | --- |
| `biomeTemperatureCenterY` | int | `80` | Die Höhe, ab der die Kurve gemessen wird. Auf ihr und darunter meldet ein Biom seine eigene `temperature` unverändert |
| `biomeTemperatureHeightFactor` | float | `-0.00125` | Wie stark sich die Temperatur je Block oberhalb dieser Höhe verschiebt, die spieleigenen 0,05 über 40 Blöcke. Negativ kühlt mit der Höhe ab, positiv wärmt |
| `biomeTemperatureScaleMaxY` | int | keiner | Die Höhe, bei der die Kurve endet, damit eine Welt, die höher ist als die des Spiels, nicht bis zur Decke weiter abkühlt. Ohne sie läuft die Kurve bis zum oberen Rand der Welt |

## Dimensionen

*die welt*

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

### Oberste Ebene

*dimensionen*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `gameRules` | nein | Objekt | keines | Regeln, die nur hier gelten |
| `portal` | nein | Objekt | keines | Ein Rahmen, der diese Dimension öffnet. Siehe [Eine Dimension über einen Rahmen öffnen](#eine-dimension-über-einen-rahmen-öffnen) |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Datei wird übersprungen, wenn nicht alle da sind |

Eine Dimension ist in dieser Version ein Datenpaket-Eintrag: Der Dimensionstyp und die Noise-Einstellungen werden für dich unter dem Namespace des Packs geschrieben, ein Vanilla-Client erfährt also beim Beitreten von ihr und reist wie in jede andere dorthin. Die Dimension behält ihren eigenen Speicherordner unter der Welt, benannt nach ihrer Id, und ist geladen, solange jemand darin ist oder ein `forceload` einen Chunk hält.

### Der Block `terrain`

*dimensionen*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `type` | nein | `overworld`, `flat`, `void`, `nether`, `end` | `overworld` | Welcher Generator des Spiels sie baut, mit seinen Noise-Einstellungen kopiert und durch die Schlüssel unten geändert |
| `minHeight` | nein | int, ein Vielfaches von 16 | der des Typs | Der Boden der Dimension. Tiefer als der des Typs macht eine Tiefenwelt unter dem Gelände, siehe [Die Tiefenwelt](#die-tiefenwelt) |
| `maxHeight` | nein | int, ein Vielfaches von 16 | der des Typs | Der Block über ihrer Decke |
| `generatorOptions` | nein | Objekt, Text oder Liste | keiner | Für `overworld` und die anderen ein Objekt, oder dessen Text, wie 1.12.2 ihn schrieb, aus `seaLevel`, `useLavaOceans` sowie `useCaves`, `useRavines`, `useDungeons`, `useLavaLakes`, `useStrongholds`, `useVillages`, `useMineShafts`, `useTemples`, `useMonuments` und `useMansions`, auf false gesetzt, um diese in der Dimension wegzulassen. Für `flat` die Schichten von unten nach oben, als `"minecraft:bedrock"`, `"59*minecraft:stone"`, `"3*minecraft:dirt"`, `"minecraft:grass_block"`, was zugleich der Standardboden ist, oder der Superflach-Text von 1.12.2, dessen Biomnummer das Biom setzt und dessen `decoration`, `lava_lake` und Strukturnamen so gelesen werden wie beim `generatorOptions` der Oberwelt |
| `structures` | nein | boolean | `true` | Ob Vanilla-Strukturen generieren |

### Der Block `biomes`

*dimensionen*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `source` | nein | `inherit`, `single` | `inherit` | `inherit` nutzt die eigene Biomkarte der Oberwelt, egal welcher Geländetyp, eine Dimension vom Typ `nether` oder `end` bekommt also die Biome der Oberwelt auf ihrem eigenen Boden; `single` überall ein einziges Biom. Eine `flat`-Dimension hat so oder so ein einziges Biom, das von `single` oder das aus ihrem Superflach-Text |
| `biome` | bei `single` | Biomname | `minecraft:plains` | Welches Biom das ist |

### Der Block `sky`

*dimensionen*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `hasSkyLight` | nein | boolean | `true` | Ob Tageslicht sie erreicht |
| `surfaceWorld` | nein | boolean | `true` | Ob Karten und Kompasse sich wie in der Oberwelt verhalten |
| `respawn` | nein | boolean | `true` | Ob Spieler hier respawnen |
| `respawnDimension` | nein | Dimensions-Id | keine | Wo sie stattdessen respawnen |
| `spawning` | nein | boolean | `true` | Ob Mobs spawnen. Aus verhindert jeden Spawn, Spawner eingeschlossen, gleich was die Gruppe `spawning` sagt |
| `nether` | nein | boolean | `false` | Wird für Portale und Decken wie der Nether behandelt |
| `beds` | nein | boolean | `true` | Aus explodieren Betten |
| `waterVaporizes` | nein | boolean | `false` | Wasser verdampft |
| `cloudHeight` | nein | int | `128` | Wo die Wolken hängen. Eine Einstellung `cloudHeight`, die diese Dimension nennt, oder eine ohne Dimension hat Vorrang |
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

## Portale und Tore

*die welt*

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
| `sound` | nein | Soundname | keiner | Wird beim Durchgehen abgespielt. Siehe [Sound-Namen](#wertelisten) |
| `owned` | nein | boolean | `true` | Nur wer es gebaut hat und wen er zulässt, darf es benutzen. Ein Portal mit Besitzer ist außerdem immun gegen Explosionen |
| `walkIn` | nein | boolean | `false` | Wer hineinläuft, reist, so wie bei einem Netherportal. Aus, wird es von Hand benutzt |

### Portalrahmen

*portale und tore*

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
| `name` | nein | Zeichenkette | der Dateiname | Der Name, der im Log erscheint |
| `axis` | nein | `vertical`, `horizontal` oder `both` | `vertical` | Ob er steht wie ein Netherportal, flach liegt wie ein Endportal oder beides darf |
| `legend` | ja | Objekt aus je einem Zeichen zu einem Block | keine | Die Blöcke, die die Zeilen verwenden dürfen. Ein Blockname mit Zuständen wird gelesen wie überall sonst |
| `rows` | ja | Liste von Zeichenketten | keine | Das Bild, oberste Zeile zuerst |
| `maxWidth` | nein | Ganzzahl | `21` | Breitestes Loch, bis zu dem ein `*` sich streckt |
| `maxHeight` | nein | Ganzzahl | `21` | Höchstes Loch, bis zu dem ein `*` sich streckt |

Drei Zeichen sind keine Blöcke. `.` ist das Loch, in dem das Portal steht, und ein Rahmen ohne eines wird abgelehnt. Ein Leerzeichen ist eine Zelle, die den Rahmen nicht kümmert, ein L-förmiger Rand entsteht also, indem man die Ecken leer lässt. `*` wiederholt: Eine Zeile, die nur aus `*` besteht, wiederholt die Zeile darüber so oft, wie der Spieler gebaut hat, und ein `*` mitten in einer Zeile wiederholt ebenso das Zeichen davor. Es darf auch gar nicht wiederholen: Das Bild mit jedem `*` gestrichen ist also das Kleinste, was zündet, und die Höchstwerte unten sind das Größte. Ein Bild ohne `*` ist genau, und der Spieler muss es so und nicht anders bauen.

Ein stehender Rahmen wird auf beiden waagerechten Achsen und in beiden Richtungen gefunden, es kommt also nicht darauf an, wie der Erbauer stand. Ein liegender wird in allen vier Drehungen gefunden.

**Wie groß er werden darf, sagt das Pack.** `maxWidth` und `maxHeight` sind das größte Loch, bis zu dem ein `*` sich streckt, und alles Kleinere bis zur Untergrenze wird angenommen, ein Pack entscheidet also selbst, ob sein Tor bei Vanillas 21 endet oder schon bei 4. Die Untergrenze ist ein Spieler: Ein stehender Rahmen wird abgelehnt, wenn sein Loch nicht mindestens 1 breit und 2 hoch werden kann, ein liegender, wenn nicht mindestens 1 mal 1, und ein Bild, das das nie erreicht, wird beim Laden mit einer Zeile im Log abgelehnt, statt ein Rahmen zu sein, durch den niemand geht.

**Ein Rahmen kostet umso mehr Suche, je mehr er sich strecken kann.** Ein Zeilen-`*` und ein Spalten-`*` zusammen heißt, dass jede Kombination bis zu beiden Höchstwerten versucht wird, ein Rahmen, der sich in beide Richtungen bis 21 streckt, sind also 441 Bilder. Die Suche gibt lieber auf, als hängen zu bleiben, und schreibt das ins Log, das Zeichen, einen Höchstwert zu senken oder eine der Streckungen zu streichen.

**Nichts verbietet einen Rahmen aus Obsidian mit Feuerzeug, doch er hat Vorrang.** Der Rahmen wird gesucht, bevor das Item selbst wirkt, ein solcher Rahmen öffnet also die Dimension des Packs dort, wo ein Netherportal gestanden hätte. Wähle einen anderen Block oder ein anderes Zündmittel, um Vanillas Portal in Ruhe zu lassen.

### Eine Dimension über einen Rahmen öffnen

*portale und tore*

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
| `color` | nein | Hexfarbe | weiß | Die Farbe, in der das Portal gezeichnet wird |
| `return` | nein | `built`, `player` oder `none` | `built` | Ob ein Rückweg gestellt, vom Spieler gebaut oder gar nicht gewährt wird |
| `gate` | nein | Torname | keiner | Ein Tor, das offen sein muss, um durchzukommen |
| `cooldown` | nein | Ganzzahl, Ticks | `60` | Bevor derselbe Spieler wieder durchgehen darf |
| `platform` | nein | boolean | `true` | Bei der Ankunft eine Landeplattform bauen |
| `platformBlock` | nein | Blockname | Stein | Woraus diese Plattform besteht |
| `sound` | nein | Soundname | keiner | Wird beim Durchgehen gespielt. Siehe [Sound-Namen](#wertelisten) |
| `owned` | nein | boolean | `false` | Nur wer es angezündet hat und wen er zulässt, darf es benutzen |

Den Block, der im Loch steht, schreibt das Pack nicht. Eine Dimension mit einem `portal`-Abschnitt bekommt einen eigenen, in der Portaltextur des Spiels unter `color` gezeichnet, hineinzulaufen statt von Hand zu benutzen, und unzerstörbar. Die Farbe multipliziert die Textur, so wie ein `tintindex` es tut: `#C77DFF` behält das Violett des Nethers, `#4CFFB0` macht es giftig. Wer ein Portal will, das gar nicht die Vanilla-Textur ist, schreibt einen gewöhnlichen eigenen `portal`-Block mit eigener Textur, auf Wunsch als [Pixelkarte](#texturen-als-pixelkarte), wo `tint` zwischen zwei Farben rampen kann.

`return` entscheidet, was auf der anderen Seite geschieht. `built` stellt denselben Rahmen auf, in der Größe, die der Spieler gebaut hat, und zündet ihn an, so wie Vanilla es macht. `player` baut nichts, lässt denselben Rahmen aber drüben anzünden, der Heimweg will also gefunden und gebaut werden. `none` lässt den Rahmen in jener Dimension gar nicht erst zünden, und die Reise geht nur hin.

**Ein Rahmen, mehrere Dimensionen.** Das Paar aus Rahmen und Zündmittel wählt die Dimension, dasselbe `standing_gate` mit Feuerzeug und mit dem eigenen Zünder eines Packs öffnet also zwei verschiedene Orte, jeden in seiner eigenen Farbe. Beanspruchen zwei Dimensionen denselben Rahmen *und* dasselbe Item, ist das ein Fehler im Pack: Die zweite wird abgelehnt und sagt es im Log, statt dass eine von beiden stillschweigend gewinnt.

Bricht ein Block des Rahmens weg, geht das Portal aus, wie in Vanilla.

### Tore

*portale und tore*

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
| `safeReturn` | nein | boolean | `false` | Ein abgewiesener Spieler wird sicher in der Welt abgesetzt, die er verlassen wollte: neben seinem Bett oder geladenen Seelenanker dort, wenn er noch steht, sonst am Spawn dieser Welt |
| `requires` | nein | Liste von Mod-Ids oder Pack-Namespacesn | keine | Das Tor wird übersprungen, wenn nicht alle vorhanden sind |
| `portalBlocks` | nein | Liste von Blocknamen | jedes Portal | Begrenzt das Tor auf diese Portalblöcke, eine Dimension kann also eine bewachte und eine offene Tür haben |

`unlock` nimmt `hold` (ein Item, das in der Hand sein muss), `consume` mit `consumeCount` (`1`), `craft` (ein Item, das gecraftet worden sein muss), `advancement` und `killed` (ein Entity-Name; das Tor öffnet sich für den, der eine davon erlegt, ein Boss kann also den Schlüssel zu einer Welt tragen) mit `killedCount` (`1`), wenn eine nicht reicht, gezählt pro Spieler oder für die ganze Welt, je nach `scope`. Mit `killedDrops` (ein Itemname) legen die gezählten Abschüsse stattdessen dieses Item dem Erleger vor die Füße, statt das Tor zu öffnen, und die Zählung beginnt von vorn, ein Schlüssel lässt sich also erneut verdienen und an jemanden weitergeben, der nie dafür gekämpft hat; sperr dann über `hold` oder `consume` desselben Items, um es zum Schlüssel zu machen. `%item%`, `%mob%` und `%dim%` werden für dich eingesetzt. Ein Schlüssel, den ein Mob droppt, braucht hier nichts Besonderes: Gib dem Mob den Drop und sperr über `hold` oder `consume`.

Tore bewachen auch die Dimensionen des Spiels selbst: Ein Tor, dessen `dimension` `minecraft:the_nether` ist, steht vor jedem Netherportal.

## Die Tiefenwelt

*die welt*

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
| `deepStone` | Blockname | keiner | Der Block, aus dem die Welt unter dem Vanilla-Gelände besteht, wenn der Boden unter -64 geht, etwa der eigene Deepslate eines Packs. Er geht über die acht Schichten unter -64 in Deepslate über, so wie Deepslate in Stein übergeht. Leer behält Stein |
| `noiseCaves` | `off`, `deep` oder `world` | `off` | Wo die Höhlen, Tunnel, Nudeln und Aquifere des Spiels weitergehen, wenn der Boden unter -64 geht: `off` lässt die Welt unter dem Vanilla-Gelände als massiven Tiefenstein für die Worldgen-Ebene zum Schnitzen, `deep` trägt sie bis zum Boden hinunter, mit den Lavaseen in dessen unterste zehn Schichten verlegt, `world` bedeutet in dieser Version dasselbe, weil das Vanilla-Gelände sie ohnehin hat |

Die Tiefenwelt ist der Ort, an dem die eigenen Worldgen-Einträge, Höhlenregionen und Härtegruppen eines Packs ihre Arbeit tun: `minHeight` und `maxHeight` eines Eintrags reichen so weit hinunter, wie der Boden geht. Die eigenen platzierten Features des Spiels bleiben im Vanilla-Gelände: eines, dessen Höhe vom Boden der Welt aus zählt, darunter die Diamanten und das untere Redstone des Spiels, zählt weiter ab -64, und eine Platzierung, die unter -64 fiele, entfällt, wie in einer Welt, deren Boden bei -64 liegt. Die Himmelsschlüssel aus 1.12.2, `deepRavines`, `oreVeins`, `terrainOffset` und die Rubic-Welt selbst haben hier keinen Zwilling, da die eigene Generierung dieser Engine ohnehin vom Boden bis zur Decke reicht.

## Höhlenregionen

*die welt*

`<namespace>/caveregions/*.json`

Der Pfad der Datei ist der Name der Region, den ein Worldgen-Eintrag dann in `caveRegions` nennt. Ein bloßer Name dort nimmt den Namespace dieses Eintrags.

Malt benannte Regionen über den Untergrund, das Pack-Gegenstück zu den Höhlenbiomen des Spiels. Der Untergrund wird in gerundete Zellen geteilt, `caveRegionCells` Blöcke breit und `caveRegionCellsY` hoch, beides `terrain`-Schlüssel, und jede Zelle würfelt nach Gewicht eine Region, oder keine. Alles, was eine Region tut, folgt deterministisch aus dem Seed, Chunks stimmen also überein, ohne je über eine Grenze zu schreiben.

### Regionsdateien

*höhlenregionen*

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
  "structureLoot": "minecraft:chests/simple_dungeon",
  "ambientSound": "minecraft:block.water.ambient",
  "soundChance": 0.02,
  "particle": "minecraft:dripping_water",
  "particleChance": 0.002
}
```

| Schlüssel | Wert | Standard | Was er macht |
| --- | --- | --- | --- |
| `weight` | int | `1` | Anteil der Zellen, die diese Region gewinnt. `0` schaltet sie ab |
| `minHeight` | int | der Weltboden | Unterkante des Bandes, in dem die Region existiert |
| `maxHeight` | int | `48` | Oberkante des Bandes. Eine Zelle, deren Mitte außerhalb liegt, wählt die Region nie |
| `waterLevel` | int | keiner | Legt den Wasserspiegel innerhalb der Region auf diese Höhe fest, anstelle der Aquifere dort. Er bleibt unter dem Meeresspiegel und mindestens zwei Blöcke über der tiefen Lava |
| `dimensions` | Liste von Dimensions-Ids | alle | In welchen Dimensionen die Region erscheint, die eigenen eines Packs eingeschlossen. Eine Region mit `biome` zeigt es nur, wo Biome nach Klima verteilt werden: in der Oberwelt, im Nether und in einer Pack-Dimension, die die Biome der Oberwelt erbt |
| `floorCover` | Block | keiner | Ersetzt den obersten Block von Höhlenböden innerhalb der Region |
| `floorChance` | 0.0 bis 1.0 | `1.0` | Wie viel vom Boden bedeckt wird |
| `ceilingCover` | Block | keiner | Ersetzt Höhlendeckenblöcke innerhalb der Region |
| `ceilingChance` | 0.0 bis 1.0 | `1.0` | Wie viel von der Decke |
| `coverReplace` | Liste von Blöcken | alles Steinartige | Was die Bedeckungen ersetzen dürfen |
| `spawns` | Liste | keine | Mobs, die innerhalb der Region spawnen, mit denselben Einträgen wie das `spawns` eines Bioms: `entity`, `type` (monster, creature, ambient oder water), `weight` (`8`), `min` (`1`) und `max` (`4`) für die Gruppengröße. Eine Stelle mit Himmelssicht bleibt dem Biom überlassen, wie bei den Bedeckungen |
| `keepDefaultSpawns` | boolean | `false` | Behält die Spawnliste des Bioms neben der der Region. Aus, ersetzt die Liste der Region sie innerhalb der Region vollständig |
| `structures` | Liste | keine | Ein Bauwerk, einmal pro Regionszelle gesetzt, im Herzen der Zelle, auf den nächsten Höhlenboden gesetzt, so wie das Spiel einem Höhlenbiom sein Wahrzeichen gibt. Einträge sind `namespace:name`-Vorlagen oder `{ "structure": "...", "weight": 3 }` zur Auswahl zwischen mehreren |
| `structureChance` | 0,0 bis 1,0 | `1.0` | Die Chance, mit der jede Zelle der Region ihr Bauwerk tatsächlich bekommt |
| `structureLoot` | `namespace:pfad` | keine | Die Beutetabelle, aus der jede Truhe in einem gesetzten Bauwerk beim ersten Öffnen gefüllt wird |
| `biome` | Biomname | keiner | Das Biom, das die Region in ihrem Raum meldet, als 3D-Biom geschrieben. Gibt der Region eigene Laub-, Gras- und Wasserfarben, eigene Musik und Umgebungsgeräusche, und Vanillas Spawn-Gewichtung liest es. Die Oberfläche darüber bleibt unberührt, da nur die Zellen geschrieben werden, die die Region einnimmt. Weggelassen behält die Region das Biom, das sie umgibt, und legt trotzdem ihre Bedeckungen, Strukturen und Spawns |
| `requires` | Liste von Mod-IDs oder Pack-Namespaces | keine | Die Region wird übersprungen, wenn nicht alle da sind |
| `ambientSound` | Geräuschname | keiner | Ein Geräusch, das einem Spieler in der Region ab und zu vorgespielt wird, so wie die Biome des Spiels ihre eigenen Höhlengeräusche ergänzen. Der Server schickt es nur diesem Spieler |
| `soundChance` | 0,0 bis 1,0 | `0.0111` | Die Chance pro Tick, dass `ambientSound` spielt |
| `particle` | Partikelname | keiner | Ein Partikel rund um einen Spieler in der Region, einer der Partikel des Spiels, die keine eigenen Einstellungen brauchen, etwa `minecraft:dripping_water`, `minecraft:happy_villager` oder `minecraft:underwater`. Ein 1.12.2-Name wie `dripWater` wird mit seinem Pack umgewandelt. Nur Luft innerhalb der Region zeigt ihn |
| `particleChance` | 0,0 bis 1,0 | `0.00625` | Die Partikeldichte der Biome des Spiels: Pro Tick werden etwa 667 Stellen im Umkreis von 16 Blöcken versucht, und jede zeigt den Partikel mit dieser Chance |

### Zellen

*höhlenregionen*

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `caveRegionCells` | Zahl, Blöcke | `128` | Wie breit eine Regionszelle ist |
| `caveRegionCellsY` | Zahl, Blöcke | `64` | Wie hoch eine Regionszelle ist |
| `caveRegionPlainWeight` | Zahl | `4` | Das Gewicht des schlichten, regionslosen Untergrunds im Wurf jeder Zelle. Höher lässt mehr Untergrund ohne Region: Mit einer einzigen Region vom Gewicht 1 bekommt etwa ein Fünftel der Zellen die Region |

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

Wie viel vom Untergrund schlicht bleibt, bestimmt der `terrain`-Schlüssel `caveRegionPlainWeight`, Standard `4`: Mit einer einzigen Region vom Gewicht 1 bekommt etwa ein Fünftel der Zellen die Region. Bedeckungen greifen unter einem Dach, eine Region, die über den Boden hinausreicht, zeigt sich an der Oberfläche also nie. Bedeckungen wirken in jeder Höhle, egal welcher Generator sie geschnitzt hat.

### Merkmale in einer Region

*höhlenregionen*

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

---

# Die Welt generieren

## Worldgen-Einträge

*die welt generieren*

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

### Was es setzt

*worldgen-einträge*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `block` | ja | Blockname | | Was gesetzt wird |
| `blocks` | nein | Liste von Objekten | keine | Eine gewichtete Liste, genutzt statt eines einzelnen Blocks. Siehe unten |
| `size` | nein | int oder Bereich | `8` | Wie viele Blöcke ein Versuch setzt, oder wie groß eine Form mit Radius ausfällt |
| `attempts` | nein | int oder Bereich | `8` | Wie oft es pro Chunk versucht wird |
| `replace` | nein | Liste von Blocknamen oder Objekten | `["minecraft:stone"]` | Was ersetzt werden darf. Siehe unten |
| `adjacent` | nein | Liste von Blocknamen oder Objekten | keine | Setzt nur dort, wo einer davon unter den 26 Blöcken steht, die die Stelle berühren. Dieselben Formen wie `replace` |
| `sparse` | nein | boolean | `false` | Streut die Blöcke, statt sie zusammenzupacken |
| `shape` | nein | Objekt | `{ "type": "cluster" }` | Die Form, die es annimmt. Siehe [Formen](#formen) |
| `spread` | nein | Objekt | `{ "type": "even" }` | Wo es hingesetzt wird. Siehe [Verteilung](#verteilung) |

### Wo es generieren darf

*worldgen-einträge*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
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
| `caveRegions` | nein | Liste von Regionsnamen | keine | Generiert nur innerhalb dieser [Höhlenregionen](#höhlenregionen) |
| `snap` | nein | `floor` oder `ceiling` | keiner | Verschiebt jeden Versuch erst senkrecht zum nächsten Höhlenboden oder zur nächsten Höhlendecke |
| `snapDepth` | nein | int | `0` | Wie weit `snap` danach über die Oberfläche hinaus geht, vom Boden nach unten und von der Decke nach oben. `0` bleibt im freien Raum an der Oberfläche, `1` ist der Oberflächenblock selbst, `2` der dahinter. Was überschrieben werden darf, regelt weiterhin `replace`, so legt ein Pack ein Band knapp unter den Boden statt darauf |

### Oberflächenzeichen und Folgeeinträge

*worldgen-einträge*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `indicators` | nein | Liste von `block=gewicht` | keine | Blöcke, die über einer erzeugten Ader verstreut auf der Oberfläche liegen bleiben, damit ein Spieler ahnt, was unter dem Boden liegt; wähle sie passend zum Inhalt der Ader. `empty=gewicht` lässt eine Stelle leer. Ein Eintrag ohne Gewicht oder mit einem Gewicht unter 1 wird protokolliert und weggelassen, und auf den Straßen und Gebäuden eines Dorfs oder einer Stadt bleibt keiner liegen |
| `indicatorCount` | nein | int oder Bereich | `1` | Wie viele Oberflächenstellen jede erzeugte Ader bekommt |
| `indicatorSpread` | nein | int, Blöcke | `0` | Wie weit über den Fußabdruck der Ader hinaus ein Hinweis landen darf |
| `then` | nein | Liste von `name=gewicht` oder Objekten | keine | Worldgen-Einträge, die direkt nach diesem aus ihm herauswachsen, an ihm angesetzt: der Ursprung des Nachfolgers liegt knapp außerhalb des Randes dieser Ader, in der Richtung, die `thenSpread` und `thenDepth` vorgeben, so dass sich beide berühren. Ein Eintrag ist `name=gewicht` oder ein Objekt mit `name`, `weight` und eigenem `spread` und `depth` (int oder Bereich), die für diesen Nachfolger allein die Werte der Ader ersetzen, so dass eine Liste eine Diamantspitze nach unten und einen Ast zur Seite schicken kann. Ein bloßer Name wird im Namespace dieses Packs gelesen, `empty=gewicht` reiht nichts ein. Ein Nachfolger behält seine eigene Form, Blöcke, Größe und `replace`, überspringt aber seine eigenen Versuche, Chance, Höhenband und Biomfilter, und darf selbst `then` tragen, so tief das Pack will; ein Eintrag, der in derselben Kette schon erzeugt wurde, beendet sie |
| `thenCount` | nein | int oder Bereich | `1` | Wie viele verschiedene Nachfolger pro erzeugter Ader aus dieser Liste gewählt werden, jeder Eintrag höchstens einmal, so dass eine Zahl gleich der Listenlänge alle wachsen lässt |
| `thenSpread` | nein | int, Blöcke | der Radius der Form | Wie weit die Richtung, in die ein Nachfolger wächst, seitlich kippen darf, gewürfelt von minus bis plus diesem Wert |
| `thenDepth` | nein | int oder Bereich | `0` | Wie weit die Richtung nach unten (negativ) oder oben kippt. `0` ohne seitliches Kippen hängt den Nachfolger gerade nach unten |
| `prospectAs` | nein | Zeichenkette | der Dateiname | Wie ein Schürfgegenstand diesen Eintrag in seiner Lesung nennt, z. B. `Hämatit` |

### Retrogen und Voraussetzungen

*worldgen-einträge*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `retrogen` | nein | boolean | `false` | Generiert auch in Chunks, die es schon gibt |
| `retrogenKey` | nein | string | der Schlüssel aus der Config | Überschreibt den Retrogen-Schlüssel für diesen einen Eintrag |
| `requires` | nein | Liste von Mod-IDs oder Pack-Namespaces | keine | Der Eintrag wird übersprungen, wenn nicht alle da sind |

### Gewichtete Blöcke

*worldgen-einträge*

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

*worldgen-einträge*

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

*worldgen-einträge*

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

*worldgen-einträge*

Ein Eintrag in der `then`-Liste eines Worldgen-Eintrags ist ein Name mit Gewicht, oder ein Objekt, wenn dieser Folger eine eigene Richtung braucht.

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
| `weight` | nein | Zahl | `1` | Wie oft dieser Folger gegenüber den anderen der Liste gewählt wird |
| `spread` | nein | Zahl, Blöcke | das `thenSpread` des Eintrags | Wie weit die Richtung dieses Folgers seitlich ausschert, nur für diesen einen Eintrag |
| `depth` | nein | Zahl oder Bereich | das `thenDepth` des Eintrags | Wie weit die Richtung nach unten, negativ, oder nach oben neigt, nur für diesen einen Eintrag |

`name=Gewicht` ist die Kurzform eines Objekts mit nur diesen beiden, und `empty=Gewicht` reiht nichts ein. Weil `spread` und `depth` pro Eintrag gelten, kann eine Liste eine Diamantspitze senkrecht nach unten und einen Zweig zur Seite aus derselben Ader schicken.

## Formen

*die welt generieren*

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
| `spring` | Eine Flüssigkeit, die aus einer Höhlenwand sickert: gesetzt, wo Gestein darüber, darunter und an drei Seiten steht und eine Seite offen ist, und zum Fließen gebracht |

### Größe und Form

*formen*

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
| `middle` | geode | Blockname | keiner | Eine Schale zwischen dem Körper und `outline`, der Calcit der Amethystgeode des Spiels |
| `budding` | geode | Blockname | keiner | Ersetzt Körperblöcke, die zur hohlen Mitte zeigen, wie knospender Amethyst. Braucht `fill` |
| `buddingChance` | geode | 0,0 bis 1,0 | `0.083` | Wie viele dieser Körperblöcke knospen |
| `crystal` | geode | Blockname | keiner | Wächst in die Höhlung neben einem `budding`-Block, wie eine Amethystgruppe |
| `crystalChance` | geode | 0,0 bis 1,0 | `0.35` | An wie vielen dieser Stellen einer wächst |
| `crack` | geode | 0,0 bis 1,0 | `0` | Die Chance, dass eine Geode aufgebrochen ist: eine Röhre von der Mitte durch jede Schicht zu einer Seite, gefüllt mit `fill`. Die Amethystgeoden des Spiels nehmen `0.95` |

### Platzierung

*formen*

| Schlüssel | Genutzt von | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `surface` | decoration, tree | Liste von Blocknamen | keine | Worauf sie sitzt |
| `seeSky` | decoration | boolean | `true` | Nur dort setzen, wo der Himmel zu sehen ist |
| `checkStay` | decoration | boolean | `true` | Nur dort setzen, wo der Block auch bestehen bliebe |
| `stackHeight` | decoration | int oder Bereich | `1` | Wie viele übereinandergestapelt werden |
| `scatterX` | decoration, tree | int | `8` | Wie weit sie seitlich streut |
| `scatterY` | decoration, tree | int | `4` | Wie weit sie senkrecht streut |
| `scatterZ` | decoration, tree | int | `8` | Wie weit sie seitlich streut |
| `rarity` | alle | int | keiner (`400` für belt) | Eine Platzierung pro so viele Chunks. Bei einem belt bestimmt das den Abstand der Gürtel; bei jeder anderen Form lässt es nur einen Chunk von so vielen überhaupt seine `attempts` würfeln. `field` ignoriert es |
| `rarityIsPerChunk` | alle | boolean | `false` | Macht aus `rarity` stattdessen die Anzahl Platzierungen pro Chunk |

### Bäume

*formen*

| Schlüssel | Genutzt von | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `log` | tree | Blockname | keiner | Der Stammblock |
| `leaves` | tree | Blockname | keiner | Der Blätterblock |
| `vines` | tree | boolean | `false` | Ranken von den Blättern hängen lassen |

Ein `tree` ohne `log` oder `leaves` generiert nichts und sagt das im Log. Nennst du ein `structure` oder mehrere unter `structures`, wird an jeder Stelle diese Vorlage gesetzt, statt einen Baum zu generieren, und dann braucht es weder `log` noch `leaves`; ein Baum aus einer Vorlage liest `turns`, `mirrors`, `integrity`, `lootTable` und `locateAs` genau wie ein `imprint`.

### Vorlagen setzen

*formen*

| Schlüssel | Genutzt von | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `structure` | imprint, tree | `namespace:name` | keine | Die Vorlage, die gesetzt wird |
| `integrity` | imprint, tree | 1 bis 100 | `100` | Prozentsatz der Blöcke der Vorlage, die tatsächlich erscheinen |
| `lootTable` | imprint, tree | `namespace:pfad` | keine | Die Beutetabelle, aus der jede Truhe in der gesetzten Vorlage beim ersten Öffnen gefüllt wird, und jeder andere Behälter, der eine annimmt, eine Shulkerkiste oder die Kiste eines Mods darunter. Gilt für `structure` und jeden Eintrag von `structures`; jede Truhe würfelt mit eigenem Seed |
| `structures` | imprint, tree | Liste | keine | Mehrere Vorlagen zur Auswahl, eine davon wird jedes Mal gesetzt. Jeder Eintrag ist `{ "structure": "namespace:name", "weight": 3 }` oder ein bloßer Name für gleiche Chancen. Überschreibt `structure` |
| `turns` | imprint, tree | Liste | beliebig | Wie herum sie gesetzt werden darf: `none`, `quarter`, `half`, `threequarter`. Einträge dürfen ein `weight` tragen. Weggelassen sind alle vier gleich wahrscheinlich |
| `mirrors` | imprint, tree | Liste | keine | Sie zusätzlich spiegeln: `none`, `leftright`, `frontback`, mit optionalem `weight`. Ein Eintrag mit eigenem Gewicht wird `{ "mirror": "leftright", "weight": 2 }` geschrieben, ein `turns`-Eintrag genauso mit `turn` |
| `at` | imprint | zwei Ints, x und z | keine | Genau einmal an diesen Blockkoordinaten an der Oberfläche setzen, wenn dieser Chunk generiert, statt nach Zufall. Siehe [Strukturen an genauen Stellen](#strukturen-an-genauen-stellen) |
| `locateAs` | imprint, tree | String | keiner | Jede Struktur, die dieser Eintrag setzt, unter diesem Namen eintragen, sodass `/rdplserver locate <Name>` die nächste findet. Siehe [Gesetzte Strukturen finden](#platzierte-strukturen-finden) |

Für eine Form, die kein eingebauter Typ abdeckt, ist `imprint` der Weg: Bau sie als `.nbt`-Vorlage und setz diese, mit `structures` zum Abwechseln, `turns` und `mirrors` zum Drehen und `integrity`, um sie rauer aufzulösen als die Datei, die du gezeichnet hast.

### Strukturen an genauen Stellen

*formen*

Vanilla-Strukturen nagelst du mit `structureAt` in den `terrain`-Einstellungen an genaue Punkte, als `structure=x,z`-Einträge, einer pro Zeile: `"structureAt": ["villages=1000,-500"]`. **x und z sind Blockkoordinaten, keine Chunkkoordinaten**, und die Struktur generiert in dem Chunk, in dem dieser Block liegt. Legt `terrainAdaptation` Dörfer als Stadtstraßen an, steht der Brunnen eines festgenagelten Dorfs genau auf diesem Block, oder so nah daran, wie sein Viertel es zulässt, wenn der Block nur wenige Blöcke vom Rand des Viertels entfernt liegt; andere Strukturen und Dörfer ohne diese Einstellung beginnen dort, wo das Spiel sie in diesem Chunk beginnen würde. Ein Eintrag pro gewünschtem Exemplar. Ihr Abstand, ihre Trennung, ihr Mindestspawnabstand und die Prüfungen auf flachen Boden treten alle beiseite, die Stelle ist damit Sache des Packs, und zwei Pins näher als einen Chunk beieinander setzen zwei Strukturen in denselben Chunk. Einmal gegründet, setzt sich die Struktur an ihrem Chunk nach den üblichen Regeln in den Boden.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `structureAt` | Liste von `structure=x,z` | keine | Nagelt eine Vanilla-Struktur an eine genaue Stelle, ein Eintrag je gewünschtem Vorkommen. x und z sind Blockkoordinaten, und die Struktur generiert in dem Chunk, der diesen Block enthält; ihr Raster, ihr Abstand, ihre Mindestentfernung vom Spawn und ihre Prüfung auf ebenen Boden treten alle zurück |

Ein `imprint`-Eintrag nagelt genauso fest, mit `"at": [x, z]` in seiner Form, und setzt sie genau einmal an diesen Koordinaten an der Oberfläche, sobald dieser Chunk generiert, statt nach Zufall. Das lässt sich mit `locateAs` kombinieren, eine festgenagelte Struktur ist also auch auffindbar.

### Platzierte Strukturen finden

*formen*

Ein `imprint`-Eintrag mit `"locateAs": "Crypt"` registriert jede Struktur, die er platziert, unter diesem Namen, und `/rdplserver locate Crypt` zeigt dann auf die nächste davon, mit dem Namen in der Tab-Vervollständigung; `/rdplserver goto Crypt` bringt dich hin. Finden lassen sich nur Strukturen, die schon generiert wurden, denn Pack-Strukturen werden beim Erzeugen der Chunks nach Zufall platziert und nicht auf einem Raster, das das Spiel vorhersagen könnte. Die Namen liegen im Spielstand der Welt, überstehen also Neustarts und funktionieren auf Servern. Ein so registrierter Name kann mit `gotoPlaceLevels` auch eine eigene Berechtigung bekommen, sodass ein Pack getrennt von den Vanilla-Strukturen entscheidet, wer zu seinen eigenen gebracht werden darf.

### Schlüssel für Felder und Adern

*formen*

| Schlüssel | Genutzt von | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `field` | field | Objekt | `{ "type": "speckle" }` | Wie das Feld errechnet wird. Dieselben Schlüssel wie das `field` einer Härtegruppe, beschrieben unter [Das Feld](#das-feld): `speckle` mit `chances` und `spread`, oder `seeded` mit `cell`, `seeds`, `reach`, `arms` und `armReach` |
| `threshold` | field, vein | 0,0 bis 1,0 | `0.5` (`0.4` bei vein) | Wie stark das Feld an einem Block sein muss, bevor dort gesetzt wird. Niedriger füllt mehr |
| `fade` | field | int | `0` | Lässt das Band oben ausfransen statt glatt zu enden: über die obersten so vielen Blöcke des Höhenbereichs sinkt die Chance jedes Blocks Stufe für Stufe, derselbe Look, den die Engine `deepStone` am Übergang zur Welt darüber gibt |
| `pattern` | vein | `default`, `banded` oder `tube` | `default` | Das Aussehen der Lagerstätte: ein verzerrter Klumpen, alle paar Blöcke gestapelte Schichten oder hohle Röhren, die sich durchs Gestein winden |
| `density` | vein | 0,0 bis 1,0 | `1.0` | Der Anteil der passenden Blöcke, die wirklich gesetzt werden, eine Münze pro Block |
| `rich` | vein | Blockname | keiner | Gesetzt ab `richAt` aufwärts im Feldbereich über `threshold`, dem Herz der Lagerstätte, statt der Blöcke des Eintrags |
| `poor` | vein | Blockname | keiner | Gesetzt in den unteren zwei Fünfteln dieses Bereichs, dem Rand, statt der Blöcke des Eintrags; die Mitte sind die Blöcke des Eintrags selbst. Eine weggelassene Stufe setzt dort die Blöcke des Eintrags |
| `richAt` | vein | 0,0 bis 1,0 | `0.88` | Wo in diesem Bereich die reiche Stufe beginnt: `0.88` hält den reichen Block auf das stärkste Achtel der Lagerstätte, eine kleinere Zahl macht den reichen Kern dicker, `1.0` lässt gar keinen reichen Block zu |
| `poorAt` | vein | 0,0 bis 1,0 | `0.4` | Wo die eigenen Blöcke des Eintrags beginnen: darunter wird der `poor`-Block gesetzt, `0.4` gibt also einen Rand aus den unteren zwei Fünfteln und `0.0` gar keinen armen Rand. Auf `richAt` begrenzt |

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

### Gürtel

*formen*

Ein `belt` ist eine Kugel, die weit größer als ein Chunk ist, gedacht für Gesteinsregionen statt für Erzadern. Sein `radius` ist die Größe der Kugel, und jeder Chunk rechnet für sich selbst aus, wo die Kugeln in seiner Nähe anfangen, aus dem Welt-Seed und dem Namen des Eintrags, ein Gürtel kommt also vollständig heraus, egal in welcher Reihenfolge die Chunks generiert werden, und es wird nie etwas in einen Nachbar-Chunk geschrieben.

```json
{
  "shape": { "type": "belt", "radius": 32, "rarity": 400 }
}
```

Ein Gürtel ignoriert `attempts` und `spread`, weil er pro Chunk statt pro Versuch gesetzt wird. `minHeight` und `maxHeight` sind das Band, in dem die Mittelpunkte liegen, und die Kugel reicht um `radius` über dieses Band hinaus. `replace` entscheidet, was er frisst, und `biomes` sowie die Grenzen für Temperatur und Niederschlag werden am Mittelpunkt geprüft, ein Gürtel erscheint also entweder ganz oder gar nicht, statt an einer Biomgrenze abgeschnitten zu werden.

Der Aufwand wächst mit der dritten Potenz von `radius`, und ein niedriger `rarity`-Wert vervielfacht ihn, fang also bei den Standardwerten an und erhöhe den Radius langsam.

### Felder

*formen*

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

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `threshold` | nein | 0.0 bis 1.0 | `0.5` | Wie stark das Feld sein muss, bevor ein Block gesetzt wird |
| `field` | ja | Objekt | keiner | Dasselbe Objekt wie bei einer Härtegruppe, mit denselben Arten `speckle` und `seeded` |

Ein niedriger `threshold` nimmt fast das ganze Feld und gibt breite Bänder, ein hoher nimmt nur die Mitte jedes Nestes und gibt kleine verstreute Taschen. Mit `speckle` bekommst du viele feine Sprenkel, mit `seeded` rundere Nester oder, sobald es Arme hat, Knoten mit Ranken, die sich einander entgegenstrecken.

Wie ein Gürtel übergeht ein Feld `attempts` und `spread`, da es je Chunk statt je Versuch gefragt wird, und es schreibt nie in einen Nachbar-Chunk. Es ergibt sich aus dem Welt-Seed und dem eigenen Namen des Eintrags, derselbe Seed gibt also immer dieselben Adern, und zwei Einträge mit verschiedenen Namen decken sich nie. `replace`, `adjacent`, `biomes` und die Klimagrenzen gelten wie sonst auch.

## Verteilung

*die welt generieren*

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

## Strukturkarten

*die welt generieren*

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

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `cell` | Zahl | `32` | Der Rasterabstand in Blöcken, bis 48. Eine Vorlage, die kleiner ist als die Zelle, sitzt in der Zellecke, sodass Stücke in voller Größe nahtlos aneinanderstoßen |
| `ground` | Zahl | `0` | Welche Ebene auf der Geländeoberfläche aufsetzt. Ebenen davor graben sich ein, so bekommt ein Bauwerk Keller |
| `at` | zwei Zahlen | keiner | Setzt eine Kopie an genaue Blockkoordinaten, so wie `structureAt` eine Struktur festlegt |
| `spacing` | Zahl | `0` | Verstreut Kopien auf einem Raster in diesem Chunkabstand, versetzt aus dem Weltseed. `0` verstreut keine, eine Karte nur mit `at` baut also genau einmal |
| `chance` | Zahl | `100` | Der Prozentanteil der Rasterplätze, die eine Kopie bauen |
| `dimensions` | Liste von Dimensions-Ids | alle | Wo die Karte bauen darf, die eigenen Dimensionen eines Packs eingeschlossen |
| `layers` | Liste | keine | Die Ebenen, von unten nach oben, jede mit `palette` und `map` |

Eine Palette nennt Vorlagen per Registry-Schlüssel aus dem `<namespace>/structures/` eines Packs.

| Wert | Was er bewirkt |
| --- | --- |
| `"a": "mypack:keep"` | Jede `a`-Zelle dieser Ebene setzt diese Vorlage |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | Jede `a`-Zelle lost die Liste nach Gewicht aus, aus dem Weltseed und dem Platz der Zelle, zwei Kopien des Bauwerks unterscheiden sich, aber dieselbe Welt baut immer dasselbe |
| `.` | Eine leere Zelle, nichts wird gesetzt |

Jede Kopie lost eine der vier Ausrichtungen aus dem Weltseed aus, und das ganze Bauwerk dreht sich gemeinsam, Vorlagen eingeschlossen, Mauern, die sich über Zellen hinweg treffen, treffen sich also weiterhin; eine Karte dreht sich, spiegelt sich aber nie. Die Bodenebene setzt auf der abgetasteten Geländeoberfläche unter der Mitte des Bauwerks auf, und die ganze Karte teilt sich diese eine Höhe. Eine verstreute Karte ist für das Spiel eine eigene Struktur, gesetzt über ein Structure-Set, das für dich geschrieben wird, jeder Chunk baut also nur seinen eigenen Ausschnitt des Rasters, und ein Bauwerk über viele Chunks entsteht ohne kaskadierende Generierung, in welcher Reihenfolge die Chunks auch laden. Ein [Dorfgrundstück](#dorfgrundstücke) vom Typ `template` kann in seinem `structure` ebenfalls eine Karte nennen, die Komposition wird dann zum Stadtgebäude.

## Dorfgrundstücke

*die welt generieren*

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

### Jedes Grundstück

*dorfgrundstücke*

| Schlüssel | Genutzt von | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `type` | allen | `farm` oder `template` | `farm` | Welche Sorte Grundstück |
| `weight` | allen | int | `3` | Wie oft dieses Grundstück gegenüber den anderen des Packs gezogen wird |
| `leastCount` | allen | int | `1` | Wenigstens so viele je Viertel: ein Viertel setzt Grundstücke entlang seiner Straßen bis zu einer Obergrenze, gewürfelt zwischen dem kleinsten `leastCount` und dem größten `mostCount` der Grundstücke, die es bauen darf, beide um 16 erhöht, oder um ein Zweiunddreißigstel von `villagePlotsLeast`, wenn das mehr ist, solange eine Stadt wächst. Grundstücke hinter anderen Grundstücken zählen nicht mit |
| `mostCount` | allen | int | `4` | Die obere Grenze dieses Wurfs |
| `width` | allen | int | `7` | Größe quer zur Straße |
| `height` | allen | int | `4` | Höhe, die über dem Boden freigeräumt wird |
| `depth` | allen | int | `9` | Größe von der Straße weg |
| `apron` | allen | int | `2` | Wie weit der Boden unter dem Grundstück von der Straßenhöhe abweichen darf, bevor es abgelehnt oder an seiner Straße entlang verschoben wird: so viele Blöcke Auffüllung darunter oder Einschnitt in eine Erhebung darüber, und höchstens so viel zwischen seiner höchsten und tiefsten Ecke. Ein breites Grundstück im Hügelland braucht mehr. Hoch gesetzt terrassiert sich das Grundstück geradewegs in einen Hang, was an der falschen Stelle einen Berg auffrisst |
| `ground` | allen | Blockname | `minecraft:dirt` | Was am Hang darunter aufgefüllt wird |
| `requires` | allen | Liste von Mod-IDs oder Pack-Namespaces | keine | Das Grundstück bleibt weg, wenn nicht alle da sind |

Grundstücke sind das, was die packeigenen Städte entlang ihrer Straßen bauen, und jedes Vorlagen-Grundstück zieht außerdem als eines ihrer Häuser in die Dörfer des Spiels selbst ein, betreten in der Mitte seiner Vorderseite. Gibt es gar keine Grundstücksdateien, baut eine Stadt die Dorfhäuser des Spiels selbst, passend zum Dorftyp des Bioms jedes Viertels. `weight` entscheidet, welches deiner Grundstücke gezogen wird, sobald eine Straße nach einem fragt, und `villagePieces` in den `villages`-Einstellungen nennt die Grundstücke, die eine Vorlage behält, als `mypack:smithy`, `smithy` oder die Struktur, die das Grundstück baut. Wie die Straßen selbst gelegt, ausgestattet, überbrückt, untertunnelt und mit Gleisen versehen werden, sind die `village*`-Einstellungen unter [Was jede Gruppe macht](#was-jede-gruppe-macht).

### Äcker

*dorfgrundstücke*

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

| Schlüssel | Genutzt von | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `crops` | farm | Liste von Blocknamen | Weizen | Eine pro Block gepflanzt, in zufälliger Wachstumsstufe |
| `edge` | farm | Blockname | `minecraft:oak_log` | Der Rahmen um das Grundstück |
| `soil` | farm | Blockname | `minecraft:farmland` | Woraus die Reihen bestehen |
| `water` | farm | boolean | `true` | Eine Wasserrinne zwischen die Reihen legen |
| `rowWidth` | farm | int | `2` | Wie breit jede Erdreihe ist |

### Aus Vorlagen gebaut

*dorfgrundstücke*

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
| `structure` | template | `namespace:name` | keine | Die Vorlage, die gesetzt wird, oder eine deiner Strukturkarten, die dann die Größe des Grundstücks bestimmt |
| `integrity` | template | 1 bis 100 | `100` | Prozentsatz der Blöcke der Vorlage, die erscheinen |
| `lootTable` | template | `namespace:pfad` | keine | Die Beutetabelle, aus der jede Truhe in der gesetzten Vorlage beim ersten Öffnen gefüllt wird. Ein Grundstück, das eine Strukturkarte nennt, bleibt unberührt |
| `villagers` | allen | int | `0` | Wie viele Leute das Grundstück spawnt |
| `villagerEntity` | allen | `namespace:name` | ein Dorfbewohner | Wer dort wohnt, etwa eine eigene Entity-Variante |
| `villagerX` | allen | int | `1` | Wo sie erscheinen, quer über das Grundstück |
| `villagerY` | allen | int | `1` | Wo sie erscheinen, über dem Boden |
| `villagerZ` | allen | int | `1` | Wo sie erscheinen, in das Grundstück hinein |

## Stadtpläne

*die welt generieren*

Ein Stadtplan zeichnet den Straßenplan einer Stadt auf ein Raster, ein Zeichen je Zelle, und die Stadt wird nach der Zeichnung angelegt, statt eine auszuwürfeln. Straßen, Plätze und Grundstücke werden dieselben Teile, die eine gewürfelte Stadt verwendet, also gelten jede Straßenoption, Brücke, Tunnel, U-Bahn, Abwasserkanal, Laterne und jedes Platzmittelstück unverändert. Die Weltvorlage nennt den Plan in `villageLayout`.

`<namespace>/citymaps/*.json`

```json
{
  "cell": 48,
  "settings": { "villagePathCenterBlock": "minecraft:red_concrete" },
  "palette": {
    "#": "street",
    "+": "plaza",
    "a": "alley",
    "T": ["mypack:tower_blue=1", "mypack:tower_gray=1"],
    "B": "mypack:block",
    "s": ["mypack:shop_blue=2", "mypack:shop_gray=1"],
    "g": "grow",
    "J": "junction",
    "b": "bulb",
    "E": { "kind": "elevated", "height": 8 },
    "W": { "kind": "street", "settings": { "villagePathExtraWidth": 8, "villagePathSidewalkWidth": 3 } }
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
| `settings` | Objekt | keins | Stadteinstellungen nur für diesen Plan, unter den Namen, die eine Weltvorlage nutzt, etwa `villagePathCenterBlock`. Sie gehen vor denen der Vorlage, und die eigenen Einstellungen eines Bioms gehen weiterhin vor ihnen |

| Wert | Was er bewirkt |
| --- | --- |
| `"#": "street"` | Ein Lauf von Straßenzellen entlang einer Zeile oder Spalte wird eine Straße in der Breite des Packs. Wo ein Zeilenlauf einen Spaltenlauf kreuzt, wird die Kreuzung wie jede andere gestaltet. Eine einzelne Straßenzelle ohne Lauf in einer Achse wird als kurzer Stummel entlang der Zeile angelegt |
| `"+": "plaza"` | Ein Platz mit seinem Mittelstück. Läufe gehen durch Platzzellen hindurch, Straßen treffen sich also am Platz, und ein Platz auf einer Kreuzung stellt sein `villageWellStructure`-Mittelstück wie einen Kreisverkehr mitten auf die Kreuzung. Der erste Platz in der Datei ist das Zentrum der Stadt selbst, der den Plan dort festmacht, wo die Stadt gegründet wird; ein Plan ohne einen wird dort zentriert |
| `"a": "alley"` | Ein schmaler Lauf. Gebäude stehen daran, aber er verbindet nichts, die Gassenregel wie gewohnt |
| `"J": "junction"` | Eine Straßenzelle, die in beide Richtungen angelegt wird, sodass dort eine Kreuzung steht, auch wo die Zeichnung nur in eine Richtung hindurchläuft. Der Arm quer dazu ist eine Zelle lang |
| `"b": "bulb"` | Eine Straßenzelle, die in einem Wendeplatz endet. Hat ein Plan eine solche Zelle, bekommen nur Straßenenden in diesen Zellen einen Wendeplatz; ein Plan ohne sie gibt drei von vier Sackgassen einen Wendeplatz, gewürfelt aus dem Seed der Welt. Ein Wendeplatz wird nur dort angelegt, wo kein Grundstück, keine andere Straße, keine Bahnlinie und kein Platzmittelstück in seiner Reichweite steht: er schrumpft, bis er passt, bis hinunter auf etwas breiter als die Straße, und ein Ende, das in keiner Größe Platz hat, bleibt eine einfache Sackgasse |
| `"E": { "kind": "elevated", "height": 8 }` | Eine Straßenzelle auf einer Fahrbahn `height` Blöcke, 2 bis 64, über dem höchsten Boden unter ihrem Abschnitt zusammenhängender erhöhter Zellen, mit einer Rampe von einem Block pro Reihe an jedem Ende. Eine Straße, die innerhalb des Abschnitts kreuzt, steigt mit, und die Grundstücke daran bleiben auf dem Boden. Ein Abschnitt, dessen Fahrbahn oder Rampen eine Bahnlinie oder das Platzmittelstück erreichen würden, bleibt ebenerdig, mit einer Zeile im Log. Jeder Wert lässt sich so als Objekt schreiben, `kind` nennt das Wort |
| `"W": { "kind": "street", "settings": { "villagePathExtraWidth": 8 } }` | Eine Straße, die mit eigenen Straßenschlüsseln angelegt und gepflastert wird, die vor denen des Plans und der Vorlage gehen. Ihre Breite folgt ihrem eigenen `villagePathExtraWidth`, `villagePathSidewalkWidth` und ihrer Linie, und Belag, Linien und Gehwege folgen ihren eigenen Blockschlüsseln, eine Allee oder eine Gasse bekommt also ein eigenes Zeichen. Ein Lauf nimmt die Schlüssel seiner ersten Zelle, die welche setzt. Wie breit oder schmal auch immer, eine gezeichnete Straße bleibt eine Straße: Sie wird nie für eine Gasse gehalten |
| `"T": "mypack:tower"` | Eine Grundstückszelle, angelegt aus dieser Grundstücksdefinition, mittig in der Zelle und zur nächsten Straße gewandt |
| `"T": ["mypack:a=3", "mypack:b=1"]` | Dasselbe, nach Gewicht aus dem Weltseed und dem Platz der Zelle ausgelost, dieselbe Welt legt dort also immer dasselbe Grundstück an |
| `"g": "grow"` | Dem gewürfelten Plan überlassen, der solche Zellen füllt und sich vom Plan aus nach außen ausbreitet |
| `.` oder `open` | Offener Boden, nichts angelegt |

Jeder Plan lost eine der vier Richtungen aus dem Weltseed aus und dreht sich als Ganzes, ein Plan liest sich also von jeder Seite gleich. Straßen werden zuerst angelegt, ein Grundstück, das eine Straße oder ein anderes Grundstück überlappen würde, bleibt mit einer Zeile im Log offen, und ein Grundstücksname, den kein Pack liefert, lässt seine Zelle genauso offen. Der Plan ändert nicht, wie die Teile gestaltet werden: die Straßenschlüssel, `villageBlocks`, die Laternen und das Platzmittelstück gelten wie für eine gewürfelte Stadt.

## Retrogen

*die welt generieren*

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

## Vorgenerierung

*die welt generieren*

Das Land einer Welt im Voraus bauen, damit niemand beim Spielen Chunks generiert: kein Chunk-Lag, eine bekannte Größe auf der Platte und ein einziges Warten am Anfang statt einer ruckelnden ersten Stunde.

Die ersten 12 Chunks um den Spawn werden immer in die Hand genommen, ganz gleich was ein Pack oder die Config sagt, denn genau so viel baut das Spiel selbst, bevor jemand beitritt. `pregenOnNewWorld` legt fest, wie viel weiter gereicht wird, und der Befehl startet einen Lauf von Hand.

`/rdplserver pregen <radius>` baut jeden Chunk innerhalb so vieler Chunks um die Stelle, an der er ausgeführt wird. `status` sagt, wie weit es ist, und `stop` beendet es. Das Land wird beim eigenen Chunk-System des Spiels angefordert, `pregenChunksInFlight` Chunks auf einmal, eine Regionsdatei von 32 mal 32 Chunks nach der anderen, die Regionen in Ringen von der Mitte aus und die Chunks einer ganzen Region entlang einer Hilbert-Kurve, wobei jede Region fertig wird, bevor die nächste beginnt, und kommt beleuchtet und fertig zurück, es gibt also keinen Beleuchtungsdurchgang, der danach zu laufen hätte.

Während ein Durchlauf läuft, wird jeder festgehalten: zum Zuschauer gemacht, an Ort und Stelle gehalten, mit einer pulsierenden Zeile mitten im Bild und dem Fortschritt in der Aktionsleiste, der Himmel ringsum angehalten, jede Kreatur und jede Maschine in jeder Dimension eingefroren, und Zeit und Wetter der Dimension, die gebaut wird, bleiben, wo sie waren. Der Modus, in dem jeder Spieler angekommen ist, wird beim Festhalten auf den Spieler geschrieben, ein Speicherstand mitten im Durchlauf, ein Absturz oder ein erneuter Beitritt lässt also nie jemanden als Zuschauer zurück; am Ende des Durchlaufs gibt er genau den Modus zurück, den er genommen hat, oder den `worldGameMode` des Packs, wenn einer gesetzt ist, bei `hardcore` Überleben. Ein Client mit dem Mod sieht die Sicht während des Haltens vernebelt und danach das Logo einblenden; ein Vanilla-Client sieht das schlichte Halten. Wie weit jede Dimension gebaut wurde, wird in der Welt gespeichert, eine fertige Welt wird also nie noch einmal gebaut. Das Ende wird allen gesagt, bevor die Sicherung der Welt entsteht, und ein Durchlauf, der von der Konsole oder einem Befehlsblock gestartet wurde, meldet seine Zahlen dorthin zurück.

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
    "resetClearsInventory": true,
    "resetClearsExperience": true,
    "spawnChunkRadius": 128,
    "spawnChunkRadii": ["minecraft:overworld=64"],
    "welcomeSays": ["Welcome to Ruby World!", "minecraft:the_nether=Welcome to the Nether!"],
    "saysCard": true,
    "saysIcon": "minecraft:compass",
    "saysColor": "1E2630",
    "saysImage": "rubyworld:textures/gui/card.png"
  }
}
```

### Was erzeugt wird

*vorgenerierung*

| Schlüssel | Was er macht | Warum du ihn setzen würdest |
| --- | --- | --- |
| `pregenOnNewWorld` | Radius in Chunks, der um den Spawn gebaut wird, bevor jemand spielt. 12 ist die Untergrenze, und 0 meint diese Untergrenze statt gar nichts, denn 12 Chunks um den Spawn baut das Spiel ohnehin von sich aus. Höher setzen, um weiter zu reichen als das Spiel | Legt fest, wie weit ein Pack über den Boden hinausreicht, den das Spiel ohnehin baut |
| `pregenDimensions` | Welche Dimensionen gebaut werden, nach Id, der Reihe nach, jede um ihren eigenen Spawn | Den Nether, das Ende oder deine eigenen Dimensionen dazunehmen |
| `pregenAllDimensions` | Jede Dimension, die der Server hält, statt einer Liste, die Oberwelt zuerst und der Rest in Id-Reihenfolge | Packs mit vielen Dimensionen. Die Dimensionen jedes Mods zählen mit, achte also auf die Größe |
| `pregenDimensionsWhenEntered` | Diese werden gebaut, wenn zum ersten Mal jemand einen Fuß hineinsetzt, und halten dabei wieder alle fest, bis es fertig ist | Dimensionen, die die meisten Spieler nie besuchen; wer nie hingeht, zahlt nichts |
| `pregenToBorder` | Füllt jede Dimension bis zu ihrer Weltgrenze statt bis zu einem Radius, zentriert auf die Grenze | Begrenzte Welten |
| `pregenBorderLimit` | Wie weit eine Grenze reichen darf, in Chunks je Richtung, bevor der Durchlauf abgelehnt wird. Nur Config, nie ein Pack-Schlüssel | Ein Schutz gegen einen ausufernden Durchlauf; erhöhe ihn nur, wenn du weißt, wie viel Zeit und Plattenplatz du damit erlaubst |

Lass ihn vor der Auslieferung einmal selbst durchlaufen, mit dem Radius, den du ausliefern willst, von Anfang bis Ende. Die Zahl der Chunks wächst mit dem Quadrat des Radius: 63 in jede Richtung sind sechzehntausend Chunks, 500 sind über eine Million, der Region-Ordner deiner Testwelt und die tatsächlich verstrichene Zeit sind also die ehrlichen Zahlen, die du den Spielern nennen kannst. Liefere keinen Radius aus, der nie durchgelaufen ist.

### Wie ein Lauf abläuft

*vorgenerierung*

| Schlüssel | Was er macht | Warum du ihn setzen würdest |
| --- | --- | --- |
| `pregenResume` | Ein gestoppter oder unterbrochener Durchlauf macht dort weiter, wo er aufgehört hat. Dimension, Mittelpunkt und Radius des Durchlaufs werden beim Start in den Spielstand geschrieben und der bisherige Stand alle zehn Sekunden, ein Absturz, ein Stromausfall oder ein Beenden mitten im Durchlauf setzen beim nächsten Laden also auf etwa zehn Sekunden genau dort wieder an, wo sie gestorben sind. Ein absichtlich gestoppter Durchlauf, per Befehl oder durch den Stillstand-Watchdog, bleibt gestoppt | Lange Durchläufe auf Servern; kleine Durchläufe starten auch ohne das billig neu |
| `pregenChunksInFlight` | Wie viele Chunks der Durchlauf beim Spiel auf einmal anfordert. Mehr hält die Generierungs-Threads beschäftigter und den Server weniger ansprechbar für die, die gehalten zusehen | Auf einem leeren Server hoch, auf einem, auf dem Leute spielen, runter |

### Was Spieler sehen

*vorgenerierung*

| Schlüssel | Was er macht | Warum du ihn setzen würdest |
| --- | --- | --- |
| `pregenRunningSays`, `pregenFinishedSays`, `pregenStoppedSays` | Die Nachrichten für die einzelnen Phasen. Die erste darf `%d` für den Prozentwert und dahinter `%s` für den Namen der Dimension enthalten, oder `%1$d` und `%2$s`, um sie in beliebiger Reihenfolge zu setzen. Auf ihren Standardwerten sprechen sie die Sprache jedes Spielers | Formulier sie im Ton deines Packs, nenne die Dimension, wenn mehrere gebaut werden, oder stell sie stumm |
| `pregenSpectatingSays` | Die Haltezeile mitten im Bild, während Land gebaut wird. Auf dem Standardwert spricht sie die Sprache jedes Spielers; leer zeigt nichts | Halte sie unter etwa fünfunddreißig Zeichen, sonst schneiden kleine Fenster sie ab |
| `pregenLogo` | Wo das Logo steht, wenn die Vorgenerierung fertig ist: `left`, `center` oder `right`, über dem Text in der Bildmitte, ein paar Sekunden lang, dann blendet es mit dem Nebel aus | Es wird immer gezeigt; ein unbekanntes Wort gilt als `center` |
| `welcomeSays` | Die grüne Begrüßung, gezeigt bei jedem Login und nach der Vorgenerierung. Ein bloßer Eintrag ist die Zeile für überall; ein Eintrag `dimension=nachricht` überschreibt sie für diese Dimension und begrüßt außerdem jede Ankunft dort, z. B. `"minecraft:the_nether=Welcome to the Nether!"`. Die Dimension darf auch als `0`, `-1` oder `1` aus 1.12.2 geschrieben sein, und wer ankommt, während Land gebaut wird, wird nicht begrüßt. Eine leere Nachricht nach dem `=` stellt diese Dimension stumm; eine leere Liste zeigt nichts. Auf dem Standardwert spricht sie die Sprache jedes Spielers | Eine bloße Zeile nennt dein Pack; mit Dimensionszeilen gibst du jeder Welt ihr Thema. Halte die Zeilen unter etwa fünfunddreißig Zeichen |
| `saysCard` | Zeigt die Zeilen, die dieser Mod sagt, die Begrüßung, den Hinweis zum Landbau für Spieler, die mitten im Lauf beitreten, und das Ende des Laufs (der laufende Fortschritt bleibt in der Aktionsleiste) sowie die Bedrohungszeilen, als Karte unten rechts statt im Chat. Die Karte gleitet herein, bleibt acht Sekunden und verblasst, und erscheint auch über einem offenen Bildschirm | Schalte es ein, wenn der Chat voll ist oder die Zeilen wie ein Teil der Welt wirken sollen statt wie Geplauder |
| `saysIcon` | Ein Item, das auf der Karte gezeichnet wird, z. B. `minecraft:compass`. Leer zeichnet keines | Gib der Karte das Wappen deines Packs |
| `saysColor` | Die Hintergrundfarbe der Karte als Hex, z. B. `1E2630`. Leer nimmt ein dunkles Schiefergrau | Passe sie an die Palette deines Packs an |
| `saysImage` | Ein PNG aus den Client-Assets des Packs, z. B. `rubyworld:textures/gui/card.png`, über die Karte gestreckt als ihr Hintergrund und über die Farbe gezeichnet. Leer zeichnet keines | Gib der Karte eine gemalte Tafel; halte das Bild breit und flach, es wird auf das gestreckt, was der Text braucht |

### Sicherung und Kartenreset

*vorgenerierung*

| Schlüssel | Was er macht | Warum du ihn setzen würdest |
| --- | --- | --- |
| `pregenBackup` | Kopiert die Welt in eine unberührte Sicherung, sobald die Vorgenerierung fertig ist und die Spieler noch gehalten werden. Die Generierung wird damit einmal bezahlt: ein späterer Reset oder eine neue Welt mit demselben Pack und Seed stellt die Kopie wieder her, statt erneut zu generieren, was weit schneller ist als zweimal vorzugenerieren. Die Kopie liegt außerhalb des Spielstands, unter `rdpl-pristine/<welt>` daneben, damit die Sicherungen anderer Mods sie nicht mit einsammeln und sie nicht in einem von ihnen verwalteten Ordner auftaucht. Eine Kopie, deren Packs nicht mehr zu den geladenen passen, wird weggeworfen und aus der vorliegenden Welt neu angelegt, ein Packwechsel setzt also nie auf die Karte eines anderen zurück | `false` |
| `pregenBackupSays` | Die Zeile in Bildschirmmitte, die Spielern während dieser Kopie gezeigt wird, mit dem Prozentsatz dahinter. Leer zeigt nichts und die Kopie wird still gemacht | `Pack requested world backup` |
| `resetSays` | Die Zeile in Bildschirmmitte, die Spielern gezeigt wird, während `/rdplserver reset` oder ein Rundenende die Karte zurücksetzt. Leer setzt still zurück | `Pack requested map reset` |
| `resetSendsTo` | Wohin ein Reset die Spieler setzt: `spawn`, eine Position als `x,y,z` oder `x,z`, wobei die Höhe eins über dem Meeresspiegel liegt, oder eines davon hinter `dimension:`, um sie in eine andere Welt zu schicken, die Dimension als Id oder als `0`, `-1` oder `1` aus 1.12.2, womit ein Reset alle in eine Lobby statt zurück in die Arena bringt | `spawn` |
| `resetRuns` | Eine Funktion, die läuft, nachdem ein Reset die Karte geräumt hat, benannt `namespace:pfad`. Sie baut die Arena wieder auf, denn ein Pack, das seine Karte aus einer Funktion gemacht hat, kann sie einfach ein zweites Mal laufen lassen. Leer führt nichts aus | leer |
| `resetClearsEntities` | Entfernt jede Entity, die kein Spieler ist. Mobs, liegende Gegenstände und Erfahrung verschwinden, was die Karte so zurücklässt, wie sie begann | `true` |
| `resetClearsScores` | Setzt jedes Ziel, das das Pack führt, wieder auf nichts, sodass eine neue Partie bei null beginnt. Die Teams selbst bleiben | `true` |
| `resetClearsInventory` | Leert das Inventar jedes Spielers, Rüstung und Zweithand eingeschlossen, sodass eine Runde mit dem beginnt, was die Karte ausgibt, und nicht mit dem, was die letzte übrig ließ. Die `gives` einer Seite werden gleich danach erneut ausgegeben | `false` |
| `resetClearsExperience` | Setzt die Erfahrung jedes Spielers auf Stufe null zurück | `false` |
| `spawnChunkRadius` | Wie weit vom Spawnpunkt, in Blöcken, Chunks geladen gehalten werden, ob ein Spieler da ist oder nicht, gerundet auf ganze Chunks: `(Blöcke + 8) / 16` in jede Richtung, der Standard `128` hält also 8. Beim Start einer Welt bereitet die Oberwelt ein Quadrat vor, das in jede Richtung 4 Chunks größer ist, bevor der Server bereit ist. `0` bereitet keine vor und hält keine. RDPL hält sie mit eigenen Chunk-Tickets, auf 1.21.1 bewirkt die Spielregel `spawnChunkRadius` daher nichts, solange dieser Schlüssel gilt | Eine Maschine oder eine Farm am Spawn am Laufen halten, oder die Spawn-Chunks mit `0` abschalten |
| `spawnChunkRadii` | Ein Radius für die Oberwelt, geschrieben als `dimension=blöcke`, etwa `minecraft:overworld=64`, der `spawnChunkRadius` überschreibt. Nur die Oberwelt hat Spawn-Chunks, ein Eintrag für eine andere Dimension ändert also nichts | Den Spawnbereich in einem Pack festlegen, das seine Radien nach Dimension setzt |

---

# Spielmodi

## Welt-Intro

*spielmodi*

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

### Seiten

*welt-intro*

| Schlüssel | Pflicht | Wert | Standard | Was er macht |
| --- | --- | --- | --- | --- |
| `mode` | nein | `scroll` oder `static` | `scroll` | Text, der läuft, oder Text, der steht, bis der Spieler weiterklickt |
| `text` | nein | Pfad zu einer `.txt`-Datei | keiner | Die Worte. Für eine Seite aus reinen Bildern lässt du ihn weg |
| `background` | nein | Texturpfad | der gekachelte Erdhintergrund | Ein Hintergrund |
| `backgrounds` | nein | Liste von Texturpfaden | keine | Mehrere, im Wechsel. Kommt zu `background` dazu, wenn du beides angibst |
| `interval` | nein | Sekunden | `5.0` | Wie lange jeder Hintergrund steht, wenn es mehr als einen gibt |
| `time` | nein | Sekunden | wird aus dem Text errechnet | Wie lange eine laufende Seite von Anfang bis Ende braucht. Auf einer stehenden Seite, oder auf der letzten Seite jeder Art, ist es die Zeit, bis die Seite von selbst weitergeht, und ohne warten sie auf den Knopf |
| `direction` | nein | `up` oder `down` | `up` | In welche Richtung der laufende Text zieht |
| `textScale` | nein | Zahl | `1.0` | Multipliziert die Schriftgröße. Eine `static`-Seite bricht ihren Text auf die Breite des Bildschirms um, abzüglich eines Rands zu beiden Seiten, und würde er dann noch unter die Knöpfe laufen, wird er kleiner gezeichnet, bis zur Hälfte, bis er passt |
| `settle` | nein | boolean | `false` | Endet mit der letzten Zeile in der Mitte, statt ganz aus dem Bild zu laufen |

### Text und Zeit

*welt-intro*

Textdateien liegen unter `assets/<namespace>/texts/*.txt`. Reiner Text, ein Absatz pro Zeile, und Leerzeilen bleiben Leerzeilen. `PLAYERNAME` wird durch den Namen des Spielers ersetzt, dieselbe Ersetzung, die auch das Vanilla-Endgedicht nutzt.

`time` legt fest, wie lange die Seite dauert, dieselbe Seite braucht also gleich lang, ob eine Zeile darauf steht oder zwanzig. Die Lesegeschwindigkeit stellst du darüber ein, wie viel du auf die Seite packst. Lässt du `time` weg, läuft die Seite so schnell wie der Vanilla-Abspann, wo mehr Text einfach länger dauert.

### Wie es abläuft

*welt-intro*

Eine laufende Seite geht zur nächsten über, wenn ihre Zeit um ist. Die letzte Seite geht nie von selbst weiter, sie wartet. Unten stehen **Next Page** und **Skip All**, auf der letzten Seite ein einzelnes **Continue to World**. Escape tut dasselbe wie Skip All. Statische Seiten zentrieren jede Zeile. Laufende Seiten halten sich an eine feste Spalte, so wie der Abspann.

Im Einzelspieler pausiert die Welt hinter dem Intro, es schleicht sich also nichts an den Spieler heran, während er liest. Die einzige Ausnahme ist Land, das beim Öffnen des Intros noch gemacht wird: dann läuft das Machen hinter den Seiten weiter, und der Spieler bleibt als Zuschauer festgehalten, bis er in die Welt weitergeht, auch wenn der Lauf vorher fertig wird. Auf einem Server läuft die Welt weiter, und ein Vanilla-Client sieht das Intro überhaupt nicht und tritt ganz normal bei. Der Willkommensgruß wartet, bis die Seiten geschlossen sind, damit er nicht hinter ihnen verloren geht.

`once` wird in den Spielerdaten gespeichert und übersteht den Tod. `/rdplserver intro` setzt es für den zurück, der ihn ausführt, das Intro läuft dann beim nächsten Beitritt wieder. Es wird nicht sofort noch einmal abgespielt, damit es kein Weg zurück in die Einstiegssequenz mitten im Spiel wird.

Hintergründe werden auf die Fenstergröße gezogen, ein 16:9-Bild passt also zu einem 16:9-Fenster, ein quadratisches sieht gestaucht aus. Schneid das Bild passend zu, statt dich auf die Anpassung zu verlassen. `music` nimmt jedes registrierte Sound-Event, von Vanilla oder aus deinem eigenen Pack über `sounds`. Es läuft nicht in Schleife, ein kurzes Stück ist also irgendwann zu Ende und lässt Stille zurück.

Bringt mehr als ein Pack ein Intro mit, laufen ihre Seiten in Pack-Reihenfolge hintereinander, statt dass eines gewinnt. Sperr sie mit `requires`, wenn du nur eines willst.

## Teams

*spielmodi*

`<namespace>/teams/*.json`

Der Dateiname ist deine Sache, gelesen wird nur der Ordner, und mehrere Dateien stapeln sich. Jede Datei ist eine Seite.

Eine Seite ist ein echtes Team auf dem Scoreboard des Spiels, also sieht `/team list` sie, sie behält ihre Mitglieder über Speichern und Neuladen, und ein Client ohne diesen Mod zeigt Farben und Namensschilder wie bei jedem Vanilla-Team. Die Mitgliedschaft läuft über den Namen, also kann alles mit Namen oder UUID auf einer Seite stehen: ein Spieler, ein Zombie, ein Dorfbewohner, ein Rüstungsständer.

```json
{
  "name": "red",
  "displayName": "Red Team",
  "color": "red",
  "friendlyFire": false,
  "joinable": false,
  "entities": ["mypack:zombie_a", "mypack:sapper_a"],
  "picks": 0,
  "picksFrom": ["players"],
  "gives": ["minecraft:iron_pickaxe", { "item": "minecraft:bread", "count": 8 }],
  "standIn": { "entity": "mypack:herobrine", "at": "23,31,0" },
  "leadRuns": "mypack:lead_chosen"
}
```

### Die Seite

*teams*

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `name` | Text | der Dateiname | Der Name des Teams auf dem Scoreboard, 1 bis 16 Zeichen. Damit arbeiten `/team` und die anderen Dateien |
| `displayName` | Text | der Name | Was Spielern statt des Namens gezeigt wird |
| `color` | Text | `white` | Eine der sechzehn Textfarben. Sie färbt das Namensschild und ist der Schlüssel für die Team-Sidebars |
| `prefix` | Text | leer | Wird vor den Namen eines Mitglieds gesetzt, nach der Farbe |
| `suffix` | Text | leer | Wird hinter den Namen eines Mitglieds gesetzt |
| `scoreboard` | Wahrheitswert | `true` | Ob die Seite als Team auf dem Scoreboard des Spiels steht. Aus stellt gar kein Team auf: Ihre Mobs tragen stattdessen die Farbe der Seite im Namen, nichts hält sie davon ab, einander anzugreifen, und es landen keine Punkte auf ihr, denn gewertet wird nach dem Team |

Eine Seite wird nur aufgestellt, wo ein Pack danach fragt: ohne `teams`-Ordner legt der Mod kein Team an, lauscht auf nichts und bietet den Befehl nicht an. Ein Serveroperator, der eine Datei ändert, kann `/rdplserver reload` ausführen und die Änderung ohne Neustart in die laufende Welt bringen.

### Kampf und Sichtbarkeit

*teams*

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `friendlyFire` | Wahrheitswert | `false` | Ob Mitglieder einander verletzen können. Zugleich der Vorgabewert von `mobFriendlyFire` |
| `mobFriendlyFire` | Wahrheitswert | `friendlyFire` | Ob die Mobs einer Seite ihre eigene Seite mit Explosionen und geworfenem TNT verletzen können, was das Spiel allein nie unterbindet. Aus bewahrt die Seite; an lässt es, wie das Spiel es hat |
| `seeFriendlyInvisibles` | Wahrheitswert | `true` | Ob Mitglieder einander sehen, während sie unsichtbar sind |
| `nameTags` | Text | `always` | `always`, `never`, `hideForOtherTeams` oder `hideForOwnTeam`, in beliebiger Groß- und Kleinschreibung |
| `deathMessages` | Text | `always` | Dieselben vier Wörter, dafür wer erfährt, dass ein Mitglied stirbt |
| `collision` | Text | `always` | `always`, `never`, `pushOtherTeams` oder `pushOwnTeam`, in beliebiger Groß- und Kleinschreibung |

### Wer beitritt

*teams*

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `entities` | Liste | leer | Entity-Ids, deren Spawns dieser Seite beitreten, etwa `minecraft:zombie` oder eine eigene |
| `players` | Liste | leer | Spielernamen, die dieser Seite beim Einloggen beitreten |
| `spawnBox` | Liste | keiner | Sechs ganze Zahlen, x y z bis x y z. Alles, was darin spawnt, tritt bei, und die Ecken dürfen in beliebiger Reihenfolge stehen |
| `joinable` | Wahrheitswert | `true` | Ob ein Spieler mit `/rdplserver team join` beitreten darf. Auf false für eine Seite, die nur für Mobs ist |
| `balance` | Wahrheitswert | `false` | Ob `/rdplserver team join` ohne Namen einen Spieler hierher setzen darf. Unter den Seiten, die das erlauben, wird die mit den wenigsten Spielern gewählt |
| `picks` | Zahl | `0` | Wie viele Mitglieder diese Seite zufällig zieht. Bei jedem Rundenbeginn lässt die Seite ihre letzte Ziehung dorthin zurück, wo sie stand, und zieht neu aus allem, was `picksFrom` nennt; zwischen den Ziehungen besetzt ein Login oder ein Spawn aus diesem Pool einen leeren Platz sofort. Ein Spieler aus allen, auf einer eigenen Seite, ist der Zweck |
| `picksFrom` | Liste | leer | Woraus gezogen wird: `players` für alle, die online sind, und Entity-Ids für jeden lebenden Mob dieser Art |
| `standIn` | Objekt | keins | Ein Mob, der die Seite hält, solange kein Spieler darauf ist: `{ "entity": "mypack:herobrine", "at": "23,31,0" }` hält einen davon an dieser Stelle der Oberwelt am Leben, beschwört ihn, wenn er fehlt, und entfernt ihn, sobald ein Spieler der Seite beitritt, sodass ein Spiel gegen die KI läuft, bis ein Spieler die Rolle übernimmt. Alle fünf Sekunden geprüft; die Stelle muss in geladenem Gelände liegen. In einem Spiel mit Lobby (`opens.by: leader`) wird ein Stellvertreter nur beschworen, solange die Lobby wartet, und wenn die Runde beginnt, sodass einer, der fällt, für den Rest der Runde und ihr Ende fort bleibt, bis alle wieder in der Lobby sind; ohne Lobby wird ein gefallener Stellvertreter nicht ersetzt, solange eine Runde läuft, die mit `ends.lastStanding` endet |

Drei Wege beizutreten, und eine Seite darf alle nutzen. `entities` nennt Entity-Ids, und alles dieser Art tritt beim Spawnen bei, so gibt ein Pack Mobs ihre Seite, ohne die Mobs anzufassen. `spawnBox` beansprucht eine Ecke der Welt, und alles, was darin spawnt, tritt bei, was zu einer Arena passt, in der beide Seiten denselben Mob nutzen. `players` nennt Spieler direkt. Darüber hinaus tritt ein Spieler mit `/rdplserver team join <name>` bei, sofern die Seite `joinable` nicht auf false setzt, und verlässt sie mit `/rdplserver team leave`.

### Startausrüstung und Startpunkt

*teams*

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `gives` | Liste | leer | Gegenstände, die einem Spieler beim Beitritt zur Seite ins Inventar gelegt werden, ein Gegenstandsname für einen oder `{ "item", "count", "unbreakable" }` für mehrere oder für einen, der sich nie abnutzt, in einen freien Platz und vor die Füße geworfen, wenn keiner frei ist. Nach einem Reset, der Inventare leert (`resetClearsInventory`), werden sie erneut ausgegeben |
| `spawn` | Text | keiner | `x,y,z` in der Oberwelt, wohin die Spieler der Seite gesetzt werden, wenn eine Runde beginnt, sodass jede Seite auf eigenem Boden startet; ohne bleiben sie, wo Reset oder Lobby sie ließen |

### Die Führung

*teams*

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `lead` | Text | `none` | Wie die Führung der Seite bestimmt wird: `none`, `first` für den, der von den Anwesenden der Seite am frühesten beitrat, sodass sie in der Reihenfolge des Beitritts weitergeht, solange einer fort ist, und mit ihm zurückkommt; es wird ihm gesagt, wenn er ankommt, nach dem Intro und jedem Halten, und erneut, wenn sie auf ihn übergeht, `topScore` für den Höchsten auf dem Ziel, das `leadOn` nennt, `appointed` für den Spieler, den `leadIs` nennt, `vote` für den, den die Mitglieder wählen, oder `claim` für den, der sie zuerst beansprucht. Eine Führung ist ein Etikett und eine Farbe und sonst nichts: sie verleiht keine Macht, also zerbricht nichts, wenn eine Führung sich ausloggt |
| `leadOn` | Text | leer | Bei `topScore` das Ziel, nach dem die Mitglieder geordnet werden. Es wird bei jedem Lesen neu ermittelt und folgt damit dem Punktestand |
| `leadIs` | Text | leer | Bei `appointed` der Spieler, der führt |
| `leadSays` | Text | `You are the current round leader` | Einem Spieler gesagt, wenn die Führung an ihn kommt: wenn er auf einer Seite ankommt, die er führt, wenn er sie beansprucht, oder wenn eine `first`-Führung auf ihn übergeht, dann mit dem Namen dessen, der ging. `{side}` ist der Anzeigename der Seite; leer sagt nichts |
| `leadRuns` | Text | leer | Eine Funktion, `namespace:pfad`, die jedes Mal einmal läuft, wenn die Führung an einen Spieler geht: die erste Führung und jede Übergabe danach. Sie läuft als die Führung, an deren Position, mit der Berechtigung einer Funktion, die ein Fortschritt als Belohnung ausführt, also ist `@s` die Führung. Jede Sekunde geprüft; für eine Führung, die offline ist, läuft sie, sobald sie wieder da ist. Ein Neustart bestimmt die Führung neu |

## Wertung

*spielmodi*

`<namespace>/scoring/*.json`

Der Dateiname ist deine Sache, gelesen wird nur der Ordner, und mehrere Dateien stapeln sich. Jede Datei ist ein Ziel.

Ein Ziel ist ein echtes Ziel auf dem Scoreboard des Spiels, also liest `/scoreboard players list` es und es behält seine Punkte über das Speichern. `criterion` ist das, was das Spiel selbst zählt: `dummy` für einen Punktestand, den nur dieses Pack bewegt, oder `deathCount`, `playerKillCount`, `totalKillCount`, `health`, `air`, `armor`, `food`, `level`, `xp`, `trigger` oder jede Statistik, geschrieben, wie `/scoreboard` sie nimmt, etwa `minecraft.custom:minecraft.jump`. Eine Statistik aus 1.12.2 wie `stat.jump` wird als die gelesen, die daraus wurde.

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
  "opens": { "by": "leader", "lobby": "0,64,0" },
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

### Das Ziel

*wertung*

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `name` | Text | der Dateiname | Der Name des Ziels auf dem Scoreboard, 1 bis 16 Zeichen |
| `displayName` | Text | der Name | Was Spielern statt des Namens gezeigt wird |
| `criterion` | Text | `dummy` | Was das Spiel selbst zählt. Ein unbekanntes wird mit einer Zeile abgelehnt, die das sagt |
| `display` | Text | leer | `sidebar`, `list`, `belowName` (`below_name` geht auch) oder `sidebar.team.<color>`. Leer zeigt es nirgends; einen Scoreboard-Bildschirm zum Öffnen gibt es nicht |
| `render` | Text | das des Kriteriums | `integer` oder `hearts` |
| `teamTotals` | Wahrheitswert | `true` | Punkte landen auf einer Zeile mit dem Namen des Teams des Mitglieds |
| `individuals` | Wahrheitswert | `false` | Punkte landen zusätzlich auf einer Zeile für das Mitglied selbst |
| `carries` | Wahrheitswert | `false` | Das Ziel überlebt einen Kartenreset, statt mit ihm gelöscht zu werden. Eine Partiewertung der Rundensiege ist eines |
| `awardsTo` | Text | leer | Ein anderes Ziel, dem dieses beim Ende einen Punkt gibt, an die führende Seite. Bei Gleichstand gibt es nichts |

### Punkte

*wertung*

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `points.kill` | Objekt | leer | Entity-Id zu Punkten, der Seite des Tötenden gutgeschrieben. `minecraft:player` wertet einen Spielerkill |
| `points.death` | Zahl | `0` | Punkte, wann immer ein Mitglied stirbt, gleich woran. Darf negativ sein |
| `points.ownKill` | Zahl | `0` | Punkte für einen Kill der eigenen Seite, anstelle des `kill`-Werts. 0 wertet ihn nicht; eine negative Zahl ist eine Strafe |

`points` ist das, was dieser Mod über das hinaus zählt, was das Spiel zählt, eingespeist in dasselbe Ziel, sodass `/scoreboard` es weiterhin liest. `kill` ist so viele Punkte je getöteter Entity-Id wert, der Seite des Tötenden gutgeschrieben; `death` ist so viele Punkte wert, wann immer ein Mitglied einer Seite stirbt, und darf negativ sein. Mit `teamTotals` landen die Punkte auf einer Zeile mit dem Namen des Teams, und genau das lässt die Sidebar vier Seiten zeigen statt einer Zeile je Mob. `individuals` fügt zusätzlich eine Zeile je Mitglied hinzu und ist standardmäßig aus, weil eine Zeile je Mob-UUID sich als Rauschen liest.

### Wie eine Runde endet

*wertung*

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `ends.atScore` | Zahl | `0` | Die Partie endet in dem Moment, in dem eine Seite dies erreicht. 0 endet nie über Punkte |
| `ends.afterMinutes` | Zahl | `0` | Die Partie endet nach so vielen Minuten. 0 endet nie über die Zeit |
| `ends.afterRounds` | Zahl | `0` | Für ein Ziel, dem ein anderes per `awardsTo` zuspielt: Die Partie endet, sobald insgesamt so viele Runden vergeben wurden, wer immer sie gewann. 0 endet nie über Runden |
| `ends.lastStanding` | Wahrheitswert | `false` | Die Runde endet, wenn nur noch eine Seite steht. Im Spiel sind die Seiten, auf denen beim Rundenbeginn ein Spieler oder ein lebender Mob steht, mindestens zwei; ein Spieler, der stirbt, ist raus und bis zum Rundenende Zuschauer, und eine Seite, deren Spieler alle raus oder fort und deren Mobs alle tot sind, ist gefallen. Die stehende Seite gewinnt die Runde, und `awardsTo` verbucht sie für diese Seite, gleich wie der Punktestand ist. Mit `resets` und `opens.by: leader` geht das Spiel danach zurück in die Lobby. Der `standIn` einer Seite wird während einer solchen Runde nicht neu beschworen |
| `ends.outSays` | Text | `You are out until the round ends` | Was einem ausgeschiedenen Spieler gesagt wird. Leer sagt nichts |
| `ends.locksTeams` | Wahrheitswert | `true` | Wer während einer laufenden Runde einer Seite beitritt, wartet bis zum Rundenende, damit niemand mitten in eine gewertete Runde fällt |

`ends` beendet die Partie, entweder in dem Moment, in dem eine Seite `atScore` erreicht, oder sobald `afterMinutes` vergangen sind. Der Stand wird dann gezeigt, vom Spiel selbst sortiert: als Chat oder als Karte, wenn `results` danach fragt. Einem Spieler ohne diesen Mod wird derselbe Stand als Chatzeilen gesagt, sodass niemand ohne Ergebnis bleibt. Mit `resets` ist dieses Ende das einer Runde: Die Wertung steht `intermissionSeconds` lang, während eine Abklingzeit in der Aktionsleiste herunterzählt, die Karte wird bis zur Begrüßung zurückgesetzt, und die nächste Runde beginnt nach fünf heruntergezählten Sekunden. `awardsTo` gibt die Runde der führenden Seite, auf einem Ziel, das den Reset mit `carries` überlebt. Ein mit `carries` bewahrtes Ziel kann von sich aus enden, `atScore` für ein Best-of, `afterRounds` für eine feste Zahl, und sein Stand wird beim Reset danach gelöscht, sodass eine neue Partie beginnt.

### Zwischen den Runden

*wertung*

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `ends.resets` | Wahrheitswert | `false` | Das Rundenende setzt die Karte zurück, wie `resetSays` und die übrigen Reset-Einstellungen unter [Weltvorlagen](#weltvorlagen) es beschreiben, dann beginnt eine neue Runde |
| `ends.intermissionSeconds` | Zahl | `10` | Wie lange die Wertung zwischen dem Ende und dem Reset steht |
| `ends.intermissionSays` | Text | `Round cooldown {seconds}` | Jede Sekunde der Pause nach einem Rundenende in der Aktionsleiste gezeigt, `{seconds}` zählt bis zum Reset herunter. Leer zeigt nichts |
| `ends.startsSays` | Text | `Round starting in {seconds}` | In der Aktionsleiste gezeigt, während die fünf Sekunden nach dem Reset die nächste Runde einleiten, `{seconds}` zählt herunter. Leer zeigt nichts |

### Die Lobby

*wertung*

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `opens.by` | Text | `auto` | `auto` eröffnet die nächste Runde von selbst, fünf Sekunden nach dem Reset. `leader` hält das Spiel stattdessen in einer Lobby: Nach dem Reset, und beim ersten Laden der Welt, wird nichts gewertet und keine Uhr läuft, Seiten können frei betreten und verlassen werden, und die Runde beginnt erst, wenn die Führung einer Seite, oder ein Operator, `/rdplserver round start` ausführt, und nicht, solange noch jemand das Welt-Intro liest; dann läuft der Fünf-Sekunden-Zähler, die Ziehungen werden gemacht, und jede Seite kommt an ihr `spawn`. Solange die Welt wartet, bis der Fünf-Sekunden-Zähler endet, bleiben Spieler, wo sie stehen, können nichts abbauen, setzen, benutzen, schlagen oder fallen lassen und nehmen keinen Schaden, wobei ihnen beim Versuch die Wartezeile gezeigt wird, und alles andere Lebende steht still: keine KI, keine Bewegung. Befehle funktionieren weiter, also können Seiten betreten und die Runde gestartet werden |
| `opens.says` | Text | `Waiting for {leader} to start the round` | Mitten auf dem Bildschirm eingeblendet, wie der Willkommensgruß, für jeden Spieler, der nicht führt: wenn er in der Lobby ankommt, nach dem Intro und sobald der Gruß gezeigt wurde; wenn die Lobby nach einer Runde wieder aufgeht; sobald er sich ändert, weil eine Führung kommt oder geht; und wenn er etwas versucht, das die Lobby verweigert. `{leader}` sind die Führungen aller Seiten oder `a leader`, solange niemand führt. Leer zeigt nichts |
| `opens.leaderSays` | Text | `Type /rdpl round start` | Auf dieselbe Weise und zu denselben Momenten einem Spieler eingeblendet, der eine Seite führt, anstelle von `opens.says`. Leer zeigt nichts |
| `opens.lobby` | Text | keiner | `x,y,z` in der Oberwelt oder `dimension:x,y,z` in einer anderen Welt, etwa `minecraft:the_nether:0,64,0`, wo alle warten, solange die Lobby hält: Jeder Spieler und jeder lebende Mob auf einer Seite wird auf einen Ring um diese Stelle gestellt, jeder zur Mitte gewandt, sodass sie einander anstarren. Jeder bekommt einen Bogen, so breit wie er selbst plus zwei Blöcke, sodass sich keiner mit einem anderen überschneidet, und der Ring wächst, wenn mehr ankommen; er wird neu aufgestellt, sobald jemand hinzukommt oder geht. Die Höhe ist der Boden, auf dem sie stehen, gefunden innerhalb von drei Blöcken in beide Richtungen. Spieler und Mobs wechseln direkt in diese Welt und zurück, ohne dass ein Portal gebaut wird. Wenn die Runde beginnt, gehen Spieler an das `spawn` ihrer Seite, und ein Mob, der noch steht, wird zurückgestellt, wo er war, in seiner eigenen Welt |
| `opens.lobbyJoins` | boolean | `false` | Stellt einen Spieler, der mitten in der Runde beitritt, als Zuschauer in die Lobby, bis die Runde endet, statt dorthin, wo er sich abgemeldet hat. Braucht `opens.lobby` |
| `opens.joinsSays` | Text | `Round is in progress, you can join after it ends` | Was ihm gesagt wird. Leer sagt nichts |

### Eine Runde zurücksetzen

*wertung*

```json
{
  "name": "wall",
  "opens": { "by": "leader" },
  "ends": { "lastStanding": true, "resets": true },
  "reset": {
    "lead": "now",
    "players": "vote",
    "teams": ["miners", "raiders"],
    "passPercent": 51,
    "voteSeconds": 30,
    "cooldownSeconds": 60
  }
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `reset.lead` | Text | `none` | Was `/rdplserver round reset` für die Führung einer Seite tut, während eine Runde läuft. `now` beendet die Runde sofort und setzt die Karte zurück; `vote` ruft stattdessen eine Abstimmung aus; `none` gibt der Führung kein eigenes Recht, sodass sie wie jeder andere Spieler eine Abstimmung ausruft, wo `players` das erlaubt. Ein Operator setzt immer sofort zurück |
| `reset.players` | Text | `none` | `vote` lässt einen Spieler jeder Seite mit `/rdplserver round reset` eine Abstimmung ausrufen. `none` überlässt das Zurücksetzen der Führung |
| `reset.teams` | Liste | leer | Die Seiten, deren Spieler eine Abstimmung ausrufen dürfen. Leer sind alle Seiten |
| `reset.passPercent` | Zahl | `51` | Der Anteil der Abstimmenden, 1 bis 100, der mit Ja stimmen muss, damit die Runde zurückgesetzt wird. `51` ist mehr als die Hälfte, `100` sind alle |
| `reset.voteSeconds` | Zahl | `30` | Wie lange eine Abstimmung läuft, mindestens fünf Sekunden. Sie schließt früher, sobald ihr Ausgang feststeht |
| `reset.cooldownSeconds` | Zahl | `60` | Wie lange nach einer gescheiterten Abstimmung keine neue ausgerufen werden kann. Eine Führung mit `now` hält das nicht auf |
| `reset.leadSays` | Text | `{player} reset the round` | Allen gesagt, wenn die Runde sofort zurückgesetzt wird, `{player}` ist, wer sie zurückgesetzt hat. Leer sagt nichts |
| `reset.voteSays` | Text | `{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds` | Allen gesagt, wenn eine Abstimmung ausgerufen wird, `{player}` ist, wer sie ausgerufen hat. Leer sagt nichts |
| `reset.tallySays` | Text | `Reset the round? {yes} yes, {no} no, {seconds}` | Jede Sekunde einer Abstimmung in der Aktionsleiste gezeigt, `{seconds}` zählt herunter. Leer zeigt nichts |
| `reset.passSays` | Text | `The vote passed, so the round is reset` | Allen gesagt, wenn eine Abstimmung durchgeht. Leer sagt nichts |
| `reset.failSays` | Text | `The vote failed, so the round goes on` | Allen gesagt, wenn eine Abstimmung scheitert. Leer sagt nichts |

Ein Zurücksetzen bricht die Runde ab, wo sie gerade steht. Die Wertung wird unter `The round was reset` gezeigt, niemand bekommt die Runde zugesprochen, die Pause zählt herunter, und die Karte wird zurückgesetzt, als hätte die Runde mit `ends.resets` geendet, zurück in die Lobby, wo `opens.by` auf `leader` steht. Das geht, ob die Runde je von selbst enden würde oder nicht, aber nicht in der Lobby, nicht während des Countdowns, der eine Runde eröffnet, und nicht, wenn eine Runde vorbei ist und ihr Reset schon kommt; eine dann noch laufende Abstimmung wird fallen gelassen.

Jeder Spieler auf einer Seite, der online ist, stimmt ab, gleich auf welcher Seite, mit `/rdplserver round vote yes` oder `no`, und kann seine Stimme ändern, solange sie läuft. Wer die Abstimmung ausruft, hat mit Ja gestimmt, und wer bei Ablauf der Zeit nicht abgestimmt hat, zählt als Nein. In einem Paket mit Seiten ruft ein Spieler ohne Seite weder aus noch stimmt er ab; in einem Paket ohne Seiten tut es jeder Spieler, der online ist. Verwendet wird die erste Wertungsdatei, deren `reset` überhaupt jemanden zurücksetzen lässt.

### Ergebnisse

*wertung*

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `results.card` | Wahrheitswert | `false` | Den Stand als Karte statt als Chat zeigen |
| `results.title` | Text | der Name und `results` | Die Überschrift der Karte |
| `results.icon` | Text | leer | Ein auf der Karte gezeichneter Gegenstand, z. B. `minecraft:tnt` |
| `results.image` | Text | leer | Ein auf der Karte gezeichnetes Bild statt eines Gegenstands |
| `results.background` | Text | ein dunkles Schiefer | Die Hintergrundfarbe der Karte |
| `results.seconds` | Zahl | `8` | Wie lange die Karte steht, mindestens eine Sekunde |

## Raids

*spielmodi*

`<namespace>/raids/*.json`

Der Dateiname ist deine Sache, gelesen wird nur der Ordner, und mehrere Dateien stapeln sich. Jede Datei ist ein Raid.

Ein Raid ist die Art, die das Spiel seit 1.14 hat, ausgetragen um eines der Dörfer des Spiels. Er beginnt, wenn ein Spieler mit dem Effekt `omen` in einem Dorf ist: Der Effekt wird entfernt, und für jeden Spieler innerhalb von `reach` um die Dorfmitte erscheint eine Bossleiste. Nach `waveDelay` Ticks trifft die erste Welle auf einem Ring um das Dorf ein und läuft zur Mitte, wobei sie unterwegs Spieler, Dorfbewohner und Eisengolems angreift. Angreifer verletzen einander nie und nehmen einander nie ins Visier, ein verirrter Pfeil oder Hieb zwischen zweien von ihnen bewirkt also nichts. Die Leiste zeigt die Gesundheit, die der Welle bleibt, und zählt die Angreifer, sobald zwei oder weniger übrig sind. Ist eine Welle besiegt, wartet die nächste `waveDelay` Ticks. Ist die letzte Welle besiegt und zwei Sekunden lang nichts zurückgekommen, ist der Raid gewonnen; sind alle Dorfbewohner tot oder ist das Dorf selbst verschwunden, nachdem eine Welle kam, ist er verloren. So oder so zeigt die Leiste das dreißig Sekunden lang, und die passende Funktion läuft als jeder Spieler in Reichweite.

Ein laufender Raid wird mit der Welt gespeichert, und seine Angreifer nehmen ihren Marsch nach dem Neuladen wieder auf. Er endet ohne Ausgang auf friedlich, nach `timeout` Ticks oder wenn keine Stelle um das Dorf eine Welle aufnehmen kann. Ein Dorf ist, wo das Spiel seine Dorfpunkte führt, die Betten, Arbeitsplätze und Glocken, die Dorfbewohner beanspruchen: Seine Mitte ist die Mitte dieser Punkte, es reicht mindestens 32 Blöcke um sie herum, und seine Dorfbewohner sind die innerhalb dieser Reichweite und bis vier Blöcke über oder unter der Höhe der Mitte. Das spieleigene `minecraft:bad_omen` startet zuerst den Raid des Spiels, also nennt ein Raid einen eigenen Effekt seines Packs.

Solange eine Welle über dem Dorf ist, laufen seine Dorfbewohner nach Hause und bleiben dort, wie wenn die Glocke des Spiels läutet. Angreifer brechen die Holztüren auf ihrem Weg auf, um an sie heranzukommen, zwölf Sekunden pro Tür, auf normal und schwer, solange `mobGriefing` an ist; Eisentüren halten. Ein Block vom Typ `bell` ist überall, wo er steht, eine Dorfglocke und läutet, wie [Glocken](#glocken) es beschreibt; das `bell` des Raids nennt jeden anderen Block, der als Glocke läuten soll. Jede Glocke im Dorf läutet, wenn eine Welle eintrifft, und ein genannter Block läutet auch, wenn ein Spieler ihn benutzt: Dorfbewohner im Umkreis von 48 Blöcken verstecken sich fünfzehn Sekunden lang, und Angreifer im Umkreis von 48 Blöcken leuchten drei Sekunden lang. `minecraft:bell` steht schon in den Dörfern des Spiels und lässt sich nennen, und sie läutet weiterhin auch auf die Art des Spiels; ein eigener Glockenblock eines Packs kommt über eine NBT-Struktur ins Dorf, als Grundstück oder als Platzmittelstück.

```json
{
  "omen": "mypack:bad_omen",
  "name": "Raid",
  "color": "red",
  "waveDelay": 300,
  "spawnDistance": 32,
  "sound": "mypack:raid_horn",
  "wins": "mypack:raid_won",
  "loses": "mypack:raid_lost",
  "bell": "minecraft:bell",
  "waves": [
    [ { "entity": "minecraft:vindicator", "count": 2 }, { "entity": "mypack:raider", "count": { "min": 1, "max": 3 } } ],
    [ { "entity": "minecraft:evoker" }, { "entity": "minecraft:vindicator", "count": 4 } ]
  ]
}
```

### Der Raid

*raids*

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `omen` | Effektname | keiner, Pflicht | Der Effekt, der den Raid startet, wenn sein Träger in einem Dorf ist. Jeder registrierte Effekt geht, auch ein eigener Trank eines Packs |
| `name` | Text | `Raid` | Der Titel der Bossleiste |
| `color` | Text | `red` | Die Farbe der Leiste: `pink`, `blue`, `red`, `green`, `yellow`, `purple` oder `white` |
| `waves` | Liste von Wellen | keine, Pflicht | Jede Welle ist eine Liste von Gruppen, und die Wellen kommen der Reihe nach |
| `waveDelay` | Zahl | `300` | Ticks vor der ersten Welle und zwischen dem Ende einer Welle und der nächsten |
| `spawnDistance` | Zahl | `32` | Wie weit von der Dorfmitte eine Welle eintrifft. Zuerst wird das Doppelte versucht, dann dieser Wert, dann das Dorf selbst |
| `reach` | Zahl | `96` | Spieler innerhalb so vieler Blöcke um die Mitte sehen die Leiste, und die Funktion zum Ausgang läuft als sie. Ein Angreifer, der sechzehn Blöcke darüber hinaus streunt, verlässt den Raid |
| `timeout` | Zahl | `48000` | Ticks, nach denen ein unfertiger Raid ohne Ausgang endet. `0` beendet ihn nie |
| `sound` | Geräuschname | keiner | Wird jedem Spieler in Reichweite von der Seite vorgespielt, aus der die Welle kommt, sobald sie eintrifft |
| `wins` | Funktion | keine | Läuft als jeder Spieler in Reichweite, wenn der Raid gewonnen ist |
| `loses` | Funktion | keine | Läuft als jeder Spieler in Reichweite, wenn der Raid verloren ist |
| `bell` | Blockname oder Liste | keiner | Andere Blöcke, die als Glocke läuten, wenn ein Spieler sie benutzt und immer wenn eine Welle eintrifft. Ein Block vom Typ `bell` läutet, ohne genannt zu sein |

### Eine Gruppe

*raids*

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `entity` | Entity-Name | keiner, Pflicht | Was kommt. Eine Entity-Variante behält ihr ganzes eigenes Verhalten und bekommt den Marsch dazu |
| `count` | Zahl oder `{ "min", "max" }` | `1` | Wie viele kommen |

---

# Steuerung

## Die Steuerungsebene

*steuerung*

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

*steuerung*

Jede Einstellung unten wird über ihre Gruppe gelesen, der `control`-Schlüssel der Gruppe entscheidet also, ob ein Pack oder die Config das letzte Wort hat. Eine Einstellung, die ein Pack setzen darf, steht im `settings`-Block einer Weltvorlage unter demselben Namen; eine mit **nur Config** wird allein aus der Config gelesen, und ein Pack, das sie schreibt, wird angemahnt und ignoriert. Standardwerte sind die der Config.

### Erze

*was jede gruppe macht*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockOres": true,
    "logBlockedOres": true,
    "oreWhitelist": ["minecraft", "mypack"],
    "prospectItems": ["minecraft:compass=iron_vein|coal_seam", "mypack:dowsing_rod=*,12"],
    "prospectItemsAreBlacklist": false,
    "prospectDrops": false,
    "prospectSlow": 2,
    "prospectWear": 2,
    "oreTypes": ["COAL", "IRON"],
    "oreTypesAreBlacklist": true,
    "blockOreDimensions": ["minecraft:overworld", "minecraft:the_nether"],
    "blockOreDimensionsAreBlacklist": false
  }
}
```

`control.ores` entscheidet diese Gruppe. Erzgenerierung nach Mod und nach Erztyp sperren.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `blockOres` | boolean | `false` | Hindert jeden Mod und Minecraft selbst daran, Erz zu generieren. Nur die Mods in oreWhitelist generieren weiter. Ein Erz ist ein Placed Feature, das auf dem Erz- oder verstreuten Erz-Feature des Spiels aufbaut, also denen, für die 1.12.2 sein Erz-Event auslöste; das deckt die Erze von Minecraft und den meisten Mods ab, dazu Erde, Kies und die Gesteinsarten. Die eigenen Worldgen-Einträge eines Packs werden nie blockiert, weder hierdurch noch durch oreTypes |
| `logBlockedOres` | boolean | `true` | Protokolliert, wenn ein Mod und ein Erztyp zum ersten Mal abgewiesen wird |
| `oreWhitelist` | Liste von Mod-Ids | `["minecraft"]` | Die Mods, die trotz `blockOres` weiter Erz generieren dürfen |
| `prospectItems` | Liste | leer | Gegenstände, die nach Worldgen-Einträgen der Form vein schürfen, wenn ein schleichender Spieler mit einem davon einen Block abbaut, als item=eintrag\|eintrag[,radius in chunks] oder item=*[,radius], z. B. minecraft:compass=iron_vein\|coal_seam oder mypack:rod=*,12. Die Lesung nennt das Erz und eine Himmelsrichtung |
| `prospectItemsAreBlacklist` | boolean | `false` | An ist die Liste jedes Gegenstands die Einträge, die er nicht liest |
| `prospectDrops` | boolean | `false` | An droppt ein im Schürfmodus abgebauter Block weiterhin und gibt Erfahrung. Aus ist die Probe verbraucht |
| `prospectSlow` | int, 1 bis 100 | `2` | Wie viele Male länger ein schleichender Spieler mit einem markierten Gegenstand zum Abbau eines Blocks braucht. `1` ist normale Geschwindigkeit |
| `prospectWear` | int, 2 bis 1000 | `2` | Wie viele Male die normale Abnutzung ein Schürfabbau das Werkzeug kostet. `2`, doppelt, ist das Mindeste, und ein Gegenstand ohne Haltbarkeit zahlt nichts |
| `oreTypes` | Liste | leer | Für welche Erztypen das gilt, wer sie auch generiert und was die Whitelist auch sagt. Bekannte Typen: COAL, IRON, COPPER, GOLD, REDSTONE, DIAMOND, LAPIS, EMERALD, QUARTZ, DIRT, GRAVEL, DIORITE, GRANITE, ANDESITE, TUFF, CLAY, SILVERFISH, CUSTOM für jedes andere Erz |
| `oreTypesAreBlacklist` | boolean | `true` | An werden die in `oreTypes` genannten Typen blockiert, aus generieren nur diese Typen |
| `blockOreDimensions` | Liste | leer | Die Dimensionen, in denen Erz blockiert wird, leer heißt jede. Eine Dimension außerhalb wird gar nicht angefasst, die Erze eines anderen Mods generieren dort also unbehelligt, während die Oberwelt blockiert bleibt |
| `blockOreDimensionsAreBlacklist` | boolean | `false` | An sind die genannten Dimensionen die, die in Ruhe gelassen werden |

### Biome

*was jede gruppe macht*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "logBlockedBiomes": true,
    "blockBiomes": true,
    "biomeWhitelist": ["minecraft", "mypack"],
    "biomeNames": ["minecraft:badlands", "minecraft:wooded_badlands"],
    "biomeNamesAreBlacklist": true,
    "blockBiomeDimensions": ["minecraft:overworld"],
    "blockBiomeDimensionsAreBlacklist": false
  }
}
```

`control.biomes` entscheidet diese Gruppe. Biome nach Mod und nach Name sperren, und was sie ersetzt.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `logBlockedBiomes` | boolean | `true` | Protokolliert je Mod, welche Biome blockiert wurden |
| `blockBiomes` | boolean | `false` | Hindert jedes Biom am Generieren außer denen der Mods in biomeWhitelist. Blockierte Biome werden zum Void-Biom oder zu dem, was die roles und der fallback der Weltvorlage nennen. Jedes Biom zu blockieren, während die Weltvorlage void ist, ohne roles und mit void als default, macht die voidWorldDimensions zur Void-Welt |
| `biomeWhitelist` | Liste | `["minecraft"]` | Die Mods, deren Biome trotz `blockBiomes` weiter generieren. Ein Pack-Biom nutzt den Namespace des Packs |
| `biomeNames` | Liste | leer | Biome, für die das gilt, nach Id wie minecraft:birch_forest oder nach dem englischen Namen, den das Spiel zeigt, wie Birch Forest. Als Blacklist werden sie blockiert, wem sie auch gehören. Als Whitelist braucht ein genanntes Biom bei eingeschaltetem blockBiomes trotzdem seinen Mod in biomeWhitelist |
| `biomeNamesAreBlacklist` | boolean | `true` | An werden die Namen in `biomeNames` blockiert. Aus generieren nur diese Namen |
| `blockBiomeDimensions` | Liste | `["minecraft:overworld"]` | Die Dimensionen, in denen Biome blockiert werden. Leer heißt jede |
| `blockBiomeDimensionsAreBlacklist` | boolean | `false` | An überspringt das Blockieren die genannten Dimensionen. Aus gilt es nur für sie |

### Generatoren

*was jede gruppe macht*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockWorldGenerators": true,
    "generatorWhitelist": ["minecraft", "mypack"],
    "blockedGenerators": ["tconstruct"],
    "blockGeneratorDimensions": ["minecraft:overworld"],
    "blockGeneratorDimensionsAreBlacklist": false,
    "generatorTypes": ["ores", "lakes"],
    "generatorTypesAreBlacklist": true,
    "generatorTypeMap": ["mymod=ores", "sky_island=structures"],
    "logBlockedGenerators": true
  }
}
```

`control.generators` entscheidet diese Gruppe. Die Weltgenerierung anderer Mods nach Mod und danach sperren, was sie erzeugt. Ein Generator ist ein platziertes Feature und gehört dem Namespace seiner Id; die eigenen Features von Minecraft, die dieses Mods und die Worldgen-Einträge eines Packs werden nie gesperrt.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `blockWorldGenerators` | boolean | `false` | Hindert jeden Mod daran, über seine eigenen platzierten Features zu generieren – so fügen Mods Schleiminseln, Höhlenkristalle und dergleichen hinzu. Nur die Mods in generatorWhitelist generieren weiter |
| `generatorWhitelist` | Liste von Mod-Ids | `["minecraft"]` | Die Mods, die weiter generieren dürfen, solange `blockWorldGenerators` an ist |
| `blockedGenerators` | Liste von Mod-Ids oder Id-Teilen | leer | Einzelne Generatoren, die rundweg gesperrt werden, was die Whitelist auch sagt, nach Mod-Id oder nach einem Teil der Id eines platzierten Features |
| `blockGeneratorDimensions` | Liste | `["minecraft:overworld"]` | Die Dimensionen, für die das Sperren von Generatoren gilt. Leer heißt jede |
| `blockGeneratorDimensionsAreBlacklist` | boolean | `false` | An überspringt das Sperren die genannten Dimensionen. Aus gilt es nur für sie |
| `generatorTypes` | Liste | leer | Typen, für die das gilt, wem der Generator auch gehört und was die Whitelist auch sagt: `ores`, `structures`, `flora`, `lakes`, `terrain` oder `unknown` für die, auf die nichts passte. Der Typ kommt aus Wörtern in der Id des Features, `crystal_ore` ist also ores und `slime_island` structures |
| `generatorTypesAreBlacklist` | boolean | `true` | An werden die Typen in `generatorTypes` gesperrt. Aus generieren nur diese Typen |
| `generatorTypeMap` | Liste von `muster=typ` | leer | Typen für Generatoren, die die Id nicht beschreibt; das Muster ist eine Mod-Id oder ein Teil einer Feature-Id, z. B. mymod=ores. Zugeordnete Einträge werden vor den eingebauten Wörtern geprüft und korrigieren so auch einen, den die Wörter falsch lesen |
| `logBlockedGenerators` | boolean | `true` | Protokolliert jeden Generator beim ersten Sperren mit dem Typ, den er bekommen hat. `/rdplserver generators` zeigt die laufenden Summen nach Mod und Typ |

### Ersetzungen

*was jede gruppe macht*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockReplacements": ["minecraft:andesite=minecraft:stone", "minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]"],
    "blockReplacementDimensions": ["minecraft:overworld"],
    "blockReplacementDimensionsAreBlacklist": false,
    "blockReplacementMinHeight": -64,
    "blockReplacementMaxHeight": 128,
    "blockReplacementKey": "cleanup_v1",
    "logBlockReplacements": true
  }
}
```

`control.replacements` entscheidet diese Gruppe. Blockersetzung in Chunks, die es schon gibt.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `blockReplacements` | Liste | leer | Blöcke, die beim Laden aus Chunks getauscht werden, geschrieben als block=block mit optionalem Zustand auf beiden Seiten, etwa minecraft:andesite=minecraft:stone oder minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]. Jeder Chunk wird einmal bearbeitet, neue eingeschlossen |
| `blockReplacementDimensions` | Liste | leer | Die Dimensionen, für die das gilt. Leer heißt jede |
| `blockReplacementDimensionsAreBlacklist` | boolean | `false` | An überspringt das Ersetzen die genannten Dimensionen. Aus gilt es nur für sie |
| `blockReplacementMinHeight` | int, -2032 bis 2031 | `-64` | Das niedrigste y, das betrachtet wird |
| `blockReplacementMaxHeight` | int, -2032 bis 2031 | `319` | Das höchste y, das betrachtet wird |
| `blockReplacementKey` | Text | `0000` | Ändere ihn, und jeder Chunk durchläuft das Ersetzen erneut |
| `logBlockReplacements` | boolean | `true` | Protokolliert die erste Ersetzung jeder Art und eine Summe, wenn eine Welt aufholt |

### Dörfer und Städte

*was jede gruppe macht*

`control.villages` entscheidet diese Gruppe. Die Stadt- und Dorfstraßen, die ein Pack anlegt: ihre Form, Ausstattung, Brücken, Tunnel, Gleise, Grundstücke und der Platz.

#### Dorfwege

*dörfer und städte*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villagePathBlock": "minecraft:stone_bricks",
    "villagePathExtraWidth": 1,
    "villageBlockSizes": ["32=3", "64=1"],
    "villageCitySpacing": 4,
    "villagePathAlleyBlock": "minecraft:gravel",
    "villagePathAlleyChance": 25,
    "villagePathMinimumWidth": 0,
    "villagePathFlatRun": 6,
    "villagePlotsLeast": 12,
    "villagePlotsMost": 30,
    "villagePlotsBackRow": true,
    "villageTieStreets": true,
    "villageLayout": "mypack:downtown",
    "villagePathCenterBlock": "minecraft:quartz_block",
    "villagePathCenterDash": 2,
    "villagePathLineBlock": "minecraft:smooth_stone_slab",
    "villagePathSidewalkBlock": "minecraft:stone_bricks",
    "villagePathSidewalkWidth": 2,
    "villagePathLampBlock": "minecraft:iron_bars",
    "villagePathLampHeight": 3,
    "villagePathLampTopBlock": "minecraft:player_head",
    "villagePathLampSideBlock": "",
    "villagePathLampStructure": "",
    "villageWellStructure": ["mypack:plaza_spire=3", "mypack:fountain=1", "empty=1"],
    "villagePathDeadEnds": ["barrier", "sidewalk"],
    "villagePathIntersects": ["mypack:crosswalk"]
  }
}
```

**Blöcke mischen.** Einige Block-Einstellungen nehmen statt eines Blocks eine Mischung: Blöcke durch Kommas getrennt, jeder mit einem Leerzeichen und einem Gewicht dahinter, etwa `"minecraft:stone_bricks 3, minecraft:cobblestone 1"`. Ein Block ohne Gewicht zählt einfach. Jeder gesetzte Block würfelt die Mischung aus dem Welt-Seed und seiner Position aus, dieselbe Welt baut also immer dasselbe Muster. Eine Mischung nehmen `villagePathVergeBlock`, `villagePathVergeWaterBlock`, `villagePathTunnelBlock`, `villagePathBridgeFrameBlock`, `villagePathBridgeFrameTopBlock`, `villageRailTunnelBlock`, `villageRailDeckBlock`, `villageRailSupportBlock`, `villageRailBarrierBlock`, `villageRailBridgeFrameBlock`, `villageRailBridgeFrameTopBlock`, `villageSubwayTunnelBlock`, `villageSubwayPlatformBlock`, `villageSubwayRailingBlock`, `villageSubwayBenchEndBlock` und `villageSewerMossBlock`. Alle anderen Block-Einstellungen nehmen durchgehend den ersten Block einer Mischung.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `villagePathBlock` | Text | leer | Die Wegoberfläche, wenn terrainAdaptation Stadtstraßen legt. Leer behält den Block, den das Biom nehmen würde: Sandstein über Sand, Terrakotta in den Badlands, Trampelpfad über Erde |
| `villagePathExtraWidth` | int, 0 oder mehr | `0` | Zusätzliche Straßenblöcke je Seite über die üblichen 3 hinaus, wenn terrainAdaptation die Straßen legt. Verbreitert die Straßen selbst, sodass die Blocks dazwischen von breiten Straßen zurücktreten |
| `villageBlockSizes` | Liste von `größe=gewicht` | leer | Wie tief die Blocks zwischen den parallelen Straßen einer Stadt sind, je Viertel einmal aus seiner Platzlage gewürfelt. Leer bemisst jeden Block nach dem größten Grundstück, das das Pack mitbringt |
| `villageCitySpacing` | int, 0 bis 256 | `16` | Wie weit auseinander Stadtviertel gesät werden, in Vierteln, deren Größe sich aus den Grundstücken ergibt (zweimal das größte Grundstück, dazu ein Platz und eine Straße je Seite, auf 16 Blöcke aufgerundet, mindestens 96): ein Viertel in jedem Quadrat aus so vielen trägt eine Stadt, und bei 1 ist jedes Viertel eine, ein Platz mit dem Brunnen in der Mitte und Straßen heraus, die an die des nächsten Viertels anschließen. 0 sät keine. Setzt das Pack den Wert nicht, legt ihn `structureSpacing` villages=chunks fest, mindestens 9 Chunks. Ein Quadrat gründet seine Stadt auf seinem flachsten Viertel, mit höchstens 10 Blöcken zwischen höchstem und tiefstem Punkt, in einem Dorfbiom (`structureBiomes` villages= wählt diese aus) und nicht dort, wo ein Waldanwesen entstehen könnte; `structureSeparation`, `structureMinDistanceFromSpawn` und `structureMost` villages= halten Städte auseinander, wie sie Dörfer auseinanderhielten, und `structureAt` villages=x,z gründet Städte nur dort, wo es sie festlegt. Eine Stadt wächst nur auf Viertel in Reichweite ihres ersten Brunnens, deren Boden um nicht mehr als 6 Blöcke ansteigt und über der Wasserlinie bleibt, und eine Stadt aus zwei oder weniger Grundstücken und Brunnen wird nicht gebaut |
| `villagePathAlleyBlock` | Block | leer | Der Belag einer Gasse, eines Weges, der für Linien und Gehwege zu schmal ist. Eine Gasse läuft zwischen den Gehwegen der Straßen, auf die sie trifft, und hat selbst keine; wo sie auf eine Straße trifft, wird kein Übergang gemalt. Leer legt Gassen mit dem Wegblock |
| `villagePathAlleyChance` | int, 0 oder mehr | `0` | Die Wahrscheinlichkeit in Prozent, dass eine Straße als Gasse gelegt wird statt in voller Breite. 0 legt keine Gassen |
| `villagePathMinimumWidth` | int, 0 oder mehr | `0` | Die schmalste erlaubte Straße. Eine Straße, die schmaler gelegt würde, wird gar nicht gelegt, und das Viertel ordnet sich um die Lücke herum an. 0 lehnt nie ab |
| `villagePathFlatRun` | int, 0 oder mehr | `6` | Straßen halten jede Höhe mindestens so viele Blöcke, bevor sie stufen, an Weltkoordinaten verankert, damit Abschnitte über Teile hinweg übereinstimmen. 0 oder 1 lässt eine Straße jeden Block stufen |
| `villagePlotsLeast` | int, 0 oder mehr | `0` | Auf wie viele Grundstücke eine Stadt wächst: Ring um Ring kommen Viertel um ihre Mitte dazu, bis sie mindestens so viele haben, nie mehr als villagePlotsMost. 0 legt nur das mittlere Viertel an |
| `villagePlotsMost` | int, 0 oder mehr | `0` | Die meisten Grundstücke, die eine Stadt haben darf: das Wachstum hält vor dem Viertel an, das darüber hinausginge, kein Viertel setzt mehr, und ein Viertel, das die Zahl erreicht, lässt die Gassen weg, an denen kein Grundstück liegt. 0 setzt keine Obergrenze |
| `villagePlotsBackRow` | Wahrheitswert | `true` | Ist das Dorf gewachsen, setzt ein zweiter Durchgang hinter jedes Grundstück an einer Straße ein weiteres, ihm zugewandt, mit demselben Wurf und derselben Platzprüfung, damit das Innere eines Blocks zwischen zwei Straßen bebaut wird statt leer zu bleiben |
| `villageTieStreets` | boolean | `true` | An bekommt ein Viertel, das seine Straßen nicht bis zum stehenden Dorf wachsen lassen kann, eine gerade Verbindungsstraße zur nächsten Straße, mit der es fluchtet. Aus wird ein solches Viertel wieder abgeräumt |
| `villageLayout` | Text | leer | Ein Stadtplan, nach dem das Viertel ausgelegt wird, statt es zu planen, benannt wie mypack:downtown und aus dem citymaps-Ordner dieses Packs gelesen. Leer plant das Viertel wie gewohnt |
| `villagePathCenterBlock` | Block | leer | Eine Mittellinie den Weg entlang. Leer zeichnet keine |
| `villagePathCenterDash` | int, 0 oder mehr | `0` | Strichelt diese Linie: N Blöcke Linie, dann einer Weg. An Weltkoordinaten verankert, sodass die Striche eines Wegstücks im nächsten weiterlaufen. `0` lässt sie durchgezogen |
| `villagePathLineBlock` | Block | leer | Randlinien zwischen Weg und Gehweg. Leer zeichnet keine |
| `villagePathSidewalkBlock` | Text | leer | Gehwege, auf Weghöhe außerhalb der Randlinien gelegt. Leer legt keine |
| `villagePathSidewalkWidth` | int, 0 oder mehr | `2` | Wie breit jeder Gehweg ist, sobald `villagePathSidewalkBlock` gesetzt ist |
| `villagePathLampBlock` | Text | `minecraft:oak_fence` | Der Block, aus dem eine Laterne an einer Straße gebaut wird, villagePathLampHeight hoch am Bordstein gestapelt. Eine Straße oder Gasse stellt eine an jedes Ende, eine dorthin, wo eine andere Straße auf sie trifft, und dazwischen alle 7 bis 12 Blöcke eine, auf ihrer tieferen Seite und auf der anderen nur, wo jene keinen Platz hat, und eine Wendeschleife säumt ihren Rand damit. Keine steht auf einer Brücke, in einem Tunnel oder weniger als zwei Blöcke vor einer Tür. Leer stellt keine Laternen |
| `villagePathLampHeight` | int, 1 oder mehr | `3` | Wie viele Blöcke hoch der Mast bis zu seinem Kopf steht |
| `villagePathLampTopBlock` | Text | `minecraft:black_wool` | Der Kopf oben auf dem Mast. Leer lässt ihn kahl |
| `villagePathLampSideBlock` | Text | `minecraft:torch` | Das Licht, das an jeder Seite des Kopfes nach außen hängt. Leer hängt keines |
| `villagePathLampStructure` | Text | leer | Eine Strukturdatei, die als ganze Laterne gesetzt wird, statt die drei Laternenblöcke zu stapeln, benannt `mypack:street_lamp` und aus dem `structures`-Ordner dieses Packs gelesen. Sie wird auf den Laternenplatz zentriert, ihre unterste Lage auf dem Bordstein, und die gesetzten Blöcke werden gehalten, damit nichts sie überschreibt. Leer stapelt die Blöcke |
| `villageWellStructure` | Liste | leer | Strukturdateien, die als Mittelpunkt jedes Platzes gesetzt werden, ein gewichteter Eintrag je Zeile, geschrieben name=weight wie mypack:plaza_spire=3, einmal je Platz ausgelost. Sie wird auf ein sechs Blöcke großes Quadrat zentriert, das freigeräumt und mit villagePathBlock gepflastert wird, ihre unterste Lage auf diesem Boden. Leer, der Anteil empty oder eine Struktur, die sich nicht laden lässt, baut dort stattdessen den Brunnen des Spiels selbst. Ein Eintrag, der nicht als name=weight geschrieben ist, wird ausgelassen |
| `villagePathDeadEnds` | Liste | leer | Wie eine Straße abgeschlossen wird, die tot endet, ein Eintrag je Zeile, je Ende ausgelost: sidewalk pflastert die letzte Reihe mit dem Gehwegblock und barrier stellt villagePathBridgeBarrierBlock in villagePathBridgeBarrierHeight Höhe an ihr entlang auf; jeder andere Eintrag wird übergangen. Beide schließen nur ein Ende
, das keine Wendeschleife bekommen hat, ein Stil, dessen Block nicht gesetzt ist, fällt aus der Auslosung, und ein Gassenende nimmt nur barrier. Leer lässt solche Enden offen |
| `villagePathIntersects` | Liste | leer | Muster, die an Kreuzungen gemalt werden, benannt nach Registrierungsschlüssel aus `<namespace>/pathintersects/` eines Packs. Ein Eintrag malt jede Kreuzung gleich; mehrere werden je Kreuzung nach Gewicht gewählt |

#### Dorfbrücken und Stege

*dörfer und städte*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villagePathSupportBlock": "minecraft:gravel",
    "villagePathBridgeBlock": "minecraft:oak_planks",
    "villagePathBridgeSidewalkBlock": "minecraft:oak_planks",
    "villagePathBridgeBarrierBlock": "minecraft:oak_fence",
    "villagePathBridgeBarrierHeight": 1,
    "villagePathBridgeDrop": 3,
    "villagePathVergeBlock": "",
    "villagePathVergeWaterBlock": "minecraft:oak_planks",
    "villagePathBridgeFrameBlock": "minecraft:stone_bricks",
    "villagePathBridgeFrameTopBlock": "minecraft:smooth_stone_slab",
    "villagePathBridgeFrameHeight": 4,
    "villagePathBridgeFrameRun": 24,
    "villagePathBridgeFrameLeast": 24,
    "villagePathPiers": ["railed", "pilings", "boardwalk"],
    "villagePathPierCargo": ["minecraft:chest=3", "mypack:crate=2,3", "empty=4"],
    "villagePathPierLoot": "resourcedatapackloader:chests/pier_cargo"
  }
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `villagePathSupportBlock` | Text | leer | Die Oberfläche selbst dort, wo der Boden blanker Fels ist, und die Pfeiler und Beine unter einer Straße über Wasser. Leer behält Vanilla-Kies, in Wüstenstädten Sandstein |
| `villagePathBridgeBlock` | Text | leer | Womit eine Straße oder ein Steg Wasser überquert. Leer deckt sie mit Brettern aus dem Holz des Dorfes: Akazie in einem Savannendorf, Fichte in einem Taigadorf, sonst Eiche |
| `villagePathBridgeSidewalkBlock` | Block | leer | Deckt den Gehweg dort, wo ein Weg Wasser überquert. Leer führt den normalen Gehwegblock hinüber |
| `villagePathBridgeBarrierBlock` | Block | leer | Geländer, an beiden Kanten eines Brückendecks aufgestapelt. Wo das Deck auf Boden aufliegt, steht keines. Leer baut keine |
| `villagePathBridgeBarrierHeight` | int, 1 oder mehr | `1` | Wie viele Blöcke hoch diese Geländer stehen |
| `villagePathBridgeDrop` | int, 0 oder mehr | `0` | Wie weit die Trasse einer Straße frei über dem Boden stehen muss, ehe der Abgrund darunter überbrückt statt aufgefüllt wird. `0` hält Straßen am Boden: sie überbrücken nur Wasser. `3` ist die Regel, der eine Eisenbahn-Trestle folgt. Das ändert die Trasse, nicht nur die Ausstattung |
| `villagePathVergeBlock` | Text | leer | Der Block, mit dem der Boden neben einer Straße und unter einem Grundstück aufgefüllt wird, wo die Stadt Land schaffen muss: die Rillen zwischen Grundstücken und die Auffüllung bis zu einer Straße über eine Lücke, die dort stattdessen mit villagePathBridgeBlock gedeckt wird, wo die Straße eine Brücke ist. Leer folgt dem Boden, auf dem er steht, und legt Sand, Terrakotta, Kies oder Erde mit Gras obenauf, wo es Erde wäre |
| `villagePathVergeWaterBlock` | Text | `minecraft:oak_planks` | Was aus dieser Füllung wird, wo sie über Wasser steht, damit ein auf einen See hinausgeführter Randstreifen keine Erdsäule ist. Er kleidet auch eine über Wasser stehengebliebene Steinstufe |
| `villagePathBridgeFrameBlock` | Block | leer | Ein Portalrahmen über einer langen Brücke: je ein Pfosten neben dem Deck und ein Querbalken darüber. Jeder Rahmen trägt unter dem Deck einen Pfeiler bis zum Grund, und in seiner Reihe wird keine Laterne aufgestellt. Leer baut keinen |
| `villagePathBridgeFrameTopBlock` | Text | leer | Der Querbalken oben auf diesem Rahmen. Leer nimmt `villagePathBridgeFrameBlock` |
| `villagePathBridgeFrameHeight` | int, 1 oder mehr | `4` | Wie viele Blöcke lichte Höhe der Rahmen über dem Deck lässt; der Balken liegt einen Block darüber |
| `villagePathBridgeFrameRun` | int, 1 oder mehr | `24` | Wie viele Reihen die Rahmen auseinanderstehen, wenn eine Brücke für mehrere lang genug ist. Sie werden symmetrisch um die Mitte des überbrückten Laufs verteilt |
| `villagePathBridgeFrameLeast` | int, 1 oder mehr | `24` | Der kürzeste überbrückte Lauf, der überhaupt einen Rahmen bekommt. Eine kürzere Brücke bleibt schlicht |
| `villagePathPiers` | Liste | leer | Stegformen für eine Straße, die über dem Wasser tot endet: der überbrückte Auslauf wird zum Steg statt zu einer Brücke ins Nirgendwo. Die Formen sind railed, pilings und boardwalk; mehrere Einträge losen je Steg eine aus. Leer bleibt ein solcher Auslauf eine schlichte Brücke |
| `villagePathPierCargo` | Liste | leer | Fracht, die innen an den Geländern eines Stegs steht, als block=gewicht-Einträge, block=gewicht,höhe zum Stapeln oder empty=gewicht für den Anteil, der frei bleibt. Ein Block darf seinen Zustand in eckigen Klammern tragen, und einer mit Ausrichtung dreht sich zur Mitte des Stegs. Eine Höhe außerhalb von 1 bis 8 stellt einen Block. Jede zweite Reihe lost die Liste auf beiden Seiten aus. Leer bleibt ein Steg leer |
| `villagePathPierLoot` | Text | `resourcedatapackloader:chests/pier_cargo` | Die Beutetabelle, aus der Frachtblöcke mit Inventar gefüllt werden, ausgelost beim ersten Öffnen. Ein Pack kann die eingebaute Tabelle ersetzen, indem es unter diesem Namen eine eigene Beutetabelle mitliefert. Leer bleiben sie leer |

#### Dorftunnel

*dörfer und städte*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villagePathTunnelBlock": "minecraft:stone_bricks",
    "villagePathTunnelDepth": 10,
    "villagePathTunnelLightBlock": "minecraft:sea_lantern",
    "villagePathTunnelLightRun": 8
  }
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `villagePathTunnelBlock` | Text | leer | Der Block, mit dem eine Straße dort ausgekleidet wird, wo sie einen Hügel durchbohrt, statt ihn aufzuschneiden: die Wände zu beiden Seiten der Röhre und die Decke darüber. Leer bohrt keine Tunnel und lässt eine Straße den Hügel hinaufsteigen |
| `villagePathTunnelDepth` | int, 1 oder mehr | `10` | Wie viel Boden über der Fahrbahn stehen muss, bevor ein Abschnitt gebohrt statt aufgeschnitten wird. Eine Erhebung, die über zwölf Reihen oder mehr so tief über der Straße liegt, wird eben gehalten und durchbohrt, ihre flacheren Zufahrten werden aufgeschnitten; eine kürzere Kuppe wird wie bisher aufgeschnitten. Zählt erst, wenn `villagePathTunnelBlock` einen Block nennt |
| `villagePathTunnelLightBlock` | Text | leer | Ein Licht, das entlang der Mittellinie in die Tunneldecke gesetzt wird. Leer setzt keins |
| `villagePathTunnelLightRun` | int, 1 oder mehr | `8` | Wie viele Blöcke diese Lichter auseinander sitzen. An Weltkoordinaten verankert, damit die Lichter eines Straßenstücks im nächsten weiterlaufen; ein Tunnel, der zu kurz ist, um eine dieser Stellen zu erreichen, wird einmal beleuchtet, in seiner Mitte |

#### Dorfkanalisation

*dörfer und städte*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageSewerBlock": "minecraft:stone_bricks",
    "villageSewerDepth": 8,
    "villageSewerHeight": 3,
    "villageSewerWidth": 5,
    "villageSewerWaterBlock": "minecraft:water",
    "villageSewerWalkBlock": "minecraft:chiseled_stone_bricks",
    "villageSewerLightBlock": "minecraft:glowstone",
    "villageSewerLightRun": 8,
    "villageSewerLadderBlock": "minecraft:ladder",
    "villageSewerCoverBlock": "minecraft:iron_trapdoor",
    "villageSewerMossBlock": "minecraft:mossy_cobblestone",
    "villageSewerMossChance": 30,
    "villageSewerVineBlock": "minecraft:vine",
    "villageSewerVineChance": 20,
    "villageSewerWellEntrance": true
  }
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `villageSewerBlock` | Blockname | leer | Der Block, mit dem ein Kanal unter den Straßen und Gassen eines Dorfes ausgekleidet wird: Boden, beide Wände und Decke. Leer gräbt keine Kanäle |
| `villageSewerDepth` | int, 4 oder mehr | `8` | Wie weit unter der Straßenoberfläche der Kanalboden liegt. Der Kanal folgt seiner Straße, eine steigende Straße trägt also einen steigenden Kanal. Braucht `villageSewerBlock` |
| `villageSewerHeight` | int, 2 oder mehr | `3` | Wie viele Blöcke Kopfhöhe über dem Gehweg stehen |
| `villageSewerWidth` | int, 3 oder mehr | `5` | Wie breit der Kanal läuft, quer gezählt einschließlich seiner beiden Wände. Eine gerade Zahl wird aufgerundet, damit die Rinne die Mitte behält |
| `villageSewerWaterBlock` | Text | `minecraft:water` | Was die Rinne in der Mitte füllt. Leer lässt sie trocken |
| `villageSewerWalkBlock` | Text | leer | Womit die Gehwege beiderseits der Rinne belegt sind. Leer läuft man auf dem Auskleidungsblock |
| `villageSewerLightBlock` | Text | leer | Der Block, der über der Rinne als Licht in die Decke gesetzt wird. Leer beleuchtet nichts |
| `villageSewerLightRun` | int, 1 oder mehr | `8` | Wie viele Blöcke diese Lichter auseinander sitzen. An Weltkoordinaten verankert, damit die Lichter eines Straßenstücks im nächsten weiterlaufen |
| `villageSewerLadderBlock` | Text | leer | Der Block, an dem ein Einstiegsschacht erklommen wird, von der Straße bis zur Kanaldecke gesetzt. Leer lässt den Schacht offen |
| `villageSewerCoverBlock` | Blockname | leer | Der Block, der einen Einstieg abdeckt, bündig in eine Ost-West-Straße gesetzt, wo eine Straße oder Gasse auf sie trifft, und auf dem Platz, wo diese Straße den Kanalring kreuzt. Eine hölzerne Falltür ist die übliche Wahl: eine eiserne braucht ein Redstone-Signal und lässt sich von Hand nicht öffnen, was den Kanal verschließt. Leer lässt die Schachtmündung offen |
| `villageSewerMossBlock` | Text | leer | Ein zweiter Block, der hier und da in die Auskleidung gemischt wird, bemooster Stein unter glattem etwa. Leer kleidet den Kanal durchgehend mit einem Block aus |
| `villageSewerMossChance` | int, 0 bis 100 | `25` | Wie viel Prozent der Auskleidungsblöcke als dieser zweite Block herauskommen. Pro Blockposition aus dem Weltseed gewürfelt, derselbe Kanal sieht also immer gleich aus |
| `villageSewerVineBlock` | Blockname | leer | Ein Block, der hier und da innen an den Kanalwänden hängt, Ranken etwa. Er hängt sich an die Wand, an der er steht. Leer hängt nichts |
| `villageSewerVineChance` | 0 bis 100 | `20` | Wie viel Prozent der Zellen neben einer Wand ihn tragen. Pro Blockposition aus dem Weltseed gewürfelt, derselbe Kanal hängt also immer gleich |
| `villageSewerWellEntrance` | Wahrheitswert | `true` | Ein Kanalring unter dem Platzring um den Brunnen, durch den der Kanal jeder Straße läuft, und je ein Gullydeckel auf dem Platz hinab auf den Ring, wo eine Ost-West-Straße ihn kreuzt, sodass die Kanalisation ein zusammenhängendes System mit Einstieg in der Stadtmitte ist. Aus endet der Kanal jeder Straße am Brunnen, und der Platz hat keinen Weg hinab |

**Kanalisation.** Wird `villageSewerBlock` gesetzt, gräbt die Stadt unter jeder Straße und Gasse einen Kanal, `villageSewerDepth` Blöcke unter der eigenen Oberfläche dieser Straße. Er ist kein eigenes Netz: Er folgt den Straßen, geht also überallhin, wo sie hingehen, steigt, wo sie steigen, und zwei Kanäle treffen sich unter einer Kreuzung, weil sich die Straßen darüber treffen; wo eine Straße oder Gasse an einer anderen endet, läuft ihr Kanal unter dieser weiter, bis er auf deren Kanal trifft. Ein Wendehammer und ein Stück, das auf einer Brücke liegt, tragen keinen. Der Querschnitt ist ein ausgekleideter Boden, eine Rinne in der Mitte, gefüllt mit `villageSewerWaterBlock`, ein Steg zu beiden Seiten, belegt mit `villageSewerWalkBlock`, `villageSewerHeight` Blöcke freie Höhe und ein ausgekleidetes Dach, `villageSewerWidth` breit einschließlich der beiden Wände, und `villageSewerLightBlock` setzt alle `villageSewerLightRun` Blöcke ein Licht über der Rinne ins Dach. Wo ein U-Bahn-Bohr durch die Tiefe des Kanals läuft, unter der Straße oder daneben, wird der Kanal quer darüber massiv zugemauert, und ein Gleis dort bleibt unberührt. Der Kanal einer Straße endet am Ring um den Brunnen, wenn `villageSewerWellEntrance` an ist, und am Brunnen selbst, wenn es aus ist. Ein Kanal steigt nie so hoch, dass er die Straße darüber stört, und ein Stück ohne Platz zwischen Straße und Weltboden wird ausgelassen statt gequetscht.

#### Dorfeisenbahnen

*dörfer und städte*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageRailLines": 1,
    "villageRailSpacing": 48,
    "villageRailDirection": "ew",
    "villageRailWidth": 3,
    "villageRailBlock": "minecraft:rail",
    "villageRailTrackSeat": "auto",
    "villageRailBedBlock": "minecraft:gravel",
    "villageRailTieBlock": "minecraft:spruce_planks",
    "villageRailTieRun": 2,
    "villageRailTracks": 2,
    "villageRailTrackGap": 2,
    "villageRailShoulderBlock": "minecraft:gravel",
    "villageRailShoulderWidth": 1,
    "villageRailPowerBlock": "minecraft:powered_rail",
    "villageRailPowerBase": "minecraft:redstone_block",
    "villageRailPowerRun": 16,
    "villageRailClimb": 8,
    "villageRailTail": 48,
    "villageRailSupportBlock": "minecraft:oak_log",
    "villageRailDeckBlock": "minecraft:oak_planks",
    "villageRailBarrierBlock": "minecraft:oak_fence",
    "villageRailBridgeFrameBlock": "minecraft:stone_bricks",
    "villageRailBridgeFrameTopBlock": "minecraft:smooth_stone_slab",
    "villageRailBridgeFrameHeight": 4,
    "villageRailBridgeFrameRun": 24,
    "villageRailBridgeFrameLeast": 24,
    "villageRailTunnelBlock": "minecraft:stone_bricks",
    "villageRailTunnelDepth": 6,
    "villageRailTunnelLightBlock": "minecraft:glowstone",
    "villageRailTunnelLightRun": 8
  }
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `villageRailLines` | int, 0 oder mehr | `0` | Wie viele Bahnlinien durch eine Stadt laufen, vor jeder Straße gelegt, damit die Stadt um sie herum wächst, jede über die ganze Länge der Stadt. Bei villageCitySpacing 1 ist jedes Viertel eine Stadt und trägt seine eigenen. 0 legt keine |
| `villageRailSpacing` | int, 1 oder mehr | `48` | Wie viele Blöcke freier Boden zwischen dem Bett einer Bahnlinie und dem der nächsten derselben Stadt mindestens liegen. 1 legt sie einen Block auseinander, so baut ein Pack einen Bahnhof aus parallelen Linien |
| `villageRailDirection` | Text | `any` | In welche Richtung die Linien laufen: `ew` von Ost nach West, `ns` von Nord nach Süd, `any` würfelt es je Stadt. `e`, `w`, `n` und `s` werden genauso gelesen |
| `villageRailWidth` | int, 3 oder mehr | `3` | Wie breit das Gleisbett mindestens ist. `3` trägt ein Gleis in der Mitte und `5` zwei; ein Bett, dem mehr Gleise abverlangt werden, als daraufpassen, wird breiter, um sie zu tragen |
| `villageRailBlock` | Block | leer | Das Gleis. Leer legt Vanilla-Schienen, auf denen Loren fahren; jeder andere Block wird gelegt, wie er ist |
| `villageRailTrackSeat` | `auto`, `on` oder `in` | `auto` | Wo das Gleis sitzt. `auto` setzt ein Schienenblock auf das Bett und jeden anderen Block bündig in die Bettoberfläche; `on` legt es immer auf das Bett; `in` setzt es immer hinein. Ein ins Bett gesetztes Gleis ist der Weg, mit dem ein Pack einen Bahn-Look aus Eisenblöcken oder Stufen statt aus Loren-Schienen legt, und ein Bahnübergang läuft dann bündig durch das Pflaster |
| `villageRailBedBlock` | Text | leer | Das Bett unter dem Gleis. Leer legt Kies |
| `villageRailTieBlock` | Text | leer | Die Schwelle, die alle `villageRailTieRun` Reihen quer über das Bett gelegt wird. Leer legt Bretter |
| `villageRailTieRun` | int, 1 oder mehr | `2` | Wie viele Reihen die Schwellen auseinanderliegen |
| `villageRailTracks` | int, 0 oder mehr | `0` | Wie viele Gleise das eine Bett trägt, nebeneinander und `villageRailTrackGap` auseinander. **Das Bett wird breiter, um sie alle zu tragen**, drei Gleise teilen sich also ein Gleisbett, statt zu drei Linien zu werden. `0` legt ein Gleis auf ein Bett unter fünf Blöcken Breite und zwei auf ein breiteres |
| `villageRailTrackGap` | int, 2 oder mehr | `2` | Wie viele Blöcke die Gleise auf einem Bett auseinanderliegen, Mitte zu Mitte. `2`, das Mindeste, lässt einen Block Bett zwischen ihnen, und genau das hält sie davon ab, ineinander zu schwenken, wie berührende Schienen es tun |
| `villageRailShoulderBlock` | Block | leer | Kleidet die äußersten Spalten des Bettes, ein Wartungspfad neben dem Gleis und die Antwort der Eisenbahn auf einen Gehweg. Leer legt keinen |
| `villageRailShoulderWidth` | int, 0 oder mehr | `1` | Wie viele Spalten breit diese Schulter je Seite ist, außerhalb von `villageRailWidth` hinzugefügt. Braucht `villageRailShoulderBlock` |
| `villageRailPowerBlock` | Text | leer | Das Antriebsgleis, das alle `villageRailPowerRun` Reihen in die Linie gesetzt wird. Leer nimmt eine Vanilla-Antriebsschiene; ein Block, der kein Gleis ist, wird einfach dorthin gelegt |
| `villageRailPowerBase` | Text | leer | Was unter einem Antriebsgleis liegt, um es zu speisen. Leer nimmt einen Redstone-Block |
| `villageRailPowerRun` | int, 0 oder mehr | `0` | Alle so viele Reihen wird eine Antriebsschiene über einem Redstone-Block in ein Vanilla-Gleis gesetzt, damit eine Lore weiterrollt. `0` setzt keine, und jedes andere Gleis als Vanilla-Schienen übergeht es |
| `villageRailClimb` | int, 1 oder mehr | `8` | Wie viele Reihen die Linie eben läuft für jeden Block, den sie steigt oder fällt. `1` legt sie so steil wie eine Straße an |
| `villageRailTail` | int, 0 oder mehr | `48` | Wie weit eine Bahnlinie an beiden Enden über das letzte Viertel der Stadt hinausläuft |
| `villageRailSupportBlock` | Text | leer | Die Pfosten unter einer Trestle-Brücke, wo die Linie über Wasser oder einen Abgrund läuft. Leer nimmt Eichenstämme |
| `villageRailDeckBlock` | Text | leer | Das Deck, auf dem eine Trestle-Brücke das Bett trägt. Leer nimmt Bretter |
| `villageRailBarrierBlock` | Text | leer | Geländer entlang beider Kanten eines Brückendecks. Leer stellt keine auf |
| `villageRailBridgeFrameBlock` | Block | leer | Ein Portalrahmen über einer langen Trestle-Brücke: je ein Pfosten an den Deckkanten und ein Balken darüber. Jede Reihe, die einen trägt, bekommt auch ihre Pfosten bis auf den Grund. Leer baut keine |
| `villageRailBridgeFrameTopBlock` | Text | leer | Der Balken oben auf dem Rahmen. Leer nimmt `villageRailBridgeFrameBlock` |
| `villageRailBridgeFrameHeight` | int, 2 oder mehr | `4` | Wie viele Blöcke lichte Höhe der Rahmen über dem Deck lässt; der Balken liegt einen Block darüber |
| `villageRailBridgeFrameRun` | int, 2 oder mehr | `24` | Wie viele Reihen die Rahmen auseinanderstehen, wenn eine Brücke für mehrere lang genug ist |
| `villageRailBridgeFrameLeast` | int, 2 oder mehr | `24` | Die kürzeste Trestle-Brücke, die überhaupt einen Rahmen bekommt. Eine kürzere bleibt schlicht |
| `villageRailTunnelBlock` | Text | leer | Der Block, mit dem eine Bahnlinie verkleidet wird, wo sie sich durch einen Hügel bohrt, statt ihn zu erklimmen. Leer bohrt keine Tunnel |
| `villageRailTunnelDepth` | int, 1 oder mehr | `6` | Wie viel Boden über dem Bett stehen muss, ehe ein Abschnitt gebohrt statt aufgeschnitten wird. Braucht `villageRailTunnelBlock` |
| `villageRailTunnelLightBlock` | Text | leer | Ein Licht, das in die Decke eines Eisenbahntunnels entlang seiner Mittellinie gesetzt wird. Leer setzt keines |
| `villageRailTunnelLightRun` | int, 1 oder mehr | `8` | Wie viele Blöcke diese Tunnellichter auseinanderstehen, an Weltkoordinaten verankert, damit die Teile übereinstimmen |

**Wo eine Linie verläuft.** Die Linien laufen parallel auf der Achse, die `villageRailDirection` nennt, und werden reihum vom ersten Brunnen der Stadt aus verteilt, erst auf der einen Seite, dann auf der anderen, jede mit mindestens `villageRailSpacing` Blöcken Boden zwischen ihrem Bett und dem der nächsten Linie. Eine Bahnlinie beginnt jenseits des Platzes und der Grundstücke um ihn herum und weicht zur Seite aus, wo sie längs auf einer Straße läge; eine U-Bahn beginnt in der Reihe des Brunnens und rückt auf die nächste Straße innerhalb von `villageSubwaySpacing`, damit sie unter einer Straße verläuft. Eine Linie wird vor den Grundstücken gelegt, daher steht kein Grundstück auf offenem Gleis, und sie läuft die ganze Stadt entlang und an beiden Enden noch `villageRailTail` über ihr letztes Viertel hinaus, wobei sie sieben Blöcke vor jeder anderen Stadt in ihrem Weg endet. Jedes Viertel, das sie durchquert, legt sein eigenes Stück, ob die Stadt dort gewachsen ist oder nicht.

**Steigung.** Eine Bahn steigt nicht wie eine Straße. Ihr Bett folgt dem über eine lange Strecke geglätteten Boden und ändert seine Höhe höchstens alle `villageRailClimb` Reihen um einen Block; eine U-Bahn folgt dem Boden `villageSubwayDepth` darunter, und eine Linie, die diese Tiefe irgendwo auf ihrem Weg nicht halten kann, über den sechs Blöcken, die ihre Auskleidung über dem Weltboden braucht, wird gar nicht gelegt. Wo der Boden um mehr als drei Blöcke abfällt oder Wasser ihn bedeckt, läuft die Linie auf einer Trestle-Brücke: ein Deck aus `villageRailDeckBlock` mit dem Gleis darauf oder darin, ohne Schwellen und Randstreifen, auf Pfosten aus `villageRailSupportBlock` unter beiden Kanten alle vier Reihen, und jeder Pfosten reicht höchstens 24 Blöcke hinab bis auf festen Boden. Wo der Boden ansteigt, wird die Linie offen eingeschnitten oder mit `villageRailTunnelBlock` durchbohrt, sobald der Boden über dem Bett auf zwölf Reihen oder mehr `villageRailTunnelDepth` tief steht; der Bohr läuft weiter, solange noch ein Block Boden ihn überdeckt. Ein offener Einschnitt mit Wasser in bis zu drei Blöcken Abstand wird bis zur Wasserhöhe in der Tunnelauskleidung ummauert, und wo der Boden neben dem Bett abfällt, wird er aufgefüllt. Über dem Bett bleiben entlang der ganzen Linie vier Blöcke frei. Eine Trestle-Brücke liegt von Ende zu Ende auf einer Höhe, und das Bett zu beiden Seiten führt als Rampe zu dieser Höhe; wo das Ebenhalten einer Brücke und die Steigrate sich widersprechen, gewinnt die Ebene, und die Rampe daneben darf früher stufen, als `villageRailClimb` sagt. Eine Trestle-Brücke von `villageRailBridgeFrameLeast` Reihen oder mehr trägt Rahmen, sobald `villageRailBridgeFrameBlock` einen Block nennt, `villageRailBridgeFrameRun` Reihen auseinander und symmetrisch um die Brückenmitte verteilt, und jede Reihe mit einem Rahmen trägt auch ihre Pfosten. Eine Reihe, in der eine Straße die Linie kreuzt, bleibt ohne Rahmen.

**Kreuzungen.** Eine Straße kreuzt eine Linie gerade hindurch. An einer Kreuzung wird die Linie über die Straße und je eine Reihe darüber hinaus eben gehalten, und die Straße wird an die Linie angepasst, nie umgekehrt, und steigt mit ihrer eigenen Neigung auf diese Höhe. Das Pflaster behält die Oberfläche, und das Gleis läuft einen Block höher darüber, oder bündig darin, wenn `villageRailTrackSeat` das Gleis ins Bett setzt, sodass eine Lore die Straße und ein Dorfbewohner das Gleis überquert. Eine Linie, die unter einer Straße hindurchgebohrt ist, die sechs oder mehr Blöcke über ihr steht, wird gar nicht gekreuzt: Die Straße behält ihre eigene Höhe und führt über den Tunnel hinweg. Diese Höhe wird am Boden der Straße gemessen, so geglättet, dass er je Reihe höchstens einen Block steigt, bevor eine Kreuzung, ein Brunnen oder eine Bahnlinie ihn festhält.

**Türstufen.** Die Stufe vor jeder Tür eines Grundstücks wird mit Boden aufgefüllt, wo der Boden abfällt, und eine steinerne Stufe, die über Wasser stehen bleibt, wird mit `villagePathVergeWaterBlock` verkleidet.

**Gleis.** Mit leerem `villageRailBlock` ist das Gleis eine Vanilla-Schiene entlang der Linie, und `villageRailPowerRun` setzt alle so viele Reihen eine eingeschaltete Antriebsschiene über einen Redstone-Block, damit eine Lore die ganze Linie fährt; ein ins Bett gesetztes Gleis trägt keine Antriebsschienen. Ein Pack, das Eisenblöcke, Gitter oder etwas anderes möchte, nennt stattdessen diese: Ein Block mit einer Achse, etwa ein Stamm, wird entlang der Linie gedreht, jeder andere wird so gelegt, wie er ist. Jeder Bahn-, U-Bahn-, Stations- und Kanalblock darf seinen Zustand in eckigen Klammern tragen, und Blöcke mischen weiter oben nennt die Einstellungen, deren gewichtete Mischung Block für Block gezogen wird.

#### Dorf-U-Bahnen

*dörfer und städte*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageSubwayLines": 0,
    "villageSubwayDepth": 24,
    "villageSubwaySpacing": 64,
    "villageSubwayDirection": "any",
    "villageSubwayWidth": 5,
    "villageSubwayBlock": "",
    "villageSubwayTrackSeat": "auto",
    "villageSubwayBedBlock": "minecraft:gravel",
    "villageSubwayTieBlock": "minecraft:spruce_planks",
    "villageSubwayTieRun": 2,
    "villageSubwayTracks": 2,
    "villageSubwayTrackGap": 2,
    "villageSubwayShoulderBlock": "",
    "villageSubwayShoulderWidth": 1,
    "villageSubwayPowerBlock": "",
    "villageSubwayPowerBase": "minecraft:redstone_block",
    "villageSubwayPowerRun": 16,
    "villageSubwayTunnelBlock": "minecraft:stone_bricks",
    "villageSubwayTunnelLightBlock": "minecraft:glowstone",
    "villageSubwayTunnelLightRun": 8,
    "villageSubwayClimb": 8,
    "villageSubwayTail": 48,
    "villageSubwaySurfaces": 25
  }
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `villageSubwayLines` | int, 0 oder mehr | `0` | Wie viele unterirdische Bahnlinien eine Stadt gräbt. 0 gräbt keine und würfelt nichts, die Stadt wird also genau so angelegt, wie sie ohne sie wäre |
| `villageSubwayDepth` | int, 6 oder mehr | `24` | Wie tief unter der Oberfläche das Bett liegt. Die Linie wird nach dem Boden über ihr abgestuft, folgt dem Gelände also in dieser Tiefe, statt eben zu verlaufen |
| `villageSubwaySpacing` | int, 1 oder mehr | `64` | Wie weit die U-Bahn-Linien einer Stadt voneinander entfernt gehalten werden |
| `villageSubwayDirection` | Text | `any` | In welche Richtung U-Bahn-Linien verlaufen: ew für Ost nach West, ns für Nord nach Süd oder any, um je Stadt zu würfeln |
| `villageSubwayWidth` | int, 3 oder mehr | `3` | Wie breit das Bett ist, ohne Schultern |
| `villageSubwayBlock` | Text | leer | Der Gleisblock. Leer legt Vanilla-Schienen |
| `villageSubwayTrackSeat` | Text | `auto` | Ob das Gleis auf dem Bett liegt, darin, oder `auto`, damit der Block entscheidet |
| `villageSubwayBedBlock` | Text | leer | Der Block, aus dem das Bett besteht. Leer nimmt Kies |
| `villageSubwayTieBlock` | Text | leer | Der Block, der als Schwellen quer über das Bett gelegt wird. Leer nimmt Bretter |
| `villageSubwayTieRun` | int, 1 oder mehr | `2` | Wie viele Blöcke Abstand die Schwellen haben |
| `villageSubwayTracks` | int, 0 oder mehr | `0` | Wie viele parallele Gleise das Bett trägt. 0 nimmt so viele, wie die Breite zulässt |
| `villageSubwayTrackGap` | int, 2 oder mehr | `2` | Wie weit parallele Gleise auseinanderliegen |
| `villageSubwayShoulderBlock` | Text | leer | Der Block zu beiden Seiten des Bettes. Leer lässt keine Schulter |
| `villageSubwayShoulderWidth` | int, 0 oder mehr | `1` | Wie breit diese Schulter ist |
| `villageSubwayPowerBlock` | Text | leer | Der Block für das angetriebene Gleis. Leer nimmt Vanilla-Antriebsschienen |
| `villageSubwayPowerBase` | Text | leer | Der Block, der unter ein angetriebenes Gleis gesetzt wird, um es zu treiben. Leer nimmt einen Redstone-Block |
| `villageSubwayPowerRun` | int, 0 oder mehr | `0` | Wie viele Blöcke Abstand die Antriebsschienen haben. 0 legt keine |
| `villageSubwayTunnelBlock` | Text | leer | Der Block, mit dem die Röhre ausgekleidet wird: die Wände zu beiden Seiten und die Decke darüber. Leer gräbt die Röhre und ihre Stationen ohne Auskleidung |
| `villageSubwayTunnelLightBlock` | Text | leer | Der Block, der als Licht in die Tunneldecke gesetzt wird. Leer beleuchtet nichts |
| `villageSubwayTunnelLightRun` | int, 1 oder mehr | `8` | Wie viele Blöcke Abstand diese Lichter haben, an Weltkoordinaten verankert, damit die Teilstücke übereinstimmen |
| `villageSubwayClimb` | int, 1 oder mehr | `8` | Wie viele Blöcke eine Linie läuft, bevor sie einen Block steigen oder fallen darf |
| `villageSubwayTail` | int, 0 oder mehr | `48` | Wie weit über die eigenen Teile der Stadt hinaus eine Linie läuft, bevor sie endet |
| `villageSubwaySurfaces` | int, 0 bis 100 | `25` | Die Chance in Hundert, dass eine U-Bahn-Linie an einem Ende an die Oberfläche steigt und von dort als gewöhnliche Bahn weiterläuft, Tunnel hinter sich und offenes Gleis vor sich. Der Aufstieg braucht villageSubwayClimb Reihen je Block, eine tiefe Linie braucht also einen langen Lauf nach oben. 0 hält jede U-Bahn auf ganzer Länge unter der Erde |

**Aufstieg.** `villageSubwaySurfaces` ist die Chance in Hundert, dass eine Linie, statt von Ende zu Ende vergraben zu bleiben, an einem Ende an die Oberfläche steigt und von dort als gewöhnliche Bahn weiterläuft: Tunnel hinter sich, offenes Gleis voraus. Der Aufstieg hält sich an `villageSubwayClimb`, einen Block je so viele Reihen, daher braucht eine Linie in `villageSubwayDepth` Tiefe allein für die Rampe Tiefe mal Steigung Reihen und danach noch eine gute Strecke, damit es sich lohnt; eine Linie ohne Platz für beides bleibt einfach unter der Erde. Der Aufstieg beginnt nicht vor dem fernen Ende der Straßen, unter denen die Linie verläuft, kommt also hinter den Straßen der Stadt nach oben statt mitten durch sie, und ein Grundstück auf dem Stück, das auftaucht, macht ihm Platz. Stationen werden erst vergeben, wenn der Aufstieg feststeht, und halten sich von der Rampe fern. Mit leerem `villageSubwayTunnelBlock` werden Bohr, Stationen und Treppen ohne Auskleidung gegraben.

#### U-Bahnhöfe

*dörfer und städte*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageSubwayStationLength": 16,
    "villageSubwayStationRun": 0,
    "villageSubwayPlatformWidth": 3,
    "villageSubwayPlatformBlock": "minecraft:mossy_stone_bricks",
    "villageSubwayRailingBlock": "minecraft:iron_bars",
    "villageSubwayBenchBlock": "minecraft:oak_stairs",
    "villageSubwayBenchEndBlock": "minecraft:oak_log",
    "villageSubwayBenchLength": 5,
    "villageSubwayStation": "mypack:subway_station",
    "villageSubwayStationFoot": 4,
    "villageSubwayStationRepeat": 12
  }
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `villageSubwayStationLength` | int, 0 oder mehr | `0` | Wie viele Blöcke lang eine Stationskammer ist, mittig auf der Reihe, auf der die Linie am nächsten am Brunnen vorbeiführt. 0 baut überhaupt keine Stationen |
| `villageSubwayStationRun` | int, 0 oder mehr | `0` | Wie viele Blöcke Abstand weitere Stationen entlang einer Linie haben, hinter der, die dem ersten Brunnen der Stadt am nächsten liegt. 0 baut nur die am Brunnen |
| `villageSubwayPlatformWidth` | int, 0 oder mehr | `3` | Wie weit die Kammer zu beiden Seiten des Bettes aufgeweitet wird, um einen Bahnsteig zu bilden |
| `villageSubwayPlatformBlock` | Text | leer | Der Block, mit dem der Bahnsteig ausgelegt wird. Leer legt ihn mit der Tunnelauskleidung aus |
| `villageSubwayRailingBlock` | Text | `minecraft:iron_bars` | Der Block, der um den Kopf der Stationstreppe geländert wird, wo sie auf die Straße mündet, damit niemand in den Schacht läuft. Leer lässt den Kopf ohne Geländer |
| `villageSubwayBenchBlock` | Block | `minecraft:oak_stairs` | Der Sitz der Bänke, die auf dem Bahnsteig einer Station und neben ihrem Treppenkopf stehen. Ein Treppenblock wird von der Linie weggedreht und liest sich als Bank; jeder Block geht. Leer lässt die Bänke weg |
| `villageSubwayBenchEndBlock` | Text | `minecraft:oak_log` | Die Lehnen an beiden Enden einer Stationsbank. Leer lässt den Sitz an beiden Enden kahl |
| `villageSubwayBenchLength` | int, 0 bis 32 | `5` | Wie lang eine Stationsbank ist, Lehnen eingerechnet. `0` lässt die Bänke weg |
| `villageSubwayStation` | Text | leer | Eine Struktur aus dem structures-Ordner eines Packs, die als Station dient: ihr Schacht, ihre Treppe und ihr Zugang von der Straße. Hol eine mit #scripts/rdpl-grab-template.py aus einer von Hand gebauten Welt: ihre festen Zellen werden gelegt und ihre Luftzellen ausgehauen, sodass die Form das Bauwerk ist und nicht eine Beschreibung davon. Leer baut gar keine Station, und ein Name, der sich nicht laden lässt, schreibt einen Fehler ins Log und baut keine |
| `villageSubwayStationFoot` | int, 0 bis 64 | `4` | Wie viele Lagen am Fuß eines Stationsbauwerks einmalig gelegt werden, vor dem Teil, der sich wiederholt. Der Boden und der Durchgang hinaus zum Bahnsteig liegen hier |
| `villageSubwayStationRepeat` | int, 0 bis 64 | `12` | Wie viele Lagen eines Stationsbauwerks sich wiederholen, damit ein Bauwerk jeder Tiefe dient: der Schacht wächst um ganze Kopien dieses Bandes, und der Gang nimmt auf, was übrig bleibt. Es muss eine ganze Windung der Treppe sein, sonst schließen die Läufe nicht an. `0` lässt das Bauwerk nie wachsen |

**Stationen.** Eine U-Bahn-Linie bekommt nur dann Stationen, wenn `villageSubwayStation` einen Bau nennt, der sich laden lässt: Ist es leer, gibt es keine Kammer, keine Treppe und keinen Zugang, und ein Name, der sich nicht laden lässt, schreibt einen Fehler ins Log und baut keine. Mit einem genannten Bau bekommt eine Linie eine Station in der Reihe des ersten Brunnens der Stadt, sobald `villageSubwayStationLength` und `villageSubwayPlatformWidth` gesetzt sind, und weitere alle `villageSubwayStationRun` Blöcke entlang der Linie. Jede rückt bis zu 48 Blöcke in beide Richtungen, um einen Platz für ihren Bau neben einer Straße zu finden, die über die ganze Länge dieses Platzes entlang der Linie läuft, abseits jeder Straße, jedes Brunnens und Platzes und mindestens die Stationslänge plus sieben von einer schon vergebenen Station entfernt; ein Grundstück an dieser Stelle macht Platz, und eine Station ohne solche Stelle entfällt. Eine Linie, die keine Station behält, öffnet keine Kammer und trägt so nie eine ohne Zugang. Die Kammer wird auf ihrer ganzen Länge eben gehalten: das zu beiden Seiten um `villageSubwayPlatformWidth` aufgeweitete Bett, mit `villageSubwayPlatformBlock` gepflastert, in der Tunnelauskleidung ummauert und überdacht, mit `villageSubwayTunnelLightBlock` und `villageSubwayTunnelLightRun` des Tunnels beleuchtet und an beiden Enden quer über dem Bohr zugemauert. Vom Bahnsteig führt ein Gang zum Stationsbau, der neben der Straße, nie unter ihr, nach oben steigt; er kommt auf der Höhe der nächsten Straße innerhalb von acht Blöcken heraus, und zwar auf der Höhe, die diese Straße am Boden hat, nicht auf der eines Decks oder einer Rampe darüber, oder auf der Höhe des Bodens, wo keine Straße ist, und entfällt beim Bau der Stadt, wo das weniger als drei Blöcke über dem Bahnsteig liegt, wo der Bau den Aufstieg selbst gewachsen nicht schafft oder wo sein Gang zum Bahnsteig länger als 32 Blöcke würde; das Log nennt den Grund. Der Boden zwischen dieser Straße und dem Bau wird auf dieselbe Höhe gebracht, wo er abfällt aufgefüllt und darüber freigeräumt, damit die Station von der Straße aus begehbar ist. Eine Bank aus `villageSubwayBenchBlock` mit Armlehnen aus `villageSubwayBenchEndBlock`, `villageSubwayBenchLength` lang, steht auf dem Bahnsteig.

**Die Station von Hand bauen.** `villageSubwayStation` nennt eine Strukturdatei, die als Station dient; so liefert ein Pack eine Form, die jemand gebaut hat, statt einer, die in Einstellungen beschrieben ist. Bau sie in einer Welt, hol sie mit #scripts/rdpl-grab-template.py heraus und leg sie dem Pack bei: Ihre Blöcke werden gelegt, wie sie gebaut sind, Schwammzellen werden zur Tunnelauskleidung, ihre Luftzellen werden ausgehoben, und was darin steht, eine Lore oder ein Rüstungsständer, kommt mit. Sie wird von der Ecke des Platzes der Station aus gesetzt. Ein Bau dient jeder Tiefe, weil sich seine Mitte wiederholt: `villageSubwayStationFoot` Ebenen werden unten einmal gelegt, mit dem Boden und dem Durchgang zum Bahnsteig, dann stapeln sich ganze Kopien der nächsten `villageSubwayStationRepeat` Ebenen, bis der Bau die Straße erreicht. Dieses Band muss eine ganze Treppenwindung sein, sonst treffen sich die Läufe nicht, wo zwei Kopien aneinanderstoßen. Ein zwei Blöcke hoher Gang führt vom Durchgang hinab zum Bahnsteig und wendet entlang der Kammer, wo der Abstieg für den geraden Weg zu lang ist; über dem Kopf des Baus wird der Boden acht Blöcke hoch freigeräumt, ein Geländer aus `villageSubwayRailingBlock` umgibt die Öffnung auf Straßenhöhe, und daneben steht eine Bank. Ein Bau, der sich nicht laden lässt, baut nirgends eine Station und schreibt einen Fehler ins Log; eine Station, die ihr Bau nicht bis zur Straße bringt, entfällt schon bei der Planung der Stadt, ohne Kammer, und das Log nennt den Grund. Der Bau bringt seine eigene Öffnung zur Straße mit.

#### Bahnverbindungen zwischen Dörfern

*dörfer und städte*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageRailLines": 1,
    "villageRailLinks": true,
    "villageRailLinkLeast": 128,
    "villageRailLinkMost": 1024,
    "villageRailLinkBridgeMost": 96,
    "villageRailLinkTunnelMost": 192,
    "villageRailLinkStation": "both",
    "villageRailLinkStationLength": 16,
    "villageRailLinkPlatformWidth": 3,
    "villageRailLinkPlatformBlock": "minecraft:stone_bricks"
  }
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `villageRailLinks` | `true` oder `false` | `false` | Verbindet benachbarte Städte, deren erste Linien sich über eine Naht hinweg gegenüberliegen. Braucht `villageRailLines`, oder `villageSubwayLines` in einem Pack ohne oberirdische Linien |
| `villageRailLinkLeast` | int, 0 oder mehr | `128` | Die kürzeste Verbindung, die gelegt wird, Stichstrecke plus Stammstrecke plus Stichstrecke, in Blöcken |
| `villageRailLinkMost` | int, 0 oder mehr | `1024` | Die längste Verbindung, die gelegt wird, Stichstrecke plus Stammstrecke plus Stichstrecke, in Blöcken |
| `villageRailLinkBridgeMost` | int, 0 oder mehr | `96` | Die längste Brücke, die eine Verbindung brauchen darf. Eine Verbindung über breiteres Wasser oder eine tiefere Senke wird nicht gelegt |
| `villageRailLinkTunnelMost` | int, 0 oder mehr | `192` | Der längste Tunnel, den eine Verbindung brauchen darf, wo `villageRailTunnelBlock` Tunnel bohrt. Eine Verbindung, die weiter bohren müsste, wird nicht gelegt |
| `villageRailLinkStation` | Text | `both` | Der Bahnhof auf jeder Stichstrecke kurz vor der Stammstrecke: `both` legt zu beiden Seiten der Linie einen Bahnsteig, `one` einen einzigen links eines Zuges, der auf die Stammstrecke zufährt, `none` baut keinen |
| `villageRailLinkStationLength` | int, 0 oder mehr | `16` | Wie viele Reihen lang die Bahnsteige sind. `0` baut keine Bahnhöfe |
| `villageRailLinkPlatformWidth` | int, 0 oder mehr | `3` | Wie viele Blöcke breit jeder Bahnsteig ist |
| `villageRailLinkPlatformBlock` | Text | leer | Der Block, aus dem die Bahnsteige gebaut sind. Leer nimmt Steinziegel |

**Was eine Verbindung ist.** Bahnverbindungen verknüpfen benachbarte Städte zu einem Netz. Städte werden je eine pro Zelle des Stadtrasters gegründet (`villageCitySpacing`), und eine Verbindung verläuft entlang der Naht zwischen zwei Zellen: Die erste Linie jeder Stadt führt über ihr Ende hinaus als Stichstrecke geradeaus bis zur Naht und trifft dort im rechten Winkel auf eine Stammstrecke, die entlang der Naht liegt. Die Stammstrecke reicht von einer Stichstrecke zur anderen und nie darüber hinaus. Sie braucht `villageRailLines`, oder `villageSubwayLines` in einem Pack ohne oberirdische Linien, und ist standardmäßig aus.

**Welche Städte verbunden werden.** Zwei Städte werden nur verbunden, wenn sie in benachbarten Zellen stehen, ihre ersten Linien auf der Achse laufen, die die Naht zwischen ihnen kreuzt, und die ganze Verbindung, von Brunnen zu Brunnen entlang des Gleises gemessen, zwischen `villageRailLinkLeast` und `villageRailLinkMost` Blöcken lang ist. Jeder Teil dieser Entscheidung ergibt sich aus dem Seed und den beiden Stadtstandorten, sie fällt also gleich aus, egal welche Stadt oder welcher Chunk zuerst entsteht. Eine Verbindung, die sich nicht ganz bauen lässt, wird gar nicht gelegt, nie halb: eine, die eine längere Brücke oder einen längeren Tunnel bräuchte, als die Einstellungen erlauben, über die Weltgrenze reichen, auf ein Waldanwesen treffen, zwei Städte näher zusammenbringen, als `structureSeparation` erlaubt, oder einen Abzweig zu nah an eine Ecke der Zellen setzen würde. Eine Stammstrecke wird nur zu einer Stadt hin gelegt, die tatsächlich gegründet wurde: Hält eine Obergrenze wie `structureMost` den Nachbarn auf oder bleibt er zu klein, um zu bestehen, wird weder eine Hälfte der Stammstrecke noch die Stichstrecke jenseits des eigenen Stadtendes gebaut. Festgelegte Städte werden genauso verbunden, eine pro Zelle; eine Zelle mit zwei festgelegten Städten verbindet keine. Andere Städte halten beim Wachsen Abstand zu den Stich- und Stammstrecken einer Verbindung, so wie sie Abstand zueinander halten.

**Gefälle.** Stich- und Stammstrecken sind Bahnlinien und werden genau wie eine Stadtlinie trassiert, überbrückt, untertunnelt und gekreuzt, mit `villageRailClimb` und den Trestle- und Tunneleinstellungen oben. Wo eine Stichstrecke auf die Stammstrecke trifft, liegen beide eben, und der Bahnhof daneben ebenso.

**Der Abzweig.** Eine Stichstrecke mündet nur in das nahe Gleis der Stammstrecke. Dieses Gleis ist dort unterbrochen, wo die Mitte der Stichstrecke darauf trifft, das linke Gleis der Stichstrecke biegt nach links hinein und das rechte nach rechts, und das ferne Gleis läuft gerade durch. Mit zwei Gleisen, die Stammstrecke oben und die Stichstrecke von unten kommend:

```
xxxxxxx
ooooooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

`x` ist Gleisbett und `o` Gleis. Wo die beiden Stichstrecken nur wenige Blöcke auseinander ankämen, rückt die erste Linie der zweiten Stadt auf die Höhe der ersten, und beide treffen sich stattdessen in einer Kreuzung: Jede Stichstrecke mündet genau wie oben nur in ihr eigenes nahes Gleis, beide Stammgleise sind an der Mitte der Stichstrecke unterbrochen, und keine Schiene kreuzt eine andere:

```
 xoxox
xxoxoxx
oooxooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

Eine eingleisige Stammstrecke hat kein zweites Gleis für die andere Stichstrecke, deshalb wird eine Verbindung, deren Stichstrecken sich auf einem einzigen Gleis frontal treffen würden, nicht gelegt. Mit nur einem Gleis biegt das Gleis der Stichstrecke nach links in das Gleis der Stammstrecke ein, und das Stammgleis jenseits dieser Kurve endet an ihr. Die Kurven werden mit festgelegter Form gesetzt, sodass Vanilla-Schienen genau dort abbiegen, wo der Abzweig gezeichnet ist, und nirgends sonst.

**Bahnhöfe.** Die letzten Reihen einer Stichstrecke vor dem Abzweig sind ein Bahnhof: Bahnsteige aus `villageRailLinkPlatformBlock` auf Höhe der Schiene, an der Außenkante mit `villageSubwayRailingBlock` eingefasst, mit einer Bank aus `villageSubwayBenchBlock` auf halber Länge jedes Bahnsteigs.

**U-Bahnen.** In einem Pack nur mit U-Bahn-Linien trägt die Verbindung die erste U-Bahn-Linie einer Stadt. Die Linie steigt zur Stammstrecke hin aus dem Boden, über eine Rampe von `villageSubwayDepth` mal `villageSubwayClimb` Reihen, und erreicht Bahnhof und Abzweig an der Oberfläche; eine solche Stadt wird nur auf einer Seite verbunden, auf der mit der kürzeren Verbindung, und die Stammstrecke ist eine oberirdische Bahn aus den `villageRail`-Einstellungen. Wo eine Stichstrecke keinen Platz für diese Rampe und ihren Bahnhof hat, steigt stattdessen die Stammstrecke zur U-Bahn hinab: Die ganze Verbindung, Stich- und Stammstrecken, bleibt in `villageSubwayDepth` unter der Erde, wird aus den `villageSubway`-Einstellungen gebaut und trifft sich im selben Abzweig, ohne Bahnhof.

#### Dorfschmuck

*dörfer und städte*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageDecor": ["mypack:street_flowers=2", "mypack:street_tree=1", "empty=3"]
  }
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `villageDecor` | Liste | leer | Dekoration, die entlang der Stadtstraßen gestreut wird, als name=gewicht-Paare, die Worldgen aus einem Pack nennen, mypack:street_flowers=2. Der Name empty ist der Anteil der Plätze, die leer bleiben, und ein Eintrag, der nicht als name=gewicht geschrieben ist, wird ausgelassen. Jeder dritte Block Randstreifen auf jeder Straßenseite lost die Liste aus, auf dem Boden dort, wie hoch er auch liegt, aber nicht in einem Tunnel, unter einem Grundstück, auf einem Platz oder weniger als zwei Blöcke vor einer Tür. Leer streut nichts |

### Strukturen

*was jede gruppe macht*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "structureSpacing": ["temples=24", "monuments=40", "mineshafts=200"],
    "structureSeparation": ["monuments=12"],
    "structureMost": ["villages=100"],
    "structureSpawners": ["dungeons=minecraft:zombie,minecraft:husk"],
    "structureMinDistanceFromSpawn": ["strongholds=1000"],
    "structureBiomes": ["temples=minecraft:desert,SANDY"],
    "structureBiomesAreBlacklist": ["temples=false"],
    "structureSpawns": ["temples=minecraft:witch:1:1:1", "monuments="],
    "structureAt": ["villages=1000,-500"],
    "structureAdaptation": ["villages=beard_thin", "mansions=bury", "monuments=none"],
    "terrainAdaptation": true,
    "villagePieces": ["mypack:smithy", "minecraft:village/plains/houses/plains_small_house_1"],
    "villagePiecesAreBlacklist": true,
    "villageBlocks": ["minecraft:cobblestone=mypack:ruby_brick", "minecraft:cobblestone=minecraft:mossy_cobblestone,20", "minecraft:oak_planks=minecraft:sandstone,100,under=minecraft:sand"]
  }
}
```

`control.structures` entscheidet diese Gruppe. Vanilla-Strukturen abschalten, ihr Abstand, ihre Trennung, Spawn-Entfernung, Biome, Spawns, Fixpunkte und Geländeanpassung.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `structureSpacing` | Liste | leer | Wie weit auseinander Vanilla-Strukturen gesät werden, in Chunks, als struktur=chunks-Einträge: die 1.12.2-Namen temples, monuments, mansions, mineshafts, strongholds, netherbridges, endcities und villages oder jede Structure-Set-Id wie pillager_outposts. Bei mineshafts bedeutet die Zahl einen Chunk von so vielen; bei strongholds ist es der Ringabstand. Netherfestungen behalten ihr eigenes Raster, das ein Abstand für netherbridges nicht erreicht |
| `structureSeparation` | Liste | leer | Wie nah zwei einer Struktur einander kommen dürfen, in Chunks, als struktur=chunks-Einträge; bei strongholds ist es die Ringstreuung. Tempel, mineshafts und netherbridges behalten ihre eigene Trennung, die dies nicht erreicht. Bei monuments wird eine Trennung, die so groß wie der Abstand ist, auf eins unter den Abstand gesenkt, und das Log sagt es |
| `structureMost` | Liste | leer | Wie viele Dörfer eine Dimension höchstens haben darf, als villages=anzahl (etwa villages=100); andere Strukturen haben keine Obergrenze: sind so viele gegründet, gründet kein Chunk ein weiteres, mit structureAt festgenagelte Chunks ausgenommen. 0 oder ein fehlender Eintrag setzt keine Obergrenze |
| `structureSpawners` | Liste von `structure=entity` | leer | Was der Spawner in einer Vanilla-Struktur spawnt, mit Komma getrennt für eine zufällige Wahl je Spawner. Die vier, die einen setzen, sind Verliese, Minen, Netherfestungen und Festungen |
| `structureMinDistanceFromSpawn` | Liste | leer | Wie weit vom Weltspawn eine Struktur beginnt, in Blöcken, als struktur=blöcke-Einträge. Gemessen vom Spawnpunkt der Welt; solange eine neue Welt ihn noch wählt, vom worldSpawn des Packs, wenn einer gesetzt ist, sonst vom Weltursprung |
| `structureBiomes` | Liste | leer | In welchen Biomen eine Struktur generieren darf, als struktur=biom,biom-Einträge mit Biom-Ids, den englischen Namen, die das Spiel zeigt, wie Birch Forest, bloßen Vanilla-Namen wie desert oder Biomtypen wie SANDY |
| `structureBiomesAreBlacklist` | Liste von `structure=true` oder `structure=false` | leer | Die Richtung der Biomliste je Struktur |
| `structureSpawns` | Liste | leer | Die Mobs, die eine Struktur spawnt, was das Biom auch sagt, als struktur=namespace:entity:gewicht:mindestens:höchstens-Einträge, kommagetrennt. Die Liste ersetzt die eigene Mobliste der Struktur ganz, welcher Art jeder Mob auch ist; eine leere Liste hinter dem = spawnt nichts |
| `structureAt` | Liste von `structure=x,z` | leer | Nagelt eine Struktur an eine genaue Stelle. Siehe [Strukturen an genauen Stellen](#strukturen-an-genauen-stellen) |
| `structureAdaptation` | Liste | Herrenhäuser `beard_thin`; jede andere Struktur behält ihre Vanilla-Anpassung | Wie sich das Gelände an eine Struktur anpasst, als struktur=modus-Einträge mit den Modi none, bury, beard_thin, beard_box und encapsulate |
| `terrainAdaptation` | boolean | `false` | Legt RDPLs eigene Stadtstraßen, ins Gelände eingelassen statt auf Stelzen über jeder Senke, und liest die villagePath- und villageRail-Optionen mit ihnen. Verändert das Gelände, eine damit gemachte Welt unterscheidet sich also von einer ohne. Städte werden so gesät, wie villageCitySpacing es vorgibt, und bei 0 keine |
| `villagePieces` | Liste | leer | Hier genannte Dorfgrundstücke, eines je Zeile, mit der vollen Id einer villages-Datei wie mypack:smithy, mit ihrem bloßen Namen oder mit dem Namen der Struktur, die ein Vorlagen-Grundstück baut. Solange villagePiecesAreBlacklist an ist, bleibt eine hier genannte Struktur außerdem leer, wo immer das Spiel sie lädt, die Dorfhäuser des Spiels selbst eingeschlossen, etwa minecraft:village/plains/houses/plains_small_house_1 |
| `villagePiecesAreBlacklist` | boolean | `true` | An werden die Grundstücke in villagePieces blockiert. Aus werden nur diese Grundstücke gebaut |
| `villageBlocks` | Liste | leer | Blöcke, aus denen Dorfgrundstücke gebaut werden, als original=ersatz-Paare, minecraft:cobblestone=mypack:ruby_brick. Jede Seite darf einen Zustand in eckigen Klammern tragen, dem das Original dann genau entsprechen muss. Ein Paar darf eine Chance von Hundert anhängen, minecraft:cobblestone=minecraft:mossy_cobblestone,20, aus dem Weltseed gewürfelt, wo der Block gelegt wird, at=block, um nur dort zu wirken, wo dieser Block steht, und under=block nur über ihm. Paare ohne Chance und Bedingung gelten zuerst, sodass ein bedingtes Paar ihr Ergebnis verwittern lassen kann. Es erfasst Felder und die Dorfhäuser des Spiels selbst, die eine Stadt baut; Straßen, Brunnen, Laternen und die Strukturen deiner Vorlagen-Grundstücke werden nie erfasst. Leer lässt jeden Block, wie er gelegt wird |

### Spawnen

*was jede gruppe macht*

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
    "threatItems": ["minecraft:diamond_sword=5,1", "minecraft:diamond=1,16,batch"],
    "threatLevels": [10, 25, 50],
    "threatMost": -1,
    "threatSpawnRate": 2.0,
    "threatNotice": 16.0,
    "threatSays": ["1=Something out there has taken notice of you.", "0=The world loses interest in you."]
  }
}
```

`control.spawning` entscheidet diese Gruppe. Mob-Spawn-Obergrenzen, feindliche Spawnraten und die Lichtgrenze.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `surfaceDayMonsterRate` | Zahl, 0.0 bis 4.0 | `1.0` | Faktor auf feindliches Spawnen an der Oberfläche bei Tag, `1.0` ist Vanilla, sodass Tagesspawnen an der Oberfläche abgeschaltet werden kann, ohne die Höhlen anzurühren |
| `surfaceNightMonsterRate` | Zahl, 0.0 bis 4.0 | `1.0` | Dasselbe für die Oberfläche bei Nacht |
| `undergroundDayMonsterRate` | Zahl, 0.0 bis 4.0 | `1.0` | Dasselbe unter Tage bei Tag |
| `undergroundNightMonsterRate` | Zahl, 0.0 bis 4.0 | `1.0` | Dasselbe unter Tage bei Nacht |
| `monsterCap` | int, -1 bis 1000 | `-1` | Wie viele Feindliche zugleich geladen sein dürfen. Vanilla ist 70, und `-1` lässt es in Ruhe |
| `creatureCap` | int, -1 bis 1000 | `-1` | Dasselbe für friedliche Tiere. Vanilla ist 10 |
| `ambientCap` | int, -1 bis 1000 | `-1` | Dasselbe für Fledermäuse und dergleichen. Vanilla ist 15 |
| `waterCreatureCap` | int, -1 bis 1000 | `-1` | Dasselbe für Tintenfische. Vanilla ist 5 |
| `monsterSpawnLight` | int, -1 bis 15 | `-1` | Das hellste Blocklicht, in dem ein feindlicher Mob noch spawnen darf, zusätzlich zu den Vanilla-Prüfungen. -1 behält allein die Vanilla-Regel. Spawner sind nicht betroffen |
| `threatItems` | Liste | leer | Gegenstände, die die Bedrohungsstufe eines Spielers heben, als item=stufe,anzahl-Einträge mit optionalem ,each oder ,batch am Ende, z. B. minecraft:diamond_sword=5,1 oder minecraft:diamond=1,16,batch. Each, der Standard, zählt die Stufe für jedes gehaltene Stück, höchstens anzahl davon; batch zählt sie einmal je volle anzahl gehaltener Stücke. Eine Anzahl über der Stapelgröße des Gegenstands wird auf die Stapelgröße gekürzt. Jede geladene Entity, die Gegenstände hält, ist ein Träger: das Hauptinventar, die Rüstung und die Nebenhand eines Spielers, ein liegender Stapel, alles mit Gegenstandsinventar wie ein Maultier mit Truhe oder eine Lore mit Truhe, und die gehaltenen Gegenstände und die Rüstung anderer Mobs. Leer schaltet die Bedrohungsstufe ab. Steht `control.spawning` auf `off`, gelten die Bedrohungseinstellungen der Config weiterhin |
| `threatLevels` | Liste | leer | Die Punktzahlen, ab denen ein Band beginnt, aufsteigend, sodass `10, 25, 50` drei Bänder macht. Leer schaltet die Bedrohungsstufe ab |
| `threatMost` | int, -1 bis 100000 | `-1` | Deckelt die Punktzahl. `-1` lässt sie ungedeckelt |
| `threatSpawnRate` | Zahl, 0.0 bis 8.0 | `1.0` | Skaliert feindliches Spawnen im Umkreis von 128 Blöcken um einen Träger im obersten Band, zusätzlich zu den anderen Faktoren; niedrigere Bänder nehmen einen anteiligen Teil |
| `threatNotice` | Zahl, 0.0 bis 64.0 | `0.0` | Wie viele Blöcke weiter feindliche Mobs, Vanilla-Mobs eingeschlossen, einen Träger im obersten Band sehen, wieder anteilig über die niedrigeren Bänder verteilt |
| `threatSays` | Liste von `band=nachricht` | leer | Die Zeilen, die in Gelb erscheinen, wenn das eigene Band eines Spielers wechselt; Band `0` ist die Zeile für den Rückfall unter das erste Band |

### Grundgestein

*was jede gruppe macht*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "flatBedrock": true,
    "flatBedrockDimensions": ["minecraft:overworld", "minecraft:the_nether"],
    "flatBedrockDimensionsAreBlacklist": false,
    "bedrockLayers": 1,
    "flatBedrockBiomes": ["minecraft:plains"],
    "flatBedrockBiomesAreBlacklist": true,
    "flatBedrockRoof": true
  }
}
```

`control.bedrock` entscheidet diese Gruppe. Flaches Grundgestein und seine Dimensions- und Biomlisten.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `flatBedrock` | boolean | `false` | Ersetzt das zerklüftete Grundgestein am Weltboden durch flache Schichten. Nur neue Chunks, solange `flatBedrockRetrogen` aus ist |
| `flatBedrockDimensions` | Liste | `["minecraft:overworld"]` | Die Dimensionen, in denen geebnet wird. Leer heißt jede |
| `flatBedrockDimensionsAreBlacklist` | boolean | `false` | An überspringt das Ebnen die genannten Dimensionen. Aus gilt es nur für sie |
| `bedrockLayers` | int, 1 bis 5 | `1` | Wie viele Schichten Grundgestein bleiben |
| `flatBedrockBiomes` | Liste von Biomnamen | leer | Die Biome, in denen geebnet wird, nach sprechendem oder Registrierungsnamen. Leer heißt jedes Biom |
| `flatBedrockBiomesAreBlacklist` | boolean | `false` | An überspringt das Ebnen die genannten Biome. Aus gilt es nur für sie |
| `flatBedrockRoof` | boolean | `false` | Ebnet auch die Grundgesteinsdecke, wo eine Dimension eine hat, etwa das Netherdach |
| `flatBedrockFiller` | Block | leer | Was das weggenommene Grundgestein ersetzt. Leer wählt je Dimension: Stein, Netherrack, Endstein |
| `flatBedrockFillers` | Liste von `dimension=block` | `["minecraft:the_nether=minecraft:netherrack", "minecraft:the_end=minecraft:end_stone"]` | Ein Füller je Dimension, der `flatBedrockFiller` für die genannten Dimensionen überschreibt |
| `flatBedrockBiomeTypes` | Liste | leer | Biomtypen, in denen geebnet wird, neben flatBedrockBiomes, als Biom-Tag wie minecraft:is_ocean oder als 1.12.2-Typname wie OCEAN. flatBedrockBiomesAreBlacklist gilt auch für sie |
| `flatBedrockRetrogen` | boolean | `false` | Ebnet das Grundgestein auch in schon vorhandenen Chunks. Jeder Chunk wird einmal bearbeitet und merkt es sich, und es lässt sich nicht rückgängig machen: Das ursprüngliche Muster ist nirgends festgehalten |
| `flatBedrockRetrogenKey` | Text | `0000` | Ändern, damit jeder Chunk erneut fürs Ebnen des Grundgesteins infrage kommt |

### Langsameres Ticken in der Ferne

*was jede gruppe macht*

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

`control.entities` entscheidet diese Gruppe. Das langsamere Tempo von Entities fern von jedem Spieler.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `slowDistantEntities` | boolean | `true` | Entities fern von jedem Spieler seltener ticken. Nichts bleibt je ungetickt, nur in langsamerem Tempo |
| `slowedKinds` | Liste | `["items", "experience"]` | Welche Sorten weniger Ticks bekommen: items, experience, projectiles, Letzteres sind Pfeile, Dreizacke, geworfene Schneebälle, Eier, Tränke, Erfahrungsfläschchen und Enderperlen sowie Lamaspucke. Alles, was selbst denkt, wird stattdessen immer gebremst, ohne hier genannt zu werden: Es entscheidet seltener, was es als Nächstes tut, und bewegt sich weiter jeden Tick. Maschinen werden nie gebremst |
| `slowDistance` | int, 64 bis 4096 | `192` | Wie weit vom nächsten Spieler, in Blöcken, bevor ein Chunk gebremst wird. Das Spiel hört jenseits von 64 auf, einem Spieler von den meisten Entities zu erzählen, darunter also nichts |
| `slowRate` | int, 1 bis 20 | `4` | Ein Tick von so vielen wird einem gebremsten Chunk gegeben. 1 ist gar keine Bremsung, 20 ist einmal je Sekunde |
| `neverSlowed` | Liste | leer | Entities, die in Ruhe gelassen werden, wie weit sie auch entfernt sind, als namespace:name |
| `slowRecheck` | int, 1 bis 100 | `20` | Wie oft, in Ticks, die Entfernung zum nächsten Spieler neu ermittelt wird. Jeder Spieler zählt für sich, wer allein weit weg ist, hat also weiterhin seinen eigenen ruhigen Raum um sich |

### Land, Halten und was der Mod sagt

*was jede gruppe macht*

`control.chunks` entscheidet diese Gruppe. Der Spawn-Chunk-Radius, die Vorgenerierung, Retrogen und der Reset, die Willkommenszeilen und die Says-Karte.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `retrogen` | boolean | `false` | Vorhandene Chunks bei Worldgen-Einträgen mit \"retrogen\": true nachholen. Aus bleiben schon vorhandene Chunks unangetastet. Chunks werden so oder so beim Generieren markiert, ein späteres Einschalten berührt also nur Chunks, die älter sind als das Pack |
| `adoptExistingChunks` | boolean | `false` | Schon vorhandene Chunks behandeln, als hätte dieses Pack sie generiert, sie also markieren, statt sie dem Retrogen zu überlassen. Einschalten, wenn ein Mod ersetzt wird, der dasselbe Erz schon generiert hat, damit Retrogen es nie verdoppelt. Später hinzugefügte Worldgen-Einträge holen sie weiterhin nach |
| `saysCard` | boolean | `false` | Die Zeilen, die dieser Mod sagt, die Begrüßung, den Hinweis zum Landbau für Spieler, die mitten im Lauf beitreten, und das Ende des Laufs (der laufende Fortschritt bleibt in der Aktionsleiste) sowie die Bedrohungszeilen, als Karte unten rechts statt im Chat zeigen. Die Karte gleitet herein, bleibt acht Sekunden und verblasst, und erscheint auch über einem offenen Bildschirm |
| `saysIcon` | Text | leer | Ein Item, das auf der Karte gezeichnet wird, z. B. minecraft:compass. Leer zeichnet keines |
| `saysColor` | Text | leer | Die Hintergrundfarbe der Karte als Hex, z. B. 1E2630. Leer nimmt ein dunkles Schiefergrau |
| `saysImage` | Text | leer | Ein PNG aus den Client-Assets des Packs, über die Karte gestreckt als ihr Hintergrund, z. B. rubyworld:textures/gui/card.png, über die Farbe gezeichnet. Leer zeichnet keines |
| `pregenOnNewWorld` | int, 0 bis 8192 | `0` | Wie weit um den Spawn, in Chunks, das Land einer Welt gebaut wird, bevor jemand sie spielt. Das Spiel baut 12 Chunks um den Spawn von sich aus, 12 ist also die Untergrenze und 0 meint diese Untergrenze statt gar nichts: Der Boden, den das Spiel ohnehin gebaut hätte, wird übernommen und in einem geordneten Zug beleuchtet, statt hinterherzutröpfeln. Höher setzen, um weiter zu reichen als das Spiel |
| `pregenToBorder` | boolean | `false` | Ob das Land einer neuen Welt bis zu ihrer Weltgrenze statt bis zu einer festen Zahl Chunks gebaut wird, zentriert auf die Grenze statt auf den Spawn. Eine Welt, deren Grenze nie hereingezogen wurde, hat keine Grenze zu erreichen und wird übergangen |
| `pregenAllDimensions` | boolean | `false` | Das Land jeder Dimension bauen, die der Server hält, Mod-Dimensionen eingeschlossen, die Oberwelt zuerst und der Rest in Id-Reihenfolge, statt nur die in pregenDimensions. In pregenDimensionsWhenEntered genannte bleiben weiterhin ihrem ersten Besucher überlassen |
| `pregenResume` | boolean | `false` | Ob ein gestoppter oder abgebrochener Durchlauf beim nächsten Laden der Welt dort weitermacht, wo er aufgehört hat, statt von vorn zu beginnen |
| `pregenChunksInFlight` | int, 1 bis 512 | `32` | Wie viele Chunks ein Landbau-Durchlauf beim Spiel auf einmal anfordert. Mehr hält die Generierungs-Threads beschäftigter und den Server weniger ansprechbar für die, die gehalten zusehen |
| `pregenBackup` | boolean | `false` | Die Welt in eine unberührte Sicherung kopieren, sobald die Vorgenerierung fertig ist und die Spieler noch gehalten werden. Die Kopie ist das, was ein Reset wiederherstellt, und eine Kopie, deren Packs nicht mehr passen, wird weggeworfen und neu angelegt |
| `resetClearsEntities` | boolean | `true` | Jede Entity entfernen, die kein Spieler ist, wenn die Karte zurückgesetzt wird |
| `resetClearsScores` | boolean | `true` | Jedes Ziel, das das Pack führt, beim Kartenreset auf nichts zurücksetzen, damit eine neue Partie bei null beginnt. Die Teams selbst bleiben |
| `resetClearsInventory` | boolean | `false` | Beim Kartenreset das Inventar jedes Spielers leeren, Rüstung und Zweithand eingeschlossen |
| `resetClearsExperience` | boolean | `false` | Beim Kartenreset die Erfahrung jedes Spielers auf Stufe null zurücksetzen |
| `spawnChunkRadius` | int, 0 bis 1024 | `128` | Wie weit vom Spawnpunkt, in Blöcken, Chunks geladen gehalten werden, ob ein Spieler da ist oder nicht, auf ganze Chunks gerundet als (Blöcke + 8) / 16 in jede Richtung, 128 hält also 8 Chunks in jede Richtung. Beim Start einer Welt bereitet die Oberwelt ein Quadrat vor, das in jede Richtung 4 Chunks größer ist, bevor der Server bereit ist, bei 128 also 25 mal 25 Chunks. 0 hält und bereitet gar keine vor, der Spawnbereich entlädt sich also wie jeder andere Ort. RDPL hält die Chunks mit eigenen Tickets, auf 1.21.1 bewirkt die Spielregel spawnChunkRadius daher nichts, solange dieser Schlüssel gilt |
| `spawnChunkRadii` | Liste | leer | Ein Radius für die Oberwelt, geschrieben als dimension=blöcke, etwa minecraft:overworld=64, der spawnChunkRadius überschreibt. Nur die Oberwelt hat Spawn-Chunks, ein Eintrag für eine andere Dimension ändert also nichts |
| `welcomeSays` | Liste | `[WELCOME]` | Willkommenszeilen, bei jedem Login und nach der Vorgenerierung in Grün gezeigt. Ein bloßer Eintrag ist die Zeile für überall; ein dimension=nachricht-Eintrag überschreibt sie für diese Dimension und begrüßt außerdem jede Ankunft dort, z. B. minecraft:the_nether=Welcome to the Nether!. Eine leere Nachricht hinter dem = stellt diese Dimension stumm; eine leere Liste zeigt nichts. Auf diesem Standardwert spricht sie die Sprache jedes Spielers |
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

### Void-Welt

*was jede gruppe macht*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "voidWorld": true,
    "voidWorldDimensions": ["minecraft:overworld"],
    "voidWorldDimensionsAreBlacklist": false,
    "voidPlatformBlock": "minecraft:stone",
    "voidPlatformHeight": 64,
    "voidPlatformSize": 5
  }
}
```

`control.voidWorld` entscheidet diese Gruppe. Void-Welt-Generierung und ihre Plattform.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `voidWorld` | boolean | `false` | Generiert die gelisteten Dimensionen als leeren Raum mit einer Plattform am Spawnpunkt und nichts Lebendigem, über das erzeugte Preset für die drei aus Vanilla und über die eigenen Dimensionsdateien eines Packs; jede andere gelistete Dimension wird geleert, während ihr Land entsteht. Die Wahl wird beim Anlegen der Welt mit ihr gespeichert, späteres Ein- oder Ausschalten lässt eine bestehende Welt also, wie sie war |
| `voidWorldDimensions` | Liste | `["minecraft:overworld"]` | Welche Dimensionen geleert werden, nach Id. Leer heißt keine, oder jede Dimension, wenn voidWorldDimensionsAreBlacklist an ist |
| `voidWorldDimensionsAreBlacklist` | boolean | `false` | An sind die genannten Dimensionen die, die in Ruhe gelassen werden |
| `voidPlatformBlock` | Block | `minecraft:stone` | Woraus die Plattform besteht |
| `voidPlatformHeight` | int, -2032 bis 2031 | `64` | Das y, auf dem die Plattform der Void-Welt sitzt |
| `voidPlatformSize` | int, 1 oder mehr | `9` | Wie breit die Plattform ist, auf eine ungerade Zahl abgerundet, damit sie mittig auf dem Spawn sitzt |
| `voidWorld` | Text | `default` | Void-Welt-Generierung und ihre Plattform [default\|global\|off] |

### Der Drache

*was jede gruppe macht*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "dragonFight": true
  }
}
```

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `dragonFight` | boolean | `true` | Ob das Ganze überhaupt stattfindet: der Drache, seine Leiste, die Kristalle, der Brunnen, auf dem er steht, und das Wiederbeleben, das ein Spieler mit Enderkristallen starten würde. Gehört zur Gruppe `structures` |

`dragonFight` gehört zur Gruppe `structures` und entscheidet, ob das Ganze überhaupt stattfindet: der Drache, seine Leiste, die Kristalle, der Brunnen, auf dem er steht, und das Wiederbeleben, das ein Spieler mit Enderkristallen starten würde. Ein geleertes Ende lässt ihn weg, solange ein Pack nicht darum bittet, und ein gewöhnliches Ende hat ihn, solange ein Pack nicht etwas anderes sagt – `dragonFight` lohnt sich also in beide Richtungen.

### Gelände

*was jede gruppe macht*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldSeed": "Hollow Ridge",
    "worldGameMode": "creative",
    "worldName": "Ruby World",
    "worldType": "largebiomes",
    "worldTypeExceptions": ["flat", "debug_all_block_states"],
    "generatorOptions": {"seaLevel": 63, "useMansions": false},
    "worldMinHeight": -128,
    "worldMaxHeight": 320,
    "deepStone": "minecraft:blackstone",
    "noiseCaves": "deep",
    "worldSpawn": "0,72,0",
    "worldBorder": 4096,
    "worldTime": 6000,
    "worldDifficulty": ["normal", "minecraft:the_nether=hard"],
    "caveRegionPlainWeight": 4,
    "caveRegionCells": 128,
    "caveRegionCellsY": 64,
    "worldGravity": ["0.17", "minecraft:overworld=1.0"],
    "worldFallDamage": ["0.17"],
    "worldJumpStrength": ["1.0"],
    "worldTerminalVelocity": ["1.0"],
    "weatherCeiling": ["minecraft:overworld=128"],
    "cloudHeight": ["minecraft:overworld=384"],
    "worldBelow": ["minecraft:overworld=minecraft:the_nether"],
    "worldAbove": ["minecraft:the_nether=minecraft:overworld"],
    "worldSeamEntities": true,
    "worldSeamBedrock": false
  }
}
```

`control.terrain` entscheidet diese Gruppe. Name, Seed und Spielmodus der Welt beim Erstellen, generatorOptions, die Höhlenregionen, die Wolkenhöhe, die Nähte zwischen den Welten und der Schwierigkeitsgrad.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `worldSeed` | Text | leer | Der Seed, mit dem jede neue Welt gemacht wird, so geschrieben, wie er getippt würde: eine Zahl wird genommen, wie sie ist, alles andere wird in eine verwandelt, wie das Spiel es tut. Auch ein dedizierter Server macht seine Welt damit. Leer lässt die Wahl in Ruhe |
| `worldGameMode` | Text | leer | Wie jede neue Welt gestartet wird, eines von survival, hardcore, creative, adventure oder spectator. Hardcore ist Überleben, bei dem der Tod die Welt beendet, spielstandweit, dasselbe wie die Wahl auf dem Weltbildschirm. Leer lässt es, wie es der Ersteller der Welt gewählt hat. Der Weltbildschirm bietet nur survival, hardcore und creative an, adventure und spectator werden daher gesetzt, während die Welt gemacht wird. Ein dedizierter Server setzt jede Welt bei jedem Start auf den Modus aus server.properties, dort wird der Modus des Packs daher vor dem Laden der Welt in server.properties geschrieben (gamemode und hardcore) |
| `worldName` | Text | leer | Wie eine neue Welt heißt, wenn der Bildschirm zum Erstellen sich öffnet. Leer lässt es, wie das Spiel sie benennt |
| `worldType` | Text | leer | Der Welttyp, auf dem die geformte Welt aufbaut, einer von default, largebiomes, amplified oder flat, wobei die 1.12.2-Namen customized und default_1_1 als default gelesen werden; flat ist eine Flachwelt-Oberwelt aus den Schichten in generatorOptions, mit den Städten des Packs darauf. Die Form unten (Höhen, Tiefenstein, Meereshöhe, Grundgestein, Void) wird als eigenes Welt-Preset erzeugt, unter Welttyp auf dem Weltbildschirm gelistet und dort gewählt, was auch gewählt wurde. Leer baut auf default auf |
| `worldTypeExceptions` | Liste | `["flat", "debug_all_block_states"]` | Welttypen, die ein Spieler wählt und die das erzeugte Preset stehen lässt, etwa flat oder debug_all_block_states. Leer heißt, jede Wahl wird ersetzt |
| `generatorOptions` | Text | leer | Bei worldType flat stattdessen die Schichten von unten nach oben, als der 1.12.2-Flachwelt-Text 3;minecraft:bedrock,59*minecraft:stone,4*minecraft:dirt,minecraft:grass_block;1;village oder als Liste von Schichten; die Zahl nach den Schichten ist das Biom, und village, biome_1, mineshaft, stronghold, oceanmonument, lava_lake und decoration danach schalten diese ein. Sonst die Geländeeinstellungen der Oberwelt als JSON-Objekt, die Schlüssel, die der angepasste Welttyp von 1.12.2 schrieb. Gelesen werden hier: seaLevel, useLavaOceans, fixedBiome sowie useCaves, useRavines, useDungeons, useLavaLakes, useStrongholds, useVillages, useMineShafts, useTemples, useMonuments und useMansions auf false. Nur beim Erstellen auf eine Welt angewandt. Leer lässt das Gelände, wie der Welttyp es macht |
| `worldMinHeight` | int, -2032 bis 2016 | `-64` | Der unterste Block der Oberwelt, ein Vielfaches von 16 bis hinunter zu -2032. Der eigene Boden des Spiels ist -64; tiefer macht eine Tiefenwelt unter dem Vanilla-Gelände, massiver Stein, bis die Worldgen-Ebene ihn schnitzt oder noiseCaves die Höhlen des Spiels hinunterträgt. Nur über das erzeugte Preset angewandt |
| `worldMaxHeight` | int, -2016 bis 2032 | `320` | Der Block über der Decke der Oberwelt, ein Vielfaches von 16 bis 2032, höchstens 4064 über worldMinHeight. Die eigene Decke des Spiels ist 320; höher lässt offenen Himmel über dem Vanilla-Gelände |
| `deepStone` | Text | leer | Der Block, aus dem die Welt unter dem Vanilla-Gelände besteht, wenn worldMinHeight unter -64 geht, etwa der eigene Deepslate eines Packs. Er geht über die acht Schichten unter -64 in Deepslate über, so wie Deepslate in Stein übergeht. Leer behält Stein |
| `noiseCaves` | Text | `off` | Wo die Höhlen, Tunnel, Nudeln und Aquifere des Spiels weitergehen, wenn worldMinHeight unter -64 geht: off lässt die Welt unter dem Vanilla-Gelände als massiven Tiefenstein für die Worldgen-Ebene zum Schnitzen, deep trägt sie bis zum Boden hinunter, mit den Lavaseen in dessen unterste zehn Schichten verlegt, world bedeutet in dieser Version dasselbe, weil das Vanilla-Gelände sie ohnehin hat |
| `worldSpawn` | Text | leer | Wo jede neue Welt spawnt, geschrieben als x,z oder x,y,z. Ohne y wird die durchschnittliche Bodenhöhe der Welt genommen, eins über der Meereshöhe oder auf einer Flachwelt die Oberkante der Schichten, und das Spiel sucht dort dann festen Stand wie bei jedem Spawn. Nur beim Erstellen auf eine Welt angewandt. Leer überlässt die Wahl dem Spiel |
| `worldBorder` | int, 0 bis 60000000 | `0` | Wie breit, in Blöcken, die Weltgrenze in jeder neuen Welt steht. Nur beim Erstellen auf eine Welt angewandt. 0 lässt die Grenze, wo das Spiel sie setzt |
| `worldTime` | int, -1 bis 23999 | `-1` | Die Tageszeit der Oberwelt festhalten, in Ticks, dieselbe Zahl, die /time set nimmt, 18000 ist also Mitternacht. Die Uhr steht und bewegt sich nie: /time set kann sie nicht verstellen, darunter zählt der Tag weiter, und wird die Einstellung entfernt, kommt diese Zeit zurück. -1 lässt die Zeit laufen |
| `worldDifficulty` | Liste | leer | Den Schwierigkeitsgrad festhalten, einer von peaceful, easy, normal oder hard. Ein bloßer Schwierigkeitsgrad gilt für jede Dimension, und ein als dimension=schwierigkeit geschriebener Eintrag, etwa minecraft:the_nether=hard, gilt nur für diese Dimension und geht dem bloßen vor. Die eigene Einstellung der Welt bleibt, wie sie war, und kommt zurück, wenn der Eintrag entfernt wird. Leer lässt es, wie gewählt |
| `caveRegionPlainWeight` | int, 0 oder mehr | `4` | Das Gewicht des schlichten, regionslosen Untergrunds gegenüber den eigenen Gewichten der Höhlenregionen. Höher lässt mehr Untergrund ohne Region |
| `caveRegionCells` | int, 16 oder mehr | `128` | Wie breit eine Höhlenregionszelle in Blöcken ist. Höhlenregionen aus Packs werden in Zellen etwa dieser Größe über den Untergrund gemalt |
| `caveRegionCellsY` | int, 16 oder mehr | `64` | Wie hoch eine Höhlenregionszelle in Blöcken ist |
| `worldGravity` | Liste | leer | Schwerkraft skalieren, als Faktor auf Vanilla, wobei 1.0 unverändert und 0.17 mondartig ist. Gilt für Spieler, Mobs, fallengelassene Items, fallende Blöcke, Pfeile, Geworfenes, TNT und Erfahrungskugeln. Ein bloßer Wert gilt für jede Dimension, und ein als dimension=wert geschriebener Eintrag gilt für diese Dimension allein und gewinnt über den bloßen. Leer lässt die Schwerkraft in Ruhe |
| `worldFallDamage` | Liste | leer | Sturzschaden auf dieselbe Weise skalieren, 0.5 halbiert ihn und 2.0 verdoppelt ihn |
| `worldJumpStrength` | Liste | leer | Sprungkraft auf dieselbe Weise skalieren, 1.5 springt anderthalbmal so hoch |
| `worldTerminalVelocity` | Liste | leer | Die höchste Fallgeschwindigkeit eines Mobs oder Spielers auf dieselbe Weise skalieren, 0.5 fällt mit halber Vanilla-Höchstgeschwindigkeit |
| `weatherCeiling` | Liste | leer | Das höchste y, das Regen und Schnee erreichen, als dimension=y-Einträge. Eine bloße Zahl gilt für jede Dimension. Darüber fällt kein Regen, setzt sich kein Schnee ab, füllen sich keine Kessel, schlägt kein Blitz ein und wird kein Niederschlag gezeichnet; darunter bleibt das Wetter unverändert. Eis ist Temperatur und kein Niederschlag, bildet sich also weiterhin über der Linie. Leer heißt keine Grenze |
| `cloudHeight` | Liste | leer | Das y, auf dem Wolken gezeichnet werden, als dimension=y-Einträge. Eine bloße Zahl gilt für jede Dimension. Sie geht dem eigenen `cloudHeight` einer Pack-Dimension vor. Leer behält die eigene Wolkenhöhe des Spiels, 192 in der Oberwelt |
| `worldBelow` | Liste | leer | Eine andere Dimension unter diese stapeln: wer aus dem Boden der Welt fällt, kommt in der genannten Dimension an, unter deren Decke bei demselben x und z, weiter fallend. Einträge werden als dimension=ziel geschrieben, etwa minecraft:overworld=minecraft:the_nether, um den Nether unter die Oberwelt zu hängen; eine bloße Id gilt für jede Dimension. Um durchzugraben, muss das Grundgestein des Bodens weggelassen sein, was worldSeamBedrock entscheidet. Leer heißt, der Boden bleibt der Boden |
| `worldAbove` | Liste | leer | Dasselbe für die Decke: wer über die Decke der Welt steigt, kommt in der genannten Dimension an, über deren Boden. Geschrieben wie worldBelow |
| `worldSeamEntities` | boolean | `true` | Ob liegende Items, Mobs und andere Entities die Weltnähte ebenfalls passieren, oder nur Spieler. Reiter und Reittiere wechseln eines nach dem anderen |
| `worldSeamBedrock` | boolean | `false` | Das Grundgestein an einer Nahtgrenze trotzdem behalten. Aus generiert eine Dimension, deren Boden oder Decke eine worldBelow- oder worldAbove-Naht trägt, dort kein Grundgestein, der Weg hindurch lässt sich also graben. Schon generierte Chunks behalten, was sie haben |

### Rezepte

*was jede gruppe macht*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockRecipes": true,
    "recipeWhitelist": ["minecraft", "mypack"],
    "blockedRecipeMods": ["tconstruct"],
    "recipeMatch": "recipe",
    "blockFurnaceRecipes": true,
    "furnaceWhitelist": ["minecraft", "mypack"],
    "blockedFurnaceMods": ["tconstruct"],
    "logBlockedRecipes": true
  }
}
```

`control.recipes` entscheidet diese Gruppe. Crafting- und Ofenrezeptsperre und ihre Whitelists. Die Ofenrezeptsperre trifft auch Schmelzofen-, Räucherofen- und Lagerfeuerrezepte, da 1.12.2 alle Kochrezepte in der einen Ofenliste führte. Steinsägen- und Schmiedetischrezepte werden nie gesperrt, da es sie in 1.12.2 nicht gab.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `blockRecipes` | boolean | `false` | Entfernt jedes Handwerksrezept außer denen der Mods in `recipeWhitelist`. Nichts ist von Haus aus ausgenommen, also nimm den eigenen Namespace des Packs auf, um seine Rezepte zu behalten |
| `recipeWhitelist` | Liste von Mod-Ids | `["minecraft"]` | Die Mods, deren Handwerksrezepte überleben |
| `blockedRecipeMods` | Liste von Mod-Ids | leer | Mods, deren Handwerksrezepte rundweg entfernt werden, was die Whitelist auch sagt |
| `recipeMatch` | `recipe`, `output` oder `both` | `recipe` | Woraus die Mod-Id beim Blockieren von Handwerksrezepten gelesen wird: aus dem Namen des Rezepts, aus dem hergestellten Gegenstand, oder aus beidem, was blockiert, wenn eines passt, und verschont, wenn eines auf der Whitelist steht |
| `blockFurnaceRecipes` | boolean | `false` | Dasselbe für Ofenrezepte; der Mod wird aus dem hergestellten Gegenstand gelesen |
| `furnaceWhitelist` | Liste von Mod-Ids | `["minecraft"]` | Die Mods, deren Ofenrezepte überleben |
| `blockedFurnaceMods` | Liste von Mod-Ids | leer | Mods, deren Ofenrezepte rundweg entfernt werden |
| `logBlockedRecipes` | boolean | `true` | Protokolliert je Mod, was blockiert wurde |
| `furnace` | boolean | `true` | furnace/*.json-Dateien anwenden, die Ofenrezepte hinzufügen und entfernen **Nur Config.** |
| `removals` | boolean | `true` | recipe_removals/*.json-Dateien anwenden, die Craftingrezepte nach Name, Namespace oder Ergebnis löschen **Nur Config.** |
| `skipMissingItems` | boolean | `true` | Rezepte überspringen, die ein nicht registriertes Item nutzen, statt sie scheitern zu lassen. Die Anzahl wird einmal protokolliert **Nur Config.** |

### Befehle

*was jede gruppe macht*

`control.commands` entscheidet diese Gruppe. Wer die eigenen Befehle des Mods ausführen darf: die goto-Berechtigungsstufen.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `gotoLevel` | int, 0 bis 4 | `3` | Die Berechtigungsstufe für /rdplserver goto <name>, der den Absender zur nächsten bringt. 3 ist ein Operator, die Stufe, auf der jeder andere Teil des Befehls sitzt. 2 lässt auch einen Befehlsblock ihn ausführen, ein Pack kann den Sprung also auf einen Knopf oder eine Druckplatte legen, ohne jemandem den Rest des Befehls zu geben. 0 lässt jeden Spieler ihn tippen. Die anderen Teile von /rdplserver bleiben bei 3, was auch hier steht |
| `gotoNextLevel` | int, 0 bis 4 | `3` | Die Berechtigungsstufe für /rdplserver goto <name> next, der die zuletzt angesteuerte überspringt und eine andere findet. Dieselbe Skala wie gotoLevel |
| `gotoBackLevel` | int, 0 bis 4 | `3` | Die Berechtigungsstufe für /rdplserver goto <name> back, der den Absender zur vorherigen zurückbringt. Dieselbe Skala wie gotoLevel |
| `gotoPlaceLevels` | Liste | leer | Berechtigungsstufen für einzelne Orte, als name=stufe-Einträge, einer je Zeile, die die drei Einstellungen oben für diesen Ort allein und in allen drei Formen überschreiben. Der Name ist das, was du hinter goto tippen würdest, ein Vanilla-Name wie Village oder Mansion oder ein Name, den ein Pack mit locateAs für seine eigenen Strukturen angemeldet hat. Dieselbe Skala: 3 ein Operator, 2 auch ein Befehlsblock, 0 jeder. Ein Pack kann so den Weg zu seinen eigenen Ruinen öffnen, während jede Vanilla-Struktur verschlossen bleibt, oder umgekehrt. Ein nirgends angemeldeter Name wird mit einer Notiz im Log ignoriert |

### Worldgen, nur Config

*was jede gruppe macht*

Diese `worldgen`-Schlüssel gehören zu keiner Gruppe und sind allein die der Config.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `worldgenDebug` | boolean | `false` | Die Debug-Zeilen, auf die andere Meldungen verweisen, nach logs/rdpl.log schreiben, etwa welches Pack eine Datei geliefert hat und was jeder Befehl getan hat. Sehr ausführlich |
| `worldTemplate` | Text | `auto` | Die Einstellungen welcher Weltvorlage gelten. Ein Pack fügt eine in worldtemplates/*.json hinzu, und du nennst sie hier als namespace:name. 'auto' nimmt die Vorlage aus dem Pack mit der höchsten Priorität. Leer nimmt keine |
| `tellWorldType` | boolean | `true` | Einem Spieler beim Beitritt zu einer mit dem erzeugten Preset gemachten Welt im Chat sagen, welche Vorlage sie geformt hat. Ein Pack kann das nicht setzen |
| `worldBorderLimit` | int, 1 bis 60000000 | `60000000` | Die breiteste Grenze, die ein Pack über worldBorder verlangen darf. Ein Pack, das mehr verlangt, wird abgewiesen, und die Grenze bleibt, wo das Spiel sie setzt. Ein Pack kann das nicht setzen |
| `retrogenKey` | Text | `0000` | Ändern, damit jeder Chunk für jeden Worldgen-Eintrag wieder für Retrogen infrage kommt. Neue Adern werden über das gelegt, was schon da ist |
| `retrogenChunksPerTick` | int, 1 oder mehr | `2` | Wie viele schon generierte Chunks je Tick nachgeholt werden. Höher ist schneller, ruckelt aber mehr |

### Die Kategorie `packs`

*was jede gruppe macht*

Wie Pack-Ordner gefunden und ausgeliefert werden. Nur Config.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `rootDirectory` | Text | `rdploader` | Ordner, aus dem Packs geladen werden, relativ zum .minecraft-Verzeichnis. Ein absoluter Pfad geht auch. Braucht einen Neustart |
| `overrideResourcePacks` | boolean | `true` | Das Asset-Pack über die vom Spieler gewählten Ressourcenpakete und die eigenen Datenpakete der Welt setzen. Ein Pack namens RDPLO... überschreibt immer, RDPLN... nie |
| `warnOnCaseMismatch` | boolean | `true` | Warnen, wenn eine Datei nur passt, weil das Dateisystem Groß- und Kleinschreibung nicht unterscheidet. Solche Packs brechen unter Linux |
| `logContents` | boolean | `false` | Jedes gefundene Pack protokollieren und wie viele Dateien es liefert |
| `traceUnresolvedVariables` | boolean | `false` | Beim ersten Mal einen Stacktrace protokollieren, wenn eine Datei mit einem '#' im Namen angefordert wird, mit dem, was danach gefragt hat |

### Die Kategorie `content`

*was jede gruppe macht*

Blöcke, Items, Flüssigkeiten und alles andere, was Packs definieren. Nur Config.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `load` | boolean | `true` | Die Blöcke, Items, Flüssigkeiten, Materialien und Kreativ-Tabs registrieren, die Packs definieren, und ihre Expositionen laden. Braucht einen Neustart |
| `vanillaClients` | boolean | `false` | Reine Vanilla-Clients bedienen: nichts aus einem Pack wird registriert, keine Blöcke, Items, Flüssigkeiten oder Kreativ-Tabs, und keine Expositionen werden geladen, sodass ein Client ohne den Mod beitreten kann. Alles, was nur auf dem Server lebt, gilt weiterhin. Braucht einen Neustart |
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

*was jede gruppe macht*

Beute, Funktionen und Registry-Namen. Nur Config.

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `lootInjections` | boolean | `true` | loot_injections/*.json-Dateien anwenden, die Pools zu Beutetabellen hinzufügen, die es schon gibt, statt die ganze Tabelle zu ersetzen |
| `playerLoot` | boolean | `true` | player_loot/*.json-Dateien anwenden, die beim Tod eines Spielers eine Beutetabelle würfeln und das Ergebnis fallen lassen, zusätzlich zum oder anstelle des Inventars |
| `registryRemaps` | boolean | `true` | registry_remap-Dateien anwenden, die einen Registry-Eintrag umbenennen, damit vor der Umbenennung gespeicherte Welten ihre Blöcke und Items behalten, statt sie zu verlieren |
| `anvils` | boolean | `true` | anvils/*.json-Dateien anwenden, mit denen ein Amboss einem Gegenstand für Erfahrungsstufen genannte Verzauberungen gibt, beim Herausnehmen einen Fortschritt einbringt und den Gegenstand bis dahin vom Gebrauch zurückhält |
| `blockDrops` | boolean | `true` | block_drops/*.json-Dateien anwenden, die dem Drop eines Blocks bei jedem Zerbrechen etwas hinzufügen oder ihn ersetzen, Erfahrung eingeschlossen, für Blöcke, die keinem Pack gehören |
| `functions` | boolean | `true` | .mcfunction-Dateien aus Packs laden, damit sie in jeder Welt funktionieren |

### Die Kategorie `tweaks`

*was jede gruppe macht*

Kleine Änderungen daran, wie Vanilla sich verhält. Nur Config; siehe [Bonus: Vanilla-Tweaks](#bonus-vanilla-tweaks).

| Einstellung | Typ | Standard | Was sie tut |
| --- | --- | --- | --- |
| `promptLeafDecay` | boolean | `true` | Blätter, die ihren Baum verlieren, verwelken binnen einer Sekunde, statt auf Random-Ticks zu warten |
| `lenientPaths` | boolean | `true` | Pfade lassen sich unter einem Block anlegen und bleiben liegen, wenn einer darübergesetzt wird |
| `unbreakableSpawners` | boolean | `false` | Mobspawner lassen sich weder abbauen noch sprengen. Der Kreativmodus entfernt sie weiterhin. Braucht einen Neustart |
| `experimentalWarning` | boolean | `false` | Die Warnung des Spiels vor experimentellen Einstellungen zeigen, wenn eine Welt erstellt oder geöffnet wird. Aus beantwortet sie, als hättest du auf Fortfahren geklickt |
| `privacy` | boolean | `true` | Telemetrie und Chat-Meldung des Spiels abschalten: kein Telemetrie-Ereignis wird gesendet oder protokolliert, der Client signiert keine Chatnachricht, der Server führt keine Chat-Sitzung und verlangt keine, keine Nachricht, die jemand sendet, lässt sich also melden. Ein Pack kann das nicht setzen. Greift beim nächsten Beitritt zu einer Welt oder einem Server |
| `darkSplash` | boolean | `true` | Zeichnet den Ladebildschirm dunkel mit dem Logo des Pack-Loaders statt dem des Spiels: das Logo wird beim Aufbau des Bildschirms getauscht, und die spieleigene Option Monochromes Logo wird eingeschaltet, wenn sie noch aus ist, was beim nächsten Start wirkt. Aus lässt die Option, wie sie ist |

---

# Andere Mods

## Blast Plaster Integration

*andere mods*

`<namespace>/blastplaster/*.json`

Der Dateiname ist deine Wahl, gelesen wird nur der Ordner, und mehrere Dateien addieren sich.

Blast Plaster behandelt, was nach einer Explosion passiert: Krater Block für Block heilen, baumbewusstes Fällen, Drop-Kontrolle. Allein liest es eine globale Config. Aus einem Pack gesteuert antwortet es **pro Dimension**, und das Pack liefert die Entscheidung mit, statt Spieler an die Config zu schicken. Ohne Pack-Dateien oder ohne installiertes Blast Plaster tut hier nichts etwas, und der Ordner wird mit einer Zeile im Log übersprungen.

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
  "blockConversions": ["minecraft:stone=minecraft:cobblestone@0.75", "#minecraft:logs=minecraft:stripped_oak_log@0.5"],
  "dimensions": {
    "minecraft:the_nether": { "explosionMode": "HEAL", "minimumTicksBeforeHeal": 200 },
    "minecraft:the_end": { "enableExplosionSmoke": false }
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
| `blockConversions` | Liste von Regeln | Worin ein gesprengter Block verwandelt wird, statt unverändert zurückzukehren, sodass ein Bauwerk pro Explosion eine Stufe verfällt |

`blockConversions` bestimmt, worin ein gesprengter Block verwandelt wird, statt unverändert zurückzukehren. Eine Regel lautet `<source>=<result>[@chance]`: die Quelle ist eine Block-ID oder ein Block-Tag mit vorangestelltem `#`; das Ergebnis ist eine Block-ID oder `nothing`, damit die Stelle leer bleibt; die Chance reicht von 0.0 bis 1.0 und ist standardmäßig 1.0. Die erste passende Regel gewinnt, spezifische Regeln gehören also über die allgemeinen, und ein Block, der bereits das Ergebnis einer Regel ist, wird nie erneut umgewandelt — eine Mauer gibt pro Explosion eine Stufe nach, statt ganz zu verschwinden.

**Vollständig Vanilla-Optik:** `EJECT_DROPS` plus `healFullTrees`, `enableFakeTossedBlocks`, `enableExplosionFlash`, `enableExplosionSmoke`, `preventMobDrops` und `playerTNTAlwaysDrops` alle aus. Jeder Schlüssel ist pro Dimension setzbar.

**Vanilla-Clients** merken nichts Ungewöhnliches. Der Blitz ist das einzige Feature, das einen Block setzt; mit gesetztem `vanillaClients` wird er darum erzwungen abgeschaltet, der Rest sind Partikel und Items, die ein blanker Client versteht.

Keine Pack-Schlüssel: Blast Plasters Debug-Log und seine Holz-zu-Laub-Paarung (die Baumerkennung muss eine Antwort für das ganze Spiel sein). Beides bleibt in Blast Plasters eigener Config.

---

# Referenz

## Wertelisten

*referenz*

### Zulässige Namen

*wertelisten*

Das sind die Namen, die der Parser überall dort annimmt, wo die Tabellen oben „eines der Materialien“ und Ähnliches sagen. Alles Unbekannte wird protokolliert und durch den Standardwert ersetzt.

**Blockmaterialien.** `air`, `grass`, `ground`, `wood`, `rock`, `iron`, `anvil`, `water`, `lava`, `leaves`, `plants`, `vine`, `sponge`, `cloth`, `fire`, `sand`, `circuits`, `carpet`, `glass`, `redstone_light`, `tnt`, `coral`, `ice`, `packed_ice`, `snow`, `crafted_snow`, `cactus`, `clay`, `gourd`, `dragon_egg`, `portal`, `cake`, `web`, `piston`, `barrier`, `structure_void`. Das Spiel selbst hat keine Materialien mehr; jeder Name tut, was dieses Material in 1.12.2 tat: Er legt die Kartenfarbe fest, ob der Block ein Werkzeug braucht, um etwas zu droppen, wie Kolben mit ihm umgehen, ob Lava ihn entzündet, ob fließende Flüssigkeit ihn wegspült und ob ein gesetzter Block ihn ersetzt.

**Sound-Typen.** `wood`, `ground`, `plant`, `stone`, `metal`, `glass`, `cloth`, `sand`, `snow`, `ladder`, `anvil`, `slime`.

**Kartenfarben.** `air`, `grass`, `sand`, `cloth`, `tnt`, `ice`, `iron`, `foliage`, `snow`, `clay`, `dirt`, `stone`, `water`, `wood`, `quartz`, `adobe`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `silver`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, `black`, `gold`, `diamond`, `lapis`, `emerald`, `obsidian`, `netherrack`.

**Render-Layer.** `solid`, `cutout`, `cutout_mipped`, `translucent`. Leer gelassen sucht sich der Block einen passend zu seinem Typ.

**Seltenheiten.** `common`, `uncommon`, `rare`, `epic`.

**Fackelpartikel.** `none`, `flame`, `colored`. `colored` nutzt `particleColor`.

**Werkzeugklassen.** `pickaxe`, `axe`, `shovel`, `hoe`, `sword`.

**Rüstungsslots.** `head` oder `helmet`, `chest` oder `chestplate`, `legs` oder `leggings`, `feet` oder `boots`.

**Färbungen.** `biome`, `none` oder eine sechsstellige Hex-Farbe. Farben sind überall in einer Definition Hex-Werte, mit oder ohne führendes `#`.

**Verhalten** für `behavesAs`. `till`, `path`, `bush`, `animals`.

**Pflanzentypen** für `plantTypes`. `plains`, `desert`, `beach`, `cave`, `water`, `nether`, `crop`. Nur 1.20.1; 1.21.1 liest den Schlüssel und ignoriert ihn.

**Biomtypen**, die Wörter, die überall dort für einen Biom-Tag stehen, wo eine Tabelle „Liste von Biomtypen“ sagt, in `biomeTypes`, den `types` eines Bioms, den `roles` einer Vorlage und einem `biomes`-Abschnitt: `ocean`, `deepocean`, `beach`, `river`, `mountain`, `mesa`, `hills`, `coniferous`, `jungle`, `forest`, `savanna`, `overworld`, `nether`, `end`, `hot`, `cold`, `sparse`, `dense`, `wet`, `dry`, `spooky`, `dead`, `lush`, `mushroom`, `magical`, `rare`, `plateau`, `modified`, `water`, `desert`, `plains`, `swamp`, `sandy`, `snowy`, `wasteland`, `void`. Die Vanilla-Wörter bilden sich auf `minecraft:is_*`-Tags ab und die übrigen auf die Konventions-Tags, `forge:is_*` auf 1.20.1 und `c:is_*` auf 1.21.1. Ein ausgeschriebener Tag, `minecraft:is_forest` oder `#minecraft:is_forest`, wird genommen, wie er ist. Groß- und Kleinschreibung spielt keine Rolle, das `FOREST` aus 1.12.2 liest sich also weiterhin.

**Rollen** für die `roles` einer Weltvorlage. Jedes Biomtyp-Wort oben: jedes nennt ein Biom, das die Biome mit diesem Tag füllt, sobald die Sperre sie entfernt hat, `"ocean": "mypack:ruby_ocean"` setzt den Rubinozean also überall dorthin, wo ein Ozean gesperrt wurde.

**Strukturen** für die `structures` einer Weltvorlage und die eigenen Listen der Gruppe `structures`: die 1.12.2-Namen `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges` und `endcities`, oder jedes Structure-Set, das das Spiel oder ein Mod mitbringt, etwa `pillager_outposts`, `ancient_cities`, `trail_ruins`, `shipwrecks`, `ocean_ruins`, `ruined_portals`, `nether_fossils`, `buried_treasures`, `desert_pyramids`, `jungle_temples`, `igloos`, `swamp_huts`, `woodland_mansions`, `ocean_monuments`, `nether_complexes`, `end_cities`. Ein 1.12.2-Name wird als die Sets gelesen, für die er stand, `temples` sind also die Pyramiden, die Dschungeltempel, die Iglus und die Sumpfhütten zusammen. Die Populate-Namen aus 1.12.2 werden ebenfalls gelesen, als die Teile der Welt dieser Version, für die sie stehen: `caves` die Höhlen-Carver (die Rauschhöhlen sind `noiseCaves`), `ravines` die Schluchten, `dungeons` die Monsterräume, `lavalakes` die Lavaseen, `netherlava` die offenen Lavaquellen des Nethers, `fire` seine Feuerflecken, `glowstone` sein Glowstone, `ice` die gefrorene oberste Schicht und `animals` die Tiere, die beim Erzeugen eines Chunks gesetzt werden. `waterlakes` wird angenommen und bewirkt nichts, da es in dieser Version keine Wasserseen gibt.

**Kreaturtypen** für Biom-Spawns und -Raten. `creature`, `monster`, `ambient`, `water`. Der Spawn einer Entity-Variante nimmt auch die übrigen Listen dieser Version beim Namen, etwa `water_ambient` oder `underground_water_creature`.

**Erztypen** für `oreTypes`. `COAL`, `IRON`, `COPPER`, `GOLD`, `REDSTONE`, `DIAMOND`, `LAPIS`, `EMERALD`, `QUARTZ`, `DIRT`, `GRAVEL`, `DIORITE`, `GRANITE`, `ANDESITE`, `TUFF`, `CLAY`, `SILVERFISH`, `CUSTOM` für jedes andere Erz.

**Schadensarten** für das `immuneTo` einer Entity-Variante, in beliebiger Schreibung und mit oder ohne Unterstriche. Die Namen aus 1.12.2, jeder mit dem, was er dort abdeckte: `inFire` (auch ein Lagerfeuer), `onFire` (auch ein Feuerball, den niemand geschossen hat), `lava`, `hotFloor`, `inWall` (auch die Weltgrenze), `cramming`, `drown`, `starve`, `cactus`, `fall`, `flyIntoWall`, `outOfWorld` (auch `/kill`), `generic`, `magic`, `indirectMagic`, `wither`, `anvil`, `fallingBlock`, `dragonBreath`, `fireworks`, `lightningBolt`, `thorns`, `arrow`, `fireball`, `thrown`, `mob` für den Schlag einer Kreatur, die Spucke eines Lamas, das Geschoss eines Shulkers oder den Schädel eines Withers, `player` für den Schlag eines Spielers, `explosion` für eine Explosion, die niemand ausgelöst hat, etwa ein Bett, und `explosion.player` für eine, die eine Kreatur oder ein Spieler ausgelöst hat, einen Creeper oder angezündetes TNT. Auch der Schaden dieser Version hat Namen: `fire` für beide Arten zu brennen, `lightning`, `void`, `freeze`, `dryOut` und `sweetBerry`. Alles andere wird als ID einer Schadensart gelesen, `minecraft:sonic_boom` oder die eines Mods.

**Sound-Namen** für die `sounds` einer Entity-Variante, das `openSound` einer Schatzkiste und den `sound` eines Portals: jedes registrierte Sound-Event, ob aus dem Spiel, aus einem Mod oder von einem Pack über `sounds` hinzugefügt. Ein 1.12.2-Name wird als der Name gelesen, den der Sound heute trägt, `entity.endermen.scream` spielt also `entity.enderman.scream`, `block.cloth.step` spielt `block.wool.step`, `entity.small_slime.squish` spielt `entity.slime.squish_small` und `record.cat` spielt `music_disc.cat`. Die vier Papageien-Imitationen, die diese Version nicht mehr hat, von Enderman, Eisbär, Wolf und Zombie Pigman, spielen nichts.

## Ordnerliste

*referenz*

Jeder Ordner, mit vollem Pfad und einem Link zum Abschnitt, der ihn beschreibt, steht unter [Wo die Dateien liegen](#wo-die-dateien-liegen).

## Befehle

*referenz*

### Eigene Befehle

*befehle*

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
| `/rdpl biome`, `biome list [all]` | keine | Jedes Biom, das generieren kann, mit seiner Id; `all` nimmt die dazu, die nichts generieren kann |
| `/rdpl biome here` | keine | Das Biom, in dem du stehst: Name, Id und Nummer |
| `/rdpl locate`, `goto`, `vein`, `gate`, `pregen`, `intro`, `team`, `round`, `dimensions`, `oregen` | die des Servers | Verknüpft. Wird wortwörtlich an `/rdplserver` weitergereicht, der entscheidet, siehe die Tabelle unten |

**Welche Server-Unterbefehle verknüpft sind und warum die übrigen nicht.** `locate`, `goto`, `vein`, `gate`, `pregen`, `intro`, `team`, `round`, `dimensions` und `oregen` können immer nur die des Servers meinen, denn nur der Server kennt die Welt, ihre Spieler und ihre Runden, `/rdpl` gibt sie also weiter. Im Einzelspieler bietet die Tab-Vervollständigung dahinter an, was `/rdplserver` anbieten würde; auf einem Server bietet `goto` die Namen der Vanilla-Strukturen an. Die übrigen, `reload`, `list`, `which`, `unused`, `config`, `pixelmap` und `biome`, behalten ihre eigene Bedeutung von deinen Packs und deinem Client. Die eigene Berechtigungsprüfung des Servers entscheidet über einen verknüpften Befehl, ein Client kann sie also weder umgehen noch eine erfundene Antwort bekommen.

**Beim täglichen Arbeiten:** F3+T lädt Texturen, Modelle und Sprachdateien neu, und `/reload` die Daten des Servers. Nimm `/rdpl reload`, wenn du eine Datei *hinzufügst* oder *löschst*, weil sich damit ändert, was der Ordner enthält.

### Serverbefehle

*befehle*

Auf einem dedizierten Server macht `/rdplserver` dasselbe für die Kopie des Ordners auf dem Server. Die Spalte Stufe ist die Berechtigungsstufe, die ein Absender braucht: `3` ist ein Operator, `2` lässt auch Befehlsblöcke zu, `0` ist jeder Spieler, und `4` liegt über Operator und erreicht niemanden.

#### Packs und Dateien

*serverbefehle*

| Befehl | Stufe | Was er macht |
| --- | --- | --- |
| `/rdplserver reload` | 3 | Den Ordner des Servers neu einlesen und alles neu laden, dann die Teams und Ziele neu aufstellen |
| `/rdplserver list` | 3 | Jedes Pack, das der Server geladen hat, seine Priorität und was es enthält |
| `/rdplserver which <namespace:path>` | 3 | Welches Pack eine bestimmte Datei liefert und welche Packs es dabei verdeckt |
| `/rdplserver unused` | 3 | Dateien in den Packs des Servers, nach denen nichts gefragt hat |
| `/rdplserver config unused` | 3 | Optionsdateien in `rdploader/config`, die kein installiertes Pack mehr definiert |
| `/rdplserver config prune` | 3 | Diese Dateien löschen |
| `/rdplserver pixelmap <namespace:path>` | 3 | Was aus einer Pixelkarte geworden ist |

#### Welt und Generierung

*serverbefehle*

| Befehl | Stufe | Was er macht |
| --- | --- | --- |
| `/rdplserver oregen` | 3 | Laufende Summen der blockierten Erzgenerierung, pro Mod und Typ |
| `/rdplserver generators` | 3 | Laufende Summen der blockierten Weltgeneratoren, pro Mod und Typ |
| `/rdplserver biome list [all]` | 3 | Jedes Biom, das auf dem Server generieren kann, mit Nummer, Id und Name; `all` nimmt die dazu, die nichts generieren kann |
| `/rdplserver biome` | 3 | Das Biom, in dem du stehst, und was das Pack damit macht: seine Id, Nummer und sein Name, ob `blockBiomes` an ist und welche Weltvorlage gilt, dazu der Boden, der Block darunter und der Stein auf y 40 |
| `/rdplserver biome here [player]` | 3 | Das Biom, in dem du oder der genannte Spieler steht: Name, Id und Nummer. Die Konsole nennt einen Spieler |
| `/rdplserver dimensions` | 3 | Jede Dimension, auch die, die Packs hinzugefügt haben |
| `/rdplserver vein <Eintrag> [Radius]` | 3 | Wo ein Worldgen-Eintrag der Form `vein` seine Adern in so vielen Chunks (Standard 8) um die Stelle angelegt hat, an der er ausgeführt wird, die nächste zuerst, ob diese Chunks schon da sind oder nicht |

#### Tor-Befehle

*serverbefehle*

| Befehl | Stufe | Was er macht |
| --- | --- | --- |
| `/rdplserver gate`, `gate list` | 3 | Jedes Tor, seine Dimension, seinen Geltungsbereich und ob es offen ist |
| `/rdplserver gate check <player>` | 3 | Welche Tore ein Spieler passiert hat |
| `/rdplserver gate grant <player> <gate>` | 3 | Ein Tor für einen Spieler öffnen |
| `/rdplserver gate revoke <player> <gate>` | 3 | Es wieder schließen |

#### Vorgenerierungsbefehle

*serverbefehle*

| Befehl | Stufe | Was er macht |
| --- | --- | --- |
| `/rdplserver pregen <radius>` | 3 | Jeden Chunk in so vielen Chunks Umkreis erzeugen. Siehe [Vorgenerierung](#vorgenerierung) |
| `/rdplserver pregen status` | 3 | Wie weit ein Lauf ist |
| `/rdplserver pregen stop` | 3 | Ihn beenden |

#### Spieler, Teams und Runden

*serverbefehle*

| Befehl | Stufe | Was er macht |
| --- | --- | --- |
| `/rdplserver intro` | 0 | Das Welt-Intro beim nächsten Beitritt noch einmal abspielen lassen. Er löscht immer nur dein eigenes und wird abgelehnt, wenn kein Pack ein Intro hat |
| `/rdplserver team`, `team join [name]`, `team leave`, `team vote <Spieler>`, `team claim` | 0 | Die Seiten, die ein Pack aufgestellt hat, und die Wege hinein und hinaus, nur angeboten, solange ein Pack Seiten aufstellt, siehe [Teams](#teams) |
| `/rdplserver round start`, `round reset`, `round vote yes`, `round vote no` | 0 | Eine Runde starten, die das Pack in einer Lobby hält, eine laufende Runde zurücksetzen oder dazu abstimmen lassen, und in einer Abstimmung stimmen, so weit die Wertung des Packs es erlaubt, und starten kann sie nur ein Spieler; Spielern nur angeboten, solange ein Pack Punkte führt, siehe [Wertung](#wertung) |
| `/rdplserver reset` | 3 | Setzt die Karte zurück, wie es ein Rundenende tut: Alle werden festgehalten, die Entities weggefegt, die Punkte gelöscht, `resetRuns` ausgeführt, die Spieler nach `resetSendsTo` gesetzt und freigegeben, und eine Runde öffnet mit dem Startzähler, wie die Reset-Einstellungen unter [Vorgenerierung](#vorgenerierung) beschreiben |

#### Orte anspringen

*serverbefehle*

| Befehl | Stufe | Was er macht |
| --- | --- | --- |
| `/rdplserver locate <name>` | 3 | Die nächste Struktur, die ein Pack unter diesem `locateAs`-Namen gesetzt hat |
| `/rdplserver goto <struktur>` | `gotoLevel`, `3` | Bringt dich zur nächsten, bei der noch niemand war, und sucht, ohne das Land auf dem Weg zu erzeugen. Ein Ort, den ein Pack mit `locateAs` angemeldet hat, ist der nächste gesetzte, ob besucht oder nicht. `temple` meint jedes verstreute Bauwerk: Wüsten- und Dschungeltempel, Hexenhütten und Iglus. Abgelehnt, solange Land gebaut wird |
| `/rdplserver goto <struktur> next` | `gotoNextLevel`, `3` | Bringt dich weiter zur nächstgelegenen, zu der du in dieser Sitzung noch nicht gebracht wurdest, ob schon einmal besucht oder nicht. Eine im Umkreis von acht Chunks um dich wird übergangen; bei einem Ort eines Packs ist es der nächste, der mehr als 128 Blöcke entfernt ist |
| `/rdplserver goto <struktur> back` | `gotoBackLevel`, `3` | Bringt dich zur vorherigen zurück und geht Schritt für Schritt durch das, wohin diese Sitzung dich geschickt hat |

### Wer goto benutzen darf

*befehle*

**`goto` öffnen.** Jeder Teil von `/rdplserver` braucht einen Operator, Stufe 3, außer `intro`, `team` und `round`, die jeder Spieler ausführen darf, wie in 1.12.2. Die drei `goto`-Formen sind das eine, worüber ein Pack entscheidet: Jede trägt eine eigene Berechtigungsstufe, die ein Pack oder die Config senken darf, getrennt von den beiden anderen und vom Rest des Befehls. Ein Pack, das `reset` für Spieler erreichbar machen will, legt ihn auf einen Befehlsblock oder in eine Funktion, die auf Stufe 3 läuft.

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

Der Wert ist die Berechtigungsstufe, die der Absender braucht. `3` (Operator) ist der Standard. `2` lässt auch Befehlsblöcke zu, ein Pack kann den Sprung also auf einen Knopf oder eine Druckplatte legen, ohne den Rest von `/rdplserver` freizugeben. `0` öffnet ihn für jeden Spieler. Die drei Einstellungen sind unabhängig: etwa `next` offen für Befehlsblöcke einer Stadt-Rundfahrt, während `back` bei den Operatoren bleibt. Ein Wert unter 0 zählt als 0 und einer über 4 als 4, und einem Operator wird `goto` selbst immer angeboten, was die Einstellungen auch sagen.

`gotoPlaceLevels` überschreibt die drei Einstellungen für einzelne Orte, als `name=stufe`-Einträge wie im Beispiel oben. Der Name ist das, was du hinter `goto` tippen würdest: ein Vanilla-Name wie `village` oder `mansion`, oder ein mit `locateAs` an einem Imprint-Eintrag angemeldeter Name. Groß-/Kleinschreibung spielt keine Rolle. Stufe `4` liegt über Operator und verschließt den Ort für alle, der Weg, einen einzelnen Ort zu verstecken, während `goto` sonst offen ist.

Ein Eintrag setzt eine Stufe für alle drei Formen dieses Ortes. Ein nicht gelisteter Ort fällt auf die drei Einstellungen oben zurück, und ein nirgends angemeldeter Name passt nie. Die Tab-Vervollständigung hält sich an dieselben Regeln: Nach `goto` werden nur die Orte angeboten, zu denen der Absender auch wirklich gebracht werden kann.

Sie liegen in der Gruppe `commands`, also entscheidet `control.commands` in der Config, ob ein Pack sie überhaupt setzen darf, und `off` dort hält alles bei Operator, ganz gleich was ein Pack verlangt.

## Gut zu wissen

*referenz*

- KubeJS und CraftTweaker laufen nach RDPL, ihre Änderungen gewinnen also weiterhin.
- Rezepte, Beutetabellen, Fortschritte und Funktionen sind hier die eigenen Datendateien des Spiels, `/reload` nimmt also eine Änderung auf und `/rdpl reload` eine neue Datei.
- Eine Struktur, die schon generiert wurde, bleibt geladen, bis du die Welt verlässt.
- Groß- und Kleinschreibung im Dateinamen zählt. Passt die Schreibweise deiner Datei nicht zu dem, wonach das Spiel gefragt hat, lädt RDPL sie trotzdem, warnt dich aber, denn unter Linux würde sie überhaupt nicht gefunden.
- Leg eine `pack.png` in `rdploader`, um dem Pack ein Symbol zu geben. Ohne eine zeigt es das RDPL-Symbol.
- Der Ordner lässt sich mit der Option `rootDirectory` in `config/resourcedatapackloader-common.toml` verschieben oder umbenennen. Ein absoluter Pfad geht auch, und es braucht einen Neustart.
- Ein Modell, das ein fertiges Vanilla-Modell nennt, erbt auch Vanillas Texturen. Eltern-Modelle wie `cube_all` und `cross` nehmen ihre Texturen aus dem Modell, das sie nennt, und sind unproblematisch.
- Telemetrie und Chat-Meldung des Spiels sind aus, solange `privacy` in der Kategorie `tweaks` an ist, was der Standard ist: Nichts wird gesendet, keine Chatnachricht wird signiert, und ein Server mit dem Mod führt für niemanden eine Chat-Sitzung.
- Eine geänderte Pack-Option wird von der Welt gemerkt, die sie ändert; lässt die Änderung Inhalte der Welt unregistriert zurück, wird die Welt in den `backups`-Ordner des Spiels gesichert, bevor sie wieder geöffnet wird, und bleibt geschlossen, wenn diese Sicherung fehlschlägt.

## Wenn etwas nicht funktioniert

*referenz*

**Sieh zuerst in `logs/rdpl.log`.** Alles, was RDPL tut, landet dort statt im Hauptlog. Fortschritte, Beutetabellen, Rezepte, Funktionen, Strukturen und jeder Inhalt werden mit dem Pack protokolliert, aus dem sie kamen, und alles Fehlerhafte mit dem Grund.

**Bei Texturen und anderen Assets ist es anders.** Nach ihnen wird viel zu oft gefragt, um sie einzeln zu protokollieren, stattdessen listet `/rdpl unused` die Dateien in deinen Packs auf, nach denen nichts gefragt hat. Führ ihn aus, wenn das Spiel fertig geladen hat. Nach einer Datei mit dem richtigen Pfad wird immer gefragt, alles Aufgelistete ist also meist ein Tippfehler, bedenke aber, dass manche Dateien nur bei Bedarf laden, etwa andere Sprachen als die, in der du spielst.

**Eine Zip ohne `assets`- oder `data`-Verzeichnis darin wird übersprungen,** ebenso jeder Ordner in `rdploader`, und das Log sagt es. Eine Zip, deren oberste Ebene ein einzelner Ordner um sie herum ist, wird genauso übersprungen.

**`/rdpl which minecraft:textures/block/stone.png`** sagt dir genau, welches Pack eine Datei liefert und was es dabei verdeckt.

**Ein für 1.12.2 geschriebenes Pack wird über die Portierung gelesen.** Siehe [Packs für 1.12.2](#packs-für-1122); das Log nennt jede Datei, die es verschoben, weggelassen oder nicht übernehmen konnte.

## Bonus: Vanilla-Tweaks

*referenz*

Kleine Änderungen daran, wie Vanilla sich verhält, jede über die Config-Kategorie `tweaks` schaltbar.

| Option | Standard | Was sie macht |
| --- | --- | --- |
| `promptLeafDecay` | an | Blätter, die ihren Baum verlieren, verwelken binnen einer Sekunde, statt auf Random-Ticks zu warten |
| `lenientPaths` | an | Pfade lassen sich unter einem Block anlegen und bleiben liegen, wenn einer darübergesetzt wird |
| `unbreakableSpawners` | aus | Mobspawner lassen sich weder abbauen noch sprengen. Der Kreativmodus entfernt sie weiterhin. Braucht einen Neustart |
| `experimentalWarning` | aus | Die Warnung des Spiels vor experimentellen Einstellungen zeigen, wenn eine Welt erstellt oder geöffnet wird. Aus beantwortet sie, als hättest du auf Fortfahren geklickt |
| `privacy` | an | Schaltet Telemetrie und Chat-Meldung des Spiels ab; siehe [Gut zu wissen](#gut-zu-wissen) |
| `darkSplash` | an | Dunkler Ladebildschirm mit dem Logo des Pack-Loaders; die Option Monochromes Logo des Spiels wird für den nächsten Start eingeschaltet |

Vier weitere sitzen in der Kategorie `content` statt in `tweaks`:

| Option | Standard | Was sie macht |
| --- | --- | --- |
| `cactusMaxHeight` | `3` | Wie hoch Vanilla-Kakteen wachsen |
| `caneMaxHeight` | `3` | Wie hoch Vanilla-Zuckerrohr wächst |
| `shovelPaths` | an | Eine Schaufel macht aus Blöcken mit `behavesAs`-Pfad einen Pfad, und Schleichen macht das rückgängig |
| `hoeTilling` | an | Eine Hacke pflügt Blöcke mit `behavesAs` till |

**Nichts davon erreicht ein Pack.** Diese Optionen ändern nur Minecrafts eigene Kakteen, Zuckerrohre, Blätter und Pfade. Ein Block, den dein Pack mit `"type": "cane"` definiert, bringt seinen eigenen `growth`-Abschnitt mit und wächst auf die Höhe, die du ihm gegeben hast, egal was sonst installiert ist.

### Unzerstörbare Spawner

*bonus: vanilla-tweaks*

`unbreakableSpawners` gibt dem Mobspawner-Block die Werte von Grundgestein: eine Härte, die sich nicht abbauen lässt, und einen Explosionswiderstand, den nichts übersteht. Ein Spieler bekommt keinen abgebaut, egal wie gut die Spitzhacke ist, und weder Creeper noch TNT noch eine Pack-Entity mit `explodes` reißen einen weg. Der Kreativmodus entfernt sie weiterhin, genau wie er Grundgestein weiterhin entfernt, ein Pack-Autor sperrt sich also nie aus dem eigenen Bau aus. Es braucht einen Neustart, weil die Werte des Blocks beim Registrieren gesetzt werden.

**Es geht um den Block, nicht um den einzelnen Spawner.** Es gibt keinen Schalter pro Spawner. Die Option ändert `minecraft:spawner` selbst, sie erreicht also jeden Spawner der Welt auf einmal: die Vanilla-Strukturen, die einen setzen, jeden, den ein Mod setzt, und jeden, den deine eigenen Packs setzen. Ein Spawner in einer deiner `.nbt`-Vorlagen, gesetzt von einem `imprint`-Eintrag, ist ein gewöhnlicher Spawnerblock mit seiner eigenen Block-Entity, er ist also abgedeckt, sobald die Option an ist.

## Schlüssel, die nicht übernommen wurden

*referenz*

Was ein Pack für 1.12.2 schreiben kann, diese Version aber nicht liest, und warum. Ein Pack, das einen davon schreibt, lädt trotzdem; der Schlüssel bewirkt nichts. Eine Zeile mit *noch nicht übernommen* kam in 1.12.2 hinzu, nachdem dieser Teil portiert war, und soll noch folgen. Jede andere Zeile kann oder muss auf dieser Engine nicht übernommen werden. Die Tabelle wird aktuell gehalten, während sich die Linien weiterentwickeln.

| Schlüssel | Wo | Warum |
| --- | --- | --- |
| `meta` | Blöcke, Items, Worldgen, Block-Drops | Seit dem Flattening tragen IDs keine Metadaten. Die Portierung schickt jedes `name:meta` durch die Datenfixer des Spiels und verwirft den Schlüssel |
| `oreDict` | Blöcke, Items, Ofen, Brennstoffe | Das Ore Dictionary gibt es nicht mehr. Die Portierung macht daraus `tags` auf den Konventions-Tags, bei einem Brennstoff `tag` |
| `oreDictionary` | Einstellungen | Das Ore Dictionary gibt es nicht mehr, also auch keine Ore-Dictionary-Dateien, die sich abschalten ließen. Tags übernehmen seine Aufgabe, und die Portierung schreibt sie aus dem `oreDict` eines Packs |
| `modelMeta` | Blöcke | Modelle werden pro Variante erzeugt, es gibt also keine Metadaten, nach denen sie sich zuordnen ließen |
| `disableOverrides`, `tolerateMissingInAdvancements` | Einstellungen | Ein Datenpaket ersetzt ein Vanilla-Rezept oder -Advancement, indem es eines unter demselben Namen mitliefert |
| `#CONSTANT`-Itemnamen | Rezepte | Rezeptkonstanten lagen in der `_constants.json` einer 1.12.2-Mod, die kein Pack mitbringt und keine Mod dieser Version hat. Nenne das Item oder den Tag, für den die Konstante stand |
| `harvestTool` außer `pickaxe`, `axe`, `shovel`, `hoe` und `sword` | Eigenschaften überschreiben | Werkzeuge bauen hier über Block-Tags ab, und nur diese fünf haben einen. Eine Werkzeugklasse, die sich eine 1.12.2-Mod ausgedacht hat, hat keinen Tag, in den man schreiben könnte, also bleiben die Werkzeuge des Blocks unberührt, und das Log sagt es |
| `careers` | Dorfbewohner | Seit 1.14 gibt es keine Karrieren mehr, also wird jede Karriere eine eigene Dorfbewohner-Datei |
| `career` | Handel, Entities | Keine Karrieren; nenne den Beruf selbst. Ein Handel, der einen Vanilla-Beruf aus 1.12.2 mit seiner Karriere nennt, geht an den Beruf, zu dem diese Karriere geworden ist |
| `gameLoopFunction` | Spielregeln | Die Spielregel gibt es nicht mehr; das Funktions-Tag `#minecraft:tick` führt eine Funktion jeden Tick aus, und die Portierung schreibt eines |
| `id` | Biome, Dimensionen | Biome und Dimensionen kennt man an ihrer Ressourcen-Location, nie an einer Zahl |
| `suffix`, `keepLoaded` | Dimensionen | Der Speicherordner folgt dem Namen der Dimension. Nur die Oberwelt hat Spawn-Chunks, eine Dimension, die geladen bleiben muss, bekommt also ein `forceload` |
| `baseHeight`, `heightVariation` | Biome | Die Geländehöhe gehört den Noise-Einstellungen, nicht dem Biom |
| `placement.villageSpawn` | Biome | Dorfbewohner kommen mit der Dorfstruktur selbst |
| `rubicWorld`, `rubicWorldDimensions`, `rubicWorldDimensionsAreBlacklist`, `verticalCubeLoadDistance`, `cubeGCInterval`, `cubeGenMillisPerRound`, `cubesSentPerTick` | Rubic-Welten | Die Rubic-Welt war der Weg von 1.12.2 über eine 256 Blöcke hohe Welt hinaus. Hier legen `minHeight` und `maxHeight` einer Dimension ihre Größe fest, und das Chunk-System streamt sie |
| `rubicHeightLimit` | Rubic-Welten | Die Höhengrenze der Rubic-Welt ist mit ihr gegangen; `minHeight` und `maxHeight` einer Dimension legen ihre Größe stattdessen fest, ohne eigene Grenze zum Anheben |
| `regionCacheLimit` | Rubic-Welten | Sein Cache für die offenen Regionsdateien einer Rubic-Welt ist mit der Rubic-Welt gegangen; das Chunk-System hier verwaltet seine eigenen Dateien |
| `skyStone`, `skyShape`, `skyIslands`, `skyThickness`, `skyHeights`, `skyAnimals` | die tiefe Welt, Biome, Höhlenregionen | Himmelsland lag über dem Geländefenster einer Rubic-Welt, und es gibt keine Rubic-Welt, die es tragen könnte |
| `deepRavines`, `oreVeins` | die tiefe Welt | Das eigene Gelände der Engine reicht schon unter y 0, mit eigenen Schluchten und großen Erzadern |
| `terrainOffset` | Einstellungen | Es gibt kein festes Vanilla-Fenster zum Verschieben; `minHeight` und `maxHeight` einer Dimension legen Boden und Decke fest |
| `terrainWorldTypes`, `terrainWorldTypesAreBlacklist` | Einstellungen | Welttypen sind hier Welt-Presets, und eine Weltvorlage nennt ihr eigenes |
| `biomeSize`, `riverSize`, `dungeonChance`, `waterLakeChance`, `lavaLakeChance`, die Schlüssel für Größe, Anzahl und Höhe der Erze und die Noise-Skalierungen eines angepassten `generatorOptions` | Einstellungen, Dimensionen | Biomgröße und Landform gehören zu den Noise-Einstellungen, und wie oft ein Feature oder ein Erz gesetzt wird, zu dessen eigenem Placed Feature, also erreicht sie keine einzelne Zahl. Ein `fixedBiome` mit einer Nummer über 39 nennt ein Biom, das diese Version nicht zuordnen kann |
| `useWaterLakes`, sowie `lake` und `dungeon` in einem Flachwelt-Text | Einstellungen, Dimensionen | Wasserseen gibt es im Spiel seit 1.18 nicht mehr. Verliese kommen auf einer Flachwelt mit `decoration` und nicht für sich allein |
| `inherit` als Quelle der `biomes` einer `flat`- oder `void`-Dimension | Dimensionen | Ein Flachwelt-Generator hat nur ein einziges Biom, eine solche Dimension nimmt also das, das ihr Flachwelt-Text nennt, oder die Ebene |
| Dimension eines anderen Mods in `flatBedrockDimensions` oder `voidWorldDimensions` | Einstellungen | Grundgestein und Leere werden in das erzeugte Welt-Preset und die eigenen Dimensionsdateien eines Packs geschrieben; eine Dimension, die ein anderer Mod macht, entsteht aus dessen eigenen Dateien |
| Welttyp eines Mods, oder `debug_all_block_states`, als `worldType` | Einstellungen | Welttypen sind heute Welt-Presets, und die geformte Welt baut auf dem spieleigenen Noise von default, largebiomes, amplified oder flat auf. Eine Debug-Welt hat kein Gelände, das sich formen ließe |
| `pregenKeepLoaded`, `pregenPauseAbove`, `pregenMillisPerRound`, `pregenRelightSays`, `hurryWritesAbove` | Vorgenerierung | Sie stimmten den Chunk-Schreiber und den Relight-Durchlauf von 1.12.2 ab. Das Chunk-System hier beleuchtet Land beim Erzeugen und schreibt nach eigenem Takt |
| `readCofhWorldFiles` | Einstellungen | CoFH World und sein eigenes Dateiformat gibt es auf dieser Engine nicht. Diese Dateien in ein Pack zu übersetzen, wie es schon 1.12.2 empfahl, ist weiterhin der Weg dorthin |
| `name:meta` in `villagePathLamp*` | Dörfer | IDs tragen keine Metadaten, daher wird ein Laternenblock mit seinem Zustand in eckigen Klammern geschrieben, und Blockdaten in geschweiften Klammern werden weiter gelesen |
| `harvestTool` mit `shears` oder einer Werkzeugklasse eines Mods | Blöcke | Ein Werkzeug liest hier Block-Tags, keinen Klassennamen, also antwortet nichts darauf. Nenne den Block-Tag des Mod-Werkzeugs unter `tags` der Variante |
| Tab-Label eines anderen Mods in `creativeTab` | Blöcke, Items, Flüssigkeiten | Ein Tab wird heute über seine Id erkannt, ein bloßes Label gilt also als Tab des eigenen Packs. Nenne den Tab des Mods mit seiner Id, etwa `modid:main` |
| `/rdpl reload <group>` | Befehle | Das Spiel lädt alle Ressourcen in einem Durchgang neu, Texturen, Modelle, Sprachen, Sounds und Shader lassen sich also nicht einzeln neu laden. `/rdpl reload` oder F3+T lädt sie alle |
| `/rdplserver biome find <name>` und `/rdpl biome find <name>` | Befehle | Vanilla sucht Biome seit 1.19 mit einem eigenen Befehl. `/locate biome <name>` findet das nächste, ganz ohne einen eigenen Pack-Befehl dafür |
| `modernChestPlacement` | Vanilla-Tweaks | Das Spiel verbindet Truhen seit 1.13 selbst so: Eine Truhe schließt sich nur an eine einzelne Truhe daneben an, die in dieselbe Richtung schaut, und beim Schleichen bleibt sie einzeln |
| `loadingScreenPercent` | Einstellungen | Der Ladebildschirm des Spiels zeigt beim Laden einer Welt schon selbst, wie weit der Spawn-Bereich fertig ist |
| `disableOptimizations` | Einstellungen | Der Schlüssel schaltete die Optimierungen für Vorgenerierung und Generierung von 1.12.2 ab. Sie waren für jene Engine geschrieben und haben hier kein Gegenstück |
| `fixTinkersModelErrors` | Einstellungen | Der Schlüssel unterdrückte die Modellfehler, die die 1.12.2-Versionen von Tinkers' Construct und Construct's Armory für jedes Werkzeug, jedes Teil und jedes Rüstungsteil ins Log schrieben. Der Fix griff in genau diese Versionen ein, also gibt es hier nichts, worauf er wirken könnte |
| `achievement.`-Kriterien | Wertung, Funktionen | Erfolge sind zu Fortschritten geworden, die keinen Zählerstand führen. Ein `stat.`-Kriterium wird als die Statistik gelesen, die daraus wurde |
| `toggledownfall`, `stats` | Funktionen | Die Befehle gibt es nicht mehr. `weather` nennt das Wetter, das gesetzt werden soll, und `execute store` hält das Ergebnis eines Befehls fest |
| `gamerule gameLoopFunction` und eine Spielregel, die sich ein Pack ausgedacht hat | Funktionen | Welche Spielregeln es gibt, legt das Spiel fest. Die Portierung macht aus dem `gameLoopFunction` einer Spielregel-Datei den Tag `#minecraft:tick`, eine Befehlszeile, die es setzt, bleibt aber stehen, damit du sie entfernst |
| ein Name mit dem Datenwert `-1` oder `*`, der zu mehreren Blöcken oder Items wurde | Funktionen: `clear`, `testforblock`, `execute ... detect`, `fill ... replace`, `clone ... filtered` | Ein Test nennt heute einen Block oder ein Item. Nenne den gemeinten oder einen Tag wie `#minecraft:wool` |
| ein Blockzustand aus `name=wert`-Paaren, der nicht der ganze 1.12.2-Zustand des Blocks ist, und ein Datenwert am Block oder Item eines anderen Mods | Funktionen | Die Data Fixer kennen nur ganze 1.12.2-Zustände, und die Metadaten eines anderen Mods haben keinen flachen Namen, zu dem sie werden könnten |
| die Partikel `footstep` und `take`, `locate Temple`, `spreadplayers` mit mehr als einem Ziel | Funktionen | Die Partikel gibt es nicht mehr, ein Tempel sind heute vier Strukturen, und `spreadplayers` nimmt ein einziges Ziel-Argument |

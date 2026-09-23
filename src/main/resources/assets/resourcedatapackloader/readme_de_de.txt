Resource Data Pack Loader
=========================

Alles, was du in diesen Ordner legst, ersetzt das, was eine Mod oder Minecraft
selbst mitbringt. Es gilt für jede Welt, im Einzelspieler wie auf Servern, und
es gibt nichts einzuschalten.


MEHR ALS NUR ÜBERSCHREIBEN
--------------------------

Packs hier können auch neue Blöcke, Items, Biome und ganze Dimensionen aus
JSON-Dateien beschreiben, festlegen, was wo generiert, Dimensionen hinter einem
Schlüssel oder einem zu erlegenden Mob verschließen und das Land einer Welt im
Voraus erzeugen, sodass niemand je auf einen Chunk wartet. HOWTO.md, die der Mod
beiliegt, behandelt all das.


EINE DATEI HINZUFÜGEN
---------------------

Öffne das Jar der Mod, suche die Datei, die du ändern willst, und kopiere ihren
Pfad ab 'assets' oder 'data'.

Um die Textur von Eisenerz zu ersetzen, liegt die Datei im Minecraft-Jar hier:

    assets/minecraft/textures/block/iron_ore.png

deine Fassung also hier:

    rdploader/assets/minecraft/textures/block/iron_ore.png

Eine Beutetabelle liegt stattdessen unter data und geht denselben Weg:

    data/minecraft/loot_table/blocks/iron_ore.json
    rdploader/data/minecraft/loot_table/blocks/iron_ore.json

Das ist die ganze Regel. Der Pfad nach 'assets' oder 'data' ist immer derselbe
wie der Pfad im Jar, es muss also nie etwas umbenannt oder verschoben werden.


ORDNUNG HALTEN
--------------

Du kannst Dateien stattdessen zu einem benannten Pack bündeln, als Zip:

    rdploader/MyTextures.zip        (mit 'assets' oder 'data' auf oberster Ebene des Zips)

Beim Zippen wählst du den Inhalt aus und zippst diesen, nicht den Ordner darum.
Ein Zip, dessen oberste Ebene ein einzelner Ordner um 'assets' oder 'data' ist,
wird übersprungen, und das Log sagt es.

Ein Ordner in rdploader ist kein Pack und wird übersprungen, und das Log sagt
es. Lose Dateien bleiben unter assets oder data, und ein Pack wird gezippt,
bevor es hierher kommt.

Liegt dieselbe Datei an zwei Stellen, gewinnt ein benanntes Pack über lose
Dateien, und /rdpl which sagt dir, welches gewonnen hat.


PACK-VORRANG
------------

Enthalten zwei benannte Packs dieselbe Datei, entscheidest du mit RDPL und einer
Zahl vor dem Zip-Namen, welches gewinnt. RDPL0 lädt zuerst, höhere
Zahlen laden später, und das zuletzt geladene Pack gewinnt:

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures.zip

Groß- und Kleinschreibung funktionieren beide, und ein Leerzeichen, Bindestrich
oder Unterstrich nach der Zahl ist optional. Das Präfix wird im Log und in
/rdpl list vom Namen des Packs abgeschnitten, RDPL1 SeasonalTextures erscheint
dort also als SeasonalTextures.


MOD-API
-------

Eine Mod kann RDPL-Inhalte in ihrer eigenen Jar mitbringen, in einem Ordner
namens rdploader, aufgebaut genau wie ein Pack:

    diemod.jar
      META-INF/neoforge.mods.toml
      rdploader/assets/diemod/textures/block/rubinerz.png

Das sind Vorgaben, keine Überschreibungen. Ein Mod-Pack lädt unter jedem Pack
in diesem Ordner, alles hier gewinnt also dagegen, und eine Mod darf nur
Dateien unter einem Namensraum liefern, den sie in ihrer eigenen neoforge.mods.toml
nennt. Alles andere wird mit einer Warnung übergangen, damit keine Mod
stillschweigend die Inhalte einer anderen oder deine umschreibt.

Jede Mod, die so etwas mitbringt, steht beim ersten Erkennen in
config/mods.json:

    {
      "diemod": {
        "enabled": true,
        "priority": -1
      }
    }

Setz enabled auf false, um die Inhalte dieser Mod abzuschalten. Lass priority
auf -1, damit sie unter allem bleibt, oder gib eine Zahl an, dann reiht sie
sich oben bei den nummerierten Packs ein. Im Log stehen die Packs mit dem
niedrigsten zuerst, und die einer Mod sind dort gekennzeichnet, es lädt also
nichts, was du nicht sehen kannst.


RESSOURCENPAKETE
----------------

Standardmäßig stehen die Dateien hier über den Ressourcenpaketen, die der
Spieler im Optionsbildschirm wählt, ein Ressourcenpaket kann sie also nicht
überschreiben. Für ein Modpack-Logo ist das richtig, für Texturen, die man
umgestalten können soll, falsch.

Setze O oder N hinter das RDPL-Präfix, um es je Pack zu entscheiden:

    rdploader/RDPLO Branding          gewinnt immer, Ressourcenpakete außen vor
    rdploader/RDPLN BaseTextures      ein Ressourcenpaket darf es überschreiben
    rdploader/RDPL1O Seasonal         Vorrang und gewinnt immer, beides zusammen

Packs ohne Buchstaben folgen der Option overrideResourcePacks in der Config, und
/rdpl list markiert die, die überschreiben.

Dieselbe Regel gilt für Datenpakete. Ein mit N markiertes Pack steht unter den
Datenpaketen, die eine Welt in ihrem eigenen datapacks-Ordner trägt, ein mit O
markiertes darüber.

Packs ohne Präfix laden vor allen nummerierten Packs, in alphabetischer
Reihenfolge, ein nummeriertes Pack gewinnt also immer über ein unnummeriertes.

Um ein Pack abzuschalten, ohne es zu löschen, hänge .disabled an seinen Namen:

    rdploader/RDPL1 SeasonalTextures.zip.disabled

Das Pack wird übersprungen, und das Log sagt es. Entferne den Zusatz, um es
wieder einzuschalten.


WAS DU ÄNDERN KANNST
--------------------

Texturen, Modelle, Blockstates, Sprachdateien, Sounds, Schriften, Splash-Texte
und alles andere, was eine Mod in ihrem assets-Ordner hält, etwa Handbücher oder
Anleitungsbücher.

Fortschritte, Beutetabellen, Rezepte, Tags, Funktionen, Strukturvorlagen und
alles andere, was eine Mod in ihrem data-Ordner hält. Das läuft serverseitig,
funktioniert also auch auf einem dedizierten Server, und eine Änderung daran
greift mit /reload.


NEUEN INHALT HINZUFÜGEN
-----------------------

Ein Pack kann auch eigene Blöcke, Items und Flüssigkeiten hinzufügen, als JSON
beschrieben. Dafür musst du keine Mod schreiben oder bauen.

Definitionen liegen unter data, ein Ordner für jede Art von Ding. Jeder
Schlüssel in "variants" ist ein Name, also registriert eine Datei unter

    rdploader/data/mypack/blocks/ores.json

mit einer Variante namens ruby_ore den Block mypack:ruby_ore. Der Name der
Datei selbst gruppiert nur. Registriert eine echte Mod diesen Namen bereits,
gewinnt die Mod und deine Variante wird übersprungen.

Der einfachste Block sind ein paar Zeilen:

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "ruby_ore": { "hardness": 3.0, "harvestLevel": 1 }
      }
    }

Modell, Blockstate, Textur und Spracheintrag lieferst du weiterhin unter
assets, genau wie jede andere Datei in diesem Ordner.

Jeder davon ist ein Ordner unter data/<deinpack>:

    blocks           items            fluids           materials
    tabs             sounds           biomes           worldgen
    caveregions      dimensions       worldtemplates   worldintro
    gates            gamerules        teams            scoring
    raids            entities         hardness         anvils
    exposures        overrides        villages         pathintersects
    structuremaps    citymaps         portalframes     blastplaster
    structure        recipe           recipe_removals  furnace
    fuels            brewing          potions          potion_types
    villagers        trades           loot_table       loot_injections
    block_drops      player_loot      advancement      function
    tags             registry_remap

Blöcke gibt es in diesen Formen, festgelegt durch das Feld "type":

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

und Items in diesen:

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   container

Eine Trankart heißt nach dem Sprachschlüssel
item.minecraft.potion.effect.<baseName>, mit splash_potion, lingering_potion
oder tipped_arrow statt potion für die anderen Formen. Ein Item vom Typ
potion_bottle ist dein eigenes Gefäß dafür: Es nimmt wie jedes andere Item
einen creativeTab und enthält die Trankarten, die du in potionTypes nennst.

Eine Datei villagers/<name>.json definiert einen Beruf. Eine Datei
trades/*.json fügt jedem Beruf Handel hinzu, ob deinem oder einem von
Minecraft, und nennt den Beruf und die Stufe, auf der der Handel erscheint.

Eine Datei entities/<name>.json macht aus einem vorhandenen Wesen ein neues. Sie
nennt das Wesen, auf dem es aufbaut, und was an ihm anders ist: seinen Namen,
sein Aussehen, wie viel Leben und Schaden es hat, wie es sich bewegt, wie es
kämpft und was es fallen lässt. Es ist ein eigenes Wesen mit eigenem Spawn-Ei
und eigener Beutetabelle, und das Wesen, auf dem es aufbaut, bleibt unberührt.

Eine Datei villages/<name>.json fügt ein Grundstück hinzu, das eine Stadt oder
ein Dorf aus einer deiner .nbt-Vorlagen bauen kann, und eine Datei
raids/<name>.json schickt Wellen gegen ein Dorf, wenn ein Spieler ein Omen
hineinträgt.

Eine Datei worldintro/<name>.json spielt eine Folge von Seiten ab, wenn jemand
die Welt betritt, bevor er die Kontrolle übernimmt. Die Texte sind einfache
.txt-Dateien unter assets/<deinpack>/texts. Sie kann einmal pro Spieler oder
bei jedem Beitritt laufen.

Eine Datei teams/<name>.json stellt eine Seite auf der Anzeigetafel des Spiels
auf und legt fest, wer ihr beitritt, und eine Datei scoring/<name>.json ist ein
Ziel, das diesen Seiten Punkte gibt und entscheidet, wie eine Partie endet.


GANZE WELTEN
------------

Ein Pack ist nicht auf einzelne Dinge beschränkt. dimensions/<name>.json
registriert eine Dimension mit eigenem Gelände, eigenen Biomen und eigenem
Himmel. gates/<name>.json knüpft eine Bedingung daran, sie zu erreichen, etwa
einen Gegenstand zu halten oder auszugeben. Ein Block vom Typ portal schickt
jeden, der hineingeht, in eine andere Dimension, und mit portalframes kann ein
Spieler einen eigenen Rahmen bauen und entzünden.

worldtemplates/<name>.json fasst die Einstellungen einer Welt in einer Datei
zusammen, sodass ein Pack eine ganze Weltform auf einmal liefern kann, statt um
ein Dutzend Änderungen an der Konfiguration zu bitten. Es kann auch die
Oberwelt selbst formen, etwa ihren Meeresspiegel und ob ihre Ozeane aus Lava
sind.

worldgen ist mehr als Erz. Ein Eintrag setzt eine Form, von einem kleinen
Klumpen deines Blocks bis zu einer deiner eigenen .nbt-Vorlagen, und legt fest,
wie oft, wie hoch und in welchen Biomen sie erscheint.

Eine Datei biomes/<name>.json definiert ein Biom: sein Klima und seine Farben,
die Blöcke, aus denen es besteht, was es schmückt, was darin spawnt und wo es
generiert.


WO ES AUFHÖRT
-------------

Das beschreibt, was ein Ding ist, nicht was es über die Zeit tut. Alles, was
eine Blockentität, einen Bildschirm oder Code in jedem Tick braucht, braucht
weiterhin eine echte Mod, mit einer Ausnahme: Ein Block vom Typ container hält
ein Inventar mit eigenem Bildschirm. Eine Maschine ist außer Reichweite; ein
Erz, ein Zaun, ein Essen oder eine Flüssigkeit nicht.


ÄNDERUNGEN SEHEN
----------------

Drücke F3+T, um Texturen, Modelle, Sprachdateien und alles andere unter assets
neu zu laden. Auf einem Server, oder für alles unter data, tippe /reload.

Wenn du eine neue Datei hinzufügst oder eine löschst, nimm stattdessen
/rdpl reload. Eine Datei zu bearbeiten, die schon da war, braucht nur F3+T oder
/reload.

/rdpl list zeigt jedes geladene Pack und was darin ist. Fahre mit der Maus über
ein Pack, um es zu sehen.

/rdpl which minecraft:textures/block/stone.png zeigt, welches Pack eine Datei
liefert und welche Packs darunter verdeckt sind.

Das funktioniert ohne Operator zu sein, denn es liest nur Dateien auf deinem
eigenen Rechner. Auf einem dedizierten Server liest /rdplserver reload die Kopie
des Servers neu ein, und /rdplserver list, which und unused antworten für sie.


WENN ETWAS NICHT FUNKTIONIERT
-----------------------------

Sieh zuerst ins Log. logs/rdpl.log nennt jedes geladene Pack und jedes
übersprungene samt Grund, und alles, was falsch ist, wird als Warnung mit
Begründung geloggt.

/rdpl unused listet jede Datei in deinen Packs auf, nach der noch nichts
gefragt hat, was meist einen Tippfehler im Pfad bedeutet. Führ es aus, nachdem
das Spiel fertig geladen hat, und bedenke, dass manche Dateien erst laden, wenn
sie gebraucht werden, etwa andere Sprachen als die, in der du spielst.

Großbuchstaben zählen. Heißt deine Datei Stone.png und das Spiel hat nach
stone.png gefragt, lädt sie trotzdem, aber eine Warnung sagt dir, dass du sie
umbenennen sollst. Tu das auch, denn überall außer in dieser Mod wird die Datei
gar nicht gefunden. Sprachdateien sind die häufigste Stolperfalle: Sie heißen
en_us.json, nicht en_US.json.

Prüfe, ob deine Dateien in einem 'assets'- oder 'data'-Ordner liegen. Ein Zip
ohne einen von beiden wird übersprungen, und das Log sagt es.


FORTSCHRITTE UND REZEPTE
------------------------

Ein Rezept, das ein Skript hinzufügt oder ersetzt, trägt den Namen, den das
Skript ihm gibt. Soll ein Fortschritt es freischalten, lege hier eine
Fortschrittsdatei ab, die dieses Rezept nennt, und der Fortschritt funktioniert
wieder von Anfang bis Ende.

Gib einem solchen Rezept im Skript einen festen Namen. Ein Name, der für dich
erfunden wird, kann sich ändern, sobald du das Rezept bearbeitest, und taugt
deshalb nicht als Ziel für einen Fortschritt.


Der Ordner rdploader selbst lässt sich mit der Option rootDirectory in
config/resourcedatapackloader-common.toml verschieben oder umbenennen. Ein
absoluter Pfad funktioniert auch, und ein Neustart ist nötig.

Leg eine pack.png neben diese Datei, um dem Pack ein Symbol zu geben.

Diese Datei wird von der Mod geschrieben und aktualisiert, sobald sie sich
ändert, alles, was du hineinschreibst, wird beim nächsten Start des Spiels also
ersetzt.

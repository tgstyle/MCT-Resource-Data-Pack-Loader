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
Pfad ab 'assets'.

Um die Textur von Eisenerz zu ersetzen, liegt die Datei im Minecraft-Jar hier:

    assets/minecraft/textures/blocks/iron_ore.png

deine Fassung also hier:

    rdploader/assets/minecraft/textures/blocks/iron_ore.png

Das ist die ganze Regel. Der Pfad nach 'assets' ist immer derselbe wie der Pfad
im Jar, es muss also nie etwas umbenannt oder verschoben werden.


ORDNUNG HALTEN
--------------

Du kannst Dateien stattdessen zu einem benannten Pack bündeln, als Ordner oder
als Zip:

    rdploader/MyTextures/assets/minecraft/textures/blocks/iron_ore.png
    rdploader/MyTextures.zip        (mit 'assets' auf oberster Ebene des Zips)

Beim Zippen wählst du den Inhalt aus und zippst diesen, nicht den Ordner darum.
Ein Zip, dessen oberste Ebene ein einzelner Ordner um 'assets' ist, wird
übersprungen, und das Log sagt es.

Ordner lassen sich beim Arbeiten leichter bearbeiten, Zips leichter
weitergeben. Sie verhalten sich gleich.

Liegt dieselbe Datei an zwei Stellen, gewinnt ein benanntes Pack über lose
Dateien. Das Log nennt zu jeder Datei das Pack, aus dem sie stammt, du siehst
also immer, welches gewonnen hat.


PACK-VORRANG
------------

Enthalten zwei benannte Packs dieselbe Datei, entscheidest du mit RDPL und einer
Zahl vor dem Ordner- oder Zip-Namen, welches gewinnt. RDPL0 lädt zuerst, höhere
Zahlen laden später, und das zuletzt geladene Pack gewinnt:

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures

Groß- und Kleinschreibung funktionieren beide, und ein Leerzeichen, Bindestrich
oder Unterstrich nach der Zahl ist optional. Das Präfix wird im Log und in
/rdpl list vom Namen des Packs abgeschnitten, RDPL1 SeasonalTextures erscheint
dort also als SeasonalTextures.


MOD-API
-------

Eine Mod kann RDPL-Inhalte in ihrer eigenen Jar mitbringen, in einem Ordner
namens rdploader, aufgebaut genau wie ein Pack:

    diemod.jar
      mcmod.info
      rdploader/assets/diemod/blocks/rubinerz.json

Das sind Vorgaben, keine Überschreibungen. Ein Mod-Pack lädt unter jedem Pack
in diesem Ordner, alles hier gewinnt also dagegen, und eine Mod darf nur
Dateien unter einem Namensraum liefern, den sie in ihrer eigenen mcmod.info
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

Fortschritte und Beutetabellen. Diese laufen serverseitig, funktionieren also
auch auf einem dedizierten Server.

Strukturvorlagen, die .nbt-Dateien unter assets/<modid>/structures. Eine
Struktur, die im structures-Ordner der Welt selbst liegt, gewinnt weiterhin über
eine Datei hier, und eine bereits platzierte Struktur bleibt geladen, bis du die
Welt verlässt.

Rezepte, auch das Ersetzen eines Mod-Rezepts oder das Hinzufügen eines eigenen.
Rezepte laden nur beim Start des Spiels, eine Änderung hier braucht also einen
Neustart statt eines Reloads.

Funktionen, die .mcfunction-Dateien unter assets/<modid>/functions. Minecraft
liest diese nur aus dem data-Ordner der Welt selbst, hier abgelegt wirken sie
also in jeder Welt. Eine in der Welt gespeicherte Funktion gewinnt weiterhin
über eine Datei hier.

Registry-Umbenennungen, damit eine Welt, die gespeichert wurde, bevor eine Mod
einen ihrer Blöcke umbenannt hat, diesen Block behält statt ihn zu verlieren.
Leg dazu eine Datei in assets/<modid>/registry_remap:

    {
      "registry": "minecraft:items",
      "mapping": { "oldmod:old_name": "newmod:new_name" }
    }

Die Registry ist die, zu der der Eintrag gehört, meist minecraft:items oder
minecraft:blocks. Umbenennungen verketten sich, A auf B und später B auf C
schickt A also auf C.

Eigenschaften von Dingen, die es schon gibt, ohne deren Dateien anzufassen. Eine
Datei in assets/<yourpack>/overrides benennt ihr Ziel über den Pfad,
overrides/minecraft/stone.json ändert also minecraft:stone, vanilla wie modded.
Blöcke nehmen Härte, Explosionswiderstand, Licht, Lichtundurchlässigkeit,
Rutschigkeit, Sound, Werkzeug und Abbaustufe sowie Entflammbarkeit. Items nehmen
Stapelgröße, Haltbarkeit und ein Behälter-Item, und jedes Item lässt sich essbar
machen, mit Nährwerten und Effekten. Die Effekte eines Tranks lassen sich neu
schreiben. Das wirkt live: Pack abschalten, /rdpl reload ausführen, und jeder
Wert springt zurück auf das, was er war, ohne Neustart. Schreib die Id der
besitzenden Mod in "requires", damit die Datei stillschweigend übersprungen
wird, wenn diese Mod nicht installiert ist.

Spielerbeute, wofür das Spiel überhaupt keinen Namen hat. Spieler lassen ihr
Inventar fallen und sonst nichts, eine Datei in assets/<modid>/player_loot gibt
ihnen also eine eigene Beutetabelle:

    {
      "table": "mypack:entities/player",
      "mode": "add",
      "rollOnKeepInventory": false
    }

"add" lässt zusätzlich zu allem, was sie getragen haben, fallen, was die Tabelle
würfelt, "replace" lässt es statt ihres Inventars fallen, und rollOnKeepInventory
entscheidet, ob die Tabelle in einer Welt, die Inventare behält, überhaupt
gewürfelt wird.

Ein Banner fällt aus der Reihe. Eine Definition registriert zwei Blöcke, deinen
und einen zweiten namens <name>_wall, und nur der stehende bekommt ein Item, das
beim Platzieren zwischen beiden wählt. Sein Modell reicht weit über den eigenen
Block hinaus, stehend bis 29,33 von 16 und an der Wand 13 unter den Block, und
der stehende Blockstate braucht Forges Format, um in Sechzehnteln zu drehen. Die
Anleitungen haben die vollständigen Maße.

Die Drops eines Blocks dürfen zufällig sein und müssen keine Items sein.
Einträge in seiner drops-Liste werden jeweils für sich entschieden, es sei denn,
du gibst ihnen ein Gewicht, dann teilen sie sich einen Topf und genau einer
kommt heraus. Ein Eintrag, der statt eines Blocks ein Entity nennt, lässt dieses
Entity dort los, wo der Block stand.

Eine Textur lässt sich als JSON schreiben statt zeichnen. Benenne die Datei nach
dem PNG mit .json am Ende, textures/blocks/panel.png.json, und gib ihr eine
Größe wie 16x16 oder 16x32, eine Palette von je einem Zeichen auf eine Farbe und
Zeilen dieser Zeichen von oben nach unten. Eine weitere solche Datei kann sie
erweitern und nur die Farben nennen, die anders sein sollen, eine Form lässt
sich also beliebig oft umfärben, ohne eine einzige Bilddatei. Was sie zeichnen,
liegt hier in pixelmap-cache und wird neu gezeichnet, sobald sich eine Map oder
ihre Vorlage ändert.

CraftTweaker und GroovyScript funktionieren genau wie bisher. Sie laufen nach
dieser Mod, alles, was deine Skripte entfernen oder ändern, gewinnt also über
eine Datei hier.

RDPL ist gut darin, ein oder zwei Rezepte zu ersetzen, und Rezepte für eigenen
Inhalt gehören in das Pack daneben. Für volle Rezeptkontrolle über ein Modpack
hinweg sind CraftTweaker und GroovyScript die besseren Werkzeuge. Eine Datei
hier ersetzt das Original vollständig, um also eine Zutat zu ändern oder einen
Beuteeintrag zu streichen, nimm jene.


NEUEN INHALT HINZUFÜGEN
-----------------------

Ein Pack kann auch eigene Blöcke, Items und Flüssigkeiten hinzufügen, als JSON
beschrieben. Dafür musst du keine Mod schreiben oder bauen.

Der Pfad der Datei ist ihr Name. Ein Block unter

    rdploader/MyPack/assets/mypack/blocks/copper_ore.json

registriert sich als mypack:copper_ore. Es gibt kein Namensfeld, das man
ausfüllen oder falsch schreiben könnte. Registriert eine echte Mod diesen Namen
bereits, gewinnt die Mod und deine Datei wird übersprungen.

Der einfachste Block sind ein paar Zeilen:

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "copper_ore": { "meta": 0, "hardness": 3.0, "harvestLevel": 1 }
      }
    }

Modell, Blockstate, Textur und Spracheintrag lieferst du weiterhin genauso wie
bei jeder anderen Datei in diesem Ordner.

Jedes davon ist ein Ordner unter assets/<yourpack>:

    blocks           items            fluids           materials
    worldgen         furnace          fuels            oredict
    sounds           tabs             recipes          recipe_removals
    loot_tables      loot_injections  advancements     functions
    structures       registry_remap   potions          potion_types
    brewing          villagers        trades           biomes
    villages         entities         gates            dimensions
    gamerules        worldtemplates   worldintro       pathintersects
    hardness         blastplaster     player_loot      overrides

Blöcke gibt es in diesen Formen, gesetzt über das Feld "type":

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner

und Items in diesen:

    basic   food    drink     potion  tool     armor    seed
    potion_bottle

Eine Trankart erscheint immer auf dem Vanilla-Trank, dem Wurftrank, dem
Verweiltrank und dem Spitzpfeil, die in den Tabs Brauen und Kampf liegen. Der
Tab ist eine Eigenschaft des Items, nicht der Trankart, es gibt also keinen Weg,
sie in einen eigenen Tab zu holen. Ein potion_bottle-Item ist stattdessen dein
eigener Behälter: Es nimmt wie jedes Item einen creativeTab, listet unter
potionTypes die Trankarten, die du nennst, und der Braustand nimmt es überall
an, wo eine Glasflasche geht.

Eine Datei villagers/<name>.json beschreibt einen Beruf und die Laufbahnen, die
er anbietet. Eine Datei trades/*.json fügt jeder Laufbahn Handel hinzu, deiner
eigenen wie einer von Minecraft, und nennt dazu Beruf, Laufbahn und die Stufe,
auf der der Handel erscheint. Nennst du eine Laufbahn, die es nicht gibt, listet
das Log die, die es gibt.

Eine Datei entities/<name>.json macht ein neues Entity aus einem, das es schon
gibt. Sie nennt das Entity, auf dem sie aufbaut, und was daran anders ist: sein
Name, seine Haut, wie viel Leben und Schaden es hat, wie schnell es sich bewegt
und wie hoch es springt, wie groß es gezeichnet wird, was es trägt, was es jagt
und was es in Ruhe lässt, und ob es weiterhin den Spawn-Regeln des Entitys
folgt, aus dem es gebaut wurde. Es ist ein Entity für sich, mit eigenem Spawn-Ei
und eigener Beutetabelle, und das, aus dem es gebaut wurde, bleibt unberührt.
Ein Dorfgrundstück lässt sich anweisen, statt eines Dorfbewohners eines davon
zu beherbergen.

Eine Datei villages/<name>.json fügt ein Grundstück hinzu, das Dörfer bauen
können, entweder eine Farm, die du beschreibst, oder eine deiner .nbt-Vorlagen.
Dieselben Einstellungen wählen, welche Vanilla-Teile noch erscheinen, wie weit
Dörfer voneinander gesät werden und in welchen Biomen sie erlaubt sind.

Eine Datei worldintro/<name>.json spielt eine Folge von Seiten ab, wenn jemand
die Welt betritt, bevor er die Kontrolle bekommt: laufender Text über einem
Bild, eine Titelkarte, eine Diashow, auf Wunsch mit Musik dahinter. Die Worte
sind einfache .txt-Dateien unter assets/<yourpack>/texts. Es kann einmal je
Spieler oder bei jedem Beitritt laufen.

GANZE WELTEN
------------

Ein Pack ist nicht auf einzelne Dinge beschränkt. dimensions/<name>.json
registriert eine Dimension mit eigenem Terrain, eigenen Biomen und eigenem
Himmel. gates/<name>.json stellt eine Bedingung an das Erreichen einer solchen,
etwa das Halten oder Abgeben eines Items. Ein Block vom Typ portal schickt, wer
hineingeht, und merkt sich, wer ihn gebaut hat.

Eine Weltvorlage kann auch die Oberwelt selbst formen und dabei Meereshöhe,
Lavaozeane und das Terrain-Rauschen setzen. Das wird beim Erzeugen einer Welt
angewandt und nie danach, eine schon vorhandene Welt bleibt also, wie sie war.

worldtemplates/<name>.json sammelt die Einstellungen einer Welt in einer Datei,
ein Pack kann eine ganze Weltgestalt also auf einmal ausliefern, statt ein
Dutzend Config-Änderungen zu verlangen. Jede Gruppe, die sie setzen kann,
antwortet auch der Kategorie control in der Config, die entscheidet, ob das Pack
entscheidet, die Config entscheidet oder die Gruppe ganz aus ist und kein Pack
sie einschalten kann.

worldgen ist mehr als Erz. Ein Eintrag ist eine Form, gesetzt von einer
Verteilung: Klumpen, lange Adern, Platten, Geoden, Schalen, Nadeln, Knollen,
Schlote, Oberflächenschmuck, ganze Bäume, Ranken, Bänder über mehrere Chunks
hinweg oder eine deiner eigenen .nbt-Vorlagen, gleichmäßig verteilt, um eine
Höhe herum, fraktal, dem Terrain folgend, auf Höhlenböden oder Höhlendecken oder
unter Wasser.


Eine Datei biomes/<name>.json beschreibt ein Biom: sein Klima und seine Farben,
die Blöcke, aus denen es besteht, was es schmückt, was darin spawnt und wo es
generiert. Seine Nummer wird für dich gewählt und beim ersten Laden in jede Welt
geschrieben, sie bleibt danach also fest, egal was du sonst noch installierst.
Setze "id" nur, wenn ein Biom eine Nummer behalten muss, die etwas anderes
bereits benutzt hat, etwa wenn ein Pack eine Mod ersetzt, die abgelöst wird. Ein
Biom umzubenennen oder zu löschen, das eine Welt bereits enthält, verliert es,
genau wie das Umnummerieren eines Blocks, nimm für eine Umbenennung also
registry_remap.

Der angezeigte Name eines Dorfbewohners ist der Lang-Schlüssel
entity.Villager.<career>, mit dem Laufbahnnamen genau so, wie du ihn geschrieben
hast, und nichts anderem. Dieser Schlüsselraum wird mit Minecraft und jedem
anderen Pack geteilt, schreib deinen Namensraum also in den Laufbahnnamen, wie
in rdpltest.prospector. Betroffen ist nur der Name: Ein Dorfbewohner speichert
seine Laufbahn als Zahl, eine Umbenennung ändert also, wie vorhandene
Dorfbewohner heißen, und das Umsortieren der Laufbahnliste ändert, welche
Laufbahn sie haben.

Eine Trankart ist auf dieselbe Weise nach ihrer Datei benannt, und ihr
angezeigter Name kommt aus dem Lang-Schlüssel potion.effect.<namespace>.<name>,
mit splash_potion.effect., lingering_potion.effect. und tipped_arrow.effect. für
die drei anderen Formen.


RUBIC-WELTEN
------------

Eine Weltvorlage kann eine Welt anfordern, die aus Würfeln statt aus 256 Blöcke
hohen Säulen gebaut ist, und die Welt wächst dann in beide Richtungen: ein Boden
weit unter null, eine Decke weit über 255, mit Terrain, Höhlen und Erz durch das
Ganze hindurch. In einer solchen Welt zu spielen ist gewöhnlich. Du gräbst,
baust, leuchtest und reist wie sonst auch, und das Vanilla-Generierungsfenster
behält seine übliche Gestalt in der höheren Welt, Mods, die Terrain generieren,
landen also dort, wo sie immer landeten.

Was ein Pack davon hat: eine eigene Welthöhe, einmal beim Erzeugen der Welt
gesetzt; eine Tiefenwelt unter dem Vanilla-Fenster, mit Rausch-Höhlen, Aquiferen
und gebänderten Erzadern in einem Stein, den du benennst; Höhlenregionen, die
Pack-Antwort auf Höhlenbiome, dreidimensional durch den Untergrund gemalt, mit
eigenen Böden, Decken, eigenem Wasserspiegel, eigenen Mobs und Strukturen;
Dimensionen, die aufeinander gestapelt sind, sodass ein Sturz aus dem Boden der
einen Welt dich in die nächste darunter trägt und der Weg oben hinaus wieder
zurück; und jede ausgelassene Dimension, die ihre gewöhnliche Welt im selben
Spielstand behält.

Darunter liegt eine Welt als 16 mal 16 mal 16 große Würfel in eigenen
Region-Dateien neben den Vanilla-Dateien, für sich generiert, geladen und
gespeichert, mit einer Lichtengine, die für diese Form geschrieben wurde.
Vanillas Annahme, eine Welt sei 256 Blöcke hoch, ist überall dort gepatcht, wo
sie tragend ist, von Baugrenzen und Todesebenen bis zu Pfadfindung, Portalen,
Leuchtfeuern, Karten und dem Renderer. Die Generatoren anderer Mods sehen
weiterhin ein normal aussehendes 256-Block-Fenster, weshalb ihr Terrain
funktioniert.

HOWTO.md hat die Einstellungen, die Höhen, die eine Welt annehmen darf, und die
Mods, neben denen das nicht läuft.


EINE WARNUNG ZU META
--------------------

Jede Variante hat eine Meta-Nummer, und diese Nummer ist das, was die Weltdatei
speichert. Eine Variante umzunummerieren, die Leute schon in einer Welt haben,
macht aus ihren Blöcken etwas anderes. Hänge neue Varianten hinten an und
nummeriere nie eine alte um.

Ein Block hält 16 Varianten, denn so viel erlauben vier Bit Metadaten. Stufen
bekommen 8, da ein Bit oben oder unten sagt, und Treppen, Leitern, Fackeln und
Feldfrüchte bekommen 1, weil Ausrichtung oder Alter den Rest belegt. Items sind
nicht so eng und dürfen Nummern auslassen.


WO ES AUFHÖRT
-------------

Das beschreibt, was ein Ding ist, nicht was es über die Zeit tut. Alles, was ein
Tile-Entity, eine GUI, ein Inventar oder Code in jedem Tick braucht, braucht
weiterhin eine echte Mod. Eine Maschine ist außer Reichweite; ein Erz, ein Zaun,
ein Essen oder eine Flüssigkeit nicht.


ÄNDERUNGEN SEHEN
----------------

Drücke F3+T, um Texturen, Modelle, Sprachdateien, Fortschritte und Beutetabellen
neu zu laden. Auf einem Server tippe /reload für dasselbe. Rezepte sind die
Ausnahme, wie oben: Sie laden nur beim Start, eine Rezeptänderung braucht also
einen Neustart.

Wenn du eine neue Datei hinzufügst oder eine löschst, nimm stattdessen
/rdpl reload. Eine Datei zu bearbeiten, die schon da war, braucht nur F3+T.

/rdpl reload textures lädt nur Texturen neu, was in einem großen Pack viel
schneller ist als F3+T. models, languages, sounds und shaders funktionieren
genauso. Lass den Namen weg, um den Ordner neu einzulesen und alles neu zu
laden.

/rdpl list zeigt jedes geladene Pack und was darin ist. Klick ein Pack an, um es
zu sehen.

/rdpl which minecraft:textures/blocks/stone.png zeigt, welches Pack eine Datei
liefert und welche Packs darunter verdeckt sind.

Das funktioniert ohne Operator zu sein, denn es liest nur Dateien auf deinem
eigenen Rechner. Auf einem dedizierten Server liest /rdplserver reload die Kopie
des Servers neu ein.


WENN ETWAS NICHT FUNKTIONIERT
-----------------------------

Sieh zuerst ins Log. Fortschritte, Beutetabellen, Rezepte, Funktionen und
Strukturen werden mit dem Pack geloggt, aus dem sie stammen, und alles, was
falsch ist, wird als Warnung mit Begründung geloggt.

Für Texturen und andere Assets listet /rdpl unused jede Datei in deinen Packs
auf, nach der noch nichts gefragt hat, was meist einen Tippfehler im Pfad
bedeutet. Führ es aus, nachdem das Spiel fertig geladen hat, und bedenke, dass
manche Dateien erst laden, wenn sie gebraucht werden, etwa andere Sprachen als
die, in der du spielst.

Großbuchstaben zählen. Heißt deine Datei Stone.png und das Spiel hat nach
stone.png gefragt, lädt sie trotzdem, aber eine Warnung sagt dir, dass du sie
umbenennen sollst. Tu das auch, denn überall außer in dieser Mod wird die Datei
gar nicht gefunden. Sprachdateien sind die häufigste Stolperfalle: Sie heißen
en_us.lang, nicht en_US.lang.

Prüfe, ob deine Dateien in einem 'assets'-Ordner liegen. Ein Pack-Ordner oder
Zip ohne einen solchen wird übersprungen, und das Log sagt es.



FORTSCHRITTE UND REZEPTE
------------------------

Wenn deine Skripte ein Rezept entfernen, funktioniert jeder Fortschritt, der es
freigeschaltet hat, weiterhin statt zu brechen. Er hat nur kein Rezept mehr zu
vergeben, und das Log nennt ihn einmal.

Hast du dieses Rezept durch ein neues ersetzt und soll der Fortschritt das neue
freischalten, gib dem neuen Rezept im Skript einen Namen:

    recipes.addShaped("rail", <minecraft:rail> * 16, [[...]]);

Das registriert es als crafttweaker:rail. Leg dann eine Fortschrittsdatei hier
ab, die auf diesen Namen zeigt, und der Fortschritt greift wieder durchgehend.

Ohne Namen heißt es etwas wie crafttweaker:ct_shaped-1834729103, ein Hash des
Rezepts selbst. Der ändert sich in dem Moment, in dem du das Rezept bearbeitest,
und kann sich verschieben, wenn davor ein weiteres Rezept hinzukommt, es ist
also nicht sicher, einen Fortschritt darauf zeigen zu lassen.


Der Ordner rdploader selbst lässt sich mit der Option rootDirectory in
config/mct_resourcedatapackloader_mixin.cfg verschieben oder umbenennen. Ein
absoluter Pfad funktioniert auch, und ein Neustart ist nötig.

Leg eine pack.png neben diese Datei, um dem Pack ein Symbol zu geben.

Diese Datei wird von der Mod geschrieben und aktualisiert, sobald sie sich
ändert, alles, was du hineinschreibst, wird beim nächsten Start des Spiels also
ersetzt.

Resource Data Pack Loader
=========================

Alles, was du in diesen Ordner legst, ersetzt das, was eine Mod oder Minecraft
selbst mitbringt. Es gilt für jede Welt, im Einzelspieler wie auf Servern, und
es gibt nichts einzuschalten.


WAS SCHON DA IST
----------------

Diese Fassung der Mod wird Stück für Stück von Minecraft 1.12.2 herübergeholt.
Was jetzt da ist, ist der Pack-Ordner selbst: das Überschreiben dessen, was
Minecraft und Mods mitbringen, in jeder Welt, auf Clients wie auf Servern.
Neue Inhalte aus JSON, die Kontrolle darüber, was generiert, und der Rest
folgen, sobald sie übertragen sind, und HOWTO.md, die der Mod beiliegt,
beschreibt jedes davon, sobald es da ist.


EINE DATEI HINZUFÜGEN
---------------------

Öffne das Jar der Mod, suche die Datei, die du ändern willst, und kopiere ihren
Pfad ab 'assets' oder 'data'.

Um die Textur von Eisenerz zu ersetzen, liegt die Datei im Minecraft-Jar hier:

    assets/minecraft/textures/block/iron_ore.png

deine Fassung also hier:

    rdploader/assets/minecraft/textures/block/iron_ore.png

Eine Beutetabelle liegt stattdessen unter data und geht denselben Weg:

    data/minecraft/loot_tables/blocks/iron_ore.json
    rdploader/data/minecraft/loot_tables/blocks/iron_ore.json

Das ist die ganze Regel. Der Pfad nach 'assets' oder 'data' ist immer derselbe
wie der Pfad im Jar, es muss also nie etwas umbenannt oder verschoben werden.


ORDNUNG HALTEN
--------------

Du kannst Dateien stattdessen zu einem benannten Pack bündeln, als Ordner oder
als Zip:

    rdploader/MyTextures/assets/minecraft/textures/block/iron_ore.png
    rdploader/MyTextures.zip        (mit 'assets' oder 'data' auf oberster Ebene des Zips)

Beim Zippen wählst du den Inhalt aus und zippst diesen, nicht den Ordner darum.
Ein Zip, dessen oberste Ebene ein einzelner Ordner um 'assets' oder 'data' ist,
wird übersprungen, und das Log sagt es.

Ordner lassen sich beim Arbeiten leichter bearbeiten, Zips leichter
weitergeben. Sie verhalten sich gleich.

Liegt dieselbe Datei an zwei Stellen, gewinnt ein benanntes Pack über lose
Dateien, und /rdpl which sagt dir, welches gewonnen hat.


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
      META-INF/mods.toml
      rdploader/assets/diemod/textures/block/rubinerz.png

Das sind Vorgaben, keine Überschreibungen. Ein Mod-Pack lädt unter jedem Pack
in diesem Ordner, alles hier gewinnt also dagegen, und eine Mod darf nur
Dateien unter einem Namensraum liefern, den sie in ihrer eigenen mods.toml
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

Prüfe, ob deine Dateien in einem 'assets'- oder 'data'-Ordner liegen. Ein
Pack-Ordner oder Zip ohne einen von beiden wird übersprungen, und das Log sagt
es.


Der Ordner rdploader selbst lässt sich mit der Option rootDirectory in
config/resourcedatapackloader-common.toml verschieben oder umbenennen. Ein
absoluter Pfad funktioniert auch, und ein Neustart ist nötig.

Leg eine pack.png neben diese Datei, um dem Pack ein Symbol zu geben.

Diese Datei wird von der Mod geschrieben und aktualisiert, sobald sie sich
ändert, alles, was du hineinschreibst, wird beim nächsten Start des Spiels also
ersetzt.

Resource Data Pack Loader
=========================

Tutto ciò che metti in questa cartella sostituisce ciò che fornisce una mod o
Minecraft stesso. Vale per ogni mondo, in giocatore singolo e sui server, e non
c'è nulla da attivare.


NON SOLO OVERRIDE
-----------------

I pack qui dentro possono anche definire nuovi blocchi, oggetti, biomi e
intere dimensioni a partire da file JSON, decidere cosa si genera e dove,
bloccare le dimensioni dietro una chiave o un mob da uccidere, e creare in
anticipo il terreno di un mondo, così nessuno deve mai aspettare un chunk.
HOWTO.md, distribuito insieme alla mod, spiega tutto.


COME AGGIUNGERE UN FILE
-----------------------

Apri il jar della mod, trova il file che vuoi cambiare e copia il suo percorso
da 'assets' o 'data' in poi.

Per sostituire la texture del minerale di ferro, il file dentro il jar di
Minecraft è:

    assets/minecraft/textures/block/iron_ore.png

quindi la tua versione va qui:

    rdploader/assets/minecraft/textures/block/iron_ore.png

Una loot table si trova invece sotto data, e si procede allo stesso modo:

    data/minecraft/loot_tables/blocks/iron_ore.json
    rdploader/data/minecraft/loot_tables/blocks/iron_ore.json

Questa è tutta la regola. Il percorso dopo 'assets' o 'data' è sempre uguale a
quello dentro il jar, quindi non serve mai rinominare o spostare nulla.


TENERE TUTTO IN ORDINE
----------------------

Puoi invece raggruppare i file in un pack con nome, sotto forma di zip:

    rdploader/MyTextures.zip        (con 'assets' o 'data' in cima allo zip)

Quando crei lo zip, seleziona il contenuto e comprimi quello, non la cartella
che lo contiene. Uno zip il cui livello principale è una sola cartella che
racchiude 'assets' o 'data' viene saltato, e il log lo segnala.

Una cartella in rdploader non è un pack e viene saltata, e il log lo segnala.
Tieni i file singoli sotto assets o data, e comprimi un pack in uno zip prima
di metterlo qui.

Se lo stesso file esiste in due posti, un pack con nome ha la precedenza sui
file singoli, e /rdpl which ti dice quale ha vinto.


PRIORITÀ DEI PACK
-----------------

Se due pack con nome contengono lo stesso file, scegli quale vince
anteponendo RDPL e un numero al nome dello zip. RDPL0 si carica per primo, i
numeri più alti si caricano dopo e vince il pack caricato per ultimo:

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures.zip

Maiuscole e minuscole sono equivalenti, e uno spazio, un trattino o un
trattino basso dopo il numero è facoltativo. Il prefisso viene tolto dal nome
del pack nel log e in /rdpl list, quindi RDPL1 SeasonalTextures compare come
SeasonalTextures.


MOD API
-------

Una mod può portare contenuti RDPL dentro il proprio jar, in una cartella
chiamata rdploader organizzata esattamente come un pack:

    thatmod.jar
      META-INF/mods.toml
      rdploader/assets/thatmod/textures/block/ruby_ore.png

Sono valori predefiniti, non override. Il pack di una mod si carica sotto ogni
pack di questa cartella, quindi ciò che metti qui ha la precedenza, e una mod
può fornire file solo sotto un namespace dichiarato nel proprio mods.toml.
Tutto il resto viene ignorato con un avviso, così nessuna mod può ridefinire di
nascosto i contenuti di un'altra o i tuoi.

Ogni mod che ne include uno viene elencata in config/mods.json la prima volta
che viene rilevata:

    {
      "thatmod": {
        "enabled": true,
        "priority": -1
      }
    }

Imposta enabled su false per disattivare i contenuti di quella mod. Lascia
priority a -1 per tenerla sotto a tutto, oppure indica un numero e prenderà il
suo posto nell'ordine descritto sopra, insieme ai pack numerati. I pack sono
elencati nel log dal più basso al più alto e quello di una mod è contrassegnato,
quindi nulla si carica senza che tu possa vederlo.


RESOURCE PACK
-------------

Per impostazione predefinita i file qui stanno sopra i resource pack scelti dal
giocatore nella schermata delle opzioni, quindi un resource pack non può
sovrascriverli. Va bene per cose come il logo di un modpack e non va bene per
le texture che vorresti lasciare libere di essere ritinteggiate.

Aggiungi O o N dopo il prefisso RDPL per decidere pack per pack:

    rdploader/RDPLO Branding          vince sempre, immune ai resource pack
    rdploader/RDPLN BaseTextures      un resource pack può sovrascriverlo
    rdploader/RDPL1O Seasonal         priorità e vince sempre, entrambe insieme

I pack senza lettera seguono l'opzione overrideResourcePacks nella
configurazione, e /rdpl list contrassegna quelli che sovrascrivono.

La stessa regola vale per i data pack. Un pack contrassegnato con N sta sotto i
data pack che un mondo ha nella propria cartella datapacks, e uno contrassegnato
con O sta sopra.

I pack senza prefisso si caricano prima di tutti i pack numerati, in ordine
alfabetico, quindi un pack numerato vince sempre su uno non numerato.

Per disattivare un pack senza cancellarlo, aggiungi .disabled alla fine del suo
nome:

    rdploader/RDPL1 SeasonalTextures.zip.disabled

Il pack viene saltato e il log lo segnala. Togli il suffisso per riattivarlo.


COSA PUOI CAMBIARE
------------------

Texture, modelli, blockstate, file di lingua, suoni, font, testi dello splash,
e qualsiasi altra cosa una mod tenga nella propria cartella assets, come libri
guida o manuali.

Advancement, loot table, ricette, tag, funzioni, modelli di struttura e
qualsiasi altra cosa una mod tenga nella propria cartella data. Sono lato
server, quindi funzionano anche su un server dedicato, e una modifica ha effetto
con /reload.

Si può anche togliere qualcosa. Un file in recipe_removals elimina le ricette
per nome, namespace o risultato, e un file in disabled mette fuori gioco blocchi
e oggetti senza annullarne la registrazione, così i mondi mantengono i propri id
ed eliminare il file riporta tutto com'era. Una loot injection aggiunge un pool
a una loot table già esistente invece di sostituirla, e block_drops aggiunge o
sostituisce ciò che lascia un blocco che non è tuo. Anche le ricette della
fornace, i tempi di combustione, le schede creative e gli eventi sonori sono
file a sé.

Si può insegnare all'incudine un lavoro nuovo. Un file in anvils indica un
oggetto, quello che gli si abbina nello slot di destra, e gli incantesimi o il
risultato che l'incudine offre per i livelli indicati. Estrarlo può far
ottenere un advancement, e l'oggetto può restare inutilizzabile finché quel
advancement non viene ottenuto.

Un file in tooltips aggiunge righe grigie di descrizione a oggetti e blocchi, e
una descrizione sotto il nome di un incantesimo.

Un file hardness imposta il tempo di estrazione e la resistenza alle esplosioni
di un gruppo di blocchi, e un file exposures definisce un pericolo come la
radiazione: colpisce i giocatori vicino a certi blocchi, che portano certi
oggetti o che si trovano in certe dimensioni, con livelli che applicano ciascuno
effetti e danni, e può essere contratto dai mob e dai giocatori vicini o cadere
con la pioggia.


AGGIUNGERE NUOVI CONTENUTI
--------------------------

Un pack può anche aggiungere blocchi, oggetti e fluidi propri, descritti in
JSON. Non devi scrivere né compilare una mod per farlo.

Le definizioni stanno sotto data, una cartella per ogni tipo di elemento. Ogni
chiave dentro "variants" è un nome, quindi un file in

    rdploader/data/mypack/blocks/ores.json

che contiene una variante chiamata ruby_ore registra mypack:ruby_ore. Il nome
del file serve solo a raggruppare. Se una mod vera registra già quel nome, vince
la mod e la tua variante viene saltata.

Il blocco più semplice sono poche righe:

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "ruby_ore": { "hardness": 3.0, "harvestLevel": 1 }
      }
    }

Modello, blockstate, texture e voce di lingua restano a tuo carico sotto
assets, come per qualsiasi altro file in questa cartella.

Ognuna di queste è una cartella sotto data/<yourpack>:

    blocks           items            fluids           materials
    tabs             sounds           biomes           worldgen
    caveregions      dimensions       worldtemplates   worldintro
    gates            gamerules        teams            scoring
    raids            entities         hardness         anvils
    exposures        overrides        villages         pathintersects
    structuremaps    citymaps         portalframes     blastplaster
    structures       recipes          recipe_removals  furnace
    fuels            brewing          potions          potion_types
    villagers        trades           loot_tables      loot_injections
    block_drops      player_loot      advancements     functions
    tags             registry_remap   cards            disabled
    tooltips
    harvests
    carries

I blocchi hanno queste forme, impostate dal campo "type":

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

e gli oggetti queste:

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   container

Un tipo di pozione si indica con la chiave di lingua
item.minecraft.potion.effect.<baseName>, con splash_potion, lingering_potion o
tipped_arrow al posto di potion per le altre forme. Un oggetto potion_bottle è
il tuo contenitore per le pozioni: accetta una creativeTab come qualsiasi altro
oggetto e contiene i tipi di pozione che indichi in potionTypes.

Un file villagers/<name>.json definisce una professione. Un file trades/*.json
aggiunge scambi a qualsiasi professione, tua o di Minecraft, indicando la
professione e il livello a cui lo scambio compare.

Un file entities/<name>.json crea una nuova entità a partire da una già
presente. Indica l'entità su cui basarsi e ciò che la distingue: il nome,
l'aspetto, la salute e i danni, come si muove, come combatte e cosa lascia. È
un'entità a sé, con uovo di spawn e loot table propri, e quella da cui deriva
resta intatta. Può anche avere un proprio inventario, slot per oggetti, un
serbatoio di fluido e un accumulatore di energia che tubi e cavi raggiungono e
che il giocatore apre accovacciandosi e facendo clic destro su di essa.

Un file villages/<name>.json aggiunge un lotto che una città o un villaggio può
costruire a partire da uno dei tuoi modelli .nbt, e un file raids/<name>.json
manda ondate contro un villaggio quando un giocatore vi porta un presagio.

Un file cards/<name>.json mostra una carta sullo schermo quando accade qualcosa,
ad esempio quando un giocatore entra in un bioma, e un file nella stessa
cartella con il nome di uno dei messaggi della mod cambia ciò che quel messaggio
dice.

Un file worldintro/<name>.json mostra una serie di pagine quando qualcuno entra
nel mondo, prima che ne prenda il controllo. Le parole sono semplici file .txt
sotto assets/<yourpack>/texts. Può partire una volta per giocatore o a ogni
ingresso.

Un file teams/<name>.json schiera una squadra nel tabellone del gioco e dice chi
ne fa parte, e un file scoring/<name>.json è un obiettivo che assegna punti a
quelle squadre e decide come finisce una partita.


MONDI INTERI
------------

Un pack non si limita alle singole cose. dimensions/<name>.json registra una
dimensione con terreno, biomi e cielo propri. gates/<name>.json pone una
condizione per raggiungerla, ad esempio possedere o consumare un oggetto. Un
blocco di tipo portal manda chiunque vi entri in un'altra dimensione, e
portalframes permette al giocatore di costruire e accendere un telaio proprio.

worldtemplates/<name>.json riunisce le impostazioni di un mondo in un unico
file, così un pack può distribuire un'intera forma di mondo in una volta invece
di chiedere una dozzina di modifiche alla configurazione. Può anche plasmare la
Superficie stessa, ad esempio il livello del mare o se gli oceani sono di lava.

Una dimensione può anche stabilire come appare e si comporta sopra la testa.
Nebbia, tinta della luce, attenuazione di sole e luna, strati di nuvole e
distorsione del calore vengono disegnati sul tuo schermo, e il suo meteo decide
se piove, nevica o c'è tempesta, quanto durano gli acquazzoni, e il colore, le
particelle, il suono e l'angolo della pioggia.

worldgen è più dei minerali. Una voce piazza una forma, da una piccola macchia
del tuo blocco fino a uno dei tuoi modelli .nbt, e decide con quale frequenza,
a che altezza e in quali biomi compare.

Un file biomes/<name>.json definisce un bioma: clima e colori, i blocchi di cui
è fatto, cosa lo decora, cosa vi compare e dove si genera.


DOVE ARRIVA IL LIMITE
---------------------

Descrive cos'è una cosa, non cosa fa nel tempo. Tutto ciò che richiede un'entità
blocco, una schermata o codice eseguito a ogni tick ha ancora bisogno di una
mod vera, con due eccezioni: un blocco di tipo container contiene un inventario
con una schermata propria, e una variante di entità può avere un inventario. Una
macchina è fuori portata; un minerale, una recinzione, un cibo o un fluido no.


PACK DI ALTRE VERSIONI
----------------------

Un pack creato per la linea 1.12.2 di questa mod si carica qui così com'è. Uno
zip viene convertito una sola volta, al proprio interno, in una cartella
versions per questa versione, e i file 1.12.2 restano com'erano, quindi lo
stesso zip continua a funzionare su ogni versione.


VEDERE LE MODIFICHE
-------------------

Premi F3+T per ricaricare texture, modelli, file di lingua e tutto il resto
sotto assets. Su un server, o per qualsiasi cosa sotto data, digita /reload.

Se aggiungi un file nuovo o ne elimini uno, usa invece /rdpl reload. Modificare
un file già presente richiede solo F3+T o /reload.

/rdpl list mostra ogni pack caricato e il suo contenuto. Passa il mouse su un
pack per vederlo.

/rdpl which minecraft:textures/block/stone.png mostra quale pack fornisce un
file e quali pack restano oscurati sotto di esso.

/rdpl config unused elenca i file di opzioni in rdploader/config che nessun pack
installato definisce più, e /rdpl config prune li elimina. /rdpl pixelmap mostra
cosa ne è uscito da una mappa di pixel, e /rdpl biome list e here danno
informazioni sui biomi.

Funzionano senza essere operatore, perché leggono soltanto file sul tuo
computer. Su un server dedicato, /rdplserver reload riesamina la copia del
server, e /rdplserver list, which e unused rispondono per quella copia.


SE QUALCOSA NON FUNZIONA
------------------------

Controlla prima il log. logs/rdpl.log elenca ogni pack caricato e ogni pack
saltato, con il motivo, e ogni problema viene registrato come avviso che ne
spiega la causa.

/rdpl unused elenca ogni file dei tuoi pack che finora nessuno ha richiesto, il
che di solito significa un errore di battitura nel percorso. Eseguilo dopo che
il gioco ha finito di caricarsi, e tieni presente che alcuni file si caricano
solo quando servono, come le lingue diverse da quella in cui giochi.

Le maiuscole contano. Se il tuo file è Stone.png e il gioco ha chiesto
stone.png, si carica comunque, ma un avviso ti dice di rinominarlo. Rinominalo,
perché fuori da questa mod il file non verrebbe trovato affatto. I file di
lingua sono quelli che fanno inciampare più spesso: si chiamano en_us.json, non
en_US.json.

Controlla che i tuoi file siano dentro una cartella 'assets' o 'data'. Uno zip
senza nessuna delle due viene saltato, e il log lo segnala.


ADVANCEMENT E RICETTE
---------------------

Una ricetta che uno script aggiunge o sostituisce è nota con il nome che le dà
lo script. Per sbloccarla con un advancement, metti qui un file di advancement
che nomini quella ricetta, e l'advancement torna a funzionare da cima a fondo.

Dai a una ricetta di questo tipo un nome fisso nello script. Un nome generato
per te può cambiare nel momento in cui modifichi la ricetta, quindi non è sicuro
collegarvi un advancement.


La cartella rdploader stessa può essere spostata o rinominata con l'opzione
rootDirectory in config/resourcedatapackloader-common.toml. Funziona anche un
percorso assoluto, e serve un riavvio.

Metti un pack.png accanto a questo file per dare un'icona al pack.

Questo file viene scritto dalla mod e aggiornato ogni volta che cambia, quindi
tutto ciò che scrivi al suo interno viene sostituito al prossimo avvio del
gioco.

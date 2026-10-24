Resource Data Pack Loader
=========================

Tutto ciò che metti in questa cartella sostituisce quanto fornito da una mod o
da Minecraft stesso. Vale per ogni mondo, in giocatore singolo e sui server, e
non c'è nulla da attivare.


PIÙ CHE SEMPLICI OVERRIDE
-------------------------

I pack qui dentro possono anche definire nuovi blocchi, oggetti, biomi e intere
dimensioni tramite file JSON, decidere cosa si genera e dove, bloccare le
dimensioni dietro una chiave o un mob che va ucciso, e generare in anticipo il
terreno di un mondo, così nessuno deve mai aspettare un chunk. HOWTO.md,
distribuito insieme alla mod, spiega tutto questo.


COME AGGIUNGERE UN FILE
-----------------------

Apri il jar della mod, trova il file che vuoi modificare e copia il suo percorso
da 'assets' in poi.

Per sostituire la texture del minerale di ferro, il file nel jar di Minecraft è:

    assets/minecraft/textures/blocks/iron_ore.png

quindi la tua versione va qui:

    rdploader/assets/minecraft/textures/blocks/iron_ore.png

Questa è l'unica regola. Il percorso dopo 'assets' è sempre identico a quello
dentro il jar, quindi non serve mai rinominare o spostare nulla.


TENERE ORDINE
-------------

Puoi raggruppare i file in un pack con nome, sotto forma di zip:

    rdploader/MyTextures.zip        (con 'assets' al livello più alto dello zip)

Quando crei lo zip, seleziona il contenuto e comprimi quello, non la cartella
che lo contiene. Uno zip il cui livello più alto è una singola cartella che
racchiude 'assets' viene ignorato, e il log lo segnala.

Una cartella in rdploader non è un pack e viene ignorata, e il log lo segnala.
Tieni i file sciolti sotto assets e comprimi un pack in uno zip prima di
metterlo qui.

Se lo stesso file esiste in due posti, un pack con nome vince sui file sciolti.
Il log indica da quale pack proviene ogni file, così puoi sempre vedere quale ha
vinto.


PRIORITÀ DEI PACK
-----------------

Se due pack con nome contengono lo stesso file, puoi decidere quale vince
anteponendo RDPL e un numero al nome dello zip. RDPL0 viene caricato per primo,
i numeri più alti vengono caricati dopo e vince il pack caricato per ultimo:

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures.zip

Maiuscole e minuscole sono equivalenti, e uno spazio, un trattino o un
trattino basso dopo il numero è facoltativo. Il prefisso viene tolto dal nome
del pack nel log e in /rdpl list, quindi RDPL1 SeasonalTextures compare come
SeasonalTextures.


API PER LE MOD
--------------

Una mod può includere contenuti RDPL nel proprio jar, in una cartella chiamata
rdploader, organizzata esattamente come un pack:

    thatmod.jar
      mcmod.info
      rdploader/assets/thatmod/blocks/ruby_ore.json

Questi sono valori predefiniti, non override. Il pack di una mod viene caricato
sotto tutti i pack di questa cartella, quindi ciò che metti qui ha la meglio, e
una mod può fornire file solo sotto un namespace dichiarato nel proprio
mcmod.info. Tutto il resto viene ignorato con un avviso, così nessuna mod può
ridefinire di nascosto i contenuti di un'altra o i tuoi.

Ogni mod che ne include uno viene elencata in config/mods.json la prima volta
che viene rilevata:

    {
      "thatmod": {
        "enabled": true,
        "priority": -1
      }
    }

Imposta enabled su false per disattivare i contenuti di quella mod. Lascia
priority a -1 per tenerli sotto a tutto, oppure indica un numero e prenderanno
il loro posto nell'ordine descritto sopra, accanto ai pack numerati. Nel log i
pack sono elencati dal più basso al più alto e quello di una mod è
contrassegnato, così non si carica nulla che tu non possa vedere.


RESOURCE PACK
-------------

Per impostazione predefinita, i file qui stanno sopra ai resource pack che il
giocatore sceglie nella schermata delle opzioni, quindi un resource pack non
può sostituirli. Va bene per cose come il logo di un modpack e non va bene per
le texture che vorresti far personalizzare agli utenti.

Aggiungi O oppure N dopo il prefisso RDPL per decidere pack per pack:

    rdploader/RDPLO Branding          vince sempre, i resource pack
                                      non possono toccarlo
    rdploader/RDPLN BaseTextures      un resource pack può sostituirlo
    rdploader/RDPL1O Seasonal         priorità e vince sempre, entrambe insieme

I pack senza lettera seguono l'opzione overrideResourcePacks nella
configurazione, e /rdpl list contrassegna quelli che sostituiscono.

I pack senza prefisso vengono caricati prima di tutti i pack numerati, in ordine
alfabetico, quindi un pack numerato vince sempre su uno non numerato.

Per disattivare un pack senza eliminarlo, aggiungi .disabled alla fine del suo
nome:

    rdploader/RDPL1 SeasonalTextures.zip.disabled

Il pack viene ignorato e il log lo segnala. Togli il suffisso per riattivarlo.


COSA PUOI MODIFICARE
--------------------

Texture, modelli, blockstate, file di lingua, suoni, font, testi splash e
qualsiasi altra cosa una mod tenga nella sua cartella assets, come libri guida
o manuali.

Progressi e loot table. Sono lato server, quindi funzionano anche su un server
dedicato.

Modelli di strutture, i file .nbt sotto assets/<modid>/structures. Una struttura
salvata nella cartella structures del mondo stesso vince comunque su un file
qui, e una struttura già piazzata resta caricata finché non esci dal mondo.

Ricette, compresa la sostituzione della ricetta di una mod o l'aggiunta di una
tua. Le ricette si caricano solo all'avvio del gioco, quindi una modifica qui
richiede un riavvio, non una ricarica.

Funzioni, i file .mcfunction sotto assets/<modid>/functions. Minecraft le legge
solo dalla cartella dati del mondo, quindi metterle qui le fa funzionare in
ogni mondo. Una funzione salvata nel mondo vince comunque su un file qui.

Ridenominazioni del registro, così un mondo salvato prima che una mod
rinominasse uno dei suoi blocchi mantiene quel blocco invece di perderlo.
Metti un file in assets/<modid>/registry_remap:

    {
      "registry": "minecraft:items",
      "mapping": { "oldmod:old_name": "newmod:new_name" }
    }

Il registro è quello a cui appartiene la voce, di solito minecraft:items o
minecraft:blocks. Le ridenominazioni si concatenano: se A viene mappato su B e
poi B su C, A porta a C.

Proprietà di cose che esistono già, senza toccare i loro file. Un file in
assets/<yourpack>/overrides indica il proprio bersaglio tramite il percorso,
quindi overrides/minecraft/stone.json modifica minecraft:stone, vanilla o con
mod che sia. I blocchi accettano durezza, resistenza alle esplosioni, luce,
opacità alla luce, scivolosità, suono, strumento e livello di raccolta, e
infiammabilità. Gli oggetti accettano dimensione della pila, durabilità e un
oggetto contenitore, e qualsiasi oggetto può diventare commestibile, con valori
nutrizionali ed effetti. Si possono riscrivere gli effetti di una pozione.
Tutto questo è attivo in tempo reale: disattiva il pack ed esegui /rdpl reload,
e ogni valore torna a com'era, senza bisogno di riavvio. Metti l'id della mod
proprietaria in "requires", così il file viene ignorato in silenzio quando
quella mod non è installata.

Il bottino del giocatore, qualcosa per cui il gioco non ha alcun nome. I
giocatori lasciano cadere il loro inventario e nient'altro, quindi un file in
assets/<modid>/player_loot dà loro una loot table tutta propria:

    {
      "table": "mypack:entities/player",
      "mode": "add",
      "rollOnKeepInventory": false
    }

"add" fa cadere ciò che la tabella genera insieme a tutto ciò che portavano con
sé, "replace" lo fa cadere al posto del loro inventario, e rollOnKeepInventory
decide se la tabella venga generata del tutto in un mondo in cui gli inventari
vengono conservati.

Lo stendardo è un caso a parte. Una sola definizione registra due blocchi, il
tuo e un secondo chiamato <name>_wall, e solo quello in piedi ottiene un
oggetto, che sceglie fra i due mentre lo piazzi. Il suo modello si estende ben
oltre il proprio blocco, fino a 29.33 su 16 in piedi e 13 sotto il blocco su una
parete, e il blockstate in piedi richiede il formato di Forge per ruotare in
sedicesimi. Le guide riportano le misure complete.

I drop di un blocco possono essere casuali e non devono per forza essere
oggetti. Le voci nella sua lista drops sono decise ciascuna per conto proprio,
a meno che tu non assegni un peso: in quel caso condividono un'unica urna e ne
esce esattamente una. Una voce che nomina un'entità al posto di un blocco fa
comparire quell'entità dove si trovava il blocco.

Una texture può essere scritta come JSON invece che disegnata. Dai al file il
nome del PNG con .json in fondo, textures/blocks/panel.png.json, e indica una
dimensione come 16x16 o 16x32, una tavolozza di un carattere per colore e righe
di quei caratteri dall'alto verso il basso. Un altro file di questo tipo può
estenderlo e indicare solo i colori che vuoi diversi, così una stessa forma può
essere ricolorata quante volte vuoi senza un solo file immagine. Quello che
disegnano viene conservato in pixelmap-cache qui e ridisegnato ogni volta che
una mappa o il suo modello cambia.

Si può anche togliere qualcosa. Un file in recipe_removals elimina le ricette
per nome, namespace o risultato, e un file in disabled toglie blocchi e oggetti
dal gioco senza annullarne la registrazione, così i mondi mantengono i loro id
ed eliminando il file tutto torna com'era. Una loot injection aggiunge un pool a
una loot table già esistente invece di sostituirla, e block_drops aggiunge o
sostituisce ciò che lascia cadere un blocco che non è tuo. Anche le ricette
della fornace, i tempi di combustione dei combustibili, i nomi dell'ore
dictionary, le schede creative e gli eventi sonori sono file a sé.

Si può insegnare all'incudine un nuovo lavoro. Un file in anvils indica un
oggetto, l'oggetto che lo accompagna nello slot destro e gli incantesimi o il
risultato che l'incudine offre per i livelli indicati. Estrarlo può far
ottenere un progresso, e l'oggetto può restare inutilizzabile finché quel
progresso non viene ottenuto.

Un file in tooltips aggiunge righe grigie di descrizione a oggetti e blocchi, e
una descrizione sotto il nome di un incantesimo.

Un file hardness imposta il tempo di estrazione e la resistenza alle esplosioni
per un gruppo di blocchi, e un file exposures definisce un pericolo come la
radiazione: colpisce i giocatori vicino a blocchi indicati, che portano oggetti
indicati o che si trovano in dimensioni indicate, in livelli che applicano
ciascuno effetti e danni, e può essere contratto da mob e giocatori vicini o
cadere con la pioggia.

CraftTweaker e GroovyScript funzionano ancora esattamente come prima. Vengono
eseguiti dopo questa mod, quindi tutto ciò che i tuoi script rimuovono o
modificano vince su un file qui.

RDPL è ottimo per sostituire una o due ricette, e le ricette per i tuoi
contenuti vanno nel pack insieme a essi. Per un controllo completo delle ricette
in un modpack, CraftTweaker e GroovyScript sono opzioni migliori. Un file qui
sostituisce l'originale per intero, quindi per cambiare un ingrediente o
eliminare una voce di bottino usa quelli.


AGGIUNGERE NUOVI CONTENUTI
--------------------------

Un pack può anche aggiungere blocchi, oggetti e fluidi propri, descritti come
JSON. Non serve scrivere né compilare una mod.

Il percorso del file è il suo nome. Un blocco in

    rdploader/MyPack/assets/mypack/blocks/copper_ore.json

viene registrato come mypack:copper_ore. Non c'è alcun campo nome da compilare o
sbagliare. Se una mod vera registra già quel nome, vince la mod e il tuo file
viene ignorato.

Il blocco più semplice è poche righe:

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "copper_ore": { "meta": 0, "hardness": 3.0, "harvestLevel": 1 }
      }
    }

Il modello, il blockstate, la texture e la voce di lingua li fornisci comunque
allo stesso modo di qualsiasi altro file in questa cartella.

Ognuna di queste è una cartella sotto assets/<yourpack>:

    blocks           items            fluids           materials
    worldgen         furnace          fuels            oredict
    sounds           tabs             recipes          recipe_removals
    loot_tables      loot_injections  advancements     functions
    structures       registry_remap   potions          potion_types
    brewing          villagers        trades           biomes
    villages         entities         gates            dimensions
    gamerules        worldtemplates   worldintro       pathintersects
    hardness         blastplaster     player_loot      overrides
    teams            scoring          caveregions      exposures
    structuremaps    citymaps         portalframes     block_drops
    texts            anvils           cards            disabled
    celestial        raids            tooltips         harvests
    carries          dialogs          veins            diets
    enchantments     dice             games            orders
    ranks            recipe_locks     spawn_locks

I blocchi hanno queste forme, definite dal campo "type":

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

e gli oggetti queste:

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   rocket

Un tipo di pozione compare sempre sulla pozione vanilla, sulla pozione
esplosiva, sulla pozione persistente e sulla freccia con effetto, che si
trovano nelle schede Preparazione pozioni e Combattimento. La scheda è una
proprietà dell'oggetto, non del tipo di pozione, quindi non c'è modo di
spostarli in una scheda tua. Un oggetto potion_bottle è invece un tuo
contenitore: accetta una creativeTab come qualsiasi altro oggetto, elenca i
tipi di pozione indicati in potionTypes, e il banco di distillazione lo accetta
ovunque funzioni una bottiglia di vetro.

Un file villagers/<name>.json definisce una professione e le carriere che
offre. Un file trades/*.json aggiunge scambi a qualsiasi carriera, tua o di
Minecraft, indicando la professione, la carriera e il livello a cui lo scambio
compare. Se indichi una carriera che non esiste, il log elenca quelle che
esistono.

Un file entities/<name>.json crea una nuova entità a partire da una già
presente. Indica l'entità su cui basarsi e che cosa cambia: il nome, la skin,
quanta salute e quanto danno ha, quanto veloce si muove e quanto in alto salta,
quanto grande viene disegnata, cosa indossa, cosa caccia e cosa ignora, e se
obbedisce ancora alle regole di spawn dell'entità da cui è stata creata. È
un'entità a sé, con il proprio uovo generatore e la propria loot table, e
quella da cui è stata creata resta intatta. Si può dire a un lotto di villaggio
di ospitarne una al posto di un villico. Può anche avere un deposito proprio,
slot per oggetti, un serbatoio di fluido e un accumulatore di energia a cui
arrivano tubi e cavi e che un giocatore apre accovacciandosi e facendo clic
destro su di essa, e un razzo di Galacticraft può essere variato allo stesso
modo e piazzato su una piattaforma di lancio con un oggetto di tipo rocket.

Un file villages/<name>.json aggiunge un lotto che i villaggi possono
costruire, che sia una fattoria da descrivere o uno dei tuoi modelli .nbt. Le
stesse impostazioni scelgono quali pezzi vanilla compaiono ancora, quanto
distano tra loro i villaggi alla creazione e in quali biomi possono comparire.

Un file worldintro/<name>.json riproduce una serie di pagine quando qualcuno
entra nel mondo, prima che prenda il controllo: testo scorrevole su un'immagine,
una schermata del titolo, una presentazione, con una musica di sottofondo se
vuoi. I testi sono semplici file .txt sotto assets/<yourpack>/texts. Può essere
riprodotto una volta per giocatore o a ogni ingresso.

Un file teams/<name>.json schiera una squadra sul tabellone segnapunti del gioco
a cui si uniscono, appena arrivano, mob, giocatori o qualsiasi cosa compaia in
un angolo del mondo, e un file scoring/<name>.json è un obiettivo che assegna
punti a uccisioni e morti di quelle squadre, termina un round a un punteggio o a
un tempo, mostra la classifica in chat o come carta e può ripristinare la mappa
per il round successivo.

Un file raids/<name>.json manda ondate contro un villaggio quando un giocatore
con l'effetto presagio vi entra. Una barra del boss mostra quanti predoni
restano, i villici corrono al chiuso, i predoni abbattono le porte di legno
e tutte le campane del villaggio suonano all'arrivo di un'ondata.

Un file cards/<name>.json mostra una carta a schermo quando succede qualcosa,
come un giocatore che entra in un bioma, e un file nella stessa cartella con il
nome di uno dei messaggi della mod cambia ciò che quel messaggio dice.

MONDI INTERI
------------

Un pack non è limitato alle singole cose. dimensions/<name>.json registra una
dimensione con terreno, biomi e cielo propri. gates/<name>.json pone una
condizione per raggiungerla, come possedere o consumare un oggetto. Un blocco di
tipo portal manda chi vi entra e ricorda chi lo ha costruito.

Un modello di mondo può anche dare forma al sovramondo stesso, impostando il
livello del mare, gli oceani di lava e il rumore del terreno. Questo viene
applicato alla creazione del mondo e mai dopo, quindi un mondo già esistente
resta com'era.

worldtemplates/<name>.json riunisce le impostazioni di un mondo in un solo file,
così un pack può distribuire in un colpo solo un'intera forma di mondo invece di
chiedere una dozzina di modifiche alla configurazione. Ogni gruppo che può
impostare risponde anche alla categoria control nella configurazione, che
decide se a decidere è il pack, se è la configurazione, oppure se il gruppo è
del tutto disattivato e nessun pack può attivarlo.

Una dimensione può anche stabilire come appare e come si comporta sopra la
testa. Nebbia, tonalità della luce, attenuazione di sole e luna, strati di
nuvole e foschia da calore vengono disegnati sul tuo schermo, e il suo meteo
decide se piove, nevica o c'è tempesta, quanto durano gli acquazzoni, e il
colore, le particelle, il suono e l'angolo della pioggia.

Con Galacticraft installato, celestial/<name>.json colloca sistemi stellari,
pianeti, lune, fasce di asteroidi e stazioni spaziali di un pack sulla mappa
stellare di Galacticraft, e una dimensione con un blocco galacticraft diventa un
luogo in cui vola un razzo.

worldgen è più che minerali. Una voce è una forma collocata da una diffusione:
blob, filoni lunghi, lastre, geodi, conche, guglie, noduli, sfiati, decorazione
della superficie, alberi interi, rampicanti, fasce che attraversano più chunk, o
uno dei tuoi modelli .nbt, distribuiti in modo uniforme, attorno a un'altezza,
in modo frattale, lungo il terreno, sul pavimento o sul soffitto delle grotte, o
sott'acqua.


Un file biomes/<name>.json definisce un bioma: il suo clima e i suoi colori, i
blocchi di cui è fatto, cosa lo decora, cosa vi compare e dove si genera. Il suo
numero viene scelto per te e scritto in ogni mondo la prima volta che quel mondo
viene caricato, così resta fisso in seguito qualunque altra cosa installi.
Imposta "id" solo quando un bioma deve mantenere un numero già usato da
qualcos'altro, ad esempio quando un pack sostituisce una mod che viene ritirata.
Rinominare o eliminare un bioma che un mondo contiene già lo fa perdere, come
rinumerare un blocco, quindi per rinominare usa invece registry_remap.

Il nome visualizzato di un villico è la chiave di lingua
entity.Villager.<career>, con il nome della carriera esattamente come lo hai
scritto e nient'altro. Quello spazio di chiavi è condiviso con Minecraft e con
ogni altro pack, quindi metti il tuo namespace nel nome della carriera, come in
rdpltest.prospector. Cambia solo il nome: un villico memorizza la propria
carriera come numero, quindi rinominarne una cambia il modo in cui vengono
chiamati i villici esistenti, e riordinare l'elenco delle carriere cambia
quale carriera hanno.

Un tipo di pozione prende il nome dal suo file allo stesso modo, e il suo nome
visualizzato deriva dalla chiave di lingua potion.effect.<namespace>.<name>,
con splash_potion.effect., lingering_potion.effect. e tipped_arrow.effect. per
le altre tre forme.


MONDI RUBIC
-----------

Un modello di mondo può chiedere un mondo costruito con cubi invece che con
colonne di 256 blocchi, e il mondo cresce allora in entrambe le direzioni: un
fondo molto sotto lo zero, un soffitto molto sopra il 255, con terreno, grotte e
minerali in tutto lo spazio. Giocarci è normale. Scavi, costruisci, illumini e
viaggi allo stesso modo, e la finestra di generazione vanilla mantiene la sua
forma abituale dentro il mondo più alto, così le mod che generano terreno lo
collocano dove lo hanno sempre fatto.

Ciò che ne ottiene un pack: un'altezza del mondo propria, impostata una volta
sola alla creazione del mondo; un mondo profondo sotto la finestra vanilla, con
grotte di rumore, falde acquifere e filoni di minerale a bande in una pietra che
indichi tu; regioni di grotte, la risposta dei pack ai biomi delle grotte,
dipinte nel sottosuolo in tre dimensioni con pavimenti, soffitti, livello
dell'acqua, mob e strutture propri; dimensioni impilate l'una sull'altra, così
cadere dal fondo di un mondo ti porta in quello sottostante e uscire dalla cima
ti riporta indietro; e qualsiasi dimensione esclusa, che mantiene il suo mondo
ordinario nello stesso salvataggio.

Sotto il cofano, un mondo è memorizzato come cubi di 16 per 16 per 16 in file
di regione propri accanto a quelli vanilla, generati, caricati e salvati in modo
indipendente, con un motore di illuminazione scritto per quella forma. Il
presupposto vanilla che un mondo sia alto 256 blocchi viene corretto ovunque sia
determinante, dai limiti di costruzione e dai piani di morte fino a pathfinding,
portali, fari, mappe e renderer. I generatori delle altre mod continuano a
vedere una finestra di aspetto normale di 256 blocchi, ed è per questo che il
loro terreno funziona.

HOWTO.md riporta le impostazioni, le altezze che un mondo può avere e le mod
accanto alle quali non funzionerà.


ATTENZIONE AL META
------------------

Ogni variante ha un numero meta, ed è quel numero che il file del mondo
memorizza. Rinumerare una variante che le persone hanno già in un mondo
trasforma i loro blocchi in qualcos'altro. Aggiungi le nuove varianti in fondo
e non rinumerare mai una vecchia.

Un blocco contiene 16 varianti, perché è quanto permettono quattro bit di
metadati. Le lastre ne ottengono 8, dato che un bit indica alto o basso, mentre
scale, scale a pioli, torce e coltivazioni ne ottengono 1, perché
l'orientamento o l'età usano il resto. Gli oggetti non sono così vincolati e
possono saltare numeri.


DOVE SI FERMA
-------------

Questo descrive che cosa è una cosa, non che cosa fa nel tempo. Tutto ciò che
richiede una tile entity, una GUI o codice eseguito a ogni tick richiede ancora
una vera mod, con due eccezioni: un blocco di tipo container contiene un
inventario con una schermata propria, e una variante di entità può avere un
deposito. Una macchina è fuori portata; un minerale, una recinzione, un cibo o
un fluido no.


PACK DI ALTRE VERSIONI
----------------------

Un pack creato per la linea 1.20.1, 1.21.1 o 26.x di questa mod viene caricato
anche qui. Uno zip viene convertito una volta, al proprio interno, in una
cartella versions/1.12.2, e i file moderni restano com'erano, quindi lo stesso
zip continua a funzionare su ogni versione.


VEDERE LE MODIFICHE
-------------------

Premi F3+T per ricaricare texture, modelli, file di lingua, progressi e loot
table. Su un server, digita /reload per ottenere lo stesso risultato. Le ricette
sono l'eccezione, come sopra: si caricano solo all'avvio, quindi una modifica
alle ricette richiede un riavvio.

Se aggiungi un nuovo file o ne elimini uno, usa invece /rdpl reload. Modificare
un file già presente richiede solo F3+T.

/rdpl reload textures ricarica solo le texture, il che è molto più veloce di
F3+T in un pack grande. models, languages, sounds e shaders funzionano allo
stesso modo. Ometti il nome per riesaminare la cartella e ricaricare tutto.

/rdpl list mostra ogni pack caricato e ciò che contiene. Fai clic su un pack per
vederlo.

/rdpl which minecraft:textures/blocks/stone.png mostra quale pack fornisce un
file e quali pack restano oscurati sotto di esso.

/rdpl config unused elenca i file di opzioni in rdploader/config che nessun pack
installato definisce più, e /rdpl config prune li elimina. /rdpl pixelmap mostra
che cosa è risultata una pixel map, /rdpl biome list e here ti informano sui
biomi, e /rdpl team e /rdpl round servono per le squadre e i round che un pack
definisce.

Questi comandi funzionano anche senza essere operatore, perché leggono solo file
sul tuo computer. Su un server dedicato, /rdplserver reload riesamina la copia
del server.


SE QUALCOSA NON FUNZIONA
------------------------

Controlla prima il log. Progressi, loot table, ricette, funzioni e strutture
vengono registrati con il pack da cui provengono, e qualsiasi problema viene
registrato come avviso che ne spiega il motivo.

Per texture e altre risorse, /rdpl unused elenca ogni file nei tuoi pack che
nessuno ha ancora richiesto, il che di solito indica un errore di battitura nel
percorso. Eseguilo dopo che il gioco ha finito di caricarsi, e tieni presente
che alcuni file si caricano solo quando servono, come le lingue diverse da
quella in cui giochi.

Le maiuscole contano. Se il tuo file è Stone.png e il gioco ha richiesto
stone.png, viene caricato comunque, ma un avviso ti dice di rinominarlo.
Rinominalo, perché fuori da questa mod il file non verrebbe trovato affatto. I
file di lingua sono quelli che fanno inciampare più spesso: sono en_us.lang, non
en_US.lang.

Controlla che i tuoi file si trovino dentro una cartella 'assets'. Uno zip che
non ne ha una viene ignorato, e il log lo segnala.



PROGRESSI E RICETTE
-------------------

Se i tuoi script rimuovono una ricetta, ogni progresso che la sbloccava continua
a funzionare invece di rompersi. Semplicemente non ha più nessuna ricetta da
darti, e il log la nomina una volta.

Se hai sostituito quella ricetta con una nuova e vuoi che il progresso sblocchi
quella nuova, dai alla nuova ricetta un nome nel tuo script:

    recipes.addShaped("rail", <minecraft:rail> * 16, [[...]]);

Questo la registra come crafttweaker:rail. Poi metti qui un file di progresso
che punti a quel nome, e il progresso funziona di nuovo da cima a fondo.

Senza un nome le viene dato qualcosa come crafttweaker:ct_shaped-1834729103,
un hash della ricetta stessa. Cambia nel momento in cui modifichi la ricetta e
può spostarsi se prima viene aggiunta un'altra ricetta, quindi non è sicuro
puntarvi un progresso.


La cartella rdploader stessa può essere spostata o rinominata con l'opzione
rootDirectory in config/mct_resourcedatapackloader_mixin.cfg. Funziona anche un
percorso assoluto, ed è necessario un riavvio.

Metti un pack.png accanto a questo file per dare un'icona al pack.

Questo file viene scritto dalla mod e aggiornato ogni volta che cambia, quindi
tutto ciò che scrivi al suo interno viene sostituito al prossimo avvio del
gioco.

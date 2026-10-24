Resource Data Pack Loader
=========================

Cokoli dáš do této složky, nahradí to, co poskytuje mód nebo samotný
Minecraft. Platí to pro každý svět, ve hře pro jednoho hráče i na serverech,
a není co zapínat.


VÍCE NEŽ PŘEPISOVÁNÍ
--------------------

Balíčky zde mohou také z JSON souborů definovat nové bloky, předměty, biomy
a celé dimenze, určovat, co se kde generuje, zamykat dimenze, dokud hráč nemá
klíč nebo neporazí určitého moba, a vytvářet terén světa předem, takže nikdo
nikdy nečeká na chunk. HOWTO.md, které je přibaleno k módu, to všechno
popisuje.


JAK PŘIDAT SOUBOR
-----------------

Otevři jar módu, najdi soubor, který chceš změnit, a zkopíruj jeho cestu od
'assets' dál.

Pro nahrazení textury železné rudy je soubor uvnitř jaru Minecraftu tady:

    assets/minecraft/textures/blocks/iron_ore.png

takže tvoje verze patří sem:

    rdploader/assets/minecraft/textures/blocks/iron_ore.png

To je celé pravidlo. Cesta za 'assets' je vždy stejná jako cesta uvnitř jaru,
takže se nikdy nemusí nic přejmenovávat ani přesouvat.


POŘÁDEK V SOUBORECH
-------------------

Soubory můžeš místo toho seskupit do pojmenovaného balíčku jako zip:

    rdploader/MyTextures.zip        (s 'assets' na nejvyšší úrovni zipu)

Při zipování vyber obsah a zipuj ten, ne složku, která ho obklopuje. Zip,
jehož nejvyšší úroveň tvoří jediná složka obalující 'assets', se přeskočí
a log to oznámí.

Složka v rdploader není balíček a přeskočí se, což log také oznámí. Volné
soubory nech pod assets a balíček před vložením sem zazipuj.

Pokud stejný soubor existuje na dvou místech, pojmenovaný balíček vyhraje nad
volnými soubory. Log uvádí, z jakého balíčku každý soubor pochází, takže vždy
vidíš, který vyhrál.


PRIORITA BALÍČKŮ
----------------

Pokud dva pojmenované balíčky obsahují stejný soubor, určíš, který vyhraje,
předřazením RDPL a čísla před název zipu. RDPL0 se načítá první, vyšší čísla
později a vyhrává balíček načtený jako poslední:

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures.zip

Na velikosti písmen nezáleží a mezera, pomlčka nebo podtržítko za číslem je
volitelné. Předpona se z názvu balíčku odstraní v logu i v /rdpl list, takže
RDPL1 SeasonalTextures se tam zobrazí jako SeasonalTextures.


MOD API
-------

Mód může nést obsah RDPL ve vlastním jaru, ve složce pojmenované rdploader,
uspořádané přesně jako balíček:

    thatmod.jar
      mcmod.info
      rdploader/assets/thatmod/blocks/ruby_ore.json

Jsou to výchozí hodnoty, ne přepsání. Balíček módu se načítá pod všemi
balíčky v této složce, takže cokoli sem dáš, nad ním vyhraje, a mód smí
dodávat soubory jen pod namespace, který uvádí ve vlastním mcmod.info.
Cokoli jiného se ignoruje s varováním, aby žádný mód nemohl nepozorovaně
předefinovat obsah jiného módu nebo tvůj.

Každý mód, který takový obsah nese, se při prvním zjištění zapíše do
config/mods.json, takto:

    {
      "thatmod": {
        "enabled": true,
        "priority": -1
      }
    }

Nastav enabled na false, abys obsah toho módu vypnul. Nech priority na -1,
aby zůstal pod vším, nebo zadej číslo a zařadí se podle něj mezi očíslované
balíčky výše. Balíčky jsou v logu seřazeny od nejnižšího a balíček módu je
tam označen, takže se nenačte nic, co bys neviděl.


BALÍČKY ZDROJŮ
--------------

Ve výchozím stavu jsou soubory zde nad balíčky zdrojů, které hráč vybírá na
obrazovce voleb, takže je balíček zdrojů nemůže přepsat. Pro logo modpacku je
to správně, pro textury, které chceš nechat lidem přetvářet, špatně.

Přidej O nebo N za předponu RDPL a rozhodneš to pro každý balíček zvlášť:

    rdploader/RDPLO Branding          vždy vyhrává, balíčky zdrojů na něj
                                      nesáhnou
    rdploader/RDPLN BaseTextures      balíček zdrojů ho může přepsat
    rdploader/RDPL1O Seasonal         priorita a vždy vyhrává, obojí dohromady

Balíčky bez písmene se řídí volbou overrideResourcePacks v konfiguraci
a /rdpl list označí ty, které přepisují.

Balíčky bez předpony se načítají před všemi očíslovanými, v abecedním pořadí,
takže očíslovaný balíček vždy vyhraje nad neočíslovaným.

Chceš-li balíček vypnout bez smazání, přidej na konec jeho názvu .disabled:

    rdploader/RDPL1 SeasonalTextures.zip.disabled

Balíček se přeskočí a log to oznámí. Odstraň příponu a zapne se znovu.


CO MŮŽEŠ PŘEPSAT
----------------

Textury, modely, stavy bloků, jazykové soubory, zvuky, písma, hlášky na
úvodní obrazovce a vše ostatní, co mód drží ve své složce assets, třeba
příručky nebo návody.

Úspěchy a tabulky kořisti. Ty běží na straně serveru, takže fungují i na
dedikovaném serveru.

Šablony struktur, soubory .nbt pod assets/<modid>/structures. Struktura
uložená ve vlastní složce structures světa pořád vyhrává nad souborem zde
a už umístěná struktura zůstává načtená, dokud svět neopustíš.

Recepty, včetně nahrazení receptu z módu nebo přidání vlastního. Recepty se
načítají jen při spuštění hry, takže změna zde vyžaduje restart, ne
znovunačtení.

Funkce, soubory .mcfunction pod assets/<modid>/functions. Minecraft je čte
jen z vlastní složky data ve světě, takže když je dáš sem, fungují v každém
světě. Funkce uložená ve světě pořád vyhrává nad souborem zde.

Přejmenování v registru, aby svět uložený předtím, než mód přejmenoval jeden
ze svých bloků, ten blok zachoval a neztratil. Dej soubor do
assets/<modid>/registry_remap:

    {
      "registry": "minecraft:items",
      "mapping": { "oldmod:old_name": "newmod:new_name" }
    }

Registr je ten, do kterého položka patří, obvykle minecraft:items nebo
minecraft:blocks. Přejmenování se řetězí, takže mapování A na B a později
B na C pošle A na C.

Vlastnosti věcí, které už existují, bez zásahu do jejich souborů. Soubor
v assets/<yourpack>/overrides určuje svůj cíl cestou, takže
overrides/minecraft/stone.json mění minecraft:stone, vanilla i modovaný.
Bloky přijímají tvrdost, odolnost proti výbuchu, světlo, neprůhlednost pro
světlo, kluzkost, zvuk, nástroj a úroveň těžby a hořlavost. Předměty
přijímají velikost stacku, odolnost a předmět kontejneru a z každého
předmětu lze udělat jedlý, s hodnotami jídla a efekty. Efekty lektvaru lze
přepsat. Funguje to za běhu: vypni balíček, spusť /rdpl reload a každá
hodnota se vrátí na původní, bez restartu. Napiš id vlastnicího módu do
"requires", aby se soubor tiše přeskočil, když ten mód není nainstalován.

Kořist hráčů, věc, pro kterou hra nemá vůbec žádný název. Hráči pouštějí
svůj inventář a nic jiného, takže jim soubor v assets/<modid>/player_loot dá
vlastní tabulku kořisti:

    {
      "table": "mypack:entities/player",
      "mode": "add",
      "rollOnKeepInventory": false
    }

"add" pustí to, co tabulka vylosuje, spolu se vším, co nesli, "replace" to
pustí místo jejich inventáře a rollOnKeepInventory rozhoduje, zda se tabulka
vůbec losuje ve světě, kde se inventáře zachovávají.

Praporec je výjimka. Jedna definice registruje dva bloky, tvůj a druhý
jménem <name>_wall, a předmět dostane jen stojící, který mezi nimi vybírá při
pokládání. Jeho model sahá daleko za vlastní blok, až na 29.33 z 16 ve stoje
a 13 pod blok na zdi, a stojící stav bloku potřebuje formát Forge, aby se
otáčel po šestnáctinách. Úplné rozměry mají průvodci.

Dropy bloku mohou být náhodné a nemusí to být předměty. Položky v jeho
seznamu drops se rozhodují každá zvlášť, pokud jim nedáš váhu, a pak sdílejí
jeden hrnec a vypadne přesně jedna z nich. Položka, která místo bloku uvádí
entitu, pustí tuto entitu na místě, kde blok stál.

Texturu lze zapsat jako JSON místo nakreslení. Pojmenuj soubor podle PNG
s .json na konci, textures/blocks/panel.png.json, a zadej mu velikost jako
16x16 nebo 16x32, paletu po jednom znaku na barvu a řádky těchto znaků shora
dolů. Další takový soubor ji může rozšířit a uvést jen barvy, které mají být
jiné, takže jeden tvar lze přebarvit kolikrát chceš bez jediného obrázku.
To, co kreslí, se uchovává zde v pixelmap-cache a překreslí se, kdykoli se
změní mapa nebo její šablona.

Věci lze také odebrat. Soubor v recipe_removals maže recepty podle názvu,
namespace nebo výstupu a soubor v disabled vyřadí bloky a předměty ze hry bez
zrušení jejich registrace, takže světy si ponechají svá id a smazáním souboru
se vše vrátí. Vstřik kořisti přidá pool do tabulky kořisti, která už existuje,
místo aby ji nahradil, a block_drops doplní nebo nahradí, co pouští blok,
který nevlastníš. Recepty pro pec, doby hoření paliv, názvy ze slovníku rud,
karty kreativního režimu a zvukové události jsou také samostatné soubory.

Kovadlinu lze naučit novou práci. Soubor v anvils uvádí předmět, předmět,
který k němu patří v pravém slotu, a kouzla nebo výsledek, které kovadlina
nabízí pro uvedené úrovně. Vyjmutí může získat úspěch a předmět může zůstat
nepoužitelný, dokud tento úspěch nezískáš.

Soubor v tooltips přidává předmětům a blokům šedé řádky popisku a pod název
kouzla vloží popis.

Soubor v hardness nastaví dobu těžby a odolnost proti výbuchu pro skupinu
bloků a soubor v exposures definuje nebezpečí, třeba záření: působí na hráče
poblíž uvedených bloků, nesoucí uvedené předměty nebo v uvedených dimenzích,
v úrovních, z nichž každá aplikuje efekty a poškození, a lze se jím nakazit od
mobů a hráčů poblíž nebo padá s deštěm.

CraftTweaker a GroovyScript fungují stále úplně stejně. Běží po tomto módu,
takže cokoli tvoje skripty odeberou nebo změní, vyhraje nad souborem zde.

RDPL se hodí na nahrazení jednoho či dvou receptů a recepty pro vlastní obsah
patří do balíčku vedle něj. Pro plnou kontrolu nad recepty napříč modpackem
jsou lepší volbou CraftTweaker a GroovyScript. Soubor zde nahrazuje originál
úplně, takže ke změně jedné ingredience nebo vypuštění jedné položky kořisti
použij je.


PŘIDÁNÍ NOVÉHO OBSAHU
---------------------

Balíček může také přidávat vlastní bloky, předměty a tekutiny popsané jako
JSON. K tomu nemusíš psát ani sestavovat žádný mód.

Cesta souboru je jeho název. Blok v

    rdploader/MyPack/assets/mypack/blocks/copper_ore.json

se zaregistruje jako mypack:copper_ore. Nevyplňuje se žádné pole názvu, které
by šlo vynechat nebo zapsat špatně. Pokud skutečný mód tento název už
registruje, vyhraje mód a tvůj soubor se přeskočí.

Nejjednodušší blok je pár řádků:

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "copper_ore": { "meta": 0, "hardness": 3.0, "harvestLevel": 1 }
      }
    }

Model, stav bloku, texturu a záznam v jazykovém souboru dodáváš stejně jako
kterýkoli jiný soubor v této složce.

Každá z těchto věcí je složka pod assets/<yourpack>:

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

Bloky existují v těchto tvarech, nastavených polem "type":

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

a předměty v těchto:

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   rocket

Typ lektvaru se vždy objeví na vanilla lektvaru, vrhacím lektvaru,
setrvávajícím lektvaru a šípu s efektem, které jsou na kartách Vaření a Boj.
Karta je vlastnost předmětu, ne typu lektvaru, takže je nelze přesunout na
vlastní kartu. Předmět potion_bottle je místo toho tvůj vlastní kontejner:
bere creativeTab jako každý jiný předmět, uvádí v potionTypes typy lektvarů,
které pojmenuješ, a varný stojan ho přijme všude, kde funguje skleněná
lahvička.

Soubor villagers/<name>.json definuje povolání a kariéry, které nabízí.
Soubor trades/*.json přidává obchody do jakékoli kariéry, tvé vlastní i
některé z Minecraftu, a uvádí povolání, kariéru a úroveň, na které se obchod
objeví. Uveď kariéru, která neexistuje, a log vypíše ty, které existují.

Soubor entities/<name>.json vytvoří novou entitu z té, která už existuje.
Uvádí entitu, na které staví, a co je na ní jiné: její jméno, skin, kolik má
zdraví a poškození, jak rychle se pohybuje a jak vysoko skáče, jak velká se
kreslí, co nosí, co loví a co ignoruje, a zda se pořád řídí pravidly spawnu
entity, ze které vznikla. Je to samostatná entita s vlastním spawn vejcem
a tabulkou kořisti a ta, ze které vznikla, zůstane nedotčená. Pozemku vesnice
lze říct, aby místo vesničana ubytoval jednu z nich. Může také nést vlastní
úložiště, sloty na předměty, nádrž na tekutinu a zásobník energie, ke kterým
se dostanou roury a kabely a které hráč otevře tak, že se skrčí a klikne na
ni pravým tlačítkem, a raketu Galacticraftu lze stejným způsobem obměnit
a umístit na startovací rampu předmětem typu rocket.

Soubor villages/<name>.json přidává pozemek, který mohou vesnice stavět, buď
farmu, kterou popíšeš, nebo jednu z tvých šablon .nbt. Stejná nastavení
vybírají, které vanilla části se ještě objeví, jak daleko od sebe se vesnice
vysévají a ve kterých biomech jsou povoleny.

Soubor worldintro/<name>.json přehraje sérii stránek, když někdo vstoupí do
světa, než získá kontrolu: rolující text nad obrázkem, titulní kartu,
prezentaci, na přání s hudbou v pozadí. Slova jsou prosté soubory .txt pod
assets/<yourpack>/texts. Může se přehrát jednou pro každého hráče nebo při
každém připojení.

Soubor teams/<name>.json postaví na skóre hry stranu, ke které se moby,
hráči nebo cokoli, co se spawnuje v rohu světa, při příchodu připojí,
a soubor scoring/<name>.json je cíl, který těmto stranám počítá zabití
a smrti, ukončí kolo na skóre nebo po čase, ukáže pořadí v chatu nebo na
kartě a může resetovat mapu pro další kolo.

Soubor raids/<name>.json posílá vlny na vesnici, když do ní vejde hráč s
efektem znamení. Boss bar ukazuje, kolik nájezdníků zbývá, vesničané se
schovávají dovnitř, nájezdníci rozbíjejí dřevěné dveře a každý zvon ve
vesnici zazvoní, když vlna přijde.

Soubor cards/<name>.json ukáže na obrazovce kartu, když se něco stane,
třeba když hráč vstoupí do biomu, a soubor ve stejné složce pojmenovaný podle
jedné ze zpráv samotného módu změní, co tato zpráva říká.

CELÉ SVĚTY
----------

Balíček se neomezuje na jednotlivé věci. dimensions/<name>.json zaregistruje
dimenzi s vlastním terénem, biomy a oblohou. gates/<name>.json klade podmínku
na její dosažení, třeba držení nebo odevzdání předmětu. Blok typu portal pošle
toho, kdo vejde, a pamatuje si, kdo ho postavil.

Šablona světa může také tvarovat samotný Overworld a nastavit hladinu moře,
lávové oceány a šum terénu. To se použije při vytvoření světa a nikdy potom,
takže už existující svět zůstane, jaký byl.

worldtemplates/<name>.json shromáždí nastavení světa do jednoho souboru,
balíček tak může dodat celý tvar světa najednou místo žádosti o tucet změn
konfigurace. Každá skupina, kterou lze nastavit, odpovídá také kategorii
control v konfiguraci, která rozhoduje, zda rozhoduje balíček, rozhoduje
konfigurace, nebo je skupina úplně vypnutá a žádný balíček ji nezapne.

Dimenze také může určit, jak vypadá a chová se nad hlavou. Její mlha, odstín
světla, tlumení slunce a měsíce, vrstvy mraků a chvění žáru se kreslí na
tvé obrazovce a její počasí rozhoduje, zda prší, sněží nebo bouří, jak dlouho
trvají přeháňky a jakou barvu, částice, zvuk a úhel má déšť.

S nainstalovaným Galacticraftem postaví celestial/<name>.json vlastní
hvězdné soustavy, planety, měsíce, pásy asteroidů a vesmírné stanice balíčku
na hvězdnou mapu Galacticraftu a dimenze s blokem galacticraft se stane
místem, kam raketa letí.

worldgen je víc než ruda. Položka je tvar umístěný rozptylem: shluky, dlouhé
žíly, desky, geody, mísy, hroty, uzlíky, průduchy, povrchová výzdoba, celé
stromy, liány, pásy přes několik chunků nebo jedna z tvých vlastních šablon
.nbt, rozložené rovnoměrně, kolem výšky, fraktálně, podél terénu, na podlahách
nebo stropech jeskyní nebo pod vodou.


Soubor biomes/<name>.json definuje biom: jeho podnebí a barvy, bloky, z nichž
se skládá, co ho zdobí, co se v něm spawnuje a kde se generuje. Jeho číslo se
vybere za tebe a zapíše do každého světa při prvním načtení tohoto světa, takže
potom zůstane pevné, ať nainstaluješ cokoli dalšího. Nastav "id" jen tehdy,
když biom musí zachovat číslo, které už použilo něco jiného, například když
balíček nahrazuje mód, který se ruší. Přejmenování nebo smazání biomu, který
svět už obsahuje, ho ztratí, stejně jako přečíslování bloku, takže na
přejmenování použij registry_remap.

Zobrazené jméno vesničana je jazykový klíč entity.Villager.<career>, s názvem
kariéry přesně tak, jak jsi ho napsal, a ničím jiným. Tento prostor klíčů sdílí
Minecraft a každý další balíček, takže dej svůj namespace do názvu kariéry,
jako v rdpltest.prospector. Dotčené je jen jméno: vesničan ukládá svou kariéru
jako číslo, takže přejmenování změní, jak se existující vesničané jmenují,
a přeuspořádání seznamu kariér změní, jakou kariéru mají.

Typ lektvaru se pojmenuje podle svého souboru stejným způsobem a jeho
zobrazené jméno pochází z jazykového klíče potion.effect.<namespace>.<name>,
s splash_potion.effect., lingering_potion.effect. a tipped_arrow.effect. pro
další tři podoby.


SVĚTY RUBIC
-----------

Šablona světa může požádat o svět postavený z kostek místo z 256 bloků
vysokých sloupců a svět pak roste oběma směry: podlaha hluboko pod nulou,
strop daleko nad 255, s terénem, jeskyněmi a rudami skrz celek. Hrát v takovém
světě je běžné. Kopeš, stavíš, svítíš a cestuješ stejně jako jinde a vanilla
okno generování si v tomto vyšším světě drží svůj obvyklý tvar, takže módy,
které generují terén, přistávají tam, kde vždy přistávaly.

Co z toho balíček získá: vlastní výšku světa, nastavenou jednou při vytvoření
světa; hluboký svět pod vanilla oknem, se šumovými jeskyněmi, aquifery
a pásmovými žilami rud v kameni, který pojmenuješ; oblasti jeskyní, odpověď
balíčků na jeskynní biomy, malované trojrozměrně skrz podzemí s vlastními
podlahami, stropy, hladinou vody, moby a strukturami; dimenze naskládané na
sebe, takže pád ze dna jednoho světa tě odnese do dalšího pod ním a výstup
nahoru tě vrátí zpět; a každá vynechaná dimenze, která si ve stejném uložení
ponechá svůj obyčejný svět.

Pod tím je svět uložen jako kostky 16 krát 16 krát 16 ve vlastních souborech
regionů vedle vanilla souborů, generované, načítané a ukládané samostatně,
se světelným enginem napsaným pro tento tvar. Vanilla předpoklad, že svět je
vysoký 256 bloků, je opraven všude, kde je nosný, od hranic stavby a rovin
smrti po hledání cest, portály, majáky, mapy a renderer. Generátory jiných
módů dál vidí normálně vypadající okno 256 bloků, proto jejich terén
funguje.

HOWTO.md obsahuje nastavení, výšky, které svět může mít, a módy, vedle nichž
to nepoběží.


VAROVÁNÍ K META
---------------

Každá varianta má číslo meta a toto číslo ukládá soubor světa. Přečíslování
varianty, kterou lidé už mají ve světě, promění jejich bloky v něco jiného.
Nové varianty přidávej na konec a starou nikdy nepřečísluj.

Blok drží 16 variant, protože tolik umožňují čtyři bity metadat. Desky
dostanou 8, protože jeden bit říká nahoře nebo dole, a schody, žebříky,
pochodně a plodiny dostanou 1, protože zbytek zabírá orientace nebo stáří.
Předměty nejsou tak těsné a smějí čísla vynechávat.


KDE TO KONČÍ
------------

Toto popisuje, čím věc je, ne co dělá v čase. Cokoli, co potřebuje tile
entity, GUI nebo kód běžící každý tick, pořád potřebuje skutečný mód, se
dvěma výjimkami: blok typu container drží inventář s vlastní obrazovkou
a varianta entity může nést úložiště. Stroj je mimo dosah; ruda, plot, jídlo
nebo tekutina ne.


BALÍČKY Z JINÝCH VERZÍ
----------------------

Balíček vytvořený pro řadu 1.20.1, 1.21.1 nebo 26.x tohoto módu se načte i
zde. Zip se jednou převede uvnitř sebe sama do složky versions/1.12.2
a moderní soubory zůstanou, jaké byly, takže stejný zip dál funguje na každé
verzi.


JAK UVIDÍŠ ZMĚNY
----------------

Stiskni F3+T, abys znovu načetl textury, modely, jazykové soubory, úspěchy
a tabulky kořisti. Na serveru napiš /reload pro totéž. Recepty jsou výjimka,
jak je uvedeno výše: načítají se jen při spuštění, takže změna receptu
vyžaduje restart.

Pokud přidáš nový soubor nebo jeden smažeš, použij místo toho /rdpl reload.
Úprava souboru, který tam už byl, vyžaduje jen F3+T.

/rdpl reload textures načte znovu jen textury, což je ve velkém balíčku
mnohem rychlejší než F3+T. models, languages, sounds a shaders fungují
stejně. Vynech název a prohledá se znovu složka a načte se znovu vše.

/rdpl list ukáže každý načtený balíček a co v něm je. Klikni na balíček a
uvidíš ho.

/rdpl which minecraft:textures/blocks/stone.png ukáže, který balíček soubor
dodává a které balíčky jsou pod ním zastíněné.

/rdpl config unused vypíše soubory voleb v rdploader/config, které už žádný
nainstalovaný balíček nedefinuje, a /rdpl config prune je smaže. /rdpl
pixelmap ukáže, co z pixelové mapy vzešlo, /rdpl biome list a here informují
o biomech a /rdpl team a /rdpl round slouží pro strany a kola, které balíček
definuje.

Fungují bez práv operátora, protože čtou jen soubory na tvém vlastním
počítači. Na dedikovaném serveru znovu načte kopii serveru /rdplserver reload.


KDYŽ NĚCO NEFUNGUJE
-------------------

Nejdřív se podívej do logu. Úspěchy, tabulky kořisti, recepty, funkce
a struktury se logují s balíčkem, ze kterého pocházejí, a cokoli špatného se
loguje jako varování s důvodem.

U textur a dalších zdrojů vypíše /rdpl unused každý soubor ve tvých
balíčcích, na který se zatím nic nezeptalo, což obvykle znamená překlep v
cestě. Spusť to po dokončení načítání hry a pamatuj, že některé soubory se
načtou, až když jsou potřeba, třeba jazyky jiné než ten, ve kterém hraješ.

Velká písmena se počítají. Pokud se tvůj soubor jmenuje Stone.png a hra se
zeptala na stone.png, stejně se načte, ale varování ti řekne, abys ho
přejmenoval. Přejmenuj ho, protože všude mimo tento mód se soubor nenajde
vůbec. Jazykové soubory lidi zaskočí nejčastěji: jmenují se en_us.lang, ne
en_US.lang.

Zkontroluj, že tvé soubory leží ve složce 'assets'. Zip bez ní se přeskočí
a log to oznámí.



ÚSPĚCHY A RECEPTY
-----------------

Pokud tvoje skripty odeberou recept, každý úspěch, který ho odemykal, dál
funguje a nerozbije se. Jen už nemá recept, který by dal, a log ho jednou
uvede.

Pokud jsi ten recept nahradil novým a chceš, aby úspěch odemykal nový, dej
novému receptu ve skriptu název:

    recipes.addShaped("rail", <minecraft:rail> * 16, [[...]]);

Tím se zaregistruje jako crafttweaker:rail. Pak sem dej soubor úspěchu
ukazující na tento název a úspěch zase funguje od začátku do konce.

Bez názvu dostane něco jako crafttweaker:ct_shaped-1834729103, hash samotného
receptu. Ten se změní ve chvíli, kdy recept upravíš, a může se posunout, když
se před něj přidá další recept, takže není bezpečné na něj úspěch odkazovat.


Složku rdploader samotnou lze přesunout nebo přejmenovat volbou rootDirectory
v config/mct_resourcedatapackloader_mixin.cfg. Funguje i absolutní cesta a je
potřeba restart.

Dej pack.png vedle tohoto souboru, abys balíčku dal ikonu.

Tento soubor zapisuje mód a aktualizuje ho vždy, když se změní, takže cokoli
do něj napíšeš, se při dalším spuštění hry nahradí.

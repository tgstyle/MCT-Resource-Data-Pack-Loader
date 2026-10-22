Resource Data Pack Loader
=========================

Cokoli vložíš do této složky, nahradí to, co poskytuje mód nebo samotný
Minecraft. Platí to pro každý svět, ve hře pro jednoho hráče i na serverech, a
nic se nemusí zapínat.


VÍC NEŽ PŘEPISOVÁNÍ
-------------------

Balíčky zde mohou také z JSON souborů definovat nové bloky, předměty, biomy a
celé dimenze, rozhodovat, co se generuje a kde, zamykat dimenze, dokud hráč
nemá klíč nebo neporazí určitého moba, a vytvářet svět předem, takže nikdo
nikdy nečeká na chunk. Všechno to popisuje HOWTO.md, které je přibaleno k modu.


JAK PŘIDAT SOUBOR
-----------------

Otevři jar módu, najdi soubor, který chceš změnit, a zkopíruj jeho cestu od
'assets' nebo 'data' dál.

Chceš-li nahradit texturu železné rudy, soubor uvnitř jaru Minecraftu je:

    assets/minecraft/textures/block/iron_ore.png

takže tvoje verze patří sem:

    rdploader/assets/minecraft/textures/block/iron_ore.png

Tabulka kořisti leží místo toho pod data a postupuje se stejně:

    data/minecraft/loot_tables/blocks/iron_ore.json
    rdploader/data/minecraft/loot_tables/blocks/iron_ore.json

To je celé pravidlo. Cesta za 'assets' nebo 'data' je vždy stejná jako cesta
uvnitř jaru, takže nikdy není potřeba nic přejmenovávat ani přesouvat.


UDRŽOVÁNÍ POŘÁDKU
-----------------

Soubory můžeš místo toho seskupit do pojmenovaného balíčku jako zip:

    rdploader/MyTextures.zip        (s 'assets' nebo 'data' v kořeni zipu)

Při zipování vyber obsah a zipuj ten, ne složku, která ho obsahuje. Zip, jehož
nejvyšší úroveň tvoří jedna složka obalující 'assets' nebo 'data', se
přeskočí a log to uvede.

Složka v rdploader není balíček a přeskočí se, což log také uvede. Volné
soubory měj pod assets nebo data a balíček zabal do zipu, než ho sem vložíš.

Pokud stejný soubor existuje na dvou místech, pojmenovaný balíček vyhraje nad
volnými soubory a /rdpl which ti řekne, který z nich zvítězil.


PRIORITA BALÍČKŮ
----------------

Pokud dva pojmenované balíčky obsahují stejný soubor, řídíš, který vyhraje,
tím, že před název zipu přidáš RDPL a číslo. RDPL0 se načte první, vyšší čísla
později a vyhrává balíček načtený jako poslední:

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures.zip

Na velikosti písmen nezáleží a mezera, pomlčka či podtržítko za číslem jsou
volitelné. Předpona se z názvu balíčku v logu i v /rdpl list odstraní, takže
RDPL1 SeasonalTextures se zobrazí jako SeasonalTextures.


MOD API
-------

Mód může nést obsah RDPL přímo ve svém jaru, ve složce pojmenované rdploader,
která je uspořádaná přesně jako balíček:

    thatmod.jar
      META-INF/mods.toml
      rdploader/assets/thatmod/textures/block/ruby_ore.png

Jsou to výchozí hodnoty, ne přepsání. Balíček módu se načítá pod všemi balíčky
v této složce, takže cokoli sem vložíš, má přednost, a mód smí dodávat soubory
jen pod namespace, který deklaruje ve svém vlastním mods.toml. Cokoli jiného
se ignoruje s varováním, takže žádný mód nemůže tiše předefinovat obsah jiného
módu ani tvůj.

Každý mód, který takový balíček obsahuje, se při prvním spatření zapíše do
souboru config/mods.json takto:

    {
      "thatmod": {
        "enabled": true,
        "priority": -1
      }
    }

Nastav enabled na false, chceš-li obsah toho módu vypnout. Nech priority na -1,
aby zůstal pod vším ostatním, nebo uveď číslo a zaujme své místo v pořadí výše,
vedle číslovaných balíčků. V logu jsou balíčky uvedeny od nejnižšího a balíček
samotného módu je tam označen, takže se nic nenačte bez tvého vědomí.


BALÍČKY ZDROJŮ
--------------

Ve výchozím stavu jsou zde uložené soubory nad balíčky zdrojů, které hráč vybere
na obrazovce možností, takže je balíček zdrojů nemůže přepsat. To je správně
třeba u loga modpacku a špatně u textur, které by lidé měli mít možnost
přeskinovat.

Přidej O nebo N za předponu RDPL a rozhodneš u každého balíčku zvlášť:

    rdploader/RDPLO Branding          vždy vyhrává, balíčky zdrojů ho nepřepíšou
    rdploader/RDPLN BaseTextures      balíček zdrojů ho může přepsat
    rdploader/RDPL1O Seasonal         priorita a vždy vyhrává, obojí dohromady

Balíčky bez písmene se řídí volbou overrideResourcePacks v konfiguraci a
/rdpl list označí ty, které přepisují.

Stejné pravidlo platí pro datové balíčky. Balíček označený N leží pod datovými
balíčky, které svět nese ve své vlastní složce datapacks, a ten označený O leží
nad nimi.

Balíčky bez předpony se načítají před všemi číslovanými, v abecedním pořadí,
takže číslovaný balíček vždy vyhraje nad nečíslovaným.

Chceš-li balíček vypnout, aniž bys ho smazal, přidej na konec jeho názvu
.disabled:

    rdploader/RDPL1 SeasonalTextures.zip.disabled

Balíček se přeskočí a log to uvede. Odstraň příponu a znovu se zapne.


CO MŮŽEŠ ZMĚNIT
---------------

Textury, modely, stavy bloků, jazykové soubory, zvuky, písma, texty splashe a
cokoli dalšího, co mód drží ve své složce assets, třeba příručky či manuály.

Pokroky, tabulky kořisti, recepty, tagy, funkce, šablony struktur a cokoli
dalšího, co mód drží ve své složce data. Ty jsou na straně serveru, takže
fungují i na dedikovaném serveru, a změna se projeví po /reload.

Věci lze také odebírat. Soubor v recipe_removals maže recepty podle názvu,
namespace nebo výstupu a soubor v disabled vyřadí bloky a předměty ze hry bez
zrušení jejich registrace, takže světy si ponechají svá id a smazání souboru
vše vrátí. Injektáž kořisti přidá pool do existující tabulky kořisti místo
jejího nahrazení a block_drops přidává k tomu, co padá z cizího bloku, nebo to
nahrazuje. Recepty do pece, doby hoření paliv, karty kreativního režimu a
zvukové události jsou také samostatné soubory.

Kovadlinu lze naučit novou práci. Soubor v anvils pojmenuje předmět, předmět,
který k němu patří v pravém slotu, a kouzla či výsledek, které kovadlina nabídne
pro uvedené úrovně. Vyjmutí může udělit pokrok a předmět může zůstat
nepoužitelný, dokud tento pokrok nezískáš.

Soubor v tooltips přidává předmětům a blokům šedé řádky popisku a pod název
kouzla vloží popis.

Soubor hardness nastavuje dobu těžby a odolnost proti výbuchu pro skupinu bloků
a soubor exposures definuje nebezpečí, například radiaci: působí na hráče poblíž
určených bloků, nesoucí určené předměty nebo v určených dimenzích, v úrovních,
z nichž každá uplatní efekty a poškození, a lze se jí nakazit od okolních mobů
a hráčů nebo může padat s deštěm.


PŘIDÁVÁNÍ NOVÉHO OBSAHU
-----------------------

Balíček může přidávat i vlastní bloky, předměty a tekutiny popsané jako JSON.
K tomu nemusíš psát ani sestavovat žádný mód.

Definice leží pod data, v jedné složce pro každý druh věcí. Každý klíč uvnitř
"variants" je název, takže soubor v

    rdploader/data/mypack/blocks/ores.json

obsahující variantu pojmenovanou ruby_ore zaregistruje mypack:ruby_ore. Název
souboru věci jen seskupuje. Pokud už skutečný mód tento název registruje,
vyhraje mód a tvoje varianta se přeskočí.

Nejjednodušší blok má jen pár řádků:

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "ruby_ore": { "hardness": 3.0, "harvestLevel": 1 }
      }
    }

Model, stav bloku, texturu a jazykový záznam stále dodáváš pod assets, stejně
jako jakýkoli jiný soubor v této složce.

Každé z těchto je složka pod data/<yourpack>:

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

Bloky se dodávají v těchto tvarech, určených polem "type":

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

a předměty v těchto:

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   container

Typ lektvaru se pojmenuje jazykovým klíčem
item.minecraft.potion.effect.<baseName>, přičemž pro ostatní podoby se místo
potion použije splash_potion, lingering_potion nebo tipped_arrow. Předmět
potion_bottle je tvoje vlastní nádoba na ně: má creativeTab jako každý jiný
předmět a drží typy lektvarů, které uvedeš v potionTypes.

Soubor villagers/<name>.json definuje profesi. Soubor trades/*.json přidává
obchody libovolné profesi, ať tvé, nebo z Minecraftu, a uvádí profesi a úroveň,
na které se obchod objeví.

Soubor entities/<name>.json vytvoří novou entitu z entity, která už existuje.
Uvede entitu, na které staví, a čím se liší: názvem, vzhledem, zdravím a
poškozením, pohybem, bojem a tím, co dropuje. Je to samostatná entita s vlastním
spawn vejcem a tabulkou kořisti a ta, ze které vznikla, zůstane nedotčena. Může
také nést vlastní úložiště, sloty na předměty, nádrž na tekutinu a zásobník
energie, ke kterým sahají roury a kabely a které hráč otevře plížením a
kliknutím pravým tlačítkem na ni.

Soubor villages/<name>.json přidává pozemek, který město či vesnice může
postavit z jedné z tvých šablon .nbt, a soubor raids/<name>.json posílá na
vesnici vlny nepřátel, když do ní hráč přinese znamení.

Soubor cards/<name>.json zobrazí na obrazovce kartu, když se něco stane, třeba
když hráč vstoupí do biomu, a soubor ve stejné složce pojmenovaný po některé ze
zpráv módu změní, co tato zpráva říká.

Soubor worldintro/<name>.json přehraje sérii stránek, když někdo vstoupí do
světa, než převezme kontrolu. Slova jsou prosté soubory .txt pod
assets/<yourpack>/texts. Může se přehrát jednou pro každého hráče nebo při
každém připojení.

Soubor teams/<name>.json postaví na herní výsledkové tabuli stranu a určí, co se
k ní připojí, a soubor scoring/<name>.json je cíl, který těmto stranám přiděluje
body a rozhoduje, jak zápas skončí.


CELÉ SVĚTY
----------

Balíček se neomezuje na jednotlivé věci. dimensions/<name>.json registruje
dimenzi s vlastním terénem, biomy a oblohou. gates/<name>.json klade podmínku na
cestu do ní, například držení nebo spotřebování předmětu. Blok typu portal pošle
toho, kdo vejde, do jiné dimenze, a portalframes umožní hráči postavit a zapálit
vlastní rám.

worldtemplates/<name>.json shrne nastavení světa do jednoho souboru, takže
balíček může dodat celý tvar světa najednou místo žádosti o tucet úprav
konfigurace. Může tvarovat i samotný Overworld, například jeho hladinu moře a
zda jsou jeho oceány z lávy.

Dimenze také může určit, jak vypadá a chová se nad hlavou. Její mlha, odstín
světla, ztlumení slunce a měsíce, vrstvy mraků a chvění horka se kreslí na tvé
obrazovce a její počasí rozhoduje, zda prší, sněží nebo bouří, jak dlouho
přeháňky trvají a jaká je barva, částice, zvuk a úhel deště.

worldgen je víc než ruda. Položka umisťuje tvar, od malé skvrny tvého bloku po
jednu z tvých vlastních šablon .nbt, a rozhoduje, jak často, jak vysoko a v
jakých biomech se objeví.

Soubor biomes/<name>.json definuje biom: jeho podnebí a barvy, bloky, z nichž je
tvořen, co ho zdobí, co se v něm spawnuje a kde se generuje.


KDE TO KONČÍ
------------

Toto popisuje, čím věc je, ne co dělá v čase. Cokoli, co potřebuje blokový
objekt, obrazovku nebo kód běžící každý tick, stále vyžaduje skutečný mód, se
dvěma výjimkami: blok typu container drží inventář s vlastní obrazovkou a
varianta entity může nést úložiště. Stroj je mimo dosah, ruda, plot, jídlo nebo
tekutina ne.


BALÍČKY Z JINÝCH VERZÍ
----------------------

Balíček vytvořený pro řadu 1.12.2 tohoto módu se zde načte tak, jak je. Zip se
jednou převede, uvnitř sebe sama, do složky versions pro tuto verzi, a soubory
1.12.2 zůstanou, jak byly, takže stejný zip funguje dál na každé verzi.


JAK UVIDÍŠ SVÉ ZMĚNY
--------------------

Stiskni F3+T pro znovunačtení textur, modelů, jazykových souborů a všeho
ostatního pod assets. Na serveru nebo pro cokoli pod data zadej /reload.

Pokud soubor přidáš nebo smažeš, použij místo toho /rdpl reload. Úprava souboru,
který tam už byl, vyžaduje jen F3+T nebo /reload.

/rdpl list zobrazí každý načtený balíček a co obsahuje. Najetím na balíček ho
zobrazíš.

/rdpl which minecraft:textures/block/stone.png ukáže, který balíček soubor
poskytuje a které balíčky pod ním zůstaly zastíněné.

/rdpl config unused vypíše soubory voleb v rdploader/config, které už nedefinuje
žádný nainstalovaný balíček, a /rdpl config prune je smaže. /rdpl pixelmap
ukáže, co z pixelové mapy vzešlo, a /rdpl biome list a here ti řeknou o biomech.

Tyto příkazy fungují i bez práv operátora, protože čtou jen soubory na tvém
vlastním počítači. Na dedikovaném serveru /rdplserver reload znovu prohledá
kopii serveru a /rdplserver list, which a unused odpovídají za něj.


KDYŽ NĚCO NEFUNGUJE
-------------------

Nejdřív zkontroluj log. logs/rdpl.log vypisuje každý načtený balíček i každý
přeskočený, s důvodem, a cokoli špatného se zapíše jako varování s vysvětlením.

/rdpl unused vypíše každý soubor ve tvých balíčcích, na který se zatím nic
nezeptalo, což obvykle znamená překlep v cestě. Spusť ho po dokončení načítání
hry a pamatuj, že některé soubory se načtou, až když jsou potřeba, například
jazyky jiné než ten, ve kterém hraješ.

Velká písmena mají význam. Pokud je tvůj soubor Stone.png a hra chtěla
stone.png, stále se načte, ale varování ti řekne, abys ho přejmenoval. Opravdu
ho přejmenuj, protože všude mimo tento mód by se soubor vůbec nenašel.
Jazykové soubory lidi zaskočí nejčastěji: jmenují se en_us.json, ne en_US.json.

Zkontroluj, že tvoje soubory leží ve složce 'assets' nebo 'data'. Zip bez
jedné z nich se přeskočí a log to uvede.


POKROKY A RECEPTY
-----------------

Recept, který skript přidá nebo nahradí, je znám pod názvem, který mu skript
dá. Aby ho pokrok odemykal, vlož sem soubor pokroku, který tento recept
pojmenuje, a pokrok opět funguje od začátku do konce.

Dej takovému receptu ve skriptu pevný název. Název vytvořený za tebe se může
změnit ve chvíli, kdy recept upravíš, takže na něj není bezpečné pokrok
napojit.


Složku rdploader lze přesunout nebo přejmenovat volbou rootDirectory
v config/resourcedatapackloader-common.toml. Funguje i absolutní cesta a je
potřeba restart.

Vedle tohoto souboru můžeš umístit pack.png, který balíčku dá ikonu.

Tento soubor zapisuje mód a aktualizuje ho při každé změně, takže cokoli do
něj napíšeš, se při dalším spuštění hry nahradí.

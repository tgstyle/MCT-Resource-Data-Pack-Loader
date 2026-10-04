Resource Data Pack Loader
=========================

Tout ce que vous placez dans ce dossier remplace ce qu'un mod ou Minecraft
lui-même fournit. Cela s'applique à tous les mondes, en solo comme sur les
serveurs, et il n'y a rien à activer.


PLUS QUE DES REMPLACEMENTS
--------------------------

Les packs d'ici peuvent aussi définir de nouveaux blocs, objets, biomes et des
dimensions entières à partir de fichiers JSON, décider ce qui se génère et où,
verrouiller des dimensions derrière une clé ou un mob à tuer, et générer à
l'avance les terres d'un monde pour que personne n'attende jamais un chunk.
HOWTO.md, livré avec le mod, couvre tout cela.


COMMENT AJOUTER UN FICHIER
--------------------------

Ouvrez le jar du mod, trouvez le fichier à modifier et copiez son chemin à
partir de 'assets' ou 'data'.

Pour remplacer la texture du minerai de fer, le fichier dans le jar de
Minecraft est :

    assets/minecraft/textures/block/iron_ore.png

votre version va donc ici :

    rdploader/assets/minecraft/textures/block/iron_ore.png

Une table de butin se trouve plutôt sous data, et fonctionne de la même façon :

    data/minecraft/loot_tables/blocks/iron_ore.json
    rdploader/data/minecraft/loot_tables/blocks/iron_ore.json

C'est toute la règle. Le chemin après 'assets' ou 'data' est toujours le même
que dans le jar, donc rien n'a jamais besoin d'être renommé ni déplacé.


GARDER LES CHOSES EN ORDRE
--------------------------

Vous pouvez regrouper des fichiers dans un pack nommé, sous forme de zip :

    rdploader/MyTextures.zip        (avec 'assets' ou 'data' au premier niveau du zip)

En zippant, sélectionnez le contenu et compressez-le, pas le dossier qui le
contient. Un zip dont le premier niveau est un unique dossier enveloppant
'assets' ou 'data' est ignoré, et le journal le signale.

Un dossier dans rdploader n'est pas un pack et est ignoré, et le journal le
signale. Gardez les fichiers isolés sous assets ou data, et compressez un pack
en zip avant de le placer ici.

Si le même fichier existe à deux endroits, un pack nommé l'emporte sur les
fichiers isolés, et /rdpl which vous dit lequel a gagné.


PRIORITÉ DES PACKS
------------------

Si deux packs nommés contiennent le même fichier, contrôlez lequel l'emporte en
faisant précéder le nom du zip de RDPL et d'un nombre. RDPL0 se charge en
premier, les nombres plus grands se chargent ensuite, et le pack chargé en
dernier l'emporte :

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures.zip

Majuscules ou minuscules, les deux marchent, et une espace, un tiret ou un
tiret bas après le nombre est facultatif. Le préfixe est retiré du nom du pack
dans le journal et dans /rdpl list, donc RDPL1 SeasonalTextures apparaît comme
SeasonalTextures.


API DES MODS
------------

Un mod peut embarquer du contenu RDPL dans son propre jar, dans un dossier
nommé rdploader organisé exactement comme un pack :

    thatmod.jar
      META-INF/mods.toml
      rdploader/assets/thatmod/textures/block/ruby_ore.png

Ce sont des valeurs par défaut, pas des remplacements. Le pack d'un mod se
charge sous tous les packs de ce dossier, donc ce que vous mettez ici l'emporte,
et un mod ne peut fournir des fichiers que sous un espace de noms qu'il déclare
dans son propre mods.toml. Tout le reste est ignoré avec un
avertissement, si bien qu'aucun mod ne peut redéfinir en douce le contenu d'un
autre ni le vôtre.

Chaque mod qui en livre un est listé dans config/mods.json la première fois
qu'il est repéré :

    {
      "thatmod": {
        "enabled": true,
        "priority": -1
      }
    }

Mettez enabled à false pour désactiver le contenu de ce mod. Laissez priority à
-1 pour le garder sous tout le reste, ou donnez un nombre et il prend sa place
dans l'ordre ci-dessus, à côté des packs numérotés. Les packs sont listés du
plus bas au plus haut dans le journal et celui d'un mod y est marqué, donc rien
ne se charge sans que vous puissiez le voir.


PACKS DE RESSOURCES
-------------------

Par défaut, les fichiers d'ici se placent au-dessus des packs de ressources que
le joueur choisit dans l'écran des options, donc un pack de ressources ne peut
pas les remplacer. C'est bien pour un logo de modpack, et mauvais pour des
textures que vous voulez voir rhabillées par les joueurs.

Ajoutez O ou N après le préfixe RDPL pour décider pack par pack :

    rdploader/RDPLO Branding          l'emporte toujours, les packs de ressources n'y touchent pas
    rdploader/RDPLN BaseTextures      un pack de ressources peut le remplacer
    rdploader/RDPL1O Seasonal         priorité et l'emporte toujours, les deux ensemble

Les packs sans lettre suivent l'option overrideResourcePacks de la
configuration, et /rdpl list marque ceux qui remplacent.

La même règle vaut pour les packs de données. Un pack marqué N se place sous
les packs de données qu'un monde porte dans son propre dossier datapacks, et un
pack marqué O se place au-dessus.

Les packs sans préfixe se chargent avant tous les packs numérotés, dans l'ordre
alphabétique, donc un pack numéroté l'emporte toujours sur un pack sans numéro.

Pour désactiver un pack sans le supprimer, ajoutez .disabled à la fin de son
nom :

    rdploader/RDPL1 SeasonalTextures.zip.disabled

Le pack est ignoré et le journal le signale. Retirez le suffixe pour le
réactiver.


CE QUE VOUS POUVEZ CHANGER
--------------------------

Textures, modèles, états de blocs, fichiers de langue, sons, polices, textes de
splash, et tout ce qu'un mod garde dans son dossier assets, comme les livres de
guide ou les manuels.

Progrès, tables de butin, recettes, tags, fonctions, modèles de structures et
tout ce qu'un mod garde dans son dossier data. Cela relève du serveur, donc cela
marche aussi sur un serveur dédié, et un changement prend effet avec /reload.

On peut aussi retirer des choses. Un fichier dans recipe_removals supprime des
recettes par nom, espace de noms ou résultat, et un fichier dans disabled retire
des blocs et des objets du jeu sans les désenregistrer, si bien que les mondes
gardent leurs ids et que supprimer le fichier rend tout. Une injection de butin
ajoute une réserve à une table de butin qui existe déjà au lieu de la remplacer,
et block_drops ajoute ou remplace ce que lâche un bloc qui n'est pas à vous.
Les recettes de fourneau, les durées de combustion, les onglets créatifs et les
événements sonores ont aussi leurs propres fichiers.

On peut apprendre un nouveau travail à une enclume. Un fichier dans anvils
nomme un objet, l'objet qui l'accompagne dans l'emplacement de droite, et les
enchantements ou le résultat que l'enclume propose pour les niveaux indiqués.
Le retirer peut faire gagner un progrès, et l'objet peut rester inutilisable
tant que ce progrès n'est pas gagné.

Un fichier hardness fixe le temps de minage et la résistance aux explosions d'un
groupe de blocs, et un fichier exposures définit un danger comme la
radioactivité : il touche les joueurs près de blocs nommés, portant des objets
nommés ou dans des dimensions nommées, par niveaux qui appliquent chacun des
effets et des dégâts, et il peut s'attraper auprès de mobs et de joueurs proches
ou tomber avec la pluie.


AJOUTER DU NOUVEAU CONTENU
--------------------------

Un pack peut aussi ajouter ses propres blocs, objets et fluides, décrits en
JSON. Vous n'avez pas besoin d'écrire ni de compiler un mod pour cela.

Les définitions se trouvent sous data, un dossier pour chaque genre de chose.
Chaque clé dans "variants" est un nom, donc un fichier à

    rdploader/data/mypack/blocks/ores.json

contenant une variante appelée ruby_ore enregistre mypack:ruby_ore. Le nom du
fichier ne sert qu'à regrouper. Si un vrai mod enregistre déjà ce nom, le mod
l'emporte et votre variante est ignorée.

Le bloc le plus simple tient en quelques lignes :

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "ruby_ore": { "hardness": 3.0, "harvestLevel": 1 }
      }
    }

Vous fournissez toujours le modèle, l'état de bloc, la texture et l'entrée de
langue sous assets, de la même façon que tout autre fichier de ce dossier.

Chacun de ceux-ci est un dossier sous data/<yourpack> :

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

Les blocs existent sous ces formes, fixées par le champ "type" :

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

et les objets sous celles-ci :

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   container

Un type de potion est nommé par la clé de langue
item.minecraft.potion.effect.<baseName>, avec splash_potion, lingering_potion
ou tipped_arrow à la place de potion pour les autres formes. Un objet
potion_bottle est votre propre conteneur pour elles : il prend un creativeTab
comme tout autre objet et contient les types de potion que vous nommez dans
potionTypes.

Un fichier villagers/<name>.json définit une profession. Un fichier
trades/*.json ajoute des échanges à n'importe quelle profession, la vôtre ou
une de Minecraft, en nommant la profession et le niveau où l'échange apparaît.

Un fichier entities/<name>.json fabrique une nouvelle entité à partir d'une qui
existe déjà. Il nomme l'entité sur laquelle il s'appuie et ce qui change : son
nom, son apparence, sa vie et ses dégâts, sa façon de se déplacer, de se
battre et ce qu'elle lâche. C'est une entité à part entière, avec son propre
œuf d'apparition et sa propre table de butin, et celle dont elle est issue
reste intacte. Elle peut aussi porter un stockage à elle, des emplacements
d'objets, un réservoir de fluide et un tampon d'énergie que les tuyaux et les
câbles atteignent et qu'un joueur ouvre en s'accroupissant et en faisant un
clic droit dessus.

Un fichier villages/<name>.json ajoute une parcelle qu'une ville ou un village
peut bâtir à partir d'un de vos modèles .nbt, et un fichier raids/<name>.json
envoie des vagues sur un village quand un joueur y apporte un présage.

Un fichier cards/<name>.json affiche une carte à l'écran quand quelque chose se
produit, comme un joueur qui entre dans un biome, et un fichier du même dossier
nommé d'après un des messages propres au mod change ce que ce message dit.

Un fichier worldintro/<name>.json joue une suite de pages quand quelqu'un entre
dans le monde, avant de prendre le contrôle. Les mots sont de simples fichiers
.txt sous assets/<yourpack>/texts. Il peut se jouer une fois par joueur ou à
chaque connexion.

Un fichier teams/<name>.json met un camp sur le tableau des scores du jeu et dit
ce qui le rejoint, et un fichier scoring/<name>.json est un objectif qui donne
des points à ces camps et décide comment une partie se termine.


MONDES ENTIERS
--------------

Un pack ne se limite pas aux choses isolées. dimensions/<name>.json enregistre
une dimension avec son propre terrain, ses biomes et son ciel. gates/<name>.json
pose une condition pour en atteindre une, comme tenir ou dépenser un objet. Un
bloc de type portal envoie dans une autre dimension quiconque le franchit, et
portalframes laisse un joueur bâtir et allumer un cadre de son cru.

worldtemplates/<name>.json rassemble les réglages d'un monde dans un seul
fichier, pour qu'un pack puisse livrer d'un coup la forme d'un monde entier au
lieu de réclamer une douzaine de modifications de configuration. Il peut aussi
façonner la surface elle-même, comme le niveau de la mer et le fait que les
océans soient de lave.

Une dimension peut aussi régler son apparence et son comportement au-dessus de
votre tête. Son brouillard, sa teinte de lumière, l'atténuation du soleil et de
la lune, ses couches de nuages et sa chaleur ondulante sont dessinés sur votre
propre écran, et sa météo décide s'il pleut, neige ou orage, combien de temps
durent les averses, ainsi que la couleur, les particules, le son et l'angle de la
pluie.

worldgen, c'est plus que du minerai. Une entrée place une forme, d'un petit
amas de votre bloc jusqu'à un de vos propres modèles .nbt, et décide à quelle
fréquence, à quelle hauteur et dans quels biomes elle apparaît.

Un fichier biomes/<name>.json définit un biome : son climat et ses couleurs, les
blocs dont il est fait, ce qui le décore, ce qui y apparaît et où il se génère.


OÙ CELA S'ARRÊTE
----------------

Cela décrit ce qu'une chose est, pas ce qu'elle fait au fil du temps. Tout ce
qui demande une entité de bloc, un écran ou du code exécuté à chaque tick
nécessite toujours un vrai mod, avec deux exceptions : un bloc de type
container contient un inventaire avec un écran à lui, et une variante d'entité
peut porter un stockage. Une machine est hors de portée ; un minerai, une
clôture, un aliment ou un fluide non.


PACKS D'AUTRES VERSIONS
-----------------------

Un pack conçu pour la ligne 1.12.2 de ce mod se charge ici tel quel. Un zip est
converti une seule fois, à l'intérieur de lui-même, dans un dossier versions
pour cette version, et les fichiers 1.12.2 restent tels quels, si bien que le
même zip continue de fonctionner sur toutes les versions.


VOIR VOS CHANGEMENTS
--------------------

Appuyez sur F3+T pour recharger les textures, les modèles, les fichiers de
langue et tout le reste sous assets. Sur un serveur, ou pour tout ce qui est
sous data, tapez /reload.

Si vous ajoutez un fichier ou en supprimez un, utilisez plutôt /rdpl reload.
Modifier un fichier qui existait déjà n'a besoin que de F3+T ou /reload.

/rdpl list montre chaque pack chargé et ce qu'il contient. Survolez un pack pour
le voir.

/rdpl which minecraft:textures/block/stone.png montre quel pack sert un fichier
et quels packs sont masqués en dessous.

/rdpl config unused liste les fichiers d'options de rdploader/config qu'aucun
pack installé ne définit plus, et /rdpl config prune les supprime. /rdpl
pixelmap montre ce qu'une carte de pixels a donné, et /rdpl biome list et here
vous renseignent sur les biomes.

Elles marchent sans être opérateur, parce qu'elles ne lisent que des fichiers
sur votre propre ordinateur. Sur un serveur dédié, /rdplserver reload relit la
copie du serveur, et /rdplserver list, which et unused répondent pour elle.


SI QUELQUE CHOSE NE MARCHE PAS
------------------------------

Regardez d'abord le journal. logs/rdpl.log liste chaque pack chargé et chacun de
ceux qui ont été ignorés, avec la raison, et tout ce qui va mal est consigné
comme avertissement disant pourquoi.

/rdpl unused liste tout fichier de vos packs que rien n'a encore demandé, ce qui
signifie généralement une faute de frappe dans le chemin. Lancez-le une fois le
jeu entièrement chargé, et gardez en tête que certains fichiers ne se chargent
que quand on en a besoin, comme les langues autres que celle dans laquelle vous
jouez.

Les majuscules comptent. Si votre fichier est Stone.png et que le jeu a demandé
stone.png, il se charge quand même, mais un avertissement vous dit de le
renommer. Faites-le, car en dehors de ce mod le fichier ne sera pas trouvé du
tout. Les fichiers de langue sont ce qui piège le plus souvent : ce sont des
en_us.json, pas des en_US.json.

Vérifiez que vos fichiers se trouvent dans un dossier 'assets' ou 'data'. Un zip
sans aucun des deux est ignoré, et le journal le signale.


PROGRÈS ET RECETTES
-------------------

Une recette qu'un script ajoute ou remplace est connue sous le nom que le
script lui donne. Pour qu'un progrès la débloque, déposez ici un fichier de
progrès qui nomme cette recette, et le progrès fonctionne de nouveau de bout en
bout.

Donnez à une telle recette un nom fixe dans le script. Un nom généré peut
changer dès que vous modifiez la recette, donc il n'est pas sûr d'y pointer un
progrès.


Le dossier rdploader lui-même peut être déplacé ou renommé avec l'option
rootDirectory de config/resourcedatapackloader-common.toml. Un chemin absolu
marche aussi, et un redémarrage est nécessaire.

Placez un pack.png à côté de ce fichier pour donner une icône au pack.

Ce fichier est écrit par le mod et mis à jour dès qu'il change, donc tout ce
que vous y tapez est remplacé au prochain démarrage du jeu.

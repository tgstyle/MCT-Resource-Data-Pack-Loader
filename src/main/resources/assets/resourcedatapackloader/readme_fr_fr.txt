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
partir de 'assets'.

Pour remplacer la texture du minerai de fer, le fichier dans le jar de
Minecraft est :

    assets/minecraft/textures/blocks/iron_ore.png

votre version va donc ici :

    rdploader/assets/minecraft/textures/blocks/iron_ore.png

C'est toute la règle. Le chemin après 'assets' est toujours le même que dans le
jar, donc rien n'a jamais besoin d'être renommé ni déplacé.


GARDER LES CHOSES EN ORDRE
--------------------------

Vous pouvez regrouper des fichiers dans un pack nommé, sous forme de zip :

    rdploader/MyTextures.zip        (avec 'assets' au premier niveau du zip)

En zippant, sélectionnez le contenu et compressez-le, pas le dossier qui le
contient. Un zip dont le premier niveau est un unique dossier enveloppant
'assets' est ignoré, et le journal le signale.

Un dossier dans rdploader n'est pas un pack et est ignoré, et le journal le
signale. Gardez les fichiers isolés sous assets, et compressez un pack en zip
avant de le placer ici.

Si le même fichier existe à deux endroits, un pack nommé l'emporte sur les
fichiers isolés. Le journal nomme le pack d'où vient chaque fichier, vous
pouvez donc toujours voir lequel a gagné.


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
      mcmod.info
      rdploader/assets/thatmod/blocks/ruby_ore.json

Ce sont des valeurs par défaut, pas des remplacements. Le pack d'un mod se
charge sous tous les packs de ce dossier, donc ce que vous mettez ici l'emporte,
et un mod ne peut fournir des fichiers que sous un espace de noms qu'il déclare
dans son propre mcmod.info. Tout le reste est ignoré avec un avertissement, si
bien qu'aucun mod ne peut redéfinir en douce le contenu d'un autre ni le vôtre.

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

Progrès et tables de butin. Cela relève du serveur, donc cela marche aussi sur
un serveur dédié.

Modèles de structures, les fichiers .nbt sous assets/<modid>/structures. Une
structure enregistrée dans le dossier structures du monde lui-même l'emporte
toujours sur un fichier d'ici, et une structure déjà placée reste chargée
jusqu'à ce que vous quittiez le monde.

Recettes, y compris remplacer la recette d'un mod ou en ajouter une à vous. Les
recettes ne se chargent qu'au démarrage du jeu, donc un changement ici demande
un redémarrage plutôt qu'un rechargement.

Fonctions, les fichiers .mcfunction sous assets/<modid>/functions. Minecraft ne
les lit que dans le dossier data du monde lui-même, donc les placer ici les fait
marcher dans tous les mondes. Une fonction enregistrée dans le monde l'emporte
toujours sur un fichier d'ici.

Renommages de registre, pour qu'un monde enregistré avant qu'un mod ne renomme
l'un de ses blocs garde ce bloc au lieu de le perdre. Placez un fichier dans
assets/<modid>/registry_remap :

    {
      "registry": "minecraft:items",
      "mapping": { "oldmod:old_name": "newmod:new_name" }
    }

Le registre est celui auquel l'entrée appartient, généralement minecraft:items
ou minecraft:blocks. Les renommages s'enchaînent : associer A à B puis plus tard
B à C envoie A vers C.

Propriétés de choses qui existent déjà, sans toucher à leurs fichiers. Un
fichier dans assets/<yourpack>/overrides désigne sa cible par son chemin, donc
overrides/minecraft/stone.json change minecraft:stone, de vanilla comme d'un
mod. Les blocs acceptent la dureté, la résistance aux explosions, la lumière,
l'opacité à la lumière, le glissant, le son, l'outil et le niveau de récolte, et
l'inflammabilité. Les objets acceptent la taille de pile, la durabilité et un
objet conteneur, et n'importe quel objet peut devenir comestible, avec des
valeurs de nourriture et des effets. Les effets d'une potion peuvent être
réécrits. Tout cela est en direct : désactivez le pack et lancez /rdpl reload, et
chaque valeur revient à ce qu'elle était, sans redémarrage. Mettez l'id du mod
propriétaire dans "requires" pour que le fichier soit ignoré discrètement quand
ce mod n'est pas installé.

Butin des joueurs, une chose pour laquelle le jeu n'a aucun nom. Les joueurs
lâchent leur inventaire et rien d'autre, donc un fichier dans
assets/<modid>/player_loot leur donne une table de butin à eux :

    {
      "table": "mypack:entities/player",
      "mode": "add",
      "rollOnKeepInventory": false
    }

"add" lâche ce que la table tire en plus de tout ce qu'ils portaient, "replace"
le lâche à la place de leur inventaire, et rollOnKeepInventory décide si la
table est seulement tirée dans un monde où les inventaires sont conservés.

Une bannière est l'exception. Une définition enregistre deux blocs, le vôtre et
un second nommé <name>_wall, et seul celui qui est debout reçoit un objet, qui
choisit entre les deux à la pose. Son modèle dépasse largement son propre bloc,
jusqu'à 29,33 sur 16 debout et 13 sous le bloc sur un mur, et l'état de bloc
debout a besoin du format de Forge pour tourner en seizièmes. Les guides ont les
mesures complètes.

Le butin d'un bloc peut être aléatoire, et n'a pas besoin d'être des objets. Les
entrées de sa liste drops sont décidées chacune de leur côté, sauf si vous leur
donnez un poids, auquel cas elles partagent une seule urne et exactement une
d'entre elles sort. Une entrée qui nomme une entité au lieu d'un bloc lâche
cette entité là où se trouvait le bloc.

Une texture peut être écrite en JSON au lieu d'être dessinée. Nommez le fichier
d'après le PNG avec .json à la fin, textures/blocks/panel.png.json, et donnez-lui
une taille comme 16x16 ou 16x32, une palette d'un caractère pour une couleur, et
des lignes de ces caractères de haut en bas. Un autre fichier de ce genre peut
l'étendre en ne nommant que les couleurs voulues différentes, si bien qu'une
même forme peut être recolorée autant de fois que vous voulez sans un seul
fichier image. Ce qu'ils dessinent est conservé dans pixelmap-cache ici et
redessiné chaque fois qu'une carte ou son modèle change.

On peut aussi retirer des choses. Un fichier dans recipe_removals supprime des
recettes par nom, espace de noms ou résultat, et un fichier dans disabled retire
des blocs et des objets du jeu sans les désenregistrer, si bien que les mondes
gardent leurs ids et que supprimer le fichier rend tout. Une injection de butin
ajoute une réserve à une table de butin qui existe déjà au lieu de la remplacer,
et block_drops ajoute ou remplace ce que lâche un bloc qui n'est pas à vous.
Les recettes de fourneau, les durées de combustion, les noms du dictionnaire des
minerais, les onglets créatifs et les événements sonores ont aussi leurs propres
fichiers.

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

CraftTweaker et GroovyScript marchent toujours exactement comme avant. Ils
s'exécutent après ce mod, donc tout ce que vos scripts retirent ou changent
l'emporte sur un fichier d'ici.

RDPL est bon pour remplacer une ou deux recettes, et les recettes de votre
propre contenu relèvent du pack qui l'accompagne. Pour un contrôle complet des
recettes à l'échelle d'un modpack, CraftTweaker et GroovyScript sont de
meilleures options. Un fichier d'ici remplace entièrement l'original, donc pour
changer un ingrédient ou retirer une entrée de butin, utilisez ceux-là.


AJOUTER DU NOUVEAU CONTENU
--------------------------

Un pack peut aussi ajouter ses propres blocs, objets et fluides, décrits en
JSON. Vous n'avez pas besoin d'écrire ni de compiler un mod pour cela.

Le chemin du fichier est son nom. Un bloc à

    rdploader/MyPack/assets/mypack/blocks/copper_ore.json

s'enregistre comme mypack:copper_ore. Il n'y a pas de champ de nom à remplir ni
à mal écrire. Si un vrai mod enregistre déjà ce nom, le mod l'emporte et votre
fichier est ignoré.

Le bloc le plus simple tient en quelques lignes :

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "copper_ore": { "meta": 0, "hardness": 3.0, "harvestLevel": 1 }
      }
    }

Vous fournissez toujours le modèle, l'état de bloc, la texture et l'entrée de
langue de la même façon que tout autre fichier de ce dossier.

Chacun de ceux-ci est un dossier sous assets/<yourpack> :

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
    celestial        raids

Les blocs existent sous ces formes, fixées par le champ "type" :

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

et les objets sous celles-ci :

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   rocket

Un type de potion apparaît toujours sur la potion de vanilla, la potion
jetable, la potion persistante et la flèche à effet, qui se trouvent dans les
onglets Alchimie et Combat. L'onglet est une propriété de l'objet, pas du type
de potion, donc il n'y a aucun moyen de les déplacer dans un onglet à vous. Un
objet potion_bottle est en revanche votre propre conteneur : il prend un
creativeTab comme tout autre objet, liste les types de potion que vous nommez
dans potionTypes, et le pupitre d'alchimie l'accepte partout où une fiole en
verre convient.

Un fichier villagers/<name>.json définit une profession et les carrières qu'elle
propose. Un fichier trades/*.json ajoute des échanges à n'importe quelle
carrière, la vôtre ou une de Minecraft, en nommant la profession, la carrière et
le niveau où l'échange apparaît. Nommez une carrière qui n'existe pas et le
journal liste celles qui existent.

Un fichier entities/<name>.json fabrique une nouvelle entité à partir d'une qui
existe déjà. Il nomme l'entité sur laquelle il s'appuie et ce qui change : son
nom, sa peau, sa vie et ses dégâts, sa vitesse et la hauteur de son saut, la
taille à laquelle elle est dessinée, ce qu'elle porte, ce qu'elle chasse et ce
qu'elle ignore, et si elle obéit toujours aux règles d'apparition de l'entité
dont elle est issue. C'est une entité à part entière, avec son propre œuf
d'apparition et sa propre table de butin, et celle dont elle est issue reste
intacte. On peut ordonner à une parcelle de village d'en loger une à la place
d'un villageois. Elle peut aussi porter un stockage à elle, des emplacements
d'objets, un réservoir de fluide et un tampon d'énergie que les tuyaux et les
câbles atteignent et qu'un joueur ouvre en s'accroupissant et en faisant un
clic droit dessus, et une fusée de Galacticraft peut varier de la même façon et
être posée sur une rampe de lancement par un objet de type rocket.

Un fichier villages/<name>.json ajoute une parcelle que les villages peuvent
bâtir, soit une ferme que vous décrivez, soit un de vos modèles .nbt. Les mêmes
réglages choisissent quelles pièces de vanilla apparaissent encore, à quelle
distance les villages sont semés les uns des autres et dans quels biomes ils
sont permis.

Un fichier worldintro/<name>.json joue une suite de pages quand quelqu'un entre
dans le monde, avant de prendre le contrôle : du texte qui défile sur une image,
une carte de titre, un diaporama, avec de la musique derrière si vous voulez. Les
mots sont de simples fichiers .txt sous assets/<yourpack>/texts. Il peut se
jouer une fois par joueur ou à chaque connexion.

Un fichier teams/<name>.json met sur le tableau des scores du jeu un camp que
rejoignent à leur arrivée les mobs, les joueurs ou tout ce qui apparaît dans un
coin du monde, et un fichier scoring/<name>.json est un objectif qui attribue
les victimes et les morts à ces camps, termine une manche sur un score ou un
chronomètre, affiche le classement en chat ou sous forme de carte, et peut
réinitialiser la carte pour la manche suivante.

Un fichier raids/<name>.json envoie des vagues sur un village quand un joueur
portant l'effet omen y entre. Une barre de boss montre combien de pillards
restent, les villageois courent se mettre à l'abri, les pillards défoncent les
portes en bois, et chaque cloche du village sonne à l'arrivée d'une vague.

Un fichier cards/<name>.json affiche une carte à l'écran quand quelque chose se
produit, comme un joueur qui entre dans un biome, et un fichier du même dossier
nommé d'après un des messages propres au mod change ce que ce message dit.


MONDES ENTIERS
--------------

Un pack ne se limite pas aux choses isolées. dimensions/<name>.json enregistre
une dimension avec son propre terrain, ses biomes et son ciel. gates/<name>.json
pose une condition pour en atteindre une, comme tenir ou dépenser un objet. Un
bloc de type portal envoie quiconque y entre, et retient qui l'a bâti.

Un modèle de monde peut aussi façonner la surface elle-même, en fixant le
niveau de la mer, les océans de lave et le bruit du terrain. Cela s'applique à la
création d'un monde et jamais ensuite, donc un monde qui existe déjà reste tel
qu'il était.

worldtemplates/<name>.json rassemble les réglages d'un monde dans un seul
fichier, pour qu'un pack puisse livrer d'un coup la forme d'un monde entier au
lieu de réclamer une douzaine de modifications de configuration. Chaque groupe
qu'il peut régler répond aussi à la catégorie control de la configuration, qui
décide si c'est le pack qui décide, la configuration, ou si le groupe est
entièrement coupé et qu'aucun pack ne peut l'activer.

Une dimension peut aussi régler son apparence et son comportement au-dessus de
votre tête. Son brouillard, sa teinte de lumière, l'atténuation du soleil et de
la lune, ses couches de nuages et sa chaleur ondulante sont dessinés sur votre
propre écran, et sa météo décide s'il pleut, neige ou orage, combien de temps
durent les averses, ainsi que la couleur, les particules, le son et l'angle de la
pluie.

Avec Galacticraft installé, celestial/<name>.json place les systèmes stellaires,
planètes, lunes, ceintures d'astéroïdes et stations spatiales d'un pack sur la
carte stellaire de Galacticraft, et une dimension dotée d'un bloc galacticraft
devient un lieu où une fusée peut se rendre.

worldgen, c'est plus que du minerai. Une entrée est une forme placée par une
répartition : amas, longues veines, plaques, géodes, cuvettes, aiguilles,
nodules, évents, décor de surface, arbres entiers, lianes, ceintures qui
s'étendent sur plusieurs chunks, ou un de vos propres modèles .nbt, répartis
uniformément, autour d'une hauteur, de façon fractale, le long du terrain, au
sol ou au plafond des grottes, ou sous l'eau.

Un fichier biomes/<name>.json définit un biome : son climat et ses couleurs, les
blocs dont il est fait, ce qui le décore, ce qui y apparaît et où il se génère.
Son numéro est choisi pour vous et écrit dans chaque monde la première fois que
ce monde se charge, donc il reste fixe ensuite, quoi que vous installiez
d'autre. Réglez "id" seulement quand un biome doit garder un numéro que quelque
chose d'autre utilisait déjà, par exemple quand un pack remplace un mod en voie
de retrait. Renommer ou supprimer un biome qu'un monde contient déjà le perd,
comme renuméroter un bloc, donc utilisez registry_remap pour un renommage.

Le nom affiché d'un villageois est la clé de langue entity.Villager.<career>,
avec le nom de la carrière exactement tel que vous l'avez écrit et rien d'autre.
Cet espace de clés est partagé avec Minecraft et tous les autres packs, donc
mettez votre espace de noms dans le nom de la carrière, comme dans
rdpltest.prospector. Seul le nom est touché : un villageois mémorise sa carrière
sous forme de nombre, donc en renommer une change la façon dont les villageois
existants sont appelés, et réordonner la liste des carrières change la carrière
qu'ils ont.

Un type de potion est nommé de la même façon d'après son fichier, et son nom
affiché vient de la clé de langue potion.effect.<namespace>.<name>, avec
splash_potion.effect., lingering_potion.effect. et tipped_arrow.effect. pour les
trois autres formes.


MONDES RUBIC
------------

Un modèle de monde peut demander un monde bâti en cubes plutôt qu'en colonnes
de 256 blocs, et le monde s'étend alors dans les deux sens : un plancher très
en dessous de zéro, un plafond très au-dessus de 255, avec terrain, grottes et
minerais partout. Y jouer est ordinaire. Vous creusez, bâtissez, éclairez et
voyagez de la même façon, et la fenêtre de génération de vanilla garde sa forme
habituelle à l'intérieur du monde plus haut, si bien que les mods qui génèrent
du terrain le placent là où ils l'ont toujours fait.

Ce qu'un pack en retire : une hauteur de monde à lui, fixée une fois à la
création du monde ; un monde profond sous la fenêtre de vanilla, avec des
grottes de bruit, des aquifères et des veines de minerai en bandes dans une
pierre que vous nommez ; des régions de grottes, la réponse des packs aux
biomes de grottes, peintes dans le sous-sol en trois dimensions avec leurs
propres sols, plafonds, niveau d'eau, mobs et structures ; des dimensions
empilées les unes sur les autres, si bien que tomber par le fond d'un monde
vous mène dans le suivant en dessous et en sortir par le haut vous ramène ; et
toute dimension omise, qui garde son monde ordinaire dans la même sauvegarde.

En dessous, un monde est stocké en cubes de 16 sur 16 sur 16 dans ses propres
fichiers de région à côté de ceux de vanilla, générés, chargés et enregistrés
séparément, avec un moteur de lumière écrit pour cette forme. L'hypothèse de
vanilla selon laquelle un monde fait 256 blocs de haut est corrigée partout où
elle porte une charge, des limites de construction et des plans de mort jusqu'à
la recherche de chemin, aux portails, aux balises, aux cartes et au moteur de
rendu. Les générateurs des autres mods voient toujours une fenêtre d'aspect
normal de 256 blocs, et c'est pourquoi leur terrain fonctionne.

HOWTO.md contient les réglages, les hauteurs qu'un monde peut prendre et les
mods à côté desquels cela ne tournera pas.


UN AVERTISSEMENT SUR META
-------------------------

Chaque variante a un numéro meta, et ce numéro est ce que le fichier du monde
enregistre. Renuméroter une variante que les gens ont déjà dans un monde
transforme leurs blocs en autre chose. Ajoutez les nouvelles variantes à la fin
et ne renumérotez jamais une ancienne.

Un bloc contient 16 variantes, parce que c'est ce que quatre bits de métadonnées
permettent. Les dalles en ont 8, puisqu'un bit dit haut ou bas, et les escaliers,
échelles, torches et cultures en ont 1, parce que l'orientation ou l'âge
utilisent le reste. Les objets sont moins contraints et peuvent sauter des
numéros.


OÙ CELA S'ARRÊTE
----------------

Cela décrit ce qu'une chose est, pas ce qu'elle fait au fil du temps. Tout ce
qui demande une tile entity, une GUI ou du code exécuté à chaque tick nécessite
toujours un vrai mod, avec deux exceptions : un bloc de type container contient
un inventaire avec un écran à lui, et une variante d'entité peut porter un
stockage. Une machine est hors de portée ; un minerai, une clôture, un aliment
ou un fluide non.


PACKS D'AUTRES VERSIONS
-----------------------

Un pack conçu pour la ligne 1.20.1, 1.21.1 ou 26.x de ce mod se charge ici
aussi. Un zip est converti une seule fois, à l'intérieur de lui-même, dans un
dossier versions/1.12.2, et les fichiers modernes restent tels quels, si bien
que le même zip continue de fonctionner sur toutes les versions.


VOIR VOS CHANGEMENTS
--------------------

Appuyez sur F3+T pour recharger les textures, les modèles, les fichiers de
langue, les progrès et les tables de butin. Sur un serveur, tapez /reload pour
la même chose. Les recettes font exception, comme plus haut : elles ne se
chargent qu'au démarrage, donc un changement de recette demande un redémarrage.

Si vous ajoutez un fichier ou en supprimez un, utilisez plutôt /rdpl reload.
Modifier un fichier qui existait déjà n'a besoin que de F3+T.

/rdpl reload textures ne recharge que les textures, ce qui est bien plus rapide
que F3+T dans un gros pack. models, languages, sounds et shaders marchent de la
même façon. Laissez le nom de côté pour relire le dossier et tout recharger.

/rdpl list montre chaque pack chargé et ce qu'il contient. Cliquez sur un pack
pour le voir.

/rdpl which minecraft:textures/blocks/stone.png montre quel pack sert un fichier
et quels packs sont masqués en dessous.

/rdpl config unused liste les fichiers d'options de rdploader/config qu'aucun
pack installé ne définit plus, et /rdpl config prune les supprime. /rdpl
pixelmap montre ce qu'une carte de pixels a donné, /rdpl biome list et here
vous renseignent sur les biomes, et /rdpl team et /rdpl round servent pour les
camps et les manches qu'un pack définit.

Elles marchent sans être opérateur, parce qu'elles ne lisent que des fichiers
sur votre propre ordinateur. Sur un serveur dédié, /rdplserver reload relit la
copie du serveur.


SI QUELQUE CHOSE NE MARCHE PAS
------------------------------

Regardez d'abord le journal. Les progrès, tables de butin, recettes, fonctions
et structures y sont consignés avec le pack d'où ils viennent, et tout ce qui va
mal est consigné comme avertissement disant pourquoi.

Pour les textures et les autres ressources, /rdpl unused liste tout fichier de
vos packs que rien n'a encore demandé, ce qui signifie généralement une faute de
frappe dans le chemin. Lancez-le une fois le jeu entièrement chargé, et gardez
en tête que certains fichiers ne se chargent que quand on en a besoin, comme les
langues autres que celle dans laquelle vous jouez.

Les majuscules comptent. Si votre fichier est Stone.png et que le jeu a demandé
stone.png, il se charge quand même, mais un avertissement vous dit de le
renommer. Faites-le, car en dehors de ce mod le fichier ne sera pas trouvé du
tout. Les fichiers de langue sont ce qui piège le plus souvent : ce sont des
en_us.lang, pas des en_US.lang.

Vérifiez que vos fichiers se trouvent dans un dossier 'assets'. Un zip sans lui
est ignoré, et le journal le signale.


PROGRÈS ET RECETTES
-------------------

Si vos scripts retirent une recette, tout progrès qui la débloquait continue de
fonctionner au lieu de se casser. Il n'a simplement plus de recette à vous
donner, et le journal le nomme une fois.

Si vous avez remplacé cette recette par une nouvelle et voulez que le progrès
débloque la nouvelle, donnez un nom à la nouvelle recette dans votre script :

    recipes.addShaped("rail", <minecraft:rail> * 16, [[...]]);

Cela l'enregistre comme crafttweaker:rail. Déposez ensuite ici un fichier de
progrès qui pointe vers ce nom, et le progrès fonctionne de nouveau de bout en
bout.

Sans nom, elle s'appelle quelque chose comme crafttweaker:ct_shaped-1834729103,
un hachage de la recette elle-même. Il change dès que vous modifiez la recette,
et peut se décaler si une autre recette est ajoutée avant elle, donc il n'est
pas sûr d'y pointer un progrès.

Le dossier rdploader lui-même peut être déplacé ou renommé avec l'option
rootDirectory de config/mct_resourcedatapackloader_mixin.cfg. Un chemin absolu
marche aussi, et un redémarrage est nécessaire.

Placez un pack.png à côté de ce fichier pour donner une icône au pack.

Ce fichier est écrit par le mod et mis à jour dès qu'il change, donc tout ce
que vous y tapez est remplacé au prochain démarrage du jeu.

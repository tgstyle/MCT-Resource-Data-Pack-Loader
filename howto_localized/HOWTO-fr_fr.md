# Resource Data Pack Loader

**Un seul dossier qui remplace tout ce que Minecraft ou un mod fournit, définit du nouveau contenu à partir de JSON et contrôle ce qui se génère, dans tous les mondes, côté client comme côté serveur, sans rien à activer pour les joueurs.**

Un exemple fonctionnel. Déposez-le tel quel dans `rdploader` et regardez comment chaque fichier est écrit.

- [RDPLExamplePack.zip](../example/RDPLExamplePack.zip) utilise presque tous les types de fichiers que le loader lit : blocs, objets, un fluide, un onglet créatif, des biomes, un modèle de monde, une dimension derrière un portail, du worldgen, une potion et son alchimie, un villageois et ses échanges, des recettes, du butin, des surcharges d'éléments vanilla, un son, un progrès et une fonction. Son readme indique quoi vérifier en jeu.

Ce guide s'adresse aux versions 1.20.1 et 1.21.1. Elles lisent les mêmes packs ; les quelques endroits où les deux diffèrent sont signalés par **1.20.1** et **1.21.1**.

---

## Sommaire

**Premiers pas**
- [Ce que c'est](#ce-que-cest)
- [Où vont les fichiers](#où-vont-les-fichiers)
- [Lire les tableaux](#lire-les-tableaux)
- [La règle unique](#la-règle-unique)
- [Organiser les packs](#organiser-les-packs)
- [Packs de ressources : qui l'emporte](#packs-de-ressources--qui-lemporte)

**Fonctionnement des packs**
- [Fonctionnement des définitions](#fonctionnement-des-définitions)
- [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger)
- [Packs côté serveur](#packs-côté-serveur)
- [Renommages du registre](#renommages-du-registre)
- [API du mod](#api-du-mod)
- [Packs écrits pour 1.12.2](#packs-écrits-pour-1122)

**Blocs et objets**
- [Blocs](#blocs)
- [Conteneurs](#conteneurs)
- [Cloches](#cloches)
- [Modèles, états de bloc et textures](#modèles-états-de-bloc-et-textures)
- [Faire en sorte que vanilla traite correctement votre bloc](#faire-en-sorte-que-vanilla-traite-correctement-votre-bloc)
- [Objets](#objets)
- [Fluides](#fluides)
- [Matériaux, onglets, sons, tags](#matériaux-onglets-sons-tags)
- [Surcharges de propriétés](#surcharges-de-propriétés)
- [Groupes de dureté](#groupes-de-dureté)

**Artisanat, butin et commerce**
- [Blocs et objets désactivés](#blocs-et-objets-désactivés)
- [Recettes de fourneau et combustibles](#recettes-de-fourneau-et-combustibles)
- [Potions, types de potions et alchimie](#potions-types-de-potions-et-alchimie)
- [Travail à l'enclume](#travail-à-lenclume)
- [Butins des blocs](#butins-des-blocs)
- [Butin des joueurs](#butin-des-joueurs)
- [Villageois et échanges](#villageois-et-échanges)

**Créatures et dangers**
- [Variantes d'entités](#variantes-dentités)
- [Expositions](#expositions)

**Le monde**
- [Modèles de monde](#modèles-de-monde)
- [Règles de jeu](#règles-de-jeu)
- [Biomes](#biomes)
- [Dimensions](#dimensions)
- [Portails et passages](#portails-et-passages)
- [Le monde profond](#le-monde-profond)
- [Régions de grottes](#régions-de-grottes)

**Générer le monde**
- [Entrées de worldgen](#entrées-de-worldgen)
- [Formes](#formes)
- [Propagations](#propagations)
- [Cartes de structures](#cartes-de-structures)
- [Parcelles de village](#parcelles-de-village)
- [Cartes de plan des villes](#cartes-de-plan-des-villes)
- [Retrogen](#retrogen)
- [Prégénération](#prégénération)

**Modes de jeu**
- [Introduction au monde](#introduction-au-monde)
- [Équipes](#équipes)
- [Score](#score)
- [Raids](#raids)
- [Cartes](#cartes)
- [Dés et paquets](#dés-et-paquets)

**Contrôle**
- [La couche de contrôle](#la-couche-de-contrôle)
- [Ce que fait chaque groupe](#ce-que-fait-chaque-groupe)

**Autres mods**
- [Intégration de Blast Plaster](#intégration-de-blast-plaster)

**Référence**
- [Listes de valeurs](#listes-de-valeurs)
- [Liste des dossiers](#liste-des-dossiers)
- [Commandes](#commandes)
- [Bon à savoir](#bon-à-savoir)
- [Quand quelque chose ne fonctionne pas](#quand-quelque-chose-ne-fonctionne-pas)
- [Bonus : ajustements vanilla](#bonus--ajustements-vanilla)
- [Clés non reprises](#clés-non-reprises)

---

# Premiers pas

## Ce que c'est

*premiers pas*

Resource Data Pack Loader (RDPL) lit un seul dossier, `rdploader`, et accomplit trois tâches :

- **Surcharges.** Un fichier placé dans le dossier remplace celui que le jeu ou un mod aurait chargé. Pas d'interrupteur, pas de réglage par monde, rien à activer pour les joueurs.
- **Nouveau contenu.** Des définitions JSON enregistrent des blocs, des objets, des fluides, des biomes, des dimensions, des potions et des villageois. Pas de Java, pas de jar.
- **Contrôle.** Bloquer la génération des minerais, des biomes, des structures ou des recettes, aplatir la bedrock, régler les taux d'apparition, vider l'Overworld, définir les valeurs par défaut du monde.

## Où vont les fichiers

*premiers pas*

Un pack a deux racines, les mêmes que celles d'un pack vanilla. `assets/` contient ce que le client affiche et fait entendre : modèles, états de bloc, textures, fichiers de langue, sons et textes de l'introduction. `data/` contient tout le reste : toutes les définitions que ce mod lit, ainsi que les fichiers de données vanilla qu'un pack remplace. Chaque chemin de ce guide est écrit à partir du namespace, donc `<namespace>/blocks/*.json` correspond à `data/mypack/blocks/ruby_ore.json` sur le disque pour un pack dont le namespace est `mypack`, et `<namespace>/models/` correspond à `assets/mypack/models/`. Chaque section rappelle son propre chemin sous son titre.

Sous `data/` :

| Chemin                                       | Ce qu'il contient                                                                                                                         |
| -------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| `<namespace>/blocks/*.json`                  | Définitions de blocs. [Blocs](#blocs)                                                                                                     |
| `<namespace>/items/*.json`                   | Définitions d'objets. [Objets](#objets)                                                                                                   |
| `<namespace>/fluids/*.json`                  | Fluides, avec un bloc et un seau. [Fluides](#fluides)                                                                                     |
| `<namespace>/materials/*.json`               | Matériaux d'outils et d'armures. [Matériaux, onglets, sons, tags](#matériaux-onglets-sons-tags)                                           |
| `<namespace>/tabs/*.json`                    | Onglets créatifs. [Matériaux, onglets, sons, tags](#matériaux-onglets-sons-tags)                                                          |
| `<namespace>/sounds/*.json`                  | Événements sonores. [Matériaux, onglets, sons, tags](#matériaux-onglets-sons-tags)                                                        |
| `<namespace>/biomes/*.json`                  | Définitions de biomes. [Biomes](#biomes)                                                                                                  |
| `<namespace>/worldgen/*.json`                | Ce qui se génère, et où. [Entrées de worldgen](#entrées-de-worldgen)                                                                      |
| `<namespace>/caveregions/*.json`             | Régions nommées peintes sur le sous-sol. [Régions de grottes](#régions-de-grottes)                                                        |
| `<namespace>/dimensions/*.json`              | Définitions de dimensions. [Dimensions](#dimensions)                                                                                      |
| `<namespace>/worldtemplates/*.json`          | Tous les réglages d'un monde dans un seul fichier. [Modèles de monde](#modèles-de-monde)                                                  |
| `<namespace>/worldintro/*.json`              | Pages affichées quand un joueur entre dans le monde. [Introduction au monde](#introduction-au-monde)                                      |
| `<namespace>/gates/*.json`                   | Conditions des portails et des dimensions. [Portails et passages](#portails-et-passages)                                                  |
| `<namespace>/gamerules/*.json`               | Règles de jeu des nouveaux mondes. [Règles de jeu](#règles-de-jeu)                                                                        |
| `<namespace>/teams/*.json`                   | Camps du tableau des scores vanilla, et ce qui les rejoint. [Équipes](#équipes)                                                           |
| `<namespace>/scoring/*.json`                 | Objectifs, points et fin d'une partie. [Score](#score)                                                                                    |
| `<namespace>/raids/*.json`                   | Vagues qui s'abattent sur un village quand un joueur y apporte un présage. [Raids](#raids)                                                |
| `<namespace>/entities/*.json`                | Variantes d'entités construites sur des entités existantes. [Variantes d'entités](#variantes-dentités)                                    |
| `<namespace>/hardness/*.json`                | Temps de minage et multiplicateurs d'explosion pour des groupes de blocs. [Groupes de dureté](#groupes-de-dureté)                         |
| `<namespace>/anvils/*.json`                  | Enchantements qu'une enclume applique à un objet nommé, un progrès qu'elle octroie, et un verrou jusque-là. [Travail à l'enclume](#travail-à-lenclume) |
| `<namespace>/cards/*.json`                   | Cartes affichées à l'écran sur un déclencheur, et les messages que ce mod dit lui-même. [Cartes](#cartes)                                 |
| `<namespace>/dice/*.json` | Dés du pack aux faces pondérées, paquets de cartes, qui entend un lancer, et la formulation des résultats. [Dés et paquets](#dés-et-paquets) |
| `<namespace>/exposures/*.json`               | Dangers qui exposent les joueurs près de blocs nommés, portant des objets nommés ou dans des dimensions nommées. [Expositions](#expositions) |
| `<namespace>/overrides/<target>/<name>.json` | Propriétés de blocs, d'objets et de types de potions existants, modifiées sur place. [Surcharges de propriétés](#surcharges-de-propriétés) |
| `<namespace>/villages/*.json`                | Parcelles qu'une ville ou un village peut construire. [Parcelles de village](#parcelles-de-village)                                       |
| `<namespace>/pathintersects/*.json`          | Motifs peints là où les routes de village se croisent. [Routes de village](#routes-de-village)                                            |
| `<namespace>/structuremaps/*.json`           | Modèles assemblés en un grand bâtiment sur une grille. [Cartes de structures](#cartes-de-structures)                                      |
| `<namespace>/citymaps/*.json`                | Un plan de rues dessiné à partir duquel une ville est aménagée au lieu d'être tirée au hasard. [Cartes de plan des villes](#cartes-de-plan-des-villes) |
| `<namespace>/portalframes/*.json`            | Cadres qu'un joueur peut construire et allumer. [Cadres de portail](#cadres-de-portail)                                                   |
| `<namespace>/blastplaster/*.json`            | Ce que fait Blast Plaster après une explosion, par dimension. [Intégration de Blast Plaster](#intégration-de-blast-plaster)               |
| `<namespace>/structures/*.nbt`               | Modèles, pour les pousses, `imprint` et les surcharges de mods. [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger)           |
| `<namespace>/recipes/*.json`                 | Recettes d'artisanat, ajoutées ou remplacées. [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger)                             |
| `<namespace>/recipe_removals/*.json`         | Recettes supprimées par nom, namespace ou résultat. [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger)                       |
| `<namespace>/disabled/*.json`                | Blocs et objets retirés du jeu. [Blocs et objets désactivés](#blocs-et-objets-désactivés)                                                 |
| `<namespace>/furnace/*.json`                 | Recettes de fourneau ajoutées et retirées. [Recettes de fourneau et combustibles](#recettes-de-fourneau-et-combustibles)                  |
| `<namespace>/fuels/*.json`                   | Durées de combustion. [Recettes de fourneau et combustibles](#recettes-de-fourneau-et-combustibles)                                       |
| `<namespace>/brewing/*.json`                 | Recettes de l'alambic. [Potions, types de potions et alchimie](#potions-types-de-potions-et-alchimie)                                     |
| `<namespace>/potions/*.json`                 | Effets de potions. [Potions, types de potions et alchimie](#potions-types-de-potions-et-alchimie)                                         |
| `<namespace>/potion_types/*.json`            | Potions en bouteille construites à partir de ces effets. [Potions, types de potions et alchimie](#potions-types-de-potions-et-alchimie)   |
| `<namespace>/villagers/*.json`               | Métiers de villageois. [Villageois et échanges](#villageois-et-échanges)                                                                  |
| `<namespace>/trades/*.json`                  | Ce que les métiers achètent et vendent. [Villageois et échanges](#villageois-et-échanges)                                                 |
| `<namespace>/loot_tables/*.json`             | Tables de butin, remplacées. [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger)                                              |
| `<namespace>/loot_injections/*.json`         | Un groupe ajouté à une table qui existe déjà. [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger)                             |
| `<namespace>/block_drops/*.json`             | Butins supplémentaires ou de remplacement pour des blocs que le pack ne possède pas. [Butins des blocs](#butins-des-blocs)                |
| `<namespace>/player_loot/*.json`             | Une table de butin tirée à la mort d'un joueur. [Butin des joueurs](#butin-des-joueurs)                                                   |
| `<namespace>/advancements/*.json`            | Progrès. [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger)                                                                  |
| `<namespace>/functions/*.mcfunction`         | Fichiers de fonctions. [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger)                                                    |
| `<namespace>/tags/<kind>/*.json`             | Tags, au format propre au jeu. [Matériaux, onglets, sons, tags](#matériaux-onglets-sons-tags)                                             |
| `<namespace>/registry_remap/*.json`          | Anciens noms associés aux nouveaux. [Renommages du registre](#renommages-du-registre)                                                     |

Sous `assets/` :

| Chemin                                                                                          | Ce qu'il contient                                                                                          |
| ----------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| `<namespace>/models/`, `<namespace>/blockstates/`, `<namespace>/textures/`, `<namespace>/lang/` | Les dossiers d'assets habituels. [Modèles, états de bloc et textures](#modèles-états-de-bloc-et-textures) |
| `<namespace>/sounds.json`                                                                       | L'index des sons que lit le jeu, à côté des définitions de `sounds/` sous `data/`                          |
| `<namespace>/texts/*.txt`                                                                       | Fichiers de texte brut, utilisés par l'introduction au monde. [Introduction au monde](#introduction-au-monde) |

**1.21.1** nomme les dossiers de données vanilla au singulier : `loot_table/`, `recipe/`, `advancement/`, `function/`, `structure/`, `tags/item/`, `tags/block/`. Un pack peut utiliser l'une ou l'autre graphie ici ; les noms au pluriel ci-dessus sont lus comme leurs jumeaux au singulier, de sorte qu'un seul pack sert les deux versions.

## Lire les tableaux

*premiers pas*

Chaque fichier est du JSON standard. Voici une entrée de worldgen représentative :

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

Les tableaux de clés de ce document indiquent si une clé est obligatoire, ce qu'elle contient et la valeur par défaut en cas d'omission. Les valeurs non reconnues sont consignées dans le journal et remplacées par la valeur par défaut ; elles ne font pas planter le jeu. Types de valeurs utilisés partout :

| Quand un tableau dit                                | Vous écrivez                                                                                                                                                                       |
| --------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| int                                                 | `8`                                                                                                                                                                                |
| int, ticks                                          | `100` (20 ticks = 1 seconde)                                                                                                                                                       |
| int ou intervalle                                   | `8`, ou `{ "min": 4, "max": 12 }` pour un tirage entre les deux                                                                                                                    |
| 0 à 15, 1 à 100 et similaires                       | un int compris dans ces bornes                                                                                                                                                     |
| float                                               | `0.5`                                                                                                                                                                              |
| booléen                                             | `true` ou `false`                                                                                                                                                                  |
| chaîne                                              | `"mots entre guillemets"`                                                                                                                                                          |
| nom de bloc, nom d'objet                            | `"minecraft:stone"`. Un état de bloc est le nom accompagné de `properties` : `{ "block": "minecraft:oak_log", "properties": { "axis": "x" } }`                                     |
| `namespace:name`                                    | `"mypack:ruby_ore"`                                                                                                                                                                |
| nom de biome, nom de son, nom d'onglet              | la même forme `namespace:name` entre guillemets                                                                                                                                    |
| id de dimension                                     | `"minecraft:overworld"`, `"minecraft:the_nether"`, `"minecraft:the_end"` ou le `"mypack:verdant"` propre à un pack. Les nombres 1.12.2 `0`, `-1` et `1` sont toujours acceptés comme ces trois dimensions |
| couleur hexadécimale                                | six chiffres hexadécimaux, `"A0C8FF"`, `#` facultatif                                                                                                                              |
| chemin de texture                                   | `"mypack:block/ruby_ore"`                                                                                                                                                          |
| liste d'ints                                        | `[4, 12]`                                                                                                                                                                          |
| liste de noms de blocs                              | `["minecraft:stone", "minecraft:andesite"]`                                                                                                                                        |
| liste de noms de biomes                             | `["minecraft:windswept_hills", "mypack:ruby_hills"]`                                                                                                                               |
| liste de types de biomes                            | `["mountain", "forest"]`, les mots de type listés sous [Listes de valeurs](#listes-de-valeurs), chacun représentant un tag de biome                                                |
| liste d'ids de mods ou de namespaces de packs       | `["quark", "mypack"]`                                                                                                                                                              |
| liste d'objets                                      | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`, clés selon le tableau propre à cet objet                                                                                   |
| objet                                               | `{ "type": "cluster" }`, clés selon son propre tableau                                                                                                                             |
| objet associant rôle et biome, nom de variante et variante | les clés viennent en premier, les valeurs en second : `{ "ocean": "mypack:ruby_ocean" }`                                                                                    |

La plupart des définitions acceptent aussi `requires`, une liste d'ids de mods ou de namespaces de packs qui doivent être présents, faute de quoi le fichier est ignoré.

## La règle unique

*premiers pas*

Ouvrez le jar, trouvez le fichier à modifier et copiez son chemin à partir de `assets` ou `data` :

```
assets/minecraft/textures/block/iron_ore.png                 (in the Minecraft jar)
rdploader/assets/minecraft/textures/block/iron_ore.png       (your override)

data/minecraft/loot_tables/blocks/iron_ore.json              (in the Minecraft jar)
rdploader/data/minecraft/loot_tables/blocks/iron_ore.json    (your override)
```

Le chemin situé après `assets` ou `data` est toujours identique à celui qui se trouve dans le jar. Rien n'est renommé ni déplacé.

## Organiser les packs

*premiers pas*

Les fichiers en vrac fonctionnent sous `rdploader/assets/<namespace>/` et `rdploader/data/<namespace>/`. Le regroupement fonctionne aussi, sous forme de zip. Un dossier dans `rdploader` n'est jamais un pack : il est ignoré avec un avertissement dans le journal, donc compressez un pack en zip avant de l'y placer. Lors de la compression, sélectionnez le contenu et compressez-le, pas le dossier qui le contient : un zip dont le niveau supérieur est un dossier enveloppant `assets` ou `data` est ignoré, et le journal le signale.

```
rdploader/assets/minecraft/textures/block/iron_ore.png
rdploader/MyTextures.zip
```

**Packs dans le mauvais dossier.** Au démarrage, avant de lire `rdploader`, RDPL parcourt le dossier `resourcepacks` du jeu et le dossier `datapacks` de chaque monde (sur un serveur dédié, le monde que désigne `level-name`) et déplace dans `rdploader` tout zip de pack RDPL qu'il trouve. Un zip est un pack RDPL quand il contient des fichiers de définition RDPL, comme `data/<namespace>/blocks/` ou, pour un pack 1.12.2, `assets/<namespace>/blocks/`. Un simple pack de ressources ou de données reste où il est. Un zip dont `rdploader` contient déjà le nom est laissé en place, de même qu'un pack RDPL qui est un dossier ; les deux donnent un avertissement. Chaque déplacement est écrit dans `logs/rdpl.log`. Le jeu retire de lui-même un pack déplacé de la liste des packs de ressources ou des packs de données du monde, et RDPL le charge depuis `rdploader` dès lors.

**Priorité.** Quand deux packs contiennent le même fichier, préfixez les noms avec `RDPL` et un nombre ; les nombres plus élevés se chargent plus tard et l'emportent :

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

Insensible à la casse ; une espace, un tiret ou un tiret bas après le nombre est facultatif ; le préfixe est masqué dans le nom affiché. Un pack sans préfixe se charge en premier et perd face à tout pack numéroté. La priorité ordonne aussi les entrées de worldgen, ce qui compte quand un pack pose des blocs qu'un autre remplace.

**Désactiver un pack** en ajoutant `.disabled` à son nom.

**Un seul zip pour toutes les versions.** Un zip peut contenir un dossier `versions/<version>/` pour chaque version de Minecraft qu'il prend en charge : `versions/1.12.2/`, `versions/1.20.1/`, `versions/1.21.1/`, et pour 26.x la version exacte qui s'exécute, `versions/26.1.2/`, `versions/26.2/` ou `versions/26.3/`. Chacun est organisé comme la racine d'un pack pour cette version, `pack.mcmeta` compris. Un fichier situé sous le dossier de la version en cours est lu à la place du même chemin à la racine ; la racine est partagée par toutes les versions, et le dossier d'une autre version n'est jamais lu. Placez à la racine ce que toutes les versions lisent de la même façon et seulement ce qui diffère dans un dossier de version, et un seul zip se charge sur les quatre.

**Les packs se convertissent d'une version à l'autre.** Charger un pack écrit pour une autre ligne le convertit la première fois, de la même manière, en écrivant ce qui a changé dans son propre dossier `versions/<version>/` comme ci-dessus ; cela se produit automatiquement au chargement, y compris celui que déclenche un `/rdpl reload` ou un `/rdplserver reload`, jamais par une commande à part. Chaque paire de versions se convertit dans les deux sens, donc un pack 1.12.2, 1.20.1, 1.21.1 ou 26.x se charge sur toutes les autres. Un pack 26.3 se charge aussi sur chaque ligne, lu selon le format 26.2 sur les plus anciennes, et un pack plus ancien se charge sur 26.3. Ce qu'un côté possède et que l'autre ne peut pas contenir est abandonné, et le journal nomme chaque abandon : en descendant depuis 26.3, entre autres, les fonctions de densité de débogage, l'exclusion d'aquifère et le niveau de surface propres à un pack, et les commandes que 26.2 n'a pas ; en montant vers 26.3, le `depth` et le `offset` d'une cible d'apparition et les clés de filons de minerai du routeur de bruit. [Clés non reprises](#clés-non-reprises) les énumère toutes.

Un `pack.mcmeta` à la racine du zip est bienvenu mais pas nécessaire : le mod présente chaque pack au jeu sous une entrée qui lui est propre, avec le format de pack attendu par le jeu, de sorte qu'un pack ne devient jamais obsolète à cause d'un numéro de format. Placez un `pack.png` à côté pour donner une icône à l'entrée du dossier. Sans lui, l'entrée affiche l'icône de RDPL.

## Packs de ressources : qui l'emporte

*premiers pas*

Par défaut, les fichiers RDPL se placent au-dessus des packs de ressources qu'un joueur sélectionne, donc un pack de ressources ne peut pas les surcharger. Ajoutez `O` ou `N` après le préfixe `RDPL` pour décider pack par pack :

```
rdploader/RDPLO Branding        always wins; resource packs cannot touch it
rdploader/RDPLN BaseTextures    a resource pack can override it
rdploader/RDPL1O Seasonal       priority and override combined
```

Les packs sans lettre suivent l'option de configuration `overrideResourcePacks`. `/rdpl list` signale les packs qui surchargent. La lettre doit terminer le préfixe (suivie d'une espace, d'un tiret, d'un tiret bas ou de rien), donc `RDPLOverhaul` est un pack nommé `Overhaul`, pas un indicateur `O`.

Les mêmes niveaux s'appliquent aux packs de données. Un pack marqué `N` se place sous les packs de données qu'un monde porte dans son propre dossier `datapacks`, et un pack marqué `O` se place au-dessus.

---

# Fonctionnement des packs

## Fonctionnement des définitions

*fonctionnement des packs*

À côté des dossiers qui surchargent des fichiers, il existe des dossiers qui décrivent de nouvelles choses. Un fichier de définition regroupe un ou plusieurs éléments d'un même genre sous `variants`, et chaque clé à l'intérieur de `variants` est un nom de registre : `data/mypack/blocks/ore.json` contenant une variante appelée `ruby_ore` enregistre `mypack:ruby_ore`. Le nom du fichier n'est qu'un regroupement et rien de plus ; un fichier peut contenir un seul bloc ou une douzaine qui partagent leurs réglages.

L'enregistrement se fait à la priorité la plus basse que le loader propose, donc si un vrai mod enregistre le même nom, le mod l'emporte et votre fichier est ignoré. Rien ici ne peut remplacer un mod.

**Où se situe la limite.** Tout ce qui requiert une entité de bloc propre, un écran, un inventaire ou une logique par tick propre exige un vrai mod, à une exception près : le type [conteneur](#conteneurs), qui possède son propre inventaire et son propre écran. Tout ce qui reste en deçà est permis.

### Votre namespace est votre mod

*fonctionnement des définitions*

Le namespace que vous choisissez est, à toutes fins pratiques, un id de mod. Rien n'est chargé comme un mod et il n'apparaît jamais dans la liste des mods, mais tout ce qui lit un id de mod lit le vôtre :

- Les noms de registre sont `mypack:ruby_ore`, exactement comme ceux d'un mod, et ils sont écrits dans chaque monde sauvegardé qui les contient.
- Les listes blanches de minerais, de biomes et de recettes de la configuration le reconnaissent, donc `oreWhitelist = mypack` conserve vos minerais et bloque ceux de tous les autres.
- `/rdpl which`, `/rdplserver oregen` et les rapports regroupent tous par lui.
- JEI, les tags et les recherches des autres mods le voient de la même façon.

Choisissez donc un nom dès le départ et ne le changez jamais. Renommer un namespace rend orphelin tout ce qui est déjà placé dans un monde, comme lorsqu'un mod change d'id ; c'est ce que `registry_remap` sert à réparer.

Cela fonctionne dans les deux sens : `requires` accepte un namespace de pack aussi bien qu'un id de mod installé, donc un pack peut dépendre d'un autre et être ignoré s'il n'est pas installé.

Un mod ou un pack nommé dans `requires` qui n'est pas installé fait ignorer la définition : une ligne est écrite dans `logs/rdpl.log` pour nommer ce qui manquait, et le jeu continue. Si un bloc attendu n'est pas dans l'onglet créatif, cette ligne du journal est le premier endroit où regarder.

`requires` n'accepte que des ids nus. Il n'existe aucune syntaxe d'intervalle de versions, donc il peut dire qu'un mod doit être présent mais pas quelle version.

L'id du mod lui-même, `resourcedatapackloader`, est réservé. Définir du contenu sous cet id est ignoré et consigné, car cela revendiquerait la propriété de choses que ce mod enregistre. Surcharger les assets propres à ce mod reste permis ; seul y enregistrer du contenu ne l'est pas.

Chaque tableau ci-dessous suit les conventions de [Lire les tableaux](#lire-les-tableaux).

La plupart des définitions acceptent aussi `requires`, une liste d'ids de mods ou de namespaces de packs qui doivent être présents, faute de quoi le fichier est ignoré.

### Options de pack

*fonctionnement des définitions*

Toutes les clés qu'accepte un fichier d'options :

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

| Clé                     | Obligatoire | Valeur                | Défaut  | Ce qu'elle fait                                                                                                                             |
| ----------------------- | ----------- | --------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| un nom d'option         | oui         | booléen, ou un objet  |         | `true` ou `false` est la valeur par défaut de l'option. Un objet porte les trois clés ci-dessous                                            |
| `hide` au niveau supérieur | non      | booléen               | `false` | Garde les options de ce pack hors de l'écran des options et du fichier généré, tout en continuant de conditionner le contenu à leurs valeurs par défaut |
| `default`               | oui         | booléen               |         | La valeur de l'option jusqu'à ce que l'utilisateur la change. Un objet sans `default` booléen est ignoré, avec un avertissement             |
| `hide` dans une option  | non         | booléen               | `false` | Masque cette seule option, qui ne peut donc pas être basculée et reste à sa valeur par défaut                                               |
| `description`           | non         | chaîne                | aucune  | Affichée sous le nom de l'option dans l'écran des options                                                                                   |

Un pack peut contenir un dossier `config` à côté de ses `assets` et `data`, avec des fichiers JSON d'options vrai/faux et leurs valeurs par défaut :

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

Un fichier portant `"hide": true` au niveau supérieur garde les options de ce pack hors de l'écran des options et du fichier généré, tout en continuant de conditionner le contenu à leurs valeurs par défaut. Deux cas le demandent : le contenu qui n'est pas prêt à être publié, et les packs modèles, où les options sont une mécanique qui tient les définitions ensemble plutôt qu'un choix que quelqu'un devrait faire. Retirez la clé pour les publier. Cela marche aussi option par option : `"hide": true` dans l'objet d'une option masque celle-là seule, de sorte qu'un pack terminé peut porter un interrupteur pour du contenu inachevé, ou une barrière de modèle, sans que ni l'un ni l'autre n'apparaisse :

    { "enablePackB": { "default": false, "hide": true } }

Comme une option masquée ne peut pas être basculée, celle qui est masquée avec `true` par défaut est de fait forcée à actif, pour du contenu qui doit rester relié par la mécanique des options mais n'est pas un choix.

Une option peut aussi être un objet portant une description, affichée sous son nom dans l'écran des options :

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

Au lancement, les fichiers d'options du pack deviennent un vrai fichier de configuration appartenant à l'utilisateur, nommé d'après le pack, `rdploader/config/PackA.json`, créé avec les valeurs par défaut du pack et fusionné lors des mises à jour du pack, de sorte que les nouvelles options arrivent sans toucher à ce que l'utilisateur a déjà réglé. Les changements s'appliquent au prochain démarrage du jeu, et le bouton Options du pack sur les écrans de sélection et de création de monde est l'endroit où un joueur les bascule. Les options appartiennent uniquement aux packs nommés, c'est-à-dire aux zips, puisque le fichier généré porte le nom du pack ; les fichiers en vrac sous `rdploader/assets` et `rdploader/data` n'ont pas de nom de pack et ne portent aucune option, donc compressez le contenu en vrac dans un pack nommé s'il a besoin d'un interrupteur.

La liste `requires` de n'importe quelle définition peut alors nommer une option avec une entrée `config:` : `"requires": ["config:enableTestingContent"]` n'enregistre ce contenu que tant que l'option vaut true, exactement comme un mod absent le ferait ignorer. Un nom nu vérifie le fichier de chaque pack, et tous les packs qui la définissent doivent être d'accord ; `"config:PackA:enableTestingContent"` désigne un seul pack. Une option qu'aucun pack ne définit compte comme false et est signalée une seule fois par un avertissement.

Une option qui conditionne quelque chose avec quoi un monde a été créé est mémorisée par ce monde, réécrite à chaque sauvegarde du monde. Changez-la et rouvrez le monde : quand le changement laisse du contenu non enregistré que le monde contient, le monde est d'abord sauvegardé, dans le dossier `backups` du jeu, exactement comme le fait l'écran Modifier le monde ; si cette sauvegarde échoue, le monde ne s'ouvre pas. Le premier joueur dans l'Overworld est informé des options qui ont changé, et qu'une copie a été faite le cas échéant.

Une entrée `file:` conditionne sur l'existence d'un fichier ou d'un dossier sous le dossier du jeu, pour relier du contenu à quelque chose d'extérieur aux packs propres de RDPL, comme le pack de ressources d'un autre mod : `"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` n'enregistre le contenu que tant que ce fichier exact est installé. Le chemin est relatif au dossier du jeu, toujours avec des barres obliques, et ne peut pas contenir `..`.

### Hériter des définitions

*fonctionnement des définitions*

Une définition de bloc ou d'objet peut partir d'une autre du même genre avec `"inherits"`, en nommant le nom de registre de n'importe quelle variante, puis surcharger ce qui diffère :

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "hardness": 4.0 } } }

L'enfant copie chaque statistique du fichier du parent et de la variante nommée, l'ordre des fichiers n'a jamais d'importance, les chaînes se résolvent en commençant par le parent, et un cercle ou un parent manquant est consigné et laisse l'enfant tel qu'il est écrit. Les champs que l'enfant écrit remplacent la valeur héritée ; les propriétés imbriquées des variantes se surchargent une à une, mais les listes comme `requires` se remplacent en entier, donc écrivez la liste complète voulue. Les blocs n'héritent que de blocs et les objets que d'objets.

### Modèles de blocs et d'objets

*fonctionnement des définitions*

Un parent peut être un pur modèle qui n'entre jamais dans le jeu, puisque l'héritage lit les fichiers de définition eux-mêmes, pas ce qui a été enregistré. Placez le modèle derrière une option masquée forcée à inactif, et il n'enregistre rien tandis que ses statistiques restent héritables :

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

Le modèle ne s'enregistre jamais, tandis que `jacks_ore` s'enregistre avec le matériau, le son, l'outil, l'onglet, les gains d'expérience et la résistance du modèle, ne surchargeant que la dureté. L'enfant doit écrire son propre `requires`, ici vidé en liste vide, car il hérite sinon de celui du parent et disparaîtrait avec lui.

## Ce que vous pouvez surcharger

*fonctionnement des packs*

- **Tout ce qui se trouve dans le dossier d'assets d'un mod**, textures, modèles, états de bloc, fichiers de langue, sons, polices, textes de splash, livres-guides, manuels
- **Progrès, tables de butin, tags et fonctions**, côté serveur, donc ils fonctionnent aussi sur les serveurs dédiés
- **Recettes**, remplacez la recette d'un mod ou ajoutez la vôtre
- **Modèles de structures**, les fichiers `.nbt` que les mods utilisent pour les bâtiments générés, sous `<namespace>/structures/`
- **Renommages du registre**, pour que les anciens mondes continuent de fonctionner quand un mod renomme un bloc ou un objet
- **Suppressions de recettes**, supprimez une recette d'artisanat par nom, namespace ou résultat
- **Blocs et objets désactivés**, retirez n'importe quel bloc ou objet du jeu, voir [Blocs et objets désactivés](#blocs-et-objets-désactivés)
- **Injections de butin**, ajoutez un groupe à une table de butin au lieu de remplacer la table entière
- **Butins des blocs**, ajoutez à ce que laisse n'importe quel bloc quand il est cassé, ou remplacez-le, expérience comprise
- **Butin des joueurs**, tirez une table de butin à la mort d'un joueur, en plus de ce qu'il portait ou à sa place
- **Propriétés des blocs, objets et potions existants**, dureté, lumière, tailles de pile, nourriture sur n'importe quoi, les effets d'une potion, voir [Surcharges de propriétés](#surcharges-de-propriétés)
- **Recettes de fourneau, durées de combustion, onglets créatifs et événements sonores**

Ce qu'un bloc laisse, c'est sa table de butin sur cette version : pour changer ce que laisse la pierre, fournissez `data/minecraft/loot_tables/blocks/stone.json`, et pour y ajouter sans la remplacer, une injection de butin ou une règle de [butins des blocs](#butins-des-blocs), qui peut aussi donner de l'expérience.

RDPL convient pour remplacer une ou deux recettes, et les recettes de votre propre contenu doivent être ajoutées dans le pack qui l'accompagne. Pour un contrôle complet des recettes dans un modpack, KubeJS et CraftTweaker sont de meilleures options, et un fichier ici remplace toujours entièrement l'original, donc pour changer un ingrédient ou supprimer une entrée de butin, utilisez ceux-là.

## Packs côté serveur

*fonctionnement des packs*

Un pack peut vivre uniquement sur le serveur, avec des joueurs sur des clients vanilla simples, sous une seule contrainte : **rien dedans ne doit rien enregistrer**. Le mod accepte n'importe quel distant ; c'est le pack qui décide. Un client vanilla joue avec les registres livrés avec lui, donc un pack qui y ajoute doit être des deux côtés.

| Le serveur seul suffit                                                                                                                                    | Nécessite le pack aussi sur le client                                                                                   |
| --------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures`, `structuremaps`, `citymaps`, `villages`, `pathintersects`, `caveregions`, `biomes`, `dimensions` | `blocks`, `items`, `fluids`, `materials`, `containers`                                                                  |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `anvils`, `tags`, `disabled`                                                                 | `potions`, `potion_types`, `sounds`, `tabs`, `exposures`                                                                |
| `loot_tables`, `loot_injections`, `player_loot`, `advancements`, `functions`                                                                              | `entities`, `villagers`, `portalframes`                                                                                 |
| `gates`, `cards`, `trades`, `registry_remap`, `teams`, `scoring`, `raids`, `hardness`, `blastplaster`                                                     | `models`, `blockstates`, `textures`, `lang`, `worldintro`, `overrides` (dossiers client : sans client, laissez-les de côté) |
| toute la couche de contrôle, les réglages et la prégénération                                                                                             |                                                                                                                         |

La colonne de droite est un arrêt net : les blocs, objets, types d'entités, sons et effets de potions qu'un client vanilla n'a pas ne peuvent pas lui être décrits, et le portail propre à une dimension fait partie des blocs du pack ; les expositions ne se chargent qu'avec ce contenu. La colonne de gauche fonctionne parce que tout ce qui s'y trouve s'exécute entièrement côté serveur, parvient au client sous forme d'entrées de pack de données que vanilla lit déjà (biomes, régions de grottes, types de dimensions), ou passe par des paquets que vanilla parle déjà (emplacement de résultat d'artisanat rempli par le serveur, paquets de progrès ordinaires, refus de portail en messages d'état, et une retenue de prégénération faite de paquets vanilla de mode de jeu, de titre et de téléportation).

Mise en place :

1. Activez `vanillaClients` dans la configuration (catégorie `content`, redémarrage requis). Il impose la colonne de droite : ces dossiers sont ignorés au chargement et chaque fichier ignoré est nommé dans le journal, de sorte qu'un fichier de bloc égaré devient une ligne de journal au lieu d'une connexion refusée.
2. Gardez quand même les définitions hors des dossiers de droite ; les fichiers ignorés sont du poids mort. Là où le pack référence des objets (le `hold` d'un portail, `killedDrops`, les résultats de recettes, les échanges), ne nommez que des objets que vanilla ou les autres mods présents des deux côtés du serveur fournissent. Un biome qui nomme les blocs de sol propres au pack garde le sol du biome de base, et une dimension ouverte par son propre portail a besoin de ce bloc de portail, donc envoyez-y les joueurs par commande.
3. Les variantes d'entités sont des types d'entités à part entière sur cette version, donc elles appartiennent à la colonne de droite : avec `vanillaClients` activé, elles sont ignorées, leurs apparitions avec elles, et le journal les nomme.
4. Installez sur le serveur comme d'habitude, avec Blast Plaster, que le mod requiert et qui n'enregistre rien non plus. Rien ne va sur les machines des joueurs ; `/rdpl` n'existera pas pour eux.
5. Testez avec une connexion propre d'un client vanilla de la même version. Les échecs sont bruyants : la connexion est refusée à la porte, pas silencieusement cassée plus tard.
6. Deux écarts cosmétiques acceptés : les recettes ajoutées par le serveur s'artisanent mais n'apparaissent pas dans le livre de recettes, et la retenue pendant la création du terrain est une simple retenue en spectateur avec la progression dans la barre d'action, sans le brouillard ni le logo que dessine le client propre au mod.

## Renommages du registre

*fonctionnement des packs*

`<namespace>/registry_remap/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers s'empilent.

Quand un mod renomme l'un de ses blocs ou objets, les mondes sauvegardés avant le renommage les perdent. Déposez ici un fichier pour associer l'ancien nom au nouveau :

```json
{
  "registry": "minecraft:item",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

Le registre est celui auquel l'entrée appartient, nommé comme le jeu le nomme : `minecraft:item`, `minecraft:block`, `minecraft:entity_type` et ainsi de suite. Les renommages s'enchaînent, donc associer A à B puis plus tard B à C envoie A directement vers C.

## API du mod

*fonctionnement des packs*

Un mod peut livrer du contenu RDPL dans son propre jar, sans avoir besoin d'un pack séparé. Placez un dossier nommé `rdploader` à la racine du jar et organisez-le exactement comme un pack :

```
thatmod.jar
  META-INF/mods.toml                (1.21.1: META-INF/neoforge.mods.toml)
  rdploader/data/thatmod/blocks/ruby_ore.json
  rdploader/assets/thatmod/textures/block/ruby_ore.png
```

Ce qu'un mod livre est une valeur par défaut, pas une surcharge. Cela se charge sous tous les packs du dossier des packs, donc tout ce qu'écrit un auteur de pack l'emporte, et un mod ne peut fournir que des fichiers sous un namespace qu'il déclare dans son propre fichier de mod. Les fichiers sous tout autre namespace sont ignorés avec un avertissement, de même qu'un dossier `rdploader` imbriqué dans un namespace, de sorte qu'un mod ne peut pas redéfinir discrètement le contenu d'un autre mod ou celui d'un auteur de pack.

Chaque mod qui en livre un obtient une entrée dans `rdploader/config/mods.json` la première fois qu'il est vu :

```json
{
  "thatmod": {
    "enabled": true,
    "priority": -1
  }
}
```

| Champ      | Valeurs           | Défaut  | Ce qu'il fait                                                                                                                               |
| ---------- | ----------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| `enabled`  | `true` ou `false` | `true`  | Désactive le contenu de ce mod, comme `.disabled` désactive un pack                                                                         |
| `priority` | `-1` ou un nombre | `-1`    | `-1` place le mod sous tous les packs ; tout autre nombre le place dans l'ordre de [priorité](#organiser-les-packs) ordinaire, à côté des packs numérotés |

Un pack de mod ne rejoint jamais le niveau de surcharge des packs de ressources, quoi que dise `overrideResourcePacks`, puisque seul un auteur de pack peut le demander avec la lettre `O`. Le journal signale les packs de mods et liste les packs du plus bas au plus haut, donc rien ne se charge à l'insu de personne.

## Packs écrits pour 1.12.2

*fonctionnement des packs*

Un pack conçu pour la ligne 1.12.2 se charge tel quel. Le loader en reconnaît un à son format `pack.mcmeta`, à des dossiers de définitions sous `assets/` sans `data/` à côté, ou à un fichier `.lang`, et le porte vers l'avant. Un zip est converti une seule fois, en lui-même : chaque fichier que cette version lit différemment est écrit dans le dossier `versions/1.20.1/` du zip (1.21.1 : `versions/1.21.1/`) avec tout ce qui suit déjà appliqué, et les fichiers 1.12.2 à la racine restent tels quels, de sorte que le même zip se charge toujours sur 1.12.2, comme le décrit [un seul zip pour toutes les versions](#organiser-les-packs). Le portage marque le dossier qu'il écrit avec un fichier `port.stamp` contenant la version de RDPL. Un zip qui a déjà le dossier de cette version est lu à travers lui ; quand son `port.stamp` nomme une autre version de RDPL, le portage réécrit le dossier à neuf, en remplaçant chaque fichier qu'il contient et en nommant chacun dans le journal, et un dossier sans `port.stamp`, comme un dossier écrit par l'auteur du pack, n'est plus jamais converti. Les fichiers 1.12.2 de la racine que le portage a remplacés, comme les définitions sous `assets/`, les fichiers `.lang`, les états de bloc et modèles 1.12.2, et les textures sous `textures/blocks/` et `textures/items/`, ne sont pas lus sur cette version ; seuls les fichiers de la racine que le portage laisse passer sans changement, comme les sons, le sont encore. Le zip est d'abord écrit dans un fichier temporaire et ne remplace l'original qu'une fois complet. Les fichiers en vrac sous `rdploader/assets` ne sont pas réécrits ; ils sont lus à travers le même portage à chaque analyse du dossier.

- Les dossiers de définitions passent de `assets/<namespace>/` à `data/<namespace>/`, et les dossiers de données vanilla avec eux : recettes, tables de butin, injections de butin, progrès, fonctions et structures.
- `textures/blocks/` et `textures/items/` sont servis comme `textures/block/` et `textures/item/`, dans les modèles, dans les cartes de pixels et dans les fichiers eux-mêmes. Un modèle d'objet à `models/item/<file>/<variant>.json` est servi comme `models/item/<variant>.json`.
- Chaque id avec métadonnées, `minecraft:wool:14` ou `minecraft:dye:4`, passe par les correcteurs de données du jeu, le même code qui met à niveau un monde 1.12.2, et ressort donc sous la forme du bloc ou de l'objet qu'il est devenu : `minecraft:red_wool`, `minecraft:lapis_lazuli`. Un état de bloc qui a survécu à l'aplatissement en tant que propriété, `minecraft:log:1` vers `minecraft:oak_log` avec `axis=y`, ressort sous forme d'objet `properties`. Les ids propres au pack se résolvent par ses propres définitions : `mypack:materials:5` devient la variante dont le `meta` valait 5, et `mypack:ruby_ore` la première variante du fichier, puisque chaque variante est ici un bloc à part. Les noms d'entités et de biomes sont corrigés de la même façon, et les numéros de dimension deviennent des ids.
- Les `variants` gardent leurs clés ; `meta` est abandonné et `oreDict` devient `tags` grâce à la correspondance du dictionnaire des minerais vers les tags de convention. Un fichier `oredict/*.json` devient un fichier de tag d'objet par nom qu'il ajoute ou retire : un retrait `-name` atterrit dans la liste `remove` du tag, et retirer `*` remplace le tag. Un `creativeTab` nu prend le namespace du pack, et un libellé d'onglet vanilla 1.12.2 comme `misc` devient l'onglet vanilla le plus proche.
- Un fichier `.lang` est servi comme le `.json` que lit le jeu, avec `tile.mypack:file.variant.name` en `block.mypack.variant`, `item.` de la même façon, `itemGroup.x` en `itemGroup.mypack.x`, `fluid.x` en deux clés de fluide, et tout le reste tel quel.
- Un état de bloc 1.12.2 n'est pas servi du tout. Ses textures sont lues à la place et servies sous les noms que cherche le générateur, `textures/block/<variant>.png` avec `_top` et `_bottom` là où l'état de bloc avait `end`, `top` ou `bottom`, de sorte que l'état de bloc et les modèles sont générés pour chaque variante comme ils le seraient pour un pack écrit ici.
- Les recettes perdent leur `data` et gagnent des ids aplatis, `forge:ore_shaped` devient `minecraft:crafting_shaped` avec les ingrédients `ore` en `tag`, les tables de butin perdent `set_data` de la même façon, et le `item` d'un progrès avec `data` devient `items`. Le `background` d'un progrès passe de `textures/blocks/` à `textures/block/`.
- Le vocabulaire Forge 1.12.2 d'une recette est lui aussi repris : un type d'ingrédient `forge:ore_dict` ou `minecraft:item` est abandonné, un ingrédient `minecraft:item_nbt` devient `forge:nbt` (1.21.1 : `neoforge:components`) avec son nbt passé par les correcteurs de données, `minecraft:item_exists` devient `forge:item_exists` (1.21.1 : chaque condition prend son nom `neoforge`, sous `neoforge:conditions`), un objet sans namespace prend celui de la recette, et `data` 32767 devient une liste de toutes les variantes. Une `#CONSTANT` issue du `_constants.json` d'un mod ne peut pas suivre, et le journal la nomme. Partout où une liste nomme des objets, `name:*` devient toutes les variantes qu'avait l'objet, et une valeur unique prend la première ; cela touche les résultats de `recipe_removals` et les retraits de `furnace`, qui sont repris comme des objets aussi. Le `item` ou `with` d'une enclume écrit `name:*` devient une liste de toutes les variantes, et n'importe laquelle convient.
- Les tables de butin renommées depuis 1.12.2 sont renommées partout où un pack en nomme une : une cible de `loot_injections`, une table de `player_loot` et une entrée `loot_table` (1.21.1 : son `value`), de sorte que `minecraft:entities/zombie_pigman` devient `minecraft:entities/zombified_piglin`. `killed_by_player` avec `inverse` devient une condition `inverted`, `entity_properties` avec `on_fire` devient un prédicat `flags`, et les noms de `set_attributes` comme `generic.maxHealth` deviennent `generic.max_health` (1.21.1 : l'opération prend son nouveau nom, et le `name` du modificateur devient son `id`).
- Une surcharge d'un bloc 1.12.2 que l'aplatissement a scindé, comme `overrides/minecraft/wool.json`, est lue comme une surcharge de chaque bloc qu'il est devenu, les seize laines, puisque 1.12.2 changeait toutes les variantes d'un coup.
- Le `gameLoopFunction` d'un fichier de règles de jeu devient le tag de fonction `#minecraft:tick`, écrit dans `data/minecraft/tags/functions/tick.json`, puisque la règle de jeu n'existe plus. Les numéros de dimension propres au pack sont lus par ses fichiers `dimensions`, donc `"id": 7` dans `dimensions/verdant.json` fait de 7 `mypack:verdant` partout où le pack le nomme. Les deux côtés d'une paire `villageBlocks` sont corrigés, la chance conservée, et un fichier `registry_remap` peut garder les `minecraft:blocks` et `minecraft:items` au pluriel de 1.12.2.
- Un modèle de monde qui désactive toutes les structures que 1.12.2 avait dans une dimension désactive aussi là les structures propres à cette version : `ancient_cities`, `buried_treasures`, `ocean_ruins`, `pillager_outposts`, `ruined_portals`, `shipwrecks` et `trail_ruins` dans l'Overworld et `nether_fossils` dans le Nether. Laissez l'un des noms 1.12.2 actif et elles sont laissées tranquilles. Les clés de contrôle des générateurs, `blockWorldGenerators` et ses compagnes, sont omises d'un modèle converti avec une ligne dans le journal, puisque rien ici ne les lit. Les couches `generatorOptions` d'un modèle plat ont leurs noms de blocs corrigés de la même façon, donc `minecraft:grass` devient `minecraft:grass_block`.
- Les fonctions sont réécrites ligne par ligne dans la syntaxe de commandes de cette version. Les ids avec valeurs de données passent par les mêmes correcteurs de données, de sorte que `give @p minecraft:wool 1 14` devient `give @p minecraft:red_wool 1` et `give @p mypack:materials 1 5` donne la variante dont le `meta` valait 5, et le nbt d'objet, d'entité et de bloc est corrigé comme le serait celui d'un monde (1.21.1 : le nbt d'un objet devient ses composants). `testforblock`, `testfor` et `scoreboard players test` deviennent `execute if`, `execute <entity> <x> <y> <z> [detect ...]` devient `execute as ... at @s [positioned ...] [if block ...] run`, `effect` prend `give` et `clear`, `blockdata`, `entitydata` et `replaceitem` deviennent `data merge` et `item replace`, et `scoreboard teams` et `scoreboard players tag` deviennent `team` et `tag`. Les sélecteurs troquent `score_X_min` et `score_X` contre `scores`, `r` et `rm` contre `distance`, `l` et `lm` contre `level`, `m` contre `gamemode`, `c` contre `limit` et `sort`, et `rx` et `ry` contre `x_rotation` et `y_rotation`. Les numéros d'enchantements et d'effets, les noms de particules et de sons, les numéros de mode de jeu et de difficulté, une durée `weather` en secondes et un `tp` relatif d'une autre entité sont repris aussi. Une ligne que le portage ne peut pas reprendre est conservée telle qu'écrite et le journal nomme le fichier, la ligne et la raison ; une fonction contenant une telle ligne ne se charge pas tant qu'elle n'est pas corrigée à la main. `block_drops` est repris tel quel, son `meta` intégré au nom du bloc ou à ses `properties`.
- Le sol d'un monde plat 1.12.2 se trouvait à y 0, et cette version pose un monde plat depuis son fond à y -64, donc le portage abaisse les hauteurs en conséquence. Quand le `worldType` d'un modèle de monde est `flat` ou `superflat`, son `worldSpawn` et son `resetSendsTo` descendent de 64 (jusqu'à son `worldMinHeight` quand il en nomme un), de même que le `spawn` d'une équipe, le `at` de `standIn` et les hauteurs de `spawnBox`, et le `opens.lobby` d'un fichier de score quand chaque modèle de monde livré par le pack est plat. Une dimension de pack au terrain `flat` abaisse son `groundLevel`, et toute position qui nomme cette dimension, de la même façon (selon son `minHeight` quand elle en a un). Les fonctions ne disent pas où elles s'exécutent, donc quand l'Overworld du pack est plat, chaque y absolu de ses fonctions descend de 64, le `y` d'un sélecteur compris, et le journal le dit une seule fois ; les hauteurs en `~` et `^` sont laissées telles quelles. Un monde au terrain normal garde toutes ses coordonnées, puisque sa surface reste au niveau de la mer.

Le journal contient une ligne de synthèse par pack porté et une ligne pour chaque fichier qu'il a déplacé, omis ou n'a pas pu reprendre, et chaque clé que cette version ne lit plus est toujours nommée par l'analyseur qui la rencontre. Le portage est un travail au mieux, pas un pack terminé : lisez ces lignes et terminez à la main ce qu'elles nomment, en commençant par toute ligne de commande conservée telle qu'écrite et toute texture dont il n'a pas trouvé de nom. Faites ces corrections à la racine du pack ou dans un zip séparé, jamais dans le dossier `versions/` que le portage a écrit : une autre version de RDPL écrit ce dossier à neuf.

---

# Blocs et objets

## Blocs

*blocs et objets*

`<namespace>/blocks/*.json`

Chaque clé à l'intérieur de `variants` est un bloc, enregistré sous le namespace du pack : un fichier contenant `ruby_ore` et `deep_ruby_ore` enregistre `mypack:ruby_ore` et `mypack:deep_ruby_ore`, qui partagent chaque réglage que le fichier écrit en dehors de `variants`. Le nom du fichier n'est qu'un regroupement.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin. Une clé marquée pour un type n'est lue que par ce type.

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

### Types

*blocs*

| Type         | Ce que vous obtenez                                                                                                                                                                                                                                   |
| ------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `basic`      | Un bloc simple. Utilisé quand `type` est absent                                                                                                                                                                                                       |
| `ore`        | Laisse autre chose que lui-même, avec fortune et toucher de soie                                                                                                                                                                                      |
| `falling`    | Tombe comme le sable ou le gravier                                                                                                                                                                                                                    |
| `slab`       | Bas, haut et double, et deux d'entre eux fusionnent en main                                                                                                                                                                                           |
| `stairs`     | Angles et pentes gérés pour vous                                                                                                                                                                                                                      |
| `fence`      | Se raccorde à ses voisins, et aux barrières d'autres mods                                                                                                                                                                                             |
| `pane`       | Se raccorde comme les vitres                                                                                                                                                                                                                          |
| `wall`       | Se raccorde comme les murets de pierre, avec la forme du poteau                                                                                                                                                                                       |
| `door`       | Haute de deux blocs, s'ouvre à la main et répond à la redstone                                                                                                                                                                                        |
| `trapdoor`   | Un battant articulé sur le haut ou le bas d'un bloc, ouvert à la main ou par la redstone                                                                                                                                                              |
| `fence_gate` | Un portillon dans une ligne de barrières, ouvert à la main ou par la redstone, et abaissé là où il rencontre un muret                                                                                                                                 |
| `banner`     | Une bannière sur un poteau ou contre un mur, seize rotations debout, portant votre propre motif                                                                                                                                                       |
| `ladder`     | Escaladable, posée contre un mur                                                                                                                                                                                                                      |
| `torch`      | Pose au mur et au sol, avec une particule. Émet la `light` de la variante telle qu'écrite, donc une torche à `0` n'en donne aucune                                                                                                                    |
| `bell`       | Une cloche comme celle des villages du jeu : sonne quand on l'utilise sur son côté, par la redstone ou quand un projectile la frappe, oscille dans son cadre, et fait briller les pillards proches. Son état de bloc porte l'orientation et la façon dont elle est suspendue |
| `log`        | Pivote vers la face contre laquelle on la place, et porte le tag `minecraft:logs` pour que l'abattage d'arbres et Blast Plaster la traitent comme un tronc                                                                                            |
| `leaves`     | Se dégrade, se cisaille, se teinte et laisse une pousse, et porte le tag `minecraft:leaves`. Laissées `opaque`, elles s'affichent pleines, comme les feuilles rapides ; réglez `"opaque": false` pour voir à travers                                   |
| `sapling`    | Pousse en arbre ou en l'une de vos structures                                                                                                                                                                                                         |
| `crop`       | Pousse par étapes, laisse une graine et un produit, graines de blé et blé pour celui que le fichier omet                                                                                                                                              |
| `flower`     | Une plante d'un bloc debout sur un sol                                                                                                                                                                                                                |
| `cane`       | Pousse vers le haut en colonne, comme les roseaux ou le cactus                                                                                                                                                                                        |
| `vine`       | Grimpe et pend sur les côtés des blocs. Avec `growth` elle descend jusqu'à `maxHeight` et, avec `spread`, gagne latéralement les murs voisins ; sans lui, elle reste comme posée                                                                       |
| `portal`     | Envoie tout ce qui y entre vers une autre dimension                                                                                                                                                                                                   |
| `container`  | Contient un inventaire qu'un joueur peut ouvrir, de n'importe quelle taille, et peut se remplir seul depuis une table de butin à sa première ouverture. S'affiche comme un bloc ordinaire ou comme un coffre, selon ce que demande le pack. Sans objet `container`, il contient trois rangées de neuf |

### Clés de fichier

*blocs*

| Clé                          | Obligatoire    | Valeur                                            | Défaut                                                                                                       | Ce qu'elle fait                                                                                                                                                                                                                                                                                                                                                                                                     |
| ---------------------------- | -------------- | ------------------------------------------------- | ------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `variants`                   | oui            | objet associant nom de variante et variante       |                                                                                                              | Un bloc par entrée. La clé est son nom de registre, et nomme son état de bloc, ses modèles, ses textures et sa clé de langue                                                                                                                                                                                                                                                                                        |
| `type`                       | non            | l'un des types ci-dessus                          | `basic`                                                                                                      | La forme que prend le bloc                                                                                                                                                                                                                                                                                                                                                                                          |
| `material`                   | non            | l'un des [matériaux de bloc](#listes-de-valeurs)  | `rock`                                                                                                       | Le bloc fait ce que faisait ce matériau sur 1.12.2 : s'il laisse quelque chose quand on le casse à la main, comment les pistons le traitent, si la lave l'enflamme, si un liquide qui coule l'emporte et si un bloc posé le remplace. Un `log` est toujours `wood`, `leaves` toujours `leaves`, une `vine` `vine`, une `torch` ou `ladder` `circuits`, un `crop` `plants`, et `stairs` et un `wall` se comportent comme leur `modelBlock` |
| `soundType`                  | non            | l'un des [types de son](#listes-de-valeurs)       | `stone` ; `wood` pour un `log`, `plant` pour `leaves` et un `crop`, celui du `modelBlock` pour `stairs` et un `wall` | Bruits de pas, de casse et de pose                                                                                                                                                                                                                                                                                                                                                                                  |
| `mapColor`                   | non            | l'une des [couleurs de carte](#listes-de-valeurs) | selon le matériau                                                                                            | Son apparence sur une carte                                                                                                                                                                                                                                                                                                                                                                                         |
| `harvestTool`                | non            | `pickaxe`, `axe`, `shovel`, `hoe`, `sword`        | `pickaxe`                                                                                                    | L'outil qui le récolte, écrit pour vous dans les tags `mineable` du jeu ; `sword` va dans `resourcedatapackloader:mineable/sword`, que minent les outils `sword` propres à un pack. Pour les butins, cela ne compte que sur un matériau qui exige un outil. Comme sur 1.12.2, une pioche mine aussi `rock`, `iron` et `anvil` à pleine vitesse, et une hache `wood`, `plants` et `vine`. Tout autre nom, comme `shears`, est consigné et omis |
| `harvestToolLevel`           | non            | 0 à 4                                             | `0`                                                                                                          | 0 bois, 1 pierre, 2 fer, 3 diamant, 4 netherite, écrit pour vous dans les tags `needs_*_tool`. Un bloc `basic`, `ore`, `container`, `portal`, `fence`, `pane`, `log`, `falling`, `slab` ou `wall` l'ignore et prend le `harvestLevel` de la variante, comme sur 1.12.2                                                                                                                                              |
| `silkHarvest`                | non            | booléen                                           | `true`                                                                                                       | Si le toucher de soie rend le bloc lui-même                                                                                                                                                                                                                                                                                                                                                                         |
| `opensWith`                  | non            | id d'objet                                        | aucun                                                                                                        | Fait du bloc un coffre verrouillé : le casser laisse le bloc lui-même, et un clic droit avec l'objet nommé en consomme un, joue le son de casse du bloc, verse la liste `drops` de la variante et retire le bloc. Tout autre clic affiche dans la barre d'action la ligne `block.<pack>.<block>.locked` des fichiers de langue                                                                                       |
| `openSound`                  | non            | nom de son                                        | le son de casse                                                                                              | Ce qu'un coffre verrouillé joue à l'ouverture à la place de son son de casse. Un nom 1.12.2 est toujours lu, voir [noms de sons](#listes-de-valeurs)                                                                                                                                                                                                                                                                |
| `expDrop`                    | non            | objet avec `min` et `max`                         | aucun                                                                                                        | Expérience lâchée quand un joueur casse le bloc, ou qu'un mob de pack qui collecte l'expérience le creuse ; les pistons, l'eau et les explosions n'en lâchent pas. Toucher de soie ne l'enlève que quand `silkHarvest` est activé                                                                                                                                                                                    |
| `creativeTab`                | non            | nom d'onglet                                      | aucun                                                                                                        | L'onglet où il apparaît, voir [Onglets créatifs](#onglets-créatifs)                                                                                                                                                                                                                                                                                                                                                 |
| `renderLayer`                | non            | `solid`, `cutout`, `cutout_mipped`, `translucent` | selon le type                                                                                                | Comment il est dessiné                                                                                                                                                                                                                                                                                                                                                                                              |
| `opaque`                     | non            | booléen                                           | `true`                                                                                                       | S'il bloque entièrement la vue et la lumière                                                                                                                                                                                                                                                                                                                                                                        |
| `fullCube`                   | non            | booléen                                           | comme `opaque`                                                                                               | S'il remplit tout son espace                                                                                                                                                                                                                                                                                                                                                                                        |
| `lightOpacity`               | non            | 0 à 255                                           | `255` si opaque, sinon `0`                                                                                   | Quelle quantité de lumière il absorbe : 15 ou plus l'arrête entièrement, et `0` laisse passer la lumière du soleil directement. Un `slab` garde la valeur propre au jeu, et un conteneur à modèle de coffre laisse passer la lumière                                                                                                                                                                                  |
| `slipperiness`               | non            | float                                             | `0.6`                                                                                                        | La glace vaut `0.98`                                                                                                                                                                                                                                                                                                                                                                                                |
| `flammability`               | non            | int                                               | `0`                                                                                                          | La facilité avec laquelle le feu le consume                                                                                                                                                                                                                                                                                                                                                                         |
| `fireSpread`                 | non            | int                                               | `0`                                                                                                          | La facilité avec laquelle le feu s'en propage                                                                                                                                                                                                                                                                                                                                                                       |
| `explosionResistanceDivisor` | non            | float                                             | `1.0`                                                                                                        | Divise la `resistance` de chaque variante face aux explosions                                                                                                                                                                                                                                                                                                                                                       |
| `modelBlock`                 | non            | nom de bloc                                       | `minecraft:stone`                                                                                            | Bloc dont le modèle est emprunté quand le vôtre ne livre ni texture ni modèle propre                                                                                                                                                                                                                                                                                                                                |
| `itemModel`                  | non            | `state`, `item`                                   | `state`                                                                                                      | `state` suit l'état de bloc, `item` cherche son propre fichier, `models/item/<name>.json`                                                                                                                                                                                                                                                                                                                           |
| `tint`                       | non            | `biome`, `none`, ou une couleur hexadécimale      | aucun                                                                                                        | Nécessite un `tintindex` dans le modèle pour s'afficher                                                                                                                                                                                                                                                                                                                                                             |
| `plantTypes`                 | non            | liste de [types de plantes](#listes-de-valeurs)   | aucun                                                                                                        | Ce qui peut être planté dessus                                                                                                                                                                                                                                                                                                                                                                                      |
| `behavesAs`                  | non            | liste de `till`, `path`, `bush`, `animals`        | aucun                                                                                                        | Comportements vanilla à adopter                                                                                                                                                                                                                                                                                                                                                                                     |
| `bounds`                     | non            | liste de six nombres, 0 à 1                       | bloc entier                                                                                                  | La boîte de collision, sous la forme `[x1, y1, z1, x2, y2, z2]`                                                                                                                                                                                                                                                                                                                                                     |
| `requires`                   | non            | liste d'ids de mods ou de namespaces de packs     | aucun                                                                                                        | Le fichier est ignoré sauf si tous sont présents                                                                                                                                                                                                                                                                                                                                                                    |
| `particle`                   | torch seul     | `none`, `flame`, `colored`                        | `flame`                                                                                                      | La particule au-dessus d'une torche                                                                                                                                                                                                                                                                                                                                                                                 |
| `particleColor`              | torch seul     | couleur hexadécimale                              | `FFFFFF`                                                                                                     | Utilisée quand `particle` vaut `colored`                                                                                                                                                                                                                                                                                                                                                                            |
| `smoke`                      | torch seul     | booléen                                           | `true`                                                                                                       | Si elle fume                                                                                                                                                                                                                                                                                                                                                                                                        |
| `leafSapling`                | leaves seul    | nom de bloc                                       | aucun                                                                                                        | La pousse qu'elles laissent                                                                                                                                                                                                                                                                                                                                                                                         |
| `leafSaplingChance`          | leaves seul    | int                                               | `5`                                                                                                          | Une feuille sur N en laisse une                                                                                                                                                                                                                                                                                                                                                                                     |
| `seed`                       | crop seul      | nom d'objet                                       | `minecraft:wheat_seeds`                                                                                      | L'objet qui la plante, et ce que laisse une culture immature                                                                                                                                                                                                                                                                                                                                                        |
| `produce`                    | crop seul      | nom d'objet                                       | `minecraft:wheat`                                                                                            | Ce que donne la récolte                                                                                                                                                                                                                                                                                                                                                                                             |
| `maxAge`                     | crop seul      | int                                               | `7`                                                                                                          | Le nombre d'étapes de croissance                                                                                                                                                                                                                                                                                                                                                                                    |
| `growth`                     | plants seul    | objet                                             | aucun                                                                                                        | Voir [Croissance](#croissance)                                                                                                                                                                                                                                                                                                                                                                                      |
| `sapling`                    | sapling seul   | objet                                             | aucun                                                                                                        | Voir [Pousses](#pousses)                                                                                                                                                                                                                                                                                                                                                                                            |
| `portal`                     | portal seul    | objet                                             | aucun                                                                                                        | Voir [Portails et passages](#portails-et-passages)                                                                                                                                                                                                                                                                                                                                                                  |
| `container`                  | container seul | objet                                             | aucun                                                                                                        | Voir [Conteneurs](#conteneurs)                                                                                                                                                                                                                                                                                                                                                                                      |
| `bell`                       | bell seul      | objet                                             | aucun                                                                                                        | Voir [Cloches](#cloches)                                                                                                                                                                                                                                                                                                                                                                                            |

### Clés de variantes

*blocs*

| Clé            | Obligatoire | Valeur                               | Défaut       | Ce qu'elle fait                                                                                                                                                                                    |
| -------------- | ----------- | ------------------------------------ | ------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `hardness`     | non         | float                                | `1.0`        | Le temps qu'il faut pour le casser. L'obsidienne vaut `50`, `-1` est incassable                                                                                                                    |
| `resistance`   | non         | float                                | `5.0`        | Résistance aux explosions telle que 1.12.2 la lit : le bloc garde les trois cinquièmes du chiffre, donc `10` donne le `6` de la pierre                                                             |
| `light`        | non         | 0 à 15                               | `0`          | Lumière émise                                                                                                                                                                                      |
| `harvestLevel` | non         | 0 à 4                                | `0`          | Le niveau d'outil d'un bloc `basic`, `ore`, `container`, `portal`, `fence`, `pane`, `log`, `falling`, `slab` ou `wall`, à la place du `harvestToolLevel` du fichier. Les autres types suivent la valeur du fichier |
| `rarity`       | non         | `common`, `uncommon`, `rare`, `epic` | `common`     | Couleur du nom dans l'infobulle                                                                                                                                                                    |
| `maxSize`      | non         | 1 à 64                               | `64`         | Taille de pile                                                                                                                                                                                     |
| `tags`         | non         | liste d'ids de tags                  | aucun        | Tags de blocs et d'objets dans lesquels cette variante est écrite, comme `forge:ores/ruby` sur 1.20.1 ou `c:ores/ruby` sur 1.21.1. Les fichiers de tags sont générés pour vous                     |
| `drops`        | non         | liste de butins                      | se laisse lui-même | Ce que donne sa destruction                                                                                                                                                                  |
| `portal`       | portal seul | objet                                | celui du fichier | Le portail propre à cette variante à la place de celui du fichier, écrit comme dans [Portails et passages](#portails-et-passages). Le fichier a quand même besoin du sien                      |

**Les noms sont définitifs.** La clé d'une variante est écrite dans chaque monde sauvegardé qui la contient. La renommer plus tard transforme les blocs posés en air, à moins qu'un [renommage du registre](#renommages-du-registre) n'associe l'ancien nom au nouveau. Un fichier peut contenir autant de variantes qu'il le souhaite ; chacune est un bloc à part, et une clé `meta` d'un pack 1.12.2 est ignorée avec une note dans le journal.

### Butins

*blocs*

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

| Clé           | Obligatoire    | Valeur             | Défaut                                 | Ce qu'elle fait                                                            |
| ------------- | -------------- | ------------------ | -------------------------------------- | -------------------------------------------------------------------------- |
| `block`       | l'un des deux  | nom de bloc ou d'objet |                                    | Ce qui est lâché                                                           |
| `entity`      | l'un des deux  | nom d'entité       |                                        | Une entité libérée quand le bloc se casse, au lieu d'un objet              |
| `amount`      | non            | int ou intervalle  | `1`                                    | Combien                                                                    |
| `chance`      | non            | 0 à 100            | `100`, ou `0` quand `guaranteed` est désactivé | La fréquence à laquelle le butin se produit                        |
| `weight`      | non            | int                | `0`                                    | Au-dessus de zéro, l'entrée rejoint un groupe qui donne exactement un butin. Voir ci-dessous |
| `bonusChance` | non            | liste d'ints       | aucun                                  | Butins supplémentaires par niveau de fortune, une entrée par niveau        |
| `guaranteed`  | non            | booléen            | `true`                                 | Raccourci hérité de `chance`. Activé vaut `100`, désactivé vaut `0`        |

Chaque entrée sans `weight` est décidée indépendamment, donc un bloc qui en a trois peut toutes les lâcher, ou aucune. Donnez un `weight` aux entrées et elles cessent d'être indépendantes : elles forment un groupe dont exactement un est choisi chaque fois que le bloc se casse, les probabilités étant proportionnelles aux poids. Ci-dessus, le diamant et l'émeraude partagent un groupe à un contre quatre, donc l'un des deux sort toujours et c'est l'émeraude quatre fois sur cinq, tandis que le rubis et le charbon sont décidés séparément et que le poisson d'argent est de nouveau une affaire à part. Les objets et les entités forment des groupes séparés, donc un objet pondéré et une entité pondérée ne se font pas concurrence.

Une entrée nommant une `entity` en libère une là où se tenait le bloc, orientée au hasard, et un mob reçoit son traitement d'apparition habituel pour la difficulté locale, donc il arrive avec l'équipement et les effets qu'il aurait eus. `amount` décide combien, `chance` à quelle fréquence, `weight` la place dans le groupe des entités. Cela se produit quand le bloc se casse, quelle que soit la manière dont il s'est cassé, donc une explosion ou un piston les libère tout comme le fait une pioche. `bonusChance` et la fortune ne signifient rien pour une entité et sont ignorés.

Un butin nommant à la fois un `block` et une `entity` utilise l'entité et le dit dans le journal.

Les butins sont écrits dans une table de butin générée, `loot_tables/blocks/<name>.json` sous le namespace du pack, sauf si le pack en livre une propre à ce chemin, auquel cas c'est le fichier du pack que le bloc utilise et `drops` n'est pas lu.

### Croissance

*blocs*

Pour `crop`, `flower`, `cane` et `vine`.

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

| Clé | Requis | Valeur | Par défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `stages` | non | entier | `16` | Nombre de stades de croissance avant la maturité. Une liane tente de pousser lors d'un tick aléatoire sur ce nombre |
| `growth` | non | entier | | Une chance sur N par tick aléatoire de passer au stade suivant |
| `spread` | non | entier | `0` | Distance à laquelle elle se propage aux blocs voisins. Une liane cesse de s'étendre latéralement dès que ce nombre de lianes se trouvent à moins de deux blocs d'elle |
| `maxHeight` | non | entier | `3` | Canne et liane. Hauteur de la colonne, ou longueur sur laquelle une liane pend ; une liane à `1` ne pousse ni ne se propage |
| `soil` | non | liste de noms de blocs | l'habituel du type | Ce sur quoi elle se tient |
| `drop` | non | nom d'objet | aucun | Canne et liane. Ce qu'elle lâche une fois cassée ; une fleur se lâche elle-même |
| `dropCount` | non | entier | `1` | Canne et liane. Combien |
| `needsSky` | non | booléen | `false` | Ne pousse que là où le ciel est visible |
| `needsWater` | non | booléen | `false` | Ne pousse qu'à proximité de l'eau |
| `waterRange` | non | entier | `1` | À quelle distance cette eau peut se trouver |
| `damage` | non | booléen | `false` | Blesse tout ce qui la touche |
| `damageAmount` | non | flottant, en demi-cœurs | `1.0` | Gravité de la blessure |
| `breaksNeighbors` | non | booléen | `false` | Casse les blocs posés à côté, comme le cactus |

### Pousses

*blocs*

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin.

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

Une `structure` remplace l'arbre généré par l'un de vos modèles, ce qui permet de construire ce qu'un générateur ne sait pas faire, et rien d'autre n'est à écrire dans le bloc. Indiquez-en plusieurs sous `structures` et la pousse en choisit une à chaque croissance, de sorte qu'un bois ne soit pas toujours le même arbre :

```json
{
  "sapling": { "structure": "mypack:ruby_tree" }
}
```

| Clé | Requis | Valeur | Par défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `soil` | non | liste de noms de blocs | aucun | Ce sur quoi elle poussera |
| `stages` | non | entier | `2` | Stades de croissance avant de devenir un arbre |
| `chance` | non | entier | `7` | Une chance sur N par tick aléatoire |
| `light` | non | 0 à 15 | `9` | Niveau de lumière requis |
| `log` | non | nom de bloc | `minecraft:oak_log` | Bloc du tronc |
| `leaves` | non | nom de bloc | `minecraft:oak_leaves` | Bloc des feuilles |
| `height` | non | entier | `4` | Hauteur du tronc |
| `vines` | non | booléen | `false` | Fait pendre des lianes depuis les feuilles |
| `structure` | non | `namespace:name` | aucun | Pousse en ce modèle au lieu d'un arbre généré |
| `structures` | non | liste | aucun | Plusieurs modèles en lesquels pousser, un seul étant choisi à chaque croissance. Chaque entrée s'écrit `{ "structure": "namespace:name", "weight": 3 }`, ou un simple nom pour des chances égales. Remplace `structure` |

## Conteneurs

*blocs et objets*

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

| Réglage | Type | Par défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `rows` | entier | `3` | Nombre de rangées d'emplacements, de 1 à 9 |
| `columns` | entier | `9` | Nombre d'emplacements par rangée, de 1 à 12 |
| `lootTable` | texte | vide | Une table de butin tirée dans le bloc la première fois que quoi que ce soit atteint son contenu, qu'il s'agisse d'un joueur qui l'ouvre, d'un entonnoir, d'un comparateur ou de sa destruction, exactement comme un coffre de donjon se remplit. Un conteneur posé par un joueur ne la tire jamais. Vide, il commence vide |
| `chestModel` | booléen ou texte | `false` | S'affiche comme un coffre à couvercle qui s'ouvre, et non comme un bloc ordinaire issu de votre propre texture. `true` utilise l'apparence du coffre vanilla ; un nom de texture tel que `mypack:entity/chest/strongbox` utilise votre propre feuille de coffre, pour le bloc posé comme pour l'objet. Un bloc à modèle de coffre a aussi `opaque` à `false` par défaut, comme un coffre vanilla, si bien que la lumière n'est pas coupée au niveau du bloc et que le coffre n'est pas affiché sombre |
| `guiTexture` | texte | vide | Votre propre image de fond pour l'écran. Vide, elle est dessinée à partir de l'écran du coffre vanilla, à la taille qu'exigent les rangées et les colonnes |
| `guiWidth` | entier | aucun | Largeur de l'écran, dessiné à partir du coin supérieur gauche de cette image lue comme une feuille de 256 sur 256, obligatoire avec `guiTexture` |
| `guiHeight` | entier | aucun | Hauteur de l'écran, obligatoire avec `guiTexture` |
| `curioSlot` | texte | vide | Pour un objet uniquement : l'emplacement Curios où il peut être porté, `back`, `belt`, `body`, `charm`, `head`, `necklace`, `ring` ou tout emplacement ajouté par un autre mod. Un sac à dos prend généralement `back`. Ignoré, tout le reste de l'objet continuant de fonctionner, quand Curios n'est pas installé. La clé 1.12.2 `bauble` est lue comme celle-ci et prend les noms de Baubles : `amulet` devient `necklace`, `ring` donne deux emplacements d'anneau, `belt`, `head`, `body` et `charm` gardent leur nom, et `trinket` convient à tous ces emplacements. Tout autre nom laisse l'objet non portable, avec une ligne d'erreur |

**Neuf rangées sur douze est le plafond**, le maximum qu'un écran puisse contenir. Un pack qui en demande davantage est ramené à cette limite, avec une ligne d'erreur qui le signale. Un avertissement pour le plus haut : un écran de neuf rangées fait 276 pixels, et un écran 1080 avec l'échelle d'interface `auto` en donne 270, si bien que le haut et le bas sont rognés de trois pixels chacun ; l'échelle 3 l'affiche en entier.

**L'écran est dessiné, pas livré.** Un conteneur de neuf colonnes ou moins et de six rangées ou moins utilise tel quel l'écran du coffre vanilla, et ressemble donc exactement à un coffre de cette taille. Tout ce qui est plus grand est assemblé à partir de la même image au moment du rendu : le bord supérieur, une rangée d'emplacements répétée autant que nécessaire, puis le bas avec l'inventaire du joueur, de sorte qu'un pack peut demander des tailles qu'aucun écran vanilla ne couvre sans livrer d'image à lui. `guiTexture` outrepasse tout cela quand un pack veut son propre style ; `guiWidth` et `guiHeight` doivent alors en donner la taille, faute de quoi l'écran dessiné est utilisé et une ligne d'erreur le signale.

**Ce que fait le bloc.** Il conserve son contenu à travers une sauvegarde et un rechargement, le lâche quand on le casse, répond à un comparateur selon son remplissage, et garde les rangées et les colonnes avec lesquelles il a été créé : modifier celles-ci plus tard dans le pack laisse donc les conteneurs déjà présents dans un monde tels qu'ils étaient. Un objet conteneur ou un bloc conteneur ne peut pas être placé dans un conteneur. `chestModel` lui donne aussi le son d'ouverture du coffre et l'animation du couvercle ; désactivé, le bloc est dessiné à partir de sa propre texture comme n'importe quel autre bloc, ce qui permet de faire une caisse, un tonneau ou une armoire.

**Colorer un coffre.** La feuille du coffre est une texture ordinaire : une carte de pixels peut donc recolorer celle de vanilla sans dessiner un seul pixel. Faites-la `extends` et donnez-lui un `tint`, puis nommez cette carte dans `chestModel`.

```json
{
  "extends": "minecraft:textures/entity/chest/normal",
  "tint": {
    "from": "#241A12",
    "to": "#D8BC80"
  }
}
```

**Un objet conteneur est une bourse** : un objet de type `container` portant le même bloc `container` avec `rows` et `columns` ; on l'ouvre par un clic droit, et il garde son contenu quand il change de mains. Sans l'objet `container`, il contient une seule rangée de neuf. Donnez-lui `curioSlot` et, là où Curios est installé, il se place dans cet emplacement et une touche l'ouvre sans l'enlever, `V` par défaut, modifiable sous Resource Data Pack Loader dans les contrôles. Appuyer de nouveau, alors qu'un conteneur porté est déjà ouvert, passe au suivant parmi ceux que vous portez et reboucle, de sorte que plusieurs conteneurs portés en même temps sont tous accessibles. La touche n'apparaît que lorsque Curios est présent, et tout le reste de l'objet, le clic droit et son inventaire, fonctionne qu'il le soit ou non. La possibilité de porter la bourse est écrite pour vous dans le tag Curios de cet emplacement.

**La table de butin se remplit à la première utilisation**, et non à la pose du bloc, ce qui la rend utile dans une structure : le premier qui l'ouvre obtient le tirage, et un entonnoir ou un comparateur qui l'atteint en premier la tire tout aussi bien. La même table peut servir pour `lootTable` sur une forme d'empreinte ou une parcelle de village, de sorte qu'un pack peut placer ces conteneurs par le worldgen et les garnir de la même manière.

## Cloches

*blocs*

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

Et son blockstate, `assets/mypack/blockstates/village_bell.json`, indexé sur `attachment` et `facing` :

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

| Réglage | Type | Par défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `swing` | booléen | `true` | Dessine la partie oscillante à partir d'un fichier de modèle distinct et la fait osciller quand la cloche sonne. `false` dessine toute la cloche à partir des modèles du blockstate, sans rien d'animé |
| `sound` | nom de son | `minecraft:block.note_block.bell` | Joué quand la cloche sonne. Vide, elle sonne en silence |
| `resonateSound` | nom de son | `minecraft:block.note_block.chime` | Joué quand la cloche résonne parce que des pillards sont proches. Vide, elle résonne en silence |

**Elle est suspendue comme la cloche du jeu.** Posée sur un bloc, elle repose sur le sol, orientée dans la direction où vous regardez ; sous un bloc, elle pend au plafond ; contre un mur, elle pend à ce mur, et entre deux murs quand le côté opposé est lui aussi plein. Elle tombe quand ce qui la retient disparaît, et une cloche entre deux murs devient une cloche à un seul mur quand l'un d'eux disparaît. Sa boîte de collision suit celle de vanilla pour chacun des quatre cas, donc `bounds` n'est pas lu.

**Ce qui la fait sonner.** Une utilisation sur le côté du corps, sous la poutre : pour une cloche posée au sol, sur les deux faces que traverse sa poutre ; pour une cloche murale, sur les deux faces voisines du mur ; pour une cloche de plafond, sur n'importe quel côté. Le dessus, le dessous et tout ce qui est au-dessus du corps ne font rien. Un signal de redstone la fait sonner une fois à son activation, et une flèche, une boule de neige ou tout autre projectile la fait sonner quand il frappe un côté qu'une main aurait pu atteindre. Le corps s'écarte du côté frappé pendant deux secondes et demie ; la redstone le fait osciller dans le sens où la cloche est orientée.

**Ce que fait une sonnerie.** Les villageois situés à moins de 32 blocs l'entendent et courent se cacher chez eux pendant quinze secondes, comme le fait la cloche du jeu. Quand un pillard se trouve à moins de 32 blocs, la cloche résonne un quart de seconde après la sonnerie, et deux secondes plus tard chaque pillard situé à moins de 48 blocs luit pendant trois secondes, avec des particules colorées à côté de la cloche, du côté où se tient chacun. Un pillard est tout ce qu'envoie un [raid](#raids), ainsi que les illageois et les sorcières du jeu. Une cloche de ce type est une cloche de village pour tous les raids : elle sonne à l'arrivée de chaque vague sans être nommée dans le `bell` du raid.

**Les modèles.** Une cloche est indexée par `attachment` et `facing`, soit seize états au total, et `powered` n'apparaît pas dans les clés. Avec `swing` activé, ces modèles ne dessinent que le cadre, et la partie oscillante est un fichier de modèle distinct, `<namespace>:block/<name>_body` : le mod le charge par ce chemin et le moteur de rendu le dessine, donc son nom n'est pas à votre choix et il n'apparaît jamais dans le blockstate. Il est modélisé dans l'espace du bloc, là où repose le corps, et s'incline autour du point situé à un demi-bloc vers l'intérieur et aux trois quarts d'un bloc en hauteur, comme celui de vanilla. Avec `swing` désactivé, il n'y a pas de modèle de corps, et les seize modèles de cadre dessinent la cloche entière. Le modèle d'objet écrit pour la main dessine le cadre et le corps ensemble, de sorte que la cloche apparaît entière en main ; livrez `models/item/<name>.json` pour la dessiner autrement.

**L'oscillation est dessinée par le client.** Une sonnerie parvient aux joueurs sous forme d'événement de bloc : un serveur dédié fait donc osciller la cloche pour tous ceux qui ont ce mod, et un joueur qui ne l'a pas ne fait qu'entendre la cloche. Les sons, la résonance et la lueur se produisent tous côté serveur.

## Modèles, états de bloc et textures

*blocs et objets*

Définir un bloc ou un objet l'enregistre. Son *apparence* est un ensemble de fichiers de ressources placés dans les mêmes dossiers et au même format que ceux du jeu, sous votre propre namespace, et sur cette version la plupart sont écrits pour vous.

```
assets/mypack/textures/block/ruby_ore.png
assets/mypack/textures/item/ruby.png
assets/mypack/lang/en_us.json
```

**Livrez une texture et le reste est généré.** Pour chaque bloc dont le pack ne livre pas le blockstate, le mod écrit le blockstate et les modèles dont le type a besoin, en pointant vers `textures/block/<name>.png` où `<name>` est la clé de la variante, et pour chaque objet qui n'a pas de `models/item/<name>.json`, un modèle d'objet pointant vers `textures/item/<name>.png`. Un bloc qui n'a ni texture ni modèle propre emprunte l'apparence de `modelBlock`, la pierre par défaut, de sorte que rien ne s'affiche jamais comme le carré violet et noir. Livrez votre propre `blockstates/<name>.json` et le mod ne génère plus rien pour ce bloc et utilise le vôtre ; il en va de même pour `models/item/<name>.json`.

| Type | Fichiers de texture recherchés | Généré à partir de |
| --- | --- | --- |
| `basic`, `ore`, `falling` | `<name>`, avec `<name>_top` et `<name>_bottom` pour les faces du haut et du bas quand le pack les livre | `cube_all`, ou `cube_bottom_top` quand une texture de dessus ou de dessous est présente |
| `flower`, `sapling`, `cane`, `leaves`, `container` sans coffre | `<name>` | `cube_all`, `cross` ou `leaves` |
| `log` | `<name>` pour le côté, `<name>_top` pour les extrémités | `cube_column` |
| `slab` | `<name>` | `slab`, `slab_top` et un double `cube_all` |
| `stairs` | `<name>` | `stairs`, `inner_stairs`, `outer_stairs`, les quarante états écrits en entier |
| `fence` | `<name>` | `fence_post` et `fence_side` en multipart, et `fence_inventory` pour la main |
| `wall` | `<name>` | les modèles de poteau et de côté du muret en multipart, et `wall_inventory` pour la main |
| `pane` | `<name>` pour la vitre, `<name>_top` pour la tranche | les cinq modèles de vitre en multipart |
| `door` | `<name>_top` et `<name>_bottom`, ou `<name>` pour les deux | les huit modèles de porte et leurs trente-deux états |
| `trapdoor` | `<name>` | les trois modèles de trappe orientable |
| `fence_gate` | `<name>` | les quatre modèles de portillon, fermé et ouvert, dans un mur et hors d'un mur |
| `ladder`, `vine`, `torch` | `<name>` | le modèle propre du jeu pour chacun |
| `bell` | `<name>` | les cadres de cloche du jeu sous les noms `<name>_floor`, `<name>_ceiling`, `<name>_wall` et `<name>_between_walls`, le `pack_bell_body` du mod sous le nom `<name>_body`, et un blockstate de seize états `attachment` et `facing` par-dessus |
| `crop` | `<name>_stage0` jusqu'à `<name>_stage<maxAge>`, ou `<name>` pour tous | un modèle `crop` par stade, `age=0` à `7` y étant associé |
| `portal` | `<name>`, ou celle du portail du Nether | `cube_all` pour un bloc `fullCube`, comme l'est un bloc de portail sur 1.12.2 ; trois dalles de portail, une par axe, pour celui qui ne l'est pas, comme le portail de cadre d'une dimension |
| `banner` | sa propre feuille, voir [Bannières](#bannières) | le modèle de bannière du jeu |
| `container` avec `chestModel` | la feuille de coffre nommée dans `chestModel` | le modèle `pack_chest` du mod |

Chaque texture est recherchée sous `textures/block/`, et le nom est la clé de la variante : un bloc enregistré sous `ruby_ore` attend donc `textures/block/ruby_ore.png` et rien d'autre n'est à écrire. Un bloc dont l'objet est dessiné à plat, une porte, une échelle, une torche, une pousse, une fleur, une canne, une liane ou une vitre, prend `textures/item/<name>.png` pour la main quand il existe, et sa texture de bloc sinon.

**Les objets** prennent `textures/item/<name>.png` et un modèle `item/generated` généré, ou `item/handheld` pour un outil. Livrez `models/item/<name>.json` pour les dessiner autrement.

**Les fluides** n'ont besoin d'aucun modèle ; il est généré à partir des textures `still` et `flow`.

**Un bloc à plusieurs variantes est en réalité plusieurs blocs.** Chaque clé sous `variants` est enregistrée séparément, et possède donc son propre blockstate, ses propres modèles et ses propres textures, nommés d'après la clé. Il n'existe pas de blockstate partagé portant une propriété `blocks`, et rien dans un blockstate n'a besoin de dire de quelle variante il s'agit : `blockstates/ruby_ore.json` est celui du minerai de rubis, et `blockstates/deep_ruby_ore.json` celui de la version profonde.

### Écrire les vôtres

*modèles, états de bloc et textures*

Tout ce qui est généré peut être remplacé. Un blockstate livré par le pack est utilisé tel quel, au format propre du jeu : les `variants` vanilla indexés sur les propriétés du bloc, ou `multipart`. Les propriétés sont celles du jeu pour chaque type : `axis` sur une bûche, `type` sur une dalle, `facing`, `half` et `shape` sur des escaliers, `facing`, `half`, `hinge` et `open` sur une porte, `facing`, `half` et `open` sur une trappe, `facing`, `in_wall` et `open` sur un portillon, `age` sur une culture et une canne, `stage` sur une pousse, `north`, `east`, `south`, `west` sur une barrière ou une vitre, avec `up` en plus sur un muret et une liane, `rotation` sur une bannière sur pied et `facing` sur une bannière murale, `axis` sur un portail, `attachment` et `facing` sur une cloche, `powered` étant laissé hors des clés. Un bloc `basic`, `ore`, `falling`, `leaves`, `flower` ou `container` n'a qu'un seul état, de clé `""`.

Faites pointer les modèles vers les parents qui prennent des textures, non vers les modèles vanilla finis : `cube_all` prend un `all` ; `cube_column` un `end` et un `side` ; `cross` un `cross` ; les parents des escaliers `bottom`, `top` et `side` ; `fence_post` et `fence_side` une `texture` ; `template_wall_post` et `template_wall_side` un `wall` ; les modèles de vitre un `pane` et un `edge` ; les parents de porte un `top` et un `bottom` ; `template_orientable_trapdoor_*` et `template_fence_gate*` une `texture` ; `template_torch` un `torch` ; `crop` un `crop` ; `vine` et `ladder` leur propre nom. Un modèle qui nomme un modèle vanilla fini tel que `oak_door_bottom_left` en hérite aussi les textures, quoi que dise le blockstate.

### Bannières

*modèles, états de bloc et textures*

Une bannière est le seul type où la forme du bloc et la forme du modèle divergent, ce qui justifie de l'exposer en détail.

**Elle enregistre deux blocs.** Une seule définition vous donne la bannière sur pied sous votre propre nom et un second bloc nommé `<name>_wall` pour la bannière suspendue. Les deux ont besoin d'un blockstate ; seule la bannière sur pied reçoit un objet, et cet objet décide lequel des deux il place : la bannière sur pied quand vous cliquez sur le dessus d'un bloc, la murale quand vous cliquez sur un côté. Vous ne placez jamais directement le bloc mural, et il n'a besoin d'aucun objet propre.

**La bannière sur pied tourne en seizièmes.** Sa propriété est `rotation`, de `0` à `15`, car une bannière tourne par seizièmes et non par quarts. Le `y` d'un blockstate n'accepte que 0, 90, 180 et 270 : chaque rotation pointe donc vers un petit modèle distinct qui prend votre modèle de bannière pour parent et le fait tourner avec un `transform` :

```json
{
  "variants": {
    "rotation=0": { "model": "mypack:block/my_banner_rotation_0" },
    "rotation=1": { "model": "mypack:block/my_banner_rotation_1" }
  }
}
```

```json
{
  "parent": "mypack:block/my_banner",
  "transform": { "rotation": { "y": -22.5 }, "origin": "center" }
}
```

… et ainsi de suite jusqu'à `15`, chacun tournant de `-22.5` degrés de plus. Le signe correspond à celui des bannières du jeu, qui tournent de moins la rotation. Construisez le modèle orienté vers le sud, puisque c'est là que pointe une bannière posée par un joueur qui regarde vers le sud. Le bloc mural est un blockstate ordinaire avec les quatre entrées `facing` habituelles à 0, 90, 180 et 270, puisqu'il n'a rien de fractionnaire. Le blockstate Forge d'un pack 1.12.2 est converti exactement en ceci lors de la conversion du pack.

**Le modèle fait presque deux blocs de haut.** Une bannière occupe un bloc pour la pose et la collision, mais elle est dessinée bien au-delà, et un modèle qui s'arrête en haut de son propre bloc paraît rabougri. Les proportions de vanilla, en seizièmes de bloc, valent la peine d'être copiées à l'identique :

| Partie | De | À |
| --- | --- | --- |
| Poteau | `0` | `28` |
| Barre transversale | `28` | `29.33` |
| Tissu | `2.67` | `29.33` |
| Largeur du tissu | `1.33` | `14.67` |
| Tissu mural | `-13` | `13.67` |

Une bannière sur pied monte donc jusqu'à `29.33`, presque deux blocs, et une bannière murale pend de treize seizièmes *sous* le bloc qui la tient. Les éléments d'un modèle peuvent s'étendre de `-16` à `32`, donc les deux tiennent. La version murale n'a ni poteau ni barre transversale, seulement du tissu.

**Le tissu est deux fois plus haut que large, et votre texture doit l'être aussi.** Cette face mesure `13.33` sur `26.67`. Plaquez-y une texture carrée et le motif est écrasé de moitié en hauteur. Une texture de bloc ne peut pas elle-même être deux fois plus haute que large, car tout ce qui n'est pas carré est lu comme une animation ; la solution est donc une feuille carrée plus grande dont le tissu n'occupe qu'une partie : un fichier de 32×32 contenant le tissu sous forme d'une région de 16×32, adressée par `"uv": [0, 0, 8, 16]`, avec les bandes du poteau et de la barre transversale dans l'espace à côté. Les coordonnées UV vont toujours de 0 à 16 quelle que soit la résolution du fichier, si bien que les mêmes nombres valent pour toutes les tailles.

**Son objet veut un modèle à lui.** Un objet qui hérite d'un modèle aussi haut dépassera de son emplacement à l'échelle habituelle d'un bloc ; donnez donc à `models/item/<name>.json` son propre bloc `display`, avec l'échelle réduite et le tout translaté pour rentrer dans le cadre.

**Sans blockstate, elle est dessinée à partir d'une feuille.** Une bannière dont le pack ne livre aucun blockstate en reçoit un généré, et le moteur de rendu de bannières du jeu la dessine selon la forme de la bannière vanilla à partir de la feuille située à `textures/entity/banner/<name>.png`, organisée comme celle de la bannière vanilla. C'est un ajout de cette version ; les blocs sur pied et mural en décident chacun par leur propre blockstate.

**Elle n'a ni couleurs ni motifs.** Une bannière de pack ne porte pas de liste de couches comme les bannières vanilla. Le dessin est la texture, de la même façon que l'apparence d'une porte est sa texture, et une définition donne une bannière. La teindre et y empiler des motifs n'est pas à la portée d'un pack.

**Elle prend le `material` que vous lui donnez.** Une bannière de pierre se mine à la pioche comme la pierre qu'elle prétend être.

### Textures écrites sous forme de cartes de pixels

*modèles, états de bloc et textures*

Une texture peut être un fichier JSON plutôt qu'un PNG. Placez-le là où irait le PNG, avec `.json` ajouté à la fin du nom entier : `textures/block/panel.png.json` répond donc à toute demande de `textures/block/panel.png`. Rien d'autre ne change : les modèles pointent vers `mypack:block/panel` comme toujours, et l'atlas, les mipmaps et un `.mcmeta` d'animation fonctionnent tous, car ce que reçoit le jeu reste un PNG. Le pack d'exemple ne livre pas un seul PNG ; chacune de ses textures est une carte.

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

| Clé | Requis | Valeur | Par défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `size` | oui, ou hérité | `widthxheight` | | Nombre de pixels en largeur et en hauteur |
| `rows` | oui, ou hérité | liste de textes | | Une chaîne par rangée de pixels, un caractère par pixel, de haut en bas |
| `palette` | oui, ou hérité | objet | | Un caractère associé à une couleur, `#RRGGBB` ou `#AARRGGBB` |
| `extends` | non | une autre carte de pixels | | La carte dont celle-ci part |
| `tint` | non | objet avec `from` et `to` | | Recolore tout ce qui est hérité le long d'un dégradé entre deux couleurs |
| `notes` | non | objet | | Un caractère associé à une ligne disant à quoi il sert, héritée et jamais dessinée |

**Il n'y a pas de nom à déclarer.** Le chemin du fichier est son nom, exactement comme pour un PNG : une carte située à `assets/mypack/textures/block/panel.png.json` est `mypack:block/panel` dans un modèle, et une carte située à `assets/mypack/textures/item/gem.png.json` est `mypack:item/gem` dans un modèle d'objet. Rien ne pointe spécialement vers une carte de pixels ; un bloc ou un objet nomme sa texture comme il l'a toujours fait et ne sait jamais laquelle des deux il a reçue. Cela signifie aussi que les dossiers de blocs et d'objets restent séparés, comme pour les PNG : `textures/block/gem.png.json` et `textures/item/gem.png.json` sont deux textures différentes, mises en cache dans deux fichiers différents.

**Toutes les tailles que vous voulez**, jusqu'à 4096 de côté, les deux côtés n'ayant pas besoin d'être égaux. `16x16` est une face de bloc ordinaire, `16x32` est le genre de bande haute dont une moitié de porte ou une animation a besoin. La taille est vérifiée et non devinée : donnez une ligne par rangée de pixels et un caractère par pixel en largeur, sinon la carte est refusée et le journal indique la rangée et ce qu'il y a trouvé. Un caractère sans couleur dans la palette est laissé transparent, si bien que `.` ou une espace fait un trou.

**Les modèles sont tout l'intérêt du système.** `extends` nomme une autre carte de pixels, sous la forme `namespace:path` ou d'un simple chemin dans le même pack, et le fichier qui l'étend hérite de son `size`, de ses `rows` et de sa `palette`. Tout ce qu'il nomme lui-même l'emporte, et il n'a pas besoin de tout nommer : une variante entière peut donc se réduire à une poignée de couleurs :

```json
{
  "extends": "mypack:textures/block/panel.png",
  "palette": { "s": "#AA7EB1", "d": "#8B6292", "e": "#6B4A72", "p": "#C5A1CB" }
}
```

C'est une seconde texture complète : la même forme en purpur, et si la forme est un jour redessinée dans le modèle, toutes les variantes suivent. Une variante peut à la place donner ses propres `rows` et garder la palette du modèle, ce qui est l'inverse : les mêmes couleurs dans un autre motif. L'héritage va jusqu'à huit niveaux de profondeur, une boucle est détectée et signalée, et une carte qui nomme un modèle que rien ne fournit est signalée au lieu d'être dessinée vide.

**Lequel de deux textures est le modèle** se décide par celle qui contient le plus de distinctions, non par celle qui a été dessinée en premier. Une variante donne une couleur à chaque caractère : tous les pixels que le modèle désigne par le même caractère ressortent donc de la même couleur dans la variante. Un minerai dessiné sur de la pierre ne peut donc pas hériter des `rows` de la pierre : la pierre désigne les positions des mouchetures comme de la pierre ordinaire, et rien de ce qu'une variante peut écrire ne scinde un caractère en deux. Retournez la chose et cela fonctionne. Prenez le minerai pour modèle, de sorte que les tons de la pierre et ceux du minerai aient chacun leurs propres caractères, et un second minerai se réduit à quatre couleurs :

```json
{
  "extends": "mypack:textures/block/ore_template",
  "palette": { "4": "#768291", "5": "#5E6977", "6": "#66717F", "7": "#848F9D" }
}
```

Une variante qui veut vraiment un motif différent donne ses propres `rows`, comme plus haut, et n'hérite alors que de la palette. Cela vaut la peine quand les couleurs sont l'essentiel et la forme accessoire ; quand c'est la forme qui compte, mettez la forme dans le modèle et laissez les variantes nommer des couleurs.

**Un modèle n'a pas besoin d'être une texture.** Une carte n'est servie au jeu que si son chemin se termine par `.png` : un modèle situé à `textures/block/ore_template.json` est donc invisible pour le jeu et n'existe que pour être étendu, alors qu'un modèle situé à `textures/block/ore_template.png.json` répondrait aussi aux demandes de `ore_template.png`. Nommez une forme partagée sans le `.png` et personne ne pourra la demander par accident.

**Un modèle peut être une vraie image plutôt qu'une carte.** Faites pointer `extends` vers un PNG fourni par n'importe quel pack ou par le jeu lui-même et la palette change de sens : les clés deviennent les couleurs déjà présentes dans cette image, les valeurs les couleurs à mettre à leur place. Rien n'est tracé et aucune `rows` n'est écrite, de sorte qu'un pack peut recolorer une texture de vanilla ou d'un mod sur place :

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

C'est un minerai de rubis dans la pierre même de vanilla : les quatre tons des mouchetures sont échangés et tous les autres pixels restent tels quels. Une couleur que l'image ne contient pas ne correspond tout simplement jamais, et la taille vient de l'image à moins que vous n'en nommiez une, qui doit alors concorder.

`extends` préfère une carte de pixels : il cherche d'abord la carte à ce chemin et ne se rabat sur l'image que si aucun pack n'en fournit. Un nom qui n'est ni l'un ni l'autre est signalé au lieu d'être dessiné vide. S'appuyer sur une image est un travail côté client, puisque ce sont les ressources du jeu lui-même qui sont lues, et un serveur dédié ne le fait donc jamais.

**Un modèle peut être teinté plutôt que repeint.** `tint` nomme deux couleurs et recolore tout ce que la carte hérite le long du dégradé qui les sépare. La luminosité de chaque couleur héritée est sa place sur ce dégradé : le noir tombe sur `from`, le blanc sur `to`, et chaque ton intermédiaire est mélangé proportionnellement. La transparence est laissée telle quelle. Un modèle en niveaux de gris plus deux couleurs forment donc une variante complète :

```json
{
  "extends": "mypack:textures/item/materials/ingot.png",
  "tint": { "from": "#626669", "to": "#DBDFE2" }
}
```

`from` peut être omis, auquel cas c'est le noir et la teinte devient une multiplication ordinaire, de même forme qu'un `tintindex` au moment du rendu. La différence est que celle-ci est dessinée une fois dans le PNG puis mise en cache : elle ne coûte donc rien par image et atteint une texture que rien ne teinte, mais elle ne peut pas non plus suivre un biome comme le peuvent `grass` ou `foliage`.

Le modèle reste une carte ordinaire : ouvrez-le, regardez-le, et il s'affiche comme le gris qu'il est. Les deux couleurs acceptent `#RRGGBB`, `#AARRGGBB` ou un `0x` initial, et une valeur qui n'est aucun de ces formats laisse la carte non dessinée plutôt que de la dessiner dans la mauvaise couleur. Une teinte s'hérite comme tout le reste et la première rencontrée dans la chaîne l'emporte : la teinte propre à une variante prime donc sur celle de ce qu'elle étend. Elle fonctionne aussi sur un modèle d'image, où elle s'exécute après les échanges de couleurs de la palette.

**Une teinte est un dégradé entre deux couleurs**, elle ne convient donc qu'à une texture dont les tons s'y trouvent. Une forme à deux régions sans rapport, la pierre d'un minerai et ses mouchetures, n'en est pas une, et demande que sa palette soit écrite en entier.

**Savoir ce que signifient les caractères d'un modèle** est la partie ingrate quand on en étend un, et c'est à cela que sert le bloc `notes` ci-dessus : un caractère associé à une courte ligne, héritée comme l'est la palette et jamais dessinée. Étiquetez les caractères d'un modèle et quiconque l'étend sait lesquels surcharger.

`/rdpl pixelmap <namespace:path>` indique alors ce qu'une carte est réellement devenue, ce qui est le moyen fiable d'écrire une variante sans ouvrir tous les fichiers de la chaîne :

```
oretest:textures/block/ruby_ore.png is 16x16
  built from oretest:textures/block/ruby_ore.png.json
  built from oretest:textures/block/gem_ore.png.json
  rows come from oretest:textures/block/gem_ore.png.json
  1  #C4353F  17 pixel(s)  set by ruby_ore.png.json  ore body, most of every lump
  2  #8E2029   8 pixel(s)  set by ruby_ore.png.json  ore shadow, the darkest tone
  a  #747474  86 pixel(s)  set by gem_ore.png.json   stone, the commonest tone
```

Chaque caractère est listé avec sa couleur, le nombre de pixels qu'il couvre, le fichier de la chaîne qui l'a défini et ce que ce fichier dit de son rôle. Le chemin peut être donné sous la forme courte, `mypack:block/panel`, ou en entier. Un caractère affichant 0 pixel est un caractère que la palette nomme et que les rangées n'utilisent jamais, ce qui est généralement une faute de frappe dans une rangée.

**Les images dessinées sont conservées sur le disque** dans `rdploader/pixelmap-cache`, sous un dossier par namespace et nommées d'après la texture, avec en fin de nom un hash de leur source. Le hash couvre toute la chaîne, la carte elle-même et chaque modèle au-dessus d'elle : modifier un modèle change donc l'empreinte de toutes les variantes qui en héritent, et elles sont toutes redessinées. Quand une carte est redessinée, ses anciens fichiers sont balayés.

Le dossier est aussi parcouru à chaque analyse des packs, et toute image dont aucun pack ne fournit plus la carte est supprimée, de même que tout dossier resté vide. Renommez une texture, retirez un pack, supprimez une carte, et son image en cache disparaît avec elle au lieu de rester là pour toujours. Supprimer tout le dossier ne coûte que le temps de tout redessiner, et il est ignoré lors de l'analyse des packs, de sorte qu'il n'est jamais pris pour un pack.

Un PNG l'emporte toujours. Si `panel.png` et `panel.png.json` existent tous deux, le PNG est servi et la carte n'est jamais dessinée : une texture générée peut donc être remplacée plus tard par une texture peinte sans rien changer à ce qui y pointe.

**Personne n'a à écrire ces fichiers à la main.** Le dépôt fournit des scripts pour tout l'aller-retour dans `pixelmap/` : `png_to_pixelmap.py` transforme un PNG en carte, `convert_pack.py` le fait pour chaque texture d'un pack, et `verify_pack.py` dessine les cartes d'un pack converti et les compare aux PNG dont elles proviennent, de sorte qu'une conversion puisse être vérifiée avant de mettre les originaux de côté.

### Pièges à connaître

*modèles, états de bloc et textures*

**Un modèle qui nomme un modèle vanilla fini en hérite aussi les textures.** `torch`, `ladder`, `oak_door_bottom_left` et `wheat_stage0` portent tous leurs propres textures : un modèle qui pointe vers l'un d'eux obtient donc l'apparence de vanilla, quoi que vous mettiez à côté. Les modèles parents tels que `cube_all`, `cross` et `crop` prennent leurs textures du modèle qui les nomme et se comportent bien, ainsi que les modèles de porte, de trappe et de portillon.

**Les noms viennent du fichier de langue.** Un bloc ou un objet affiche sa clé brute tant que `lang/en_us.json` ne lui en donne pas une, et les clés sont celles du jeu : `block.mypack.ruby_ore` pour un bloc et l'objet qui le place, `item.mypack.ruby` pour un objet, `itemGroup.mypack.tab` pour un onglet créatif, `fluid_type.mypack.molten_ruby` et `fluid.mypack.molten_ruby` pour un fluide, `effect.mypack.ruby_sight` pour un effet de potion, `entity.mypack.angry_cow` pour une variante d'entité, `biome.mypack.ruby_forest` pour un biome. Un seul nom chacun ; rien sur cette version n'exige qu'une clé soit écrite deux fois.

## Faire en sorte que vanilla traite correctement votre bloc

*blocs et objets*

Vanilla vérifie l'identité de ses propres blocs à une douzaine d'endroits : un bloc de pack qui devrait manifestement fonctionner ne le fait donc souvent pas. Deux clés y remédient.

```json
{
  "material": "ground",
  "plantTypes": ["plains", "crop"],
  "behavesAs": ["till", "path"],
  "variants": { "ruby_grass": { "hardness": 0.6 } }
}
```

**`plantTypes`** liste les types de plantes que votre bloc accepte, de sorte que pousses, cultures et fleurs puissent y être plantées : `plains`, `desert`, `beach`, `cave`, `water`, `nether` et `crop`. **1.21.1** n'a aucun type de plante ; la liste de sols de la plante décide seule, et la clé est lue puis ignorée.

**`behavesAs`** fait traiter votre bloc par vanilla comme l'un des siens :

| Valeur | Ce que ça fait |
| --- | --- |
| `till` | Une houe le transforme en terre labourée, ou en ce que nomme `hoeTillsInto` dans la configuration |
| `path` | Une pelle le transforme en chemin en terre, ou en ce que nomme `shovelPathBecomes` |
| `bush` | Fleurs, herbe et pousses peuvent y être plantées et y rester, comme sur la terre. Équivaut à `plains` dans `plantTypes` |
| `animals` | Les animaux y apparaissent à la lumière, comme sur l'herbe |

## Objets

*blocs et objets*

`<namespace>/items/*.json`

Chaque clé à l'intérieur de `variants` est un objet, enregistré sous le namespace du pack : un fichier contenant `ruby_apple` et `dried_ruby_apple` enregistre donc `mypack:ruby_apple` et `mypack:dried_ruby_apple` ; le nom du fichier lui-même ne sert qu'à regrouper. Le modèle de chacun est généré à partir de `textures/item/<name>.png`, à moins que le pack ne livre `models/item/<name>.json`.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin. Une clé marquée pour un type n'est lue que par ce type.

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
  "rolls": "d6",
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

### Types d'objets

*objets*

| Type | Ce que vous obtenez |
| --- | --- |
| `basic` | Un objet simple. Utilisé quand `type` est absent |
| `food` | Se mange, avec faim et saturation |
| `drink` | Se boit plutôt que se mange, et rend un conteneur vide |
| `tool` | Pioche, hache, pelle, houe ou épée à partir d'un matériau |
| `armor` | Casque, plastron, jambières ou bottes à partir d'un matériau |
| `seed` | Plante l'une de vos cultures |
| `potion` | Applique vos effets de potion à l'utilisation |
| `potion_bottle` | Contient vos types de potions, et les montre dans un onglet créatif |
| `container` | Une bourse : un inventaire porté en main, ou sur soi, voir [Conteneurs](#conteneurs) |

Un `potion_bottle` liste ce qu'il peut contenir avec `potionTypes`, un tableau de noms de types de potions tel que `["mypack:ruby_tonic"]`. Un objet dont la liste est vide n'enregistre rien, et le journal le signale.

### Clés de fichier d'objet

*objets*

| Clé | Requis | Valeur | Par défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `variants` | oui | objet associant nom de variante et variante | | Un objet par entrée. La clé est son nom de registre, et nomme son modèle, sa texture et sa clé de langue |
| `type` | non | l'un des types ci-dessus | `basic` | Le type que prend l'objet |
| `creativeTab` | non | nom d'onglet | aucun | L'onglet où il apparaît, voir [Onglets créatifs](#onglets-créatifs) |
| `material` | tool, armor | nom de matériau | aucun | Lequel de vos matériaux le compose |
| `toolClass` | tool | `pickaxe`, `axe`, `shovel`, `sword` | aucun | De quel outil il s'agit. Une `sword` est un outil de 1.12.2 et non une épée vanilla : elle frappe à 3 plus les `damage` du matériau, mine les blocs dont le `harvestTool` est `sword`, accepte les enchantements de minage, et ne fait ni attaque balayée ni coupe les toiles d'araignée |
| `slot` | armor | `head`, `chest`, `legs`, `feet` | aucun | Où il se porte. `helmet`, `chestplate`, `leggings` et `boots` fonctionnent aussi |
| `eat` | food | booléen | `false` | Utilise l'animation de repas |
| `alwaysEdible` | food | booléen | `false` | Peut se manger avec une barre de faim pleine |
| `useDuration` | non | entier, en ticks | `32` | Temps que prend son utilisation |
| `attackSpeed` | non | flottant | adapté à la classe d'outil | Pour `tool`, l'attribut de vitesse d'attaque, celui d'une épée étant `-2.4` |
| `cooldown` | non | entier, en ticks | `0` | Pour `food`, `drink` et `potion`, durée pendant laquelle l'objet refuse une nouvelle utilisation après avoir été consommé ; pour un objet avec `rolls`, le délai entre deux lancers |
| `container` | drink | nom d'objet | aucun | Ce qui reste, comme une fiole. Sur un objet `container`, cette clé désigne à la place les réglages propres de la bourse, voir [Conteneurs](#conteneurs) |
| `crop` | seed | nom de bloc | aucun | La culture qu'il plante |
| `soil` | seed | nom de bloc | `minecraft:farmland` | Ce sur quoi il peut être planté |
| `rolls` | non | `coin`, `d6`, `2d6+1`, un dé ou un paquet | aucun | Pour un objet simple, un clic droit lance comme le ferait `/rdplserver game` et l'annonce au public par défaut du pack. Voir [Dés et paquets](#dés-et-paquets) |
| `requires` | non | liste d'ids de mods ou de namespaces de packs | aucun | Le fichier est ignoré à moins que tous soient présents |

### Clés de variantes d'objet

*objets*

| Clé | Requis | Valeur | Par défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `maxSize` | non | 1 à 64 | `64` | Taille de la pile |
| `rarity` | non | `common`, `uncommon`, `rare`, `epic` | `common` | Couleur du nom dans l'infobulle |
| `healAmount` | food | entier, en demi-cuisses | `0` | Faim restaurée |
| `saturation` | food | flottant | `0.0` | Saturation restaurée |
| `tags` | non | liste d'ids de tags | aucun | Tags d'objets dans lesquels cette variante est écrite ; les fichiers de tags sont générés pour vous |
| `potion` | food, drink | `potion,duration,amplifier` | aucun | Un effet appliqué quand la variante est mangée ou bue. Une quatrième partie, `true`, le rend ambiant. Un effet bénéfique est nommé en vert dans l'infobulle, suivi du niveau en chiffres romains quand il dépasse 0, et aucun effet qu'elle donne n'affiche de particules |

## Fluides

*blocs et objets*

`<namespace>/fluids/*.json`

Le chemin du fichier est le nom de registre du bloc du fluide. `name` nomme le fluide lui-même, son seau et ses clés de langue, et vaut le chemin du fichier sauf si le fichier le définit.

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

| Clé | Requis | Valeur | Par défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `name` | non | chaîne | le nom du fichier | Le nom de registre du fluide et de son seau (`<name>_bucket`) ; le bloc garde le chemin du fichier |
| `still` | non | chemin de texture | eau vanilla immobile | Texture du fluide immobile |
| `flow` | non | chemin de texture | eau vanilla courante | Texture du fluide courant |
| `color` | non | couleur hexadécimale | aucun | Teinte appliquée à ces textures. Sur les textures d'eau par défaut, elle est multipliée par le bleu de l'eau de 1.12.2, de sorte qu'une couleur choisie pour 1.12.2 rend ici la même |
| `bucket` | non | booléen | `true` | Enregistre un seau pour lui |
| `luminosity` | non | 0 à 15 | `0` | Lumière émise |
| `density` | non | entier | `1000` | Négatif, il flotte vers le haut, comme un gaz |
| `temperature` | non | entier, en kelvins | `300` | L'eau est à 300, la lave à 1300 |
| `viscosity` | non | entier | `1000` | À quel point il s'écoule lentement : le fluide avance une fois tous les viscosity / 200 ticks. L'eau est à 1000, la lave à 6000 |
| `gaseous` | non | booléen | `false` | Traité comme un gaz |
| `creativeTab` | non | nom d'onglet | aucun | L'onglet où apparaît le seau |
| `block` | non | objet | | Le bloc du fluide. `material` (`water`) : `water` permet de nager et de se noyer, fait flotter les bateaux, éteint les créatures en feu, garde les terres labourées humides et s'évapore quand on le verse dans le Nether ; `lava` enflamme tout ce qui s'y trouve, ne permet ni de nager ni de se noyer et utilise les sons du seau de lave ; tout autre matériau ne fait rien de tout cela. `flammability` (`0`) et `fireSpread` (`0`) : avec quelle facilité le feu consume le bloc et s'en propage. `quantaPerBlock` (`0`, lu comme 8) : jusqu'où il s'écoule depuis une source, un bloc de moins que le nombre comme sur 1.12.2 ; les fluides atteignent ici 1, 2, 3 ou 7 blocs, de sorte que 4 et 5 s'écoulent sur 3 blocs et 6 et plus sur 7. `potions` (aucun, une liste d'effets donnés à tout ce qui s'y trouve, chacun écrit `potion,duration,amplifier` avec une quatrième partie facultative `true` pour un effet ambiant) |
| `requires` | non | liste d'ids de mods ou de namespaces de packs | aucun | Le fichier est ignoré à moins que tous soient présents |

## Matériaux, onglets, sons, tags

*blocs et objets*

`<namespace>/materials/*.json`

Le chemin du fichier est le nom du matériau, que l'objet outil ou armure nomme ensuite dans `material`.

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

| Clé | Requis | Valeur | Par défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `harvestLevel` | non | 0 à 4 | `1` | Niveau de l'outil. 0 bois, 1 pierre, 2 fer, 3 diamant, 4 netherite |
| `durability` | non | entier | `250` | Utilisations avant qu'il ne se casse |
| `efficiency` | non | flottant | `6.0` | Vitesse de minage. Le diamant est à 8 |
| `damage` | non | flottant | `2.0` | Bonus de dégâts d'attaque |
| `enchantability` | non | entier | `14` | Qualité des enchantements. L'or est à 22 |
| `repairItem` | non | nom d'objet | aucun | Ce qui répare à l'enclume un outil fait de ce matériau. Une armure qui en est faite n'est pas réparée ainsi, comme sur 1.12.2 |
| `reduction` | non | liste de quatre entiers | | Points d'armure, dans l'ordre bottes, jambières, plastron, casque |
| `toughness` | non | flottant | `0.0` | Résistance de l'armure, comme celle du diamant |
| `equipSound` | non | nom de son | `item.armor.equip_iron` | Son quand l'armure est enfilée |
| `armorTexture` | non | préfixe de texture | le nom du fichier | La texture de l'armure portée, lue dans `textures/models/armor/<name>_layer_1.png` et `_layer_2.png` sous ce namespace |

### Onglets créatifs

*matériaux, onglets, sons, tags*

`<namespace>/tabs/*.json`

Les blocs, objets et seaux nomment leur onglet dans `creativeTab`. Un id complet, tel que `mypack:rubypack` ou `minecraft:combat`, est utilisé tel qu'écrit. Un nom simple est lu comme 1.12.2 lisait le libellé d'un onglet : `buildingBlocks` va à `minecraft:building_blocks`, `decorations` à `minecraft:functional_blocks`, `redstone` à `minecraft:redstone_blocks`, `transportation` et `tools` à `minecraft:tools_and_utilities`, `misc` et `materials` à `minecraft:ingredients`, `food` et `brewing` à `minecraft:food_and_drinks`, et `combat` à `minecraft:combat`, et tout autre nom simple est l'onglet `<namespace>:<name>` dans le namespace du fichier qui le nomme. Un onglet qu'aucun fichier ne déclare est créé pour vous, titré d'après `itemGroup.<namespace>.<name>` et montrant le premier objet qu'il contient. Le chemin d'un fichier d'onglet est le nom de l'onglet, sauf si `label` le remplace.

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| Clé | Requis | Valeur | Par défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `label` | non | chaîne | le nom du fichier | L'id de l'onglet : blocs et objets le nomment dans `creativeTab`, et le nom affiché vient de `itemGroup.<namespace>.<label>` dans les fichiers de langue |
| `icon` | non | nom d'objet | aucun | L'objet affiché sur l'onglet |

### Sons

*matériaux, onglets, sons, tags*

`<namespace>/sounds/*.json`

Le nom du fichier est libre, seul le dossier est lu, et plusieurs fichiers se cumulent.

Le format `sounds.json` de vanilla, de sorte qu'un pack puisse livrer son propre audio. Un fichier ici enregistre les événements sonores ; le client lit toujours l'audio via le `assets/<namespace>/sounds.json` du pack, livrez donc les deux, l'index sous `assets` et les événements sous `data`.

### Tags

*matériaux, onglets, sons, tags*

`<namespace>/tags/<kind>/*.json`

Les tags sont au format du jeu, dans le dossier du jeu, et un pack les livre comme il le ferait dans un datapack : `tags/items/ores/ruby.json` (1.21.1 : `tags/item/`) contenant `{ "values": ["mypack:ruby_ore"] }` place le minerai dans `mypack:ores/ruby`, et un fichier sous `data/forge/tags/items/ores/ruby.json` (1.21.1 : `data/c/...`) ajoute au tag de convention partagé que lit chaque mod. Les blocs et objets propres d'un pack nomment les leurs dans les `tags` de la variante à la place, et les fichiers sont écrits pour vous ; un `harvestTool` et un `harvestToolLevel` écrivent de la même façon les tags `mineable` et `needs_*_tool`.

Le dictionnaire des minerais de 1.12.2 est ce que les tags ont remplacé. Ses noms correspondent aux tags de convention : `oreRuby` est `forge:ores/ruby` sur 1.20.1 et `c:ores/ruby` sur 1.21.1, `ingotCopper` est `ingots/copper`, `gemRuby` est `gems/ruby`, `dustX` est `dusts/x`, `nuggetX` est `nuggets/x`, `blockX` est `storage_blocks/x`, et `logWood`, `plankWood` et `stickWood` sont les `minecraft:logs` et `minecraft:planks` du jeu et le tag de convention `rods/wooden`. Le `"remove": [...]` d'un fichier de tag retire des entrées isolées d'un tag, et `"replace": true` avec un `"values": []` vide le vide entièrement : un tag issu d'un pack inférieur ou d'un mod peut donc être réduit ou vidé. Pour retirer des objets de tous les tags à la fois, et hors du jeu, utilisez [Blocs et objets désactivés](#blocs-et-objets-désactivés).

## Surcharges de propriétés

*blocs et objets*

`<namespace>/overrides/<target>/<name>.json`

Le chemin nomme la cible : tout ce qui suit `overrides/` est le namespace et le nom du bloc, de l'objet ou du type de potion modifié.

Partout ailleurs, un pack remplace un fichier ou en ajoute un. Une surcharge ne fait ni l'un ni l'autre : elle modifie les propriétés d'un bloc, d'un objet ou d'un type de potion qui existe déjà, vanilla ou moddé, sans toucher à aucun de ses fichiers. Le chemin nomme la cible : `overrides/minecraft/stone.json` modifie donc `minecraft:stone`, et `overrides/tconstruct/<name>.json` modifie de la même façon le bloc de ce mod.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin.

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

### Propriétés des blocs

*surcharges de propriétés*

Toutes les clés sont facultatives et un fichier ne modifie que ce qu'il nomme : un fichier situé à `overrides/minecraft/stone.json` qui ne contient que `hardness`, `light` et `soundType` rend la pierre presque instantanée à miner, lumineuse, et au son de verre. Un même fichier porte ensemble des clés de bloc, d'objet et de potion. Celles-ci s'appliquent quand la cible est un bloc :

| Clé | Valeur | Ce que ça fait |
| --- | --- | --- |
| `hardness` | flottant | Temps de minage, le même chiffre que prend une définition de bloc. Sans `resistance`, elle relève aussi la résistance aux explosions à au moins ce même chiffre, comme le fait 1.12.2 |
| `resistance` | flottant | Résistance aux explosions telle que la lit 1.12.2 : le bloc conserve trois cinquièmes du chiffre, donc `10` donne le `6` de la pierre |
| `slipperiness` | flottant | `0.6` est un sol ordinaire, `0.98` est de la glace |
| `light` | `0` à `15` | Lumière émise |
| `lightOpacity` | `0` à `15` | Quantité de lumière que le bloc arrête |
| `soundType` | l'un des types de son | Sons de pas, de pose et de destruction |
| `harvestTool` | `pickaxe`, `axe`, `shovel`, `hoe` ou `sword` | Ce qui le mine vite, écrit dans les tags d'outils ; `harvestToolLevel`, `0` par défaut, fixe le niveau : 1 pierre, 2 fer, 3 diamant, 4 et plus netherite. Le fait que le bloc lâche ou non son butin sans le bon outil reste tel que le bloc l'a |
| `flammability` | entier | Avec quelle facilité il se consume ; `fireSpread`, `5` par défaut, avec quelle facilité le feu l'atteint |

### Propriétés des objets

*surcharges de propriétés*

| Clé | Valeur | Ce que ça fait |
| --- | --- | --- |
| `maxStackSize` | `1` à `64` | Taille de la pile |
| `maxDamage` | entier | Durabilité |
| `containerItem` | nom d'objet | Laissé dans la grille d'artisanat, comme le fait un seau |
| `food` | objet | Rend l'objet comestible, voir ci-dessous |

Un nom qui est à la fois un bloc et un objet, et c'est le cas de l'objet de tout bloc plaçable, prend les deux groupes depuis un seul fichier :

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

À `overrides/minecraft/oak_planks.json`, cela fait que les planches se cassent à peu près aussi vite que la terre et qu'on peut les manger. `food` accepte `heal` (`1`), `saturation` (`0.6`), `alwaysEdible` (`false` ; `true` permet de manger avec une barre de faim pleine) et `effects`, dont les entrées s'écrivent exactement comme celles d'un type de potion. Un objet déjà comestible accepte de nouveaux `heal`, `saturation` et `alwaysEdible` ; `effects` n'est pas pris en charge sur un tel objet, et le journal le signale. Quand l'objet comestible place un bloc, visez le ciel pour manger, puisque viser un bloc le place : c'est l'ordre d'utilisation de vanilla, pas un bogue.

### Effets des types de potions

*surcharges de propriétés*

`effects` au niveau supérieur du fichier réécrit d'un coup la liste d'effets d'un type de potion :

```json
{
  "effects": [
    { "potion": "minecraft:levitation", "duration": 200, "amplifier": 0 }
  ]
}
```

À `overrides/minecraft/swiftness.json`, la potion de Rapidité accorde désormais la Lévitation. Chaque entrée accepte `potion` (obligatoire), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) et `showParticles` (`true`), comme dans `potion_types/`, et la liste ne peut pas être vide.

### Autres mods, rechargements et limites

*surcharges de propriétés*

Une cible que possède un autre mod doit porter ce mod dans `requires`, afin que le fichier soit ignoré sans bruit quand le mod n'est pas installé au lieu d'être signalé comme cible manquante :

```json
{
  "requires": ["tconstruct"],
  "hardness": 1.0
}
```

Les surcharges sont actives en direct. Les valeurs d'origine sont mémorisées avant le premier changement : désactiver le pack et lancer `/rdplserver reload` remet donc tout comme c'était, sans redémarrage ; il en va de même à chaque entrée dans un monde. Un fichier par cible : quand deux packs surchargent la même chose, le fichier du pack suivant remplace entièrement celui du précédent, et le journal le signale.

Deux limites à connaître. Un bloc ou un objet dont le propre code calcule une propriété ignore le champ qui la sous-tend : la surcharge s'applique alors mais ne change rien ; vanilla ne fait cela que pour la résistance aux explosions des escaliers, mais les mods sont libres de le faire n'importe où. Et les objets rendus comestibles ne fonctionnent que sur des objets sans comportement de clic droit propre : un objet qui fait déjà quelque chose à l'utilisation continue de le faire.

Les surcharges exigent que le pack soit présent côté client comme côté serveur, puisque la vitesse de minage, la lumière et l'alimentation se produisent toutes sur l'écran du joueur : elles ne conviennent donc pas aux packs côté serveur. `overrides` dans la catégorie de configuration `content` désactive entièrement le dossier.

## Groupes de dureté

*blocs et objets*

`<namespace>/hardness/*.json`

Le chemin du fichier nomme le groupe dans le journal et rien d'autre ne le lit, de sorte que plusieurs fichiers se cumulent.

Donne à un groupe de blocs un multiplicateur de temps de minage, tiré par position de bloc. Le bloc lui-même n'est jamais modifié : rien n'est enregistré, rien n'est écrit dans le monde, et un monde ouvert sans le pack est du vanilla ordinaire.

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

### Minage et explosifs

*groupes de dureté*

| Clé | Requis | Valeur | Par défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `blocks` | oui | liste de noms de blocs ou d'objets | | Le groupe. Mêmes formes qu'un `replace` de worldgen |
| `except` | non | liste de noms de blocs ou d'objets | aucun | Retiré du groupe, quoi que dise `blocks` |
| `miningTime` | non | nombre, ou objet avec `min` et `max` | `1.0` | Combien de fois plus longtemps le bloc met à se casser, pour un joueur comme pour un mob `digs` |
| `blastResistance` | non | nombre, ou objet avec `min` et `max` | `1.0` | Multiplie la résistance du bloc aux explosions |
| `buckets` | non | 1 à 256 | `10` | En combien de paliers la plage est divisée |
| `minHeight` | non | entier | le bas du monde | En dessous, le tirage donne le palier le plus dur |
| `maxHeight` | non | entier | le haut du monde | Au-dessus, le tirage donne le palier le plus dur |
| `field` | non | objet | voir ci-dessous | La forme en amas que prend le tirage |
| `requires` | non | liste d'ids de mods ou de namespaces de packs | aucun | Le fichier est ignoré à moins que tous soient présents |

Un nombre unique donne à chaque bloc du groupe le même multiplicateur, et rien n'est tiré. Un `min` et un `max` se tirent par position : `max` là où le champ est vide, `min` au centre d'un amas, et les paliers intermédiaires sont déterminés par `buckets`.

### Minage en aventure et déblocages

*groupes de dureté*

| Clé | Requis | Valeur | Par défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `keeps` | non | booléen | `false` | Le bloc reste en place quand il est miné : le butin, l'expérience, l'usure de l'outil et le son de destruction se produisent tous, et le bloc est toujours là pour être miné à nouveau, de sorte que le groupe forme un filon sans fin au rythme que fixe `miningTime`. Le mode Créatif le retire comme toujours |
| `adventure` | non | objet | aucun | Qui peut casser le groupe en mode Aventure, où rien ne se casse autrement. `tools` liste les objets dont l'un doit être en main, vide pour n'importe quel objet tenu ; `teams`, `players` et `entities` disent qui, une équipe par son nom, un joueur par son pseudo, un mob par son id d'entité pour la tâche `digs`, et trois listes vides signifient n'importe qui muni de l'outil. Survie et Créatif ne sont pas touchés |
| `advancement` | non | `namespace:path` | aucun | Le groupe ne compte pour un joueur que lorsqu'il a ce progrès. Deux groupes peuvent nommer le même bloc, l'un avec un progrès et l'autre sans, et celui qui est débloqué l'emporte ; un joueur qui ne l'a pas obtient le groupe simple, ou vanilla s'il n'y en a pas. Les mobs n'ont aucun progrès, donc un groupe verrouillé n'atteint jamais une tâche `digs`, et la résistance aux explosions et le tirage de texture, qui n'appartiennent à aucun joueur, viennent du groupe simple |
| `becomes` | non | objet | aucun | Les blocs du groupe se transforment en un autre bloc, dans tout le monde, dès que n'importe quel joueur obtient `advancement` : `{ "advancement": "mypack:deep_miner", "block": "mypack:rich_ore" }`. Chaque chunk chargé est balayé aussitôt, et un chunk chargé ou créé plus tard l'est à son arrivée, de sorte que l'ancien bloc disparaît pour de bon. Donnez au nouveau bloc un groupe à lui pour changer sa façon de se miner |

### Le champ

*groupes de dureté*

Le tirage n'est pas fait pour chaque bloc de façon entièrement indépendante, sinon le dur et le tendre ne seraient que du bruit pur, sans aucune forme. `field` décide de cette forme, et `type` choisit entre deux façons d'y parvenir.

```json
{
  "field": { "type": "speckle" }
}
```

| Clé    | Requis | Valeur                | Défaut    | Rôle                                  |
| --- | --- | --- | --- | --- |
| `type` | non    | `speckle` ou `seeded` | `speckle` | Lequel des deux modes ci-dessous est utilisé |

#### speckle

*le champ*

Chaque bloc tire son propre palier, et un bloc situé à une face de distance peut lui transmettre un palier plus faible. On obtient des taches denses et fines, la plupart d'un seul bloc, avec çà et là une zone plus large là où elles se rejoignent. C'est le mode le plus proche de la sensation de minage du mod dont celui-ci s'inspire.

```json
{
  "field": {
    "type": "speckle",
    "chances": [30, 30, 20, 20, 10, 10, 10, 10, 50],
    "spread": 0.15
  }
}
```

| Clé       | Requis | Valeur                     | Défaut                                 | Rôle                                                                                        |
| --- | --- | --- | --- | --- |
| `chances` | non    | liste d'entiers, pour mille | `[30, 30, 20, 20, 10, 10, 10, 10, 50]` | Fréquence à laquelle un bloc démarre à chaque palier, le plus tendre en dernier. Tout le reste correspond au palier le plus dur |
| `spread`  | non    | 0.0 à 1.0                  | `0.15`                                 | Fréquence à laquelle un palier se transmet au bloc voisin, avec un palier de moins ou trois de moins |

La liste se lit en partant du plus tendre à la fin : la dernière entrée est donc le palier le plus tendre, et la première se situe juste au-dessus du plus dur. Avec les nombres ci-dessus, environ sept blocs sur dix sont au palier le plus dur et les autres sont éparpillés dedans.

#### seeded

*le champ*

Des graines sont disposées sur un réseau calculé à partir du monde et de la position, et le palier d'un bloc dépend de sa proximité avec la graine la plus proche. On obtient des zones moins nombreuses, plus grandes et plus rondes, qui se fondent les unes dans les autres, et qui peuvent pousser des bras.

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

| Clé        | Requis | Valeur          | Défaut | Rôle                                  |
| --- | --- | --- | --- | --- |
| `cell`     | non    | entier, blocs   | `8`    | Écart entre les graines               |
| `seeds`    | non    | 1 à 4           | `1`    | Graines par cellule                   |
| `reach`    | non    | flottant, blocs | `3.0`  | Distance à laquelle l'influence d'une graine porte |
| `arms`     | non    | 0 à 6           | `0`    | Bras rayonnant depuis chaque graine   |
| `armReach` | non    | flottant, blocs | `0.0`  | Distance à laquelle les bras portent  |

Si `arms` est omis, les zones sont rondes. Donner des bras à une graine en fait un nœud à tentacules, et les bras de nœuds voisins s'étendent les uns vers les autres, ce qui donne un filon plutôt qu'une boule. Gardez `reach` au-dessus de la moitié de `cell`, sinon les zones ne peuvent pas se toucher et vous obtenez des boules séparées sans rien entre elles.

### L'afficher

*groupes de dureté*

Le multiplicateur est invisible en lui-même. Pour que le joueur voie quels blocs sont résistants, donnez au bloc un blockstate avec une variante par palier, toutes de poids égal, listées en commençant par la plus dure :

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

Minecraft choisit déjà une variante d'après la position d'un bloc, et un groupe de dureté lui fournit le palier à la place, de sorte que la texture et le multiplicateur concordent toujours. Le pack d'exemple fait exactement cela pour sa pierre de rubis.

Trois points doivent être corrects, et aucun ne s'annonce quand il est faux.

**Exactement `buckets` entrées, toutes de même poids.** Le palier sert de position dans la liste : une liste d'une autre longueur, ou dont les poids diffèrent, pointe discrètement vers la mauvaise texture.

**Un nom de modèle précédé de `block/`.** Sur cette version, un blockstate nomme le fichier de modèle en entier : `"model": "mypack:block/stone_step0"` lit donc `models/block/stone_step0.json` ; un simple `mypack:stone_step0` cherche `models/stone_step0.json`, qui n'existe pas, et l'entrée est ignorée sans un mot.

**La même clé que celle que demande le jeu.** Un bloc à un seul état a pour clé `""`, et la pierre vanilla en fait partie. Un bloc avec des propriétés a pour clé l'ensemble de celles-ci : une surcharge pour une bûche demande donc `axis=x`, `axis=y` et `axis=z`, chacun avec sa propre liste.

Activez `worldgenDebug` et chaque groupe de dureté est vérifié par rapport à son modèle précompilé à l'entrée dans un monde, en indiquant le blockstate, le nombre de variantes restantes, la texture finalement associée à chacune et les packs que le jeu a fusionnés pour y arriver. C'est le moyen le plus rapide de repérer l'un des trois points ci-dessus, et il avertit aussi lorsque la surcharge d'un blockstate partagé a modifié un état que le groupe n'avait jamais nommé.

### Ce qu'il ne touche pas

*groupes de dureté*

Seul le minage du joueur lui-même est modifié. Les machines qui cassent des blocs lisent directement la dureté du bloc et ne sont pas affectées. Les blocs posés par un joueur sont tirés comme les autres, puisque le tirage appartient à l'emplacement et non au bloc, et un bloc transporté ailleurs prend ce que dit son nouvel emplacement.

---

# Artisanat, butin et commerce

## Blocs et objets désactivés

*artisanat, butin et commerce*

`<namespace>/disabled/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers s'empilent.

Retire des blocs et des objets du jeu sans les désenregistrer : les mondes gardent donc leurs ids, et supprimer le fichier ramène tout. Le contenu vanilla, celui des mods et celui des packs sont traités de la même façon, y compris les blocs et objets propres au pack, et un bloc désactivé désactive son objet tout comme un objet désactivé désactive son bloc.

```json
{
  "requires": ["thermal"],
  "names": ["thermal:tin_ore", "thermal:deepslate_tin_ore", "mekanism:salt*"],
  "namespaces": ["bigreactors"],
  "tags": ["forge:ores/tin"]
}
```

| Clé          | Requis | Valeur                       | Défaut  | Rôle                                                                                                                             |
| --- | --- | --- | --- | --- |
| `names`      | non    | liste de noms de blocs et d'objets | aucun   | Ce qui est désactivé. Un nom se terminant par `*` correspond à tous les noms qui commencent par le reste                         |
| `namespaces` | non    | liste d'ids de mods          | aucun   | Tous les blocs et objets du mod                                                                                                  |
| `tags`       | non    | liste de noms de tags        | aucun   | Tous les objets du tag d'objets et tous les blocs du tag de blocs de ce nom, et les deux tags sont laissés vides. Un `#` initial est accepté |
| `requires`   | non    | liste d'ids de mods          | aucun   | Le fichier est ignoré sauf si tous sont chargés. Les entrées `config:` et `file:` fonctionnent comme partout ailleurs            |

Un bloc ou un objet désactivé :

- disparaît de tous les onglets créatifs et de l'onglet de recherche, et est masqué dans JEI
- n'a aucune recette qui le fabrique ou l'utilise : toute recette de tout type qui l'a pour résultat disparaît, qu'il s'agisse d'artisanat, de cuisson, de taille de pierre ou de forge, de même que toute recette dont un emplacement ne peut être rempli que par lui. Un emplacement qui accepte aussi autre chose garde sa recette, et un emplacement de tag la perd simplement avec le tag
- est retiré de tous les tags d'objets et de blocs, et figure à la place dans `resourcedatapackloader:disabled`
- est retiré de tout tirage de butin, coffres, mobs et pêche compris, des butins de blocs et des échanges des villageois et du marchand ambulant, et une pile de cet objet qui tombe s'évapore
- ne peut être ni posé, ni utilisé, ni brandi, ni ramassé, et la pile en main est supprimée quand un joueur essaie
- est supprimé partout où une pile de cet objet apparaît : de l'inventaire et du coffre de l'Ender d'un joueur à la connexion puis chaque seconde, de tout conteneur quand un joueur l'ouvre, et des coffres et autres inventaires au chargement de leur chunk
- est retiré du monde là où il est posé : chaque bloc devient de l'air, son bloc-entité avec, au chargement de son chunk

Pour remplacer des blocs posés par autre chose au lieu de les retirer, donnez-leur une ligne `blockReplacements` telle que `thermal:tin_ore=minecraft:stone` dans le modèle de monde, voir [Remplacements](#remplacements). Un bloc que le processus de remplacement échange lui est laissé. Les recettes qu'un autre mod conserve dans ses propres machines appartiennent à ce mod et ne sont pas touchées. Le masquage dans les onglets créatifs et dans JEI se fait côté client : un client vanilla affiche donc toujours l'objet. `content.disabled` dans la configuration désactive le dossier.

Pour vider un tag tout en laissant ses objets en jeu, utilisez plutôt un fichier de tag avec `"replace": true` et un `"values": []` vide, voir [Tags](#tags).

## Recettes de fourneau et combustibles

*artisanat, butin et commerce*

`<namespace>/furnace/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers s'empilent.

Ajoute et retire des recettes de fourneau. Un retrait supprime aussi les recettes correspondantes du haut fourneau, du fumoir et du feu de camp, puisque 1.12.2 conservait toutes les recettes de cuisson dans l'unique liste du fourneau ; un ajout ne crée qu'une recette de fourneau.

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

Entrées sous `add` :

| Clé          | Requis | Valeur     | Défaut | Rôle                                                                                                                                           |
| --- | --- | --- | --- | --- |
| `input`      | oui    | nom d'objet | aucun  | Ce qui entre                                                                                                                                   |
| `output`     | oui    | nom d'objet | aucun  | Ce qui sort                                                                                                                                    |
| `count`      | non    | entier     | `1`    | Combien en sortent                                                                                                                             |
| `experience` | non    | nombre     | `0.0`  | Expérience par objet cuit, un point au maximum : 1.0 ou plus donne un point pour chaque objet retiré, donc un `count` de 2 en donne deux. Le minerai de fer donne 0.7 |

Un ajout dont l'entrée est déjà cuite par une autre recette est ignoré et le journal nomme la recette qui gêne, comme le fait 1.12.2 ; retirez cette recette dans le même fichier ou dans un fichier précédent pour la remplacer.

Les entrées sous `remove` sont soit un simple nom d'objet, qui retire toutes les recettes qui le produisent, soit un objet nommant `input`, `result`, ou les deux pour affiner. Un retrait qui ne nomme ni l'un ni l'autre est ignoré et le journal le signale.

Les fichiers s'appliquent dans l'ordre de chargement, les retraits de chaque fichier avant ses ajouts. Un retrait dans un fichier ultérieur supprime donc aussi un ajout fait par un fichier antérieur, mais n'atteint jamais un ajout qui vient après lui. Un ajout compte comme une recette de fourneau du mod auquel appartient son résultat : `blockFurnaceRecipes` et `blockedFurnaceMods` le bloquent donc comme n'importe quelle autre.

`<namespace>/fuels/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers s'empilent.

```json
{
  "fuels": [
    { "item": "mypack:ruby_coal", "burnTime": 2400 },
    { "tag": "forge:gems/ruby", "burnTime": 800 }
  ]
}
```

| Clé        | Requis         | Valeur      | Défaut | Rôle                         |
| --- | --- | --- | --- | --- |
| `item`     | l'un des deux  | nom d'objet | aucun  | L'objet qui brûle            |
| `tag`      | l'un des deux  | id de tag   | aucun  | Tout ce qui est dans ce tag brûle |
| `burnTime` | oui            | entier, ticks | `0`  | Le charbon fait 1600, une planche 300 |

## Potions, types de potions et alchimie

*artisanat, butin et commerce*

`<namespace>/potions/*.json`

Le chemin du fichier est le nom de registre de l'effet : `mypack/potions/ruby_sight.json` enregistre donc `mypack:ruby_sight`, qu'un type de potion nomme ensuite.

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

| Clé             | Requis | Valeur            | Défaut                                    | Rôle                                                                                                                     |
| --- | --- | --- | --- | --- |
| `name`          | non    | clé de traduction | `effect.<namespace>.<name>`               | Ce que voit le joueur                                                                                                    |
| `color`         | non    | couleur hexadécimale | `FFFFFF`                               | Couleur des particules                                                                                                   |
| `badEffect`     | non    | booléen           | `false`                                   | Compte comme nuisible : un œil d'araignée fermenté l'inverse donc                                                        |
| `beneficial`    | non    | booléen           | `false`                                   | Affiché comme un effet positif                                                                                           |
| `instant`       | non    | booléen           | `false`                                   | S'applique une fois au lieu de durer                                                                                     |
| `effectiveness` | non    | flottant          | `0.5`                                     | Lu pour qu'un fichier 1.12.2 se charge ; ni 1.12.2 ni cette version n'en tient compte                                    |
| `attributes`    | non    | liste d'objets    | aucun                                     | `attribute` (l'id du jeu, tel que `minecraft:generic.movement_speed`), `uuid`, `amount` (`0.0`), `operation` (`0`)       |
| `icon`          | non    | objet             | aucun                                     | `x` et `y`, la colonne et la ligne de l'icône sur la feuille d'états 1.12.2, chacune `0` si omise. Lu uniquement sans `iconTexture` |
| `iconTexture`   | non    | chemin de texture | l'icône RDPL, ou aucune quand `icon` est défini | Une image fournie par un pack, telle que `mypack:textures/effect/rage.png`, dessinée en entier comme icône         |

L'icône de l'effet est la texture `assets/<namespace>/textures/mob_effect/<name>.png`, de 18 par 18 comme celles du jeu, et un pack qui la fournit à cet endroit l'emporte toujours. Sinon, `iconTexture` nomme une image fournie par un pack, qui est copiée à cet endroit en entier ; `icon` seul choisit l'icône d'un effet vanilla d'après sa place sur la feuille d'états 1.12.2, `x` en largeur et `y` en hauteur à partir de 0, par exemple `{ "x": 2, "y": 1 }` pour Saut amélioré ; et un effet qui ne nomme ni l'un ni l'autre affiche l'icône RDPL.

### Types de potions

*potions, types de potions et alchimie*

`<namespace>/potion_types/*.json`

Le chemin du fichier est le nom de registre du type de potion, qu'un objet `potion_bottle` nomme ensuite dans `potionTypes`.

```json
{
  "baseName": "ruby_sight",
  "effects": [
    { "potion": "mypack:ruby_sight", "duration": 3600, "amplifier": 0, "ambient": false, "showParticles": true }
  ]
}
```

| Clé        | Requis | Valeur          | Défaut                 | Rôle                                                                                                                                                                                                                                                                                               |
| --- | --- | --- | --- | --- |
| `baseName` | non    | chaîne          | le namespace et le nom | Nomme la potion : la clé de langue `item.minecraft.potion.effect.<baseName>`, avec `splash_potion`, `lingering_potion` ou `tipped_arrow` à la place de `potion` pour les autres formes. Une `potion_bottle` qui la contient affiche le même nom, et les clés `potion.effect.<baseName>` d'un pack 1.12.2 sont converties |
| `effects`  | oui    | liste d'objets  |                        | Voir ci-dessous                                                                                                                                                                                                                                                                                    |

Chaque effet accepte `potion` (requis), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) et `showParticles` (`true`).

### Alchimie

*potions, types de potions et alchimie*

`<namespace>/brewing/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers s'empilent.

```json
{
  "brewing": [
    { "input": "minecraft:potion", "ingredient": "mypack:ruby", "output": "mypack:ruby_potion", "requires": ["mypack"] },
    { "from": "minecraft:awkward", "ingredient": "mypack:ruby", "to": "mypack:ruby_tonic" }
  ]
}
```

Chaque entrée est soit `input`, `ingredient` et `output`, qui transforme un objet en un autre, soit `from`, `ingredient` et `to`, qui transforme un type de potion en un autre. `ingredient` est requis dans les deux cas, et une entrée accepte aussi `requires`, de sorte qu'une recette puisse être ignorée sans que le fichier le soit.

## Travail à l'enclume

*artisanat, butin et commerce*

`<namespace>/anvils/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers s'empilent. Chaque fichier est un travail.

Placez l'objet nommé dans l'emplacement gauche d'une enclume et son objet `with` dans l'emplacement droit : l'enclume propose alors l'objet de gauche avec les enchantements listés, ou son `result`, pour le nombre de niveaux indiqué ; un exemplaire de chacun est consommé sauf si un `count` en demande davantage, et le reste de chaque pile demeure dans l'enclume. Retirer le résultat peut aussi faire gagner un advancement, et l'objet peut être interdit d'usage tant que cet advancement n'est pas obtenu : une épée qui ne frappe qu'une fois travaillée.

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

| Clé            | Requis | Valeur                                              | Défaut         | Rôle                                                                                                                                                                                                                                                                  |
| --- | --- | --- | --- | --- |
| `item`         | oui    | nom d'objet, une liste de noms, ou `{ "item", "count" }` |           | Ce qui va dans l'emplacement gauche, n'importe quel objet d'une liste, et combien un travail en consomme, un par défaut ; le reste de la pile est conservé pour le suivant. `{ "item": "minecraft:coal", "count": 8 }` avec un `result` en diamant, c'est huit charbons pour un diamant |
| `with`         | oui    | nom d'objet, une liste de noms, ou `{ "item", "count" }` |           | Ce qui va dans l'emplacement droit, n'importe quel objet d'une liste, et combien sont dépensés, un par défaut : `{ "item": "minecraft:coal", "count": 10 }` exige une pile d'au moins dix et en prend dix. Une enclume ne réagit jamais à un objet seul, donc chaque travail est une paire |
| `result`       | non    | nom d'objet, ou `{ "item", "count" }`               | l'objet de gauche | Ce qui sort à la place de l'objet de gauche, et en quelle quantité, un par défaut, en gardant les tags de l'objet de gauche : une pioche en fer incassable et dix charbons peuvent ainsi donner une pioche en diamant incassable. Les enchantements vont sur ce qui sort |
| `levels`       | non    | entier                                              | `1`            | Les niveaux d'expérience que coûte le travail, 1 au minimum                                                                                                                                                                                                           |
| `enchantments` | non    | objet nom d'enchantement vers niveau                | aucun          | Avec quoi l'objet ressort. Un niveau qu'il possède déjà à cette hauteur ou au-dessus est laissé tel quel, et s'il n'y a rien à relever, l'enclume ne propose rien, sauf si `grants` est défini                                                                          |
| `grants`       | non    | `namespace:path`                                    | aucun          | Un advancement obtenu quand le résultat est retiré. Fournissez-le sous `advancements/` avec un critère `impossible`, pour que rien d'autre ne le donne                                                                                                                |
| `locks`        | non    | booléen                                             | `false`        | Tant que le joueur n'a pas `grants`, l'objet ne peut ni frapper quoi que ce soit, ni être utilisé, ni servir à creuser ; le joueur est informé de ce qu'il attend dès qu'il le prend en main. Le placer dans l'enclume reste permis, c'est ainsi qu'on le débloque       |

Les réparations et combinaisons propres à l'enclume ne sont pas touchées : celle-ci ne répond que lorsque la gauche contient un objet nommé et que la droite contient son `with`.

Un mob avec `collectsExperience` dépense aussi ses niveaux ici. Tant qu'il tient `item` dans sa main principale et `with` dans sa main secondaire et qu'il a les `levels` à payer, il se rend à une enclume, une enclume ébréchée ou une enclume endommagée située à moins de 16 blocs en largeur et 4 en haut ou en bas, et la travaille une fois à moins de 3 blocs : les niveaux sont retirés des siens comme ils le sont pour un joueur, `with` est consommé, l'enclume s'use comme sous un joueur, et le résultat se retrouve dans sa main principale. Lorsqu'il passe sur un objet au sol que l'un des travaux d'enclume nomme sous `with`, il le ramasse dans sa main secondaire. `grants` et `locks` ne concernent que les joueurs : un mob ne gagne donc rien avec `grants` et aucun verrou ne le retient.

## Butins des blocs

*artisanat, butin et commerce*

`<namespace>/block_drops/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers s'empilent.

La table de butin d'un bloc décide de ce qu'il lâche, mais en fournir une remplace la table entière, et une table de butin ne peut pas donner d'expérience. Une règle ici nomme un bloc et ce que sa destruction lâche en plus des butins habituels, ou à leur place, expérience comprise, et laisse la table du bloc intacte.

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

| Clé           | Requis | Valeur                      | Défaut  | Rôle                                                                                                                      |
| --- | --- | --- | --- | --- |
| `block`       | oui    | id de bloc                  |         | Le bloc que surveille la règle                                                                                            |
| `properties`  | non    | objet propriété vers valeur | aucun   | Uniquement les états portant ces valeurs, comme `{ "axis": "x" }` sur une bûche ; sans cela, tous les états. Un `meta` 1.12.2 n'est pas lu |
| `replace`     | non    | booléen                     | `false` | Indique si les butins habituels sont écartés avant que ceux-ci soient tirés                                               |
| `advancement` | non    | `namespace:path`            | aucun   | La règle ne compte que pour un joueur ayant cet advancement : le même bloc peut ainsi lâcher une chose avant et une autre après |
| `drops`       | oui    | liste de butins             |         | Chacun est tiré indépendamment quand le bloc est détruit                                                                  |

Chaque butin :

| Clé          | Requis                   | Valeur                      | Défaut   | Rôle                                                                                                                                                                                                                                                                                       |
| --- | --- | --- | --- | --- |
| `item`       | oui, sauf `experience`   | id d'objet                  |          | Ce qui est lâché                                                                                                                                                                                                                                                                           |
| `experience` | non                      | nombre ou `low-high`        |          | À la place d'un objet, cette quantité d'expérience sous forme d'orbes, tirée uniformément dans l'intervalle. `chance` et `silkTouch` s'appliquent comme pour un objet                                                                                                                       |
| `count`      | non                      | nombre ou `low-high`        | `1`      | Combien, tiré uniformément dans l'intervalle                                                                                                                                                                                                                                               |
| `chance`     | non                      | flottant                    | `1.0`    | La probabilité que le butin ait lieu, `0.05` signifiant une casse sur vingt                                                                                                                                                                                                                |
| `fortune`    | non                      | entier                      | `0`      | Jusqu'à ce nombre d'exemplaires supplémentaires par niveau de Fortune de l'outil                                                                                                                                                                                                           |
| `silkTouch`  | non                      | `either`, `only` ou `never` | `either` | Indique si le butin exige une récolte au Toucher de soie, la refuse, ou s'en moque. C'est le cas lorsqu'un joueur casse, avec un outil au Toucher de soie, un bloc récoltable ainsi selon la règle de 1.12.2 : un bloc plein sans bloc-entité, ou les vitres, barreaux de fer, toiles d'araignée et coffres de l'Ender |

Les règles voient toute casse qui lâche le butin du bloc, comme dans 1.12.2 : celle d'un joueur, mais aussi les explosions, les pistons, l'eau qui coule, les mobs et un mob de pack qui creuse le bloc, qui déclenchent toutes les règles sans `advancement`. Lorsqu'une explosion réduit les butins propres du bloc, chaque objet tiré survit selon les mêmes probabilités ; l'expérience n'est pas réduite. Plusieurs règles pour un même bloc s'appliquent toutes, un `replace` sur l'une d'elles écartant d'abord les butins habituels.

## Butin des joueurs

*artisanat, butin et commerce*

`<namespace>/player_loot/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers s'empilent.

Le jeu ne donne aucune table de butin propre aux joueurs : à la mort, seul l'inventaire tombe, et aucun nom de table ne pourrait être surchargé par un pack. RDPL en ajoute une, tirée à la mort d'un joueur :

```json
{
  "table": "mypack:entities/player",
  "mode": "add",
  "rollOnKeepInventory": false,
  "dropLoose": false
}
```

| Clé                   | Requis | Valeur             | Défaut  | Rôle                                                                                |
| --- | --- | --- | --- | --- |
| `table`               | oui    | nom de table       |         | La table de butin tirée à la mort d'un joueur                                       |
| `mode`                | non    | `add` ou `replace` | `add`   | Indique si les objets de la table s'ajoutent à l'inventaire ou prennent sa place    |
| `rollOnKeepInventory` | non    | booléen            | `false` | Indique si la table est tirée lors d'une mort où l'inventaire a été conservé        |
| `dropLoose`           | non    | booléen            | `false` | Indique si les objets sont posés directement au sol au lieu de rejoindre les butins de mort |

`add` lâche les objets de la table en plus de l'inventaire, ce qui convient aux primes de chasse. `replace` écarte l'inventaire et ne lâche que ce que tire la table.

Avec `rollOnKeepInventory` désactivé, les morts sous `keepInventory` (et les morts en spectateur, qui conservent toujours l'inventaire) ne tirent rien. L'activer garde les morts coûteuses dans les mondes où l'inventaire est conservé.

Plusieurs fichiers s'empilent, chacun évalué selon ses propres termes. Si une entrée applicable est `replace`, l'inventaire est vidé une seule fois avant le tirage : une entrée `add` à ses côtés est donc tout de même prise en compte.

La table est une table de butin ordinaire recherchée par son nom : elle peut se trouver dans le pack à `loot_tables/entities/player.json`, être n'importe quelle table vanilla ou de mod, et être atteinte par `loot_injections`. Contexte de butin : le joueur qui meurt est l'entité dépouillée, le tueur (s'il y en a un) est le joueur qui tue, et la source de dégâts est renseignée : `killed_by_player`, `entity_properties`, `random_chance_with_looting` et le reste se comportent donc normalement.

Une fonction de butin est propre à RDPL, utilisable dans toute table ayant une entité dépouillée : `rdpl:killed_name` nomme l'objet lâché d'après la victime. `format` façonne le nom affiché (`%s` désigne la victime, par défaut simplement le nom), et `tag` écrit à la place le nom brut dans une clé de chaîne NBT pour les objets qui la lisent eux-mêmes.

```json
{ "item": "mypack:human_skull", "weight": 1,
  "functions": [ { "function": "rdpl:killed_name", "format": "%s's Skull" } ] }
```

**Mods de tombes.** Les objets tirés rejoignent les butins de mort ordinaires avant qu'un mod de tombes ne les lise : ils finissent donc dans la tombe avec tout le reste (`replace` met le contenu de la table dans la tombe à la place de l'inventaire). Aucune configuration requise.

`dropLoose` contourne entièrement la liste des butins : les objets sont placés directement dans le monde, de sorte que les mods de tombes ne les voient jamais ; l'inventaire va dans la tombe, les objets de la table gisent au sol pour le tueur. Utilisez-le pour des dépouilles qui reviennent au tueur plutôt qu'à la tombe de la victime. Sans mod de tombes, il ne change presque rien. Précaution : les objets existent avant que quoi que ce soit en aval puisse annuler les butins, donc les entrées qui ne doivent pas survivre à une mort annulée doivent le laisser désactivé.

Mettez `playerLoot` à `false` dans la catégorie `data` de la configuration pour désactiver entièrement le dossier.

## Villageois et échanges

*artisanat, butin et commerce*

`<namespace>/villagers/*.json`

Le chemin du fichier est le nom de registre de la profession : `mypack/villagers/jeweller.json` enregistre donc `mypack:jeweller`, qu'un échange nomme ensuite dans `profession`.

```json
{
  "jobSite": "mypack:gem_bench",
  "workSound": "minecraft:entity.villager.work_toolsmith"
}
```

| Clé         | Requis | Valeur      | Défaut | Rôle                                                                                                                                                                                                                                                                              |
| --- | --- | --- | --- | --- |
| `jobSite`   | non    | nom de bloc | aucun  | Le bloc qu'un villageois revendique pour adopter cette profession, comme la table de forge fait un forgeron d'outils. Sans lui, aucun bloc ne distribue la profession : comme sur 1.12.2, un villageois généré ou né l'obtient au hasard, la garde, et sans bloc où travailler ne regarnit jamais ses stocks |
| `workSound` | non    | nom de son  | aucun  | Ce qu'il joue en travaillant à ce bloc                                                                                                                                                                                                                                            |

Les carrières sont une idée de 1.12.2 que le jeu n'a plus : une profession est un seul ensemble d'échanges, donc un pack qui avait deux carrières fournit deux fichiers de villageois. L'apparence du villageois est une texture ordinaire, fournie à `assets/<namespace>/textures/entity/villager/profession/<name>.png` et `textures/entity/zombie_villager/profession/<name>.png`, exactement là où le jeu range les siennes. Un échange qui nomme une profession vanilla 1.12.2 avec sa `career`, comme `minecraft:smith` avec `armor`, va à la profession en laquelle cette carrière s'est transformée, ici `minecraft:armorer`. Les `texture` et `zombieTexture` d'un fichier 1.12.2, une apparence complète fournie par un pack, sont copiées vers ces deux chemins lorsque le pack n'a rien à cet endroit, et l'apparence est dessinée par-dessus celle du villageois.

### Échanges

*villageois et échanges*

`<namespace>/trades/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers s'empilent.

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

| Clé          | Requis | Valeur            | Défaut | Rôle                                                                                                 |
| --- | --- | --- | --- | --- |
| `profession` | oui    | nom de profession |        | À qui appartient cet échange. Un nom vanilla 1.12.2 avec sa `career` est aussi lu, voir plus haut    |
| `level`      | non    | entier            | `1`    | À quel palier d'échange il apparaît, de 1 à 5. Un niveau plus élevé rejoint le niveau 5, le plus haut qu'un villageois atteigne |
| `maxUses`    | non    | entier            | `12`   | Nombre d'utilisations avant le verrouillage                                                          |
| `xp`         | non    | entier            | `2`    | Expérience que le villageois gagne par échange vers son niveau suivant                               |

Une pile est `item` avec `min` (`1`) et `max` (`min`) : un prix fixe n'est donc que `min`.

---

# Créatures et dangers

## Variantes d'entités

*créatures et dangers*

`<namespace>/entities/*.json`

Le chemin du fichier est le nom de registre de la variante : `mypack/entities/angry_cow.json` enregistre donc `mypack:angry_cow`, ce à quoi se rapportent `becomes`, un œuf d'apparition et la sauvegarde d'un monde.

Un fichier ici crée une nouvelle entité à partir d'une entité existante. C'est une vraie entité à part entière, avec son propre nom de registre, son propre nom dans le monde, son propre œuf d'apparition, et sa propre table de butin si vous lui en donnez une, bâtie sur le comportement d'une autre entité sans la remplacer. Rien ne change pour l'entité copiée.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin.

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

### Identité

*variantes d'entités*

| Clé             | Requis | Valeur                             | Défaut    | Rôle                                                                                                                                                                            |
| --- | --- | --- | --- | --- |
| `entity`        | oui    | `namespace:name`                   | aucun     | L'entité sur laquelle bâtir. Celle de n'importe quel mod, tant qu'elle accepte un simple constructeur de monde                                                                  |
| `name`          | non    | chaîne                             | aucun     | Le nom qu'elle porte dans le monde, dans les messages de mort et sur son œuf                                                                                                    |
| `showName`      | non    | booléen                            | `false`   | Affiche le nom sans qu'on la regarde                                                                                                                                            |
| `profession`    | non    | `namespace:name`                   | aléatoire | Pour un villageois, le métier qu'il exerce                                                                                                                                      |
| `baby`          | non    | booléen ou 0.0 à 1.0               | `false`   | À quelle fréquence l'apparition est un petit, et il le reste. `true` signifie toujours, un nombre est la part concernée                                                         |
| `becomes`       | non    | liste                              | aucun     | Les autres variantes en lesquelles celle-ci peut se transformer à son apparition, selon leur poids. Voir ci-dessous                                                             |
| `egg`           | non    | booléen ou objet                   | `true`    | Un œuf d'apparition, coloré comme celui de l'entité copiée. `{ "primary": "AABBCC", "secondary": "112233" }` choisit vos propres couleurs, `false` omet l'œuf                   |
| `keepsBaseBaby` | non    | booléen                            | `false`   | Indique si le tirage « petit » de la base s'exécute aussi. Sans lui, une variante basée sur le zombie n'apparaît jeune que selon `baby`, sans enfant issu du tirage propre au zombie ni poulet jockey |
| `requires`      | non    | liste d'ids de mods ou de namespaces de packs | aucun | La variante est omise sauf si tous sont présents                                                                                                                        |

Une variante est une classe à part entière : un monde qui en contient une dépend donc du pack qui l'a créée, de la même façon qu'il dépend d'un mod. Retirez le fichier et les créatures de ce monde disparaissent avec lui.

**Un œuf ou un générateur qui donne un mélange.** Une variante est une classe à part entière : seule, elle fait donc toujours apparaître exactement ce qu'elle dit. `becomes` est le moyen par lequel un pack contourne cela : une liste de variantes en lesquelles celle-ci peut se transformer à son apparition, chacune avec un poids, décidé pour chaque créature.

```json
{
  "becomes": [
    { "variant": "mypack:walker", "weight": 95 },
    { "variant": "mypack:little_walker", "weight": 5 }
  ]
}
```

Se nommer soi-même est la façon de rester tel quel, et les poids sont les probabilités. Placez cela sur `mypack:walker` et un œuf et une entrée d'apparition donnent surtout des marcheurs avec un petit de temps en temps, comme un œuf de zombie vous donne un bébé de loin en loin. Cela se produit lorsque la créature entre dans le monde : cela vaut donc pour les œufs, `/summon` et l'apparition naturelle, et la créature qui arrive est une vraie créature de la variante choisie avec tout ce que dit cette variante. Un générateur est plus strict : il ne tire que parmi les variantes de la même base et de la même équipe que celle à laquelle il est réglé, donc un générateur de zombies donne les zombies de cette équipe et leurs petits, et jamais une créature d'un autre type ou d'une autre équipe, comme un générateur de zombies vanilla reste un générateur de zombies. Une variante obtenue ainsi ne se transforme pas à nouveau : deux variantes peuvent donc se nommer l'une l'autre sans tourner en boucle.

**Où intervient `baby`.** Le jeu n'a pas de zombie bébé à proprement parler : il y a un seul zombie qui tire à son apparition s'il est un enfant. `baby` dit à quelle fréquence, donc `"baby": 0.05` est l'habitude vanilla et `"baby": true` est toujours. Une variante ne reprend pas en plus le tirage du zombie : aucun enfant ni poulet jockey n'apparaît sans que `baby` l'ait demandé ; `keepsBaseBaby` rend ce tirage. Ce sont deux façons d'arriver au même résultat, et laquelle choisir dépend de la différence voulue : `baby` seul donne une variante parfois jeune, `becomes` donne plusieurs variantes qui diffèrent comme vous le souhaitez, et un mélange des deux convient aussi.

### Apparence

*variantes d'entités*

| Clé          | Requis | Valeur                                 | Défaut      | Rôle                                                                                                       |
| --- | --- | --- | --- | --- |
| `texture`    | non    | `namespace:textures/entity/<file>.png` | aucun       | Une apparence propre, disposée de la même façon que celle de l'entité copiée                               |
| `leftHanded` | non    | booléen                                | `false`     | Tient son arme dans l'autre main                                                                           |
| `glowing`    | non    | booléen                                | `false`     | Cernée d'un contour visible à travers les murs                                                             |
| `invisible`  | non    | booléen                                | `false`     | Non dessinée, bien que son équipement le soit toujours                                                     |
| `scale`      | non    | flottant                               | `1.0`       | Sa taille à l'affichage, et la taille de sa hitbox                                                         |
| `angryScale` | non    | flottant                               | `scale`     | La taille à laquelle elle gonfle tant qu'elle a quelque chose à attaquer, et pendant trois secondes après l'avoir perdu |
| `width`      | non    | flottant                               | celle de la base | Sa hitbox en largeur, avant l'application de `scale`                                                  |
| `height`     | non    | flottant                               | celle de la base | Sa hitbox en hauteur, avant l'application de `scale`                                                  |
| `bright`     | non    | booléen                                | `false`     | Dessinée en pleine lumière où qu'elle se tienne, comme en plein soleil de midi : jamais assombrie par la nuit, l'ombre ou une grotte |
| `hideArmor`  | non    | booléen                                | `false`     | Porte son armure sans qu'elle soit dessinée                                                                |
| `hideHeld`   | non    | booléen                                | `false`     | Idem pour ce qu'elle tient en main                                                                         |
| `tint`       | non    | couleur hexadécimale                   | aucun       | Colore l'entité à l'affichage                                                                              |
| `tintParts`  | non    | liste de `body`, `armor`, `held`       | `["body"]`  | Les parties que la teinte atteint                                                                          |

`scale` modifie à la fois le modèle et la hitbox des deux côtés : ce que vous voyez est donc ce que vous pouvez toucher. Une créature qui change elle-même de taille, un animal qui grandit ou un zombie enfant, est mise à l'échelle autour de la taille qu'elle a choisie, de sorte que les deux ne se contrarient pas. `angryScale` la fait gonfler tant qu'elle a une cible et la ramène à `scale` quand elle en perd une. Comme le client n'est jamais informé de ce que chasse une créature, c'est l'indicateur de sprint qui transmet l'information ; il est activé sur une variante qui utilise `angryScale` et sur rien d'autre, donc un mod qui lit le sprint de vos variantes le verra changer. Grandir sous un plafond bas est possible, comme pour un slime qui grossit : gardez donc l'écart modeste.

Une `texture` est liée à la place de celle que l'entité utiliserait normalement, quel que soit le moteur de rendu dont elle hérite : cela fonctionne donc pour les entités de mods comme pour les vanilla. Elle doit correspondre au modèle sur lequel elle est dessinée, puisque le modèle est celui de l'entité de base : une apparence, pas une nouvelle forme. Les couches gardent leurs propres textures : l'armure ressemble donc toujours à une armure sur un zombie reskinné.

L'armure n'est jamais dessinée que sur une entité dont le moteur de rendu a une couche d'armure, c'est-à-dire les mobs humanoïdes et les villageois. Une variante de vache ou d'araignée peut porter une armure et en tire la protection, mais rien ne la dessine : `armor` sous `attributes` est donc généralement la façon la plus propre de rendre une telle créature résistante. `hideArmor` sert dans l'autre cas : un humanoïde qui doit garder l'armure dans ses emplacements, pour la protection ou pour un mod qui les lit, sans qu'elle soit visible.

### Ses sons

*variantes d'entités*

| Clé           | Requis | Valeur   | Défaut           | Rôle                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| --- | --- | --- | --- | --- |
| `sounds`      | non    | objet    | ceux de la base  | `ambient`, `hurt` et `death`, chacun un événement sonore enregistré, et un nom 1.12.2 reste lu, voir [noms de sons](#listes-de-valeurs). Trois autres pour lesquels la base n'a pas de son : `target` est joué une fois à chaque fois qu'elle prend une cible, et `explode` est le son de son explosion, qu'elle se fasse exploser avec `explodes` ou lance du TNT avec `throws`. `throw` est joué quand elle lance quoi que ce soit avec `throws`, à la place du lancer de boule de neige, ou du sifflement de la mèche pour le TNT. `targetVaries` décale chaque lecture de `target` vers le haut ou le bas d'une valeur aléatoire dans la limite de ce nombre de demi-tons : `3` erre donc d'un quart d'octave de part et d'autre ; `0` le joue tel quel. Sur cette version, le jeu joue aussi son propre son d'explosion, car un client 1.20.1 choisit lui-même le son d'explosion ; sur 1.21.1, `explode` le remplace |
| `soundVolume` | non    | nombre   | `1.0`            | Le volume de ces sons                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `soundPitch`  | non    | nombre   | `1.0`            | La hauteur de lecture. Sous 1, plus grave ; au-dessus de 1, plus aigu                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `silent`      | non    | booléen  | `false`          | Ne fait aucun bruit                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |

### Santé, dégâts et effets

*variantes d'entités*

| Clé                 | Requis | Valeur                                          | Défaut          | Rôle                                                                                                                                                                                                                                                                                  |
| --- | --- | --- | --- | --- |
| `immuneTo`          | non    | liste de types de dégâts                        | aucun           | Les dégâts qu'elle encaisse sans broncher, par les noms qu'utilisait 1.12.2, `fall`, `drown`, `explosion`, `explosion.player`, `magic`, `indirectMagic`, `mob`, `player`, `inWall` et les autres, ou par un id de type de dégâts. Voir [types de dégâts](#listes-de-valeurs)           |
| `fallDamage`        | non    | flottant                                        | `1.0`           | Multiplie les dégâts d'une chute. `0` supprime les dégâts de chute                                                                                                                                                                                                                    |
| `absorption`        | non    | flottant                                        | `0`             | Cœurs supplémentaires en plus de sa santé                                                                                                                                                                                                                                             |
| `creatureAttribute` | non    | `undefined`, `undead`, `arthropod` ou `illager` | celui de la base | Ce qu'elle est considérée être, afin que Châtiment et les potions de soin la traitent en conséquence                                                                                                                                                                                  |
| `effects`           | non    | liste d'objets                                  | aucun           | Les effets qu'elle a en permanence : `{ "potion": "minecraft:strength", "amplifier": 1 }`                                                                                                                                                                                             |
| `fireproof`         | non    | booléen                                         | `false`         | Ne prend jamais feu du tout : jamais blessée par le feu ou la lave, et ne brûle jamais en plein jour                                                                                                                                                                                  |
| `invulnerable`      | non    | booléen                                         | `false`         | Ne subit de dégâts que du vide et du mode créatif                                                                                                                                                                                                                                     |
| `attributes`        | non    | objet                                           | aucun           | `maxHealth`, `movementSpeed`, `attackDamage`, `attackSpeed`, `knockbackResistance`, `followRange`, `armor`. Un attribut que l'entité n'a pas normalement lui est donné. `attackSpeed` est le nombre de coups par seconde pour un combattant au corps à corps, `1` comme dans le jeu, donc `2` frappe deux fois plus souvent |
| `hurtResistance`    | non    | entier, ticks                                   | le `20` du jeu  | Durée après un coup pendant laquelle elle ne peut plus être blessée. Les coups plus rapides que la moitié de cette valeur sont perdus : un attaquant rapide veut donc une cible avec une valeur plus basse |
| `ignoresEffects`    | non    | liste de noms d'effets                          | aucun           | Les effets qui ne s'appliquent jamais à elle, quoi ou qui que ce soit les applique : un coup, une potion jetable, un balise, une flèche, `/effect`. `all` refuse tous les effets, une variante part donc d'une page blanche. Ses propres `effects` lui sont tout de même appliqués |

### Déplacement

*variantes d'entités*

| Clé              | Requis | Valeur          | Défaut           | Rôle                                                                                                                                                                                                                                                                                                                            |
| --- | --- | --- | --- | --- |
| `jumpMultiplier` | non    | flottant        | `1.0`            | De combien elle saute plus haut que l'entité qu'elle copie                                                                                                                                                                                                                                                                      |
| `maxFallHeight`  | non    | entier          | celle de la base | De quelle hauteur elle acceptera de tomber en cherchant son chemin                                                                                                                                                                                                                                                              |
| `noAI`           | non    | booléen         | `false`          | Reste où on la place et ne fait rien                                                                                                                                                                                                                                                                                            |
| `leashable`      | non    | booléen         | `false`          | Peut être menée en laisse, même si l'entité qu'elle copie ne le pouvait jamais                                                                                                                                                                                                                                                  |
| `steerable`      | non    | booléen         | `false`          | Peut être dirigée lorsqu'on la chevauche                                                                                                                                                                                                                                                                                        |
| `pathPriorities` | non    | objet           | aucun            | Ce qu'elle acceptera de traverser, comme `WATER`, `LAVA`, `DANGER_FIRE`, `DOOR_WOOD_CLOSED` et les autres types de chemin du jeu, chacun un nombre dont une valeur négative signifie jamais. Les `DANGER_CACTUS` et `DAMAGE_CACTUS` de 1.12.2 sont lus comme `DANGER_OTHER` et `DAMAGE_OTHER`, où cette version range le cactus avec les buissons de baies sucrées |
| `stepHeight`     | non    | flottant, blocs | celle de la base | La hauteur d'un rebord qu'elle gravit sans sauter                                                                                                                                                                                                                                                                               |
| `climbs`         | non    | booléen         | celui de la base | Activé, elle escalade tout mur contre lequel elle marche, comme une araignée, quelle que soit sa base. Désactivé, elle n'escalade rien, pas même une échelle, et une araignée reste au sol                                                                                                                                       |
| `teleports`      | non    | booléen         | `true`           | Indique si un enderman ou un shulker peut se téléporter. Désactivé, il reste où il se tient, de jour comme dans l'eau                                                                                                                                                                                                           |
| `walks`          | non    | booléen         | `false`          | Un lapin marche comme les autres animaux au lieu de se déplacer par bonds. Seul un lapin le lit                                                                                                                                                                                                                                 |

### Eau

*variantes d'entités*

| Clé                  | Requis | Valeur   | Défaut  | Rôle                                                                                                                                                                                                                                     |
| --- | --- | --- | --- | --- |
| `breathesUnderwater` | non    | booléen  | `false` | Ne se noie jamais, et coule pour marcher au fond plutôt que de nager vers la surface. Elle s'oriente toujours comme au sol : une eau profonde dont elle ne peut pas sortir à pied la retiendra                                              |
| `swims`              | non    | booléen  | `false` | Se déplace dans l'eau comme un poulpe ou un gardien, et ne se noie jamais. Elle cherche son chemin à travers l'eau plutôt que sur le sol : sa place est donc dans l'eau, et elle est échouée hors de l'eau                               |
| `amphibious`         | non    | booléen  | `false` | Marche sur terre et nage correctement dans l'eau, en changeant sa façon de s'orienter quand elle y entre et en sort. Ne se noie jamais. Ce qu'elle poursuivait est oublié au bord de l'eau : elle hésite donc un instant à chaque traversée |
| `waterSlowdown`      | non    | flottant | `0.8`   | De combien l'eau la ralentit. Plus haut, c'est plus rapide                                                                                                                                                                               |

### Combat

*variantes d'entités*

| Clé             | Requis | Valeur                | Défaut                | Rôle                                                                                                                                                                                                                                                                                                                                                                                          |
| --- | --- | --- | --- | --- |
| `hostile`       | non    | booléen               | `false`               | Attaque ce qu'elle peut atteindre, et riposte quand elle est blessée. Une variante hostile compte comme un monstre pour le jeu quelle que soit sa base : le plafond de monstres la retient donc. Le mode paisible ne la supprime que si sa base est un monstre ; toute autre base reste, incapable de blesser un joueur. Elle abandonne les tâches d'animal que sa base apportait : reproduction, appât, suivre un parent, un maître ou ses semblables, s'asseoir |
| `targets`       | non    | liste de noms d'entités | le joueur           | Ce qu'elle cherche tant qu'elle est hostile. `minecraft:player` est reconnu bien que le joueur ne soit pas une entité enregistrée                                                                                                                                                                                                                                                              |
| `attackReach`   | non    | flottant, blocs       | sa taille             | La portée d'un coup au corps à corps. Le jeu porte à deux fois la largeur, c'est pourquoi une créature agrandie frappe de plus loin ; ceci la fixe directement                                                                                                                                                                                                                                  |
| `knockback`     | non    | flottant              | celui de la base, `0.4` | La force avec laquelle ses coups repoussent. `0` ne repousse pas du tout                                                                                                                                                                                                                                                                                                                   |
| `hitEffects`    | non    | booléen               | `true`                | Indique si elle applique à ce qu'elle frappe l'effet qu'applique l'entité qu'elle copie : le wither d'un Wither squelette, le poison d'une araignée des cavernes, la faim d'un zombie momifié. Désactivé, elle ne frappe que pour les dégâts                                                                                                                                                  |
| `hitFire`       | non    | booléen               | `true`                | Indique si elle met le feu à ce qu'elle frappe quand l'entité qu'elle copie le ferait : un zombie en feu, la boule de feu d'un blaze. Désactivé, rien de ce qu'elle fait n'enflamme sa cible                                                                                                                                                                                                  |
| `passive`       | non    | booléen               | `false`               | L'empêche d'attaquer quoi que ce soit, quel que soit son comportement habituel                                                                                                                                                                                                                                                                                                                |
| `threatLeast`   | non    | entier                | `0`                   | La plus basse bande de menace dans laquelle un joueur ou autre porteur situé à moins de 128 blocs doit se trouver pour que la variante apparaisse naturellement. `0` fait apparaître comme d'habitude                                                                                                                                                                                         |
| `threatHostile` | non    | entier                | `0`                   | La plus basse bande de menace dans laquelle un joueur doit se trouver pour que la variante l'attaque de son propre chef. En dessous, la variante est docile envers ce joueur, bien qu'elle riposte quand elle est frappée. `0` attaque comme d'habitude                                                                                                                                       |

`hostile` supprime aussi le comportement qui faisait fuir la créature : un animal qui évitait les joueurs ou paniquait quand il était blessé ne fait plus ni l'un ni l'autre une fois hostile, puisqu'il fuirait sinon ce qu'il est censé attaquer. Il faut une entité qui marche sur le sol, car il utilise le même comportement d'attaque que vanilla donne à ses propres mobs. Une base volante ou nageuse est journalisée et laissée telle quelle. `passive` agit plus largement, mais n'atteint que le comportement construit comme le fait vanilla : un mod dont l'hostilité est écrite dans son propre code de tick ou de dégâts, un pack ne peut pas l'en dissuader.

### Équipement, butins et expérience

*variantes d'entités*

| Clé                  | Requis | Valeur                      | Défaut           | Rôle                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| --- | --- | --- | --- | --- |
| `lootTable`          | non    | `namespace:entities/<name>` | celle de la base | Ce qu'elle lâche. Sans cela, elle lâche ce que lâche l'entité qu'elle copie                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `experience`         | non    | entier                      | celle de la base | La quantité d'expérience qu'elle lâche                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `collectsExperience` | non    | booléen                     | `false`          | Récolte l'expérience comme le fait un joueur : les orbes situés à moins de huit blocs dérivent vers elle et sont pris au contact, le Raccommodage sur son équipement est réparé en premier, et les points forment des niveaux sur la courbe propre au joueur, conservés sur le mob à travers une sauvegarde. Ce qu'elle tue lâche son expérience comme si un joueur avait porté le coup, un bloc que sa tâche `digs` casse lâche l'expérience propre au bloc, et un tirage d'expérience de `block_drops` lui revient aussi. À sa mort, elle lâche sept par niveau jusqu'à cent, sauf si `keepInventory` est activé. Les objectifs avec le critère `xp` ou `level` portent son total et son niveau sur une ligne nommée d'après son UUID, qu'une fonction lit avec `execute if score` ou un sélecteur `scores={<objective>=N..}`. Elle dépense ses niveaux en travail à l'enclume comme un joueur, voir [Travail à l'enclume](#travail-à-lenclume) |
| `dropChance`         | non    | 0 à 1                       | `0`              | La probabilité que chaque pièce d'équipement soit lâchée                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `picksUpLoot`        | non    | booléen                     | `false`          | Ramasse ce sur quoi elle marche                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `equipment`          | non    | objet                       | aucun            | `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`, chacun un nom d'objet                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |

Une variante lâche ce que lâche l'entité qu'elle copie, car la table de butin est fixée dans le code propre de cette entité au lieu d'être recherchée par son nom. `lootTable` la dirige vers une table de votre cru, que vous fournissez ensuite à `loot_tables/entities/<name>.json` comme n'importe quelle autre.

### Comportements spéciaux

*variantes d'entités*

| Clé              | Requis | Valeur        | Défaut            | Rôle                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| --- | --- | --- | --- | --- |
| `digs`           | non    | booléen       | `false`           | Creuse à travers tout ce qui se dresse entre elle et sa cible, avec l'outil qu'elle tient : une pelle dans la terre, le sable et le gravier, une pioche dans la pierre, une hache dans le bois, et seulement ce que le matériau de cet outil peut casser : une pioche en bois n'ouvre donc jamais le minerai de fer et rien n'ouvre l'obsidienne sans diamant. Un bloc prend autant de temps que pour un joueur avec cet outil, lâche ce qu'il lâcherait, et use l'outil. Donnez-lui l'outil avec `equipment` ; à mains nues, elle ne creuse rien, et elle ne creuse rien là où `mobGriefing` est désactivé. Elle ne cherche jamais de contournement : avec une cible, elle marche droit dessus et creuse tout ce qui barre la route, et là où l'outil ne peut pas ouvrir le bloc, elle reste là et pousse. Nécessite `hostile`. Elle prend ses cibles sans avoir besoin de les voir, puisque ce vers quoi elle creuse est par nature derrière quelque chose |
| `throws`         | non    | booléen       | `false`           | Lance ce qu'elle tient sur sa cible depuis une certaine distance, et si c'est du TNT, elle l'allume et recule. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `throwAmmo`      | non    | entier        | aucun             | Combien elle en a à lancer. Omis, elle n'est jamais à court                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `throwReload`    | non    | entier, secondes | `explosionFuse` | Combien de temps sa main reste vide avant qu'elle en tire un autre                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `throwRetreat`   | non    | entier, secondes | `explosionFuse` | Combien de temps elle reste à distance après un lancer avant de se retourner                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `throwPower`     | non    | flottant      | `1.0`             | La force du lancer. Le doubler double à peu près la portée                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `throwArc`       | non    | flottant      | `0.35`            | La hauteur de la cloche. Plus haut, elle reste plus longtemps en l'air ; près de zéro, c'est un jet tendu ; sous zéro, le lancer part vers le bas                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `throwReturns`   | non    | booléen       | `false`           | Ce qu'elle lance vole comme un trident : il frappe pour l'`attackDamage` de la variante, ou 8 sur une base qui n'en a pas, puis revient dans sa main comme la Loyauté ramène un trident. Il n'est jamais épuisé et est visé sur la cible comme le vise un squelette, plus vite avec `throwPower` et avec moins de dispersion aux difficultés plus élevées, et le lanceur reste sur place pendant qu'il vole : `throwAmmo`, `throwReload`, `throwRetreat` et `throwArc` ne s'y appliquent donc pas. Le TNT est lancé comme toujours |
| `explodes`       | non    | booléen       | `false`           | S'explose à côté de sa cible, comme un creeper. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `explosionPower` | non    | nombre        | `3.0`             | La taille de l'explosion. Un creeper fait 3, le TNT 4. Sur une base de creeper ou de ghast, écrire ceci ou `explosionFuse` règle aussi l'explosion propre à la base sans `explodes` : la taille et la mèche de l'explosion d'un creeper, la boule de feu d'un ghast, chacune en nombres entiers, de sorte qu'un ghast à qui l'on ne donne que `explosionFuse` explose à 3 au lieu de son 1 habituel                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `explosionFuse`  | non    | entier, ticks | `30`              | Combien de temps elle siffle avant d'exploser, et la mèche propre d'une base de creeper                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `explosionFire`  | non    | booléen       | `false`           | Laisse des feux derrière elle                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `charges`        | non    | booléen       | `false`           | Fonce sur sa cible depuis une certaine distance et frappe avec un fort recul au contact, comme le fait un ravageur, puis se repose avant la course suivante. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `pounces`        | non    | booléen       | `false`           | S'accroupit, puis bondit sur sa cible en arc de cercle et frappe à l'atterrissage, comme le fait un renard. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `sniffs`         | non    | entier, blocs | `0`               | Entend les joueurs qui se déplacent à moins de ce nombre de blocs, murs ou non, et marche vers l'endroit où elle les a entendus ; un joueur qui s'accroupit ou reste immobile n'est pas entendu, et celui qu'elle voit ensuite devient sa cible. `0` n'écoute pas. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `fleesWhenHurt`  | non    | 0.0 à 1.0     | `0`               | Rompt le combat et fuit celui qu'elle affronte tant que sa santé est sous cette fraction, et revient une fois au-dessus. `0` ne fuit jamais. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `sleepsByDay`    | non    | booléen       | `false`           | Trouve de l'ombre en journée et y reste immobile jusqu'à la nuit ou jusqu'à ce que quelque chose l'attaque. Pendant son repos, elle est couchée sur le côté                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `home`           | non    | entier, blocs | `0`               | Reste dans ce nombre de blocs autour de l'endroit où elle s'est d'abord tenue, errant à l'intérieur et revenant quand elle s'écarte. `0` erre librement                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `patrols`        | non    | booléen       | `false`           | Parcourt le territoire par longues étapes avec ses semblables qui suivent un chef, comme le fait une patrouille de pillards. Un groupe qui apparaît ensemble choisit un chef ; les autres restent à quelques blocs de lui, et quand le chef prend une cible, tous la prennent. Un suiveur qui perd son chef prend lui-même la tête. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `swoops`         | non    | booléen       | `false`           | Tournoie au-dessus de sa cible et plonge à travers elle, frappant au passage, comme le fait un fantôme. La variante reçoit un assistant de vol : elle vole pendant sa chasse et se pose au sol au repos ; il lui faut une base qui soit une créature, un perroquet par exemple, et une chauve-souris n'en est pas une. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `gusts`          | non    | booléen       | `false`           | Se prépare et lâche une rafale de vent sur sa cible depuis une certaine distance, projetant tout ce qui est près de la cible en arrière et en l'air, comme le fait la charge de vent d'une breeze. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `gustPower`      | non    | flottant      | `1.5`             | La force avec laquelle une rafale projette. Un coup de mob fait 0.4, un fort enchantement de recul environ 1                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |

**Lancer au lieu de charger.** `explodes` envoie une créature s'exploser sur sa cible. `throws` est l'autre tempérament : elle garde ses distances, lance ce qui se trouve dans sa main principale sur ce qu'elle combat, et si c'est du TNT, elle l'allume, le lance, et recule pendant qu'il brûle.

```json
{
  "hostile": true,
  "throws": true,
  "explosionFuse": 50,
  "equipment": { "mainhand": "minecraft:tnt" }
}
```

Lancer vide sa main, puisqu'elle a lancé la chose. Elle garde alors ses distances pendant `throwRetreat`, en tire un autre après `throwReload`, et se retourne vers sa cible : une boucle de lancer, repli, rechargement, rapprochement. Donnez-lui un `throwAmmo` et cette boucle prend fin quand le compte tombe à zéro, sa main restant vide pour de bon et son attaque ordinaire reprenant le relais. Omettez `throwAmmo` et elle n'est jamais à court.

Le compte est écrit dans la créature : il ne se remplit donc pas parce qu'un chunk a été déchargé puis rechargé. Tout ce qui n'est pas du TNT vole comme un objet et retombe, ce qui rend un sapeur qui jette des pierres ou de la chair putréfiée aussi simple à faire qu'un autre qui jette des explosifs.

`explosionFuse` reste la mèche du TNT lancé, et tient lieu de l'une ou l'autre minuterie que vous omettez : une variante écrite avant ces clés se comporte donc exactement comme avant.

La façon dont le lancer lui-même vole tient à `throwPower` et `throwArc`. La première est un multiplicateur de la poussée, et comme la poussée croît déjà avec la distance, l'augmenter allonge la portée sans changer le temps que le lancer passe en l'air. La seconde est la portance, et elle change la forme : haute, elle lobe par-dessus un mur et prend son temps ; près de zéro, elle est jetée à plat et retombe presque aussitôt ; sous zéro, elle est lancée vers le bas sur ce qui se trouve en dessous. Les deux laissent la mèche intacte : une charge lobée et une charge tendue explosent donc au même nombre de secondes après avoir quitté la main, ce qui décide si l'une éclate au-dessus de vous ou retombe d'abord et attend. La distance depuis laquelle elle lancera est son `followRange`, et elle se rapproche comme d'habitude dès que vous êtes à moins de trois blocs : elle est donc dangereuse à distance et ordinaire à bout portant.

### Tâches

*variantes d'entités*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `tasks` | non | liste | aucun | Toute tâche que connaît le jeu, ajoutée à la variante par son nom avec la priorité de votre choix, ou retirée de ce que sa base apportait. La liste ci-dessous |

**Toute tâche que connaît le jeu.** Les clés ci-dessus sont les comportements propres à RDPL. `tasks` va au-delà : elle atteint toutes les tâches que vanilla utilise lui-même, sur n'importe quelle base. Une entrée est un objet qui nomme la `task` et sa `priority`, plus ce que cette tâche lit ; un nom précédé d'un `-` retire toutes les tâches de ce type que la base apportait. Les priorités se lisent à partir de 0, et vanilla garde les siennes entre 1 et 8 : une tâche à 0 l'emporte donc sur tout ce que fait la base, et une tâche à 9 ne s'exécute que lorsque rien d'autre n'en veut.

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

La liste est appliquée après que `hostile`, `passive` et les comportements ci-dessus ont fait leur travail : elle a donc le dernier mot. Les tâches qui déplacent le corps s'excluent mutuellement : l'une ne s'exécute que si rien de plus prioritaire ne déplace la créature, et l'attaque dont un monstre est pourvu se trouve à 2. Un bond ou une fuite sur un zombie demande donc la priorité 1, sans quoi elle n'a jamais son tour ; l'araignée et le loup placent leur bond avant leur attaque pour la même raison. Une tâche que la base exécute déjà est ajoutée une seconde fois au lieu d'être remplacée : retirez d'abord l'ancienne. Certaines tâches n'ont de sens que sur une base qui possède ce qu'elles pilotent : un combat à l'arc exige une base qui tire, s'asseoir exige une base qui peut être apprivoisée, et le commerce exige un villageois. Si vous en demandez une sur une base qui ne peut pas la porter, le journal indique quelle base il faut, et la variante s'en passe. Sur cette version, un villageois fonctionne avec le cerveau du jeu plutôt qu'avec des tâches ; les lignes de villageois ci-dessous n'atteignent donc que ce que le cerveau laisse aux tâches.

| Clé | Type | Défaut | Rôle |
| --- | --- | --- | --- |
| `priority` | int | requis | Sa place parmi les tâches de la base. Plus la valeur est basse, plus elle passe tôt |
| `speed` | nombre | celle de la tâche | Vitesse de déplacement pendant la tâche, en multiplicateur de sa vitesse de marche |
| `nearSpeed` | nombre | `1.2` | `avoidEntity` : le multiplicateur une fois que ce qu'il évite est proche |
| `distance` | nombre, blocs | celle de la tâche | À quelle distance il regarde, suit, tire ou se tient à l'écart |
| `near` | nombre, blocs | celle de la tâche | `follow`, `followOwner`, `followOwnerFlying` : à quelle distance il s'approche avant de s'arrêter |
| `chance` | nombre | celle de la tâche | `wander` : un tirage sur ce nombre de ticks ; `wanderAvoidWater` : la probabilité, de 0 à 1, de quitter l'abri ; `watchClosest`, `watchClosest2` : la probabilité, de 0 à 1, de regarder à chaque tick |
| `leap` | nombre | `0.4` | `leapAtTarget` : la hauteur du bond |
| `cooldown` | int, ticks | `20` | `attackRanged`, `attackRangedBow` : ticks entre deux tirs |
| `entity` | nom d'entité | aucun | L'entité que la tâche cherche, évite, observe ou avec laquelle elle se reproduit. `minecraft:player` est reconnu |
| `items` | liste de noms d'objets | aucun | `tempt` : ce qu'un joueur tend |
| `sight` | booléen | `true` | `nearestAttackableTarget`, `targetNonTamed` : seulement ce qu'il peut voir |
| `nearby` | booléen | `false` | `nearestAttackableTarget` : seulement ce qui se trouve dans sa propre portée de poursuite |
| `help` | booléen | `false` | `hurtByTarget` : les congénères proches se joignent à lui |
| `memory` | booléen | `false` | `attackMelee`, `zombieAttack` : il poursuit une cible qu'il a perdue de vue |
| `close` | booléen | `false` | `openDoor` : il referme la porte derrière lui |
| `nocturnal` | booléen | `false` | `moveThroughVillage` : seulement la nuit |
| `scared` | booléen | `false` | `tempt` : un joueur qui bouge trop vite rompt le charme |

La colonne `Liste` indique où vit la tâche. `tasks` est ce que fait la créature ; `targets` est la manière dont elle choisit ce qu'elle poursuit, et une tâche de ciblage sans attaque correspondante ne fait rien à elle seule.

| Tâche | Requiert | Liste | Lit | Rôle |
| --- | --- | --- | --- | --- |
| `attackMelee` | une créature marcheuse | `tasks` | `speed`, `memory` | Marche jusqu'à sa cible et la frappe |
| `attackRanged` | une base qui tire | `tasks` | `speed`, `cooldown`, `distance` | Garde ses distances et tire ce que sa base tire |
| `attackRangedBow` | un monstre qui tire | `tasks` | `speed`, `cooldown`, `distance` | Le combat à l'arc du squelette : il se déplace latéralement, bande l'arc et décoche |
| `avoidEntity` | une créature marcheuse | `tasks` | `entity`, `distance`, `speed`, `nearSpeed` | Fuit l'entité nommée dès qu'elle passe à moins de `distance` |
| `beg` | un loup | `tasks` | `distance` | Quémande auprès d'un joueur qui tend de la nourriture |
| `breakDoor` | n'importe quelle base | `tasks` | | Brise les portes en bois sur son chemin, en difficulté difficile |
| `creeperSwell` | un creeper | `tasks` | | Siffle et explose près de sa cible |
| `defendVillage` | un golem de fer | `targets` | | S'en prend à quiconque a attaqué un villageois |
| `eatGrass` | n'importe quelle base | `tasks` | | Mange de l'herbe, comme le fait un mouton |
| `findEntityNearest` | n'importe quelle base | `targets` | `entity` | Cible l'entité nommée la plus proche, comme le font un slime ou un ghast |
| `findEntityNearestPlayer` | n'importe quelle base | `targets` | | Cible le joueur le plus proche qu'il peut atteindre |
| `fleeSun` | une créature marcheuse | `tasks` | `speed` | Cherche l'ombre quand le soleil l'atteint |
| `follow` | n'importe quelle base | `tasks` | `speed`, `near`, `distance` | Suit ses congénères |
| `followGolem` | un villageois | `tasks` | | Suit un golem de fer qui tend une fleur |
| `followOwner` | une base apprivoisable | `tasks` | `speed`, `near`, `distance` | Suit son maître et se téléporte auprès de lui quand il est trop loin derrière |
| `followOwnerFlying` | une base apprivoisable | `tasks` | `speed`, `near`, `distance` | Pareil, en volant |
| `followParent` | un animal | `tasks` | `speed` | Un petit reste près d'un adulte de son espèce |
| `harvestFarmland` | un villageois | `tasks` | `speed` | Récolte les cultures mûres et les replante |
| `hurtByTarget` | une créature marcheuse | `targets` | `help` | Riposte contre ce qui l'a frappé |
| `landOnOwnersShoulder` | un perroquet | `tasks` | | Se pose sur l'épaule de son maître |
| `leapAtTarget` | n'importe quelle base | `tasks` | `leap` | Bondit sur sa cible de près |
| `llamaFollowCaravan` | un lama | `tasks` | `speed` | Se range derrière un lama tenu en laisse |
| `lookAtTradePlayer` | un villageois | `tasks` | | Fait face au joueur avec qui il échange |
| `lookAtVillager` | un golem de fer | `tasks` | | De temps en temps, tend une fleur à un villageois et le regarde |
| `lookIdle` | n'importe quelle base | `tasks` | | Regarde autour de lui de temps à autre |
| `mate` | un animal | `tasks` | `speed`, `entity` | Se reproduit lorsqu'il est en amour, avec son espèce ou avec l'`entity` nommée |
| `moveIndoors` | une créature marcheuse | `tasks` | | Rentre dans une maison du village à la tombée de la nuit |
| `moveThroughVillage` | une créature marcheuse | `tasks` | `speed`, `nocturnal` | Parcourt les chemins du village de porte en porte |
| `moveTowardsRestriction` | une créature marcheuse | `tasks` | `speed` | Retourne vers son point d'attache quand il s'en écarte |
| `moveTowardsTarget` | une créature marcheuse | `tasks` | `speed`, `distance` | Se rapproche d'une cible lointaine |
| `nearestAttackableTarget` | une créature marcheuse | `targets` | `entity`, `sight`, `nearby` | Cible l'entité nommée la plus proche |
| `ocelotAttack` | n'importe quelle base | `tasks` | | La traque et le bond du chat |
| `ocelotSit` | un chat | `tasks` | `speed` | S'assoit sur les coffres, les lits et les fourneaux allumés. Les ocelots apprivoisés sont devenus des chats : cette tâche exige donc une base de chat |
| `openDoor` | n'importe quelle base | `tasks` | `close` | Ouvre les portes en bois qu'il franchit |
| `ownerHurtByTarget` | une base apprivoisable | `targets` | | S'en prend à ce qui a frappé son maître |
| `ownerHurtTarget` | une base apprivoisable | `targets` | | S'en prend à ce que son maître a frappé |
| `panic` | une créature marcheuse | `tasks` | `speed` | Court quand elle est blessée ou en feu |
| `play` | un villageois | `tasks` | `speed` | Les enfants jouent à chat entre eux |
| `restrictOpenDoor` | une créature marcheuse | `tasks` | | Reste à l'intérieur des portes du village la nuit |
| `restrictSun` | une créature marcheuse | `tasks` | | Reste à l'ombre de jour |
| `runAroundLikeCrazy` | un cheval, un âne, une mule ou un lama | `tasks` | `speed` | Désarçonne un cavalier en qui il n'a pas encore confiance |
| `sit` | une base apprivoisable | `tasks` | | S'assoit quand on le lui ordonne |
| `skeletonRiders` | un cheval squelette | `tasks` | | Fait venir des cavaliers squelettes quand un joueur approche, le cheval piège |
| `swimming` | n'importe quelle base | `tasks` | | Garde la tête hors de l'eau |
| `targetNonTamed` | une base apprivoisable | `targets` | `entity`, `sight` | Cible l'entité nommée tant qu'il n'est pas encore apprivoisé |
| `tempt` | une créature marcheuse | `tasks` | `items`, `speed`, `scared` | Suit un joueur qui tend l'un des `items` |
| `tradePlayer` | un villageois | `tasks` | | Reste immobile pendant un échange |
| `villagerInteract` | un villageois | `tasks` | | Bavarde avec les autres villageois |
| `villagerMate` | un villageois | `tasks` | | Se reproduit quand le village a de la place |
| `wander` | une créature marcheuse | `tasks` | `speed`, `chance` | Flâne ici et là |
| `wanderAvoidWater` | une créature marcheuse | `tasks` | `speed`, `chance` | Flâne en évitant l'eau |
| `wanderAvoidWaterFlying` | une créature marcheuse | `tasks` | `speed` | Flâne dans les airs et se perche dans les arbres |
| `watchClosest` | n'importe quelle base | `tasks` | `entity`, `distance`, `chance` | Regarde l'entité nommée la plus proche, le joueur si aucune n'est nommée |
| `watchClosest2` | n'importe quelle base | `tasks` | `entity`, `distance`, `chance` | Pareil, maintenu pendant qu'une autre tâche s'exécute |
| `zombieAttack` | un zombie | `tasks` | `speed`, `memory` | L'attaque du zombie, les bras levés |

### Apparition et disparition

*variantes d'entités*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `despawns` | non | booléen | `true` | Désactivé, elle reste même quand elle serait normalement évacuée |
| `despawnAfter` | non | int, secondes | aucun | Elle disparaît discrètement dès qu'elle est restée aussi longtemps dans le monde, quelle que soit la distance de tout joueur |
| `persistent` | non | booléen | `false` | Ne disparaît jamais |
| `ignoresSpawnRules` | non | booléen | `false` | Apparaît là où on la place, sans tenir compte des règles dont elle a hérité |
| `spawns` | non | liste d'objets | aucun | `creatureType`, `weight`, `min` et `max`, la même forme que celle d'un biome. `creatureType` est l'un des [types de créatures](#listes-de-valeurs), `creature` s'il est omis, et choisit la liste d'apparition que l'entrée rejoint ; une entrée dont le type est inconnu du jeu n'ajoute rien |
| `biomes` | non | liste de noms de biomes | tous les biomes | Où ces apparitions sont ajoutées, par id de biome ou par le nom que 1.12.2 affichait pour un biome vanilla, comme `Extreme Hills`. Sans cette clé ni `biomeTypes`, tous les biomes les prennent, le Nether et l'End compris, et un biome que les deux listes retiennent prend chaque apparition une seule fois |
| `biomeTypes` | non | liste de types de biomes | aucun | Pareil, par mot de type |

**Une créature à durée de vie limitée.** `despawnAfter` compte en secondes depuis le moment où une créature entre pour la première fois dans le monde, et la retire discrètement quand le temps est écoulé : ni mort, ni butin, ni son, exactement comme si elle s'était éloignée et avait été évacuée. L'horloge est inscrite dans la créature elle-même ; elle continue donc de tourner d'une sauvegarde à l'autre au lieu de repartir de zéro chaque fois qu'un chunk revient.

C'est un mécanisme à part, pas un coup de pouce aux règles que gouvernent `despawns` et `persistent`. Ces deux clés décident si le jeu peut évacuer une créature parce qu'elle est loin de tout le monde ; celle-ci est la promesse qu'elle disparaîtra à un moment donné, quoi qu'il arrive. Une créature peut être `persistent` et avoir tout de même une durée de vie limitée, ce qui convient à quelque chose invoqué pour un combat ou un événement qui ne doit pas lui survivre.

L'horloge suit le temps du monde : elle s'arrête quand personne ne joue et ne compte pas les minutes pendant lesquelles un chunk est resté déchargé.

### Réseau

*variantes d'entités*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `trackingRange` | non | int | `80` | À quelle distance le client est informé de son existence |
| `trackVelocity` | non | booléen | `true` | Envoie sa vitesse en plus de sa position. Désactivé, cela économise du trafic pour ce qui bouge à peine |
| `trackingFrequency` | non | int | `3` | À quelle fréquence, en ticks |

### Stockage

*variantes d'entités*

```json
{
  "entity": "minecraft:pig",
  "name": "Pack Pig",
  "storage": {
    "items": {
      "filter": [
        { "item": "minecraft:coal", "max": 128 },
        { "tag": "forge:ingots/iron" }
      ]
    },
    "fluid": {
      "capacity": 16000,
      "buckets": true,
      "use": 5,
      "filter": [
        { "fluid": "minecraft:water", "max": 8000 }
      ]
    },
    "energy": { "capacity": 100000, "transfer": 1000, "use": 20 },
    "runsDry": "stops",
    "dropsOnDeath": true
  }
}
```

`storage` donne à une variante de n'importe quelle entité des emplacements d'objets, un réservoir de fluide et un tampon d'énergie, chacun seulement lorsque son objet est écrit. Chacun est proposé comme capacité d'objets, de fluide ou d'énergie de l'entité : tout ce qui déplace des objets, du fluide ou de l'énergie vers une entité l'atteint donc. Là où l'entité de base répond elle-même à cette capacité, comme le fait un mob pour ses mains et son armure, et un cheval ou un wagonnet-coffre pour son inventaire, c'est le stockage du pack qui répond à sa place, sur tous les côtés. Un joueur ouvre l'écran en s'accroupissant et en faisant un clic droit sur l'entité. Le contenu est sauvegardé avec l'entité.

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `items` | non | objet | aucun | Donne à l'entité des emplacements d'objets. La zone fait trois rangées de 9 : chaque jauge de fluide ou d'énergie prend une rangée et les emplacements occupent le reste, soit 1x9 avec les deux jauges, 2x9 avec une seule et 3x9 sans aucune. Sans `items`, seules les jauges s'affichent |
| `fluid` | non | objet | aucun | Un réservoir de fluide |
| `energy` | non | objet | aucun | Un tampon de Forge Energy |
| `dropsOnDeath` | non | booléen | `true` | Les objets stockés se répandent au sol là où l'entité meurt. `false` les perd. Le fluide et l'énergie sont perdus dans les deux cas |
| `runsDry` | non | `stops`, `slows` ou `hurts` | `stops` | Ce qui se passe tant qu'elle ne peut pas payer une seconde complète de `use`. `stops` : elle ne réfléchit plus et reste où elle est, ses tâches, cibles et comportements restent tous inactifs jusqu'au remplissage, mais elle tombe et peut toujours être poussée. `slows` : elle se déplace à demi-vitesse. `hurts` : elle subit 1 dégât par seconde, comme sous l'effet de la faim, de sorte que `immuneTo` avec `starve` l'en préserve |

**Fonctionner avec ce qu'elle porte.** Un `use` sur le réservoir ou le tampon est un coût de fonctionnement : chaque seconde, l'entité y prélève cette quantité, sans égard à `transfer`, `buckets` ni aux filtres. Quand l'un des deux contient moins qu'une seconde complète de `use`, l'entité est à sec : plus rien n'est prélevé, `runsDry` décide de ce qui se passe, et tout revient à la normale dès qu'il est rempli de nouveau. Seule une créature dépense ; sur une base qui n'est pas vivante, comme un wagonnet, `use` ne fait rien.

`items` :

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `filter` | non | liste d'entrées de filtre | aucun | Ce que les emplacements acceptent. Sans cette clé, ils acceptent tout |

`fluid` :

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `capacity` | oui | int, mB | aucun | Ce que le réservoir contient |
| `filter` | non | liste d'entrées de filtre | aucun | Les fluides que le réservoir accepte. Sans cette clé, il accepte tout |
| `buckets` | non | booléen | `false` | Un clic droit avec un seau ou un autre conteneur de fluide, sans s'accroupir, le vide dans le réservoir ou le remplit depuis le réservoir. Un clic qui ne déplace aucun fluide est laissé à l'entité |
| `use` | non | int, mB par seconde | `0` | Ce que l'entité consomme du réservoir chaque seconde où elle est en vie. `0` ne coûte rien |

`energy` :

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `capacity` | oui | int, FE | aucun | L'énergie qu'il contient |
| `transfer` | non | int, FE | sans limite | La plus grande quantité d'énergie déplacée, en entrée ou en sortie, en une opération |
| `use` | non | int, FE par seconde | `0` | L'énergie que l'entité consomme chaque seconde où elle est en vie. `0` ne coûte rien |

Une entrée de filtre. La première entrée qui correspond décide, et tout ce qu'aucune entrée ne retient est refusé :

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `item` | l'une des trois | id d'objet | aucun | Un objet, sous la forme `namespace:name` |
| `tag` | l'une des trois | id de tag d'objet | aucun | Tous les objets de ce tag, comme `forge:ingots/iron` |
| `fluid` | l'une des trois | id de fluide | aucun | Un fluide par son id, comme `minecraft:water`. Lu seulement par un filtre de fluide |
| `max` | non | int | `0` | La plus grande quantité conservée à la fois, comptée sur tous les emplacements, ou en mB pour un fluide. `0` signifie sans limite |

## Expositions

*créatures et dangers*

`<namespace>/exposures/*.json`

Le chemin du fichier est le nom du danger, et son message de mort provient de la clé de lang `death.attack.rdpl.<file name>`. Les expositions ne se chargent que lorsque `load` est activé et `vanillaClients` désactivé.

Un danger défini par un pack : des blocs, objets et dimensions nommés exposent les joueurs qui se tiennent près de ces blocs, portent ces objets ou séjournent dans ces dimensions, par niveaux, chaque niveau appliquant des effets et des dégâts périodiques. Un danger peut aussi se contracter auprès des mobs et des joueurs proches, ou tomber avec la pluie ([Contagion et météo](#contagion-et-météo)). Un fichier définit un danger ; plusieurs fonctionnent côte à côte.

```json
{
  "blocks": [ "mypack:nuclear_waste=2", "mypack:uranium_ore" ],
  "items": [ "mypack:nuclear_waste" ],
  "dimensions": [ "minecraft:the_nether" ],
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

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `blocks` | l'une des cinq | liste de `block` ou `block=level` | | Blocs qui exposent un joueur se tenant près d'eux. Sans niveau, c'est 1 |
| `items` | l'une des cinq | liste de `item` ou `item=level` | | Objets qui exposent un joueur qui les porte ou les revêt |
| `dimensions` | l'une des cinq | liste de `dim` ou `dim=level` | | Ids de dimensions qui exposent tout joueur qui s'y trouve |
| `levels` | oui | liste de niveaux | | L'échelle de gravité, la première entrée est le niveau 1. Un joueur reçoit le plus haut niveau que l'une des sources atteint |
| `immunity` | non | nom de potion | aucun | Un effet dont le porteur n'est pas exposé du tout |
| `scanInterval` | non | ticks | `20` | À quelle fréquence l'environnement et l'inventaire sont vérifiés |
| `range` | non | blocs | `10` | Jusqu'où porte l'exposition d'un bloc, sous forme de sphère |
| `sourcesForNextLevel` | non | int | `0` | Ce nombre de sources proches d'un même niveau le fait monter d'un niveau. `0` désactive cela |
| `skipsCreative` | non | booléen | `true` | Les joueurs en créatif et en spectateur sont épargnés |

### Niveaux

*expositions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `effect` | oui | nom de potion | | L'effet qui marque le niveau sur le joueur. Sa présence déclenche les dégâts : il doit donc être un effet que le pack définit pour cela |
| `damage` | non | demi-cœurs | `0` | Dégâts infligés tous les `damageInterval` ticks tant que le niveau persiste. Ils ignorent l'armure |
| `damageInterval` | non | ticks | `160` | À quelle fréquence ces dégâts sont infligés |
| `effects` | non | liste d'effets | aucun | Effets supplémentaires appliqués en même temps, de la même forme que celle des types de potions. Sans `duration`, ils suivent la fenêtre d'analyse |

Les effets de niveau durent un peu plus longtemps que l'analyse suivante : en s'éloignant, ils s'éteignent donc d'eux-mêmes. Un joueur mort des dégâts d'exposition voit un message tiré de `death.attack.rdpl.<file name>`, que fournissent les fichiers de lang du pack.

### Contagion et météo

*expositions*

Deux sources de plus, écrites dans le même fichier. Les porteurs et les sujets exposés transmettent le danger aux receveurs qui les entourent, et la pluie ou l'orage exposent les joueurs sur lesquels ils tombent.

```json
{
  "carriers": [ "minecraft:zombie_villager=2" ],
  "contagious": true,
  "catchers": [ "minecraft:player", "minecraft:villager" ],
  "contagionRange": 4,
  "contagionChance": 0.1,
  "contagionDuration": 1200,
  "weather": [ "rain", "thunder=2" ],
  "weatherDimensions": [ "minecraft:overworld" ],
  "levels": [ { "effect": "mypack:sickness_1", "damage": 1.0 }, { "effect": "mypack:sickness_2", "damage": 2.0, "damageInterval": 80 } ]
}
```

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `carriers` | l'une des cinq | liste de `entity` ou `entity=level` | | Mobs, ou `minecraft:player`, qui transmettent toujours le danger à ce niveau. Sans niveau, c'est 1 |
| `contagious` | non | booléen | `false` | Toute personne exposée transmet le danger au niveau qu'elle détient |
| `catchers` | non | liste de noms d'entités | `minecraft:player` | Qui peut le contracter. Un mob ne le contracte qu'auprès de porteurs et de sujets exposés, jamais auprès de blocs, d'objets ou de la météo |
| `contagionRange` | non | blocs | `4` | Jusqu'où porte un porteur ou un sujet exposé, sous forme de sphère |
| `contagionChance` | non | `0` à `1` | `0.1` | La probabilité, à chaque analyse du porteur ou du sujet exposé, que chaque receveur à portée le contracte |
| `contagionDuration` | non | ticks | `1200` | Combien de temps un danger contracté conserve le niveau contracté. Le contracter de nouveau fait repartir le décompte |
| `weather` | l'une des cinq | liste de `kind` ou `kind=level` | | `rain` expose un joueur sur qui la pluie tombe : ciel dégagé au-dessus de lui, dans un biome où il pleut. `thunder` compte pendant un orage |
| `weatherDimensions` | non | liste de `dim` | toutes les dimensions | Ids de dimensions où la météo expose |

Un danger contracté compte comme une source de plus dans l'analyse, le niveau le plus haut l'emportant comme pour toute autre, et `immunity` en protège aussi. Rien ne se propage tant que `contagionRange` et `contagionChance` ne sont pas supérieurs à `0` et que le fichier ne nomme pas `carriers` ou ne définit pas `contagious` ; les mobs ne sont examinés que si un fichier le demande.

---

# Le monde

## Modèles de monde

*le monde*

`<namespace>/worldtemplates/*.json`

Le chemin du fichier est le nom du modèle, que l'option de configuration `worldTemplate` peut nommer pour le choisir d'office.

Rassemble la forme d'un monde dans un seul fichier, afin qu'un pack livre un monde entier d'un coup plutôt que de demander au joueur de régler une douzaine d'options de configuration.

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

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `name` | non | chaîne | le nom du fichier | Affiché dans le journal et dans les rapports |
| `default` | non | nom de biome ou `void` | `void` | Ce qui remplit un biome que le blocage a retiré. `void` y laisse le biome du vide. Un biome non enregistré est consigné dans le journal et le vide est utilisé. `fallback` est la même clé sous un autre nom |
| `roles` | non | objet de rôle vers biome | aucun | Biomes qui remplissent des rôles particuliers : `ocean`, `river`, `beach`, `mushroom`, `swamp`, `hills`, `mountain`, `jungle`, `forest`, `savanna`, `sandy`, `mesa`, `snowy`, `wasteland`, `plains` et `water`, examinés dans cet ordre quel que soit l'ordre dans lequel le fichier les écrit : un biome bloqué qui est à la fois océan et enneigé prend donc le rôle d'océan. Un rôle qui nomme `void` ou un biome non enregistré passe au suivant. Les rôles ne s'appliquent que dans les `dimensions` du modèle ; ailleurs, un biome bloqué devient le vide |
| `structures` | non | objet de [nom de structure](#listes-de-valeurs) vers booléen | aucun | Structures vanilla activées ou désactivées |
| `settings` | non | objet | aucun | Valeurs de configuration que le modèle définit |
| `dimensions` | non | liste d'ids de dimensions | toutes les dimensions | Les dimensions auxquelles il s'applique |
| `requires` | non | liste d'ids de mods ou de namespaces de packs | aucun | Le modèle est ignoré à moins que tous ne soient présents |

`settings` utilise les mêmes noms de clés que la configuration : il n'y a donc aucune table de correspondance à apprendre.

Le modèle actif est déterminé par l'option de configuration `worldTemplate`. Laissée sur `auto`, c'est le pack de plus haute priorité qui en livre un qui l'emporte, selon le même ordre que tout le reste ; quand plusieurs packs livrent un modèle, le journal les nomme tous ainsi que celui qui est en vigueur, puisque les autres ne font rien, réglages compris. En nommer un ici le choisit d'office. Cinq sont intégrés et peuvent être nommés ainsi : `void`, `vanilla` (océans, rivières, plages, champignonnières, marais et collines conservés comme ceux du jeu, plaines partout ailleurs), `ocean` (rivières et plages conservées, océan partout ailleurs), `plains` et `desert`. `auto` ne choisit jamais un modèle intégré.

**Un biome peut se construire différemment.** Un objet `biomes` dans `settings` contient des réglages de village propres à un biome nommé : un village du désert pose ainsi des rues de grès là où un village de plaines pose du béton, sans qu'il faille deux packs distincts. Nommez un biome par son id, `minecraft:desert`, par l'un des mots de type que ce mod associe aux tags de biomes (`sandy`, `snowy`, `desert`, `forest`, `jungle`, `mountain`, `ocean`, `swamp`, `hot`, `cold` et les autres), ou par un tag écrit en toutes lettres, `#minecraft:is_forest` ; un id exact est examiné avant les types, de sorte qu'une règle générale peut être surchargée pour un seul biome. Tout ce qui n'est pas nommé dans une section retombe sur le réglage simple situé au-dessus.

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

Chaque réglage de bloc qu'acceptent une route, un pont, une voie ferrée, un métro, une station ou un égout y répond, et la syntaxe de mélange pondéré fonctionne dans une section comme en dehors. Le biome est lu au moment où une pièce est construite, et les blocs sont repris partout où le sol change de biome : une route ou une voie ferrée qui sort d'un désert change donc de matériau à la frontière même. Une ligne de journal au chargement du monde indique combien de sections un pack a livrées et les nomme, et, avec le débogage activé, chaque biome indique quelle section il a prise, ou qu'il n'en a pris aucune et à quoi il aurait répondu.

## Règles de jeu

*le monde*

`<namespace>/gamerules/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers se cumulent.

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

Chaque clé est l'id du monde auquel les règles appartiennent : `minecraft:overworld`, `minecraft:the_nether`, `minecraft:the_end`, celui d'un pack ou celui qu'utilise un mod ; les nombres de 1.12.2, `0`, `-1` et `1`, sont toujours acceptés pour les trois mondes vanilla. Les valeurs sont des chaînes, comme dans la commande `/gamerule`, donc `"false"` plutôt que `false`. Elles s'appliquent aux nouveaux mondes. Une règle qu'un fichier omet prend la valeur par défaut du jeu dans ce monde, et non la valeur que le reste de la sauvegarde utilise, et un client qui a le pack lit les mêmes règles. Un fichier de dimension porte les mêmes règles dans un bloc `gameRules` à la place, qui ne s'applique jamais qu'à ce monde.

## Biomes

*le monde*

`<namespace>/biomes/*.json`

Le chemin du fichier est le nom de registre du biome : `mypack/biomes/ruby_forest.json` enregistre donc `mypack:ruby_forest`. `name` n'est que ce qui est montré au joueur, et `biome.mypack.ruby_forest` dans les fichiers de lang le dit dans chaque langue.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin.

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
  "snowColor": "E8F0FF",
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

### Le biome

*biomes*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `name` | non | chaîne | le nom du fichier | Nom montré au joueur |
| `types` | non | liste de types de biomes | deviné | Inscrit le biome dans les tags que ces mots de type désignent, comme `forest`, `cold`, `wet` ou `nether`, afin que d'autres mods le trouvent. Omis, les types sont devinés à partir du biome, comme le jeu les devinait : `forest` ou `jungle` à partir de trois arbres ou plus, `plains` sinon, `hot`, `cold`, `wet` et `dry` d'après la température et les précipitations, `sparse` ou `dense` d'après le nombre d'arbres, `snowy` d'après `snow`, et `sandy`, `mushroom` ou `mesa` d'après un sol de sable, de mycélium ou de terre cuite |
| `baseBiome` | non | nom de biome | `minecraft:plains` | Un biome existant dont copier les réglages. Un nom qui n'est pas un biome fourni par le jeu ou un mod est consigné dans le journal, et les plaines sont utilisées |
| `requires` | non | liste d'ids de mods ou de namespaces de packs | aucun | Le fichier est ignoré à moins que tous ne soient présents |

Sur cette version, un biome est une entrée de data pack, écrite pour vous sous `worldgen/biome/`, et le terrain qui se trouve dessous relève des réglages de bruit plutôt que du biome, ce qui explique l'absence de `baseHeight` et de `heightVariation` : la forme du terrain vient de l'endroit où le climat place le biome, comme pour ceux du jeu. Un `id` de 1.12.2 est lu puis ignoré.

### Climat

*biomes*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `temperature` | non | flottant | `0.5` | Sous 0,15, il neige ; au-dessus de 1,0, c'est la chaleur du désert |
| `rainfall` | non | flottant, 0 à 1 | `0.5` | À quel point il est humide |
| `rain` | non | booléen | `true` | Si la météo se produit tout court |
| `snow` | non | booléen | `false` | Si la pluie tombe sous forme de neige. La neige ne tombe que là où la température est inférieure à 0,15, et cette clé ne change pas la température : un biome plus chaud connaît donc toujours la pluie |

### Sol et couleurs

*biomes*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `topBlock` | non | nom de bloc | herbe | Le bloc de surface |
| `fillerBlock` | non | nom de bloc | terre | Juste sous la surface |
| `stoneBlock` | non | nom de bloc | pierre | L'essentiel du sol |
| `waterColor` | non | couleur hexadécimale | `FFFFFF` | Teinte de l'eau |
| `grassColor` | non | couleur hexadécimale | selon le climat | Teinte de l'herbe, à la place de la couleur que donneraient la température et les précipitations |
| `foliageColor` | non | couleur hexadécimale | selon le climat | Teinte des feuilles, de la même manière |
| `snowColor` | non | couleur hexadécimale | celle de la dimension | Teinte de la neige au sol, prioritaire sur le `snowColor` de la dimension |

### Décoration et apparitions

*biomes*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `decoration` | non | objet | celle du biome de base | Nombres par chunk, qui modifient ce que le biome de base place déjà. Les noms lus sont `trees`, `flowers`, `grass`, `deadbush`, `mushrooms`, `bigmushrooms`, `reeds`, `cacti`, `sand`, `gravel`, `clay` et `waterlily`, plus les interrupteurs `falls` (lacs et sources), `pumpkins`, `desertwells`, `ice` (pics et plaques de glace), `fossils` et `rocks` (rochers de forêt), où toute valeur supérieure à zéro conserve la fréquence propre du biome de base et zéro ou moins supprime l'élément, et `extratreechance`, un pourcentage de chances d'avoir un arbre de plus, où `0` supprime aussi l'arbre supplémentaire que le biome de base tire au sort. Un nombre sur un type que le biome de base ne place pas n'ajoute rien ; écrivez pour cela une entrée de worldgen. Tout autre nom est consigné dans le journal et ignoré |
| `spawns` | non | liste d'objets | la liste vanilla | Voir ci-dessous |
| `keepDefaultSpawns` | non | booléen | `false` | Conserve la liste de vanilla en plus de la vôtre |
| `spawnChance` | non | flottant, inférieur à 1 | `0.1` | La probabilité qu'un autre troupeau soit placé lors de la première création du terrain. Le jeu continue de tirer tant que cela réussit : 1 ne s'arrête donc jamais et remplit le monde jusqu'à épuisement de la place. Toute valeur supérieure ou égale à 0,99 est refusée et 0,99 est utilisé |
| `spawnRates` | non | objet de `surfaceDay`, `surfaceNight`, `undergroundDay`, `undergroundNight` vers un multiplicateur | aucun | À quelle fréquence les mobs hostiles apparaissent ici, à la place des réglages globaux. Voir ci-dessous |

Une entrée d'apparition accepte `entity` (requis), `type` (`creature`, l'un de `monster`, `creature`, `ambient` ou `water`, les tirets bas d'un nom tel que `water_creature` étant facultatifs), `weight` (`10`), `min` (`1`) et `max` (`min`).

`spawnRates` ne concerne que les mobs hostiles, et rien d'autre. Elle accepte quatre clés et aucune autre : `surfaceDay` et `surfaceNight` pour les endroits où l'on voit le ciel, `undergroundDay` et `undergroundNight` pour ceux où on ne le voit pas. Chacune est un multiplicateur de la fréquence à laquelle un mob hostile a le droit d'apparaître : `1` est le taux ordinaire, `0` les arrête complètement, une valeur inférieure à 1 refuse certaines tentatives, et une valeur supérieure à 1 laisse passer des tentatives que le jeu aurait autrement refusées, si bien que `2` donne deux fois plus d'apparitions. Une clé omise signifie que le biome ne décide pas, et c'est le réglage global pour ce moment et ce lieu qui est utilisé. Tout ce qui est écrit d'autre ici n'est pas une clé et est ignoré : un taux nommé d'après un type de créature ne fait donc rien du tout.

### Où il se génère

*biomes*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `placement` | non | objet | aucun | Où il se génère. Voir ci-dessous |
| `villageType` | non | `oak`, `sandstone`, `acacia` ou `spruce` | aucun | De quoi est construit un village situé ici : le village de plaines, du désert, de la savane ou de la taïga. Vide, il construit celui des plaines, comme il le ferait sans la clé |

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `climate` | non | `icy`, `cool`, `medium`, `warm` ou `desert` | aucun | La bande climatique qu'il rejoint, les mêmes cinq par lesquelles les biomes de l'overworld sont répartis. Omise, laissée à un `weight` de 0 ou nommant un climat absent de cette liste, le biome est enregistré mais jamais placé, à moins que les `roles` d'un modèle, le `biome` d'une dimension ou une bande d'altitude ne le demandent |
| `weight` | non | int | `10` | À quelle fréquence il est choisi face à ses voisins dans cette bande |
| `villages` | non | booléen | `false` | Les villages peuvent se générer |
| `strongholds` | non | booléen | `false` | Les forteresses peuvent se générer |
| `playerSpawn` | non | booléen | `false` | Le point d'apparition du monde peut être placé ici |

### Bandes d'altitude

*biomes*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `minHeight` | non | int | aucun | Le y le plus bas à partir duquel ce biome prend le relais comme biome 3D. Définir l'une ou l'autre hauteur transforme le biome en bande : la colonne garde son propre biome en dehors de la bande, et à l'intérieur, chaque cellule de 4 par 4 par 4 du monde indique celui-ci |
| `maxHeight` | non | int | aucun | Le y le plus haut de cette bande |
| `replaces` | non | liste de noms de biomes | tous les biomes | Restreint la bande aux colonnes dont le biome propre est nommé ici, de sorte qu'une bande alpine puisse recouvrir des montagnes et rien d'autre. Le biome propre d'une colonne est celui de sa surface. Sans `minHeight` ni `maxHeight`, elle ne fait rien |

### Température selon l'altitude

*biomes*

**Température selon l'altitude.** Un biome se refroidit en s'élevant, ce qui met de la neige au sommet des montagnes et arrête la pluie au-dessus d'une certaine ligne. Trois clés de `terrain` déplacent cette courbe, ce qui compte sur une dimension dont le sol se trouve bien au-dessus ou bien en dessous de la hauteur que le jeu suppose. Non définies, la courbe propre au jeu reste en place : un pack qui n'y touche pas ne change donc rien.

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

| Clé | Valeur | Défaut | Rôle |
| --- | --- | --- | --- |
| `biomeTemperatureCenterY` | int | `80` | La hauteur à partir de laquelle la courbe est mesurée. À cette hauteur ou en dessous, un biome indique sa propre `temperature` intacte |
| `biomeTemperatureHeightFactor` | flottant | `-0.00125` | De combien la température varie par bloc au-dessus de cette hauteur, soit le 0,05 du jeu sur 40 blocs. Une valeur négative refroidit avec l'altitude, une valeur positive réchauffe |
| `biomeTemperatureScaleMaxY` | int | aucun | La hauteur où la courbe s'arrête, afin qu'un monde plus haut que celui du jeu ne continue pas de se refroidir jusqu'à son plafond. Non défini, la courbe va jusqu'au sommet du monde |

## Dimensions

*le monde*

`<namespace>/dimensions/*.json`

Le chemin du fichier est l'id de la dimension : `mypack/dimensions/verdant.json` est donc `mypack:verdant`, ce que nomment un portail, un passage, un fichier de règles de jeu et `/execute in`. Il n'existe pas d'id numérique sur cette version, et un `id` ou `suffix` de 1.12.2 est lu puis ignoré.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin.

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
    "renderWeather": true,
    "sun": { "texture": "mypack:textures/environment/red_sun.png", "size": 18 },
    "bodies": [
      { "texture": "mypack:textures/environment/twin_moon.png", "size": 12, "angle": 150, "tilt": 20 },
      { "texture": "mypack:textures/environment/home.png", "size": 6, "angle": 20, "tilt": 40, "followsTime": false }
    ],
    "stars": { "count": 6000, "size": 0.12 }
  },
  "physics": { "gravity": 0.4, "fallDamage": 0.5, "arrowGravity": 0.3 },
  "time": { "dayLength": 36000 },
  "weather": {
    "precipitation": true,
    "lightning": true,
    "snow": false,
    "freeze": false,
    "cycle": { "rainTicks": [1000, 4600], "clearTicks": [1000, 3000], "maxStrength": 0.6, "thunderTicks": [3600, 15600], "calmTicks": [12000, 60000], "thunderStrength": 1.0 },
    "rain": { "particle": "minecraft:rain", "sound": "minecraft:weather.rain", "volume": 0.2, "interval": 3, "color": "#88AAFF", "snowColor": "#FFFFFF", "angle": 30, "heading": 90 },
    "wind": { "gust": 15, "every": [200, 600], "swing": 30 }
  },
  "ambience": { "music": "mypack:music.ruby", "musicDelay": [1200, 3600], "loopSound": "mypack:ambient.ruby_wind", "ambientSound": "minecraft:ambient.cave", "soundChance": 0.0111, "particle": "minecraft:dust", "particleChance": 0.00625, "particleColor": "#FF4060" },
  "gameRules": { "doMobSpawning": "false" }
}
```

### Niveau supérieur

*dimensions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `gameRules` | non | objet | aucun | Règles qui ne s'appliquent qu'ici |
| `portal` | non | objet | aucun | Un cadre qui ouvre cette dimension. Voir [Ouvrir une dimension avec un cadre](#ouvrir-une-dimension-avec-un-cadre) |
| `requires` | non | liste d'ids de mods ou de namespaces de packs | aucun | Le fichier est ignoré à moins que tous ne soient présents |

Sur cette version, une dimension est une entrée de data pack : le type de dimension et les réglages de bruit sont écrits pour vous sous le namespace du pack, de sorte qu'un client vanilla en est informé en se connectant et s'y rend comme dans n'importe quelle autre. La dimension garde son propre dossier de sauvegarde sous le monde, nommé d'après son id, et elle est chargée tant que quelqu'un s'y trouve, ou tant qu'un `forceload` retient un chunk.

### Le bloc `terrain`

*dimensions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `type` | non | `overworld`, `flat`, `void`, `nether`, `end` | `overworld` | Lequel des générateurs du jeu la construit, avec ses réglages de bruit copiés puis modifiés par les clés ci-dessous |
| `minHeight` | non | int, multiple de 16 | celle du type | Le plancher de la dimension. Plus bas que celui du type, cela crée un monde profond sous le terrain, voir [Le monde profond](#le-monde-profond) |
| `maxHeight` | non | int, multiple de 16 | celle du type | Le bloc situé au-dessus de son sommet |
| `generatorOptions` | non | objet, texte ou liste | aucun | Pour `overworld` et les autres, un objet, ou le texte d'un objet tel que 1.12.2 l'écrivait, avec `seaLevel`, `useLavaOceans`, et `useCaves`, `useRavines`, `useDungeons`, `useLavaLakes`, `useStrongholds`, `useVillages`, `useMineShafts`, `useTemples`, `useMonuments` et `useMansions` à false pour les exclure de cette dimension. Pour `flat`, les couches, de bas en haut, sous la forme `"minecraft:bedrock"`, `"59*minecraft:stone"`, `"3*minecraft:dirt"`, `"minecraft:grass_block"`, qui est aussi le sol par défaut, ou le texte superflat de 1.12.2, dont le numéro de biome définit le biome et dont les noms `decoration`, `lava_lake` et de structures sont lus comme les lit le `generatorOptions` de l'overworld |
| `structures` | non | booléen | `true` | Si les structures vanilla se génèrent |

### Le bloc `biomes`

*dimensions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `source` | non | `inherit`, `single` | `inherit` | `inherit` utilise la carte de biomes de l'overworld quel que soit le type de terrain : une dimension `nether` ou `end` reçoit donc les biomes de l'overworld sur son propre sol ; `single` utilise un seul biome partout. Une dimension `flat` ne contient qu'un seul biome dans les deux cas, celui de `single` ou celui de son texte superflat |
| `biome` | quand `single` | nom de biome | `minecraft:plains` | Quel est ce biome |

### Le bloc `sky`

*dimensions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `hasSkyLight` | non | booléen | `true` | Si la lumière du jour l'atteint |
| `surfaceWorld` | non | booléen | `true` | Si les cartes et les boussoles se comportent comme dans l'overworld |
| `respawn` | non | booléen | `true` | Si les joueurs réapparaissent ici |
| `respawnDimension` | non | id de dimension | aucun | Où ils réapparaissent à la place |
| `spawning` | non | booléen | `true` | Si les mobs apparaissent. Désactivé, cela arrête toute apparition, générateurs compris, quoi que dise le groupe `spawning` |
| `nether` | non | booléen | `false` | Traitée comme le Nether pour les portails et les plafonds |
| `beds` | non | booléen | `true` | Désactivé, les lits explosent |
| `waterVaporizes` | non | booléen | `false` | L'eau s'évapore |
| `cloudHeight` | non | int | `128` | Où se trouvent les nuages. Un réglage `cloudHeight` qui nomme cette dimension, ou un réglage sans nom, l'emporte sur cette clé |
| `cloudColor` | non | couleur hexadécimale | aucun | Teinte des nuages |
| `cloudSpeed` | non | flottant | `1.0` | À quelle vitesse les nuages dérivent. `0` les immobilise, une valeur négative les fait aller en sens inverse |
| `cloudLayers` | non | liste d'objets | aucun | Plusieurs couches de nuages. Voir [Brouillard, lumière, nuages et chaleur](#brouillard-lumière-nuages-et-chaleur) |
| `groundLevel` | non | int | `63` | Le niveau de la mer, utilisé pour l'horizon, pour les recherches de point d'apparition et pour l'endroit où se pose une arrivée par passage ou une chute au-dessus du vide |
| `movementFactor` | non | flottant | `1.0` | Rapport de distance avec l'overworld. Le Nether utilise 8 |
| `fogColor` | non | couleur hexadécimale ou `sample` | aucun | Teinte du brouillard à midi. Elle s'assombrit la nuit comme le brouillard vanilla. `sample` mélange le ciel avec le sol autour du joueur |
| `showFog` | non | booléen | `false` | Brouillard épais, comme dans le Nether |
| `fogDensity` | non | flottant, 0 à 1 | `0.0` | L'épaisseur du brouillard. `0` conserve la distance vanilla, `1` la resserre à 8 blocs |
| `fogGroundWeight` | non | flottant, 0 à 1 | `0.5` | Avec `fogColor: sample`, le poids du sol face au ciel |
| `skyColor` | non | couleur hexadécimale | aucun | Teinte du ciel à midi. Elle s'assombrit la nuit et grisonne sous la pluie et l'orage comme le ciel vanilla |
| `fixedTime` | non | int, ticks | aucun | Verrouille l'heure de la journée |
| `sunriseColors` | non | booléen | `true` | Si le lever et le coucher du soleil sont teintés |
| `ambientLight` | non | flottant, 0 à 1 | `0.0` | Lumière minimale partout |
| `lightSkyColor` | non | couleur hexadécimale | aucun | Teinte de la lumière du jour sur les blocs et les mobs |
| `lightBlockColor` | non | couleur hexadécimale | aucun | Teinte de la lumière des torches et des autres lumières de blocs |
| `skyFactor` | non | flottant, 0 à 1 | `1.0` | À quel point la lumière du jour paraît vive. Dessinée uniquement côté client : l'apparition des mobs ne change donc pas |
| `starBrightness` | non | flottant, 0 à 1 | aucun | L'éclat des étoiles |
| `sunBrightness` | non | flottant, 0 à 1 | `1.0` | L'éclat avec lequel le soleil est dessiné |
| `moonBrightness` | non | flottant, 0 à 1 | `1.0` | L'éclat avec lequel la lune est dessinée, et, avec `bodies`, celui de tous les corps sauf le soleil |
| `heat` | non | objet | aucun | Une brume de chaleur sur la vue. Voir [Brouillard, lumière, nuages et chaleur](#brouillard-lumière-nuages-et-chaleur) |
| `renderSky` | non | booléen | `true` | Désactivé, rien ne dessine le ciel, le soleil, la lune ni les étoiles, ce qui ne laisse que la couleur du brouillard |
| `renderClouds` | non | booléen | `true` | Désactivé, aucun nuage n'est dessiné |
| `renderWeather` | non | booléen | `true` | Désactivé, ni pluie ni neige ne sont dessinées |
| `sun` | non | objet | aucun | Votre propre soleil. Voir [Le moteur de rendu du ciel](#le-moteur-de-rendu-du-ciel) |
| `bodies` | non | liste d'objets | aucun | Planètes et lunes suspendues dans le ciel. Voir [Le moteur de rendu du ciel](#le-moteur-de-rendu-du-ciel) |
| `stars` | non | objet | aucun | Votre propre champ d'étoiles. Voir [Le moteur de rendu du ciel](#le-moteur-de-rendu-du-ciel) |

### Le moteur de rendu du ciel

*dimensions*

Définir l'un de `sun`, `bodies` ou `stars` remplace le ciel vanilla par celui de RDPL, qui dessine le même dôme, la même lueur de l'aube et le même vide que vanilla, mais prend le soleil, les autres corps et les étoiles dans le pack. Il n'est dessiné que côté client, et un serveur dédié ne le charge jamais. `renderSky: false` l'emporte toujours et ne dessine rien, et `renderClouds: false` est la façon d'obtenir un ciel sans nuages.

Sans `bodies`, la lune vanilla et ses phases restent. Avec `bodies`, la liste contient tout ce qui n'est pas le soleil : une liste vide donne donc un ciel sans lune.

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `sun.texture` | non | chemin de texture | le soleil vanilla | L'image du soleil |
| `sun.size` | non | flottant | `30` | La moitié de la largeur du soleil à une distance de 100. `0` le cache |
| `bodies[].texture` | oui | chemin de texture | | L'image du corps |
| `bodies[].size` | non | flottant | `20` | La moitié de sa largeur à une distance de 100. La lune vanilla vaut `20` |
| `bodies[].angle` | non | flottant, degrés | `180` | À quelle distance, le long de la trajectoire du soleil, il se trouve derrière lui. `180` est l'emplacement de la lune vanilla. Avec `followsTime` désactivé, il se mesure depuis la verticale, de sorte que `0` est le zénith et `90` l'horizon |
| `bodies[].tilt` | non | flottant, degrés | `0` | À quelle distance il se trouve de la trajectoire du soleil, vers le nord ou le sud |
| `bodies[].followsTime` | non | booléen | `true` | Désactivé, il reste immobile dans le ciel au lieu de tourner avec le soleil |
| `stars.count` | non | int | `1500` | Combien d'étoiles |
| `stars.size` | non | flottant | `0.15` | La plus petite étoile ; la plus grande est encore deux tiers plus grosse |

### Brouillard, lumière, nuages et chaleur

*dimensions*

Ces clés se placent dans le bloc `sky` à côté des plus anciennes, qui continuent de fonctionner comme avant. Elles ne sont toutes dessinées que côté client : un serveur dédié les ignore donc et aucune sauvegarde ne change.

```json
{
  "sky": {
    "fogColor": "sample",
    "fogDensity": 0.4,
    "fogGroundWeight": 0.6,
    "lightSkyColor": "#FFD8A0",
    "lightBlockColor": "#A0C0FF",
    "skyFactor": 0.7,
    "cloudSpeed": 2.0,
    "cloudLayers": [
      { "height": 140, "speed": 1.0, "color": "#FFFFFF" },
      { "height": 220, "speed": -3.0, "color": "#C0A0FF" }
    ],
    "sunBrightness": 0.5,
    "moonBrightness": 0.3,
    "heat": { "strength": 0.1, "minTemperature": 1.5, "dayOnly": true, "mode": "world", "startDistance": 32 }
  }
}
```

`fogColor: "sample"` lit les blocs du dessus dans un carré de 33 par 33 blocs autour du joueur une fois par seconde, éclaire leurs couleurs de carte selon l'heure de la journée et les mélange avec la couleur du ciel. Le brouillard glisse progressivement vers chaque nouvel échantillon. `fogDensity` fonctionne avec une couleur de brouillard échantillonnée, définie ou absente. Sous l'eau, dans la lave et pendant la cécité, le brouillard vanilla reste.

`lightSkyColor` et `lightBlockColor` teintent la carte de lumière : chaque bloc éclairé et chaque mob prend donc la teinte. `skyFactor` modifie l'éclat apparent de la lumière du jour, tandis que le niveau de lumière que le serveur compte pour l'apparition et les cultures reste le même.

Sans `cloudLayers`, `cloudSpeed` change la vitesse de l'unique couche vanilla à `cloudHeight`. Avec `cloudLayers`, chaque entrée est une couche à part entière, et `cloudHeight`, `cloudSpeed` et `cloudColor` comblent ce qu'une entrée omet. `renderClouds: false` n'en dessine toujours aucune.

`sunBrightness` et `moonBrightness` atténuent le soleil et la lune en plus de l'atténuation vanilla due à la pluie, dans le ciel vanilla comme dans le vôtre issu du [moteur de rendu du ciel](#le-moteur-de-rendu-du-ciel).

`heat` pose une brume ondulante sur la vue tant que le joueur se tient dans un biome au moins aussi chaud que `minTemperature`. Un désert vaut 2,0 et les plaines 0,8. La brume apparaît et disparaît en fondu sur quelques secondes et reste désactivée sous l'eau. Elle exige la prise en charge des shaders par la carte graphique et reste désactivée tant qu'un autre shader plein écran, comme la vue spectateur, est actif.

`mode` décide où la brume se pose. `screen` déforme une bande fixe dans le bas de l'écran, où que le joueur regarde. `world` suit le terrain : ce qui se trouve à moins de `startDistance` blocs reste net, la brume s'intensifie vers le bord lointain de la distance d'affichage, là où le brouillard se referme, et le ciel n'est jamais touché, que le joueur regarde vers le bas, droit devant ou vers le haut.

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `cloudLayers[].height` | non | flottant | `cloudHeight` | Où se trouve la couche |
| `cloudLayers[].speed` | non | flottant | `cloudSpeed` | À quelle vitesse elle dérive. `0` l'immobilise, une valeur négative la fait aller en sens inverse |
| `cloudLayers[].color` | non | couleur hexadécimale | `cloudColor` | Sa teinte |
| `heat.strength` | non | flottant, 0 à 1 | `0.1` | L'intensité de la brume |
| `heat.minTemperature` | non | flottant | `1.5` | La température de biome la plus basse qui produit une brume |
| `heat.dayOnly` | non | booléen | `true` | Activé, la brume s'estompe avec la lumière du jour et disparaît la nuit |
| `heat.mode` | non | chaîne | `screen` | Où la brume se pose, `screen` ou `world` |
| `heat.startDistance` | non | flottant | `32` | En mode `world`, à combien de blocs de distance la brume commence |

### Neige, fluides, étoiles et éclairs

*dimensions*

Ces clés se placent elles aussi dans le bloc `sky` et ne sont, elles aussi, dessinées que côté client.

```json
{
  "sky": {
    "snowColor": "#C8E0FF",
    "waterFogColor": "#103040",
    "lavaFogColor": "#802000",
    "starColor": "#FFE0A0",
    "starTwinkle": 0.5,
    "lightningColor": "#A080FF"
  }
}
```

`snowColor` teinte les couches de neige et les blocs de neige au sol. Le `snowColor` propre à un biome l'emporte sur celui de la dimension, et les couleurs se fondent aux frontières entre biomes, comme pour l'herbe.

`waterFogColor` et `lavaFogColor` remplacent la couleur du brouillard que voit la caméra sous l'eau ou dans la lave. La nuit, la profondeur et la vision nocturne continuent de l'assombrir ou de l'éclaircir, comme pour la couleur vanilla.

Dans cette version, `waterFogColor` remplace dans toute la dimension le `water_fog_color` que chaque biome porte dans ses `effects`. Sans la clé, le `water_fog_color` d'un JSON de biome vanilla fonctionne comme d'habitude.

`starColor` teinte les étoiles, dans le ciel vanilla comme dans le vôtre issu du [moteur de rendu du ciel](#le-moteur-de-rendu-du-ciel). `starTwinkle` les fait scintiller : les étoiles se répartissent en huit groupes qui faiblissent et se ravivent chacun à son rythme, et la valeur indique jusqu'où elles faiblissent ; à `1`, un groupe s'éteint complètement au plus bas.

`lightningColor` teinte les éclairs.

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `snowColor` | non | couleur hexadécimale | blanc | Teinte des couches et des blocs de neige |
| `waterFogColor` | non | couleur hexadécimale | celle du biome | Couleur du brouillard sous l'eau |
| `lavaFogColor` | non | couleur hexadécimale | `991A00` | Couleur du brouillard dans la lave |
| `starColor` | non | couleur hexadécimale | blanc | Teinte des étoiles |
| `starTwinkle` | non | flottant, 0 à 1 | `0.0` | Jusqu'où les étoiles faiblissent en scintillant. `0` les garde fixes |
| `lightningColor` | non | couleur hexadécimale | `737380` | Teinte des éclairs |

### Skybox, aurore et arc-en-ciel

*dimensions*

Ces clés se placent elles aussi dans le bloc `sky`, et elles aussi ne sont dessinées que côté client.

```json
{
  "sky": {
    "skybox": {
      "up": "mypack:textures/sky/up.png",
      "down": "mypack:textures/sky/down.png",
      "north": "mypack:textures/sky/north.png",
      "east": "mypack:textures/sky/east.png",
      "south": "mypack:textures/sky/south.png",
      "west": "mypack:textures/sky/west.png"
    },
    "aurora": {
      "color": "#40FF90",
      "topColor": "#8040FF"
    },
    "rainbow": true
  }
}
```

`skybox` peint vos propres images sur le ciel, derrière la lueur de l'aube, le soleil, la lune et les étoiles. Donnez les six faces d'un cube sous forme de chemins de texture complets, disposées comme le cube déplié : `up` touche le bord supérieur de `north`, `down` son bord inférieur, `west` se trouve à sa gauche et `east` à sa droite, avec `south` après `east`. Ou donnez seulement `panorama`, une image 2:1 enroulée autour de tout le ciel : son bord gauche regarde le nord et elle tourne dans le sens des aiguilles d'une montre par l'est, le sud et l'ouest ; sa ligne du haut est à la verticale au-dessus et celle du bas à la verticale en dessous. Une skybox à laquelle il manque une face et qui n'a pas de panorama est ignorée, avec une erreur dans le journal.

`aurora` suspend la nuit des rideaux lumineux bas sur le ciel du nord. Ils ondulent lentement, apparaissent quand le soleil se couche et disparaissent le jour et sous la pluie. `color` est la couleur à leur pied, `topColor` celle vers laquelle ils s'estompent en haut.

`rainbow` affiche un arc-en-ciel à l'opposé du soleil dès que la pluie s'arrête en journée. Il s'estompe dans les deux minutes qui suivent la fin de la pluie, et une nouvelle averse l'efface.

Aucune clé vanilla ne donne à une dimension ses propres images de ciel, une aurore ou un arc-en-ciel, ces clés fonctionnent donc de la même façon sur toutes les versions.

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `skybox.<face>` | non | chemin de texture | aucun | Une face du cube : `up`, `down`, `north`, `east`, `south` ou `west`. Les six sont nécessaires |
| `skybox.panorama` | non | chemin de texture | aucun | Une image enroulée autour de tout le ciel, à la place des faces |
| `aurora.color` | non | couleur hexadécimale | `40FF90` | Couleur au pied des rideaux |
| `aurora.topColor` | non | couleur hexadécimale | `8040FF` | Couleur en haut, là où les rideaux s'estompent |
| `rainbow` | non | booléen | `false` | Afficher un arc-en-ciel après la pluie |

### Le bloc `physics`

*dimensions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `gravity` | non | flottant, supérieur à 0 | `1.0` | Accélération de chute ici, en multiplicateur de celle de vanilla. `0.17` est proche de la Lune |
| `fallDamage` | non | flottant, supérieur à 0 | `1.0` | Dégâts de chute ici, en multiplicateur |
| `arrowGravity` | non | flottant, supérieur à 0 | suit `gravity` | À quelle vitesse les flèches chutent ici, en multiplicateur |

Ce sont les mêmes multiplicateurs que les clés de modèle de monde `worldGravity` et `worldFallDamage`, définis sur la dimension. La ligne `dimension=value` d'un modèle de monde pour cette dimension l'emporte toujours ; une valeur de modèle de monde sans nom ne couvre que les dimensions qui ne définissent rien par elles-mêmes.

### Le bloc `time`

*dimensions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `dayLength` | non | entier, ticks | `24000` | Durée d'un jour et d'une nuit ici. Les phases de la lune continuent de tourner une fois tous les 24000 ticks |

### Le bloc `weather`

*dimensions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `precipitation` | non | booléen | `true` | Désactivé, il n'y pleut, ne neige et n'orage jamais ici |
| `lightning` | non | booléen | `true` | Désactivé, la pluie et les orages arrivent sans foudre |
| `snow` | non | booléen | `true` | Désactivé, la neige ne se dépose jamais |
| `freeze` | non | booléen | `true` | Désactivé, l'eau ne gèle jamais |
| `cycle.rainTicks` | non | entier ou `[min, max]` | `[1000, 4600]` | Durée d'une averse |
| `cycle.clearTicks` | non | entier ou `[min, max]` | `[1000, 3000]` | Durée de l'accalmie entre deux averses |
| `cycle.maxStrength` | non | flottant, de plus de 0 à 1 | `0.6` | L'intensité maximale d'une averse. Chaque averse oscille entre le quart de cette valeur et sa totalité |
| `cycle.thunderTicks` | non | entier ou `[min, max]` | aucun | Durée d'un orage. Sans cette clé, le cycle ne produit jamais d'orage |
| `cycle.calmTicks` | non | entier ou `[min, max]` | `[12000, 180000]` | Durée du calme entre deux orages |
| `cycle.thunderStrength` | non | flottant, de plus de 0 à 1 | `1` | Degré d'obscurité d'un orage. La foudre ne tombe qu'au-dessus de `0.9` |
| `rain.particle` | non | id de particule | `minecraft:rain` | Ce qui éclabousse là où la pluie tombe |
| `rain.sound` | non | nom de son | `minecraft:weather.rain` | Le son de la pluie |
| `rain.volume` | non | flottant | `0.2` | Son volume, divisé par deux quand la pluie tombe au-dessus de vous |
| `rain.interval` | non | entier | `3` | La rareté du son : plus la valeur est élevée, plus il est espacé ; `0` le joue à chaque occasion |
| `rain.color` | non | couleur hexadécimale | `#FFFFFF` | Teinte de la pluie qui tombe |
| `rain.snowColor` | non | couleur hexadécimale | `#FFFFFF` | Teinte de la neige qui tombe |
| `rain.angle` | non | flottant, de 0 à 180 | `0` | Degrés par rapport à la verticale descendante : `90` souffle à l'horizontale, `180` monte droit vers le haut. Elle est dessinée inclinée de 75 degrés au maximum |
| `rain.heading` | non | flottant, degrés | `0` | Direction du souffle : `0` sud, `90` ouest, `180` nord, `270` est |
| `wind.gust` | non | flottant, de 0 à 90 | `15` | Degrés qu'une rafale ajoute à `angle` à son plus fort, sans jamais dépasser l'horizontale |
| `wind.every` | non | entier ou `[min, max]` | `[200, 600]` | Ticks d'une rafale à la suivante |
| `wind.swing` | non | flottant, de 0 à 180 | `30` | Degrés dont une rafale fait pivoter `heading` d'un côté |

Un bloc `wind` rend la pluie rafaleuse. De temps à autre, une rafale l'incline jusqu'à `gust` degrés de plus et fait pivoter sa direction jusqu'à `swing` degrés d'un côté ; elle enfle puis retombe en moins de quatre secondes, et les rafales reviennent tous les `every` ticks. La pluie et la neige s'inclinent avec elle, et les particules d'ambiance de la dimension dérivent du côté où penche la pluie, avec ou sans rafales. Un bloc `wind` sans bloc `rain` donne à la pluie ses valeurs par défaut.

Les autres dimensions partagent la pluie de l'overworld. Un `cycle` donne à celle-ci sa propre météo : les averses vont et viennent selon les durées ci-dessus, quoi que fasse l'overworld. Avec `thunderTicks`, elle connaît aussi des orages, selon ses propres durées ; un orage qui rencontre une averse la porte à pleine intensité, assombrit le ciel et, si `lightning` est activé, déclenche la foudre. `weatherCeiling` dans un [modèle de monde](#modèles-de-monde) continue de plafonner la hauteur que la pluie atteint.

Un bloc `rain` change l'aspect et le son de la pluie et de la neige ici, avec ou sans `cycle` ; sans lui, ils ont l'aspect et le son de vanilla.

### Le bloc `ambience`

*dimensions*

| Clé              | Requis | Valeur                 | Défaut           | Rôle                                                                                                                                                                                                                                                |
| ---------------- | ------ | ---------------------- | ---------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `music`          | non    | nom de son             | aucun            | Musique jouée ici à la place des morceaux habituels, en créatif aussi. En arrivant, le morceau en cours est coupé                                                                                                                                   |
| `musicDelay`     | non    | entier ou `[min, max]` | `[12000, 24000]` | Ticks de silence entre deux morceaux                                                                                                                                                                                                                |
| `loopSound`      | non    | nom de son             | aucun            | Un son qui tourne en boucle tant que vous êtes ici, en fondu à l'arrivée comme au départ                                                                                                                                                            |
| `ambientSound`   | non    | nom de son             | aucun            | Un son joué de temps à autre, comme le son d'ajout (additions) d'un biome                                                                                                                                                                           |
| `soundChance`    | non    | 0.0 à 1.0              | `0.0111`         | La probabilité à chaque tick que `ambientSound` soit joué                                                                                                                                                                                           |
| `particle`       | non    | id de particule        | aucun            | Une particule qui flotte dans l'air autour de vous, comme `minecraft:ash`, `minecraft:white_ash`, `minecraft:crimson_spore` ou `minecraft:dust`                                                                                                     |
| `particleChance` | non    | 0.0 à 1.0              | `0.00625`        | Sa densité, comptée comme pour les biomes modernes : à chaque tick environ 667 endroits dans un rayon de 16 blocs et 667 autres dans un rayon de 32 sont essayés, et chacun qui n'est pas un bloc plein affiche la particule avec cette probabilité |
| `particleColor`  | non    | couleur hexadécimale   | aucun            | La teinte d'une particule qui en accepte une : `minecraft:dust`, `minecraft:entity_effect` et `minecraft:ambient_entity_effect`                                                                                                                     |

Un bloc `ambience` donne à la dimension sa propre musique, ses propres sons et des particules qui flottent. Les clés alimentent les effets de biome du jeu lui-même (musique, boucle d'ambiance, son d'ajout et particule d'ambiance), si bien qu'ils sonnent et s'affichent comme ceux d'un biome, et le réglage « Particules » les réduit de la même façon. Une clé donnée ici l'emporte sur tous les biomes de la dimension ; une clé absente laisse à chaque biome la sienne, donc un JSON de biome vanilla avec ses effets fonctionne aussi.

## Portails et passages

*le monde*

`<namespace>/blocks/*.json`

Un portail est une définition de bloc ordinaire : la même règle de chemin s'applique, et chaque variante est un bloc de portail.

Un bloc `portal` porte une section `portal` :

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

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `dimension` | oui | id de dimension |  | Où il vous envoie |
| `returnDimension` | non | id de dimension | `minecraft:overworld` | Où il vous renvoie |
| `gate` | non | nom de passage | aucun | Un passage qui doit être ouvert pour traverser |
| `cooldown` | non | entier, ticks | `60` | Délai avant que le même joueur puisse le réutiliser |
| `platform` | non | booléen | `true` | Construit une plateforme d'atterrissage à l'arrivée |
| `platformBlock` | non | nom de bloc | le cadre du portail lui-même | De quoi est faite cette plateforme |
| `sound` | non | nom de son | aucun | Joué au passage. Voir [noms de sons](#listes-de-valeurs) |
| `owned` | non | booléen | `true` | Seul celui qui l'a construit, et ceux qu'il autorise, peuvent l'utiliser. Un portail possédé résiste aussi aux explosions |
| `walkIn` | non | booléen | `false` | Marcher dans le bloc téléporte, comme avec un portail du Nether. Désactivé, il s'utilise à la main |

### Cadres de portail

*portails et passages*

`<namespace>/portalframes/*.json`

Le chemin du fichier est le nom de registre du cadre, qu'une dimension nomme ensuite dans `frames`.

Un cadre est un plan de ce que le joueur doit construire, rien de plus : il indique quels blocs forment le contour et où se trouve le vide, et ne dit rien de la destination du portail. C'est voulu, car une dimension revendique un cadre sans le posséder, et deux dimensions peuvent revendiquer le même.

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

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `name` | non | chaîne | le nom du fichier | Le nom utilisé dans le journal |
| `axis` | non | `vertical`, `horizontal` ou `both` | `vertical` | S'il se dresse comme un portail du Nether, s'il est posé à plat comme un portail de l'End, ou s'il peut être l'un ou l'autre |
| `legend` | oui | objet associant un caractère à un bloc | aucun | Les blocs que les lignes peuvent utiliser. Un nom de bloc avec états se lit comme partout ailleurs |
| `rows` | oui | liste de chaînes | aucun | Le plan, dessiné en commençant par la ligne du haut |
| `maxWidth` | non | entier | `21` | Largeur maximale du vide jusqu'où un `*` peut s'étirer |
| `maxHeight` | non | entier | `21` | Hauteur maximale du vide jusqu'où un `*` peut s'étirer |

Trois caractères ne sont pas des blocs. `.` est le vide dans lequel se tient le portail, et un cadre sans vide est refusé. Une espace est une case dont le cadre n'a cure : un contour en L se dessine donc en laissant les coins vides. `*` répète : une ligne composée uniquement de `*` répète la ligne du dessus autant de fois que le joueur l'a construite, et un `*` à l'intérieur d'une ligne répète de la même façon le caractère qui le précède. Il peut ne répéter aucune fois, de sorte que le plan lu en rayant tous les `*` est la plus petite construction qui s'allume, et les maximums ci-dessus sont la plus grande. Un plan sans aucun `*` est exact, et le joueur doit construire cela et rien d'autre.

Un cadre vertical est reconnu sur l'un ou l'autre des axes horizontaux et dans les deux sens, peu importe donc la direction dans laquelle le bâtisseur regardait. Un cadre horizontal est reconnu dans les quatre rotations.

**Sa taille maximale appartient au pack.** `maxWidth` et `maxHeight` sont le plus grand vide jusqu'où un `*` s'étirera, et tout ce qui est plus petit, jusqu'au plancher, est accepté : un pack décide donc si son portail plafonne à 21 comme vanilla ou à 4. Le plancher est un joueur : un cadre dressé est refusé si son vide ne peut pas faire au moins 1 de large sur 2 de haut, un cadre à plat au moins 1 sur 1, et un plan qui ne peut jamais l'atteindre est refusé au chargement par une ligne dans le journal, plutôt que de devenir un cadre que personne ne peut franchir.

**Un cadre coûte d'autant plus cher à rechercher qu'il peut s'étirer.** Avec à la fois un `*` de ligne et un `*` de colonne, toutes les combinaisons jusqu'aux deux maximums sont essayées : un cadre qui s'étire dans les deux sens jusqu'à 21 représente 441 plans. La recherche abandonne plutôt que de se figer, et le signale dans le journal, ce qui est le signe qu'il faut baisser un maximum ou supprimer l'un des étirements.

**Rien n'empêche un cadre d'être en obsidienne allumée au briquet, mais il est prioritaire.** Un cadre est recherché avant que l'objet n'agisse de lui-même : un tel cadre ouvre donc la dimension du pack là où un portail du Nether se serait dressé. Choisissez un autre bloc ou un autre allumeur pour laisser le portail de vanilla tranquille.

### Ouvrir une dimension avec un cadre

*portails et passages*

`<namespace>/dimensions/*.json`

Une dimension s'ouvre par un cadre en portant une section `portal`. Le cadre et ce qui l'allume, ensemble, déterminent la dimension : une même forme de cadre peut donc mener à plusieurs endroits selon ce avec quoi on l'a allumée.

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

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `frames` | oui | liste de noms de cadres | aucun | Les cadres qui ouvrent cette dimension |
| `ignitedBy` | non | nom d'objet | `minecraft:flint_and_steel` | Ce qu'un joueur tient pour en allumer un |
| `color` | non | couleur hexadécimale | blanc | La couleur dans laquelle le portail est dessiné |
| `return` | non | `built`, `player` ou `none` | `built` | Si un chemin de retour est fourni, construit par le joueur, ou pas du tout |
| `gate` | non | nom de passage | aucun | Un passage qui doit être ouvert pour traverser |
| `cooldown` | non | entier, ticks | `60` | Délai avant que le même joueur puisse traverser à nouveau |
| `platform` | non | booléen | `true` | Construit une plateforme d'atterrissage à l'arrivée |
| `platformBlock` | non | nom de bloc | pierre | De quoi est faite cette plateforme |
| `sound` | non | nom de son | aucun | Joué au passage. Voir [noms de sons](#listes-de-valeurs) |
| `owned` | non | booléen | `false` | Seul celui qui l'a allumé, et ceux qu'il autorise, peuvent l'utiliser |

Le bloc qui occupe le vide n'est pas écrit par le pack. Une dimension dotée d'une section `portal` en reçoit un qui lui est propre, dessiné avec la texture de portail du jeu sous `color`, que l'on traverse en marchant plutôt qu'en l'utilisant à la main, et incassable. La couleur se multiplie à la texture, comme le fait un `tintindex` : `#C77DFF` conserve le violet du Nether et `#4CFFB0` le rend toxique. Pour un portail dont la texture n'est pas du tout celle de vanilla, écrivez votre propre bloc `portal` ordinaire avec sa texture, dessinée si vous le souhaitez comme une [carte de pixels](#textures-écrites-sous-forme-de-cartes-de-pixels), où `tint` peut faire un dégradé entre deux couleurs.

`return` décide de ce qui se passe de l'autre côté. `built` dresse le même cadre, à la taille construite par le joueur, et l'allume, ce qui est le comportement de vanilla. `player` ne construit rien mais permet d'allumer là-bas le même cadre : le chemin du retour doit donc être trouvé et fabriqué. `none` refuse purement et simplement d'allumer le cadre dans cette dimension, et le voyage est à sens unique.

**Un cadre, plusieurs dimensions.** Le couple formé par un cadre et l'objet qui l'allume détermine la dimension : le même `standing_gate` allumé au briquet et allumé avec l'allumeur propre au pack ouvre donc deux endroits différents, chacun avec sa couleur. Deux dimensions revendiquant le même cadre *et* le même objet constituent une erreur du pack : la seconde est refusée et le journal le dit, plutôt que de laisser l'une des deux l'emporter en silence.

Casser n'importe quel bloc du cadre éteint le portail, comme dans vanilla.

### Portails

*portails et passages*

`<namespace>/gates/*.json`

Le chemin du fichier est le nom de registre du passage, qu'un portail nomme ensuite dans `gate`.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin.

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

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `dimension` | oui | id de dimension |  | La dimension qu'il garde |
| `name` | non | chaîne | le nom du fichier | Affiché au joueur |
| `scope` | non | `player`, `global` | `player` | Un joueur à la fois, ou le monde entier d'un coup |
| `open` | non | booléen | `false` | S'il démarre ouvert |
| `unlock` | non | objet |  | Ce qui l'ouvre. Voir ci-dessous |
| `unlockedMessage` | non | chaîne | `%dim% is now open` | Affiché quand il s'ouvre |
| `blockedMessage` | non | chaîne | `You need %item% to enter %dim%` | Affiché quand il refuse |
| `safeReturn` | non | booléen | `false` | Un joueur refoulé est déposé en lieu sûr dans le monde qu'il tentait de quitter : près de son lit ou de son ancre de réapparition chargée s'ils s'y trouvent encore, sinon au point d'apparition de ce monde |
| `requires` | non | liste d'ids de mods ou de namespaces de packs | aucun | Le passage est ignoré à moins que tous soient présents |
| `portalBlocks` | non | liste de noms de blocs | tous les portails | Limite le passage à ces blocs de portail, de sorte qu'une dimension peut avoir une porte gardée et une porte ouverte |

`unlock` accepte `hold` (un objet qui doit être tenu en main), `consume` avec `consumeCount` (`1`), `craft` (un objet qui doit avoir été fabriqué), `advancement`, et `killed` (un nom d'entité : le passage s'ouvre pour quiconque en tue une, de sorte qu'un boss peut détenir la clé d'un monde) avec `killedCount` (`1`) quand une seule ne suffit pas, comptabilisé par joueur ou pour le monde entier selon la portée. Ajouter `killedDrops` (un nom d'objet) fait que les morts comptées déposent cet objet aux pieds du tueur au lieu d'ouvrir le passage, et remet le compte à zéro : une clé peut ainsi être regagnée et remise à quelqu'un qui ne s'est jamais battu pour elle ; conditionnez le passage à `hold` ou `consume` du même objet pour en faire la clé. `%item%`, `%mob%` et `%dim%` sont remplis pour vous. Une clé que lâche un mob n'exige rien de spécial ici : donnez le butin au mob et conditionnez le passage à `hold` ou `consume`.

Les passages gardent aussi les dimensions du jeu : un passage dont la `dimension` est `minecraft:the_nether` se dresse devant chaque portail du Nether.

## Le monde profond

*le monde*

L'overworld peut être plus haut ou plus profond que ne le permet le jeu, et l'espace qui s'ouvre sous le terrain est rempli par une génération qui lui est propre. Quatre clés `terrain` s'en chargent, dans le bloc `settings` d'un modèle de monde comme les autres ; une dimension de pack fait de même avec `minHeight` et `maxHeight` dans son propre `terrain`.

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

| Clé | Valeur | Défaut | Rôle |
| --- | --- | --- | --- |
| `worldMinHeight` | entier, multiple de 16, jusqu'à -2032 | `-64` | Le bloc le plus bas de l'overworld. Le plancher du jeu est -64 ; plus bas, on obtient un monde profond sous le terrain vanilla, en pierre pleine jusqu'à ce que la couche de worldgen la creuse ou que `noiseCaves` prolonge les grottes du jeu vers le bas. Appliqué uniquement via le préréglage généré : un monde créé avant le pack garde sa hauteur |
| `worldMaxHeight` | entier, multiple de 16, jusqu'à 2032 et au plus 4064 au-dessus du plancher | `320` | Le bloc situé au-dessus du sommet de l'overworld. Le sommet du jeu est 320 ; plus haut, on obtient un ciel dégagé au-dessus du terrain vanilla |
| `deepStone` | nom de bloc | aucun | Le bloc dont est fait le monde sous le terrain vanilla quand le plancher descend sous -64, par exemple le deepslate propre à un pack. Il se fond dans le deepslate sur les huit couches sous -64, comme le deepslate se fond dans la pierre. Vide, il garde la pierre |
| `noiseCaves` | `off`, `deep` ou `world` | `off` | Où les grottes, tunnels, nouilles et aquifères du jeu se poursuivent quand le plancher descend sous -64 : `off` garde le monde sous le terrain vanilla en pierre profonde pleine pour que la couche de worldgen la creuse, `deep` les prolonge jusqu'au plancher avec les lacs de lave déplacés dans ses dix couches du bas, `world` signifie la même chose sur cette version car le terrain vanilla en comporte déjà |

Le monde profond est l'endroit où les entrées de worldgen, régions de grottes et groupes de dureté d'un pack font leur travail : `minHeight` et `maxHeight` sur une entrée descendent aussi bas que le plancher. Les éléments placés du jeu restent cantonnés au terrain vanilla : celui dont la hauteur se compte depuis le bas du monde, dont les diamants du jeu et la redstone basse, compte toujours depuis -64, et un placement qui tomberait sous -64 est écarté, comme dans un monde dont le plancher est -64. Les clés de ciel de 1.12.2, `deepRavines`, `oreVeins`, `terrainOffset` et le monde rubic lui-même n'ont pas d'équivalent ici, puisque la génération de ce moteur couvre déjà tout, du plancher au plafond.

## Régions de grottes

*le monde*

`<namespace>/caveregions/*.json`

Le chemin du fichier est le nom de la région, qu'une entrée de worldgen nomme ensuite dans `caveRegions`. Un nom simple y prend le namespace de cette entrée.

Peint des régions nommées sur le sous-sol, l'équivalent côté pack des biomes de grottes du jeu. Le sous-sol est divisé en cellules arrondies de `caveRegionCells` blocs de large et `caveRegionCellsY` de haut, deux clés `terrain`, et chaque cellule tire au sort une région, ou aucune, selon le poids. Tout ce que fait une région découle de façon déterministe de la graine : les chunks s'accordent donc entre eux sans jamais écrire au-delà d'une frontière.

### Fichiers de région

*régions de grottes*

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin.

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

| Clé | Valeur | Défaut | Rôle |
| --- | --- | --- | --- |
| `weight` | entier | `1` | Part des cellules que cette région remporte. `0` la désactive |
| `minHeight` | entier | le plancher du monde | Bas de la bande dans laquelle la région existe |
| `maxHeight` | entier | `48` | Haut de cette bande. Une cellule dont le centre est hors de la bande ne choisit jamais la région |
| `waterLevel` | entier | aucun | Fixe la nappe d'eau à l'intérieur de la région à cette hauteur, à la place des aquifères. Elle reste sous le niveau de la mer et au moins deux blocs au-dessus de la lave profonde |
| `dimensions` | liste d'ids de dimensions | toutes | Les dimensions où la région apparaît, y compris celles d'un pack. Une région dotée d'un `biome` ne l'affiche que là où les biomes sont placés selon le climat : l'overworld, le Nether et une dimension de pack qui hérite des biomes de l'overworld |
| `floorCover` | bloc | aucun | Remplace le bloc supérieur des sols de grotte à l'intérieur de la région |
| `floorChance` | 0.0 à 1.0 | `1.0` | La part du sol qui est recouverte |
| `ceilingCover` | bloc | aucun | Remplace les blocs de plafond de grotte à l'intérieur de la région |
| `ceilingChance` | 0.0 à 1.0 | `1.0` | La part du plafond |
| `coverReplace` | liste de blocs | les blocs de base du jeu : pierre, minerais, pierre taillée, grès, terre cuite, pierre de l'End et obsidienne | Ce que les revêtements peuvent remplacer. Un bloc listé ne correspond qu'à lui-même : `minecraft:stone` ne couvre pas aussi l'andésite, le deepslate ou le tuf, listez donc chaque pierre que le sol peut être |
| `spawns` | liste | aucun | Les mobs qui apparaissent dans la région, les mêmes entrées qu'accepte `spawns` d'un biome : `entity`, `type` (monster, creature, ambient ou water), `weight` (`8`), `min` (`1`) et `max` (`4`) pour la taille du groupe. Un endroit qui voit le ciel est laissé au biome, comme le sont les revêtements |
| `keepDefaultSpawns` | booléen | `false` | Conserve la liste d'apparitions propre au biome en plus de celle de la région. Désactivé, la liste de la région la remplace entièrement à l'intérieur de la région |
| `structures` | liste | aucun | Une structure placée une fois par cellule de région, au cœur de la cellule, calée sur un sol de grotte, comme le jeu donne son repère à un biome de grotte. Les entrées sont des modèles `namespace:name`, ou `{ "structure": "...", "weight": 3 }` pour choisir entre plusieurs |
| `structureChance` | 0.0 à 1.0 | `1.0` | La probabilité que chaque cellule de la région reçoive effectivement sa structure |
| `structureLoot` | `namespace:path` | aucun | La table de butin dont est rempli chaque coffre d'une structure placée, la première fois qu'il est ouvert |
| `biome` | nom de biome | aucun | Le biome que la région annonce dans son volume, écrit comme un biome 3D. Donne à la région son propre feuillage, ses propres couleurs d'herbe et d'eau, sa musique et ses sons d'ambiance, et permet à vanilla de lire la pondération des apparitions. La surface au-dessus reste intacte, puisque seules les cellules occupées par la région sont écrites. Omis, la région garde le biome qui l'entoure et pose tout de même ses revêtements, structures et apparitions |
| `requires` | liste d'ids de mods ou de namespaces de packs | aucun | La région est ignorée à moins que tous soient présents |
| `ambientSound` | nom de son | aucun | Un son joué de temps à autre à un joueur qui se tient dans la région, comme les biomes du jeu ajoutent leurs propres sons de grotte. Envoyé par le serveur à ce joueur seul |
| `soundChance` | 0.0 à 1.0 | `0.0111` | La probabilité à chaque tick que `ambientSound` soit joué |
| `particle` | nom de particule | aucun | Une particule affichée autour d'un joueur dans la région, l'une des particules du jeu qui n'ont aucun réglage propre, comme `minecraft:dripping_water`, `minecraft:happy_villager` ou `minecraft:underwater`. Un nom de 1.12.2 comme `dripWater` est converti avec son pack. Seul l'air à l'intérieur de la région l'affiche |
| `particleChance` | 0.0 à 1.0 | `0.00625` | La densité de particules propre aux biomes : à chaque tick, environ 667 endroits dans un rayon de 16 blocs sont essayés, et chacun affiche la particule avec cette probabilité |

### Cellules

*régions de grottes*

| Réglage | Type | Défaut | Rôle |
| --- | --- | --- | --- |
| `caveRegionCells` | entier, blocs | `128` | Largeur d'une cellule de région |
| `caveRegionCellsY` | entier, blocs | `64` | Hauteur d'une cellule de région |
| `caveRegionPlainWeight` | entier | `4` | Le poids du sous-sol ordinaire, sans région, dans le tirage de chaque cellule. Plus il est élevé, plus il reste de sous-sol sans région : avec une seule région de poids 1, environ une cellule sur cinq la reçoit |

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

La part du sous-sol qui reste ordinaire est la clé `terrain` `caveRegionPlainWeight`, `4` par défaut : avec une seule région de poids 1, environ une cellule sur cinq reçoit la région. Les revêtements s'appliquent sous un toit : une région qui monte au-dessus du sol n'apparaît donc jamais en surface. Les revêtements fonctionnent dans toutes les grottes, quel que soit le générateur qui les a creusées.

### Éléments d'une région

*régions de grottes*

Les éléments se rattachent par deux clés sur des [entrées de worldgen](#entrées-de-worldgen) ordinaires. `caveRegions` liste les régions dans lesquelles une entrée peut se générer, vérifiées à la position placée : champignons, cristaux ou tout autre élément n'apparaissent ainsi que dans leur région. `snap` déplace d'abord chaque tentative à la verticale jusqu'à la surface de grotte la plus proche : `floor` pour ce qui se dresse, `ceiling` pour ce qui pend. Une région de type concrétions n'a besoin d'aucune nouvelle forme :

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

Le `replace` de `minecraft:air` compte : ce sur quoi une forme placée écrit est vérifié par rapport à `replace`, dont le défaut est la pierre, de sorte que tout ce qui est construit dans l'espace ouvert d'une grotte exige que l'air soit listé. La même entrée avec `"snap": "floor"` et sans `hanging` fait pousser les stalagmites correspondantes. Le filtre de région fonctionne avec toutes les formes placées ; `belt` et `field` se placent selon leurs propres règles et l'ignorent.

---

# Générer le monde

## Entrées de worldgen

*générer le monde*

`<namespace>/worldgen/*.json`

Le chemin du fichier nomme l'entrée, et les formes `belt`, `field` et `vein` en tirent leur bruit : renommer un fichier déplace donc ce qu'il génère.

Décrit quelque chose qui se génère. Chaque entrée est une **forme** placée par une **propagation**, filtrée selon l'endroit où elle est autorisée.

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

Seul `block` est requis ; tout le reste peut être omis et prend sa valeur par défaut. `blocks` remplace `block` quand un seul ne suffit pas, et a son propre exemple plus bas.

### Ce qu'il place

*entrées de worldgen*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `block` | oui | nom de bloc |  | Ce qui est placé |
| `blocks` | non | liste d'objets | aucun | Une liste pondérée, utilisée à la place d'un seul bloc. Voir ci-dessous |
| `size` | non | entier ou intervalle | `8` | Combien de blocs une tentative place, ou la taille d'une forme dotée d'un rayon |
| `attempts` | non | entier ou intervalle | `8` | Combien de fois par chunk elle essaie |
| `replace` | non | liste de noms de blocs ou d'objets | `["minecraft:stone"]` | Ce qu'elle peut remplacer. Voir ci-dessous |
| `adjacent` | non | liste de noms de blocs ou d'objets | aucun | Ne place que là où l'un de ces blocs figure parmi les 26 blocs qui touchent l'endroit. Mêmes formes que `replace` |
| `sparse` | non | booléen | `false` | Disperse les blocs au lieu de les tasser |
| `shape` | non | objet | `{ "type": "cluster" }` | La forme qu'elle prend. Voir [Formes](#formes) |
| `spread` | non | objet | `{ "type": "even" }` | Où elle est placée. Voir [Propagations](#propagations) |

### Où il peut se générer

*entrées de worldgen*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `minHeight` | non | entier | `0` | Le y le plus bas où elle placera |
| `maxHeight` | non | entier | `64` | Le y le plus haut où elle placera |
| `dimensions` | non | liste d'ids de dimensions | toutes les dimensions | Les dimensions où elle s'exécute |
| `dimensionsAreBlacklist` | non | booléen | `false` | Transforme cette liste en liste de celles à éviter |
| `biomes` | non | liste de noms de biomes | tous les biomes | Les biomes où elle s'exécute |
| `biomeTypes` | non | liste de types de biomes | aucun | Les biomes par mot de type, comme `forest` ou `nether` |
| `biomesAreBlacklist` | non | booléen | `false` | Transforme ces listes en listes de ceux à éviter |
| `minTemperature` | non | flottant | `-100.0` | Le biome le plus froid où elle se générera |
| `maxTemperature` | non | flottant | `100.0` | Le biome le plus chaud où elle se générera |
| `minRainfall` | non | flottant | `-100.0` | Le biome le plus sec où elle se générera |
| `maxRainfall` | non | flottant | `100.0` | Le biome le plus humide où elle se générera |
| `minDistanceFromSpawn` | non | entier, blocs | `0` | Distance au point d'apparition du monde à partir de laquelle elle commence |
| `caveRegions` | non | liste de noms de régions | aucun | Ne génère qu'à l'intérieur de ces [régions de grottes](#régions-de-grottes) |
| `snap` | non | `floor` ou `ceiling` | aucun | Déplace d'abord chaque tentative à la verticale jusqu'au sol ou au plafond de grotte le plus proche |
| `snapDepth` | non | entier | `0` | Jusqu'où `snap` s'enfonce ensuite au-delà de la surface, vers le bas depuis un sol et vers le haut depuis un plafond. `0` reste dans l'espace ouvert contre la surface, `1` est le bloc de surface lui-même, `2` celui qui est derrière. Ce qu'elle peut écraser reste régi par `replace` : c'est ainsi qu'un pack fait une bande d'un bloc juste sous le sol plutôt que dessus |

### Panneaux de surface et suiveurs

*entrées de worldgen*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `indicators` | non | liste de `block=weight` | aucun | Des blocs laissés épars en surface au-dessus d'un filon généré, pour qu'un joueur devine ce qui se trouve sous terre ; choisissez-les en fonction du contenu du filon. `empty=weight` laisse un endroit nu. Une entrée sans poids, ou dont le poids est inférieur à 1, est consignée dans le journal et écartée, et aucun n'est laissé sur les rues et les bâtiments d'un village ou d'une ville |
| `indicatorCount` | non | entier ou intervalle | `1` | Combien d'endroits de surface reçoit chaque filon généré |
| `indicatorSpread` | non | entier, blocs | `0` | Jusqu'où au-delà de l'emprise du filon un indicateur peut se poser |
| `then` | non | liste de `name=weight` ou d'objets | aucun | Des entrées de worldgen qui poussent à partir de celle-ci juste après sa génération, rattachées à elle : l'origine du suiveur est placée juste à l'extérieur du bord de ce filon, dans la direction que donnent `thenSpread` et `thenDepth`, de sorte que les deux se touchent. Une entrée est `name=weight`, ou un objet avec `name`, `weight` et ses propres `spread` et `depth` (entier ou intervalle) qui remplacent ceux du filon pour ce seul suiveur : une même liste peut ainsi envoyer une pointe de diamant vers le bas et une branche sur le côté. Un nom simple est lu dans le namespace de ce pack, `empty=weight` ne met rien en file. Un suiveur garde sa propre forme, ses blocs, sa taille et son `replace` mais ignore ses propres tentatives, sa probabilité, sa bande d'altitude et ses filtres de biome, et peut lui-même porter `then`, aussi profondément que le pack le veut ; une entrée déjà générée dans la même chaîne l'arrête |
| `thenCount` | non | entier ou intervalle | `1` | Combien de suiveurs différents sont choisis dans cette liste par filon généré, chaque entrée au plus une fois : un nombre égal à la longueur de la liste fait donc pousser chacun d'eux |
| `thenSpread` | non | entier, blocs | le rayon de la forme | Jusqu'où, sur le côté, la direction dans laquelle pousse un suiveur peut dévier, tirée de moins cette valeur à plus cette valeur |
| `thenDepth` | non | entier ou intervalle | `0` | Jusqu'où la direction penche vers le bas (négatif) ou vers le haut. `0` sans déviation latérale fait pendre le suiveur droit vers le bas |
| `prospectAs` | non | chaîne | le nom du fichier | Comment un objet de prospection nomme cette entrée dans son relevé, par ex. `Hematite` |

### Retrogen et prérequis

*entrées de worldgen*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `retrogen` | non | booléen | `false` | Génère aussi dans les chunks qui existent déjà |
| `retrogenKey` | non | chaîne | la clé de la configuration | Remplace la clé de retrogen pour cette seule entrée |
| `requires` | non | liste d'ids de mods ou de namespaces de packs | aucun | L'entrée est ignorée à moins que tous soient présents |

### Blocs pondérés

*entrées de worldgen*

`blocks` remplace `block` quand une seule entrée ne suffit pas. Les poids sont relatifs : 80 et 20 font donc quatre contre un.

```json
{
  "blocks": [
    { "block": "minecraft:magenta_wool", "weight": 80 },
    { "block": "minecraft:oak_log", "weight": 20, "properties": { "axis": "x" } }
  ]
}
```

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `block` | oui | nom de bloc |  | Ce qui est placé |
| `weight` | non | entier | `1` | La fréquence à laquelle celui-ci est choisi par rapport aux autres |
| `properties` | non | objet associant propriété et valeur | aucun | Propriétés d'état de bloc par nom, pour un état autre que celui par défaut du bloc |

`block` reste requis au niveau supérieur du fichier même quand `blocks` est utilisé ; la première entrée est une bonne valeur à y mettre.

### Cibles de remplacement

*entrées de worldgen*

`replace` est une liste, et chaque entrée prend l'une de deux formes.

```json
{
  "replace": [
    "minecraft:stone",
    { "block": "minecraft:oak_log", "properties": { "axis": "y" } }
  ]
}
```

| Forme | Exemple | Ce qu'elle reconnaît |
| --- | --- | --- |
| Nom | `"minecraft:stone"` | Tous les états de ce bloc |
| Objet | `{ "block": "minecraft:oak_log", "properties": { "axis": "y" } }` | Uniquement cet état |

Un nom de 1.12.2 avec des métadonnées à la fin, `minecraft:stone:3`, correspond à tous les états du bloc et le signale dans le journal, puisque les blocs qui portaient des métadonnées sont désormais des blocs distincts : écrivez `minecraft:diorite`. Utilisez `"minecraft:air"` pour générer dans l'espace ouvert.

### Blocs adjacents

*entrées de worldgen*

`adjacent` accepte les mêmes formes que `replace` et ajoute une seconde condition par-dessus : l'endroit n'est utilisé que si au moins un des 26 blocs qui le touchent, faces, arêtes et coins, correspond à la liste. Omis, rien n'est vérifié.

```json
{
  "block": "mypack:sulfur_ore",
  "replace": ["minecraft:sandstone"],
  "adjacent": ["minecraft:air"]
}
```

Cela place du soufre dans le grès uniquement là où il est déjà ouvert sur une grotte ou sur la surface, et laisse le grès enfoui tranquille. Les voisins situés dans des chunks qui n'existent pas encore sont considérés comme ne correspondant pas, au lieu d'être lus : la vérification ne provoque donc jamais la génération d'un chunk.

Toutes les formes la respectent, car elle fait partie de la décision de savoir si un bloc isolé peut être pris. Une `geode` nomme séparément sa croûte et son remplissage, et ces deux-là sont placés sans la vérification.

Une entrée qui ne nomme que des blocs non enregistrés est ignorée avec une erreur, au lieu de se générer partout.

### Entrées de suiveurs

*entrées de worldgen*

Une entrée de la liste `then` d'une entrée de worldgen est un nom avec un poids, ou un objet quand ce suiveur a besoin de sa propre direction.

```json
{
  "then": [
    "mypack:quartz_halo=2",
    "empty=1",
    { "name": "mypack:side_branch", "weight": 1, "spread": 4, "depth": 0 }
  ]
}
```

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `name` | oui | nom d'entrée |  | L'entrée de worldgen qui pousse à partir de celle-ci. Un nom simple est lu dans le namespace de ce pack |
| `weight` | non | entier | `1` | La fréquence à laquelle ce suiveur est choisi par rapport aux autres de la liste |
| `spread` | non | entier, blocs | le `thenSpread` de l'entrée | Jusqu'où, sur le côté, la direction de ce suiveur peut dévier, pour cette seule entrée |
| `depth` | non | entier ou intervalle | le `thenDepth` de l'entrée | Jusqu'où, vers le bas (négatif) ou vers le haut, la direction de ce suiveur penche, pour cette seule entrée |

`name=weight` est la forme courte d'un objet qui ne porte que ces deux clés, et `empty=weight` ne met rien en file. Comme `spread` et `depth` sont propres à chaque entrée, une même liste peut envoyer une pointe de diamant droit vers le bas et une branche sur le côté à partir du même filon.

## Formes

*générer le monde*

Un bloc `shape` avec un `type`. Les clés non listées pour un type sont ignorées par celui-ci.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin. Une clé marquée pour un type n'est lue que par ce type.

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

| Type | Ce qu'il crée |
| --- | --- |
| `cluster` | L'amas par défaut, un filon de minerai. Utilise `size` |
| `largevein` | Un long filon sinueux avec des branches. Utilise `size` |
| `plate` | Un disque plat |
| `geode` | Une poche creuse avec une croûte |
| `decoration` | Une dispersion en surface, comme des fleurs ou des champignons. Utilise `size` |
| `tree` | Un arbre entier |
| `vines` | Des lianes sur ce qui s'y trouve déjà. Utilise `size` |
| `basin` | Une cuvette qui s'approfondit vers le milieu |
| `spire` | Une colonne qui s'effile |
| `nodule` | Une boule irrégulière |
| `vent` | Une colonne étroite qui s'arrête quand elle rencontre quelque chose |
| `imprint` | Un de vos modèles `.nbt`. Un modèle qui tient dans un chunk est décalé pour atterrir en entier dans le chunk en cours de construction plutôt que d'empiéter sur un voisin qui n'a pas encore été créé, quelle que soit sa rotation ; un modèle plus grand qu'un chunk n'est placé que là où le terrain qui l'entoure existe déjà |
| `belt` | Un amas couvrant plusieurs chunks, pour les régions de pierre |
| `field` | Des filons calculés pour tous les blocs à la fois, partageant leur forme avec les groupes de dureté |
| `vein` | Un gisement calculé comme un champ de bruit à graine autour d'une origine, à la manière d'Immersive Geology : chaque chunk écrit sa propre tranche de chaque filon dont la portée de 24 blocs le touche, sans aucune cascade, et `/rdplserver vein` peut dire où se trouvera un filon avant que le terrain ne soit créé. Utilise `size`, `attempts`, `rarity` et la bande d'altitude ; `pattern` choisit l'apparence |
| `spring` | Un fluide qui suinte d'une paroi de grotte : placé là où de la roche se trouve au-dessus, en dessous et sur trois côtés avec un côté ouvert, et mis en écoulement |

### Taille et forme

*formes*

| Clé | Utilisée par | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `type` | toutes | l'une des formes ci-dessus | `cluster` | Quelle forme |
| `radius` | plate, geode, basin, spire, nodule, vent | entier ou intervalle | `6` | Sa largeur |
| `height` | plate, geode, basin, spire, vent, tree | entier ou intervalle | `1`, `8` pour geode, `5` pour tree | Sa hauteur ou son épaisseur |
| `width` | geode | entier ou intervalle | `12` | L'envergure totale de la poche |
| `plane` | plate, basin, spire, vent | `circle`, `square` | `circle` | Son empreinte au sol |
| `slim` | plate, largevein, nodule | booléen | `false` | Plate : une couche de moins. Largevein : branches d'un seul bloc. Nodule : coque creuse |
| `hanging` | spire, vent | booléen | `false` | Pousse vers le bas depuis un plafond au lieu de vers le haut depuis un sol |
| `taper` | spire | `straight`, `bell`, `needle` | `straight` | Comment la largeur diminue vers la pointe. `straight` rétrécit régulièrement, `bell` garde sa largeur en bas puis chute, `needle` s'amincit aussitôt en une longue pointe |
| `outline` | geode | nom de bloc | aucun | Le bloc de la croûte |
| `fill` | geode | nom de bloc | aucun | Ce qui remplit le milieu. Omis, le milieu est creux |
| `middle` | geode | nom de bloc | aucun | Une coque entre le corps et `outline`, la calcite de la géode d'améthyste du jeu |
| `budding` | geode | nom de bloc | aucun | Substitué aux blocs du corps qui font face au milieu creux, comme l'améthyste bourgeonnante. Exige `fill` |
| `buddingChance` | geode | 0.0 à 1.0 | `0.083` | La part de ces blocs du corps qui bourgeonnent |
| `crystal` | geode | nom de bloc | aucun | Qui pousse dans le creux à côté d'un bloc `budding`, comme un amas d'améthyste |
| `crystalChance` | geode | 0.0 à 1.0 | `0.35` | La part de ces endroits où il en pousse un |
| `crack` | geode | 0.0 à 1.0 | `0` | La probabilité qu'une géode soit fendue : un tube allant du milieu vers l'extérieur à travers toutes les couches d'un côté, rempli de `fill`. Les géodes d'améthyste du jeu utilisent `0.95` |

### Placement

*formes*

| Clé | Utilisée par | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `surface` | decoration, tree | liste de noms de blocs | aucun | Sur quoi elle se posera |
| `seeSky` | decoration | booléen | `true` | Ne place que là où le ciel est visible |
| `checkStay` | decoration | booléen | `true` | Ne place que là où le bloc survivrait |
| `stackHeight` | decoration | entier ou intervalle | `1` | Combien en empiler les uns sur les autres |
| `scatterX` | decoration, tree | entier | `8` | Jusqu'où elle s'écarte sur le côté |
| `scatterY` | decoration, tree | entier | `4` | Jusqu'où elle s'écarte à la verticale |
| `scatterZ` | decoration, tree | entier | `8` | Jusqu'où elle s'écarte sur le côté |
| `rarity` | toute | entier | aucun (`400` pour belt) | Un placement par ce nombre de chunks. Sur une ceinture, cela espace les ceintures ; sur toute autre forme, cela conditionne l'entrée entière : un seul chunk sur ce nombre tire ses `attempts`. `field` l'ignore |
| `rarityIsPerChunk` | toute | booléen | `false` | Transforme `rarity` en nombre de placements que reçoit chaque chunk |

### Arbres

*formes*

| Clé | Utilisée par | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `log` | tree | nom de bloc | aucun | Le bloc du tronc |
| `leaves` | tree | nom de bloc | aucun | Le bloc de feuillage |
| `vines` | tree | booléen | `false` | Fait pendre des lianes aux feuilles |

Un `tree` sans `log` ni `leaves` ne génère rien, et le signale dans le journal. Nommer un `structure`, ou plusieurs sous `structures`, plante ce modèle à chaque endroit au lieu d'en faire pousser un, et alors ni `log` ni `leaves` ne sont nécessaires ; un arbre à modèle lit `turns`, `mirrors`, `integrity`, `lootTable` et `locateAs` exactement comme le fait un `imprint`.

### Placer des modèles

*formes*

| Clé | Utilisée par | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `structure` | imprint, tree | `namespace:name` | aucun | Le modèle à placer |
| `integrity` | imprint, tree | 1 à 100 | `100` | Pourcentage des blocs du modèle qui apparaissent réellement |
| `lootTable` | imprint, tree | `namespace:path` | aucun | La table de butin dont est rempli chaque coffre du modèle placé la première fois qu'il est ouvert, ainsi que tout autre conteneur qui en accepte une, dont une boîte de shulker ou la caisse d'un mod. Couvre `structure` et chaque entrée de `structures` ; chaque coffre tire sa propre graine |
| `structures` | imprint, tree | liste | aucun | Plusieurs modèles entre lesquels choisir, un seul étant placé à chaque fois. Chaque entrée est `{ "structure": "namespace:name", "weight": 3 }`, ou un nom simple pour des chances égales. Remplace `structure` |
| `turns` | imprint, tree | liste | toutes | Dans quel sens il peut être placé : `none`, `quarter`, `half`, `threequarter`. Les entrées peuvent porter un `weight`. Omis, les quatre sont équiprobables |
| `mirrors` | imprint, tree | liste | aucun | Le retourne aussi : `none`, `leftright`, `frontback`, avec `weight` en option. Une entrée qui nomme son propre poids s'écrit `{ "mirror": "leftright", "weight": 2 }`, et une entrée de `turns` de même avec `turn` |
| `at` | imprint | deux entiers, x et z | aucun | Place exactement une fois à ces coordonnées de bloc en surface, quand ce chunk se génère, au lieu de le faire au hasard. Voir [Structures à des endroits précis](#structures-à-des-endroits-précis) |
| `locateAs` | imprint, tree | chaîne | aucun | Enregistre chaque structure que place cette entrée sous ce nom, de sorte que `/rdplserver locate <name>` trouve la plus proche. Voir [Retrouver les structures placées](#retrouver-les-structures-placées) |

Pour une forme qu'aucun type intégré ne couvre, `imprint` est la solution : construisez-la comme un modèle `.nbt` et placez-le, avec `structures` pour le faire varier, `turns` et `mirrors` pour le tourner, et `integrity` pour le dissoudre en quelque chose de plus rugueux que le fichier que vous avez dessiné.

### Structures à des endroits précis

*formes*

Les structures vanilla se fixent à des endroits exacts avec `structureAt` dans les réglages `terrain`, sous forme d'entrées `structure=x,z`, une par ligne : `"structureAt": ["villages=1000,-500"]`. **Les x et z sont des coordonnées de bloc, pas de chunk**, et la structure se génère dans le chunk qui contient ce bloc. Avec `terrainAdaptation` qui pose les villages comme des rues de ville, le puits d'un village fixé se dresse sur ce bloc même, ou aussi près que son quartier le permet quand le bloc se trouve à quelques blocs du bord du quartier ; les autres structures, et les villages posés sans lui, commencent là où le jeu les ferait commencer dans ce chunk. Une entrée par instance voulue. Son espacement, sa séparation, sa distance minimale d'apparition et ses vérifications de terrain plat s'effacent tous : l'endroit est donc sous la responsabilité du pack, et deux points fixés à moins d'un chunk l'un de l'autre mettent deux structures dans le même chunk. La structure se cale sur le sol de son chunk selon les règles habituelles une fois fondée.

| Réglage | Type | Défaut | Rôle |
| --- | --- | --- | --- |
| `structureAt` | liste de `structure=x,z` | aucun | Fixe une structure vanilla à un endroit exact, une entrée par instance voulue. Les x et z sont des coordonnées de bloc, et la structure se génère dans le chunk qui contient ce bloc ; son espacement, sa séparation, sa distance minimale d'apparition et ses vérifications de terrain plat s'effacent tous |

Une entrée `imprint` se fixe de la même façon avec `"at": [x, z]` dans sa forme, en plaçant exactement une fois à ces coordonnées en surface quand ce chunk se génère, au lieu de le faire au hasard. Elle se combine avec `locateAs`, de sorte qu'une structure fixée peut aussi être retrouvée.

### Retrouver les structures placées

*formes*

Une entrée `imprint` avec `"locateAs": "Crypt"` enregistre chaque structure qu'elle place sous ce nom, et `/rdplserver locate Crypt` désigne alors la plus proche, le nom étant proposé par la complétion avec Tab ; `/rdplserver goto Crypt` vous y emmène. Seules les structures déjà générées peuvent être trouvées, puisque les structures de pack sont placées au hasard à mesure que les chunks sont créés, et non sur une grille que le jeu pourrait prédire. Les noms vivent dans la sauvegarde du monde : ils survivent aux redémarrages et fonctionnent sur les serveurs. Un nom enregistré ainsi peut aussi recevoir sa propre permission avec `gotoPlaceLevels`, de sorte qu'un pack décide séparément de qui peut être emmené vers ses propres structures et vers celles de vanilla.

### Clés de champs et de filons

*formes*

| Clé | Utilisée par | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `field` | field | objet | `{ "type": "speckle" }` | Comment le champ est calculé. Mêmes clés que le `field` d'un groupe de dureté, décrites sous [Le champ](#le-champ) : `speckle` avec `chances` et `spread`, ou `seeded` avec `cell`, `seeds`, `reach`, `arms` et `armReach` |
| `threshold` | field, vein | 0.0 à 1.0 | `0.5` (`0.4` pour vein) | L'intensité que le champ doit avoir en un bloc pour qu'il soit placé. Plus bas remplit davantage |
| `fade` | field | entier | `0` | Estompe le haut de la bande au lieu de la terminer net : sur ce nombre de blocs du haut de la plage d'altitude, la probabilité de placement de chaque bloc diminue graduellement, avec l'aspect que le moteur donne à `deepStone` là où il rejoint le monde au-dessus |
| `pattern` | vein | `default`, `banded` ou `tube` | `default` | L'apparence du gisement : un amas déformé, des couches empilées tous les quelques blocs, ou des tubes creux qui serpentent dans la roche |
| `density` | vein | 0.0 à 1.0 | `1.0` | La part des blocs qualifiés qui sont réellement placés, une pièce lancée par bloc |
| `rich` | vein | nom de bloc | aucun | Placé de `richAt` vers le haut dans la plage du champ au-dessus de `threshold`, le cœur du gisement, à la place des blocs de l'entrée |
| `poor` | vein | nom de bloc | aucun | Placé dans les deux cinquièmes du bas de cette plage, la frange, à la place des blocs de l'entrée ; le milieu est constitué des blocs propres à l'entrée. Si l'un des deux niveaux est omis, les blocs de l'entrée y sont placés |
| `richAt` | vein | 0.0 à 1.0 | `0.88` | Où le niveau riche commence dans cette plage : `0.88` limite le bloc riche au huitième le plus fort du gisement, un nombre plus bas épaissit le cœur riche, `1.0` ne laisse aucun bloc riche |
| `poorAt` | vein | 0.0 à 1.0 | `0.4` | Où commencent les blocs propres à l'entrée : en dessous, le bloc `poor` est placé, de sorte que `0.4` donne une frange des deux cinquièmes du bas et `0.0` ne laisse aucune frange pauvre. Borné par `richAt` |

Un filon `field` est la seule forme que l'on décrit plutôt que de la choisir. Il exécute le même treillis que les groupes de dureté : `seeded` avec quelques bras donne donc des nœuds aux ramifications qui s'étendent vers leurs voisins, ce qui est un filon plutôt qu'un amas, et `threshold` décide de la part qui est assez solide pour être placée :

```json
{
  "shape": {
    "type": "field",
    "threshold": 0.4,
    "field": { "type": "seeded", "cell": 10, "reach": 6.0, "arms": 3, "armReach": 5.0 }
  }
}
```

Les clés vont dans un objet `field` à part, pas à côté de `type`, puisque `type` sur la forme dit déjà `field`.

### Ceintures

*formes*

Une `belt` est une boule bien plus grande qu'un chunk, utilisée pour les régions de pierre plutôt que pour les filons de minerai. Son `radius` est la taille de la boule, et chaque chunk calcule lui-même où commencent les boules proches de lui, à partir de la graine du monde et du nom de l'entrée : une ceinture sort donc entière quelle que soit la façon dont les chunks sont générés, et rien n'est jamais écrit dans un chunk voisin.

```json
{
  "shape": { "type": "belt", "radius": 32, "rarity": 400 }
}
```

Une ceinture ignore `attempts` et `spread`, puisqu'elle est placée par chunk plutôt que par tentative. `minHeight` et `maxHeight` sont la bande dans laquelle se situent les centres, et la boule s'étend de `radius` au-delà de cette bande. `replace` décide de ce qu'elle dévore, `biomes` et les limites de température et de précipitations sont vérifiés au centre : une ceinture apparaît donc en entier ou pas du tout, au lieu d'être coupée à la limite d'un biome.

Le coût croît avec le cube de `radius`, et une `rarity` basse le multiplie : partez des valeurs par défaut et augmentez le rayon lentement.

### Champs

*formes*

Un `field` ne place rien en un point et tout en même temps. Au lieu de choisir un emplacement et de bâtir une forme autour, il pose une question à chaque bloc du chunk, entre `minHeight` et `maxHeight`, et place là où la réponse atteint au moins `threshold`. C'est la même question que celle des groupes de dureté, de sorte que les deux décrivent les mêmes filons et qu'un pack peut faire un groupe et une entrée qui s'accordent.

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

| Clé         | Obligatoire | Valeur      | Défaut | Ce qu'elle fait                                                                    |
| ----------- | ----------- | ----------- | ------ | ---------------------------------------------------------------------------------- |
| `threshold` | non         | 0.0 à 1.0   | `0.5`  | Intensité que le champ doit atteindre pour qu'un bloc soit placé                   |
| `field`     | oui         | objet       | aucun  | Le même objet qu'un groupe de dureté, avec les mêmes types `speckle` et `seeded`   |

Un `threshold` bas retient la majeure partie du champ et donne de larges veines ; un `threshold` haut ne retient que le cœur de chaque amas et donne de petites poches éparses. Avec `speckle`, vous obtenez de nombreuses petites mouchetures ; avec `seeded`, des taches plus rondes ou, dès qu'il a des bras, des nœuds dont les ramifications s'étirent entre eux.

Comme une ceinture, un champ ignore `attempts` et `spread`, puisqu'il est interrogé par chunk et non par tentative, et il n'écrit jamais dans un chunk voisin. Il est calculé à partir de la graine du monde et du nom propre de l'entrée : la même graine donne donc toujours les mêmes filons, et deux entrées de noms différents ne s'alignent jamais. `replace`, `adjacent`, `biomes` et les limites de climat s'appliquent comme d'habitude.

## Propagations

*générer le monde*

Un bloc `spread` avec un `type`.

Chaque clé est montrée d'un coup. Un vrai fichier n'écrit que celles dont il a besoin. Une clé marquée pour un type n'est lue que par ce type.

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

| Type        | Où elle place les choses                                        |
| ----------- | --------------------------------------------------------------- |
| `even`      | N'importe où entre les altitudes, de façon uniforme. Le défaut  |
| `centered`  | Pondérée vers une altitude, de plus en plus clairsemée avec la distance |
| `sprawl`    | Filons fractals couvrant une plage d'altitudes                  |
| `terrain`   | Suit la surface                                                 |
| `cavern`    | Sur le sol des grottes, ou leur plafond                         |
| `submerged` | Sous l'eau ou un autre fluide                                   |

| Clé                 | Utilisée par | Valeur                      | Défaut                            | Ce qu'elle fait                                                    |
| ------------------- | ------------ | --------------------------- | --------------------------------- | ------------------------------------------------------------------ |
| `type`              | tous         | l'une des propagations ci-dessus | `even`                       | Quelle propagation                                                 |
| `center`            | centered     | int                         | milieu de la plage d'altitudes    | L'altitude autour de laquelle elle se concentre                    |
| `range`             | centered     | int                         | la moitié de la plage d'altitudes | Jusqu'où elle s'étend à partir de cette altitude                   |
| `smoothness`        | centered     | 1 à 8                       | `2`                               | Combien de tirages sont moyennés. Plus c'est haut, plus la bande est serrée |
| `veinHeight`        | sprawl       | int                         | la plage d'altitudes              | La hauteur d'un filon                                              |
| `veinDiameter`      | sprawl       | int                         | `12`                              | La largeur d'un filon                                              |
| `verticalDensity`   | sprawl       | 1 à 100                     | `16`                              | La compacité à la verticale                                        |
| `horizontalDensity` | sprawl       | 1 à 100                     | `32`                              | La compacité à l'horizontale                                       |
| `offsetMin`         | terrain      | int                         | `0`                               | Décalage minimal par rapport à la surface                          |
| `offsetMax`         | terrain      | int                         | `offsetMin`                       | Décalage maximal par rapport à la surface                          |
| `ceiling`           | cavern       | boolean                     | `false`                           | S'accrocher au plafond de la grotte plutôt qu'au sol               |

## Cartes de structures

*générer le monde*

Une carte de structures assemble des modèles en un seul bâtiment nommé sur une grille, bien au-delà de la limite de 48 blocs d'un seul fichier `.nbt`. Chaque couche est dessinée en rangées de caractères uniques, un caractère par cellule, et s'empile d'une hauteur de cellule au-dessus de la couche précédente. Au plus 8 couches de 8 par 8 cellules, soit 256 blocs de côté avec la cellule par défaut de 32.

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

| Réglage      | Type                  | Défaut | Ce qu'il fait                                                                                                                                    |
| ------------ | --------------------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| `cell`       | nombre                | `32`   | Le pas de la grille en blocs, jusqu'à 48. Un modèle plus petit que la cellule se place au coin de la cellule, de sorte que les pièces pleine taille se jouxtent sans raccord visible |
| `ground`     | nombre                | `0`    | Quelle couche pose son sol à la surface du terrain. Les couches qui la précèdent creusent vers le bas, ce qui donne des sous-sols au bâtiment      |
| `at`         | deux nombres          | aucun  | Épingle une copie à des coordonnées de bloc exactes, comme `structureAt` épingle un village                                                      |
| `spacing`    | nombre                | `0`    | Disperse des copies sur une grille espacée d'autant de chunks, avec un décalage aléatoire tiré de la graine du monde. `0` n'en disperse aucune, donc une carte avec seulement `at` ne se construit qu'une fois |
| `chance`     | nombre                | `100`  | Le pourcentage d'emplacements de la grille qui construisent une copie                                                                            |
| `dimensions` | liste d'ids de dimension | toutes | Où la carte peut se construire, y compris dans les dimensions propres à un pack                                                                |
| `layers`     | liste                 | aucun  | Les couches, de bas en haut, chacune avec une `palette` et une `map`                                                                             |

Une palette désigne des modèles par leur clé de registre, depuis le dossier `<namespace>/structures/` d'un pack.

| Valeur                                      | Ce qu'elle fait                                                                                                                                                       |
| ------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `"a": "mypack:keep"`                        | Chaque cellule `a` de cette couche place ce modèle                                                                                                                    |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | Chaque cellule `a` tire dans la liste selon le poids, à partir de la graine du monde et de l'emplacement de la cellule : deux copies du bâtiment diffèrent, mais le même monde construit toujours la même |
| `.`                                         | Une cellule vide, rien n'est placé                                                                                                                                    |

Chaque copie tire l'une des quatre orientations à partir de la graine du monde et tout le bâtiment tourne d'un bloc, modèles compris, de sorte que les murs qui se rejoignent d'une cellule à l'autre se rejoignent toujours ; une carte tourne mais ne se reflète jamais. La couche de sol pose son plancher à la surface du terrain échantillonnée sous le milieu du bâtiment, et toute la carte partage cette unique hauteur. Une carte dispersée est une structure à part entière pour le jeu, placée par un ensemble de structures écrit pour vous : chaque chunk ne construit donc que sa propre tranche de la grille, et un bâtiment s'étendant sur de nombreux chunks arrive sans génération en cascade, quel que soit l'ordre de chargement des chunks. Une [parcelle de village](#parcelles-de-village) de type `template` peut aussi désigner une carte comme `structure`, ce qui fait du composite un bâtiment de ville.

## Parcelles de village

*générer le monde*

`<namespace>/villages/*.json`

Le chemin du fichier est le nom de la parcelle, que `villagePieces` peut ensuite nommer pour la garder ou l'écarter.

Un fichier ici ajoute une pièce que les villes et villages du pack peuvent construire. Deux sortes, choisies avec `type`.

Chaque clé est montrée d'un coup. Un vrai fichier n'écrit que celles dont il a besoin. Une clé marquée pour un type n'est lue que par ce type.

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

### Chaque parcelle

*parcelles de village*

| Clé          | Utilisée par | Valeur                                  | Défaut           | Ce qu'elle fait                                                                                                                                                                                                                                                                                                                                                                    |
| ------------ | ------------ | --------------------------------------- | ---------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `type`       | tous         | `farm` ou `template`                    | `farm`           | Quelle sorte de parcelle                                                                                                                                                                                                                                                                                                                                                           |
| `weight`     | tous         | int                                     | `3`              | La fréquence à laquelle cette parcelle est choisie face aux autres du pack                                                                                                                                                                                                                                                                                                         |
| `leastCount` | tous         | int                                     | `1`              | Minimum par quartier : un quartier installe des parcelles le long de ses rues jusqu'à un plafond tiré entre le plus bas `leastCount` et le plus haut `mostCount` des parcelles qu'il peut construire, tous deux augmentés de 16, ou d'un trente-deuxième de `villagePlotsLeast` quand c'est plus, tant qu'une ville grandit. Les parcelles situées derrière d'autres parcelles ne comptent pas |
| `mostCount`  | tous         | int                                     | `4`              | Le haut de ce tirage                                                                                                                                                                                                                                                                                                                                                               |
| `width`      | tous         | int                                     | `7`              | Taille en travers de la rue                                                                                                                                                                                                                                                                                                                                                        |
| `height`     | tous         | int                                     | `4`              | Hauteur dégagée au-dessus du sol                                                                                                                                                                                                                                                                                                                                                   |
| `depth`      | tous         | int                                     | `9`              | Taille en s'éloignant de la rue                                                                                                                                                                                                                                                                                                                                                    |
| `apron`      | tous         | int                                     | `2`              | De combien le sol sous la parcelle peut s'écarter du niveau de la rue avant que la parcelle soit refusée ou glissée le long de sa rue : autant de blocs de remblai dessous, ou de déblai dans une hauteur au-dessus, et pas plus que cela entre son coin le plus haut et le plus bas. Une parcelle large en terrain vallonné en demande davantage. Réglé haut, la parcelle se met en terrasses droit dans une pente, ce qui, au mauvais endroit, dévore une montagne |
| `ground`     | tous         | nom de bloc                             | `minecraft:dirt` | Ce qui est tassé dessous sur une pente                                                                                                                                                                                                                                                                                                                                             |
| `requires`   | tous         | liste d'ids de mod ou de namespaces de pack | aucun        | La parcelle est écartée à moins que tous soient présents                                                                                                                                                                                                                                                                                                                           |

Les parcelles sont ce que les villes propres au pack construisent le long de leurs rues, et chaque parcelle de type modèle rejoint aussi les villages du jeu comme l'une de leurs maisons, avec l'entrée au milieu de sa façade. Sans aucun fichier de parcelle, une ville construit les maisons de village du jeu pour le type de village du biome de chaque quartier. `weight` décide laquelle de vos parcelles est choisie quand une rue en demande une, et `villagePieces` dans les réglages `villages` nomme les parcelles qu'un modèle garde, sous la forme `mypack:smithy`, `smithy` ou la structure que la parcelle construit. La façon dont les rues elles-mêmes sont tracées, habillées, franchies par des ponts, percées de tunnels et dotées de rails relève des réglages `village*` décrits dans [Ce que fait chaque groupe](#ce-que-fait-chaque-groupe).

### Fermes

*parcelles de village*

Une `farm` est un champ décrit plutôt que codé : une parcelle de la taille demandée, bordée d'un bloc, remplie de rangées de terre séparées par des canaux d'eau, plantée d'une culture choisie bloc par bloc dans votre liste.

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

| Clé        | Utilisée par | Valeur                  | Défaut               | Ce qu'elle fait                                        |
| ---------- | ------------ | ----------------------- | -------------------- | ------------------------------------------------------ |
| `crops`    | farm         | liste de noms de blocs  | blé                  | Planté un par bloc, à un stade de croissance aléatoire |
| `edge`     | farm         | nom de bloc             | `minecraft:oak_log`  | Le cadre autour de la parcelle                         |
| `soil`     | farm         | nom de bloc             | `minecraft:farmland` | De quoi les rangées sont faites                        |
| `water`    | farm         | boolean                 | `true`               | Placer un canal d'eau entre les rangées                |
| `rowWidth` | farm         | int                     | `2`                  | La largeur de chaque rangée de terre                   |

### Construit à partir de modèles

*parcelles de village*

Un `template` place à la place l'une de vos structures `.nbt`, tournée pour faire face à la rue.

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

Un `template` dont la `structure` désigne l'une de vos [cartes de structures](#cartes-de-structures) place tout le composite comme parcelle. La taille de la parcelle vient alors de la carte, son emprise et ses couches empilées multipliées par la cellule, de sorte que `width`, `height`, `depth` et `integrity` ne sont pas lus. Les couches qui précèdent le `ground` de la carte creusent vers le bas comme sous-sols, et les cellules de palette pondérées sont toujours tirées bâtiment par bâtiment : deux tours issues de la même carte peuvent donc différer.

```json
{
  "type": "template",
  "weight": 2,
  "structure": "mypack:castle",
  "villagers": 4
}
```

| Clé              | Utilisée par | Valeur           | Défaut        | Ce qu'elle fait                                                                                                                                              |
| ---------------- | ------------ | ---------------- | ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `structure`      | template     | `namespace:name` | aucun         | Le modèle à placer, ou l'une de vos cartes de structures, qui fixe alors la taille de la parcelle                                                            |
| `integrity`      | template     | 1 à 100          | `100`         | Pourcentage des blocs du modèle qui apparaissent                                                                                                             |
| `lootTable`      | template     | `namespace:path` | aucun         | La table de butin dont est rempli chaque coffre du modèle placé, la première fois qu'il est ouvert. Une parcelle qui désigne une carte de structures est laissée telle quelle |
| `villagers`      | tous         | int              | `0`           | Combien de personnes la parcelle fait apparaître                                                                                                             |
| `villagerEntity` | tous         | `namespace:name` | un villageois | Qui y habite, par exemple une variante d'entité de votre cru                                                                                                 |
| `villagerX`      | tous         | int              | `1`           | Où ils apparaissent, en travers de la parcelle                                                                                                               |
| `villagerY`      | tous         | int              | `1`           | Où ils apparaissent, au-dessus du sol                                                                                                                        |
| `villagerZ`      | tous         | int              | `1`           | Où ils apparaissent, en profondeur dans la parcelle                                                                                                          |

## Cartes de plan des villes

*générer le monde*

Une carte de ville dessine le plan des rues d'une ville sur une grille, un caractère par cellule, et la ville est tracée d'après le dessin au lieu d'être tirée au hasard. Les rues, places et parcelles sortent sous forme des mêmes pièces qu'une ville tirée au hasard, de sorte que chaque option de rue, pont, tunnel, métro, égout, lampadaire et pièce maîtresse de place s'applique sans changement. Le modèle de monde nomme la carte dans `villageLayout`.

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

| Réglage    | Type   | Défaut | Ce qu'il fait                                                                                                                                                                                              |
| ---------- | ------ | ------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `cell`     | nombre | `48`   | Le pas de la grille en blocs, de 8 à 128. Les rues courent au milieu de leurs cellules à la largeur de rue du pack et les parcelles sont centrées dans les leurs : une cellule doit donc accueillir la plus large parcelle plus la place de border la rue |
| `palette`  | objet  | aucun  | Ce que pose chaque caractère, détaillé ci-dessous                                                                                                                                                          |
| `map`      | liste  | aucun  | Les rangées, jusqu'à 64 par 64 cellules. Une rangée plus courte que la plus large est ouverte au-delà de sa fin                                                                                            |
| `settings` | objet  | aucun  | Réglages de ville propres à cette carte, sous les noms qu'utilise un modèle de monde, tels que `villagePathCenterBlock`. Ils l'emportent sur ceux du modèle, et les réglages propres à un biome l'emportent encore sur eux |

| Valeur                                                                  | Ce qu'elle fait                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| ----------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `"#": "street"`                                                         | Une suite de cellules de rue le long d'une rangée ou d'une colonne devient une seule rue à la largeur du pack. Là où une suite de rangée croise une suite de colonne, le carrefour est peint comme n'importe quel autre. Une cellule de rue isolée, sans suite dans aucun des deux axes, est posée comme un court tronçon le long de la rangée                                                                                                                         |
| `"+": "plaza"`                                                          | Une place avec sa pièce maîtresse. Les suites traversent les cellules de place, de sorte que les rues se rejoignent à la place, et une place sur un croisement dresse sa pièce maîtresse `villageWellStructure` au milieu du carrefour comme un rond-point. La première place du fichier est le centre même de la ville, ce qui cale la carte sur l'endroit où la ville est fondée ; une carte sans place y est centrée                                                  |
| `"a": "alley"`                                                          | Une suite étroite. Les bâtiments y donnent, mais elle ne relie rien, selon la règle habituelle des ruelles                                                                                                                                                                                                                                                                                                                                                             |
| `"J": "junction"`                                                       | Une cellule de rue tracée dans les deux sens, de sorte qu'un croisement s'y dresse même quand le dessin ne la traverse que dans un sens. Le bras qui la traverse mesure une cellule de long                                                                                                                                                                                                                                                                             |
| `"b": "bulb"`                                                           | Une cellule de rue qui se termine en cul-de-sac. Dès qu'une carte a une cellule bulbe, seules les extrémités de rue situées dans des cellules bulbes en reçoivent un ; une carte sans aucune donne un cul-de-sac à trois impasses sur quatre, tiré de la graine du monde. Un cul-de-sac n'est implanté que là où aucune parcelle, autre rue, voie ferrée ou pièce maîtresse de place ne se dresse à sa portée : il rétrécit pour tenir, jusqu'à un peu plus large que la rue, et une extrémité sans place à aucune taille reste une simple extrémité                    |
| `"E": { "kind": "elevated", "height": 8 }`                              | Une cellule de rue surélevée sur un tablier de `height` blocs, de 2 à 64, au-dessus du terrain le plus haut sous son tronçon de cellules surélevées jointes, avec une rampe d'un bloc par rangée à chaque bout. Un croisement de rues à l'intérieur du tronçon monte avec lui, et les parcelles qui le bordent restent au sol. Un tronçon dont le tablier ou les rampes atteindraient une voie ferrée ou la pièce maîtresse de place reste au niveau du sol, avec une ligne dans le journal. N'importe quelle valeur peut s'écrire ainsi sous forme d'objet, `kind` nommant le mot |
| `"W": { "kind": "street", "settings": { "villagePathExtraWidth": 8 } }` | Une rue tracée et pavée avec ses propres clés de rue, qui l'emportent sur celles de la carte et du modèle. Sa largeur suit son propre `villagePathExtraWidth`, `villagePathSidewalkWidth` et sa ligne, et sa surface, ses lignes et ses trottoirs suivent ses propres clés de bloc : une avenue ou une venelle se dessine ainsi avec un marquage à elle. Une suite prend les clés de sa première cellule qui en définit. Aussi large ou étroite soit-elle, une rue dessinée reste une rue : elle n'est jamais prise pour une ruelle |
| `"T": "mypack:tower"`                                                   | Une cellule de parcelle, tracée d'après cette définition de parcelle, centrée dans la cellule et tournée vers la rue la plus proche                                                                                                                                                                                                                                                                                                                                    |
| `"T": ["mypack:a=3", "mypack:b=1"]`                                     | Pareil, tiré selon le poids à partir de la graine du monde et de l'emplacement de la cellule, de sorte que le même monde pose toujours la même parcelle à cet endroit                                                                                                                                                                                                                                                                                                  |
| `"g": "grow"`                                                           | Laissé à la disposition tirée au hasard, qui remplit ces cellules et s'étend vers l'extérieur à partir de la carte                                                                                                                                                                                                                                                                                                                                                     |
| `.` ou `open`                                                           | Terrain libre, rien de posé                                                                                                                                                                                                                                                                                                                                                                                                                                            |

Chaque carte tire l'une des quatre orientations à partir de la graine du monde et tourne d'un bloc, de sorte qu'un plan se lit pareil quel que soit le côté. Les rues sont tracées en premier : une parcelle qui chevaucherait une rue ou une autre parcelle reste donc ouverte, avec une ligne dans le journal, et un nom de parcelle qu'aucun pack ne fournit laisse sa cellule ouverte de la même façon. La carte ne change pas l'habillage des pièces : les clés de rue, `villageBlocks`, les lampadaires et la pièce maîtresse de place se lisent tous comme pour une ville tirée au hasard.

## Retrogen

*générer le monde*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "retrogen": true,
    "adoptExistingChunks": false
  }
}
```

| Réglage               | Type    | Défaut  | Ce qu'il fait                                                                                                                                                                                                                                                              |
| --------------------- | ------- | ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `retrogen`            | boolean | `false` | Rattrape les chunks sauvegardés avant l'existence d'une entrée, pour chaque entrée de worldgen marquée `"retrogen": true`. Désactivé, les chunks déjà existants sont laissés tels quels. Les chunks sont marqués au fur et à mesure de leur génération dans les deux cas : l'activer plus tard ne touche donc que les chunks plus anciens que le pack |
| `adoptExistingChunks` | boolean | `false` | Ce qui se passe la première fois qu'un ancien chunk est vu : activé, il est estampillé comme si ce pack l'avait déjà généré et n'est jamais rattrapé ; désactivé, il est rattrapé comme n'importe quel autre. Pour remplir un monde existant, activez `retrogen` et laissez ceci désactivé |

Une entrée avec `"retrogen": true` est générée dans les chunks qui ont été sauvegardés avant que vous ne l'ajoutiez. Chaque chunk enregistre ce qu'il a déjà reçu, de sorte que rien n'est fait deux fois.

Le drapeau d'entrée marque seulement une entrée comme éligible. Le rattrapage est activé par le réglage `retrogen`, qu'un pack peut définir dans son bloc `settings` ou qu'un joueur peut définir dans la configuration, et il est désactivé par défaut. À côté, `adoptExistingChunks` décide de ce qui se passe la première fois qu'un ancien chunk est vu : activé, le chunk est estampillé comme si ce pack l'avait déjà généré et n'est jamais rattrapé ; désactivé, il est rattrapé comme n'importe quel autre. Activer `retrogen` alors que `adoptExistingChunks` l'est aussi ne fait rien, car chaque ancien chunk est écarté avant de pouvoir être mis en file d'attente. Pour remplir un monde existant, activez `retrogen` et désactivez `adoptExistingChunks` en même temps. `retrogenChunksPerTick` dans la configuration, `2` par défaut, est le nombre d'anciens chunks rattrapés à chaque tick.

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

Modifier `retrogenKey` dans la configuration rend à nouveau tous les chunks éligibles, ce qui ajoute les nouveaux filons par-dessus les anciens : la densité double. C'est voulu, et c'est pourquoi la clé est manuelle.

## Prégénération

*générer le monde*

Créer le terrain d'un monde à l'avance, pour que personne ne génère de chunks en jouant : pas de lag de chunks, une taille connue sur le disque, et une seule attente au départ au lieu d'une première heure saccadée.

Les 12 premiers chunks autour du point d'apparition sont toujours pris en charge, quoi que disent un pack ou la configuration, car le jeu en fait exactement autant lui-même avant que quiconque se connecte. `pregenOnNewWorld` fixe jusqu'où aller au-delà, et la commande en lance une à la main.

`/rdplserver pregen <radius>` génère chaque chunk situé à cette distance en chunks de l'endroit où elle est lancée. `status` indique où elle en est et `stop` y met fin. Le terrain est demandé au système de chunks du jeu lui-même, `pregenChunksInFlight` chunks à la fois, un fichier de région de 32 par 32 chunks à la fois, les régions étant prises en anneaux à partir du milieu et les chunks d'une région entière suivant une courbe de Hilbert, chaque région étant terminée avant que la suivante soit commencée ; il revient éclairé et fini, il n'y a donc pas de passe d'éclairage à lancer ensuite.

Pendant une exécution, tout le monde est retenu : mis en spectateur, maintenu en place, avec une ligne pulsante au milieu de l'écran et la progression dans la barre d'action, le ciel figé autour d'eux, chaque créature et chaque machine de chaque dimension gelées, et l'heure et la météo de la dimension en cours de création maintenues là où elles étaient. Le mode dans lequel chaque joueur est arrivé est écrit sur le joueur au moment où il est retenu : une sauvegarde faite en pleine exécution, un plantage ou une reconnexion ne laisse donc jamais personne bloqué en spectateur ; la fin de l'exécution rend exactement le mode qu'elle a pris, ou le `worldGameMode` du pack s'il y en a un, survie pour `hardcore`. Un client avec le mod voit la vue embrumée pendant la retenue et le logo apparaître en fondu ensuite ; un client vanilla voit la simple retenue. Jusqu'où chaque dimension a été créée est enregistré dans le monde, si bien qu'un monde terminé ne relance jamais. La fin est annoncée à tout le monde avant que la sauvegarde du monde soit faite, et une exécution lancée depuis la console ou un bloc de commande y renvoie ses décomptes.

Dans un pack, ces clés vont dans le bloc `settings` d'un [modèle de monde](#modèles-de-monde), comme toutes les autres clés `chunks`. Chacune est montrée, `pregenBorderLimit` étant la seule absente puisque seule la configuration la détient :

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
    "saysImage": "rubyworld:textures/gui/card.png",
    "saysBackground": true,
    "saysFont": "rubyworld:runes",
    "toasts": ["advancements"]
  }
}
```

### Ce qui est créé

*prégénération*

| Clé                           | Ce qu'elle fait                                                                                                                                                                                                                                         | Pourquoi la définir                                                                  |
| ----------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------ |
| `pregenOnNewWorld`            | Rayon en chunks créé autour du point d'apparition avant que quiconque joue. 12 est le plancher et 0 signifie ce plancher et non rien, puisque le jeu génère de lui-même 12 chunks autour du point d'apparition. Augmentez-le pour aller plus loin que le jeu | Fixe jusqu'où un pack s'étend au-delà du terrain que le jeu crée déjà                |
| `pregenDimensions`            | Quelles dimensions sont créées, par id, dans l'ordre, chacune autour de son propre point d'apparition                                                                                                                                                    | Ajouter le Nether, l'End ou vos propres dimensions                                   |
| `pregenAllDimensions`         | Chaque dimension que le serveur contient au lieu d'une liste, l'overworld d'abord et les autres dans l'ordre des ids                                                                                                                                    | Packs avec de nombreuses dimensions. Les dimensions de chaque mod comptent, attention à la taille |
| `pregenDimensionsWhenEntered` | Celles-ci sont créées la première fois que quelqu'un y met le pied, en retenant à nouveau tout le monde jusqu'à la fin                                                                                                                                  | Dimensions que la plupart des joueurs ne visitent jamais ; ceux qui n'y vont jamais ne paient rien |
| `pregenToBorder`              | Remplir chaque dimension jusqu'à sa bordure du monde au lieu d'un rayon, centré sur la bordure                                                                                                                                                          | Mondes bornés                                                                        |
| `pregenBorderLimit`           | Jusqu'où une bordure peut s'étendre, en chunks dans chaque sens, avant que l'exécution soit refusée. Configuration uniquement, jamais une clé de pack                                                                                                    | Une protection contre une exécution qui s'emballe ; ne l'augmentez qu'en connaissant le temps et le disque qu'elle autorise |

Lancez-la vous-même avant de livrer, au rayon livré, du début à la fin. Les chunks croissent comme le carré du rayon : 63 dans chaque sens font seize mille chunks, 500 en font plus d'un million ; le dossier de région de votre monde de test et le temps réel écoulé sont donc les chiffres honnêtes à présenter aux joueurs. Ne livrez pas un rayon qui n'a jamais été exécuté.

### Comportement d'une partie

*prégénération*

| Clé                    | Ce qu'elle fait                                                                                                                                                                                                                                                                                                                                                                                                   | Pourquoi la définir                                                    |
| ---------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------- |
| `pregenResume`         | Une exécution arrêtée ou interrompue reprend là où elle s'était arrêtée. La dimension, le centre et le rayon de l'exécution sont écrits dans la sauvegarde au démarrage, et le décompte atteint toutes les dix secondes : un plantage, une coupure de courant ou un arrêt en cours d'exécution reprennent donc tous à environ dix secondes de là où ils se sont arrêtés, au chargement suivant. Une exécution arrêtée volontairement, par commande ou par le chien de garde de blocage, reste arrêtée | Longues exécutions sur serveurs ; les petites redémarrent à peu de frais sans cela |
| `pregenChunksInFlight` | Combien de chunks l'exécution demande au jeu à la fois. Plus il y en a, plus les threads de génération restent occupés et moins le serveur répond à ceux qui sont retenus à regarder                                                                                                                                                                                                                              | À augmenter sur un serveur vide, à baisser sur un serveur où l'on joue |

### Ce que voient les joueurs

*prégénération*

| Clé                                                            | Ce qu'elle fait                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | Pourquoi la définir                                                                                                    |
| -------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| `pregenRunningSays`, `pregenFinishedSays`, `pregenStoppedSays` | Les messages de chaque étape. Le premier peut contenir `%d` pour le pourcentage puis, après lui, `%s` pour le nom de la dimension, ou `%1$d` et `%2$s` pour les mettre dans l'ordre voulu. Laissés à leurs valeurs par défaut, ils parlent la langue de chaque joueur                                                                                                                                                                                                                                                              | Reformulez-les dans la voix de votre pack, nommez la dimension quand plusieurs sont créées, ou faites-les taire         |
| `pregenSpectatingSays`                                         | La ligne de retenue au milieu de l'écran pendant la création du terrain. Laissée à sa valeur par défaut, elle parle la langue de chaque joueur ; vide, elle n'affiche rien                                                                                                                                                                                                                                                                                                                                                         | Gardez-la sous environ trente-cinq caractères, sinon les petites fenêtres la tronquent                                  |
| `pregenLogo`                                                   | Où se tient le logo à la fin de la prégénération : `left`, `center` ou `right`, au-dessus du texte du milieu de l'écran, affiché quelques secondes puis disparaissant en fondu avec le brouillard                                                                                                                                                                                                                                                                                                                                  | Il est toujours affiché ; un mot inconnu est lu comme `center`                                                          |
| `welcomeSays`                                                  | Le message d'accueil vert, affiché à chaque connexion et après la prégénération. Une entrée simple est la ligne valable partout ; une entrée `dimension=message` la remplace pour cette dimension et accueille aussi chaque arrivée là-bas, par ex. `"minecraft:the_nether=Welcome to the Nether!"`. La dimension peut aussi s'écrire `0`, `-1` ou `1` comme en 1.12.2, et personne n'est accueilli en arrivant pendant la création du terrain. Un message vide après le `=` fait taire cette dimension ; une liste vide n'affiche rien. Laissé à sa valeur par défaut, il parle la langue de chaque joueur | Une ligne simple nomme votre pack ; ajoutez des lignes par dimension pour thématiser chaque monde. Gardez les lignes sous environ trente-cinq caractères |
| `saysCard`                                                     | Affiche les lignes que dit ce mod, l'accueil, la note de création du terrain que reçoit un joueur qui se connecte en cours d'exécution et la fin de l'exécution (la progression en cours reste dans la barre d'action), ainsi que les lignes de menace, sous forme de carte dans le coin inférieur droit au lieu du chat. La carte glisse en place, reste huit secondes puis s'estompe, et s'affiche aussi par-dessus un écran ouvert                                                                                                | Activez-la quand le chat est chargé ou que les lignes doivent se lire comme une partie du monde plutôt que comme du bavardage |
| `saysIcon`                                                     | Un objet dessiné sur la carte, par ex. `minecraft:compass`. Vide, rien n'est dessiné                                                                                                                                                                                                                                                                                                                                                                                                                                               | Donnez à la carte l'emblème de votre pack                                                                              |
| `saysColor`                                                    | La couleur de fond de la carte en hexadécimal, par ex. `1E2630`. Vide, un gris ardoise sombre est utilisé                                                                                                                                                                                                                                                                                                                                                                                                                          | Accordez-la à la palette de votre pack                                                                                 |
| `saysImage`                                                    | Un PNG des assets client du pack, par ex. `rubyworld:textures/gui/card.png`, étiré sur la carte comme fond et dessiné par-dessus la couleur. Vide, rien n'est dessiné                                                                                                                                                                                                                                                                                                                                                               | Donnez à la carte un panneau peint ; gardez l'image large et basse, elle est étirée à la taille que le texte exige     |
| `saysBackground`                                               | Dessine le panneau de la carte, sa bordure et le liseré de couleur, ainsi que le fond sombre derrière l'accueil et les notes au milieu de l'écran pendant qu'un joueur est retenu. Désactivé, il ne reste que le texte, qui garde son ombre, et `saysImage` s'il est défini                                                                                                                                                                                                                                                         | Laissez les lignes flotter par-dessus le monde, ou laissez un `saysImage` peint se suffire à lui-même                  |
| `saysFont`                                                     | La police dans laquelle est dessiné le texte de la carte, nommée `namespace:name`, par ex. `rubyworld:runes`. Vide, la police RDPL, `resourcedatapackloader:rdpl`, est utilisée. Le fichier qu'elle nomme est décrit sous Cartes                                                                                                                                                                                                                                                                                                      | Donnez à la carte la typographie propre à votre pack                                                                   |
| `toasts`                                                       | Lesquels des toasts du jeu, les fenêtres contextuelles dans le coin supérieur droit, sont affichés. `true` les affiche tous et `false` aucun ; une liste n'affiche que les sortes qu'elle nomme : `advancements`, `recipes` pour les recettes débloquées, `tutorial` pour les conseils du tutoriel, `system` pour les notifications propres au jeu, et `other` pour tous les toasts que les autres ne couvrent pas, tels que ceux d'autres mods. Par défaut, aucun n'est affiché. Le client d'un joueur prend la valeur à sa connexion | Gardez `["advancements"]` quand votre pack guide les joueurs par des advancements et que le reste gêne                  |

### Sauvegarde et réinitialisation de la carte

*prégénération*

| Clé                     | Ce qu'elle fait                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   | Pourquoi la définir                                                              |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------- |
| `pregenBackup`          | Copier le monde dans une sauvegarde vierge une fois la prégénération terminée, tant que les joueurs sont encore retenus. La génération n'est alors payée qu'une fois : une réinitialisation ultérieure, ou un nouveau monde avec le même pack et la même graine, restaure la copie au lieu de générer à nouveau, ce qui est bien plus rapide que de prégénérer deux fois. La copie est conservée hors de la sauvegarde, dans `rdpl-pristine/<world>` à côté d'elle, de sorte que les sauvegardes d'un autre mod ne l'emportent pas et qu'elle n'apparaît pas dans un dossier qu'ils gèrent. Une copie dont les packs ne correspondent plus à ceux chargés est jetée et refaite à partir du monde en cours, si bien qu'un changement de pack ne réinitialise jamais vers la carte de quelqu'un d'autre | `false`                                                                          |
| `pregenBackupSays`      | La ligne au milieu de l'écran affichée aux joueurs pendant que cette copie est faite, avec le pourcentage derrière. Vide, rien n'est affiché et la copie se fait en silence                                                                                                                                                                                                                                                                                                                                                                                                                       | `Pack requested world backup`                                                    |
| `resetSays`             | La ligne au milieu de l'écran affichée aux joueurs pendant que `/rdplserver reset` ou la fin d'une manche remet la carte en place. Vide, la réinitialisation se fait en silence                                                                                                                                                                                                                                                                                                                                                                                                                   | `Pack requested map reset`                                                       |
| `resetSendsTo`          | Où les joueurs sont placés par une réinitialisation : `spawn`, une position sous la forme `x,y,z` ou `x,z`, où la hauteur est un bloc au-dessus du niveau de la mer, ou l'une ou l'autre derrière `dimension:` pour les envoyer dans un autre monde, la dimension par id ou sous la forme `0`, `-1` ou `1` de la 1.12.2, ce qui permet à une réinitialisation de déposer tout le monde dans un hall d'attente plutôt que de le remettre dans l'arène                                                                                                                                                | `spawn`                                                                          |
| `resetRuns`             | Une fonction exécutée après qu'une réinitialisation a vidé la carte, nommée `namespace:path`. C'est ce qui reconstruit l'arène, puisqu'un pack qui a créé sa carte à partir d'une fonction peut simplement la relancer une seconde fois. Vide, rien n'est exécuté                                                                                                                                                                                                                                                                                                                                 | vide                                                                             |
| `resetClearsEntities`   | Supprimer toute entité qui n'est pas un joueur. Mobs, objets au sol et expérience disparaissent tous, ce qui remet la carte comme au départ                                                                                                                                                                                                                                                                                                                                                                                                                                                       | `true`                                                                           |
| `resetClearsScores`     | Remettre à rien chaque objectif que tient le pack, pour qu'un nouveau match reparte de zéro. Les équipes elles-mêmes sont conservées                                                                                                                                                                                                                                                                                                                                                                                                                                                              | `true`                                                                           |
| `resetClearsInventory`  | Vider l'inventaire de chaque joueur, armure et seconde main comprises, pour qu'une manche commence avec ce que distribue la carte et non ce que la précédente a laissé. Le `gives` d'un camp est redistribué juste après                                                                                                                                                                                                                                                                                                                                                                          | `false`                                                                          |
| `resetClearsExperience` | Remettre l'expérience de chaque joueur au niveau zéro                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | `false`                                                                          |
| `spawnChunkRadius`      | À quelle distance du point d'apparition, en blocs, les chunks sont maintenus chargés, qu'un joueur s'y trouve ou non, arrondie en chunks entiers : `(blocks + 8) / 16` dans chaque sens, de sorte que le défaut `128` en maintient 8. Au démarrage d'un monde, l'overworld prépare un carré de 4 chunks de plus dans chaque sens avant que le serveur soit prêt. `0` n'en prépare ni n'en maintient aucun. RDPL les maintient avec ses propres tickets de chunk : en 1.21.1, la règle de jeu `spawnChunkRadius` ne fait donc rien tant que cette clé est en vigueur                              | Maintenir en marche une machine ou une ferme au point d'apparition, ou désactiver les chunks de spawn avec `0` |
| `spawnChunkRadii`       | Un rayon pour l'overworld écrit sous la forme `dimension=blocks`, comme dans `minecraft:overworld=64`, qui remplace `spawnChunkRadius`. Seul l'overworld a des chunks de spawn : une entrée pour toute autre dimension ne change donc rien                                                                                                                                                                                                                                                                                                                                                         | Dimensionner la zone d'apparition dans un pack qui fixe ses rayons par dimension |

---

# Modes de jeu

## Introduction au monde

*modes de jeu*

`<namespace>/worldintro/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu. Chaque introduction livrée par un pack s'exécute, dans l'ordre des packs.

Affiche une suite de pages quand un joueur entre dans le monde, avant qu'il prenne le contrôle. Du texte défilant sur une image, un écran-titre, un diaporama, ou les trois à la suite.

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

| Clé        | Obligatoire | Valeur                                      | Défaut | Ce qu'elle fait                                                                  |
| ---------- | ----------- | ------------------------------------------- | ------ | -------------------------------------------------------------------------------- |
| `pages`    | oui         | liste de pages                              | aucun  | Affichées dans l'ordre. Un fichier sans page est refusé avec une erreur          |
| `once`     | non         | boolean                                     | `false` | Jouer une fois par joueur et par monde au lieu de chaque connexion              |
| `music`    | non         | nom d'événement sonore                      | aucun  | Une piste pour toute la suite, lancée avec la première page                      |
| `requires` | non         | liste d'ids de mod ou de namespaces de pack | aucun  | L'introduction est ignorée à moins que tous soient présents                      |

### Pages

*introduction au monde*

| Clé           | Obligatoire | Valeur                   | Défaut                        | Ce qu'elle fait                                                                                                                                                                                                                                         |
| ------------- | ----------- | ------------------------ | ----------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `mode`        | non         | `scroll` ou `static`     | `scroll`                      | Du texte qui bouge, ou du texte qui reste immobile jusqu'à ce que le joueur passe à la suite                                                                                                                                                            |
| `text`        | non         | chemin d'un fichier `.txt` | aucun                       | Les mots. Omettez-le pour une page qui n'est que des images                                                                                                                                                                                             |
| `background`  | non         | chemin de texture        | le fond de terre en mosaïque  | Un seul fond                                                                                                                                                                                                                                            |
| `backgrounds` | non         | liste de chemins de texture | aucun                      | Plusieurs, en alternance. S'ajoute à `background` si vous donnez les deux                                                                                                                                                                               |
| `interval`    | non         | secondes                 | `5.0`                         | Combien de temps chaque fond est maintenu, quand il y en a plusieurs                                                                                                                                                                                    |
| `time`        | non         | secondes                 | calculé d'après le texte      | Combien de temps dure une page défilante, du début à la fin. Sur une page fixe, ou sur la dernière page de n'importe quelle sorte, c'est le délai avant que la page passe d'elle-même à la suite ; sans lui, elles attendent le bouton                    |
| `direction`   | non         | `up` ou `down`           | `up`                          | Dans quel sens voyage le texte défilant                                                                                                                                                                                                                 |
| `textScale`   | non         | nombre                   | `1.0`                         | Multiplie la taille de la police. Une page `static` adapte son texte à la largeur de l'écran, moins une marge de chaque côté, et quand il passerait encore sous les boutons, son texte est dessiné plus petit, jusqu'à la moitié, jusqu'à ce qu'il tienne |
| `settle`      | non         | boolean                  | `false`                       | Terminer avec la dernière ligne centrée plutôt que de sortir complètement de l'écran                                                                                                                                                                    |

### Texte et durée

*introduction au monde*

Les fichiers texte vont dans `assets/<namespace>/texts/*.txt`. Texte brut, un paragraphe par ligne, et les lignes vides sont conservées comme lignes vides. Un fichier `.md` est lu de la même façon, et l'un comme l'autre acceptent la mise en forme ci-dessous. `PLAYERNAME` est remplacé par le nom du joueur, la même substitution que celle du poème de fin vanilla.

`time` fixe la durée de la page, de sorte que la même page prend le même temps qu'elle contienne une ligne ou vingt. Réglez la vitesse de lecture d'après la quantité de texte que vous mettez sur la page. Omettez `time` et la page défile à la même vitesse que les crédits vanilla, où plus de texte prend simplement plus de temps.

### Mise en forme du texte

*introduction au monde*

Les textes d'introduction acceptent le Markdown. Un fichier sans marque s'affiche exactement comme du texte brut.

```
# The Long Night
## Chapter one
Welcome, **PLAYERNAME**. The *old roads* are ~~open~~ closed; type `/spawn` to go back.
- Find the **lighthouse**
- Keep the fire lit; a long item wraps under its own text, not under the bullet
  - A nested item
1. Gather wood
2. Build the gate
> The keeper wrote this before the storm.
---
![The lighthouse](mypack:textures/gui/lighthouse.png)
See [the map](https://example.com/map) for the way, and \*this\* stays plain.
```

| Marque        | Écrite sous la forme                                      | S'affiche comme                                                                                                                                                                                                                                                                        |
| ------------- | --------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Titre         | `# `, `## `, `### ` en début de ligne                     | Gras et plus grand : deux fois, une fois et demie et une fois un quart la taille du texte, aligné comme le corps du texte                                                                                                                                                              |
| Gras          | `**text**`                                                | La chasse grasse de la police                                                                                                                                                                                                                                                          |
| Italique      | `*text*`                                                  | La chasse italique de la police                                                                                                                                                                                                                                                        |
| Gras italique | `***text***`                                              | La chasse grasse, penchée                                                                                                                                                                                                                                                              |
| Barré         | `~~text~~`                                                | Barré                                                                                                                                                                                                                                                                                  |
| Code          | `` `text` ``                                              | Teinté d'aqua                                                                                                                                                                                                                                                                          |
| Lien          | `[text](url)`                                             | Le texte seul, souligné ; non cliquable                                                                                                                                                                                                                                                |
| Runique       | `{runic}text{/runic}`                                     | Le texte dans le chiffre runique, `resourcedatapackloader:rdpl_runic`, tandis que le reste de la ligne garde sa police ; le gras et l'italique à l'intérieur prennent les chasses grasse et italique du chiffre. Cela fonctionne dans les titres, les éléments de liste et les citations, et un `{runic}` non fermé s'affiche tel qu'écrit |
| Puce          | `- ` ou `* ` en début de ligne                            | Une puce, avec les lignes de retour à la ligne indentées sous le texte ; deux espaces avant la marque l'imbriquent d'un niveau                                                                                                                                                          |
| Numéroté      | `1. ` en début de ligne                                   | Le numéro tel qu'écrit, indenté de la même façon                                                                                                                                                                                                                                       |
| Citation      | `> ` en début de ligne                                    | Indentée et atténuée                                                                                                                                                                                                                                                                   |
| Filet         | `---` sur une ligne à lui seul                            | Une ligne horizontale sur toute la largeur du texte                                                                                                                                                                                                                                    |
| Image         | `![alt](namespace:textures/....png)` sur une ligne à elle seule | L'image, réduite à la largeur du texte en gardant ses proportions ; le texte alternatif s'affiche si elle ne peut pas être lue                                                                                                                                                  |
| Échappement   | `\` devant une marque, par ex. `\*`                       | La marque comme simple caractère                                                                                                                                                                                                                                                       |

Les tableaux et les blocs de code délimités (entre des lignes ```) sont dessinés en texte brut, marques comprises. Le temps calculé d'une page défilante et la réduction d'une page fixe pour tenir comptent tous deux la hauteur mise en page, images comprises. Les titres et lignes de carte, les messages Says et les notes d'accueil et de retenue acceptent les marques en ligne du gras au runique, une ligne chacun.

### Déroulement du jeu

*introduction au monde*

Une page défilante passe à la suivante quand son temps est écoulé. La dernière page n'avance jamais d'elle-même, elle attend. En bas se trouvent **Page suivante** et **Tout passer**, ou un unique **Continuer vers le monde** sur la dernière page. Échap fait la même chose que Tout passer. Les pages fixes centrent chaque ligne. Les pages défilantes gardent une colonne fixe, à la manière des crédits.

En solo, le monde se met en pause derrière l'introduction, de sorte que rien ne s'approche du joueur pendant qu'il lit. La seule exception est un terrain encore en cours de création à l'ouverture de l'introduction : la création se poursuit alors derrière les pages, et le joueur reste retenu en spectateur jusqu'à ce qu'il continue vers le monde, même si l'exécution se termine avant. Sur un serveur, le monde continue de tourner, et un client vanilla ne voit jamais l'introduction et se connecte normalement. Le message d'accueil attend que les pages soient fermées, pour ne pas être perdu derrière elles.

`once` est mémorisé dans les données sauvegardées du joueur et survit à la mort. `/rdplserver intro` l'efface pour celui qui la lance, de sorte que l'introduction se rejoue à sa prochaine connexion. Elle ne se rejoue pas sur-le-champ, ce qui évite d'en faire un moyen de retourner à la séquence d'entrée au milieu d'une partie.

Les fonds sont étirés pour remplir la fenêtre : une image 16:9 convient donc à une fenêtre 16:9 et une image carrée paraît écrasée. Recadrez l'image à la bonne forme plutôt que de compter sur l'ajustement. `music` accepte n'importe quel événement sonore enregistré, vanilla ou ajouté par votre propre pack via `sounds`. Elle ne boucle pas : une piste courte se termine donc et laisse le silence derrière elle.

Si plusieurs packs livrent une introduction, leurs pages s'enchaînent dans l'ordre des packs au lieu qu'une seule l'emporte. Conditionnez-les avec `requires` si vous n'en voulez qu'une.

## Équipes

*modes de jeu*

`<namespace>/teams/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers s'empilent. Chaque fichier est un camp.

Un camp est une vraie équipe du tableau des scores du jeu lui-même : `/team list` la voit, elle garde ses membres à travers une sauvegarde et un rechargement, et un client sans ce mod affiche les couleurs et les noms exactement comme pour n'importe quelle équipe vanilla. L'appartenance se fait par nom : tout ce qui a un nom ou un UUID peut donc être dans un camp, qu'il s'agisse d'un joueur, d'un zombie, d'un villageois, d'un porte-armure.

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

### Le camp

*équipes*

| Réglage       | Type    | Défaut                | Ce qu'il fait                                                                                                                                                                                                                                                  |
| ------------- | ------- | --------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`        | texte   | le nom du fichier     | Le nom de l'équipe sur le tableau des scores, de 1 à 16 caractères. C'est ce qu'utilisent `/team` et les autres fichiers                                                                                                                                       |
| `displayName` | texte   | le nom                | Ce qui est montré aux joueurs à la place du nom                                                                                                                                                                                                                |
| `color`       | texte   | `white`               | L'une des seize couleurs de texte. Elle teinte le nom au-dessus de la tête et sert de clé aux emplacements de barre latérale par équipe                                                                                                                        |
| `prefix`      | texte   | vide                  | Placé devant le nom d'un membre, après la couleur                                                                                                                                                                                                              |
| `suffix`      | texte   | vide                  | Placé après le nom d'un membre                                                                                                                                                                                                                                 |
| `scoreboard`  | boolean | `true`                | Si le camp existe comme équipe sur le tableau des scores du jeu. Désactivé, il n'aligne aucune équipe : ses mobs portent à la place la couleur du camp dans leur nom, rien ne les empêche de se battre entre eux, et aucun point ne lui est attribué, puisque le score va à l'équipe |

Un camp n'est aligné que là où un pack en demande un : sans dossier `teams` nulle part, le mod n'ajoute aucune équipe, n'écoute rien et ne propose pas la commande. Un opérateur de serveur qui modifie un fichier peut lancer `/rdplserver reload` pour aligner le changement dans le monde en cours sans redémarrer.

### Combat et visibilité

*équipes*

| Réglage                 | Type    | Défaut         | Ce qu'il fait                                                                                                                                                                      |
| ----------------------- | ------- | -------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `friendlyFire`          | boolean | `false`        | Si les membres peuvent se blesser entre eux. C'est aussi le défaut de `mobFriendlyFire`                                                                                            |
| `mobFriendlyFire`       | boolean | `friendlyFire` | Si les mobs d'un camp peuvent blesser leur propre camp avec des explosions et du TNT lancé, ce que le jeu seul n'empêche jamais. Désactivé épargne le camp ; activé laisse les choses comme le jeu les a |
| `seeFriendlyInvisibles` | boolean | `true`         | Si les membres se voient entre eux lorsqu'ils sont invisibles                                                                                                                      |
| `nameTags`              | texte   | `always`       | `always`, `never`, `hideForOtherTeams` ou `hideForOwnTeam`, quelle que soit la casse                                                                                               |
| `deathMessages`         | texte   | `always`       | Les mêmes quatre mots, pour savoir qui est informé quand un membre meurt                                                                                                           |
| `collision`             | texte   | `always`       | `always`, `never`, `pushOtherTeams` ou `pushOwnTeam`, quelle que soit la casse                                                                                                    |

### Qui rejoint

*équipes*

| Réglage     | Type    | Défaut  | Ce qu'il fait                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| ----------- | ------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `entities`  | liste   | vide    | Ids d'entités dont chaque apparition rejoint ce camp, comme `minecraft:zombie` ou l'une des vôtres                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `players`   | liste   | vide    | Noms de joueurs qui rejoignent ce camp à leur connexion                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `spawnBox`  | liste   | aucun   | Six nombres entiers, x y z à x y z. Tout ce qui apparaît à l'intérieur rejoint le camp, et les coins peuvent être donnés dans un sens ou dans l'autre                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `joinable`  | boolean | `true`  | Si un joueur peut rejoindre avec `/rdplserver team join`. Mettez-le à false pour un camp réservé aux mobs                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `balance`   | boolean | `false` | Si `/rdplserver team join` sans nom peut placer un joueur ici. Parmi les camps qui l'autorisent, celui qui compte le moins de joueurs est choisi                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `picks`     | nombre  | `0`     | Combien de membres ce camp tire au hasard. À chaque ouverture de manche, le camp laisse son dernier tirage retourner là où il se tenait et tire à nouveau parmi tout ce que nomme `picksFrom` ; entre deux tirages, une connexion ou une apparition issue de ce vivier remplit aussitôt une place vide. Un joueur parmi tous, dans un camp à lui seul, voilà à quoi cela sert                                                                                                                                                                                                                                                                                                                                                                                |
| `picksFrom` | liste   | vide    | De quoi le tirage est fait : `players` pour tous les joueurs en ligne, et des ids d'entités pour chaque mob vivant de cette sorte                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `standIn`   | objet   | aucun   | Un mob qui tient le camp tant qu'aucun joueur n'y est : `{ "entity": "mypack:herobrine", "at": "23,31,0" }` maintient en vie une entité de ce type à cet endroit de l'overworld, l'invoquant quand elle manque, et la retire dès qu'un joueur rejoint le camp : une partie se joue ainsi contre l'IA jusqu'à ce qu'un joueur prenne le rôle. Vérifié toutes les cinq secondes ; l'endroit doit être en terrain chargé. Dans un jeu avec hall d'attente (`opens.by: leader`), un remplaçant n'est invoqué que pendant que le hall attend et à l'ouverture de la manche : celui qui tombe reste donc disparu pendant le reste de la manche et sa fin, jusqu'à ce que tout le monde soit de retour dans le hall ; sans hall, un remplaçant tombé n'est pas remplacé pendant qu'une manche qui se termine sur `ends.lastStanding` se déroule |

Trois façons de rejoindre, et un camp peut toutes les utiliser. `entities` nomme des ids d'entités, et tout ce qui est de ce type rejoint à son apparition, ce qui permet à un pack de donner des camps aux mobs sans toucher aux mobs. `spawnBox` revendique un coin du monde, et tout ce qui apparaît à l'intérieur rejoint, ce qui convient à une arène où les deux camps utilisent le même mob. `players` nomme directement des joueurs. Au-delà, un joueur peut rejoindre avec `/rdplserver team join <name>` sauf si le camp met `joinable` à false, et partir avec `/rdplserver team leave`.

### Kit de départ et point d'apparition

*équipes*

| Réglage | Type  | Défaut | Ce qu'il fait                                                                                                                                                                                                                                                                                                              |
| ------- | ----- | ------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `gives` | liste | vide   | Objets placés dans l'inventaire d'un joueur quand il rejoint le camp, un nom d'objet pour un seul ou `{ "item", "count", "unbreakable" }` pour plusieurs, ou pour un qui ne s'use jamais, dans n'importe quel emplacement libre et jetés à ses pieds s'il n'y en a pas. Redistribués après une réinitialisation qui vide les inventaires (`resetClearsInventory`) |
| `spawn` | texte | aucun  | `x,y,z` dans l'overworld où les joueurs du camp sont placés à l'ouverture d'une manche, pour que chaque camp démarre sur son propre terrain ; sans lui, ils restent là où la réinitialisation ou le hall les a laissés                                                                                                      |

### Le chef

*équipes*

| Réglage    | Type  | Défaut                             | Ce qu'il fait                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ---------- | ----- | ---------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `lead`     | texte | `none`                             | Comment le chef du camp est choisi : `none` ; `first` pour celui qui a rejoint le camp le plus tôt parmi ceux en ligne, de sorte que le rôle descend l'ordre d'arrivée quand l'un est absent et revient avec lui ; ils sont prévenus à leur arrivée, après l'introduction et toute retenue, et de nouveau quand il leur échoit ; `topScore` pour celui qui est le plus haut sur l'objectif que nomme `leadOn` ; `appointed` pour le joueur que nomme `leadIs` ; `vote` pour celui pour qui les membres votent ; ou `claim` pour celui qui le réclame le premier. Un chef est une étiquette et une couleur, rien de plus : il n'accorde aucun pouvoir, de sorte qu'un chef qui se déconnecte ne casse rien |
| `leadOn`   | texte | vide                               | Avec `topScore`, l'objectif selon lequel les membres sont classés. Il est recalculé à chaque lecture, de sorte qu'il suit le score                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `leadIs`   | texte | vide                               | Avec `appointed`, le joueur qui dirige                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `leadSays` | texte | `You are the current round leader` | Dit à un joueur quand le rôle de chef lui échoit : à son arrivée dans un camp qu'il dirige, quand il le réclame, ou quand un chef `first` lui passe le relais, auquel cas il indique qui est parti. `{side}` est le nom affiché du camp ; vide, rien n'est dit                                                                                                                                                                                                                                                                                                                                                                                                      |
| `leadRuns` | texte | vide                               | Une fonction, `namespace:path`, exécutée une fois à chaque passage du rôle de chef à un joueur : le premier chef, et chaque passation ensuite. Elle s'exécute en tant que chef, à sa position, avec la permission qu'a une fonction récompensant un advancement, de sorte que `@s` est le chef. Vérifié chaque seconde ; pour un chef hors ligne, elle s'exécute à sa prochaine connexion. Un redémarrage décide du chef à nouveau                                                                                                                                                                                                                                      |

## Score

*modes de jeu*

`<namespace>/scoring/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers s'empilent. Chaque fichier est un objectif.

Un objectif est un vrai objectif du tableau des scores du jeu lui-même : `/scoreboard players list` le lit et il garde ses scores à travers une sauvegarde. `criterion` est ce que le jeu compte de lui-même : `dummy` pour un score que seul ce pack fait bouger, ou `deathCount`, `playerKillCount`, `totalKillCount`, `health`, `air`, `armor`, `food`, `level`, `xp`, `trigger`, ou toute statistique écrite comme `/scoreboard` l'accepte, telle que `minecraft.custom:minecraft.jump`. Une statistique de la 1.12.2 comme `stat.jump` est lue comme celle qu'elle est devenue.

```json
{
  "name": "kaboom",
  "displayName": "Kills",
  "criterion": "dummy",
  "display": "sidebar",
  "teamTotals": true,
  "tiebreak": true,
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

### L'objectif

*score*

| Réglage       | Type    | Défaut                     | Ce qu'il fait                                                                                                                                                  |
| ------------- | ------- | -------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`        | texte   | le nom du fichier          | Le nom de l'objectif sur le tableau des scores, de 1 à 16 caractères                                                                                           |
| `displayName` | texte   | le nom                     | Ce qui est montré aux joueurs à la place du nom                                                                                                                |
| `criterion`   | texte   | `dummy`                    | Ce que le jeu compte de lui-même. Un critère inconnu est refusé avec une ligne qui le dit                                                                      |
| `display`     | texte   | vide                       | `sidebar`, `list`, `belowName` (`below_name` est accepté aussi) ou `sidebar.team.<color>`. Vide, il n'apparaît nulle part ; il n'y a pas d'écran de tableau des scores à ouvrir |
| `render`      | texte   | celui du critère           | `integer` ou `hearts`                                                                                                                                          |
| `teamTotals`  | boolean | `true`                     | Les points vont sur une ligne nommée d'après l'équipe du membre                                                                                                |
| `individuals` | boolean | `false`                    | Les points vont aussi sur une ligne propre au membre                                                                                                           |
| `carries`     | boolean | `false`                    | L'objectif survit à une réinitialisation de la carte au lieu d'être effacé avec elle. Un décompte de victoires de manches en est un                            |
| `awardsTo`    | texte   | vide                       | Un autre objectif auquel celui-ci donne un point à sa fin, au camp qui menait. Un classement par niveaux ne distribue rien                                    |
| `tiebreak` | booléen | `false` | Une manche qui finit à égalité en tête tire au sort l'un des camps à égalité avec le hasard du monde, consigne le tirage et l'attribue comme d'habitude |

### Points

*score*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `points.kill` | objet | vide | Id d'entité vers points, crédités au camp du tueur. `minecraft:player` donne des points pour un joueur tué |
| `points.death` | entier | `0` | Points à chaque mort d'un membre, quelle qu'en soit la cause. Peut être négatif |
| `points.ownKill` | entier | `0` | Points pour la mort d'un membre du camp du tueur, à la place de la valeur de `kill`. 0 ne marque rien ; un nombre négatif est une pénalité |

`points` est ce que ce mod ajoute à ce que le jeu compte, injecté dans le même objectif pour que `/scoreboard` le lise toujours. `kill` vaut tant de points par id d'entité tuée, crédités au camp du tueur ; `death` vaut tant de points chaque fois qu'un membre d'un camp meurt, et peut être négatif. Avec `teamTotals`, les points vont sur une ligne portant le nom de l'équipe, ce qui permet à la barre latérale d'afficher quatre camps plutôt qu'une ligne par mob. `individuals` ajoute en plus une ligne par membre ; il est désactivé par défaut, car une ligne par UUID de mob n'est que du bruit.

### Comment une manche se termine

*score*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `ends.atScore` | entier | `0` | La partie se termine dès qu'un camp atteint ce score. 0 ne termine jamais sur le score |
| `ends.afterMinutes` | entier | `0` | La partie se termine après ce nombre de minutes. 0 ne termine jamais sur la durée |
| `ends.afterRounds` | entier | `0` | Pour un objectif qu'un autre `awardsTo` : la partie se termine quand ce nombre de manches a été attribué au total, quels qu'en soient les vainqueurs. 0 ne termine jamais sur le nombre de manches |
| `ends.lastStanding` | booléen | `false` | La manche se termine quand il ne reste qu'un seul camp debout. Les camps en lice sont ceux qui comptent un joueur ou un mob vivant à l'ouverture de la manche, deux au minimum ; un joueur qui meurt est éliminé et passe en spectateur jusqu'à la fin de la manche, et un camp dont tous les joueurs sont éliminés ou partis et dont tous les mobs sont morts est tombé. Le camp resté debout remporte la manche, et `awardsTo` l'enregistre pour ce camp quel que soit le score. Avec `resets` et `opens.by: leader`, la partie retourne ensuite au hall d'attente. Le `standIn` d'un camp n'est pas invoqué de nouveau pendant une telle manche |
| `ends.outSays` | texte | `You are out until the round ends` | Ce que l'on dit à un joueur éliminé. Vide : ne dit rien |
| `ends.locksTeams` | booléen | `true` | Rejoindre un camp pendant une manche en cours attend la fin de celle-ci, pour que personne ne s'invite dans une manche déjà comptabilisée |

`ends` termine la partie, soit dès qu'un camp atteint `atScore`, soit une fois `afterMinutes` écoulées. Le classement est alors affiché, établi par le jeu lui-même : dans le chat, ou sous forme de carte si `results` en demande une. Un joueur sans ce mod reçoit le même classement en lignes de chat, afin que personne ne reste sans résultat. Avec `resets`, cette fin est celle d'une manche : le classement reste affiché pendant `intermissionSeconds` tandis qu'un compte à rebours défile dans la barre d'action, la carte est réinitialisée jusqu'à l'accueil, puis la manche suivante s'ouvre après un décompte de cinq secondes. `awardsTo` attribue la manche au camp qui menait, sur un objectif que `carries` reporte d'une réinitialisation à l'autre. Un objectif reporté peut se terminer de lui-même -- `atScore` pour un meilleur des N, `afterRounds` pour un nombre fixe de manches -- et son classement est effacé à la réinitialisation suivante, de sorte qu'une nouvelle partie s'ouvre.

### Entre les manches

*score*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `ends.resets` | booléen | `false` | La fin de la manche réinitialise la carte, comme le décrivent `resetSays` et les autres réglages de réinitialisation sous [Modèles de monde](#modèles-de-monde), puis une nouvelle manche s'ouvre |
| `ends.intermissionSeconds` | entier | `10` | Durée pendant laquelle le classement reste affiché entre la fin et la réinitialisation |
| `ends.intermissionSays` | texte | `Round cooldown {seconds}` | Affiché dans la barre d'action chaque seconde de l'intermède après la fin d'une manche, `{seconds}` décomptant jusqu'à la réinitialisation. Vide : n'affiche rien |
| `ends.startsSays` | texte | `Round starting in {seconds}` | Affiché dans la barre d'action pendant le décompte de cinq secondes qui ouvre la manche suivante après la réinitialisation, `{seconds}` décomptant. Vide : n'affiche rien |

### Le hall d'attente

*score*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `opens.by` | texte | `auto` | `auto` ouvre la manche suivante d'elle-même, cinq secondes après la réinitialisation. `leader` garde plutôt la partie dans un hall d'attente : après la réinitialisation, et au premier chargement du monde, rien n'est comptabilisé et aucune horloge ne tourne, les camps peuvent être rejoints et quittés librement, et la manche ne s'ouvre que lorsque le chef d'un camp, ou un opérateur, exécute `/rdplserver round start`, et pas tant que quelqu'un lit encore l'introduction du monde ; ensuite le décompte de cinq secondes s'écoule, les tirages sont faits, et chaque camp est placé à son `spawn`. Tant que le monde attend, jusqu'à la fin du décompte de cinq secondes, les joueurs restent où ils se trouvent et ne peuvent rien casser, poser, utiliser, frapper ni lâcher, ne subissent aucun dégât, et la ligne d'attente leur est montrée quand ils essaient ; tout le reste du vivant reste immobile : ni IA, ni déplacement. Les commandes fonctionnent toujours, on peut donc rejoindre des camps et lancer la manche |
| `opens.says` | texte | `Waiting for {leader} to start the round` | Affiché au milieu de l'écran, comme l'accueil, à chaque joueur qui n'est pas chef : à son arrivée dans le hall d'attente après l'introduction, une fois l'accueil montré ; à la réouverture du hall après une manche ; à chaque changement, quand un chef arrive ou part ; et quand il tente quelque chose que le hall refuse. `{leader}` désigne les chefs de tous les camps, ou `a leader` tant que personne ne dirige. Vide : n'affiche rien |
| `opens.leaderSays` | texte | `Type /rdpl round start` | Affiché de la même manière et aux mêmes moments au joueur qui dirige un camp, à la place de `opens.says`. Vide : n'affiche rien |
| `opens.lobby` | texte | aucun | `x,y,z` dans l'Overworld, ou `dimension:x,y,z` dans un autre monde, comme `minecraft:the_nether:0,64,0`, là où tout le monde attend tant que le hall tient : chaque joueur, et chaque mob vivant d'un camp, est placé sur un anneau autour de ce point, tous tournés vers son centre, si bien qu'ils se regardent fixement. Chacun reçoit un arc aussi large que lui plus deux blocs, pour qu'aucun n'en chevauche un autre, et l'anneau s'agrandit à mesure que d'autres arrivent ; il est redessiné chaque fois que quelqu'un le rejoint ou le quitte. La hauteur est celle du sol sur lequel ils se tiennent, cherché à trois blocs près dans les deux sens. Joueurs et mobs passent directement dans ce monde et en reviennent, sans qu'aucun portail soit construit. À l'ouverture de la manche, les joueurs vont au `spawn` de leur camp, et un mob encore debout est remis là où il était, dans son propre monde |
| `opens.lobbyJoins` | booléen | `false` | Place un joueur qui se connecte en cours de manche dans le hall d'attente en spectateur jusqu'à la fin de la manche, au lieu de l'endroit où il s'était déconnecté. Nécessite `opens.lobby` |
| `opens.joinsSays` | texte | `Round is in progress, you can join after it ends` | Ce qu'on leur dit. Vide : ne dit rien |

### Réinitialiser une manche

*score*

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

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `reset.lead` | texte | `none` | Ce que fait `/rdplserver round reset` pour le chef d'un camp pendant une manche. `now` termine la manche aussitôt et réinitialise la carte ; `vote` lance un vote à la place ; `none` ne donne au chef aucun pouvoir propre, il lance donc un vote comme n'importe quel autre joueur là où `players` le permet. Un opérateur réinitialise toujours aussitôt |
| `reset.players` | texte | `none` | `vote` permet à un joueur de n'importe quel camp de lancer un vote avec `/rdplserver round reset`. `none` laisse la réinitialisation au chef |
| `reset.teams` | liste | vide | Les camps dont les joueurs peuvent lancer un vote. Vide : tous les camps |
| `reset.passPercent` | entier | `51` | La part des votants, de 1 à 100, qui doit voter oui pour que la manche soit réinitialisée. `51` est plus de la moitié, `100` est tout le monde |
| `reset.voteSeconds` | entier | `30` | Durée d'un vote, cinq secondes au minimum. Il se clôt plus tôt dès que son issue est certaine |
| `reset.cooldownSeconds` | entier | `60` | Délai après un vote échoué avant qu'un autre puisse être lancé. Un chef avec `now` n'est pas freiné par ce délai |
| `reset.leadSays` | texte | `{player} reset the round` | Dit à tout le monde quand la manche est réinitialisée aussitôt, `{player}` étant celui qui l'a réinitialisée. Vide : ne dit rien |
| `reset.voteSays` | texte | `{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds` | Dit à tout le monde quand un vote est lancé, `{player}` étant celui qui l'a lancé. Vide : ne dit rien |
| `reset.tallySays` | texte | `Reset the round? {yes} yes, {no} no, {seconds}` | Affiché dans la barre d'action chaque seconde d'un vote, `{seconds}` décomptant. Vide : n'affiche rien |
| `reset.passSays` | texte | `The vote passed, so the round is reset` | Dit à tout le monde quand un vote est adopté. Vide : ne dit rien |
| `reset.failSays` | texte | `The vote failed, so the round goes on` | Dit à tout le monde quand un vote échoue. Vide : ne dit rien |

Une réinitialisation interrompt la manche là où elle en est. Le classement est affiché sous `The round was reset`, personne ne remporte la manche, l'intermède décompte, et la carte est réinitialisée comme si la manche s'était terminée avec `ends.resets`, retour au hall d'attente là où `opens.by` vaut `leader`. Cela fonctionne que la manche doive ou non finir d'elle-même, mais pas dans le hall d'attente, ni pendant le décompte qui ouvre une manche, ni une fois la manche terminée et sa réinitialisation en route ; un vote encore en cours à ce moment-là est abandonné.

Chaque joueur en ligne appartenant à un camp vote, quel que soit son camp, avec `/rdplserver round vote yes` ou `no`, et peut changer son vote tant qu'il dure. Celui qui lance le vote a voté oui, et un joueur qui n'a pas voté à l'expiration du temps compte comme non. Dans un pack avec des camps, un joueur sans camp ne lance ni ne vote ; dans un pack sans camps, tous les joueurs en ligne le font. Le premier fichier de score dont le `reset` permet à quelqu'un de réinitialiser est celui qui est utilisé.

### Résultats

*score*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `results.card` | booléen | `false` | Affiche le classement sous forme de carte plutôt que dans le chat |
| `results.title` | texte | le nom et `results` | Le titre de la carte |
| `results.icon` | texte | vide | Un objet dessiné sur la carte, p. ex. `minecraft:tnt` |
| `results.image` | texte | vide | Une image dessinée sur la carte à la place d'un objet |
| `results.background` | texte | une ardoise sombre | La couleur de fond de la carte |
| `results.seconds` | entier | `8` | Durée d'affichage de la carte, une seconde au minimum |

## Raids

*modes de jeu*

`<namespace>/raids/*.json`

Le nom du fichier est libre, seul le dossier est lu, et plusieurs fichiers se cumulent. Chaque fichier est un raid.

Un raid est le type que le jeu connaît depuis la 1.14, mené sur l'un des villages du jeu. Il commence quand un joueur sous l'effet `omen` se trouve dans un village : l'effet est retiré, et une barre de boss apparaît pour chaque joueur à moins de `reach` du centre du village. Après `waveDelay` ticks, la première vague arrive sur un anneau autour du village et marche vers le centre, attaquant en chemin joueurs, villageois et golems de fer. Les pillards ne se blessent ni ne se ciblent jamais entre eux : une flèche perdue ou un coup entre deux d'entre eux ne fait rien. La barre montre la santé qu'il reste à la vague, et compte les pillards une fois qu'il n'en reste que deux ou moins. Quand une vague a disparu, la suivante attend `waveDelay` ticks. Quand la dernière vague a disparu et que rien n'est revenu pendant deux secondes, le raid est gagné ; quand tous les villageois sont morts ou que le village lui-même a disparu après l'arrivée d'une vague, il est perdu. Dans les deux cas la barre l'annonce pendant trente secondes, et la fonction correspondante s'exécute en tant que chaque joueur à portée.

Un raid en cours est sauvegardé avec le monde, et ses pillards reprennent leur marche après un rechargement. Il s'arrête sans conclusion en mode paisible, après `timeout` ticks, ou quand aucun emplacement autour du village ne peut accueillir une vague. Un village est l'endroit où le jeu garde ses points de village, les lits, postes de travail et cloches que les villageois revendiquent : son centre est le milieu de ces points, il s'étend sur au moins 32 blocs autour, et ses villageois sont ceux situés dans cette portée et à quatre blocs de la hauteur du centre. Le `minecraft:bad_omen` du jeu déclenche d'abord le raid du jeu, c'est pourquoi un raid nomme un effet propre à son pack.

Tant qu'une vague est sur le village, ses villageois courent chez eux et y restent, comme lorsque la cloche du jeu sonne. Les pillards détruisent les portes en bois sur leur chemin pour les atteindre, douze secondes par porte, en difficulté normale et difficile tant que `mobGriefing` est activé ; les portes en fer résistent. Un bloc de type `bell` est une cloche de village où qu'il se trouve, et sonne comme le décrit [Cloches](#cloches) ; le `bell` du raid désigne tout autre bloc à faire sonner comme tel. Chaque cloche du village sonne à l'arrivée d'une vague, et un bloc désigné sonne aussi quand un joueur l'utilise : les villageois à moins de 48 blocs se cachent pendant quinze secondes et les pillards à moins de 48 blocs luisent pendant trois. `minecraft:bell` se trouve déjà dans les villages du jeu et peut être désignée, et elle continue de sonner à la manière du jeu aussi ; le bloc de cloche propre à un pack est placé dans le village par une structure NBT, comme parcelle ou comme pièce centrale de la place.

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

### Le raid

*raids*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `omen` | nom d'effet | aucun, obligatoire | L'effet qui déclenche le raid quand son porteur se trouve dans un village. N'importe quel effet enregistré convient, y compris une potion propre au pack |
| `name` | texte | `Raid` | Le titre de la barre de boss |
| `color` | texte | `red` | La couleur de la barre : `pink`, `blue`, `red`, `green`, `yellow`, `purple` ou `white` |
| `waves` | liste de vagues | aucun, obligatoire | Chaque vague est une liste de groupes, et les vagues arrivent dans l'ordre |
| `waveDelay` | entier | `300` | Ticks avant la première vague, et entre la fin d'une vague et la suivante |
| `spawnDistance` | entier | `32` | À quelle distance du centre du village arrive une vague. Les premiers essais se font au double de cette distance, puis à cette distance, puis à l'intérieur du village |
| `reach` | entier | `96` | Les joueurs à moins de ce nombre de blocs du centre voient la barre, et la fonction de fin s'exécute en tant qu'eux. Un pillard qui s'écarte de seize blocs au-delà quitte le raid |
| `timeout` | entier | `48000` | Ticks après lesquels un raid inachevé s'arrête sans conclusion. `0` ne l'arrête jamais |
| `sound` | nom de son | aucun | Joué à chaque joueur à portée, depuis le côté d'où vient la vague, à l'arrivée de chaque vague |
| `wins` | fonction | aucune | S'exécute en tant que chaque joueur à portée quand le raid est gagné |
| `loses` | fonction | aucune | S'exécute en tant que chaque joueur à portée quand le raid est perdu |
| `bell` | nom de bloc ou liste | aucun | Autres blocs qui sonnent comme une cloche, quand un joueur les utilise et à chaque arrivée de vague. Un bloc de type `bell` sonne sans être nommé |

### Un groupe

*raids*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `entity` | nom d'entité | aucun, obligatoire | Ce qui arrive. Une variante d'entité garde tout son comportement propre et gagne la marche |
| `count` | entier ou `{ "min", "max" }` | `1` | Combien arrivent |

---

## Cartes

*modes de jeu*

`<namespace>/cards/*.json`

Le nom du fichier est libre, seul le dossier est lu, et plusieurs fichiers se cumulent. Chaque fichier est une règle, et son id est `<namespace>:<nom du fichier>`. Une règle attend un déclencheur, vérifie son `when`, et montre une carte à son public ; elle peut aussi exécuter une fonction. Rien n'a besoin d'être installé côté client : un joueur sans le mod reçoit une carte de coin sous forme de lignes de chat et une carte centrale sous forme de titre.

Chaque message que ce mod dit lui-même est une règle intégrée, listée plus bas. Un pack en modifie une en écrivant un fichier portant cet id, `rdpl/cards/<nom>.json`, qui n'a besoin d'aucun déclencheur : ce qu'il omet reste comme aujourd'hui, et `{text}` représente le message que le mod aurait dit. Un pack qui n'en écrit aucune voit chaque message comme avant.

```json
{
  "trigger": "biome_enter",
  "biomes": ["minecraft:desert", "#minecraft:is_badlands"],
  "title": "The Dry Lands",
  "lines": ["Day {day}, {player}.", "Water is scarce from here on."],
  "style": "center",
  "image": "mypack:textures/gui/desert_card.png",
  "color": "3A2A10",
  "ticks": 120,
  "when": { "timeFrom": 0, "timeTo": 12000 },
  "repeat": "once_per_player"
}
```

```json
{
  "lines": ["{text}", "Speak to the gatekeeper for more."],
  "icon": "minecraft:ender_eye",
  "cooldown": 30
}
```

Le deuxième fichier, enregistré sous `rdpl/cards/gate_blocked.json`, transforme la ligne rouge de la barre d'action qu'affiche un portail fermé en une carte avec une icône et une seconde ligne, et ne la montre qu'une fois toutes les trente secondes au plus.

```json
{
  "trigger": "first_join",
  "title": "Ruby World",
  "lines": ["Welcome, {player}."],
  "style": "center",
  "background": false,
  "font": "mypack:runes"
}
```

Le troisième accueille un joueur à sa première connexion avec une carte centrale sans panneau derrière elle, seulement son texte et l'ombre du texte, dessinés dans la police propre au pack.

### Déclencheurs

*cartes*

| Déclencheur | Requiert | Se déclenche quand |
| --- | --- | --- |
| `command` | rien | `/rdplserver card <rule> [players]` est exécutée. La commande ignore `when`, `repeat` et `cooldown`, et exécute toujours `runs`. Toute règle peut être montrée ainsi, quel que soit son déclencheur |
| `first_join` | rien | Un joueur rejoint le monde pour la première fois |
| `dimension_enter` | `dimension` | Un joueur arrive dans cette dimension |
| `biome_enter` | `biomes` | Un joueur entre dans l'un de ces biomes en venant d'ailleurs |
| `structure_enter` | `structures` | Un joueur entre dans l'une de ces structures depuis l'extérieur |
| `advancement` | `advancement` | Un joueur obtient ce progrès |
| `time_of_day` | `time` | L'horloge du jour passe ce tick, de `0` à `23999`, tant que des joueurs sont dans la dimension. Une horloge réglée par une commande ou un lit ne compte pas |
| `day` | rien, ou `day` | Un nouveau jour commence dans la dimension ; avec `day`, seulement ce jour-là |
| `craft` | `item` | Un joueur fabrique cet objet |
| `pickup` | `item` | Un joueur ramasse cet objet |
| `kill` | `entity` | Un joueur tue cette entité, ou la `count`-ième d'entre elles |
| `respawn` | rien | Un joueur réapparaît après être mort |
| `death` | rien | Un joueur meurt |
| `y_level` | `below` ou `above` | Un joueur passe sous ou au-dessus de cette hauteur |
| `play_time` | `minutes` | Le temps d'un joueur dans le monde atteint ce nombre de minutes, compté depuis la fermeture de l'introduction du monde, ou depuis la connexion quand aucune introduction ne lui est montrée |
| `score` | `objective` | Le score d'un joueur dans cet objectif atteint `score` |

Biome, structure, hauteur, temps de jeu et score sont vérifiés une fois par seconde pour chaque joueur, et se déclenchent au passage de l'extérieur à l'intérieur, jamais à la première vérification après une connexion. Une règle `time_of_day` ou `day` dont le public n'est pas `player` se déclenche une fois pour la dimension plutôt qu'une fois pour chaque joueur qui s'y trouve.

Une carte qui se déclenche alors qu'un joueur a encore l'introduction du monde ouverte attend et s'affiche à la fermeture de l'introduction, quel que soit son déclencheur, `command` compris. Elle est abandonnée si le joueur part avant.

### Réglages des déclencheurs

*cartes*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `trigger` | texte | aucun, obligatoire | L'un des déclencheurs ci-dessus. Une règle intégrée n'en prend aucun |
| `dimension` | texte | aucun | Un id de dimension comme `minecraft:the_nether` ; un id sans namespace est lu comme `minecraft:`. Pour `dimension_enter`, c'est celle où l'on entre ; pour tout autre déclencheur, elle limite la règle aux joueurs présents dans cette dimension |
| `biomes` | liste | aucun | Des ids de biomes comme `minecraft:desert`, ou `#tag` pour un tag de biome comme `#minecraft:is_ocean` |
| `structures` | liste | aucun | Des ids de structures comme `minecraft:village_plains`, ou `#tag` pour un tag de structure comme `#minecraft:village`, qui comptent tant que le joueur se trouve dans l'un de leurs éléments ; ou le nom d'une structure qu'un pack place via `structures`, qui compte dans le rayon `radius` autour de son emplacement |
| `radius` | entier | `32` | À quelle distance on est considéré à l'intérieur d'une structure propre à un pack |
| `advancement` | texte | aucun | L'id du progrès |
| `item` | texte | aucun | L'objet, écrit comme ailleurs dans un pack, par exemple `minecraft:diamond_sword` |
| `entity` | texte | aucun | L'id de l'entité, par exemple `minecraft:zombie` |
| `count` | entier | `1` | Pour `kill` : combien de victimes il faut. Le compte repart de zéro après le déclenchement de la règle |
| `below`, `above` | entier | aucun | Pour `y_level` : la hauteur sous laquelle ou au-dessus de laquelle passer |
| `time` | entier | `0` | Pour `time_of_day` : le tick du jour |
| `day` | entier | aucun | Pour `day` : l'unique jour où se déclencher. Sans lui, chaque jour |
| `minutes` | entier | aucun | Pour `play_time` |
| `objective`, `score` | texte, entier | aucun, `1` | Pour `score` : l'objectif et la valeur à atteindre |
| `requires` | liste d'ids de mod ou de namespaces de pack | aucun | Le fichier est ignoré sauf si tous sont présents |

### Quand

*cartes*

`when` regroupe des conditions qui doivent toutes être vraies au moment où le déclencheur se produit.

| Réglage | Type | Ce qui est vérifié |
| --- | --- | --- |
| `biomes` | liste | Le joueur se trouve dans l'un de ces biomes, écrits comme pour le déclencheur |
| `timeFrom`, `timeTo` | entier | L'horloge du jour est dans cette fenêtre, qui peut passer minuit, comme `13000` à `1000` |
| `dayAtLeast` | entier | Le numéro du jour est au moins celui-ci |
| `advancement` | texte | Le joueur a ce progrès |
| `gameMode` | texte | Le joueur est dans ce mode de jeu : `survival`, `creative`, `adventure` ou `spectator` |
| `team` | texte | Le joueur est dans cette équipe du tableau des scores |
| `objective`, `scoreAtLeast` | texte, entier | Le score du joueur dans l'objectif est au moins celui-ci |

### La carte

*cartes*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `title` | texte | aucun | La première ligne, dessinée plus grande sur une carte centrale |
| `lines` | liste | aucun | Jusqu'à seize lignes. Une règle a besoin d'un titre ou de lignes, sauf si elle est intégrée. `{player}`, `{dim}`, `{biome}` et `{day}` sont remplacés ; `{text}` est le message intégré, et sur une ligne à lui seul il donne toutes ses lignes |
| `style` | texte | `corner` | `corner` est la carte en bas à droite qu'affiche `saysCard` ; `center` est une carte au milieu de l'écran ; `chat` est du chat ; `bar` est la barre d'action |
| `icon` | texte | `saysIcon` | Un objet dessiné sur une carte de coin. Vide : n'en dessine aucun |
| `color` | texte | `saysColor` | La couleur de fond de la carte en hexadécimal |
| `image` | texte | `saysImage` | Un PNG des assets client du pack, étiré sur la carte comme fond |
| `background` | booléen | `saysBackground` | `false` supprime le panneau, sa bordure et la bande de couleur ; le texte garde son ombre, et une `image` se dessine toujours |
| `font` | texte | `saysFont` | La police dans laquelle le texte de la carte est dessiné, sous la forme `namespace:name`. Vide : utilise la police RDPL |
| `ticks` | entier | `160` | Durée de présence de la carte, fondu compris |
| `audience` | texte | `player` | Qui la voit : `player`, `everyone`, `dimension` (tous les joueurs de la dimension du joueur) ou `team` (l'équipe du tableau des scores du joueur) |
| `repeat` | texte | `always` | `always`, `once_per_player`, `once_per_world` ou `once_per_session` (de nouveau après la reconnexion du joueur) |
| `cooldown` | entier | `0` | Secondes avant que la règle se déclenche de nouveau pour le même joueur |
| `runs` | texte | aucun | Une fonction exécutée en tant que le joueur quand la règle se déclenche |

Une carte de coin passe dans le chat quand `saysCard` est désactivé. Ce qu'un joueur a vu est conservé avec le joueur, et survit donc à la mort et au changement de dimension ; `once_per_world` est conservé avec le monde.

La police RDPL, `resourcedatapackloader:rdpl`, est celle de tous les textes par défaut : cartes, messages Says, notes d'accueil et d'attente, introduction du monde, ainsi que menus, chat, HUD et infobulles du jeu. Ses variantes gras et italique sont `resourcedatapackloader:rdpl_bold` et `resourcedatapackloader:rdpl_italic`. Les lettres de la table d'enchantement restent celles du jeu.

RDPL fournit ces polices et ces caractères. Le `font` d'une carte, d'une note ou d'une introduction peut nommer une police RDPL par son nom court, ou par son id complet :

| Nom | Ce qu'il dessine |
| --- | --- |
| `rdpl` (ou `resourcedatapackloader:rdpl`) | La police RDPL, avec le cyrillique (U+0400 à U+04FF) et l'alphabet runique (U+16A0 à U+16F8) |
| `rdpl_runic` (ou `resourcedatapackloader:rdpl_runic`) | Un chiffrement runique : les lettres A à Z et a à z sont dessinées en runes, et tout autre caractère est dessiné dans la police RDPL. Un passage en gras est dessiné en `rdpl_runic_bold` et un passage en italique en `rdpl_runic_italic` |
| Runes, U+16A0 à U+16F8 | Écrites comme les caractères runiques eux-mêmes (ᚠ ᚢ ᚦ ᚨ ᚱ ᚲ), dans tout texte que dessine la police RDPL, chat compris ; les passages en gras et en italique gardent leur variante |

Une police de carte est une définition de police dans `assets/<namespace>/font/<name>.json`, au même format que les polices du jeu, et la carte est dimensionnée d'après les largeurs de cette police. Un fournisseur `bitmap` dont le `file` est `<namespace>:font/<name>.png` et dont les `chars` sont les seize lignes de l'`ascii.png` du jeu lit le même PNG que celui utilisé par la version 1.12.2 dans `assets/<namespace>/textures/font/<name>.png`, si bien qu'un seul pack dessine les mêmes lettres sur les trois versions. `minecraft:default` désigne la police du jeu. Une police qu'aucun pack ne possède retombe sur la police du jeu, avec un avertissement dans `rdpl.log`.

Un pack modifie la police RDPL en fournissant son propre `assets/resourcedatapackloader/font/rdpl.json`, ou les PNG sous `assets/resourcedatapackloader/textures/font/` dont elle se sert ; l'un ou l'autre la remplace partout, texte du jeu compris. Le texte du jeu utilise la police RDPL parce que le mod fournit `assets/minecraft/font/default.json` avec la police RDPL en premier et les polices propres au jeu ensuite pour tous les autres caractères. Le `assets/minecraft/font/default.json` d'un pack est lu avant celui du mod, donc une copie de celui de vanilla rend au texte du jeu sa propre police ; le texte propre à RDPL garde la police RDPL sauf si `saysFont` vaut `minecraft:default`.

Les titres et lignes de carte, les messages Says et les notes d'accueil et d'attente acceptent les marques en ligne du tableau sous Introduction au monde, Mise en forme du texte : gras, italique, gras italique, barré, code, liens et passages runiques. Un passage en gras est dessiné dans la variante `_bold` de la police et un passage en italique dans sa variante `_italic` ; pour une police sans cette variante, le passage prend le style gras ou italique du jeu, et la carte est dimensionnée d'après les passages tels que dessinés. Les joueurs sans le mod reçoivent les mêmes marques sous forme de mise en forme du chat, et un passage runique sous forme de ses lettres ordinaires.

### Règles intégrées

*cartes*

| Id | Le message | Origine du texte |
| --- | --- | --- |
| `rdpl:gate_unlocked` | Un portail s'ouvre | `unlockedMessage` dans [Portails](#portails) |
| `rdpl:gate_blocked` | Un portail fermé repousse un joueur, dans la barre d'action | `blockedMessage` dans [Portails](#portails) |
| `rdpl:team_joined` | Un joueur rejoint un camp | le `displayName` du camp |
| `rdpl:team_lead` | Le chef d'un camp échoit à un joueur | `leadSays` dans [Équipes](#équipes) |
| `rdpl:team_picked` | Un joueur est choisi pour un camp | le `displayName` du camp |
| `rdpl:team_round_ended` | La manche est terminée, un joueur est donc déplacé vers un camp | le `displayName` du camp |
| `rdpl:lobby_joins` | Un joueur qui se connecte en cours de manche est envoyé au hall d'attente | `opens.joinsSays` dans [Le hall d'attente](#le-hall-dattente) |
| `rdpl:lobby_note` | La ligne du hall d'attente au milieu de l'écran | `opens.says`, `opens.leaderSays` dans [Le hall d'attente](#le-hall-dattente) |
| `rdpl:scoring_results` | Le classement en fin de manche, pour chaque joueur | `results.card`, `results.title`, `results.icon`, `results.image`, `results.background`, `results.seconds` dans [Résultats](#résultats) |
| `rdpl:scoring_out` | Un joueur éliminé | `ends.outSays` dans [Comment une manche se termine](#comment-une-manche-se-termine) |
| `rdpl:reset_lead` | Le chef réinitialise la manche | `reset.leadSays` dans [Réinitialiser une manche](#réinitialiser-une-manche) |
| `rdpl:reset_vote` | Un vote de réinitialisation est lancé | `reset.voteSays` |
| `rdpl:reset_pass` | Le vote est adopté | `reset.passSays` |
| `rdpl:reset_fail` | Le vote échoue | `reset.failSays` |
| `rdpl:anvil_waits` | Le travail d'une enclume attend un progrès | [Travail à l'enclume](#travail-à-lenclume) |
| `rdpl:threat` | La bande de menace d'un joueur change | `threatSays` |
| `rdpl:prospect` | Chaque ligne que rapporte une trouvaille de prospection | la trouvaille |
| `rdpl:prospect_none` | La prospection n'a rien trouvé | le fichier de langue |
| `rdpl:pregen_ended` | La prégénération se termine ou s'arrête | `pregenFinishedSays`, `pregenStoppedSays` dans [Prégénération](#prégénération) |
| `rdpl:pregen_running` | La ligne de progression que voit un joueur qui se connecte pendant la prégénération | `pregenRunningSays` |

`welcomeSays` n'est pas une règle et garde son logo ; une règle `first_join` ou `dimension_enter` s'y ajoute. Les décomptes et totaux d'une manche dans la barre d'action restent tels que leurs réglages les font.

## Dés et paquets

*modes de jeu*

`<namespace>/dice/*.json`

Le nom du fichier est à votre choix, et plusieurs fichiers s'additionnent. Un fichier nomme des dés dont les faces portent des poids, des paquets de cartes où l'on pioche sans remettre, qui entend un lancer par défaut, et la formulation des résultats. Les lancers se font avec [`/rdplserver game`](#jeux) et avec tout objet qui a [`rolls`](#clés-de-fichier-dobjet).

```json
{
  "audience": "all",
  "dice": {
    "fate": { "plus": 1, "blank": 2, "minus": 1 }
  },
  "decks": {
    "tarot": ["The Fool", "The Magician", "The High Priestess", "The Empress"]
  },
  "says": {
    "coin": "{player} tosses the old coin: {result}"
  }
}
```

| Clé | Type | Par défaut | Ce qu'elle fait |
| --- | --- | --- | --- |
| `audience` | texte | `all` | Qui entend un lancer qui ne nomme pas le sien : `self`, `team`, `all`, `radius <blocs>` ou `silent`. Le premier pack qui la définit l'emporte ; un suivant est consigné |
| `dice` | objet | vide | Nom du dé vers un objet de face et de poids. Une face de poids 2 sort deux fois plus souvent qu'une de poids 1. Les poids sont des entiers à partir de 1 |
| `decks` | objet | vide | Nom du paquet vers sa liste de cartes, ou vers un objet `{ "cards": [...], "reshuffle": false }`. `reshuffle` vaut `true` sauf indication : piocher dans un paquet vide remélange toutes les cartes puis pioche. Avec `false` le paquet reste vide jusqu'à `game deck shuffle` |
| `says` | objet | vide | Clé de formulation vers un texte, à la place de la formulation du mod dans toutes les langues. Les clés et leurs champs sont plus bas |

Un nom de dé ou de paquet appartient au premier pack qui le charge. Un autre pack qui reprend ce nom, ou le nom `coin`, est écarté avec une erreur dans le journal. Un dé du pack se lance avec `game die <name>` et montre sa face ; enregistré comme score, il compte comme la place de la face dans le fichier, à partir de 1.

Un paquet est une pile qui s'épuise. `game deck draw <name>` pioche une carte au hasard dans ce qui reste, et rien ne revient avant que le paquet vide se remélange à la pioche suivante ou que `game deck shuffle <name>` remette toutes les cartes. La pile est enregistrée avec le monde, donc un redémarrage ne la mélange pas.

Chaque lancer utilise le hasard propre au monde et s'écrit dans le journal avec qui l'a fait, ce qui a été lancé et le résultat. `game last` montre les plus récents. Les résultats partent en lignes de chat ordinaires construites sur le serveur, donc un joueur sans le mod les lit aussi. La notation des dés se lit comme le nombre, `d`, les faces et un modificateur facultatif : `3d8-2` fait trois dés à huit faces additionnés, moins 2, et `d20` un dé à vingt faces. Le chat et le journal écrivent le lancer en toutes lettres, comme « Boss lance 3 dés à huit faces, moins 2 : [2, 6, 7] = 13 », et `{dice}` contient cette formulation.

| Clé de formulation | Champs |
| --- | --- |
| `coin`, `pickplayer`, `pickteam` | `{player}`, `{result}` |
| `heads`, `tails`, `lastnone`, `nobody`, `notallowed`, `usage` | aucun |
| `die` | `{player}`, `{dice}`, `{sides}`, `{result}` |
| `packdie` | `{player}`, `{die}`, `{result}` |
| `dice` | `{player}`, `{dice}`, `{rolls}`, `{result}` |
| `advantage`, `disadvantage` | `{player}`, `{dice}`, `{first}`, `{second}`, `{result}` |
| `pickmember` | `{player}`, `{team}`, `{result}` |
| `draw`, `reshuffled` | `{player}`, `{deck}`, `{result}`, `{left}` |
| `shuffle` | `{player}`, `{deck}`, `{left}` |
| `left`, `empty`, `nodeck` | `{deck}`, et `{left}` pour `left` |
| `teamroll` | `{member}`, `{dice}`, `{result}` |
| `teamrollwin` | `{result}`, `{score}` |
| `tiebreak` | `{objective}`, `{sides}`, `{result}` |
| `notie`, `noobjective` | `{objective}` |
| `badsides`, `badroll`, `badaudience`, `nodie`, `noteam` | `{sides}`, `{roll}`, `{audience}`, `{name}`, `{team}` dans l'ordre |

La formulation propre du mod se trouve dans ses fichiers de langue sous `rdpl.game.<key>`, donc un pack de ressources peut aussi la changer langue par langue.

---

# Contrôle

## La couche de contrôle

*contrôle*

Tout ce qui arrête ou modifie la génération est regroupé, et chaque groupe a une clé dans la catégorie `control` de la configuration, avec trois valeurs :

| Valeur | Signification |
| --- | --- |
| `default` | Le pack décide. Les valeurs de la configuration servent de repli |
| `global` | La configuration l'emporte. Les sections de pack sont ignorées |
| `off` | Le groupe est entièrement désactivé et aucun pack ne peut l'activer |

Les groupes sont `ores`, `biomes`, `structures`, `spawning`, `bedrock`, `voidWorld`, `recipes`, `terrain`, `replacements`, `villages`, `entities`, `chunks`, `blastPlaster`, `commands` et `server`.

Les réglages se résolvent selon **section de biome → modèle de monde → configuration**. Le bloc `settings` d'un modèle de monde utilise les mêmes noms de clés que la configuration, si bien qu'un pack les règle comme vous le feriez vous-même :

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

Avec le contrôle d'un groupe sur `default`, ces valeurs l'emportent, sur `global` elles sont ignorées, et sur `off` le groupe entier ne fait rien, quoi que dise n'importe quel pack. Une clé qu'un modèle nomme et que rien ne lit est signalée une fois dans le journal, de même qu'une clé d'une section `biomes` qui n'est pas un réglage de village.

Le fichier de configuration est `config/resourcedatapackloader-common.toml`. Chaque clé ci-dessous y porte le même nom, sous sa catégorie, et une liste s'écrit comme la liste TOML qu'utilise le format de configuration du jeu.

## Ce que fait chaque groupe

*contrôle*

Chaque réglage ci-dessous est lu via son groupe, c'est donc la clé `control` du groupe qui décide si c'est un pack ou la configuration qui a le dernier mot. Un réglage qu'un pack peut définir apparaît dans le bloc `settings` d'un modèle de monde sous le même nom ; un réglage marqué **configuration uniquement** n'est lu que dans la configuration, et un pack qui l'écrit est averti et ignoré. Les valeurs par défaut sont celles de la configuration.

### Minerais

*ce que fait chaque groupe*

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

`control.ores` décide de ce groupe. Blocage de la génération des minerais par mod et par type de minerai.

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `blockOres` | booléen | `false` | Empêche tous les mods, et Minecraft lui-même, de générer des minerais. Seuls les mods de oreWhitelist génèrent encore. Un minerai est une feature placée construite sur la feature de minerai ou de minerai dispersé du jeu, celles pour lesquelles la 1.12.2 levait son événement de minerai, ce qui couvre les minerais de Minecraft et de la plupart des mods, la terre, le gravier et les variétés de pierre. Les entrées de worldgen propres à un pack ne sont jamais bloquées, ni par ce réglage ni par oreTypes |
| `logBlockedOres` | booléen | `true` | Journalise la première fois que chaque mod et chaque type de minerai est refoulé |
| `oreWhitelist` | liste d'ids de mod | `["minecraft"]` | Les mods encore autorisés à générer des minerais tant que `blockOres` est activé |
| `prospectItems` | liste | vide | Objets qui prospectent les entrées de worldgen en forme de filon quand un joueur accroupi casse un bloc avec l'un d'eux, sous la forme objet=entrée\|entrée[,rayon en chunks] ou objet=*[,rayon], p. ex. minecraft:compass=iron_vein\|coal_seam ou mypack:rod=*,12. Le relevé donne le nom du minerai et un point cardinal |
| `prospectItemsAreBlacklist` | booléen | `false` | Activé, la liste de chaque objet désigne les entrées qu'il ne lit pas |
| `prospectDrops` | booléen | `false` | Activé, un bloc cassé en mode prospection lâche quand même son butin et donne de l'expérience. Désactivé, l'échantillon est consommé |
| `prospectSlow` | entier, 1 à 100 | `2` | Combien de fois plus longtemps un joueur accroupi tenant un objet étiqueté met à casser un bloc. `1` est la vitesse normale |
| `prospectWear` | entier, 2 à 1000 | `2` | Combien de fois l'usure normale coûte une casse de prospection à l'outil. `2`, le double, est le minimum autorisé, et un objet sans durabilité ne paie rien |
| `oreTypes` | liste | vide | Types de minerais concernés, quel que soit celui qui les génère et ce que dit la liste blanche. Types connus : COAL, IRON, COPPER, GOLD, REDSTONE, DIAMOND, LAPIS, EMERALD, QUARTZ, DIRT, GRAVEL, DIORITE, GRANITE, ANDESITE, TUFF, CLAY, SILVERFISH, CUSTOM pour tout autre minerai |
| `oreTypesAreBlacklist` | booléen | `true` | Activé, les types de `oreTypes` sont ceux qui sont bloqués. Désactivé, seuls ces types sont générés |
| `blockOreDimensions` | liste | vide | Les dimensions auxquelles s'applique le blocage des minerais, vide signifiant toutes. Une dimension hors du périmètre n'est pas touchée du tout, si bien que les minerais d'un autre mod s'y génèrent alors que l'Overworld reste bloqué |
| `blockOreDimensionsAreBlacklist` | booléen | `false` | Activé, les dimensions listées sont celles qu'on laisse tranquilles |

### Biomes

*ce que fait chaque groupe*

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

`control.biomes` décide de ce groupe. Blocage des biomes par mod et par nom, et ce qui les remplace.

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `logBlockedBiomes` | booléen | `true` | Journalise un décompte par mod des biomes bloqués |
| `blockBiomes` | booléen | `false` | Empêche tous les biomes de se générer, sauf ceux des mods de biomeWhitelist. Les biomes bloqués deviennent le biome du vide, ou ce que désignent les rôles et le repli du modèle de monde. Bloquer tous les biomes alors que le modèle de monde est vide, sans rôles et avec un défaut vide, fait des voidWorldDimensions un monde du vide |
| `biomeWhitelist` | liste d'ids de mod | `["minecraft"]` | Les mods dont les biomes se génèrent encore tant que `blockBiomes` est activé. Un biome de pack utilise le namespace du pack |
| `biomeNames` | liste | vide | Biomes concernés, par id comme minecraft:birch_forest ou par le nom qu'affiche le jeu, comme Forêt de bouleaux. En liste noire, ils sont bloqués quel que soit leur propriétaire. En liste blanche, un biome listé a quand même besoin que son mod figure dans biomeWhitelist tant que blockBiomes est activé |
| `biomeNamesAreBlacklist` | booléen | `true` | Activé, les noms de `biomeNames` sont bloqués. Désactivé, seuls ces noms sont générés |
| `blockBiomeDimensions` | liste | `["minecraft:overworld"]` | Les dimensions auxquelles s'applique le blocage des biomes. Vide signifie toutes |
| `blockBiomeDimensionsAreBlacklist` | booléen | `false` | Activé, le blocage saute les dimensions listées. Désactivé, il ne s'applique qu'à elles |

### Générateurs

*ce que fait chaque groupe*

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

`control.generators` décide de ce groupe. Blocage de la génération du monde par d'autres mods, par mod et par ce qu'elle produit. Un générateur est une feature placée, propriété du namespace de son id ; les features de Minecraft, celles de ce mod et les entrées de worldgen d'un pack ne sont jamais bloquées.

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `blockWorldGenerators` | booléen | `false` | Empêche tous les mods de générer via leurs propres features placées, c'est ainsi que les mods ajoutent îles de slime, cristaux de grotte et autres. Seuls les mods de generatorWhitelist génèrent encore |
| `generatorWhitelist` | liste d'ids de mod | `["minecraft"]` | Les mods encore autorisés à générer tant que `blockWorldGenerators` est activé |
| `blockedGenerators` | liste d'ids de mod ou de fragments d'id | vide | Générateurs individuels bloqués d'office, quoi que dise la liste blanche, par id de mod ou par fragment d'id de feature placée |
| `blockGeneratorDimensions` | liste | `["minecraft:overworld"]` | Les dimensions auxquelles s'applique le blocage des générateurs. Vide signifie toutes |
| `blockGeneratorDimensionsAreBlacklist` | booléen | `false` | Activé, le blocage saute les dimensions listées. Désactivé, il ne s'applique qu'à elles |
| `generatorTypes` | liste | vide | Types concernés, quel que soit le propriétaire du générateur et ce que dit la liste blanche : `ores`, `structures`, `flora`, `lakes`, `terrain`, ou `unknown` pour ceux que rien n'a reconnus. Le type vient de mots de l'id de la feature, donc `crystal_ore` est du minerai et `slime_island` est une structure |
| `generatorTypesAreBlacklist` | booléen | `true` | Activé, les types de `generatorTypes` sont ceux qui sont bloqués. Désactivé, seuls ces types sont générés |
| `generatorTypeMap` | liste de `pattern=type` | vide | Types pour les générateurs que l'id ne décrit pas, le motif étant un id de mod ou un fragment d'id de feature, p. ex. mymod=ores. Les entrées associées sont vérifiées avant les mots intégrés, elles corrigent donc aussi un cas que les mots lisent à tort |
| `logBlockedGenerators` | booléen | `true` | Journalise chaque générateur avec le type qui lui a été attribué la première fois qu'il est bloqué. `/rdplserver generators` affiche les totaux cumulés par mod et par type |

### Remplacements

*ce que fait chaque groupe*

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

`control.replacements` décide de ce groupe. Remplacement de blocs dans des chunks qui existent déjà.

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `blockReplacements` | liste | vide | Blocs échangés dans les chunks à leur chargement, écrits sous la forme bloc=bloc avec un état facultatif de chaque côté, comme minecraft:andesite=minecraft:stone ou minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]. Chaque chunk est traité une fois, les nouveaux compris |
| `blockReplacementDimensions` | liste | vide | Les dimensions auxquelles cela s'applique. Vide signifie toutes |
| `blockReplacementDimensionsAreBlacklist` | booléen | `false` | Activé, le remplacement saute les dimensions listées. Désactivé, il ne s'applique qu'à elles |
| `blockReplacementMinHeight` | entier, -2032 à 2031 | `-64` | Le y le plus bas examiné |
| `blockReplacementMaxHeight` | entier, -2032 à 2031 | `319` | Le y le plus haut examiné |
| `blockReplacementKey` | chaîne | `0000` | Changez-la et chaque chunk repasse par le remplacement |
| `logBlockReplacements` | booléen | `true` | Journalise la première fois que chaque remplacement est effectué, et un total quand un monde est rattrapé |

### Villages et villes

*ce que fait chaque groupe*

`control.villages` décide de ce groupe. Les rues de villes et de villages qu'un pack trace : leur forme, leur habillage, ponts, tunnels, rails, parcelles et place.

#### Routes de village

*villages et villes*

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

**Mélanger des blocs.** Certains réglages de bloc acceptent un mélange au lieu d'un seul bloc : des blocs séparés par des virgules, chacun suivi d'une espace et d'un poids, comme dans `"minecraft:stone_bricks 3, minecraft:cobblestone 1"`. Un bloc sans poids compte une fois. Chaque bloc posé tire au sort dans le mélange à partir de la graine du monde et de son emplacement, si bien qu'un même monde construit toujours le même motif. Les réglages qui acceptent un mélange sont `villagePathVergeBlock`, `villagePathVergeWaterBlock`, `villagePathTunnelBlock`, `villagePathBridgeFrameBlock`, `villagePathBridgeFrameTopBlock`, `villageRailTunnelBlock`, `villageRailDeckBlock`, `villageRailSupportBlock`, `villageRailBarrierBlock`, `villageRailBridgeFrameBlock`, `villageRailBridgeFrameTopBlock`, `villageSubwayTunnelBlock`, `villageSubwayPlatformBlock`, `villageSubwayRailingBlock`, `villageSubwayBenchEndBlock` et `villageSewerMossBlock`. Tout autre réglage de bloc utilise partout le premier bloc d'un mélange.

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `villagePathBlock` | texte | vide | La surface de la route quand terrainAdaptation trace les routes de ville. Vide : garde le bloc que le biome utiliserait, grès sur le sable, terre cuite sur les badlands, chemin de terre sur la terre |
| `villagePathExtraWidth` | entier, 0 ou plus | `0` | Blocs de largeur de route supplémentaires de chaque côté au-delà des 3 habituels, quand terrainAdaptation trace les routes. Élargit les rues elles-mêmes, si bien que les blocs entre elles s'écartent des routes larges |
| `villageBlockSizes` | liste de `size=weight` | vide | Profondeur des îlots entre les rues parallèles d'une ville, tirée une fois par quartier d'après la position de sa place. Vide : dimensionne chaque îlot d'après la plus grande parcelle que le pack fournit |
| `villageCitySpacing` | entier, 0 à 256 | `16` | Espacement entre les quartiers de ville semés, en quartiers dimensionnés d'après les parcelles (deux fois la plus grande parcelle, plus une place et une rue de chaque côté, arrondi à 16 blocs, 96 au minimum) : un quartier dans chaque carré de cette taille porte une ville, et à 1 chaque quartier en est une, une place avec le puits en son centre et des rues qui en partent et rejoignent celles du quartier suivant. 0 n'en sème aucune. Quand le pack ne le définit pas, `structureSpacing` villages=chunks le définit, 9 chunks au minimum. Un carré fonde sa ville sur son quartier le plus plat, avec au plus 10 blocs de dénivelé, dans un biome de village (`structureBiomes` villages= les choisit), et pas là où un manoir de la forêt pourrait apparaître ; `structureSeparation`, `structureMinDistanceFromSpawn` et `structureMost` villages= tiennent les villes à l'écart comme ils tenaient les villages, et `structureAt` villages=x,z ne fonde des villes que là où il les épingle. Une ville ne s'étend que sur les quartiers à portée de son premier puits dont le sol monte de 6 blocs au plus et reste au-dessus du niveau de l'eau, et une ville de deux parcelles et puits ou moins n'est pas construite |
| `villagePathAlleyBlock` | bloc | vide | La surface d'une ruelle, une route trop étroite pour porter des lignes et des trottoirs. Une ruelle passe entre les trottoirs des rues qu'elle rencontre et n'en porte aucun en propre, et aucun passage piéton n'est peint là où elle rejoint une rue. Vide : trace les ruelles avec le bloc de route |
| `villagePathAlleyChance` | entier, 0 ou plus | `0` | La probabilité en pourcentage qu'une rue soit tracée en ruelle plutôt qu'à sa pleine largeur. 0 ne trace aucune ruelle |
| `villagePathMinimumWidth` | entier, 0 ou plus | `0` | La rue la plus étroite autorisée. Une rue qui serait tracée plus étroite que cela n'est pas tracée du tout, et le quartier s'organise autour du vide. 0 ne refuse jamais |
| `villagePathFlatRun` | entier, 0 ou plus | `6` | Les rues gardent chaque pente au moins ce nombre de blocs avant de changer de niveau, ancrées aux coordonnées du monde pour que les tronçons concordent d'un morceau à l'autre. 0 ou 1 laisse une rue changer de niveau à chaque bloc |
| `villagePlotsLeast` | entier, 0 ou plus | `0` | Le nombre de parcelles jusqu'auquel une ville grandit : des quartiers sont ajoutés anneau par anneau autour de son centre jusqu'à en contenir au moins autant, sans jamais dépasser villagePlotsMost. 0 ne trace que le quartier central |
| `villagePlotsMost` | entier, 0 ou plus | `0` | Le plus grand nombre de parcelles qu'une ville peut contenir : la croissance s'arrête avant le quartier qui le dépasserait et aucun quartier n'en accueille davantage, et un quartier qui l'atteint omet les ruelles que ne borde aucune parcelle. 0 ne fixe aucun plafond |
| `villagePlotsBackRow` | booléen | `true` | Une fois le village grandi, une deuxième passe place une parcelle juste derrière chaque parcelle qui donne sur une rue, tournée vers elle, avec le même tirage et le même test d'espace, si bien que l'intérieur d'un îlot entre deux rues est construit plutôt que laissé nu |
| `villageTieStreets` | booléen | `true` | Activé, un quartier qui ne peut pas prolonger ses rues jusqu'au village existant reçoit une rue de liaison droite tracée jusqu'à la rue la plus proche avec laquelle il s'aligne. Désactivé, un tel quartier est démonté |
| `villageLayout` | texte | vide | Un plan de ville tracé à la place du plan du quartier, nommé comme mypack:downtown et lu dans le dossier citymaps de ce pack. Vide : planifie le quartier comme d'habitude |
| `villagePathCenterBlock` | bloc | vide | Une ligne centrale au milieu de la route. Vide : n'en trace aucune |
| `villagePathCenterDash` | entier, 0 ou plus | `0` | Met cette ligne en pointillés : N blocs de ligne, puis un de route. Ancrés aux coordonnées du monde, si bien que les tirets d'un tronçon de route se poursuivent dans le suivant. `0` la garde continue |
| `villagePathLineBlock` | bloc | vide | Lignes de bord entre route et trottoir. Vide : n'en trace aucune |
| `villagePathSidewalkBlock` | bloc | vide | Trottoirs, posés au niveau de la route à l'extérieur des lignes de bord. Vide : n'en pose aucun |
| `villagePathSidewalkWidth` | entier, 0 ou plus | `2` | La largeur de chaque trottoir, une fois `villagePathSidewalkBlock` défini |
| `villagePathLampBlock` | texte | `minecraft:oak_fence` | Le bloc dont est construit un lampadaire le long d'une rue, empilé sur villagePathLampHeight de haut sur la bordure. Une rue ou une ruelle en porte un à chaque bout, un là où une autre rue la rejoint et un tous les 7 à 12 blocs entre les deux, sur son côté bas et sur l'autre seulement là où celui-ci n'a pas de place, et une impasse en borde son pourtour. Aucun ne se dresse sur un pont, dans un tunnel ni à moins de deux blocs d'une porte. Vide : ne dresse aucun lampadaire |
| `villagePathLampHeight` | entier, 1 ou plus | `3` | Combien de blocs de haut s'élève le poteau avant sa tête |
| `villagePathLampTopBlock` | bloc | `minecraft:black_wool` | La tête au sommet du poteau. Vide : la laisse nue |
| `villagePathLampSideBlock` | bloc | `minecraft:torch` | La lumière accrochée de chaque côté de la tête, tournée vers l'extérieur. Vide : n'en accroche aucune |
| `villagePathLampStructure` | texte | vide | Un fichier de structure placé comme lampadaire entier au lieu d'empiler les trois blocs de lampadaire, nommé `mypack:street_lamp` et lu dans le dossier `structures` de ce pack. Il est centré sur l'emplacement du lampadaire, sa couche la plus basse sur la bordure, et les blocs qu'il pose sont retenus pour que rien d'autre ne les écrase. Vide : empile les blocs |
| `villageWellStructure` | liste | vide | Fichiers de structure placés comme pièce centrale de chaque place, une entrée pondérée par ligne écrite nom=poids comme mypack:plaza_spire=3, tirée une fois par place. Elle est centrée sur un carré de six blocs dégagé et pavé avec villagePathBlock, sa couche la plus basse sur ce sol. Vide, la part vide, ou une structure qui ne peut pas être chargée construit à la place le puits du jeu. Une entrée qui n'est pas écrite nom=poids est omise |
| `villagePathDeadEnds` | liste | vide | Comment une rue en cul-de-sac est fermée, une entrée par ligne, tirée par extrémité : sidewalk pave la rangée de l'extrémité avec le bloc de trottoir et barrier dresse villagePathBridgeBarrierBlock le long de celle-ci sur villagePathBridgeBarrierHeight de haut ; toute autre entrée est ignorée. Elles ne ferment qu'une extrémité qui n'a pas fait naître d'impasse en cul-de-sac, un style dont le bloc n'est pas défini sort du tirage, et une extrémité de ruelle n'accepte que barrier. Vide : laisse ces extrémités ouvertes |
| `villagePathIntersects` | liste | vide | Motifs peints aux carrefours, nommés par clé de registre depuis le dossier `<namespace>/pathintersects/` d'un pack. Une entrée peint tous les carrefours de la même façon ; plusieurs sont choisies par carrefour selon leur poids |

#### Ponts et pontons de village

*villages et villes*

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

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `villagePathSupportBlock` | texte | vide | La surface elle-même là où le sol est de la roche nue, et les pontons et piliers sous une rue au-dessus de l'eau. Vide : garde le gravier vanilla, du grès dans les villes du désert |
| `villagePathBridgeBlock` | texte | vide | Le bloc avec lequel une rue ou un ponton traverse l'eau. Vide : le tablier est en planches du bois du village : acacia dans un village de savane, épicéa dans un village de taïga, chêne ailleurs |
| `villagePathBridgeSidewalkBlock` | bloc | vide | Le tablier du trottoir là où une route traverse l'eau. Vide : fait traverser le bloc de trottoir normal |
| `villagePathBridgeBarrierBlock` | bloc | vide | Barrières empilées le long des deux bords d'un tablier de pont. Aucune là où le tablier repose sur le sol. Vide : n'en construit aucune |
| `villagePathBridgeBarrierHeight` | entier, 1 ou plus | `1` | Combien de blocs de haut s'élèvent ces barrières |
| `villagePathBridgeDrop` | entier, 0 ou plus | `0` | De combien le niveau d'une route doit s'élever au-dessus du sol pour que le vide sous elle soit enjambé plutôt que comblé en plein. `0` garde les routes au sol : elles ne franchissent que l'eau, rien d'autre. `3` est la règle que suit un viaduc ferroviaire. Cela déplace le niveau de la route, pas seulement l'habillage |
| `villagePathVergeBlock` | texte | vide | Le bloc dont est comblé le sol à côté d'une rue et sous une parcelle là où la ville doit créer du terrain : les rainures entre parcelles et le remblai jusqu'à une rue par-dessus un vide, qui est plutôt couvert de villagePathBridgeBlock là où la rue est un pont. Vide : suit le sol sur lequel il se trouve, sable, terre cuite, gravier ou terre avec de l'herbe dessus là où ce serait de la terre |
| `villagePathVergeWaterBlock` | bloc | `minecraft:oak_planks` | Ce que devient ce remblai là où il se trouve au-dessus de l'eau, pour qu'un bas-côté avancé sur un lac ne soit pas une colonne de terre. Il habille aussi un seuil de pierre laissé au-dessus de l'eau |
| `villagePathBridgeFrameBlock` | bloc | vide | Un portique au-dessus d'un long pont : un poteau de chaque côté du tablier et une poutre en travers au sommet. Chaque portique porte un pilier jusqu'au sol sous le tablier, et aucun lampadaire n'est dressé sur la rangée où il se tient. Vide : n'en construit aucun |
| `villagePathBridgeFrameTopBlock` | bloc | vide | La poutre en travers au sommet de ce portique. Vide : utilise `villagePathBridgeFrameBlock` |
| `villagePathBridgeFrameHeight` | entier, 1 ou plus | `4` | Combien de blocs de hauteur libre le portique laisse au-dessus du tablier, la poutre se trouvant un bloc au-dessus |
| `villagePathBridgeFrameRun` | entier, 1 ou plus | `24` | À combien de rangées les portiques se dressent les uns des autres quand un pont est assez long pour en porter plusieurs. Ils sont répartis symétriquement autour du milieu du tronçon enjambé |
| `villagePathBridgeFrameLeast` | entier, 1 ou plus | `24` | Le plus court tronçon enjambé qui reçoive un portique. Un pont plus court reste nu |
| `villagePathPiers` | liste | vide | Styles de ponton pour une rue qui se termine en cul-de-sac au-dessus de l'eau : la queue enjambée devient un ponton au lieu d'un pont vers nulle part. Les styles sont railed, pilings et boardwalk ; plusieurs entrées en tirent un par ponton. Vide : laisse une telle queue en simple pont |
| `villagePathPierCargo` | liste | vide | Cargaison posée le long de l'intérieur des garde-corps d'un ponton, sous forme d'entrées bloc=poids, bloc=poids,hauteur pour l'empiler, ou empty=poids pour la part laissée libre. Un bloc peut porter son état entre crochets, et un bloc avec une orientation se tourne vers le milieu du ponton. Une hauteur qui n'est pas de 1 à 8 donne un seul bloc. Chaque autre rangée tire la liste de chaque côté. Vide : laisse les pontons nus |
| `villagePathPierLoot` | texte | `resourcedatapackloader:chests/pier_cargo` | La table de butin dont sont remplis les blocs de cargaison dotés d'un inventaire, tirée la première fois que l'un d'eux est ouvert. Un pack peut remplacer la table intégrée en fournissant sa propre table de butin sous ce nom. Vide : les laisse vides |

#### Tunnels de village

*villages et villes*

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

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `villagePathTunnelBlock` | texte | vide | Le bloc dont est revêtue une rue là où elle perce une colline au lieu de l'entailler : les parois de chaque côté du percement et la voûte au-dessus. Vide : ne perce aucun tunnel et laisse la rue grimper la colline |
| `villagePathTunnelDepth` | entier, 1 ou plus | `10` | Quelle épaisseur de terrain doit se trouver au-dessus de la surface de la route avant qu'un tronçon soit percé plutôt qu'entaillé. Une bosse enfouie à cette profondeur sur douze rangées ou plus est maintenue de niveau et percée, ses approches moins profondes étant entaillées ; une bosse plus courte est entaillée comme avant. Ne compte qu'une fois que `villagePathTunnelBlock` nomme un bloc |
| `villagePathTunnelLightBlock` | bloc | vide | Une lumière encastrée dans la voûte du tunnel le long de son axe. Vide : n'en éclaire aucune |
| `villagePathTunnelLightRun` | entier, 1 ou plus | `8` | À combien de blocs de distance se trouvent ces lumières. Ancrées aux coordonnées du monde, si bien que les lumières d'un tronçon de route se poursuivent dans le suivant ; un tunnel trop court pour atteindre l'un de ces emplacements est éclairé une fois, en son milieu |

#### Égouts de village

*villages et villes*

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

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `villageSewerBlock` | nom de bloc | vide | Le bloc dont est revêtu un égout sous les rues et ruelles d'un village : son sol, ses deux parois et sa voûte. Vide : ne creuse aucun égout |
| `villageSewerDepth` | entier, 4 ou plus | `8` | À quelle profondeur sous la surface propre d'une rue se trouve le sol de l'égout. L'égout suit la rue sous laquelle il passe, si bien qu'une rue qui monte porte un égout qui monte. Nécessite `villageSewerBlock` |
| `villageSewerHeight` | entier, 2 ou plus | `3` | Combien de blocs de hauteur libre se trouvent au-dessus du passage |
| `villageSewerWidth` | entier, 3 ou plus | `5` | La largeur de l'égout, comptée en travers parois comprises. Un nombre pair est arrondi à l'impair supérieur pour que le canal garde le milieu |
| `villageSewerWaterBlock` | nom de bloc | `minecraft:water` | Ce qui remplit le canal au milieu. Vide : laisse le canal à sec |
| `villageSewerWalkBlock` | nom de bloc | vide | Le revêtement des passages de chaque côté du canal. Vide : marche sur le bloc de revêtement |
| `villageSewerLightBlock` | nom de bloc | vide | Le bloc encastré dans la voûte au-dessus du canal comme lumière. Vide : n'en éclaire aucune |
| `villageSewerLightRun` | entier, 1 ou plus | `8` | À combien de blocs de distance se trouvent ces lumières. Ancrées aux coordonnées du monde, si bien que les lumières d'un tronçon de route se poursuivent dans le suivant |
| `villageSewerLadderBlock` | texte | vide | Le bloc par lequel on monte dans un puits de bouche d'égout, posé le long du puits depuis la rue jusqu'à la voûte de l'égout. Vide : laisse le puits ouvert |
| `villageSewerCoverBlock` | nom de bloc | vide | Le bloc qui couvre une bouche d'égout, posé à fleur dans une rue est-ouest partout où une rue ou une ruelle la rejoint, et sur la place là où cette rue croise la boucle d'égout. Une trappe en bois est le choix habituel : une trappe en fer réagit à un signal de redstone et aucun joueur ne peut l'ouvrir à la main, ce qui lui ferme l'égout. Vide : laisse l'orifice du puits ouvert |
| `villageSewerMossBlock` | nom de bloc | vide | Un second bloc mêlé ici et là au revêtement, de la pierre moussue parmi de la pierre ordinaire par exemple. Vide : revêt l'égout d'un seul bloc partout |
| `villageSewerMossChance` | 0 à 100 | `25` | Quel pourcentage des blocs de revêtement sort sous forme de ce second bloc. Tiré par position de bloc à partir de la graine du monde, si bien qu'un même égout sort toujours identique |
| `villageSewerVineBlock` | nom de bloc | vide | Un bloc accroché ici et là à l'intérieur des parois de l'égout, des lianes par exemple. Il s'accroche à la paroi contre laquelle il se trouve. Vide : n'accroche rien |
| `villageSewerVineChance` | 0 à 100 | `20` | Quel pourcentage des cellules à côté d'une paroi en porte. Tiré par position de bloc à partir de la graine du monde, si bien qu'un même égout est toujours garni de la même façon |
| `villageSewerWellEntrance` | booléen | `true` | Une boucle d'égout sous l'anneau de la place autour du puits, que traverse l'égout de chaque rue, et une bouche d'égout sur la place qui descend vers la boucle de chaque côté où une rue est-ouest la croise, si bien que les égouts forment un seul système connecté avec une entrée au centre de la ville. Désactivé, l'égout de chaque rue s'arrête au puits et la place n'a aucun accès vers le bas |

**Égouts.** Nommer `villageSewerBlock` creuse un égout sous chaque rue et ruelle, à `villageSewerDepth` blocs sous la surface propre de cette rue. Ce n'est pas un réseau à part : il suit les rues, si bien que partout où elles vont l'égout va, qu'il monte là où elles montent, et que deux égouts se rejoignent sous un carrefour parce que les rues au-dessus d'eux se rejoignent ; là où une rue ou une ruelle se termine contre une autre, son égout continue sous celle-ci pour la rejoindre. Une impasse et un tronçon porté par un pont n'en portent aucun. La coupe est un sol revêtu, un canal au milieu rempli de `villageSewerWaterBlock`, un passage de chaque côté revêtu de `villageSewerWalkBlock`, `villageSewerHeight` blocs de hauteur libre et une voûte revêtue, `villageSewerWidth` de large en travers parois comprises, et `villageSewerLightBlock` encastre une lumière dans la voûte au-dessus du canal tous les `villageSewerLightRun` blocs. Là où un percement de métro traverse la profondeur de l'égout, sous la rue ou à côté, l'égout est muré en plein à cet endroit et les rails qui s'y trouvent sont laissés intacts. L'égout d'une rue s'arrête à la boucle autour du puits quand `villageSewerWellEntrance` est activé, et au puits lui-même quand il est désactivé. Un égout ne remonte jamais assez pour perturber la rue au-dessus de lui, et un tronçon sans place entre la rue et le plancher du monde est sauté plutôt que comprimé.

#### Voies ferrées de village

*villages et villes*

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

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `villageRailLines` | entier, 0 ou plus | `0` | Combien de lignes de chemin de fer traversent une ville, tracées avant toute rue pour que la ville se développe autour d'elles, chacune parcourant toute la longueur de la ville. Avec villageCitySpacing à 1, chaque quartier est une ville et porte la sienne. 0 n'en trace aucune |
| `villageRailSpacing` | entier, 1 ou plus | `48` | Le moins de blocs de terrain dégagé entre le lit d'une ligne de chemin de fer et celui de la suivante de la même ville. 1 les pose à un bloc l'une de l'autre, ce qui permet à un pack de construire un faisceau de voies parallèles |
| `villageRailDirection` | texte | `any` | Dans quel sens courent les lignes : `ew` d'est en ouest, `ns` du nord au sud, `any` tire au sort par ville. `e`, `w`, `n` et `s` se lisent de la même façon |
| `villageRailWidth` | entier, 3 ou plus | `3` | La largeur minimale du lit de voie. `3` porte une voie au milieu et `5` en porte deux ; un lit auquel on demande plus de voies que cela n'en contient s'élargit pour les accueillir |
| `villageRailBlock` | bloc | vide | La voie. Vide : pose des rails vanilla, sur lesquels roulent les wagonnets ; tout autre bloc est posé tel quel |
| `villageRailTrackSeat` | `auto`, `on` ou `in` | `auto` | Où repose la voie. `auto` pose un bloc de rail sur le lit et encastre tout autre bloc à fleur de la surface du lit ; `on` la pose toujours sur le lit ; `in` l'encastre toujours dans le lit. Une voie encastrée dans le lit est la façon dont un pack obtient un aspect de rail en blocs de fer ou en dalles plutôt qu'en rails de wagonnet, et un passage à niveau traverse alors le pavage à fleur |
| `villageRailBedBlock` | bloc | vide | Le lit sous la voie. Vide : pose du gravier |
| `villageRailTieBlock` | texte | vide | La traverse posée en travers du lit toutes les villageRailTieRun rangées. Vide : pose des planches de chêne |
| `villageRailTieRun` | entier, 1 ou plus | `2` | À combien de rangées les traverses sont espacées |
| `villageRailTracks` | entier, 0 ou plus | `0` | Combien de voies porte un même lit, côte à côte et espacées de `villageRailTrackGap`. **Le lit s'élargit pour toutes les accueillir**, si bien que trois voies partagent un seul lit plutôt que de devenir trois lignes. `0` pose une voie sur un lit de moins de cinq de large et deux sur un lit plus large |
| `villageRailTrackGap` | entier, 2 ou plus | `2` | De combien de blocs les voies d'un lit sont espacées, de centre à centre. `2`, le minimum autorisé, laisse un bloc de lit entre elles, ce qui les empêche de se courber l'une vers l'autre comme le font des rails qui se touchent |
| `villageRailShoulderBlock` | bloc | vide | Habille les colonnes les plus extérieures du lit, un chemin d'entretien à côté de la voie et l'équivalent ferroviaire d'un trottoir de route. Vide : n'en pose aucun |
| `villageRailShoulderWidth` | entier, 0 ou plus | `1` | Combien de colonnes de large est cet accotement de chaque côté, ajoutées à l'extérieur de `villageRailWidth`. Nécessite `villageRailShoulderBlock` |
| `villageRailPowerBlock` | bloc | vide | La voie alimentée insérée dans la ligne toutes les `villageRailPowerRun` rangées. Vide : utilise un rail propulseur vanilla ; un bloc qui n'est pas un rail est simplement posé là |
| `villageRailPowerBase` | bloc | vide | Ce qui se trouve sous une voie alimentée pour l'alimenter. Vide : utilise un bloc de redstone |
| `villageRailPowerRun` | entier, 0 ou plus | `0` | Toutes les tant de rangées, un rail propulseur sur un bloc de redstone est inséré dans une voie de rails vanilla, pour qu'un wagonnet continue de rouler. `0` n'en alimente aucun, et toute voie autre que des rails vanilla l'ignore |
| `villageRailClimb` | entier, 1 ou plus | `8` | Combien de rangées la ligne court à niveau pour chaque bloc qu'elle monte ou descend. `1` la met en pente aussi raide qu'une route |
| `villageRailTail` | entier, 0 ou plus | `48` | Jusqu'où une ligne de chemin de fer se prolonge au-delà du dernier quartier de la ville, à chaque extrémité |
| `villageRailSupportBlock` | texte | vide | Le bloc des poteaux sous un viaduc, là où la ligne passe au-dessus de l'eau ou d'un vide. Vide : utilise des bûches de chêne |
| `villageRailDeckBlock` | texte | vide | Le tablier sur lequel un viaduc porte le lit. Vide : utilise des planches de chêne |
| `villageRailBarrierBlock` | bloc | vide | Barrières le long des deux bords du tablier d'un viaduc. Vide : n'en dresse aucune |
| `villageRailBridgeFrameBlock` | bloc | vide | Un portique au-dessus d'un long viaduc : un poteau de chaque côté du tablier et une poutre en travers au sommet. Chaque rangée qui en porte un porte aussi ses poteaux d'appui jusqu'au lit. Vide : n'en construit aucun |
| `villageRailBridgeFrameTopBlock` | bloc | vide | La poutre en travers au sommet de ce portique. Vide : utilise `villageRailBridgeFrameBlock` |
| `villageRailBridgeFrameHeight` | entier, 2 ou plus | `4` | Combien de blocs de hauteur libre le portique laisse au-dessus du tablier, la poutre se trouvant un bloc au-dessus |
| `villageRailBridgeFrameRun` | entier, 2 ou plus | `24` | À combien de rangées les portiques se dressent les uns des autres quand un viaduc est assez long pour en porter plusieurs |
| `villageRailBridgeFrameLeast` | entier, 2 ou plus | `24` | Le plus court viaduc qui reçoive un portique. Un viaduc plus court reste nu |
| `villageRailTunnelBlock` | texte | vide | Le bloc dont est revêtue une ligne de chemin de fer là où elle perce une colline au lieu de la gravir. Vide : ne perce aucun tunnel |
| `villageRailTunnelDepth` | entier, 1 ou plus | `6` | Quelle épaisseur de terrain doit se trouver au-dessus du lit avant qu'un tronçon soit percé plutôt qu'entaillé. Nécessite `villageRailTunnelBlock` |
| `villageRailTunnelLightBlock` | bloc | vide | Une lumière encastrée dans la voûte d'un tunnel ferroviaire le long de son axe. Vide : n'en éclaire aucune |
| `villageRailTunnelLightRun` | entier, 1 ou plus | `8` | À combien de blocs de distance se trouvent ces lumières de tunnel, ancrées aux coordonnées du monde pour que les tronçons concordent |

**Où passe une ligne.** Les lignes courent parallèlement, sur l'axe que nomme `villageRailDirection`, et sont espacées à partir du premier puits de la ville tour à tour, d'abord d'un côté puis de l'autre, chacune gardant au moins `villageRailSpacing` blocs de terrain entre son lit et celui de la ligne suivante. Un chemin de fer démarre à l'écart de la place et des parcelles qui l'entourent et se décale partout où il longerait une rue ; un métro démarre sur la rangée du puits et se décale sur la rue la plus proche dans la limite de `villageSubwaySpacing`, si bien qu'il passe sous une route. Une ligne est tracée avant les parcelles, donc aucune parcelle ne se dresse sur une voie ouverte, et elle parcourt toute la longueur de la ville et se prolonge de `villageRailTail` au-delà de son dernier quartier à chaque extrémité, s'arrêtant sept blocs avant toute autre ville sur son chemin. Chaque quartier qu'elle traverse trace son propre tronçon, que la ville s'y soit étendue ou non.

**Pente.** Un chemin de fer ne grimpe pas comme une rue. Son lit suit le sol lissé sur une longue distance et ne change de niveau que d'un bloc au plus toutes les `villageRailClimb` rangées ; un métro suit le sol à `villageSubwayDepth` en dessous, et une ligne qui ne peut tenir cette profondeur nulle part sur son tracé, avec les six blocs de place dont son revêtement a besoin au-dessus du plancher du monde, n'est pas tracée du tout. Là où le sol chute de plus de trois blocs, ou que l'eau le couvre, la ligne passe sur un viaduc : un tablier de `villageRailDeckBlock` avec la voie dessus ou dedans et sans traverses ni accotement, sur des poteaux `villageRailSupportBlock` sous les deux bords toutes les quatre rangées, chaque poteau descendant jusqu'au sol solide sur 24 blocs au plus. Là où le sol monte, la ligne est entaillée, ou percée avec `villageRailTunnelBlock` dès que le terrain au-dessus du lit atteint `villageRailTunnelDepth` d'épaisseur sur douze rangées ou plus ; le percement se poursuit tant qu'un bloc de terrain le couvre encore. Une tranchée ouverte avec de l'eau à moins de trois blocs est murée dans le revêtement du tunnel jusqu'à l'eau, et le sol à côté du lit est remblayé là où il s'affaisse. Quatre blocs sont gardés dégagés au-dessus du lit sur toute la ligne. Un viaduc reste à une seule hauteur d'un bout à l'autre, et le lit de chaque côté monte en rampe pour la rejoindre ; là où tenir un viaduc à niveau et la vitesse de montée sont en désaccord, le niveau l'emporte et la rampe à côté peut changer de niveau plus tôt que ne le dit `villageRailClimb`. Un viaduc de `villageRailBridgeFrameLeast` rangées ou plus porte des portiques dès que `villageRailBridgeFrameBlock` nomme un bloc, à `villageRailBridgeFrameRun` rangées d'écart et répartis symétriquement autour du milieu du viaduc, et chaque rangée qui en porte un en porte aussi les poteaux d'appui. Une rangée où une rue croise la ligne est laissée sans portique.

**Croisements.** Une rue croise une ligne en ligne droite. À un croisement, la ligne est maintenue à niveau en travers de la rue et une rangée au-delà de chaque côté, et c'est la rue qui est mise à la pente de la ligne, jamais l'inverse, en rampe vers ce niveau selon sa propre pente. Le pavage garde la surface et la voie le traverse un bloc plus haut, ou à fleur quand `villageRailTrackSeat` encastre la voie dans le lit, si bien qu'un wagonnet traverse la rue et un villageois traverse la voie. Une ligne percée sous une rue qui se trouve six blocs ou plus au-dessus d'elle n'est pas croisée du tout : la rue garde sa propre pente et passe par-dessus le tunnel. Cette hauteur est lue sur le sol de la rue lissé pour monter d'un bloc par rangée au plus, avant que tout croisement, puits ou chemin de fer ne le fixe.

**Seuils.** La marche devant chaque porte d'une parcelle est comblée de terre là où le sol s'affaisse, et une marche de pierre laissée au-dessus de l'eau est habillée de `villagePathVergeWaterBlock`.

**Voie.** Avec `villageRailBlock` vide, la voie est du rail vanilla orienté le long de la ligne, et `villageRailPowerRun` pose un rail propulseur, activé, sur un bloc de redstone toutes les tant de rangées pour qu'un wagonnet parcoure toute la ligne ; une voie encastrée dans le lit ne porte aucun rail propulseur. Un pack qui veut des blocs de fer, des barreaux ou autre chose les nomme à la place : un bloc doté d'un axe, une bûche par exemple, est orienté le long de la ligne, et tout autre est posé tel quel. Chaque bloc de rail, de métro, de station et d'égout peut porter son état entre crochets, et Mélanger des blocs plus haut nomme les réglages dont un mélange pondéré est tiré bloc par bloc.

#### Métros de village

*villages et villes*

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

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `villageSubwayLines` | entier, 0 ou plus | `0` | Combien de lignes de chemin de fer souterraines une ville creuse. 0 n'en creuse aucune et ne tire rien, la ville est donc tracée exactement comme elle le serait sans elles |
| `villageSubwayDepth` | entier, 6 ou plus | `24` | À quelle profondeur sous la surface se trouve le lit. La ligne est mise en pente d'après le sol au-dessus d'elle, elle suit donc le terrain à cette profondeur plutôt que de courir à niveau |
| `villageSubwaySpacing` | entier, 1 ou plus | `64` | À quelle distance les lignes de métro d'une ville sont tenues les unes des autres |
| `villageSubwayDirection` | texte | `any` | Dans quel sens courent les lignes de métro : ew d'est en ouest, ns du nord au sud, ou any pour tirer au sort par ville |
| `villageSubwayWidth` | entier, 3 ou plus | `3` | La largeur du lit, accotements non compris |
| `villageSubwayBlock` | bloc | vide | Le bloc de voie. Vide : pose du rail vanilla |
| `villageSubwayTrackSeat` | chaîne | `auto` | Si la voie repose sur le lit, dedans, ou `auto` pour laisser le bloc décider |
| `villageSubwayBedBlock` | bloc | vide | Le bloc dont est fait le lit. Vide : utilise du gravier |
| `villageSubwayTieBlock` | bloc | vide | Le bloc posé en travers du lit comme traverses. Vide : utilise des planches |
| `villageSubwayTieRun` | entier, 1 ou plus | `2` | À combien de blocs les traverses sont espacées |
| `villageSubwayTracks` | entier, 0 ou plus | `0` | Combien de voies parallèles porte le lit. 0 en prend autant que la largeur le permet |
| `villageSubwayTrackGap` | entier, 2 ou plus | `2` | À quelle distance les voies parallèles sont espacées |
| `villageSubwayShoulderBlock` | bloc | vide | Le bloc de chaque côté du lit. Vide : ne laisse aucun accotement |
| `villageSubwayShoulderWidth` | entier, 0 ou plus | `1` | La largeur de cet accotement |
| `villageSubwayPowerBlock` | bloc | vide | Le bloc de voie alimentée. Vide : utilise un rail propulseur vanilla |
| `villageSubwayPowerBase` | bloc | vide | Le bloc posé sous une voie alimentée pour l'actionner. Vide : utilise un bloc de redstone |
| `villageSubwayPowerRun` | entier, 0 ou plus | `0` | À combien de blocs les voies alimentées sont espacées. 0 n'en pose aucune |
| `villageSubwayTunnelBlock` | texte | vide | Le bloc dont est revêtu le percement : les parois de chaque côté et la voûte au-dessus. Vide : creuse le percement et ses stations sans revêtement |
| `villageSubwayTunnelLightBlock` | bloc | vide | Le bloc encastré dans la voûte du tunnel comme lumière. Vide : n'en éclaire aucune |
| `villageSubwayTunnelLightRun` | entier, 1 ou plus | `8` | À combien de blocs de distance se trouvent ces lumières, ancrées aux coordonnées du monde pour que les tronçons concordent |
| `villageSubwayClimb` | entier, 1 ou plus | `8` | Combien de blocs une ligne parcourt avant de pouvoir monter ou descendre d'un bloc |
| `villageSubwayTail` | entier, 0 ou plus | `48` | Jusqu'où une ligne se prolonge au-delà des éléments propres à la ville avant de s'arrêter |
| `villageSubwaySurfaces` | entier, 0 à 100 | `25` | La chance sur cent qu'une ligne de métro remonte à la surface à une extrémité et se poursuive de là comme un chemin de fer ordinaire, tunnel derrière elle et voie à ciel ouvert devant. La montée prend villageSubwayClimb rangées par bloc, si bien qu'une ligne profonde passe un long tronçon à remonter. 0 garde chaque métro enterré sur toute sa longueur |

**Remonter à la surface.** `villageSubwaySurfaces` est la chance sur cent qu'une ligne, au lieu de rester enterrée d'un bout à l'autre, remonte à la surface à une extrémité et se poursuive de là comme un chemin de fer ordinaire : tunnel derrière elle, voie à ciel ouvert devant. La montée obéit à `villageSubwayClimb`, un bloc par ce nombre de rangées, si bien qu'une ligne profonde de `villageSubwayDepth` consacre profondeur fois montée rangées à la seule rampe et a besoin d'un bon tronçon au-delà pour mériter ce nom ; une ligne sans place pour les deux reste simplement sous terre. La montée ne commence pas plus près que l'extrémité lointaine des rues sous lesquelles passe la ligne, si bien qu'elle émerge au-delà des rues de la ville plutôt qu'au travers, et une parcelle située sur le tronçon qui émerge lui cède la place. Les stations sont réservées une fois la montée décidée et se tiennent à l'écart de la rampe. Avec `villageSubwayTunnelBlock` vide, le percement, ses stations et ses escaliers sont creusés sans revêtement.

#### Stations de métro

*villages et villes*

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

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `villageSubwayStationLength` | entier, 0 ou plus | `0` | Combien de blocs de long est une salle de station, centrée sur la rangée où la ligne passe au plus près du puits. 0 ne construit aucune station |
| `villageSubwayStationRun` | entier, 0 ou plus | `0` | À combien de blocs d'écart se trouvent d'autres stations le long d'une ligne, après celle la plus proche du premier puits de la ville. 0 ne construit que celle du puits |
| `villageSubwayPlatformWidth` | entier, 0 ou plus | `3` | De combien la salle est élargie de chaque côté du lit pour former un quai |
| `villageSubwayPlatformBlock` | bloc | vide | Le bloc dont est pavé le quai. Vide : le pave avec le revêtement du tunnel |
| `villageSubwayRailingBlock` | bloc | `minecraft:iron_bars` | Le bloc posé en garde-corps autour de la tête de l'escalier d'une station là où il débouche sur la rue, pour que personne ne tombe dans le puits. Vide : laisse la tête sans garde-corps |
| `villageSubwayBenchBlock` | bloc | `minecraft:oak_stairs` | L'assise des bancs posés sur le quai d'une station et à côté de la tête de son escalier. Un bloc d'escalier est tourné dos à la ligne et se lit comme un banc ; n'importe quel bloc convient. Vide : omet les bancs |
| `villageSubwayBenchEndBlock` | bloc | `minecraft:oak_log` | Les accoudoirs à chaque bout d'un banc de station. Vide : laisse l'assise nue aux deux bouts |
| `villageSubwayBenchLength` | entier, 0 à 32 | `5` | La longueur d'un banc de station, accoudoirs compris. `0` omet les bancs |
| `villageSubwayStation` | texte | vide | Une structure du dossier structures d'un pack utilisée comme station : son puits, ses escaliers et son accès depuis la rue. Extrayez-en une d'un monde construit à la main avec #scripts/rdpl-grab-template.py : ses cellules pleines sont posées et ses cellules d'air sont creusées, si bien que la forme est la construction et non sa description. Vide : ne construit aucune station, et un nom qui ne peut pas être chargé journalise une erreur et n'en construit aucune |
| `villageSubwayStationFoot` | entier, 0 à 64 | `4` | Combien de couches au pied d'une construction de station sont posées une seule fois, avant la partie qui se répète. Le sol et la porte vers le quai s'y trouvent |
| `villageSubwayStationRepeat` | entier, 0 à 64 | `12` | Combien de couches d'une construction de station se répètent, si bien qu'une seule construction sert pour n'importe quelle profondeur : le puits s'allonge par copies entières de cette bande et le couloir absorbe le reste. Ce doit être un tour entier de l'escalier, sinon les volées ne se rejoindront pas. `0` n'allonge jamais la construction |

**Stations.** Une ligne de métro n'obtient de stations que si `villageSubwayStation` nomme une construction qui se charge : avec lui vide, il n'y a ni salle, ni escalier, ni accès, et un nom qui ne peut pas être chargé journalise une erreur et ne construit rien. Avec une construction nommée, une ligne obtient une station à la rangée du premier puits de la ville dès que `villageSubwayStationLength` et `villageSubwayPlatformWidth` sont définis, et d'autres tous les `villageSubwayStationRun` blocs le long de celle-ci. Chacune glisse jusqu'à 48 blocs dans un sens ou l'autre pour trouver un emplacement pour sa construction à côté d'une rue qui longe la ligne sur toute la longueur de cet emplacement, hors de toute rue, puits et place, et à au moins la longueur de la station plus sept d'une station déjà réservée ; une parcelle située sur cet emplacement lui cède la place, et une station sans un tel emplacement est omise. Une ligne qui ne garde aucune station n'ouvre aucune salle, elle n'en porte donc jamais une sans accès. La salle est maintenue à niveau sur sa longueur : le lit élargi de `villageSubwayPlatformWidth` de chaque côté, pavé de `villageSubwayPlatformBlock`, muré et couvert du revêtement du tunnel, éclairé par les `villageSubwayTunnelLightBlock` et `villageSubwayTunnelLightRun` propres au tunnel, et murée en travers du percement aux deux bouts. Depuis le quai, un couloir mène à la construction de la station, qui remonte jusqu'à la rue à côté de la route, jamais dessous ; la construction émerge à la pente de la rue la plus proche dans un rayon de huit blocs, la pente que cette rue garde au sol et non celle d'un tablier ou d'une rampe élevés au-dessus d'elle, ou à la hauteur du sol là où il n'y a pas de rue, et elle est omise quand la ville est construite là où cette hauteur est à moins de trois blocs au-dessus du quai, là où la construction ne peut pas effectuer la montée même agrandie, ou là où son couloir vers le quai dépasserait 32 blocs ; le journal dit lequel. Le sol entre cette rue et la construction est amené à la même hauteur, comblé là où il s'affaisse et dégagé au-dessus, si bien qu'on entre dans la station depuis la route. Un banc de `villageSubwayBenchBlock` avec des accoudoirs `villageSubwayBenchEndBlock`, long de `villageSubwayBenchLength`, se dresse sur le quai.

**Construire la station à la main.** `villageSubwayStation` nomme un fichier de structure utilisé comme station, c'est ainsi qu'un pack fournit une forme que quelqu'un a construite plutôt que décrite par des réglages. Construisez-la dans un monde, extrayez-la avec #scripts/rdpl-grab-template.py et placez-la avec le pack : ses blocs sont posés tels que construits, les cellules d'éponge deviennent le revêtement du tunnel, ses cellules d'air sont creusées, et tout ce qui s'y trouve, un wagonnet ou un porte-armure, vient avec. Elle est implantée depuis le coin de l'emplacement de la station. Une seule construction sert pour n'importe quelle profondeur parce que son milieu se répète : `villageSubwayStationFoot` couches sont posées une fois en bas, portant le sol et la porte vers le quai, puis des copies entières des `villageSubwayStationRepeat` couches suivantes s'empilent jusqu'à ce que la construction atteigne la rue. Cette bande doit être un tour entier de l'escalier, sinon les volées ne se rejoindront pas là où deux copies se joignent. Un couloir de deux blocs de haut va de la porte au quai, revenant le long de la salle là où la descente est trop longue pour aller tout droit ; le terrain au-dessus de la tête de la construction est dégagé sur huit blocs de haut, un garde-corps en `villageSubwayRailingBlock` entoure l'ouverture au niveau de la rue et un banc se dresse à côté. Une construction qui ne peut pas être chargée ne construit aucune station nulle part et journalise une erreur ; une station que sa construction ne peut pas amener jusqu'à la rue est omise à la planification de la ville, sans salle, et journalise pourquoi. La construction porte sa propre ouverture sur la rue.

#### Liaisons ferroviaires

*villages et villes*

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

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `villageRailLinks` | vrai/faux | `false` | Relie les villes voisines dont les premières lignes se font face de part et d'autre d'un raccord. Nécessite `villageRailLines`, ou `villageSubwayLines` sur un pack sans lignes de surface |
| `villageRailLinkLeast` | entier, 0 ou plus | `128` | La plus courte liaison posée, branche plus tronc plus branche, en blocs |
| `villageRailLinkMost` | entier, 0 ou plus | `1024` | La plus longue liaison posée, branche plus tronc plus branche, en blocs |
| `villageRailLinkBridgeMost` | entier, 0 ou plus | `96` | Le plus long pont qu'une liaison puisse exiger. Une liaison au-dessus d'une eau plus large ou d'un vide plus profond n'est pas posée |
| `villageRailLinkTunnelMost` | entier, 0 ou plus | `192` | Le plus long tunnel qu'une liaison puisse exiger là où `villageRailTunnelBlock` perce des tunnels. Une liaison qui devrait percer plus loin n'est pas posée |
| `villageRailLinkStation` | texte | `both` | La station sur chaque branche juste avant le tronc : `both` pose un quai de chaque côté de la ligne, `one` un seul quai à gauche d'un train qui arrive au tronc, `none` n'en construit aucune |
| `villageRailLinkStationLength` | entier, 0 ou plus | `16` | Combien de rangées de long sont les quais de station. `0` ne construit aucune station |
| `villageRailLinkPlatformWidth` | entier, 0 ou plus | `3` | Combien de blocs de large est chaque quai |
| `villageRailLinkPlatformBlock` | bloc | vide | Le bloc dont les quais sont construits. Vide : utilise des briques de pierre |

**Ce qu'est une liaison.** Les liaisons ferroviaires relient les villes voisines en un seul réseau. Les villes sont fondées à raison d'une par cellule de la grille des villes (`villageCitySpacing`), et une liaison court le long du raccord entre deux cellules : la première ligne de chaque ville se prolonge au-delà de sa queue en branche, tout droit jusqu'au raccord, et rejoint à angle droit un tronc posé le long du raccord. Le tronc va d'une branche à l'autre et jamais au-delà. Elle nécessite `villageRailLines`, ou `villageSubwayLines` sur un pack sans lignes de surface, et elle est désactivée par défaut.

**Quelles villes sont reliées.** Deux villes ne sont reliées que si elles se trouvent dans des cellules voisines, que leurs premières lignes courent sur l'axe qui traverse le raccord entre elles, et que la liaison entière, mesurée de puits à puits le long de la voie, fait entre `villageRailLinkLeast` et `villageRailLinkMost` blocs. Chaque élément de la décision est calculé à partir de la graine et des deux sites de ville, si bien que le résultat est le même quelle que soit la ville ou le chunk créé en premier. Une liaison qui ne peut pas être construite entière n'est pas posée du tout, jamais à moitié construite : celle qui exigerait un pont ou un tunnel plus long que ne le permettent les réglages, dépasserait la bordure du monde, heurterait un manoir de la forêt, placerait deux villes plus près que ne l'autorise `structureSeparation`, ou amènerait une jonction trop près d'un angle des cellules. Un tronc n'est posé que vers une ville effectivement fondée : quand un plafond tel que `structureMost` arrête la voisine, ou qu'elle devient trop petite pour être gardée, ni l'une ni l'autre moitié du tronc ni la branche au-delà de la queue de la ville ne sont construites. Les villes épinglées sont reliées de la même façon, une par cellule ; une cellule qui contient deux épingles n'en relie aucune. Les autres villes restent à l'écart de la branche et du tronc d'une liaison en grandissant, comme elles restent à l'écart les unes des autres.

**Pente.** Les branches et les troncs sont des lignes de chemin de fer, mises en pente, enjambées, percées et croisées exactement comme une ligne de ville, avec `villageRailClimb` et les réglages de viaduc et de tunnel ci-dessus. Là où une branche rejoint le tronc, les deux sont à niveau, de même que la station à côté.

**La jonction.** Une branche ne rejoint que la voie proche du tronc. Cette voie est interrompue là où le milieu de la branche la rencontre, la voie gauche de la branche s'y courbe vers la gauche et sa voie droite vers la droite, et la voie lointaine continue tout droit. Avec deux voies, le tronc en haut et la branche qui arrive d'en bas :

```
xxxxxxx
ooooooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

`x` est le lit de voie et `o` est la voie. Là où les deux branches arriveraient à quelques blocs l'une de l'autre, la première ligne de la seconde ville se décale pour s'aligner sur la première, et les deux se rejoignent plutôt en carrefour : chaque branche ne s'insère que dans sa propre voie proche exactement comme ci-dessus, les deux voies du tronc sont interrompues au centre de la branche, et aucun rail n'en croise un autre :

```
 xoxox
xxoxoxx
oooxooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

Un tronc à voie unique n'a pas de seconde voie à donner à l'autre branche, si bien qu'une liaison dont les branches se rencontreraient de front sur une voie unique n'est pas posée. Avec une voie unique, la voie de la branche se courbe dans la voie du tronc vers la gauche, et la voie du tronc au-delà de cette courbe se termine contre elle. Les courbes sont posées avec leurs formes fixées, si bien que le rail vanilla tourne là où la jonction est dessinée et nulle part ailleurs.

**Stations.** Les dernières rangées d'une branche avant la jonction forment une station : des quais de `villageRailLinkPlatformBlock` à niveau avec le rail, bordés d'un garde-corps sur le bord extérieur avec `villageSubwayRailingBlock`, avec un banc de `villageSubwayBenchBlock` à mi-longueur de chaque quai.

**Métros.** Sur un pack avec uniquement des lignes de métro, la liaison porte la première ligne de métro d'une ville. La ligne sort du sol vers le tronc, avec une rampe longue de `villageSubwayDepth` fois `villageSubwayClimb` rangées, et atteint la station et la jonction en surface ; une ville de ce type ne se relie que d'un seul côté, celui de la liaison la plus courte, et le tronc est un chemin de fer de surface construit à partir des réglages `villageRail`. Là où une branche n'a pas la place pour cette rampe et sa station, le tronc descend plutôt vers le métro : toute la liaison, branches et tronc, reste sous terre à `villageSubwayDepth`, est construite à partir des réglages `villageSubway`, et se rejoint dans la même jonction sans station.

#### Décoration de village

*villages et villes*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageDecor": ["mypack:street_flowers=2", "mypack:street_tree=1", "empty=3"]
  }
}
```

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `villageDecor` | liste | vide | Décoration semée le long des rues de la ville, sous forme de paires nom=poids nommant du worldgen d'un pack, mypack:street_flowers=2. Le nom empty est la part des emplacements laissés nus, et une entrée qui n'est pas écrite nom=poids est omise. Un bloc de bas-côté sur trois de chaque côté d'une rue tire la liste, sur le sol à cet endroit quelle que soit sa hauteur, mais pas dans un tunnel, ni sous une parcelle, ni sur une place, ni à moins de deux blocs d'une porte. Vide : ne sème rien |

### Structures

*ce que fait chaque groupe*

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

`control.structures` décide de ce groupe : structures vanilla désactivées, leur espacement, leur séparation, leur distance au point d'apparition, leurs biomes, leurs apparitions de créatures, les emplacements imposés et l'adaptation du terrain.

| Paramètre | Type | Défaut | Rôle |
| --- | --- | --- | --- |
| `structureSpacing` | liste | vide | Distance à laquelle les structures vanilla sont semées, en chunks, sous forme d'entrées structure=chunks : les noms de la 1.12.2 temples, monuments, mansions, mineshafts, strongholds, netherbridges, endcities et villages, ou l'identifiant de n'importe quel ensemble de structures, comme pillager_outposts. Pour mineshafts, le nombre signifie un chunk sur autant ; pour strongholds, c'est la distance entre anneaux. Les forteresses du Nether gardent leur propre grille, que l'espacement netherbridges n'atteint pas |
| `structureSeparation` | liste | vide | La distance minimale entre deux structures du même type, en chunks, sous forme d'entrées structure=chunks ; pour strongholds, c'est l'étalement des anneaux. Les temples, mineshafts et netherbridges gardent leur propre séparation, que ce paramètre n'atteint pas. Pour les monuments, une séparation aussi grande que l'espacement est ramenée à un de moins que l'espacement, et le journal le signale |
| `structureMost` | liste | vide | Le nombre maximal de villages qu'une dimension peut contenir, sous forme villages=nombre (par exemple villages=100) ; les autres structures ne sont pas plafonnées : une fois ce nombre de villages fondés, plus aucun chunk n'en fonde d'autre, hors chunks imposés par structureAt. 0 ou une entrée absente ne fixe aucun plafond |
| `structureSpawners` | liste de `structure=entity` | vide | Ce que fait apparaître le générateur de monstres d'une structure vanilla, séparé par des virgules pour un tirage aléatoire par générateur. Les quatre structures qui en placent un sont dungeons, mineshafts, les forteresses du Nether et strongholds |
| `structureMinDistanceFromSpawn` | liste | vide | La distance au point d'apparition du monde à partir de laquelle une structure commence, en blocs, sous forme d'entrées structure=blocs. Mesurée depuis le point d'apparition du monde ; tant qu'un nouveau monde en choisit encore un, depuis le worldSpawn du pack s'il y en a un, sinon depuis l'origine du monde |
| `structureBiomes` | liste | vide | Où une structure peut se générer, sous forme d'entrées structure=biome,biome nommant des identifiants de biome, les noms affichés par le jeu comme Birch Forest, des noms vanilla nus comme desert, ou des types de biome comme SANDY |
| `structureBiomesAreBlacklist` | liste de `structure=true` ou `structure=false` | vide | Le sens de la liste de biomes de chaque structure |
| `structureSpawns` | liste | vide | Les créatures qu'une structure fait apparaître quel que soit le biome, sous forme d'entrées structure=namespace:entity:poids:min:max, séparées par des virgules. La liste remplace entièrement la liste de créatures propre à la structure, quel que soit le type de chaque créature ; une liste vide après le = ne fait rien apparaître |
| `structureAt` | liste de `structure=x,z` | vide | Fixe une structure à un endroit exact. Voir [Structures à des endroits précis](#structures-à-des-endroits-précis) |
| `structureAdaptation` | liste | mansions `beard_thin` ; toute autre structure garde son adaptation vanilla | La manière dont le terrain s'adapte à une structure, sous forme d'entrées structure=mode avec les modes none, bury, beard_thin, beard_box et encapsulate |
| `terrainAdaptation` | booléen | `false` | Pose les rues de ville propres à RDPL, ancrées dans le terrain au lieu de reposer sur des pilotis au-dessus de chaque creux, et lit avec elles les options villagePath et villageRail. Modifie le terrain : un monde créé avec cette option activée diffère d'un monde créé sans. Les villes sont semées comme l'indique villageCitySpacing, et à 0 il n'y en a aucune |
| `villagePieces` | liste | vide | Parcelles de village nommées ici, une par ligne, par l'identifiant complet d'un fichier villages comme mypack:smithy, par son nom nu, ou par le nom de la structure que construit une parcelle de modèle. Tant que villagePiecesAreBlacklist est activé, une structure nommée ici est aussi laissée vide partout où le jeu la charge, y compris les maisons de village du jeu, comme minecraft:village/plains/houses/plains_small_house_1 |
| `villagePiecesAreBlacklist` | booléen | `true` | Activé, les parcelles de villagePieces sont bloquées. Désactivé, seules ces parcelles sont construites |
| `villageBlocks` | liste | vide | Les blocs avec lesquels les parcelles de village sont construites, sous forme de paires original=remplacement, minecraft:cobblestone=mypack:ruby_brick. Chaque côté peut porter un état entre crochets, que l'original doit alors respecter exactement. Une paire peut ajouter une chance sur 100, minecraft:cobblestone=minecraft:mossy_cobblestone,20, pesée à partir de la graine du monde à l'endroit où le bloc est posé, at=bloc pour ne s'appliquer que là où se trouve ce bloc, et under=bloc seulement au-dessus de lui. Les paires sans chance ni condition s'appliquent en premier, si bien qu'une paire conditionnelle peut altérer leur résultat. Elle régit les fermes et les maisons de village du jeu qu'une ville construit ; les rues, puits, lampadaires et les structures de vos parcelles de modèle ne sont jamais régis. Vide, chaque bloc reste tel qu'il est posé |

### Apparition

*ce que fait chaque groupe*

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

`control.spawning` décide de ce groupe : plafonds d'apparition des créatures, taux d'apparition des monstres et plafond de luminosité.

| Paramètre | Type | Défaut | Rôle |
| --- | --- | --- | --- |
| `surfaceDayMonsterRate` | nombre, 0.0 à 4.0 | `1.0` | Multiplicateur de l'apparition des monstres en surface de jour, `1.0` correspondant au vanilla : on peut ainsi désactiver l'apparition en surface en plein jour sans toucher aux grottes |
| `surfaceNightMonsterRate` | nombre, 0.0 à 4.0 | `1.0` | Idem pour la surface de nuit |
| `undergroundDayMonsterRate` | nombre, 0.0 à 4.0 | `1.0` | Idem pour le sous-sol de jour |
| `undergroundNightMonsterRate` | nombre, 0.0 à 4.0 | `1.0` | Idem pour le sous-sol de nuit |
| `monsterCap` | entier, -1 à 1000 | `-1` | Combien de monstres peuvent être chargés en même temps. Le vanilla est à 70, et `-1` n'y touche pas |
| `creatureCap` | entier, -1 à 1000 | `-1` | Idem pour les animaux passifs. Le vanilla est à 10 |
| `ambientCap` | entier, -1 à 1000 | `-1` | Idem pour les chauves-souris et consorts. Le vanilla est à 15 |
| `waterCreatureCap` | entier, -1 à 1000 | `-1` | Idem pour les calmars. Le vanilla est à 5 |
| `monsterSpawnLight` | entier, -1 à 15 | `-1` | La lumière de bloc la plus forte dans laquelle un monstre peut encore apparaître, en plus des vérifications vanilla. -1 ne garde que la règle vanilla. Les générateurs de monstres ne sont pas concernés |
| `threatItems` | liste | vide | Objets qui augmentent le niveau de menace d'un joueur, sous forme d'entrées objet=niveau,nombre avec un ,each ou ,batch facultatif à la fin, p. ex. minecraft:diamond_sword=5,1 ou minecraft:diamond=1,16,batch. Each, le mode par défaut, ajoute le niveau pour chaque exemplaire porté, sans en compter plus que nombre ; batch ajoute le niveau une fois pour chaque nombre d'exemplaires portés. Un nombre supérieur à la taille de pile de l'objet est ramené à cette taille. Toute entité chargée qui détient des objets est un porteur : l'inventaire principal, l'armure et la main secondaire d'un joueur, une pile jetée au sol, tout ce qui possède un inventaire d'objets comme une mule avec coffre ou une wagonnet avec coffre, et les objets tenus et l'armure des autres créatures. Vide, le niveau de menace est désactivé. Avec `control.spawning` sur `off`, les paramètres de menace propres à la config s'appliquent quand même |
| `threatLevels` | liste | vide | Les scores qui font entrer dans chaque palier, par ordre croissant : `10, 25, 50` donne donc trois paliers. Vide, le niveau de menace est désactivé |
| `threatMost` | entier, -1 à 100000 | `-1` | Plafonne le score. `-1` ne le plafonne pas |
| `threatSpawnRate` | nombre, 0.0 à 8.0 | `1.0` | Multiplie l'apparition des monstres à moins de 128 blocs d'un porteur du palier supérieur, en plus des autres taux, les paliers inférieurs en recevant une part proportionnelle |
| `threatNotice` | nombre, 0.0 à 64.0 | `0.0` | De combien de blocs de plus les monstres, vanilla compris, repèrent un porteur du palier supérieur, là encore réparti sur les paliers inférieurs |
| `threatSays` | liste de `band=message` | vide | Les lignes affichées en jaune quand le palier d'un joueur change, le palier `0` étant la ligne pour le retour sous le premier palier |

### Bedrock

*ce que fait chaque groupe*

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
    "flatBedrockRoof": true,
    "flatBedrockFiller": "minecraft:cobblestone",
    "flatBedrockFillers": ["minecraft:the_nether=minecraft:netherrack", "minecraft:the_end=minecraft:end_stone"],
    "flatBedrockBiomeTypes": ["minecraft:is_ocean"],
    "flatBedrockRetrogen": true
  }
}
```

`control.bedrock` décide de ce groupe : bedrock plat et ses listes de dimensions et de biomes.

| Paramètre | Type | Défaut | Rôle |
| --- | --- | --- | --- |
| `flatBedrock` | booléen | `false` | Remplace le bedrock irrégulier au fond du monde par des couches plates. Nouveaux chunks seulement, sauf si `flatBedrockRetrogen` est activé |
| `flatBedrockDimensions` | liste | `["minecraft:overworld"]` | Les dimensions où aplanir. Vide signifie toutes |
| `flatBedrockDimensionsAreBlacklist` | booléen | `false` | Activé, l'aplanissement ignore les dimensions listées. Désactivé, il ne s'applique qu'à elles |
| `bedrockLayers` | entier, 1 à 5 | `1` | Combien de couches de bedrock restent |
| `flatBedrockBiomes` | liste de noms de biomes | vide | Les biomes où aplanir, par nom convivial ou nom du registre. Vide signifie tous les biomes |
| `flatBedrockBiomesAreBlacklist` | booléen | `false` | Activé, l'aplanissement ignore les biomes listés. Désactivé, il ne s'applique qu'à eux |
| `flatBedrockRoof` | booléen | `false` | Aplanit aussi le plafond de bedrock, là où une dimension en a un, comme le toit du Nether |
| `flatBedrockFiller` | bloc | vide | Ce qui remplace le bedrock retiré. Vide, le choix dépend de la dimension : pierre, netherrack, pierre de l'End |
| `flatBedrockFillers` | liste de `dimension=block` | `["minecraft:the_nether=minecraft:netherrack", "minecraft:the_end=minecraft:end_stone"]` | Un remplissage par dimension, qui prend le pas sur `flatBedrockFiller` pour les dimensions nommées |
| `flatBedrockBiomeTypes` | liste | vide | Types de biome où aplanir le bedrock, en plus de flatBedrockBiomes, par étiquette de biome comme minecraft:is_ocean ou par nom de type 1.12.2 comme OCEAN. flatBedrockBiomesAreBlacklist s'applique aussi à eux |
| `flatBedrockRetrogen` | booléen | `false` | Aplanit aussi le bedrock des chunks qui existent déjà. Chaque chunk n'est traité qu'une fois et s'en souvient, et on ne peut pas revenir en arrière : le motif d'origine n'est enregistré nulle part |
| `flatBedrockRetrogenKey` | texte | `0000` | Modifiez-la pour rendre de nouveau chaque chunk éligible à l'aplanissement du bedrock |

### Ticks ralentis au loin

*ce que fait chaque groupe*

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

`control.entities` décide de ce groupe : le rythme ralenti des entités éloignées de tous les joueurs.

| Paramètre | Type | Défaut | Rôle |
| --- | --- | --- | --- |
| `slowDistantEntities` | booléen | `true` | Fait avancer moins souvent les entités éloignées de tous les joueurs. Rien n'est jamais laissé sans tick, seulement mis à jour à un rythme plus lent |
| `slowedKinds` | liste | `["items", "experience"]` | Les types qui reçoivent moins de ticks : items, experience, projectiles, ce dernier regroupant flèches, tridents, boules de neige, œufs, potions, fioles d'expérience et perles de l'Ender lancés, ainsi que les crachats de lama. Tout ce qui réfléchit par lui-même est toujours ralenti à la place, sans qu'il soit nommé ici : il choisit moins souvent ce qu'il fait ensuite, tout en se déplaçant à chaque tick. Les machines ne sont jamais ralenties |
| `slowDistance` | entier, 64 à 4096 | `192` | À quelle distance du joueur le plus proche, en blocs, un chunk est ralenti. Le jeu cesse de signaler à un joueur la plupart des entités au-delà de 64, donc rien en dessous |
| `slowRate` | entier, 1 à 20 | `4` | Un tick sur autant est accordé à un chunk ralenti. 1 n'est aucun ralentissement, 20 est une fois par seconde |
| `neverSlowed` | liste | vide | Entités laissées tranquilles quelle que soit leur distance, sous la forme namespace:name |
| `slowRecheck` | entier, 1 à 100 | `20` | À quelle fréquence, en ticks, la distance au joueur le plus proche est recalculée. Chaque joueur compte pour lui-même : quelqu'un d'isolé au loin garde donc son propre espace calme autour de lui |

### Terrain, retenues et ce que dit le mod

*ce que fait chaque groupe*

`control.chunks` décide de ce groupe : le rayon des chunks de spawn, la prégénération, la régénération rétroactive et la réinitialisation, les lignes de bienvenue, la carte de messages et les toasts du jeu.

| Paramètre | Type | Défaut | Rôle |
| --- | --- | --- | --- |
| `retrogen` | booléen | `false` | Met à niveau les chunks existants avec les entrées de worldgen portant \"retrogen\": true. Désactivé, les chunks qui existent déjà sont laissés tels quels. Les chunks sont marqués à leur génération dans les deux cas : l'activer plus tard ne touche donc que les chunks plus anciens que le pack |
| `adoptExistingChunks` | booléen | `false` | Traite les chunks qui existent déjà comme s'ils avaient été générés par ce pack, en les marquant au lieu de les laisser à la régénération rétroactive. Activez-le quand vous remplacez un mod qui générait déjà le même minerai, afin que la régénération rétroactive ne le double jamais. Les entrées de worldgen ajoutées plus tard s'y régénèrent quand même |
| `saysCard` | booléen | `false` | Affiche les lignes que dit ce mod, la bienvenue, la note de création du terrain que reçoit un joueur qui se connecte en cours de route et la fin de l'exécution (la progression en cours reste dans la barre d'action), ainsi que les lignes de menace, sous forme de carte dans le coin inférieur droit au lieu du chat. La carte glisse dans l'écran, reste huit secondes puis s'estompe, et s'affiche aussi par-dessus un écran ouvert |
| `saysIcon` | texte | vide | Un objet dessiné sur la carte, p. ex. minecraft:compass. Vide, aucun n'est dessiné |
| `saysColor` | texte | vide | La couleur de fond de la carte en hexadécimal, p. ex. 1E2630. Vide, un gris ardoise sombre est utilisé |
| `saysImage` | texte | vide | Un PNG des ressources client du pack étiré sur la carte comme arrière-plan, p. ex. rubyworld:textures/gui/card.png, dessiné par-dessus la couleur. Vide, aucun n'est dessiné |
| `saysBackground` | booléen | `true` | Dessine le panneau, la bordure et la bande de couleur de la carte, ainsi que le fond sombre derrière la bienvenue et les notes de retenue. Désactivé, il ne reste que le texte, qui garde son ombre, et saysImage s'il est défini |
| `saysFont` | texte | vide | Une police pour le texte de la carte, nommée namespace:name, p. ex. rubyworld:runes pour le assets/rubyworld/font/runes.json du pack. Vide, la police de RDPL est utilisée |
| `toasts` | booléen | `false` | Affiche les toasts du jeu, les fenêtres contextuelles du coin supérieur droit pour les avancements, les recettes débloquées, les conseils du tutoriel et les avis système, y compris ceux des autres mods. Désactivé, aucun n'est affiché. Prend effet au prochain monde ou serveur rejoint. Un modèle de monde peut lister à la place les types à afficher |
| `pregenOnNewWorld` | entier, 0 à 8192 | `0` | Jusqu'où autour du point d'apparition, en chunks, un monde voit son terrain créé avant que quiconque y joue. Le jeu en crée 12 autour du point d'apparition de lui-même : 12 est donc le plancher et 0 désigne ce plancher plutôt que rien : le terrain que le jeu allait créer de toute façon est adopté et éclairé en une seule passe organisée au lieu d'arriver au compte-gouttes. Augmentez-le pour aller plus loin que le jeu |
| `pregenToBorder` | booléen | `false` | Indique si un nouveau monde voit son terrain créé jusqu'à sa bordure de monde au lieu d'un nombre fixe de chunks, centré sur la bordure plutôt que sur le point d'apparition. Un monde dont la bordure n'a jamais été resserrée n'a pas de bordure à atteindre et est ignoré |
| `pregenAllDimensions` | booléen | `false` | Crée le terrain de chaque dimension que contient le serveur, y compris celles des mods, l'overworld d'abord et les autres par ordre d'identifiant, au lieu des seules dimensions de pregenDimensions. Celles nommées dans pregenDimensionsWhenEntered restent réservées à leur premier visiteur |
| `pregenResume` | booléen | `false` | Indique si une exécution arrêtée ou interrompue reprend où elle s'était arrêtée au prochain chargement du monde, plutôt que de repartir de zéro |
| `pregenChunksInFlight` | entier, 1 à 512 | `32` | Combien de chunks une exécution de création de terrain demande au jeu à la fois. Plus, c'est garder les threads de génération plus occupés et le serveur moins réactif pour celui qui est retenu en attente |
| `pregenBackup` | booléen | `false` | Copie le monde dans une sauvegarde intacte une fois la prégénération terminée, tant que les joueurs sont encore retenus. La copie est ce qu'une réinitialisation restaurerait, et une copie dont les packs ne correspondent plus est jetée et refaite |
| `resetClearsEntities` | booléen | `true` | Supprime toutes les entités qui ne sont pas des joueurs quand la carte est réinitialisée |
| `resetClearsScores` | booléen | `true` | Remet à zéro chaque objectif que tient le pack quand la carte est réinitialisée, pour qu'une nouvelle partie reparte de zéro. Les équipes elles-mêmes sont conservées |
| `resetClearsInventory` | booléen | `false` | Vide l'inventaire de chaque joueur, armure et main secondaire comprises, quand la carte est réinitialisée |
| `resetClearsExperience` | booléen | `false` | Remet l'expérience de chaque joueur au niveau zéro quand la carte est réinitialisée |
| `spawnChunkRadius` | entier, 0 à 1024 | `128` | À quelle distance du point d'apparition, en blocs, les chunks sont maintenus chargés, qu'un joueur soit là ou non, arrondie en chunks entiers selon (blocs + 8) / 16 de chaque côté : 128 maintient donc 8 chunks de chaque côté. Au démarrage d'un monde, l'overworld prépare un carré plus large de 4 chunks de chaque côté avant que le serveur soit prêt, soit 25 sur 25 chunks à 128. 0 n'en prépare ni n'en maintient aucun, et la zone d'apparition se décharge comme n'importe où ailleurs. RDPL maintient les chunks avec ses propres tickets : en 1.21.1, la règle de jeu spawnChunkRadius ne fait donc rien tant que cette clé est en vigueur |
| `spawnChunkRadii` | liste | vide | Un rayon pour l'overworld écrit sous la forme dimension=blocs, comme dans minecraft:overworld=64, qui prend le pas sur spawnChunkRadius. Seul l'overworld a des chunks de spawn : une entrée pour toute autre dimension ne change rien |
| `welcomeSays` | liste | `[WELCOME]` | Lignes de bienvenue, affichées en vert à chaque connexion et après la prégénération. Une entrée nue est la ligne valable partout ; une entrée dimension=message la remplace pour cette dimension et accueille aussi chaque arrivée dans celle-ci, p. ex. minecraft:the_nether=Welcome to the Nether!. Un message vide après le = rend cette dimension muette ; une liste vide n'affiche rien. Laissée à cette valeur par défaut, elle parle la langue de chaque joueur |
| `pregenBorderLimit` | entier, 1 à 1875000 | `8192` | La limite la plus lointaine qu'une bordure peut atteindre, en chunks de chaque côté, avant que la création du terrain jusqu'à elle soit refusée. Elle sert à empêcher une erreur de tourner pendant des semaines, pas à être relevée, et un pack ne peut pas la définir. Un carré de 8192 contient 268 millions de chunks **Config uniquement.** |
| `pregenDimensions` | liste | `["minecraft:overworld"]` | Les dimensions dans lesquelles un nouveau monde voit son terrain créé, par identifiant, dans l'ordre indiqué, l'une après l'autre |
| `pregenDimensionsWhenEntered` | liste | vide | Les dimensions dont le terrain est créé non pas d'avance mais la première fois que quelqu'un y met le pied, avec la même portée, en retenant tout le monde de la même façon jusqu'à la fin. Une dimension nommée ici et dans pregenDimensions est simplement créée d'avance |
| `pregenRunningSays` | texte | `World pregeneration running, %d%% done` | Le message de progression que voient les joueurs pendant la génération du monde, où %d est le pourcentage et un second %s la dimension. Vide, ils ne reçoivent aucun message. Laissé à cette valeur par défaut, il parle la langue de chaque joueur |
| `pregenFinishedSays` | texte | `World pregeneration finished` | Le message que voient les joueurs quand la génération se termine. Vide, ils ne reçoivent aucun message. Laissé à cette valeur par défaut, il parle la langue de chaque joueur |
| `pregenStoppedSays` | texte | `World pregeneration stopped` | Le message que voient les joueurs quand la génération est arrêtée avant la fin. Vide, ils ne reçoivent aucun message. Laissé à cette valeur par défaut, il parle la langue de chaque joueur |
| `pregenSpectatingSays` | texte | `Spectating until the world is ready` | Le message au milieu de l'écran que voient les joueurs tant qu'ils sont retenus en spectateur pendant la génération du monde. Vide, rien n'est affiché. Laissé à cette valeur par défaut, il parle la langue de chaque joueur |
| `pregenLogo` | texte | `center` | L'emplacement du logo à la fin de la prégénération : left, center ou right, au-dessus du texte du milieu de l'écran. Il est toujours affiché ; un mot inconnu est lu comme center |
| `pregenBackupSays` | texte | `Pack requested world backup` | Le message au milieu de l'écran que voient les joueurs pendant la copie de cette sauvegarde. Vide, rien n'est affiché |
| `resetSays` | texte | `Pack requested map reset` | La ligne au milieu de l'écran montrée aux joueurs pendant que /rdpl reset remet la carte en place. Vide, la réinitialisation se fait en silence |
| `resetSendsTo` | texte | `spawn` | L'endroit où les joueurs sont placés par une réinitialisation : spawn, une position sous la forme x,y,z, ou dimension:x,y,z pour les envoyer dans un autre monde |
| `resetRuns` | texte | vide | Une fonction exécutée une fois que la réinitialisation a vidé la carte, nommée namespace:path. Vide, rien n'est exécuté |

### Monde du vide

*ce que fait chaque groupe*

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

`control.voidWorld` décide de ce groupe : la génération d'un monde du vide et sa plateforme.

| Paramètre | Type | Défaut | Rôle |
| --- | --- | --- | --- |
| `voidWorld` | booléen | `false` | Génère les dimensions listées comme un espace vide avec une plateforme au point d'apparition et rien de vivant, via le préréglage généré pour les trois dimensions vanilla et via les fichiers de dimension propres à un pack ; toute autre dimension listée est vidée au fur et à mesure que son terrain est créé. Le choix est conservé avec le monde à sa création : l'activer ou le désactiver plus tard laisse un monde existant tel qu'il était |
| `voidWorldDimensions` | liste | `["minecraft:overworld"]` | Les dimensions rendues vides, par identifiant. Vide, cela signifie aucune, ou toutes les dimensions quand voidWorldDimensionsAreBlacklist est activé |
| `voidWorldDimensionsAreBlacklist` | booléen | `false` | Activé, les dimensions listées sont celles qu'on laisse intactes |
| `voidPlatformBlock` | bloc | `minecraft:stone` | De quoi la plateforme est faite |
| `voidPlatformHeight` | entier, -2032 à 2031 | `64` | Le y auquel se trouve la plateforme du monde du vide |
| `voidPlatformSize` | entier, 1 ou plus | `9` | La largeur de la plateforme, arrondie à l'impair inférieur pour qu'elle soit centrée sur le point d'apparition |
| `voidWorld` | texte | `default` | Génération d'un monde du vide et sa plateforme [default\|global\|off] |

### Le dragon

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "dragonFight": true
  }
}
```

| Paramètre | Type | Défaut | Rôle |
| --- | --- | --- | --- |
| `dragonFight` | booléen | `true` | Détermine si l'ensemble a lieu : le dragon, sa barre, les cristaux, la fontaine sur laquelle il se tient, et la réapparition qu'un joueur déclenche avec des cristaux de l'End. Appartient au groupe `structures` |

`dragonFight` appartient au groupe `structures` et détermine si l'ensemble a lieu : le dragon, sa barre, les cristaux, la fontaine sur laquelle il se tient, et la réapparition qu'un joueur déclenche avec des cristaux de l'End. Un End vidé ne l'a pas sauf demande d'un pack, et un End ordinaire l'a sauf avis contraire d'un pack : il vaut donc la peine de définir `dragonFight` dans un sens comme dans l'autre.

### Terrain

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldSeed": "Hollow Ridge",
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

`control.terrain` décide de ce groupe : le nom et la graine du monde à la création, generatorOptions, les régions de grottes, la hauteur des nuages et les jointures entre mondes.

| Paramètre | Type | Défaut | Rôle |
| --- | --- | --- | --- |
| `worldSeed` | chaîne | vide | La graine avec laquelle chaque nouveau monde est créé, écrite comme on la saisirait : un nombre est utilisé tel quel, et tout le reste est converti en nombre comme le fait le jeu. Un serveur dédié crée aussi son monde avec elle et l'écrit dans `server.properties` sous `level-seed`. Vide, le choix est laissé libre |
| `worldName` | texte | vide | Le nom d'un nouveau monde à l'ouverture de l'écran de création. Vide, il garde le nom que lui donne le jeu |
| `worldType` | texte | vide | Le type de monde sur lequel le monde façonné est construit, parmi default, largebiomes, amplified ou flat, les noms 1.12.2 customized et default_1_1 étant lus comme default ; flat est un overworld superplat construit à partir des couches de generatorOptions, avec les villes du pack par-dessus. La forme décrite plus bas (hauteurs, pierre profonde, niveau de la mer, bedrock, vide) est générée comme un préréglage de monde à part, listé sous Type de monde sur l'écran du monde et choisi là quel que soit ce qui avait été sélectionné. Un serveur dédié l'écrit dans `server.properties` sous `level-type`, en nommant ce préréglage, ou le préréglage propre au jeu quand rien n'est façonné, sauf si `level-type` nomme déjà l'un des worldTypeExceptions. Vide, la construction se fait sur default |
| `worldTypeExceptions` | liste | `["flat", "debug_all_block_states"]` | Les types de monde choisis par un joueur que le préréglage généré laisse tranquilles, comme flat ou debug_all_block_states. Vide, tous les choix sont remplacés |
| `generatorOptions` | texte | vide | Les paramètres de terrain de l'overworld sous forme d'objet JSON, les clés qu'écrivait le type de monde customized de la 1.12.2. Sont lues ici : seaLevel, useLavaOceans, fixedBiome, ainsi que useCaves, useRavines, useDungeons, useLavaLakes, useStrongholds, useVillages, useMineShafts, useTemples, useMonuments et useMansions mis à false. Avec worldType flat, ce sont à la place les couches, de bas en haut, comme le texte superplat de la 1.12.2 3;minecraft:bedrock,59*minecraft:stone,4*minecraft:dirt,minecraft:grass_block;1;village ou une liste de couches ; le nombre après les couches est le biome, et village, biome_1, mineshaft, stronghold, oceanmonument, lava_lake et decoration après lui les activent. Appliqué à un monde uniquement à sa création. Un serveur dédié l'écrit dans `server.properties` sous `generator-settings`, les couches plates sous forme du JSON plat propre au jeu, sauf si `level-type` nomme déjà l'un des worldTypeExceptions. Vide, le terrain reste tel que le type de monde le produit |
| `worldMinHeight` | entier, -2032 à 2016 | `-64` | Le bloc le plus bas de l'overworld, un multiple de 16 jusqu'à -2032. Le fond du jeu est à -64 ; plus bas, cela donne un monde profond sous le terrain vanilla, de la pierre pleine jusqu'à ce que la couche de worldgen la creuse ou que noiseCaves prolonge les grottes du jeu vers le bas. Appliqué uniquement via le préréglage généré |
| `worldMaxHeight` | entier, -2016 à 2032 | `320` | Le bloc au-dessus du sommet de l'overworld, un multiple de 16 jusqu'à 2032, à au plus 4064 au-dessus de worldMinHeight. Le sommet du jeu est à 320 ; plus haut, cela laisse un ciel ouvert au-dessus du terrain vanilla |
| `deepStone` | texte | vide | Le bloc dont est fait le monde sous le terrain vanilla quand worldMinHeight descend sous -64, comme l'ardoise des abîmes propre à un pack. Il se fond dans l'ardoise des abîmes sur les huit couches sous -64, comme celle-ci se fond dans la pierre. Vide, la pierre est conservée |
| `noiseCaves` | texte | `off` | Où les grottes, tunnels, nouilles et aquifères du jeu se poursuivent quand worldMinHeight descend sous -64 : off garde le monde sous le terrain vanilla en pierre profonde pleine que la couche de worldgen creusera, deep les prolonge jusqu'au fond avec les lacs de lave déplacés dans ses dix couches du bas, world signifie la même chose dans cette version parce que le terrain vanilla les contient déjà |
| `worldSpawn` | texte | vide | L'endroit où chaque nouveau monde apparaît, écrit x,z ou x,y,z. Sans y, on utilise le niveau moyen du sol du monde, un bloc au-dessus du niveau de la mer, ou le sommet des couches dans un monde plat, et le jeu y trouve ensuite un appui sûr comme pour tout point d'apparition. Appliqué à un monde uniquement à sa création. Vide, le choix est laissé au jeu |
| `worldBorder` | entier, 0 à 60000000 | `0` | La largeur, en blocs, de la bordure du monde dans chaque nouveau monde. Appliquée à un monde uniquement à sa création. 0 laisse la bordure là où le jeu la place |
| `worldTime` | entier, -1 à 23999 | `-1` | Verrouille l'heure du jour de l'overworld, en ticks, la même valeur que prend /time set : 18000 est donc minuit. L'horloge s'arrête et ne bouge plus : /time set ne peut pas la déplacer, le jour continue de compter en dessous, et retirer le paramètre rend cette heure. -1 laisse le temps s'écouler |
| `caveRegionPlainWeight` | entier, 0 ou plus | `4` | Le poids du sous-sol ordinaire, sans région, face aux poids propres des régions de grottes. Plus il est élevé, plus le sous-sol reste dépourvu de région |
| `caveRegionCells` | entier, 16 ou plus | `128` | La largeur d'une cellule de région de grottes en blocs. Les régions de grottes des packs sont peintes sur le sous-sol par cellules de cette taille environ |
| `caveRegionCellsY` | entier, 16 ou plus | `64` | La hauteur d'une cellule de région de grottes en blocs |
| `worldGravity` | liste | vide | Met la gravité à l'échelle, comme multiplicateur du vanilla où 1.0 ne change rien et 0.17 évoque la Lune. Concerne les joueurs, les créatures, les objets jetés, les blocs qui tombent, les flèches, les projectiles, la TNT et les orbes d'expérience. Une valeur nue concerne toutes les dimensions, et une entrée écrite dimension=valeur concerne cette seule dimension et l'emporte sur la valeur nue. Vide, la gravité reste inchangée |
| `worldFallDamage` | liste | vide | Met les dégâts de chute à l'échelle de la même façon, 0.5 les divisant par deux et 2.0 les doublant |
| `worldJumpStrength` | liste | vide | Met la force de saut à l'échelle de la même façon, 1.5 sautant une fois et demie plus haut |
| `worldTerminalVelocity` | liste | vide | Met à l'échelle la vitesse maximale de chute d'une créature ou d'un joueur de la même façon, 0.5 tombant à la moitié de la vitesse maximale du vanilla |
| `weatherCeiling` | liste | vide | Le y le plus haut qu'atteignent la pluie et la neige, sous forme d'entrées dimension=y. Un nombre nu concerne toutes les dimensions. Au-dessus, la pluie ne tombe pas, la neige ne se dépose pas, les chaudrons ne se remplissent pas, la foudre ne frappe pas et aucune précipitation n'est dessinée ; en dessous, la météo est inchangée. La glace dépend de la température et non des précipitations : elle se forme donc encore au-dessus de la ligne. Vide, il n'y a pas de plafond |
| `cloudHeight` | liste | vide | Le y auquel les nuages sont dessinés, sous forme d'entrées dimension=y. Un nombre nu concerne toutes les dimensions. Il l'emporte sur le `cloudHeight` propre à la dimension d'un pack. Vide, la hauteur de nuages du jeu est conservée, 192 dans l'overworld |
| `worldBelow` | liste | vide | Empile une autre dimension sous celle-ci : tomber par le bas du monde vous emmène dans la dimension nommée, où vous arrivez sous son plafond aux mêmes x et z, en continuant de tomber. Les entrées s'écrivent dimension=cible, comme minecraft:overworld=minecraft:the_nether pour suspendre le Nether sous l'overworld ; un identifiant nu concerne toutes les dimensions. Creuser jusqu'au bout exige que le bedrock du sol soit omis, ce que décide worldSeamBedrock. Vide, le sol reste le sol |
| `worldAbove` | liste | vide | Pareil pour le plafond : s'élever au-dessus du sommet du monde vous emmène dans la dimension nommée, où vous arrivez au-dessus de son sol. S'écrit comme worldBelow |
| `worldSeamEntities` | booléen | `true` | Indique si les objets jetés, les créatures et les autres entités franchissent aussi les jointures entre mondes, ou seulement les joueurs. Cavaliers et montures traversent un par un |
| `worldSeamBedrock` | booléen | `false` | Conserve malgré tout le bedrock à la limite d'une jointure. Désactivé, une dimension dont le sol ou le plafond porte une jointure worldBelow ou worldAbove ne génère pas de bedrock à cet endroit, de sorte que le passage peut être creusé. Les chunks déjà générés gardent ce qu'ils ont |

### Serveur

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldGameMode": "creative",
    "worldDifficulty": ["normal", "minecraft:the_nether=hard"],
    "worldLanCommands": false,
    "privacy": true,
    "worldForceGameMode": true,
    "worldPvp": false,
    "worldFlight": true,
    "worldSpawnProtection": 0,
    "worldNether": false,
    "worldCommandBlocks": true,
    "worldIdleTimeout": 30,
    "worldMotd": "Ruby World",
    "worldMaxSize": 10000,
    "worldStructures": true,
    "worldSpawnMonsters": true,
    "worldSpawnAnimals": true,
    "worldSpawnNpcs": false,
    "worldViewDistance": 12,
    "worldSimulationDistance": 8
  }
}
```

`control.server` décide de ce groupe : les lignes de `server.properties` qu'un pack peut définir, avec le mode de jeu, la difficulté et les commandes sur un monde ouvert au réseau local. Sur un serveur dédié, chaque valeur qu'un pack définit ici est écrite dans `server.properties` au démarrage du serveur, si bien que le fichier indique ce qui est en vigueur, et celles que le serveur a déjà lues lui sont appliquées elles aussi. Un monde solo prend ce que possède un serveur intégré, comme l'indique chaque ligne. Vide, ou `-1` pour un nombre, laisse la valeur propre au serveur, et avec `control.server` sur `off`, chaque ligne reste telle que le serveur la tient.

| Paramètre | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `worldGameMode` | texte | vide | Dans quel mode chaque nouveau monde est démarré, parmi survival, hardcore, creative, adventure ou spectator. Hardcore est le mode survie où la mort met fin au monde, pour toute la sauvegarde, comme le choix de l'écran du monde. Vide laisse le mode choisi par celui qui a créé le monde. L'écran du monde ne propose que survival, hardcore et creative, donc adventure et spectator se définissent à la création du monde. Un serveur dédié règle chaque monde sur le mode de son server.properties à chaque démarrage, si bien que le mode du pack y est écrit dans server.properties (gamemode et hardcore) avant le chargement du monde |
| `privacy` | booléen | `true` | Indique si la télémétrie et le signalement de chat du jeu sont désactivés : aucun événement de télémétrie n'est envoyé, le client ne signe aucun message de chat, le serveur ne garde aucune session de chat et n'en exige pas, donc aucun message ne peut être signalé. Un pack qui l'omet reçoit l'option de configuration `privacy` de la catégorie `tweaks`. Prend effet au prochain monde ou serveur rejoint |
| `worldLanCommands` | booléen | `true` | Indique si un joueur qui ouvre un monde solo au réseau local peut activer les commandes pour tous ceux qui le rejoignent. `false` grise le bouton Autoriser les triches de l'écran Ouvrir au réseau local et le maintient désactivé, et le monde est ouvert sans commandes quelle que soit la façon dont on le demande, `/publish` compris |
| `worldDifficulty` | liste | vide | Verrouille la difficulté, parmi peaceful, easy, normal ou hard. Une difficulté seule couvre toutes les dimensions, et une entrée écrite sous la forme dimension=difficulté, comme minecraft:the_nether=hard, couvre cette dimension seule et l'emporte sur la difficulté seule. Le réglage propre au monde reste tel qu'il était et revient quand l'entrée est retirée. Un serveur dédié écrit la difficulté de l'Overworld dans `server.properties` sous `difficulty`. Vide la laisse telle qu'elle a été choisie |
| `worldForceGameMode` | booléen | vide | Indique si un joueur qui se connecte est remis dans le mode de jeu du serveur à chaque fois, la ligne `force-gamemode`. Un monde ouvert au réseau local le fait déjà, et `false` l'arrête là aussi |
| `worldPvp` | booléen | vide | Indique si les joueurs peuvent se blesser entre eux, la ligne `pvp`. Un monde solo la prend en compte aussi |
| `worldFlight` | booléen | vide | Indique si un joueur qui vole en survie est laissé tranquille au lieu d'être expulsé, la ligne `allow-flight`. Un monde solo la prend en compte aussi |
| `worldSpawnProtection` | entier, -1 ou plus | `-1` | Le nombre de blocs autour du point d'apparition dans lesquels seuls les opérateurs peuvent construire, la ligne `spawn-protection`, 0 pour aucun. Seul un serveur dédié protège son point d'apparition |
| `worldNether` | booléen | vide | Indique si le Nether est accessible, la ligne `allow-nether`. `false` le ferme aussi dans un monde solo |
| `worldCommandBlocks` | booléen | vide | Indique si les blocs de commande s'exécutent, la ligne `enable-command-block`. Un monde solo les exécute déjà, et `false` les désactive là aussi |
| `worldIdleTimeout` | entier, -1 ou plus | `-1` | Le nombre de minutes pendant lesquelles un joueur peut rester inactif avant d'être expulsé, la ligne `player-idle-timeout`, 0 pour jamais. Un monde solo la prend en compte aussi |
| `worldMotd` | texte | vide | La ligne affichée sous le nom du serveur dans la liste des serveurs, la ligne `motd`. Un monde solo ouvert au réseau local l'affiche à la place du propriétaire et du nom du monde |
| `worldMaxSize` | entier, -1 à 29999984 | `-1` | La distance maximale, en blocs depuis le centre, que la bordure d'un monde peut jamais atteindre, la ligne `max-world-size`. Un monde solo la prend en compte aussi |
| `worldStructures` | booléen | vide | Indique si un nouveau monde génère des structures, la ligne `generate-structures` et le choix Générer des structures de l'écran du monde. Appliqué à un monde uniquement lors de sa création |
| `worldSpawnMonsters` | booléen | vide | Indique si les monstres hostiles apparaissent, la ligne `spawn-monsters`. `false` les arrête aussi dans un monde solo |
| `worldSpawnAnimals` | booléen | vide | Indique si les animaux apparaissent, la ligne `spawn-animals`. `false` les arrête aussi dans un monde solo |
| `worldSpawnNpcs` | booléen | vide | Indique si les villageois apparaissent, la ligne `spawn-npcs`. `false` les arrête aussi dans un monde solo |
| `worldViewDistance` | entier, -1 à 32 | `-1` | À combien de chunks de distance un serveur dédié envoie le monde à chaque joueur, la ligne `view-distance`. Un monde solo suit plutôt la distance d'affichage |
| `worldSimulationDistance` | entier, -1 à 32 | `-1` | À combien de chunks de distance un serveur dédié fait vivre le monde autour de chaque joueur, la ligne `simulation-distance`. Un monde solo suit plutôt son propre réglage |

### Recettes

*ce que fait chaque groupe*

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

`control.recipes` décide de ce groupe. Blocage des recettes de fabrication et de four, et leurs listes blanches. Le blocage des recettes de four emporte avec lui celles du haut fourneau, du fumoir et du feu de camp, puisque la 1.12.2 gardait toutes les recettes de cuisson dans la seule liste du four. Les recettes de la scie à pierre et de la forge ne sont jamais bloquées, puisque la 1.12.2 n'en avait pas.

| Paramètre | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `blockRecipes` | booléen | `false` | Supprime toutes les recettes de fabrication, sauf celles des mods de `recipeWhitelist`. Rien n'est exempté par défaut, donc indiquez l'espace de noms de votre propre pack pour conserver ses recettes |
| `recipeWhitelist` | liste d'ids de mod | `["minecraft"]` | Les mods dont les recettes de fabrication survivent |
| `blockedRecipeMods` | liste d'ids de mod | vide | Les mods dont les recettes de fabrication sont supprimées d'office, quoi que dise la liste blanche |
| `recipeMatch` | `recipe`, `output` ou `both` | `recipe` | D'où est lu l'id du mod quand les recettes de fabrication sont bloquées : le nom de la recette elle-même, l'objet qu'elle produit, ou l'un ou l'autre, ce qui bloque quand l'un des deux correspond et épargne quand l'un des deux est sur liste blanche |
| `blockFurnaceRecipes` | booléen | `false` | Même chose pour les recettes de four, le mod étant lu d'après l'objet produit |
| `furnaceWhitelist` | liste d'ids de mod | `["minecraft"]` | Les mods dont les recettes de four survivent |
| `blockedFurnaceMods` | liste d'ids de mod | vide | Les mods dont les recettes de four sont supprimées d'office |
| `logBlockedRecipes` | booléen | `true` | Journalise un décompte par mod de ce qui a été bloqué |
| `furnace` | booléen | `true` | Applique les fichiers furnace/*.json, qui ajoutent et retirent des recettes de cuisson au four **Configuration uniquement.** |
| `removals` | booléen | `true` | Applique les fichiers recipe_removals/*.json, qui suppriment des recettes de fabrication par nom, espace de noms ou résultat **Configuration uniquement.** |
| `skipMissingItems` | booléen | `true` | Ignore les recettes qui utilisent un objet non enregistré, au lieu de les laisser échouer. Le décompte est journalisé une seule fois **Configuration uniquement.** |

### Commandes

*ce que fait chaque groupe*

`control.commands` décide de ce groupe. Qui peut exécuter les commandes propres au mod : les niveaux de permission de goto.

| Paramètre | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `gotoLevel` | entier, 0 à 4 | `3` | Le niveau de permission requis pour /rdplserver goto <name>, qui emmène l'expéditeur vers la structure la plus proche. 3 correspond à un opérateur, le niveau où se situe tout le reste de la commande. 2 permet aussi à un bloc de commande de l'exécuter, de sorte qu'un pack peut placer le saut sur un bouton ou une plaque de pression sans donner à personne le reste de la commande. 0 laisse n'importe quel joueur la saisir. Les autres parties de /rdplserver restent à 3 quoi que dise ce réglage |
| `gotoNextLevel` | entier, 0 à 4 | `3` | Le niveau de permission pour /rdplserver goto <name> next, qui passe celle où il a emmené l'expéditeur en dernier et en trouve une autre. Même échelle que gotoLevel |
| `gotoBackLevel` | entier, 0 à 4 | `3` | Le niveau de permission pour /rdplserver goto <name> back, qui ramène l'expéditeur vers la précédente. Même échelle que gotoLevel |
| `gotoPlaceLevels` | liste | vide | Des niveaux de permission pour des lieux précis, sous forme d'entrées nom=niveau, une par ligne, qui remplacent les trois réglages ci-dessus pour ce lieu seul et dans ses trois formes. Le nom est ce que vous taperiez après goto, donc un nom vanilla comme Village ou Mansion, ou un nom qu'un pack a enregistré pour ses propres structures avec locateAs. Même échelle : 3 un opérateur, 2 aussi un bloc de commande, 0 n'importe qui. Un pack peut ainsi ouvrir la voie vers ses propres ruines tout en gardant fermées toutes les structures vanilla, ou l'inverse. Un nom que rien n'a enregistré est ignoré, avec une note dans le journal |

### Worldgen, configuration uniquement

*ce que fait chaque groupe*

Ces clés `worldgen` n'appartiennent à aucun groupe et relèvent de la configuration seule.

| Paramètre | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `worldgenDebug` | booléen | `false` | Écrit dans logs/rdpl.log les lignes de débogage auxquelles renvoient d'autres messages, comme quel pack a servi un fichier et ce que chaque commande a fait. Très verbeux |
| `worldTemplate` | texte | `auto` | Les réglages de quel modèle de monde s'appliquent. Un pack en ajoute un dans worldtemplates/*.json et vous le nommez ici sous la forme espace_de_noms:nom. 'auto' choisit le modèle du pack de plus haute priorité. Vide n'en utilise aucun |
| `tellWorldType` | booléen | `true` | Indique dans le chat à un joueur qui rejoint un monde créé avec le préréglage généré quel modèle l'a façonné. Un pack ne peut pas le définir |
| `worldBorderLimit` | entier, 1 à 60000000 | `60000000` | La bordure la plus large qu'un pack a le droit de demander via worldBorder. Un pack qui en demande plus est refusé et la bordure reste là où le jeu la met. Un pack ne peut pas le définir |
| `retrogenKey` | texte | `0000` | Changez-la pour rendre de nouveau chaque chunk éligible à la rétrogénération, pour chaque entrée worldgen. Les nouveaux filons s'ajoutent à ce qui est déjà là |
| `retrogenChunksPerTick` | entier, 1 ou plus | `2` | Combien de chunks déjà générés rattraper par tick. Plus c'est élevé, plus c'est rapide, mais plus ça saccade |

### La catégorie `packs`

*ce que fait chaque groupe*

Comment les dossiers de packs sont trouvés et servis. Configuration uniquement.

| Paramètre | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `rootDirectory` | texte | `rdploader` | Dossier d'où les packs sont chargés, relatif au répertoire .minecraft. Un chemin absolu fonctionne aussi. Nécessite un redémarrage |
| `overrideResourcePacks` | booléen | `true` | Insère le pack d'assets au-dessus des packs de ressources sélectionnés par le joueur et des packs de données propres au monde. Un pack nommé RDPLO... prend toujours le dessus, RDPLN... jamais |
| `warnOnCaseMismatch` | booléen | `true` | Avertit quand un fichier ne correspond que parce que le système de fichiers ne distingue pas la casse. De tels packs ne fonctionnent pas sous Linux |
| `logContents` | booléen | `false` | Journalise chaque pack trouvé et le nombre de fichiers qu'il fournit |
| `traceUnresolvedVariables` | booléen | `false` | Journalise une trace de pile la première fois qu'un fichier dont le nom contient un '#' est demandé, en nommant ce qui l'a demandé |

### La catégorie `content`

*ce que fait chaque groupe*

Les blocs, objets, fluides et tout le reste que les packs définissent. Configuration uniquement.

| Paramètre | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `load` | booléen | `true` | Enregistre les blocs, objets, fluides, matériaux et onglets créatifs que les packs définissent, et charge leurs expositions. Nécessite un redémarrage |
| `vanillaClients` | booléen | `false` | Sert les clients vanilla purs : rien d'aucun pack n'est enregistré, ni blocs, ni objets, ni fluides, ni onglets créatifs, et aucune exposition n'est chargée, de sorte qu'un client sans le mod peut se connecter. Tout ce qui vit côté serveur seul s'applique toujours. Nécessite un redémarrage |
| `sounds` | booléen | `true` | Enregistre les événements sonores nommés par sounds/*.json, pour que les packs puissent fournir leur propre audio |
| `fuels` | booléen | `true` | Applique les fichiers fuels/*.json, qui donnent aux objets un temps de combustion au four |
| `potions` | booléen | `true` | Enregistre les effets et types de potion décrits par potions/*.json et potion_types/*.json dans les packs. Nécessite un redémarrage |
| `brewing` | booléen | `true` | Applique les fichiers brewing/*.json, qui ajoutent des recettes d'alambic |
| `villagers` | booléen | `true` | Enregistre les métiers de villageois décrits par villagers/*.json et applique les échanges de trades/*.json. Nécessite un redémarrage |
| `entities` | booléen | `true` | Enregistre les variantes d'entité décrites par entities/*.json dans les packs. Nécessite un redémarrage |
| `overrides` | booléen | `true` | Applique les fichiers overrides/<namespace>/<name>.json, qui modifient les propriétés de blocs, d'objets et de types de potion déjà existants, vanilla ou moddés |
| `disabled` | booléen | `true` | Applique les fichiers disabled/*.json, qui retirent des blocs et des objets du jeu : plus d'onglet créatif, d'entrée JEI, de recette, de butin, d'échange, de tag, de placement, d'utilisation ni de ramassage, et les piles qui en contiennent sont supprimées |
| `hardness` | booléen | `true` | Applique les fichiers hardness/*.json, qui donnent à un groupe de blocs un multiplicateur de temps de minage et de résistance aux explosions, tiré par position de bloc |
| `shovelPaths` | booléen | `true` | Laisse une pelle transformer les blocs marqués behavesAs path en chemin, et rétablir un chemin en étant accroupi |
| `shovelPathBecomes` | texte | vide | En quoi une pelle transforme ces blocs. Vide utilise le chemin de terre |
| `shovelPathReverts` | texte | vide | En quoi s'accroupir avec une pelle retransforme un chemin. Vide utilise la terre |
| `hoeTilling` | booléen | `true` | Laisse une houe labourer les blocs marqués behavesAs till |
| `hoeTillsInto` | texte | vide | En quoi une houe transforme ces blocs. Vide utilise la terre labourée |
| `caneMaxHeight` | entier, 1 à 255 | `3` | La hauteur à laquelle pousse la canne à sucre vanilla. Vanilla vaut 3. Les blocs de canne définis par un pack utilisent leur propre section growth et ignorent ce réglage |
| `cactusMaxHeight` | entier, 1 à 255 | `3` | Même chose pour le cactus vanilla |

### La catégorie `data`

*ce que fait chaque groupe*

Le butin, les fonctions et les noms de registre. Configuration uniquement.

| Paramètre | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `lootInjections` | booléen | `true` | Applique les fichiers loot_injections/*.json, qui ajoutent des pools à des tables de butin déjà existantes au lieu de remplacer la table entière |
| `playerLoot` | booléen | `true` | Applique les fichiers player_loot/*.json, qui tirent une table de butin à la mort d'un joueur et font tomber ce qu'elle produit, en plus de l'inventaire ou à sa place |
| `registryRemaps` | booléen | `true` | Applique les fichiers registry_remap, qui renomment une entrée de registre pour que les mondes sauvegardés avant le renommage conservent leurs blocs et objets au lieu de les perdre |
| `anvils` | booléen | `true` | Applique les fichiers anvils/*.json, qui permettent à une enclume de poser des enchantements nommés sur un objet contre un coût en niveaux, d'accorder un progrès quand il est retiré, et d'en interdire l'usage jusque-là |
| `blockDrops` | booléen | `true` | Applique les fichiers block_drops/*.json, qui ajoutent à ce qu'un bloc fait tomber quand il se brise, ou le remplacent, expérience comprise, pour les blocs dont le pack n'est pas propriétaire |
| `functions` | booléen | `true` | Charge les fichiers .mcfunction des packs, pour qu'ils fonctionnent dans tous les mondes |

### La catégorie `tweaks`

*ce que fait chaque groupe*

De petits changements dans le comportement de vanilla. Configuration uniquement, sauf `privacy`, qu'un pack peut aussi définir ; voir [Bonus : ajustements vanilla](#bonus--ajustements-vanilla).

| Paramètre | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `promptLeafDecay` | booléen | `true` | Les feuilles qui perdent leur arbre disparaissent en moins d'une seconde au lieu d'attendre les ticks aléatoires |
| `lenientPaths` | booléen | `true` | Des chemins peuvent être faits sous un bloc et y restent quand on en place un au-dessus |
| `unbreakableSpawners` | booléen | `false` | Les générateurs de monstres ne peuvent être ni minés ni détruits par une explosion. Le mode créatif les retire toujours. Nécessite un redémarrage |
| `experimentalWarning` | booléen | `false` | Affiche l'avertissement du jeu sur les réglages expérimentaux quand un monde est créé ou ouvert. Désactivé y répond comme si vous aviez cliqué sur continuer |
| `privacy` | booléen | `true` | Désactive la télémétrie et le signalement de chat du jeu : aucun événement de télémétrie n'est envoyé ni journalisé, le client ne signe aucun message de chat, le serveur ne garde aucune session de chat et n'en exige pas, donc aucun message envoyé par quiconque ne peut être signalé, et le client n'affiche aucune notification d'avertissement indiquant qu'un serveur n'impose pas le chat sécurisé. Un pack peut le définir sous la forme `privacy` dans les `settings` d'un modèle de monde, sous le groupe [Serveur](#serveur). Prend effet au prochain monde ou serveur rejoint |
| `darkSplash` | booléen | `true` | Dessine l'écran de chargement en sombre avec le logo du chargeur de packs à la place de celui du jeu : le logo est remplacé à la création de l'écran, et l'option Logo monochrome du jeu est activée si elle ne l'est pas encore, ce qui prend effet au prochain démarrage. Désactivé laisse l'option telle quelle |

---

# Autres mods

## Intégration de Blast Plaster

*autres mods*

`<namespace>/blastplaster/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers se cumulent.

Blast Plaster gère le comportement après une explosion : réparation des cratères bloc par bloc, abattage tenant compte des arbres, contrôle des drops. Seul, il lit une unique configuration globale. Piloté depuis un pack, il répond **par dimension**, et le pack fournit la décision au lieu de demander aux joueurs de modifier une configuration. Sans fichiers de pack, ou sans Blast Plaster installé, rien ici ne fait quoi que ce soit, et le dossier est ignoré avec une ligne dans le journal.

Les clés écrites en haut du fichier s'appliquent partout ; un bloc `dimensions` les remplace pour une dimension, par id. Tout ce qu'un pack ne nomme jamais garde ce que dit la configuration propre à Blast Plaster, de sorte qu'un pack définit les quelques clés qui l'intéressent et laisse le reste tranquille.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin.

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

`explosionMode` est l'interrupteur principal : `HEAL` restaure le cratère avec le temps, `EJECT_DROPS` laisse le trou et fait tomber environ un tiers des blocs (comportement vanilla), `VISUAL_TOSS` laisse le trou et ne fait rien tomber. Piloté par un pack, la valeur par défaut est `EJECT_DROPS` (et non le `HEAL` de Blast Plaster), de sorte qu'une installation non configurée se comporte comme vanilla.

| Clé | Valeur | Ce qu'elle fait |
| --- | --- | --- |
| `explosionMode` | `HEAL`, `EJECT_DROPS`, `VISUAL_TOSS` | Ce qui se passe après la détonation |
| `healCreepers`, `healNonPlayerTNT`, `healWither`, `healAll` | true ou false | Quelles explosions sont prises en charge |
| `processPlayerIgnitedTNT` | true ou false | Si le TNT allumé par un joueur est pris en charge avec le reste |
| `customEntitiesToHeal` | liste de noms d'entités | Les explosions venant d'autres mods, nommées sous la forme `modid:entity` |
| `healFullTrees` | true ou false | Un arbre entamé par une explosion est pris ou restauré en entier, plutôt que cisaillé |
| `maxTreeSize` | nombre | Le plus grand nombre de blocs qu'un arbre peut revendiquer avant d'être laissé tranquille |
| `minimumTicksBeforeHeal`, `randomTickVar` | nombres | Combien de temps avant que la réparation commence, et à quel point son rythme est irrégulier |
| `overrideBlocks` | true ou false | Si la réparation écrase ce qui a été construit depuis dans le trou |
| `enableFakeTossedBlocks` | true ou false | Les débris qui jaillissent de l'explosion |
| `enableExplosionFlash` | true ou false | L'éclair lumineux au moment de l'explosion |
| `explosionFlashDuration`, `explosionFlashLightLevel`, `explosionFlashParticleCount`, `explosionFlashPulses` | nombres | La durée de l'éclair, son intensité lumineuse, le nombre de particules qu'il projette et le nombre de fois qu'il pulse |
| `enableExplosionSmoke` | true ou false | La colonne de fumée qui suit |
| `explosionSmokeDuration`, `explosionSmokeParticleCount` | nombres | La durée pendant laquelle la fumée persiste et son épaisseur |
| `playerTNTAlwaysDrops`, `playerTNTDropFullBlocks` | true ou false | Ce que laisse derrière lui le TNT d'un joueur |
| `enableDropSuppression`, `dtSpecialDrops` | true ou false | Les drops à l'intérieur d'une explosion, et les drops propres à Dynamic Trees |
| `preventMobDrops` | true ou false | Si les mobs tués par une explosion font quand même tomber leur butin |
| `blockConversions` | liste de règles | En quoi un bloc soufflé se transforme au lieu de revenir tel qu'il était, de sorte qu'une construction s'use d'un cran à chaque explosion |

`blockConversions` décide en quoi un bloc soufflé se transforme au lieu de revenir tel qu'il était. Une règle se lit `<source>=<résultat>[@chance]` : la source est un id de bloc, ou un tag de blocs précédé de `#` ; le résultat est un id de bloc, ou `nothing` pour laisser l'espace vide ; la chance va de 0.0 à 1.0 et vaut 1.0 par défaut. La première règle qui correspond l'emporte, donc les règles précises passent avant les règles larges, et un bloc qui est déjà le résultat d'une règle n'est jamais converti de nouveau — un mur perd un cran à chaque explosion au lieu de s'éroder jusqu'à disparaître.

**Apparence entièrement vanilla :** `EJECT_DROPS` avec `healFullTrees`, `enableFakeTossedBlocks`, `enableExplosionFlash`, `enableExplosionSmoke`, `preventMobDrops` et `playerTNTAlwaysDrops` tous désactivés. Chaque clé peut être définie par dimension.

**Les clients vanilla** ne voient rien d'inhabituel. L'éclair est la seule fonctionnalité qui place un bloc, donc avec `vanillaClients` activé il est forcément désactivé ; tout le reste n'est que particules et objets qu'un client ordinaire comprend.

Ne sont pas des clés de pack : la journalisation de débogage de Blast Plaster et son appariement bûche-feuilles (l'identification des arbres doit avoir une seule réponse pour tout le jeu). Les deux restent dans la configuration propre à Blast Plaster.

---

# Référence

## Listes de valeurs

*référence*

### Noms acceptés

*listes de valeurs*

Ce sont les noms que l'analyseur accepte partout où les tableaux ci-dessus disent « l'un des matériaux », et ainsi de suite. Tout ce qui n'est pas reconnu est journalisé et remplacé par la valeur par défaut.

**Matériaux de bloc.** `air`, `grass`, `ground`, `wood`, `rock`, `iron`, `anvil`, `water`, `lava`, `leaves`, `plants`, `vine`, `sponge`, `cloth`, `fire`, `sand`, `circuits`, `carpet`, `glass`, `redstone_light`, `tnt`, `coral`, `ice`, `packed_ice`, `snow`, `crafted_snow`, `cactus`, `clay`, `gourd`, `dragon_egg`, `portal`, `cake`, `web`, `piston`, `barrier`, `structure_void`. Le jeu lui-même n'a plus de matériaux ; chaque nom fait ce que faisait ce matériau en 1.12.2 : il définit la couleur de carte, si le bloc exige un outil pour lâcher quoi que ce soit, comment les pistons le traitent, si la lave l'enflamme, si un liquide qui coule l'emporte et si un bloc posé le remplace.

**Types de son.** `wood`, `ground`, `plant`, `stone`, `metal`, `glass`, `cloth`, `sand`, `snow`, `ladder`, `anvil`, `slime`.

**Couleurs de carte.** `air`, `grass`, `sand`, `cloth`, `tnt`, `ice`, `iron`, `foliage`, `snow`, `clay`, `dirt`, `stone`, `water`, `wood`, `quartz`, `adobe`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `silver`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, `black`, `gold`, `diamond`, `lapis`, `emerald`, `obsidian`, `netherrack`.

**Couches de rendu.** `solid`, `cutout`, `cutout_mipped`, `translucent`. Laissé vide, le bloc en choisit une adaptée à son type.

**Raretés.** `common`, `uncommon`, `rare`, `epic`.

**Particules de torche.** `none`, `flame`, `colored`. `colored` utilise `particleColor`.

**Classes d'outils.** `pickaxe`, `axe`, `shovel`, `hoe`, `sword`.

**Emplacements d'armure.** `head` ou `helmet`, `chest` ou `chestplate`, `legs` ou `leggings`, `feet` ou `boots`.

**Teintes.** `biome`, `none`, ou une couleur hexadécimale à six chiffres. Les couleurs, partout dans une définition, sont en hexadécimal, avec ou sans `#` initial.

**Comportements** pour `behavesAs`. `till`, `path`, `bush`, `animals`.

**Types de plantes** pour `plantTypes`. `plains`, `desert`, `beach`, `cave`, `water`, `nether`, `crop`. 1.20.1 uniquement ; la 1.21.1 lit la clé et l'ignore.

**Types de biome**, les mots qui représentent un tag de biome partout où un tableau dit « liste de types de biome », dans `biomeTypes`, les `types` d'un biome, les `roles` d'un modèle et une section `biomes` : `ocean`, `deepocean`, `beach`, `river`, `mountain`, `mesa`, `hills`, `coniferous`, `jungle`, `forest`, `savanna`, `overworld`, `nether`, `end`, `hot`, `cold`, `sparse`, `dense`, `wet`, `dry`, `spooky`, `dead`, `lush`, `mushroom`, `magical`, `rare`, `plateau`, `modified`, `water`, `desert`, `plains`, `swamp`, `sandy`, `snowy`, `wasteland`, `void`. Les mots vanilla correspondent aux tags `minecraft:is_*` et les autres aux tags de convention, `forge:is_*` en 1.20.1 et `c:is_*` en 1.21.1. Un tag écrit en toutes lettres, `minecraft:is_forest` ou `#minecraft:is_forest`, est pris tel quel. La casse n'a pas d'importance, donc le `FOREST` de la 1.12.2 se lit toujours.

**Rôles** pour les `roles` d'un modèle de monde. N'importe quel mot de type de biome ci-dessus : chacun nomme un biome qui remplit les biomes portant ce tag une fois que le blocage les a retirés, de sorte que `"ocean": "mypack:ruby_ocean"` place l'océan de rubis partout où un océan a été bloqué.

**Structures** pour les `structures` d'un modèle de monde et pour les propres listes du groupe `structures` : les noms de la 1.12.2 `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges` et `endcities`, ou n'importe quel ensemble de structures fourni par le jeu ou un mod, comme `pillager_outposts`, `ancient_cities`, `trail_ruins`, `shipwrecks`, `ocean_ruins`, `ruined_portals`, `nether_fossils`, `buried_treasures`, `desert_pyramids`, `jungle_temples`, `igloos`, `swamp_huts`, `woodland_mansions`, `ocean_monuments`, `nether_complexes`, `end_cities`. Un nom de la 1.12.2 est lu comme les ensembles qu'il désignait, donc `temples` regroupe les pyramides, les temples de la jungle, les igloos et les huttes de sorcière. Les noms de peuplement de la 1.12.2 sont lus eux aussi, comme les parties du monde de cette version qu'ils désignent : `caves` les creuseurs de grottes (les grottes de bruit sont `noiseCaves`), `ravines` les canyons, `dungeons` les salles de monstres, `lavalakes` les lacs de lave, `netherlava` les sources de lave à ciel ouvert du Nether, `fire` les nappes de feu du Nether, `glowstone` sa pierre lumineuse, `ice` la couche supérieure gelée et `animals` les animaux placés à la création d'un chunk. `waterlakes` est accepté et ne fait rien, puisque cette version n'a pas de lacs d'eau.

**Types de créatures** pour les apparitions et taux de biome. `creature`, `monster`, `ambient`, `water`. L'apparition d'une variante d'entité accepte aussi, par leur nom, les autres listes de cette version, comme `water_ambient` ou `underground_water_creature`.

**Types de minerai** pour `oreTypes`. `COAL`, `IRON`, `COPPER`, `GOLD`, `REDSTONE`, `DIAMOND`, `LAPIS`, `EMERALD`, `QUARTZ`, `DIRT`, `GRAVEL`, `DIORITE`, `GRANITE`, `ANDESITE`, `TUFF`, `CLAY`, `SILVERFISH`, `CUSTOM` pour tout autre minerai.

**Types de dégâts** pour les `immuneTo` d'une variante d'entité, quelle que soit la casse et avec ou sans tirets bas. Les noms de la 1.12.2, chacun couvrant ce qu'il couvrait alors : `inFire` (un feu de camp aussi), `onFire` (une boule de feu que personne n'a tirée aussi), `lava`, `hotFloor`, `inWall` (la bordure du monde aussi), `cramming`, `drown`, `starve`, `cactus`, `fall`, `flyIntoWall`, `outOfWorld` (`/kill` aussi), `generic`, `magic`, `indirectMagic`, `wither`, `anvil`, `fallingBlock`, `dragonBreath`, `fireworks`, `lightningBolt`, `thorns`, `arrow`, `fireball`, `thrown`, `mob` pour le coup d'une créature, le crachat d'un lama, le projectile d'un shulker ou le crâne d'un wither, `player` pour le coup d'un joueur, `explosion` pour une explosion que personne n'a déclenchée, comme celle d'un lit, et `explosion.player` pour une explosion qu'une créature ou un joueur a déclenchée, un creeper ou du TNT allumé. Les dégâts propres à cette version ont eux aussi des noms : `fire` pour l'un ou l'autre type de brûlure, `lightning`, `void`, `freeze`, `dryOut` et `sweetBerry`. Tout le reste est lu comme un id de type de dégâts, `minecraft:sonic_boom` ou un id propre à un mod.

**Noms de sons** pour les `sounds` d'une variante d'entité, le `openSound` d'un coffre verrouillé et le `sound` d'un portail : tout événement sonore enregistré, celui du jeu, d'un mod ou d'un son qu'un pack ajoute via `sounds`. Un nom de la 1.12.2 est lu comme le nom que porte désormais ce son, donc `entity.endermen.scream` joue `entity.enderman.scream`, `block.cloth.step` joue `block.wool.step`, `entity.small_slime.squish` joue `entity.slime.squish_small` et `record.cat` joue `music_disc.cat`. Les quatre imitations de perroquet que cette version a abandonnées, celles de l'enderman, de l'ours polaire, du loup et du cochon zombie, ne jouent rien.

## Liste des dossiers

*référence*

Chaque dossier, avec son chemin complet et un lien vers la section qui le décrit, se trouve dans [Où vont les fichiers](#où-vont-les-fichiers).

## Commandes

*référence*

### Vos propres commandes

*commandes*

`/rdpl` s'exécute sur votre propre machine et ne demande aucune permission, car tout ce qu'il touche vous appartient. Un rechargement réanalyse le dossier qui est le vôtre et rafraîchit vos propres ressources ; il n'atteint aucun serveur, donc la copie du serveur se recharge avec `/rdplserver reload`. En solo, les deux ne font qu'une seule machine, si bien que `/rdpl reload` est aussi ce qui réapplique vos [surcharges de propriétés](#surcharges-de-propriétés) et reconstitue vos équipes.

| Commande | Niveau | Ce qu'elle fait |
| --- | --- | --- |
| `/rdpl list` | aucun | Chaque pack chargé, sa priorité et ce qu'il contient. Cliquez sur un pack pour y chercher un fichier |
| `/rdpl which <namespace:path>` | aucun | Quel pack fournit un fichier donné, et quels packs il masque |
| `/rdpl reload` | aucun | Réanalyse le dossier et recharge tout, y compris le rechargement des ressources du jeu |
| `/rdpl unused` | aucun | Les fichiers de vos packs que rien n'a encore demandés, généralement une faute de frappe dans un chemin |
| `/rdpl config unused` | aucun | Les fichiers d'options de `rdploader/config` qu'aucun pack installé ne définit plus |
| `/rdpl config prune` | aucun | Supprime ces fichiers |
| `/rdpl pixelmap <namespace:path>` | aucun | Ce qu'une [carte de pixels](#textures-écrites-sous-forme-de-cartes-de-pixels) a donné, caractère par caractère |
| `/rdpl biome`, `biome list [all]` | aucun | Chaque biome qui peut se générer, et son id ; `all` inclut ceux que rien ne peut générer |
| `/rdpl biome here` | aucun | Le biome dans lequel vous vous trouvez : son nom, son id et son numéro |
| `/rdpl biome find <name>` | celui du serveur | Liée. Transmise mot pour mot à `/rdplserver`, qui décide, voir donc le tableau ci-dessous |
| `/rdpl locate`, `goto`, `vein`, `gate`, `pregen`, `intro`, `team`, `round`, `dimensions`, `oregen`, `game` | celui du serveur | Liée. Transmise mot pour mot à `/rdplserver`, qui décide, voir donc le tableau ci-dessous |

**Quelles sous-commandes du serveur sont liées, et pourquoi les autres ne le sont pas.** `locate`, `goto`, `vein`, `gate`, `pregen`, `intro`, `team`, `round`, `dimensions`, `oregen` et `game` ne peuvent jamais désigner que le serveur, puisque seul le serveur connaît le monde, ses joueurs et ses manches, donc `/rdpl` les lui transmet. En solo, la complétion par tabulation après l'une d'elles propose ce que proposerait `/rdplserver` ; sur un serveur, `goto` propose les noms de structures vanilla. Les autres, `reload`, `list`, `which`, `unused`, `config`, `pixelmap` et `biome`, gardent leur propre sens, celui de vos packs et de votre client. La vérification de permission propre au serveur décide d'une commande liée, de sorte qu'un client ne peut ni la contourner ni se voir donner une réponse fabriquée.

**Édition au quotidien :** F3+T recharge les textures, les modèles et les fichiers de langue, et `/reload` les données du serveur. Utilisez `/rdpl reload` quand vous *ajoutez* ou *supprimez* un fichier, puisque cela change ce que contient le dossier.

### Commandes serveur

*commandes*

Sur un serveur dédié, `/rdplserver` fait la même chose pour la copie du dossier propre au serveur. La colonne Niveau est le niveau de permission dont un expéditeur a besoin : `3` correspond à un opérateur, `2` admet aussi les blocs de commande, `0` est n'importe quel joueur, et `4` est au-dessus d'opérateur et n'atteint personne.

#### Packs et fichiers

*commandes serveur*

| Commande | Niveau | Ce qu'elle fait |
| --- | --- | --- |
| `/rdplserver reload` | 3 | Réanalyse le dossier du serveur et recharge tout, puis reconstitue les équipes et les objectifs |
| `/rdplserver list` | 3 | Chaque pack que le serveur a chargé, sa priorité et ce qu'il contient |
| `/rdplserver which <namespace:path>` | 3 | Quel pack fournit un fichier donné, et quels packs il masque |
| `/rdplserver unused` | 3 | Les fichiers des packs du serveur que rien n'a demandés |
| `/rdplserver config unused` | 3 | Les fichiers d'options de `rdploader/config` qu'aucun pack installé ne définit plus |
| `/rdplserver config prune` | 3 | Supprime ces fichiers |
| `/rdplserver pixelmap <namespace:path>` | 3 | Ce qu'une carte de pixels a donné |

#### Monde et génération

*commandes serveur*

| Commande | Niveau | Ce qu'elle fait |
| --- | --- | --- |
| `/rdplserver oregen` | 3 | Totaux cumulés de la génération de minerai qui a été bloquée, par mod et par type |
| `/rdplserver generators` | 3 | Totaux cumulés des générateurs de monde qui ont été bloqués, par mod et par type |
| `/rdplserver biome list [all]` | 3 | Chaque biome qui peut se générer sur le serveur, avec son numéro, son id et son nom ; `all` inclut ceux que rien ne peut générer |
| `/rdplserver biome` | 3 | Le biome dans lequel vous vous trouvez et ce que le pack en fait : son id, son numéro et son nom, si `blockBiomes` est activé et quel modèle de monde est actif, ainsi que le sol, le bloc en dessous et la pierre à y 40 |
| `/rdplserver biome here [player]` | 3 | Le biome dans lequel vous vous trouvez, ou le joueur nommé : son nom, son id et son numéro. La console nomme un joueur |
| `/rdplserver biome find <name>` | 3 | L'endroit le plus proche, dans un rayon de 6400 blocs, où se génère un biome correspondant à cet id ou à ce nom affiché : ses coordonnées et la distance depuis l'endroit où la commande est exécutée. Le dit quand aucun n'est aussi proche, ou quand le nom ne correspond à aucun biome. `/rdpl biome find` lui transmet la commande |
| `/rdplserver dimensions` | 3 | Chaque dimension, y compris celles que les packs ont ajoutées |
| `/rdplserver vein <entry> [radius]` | 3 | Où une entrée worldgen de forme `vein` a ses filons semés, dans ce nombre de chunks (8 par défaut) autour de l'endroit où la commande est exécutée, les plus proches d'abord, que ces chunks existent déjà ou non. `/rdpl vein` lui transmet la commande |

#### Commandes de portail

*commandes serveur*

| Commande | Niveau | Ce qu'elle fait |
| --- | --- | --- |
| `/rdplserver gate`, `gate list` | 3 | Chaque portail, sa dimension, sa portée et s'il est ouvert |
| `/rdplserver gate check <player>` | 3 | Quels portails un joueur a franchis |
| `/rdplserver gate grant <player> <gate>` | 3 | Ouvre un portail pour un joueur |
| `/rdplserver gate revoke <player> <gate>` | 3 | Le referme |

#### Commandes de prégénération

*commandes serveur*

| Commande | Niveau | Ce qu'elle fait |
| --- | --- | --- |
| `/rdplserver pregen <radius>` | 3 | Génère chaque chunk dans ce nombre de chunks autour de l'endroit où la commande est exécutée. Voir [Prégénération](#prégénération) |
| `/rdplserver pregen status` | 3 | Où en est une exécution |
| `/rdplserver pregen stop` | 3 | Y met fin |

#### Joueurs, équipes et manches

*commandes serveur*

| Commande | Niveau | Ce qu'elle fait |
| --- | --- | --- |
| `/rdplserver intro` | 0 | Fait rejouer l'intro du monde à votre prochaine connexion. N'importe quel joueur peut l'exécuter, et elle ne réinitialise jamais que la sienne ; elle est refusée quand aucun pack n'a d'intro |
| `/rdplserver team`, `team join [name]`, `team leave`, `team vote <player>`, `team claim` | 0 | Les camps qu'un pack a constitués et les moyens de les rejoindre et de les quitter, proposés seulement tant qu'un pack constitue des camps, voir [Équipes](#équipes) |
| `/rdplserver round start`, `round reset`, `round vote yes`, `round vote no` | 0 | Démarre une manche que le pack garde dans un hall, réinitialise une manche en cours ou appelle à voter pour le faire, et y vote, selon ce que permet le score du pack, et seul un joueur en démarre une ; proposées aux joueurs seulement tant qu'un pack tient le score, voir [Score](#score) |
| `/rdplserver card <rule> [players]` | 2 | Montre une [règle de carte](#cartes) aux joueurs nommés, ou à vous-même, par son id ou son nom de fichier. `when`, `repeat` et `cooldown` sont ignorés |
| `/rdplserver reset` | 3 | Remet la carte comme le fait la fin d'une manche : tout le monde est retenu, les entités balayées, les scores effacés, les `resetRuns` exécutés, les joueurs placés à `resetSendsTo` puis libérés, et une manche s'ouvre avec le nombre de départ, comme le décrivent les réglages de réinitialisation sous [Prégénération](#prégénération). Non transmise depuis `/rdpl` |

#### Se rendre quelque part

*commandes serveur*

| Commande | Niveau | Ce qu'elle fait |
| --- | --- | --- |
| `/rdplserver locate <name>` | 3 | La structure la plus proche qu'un pack a placée sous ce nom `locateAs` |
| `/rdplserver goto <structure>` | `gotoLevel`, `3` | Vous emmène vers la plus proche où personne n'est encore allé, en cherchant sans générer le terrain en chemin. Un lieu qu'un pack a enregistré avec `locateAs` est le plus proche placé, visité ou non. `temple` désigne toutes les structures dispersées : temples du désert et de la jungle, huttes de sorcière et igloos. Refusée pendant que du terrain est en cours de création |
| `/rdplserver goto <structure> next` | `gotoNextLevel`, `3` | Vous emmène plus loin vers la plus proche où l'on ne vous a pas emmené pendant cette session, qu'elle ait déjà été visitée ou non. Une structure à moins de huit chunks de vous est passée ; pour le lieu d'un pack, c'est la plus proche à plus de 128 blocs |
| `/rdplserver goto <structure> back` | `gotoBackLevel`, `3` | Vous emmène vers celle d'avant, en revenant sur les lieux où cette session vous a envoyé |

#### Jeux

*commandes serveur*

| Commande | Niveau | Ce qu'elle fait |
| --- | --- | --- |
| `/rdplserver game coin` | 0 | Lancer une pièce. Face compte pour 1, pile pour 0 |
| `/rdplserver game die <sides>` | 0 | Lancer un dé de 2 à 1000 faces |
| `/rdplserver game die <name>` | 0 | Lancer un [dé du pack](#dés-et-paquets) selon ses poids |
| `/rdplserver game dice <roll>` | 0 | Lancer jusqu'à 100 dés et les additionner, comme `2d6`, `d20` ou `3d8-2`. Chaque dé est affiché |
| `/rdplserver game advantage [roll]`, `disadvantage [roll]` | 0 | Lancer deux fois et garder le total le plus haut, ou le plus bas. Sans précision, le lancer est `1d20` |
| `/rdplserver game pick player` | 0 | Tirer au sort un joueur connecté |
| `/rdplserver game pick team [team]` | 0 | Tirer au sort une équipe du tableau des scores, ou un membre connecté de l'équipe nommée |
| `/rdplserver game deck draw <name>` | 0 | Piocher une carte dans ce qui reste d'un paquet du pack |
| `/rdplserver game deck left <name>` | 0 | Combien de cartes il reste au paquet |
| `/rdplserver game deck shuffle <name>` | 2 | Remettre toutes les cartes |
| `/rdplserver game teamroll [roll]` | 0 | Chacun dans le camp de l'expéditeur lance et le plus haut gagne, une égalité tirée au sort. Sans équipes, l'expéditeur lance seul |
| `/rdplserver game tiebreak [objective]` | 2 | Tirer au sort l'un des camps à égalité en tête d'un objectif : celui nommé, sinon le premier objectif de score avec `tiebreak`, sinon le premier |
| `/rdplserver game last [count]` | 0 | Les derniers lancers, du plus récent au plus ancien : 10, ou le nombre donné jusqu'à 50 |

Tout lancer peut finir par `store <objective>`, qui écrit son nombre dans le score de l'expéditeur pour cet objectif, et par `audience <qui>`, qui remplace la valeur par défaut du pack : `self`, `team` (le camp de l'expéditeur, ou l'expéditeur seul sans équipes), `all`, `radius <blocs>` (les joueurs du même monde à cette distance) ou `silent`, qui ne fait qu'enregistrer. `/rdpl game` lui est transmis.

### Qui peut utiliser goto

*commandes*

**Ouvrir `goto`.** Chaque partie de `/rdplserver` demande un opérateur, niveau 3, sauf `intro`, `team` et `round`, que n'importe quel joueur peut exécuter, comme en 1.12.2, et `game`, dont les parties ont [leurs propres niveaux](#qui-peut-utiliser-game). Les trois formes de `goto` sont la seule chose qu'un pack décide : chacune porte son propre niveau de permission qu'un pack ou la configuration peut abaisser, séparément des deux autres et du reste de la commande. Un pack qui veut que `reset` soit accessible aux joueurs le place sur un bloc de commande ou dans une fonction, qui s'exécute au niveau 3.

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

| Paramètre | Ce qu'il régit |
| --- | --- |
| `gotoLevel` | `goto <structure>` |
| `gotoNextLevel` | `goto <structure> next` |
| `gotoBackLevel` | `goto <structure> back` |
| `gotoPlaceLevels` | Un lieu nommé, dans les trois formes |

La valeur est le niveau de permission dont un expéditeur a besoin. `3` (opérateur) est la valeur par défaut. `2` admet aussi les blocs de commande, de sorte qu'un pack peut placer un saut sur un bouton ou une plaque de pression sans exposer le reste de `/rdplserver`. `0` l'ouvre à n'importe quel joueur. Les trois réglages sont indépendants : par exemple, `next` ouvert aux blocs de commande pour une visite de village tandis que `back` reste réservé aux opérateurs. Une valeur inférieure à 0 compte pour 0 et une valeur supérieure à 4 pour 4, et un opérateur se voit toujours proposer `goto` lui-même, quoi que disent les réglages.

`gotoPlaceLevels` remplace les trois réglages pour des lieux précis, sous forme d'entrées `name=level`, comme dans l'exemple ci-dessus. Le nom est ce que vous taperiez après `goto` : un nom vanilla comme `village` ou `mansion`, ou un nom enregistré avec `locateAs` sur une entrée imprint. La correspondance ignore la casse. Un niveau de `4` est au-dessus d'opérateur et ferme ce lieu à tout le monde, la façon de cacher un lieu tandis que le reste de `goto` est ouvert.

Une entrée fixe un seul niveau pour les trois formes de ce lieu. Un lieu non listé retombe sur les trois réglages ci-dessus, et un nom non enregistré ne correspond jamais. La complétion par tabulation suit les mêmes règles, donc après `goto` un expéditeur ne se voit proposer que les lieux où il peut réellement être emmené.

Ceux-ci se trouvent dans le groupe `commands`, donc `control.commands` dans la configuration décide si un pack peut les définir, et `off` y garde tout au niveau opérateur quoi que demande un pack.

### Qui peut utiliser game

*commandes*

Chaque partie de `game` a son propre niveau : 0 pour chaque lancer, et 2 pour `deck shuffle` et `tiebreak`. `gameLevels` change n'importe lequel d'entre eux, sous forme d'entrées `partie=niveau`, où la partie est ce qui suit `game`.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "gameLevels": ["coin=0", "deck draw=0", "deck shuffle=3", "tiebreak=4"]
  }
}
```

| Réglage | Ce qu'il régit |
| --- | --- |
| `gameLevels` | Une partie de `game` : `coin`, `die`, `dice`, `advantage`, `disadvantage`, `pick`, `deck draw`, `deck shuffle`, `deck left`, `teamroll`, `tiebreak` ou `last` |

L'échelle est celle de `goto`, et `4` ferme une partie à tout le monde. La complétion par tabulation ne propose que les parties qu'un expéditeur peut lancer. `gameLevels` se trouve dans le groupe `commands` avec les réglages de `goto`.

## Bon à savoir

*référence*

- KubeJS et CraftTweaker s'exécutent après RDPL, donc leurs changements l'emportent toujours.
- Les recettes, tables de butin, progrès et fonctions sont ici les propres fichiers de données du jeu, donc `/reload` prend en compte une modification et `/rdpl reload` un nouveau fichier.
- Une structure déjà générée reste chargée jusqu'à ce que vous quittiez le monde.
- La casse des noms de fichier compte. Si la capitalisation de votre fichier ne correspond pas à ce que le jeu a demandé, RDPL le charge quand même mais vous avertit, car sous Linux il ne serait pas trouvé du tout.
- Placez un `pack.png` dans `rdploader` pour donner une icône au pack. Sans lui, c'est l'icône de RDPL qui s'affiche.
- Le dossier peut être déplacé ou renommé avec l'option `rootDirectory` dans `config/resourcedatapackloader-common.toml`. Un chemin absolu fonctionne aussi, et cela nécessite un redémarrage.
- Un modèle qui nomme un modèle vanilla fini hérite aussi des textures de vanilla. Les modèles parents comme `cube_all` et `cross` prennent leurs textures du modèle qui les nomme et ne posent pas de problème.
- La télémétrie et le signalement de chat du jeu sont désactivés tant que `privacy` dans la catégorie `tweaks` est activé, ce qui est le cas par défaut : rien n'est envoyé, aucun message de chat n'est signé, un serveur qui exécute le mod ne garde de session de chat pour personne, et se connecter à un serveur qui n'impose pas le chat sécurisé n'affiche aucune notification d'avertissement.
- Une option de pack modifiée est mémorisée par le monde qu'elle change ; quand le changement laisse du contenu non enregistré que le monde contient, le monde est sauvegardé dans le dossier `backups` du jeu avant d'être rouvert, et reste fermé si cette sauvegarde échoue.

## Quand quelque chose ne fonctionne pas

*référence*

**Consultez d'abord `logs/rdpl.log`.** Tout ce que fait RDPL va là plutôt que dans le journal principal. Les progrès, tables de butin, recettes, fonctions, structures et chaque élément de contenu y sont journalisés avec le pack dont ils proviennent, et tout ce qui est mal formé y est journalisé avec la raison.

**Les textures et les autres assets sont différents.** Ils sont demandés beaucoup trop souvent pour être journalisés un par un, donc c'est `/rdpl unused` qui liste les fichiers de vos packs que rien n'a demandés. Exécutez-la une fois que le jeu a fini de charger. Un fichier au bon chemin est toujours demandé, donc tout ce qui est listé est généralement une faute de frappe, mais gardez à l'esprit que certains fichiers ne se chargent que lorsqu'ils sont nécessaires, comme les langues autres que celle dans laquelle vous jouez.

**Un zip sans répertoire `assets` ou `data` à l'intérieur est ignoré,** de même que tout dossier dans `rdploader`, et le journal le dit. Un zip dont le premier niveau est un dossier unique qui les enveloppe est ignoré de la même façon.

**`/rdpl which minecraft:textures/block/stone.png`** vous dit exactement quel pack sert un fichier et ce qu'il masque.

**Un pack écrit pour la 1.12.2 est lu à travers le portage ascendant.** Voir [Packs écrits pour 1.12.2](#packs-écrits-pour-1122) ; le journal nomme chaque fichier qu'il a déplacé, laissé de côté ou n'a pas pu reprendre.

## Bonus : ajustements vanilla

*référence*

De petits changements dans le comportement de vanilla, chacun activé dans la catégorie de configuration `tweaks`.

| Option | Défaut | Ce qu'elle fait |
| --- | --- | --- |
| `promptLeafDecay` | activé | Les feuilles qui perdent leur arbre disparaissent en moins d'une seconde au lieu d'attendre les ticks aléatoires |
| `lenientPaths` | activé | Des chemins peuvent être faits sous un bloc et y restent quand on en place un au-dessus |
| `unbreakableSpawners` | désactivé | Les générateurs de monstres ne peuvent être ni minés ni détruits par une explosion. Le mode créatif les retire toujours. Nécessite un redémarrage |
| `experimentalWarning` | désactivé | Affiche l'avertissement du jeu sur les réglages expérimentaux quand un monde est créé ou ouvert. Désactivé y répond comme si vous aviez cliqué sur continuer |
| `privacy` | activé | Désactive la télémétrie et le signalement de chat du jeu ; voir [Bon à savoir](#bon-à-savoir) |
| `darkSplash` | activé | Écran de chargement sombre avec le logo du chargeur de packs ; l'option Logo monochrome du jeu est activée pour le prochain démarrage |

Quatre autres se trouvent dans la catégorie `content` plutôt que dans `tweaks` :

| Option | Défaut | Ce qu'elle fait |
| --- | --- | --- |
| `cactusMaxHeight` | `3` | La hauteur à laquelle pousse le cactus vanilla |
| `caneMaxHeight` | `3` | La hauteur à laquelle pousse la canne à sucre vanilla |
| `shovelPaths` | activé | Une pelle transforme les blocs marqués `behavesAs` path en chemin, et s'accroupir en rétablit un |
| `hoeTilling` | activé | Une houe laboure les blocs marqués `behavesAs` till |

**Rien de tout cela n'atteint un pack.** Ces options ne changent que le cactus, la canne, les feuilles et les chemins propres à Minecraft. Un bloc que votre pack définit avec `"type": "cane"` porte sa propre section `growth` et pousse à la hauteur que vous lui avez donnée, quoi que l'on installe d'autre.

### Générateurs incassables

*bonus : ajustements vanilla*

`unbreakableSpawners` donne au bloc générateur de monstres les valeurs de la bedrock, une dureté incassable et une résistance aux explosions que rien ne survit. Un joueur ne peut pas en miner un, aussi bonne que soit la pioche, et ni les creepers, ni le TNT, ni une entité de pack qui `explodes` n'en détruira un. Le mode créatif les retire toujours, exactement comme il retire toujours la bedrock, de sorte qu'un auteur de pack n'est jamais enfermé hors de sa propre construction. Cela nécessite un redémarrage, puisque les valeurs du bloc sont fixées lors de son enregistrement.

**C'est le bloc, pas le générateur.** Il n'existe pas d'interrupteur par générateur. L'option modifie `minecraft:spawner` lui-même, donc elle atteint d'un coup tous les générateurs du monde : les structures vanilla qui en placent un, ceux qu'un mod place, et ceux que vos propres packs placent. Un générateur à l'intérieur d'un de vos modèles `.nbt`, placé par une entrée `imprint`, est un bloc générateur ordinaire portant sa propre entité de bloc, donc il est couvert dès que l'option est activée.

## Clés non reprises

*référence*

Ce qu'un pack 1.12.2 peut écrire et que cette version ne lit pas, et pourquoi. Un pack qui en écrit une se charge quand même ; la clé ne fait rien. Une ligne qui dit *pas encore reprise* a été ajoutée à la 1.12.2 après le portage de cette partie et reste à venir. Toutes les autres lignes ne peuvent pas, ou n'ont pas besoin de, passer sur ce moteur. Une ligne marquée *depuis 26.3* est ce qu'un pack 26.3 peut écrire et que cette version ne peut pas contenir ; le journal nomme chacune d'elles quand elle est abandonnée. Le tableau est tenu à jour au fil de l'avancée des versions.

| Clé | Où | Pourquoi |
| --- | --- | --- |
| `meta` | blocs, objets, worldgen, drops de blocs | Les ids ne portent plus de métadonnées depuis l'aplatissement. Le portage fait passer chaque `name:meta` par les correcteurs de données du jeu et supprime la clé |
| `oreDict` | blocs, objets, four, combustibles, filtres de stockage d'entité | Le dictionnaire de minerais a disparu. Le portage le convertit en `tags` sur les tags de convention, et celui d'un combustible ou d'une entrée de filtre de stockage en `tag` |
| `galacticraft` | variantes d'entité | Il n'existe pas de Galacticraft pour cette version, donc une variante n'a ni niveau de fusée, ni réservoir de carburant, ni soute, ni charge utile à définir |
| `oreDictionary` | paramètres | Le dictionnaire de minerais a disparu, donc il ne reste aucun fichier de dictionnaire de minerais à désactiver. Les tags font son travail, et le portage les écrit d'après les `oreDict` d'un pack |
| `modelMeta` | blocs | Les modèles sont générés par variante, donc il n'y a pas de métadonnées pour les associer |
| `disableOverrides`, `tolerateMissingInAdvancements` | paramètres | Un pack de données remplace une recette ou un progrès vanilla en en fournissant un sous le même nom |
| noms d'objets `#CONSTANT` | recettes | Les constantes de recette vivaient dans le `_constants.json` d'un mod 1.12.2, qu'aucun pack ne contient et qu'aucun mod de cette version n'a. Nommez l'objet ou le tag que la constante désignait |
| `harvestTool` autre que `pickaxe`, `axe`, `shovel`, `hoe` et `sword` | surcharges de propriétés | Ici, les outils minent d'après des tags de blocs, et seuls ces cinq en ont un. Une classe d'outil inventée par un mod 1.12.2 n'a pas de tag à écrire, donc les outils du bloc sont laissés tels quels et le journal l'indique |
| `careers` | villageois | Il n'y a plus de carrières depuis la 1.14, donc chaque carrière devient un fichier de villageois à part |
| `career` | échanges, entités | Pas de carrières ; nommez le métier lui-même. Un échange qui nomme un métier vanilla de la 1.12.2 avec sa carrière va au métier que cette carrière est devenue |
| `gameLoopFunction` | règles de jeu | La règle de jeu a disparu ; le tag de fonction `#minecraft:tick` exécute une fonction à chaque tick, et le portage en écrit un |
| `id` | biomes, dimensions | Les biomes et les dimensions sont connus par leur identifiant de ressource, jamais par un numéro |
| `suffix`, `keepLoaded` | dimensions | Le dossier de sauvegarde suit le nom de la dimension. Seule l'Overworld a des chunks de point d'apparition, donc une dimension qui doit rester chargée prend un `forceload` |
| `baseHeight`, `heightVariation` | biomes | La hauteur du terrain relève des paramètres de bruit, pas du biome |
| `placement.villageSpawn` | biomes | Les villageois viennent avec la structure du village elle-même |
| `rubicWorld`, `rubicWorldDimensions`, `rubicWorldDimensionsAreBlacklist`, `verticalCubeLoadDistance`, `cubeGCInterval`, `cubeGenMillisPerRound`, `cubesSentPerTick` | mondes rubic | Le monde rubic était la façon de la 1.12.2 de dépasser un monde de 256 blocs. Ici, les `minHeight` et `maxHeight` d'une dimension fixent sa taille et le système de chunks la diffuse en continu |
| `rubicHeightLimit` | mondes rubic | Le plafond de hauteur du monde rubic a disparu avec lui ; les `minHeight` et `maxHeight` d'une dimension fixent sa taille à la place, sans plafond séparé à relever |
| `regionCacheLimit` | mondes rubic | Son cache des fichiers de région ouverts propres à un monde rubic a disparu avec le monde rubic ; le système de chunks d'ici gère ses propres fichiers |
| `skyStone`, `skyShape`, `skyIslands`, `skyThickness`, `skyHeights`, `skyAnimals` | le monde profond, biomes, régions de grottes | Les terres du ciel vivaient au-dessus de la fenêtre de terrain d'un monde rubic, et il n'y a pas de monde rubic pour les contenir |
| `deepRavines`, `oreVeins` | le monde profond | Le terrain du moteur descend déjà sous y 0, avec ses propres ravins et de grands filons de minerai |
| `terrainOffset` | paramètres | Il n'y a pas de fenêtre vanilla fixe à décaler ; les `minHeight` et `maxHeight` d'une dimension fixent son plancher et son plafond |
| `terrainWorldTypes`, `terrainWorldTypesAreBlacklist` | paramètres | Les types de monde sont ici des préréglages de monde, et un modèle de monde nomme le sien |
| `biomeSize`, `riverSize`, `dungeonChance`, `waterLakeChance`, `lavaLakeChance`, les clés de taille, de nombre et de hauteur des minerais et les clés d'échelle de bruit d'un `generatorOptions` personnalisé | paramètres, dimensions | La taille des biomes et la forme du terrain relèvent des paramètres de bruit, et la fréquence de placement d'un élément ou d'un minerai de son propre élément placé, donc aucun nombre unique ne les atteint. Un `fixedBiome` numéroté au-dessus de 39 nomme un biome que cette version ne peut pas associer |
| `useWaterLakes`, et `lake` et `dungeon` dans un texte superflat | paramètres, dimensions | Le jeu n'a plus de lacs d'eau depuis la 1.18. Les donjons d'un monde plat viennent avec `decoration` et non seuls |
| `inherit` comme source `biomes` d'une dimension `flat` ou `void` | dimensions | Un générateur plat ne contient qu'un seul biome, donc une telle dimension prend celui que nomme son texte superflat, ou plains |
| la dimension d'un autre mod dans `flatBedrockDimensions` ou `voidWorldDimensions` | paramètres | La bedrock et le vide sont écrits dans le préréglage de monde généré et dans les fichiers de dimension du pack ; une dimension qu'un autre mod crée est construite à partir de ses propres fichiers |
| le type de monde d'un mod, ou `debug_all_block_states`, comme `worldType` | paramètres | Les types de monde sont désormais des préréglages de monde, et le monde façonné est construit sur le bruit par défaut du jeu, les grands biomes, l'amplifié ou le plat. Un monde de débogage n'a pas de terrain à façonner |
| `pregenKeepLoaded`, `pregenPauseAbove`, `pregenMillisPerRound`, `pregenRelightSays`, `hurryWritesAbove` | prégénération | Elles réglaient l'écriveur de chunks et la passe de recalcul de lumière de la 1.12.2. Le système de chunks d'ici éclaire le terrain à mesure qu'il le crée et écrit selon son propre calendrier |
| `readCofhWorldFiles` | paramètres | CoFH World et son propre format de fichier n'existent pas sur ce moteur. Traduire ces fichiers en un pack, comme la 1.12.2 le recommandait déjà, reste la façon de passer |
| `name:meta` dans `villagePathLamp*` | villages | Les ids ne portent pas de métadonnées, donc un bloc de lampe s'écrit avec son état entre crochets, et les données de bloc entre accolades se lisent toujours |
| `harvestTool` nommant `shears` ou une classe d'outil moddée | blocs | Un outil lit ici des tags de blocs, pas un nom de classe, donc rien ne répond à un tel nom. Nommez le tag de blocs propre à l'outil moddé dans les `tags` de la variante |
| l'étiquette d'onglet d'un autre mod dans `creativeTab` | blocs, objets, fluides | Un onglet est désormais connu par son id, donc une étiquette seule est lue comme un onglet propre au pack. Nommez l'onglet du mod par son id, comme `modid:main` |
| `/rdpl reload <group>` | commandes | Le jeu recharge toutes les ressources en une seule passe, donc les textures, modèles, langues, sons et shaders ne peuvent pas être rechargés isolément. `/rdpl reload` ou F3+T les recharge tous |
| `modernChestPlacement` | ajustements vanilla | Le jeu apparie les coffres ainsi depuis la 1.13 : un coffre ne rejoint un coffre simple à côté de lui que si les deux regardent dans la même direction, et s'accroupir le garde simple |
| `loadingScreenPercent` | paramètres | L'écran de chargement du monde du jeu indique déjà quelle part de la zone d'apparition est prête |
| `disableOptimizations` | paramètres | Elle désactivait les optimisations de prégénération et de génération de la 1.12.2, écrites pour ce moteur et sans équivalent ici |
| `fixTinkersModelErrors` | paramètres | Elle faisait taire les erreurs de modèle que les versions 1.12.2 de Tinkers' Construct et de Construct's Armory journalisaient pour chaque outil, pièce et pièce d'armure. Le correctif s'insérait dans ces versions, donc il n'y a rien sur quoi il puisse agir ici |
| critères `achievement.` | score, fonctions | Les succès sont devenus des progrès, qui ne tiennent aucun score à compter. Un critère `stat.` est lu comme la statistique qu'il est devenu |
| `toggledownfall`, `stats` | fonctions | Les commandes ont disparu. `weather` nomme la météo à définir, et `execute store` conserve le résultat d'une commande |
| `gamerule gameLoopFunction`, et une règle de jeu inventée par un pack | fonctions | Le jeu décide quelles règles de jeu existent. Le portage convertit le `gameLoopFunction` d'un fichier de règles de jeu en tag `#minecraft:tick`, mais une ligne de commande qui le définit est conservée pour que vous la retiriez |
| un nom avec la valeur de donnée `-1` ou `*` devenu plusieurs blocs ou objets | fonctions : `clear`, `testforblock`, `execute ... detect`, `fill ... replace`, `clone ... filtered` | Un test nomme désormais un seul bloc ou objet. Nommez celui qui est visé, ou un tag comme `#minecraft:wool` |
| un état de bloc écrit en paires `name=value` qui n'est pas l'état 1.12.2 complet du bloc, et une valeur de donnée sur le bloc ou l'objet d'un autre mod | fonctions | Les correcteurs de données ne connaissent que les états 1.12.2 complets, et les métadonnées d'un autre mod n'ont pas de nom aplati vers lequel aller |
| les particules `footstep` et `take`, `locate Temple`, `spreadplayers` avec plus d'une cible | fonctions | Les particules ont disparu, un temple est désormais quatre structures, et `spreadplayers` prend un seul argument de cible |
| `debug_functions` | paramètres de bruit, *depuis 26.3* | La 26.2 et les versions antérieures n'ont pas de fonctions de densité de débogage, donc la liste est abandonnée |
| `exclusion` et `surface_level` dans `aquifers` | paramètres de bruit, *depuis 26.3* | La 26.2 et les versions antérieures intègrent les deux, donc ceux du pack sont abandonnés |
| une entrée `spawn_target` nommant autre chose qu'un axe climatique du `noise_router` | paramètres de bruit, *depuis 26.3* | Une cible d'apparition plus ancienne ne couvre que les cinq axes climatiques, donc cette entrée est abandonnée |
| des filons de minerai placés via `material_rule` | paramètres de bruit, *depuis 26.3* | La 26.2 ne peut pas placer de filons de minerai par une règle de matériau, donc ils sont abandonnés |
| le `count`, `thickness`, `weird_thickness_bias` ou `start_vertical_radius_multiplier` propre à un creuseur de grottes | creuseurs, *depuis 26.3* | La 26.2 ne peut pas les définir, donc la forme de grotte vanilla de l'Overworld ou du Nether est utilisée |
| `creature_world_gen_spawn_probability` comme modificateur, et un attribut qui s'ajoute à la valeur de sa dimension | biomes, *depuis 26.3* | La 26.2 prend une valeur simple : la chance d'apparition par défaut est conservée, et la valeur propre au biome remplace celle de la dimension |
| des décalages x et z différents, et `normalize` défini sur false | éléments placés, bruits, *depuis 26.3* | La 26.2 étale les deux axes d'une même quantité, donc les deux utilisent le décalage x, et elle normalise toujours un bruit |
| un type d'élément que la 26.2 n'a pas, ou un élément 26.3 construit à partir de blocs qu'elle n'a pas | éléments, biomes, *depuis 26.3* | Un tel élément ne place rien, et un biome omet un élément placé 26.3 sans équivalent 26.2 |
| `straw_bed_rule`, et `destroy_on_leave` dans une règle de lit | attributs d'environnement, *depuis 26.3* | La 26.2 n'a ni l'une ni l'autre de ces règles, donc les deux sont abandonnées |
| un objet avec quantité ou composants parmi les décorations de pot | fichiers de données, fonctions, *depuis 26.3* | La 26.2 lit un objet simple, donc la quantité et les composants sont abandonnés |
| `compute`, `posteffect`, `item fill`, `item override` et `execute if slots`, et l'animation de `swing` | fonctions, *depuis 26.3* | La 26.2 n'a aucune de ces commandes, donc la ligne est abandonnée ; une ligne `swing` est conservée et balance simplement |
| `shade_direction_override` autre que `up`, et `trim_overrides` | modèles, assets d'équipement, *depuis 26.3* | La 26.2 ombre normalement un tel élément, et lit les palettes d'ornement par équipement dans les `override_armor_assets` du matériau d'ornement |
| des composants sur un objet distillé, et un tag ou une liste d'objets à partir desquels distiller | recettes d'alambic, *depuis 26.3* | Une recette d'alambic 26.2 ne peut nommer ni l'un ni l'autre : les composants sont abandonnés, et une recette qui distille à partir d'un tag ou d'une liste est abandonnée en entier |
| un fournisseur de nombre pour lequel la 26.2 n'a pas de forme, et une valeur de repli autre que 0 | tables de butin, prédicats, modificateurs d'objet, *depuis 26.3* | Le fournisseur devient 0, et un score ou un nombre stocké manquant se replie sur 0 |
| un test de condition de bloc autre que `blocks` et `state`, un ensemble d'ids là où la 26.2 en lit un seul, un tag de prédicats ou de modificateurs d'objet, une carte d'explorateur vers des structures nommées | tables de butin, prédicats, modificateurs d'objet, *depuis 26.3* | Le test est abandonné, seul le premier id est conservé, un tag est lu comme l'unique id qu'il nomme, et la carte mène à des structures de trésor |

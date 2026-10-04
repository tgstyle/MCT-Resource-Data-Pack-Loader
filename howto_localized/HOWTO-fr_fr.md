# Resource Data Pack Loader

**Un seul dossier qui remplace tout ce que Minecraft ou un mod fournit, définit du nouveau contenu à partir de JSON et contrôle ce qui se génère, dans tous les mondes, côté client comme côté serveur, sans rien à activer pour les joueurs.**

Douze exemples fonctionnels. Déposez-en un directement dans `rdploader` et observez la manière dont chaque fichier est écrit.

- [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) couvre la plupart des fonctionnalités : blocs, objets, biomes, une dimension, un modèle de monde et toutes les formes de worldgen.
- [RDPLExampleOrePackVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleOrePackVoid.zip) transforme l'Overworld en un vide où la worldgen flotte dans les airs, une forme par bande d'altitude, pour que chacune soit facile à observer isolément.
- [RDPLExampleVeinShapes.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleVeinShapes.zip) place trois filons de minerai dans un Overworld ordinaire, un par motif de filon (simple, rubané et tubulaire), chacun avec des paliers riche, normal et pauvre, et quelques blocs repères en surface au-dessus pour les prospecter.
- [RDPLExampleVeinShapesVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleVeinShapesVoid.zip) suspend les trois mêmes motifs de filon dans un vide, pour que chaque forme se voie en entier.
- [RDPLExampleDeepWorld.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleDeepWorld.zip) fait de l'Overworld un monde Rubic, avec 256 blocs de monde généré sous celui de vanilla et 128 au-dessus : le mélange de pierre des profondeurs, des grottes de bruit modernes, des ravins, des filons de minerai rubanés, trois régions de grottes à descendre, et des îles flottantes au-dessus de vous, découpées par le même bruit.
- [RDPLExampleContainers.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleContainers.zip) ajoute des blocs et des objets portés qui contiennent un inventaire, de toutes les tailles, de trois emplacements jusqu'au maximum autorisé, avec une table de butin, le modèle du coffre teinté à partir de la feuille de vanilla, chaque texture dessinée comme une carte de pixels, et deux bourses qui peuvent se porter dans Baubles.
- [RDPLExampleMegaCity32.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleMegaCity32.zip) crée un monde superplat contenant un seul village volontairement gigantesque, étendu à mille parcelles et ancré à l'origine, avec des rues aménagées en routes de béton, trottoirs, lignes centrales en pointillés et lampadaires, des égouts sous les rues, deux lignes de métro avec leurs stations en dessous et une voie ferrée à travers la ville, et des bâtiments composés à partir de cartes de structures en quatre tailles et trois façades plutôt qu'à partir des maisons de vanilla.
- [RDPLExampleMegaCity64.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleMegaCity64.zip) est cette même ville sur un monde Rubic dont le plafond est à 512 et les nuages relevés à 384, de sorte que les tours s'élèvent à 256 blocs au-dessus de la rue, et chaque quartier tire au sort une profondeur de bloc de 16, 32 ou 64 pour qu'une grille grossière côtoie une grille fine.
- [RDPLExampleCityCustomMap.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleCityCustomMap.zip) dessine cette même ville à partir d'une carte de ville au lieu de la tirer au sort : une grille de caractères à 48 blocs par cellule, avec une palette qui nomme rues, places, ruelles et choix pondérés de bâtiments, de sorte que le plan des blocs est tracé à la main.
- [MCTKamikazeDemo.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/MCTKamikazeDemo.zip) oppose quatre factions dans une arène de bedrock sous une nuit permanente : chaque camp est une véritable équipe du tableau des scores de vanilla que ses mobs rejoignent en apparaissant, un camp marque des points pour chaque mob d'un autre camp qu'il tue, une manche se termine sur une carte au bout de deux minutes, et trois manches font une partie.
- [RDPLExampleRaid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleRaid.zip) place un monde plat autour d'un village dont le puits est un pavillon à cloche et donne au joueur un Mauvais présage dès sa connexion : cinq vagues d'illagers de vanilla, une sorcière et les propres Pillards, Capitaines de raid, Lanceurs de hache et Ravageurs du pack, le présage et Héros du village sous forme d'effets propres au pack avec des icônes en cartes de pixels, une boisson qui ramène le présage, et les fonctions qui mettent fin au raid.
- [RDPLExampleGalacticraft.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleGalacticraft.zip) nécessite Galacticraft et place sur la carte stellaire un système solaire qui lui est propre, avec une planète atteinte par une fusée de niveau 2 qui possède son propre ciel, sa gravité, sa durée du jour, sa météo et son atmosphère, une planète à observer mais où l'on ne peut jamais atterrir, et, avec GalaxySpace, une planète de glace autour de Tau Ceti.
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
- [Packs écrits pour 1.20.1, 1.21.1 et 26.x](#packs-écrits-pour-1201-1211-et-26x)

**Blocs et objets**
- [Blocs](#blocs)
- [Conteneurs](#conteneurs)
- [Modèles, états de bloc et textures](#modèles-états-de-bloc-et-textures)
- [États de bloc par type](#états-de-bloc-par-type)
- [Faire en sorte que vanilla traite correctement votre bloc](#faire-en-sorte-que-vanilla-traite-correctement-votre-bloc)
- [Objets](#objets)
- [Fluides](#fluides)
- [Matériaux, onglets, sons, dictionnaire des minerais](#matériaux-onglets-sons-dictionnaire-des-minerais)
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
- [Corps célestes de Galacticraft](#corps-célestes-de-galacticraft)
- [Portails et passages](#portails-et-passages)
- [Mondes Rubic](#mondes-rubic)
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

**Contrôle**
- [La couche de contrôle](#la-couche-de-contrôle)
- [Ce que fait chaque groupe](#ce-que-fait-chaque-groupe)

**Autres mods**
- [Universal Tweaks](#universal-tweaks)
- [Mo' Villages](#mo-villages)
- [CoFH World](#cofh-world)
- [Lost Cities](#lost-cities)
- [Intégration de Blast Plaster](#intégration-de-blast-plaster)
- [Mods de tombes](#mods-de-tombes)

**Référence**
- [Listes de valeurs](#listes-de-valeurs)
- [Liste des dossiers](#liste-des-dossiers)
- [Commandes](#commandes)
- [Bon à savoir](#bon-à-savoir)
- [Quand quelque chose ne fonctionne pas](#quand-quelque-chose-ne-fonctionne-pas)
- [Bonus : ajustements vanilla](#bonus--ajustements-vanilla)
- [Bonus : correctif des conflits de plugins JEI](#bonus--correctif-des-conflits-de-plugins-jei)
- [Bonus : moins d'erreurs au démarrage](#bonus--moins-derreurs-au-démarrage)

---

# Premiers pas

## Ce que c'est

*premiers pas*

Resource Data Pack Loader (RDPL) lit un seul dossier, `rdploader`, et accomplit trois tâches :

- **Surcharges.** Un fichier placé dans le dossier remplace celui que le jeu ou un mod aurait chargé. Aucun interrupteur, aucune configuration par monde, rien à activer pour les joueurs.
- **Nouveau contenu.** Des définitions JSON enregistrent des blocs, des objets, des fluides, des biomes, des dimensions, des potions et des villageois. Pas de Java, pas de jar.
- **Contrôle.** Bloquer la génération des minerais, des biomes, des structures ou des recettes, aplatir la bedrock, régler les taux d'apparition, vider l'Overworld, définir les valeurs par défaut du monde.

## Où vont les fichiers

*premiers pas*

Tous les chemins de ce guide sont écrits à partir de `assets/`, si bien que `<namespace>/blocks/*.json` correspond à `assets/mypack/blocks/ruby_ore.json` sur le disque pour un pack dont le namespace est `mypack`. Chaque section rappelle son propre chemin sous son titre, avec une note sur ce que devient ce chemin.

| Chemin | Ce qu'il contient |
| --- | --- |
| `<namespace>/blocks/*.json` | Définitions de blocs. [Blocs](#blocs) |
| `<namespace>/items/*.json` | Définitions d'objets. [Objets](#objets) |
| `<namespace>/fluids/*.json` | Fluides, avec un bloc et un seau. [Fluides](#fluides) |
| `<namespace>/materials/*.json` | Matériaux d'outils et d'armures. [Matériaux, onglets, sons, dictionnaire des minerais](#matériaux-onglets-sons-dictionnaire-des-minerais) |
| `<namespace>/tabs/*.json` | Onglets créatifs. [Matériaux, onglets, sons, dictionnaire des minerais](#matériaux-onglets-sons-dictionnaire-des-minerais) |
| `<namespace>/sounds/*.json` | Événements sonores. [Matériaux, onglets, sons, dictionnaire des minerais](#matériaux-onglets-sons-dictionnaire-des-minerais) |
| `<namespace>/oredict/*.json` | Noms du dictionnaire des minerais. [Matériaux, onglets, sons, dictionnaire des minerais](#matériaux-onglets-sons-dictionnaire-des-minerais) |
| `<namespace>/biomes/*.json` | Définitions de biomes. [Biomes](#biomes) |
| `<namespace>/worldgen/*.json` | Ce qui se génère, et où. [Entrées de worldgen](#entrées-de-worldgen) |
| `<namespace>/caveregions/*.json` | Régions nommées peintes sur le sous-sol. [Régions de grottes](#régions-de-grottes) |
| `<namespace>/dimensions/*.json` | Définitions de dimensions. [Dimensions](#dimensions) |
| `<namespace>/celestial/*.json` | Systèmes stellaires et corps célestes pour la carte de Galacticraft. [Corps célestes de Galacticraft](#corps-célestes-de-galacticraft) |
| `<namespace>/worldtemplates/*.json` | Tous les réglages d'un monde dans un seul fichier. [Modèles de monde](#modèles-de-monde) |
| `<namespace>/worldintro/*.json` | Pages affichées lorsqu'un joueur entre dans le monde. [Introduction au monde](#introduction-au-monde) |
| `<namespace>/gates/*.json` | Conditions sur les portails et les dimensions. [Portails et passages](#portails-et-passages) |
| `<namespace>/gamerules/*.json` | Règles de jeu pour les nouveaux mondes. [Règles de jeu](#règles-de-jeu) |
| `<namespace>/teams/*.json` | Camps du tableau des scores de vanilla, et ce qui les rejoint. [Équipes](#équipes) |
| `<namespace>/scoring/*.json` | Objectifs, points et fin d'une partie. [Score](#score) |
| `<namespace>/raids/*.json` | Vagues qui s'abattent sur un village lorsqu'un joueur y apporte un présage. [Raids](#raids) |
| `<namespace>/entities/*.json` | Variantes d'entités construites sur des entités existantes. [Variantes d'entités](#variantes-dentités) |
| `<namespace>/hardness/*.json` | Temps de minage et multiplicateurs d'explosion pour des groupes de blocs. [Groupes de dureté](#groupes-de-dureté) |
| `<namespace>/exposures/*.json` | Dangers qui exposent les joueurs près de blocs nommés, portant des objets nommés ou dans des dimensions nommées. [Expositions](#expositions) |
| `<namespace>/overrides/<target>/<name>.json` | Propriétés de blocs, d'objets et de types de potions existants, modifiées sur place. [Surcharges de propriétés](#surcharges-de-propriétés) |
| `<namespace>/villages/*.json` | Parcelles que les villages peuvent bâtir. [Parcelles de village](#parcelles-de-village) |
| `<namespace>/pathintersects/*.json` | Motifs peints là où les routes de village se croisent. [Routes de village](#routes-de-village) |
| `<namespace>/structuremaps/*.json` | Modèles composés en un grand bâtiment sur une grille. [Cartes de structures](#cartes-de-structures) |
| `<namespace>/citymaps/*.json` | Un plan de rues dessiné à partir duquel un village est aménagé au lieu de croître. [Cartes de plan des villes](#cartes-de-plan-des-villes) |
| `<namespace>/portalframes/*.json` | Cadres qu'un joueur peut construire et allumer. [Cadres de portail](#cadres-de-portail) |
| `<namespace>/blastplaster/*.json` | Ce que fait Blast Plaster après une explosion, par dimension. [Intégration de Blast Plaster](#intégration-de-blast-plaster) |
| `<namespace>/structures/*.nbt` | Modèles, pour les pousses, `imprint` et les surcharges de mods. [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger) |
| `<namespace>/recipes/*.json` | Recettes d'artisanat, ajoutées ou remplacées. [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger) |
| `<namespace>/recipe_removals/*.json` | Recettes supprimées par nom, namespace ou résultat. [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger) |
| `<namespace>/disabled/*.json` | Blocs et objets retirés du jeu. [Blocs et objets désactivés](#blocs-et-objets-désactivés) |
| `<namespace>/furnace/*.json` | Recettes de fourneau ajoutées et retirées. [Recettes de fourneau et combustibles](#recettes-de-fourneau-et-combustibles) |
| `<namespace>/fuels/*.json` | Durées de combustion. [Recettes de fourneau et combustibles](#recettes-de-fourneau-et-combustibles) |
| `<namespace>/brewing/*.json` | Recettes du pupitre d'alchimie. [Potions, types de potions et alchimie](#potions-types-de-potions-et-alchimie) |
| `<namespace>/potions/*.json` | Effets de potion. [Potions, types de potions et alchimie](#potions-types-de-potions-et-alchimie) |
| `<namespace>/potion_types/*.json` | Potions en bouteille construites à partir de ces effets. [Potions, types de potions et alchimie](#potions-types-de-potions-et-alchimie) |
| `<namespace>/villagers/*.json` | Professions de villageois. [Villageois et échanges](#villageois-et-échanges) |
| `<namespace>/trades/*.json` | Ce que les métiers achètent et vendent. [Villageois et échanges](#villageois-et-échanges) |
| `<namespace>/loot_tables/*.json` | Tables de butin, remplacées. [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger) |
| `<namespace>/loot_injections/*.json` | Une réserve ajoutée à une table de butin existante. [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger) |
| `<namespace>/block_drops/*.json` | Butins supplémentaires ou de remplacement pour des blocs que le pack ne possède pas. [Butins des blocs](#butins-des-blocs) |
| `<namespace>/anvils/*.json` | Enchantements qu'une enclume applique à un objet nommé, un progrès qu'elle fait obtenir, et un verrou jusque-là. [Travail à l'enclume](#travail-à-lenclume) |
| `<namespace>/cards/*.json` | Cartes affichées à l'écran sur un déclencheur, et les messages que ce mod émet lui-même. [Cartes](#cartes) |
| `<namespace>/player_loot/*.json` | Une table de butin tirée à la mort d'un joueur. [Butin des joueurs](#butin-des-joueurs) |
| `<namespace>/advancements/*.json` | Progrès. [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger) |
| `<namespace>/functions/*.mcfunction` | Fichiers de fonctions. [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger) |
| `<namespace>/registry_remap/*.json` | Anciens noms associés à de nouveaux. [Renommages du registre](#renommages-du-registre) |
| `<namespace>/texts/*.txt` | Fichiers de texte brut, utilisés par l'introduction au monde. [Introduction au monde](#introduction-au-monde) |
| `<namespace>/models/`, `<namespace>/blockstates/`, `<namespace>/textures/`, `<namespace>/lang/` | Les dossiers de ressources habituels. [Modèles, états de bloc et textures](#modèles-états-de-bloc-et-textures) |

## Lire les tableaux

*premiers pas*

Chaque fichier est en JSON standard. Une entrée de worldgen représentative :

```json
{
  "blocks": [
    { "block": "minecraft:wool", "weight": 80, "properties": { "color": "magenta" } },
    { "block": "mypack:ruby_ore", "weight": 20 }
  ],
  "size": { "min": 4, "max": 12 },
  "attempts": 12,
  "maxTemperature": 0.5,
  "sparse": true,
  "replace": ["minecraft:stone", "minecraft:andesite"],
  "dimensions": [0, -1]
}
```

Les tableaux de clés de ce document indiquent si une clé est obligatoire, ce qu'elle contient, et la valeur par défaut en cas d'omission. Les valeurs non reconnues sont consignées dans le journal et remplacées par la valeur par défaut ; elles ne font pas planter le jeu. Types de valeurs utilisés partout :

| Quand un tableau indique | Vous écrivez |
| --- | --- |
| int | `8` |
| int, ticks | `100` (20 ticks = 1 seconde) |
| int ou intervalle | `8`, ou `{ "min": 4, "max": 12 }` pour tirer entre les deux |
| 0 à 15, 1 à 100 et similaires | un int compris dans ces bornes |
| float | `0.5` |
| booléen | `true` ou `false` |
| chaîne | `"des mots entre guillemets"` |
| nom de bloc, nom d'objet | `"minecraft:stone"`, avec la métadonnée en troisième partie : `"minecraft:stone:3"` |
| `namespace:name` | `"mypack:ruby_ore"` |
| nom de biome, nom de son, nom d'onglet | la même forme `namespace:name` entre guillemets |
| couleur hexadécimale | six chiffres hexadécimaux, `"A0C8FF"`, `#` facultatif |
| chemin de texture | `"mypack:blocks/ruby_ore"` |
| liste d'ints | `[0, -1]` |
| liste de noms de blocs | `["minecraft:stone", "minecraft:andesite"]` |
| liste de noms de biomes | `["minecraft:extreme_hills", "mypack:ruby_hills"]` |
| liste de types du dictionnaire | `["MOUNTAIN", "FOREST"]` |
| liste d'identifiants de mods ou de namespaces de packs | `["quark", "mypack"]` |
| liste d'objets | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`, clés selon le tableau propre à cet objet |
| objet | `{ "type": "cluster" }`, clés selon son propre tableau |
| objet associant rôle et biome, ou nom de variante et variante | les clés sont le premier élément, les valeurs le second : `{ "ocean": "mypack:ruby_ocean" }` |

La plupart des définitions acceptent aussi `requires`, une liste d'identifiants de mods ou de namespaces de packs qui doivent être présents, faute de quoi le fichier est ignoré.

## La règle unique

*premiers pas*

Ouvrez le jar, trouvez le fichier que vous voulez modifier et copiez son chemin à partir de `assets` :

```
assets/minecraft/textures/blocks/iron_ore.png        (in the Minecraft jar)
rdploader/assets/minecraft/textures/blocks/iron_ore.png    (your override)
```

Le chemin situé après `assets` est toujours identique à celui qui se trouve dans le jar. Rien n'est renommé ni déplacé.

## Organiser les packs

*premiers pas*

Les fichiers isolés fonctionnent sous `rdploader/assets/<namespace>/`. Le regroupement fonctionne aussi, sous forme de zip. Un dossier dans `rdploader` n'est jamais un pack : il est ignoré avec un avertissement dans le journal, alors compressez un pack en zip avant de le placer là.

```
rdploader/assets/minecraft/textures/blocks/iron_ore.png
rdploader/MyTextures.zip
```

**Packs dans le mauvais dossier.** Au démarrage, avant de lire `rdploader`, RDPL parcourt le dossier `resourcepacks` du jeu et déplace dans `rdploader` chaque zip de pack RDPL qu'il y trouve. Un zip est un pack RDPL lorsqu'il contient des fichiers de définition RDPL, tels que `assets/<namespace>/blocks/` ou, pour un pack moderne, `data/<namespace>/blocks/`. Un pack de ressources ordinaire reste où il est. Un zip dont le nom existe déjà dans `rdploader` est laissé en place, tout comme un pack RDPL placé dans un dossier ; les deux donnent lieu à un avertissement. Chaque déplacement est consigné dans `logs/rdpl.log`. Un pack déplacé n'apparaît plus dans la liste des packs de ressources, et RDPL le charge désormais depuis `rdploader`.

**Priorité.** Lorsque deux packs contiennent le même fichier, préfixez les noms avec `RDPL` et un nombre ; les nombres les plus élevés se chargent plus tard et l'emportent :

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

Insensible à la casse ; une espace, un tiret ou un tiret bas après le nombre est facultatif ; le préfixe est masqué dans le nom affiché. Un pack sans préfixe se charge en premier et perd face à tout pack numéroté. La priorité ordonne aussi les entrées de worldgen, ce qui compte lorsqu'un pack pose des blocs qu'un autre remplace.

**Désactiver un pack** en ajoutant `.disabled` à son nom.

**Un seul zip pour toutes les versions.** Un zip peut contenir un dossier `versions/<version>/` pour chaque version de Minecraft qu'il prend en charge : `versions/1.12.2/`, `versions/1.20.1/`, `versions/1.21.1/`, et pour 26.x la version exacte qui s'exécute, `versions/26.1.2/`, `versions/26.2/` ou `versions/26.3/`. Chacun est organisé comme la racine d'un pack pour cette version, `pack.mcmeta` compris. Un fichier situé sous le dossier de la version en cours est lu à la place du même chemin à la racine ; la racine est partagée par toutes les versions, et le dossier d'une autre version n'est jamais lu. Placez à la racine ce que toutes les versions lisent de la même façon et uniquement ce qui diffère dans un dossier de version, et un seul zip se charge sur les quatre.

**Les packs se convertissent d'une version à l'autre.** Charger un pack écrit pour une autre ligne le convertit la première fois, de la même manière, en écrivant ce qui a changé dans son propre dossier `versions/<version>/` comme ci-dessus ; cela se produit automatiquement au chargement, y compris celui que déclenche un `/rdpl reload` ou un `/rdplserver reload`, jamais par une commande dédiée. Chaque paire de versions se convertit dans les deux sens, si bien qu'un pack 1.12.2, 1.20.1, 1.21.1 ou 26.x se charge sur n'importe laquelle des autres. Un pack 26.3 se charge lui aussi sur toutes les lignes, lu selon le format 26.2 sur les plus anciennes, et un pack plus ancien se charge sur 26.3. Ce qu'un côté possède et que l'autre ne peut pas contenir est abandonné, et le journal nomme chaque abandon : en descendant depuis 26.3, entre autres, les fonctions de densité de débogage, l'exclusion d'aquifère propre à un pack et le niveau de surface, ainsi que les commandes que 26.2 n'a pas ; en montant vers 26.3, le `depth` et le `offset` d'une cible d'apparition et les clés de filon de minerai du routeur de bruit. Sur 1.12.2, la worldgen vanilla d'un pack moderne, comme ses biomes, ses features et ses réglages de bruit, ainsi que son dossier `neoforge`, sont eux aussi écartés, car 1.12.2 n'a pas d'équivalent pour l'un comme pour l'autre.

## Packs de ressources : qui l'emporte

*premiers pas*

Par défaut, les fichiers RDPL se placent au-dessus des packs de ressources qu'un joueur sélectionne, de sorte qu'un pack de ressources ne peut pas les surcharger. Ajoutez `O` ou `N` après le préfixe `RDPL` pour décider pack par pack :

```
rdploader/RDPLO Branding        always wins; resource packs cannot touch it
rdploader/RDPLN BaseTextures    a resource pack can override it
rdploader/RDPL1O Seasonal       priority and override combined
```

Les packs sans lettre suivent l'option de configuration `overrideResourcePacks`. `/rdpl list` marque les packs qui surchargent. La lettre doit terminer le préfixe (suivie d'une espace, d'un tiret, d'un tiret bas, ou de rien), de sorte que `RDPLOverhaul` est un pack nommé `Overhaul`, et non un indicateur `O`.

---

# Fonctionnement des packs

## Fonctionnement des définitions

*fonctionnement des packs*

Les dossiers d'un pack font deux choses : certains décrivent de nouvelles choses, et les autres remplacent des fichiers que le jeu ou un mod possède déjà, ce que couvre [Ce que vous pouvez surcharger](#ce-que-vous-pouvez-surcharger). Pour le premier type, le chemin est l'identité : un fichier situé à `assets/mypack/blocks/ruby_ore.json` enregistre un bloc appelé `mypack:ruby_ore`.

L'enregistrement se fait à la priorité la plus basse que Forge propose, donc si un vrai mod enregistre le même nom, le mod l'emporte et votre fichier est ignoré. Rien ici ne peut remplacer un mod.

**Où se situe la limite.** Tout ce qui demande une tile entity, une GUI, un inventaire ou une logique propre à chaque tick nécessite un vrai mod. Tout ce qui reste en deçà est permis.

### Votre namespace est votre mod

*fonctionnement des définitions*

Le namespace que vous choisissez est, à toutes fins pratiques, un identifiant de mod. Rien n'est chargé comme un mod et il n'apparaît jamais dans la liste des mods, mais tout ce qui lit un identifiant de mod lit le vôtre :

- Les noms de registre sont `mypack:ruby_ore`, exactement comme ceux d'un mod, et ils sont écrits dans chaque monde sauvegardé qui les contient.
- Les listes blanches de minerais, de biomes, de générateurs et de recettes de la configuration le reconnaissent, de sorte que `oreWhitelist = mypack` conserve vos minerais et vos blocs et ceux de personne d'autre.
- `/rdpl which`, `/rdplserver oregen` et les rapports regroupent tous par namespace.
- JEI, le dictionnaire des minerais et les recherches des autres mods le voient de la même façon.

Choisissez donc un nom dès le départ et ne le changez jamais. Renommer un namespace rend orphelin tout ce qui est déjà placé dans un monde, exactement comme lorsqu'un mod change son identifiant ; c'est ce que `registry_remap` existe pour réparer.

Cela fonctionne dans les deux sens : `requires` accepte un namespace de pack aussi volontiers qu'un identifiant de mod installé, de sorte qu'un pack peut dépendre d'un autre et être ignoré lorsque celui-ci n'est pas installé.

**Un mod manquant arrête le jeu, comme le fait la dépendance d'un mod.** Chaque identifiant de mod nommé par un `requires` n'importe où dans vos packs est transmis à Forge comme une dépendance de ce mod, avant que quoi que ce soit ne se charge. Si l'un d'eux n'est pas installé, vous obtenez l'écran standard des mods manquants qui nomme ce qui est nécessaire, sur un client ou sur un serveur dédié, et rien ne se génère ni ne s'enregistre entre-temps.

Un *pack* manquant, c'est différent. Les namespaces de packs ne sont pas des mods, ils n'atteignent donc jamais cette vérification : la définition est ignorée, une ligne est écrite dans `logs/rdpl.log` pour nommer ce qui manquait, et le jeu continue. Si un bloc que vous attendiez ne se trouve pas dans l'onglet créatif, cette ligne du journal est le premier endroit où regarder.

`requires` n'accepte que des identifiants simples. Il n'existe pas de syntaxe d'intervalle de versions, il peut donc dire qu'un mod doit être présent, mais pas quelle version.

Les deux identifiants propres au mod, `resourcedatapackloader` et `resourcedatapackloader_mixin`, sont réservés. Définir du contenu sous ces identifiants est ignoré et consigné, car cela revendiquerait la propriété de choses que ce mod enregistre. Surcharger les ressources propres à ce mod reste permis, seul l'enregistrement de contenu à cet endroit ne l'est pas.

Chaque tableau ci-dessous suit les conventions de [Lire les tableaux](#lire-les-tableaux).

La plupart des définitions acceptent aussi `requires`, une liste d'identifiants de mods ou de namespaces de packs qui doivent être présents, faute de quoi le fichier est ignoré.

### Options de pack

*fonctionnement des définitions*

Un pack peut contenir un dossier `config` à côté de ses `assets`, avec des fichiers JSON d'options vrai/faux et leurs valeurs par défaut :

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

Un fichier contenant `"hide": true` au niveau supérieur garde les options de ce pack hors de l'écran des options et du fichier généré, tandis que les options continuent de conditionner le contenu à leurs valeurs par défaut. Deux cas le réclament : le contenu qui n'est pas prêt à être publié, et les packs modèles, où les options sont une mécanique qui tient les définitions ensemble plutôt qu'un choix que quiconque devrait faire. Retirez la clé pour les publier. Cela fonctionne aussi option par option : `"hide": true` dans l'objet d'une option ne masque que celle-là, de sorte qu'un pack terminé peut porter un interrupteur pour du contenu inachevé, ou une barrière de modèle, sans que ni l'un ni l'autre n'apparaisse :

    { "enablePackB": { "default": false, "hide": true } }

Comme une option masquée ne peut pas être basculée, une option masquée dont la valeur par défaut est true est en pratique forcée à actif, pour du contenu qui doit rester relié à la mécanique des options sans être un choix.

Une option peut aussi être un objet portant une description, affichée sous son nom dans l'écran des options :

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

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

| Clé | Obligatoire | Valeur | Par défaut | Ce qu'elle fait |
| --- | --- | --- | --- | --- |
| un nom d'option | oui | booléen, ou un objet | | `true` ou `false` est la valeur par défaut de l'option. Un objet porte les trois clés ci-dessous |
| `hide` au niveau supérieur | non | booléen | `false` | Garde les options de ce pack hors de l'écran des options et du fichier généré, tandis qu'elles continuent de conditionner le contenu à leurs valeurs par défaut |
| `default` | oui | booléen | | La valeur de l'option jusqu'à ce que l'utilisateur la change. Un objet sans `default` booléen est ignoré, avec un avertissement |
| `hide` dans une option | non | booléen | `false` | Masque cette seule option, qui ne peut donc pas être basculée et reste à sa valeur par défaut |
| `description` | non | chaîne | aucune | Affichée sous le nom de l'option dans l'écran des options |

Au lancement, les fichiers d'options du pack deviennent un véritable fichier de configuration appartenant à l'utilisateur, nommé d'après le pack, `rdploader/config/PackA.json`, créé avec les valeurs par défaut du pack et fusionné lors des mises à jour du pack, de sorte que les nouvelles options arrivent sans toucher à ce que l'utilisateur a déjà réglé. Les modifications s'appliquent au prochain démarrage du jeu. Les options n'appartiennent qu'aux packs nommés, c'est-à-dire aux zips, puisque le fichier généré porte le nom du pack ; les fichiers isolés sous `rdploader/assets` n'ont pas de nom de pack et ne portent aucune option, alors compressez le contenu isolé dans un pack nommé s'il a besoin d'un interrupteur.

La liste `requires` de n'importe quelle définition peut alors nommer une option avec une entrée `config:` : `"requires": ["config:enableTestingContent"]` n'enregistre ce contenu que tant que l'option est à true, exactement comme un mod manquant le ferait ignorer. Un nom simple vérifie le fichier de chaque pack et tous les packs qui la définissent doivent s'accorder ; `"config:PackA:enableTestingContent"` désigne un seul pack. Une option qu'aucun pack ne définit compte comme false et fait l'objet d'un avertissement unique.

Une entrée `file:` conditionne sur l'existence d'un fichier ou d'un dossier sous le dossier du jeu, pour lier du contenu à quelque chose d'extérieur aux packs propres à RDPL, comme le pack de ressources d'un autre mod : `"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` n'enregistre le contenu que tant que ce fichier exact est installé. Le chemin est relatif au dossier du jeu, toujours avec des barres obliques, et ne peut pas contenir `..`.

### Hériter des définitions

*fonctionnement des définitions*

Une définition de bloc ou d'objet peut partir d'une autre du même genre avec `"inherits"`, en nommant le nom de registre de n'importe quelle variante, puis surcharger ce qui diffère :

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "meta": 0, "hardness": 4.0 } } }

L'enfant copie toutes les caractéristiques du fichier du parent et de la variante nommée, l'ordre des fichiers n'a jamais d'importance, les chaînes se résolvent en commençant par le parent, et un cycle ou un parent manquant est consigné et laisse l'enfant tel qu'il est écrit. Les champs que l'enfant écrit remplacent la valeur héritée ; les propriétés imbriquées des variantes se surchargent une à une, mais les listes comme `requires` se remplacent entièrement, alors écrivez la liste complète voulue. Les blocs n'héritent que de blocs et les objets que d'objets.

### Modèles de blocs et d'objets

*fonctionnement des définitions*

Un parent peut être un pur modèle qui n'entre jamais dans le jeu, puisque l'héritage lit les fichiers de définition eux-mêmes, et non ce qui a été enregistré. Placez le modèle derrière une option masquée forcée à inactif, et il n'enregistre rien tandis que ses caractéristiques restent héritables :

`config/options.json`

```json
{
  "templates": { "default": false, "hide": true, "description": "Never on, parents only" }
}
```

`assets/jacksmod/blocks/ore_template.json`

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
    "ore_template": { "meta": 0, "hardness": 3.0, "resistance": 5.0 }
  }
}
```

`assets/jacksmod/blocks/jacks_ore.json`

```json
{
  "inherits": "jacksmod:ore_template",
  "requires": [],
  "variants": {
    "jacks_ore": { "meta": 0, "hardness": 4.0 }
  }
}
```

Le modèle ne s'enregistre jamais, tandis que `jacks_ore` s'enregistre avec le matériau, le son, l'outil, l'onglet, les gains d'expérience et la résistance du modèle, en ne surchargeant que la dureté. L'enfant doit écrire son propre `requires`, ici vidé en liste vide, car il hérite sinon de celui du parent et disparaîtrait avec lui.

## Ce que vous pouvez surcharger

*fonctionnement des packs*

- **Tout ce qui se trouve dans le dossier de ressources d'un mod**, textures, modèles, états de bloc, fichiers de langue, sons, polices, textes de splash, livres-guides, manuels
- **Progrès et tables de butin**, côté serveur, pour qu'ils fonctionnent aussi sur les serveurs dédiés
- **Recettes**, remplacer la recette d'un mod ou ajouter la vôtre
- **Modèles de structures**, les fichiers `.nbt` que les mods utilisent pour les bâtiments générés, sous `<namespace>/structures/`
- **Fonctions**, les fichiers `.mcfunction` sous `<namespace>/functions/`
- **Renommages du registre**, pour que les anciens mondes continuent de fonctionner lorsqu'un mod renomme un bloc ou un objet
- **Suppressions de recettes**, supprimer une recette d'artisanat par nom, namespace ou résultat
- **Blocs et objets désactivés**, retirer n'importe quel bloc ou objet du jeu, voir [Blocs et objets désactivés](#blocs-et-objets-désactivés)
- **Injections de butin**, ajouter une réserve à une table de butin au lieu de remplacer l'ensemble
- **Butin des joueurs**, tirer une table de butin à la mort d'un joueur, en plus de ce qu'il portait ou à la place
- **Butins des blocs**, ajouter à ce que laisse n'importe quel bloc lorsqu'un joueur le casse, ou le remplacer
- **Propriétés des blocs, objets et potions existants**, dureté, lumière, tailles de pile, nourriture sur n'importe quoi, les effets d'une potion, voir [Surcharges de propriétés](#surcharges-de-propriétés)
- **Noms du dictionnaire des minerais, recettes de fourneau, durées de combustion, onglets créatifs et événements sonores**

RDPL convient pour remplacer une ou deux recettes, et les recettes de votre propre contenu doivent être ajoutées dans le pack lui-même. Pour un contrôle complet des recettes sur un modpack, CraftTweaker et GroovyScript sont de meilleures options, et un fichier placé ici remplace toujours entièrement l'original ; pour changer un ingrédient ou retirer une seule entrée de butin, utilisez donc ceux-là.

## Packs côté serveur

*fonctionnement des packs*

Un pack peut vivre uniquement sur le serveur, avec des joueurs sur des clients vanilla ordinaires, sous une seule contrainte : **rien dans le pack ne doit rien enregistrer**. Les deux identifiants de mod acceptent n'importe quel distant ; c'est le pack qui décide. Un client vanilla joue avec les registres livrés avec lui, donc un pack qui y ajoute quelque chose doit être présent des deux côtés.

| Le serveur seul suffit | Nécessite aussi le pack sur le client |
| --- | --- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures`, `caveregions` | `blocks`, `items`, `fluids`, `materials` |
| `villages`, `pathintersects`, `structuremaps`, `citymaps` | `potions`, `potion_types`, `sounds`, `tabs` |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `oredict`, `disabled` | `biomes`, `dimensions`, `portalframes` |
| `loot_tables`, `loot_injections`, `block_drops`, `anvils`, `player_loot`, `advancements`, `functions` | `villagers` |
| `gates`, `cards`, `registry_remap`, `exposures`, `hardness`, `overrides`, `trades` (pour les professions que le client connaît : celles de vanilla, ou d'un mod présent des deux côtés) | `entities`, `worldintro`, `texts` (l'introduction n'est jamais montrée à un client vanilla) |
| `teams`, `scoring` | `models`, `blockstates`, `textures`, `lang` (dossiers client — sans client, ne les incluez pas) |
| toute la couche de contrôle, les réglages et la prégénération | |

La colonne de droite est un arrêt dur : un client vanilla envoyé vers une dimension inconnue est déconnecté, et les blocs inconnus ne peuvent pas lui être décrits. La colonne de gauche fonctionne parce que tout ce qui s'y trouve s'exécute entièrement côté serveur ou atteint le client par des paquets que vanilla comprend déjà (emplacement de résultat de fabrication rempli par le serveur, paquets de progrès ordinaires, refus de portail transmis par messages d'état, et une retenue de prégénération composée de paquets vanilla de mode de jeu, de titre et de téléportation).

`worldtemplates` est côté serveur avec une exception : **`rubicWorld` ne peut pas être utilisé avec `vanillaClients`**. Un monde Rubic est fait de cubes, et un client sans le mod ne peut pas les recevoir ; il serait donc refoulé à la connexion ou ne verrait rien du tout. Quand les deux sont activés, les nouveaux mondes sont créés ordinaires plutôt que Rubic et le journal en donne la raison, au lieu de laisser un serveur qui refuse tous les joueurs. Activer `vanillaClients` pour un monde qui avait *déjà* été créé comme monde Rubic est le seul cas qui arrête franchement le jeu : charger une telle sauvegarde comme un monde ordinaire la ruinerait, elle est donc laissée intacte pour que vous décidiez.

Mise en place :

1. Activez `vanillaClients` dans la configuration (catégorie `content`, nécessite un redémarrage). Il impose la colonne de droite : ces dossiers sont ignorés au chargement et chaque fichier ignoré est nommé dans le journal, de sorte qu'un fichier de bloc égaré devient une ligne de journal au lieu d'une connexion refusée.
2. Gardez malgré tout les définitions hors des dossiers de droite ; les fichiers ignorés sont du poids mort. Là où le pack fait référence à des objets (le `hold` d'un portail, `killedDrops`, les résultats de recettes, les échanges), ne nommez que des objets que fournissent vanilla ou les autres mods du serveur présents des deux côtés.
3. Laissez de côté les variantes d'entités. Chacune est enregistrée comme une entité à part entière, qu'un client vanilla n'a aucun moyen de faire apparaître ; `vanillaClients` les ignore donc comme il ignore les blocs et les nomme dans le journal ; le `standIn` d'un camp ou une apparition qui en nomme une n'a alors plus rien à créer.
4. Installez sur le serveur comme d'habitude. Rien ne va sur les machines des joueurs ; `/rdpl` n'existera pas pour eux, ils utilisent donc les formes `/rdplserver` : `/rdplserver team join`, `/rdplserver round start`, `round reset`, `round vote yes`. `opens.leaderSays` et `reset.voteSays` nomment `/rdpl` par défaut, formulez-les donc avec `/rdplserver` dans un pack servi à des clients vanilla.
5. Testez avec une connexion propre d'un client vanilla de la même version. Les échecs sont bruyants : la connexion est refusée à la porte, et non défaillante en silence plus tard.
6. Lacunes acceptées :
   - Les recettes ajoutées par le serveur se fabriquent mais n'apparaissent pas dans le livre de recettes.
   - L'introduction au monde n'est pas affichée. Un client vanilla n'est pas attendu non plus : il est accueilli aussitôt, libéré d'une retenue de prégénération avec tous les autres, et ne retarde jamais `/rdpl round start`.
   - Tout ce que ce mod affiche sous forme de carte, comme les résultats, l'avis du chef ou les lignes `saysCard`, arrive sous forme de lignes de chat, et les notes en milieu d'écran, comme celles du hall d'attente, arrivent sous forme de titres.
   - Un groupe de dureté fixe le temps que met le serveur à casser un bloc, mais l'animation de fissures du client suit le rythme habituel du bloc. Une surcharge d'un nombre que le client lit aussi, comme la taille de pile, la durabilité, la dureté ou la lumière, y affiche toujours l'ancienne valeur.
   - Le minage `adventure` d'un groupe de dureté ne fonctionne pas : un client vanilla en mode aventure ne commence jamais à creuser, sauf si l'objet tenu nomme le bloc dans son propre tag `CanDestroy`.
   - Un bloc ou un objet désactivé apparaît toujours dans les onglets créatifs d'un client vanilla, que le client construit lui-même ; tout le reste de la désactivation fonctionne depuis le serveur.

## Renommages du registre

*fonctionnement des packs*

`<namespace>/registry_remap/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers s'empilent.

Lorsqu'un mod renomme l'un de ses blocs ou objets, les mondes sauvegardés avant le renommage les perdent. Déposez un fichier ici pour associer l'ancien nom au nouveau :

```json
{
  "registry": "minecraft:items",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

Le registre est celui auquel appartient l'entrée, généralement `minecraft:items` ou `minecraft:blocks`. Les renommages s'enchaînent : associer A à B puis plus tard B à C envoie A directement vers C.

## API du mod

*fonctionnement des packs*

Un mod peut livrer du contenu RDPL dans son propre jar, sans avoir besoin d'un pack séparé. Placez un dossier nommé `rdploader` à la racine du jar et organisez-le exactement comme un pack :

```
thatmod.jar
  mcmod.info
  rdploader/assets/thatmod/blocks/ruby_ore.json
```

Ce qu'un mod livre est une valeur par défaut, pas une surcharge. Cela se charge sous tous les packs du dossier des packs, de sorte que tout ce qu'écrit l'auteur d'un pack l'emporte, et un mod ne peut fournir que des fichiers sous un namespace qu'il déclare dans son propre `mcmod.info`. Les fichiers sous tout autre namespace sont ignorés avec un avertissement, tout comme un dossier `rdploader` imbriqué dans un namespace, de sorte qu'un mod ne peut pas redéfinir discrètement le contenu d'un autre mod ou celui d'un auteur de pack.

Chaque mod qui en livre un obtient une entrée dans `rdploader/config/mods.json` la première fois qu'il est rencontré :

```json
{
  "thatmod": {
    "enabled": true,
    "priority": -1
  }
}
```

| Champ | Valeurs | Par défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `enabled` | `true` ou `false` | `true` | Désactive le contenu de ce mod, comme `.disabled` désactive un pack |
| `priority` | `-1` ou un nombre | `-1` | `-1` maintient le mod sous tous les packs ; tout autre nombre le place dans l'ordre de [priorité](#organiser-les-packs) ordinaire, à côté des packs numérotés |

Un pack de mod ne rejoint jamais le palier de surcharge des packs de ressources, quoi que dise `overrideResourcePacks`, puisque seul l'auteur d'un pack peut le demander avec la lettre `O`. Le journal marque les packs de mods et liste les packs du plus bas au plus haut, de sorte que rien ne se charge à l'insu de personne.

## Packs écrits pour 1.20.1, 1.21.1 et 26.x

*fonctionnement des packs*

Un pack conçu pour la ligne 1.20.1, 1.21.1 ou 26.x de ce mod se charge ici aussi. Le chargeur en reconnaît un à un format `pack.mcmeta` supérieur à 3, à un dossier `data/` à côté de `assets/`, ou à des fichiers de langue `.json` et à `textures/block/` sans équivalent 1.12.2, et le ramène de la même façon, quelle que soit la ligne pour laquelle il a été écrit. Un zip est converti une seule fois, en lui-même : chaque fichier que 1.12.2 lit différemment est écrit dans le dossier `versions/1.12.2/` du zip, et les fichiers modernes à la racine restent tels quels, de sorte que le même zip se charge toujours sur 1.20.1, 1.21.1 et 26.x, comme le décrit [un seul zip pour toutes les versions](#organiser-les-packs). Le portage marque le dossier qu'il écrit avec un fichier `port.stamp` contenant la version de RDPL. Un zip qui possède déjà un dossier `versions/1.12.2/` est lu à travers lui ; lorsque son `port.stamp` nomme une autre version de RDPL, le portage réécrit le dossier à neuf, en remplaçant chaque fichier qu'il contient et en nommant chacun dans le journal, et un dossier sans `port.stamp`, comme celui qu'aurait écrit l'auteur du pack, n'est plus jamais converti. Parmi les fichiers de la racine, seuls ceux que le portage laisse passer sans changement, comme les sons et les textures en dehors de `textures/block/` et `textures/item/`, sont encore lus. Le zip est d'abord écrit dans un fichier temporaire et ne remplace l'original qu'une fois complet. Les fichiers isolés sous `rdploader/assets` et `rdploader/data` ne sont pas réécrits ; ils sont lus à travers le même portage à chaque analyse du dossier.

Un fichier de bloc moderne revient avec un `meta` pour chaque variante, dans l'ordre où les variantes sont écrites, et ses tags sous forme de noms du dictionnaire des minerais :

```json
{
  "type": "ore",
  "creativeTab": "rdpltest",
  "variants": {
    "cluster": { "meta": 0, "hardness": 3.0 },
    "worm": { "meta": 1, "hardness": 3.0, "oreDict": ["oreTestium"] }
  }
}
```

| Fichier moderne | Fichier 1.12.2 |
| --- | --- |
| `data/<ns>/<folder>/` pour chaque dossier de définitions | `assets/<ns>/<folder>/` |
| `recipe/`, `loot_table/`, `advancement/`, `function/`, `structure/` (1.21.1) | `recipes/`, `loot_tables/`, `advancements/`, `functions/`, `structures/` |
| `data/*/tags/items/` (1.21.1 : `tags/item/`) | `assets/<ns>/oredict/converted_tags.json` |
| `data/minecraft/tags/functions/tick.json` | `assets/<ns>/gamerules/converted_tick.json`, le `gameLoopFunction` de l'Overworld |
| recettes `minecraft:smelting` | `assets/<ns>/furnace/converted_smelting.json` |
| `assets/<ns>/lang/<lang>.json` | `assets/<ns>/lang/<lang>.lang` |
| `textures/block/`, `textures/item/` | `textures/blocks/`, `textures/items/` |
| `models/item/<variant>.json` | `models/item/<file>/<variant>.json` |
| aucun état de bloc (généré sur la ligne moderne) | un état de bloc Forge généré pour chaque fichier de bloc, avec les modèles dont son type a besoin |

- Chaque identifiant vanilla moderne est ramené par une table d'aplatissement livrée dans le jar, construite à partir des data fixers du jeu lui-même, de sorte qu'il ressort comme le bloc ou l'objet 1.12.2 avec sa métadonnée : `minecraft:red_wool` devient `minecraft:wool:14`, `minecraft:oak_log` avec `axis=x` devient `minecraft:log:4`. Là où une clé contient un bloc et une métadonnée séparés, comme `block` dans la worldgen et `modelBlock`, la métadonnée va dans `meta` ou `modelMeta` ; un `soil` conserve le bloc entier. Les identifiants propres au pack se résolvent par ses propres fichiers : `mypack:worm` devient `mypack:test_ore:1`. Les noms d'entités, de biomes, de tables de butin, de sons, de particules et d'attributs sont ramenés de la même façon, et une dimension nommée devient un nombre : celles du pack prennent leur `id` ou, à défaut, un nombre stable à partir de 1000 que le journal nomme.
- Les tags deviennent des noms du dictionnaire des minerais par l'inverse de la correspondance de convention : `forge:gems/testium` et `c:gems/testium` deviennent `gemTestium`, `minecraft:logs` devient `logWood`. Le `tag` d'un ingrédient de recette devient un ingrédient `forge:ore_dict` et la recette devient `forge:ore_shaped` ou `forge:ore_shapeless` ; le `tag` d'un combustible devient `oreDict`. Chaque objet de recette reçoit un `data`, puisque 1.12.2 refuse un objet avec sous-types sans lui.
- Le `block.mypack.worm` d'un fichier de langue devient `tile.mypack:test_ore.worm.name`, `item.` de la même façon sous `item.`, `itemGroup.mypack.tab` devient `itemGroup.tab` et `fluid.mypack.x` devient les deux clés de fluide 1.12.2.
- Les tables de butin perdent ce que 1.12.2 ne sait pas lire : la variante d'un objet devient `set_data`, les fournisseurs de nombres deviennent `min` et `max`, les réserves reçoivent un `name`, les entrées `alternatives` et `group` sont aplaties, et une fonction, une condition ou un type d'entrée que 1.12.2 n'a pas est écarté avec une ligne dans le journal. Le `items` d'un progrès devient `item` et `data`, et un `tag` devient `forge:ore_dict`.
- Les fonctions sont réécrites ligne par ligne dans la syntaxe 1.12.2 : `execute as ... at @s run` devient `execute <entity> ~ ~ ~`, `execute if block` devient `detect`, `tag` et `team` deviennent `scoreboard players tag` et `scoreboard teams`, `data merge` devient `blockdata` et `entitydata`, et les sélecteurs troquent `distance`, `scores`, `limit` et `gamemode` contre `r`, `score_X_min`, `c` et `m`. Le `SpawnData` d'un générateur perd son enveloppe `entity`. Une ligne que le portage ne sait pas ramener, comme `bossbar` ou une ligne de macro, est transformée en commentaire et le journal nomme le fichier, la ligne et la raison, de sorte que la fonction se charge quand même.
- Un `.nbt` de structure au-delà de la version de données 1343 voit sa palette, ses générateurs, ses piles d'objets et ses identifiants d'entités ramenés ; un bloc sans équivalent 1.12.2 est laissé tel qu'il est écrit et place de l'air.
- Le sol d'un monde plat moderne se trouve au bas du monde, et 1.12.2 pose un monde plat à partir de y 0, si bien que le portage remonte les altitudes de 64 (du `worldMinHeight` du modèle lorsqu'il en nomme un) : le `worldSpawn` et le `resetSendsTo` d'un modèle plat, les altitudes d'apparition d'une équipe, le `groundLevel` d'une dimension plate, et chaque y absolu dans les fonctions lorsque l'Overworld du pack est plat.
- Écartés, chacun avec une ligne dans le journal : la worldgen pilotée par les données de vanilla, les types de dimensions, les types de dégâts, les enchantements et les autres registres que 1.12.2 n'a pas ; les tags de blocs, d'entités et de fluides ; les tags de fonctions autres que `tick` ; la taille de pierre, la forge et les autres types de recettes que 1.12.2 n'a pas ; les composants d'objets ; les clés de structure que seuls les mondes modernes génèrent ; un nom `behavesAs` autre que `till`, `path`, `bush` et `animals` ; `jobSite`, et une profession sans `careers` en reçoit un nommé d'après son fichier.

Le journal contient une ligne de synthèse par pack converti et une ligne pour chaque fichier qu'il a déplacé, converti, écarté ou n'a pas pu ramener, et chaque clé que 1.12.2 ne lit pas est toujours nommée par l'analyseur qui la rencontre. Le portage est un effort au mieux, pas un pack terminé : lisez ces lignes et terminez à la main ce qu'elles nomment, en commençant par tout état de bloc généré dont il n'a pas trouvé les textures. Faites ces corrections à la racine du pack ou dans un zip séparé, jamais dans le dossier `versions/1.12.2/` que le portage a écrit : une autre version de RDPL réécrit ce dossier à neuf.

---

# Blocs et objets

## Blocs

*blocs et objets*

`<namespace>/blocks/*.json`

Le chemin du fichier est le nom de registre du bloc, de sorte que `mypack/blocks/ruby_ore.json` enregistre `mypack:ruby_ore`. Les clés à l'intérieur de `variants` nomment les valeurs de métadonnée de ce seul bloc ; ce ne sont pas des blocs à part entière.

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
  "modelMeta": 0,
  "itemModel": "state",
  "tint": "biome",
  "plantTypes": ["Plains", "Crop"],
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
  "portal": { "dimension": 12 },
  "variants": {
    "ruby_ore": {
      "meta": 0,
      "hardness": 3.0,
      "resistance": 5.0,
      "light": 0,
      "harvestLevel": 2,
      "rarity": "rare",
      "maxSize": 64,
      "oreDict": ["oreRuby"],
      "drops": [
        { "block": "mypack:ruby", "amount": { "min": 1, "max": 2 }, "bonusChance": [1, 2] }
      ]
    },
    "deep_ruby_ore": {
      "meta": 1,
      "hardness": 4.5,
      "resistance": 8.0,
      "light": 3
    }
  }
}
```

### Types

*blocs*

| Type | Ce que vous obtenez |
| --- | --- |
| `basic` | Un bloc ordinaire. Utilisé lorsque `type` est absent |
| `ore` | Laisse autre chose que lui-même, avec Fortune et Toucher de soie |
| `falling` | Tombe comme le sable ou le gravier |
| `slab` | Bas, haut et double, et deux dalles fusionnent en main |
| `stairs` | Coins et pentes gérés pour vous |
| `fence` | Se connecte à ses voisins, et aux barrières d'autres mods |
| `pane` | Se connecte comme les vitres |
| `wall` | Se connecte comme les murets de pierre, avec la forme de poteau |
| `door` | Haute de deux blocs, s'ouvre à la main et répond à la redstone. Utilise une seule variante, puisque le reste de la métadonnée porte la charnière, l'orientation et l'état ouvert ou non |
| `trapdoor` | Un battant à charnière sur le haut ou le bas d'un bloc, ouvert à la main ou par la redstone. Une seule variante, la métadonnée porte l'orientation, la moitié et l'état ouvert ou non |
| `fence_gate` | Un portillon dans une ligne de barrières, ouvert à la main ou par la redstone, et abaissé là où il rencontre un muret. Une seule variante |
| `banner` | Une bannière sur un poteau ou contre un mur, seize rotations debout, portant votre propre motif. Enregistre un second bloc nommé `<name>_wall` pour la bannière accrochée |
| `ladder` | Escaladable, posée contre un mur |
| `torch` | Pose murale et au sol, avec une particule |
| `bell` | Une cloche comme celle des villages à partir de 1.14 : elle sonne lorsqu'on l'utilise sur son côté, par la redstone ou lorsqu'un projectile la frappe, se balance dans son cadre, et fait briller les pillards proches. Une seule variante, la métadonnée porte l'orientation et la façon dont elle est suspendue |
| `log` | Pivote vers la face contre laquelle vous la placez, et est enregistrée comme `logWood` dans le dictionnaire des minerais pour que l'abattage d'arbres et Blast Plaster la traitent comme un tronc |
| `leaves` | Se dégrade, se coupe à la cisaille, se teinte et laisse une pousse, et est enregistré comme `treeLeaves` dans le dictionnaire des minerais |
| `sapling` | Pousse en arbre ou en l'une de vos structures |
| `crop` | Pousse par étapes, laisse une graine et un produit |
| `flower` | Une plante d'un bloc dressée sur le sol |
| `cane` | Pousse vers le haut en colonne, comme la canne à sucre ou le cactus |
| `vine` | Grimpe et pend sur les côtés des blocs |
| `portal` | Envoie tout ce qui y entre vers une autre dimension |
| `container` | Contient un inventaire qu'un joueur peut ouvrir, de n'importe quelle taille, et peut se remplir à partir d'une table de butin à sa première ouverture. S'affiche comme un bloc ordinaire ou comme un coffre, selon ce que demande le pack |

### Clés de fichier

*blocs*

| Clé | Obligatoire | Valeur | Par défaut | Ce qu'elle fait |
| --- | --- | --- | --- | --- |
| `variants` | oui | objet associant nom de variante et variante | | Une entrée par valeur de métadonnée. La clé nomme cette valeur dans l'état de bloc, le chemin du modèle et la clé de langue. Le nom de registre provient du chemin du fichier lui-même |
| `type` | non | l'un des types ci-dessus | `basic` | La forme que prend le bloc |
| `material` | non | l'un des [matériaux de bloc](#listes-de-valeurs) | `rock` | Comportement au minage, pistons, feu et liquides |
| `soundType` | non | l'un des [types de son](#listes-de-valeurs) | `stone` ; `wood` pour un `log`, `plant` pour `leaves` et un `crop`, celui du `modelBlock` pour `stairs` et un `wall` | Pas, destruction et pose |
| `mapColor` | non | l'une des [couleurs de carte](#listes-de-valeurs) | selon le matériau | Son apparence sur une carte |
| `harvestTool` | non | `pickaxe`, `axe`, `shovel` | `pickaxe` | Quel outil le récolte |
| `harvestToolLevel` | non | 0 à 3 | `0` | 0 bois, 1 pierre, 2 fer, 3 diamant |
| `silkHarvest` | non | booléen | `true` | Si Toucher de soie rend le bloc lui-même |
| `opensWith` | non | identifiant d'objet | aucun | Fait du bloc un coffre verrouillé : le casser laisse le bloc lui-même, et un clic droit avec l'objet nommé en consomme un, joue le son de destruction du bloc, verse la liste `drops` de la variante et retire le bloc. Tout autre clic affiche dans la barre d'action la ligne `tile.<pack>:<block>.<variant>.locked` tirée des fichiers de langue |
| `openSound` | non | nom de son | le son de destruction | Ce que joue un coffre verrouillé à l'ouverture à la place de son son de destruction |
| `expDrop` | non | objet avec `min` et `max` | aucun | Expérience laissée lorsqu'il est cassé sans Toucher de soie |
| `creativeTab` | non | nom d'onglet | aucun | L'onglet où il apparaît |
| `renderLayer` | non | `solid`, `cutout`, `cutout_mipped`, `translucent` | adapté au type | Comment il est dessiné |
| `opaque` | non | booléen | `true` | S'il bloque entièrement la vue et la lumière |
| `fullCube` | non | booléen | identique à `opaque` | S'il remplit tout son espace |
| `lightOpacity` | non | 0 à 255 | `255` si opaque, sinon `0` | Quelle quantité de lumière il absorbe |
| `slipperiness` | non | float | `0.6` | La glace vaut `0.98` |
| `flammability` | non | int | `0` | Avec quelle facilité le feu le consume |
| `fireSpread` | non | int | `0` | Avec quelle facilité le feu s'en propage |
| `explosionResistanceDivisor` | non | float | `1.0` | Divise la `resistance` de chaque variante face aux explosions |
| `modelBlock` | non | nom de bloc | `minecraft:stone` | Bloc dont le modèle est emprunté lorsque le vôtre n'en a pas |
| `modelMeta` | non | int | `0` | Quelle variante de ce modèle |
| `itemModel` | non | `state`, `item` | `state` | `state` suit l'état de bloc, `item` cherche son propre fichier |
| `tint` | non | `biome`, `none`, ou une couleur hexadécimale | aucun | Nécessite un `tintindex` dans le modèle pour s'afficher |
| `plantTypes` | non | liste de [types de plantes](#listes-de-valeurs) | aucun | Ce qui peut être planté dessus |
| `behavesAs` | non | liste de `till`, `path`, `bush`, `animals` | aucun | Comportements vanilla à adopter |
| `bounds` | non | liste de six nombres, 0 à 1 | bloc plein | La boîte de collision, sous la forme `[x1, y1, z1, x2, y2, z2]` |
| `requires` | non | liste d'identifiants de mods ou de namespaces de packs | aucun | Le fichier est ignoré sauf si tous sont présents |
| `particle` | torch uniquement | `none`, `flame`, `colored` | `flame` | La particule au-dessus d'une torche |
| `particleColor` | torch uniquement | couleur hexadécimale | `FFFFFF` | Utilisée lorsque `particle` vaut `colored` |
| `smoke` | torch uniquement | booléen | `true` | Si elle fume |
| `leafSapling` | leaves uniquement | nom de bloc | aucun | La pousse qu'elles laissent |
| `leafSaplingChance` | leaves uniquement | int | `5` | Une feuille sur N en laisse une |
| `seed` | crop uniquement | nom d'objet | `minecraft:wheat_seeds` | L'objet qui le plante, et ce que laisse une culture non mûre |
| `produce` | crop uniquement | nom d'objet | `minecraft:wheat` | Ce que donne la récolte |
| `maxAge` | crop uniquement | int | `7` | Combien d'étapes de croissance |
| `growth` | plants uniquement | objet | aucun | Voir [Croissance](#croissance) |
| `sapling` | sapling uniquement | objet | aucun | Voir [Pousses](#pousses) |
| `portal` | portal uniquement | objet | aucun | Voir [Portails et passages](#portails-et-passages) |
| `container` | container uniquement | objet | aucun | Voir [Conteneurs](#conteneurs) |
| `bell` | bell uniquement | objet | aucun | Voir [Cloches](#cloches) |

### Clés de variantes

*blocs*

| Clé | Obligatoire | Valeur | Par défaut | Ce qu'elle fait |
| --- | --- | --- | --- | --- |
| `meta` | oui | 0 à 15 | | La valeur de métadonnée que revendique cette variante |
| `hardness` | non | float | `1.0` | Le temps qu'il faut pour le casser. L'obsidienne vaut `50`, `-1` est incassable |
| `resistance` | non | float | `5.0` | Résistance aux explosions |
| `light` | non | 0 à 15 | `0` | Lumière émise |
| `harvestLevel` | non | 0 à 3 | `0` | Surcharge le niveau d'outil pour cette variante |
| `rarity` | non | `common`, `uncommon`, `rare`, `epic` | `common` | Couleur du nom dans l'infobulle |
| `maxSize` | non | 1 à 64 | `64` | Taille de pile |
| `oreDict` | non | liste de noms du dictionnaire des minerais | aucun | Noms du dictionnaire des minerais sous lesquels cette variante est enregistrée |
| `drops` | non | liste de butins | se laisse lui-même | Ce que donne sa destruction |

**La métadonnée est permanente.** Le nombre qu'une variante revendique est écrit dans chaque monde sauvegardé qui la contient. Renuméroter ou réordonner les variantes plus tard transforme les blocs placés en autre chose. Ajoutez les nouvelles variantes à la fin et ne réutilisez jamais un numéro.

Un bloc `basic` peut contenir seize variantes ; un `slab` huit ; `log` et `leaves` quatre, parce que les indicateurs d'axe et de dégradation ont besoin de bits à eux ; les types à état unique en contiennent une.

### Butins

*blocs*

```json
{
  "drops": [
    { "block": "mypack:ruby", "meta": 0, "amount": { "min": 1, "max": 3 }, "chance": 100, "guaranteed": true, "bonusChance": [1, 2, 3] },
    { "block": "minecraft:coal", "amount": 1, "chance": 25 },
    { "block": "minecraft:diamond", "weight": 1 },
    { "block": "minecraft:emerald", "weight": 4 },
    { "entity": "minecraft:silverfish", "amount": { "min": 1, "max": 2 }, "chance": 15 }
  ]
}
```

| Clé | Obligatoire | Valeur | Par défaut | Ce qu'elle fait |
| --- | --- | --- | --- | --- |
| `block` | l'un des deux | nom de bloc ou d'objet | | Ce qui est laissé |
| `entity` | l'un des deux | nom d'entité | | Une entité libérée à la destruction du bloc, à la place d'un objet |
| `meta` | non | int | `0` | Quelle variante de celui-ci |
| `amount` | non | int ou intervalle | `1` | Combien |
| `chance` | non | 0 à 100 | `100`, ou `0` lorsque `guaranteed` est désactivé | Avec quelle fréquence le butin se produit |
| `weight` | non | int | `0` | Au-dessus de zéro, l'entrée rejoint une réserve qui donne exactement un butin. Voir ci-dessous |
| `bonusChance` | non | liste d'ints | aucun | Butins supplémentaires par niveau de Fortune, une entrée par niveau |
| `guaranteed` | non | booléen | `true` | Raccourci historique pour `chance`. Activé vaut `100`, désactivé vaut `0` |

Chaque entrée sans `weight` est décidée isolément, de sorte qu'un bloc qui en compte trois peut tout laisser, ou rien. Donnez un `weight` aux entrées et elles cessent d'être indépendantes : elles forment une seule réserve, dont exactement une est choisie à chaque destruction du bloc, avec des probabilités proportionnelles aux poids. Ci-dessus, le diamant et l'émeraude partagent une réserve à un contre quatre, de sorte que l'un des deux sort toujours et que c'est l'émeraude quatre fois sur cinq, tandis que le rubis et le charbon sont décidés séparément et que le poisson d'argent est encore une affaire à part. Les objets et les entités se mettent en réserve séparément, de sorte qu'un objet pondéré et une entité pondérée ne se concurrencent pas.

Une entrée qui nomme une `entity` en libère une là où se tenait le bloc, orientée au hasard, et un mob reçoit son traitement d'apparition habituel selon la difficulté locale, de sorte qu'il arrive avec l'équipement et les effets qu'il aurait eus. `amount` décide combien, `chance` à quelle fréquence, `weight` la place dans la réserve d'entités. Cela se produit à la destruction du bloc, quelle qu'en soit la cause, de sorte qu'une explosion ou un piston les libère tout comme une pioche. `meta`, `bonusChance` et Fortune ne signifient rien pour une entité et sont ignorés.

Un butin qui nomme à la fois un `block` et une `entity` utilise l'entité et le signale dans le journal.

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

| Clé | Obligatoire | Valeur | Par défaut | Ce qu'elle fait |
| --- | --- | --- | --- | --- |
| `stages` | non | int | `16` | Étapes de croissance avant la fin |
| `growth` | non | int | | Une chance sur N par tick aléatoire d'avancer |
| `spread` | non | int | `0` | Jusqu'où il se propage aux blocs voisins |
| `maxHeight` | non | int | `3` | Cane uniquement. La hauteur que la colonne atteint |
| `drop` | non | nom d'objet | aucun | Ce qu'il laisse lorsqu'il est cassé |
| `dropCount` | non | int | `1` | Combien |
| `needsSky` | non | booléen | `false` | Ne pousse que là où le ciel est visible |
| `needsWater` | non | booléen | `false` | Ne pousse que près de l'eau |
| `waterRange` | non | int | `1` | À quelle distance cette eau peut se trouver |
| `damage` | non | booléen | `false` | Blesse tout ce qui le touche |
| `damageAmount` | non | float, demi-cœurs | `1.0` | À quel point il blesse |
| `breaksNeighbors` | non | booléen | `false` | Casse les blocs placés à côté, comme le cactus |

### Pousses

*blocs*

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin.

```json
{
  "sapling": {
    "soil": ["minecraft:grass", "minecraft:dirt"],
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

Un `structure` remplace l'arbre généré par l'un de vos modèles, ce qui permet de construire ce qu'un générateur ne peut pas, et rien d'autre dans le bloc n'a besoin d'être écrit. Nommez-en plusieurs sous `structures` à la place et la pousse en choisit un à chaque croissance, pour qu'un bois ne soit pas le même arbre à l'infini :

```json
{
  "sapling": { "structure": "mypack:ruby_tree" }
}
```

| Clé | Obligatoire | Valeur | Par défaut | Ce qu'elle fait |
| --- | --- | --- | --- | --- |
| `soil` | non | liste de noms de blocs | aucun | Ce sur quoi elle poussera |
| `stages` | non | int | `2` | Étapes de croissance avant de devenir un arbre |
| `chance` | non | int | `7` | Une chance sur N par tick aléatoire |
| `light` | non | 0 à 15 | `9` | Niveau de lumière requis |
| `log` | non | nom de bloc | `minecraft:log` | Bloc de tronc |
| `leaves` | non | nom de bloc | `minecraft:leaves` | Bloc de feuillage |
| `height` | non | int | `4` | Hauteur du tronc |
| `vines` | non | booléen | `false` | Faire pendre des lianes des feuilles |
| `structure` | non | `namespace:name` | aucun | Pousse en ce modèle au lieu d'un arbre généré |
| `structures` | non | liste | aucun | Plusieurs modèles en lesquels pousser, un choisi à chaque croissance. Chaque entrée est `{ "structure": "namespace:name", "weight": 3 }`, ou un simple nom pour des chances égales. Surcharge `structure` |

## Conteneurs

*blocs et objets*

`<namespace>/blocks/*.json`

```json
{
  "type": "container",
  "material": "wood",
  "creativeTab": "decorations",
  "container": {
    "rows": 6,
    "columns": 9,
    "lootTable": "minecraft:chests/simple_dungeon",
    "chestModel": true
  },
  "variants": [ { "name": "crate", "hardness": 2.5 } ]
}
```

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `rows` | entier | `3` | Nombre de rangées d'emplacements, de 1 à 9 |
| `columns` | entier | `9` | Nombre d'emplacements par rangée, de 1 à 12 |
| `lootTable` | texte | vide | Une table de butin tirée dans le bloc la première fois qu'un joueur l'ouvre, exactement comme se remplit un coffre de donjon. Vide, il reste vide au départ |
| `chestModel` | booléen ou texte | `false` | S'affiche comme un coffre à couvercle qui s'ouvre, et non comme un bloc ordinaire issu de votre propre modèle. `true` utilise la texture de coffre vanilla ; un nom de texture tel que `mypack:blocks/strongbox_chest` utilise votre propre feuille de coffre, pour le bloc placé comme pour l'objet. Donnez à l'état de bloc le modèle `resourcedatapackloader:pack_chest`, avec le même nom sous `texture`, pour que l'objet tenu en main ait lui aussi la forme d'un coffre. Un bloc à modèle de coffre met aussi `opaque` à `false` par défaut, comme un coffre vanilla, de sorte que la lumière n'est pas coupée au niveau du bloc et que le coffre n'est pas dessiné sombre. |
| `guiTexture` | texte | vide | Votre propre image de fond pour l'écran. Vide, l'écran est dessiné à partir de l'écran de coffre vanilla, à la taille qu'exigent les rangées et les colonnes |
| `guiWidth` | entier | aucun | Largeur de cette image, obligatoire avec `guiTexture` |
| `guiHeight` | entier | aucun | Hauteur de cette image, obligatoire avec `guiTexture` |
| `bauble` | texte | vide | Pour un objet uniquement : l'emplacement Baubles où il peut être porté — `amulet`, `ring`, `belt`, `trinket`, `head`, `body` ou `charm`. Un sac à dos prend généralement `body` ou `charm`. Ignoré, sans que rien d'autre dans l'objet cesse de fonctionner, quand Baubles n'est pas installé. Chaque nom correspond à une case de l'onglet Baubles : un objet demandant `body` va dans cette case et aucune autre ; `ring` désigne les deux cases d'anneau et `trinket` convient à toutes les cases. Baubles est une dépendance facultative : ce mod se charge après lui quand il est présent et fonctionne sans lui quand il est absent, si bien qu'un pack nommant un emplacement ne pose aucun problème sur un serveur qui n'a jamais entendu parler de Baubles. |

**Neuf rangées sur douze, c'est le plafond**, soit le plus grand format qu'offre Iron Chest et le maximum qu'un écran peut contenir. Un pack qui en demande davantage est ramené à cette limite, avec une ligne d'erreur qui le signale. Un avertissement à propos du format le plus haut : un écran de neuf rangées mesure 276 pixels, et un affichage 1080 avec l'échelle d'interface `auto` en donne 270, si bien que le haut et le bas sont rognés de trois pixels chacun — l'échelle 3 l'affiche en entier. Iron Chest tient sur neuf rangées parce qu'il fournit ses propres textures, plus compactes ; un pack qui veut la même chose peut définir `guiTexture` et dessiner la sienne.

**L'écran est dessiné, pas fourni.** Un conteneur de neuf colonnes ou moins et de six rangées ou moins utilise tel quel l'écran de coffre vanilla, et ressemble donc exactement à un coffre de cette taille. Tout ce qui est plus grand est assemblé à partir de la même image au moment du dessin -- le bord supérieur, une rangée d'emplacements répétée autant que nécessaire, et le bas avec l'inventaire du joueur -- de sorte qu'un pack peut demander des tailles qu'aucun écran vanilla ne couvre sans fournir d'image. `guiTexture` remplace tout cela quand un pack veut son propre aspect ; `guiWidth` et `guiHeight` doivent alors en donner la taille, faute de quoi l'écran dessiné est utilisé et une ligne d'erreur le signale.

**Ce que fait le bloc.** Il conserve son contenu à travers une sauvegarde et un rechargement, le lâche quand on le casse, répond à un comparateur selon son remplissage, et peut être renommé dans une enclume comme un coffre. `chestModel` lui donne aussi le son d'ouverture du coffre et l'animation du couvercle ; désactivé, le bloc est dessiné d'après le modèle que nomme votre propre `modelBlock`, si bien qu'une caisse, un tonneau ou une armoire fonctionnent tous.

**Colorer un coffre.** La feuille de coffre est une texture ordinaire : une carte de pixels peut donc recolorer celle de vanilla sans dessiner un seul pixel. Faites-lui `extends` de la texture et donnez-lui un `tint`, puis nommez cette carte dans `chestModel` et comme `texture` du modèle.

```json
{
  "extends": "minecraft:textures/entity/chest/normal",
  "tint": {
    "from": "#241A12",
    "to": "#D8BC80"
  }
}
```

Le bloc placé et l'objet tenu en main lisent ce même nom, ils sont donc assortis. Ne le nommer que dans l'un des deux laisse l'autre au brun vanilla.

**Un objet conteneur peut être porté.** Donnez-lui `bauble` et, là où Baubles est installé, il se place dans cet emplacement et une touche l'ouvre sans l'enlever — `V` par défaut, modifiable sous Resource Data Pack Loader dans les contrôles. `B` est la touche que Baubles utilise pour son propre onglet : les deux ne partagent donc pas de touche. Appuyer de nouveau, alors qu'un conteneur porté est déjà ouvert, passe au suivant parmi ceux que vous portez et reboucle, si bien que plusieurs conteneurs portés en même temps sont tous accessibles. La touche n'apparaît que lorsque Baubles est présent, et tout le reste de l'objet, le clic droit et son inventaire, fonctionne qu'il le soit ou non. Baubles n'a pas d'emplacement de sac à dos propre ; `body` et `charm` sont les deux qu'un sac à dos prend généralement.

**La table de butin se remplit à la première ouverture**, et non quand le bloc est placé, ce qui la rend utile dans une structure : celui qui l'ouvre le premier obtient le tirage. La même table peut servir à `lootTable` sur une forme d'empreinte ou une parcelle de village, de sorte qu'un pack peut placer ces blocs par la génération du monde et les garnir de la même façon.

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
    "sound": "minecraft:block.note.bell",
    "resonateSound": "minecraft:block.note.chime"
  },
  "variants": { "village_bell": { "meta": 0, "hardness": 5.0, "resistance": 30 } }
}
```

Et son état de bloc, `assets/mypack/blockstates/village_bell.json` :

```json
{
  "forge_marker": 1,
  "defaults": { "model": "mypack:bell_floor" },
  "variants": {
    "inventory": [{ "model": "mypack:bell_item" }],
    "body": [{ "model": "mypack:bell_body" }],
    "facing": { "north": { "y": 0 }, "east": { "y": 90 }, "south": { "y": 180 }, "west": { "y": 270 } },
    "attachment": {
      "floor": { "model": "mypack:bell_floor" },
      "ceiling": { "model": "mypack:bell_ceiling" },
      "single_wall": { "model": "mypack:bell_wall" },
      "double_wall": { "model": "mypack:bell_between_walls" }
    }
  }
}
```

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `swing` | booléen | `true` | Dessine le corps à partir de son propre modèle et le fait osciller quand la cloche sonne. `false` dessine toute la cloche comme un seul bloc immobile, sans rien d'animé |
| `sound` | nom de son | `minecraft:block.note.bell` | Joué quand la cloche sonne. Vide, elle sonne en silence |
| `resonateSound` | nom de son | `minecraft:block.note.chime` | Joué quand la cloche résonne parce que des pillards sont proches. Vide, elle résonne en silence |

**Elle est suspendue comme la cloche du jeu à partir de la 1.14.** Posée sur un bloc, elle repose sur le sol, tournée dans le sens où vous regardez ; sous un bloc, elle pend du plafond ; contre un mur, elle pend de ce mur, et entre deux murs quand le côté opposé est lui aussi plein. Elle tombe quand ce qui la tient disparaît, et une cloche entre deux murs devient une cloche à un seul mur quand l'un des deux disparaît. Sa boîte de collision suit celle de vanilla pour chacun des quatre cas, `bounds` n'est donc pas lu.

**Ce qui la fait sonner.** Utiliser le côté du corps, sous la poutre : pour une cloche posée au sol, sur les deux faces que traverse sa poutre ; pour une cloche murale, sur les deux faces à côté du mur ; pour une cloche de plafond, sur n'importe quel côté. Le dessus, le dessous et tout ce qui se trouve au-dessus du corps ne font rien. Un signal de redstone la fait sonner une fois à son activation, et une flèche, une boule de neige ou tout autre projectile la fait sonner quand il frappe un côté qu'une main pourrait atteindre. Le corps s'écarte du côté frappé pendant deux secondes et demie ; la redstone le fait osciller dans le sens où la cloche est orientée.

**Ce que fait une sonnerie.** Les villageois situés à moins de 32 blocs l'entendent et rentrent à l'abri pendant quinze secondes, jusqu'à la porte la plus proche que connaît leur village. Quand un pillard se trouve à moins de 32 blocs, la cloche résonne un quart de seconde après la sonnerie, et deux secondes plus tard chaque pillard à moins de 48 blocs luit pendant trois secondes, avec des particules colorées à côté de la cloche, du côté où chacun se tient. Est pillard tout ce qu'un [raid](#raids) a envoyé, ainsi que les illageois et les sorcières du jeu. Une cloche de ce type est une cloche de village pour tous les raids : elle sonne à l'arrivée de chaque vague sans être nommée dans le `bell` du raid.

**Les modèles.** Une cloche n'a pas de propriété `blocks`, son état de bloc est donc indexé par `facing` et `attachment`. Avec `swing` activé, ces modèles ne dessinent que le cadre, et la partie oscillante est une entrée supplémentaire nommée `body`, modélisée dans l'espace du bloc là où elle repose ; elle pivote autour du point situé à un demi-bloc vers l'intérieur et aux trois quarts de la hauteur, comme celle de vanilla. L'entrée `inventory` est l'objet, cadre et corps ensemble. Avec `swing` désactivé, il n'y a pas d'entrée `body`, et les quatre modèles de cadre dessinent aussi la cloche.

**L'oscillation est dessinée par le client.** Une sonnerie parvient aux joueurs sous forme d'événement de bloc : un serveur dédié fait donc osciller la cloche pour tous ceux qui ont ce mod, et un joueur qui ne l'a pas ne fait qu'entendre la cloche. Les sons, la résonance et la luminescence se produisent tous sur le serveur.

## Modèles, états de bloc et textures

*blocs et objets*

Définir un bloc ou un objet l'enregistre. Son *apparence* reste un ensemble ordinaire de fichiers de ressources, dans les mêmes dossiers et le même format que ceux que Minecraft utilise déjà, sous votre propre namespace.

```
assets/mypack/blockstates/ruby_ore.json
assets/mypack/models/block/ruby_ore.json
assets/mypack/models/item/ruby/ruby.json
assets/mypack/textures/blocks/ruby_ore.png
assets/mypack/lang/en_us.lang
```

### Nommer les variantes

*modèles, états de bloc et textures*

Tout bloc ayant plus d'une variante reçoit une propriété appelée `blocks`, dont les valeurs sont les noms de variantes de la définition. Un fichier de bloc qui enregistre `ruby_ore` et `deep_ruby_ore` a donc besoin d'un état de bloc avec ces deux variantes :

```json
{
  "variants": {
    "blocks=ruby_ore": { "model": "mypack:ruby_ore" },
    "blocks=deep_ruby_ore": { "model": "mypack:deep_ruby_ore" }
  }
}
```

Un bloc à variante unique conserve lui aussi la propriété `blocks`, sa clé reste donc `blocks=<name>`, mais seulement sur les types qui possèdent cette propriété. Douze types consacrent toute leur métadonnée à leur forme, ne contiennent qu'une variante et n'ont pas de propriété `blocks` : ils s'indexent donc sur leurs propres propriétés seules. [États de bloc par type](#états-de-bloc-par-type) indique lesquels.

Quand le bloc a des propriétés propres, elles sont jointes par des virgules dans l'ordre où l'état les énumère : `blocks=ruby_log,axis=y`, `blocks=ruby_slab,half=bottom`, `blocks=ruby_wall,up=true,north=true`. Un bloc d'escalier n'a pas de propriété `blocks` ; il est donc indexé par `facing=east,half=bottom,shape=straight` et rien d'autre. Deux propriétés sont omises volontairement : la propriété de variante propre à un muret, et `check_decay` et `decayable` d'un bloc de feuilles, de sorte que les feuilles n'ont besoin que de `blocks=ruby_leaves`. Une bannière n'a aucune propriété de variante, et s'indexe par `rotation=0` à `15` quand elle est posée au sol, ou `facing=north` contre un mur, ce que traite [Bannières](#bannières).

### États de bloc par type

*modèles, états de bloc et textures*

Deux choses déterminent ce que doit contenir un fichier d'état de bloc : si le type porte la propriété `blocks`, et quelles propriétés il possède en propre.

| Type | Enregistre | Propriétés de l'état de bloc | Variantes |
| --- | --- | --- | --- |
| `basic`, `ore`, `falling` | un bloc | `blocks` | 16 |
| `flower` | un bloc | `blocks` | 16 |
| `portal` | un bloc | `blocks` | 16 |
| `fence`, `pane` | un bloc | `blocks`, `north`, `east`, `south`, `west` | 16 |
| `wall` | un bloc | `blocks`, `up`, `north`, `east`, `south`, `west` | 16 |
| `slab` | deux, `<name>` et `<name>_double` | la demi-dalle `blocks` et `half` ; la dalle double `blocks` seul | 8 |
| `log` | un bloc | `blocks`, `axis`, qui vaut `x`, `y`, `z` ou `none` | 4 |
| `leaves` | un bloc | `blocks` | 4 |
| `stairs` | un bloc | `facing`, `half`, `shape` | 1 |
| `door` | un bloc | `facing`, `half`, `hinge`, `open` | 1 |
| `trapdoor` | un bloc | `facing`, `half`, `open` | 1 |
| `fence_gate` | un bloc | `facing`, `in_wall`, `open` | 1 |
| `banner` | deux, `<name>` et `<name>_wall` | `rotation` pour la bannière posée, de `0` à `15` ; `facing` pour la murale | 1 |
| `ladder`, `torch` | un bloc | `facing`, et une torche ajoute `up` aux quatre murs | 1 |
| `bell` | un bloc | `facing` et `attachment`, qui vaut `floor`, `ceiling`, `single_wall` ou `double_wall`, plus une entrée `body` pour la partie oscillante | 1 |
| `crop` | un bloc | `age`, toujours de `0` à `7`, quel que soit `maxAge` | 1 |
| `cane` | un bloc | `age`, de `0` à `15` | 1 |
| `sapling` | un bloc | `stage`, de `0` à une unité de moins que `stages` | 1 |
| `vine` | un bloc | `up`, `north`, `east`, `south`, `west`, et multipart uniquement | 1 |

Quatre propriétés sont retirées pour vous, écrivez donc les clés sans elles : `powered` sur les portes et les portillons, `variant` sur les murets, et `check_decay` et `decayable` sur les feuilles.

Une culture conserve les huit valeurs d'`age` de vanilla quel que soit `maxAge`, puisque `maxAge` ne décide que de la hauteur de croissance : son état de bloc écrit donc toujours `age=0` à `age=7`.

Deux types enregistrent un second bloc. Le `<name>_double` d'une dalle a besoin de son propre état de bloc, indexé sur `blocks` sans `half`, et ne reçoit jamais d'objet propre. Le `<name>_wall` d'une bannière est traité sous [Bannières](#bannières).

**Vine est le seul type qui ne peut pas utiliser le format Forge**, puisque `forge_marker` ne gère pas le multipart : son état de bloc est donc une simple liste `multipart` vanilla, avec la texture intégrée au modèle.

**Le format Forge est plus court, et c'est celui qu'utilise le pack d'exemple.** Un état de bloc vanilla détaille chaque combinaison comme une clé distincte, ce qui fait quarante clés pour un escalier. Avec `"forge_marker": 1`, le fichier énumère chaque propriété une seule fois et le jeu les combine : les mêmes quarante états tiennent alors en onze entrées :

```json
{
  "forge_marker": 1,
  "defaults": {
    "model": "stairs",
    "textures": {
      "bottom": "mypack:blocks/ruby_brick",
      "top": "mypack:blocks/ruby_brick",
      "side": "mypack:blocks/ruby_brick"
    },
    "uvlock": true
  },
  "variants": {
    "inventory": [{}],
    "facing": { "east": { "y": 0 }, "south": { "y": 90 }, "west": { "y": 180 }, "north": { "y": 270 } },
    "half": { "bottom": {}, "top": { "x": 180 } },
    "shape": {
      "straight": {},
      "inner_left": { "model": "inner_stairs" },
      "inner_right": { "model": "inner_stairs" },
      "outer_left": { "model": "outer_stairs" },
      "outer_right": { "model": "outer_stairs" }
    }
  }
}
```

`defaults` est fusionné dans chaque entrée, un nom de modèle simple tel que `stairs` signifie `minecraft:block/stairs`, et `inventory` est le modèle qu'utilise l'objet tenu en main. Les trois parents d'escalier, `stairs`, `inner_stairs` et `outer_stairs`, prennent les textures `bottom`, `top` et `side`.

**Les types à connexion ajoutent un sous-modèle par côté.** Une barrière, une vitre ou un muret a un booléen par direction, et un `true` colle un autre modèle sur le poteau au lieu de le remplacer :

```json
{
  "forge_marker": 1,
  "defaults": {
    "model": "fence_post",
    "textures": { "texture": "mypack:blocks/ruby_planks" },
    "uvlock": true
  },
  "variants": {
    "blocks": {
      "oak": { "textures": { "texture": "mypack:blocks/ruby_planks" } },
      "birch": { "textures": { "texture": "mypack:blocks/pale_planks" } }
    },
    "north": { "true": { "submodel": { "north": { "model": "fence_side", "uvlock": true } } }, "false": {} },
    "east": { "true": { "submodel": { "east": { "model": "fence_side", "y": 90, "uvlock": true } } }, "false": {} },
    "south": { "true": { "submodel": { "south": { "model": "fence_side", "y": 180, "uvlock": true } } }, "false": {} },
    "west": { "true": { "submodel": { "west": { "model": "fence_side", "y": 270, "uvlock": true } } }, "false": {} }
  }
}
```

Les parents sont `fence_post` et `fence_side` avec une `texture` ; `wall_post` et `wall_side` avec un `wall`, plus `block` pour le cas sans poteau ; et `pane_post`, `pane_side`, `pane_side_alt`, `pane_noside` et `pane_noside_alt` avec un `pane` et un `edge`. Chacun d'eux exige `"uvlock": true`.

Les autres prennent un seul parent. `cube_all` prend un `all`, et c'est ce que veut un bloc `basic`, `ore`, `falling` ou `leaves`. `cube_column` prend un `end` et un `side` ; c'est celui d'un `log`, orienté par `axis`. `cross` prend un `cross` et c'est ce que veut un `flower`, un `cane` ou un `sapling` ; un `crop` utilise ses propres modèles par stade. Une dalle a besoin de deux modèles propres, une moitié basse et une moitié haute, puisqu'elle est dessinée comme une forme et non comme un cube.

**Le pack d'exemple est la référence concrète.** [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) fournit une définition, un état de bloc et des modèles pour chaque type du tableau ci-dessus, au format vanilla comme au format Forge : une forme qui n'a rien d'évident se copie plus vite qu'elle ne se déduit.

### Modèles d'objets

*modèles, états de bloc et textures*

Par défaut, l'objet utilise ce que l'état de bloc donne pour cette variante : rien de plus n'est donc nécessaire. Définir `"itemModel": "item"` sur le bloc lui fait chercher son propre fichier à la place, dans `models/item/<block>/<variant>.json`.

Les objets fonctionnent toujours de cette seconde manière, parce que chaque objet de pack a des sous-types :

```
assets/mypack/models/item/ruby/ruby.json
assets/mypack/models/item/ruby/polished_ruby.json
```

Le chemin est le nom de registre de l'objet, puis le nom de la variante.

Les fluides n'ont besoin d'aucun modèle : il est généré à partir des textures `still` et `flow`.

### Portes, trappes et portillons

*modèles, états de bloc et textures*

Les trois consacrent toute leur métadonnée à la forme qu'ils prennent : chacun est donc une variante unique, et chacun a quelques points à connaître avant d'écrire les fichiers.

**Ils ne portent pas de propriété `blocks`**, leurs états de bloc sont donc indexés par la seule forme : `facing=east,half=lower,hinge=left,open=false` pour une porte, `facing=north,half=bottom,open=false` pour une trappe, `facing=south,in_wall=false,open=false` pour un portillon. Cela fait 32 clés, 16 et 16.

**`powered` est omis des portes et des portillons.** Les deux l'ont bel et bien, et les deux verraient sinon leurs états de bloc doubler pour un axe qui ne change rien de visible. Il est retiré pour vous, exactement comme le jeu le retire de ses propres portes et portillons : écrivez donc les clés sans lui. Les trappes ne l'ont jamais eu.

**Pointez les modèles vers les parents qui acceptent des textures**, et non vers les modèles vanilla finis :

| Type | Parents |
| --- | --- |
| `door` | `block/door_bottom`, `block/door_bottom_rh`, `block/door_top`, `block/door_top_rh` |
| `trapdoor` | `block/trapdoor_bottom`, `block/trapdoor_top`, `block/trapdoor_open` |
| `fence_gate` | `block/fence_gate_closed`, `block/fence_gate_open`, `block/wall_gate_closed`, `block/wall_gate_open` |

Une porte prend deux textures, `bottom` et `top` ; les deux autres en prennent une, `texture`. Les deux modèles de haut de porte utilisent `bottom` pour habiller leur arête supérieure : déclarez donc les deux dans les quatre fichiers, même si ceux du haut semblent n'en demander qu'une. Les variantes de portillon veulent `"uvlock": true`, comme celles du jeu.

**Leurs textures utilisent chaque pixel, et c'est ce qui piège le plus de monde.** Les grandes faces d'une porte sont mappées en `[0, 0, 16, 16]`, l'image entière, et ses arêtes étroites ainsi que son haut et son bas sortent du même carré : colonnes 0 à 3 pour les côtés, 13 à 16 pour le haut et le bas. Une trappe est identique : ses faces planes occupent l'image entière et ses quatre rebords sont pris dans les lignes 13 à 16.

Ne laissez donc aucune marge vide. Dégagez quelques colonnes sur un bord en pensant que la forme est plus étroite que le fichier, et vous découpez une fente en plein milieu de la face et perdez complètement le haut et le bas. Dessinez plutôt le cadre ou les montants dans ces pixels de bord, et ils se liront comme une garniture sur les bords mêmes du bloc.

**Leurs objets diffèrent selon le type.** Celui d'une porte est un sprite plat, `item/generated` sur sa propre `textures/items/<name>.png`, puisqu'une porte en main est dessinée comme une image et non comme une forme. Ceux d'une trappe et d'un portillon ont pour parent un modèle de bloc, la moitié basse et le portillon fermé, comme le fait le jeu avec les siens.

Tous trois prennent le `material` que vous leur donnez. Un portillon est construit sur un bloc qui se fixe de lui-même au bois : ce mod remet donc le matériau au vôtre à l'enregistrement, et un portillon de pierre se mine à la pioche comme la pierre qu'il prétend être.

### Bannières

*modèles, états de bloc et textures*

Une bannière est le seul type où la forme du bloc et la forme du modèle divergent : elle mérite donc d'être exposée en détail.

**Elle enregistre deux blocs.** Une définition vous donne la bannière posée au sol sous votre propre nom et un second bloc nommé `<name>_wall` pour la bannière murale. Les deux ont besoin d'un état de bloc ; seul celui posé au sol reçoit un objet, et cet objet décide lequel des deux il place : la bannière au sol quand vous cliquez sur le dessus d'un bloc, la murale quand vous cliquez sur un côté. Vous ne placez jamais directement le bloc mural et il n'a pas besoin d'objet propre.

**Celle posée au sol a besoin d'un état de bloc Forge.** Sa propriété est `rotation`, qui va de `0` à `15`, car une bannière tourne par seizièmes et non par quarts. Un état de bloc vanilla ne peut pas l'exprimer : son `y` passe par `ModelRotation`, qui n'accepte que 0, 90, 180 et 270 et lève une exception pour tout le reste. Le format Forge accepte n'importe quel angle : les seize entrées s'écrivent donc sous forme de transformation :

```json
{
  "forge_marker": 1,
  "defaults": { "model": "mypack:my_banner" },
  "variants": {
    "rotation": {
      "0": { "transform": { "rotation": { "y": 0 } } },
      "1": { "transform": { "rotation": { "y": -22.5 } } }
    }
  }
}
```

… et ainsi de suite jusqu'à `15`, chacune tournant de `-22.5` degrés de plus. Le signe correspond à celui des bannières du jeu, qui tournent de moins la rotation. Construisez le modèle orienté vers le sud, puisque c'est là que pointe une bannière placée par un joueur qui regarde vers le sud. Le bloc mural est un état de bloc vanilla ordinaire avec les quatre entrées `facing` habituelles à 0, 90, 180 et 270, puisqu'il n'a rien de fractionnaire.

**Le modèle fait presque deux blocs de haut.** Une bannière occupe un bloc pour le placement et la collision, mais elle est dessinée bien au-delà : un modèle qui s'arrête en haut de son propre bloc paraît rabougri. Il vaut la peine de copier exactement les proportions de vanilla, en seizièmes de bloc :

| Partie | De | À |
| --- | --- | --- |
| Poteau | `0` | `28` |
| Traverse | `28` | `29.33` |
| Tissu | `2.67` | `29.33` |
| Largeur du tissu | `1.33` | `14.67` |
| Tissu mural | `-13` | `13.67` |

Une bannière au sol monte donc jusqu'à `29.33`, presque deux blocs, et une bannière murale pend de treize seizièmes *sous* le bloc qui la tient. Les éléments d'un modèle peuvent aller de `-16` à `32` : les deux tiennent. La forme murale n'a ni poteau ni traverse, seulement du tissu.

**Le tissu est deux fois plus haut que large, et votre texture doit l'être aussi.** Cette face mesure `13.33` sur `26.67`. Plaquez-y une texture carrée et le motif est écrasé de moitié en hauteur. Les textures de bloc ne peuvent pas être elles-mêmes deux fois plus hautes que larges, car tout ce qui n'est pas carré est lu comme une animation ; le contournement consiste à utiliser une feuille carrée plus grande où le tissu n'occupe qu'une partie : un fichier de 32×32 contenant le tissu sous forme d'une région de 16×32, adressée par `"uv": [0, 0, 8, 16]`, avec les bandes du poteau et de la traverse dans l'espace à côté. Les coordonnées UV vont toujours de 0 à 16 quelle que soit la résolution du fichier : les mêmes nombres fonctionnent donc à toutes les tailles.

**Son objet veut un modèle propre.** Un objet qui hérite d'un modèle aussi haut déborde de son emplacement à l'échelle de bloc habituelle : donnez donc à `models/item/<name>.json` un bloc `display` propre, avec l'échelle réduite et le tout translaté pour rentrer dans le cadre.

**Il n'y a ni couleurs ni motifs.** Une bannière de pack n'a pas d'entité de bloc : rien ne porte donc la liste de calques que les bannières vanilla conservent dans la leur. Le dessin est la texture, comme l'aspect d'une porte est sa texture, et une définition donne une bannière. La teindre et y empiler des motifs n'est pas à la portée d'un pack.

**Elle prend le `material` que vous lui donnez.** Le bloc sur lequel elle est construite se fixe de lui-même au bois : ce mod remet donc le matériau au vôtre à l'enregistrement, et une bannière de pierre se mine à la pioche comme la pierre qu'elle prétend être.

### Textures écrites sous forme de cartes de pixels

*modèles, états de bloc et textures*

Une texture peut être un fichier JSON au lieu d'un PNG. Placez-la là où serait allé le PNG, avec `.json` ajouté à la fin du nom complet : `textures/blocks/panel.png.json` répond ainsi à toute demande de `textures/blocks/panel.png`. Rien d'autre ne change : les modèles pointent vers `mypack:blocks/panel` comme avant, et l'atlas, les mipmaps et un `.mcmeta` d'animation fonctionnent tous, car ce que reçoit le jeu reste un PNG.

```json
{
  "extends": "mypack:textures/blocks/panel_template",
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

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `size` | oui, ou héritée | `widthxheight` | | Nombre de pixels en largeur et en hauteur |
| `rows` | oui, ou héritée | liste de textes | | Une chaîne par rangée de pixels, un caractère par pixel, de haut en bas |
| `palette` | oui, ou héritée | objet | | Un caractère pour une couleur, `#RRGGBB` ou `#AARRGGBB` |
| `extends` | non | une autre carte de pixels | | La carte dont celle-ci part |
| `tint` | non | objet avec `from` et `to` | | Recolore tout ce qui est hérité le long d'un dégradé entre deux couleurs |
| `notes` | non | objet | | Un caractère pour une ligne disant à quoi il sert, héritée et jamais dessinée |

**Il n'y a pas de nom à déclarer.** Le chemin du fichier est son nom, exactement comme pour un PNG : une carte à `assets/mypack/textures/blocks/panel.png.json` est `mypack:blocks/panel` dans un modèle, et une carte à `assets/mypack/textures/items/gem.png.json` est `mypack:items/gem` dans un modèle d'objet. Rien ne pointe spécialement vers une carte de pixels ; un bloc ou un objet nomme sa texture comme il l'a toujours fait et ne saura jamais laquelle des deux il a reçue. Cela signifie aussi que les dossiers de blocs et d'objets restent séparés, comme pour les PNG : `textures/blocks/gem.png.json` et `textures/items/gem.png.json` sont deux textures différentes, mises en cache dans deux fichiers différents.

**La taille que vous voulez**, jusqu'à 4096 de côté, et les deux côtés n'ont pas besoin d'être égaux. `16x16` est une face de bloc ordinaire, `16x32` est le genre de bande haute que veulent une moitié de porte ou une animation. La taille est vérifiée et non devinée : donnez une rangée par ligne de pixels et un caractère par pixel en largeur, faute de quoi la carte est refusée et le journal nomme la rangée et ce qu'il y a trouvé. Un caractère sans couleur dans la palette reste transparent : `.` ou une espace forme donc un trou.

**Les modèles sont tout l'intérêt de la chose.** `extends` nomme une autre carte de pixels, sous la forme `namespace:path` ou d'un simple chemin dans le même pack, et le fichier qui l'étend hérite de son `size`, de ses `rows` et de sa `palette`. Tout ce qu'il nomme lui-même l'emporte, et il n'a pas besoin de tout nommer : une variante entière peut donc se réduire à une poignée de couleurs :

```json
{
  "extends": "mypack:textures/blocks/panel.png",
  "palette": { "s": "#AA7EB1", "d": "#8B6292", "e": "#6B4A72", "p": "#C5A1CB" }
}
```

C'est une seconde texture complète : la même forme en purpur, et si la forme est un jour redessinée dans le modèle, chaque variante suit. Une variante peut à l'inverse fournir ses propres `rows` et garder la palette du modèle, ce qui est l'inverse : les mêmes couleurs selon un motif différent. L'héritage va jusqu'à huit niveaux de profondeur, une boucle est détectée et signalée, et une carte nommant un modèle que rien ne fournit est signalée au lieu d'être dessinée vide.

**Lequel de deux textures est le modèle** se décide selon celle qui contient le plus de distinctions, et non selon celle qui a été dessinée en premier. Une variante donne une couleur à chaque caractère : tout pixel que le modèle appelle du même caractère sort donc de la même couleur dans la variante. Un minerai dessiné sur de la pierre ne peut donc pas hériter des `rows` de la pierre : la pierre appelle « pierre simple » les emplacements des mouchetures, et rien de ce qu'une variante peut écrire ne scinde un caractère en deux. Inversez les rôles et cela fonctionne. Faites du minerai le modèle, pour que les tons de pierre et les tons de minerai aient chacun leurs propres caractères, et un second minerai se résume à quatre couleurs :

```json
{
  "extends": "mypack:textures/blocks/ore_template",
  "palette": { "4": "#768291", "5": "#5E6977", "6": "#66717F", "7": "#848F9D" }
}
```

Une variante qui veut vraiment un motif différent donne ses propres `rows`, comme plus haut, et n'hérite alors que de la palette. Cela vaut la peine quand les couleurs sont l'essentiel et que la forme est accessoire ; quand la forme est l'essentiel, mettez la forme dans le modèle et laissez les variantes nommer des couleurs.

**Un modèle n'a pas besoin d'être une texture du tout.** Une carte n'est servie au jeu que lorsque son chemin se termine par `.png` : un modèle à `textures/blocks/ore_template.json` est donc invisible pour le jeu et n'existe que pour être étendu, tandis qu'un modèle à `textures/blocks/ore_template.png.json` répondrait aussi aux demandes de `ore_template.png`. Nommez une forme partagée sans le `.png` et personne ne pourra la demander par accident.

**Un modèle peut être une vraie image plutôt qu'une carte.** Faites pointer `extends` vers un PNG que fournit n'importe quel pack ou le jeu lui-même, et la palette change de sens : les clés deviennent les couleurs déjà présentes dans cette image, les valeurs les couleurs à y substituer. Rien n'est tracé et aucune `rows` n'est écrite : un pack peut donc recolorer une texture vanilla ou de mod là où elle se trouve :

```json
{
  "extends": "minecraft:textures/blocks/coal_ore.png",
  "palette": {
    "#3F3F3F": "#C4353F",
    "#343434": "#8E2029",
    "#373737": "#A32A33",
    "#454545": "#DE5F68"
  }
}
```

C'est un minerai de rubis dans la propre pierre de vanilla : les quatre tons de mouchetures sont remplacés et tous les autres pixels restent tels quels. Une couleur que l'image ne contient pas ne correspond simplement jamais, et la taille vient de l'image à moins que vous n'en nommiez une, qui doit alors concorder.

`extends` préfère une carte de pixels : il cherche d'abord la carte à ce chemin et ne se rabat sur l'image que si aucun pack n'en fournit. Un nom qui n'est ni l'un ni l'autre est signalé au lieu d'être dessiné vide. Construire sur une image est un travail côté client, puisque ce sont les ressources du jeu qui sont lues : un serveur dédié ne le fait donc jamais.

**Un modèle peut être teinté au lieu d'être repeint.** `tint` nomme deux couleurs et recolore tout ce que la carte hérite le long du dégradé entre elles. La luminosité de chaque couleur héritée est sa position sur ce dégradé : le noir tombe sur `from`, le blanc sur `to`, et chaque ton intermédiaire est mélangé en proportion. La transparence est laissée intacte. Un modèle en niveaux de gris et deux couleurs forment ainsi une variante complète :

```json
{
  "extends": "mypack:textures/items/materials/ingot.png",
  "tint": { "from": "#626669", "to": "#DBDFE2" }
}
```

`from` peut être omis, auquel cas il vaut noir et la teinte devient une simple multiplication, de même forme qu'un `tintindex` au moment du rendu. La différence est que celle-ci est dessinée une fois dans le PNG puis mise en cache : elle ne coûte rien par image et atteint une texture que rien ne teinte, mais elle ne peut pas non plus suivre un biome comme le font `grass` ou `foliage`.

Le modèle reste une carte ordinaire : ouvrez-le, regardez-le, et il s'affiche dans le gris qu'il est. Les deux couleurs acceptent `#RRGGBB`, `#AARRGGBB` ou un `0x` initial, et une valeur qui n'est aucun des trois laisse la carte non dessinée au lieu de la dessiner dans la mauvaise couleur. Une teinte s'hérite comme tout le reste et la première rencontrée en remontant la chaîne l'emporte : la teinte propre à une variante prime donc sur celle qu'elle étend. Elle fonctionne aussi sur un modèle image, où elle s'exécute après les remplacements de couleurs de la palette.

**Une teinte est un dégradé entre deux couleurs**, elle ne convient donc qu'à une texture dont les tons s'y alignent. Une forme à deux régions sans rapport, la pierre d'un minerai et ses mouchetures, n'en est pas une, et demande plutôt que sa palette soit écrite en entier.

**Savoir ce que signifient les caractères d'un modèle** est la partie délicate quand on en étend un, et c'est à cela que sert le bloc `notes` ci-dessus : un caractère pour une courte ligne, hérité comme l'est la palette et jamais dessiné. Annotez les caractères d'un modèle et celui qui l'étend saura lesquels surcharger.

`/rdpl pixelmap <namespace:path>` indique alors ce qu'une carte a réellement produit, ce qui est le moyen fiable d'écrire une variante sans ouvrir chaque fichier de la chaîne :

```
oretest:textures/blocks/ruby_ore.png is 16x16
  built from oretest:textures/blocks/ruby_ore.png.json
  built from oretest:textures/blocks/gem_ore.png.json
  rows come from oretest:textures/blocks/gem_ore.png.json
  1  #C4353F  17 pixel(s)  set by ruby_ore.png.json  ore body, most of every lump
  2  #8E2029   8 pixel(s)  set by ruby_ore.png.json  ore shadow, the darkest tone
  a  #747474  86 pixel(s)  set by gem_ore.png.json   stone, the commonest tone
```

Chaque caractère est listé avec sa couleur, le nombre de pixels qu'il couvre, le fichier de la chaîne qui l'a défini et ce que ce fichier dit de son rôle. Le chemin peut être donné sous sa forme courte, `mypack:blocks/panel`, ou en entier. Un caractère affichant 0 pixel est un caractère que la palette nomme et que les rangées n'utilisent jamais, ce qui est généralement une faute de frappe dans une rangée.

**Les images dessinées sont conservées sur le disque** dans `rdploader/pixelmap-cache`, dans un dossier par namespace et nommées d'après la texture, avec en fin de nom un hachage de sa source. Le hachage couvre toute la chaîne, la carte elle-même et chaque modèle au-dessus d'elle : modifier un modèle change donc l'empreinte de chaque variante qui en hérite, et toutes sont redessinées. Quand une carte est redessinée, ses anciens fichiers sont balayés.

Le dossier est aussi parcouru à chaque analyse des packs, et toute image dont plus aucun pack ne fournit la carte est supprimée, ainsi que tout dossier resté vide. Renommez une texture, retirez un pack, supprimez une carte, et son image en cache disparaît avec elle au lieu de s'attarder indéfiniment. Supprimer tout le dossier ne coûte que le temps de tout redessiner, et il est ignoré lors de l'analyse des packs : il n'est donc jamais pris pour un pack.

Un PNG l'emporte toujours. Si `panel.png` et `panel.png.json` existent tous deux, le PNG est servi et la carte n'est jamais dessinée : une texture générée peut donc être remplacée plus tard par une texture peinte sans rien changer à ce qui y pointe.

**Personne n'a à écrire ces fichiers à la main.** Le dépôt fournit des scripts pour tout l'aller-retour dans [`pixelmap/`](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/tree/1.12.2-1.0-Release/pixelmap) : `png_to_pixelmap.py` transforme un PNG en carte, `convert_pack.py` le fait pour chaque texture d'un pack, et `verify_pack.py` dessine les cartes d'un pack converti et les compare aux PNG dont elles proviennent, de sorte qu'une conversion peut être validée avant de mettre les originaux de côté.

### Pièges à connaître

*modèles, états de bloc et textures*

**Un état de bloc qui nomme un modèle vanilla brut hérite aussi des textures de vanilla.** `normal_torch`, `ladder`, `wooden_door_*` et `wheat_stage*` portent tous leurs propres textures : un bloc qui pointe vers l'un d'eux obtient l'aspect de vanilla quoi que vous écriviez dans l'état de bloc. Les modèles parents tels que `cube_all`, `cross` et `block/crop` prennent leurs textures dans l'état de bloc et se comportent bien, tout comme les parents de porte, de trappe et de portillon énumérés sous [Portes, trappes et portillons](#portes-trappes-et-portillons).

**`forge_marker: 1` ne gère pas le multipart.** Un état de bloc de vigne doit être un multipart vanilla pur, avec les textures intégrées au modèle plutôt que transmises.

**Les noms viennent du fichier de langue, et un bloc en veut DEUX.** Un bloc ou un objet affiche une clé brute tant que `lang/en_us.lang` ne lui en donne pas une. L'objet que vous tenez et placez est indexé par le nom de registre du bloc suivi de la variante, `tile.mypack:ruby_ore.ruby_ore.name=Ruby Ore`, et c'est celui dont se souviennent la plupart des packs. Le BLOC lui-même est indexé par le seul nom de registre, `tile.mypack:ruby_ore.name=Ruby Ore`, et c'est ce que lit tout ce qui demande son nom au bloc placé — le titre de l'écran d'un conteneur, entre autres. Écrivez les deux, sinon l'objet s'affiche correctement en main alors que l'écran qu'il ouvre a un titre vide.

**Les types à variante unique se nomment deux fois.** Un bloc qui peut contenir plusieurs variantes est indexé par son seul nom de registre, comme ci-dessus. Un bloc dont toute la métadonnée va à sa forme ajoute après lui le nom de la variante : une porte définie dans `blocks/my_door.json` avec une variante unique appelée `my_door` est donc `tile.mypack:my_door.my_door.name=My Door`. Cela couvre `door`, `trapdoor`, `fence_gate`, `banner`, `stairs`, `ladder`, `torch`, `crop`, `cane`, `sapling` et `vine`. Quand un tel type a un objet propre, comme une porte et une bannière, il veut la même clé une seconde fois sous `item.` plutôt que `tile.`.

## Faire en sorte que vanilla traite correctement votre bloc

*blocs et objets*

Vanilla teste ses propres blocs par identité en une douzaine d'endroits : un bloc de pack qui devrait manifestement fonctionner ne fonctionne donc souvent pas. Deux clés y remédient.

```json
{
  "material": "ground",
  "plantTypes": ["Plains", "Crop"],
  "behavesAs": ["till", "path"],
  "variants": { "ruby_grass": { "meta": 0, "hardness": 0.6 } }
}
```

**`plantTypes`** énumère les types de plantes Forge que votre bloc accepte, de sorte que pousses, cultures et fleurs puissent y être plantées.

**`behavesAs`** fait traiter votre bloc par vanilla comme l'un des siens :

| Valeur | Ce que ça fait |
| --- | --- |
| `till` | Une houe le transforme en terre labourée |
| `path` | Une pelle le transforme en chemin de terre |
| `bush` | Fleurs, herbe et pousses peuvent y être plantées et y rester, comme sur la terre. Identique à `plains` dans `plantTypes` |
| `animals` | Les animaux y apparaissent à la lumière, comme sur l'herbe |

## Objets

*blocs et objets*

`<namespace>/items/*.json`

Le chemin du fichier est le nom de registre de l'objet : `mypack/items/ruby.json` enregistre donc `mypack:ruby`. Les clés à l'intérieur de `variants` nomment les valeurs de métadonnée de cet objet unique, et le modèle de chacune se place dans `models/item/ruby/<key>.json`.

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
  "container": "minecraft:glass_bottle",
  "crop": "mypack:ruby_crop",
  "soil": "minecraft:farmland",
  "potionTypes": ["mypack:ruby_tonic"],
  "rocket": "mypack:supply_rocket",
  "requires": ["mypack"],
  "variants": {
    "ruby_apple": {
      "meta": 0,
      "maxSize": 64,
      "rarity": "rare",
      "healAmount": 6,
      "saturation": 0.8,
      "oreDict": ["foodRuby"],
      "potion": "minecraft:speed,600,1"
    },
    "dried_ruby_apple": { "meta": 1, "healAmount": 3, "saturation": 0.4 }
  }
}
```

### Types d'objets

*objets*

| Type | Ce que vous obtenez |
| --- | --- |
| `basic` | Un objet simple. Utilisé quand `type` est absent |
| `food` | Se mange, avec faim et saturation |
| `drink` | Se boit au lieu de se manger, et rend un récipient vide |
| `tool` | Pioche, hache, pelle ou épée à partir d'un matériau |
| `armor` | Casque, plastron, jambières ou bottes à partir d'un matériau |
| `seed` | Plante l'une de vos cultures |
| `potion` | Applique vos effets de potion à l'utilisation |
| `potion_bottle` | Contient vos types de potions, et les affiche dans un onglet créatif |
| `rocket` | Place l'une de vos variantes de fusée Galacticraft sur un pas de tir |

Un `potion_bottle` énumère ce qu'il peut contenir avec `potionTypes`, un tableau de noms de types de potions tel que `["mypack:ruby_tonic"]`. Avec une liste vide, il n'enregistre rien, et le journal le signale.

Un `rocket` nomme avec `rocket` la variante d'entité qu'il place. Il a besoin de Galacticraft ; sans lui, ou sans `rocket`, il n'enregistre rien, et le journal le signale. Voir Fusées de Galacticraft sous Variantes d'entités.

### Clés de fichier d'objet

*objets*

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `variants` | oui | objet de nom de variante vers variante | | Une entrée par valeur de métadonnée. La clé nomme cette valeur dans l'état de bloc, le chemin du modèle et la clé de langue. Le nom de registre vient du chemin du fichier lui-même |
| `type` | non | l'un des types ci-dessus | `basic` | Le type que prend l'objet |
| `creativeTab` | non | nom d'onglet | aucun | L'onglet où il apparaît |
| `material` | tool, armor | nom de matériau | aucun | Celui de vos matériaux dont il est fait |
| `toolClass` | tool | `pickaxe`, `axe`, `shovel`, `sword` | aucun | De quel outil il s'agit |
| `slot` | armor | `head`, `chest`, `legs`, `feet` | aucun | Où il se porte. `helmet`, `chestplate`, `leggings` et `boots` fonctionnent aussi |
| `eat` | food | booléen | `false` | Utilise l'animation de repas |
| `alwaysEdible` | food | booléen | `false` | Peut être mangé avec une barre de faim pleine |
| `useDuration` | non | entier, ticks | `32` | Durée de l'utilisation |
| `attackSpeed` | non | décimal | adapté à la classe d'outil | Pour `tool`, l'attribut de vitesse d'attaque, comme `-2.4` pour une épée |
| `cooldown` | non | entier, ticks | `0` | Pour `food`, `drink` et `potion`, durée pendant laquelle l'objet refuse d'être réutilisé après avoir été consommé |
| `container` | drink | nom d'objet | aucun | Ce qui reste, comme une bouteille |
| `crop` | seed | nom de bloc | aucun | La culture qu'il plante |
| `soil` | seed | nom de bloc | `minecraft:farmland` | Sur quoi il peut être planté |
| `rocket` | rocket | `namespace:name` | aucun | La variante d'entité qu'il place |
| `requires` | non | liste d'identifiants de mods ou de namespaces de packs | aucun | Le fichier est ignoré sauf si tous sont présents |

### Clés de variantes d'objet

*objets*

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `meta` | oui | 0 à 15 | | La valeur de métadonnée que revendique cette variante |
| `maxSize` | non | 1 à 64 | `64` | Taille de pile |
| `rarity` | non | `common`, `uncommon`, `rare`, `epic` | `common` | Couleur du nom dans l'infobulle |
| `healAmount` | food | entier, demi-cuisses | `0` | Faim restaurée |
| `saturation` | food | décimal | `0.0` | Saturation restaurée |
| `oreDict` | non | liste de noms du dictionnaire des minerais | aucun | Noms du dictionnaire des minerais sous lesquels cette variante est enregistrée |
| `potion` | food, drink | `potion,duration,amplifier` | aucun | Un effet appliqué quand la variante est mangée ou bue. Une quatrième partie, `true`, le rend ambiant. Un effet bénéfique est nommé dans l'infobulle |

## Fluides

*blocs et objets*

`<namespace>/fluids/*.json`

Le chemin du fichier est le nom de registre du fluide, sauf si `name` le remplace.

```json
{
  "name": "molten_ruby",
  "still": "mypack:blocks/molten_ruby_still",
  "flow": "mypack:blocks/molten_ruby_flow",
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

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `name` | non | chaîne | le nom du fichier | Le nom de registre du fluide |
| `still` | non | chemin de texture | eau immobile vanilla | Texture du fluide immobile |
| `flow` | non | chemin de texture | eau courante vanilla | Texture du fluide courant |
| `color` | non | couleur hexadécimale | aucun | Teinte appliquée à ces textures |
| `bucket` | non | booléen | `true` | Enregistre un seau pour lui |
| `luminosity` | non | 0 à 15 | `0` | Lumière émise |
| `density` | non | entier | `1000` | Une valeur négative flotte vers le haut, comme un gaz |
| `temperature` | non | entier, kelvin | `300` | L'eau vaut 300, la lave 1300 |
| `viscosity` | non | entier | `1000` | Lenteur d'écoulement. L'eau vaut 1000, la lave 6000 |
| `gaseous` | non | booléen | `false` | Traité comme un gaz |
| `creativeTab` | non | nom d'onglet | aucun | L'onglet où apparaît le seau |
| `block` | non | objet | | Le bloc de fluide. `material` (`water`), `flammability` (`0`), `fireSpread` (`0`), `quantaPerBlock` (`0`), `potions` (aucune, une liste d'effets donnés à tout ce qui s'y trouve, chacun écrit `potion,duration,amplifier` avec une quatrième partie facultative `true` pour un effet ambiant) |
| `requires` | non | liste d'identifiants de mods ou de namespaces de packs | aucun | Le fichier est ignoré sauf si tous sont présents |

## Matériaux, onglets, sons, dictionnaire des minerais

*blocs et objets*

`<namespace>/materials/*.json`

Le chemin du fichier est le nom du matériau, qu'un objet outil ou armure nomme ensuite dans `material`.

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

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `harvestLevel` | non | 0 à 3 | `1` | Niveau de l'outil. 0 bois, 1 pierre, 2 fer, 3 diamant |
| `durability` | non | entier | `250` | Utilisations avant qu'il ne casse |
| `efficiency` | non | décimal | `6.0` | Vitesse de minage. Le diamant vaut 8 |
| `damage` | non | décimal | `2.0` | Bonus de dégâts d'attaque |
| `enchantability` | non | entier | `14` | Qualité des enchantements. L'or vaut 22 |
| `repairItem` | non | nom d'objet | aucun | Ce qui le répare dans une enclume |
| `reduction` | non | liste de quatre entiers | | Points d'armure, dans l'ordre bottes, jambières, plastron, casque |
| `toughness` | non | décimal | `0.0` | Robustesse de l'armure, comme celle du diamant |
| `equipSound` | non | nom de son | `item.armor.equip_iron` | Son quand l'armure est enfilée |
| `armorTexture` | non | préfixe de texture | le nom du fichier | La texture de l'armure portée |

### Onglets créatifs

*matériaux, onglets, sons, dictionnaire des minerais*

`<namespace>/tabs/*.json`

Le chemin du fichier est le nom de l'onglet, sauf si `label` le remplace, et les blocs et objets le nomment dans `creativeTab`.

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `label` | non | chaîne | le nom du fichier | L'identifiant de l'onglet : les blocs et objets le nomment dans `creativeTab`, et le nom affiché vient de `itemGroup.<label>` dans les fichiers de langue |
| `icon` | non | nom d'objet | aucun | L'objet affiché sur l'onglet |

### Sons

*matériaux, onglets, sons, dictionnaire des minerais*

`<namespace>/sounds/*.json`

Le nom du fichier est à vous de choisir, seul le dossier est lu, et plusieurs fichiers se cumulent.

Le format `sounds.json` de vanilla, de sorte qu'un pack peut fournir son propre audio.

### Dictionnaire des minerais

*matériaux, onglets, sons, dictionnaire des minerais*

`<namespace>/oredict/*.json`

Le nom du fichier est à vous de choisir, seul le dossier est lu, et plusieurs fichiers se cumulent.

Ajoute des noms du dictionnaire des minerais à des objets qui existent déjà. Chaque clé est un nom du dictionnaire des minerais et sa valeur les objets enregistrés sous ce nom : un fichier n'a donc aucune clé fixe propre. Les blocs et objets d'un pack nomment les leurs dans le `oreDict` de la variante à la place.

Une clé commençant par `-` retire au lieu d'ajouter : `"-ingotCopper": ["thermalfoundation:material:128"]` retire cet objet du nom, et `["*"]` vide le nom. Les recettes qui utilisaient le nom cessent aussitôt de reconnaître l'objet, ce qui est le but. Un nom que rien n'enregistre est refusé avec une erreur, de même qu'un objet que le nom ne porte pas. Une entrée enregistrée pour toutes les métadonnées, comme vanilla enregistre `plankWood`, est retirée entièrement quelle que soit la métadonnée que vous nommez, et le journal le signale ; nommez-la avec `:*` pour dire la même chose explicitement.

```json
{
  "_note": "ruby equivalents",
  "gemRuby": ["mypack:ruby", "mypack:polished_ruby:1"],
  "oreRuby": ["mypack:ruby_ore", "minecraft:redstone_ore"]
}
```

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| un nom du dictionnaire des minerais | oui | liste de noms d'objets | | Les objets enregistrés sous ce nom. Métadonnée en troisième partie, `"mypack:ruby:1"` |
| un nom commençant par `_` | non | n'importe quoi | | Ignoré, de sorte qu'un fichier peut contenir une note pour lui-même |

## Surcharges de propriétés

*blocs et objets*

`<namespace>/overrides/<target>/<name>.json`

Le chemin nomme la cible : tout ce qui suit `overrides/` est le namespace et le nom du bloc, de l'objet ou du type de potion modifié.

Partout ailleurs, un pack remplace un fichier ou en ajoute un. Une surcharge ne fait ni l'un ni l'autre : elle modifie les propriétés d'un bloc, d'un objet ou d'un type de potion qui existe déjà, vanilla ou modé, sans toucher à aucun de ses fichiers. Le chemin nomme la cible : `overrides/minecraft/stone.json` modifie donc `minecraft:stone`, et `overrides/tconstruct/<name>.json` modifie de la même façon le bloc de ce mod.

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

Chaque clé est facultative et un fichier ne modifie que ce qu'il nomme : un fichier à `overrides/minecraft/stone.json` ne contenant que `hardness`, `light` et `soundType` rend la pierre presque instantanée à miner, lumineuse, et lui donne le son du verre. Un fichier porte ensemble des clés de bloc, d'objet et de potion. Celles-ci s'appliquent quand la cible est un bloc :

| Clé | Valeur | Ce que ça fait |
| --- | --- | --- |
| `hardness` | décimal | Temps de minage, le même chiffre qu'accepte une définition de bloc |
| `resistance` | décimal | Résistance aux explosions |
| `slipperiness` | décimal | `0.6` est le sol ordinaire, `0.98` la glace |
| `light` | `0` à `15` | Lumière émise |
| `lightOpacity` | `0` à `255` | Quantité de lumière que le bloc arrête |
| `soundType` | l'un des types de son | Sons de pas, de placement et de destruction |
| `harvestTool` | classe d'outil | Ce qui le mine ; `harvestToolLevel`, `0` par défaut, fixe le niveau |
| `flammability` | entier | Facilité avec laquelle il brûle ; `fireSpread`, `5` par défaut, facilité avec laquelle le feu l'atteint |

### Propriétés des objets

*surcharges de propriétés*

Et celles-ci quand la cible est un objet :

| Clé | Valeur | Ce que ça fait |
| --- | --- | --- |
| `maxStackSize` | `1` à `64` | Taille de pile |
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

À `overrides/minecraft/planks.json`, cela fait casser les planches à peu près aussi vite que la terre et permet de les manger. `food` accepte `heal` (`1`), `saturation` (`0.6`), `alwaysEdible` (`false` ; `true` permet de manger avec une barre de faim pleine) et `effects`, dont les entrées s'écrivent exactement comme celles d'un type de potion. Un objet qui est déjà de la nourriture prend de nouveaux `heal`, `saturation` et `alwaysEdible` ; `effects` n'est pas géré sur un tel objet, et le journal le signale. Quand l'objet comestible place un bloc, visez le ciel pour manger, puisque viser un bloc le place : c'est l'ordre d'utilisation de vanilla, pas un bogue.

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

À `overrides/minecraft/swiftness.json`, la potion de rapidité accorde désormais la lévitation. Chaque entrée accepte `potion` (obligatoire), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) et `showParticles` (`true`), comme dans `potion_types/`, et la liste ne peut pas être vide.

### Autres mods, rechargements et limites

*surcharges de propriétés*

Une cible que possède un autre mod doit porter ce mod dans `requires`, pour que le fichier soit ignoré discrètement quand le mod n'est pas installé au lieu d'être signalé comme cible manquante :

```json
{
  "requires": ["tconstruct"],
  "hardness": 1.0
}
```

Les surcharges sont actives en direct. Les valeurs d'origine sont mémorisées avant la première modification : désactiver le pack et lancer `/rdpl reload` rétablit donc tout comme c'était, sans redémarrage ; la même chose se produit à chaque entrée dans un monde. Un fichier par cible : quand deux packs surchargent la même chose, le fichier du pack le plus tardif remplace entièrement celui du précédent, et le journal le signale.

Deux limites à connaître. Un bloc ou un objet dont le propre code calcule une propriété ignore le champ qui la sous-tend : la surcharge s'applique mais ne change rien ; vanilla ne le fait que pour la résistance aux explosions des escaliers, mais les mods sont libres de le faire n'importe où. Et les objets rendus comestibles ne fonctionnent que sur des objets sans comportement de clic droit propre : un objet qui fait déjà quelque chose quand on l'utilise continue de le faire.

Les surcharges exigent le pack côté client comme côté serveur, puisque la vitesse de minage, la lumière et le fait de manger se passent tous sur l'écran du joueur : elles ne conviennent donc pas aux packs côté serveur. `overrides` dans la catégorie de configuration `content` désactive entièrement le dossier.

## Groupes de dureté

*blocs et objets*

`<namespace>/hardness/*.json`

Le chemin du fichier nomme le groupe dans le journal et rien d'autre ne le lit : plusieurs fichiers se cumulent donc.

Donne à un groupe de blocs un multiplicateur de temps de minage, tiré par position de bloc. Le bloc lui-même n'est jamais modifié : rien n'est enregistré, rien n'est écrit dans le monde, et un monde ouvert sans le pack est du vanilla ordinaire.

```json
{
  "blocks": ["minecraft:stone:0"],
  "except": [{ "block": "minecraft:stone", "properties": { "variant": "andesite" } }],
  "miningTime": { "min": 1.0, "max": 20.0 },
  "blastResistance": { "min": 1.0, "max": 4.0 },
  "buckets": 10,
  "minHeight": 0,
  "maxHeight": 255,
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

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `blocks` | oui | liste de noms de blocs ou d'objets | | Le groupe. Les mêmes trois formes qu'un `replace` de worldgen |
| `except` | non | liste de noms de blocs ou d'objets | aucun | Retirés du groupe, quoi que dise `blocks` |
| `miningTime` | non | nombre, ou objet avec `min` et `max` | `1.0` | Combien de fois plus longtemps le bloc met à casser, pour un joueur comme pour un mob `digs` |
| `blastResistance` | non | nombre, ou objet avec `min` et `max` | `1.0` | Multiplie la résistance aux explosions du bloc |
| `buckets` | non | 1 à 256 | `10` | En combien de paliers la plage est divisée |
| `minHeight` | non | entier | `0` | En dessous, le tirage donne le palier le plus dur |
| `maxHeight` | non | entier | `255` | Au-dessus, le tirage donne le palier le plus dur |
| `field` | non | objet | voir ci-dessous | La forme en amas que prend le tirage |
| `requires` | non | liste d'identifiants de mods ou de namespaces de packs | aucun | Le fichier est ignoré sauf si tous sont présents |

Un nombre seul donne à chaque bloc du groupe le même multiplicateur, et rien n'est tiré. Un `min` et un `max` tirent par position : `max` là où le champ est vide, `min` au centre d'un amas, et les paliers intermédiaires sont déterminés par `buckets`.

### Minage en aventure et déblocages

*groupes de dureté*

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `keeps` | non | booléen | `false` | Le bloc reste en place quand il est miné : les butins, l'expérience, l'usure de l'outil et le son de destruction se produisent tous et le bloc est toujours là pour être miné de nouveau, si bien que le groupe est une veine sans fin au rythme que fixe `miningTime`. Le mode créatif le retire comme toujours |
| `adventure` | non | objet | aucun | Qui peut casser le groupe en mode aventure, où rien ne se casse autrement. `tools` énumère les objets dont l'un doit être en main, vide pour n'importe quel objet tenu ; `teams`, `players` et `entities` disent qui, une équipe par son nom, un joueur par son nom, un mob par son identifiant d'entité pour la tâche `digs`, et les trois vides signifient quiconque a l'outil. Survie et créatif ne sont pas touchés |
| `advancement` | non | `namespace:path` | aucun | Le groupe ne compte pour un joueur qu'une fois qu'il a obtenu ce progrès. Deux groupes peuvent nommer le même bloc, l'un avec un progrès et l'autre sans, et celui qui est débloqué l'emporte ; un joueur qui ne l'a pas obtient le groupe simple, ou vanilla s'il n'y en a pas. Les mobs n'ont aucun progrès : un groupe conditionné n'atteint donc jamais une tâche `digs`, et la résistance aux explosions et le tirage de texture, qui n'appartiennent à aucun joueur, viennent du groupe simple |
| `becomes` | non | objet | aucun | Les blocs du groupe se transforment en un autre bloc, dans tout le monde, dès qu'un joueur obtient `advancement` : `{ "advancement": "mypack:deep_miner", "block": "mypack:rich_ore" }`. Chaque chunk chargé est balayé aussitôt, un chunk chargé plus tard est balayé à son arrivée, et un chunk créé plus tard est balayé juste après la mise en place de son minerai : l'ancien bloc a donc disparu pour de bon. Donnez au nouveau bloc un groupe à lui pour changer sa façon d'être miné |

### Le champ

*groupes de dureté*

Le tirage n'est pas fait pour chaque bloc entièrement isolément, sans quoi dur et mou ne seraient que du bruit pur, sans forme. `field` décide de la forme qu'il prend, et `type` choisit entre deux façons d'y parvenir.

```json
{
  "field": { "type": "speckle" }
}
```

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `type` | non | `speckle` ou `seeded` | `speckle` | Lequel des deux ci-dessous est utilisé |

#### speckle

*le champ*

Chaque bloc tire son propre palier, et un bloc situé à une face de distance peut lui transmettre un palier plus faible. Cela donne des mouchetures denses et fines, la plupart d'un seul bloc, avec çà et là un amas plus grand là où elles se rejoignent. C'est le plus proche des deux de la sensation de minage du mod dont celui-ci s'inspire.

```json
{
  "field": {
    "type": "speckle",
    "chances": [30, 30, 20, 20, 10, 10, 10, 10, 50],
    "spread": 0.15
  }
}
```

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `chances` | non | liste d'entiers, pour mille | `[30, 30, 20, 20, 10, 10, 10, 10, 50]` | Fréquence à laquelle un bloc démarre à chaque palier, le plus mou en dernier. Tout le reste est le palier le plus dur |
| `spread` | non | 0.0 à 1.0 | `0.15` | Fréquence à laquelle un palier se transmet au bloc voisin, d'un palier plus faible ou de trois |

La liste se lit du plus mou en dernier : l'entrée finale est donc le palier le plus mou et la première est un cran au-dessus du plus dur. Avec les nombres ci-dessus, environ sept blocs sur dix sont au palier le plus dur et les autres y sont dispersés.

#### seeded

*le champ*

Des germes sont posés sur un réseau calculé à partir du monde et de la position, et le palier d'un bloc dépend de sa proximité avec le plus proche. Cela donne des amas moins nombreux, plus grands et plus ronds, qui se fondent les uns dans les autres, et qui peuvent pousser des bras.

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

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `cell` | non | entier, blocs | `8` | Distance entre les germes |
| `seeds` | non | 1 à 4 | `1` | Germes par cellule |
| `reach` | non | décimal, blocs | `3.0` | Jusqu'où porte l'influence d'un germe |
| `arms` | non | 0 à 6 | `0` | Bras rayonnant depuis chaque germe |
| `armReach` | non | décimal, blocs | `0.0` | Jusqu'où portent les bras |

Sans `arms`, les amas sont ronds. Donner des bras à un germe en fait un nœud à vrilles, et les bras de nœuds voisins se tendent les uns vers les autres, ce qui donne un filon plutôt qu'une boule. Gardez `reach` au-dessus de la moitié de `cell`, sans quoi les amas ne peuvent pas se toucher et vous obtenez des boules séparées sans rien entre elles.

### L'afficher

*groupes de dureté*

Le multiplicateur est invisible en soi. Pour qu'un joueur voie quels blocs sont durs, donnez au bloc un état de bloc avec une variante par palier, toutes de même poids, listées du plus dur au plus mou :

```json
{
  "variants": {
    "normal": [
      { "model": "mypack:stone_step0", "weight": 1 },
      { "model": "mypack:stone_step1", "weight": 1 }
    ]
  }
}
```

Minecraft choisit déjà une variante d'après la position d'un bloc, et un groupe de dureté lui transmet le palier à la place : la texture et le multiplicateur concordent donc toujours.

Trois choses doivent être justes, et aucune ne s'annonce quand elle ne l'est pas.

**Exactement `buckets` entrées, toutes de même poids.** Le palier sert de position dans la liste : une liste d'une autre longueur, ou dont les poids diffèrent, pointe donc discrètement vers la mauvaise texture.

**Un nom de modèle sans `block/` devant.** Un état de bloc ajoute lui-même `block/` : `"model": "mypack:step_stone"` lit donc le fichier à `models/block/step_stone.json`. Écrire `mypack:block/step_stone` cherche `models/block/block/step_stone.json`, qui n'existe pas, et l'entrée est abandonnée sans un mot.

**La même clé que celle que demande le jeu.** Tous les blocs ne sont pas indexés comme leurs propriétés se lisent. La pierre vanilla indexe tout sous `normal`, et non `variant=stone` : une surcharge qui n'écrit que `variant=stone` est fusionnée puis jamais consultée. Écrire les deux clés est sans danger, puisque la fusion se fait clé par clé et qu'un pack l'emporte sur ce qui l'a précédé.

Activez `worldgenDebug` et chaque groupe de dureté est vérifié contre son modèle précompilé à l'entrée dans un monde, en nommant l'état de bloc, le nombre de variantes qui ont survécu, la texture finale de chacune et les packs que le jeu a fusionnés pour en arriver là. C'est le moyen le plus rapide de trouver l'un des trois problèmes ci-dessus, et il avertit aussi quand la surcharge d'un état de bloc partagé a modifié un état que le groupe n'avait jamais nommé.

### Ce qu'il ne touche pas

*groupes de dureté*

Seul le minage propre au joueur est modifié. Les machines qui cassent des blocs lisent directement la dureté du bloc et ne sont pas affectées. Les blocs qu'un joueur place sont tirés comme les autres, puisque le tirage appartient à l'endroit et non au bloc, et un bloc transporté ailleurs prend ce que dit son nouvel endroit.

---

# Artisanat, butin et commerce

## Blocs et objets désactivés

*artisanat, butin et commerce*

`<namespace>/disabled/*.json`

Le nom du fichier est à vous de choisir, seul le dossier est lu, et plusieurs fichiers se cumulent.

Retire des blocs et des objets du jeu sans les désenregistrer : les mondes gardent donc leurs identifiants, et supprimer le fichier ramène tout. Le contenu vanilla, de mod et de pack est traité de la même façon, y compris les propres blocs et objets d'un pack, et un bloc désactivé désactive son objet tout comme un objet désactivé désactive son bloc.

```json
{
  "requires": ["thermalfoundation"],
  "names": ["thermalfoundation:ore", "thermalfoundation:material:128", "mekanism:salt*"],
  "namespaces": ["bigreactors"],
  "oreDict": ["oreTin"]
}
```

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `names` | non | liste de noms de blocs et d'objets | aucun | Ce qui est désactivé. Une métadonnée en troisième partie, `"thermalfoundation:material:128"`, désactive cet objet précis, et `:*` toutes les métadonnées. Un nom se terminant par `*` correspond à tout nom commençant par le reste |
| `namespaces` | non | liste d'identifiants de mods | aucun | Chaque bloc et objet du mod |
| `oreDict` | non | liste de noms du dictionnaire des minerais | aucun | Chaque objet enregistré sous le nom, et le nom est laissé vide |
| `requires` | non | liste d'identifiants de mods | aucun | Le fichier est ignoré sauf si tous sont chargés. Les entrées `config:` et `file:` fonctionnent comme partout ailleurs |

Un bloc ou un objet désactivé :

- disparaît de chaque onglet créatif et de l'onglet de recherche, et est masqué dans JEI et HEI
- n'a aucune recette qui le fabrique ou l'utilise : toute recette d'artisanat dont il est le résultat disparaît, ainsi que toute recette d'artisanat comportant un emplacement que lui seul peut remplir, de même que toute recette de fourneau qui le fait cuire ou qui cuit pour l'obtenir. Un emplacement qui accepte aussi autre chose garde sa recette, et un emplacement du dictionnaire des minerais la perd simplement avec le nom
- est retiré de chaque nom du dictionnaire des minerais
- est supprimé de chaque tirage de butin, coffres, mobs et pêche confondus, des butins de blocs et des échanges de villageois, et une pile qui en est lâchée s'évapore
- ne peut être ni placé, ni utilisé, ni brandi, ni ramassé, et la pile en main est supprimée quand un joueur essaie
- est supprimé partout où une pile apparaît : de l'inventaire et du coffre de l'Ender d'un joueur à la connexion puis chaque seconde ensuite, de tout conteneur quand un joueur l'ouvre, et des coffres et autres inventaires au chargement de leur chunk
- est retiré du monde là où il est placé : chaque bloc de ce type devient de l'air, son entité de bloc avec lui, au chargement de son chunk

Pour remplacer des blocs placés par autre chose au lieu de les retirer, donnez-leur dans le modèle de monde une ligne `blockReplacements` telle que `thermalfoundation:ore=minecraft:stone`, voir [Remplacements](#remplacements). Un bloc que le processus de remplacement échange lui est laissé. Les recettes qu'un autre mod conserve dans ses propres machines appartiennent à ce mod et ne sont pas atteintes. Les fichiers sont lus une seule fois au démarrage, et `content.disabled` dans la configuration désactive le dossier, ce qui demande un redémarrage.

Pour vider un nom du dictionnaire des minerais tout en gardant ses objets en jeu, utilisez plutôt `"-name": ["*"]` dans un fichier de [dictionnaire des minerais](#dictionnaire-des-minerais).

## Recettes de fourneau et combustibles

*artisanat, butin et commerce*

`<namespace>/furnace/*.json`

Le nom du fichier est à vous de choisir, seul le dossier est lu, et plusieurs fichiers se cumulent.

Ajoute et retire des recettes de cuisson.

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

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `input` | oui | nom d'objet | aucun | Ce qui entre |
| `output` | oui | nom d'objet | aucun | Ce qui sort |
| `count` | non | entier | `1` | Combien en sortent |
| `experience` | non | nombre | `0.0` | Expérience par cuisson. Le minerai de fer donne 0.7 |

Un ajout dont l'entrée est déjà cuite par une recette est ignoré, et le journal nomme ce que cette entrée donne désormais ; retirez cette recette dans le même fichier pour la remplacer.

Les entrées sous `remove` sont soit un simple nom d'objet, qui retire toute recette le produisant, soit un objet nommant `input`, `result`, ou les deux pour affiner. Une suppression qui ne nomme ni l'un ni l'autre est ignorée, et le journal le signale.

`<namespace>/fuels/*.json`

Le nom du fichier est à vous de choisir, seul le dossier est lu, et plusieurs fichiers se cumulent.

```json
{
  "fuels": [
    { "item": "mypack:ruby_coal", "burnTime": 2400 },
    { "oreDict": "gemRuby", "burnTime": 800 }
  ]
}
```

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `item` | l'un des deux | nom d'objet | aucun | L'objet qui brûle |
| `oreDict` | l'un des deux | nom du dictionnaire des minerais | aucun | Tout ce qui se trouve sous ce nom brûle |
| `burnTime` | oui | entier, ticks | `0` | Le charbon vaut 1600, une planche 300 |

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
  "icon": { "x": 0, "y": 0 },
  "iconTexture": "mypack:textures/gui/effects.png",
  "attributes": [
    { "attribute": "generic.movementSpeed", "uuid": "91AEAA56-376B-4498-935B-2F7F68070635", "amount": 0.2, "operation": 2 }
  ]
}
```

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `name` | non | clé de traduction | `effect.<namespace>.<name>` | Ce que voit le joueur |
| `color` | non | couleur hexadécimale | `FFFFFF` | Couleur des particules |
| `badEffect` | non | booléen | `false` | Compte comme nuisible : un œil d'araignée fermenté l'inverse donc |
| `beneficial` | non | booléen | `false` | Affiché comme un effet bénéfique |
| `instant` | non | booléen | `false` | S'applique une fois au lieu de s'étaler dans le temps |
| `effectiveness` | non | décimal | `0.5` | La valeur que lui accorde l'IA des mobs |
| `icon` | non | objet avec `x` et `y` | `0`, `0` | Position de l'icône dans la feuille |
| `iconTexture` | non | chemin de texture | l'icône RDPL, ou la feuille vanilla quand `icon` est défini | Votre propre icône de 18 sur 18 |
| `attributes` | non | liste d'objets | aucun | `attribute`, `uuid`, `amount` (`0.0`), `operation` (`0`) |

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

| Clé | Obligatoire | Valeur | Défaut | Ce que ça fait |
| --- | --- | --- | --- | --- |
| `baseName` | non | chaîne | le namespace et le nom | Le nom à partir duquel la bouteille est construite |
| `effects` | oui | liste d'objets | | Voir ci-dessous |

Chaque effet accepte `potion` (obligatoire), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) et `showParticles` (`true`).

### Alchimie

*potions, types de potions et alchimie*

`<namespace>/brewing/*.json`

Le nom du fichier est à vous de choisir, seul le dossier est lu, et plusieurs fichiers se cumulent.

```json
{
  "brewing": [
    { "input": "minecraft:potion", "ingredient": "mypack:ruby", "output": "mypack:ruby_potion", "requires": ["mypack"] },
    { "from": "minecraft:awkward", "ingredient": "mypack:ruby", "to": "mypack:ruby_tonic" }
  ]
}
```

Chaque entrée est soit `input`, `ingredient` et `output`, qui transforme un objet en un autre, soit `from`, `ingredient` et `to`, qui transforme un type de potion en un autre. `ingredient` est obligatoire dans les deux cas, et une entrée accepte aussi `requires`, de sorte qu'une recette peut être ignorée sans que le fichier le soit.

## Travail à l'enclume

*artisanat, butin et commerce*

`<namespace>/anvils/*.json`

Le nom du fichier est libre, seul le dossier est lu, et plusieurs fichiers se cumulent. Chaque fichier décrit une opération.

Placez l'objet indiqué dans l'emplacement gauche d'une enclume et son objet `with` dans l'emplacement droit : l'enclume propose alors l'objet de gauche avec les enchantements listés, ou son `result`, pour le nombre de niveaux indiqué. Un exemplaire de chaque est consommé, sauf si un nombre en demande davantage, et le reste de chaque pile demeure dans l'enclume. Retirer le résultat peut aussi accorder un progrès, et l'objet peut être inutilisable tant que ce progrès n'est pas obtenu : une épée qui ne frappe qu'une fois travaillée.

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

| Clé            | Requis | Valeur                              | Défaut        | Rôle                                                                                                                                                                                                                                                             |
| -------------- | -------- | ----------------------------------- | ------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `item`         | oui      | nom d'objet, ou `{ "item", "count" }` |               | Ce qui va dans l'emplacement gauche, et combien d'exemplaires une opération en prend, un par défaut ; le reste de la pile est conservé pour la suivante. `{ "item": "minecraft:coal", "count": 8 }` avec un `result` en diamant, c'est huit charbons pour un diamant. Métadonnée comme `minecraft:dye:4` |
| `with`         | oui      | nom d'objet, ou `{ "item", "count" }` |               | Ce qui va dans l'emplacement droit, et combien d'exemplaires sont consommés, un par défaut : `{ "item": "minecraft:coal", "count": 10 }` exige une pile d'au moins dix et en prend dix. L'enclume ne se manifeste jamais pour un objet isolé, donc chaque opération est une paire |
| `result`       | non      | nom d'objet, ou `{ "item", "count" }` | l'objet de gauche | Ce qui sort à la place de l'objet de gauche, et en quelle quantité, une par défaut, en gardant les tags de l'objet de gauche : une pioche en fer incassable et dix charbons peuvent ainsi donner une pioche en diamant incassable. Les enchantements vont sur ce qui sort |
| `levels`       | non      | entier                              | `1`           | Les niveaux d'expérience que coûte l'opération, 1 au minimum                                                                                                                                                                                                     |
| `enchantments` | non      | objet nom d'enchantement vers niveau | aucun         | Ce avec quoi l'objet ressort. Un niveau déjà présent à cette valeur ou plus est laissé tel quel, et s'il n'y a rien à relever l'enclume ne propose rien, sauf si `grants` est défini                                                                              |
| `grants`       | non      | `namespace:path`                    | aucun         | Un progrès obtenu quand le résultat est retiré. Fournissez-le sous `advancements/` avec un critère `impossible`, pour que rien d'autre ne l'accorde                                                                                                              |
| `locks`        | non      | booléen                             | `false`       | Tant que le joueur n'a pas `grants`, l'objet ne peut ni frapper, ni être utilisé, ni servir à creuser ; le joueur est informé de ce qu'il attend dès que l'objet arrive dans sa main. Le placer dans l'enclume reste permis, c'est ainsi qu'on le débloque          |

Les réparations et combinaisons propres à l'enclume restent intactes : elle ne répond ici que lorsque la gauche contient un objet nommé et la droite son `with`.

Un mob avec `collectsExperience` dépense lui aussi ses niveaux ici. Tant qu'il tient `item` dans sa main principale et `with` dans sa main secondaire et qu'il a de quoi payer `levels`, il se rend à une enclume située à moins de 16 blocs et l'utilise une fois à moins de 3 blocs : les niveaux sont retirés des siens comme de ceux d'un joueur, `with` est consommé, l'enclume s'use comme sous un joueur, et le résultat finit dans sa main principale. En passant sur un objet au sol que certaines opérations nomment sous `with`, il le ramasse dans sa main secondaire. `grants` et `locks` ne concernent que les joueurs : un mob n'obtient rien avec `grants` et aucun verrou ne le retient.

## Butins des blocs

*artisanat, butin et commerce*

`<namespace>/block_drops/*.json`

Le nom du fichier est libre, seul le dossier est lu, et plusieurs fichiers se cumulent.

Les blocs vanilla de la 1.12 n'ont pas de tables de butin : un pack pouvait donc ajouter à ce que ses propres blocs lâchent, mais pas toucher à la pierre, à un minerai ou au bloc d'un autre mod. Ceci le permet : une règle désigne un bloc et ce que lâche un joueur qui le récolte, en plus des butins habituels ou à leur place.

```json
{
  "block": "minecraft:stone",
  "meta": 0,
  "replace": false,
  "advancement": "mypack:deep_miner",
  "drops": [
    { "item": "minecraft:diamond", "count": "1-2", "chance": 0.05, "fortune": 1, "silkTouch": "never" },
    { "item": "minecraft:emerald", "silkTouch": "only" },
    { "experience": "2-4", "chance": 0.5 }
  ]
}
```

| Clé           | Requis | Valeur           | Défaut  | Rôle                                                                                                                      |
| ------------- | -------- | ---------------- | ------- | ------------------------------------------------------------------------------------------------------------------------- |
| `block`       | oui      | id de bloc       |         | Le bloc surveillé par la règle                                                                                            |
| `meta`        | non      | entier           | `-1`    | Uniquement cette métadonnée du bloc ; `-1` désigne tous les états                                                         |
| `replace`     | non      | booléen          | `false` | Indique si les butins habituels sont écartés avant de tirer ceux-ci                                                       |
| `advancement` | non      | `namespace:path` | aucun   | La règle ne compte que pour un joueur ayant ce progrès, si bien que le même bloc peut lâcher une chose avant et une autre après |
| `drops`       | oui      | liste de butins  |         | Chacun est tiré indépendamment quand un joueur casse le bloc                                                              |

Chaque butin :

| Clé          | Requis                   | Valeur                      | Défaut   | Rôle                                                                                                                            |
| ------------ | ------------------------ | --------------------------- | -------- | ------------------------------------------------------------------------------------------------------------------------------- |
| `item`       | oui, sauf avec `experience` | id d'objet               |          | Ce qui est lâché, avec une métadonnée comme `minecraft:dye:4`                                                                   |
| `experience` | non                      | nombre ou `bas-haut`        |          | À la place d'un objet, cette quantité d'expérience sous forme d'orbes, tirée uniformément dans l'intervalle. `chance` et `silkTouch` s'appliquent comme pour un objet |
| `count`      | non                      | nombre ou `bas-haut`        | `1`      | La quantité, tirée uniformément dans l'intervalle                                                                               |
| `chance`     | non                      | flottant                    | `1.0`    | La probabilité que le butin ait lieu, `0.05` valant un bloc cassé sur vingt                                                     |
| `fortune`    | non                      | entier                      | `0`      | Jusqu'à ce nombre d'exemplaires supplémentaires par niveau de Fortune sur l'outil                                               |
| `silkTouch`  | non                      | `either`, `only` ou `never` | `either` | Indique si le butin exige un outil avec Toucher de soie, le refuse, ou y est indifférent                                        |

Les règles ne voient que la récolte d'un joueur ; les explosions, les pistons et la destruction par les mobs ne tirent rien. Plusieurs règles pour un même bloc s'appliquent toutes, un `replace` sur l'une d'elles effaçant d'abord les butins habituels.

## Butin des joueurs

*artisanat, butin et commerce*

`<namespace>/player_loot/*.json`

Le nom du fichier est libre, seul le dossier est lu, et plusieurs fichiers se cumulent.

Vanilla 1.12 ne donne aucune table de butin aux joueurs : à la mort, seul l'inventaire tombe, et il n'existe aucun nom de table qu'un pack pourrait surcharger. RDPL en ajoute une, tirée à la mort d'un joueur :

```json
{
  "table": "mypack:entities/player",
  "mode": "add",
  "rollOnKeepInventory": false,
  "dropLoose": false
}
```

| Clé                   | Requis | Valeur             | Défaut  | Rôle                                                                                |
| --------------------- | -------- | ------------------ | ------- | ----------------------------------------------------------------------------------- |
| `table`               | oui      | nom de table       |         | La table de butin tirée à la mort d'un joueur                                       |
| `mode`                | non      | `add` ou `replace` | `add`   | Indique si les objets de la table s'ajoutent à l'inventaire ou le remplacent        |
| `rollOnKeepInventory` | non      | booléen            | `false` | Indique si la table est tirée lors d'une mort où l'inventaire a été conservé        |
| `dropLoose`           | non      | booléen            | `false` | Indique si les objets sont posés directement au sol plutôt que de rejoindre les butins de la mort |

`add` lâche les objets de la table en plus de l'inventaire : à utiliser pour des primes de meurtre. `replace` écarte l'inventaire et ne lâche que ce que la table tire.

Avec `rollOnKeepInventory` désactivé, les morts sous `keepInventory` (et celles en spectateur, qui conservent toujours l'inventaire) ne tirent rien. L'activer garde les morts coûteuses dans les mondes avec conservation de l'inventaire.

Plusieurs fichiers se cumulent, chacun évalué pour son propre compte. Si une entrée applicable est en `replace`, l'inventaire est vidé une seule fois avant le tirage, de sorte qu'une entrée `add` à ses côtés est quand même appliquée.

La table est une table de butin ordinaire retrouvée par son nom : elle peut se trouver dans le pack à `loot_tables/entities/player.json`, être n'importe quelle table vanilla ou de mod, et être atteinte par `loot_injections`. Contexte de butin : le joueur qui meurt est l'entité dépouillée, le tueur (s'il y en a un) est le joueur qui tue, et la source de dégâts est définie — `killed_by_player`, `entity_properties`, `random_chance_with_looting`, `looting_enchant` et `quality` se comportent tous normalement.

Une fonction de butin est propre à RDPL, utilisable dans toute table avec une entité dépouillée : `rdpl:killed_name` donne à l'objet lâché le nom de la victime. `format` façonne le nom affiché (`%s` est la victime, par défaut simplement le nom), et `tag` écrit à la place le nom brut dans une clé de chaîne NBT, pour les objets qui la lisent eux-mêmes.

```json
{ "item": "mypack:human_skull", "weight": 1,
  "functions": [ { "function": "rdpl:killed_name", "format": "%s's Skull" } ] }
```

**Mods de tombes.** Les objets tirés rejoignent les butins de mort ordinaires avant qu'un mod de tombes ne les lise, ils finissent donc dans la tombe avec tout le reste (`replace` met le contenu de la table dans la tombe à la place de l'inventaire). Cela vaut pour Gravestone, GraveStone Mod, Corail Tombstone et tout autre mod qui travaille à partir de la liste des butins de la mort. Aucune configuration requise.

`dropLoose` contourne entièrement la liste des butins : les objets sont posés directement dans le monde, les mods de tombes ne les voient donc jamais — l'inventaire va dans la tombe, les objets de la table traînent au sol pour le tueur. À utiliser pour des dépouilles qui reviennent au tueur plutôt qu'à la tombe de la victime. Sans mod de tombes, cela change peu de choses. Mise en garde : les objets existent avant que quoi que ce soit en aval puisse annuler les butins, donc les entrées qui ne doivent pas survivre à une mort annulée devraient s'en passer.

Réglez `playerLoot` de la catégorie `data` de la configuration sur `false` pour désactiver entièrement le dossier.

## Villageois et échanges

*artisanat, butin et commerce*

`<namespace>/villagers/*.json`

Le chemin du fichier est le nom de registre de la profession : `mypack/villagers/jeweller.json` enregistre donc `mypack:jeweller`, qu'un échange désigne ensuite dans `profession`.

```json
{
  "careers": ["gem_cutter", "appraiser"],
  "texture": "mypack:textures/entity/villager/jeweller.png",
  "zombieTexture": "mypack:textures/entity/zombie_villager/jeweller.png"
}
```

| Clé             | Requis | Valeur        | Défaut                      | Rôle                                                                  |
| --------------- | -------- | ------------- | --------------------------- | --------------------------------------------------------------------- |
| `careers`       | oui      | liste de noms | aucun                       | Les carrières proposées par cette profession. Une profession sans carrière est refusée |
| `texture`       | non      | chemin de texture | le villageois vanilla   | L'apparence du villageois                                             |
| `zombieTexture` | non      | chemin de texture | le zombie villageois vanilla | Son apparence une fois zombifié                                   |

### Échanges

*villageois et échanges*

`<namespace>/trades/*.json`

Le nom du fichier est libre, seul le dossier est lu, et plusieurs fichiers se cumulent.

```json
{
  "trades": [
    {
      "profession": "mypack:jeweller",
      "career": "gem_cutter",
      "level": 1,
      "maxUses": 12,
      "buy": { "item": "minecraft:emerald", "min": 2, "max": 4 },
      "sell": { "item": "mypack:ruby", "min": 1 }
    }
  ]
}
```

| Clé          | Requis | Valeur          | Défaut  | Rôle                                |
| ------------ | -------- | --------------- | ------- | ----------------------------------- |
| `profession` | oui      | nom de profession |       | À qui appartient cet échange        |
| `career`     | oui      | nom de carrière |         | Quelle carrière au sein de celle-ci |
| `level`      | non      | entier          | `1`     | À quel palier d'échange il apparaît |
| `maxUses`    | non      | entier          | `12`    | Nombre d'utilisations avant blocage |

Une pile s'écrit `item` avec `min` (`1`) et `max` (`min`) : un prix fixe n'est donc que `min`.

---

# Créatures et dangers

## Variantes d'entités

*créatures et dangers*

`<namespace>/entities/*.json`

Le chemin du fichier est le nom de registre de la variante : `mypack/entities/angry_cow.json` enregistre donc `mypack:angry_cow`, ce à quoi se réfèrent `becomes`, un œuf d'apparition et une sauvegarde du monde.

Un fichier ici fabrique une nouvelle entité à partir d'une entité existante. C'est une vraie entité à part entière, avec son propre nom de registre, son propre nom dans le monde, son propre œuf d'apparition, et une table de butin à elle si vous en donnez une, bâtie sur le comportement d'une autre entité sans la remplacer. Rien ne change pour l'entité copiée.

Toutes les clés, montrées en une fois. Un vrai fichier n'écrit que celles dont il a besoin.

```json
{
  "entity": "minecraft:cow",
  "name": "Angry Cow",
  "showName": false,
  "texture": "mypack:textures/entity/angry_cow.png",
  "lootTable": "mypack:entities/angry_cow",
  "profession": "mypack:jeweller",
  "career": 1,
  "baby": 0.05,
  "becomes": [
    { "variant": "mypack:angry_cow", "weight": 95 },
    { "variant": "mypack:little_angry_cow", "weight": 5 }
  ],
  "sounds": { "ambient": "entity.cow.ambient", "hurt": "entity.cow.hurt", "death": "entity.cow.death", "target": "mypack:scream", "targetVaries": 3, "explode": "mypack:boom", "throw": "mypack:whoosh" },
  "soundVolume": 1.0,
  "soundPitch": 1.0,
  "immuneTo": ["fall", "drown", "explosion", "magic", "cactus", "lava", "wither", "starve", "anvil", "inWall"],
  "jumpMultiplier": 1.0,
  "fallDamage": 1.0,
  "hitEffects": true,
  "ignoresEffects": ["minecraft:wither", "minecraft:poison"],
  "hitFire": true,
  "attackReach": 2.0,
  "hurtResistance": 20,
  "stepHeight": 0.6,
  "knockback": 0.4,
  "climbs": false,
  "teleports": true,
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
  "bright": false,
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
    "attackSpeed": 1.0,
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
  "digs": false,
  "collectsExperience": false,
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
  "biomeTypes": ["PLAINS"],
  "trackingRange": 80,
  "trackVelocity": true,
  "trackingFrequency": 3,
  "storage": {
    "items": { "filter": [{ "item": "minecraft:coal", "max": 128 }] },
    "fluid": { "capacity": 16000, "buckets": true, "use": 5, "filter": [{ "fluid": "water" }] },
    "energy": { "capacity": 100000, "transfer": 1000, "use": 20 },
    "runsDry": "stops",
    "dropsOnDeath": true
  },
  "galacticraft": {
    "tier": 2,
    "fuelTank": 2000,
    "cargoSlots": 36,
    "cargo": [{ "oreDict": "ingotIron", "max": 128 }],
    "requiredPayload": [{ "item": "minecraft:iron_ingot", "count": 64 }],
    "payload": [{ "item": "minecraft:iron_ingot", "count": 64 }]
  },
  "requires": ["mypack"]
}
```

### Identité

*variantes d'entités*

| Clé             | Requis | Valeur                             | Défaut  | Rôle                                                                                                                                                                          |
| --------------- | -------- | ---------------------------------- | ------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `entity`        | oui      | `namespace:name`                   | aucun   | L'entité sur laquelle s'appuyer. Celle de n'importe quel mod, tant qu'elle accepte un constructeur de monde simple                                                            |
| `name`          | non      | chaîne                             | aucun   | Le nom qu'elle porte dans le monde, dans les messages de mort et sur son œuf                                                                                                  |
| `showName`      | non      | booléen                            | `false` | Affiche le nom sans qu'on la regarde                                                                                                                                          |
| `egg`           | non      | booléen ou objet                   | `true`  | Un œuf d'apparition, coloré comme l'œuf de l'entité copiée. `{ "primary": "AABBCC", "secondary": "112233" }` choisit vos propres couleurs, `false` supprime l'œuf             |
| `becomes`       | non      | liste                              | aucun   | Les autres variantes en lesquelles celle-ci peut se transformer à l'apparition, selon un poids. Voir plus bas                                                                 |
| `baby`          | non      | booléen ou 0.0 à 1.0               | `false` | La fréquence à laquelle elle apparaît jeune, et elle le reste. `true` signifie toujours, un nombre est la part des apparitions concernées                                     |
| `keepsBaseBaby` | non      | booléen                            | `false` | Indique si le tirage de jeune propre à la base s'exécute aussi. Sans lui, une variante basée sur le zombie n'apparaît jeune que comme le dit `baby`, sans enfant `zombieBabyChance` de Forge ni poulet-jockey |
| `profession`    | non      | `namespace:name`                   | aléatoire | Pour un villageois, le métier qu'il exerce                                                                                                                                  |
| `career`        | non      | entier                             | aléatoire | Quelle carrière au sein de cette profession, à partir de 1                                                                                                                  |
| `requires`      | non      | liste d'ids de mods ou de namespaces de packs | aucun | La variante est ignorée sauf si tous sont présents                                                                                                                      |

Une variante est une classe à part entière : un monde qui en contient une dépend donc du pack qui l'a créée, au même titre que d'un mod. Retirez le fichier et les créatures de ce monde disparaissent avec lui.

**Un seul œuf ou générateur pour un mélange.** Une variante est une classe à part entière : seule, elle fait donc toujours apparaître exactement ce qu'elle dit. `becomes` est le moyen pour un pack de contourner cela : une liste de variantes en lesquelles celle-ci peut se transformer à l'apparition, chacune avec un poids, décidée créature par créature.

```json
{
  "becomes": [
    { "variant": "mypack:walker", "weight": 95 },
    { "variant": "mypack:little_walker", "weight": 5 }
  ]
}
```

Se nommer soi-même, c'est rester tel quel, et les poids sont les probabilités. Mettez cela sur `mypack:walker` : un seul œuf et une seule entrée d'apparition donnent surtout des marcheurs et, de temps en temps, un petit, comme un œuf de zombie vous donne parfois un bébé. Cela se produit quand la créature entre dans le monde, donc pour les œufs, `/summon` et l'apparition naturelle à la fois, et la créature qui arrive est une vraie créature de la variante choisie, avec tout ce que cette variante prévoit. Un générateur est plus strict : il ne tire que parmi les variantes de même mob de base et de même équipe que la variante à laquelle il est réglé, si bien qu'un générateur de zombies donne les zombies de cette équipe et leurs petits, jamais une créature d'une autre espèce ou d'une autre équipe, comme un générateur de zombies vanilla reste un générateur de zombies. Une variante atteinte ainsi ne se transforme pas de nouveau : deux variantes peuvent donc se nommer mutuellement sans tourner en boucle.

**Où se place `baby`.** Le jeu n'a pas de zombie bébé à part : il y a un seul zombie qui tire, à l'apparition, s'il est un enfant. `baby` dit à quelle fréquence, donc `"baby": 0.05` est l'habitude vanilla et `"baby": true` est toujours. Une variante ne reprend pas en plus le tirage propre au zombie : aucun enfant ni poulet-jockey n'apparaît sans que `baby` l'ait demandé ; `keepsBaseBaby` rend ce tirage. Ce sont deux manières d'obtenir la même chose, et le choix dépend de la différence voulue : `baby` seul donne une variante parfois jeune, `becomes` donne plusieurs variantes qui diffèrent comme vous voulez, et un mélange des deux est possible.

### Apparence

*variantes d'entités*

| Clé          | Requis | Valeur                                 | Défaut     | Rôle                                                                                                       |
| ------------ | -------- | -------------------------------------- | ---------- | ---------------------------------------------------------------------------------------------------------- |
| `texture`    | non      | `namespace:textures/entity/<file>.png` | aucun      | Une apparence à elle, agencée comme celle de l'entité copiée                                               |
| `tint`       | non      | couleur hexadécimale                   | aucun      | Colore l'entité lors de son affichage                                                                      |
| `tintParts`  | non      | liste de `body`, `armor`, `held`       | `["body"]` | Les parties que la teinte atteint                                                                          |
| `scale`      | non      | flottant                               | `1.0`      | Sa taille à l'affichage, et celle de sa boîte de collision                                                 |
| `angryScale` | non      | flottant                               | `scale`    | La taille qu'elle atteint en gonflant tant qu'elle a quelque chose à attaquer, et pendant trois secondes après l'avoir perdu |
| `width`      | non      | flottant                               | celle de la base | Sa largeur de collision, avant application de `scale`                                                |
| `height`     | non      | flottant                               | celle de la base | Sa hauteur de collision, avant application de `scale`                                                |
| `glowing`    | non      | booléen                                | `false`    | Contourée à travers les murs                                                                               |
| `bright`     | non      | booléen                                | `false`    | Affichée en pleine lumière où qu'elle se trouve, comme sous le soleil de midi, donc jamais assombrie par la nuit, l'ombre ou une grotte |
| `invisible`  | non      | booléen                                | `false`    | Non affichée, mais son équipement l'est toujours                                                           |
| `hideArmor`  | non      | booléen                                | `false`    | Porte son armure sans qu'elle soit affichée                                                                |
| `hideHeld`   | non      | booléen                                | `false`    | Idem pour ce qu'elle tient en main                                                                         |
| `leftHanded` | non      | booléen                                | `false`    | Tient son arme dans l'autre main                                                                           |

`scale` modifie à la fois le modèle et la boîte de collision, des deux côtés : ce que vous voyez est ce que vous pouvez toucher. Une créature qui change elle-même de taille, un animal qui grandit ou un zombie enfant, est mise à l'échelle autour de la taille qu'elle a choisie, les deux ne se contrarient donc pas. `angryScale` la fait gonfler tant qu'elle a une cible et la ramène à `scale` quand elle la perd. Comme le client ne sait jamais ce qu'une créature chasse, c'est le drapeau de sprint qui transporte cette information : il est activé sur une variante qui utilise `angryScale` et nulle part ailleurs, si bien qu'un mod qui lit le sprint sur vos variantes le verra changer. Grandir sous un plafond bas est possible, comme pour un slime qui grossit : gardez donc l'écart modeste.

Une `texture` est liée à la place de celle que l'entité utiliserait normalement, quel que soit le moteur de rendu dont elle hérite, et fonctionne donc pour les entités de mods comme pour celles de vanilla. Elle doit correspondre au modèle sur lequel elle est dessinée, puisque le modèle est celui de l'entité de base : une apparence, pas une nouvelle forme. Les couches gardent leurs propres textures, l'armure ressemble donc toujours à de l'armure sur un zombie reskinné.

L'armure n'est dessinée que sur une entité dont le moteur de rendu possède une couche d'armure, ce qui dans cette version désigne les mobs humanoïdes et les villageois. Une variante de vache ou d'araignée peut porter une armure et en tire la protection, mais rien ne l'affiche : `armor` sous `attributes` est donc généralement la façon la plus propre de rendre une telle créature robuste. `hideArmor` sert à l'autre cas : un humanoïde qui doit garder l'armure dans ses emplacements, pour la protection ou pour un mod qui les lit, sans qu'on la voie.

### Ses sons

*variantes d'entités*

| Clé           | Requis | Valeur  | Défaut     | Rôle                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| ------------- | -------- | ------- | ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `sounds`      | non      | objet   | ceux de la base | `ambient`, `hurt` et `death`, chacun un évènement sonore enregistré. Trois autres pour lesquels la base n'a aucun son : `target` est joué une fois à chaque fois qu'elle prend une cible, et `explode` est le son de son explosion à la place de celui du jeu, qu'elle se fasse exploser avec `explodes` ou lance de la TNT avec `throws`. `throw` est joué quand elle lance quoi que ce soit avec `throws`, à la place du lancer de boule de neige, ou du sifflement de la mèche pour la TNT. `targetVaries` décale chaque lecture de `target` vers le haut ou le bas d'une valeur aléatoire dans la limite de ce nombre de demi-tons : `3` fait donc varier d'un quart d'octave de part et d'autre ; `0` le joue tel quel |
| `soundVolume` | non      | nombre  | `1.0`      | Le volume de ces sons                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `soundPitch`  | non      | nombre  | `1.0`      | Leur hauteur. En dessous de 1 c'est plus grave, au-dessus de 1 plus aigu                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `silent`      | non      | booléen | `false`    | Ne fait aucun bruit                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |

### Santé, dégâts et effets

*variantes d'entités*

| Clé                 | Requis | Valeur                                          | Défaut           | Rôle                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| ------------------- | -------- | ----------------------------------------------- | ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `attributes`        | non      | objet                                           | aucun            | `maxHealth`, `movementSpeed`, `attackDamage`, `attackSpeed`, `knockbackResistance`, `followRange`, `armor`. Un attribut que l'entité n'a pas normalement lui est ajouté. `attackSpeed` est le nombre de coups par seconde pour un combattant au corps à corps, `1` comme dans le jeu, donc `2` frappe deux fois plus souvent. Tout nom que la base possède déjà fonctionne aussi, `zombie.spawnReinforcements`, `horse.jumpStrength`. `attackDamage` sur une base qui tire correspond aux dégâts de ses flèches |
| `absorption`        | non      | flottant                                        | `0`              | Des cœurs supplémentaires en plus de sa santé                                                                                                                                                                                                                                                                                                                                                                                                           |
| `invulnerable`      | non      | booléen                                         | `false`          | Ne subit aucun dégât, hors le vide et le mode créatif                                                                                                                                                                                                                                                                                                                                                                                                   |
| `fireproof`         | non      | booléen                                         | `false`          | Ne prend jamais feu, donc n'est jamais blessée par le feu ou la lave et ne brûle jamais en plein jour                                                                                                                                                                                                                                                                                                                                                   |
| `immuneTo`          | non      | liste de types de dégâts                        | aucun            | Les dégâts qu'elle ignore : `fall`, `drown`, `explosion`, `magic`, `cactus`, `lava`, `wither`, `starve`, `anvil`, `inWall` et les autres                                                                                                                                                                                                                                                                                                                 |
| `fallDamage`        | non      | flottant                                        | `1.0`            | Multiplie les dégâts d'une chute. `0` supprime les dégâts de chute                                                                                                                                                                                                                                                                                                                                                                                      |
| `hurtResistance`    | non      | entier, ticks                                   | celui de la base, `20` | Le temps après un coup pendant lequel elle ne peut pas être blessée de nouveau. Les coups plus rapprochés que la moitié de cette durée sont perdus : un attaquant rapide veut donc une cible avec une valeur plus basse                                                                                                                                                                                                                      |
| `effects`           | non      | liste d'objets                                  | aucun            | Les effets qu'elle a en permanence : `{ "potion": "minecraft:strength", "amplifier": 1 }`                                                                                                                                                                                                                                                                                                                                                               |
| `ignoresEffects`    | non      | liste d'ids de potions, ou `all`                | aucun            | Les effets qui ne s'appliquent jamais à elle, quel que soit celui ou ce qui les applique : un coup, une potion jetable, une balise, une flèche, `/effect`. `all` refuse tous les effets, une variante part ainsi d'une page blanche. Ses propres `effects` lui sont tout de même appliqués                                                                                                                                                                  |
| `creatureAttribute` | non      | `undefined`, `undead`, `arthropod` ou `illager` | celui de la base | Ce qu'elle est considérée être, pour que Châtiment et les potions de soin la traitent en conséquence                                                                                                                                                                                                                                                                                                                                                    |

### Déplacement

*variantes d'entités*

| Clé              | Requis | Valeur        | Défaut     | Rôle                                                                                                                                      |
| ---------------- | -------- | ------------- | ---------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| `jumpMultiplier` | non      | flottant      | `1.0`      | De combien elle saute plus haut que l'entité qu'elle copie                                                                                |
| `stepHeight`     | non      | flottant, blocs | celle de la base | La hauteur de rebord qu'elle gravit sans sauter. La plupart des créatures montent `0.6`, un zombie `1.0`                            |
| `maxFallHeight`  | non      | entier        | celle de la base | De quelle hauteur elle acceptera de tomber en cherchant son chemin                                                                  |
| `climbs`         | non      | booléen       | celui de la base | Escalade les murs comme une araignée, et y trace son chemin ; `false` cloue une araignée au sol                                     |
| `teleports`      | non      | booléen       | `true`     | Indique si un enderman ou un shulker peut se téléporter. Désactivé, il reste où il se tient, en plein jour et dans l'eau aussi            |
| `walks`          | non      | booléen       | `false`    | Un lapin marche comme les autres animaux au lieu de se déplacer par bonds. Seul un lapin le lit                                           |
| `pathPriorities` | non      | objet         | aucun      | Ce qu'elle acceptera de traverser, comme `WATER`, `LAVA`, `DANGER_FIRE`, `DOOR_WOOD_CLOSED` et les autres, chacun un nombre où une valeur négative signifie jamais |
| `leashable`      | non      | booléen       | `false`    | Peut être menée en laisse, même si l'entité qu'elle copie ne le pouvait jamais                                                            |
| `steerable`      | non      | booléen       | `false`    | Peut être dirigée quand on la chevauche                                                                                                   |
| `noAI`           | non      | booléen       | `false`    | Reste où on la place et ne fait rien                                                                                                      |

### Eau

*variantes d'entités*

| Clé                  | Requis | Valeur  | Défaut  | Rôle                                                                                                                                                                                                                                     |
| -------------------- | -------- | ------- | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `breathesUnderwater` | non      | booléen | `false` | Ne se noie jamais, et coule pour marcher au fond plutôt que de nager vers la surface. Elle trouve toujours son chemin au sol : une eau profonde dont elle ne peut pas sortir à pied la retiendra                                          |
| `swims`              | non      | booléen | `false` | Se déplace dans l'eau comme un poulpe ou un gardien, et ne se noie jamais. Elle trouve son chemin dans l'eau plutôt qu'au sol : sa place est dans l'eau et elle est échouée en dehors                                                    |
| `amphibious`         | non      | booléen | `false` | Marche sur terre et nage correctement dans l'eau, en changeant sa manière de trouver son chemin quand elle entre dans l'eau ou en sort. Elle ne se noie jamais. Ce qu'elle poursuivait est oublié au bord de l'eau : elle hésite donc un instant à chaque traversée |
| `waterSlowdown`      | non      | flottant | `0.8`  | De combien l'eau la ralentit. Plus haut, c'est plus rapide                                                                                                                                                                               |

### Combat

*variantes d'entités*

| Clé             | Requis | Valeur               | Défaut            | Rôle                                                                                                                                                                                                                                                                                                            |
| --------------- | -------- | -------------------- | ----------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `hostile`       | non      | booléen              | `false`           | Attaque ce qu'elle peut atteindre, et riposte quand on la blesse. Une variante hostile compte comme un monstre pour le jeu quelle que soit sa base : le plafond de monstres la limite et le mode paisible la supprime, et elle abandonne les tâches d'animal de sa base, reproduction, tentation, suivi d'un parent, d'un maître ou de ses congénères, position assise |
| `passive`       | non      | booléen              | `false`           | L'empêche d'attaquer quoi que ce soit, quel que soit son comportement habituel                                                                                                                                                                                                                                  |
| `targets`       | non      | liste de noms d'entités | le joueur      | Ce qu'elle cherche tant qu'elle est hostile. `minecraft:player` est reconnu bien que le joueur ne soit pas une entité enregistrée                                                                                                                                                                               |
| `attackReach`   | non      | flottant, blocs      | sa taille         | La portée d'un coup au corps à corps. Le jeu compte deux fois la largeur, ce qui explique qu'une créature agrandie frappe de plus loin ; ceci la fixe directement                                                                                                                                                |
| `knockback`     | non      | flottant             | celui de la base, `0.4` | La force avec laquelle ses coups repoussent. `0` ne repousse pas du tout                                                                                                                                                                                                                                  |
| `hitEffects`    | non      | booléen              | `true`            | Indique si elle inflige à ce qu'elle frappe l'effet qu'inflige l'entité qu'elle copie : le wither d'un squelette wither, le poison d'une araignée bleue, la faim d'un zombie momifié. Désactivé, elle ne frappe que pour les dégâts                                                                                |
| `hitFire`       | non      | booléen              | `true`            | Indique si elle enflamme ce qu'elle frappe quand l'entité qu'elle copie le ferait : un zombie en feu, la boule de feu d'un blaze. Désactivé, rien de ce qu'elle fait ne met le feu à sa cible                                                                                                                    |
| `threatLeast`   | non      | entier               | `0`               | Le plus bas palier de menace dans lequel un joueur ou autre porteur situé à moins de 128 blocs doit se trouver avant que la variante apparaisse naturellement. `0` apparaît comme d'habitude                                                                                                                      |
| `threatHostile` | non      | entier               | `0`               | Le plus bas palier de menace dans lequel un joueur doit se trouver avant que la variante s'en prenne à lui d'elle-même. En dessous, la variante est docile envers ce joueur, bien qu'elle riposte encore quand on la frappe. `0` attaque comme d'habitude                                                          |

`hostile` supprime aussi le comportement qui faisait fuir la créature : un animal qui évitait les joueurs ou paniquait en étant blessé ne fait plus ni l'un ni l'autre une fois hostile, car il fuirait sinon ce qu'il est censé attaquer. Il faut une entité qui marche au sol, puisqu'elle utilise le même comportement d'attaque que vanilla donne à ses propres mobs. Une base volante ou nageuse est consignée dans le journal et laissée telle quelle. `passive` agit plus largement, mais n'atteint que les comportements construits comme vanilla les construit : un mod dont l'hostilité est écrite dans son propre code de tick ou de dégâts n'est pas quelque chose qu'un pack peut apaiser.

### Équipement, butins et expérience

*variantes d'entités*

| Clé                  | Requis | Valeur                      | Défaut     | Rôle                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| -------------------- | -------- | --------------------------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `equipment`          | non      | objet                       | aucun      | `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`, chacun un nom d'objet                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `dropChance`         | non      | 0 à 1                       | `0`        | La probabilité que chaque pièce d'équipement soit lâchée                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `picksUpLoot`        | non      | booléen                     | `false`    | Ramasse ce sur quoi elle marche                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `lootTable`          | non      | `namespace:entities/<name>` | celle de la base | Ce qu'elle lâche. Sans cette clé, elle lâche ce que lâche l'entité qu'elle copie                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `experience`         | non      | entier                      | celle de la base | La quantité d'expérience qu'elle lâche                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `collectsExperience` | non      | booléen                     | `false`    | Récolte l'expérience comme le fait un joueur : les orbes situés à moins de huit blocs dérivent vers elle et sont absorbés au contact, le Raccommodage sur son équipement est réparé en premier, et les points forment des niveaux selon la courbe du joueur, conservés sur le mob à travers une sauvegarde. Ce qu'elle tue lâche son expérience comme si un joueur avait porté le coup, un bloc que sa tâche `digs` casse lâche l'expérience propre au bloc, et un tirage d'expérience de `block_drops` lui revient aussi. À sa mort, elle lâche sept par niveau jusqu'à cent, sauf si `keepInventory` est activé. Les objectifs avec le critère `xp` ou `level` portent son total et son niveau sur une ligne nommée par son UUID, de sorte qu'une fonction les lit avec `score_<objective>_min`. Elle dépense ses niveaux en travail à l'enclume comme un joueur, voir [Travail à l'enclume](#travail-à-lenclume) |

Une variante lâche ce que lâche l'entité qu'elle copie, car la table de butin est fixée dans le code de cette entité et non retrouvée par son nom. `lootTable` la dirige vers une table de votre cru, que vous fournissez ensuite à `loot_tables/entities/<name>.json` comme n'importe quelle autre.

### Comportements spéciaux

*variantes d'entités*

| Clé              | Requis | Valeur       | Défaut          | Rôle                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ---------------- | -------- | ------------ | --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `throws`         | non      | booléen      | `false`         | Lance ce qu'elle tient sur sa cible depuis une certaine distance, et si c'est de la TNT elle l'allume et recule. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `throwAmmo`      | non      | entier       | aucun           | Combien d'exemplaires elle a à lancer. S'il est omis, elle n'en manque jamais                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `throwReload`    | non      | entier, secondes | `explosionFuse` | Combien de temps sa main reste vide avant d'en prendre un autre                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `throwRetreat`   | non      | entier, secondes | `explosionFuse` | Combien de temps elle reste à distance après un lancer avant de se retourner                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `throwPower`     | non      | flottant     | `1.0`           | La force du lancer. Le doubler double à peu près la portée                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `throwArc`       | non      | flottant     | `0.35`          | La hauteur de la cloche. Plus haut, cela reste plus longtemps en l'air, près de zéro c'est un jet à plat, sous zéro cela lance vers le bas                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `throwReturns`   | non      | booléen      | `false`         | Ce qu'elle lance vole comme un trident : il frappe pour l'`attackDamage` de la variante, ou 8 sur une base qui n'en a pas, puis revient dans sa main comme Loyauté ramène un trident. Il n'est jamais épuisé et est visé sur la cible comme un squelette vise, plus vite avec `throwPower` et avec moins de dispersion aux difficultés plus élevées, et le lanceur tient sa position pendant qu'il vole : `throwAmmo`, `throwReload`, `throwRetreat` et `throwArc` ne s'y appliquent donc pas. La TNT est lancée comme toujours                                                                                                                                                                                                                                                                                                                  |
| `explodes`       | non      | booléen      | `false`         | Se fait exploser près de sa cible, comme un creeper. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `explosionPower` | non      | nombre       | `3.0`           | La taille de l'explosion. Un creeper fait 3, la TNT 4. Sur une base de creeper c'est aussi l'explosion propre au creeper, et sur un ghast celle de la boule de feu                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `explosionFuse`  | non      | entier, ticks | `30`           | Combien de temps elle siffle avant d'exploser. Sur une base de creeper c'est aussi la mèche propre au creeper                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `explosionFire`  | non      | booléen      | `false`         | Laisse des feux derrière elle                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `charges`        | non      | booléen      | `false`         | Fonce sur sa cible depuis une certaine distance et la frappe avec un fort recul au contact, comme un ravageur, puis se repose avant l'assaut suivant. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `pounces`        | non      | booléen      | `false`         | Se ramasse, puis bondit sur sa cible en arc et frappe à l'atterrissage, comme un renard. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `sniffs`         | non      | entier, blocs | `0`            | Entend les joueurs qui bougent dans ce nombre de blocs, murs ou pas, et marche vers l'endroit où elle les a entendus ; un joueur qui s'accroupit ou reste immobile n'est pas entendu, et celui qu'elle voit alors devient sa cible. `0` n'écoute pas. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `fleesWhenHurt`  | non      | 0.0 à 1.0    | `0`             | Rompt le combat et fuit celui qu'elle affronte tant que sa santé est sous cette fraction, et revient une fois au-dessus. `0` ne fuit jamais. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `sleepsByDay`    | non      | booléen      | `false`         | Cherche l'ombre de jour et y reste immobile jusqu'à la nuit ou jusqu'à ce que quelque chose l'attaque. Pendant son repos, elle est couchée sur le côté                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `home`           | non      | entier, blocs | `0`            | Reste dans ce nombre de blocs autour de l'endroit où elle s'est tenue en premier, errant à l'intérieur et revenant quand elle s'écarte. `0` erre librement                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `patrols`        | non      | booléen      | `false`         | Parcourt le pays par longues étapes avec d'autres de son espèce qui suivent un chef, comme une patrouille de pillards. Un groupe qui apparaît ensemble choisit un chef ; les autres restent à quelques blocs de lui, et quand le chef prend une cible, tous la prennent. Un suiveur qui perd son chef prend lui-même la tête. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `swoops`         | non      | booléen      | `false`         | Tourne au-dessus de sa cible et plonge à travers elle, frappant au passage, comme un fantôme. La variante reçoit un assistant de vol : elle vole pendant la chasse et se pose au sol au repos ; il lui faut une base qui soit une créature, un perroquet par exemple, et une chauve-souris n'en est pas une. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `gusts`          | non      | booléen      | `false`         | Se concentre puis lâche une rafale de vent sur sa cible depuis une certaine distance, projetant tout ce qui est près de la cible en arrière et en l'air, comme la charge de vent d'un Breeze. Nécessite `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `gustPower`      | non      | flottant     | `1.5`           | La force de projection d'une rafale. Un coup de mob vaut 0.4, un fort enchantement de recul environ 1                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `digs`           | non      | booléen      | `false`         | Creuse tout ce qui se dresse entre elle et sa cible, avec l'outil qu'elle tient : une pelle dans la terre, le sable et le gravier, une pioche dans la pierre, une hache dans le bois, et seulement ce que le matériau de cet outil peut casser, de sorte qu'une pioche en bois n'ouvre jamais un minerai de fer et que rien n'ouvre l'obsidienne sans diamant. Un bloc prend le temps qu'il prendrait pour un joueur avec cet outil, lâche ce qu'il lâcherait, et use l'outil. Donnez-lui l'outil avec `equipment` ; les mains nues, elle ne creuse rien, et elle ne creuse rien là où `mobGriefing` est désactivé. Elle ne cherche jamais à contourner : avec une cible, elle marche droit dessus et creuse tout ce qui gêne, et là où l'outil ne peut pas ouvrir le bloc, elle reste là et pousse. Nécessite `hostile`. Elle prend ses cibles sans avoir besoin de les voir, car ce vers quoi elle creuse est par nature derrière quelque chose |

**Lancer au lieu de charger.** `explodes` envoie une créature se faire exploser. `throws` est l'autre tempérament : elle garde ses distances, lance ce qu'elle a dans sa main principale sur ce qu'elle combat, et si c'est de la TNT elle l'allume, la lance, et recule pendant qu'elle brûle.

```json
{
  "hostile": true,
  "throws": true,
  "explosionFuse": 50,
  "equipment": { "mainhand": "minecraft:tnt" }
}
```

Lancer vide sa main, puisqu'elle a lancé l'objet. Elle reste alors à distance pendant `throwRetreat`, en prend un autre après `throwReload`, et se retourne contre sa cible : une boucle de lancer, repli, rechargement, rapprochement. Donnez-lui un `throwAmmo` et cette boucle s'arrête quand le compte est épuisé, sa main restant vide pour de bon et son attaque ordinaire reprenant le relais. Omettez `throwAmmo` et elle n'en manque jamais.

Le compte est écrit dans la créature, il ne se remplit donc pas parce qu'un chunk a été déchargé puis rechargé. Tout ce qui n'est pas de la TNT vole comme un objet et retombe, ce qui rend un sapeur qui jette des cailloux ou de la chair putréfiée aussi simple qu'un qui jette des explosifs.

`explosionFuse` reste la mèche de la TNT lancée, et remplace l'un ou l'autre des délais que vous omettez, de sorte qu'une variante écrite avant ces clés se comporte exactement comme avant.

La façon dont le lancer vole dépend de `throwPower` et `throwArc`. Le premier est un multiplicateur de la poussée, et comme la poussée croît déjà avec la distance, l'augmenter allonge la portée sans changer la durée du vol. Le second est la portance, et il change la forme : élevé, il lobe par-dessus un mur et prend son temps, près de zéro il est lancé à plat et retombe presque aussitôt, sous zéro il est jeté vers le bas sur quelque chose en contrebas. Les deux laissent la mèche intacte : une charge en cloche et une charge à plat explosent donc le même nombre de secondes après avoir quitté la main, ce qui décide si elle éclate en l'air ou retombe d'abord et attend. La distance depuis laquelle elle lancera est son `followRange`, et elle se rapproche comme d'habitude une fois que vous êtes à moins de trois blocs : dangereuse à distance, ordinaire en face à face.

### Tâches

*variantes d'entités*

**Toute tâche du jeu.** Les clés ci-dessus sont des comportements propres à RDPL. `tasks` va au-delà : jusqu'à toutes les tâches que vanilla emploie lui-même, sur n'importe quelle base : une entrée est un objet qui nomme la `task` et sa `priority`, plus ce que cette tâche lit ; un nom précédé d'un `-` retire toutes les tâches de ce type fournies avec la base. Les priorités vont de 0, le premier, vanilla gardant les siennes entre 1 et 8 : une tâche à 0 l'emporte donc sur tout ce que fait la base, et une à 9 ne s'exécute que lorsque rien d'autre n'en veut.

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

| Clé     | Requis | Valeur | Défaut | Rôle                                                                                                                                           |
| ------- | -------- | ----- | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| `tasks` | non      | liste | aucun   | Toute tâche du jeu, ajoutée à la variante par son nom avec la priorité de votre choix, ou retirée de ce que sa base apportait. La liste ci-dessous |

La liste est appliquée après que `hostile`, `passive` et les comportements ci-dessus ont fait leur travail : elle a donc le dernier mot. Les tâches qui déplacent le corps s'excluent mutuellement : l'une ne s'exécute que si rien devant elle en priorité ne déplace la créature, et l'attaque dont un monstre est pourvu se situe à 2, de sorte qu'un bond ou une fuite sur un zombie a besoin de la priorité 1 sans quoi elle n'a jamais son tour ; l'araignée et le loup gardent leur bond devant leur attaque pour la même raison. Une tâche que la base exécute déjà est ajoutée une seconde fois plutôt que remplacée ; retirez d'abord l'ancienne. Certaines tâches n'ont de sens que sur une base qui possède ce qu'elles pilotent : un combat à l'arc exige une base qui tire, s'asseoir exige une base qui peut être apprivoisée, et commercer exige un villageois. Demandez-en une sur une base qui ne peut pas la porter et le journal dit de quelle base elle a besoin, et la variante s'en passe.

| Clé         | Type               | Défaut           | Rôle                                                                                                                                                                   |
| ----------- | ------------------ | ---------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `priority`  | entier             | obligatoire      | Sa place parmi les tâches de la base. Plus bas s'exécute en premier                                                                                                    |
| `speed`     | nombre             | l'habituel de la tâche | La vitesse de déplacement pendant que la tâche s'exécute, en multiplicateur de sa vitesse de marche                                                              |
| `nearSpeed` | nombre             | `1.2`            | `avoidEntity` : le multiplicateur une fois que ce qu'elle évite est proche                                                                                             |
| `distance`  | nombre, blocs      | l'habituelle de la tâche | La distance à laquelle elle regarde, suit, tire ou se tient à l'écart                                                                                          |
| `near`      | nombre, blocs      | l'habituelle de la tâche | `follow`, `followOwner`, `followOwnerFlying` : à quelle distance elle s'approche avant de s'arrêter                                                            |
| `chance`    | nombre             | l'habituelle de la tâche | `wander` : un tirage tous les tant de ticks ; `wanderAvoidWater` : la probabilité, de 0 à 1, de quitter l'abri ; `watchClosest`, `watchClosest2` : la probabilité, de 0 à 1, de regarder à chaque tick |
| `leap`      | nombre             | `0.4`            | `leapAtTarget` : la hauteur du bond                                                                                                                                    |
| `cooldown`  | entier, ticks      | `20`             | `attackRanged`, `attackRangedBow` : ticks entre deux tirs                                                                                                              |
| `entity`    | nom d'entité       | aucun            | Quelle entité la tâche cherche, évite, observe ou avec laquelle elle se reproduit. `minecraft:player` est reconnu                                                      |
| `items`     | liste de noms d'objets | aucun        | `tempt` : ce qu'un joueur tend                                                                                                                                         |
| `sight`     | booléen            | `true`           | `nearestAttackableTarget`, `targetNonTamed` : uniquement ce qu'elle peut voir                                                                                          |
| `nearby`    | booléen            | `false`          | `nearestAttackableTarget` : uniquement ce qui est dans sa propre portée de suivi                                                                                       |
| `help`      | booléen            | `false`          | `hurtByTarget` : les autres de son espèce à proximité se joignent à elle                                                                                               |
| `memory`    | booléen            | `false`          | `attackMelee`, `zombieAttack` : continue de poursuivre une cible qu'elle a perdue de vue                                                                               |
| `close`     | booléen            | `false`          | `openDoor` : referme la porte derrière elle                                                                                                                            |
| `nocturnal` | booléen            | `false`          | `moveThroughVillage` : uniquement la nuit                                                                                                                              |
| `scared`    | booléen            | `false`          | `tempt` : un joueur qui bouge trop vite rompt le charme                                                                                                                |

La colonne `Liste` indique où la tâche réside. `tasks` est ce que fait la créature ; `targets` est la manière dont elle choisit ce qu'elle poursuit, et une tâche de ciblage sans attaque correspondante ne fait rien d'elle-même.

| Tâche                     | Nécessite                      | Liste     | Lit                                        | Rôle                                                                      |
| ------------------------- | ------------------------------ | --------- | ------------------------------------------ | ------------------------------------------------------------------------- |
| `attackMelee`             | une créature terrestre         | `tasks`   | `speed`, `memory`                          | S'approche de sa cible et la frappe                                       |
| `attackRanged`            | une base qui tire              | `tasks`   | `speed`, `cooldown`, `distance`            | Garde ses distances et tire ce que sa base tire                           |
| `attackRangedBow`         | un monstre qui tire            | `tasks`   | `speed`, `cooldown`, `distance`            | Le combat à l'arc du squelette : se déplace de côté, bande et décoche     |
| `avoidEntity`             | une créature terrestre         | `tasks`   | `entity`, `distance`, `speed`, `nearSpeed` | Fuit l'entité nommée quand elle s'approche à moins de `distance`          |
| `beg`                     | un loup                        | `tasks`   | `distance`                                 | Quémande auprès d'un joueur qui tend de la nourriture                     |
| `breakDoor`               | n'importe quelle base          | `tasks`   |                                            | Brise les portes en bois sur son chemin, en difficulté difficile          |
| `creeperSwell`            | un creeper                     | `tasks`   |                                            | Siffle et explose près de sa cible                                        |
| `defendVillage`           | un golem de fer                | `targets` |                                            | S'en prend à quiconque a attaqué un villageois                            |
| `eatGrass`                | n'importe quelle base          | `tasks`   |                                            | Mange de l'herbe, comme un mouton                                         |
| `findEntityNearest`       | n'importe quelle base          | `targets` | `entity`                                   | Cible la plus proche de l'entité nommée, comme un slime ou un ghast       |
| `findEntityNearestPlayer` | n'importe quelle base          | `targets` |                                            | Cible le joueur le plus proche qu'elle peut atteindre                     |
| `fleeSun`                 | une créature terrestre         | `tasks`   | `speed`                                    | Cherche l'ombre quand le soleil l'atteint                                 |
| `follow`                  | n'importe quelle base          | `tasks`   | `speed`, `near`, `distance`                | Suit les autres de sa propre espèce                                       |
| `followGolem`             | un villageois                  | `tasks`   |                                            | Suit un golem de fer qui tend une fleur                                   |
| `followOwner`             | une base apprivoisable         | `tasks`   | `speed`, `near`, `distance`                | Suit son maître, et se téléporte auprès de lui quand elle est loin derrière |
| `followOwnerFlying`       | une base apprivoisable         | `tasks`   | `speed`, `near`, `distance`                | Idem, en volant                                                           |
| `followParent`            | un animal                      | `tasks`   | `speed`                                    | Un petit reste près d'un adulte de son espèce                             |
| `harvestFarmland`         | un villageois                  | `tasks`   | `speed`                                    | Récolte les cultures mûres et les replante                                |
| `hurtByTarget`            | une créature terrestre         | `targets` | `help`                                     | Riposte à ce qui l'a frappée                                              |
| `landOnOwnersShoulder`    | un perroquet                   | `tasks`   |                                            | Se perche sur l'épaule de son maître                                      |
| `leapAtTarget`            | n'importe quelle base          | `tasks`   | `leap`                                     | Bondit sur sa cible de près                                               |
| `llamaFollowCaravan`      | un lama                        | `tasks`   | `speed`                                    | Se met à la suite d'un lama mené en laisse                                |
| `lookAtTradePlayer`       | un villageois                  | `tasks`   |                                            | Fait face au joueur avec qui il échange                                   |
| `lookAtVillager`          | un golem de fer                | `tasks`   |                                            | Regarde les villageois                                                    |
| `lookIdle`                | n'importe quelle base          | `tasks`   |                                            | Regarde autour d'elle de temps en temps                                   |
| `mate`                    | un animal                      | `tasks`   | `speed`, `entity`                          | Se reproduit en mode amour, avec sa propre espèce ou l'`entity` nommée    |
| `moveIndoors`             | une créature terrestre         | `tasks`   |                                            | Rentre dans une maison du village à la tombée de la nuit                  |
| `moveThroughVillage`      | une créature terrestre         | `tasks`   | `speed`, `nocturnal`                       | Parcourt les chemins du village de porte en porte                         |
| `moveTowardsRestriction`  | une créature terrestre         | `tasks`   | `speed`                                    | Retourne vers son point d'attache quand elle s'écarte                     |
| `moveTowardsTarget`       | une créature terrestre         | `tasks`   | `speed`, `distance`                        | Se rapproche d'une cible lointaine                                        |
| `nearestAttackableTarget` | une créature terrestre         | `targets` | `entity`, `sight`, `nearby`                | Cible la plus proche de l'entité nommée                                   |
| `ocelotAttack`            | n'importe quelle base          | `tasks`   |                                            | La traque et le bond du chat                                              |
| `ocelotSit`               | un ocelot                      | `tasks`   | `speed`                                    | S'assoit sur les coffres, les lits et les fourneaux allumés               |
| `openDoor`                | n'importe quelle base          | `tasks`   | `close`                                    | Ouvre les portes en bois qu'elle franchit                                 |
| `ownerHurtByTarget`       | une base apprivoisable         | `targets` |                                            | S'en prend à ce qui a frappé son maître                                   |
| `ownerHurtTarget`         | une base apprivoisable         | `targets` |                                            | S'en prend à ce que son maître a frappé                                   |
| `panic`                   | une créature terrestre         | `tasks`   | `speed`                                    | Court quand elle est blessée ou en feu                                    |
| `play`                    | un villageois                  | `tasks`   | `speed`                                    | Les enfants jouent à chat entre eux                                       |
| `restrictOpenDoor`        | une créature terrestre         | `tasks`   |                                            | Reste derrière les portes du village la nuit                              |
| `restrictSun`             | une créature terrestre         | `tasks`   |                                            | Reste à l'ombre de jour                                                   |
| `runAroundLikeCrazy`      | un cheval, un âne, une mule ou un lama | `tasks` | `speed`                              | Désarçonne un cavalier en qui elle n'a pas encore confiance               |
| `sit`                     | une base apprivoisable         | `tasks`   |                                            | S'assoit quand on le lui ordonne                                          |
| `skeletonRiders`          | un cheval squelette            | `tasks`   |                                            | Appelle des cavaliers squelettes quand un joueur s'approche, le cheval piège |
| `swimming`                | n'importe quelle base          | `tasks`   |                                            | Garde la tête hors de l'eau                                               |
| `targetNonTamed`          | une base apprivoisable         | `targets` | `entity`, `sight`                          | Cible l'entité nommée tant qu'elle n'est pas encore apprivoisée           |
| `tempt`                   | une créature terrestre         | `tasks`   | `items`, `speed`, `scared`                 | Suit un joueur qui tend l'un des `items`                                  |
| `tradePlayer`             | un villageois                  | `tasks`   |                                            | Reste immobile pendant l'échange                                          |
| `villagerInteract`        | un villageois                  | `tasks`   |                                            | Discute avec d'autres villageois                                          |
| `villagerMate`            | un villageois                  | `tasks`   |                                            | Se reproduit quand le village a de la place                               |
| `wander`                  | une créature terrestre         | `tasks`   | `speed`, `chance`                          | Erre au hasard                                                            |
| `wanderAvoidWater`        | une créature terrestre         | `tasks`   | `speed`, `chance`                          | Erre en évitant l'eau                                                     |
| `wanderAvoidWaterFlying`  | une créature terrestre         | `tasks`   | `speed`                                    | Erre dans les airs et se perche dans les arbres                           |
| `watchClosest`            | n'importe quelle base          | `tasks`   | `entity`, `distance`, `chance`             | Regarde la plus proche de l'entité nommée, le joueur si aucune n'est nommée |
| `watchClosest2`           | n'importe quelle base          | `tasks`   | `entity`, `distance`, `chance`             | Idem, maintenu pendant qu'une autre tâche s'exécute                       |
| `zombieAttack`            | un zombie                      | `tasks`   | `speed`, `memory`                          | L'attaque du zombie, bras levés                                           |

### Apparition et disparition

*variantes d'entités*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `spawns` | non | liste d'objets | aucun | `creatureType`, `weight`, `min` et `max`, la même forme que celle d'un biome |
| `biomes` | non | liste de noms de biomes | tous les biomes | Où ces apparitions sont ajoutées |
| `biomeTypes` | non | liste de types du dictionnaire | aucun | Idem, par type |
| `ignoresSpawnRules` | non | booléen | `false` | Apparaît où on le place, sans tenir compte des règles héritées |
| `despawns` | non | booléen | `true` | Désactivé, il reste même quand il serait normalement retiré |
| `despawnAfter` | non | entier, secondes | aucun | Il s'en va discrètement une fois ce temps passé dans le monde, quelle que soit la distance des joueurs |
| `persistent` | non | booléen | `false` | Ne disparaît jamais |

**Une créature à durée de vie limitée.** `despawnAfter` compte en secondes à partir du moment où une créature entre pour la première fois dans le monde, et la retire discrètement quand le temps est écoulé : ni mort, ni butin, ni son, exactement comme si elle s'était éloignée et avait été retirée. L'horloge est inscrite dans la créature elle-même : elle continue de tourner après une sauvegarde et un rechargement, au lieu de repartir de zéro à chaque retour d'un chunk.

C'est un mécanisme à part, et non un complément aux règles que gouvernent `despawns` et `persistent`. Ces deux clés décident si le jeu peut retirer une créature parce qu'elle est loin de tout joueur ; celle-ci est une promesse qu'elle partira à un moment fixé, quoi qu'il arrive. Une créature peut être `persistent` et avoir tout de même une durée de vie, ce qui convient à ce qu'on invoque pour un combat ou un événement et qui ne doit pas lui survivre.

L'horloge suit le temps du monde : elle s'arrête quand personne ne joue et ne compte pas les minutes pendant lesquelles un chunk est resté déchargé.

### Réseau

*variantes d'entités*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `trackingRange` | non | entier | `80` | À quelle distance le client est informé de son existence |
| `trackVelocity` | non | booléen | `true` | Envoie sa vitesse en plus de sa position. Désactivé, cela économise du trafic pour ce qui bouge à peine |
| `trackingFrequency` | non | entier | `3` | Fréquence d'envoi, en ticks |

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
        { "oreDict": "ingotIron" }
      ]
    },
    "fluid": {
      "capacity": 16000,
      "buckets": true,
      "use": 5,
      "filter": [
        { "fluid": "water", "max": 8000 }
      ]
    },
    "energy": { "capacity": 100000, "transfer": 1000, "use": 20 },
    "runsDry": "stops",
    "dropsOnDeath": true
  }
}
```

`storage` donne à une variante de n'importe quelle entité des emplacements d'objets, un réservoir de fluide et un tampon d'énergie, chacun uniquement si son objet est écrit. Chacun est proposé comme capacité d'objets, de fluides ou d'énergie de Forge de l'entité, si bien que tout ce qui déplace des objets, du fluide ou de l'énergie vers une entité l'atteint. Quand l'entité de base répond elle-même à cette capacité, comme le fait un mob pour ses mains et son armure, ou un cheval ou un wagonnet à coffre pour son inventaire, le stockage du pack répond à sa place, sur tous les côtés. Un joueur ouvre l'écran en s'accroupissant et en faisant un clic droit sur l'entité. Le contenu est sauvegardé avec l'entité.

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `items` | non | objet | aucun | Donne à l'entité des emplacements d'objets. La zone fait trois rangées de 9 : chaque jauge de fluide ou d'énergie prend une rangée et les emplacements occupent le reste, soit 1x9 avec les deux jauges, 2x9 avec une seule et 3x9 sans aucune. Sans `items`, seules les jauges s'affichent |
| `fluid` | non | objet | aucun | Un réservoir de fluide |
| `energy` | non | objet | aucun | Un tampon de Forge Energy |
| `dropsOnDeath` | non | booléen | `true` | Les objets stockés tombent au sol à l'endroit où l'entité meurt. `false` les perd. Le fluide et l'énergie sont perdus dans les deux cas |
| `runsDry` | non | `stops`, `slows` ou `hurts` | `stops` | Ce qui se passe quand elle ne peut pas payer une seconde complète de `use`. `stops` : elle ne réfléchit plus et reste sur place, ses tâches, cibles et comportements sont tous au repos jusqu'à ce qu'elle soit réapprovisionnée, mais elle tombe toujours et peut être poussée. `slows` : elle se déplace à demi-vitesse. `hurts` : elle subit 1 dégât par seconde, comme en cas de famine, donc `immuneTo` avec `starve` l'en protège |

**Fonctionner avec ce qu'elle transporte.** Un `use` sur le réservoir ou le tampon est un coût de fonctionnement : chaque seconde, l'entité y prélève cette quantité, indépendamment de `transfer`, de `buckets` et des filtres. Quand l'un des deux contient moins qu'une seconde complète de `use`, l'entité est à sec : plus rien n'est prélevé, `runsDry` décide de ce qui se passe, et tout redevient normal dès qu'on le remplit à nouveau. Seule une créature dépense ; sur une base qui n'est pas vivante, comme un wagonnet, `use` ne fait rien.

`items` :

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `filter` | non | liste d'entrées de filtre | aucun | Ce que les emplacements acceptent. Sans filtre, ils acceptent tout |

`fluid` :

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `capacity` | oui | entier, mB | aucun | Ce que contient le réservoir |
| `filter` | non | liste d'entrées de filtre | aucun | Les fluides que le réservoir accepte. Sans filtre, il accepte tout |
| `buckets` | non | booléen | `false` | Un clic droit avec un seau ou un autre récipient de fluide, sans s'accroupir, le vide dans le réservoir ou le remplit depuis le réservoir. Un clic qui ne déplace aucun fluide est laissé à l'entité |
| `use` | non | entier, mB par seconde | `0` | Ce que l'entité consomme dans le réservoir chaque seconde où elle est en vie. `0` ne coûte rien |

`energy` :

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `capacity` | oui | entier, FE | aucun | L'énergie qu'il contient |
| `transfer` | non | entier, FE | sans limite | La plus grande quantité d'énergie entrant ou sortant en une seule opération |
| `use` | non | entier, FE par seconde | `0` | L'énergie que l'entité consomme chaque seconde où elle est en vie. `0` ne coûte rien |

Une entrée de filtre. La première entrée qui correspond décide, et tout ce qu'aucune entrée ne couvre est refusé :

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `item` | l'un des trois | nom d'objet | aucun | Un objet, sous la forme `namespace:name` ou `namespace:name:meta` |
| `oreDict` | l'un des trois | nom de minerai | aucun | Tous les objets portant ce nom du dictionnaire des minerais |
| `fluid` | l'un des trois | nom de fluide | aucun | Un fluide désigné par son nom enregistré, comme `water`. Lu uniquement par un filtre de fluide |
| `max` | non | entier | `0` | La plus grande quantité détenue à la fois, comptée sur tous les emplacements, ou en mB pour un fluide. `0` signifie aucune limite |

### Fusées de Galacticraft

*variantes d'entités*

```json
{
  "entity": "galacticraftcore:rocket_t1",
  "name": "Supply Rocket",
  "egg": false,
  "trackingRange": 150,
  "trackingFrequency": 1,
  "galacticraft": {
    "tier": 2,
    "fuelTank": 2000,
    "cargoSlots": 36,
    "cargo": [
      { "item": "minecraft:iron_ingot", "max": 128 },
      { "oreDict": "ingotCopper" }
    ],
    "requiredPayload": [
      { "item": "minecraft:iron_ingot", "count": 64 }
    ],
    "payload": [
      { "item": "minecraft:iron_ingot", "count": 64 }
    ]
  }
}
```

L'objet qui la place, dans `<namespace>/items/supply_rocket.json` :

```json
{
  "type": "rocket",
  "rocket": "mypack:supply_rocket",
  "variants": {
    "supply_rocket": { "meta": 0 }
  }
}
```

Une variante de fusée de Galacticraft (`galacticraftcore:rocket_t1`, `galacticraftplanets:rocket_t2`, `galacticraftplanets:rocket_t3`) lit un objet `galacticraft`. Sur toute autre entité, l'objet est ignoré, et le journal le signale. La fusée se place sur une rampe de lancement avec un objet de type `rocket`. Casser la fusée, ou atterrir avec elle sur un autre corps céleste, rend cet objet avec la cargaison et le carburant qu'il contient.

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `tier` | non | entier | celui de la fusée de base | Le niveau de la fusée, qui détermine les corps qu'elle atteint |
| `fuelTank` | non | entier | celui de la fusée de base | Taille du réservoir de carburant. Une fusée de niveau 1 a `1000` |
| `cargoSlots` | non | `0`, `27`, `36`, `54` | `0` | Les tailles de cargaison de Galacticraft. `27` donne 18 emplacements, comme sur les fusées de Galacticraft elles-mêmes |
| `cargo` | non | liste d'entrées de filtre | aucun | Ce que les emplacements de cargaison et un chargeur de cargaison peuvent accepter. Sans filtre, ils acceptent tout. Les entrées sont celles de `storage`, avec `item` ou `oreDict` |
| `requiredPayload` | non | liste | aucun | Ce qui doit se trouver dans la cargaison avant le lancement de la fusée. Chaque entrée est `item` ou `oreDict` et un `count`, `1` par défaut |
| `payload` | non | liste | aucun | Ce que porte un objet de fusée neuf, chargé dans la cargaison lors de son premier placement. Chaque entrée est `item` et un `count`, `1` par défaut |

## Expositions

*créatures et dangers*

`<namespace>/exposures/*.json`

Le chemin du fichier est le nom du danger, et son message de mort provient de la clé de langue `death.attack.rdpl.<file name>`.

Un danger défini par le pack : des blocs, des objets et des dimensions nommés exposent les joueurs qui se tiennent près de ces blocs, qui portent ces objets ou qui séjournent dans ces dimensions, par niveaux, chaque niveau appliquant des effets et des dégâts périodiques. Un danger peut aussi être contracté auprès de mobs et de joueurs proches, ou tomber avec la pluie ([Contagion et météo](#contagion-et-météo)). Un fichier définit un danger ; plusieurs coexistent. Les valeurs par défaut de chaque clé sont les nombres qu'utilise la radioactivité d'Immersive World.

```json
{
  "blocks": [ "mypack:nuclear_waste=2", "mypack:uranium_ore" ],
  "items": [ "mypack:nuclear_waste" ],
  "dimensions": [ "-1" ],
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
| `blocks` | l'un des cinq | liste de `block` ou `block=level` | | Blocs qui exposent un joueur se tenant près d'eux. Sans niveau, c'est 1 |
| `items` | l'un des cinq | liste de `item` ou `item=level` | | Objets qui exposent un joueur qui les porte ou les revêt |
| `dimensions` | l'un des cinq | liste de `dim` ou `dim=level` | | Identifiants numériques de dimensions qui exposent tout joueur s'y trouvant |
| `levels` | oui | liste de niveaux | | L'échelle de gravité, la première entrée est le niveau 1. Un joueur reçoit le plus haut niveau qu'atteint une source |
| `immunity` | non | nom de potion | aucun | Un effet dont le porteur n'est pas du tout exposé |
| `scanInterval` | non | ticks | `20` | Fréquence de vérification de l'environnement et de l'inventaire |
| `range` | non | blocs | `10` | Portée de l'exposition d'un bloc, sous forme de sphère |
| `sourcesForNextLevel` | non | entier | `0` | Ce nombre de sources proches d'un même niveau le fait monter d'un niveau. `0` désactive cela |
| `skipsCreative` | non | booléen | `true` | Les joueurs en créatif et en spectateur sont épargnés |

### Niveaux

*expositions*

Chaque niveau :

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `effect` | oui | nom de potion | | L'effet qui marque le niveau sur le joueur. Sa présence entraîne les dégâts, donc il doit être un effet que le pack définit pour cet usage |
| `damage` | non | demi-cœurs | `0` | Dégâts infligés tous les `damageInterval` ticks tant que le niveau tient. Ils ignorent l'armure |
| `damageInterval` | non | ticks | `160` | Fréquence de ces dégâts |
| `effects` | non | liste d'effets | aucun | Effets supplémentaires appliqués en même temps, de la même forme que ceux des types de potions. Sans `duration`, ils suivent la fenêtre d'analyse |

Les effets du niveau durent un peu au-delà de l'analyse suivante, si bien qu'en s'éloignant on les laisse s'éteindre d'eux-mêmes. La mort par dégâts d'exposition lit son message dans `death.attack.rdpl.<file name>`, que fournissent les fichiers de langue du pack.

### Contagion et météo

*expositions*

Deux sources de plus, écrites dans le même fichier. Les porteurs et les sujets exposés transmettent le danger aux receveurs autour d'eux, et la pluie ou l'orage exposent les joueurs sur lesquels ils tombent.

```json
{
  "carriers": [ "minecraft:zombie_villager=2" ],
  "contagious": true,
  "catchers": [ "minecraft:player", "minecraft:villager" ],
  "contagionRange": 4,
  "contagionChance": 0.1,
  "contagionDuration": 1200,
  "weather": [ "rain", "thunder=2" ],
  "weatherDimensions": [ "0" ],
  "levels": [ { "effect": "mypack:sickness_1", "damage": 1.0 }, { "effect": "mypack:sickness_2", "damage": 2.0, "damageInterval": 80 } ]
}
```

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `carriers` | l'un des cinq | liste de `entity` ou `entity=level` | | Mobs, ou `minecraft:player`, qui transmettent toujours le danger à ce niveau. Sans niveau, c'est 1 |
| `contagious` | non | booléen | `false` | Toute personne exposée transmet le danger au niveau qu'elle détient |
| `catchers` | non | liste de noms d'entités | `minecraft:player` | Qui peut le contracter. Un mob ne le contracte qu'auprès de porteurs et de sujets exposés, jamais auprès de blocs, d'objets ou de la météo |
| `contagionRange` | non | blocs | `4` | Portée d'un porteur ou d'un sujet exposé, sous forme de sphère |
| `contagionChance` | non | `0` à `1` | `0.1` | La probabilité, à chaque analyse du porteur ou du sujet exposé, que chaque receveur à portée le contracte |
| `contagionDuration` | non | ticks | `1200` | Durée pendant laquelle un danger contracté conserve le niveau attrapé. Le contracter de nouveau remet le compteur à zéro |
| `weather` | l'un des cinq | liste de `kind` ou `kind=level` | | `rain` expose un joueur sur lequel tombe la pluie : ciel dégagé au-dessus de lui, dans un biome où il pleut. `thunder` compte pendant un orage |
| `weatherDimensions` | non | liste de `dim` | toutes les dimensions | Identifiants numériques de dimensions où la météo expose |

Un danger contracté compte comme une source de plus dans l'analyse, le plus haut niveau l'emportant comme pour toute autre, et `immunity` en protège aussi. Rien ne se propage tant que `contagionRange` et `contagionChance` ne sont pas supérieurs à `0` et que le fichier ne nomme pas `carriers` ou ne définit pas `contagious`, et les mobs ne sont examinés que lorsqu'un fichier le demande.

---

# Le monde

## Modèles de monde

*le monde*

`<namespace>/worldtemplates/*.json`

Le chemin du fichier est le nom du modèle, que l'option de configuration `worldTemplate` peut nommer pour le choisir d'office.

Rassemble la forme d'un monde dans un seul fichier, pour qu'un pack livre un monde entier d'un coup au lieu de demander au joueur de régler une douzaine d'options de configuration.

```json
{
  "name": "Ruby World",
  "default": "void",
  "dimensions": [0],
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
| `default` | non | nom de biome ou `void` | `void` | Ce qui comble un biome supprimé par le blocage |
| `roles` | non | objet de rôle vers biome | aucun | Biomes qui remplissent des rôles particuliers, comme l'océan ou la rivière |
| `structures` | non | objet de [nom de structure](#listes-de-valeurs) vers booléen | aucun | Structures vanilla activées ou désactivées |
| `settings` | non | objet | aucun | Valeurs de configuration que le modèle définit |
| `dimensions` | non | liste d'entiers | toutes les dimensions | Les dimensions auxquelles il s'applique |

`settings` utilise les mêmes noms de clés que la configuration, il n'y a donc aucune table de correspondance à apprendre.

Le modèle actif est déterminé par l'option de configuration `worldTemplate`. Laissée sur `auto`, le pack de plus haute priorité qui en livre un l'emporte, selon le même ordre que tout le reste. Nommer un modèle à cet endroit le choisit d'office.

## Règles de jeu

*le monde*

`<namespace>/gamerules/*.json`

Le nom du fichier est libre, seul le dossier est lu, et plusieurs fichiers se cumulent.

```json
{
  "0": {
    "doFireTick": "false",
    "keepInventory": "true",
    "randomTickSpeed": "3"
  },
  "-1": {
    "doFireTick": "true"
  }
}
```

Chaque clé est l'identifiant du monde auquel les règles appartiennent : `0` pour l'Overworld, `-1` pour le Nether, `1` pour l'End, et ce qu'un mod utilise pour les siens. Les valeurs sont des chaînes, comme dans la commande `/gamerule`, donc `"false"` plutôt que `false`. Elles s'appliquent aux nouveaux mondes. Un fichier de dimension porte les mêmes règles dans un bloc `gameRules`, qui ne s'applique alors qu'à ce monde.

## Biomes

*le monde*

`<namespace>/biomes/*.json`

Le chemin du fichier est le nom de registre du biome : `mypack/biomes/ruby_forest.json` enregistre donc `mypack:ruby_forest`. `name` n'est que ce qui est montré au joueur.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin.

```json
{
  "name": "Ruby Forest",
  "id": 200,
  "types": ["FOREST", "DENSE", "WET"],
  "temperature": 0.7,
  "rainfall": 0.8,
  "rain": true,
  "snow": false,
  "baseHeight": 0.15,
  "heightVariation": 0.25,
  "topBlock": "mypack:ruby_grass",
  "fillerBlock": "minecraft:dirt",
  "stoneBlock": "mypack:ruby_stone",
  "baseBiome": "minecraft:forest",
  "waterColor": "8040A0",
  "grassColor": "6BA33C",
  "foliageColor": "4E8B2A",
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
    "villageSpawn": true,
    "strongholds": false,
    "playerSpawn": true
  },
  "villageType": "oak",
  "minHeight": 100,
  "maxHeight": 156,
  "replaces": ["minecraft:plains", "minecraft:forest"],
  "skyStone": "minecraft:end_stone",
  "skyIslands": 0.2,
  "skyThickness": 2.0,
  "requires": ["mypack"]
}
```

### Le biome

*biomes*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `name` | non | chaîne | le nom du fichier | Nom montré au joueur |
| `id` | non | entier | attribué pour vous | Identifiant de biome fixe. À définir seulement si vous avez besoin qu'il soit stable |
| `types` | non | liste de types du dictionnaire | deviné | Enregistre le biome sous ces types, comme `FOREST`, `COLD`, `WET` ou `NETHER`, pour que d'autres mods le trouvent. S'ils sont omis, Forge les devine au mieux d'après le nombre d'arbres, l'altitude, la température, les précipitations et le bloc du sol du biome |
| `baseBiome` | non | nom de biome | aucun | Un biome existant dont on copie les réglages |
| `requires` | non | liste d'identifiants de mods ou d'espaces de noms de packs | aucun | Le fichier est ignoré à moins que tous soient présents |

### Climat

*biomes*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `temperature` | non | flottant | `0.5` | En dessous de 0.15, il neige ; au-dessus de 1.0, la chaleur est celle du désert |
| `rainfall` | non | flottant, 0 à 1 | `0.5` | Son humidité |
| `rain` | non | booléen | `true` | Si la météo se manifeste ou non |
| `snow` | non | booléen | `false` | Si la pluie tombe sous forme de neige |

### Sol et couleurs

*biomes*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `baseHeight` | non | flottant | `0.1` | Altitude du terrain. Le niveau de la mer est 0, les plaines 0.125 |
| `heightVariation` | non | flottant | `0.2` | Son degré de relief |
| `topBlock` | non | nom de bloc | herbe | Le bloc de surface |
| `fillerBlock` | non | nom de bloc | terre | Juste sous la surface |
| `stoneBlock` | non | nom de bloc | pierre | L'essentiel du sol |
| `waterColor` | non | couleur hexadécimale | `FFFFFF` | Teinte de l'eau |
| `grassColor` | non | couleur hexadécimale | selon le climat | Teinte de l'herbe, à la place de la couleur que donneraient la température et les précipitations |
| `foliageColor` | non | couleur hexadécimale | selon le climat | Teinte des feuilles, de la même façon |

### Décoration et apparitions

*biomes*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `decoration` | non | objet | quantités vanilla | Quantités par chunk. Les noms lus sont `trees`, `flowers`, `grass`, `deadbush`, `mushrooms`, `bigmushrooms`, `reeds`, `cacti`, `sand`, `gravel`, `clay` et `waterlily`, plus `falls`, où une valeur supérieure à zéro signifie que des lacs et des sources se génèrent, et `extratreechance`, un pourcentage de chance d'avoir un arbre de plus. Tout autre nom est consigné dans le journal et ignoré |
| `spawns` | non | liste d'objets | liste vanilla | Voir ci-dessous |
| `keepDefaultSpawns` | non | booléen | `false` | Conserve la liste vanilla à côté de la vôtre |
| `spawnChance` | non | flottant, inférieur à 1 | `0.1` | La probabilité qu'un autre troupeau soit placé lors de la première création du terrain. Le jeu continue de lancer les dés tant que cela réussit, donc 1 ne s'arrête jamais et remplit le monde jusqu'à manquer de place. Toute valeur supérieure ou égale à 0.99 est refusée et 0.99 est utilisé |
| `spawnRates` | non | objet de `surfaceDay`, `surfaceNight`, `undergroundDay`, `undergroundNight` vers un multiplicateur | aucun | Fréquence d'apparition des mobs hostiles ici, à la place des réglages globaux. Voir ci-dessous |

Une entrée d'apparition accepte `entity` (obligatoire), `type` (`creature`, l'un des [types de créatures](#listes-de-valeurs)), `weight` (`10`), `min` (`1`) et `max` (`min`).

`spawnRates` ne concerne que les mobs hostiles, et rien d'autre. Il accepte quatre clés et aucune autre : `surfaceDay` et `surfaceNight` pour les endroits d'où l'on voit le ciel, `undergroundDay` et `undergroundNight` pour ceux d'où on ne le voit pas. Chacune est un multiplicateur de la fréquence à laquelle un mob hostile peut apparaître : `1` est le taux ordinaire, `0` les arrête complètement, une valeur inférieure à 1 refuse une part des tentatives, et une valeur supérieure à 1 laisse passer des tentatives que le jeu aurait autrement refusées, donc `2` en donne deux fois plus. Une clé omise signifie que le biome ne décide pas, et c'est le réglage global pour ce moment et ce lieu qui s'applique. Tout autre élément écrit ici n'est pas une clé et est ignoré, de sorte qu'un taux nommé d'après un type de créature ne fait absolument rien.

### Où il se génère

*biomes*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `placement` | non | objet | aucun | Où il se génère. Voir ci-dessous |
| `villageType` | non | `oak`, `sandstone`, `acacia` ou `spruce` | aucun | Avec quoi est construit un village situé ici. Vide, il se construit en chêne, comme sans la clé |

`placement` :

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `climate` | non | chaîne | aucun | Le groupe climatique vanilla qu'il rejoint |
| `weight` | non | entier | `10` | Sa fréquence de choix face à ses voisins |
| `villages` | non | booléen | `false` | Des villages peuvent s'y générer |
| `villageSpawn` | non | booléen | `true` | Des villageois peuvent y apparaître |
| `strongholds` | non | booléen | `false` | Des forteresses peuvent s'y générer |
| `playerSpawn` | non | booléen | `false` | Le point d'apparition du monde peut être placé ici |

### Bandes d'altitude et îles flottantes

*biomes*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `minHeight` | non | entier | aucun | Le y le plus bas où ce biome prend le relais en tant que biome 3D. Définir l'une ou l'autre altitude transforme le biome en bande : la colonne garde son propre biome en dehors, et à l'intérieur chaque cellule de 4 sur 4 sur 4 du monde indique celui-ci. Mondes rubic uniquement, et appliqué à mesure que le terrain est créé, donc le terrain existant garde ce qu'il avait |
| `maxHeight` | non | entier | aucun | Le y le plus haut de cette bande |
| `replaces` | non | liste de noms de biomes | tous les biomes | Restreint la bande aux colonnes dont le biome propre est nommé ici, de sorte qu'une bande alpine puisse recouvrir les montagnes et rien d'autre |
| `skyStone` | non | nom de bloc | le réglage du monde | Le bloc dont sont faites les îles flottantes sous leur surface là où ce biome s'applique. Sur une bande, `topBlock` et `fillerBlock` peignent la surface de l'île avec lui : une bande est donc le moyen de donner à une portion de ciel ses propres îles |
| `skyIslands` | non | flottant, `-1` à `1` | le réglage du monde | Le seuil des îles là où ce biome s'applique. Plus il est bas, plus il rassemble de terre |
| `skyThickness` | non | flottant, `0` ou plus | le réglage du monde | La solidité des îles là où ce biome s'applique |

### Température selon l'altitude

*biomes*

**Température selon l'altitude.** Un biome se refroidit en montant, ce qui met de la neige au sommet des montagnes et arrête la pluie au-dessus d'une certaine ligne. Trois clés `terrain` déplacent cette courbe, ce qui compte dans un monde rubic où le sol peut se trouver bien au-dessus ou au-dessous de l'altitude que le jeu suppose. Les valeurs par défaut sont ce que fait le jeu, donc un pack qui n'y touche pas ne change rien.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "biomeTemperatureCenterY": 64,
    "biomeTemperatureHeightFactor": -0.001667,
    "biomeTemperatureScaleMaxY": 256
  }
}
```

| Clé | Valeur | Défaut | Rôle |
| --- | --- | --- | --- |
| `biomeTemperatureCenterY` | entier | `64` | L'altitude à partir de laquelle la courbe est mesurée. À cette altitude ou en dessous, un biome indique sa propre `temperature` sans modification |
| `biomeTemperatureHeightFactor` | flottant | `-0.001667` | De combien la température évolue par bloc au-dessus de cette altitude, soit les 0.05 du jeu sur 30 blocs. Une valeur négative refroidit avec l'altitude, une valeur positive réchauffe |
| `biomeTemperatureScaleMaxY` | entier | `256` | L'altitude où la courbe s'arrête, pour qu'un monde plus haut que celui du jeu ne continue pas de se refroidir jusqu'à son plafond |

## Dimensions

*le monde*

`<namespace>/dimensions/*.json`

Le chemin du fichier nomme la dimension pour `suffix`, dont la valeur par défaut est `DIM_<name>`. La dimension elle-même est retrouvée par son `id`, c'est donc ce nombre auquel tout le reste se réfère.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin.

```json
{
  "id": 12,
  "suffix": "DIM_ruby",
  "keepLoaded": false,
  "requires": ["mypack"],
  "terrain": {
    "type": "overworld",
    "generatorOptions": "",
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
    "respawnDimension": 0,
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
    "rain": { "particle": "droplet", "sound": "minecraft:weather.rain", "volume": 0.2, "interval": 3, "color": "#88AAFF", "snowColor": "#FFFFFF", "angle": 30, "heading": 90 }
  },
  "gameRules": { "doMobSpawning": "false" }
}
```

### Niveau supérieur

*dimensions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `id` | oui | entier | | L'identifiant de la dimension. Il ne doit pas entrer en conflit avec celui d'un autre mod |
| `suffix` | non | chaîne | `DIM_<name>` | Le dossier de sauvegarde |
| `keepLoaded` | non | booléen | `false` | La garde chargée quand personne n'y est |
| `gameRules` | non | objet | aucun | Des règles qui ne s'appliquent qu'ici |
| `requires` | non | liste d'identifiants de mods ou d'espaces de noms de packs | aucun | Le fichier est ignoré à moins que tous soient présents |

### Le bloc `terrain`

*dimensions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `type` | non | `overworld`, `flat`, `void`, `nether`, `end` | `overworld` | Le générateur qui la construit |
| `generatorOptions` | non | chaîne | aucun | La chaîne du générateur, telle qu'un préréglage de monde superplat l'utilise |
| `structures` | non | booléen | `true` | Si les structures vanilla se génèrent |

### Le bloc `biomes`

*dimensions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `source` | non | `inherit`, `single` | `inherit` | `inherit` utilise la carte des biomes normale, `single` utilise un seul biome partout |
| `biome` | si `single` | nom de biome | `minecraft:plains` | Quel est ce biome |

### Le bloc `sky`

*dimensions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `hasSkyLight` | non | booléen | `true` | Si la lumière du jour y parvient |
| `surfaceWorld` | non | booléen | `true` | Si les cartes et les boussoles se comportent comme dans l'Overworld |
| `respawn` | non | booléen | `true` | Si les joueurs y réapparaissent |
| `respawnDimension` | non | entier | aucun | Où ils réapparaissent à la place |
| `spawning` | non | booléen | `true` | Si les mobs y apparaissent |
| `nether` | non | booléen | `false` | Traitée comme le Nether pour les portails et les plafonds |
| `beds` | non | booléen | `true` | Désactivé, les lits explosent |
| `waterVaporizes` | non | booléen | `false` | L'eau s'évapore |
| `cloudHeight` | non | entier | `128` | Où se situent les nuages |
| `cloudColor` | non | couleur hexadécimale | aucun | Teinte des nuages |
| `cloudSpeed` | non | flottant | `1.0` | Vitesse de dérive des nuages. `0` les immobilise, une valeur négative les fait aller en sens inverse |
| `cloudLayers` | non | liste d'objets | aucun | Plusieurs couches de nuages. Voir [Brouillard, lumière, nuages et chaleur](#brouillard-lumière-nuages-et-chaleur) |
| `groundLevel` | non | entier | `63` | Niveau de la mer, utilisé pour l'horizon et les recherches de point d'apparition |
| `movementFactor` | non | flottant | `1.0` | Rapport de distance avec l'Overworld. Le Nether utilise 8 |
| `fogColor` | non | couleur hexadécimale ou `sample` | aucun | Teinte du brouillard à midi. Il s'assombrit la nuit comme le brouillard vanilla. `sample` mélange le ciel avec le sol autour du joueur |
| `showFog` | non | booléen | `false` | Brouillard épais, comme dans le Nether |
| `fogDensity` | non | flottant, 0 à 1 | `0.0` | Épaisseur du brouillard. `0` conserve la distance vanilla, `1` la referme à 8 blocs |
| `fogGroundWeight` | non | flottant, 0 à 1 | `0.5` | Avec `fogColor: sample`, le poids du sol par rapport au ciel |
| `skyColor` | non | couleur hexadécimale | aucun | Teinte du ciel à midi. Il s'assombrit la nuit et grisonne sous la pluie et l'orage comme le ciel vanilla |
| `fixedTime` | non | entier, ticks | aucun | Verrouille l'heure de la journée |
| `sunriseColors` | non | booléen | `true` | Si le lever et le coucher du soleil sont teintés |
| `ambientLight` | non | flottant, 0 à 1 | `0.0` | Lumière minimale partout |
| `lightSkyColor` | non | couleur hexadécimale | aucun | Teinte de la lumière du jour sur les blocs et les mobs |
| `lightBlockColor` | non | couleur hexadécimale | aucun | Teinte de la lumière des torches et des autres sources de lumière de bloc |
| `skyFactor` | non | flottant, 0 à 1 | `1.0` | Luminosité apparente de la lumière du jour. Dessinée uniquement côté client, donc l'apparition des mobs ne change pas |
| `starBrightness` | non | flottant, 0 à 1 | aucun | Luminosité des étoiles |
| `sunBrightness` | non | flottant, 0 à 1 | `1.0` | Luminosité avec laquelle le soleil est dessiné |
| `moonBrightness` | non | flottant, 0 à 1 | `1.0` | Luminosité avec laquelle la lune est dessinée, et avec `bodies` tous les corps sauf le soleil |
| `heat` | non | objet | aucun | Un mirage de chaleur sur la vue. Voir [Brouillard, lumière, nuages et chaleur](#brouillard-lumière-nuages-et-chaleur) |
| `renderSky` | non | booléen | `true` | Désactivé, rien ne dessine le ciel, le soleil, la lune ni les étoiles, ne laissant que la couleur du brouillard |
| `renderClouds` | non | booléen | `true` | Désactivé, aucun nuage n'est dessiné |
| `renderWeather` | non | booléen | `true` | Désactivé, ni pluie ni neige ne sont dessinées |
| `sun` | non | objet | aucun | Votre propre soleil. Voir [Le moteur de rendu du ciel](#le-moteur-de-rendu-du-ciel) |
| `bodies` | non | liste d'objets | aucun | Planètes et lunes suspendues dans le ciel. Voir [Le moteur de rendu du ciel](#le-moteur-de-rendu-du-ciel) |
| `stars` | non | objet | aucun | Votre propre champ d'étoiles. Voir [Le moteur de rendu du ciel](#le-moteur-de-rendu-du-ciel) |

### Le moteur de rendu du ciel

*dimensions*

Définir l'un des éléments `sun`, `bodies` ou `stars` remplace le ciel vanilla par celui de RDPL, qui dessine la même voûte, la même lueur de l'aube et le même vide que le ciel vanilla, mais prend le soleil, les autres corps et les étoiles dans le pack. Il est dessiné uniquement côté client, et un serveur dédié ne le charge jamais. `renderSky: false` l'emporte toujours et ne dessine rien, et `renderClouds: false` est le moyen d'obtenir un ciel sans nuages.

Sans `bodies`, la lune vanilla et ses phases restent. Avec `bodies`, la liste contient tout ce qui n'est pas le soleil : une liste vide donne donc un ciel sans lune.

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `sun.texture` | non | chemin de texture | le soleil vanilla | L'image du soleil |
| `sun.size` | non | flottant | `30` | La moitié de la largeur du soleil à une distance de 100. `0` le masque |
| `bodies[].texture` | oui | chemin de texture | | L'image du corps |
| `bodies[].size` | non | flottant | `20` | La moitié de sa largeur à une distance de 100. La lune vanilla vaut `20` |
| `bodies[].angle` | non | flottant, degrés | `180` | Sa distance le long de la trajectoire du soleil, derrière celui-ci. `180` est l'endroit où se trouve la lune vanilla. Avec `followsTime` désactivé, elle se mesure depuis le point juste au-dessus de la tête, donc `0` est le zénith et `90` l'horizon |
| `bodies[].tilt` | non | flottant, degrés | `0` | De combien il s'écarte de la trajectoire du soleil, vers le nord ou le sud |
| `bodies[].followsTime` | non | booléen | `true` | Désactivé, il reste immobile dans le ciel au lieu de tourner avec le soleil |
| `stars.count` | non | entier | `1500` | Le nombre d'étoiles |
| `stars.size` | non | flottant | `0.15` | La plus petite étoile ; la plus grande est encore deux tiers plus grosse |

### Brouillard, lumière, nuages et chaleur

*dimensions*

Ces clés se placent dans le bloc `sky` à côté des plus anciennes, qui continuent de fonctionner comme avant. Elles sont toutes dessinées uniquement côté client : un serveur dédié les ignore et aucune sauvegarde ne change.

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

`fogColor: "sample"` lit les blocs du dessus dans un carré de 33 sur 33 blocs autour du joueur une fois par seconde, éclaire leurs couleurs de carte selon l'heure de la journée et les mélange avec la couleur du ciel. Le brouillard évolue progressivement vers chaque nouvel échantillon. `fogDensity` fonctionne avec une couleur de brouillard échantillonnée, une couleur fixe ou aucune. Sous l'eau, dans la lave et sous l'effet de cécité, le brouillard vanilla reste en place.

`lightSkyColor` et `lightBlockColor` teintent la carte de lumière, si bien que chaque bloc éclairé et chaque mob prend la teinte. `skyFactor` modifie la luminosité apparente de la lumière du jour, tandis que le niveau de lumière que le serveur compte pour l'apparition et les cultures reste le même.

Sans `cloudLayers`, `cloudSpeed` modifie la vitesse de l'unique couche vanilla à `cloudHeight`. Avec `cloudLayers`, chaque entrée est une couche à part entière, et `cloudHeight`, `cloudSpeed` et `cloudColor` complètent ce qu'une entrée omet. `renderClouds: false` n'en dessine toujours aucune.

`sunBrightness` et `moonBrightness` estompent le soleil et la lune en plus de l'atténuation vanilla due à la pluie, dans le ciel vanilla comme dans le vôtre issu de [Le moteur de rendu du ciel](#le-moteur-de-rendu-du-ciel).

`heat` étend un mirage ondulant sur la vue tant que le joueur se tient dans un biome au moins aussi chaud que `minTemperature`. Un désert vaut 2.0 et les plaines 0.8. Le mirage apparaît et disparaît progressivement en quelques secondes et reste absent sous l'eau. Il demande la prise en charge des shaders par la carte graphique et reste désactivé tant qu'un autre shader plein écran, comme la vue spectateur, est actif.

`mode` décide où le mirage se pose. `screen` déforme une bande fixe dans le bas de l'écran, où que le joueur regarde. `world` suit le terrain : ce qui se trouve à moins de `startDistance` blocs reste net, le mirage s'intensifie vers le bord lointain de la distance d'affichage, là où le brouillard se referme, et le ciel n'est jamais touché, que le joueur regarde vers le bas, droit devant ou vers le haut.

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `cloudLayers[].height` | non | flottant | `cloudHeight` | Où se situe la couche |
| `cloudLayers[].speed` | non | flottant | `cloudSpeed` | Sa vitesse de dérive. `0` l'immobilise, une valeur négative l'inverse |
| `cloudLayers[].color` | non | couleur hexadécimale | `cloudColor` | Sa teinte |
| `heat.strength` | non | flottant, 0 à 1 | `0.1` | L'intensité du mirage |
| `heat.minTemperature` | non | flottant | `1.5` | La température de biome la plus basse qui produit un mirage |
| `heat.dayOnly` | non | booléen | `true` | Activé, le mirage s'estompe avec la lumière du jour et disparaît la nuit |
| `heat.mode` | non | chaîne | `screen` | Où le mirage se pose, `screen` ou `world` |
| `heat.startDistance` | non | flottant | `32` | En mode `world`, à combien de blocs de distance le mirage commence |

### Le bloc `physics`

*dimensions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `gravity` | non | flottant, supérieur à 0 | `1.0` | Accélération de chute ici, en multiplicateur de la valeur vanilla. `0.17` correspond à la Lune |
| `fallDamage` | non | flottant, supérieur à 0 | `1.0` | Dégâts de chute ici, en multiplicateur |
| `arrowGravity` | non | flottant, supérieur à 0 | suit `gravity` | Vitesse de chute des flèches ici, en multiplicateur |

Ce sont les mêmes multiplicateurs que dans [Physique du monde](#physique-du-monde), définis sur la dimension. Une ligne `dimension=value` d'un modèle de monde pour cette dimension l'emporte toujours ; une valeur de modèle de monde sans dimension ne couvre que les dimensions qui ne définissent rien par elles-mêmes. Sur un [corps céleste de Galacticraft](#corps-célestes-de-galacticraft), c'est Galacticraft qui les applique.

### Le bloc `time`

*dimensions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `dayLength` | non | entier, ticks | `24000` | Durée d'un jour et d'une nuit ici. La phase de la lune fait toujours un tour tous les 24000 ticks |

### Le bloc `weather`

*dimensions*

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `precipitation` | non | booléen | `true` | Désactivé, il n'y pleut, neige ou orage jamais |
| `lightning` | non | booléen | `true` | Désactivé, la pluie et les orages viennent sans foudre |
| `snow` | non | booléen | `true` | Désactivé, la neige ne s'accumule jamais |
| `freeze` | non | booléen | `true` | Désactivé, l'eau ne gèle jamais |
| `cycle.rainTicks` | non | entier ou `[min, max]` | `[1000, 4600]` | Durée d'une averse |
| `cycle.clearTicks` | non | entier ou `[min, max]` | `[1000, 3000]` | Durée de l'accalmie entre deux averses |
| `cycle.maxStrength` | non | flottant, supérieur à 0 jusqu'à 1 | `0.6` | L'intensité maximale d'une averse. Chaque averse évolue entre un quart de cette valeur et sa totalité |
| `cycle.thunderTicks` | non | entier ou `[min, max]` | aucun | Durée d'un orage. Sans cette clé, le cycle ne produit jamais d'orage |
| `cycle.calmTicks` | non | entier ou `[min, max]` | `[12000, 180000]` | Durée du calme entre deux orages |
| `cycle.thunderStrength` | non | flottant, supérieur à 0 jusqu'à 1 | `1` | À quel point un orage s'assombrit. La foudre ne frappe qu'au-dessus de `0.9` |
| `rain.particle` | non | nom de particule | `droplet` | Ce qui éclabousse là où la pluie tombe |
| `rain.sound` | non | nom de son | `minecraft:weather.rain` | Le son de la pluie |
| `rain.volume` | non | flottant | `0.2` | Son volume, réduit de moitié quand la pluie tombe au-dessus de vous |
| `rain.interval` | non | entier | `3` | Rareté du son ; plus la valeur est élevée, plus il est espacé, `0` le joue à chaque occasion |
| `rain.color` | non | couleur hexadécimale | `#FFFFFF` | Teinte de la pluie qui tombe |
| `rain.snowColor` | non | couleur hexadécimale | `#FFFFFF` | Teinte de la neige qui tombe |
| `rain.angle` | non | flottant, 0 à 180 | `0` | Degrés par rapport à la verticale descendante : `90` souffle à l'horizontale, `180` monte tout droit. Elle est dessinée inclinée de 75 degrés au plus |
| `rain.heading` | non | flottant, degrés | `0` | Direction du souffle : `0` sud, `90` ouest, `180` nord, `270` est |

Les autres dimensions partagent la pluie de l'Overworld. Un `cycle` donne à celle-ci une météo propre : les averses vont et viennent selon les durées ci-dessus, quoi que fasse l'Overworld. Avec `thunderTicks`, elle connaît aussi des orages, selon ses propres durées ; un orage qui rencontre une averse porte l'averse à pleine intensité, assombrit le ciel et, avec `lightning` activé, amène la foudre. `weatherCeiling` dans un [modèle de monde](#modèles-de-monde) plafonne toujours l'altitude que la pluie atteint.

Un bloc `rain` change l'aspect et le son de la pluie et de la neige ici, avec ou sans `cycle` ; sans lui, ils ont l'aspect et le son vanilla. Il fonctionne de la même façon sur une planète ou une lune de Galacticraft.

## Corps célestes de Galacticraft

*le monde*

`<namespace>/celestial/*.json` et le bloc `galacticraft` de `<namespace>/dimensions/*.json`

Avec Galacticraft installé, un pack peut placer ses propres systèmes stellaires, planètes, lunes, ceintures d'astéroïdes et stations spatiales sur la carte stellaire de Galacticraft, et faire d'une dimension de pack un lieu où une fusée peut se rendre. Sans Galacticraft, tout cela est ignoré : les fichiers `celestial/` sont ignorés et une dimension avec un bloc `galacticraft` n'est pas enregistrée, et le journal le signale.

Le nom d'un corps sur la carte provient du fichier de langue du pack, sous la clé que Galacticraft utilise : `solarsystem.<name>`, `star.<name>`, `planet.<name>`, `moon.<name>` ou, pour une station, `satellite.<name>`. Une ceinture d'astéroïdes prend `planet.<name>` autour d'une étoile et `moon.<name>` autour d'une planète.

### Systèmes stellaires et corps visibles uniquement sur la carte

*corps célestes de Galacticraft*

`<namespace>/celestial/*.json`

Un fichier ici est soit un système stellaire, soit une planète ou une lune qui figure sur la carte sans lieu où atterrir.

```json
{
  "kind": "system",
  "name": "ember",
  "galaxy": "milky_way",
  "mapPosition": [0.9, -0.5],
  "star": {
    "name": "ember",
    "icon": "galacticraftcore:textures/gui/celestialbodies/sun.png",
    "relativeSize": 1.2
  }
}
```

```json
{
  "kind": "planet",
  "name": "ash",
  "parent": "ember",
  "icon": "galacticraftcore:textures/gui/celestialbodies/mercury.png",
  "relativeSize": 0.5,
  "distance": 1.4,
  "orbitTime": 2.5,
  "tier": 3
}
```

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `kind` | oui | `system`, `planet`, `moon` | | Ce que crée le fichier |
| `galaxy` | non | chaîne | `milky_way` | La galaxie d'un système |
| `mapPosition` | pour un système | `[x, y]` ou `[x, y, z]` | | Où le système se situe sur la carte de la galaxie |
| `star` | non | objet de [clés de carte](#clés-de-carte) | | L'étoile du système, dessinée en son centre |
| `requires` | non | liste d'identifiants de mods ou d'espaces de noms de packs | aucun | Le fichier est ignoré à moins que tous soient présents |

Une planète ou une lune ici accepte les [clés de carte](#clés-de-carte) avec un `tier` par défaut de `0`. Les planètes et les lunes sont placées sur la carte après tous les systèmes, si bien qu'une planète peut orbiter autour d'un système du même pack.

### Clés de carte

*corps célestes de Galacticraft*

Chaque corps, qu'il s'agisse d'un fichier `celestial/`, de l'`star` d'un système ou du bloc `galacticraft` d'une dimension, se place sur la carte avec ces clés.

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `name` | non | chaîne | le nom du fichier | Le nom du corps, que sa clé de langue et chaque `parent` utilisent |
| `parent` | pour une lune ou une station | nom | `sol` pour une planète ou une ceinture | Le système qu'orbite une planète, la planète qu'orbite une lune, la planète ou la lune qu'orbite une station, ou un système ou une planète pour une ceinture d'astéroïdes |
| `icon` | non | chemin de texture | l'icône de Mars, de la Lune, du Soleil, d'astéroïde ou de station de Galacticraft | L'image sur la carte |
| `relativeSize` | non | flottant | `1.0`, une lune ou une station `0.2667` | Sa taille sur la carte |
| `distance` | non | flottant | `1.0`, une lune `13`, une station `9` | À quelle distance il orbite |
| `scaledDistance` | non | flottant | `distance` | La distance utilisée sur la carte agrandie |
| `orbitTime` | non | flottant, années | `1.0`, une lune `100`, une station `20` | Durée d'une orbite sur la carte. Une valeur négative tourne à l'envers |
| `phaseShift` | non | flottant, radians | `0`, une station espacée | Où il commence sur son orbite. Une station sans cette clé commence 2.4 radians après la dernière station autour de la même planète, stations de ses lunes comprises, de sorte que deux stations ne partagent jamais le même point |
| `ringColor` | non | couleur hexadécimale | `19E599` | La ligne d'orbite, là où la carte en dessine une |
| `tier` | non | entier | `1` pour une dimension, celui de son parent pour une station, `0` sinon | Le niveau de fusée dont la carte indique qu'il est nécessaire. Avec GalaxySpace, la carte d'AsmodeusCore calcule plutôt le niveau d'après la distance, et un corps autour d'une autre étoile exige le niveau maximal, sauf si `enableNewTierSystem` est désactivé dans `config/AsmodeusCore/core.conf` |

### Le bloc `galacticraft`

*corps célestes de Galacticraft*

`<namespace>/dimensions/*.json`

Un bloc `galacticraft` dans un fichier de dimension fait de cette dimension une planète ou une lune à part entière, ou une [ceinture d'astéroïdes](#ceintures-dastéroïdes) ou une [station spatiale](#stations-spatiales). Tout ce qui est hors du bloc, le ciel, la physique, le temps et la météo, reste celui de la dimension et fonctionne de la même façon avec ou sans Galacticraft ; le bloc ne contient que ce que Galacticraft lit.

```json
{
  "id": 71,
  "sky": { "skyColor": "3A1A10", "sun": { "size": 18 } },
  "physics": { "gravity": 0.4, "fallDamage": 0.5 },
  "weather": {
    "cycle": { "maxStrength": 0.5 },
    "rain": { "particle": "smoke", "sound": "minecraft:block.lava.extinguish", "volume": 0.04, "interval": 20 }
  },
  "galacticraft": {
    "kind": "planet",
    "name": "cinder",
    "parent": "ember",
    "distance": 0.75,
    "tier": 2,
    "minTier": 2,
    "landing": "balloons",
    "landingHeight": 700,
    "arrival": "departure",
    "exitHeight": 1000,
    "rocketGui": "mypack:textures/gui/cinder_rocket_gui.png",
    "checklist": ["equip_oxygen_suit", "thermal_padding"],
    "atmosphere": {
      "gases": ["CO2", "NITROGEN"],
      "breathable": false,
      "corrosive": true,
      "temperature": 3.0,
      "wind": 0.3,
      "density": 0.4
    },
    "meteorFrequency": 10,
    "fuelMultiplier": 0.9,
    "soundReduction": 2.5,
    "solarEnergy": 1.6,
    "netherPortals": false,
    "dungeon": { "spacing": 704, "chest": "mypack:chests/cinder_dungeon" }
  }
}
```

Le bloc accepte les [clés de carte](#clés-de-carte), plus :

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `kind` | non | `planet`, `moon`, `asteroids`, `station` | `planet` | Ce qu'est la dimension |
| `reachable` | non | booléen | `true` | Désactivé, elle figure sur la carte mais aucune fusée n'y va |
| `minTier` | non | entier | `tier` | Le niveau de fusée le plus bas qui peut atterrir |
| `landing` | non | `lander`, `parachute`, `balloons` | `lander` | Comment un joueur descend. `balloons` demande Galacticraft Planets et devient un module d'atterrissage sans lui. `disableLander` de Galacticraft force `parachute` |
| `landingHeight` | non | flottant | `900`, un parachute `250` | L'altitude à laquelle un joueur arrive |
| `arrival` | non | `departure`, `spawn` | `departure` | Atterrir au-dessus de l'endroit où la fusée a décollé, ou au-dessus du point d'apparition de cette dimension |
| `exitHeight` | non | flottant | `1200` | L'altitude à laquelle une fusée quittant cette dimension la quitte |
| `rocketGui` | non | chemin de texture | celui de l'Overworld de Galacticraft | L'écran de vol |
| `checklist` | non | liste de chaînes | aucun | Clés de liste de contrôle de Galacticraft affichées avant le lancement |
| `meteorFrequency` | non | flottant | d'après `density` | Rareté des chutes de météorites, près de chaque joueur environ une fois toutes les tant de fois 750 ticks. `0` les arrête |
| `fuelMultiplier` | non | flottant | `1.0` | Carburant que brûle une fusée qui part d'ici |
| `soundReduction` | non | flottant | d'après `density` | De combien le son est plus faible dans cet air |
| `solarEnergy` | non | flottant | `1.0` | Production des panneaux solaires ici |
| `netherPortals` | non | booléen | `false` | Si les portails du Nether s'allument ici |

| Clé de `atmosphere` | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `gases` | non | liste de `NITROGEN`, `OXYGEN`, `CO2`, `WATER`, `METHANE`, `HYDROGEN`, `HELIUM`, `ARGON` | aucun | L'air. Aucun gaz signifie le vide, et le feu a besoin de `OXYGEN` |
| `breathable` | non | booléen | de l'oxygène et pas de CO2 | Si les joueurs respirent sans combinaison |
| `corrosive` | non | booléen | `false` | Ronge l'armure sans contrôleur de bouclier |
| `temperature` | non | flottant | `0` | Niveau thermique de Galacticraft. En dessous de 0, il fait froid, au-dessus, chaud ; le rembourrage thermique y répond |
| `wind` | non | flottant | `1.0` avec des gaz, `0` sans | Agite les drapeaux et alimente l'énergie éolienne |
| `density` | non | flottant | `1.0` | L'épaisseur de l'air. Elle détermine les valeurs par défaut des météorites et du son |

| Clé de `dungeon` | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `spacing` | non | entier, blocs | `0` | Distance entre les donjons de Galacticraft. `0` signifie aucun |
| `chest` | non | table de butin | aucun | Le butin de leurs coffres |

Galacticraft ne construit ses donjons que dans son propre terrain, donc `dungeon` n'a d'effet que là où quelque chose exécute le générateur de donjons de Galacticraft ; le terrain de RDPL ne le fait pas.

L'aspect et le son de la pluie sur une planète relèvent du bloc [`weather.rain`](#le-bloc-weather) de la dimension elle-même, comme dans l'exemple ci-dessus.

**Qui enregistre la dimension.** La dimension d'un corps accessible est enregistrée par Galacticraft, de sorte que les fusées et les clients multijoueurs la voient ; une dimension avec `reachable: false` est enregistrée par RDPL. Si le corps ne peut pas être placé, parce que son parent est inconnu ou que son nom est pris, la dimension n'est pas enregistrée, et le journal en donne la raison.

### Ceintures d'astéroïdes

*corps célestes de Galacticraft*

`<namespace>/dimensions/*.json`

Une dimension dont le bloc `galacticraft` contient `kind: "asteroids"` est une ceinture d'astéroïdes : le propre champ d'astéroïdes de Galacticraft, généré dans un vide. Elle demande Galacticraft Planets et un `terrain` de type `void` ; sans l'un ou l'autre, la dimension n'est pas enregistrée, et le journal en donne la raison.

```json
{
  "id": 73,
  "terrain": { "type": "void" },
  "sky": { "skyColor": "000000", "starBrightness": 1.0 },
  "physics": { "gravity": 0.1 },
  "galacticraft": {
    "kind": "asteroids",
    "name": "slag",
    "parent": "ember",
    "distance": 1.75,
    "tier": 3
  }
}
```

Le `parent` d'une ceinture est un système stellaire ou une planète. Autour d'un système, elle figure sur la carte comme une planète, et autour d'une planète comme une lune. Elle accepte toutes les clés du [bloc `galacticraft`](#le-bloc-galacticraft) sauf celles-ci, qu'une ceinture ignore :

| Clé | Dans une ceinture |
| --- | --- |
| `landing` | Un joueur arrive dans une capsule d'entrée sur l'astéroïde le plus proche, comme dans la ceinture de Galacticraft elle-même |
| `landingHeight` | La capsule d'entrée fixe sa propre altitude |
| `arrival` | La capsule d'entrée choisit l'astéroïde |
| `dungeon` | Une ceinture de pack n'a pas de bases abandonnées |

Le ciel, la gravité, le temps et la météo sont les clés propres de la dimension, comme sur n'importe quelle planète de pack. Cinq d'entre eux ont une valeur par défaut différente dans une ceinture, afin de correspondre à celles de Galacticraft :

| Clé | Sans elle |
| --- | --- |
| `sky.fogColor` | `000000`, donc le brouillard et l'horizon sont noirs |
| `sky.renderClouds` | `false` |
| `sky.sunriseColors` | `false`, une ceinture n'a pas de coucher de soleil |
| `sky.sun`, `sky.bodies`, `sky.stars` | Le ciel d'astéroïdes de Galacticraft est dessiné : un petit soleil blanc, pas de lune et un champ d'étoiles dense |
| `time.dayLength` | Pas de jour : le soleil reste immobile sur l'horizon et il fait toujours jour |

Définir l'un des éléments `sun`, `bodies` ou `stars` dessine à la place le ciel du pack, et `sky.renderSky` désactivé n'en dessine toujours aucun. Définir `time.dayLength`, même à `24000`, ou `sky.fixedTime` donne à la ceinture un jour qui lui est propre.

### Stations spatiales

*corps célestes de Galacticraft*

`<namespace>/dimensions/*.json`

Un fichier de dimension dont le bloc `galacticraft` contient `kind: "station"` permet aux joueurs de construire une station spatiale en orbite autour d'une planète ou d'une lune de pack, via le bouton de carte de Galacticraft et sa propre dimension d'orbite. Le fichier est le type de station, et non une station : chaque station qu'un joueur construit est une dimension à part, que Galacticraft crée et conserve.

```json
{
  "id": 80,
  "sky": { "skyColor": "000000", "starBrightness": 1.0 },
  "time": { "dayLength": 12000 },
  "galacticraft": {
    "kind": "station",
    "name": "cinder_station",
    "parent": "cinder",
    "tier": 2,
    "showName": true,
    "recipe": {
      "ingotTin": 32,
      "ingotIron": 24,
      "minecraft:wool:14": 8
    }
  }
}
```

| Clé | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `kind` | oui | `station` | | Fait du fichier une station |
| `parent` | oui | nom | | La planète ou la lune qu'elle orbite : une planète ou une lune accessible créée par une dimension de pack. La station d'une lune figure sur la carte à côté de la lune, autour de la planète de la lune |
| `tier` | non | entier | celui du parent | Le niveau de fusée qui atteint la station |
| `showName` | non | booléen | `false` | Activé, la carte liste chaque station sous le `name` de ce fichier là où Galacticraft écrit `Station: <owner>` ; la ligne du propriétaire reste. Une station que son propriétaire a renommée garde le nom du propriétaire |
| `recipe` | non | objet d'ingrédient et de quantité | la recette de station de Galacticraft | Ce que coûte la construction d'une station. Un ingrédient est un nom du dictionnaire des minerais, `modid:item` ou `modid:item:meta` |
| `checklist` | non | liste de chaînes | aucun | Clés de liste de contrôle de Galacticraft affichées avant le lancement |

Les autres [clés de carte](#clés-de-carte) placent la station sur la carte. Rien d'autre dans le bloc `galacticraft` ne s'applique : une station a l'air, la gravité et l'arrivée propres aux stations de Galacticraft.

En dehors du bloc, une station lit ces clés de dimension et aucune autre :

| Clé | Sans elle |
| --- | --- |
| `id` | Obligatoire. La station prend cet identifiant et le suivant |
| `sky.skyColor`, `sky.fogColor` | Les couleurs d'orbite de Galacticraft |
| `sky.starBrightness` | Les étoiles d'orbite de Galacticraft |
| `sky.sun`, `sky.bodies`, `sky.stars` | Le ciel d'orbite de Galacticraft, avec son parent en dessous |
| `sky.renderSky`, `sky.renderClouds`, `sky.renderWeather` | `true` |
| `time.dayLength` | `24000` |

**Identifiants.** `id` et `id + 1` sont des identifiants de type de dimension, et non la dimension de la station ; les deux doivent être libres. Chaque station construite reçoit le prochain identifiant de dimension libre au moment de sa construction, et Galacticraft conserve cet identifiant, le propriétaire et le nom de la station dans la sauvegarde du monde, si bien que la station revient au même identifiant après un redémarrage. Son dossier de sauvegarde est celui de Galacticraft, `DIM_SPACESTATION<id>`.

**Un seul type de station par corps.** Une planète ou une lune qui a déjà une station, d'un autre pack ou d'un autre mod, la garde : la seconde n'est pas enregistrée, et le journal le signale.

### GalaxySpace et ExtraPlanets

*corps célestes de Galacticraft*

`<namespace>/celestial/*.json` et le bloc `galacticraft` de `<namespace>/dimensions/*.json`

Deux extensions de Galacticraft lisent plus d'informations sur un corps que Galacticraft. Un objet `galaxyspace`, dans une planète ou une lune de `celestial/`, dans l'`star` d'un système ou dans le bloc `galacticraft` d'une dimension, prend effet quand GalaxySpace est installé. Un objet `extraplanets`, uniquement dans le bloc `galacticraft` d'une dimension, prend effet quand ExtraPlanets est installé. Sans l'extension, son objet ne fait rien et le journal le signale une fois pour le corps, qui est tout de même créé comme un corps Galacticraft ordinaire. Une planète peut orbiter autour d'un système de GalaxySpace, comme `tauceti`, `barnards`, `acentauri` ou `proxima`, avec ou sans ces objets.

```json
{
  "id": 72,
  "physics": { "gravity": 0.7 },
  "time": { "dayLength": 48000 },
  "galacticraft": {
    "name": "frost",
    "parent": "tauceti",
    "distance": 1.4,
    "tier": 5,
    "atmosphere": { "gases": ["NITROGEN", "METHANE"], "temperature": -2.5 },
    "galaxyspace": {
      "pressure": 30,
      "radiation": true,
      "class": "iceworld",
      "orbitEccentricity": [1.2, 0.9],
      "orbitOffset": [0.5, 0],
      "thermalVariation": 0.4,
      "solarWind": 0.8,
      "weather": "frozen_storm"
    },
    "extraplanets": {
      "pressure": 40,
      "radiation": 12,
      "temperatureDay": -60,
      "temperatureNight": -80,
      "lander": "general"
    }
  }
}
```

```json
{
  "kind": "system",
  "name": "ember",
  "mapPosition": [0.9, -0.5],
  "star": {
    "name": "ember",
    "galaxyspace": { "starType": "subgiant", "starColor": "orange", "habitableZone": [1.2, 0.6] }
  }
}
```

La `gravity` et la `dayLength` d'une dimension sont aussi ce que GalaxySpace affiche pour le corps et utilise pour lui ; elles n'ont pas besoin de clé propre.

| Clé de `galaxyspace` | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `pressure` | non | flottant | `0` | Pression de l'air. Au-dessus de `10`, elle provoque la nausée, au-dessus de `25` la lenteur, au-dessus de `35` la cécité et au-dessus de `45` des dégâts, sauf si l'armure de GalaxySpace ou sa configuration l'en empêche |
| `radiation` | non | booléen | `false` | Radiation solaire, qui s'accumule chez un joueur se tenant en plein jour à ciel ouvert à moins que son armure ne le protège |
| `class` | non | `selena`, `desert`, `terra`, `oceanide`, `gasgiant`, `icegiant`, `asteroid`, `titan`, `iceworld` | aucun | La classe de planète que la carte et le livre-guide de GalaxySpace affichent |
| `orbitEccentricity` | non | `[x, y]` | `[0, 0]` | Étire l'orbite que dessine la carte de GalaxySpace le long de chaque axe. `0` ou moins laisse cet axe rond |
| `orbitOffset` | non | `[x, y]` | `[0, 0]` | Déplace le centre de cette orbite |
| `freezeBlocks` | non | booléen | `true` | Si le méthane liquide et l'hélium-hydrogène de GalaxySpace placés hors d'un air scellé s'enflamment sous une forte chaleur ou disparaissent sous un froid intense ; l'eau n'est jamais modifiée |
| `thermalVariation` | non | flottant | `0` | De combien le niveau thermique oscille entre midi et minuit, en part de `atmosphere.temperature`, ou de cette valeur quand celle-ci vaut `0`. Demande le système thermique avancé de GalaxySpace |
| `solarWind` | non | flottant | la taille de l'étoile au carré | Production des panneaux à vent solaire de GalaxySpace ici |
| `weather` | non | `dust_storm`, `frozen_storm`, `lightning_storm`, `meteoric_rain` | aucun | Une tempête qui va et vient. Une tempête de poussière blesse quiconque se tient à ciel ouvert, une pluie météoritique fait tomber des météorites, une tempête de foudre fait tomber la foudre |
| `weatherFrequency` | non | flottant | `1` | Fréquence des coups de foudre d'une tempête de foudre |
| `starType` | non | `subdwarf`, `dwarf`, `subgiant`, `giant`, `supergiant`, `hypergiant`, `blackhole` | aucun | Le type d'une étoile, affiché sur la carte de GalaxySpace |
| `starColor` | non | `brown`, `red`, `orange`, `yellow`, `white`, `lightblue`, `blue`, ou une classe de `M1` à `O3` | aucun | La classe de couleur d'une étoile, affichée sur la carte de GalaxySpace |
| `habitableZone` | non | `[distance, width]` | `[0, 0]` | La bande autour d'une étoile que la carte de GalaxySpace marque comme habitable |

`starType`, `starColor` et `habitableZone` ne sont lus que dans l'`star` d'un système ; les autres uniquement dans une planète ou une lune. GalaxySpace dessine ses tempêtes par planète : la tempête d'un pack agit donc, mais n'affiche aucun ciel propre.

| Clé de `extraplanets` | Requis | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `pressure` | non | entier, `0` à `100` | aucun | Pression d'ExtraPlanets. Au-dessus de `0`, elle blesse un joueur sans combinaison spatiale d'ExtraPlanets, et s'affiche sur son ATH |
| `radiation` | non | entier, `0` à `100` | la valeur par défaut d'ExtraPlanets pour les autres extensions | Radiation d'ExtraPlanets, qui s'accumule chez un joueur dont le niveau de combinaison ne la couvre pas |
| `temperatureDay` | non | flottant | `atmosphere.temperature` | Le niveau thermique de jour, sur l'échelle d'ExtraPlanets, qui va d'environ `-140` à `100`. Demande l'option de rembourrage thermique des niveaux 3 et 4 d'ExtraPlanets |
| `temperatureNight` | non | flottant | `temperatureDay` | Le niveau thermique de nuit |
| `lander` | non | `general`, `jupiter`, `saturn`, `mercury`, `neptune`, `uranus` | le module d'atterrissage de Galacticraft | Le module d'atterrissage d'ExtraPlanets dans lequel descend un joueur, quand `landing` vaut `lander` |

## Portails et passages

*le monde*

`<namespace>/blocks/*.json`

Un portail est une définition de bloc ordinaire : la même règle de chemin s'applique, et le chemin du fichier est le nom de registre du bloc.

Un bloc `portal` porte une section `portal` :

```json
{
  "type": "portal",
  "material": "portal",
  "portal": {
    "dimension": 12,
    "returnDimension": 0,
    "gate": "mypack:ruby_gate",
    "cooldown": 60,
    "platform": true,
    "platformBlock": "mypack:ruby_block",
    "sound": "block.portal.travel",
    "owned": true
  },
  "variants": { "ruby_portal": { "meta": 0, "hardness": -1, "light": 11 } }
}
```

| Clé | Obligatoire | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `dimension`       | oui | entier      |                          | La destination                                                                                        |
| `returnDimension` | non | entier      | `0`                      | La dimension où il ramène                                                                             |
| `gate`            | non | nom de portail | aucun                 | Un portail qui doit être ouvert pour passer                                                           |
| `cooldown`        | non | entier, en ticks | `60`                | Délai avant que le même joueur puisse le réutiliser                                                   |
| `platform`        | non | booléen     | `true`                   | Construit une plateforme d'atterrissage à l'arrivée                                                   |
| `platformBlock`   | non | nom de bloc | le cadre du portail lui-même | Le matériau de cette plateforme                                                                   |
| `sound`           | non | nom de son  | aucun                    | Joué au passage                                                                                       |
| `owned`           | non | booléen     | `true`                   | Seul celui qui l'a construit, et ceux qu'il autorise, peut l'utiliser. Un portail possédé est aussi insensible aux explosions |
| `walkIn`          | non | booléen     | `false`                  | Marcher dans le bloc téléporte, comme avec un portail du Nether. Désactivé, il s'utilise à la main   |

### Cadres de portail

*portails et passages*

`<namespace>/portalframes/*.json`

Le chemin du fichier est le nom de registre du cadre, qu'une dimension cite ensuite dans `frames`.

Un cadre est le plan de ce qu'un joueur doit construire, et rien de plus : il indique quels blocs forment le bord et où se trouve le vide, et ne dit rien de la destination du portail. C'est voulu, car une dimension revendique un cadre sans le posséder, et deux dimensions peuvent revendiquer le même.

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

| Clé | Obligatoire | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `name`      | non | chaîne                              | le nom du fichier | Le nom utilisé dans le journal                                                                    |
| `axis`      | non | `vertical`, `horizontal` ou `both`  | `vertical`        | S'il se dresse comme un portail du Nether, repose à plat comme un portail de l'End, ou peut faire l'un ou l'autre |
| `legend`    | oui | objet associant un caractère à un bloc | aucun          | Les blocs que les lignes peuvent employer. Un nom de bloc avec des états se lit comme partout ailleurs |
| `rows`      | oui | liste de chaînes                    | aucun             | Le plan, dessiné en commençant par la ligne du haut                                               |
| `maxWidth`  | non | entier                              | `21`              | Largeur maximale du vide jusqu'où un `*` peut s'étirer                                            |
| `maxHeight` | non | entier                              | `21`              | Hauteur maximale du vide jusqu'où un `*` peut s'étirer                                            |

Trois caractères ne sont pas des blocs. `.` est le vide dans lequel se tient le portail, et un cadre qui n'en comporte pas est refusé. Une espace est une cellule dont le cadre ne se soucie pas : un contour en forme de L se dessine donc en laissant les coins vides. `*` répète : une ligne composée uniquement de `*` répète la ligne du dessus autant de fois que le joueur l'a construite, et un `*` à l'intérieur d'une ligne répète de la même façon le caractère qui le précède. Il peut ne répéter rien du tout : le plan lu en barrant chaque `*` est donc le plus petit cadre qui s'allumera, et les maxima ci-dessus sont le plus grand. Un plan sans aucun `*` est exact, et le joueur doit construire cela et rien d'autre.

Un cadre vertical est reconnu sur l'un ou l'autre axe horizontal et dans les deux sens, peu importe donc dans quelle direction le bâtisseur regardait. Un cadre horizontal est reconnu dans les quatre orientations.

**La taille maximale appartient au pack.** `maxWidth` et `maxHeight` sont le plus grand vide jusqu'où un `*` s'étirera, et tout ce qui est plus petit, jusqu'au minimum, est accepté : un pack décide donc si son portail plafonne à 21, comme en vanilla, ou à 4. Le minimum est un joueur : un cadre dressé est refusé si son vide ne peut pas faire au moins 1 de large sur 2 de haut, un cadre à plat au moins 1 sur 1, et un plan qui ne pourra jamais l'atteindre est refusé au chargement avec une ligne dans le journal, plutôt que de devenir un cadre que personne ne pourra traverser.

**Un cadre coûte d'autant plus cher à rechercher qu'il peut s'étirer.** Avec à la fois un `*` de ligne et un `*` de colonne, toutes les combinaisons jusqu'aux deux maxima sont essayées : un cadre qui s'étire dans les deux sens jusqu'à 21 représente 441 plans. La recherche abandonne plutôt que de se figer, et le signale dans le journal, ce qui indique qu'il faut baisser un maximum ou renoncer à l'un des étirements.

**Rien n'empêche un cadre d'être en obsidienne allumé au briquet, mais il est prioritaire.** Un cadre est recherché avant que l'objet ne fasse son propre travail : un tel cadre ouvre donc la dimension du pack là où se serait dressé un portail du Nether. Choisissez un autre bloc ou un autre allumeur pour laisser le portail vanilla tranquille.

### Ouvrir une dimension avec un cadre

*portails et passages*

`<namespace>/dimensions/*.json`

Une dimension s'ouvre au moyen d'un cadre en portant une section `portal`. Le cadre et ce qui l'allume, ensemble, déterminent la dimension : une même forme de cadre peut donc mener à plusieurs endroits selon ce avec quoi on l'a allumé.

```json
{
  "id": 12,
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

| Clé | Obligatoire | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `frames`        | oui | liste de noms de cadres      | aucun                       | Les cadres qui ouvrent cette dimension                                    |
| `ignitedBy`     | non | nom d'objet                  | `minecraft:flint_and_steel` | Ce que le joueur tient pour en allumer un                                 |
| `color`         | non | couleur hexadécimale         | blanc                       | La couleur dans laquelle le portail est dessiné                           |
| `return`        | non | `built`, `player` ou `none`  | `built`                     | Si un chemin de retour est fourni, construit par le joueur, ou absent     |
| `gate`          | non | nom de portail               | aucun                       | Un portail qui doit être ouvert pour passer                               |
| `cooldown`      | non | entier, en ticks             | `60`                        | Délai avant que le même joueur puisse repasser                            |
| `platform`      | non | booléen                      | `true`                      | Construit une plateforme d'atterrissage à l'arrivée                       |
| `platformBlock` | non | nom de bloc                  | pierre                      | Le matériau de cette plateforme                                           |
| `sound`         | non | nom de son                   | aucun                       | Joué au passage                                                           |
| `owned`         | non | booléen                      | `false`                     | Seul celui qui l'a allumé, et ceux qu'il autorise, peut l'utiliser        |

Le bloc qui occupe le vide n'est pas écrit par le pack. Une dimension dotée d'une section `portal` reçoit le sien, nommé `<namespace>:portal_<dimension>`, dessiné avec la texture de portail du jeu sous `color`, que l'on traverse en marchant plutôt qu'à la main, et incassable. La couleur multiplie la texture, comme le fait un `tintindex` : `#C77DFF` conserve le violet du Nether et `#4CFFB0` le rend toxique. Pour un portail qui n'a plus rien de la texture vanilla, écrivez votre propre bloc `portal` ordinaire avec son modèle et une texture dessinée comme une [carte de pixels](#textures-écrites-sous-forme-de-cartes-de-pixels), où `tint` peut dégrader entre deux couleurs.

`return` décide de ce qui se passe de l'autre côté. `built` dresse le même cadre, à la taille que le joueur a construite, et l'allume, ce qui correspond au comportement vanilla. `player` ne construit rien mais permet d'allumer le même cadre là-bas : le chemin du retour reste donc à trouver et à fabriquer. `none` refuse d'allumer le cadre dans cette dimension, et le voyage est à sens unique.

**Un cadre, plusieurs dimensions.** C'est le couple formé par un cadre et l'objet qui l'allume qui désigne la dimension : le même `standing_gate` allumé au briquet et allumé avec l'allumeur propre au pack ouvre donc deux endroits différents, chacun avec sa couleur. Deux dimensions qui revendiquent le même cadre *et* le même objet constituent une erreur du pack : la seconde est refusée et le journal le dit, au lieu que l'une des deux l'emporte discrètement.

Briser un bloc quelconque du cadre éteint le portail, comme en vanilla.

### Portails

*portails et passages*

`<namespace>/gates/*.json`

Le chemin du fichier est le nom de registre du portail (« gate »), qu'un bloc de portail cite ensuite dans `gate`.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin.

```json
{
  "dimension": 12,
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

| Clé | Obligatoire | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `dimension`       | oui | entier                                  |                                   | La dimension qu'il garde                                                                                       |
| `name`            | non | chaîne                                  | le nom du fichier                 | Montré au joueur                                                                                               |
| `scope`           | non | `player`, `global`                      | `player`                          | Un joueur à la fois, ou le monde entier d'un coup                                                              |
| `open`            | non | booléen                                 | `false`                           | S'il est ouvert dès le départ                                                                                  |
| `unlock`          | non | objet                                   |                                   | Ce qui l'ouvre. Voir ci-dessous                                                                                |
| `unlockedMessage` | non | chaîne                                  | `%dim% is now open`               | Montré à son ouverture                                                                                         |
| `blockedMessage`  | non | chaîne                                  | `You need %item% to enter %dim%`  | Montré quand il refuse                                                                                         |
| `safeReturn`      | non | booléen                                 | `false`                           | Un retour bloqué atterrit tout de même en lieu sûr au lieu d'être refusé                                       |
| `requires`        | non | liste d'ids de mods ou de namespaces de packs | aucun                       | Le portail est ignoré sauf si tous sont présents                                                               |
| `portalBlocks`    | non | liste de noms de blocs                  | tous les portails                 | Limite le portail à ces blocs de portail, de sorte qu'une dimension puisse avoir une porte gardée et une porte ouverte |

`unlock` accepte `hold` (un objet qui doit être tenu en main), `consume` avec `consumeCount` (`1`), `craft` (un objet qui doit avoir été fabriqué), `advancement`, et `killed` (un nom d'entité : le portail s'ouvre pour quiconque en tue une, de sorte qu'un boss peut détenir la clé d'un monde) avec `killedCount` (`1`) quand une seule ne suffit pas, décompté par joueur ou pour le monde entier selon la portée. Ajouter `killedDrops` (un nom d'objet) fait que les morts comptées déposent cet objet aux pieds du tueur au lieu d'ouvrir le portail, et remet le décompte à zéro : une clé peut ainsi être regagnée et remise à quelqu'un qui n'a jamais combattu pour elle ; conditionnez avec `hold` ou `consume` le même objet pour en faire la clé. `%item%`, `%mob%` et `%dim%` sont remplacés pour vous. Une clé qu'un mob lâche n'exige rien de spécial ici : donnez le butin au mob et conditionnez avec `hold` ou `consume`.

## Mondes Rubic

*le monde*

`rubicWorld` dans les réglages `terrain` reconstruit le monde d'une dimension à partir de cubes de 16×16×16 au lieu de colonnes de 256 blocs, de sorte que son plancher et son plafond peuvent se trouver où le pack les place. La génération du terrain elle-même est inchangée : le générateur vanilla et le worldgen des autres mods fonctionnent comme d'habitude et produisent le même relief ; il y a simplement du monde au-dessus et en dessous.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "rubicWorld": true,
    "worldMinHeight": -1024,
    "worldMaxHeight": 1024,
    "rubicWorldDimensions": [0, -1],
    "rubicWorldDimensionsAreBlacklist": false,
    "terrainOffset": 0
  }
}
```

Toutes les clés se trouvent dans le groupe `terrain`, dans le bloc `settings` d'un modèle de monde comme les autres :

| Clé | Valeur | Défaut | Rôle |
| --- | --- | --- | --- |
| `rubicWorld`                       | booléen                              | `false` | Active les mondes Rubic |
| `worldMinHeight`                   | entier, multiple de 16               | `-64`   | Le plancher du monde |
| `worldMaxHeight`                   | entier, multiple de 16               | `320`   | Le plafond du monde |
| `rubicWorldDimensions`             | liste d'entiers                      | vide    | Quelles dimensions deviennent Rubic. Vide signifie toutes les dimensions |
| `rubicWorldDimensionsAreBlacklist` | booléen                              | `false` | Traite la liste comme les dimensions à laisser tranquilles |
| `terrainOffset`                    | entier, multiple de 16 non négatif   | `0`     | Décale vers le haut toute la fenêtre de terrain vanilla. Pour les préréglages en couches simples : un monde plat avec `272` place sa surface près de y 275, au-dessus du plafond vanilla. Les décorations et structures qu'un préréglage demande se génèrent toujours à leurs hauteurs non décalées |

**Hauteurs.** `worldMinHeight` doit être inférieur à `worldMaxHeight`, tous deux multiples de 16, et tous deux dans la limite que permet `rubicHeightLimit` dans la configuration (`4096` blocs dans chaque sens par défaut ; configuration uniquement, jamais une clé de pack). Tout le reste est refusé avec une ligne de journal et le monde est créé de `-64` à `320`. La hauteur coûte de la place : chaque tranche de 16 blocs ajoute un cube à chaque colonne, si bien que la mémoire, le disque et le temps de prégénération augmentent avec elle ; le commentaire de configuration sur `rubicHeightLimit` donne les chiffres.

**La fenêtre de terrain.** Le générateur propre à la dimension conserve sa propre hauteur, 256 blocs dans l'Overworld, et c'est cette fenêtre que `terrainOffset` fait glisser. `worldMinHeight` et `worldMaxHeight` ajoutent de la place autour de la fenêtre, jamais à l'intérieur. Relever le plafond ne relève pas le terrain, cela ajoute du ciel ; abaisser le plancher n'approfondit pas les grottes que le générateur a creusées, cela ajoute du monde profond. Le niveau de la mer se trouve lui aussi dans la fenêtre : il suit donc `terrainOffset` et vient du type de monde, non d'une clé Rubic. Pour placer la surface plus haut dans le monde, augmentez `terrainOffset`. Pour ajouter de la place au-dessus ou en dessous, déplacez les hauteurs. Chaque cube hors de la fenêtre est tout de même généré et éclairé dans chaque colonne : un plafond plus haut coûte donc du temps de prégénération, qu'on le remplisse ou non, et coûte en plus de la mémoire et du disque dès que `skyStone` le remplit.

**Ce qu'une fenêtre décalée fait aux autres mods.** La population s'exécute sur la colonne : chaque générateur qu'un mod enregistre s'exécute donc toujours une fois par chunk, sans aucune coordonnée traduite. Ce qui change, c'est l'endroit où atterrissent ses propres calculs. Un générateur qui demande au monde où se trouve le sol, par le bloc solide le plus haut ou la hauteur de précipitation, suit le terrain décalé : les deux gèrent Rubic, ce qui couvre les arbres, les fleurs et la plupart des décorations. Un générateur qui calcule une hauteur absolue, comme le schéma de minerai habituel d'un y aléatoire sous 64, continue d'écrire à cette hauteur, qui après un décalage est le remplissage ou le monde profond bien en dessous du terrain. Le niveau de la mer n'est pas décalé non plus : un générateur qui le teste lit donc le nombre non décalé. Ces écritures atterrissent aussi hors des cubes que la population garde chargés, et attirent des cubes supplémentaires pendant qu'une colonne se peuple. Un grand `terrainOffset` convient à un pack qui décrit sa propre génération, pas à un pack empilé sur le worldgen d'un autre. Pour la seule place, la profondeur est la direction la moins coûteuse : sous la fenêtre se trouve un générateur complet avec sa propre pierre, ses grottes, ses filons, ses aquifères et ses donjons, et il laisse la surface aux hauteurs que tous les autres générateurs supposent, alors que l'espace au-dessus de la fenêtre est un décor qu'un pack doit meubler lui-même.

**Décidé par sauvegarde, une fois.** Le fait qu'une dimension soit Rubic et ses hauteurs sont écrits dans sa sauvegarde la première fois qu'elle se charge, puis valent pour toujours : un monde Rubic reste Rubic même une fois le pack retiré, et ses hauteurs ne peuvent plus être modifiées. Les dimensions autres que l'Overworld reprennent les hauteurs de l'Overworld. Les terrains Anvil existants ne sont pas convertis : Rubic conserve ses terrains dans ses propres fichiers `region2d`/`region3d`, si bien qu'une dimension déjà générée en Anvil repart de zéro pour son terrain. Activez-le pour les nouveaux mondes.

**Exclure des dimensions.** Une dimension absente de `rubicWorldDimensions` conserve son monde Anvil ordinaire, dans la même sauvegarde : les dimensions Rubic et Anvil se mélangent librement. C'est le bon choix pour les dimensions dont les générateurs écrivent dans les entrailles des chunks au lieu de passer par le cycle de population ordinaire. Indépendamment de la liste, un monde dont les classes serveur ont été remplacées par un autre mod est ignoré, avec une ligne de journal pour le dire.

**Espace hors de la fenêtre.** La plage propre au générateur garde sa forme habituelle, et la place qu'un monde Rubic ajoute autour est remplie avec le bloc par lequel cette plage se termine : de la pierre sous l'Overworld, de l'air au-dessus. Une dimension dont le sommet est scellé par de la bedrock, le Nether avant tout, compte comme fermée : la place au-dessus est donc laissée vide au lieu d'être remplie de la netherrack située sous son toit. Le toit lui-même n'est pas touché. `deepStone` nomme le bloc de la place sous la fenêtre, `skyStone` celui de la place au-dessus.

**CubicChunks.** Faire fonctionner les deux n'est pas pris en charge. Avec CubicChunks installé et un pack qui demande `rubicWorld`, le chargement s'arrête avec un message : retirez CubicChunks, ou retirez `rubicWorld` du pack et laissez CubicChunks créer les mondes.

### Passer un monde à CubicChunks et revenir en arrière

*mondes Rubic*

**Noms de fichiers.** Un monde Rubic conserve ses colonnes dans `region2d/<x>.<z>.2rdr` et ses cubes dans `region3d/<x>.<y>.<z>.3rdr`. Une entrée trop grande pour son fichier de région va dans un dossier voisin, nommé d'après le fichier avec `.ext` ajouté. Les mondes créés avant ces noms utilisaient `.2dr` et `.3dr` : le mod renomme lui-même ces fichiers et dossiers au chargement d'une dimension, avec une ligne de journal par dimension, si bien qu'un ancien monde n'exige aucune intervention manuelle.

**Ouvrir un monde CubicChunks.** Quand un pack demande `rubicWorld` et que le monde ouvert est marqué comme monde CubicChunks, le mod demande confirmation avant de faire quoi que ce soit, comme Forge le fait pour les entrées de registre manquantes : un écran de confirmation en solo, et sur un serveur dédié un message en console auquel on répond par `/fml confirm` ou `/fml cancel`, ou à l'avance avec `-Dfml.queryResult=confirm`. Sur oui, la sauvegarde de monde de Forge est écrite sous forme de zip dans le dossier des sauvegardes et le monde est converti sur place en monde Rubic, puis le chargement continue. Sur non, le chargement s'arrête et rien n'est modifié dans le monde.

**Le convertisseur.** La même conversion, et le chemin inverse, peuvent être lancés hors du jeu. Le dépôt fournit [`scripts/convert_rubic_world.py`](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/tree/1.12.2-1.0-Release/scripts), qui transforme un monde Rubic en monde CubicChunks, ou un monde CubicChunks en monde Rubic. Il demande Python 3 et rien d'autre. Fermez le jeu et sauvegardez d'abord le monde, puis examinez la simulation avant de la lancer pour de bon.

```
python3 scripts/convert_rubic_world.py to-cubic "saves/My World" --dry-run
python3 scripts/convert_rubic_world.py to-cubic "saves/My World"
```

| Argument | Rôle |
| --- | --- |
| `to-cubic`       | Un monde Rubic devient un monde CubicChunks |
| `to-rubic`       | Un monde CubicChunks devient un monde Rubic |
| `<world folder>` | Le dossier de sauvegarde, celui qui contient `level.dat` |
| `--dry-run`      | Affiche chaque changement et n'en fait aucun |

**Ce qu'il modifie.** Dans chaque dimension, les fichiers de région et leurs dossiers `.ext` prennent les noms de l'autre côté (`.2rdr` et `.3rdr` pour Rubic, `.2dr` et `.3dr` pour CubicChunks), et `data/rdplRubicData.dat` devient `data/cubicChunksData.dat` ou l'inverse, avec les hauteurs conservées et le format de stockage et le générateur de compatibilité nommés comme l'autre mod les nomme. En dernier lieu, le marqueur de `level.dat` et de `level.dat_old` est échangé entre `isRubicWorld` et `isCubicWorld`. Les cubes et les colonnes eux-mêmes ne sont pas réécrits.

**Ce qu'il refuse.** Un monde dont le marqueur de `level.dat` ne correspond pas au sens demandé, un format de stockage ou un générateur de compatibilité que l'autre mod n'a pas, et un renommage dont la cible existe déjà. Un refus ne change rien, et une exécution interrompue peut être relancée.

**Ce qui ne se transmet pas.** Un monde converti ne contient que ce que comprennent les deux mods. Les blocs et dimensions définis par un pack n'existent pas sous CubicChunks seul, et chaque mod recalcule la lumière des cubes que l'autre a enregistrés.

### Diffusion des cubes

*mondes Rubic*

**Diffusion des cubes.** Quatre clés `chunks` décident de la façon dont les cubes parviennent à un joueur et du moment où ils sont relâchés. Elles n'ont d'effet que sur un monde Rubic, et les valeurs par défaut sont les nombres auxquels le sous-système a été réglé : un pack qui n'y touche pas ne paie rien.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "verticalCubeLoadDistance": 8,
    "cubesSentPerTick": 649,
    "cubeGenMillisPerRound": 50,
    "cubeGCInterval": 200
  }
}
```

| Clé | Valeur | Défaut | Rôle |
| --- | --- | --- | --- |
| `verticalCubeLoadDistance` | entier, en cubes        | `8`   | Combien de cubes au-dessus et en dessous d'un joueur un ticket de chargement de chunks retient. Le curseur de réglages vidéo du même nom est la distance d'affichage propre au client, définie par celui qui joue et non par un pack |
| `cubesSentPerTick`         | entier, en cubes        | `649` | Combien de cubes un joueur peut recevoir en un tick. L'augmenter remplit plus vite une bulle de vue et grossit les paquets de chaque tick ; un paquet est tout de même scindé à 1024 cubes ou 512 Ko, selon ce qui arrive en premier |
| `cubeGenMillisPerRound`    | entier, en millisecondes | `50` | Combien de temps un tick peut passer à générer les cubes que les joueurs attendent |
| `cubeGCInterval`           | entier, en ticks        | `200` | À quelle fréquence les cubes que personne ne regarde sont relâchés |

**Client.** Les réglages vidéo gagnent un curseur de distance d'affichage verticale, l'équivalent vertical de la distance d'affichage (`verticalCubeLoadDistance` dans la configuration, qui appartient à celui qui joue). Tout le reste du groupe `terrain` — prégénération, physique du monde, point d'apparition, bordure — s'applique inchangé aux mondes Rubic.

## Le monde profond

*le monde*

Neuf autres clés `terrain` remplissent l'espace qu'un monde Rubic ouvre autour de la fenêtre de terrain vanilla avec une génération de style moderne. Elles n'ont d'effet que sur un monde Rubic :

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "rubicWorld": true,
    "worldMinHeight": -64,
    "worldMaxHeight": 1024,
    "deepStone": "mypack:slate",
    "skyStone": "minecraft:end_stone",
    "skyShape": "islands",
    "skyIslands": 0.05,
    "skyThickness": 3.0,
    "skyHeights": [400, 800],
    "noiseCaves": "world",
    "deepRavines": true,
    "oreVeins": ["minecraft:iron_ore,,mypack:slate@1,-56,20"]
  }
}
```

| Clé | Valeur | Défaut | Rôle |
| --- | --- | --- | --- |
| `deepStone`    | `namespace:block`, méta sous la forme `@meta` | aucun     | Le bloc dont est fait le monde sous la fenêtre, comme le deepslate propre à un pack. Il se fond dans la pierre de la fenêtre sur les huit couches les plus basses de la fenêtre, comme les versions modernes fondent le deepslate |
| `skyStone`     | `namespace:block`, méta sous la forme `@meta` | aucun     | Le bloc dont est fait le monde au-dessus de la fenêtre sous sa surface, façonné en terres flottantes par le même bruit qui creuse le monde profond en dessous : ce qui est grotte là-bas est île ici. Vide, il laisse l'espace au-dessus de la fenêtre vide, comme il l'a toujours été. La terre porte la propre surface de la colonne, le bloc du dessus et les trois en dessous étant pris dans le biome : une île de l'Overworld se lit donc comme de l'herbe sur de la terre sur ce bloc. Un biome ou une région de grottes peut nommer ses propres `skyStone`, `skyIslands` et `skyThickness`, de sorte qu'une bande ou une région porte ses propres îles, résolues par colonne, la région l'emportant sur la bande et la bande sur le biome. Les îles sont décorées pour elles-mêmes : chaque cube au-dessus de la fenêtre exécute les éléments propres au biome contre la surface située dans ce cube, si bien que les arbres, l'herbe, les fleurs, les champignons, les roseaux et les touffes se posent sur l'île au lieu d'être éparpillés le long de la colonne comme vanilla les place ; et une bande de biome là-haut se décore avec ses propres quantités. Les ajouts propres à un biome s'y exécutent aussi, pas seulement les ajouts communs : puits du désert, melons de jungle, canopée dense et champignons d'une forêt de chênes noirs, rochers de la taïga, pics de glace des biomes glacés, et les grandes fleurs et herbes que chaque biome place. Une île n'est jamais faite d'un bloc qui tombe : là où la surface d'un biome serait du sable ou du gravier, l'île utilise du grès, ou son propre `skyStone` pour tout le reste, puisque rien ne retient un bloc qui tombe en plein air. Les troupeaux sont placés de la même façon, par cube, si bien que les animaux commencent sur les îles à mesure que le terrain est créé. La profondeur de surface varie d'un à quatre blocs de remplissage avec le bruit : le bord d'une île n'est donc pas une croûte uniforme, et la croûte est mesurée le long de la pente plutôt qu'à la verticale, si bien qu'une face abrupte garde sa terre au lieu de s'amincir jusqu'à rien. La surface suit le biome que le ciel lui-même signale, la région de grottes d'abord, puis un biome par bande d'altitude, puis la colonne en dessous : nommer `minecraft:mesa` sur une région du ciel donne donc des îles d'argile rayée à n'importe quelle hauteur, les mêmes bandes que le sol, et nommer un désert donne son sable, transformé en grès parce que rien ne retient un bloc qui tombe. Les animaux s'y installent, ce que `skyAnimals` dans le groupe `spawning` désactive. La part du ciel qui devient terre est `skyIslands`, et sa valeur par défaut de 0.5 est un archipel : sur un monde généré, environ sept cubes sur huit au-dessus de la fenêtre restent vides et sa couche la plus remplie avoisine un tiers : le ciel se traverse en vol plutôt qu'il ne se parcourt à pied. Abaissez-le vers 0.2 et la bande se referme en un plafond ondulé couvert de collines, solide aux quatre cinquièmes en son milieu, sur lequel on peut construire mais qui n'est plus des îles. Les îles s'arrêtent huit blocs avant `worldMaxHeight` : un sommet n'est donc jamais coupé net contre le plafond, et les arbres et les plantes ont de la place au-dessus ; `caves` remplit jusqu'au plafond comme avant. Chaque dimension Rubic a sa propre fenêtre, et cela remplit donc l'espace au-dessus de chacune : dans le Nether, dont la fenêtre fait 128 de haut, c'est la place au-dessus du toit de bedrock, et un raccord qui ouvre le plafond efface le toit lui-même |
| `skyShape`     | `islands` ou `caves`                      | `islands` | La forme donnée au monde au-dessus de la fenêtre. `islands` est de la terre flottante. `caves` est de la roche pleine traversée de grottes, le traitement du monde profond retourné vers le haut, qui donne environ 86 pour cent de plein, le même rapport roche/grotte que le monde profond. Rien ne s'inonde dans les deux cas, car aucun aquifère n'est consulté au-dessus de la fenêtre. Lu seulement quand `skyStone` nomme un bloc |
| `skyIslands`   | nombre, de `-1` à `1`                     | `0.5`     | La facilité avec laquelle le ciel se rassemble en îles. Plus bas répartit les îles sur plus de ciel et approfondit leur ombre, plus haut laisse moins de morceaux et plus petits. La valeur par défaut laisse environ sept cubes sur huit vides et culmine près d'un tiers ; vers `0.2` la bande se referme en un plafond avec des collines, solide aux quatre cinquièmes en son milieu. Lu seulement quand `skyStone` nomme un bloc et que `skyShape` vaut `islands` |
| `skyThickness` | nombre, `0` ou plus                       | `2.0`     | La solidité d'une île. Plus haut remplit les îles, plus bas les évide et amincit leurs bords jusqu'à rien. Lu seulement quand `skyStone` nomme un bloc et que `skyShape` vaut `islands` |
| `skyHeights`   | deux entiers, le plus bas puis le plus haut | aucun   | Le bloc le plus bas et le plus haut qu'une île peut atteindre, comptés depuis le bas de la fenêtre comme les hauteurs de `oreVeins`. Vide remplit tout le monde au-dessus de la fenêtre, ce qui sur un monde haut représente énormément de ciel. Lu seulement quand `skyStone` nomme un bloc |
| `noiseCaves`   | `off`, `deep`, `world`                    | `off`     | Grottes de bruit de style moderne : cavernes fromage, tunnels spaghetti, entrées de grottes près de la surface et piliers dans les grandes salles. `deep` ne creuse que sous la fenêtre, `world` creuse le monde entier |
| `deepRavines`  | booléen                                   | `false`   | Creuse des ravins de style vanilla à travers le monde sous la fenêtre, de longs canyons escarpés. Un ravin prend les fluides propres au monde profond là où il les traverse, se remplissant de lave sous la ligne de lave et gardant l'eau d'un aquifère ou son mur de pression au-dessus, de sorte qu'il ne vide jamais ce qu'il entaille. Les versions modernes ne creusent leurs canyons qu'à l'intérieur de la fenêtre : le monde profond n'en a donc aucun sauf si cette option est activée |
| `oreVeins`     | liste de `ore,extra,filler,lowest,highest` | aucun    | De grands filons de minerai rayés, composés surtout du bloc `filler` avec le `ore` disséminé dedans et une rare chance du `extra`, qui peut rester vide. Les hauteurs se comptent depuis le bas de la fenêtre : les négatives atteignent donc le monde profond |

L'eau et la lave se comportent bien là-dessous. De la lave en vrac remplit les couches les plus basses, et les grottes au-dessus portent des aquifères locaux — le même schéma de points d'échantillonnage et de pression que celui des versions modernes, porté depuis 26.1.2 — si bien que des poches d'eau calme se tiennent à leurs propres niveaux, avec des murs de pierre profonde façonnés par le bruit partout où deux niveaux se rencontrent ou où l'eau rencontre la lave. Sous les océans, les grottes s'inondent vers le niveau de la mer, comme les versions modernes rattachent leurs aquifères à la surface.

**Par dimension.** `deepStone`, `noiseCaves`, `skyStone`, `skyShape`, `deepRavines` et `oreVeins` acceptent chacun une valeur unique ou une liste, et une entrée de liste écrite `dimension=valeur` s'applique à cette seule dimension. Là où une entrée nomme une dimension, ces entrées décident seules et les entrées sans nom sont ignorées, si bien que `"1="` sans rien derrière désactive la clé pour cette dimension. Une valeur sans dimension atteint toutes les dimensions Rubic sauf l'End, qui reste vide à moins qu'un pack ne le nomme : remplir l'End serait la fin de l'End, et un End rempli aveugle aussi la recherche vanilla de la passerelle, qui remonte depuis 1024 blocs tant qu'elle rencontre des chunks contenant des blocs. Nommez-le et vous obtenez ce que vous avez demandé.

Avec `noiseCaves` activé, le monde profond tire aussi des salles de monstres de style moderne — environ quatre essais par colonne de chunk sous la fenêtre, aucun à moins de six blocs du plancher du monde — si bien que des générateurs de donjon et leur butin de coffres apparaissent dans les grottes profondes comme dans les versions modernes.

La portée `world` retire aussi deux reliquats vanilla qui combattraient les grottes remaniées. La lave que vanilla verse dans ses grottes sous y 10 est jugée par l'aquifère à la place, si bien que l'ancienne fenêtre de lave disparaît, et les lacs d'eau enfouis de vanilla — mares de surface comprises — cessent de se générer, comme les versions modernes les ont abandonnés ; les poches propres à l'aquifère prennent leur place.

La portée `deep` laisse la bande vanilla telle quelle, fenêtre de lave comprise, et se contente de sceller le raccord où les deux se rencontrent. De la lave ou de l'eau sur la couche la plus basse de la fenêtre avec une grotte profonde ouverte juste en dessous devient de la pierre profonde, afin que la fenêtre ne puisse pas se vider dans les grottes d'en dessous.

## Régions de grottes

*le monde*

`<namespace>/caveregions/*.json`

Le chemin du fichier est le nom de la région, qu'une entrée de worldgen cite ensuite dans `caveRegions`. Un nom seul prend ici le namespace propre à cette entrée.

Peint des régions nommées sur le sous-sol, l'équivalent pour les packs des biomes de grottes modernes. Le sous-sol est divisé en cellules arrondies — larges de `caveRegionCells` blocs et hautes de `caveRegionCellsY`, deux clés `terrain` — et chaque cellule tire une région, ou aucune, selon le poids. Tout ce que fait une région découle de façon déterministe de la seed : les chunks s'accordent donc entre eux sans jamais écrire par-delà une frontière.

### Fichiers de région

*régions de grottes*

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin.

```json
{
  "weight": 3,
  "minHeight": -56,
  "maxHeight": 16,
  "dimensions": [0],
  "biome": "minecraft:mushroom_island",
  "floorCover": "minecraft:mycelium",
  "floorChance": 0.8,
  "ceilingCover": "minecraft:brown_mushroom_block",
  "ceilingChance": 0.3,
  "coverReplace": ["minecraft:stone", "mypack:slate"],
  "waterLevel": -24,
  "keepDefaultSpawns": false,
  "spawns": [
    { "entity": "minecraft:mooshroom", "type": "creature", "weight": 12, "min": 2, "max": 4 }
  ],
  "structures": [
    { "structure": "mypack:cave_shrine", "weight": 3 },
    "mypack:cave_well"
  ],
  "structureChance": 0.5,
  "skyStone": "minecraft:sandstone",
  "skyIslands": 0.2,
  "skyThickness": 2.0,
  "ambientSound": "minecraft:block.water.ambient",
  "soundChance": 0.02,
  "particle": "dripWater",
  "particleChance": 0.002
}
```

| Clé | Valeur | Défaut | Rôle |
| --- | --- | --- | --- |
| `weight`            | entier           | `1`                  | Part des cellules que cette région remporte. `0` la désactive |
| `minHeight`         | entier           | le plancher du monde | Bas de la bande dans laquelle la région existe |
| `maxHeight`         | entier           | `48`                 | Haut de cette bande. Une cellule dont le centre se trouve hors de la bande ne choisit jamais la région |
| `dimensions`        | liste d'entiers  | toutes               | Dans quelles dimensions la région apparaît |
| `floorCover`        | bloc             | aucun                | Remplace le bloc du dessus des sols de grotte dans la région |
| `floorChance`       | 0.0 à 1.0        | `1.0`                | Quelle part du sol est recouverte |
| `ceilingCover`      | bloc             | aucun                | Remplace les blocs de plafond de grotte dans la région |
| `ceilingChance`     | 0.0 à 1.0        | `1.0`                | Quelle part du plafond |
| `coverReplace`      | liste de blocs   | tout ce qui ressemble à de la pierre | Ce que les revêtements peuvent remplacer |
| `waterLevel`        | entier           | aucun                | Fixe le niveau d'eau de chaque point d'échantillonnage d'aquifère dans la région, de sorte que ses grottes s'inondent jusqu'à cette hauteur. Les murs là où la région rencontre des grottes sèches sont façonnés par le même bruit de pression que les aquifères modernes, et l'eau ne touche jamais le sol de lave. Exige `noiseCaves` activé |
| `spawns`            | liste            | aucun                | Les mobs qui apparaissent dans la région, les mêmes entrées que celles qu'accepte le `spawns` d'un biome : `entity`, `type` (monster, creature, ambient ou water), `weight`, `min` et `max` pour la taille du groupe. Sous la fenêtre de terrain, un endroit qui voit le ciel est laissé au biome, comme le sont les revêtements ; au-dessus de la fenêtre, où la seule terre est celle de la génération du ciel, la liste s'applique aussi à l'air libre |
| `keepDefaultSpawns` | booléen          | `false`              | Conserve la liste d'apparition propre au biome à côté de celle de la région. Désactivé, la liste de la région la remplace entièrement dans la région |
| `structures`        | liste            | aucun                | Une structure placée une fois par cellule de région, au cœur de la cellule, calée sur un sol de grotte — comme les versions modernes donnent un point de repère à un biome de grotte. Les entrées sont des modèles `namespace:name`, ou `{ "structure": "...", "weight": 3 }` pour choisir parmi plusieurs |
| `structureChance`   | 0.0 à 1.0        | `1.0`                | La probabilité que chaque cellule de la région obtienne réellement sa structure |
| `structureLoot`     | `namespace:path` | aucun                | La table de butin dont est rempli chaque coffre d'une structure placée la première fois qu'il est ouvert |
| `biome`             | nom de biome     | aucun                | Le biome que la région signale dans son volume, écrit dans le cube comme un biome 3D. Donne à la région son propre feuillage, ses couleurs d'herbe et d'eau, sa musique et ses sons d'ambiance, et permet à la pondération d'apparition vanilla de le lire. La surface au-dessus n'est pas touchée, car seules les cellules occupées par la région sont écrites |
| `skyStone`          | bloc             | le réglage du monde  | Le bloc dont sont faites les îles du ciel sous leur surface dans cette région, de sorte qu'une région porte ses propres îles |
| `skyIslands`        | `-1` à `1`       | le réglage du monde  | Le seuil des îles dans la région. Plus bas rassemble plus de terre |
| `skyThickness`      | `0` ou plus      | le réglage du monde  | La solidité des îles de la région |
| `ambientSound`      | nom de son       | aucun                | Un son joué de temps à autre à un joueur qui se tient dans la région, comme les biomes modernes ajoutent leurs propres sons de grotte. Envoyé par le serveur à ce seul joueur |
| `soundChance`       | 0.0 à 1.0        | `0.0111`             | La probabilité à chaque tick que `ambientSound` soit joué |
| `particle`          | nom de particule | aucun                | Une particule affichée autour d'un joueur dans la région, l'un des noms de particules du jeu comme `dripWater`, `happyVillager` ou `depthsuspend`. Seul l'air dans la région l'affiche |
| `particleChance`    | 0.0 à 1.0        | `0.00625`            | La densité de particules des biomes modernes : à chaque tick environ 667 endroits dans un rayon de 16 blocs sont essayés, et chacun affiche la particule avec cette probabilité |

### Cellules

*régions de grottes*

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

| Réglage | Type | Défaut | Rôle |
| --- | --- | --- | --- |
| `caveRegionCells`       | entier, en blocs | `128` | La largeur d'une cellule de région |
| `caveRegionCellsY`      | entier, en blocs | `64`  | La hauteur d'une cellule de région |
| `caveRegionPlainWeight` | entier           | `4`   | Le poids du sous-sol ordinaire, sans région, dans le tirage de chaque cellule. Plus haut laisse davantage de sous-sol sans aucune région : avec une seule région de poids 1, environ un cinquième des cellules l'obtiennent |

La part du sous-sol qui reste ordinaire est la clé `terrain` `caveRegionPlainWeight`, `4` par défaut : avec une seule région de poids 1, environ un cinquième des cellules obtiennent la région. Les revêtements s'appliquent sous un toit : une région qui atteint la surface ne se voit donc jamais en surface ; au-dessus de la fenêtre de terrain ils s'appliquent aussi à l'air libre, puisque tout ce qui s'y trouve est de la terre créée par la génération du ciel. Les revêtements fonctionnent dans toutes les grottes, quel que soit le générateur qui les a creusées ; `waterLevel` est la seule clé qui exige les grottes de bruit, parce que l'inondation est placée pendant leur creusement.

### Éléments d'une région

*régions de grottes*

Les éléments se rattachent par deux clés des [entrées de worldgen](#entrées-de-worldgen) ordinaires. `caveRegions` liste les régions dans lesquelles une entrée peut se générer, vérifiées à la position placée : champignons, cristaux ou tout autre élément n'apparaissent donc qu'à l'intérieur de leur région. `snap` déplace d'abord chaque tentative verticalement jusqu'à la surface de grotte la plus proche : `floor` pour ce qui se dresse, `ceiling` pour ce qui pend. Une région de type dripstone n'exige aucune forme nouvelle :

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

Le `replace` de `minecraft:air` compte : ce sur quoi une forme placée écrit est vérifié par rapport à `replace`, dont la valeur par défaut est la pierre, de sorte que tout ce qui est construit dans l'espace ouvert d'une grotte exige que l'air soit listé. La même entrée avec `"snap": "floor"` et sans `hanging` fait pousser les stalagmites correspondantes. Le filtre de région fonctionne avec toutes les formes placées ; `belt` et `field` se placent selon leurs propres règles et l'ignorent.

---

# Générer le monde

## Entrées de worldgen

*générer le monde*

`<namespace>/worldgen/*.json`

Le chemin du fichier nomme l'entrée, et les formes `belt` et `field` initialisent leur bruit à partir de lui : renommer un fichier déplace donc ce qu'il génère.

Décrit quelque chose qui se génère. Chaque entrée est une **forme** placée par une **propagation**, filtrée selon l'endroit où elle est autorisée.

```json
{
  "block": "mypack:ruby_ore",
  "meta": 0,
  "blocks": [
    { "block": "mypack:ruby_ore", "meta": 0, "weight": 80 },
    { "block": "minecraft:wool", "weight": 20, "properties": { "color": "magenta" } }
  ],
  "size": 8,
  "attempts": 12,
  "replace": ["minecraft:stone"],
  "adjacent": ["minecraft:air"],
  "minHeight": 8,
  "maxHeight": 48,
  "dimensions": [0],
  "dimensionsAreBlacklist": false,
  "biomes": ["minecraft:extreme_hills"],
  "biomeTypes": ["MOUNTAIN"],
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
  "requires": ["quark"],
  "shape": { "type": "cluster" },
  "spread": { "type": "even" }
}
```

Seul `block` est obligatoire ; tout le reste peut être omis et prend sa valeur par défaut. `blocks` remplace `block` quand un seul ne suffit pas et possède son propre exemple plus bas.

### Ce qu'il place

*entrées de worldgen*

| Clé | Obligatoire | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `block`    | oui | nom de bloc                       |                         | Ce qui est placé |
| `meta`     | non | entier                            | `0`                     | Quelle variante de ce bloc |
| `blocks`   | non | liste d'objets                    | aucun                   | Une liste pondérée, utilisée à la place d'un seul bloc. Voir ci-dessous |
| `size`     | non | entier ou intervalle              | `8`                     | Combien de blocs une tentative place, ou la taille d'une forme dotée d'un rayon |
| `attempts` | non | entier ou intervalle              | `8`                     | Combien de fois par chunk il essaie |
| `sparse`   | non | booléen                           | `false`                 | Disperse les blocs au lieu de les tasser ensemble |
| `shape`    | non | objet                             | `{ "type": "cluster" }` | La forme qu'il prend. Voir [Formes](#formes) |
| `spread`   | non | objet                             | `{ "type": "even" }`    | Où il est placé. Voir [Propagations](#propagations) |
| `replace`  | non | liste de noms de blocs ou d'objets | `["minecraft:stone"]`  | Ce qu'il peut remplacer. Voir ci-dessous |
| `adjacent` | non | liste de noms de blocs ou d'objets | aucun                  | Ne place que si l'un de ceux-ci figure parmi les 26 blocs qui touchent l'emplacement. Mêmes trois formes que `replace` |

### Où il peut se générer

*entrées de worldgen*

| Clé | Obligatoire | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `minHeight`              | non | entier                        | `0`                  | Le y le plus bas où il placera |
| `maxHeight`              | non | entier                        | `64`                 | Le y le plus haut où il placera |
| `snap`                   | non | `floor` ou `ceiling`          | aucun                | Déplace d'abord chaque tentative verticalement jusqu'au sol ou au plafond de grotte le plus proche |
| `snapDepth`              | non | entier                        | `0`                  | De combien `snap` se déplace ensuite au-delà de la surface, vers le bas depuis un sol et vers le haut depuis un plafond. `0` reste dans l'espace ouvert contre la surface, `1` est le bloc de surface lui-même, `2` celui qui est derrière. Ce qu'il peut écraser reste régi par `replace` : c'est ainsi qu'un pack répartit en bandes un bloc juste sous le sol plutôt que dessus |
| `dimensions`             | non | liste d'entiers               | toutes les dimensions | Dans quelles dimensions il s'exécute |
| `dimensionsAreBlacklist` | non | booléen                       | `false`              | Transforme cette liste en celles à éviter |
| `biomes`                 | non | liste de noms de biomes       | tous les biomes      | Dans quels biomes il s'exécute |
| `biomeTypes`             | non | liste de types du dictionnaire | aucun               | Les biomes par type, comme `FOREST` ou `NETHER` |
| `biomesAreBlacklist`     | non | booléen                       | `false`              | Transforme ces listes en celles à éviter |
| `minTemperature`         | non | flottant                      | `-100.0`             | Le biome le plus froid dans lequel il se générera |
| `maxTemperature`         | non | flottant                      | `100.0`              | Le biome le plus chaud dans lequel il se générera |
| `minRainfall`            | non | flottant                      | `-100.0`             | Le biome le plus sec dans lequel il se générera |
| `maxRainfall`            | non | flottant                      | `100.0`              | Le biome le plus humide dans lequel il se générera |
| `minDistanceFromSpawn`   | non | entier, en blocs              | `0`                  | À quelle distance du point d'apparition du monde il commence |
| `caveRegions`            | non | liste de noms de régions      | aucun                | Ne se génère qu'à l'intérieur de ces [régions de grottes](#régions-de-grottes) |

### Panneaux de surface et suiveurs

*entrées de worldgen*

| Clé | Obligatoire | Valeur | Défaut | Rôle |
| --- | --- | --- | --- | --- |
| `indicators`      | non | liste de `block=weight`              | aucun                | Des blocs laissés éparpillés en surface au-dessus d'un filon qui s'est généré, afin qu'un joueur puisse savoir ce qui se trouve sous terre ; choisissez-les pour qu'ils correspondent au contenu du filon. `empty=weight` laisse un endroit nu |
| `indicatorCount`  | non | entier ou intervalle                 | `1`                  | Combien d'endroits de surface chaque filon généré obtient |
| `indicatorSpread` | non | entier, en blocs                     | `0`                  | À quelle distance au-delà de l'emprise du filon un indicateur peut atterrir |
| `then`            | non | liste de `name=weight` ou d'objets   | aucun                | Les entrées de worldgen qui poussent à partir de celle-ci juste après sa génération, rattachées à elle : l'origine du suiveur est placée juste hors du bord de ce filon, dans la direction que donnent `thenSpread` et `thenDepth`, de sorte que les deux se touchent. Une entrée est `name=weight`, ou un objet avec `name`, `weight` et ses propres `spread` et `depth` (entier ou intervalle) qui remplacent ceux du filon pour ce seul suiveur, si bien qu'une même liste peut envoyer une pointe de diamant vers le bas et une branche sur le côté. Un nom seul est lu dans le namespace de ce pack, `empty=weight` ne met rien en file. Un suiveur garde sa propre forme, ses blocs, sa taille et son `replace`, mais ignore ses propres tentatives, sa probabilité, sa bande de hauteur et ses filtres de biome, et peut lui-même porter `then`, aussi profondément que le pack le souhaite ; une entrée déjà générée dans la même chaîne l'arrête |
| `thenCount`       | non | entier ou intervalle                 | `1`                  | Combien de suiveurs différents sont choisis dans cette liste par filon généré, chaque entrée au plus une fois : un nombre égal à la longueur de la liste fait donc pousser chacun d'eux |
| `thenSpread`      | non | entier, en blocs                     | le rayon de la forme | De combien la direction dans laquelle pousse un suiveur peut s'incliner latéralement, tirée de moins cette valeur à plus cette valeur |
| `thenDepth`       | non | entier ou intervalle                 | `0`                  | De combien la direction s'incline vers le bas (négatif) ou vers le haut. `0` sans inclinaison latérale fait pendre le suiveur droit vers le bas |
| `prospectAs`      | non | chaîne                               | le nom du fichier    | Comment un objet de prospection nomme cette entrée dans sa lecture, par ex. `Hematite` |

### Retrogen et prérequis

*entrées de worldgen*

| Clé | Requis | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `retrogen` | non | booléen | `false` | Génère aussi dans les chunks déjà existants |
| `retrogenKey` | non | chaîne | la clé de la configuration | Remplace la clé de retrogen pour cette seule entrée |
| `requires` | non | liste d'ids de mods ou de namespaces de pack | aucun | L'entrée est ignorée sauf si tous sont présents |

### Blocs pondérés

*entrées de worldgen*

`blocks` remplace `block` quand une seule entrée ne suffit pas. Les poids sont relatifs : 80 et 20 donnent donc quatre contre un.

```json
{
  "blocks": [
    { "block": "minecraft:wool", "meta": 2, "weight": 80 },
    { "block": "minecraft:wool", "weight": 20, "properties": { "color": "lime" } }
  ]
}
```

| Clé | Requis | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `block` | oui | nom de bloc | | Ce qui est placé |
| `meta` | non | entier | `0` | Quelle variante |
| `weight` | non | entier | `1` | Fréquence de choix de celui-ci par rapport aux autres |
| `properties` | non | objet de propriété vers valeur | aucun | Propriétés d'état de bloc par nom, pour les états sans métadonnée propre |

`block` et `meta` restent obligatoires au niveau supérieur du fichier même quand `blocks` est utilisé ; la première entrée y est une bonne valeur.

### Cibles de remplacement

*entrées de worldgen*

`replace` est une liste, et chaque entrée prend l'une des trois formes.

```json
{
  "replace": [
    "minecraft:stone",
    "minecraft:stone:3",
    { "block": "minecraft:stone", "properties": { "variant": "andesite" } },
    { "block": "minecraft:stone", "meta": 5 }
  ]
}
```

| Forme | Exemple | Ce qu'elle cible |
| --- | --- | --- |
| Nom | `"minecraft:stone"` | Tous les états de ce bloc |
| Nom et métadonnée | `"minecraft:stone:3"` | Uniquement cette métadonnée, ici la diorite |
| Objet | `{ "block": "minecraft:stone", "properties": { "variant": "andesite" } }` | Uniquement cet état |

La forme objet accepte aussi `meta` à la place de `properties`, ce qui équivaut à la forme avec deux-points. Utilisez `"minecraft:air"` pour générer dans l'espace libre.

### Blocs adjacents

*entrées de worldgen*

`adjacent` accepte les trois mêmes formes que `replace` et ajoute une seconde condition par-dessus : l'emplacement n'est utilisé que si au moins un des 26 blocs qui le touchent, par les faces, les arêtes et les coins, correspond à la liste. S'il est omis, rien n'est vérifié.

```json
{
  "block": "mypack:sulfur_ore",
  "replace": ["minecraft:sandstone"],
  "adjacent": ["minecraft:air"]
}
```

Cela place du soufre dans le grès uniquement là où il est déjà ouvert sur une grotte ou sur la surface, et laisse en paix le grès enfoui. Les voisins situés dans des chunks qui n'existent pas encore sont considérés comme ne correspondant pas, au lieu d'être lus, de sorte que la vérification ne provoque jamais la génération d'un chunk.

Toutes les formes la respectent, puisqu'elle fait partie de la décision de savoir si un bloc isolé peut être pris. Une `geode` nomme séparément sa croûte et son remplissage, et ces deux-là sont placés sans cette vérification.

Une entrée qui ne nomme que des blocs non enregistrés est ignorée avec une erreur au lieu de se générer partout.

### Entrées de suiveurs

*entrées de worldgen*

Une entrée de la liste `then` d'une entrée de worldgen est un nom accompagné d'un poids, ou un objet quand ce suiveur a besoin d'une direction qui lui est propre.

```json
{
  "then": [
    "mypack:quartz_halo=2",
    "empty=1",
    { "name": "mypack:side_branch", "weight": 1, "spread": 4, "depth": 0 }
  ]
}
```

| Clé | Requis | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `name` | oui | nom d'entrée | | L'entrée de worldgen qui pousse à partir de celle-ci. Un nom seul est lu dans le namespace de ce pack |
| `weight` | non | entier | `1` | Fréquence de choix de ce suiveur par rapport aux autres de la liste |
| `spread` | non | entier, en blocs | le `thenSpread` de l'entrée | De combien la direction de ce suiveur peut dévier latéralement, pour cette entrée seulement |
| `depth` | non | entier ou intervalle | le `thenDepth` de l'entrée | De combien la direction de ce suiveur penche vers le bas (négatif) ou vers le haut, pour cette entrée seulement |

`name=weight` est la forme courte d'un objet qui ne contient que ces deux clés, et `empty=weight` ne met rien en file d'attente. Comme `spread` et `depth` sont propres à chaque entrée, une même liste peut envoyer une pointe de diamant droit vers le bas et une branche sur le côté à partir du même filon.

## Formes

*générer le monde*

Un bloc `shape` avec un `type`. Les clés non listées pour un type sont ignorées par celui-ci.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin. Une clé marquée pour un seul type n'est lue que par ce type.

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
    "surface": ["minecraft:grass"],
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

| Type | Ce qu'il crée |
| --- | --- |
| `cluster` | L'amas par défaut, un filon de minerai. Utilise `size` |
| `largevein` | Un long filon sinueux avec des ramifications. Utilise `size` |
| `plate` | Un disque plat |
| `geode` | Une poche creuse avec une croûte |
| `decoration` | Un semis en surface, comme des fleurs ou des champignons. Utilise `size` |
| `tree` | Un arbre entier |
| `vines` | Des lianes sur ce qui est déjà là. Utilise `size` |
| `basin` | Une cuvette qui s'approfondit vers le milieu |
| `spire` | Une colonne qui s'effile |
| `nodule` | Une boule irrégulière |
| `vent` | Une colonne étroite qui s'arrête dès qu'elle heurte quelque chose |
| `imprint` | Un de vos modèles `.nbt`. Un modèle qui tient dans un chunk est décalé pour atterrir en entier dans le chunk en cours de construction, au lieu d'empiéter sur un voisin qui n'a pas encore été créé, quelle que soit son orientation ; un modèle plus grand qu'un chunk n'est placé que là où le terrain environnant existe déjà |
| `belt` | Un amas couvrant plusieurs chunks, pour les régions de pierre |
| `field` | Des filons calculés pour tous les blocs à la fois, partageant leur forme avec les groupes de dureté |
| `vein` | Un gisement calculé comme un champ de bruit à graine autour d'une origine, à la manière d'Immersive Geology : chaque chunk écrit sa propre tranche de chaque filon dont la portée de 24 blocs l'atteint, donc rien ne se propage en cascade, et `/rdplserver vein` peut dire où se trouvera un filon avant que le terrain soit créé. Utilise `size`, `attempts`, `rarity` et la bande d'altitude ; `pattern` choisit l'apparence |
| `spring` | Un fluide qui suinte d'une paroi de grotte : placé là où il y a de la roche au-dessus, en dessous et sur trois côtés, avec un côté ouvert, et mis en écoulement |

### Taille et forme

*formes*

| Clé | Utilisé par | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `type` | tous | l'une des formes ci-dessus | `cluster` | Quelle forme |
| `radius` | plate, geode, basin, spire, nodule, vent | entier ou intervalle | `6` | Sa largeur |
| `height` | plate, geode, basin, spire, vent, tree | entier ou intervalle | `1`, `8` pour geode, `5` pour tree | Sa hauteur ou son épaisseur |
| `width` | geode | entier ou intervalle | `12` | L'étendue totale de la poche |
| `plane` | plate, basin, spire, vent | `circle`, `square` | `circle` | Son empreinte au sol |
| `slim` | plate, largevein, nodule | booléen | `false` | Plate : une couche de moins. Largevein : branches d'un seul bloc. Nodule : coque creuse |
| `hanging` | spire, vent | booléen | `false` | Pousse vers le bas depuis un plafond au lieu de monter depuis un sol |
| `taper` | spire | `straight`, `bell`, `needle` | `straight` | Comment la largeur diminue vers la pointe. `straight` s'amincit régulièrement, `bell` garde sa largeur en bas puis chute, `needle` s'affine d'emblée en une longue pointe |
| `outline` | geode | nom de bloc | aucun | Le bloc de la croûte |
| `fill` | geode | nom de bloc | aucun | Ce qui remplit le milieu. S'il est omis, le milieu est creux |
| `middle` | geode | nom de bloc | aucun | Une coque entre le corps et `outline`, la calcite d'une géode d'améthyste moderne |
| `budding` | geode | nom de bloc | aucun | Substitué aux blocs du corps qui font face au milieu creux, comme l'améthyste bourgeonnante. Nécessite `fill` |
| `buddingChance` | geode | 0.0 à 1.0 | `0.083` | Quelle part de ces blocs du corps bourgeonne |
| `crystal` | geode | nom de bloc | aucun | Qui pousse dans le creux à côté d'un bloc `budding`, comme un amas d'améthyste |
| `crystalChance` | geode | 0.0 à 1.0 | `0.35` | Quelle part de ces emplacements en voit pousser un |
| `crack` | geode | 0.0 à 1.0 | `0` | La probabilité qu'une géode soit ouverte : un tube allant du milieu vers l'extérieur à travers chaque couche d'un côté, rempli avec `fill`. Les géodes d'améthyste modernes utilisent `0.95` |

### Placement

*formes*

| Clé | Utilisé par | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `surface` | decoration, tree | liste de noms de blocs | aucun | Ce sur quoi il se posera |
| `seeSky` | decoration | booléen | `true` | Ne place que là où le ciel est visible |
| `checkStay` | decoration | booléen | `true` | Ne place que là où le bloc survivrait |
| `stackHeight` | decoration | entier ou intervalle | `1` | Combien en empiler les uns sur les autres |
| `scatterX` | decoration, tree | entier | `8` | Jusqu'où il s'écarte latéralement |
| `scatterY` | decoration, tree | entier | `4` | Jusqu'où il s'écarte verticalement |
| `scatterZ` | decoration, tree | entier | `8` | Jusqu'où il s'écarte latéralement |
| `rarity` | toutes | entier | aucun (`400` pour belt) | Un placement par ce nombre de chunks. Sur une ceinture, cela espace les ceintures ; sur toute autre forme, cela filtre l'entrée entière, de sorte qu'un seul chunk sur ce nombre tente ses `attempts`. `field` l'ignore |
| `rarityIsPerChunk` | toutes | booléen | `false` | Transforme `rarity` en nombre de placements par chunk |

### Arbres

*formes*

```json
{
  "shape": { "type": "tree", "log": "mypack:ruby_log", "leaves": "mypack:ruby_leaves", "height": { "min": 4, "max": 7 }, "surface": ["minecraft:grass"] }
}
```

Un `tree` sans `log` ni `leaves` ne génère rien, et le dit dans le journal. Nommer une `structure`, ou plusieurs sous `structures`, plante ce modèle à chaque emplacement au lieu d'y faire pousser un arbre, et ni `log` ni `leaves` ne sont alors nécessaires ; un arbre issu d'un modèle lit `turns`, `mirrors`, `integrity`, `lootTable` et `locateAs` exactement comme le fait un `imprint`.

| Clé | Utilisé par | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `log` | tree | nom de bloc | aucun | Le bloc du tronc |
| `leaves` | tree | nom de bloc | aucun | Le bloc de feuillage |
| `vines` | tree | booléen | `false` | Fait pendre des lianes sous les feuilles |

### Placer des modèles

*formes*

| Clé | Utilisé par | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `structure` | imprint, tree | `namespace:name` | aucun | Le modèle à placer |
| `integrity` | imprint, tree | 1 à 100 | `100` | Pourcentage des blocs du modèle qui apparaissent réellement |
| `lootTable` | imprint, tree | `namespace:path` | aucun | La table de butin dont est rempli chaque coffre du modèle placé à sa première ouverture, ainsi que tout autre conteneur qui en accepte une, boîte de shulker ou caisse d'un mod comprises. Couvre `structure` et chaque entrée de `structures` ; chaque coffre tire sa propre graine |
| `structures` | imprint, tree | liste | aucun | Plusieurs modèles au choix, un seul placé à chaque fois. Chaque entrée s'écrit `{ "structure": "namespace:name", "weight": 3 }`, ou un simple nom pour des chances égales. Remplace `structure` |
| `turns` | imprint, tree | liste | toutes | Dans quel sens il peut être placé : `none`, `quarter`, `half`, `threequarter`. Les entrées peuvent porter un `weight`. S'il est omis, les quatre sont équiprobables |
| `mirrors` | imprint, tree | liste | aucun | Le retourner aussi : `none`, `leftright`, `frontback`, avec `weight` facultatif. Une entrée qui nomme son propre poids s'écrit `{ "mirror": "leftright", "weight": 2 }`, et une entrée de `turns` de même avec `turn` |
| `at` | imprint | deux entiers, x et z | aucun | Place une seule fois, exactement à ces coordonnées de bloc en surface, quand ce chunk se génère, au lieu d'un tirage aléatoire. Voir [Structures à des endroits précis](#structures-à-des-endroits-précis) |
| `locateAs` | imprint, tree | chaîne | aucun | Enregistre chaque structure placée par cette entrée sous ce nom, pour que `/locate <name>` trouve la plus proche. Voir [Retrouver les structures placées](#retrouver-les-structures-placées) |

Pour une forme qu'aucun type intégré ne couvre, `imprint` est la solution : construisez-la comme un modèle `.nbt` et placez celui-ci, avec `structures` pour le varier, `turns` et `mirrors` pour le tourner, et `integrity` pour le dissoudre en quelque chose de plus rugueux que le fichier que vous avez dessiné.

### Structures à des endroits précis

*formes*

Les structures vanilla se fixent à des endroits exacts avec `structureAt` dans les réglages `terrain`, sous forme d'entrées `structure=x,z`, une par ligne : `"structureAt": ["villages=1000,-500"]`. **Les x et z sont des coordonnées de bloc, pas de chunk**, et la structure se génère dans le chunk qui contient ce bloc ; le puits d'un village se dresse sur ce bloc même, tandis que les autres structures démarrent là où le jeu les ferait démarrer dans ce chunk. Une entrée par instance voulue. Son espacement, sa séparation, sa distance minimale d'apparition et ses vérifications de terrain plat sont tous mis de côté, donc l'endroit relève de la responsabilité du pack, et deux points fixés à moins d'un chunk l'un de l'autre mettent deux structures dans le même chunk. Une fois fondée, la structure s'ancre au sol de son chunk selon les règles habituelles.

| Réglage | Type | Défaut | Effet |
| --- | --- | --- | --- |
| `structureAt` | liste de `structure=x,z` | aucun | Fixe une structure vanilla à un endroit exact, une entrée par instance voulue. Les x et z sont des coordonnées de bloc, et la structure se génère dans le chunk qui contient ce bloc ; son espacement, sa séparation, sa distance minimale d'apparition et ses vérifications de terrain plat sont tous mis de côté |

Une entrée `imprint` se fixe de la même façon avec `"at": [x, z]` dans sa forme, se plaçant exactement une fois à ces coordonnées en surface quand ce chunk se génère, au lieu d'un tirage aléatoire. Elle se combine avec `locateAs`, de sorte qu'une structure fixée peut aussi être retrouvée avec /locate.

### Retrouver les structures placées

*formes*

Une entrée `imprint` avec `"locateAs": "Crypt"` enregistre chaque structure qu'elle place sous ce nom, et `/locate Crypt` désigne alors la plus proche, le nom étant proposé dans la complétion par tabulation. Seules les structures déjà générées peuvent être retrouvées, puisque les structures de pack sont placées au hasard à mesure que les chunks sont créés, et non sur une grille que le jeu pourrait prédire. Les noms sont conservés dans la sauvegarde du monde, donc ils survivent aux redémarrages et fonctionnent sur les serveurs. Un nom enregistré ainsi peut aussi recevoir sa propre permission avec `gotoPlaceLevels`, afin qu'un pack décide qui peut être emmené vers ses propres structures séparément de celles de vanilla.

### Clés de champs et de filons

*formes*

| Clé | Utilisé par | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `field` | field | objet | `{ "type": "speckle" }` | Comment le champ est calculé. Mêmes clés que le `field` d'un groupe de dureté, décrites sous [Le champ](#le-champ) : `speckle` avec `chances` et `spread`, ou `seeded` avec `cell`, `seeds`, `reach`, `arms` et `armReach` |
| `threshold` | field, vein | 0.0 à 1.0 | `0.5` (`0.4` pour vein) | Quelle intensité le champ doit atteindre en un bloc pour qu'il soit placé. Plus bas, cela remplit davantage |
| `fade` | field | entier | `0` | Éparpille le haut de la bande au lieu de la terminer net : sur ce nombre de blocs au sommet de l'intervalle d'altitude, la probabilité de placement de chaque bloc diminue progressivement, le même aspect que le moteur donne à `deepStone` là où il rejoint le monde au-dessus |
| `pattern` | vein | `default`, `banded` ou `tube` | `default` | L'apparence du gisement : un amas déformé, des strates empilées tous les quelques blocs, ou des tubes creux serpentant dans la roche |
| `density` | vein | 0.0 à 1.0 | `1.0` | La part des blocs éligibles qui sont réellement placés, un tirage à pile ou face par bloc |
| `rich` | vein | nom de bloc | aucun | Placé dans le cinquième supérieur de l'intervalle du champ au-dessus de `threshold`, le cœur du gisement, à la place des blocs de l'entrée |
| `poor` | vein | nom de bloc | aucun | Placé dans les deux cinquièmes inférieurs de cet intervalle, la frange, à la place des blocs de l'entrée ; le milieu est constitué des blocs de l'entrée. Si l'un des paliers est omis, les blocs de l'entrée y sont placés |
| `richAt` | vein | 0.0 à 1.0 | `0.88` | Où le palier riche commence dans cet intervalle : `0.88` limite le bloc riche au huitième le plus intense du gisement, un nombre plus bas épaissit le cœur riche, `1.0` ne laisse aucun bloc riche |
| `poorAt` | vein | 0.0 à 1.0 | `0.4` | Où commencent les blocs propres à l'entrée : en dessous, le bloc `poor` est placé, donc `0.4` donne une frange des deux cinquièmes inférieurs et `0.0` ne laisse aucune frange pauvre. Borné à `richAt` |

### Ceintures

*formes*

Une `belt` est une boule bien plus grande qu'un chunk, utilisée pour des régions de pierre plutôt que pour des filons de minerai. Son `radius` est la taille de la boule, et chaque chunk calcule lui-même où commencent les boules proches de lui, à partir de la graine du monde et du nom de l'entrée, de sorte qu'une ceinture ressort entière quelle que soit la façon dont les chunks sont générés et que rien n'est jamais écrit dans un chunk voisin.

```json
{
  "shape": { "type": "belt", "radius": 32, "rarity": 400 }
}
```

Une ceinture ignore `attempts` et `spread`, puisqu'elle est placée par chunk et non par tentative. `minHeight` et `maxHeight` forment la bande où se trouvent les centres, et la boule s'étend de `radius` au-delà de cette bande. `replace` décide de ce qu'elle dévore, `biomes` et les limites de température et de précipitations sont vérifiés au centre, de sorte qu'une ceinture apparaît soit entière, soit pas du tout, au lieu d'être coupée à la frontière d'un biome.

Le coût croît avec le cube de `radius`, et une `rarity` basse le multiplie, donc partez des valeurs par défaut et augmentez le rayon lentement.

### Champs

*formes*

Un `field` ne place rien en un point et tout à la fois. Au lieu de choisir un endroit et de construire une forme autour, il pose une question à chaque bloc du chunk, entre `minHeight` et `maxHeight`, et place là où la réponse atteint au moins `threshold`. La question est la même que celle des groupes de dureté, de sorte que les deux décrivent les mêmes filons, et qu'un pack peut créer un groupe et une entrée qui concordent.

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

| Clé | Requis | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `threshold` | non | 0.0 à 1.0 | `0.5` | Quelle intensité le champ doit atteindre avant qu'un bloc soit placé |
| `field` | oui | objet | aucun | Le même objet que celui d'un groupe de dureté, avec les mêmes types `speckle` et `seeded` |

Un `threshold` bas prend la majeure partie du champ et donne de larges veines, un `threshold` élevé ne prend que le centre de chaque amas et donne de petites poches dispersées. Avec `speckle`, vous obtenez de nombreuses minuscules taches ; avec `seeded`, des plaques plus rondes ou, dès qu'il a des bras, des nœuds dont les tentacules s'étendent entre eux.

Comme une ceinture, un champ ignore `attempts` et `spread`, puisqu'on l'interroge par chunk et non par tentative, et il n'écrit jamais dans un chunk voisin. Il est calculé à partir de la graine du monde et du nom de l'entrée, donc la même graine donne toujours les mêmes filons, et deux entrées de noms différents ne s'alignent jamais. `replace`, `adjacent`, `biomes` et les limites climatiques s'appliquent comme d'habitude.

Un filon `field` est la seule forme que l'on décrit au lieu de la choisir. Il exécute le même réseau que celui des groupes de dureté, donc `seeded` avec quelques bras donne des nœuds dont les tentacules s'étendent vers leurs voisins, ce qui fait un filon plutôt qu'un amas, et `threshold` décide quelle part est assez solide pour être placée :

```json
{
  "shape": {
    "type": "field",
    "threshold": 0.4,
    "field": { "type": "seeded", "cell": 10, "reach": 6.0, "arms": 3, "armReach": 5.0 }
  }
}
```

Les clés vont dans un objet `field` à part, et non à côté de `type`, puisque le `type` de la forme dit déjà `field`.

## Propagations

*générer le monde*

Un bloc `spread` avec un `type`.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin. Une clé marquée pour un seul type n'est lue que par ce type.

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

| Type | Où il place les éléments |
| --- | --- |
| `even` | N'importe où entre les altitudes, uniformément. La valeur par défaut |
| `centered` | Pondéré vers une altitude, s'amenuisant avec la distance |
| `sprawl` | Des filons fractals couvrant un intervalle d'altitude |
| `terrain` | En suivant la surface |
| `cavern` | Sur les sols ou les plafonds des grottes |
| `submerged` | Sous l'eau ou un autre fluide |

| Clé | Utilisé par | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `type` | tous | l'une des propagations ci-dessus | `even` | Quelle propagation |
| `center` | centered | entier | milieu de l'intervalle d'altitude | L'altitude autour de laquelle il se regroupe |
| `range` | centered | entier | la moitié de l'intervalle d'altitude | À quelle distance de cette altitude il s'étend |
| `smoothness` | centered | 1 à 8 | `2` | Combien de tirages sont moyennés. Plus c'est élevé, plus la bande est resserrée |
| `veinHeight` | sprawl | entier | l'intervalle d'altitude | La hauteur d'un filon |
| `veinDiameter` | sprawl | entier | `12` | La largeur d'un filon |
| `verticalDensity` | sprawl | 1 à 100 | `16` | Sa solidité verticale |
| `horizontalDensity` | sprawl | 1 à 100 | `32` | Sa solidité horizontale |
| `offsetMin` | terrain | entier | `0` | Décalage le plus bas par rapport à la surface |
| `offsetMax` | terrain | entier | `offsetMin` | Décalage le plus haut par rapport à la surface |
| `ceiling` | cavern | booléen | `false` | S'accroche au plafond de la grotte plutôt qu'au sol |

## Cartes de structures

*générer le monde*

Une carte de structures assemble des modèles en un seul bâtiment nommé sur une grille, bien au-delà de la limite de 32 blocs d'un simple fichier `.nbt`. Chaque couche est dessinée en rangées de caractères uniques, un caractère par cellule, et s'empile d'une hauteur de cellule au-dessus de la couche précédente. Au plus 8 couches de 8 sur 8 cellules, ce qui, avec la cellule par défaut de 32, fait 256 blocs de côté, la hauteur de construction de vanilla.

`<namespace>/structuremaps/*.json`

```json
{
  "name": "Castle",
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

| Réglage | Type | Défaut | Effet |
| --- | --- | --- | --- |
| `name` | texte | le nom du fichier | Comment la carte est appelée dans les journaux |
| `cell` | nombre | `32` | Le pas de la grille en blocs, jusqu'à 48. Un modèle plus petit que la cellule se place au coin de la cellule, de sorte que les pièces de pleine taille se raccordent sans couture |
| `ground` | nombre | `0` | Quelle couche repose sur la surface du terrain. Les couches précédentes creusent vers le bas, ce qui donne des sous-sols au bâtiment |
| `at` | deux nombres | aucun | Fixe une copie à des coordonnées de bloc exactes, comme `structureAt` fixe un village |
| `spacing` | nombre | `0` | Disperse des copies sur une grille espacée de ce nombre de chunks, avec un décalage aléatoire tiré de la graine du monde. `0` n'en disperse aucune, donc une carte avec seulement `at` ne se construit qu'une fois |
| `chance` | nombre | `100` | Le pourcentage des emplacements de la grille qui construisent une copie |
| `dimensions` | liste | toutes | Les ids de dimension où la carte peut se construire |
| `layers` | liste | aucun | Les couches, de bas en haut, chacune avec une `palette` et une `map` |

Une palette nomme des modèles par clé de registre à partir du dossier `<namespace>/structures/` d'un pack.

| Valeur | Effet |
| --- | --- |
| `"a": "mypack:keep"` | Chaque cellule `a` de cette couche place ce modèle |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | Chaque cellule `a` tire dans la liste selon les poids, à partir de la graine du monde et de l'emplacement de la cellule, de sorte que deux copies du bâtiment diffèrent mais qu'un même monde construit toujours le même |
| `.` | Une cellule vide, rien n'est placé |

Chaque copie tire l'une des quatre orientations à partir de la graine du monde et le bâtiment entier tourne d'un bloc, modèles compris, de sorte que les murs qui se rejoignent d'une cellule à l'autre se rejoignent toujours. La couche de sol repose sur la surface du terrain échantillonnée sous le milieu du bâtiment. Chaque chunk ne construit que sa propre tranche de la grille, donc un bâtiment qui s'étend sur de nombreux chunks apparaît sans génération en cascade, quel que soit l'ordre de chargement des chunks. Une [parcelle de village](#parcelles-de-village) de type `template` peut aussi nommer une carte comme `structure`, ce qui fait du composite un bâtiment de village.

## Parcelles de village

*générer le monde*

`<namespace>/villages/*.json`

Le chemin du fichier est le nom de la parcelle, que `villagePieces` peut ensuite nommer pour la garder ou l'écarter.

Un fichier ici ajoute une pièce que les villages peuvent construire, en plus de celles de vanilla. Deux sortes, choisies avec `type`.

Toutes les clés, montrées d'un coup. Un vrai fichier n'écrit que celles dont il a besoin. Une clé marquée pour un seul type n'est lue que par ce type.

```json
{
  "type": "farm",
  "weight": 3,
  "leastCount": 1,
  "mostCount": 4,
  "width": 7,
  "height": 4,
  "depth": 9,
  "crops": ["simplecorn:corn", "minecraft:wheat"],
  "edge": "minecraft:log",
  "soil": "minecraft:farmland",
  "water": true,
  "rowWidth": 2,
  "structure": "mypack:blacksmith_shed",
  "integrity": 100,
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

| Clé | Utilisé par | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `type` | toutes | `farm` ou `template` | `farm` | Quelle sorte de parcelle |
| `weight` | toutes | entier | `3` | Fréquence de choix de cette parcelle par rapport aux autres du pack |
| `leastCount` | toutes | entier | `1` | Minimum par village, avant ajout de la taille du village |
| `mostCount` | toutes | entier | `4` | Maximum par village, avant ajout de la taille du village |
| `width` | toutes | entier | `7` | Taille dans le sens du chemin |
| `height` | toutes | entier | `4` | Hauteur dégagée au-dessus du sol |
| `depth` | toutes | entier | `9` | Taille en s'éloignant du chemin |
| `apron` | toutes | entier | `2` | De combien le sol peut différer du niveau de la route sous la parcelle avant qu'elle soit refusée ou glissée le long de sa route : ce nombre de blocs de remblai dessous, ou de déblai dans une hauteur au-dessus, et pas plus que cela entre son coin le plus haut et le plus bas. Une grande parcelle en terrain vallonné en demande davantage. Réglé haut, la parcelle se met en terrasses droit dans une pente, ce qui, au mauvais endroit, dévore une montagne |
| `ground` | toutes | nom de bloc | `minecraft:dirt` | Ce qui est tassé dessous sur une pente |
| `requires` | toutes | liste d'ids de mods ou de namespaces de pack | aucun | La parcelle est écartée sauf si tous sont présents |

Chaque parcelle de pack est proposée aux villages comme une seule entrée, donc `weight` décide laquelle de vos parcelles est choisie quand un village en demande une. La parcelle utilisée par un placement est inscrite dans les données propres du village, de sorte qu'il se reconstruit correctement au chargement.

### Fermes

*parcelles de village*

Une `farm` est le champ de vanilla, décrit plutôt que codé : une parcelle de la taille demandée, bordée d'un bloc, remplie de rangées de terre séparées par des canaux d'eau, plantée d'une culture tirée par bloc dans votre liste.

```json
{
  "type": "farm",
  "weight": 3,
  "width": 7,
  "depth": 9,
  "crops": ["simplecorn:corn"],
  "edge": "minecraft:log",
  "water": true,
  "rowWidth": 2
}
```

| Clé | Utilisé par | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `crops` | farm | liste de noms de blocs | blé | Plantées une par bloc, à un stade de croissance aléatoire |
| `edge` | farm | nom de bloc | `minecraft:log` | Le cadre autour de la parcelle |
| `soil` | farm | nom de bloc | `minecraft:farmland` | De quoi sont faites les rangées |
| `water` | farm | booléen | `true` | Met un canal d'eau entre les rangées |
| `rowWidth` | farm | entier | `2` | La largeur de chaque rangée de terre |

### Construit à partir de modèles

*parcelles de village*

Un `template` place à la place une de vos structures `.nbt`, tournée pour faire face au chemin du village.

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

Un `template` dont la `structure` nomme l'une de vos [cartes de structures](#cartes-de-structures) place le composite entier comme parcelle. La taille de la parcelle vient alors de la carte, son empreinte et ses couches empilées multipliées par la cellule, donc `width`, `height`, `depth` et `integrity` ne sont pas lus. Les couches précédant le `ground` de la carte creusent vers le bas comme sous-sols, et les cellules de palette pondérées sont toujours tirées pour chaque bâtiment, de sorte que deux tours issues de la même carte peuvent différer.

```json
{
  "type": "template",
  "weight": 2,
  "structure": "mypack:castle",
  "villagers": 4
}
```

| Clé | Utilisé par | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `structure` | template | `namespace:name` | aucun | Le modèle à placer, ou l'une de vos cartes de structures, qui fixe alors la taille de la parcelle |
| `integrity` | template | 1 à 100 | `100` | Pourcentage des blocs du modèle qui apparaissent |
| `lootTable` | template | `namespace:path` | aucun | La table de butin dont est rempli chaque coffre du modèle placé à sa première ouverture. Une parcelle qui nomme une carte de structures n'est pas touchée |
| `villagers` | toutes | entier | `0` | Combien de personnes la parcelle fait apparaître |
| `villagerEntity` | toutes | `namespace:name` | un villageois | Qui y habite, par exemple une variante d'entité de votre cru |
| `villagerX` | toutes | entier | `1` | Où ils apparaissent, dans la largeur de la parcelle |
| `villagerY` | toutes | entier | `1` | Où ils apparaissent, au-dessus du sol |
| `villagerZ` | toutes | entier | `1` | Où ils apparaissent, en profondeur dans la parcelle |

## Cartes de plan des villes

*générer le monde*

Une carte de ville dessine le plan des rues d'un village sur une grille, un caractère par cellule, et le village est aménagé d'après le dessin au lieu de croître. Les rues, les places et les parcelles ressortent sous forme des mêmes pièces qu'un village qui a grandi, de sorte que chaque option de route, pont, ponton, impasse, lampadaire, cul-de-sac et pièce maîtresse de place s'applique sans changement. Le modèle de monde nomme la carte dans `villageLayout`.

`<namespace>/citymaps/*.json`

```json
{
  "name": "Downtown",
  "cell": 48,
  "settings": { "villagePathCenterBlock": "minecraft:concrete:14" },
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

| Réglage | Type | Défaut | Effet |
| --- | --- | --- | --- |
| `name` | texte | le nom du fichier | Comment la carte est appelée dans les journaux |
| `cell` | nombre | `48` | Le pas de la grille en blocs, de 8 à 128. Les routes passent au milieu de leurs cellules à la largeur de route du pack et les parcelles sont centrées dans les leurs, donc une cellule doit accueillir la plus large parcelle plus de la place pour donner sur la rue |
| `palette` | objet | aucun | Ce que pose chaque caractère, listé ci-dessous |
| `map` | liste | aucun | Les rangées, jusqu'à 64 sur 64 cellules. Une rangée plus courte que la plus large est ouverte au-delà de sa fin |
| `settings` | objet | aucun | Réglages de village pour cette seule carte, sous les noms qu'utilise un modèle de monde, comme `villagePathCenterBlock`. Ils l'emportent sur ceux du modèle, et les réglages de village propres à un biome l'emportent encore sur eux |

| Valeur | Effet |
| --- | --- |
| `"#": "street"` | Une suite de cellules de rue le long d'une rangée ou d'une colonne devient une seule boîte de route à la largeur de route du pack. Là où une suite en rangée croise une suite en colonne, le carrefour est peint comme n'importe quel autre. Une cellule de rue isolée, sans suite dans aucun des deux axes, est posée comme un court tronçon le long de la rangée |
| `"+": "plaza"` | Un puits avec son anneau de place. Les suites traversent les cellules de place, donc les rues se rejoignent au puits, et une place sur un croisement dresse son puits, ou sa pièce maîtresse `villageWellStructure`, au milieu du carrefour comme un rond-point. La première place du fichier est le puits propre du village, ce qui fixe la carte à l'endroit où le village se fonde ; une carte sans place est centrée à cet endroit |
| `"a": "alley"` | Une voie étroite. Les bâtiments y donnent, mais elle ne relie rien, la règle des ruelles comme d'habitude |
| `"J": "junction"` | Une cellule de rue posée dans les deux sens, de sorte qu'un croisement s'y dresse même quand le dessin ne la traverse que dans un sens. Le bras qui la traverse mesure une cellule |
| `"b": "bulb"` | Une cellule de rue qui se termine en cul-de-sac. Dès qu'une carte possède une cellule bulb, seuls les bouts de route situés dans des cellules bulb reçoivent un bulbe, et tous ceux qui ont la place en reçoivent un ; une carte sans cellule bulb en garde trois bouts sur quatre |
| `"E": { "kind": "elevated", "height": 8 }` | Une cellule de rue surélevée sur un tablier de `height` blocs, de 2 à 64, au-dessus du sol le plus haut sous son tronçon de cellules surélevées jointes, avec une rampe d'un bloc par rangée à chaque bout. Un croisement de rues à l'intérieur du tronçon monte avec lui. Un tronçon dont le tablier ou les rampes atteindraient une rangée qu'une voie ferrée ou un puits tient à son propre niveau reste au niveau du sol, avec une ligne dans le journal. N'importe quelle valeur peut s'écrire ainsi sous forme d'objet, `kind` nommant le mot |
| `"W": { "kind": "street", "settings": { "villagePathExtraWidth": 8 } }` | Une rue posée et pavée avec ses propres clés de route, qui l'emportent sur celles de la carte et du modèle. Sa largeur suit ses propres `villagePathExtraWidth`, `villagePathSidewalkWidth` et ligne, et sa surface, ses lignes et ses trottoirs suivent ses propres clés de blocs, de sorte qu'une avenue ou une venelle se dessine avec un marquage propre. Une suite prend les clés de sa première cellule qui en définit. Aussi large ou étroite soit-elle, une rue dessinée reste une rue : elle n'est jamais prise pour une ruelle ou un cul-de-sac |
| `"T": "mypack:tower"` | Une cellule de parcelle, posée d'après cette définition de parcelle, centrée dans la cellule et tournée vers la rue la plus proche |
| `"T": ["mypack:a=3", "mypack:b=1"]` | Pareil, tiré selon les poids à partir de la graine du monde et de l'emplacement de la cellule, de sorte qu'un même monde pose toujours la même parcelle à cet endroit |
| `"g": "grow"` | Laissée à la croissance. Avec `villagePlotsLeast` défini, les quartiers qui ont grandi et le remplissage des rues comblent ces cellules et s'étendent vers l'extérieur depuis la carte ; sans lui, la cellule reste ouverte |
| `.` | Terrain ouvert, rien n'est posé |

Chaque carte tire l'une des quatre orientations à partir de la graine du monde et tourne d'un bloc, de sorte qu'un plan se lit de la même façon de tous les côtés. Les routes sont posées en premier, donc une parcelle qui chevaucherait une route ou une autre parcelle est laissée ouverte avec une ligne dans le journal, et un nom de parcelle qu'aucun pack ne fournit laisse sa cellule ouverte de la même façon. La carte ne change pas la façon dont les pièces sont habillées : les clés de route, `villageBlocks`, les lampadaires et le remplacement du puits se lisent tous comme pour un village qui a grandi. Rien ne pousse à partir d'une carte dessinée : aucune ruelle n'est comblée à côté de ses rues, et ses bouts de route reçoivent leurs bulbes, trois sur quatre comme d'habitude ou selon ses cellules bulb, mais sans maisons le long.

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

| Réglage | Type | Défaut | Effet |
| --- | --- | --- | --- |
| `retrogen` | booléen | `false` | Rattrape les chunks sauvegardés avant l'existence d'une entrée, pour chaque entrée de worldgen marquée `"retrogen": true`. Désactivé, les chunks déjà existants sont laissés tels quels |
| `adoptExistingChunks` | booléen | `false` | Ce qui se passe la première fois qu'un ancien chunk est vu : activé, il est marqué comme si ce pack l'avait déjà généré et n'est jamais rattrapé ; désactivé, il est rattrapé comme n'importe quel autre. Pour remplir un monde existant, activez `retrogen` et désactivez ceci |

Une entrée avec `"retrogen": true` est générée dans les chunks qui ont été sauvegardés avant son ajout. Chaque chunk enregistre ce qu'il a reçu, donc rien n'est fait deux fois.

Le drapeau de l'entrée marque seulement une entrée comme éligible. Le rattrapage est activé par le réglage `retrogen`, qu'un pack peut définir dans son bloc `settings` ou qu'un joueur peut définir dans la configuration, et il est désactivé par défaut. En complément, `adoptExistingChunks` décide de ce qui se passe la première fois qu'un ancien chunk est vu : activé, le chunk est marqué comme si ce pack l'avait déjà généré et n'est jamais rattrapé ; désactivé, il est rattrapé comme n'importe quel autre. Activer `retrogen` alors que `adoptExistingChunks` l'est aussi ne fait rien, car chaque ancien chunk est écarté avant de pouvoir être mis en file d'attente. Pour remplir un monde existant, activez `retrogen` et désactivez `adoptExistingChunks` ensemble.

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

Changer `retrogenKey` dans la configuration rend de nouveau chaque chunk éligible, ce qui ajoute les nouveaux filons par-dessus les anciens, donc la densité double. C'est voulu, et c'est pourquoi la clé est manuelle.

## Prégénération

*générer le monde*

Créer le terrain d'un monde à l'avance, pour que personne ne génère de chunks en jouant : pas de lag de chunks, une taille connue sur le disque, et une attente unique au départ au lieu d'une première heure saccadée.

Les 12 premiers chunks autour du point d'apparition sont toujours pris en charge, quoi que disent un pack ou la configuration, parce que le jeu en crée exactement autant lui-même avant que quiconque rejoigne. Laissé à lui-même, ce terrain arrive sans éclairage et est habillé chunk par chunk à mesure que le joueur le parcourt ; adopté, il est terminé en une seule passe et le joueur atterrit sur un terrain déjà fini. `pregenOnNewWorld` fixe jusqu'où aller au-delà, et la commande en lance une à la main.

`/rdplserver pregen <radius>` crée chaque chunk situé à ce nombre de chunks ou moins de l'endroit où elle est lancée. `status` indique où elle en est, `stop` y met fin, et `<radius> relight` ne lance que la passe d'éclairage sur le terrain qui existe déjà, habillant les raccords que la passe n'avait pas pu atteindre et laissant tranquille tout ce qui n'a jamais été créé.

Tant qu'une exécution est en cours, tout le monde est retenu : mis en spectateur, maintenu en place, avec une ligne pulsante au milieu de l'écran, le monde en pause autour de lui. Le mode dans lequel chaque joueur est arrivé est inscrit sur le joueur au moment où il est retenu, de sorte qu'une sauvegarde faite en cours d'exécution, un plantage ou une reconnexion ne laisse jamais personne bloqué en spectateur ; la fin de l'exécution rend exactement le mode qu'elle a pris, ou le `worldGameMode` du pack quand il est défini. La progression est annoncée à chaque dixième du parcours, chaque exécution rééclaire son propre carré à sa fin, et quand tout est terminé les joueurs sont libérés et accueillis. La part de chaque dimension déjà créée est sauvegardée dans le monde, donc un monde terminé ne se relance jamais, sauf si l'un des fichiers où vit le terrain d'une dimension disparaît du disque, ce qui est remarqué et fait refaire cette dimension.

Dans un pack, ces clés vont dans le bloc `settings` d'un [modèle de monde](#modèles-de-monde), comme toute autre clé `chunks`. Toutes sont montrées, `pregenBorderLimit` étant la seule absente puisque seule la configuration la contient :

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "pregenOnNewWorld": 63,
    "pregenDimensions": [0, -1],
    "pregenAllDimensions": false,
    "pregenDimensionsWhenEntered": [1],
    "pregenToBorder": false,
    "pregenResume": true,
    "pregenKeepLoaded": 2048,
    "pregenPauseAbove": 2000,
    "pregenMillisPerRound": 200,
    "pregenRunningSays": "Building your world, %d%% done",
    "pregenRelightSays": "Lighting your world, %d%% done",
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
    "welcomeSays": ["Welcome to Ruby World!", "-1=Welcome to the Nether!"],
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

| Clé | Effet | Pourquoi la régler |
| --- | --- | --- |
| `pregenOnNewWorld` | Rayon en chunks créé autour du point d'apparition avant que quiconque joue. 12 est le plancher et 0 signifie ce plancher plutôt que rien, puisque le jeu crée de toute façon 12 chunks autour du point d'apparition : l'exécution adopte ce terrain et l'éclaire en une seule passe au lieu de le laisser arriver au compte-gouttes derrière le joueur. Augmentez-le pour aller plus loin que le jeu | Fixe jusqu'où un pack va au-delà du terrain que le jeu crée déjà |
| `pregenDimensions` | Quelles dimensions sont créées, dans l'ordre, chacune autour de son propre point d'apparition | Ajoutez le Nether, l'End ou vos propres dimensions |
| `pregenAllDimensions` | Chaque dimension enregistrée au lieu d'une liste, l'Overworld en premier | Packs comptant de nombreuses dimensions. Les dimensions de chaque mod comptent, donc attention à la taille |
| `pregenDimensionsWhenEntered` | Celles-ci sont créées la première fois que quelqu'un y met le pied, en retenant de nouveau tout le monde jusqu'à la fin | Dimensions que la plupart des joueurs ne visitent pas ; ceux qui n'y vont jamais ne paient rien |
| `pregenToBorder` | Remplit chaque dimension jusqu'à sa bordure de monde au lieu d'un rayon | Mondes bornés |
| `pregenBorderLimit` | Jusqu'où une bordure peut s'étendre avant que l'exécution soit refusée. Configuration uniquement, jamais une clé de pack | Une protection contre une exécution qui s'emballe ; ne l'augmentez qu'en connaissant le temps et l'espace disque qu'elle autorise |

### Comportement d'une partie

*prégénération*

| Clé | Effet | Pourquoi la régler |
| --- | --- | --- |
| `pregenResume` | Une exécution arrêtée ou interrompue reprend là où elle s'était arrêtée. La dimension, le centre et le rayon de l'exécution sont inscrits dans la sauvegarde à son démarrage, donc un plantage, une coupure de courant ou une fermeture en cours d'exécution reprennent tous à une dizaine de secondes près de l'endroit où ils se sont arrêtés au chargement suivant. Une exécution arrêtée volontairement, par commande ou par le chien de garde, reste arrêtée | Longues exécutions sur serveur ; les petites exécutions redémarrent à peu de frais sans cela |
| `pregenKeepLoaded` | Chunks gardés chargés derrière l'exécution pour que les voisins d'un chunk soient disponibles quand il est habillé et éclairé | Augmentez-le si le rééclairage signale de nombreux chunks laissés pour plus tard ; coûte de la mémoire |
| `pregenPauseAbove` | L'exécution se repose quand ce nombre de chunks attendent d'être écrits | Baissez-le pour un disque lent |
| `pregenMillisPerRound` | Combien de temps chaque tick peut passer à créer du terrain | Augmentez-le sur un monde vide, baissez-le sur un serveur où des gens jouent |

La prégénération a son propre chemin rapide pour l'éclairage, et elle s'efface quand un moteur de lumière comme Alfheim ou Phosphor est installé, laissant ce moteur faire le travail. Dans les deux cas, vous obtenez un terrain terminé et entièrement éclairé.

Lancez-la vous-même avant de publier, au rayon qui sera livré, du début à la fin. Les chunks croissent avec le carré du rayon, 63 dans les deux sens font seize mille chunks, 500 en font plus d'un million, à environ dix kilo-octets chacun, donc le dossier de région de votre monde de test et le temps écoulé sont les chiffres honnêtes à présenter aux joueurs. Ne livrez pas un rayon qui n'a jamais été exécuté.

### Ce que voient les joueurs

*prégénération*

| Clé | Effet | Pourquoi la régler |
| --- | --- | --- |
| `pregenRunningSays`, `pregenRelightSays`, `pregenFinishedSays`, `pregenStoppedSays` | Les messages de chat de chaque étape. Les deux premiers peuvent contenir `%d` pour le pourcentage puis, après lui, `%s` pour le nom de la dimension, ou `%1$d` et `%2$s` pour les placer dans n'importe quel ordre, et se terminent toujours par ` - ETA 00:00:00` pour cette passe, ce qui n'est pas un réglage. Terminé et arrêté sont dits une seule fois, quand tout ce qui était demandé est fait, et se terminent par ` - Total time 00:00:00` pour l'ensemble, ce qui n'est pas non plus un réglage | Reformulez-les dans la voix de votre pack, nommez la dimension quand plusieurs sont créées, ou rendez-les muets |
| `pregenSpectatingSays` | La ligne de retenue au milieu de l'écran pendant la création du terrain. Laissée à sa valeur par défaut, elle parle la langue de chaque joueur ; vide, elle n'affiche rien | Gardez-la sous environ trente-cinq caractères sinon les petites fenêtres la tronquent |
| `pregenLogo` | Où se tient le logo à la fin de la prégénération : `left`, `center` ou `right`, au-dessus du texte du milieu de l'écran, affiché quelques secondes puis s'estompant avec le brouillard | Il est toujours affiché ; un mot inconnu est lu comme `center` |
| `welcomeSays` | Le message d'accueil vert, affiché à chaque connexion et après la prégénération. Une entrée simple est la ligne valable partout ; une entrée `dimension=message` la remplace pour cette dimension et accueille aussi chaque arrivée là-bas, par ex. `"-1=Welcome to the Nether!"`. Un message vide après le `=` rend cette dimension muette ; une liste vide n'affiche rien. Laissé à sa valeur par défaut, il parle la langue de chaque joueur | Une ligne simple nomme votre pack ; ajoutez des lignes par dimension pour thématiser chaque monde. Gardez les lignes sous environ trente-cinq caractères |
| `saysCard` | Affiche les lignes que ce mod prononce, l'accueil, la progression de la prégénération et les lignes de menace, sous forme de carte dans le coin inférieur droit au lieu du chat. La carte glisse en place, reste huit secondes et s'estompe, et s'affiche aussi par-dessus un écran ouvert | Activez-le quand le chat est chargé ou que les lignes doivent se lire comme faisant partie du monde plutôt que comme du bavardage |
| `saysIcon` | Un objet dessiné sur la carte, par ex. `minecraft:compass`. Vide, n'en dessine aucun | Donnez à la carte l'emblème de votre pack |
| `saysColor` | La couleur de fond de la carte en hexadécimal, par ex. `1E2630`. Vide, utilise un gris ardoise sombre | Accordez-la à la palette de votre pack |
| `saysImage` | Un PNG des ressources client du pack, par ex. `rubyworld:textures/gui/card.png`, étiré sur la carte comme arrière-plan et dessiné par-dessus la couleur. Vide, n'en dessine aucun | Donnez à la carte un panneau peint ; gardez l'image large et basse, elle est étirée à ce dont le texte a besoin |
| `saysBackground` | Dessine le panneau de la carte, sa bordure et la bande de couleur, ainsi que le fond sombre derrière l'accueil et les notes au milieu de l'écran pendant qu'un joueur est retenu. Désactivé, il ne reste que le texte, qui garde son ombre, et `saysImage` s'il est défini | Laissez les lignes flotter au-dessus du monde, ou laissez un `saysImage` peint se suffire à lui-même |
| `saysFont` | La police dans laquelle le texte de la carte est dessiné, nommée `namespace:name`, par ex. `rubyworld:runes`. Vide, utilise la police de RDPL, `resourcedatapackloader:rdpl`. Le fichier qu'elle nomme est décrit sous Cartes | Donnez à la carte les lettres propres à votre pack |
| `toasts` | Lesquelles des notifications du jeu, les fenêtres en haut à droite, sont affichées. `true` les affiche toutes et `false` aucune ; une liste n'affiche que les sortes qu'elle nomme : `advancements`, `recipes` pour les recettes débloquées, `tutorial` pour les conseils du tutoriel, `system` pour les avis propres au jeu, et `other` pour toute notification que les autres ne couvrent pas, comme celles d'autres mods. Par défaut, aucune n'est affichée. Le client d'un joueur prend la valeur à sa connexion | Gardez `["advancements"]` quand votre pack guide les joueurs par des progrès et que les autres gênent |

### Sauvegarde et réinitialisation de la carte

*prégénération*

| Clé | Effet | Pourquoi la régler |
| --- | --- | --- |
| `pregenBackup` | Copie le monde dans une sauvegarde vierge une fois la prégénération terminée, pendant que les joueurs sont encore retenus. La génération n'est alors payée qu'une fois : une réinitialisation ultérieure, ou un nouveau monde sur le même pack et la même graine, restaure la copie au lieu de générer de nouveau, ce qui est bien plus rapide que de prégénérer deux fois. La copie est gardée en dehors de la sauvegarde, dans `rdpl-pristine/<world>` à côté d'elle, afin que les sauvegardes d'un autre mod ne la balaient pas et qu'elle n'apparaisse pas dans un dossier qu'ils gèrent. Une copie dont les packs ne correspondent plus à ceux chargés est jetée et refaite à partir du monde en cours, de sorte qu'un changement de pack ne réinitialise jamais vers la carte de quelqu'un d'autre | `false` |
| `pregenBackupSays` | La ligne au milieu de l'écran montrée aux joueurs pendant la réalisation de cette copie, suivie du pourcentage. Vide, n'affiche rien et la copie est faite silencieusement | `Pack requested world backup` |
| `resetSays` | La ligne au milieu de l'écran montrée aux joueurs pendant que `/rdplserver reset` ou la fin d'une manche remet la carte en place. Vide, réinitialise silencieusement | `Pack requested map reset` |
| `resetSendsTo` | Où les joueurs sont placés par une réinitialisation : `spawn`, une position sous la forme `x,y,z`, ou `dimension:x,y,z` pour les envoyer dans un autre monde, ce qui permet à une réinitialisation de déposer tout le monde dans un hall d'attente plutôt que de les remettre dans l'arène | `spawn` |
| `resetRuns` | Une fonction exécutée après qu'une réinitialisation a vidé la carte, nommée `namespace:path`. C'est ce qui reconstruit l'arène, puisqu'un pack qui a fait sa carte à partir d'une fonction peut simplement l'exécuter une seconde fois. Vide, n'exécute rien | vide |
| `resetClearsEntities` | Supprime toute entité qui n'est pas un joueur. Les monstres, les objets au sol et l'expérience disparaissent tous, ce qui laisse la carte comme au départ | `true` |
| `resetClearsScores` | Remet à zéro chaque objectif que le pack tient, pour qu'un nouveau match parte de zéro. Les équipes elles-mêmes sont conservées | `true` |
| `resetClearsInventory` | Vide l'inventaire de chaque joueur, armure et main secondaire comprises, pour qu'une manche commence avec ce que la carte distribue et non avec ce que la précédente a laissé. Le `gives` d'un camp est redistribué juste après | `false` |
| `resetClearsExperience` | Remet l'expérience de chaque joueur au niveau zéro | `false` |

---

# Modes de jeu

## Introduction au monde

*modes de jeu*

`<namespace>/worldintro/*.json`

Le nom du fichier est à votre guise, seul le dossier est lu. Chaque introduction livrée par un pack s'exécute, dans l'ordre des packs.

Affiche une suite de pages quand un joueur entre dans le monde, avant qu'il prenne le contrôle. Du texte qui défile sur une image, une carte de titre, un diaporama, ou les trois à la suite.

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

| Clé | Requis | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `pages` | oui | liste de pages | aucun | Affichées dans l'ordre. Un fichier sans page est refusé avec une erreur |
| `once` | non | booléen | `false` | Jouée une fois par joueur et par monde au lieu de chaque connexion |
| `music` | non | nom d'événement sonore | aucun | Une piste pour toute la suite, lancée avec la première page |
| `requires` | non | liste d'ids de mods ou de namespaces de pack | aucun | L'introduction est ignorée sauf si tous sont présents |

### Pages

*introduction au monde*

Chaque entrée de `pages` :

| Clé | Requis | Valeur | Défaut | Effet |
| --- | --- | --- | --- | --- |
| `mode` | non | `scroll` ou `static` | `scroll` | Du texte qui bouge, ou du texte qui reste immobile jusqu'à ce que le joueur passe à la suite |
| `text` | non | chemin vers un fichier `.txt` | aucun | Les mots. Omettez-le pour une page qui n'est que des images |
| `background` | non | chemin de texture | le fond de terre en mosaïque | Un seul arrière-plan |
| `backgrounds` | non | liste de chemins de textures | aucun | Plusieurs, en alternance. S'ajoute à `background` si vous donnez les deux |
| `interval` | non | secondes | `5.0` | Combien de temps chaque arrière-plan est tenu, quand il y en a plusieurs |
| `time` | non | secondes | calculé à partir du texte | Combien de temps dure une page défilante, du début à la fin. Sur une page fixe, ou sur la dernière page de n'importe quel type, c'est le délai avant que la page passe d'elle-même à la suite, et sans lui elles attendent le bouton |
| `direction` | non | `up` ou `down` | `up` | Dans quel sens le texte défilant se déplace |
| `textScale` | non | nombre | `1.0` | Multiplie la taille de la police. Une page `static` coupe son texte à la largeur de l'écran, moins une marge de chaque côté, et quand il passerait encore sous les boutons, son texte est dessiné plus petit, jusqu'à la moitié, jusqu'à ce qu'il tienne |
| `settle` | non | booléen | `false` | Termine avec la dernière ligne centrée plutôt que de sortir complètement de l'écran |

### Texte et durée

*introduction au monde*

Les fichiers texte se placent dans `<namespace>/texts/*.txt`. Du texte brut, un paragraphe par ligne, et les lignes vides sont conservées telles quelles. Un fichier `.md` est lu de la même façon, et les deux types acceptent la mise en forme ci-dessous. `PLAYERNAME` est remplacé par le nom du joueur, la même substitution que dans le poème de fin vanilla.

`time` fixe la durée d'une page : une même page dure donc le même temps qu'elle contienne une ligne ou vingt. Réglez la vitesse de lecture selon la quantité de texte que vous placez sur la page. Sans `time`, la page défile à la même vitesse que le générique vanilla, où plus de texte prend simplement plus de temps.

### Mise en forme du texte

*introduction au monde*

Les textes d'introduction acceptent le Markdown. Un fichier sans aucune marque s'affiche exactement comme du texte brut.

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

| Marque | Écrite ainsi | Rendu |
| --- | --- | --- |
| Titre | `# `, `## `, `### ` en début de ligne | En gras et plus grand : deux fois, une fois et demie et une fois et quart la taille du texte, aligné comme le corps du texte |
| Gras | `**text**` | La variante grasse de la police |
| Italique | `*text*` | La variante italique de la police |
| Gras italique | `***text***` | La variante grasse, inclinée |
| Barré | `~~text~~` | Texte barré |
| Code | `` `text` `` | Teinté d'aqua |
| Lien | `[text](url)` | Le texte seul, souligné ; non cliquable |
| Runique | `{runic}text{/runic}` | Le texte dans le chiffre runique, `resourcedatapackloader:rdpl_runic`, tandis que le reste de la ligne garde sa police ; le gras et l'italique à l'intérieur prennent les variantes grasse et italique du chiffre. Fonctionne dans les titres, les éléments de liste et les citations, et un `{runic}` non refermé s'affiche tel qu'il est écrit |
| Puce | `- ` ou `* ` en début de ligne | Une puce, les lignes suivantes étant en retrait sous le texte ; deux espaces avant la marque l'imbriquent d'un niveau |
| Numérotée | `1. ` en début de ligne | Le numéro tel qu'il est écrit, avec le même retrait |
| Citation | `> ` en début de ligne | En retrait et estompée |
| Filet | `---` seul sur sa ligne | Une ligne horizontale sur toute la largeur du texte |
| Image | `![alt](namespace:textures/....png)` seule sur sa ligne | L'image, réduite à la largeur du texte en conservant ses proportions ; le texte alternatif s'affiche si elle est illisible |
| Échappement | `\` avant une marque, p. ex. `\*` | La marque comme simple caractère |

Les tableaux et les blocs de code délimités (entre des lignes ```) sont affichés comme du texte brut, marques comprises. Le temps calculé d'une page défilante et l'ajustement d'une page fixe comptent tous deux la hauteur mise en page, images comprises. Les titres et lignes de cartes, les messages Says et les notes d'accueil et de retenue acceptent les marques en ligne, du gras au runique, sur une seule ligne.

### Déroulement du jeu

*introduction au monde*

Une page défilante passe à la suivante quand son temps est écoulé. La dernière page n'avance jamais seule, elle attend. En bas se trouvent **Next Page** et **Skip All**, ou un unique **Continue to World** sur la dernière page. Échap fait la même chose que Skip All. Les pages fixes centrent chaque ligne. Les pages défilantes gardent une colonne fixe, comme le générique.

En solo, le monde est en pause derrière l'introduction, de sorte que rien ne s'approche du joueur pendant qu'il lit. La seule exception est le terrain encore en cours de création à l'ouverture de l'introduction : la création se poursuit alors derrière les pages, et le joueur reste retenu en spectateur jusqu'à ce qu'il continue vers le monde, même si le travail se termine avant. Sur un serveur, le monde continue de tourner, et un client vanilla ne voit jamais l'introduction et rejoint la partie normalement. Le message d'accueil attend la fermeture des pages, afin de ne pas se perdre derrière elles.

`once` est mémorisé dans les données sauvegardées du joueur et survit à la mort. `/rdplserver intro` l'efface pour celui qui l'exécute, de sorte que l'introduction se rejoue à sa prochaine connexion. Elle ne se rejoue pas sur-le-champ, ce qui évite d'en faire un moyen de retourner dans la séquence d'entrée en pleine partie.

Les arrière-plans sont étirés pour remplir la fenêtre : une image 16:9 convient à une fenêtre 16:9, et une image carrée paraîtra écrasée. Recadrez l'image aux bonnes proportions plutôt que de compter sur l'ajustement. `music` accepte tout événement sonore enregistré, vanilla ou ajouté par votre propre pack via `sounds`. Il ne boucle pas : une piste courte se termine et laisse le silence derrière elle.

Si plusieurs packs fournissent une introduction, leurs pages s'enchaînent dans l'ordre des packs au lieu qu'une seule l'emporte. Limitez-les avec `requires` si vous n'en voulez qu'une.

## Équipes

*modes de jeu*

`<namespace>/teams/*.json`

Le nom du fichier est libre, seul le dossier est lu, et plusieurs fichiers se cumulent. Chaque fichier est un camp.

Un camp est une véritable équipe du tableau des scores du jeu : `/scoreboard teams list` la voit, elle conserve ses membres après une sauvegarde et un rechargement, et un client sans ce mod affiche les couleurs et les plaques de nom comme pour n'importe quelle équipe vanilla. L'appartenance se fait par nom, donc tout ce qui a un nom ou un UUID peut faire partie d'un camp : un joueur, un zombie, un villageois, un porte-armure.

Un camp n'est mis en place que là où un pack en demande un : sans dossier `teams` nulle part, le mod n'ajoute aucune équipe, n'écoute rien et ne propose pas la commande. Un opérateur de serveur qui modifie un fichier peut exécuter `/rdpl reload` pour appliquer le changement au monde en cours sans redémarrer.

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

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `name` | texte | le nom du fichier | Le nom de l'équipe sur le tableau des scores, de 1 à 16 caractères. C'est ce qu'utilisent `/scoreboard` et les autres fichiers |
| `displayName` | texte | le nom | Ce qui est montré aux joueurs à la place du nom |
| `color` | texte | `white` | Une des seize couleurs de texte. Elle teinte la plaque de nom et sert de clé aux emplacements de la barre latérale propres à chaque équipe |
| `prefix` | texte | vide | Placé devant le nom d'un membre, après la couleur |
| `suffix` | texte | vide | Placé après le nom d'un membre |
| `scoreboard` | booléen | `true` | Indique si le camp existe comme équipe sur le tableau des scores du jeu. Désactivé, aucune équipe n'est créée : ses mobs portent la couleur du camp dans leur nom, rien ne les empêche de se battre entre eux, et aucun point ne lui est attribué, puisque le score va à l'équipe |

### Combat et visibilité

*équipes*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `friendlyFire` | booléen | `false` | Indique si les membres peuvent se blesser entre eux. C'est aussi la valeur par défaut de `mobFriendlyFire` |
| `mobFriendlyFire` | booléen | `friendlyFire` | Indique si les mobs d'un camp peuvent blesser leur propre camp avec des explosions et du TNT lancé, ce que le jeu seul n'empêche jamais. Désactivé, le camp est épargné ; activé, il reste comme le jeu l'a prévu |
| `seeFriendlyInvisibles` | booléen | `true` | Indique si les membres se voient entre eux lorsqu'ils sont invisibles |
| `nameTags` | texte | `always` | `always`, `never`, `hideForOtherTeams` ou `hideForOwnTeam` |
| `deathMessages` | texte | `always` | Les mêmes quatre mots, pour savoir qui est prévenu à la mort d'un membre |
| `collision` | texte | `always` | `always`, `never`, `pushOtherTeams` ou `pushOwnTeam` |

### Qui rejoint

*équipes*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `entities` | liste | vide | Les ids d'entités dont chaque apparition rejoint ce camp, comme `minecraft:zombie` ou une des vôtres |
| `players` | liste | vide | Les noms de joueurs qui rejoignent ce camp à leur connexion |
| `spawnBox` | liste | aucun | Six nombres entiers, x y z à x y z. Tout ce qui apparaît à l'intérieur rejoint le camp, et les coins peuvent être donnés dans un sens ou dans l'autre |
| `joinable` | booléen | `true` | Indique si un joueur peut rejoindre avec `/rdpl team join`. Mettez-le à false pour un camp réservé aux mobs |
| `balance` | booléen | `false` | Indique si `/rdpl team join` sans nom peut placer un joueur ici. Parmi les camps qui l'autorisent, celui qui compte le moins de joueurs est choisi |
| `picks` | nombre | `0` | Le nombre de membres que ce camp tire au sort. À chaque ouverture de manche, le camp laisse son dernier tirage retourner là où il se tenait et tire de nouveau parmi tout ce que nomme `picksFrom` ; entre deux tirages, une connexion ou une apparition issue de ce groupe comble aussitôt une place vide. Un joueur parmi tous, dans un camp à part, voilà à quoi ça sert |
| `picksFrom` | liste | vide | Ce parmi quoi le tirage est fait : `players` pour tous les joueurs en ligne, et des ids d'entités pour chaque mob vivant de ce type |
| `standIn` | objet | aucun | Un mob qui tient le camp tant qu'aucun joueur n'y est : `{ "entity": "mypack:herobrine", "at": "23,31,0" }` maintient en vie une entité de ce type à cet endroit de l'Overworld, la fait apparaître quand elle manque, et la retire dès qu'un joueur rejoint le camp, de sorte qu'une partie se joue contre l'IA jusqu'à ce qu'un joueur prenne le rôle. Vérifié toutes les cinq secondes ; l'endroit doit se trouver en terrain chargé. Dans une partie avec hall d'attente (`opens.by: leader`), un remplaçant n'est invoqué que pendant l'attente du hall et à l'ouverture de la manche : un remplaçant tombé reste donc absent jusqu'à la fin de la manche et de sa clôture, jusqu'à ce que tout le monde soit de retour dans le hall ; sans hall, un remplaçant tombé n'est pas remplacé pendant une manche qui se termine sur `ends.lastStanding` |

Il existe trois façons de rejoindre, et un camp peut toutes les utiliser. `entities` nomme des ids d'entités, et tout ce qui est de ce type rejoint à son apparition, ce qui permet à un pack de donner des camps aux mobs sans toucher aux mobs. `spawnBox` revendique un coin du monde, et tout ce qui y apparaît rejoint, ce qui convient à une arène où les deux camps utilisent le même mob. `players` nomme directement des joueurs. En plus de cela, un joueur peut rejoindre avec `/rdpl team join <name>` sauf si le camp met `joinable` à false, et partir avec `/rdpl team leave`.

### Kit de départ et point d'apparition

*équipes*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `gives` | liste | vide | Des objets placés dans l'inventaire d'un joueur quand il rejoint le camp, un nom d'objet pour un seul ou `{ "item", "count", "unbreakable" }` pour plusieurs, ou pour un objet qui ne s'use jamais, dans n'importe quel emplacement libre, et jetés à ses pieds s'il n'y en a aucun. Redistribués après une réinitialisation qui vide les inventaires (`resetClearsInventory`) |
| `spawn` | texte | aucun | `x,y,z` dans l'Overworld où les joueurs du camp sont placés à l'ouverture d'une manche, afin que chaque camp commence sur son propre terrain ; sans cela, ils restent là où la réinitialisation ou le hall les a laissés |

### Le chef

*équipes*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `lead` | texte | `none` | Comment le chef du camp est choisi : `none` ; `first` pour celui qui a rejoint le camp le plus tôt parmi les joueurs en ligne, de sorte que le rôle descend l'ordre d'arrivée tant que l'un d'eux est absent et lui revient à son retour ; il est prévenu à son arrivée, après l'introduction et toute retenue, et de nouveau quand le rôle lui passe ; `topScore` pour celui qui est en tête de l'objectif que nomme `leadOn` ; `appointed` pour le joueur que nomme `leadIs` ; `vote` pour celui que les membres élisent ; ou `claim` pour celui qui le revendique en premier. Un chef est une étiquette et une couleur, rien de plus : il n'accorde aucun pouvoir, donc un chef qui se déconnecte ne casse rien |
| `leadOn` | texte | vide | Avec `topScore`, l'objectif selon lequel les membres sont classés. Il est recalculé à chaque lecture, donc il suit le score |
| `leadIs` | texte | vide | Avec `appointed`, le joueur qui dirige |
| `leadSays` | texte | `You are the current round leader` | Dit à un joueur quand le rôle de chef lui échoit : à son arrivée dans un camp qu'il dirige, quand il le revendique, ou quand un chef `first` lui passe le rôle, avec le nom de celui qui est parti. `{side}` est le nom affiché du camp ; vide, rien n'est dit |
| `leadRuns` | texte | vide | Une fonction, `namespace:path`, exécutée une fois chaque fois que le rôle de chef passe à un joueur : le premier chef, et chaque transmission ensuite. Elle s'exécute en tant que chef, à sa position, avec la permission d'une fonction qu'octroie un progrès, donc `@s` est le chef. Vérifié chaque seconde ; pour un chef hors ligne, elle s'exécute à sa prochaine connexion. Un redémarrage redétermine le chef de zéro |

## Score

*modes de jeu*

`<namespace>/scoring/*.json`

Le nom du fichier est libre, seul le dossier est lu, et plusieurs fichiers se cumulent. Chaque fichier est un objectif.

Un objectif est un véritable objectif du tableau des scores du jeu : `/scoreboard players list` le lit et il conserve ses scores après une sauvegarde. `criterion` est ce que le jeu compte de lui-même : `dummy` pour un score que seul ce pack fait bouger, ou `deathCount`, `playerKillCount`, `totalKillCount`, `health`, ou tout nom `stat.` ou `achievement.` que le jeu connaît.

```json
{
  "name": "kaboom",
  "displayName": "Kills",
  "criterion": "dummy",
  "display": "sidebar",
  "teamTotals": true,
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

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `name` | texte | le nom du fichier | Le nom de l'objectif sur le tableau des scores, de 1 à 16 caractères |
| `displayName` | texte | le nom | Ce qui est montré aux joueurs à la place du nom |
| `criterion` | texte | `dummy` | Ce que le jeu compte de lui-même. Un critère inconnu est refusé avec une ligne qui l'indique |
| `display` | texte | vide | `sidebar`, `list`, `belowName` ou `sidebar.team.<color>`. Vide, il n'est affiché nulle part ; il n'y a pas d'écran de tableau des scores à ouvrir |
| `render` | texte | celui du critère | `integer` ou `hearts` |
| `teamTotals` | booléen | `true` | Les points vont sur une ligne portant le nom de l'équipe du membre |
| `individuals` | booléen | `false` | Les points vont aussi sur une ligne propre au membre |
| `carries` | booléen | `false` | L'objectif survit à une réinitialisation de la carte au lieu d'être effacé avec elle. Le décompte d'un match, avec les manches gagnées, en est un |
| `awardsTo` | texte | vide | Un autre objectif auquel celui-ci donne un point à sa fin, au camp qui menait. Une égalité n'accorde rien |

### Points

*score*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `points.kill` | objet | vide | Id d'entité vers des points, crédités au camp du tueur. `minecraft:player` marque une victoire sur un joueur |
| `points.death` | int | `0` | Des points chaque fois qu'un membre meurt, quelle qu'en soit la cause. Peut être négatif |
| `points.ownKill` | int | `0` | Des points pour un meurtre du propre camp du tueur, à la place de la valeur de `kill`. 0 ne marque rien ; un nombre négatif est une pénalité |

`points` est ce que ce mod ajoute à ce que le jeu compte, versé dans le même objectif afin que `/scoreboard` le lise toujours. `kill` vaut tant de points par id d'entité tuée, crédités au camp du tueur ; `death` vaut tant chaque fois qu'un membre d'un camp meurt, et peut être négatif. Avec `teamTotals`, les points vont sur une ligne portant le nom de l'équipe, ce qui permet à la barre latérale d'afficher quatre camps plutôt qu'une ligne par mob. `individuals` ajoute aussi une ligne par membre, et est désactivé par défaut parce qu'une ligne par UUID de mob ressemble à du bruit.

### Comment une manche se termine

*score*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `ends.atScore` | int | `0` | Le match se termine dès qu'un camp atteint ce score. 0 ne termine jamais sur le score |
| `ends.afterMinutes` | int | `0` | Le match se termine après ce nombre de minutes. 0 ne termine jamais sur le temps |
| `ends.afterRounds` | int | `0` | Pour un objectif qu'un autre `awardsTo` : le match se termine une fois ce nombre de manches attribuées au total, quel qu'en soit le vainqueur. 0 ne termine jamais sur les manches |
| `ends.lastStanding` | booléen | `false` | La manche se termine quand un seul camp reste debout. Les camps en jeu sont ceux qui ont un joueur ou un mob vivant à l'ouverture de la manche, deux au minimum ; un joueur qui meurt est éliminé, revenant en spectateur jusqu'à la fin de la manche, et un camp dont tous les joueurs sont éliminés ou partis et dont tous les mobs sont morts est tombé. Le camp resté debout remporte la manche, et `awardsTo` l'enregistre pour ce camp quel que soit le score. Avec `resets` et `opens.by: leader`, la partie retourne ensuite au hall. Le `standIn` d'un camp n'est pas invoqué de nouveau tant qu'une telle manche se déroule |
| `ends.outSays` | texte | `You are out until the round ends` | Ce qui est dit à un joueur éliminé. Vide, rien n'est dit |
| `ends.locksTeams` | booléen | `true` | Rejoindre un camp pendant une manche en cours attend la fin de la manche, afin que personne ne tombe en cours de route dans une manche comptabilisée |

`ends` termine le match, soit dès qu'un camp atteint `atScore`, soit une fois `afterMinutes` écoulées. Le classement est alors affiché, établi par le jeu lui-même : dans le chat, ou sous forme de carte si `results` en demande une. Un joueur sans ce mod reçoit le même classement en lignes de chat, afin que personne ne reste sans résultat. Avec `resets`, cette fin est celle d'une manche : le classement reste affiché pendant `intermissionSeconds` tandis qu'un compte à rebours défile dans la barre d'action, la carte revient à l'accueil, et la manche suivante s'ouvre après un décompte de cinq secondes. `awardsTo` donne la manche au camp qui menait, sur un objectif qui `carries` à travers la réinitialisation. Un objectif reporté peut se terminer de lui-même -- `atScore` pour un meilleur des N, `afterRounds` pour un nombre fixe -- et son classement est effacé à la réinitialisation suivante, de sorte qu'un nouveau match s'ouvre.

### Entre les manches

*score*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `ends.resets` | booléen | `false` | La fin de la manche réinitialise la carte, comme le décrivent `resetSays` et les autres réglages de réinitialisation, puis une nouvelle manche s'ouvre |
| `ends.intermissionSeconds` | int | `10` | Combien de temps le classement reste affiché entre la fin et la réinitialisation |
| `ends.intermissionSays` | texte | `Round cooldown {seconds}` | Affiché dans la barre d'action chaque seconde de l'intermède après la fin d'une manche, `{seconds}` décomptant jusqu'à la réinitialisation. Vide, rien n'est affiché |
| `ends.startsSays` | texte | `Round starting in {seconds}` | Affiché dans la barre d'action pendant le décompte de cinq secondes qui ouvre la manche suivante après la réinitialisation, `{seconds}` décomptant. Vide, rien n'est affiché |

### Le hall d'attente

*score*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `opens.by` | texte | `auto` | `auto` ouvre la manche suivante d'elle-même, cinq secondes après la réinitialisation. `leader` retient la partie dans un hall : après la réinitialisation, et au premier chargement du monde, rien n'est comptabilisé et aucune horloge ne tourne, on peut rejoindre et quitter les camps librement, et la manche ne s'ouvre que lorsque le chef d'un camp, ou un opérateur, exécute `/rdpl round start`, et pas tant que quelqu'un lit encore l'introduction au monde ; ensuite le décompte de cinq secondes s'écoule, les tirages sont faits, et chaque camp est placé à son `spawn`. Tant que le monde attend, jusqu'à la fin du décompte de cinq secondes, les joueurs restent où ils se trouvent et ne peuvent rien casser, poser, utiliser, frapper ni lâcher et ne subissent aucun dégât, la ligne d'attente leur étant montrée quand ils essaient, et tout autre être vivant reste immobile : ni IA, ni mouvement. Les commandes fonctionnent toujours, donc on peut rejoindre des camps et lancer la manche |
| `opens.says` | texte | `Waiting for {leader} to start the round` | Affiché au milieu de l'écran, comme l'accueil, à chaque joueur qui ne dirige pas : à son arrivée dans le hall après l'introduction, une fois l'accueil montré ; quand le hall se rouvre après une manche ; chaque fois qu'il change, quand un chef arrive ou part ; et quand le joueur tente quelque chose que le hall refuse. `{leader}` désigne les chefs de tous les camps, ou `a leader` tant que personne ne dirige. Vide, rien n'est affiché |
| `opens.leaderSays` | texte | `Type /rdpl round start` | Affiché de la même façon et aux mêmes moments à un joueur qui dirige un camp, à la place de `opens.says`. Vide, rien n'est affiché |
| `opens.lobby` | texte | aucun | `x,y,z` dans l'Overworld, ou `dimension:x,y,z` dans un autre monde, comme `-1:0,64,0`, où tout le monde attend tant que le hall tient : chaque joueur, et chaque mob vivant d'un camp, est placé sur un anneau autour de ce point, chacun tourné vers le centre, de sorte qu'ils se regardent tous fixement. Chacun reçoit un arc aussi large que lui plus deux blocs, pour qu'aucun n'en chevauche un autre, et l'anneau grandit à mesure que d'autres arrivent ; il est recalculé chaque fois que quelqu'un le rejoint ou le quitte. La hauteur est celle du sol sur lequel ils se tiennent, trouvé à trois blocs près dans les deux sens. Joueurs et mobs passent directement dans ce monde et en reviennent, sans portail construit. À l'ouverture de la manche, les joueurs vont au `spawn` de leur camp, et un mob encore debout est remis là où il était, dans son propre monde |
| `opens.lobbyJoins` | booléen | `false` | Place un joueur qui se connecte en cours de manche dans le hall en spectateur jusqu'à la fin de la manche, au lieu de l'endroit où il s'était déconnecté. Nécessite `opens.lobby` |
| `opens.joinsSays` | texte | `Round is in progress, you can join after it ends` | Ce qui leur est dit. Vide, rien n'est dit |

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
| `reset.lead` | texte | `none` | Ce que fait `/rdpl round reset` pour le chef d'un camp pendant une manche. `now` termine aussitôt la manche et réinitialise la carte ; `vote` lance un vote à la place ; `none` ne donne au chef aucun pouvoir propre, de sorte qu'il lance un vote comme n'importe quel autre joueur là où `players` le permet. Un opérateur réinitialise toujours aussitôt |
| `reset.players` | texte | `none` | `vote` permet à un joueur de n'importe quel camp de lancer un vote avec `/rdpl round reset`. `none` laisse la réinitialisation au chef |
| `reset.teams` | liste | vide | Les camps dont les joueurs peuvent lancer un vote. Vide, c'est chaque camp |
| `reset.passPercent` | int | `51` | La part des votants, de 1 à 100, qui doivent voter oui pour que la manche soit réinitialisée. `51` est plus de la moitié, `100` est tout le monde |
| `reset.voteSeconds` | int | `30` | La durée d'un vote, cinq secondes au minimum. Il se ferme plus tôt dès que son issue est certaine |
| `reset.cooldownSeconds` | int | `60` | Le délai après un vote échoué avant qu'un autre puisse être lancé. Un chef avec `now` n'est pas retenu par ce délai |
| `reset.leadSays` | texte | `{player} reset the round` | Dit à tout le monde quand la manche est réinitialisée aussitôt, `{player}` étant celui qui l'a réinitialisée. Vide, rien n'est dit |
| `reset.voteSays` | texte | `{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds` | Dit à tout le monde quand un vote est lancé, `{player}` étant celui qui l'a lancé. Vide, rien n'est dit |
| `reset.tallySays` | texte | `Reset the round? {yes} yes, {no} no, {seconds}` | Affiché dans la barre d'action chaque seconde d'un vote, `{seconds}` décomptant. Vide, rien n'est affiché |
| `reset.passSays` | texte | `The vote passed, so the round is reset` | Dit à tout le monde quand un vote est adopté. Vide, rien n'est dit |
| `reset.failSays` | texte | `The vote failed, so the round goes on` | Dit à tout le monde quand un vote échoue. Vide, rien n'est dit |

Une réinitialisation coupe la manche là où elle en est. Le classement est affiché sous `The round was reset`, personne ne remporte la manche, l'intermède décompte, et la carte se réinitialise comme si la manche s'était terminée avec `ends.resets`, retournant au hall là où `opens.by` vaut `leader`. Elle fonctionne que la manche doive ou non se terminer d'elle-même, mais pas dans le hall, pendant le décompte qui ouvre une manche, ni une fois la manche terminée et sa réinitialisation en route ; un vote encore en cours est alors abandonné.

Chaque joueur en ligne dans un camp vote, quel que soit son camp, avec `/rdpl round vote yes` ou `no`, et peut changer son vote pendant qu'il se déroule. Celui qui lance le vote a voté oui, et un joueur qui n'a pas voté à la fin du temps imparti compte pour non. Dans un pack avec des camps, un joueur sans camp ne lance ni ne vote ; dans un pack sans camps, chaque joueur en ligne le fait. Le premier fichier de score dont `reset` laisse quelqu'un réinitialiser est celui qui est utilisé.

### Résultats

*score*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `results.card` | booléen | `false` | Affiche le classement sous forme de carte plutôt que dans le chat |
| `results.title` | texte | le nom et `results` | Le titre de la carte |
| `results.icon` | texte | vide | Un objet dessiné sur la carte, p. ex. `minecraft:tnt` |
| `results.image` | texte | vide | Une image dessinée sur la carte à la place d'un objet |
| `results.background` | texte | un gris ardoise sombre | La couleur d'arrière-plan de la carte |
| `results.seconds` | int | `8` | Combien de temps la carte reste affichée, au moins une seconde |

## Raids

*modes de jeu*

`<namespace>/raids/*.json`

Le nom du fichier est libre, seul le dossier est lu, et plusieurs fichiers se cumulent. Chaque fichier est un raid.

Un raid est celui que le jeu propose à partir de la 1.14, construit sur les villages que la 1.12.2 gère déjà. Il commence quand un joueur porteur de l'effet `omen` se trouve dans un village : l'effet lui est retiré, et une barre de boss apparaît pour chaque joueur à moins de `reach` du centre du village. Après `waveDelay` ticks, la première vague arrive sur un anneau autour du village et marche vers le centre, attaquant joueurs, villageois et golems de fer au passage. Les pillards ne se blessent ni ne se prennent jamais pour cible entre eux, donc une flèche perdue ou un coup entre deux d'entre eux ne fait rien. La barre montre la santé qu'il reste à la vague, et compte les pillards quand il en reste deux ou moins. Une fois une vague éliminée, la suivante attend `waveDelay` ticks. Quand la dernière vague est éliminée et que rien n'est revenu depuis deux secondes, le raid est gagné ; quand tous les villageois sont morts ou que le village lui-même a disparu après l'arrivée d'une vague, il est perdu. Dans les deux cas, la barre l'indique pendant trente secondes, et la fonction correspondante s'exécute en tant que chaque joueur à portée.

Un raid en cours est sauvegardé avec le monde, et ses pillards reprennent leur marche après un rechargement. Il s'arrête sans conclusion en mode paisible, après `timeout` ticks, ou quand aucun endroit autour du village ne peut accueillir une vague. Un village ne compte qu'une fois qu'un villageois a trouvé ses portes, donc un raid exige un village que le jeu a repéré.

Tant qu'une vague est sur le village, ses villageois courent s'abriter à l'intérieur, vers la porte la plus proche que connaît le village, et y restent. Les pillards démolissent les portes en bois qui leur barrent la route pour les atteindre, douze secondes par porte, en difficulté normale et difficile tant que `mobGriefing` est activé ; les portes en fer tiennent. Un bloc de type `bell` est une cloche de village où qu'il se trouve, et sonne comme le décrit [Cloches](#cloches) ; le `bell` du raid nomme tout autre bloc à faire sonner comme une cloche. Chaque cloche du village sonne à l'arrivée d'une vague, et un bloc nommé sonne aussi quand un joueur l'utilise : les villageois à moins de 48 blocs se cachent pendant quinze secondes et les pillards à moins de 48 blocs luisent pendant trois. Rien ne génère de cloche. Un pack qui en veut une définit le bloc, le place dans une structure NBT, et place cette structure dans le village, comme parcelle ou en remplacement du puits, afin que la cloche se dresse là où vivent les villageois.

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
  "bell": "mypack:village_bell",
  "waves": [
    [ { "entity": "minecraft:vindication_illager", "count": 2 }, { "entity": "mypack:raider", "count": { "min": 1, "max": 3 } } ],
    [ { "entity": "minecraft:evocation_illager" }, { "entity": "minecraft:vindication_illager", "count": 4 } ]
  ]
}
```

### Le raid

*raids*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `omen` | nom d'effet | aucun, requis | L'effet qui déclenche le raid quand son porteur se trouve dans un village. Tout effet enregistré convient, y compris une potion propre à un pack |
| `name` | texte | `Raid` | Le titre de la barre de boss |
| `color` | texte | `red` | La couleur de la barre : `pink`, `blue`, `red`, `green`, `yellow`, `purple` ou `white` |
| `waves` | liste de vagues | aucun, requis | Chaque vague est une liste de groupes, et les vagues arrivent dans l'ordre |
| `waveDelay` | int | `300` | Les ticks avant la première vague, et entre la fin d'une vague et la suivante |
| `spawnDistance` | int | `32` | À quelle distance du centre du village arrive une vague. Les premiers essais se font au double de cette valeur, puis à cette valeur, puis à l'intérieur du village |
| `reach` | int | `96` | Les joueurs à moins de ce nombre de blocs du centre voient la barre, et la fonction de fin s'exécute en tant qu'eux. Un pillard qui s'écarte de seize blocs au-delà quitte le raid |
| `timeout` | int | `48000` | Les ticks au bout desquels un raid inachevé s'arrête sans conclusion. `0` ne l'arrête jamais |
| `sound` | nom de son | aucun | Joué à chaque joueur à portée, depuis le côté d'où vient la vague, à l'arrivée de chaque vague |
| `wins` | fonction | aucune | S'exécute en tant que chaque joueur à portée quand le raid est gagné |
| `loses` | fonction | aucune | S'exécute en tant que chaque joueur à portée quand le raid est perdu |
| `bell` | nom de bloc ou liste | aucun | D'autres blocs qui sonnent comme une cloche, quand un joueur les utilise et à l'arrivée de chaque vague. Un bloc de type `bell` sonne sans être nommé. Placez-le dans le village via une structure NBT, puisque rien ne le génère |

### Un groupe

*raids*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `entity` | nom d'entité | aucun, requis | Ce qui arrive. Une variante d'entité conserve tout son comportement et gagne la marche |
| `count` | int ou `{ "min", "max" }` | `1` | Combien arrivent |

---

## Cartes

*modes de jeu*

`<namespace>/cards/*.json`

Le nom du fichier est libre, seul le dossier est lu, et plusieurs fichiers se cumulent. Chaque fichier est une règle, et son id est `<namespace>:<file name>`. Une règle attend un déclencheur, vérifie son `when`, et montre une carte à son public ; elle peut aussi exécuter une fonction. Rien n'est nécessaire côté client : un joueur sans le mod reçoit une carte de coin sous forme de lignes de chat et une carte centrale sous forme de titre.

Chaque message que ce mod dit lui-même est une règle intégrée, listée plus bas. Un pack en modifie une en écrivant un fichier portant cet id, `rdpl/cards/<name>.json`, qui n'a pas besoin de déclencheur : tout ce qu'il omet reste comme aujourd'hui, et `{text}` représente le message que le mod aurait dit. Un pack qui n'en écrit aucune voit chaque message comme avant.

```json
{
  "trigger": "biome_enter",
  "biomes": ["minecraft:desert", "#SANDY"],
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
| `biome_enter` | `biomes` | Un joueur entre dans un de ces biomes depuis un autre endroit |
| `structure_enter` | `structures` | Un joueur entre dans une de ces structures depuis l'extérieur |
| `advancement` | `advancement` | Un joueur obtient ce progrès |
| `time_of_day` | `time` | L'horloge du jour passe ce tick, de `0` à `23999`, tant que des joueurs sont dans la dimension. Une horloge réglée par une commande ou un lit ne compte pas |
| `day` | rien, ou `day` | Un nouveau jour commence dans la dimension ; avec `day`, seulement ce jour |
| `craft` | `item` | Un joueur fabrique cet objet |
| `pickup` | `item` | Un joueur ramasse cet objet |
| `kill` | `entity` | Un joueur tue cette entité, ou la `count`-ième |
| `respawn` | rien | Un joueur réapparaît après être mort |
| `death` | rien | Un joueur meurt |
| `y_level` | `below` ou `above` | Un joueur passe sous ou au-dessus de cette hauteur |
| `play_time` | `minutes` | Le temps d'un joueur dans le monde atteint ce nombre de minutes, compté depuis la fermeture de l'introduction au monde, ou depuis la connexion quand aucune introduction ne lui est montrée |
| `score` | `objective` | Le score d'un joueur dans cet objectif atteint `score` |

Le biome, la structure, la hauteur, le temps de jeu et le score sont vérifiés une fois par seconde pour chaque joueur, et se déclenchent au passage de l'extérieur à l'intérieur, jamais à la première vérification après une connexion. Une règle `time_of_day` ou `day` dont le public n'est pas `player` se déclenche une fois pour la dimension au lieu d'une fois pour chaque joueur qui s'y trouve.

Une carte qui se déclenche alors qu'un joueur a encore l'introduction au monde ouverte attend et s'affiche à la fermeture de l'introduction, quel que soit son déclencheur, y compris `command`. Elle est abandonnée si le joueur part avant.

### Réglages des déclencheurs

*cartes*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `trigger` | texte | aucun, requis | Un des déclencheurs ci-dessus. Une règle intégrée n'en prend aucun |
| `dimension` | texte | aucun | Un id de dimension comme `-1`, ou son nom comme `the_nether`. Pour `dimension_enter`, c'est celle dans laquelle on entre ; pour tous les autres déclencheurs, il limite la règle aux joueurs de cette dimension |
| `biomes` | liste | aucun | Des noms de biomes comme `minecraft:desert`, ou `#TYPE` pour un type de biome Forge comme `#SNOWY` |
| `structures` | liste | aucun | `Village`, `Temple`, `Mansion`, `Monument`, `Mineshaft`, `Stronghold`, `Fortress` ou `EndCity`, ou le nom d'une structure qu'un pack place via `structures`, qui compte dans un rayon `radius` de l'endroit où elle a été placée |
| `radius` | int | `32` | À quelle distance on est considéré à l'intérieur d'une structure propre à un pack |
| `advancement` | texte | aucun | L'id du progrès |
| `item` | texte | aucun | L'objet, écrit comme ailleurs dans un pack, par exemple `minecraft:diamond_sword` |
| `entity` | texte | aucun | L'id de l'entité, par exemple `minecraft:zombie` |
| `count` | int | `1` | Pour `kill` : combien de victimes il faut. Le compte repart de zéro après le déclenchement de la règle |
| `below`, `above` | int | aucun | Pour `y_level` : la hauteur sous ou au-dessus de laquelle passer |
| `time` | int | `0` | Pour `time_of_day` : le tick du jour |
| `day` | int | aucun | Pour `day` : l'unique jour où se déclencher. Sans lui, chaque jour |
| `minutes` | int | aucun | Pour `play_time` |
| `objective`, `score` | texte, int | aucun, `1` | Pour `score` : l'objectif et la valeur à atteindre |
| `requires` | liste d'ids de mods ou de namespaces de packs | aucun | Le fichier est ignoré sauf si tous sont présents |

### Quand

*cartes*

`when` regroupe des conditions qui doivent toutes être vraies au moment où le déclencheur se produit.

| Réglage | Type | Ce qu'il vérifie |
| --- | --- | --- |
| `biomes` | liste | Le joueur se trouve dans un de ces biomes, écrits comme pour le déclencheur |
| `timeFrom`, `timeTo` | int | L'horloge du jour est dans cette fenêtre, qui peut dépasser minuit, comme `13000` à `1000` |
| `dayAtLeast` | int | Le numéro du jour est au moins celui-ci |
| `advancement` | texte | Le joueur possède ce progrès |
| `gameMode` | texte | Le joueur est dans ce mode de jeu : `survival`, `creative`, `adventure` ou `spectator` |
| `team` | texte | Le joueur est dans cette équipe du tableau des scores |
| `objective`, `scoreAtLeast` | texte, int | Le score du joueur dans cet objectif est au moins celui-ci |

### La carte

*cartes*

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `title` | texte | aucun | La première ligne, dessinée plus grande sur une carte centrale |
| `lines` | liste | aucun | Jusqu'à seize lignes. Une règle a besoin d'un titre ou de lignes, sauf une règle intégrée. `{player}`, `{dim}`, `{biome}` et `{day}` sont remplacés ; `{text}` est le message intégré, et seul sur une ligne il donne toutes ses lignes |
| `style` | texte | `corner` | `corner` est la carte en bas à droite qu'affiche `saysCard` ; `center` est une carte au milieu de l'écran ; `chat` est du chat ; `bar` est la barre d'action |
| `icon` | texte | `saysIcon` | Un objet dessiné sur une carte de coin. Vide, aucun n'est dessiné |
| `color` | texte | `saysColor` | La couleur d'arrière-plan de la carte en hexadécimal |
| `image` | texte | `saysImage` | Un PNG des ressources client du pack, étiré sur la carte comme arrière-plan |
| `background` | booléen | `saysBackground` | `false` supprime le panneau, sa bordure et la bande de couleur ; le texte garde son ombre, et une `image` s'affiche toujours |
| `font` | texte | `saysFont` | La police dans laquelle le texte de la carte est dessiné, sous la forme `namespace:name`. Vide, la police RDPL est utilisée |
| `ticks` | int | `160` | Combien de temps la carte reste, fondu compris |
| `audience` | texte | `player` | Qui la voit : `player`, `everyone`, `dimension` (tous ceux de la dimension du joueur) ou `team` (l'équipe du joueur sur le tableau des scores) |
| `repeat` | texte | `always` | `always`, `once_per_player`, `once_per_world` ou `once_per_session` (de nouveau après la reconnexion du joueur) |
| `cooldown` | int | `0` | Les secondes avant que la règle se redéclenche pour le même joueur |
| `runs` | texte | aucun | Une fonction exécutée en tant que le joueur quand la règle se déclenche |

Une carte de coin passe dans le chat quand `saysCard` est désactivé. Ce qu'un joueur a vu est conservé avec le joueur, donc cela survit à la mort et au passage entre dimensions ; `once_per_world` est conservé avec le monde.

La police RDPL, `resourcedatapackloader:rdpl`, est celle par défaut pour tout le texte : cartes, messages Says, notes d'accueil et de retenue, introduction au monde, ainsi que les menus, le chat, l'interface et les infobulles du jeu lui-même. Ses variantes grasse et italique sont `resourcedatapackloader:rdpl_bold` et `resourcedatapackloader:rdpl_italic`. Les caractères de la table d'enchantement restent ceux du jeu.

RDPL fournit ces polices et ces caractères. Le `font` d'une carte, d'une note ou d'une introduction peut nommer une police RDPL par son nom court, ou par son id complet :

| Nom | Ce qu'elle dessine |
| --- | --- |
| `rdpl` (ou `resourcedatapackloader:rdpl`) | La police RDPL, avec le cyrillique (U+0400 à U+04FF) et l'alphabet runique (U+16A0 à U+16F8) |
| `rdpl_runic` (ou `resourcedatapackloader:rdpl_runic`) | Un chiffre runique : les lettres A à Z et a à z sont dessinées en runes, et tout autre caractère est dessiné dans la police RDPL. Un passage en gras est dessiné en `rdpl_runic_bold` et un passage en italique en `rdpl_runic_italic` |
| Runes, U+16A0 à U+16F8 | Écrites sous forme des caractères runiques eux-mêmes (ᚠ ᚢ ᚦ ᚨ ᚱ ᚲ), dans tout texte que dessine la police RDPL, chat compris ; les passages en gras et en italique gardent leur variante |

Une police de carte est un PNG à `assets/<namespace>/textures/font/<name>.png`, une grille de 16 par 16 glyphes disposés comme le `ascii.png` du jeu, et la carte est dimensionnée d'après les largeurs de glyphes lues dedans. Un `<name>_cyrillic.png` à côté, la même grille contenant Unicode U+0400 à U+04FF, dessine le cyrillique ; sans lui, le cyrillique vient des pages du jeu. Un `<name>_runes.png`, la même grille contenant U+1600 à U+16FF, dessine les runes de la même façon. Les versions 1.20.1 et 1.21.1 lisent à la place une définition de police à `assets/<namespace>/font/<name>.json`, dont le fournisseur `bitmap` peut pointer vers le même PNG, de sorte qu'un pack qui fournit les deux fichiers dessine les mêmes lettres sur les trois versions. `minecraft:default` désigne la police du jeu. Une police qu'aucun pack ne contient revient à la police du jeu, avec un avertissement dans `rdpl.log`.

Un pack modifie la police RDPL en fournissant son propre `assets/resourcedatapackloader/textures/font/rdpl.png` (et `rdpl_bold.png`, `rdpl_italic.png` ainsi que leurs feuilles `_cyrillic.png` et `_runes.png`), qui la remplace partout, texte du jeu compris. Un pack qui fournit `assets/minecraft/textures/font/ascii.png`, par exemple une copie de celui de vanilla, donne à la place cette police au texte propre au jeu, avec le cyrillique et les runes issus des pages du jeu ; le texte propre à RDPL garde la police RDPL sauf si `saysFont` vaut `minecraft:default`.

Les titres et lignes de cartes, les messages Says et les notes d'accueil et de retenue acceptent les marques en ligne du tableau sous Introduction au monde, Mise en forme du texte : gras, italique, gras italique, barré, code, liens et passages runiques. Un passage en gras est dessiné dans la variante `_bold` de la police et un passage en italique dans sa variante `_italic` ; pour une police sans cette variante, le passage prend le style gras ou italique du jeu, et la carte est dimensionnée d'après les passages tels qu'ils sont dessinés. Les joueurs sans le mod reçoivent les mêmes marques sous forme de mise en forme du chat, et un passage runique sous forme de ses lettres ordinaires.

### Règles intégrées

*cartes*

| Id | Le message | D'où vient son texte |
| --- | --- | --- |
| `rdpl:gate_unlocked` | Un portail s'ouvre | `unlockedMessage` dans [Portails](#portails) |
| `rdpl:gate_blocked` | Un portail fermé renvoie un joueur, dans la barre d'action | `blockedMessage` dans [Portails](#portails) |
| `rdpl:team_joined` | Un joueur rejoint un camp | le `displayName` du camp |
| `rdpl:team_lead` | Le rôle de chef d'un camp échoit à un joueur | `leadSays` dans [Équipes](#équipes) |
| `rdpl:team_picked` | Un joueur est tiré au sort pour un camp | le `displayName` du camp |
| `rdpl:team_round_ended` | La manche est terminée, donc un joueur est déplacé vers un camp | le `displayName` du camp |
| `rdpl:lobby_joins` | Un joueur qui se connecte en cours de manche est envoyé au hall | `opens.joinsSays` dans [Le hall d'attente](#le-hall-dattente) |
| `rdpl:lobby_note` | La ligne du hall au milieu de l'écran | `opens.says`, `opens.leaderSays` dans [Le hall d'attente](#le-hall-dattente) |
| `rdpl:scoring_results` | Le classement à la fin d'une manche, pour chaque joueur | `results.card`, `results.title`, `results.icon`, `results.image`, `results.background`, `results.seconds` dans [Résultats](#résultats) |
| `rdpl:scoring_out` | Un joueur éliminé | `ends.outSays` dans [Comment une manche se termine](#comment-une-manche-se-termine) |
| `rdpl:reset_lead` | Le chef réinitialise la manche | `reset.leadSays` dans [Réinitialiser une manche](#réinitialiser-une-manche) |
| `rdpl:reset_vote` | Un vote de réinitialisation est lancé | `reset.voteSays` |
| `rdpl:reset_pass` | Le vote est adopté | `reset.passSays` |
| `rdpl:reset_fail` | Le vote échoue | `reset.failSays` |
| `rdpl:anvil_waits` | Le travail d'une enclume attend un progrès | [Travail à l'enclume](#travail-à-lenclume) |
| `rdpl:threat` | La tranche de menace d'un joueur change | `threatSays` |
| `rdpl:prospect` | Chaque ligne que rapporte une trouvaille de prospection | la trouvaille |
| `rdpl:prospect_none` | La prospection n'a rien trouvé | le fichier de langue |
| `rdpl:pregen_ended` | La prégénération se termine ou s'arrête | `pregenFinishedSays`, `pregenStoppedSays` dans [Prégénération](#prégénération) |
| `rdpl:pregen_running` | La ligne de progression qu'un joueur voit en se connectant pendant la prégénération | `pregenRunningSays` |

`welcomeSays` n'est pas une règle et garde son logo ; une règle `first_join` ou `dimension_enter` s'y ajoute. Les décomptes et les totaux d'une manche dans la barre d'action restent tels que leurs réglages les font.

---

# Contrôle

## La couche de contrôle

*contrôle*

Tout ce qui arrête ou modifie la génération est regroupé, et chaque groupe a une clé dans la catégorie `control` de la configuration, avec trois valeurs :

| Valeur | Ce qu'elle signifie |
| --- | --- |
| `default` | Le pack décide. Les valeurs de la configuration servent de repli |
| `global` | La configuration l'emporte. Les sections des packs sont ignorées |
| `off` | Le groupe est entièrement désactivé et aucun pack ne peut l'activer |

Les groupes sont `ores`, `biomes`, `generators`, `structures`, `spawning`, `bedrock`, `voidWorld`, `recipes`, `terrain`, `entities`, `chunks`, `commands` et `server`.

Les réglages se résolvent **biome → modèle de monde → configuration**. Le bloc `settings` d'un modèle de monde utilise les mêmes noms de clés que la configuration, de sorte qu'un pack les définit comme vous le feriez :

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

Quand le contrôle d'un groupe est sur `default`, ceux-ci l'emportent ; sur `global`, ils sont ignorés ; et sur `off`, le groupe entier ne fait rien, quoi que dise un pack.

## Ce que fait chaque groupe

*contrôle*

### Minerais

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockOres": true,
    "oreWhitelist": ["minecraft", "mypack"],
    "oreTypes": ["COAL", "IRON"],
    "oreTypesAreBlacklist": true,
    "blockOreDimensions": [0, -1],
    "blockOreDimensionsAreBlacklist": false,
    "prospectItems": ["minecraft:compass=iron_vein|coal_seam", "mypack:dowsing_rod=*,12"],
    "prospectItemsAreBlacklist": false,
    "prospectWear": 2,
    "prospectSlow": 2,
    "prospectDrops": false
  }
}
```

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `blockOres` | booléen | `false` | Empêche tous les mods et Minecraft de générer du minerai, sauf les mods nommés dans `oreWhitelist`. Seule la génération qui passe par l'événement de génération de minerai de Forge peut être atteinte, ce qui couvre Minecraft et la plupart des mods, mais pas tous |
| `oreWhitelist` | liste d'ids de mods | `["minecraft"]` | Les mods encore autorisés à générer du minerai tant que `blockOres` est activé |
| `oreTypes` | liste de types de minerai | aucun | Les types de minerai auxquels s'applique le blocage, écrits comme les noms Forge, `COAL`, `IRON`. Vide signifie tous les types |
| `oreTypesAreBlacklist` | booléen | `true` | Activé, les types de `oreTypes` sont ceux qui sont bloqués. Désactivé, seuls ces types se génèrent |
| `blockOreDimensions` | liste d'int | aucun | Les dimensions auxquelles s'applique le blocage des minerais, vide signifiant chacune d'elles. Une dimension hors du périmètre n'est pas touchée du tout, donc les minerais d'un autre mod s'y génèrent alors que l'Overworld reste bloqué |
| `blockOreDimensionsAreBlacklist` | booléen | `false` | Activé, les dimensions listées sont celles qu'on laisse tranquilles |
| `prospectItems` | liste de `item=entries` | aucun | Les objets qui prospectent les entrées de worldgen de forme `vein` quand un joueur accroupi casse un bloc avec l'un d'eux en main. La forme est décrite dans le paragraphe ci-dessous |
| `prospectItemsAreBlacklist` | booléen | `false` | Activé, la liste de chaque objet désigne les entrées qu'il ne lit pas |
| `prospectWear` | int | `2` | Combien de fois l'usure normale coûte à l'outil une casse de prospection. `2`, le double, est le minimum autorisé, et un objet sans durabilité ne paie rien |
| `prospectSlow` | int | `2` | Combien de fois plus longtemps un joueur accroupi avec un objet marqué met à casser un bloc. `1` est la vitesse normale |
| `prospectDrops` | booléen | `false` | Activé, un bloc cassé en mode prospection lâche encore son butin et donne de l'expérience. Désactivé, l'échantillon est consommé |

**La lecture.** Une entrée `prospectItems` s'écrit `item=entry|entry[,radius in chunks]`, ou `item=*[,radius]` pour chaque entrée de filon, le rayon valant 8 par défaut. Un joueur accroupi qui casse un bloc avec un tel objet en main reçoit, pour chaque entrée qu'il lit, `Possible hit on <ore> <direction> of this location, <deeper down | higher up | at about this depth>` — un des huit points cardinaux ou intercardinaux depuis le bloc cassé vers le filon généré le plus proche, et jamais une position ; `at this location` quand le bloc se trouve déjà à portée du filon, et `No sign of anything here` quand rien n'est généré dans le rayon. Le minerai est nommé par le `prospectAs` de l'entrée, sinon par son nom de fichier. La lecture rejoue les mêmes tirages que la génération, donc elle est juste pour un terrain pas encore créé, et un objet marqué indique dans son infobulle ce qu'il prospecte.

### Biomes

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockBiomes": true,
    "biomeWhitelist": ["minecraft", "mypack"],
    "biomeNames": ["minecraft:mesa", "minecraft:mesa_rock"],
    "biomeNamesAreBlacklist": true,
    "blockBiomeDimensions": [0],
    "blockBiomeDimensionsAreBlacklist": false
  }
}
```

| Réglage | Type | Défaut | Ce que ça fait |
| --- | --- | --- | --- |
| `blockBiomes` | booléen | `false` | Empêche tous les biomes de se générer, sauf ceux des mods de `biomeWhitelist`. Les biomes bloqués sont remplacés sur la carte de biomes terminée, la seule façon d'atteindre les océans, les îles de champignons, les variantes de mesa, la jungle, les collines et les rivages. Bloquez-les tous et l'Overworld devient de lui-même un monde du vide |
| `biomeWhitelist` | liste d'ids de mods | `minecraft` | Les mods dont les biomes se génèrent encore tant que `blockBiomes` est activé. Un biome de pack utilise le namespace du pack |
| `biomeNames` | liste de noms de biomes | aucun | Les biomes auxquels cela s'applique par leur nom, quel qu'en soit le propriétaire et quoi que dise la liste blanche. Un nom courant comme `Birch Forest` ou un nom de registre |
| `biomeNamesAreBlacklist` | booléen | `true` | Activé, les noms de `biomeNames` sont bloqués. Désactivé, seuls ces noms se génèrent |
| `blockBiomeDimensions` | liste d'int | `0`, l'Overworld | Les dimensions auxquelles s'applique le blocage des biomes. Vide signifie chacune d'elles |
| `blockBiomeDimensionsAreBlacklist` | booléen | `false` | Activé, le blocage ignore les dimensions listées. Désactivé, il ne s'applique qu'à elles |

`blockBiomes` et `biomeWhitelist` fonctionnent par mod, et `biomeNames` avec `biomeNamesAreBlacklist` par nom. Les biomes bloqués sont remplacés sur la carte de biomes terminée, ce qui est la seule façon d'atteindre les océans, les îles de champignons, les variantes de mesa, la jungle, les collines et les rivages, car ils sont choisis en dehors des listes qu'un mod peut modifier. Bloquez tous les biomes et l'Overworld devient de lui-même un monde du vide. `blockBiomeDimensions` limite le tout à certaines dimensions, vide signifiant chacune d'elles, et `blockBiomeDimensionsAreBlacklist` transforme cette liste en exclusion.

### Générateurs

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockWorldGenerators": true,
    "generatorWhitelist": ["minecraft", "mypack"],
    "blockedGenerators": ["tconstruct"],
    "blockGeneratorDimensions": [0],
    "blockGeneratorDimensionsAreBlacklist": false,
    "generatorTypes": ["ores", "lakes"],
    "generatorTypesAreBlacklist": true,
    "generatorTypeMap": ["mrtjpcore=ores", "deworldgenhandler=structures"],
    "logBlockedGenerators": true
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `blockWorldGenerators` | booléen | `false` | Empêche les autres mods de générer via leurs propres générateurs de monde, c'est ainsi que les mods ajoutent ce que les événements de Forge ne voient jamais : îles de slimes, cristaux de grotte et autres. La génération propre à ce mod par les packs n'est jamais bloquée |
| `generatorWhitelist` | liste d'identifiants de mods | `minecraft` | Les mods qui ont encore le droit d'exécuter leurs propres générateurs |
| `blockedGenerators` | liste d'identifiants de mods ou de fragments de nom de classe | aucun | Générateurs individuels bloqués d'office, quoi que dise la liste blanche |
| `blockGeneratorDimensions` | liste d'entiers | `0`, la surface | Les dimensions concernées. Vide signifie toutes |
| `blockGeneratorDimensionsAreBlacklist` | booléen | `false` | Activé, le blocage ignore les dimensions listées. Désactivé, il ne s'applique qu'à elles |
| `generatorTypes` | liste de types | aucun | Bloque selon ce qu'un générateur produit plutôt que selon son propriétaire : `ores`, `structures`, `flora`, `lakes`, `terrain`, ou `unknown` pour ceux qui ne correspondent à rien |
| `generatorTypesAreBlacklist` | booléen | `true` | Activé, les types listés sont bloqués. Désactivé, seuls ces types se génèrent |
| `generatorTypeMap` | liste de `motif=type` | aucun | Types pour les générateurs dont le nom de classe ne dit rien, le motif étant un identifiant de mod ou une partie du nom de classe d'un générateur. Les entrées associées sont vérifiées avant les mots intégrés, elles corrigent donc aussi un générateur que les mots interprètent de travers |
| `logBlockedGenerators` | booléen | `true` | Journalise chaque générateur avec le type qui lui a été attribué la première fois qu'il est bloqué. `/rdplserver generators` affiche les totaux cumulés par mod et par type |

`blockWorldGenerators` empêche les autres mods de générer via leurs propres générateurs de monde, c'est ainsi que les mods ajoutent ce que les événements de Forge ne voient jamais, îles de slimes, cristaux de grotte et autres. `generatorWhitelist` conserve les mods nommés, `blockedGenerators` désigne des générateurs individuels, et la génération propre à ce mod par les packs n'est jamais bloquée. `blockGeneratorDimensions` la limite à certaines dimensions, `blockGeneratorDimensionsAreBlacklist` permettant d'inverser la liste.

`generatorTypes` bloque selon ce qu'un générateur produit plutôt que selon le mod qui le possède : `ores`, `structures`, `flora`, `lakes`, `terrain`, ou `unknown` pour ceux qui ne correspondent à rien. `generatorTypesAreBlacklist` décide du sens : activé, les types listés sont bloqués ; désactivé, seuls les types listés se génèrent. Un type bloque quoi que dise la liste blanche, comme le fait `oreTypes`, ce qui permet d'empêcher tous les mods d'ajouter des minerais tout en laissant leurs donjons et leurs arbres tranquilles.

Le type est déduit du nom de classe du générateur, comparé à une liste de mots intégrée pour chaque type. Cela interprète correctement la plupart des mods, `NetherOreGenerator` donne des minerais, `SlimeIslandGenerator` des structures, mais un générateur dont le nom ne désigne rien de précis, comme le `SimpleGenHandler` de ProjectRed ou le `DEWorldGenHandler` de Draconic Evolution, ressort comme `unknown`. `generatorTypeMap` corrige cela à la main, un `motif=type` par ligne, où le motif est un identifiant de mod ou une partie du nom de classe d'un générateur :

```
mrtjpcore=ores
deworldgenhandler=structures
```

Les entrées associées sont vérifiées avant les mots intégrés, elles corrigent donc aussi un générateur que les mots interprètent de travers. Activez `logBlockedGenerators` et chaque générateur est journalisé avec le type qui lui a été attribué la première fois qu'il est bloqué, et `/rdplserver generators` affiche les totaux cumulés par mod et par type.

### Remplacements

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockReplacements": [
      "bigreactors:oreyellorite=minecraft:stone",
      "mekanism:oreblock:0=minecraft:stone"
    ],
    "blockReplacementDimensions": [0],
    "blockReplacementDimensionsAreBlacklist": false,
    "blockReplacementMinHeight": 0,
    "blockReplacementMaxHeight": 255,
    "blockReplacementKey": "cleanup_v1"
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `blockReplacements` | liste de `bloc=bloc` | aucun | Blocs retirés des chunks déjà existants, avec une méta facultative de chaque côté. Chaque chunk n'est traité qu'une fois à son chargement et marqué dans ses propres données, il n'est donc jamais traité deux fois |
| `blockReplacementDimensions` | liste d'entiers | aucun | Les dimensions concernées. Vide signifie toutes |
| `blockReplacementDimensionsAreBlacklist` | booléen | `false` | Activé, le remplacement ignore les dimensions listées. Désactivé, il ne s'applique qu'à elles |
| `blockReplacementMinHeight` | entier | `0` | Le y le plus bas examiné |
| `blockReplacementMaxHeight` | entier | `255` | Le y le plus haut examiné |
| `blockReplacementKey` | chaîne | `0000` | Changez-la et chaque chunk repasse par le remplacement |

`blockReplacements` retire des blocs des chunks déjà existants, un `bloc=bloc` par ligne, avec une méta facultative de chaque côté :

```
bigreactors:oreyellorite=minecraft:stone
mekanism:oreblock:0=minecraft:stone
tconstruct:ore:0=minecraft:netherrack
```

Chaque chunk est traité une seule fois, lorsqu'il est chargé depuis le disque, et marqué dans les données propres au chunk pour ne jamais être traité deux fois. Un chunk généré pour la première fois est nettoyé à son prochain chargement plutôt qu'immédiatement, car les chunks voisins y écrivent encore pendant sa génération. Un chunk situé en bordure des terres explorées est nettoyé mais pas marqué, il est donc nettoyé de nouveau une fois que le terrain qui l'entoure existe. `blockReplacementDimensions` et `blockReplacementDimensionsAreBlacklist` choisissent où, `blockReplacementMinHeight` et `blockReplacementMaxHeight` choisissent la tranche du monde à examiner, et `blockReplacementKey` est une chaîne que vous changez pour que chaque chunk repasse par le traitement. Il s'exécute que `retrogen` soit activé ou non, car un monde à nettoyer est généralement un monde où l'on ne veut pas ajouter de nouveaux filons. Il ne fait que remplacer des blocs : ce qu'un mod a généré sous forme de structure ne peut pas être retiré ainsi, car le terrain qu'elle a remplacé n'a jamais été enregistré.

### Villages

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageBlocks": [
      "minecraft:cobblestone=mypack:ruby_brick",
      "minecraft:cobblestone=minecraft:mossy_cobblestone,20",
      "minecraft:planks=minecraft:sandstone,100,under=minecraft:sand"
    ],
    "villagePieces": ["field1", "field2"],
    "villagePiecesAreBlacklist": true,
    "villagePlotsLeast": 12,
    "villagePlotsMost": 30,
    "villagePlotsBackRow": true,
    "villageTieStreets": true,
    "villageBlockSizes": ["32=3", "64=1"],
    "villageLayout": "mypack:downtown"
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `villageBlocks` | liste de `original=remplacement` | aucun | Les blocs dont sont construits les éléments du village, appliqués après que tous les autres mods ont eu leur mot à dire. Une paire peut porter une probabilité et une condition, elle devient alors une règle ; les champs sont dans le tableau ci-dessous |
| `villagePieces` | liste de noms d'éléments | aucun | Éléments de village vanilla nommés un par ligne : `house1`, `house2`, `house3`, `house4garden`, `church`, `woodhut`, `hall`, `field1`, `field2`. Une parcelle de pack est désignée par son propre modèle, tout comme les éléments ajoutés par d'autres mods |
| `villagePiecesAreBlacklist` | booléen | `true` | Activé, les éléments listés sont bloqués. Désactivé, seuls ces éléments se génèrent, et une liste blanche ne retire jamais que les éléments propres à vanilla |
| `villagePlotsLeast` | entier | `0` | Le moins de parcelles bâties dont un village se contente, en comptant maisons, fermes et parcelles de pack mais jamais les routes, les torches ni le puits. Un village dont le plan est plus petit est régénéré quelques fois et le plan le plus grand l'emporte. `0` conserve vanilla |
| `villagePlotsBackRow` | booléen | `true` | Une fois le village développé, une seconde passe installe une parcelle directement derrière chaque parcelle qui donne sur une rue, tournée vers elle, avec le même tirage et le même test de place, de sorte que l'intérieur d'un îlot entre deux rues soit bâti plutôt que laissé nu |
| `villagePlotsMost` | entier | `0` | Le plus qu'il peut en avoir ; au maximum il cesse de croître tout net, plus de bâtiments ni de routes. `0` conserve vanilla |
| `villageTieStreets` | booléen | `true` | Activé, un quartier qui ne peut pas raccorder ses rues au village existant se voit tracer une rue de liaison droite jusqu'à la rue la plus proche avec laquelle il s'aligne. Désactivé, un tel quartier est démoli |
| `villageBlockSizes` | liste de `taille=poids` | aucun | La profondeur des îlots entre les rues parallèles d'une ville, tirée une fois par quartier d'après la position de sa place. Vide, tous les îlots prennent la taille de la plus grande parcelle que le pack fournit |
| `villageLayout` | chaîne | vide | Désigne une [carte de plan de ville](#cartes-de-plan-des-villes) qui dispose le village d'après un plan de rues dessiné au lieu de le faire croître |

Les villages utilisent les mêmes listes `structure=valeur` que toutes les autres structures, sous le nom `villages`, de sorte que `structureSpacing`, `structureMinDistanceFromSpawn`, `structureBiomes` et `structureBiomesAreBlacklist` les concernent tous. Une liste `structureBiomes` qui n'est pas une liste noire ajoute aussi tout biome nommé que la liste propre à la structure ne contenait pas, ce qui permet d'envoyer des villages dans les montagnes ; nommez-les par leur nom de registre pour cela, car seuls les noms de registre peuvent ajouter. Leur espacement a un plancher de 9, car vanilla en soustrait 8. `villagePieces` appartient au même groupe, un seul interrupteur couvre donc tout ce qui touche à l'emplacement des villages et à leurs matériaux, tandis que le groupe `villages` ne couvre que les parcelles qu'ajoute un pack.

`villageBlocks` remplace les blocs dont un village est construit, sous forme de paires `original=remplacement` : `minecraft:cobblestone=mypack:ruby_brick`. Il est appliqué après que tous les autres mods ont eu leur mot à dire, un pack l'emporte donc toujours, même contre les mods qui changent les matériaux des villages selon le biome. Les deux côtés acceptent un nom de bloc simple ou un nom avec états. Les routes sont désignées séparément par `villagePathBlock` et ses réglages voisins.

Une paire peut porter après elle une probabilité et une condition, écrites sous forme de champs séparés par des virgules, et elle devient alors une règle plutôt qu'un simple remplacement. `minecraft:cobblestone=minecraft:mossy_cobblestone,20` patine un cinquième des pavés qu'un village pose ; `minecraft:planks=minecraft:sandstone,100,under=minecraft:sand` change le sol uniquement là où une maison se dresse sur du sable. Les champs qui suivent la paire peuvent être donnés dans n'importe quel ordre, et une entrée qui nomme un champ qu'elle ne sait pas lire est refusée en entier plutôt qu'appliquée à moitié.

| Champ | Valeur | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| chance | entier, de 1 à 100 | `100` | Combien de fois la règle s'applique, sur cent |
| `at=` | nom de bloc | aucun | Uniquement là où ce bloc se trouve déjà à l'emplacement bâti |
| `under=` | nom de bloc | aucun | Uniquement là où ce bloc se trouve directement sous l'emplacement |

Une paire simple est résolue au moment où un élément demande au jeu avec quoi il doit construire, elle change donc d'un coup tous les murs de ce bloc. Une règle est évaluée à l'endroit où le bloc est réellement posé, emplacement par emplacement, ce qui donne un sens à la probabilité et à la condition, et elle voit le bloc tel qu'il est sur le point d'être placé, après l'intervention de toute paire simple. Les emplacements touchés par une probabilité sont calculés à partir de la graine du monde et de l'emplacement lui-même, de sorte qu'un même monde patine toujours les mêmes blocs, quel que soit le nombre de fois où il est généré.

Les routes ne sont jamais soumises à des règles, afin que les pentes, les ponts et les motifs de carrefour lisent toujours la route qu'ils ont posée. Une parcelle à modèle pose son propre fichier `.nbt` au lieu de construire à la manière du jeu, les règles n'y pénètrent donc pas ; ses blocs sont ceux du fichier. Les paires simples comme les règles fonctionnent que `terrainAdaptation` soit activé ou non.

`villagePieces` désigne des éléments de village vanilla, `house1`, `house2`, `house3`, `house4garden`, `church`, `woodhut`, `hall`, `field1` et `field2`, et `villagePiecesAreBlacklist` décide du sens, ce qui permet de supprimer les champs de blé de vanilla en gardant les maisons, ou de ne lister que les éléments voulus. Une parcelle de pack est désignée par son propre modèle : soit par le nom complet, `mypack:big_house`, soit simplement `big_house`, soit par le nom propre de la parcelle si vous préférez. Un pack peut donc fournir dix parcelles et un modèle de monde peut en retirer une sans toucher aux neuf autres. Il en va de même des éléments ajoutés par d'autres mods, dont les maisons de Tektopia ou les parcelles de Recurrent Complex : une liste blanche ne retire jamais que les éléments propres à vanilla, y lister ceux de vanilla que vous voulez ne supprimera donc pas discrètement ceux de quelqu'un d'autre. Pour retirer un élément d'un mod, utilisez une liste noire et nommez-le, `tekhouse2` et consorts.

`villagePlotsLeast` et `villagePlotsMost` bornent le nombre de parcelles avec lesquelles un village est bâti, en comptant maisons, fermes et parcelles de pack, jamais les routes, les torches ni le puits. Un village dont le plan tombe sous le minimum est régénéré en plus grand, en quelques essais, et le plan le plus grand l'emporte, un terrain exigu peut donc rester en deçà de la demande. Au maximum, le village cesse de croître tout net : plus de bâtiments ni de routes. `0` à l'une ou l'autre extrémité conserve le comportement vanilla de ce côté.

`villageTieStreets`, activé sauf indication contraire, trace une rue de liaison pour un quartier qui ne peut pas raccorder ses rues au village existant : une rue droite sur toute la largeur, depuis l'une des extrémités de rue du quartier jusqu'à la rue existante la plus proche avec laquelle il s'aligne, lorsque cette ligne est plus longue que la largeur d'une route, ne dépasse pas 112 rangées, est libre de tout élément, ne longe pas une rue parallèle, ne traverse pas un tunnel et est assez plane pour qu'on y marche. Sans lui, un tel quartier est démoli, ce qui cantonne une ville sur terrain accidenté à sa place et à ses quatre rues ; avec lui, le quartier se raccorde et continue de croître. Une ville en terrain plat en a rarement besoin, et il est activé par défaut pour qu'un pack qui demande une grande ville en obtienne une ; désactivez-le pour garder petite une ville sur terrain accidenté.

`villageBlockSizes` fixe la profondeur des îlots entre les rues parallèles d'une ville, sous forme d'entrées pondérées `taille=poids` : `32=3` et `64=1` donnent trois quartiers sur quatre avec des îlots profonds de 32 et le reste avec 64. Chaque quartier tire sa taille une fois d'après la position de sa place, un même monde obtient donc toujours le même mélange. Ses rues ouvrent des rues latérales le long de leur tracé tous les deux îlots plus une largeur de route, de sorte qu'un quartier de 16 forme une grille fine et un quartier de 64 une grille grossière, et deux rues parallèles conservent entre elles autant de blocs plus ceux du quartier voisin, si bien qu'un quartier de 32 à côté d'un quartier de 64 laisse 96 entre elles. Seules les parcelles qui tiennent dans la profondeur sont bâties le long des rues d'un quartier, et parmi celles qui conviennent, la probabilité d'une parcelle est son poids multiplié par sa largeur, de sorte que les îlots profonds favorisent les bâtiments qui les remplissent et qu'une parcelle de 64 de large ne donne jamais sur un îlot profond de 32. La longueur des rues et l'espacement des places suivent toujours la plus grande parcelle que le pack fournit, ce qui permet à chaque quartier de rejoindre la ville. Vide, tous les îlots prennent la taille de cette plus grande parcelle et les rues ne se ramifient qu'à leurs extrémités, comme avant.

`villageLayout` désigne une [carte de plan de ville](#cartes-de-plan-des-villes) qui dispose le village d'après un plan de rues dessiné au lieu de le faire croître ; vide, il croît comme d'habitude.

#### Routes de village

*villages*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villagePathBlock": "minecraft:stonebrick",
    "villagePathSupportBlock": "minecraft:gravel",
    "villagePathBridgeBlock": "minecraft:planks",
    "villagePathBridgeBarrierBlock": "minecraft:oak_fence",
    "villagePathBridgeBarrierHeight": 1,
    "villagePathBridgeSidewalkBlock": "minecraft:planks",
    "villagePathBridgeDrop": 3,
    "villagePathBridgeFrameBlock": "minecraft:stonebrick",
    "villagePathBridgeFrameTopBlock": "minecraft:stone_slab",
    "villagePathBridgeFrameHeight": 4,
    "villagePathBridgeFrameRun": 24,
    "villagePathBridgeFrameLeast": 24,
    "villagePathVergeBlock": "",
    "villagePathVergeWaterBlock": "minecraft:planks",
    "villagePathTunnelBlock": "minecraft:stonebrick",
    "villagePathTunnelDepth": 10,
    "villagePathTunnelLightBlock": "minecraft:sea_lantern",
    "villagePathTunnelLightRun": 8,
    "villageSewerBlock": "minecraft:stonebrick",
    "villageSewerDepth": 8,
    "villageSewerHeight": 3,
    "villageSewerWidth": 5,
    "villageSewerWaterBlock": "minecraft:water",
    "villageSewerWalkBlock": "minecraft:stonebrick:3",
    "villageSewerLightBlock": "minecraft:glowstone",
    "villageSewerLightRun": 8,
    "villageSewerLadderBlock": "minecraft:ladder",
    "villageSewerCoverBlock": "minecraft:iron_trapdoor",
    "villageSewerMossBlock": "minecraft:mossy_cobblestone",
    "villageSewerMossChance": 30,
    "villageSewerVineBlock": "minecraft:vine",
    "villageSewerVineChance": 20,
    "villageSewerWellEntrance": true,
    "villagePathCenterBlock": "minecraft:quartz_block",
    "villagePathCenterDash": 2,
    "villagePathLineBlock": "minecraft:stone_slab",
    "villagePathSidewalkBlock": "minecraft:stonebrick",
    "villagePathSidewalkWidth": 2,
    "villagePathExtraWidth": 1,
    "villagePathMinimumWidth": 0,
    "villagePathAlleyBlock": "minecraft:gravel",
    "villagePathAlleyChance": 25,
    "villagePathFlatRun": 6,
    "villagePathIntersects": ["mypack:crosswalk"],
    "villagePathPiers": ["railed", "pilings", "boardwalk"],
    "villagePathDeadEnds": ["barrier", "sidewalk"],
    "villagePathLampBlock": "minecraft:iron_bars",
    "villagePathLampHeight": 3,
    "villagePathLampTopBlock": "minecraft:skull:1{SkullType:3}",
    "villagePathLampSideBlock": "",
    "villagePathLampStructure": "",
    "villageWellStructure": ["mypack:plaza_spire=3", "mypack:fountain=1", "empty=1"],
    "villagePathPierCargo": ["minecraft:chest=3", "mypack:crate=2,3", "empty=4"],
    "villagePathPierLoot": "resourcedatapackloader:chests/pier_cargo"
  }
}
```

Tout ce qui suit ne fait quelque chose que lorsque `terrainAdaptation` est activé. Chacun de ces réglages est vide ou nul par défaut, ce qui laisse les routes de vanilla exactement comme elles étaient.

**Mélanger des blocs.** Certains réglages de bloc acceptent un mélange au lieu d'un seul bloc : des blocs séparés par des virgules, chacun suivi d'une espace et d'un poids, comme dans `"minecraft:stonebrick 3, minecraft:cobblestone 1"`. Un bloc sans poids compte une fois. Chaque bloc posé tire dans le mélange à partir de la graine du monde et de son emplacement, de sorte qu'un même monde construit toujours le même motif. Les réglages qui acceptent un mélange sont `villagePathVergeBlock`, `villagePathVergeWaterBlock`, `villagePathTunnelBlock`, `villagePathBridgeFrameBlock`, `villagePathBridgeFrameTopBlock`, `villageRailTunnelBlock`, `villageRailDeckBlock`, `villageRailSupportBlock`, `villageRailBarrierBlock`, `villageRailBridgeFrameBlock`, `villageRailBridgeFrameTopBlock`, `villageSubwayTunnelBlock`, `villageSubwayPlatformBlock`, `villageSubwayRailingBlock`, `villageSubwayBenchEndBlock` et `villageSewerMossBlock`. Tous les autres réglages de bloc utilisent partout le premier bloc d'un mélange.

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `villagePathBlock` | bloc | vide | La surface de la route. Vide, conserve le bloc que le biome utiliserait, grès sur le sable, argile durcie sur la mesa, chemin de terre sur la terre |
| `villagePathVergeBlock` | bloc | vide | Le bloc dont est comblé le sol à côté d'une route et sous une parcelle là où le village doit créer du terrain. Vide, suit le terrain, en posant le remplissage propre au biome avec de l'herbe au-dessus là où ce serait de la terre |
| `villagePathVergeWaterBlock` | bloc | `minecraft:planks` | Ce que devient ce remplissage là où il se trouve au-dessus de l'eau, pour qu'un bas-côté avancé sur un lac ne soit pas une colonne de terre. Il habille aussi un seuil de pierre laissé au-dessus de l'eau |
| `villagePathCenterBlock` | bloc | vide | Une ligne centrale au milieu de la route. Vide, n'en trace aucune |
| `villagePathCenterDash` | nombre | `0` | Met cette ligne en pointillés : N blocs de ligne, puis un de route. Ancrés aux coordonnées du monde, les tirets d'un élément de route se poursuivent dans le suivant. `0` la garde continue |
| `villagePathLineBlock` | bloc | vide | Lignes de rive entre la route et le trottoir. Vide, n'en trace aucune |
| `villagePathSidewalkBlock` | bloc | vide | Trottoirs, posés à niveau avec la route à l'extérieur des lignes de rive. Vide, n'en pose aucun |
| `villagePathSidewalkWidth` | nombre | `2` | La largeur de chaque trottoir, dès que `villagePathSidewalkBlock` est défini |
| `villagePathExtraWidth` | nombre | `0` | Blocs de route supplémentaires de chaque côté au-delà des 3 de vanilla. Élargit les éléments de route eux-mêmes, les maisons reculent donc devant une large rue |
| `villagePathMinimumWidth` | nombre | `0` | La route la plus étroite qui vaille la peine d'être posée. Un tronçon qui ne peut pas tenir dans son habillage complet retombe en une ruelle nue de 3 de large ; en dessous de cette largeur il n'est pas posé du tout et le village se dispose autour. `0` ne refuse jamais |
| `villagePathAlleyBlock` | bloc | vide | La surface d'une ruelle, une route trop étroite pour porter lignes et trottoirs. Une ruelle court entre les trottoirs des rues qu'elle rencontre et n'en porte aucun en propre, et aucun passage piéton n'est peint là où elle croise une rue. Vide, pose les ruelles avec le bloc de route |
| `villagePathAlleyChance` | nombre | `0` | La probabilité en pourcentage qu'une route soit posée comme ruelle au lieu de s'élargir en rue complète. `0` ne pose de ruelle que là où une rue complète ne tient pas, ce qui en pratique ne concerne que le premier quartier, bondé. L'augmenter change les routes posées et remodèle donc tout le graphe des rues ; mesuré à 50, il a coûté sept carrefours scindés de plus, augmentez-le donc et vérifiez le résultat |
| `villagePathFlatRun` | nombre | `6` | Sur combien de blocs une route garde la même hauteur avant de monter d'une marche. Ancré aux coordonnées du monde pour que les éléments voisins s'accordent. `0` fait une marche à chaque bloc, comme les pentes de vanilla |
| `villagePathIntersects` | liste | aucun | Motifs peints aux carrefours, désignés par clé de registre depuis le dossier `<namespace>/pathintersects/` d'un pack. Une entrée peint tous les carrefours à l'identique ; avec plusieurs, un motif est choisi par carrefour selon son poids |
| `villagePathDeadEnds` | liste | aucun | Comment une route en cul-de-sac est fermée, lorsqu'elle n'a pas fait pousser d'impasse, listé ci-dessous. Une entrée ferme tous les culs-de-sac à l'identique ; avec plusieurs, une est tirée par extrémité à partir de la graine du monde. Vide, laisse les culs-de-sac ouverts |
| `villagePathLampBlock` | bloc ou bloc avec données | `minecraft:oak_fence` | Le bloc dont est construit un lampadaire le long d'une route, empilé sur la bordure. Vide, ne dresse aucun lampadaire |
| `villagePathLampHeight` | nombre | `3` | Combien de blocs de haut s'élève le poteau avant sa tête |
| `villagePathLampTopBlock` | bloc ou bloc avec données | `minecraft:wool:15` | La tête au sommet du poteau. Vide, la laisse nue |
| `villagePathLampSideBlock` | bloc ou bloc avec données | `minecraft:torch` | La lumière accrochée de chaque côté de la tête, tournée vers l'extérieur. Vide, n'en accroche aucune |
| `villagePathLampStructure` | texte | vide | Un fichier de structure posé comme lampadaire entier au lieu d'empiler les trois blocs de lampadaire, nommé `mypack:street_lamp` et lu dans le dossier `structures` de ce pack. Il est centré sur l'emplacement du lampadaire, sa couche la plus basse sur la bordure, et les blocs qu'il pose sont protégés pour que rien d'autre ne les écrase. Vide, empile les blocs |
| `villageWellStructure` | liste | aucun | Fichiers de structure posés comme pièce maîtresse de la place à la place du puits, sous forme d'entrées pondérées `nom=poids` comme `mypack:plaza_spire=3`, lus dans le dossier `structures` de ce pack et tirés une fois par puits d'après sa position, de sorte qu'un même puits obtient toujours la même structure. Une entrée `empty=poids` garde le puits pour cette part. La structure choisie est centrée sur l'emprise de six par six du puits, sa couche la plus basse sur le sol de la place, ce sol est pavé sous elle, et les blocs qu'elle pose sont protégés pour que l'habillage de la place les laisse intacts. Une structure plus large s'étale sur l'anneau de la place. Sans entrée, le puits est construit |

Une route est habillée du milieu vers l'extérieur : ligne centrale, puis route, puis lignes de rive, puis trottoirs. Les largeurs qui ne tiennent pas se replient au lieu de déborder, un tronçon étroit perd donc discrètement son trottoir avant de perdre sa route.

`villagePathBlock` et ses réglages voisins l'emportent sur `villageBlocks`. Un bloc de route nommé est utilisé tel quel, alors que la table de correspondance ne touche que ce que la route aurait choisi d'elle-même. Laissez-les vides et la table décide, c'est ainsi qu'un pack garde la surface fidèle au biome tout en la recolorant.

**Les blocs de lampadaire portent des données.** Les trois blocs de lampadaire acceptent un nom simple, un nom avec métadonnées, ou un nom avec des données de bloc-entité entre accolades, `minecraft:skull:1{SkullType:3}`. Les accolades sont lues comme du NBT et appliquées à la bloc-entité après la pose du bloc, c'est ainsi qu'un lampadaire venu d'un autre mod conserve les réglages dont il a besoin. Un NBT erroné est signalé et ignoré au lieu d'empêcher la construction du lampadaire.

**Culs-de-sac.** Une route qui se termine en cul-de-sac sans avoir fait pousser d'impasse est fermée par `villagePathDeadEnds`, un style tiré par extrémité à partir de la graine du monde. Un style dont le bloc n'est pas défini sort du tirage, de sorte que `barrier` ne ferme rien tant que `villagePathBridgeBarrierBlock` ne nomme pas un bloc, et qu'une extrémité de ruelle ne prend jamais `sidewalk`.

| Valeur | Ce qu'elle fait |
| --- | --- |
| `sidewalk` | Pave la rangée d'extrémité avec le bloc de trottoir |
| `barrier` | Dresse le bloc de barrière le long de la rangée d'extrémité, sur `villagePathBridgeBarrierHeight` de haut |

**Motifs de carrefour.** `villagePathIntersects` désigne des fichiers qu'un pack fournit, chacun étant une petite image de ce qu'il faut peindre là où deux routes se rejoignent, dessinée en rangées de caractères uniques, un caractère par bloc.

`<namespace>/pathintersects/*.json`

Le chemin du fichier est la clé de registre du motif, que `villagePathIntersects` désigne ensuite.

```json
{
  "name": "Crosswalk",
  "weight": 3,
  "legend": { "w": "minecraft:quartz_block", "y": "minecraft:wool@4" },
  "mouth": ["wwww", "....", "wwww"],
  "corner": ["yy.", "y..", "..."]
}
```

| Clé | Valeur | Défaut | Ce qu'elle fait |
| --- | --- | --- | --- |
| `name` | chaîne | le nom du fichier | Le nom utilisé dans le journal |
| `weight` | entier, 1 et plus | `1` | Part des carrefours que ce motif remporte lorsque plusieurs sont listés |
| `legend` | objet d'un caractère vers un bloc | aucun | Les caractères que les rangées peuvent utiliser en plus des rôles ci-dessous. Un caractère qui est déjà un rôle est refusé avec une ligne de journal |
| `mouth` | liste de chaînes | aucun | Rangées peintes sur chaque approche, en dehors de la route qui croise. La première rangée est la plus proche du carrefour et les suivantes s'éloignent. Les caractères courent en travers de la route et se répètent là où une rangée est plus courte que la largeur de la route |
| `corner` | liste de chaînes | aucun | Rangées peintes à l'intérieur du carrefour lui-même. La première rangée est la plus proche du bord de la route qui croise, et dans une rangée le premier caractère est le plus proche du bord de la route elle-même, en allant vers l'intérieur. Une cellule que l'image n'atteint pas est laissée telle quelle |

Cinq caractères sont des rôles et non des blocs, ils suivent donc ce dont la route est déjà habillée : `r` est la surface de la route, `l` la ligne de rive, `s` le trottoir, `.` laisse le bloc exactement comme il était, et `c` est réservé et peint la surface de la route. Un rôle dont le pack n'a jamais défini le bloc retombe sur la surface de la route, et tout autre caractère est cherché dans la `legend`, retombant lui aussi sur la surface de la route.

Le motif qu'obtient un carrefour est calculé à partir de la graine du monde et de la position du carrefour lui-même, de sorte qu'un même monde peint toujours les mêmes carrefours. Un motif n'est peint que là où trois rues ou plus se rejoignent, à un carrefour ou à la place d'un puits ; deux rues qui se rejoignent forment un simple coude.

#### Ponts et pontons de village

*villages*

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `villagePathSupportBlock` | bloc | vide | La surface elle-même là où le sol est de la roche nue, ainsi que les piles et les pieds sous une route au-dessus de l'eau. Vide, conserve le gravier de vanilla, le grès dans les villages du désert |
| `villagePathBridgeBlock` | bloc | vide | Ce avec quoi une route traverse l'eau. Vide, conserve les planches de vanilla |
| `villagePathBridgeBarrierBlock` | bloc | vide | Barrières empilées sur les deux bords du tablier d'un pont. Vide, n'en construit aucune |
| `villagePathBridgeBarrierHeight` | nombre | `1` | Combien de blocs de haut s'élèvent ces barrières |
| `villagePathBridgeSidewalkBlock` | bloc | vide | Habille le trottoir là où une route franchit l'eau. Vide, prolonge le bloc de trottoir normal sur le pont |
| `villagePathBridgeDrop` | nombre | `0` | De combien le profil d'une route doit se tenir au-dessus du sol avant que le vide en dessous soit franchi par un pont plutôt que comblé. `0` garde les routes au sol : elles ne franchissent que l'eau. `3` est la règle que suit un viaduc ferroviaire. Cela déplace le profil, pas seulement l'habillage |
| `villagePathBridgeFrameBlock` | bloc | vide | Un portique au-dessus d'un long pont : un poteau de chaque côté du tablier et une poutre au sommet. Chaque portique porte une pile jusqu'au sol sous le tablier, et aucun lampadaire n'est dressé sur la rangée où il se trouve. Vide, n'en construit aucun |
| `villagePathBridgeFrameTopBlock` | bloc | vide | La poutre au sommet de ce portique. Vide, utilise `villagePathBridgeFrameBlock` |
| `villagePathBridgeFrameHeight` | nombre | `4` | Combien de blocs de hauteur libre le portique laisse au-dessus du tablier, la poutre se trouvant un bloc au-dessus |
| `villagePathBridgeFrameRun` | nombre | `24` | De combien de rangées les portiques sont espacés lorsqu'un pont est assez long pour en porter plusieurs |
| `villagePathBridgeFrameLeast` | nombre | `24` | Le plus court tronçon franchi par un pont qui reçoit un portique. Un pont plus court reste nu |
| `villagePathPiers` | liste | aucun | Styles de ponton pour une route qui s'arrête au-dessus de l'eau, listés ci-dessous. La queue en pont devient un ponton ; avec plusieurs entrées, un style est tiré par ponton. Vide, un tel pont reste un simple pont |
| `villagePathPierCargo` | liste | aucun | Cargaison disposée le long de l'intérieur des garde-corps d'un ponton, sous forme d'entrées pondérées listées ci-dessous. Une rangée sur deux de chaque ponton tire dans la liste de chaque côté, les poids décident donc de l'affluence d'un ponton. Vide, les pontons restent nus |
| `villagePathPierLoot` | texte | `resourcedatapackloader:chests/pier_cargo` | La table de butin dont est remplie une cargaison dotée d'un inventaire, tirée la première fois qu'elle est ouverte. Vide, une telle cargaison reste vide |

**Un tablier de niveau.** Chaque pont se tient à une seule hauteur d'un bout à l'autre, quelle que soit la hauteur de ses deux rives ; la route de chaque côté monte en rampe pour le rejoindre.

**Un vide à sec.** Une route comble un creux de manière pleine et ne franchit que l'eau par un pont, sauf si `villagePathBridgeDrop` indique une hauteur : une rangée dont le profil se tient à plus de ce nombre de blocs au-dessus du sol est alors posée sur des pieds, à la manière d'un viaduc ferroviaire qui franchit un ravin. Cela change le profil et non l'habillage, un village posé avec ce réglage ne correspond donc pas à un village posé sans.

**Portiques.** Un tronçon franchi par un pont de `villagePathBridgeFrameLeast` rangées ou plus porte des portiques au-dessus du tablier dès que `villagePathBridgeFrameBlock` nomme un bloc : un poteau de chaque côté et une poutre au sommet, avec `villagePathBridgeFrameHeight` blocs de hauteur libre en dessous. Plusieurs se dressent sur un long pont, espacés de `villagePathBridgeFrameRun` rangées et répartis symétriquement autour du milieu du tronçon, de sorte qu'un même pont porte toujours les mêmes portiques. Une rangée où une autre route croise le pont reste ouverte, et un ponton ne porte aucun portique : un appontement n'est pas un pont.

**Pontons.** Une route qui s'avance au-dessus de l'eau et se termine sur rien devient un ponton plutôt qu'un pont vers nulle part, dès que `villagePathPiers` nomme au moins un style. Avec plusieurs entrées, un style est tiré par ponton, à partir de la graine du monde et de l'extrémité du ponton, de sorte qu'un même monde construit toujours le même ponton. Chaque ponton repose sur des pilotis du bloc de support, enfoncés jusqu'au fond aux deux bords du tablier une rangée sur quatre, quel que soit son style. Le tablier est le bloc de pont, les garde-corps et les poteaux le bloc de barrière, et les pilotis le bloc de support.

| Valeur | Ce qu'elle fait |
| --- | --- |
| `railed` | Garde le tablier complet, uni, sans lignes ni bande de trottoir, et ferme l'extrémité avec le bloc de barrière |
| `pilings` | Ouvre les barrières latérales en poteaux toutes les quatre rangées, au-dessus de ces mêmes supports |
| `boardwalk` | Rétrécit le tablier à la largeur centrale de la route |

**Cargaison de ponton.** `villagePathPierCargo` dispose une cargaison sur un ponton. Une rangée sur deux tire dans la liste une fois de chaque côté, une colonne en retrait des garde-corps, ce qui laisse le milieu du tablier libre pour marcher, n'encombre jamais la rangée d'extrémité munie de garde-corps, et empêche deux cargaisons de se tenir côte à côte, car deux coffres posés au contact formeraient un seul grand coffre. Une pile n'est posée que là où chacun de ses blocs tient, et nommer le même bloc deux fois à des hauteurs différentes est le moyen d'obtenir sur un ponton des piles de tailles variées.

| Valeur | Ce qu'elle fait |
| --- | --- |
| `<block>=<weight>` | Un bloc et sa part des emplacements, posé sur une hauteur |
| `<block>=<weight>,<height>` | Idem, empilé sur ce nombre de blocs de haut, de 1 à 8 |
| `empty=<weight>` | La part du tablier laissée libre |

Un bloc qui porte un inventaire de butin, un coffre par exemple, est rempli depuis `villagePathPierLoot`, tiré la première fois qu'un joueur l'ouvre, comme un coffre vanilla. La table intégrée est du matériel de récupération marin facile à rassembler. Un pack la remplace en fournissant son propre `loot_tables/chests/pier_cargo.json` sous le namespace `resourcedatapackloader`, ou en nommant une table qui lui est propre.

#### Tunnels de village

*villages*

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `villagePathTunnelBlock` | bloc | vide | Revêt une route là où elle perce une colline au lieu de l'entailler : les parois de chaque côté du percement et la voûte au-dessus. Vide, ne perce aucun tunnel et une route entaille une colline comme avant |
| `villagePathTunnelDepth` | nombre | `10` | Quelle épaisseur de terrain doit se trouver au-dessus de la surface de la route avant qu'un tronçon soit percé plutôt qu'entaillé. Une élévation enfouie à cette profondeur sur douze rangées ou plus est maintenue à niveau et percée, ses approches moins profondes étant entaillées ; une bosse plus courte est entaillée comme avant. Ne compte que lorsque `villagePathTunnelBlock` nomme un bloc |
| `villagePathTunnelLightBlock` | bloc | vide | Une lumière encastrée dans la voûte du tunnel le long de son axe. Vide, n'éclaire rien |
| `villagePathTunnelLightRun` | nombre | `8` | L'écart en blocs entre ces lumières. Ancré aux coordonnées du monde, les lumières d'un élément de route se poursuivent dans le suivant ; un tunnel trop court pour atteindre l'un de ces emplacements est éclairé une fois, en son milieu |

**Tunnels.** Sans bloc de tunnel, une route qui rencontre une colline la gravit, d'un bloc par rangée au plus, et n'entaille pas plus de deux blocs de profondeur dans une élévation courte. Dès que `villagePathTunnelBlock` nomme un bloc, une élévation qui se tient à `villagePathTunnelDepth` ou plus au-dessus de la route sur au moins douze rangées est percée à la place : la route garde le niveau du côté le plus haut sur toute l'élévation, chaque rangée recouverte d'autant de terrain reçoit un percement de quatre blocs de haut avec le bloc de revêtement pour les parois et la voûte, et les rangées moins profondes avant les portails sont entaillées comme approche. Une route qui rencontre une paroi de montagne plutôt qu'une colline qu'elle peut surplomber n'est pas non plus gravie : elle garde le niveau auquel elle arrive et cherche l'autre côté, jusqu'à 98 rangées au-delà de l'endroit où l'élément se serait terminé. Si elle le trouve dans cette portée, avec le terrain intermédiaire libre de tout autre élément, l'élément est allongé pour ressortir au portail opposé, un tunnel traverse donc toujours de part en part. Sinon, la route s'arrête au pied de la montagne et n'y entre jamais. Toute la rue passe, voies, lignes et trottoirs compris, éclairée depuis la voûte par `villagePathTunnelLightBlock` tous les `villagePathTunnelLightRun` blocs, tandis que les lampadaires et la décoration des bas-côtés s'arrêtent aux portails. Un carrefour n'est jamais percé, une rue transversale rejoint donc toujours la route à l'air libre. Aucune parcelle n'est implantée le long d'un tronçon que la route percera et aucune rue ne s'en détache, de sorte qu'une maison ne donne jamais sur un tunnel et qu'aucun carrefour n'y est taillé ; un quartier qui ne trouve pas ailleurs de place pour ses parcelles pose moins de rues à cet endroit.

#### Égouts de village

*villages*

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `villageSewerBlock` | nom de bloc | aucun | Le bloc dont est revêtu un égout sous les rues et ruelles d'un village : son sol, ses deux parois et sa voûte. Vide, ne creuse aucun égout |
| `villageSewerDepth` | nombre | `8` | À quelle profondeur sous la surface propre de la rue se trouve le sol de l'égout. L'égout suit la rue sous laquelle il passe, une rue qui monte porte donc un égout qui monte |
| `villageSewerHeight` | nombre | `3` | Combien de blocs de hauteur libre se trouvent au-dessus de la galerie |
| `villageSewerWidth` | nombre | `5` | La largeur de l'égout, comptée en travers parois comprises. Un nombre pair est arrondi au supérieur pour que le canal garde le milieu |
| `villageSewerWaterBlock` | nom de bloc | `minecraft:water` | Ce qui remplit le canal au milieu. Vide, laisse le canal à sec |
| `villageSewerWalkBlock` | nom de bloc | aucun | Le revêtement des galeries de chaque côté du canal. Vide, on marche sur le bloc de revêtement |
| `villageSewerLightBlock` | nom de bloc | aucun | Le bloc encastré dans la voûte au-dessus du canal comme lumière. Vide, n'éclaire rien |
| `villageSewerLightRun` | nombre | `8` | L'écart en blocs entre ces lumières. Ancré aux coordonnées du monde, les lumières d'un élément de route se poursuivent dans le suivant |
| `villageSewerLadderBlock` | nom de bloc | aucun | Le bloc par lequel on gravit un puits de bouche d'égout, posé le long du puits depuis la rue jusqu'à la galerie de l'égout. Vide, laisse le puits ouvert |
| `villageSewerCoverBlock` | nom de bloc | aucun | Le bloc qui recouvre une bouche d'égout, posé à fleur dans une rue est-ouest partout où une rue ou une ruelle la rencontre, et sur la place là où cette rue croise la boucle d'égout. Une trappe en bois est le choix habituel : une trappe en fer reçoit un signal de redstone et aucun joueur ne peut l'ouvrir à la main, ce qui lui ferme l'égout. Vide, laisse l'embouchure du puits ouverte |
| `villageSewerMossBlock` | nom de bloc | aucun | Un second bloc mêlé çà et là au revêtement, de la pierre moussue parmi de la pierre ordinaire par exemple. Vide, revêt l'égout d'un seul bloc partout |
| `villageSewerMossChance` | de 0 à 100 | `25` | Le pourcentage de blocs de revêtement qui ressortent sous la forme de ce second bloc. Tiré par position de bloc à partir de la graine du monde, un même égout ressort donc toujours identique |
| `villageSewerVineBlock` | nom de bloc | aucun | Un bloc suspendu çà et là à l'intérieur des parois de l'égout, des lianes par exemple. Il s'accroche à la paroi contre laquelle il se trouve. Vide, ne suspend rien |
| `villageSewerVineChance` | de 0 à 100 | `20` | Le pourcentage de cellules à côté d'une paroi qui le portent. Tiré par position de bloc à partir de la graine du monde, un même égout est donc toujours garni de la même façon |
| `villageSewerWellEntrance` | booléen | `true` | Une boucle d'égout sous l'anneau de la place autour du puits, l'égout de chaque rue la traversant, et une bouche d'égout sur la place qui y descend de chaque côté où une rue est-ouest la croise, de sorte que les égouts forment un seul système connecté avec un accès au centre de la ville. Désactivé, l'égout de chaque rue s'arrête au puits et la place n'a aucun accès vers le bas |

**Égouts.** Nommer `villageSewerBlock` creuse un égout sous chaque rue et ruelle, à `villageSewerDepth` blocs sous la surface propre de cette rue. Ce n'est pas un réseau à part : il suit les routes, de sorte que partout où vont les rues l'égout va, il tourne là où elles tournent, il monte là où elles montent, et deux égouts se rejoignent sous un carrefour parce que les rues au-dessus se rejoignent ; là où une rue ou une ruelle se termine contre une autre route, son égout se poursuit sous cette route pour rejoindre celui de l'autre. Un renflement d'impasse et une rangée portée par un pont n'en portent aucun. La coupe est un sol revêtu, un canal au milieu rempli de `villageSewerWaterBlock`, une galerie de chaque côté revêtue de `villageSewerWalkBlock`, `villageSewerHeight` blocs de hauteur libre et une voûte revêtue, sur `villageSewerWidth` de large parois comprises. `villageSewerLightBlock` encastre une lumière dans la voûte au-dessus du canal tous les `villageSewerLightRun` blocs. Un égout ne monte jamais assez pour perturber la rue qui le recouvre, et un tronçon sans place entre la route et le plancher du monde est ignoré plutôt que comprimé.

#### Voies ferrées de village

*villages*

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
    "villageRailTieBlock": "minecraft:planks:1",
    "villageRailTieRun": 2,
    "villageRailTracks": 2,
    "villageRailTrackGap": 2,
    "villageRailShoulderBlock": "minecraft:gravel",
    "villageRailShoulderWidth": 1,
    "villageRailPowerBlock": "minecraft:golden_rail",
    "villageRailPowerBase": "minecraft:redstone_block",
    "villageRailTunnelLightBlock": "minecraft:glowstone",
    "villageRailTunnelLightRun": 8,
    "villageRailPowerRun": 16,
    "villageRailSupportBlock": "minecraft:log",
    "villageRailDeckBlock": "minecraft:planks",
    "villageRailBarrierBlock": "minecraft:oak_fence",
    "villageRailBridgeFrameBlock": "minecraft:stonebrick",
    "villageRailBridgeFrameTopBlock": "minecraft:stone_slab",
    "villageRailBridgeFrameHeight": 4,
    "villageRailBridgeFrameRun": 24,
    "villageRailBridgeFrameLeast": 24,
    "villageRailTunnelBlock": "minecraft:stonebrick",
    "villageRailTunnelDepth": 6,
    "villageRailClimb": 8,
    "villageRailTail": 48,
    "villageSubwayLines": 0,
    "villageSubwayDepth": 24,
    "villageSubwaySpacing": 64,
    "villageSubwayDirection": "any",
    "villageSubwayWidth": 5,
    "villageSubwayBlock": "",
    "villageSubwayTrackSeat": "auto",
    "villageSubwayBedBlock": "minecraft:gravel",
    "villageSubwayTieBlock": "minecraft:planks:1",
    "villageSubwayTieRun": 2,
    "villageSubwayTracks": 2,
    "villageSubwayTrackGap": 2,
    "villageSubwayShoulderBlock": "",
    "villageSubwayShoulderWidth": 1,
    "villageSubwayPowerBlock": "",
    "villageSubwayPowerBase": "minecraft:redstone_block",
    "villageSubwayPowerRun": 16,
    "villageSubwayTunnelBlock": "minecraft:stonebrick",
    "villageSubwayTunnelLightBlock": "minecraft:glowstone",
    "villageSubwayTunnelLightRun": 8,
    "villageSubwayClimb": 8,
    "villageSubwayTail": 48,
    "villageSubwayStationLength": 16,
    "villageSubwayStationRun": 0,
    "villageSubwayPlatformWidth": 3,
    "villageSubwayPlatformBlock": "minecraft:stonebrick:1",
    "villageSubwayStation": "mypack:subway_station",
    "villageSubwayStationFoot": 4,
    "villageSubwayStationRepeat": 12,
    "villageSubwayRailingBlock": "minecraft:iron_bars",
    "villageSubwayBenchBlock": "minecraft:oak_stairs",
    "villageSubwayBenchEndBlock": "minecraft:log",
    "villageSubwayBenchLength": 5,
    "villageSubwaySurfaces": 25
  }
}
```

Une ligne de chemin de fer est un tronçon droit de voie qui traverse tout le village sur un axe et se prolonge au-delà de son dernier élément à chaque extrémité. Elle est posée avant la première rue, la ville se développe donc autour d'elle : aucune maison ne se dresse sur la ligne, une rue ne peut que la croiser en ligne droite, et rien ne s'en détache. Comme les routes, elle a besoin de `terrainAdaptation`. `villageRailLines` vaut `0` par défaut, ce qui n'en pose aucune et laisse un village exactement tel qu'il était.

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `villageRailLines` | nombre | `0` | Combien de lignes traversent chaque village. `0` n'en pose aucune |
| `villageRailSpacing` | nombre | `48` | Le moins de blocs de terrain dégagé entre le ballast d'une ligne et celui de la suivante du même village. `1` les pose à un bloc l'une de l'autre, c'est ainsi qu'un pack construit un faisceau de lignes parallèles |
| `villageRailDirection` | texte | `any` | Dans quel sens courent les lignes : `ew` d'est en ouest, `ns` du nord au sud, `any` tire au sort par village. `e`, `w`, `n` et `s` sont lus de la même façon |
| `villageRailWidth` | nombre | `3` | Le minimum de largeur du ballast. `3` porte une voie au milieu et `5` en porte deux ; un ballast à qui l'on demande plus de voies que cela n'en contient s'élargit pour les accueillir |
| `villageRailTracks` | nombre | `0` | Combien de voies porte un même ballast, côte à côte et espacées de `villageRailTrackGap`. **Le ballast s'élargit pour toutes les contenir**, trois voies partagent donc un même ballast au lieu de devenir trois lignes. `0` pose une voie sur un ballast de moins de cinq de large et deux sur un plus large |
| `villageRailTrackGap` | nombre | `2` | De combien de blocs les voies d'un ballast sont espacées, de centre à centre. `2`, le minimum autorisé, laisse un bloc de ballast entre elles, ce qui les empêche de s'incurver l'une vers l'autre comme le font des rails au contact |
| `villageRailBlock` | bloc | vide | La voie. Vide, pose des rails vanilla, que les wagonnets empruntent ; tout autre bloc est posé tel quel |
| `villageRailTrackSeat` | `auto`, `on` ou `in` | `auto` | Où la voie est assise. `auto` pose un bloc de rail sur le ballast et encastre tout autre bloc à fleur de la surface du ballast ; `on` la pose toujours sur le ballast ; `in` l'encastre toujours dans le ballast. Une voie encastrée dans le ballast est le moyen pour un pack de poser un rail en blocs de fer ou en dalles plutôt qu'en rails de wagonnet, et un passage à niveau traverse alors le pavage à fleur |
| `villageRailBedBlock` | bloc | vide | Le ballast sous la voie. Vide, pose du gravier |
| `villageRailTieBlock` | bloc | vide | La traverse posée en travers du ballast toutes les `villageRailTieRun` rangées. Vide, pose des planches |
| `villageRailTieRun` | nombre | `2` | De combien de rangées les traverses sont espacées |
| `villageRailShoulderBlock` | bloc | vide | Habille les colonnes les plus extérieures du ballast, un chemin d'entretien à côté de la voie et l'équivalent ferroviaire du trottoir d'une route. Vide, n'en pose aucun |
| `villageRailShoulderWidth` | nombre | `1` | Combien de colonnes de large fait cet accotement de chaque côté, ajoutées à l'extérieur de `villageRailWidth` |
| `villageRailPowerBlock` | bloc | vide | La voie propulsée insérée dans la ligne toutes les `villageRailPowerRun` rangées. Vide, utilise un rail propulseur vanilla ; un bloc qui n'est pas un rail est simplement posé à cet endroit |
| `villageRailPowerBase` | bloc | vide | Ce qui se trouve sous une voie propulsée pour l'alimenter. Vide, utilise un bloc de redstone |
| `villageRailPowerRun` | nombre | `0` | Toutes les tant de rangées, un rail propulseur sur un bloc de redstone est inséré dans une voie de rails vanilla, pour qu'un wagonnet continue de rouler. `0` n'en propulse aucun, et toute voie autre que des rails vanilla l'ignore |
| `villageRailSupportBlock` | bloc | vide | Les poteaux sous un viaduc. Vide, utilise des bûches |
| `villageRailDeckBlock` | bloc | vide | Le tablier sur lequel un viaduc porte le ballast. Vide, utilise des planches |
| `villageRailBarrierBlock` | bloc | vide | Barrières le long des deux bords du tablier d'un viaduc. Vide, n'en dresse aucune |
| `villageRailBridgeFrameBlock` | bloc | vide | Un portique au-dessus d'un long viaduc : un poteau de chaque côté du tablier et une poutre au sommet. Chaque rangée qui en porte un porte aussi ses poteaux de soutien jusqu'au ballast. Vide, n'en construit aucun |
| `villageRailBridgeFrameTopBlock` | bloc | vide | La poutre au sommet de ce portique. Vide, utilise `villageRailBridgeFrameBlock` |
| `villageRailBridgeFrameHeight` | nombre | `4` | Combien de blocs de hauteur libre le portique laisse au-dessus du tablier, la poutre se trouvant un bloc au-dessus |
| `villageRailBridgeFrameRun` | nombre | `24` | De combien de rangées les portiques sont espacés lorsqu'un viaduc est assez long pour en porter plusieurs |
| `villageRailBridgeFrameLeast` | nombre | `24` | Le plus court viaduc qui reçoit un portique. Un viaduc plus court reste nu |
| `villageRailTunnelBlock` | bloc | vide | Revêt les parois et la voûte là où la ligne perce une colline. Vide, ne perce aucun tunnel et entaille chaque colline |
| `villageRailTunnelDepth` | nombre | `6` | Quelle épaisseur de terrain doit se trouver au-dessus du ballast avant qu'un tronçon soit percé plutôt qu'entaillé. Nécessite `villageRailTunnelBlock` |
| `villageRailTunnelLightBlock` | bloc | vide | Une lumière encastrée dans la voûte d'un tunnel ferroviaire le long de son axe. Vide, n'éclaire rien |
| `villageRailTunnelLightRun` | nombre | `8` | L'écart en blocs entre ces lumières de tunnel, ancré aux coordonnées du monde pour que les éléments s'accordent |
| `villageRailClimb` | nombre | `8` | Sur combien de rangées la ligne reste à niveau pour chaque bloc qu'elle gravit ou descend. `1` lui donne une pente aussi raide que celle d'une route |
| `villageRailTail` | nombre | `48` | Sur quelle distance la ligne se prolonge au-delà du dernier élément du village à chaque extrémité |

**Où passe une ligne.** Les lignes sont parallèles, sur l'axe que nomme `villageRailDirection`, et sont espacées à partir de la place du puits tour à tour, d'abord d'un côté puis de l'autre, chacune gardant au moins `villageRailSpacing` blocs de terrain entre son ballast et celui de la ligne suivante. Une ligne ne traverse jamais la place ni une parcelle : elle est posée avant la première rue, de sorte que chaque rue et chaque maison du village sont disposées autour d'elle, et elle est ajustée au village développé plus `villageRailTail` à chaque extrémité une fois le village disposé.

**Pente.** Un chemin de fer ne grimpe pas comme une route. Son ballast suit le sol lissé sur une longue distance et ne change de niveau que d'un bloc au plus toutes les `villageRailClimb` rangées. Là où le sol s'affaisse de plus de trois blocs, la ligne passe sur un viaduc, `villageRailDeckBlock` sur des poteaux `villageRailSupportBlock` toutes les quatre rangées, au-dessus de l'eau comme au-dessus d'un ravin. Là où le sol s'élève, la ligne est entaillée, ou percée avec `villageRailTunnelBlock` dès que le terrain au-dessus du ballast atteint `villageRailTunnelDepth` d'épaisseur sur douze rangées ou plus. Quatre blocs sont maintenus dégagés au-dessus du ballast sur toute la ligne. Un viaduc se tient à une seule hauteur d'un bout à l'autre, et le ballast de chaque côté monte en rampe pour rejoindre cette hauteur ; lorsque maintenir un viaduc à niveau et le rythme de montée se contredisent, le niveau l'emporte et la rampe voisine peut monter plus tôt que ne le dit `villageRailClimb`. Un viaduc de `villageRailBridgeFrameLeast` rangées ou plus porte des portiques dès que `villageRailBridgeFrameBlock` nomme un bloc, espacés de `villageRailBridgeFrameRun` rangées et répartis symétriquement autour du milieu du viaduc, et chaque rangée qui en porte un emmène avec lui ses poteaux de soutien jusqu'au ballast. Une rangée où une route croise la ligne reste ouverte.

**Croisements.** Une rue croise une ligne en ligne droite et se prolonge au-delà des deux bords du ballast d'au moins sept blocs. Une rue qui se terminerait sur la ligne ou dans ces sept blocs est prolongée de l'autre côté lorsque sa pente le permet, et sinon arrêtée sept blocs avant le ballast ; une rue qui commencerait sur la ligne ou la longerait est refusée. À un croisement, c'est la rue qui est adaptée à la ligne, jamais l'inverse, et elle monte en rampe jusqu'à ce niveau, à sa propre pente praticable, de chaque côté. Le pavage garde la surface et la voie la traverse un bloc plus haut, de sorte qu'un wagonnet traverse la route et qu'un villageois traverse la voie. Une ligne percée sous une colline n'est pas du tout croisée : la rue passe au-dessus du tunnel.

**Seuils.** Lorsque `terrainAdaptation` est activé, aucun bâtiment de village ne pose de bloc d'escalier en dehors de sa propre emprise : les marches de seuil que vanilla place devant une porte sont omises, car la façade de la route et le tablier de la parcelle amènent eux-mêmes le sol jusqu'à la porte.

**Voie.** Avec `villageRailBlock` vide, la voie est faite de rails vanilla orientés le long de la ligne, et `villageRailPowerRun` pose un rail propulseur sur un bloc de redstone toutes les tant de rangées pour qu'un wagonnet parcoure toute la ligne. Un pack qui veut des blocs de fer, des barreaux ou autre chose les nomme à la place, et la ligne est habillée avec ce bloc tel quel.

#### Métros de village

*villages*

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `villageSubwayLines` | nombre | `0` | Combien de lignes de chemin de fer souterraines un village creuse. 0 n'en creuse aucune et ne tire rien au sort, le village est donc disposé exactement comme il le serait sans elles |
| `villageSubwayDepth` | nombre | `24` | À quelle profondeur sous la surface se trouve le ballast. La ligne est nivelée d'après le sol au-dessus d'elle, elle suit donc le relief à cette profondeur au lieu de rester à niveau |
| `villageSubwaySpacing` | nombre | `64` | La distance à laquelle les lignes de métro d'un village sont tenues les unes des autres |
| `villageSubwayDirection` | chaîne | `any` | Dans quel sens courent les lignes de métro : `ew` d'est en ouest, `ns` du nord au sud, ou `any` pour tirer au sort par village |
| `villageSubwayWidth` | nombre | `3` | La largeur du ballast, avant les accotements |
| `villageSubwayTracks` | nombre | `0` | Combien de voies parallèles porte le ballast. 0 en prend autant que la largeur le permet |
| `villageSubwayTrackGap` | nombre | `2` | L'écart entre voies parallèles |
| `villageSubwayBlock` | bloc | vide | Le bloc de voie. Vide, pose des rails vanilla |
| `villageSubwayTrackSeat` | chaîne | `auto` | Si la voie repose sur le ballast, dans le ballast, ou `auto` pour laisser le bloc décider |
| `villageSubwayBedBlock` | bloc | vide | Le bloc dont le ballast est fait. Vide, utilise du gravier |
| `villageSubwayTieBlock` | bloc | vide | Le bloc posé en travers du ballast comme traverses. Vide, utilise des planches |
| `villageSubwayTieRun` | nombre | `2` | De combien de blocs les traverses sont espacées |
| `villageSubwayShoulderBlock` | bloc | vide | Le bloc de chaque côté du ballast. Vide, ne laisse aucun accotement |
| `villageSubwayShoulderWidth` | nombre | `1` | La largeur de cet accotement |
| `villageSubwayPowerBlock` | bloc | vide | Le bloc de voie propulsée. Vide, utilise un rail propulseur vanilla |
| `villageSubwayPowerBase` | bloc | vide | Le bloc placé sous une voie propulsée pour l'alimenter. Vide, utilise un bloc de redstone |
| `villageSubwayPowerRun` | nombre | `0` | De combien de blocs les voies propulsées sont espacées. 0 n'en pose aucune |
| `villageSubwayTunnelBlock` | bloc | vide | Le bloc dont le percement est revêtu : les parois de chaque côté et la voûte au-dessus. Vide, creuse le percement et ses stations sans revêtement |
| `villageSubwayTunnelLightBlock` | bloc | vide | Le bloc encastré dans la voûte du tunnel comme lumière. Vide, n'éclaire rien |
| `villageSubwayTunnelLightRun` | nombre | `8` | L'écart en blocs entre ces lumières, ancré aux coordonnées du monde pour que les éléments s'accordent |
| `villageSubwayClimb` | nombre | `8` | Sur combien de blocs une ligne court avant de pouvoir monter ou descendre d'un bloc |
| `villageSubwayTail` | nombre | `48` | Sur quelle distance au-delà des éléments propres au village une ligne court avant de s'arrêter |
| `villageSubwaySurfaces` | nombre | `25` | La probabilité sur cent qu'une ligne de métro remonte à la surface à une extrémité et s'y poursuive comme un chemin de fer ordinaire, tunnel derrière elle et voie à ciel ouvert devant. `0` garde chaque métro enterré sur toute sa longueur |

**Remonter à la surface.** `villageSubwaySurfaces` est la probabilité sur cent qu'une ligne, au lieu de rester enterrée d'un bout à l'autre, remonte à la surface à une extrémité et s'y poursuive comme un chemin de fer ordinaire — tunnel derrière elle, voie à ciel ouvert devant. La montée obéit à `villageSubwayClimb`, un bloc par ce nombre de rangées, de sorte qu'une ligne à `villageSubwayDepth` de profondeur consacre profondeur fois montée rangées à la seule rampe et a besoin d'un bon tronçon au-delà pour mériter son nom ; une ligne qui n'a pas la place pour les deux reste simplement sous terre. Sur une ligne courte qui porte une station, augmenter `villageSubwayClimb` est ce qui fait de la place pour les deux.

#### Stations de métro

*villages*

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `villageSubwayStationLength` | nombre | `0` | Combien de blocs de long fait une salle de station, centrée sur la rangée où la ligne passe au plus près du puits. 0 ne construit aucune station |
| `villageSubwayStationRun` | nombre | `0` | De combien de blocs les autres stations sont espacées le long de la ligne, au-delà de celle du puits. Chacune glisse un peu le long de la ligne pour trouver un sol qui la supporte et est omise là où aucun ne convient. 0 ne construit que celle-là |
| `villageSubwayStationRepeat` | nombre | `12` | Combien de couches d'une construction de station se répètent, de sorte qu'une seule construction serve à toute profondeur : le puits s'allonge par copies entières de cette bande et le couloir absorbe ce qui reste. Cela doit correspondre à un tour entier de l'escalier, sinon les volées ne se raccorderont pas. `0` n'allonge jamais la construction |
| `villageSubwayStationFoot` | nombre | `4` | Combien de couches au pied d'une construction de station sont posées une seule fois, avant la partie qui se répète. Le sol et la porte vers le quai s'y trouvent |
| `villageSubwayPlatformWidth` | nombre | `3` | De combien la salle est élargie de chaque côté du ballast pour former un quai |
| `villageSubwayPlatformBlock` | bloc | vide | Le bloc dont le quai est revêtu au sol. Vide, le revêt du revêtement du tunnel |
| `villageSubwayStation` | texte | vide | Le fichier de structure dont chaque station est construite, nommé `mypack:subway_station` et lu dans le dossier `structures` de ce pack. Ses blocs sont posés tels que construits, l'éponge tenant lieu de revêtement du tunnel, et ses cellules d'air sont creusées, de sorte que ce qui se dresse sous terre est la construction et non une description de celle-ci. Une ligne n'a de stations que lorsque ce réglage nomme une construction qui se charge : vide, ou avec un nom qui ne peut être chargé, aucune station n'est construite |
| `villageSubwayRailingBlock` | bloc | `minecraft:iron_bars` | Le bloc qui borde la tête de l'escalier d'une station là où il débouche sur la rue, pour que personne ne tombe dans le puits. Vide, laisse la tête sans garde-corps |
| `villageSubwayBenchBlock` | bloc | `minecraft:oak_stairs` | L'assise des bancs posés sur le quai d'une station et à côté de la tête de son escalier. Un bloc d'escalier est tourné dos à la ligne et se lit comme un banc ; n'importe quel bloc convient. Vide, omet les bancs |
| `villageSubwayBenchEndBlock` | bloc | `minecraft:log` | Les accoudoirs à chaque extrémité d'un banc de station. Vide, laisse l'assise nue aux deux bouts |
| `villageSubwayBenchLength` | nombre | `5` | La longueur d'un banc de station, accoudoirs compris. `0` omet les bancs |

**Stations.** Une ligne de métro reçoit une station là où elle passe au plus près du puits dès que `villageSubwayStationLength` est défini et que `villageSubwayStation` nomme une construction qui se charge, puis d'autres tous les `villageSubwayStationRun` blocs le long de la ligne. Chacune glisse de quelques blocs de part et d'autre pour trouver un emplacement que le sol supporte, reste à l'écart des stations déjà retenues, et est simplement omise là où rien de viable n'est proche, de sorte qu'une ligne ne porte jamais une salle sans accès. La salle est le ballast élargi de `villageSubwayPlatformWidth` de chaque côté, au sol revêtu de `villageSubwayPlatformBlock`, aux parois et à la voûte revêtues du revêtement du tunnel, et éclairée par les `villageSubwayTunnelLightBlock` et `villageSubwayTunnelLightRun` propres au tunnel. Depuis le quai, un couloir mène à une cage d'escalier qui monte jusqu'à la rue à côté de la route, jamais dessous, et jamais à travers la place du puits ni une maison ; là où la montée est trop longue pour aller tout droit, le couloir fait d'abord demi-tour le long de la salle. Un garde-corps de `villageSubwayRailingBlock` ceint la tête d'escalier au niveau de la rue, l'extrémité proche restant ouverte comme entrée, et un banc de `villageSubwayBenchBlock` avec des accoudoirs de `villageSubwayBenchEndBlock`, long de `villageSubwayBenchLength`, se dresse sur le quai et de nouveau à côté de la tête d'escalier.

**Construire la station à la main.** `villageSubwayStation` nomme le fichier de structure dont chaque station est construite, et sans lui aucune station n'est construite. C'est ainsi qu'un pack fournit une forme que quelqu'un a construite plutôt qu'une forme décrite par des réglages. Construisez-la dans un monde, marquez la structure dans un bloc quelconque, exportez-la, et placez-la avec le pack : ses blocs sont posés tels que construits, l'éponge tenant lieu de revêtement du tunnel, et ses cellules d'air sont creusées. Une seule construction sert à toute profondeur parce que son milieu se répète — `villageSubwayStationFoot` couches sont posées une fois en bas, portant le sol et la porte vers le quai, puis des copies entières des `villageSubwayStationRepeat` couches suivantes s'empilent jusqu'à ce que la construction atteigne la rue. Cette bande doit correspondre à un tour entier de l'escalier, sinon les volées ne se rejoindront pas là où deux copies se raccordent. La construction porte sa propre ouverture sur la rue.

#### Liaisons ferroviaires

*villages*

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
    "villageRailLinkPlatformBlock": "minecraft:stonebrick"
  }
}
```

Les liaisons ferroviaires relient les villages voisins en un seul réseau. Les villages sont fondés un par cellule de la grille des villages (`structureSpacing`), et une liaison court le long de la jointure entre deux cellules : la première ligne de chaque village se prolonge au-delà de sa queue sous forme d'embranchement, tout droit jusqu'à la jointure, et rejoint une artère posée le long de la jointure à angle droit. L'artère va d'un embranchement à l'autre et ne les dépasse jamais. Elle a besoin de `villageRailLines`, ou de `villageSubwayLines` sur un pack sans lignes de surface, et elle est désactivée par défaut.

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `villageRailLinks` | true/false | `false` | Relie les villages voisins dont les premières lignes se font face de part et d'autre d'une jointure |
| `villageRailLinkLeast` | nombre | `128` | La plus courte liaison posée, embranchement plus artère plus embranchement, en blocs |
| `villageRailLinkMost` | nombre | `1024` | La plus longue liaison posée, embranchement plus artère plus embranchement, en blocs |
| `villageRailLinkBridgeMost` | nombre | `96` | Le plus long pont dont une liaison peut avoir besoin. Une liaison au-dessus d'une eau plus large ou d'un vide plus profond n'est pas posée |
| `villageRailLinkTunnelMost` | nombre | `192` | Le plus long tunnel dont une liaison peut avoir besoin là où `villageRailTunnelBlock` perce des tunnels. Une liaison qui devrait percer plus loin n'est pas posée |
| `villageRailLinkStation` | texte | `both` | La station sur chaque embranchement juste avant l'artère : `both` pose un quai de chaque côté de la ligne, `one` un seul quai à gauche d'un train qui arrive à l'artère, `none` n'en construit aucune |
| `villageRailLinkStationLength` | nombre | `16` | Combien de rangées de long font les quais de station. `0` ne construit aucune station |
| `villageRailLinkPlatformWidth` | nombre | `3` | Combien de blocs de large fait chaque quai |
| `villageRailLinkPlatformBlock` | bloc | vide | Le bloc dont les quais sont construits. Vide, utilise des briques de pierre |

**Quels villages sont reliés.** Deux villages ne sont reliés que lorsqu'ils se trouvent dans des cellules voisines, que leurs premières lignes courent sur l'axe qui traverse la jointure entre eux, et que la liaison entière, mesurée de puits à puits le long de la voie, est comprise entre `villageRailLinkLeast` et `villageRailLinkMost` blocs. Chaque élément de la décision est calculé à partir de la graine et des deux sites de village, le résultat est donc le même quel que soit le village ou le chunk généré en premier. Une liaison qui ne peut pas être construite entièrement n'est pas posée du tout, jamais à moitié : celle qui aurait besoin d'un pont ou d'un tunnel plus long que les réglages ne le permettent, qui dépasserait la bordure du monde, qui heurterait un manoir de la forêt, qui placerait deux villages plus près que ne le permet `structureSeparation`, ou qui amènerait une jonction trop près d'un coin des cellules. Une artère n'est posée que vers un village qui a réellement été fondé : lorsqu'un plafond comme `structureMost` arrête le voisin, ou qu'il devient trop petit pour être conservé, ni l'une ni l'autre moitié de l'artère ni l'embranchement au-delà de la queue propre au village ne sont construits. Les villages épinglés sont reliés de la même façon, un par cellule ; une cellule qui contient deux épingles n'en relie aucun. Les autres villages restent à l'écart de l'embranchement et de l'artère d'une liaison en grandissant, comme ils restent à l'écart les uns des autres.

**Pente.** Les embranchements et les artères sont des lignes de chemin de fer, nivelées, franchies par des ponts, percées de tunnels et croisées exactement comme une ligne de village, avec `villageRailClimb` et les réglages de viaduc et de tunnel ci-dessus. Là où un embranchement rejoint l'artère, les deux sont à niveau, tout comme la station à côté.

**La jonction.** Un embranchement ne rejoint que la voie proche de l'artère. Cette voie est interrompue là où le milieu de l'embranchement la rencontre, la voie gauche de l'embranchement s'incurve vers la gauche pour la rejoindre et sa voie droite vers la droite, et la voie éloignée passe tout droit. Avec deux voies, l'artère en haut et l'embranchement qui arrive par le bas :

```
xxxxxxx
ooooooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

`x` est le ballast et `o` la voie. Là où deux embranchements arriveraient à quelques blocs l'un de l'autre, la première ligne du second village se déplace pour s'aligner sur la première, et les deux se rejoignent en un carrefour en croix : chaque embranchement ne se raccorde qu'à sa propre voie proche exactement comme ci-dessus, les deux voies de l'artère sont interrompues au centre de l'embranchement, et aucun rail n'en croise un autre :

```
 xoxox
xxoxoxx
oooxooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

Une artère à voie unique n'a pas de seconde voie à offrir à l'autre embranchement, une liaison dont les embranchements se rencontreraient de front sur une voie unique n'est donc pas posée. Avec une voie unique, la voie de l'embranchement s'incurve vers la voie de l'artère du côté gauche, et la voie de l'artère au-delà de cette courbe s'arrête contre elle. Les courbes sont posées avec leurs formes figées, de sorte que les rails vanilla ne tournent qu'à l'endroit où la jonction est dessinée et nulle part ailleurs.

**Stations.** Les dernières rangées d'un embranchement avant la jonction forment une station : des quais de `villageRailLinkPlatformBlock` à niveau avec le rail, bordés le long du bord extérieur par `villageSubwayRailingBlock`, avec un banc de `villageSubwayBenchBlock` à mi-longueur de chaque quai.

**Métros.** Sur un pack qui n'a que des lignes de métro, la liaison porte la première ligne de métro d'un village. La ligne sort de terre en direction de l'artère, la rampe faisant `villageSubwayDepth` fois `villageSubwayClimb` rangées de long, et atteint la station et la jonction en surface ; un village de ce type n'est relié que d'un côté, celui de la liaison la plus courte, et l'artère est un chemin de fer de surface construit à partir des réglages `villageRail`. Là où un embranchement n'a pas la place pour cette rampe et sa station, l'artère descend au contraire vers le métro : toute la liaison, embranchements et artère, reste souterraine à `villageSubwayDepth`, est construite à partir des réglages `villageSubway`, et se rejoint dans la même jonction sans station.

#### Décoration de village

*villages*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageDecor": ["mypack:street_flowers=2", "mypack:street_tree=1", "empty=3"]
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `villageDecor` | liste de `nom=poids` | aucun | Disperse la worldgen propre à ce pack le long des bas-côtés des routes de village. Le nom est une clé de registre de worldgen, le poids est la part des emplacements de cette entrée, et `empty=poids` est la part des emplacements laissés nus |

`villageDecor` disperse la worldgen d'un pack le long des bas-côtés des routes de village, ce qui évite qu'un village ressemble à des maisons plantées dans de l'herbe nue. Chaque entrée est `nom=poids` : le nom est une clé de registre de worldgen, `mypack:street_flowers`, et le poids est la part des emplacements de cette entrée. Le nom `empty` est la part des emplacements laissés nus, et c'est celle qu'il faut bien régler, car une liste qui en est dépourvue remplit chaque emplacement de chaque bas-côté et le village devient une pépinière plutôt qu'une rue.

Un bloc sur trois de chaque côté d'une route est un emplacement, compté à partir des coordonnées du monde pour que l'espacement se poursuive d'un élément de route au suivant. Un emplacement est ignoré lorsqu'il tombe à l'intérieur d'un élément du village, sur la route elle-même, devant une porte, ou là où le sol n'est pas de l'air libre reposant sur quelque chose de solide. Ce qui pousse à un emplacement est calculé à partir de la graine du monde et de l'emplacement lui-même, de sorte qu'un même monde disperse toujours de la même façon.

Le nom pointe vers une entrée de worldgen ordinaire de `<namespace>/worldgen/*.json`, de sorte qu'une `decoration`, un `tree` ou un `imprint` conviennent tous, et chacun garde ses propres blocs, tailles et dispersion. Seule la forme de cette entrée est utilisée ici : ses biomes, dimensions, hauteurs et sa rareté sont la manière dont elle se sème dans le monde en général, et le village ne les consulte pas, une entrée destinée au bas-côté est donc mieux écrite pour rien d'autre. Un bas-côté est de l'air libre reposant sur le sol, une telle entrée veut donc que `replace` soit défini à `minecraft:air` ; une entrée qui ne nomme jamais `replace` reçoit la valeur par défaut habituelle de `minecraft:stone` et ne dresse discrètement rien ici.

Tant que `terrainAdaptation` est activé, ce que fait pousser un emplacement est protégé contre le nettoyage propre au village, de sorte qu'un arbre dressé sur un bas-côté n'est pas abattu de nouveau lorsque le chunk suivant est habillé. Désactivé, il n'y a aucun nettoyage contre lequel le protéger, et la dispersion est identique.

#### Réglages de village par biome

*villages*

**Un biome peut construire différemment.** Un objet `biomes` à l'intérieur de `settings` contient des réglages de village propres à un biome nommé, de sorte qu'un village du désert pose des rues de grès là où un village de plaines pose du béton, sans que ce soient deux packs distincts. Nommez un biome par son id, `minecraft:desert`, ou par un type de biome Forge, `SANDY`, `SNOWY`, `MESA`, `JUNGLE` et les autres ; un id exact est examiné avant les types, une règle générale peut donc être surchargée pour un seul biome. Tout ce qui n'est pas nommé dans une section retombe sur le réglage simple situé au-dessus.

```json
{
  "settings": {
    "villagePathBlock": "minecraft:concrete:15",
    "villageSubwayTunnelBlock": "minecraft:stonebrick",
    "biomes": {
      "SANDY": {
        "villagePathBlock": "minecraft:sandstone:2",
        "villageSubwayTunnelBlock": "minecraft:sandstone"
      },
      "minecraft:icy_plains": {
        "villageSubwayTunnelBlock": "minecraft:packed_ice"
      }
    }
  }
}
```

Chaque réglage de bloc que prend une route, un pont, un chemin de fer, un métro, une station ou un égout y répond, et la syntaxe de mélange pondéré fonctionne dans une section comme en dehors. Le biome est lu au moment où un élément est construit, et les blocs sont repris partout où le sol change de biome, de sorte qu'une route ou un chemin de fer qui sort d'un désert change de matériau à la frontière même. Une ligne de journal au chargement du monde indique combien de sections un pack a fournies et les nomme, et avec le débogage activé chaque biome indique quelle section il a retenue, ou qu'il n'en a retenu aucune et à quoi il aurait répondu.

### Blast Plaster

*ce que fait chaque groupe*

Ce qui se passe après une explosion, d'après `<namespace>/blastplaster/*.json`. `default` laisse les packs décider, `global` ignore les fichiers de pack et laisse les valeurs par défaut de ce mod par-dessus la configuration de Blast Plaster, et `off` rend entièrement la main à la configuration propre de Blast Plaster.

### Structures

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "structureSpacing": ["temples=24", "monuments=40", "mineshafts=200"],
    "structureSeparation": ["monuments=12"],
    "structureMinDistanceFromSpawn": ["strongholds=1000"],
    "structureBiomes": ["temples=minecraft:desert,SANDY"],
    "structureBiomesAreBlacklist": false,
    "structureSpawns": ["temples=minecraft:witch:1:1:1", "monuments="],
    "structureSpawners": ["dungeons=minecraft:zombie,minecraft:husk"],
    "structureAt": ["villages=1000,-500"],
    "structureMost": ["villages=100"]
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `structureSpacing` | liste de `structure=chunks` | vanilla | L'écart entre deux structures lors de leur génération. Concerne les temples, monuments, manoirs, cités de l'End et forteresses ; pour `mineshafts`, le nombre signifie un chunk sur autant, car c'est ainsi que vanilla les place |
| `structureSeparation` | liste de `structure=chunks` | vanilla | La distance minimale entre deux structures du même type. Concerne les monuments, manoirs, cités de l'End, forteresses et villages, pour lesquels c'est le nombre minimal de chunks entre un village et le suivant, quoi que la grille autorise |
| `structureMinDistanceFromSpawn` | liste de `structure=blocs` | vanilla | À quelle distance du point d'apparition du monde une structure commence à se générer |
| `structureBiomes` | liste de `structure=biome,biome` | vanilla | Les biomes dans lesquels une structure se génère, par nom de registre ou par type du dictionnaire de biomes. Concerne toutes les structures sauf les cités de l'End, puisque l'End est un seul biome dans cette version |
| `structureBiomesAreBlacklist` | liste de `structure=true` ou `structure=false` | `false` | Le sens de la liste de biomes de chaque structure |
| `structureSpawns` | liste de `structure=entité:poids:min:max` | vanilla | Les mobs qu'une structure fait apparaître quel que soit le biome qui l'entoure. Seuls les temples, monuments et forteresses du Nether gardent une telle liste ; une ligne vide après le signe égal empêche cette structure de faire apparaître quoi que ce soit |
| `structureSpawners` | liste de `structure=entité` | vanilla | Ce que fait apparaître le générateur de monstres à l'intérieur d'une structure vanilla, séparé par des virgules pour un tirage aléatoire par générateur. Les quatre qui en placent un sont les donjons, mines abandonnées, forteresses du Nether et forteresses |
| `structureAt` | liste de `structure=x,z` | aucun | Fixe une structure à un endroit précis. Voir [Structures à des endroits précis](#structures-à-des-endroits-précis) |
| `structureMost` | liste de `structure=nombre` | aucun | Le maximum d'une structure que peut contenir une dimension. Seuls les villages le lisent, et un village fixé avec `structureAt` est fondé quoi qu'il arrive |

Des structures vanilla désactivées par leur nom, dimension par dimension. Le placement se règle avec quatre listes écrites sous la forme `structure=valeur`, une par ligne : `structureSpacing` pour l'écart entre deux structures, `structureSeparation` pour la distance minimale entre deux d'entre elles, `structureMinDistanceFromSpawn` pour la distance à partir de laquelle elles apparaissent, et `structureBiomes` avec `structureBiomesAreBlacklist` pour les endroits où elles sont autorisées.

```
temples=24
monuments=40
mineshafts=200
```

```
temples=minecraft:desert,SANDY
monuments=minecraft:deep_ocean
```

Toutes les structures ne comprennent pas tous les réglages. L'espacement concerne les temples, monuments, manoirs, cités de l'End et forteresses ; pour `mineshafts`, le nombre signifie un chunk sur autant plutôt qu'une grille, car c'est ainsi que vanilla les place. La séparation concerne les monuments, manoirs, cités de l'End, forteresses et villages, pour lesquels c'est le nombre minimal de chunks entre un village et le suivant, quoi que la grille autorise. `structureMost` plafonne le nombre d'une structure que peut contenir une dimension, `villages=100` : une fois ce nombre fondé, aucun autre ne l'est, où que la grille le placerait, tandis qu'un village fixé avec `structureAt` est fondé quoi qu'il arrive. Seuls les villages le lisent. Les biomes concernent toutes les structures sauf les cités de l'End, car l'End est un seul biome dans cette version et il n'y a rien à choisir. Les cités de l'End choisissent toujours leur propre emplacement dans la grille : elles ne se posent que sur une île extérieure dont la surface atteint y60, si bien qu'augmenter leur espacement les raréfie mais ne peut pas en placer une au-dessus du vide. Les forteresses du Nether suivent une grille fixe que vanilla n'expose pas, de sorte que seules les listes de biomes et de distance au point d'apparition les concernent. Les villages gardent leurs propres `villageSpacing`, `villageBiomes` et le reste.

`structureSpawns` remplace les mobs qu'une structure fait apparaître quel que soit le biome qui l'entoure, écrits sous la forme `structure=namespace:entité:poids:min:max`, séparés par des virgules :

```
netherbridges=minecraft:blaze:10:2:3,minecraft:wither_skeleton:8:5:5
temples=minecraft:witch:1:1:1
monuments=
```

Seuls les temples, monuments et forteresses du Nether gardent une telle liste dans cette version ; les villages placent leurs villageois à partir des pièces elles-mêmes, et les mines abandonnées, forteresses et cités de l'End utilisent à la place des générateurs et des mobs placés. Laisser la ligne vide après le signe égal, comme pour monuments ci-dessus, empêche cette structure de faire apparaître quoi que ce soit.

`structureSpawners` indique ce que fait apparaître le générateur de monstres à l'intérieur d'une structure vanilla, écrit sous la forme `structure=namespace:entité`, séparé par des virgules pour un tirage aléatoire par générateur :

```
dungeons=minecraft:zombie,minecraft:husk
mineshafts=minecraft:cave_spider
netherbridges=minecraft:wither_skeleton
strongholds=minecraft:silverfish
```

Quatre structures vanilla placent un générateur : la salle du donjon, le couloir de la mine abandonnée, le trône de la forteresse du Nether et la salle du portail de la forteresse. Chacune est atteinte séparément, si bien que les générateurs placés par d'autres mods ne sont jamais touchés. Les donjons tirent normalement dans la liste à laquelle les mods ajoutent via Forge ; les nommer ici reprend donc aussi ce choix en main.

L'espacement décide de l'endroit où une structure est générée, donc le modifier dans un monde existant laisse ce qui s'y trouve et place les nouvelles sur une grille différente.

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
    "skyAnimals": false,
    "threatItems": ["minecraft:diamond_sword=5,1", "minecraft:diamond=1,16,batch"],
    "threatLevels": [10, 25, 50],
    "threatMost": -1,
    "threatSpawnRate": 2.0,
    "threatNotice": 16.0,
    "threatSays": ["1=Something out there has taken notice of you.", "0=The world loses interest in you."]
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `surfaceDayMonsterRate` | flottant | `1.0` | Multiplicateur de l'apparition des monstres hostiles en surface de jour, `1.0` étant vanilla, ce qui permet de couper l'apparition en surface en plein jour sans toucher aux grottes |
| `surfaceNightMonsterRate` | flottant | `1.0` | La même chose pour la surface de nuit |
| `undergroundDayMonsterRate` | flottant | `1.0` | La même chose sous terre de jour |
| `undergroundNightMonsterRate` | flottant | `1.0` | La même chose sous terre de nuit |
| `monsterCap` | entier | `-1` | Combien de monstres hostiles peuvent être chargés en même temps. Vanilla : 70, et `-1` n'y touche pas |
| `creatureCap` | entier | `-1` | La même chose pour les animaux passifs. Vanilla : 10 |
| `ambientCap` | entier | `-1` | La même chose pour les chauves-souris et assimilés. Vanilla : 15 |
| `waterCreatureCap` | entier | `-1` | La même chose pour les poulpes. Vanilla : 5 |
| `monsterSpawnLight` | entier | `-1` | Le niveau maximal de lumière de bloc qu'une apparition hostile tolère, en plus des vérifications vanilla. `0` est la règle moderne, où une torche protège entièrement une grotte ; `-1` garde le hasard de vanilla |
| `skyAnimals` | booléen | `true` | Si les mobs passifs s'installent sur les terres qu'un monde rubic génère au-dessus de sa fenêtre de terrain, les îles flottantes avant tout. Désactivé, les animaux et les chauves-souris restent au sol en dessous. Les générateurs de monstres ignorent les deux |
| `threatItems` | liste de `objet=niveau,nombre` | aucun | Les objets qui augmentent le score de menace d'un porteur, avec un `,each` ou `,batch` facultatif : `each`, par défaut, ajoute le niveau pour chaque exemplaire porté jusqu'à `nombre` ; `batch` l'ajoute une fois par lot entier de `nombre` portés |
| `threatLevels` | liste d'entiers | aucun | Les scores qui font entrer dans chaque palier, croissants, si bien que `10, 25, 50` donne trois paliers. Vide, la menace est désactivée |
| `threatMost` | entier | `-1` | Plafonne le score. `-1` ne plafonne rien |
| `threatSpawnRate` | flottant | `1.0` | Met à l'échelle l'apparition des monstres hostiles dans un rayon de 128 blocs autour d'un porteur du palier le plus haut, en plus des autres taux, les paliers inférieurs en recevant une part proportionnelle |
| `threatNotice` | flottant, blocs | `0.0` | De combien de blocs de plus les monstres hostiles, vanilla compris, voient un porteur du palier le plus haut, là aussi réparti sur les paliers inférieurs |
| `threatSays` | liste de `palier=message` | aucun | Les lignes affichées en jaune quand le palier d'un joueur change, le palier `0` étant la ligne pour le retour sous le premier palier |

Taux et plafonds d'apparition des mobs, par biome. L'apparition des monstres hostiles est mise à l'échelle par `surfaceDayMonsterRate`, `surfaceNightMonsterRate`, `undergroundDayMonsterRate` et `undergroundNightMonsterRate`, chacun un multiplicateur où `1.0` est vanilla, ce qui permet de couper l'apparition en surface en plein jour sans toucher aux grottes. Les plafonds sont `monsterCap`, `creatureCap` pour les animaux passifs, `ambientCap` pour les chauves-souris et assimilés, et `waterCreatureCap` pour les poulpes ; ceux de vanilla sont 70, 10, 15 et 5, et `-1` n'en touche aucun. `monsterSpawnLight` plafonne la lumière de bloc qu'une apparition hostile tolère, en plus des vérifications vanilla : `0` est la règle moderne, où une torche protège entièrement une grotte, et `-1`, la valeur par défaut, garde le hasard de vanilla. `skyAnimals` décide si les mobs passifs s'installent sur les terres qu'un monde rubic génère au-dessus de sa fenêtre de terrain, les îles flottantes avant tout : `true`, la valeur par défaut, laisse les troupeaux de vanilla là où se trouve le bloc le plus haut, et `false` garde les animaux et les chauves-souris au sol en dessous. Les générateurs de monstres ignorent les deux.

Le niveau de menace note ce que porte chaque joueur et laisse le monde répondre. `threatItems` liste les objets qui comptent, sous forme d'entrées `objet=niveau,nombre` avec un `,each` ou `,batch` facultatif à la fin : `each`, par défaut, ajoute le niveau pour chaque exemplaire porté, sans en compter plus de `nombre`, et `batch` ajoute le niveau une fois pour chaque `nombre` portés, lots entiers seulement. Un nombre supérieur à la taille de pile de l'objet est ramené à cette taille, si bien qu'une pile pleine est le maximum qu'une entrée peut compter, et l'objet peut porter une métadonnée comme `minecraft:dye:4`. Toute entité chargée qui détient des objets est un porteur, pas seulement les joueurs : l'inventaire principal, l'armure et la seconde main d'un joueur, une pile jetée au sol, tout ce qui possède un inventaire d'objets comme une mule à coffre ou une wagonnet à coffre, ainsi que les objets tenus et l'armure de n'importe quel autre mob, de sorte qu'une zone reste dangereuse autour de ce qui y gît, y roule ou y marche. Les joueurs en créatif et en spectateur ne marquent rien. `threatLevels` sont les scores qui font entrer dans chaque palier, croissants, si bien que `[10, 25, 50]` donne trois paliers, et `threatMost` plafonne le score, `-1` ne plafonnant rien. Le score est relevé toutes les cinq secondes. `threatSpawnRate` met à l'échelle l'apparition des monstres hostiles dans un rayon de 128 blocs autour d'un porteur du palier le plus haut, en plus des autres taux, et les paliers inférieurs prennent une part proportionnelle du changement : `2.0` la double au sommet et ajoute un tiers au palier un sur trois. `threatNotice` est le nombre de blocs de plus à partir duquel les monstres hostiles, vanilla compris, voient un porteur du palier le plus haut, là aussi réparti proportionnellement sur les paliers inférieurs. `threatSays` sont les lignes affichées en jaune quand le palier d'un joueur change, sous forme d'entrées `palier=message`, le palier `0` étant la ligne pour le retour sous le premier palier. Une variante d'entité peut définir `threatLeast` pour n'apparaître naturellement que tant qu'un porteur situé à moins de 128 blocs se trouve dans ce palier ou au-dessus. Laisser l'une ou l'autre liste vide désactive la menace.

### Implantation des structures

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "structureAdaptation": ["villages=beard_thin", "mansions=bury", "monuments=none"]
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `structureAdaptation` | liste de `structure=mode` | villages et manoirs `beard_thin`, le reste `none` | Les structures auxquelles le terrain s'adapte et de quelle façon, pour les villages, forteresses, mines abandonnées, monuments et manoirs. Les modes sont `none`, `bury`, `beard_thin`, `beard_box` et `encapsulate` |

`structureAdaptation` décide des structures auxquelles le terrain s'adapte et de quelle façon, sous forme d'entrées `structure=mode`, `"mansions=bury"`, `"monuments=none"`, pour les villages, forteresses, mines abandonnées, monuments et manoirs, avec les cinq modes qu'utilisent les versions modernes : `none`, `bury`, `beard_thin`, `beard_box` et `encapsulate`. Les villages et les manoirs sont en `beard_thin` sauf surcharge et tout le reste est en `none` sauf mention. Les temples ne peuvent pas encore être nommés, car ils ne se placent qu'au fil de leur construction, si bien qu'il n'y a rien à quoi le terrain puisse s'adapter à temps.

### Implantation des villages

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "terrainAdaptation": true
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `terrainAdaptation` | booléen | `false` | Retravaille la façon dont les villages choisissent leur sol et s'y posent : routes nivelées, bâtiments assis, anneaux en talus et tout le reste de ce que décrit cette section. Ce qu'il met en place est permanent |

**Ce qu'il met en place est permanent.** Il remodèle le terrain à la création du monde, si bien que tout ce qu'il inscrit dans une sauvegarde y reste. Un village posé par une ancienne version n'est jamais revisité ni réparé par une plus récente, donc deux mondes créés à partir de la même seed sur deux versions différentes du mod ne correspondront pas, et les villages d'un monde sont un instantané du jour où ces chunks ont été générés.

`terrainAdaptation` retravaille la façon dont les villages choisissent leur sol et s'y posent, porté dans l'esprit de la manière dont les versions modernes implantent leurs structures, puis poussé plus loin. Un village ne se fonde que sur un chunk dont le sol varie de dix blocs au plus, et jamais à moins de huit chunks d'un autre village ; les régions qui n'offrent aucun chunk de ce genre ne fondent rien du tout. Le puits s'installe au niveau le plus bas que touche son emprise, et tout le village se décale avec lui, si bien que tout le reste se met à niveau à partir de là.

Les routes sont nivelées au moment où on les pose : la surface suit le sol naturel le plus bas sur la largeur de la route, les bosses sont rabotées, les creux comblés, la pente ne dépasse jamais un bloc par marche, et les courtes failles sont franchies par des planches. La surface de la route suit le sol qu'elle traverse : chemins d'herbe sur la terre, grès sur le sable, argile durcie sur la mesa, gravier sur la pierre et sur le gravier, planches au-dessus de l'eau, de sorte qu'un village du désert reçoit des rues de grès plutôt qu'un chemin de terre et que les routes ne disparaissent plus là où le sol n'est pas de l'herbe. Là où deux routes se croisent, elles se rejoignent au plus bas des deux niveaux, puisqu'un niveau que les deux peuvent atteindre est le seul qui ne laisse aucune marche entre elles.

Chaque bâtiment s'installe un bloc au-dessus de la route qu'il borde, lu sur la route posée ou prédit d'après le sol sur lequel la route sera nivelée quand elle n'est pas encore construite, de sorte que les marches de son seuil reposent sur la surface de la route et que sa porte se trouve derrière elles. Un bâtiment dont l'emprise exigerait plus de deux blocs de terrain rapporté sous une quelconque de ses parties n'est pas construit là : il glisse jusqu'à douze blocs le long de sa route à la recherche de l'assise la moins profonde, et il est abandonné s'il n'en trouve aucune, si bien que les villages sur terrain accidenté sortent plus clairsemés plutôt que perchés. L'anneau autour d'un bâtiment est remblayé du côté aval et entaillé du côté amont, un bloc moins haut encore un anneau plus loin.

Les fermes gardent le niveau de sol propre à vanilla. Les lampadaires se dressent au niveau de la route qu'ils éclairent plutôt qu'à celui de l'accotement voisin, avec du terrain comblé dessous là où la route passe au-dessus du bas-côté, et les poteaux à torches de vanilla sont retirés du plan puisque ceux-ci les remplacent. Le sol est comblé sous chaque bâtiment jusqu'à la surface d'appui la plus proche, dans le même matériau que celui sur lequel il repose, les murs et les embrasures sont dégagés des flancs de colline, la terre est retirée des toits, et tout arbre se trouvant dans une structure est abattu en entier, ses feuilles partant avec son bois tandis que chaque feuille qu'une branche encore debout possède est laissée en place. Les manoirs et les éléments isolés (temples, cabanes, igloos) sont soumis au même critère de sol plat avant de pouvoir se placer.

Il remodèle le terrain lui-même au moment où il est créé, si bien qu'un monde généré avec lui activé diffère d'un monde généré sans, le même avertissement que portent les versions modernes, et il est désactivé sauf si un pack ou la configuration le demande.

### Bedrock

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "flatBedrock": true,
    "flatBedrockRetrogen": false,
    "bedrockLayers": 1,
    "flatBedrockRoof": true,
    "flatBedrockFiller": "minecraft:stone",
    "flatBedrockFillers": ["-1=minecraft:netherrack", "1=minecraft:end_stone"],
    "flatBedrockDimensions": [0, -1],
    "flatBedrockDimensionsAreBlacklist": false,
    "flatBedrockBiomes": ["minecraft:plains"],
    "flatBedrockBiomeTypes": ["MOUNTAIN"],
    "flatBedrockBiomesAreBlacklist": true
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `flatBedrock` | booléen | `false` | Remplace la bedrock irrégulière au fond du monde par des couches plates. Nouveaux chunks seulement, sauf si `flatBedrockRetrogen` est activé |
| `flatBedrockRetrogen` | booléen | `false` | Aplatit aussi la bedrock des chunks déjà existants. Chaque chunk n'est traité qu'une fois et s'en souvient, et c'est irréversible : le motif d'origine n'est enregistré nulle part |
| `bedrockLayers` | entier | `1` | Combien de couches de bedrock restent |
| `flatBedrockRoof` | booléen | `false` | Aplatit aussi le plafond de bedrock, là où une dimension en a un, comme le toit du Nether |
| `flatBedrockFiller` | bloc | vide | Ce qui remplace la bedrock retirée. Vide, le choix se fait par dimension : pierre, netherrack, pierre de l'End |
| `flatBedrockFillers` | liste de `dimension=bloc` | les valeurs du Nether et de l'End | Un remplissage par dimension, qui prend le pas sur `flatBedrockFiller` pour les dimensions nommées |
| `flatBedrockDimensions` | liste d'entiers | `0`, l'overworld | Les dimensions dans lesquelles aplatir. Vide signifie toutes |
| `flatBedrockDimensionsAreBlacklist` | booléen | `false` | Activé, l'aplatissement saute les dimensions listées. Désactivé, il ne s'applique qu'à elles |
| `flatBedrockBiomes` | liste de noms de biomes | aucun | Les biomes dans lesquels aplatir, par nom courant ou nom de registre. Vide signifie tous les biomes |
| `flatBedrockBiomeTypes` | liste de types du dictionnaire | aucun | Les types du dictionnaire de biomes dans lesquels aplatir, en plus de `flatBedrockBiomes`. `OCEAN`, `RIVER`, `MOUNTAIN` et les autres |
| `flatBedrockBiomesAreBlacklist` | booléen | `false` | Activé, l'aplatissement saute les biomes listés. Désactivé, il ne s'applique qu'à eux |

`flatBedrock` remplace la couche irrégulière par des couches plates, par dimension et par biome, avec un bloc de remplissage de votre choix. `flatBedrockRetrogen` le fait aussi pour les chunks déjà existants. C'est irréversible, le motif d'origine n'est enregistré nulle part. `bedrockLayers` fixe le nombre de couches qui restent, `flatBedrockRoof` traite aussi le plafond là où une dimension en a un, et `flatBedrockFiller` est ce qui remplace la bedrock retirée, laissé vide pour choisir par dimension, `flatBedrockFillers` en nommant plutôt un par dimension. Les dimensions et biomes concernés sont réglés par `flatBedrockDimensions`, `flatBedrockBiomes` et `flatBedrockBiomeTypes`, `flatBedrockDimensionsAreBlacklist` et `flatBedrockBiomesAreBlacklist` transformant ces listes en exclusions.

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

Les entités coûtent plus cher à un serveur que n'importe quoi d'autre, et la plupart d'entre elles sont loin de tout joueur. `slowDistantEntities` donne à un chunk sans joueur à moins de `slowDistance` blocs un tick sur `slowRate`, si bien que ce qu'il contient bouge, flotte, brûle et disparaît toujours, mais à un rythme plus lent. Rien n'est jamais laissé sans tick.

| Clé | Requise | Valeur | Défaut | Ce qu'elle fait |
| --- | --- | --- | --- | --- |
| `slowDistantEntities` | non | booléen | `true` | Si quoi que ce soit est ralenti |
| `slowedKinds` | non | liste de `items`, `experience`, `projectiles` | `{items, experience}` | Les types qui reçoivent moins de ticks. Tout ce qui pense par soi-même est toujours ralenti autrement et n'est pas nommé ici. Les machines ne sont jamais ralenties |
| `slowDistance` | non | entier, 64 et plus | `192` | À quelle distance du joueur le plus proche un chunk est ralenti |
| `slowRate` | non | entier, 1 à 20 | `4` | Un tick sur autant est donné à un chunk ralenti. `1` ne ralentit rien |
| `neverSlowed` | non | liste de noms d'entités | aucun | Laissées intactes, aussi loin qu'elles soient |
| `slowRecheck` | non | entier, 1 à 100 | `20` | À quelle fréquence la distance au joueur le plus proche est recalculée |

Tout ce qui pense par soi-même, chaque mob, animal, villageois et golem, quel que soit le mod dont il provient, est traité différemment du reste et n'est pas du tout nommé dans `slowedKinds`. Il ne reçoit jamais moins de ticks, car un joueur peut le voir marcher. Il continue au contraire de recevoir un tick à chaque tick et on lui fait réfléchir moins souvent : la partie de son esprit qui décide de la suite, qui est aussi la plus coûteuse, n'est sollicitée qu'une fois sur `slowRate` au lieu d'un tick sur trois. Il continue de se déplacer, de tomber, de se noyer, de brûler et de chercher son chemin exactement comme il le ferait, et change simplement d'avis moins souvent tant que personne n'est à proximité. Il n'y a rien à voir, ni saccade ni rattrapage, et celui sur lequel un joueur arrive a retrouvé son comportement ordinaire avant d'être en vue. Comme cela ne peut pas se remarquer, ce n'est pas un choix : cela se produit partout où le ralentissement est activé.

Ce qui reçoit moins de ticks vieillit toujours à l'allure normale. Un objet jeté au sol et une orbe d'expérience portent chacun leur propre compteur qui décide de leur disparition, et lors d'un tick qu'un chunk ralenti ne prend pas, ce compteur avance quand même. Un objet reste donc cinq minutes au sol plutôt que vingt. Seul ce qu'il fait à chaque tick est réduit, jamais sa durée de vie.

Un chunk que quelque chose maintient délibérément chargé n'est jamais ralenti, quelle que soit sa distance. Ce sont les chunks qu'un chargeur de chunks conserve, et tout l'intérêt d'en garder un est que ce qu'il contient continue de tourner, de sorte qu'une ferme laissée en fonctionnement pendant que son propriétaire est ailleurs travaille au rythme pour lequel elle a été construite. Les chunks autour du point d'apparition d'un monde n'en font pas partie, puisque personne ne les a demandés, et sont donc ralentis comme partout ailleurs.

Un chunk entier est ralenti ou non ralenti d'un seul bloc, de sorte que ce qu'il contient se comporte toujours comme il le faut : les objets atterrissent dans le même tas, un mob suit toujours celui qui est à côté. Chaque joueur compte pour lui-même, si bien que quelqu'un parti seul dans son coin garde un espace calme autour de lui où qu'il soit. Tout ce qui est monté, nommé, apprivoisé, tenu en laisse, lumineux, empêché de disparaître, sous l'effet d'une potion ou déjà en train de poursuivre un joueur est laissé tranquille quelle que soit sa distance, tout comme toutes les machines. Cela s'applique à tous les mondes, y compris ceux qu'ajoute un mod.

### Surveiller le travail sur les chunks

*ce que fait chaque groupe*

Avec `worldgenDebug` activé, une ligne toutes les cent manches indique comment le monde dépense son travail sur les chunks : combien de chunks ont été créés neufs, combien ont dû être rappelés après avoir été relâchés, combien d'entre eux sont revenus du disque plutôt que de la file encore en attente d'écriture, combien de fichiers de région ont été ouverts et à quelle fréquence ils ont tous été fermés d'un coup, et le plus grand nombre de chunks retenus et d'écritures en attente à un moment donné. Elle sert à déterminer si la génération du terrain coûte du temps en génération ou à récupérer le même terrain, et il vaut donc la peine de l'activer avant une grande prégénération et de la désactiver ensuite.

Trois autres lignes suivent : une pour l'écriture des chunks vers le stockage, une pour leur éclairage, et une qui décompose la création du terrain lui-même en le sol, l'habillage que le jeu y ajoute, et l'habillage qu'y ajoute chaque mod, les cinq pires étant nommés. Un monde lent peut alors se lire comme quatre coûts distincts plutôt qu'un seul, et le mod responsable est nommé plutôt que deviné.

### Créer le terrain à l'avance

*ce que fait chaque groupe*

Assez vaste pour avoir sa propre section, voir [Prégénération](#prégénération).

### Blocs en attente de leur tour

*ce que fait chaque groupe*

L'eau qui s'écoule, la lave qui refroidit et les cultures qui poussent sont autant de blocs qui attendent un moment avant de faire quelque chose, et le jeu les garde tous dans un seul tas. Chaque fois qu'un chunk est écrit, il parcourt ce tas d'un bout à l'autre pour trouver les quelques-uns qui lui appartiennent, de sorte que plus un monde en compte, plus chaque écriture devient lente, que le chunk écrit en ait ou non. Ils sont triés selon le chunk dans lequel ils se trouvent, et ce tri est jeté puis refait dès que le tas change ou que la manche passe, si bien que l'écriture d'un chunk ne regarde que la poignée qui le concerne.

### Plus de place pour les blocs d'un chunk

*ce que fait chaque groupe*

Un chunk est conservé par tranches, et chaque tranche contient une liste des sortes de blocs qui s'y trouvent, avec au départ de la place pour seize. Dépasser seize oblige à créer une liste plus grande et à y recopier chacun des quatre mille blocs de la tranche, puis à recommencer à trente-deux, et encore à soixante-quatre. Un sol composé de quelques sortes de pierre et de minerai dépasse tout cela, et l'opération est donc faite quatre fois pour un peu de place. Elle passe désormais directement à la plus grande de ces tailles dès la première fois que la place manque, ce qui fait une seule copie au lieu de quatre et coûte quelques kilo-octets par tranche qui est de toute façon utilisée dans l'instant.

### Préparer les chunks à l'écriture

*ce que fait chaque groupe*

Avant qu'un chunk puisse être écrit, il est converti dans la forme qui va sur le disque, ce qui parcourt chacun de ses blocs et cherche chacun d'eux par son nom dans une table. Le sol se présente en longues séries de la même chose, si bien que la même recherche est faite des milliers de fois pour la même pierre, et la réponse à la dernière est simplement conservée et réutilisée quand le bloc suivant est identique. Cela ne peut pas être désactivé, puisqu'il n'y a rien à peser : la réponse est la même dans les deux cas.

### Écriture des chunks

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "hurryWritesAbove": 100
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `hurryWritesAbove` | entier, chunks | `100` | Combien de chunks terminés peuvent attendre d'être écrits avant que l'écrivain cesse de se reposer un centième de seconde après chacun et écrive simplement aussi vite qu'il le peut. `0` le laisse toujours se reposer, comme le fait le jeu |

Le jeu écrit les chunks terminés sur un thread à part, un à la fois, en se reposant un centième de seconde après chacun. Cela le limite à environ cent chunks par seconde, quelle que soit la rapidité du disque, ce qui est largement suffisant tant que quelqu'un joue et loin de l'être quand le terrain est créé en masse, de sorte que les chunks non écrits s'accumulent en mémoire. `hurryWritesAbove` indique combien peuvent attendre avant qu'il cesse de se reposer et écrive simplement aussi vite qu'il le peut. `100` est la valeur par défaut et correspond au seuil à partir duquel le jeu lui-même commence à retenir la génération ; `0` le laisse toujours se reposer, comme le fait le jeu. Rien ne change tant que le nombre en attente est faible, ce qui est le cas à chaque moment ordinaire du jeu.

Chaque fois que le nettoyage s'exécute, une ligne est écrite à mesure, indiquant quel balayeur s'est exécuté, combien de temps il a pris, ce qui était retenu avant et après, et de combien de mémoire disposait alors le jeu. Si cette mémoire change, c'est signalé, car la croissance de la mémoire est elle-même la cause des plus longues de ces pauses : un jeu lancé avec moins de mémoire que ce dont il finit par avoir besoin s'arrêtera pour l'agrandir, à répétition, à des moments sans rapport avec ce qu'il fait. Le lancer avec toute la mémoire qui lui est permise évite cela entièrement.

Une dernière ligne indique la quantité de déchets de travail jetés depuis le dernier relevé, le temps qu'a pris leur nettoyage et le nombre de balayages que cela a représenté, ainsi que la part de la mémoire autorisée que le jeu détient actuellement. Créer du terrain jette énormément de choses par nature, puisque chaque chunk est converti en tableaux neufs avant d'être écrit, et ce nettoyage se fait entre les manches plutôt que pendant, de sorte qu'il apparaît comme une saccade plutôt que comme du temps dans l'un des compteurs ci-dessus.

### Chunks de spawn

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "spawnChunkRadius": 128,
    "spawnChunkRadii": ["0=64", "7=0"]
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `spawnChunkRadius` | entier, blocs | `128` | À quelle distance du point d'apparition d'un monde, en blocs, des chunks sont maintenus chargés que quelqu'un s'y trouve ou non. B blocs maintiennent `r = (B + 8) / 16` chunks dans chaque direction à partir du chunk de spawn, `(2r+1)²` au total, et le monde en prépare `(2r+9)²` autour de lui au démarrage. `128` est ce que fait le jeu, avec 289 maintenus et 625 préparés, et `0` n'en maintient ni n'en prépare aucun |
| `spawnChunkRadii` | liste de `dimension=blocs` | aucun | Un rayon pour une dimension à la fois, qui prend le pas sur `spawnChunkRadius` pour les dimensions nommées |

Le jeu maintient chargés les chunks autour du point d'apparition d'un monde que quelqu'un s'y trouve ou non, afin que les mods aient toujours un endroit qui tourne. C'est 128 blocs dans toutes les directions, environ 289 chunks, et ce n'est pas réglable dans le jeu. `spawnChunkRadius` fixe cette distance. `128` est ce que fait le jeu et la valeur par défaut, un nombre plus petit garde une ancre plus petite, et `0` n'en garde aucune, si bien que la zone de spawn se décharge comme n'importe où ailleurs. `spawnChunkRadii` fixe un rayon pour une dimension à la fois, écrit sous la forme `dimension=blocs`, un par ligne, et prend le pas sur `spawnChunkRadius` pour les dimensions nommées.

Seule une dimension enregistrée pour conserver son spawn en garde un, ce qui, dans le jeu lui-même, ne concerne que l'overworld, le Nether et l'End n'en ayant jamais conservé, si bien que régler cela pour eux ne change rien. Une dimension qu'ajoute un mod n'en conserve un que si ce mod l'a demandé, et un mod qui l'a fait porte souvent 289 chunks de plus dont un pack ne voulait pas. Le fait qu'un monde reste chargé ou non est une tout autre affaire que ce réglage ne touche pas : une dimension qu'un mod a marquée comme restant chargée le reste à `0`, elle cesse simplement de maintenir des chunks. La plupart des mods qui utilisent le spawn comme ancre veulent qu'il y ait quelque chose là plutôt que 289 chunks, si bien qu'un petit nombre les fait généralement fonctionner alors qu'un `0` non.

### Monde du vide

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "voidWorld": true,
    "voidPlatformBlock": "minecraft:stone",
    "voidPlatformSize": 5,
    "voidPlatformHeight": 64,
    "voidWorldDimensions": [0],
    "voidWorldDimensionsAreBlacklist": false
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `voidWorld` | booléen | `false` | Génère un monde vide avec une plateforme au point d'apparition, et empêche mobs, animaux, structures et tout ce qu'un mod générerait autrement d'y apparaître |
| `voidPlatformBlock` | bloc | `minecraft:stone` | De quoi la plateforme est faite |
| `voidPlatformSize` | entier, blocs | `9` | La largeur de la plateforme, arrondie à l'impair inférieur pour qu'elle soit centrée sur le point d'apparition |
| `voidPlatformHeight` | entier | `64` | À quelle hauteur au-dessus du fond du monde se trouve la plateforme |
| `voidWorldDimensions` | liste d'entiers | `0`, l'overworld | Les dimensions vidées. Seul l'overworld reçoit une plateforme |
| `voidWorldDimensionsAreBlacklist` | booléen | `false` | Activé, les dimensions listées sont celles qu'on laisse intactes |

`voidWorld` génère un monde vide avec une plateforme au point d'apparition, et empêche mobs, animaux, structures et tout ce qu'un mod générerait autrement d'y apparaître. Le bloc, la taille et la hauteur de la plateforme sont `voidPlatformBlock`, `voidPlatformSize` et `voidPlatformHeight` ; la taille est arrondie à un nombre impair de blocs pour que la plateforme soit centrée sur le point d'apparition. `voidWorldDimensions` choisit les mondes qui sont vidés, l'overworld seul par défaut, et `voidWorldDimensionsAreBlacklist` transforme cette liste en celle des mondes à laisser intacts. Le Nether et l'End sont vidés de la même manière que l'overworld, qu'il s'agisse de ceux que construit cette version ou de ceux par lesquels un mod les a remplacés. Seul l'overworld reçoit une plateforme, si bien que fournir un accès à un Nether ou à un End vidé est l'affaire du pack. Un End vidé n'a ni dragon, ni cristaux, ni fontaine de bedrock non plus, puisque le combat qui les construit n'est pas lancé.

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

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `dragonFight` | booléen | `true` | Si le tout a lieu ou non : le dragon, sa barre, les cristaux, la fontaine sur laquelle il se tient, et la réinvocation qu'un joueur lancerait avec des cristaux de l'End. Appartient au groupe `structures` |

`dragonFight` appartient au groupe `structures` et décide si le tout a lieu ou non : le dragon, sa barre, les cristaux, la fontaine sur laquelle il se tient, et la réinvocation qu'un joueur lancerait avec des cristaux de l'End. Un End vidé s'en passe sauf si un pack le demande, et un End ordinaire l'a sauf si un pack dit le contraire, si bien que `dragonFight` vaut la peine d'être réglé dans un sens comme dans l'autre.

### Terrain

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldType": "biomesop",
    "worldTypeExceptions": ["flat", "debug_all_block_states"],
    "worldSeed": "Hollow Ridge",
    "generatorOptions": "3;minecraft:bedrock,59*minecraft:stone,3*minecraft:dirt,minecraft:grass;1",
    "terrainWorldTypes": ["default", "customized"],
    "terrainWorldTypesAreBlacklist": false
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `worldType` | chaîne | vide | Le type de monde avec lequel chaque nouveau monde est créé, quel que soit celui choisi sur l'écran de création : `default`, `largebiomes`, `amplified`, `customized`, ou un type qu'ajoute un mod. Un serveur dédié l'écrit dans `server.properties` sous `level-type` avant le chargement des mondes, sauf si `level-type` nomme déjà l'une des `worldTypeExceptions`. Vide, le choix revient à celui qui crée le monde |
| `worldTypeExceptions` | liste de types de monde | `flat`, `debug_all_block_states` | Les choix que `worldType` laisse en place |
| `worldSeed` | chaîne | vide | La seed avec laquelle chaque nouveau monde est créé, écrite comme on la saisirait : un nombre est utilisé tel quel, et tout le reste est converti en nombre comme le fait le jeu. Un serveur dédié l'écrit dans `server.properties` sous `level-seed` avant le chargement des mondes |
| `terrainWorldTypes` | liste de types de monde | aucun | Les types de monde auxquels les réglages de terrain sont appliqués. Vide signifie tous |
| `terrainWorldTypesAreBlacklist` | booléen | `false` | Activé, les types de monde listés sont ceux qu'on laisse intacts |

`worldType` décide du type d'un nouveau monde, quel que soit celui choisi sur l'écran de création, `default`, `largebiomes`, `amplified`, `customized`, ou un type qu'ajoute un mod comme `biomesop` ou `realistic`. Un pack construit autour d'un type de monde le nomme ici et chaque nouveau monde est créé ainsi. Vide, la valeur par défaut, le choix revient à celui qui crée le monde. Un monde déjà existant garde le type avec lequel il a été créé, et un nom que rien ne fournit est consigné dans le journal puis ignoré. `worldTypeExceptions` nomme les choix qui sont laissés en place, superplat et le monde de débogage pour commencer, car un pack qui veut un seul type de monde ne cherche que rarement à retirer le superplat à quelqu'un qui fait des essais, et celui qui crée un monde est informé dans le chat, une fois dedans, que le pack a choisi son type. Ce message relève de la configuration avec `tellWorldType`, pas d'un pack, si bien que le joueur peut le désactiver pour lui-même et qu'aucun pack ne peut le réactiver. Les réglages avec lesquels le monde a été créé sont abandonnés quand le type change, puisqu'ils avaient été écrits pour le type choisi.

`worldSeed` décide de la seed avec laquelle chaque nouveau monde est créé, quoi qu'on ait saisi sur l'écran de création. Elle s'écrit comme on la saisirait : un nombre est utilisé tel quel, et tout le reste est converti en nombre comme le jeu convertit un mot, si bien que `Hollow Ridge` et `-4172144997902289642` sont tous deux permis et donnent toujours le même monde. Vide, la valeur par défaut, le choix revient à celui qui crée le monde. Un monde déjà existant garde la seed avec laquelle il a été créé, donc cela ne décide jamais que de ce que reçoit un nouveau monde. Un pack construit autour d'une seule carte nomme sa seed ici et chaque monde créé avec ce pack est cette carte.

`generatorOptions` façonne l'overworld lui-même, niveau de la mer, océans de lave et chaque bruit de terrain, dans le même format qu'écrit le type de monde personnalisé. Il est appliqué à un monde au moment de sa création et jamais ensuite, si bien qu'un monde déjà existant reste exactement tel qu'il était. Un monde qui porte déjà ses propres options les garde, et le journal nomme la chaîne utilisée. Un serveur dédié les écrit dans `server.properties` sous `generator-settings` avant le chargement des mondes.

Un type de monde qui porte ses propres réglages et ne regarde jamais ceux du monde, comme le type realistic de Quark, reçoit les réglages du pack fusionnés aux siens, si bien que la forme pour laquelle il a été conçu reste, sauf si un pack demande autre chose.

`terrainWorldTypes` nomme les types de monde auxquels les réglages sont appliqués, `default`, `customized`, `biomesop`, `realistic` et ainsi de suite, et `terrainWorldTypesAreBlacklist` en fait la liste de ceux à laisser intacts. Vide, la valeur par défaut, elle désigne tous les types de monde. Un pack qui façonne le monde ordinaire mais veut qu'un type de monde fourni par un mod reste exactement tel que ce mod l'a conçu le nomme ici et n'a rien d'autre à faire : rien n'est fusionné, rien n'est transmis, et l'écran de personnalisation du mod reste ouvert. Les noms sont comparés au type de monde avec lequel un monde a été créé, si bien que nommer un type que rien ici ne fournit ne correspond jamais à rien et ne coûte rien.

Tout ce qui suit sur Biomes O' Plenty ne se produit que lorsque ce mod est installé, puisque le travail est fait par une compatibilité qui n'est chargée qu'en sa présence. Sans lui, il n'y a pas de type de monde `biomesop` à choisir, et un pack qui en nomme un se retrouve avec le type de monde avec lequel le monde a réellement été créé.

Sur un monde Biomes O' Plenty, les mêmes réglages sont traduits dans les termes que lit ce mod, de sorte qu'un pack n'a pas besoin d'en fournir une seconde copie. `biomeSize` devient l'une de ses cinq tailles, les réglages de bruit et d'échelle passent tels quels, et tout ce qu'il ne lit jamais est écarté avec une ligne dans le journal pour le dire. Ce mod lit bien moins de choses que le type de monde personnalisé, et ne lit jamais le niveau de la mer, les grottes, les lacs ni les interrupteurs de structures à partir de ses réglages, si bien qu'on les lui transmet directement à la place, et un pack les règle comme il le ferait pour n'importe quel autre type de monde.

Il décide de deux choses par lui-même. Les rivières sortent de ses propres couches et n'ont aucun réglage, si bien que `riverSize` n'y signifie rien. Et l'emplacement réel des océans, des montagnes et des régions relève aussi de ses couches, accessibles seulement par `landScheme`, `tempScheme`, `rainScheme` et `biomeSize`, de sorte qu'un pack façonne ce monde dans les termes de ce mod plutôt que dans ceux du type de monde personnalisé. Un monde d'un seul biome reste du ressort d'un pack : bloquez tous les biomes et nommez celui que vous voulez comme `default` du modèle, ce qui fonctionne de la même façon sur son type de monde que sur n'importe quel autre.

Tout le reste de ce que fait un pack, bloquer des biomes et des minerais, remplacer des blocs, la bedrock plate, le placement des structures, son propre worldgen, n'est jamais passé par cette chaîne et fonctionne de la même manière sur n'importe quel type de monde.

### Serveur

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldGameMode": "creative",
    "worldDifficulty": ["normal", "-1=hard"],
    "worldLanCommands": false,
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
    "worldBuildHeight": 256
  }
}
```

`control.server` décide de ce groupe : les lignes de `server.properties` qu'un pack peut régler, avec le mode de jeu, la difficulté et les commandes sur un monde ouvert au LAN. Sur un serveur dédié, chaque valeur qu'un pack définit ici est écrite dans `server.properties` au démarrage du serveur, si bien que le fichier indique ce qui est en vigueur, et celles que le serveur a déjà lues sont aussi appliquées à celui-ci. Un monde solo prend ce qu'a un serveur intégré, comme l'indique chaque ligne. Vide, ou `-1` pour un nombre, laisse la valeur propre au serveur, et avec `control.server` sur `off`, chaque ligne reste telle que le serveur l'a.

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `worldGameMode` | `survival`, `hardcore`, `creative`, `adventure` ou `spectator` | vide | Le mode dans lequel chaque nouveau monde démarre, appliqué à la création seulement en solo. Un serveur dédié remet chaque monde dans le mode de son `server.properties` à chaque démarrage, si bien que le mode du pack y est écrit dans `server.properties` (`gamemode` et `hardcore`) avant le chargement des mondes. `hardcore` est survie plus l'indicateur hardcore de la sauvegarde, et `creative` active aussi les triches |
| `worldLanCommands` | booléen | `true` | Si un joueur qui ouvre un monde solo au LAN peut activer les commandes pour tous ceux qui le rejoignent. `false` grise le bouton Autoriser les triches de l'écran Ouvrir au LAN et le maintient désactivé, et le monde est ouvert sans commandes quelle que soit la façon dont elles sont demandées, `/publish` compris |
| `worldDifficulty` | liste | aucun | Verrouille la difficulté sur `peaceful`, `easy`, `normal` ou `hard`. Une difficulté seule couvre toutes les dimensions, et une ligne `dimension=difficulté` la surcharge pour celle-là. Un serveur dédié écrit la difficulté de l'overworld dans `server.properties` sous `difficulty` |
| `worldForceGameMode` | booléen | vide | Si un joueur qui se connecte est remis dans le mode de jeu du serveur à chaque fois, la ligne `force-gamemode`. Un monde solo ouvert au LAN la prend aussi |
| `worldPvp` | booléen | vide | Si les joueurs peuvent se blesser entre eux, la ligne `pvp`. Un monde solo la prend aussi |
| `worldFlight` | booléen | vide | Si un joueur qui vole en survie est laissé tranquille au lieu d'être expulsé, la ligne `allow-flight`. Un monde solo la prend aussi |
| `worldSpawnProtection` | entier, -1 ou plus | `-1` | Combien de blocs autour du point d'apparition seuls les opérateurs peuvent construire, la ligne `spawn-protection`, 0 pour aucun. Seul un serveur dédié protège son point d'apparition |
| `worldNether` | booléen | vide | Si le Nether peut être visité, la ligne `allow-nether`. `false` le ferme aussi dans un monde solo |
| `worldCommandBlocks` | booléen | vide | Si les blocs de commande s'exécutent, la ligne `enable-command-block`. Un monde solo les exécute déjà, et `false` les désactive là aussi |
| `worldIdleTimeout` | entier, -1 ou plus | `-1` | Combien de minutes un joueur peut rester inactif avant d'être expulsé, la ligne `player-idle-timeout`, 0 pour jamais. Un monde solo la prend aussi |
| `worldMotd` | texte | vide | La ligne affichée sous le nom du serveur dans la liste des serveurs, la ligne `motd`. Un monde solo ouvert au LAN l'affiche à la place du propriétaire et du nom du monde |
| `worldMaxSize` | entier, -1 à 29999984 | `-1` | La distance maximale, en blocs depuis le centre, jusqu'où une bordure de monde peut s'étendre, la ligne `max-world-size`. Un monde solo la prend aussi |
| `worldStructures` | booléen | vide | Si un nouveau monde génère des structures, la ligne `generate-structures` et le choix Générer des structures sur l'écran du monde. Appliqué à un monde seulement au moment de sa création |
| `worldSpawnMonsters` | booléen | vide | Si les monstres hostiles apparaissent, la ligne `spawn-monsters`. `false` les empêche aussi dans un monde solo |
| `worldSpawnAnimals` | booléen | vide | Si les animaux apparaissent, la ligne `spawn-animals`. Un monde solo la prend aussi |
| `worldSpawnNpcs` | booléen | vide | Si les villageois apparaissent, la ligne `spawn-npcs`. Un monde solo la prend aussi |
| `worldViewDistance` | entier, -1 à 32 | `-1` | Jusqu'à combien de chunks un serveur dédié envoie le monde à chaque joueur, la ligne `view-distance`. Un monde solo suit la distance d'affichage à la place |
| `worldBuildHeight` | entier, -1 à 256 | `-1` | Le y le plus élevé auquel un bloc peut être placé, la ligne `max-build-height`, arrondi à un multiple de 16 entre 64 et 256. Un monde solo la prend aussi |

**`worldGameMode`** (groupe `server`) : `survival`, `hardcore`, `creative`, `adventure` ou `spectator`. Appliqué à la création du monde seulement ; les mondes existants ne sont pas touchés, et un changement de mode ultérieur est laissé tranquille. `hardcore` est survie plus l'indicateur hardcore vanilla de toute la sauvegarde ; `creative` active aussi les triches, comme le ferait la case à cocher de l'écran de création. L'écran de création s'ouvre avec le mode (et la seed du pack) présélectionnés ; un joueur peut le changer là, mais le pack le remet en place à la création. `adventure` et `spectator` ne sont pas proposés sur cet écran et sont appliqués au moment où le monde est créé.

**`worldLanCommands`** (groupe `server`) : `true` (par défaut) laisse l'écran Ouvrir au LAN tel que vanilla l'a. `false` grise son bouton Autoriser les triches et le maintient désactivé, si bien qu'un joueur qui ouvre le monde au LAN ne peut pas donner les commandes à tous ceux qui le rejoignent. Le monde est aussi ouvert sans commandes côté serveur, quoi qui les demande, `/publish` compris. Cela ne régit que l'ouverture au LAN ; un serveur dédié n'est pas concerné.

**`worldDifficulty`** (groupe `server`) : `peaceful`, `easy`, `normal` ou `hard`. Une valeur seule couvre toutes les dimensions ; des lignes `dimension=difficulté` (`-1=hard`) surchargent dimension par dimension. Le verrou tient face au menu pause. Vide (par défaut), la difficulté est laissée au joueur.

### Journalisation

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "logBlockedOres": true,
    "logBlockedBiomes": true,
    "logBlockedGenerators": true,
    "logBlockedRecipes": true,
    "logBlockReplacements": true
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `logBlockedOres` | booléen | `true` | Consigne la première fois que chaque mod et type de minerai est refusé |
| `logBlockedBiomes` | booléen | `true` | Consigne, par mod, le nombre de biomes bloqués |
| `logBlockedGenerators` | booléen | `true` | Consigne la première fois que chaque mod et générateur est bloqué |
| `logBlockedRecipes` | booléen | `true` | Consigne, par mod, ce qui a été bloqué |
| `logBlockReplacements` | booléen | `true` | Consigne la première fois que chaque remplacement est effectué, et un total quand un monde rattrape son retard |

`logBlockedOres`, `logBlockedBiomes`, `logBlockedRecipes` et `logBlockReplacements` consignent chacun la première fois que quelque chose est refusé, de sorte que vous voyez ce qu'une règle de blocage a réellement attrapé plutôt que de deviner d'après ce qui manque. C'est la première chose à activer quand une règle semble ne rien faire, ou en faire trop.

### Recettes

*ce que fait chaque groupe*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockRecipes": true,
    "recipeWhitelist": ["minecraft", "mypack"],
    "blockedRecipeMods": ["tconstruct"],
    "blockFurnaceRecipes": true,
    "furnaceWhitelist": ["minecraft", "mypack"],
    "blockedFurnaceMods": ["tconstruct"],
    "recipeMatch": "recipe"
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `blockRecipes` | booléen | `false` | Supprime toutes les recettes d'artisanat sauf celles des mods de `recipeWhitelist`. Rien n'est exempté par défaut, donc listez le namespace de votre propre pack pour conserver ses recettes. Les ajouts de CraftTweaker et de GroovyScript survivent toujours |
| `recipeWhitelist` | liste d'identifiants de mod | `minecraft` | Les mods dont les recettes d'artisanat survivent |
| `blockedRecipeMods` | liste d'identifiants de mod | aucun | Les mods dont les recettes d'artisanat sont purement supprimées, quoi qu'en dise la liste blanche |
| `blockFurnaceRecipes` | booléen | `false` | La même chose pour les recettes de fourneau, le mod étant lu d'après l'objet produit |
| `furnaceWhitelist` | liste d'identifiants de mod | `minecraft` | Les mods dont les recettes de fourneau survivent |
| `blockedFurnaceMods` | liste d'identifiants de mod | aucun | Les mods dont les recettes de fourneau sont purement supprimées |
| `recipeMatch` | `recipe`, `output` ou `both` | `recipe` | D'où l'identifiant du mod est lu quand les recettes d'artisanat sont bloquées : le nom propre de la recette, l'objet qu'elle produit, ou l'un ou l'autre, ce qui bloque quand l'un des deux correspond et épargne quand l'un des deux est sur liste blanche |

`blockRecipes` et `blockFurnaceRecipes` suppriment tout sauf les mods de leurs listes blanches. Rien n'est exempté par défaut, donc listez le namespace de votre propre pack pour conserver ses recettes. Les ajouts de CraftTweaker et de GroovyScript survivent toujours, quoi qu'en dise la liste blanche. Les listes blanches sont `recipeWhitelist` et `furnaceWhitelist` ; `blockedRecipeMods` et `blockedFurnaceMods` vont dans l'autre sens et suppriment les recettes d'un mod nommé quoi qu'en dise la liste blanche. `recipeMatch` décide d'où l'identifiant du mod est lu quand les recettes d'artisanat sont bloquées : `recipe`, la valeur par défaut, utilise le nom propre de la recette, `output` utilise l'objet qu'elle produit, et `both` bloque quand l'un des deux correspond et épargne quand l'un des deux est sur liste blanche.

---

# Autres mods

## Universal Tweaks

*autres mods*

Universal Tweaks recoupe plusieurs des ajustements vanilla de ce mod. Là où ils se recoupent, ce mod s'efface (consigné à chaque fois, en nommant ce qui a été ignoré) plutôt que de laisser deux mods modifier la même méthode.

| Ce qui se recoupe | Quand ce mod s'efface |
| --- | --- |
| `promptLeafDecay` | Universal Tweaks a `Fast Leaf Decay` activé |
| `lenientPaths` | Universal Tweaks a `Lenient Paths` activé |
| `cactusMaxHeight` | Universal Tweaks est installé |
| `caneMaxHeight` | Universal Tweaks est installé |
| Retour du portail du Nether | Universal Tweaks est installé |

Les deux premiers lisent les propres interrupteurs d'Universal Tweaks dans `config/Universal Tweaks - Tweaks.cfg`, si bien qu'en désactiver un là-bas rend cette tâche à ce mod. La paire de hauteurs n'a pas d'interrupteur de ce genre à lire, seulement `Cactus Size` et `Sugar Cane Size`, si bien que ce mod s'efface dès qu'Universal Tweaks est présent et que vous réglez la hauteur là-bas.

**Retour du portail du Nether** : ce mod enregistre l'endroit où vous êtes entré dans le Nether et vous y ramène, au lieu de la recherche du portail le plus proche de vanilla. Universal Tweaks a sa propre gestion, donc cela est entièrement ignoré lorsqu'il est installé.

**Rien de tout cela ne touche un pack.** Tout ce qui précède concerne les cactus, la canne à sucre, les feuilles, les chemins et les portails de Minecraft lui-même. Les blocs que définit votre pack portent leur propre comportement, et les portails de pack sous `portals/*.json` forment un système distinct qu'Universal Tweaks ne voit jamais.

## Mo' Villages

*autres mods*

Mo' Villages ajoute des biomes de village et remplace des matériaux de village, deux choses que les packs peuvent aussi régler. Contrairement aux recoupements avec Universal Tweaks, ici le pack garde le dernier mot.

| Ce qui se recoupe | Ce qui se passe |
| --- | --- |
| `structureSpacing` pour les villages | Mo' Villages fixe son propre espacement à partir de `villageDistance` après que ce mod a demandé le sien. Si un pack a nommé un espacement, ce mod remet son nombre et le signale une fois dans le journal |
| `villageBlocks` | Mo' Villages remplace les matériaux de village par biome et marque le remplacement comme définitif. La table d'un pack est appliquée après, donc le pack l'emporte |
| `structureBiomes` pour les villages | Mo' Villages ajoute ses biomes à la liste propre du jeu. Une liste blanche de pack décide toujours de ce qui survit |

Rien ici ne demande d'activation. Si un pack ne précise ni espacement ni table de blocs, Mo' Villages est laissé libre de faire comme il l'entend.

Deux choses à savoir quand les deux sont installés. Mo' Villages règle aussi `minTownSeparation`, ce qui ne fait absolument rien en 1.12 : le champ est écrit une fois et jamais lu, ni par le jeu ni par ce mod. Et les blocs d'un village sont décidés par biome par Mo' Villages avant l'exécution de `villageBlocks`, si bien qu'en associant à la fois le bloc d'origine et le bloc par lequel Mo' Villages l'a remplacé, on attrape un village dans les deux cas, `minecraft:cobblestone=...` et `minecraft:brick_block=...` ensemble.

## CoFH World

*autres mods*

Les mods qui exigent CoFH World se chargent sans lui, l'exigence étant retirée automatiquement, sauf pour les mods qui appellent réellement son API et planteraient.

Leur propre génération n'a alors pas lieu, parce que CoFH World est ce qui lit leurs `assets/<modid>/world/*.json`. Un pack est censé y suppléer.

À défaut, `readCofhWorldFiles` lit ces fichiers directement dans les jars des mods et les génère par l'intermédiaire de ce mod. Il est désactivé par défaut, et il s'efface quand le vrai CoFH World est installé, qui génère alors normalement. Chaque générateur et chaque distribution de CoFH qui produit quelque chose est converti, associé aux formes et aux propagations ci-dessus. Les formes sont la géométrie propre à ce mod, si bien qu'un lac ou une flèche n'aura pas exactement le même aspect. Les listes de structures pondérées, les tables de rotation et de miroir, les listes de blocs ignorés et l'effilement des stalagmites passent toutes. L'effilement est reproduit par la forme plutôt que par la formule, si bien que le contour d'une flèche est proche mais pas identique.

Traduire les fichiers en un pack est la voie prise en charge, et le seul moyen de modifier ce qu'ils génèrent.

## Lost Cities

*autres mods*

Lost Cities remplace le générateur de l'overworld par le sien, si bien que tout ce qui est branché sur le générateur ordinaire cesserait de fonctionner dans ses mondes. Une compatibilité qui ne se charge que lorsque Lost Cities est installé reprend trois choses :

- `generatorOptions` façonne le terrain entre les villes et sous elles. Lost Cities ne lit que les réglages de bruit, si bien que le niveau du sol, le niveau de l'eau, les grottes, les lacs et les interrupteurs de structures proviennent de ses propres profils, et `seaLevel` ne fait rien dans ses mondes ; le résumé du journal le dit. `terrainWorldTypes` le conditionne comme n'importe quel autre type, reconnu sous le nom `lostcities`.
- Un monde du vide fonctionne, y compris celui qu'amène une liste de biomes entièrement bloquée. Les villes et le terrain disparaissent tous deux, et la plateforme et le point d'apparition se comportent comme partout ailleurs.
- Le `stoneBlock` d'un biome de pack remplace la pierre dessous, sur tous les types de paysage qu'a Lost Cities, normal, flottant, spatial et caverne.

Les villes elles-mêmes ne sont pas à ce mod de les modifier. Leur taille et leur fréquence, les matériaux des bâtiments, le niveau du sol et de l'eau, tout cela se trouve dans les propres fichiers de profil de Lost Cities sous `config/lostcities`, et son JSON de bâtiments passe par son propre réglage `assets` au même endroit. Un pack qui fournit un monde Lost Cities fournit ces fichiers avec lui, de la même façon qu'il fournit la configuration de n'importe quel autre mod.

`worldType` réglé sur `lostcities` fait de chaque nouveau monde un monde Lost Cities, de la même façon qu'il en fait un monde `biomesop` ou `realistic`. Forcer un type abandonne les réglages que le monde aurait portés, si bien que le monde se retrouve sur le profil par défaut de Lost Cities, et `defaultProfile` dans `config/lostcities/general.cfg` nomme lequel c'est. Dans l'autre sens, un pack qui force un type différent retire Lost Cities à un joueur qui l'avait choisi, si bien qu'un pack qui veut laisser ce choix ouvert ajoute `lostcities` à `worldTypeExceptions`.

Tout le reste n'est jamais passé par le générateur et fonctionne comme partout ailleurs : le worldgen de pack, le blocage des minerais et des biomes, l'espacement des structures et les générateurs de monstres, la bedrock plate, le retrogen, la prégénération, et ses deux tables de butin de coffre se surchargent et reçoivent des ajouts comme les autres.

## Intégration de Blast Plaster

*autres mods*

`<namespace>/blastplaster/*.json`

Le nom du fichier est à votre choix, seul le dossier est lu, et plusieurs fichiers s'empilent.

Blast Plaster (une dépendance de ce mod) gère le comportement après explosion : réparation des cratères bloc par bloc, abattage des arbres en tenant compte de leur forme, contrôle des butins. Seul, il lit une configuration globale unique. Piloté par un pack, il répond **dimension par dimension**, et le pack fournit la décision au lieu de demander aux joueurs de modifier une configuration. L'abattage des arbres de village réutilise aussi sa géométrie d'arbre, ce qui explique pourquoi un arbre au-dessus d'une nouvelle route tombe en entier. Sans fichiers de pack, Blast Plaster se comporte exactement comme s'il était installé seul.

Les clés écrites tout en haut du fichier s'appliquent partout ; un bloc `dimensions` les surcharge pour une dimension par son identifiant. Tout ce qu'un pack ne nomme jamais garde ce que dit la configuration propre à Blast Plaster, si bien qu'un pack règle les quelques-unes qui l'intéressent et laisse le reste tranquille.

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
  "blockConversions": ["minecraft:stone=minecraft:cobblestone@0.75", "#logWood=minecraft:log:0@0.5"],
  "dimensions": {
    "-1": { "explosionMode": "HEAL", "minimumTicksBeforeHeal": 200 },
    "1": { "enableExplosionSmoke": false }
  }
}
```

`explosionMode` est l'interrupteur principal : `HEAL` restaure le cratère avec le temps, `EJECT_DROPS` laisse le trou et fait tomber environ un tiers des blocs (comportement vanilla), `VISUAL_TOSS` laisse le trou et ne fait rien tomber. Piloté par un pack, la valeur par défaut est `EJECT_DROPS` (et non le `HEAL` de Blast Plaster), si bien qu'une installation non configurée se comporte comme vanilla.

| Clé | Valeur | Ce qu'elle fait |
| --- | --- | --- |
| `explosionMode` | `HEAL`, `EJECT_DROPS`, `VISUAL_TOSS` | Ce qui se passe après la détonation |
| `healCreepers`, `healNonPlayerTNT`, `healWither`, `healAll` | true ou false | Quelles explosions sont prises en charge |
| `processPlayerIgnitedTNT` | true ou false | Si le TNT allumé par un joueur est traité avec le reste |
| `customEntitiesToHeal` | liste de noms d'entités | Les explosions d'autres mods, nommées `modid:entité` |
| `healFullTrees` | true ou false | Un arbre entamé par une explosion est enlevé ou restauré en entier, plutôt que cisaillé |
| `maxTreeSize` | nombre | Le plus grand nombre de blocs qu'un arbre peut revendiquer avant d'être laissé tranquille |
| `minimumTicksBeforeHeal`, `randomTickVar` | nombres | Combien de temps avant que la réparation commence, et à quel point son rythme est irrégulier |
| `overrideBlocks` | true ou false | Si la réparation écrase ce qui a été construit depuis dans le trou |
| `enableFakeTossedBlocks` | true ou false | Les débris projetés par l'explosion |
| `enableExplosionFlash` | true ou false | L'éclair lumineux au moment de l'explosion |
| `explosionFlashDuration`, `explosionFlashLightLevel`, `explosionFlashParticleCount`, `explosionFlashPulses` | nombres | Combien de temps dure l'éclair, son intensité, combien de particules il projette et combien de fois il pulse |
| `enableExplosionSmoke` | true ou false | La colonne de fumée qui suit |
| `explosionSmokeDuration`, `explosionSmokeParticleCount` | nombres | Combien de temps la fumée persiste et son épaisseur |
| `playerTNTAlwaysDrops`, `playerTNTDropFullBlocks` | true ou false | Ce que laisse derrière lui le TNT d'un joueur |
| `enableDropSuppression`, `dtSpecialDrops` | true ou false | Les butins à l'intérieur d'une explosion, et ceux propres à Dynamic Trees |
| `preventMobDrops` | true ou false | Si les mobs tués par une explosion lâchent encore leur butin |
| `blockConversions` | liste de règles | En quoi se transforme un bloc soufflé au lieu de revenir tel quel, si bien qu'une construction s'use d'un cran par explosion |

`blockConversions` décide en quoi se transforme un bloc soufflé au lieu de revenir tel qu'il était. Une règle se lit `<source>=<résultat>[@chance]` : la source est un identifiant de bloc, un identifiant de bloc avec une méta (`minecraft:log:1`), ou un nom du dictionnaire des minerais précédé d'un `#` ; le résultat est un identifiant de bloc, un identifiant de bloc avec une méta, ou `nothing` pour laisser l'espace vide ; la chance va de 0.0 à 1.0 et vaut 1.0 par défaut. La première règle qui correspond l'emporte, si bien que les règles spécifiques vont au-dessus des règles générales, et un bloc qui est déjà le résultat d'une règle n'est jamais converti à nouveau — un mur perd un cran par explosion plutôt que de s'user jusqu'à disparaître.

**Apparence entièrement vanilla :** `EJECT_DROPS` plus `healFullTrees`, `enableFakeTossedBlocks`, `enableExplosionFlash`, `enableExplosionSmoke`, `preventMobDrops` et `playerTNTAlwaysDrops` tous désactivés. Chaque clé peut être définie par dimension.

**Les clients vanilla** ne voient rien d'inhabituel. L'éclair est la seule fonctionnalité qui place un bloc, si bien qu'avec `vanillaClients` réglé il est forcé à off ; tout le reste n'est que particules et objets qu'un client ordinaire comprend.

Ne sont pas des clés de pack : la journalisation de débogage de Blast Plaster et son association bûche-feuilles (l'identification des arbres doit avoir une seule réponse pour tout le jeu). Les deux restent dans la configuration propre à Blast Plaster.

## Mods de tombes

*autres mods*

Aucune configuration nécessaire. Les objets de `player_loot` rejoignent les butins ordinaires de la mort avant qu'un mod de tombes ne les lise, si bien qu'ils finissent dans la tombe avec l'inventaire — cela fonctionne avec Gravestone, GraveStone Mod, Corail Tombstone et tout autre mod qui lit la liste des butins de la mort. Par entrée, `dropLoose` contourne la liste des butins pour que les objets restent au sol pour le tueur au lieu d'aller dans la tombe. Les clés et la mise en garde sur `dropLoose` : [Butin des joueurs](#butin-des-joueurs).

---

# Référence

## Listes de valeurs

*référence*

Ce sont les noms que l'analyseur accepte partout où les tableaux ci-dessus parlent de « l'un des matériaux », et ainsi de suite. Tout ce qui n'est pas reconnu est consigné dans le journal et remplacé par la valeur par défaut.

### Noms acceptés

*listes de valeurs*

**Matériaux de bloc.** `air`, `grass`, `ground`, `wood`, `rock`, `iron`, `anvil`, `water`, `lava`, `leaves`, `plants`, `vine`, `sponge`, `cloth`, `fire`, `sand`, `circuits`, `carpet`, `glass`, `redstone_light`, `tnt`, `coral`, `ice`, `packed_ice`, `snow`, `crafted_snow`, `cactus`, `clay`, `gourd`, `dragon_egg`, `portal`, `cake`, `web`, `piston`, `barrier`, `structure_void`.

**Types de son.** `wood`, `ground`, `plant`, `stone`, `metal`, `glass`, `cloth`, `sand`, `snow`, `ladder`, `anvil`, `slime`.

**Couleurs de carte.** `air`, `grass`, `sand`, `cloth`, `tnt`, `ice`, `iron`, `foliage`, `snow`, `clay`, `dirt`, `stone`, `water`, `wood`, `quartz`, `adobe`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `silver`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, `black`, `gold`, `diamond`, `lapis`, `emerald`, `obsidian`, `netherrack`.

**Couches de rendu.** `solid`, `cutout`, `cutout_mipped`, `translucent`. Laissée vide, le bloc en choisit une adaptée à son type.

**Raretés.** `common`, `uncommon`, `rare`, `epic`.

**Particules de torche.** `none`, `flame`, `colored`. `colored` utilise `particleColor`.

**Classes d'outils.** `pickaxe`, `axe`, `shovel`, `sword`.

**Emplacements d'armure.** `head` ou `helmet`, `chest` ou `chestplate`, `legs` ou `leggings`, `feet` ou `boots`.

**Teintes.** `biome`, `none`, ou une couleur hexadécimale à six chiffres. Les couleurs, partout dans une définition, sont en hexadécimal, avec ou sans `#` initial.

**Comportements** pour `behavesAs`. `till`, `path`, `bush`, `animals`.

**Structures** pour un modèle de monde, et pour les listes propres au groupe `structures`. `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges`, `endcities`, `caves`, `ravines`, et `reccomplex`, qui désactive tout ce que Recurrent Complex génère de lui-même — ses structures naturelles et ses substituts de décoration — en laissant intact ce qui se dresse déjà dans le monde. Huit autres nomment ce que place l'étape de peuplement plutôt qu'un générateur de structure : `dungeons`, `waterlakes`, `lavalakes`, `netherlava`, `fire`, `glowstone`, `ice` et `animals`.

**Types de créatures** pour les apparitions et les taux des biomes. `creature`, `monster`, `ambient`, `water_creature`.

**Rôles** pour les `roles` d'un modèle de monde. `ocean`, `river`, `beach`, `mushroom`, `swamp`, `hills`, `mountain`, `jungle`, `forest`, `savanna`, `sandy`, `mesa`, `snowy`, `wasteland`, `plains`, `water`. Chacun désigne un biome qui remplit ce rôle une fois que le blocage a retiré ceux qui l'auraient fait.

**Types de minerai** pour `oreTypes`. `COAL`, `IRON`, `GOLD`, `REDSTONE`, `DIAMOND`, `LAPIS`, `EMERALD`, `QUARTZ`, `DIRT`, `GRAVEL`, `DIORITE`, `GRANITE`, `ANDESITE`, `SILVERFISH`, `CUSTOM`.

### Réglages du monde

*listes de valeurs*

Les clés `terrain` ci-dessous, réunies dans le bloc `settings` d'un modèle de monde :

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldName": "Ruby World",
    "worldSpawn": "0,72,0",
    "worldBorder": 4096,
    "worldTime": 6000,
    "weatherCeiling": ["0=128"],
    "cloudHeight": ["0=384"]
  }
}
```

| Réglage | Type | Défaut | Ce qu'il fait |
| --- | --- | --- | --- |
| `worldName` | chaîne | vide | Préremplit la zone de nom de l'écran de création de monde, et le dossier de sauvegarde en découle. Il ne remplit la zone que tant qu'elle contient encore le nom par défaut du jeu et, contrairement à la seed et au mode de jeu, il n'est pas réappliqué ensuite |
| `worldSpawn` | `x,z` ou `x,y,z` | vide | L'endroit où apparaît chaque nouveau monde, appliqué à la création seulement. Sans y, la surface au niveau du sol du type de monde est utilisée |
| `worldBorder` | entier, blocs | `0` | Le diamètre de bordure donné à chaque nouveau monde, le chiffre que prend `/worldborder set`. `0` laisse la bordure tranquille |
| `worldTime` | entier, ticks | `-1` | L'heure du jour à laquelle démarre chaque nouveau monde. `-1` la laisse tranquille |
| `weatherCeiling` | liste de `dimension=y` | aucun | Le y le plus élevé qu'atteignent la pluie et la neige. Un nombre seul couvre toutes les dimensions |
| `cloudHeight` | liste de `dimension=y` | aucun | Le y auquel les nuages sont dessinés. Un nombre seul couvre toutes les dimensions, et vide conserve la hauteur propre au jeu |

**`worldName`** (groupe `terrain`) préremplit la zone de nom de l'écran de création de monde ; le dossier de sauvegarde en découle comme d'habitude. Il ne remplit la zone que tant qu'elle contient encore le nom par défaut du jeu, si bien qu'un nom saisi par un joueur n'est jamais écrasé, et, contrairement à la seed et au mode de jeu, il n'est pas réappliqué ensuite — ce que contient la zone à la création est le nom.

**`worldSpawn`** (groupe `terrain`) : `x,z` ou `x,y,z`. Appliqué à la création seulement. Sans y, la surface au niveau du sol du type de monde est utilisée. Les entrées non entières sont signalées et ignorées. Pertinent surtout en superplat : la recherche de point d'apparition de vanilla cherche de l'herbe au niveau de la mer, n'en trouve jamais sur une pile de couches, et peut errer sur des centaines de blocs — `worldSpawn` la fixe.

**`worldBorder`** (groupe `terrain`) : diamètre de la bordure en blocs, le chiffre que prend `/worldborder set`. Appliqué à la création ; `0` (par défaut) laisse la bordure tranquille ; elle peut encore être déplacée par commande ensuite. `worldBorderLimit` dans la configuration plafonne ce qu'un pack peut demander — un pack qui en demande plus est refusé et consigné, pas ramené à la limite, si bien qu'un pack ne peut pas imposer à un serveur une bordure que l'opérateur n'a pas acceptée.

**`worldTime`** (groupe `terrain`) : une valeur en ticks comme la prend `/time set` (`18000` minuit, `6000` midi). Verrouille l'horloge de l'overworld ; tout ce qui lit l'heure du jour (apparition des mobs, sommeil) voit la valeur verrouillée. `-1` (par défaut) laisse le temps s'écouler. L'équivalent pour l'overworld du `fixedTime` d'une dimension personnalisée, et indépendant de `doDaylightCycle`.

**`cloudHeight`** (groupe `terrain`) : le y auquel les nuages sont dessinés. Un nombre seul couvre toutes les dimensions ; des lignes `dimension=y` (`0=384`) surchargent dimension par dimension. C'est ce que règle un pack aux grands bâtiments pour que l'horizon se dresse sous les nuages plutôt qu'à travers eux, et sur un monde rubic c'est un y absolu, si bien qu'un plafond relevé est l'endroit au-dessus duquel placer les nuages. Vide (par défaut), la hauteur propre au jeu est conservée, 128 dans l'overworld, décalée vers le haut avec `terrainOffset` sur un monde rubic.

**`weatherCeiling`** (groupe `terrain`) : le y le plus élevé qu'atteignent la pluie et la neige. Un nombre seul couvre toutes les dimensions ; des lignes `dimension=y` (`0=128`) surchargent dimension par dimension. Au-dessus, la pluie ne tombe pas, la neige ne se dépose pas, les chaudrons ne se remplissent pas, la foudre ne frappe pas et aucune précipitation n'est dessinée ; en dessous, la météo est inchangée. Vide (par défaut), il n'y a aucun plafond. La glace dépend de la température plutôt que des précipitations, si bien qu'elle se forme encore au-dessus de la ligne.

### Physique du monde

*listes de valeurs*

**Physique du monde** — quatre clés `terrain`, chacune un multiplicateur de vanilla (`1.0` = inchangé), chacune prenant une valeur seule pour toutes les dimensions ou des surcharges `dimension=valeur` :

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldGravity": ["0.17", "0=1.0"],
    "worldFallDamage": ["0.17"],
    "worldJumpStrength": ["1.0"],
    "worldTerminalVelocity": ["1.0"]
  }
}
```

| Réglage | Met à l'échelle | Remarques |
| --- | --- | --- |
| `worldGravity` | L'accélération de chute des joueurs, mobs, objets jetés, blocs en chute, flèches, entités lancées, TNT et orbes d'XP | `0.17` est proche de la Lune ; les arcs de saut et les portées des projectiles suivent automatiquement |
| `worldFallDamage` | Les dégâts de chute | Une dimension à faible gravité veut généralement que ce réglage soit assorti |
| `worldJumpStrength` | La vitesse de saut | Appliqué par-dessus le changement de gravité |
| `worldTerminalVelocity` | La vitesse de chute maximale, en proportion du plafond vanilla | Le vol à l'élytre n'est pas touché |

Les quatre vides (par défaut) conservent la physique vanilla. Sur les dimensions de Galacticraft, la clé de gravité met à l'échelle la gravité propre à Galacticraft.

### Raccords du monde

*listes de valeurs*

**Raccords du monde** — empilent les dimensions à la verticale : quitter un monde par son plancher ou son plafond dépose l'entité dans la dimension située en dessous ou au-dessus, aux mêmes x et z.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldBelow": ["0=-1"],
    "worldAbove": ["-1=0"],
    "worldSeamEntities": true,
    "worldSeamBedrock": false
  }
}
```

| Paramètre | Valeur | Défaut | Effet |
| --- | --- | --- | --- |
| `worldBelow` | lignes `dimension=cible`, ou un id seul pour toutes les dimensions | aucun | Dimension où l'on arrive en tombant sous le plancher du monde |
| `worldAbove` | idem | aucun | Dimension où l'on arrive en montant au-delà du sommet généré, c'est-à-dire le toit du Nether et non sa limite de construction |
| `worldSeamEntities` | booléen | `true` | Indique si les objets, mobs et autres entités traversent, ou seulement les joueurs |
| `worldSeamBedrock` | booléen | `false` | Conserve la bedrock à la limite d'un raccord. Désactivé, la limite n'en génère aucune, et le passage peut donc être creusé |

Si les deux listes sont vides (le défaut), tous les mondes restent fermés. La couche de blocs la plus externe d'un monde sert de porte : entrer dans la couche du bas vous emmène vers le bas, entrer dans la couche du haut vous ramène vers le haut. Les arrivées se posent à l'écart de celle-ci, trois couches à l'intérieur en descendant et une seule en montant, pour que rien ne rebondisse aussitôt. En descendant, les couches au-dessus du point d'arrivée sont aussi dégagées jusqu'à la porte, de sorte que l'entrée reste visible d'en bas et serve de chemin de retour.

Cassez un bloc dans une couche de porte et le monde de l'autre côté apparaît à travers : le ciel de la dimension du dessous se montre sous le plancher, et celui de la dimension du dessus au-dessus du plafond. Cet effet n'est dessiné que côté client, dans la distance d'affichage, et ne change rien au monde lui-même. L'élan se conserve à la traversée.

Les traversées d'un joueur sont mémorisées. Descendre marque le trou, et remonter près de lui vous dépose là où ce trou vous avait placé la dernière fois, si bien qu'un puits que vous empruntez souvent vous ramène toujours au même endroit connu plutôt qu'à un endroit nouveau. Le premier retour détermine l'atterrissage : cet endroit s'il a un sol dessous, sinon la place debout la plus proche en s'éloignant du raccord d'une hauteur à la fois, et sinon une niche creusée dans le bord juste à côté du trou, puisqu'un puits creusé droit vers le bas n'a pas encore de rebord. Traverser vers le haut à un endroit où aucun de vos trous ne se trouve à proximité crée simplement un nouvel atterrissage. Arriver par le bas sans aucun emplacement praticable à proximité ramène à la surface de cette colonne. Les pieds et la tête sont dégagés si l'endroit est dans la roche, en cassant ces blocs normalement pour qu'ils lâchent leur contenu, conteneurs compris.

Les chaînes s'empilent en donnant à chaque dimension ses propres lignes, et les cavaliers et leurs montures traversent séparément.

Les portails s'appliquent aux joueurs. Un joueur qui n'a pas débloqué la cible reçoit le message de refus du portail et est replacé sur le dernier sol où il se tenait, ou sur une corniche près du raccord ; les raccords ne posent aucun bloc, donc un puits verrouillé ne peut pas être exploité en y tombant. Les objets et les mobs n'ont pas de portail propre : avec `worldSeamEntities` activé, ils traversent quel que soit celui qui les a perdus, et désactivé, ils tombent au-delà d'un plancher ouvert et sont perdus comme dans n'importe quel trou. `worldSeamBedrock` scelle le plancher à la place, et un pack qui garde sa bedrock fournit lui-même le passage, généralement une [surcharge de propriétés](#surcharges-de-propriétés) donnant à `minecraft:bedrock` une `hardness` positive. Les chunks générés avant le raccord conservent la bedrock qu'ils ont déjà.

**Mondes Rubic** — `rubicWorld`, `worldMinHeight`, `worldMaxHeight`, `rubicWorldDimensions`, `rubicWorldDimensionsAreBlacklist` et `terrainOffset` sont aussi des clés de `terrain` : voir [Mondes Rubic](#mondes-rubic).

## Liste des dossiers

*référence*

Chaque dossier, avec son chemin complet et un lien vers la section qui le décrit, se trouve dans [Où vont les fichiers](#où-vont-les-fichiers).

## Commandes

*référence*

### Vos propres commandes

*commandes*

`/rdpl` s'exécute sur votre propre machine et ne demande aucune permission, car tout ce qu'elle touche vous appartient. Un rechargement réanalyse le dossier qui est le vôtre, réapplique vos [surcharges de propriétés](#surcharges-de-propriétés) à votre propre copie des blocs et des objets, et actualise vos propres ressources ; il n'atteint aucun serveur, donc la copie du serveur se recharge avec `/rdplserver reload`. En solo, les deux ne font qu'une seule machine, si bien que `/rdpl reload` recharge aussi les tables de butin, les progrès et les fonctions du serveur intégré, comme le rechargement propre à vanilla. Elle fonctionne sur n'importe quel serveur, qu'il ait le mod ou non.

| Commande | Niveau | Effet |
| --- | --- | --- |
| `/rdpl list` | aucun | Chaque pack chargé, sa priorité et son contenu. Cliquez sur un pack pour chercher un fichier dedans |
| `/rdpl which <namespace:path>` | aucun | Quel pack fournit un fichier donné, et quels packs il masque |
| `/rdpl reload` | aucun | Réanalyse le dossier et recharge tout |
| `/rdpl reload <group>` | aucun | Ne recharge qu'un seul type : `textures`, `models`, `languages`, `sounds` ou `shaders` |
| `/rdpl unused` | aucun | Fichiers de vos packs que rien n'a encore demandés, souvent une faute de frappe dans un chemin |
| `/rdpl config unused` | aucun | Fichiers d'options dans `rdploader/config` qu'aucun pack installé ne définit plus |
| `/rdpl config prune` | aucun | Supprime ces fichiers |
| `/rdpl pixelmap <namespace:path>` | aucun | Ce qu'une [carte de pixels](#textures-écrites-sous-forme-de-cartes-de-pixels) a donné, caractère par caractère |
| `/rdpl biome list` | aucun | Chaque biome pouvant être généré, et son id |
| `/rdpl biome here` | aucun | Le biome dans lequel vous vous trouvez |
| `/rdpl biome find <name>` | celui du serveur | Liée. Transmise à `/rdplserver biome find`, le seul côté à connaître la seed |
| `/rdpl team` | aucun | Les camps qu'un pack a mis en place, chacun dans sa couleur, celui dans lequel vous êtes, et qui dirige chacun |
| `/rdpl team join [name]` | aucun | Rejoindre un camp. Seuls les camps que le pack laisse ouverts sont proposés ; un camp de mobs n'est pas un camp que l'on peut rejoindre. Sans nom, vous êtes placé dans le camp qui a le moins de joueurs parmi ceux qui acceptent des joueurs par `balance` |
| `/rdpl team leave` | aucun | Quitter le camp dans lequel vous êtes |
| `/rdpl team vote <player>` | aucun | Voter pour celui qui dirige votre camp, lorsque le pack choisit son chef par un vote. Une égalité ne laisse personne à la tête |
| `/rdpl team claim` | aucun | Prendre la direction de votre camp, lorsque le pack permet de la revendiquer et que personne dans le camp ne la détient |
| `/rdpl round start` | aucun | Lance la manche, lorsque le pack la retient dans un hall (`opens.by`). Pour le chef d'un camp, ou un opérateur |
| `/rdpl round reset` | aucun | Réinitialise la manche en cours, ou soumet cette réinitialisation à un vote, selon le `reset` du pack. Pour le chef d'un camp, un joueur d'un camp que le pack autorise à lancer un vote, ou un opérateur |
| `/rdpl round vote yes`, `no` | aucun | Voter dans un vote en cours pour réinitialiser la manche. Pour un joueur dans un camp |
| `/rdpl oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro`, `goto`, `vein` | celui du serveur | Liées. Transmises mot pour mot à `/rdplserver`, qui décide ; voir le tableau ci-dessous |

**Quelles sous-commandes serveur sont liées, et pourquoi les autres ne le sont pas.** Une sous-commande serveur reçoit une transmission exactement lorsque le client n'a aucun sens propre pour ce nom : `oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro`, `goto`, `vein` et `team` ne peuvent signifier que ce que veut le serveur, donc `/rdpl` les lui confie. Les six que le client possède aussi, `reload`, `list`, `which`, `unused`, `config` et `biome`, gardent leur propre sens, celui de vos packs et de votre client, et les transmettre le leur ôterait. `biome find` est de toute façon la seule partie d'un nom partagé qui revient au serveur, puisque lui seul connaît la seed du monde ; cette seule forme est donc transmise, tandis que `biome list` et `biome here` restent chez vous. Cela règle aussi la question des permissions : c'est la vérification d'opérateur du serveur qui tranche, et un client ne peut ni la contourner ni se voir donner une réponse fabriquée.

**`/rdpl` atteint aussi la commande serveur.** Tout ce que `/rdpl` ne traite pas lui-même, `oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro`, `goto`, `vein` et `team`, est transmis tel quel à `/rdplserver` et proposé dans la complétion par tabulation, de sorte qu'une seule commande suffit à taper en solo. Elle est transmise mot pour mot et le serveur décide comme toujours, permissions comprises : taper le nom plus court n'ouvre donc rien. Les sous-commandes que les deux possèdent, `reload`, `list`, `which`, `unused`, `biome` et `config`, restent à `/rdpl` et désignent les packs propres au client. `biome find` est la seule exception au sein d'un nom partagé : seul le serveur connaît la seed du monde, donc cette forme est transmise tandis que `biome list` et `biome here` répondent depuis votre propre client.

**Édition au quotidien :** `/rdpl reload textures` est bien plus rapide que F3+T dans un gros modpack. F3+T fonctionne toujours et recharge tout. Utilisez `/rdpl reload` seul lorsque vous *ajoutez* ou *supprimez* un fichier, car cela change le contenu du dossier.

### Commandes serveur

*commandes serveur*

Sur un serveur dédié, `/rdplserver` fait la même chose pour la copie du dossier propre au serveur. La colonne Niveau est le niveau de permission dont un émetteur a besoin : `3` est un opérateur, `2` admet aussi les blocs de commande, `0` est n'importe quel joueur, et `4` est au-dessus de l'opérateur et n'atteint personne. Seules `intro`, `team`, `card` et les trois formes de `goto` sont ouvertes en dessous du niveau opérateur, et `goto` est la seule qu'un pack peut déplacer.

#### Packs et fichiers

*commandes serveur*

| Commande | Niveau | Effet |
| --- | --- | --- |
| `/rdplserver reload` | 3 | Réanalyse le dossier du serveur et recharge tout |
| `/rdplserver list` | 3 | Chaque pack que le serveur a chargé, sa priorité et son contenu |
| `/rdplserver which <namespace:path>` | 3 | Quel pack fournit un fichier donné, et quels packs il masque |
| `/rdplserver unused` | 3 | Fichiers des packs du serveur que rien n'a demandés |
| `/rdplserver config unused` | 3 | Fichiers d'options dans `rdploader/config` qu'aucun pack installé ne définit plus |
| `/rdplserver config prune` | 3 | Supprime ces fichiers |

#### Monde et génération

*commandes serveur*

| Commande | Niveau | Effet |
| --- | --- | --- |
| `/rdplserver oregen` | 3 | Totaux cumulés de la génération de minerais bloquée, par mod et par type |
| `/rdplserver generators` | 3 | Totaux cumulés des générateurs de monde bloqués, par mod et par type |
| `/rdplserver biome` | 3 | Chaque biome pouvant être généré sur le serveur |
| `/rdplserver biome list [all]` | 3 | La même chose avec l'id de chaque biome ; `all` inclut ceux que rien ne peut générer |
| `/rdplserver biome here` | 3 | Le biome dans lequel vous vous trouvez. La console ne se trouve nulle part, donc depuis elle la commande demande un joueur à la place |
| `/rdplserver biome here <player>` | 3 | Le biome dans lequel ce joueur se trouve, forme que veulent la console et un script |
| `/rdplserver biome find <name>` | 3 | L'endroit le plus proche où un biome se génère, sans générer de chunks pour le chercher |
| `/rdplserver dimensions` | 3 | Chaque dimension, y compris celles ajoutées par des packs |
| `/rdplserver vein <entry> [radius]` | 3 | Où une entrée de worldgen de type `vein` a ses filons semés dans ce nombre de chunks (8 par défaut) autour de l'endroit où elle est lancée, du plus proche au plus lointain, que ces chunks existent déjà ou non. `/rdpl vein` y est transmise |

#### Commandes de portail

*commandes serveur*

| Commande | Niveau | Effet |
| --- | --- | --- |
| `/rdplserver gate list` | 3 | Chaque portail et s'il est ouvert |
| `/rdplserver gate check <player>` | 3 | Quels portails un joueur a franchis |
| `/rdplserver gate grant <player> <gate>` | 3 | Ouvre un portail pour un joueur |
| `/rdplserver gate revoke <player> <gate>` | 3 | Le referme |

#### Commandes de prégénération

*commandes serveur*

| Commande | Niveau | Effet |
| --- | --- | --- |
| `/rdplserver pregen <radius>` | 3 | Génère chaque chunk dans ce nombre de chunks autour de l'endroit où elle est lancée. Voir [Prégénération](#prégénération) |
| `/rdplserver pregen <radius> relight` | 3 | Ne lance que la passe d'éclairage sur les terres qui existent déjà |
| `/rdplserver pregen status` | 3 | Où en est une exécution |
| `/rdplserver pregen stop` | 3 | Y met fin |

#### Joueurs, équipes et manches

*commandes serveur*

| Commande | Niveau | Effet |
| --- | --- | --- |
| `/rdplserver intro` | 0 | Rejoue l'intro du monde à votre prochaine connexion. N'importe quel joueur peut l'exécuter, et elle ne réinitialise jamais que la sienne |
| `/rdplserver team`, `team join [name]`, `team leave`, `team vote <player>`, `team claim` | 0 | Identiques aux formes `/rdpl team` ci-dessus, qui leur sont transmises |
| `/rdplserver round start` | 0 | Identique à `/rdpl round start`, qui lui est transmise |
| `/rdplserver round reset`, `round vote yes`, `round vote no` | 0 | Identiques aux formes `/rdpl round` ci-dessus, qui leur sont transmises |
| `/rdplserver card <rule> [players]` | 2 | Affiche une [règle de carte](#cartes) aux joueurs nommés, ou à vous-même, par son id ou son nom de fichier. `when`, `repeat` et `cooldown` sont ignorés |
| `/rdplserver reset` | 3 | Remet la carte dans l'état où la laisse la fin d'une manche : tout le monde est retenu, les entités balayées, les scores effacés, les `resetRuns` exécutés, les joueurs placés à `resetSendsTo` puis libérés, et une manche s'ouvre avec le décompte de départ, comme le décrivent les paramètres de réinitialisation sous [Prégénération](#prégénération). Non transmise depuis `/rdpl` |

#### Se rendre quelque part

*commandes serveur*

| Commande | Niveau | Effet |
| --- | --- | --- |
| `/rdplserver goto <structure>` | `gotoLevel`, `3` | Vous emmène à la plus proche où personne n'est encore allé, en cherchant sans générer le terrain en chemin |
| `/rdplserver goto <structure> next` | `gotoNextLevel`, `3` | Vous emmène plus loin vers la plus proche où l'on ne vous a pas encore conduit durant cette session, qu'elle ait déjà été visitée ou non |
| `/rdplserver goto <structure> back` | `gotoBackLevel`, `3` | Vous emmène à celle d'avant, en revenant en arrière parmi les lieux où cette session vous a envoyé |

### Qui peut utiliser goto

*commandes*

**Ouvrir `goto`.** Chaque partie de `/rdplserver` demande un opérateur, niveau 3, sauf `intro` et `team`, qui sont des commandes propres au joueur et toujours de niveau 0, et `card`, de niveau 2 pour qu'un bloc de commande puisse afficher une carte. Les trois formes de `goto` sont la seule chose qu'un pack décide : chacune porte un niveau de permission qui lui est propre, qu'un pack ou la config peut abaisser, indépendamment des deux autres et du reste de la commande.

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

La valeur est le niveau de permission dont un émetteur a besoin. `3` (opérateur) est le défaut. `2` admet aussi les blocs de commande, si bien qu'un pack peut placer un saut sur un bouton ou une plaque de pression sans exposer le reste de `/rdplserver`. `0` l'ouvre à n'importe quel joueur. Les trois paramètres sont indépendants : par exemple, `next` ouvert aux blocs de commande pour une visite de villages pendant que `back` reste réservé aux opérateurs.

Comme `intro` est ouverte à tous, n'importe quel joueur accède à `/rdplserver` lui-même ; chaque autre sous-commande vérifie donc d'elle-même la qualité d'opérateur et refuse avec un message. La complétion par tabulation suit : un non-opérateur se voit proposer `intro`, `team` là où un pack met en place un camp, et aussi `goto` dès qu'un niveau le lui permet.

`gotoPlaceLevels` remplace les trois paramètres pour des lieux précis, sous forme d'entrées `name=level`, comme dans l'exemple ci-dessus. Le nom est ce que vous taperiez après `goto` : un nom vanilla tel que `Village` ou `Mansion`, ou un nom enregistré avec `locateAs` sur une entrée `imprint`. La correspondance ignore la casse. Un niveau `4` est au-dessus de l'opérateur et ferme ce lieu à tout le monde — le moyen de cacher un lieu tandis que le reste de `goto` est ouvert.

Une entrée fixe un seul niveau pour les trois formes de ce lieu. Un lieu non listé retombe sur les trois paramètres ci-dessus, et un nom non enregistré ne correspond jamais.

La complétion par tabulation suit les mêmes règles : après `goto`, un émetteur ne se voit donc proposer que les lieux où il peut réellement être conduit.

Ceux-ci se trouvent dans le groupe `commands`, donc `control.commands` dans la config décide si un pack peut seulement les définir, et `off` y maintient tout au niveau opérateur quoi que demande un pack.

## Bon à savoir

*référence*

- CraftTweaker et GroovyScript s'exécutent après RDPL, leurs changements l'emportent donc toujours.
- Les recettes ne se chargent qu'au démarrage ; les modifier demande donc un redémarrage plutôt qu'un rechargement.
- Les fonctions enregistrées dans le dossier de données propre d'un monde l'emportent toujours sur une fonction d'un pack, tout comme les progrès propres à ce monde.
- Une structure déjà générée reste chargée jusqu'à ce que vous quittiez le monde.
- La casse des noms de fichiers compte. Si la capitalisation de votre fichier ne correspond pas à ce que le jeu a demandé, RDPL le charge quand même mais vous avertit, car sous Linux il ne serait pas trouvé du tout.
- Placez un `pack.png` dans `rdploader` pour donner une icône au pack. Sans lui, c'est l'icône de RDPL qui s'affiche.
- Le dossier peut être déplacé ou renommé avec l'option `rootDirectory` dans `config/mct_resourcedatapackloader_mixin.cfg`. Un chemin absolu fonctionne aussi, et cela demande un redémarrage.
- Les blockstates qui désignent un modèle vanilla nu héritent aussi des textures de vanilla. Les modèles parents tels que `cube_all` et `cross` prennent leurs textures dans le blockstate et ne posent pas de problème.
- `forge_marker: 1` ne gère pas le multipart ; les blockstates de lianes doivent donc être du multipart vanilla ordinaire, avec les textures intégrées au modèle.
- L'écran de chargement de Forge est dessiné dans des couleurs sombres, avec le logo de ce mod à la place de celui de Forge. `darkSplash` dans la catégorie de config `client` rétablit les couleurs propres à Forge ; les couleurs réglées à la main dans `config/splash.properties` ne sont touchées dans aucun cas, et cela demande un redémarrage.

## Quand quelque chose ne fonctionne pas

*référence*

**Consultez d'abord `logs/rdpl.log`.** Tout ce que fait RDPL y est écrit plutôt que dans le journal principal. Les progrès, tables de butin, recettes, fonctions, structures et chaque élément de contenu y sont consignés avec le pack d'où ils viennent, et tout ce qui est mal formé l'est avec la raison.

**Les textures et autres ressources sont un cas à part.** Elles sont demandées bien trop souvent pour être consignées une à une ; `/rdpl unused` liste donc les fichiers de vos packs que rien n'a demandés. Lancez-la une fois le jeu entièrement chargé. Un fichier au bon chemin est toujours demandé, donc tout ce qui est listé est le plus souvent une faute de frappe, mais gardez à l'esprit que certains fichiers ne se chargent qu'au moment du besoin, comme les langues autres que celle dans laquelle vous jouez.

**Un zip sans dossier `assets` à l'intérieur est ignoré,** tout comme n'importe quel dossier dans `rdploader`, et le journal le signale.

**`/rdpl which minecraft:textures/blocks/stone.png`** vous dit exactement quel pack fournit un fichier et ce qu'il masque.

## Bonus : ajustements vanilla

*référence*

Petits changements dans le comportement de vanilla, chacun activé dans la catégorie de config `tweaks`.

| Option | Défaut | Effet |
| --- | --- | --- |
| `promptLeafDecay` | activé | Les feuilles qui perdent leur arbre disparaissent en moins d'une seconde au lieu d'attendre les ticks aléatoires |
| `lenientPaths` | activé | Des chemins d'herbe peuvent être faits sous un bloc et restent en place quand on en pose un au-dessus |
| `unbreakableSpawners` | désactivé | Les générateurs de monstres ne peuvent être ni minés ni détruits par une explosion |
| `modernChestPlacement` | activé | Les coffres s'associent comme à partir de la 1.13 |

Trois autres se trouvent dans la catégorie `content` plutôt que `tweaks` :

| Option | Défaut | Effet |
| --- | --- | --- |
| `cactusMaxHeight` | `3` | Hauteur que atteint le cactus vanilla |
| `caneMaxHeight` | `3` | Hauteur qu'atteint la canne à sucre vanilla |
| `shovelPaths` | activé | Une pelle transforme en chemin les blocs marqués `behavesAs` path, et s'accroupir en annule un |

**Ceux-ci s'effacent devant Universal Tweaks**, qui modifie les mêmes blocs vanilla. Voir [Universal Tweaks](#universal-tweaks) pour savoir exactement quand.

**Rien de cela n'atteint un pack.** Ces options ne changent que le cactus, la canne, les feuilles et les chemins propres à Minecraft. Un bloc que votre pack définit avec `"type": "cane"` porte sa propre section `growth` et pousse à la hauteur que vous avez indiquée, quoi qu'il y ait d'autre d'installé. `lenientPaths` lève aussi la même restriction pour les blocs de pack utilisant `behavesAs`, ce qu'Universal Tweaks ne touche pas ; cette moitié reste donc active dans les deux cas.

### Générateurs incassables

*bonus : ajustements vanilla*

`unbreakableSpawners` donne au bloc générateur de monstres les valeurs de la bedrock, une dureté incassable et une résistance aux explosions que rien ne survit. Un joueur ne peut en miner aucun, quelle que soit la qualité de la pioche, et ni les creepers, ni la TNT, ni une entité de pack qui `explodes` n'en détruiront. Le mode Créatif les retire toujours, exactement comme il retire toujours la bedrock, afin qu'un auteur de pack ne soit jamais bloqué hors de sa propre construction. Cela demande un redémarrage, puisque les valeurs ne sont fixées qu'une fois, à la fin du chargement du jeu.

**C'est le bloc, pas le générateur.** Il n'existe aucun interrupteur par générateur. L'option modifie `minecraft:mob_spawner` lui-même, donc elle atteint d'un coup tous les générateurs du monde : les quatre structures vanilla qui en placent un, ceux que place un mod, et ceux que placent vos propres packs.

Ce dernier cas est la réponse pour une structure personnalisée. Un générateur dans l'un de vos modèles `.nbt`, placé par une entrée `imprint`, est un bloc générateur de monstres ordinaire portant sa propre tile entity, donc il est couvert dès que l'option est activée. Construisez la structure avec un générateur dedans de la manière habituelle, définissez ce qu'il fait apparaître dans les données de tile entity du modèle, activez `unbreakableSpawners`, et celui de votre donjon est aussi incassable que ceux de vanilla. Rien ne va dans le pack pour cela, et il n'y a aucun moyen de ne protéger que les vôtres en laissant cassables ceux du reste du monde.

### Placement des coffres

*bonus : ajustements vanilla*

`modernChestPlacement` place les coffres et les coffres piégés comme le font la 1.13 et les versions suivantes.

- Un coffre rejoint un coffre simple directement à sa gauche ou à sa droite, et seulement si les deux sont orientés dans le même sens. Un coffre placé devant ou derrière un autre ne le rejoint jamais.
- S'accroupir garde le nouveau coffre simple, sauf si l'on clique sur le côté d'un coffre simple : il rejoint alors ce coffre et se tourne dans le même sens.
- Un coffre peut se tenir à côté d'un coffre double, où il reste simple, ce qui permet une rangée de coffres le long d'un mur.

Chaque coffre se souvient de son partenaire, si bien que ce qui a été placé reste associé ou simple après un rechargement, qu'un entonnoir ou un tuyau ne remplit que le coffre qu'il touche, et que casser une moitié laisse l'autre simple. Les coffres placés avant l'activation de l'option, ou par la worldgen et les structures, s'associent comme la 1.12 l'a toujours fait. Un client sans RDPL dessine toujours deux coffres simples qui se touchent comme un seul coffre double, mais les ouvre séparément.

## Bonus : correctif des conflits de plugins JEI

*référence*

Certains mods interrogent le registre de recettes de JEI avant que les mods qui le fournissent aient fini de s'initialiser, ce qui inonde les journaux de centaines d'erreurs inoffensives mais bruyantes et peut casser silencieusement l'intégration JEI d'un mod. RDPL le détecte automatiquement et corrige l'ordre des notifications. Il fonctionne avec Just Enough Items et avec Had Enough Items. Si aucun des deux n'est installé, il ne se passe rien.

## Bonus : moins d'erreurs au démarrage

*référence*

- Les recettes qui référencent un objet qu'aucun mod n'a réellement enregistré, généralement du contenu désactivé dans la config d'un mod, sont ignorées au lieu de provoquer une erreur d'analyse. Le nombre est consigné une seule fois. (`skipMissingItems`)
- Les progrès qui récompensent une recette qu'un script a depuis supprimée se chargent quand même, au lieu d'échouer. Ils ne débloquent simplement jamais cette recette, et l'ensemble est résumé en une ligne. (`tolerateMissingInAdvancements`)

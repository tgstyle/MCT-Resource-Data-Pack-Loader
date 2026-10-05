# Resource Data Pack Loader

**Una carpeta que sobrescribe todo lo que aporta Minecraft o un mod, define contenido nuevo desde JSON y controla qué se genera, en cualquier mundo, en clientes y servidores, sin nada que los jugadores tengan que activar.**

Catorce ejemplos funcionales. Suelta cualquiera de ellos directamente en `rdploader` y fíjate en cómo está escrito cada archivo.

- [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) cubre la mayoría de las funciones, bloques, ítems, biomas, una dimensión, una plantilla de mundo y todas las formas de worldgen.
- [RDPLExampleOrePackVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleOrePackVoid.zip) convierte el mundo principal en un vacío con worldgen suspendido en el aire, una forma por franja de altura, de modo que cada una se ve bien por separado.
- [RDPLExampleVeinShapes.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleVeinShapes.zip) coloca tres vetas de mineral en un mundo principal corriente, una por patrón de veta (simple, con bandas y en tubo), cada una con niveles rico, normal y pobre y unos bloques marcadores en la superficie encima para prospectar.
- [RDPLExampleVeinShapesVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleVeinShapesVoid.zip) suspende los mismos tres patrones de veta en un vacío, para que cada forma se vea entera.
- [RDPLExampleDeepWorld.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleDeepWorld.zip) convierte el mundo principal en un mundo Rubic con 256 bloques de mundo generado por debajo del de vanilla y 128 por encima: la mezcla de piedra profunda, cuevas de ruido modernas, barrancos, vetas de mineral con bandas, tres regiones de cuevas por las que descender e islas flotantes en lo alto talladas por el mismo ruido.
- [RDPLExampleContainers.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleContainers.zip) añade bloques e ítems de mano que guardan un inventario, en todos los tamaños desde tres ranuras hasta el máximo permitido, con una tabla de botín, el modelo del cofre teñido a partir de la hoja de vanilla, todas las texturas dibujadas como mapas de píxeles y dos bolsas que se pueden llevar puestas en Baubles.
- [RDPLExampleMegaCity32.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleMegaCity32.zip) crea un mundo superplano con una única aldea deliberadamente enorme, crecida hasta mil parcelas y fijada en el origen, con las calles vestidas de carreteras de hormigón, aceras, centros discontinuos y farolas, alcantarillas bajo las calles, dos líneas de metro con sus estaciones debajo y un ferrocarril que cruza la ciudad, y los edificios compuestos a partir de mapas de estructuras en cuatro tamaños y tres fachadas en lugar de casas de vanilla.
- [RDPLExampleMegaCity64.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleMegaCity64.zip) es esa misma ciudad en un mundo Rubic con el techo a 512 y las nubes elevadas a 384, de modo que las torres se alzan 256 bloques sobre la calle, y cada distrito sortea una profundidad de bloque de 16, 32 o 64 para que una cuadrícula gruesa se mezcle con una fina.
- [RDPLExampleCityCustomMap.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleCityCustomMap.zip) dibuja esa misma ciudad a partir de un mapa de ciudad en lugar de sortearla: una cuadrícula de caracteres a 48 bloques por celda, con una paleta que nombra calles, plazas, callejones y selecciones ponderadas de edificios, de modo que el plano de bloques se traza a mano.
- [MCTKamikazeDemo.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/MCTKamikazeDemo.zip) enfrenta a cuatro facciones en una arena de lecho de roca bajo una noche permanente: cada bando es un equipo real del marcador de vanilla al que se unen sus mobs al aparecer, un bando puntúa por cada mob de otro bando que mata, una ronda termina con una tarjeta a los dos minutos y tres rondas forman una partida.
- [RDPLExampleRaid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleRaid.zip) crea un mundo plano alrededor de una aldea cuyo pozo es un pabellón con campana y le da al jugador un Mal presagio al unirse: cinco oleadas de illagers de vanilla, una bruja y los propios Saqueadores, Capitanes de asalto, Lanzahachas y Devastadores del pack, el presagio y Héroe de la aldea como efectos propios del pack con iconos de mapas de píxeles, una bebida que devuelve el presagio y las funciones que ponen fin al asalto.
- [RDPLExampleGalacticraft.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleGalacticraft.zip) requiere Galacticraft y coloca un sistema estelar propio en el mapa estelar, con un planeta al que se llega en un cohete de nivel 2 que tiene su propio cielo, gravedad, duración del día, clima y atmósfera, un planeta para contemplar pero en el que nunca se puede aterrizar y, con GalaxySpace, un planeta de hielo alrededor de Tau Ceti.
- [RDPLExampleGameHall.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleGameHall.zip) convierte un mundo plano en una sala de juegos: dados ponderados del pack, una baraja de mobs que se baraja sola y una baraja de la fortuna que queda vacía hasta que se baraja, un Cubilete que tira 2d6 con un clic derecho, un Duelo de dados por turnos contra la Casa que resuelve un empate con un sorteo, y ajedrez y damas jugados con mobs contra el ordenador.
- [RDPLExampleColony.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleColony.zip) construye solo un patio de trabajo al cerrar la introducción y pone a aldeanos a hacer todo tipo de órdenes de trabajo: un minero cuyo alcance crece con su pico, un leñador que se queda lo que tala, un granjero que mantiene una reserva fija de trigo y un porteador que acarrea roca, cada orden abierta con un cartel, una herramienta, una contratación o un cofre de reservas.
---

## Contenido

**Primeros pasos**
- [Qué es](#qué-es)
- [Dónde van los archivos](#dónde-van-los-archivos)
- [Cómo leer las tablas](#cómo-leer-las-tablas)
- [La única regla](#la-única-regla)
- [Organizar los packs](#organizar-los-packs)
- [Packs de recursos: quién gana](#packs-de-recursos-quién-gana)

**Cómo funcionan los packs**
- [Cómo funcionan las definiciones](#cómo-funcionan-las-definiciones)
- [Qué puedes sobrescribir](#qué-puedes-sobrescribir)
- [Packs del lado del servidor](#packs-del-lado-del-servidor)
- [Renombrados de registro](#renombrados-de-registro)
- [API de mods](#api-de-mods)
- [Packs escritos para 1.20.1, 1.21.1 y 26.x](#packs-escritos-para-1201-1211-y-26x)

**Bloques e ítems**
- [Bloques](#bloques)
- [Contenedores](#contenedores)
- [Modelos, blockstates y texturas](#modelos-blockstates-y-texturas)
- [Blockstates por tipo](#blockstates-por-tipo)
- [Hacer que vanilla trate bien tu bloque](#hacer-que-vanilla-trate-bien-tu-bloque)
- [Ítems](#ítems)
- [Fluidos](#fluidos)
- [Materiales, pestañas, sonidos, diccionario de minerales](#materiales-pestañas-sonidos-diccionario-de-minerales)
- [Sobrescritura de propiedades](#sobrescritura-de-propiedades)
- [Grupos de dureza](#grupos-de-dureza)

**Fabricación, botín y comercio**
- [Bloques e ítems desactivados](#bloques-e-ítems-desactivados)
- [Recetas de horno y combustibles](#recetas-de-horno-y-combustibles)
- [Pociones, tipos de poción y elaboración](#pociones-tipos-de-poción-y-elaboración)
- [Trabajo en el yunque](#trabajo-en-el-yunque)
- [Drops de bloques](#drops-de-bloques)
- [Botín de jugadores](#botín-de-jugadores)
- [Aldeanos y comercio](#aldeanos-y-comercio)

**Criaturas y peligros**
- [Variantes de entidades](#variantes-de-entidades)
- [Órdenes de trabajo](#órdenes-de-trabajo)
- [Exposiciones](#exposiciones)

**El mundo**
- [Plantillas de mundo](#plantillas-de-mundo)
- [Reglas del juego](#reglas-del-juego)
- [Biomas](#biomas)
- [Dimensiones](#dimensiones)
- [Cuerpos de Galacticraft](#cuerpos-de-galacticraft)
- [Portales y puertas dimensionales](#portales-y-puertas-dimensionales)
- [Mundos Rubic](#mundos-rubic)
- [El mundo profundo](#el-mundo-profundo)
- [Regiones de cuevas](#regiones-de-cuevas)

**Generación del mundo**
- [Entradas de worldgen](#entradas-de-worldgen)
- [Formas](#formas)
- [Dispersiones](#dispersiones)
- [Mapas de estructuras](#mapas-de-estructuras)
- [Parcelas de aldea](#parcelas-de-aldea)
- [Mapas de trazado de ciudades](#mapas-de-trazado-de-ciudades)
- [Ciudad continua](#ciudad-continua)
- [Retrogen](#retrogen)
- [Pregeneración](#pregeneración)

**Modos de juego**
- [Introducción al mundo](#introducción-al-mundo)
- [Equipos](#equipos)
- [Puntuación](#puntuación)
- [Asaltos](#asaltos)
- [Tarjetas](#tarjetas)
- [Dados y mazos](#dados-y-mazos)

**Control**
- [La capa de control](#la-capa-de-control)
- [Qué hace cada grupo](#qué-hace-cada-grupo)

**Otros mods**
- [Universal Tweaks](#universal-tweaks)
- [Mo' Villages](#mo-villages)
- [CoFH World](#cofh-world)
- [Lost Cities](#lost-cities)
- [Integración con Blast Plaster](#integración-con-blast-plaster)
- [Mods de tumbas](#mods-de-tumbas)

**Referencia**
- [Listas de valores](#listas-de-valores)
- [Lista de carpetas](#lista-de-carpetas)
- [Comandos](#comandos)
- [Conviene saber](#conviene-saber)
- [Cuando algo no funciona](#cuando-algo-no-funciona)
- [Extra: ajustes de vanilla](#extra-ajustes-de-vanilla)
- [Extra: arreglo de conflictos de plugins de JEI](#extra-arreglo-de-conflictos-de-plugins-de-jei)
- [Extra: menos errores al iniciar](#extra-menos-errores-al-iniciar)

---

# Primeros pasos

## Qué es

*primeros pasos*

Resource Data Pack Loader (RDPL) lee una única carpeta, `rdploader`, y hace tres trabajos:

- **Sobrescrituras.** Un archivo de la carpeta sustituye al que el juego o un mod habría cargado. Sin interruptores, sin configuración por mundo, sin nada que los jugadores tengan que activar.
- **Contenido nuevo.** Las definiciones JSON registran bloques, ítems, fluidos, biomas, dimensiones, pociones y aldeanos. Sin Java, sin jar.
- **Control.** Bloquea la generación de minerales, biomas, estructuras o recetas, aplana el lecho de roca, fija tasas de aparición, vacía el mundo principal y establece valores por defecto del mundo.

## Dónde van los archivos

*primeros pasos*

Todas las rutas de esta guía se escriben desde `assets/` en adelante, así que `<namespace>/blocks/*.json` es `assets/mypack/blocks/ruby_ore.json` en disco para un pack cuyo espacio de nombres es `mypack`. Cada sección repite su propia ruta bajo el encabezado, con una nota sobre en qué se convierte esa ruta.

| Ruta | Qué contiene |
| --- | --- |
| `<namespace>/blocks/*.json` | Definiciones de bloques. [Bloques](#bloques) |
| `<namespace>/items/*.json` | Definiciones de ítems. [Ítems](#ítems) |
| `<namespace>/fluids/*.json` | Fluidos, con un bloque y un cubo. [Fluidos](#fluidos) |
| `<namespace>/materials/*.json` | Materiales de herramientas y armaduras. [Materiales, pestañas, sonidos, diccionario de minerales](#materiales-pestañas-sonidos-diccionario-de-minerales) |
| `<namespace>/tabs/*.json` | Pestañas creativas. [Materiales, pestañas, sonidos, diccionario de minerales](#materiales-pestañas-sonidos-diccionario-de-minerales) |
| `<namespace>/sounds/*.json` | Eventos de sonido. [Materiales, pestañas, sonidos, diccionario de minerales](#materiales-pestañas-sonidos-diccionario-de-minerales) |
| `<namespace>/oredict/*.json` | Nombres del diccionario de minerales. [Materiales, pestañas, sonidos, diccionario de minerales](#materiales-pestañas-sonidos-diccionario-de-minerales) |
| `<namespace>/biomes/*.json` | Definiciones de biomas. [Biomas](#biomas) |
| `<namespace>/worldgen/*.json` | Qué se genera y dónde. [Entradas de worldgen](#entradas-de-worldgen) |
| `<namespace>/caveregions/*.json` | Regiones con nombre pintadas sobre el subsuelo. [Regiones de cuevas](#regiones-de-cuevas) |
| `<namespace>/dimensions/*.json` | Definiciones de dimensiones. [Dimensiones](#dimensiones) |
| `<namespace>/celestial/*.json` | Sistemas estelares y cuerpos para el mapa de Galacticraft. [Cuerpos de Galacticraft](#cuerpos-de-galacticraft) |
| `<namespace>/worldtemplates/*.json` | Todos los ajustes de un mundo en un solo archivo. [Plantillas de mundo](#plantillas-de-mundo) |
| `<namespace>/worldintro/*.json` | Páginas que se muestran cuando un jugador entra en el mundo. [Introducción al mundo](#introducción-al-mundo) |
| `<namespace>/gates/*.json` | Condiciones de portales y dimensiones. [Portales y puertas dimensionales](#portales-y-puertas-dimensionales) |
| `<namespace>/gamerules/*.json` | Reglas del juego para mundos nuevos. [Reglas del juego](#reglas-del-juego) |
| `<namespace>/teams/*.json` | Bandos del marcador de vanilla y qué se une a ellos. [Equipos](#equipos) |
| `<namespace>/scoring/*.json` | Objetivos, puntos y cómo termina una partida. [Puntuación](#puntuación) |
| `<namespace>/raids/*.json` | Oleadas que atacan una aldea cuando un jugador lleva un presagio a ella. [Asaltos](#asaltos) |
| `<namespace>/entities/*.json` | Variantes de entidades creadas a partir de entidades que ya existen. [Variantes de entidades](#variantes-de-entidades) |
| `<namespace>/hardness/*.json` | Multiplicadores de tiempo de minado y de explosión para grupos de bloques. [Grupos de dureza](#grupos-de-dureza) |
| `<namespace>/exposures/*.json` | Peligros que exponen a los jugadores cerca de bloques con nombre, al llevar ítems con nombre o en dimensiones con nombre. [Exposiciones](#exposiciones) |
| `<namespace>/orders/*.json` | Trabajos que las variantes de entidad hacen para un jugador en un cofre. [Órdenes de trabajo](#órdenes-de-trabajo) |
| `<namespace>/overrides/<target>/<name>.json` | Propiedades de bloques, ítems y tipos de poción existentes, modificadas en el sitio. [Sobrescritura de propiedades](#sobrescritura-de-propiedades) |
| `<namespace>/villages/*.json` | Parcelas que pueden construir las aldeas. [Parcelas de aldea](#parcelas-de-aldea) |
| `<namespace>/pathintersects/*.json` | Diseños pintados donde se cruzan los caminos de las aldeas. [Caminos de aldea](#caminos-de-aldea) |
| `<namespace>/structuremaps/*.json` | Plantillas compuestas en un único edificio grande sobre una cuadrícula. [Mapas de estructuras](#mapas-de-estructuras) |
| `<namespace>/citymaps/*.json` | Un plano de calles dibujado a partir del cual se traza una aldea en lugar de dejarla crecer. [Mapas de trazado de ciudades](#mapas-de-trazado-de-ciudades) |
| `<namespace>/portalframes/*.json` | Marcos que un jugador puede construir y encender. [Marcos de portal](#marcos-de-portal) |
| `<namespace>/blastplaster/*.json` | Qué hace Blast Plaster tras una explosión, por dimensión. [Integración con Blast Plaster](#integración-con-blast-plaster) |
| `<namespace>/structures/*.nbt` | Plantillas, para brotes, `imprint` y sobrescrituras de mods. [Qué puedes sobrescribir](#qué-puedes-sobrescribir) |
| `<namespace>/recipes/*.json` | Recetas de fabricación, añadidas o sustituidas. [Qué puedes sobrescribir](#qué-puedes-sobrescribir) |
| `<namespace>/recipe_removals/*.json` | Recetas eliminadas por nombre, espacio de nombres o resultado. [Qué puedes sobrescribir](#qué-puedes-sobrescribir) |
| `<namespace>/disabled/*.json` | Bloques e ítems sacados del juego. [Bloques e ítems desactivados](#bloques-e-ítems-desactivados) |
| `<namespace>/furnace/*.json` | Recetas de horno añadidas y eliminadas. [Recetas de horno y combustibles](#recetas-de-horno-y-combustibles) |
| `<namespace>/fuels/*.json` | Tiempos de combustión. [Recetas de horno y combustibles](#recetas-de-horno-y-combustibles) |
| `<namespace>/brewing/*.json` | Recetas del soporte para pociones. [Pociones, tipos de poción y elaboración](#pociones-tipos-de-poción-y-elaboración) |
| `<namespace>/potions/*.json` | Efectos de poción. [Pociones, tipos de poción y elaboración](#pociones-tipos-de-poción-y-elaboración) |
| `<namespace>/potion_types/*.json` | Pociones embotelladas construidas a partir de esos efectos. [Pociones, tipos de poción y elaboración](#pociones-tipos-de-poción-y-elaboración) |
| `<namespace>/villagers/*.json` | Profesiones de aldeanos. [Aldeanos y comercio](#aldeanos-y-comercio) |
| `<namespace>/trades/*.json` | Qué compran y venden las carreras. [Aldeanos y comercio](#aldeanos-y-comercio) |
| `<namespace>/loot_tables/*.json` | Tablas de botín, sustituidas. [Qué puedes sobrescribir](#qué-puedes-sobrescribir) |
| `<namespace>/loot_injections/*.json` | Un grupo añadido a una tabla que ya existe. [Qué puedes sobrescribir](#qué-puedes-sobrescribir) |
| `<namespace>/block_drops/*.json` | Drops extra o sustitutivos para bloques que un pack no posee. [Drops de bloques](#drops-de-bloques) |
| `<namespace>/anvils/*.json` | Encantamientos que un yunque aplica a un ítem con nombre, un logro que concede y un bloqueo hasta entonces. [Trabajo en el yunque](#trabajo-en-el-yunque) |
| `<namespace>/cards/*.json` | Tarjetas en pantalla mostradas con un disparador, y los mensajes que dice el propio mod. [Tarjetas](#tarjetas) |
| `<namespace>/dice/*.json` | Dados del pack con caras ponderadas, mazos de cartas, quién oye una tirada y la redacción de los resultados. [Dados y mazos](#dados-y-mazos) |
| `<namespace>/games/*.json` | Juegos de tablero con criaturas como piezas: el tablero, las piezas y cómo se mueven, y lo que paga un resultado. [Juegos de tablero](#juegos-de-tablero) |
| `<namespace>/player_loot/*.json` | Una tabla de botín que se tira cuando muere un jugador. [Botín de jugadores](#botín-de-jugadores) |
| `<namespace>/advancements/*.json` | Logros. [Qué puedes sobrescribir](#qué-puedes-sobrescribir) |
| `<namespace>/functions/*.mcfunction` | Archivos de funciones. [Qué puedes sobrescribir](#qué-puedes-sobrescribir) |
| `<namespace>/registry_remap/*.json` | Nombres antiguos asignados a nuevos. [Renombrados de registro](#renombrados-de-registro) |
| `<namespace>/texts/*.txt` | Archivos de texto plano, usados por la introducción al mundo. [Introducción al mundo](#introducción-al-mundo) |
| `<namespace>/models/`, `<namespace>/blockstates/`, `<namespace>/textures/`, `<namespace>/lang/` | Las carpetas de recursos habituales. [Modelos, blockstates y texturas](#modelos-blockstates-y-texturas) |

## Cómo leer las tablas

*primeros pasos*

Todos los archivos son JSON estándar. Una entrada de worldgen representativa:

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

Las tablas de clave de este documento indican si una clave es obligatoria, qué contiene y cuál es el valor por defecto si se omite. Los valores no reconocidos se registran en el log y se sustituyen por el valor por defecto; no hacen que el juego se cierre. Tipos de valor usados en todo el documento:

| Cuando una tabla dice | Escribes |
| --- | --- |
| int | `8` |
| int, ticks | `100` (20 ticks = 1 segundo) |
| int o rango | `8`, o `{ "min": 4, "max": 12 }` para sortear entre ellos |
| 0 a 15, 1 a 100 y similares | un int dentro de esos límites |
| float | `0.5` |
| boolean | `true` o `false` |
| string | `"palabras entre comillas"` |
| nombre de bloque, nombre de ítem | `"minecraft:stone"`, con los metadatos como tercera parte: `"minecraft:stone:3"` |
| `namespace:name` | `"mypack:ruby_ore"` |
| nombre de bioma, nombre de sonido, nombre de pestaña | la misma forma `namespace:name` entre comillas |
| color hexadecimal | seis dígitos hexadecimales, `"A0C8FF"`, `#` opcional |
| ruta de textura | `"mypack:blocks/ruby_ore"` |
| lista de ints | `[0, -1]` |
| lista de nombres de bloque | `["minecraft:stone", "minecraft:andesite"]` |
| lista de nombres de bioma | `["minecraft:extreme_hills", "mypack:ruby_hills"]` |
| lista de tipos de diccionario | `["MOUNTAIN", "FOREST"]` |
| lista de ids de mods o espacios de nombres de packs | `["quark", "mypack"]` |
| lista de objetos | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`, con las claves según la tabla propia de ese objeto |
| objeto | `{ "type": "cluster" }`, con las claves según su propia tabla |
| objeto de rol a bioma, de nombre de variante a variante | las claves son lo primero, los valores lo segundo: `{ "ocean": "mypack:ruby_ocean" }` |

La mayoría de las definiciones también aceptan `requires`, una lista de ids de mods o espacios de nombres de packs que deben estar presentes o el archivo se omite.

## La única regla

*primeros pasos*

Abre el jar, busca el archivo que quieres cambiar y copia su ruta desde `assets` en adelante:

```
assets/minecraft/textures/blocks/iron_ore.png        (in the Minecraft jar)
rdploader/assets/minecraft/textures/blocks/iron_ore.png    (your override)
```

La ruta posterior a `assets` es siempre idéntica a la ruta dentro del jar. Nada se renombra ni se mueve.

## Organizar los packs

*primeros pasos*

Los archivos sueltos funcionan bajo `rdploader/assets/<namespace>/`. Agruparlos también funciona, en un zip. Una carpeta dentro de `rdploader` nunca es un pack: se omite con una advertencia en el log, así que comprime el pack en un zip antes de ponerlo ahí.

```
rdploader/assets/minecraft/textures/blocks/iron_ore.png
rdploader/MyTextures.zip
```

**Packs en la carpeta equivocada.** Al iniciar, antes de leer `rdploader`, RDPL revisa la carpeta `resourcepacks` del juego y mueve a `rdploader` todos los zips de packs RDPL que encuentra. Un zip es un pack RDPL cuando contiene archivos de definición de RDPL, como `assets/<namespace>/blocks/` o, en un pack moderno, `data/<namespace>/blocks/`. Un pack de recursos normal se queda donde está. Un zip cuyo nombre ya existe en `rdploader` se deja en su sitio, y lo mismo un pack RDPL dentro de una carpeta; en ambos casos se emite una advertencia. Cada movimiento se escribe en `logs/rdpl.log`. Un pack movido deja de aparecer en la lista de packs de recursos y, desde entonces, RDPL lo carga desde `rdploader`.

**Prioridad.** Cuando dos packs contienen el mismo archivo, antepón a los nombres `RDPL` y un número; los números más altos se cargan después y ganan:

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

No distingue mayúsculas de minúsculas; un espacio, guion o guion bajo tras el número es opcional; el prefijo se oculta del nombre mostrado. Un pack sin prefijo se carga primero y pierde frente a cualquier pack numerado. La prioridad también ordena las entradas de worldgen, lo que importa cuando un pack coloca bloques que otro sustituye.

**Desactiva un pack** añadiendo `.disabled` a su nombre.

**Un zip para todas las versiones.** Un zip puede llevar una carpeta `versions/<version>/` por cada versión de Minecraft a la que sirve: `versions/1.12.2/`, `versions/1.20.1/`, `versions/1.21.1/` y, en 26.x, la versión exacta en la que se ejecuta, `versions/26.1.2/`, `versions/26.2/` o `versions/26.3/`. Cada una se organiza como la raíz de un pack para esa versión, `pack.mcmeta` incluido. Un archivo de la carpeta de la versión en ejecución se lee en lugar de la misma ruta en la raíz; la raíz es compartida por todas las versiones y la carpeta de otra versión nunca se lee. Pon en la raíz lo que todas las versiones leen igual y en una carpeta de versión solo lo que difiere, y un único zip se cargará en las cuatro.

**Los packs se convierten entre versiones.** Al cargar un pack escrito para otra línea, se convierte la primera vez, de la misma manera, escribiendo lo que cambió en su propia carpeta `versions/<version>/` como se ha descrito; esto ocurre automáticamente al cargar, incluida la carga que provoca un `/rdpl reload` o `/rdplserver reload`, nunca mediante un comando propio. Cada par de versiones se convierte en ambos sentidos, así que un pack de 1.12.2, 1.20.1, 1.21.1 o 26.x se carga en cualquiera de las otras. Un pack de 26.3 también se carga en todas las líneas, leído mediante el formato de 26.2 en las más antiguas, y un pack más antiguo se carga en 26.3. Lo que un lado tiene y el otro no puede contener se descarta, y el log nombra cada descarte: al bajar desde 26.3, entre otras cosas, las funciones de densidad de depuración, la exclusión de acuífero y el nivel de superficie propios de un pack, y los comandos que 26.2 no tiene; al subir a 26.3, el `depth` y el `offset` de un objetivo de aparición y las claves de veta de mineral del router de ruido. En 1.12.2, el worldgen de vanilla de un pack moderno, como sus biomas, características y ajustes de ruido, y su carpeta `neoforge` también se dejan fuera, ya que 1.12.2 no tiene equivalente para ninguno de los dos.

## Packs de recursos: quién gana

*primeros pasos*

Por defecto, los archivos de RDPL están por encima de los packs de recursos que selecciona un jugador, de modo que un pack de recursos no puede sobrescribirlos. Añade `O` o `N` después del prefijo `RDPL` para decidirlo pack por pack:

```
rdploader/RDPLO Branding        always wins; resource packs cannot touch it
rdploader/RDPLN BaseTextures    a resource pack can override it
rdploader/RDPL1O Seasonal       priority and override combined
```

Los packs sin letra siguen la opción de configuración `overrideResourcePacks`. `/rdpl list` marca los packs que sobrescriben. La letra debe terminar el prefijo (seguida de un espacio, guion, guion bajo o nada), así que `RDPLOverhaul` es un pack llamado `Overhaul`, no una marca `O`.

---

# Cómo funcionan los packs

## Cómo funcionan las definiciones

*cómo funcionan los packs*

Las carpetas de un pack hacen dos cosas: unas describen cosas nuevas y el resto sustituyen archivos que el juego o un mod ya tienen, tal como se explica en [Qué puedes sobrescribir](#qué-puedes-sobrescribir). En el primer caso, la ruta es la identidad: un archivo en `assets/mypack/blocks/ruby_ore.json` registra un bloque llamado `mypack:ruby_ore`.

El registro se hace con la prioridad más baja que ofrece Forge, así que si un mod real registra el mismo nombre, gana el mod y tu archivo se ignora. Nada de esto puede sustituir a un mod.

**Dónde está el límite.** Todo lo que necesite una entidad de bloque, una GUI, un inventario o lógica propia por tick necesita un mod real. Todo lo que queda por debajo de eso es terreno permitido.

### Tu espacio de nombres es tu mod

*cómo funcionan las definiciones*

El espacio de nombres que elijas es, a todos los efectos prácticos, un id de mod. Nada se carga como mod y nunca aparece en la lista de mods, pero todo lo que lee un id de mod lee el tuyo:

- Los nombres de registro son `mypack:ruby_ore`, exactamente como lo serían los de un mod, y se escriben en todos los mundos guardados que los contienen.
- Las listas blancas de minerales, biomas, generadores y recetas de la configuración lo reconocen, así que `oreWhitelist = mypack` conserva tus minerales y bloques y los de nadie más.
- `/rdpl which`, `/rdplserver oregen` y los informes agrupan todos por él.
- JEI, el diccionario de minerales y las consultas de otros mods lo ven de la misma manera.

Así que elige un nombre al principio y no lo cambies nunca. Renombrar un espacio de nombres deja huérfano todo lo que ya está colocado en un mundo, igual que cuando un mod cambia su id; para eso existe `registry_remap`, para repararlo.

Esto funciona en ambos sentidos: `requires` acepta un espacio de nombres de pack igual que el id de un mod instalado, de modo que un pack puede depender de otro y omitirse cuando este no está instalado.

**Un mod ausente detiene el juego, igual que la dependencia de un mod.** Todo id de mod nombrado en un `requires` en cualquiera de tus packs se entrega a Forge como dependencia de este mod, antes de que cargue nada. Si falta alguno, aparece la pantalla estándar de Mods ausentes que indica lo necesario, en un cliente o en un servidor dedicado, y mientras tanto no se genera ni se registra nada.

Un *pack* ausente es distinto. Los espacios de nombres de pack no son mods, así que nunca llegan a esa comprobación: la definición se omite, se escribe una línea en `logs/rdpl.log` con lo que faltaba y el juego sigue. Si un bloque que esperabas no está en la pestaña creativa, esa línea del log es el primer sitio donde mirar.

`requires` solo admite ids simples. No hay sintaxis de rangos de versión, así que puede indicar que un mod debe estar presente pero no qué versión.

Los dos ids propios del mod, `resourcedatapackloader` y `resourcedatapackloader_mixin`, están reservados. Definir contenido bajo ellos se ignora y se registra en el log, porque equivaldría a reclamar la propiedad de cosas que registra este mod. Sobrescribir los recursos propios de este mod sigue estando bien; lo único que no se puede es registrar contenido ahí.

Todas las tablas siguientes siguen las convenciones de [Cómo leer las tablas](#cómo-leer-las-tablas).

La mayoría de las definiciones también aceptan `requires`, una lista de ids de mods o espacios de nombres de packs que deben estar presentes o el archivo se omite.

### Opciones del pack

*cómo funcionan las definiciones*

Un pack puede llevar una carpeta `config` junto a su `assets`, con archivos JSON de opciones verdadero/falso y sus valores por defecto:

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

Un archivo con `"hide": true` en el nivel superior mantiene las opciones de ese pack fuera de la pantalla de opciones y del archivo generado por completo, mientras las opciones siguen condicionando el contenido con sus valores por defecto. Hay dos casos que lo piden: el contenido que aún no está listo para publicarse y los packs plantilla, donde las opciones son maquinaria que mantiene unidas las definiciones y no una elección que nadie deba hacer. Quita la clave para publicarlos. Lo mismo funciona por opción: `"hide": true` dentro del objeto de una opción oculta solo esa, de modo que un pack terminado puede llevar un interruptor para contenido sin terminar, o una puerta de plantilla, sin que ninguno de los dos se muestre:

    { "enablePackB": { "default": false, "hide": true } }

Como una opción oculta no se puede cambiar, una oculta con su valor por defecto en true queda efectivamente forzada a activa, para contenido que debe seguir conectado a través de la maquinaria de opciones pero que no es una elección.

Una opción también puede ser un objeto que lleve una descripción, mostrada bajo su nombre en la pantalla de opciones:

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

Todas las claves que acepta un archivo de opciones:

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

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| un nombre de opción | sí | boolean, o un objeto | | `true` o `false` es el valor por defecto de la opción. Un objeto lleva las tres claves siguientes |
| `hide` en el nivel superior | no | boolean | `false` | Mantiene las opciones de este pack fuera de la pantalla de opciones y del archivo generado por completo, mientras siguen condicionando el contenido con sus valores por defecto |
| `default` | sí | boolean | | El valor de la opción hasta que el usuario lo cambie. Un objeto sin un `default` booleano se ignora, con una advertencia |
| `hide` dentro de una opción | no | boolean | `false` | Oculta solo esa opción, de modo que no se puede cambiar y se queda en su valor por defecto |
| `description` | no | string | ninguno | Se muestra bajo el nombre de la opción en la pantalla de opciones |

Al iniciar, los archivos de opciones del pack se convierten en un único archivo de configuración real que pertenece al usuario, con el nombre del pack, `rdploader/config/PackA.json`, creado con los valores por defecto del pack y fusionado en las actualizaciones del pack para que las opciones nuevas lleguen sin tocar lo que el usuario ya haya fijado. Los cambios se aplican en el siguiente inicio del juego. Las opciones pertenecen solo a packs con nombre, es decir, zips, ya que el archivo generado lleva el nombre del pack; los archivos sueltos bajo `rdploader/assets` no tienen nombre de pack y no llevan opciones, así que comprime el contenido suelto en un pack con nombre si necesita un interruptor.

La lista `requires` de cualquier definición puede entonces nombrar una opción con una entrada `config:`: `"requires": ["config:enableTestingContent"]` registra ese contenido solo mientras la opción sea true, exactamente como si lo omitiera un mod ausente. Un nombre simple comprueba el archivo de todos los packs y todos los packs que lo definan deben coincidir; `"config:PackA:enableTestingContent"` nombra uno solo. Una opción que ningún pack define cuenta como false y se advierte de ello una vez.

Una entrada `file:` condiciona según que exista un archivo o carpeta bajo la carpeta del juego, para acoplar contenido a algo ajeno a los packs propios de RDPL, como el pack de recursos de otro mod: `"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` registra el contenido solo mientras ese archivo exacto esté instalado. La ruta es relativa a la carpeta del juego, siempre con barras normales, y no puede contener `..`.

### Herencia de definiciones

*cómo funcionan las definiciones*

Una definición de bloque o ítem puede partir de otra del mismo tipo con `"inherits"`, nombrando el nombre de registro de cualquier variante, y luego sobrescribir lo que difiera:

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "meta": 0, "hardness": 4.0 } } }

El hijo copia todas las estadísticas del archivo del padre y de la variante nombrada, el orden de los archivos nunca importa, las cadenas se resuelven empezando por el padre, y un círculo o un padre ausente se registra en el log y deja al hijo tal como está escrito. Los campos que escribe el hijo sustituyen al valor heredado; las propiedades anidadas de las variantes se sobrescriben una a una, pero las listas como `requires` se sustituyen enteras, así que escribe la lista completa que quieras. Los bloques solo heredan de bloques y los ítems solo de ítems.

### Plantillas de bloques e ítems

*cómo funcionan las definiciones*

Un padre puede ser una plantilla pura que nunca entra en el juego, ya que la herencia lee los propios archivos de definición, no lo que se registró. Condiciona la plantilla a una opción oculta que esté forzada a desactivada y no registrará nada, mientras sus estadísticas siguen siendo heredables:

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

La plantilla nunca se registra, mientras que `jacks_ore` se registra con el material, el sonido, la herramienta, la pestaña, los drops de experiencia y la resistencia de la plantilla, sobrescribiendo solo la dureza. El hijo debe escribir su propio `requires`, aquí vaciado a una lista vacía, porque de lo contrario heredaría el del padre y desaparecería con él.

## Qué puedes sobrescribir

*cómo funcionan los packs*

- **Cualquier cosa de la carpeta de recursos de un mod**, texturas, modelos, blockstates, archivos de idioma, sonidos, fuentes, textos de presentación, libros guía, manuales
- **Logros y tablas de botín**, del lado del servidor, de modo que también funcionan en servidores dedicados
- **Recetas**, sustituye la receta de un mod o añade la tuya
- **Plantillas de estructuras**, los archivos `.nbt` que usan los mods para los edificios generados, bajo `<namespace>/structures/`
- **Funciones**, los archivos `.mcfunction` bajo `<namespace>/functions/`
- **Renombrados de registro**, mantén funcionando los mundos antiguos cuando un mod renombra un bloque o ítem
- **Eliminación de recetas**, borra una receta de fabricación por nombre, espacio de nombres o resultado
- **Bloques e ítems desactivados**, saca del juego cualquier bloque o ítem, consulta [Bloques e ítems desactivados](#bloques-e-ítems-desactivados)
- **Inyecciones de botín**, añade un grupo a una tabla de botín en lugar de sustituirla entera
- **Botín de jugadores**, tira una tabla de botín cuando muere un jugador, además de lo que llevaba o en lugar de ello
- **Drops de bloques**, añade o sustituye lo que suelta cualquier bloque cuando un jugador lo rompe
- **Propiedades de bloques, ítems y pociones existentes**, dureza, luz, tamaños de pila, comida en cualquier cosa, los efectos de una poción, consulta [Sobrescritura de propiedades](#sobrescritura-de-propiedades)
- **Nombres del diccionario de minerales, recetas de horno, tiempos de combustión, pestañas creativas y eventos de sonido**

RDPL sirve para sustituir una o dos recetas, y las recetas de tu propio contenido deben añadirse en el pack junto a él. Para un control total de las recetas en un modpack, CraftTweaker y GroovyScript son mejores opciones, y un archivo de aquí sigue sustituyendo al original por completo, así que para cambiar un ingrediente o quitar una entrada de botín, usa esos.

## Packs del lado del servidor

*cómo funcionan los packs*

Un pack puede vivir solo en el servidor, con jugadores en clientes vanilla puros, con una única condición: **nada de lo que contiene puede registrar nada**. Ambos ids de mod aceptan cualquier remoto; el pack decide. Un cliente vanilla juega con los registros con los que vino, así que un pack que les añade algo debe estar en ambos lados.

| Basta con el servidor | Necesita el pack también en el cliente |
| --- | --- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures`, `caveregions` | `blocks`, `items`, `fluids`, `materials` |
| `villages`, `pathintersects`, `structuremaps`, `citymaps` | `potions`, `potion_types`, `sounds`, `tabs` |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `oredict`, `disabled` | `biomes`, `dimensions`, `portalframes` |
| `loot_tables`, `loot_injections`, `block_drops`, `anvils`, `player_loot`, `advancements`, `functions` | `villagers` |
| `gates`, `cards`, `registry_remap`, `exposures`, `hardness`, `overrides`, `trades` (para profesiones que el cliente conoce: las de vanilla o las de un mod presente en ambos lados) | `entities`, `worldintro`, `texts` (la introducción nunca se muestra a un cliente vanilla) |
| `teams`, `scoring` | `models`, `blockstates`, `textures`, `lang` (carpetas del cliente: sin cliente, déjalas fuera) |
| toda la capa de control, los ajustes y la pregeneración | |

La columna de la derecha es un tope absoluto: un cliente vanilla enviado a una dimensión desconocida se desconecta, y los bloques desconocidos no se le pueden describir. La columna de la izquierda funciona porque todo lo que contiene se ejecuta por completo en el servidor o llega al cliente mediante paquetes que vanilla ya entiende (casilla de resultado de fabricación rellenada por el servidor, paquetes de logros normales, rechazos de portal mediante mensajes de estado y una retención de pregeneración hecha con paquetes vanilla de modo de juego, título y teletransporte).

`worldtemplates` es del lado del servidor con una excepción: **`rubicWorld` no se puede usar con `vanillaClients`**. Un mundo Rubic está hecho de cubos, y a un cliente sin el mod no se le pueden enviar, así que se le rechazaría al iniciar sesión o no vería nada. Con ambos activados, los mundos nuevos se crean planos en lugar de Rubic y el log explica por qué, en vez de dejar un servidor que rechaza a todos los jugadores. Activar `vanillaClients` para un mundo que *ya* se creó como mundo Rubic es el único caso que detiene el juego por completo: cargar ese guardado como mundo normal lo arruinaría, así que se deja intacto para que lo decidas tú.

Configuración:

1. Activa `vanillaClients` en la configuración (categoría `content`, requiere reiniciar). Aplica la columna de la derecha: esas carpetas se omiten al cargar y cada archivo omitido se nombra en el log, de modo que un archivo de bloque que se haya colado se convierte en una línea de log en lugar de una conexión rechazada.
2. Mantén igualmente las definiciones fuera de las carpetas de la derecha; los archivos omitidos son lastre. Cuando el pack haga referencia a ítems (el `hold` de un portal, `killedDrops`, resultados de recetas, comercios), nombra solo ítems que aporten vanilla o los demás mods presentes en ambos lados del servidor.
3. Deja fuera las variantes de entidades. Cada una se registra como una entidad propia, que un cliente vanilla no tiene forma de hacer aparecer, así que `vanillaClients` las omite igual que a los bloques y las nombra en el log; el `standIn` de un bando o una aparición que nombre una no tiene entonces nada que crear.
4. Instala en el servidor como de costumbre. No se pone nada en las máquinas de los jugadores; `/rdpl` no existirá para ellos, así que usan las formas `/rdplserver`: `/rdplserver team join`, `/rdplserver round start`, `round reset`, `round vote yes`. `opens.leaderSays` y `reset.voteSays` nombran `/rdpl` por defecto, así que redáctalos con `/rdplserver` en un pack servido a clientes vanilla.
5. Prueba con una conexión limpia de un cliente vanilla de la misma versión. Los fallos son ruidosos: la conexión se rechaza en la puerta, no se rompe sin avisar más adelante.
6. Carencias aceptadas:
   - Las recetas añadidas por el servidor se fabrican pero no aparecen en el libro de recetas.
   - La introducción al mundo no se muestra. Tampoco se espera a un cliente vanilla: se le da la bienvenida de inmediato, se le libera de una retención de pregeneración junto con todos los demás y nunca retrasa `/rdpl round start`.
   - Todo lo que este mod muestra como tarjeta, como los resultados, el aviso del líder o las líneas de `saysCard`, llega como líneas de chat, y las notas a media pantalla, como las del vestíbulo, llegan como títulos.
   - Un grupo de dureza fija cuánto tarda el servidor en romper un bloque, pero la animación de grietas del cliente va al ritmo habitual del bloque. Una sobrescritura de un número que el cliente también lee, como el tamaño de pila, la durabilidad, la dureza o la luz, sigue mostrando allí el valor antiguo.
   - El minado `adventure` de un grupo de dureza no funciona: un cliente vanilla en modo aventura nunca empieza a picar a menos que el ítem en mano nombre el bloque en su propia etiqueta `CanDestroy`.
   - Un bloque o ítem desactivado sigue apareciendo en las pestañas creativas de un cliente vanilla, que las construye él mismo; todo lo demás sobre desactivar funciona desde el servidor.

## Renombrados de registro

*cómo funcionan los packs*

`<namespace>/registry_remap/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

Cuando un mod renombra uno de sus bloques o ítems, los mundos guardados antes del cambio los pierden. Suelta aquí un archivo para asignar el nombre antiguo al nuevo:

```json
{
  "registry": "minecraft:items",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

El registro es aquel al que pertenece la entrada, normalmente `minecraft:items` o `minecraft:blocks`. Los renombrados se encadenan, así que asignar A a B y más tarde B a C lleva A directamente a C.

## API de mods

*cómo funcionan los packs*

Un mod puede incluir contenido de RDPL dentro de su propio jar, de modo que no necesita un pack aparte. Pon una carpeta llamada `rdploader` en la raíz del jar y organízala exactamente como un pack:

```
thatmod.jar
  mcmod.info
  rdploader/assets/thatmod/blocks/ruby_ore.json
```

Lo que aporta un mod es un valor por defecto, no una sobrescritura. Se carga por debajo de todos los packs de la carpeta de packs, así que cualquier cosa que escriba el autor de un pack gana sobre ello, y un mod solo puede aportar archivos bajo un espacio de nombres que declare en su propio `mcmod.info`. Los archivos bajo cualquier otro espacio de nombres se ignoran con una advertencia, y lo mismo una carpeta `rdploader` anidada dentro de un espacio de nombres, de modo que un mod no puede redefinir a escondidas el contenido de otro mod ni el de un autor de packs.

Todo mod que incluye uno obtiene una entrada en `rdploader/config/mods.json` la primera vez que se detecta:

```json
{
  "thatmod": {
    "enabled": true,
    "priority": -1
  }
}
```

| Campo | Valores | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `enabled` | `true` o `false` | `true` | Desactiva el contenido de ese mod, igual que `.disabled` desactiva un pack |
| `priority` | `-1` o un número | `-1` | `-1` mantiene el mod por debajo de todos los packs; cualquier otro número lo coloca en el orden de [prioridad](#organizar-los-packs) ordinario junto a los packs numerados |

Un pack de mod nunca entra en el nivel de sobrescritura de packs de recursos, diga lo que diga `overrideResourcePacks`, ya que solo el autor de un pack puede pedirlo con la letra `O`. El log marca los packs de mods y lista los packs empezando por el más bajo, de modo que nada se carga sin que se vea.

## Packs escritos para 1.20.1, 1.21.1 y 26.x

*cómo funcionan los packs*

Un pack hecho para la línea 1.20.1, 1.21.1 o 26.x de este mod también se carga aquí. El cargador reconoce uno por un formato de `pack.mcmeta` superior a 3, por una carpeta `data/` junto a `assets/`, o por archivos de idioma `.json` y `textures/block/` sin equivalente en 1.12.2, y lo trae de vuelta de la misma manera sea cual sea la línea para la que se escribió. Un zip se convierte una sola vez, dentro de sí mismo: cada archivo que 1.12.2 lee de forma distinta se escribe en la carpeta `versions/1.12.2/` del zip, y los archivos modernos de la raíz se quedan como estaban, de modo que el mismo zip sigue cargándose en 1.20.1, 1.21.1 y 26.x, tal como describe [un zip para todas las versiones](#organizar-los-packs). La conversión marca la carpeta que escribe con un archivo `port.stamp` que contiene la versión de RDPL. Un zip que ya tiene una carpeta `versions/1.12.2/` se lee a través de ella; cuando su `port.stamp` nombra otra versión de RDPL, la conversión escribe la carpeta de nuevo, sustituyendo todos los archivos que contiene y nombrando cada uno en el log, y una carpeta sin `port.stamp`, como la que haya escrito el autor del pack, no se vuelve a convertir nunca. De los archivos de la raíz, solo se siguen leyendo los que la conversión deja pasar sin cambios, como los sonidos y las texturas fuera de `textures/block/` y `textures/item/`. El zip se escribe primero en un archivo temporal y solo sustituye al original cuando está completo. Los archivos sueltos bajo `rdploader/assets` y `rdploader/data` no se reescriben; se leen a través de la misma conversión cada vez que se explora la carpeta.

Un archivo de bloque moderno regresa con un `meta` por cada variante, en el orden en que están escritas las variantes, y sus etiquetas como nombres del diccionario de minerales:

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

| Archivo moderno | Archivo de 1.12.2 |
| --- | --- |
| `data/<ns>/<folder>/` para cada carpeta de definiciones | `assets/<ns>/<folder>/` |
| `recipe/`, `loot_table/`, `advancement/`, `function/`, `structure/` (1.21.1) | `recipes/`, `loot_tables/`, `advancements/`, `functions/`, `structures/` |
| `data/*/tags/items/` (1.21.1: `tags/item/`) | `assets/<ns>/oredict/converted_tags.json` |
| `data/minecraft/tags/functions/tick.json` | `assets/<ns>/gamerules/converted_tick.json`, el `gameLoopFunction` del mundo principal |
| recetas `minecraft:smelting` | `assets/<ns>/furnace/converted_smelting.json` |
| `assets/<ns>/lang/<lang>.json` | `assets/<ns>/lang/<lang>.lang` |
| `textures/block/`, `textures/item/` | `textures/blocks/`, `textures/items/` |
| `models/item/<variant>.json` | `models/item/<file>/<variant>.json` |
| sin blockstate (se genera en la línea moderna) | un blockstate de Forge generado por cada archivo de bloque, con los modelos que necesita su tipo |

- Todo id vanilla moderno se trae de vuelta mediante una tabla de aplanado incluida en el jar, construida a partir de los propios data fixers del juego, de modo que sale como el bloque o ítem de 1.12.2 con sus metadatos: `minecraft:red_wool` se convierte en `minecraft:wool:14`, `minecraft:oak_log` con `axis=x` se convierte en `minecraft:log:4`. Donde una clave contiene un bloque y un meta por separado, como `block` en worldgen y `modelBlock`, el meta va a `meta` o `modelMeta`; un `soil` conserva el bloque entero. Los ids propios del pack se resuelven a través de sus propios archivos: `mypack:worm` se convierte en `mypack:test_ore:1`. Los nombres de entidades, biomas, tablas de botín, sonidos, partículas y atributos se traen de vuelta de la misma manera, y una dimensión con nombre se convierte en un número: las del propio pack toman su `id` o, si no lo tienen, un número estable a partir de 1000 que nombra el log.
- Las etiquetas se convierten en nombres del diccionario de minerales mediante el inverso de la asignación por convención: `forge:gems/testium` y `c:gems/testium` se convierten en `gemTestium`, `minecraft:logs` se convierte en `logWood`. El `tag` de un ingrediente de receta se convierte en un ingrediente `forge:ore_dict` y la receta en `forge:ore_shaped` o `forge:ore_shapeless`; el `tag` de un combustible se convierte en `oreDict`. Todo ítem de receta recibe un `data`, ya que 1.12.2 rechaza un ítem con subtipos que no lo tenga.
- El `block.mypack.worm` de un archivo de idioma se convierte en `tile.mypack:test_ore.worm.name`, `item.` igual bajo `item.`, `itemGroup.mypack.tab` se convierte en `itemGroup.tab` y `fluid.mypack.x` se convierte en ambas claves de fluido de 1.12.2.
- Las tablas de botín pierden lo que 1.12.2 no puede leer: la variante de un ítem se convierte en `set_data`, los proveedores de números se convierten en `min` y `max`, los grupos reciben un `name`, las entradas `alternatives` y `group` se aplanan, y una función, condición o tipo de entrada que 1.12.2 no tiene se deja fuera con una línea en el log. El `items` de un logro se convierte en `item` y `data`, y un `tag` se convierte en `forge:ore_dict`.
- Las funciones se reescriben línea por línea a la sintaxis de 1.12.2: `execute as ... at @s run` se convierte en `execute <entity> ~ ~ ~`, `execute if block` se convierte en `detect`, `tag` y `team` se convierten en `scoreboard players tag` y `scoreboard teams`, `data merge` se convierte en `blockdata` y `entitydata`, y los selectores cambian `distance`, `scores`, `limit` y `gamemode` por `r`, `score_X_min`, `c` y `m`. El `SpawnData` de un spawner pierde su envoltorio `entity`. Una línea que la conversión no puede traer, como `bossbar` o una línea de macro, se convierte en comentario y el log nombra el archivo, la línea y el motivo, de modo que la función sigue cargándose.
- Un `.nbt` de estructura por encima de la versión de datos 1343 recupera su paleta, sus spawners, sus pilas de ítems y los ids de sus entidades; un bloque sin equivalente en 1.12.2 se deja como está escrito y coloca aire.
- El suelo de un mundo plano moderno está en el fondo del mundo, y 1.12.2 construye un mundo plano desde y 0, así que la conversión sube las alturas 64 (o el `worldMinHeight` de la plantilla cuando lo nombra): el `worldSpawn` y el `resetSendsTo` de una plantilla plana, las alturas de aparición de un equipo, el `groundLevel` de una dimensión plana y todas las y absolutas de las funciones cuando el mundo principal del pack es plano.
- Se deja fuera, cada cosa con una línea en el log: el worldgen basado en datos de vanilla, los tipos de dimensión, los tipos de daño, los encantamientos y otros registros que 1.12.2 no tiene; las etiquetas de bloques, entidades y fluidos; las etiquetas de funciones que no sean `tick`; el cortapiedras, la herrería y otros tipos de receta que 1.12.2 no tiene; los componentes de ítems; las claves de estructura que solo generan los mundos modernos; un nombre de `behavesAs` distinto de `till`, `path`, `bush` y `animals`; `jobSite`, y una profesión sin `careers` recibe una con el nombre de su archivo.

El log lleva una línea de resumen por cada pack convertido y una línea por cada archivo que movió, convirtió, dejó fuera o no pudo traer, y todas las claves que 1.12.2 no lee las sigue nombrando el parser que se las encuentra. La conversión es un mejor esfuerzo, no un pack terminado: lee esas líneas y termina a mano lo que nombran, empezando por cualquier blockstate generado cuyas texturas no haya podido encontrar. Haz esos arreglos en la raíz del pack o en un zip aparte, nunca en la carpeta `versions/1.12.2/` que escribió la conversión: otra versión de RDPL escribe esa carpeta de nuevo.

---

# Bloques e ítems

## Bloques

*bloques e ítems*

`<namespace>/blocks/*.json`

La ruta del archivo es el nombre de registro del bloque, así que `mypack/blocks/ruby_ore.json` registra `mypack:ruby_ore`. Las claves dentro de `variants` nombran los valores de metadatos de ese único bloque; no son bloques por sí mismas.

Todas las claves, mostradas de una vez. Un archivo real escribe solo las que necesita. Una clave marcada para un tipo solo la lee ese tipo.

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

### Tipos

*bloques*

| Tipo | Qué obtienes |
| --- | --- |
| `basic` | Un bloque simple. Se usa cuando falta `type` |
| `ore` | Suelta algo distinto de sí mismo, con fortuna y toque de seda |
| `falling` | Cae como la arena o la grava |
| `slab` | Inferior, superior y doble, y dos de ellos se fusionan en la mano |
| `stairs` | Esquinas y pendientes resueltas por ti |
| `fence` | Se conecta con sus vecinos y con las vallas de otros mods |
| `pane` | Se conecta como los paneles de cristal |
| `wall` | Se conecta como los muros de adoquín, con la forma de poste |
| `door` | De dos bloques de alto, se abre a mano y responde a la redstone. Usa una sola variante, ya que el resto de los metadatos llevan la bisagra, la orientación y si está abierta |
| `trapdoor` | Una trampilla abatible en la parte superior o inferior de un bloque, abierta a mano o con redstone. Una sola variante, los metadatos llevan la orientación, la mitad y si está abierta |
| `fence_gate` | Una puerta en una línea de vallas, abierta a mano o con redstone, y rebajada donde se une a un muro. Una sola variante |
| `banner` | Un estandarte sobre un poste o contra una pared, con dieciséis rotaciones de pie, que lleva tu propio diseño. Registra un segundo bloque llamado `<name>_wall` para el colgado |
| `ladder` | Se puede trepar, colocada contra una pared |
| `torch` | Colocación en pared y en suelo, con una partícula |
| `bell` | Una campana como la que tienen las aldeas desde la 1.14: suena al usarla por un lado, con redstone o cuando la golpea un proyectil, se balancea en su marco y hace brillar a los saqueadores cercanos. Una sola variante, los metadatos llevan la orientación y cómo cuelga |
| `log` | Gira hacia la cara contra la que lo colocas, y se registra como `logWood` en el diccionario de minerales para que la tala de árboles y Blast Plaster lo traten como tronco |
| `leaves` | Se descompone, se cizalla, se tiñe y suelta un brote, y se registra como `treeLeaves` en el diccionario de minerales |
| `sapling` | Crece hasta convertirse en un árbol o en una de tus estructuras |
| `crop` | Crece por etapas, suelta una semilla y un ítem de cosecha |
| `flower` | Una planta de un solo bloque que se sostiene sobre tierra |
| `cane` | Crece hacia arriba en una columna, como las cañas o el cactus |
| `vine` | Trepa y cuelga de los lados de los bloques |
| `portal` | Envía a lo que entre a otra dimensión |
| `container` | Guarda un inventario que un jugador puede abrir, de cualquier tamaño, y puede llenarse solo desde una tabla de botín la primera vez que se abre. Se dibuja como un bloque normal o como un cofre, según lo que pida el pack |

### Claves de archivo

*bloques*

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `variants` | sí | objeto de nombre de variante a variante | | Una entrada por cada valor de metadatos. La clave nombra ese valor en el blockstate, la ruta del modelo y la clave de idioma. El nombre de registro sale de la propia ruta del archivo |
| `type` | no | uno de los tipos anteriores | `basic` | Qué forma adopta el bloque |
| `material` | no | uno de los [materiales de bloque](#listas-de-valores) | `rock` | Comportamiento al minar, pistones, fuego y líquidos |
| `soundType` | no | uno de los [tipos de sonido](#listas-de-valores) | `stone`; `wood` para un `log`, `plant` para `leaves` y un `crop`, el del `modelBlock` para `stairs` y un `wall` | Pasos, rotura y colocación |
| `mapColor` | no | uno de los [colores de mapa](#listas-de-valores) | el del material | Cómo se ve en un mapa |
| `harvestTool` | no | `pickaxe`, `axe`, `shovel` | `pickaxe` | Qué herramienta lo recolecta |
| `harvestToolLevel` | no | 0 a 3 | `0` | 0 madera, 1 piedra, 2 hierro, 3 diamante |
| `silkHarvest` | no | boolean | `true` | Si el toque de seda devuelve el propio bloque |
| `opensWith` | no | id de ítem | ninguno | Convierte el bloque en una caja con cerradura: al romperlo suelta el propio bloque, y al hacer clic derecho con el ítem nombrado se consume uno, suena el sonido de rotura del bloque, se entrega la lista `drops` de la variante y se elimina el bloque. Cualquier otro clic muestra en la barra de acción la línea `tile.<pack>:<block>.<variant>.locked` de los archivos de idioma |
| `openSound` | no | nombre de sonido | el sonido de rotura | Lo que reproduce una caja con cerradura al abrirse en lugar de su sonido de rotura |
| `expDrop` | no | objeto con `min` y `max` | ninguno | Experiencia soltada al romperlo sin toque de seda |
| `creativeTab` | no | nombre de pestaña | ninguno | La pestaña en la que aparece |
| `renderLayer` | no | `solid`, `cutout`, `cutout_mipped`, `translucent` | según el tipo | Cómo se dibuja |
| `opaque` | no | boolean | `true` | Si bloquea por completo la vista y la luz |
| `fullCube` | no | boolean | igual que `opaque` | Si llena todo su espacio |
| `lightOpacity` | no | 0 a 255 | `255` si es opaco, si no `0` | Cuánta luz absorbe |
| `slipperiness` | no | float | `0.6` | El hielo es `0.98` |
| `flammability` | no | int | `0` | Con qué facilidad lo consume el fuego |
| `fireSpread` | no | int | `0` | Con qué facilidad se propaga el fuego desde él |
| `explosionResistanceDivisor` | no | float | `1.0` | Divide la `resistance` de cada variante frente a las explosiones |
| `modelBlock` | no | nombre de bloque | `minecraft:stone` | Bloque cuyo modelo se toma prestado cuando el tuyo no tiene ninguno |
| `modelMeta` | no | int | `0` | Qué variante de ese modelo |
| `itemModel` | no | `state`, `item` | `state` | `state` sigue el blockstate, `item` busca su propio archivo |
| `tint` | no | `biome`, `none` o un color hexadecimal | ninguno | Necesita un `tintindex` en el modelo para verse |
| `plantTypes` | no | lista de [tipos de planta](#listas-de-valores) | ninguno | Qué se puede plantar sobre él |
| `behavesAs` | no | lista de `till`, `path`, `bush`, `animals` | ninguno | Comportamientos de vanilla que adopta |
| `bounds` | no | lista de seis números, 0 a 1 | bloque completo | La caja de colisión, como `[x1, y1, z1, x2, y2, z2]` |
| `requires` | no | lista de ids de mods o espacios de nombres de packs | ninguno | El archivo se omite salvo que estén presentes todos |
| `particle` | solo torch | `none`, `flame`, `colored` | `flame` | La partícula sobre una antorcha |
| `particleColor` | solo torch | color hexadecimal | `FFFFFF` | Se usa cuando `particle` es `colored` |
| `smoke` | solo torch | boolean | `true` | Si echa humo |
| `leafSapling` | solo leaves | nombre de bloque | ninguno | El brote que sueltan |
| `leafSaplingChance` | solo leaves | int | `5` | Una de cada N hojas suelta uno |
| `seed` | solo crop | nombre de ítem | `minecraft:wheat_seeds` | El ítem que lo planta y lo que suelta un cultivo sin madurar |
| `produce` | solo crop | nombre de ítem | `minecraft:wheat` | Lo que da la cosecha |
| `maxAge` | solo crop | int | `7` | Cuántas etapas de crecimiento |
| `growth` | solo plants | objeto | ninguno | Consulta [Crecimiento](#crecimiento) |
| `sapling` | solo sapling | objeto | ninguno | Consulta [Brotes](#brotes) |
| `portal` | solo portal | objeto | ninguno | Consulta [Portales y puertas dimensionales](#portales-y-puertas-dimensionales) |
| `container` | solo container | objeto | ninguno | Consulta [Contenedores](#contenedores) |
| `bell` | solo bell | objeto | ninguno | Consulta [Campanas](#campanas) |

### Claves de variantes

*bloques*

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `meta` | sí | 0 a 15 | | El valor de metadatos que reclama esta variante |
| `hardness` | no | float | `1.0` | Cuánto tarda en romperse. La obsidiana es `50`, `-1` es irrompible |
| `resistance` | no | float | `5.0` | Resistencia a las explosiones |
| `light` | no | 0 a 15 | `0` | Luz que emite |
| `harvestLevel` | no | 0 a 3 | `0` | Sobrescribe el nivel de herramienta para esta variante |
| `rarity` | no | `common`, `uncommon`, `rare`, `epic` | `common` | Color del nombre en la descripción emergente |
| `maxSize` | no | 1 a 64 | `64` | Tamaño de pila |
| `oreDict` | no | lista de nombres del diccionario de minerales | ninguno | Nombres del diccionario de minerales bajo los que se registra esta variante |
| `drops` | no | lista de drops | se suelta a sí mismo | Lo que da al romperlo |

**Los metadatos son permanentes.** El número que reclama una variante se escribe en todos los mundos guardados que la contienen. Renumerar o reordenar variantes más adelante convierte los bloques colocados en otra cosa. Añade las variantes nuevas al final y no reutilices nunca un número.

Un bloque `basic` puede tener dieciséis variantes; un `slab`, ocho; `log` y `leaves`, cuatro, porque el eje y los indicadores de descomposición necesitan bits propios; los tipos de un solo estado tienen una.

### Drops

*bloques*

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

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `block` | una de las dos | nombre de bloque o ítem | | Lo que se suelta |
| `entity` | una de las dos | nombre de entidad | | Una entidad liberada al romperse el bloque, en lugar de un ítem |
| `meta` | no | int | `0` | Qué variante de ello |
| `amount` | no | int o rango | `1` | Cuántos |
| `chance` | no | 0 a 100 | `100`, o `0` cuando `guaranteed` está desactivado | Con qué frecuencia se produce el drop |
| `weight` | no | int | `0` | Por encima de cero, la entrada se une a un grupo que da exactamente un drop. Mira más abajo |
| `bonusChance` | no | lista de ints | ninguno | Drops extra por nivel de fortuna, una entrada por nivel |
| `guaranteed` | no | boolean | `true` | Abreviatura heredada de `chance`. Activado es `100`, desactivado es `0` |

Cada entrada sin `weight` se decide por separado, así que un bloque con tres de ellas puede soltar las tres o ninguna. Dale un `weight` a las entradas y dejan de ser independientes: forman un único grupo del que se elige exactamente una cada vez que se rompe el bloque, con probabilidades proporcionales a los pesos. Arriba, el diamante y la esmeralda comparten un grupo a razón de uno a cuatro, así que siempre sale una de las dos y es esmeralda cuatro veces de cada cinco, mientras que el rubí y el carbón se deciden por separado y el lepisma es otra cosa distinta. Los ítems y las entidades forman grupos separados, de modo que un ítem con peso y una entidad con peso no compiten.

Una entrada que nombra una `entity` libera una donde estaba el bloque, mirando en una dirección aleatoria, y a un mob se le da su tratamiento de aparición habitual según la dificultad local, de modo que llega con el equipo y los efectos que habría tenido. `amount` decide cuántas, `chance` con qué frecuencia, `weight` la mete en el grupo de entidades. Ocurre al romperse el bloque, sea cual sea la forma en que se rompió, así que una explosión o un pistón las libera igual que un pico. `meta`, `bonusChance` y la fortuna no significan nada para una entidad y se ignoran.

Un drop que nombra tanto un `block` como una `entity` usa la entidad y lo dice en el log.

### Crecimiento

*bloques*

Para `crop`, `flower`, `cane` y `vine`.

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

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `stages` | no | int | `16` | Etapas de crecimiento antes de terminar |
| `growth` | no | int | | Una probabilidad entre N por tick aleatorio de avanzar |
| `spread` | no | int | `0` | Hasta dónde se extiende a los bloques vecinos |
| `maxHeight` | no | int | `3` | Solo cane. Qué altura alcanza la columna |
| `drop` | no | nombre de ítem | ninguno | Lo que suelta al romperse |
| `dropCount` | no | int | `1` | Cuántos |
| `needsSky` | no | boolean | `false` | Solo crece donde se ve el cielo |
| `needsWater` | no | boolean | `false` | Solo crece cerca del agua |
| `waterRange` | no | int | `1` | A qué distancia puede estar esa agua |
| `damage` | no | boolean | `false` | Hace daño a lo que lo toca |
| `damageAmount` | no | float, medios corazones | `1.0` | Cuánto daño hace |
| `breaksNeighbors` | no | boolean | `false` | Rompe los bloques colocados a su lado, como el cactus |

### Brotes

*bloques*

Todas las claves, mostradas de una vez. Un archivo real escribe solo las que necesita.

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

Una `structure` sustituye el árbol generado por una de tus plantillas, que es la forma de construir algo que un generador no puede, y no hace falta escribir nada más en el bloque. Nombra varias bajo `structures` en su lugar y el brote elige una cada vez que crece, de modo que un bosque no sea el mismo árbol una y otra vez:

```json
{
  "sapling": { "structure": "mypack:ruby_tree" }
}
```

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `soil` | no | lista de nombres de bloque | ninguno | Sobre qué crecerá |
| `stages` | no | int | `2` | Etapas de crecimiento antes de convertirse en árbol |
| `chance` | no | int | `7` | Una entre N por tick aleatorio |
| `light` | no | 0 a 15 | `9` | Nivel de luz necesario |
| `log` | no | nombre de bloque | `minecraft:log` | Bloque del tronco |
| `leaves` | no | nombre de bloque | `minecraft:leaves` | Bloque de las hojas |
| `height` | no | int | `4` | Altura del tronco |
| `vines` | no | boolean | `false` | Hace colgar enredaderas de las hojas |
| `structure` | no | `namespace:name` | ninguno | Crece hasta esta plantilla en lugar de un árbol generado |
| `structures` | no | lista | ninguno | Varias plantillas en las que crecer, una elegida cada vez que crece. Cada entrada es `{ "structure": "namespace:name", "weight": 3 }`, o un nombre simple para probabilidades iguales. Sobrescribe `structure` |

## Contenedores

*bloques e ítems*

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

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `rows` | entero | `3` | Cuántas filas de ranuras, de 1 a 9 |
| `columns` | entero | `9` | Cuántas ranuras por fila, de 1 a 12 |
| `lootTable` | texto | vacío | Una tabla de botín que se sortea en el bloque la primera vez que un jugador lo abre, igual que se llena un cofre de mazmorra. Vacío lo deja vacío al empezar |
| `chestModel` | booleano o texto | `false` | Se dibuja como un cofre con tapa que se abre, en lugar de como un bloque normal con tu propio modelo. `true` usa el aspecto del cofre de vanilla; un nombre de textura como `mypack:blocks/strongbox_chest` usa en su lugar tu propia hoja de cofre, tanto para el bloque colocado como para el ítem. Dale al blockstate el modelo `resourcedatapackloader:pack_chest`, con el mismo nombre en `texture`, para que el ítem que llevas en la mano también tenga forma de cofre. Un bloque con modelo de cofre también establece `opaque` en `false` por defecto, como un cofre de vanilla, de modo que la luz no se corta en el bloque y el cofre no se dibuja oscuro. |
| `guiTexture` | texto | vacío | Tu propia imagen de fondo para la pantalla. Vacío dibuja una a partir de la pantalla del cofre de vanilla, con el tamaño que necesiten las filas y columnas |
| `guiWidth` | entero | ninguno | Ancho de esa imagen, obligatorio con `guiTexture` |
| `guiHeight` | entero | ninguno | Alto de esa imagen, obligatorio con `guiTexture` |
| `bauble` | texto | vacío | Solo para ítems: la ranura de Baubles en la que se puede llevar puesto: `amulet`, `ring`, `belt`, `trinket`, `head`, `body` o `charm`. Una mochila suele usar `body` o `charm`. Se ignora, y todo lo demás del ítem sigue funcionando, cuando Baubles no está instalado. Cada nombre es un solo hueco de la pestaña de Baubles, así que un ítem que pide `body` cabe en ese hueco y en ningún otro; `ring` son los dos huecos de anillo y `trinket` cabe en todos los huecos. Baubles es una dependencia blanda: este mod se carga después de él cuando está presente y funciona sin él cuando no lo está, así que un pack que nombra una ranura es seguro en un servidor que nunca ha oído hablar de Baubles. |

**Nueve filas por doce es el límite**, que es lo más grande que ofrece Iron Chest y lo máximo que cabe en una pantalla. Un pack que pida más se recorta a ese límite con una línea de error que lo indica. Una advertencia sobre el más alto: una pantalla de nueve filas mide 276 píxeles, y una pantalla de 1080 con la escala de GUI en `auto` da 270, así que arriba y abajo se recortan tres píxeles por lado; con la escala 3 se ve entera. Iron Chest cabe con nueve filas porque trae su propio arte, más compacto; un pack que quiera lo mismo puede definir `guiTexture` y dibujar el suyo.

**La pantalla se dibuja, no se incluye.** Un contenedor de nueve columnas o menos y seis filas o menos usa la pantalla del cofre de vanilla tal cual, así que se ve exactamente como un cofre de ese tamaño. Cualquier cosa mayor se ensambla a partir de la misma imagen al dibujar -- el borde superior, una fila de ranuras repetida hasta ajustarse y la parte inferior con el inventario del propio jugador --, de modo que un pack puede pedir tamaños que ninguna pantalla de vanilla cubre sin incluir una imagen propia. `guiTexture` anula todo eso cuando un pack quiere su propio aspecto, y entonces `guiWidth` y `guiHeight` deben indicar su tamaño; si no, se usa la dibujada y una línea de error lo indica.

**Qué hace el bloque.** Conserva su contenido al guardar y volver a cargar, lo suelta al romperse, responde a un comparador según lo lleno que esté y se puede renombrar en un yunque como un cofre. `chestModel` le da además el sonido de apertura del cofre y la animación de la tapa; si se deja desactivado, el bloque se dibuja con el modelo que nombre tu propio `modelBlock`, así que sirven igual una caja, un barril o un armario.

**Colorear un cofre.** La hoja del cofre es una textura normal, así que un mapa de píxeles puede recolorear la de vanilla sin dibujar un solo píxel: haz que la `extends` y dale un `tint`, y luego nombra ese mapa en `chestModel` y como `texture` del modelo.

```json
{
  "extends": "minecraft:textures/entity/chest/normal",
  "tint": {
    "from": "#241A12",
    "to": "#D8BC80"
  }
}
```

El bloque colocado y el ítem que llevas en la mano leen ese mismo nombre, así que coinciden. Si lo nombras solo en uno de los dos, el otro se queda con el marrón de vanilla.

**Un ítem contenedor se puede llevar puesto.** Dale `bauble` y, donde Baubles esté instalado, va en esa ranura y una tecla lo abre sin quitárselo: `V` por defecto, reasignable en los controles bajo Resource Data Pack Loader. `B` es la que Baubles asigna a su propia pestaña, así que las dos no comparten tecla. Al pulsarla de nuevo, con un contenedor puesto ya abierto, pasa al siguiente que lleves puesto y vuelve al principio, de modo que se puede llegar a todos aunque lleves varios a la vez. La tecla solo aparece cuando Baubles está presente, y todo lo demás del ítem, el clic derecho y su inventario, funciona tanto si lo está como si no. Baubles no tiene una ranura de mochila propia; `body` y `charm` son las dos que suele usar una mochila.

**La tabla de botín se llena al abrir por primera vez**, no al colocar el bloque, lo que lo hace útil en una estructura: quien lo abra primero se lleva el sorteo. La misma tabla se puede usar con `lootTable` en una forma de imprint o en una parcela de aldea, de modo que un pack puede colocar estos bloques mediante worldgen y llenarlos de la misma manera.

## Campanas

*bloques*

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

Y su blockstate, `assets/mypack/blockstates/village_bell.json`:

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

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `swing` | booleano | `true` | Dibuja el cuerpo con su propio modelo y lo balancea cuando la campana suena. `false` dibuja toda la campana como un único bloque quieto, sin nada animado |
| `sound` | nombre de sonido | `minecraft:block.note.bell` | Se reproduce cuando la campana suena. Vacío suena en silencio |
| `resonateSound` | nombre de sonido | `minecraft:block.note.chime` | Se reproduce cuando la campana resuena porque hay asaltantes cerca. Vacío resuena en silencio |

**Cuelga igual que la campana del propio juego desde la 1.14.** Colocada encima de un bloque se apoya en el suelo, girada hacia donde miras; bajo un bloque cuelga del techo; contra una pared cuelga de esa pared, y entre dos paredes cuando el lado opuesto también es sólido. Se cae cuando desaparece lo que la sostiene, y una campana entre dos paredes pasa a ser de una sola pared cuando una de ellas desaparece. Su caja de colisión sigue la de vanilla para cada uno de los cuatro casos, así que `bounds` no se lee.

**Qué la hace sonar.** Usar el lado del cuerpo, por debajo de la viga: en una campana de suelo, las dos caras que cruza su viga; en una de pared, las dos caras junto a la pared; en una de techo, cualquier lado. La parte superior, la inferior y cualquier cosa por encima del cuerpo no hacen nada. Una señal de redstone la hace sonar una vez al activarse, y una flecha, una bola de nieve o cualquier otro proyectil la hace sonar al golpear un lado que una mano podría alcanzar. El cuerpo se balancea alejándose del lado golpeado durante dos segundos y medio; la redstone lo balancea en la dirección hacia la que mira la campana.

**Qué hace un toque.** Los aldeanos a menos de 32 bloques lo oyen y se meten en casa durante quince segundos, hacia la puerta más cercana que conozca su aldea. Cuando hay un asaltante a menos de 32 bloques, la campana resuena un cuarto de segundo después del toque, y dos segundos más tarde todos los asaltantes a menos de 48 bloques brillan durante tres segundos, con partículas de colores junto a la campana en el lado en que se encuentra cada uno. Es asaltante todo lo que envía un [asalto](#asaltos), además de los illagers y las brujas del propio juego. Una campana de este tipo es una campana de aldea para todos los asaltos: suena cada vez que llega una oleada sin que haga falta nombrarla en el `bell` del asalto.

**Los modelos.** Una campana no tiene propiedad `blocks`, así que su blockstate se define por `facing` y `attachment`. Con `swing` activado, esos modelos dibujan solo el armazón, y la parte que se balancea es una entrada más llamada `body`, modelada en el espacio del bloque donde reposa; se inclina alrededor del punto situado medio bloque hacia dentro y tres cuartos de bloque hacia arriba, como la de vanilla. La entrada `inventory` es el ítem, con armazón y cuerpo juntos. Con `swing` desactivado no hay entrada `body`, y los cuatro modelos del armazón dibujan también la campana.

**El balanceo lo dibuja el cliente.** Un toque llega a los jugadores como un evento de bloque, así que un servidor dedicado balancea la campana para todos los que tengan este mod, y un jugador sin él solo la oye. Los sonidos, la resonancia y el brillo suceden todos en el servidor.

## Modelos, blockstates y texturas

*bloques e ítems*

Definir un bloque o un ítem lo registra. Cómo *se ve* sigue siendo un conjunto normal de archivos de recursos, en las mismas carpetas y con el mismo formato que ya usa Minecraft, bajo tu propio espacio de nombres.

```
assets/mypack/blockstates/ruby_ore.json
assets/mypack/models/block/ruby_ore.json
assets/mypack/models/item/ruby/ruby.json
assets/mypack/textures/blocks/ruby_ore.png
assets/mypack/lang/en_us.lang
```

### Nombrar las variantes

*modelos, blockstates y texturas*

Todo bloque con más de una variante recibe una propiedad llamada `blocks`, cuyos valores son los nombres de variante de la definición. Así, un archivo de bloque que registre `ruby_ore` y `deep_ruby_ore` necesita un blockstate con esas dos variantes:

```json
{
  "variants": {
    "blocks=ruby_ore": { "model": "mypack:ruby_ore" },
    "blocks=deep_ruby_ore": { "model": "mypack:deep_ruby_ore" }
  }
}
```

Un bloque con una sola variante conserva también la propiedad `blocks`, así que su clave sigue siendo `blocks=<name>`, pero solo en los tipos que tienen esa propiedad. Doce tipos gastan todos sus metadatos en su forma, tienen una sola variante y no llevan la propiedad `blocks`, así que se identifican únicamente por sus propias propiedades. [Blockstates por tipo](#blockstates-por-tipo) indica cuál es cuál.

Cuando el bloque tiene propiedades propias, se unen con comas en el orden en que las lista el estado: `blocks=ruby_log,axis=y`, `blocks=ruby_slab,half=bottom`, `blocks=ruby_wall,up=true,north=true`. Un bloque de escaleras no tiene propiedad `blocks`, así que se identifica por `facing=east,half=bottom,shape=straight` y nada más. Dos propiedades se dejan fuera a propósito: la propiedad de variante propia de un muro, y `check_decay` y `decayable` de un bloque de hojas, de modo que las hojas solo necesitan `blocks=ruby_leaves`. Un estandarte no tiene ninguna propiedad de variante, y se identifica por `rotation=0` hasta `15` si está de pie, o por `facing=north` en una pared, lo que cubre [Estandartes](#estandartes).

### Blockstates por tipo

*modelos, blockstates y texturas*

Dos cosas deciden lo que debe contener un archivo de blockstate: si el tipo lleva la propiedad `blocks` y qué propiedades tiene de por sí.

| Tipo | Registra | Propiedades del blockstate | Variantes |
| --- | --- | --- | --- |
| `basic`, `ore`, `falling` | un bloque | `blocks` | 16 |
| `flower` | un bloque | `blocks` | 16 |
| `portal` | un bloque | `blocks` | 16 |
| `fence`, `pane` | un bloque | `blocks`, `north`, `east`, `south`, `west` | 16 |
| `wall` | un bloque | `blocks`, `up`, `north`, `east`, `south`, `west` | 16 |
| `slab` | dos, `<name>` y `<name>_double` | la losa simple `blocks` y `half`; la doble solo `blocks` | 8 |
| `log` | un bloque | `blocks`, `axis`, que es `x`, `y`, `z` o `none` | 4 |
| `leaves` | un bloque | `blocks` | 4 |
| `stairs` | un bloque | `facing`, `half`, `shape` | 1 |
| `door` | un bloque | `facing`, `half`, `hinge`, `open` | 1 |
| `trapdoor` | un bloque | `facing`, `half`, `open` | 1 |
| `fence_gate` | un bloque | `facing`, `in_wall`, `open` | 1 |
| `banner` | dos, `<name>` y `<name>_wall` | `rotation` de pie, de `0` a `15`; el de pared `facing` | 1 |
| `ladder`, `torch` | un bloque | `facing`, y una antorcha añade `up` a las cuatro paredes | 1 |
| `bell` | un bloque | `facing` y `attachment`, que es `floor`, `ceiling`, `single_wall` o `double_wall`, más una entrada `body` para la parte que se balancea | 1 |
| `crop` | un bloque | `age`, siempre de `0` a `7`, diga lo que diga `maxAge` | 1 |
| `cane` | un bloque | `age`, de `0` a `15` | 1 |
| `sapling` | un bloque | `stage`, de `0` a uno menos que `stages` | 1 |
| `vine` | un bloque | `up`, `north`, `east`, `south`, `west`, y solo multipart | 1 |

Cuatro propiedades se eliminan por ti, así que escribe las claves sin ellas: `powered` en puertas y portones, `variant` en muros, y `check_decay` y `decayable` en hojas.

Un cultivo conserva los ocho valores de `age` de vanilla sea cual sea `maxAge`, ya que `maxAge` solo decide hasta dónde crece, así que su blockstate escribe siempre de `age=0` a `age=7`.

Dos tipos registran un segundo bloque. El `<name>_double` de una losa necesita su propio blockstate, definido por `blocks` sin `half`, y nunca recibe un ítem propio. El `<name>_wall` de un estandarte se explica en [Estandartes](#estandartes).

**Vine es el único tipo que no puede usar el formato de Forge**, ya que `forge_marker` no admite multipart, así que su blockstate es una lista `multipart` de vanilla normal con la textura incluida en el modelo.

**El formato de Forge es más corto, y es el que usa el pack de ejemplo.** Un blockstate de vanilla escribe cada combinación como una clave propia, que para las escaleras son cuarenta. Con `"forge_marker": 1` el archivo lista cada propiedad una sola vez y el juego las combina, de modo que esos mismos cuarenta estados son once entradas:

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

`defaults` se fusiona en cada entrada, un nombre de modelo sin más como `stairs` significa `minecraft:block/stairs`, e `inventory` es el modelo que usa el ítem que llevas en la mano. Los tres modelos padre de escaleras, `stairs`, `inner_stairs` y `outer_stairs`, aceptan las texturas `bottom`, `top` y `side`.

**Los tipos conectables añaden un submodelo por lado.** Una valla, un panel o un muro tiene un booleano por dirección, y un `true` pega otro modelo al poste en lugar de sustituirlo:

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

Los modelos padre son `fence_post` y `fence_side` con una `texture`; `wall_post` y `wall_side` con un `wall`, más `block` para el caso sin poste; y `pane_post`, `pane_side`, `pane_side_alt`, `pane_noside` y `pane_noside_alt` con un `pane` y un `edge`. Todos ellos necesitan `"uvlock": true`.

Los demás aceptan un único modelo padre. `cube_all` acepta un `all`, y es lo que quiere un bloque `basic`, `ore`, `falling` o `leaves`. `cube_column` acepta un `end` y un `side`, que es un `log`, girado según `axis`. `cross` acepta un `cross` y es lo que quiere un `flower`, `cane` o `sapling`; un `crop` usa sus propios modelos por etapa. Una losa necesita dos modelos propios, una mitad inferior y una superior, ya que se dibuja como una forma y no como un cubo.

**El pack de ejemplo es la referencia práctica.** [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) incluye una definición, un blockstate y modelos para cada tipo de la tabla anterior, tanto en el formato de vanilla como en el de Forge, así que una forma que no resulta obvia es más rápida de copiar que de deducir.

### Modelos de ítems

*modelos, blockstates y texturas*

Por defecto el ítem usa lo que el blockstate le dé a esa variante, así que no hace falta nada más. Si pones `"itemModel": "item"` en el bloque, buscará en su lugar su propio archivo, en `models/item/<block>/<variant>.json`.

Los ítems siempre funcionan de esta segunda manera, porque todo ítem de un pack tiene subtipos:

```
assets/mypack/models/item/ruby/ruby.json
assets/mypack/models/item/ruby/polished_ruby.json
```

La ruta es el nombre de registro del ítem y después el nombre de la variante.

Los fluidos no necesitan ningún modelo: se genera uno a partir de las texturas `still` y `flow`.

### Puertas, trampillas y puertas de valla

*modelos, blockstates y texturas*

Los tres gastan todos sus metadatos en la forma que adoptan, así que cada uno es una única variante, y cada uno tiene algunas cosas que conviene saber antes de escribir los archivos.

**No llevan propiedad `blocks`**, así que sus blockstates se identifican solo por la forma: `facing=east,half=lower,hinge=left,open=false` para una puerta, `facing=north,half=bottom,open=false` para una trampilla, `facing=south,in_wall=false,open=false` para una puerta de valla. Son 32 claves, 16 y 16.

**`powered` se omite en puertas y portones.** Ambos lo tienen de verdad, y de otro modo duplicarían sus blockstates por un eje que no cambia nada visible. Se elimina por ti, exactamente como el juego lo elimina de sus propias puertas y portones, así que escribe las claves sin él. Las trampillas nunca lo tuvieron.

**Apunta los modelos a los padres que aceptan texturas**, no a los de vanilla ya terminados:

| Tipo | Modelos padre |
| --- | --- |
| `door` | `block/door_bottom`, `block/door_bottom_rh`, `block/door_top`, `block/door_top_rh` |
| `trapdoor` | `block/trapdoor_bottom`, `block/trapdoor_top`, `block/trapdoor_open` |
| `fence_gate` | `block/fence_gate_closed`, `block/fence_gate_open`, `block/wall_gate_closed`, `block/wall_gate_open` |

Una puerta acepta dos texturas, `bottom` y `top`; las otras dos aceptan una, `texture`. Los dos modelos de la parte superior de la puerta usan `bottom` para cubrir su borde superior, así que declara ambas en los cuatro archivos aunque los de arriba parezcan necesitar solo una. Las variantes de puerta de valla necesitan `"uvlock": true`, como las del propio juego.

**Sus texturas usan todos los píxeles, y esta es la que pilla a la gente.** Las caras anchas de una puerta se mapean con `[0, 0, 16, 16]`, la imagen entera, y sus bordes estrechos y su parte superior e inferior salen del mismo cuadrado: las columnas de 0 a 3 para los lados, de 13 a 16 para arriba y abajo. Una trampilla es igual: sus caras planas son la imagen entera y sus cuatro cantos se toman de las filas de 13 a 16.

Así que no dejes ningún margen vacío. Si dejas libres unas columnas en un borde pensando que la forma es más estrecha que el archivo, abres una rendija de lado a lado en mitad de la cara y pierdes por completo la parte superior e inferior. Dibuja en su lugar el marco o los largueros en esos píxeles del borde, y se leerán como remate en los bordes del propio bloque.

**Sus ítems difieren según el tipo.** El de una puerta es un sprite plano, `item/generated` sobre su propio `textures/items/<name>.png`, ya que una puerta en la mano se dibuja como una imagen y no como una forma. El de una trampilla y el de una puerta de valla tienen como padre un modelo de bloque, la mitad inferior y la puerta cerrada, que es lo que hace el juego con los suyos.

Los tres aceptan el `material` que les des. Una puerta de valla se construye sobre un bloque que se fija a la madera, así que este mod devuelve el material al tuyo al registrarla, y una puerta de valla de piedra se mina con un pico como la piedra que dice ser.

### Estandartes

*modelos, blockstates y texturas*

Un estandarte es el único tipo en el que la forma del bloque y la forma del modelo se separan, así que merece explicarse por completo.

**Registra dos bloques.** Una definición te da el estandarte de pie con tu propio nombre y un segundo bloque llamado `<name>_wall` para el colgado. Ambos necesitan un blockstate; solo el de pie recibe un ítem, y ese ítem decide cuál de los dos coloca: de pie cuando haces clic en la parte superior de un bloque y de pared cuando haces clic en un lado. Nunca colocas directamente el bloque de pared y no necesita ítem propio.

**El de pie necesita un blockstate de Forge.** Su propiedad es `rotation`, que va de `0` a `15`, porque un estandarte gira en dieciseisavos y no en cuartos. Un blockstate de vanilla no puede expresarlo: su `y` pasa por `ModelRotation`, que solo acepta 0, 90, 180 y 270 y lanza una excepción con cualquier otro valor. El formato de Forge admite cualquier ángulo, así que las dieciséis entradas se escriben como una transformación:

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

…y así hasta `15`, cada una `-22.5` grados más allá. El signo coincide con el de los estandartes del propio juego, que giran en menos la rotación. Construye el modelo mirando al sur, ya que ahí es donde acaba apuntando un estandarte colocado por un jugador que mira al sur. El bloque de pared es un blockstate de vanilla normal con las cuatro entradas habituales de `facing` a 0, 90, 180 y 270, ya que no tiene nada de fraccionario.

**El modelo mide casi dos bloques de alto.** Un estandarte ocupa un bloque para la colocación y la colisión, pero se dibuja muy por fuera de él, y un modelo que se detiene en la parte superior de su propio bloque se ve raquítico. Conviene copiar exactamente las proporciones de vanilla, en dieciseisavos de bloque:

| Parte | Desde | Hasta |
| --- | --- | --- |
| Poste | `0` | `28` |
| Travesaño | `28` | `29.33` |
| Tela | `2.67` | `29.33` |
| Ancho de la tela | `1.33` | `14.67` |
| Tela de pared | `-13` | `13.67` |

Así que un estandarte de pie llega hasta `29.33`, casi dos bloques, y un estandarte de pared cuelga trece dieciseisavos *por debajo* del bloque que lo sostiene. Los elementos del modelo pueden ir de `-16` a `32`, de modo que ambos caben. La forma de pared no tiene poste ni travesaño, solo tela.

**La tela es el doble de alta que de ancha, y tu textura también tiene que serlo.** Esa cara mide `13.33` por `26.67`. Si mapeas una textura cuadrada sobre ella, el diseño se aplasta a la mitad de su altura. Las texturas de bloque no pueden ser el doble de altas que de anchas, ya que cualquier cosa no cuadrada se interpreta como una animación, así que la solución es una hoja cuadrada más grande con la tela en una parte de ella: un archivo de 32×32 que contenga la tela como una región de 16×32, direccionada como `"uv": [0, 0, 8, 16]`, con las tiras del poste y el travesaño en el espacio contiguo. Las coordenadas UV siempre van de 0 a 16 sea cual sea la resolución del archivo, así que los mismos números sirven con cualquier tamaño.

**Su ítem necesita un modelo propio.** Un ítem que hereda un modelo tan alto se saldrá de su ranura a la escala habitual de bloque, así que dale a `models/item/<name>.json` un bloque `display` propio con la escala reducida y todo el conjunto trasladado de vuelta al encuadre.

**No tiene colores ni patrones.** Un estandarte de pack no tiene entidad de bloque, así que nada guarda la lista de capas que los estandartes de vanilla conservan en la suya. El diseño es la textura, igual que el aspecto de una puerta es su textura, y una definición es un estandarte. Teñirlo y apilarle patrones no es algo a lo que un pack pueda llegar.

**Acepta el `material` que le des.** El bloque sobre el que se construye se fija a la madera, así que este mod devuelve el material al tuyo al registrarlo, y un estandarte de piedra se mina con un pico como la piedra que dice ser.

### Texturas escritas como mapas de píxeles

*modelos, blockstates y texturas*

Una textura puede ser un archivo JSON en lugar de un PNG. Colócalo donde habría ido el PNG, con `.json` añadido al final del nombre completo, de modo que `textures/blocks/panel.png.json` responde a toda petición de `textures/blocks/panel.png`. Nada más cambia: los modelos apuntan a `mypack:blocks/panel` como siempre, y el atlas, los mipmaps y un `.mcmeta` de animación funcionan, porque lo que recibe el juego sigue siendo un PNG.

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

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `size` | sí, o heredada | `anchoxalto` | | Cuántos píxeles de ancho y de alto |
| `rows` | sí, o heredadas | lista de texto | | Una cadena por fila de píxeles, un carácter por píxel, de arriba abajo |
| `palette` | sí, o heredada | objeto | | Un carácter para cada color, `#RRGGBB` o `#AARRGGBB` |
| `extends` | no | otro mapa de píxeles | | El mapa del que parte este |
| `tint` | no | objeto con `from` y `to` | | Recolorea todo lo heredado a lo largo de una rampa entre dos colores |
| `notes` | no | objeto | | Un carácter con una línea que dice para qué sirve, heredada y nunca dibujada |

**No hay ningún nombre que declarar.** La propia ruta del archivo es su nombre, exactamente como la de un PNG, así que un mapa en `assets/mypack/textures/blocks/panel.png.json` es `mypack:blocks/panel` en un modelo, y un mapa en `assets/mypack/textures/items/gem.png.json` es `mypack:items/gem` en un modelo de ítem. Nada apunta a un mapa de píxeles de forma especial; un bloque o un ítem nombra su textura como siempre y nunca llega a saber cuál de las dos recibió. Eso significa también que las carpetas de bloques y de ítems siguen separadas, como con los PNG: `textures/blocks/gem.png.json` y `textures/items/gem.png.json` son dos texturas distintas y se guardan en caché como dos archivos distintos.

**Cualquier tamaño**, hasta 4096 por lado, y los dos lados no tienen por qué coincidir. `16x16` es una cara de bloque normal, `16x32` es el tipo de tira alta que quiere la mitad de una puerta o una animación. El tamaño se comprueba en lugar de adivinarse: da una fila por cada línea de píxeles y un carácter por cada píxel de ancho, o el mapa se rechaza y el registro indica la fila y lo que encontró. Un carácter sin color en la paleta se deja transparente, así que `.` o un espacio es un hueco.

**Las plantillas son lo importante.** `extends` nombra otro mapa de píxeles, como `namespace:path` o una ruta sin más dentro del mismo pack, y el archivo que lo extiende hereda su `size`, sus `rows` y su `palette`. Lo que nombre por sí mismo prevalece, y no necesita nombrarlo todo, así que una variante entera puede ser un puñado de colores:

```json
{
  "extends": "mypack:textures/blocks/panel.png",
  "palette": { "s": "#AA7EB1", "d": "#8B6292", "e": "#6B4A72", "p": "#C5A1CB" }
}
```

Eso es una segunda textura completa: la misma forma en púrpura, y si algún día se redibuja la forma en la plantilla, todas las variantes la siguen. Una variante puede en cambio dar sus propias `rows` y conservar la paleta de la plantilla, que es lo contrario: los mismos colores con un patrón distinto. La herencia llega hasta ocho niveles, un bucle se detecta y se informa, y un mapa que nombra una plantilla que nada proporciona se informa en lugar de dibujarse en blanco.

**Cuál de dos texturas es la plantilla** lo decide cuál contiene más distinciones, no cuál se dibujó primero. Una variante da un color a cada carácter, así que todo píxel que la plantilla llama con el mismo carácter sale del mismo color en la variante. Por eso un mineral dibujado sobre piedra no puede heredar las `rows` de la piedra: la piedra llama piedra normal a las posiciones de las motas, y nada de lo que pueda escribir una variante separa un carácter en dos. Dale la vuelta y funciona. Haz que el mineral sea la plantilla, de modo que los tonos de piedra y los del mineral tengan cada uno sus propios caracteres, y un segundo mineral son cuatro colores:

```json
{
  "extends": "mypack:textures/blocks/ore_template",
  "palette": { "4": "#768291", "5": "#5E6977", "6": "#66717F", "7": "#848F9D" }
}
```

Una variante que de verdad quiera un patrón distinto da sus propias `rows`, como más arriba, y entonces hereda solo la paleta. Merece la pena cuando lo importante son los colores y la forma es incidental; cuando lo importante es la forma, pon la forma en la plantilla y deja que las variantes nombren los colores.

**Una plantilla no tiene por qué ser una textura.** Un mapa solo se sirve al juego cuando su ruta termina en `.png`, así que una plantilla en `textures/blocks/ore_template.json` es invisible para el juego y existe únicamente para ser extendida, mientras que una en `textures/blocks/ore_template.png.json` respondería también a las peticiones de `ore_template.png`. Nombra una forma compartida sin el `.png` y nada podrá pedirla por accidente.

**Una plantilla puede ser una imagen real en lugar de un mapa.** Apunta `extends` a un PNG que proporcione cualquier pack o el propio juego y la paleta cambia de significado: las claves pasan a ser los colores que ya hay en esa imagen y los valores, los colores que se ponen en su lugar. No se traza nada y no se escriben `rows`, así que un pack puede recolorear una textura de vanilla o de un mod donde está:

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

Eso es un mineral de rubí sobre la propia piedra de vanilla: los cuatro tonos de las motas se cambian y todos los demás píxeles se dejan como estaban. Un color que la imagen no contiene simplemente nunca coincide, y el tamaño sale de la imagen a menos que indiques uno, que entonces debe concordar.

`extends` prefiere un mapa de píxeles: busca primero el mapa en esa ruta y solo recurre a la imagen cuando ningún pack lo proporciona. Un nombre que no es ni lo uno ni lo otro se informa en lugar de dibujarse en blanco. Construir sobre una imagen es trabajo del lado del cliente, ya que se leen los recursos del propio juego, así que un servidor dedicado nunca lo hace.

**Una plantilla puede teñirse en lugar de repintarse.** `tint` nombra dos colores y recolorea todo lo que el mapa hereda a lo largo de la rampa entre ellos. El brillo de cada color heredado es su posición en esa rampa, de modo que el negro cae en `from`, el blanco cae en `to` y cada tono intermedio se mezcla en proporción. La transparencia no se toca. Eso hace que una plantilla en escala de grises más dos colores sea una variante completa:

```json
{
  "extends": "mypack:textures/items/materials/ingot.png",
  "tint": { "from": "#626669", "to": "#DBDFE2" }
}
```

`from` puede omitirse, en cuyo caso es negro y el tinte se convierte en una multiplicación normal, con la misma forma que un `tintindex` en el momento de renderizar. La diferencia es que este se dibuja una sola vez en el PNG y se guarda en caché, así que no cuesta nada por fotograma y llega a una textura que nada tiñe, pero tampoco puede seguir a un bioma como lo hacen `grass` o `foliage`.

La plantilla sigue siendo un mapa normal: ábrela, mírala y se dibuja con el gris que es. Ambos colores aceptan `#RRGGBB`, `#AARRGGBB` o un `0x` inicial, y un valor que no sea ninguno de ellos deja el mapa sin dibujar en lugar de dibujarlo con el color equivocado. Un tinte se hereda como todo lo demás y gana el primero de la cadena, así que el tinte propio de una variante prevalece sobre el del mapa que extiende. También funciona con una plantilla de imagen, donde se aplica después de los cambios de color de la paleta.

**Un tinte es una rampa entre dos colores**, así que solo sirve para una textura cuyos tonos estén en una. Una forma con dos regiones sin relación, como la piedra de un mineral frente a sus motas, no lo es, y necesita en cambio su paleta escrita por completo.

**Saber qué significan los caracteres de una plantilla** es la parte incómoda de extender una, y para eso está el bloque `notes` de arriba: un carácter con una línea breve, heredada igual que la paleta y nunca dibujada. Etiqueta los caracteres de una plantilla y quien la extienda sabrá cuáles sobrescribir.

`/rdpl pixelmap <namespace:path>` informa entonces de cómo quedó realmente un mapa, que es la manera fiable de escribir una variante sin abrir todos los archivos de la cadena:

```
oretest:textures/blocks/ruby_ore.png is 16x16
  built from oretest:textures/blocks/ruby_ore.png.json
  built from oretest:textures/blocks/gem_ore.png.json
  rows come from oretest:textures/blocks/gem_ore.png.json
  1  #C4353F  17 pixel(s)  set by ruby_ore.png.json  ore body, most of every lump
  2  #8E2029   8 pixel(s)  set by ruby_ore.png.json  ore shadow, the darkest tone
  a  #747474  86 pixel(s)  set by gem_ore.png.json   stone, the commonest tone
```

Cada carácter aparece con su color, cuántos píxeles cubre, qué archivo de la cadena lo definió y para qué dice ese archivo que sirve. La ruta puede darse en la forma corta, `mypack:blocks/panel`, o completa. Un carácter que muestra 0 píxeles es uno que la paleta nombra y que las filas nunca usan, lo que suele ser un error tipográfico en una fila.

**Las imágenes dibujadas se guardan en disco** en `rdploader/pixelmap-cache`, bajo una carpeta por espacio de nombres y con el nombre de la textura seguido de un hash de su origen. El hash abarca toda la cadena, el propio mapa y cada plantilla por encima de él, así que editar una plantilla cambia la marca de todas las variantes que heredan de ella y todas se redibujan. Cuando un mapa se redibuja, sus archivos anteriores se eliminan.

La carpeta también se recorre cada vez que se examinan los packs, y se elimina toda imagen cuyo mapa ya no proporcione ningún pack, junto con cualquier carpeta que quede vacía. Renombra una textura, quita un pack o borra un mapa y su imagen en caché desaparece con él en lugar de quedarse ahí para siempre. Borrar la carpeta entera no cuesta más que el tiempo de volver a dibujarlas, y se omite al examinar los packs, así que nunca se confunde con un pack.

Un PNG siempre gana. Si existen `panel.png` y `panel.png.json`, se sirve el PNG y el mapa nunca se dibuja, así que una textura generada se puede sustituir más adelante por una pintada sin cambiar nada de lo que apunta a ella.

**Nadie tiene que escribir estos archivos a mano.** El repositorio incluye scripts para todo el proceso en [`pixelmap/`](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/tree/1.12.2-1.0-Release/pixelmap): `png_to_pixelmap.py` convierte un PNG en un mapa, `convert_pack.py` lo hace con todas las texturas que contiene un pack, y `verify_pack.py` dibuja los mapas de un pack convertido y los compara con los PNG de los que salieron, de modo que se puede confiar en una conversión antes de apartar los originales.

### Trampas que conviene conocer

*modelos, blockstates y texturas*

**Un blockstate que nombra un modelo de vanilla sin más hereda también las texturas de vanilla.** `normal_torch`, `ladder`, `wooden_door_*` y `wheat_stage*` llevan todos sus propias texturas, así que un bloque que apunte a uno de ellos obtiene el aspecto de vanilla pongas lo que pongas en el blockstate. Los modelos padre como `cube_all`, `cross` y `block/crop` toman sus texturas del blockstate y se portan bien, igual que los modelos padre de puerta, trampilla y puerta de valla indicados en [Puertas, trampillas y puertas de valla](#puertas-trampillas-y-puertas-de-valla).

**`forge_marker: 1` no admite multipart.** El blockstate de una enredadera tiene que ser un multipart de vanilla normal, con las texturas incluidas en el modelo en lugar de pasadas desde fuera.

**Los nombres salen del archivo de idioma, y un bloque necesita DOS.** Un bloque o un ítem muestra una clave en bruto hasta que `lang/en_us.lang` le da un nombre. El ítem que sostienes y colocas se identifica por el nombre de registro del bloque seguido de la variante, `tile.mypack:ruby_ore.ruby_ore.name=Ruby Ore`, y esa es la que más packs recuerdan. El BLOQUE en sí se identifica solo por el nombre de registro, `tile.mypack:ruby_ore.name=Ruby Ore`, y es lo que lee cualquier cosa que le pida su nombre al bloque colocado, como el título de la pantalla de un contenedor. Escribe las dos, o el ítem se leerá bien en tu mano mientras que la pantalla que abre tendrá el título en blanco.

**Los tipos de variante única se nombran dos veces.** Un bloque que puede contener varias variantes se identifica solo por su nombre de registro, como arriba. Un bloque cuyos metadatos van todos a su forma añade detrás el nombre de la variante, así que una puerta definida en `blocks/my_door.json` con una variante llamada `my_door` es `tile.mypack:my_door.my_door.name=My Door`. Eso abarca `door`, `trapdoor`, `fence_gate`, `banner`, `stairs`, `ladder`, `torch`, `crop`, `cane`, `sapling` y `vine`. Cuando un tipo así tiene un ítem propio, como una puerta y un estandarte, necesita la misma clave otra vez bajo `item.` en lugar de `tile.`.

## Hacer que vanilla trate bien tu bloque

*bloques e ítems*

Vanilla comprueba sus propios bloques por identidad en una docena de sitios, así que un bloque de pack que obviamente debería funcionar a menudo no lo hace. Dos claves lo resuelven.

```json
{
  "material": "ground",
  "plantTypes": ["Plains", "Crop"],
  "behavesAs": ["till", "path"],
  "variants": { "ruby_grass": { "meta": 0, "hardness": 0.6 } }
}
```

**`plantTypes`** lista los tipos de planta de Forge que admite tu bloque, de modo que se puedan plantar en él brotes, cultivos y flores.

**`behavesAs`** hace que vanilla trate tu bloque como uno de los suyos:

| Valor | Qué hace |
| --- | --- |
| `till` | Una azada lo convierte en tierra de cultivo |
| `path` | Una pala lo convierte en un camino de tierra |
| `bush` | Se pueden plantar en él flores, hierba y brotes, y se mantienen, como en la tierra. Equivale a `plains` en `plantTypes` |
| `animals` | Los animales aparecen en él con luz, como en la hierba |

## Ítems

*bloques e ítems*

`<namespace>/items/*.json`

La ruta del archivo es el nombre de registro del ítem, así que `mypack/items/ruby.json` registra `mypack:ruby`. Las claves dentro de `variants` nombran los valores de metadatos de ese ítem, y el modelo de cada una va en `models/item/ruby/<key>.json`.

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita. Una clave marcada para un tipo solo la lee ese tipo.

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

### Tipos de ítem

*ítems*

| Tipo | Qué obtienes |
| --- | --- |
| `basic` | Un ítem normal. Se usa cuando falta `type` |
| `food` | Se come, con hambre y saturación |
| `drink` | Se bebe en lugar de comerse, y devuelve un recipiente vacío |
| `tool` | Pico, hacha, pala o espada a partir de un material |
| `armor` | Casco, peto, pantalones o botas a partir de un material |
| `seed` | Planta uno de tus cultivos |
| `potion` | Aplica los efectos de tu poción al usarse |
| `potion_bottle` | Contiene tus tipos de poción y los muestra en una pestaña creativa |
| `rocket` | Coloca una de tus variantes de cohete de Galacticraft en una plataforma de lanzamiento |

Un `potion_bottle` lista lo que puede contener con `potionTypes`, una matriz de nombres de tipos de poción como `["mypack:ruby_tonic"]`. Uno con una lista vacía no registra nada, y el registro lo indica.

Un `rocket` nombra con `rocket` la variante de entidad que coloca. Necesita Galacticraft, y sin él o sin `rocket` no registra nada, y el registro lo indica. Consulta los cohetes de Galacticraft en las variantes de entidades.

### Claves de archivo de ítems

*ítems*

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `variants` | sí | objeto de nombre de variante a variante | | Una entrada por valor de metadatos. La clave da nombre a ese valor en el blockstate, la ruta del modelo y la clave de idioma. El nombre de registro sale de la propia ruta del archivo |
| `type` | no | uno de los tipos anteriores | `basic` | Qué tipo adopta el ítem |
| `creativeTab` | no | nombre de pestaña | ninguna | La pestaña en la que aparece |
| `material` | tool, armor | nombre de material | ninguno | De cuál de tus materiales está hecho |
| `toolClass` | tool | `pickaxe`, `axe`, `shovel`, `sword` | ninguna | Qué herramienta es |
| `slot` | armor | `head`, `chest`, `legs`, `feet` | ninguna | Dónde se lleva puesto. También valen `helmet`, `chestplate`, `leggings` y `boots` |
| `eat` | food | booleano | `false` | Usa la animación de comer |
| `alwaysEdible` | food | booleano | `false` | Se puede comer con la barra de hambre llena |
| `useDuration` | no | entero, ticks | `32` | Cuánto tarda en usarse |
| `attackSpeed` | no | decimal | según la clase de herramienta | Para `tool`, el atributo de velocidad de ataque, como el `-2.4` de una espada |
| `cooldown` | no | entero, ticks | `0` | Para `food`, `drink` y `potion`, cuánto tiempo rechaza el ítem volver a usarse tras consumirse; en un ítem con `rolls`, el tiempo entre tiradas |
| `container` | drink | nombre de ítem | ninguno | Lo que queda atrás, como una botella |
| `crop` | seed | nombre de bloque | ninguno | El cultivo que planta |
| `soil` | seed | nombre de bloque | `minecraft:farmland` | Sobre qué se puede plantar |
| `rocket` | rocket | `namespace:name` | ninguno | La variante de entidad que coloca |
| `rolls` | no | `coin`, `d6`, `2d6+1`, un dado o un mazo | ninguno | En un ítem simple, un clic derecho tira como lo haría `/rdplserver game` y se lo dice a la audiencia por defecto del pack. Consulta [Dados y mazos](#dados-y-mazos) |
| `passesTurn` | no | booleano | `false` | En un ítem simple, un clic derecho pasa el turno de quien lo sostiene, como hace `/rdplserver game pass`. Consulta [Turnos](#turnos) |
| `requires` | no | lista de ids de mods o espacios de nombres de packs | ninguno | El archivo se omite a menos que estén presentes todos |

### Claves de variantes de ítems

*ítems*

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `meta` | sí | de 0 a 15 | | El valor de metadatos que reclama esta variante |
| `maxSize` | no | de 1 a 64 | `64` | Tamaño de la pila |
| `rarity` | no | `common`, `uncommon`, `rare`, `epic` | `common` | Color del nombre en la descripción emergente |
| `healAmount` | food | entero, medios muslos | `0` | Hambre que restaura |
| `saturation` | food | decimal | `0.0` | Saturación que restaura |
| `oreDict` | no | lista de nombres del diccionario de minerales | ninguna | Nombres del diccionario de minerales bajo los que se registra esta variante |
| `potion` | food, drink | `potion,duration,amplifier` | ninguna | Un efecto que se aplica cuando se come o se bebe la variante. Una cuarta parte, `true`, lo hace ambiental. Un efecto beneficioso se nombra en la descripción emergente |

## Fluidos

*bloques e ítems*

`<namespace>/fluids/*.json`

La ruta del archivo es el nombre de registro del fluido, a menos que `name` lo anule.

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

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `name` | no | cadena | el nombre del archivo | El nombre de registro del fluido |
| `still` | no | ruta de textura | agua quieta de vanilla | Textura del fluido quieto |
| `flow` | no | ruta de textura | agua en movimiento de vanilla | Textura del fluido en movimiento |
| `color` | no | color hexadecimal | ninguno | Tinte aplicado a esas texturas |
| `bucket` | no | booleano | `true` | Registra un cubo para él |
| `luminosity` | no | de 0 a 15 | `0` | Luz que emite |
| `density` | no | entero | `1000` | Un valor negativo flota hacia arriba, como un gas |
| `temperature` | no | entero, kelvin | `300` | El agua es 300, la lava 1300 |
| `viscosity` | no | entero | `1000` | Con qué lentitud fluye. El agua es 1000, la lava 6000 |
| `gaseous` | no | booleano | `false` | Se trata como un gas |
| `creativeTab` | no | nombre de pestaña | ninguna | La pestaña en la que aparece el cubo |
| `block` | no | objeto | | El bloque del fluido. `material` (`water`), `flammability` (`0`), `fireSpread` (`0`), `quantaPerBlock` (`0`), `potions` (ninguno, una lista de efectos que se aplican a todo lo que esté dentro, cada uno escrito `potion,duration,amplifier` con una cuarta parte opcional `true` para uno ambiental) |
| `requires` | no | lista de ids de mods o espacios de nombres de packs | ninguno | El archivo se omite a menos que estén presentes todos |

## Materiales, pestañas, sonidos, diccionario de minerales

*bloques e ítems*

`<namespace>/materials/*.json`

La ruta del archivo es el nombre del material, que un ítem de herramienta o armadura nombra después en `material`.

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

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `harvestLevel` | no | de 0 a 3 | `1` | Nivel de la herramienta. 0 madera, 1 piedra, 2 hierro, 3 diamante |
| `durability` | no | entero | `250` | Usos antes de romperse |
| `efficiency` | no | decimal | `6.0` | Velocidad de minado. El diamante es 8 |
| `damage` | no | decimal | `2.0` | Bonificación de daño de ataque |
| `enchantability` | no | entero | `14` | Qué tan buenos son los encantamientos. El oro es 22 |
| `repairItem` | no | nombre de ítem | ninguno | Lo que lo repara en un yunque |
| `reduction` | no | lista de cuatro enteros | | Puntos de armadura, en el orden botas, pantalones, peto, casco |
| `toughness` | no | decimal | `0.0` | Dureza de la armadura, como la tiene el diamante |
| `equipSound` | no | nombre de sonido | `item.armor.equip_iron` | Sonido al ponerse la armadura |
| `armorTexture` | no | prefijo de textura | el nombre del archivo | La textura de la armadura puesta |

### Pestañas creativas

*materiales, pestañas, sonidos, diccionario de minerales*

`<namespace>/tabs/*.json`

La ruta del archivo es el nombre de la pestaña, a menos que `label` lo anule, y los bloques e ítems la nombran en `creativeTab`.

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `label` | no | cadena | el nombre del archivo | El id de la pestaña: los bloques e ítems la nombran en `creativeTab`, y el nombre que se muestra sale de `itemGroup.<label>` en los archivos de idioma |
| `icon` | no | nombre de ítem | ninguno | El ítem que se muestra en la pestaña |

### Sonidos

*materiales, pestañas, sonidos, diccionario de minerales*

`<namespace>/sounds/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

El formato `sounds.json` de vanilla, de modo que un pack puede incluir su propio audio.

### Diccionario de minerales

*materiales, pestañas, sonidos, diccionario de minerales*

`<namespace>/oredict/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

Añade nombres del diccionario de minerales a ítems que ya existen. Cada clave es un nombre del diccionario de minerales y su valor los ítems registrados bajo él, así que un archivo no tiene claves fijas propias. Los bloques e ítems propios de un pack nombran las suyas en el `oreDict` de la variante.

Una clave que empieza por `-` elimina en lugar de añadir: `"-ingotCopper": ["thermalfoundation:material:128"]` quita ese ítem del nombre, y `["*"]` vacía el nombre. Las recetas que usaban el nombre dejan de reconocer el ítem al instante, que es lo que se busca. Un nombre que nada registra se rechaza con un error, y también un ítem que el nombre no contiene. Una entrada registrada para todos los metadatos, como vanilla registra `plankWood`, se elimina entera sea cual sea el metadato que nombres, y el registro lo indica; nómbrala con `:*` para decir lo mismo con claridad.

```json
{
  "_note": "ruby equivalents",
  "gemRuby": ["mypack:ruby", "mypack:polished_ruby:1"],
  "oreRuby": ["mypack:ruby_ore", "minecraft:redstone_ore"]
}
```

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| un nombre del diccionario de minerales | sí | lista de nombres de ítems | | Los ítems registrados bajo él. Los metadatos como tercera parte, `"mypack:ruby:1"` |
| un nombre que empieza por `_` | no | cualquier cosa | | Se omite, de modo que un archivo puede llevar una nota para sí mismo |

## Sobrescritura de propiedades

*bloques e ítems*

`<namespace>/overrides/<target>/<name>.json`

La ruta nombra el objetivo: todo lo que sigue a `overrides/` es el espacio de nombres y el nombre del bloque, ítem o tipo de poción que se modifica.

En cualquier otro sitio, un pack sustituye un archivo o añade uno. Una sobrescritura no hace ninguna de las dos cosas: cambia las propiedades de un bloque, ítem o tipo de poción que ya existe, de vanilla o de un mod, sin tocar ninguno de sus archivos. La ruta nombra el objetivo, así que `overrides/minecraft/stone.json` cambia `minecraft:stone`, y `overrides/tconstruct/<name>.json` cambia igual el bloque de ese mod.

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita.

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

### Propiedades de bloques

*sobrescritura de propiedades*

Todas las claves son opcionales y un archivo cambia solo lo que nombra, así que un archivo en `overrides/minecraft/stone.json` que contenga únicamente `hardness`, `light` y `soundType` hace que la piedra se mine casi al instante, brille y suene como el cristal. Un archivo puede llevar claves de bloque, de ítem y de poción a la vez. Estas se aplican cuando el objetivo es un bloque:

| Clave | Valor | Qué hace |
| --- | --- | --- |
| `hardness` | decimal | Tiempo de minado, la misma cifra que acepta una definición de bloque |
| `resistance` | decimal | Resistencia a las explosiones |
| `slipperiness` | decimal | `0.6` es suelo normal, `0.98` es hielo |
| `light` | de `0` a `15` | Luz que emite |
| `lightOpacity` | de `0` a `255` | Cuánta luz detiene el bloque |
| `soundType` | uno de los tipos de sonido | Sonidos de pisada, colocación y rotura |
| `harvestTool` | clase de herramienta | Lo que lo mina; `harvestToolLevel`, por defecto `0`, fija el nivel |
| `flammability` | entero | Con qué facilidad se consume al arder; `fireSpread`, por defecto `5`, con qué facilidad le llega el fuego |

### Propiedades de ítems

*sobrescritura de propiedades*

Y estas cuando el objetivo es un ítem:

| Clave | Valor | Qué hace |
| --- | --- | --- |
| `maxStackSize` | de `1` a `64` | Tamaño de la pila |
| `maxDamage` | entero | Durabilidad |
| `containerItem` | nombre de ítem | Se queda en la cuadrícula de fabricación, como un cubo |
| `food` | objeto | Hace que el ítem sea comestible, véase más abajo |

Un nombre que es a la vez un bloque y un ítem, y el ítem de todo bloque colocable lo es, toma ambos grupos de un solo archivo:

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

En `overrides/minecraft/planks.json` eso hace que las tablas se rompan casi tan rápido como la tierra y que se puedan comer. `food` acepta `heal` (`1`), `saturation` (`0.6`), `alwaysEdible` (`false`; `true` permite comer con la barra de hambre llena) y `effects`, cuyas entradas se escriben exactamente como las de un tipo de poción. Un ítem que ya es comida acepta nuevos `heal`, `saturation` y `alwaysEdible`; `effects` en uno de esos no está admitido, y el registro lo indica. Cuando el ítem comestible coloca un bloque, apunta al cielo para comer, ya que apuntar a un bloque lo coloca: ese es el orden de uso de vanilla, no un error.

### Efectos de los tipos de poción

*sobrescritura de propiedades*

`effects` en el nivel superior del archivo reescribe por completo la lista de efectos de un tipo de poción:

```json
{
  "effects": [
    { "potion": "minecraft:levitation", "duration": 200, "amplifier": 0 }
  ]
}
```

En `overrides/minecraft/swiftness.json` la poción de velocidad ahora otorga Levitación. Cada entrada acepta `potion` (obligatorio), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) y `showParticles` (`true`), igual que en `potion_types/`, y la lista no puede estar vacía.

### Otros mods, recargas y límites

*sobrescritura de propiedades*

Un objetivo que pertenece a otro mod debe llevar ese mod en `requires`, de modo que el archivo se omita sin ruido cuando el mod no está instalado en lugar de informarse como un objetivo inexistente:

```json
{
  "requires": ["tconstruct"],
  "hardness": 1.0
}
```

Las sobrescrituras son en vivo. Los valores originales se recuerdan antes del primer cambio, así que al desactivar el pack y ejecutar `/rdpl reload` todo vuelve a como estaba, sin reiniciar; lo mismo ocurre en cada entrada a un mundo. Un archivo por objetivo: cuando dos packs sobrescriben lo mismo, el archivo del pack posterior sustituye al anterior por completo, y el registro lo indica.

Dos límites que conviene conocer. Un bloque o ítem cuyo propio código calcula una propiedad ignora el campo que hay detrás, de modo que la sobrescritura se aplica pero no cambia nada; vanilla solo hace esto con la resistencia a las explosiones de las escaleras, pero los mods son libres de hacerlo en cualquier parte. Y los ítems hechos comestibles solo funcionan con ítems sin comportamiento propio al hacer clic derecho: un ítem que ya hace algo al usarse sigue haciéndolo.

Las sobrescrituras necesitan el pack tanto en el cliente como en el servidor, ya que la velocidad de minado, la luz y el comer suceden en la pantalla del jugador, así que no son para packs del lado del servidor. `overrides` en la categoría de configuración `content` desactiva la carpeta por completo.

## Grupos de dureza

*bloques e ítems*

`<namespace>/hardness/*.json`

La ruta del archivo nombra el grupo en el registro y nada más la lee, así que varios archivos se acumulan.

Da a un grupo de bloques un multiplicador del tiempo de minado, sorteado por posición de bloque. El bloque en sí nunca se modifica: no se registra nada, no se escribe nada en el mundo, y un mundo abierto sin el pack es vanilla normal.

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

### Minería y voladuras

*grupos de dureza*

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `blocks` | sí | lista de nombres de bloques u objetos | | El grupo. Las mismas tres formas que un `replace` de worldgen |
| `except` | no | lista de nombres de bloques u objetos | ninguno | Se retira del grupo, diga lo que diga `blocks` |
| `miningTime` | no | número, u objeto con `min` y `max` | `1.0` | Cuántas veces más tarda en romperse el bloque, tanto para un jugador como para un mob con `digs` |
| `blastResistance` | no | número, u objeto con `min` y `max` | `1.0` | Multiplica la resistencia a las explosiones del bloque |
| `buckets` | no | de 1 a 256 | `10` | En cuántos pasos se divide el rango |
| `minHeight` | no | entero | `0` | Por debajo de esto el sorteo da el paso más duro |
| `maxHeight` | no | entero | `255` | Por encima de esto el sorteo da el paso más duro |
| `field` | no | objeto | véase más abajo | La forma en que se agrupa el sorteo |
| `requires` | no | lista de ids de mods o espacios de nombres de packs | ninguno | El archivo se omite a menos que estén presentes todos |

Un solo número da a todos los bloques del grupo el mismo multiplicador, y no se sortea nada. Un `min` y un `max` se sortean por posición: `max` donde el campo está vacío, `min` en el centro de un grupo, y los pasos intermedios los decide `buckets`.

### Minería de aventura y desbloqueos

*grupos de dureza*

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `keeps` | no | booleano | `false` | El bloque se queda donde está cuando se mina: los drops, la experiencia, el desgaste de la herramienta y el sonido de rotura suceden y el bloque sigue ahí para volver a minarse, de modo que el grupo es una veta interminable al ritmo que fije `miningTime`. El modo creativo lo quita como siempre |
| `adventure` | no | objeto | ninguno | Quién puede romper el grupo en modo aventura, donde de otro modo no se rompe nada. `tools` lista los ítems de los que uno debe estar en la mano, vacío para cualquier cosa que se sostenga; `teams`, `players` y `entities` indican quién, un equipo por su nombre, un jugador por su nombre, un mob por su id de entidad para la tarea `digs`, y los tres vacíos significa cualquiera con la herramienta. Supervivencia y creativo no se tocan |
| `advancement` | no | `namespace:path` | ninguno | El grupo cuenta para un jugador solo cuando tiene ese progreso. Dos grupos pueden nombrar el mismo bloque, uno con un progreso y otro sin él, y gana el desbloqueado; un jugador que no lo tiene recibe el grupo normal, o vanilla si no hay ninguno. Los mobs no tienen progresos, así que un grupo restringido nunca llega a una tarea `digs`, y la resistencia a las explosiones y el sorteo de textura, que no pertenecen a ningún jugador, proceden del grupo normal |
| `becomes` | no | objeto | ninguno | Los bloques del grupo se convierten en otro bloque, en todo el mundo, en el momento en que cualquier jugador consigue `advancement`: `{ "advancement": "mypack:deep_miner", "block": "mypack:rich_ore" }`. Todos los chunks cargados se recorren al instante, un chunk que se cargue más tarde se recorre al entrar, y un chunk que se cree más tarde se recorre justo después de colocar su mineral, de modo que el bloque antiguo desaparece para siempre. Dale al nuevo bloque un grupo propio para cambiar cómo se mina |

### El campo

*grupos de dureza*

El sorteo no se hace para cada bloque completamente por separado, o lo duro y lo blando serían puro ruido sin forma alguna. `field` decide qué forma toma, y `type` elige entre dos maneras de lograrlo.

```json
{
  "field": { "type": "speckle" }
}
```

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `type` | no | `speckle` o `seeded` | `speckle` | Cuál de las dos siguientes se usa |

#### speckle

*el campo*

Cada bloque saca su propio paso, y un bloque situado a una cara de distancia puede pasarle un paso más débil. Eso da motas densas y de grano fino, la mayoría de un solo bloque, con alguna mancha mayor donde se juntan. Es la más cercana de las dos a cómo se siente el minado en el mod del que se toma la idea.

```json
{
  "field": {
    "type": "speckle",
    "chances": [30, 30, 20, 20, 10, 10, 10, 10, 50],
    "spread": 0.15
  }
}
```

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `chances` | no | lista de enteros, por mil | `[30, 30, 20, 20, 10, 10, 10, 10, 50]` | Con qué frecuencia empieza un bloque en cada paso, el más blando al final. Lo que sobra es el paso más duro |
| `spread` | no | de 0.0 a 1.0 | `0.15` | Con qué frecuencia un paso se transmite al bloque contiguo, un paso más débil o tres |

La lista se lee con el más blando al final, así que la última entrada es el paso más blando y la primera es uno por encima del más duro. Con los números anteriores unos siete bloques de cada diez son el paso más duro y el resto está repartido entre ellos.

#### seeded

*el campo*

Las semillas se sitúan en una retícula calculada a partir del mundo y de la posición, y el paso de un bloque depende de lo cerca que esté de la más próxima. Eso da manchas menos numerosas, más grandes y más redondeadas que se funden unas con otras, y puede crecer con brazos.

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

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `cell` | no | entero, bloques | `8` | A qué distancia están las semillas entre sí |
| `seeds` | no | de 1 a 4 | `1` | Semillas en cada celda |
| `reach` | no | decimal, bloques | `3.0` | Hasta dónde llega la influencia de una semilla |
| `arms` | no | de 0 a 6 | `0` | Brazos que salen de cada semilla |
| `armReach` | no | decimal, bloques | `0.0` | Hasta dónde llegan los brazos |

Si se omite `arms`, las manchas son redondas. Dar brazos a una semilla la convierte en un nudo con zarcillos, y los brazos de nudos vecinos se alcanzan entre sí, lo que es una veta y no una bola. Mantén `reach` por encima de la mitad de `cell` o las manchas no podrán tocarse y obtendrás bolas separadas sin nada entre ellas.

### Mostrarlo

*grupos de dureza*

El multiplicador es invisible por sí solo. Para que un jugador pueda ver qué bloques son duros, dale al bloque un blockstate con una variante por cada paso, todas del mismo peso, listadas empezando por la más dura:

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

Minecraft ya elige una variante según la posición de un bloque, y un grupo de dureza le entrega el paso en su lugar, de modo que la textura y el multiplicador siempre coinciden.

Tres cosas tienen que estar bien, y ninguna avisa cuando está mal.

**Exactamente `buckets` entradas, todas con el mismo peso.** El paso se usa como un lugar en la lista, así que una lista de otra longitud, o una en la que los pesos difieran, apunta en silencio a la textura equivocada.

**Un nombre de modelo sin `block/` delante.** Un blockstate añade `block/` por sí mismo, así que `"model": "mypack:step_stone"` lee el archivo en `models/block/step_stone.json`. Escribir `mypack:block/step_stone` busca `models/block/block/step_stone.json`, que no existe, y la entrada se descarta sin decir nada.

**La misma clave que pide el juego.** No todos los bloques se identifican como se lee en sus propiedades. La piedra de vanilla lo guarda todo bajo `normal`, no bajo `variant=stone`, así que una sobrescritura que solo escribe `variant=stone` se fusiona y luego nunca se consulta. Escribir ambas claves es seguro, ya que la fusión es por clave y un pack prevalece sobre lo anterior.

Activa `worldgenDebug` y cada grupo de dureza se comprueba contra su modelo ya horneado al entrar en un mundo, indicando el blockstate, cuántas variantes sobrevivieron, qué textura acabó teniendo cada una y qué packs fusionó el juego para llegar ahí. Es la forma más rápida de encontrar cualquiera de los tres problemas anteriores, y además avisa cuando sobrescribir un blockstate compartido ha cambiado un estado que el grupo nunca nombró.

### Lo que no alcanza

*grupos de dureza*

Solo cambia el minado del propio jugador. Las máquinas que rompen bloques leen la dureza del bloque directamente y no se ven afectadas. Los bloques que coloca un jugador se sortean igual que cualquier otro, ya que el sorteo pertenece al lugar y no al bloque, y un bloque llevado a otro sitio adopta lo que diga su nuevo lugar.

---

# Fabricación, botín y comercio

## Bloques e ítems desactivados

*fabricación, botín y comercio*

`<namespace>/disabled/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

Saca bloques e ítems del juego sin anular su registro, de modo que los mundos conservan sus ids y borrar el archivo lo devuelve todo. El contenido de vanilla, de mods y de packs se trata igual, incluidos los propios bloques e ítems de un pack, y un bloque desactivado desactiva su ítem igual que un ítem desactivado desactiva su bloque.

```json
{
  "requires": ["thermalfoundation"],
  "names": ["thermalfoundation:ore", "thermalfoundation:material:128", "mekanism:salt*"],
  "namespaces": ["bigreactors"],
  "oreDict": ["oreTin"]
}
```

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `names` | no | lista de nombres de bloques e ítems | ninguno | Lo que se desactiva. Los metadatos como tercera parte, `"thermalfoundation:material:128"`, desactivan ese único ítem, y `:*` todos los metadatos. Un nombre que termina en `*` coincide con todo nombre que empiece por el resto |
| `namespaces` | no | lista de ids de mods | ninguno | Todos los bloques e ítems del mod |
| `oreDict` | no | lista de nombres del diccionario de minerales | ninguno | Todo ítem registrado bajo el nombre, y el nombre queda vacío |
| `requires` | no | lista de ids de mods | ninguno | El archivo se omite a menos que estén cargados todos. Las entradas `config:` y `file:` funcionan como en cualquier otro sitio |

Un bloque o ítem desactivado:

- desaparece de todas las pestañas creativas y de la pestaña de búsqueda, y se oculta en JEI y HEI
- no tiene ninguna receta que lo fabrique o lo use: se van todas las recetas de fabricación que lo tienen como resultado, y también todas las recetas de fabricación con una ranura que solo él puede llenar, junto con todas las recetas de horno que lo funden o que funden algo para obtenerlo. Una ranura que admite también otra cosa conserva su receta, y una ranura del diccionario de minerales simplemente la pierde junto con el nombre
- se quita de todos los nombres del diccionario de minerales
- se elimina de todo sorteo de botín, ya sea de cofres, de mobs o de pesca, de los drops de bloques y de los comercios de aldeanos, y una pila suya que se suelte se desvanece
- no se puede colocar, usar, blandir ni recoger, y la pila que se lleva en la mano se borra cuando un jugador lo intenta
- se borra dondequiera que aparezca una pila suya: del inventario y del cofre de Ender de un jugador al iniciar sesión y cada segundo después, de cualquier contenedor cuando un jugador lo abre, y de cofres y otros inventarios al cargarse su chunk
- se elimina del mundo donde está colocado: cada bloque suyo se convierte en aire, junto con su entidad de bloque, al cargarse su chunk

Para cambiar los bloques colocados por otra cosa en lugar de eliminarlos, dales una línea `blockReplacements` como `thermalfoundation:ore=minecraft:stone` en la plantilla de mundo, véase [Sustituciones](#sustituciones). Un bloque que el proceso de sustitución cambia se deja en manos de este. Las recetas que otro mod guarda dentro de sus propias máquinas pertenecen a ese mod y no se alcanzan. Los archivos se leen una vez al arrancar, y `content.disabled` en la configuración desactiva la carpeta, lo que requiere reiniciar.

Para vaciar un nombre del diccionario de minerales mientras sus ítems siguen en juego, usa en cambio `"-name": ["*"]` en un archivo del [diccionario de minerales](#diccionario-de-minerales).

## Recetas de horno y combustibles

*fabricación, botín y comercio*

`<namespace>/furnace/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

Añade y elimina recetas de fundición.

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

Entradas bajo `add`:

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `input` | sí | nombre de ítem | ninguno | Lo que entra |
| `output` | sí | nombre de ítem | ninguno | Lo que sale |
| `count` | no | entero | `1` | Cuántos salen |
| `experience` | no | número | `0.0` | Experiencia por fundición. El mineral de hierro da 0.7 |

Una adición cuya entrada ya funde algo se ignora, y el registro nombra en qué se funde ahora esa entrada; elimina esa receta en el mismo archivo para sustituirla.

Las entradas bajo `remove` son o bien un nombre de ítem sin más, que elimina todas las recetas que lo producen, o bien un objeto que nombra `input`, `result`, o ambos para acotar. Una eliminación que no nombra ninguno se omite y el registro lo indica.

`<namespace>/fuels/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

```json
{
  "fuels": [
    { "item": "mypack:ruby_coal", "burnTime": 2400 },
    { "oreDict": "gemRuby", "burnTime": 800 }
  ]
}
```

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `item` | una de las dos | nombre de ítem | ninguno | El ítem que arde |
| `oreDict` | una de las dos | nombre del diccionario de minerales | ninguno | Todo lo que hay bajo ese nombre arde |
| `burnTime` | sí | entero, ticks | `0` | El carbón es 1600, una tabla 300 |

## Pociones, tipos de poción y elaboración

*fabricación, botín y comercio*

`<namespace>/potions/*.json`

La ruta del archivo es el nombre de registro del efecto, así que `mypack/potions/ruby_sight.json` registra `mypack:ruby_sight`, que un tipo de poción nombra después.

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

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `name` | no | clave de traducción | `effect.<namespace>.<name>` | Lo que ve el jugador |
| `color` | no | color hexadecimal | `FFFFFF` | Color de las partículas |
| `badEffect` | no | booleano | `false` | Cuenta como dañino, de modo que un ojo de araña fermentado lo invierte |
| `beneficial` | no | booleano | `false` | Se muestra como un efecto bueno |
| `instant` | no | booleano | `false` | Se aplica una vez en lugar de con el tiempo |
| `effectiveness` | no | decimal | `0.5` | Cuánto lo valora la IA de los mobs |
| `icon` | no | objeto con `x` e `y` | `0`, `0` | Dónde se sitúa el icono en la hoja |
| `iconTexture` | no | ruta de textura | el icono de RDPL, o la hoja de vanilla cuando se define `icon` | Tu propio icono de 18 por 18 |
| `attributes` | no | lista de objetos | ninguno | `attribute`, `uuid`, `amount` (`0.0`), `operation` (`0`) |

### Tipos de poción

*pociones, tipos de poción y elaboración*

`<namespace>/potion_types/*.json`

La ruta del archivo es el nombre de registro del tipo de poción, que un ítem `potion_bottle` nombra después en `potionTypes`.

```json
{
  "baseName": "ruby_sight",
  "effects": [
    { "potion": "mypack:ruby_sight", "duration": 3600, "amplifier": 0, "ambient": false, "showParticles": true }
  ]
}
```

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- | --- |
| `baseName` | no | cadena | el espacio de nombres y el nombre | El nombre a partir del cual se construye la botella |
| `effects` | sí | lista de objetos | | Véase más abajo |

Cada efecto acepta `potion` (obligatorio), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) y `showParticles` (`true`).

### Elaboración de pociones

*pociones, tipos de poción y elaboración*

`<namespace>/brewing/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

```json
{
  "brewing": [
    { "input": "minecraft:potion", "ingredient": "mypack:ruby", "output": "mypack:ruby_potion", "requires": ["mypack"] },
    { "from": "minecraft:awkward", "ingredient": "mypack:ruby", "to": "mypack:ruby_tonic" }
  ]
}
```

Cada entrada es o bien `input`, `ingredient` y `output`, que elabora un ítem a partir de otro, o bien `from`, `ingredient` y `to`, que convierte un tipo de poción en otro. `ingredient` es obligatorio en ambos casos, y una entrada acepta también `requires`, de modo que se puede omitir una receta sin omitir el archivo.

## Trabajo en el yunque

*fabricación, botín y comercio*

`<namespace>/anvils/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan. Cada archivo es un trabajo.

Pon el ítem indicado en la ranura izquierda de un yunque y su ítem `with` en la derecha, y el yunque devuelve el de la izquierda con los encantamientos indicados, o su `result`, por los niveles indicados; se gasta uno de cada uno salvo que un recuento pida más, y el resto de cada pila queda en el yunque. Sacarlo también puede otorgar un progreso, y el ítem puede quedar bloqueado para su uso hasta que se consiga ese progreso: una espada que solo golpea una vez trabajada.

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

| Clave          | Obligatorio | Valor                                  | Predeterminado  | Qué hace                                                                                                                                                                                                                                                                         |
| -------------- | ----------- | -------------------------------------- | --------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `item`         | sí          | nombre de ítem, o `{ "item", "count" }` |                 | Lo que va en la ranura izquierda, y cuántas unidades necesita un trabajo, una por defecto; el resto de la pila queda para el siguiente. `{ "item": "minecraft:coal", "count": 8 }` con un `result` de diamante son ocho de carbón por un diamante. Metadatos como `minecraft:dye:4` |
| `with`         | sí          | nombre de ítem, o `{ "item", "count" }` |                 | Lo que va en la ranura derecha, y cuántas unidades se gastan, una por defecto: `{ "item": "minecraft:coal", "count": 10 }` pide una pila de al menos diez y se lleva diez. El yunque nunca ofrece nada por un ítem suelto, así que cada trabajo es una pareja                    |
| `result`       | no          | nombre de ítem, o `{ "item", "count" }` | el ítem izquierdo | Lo que sale en lugar del ítem izquierdo, y cuántas unidades, una por defecto, conservando las etiquetas del ítem izquierdo, de modo que un pico de hierro irrompible y diez de carbón pueden devolver uno de diamante irrompible. Los encantamientos van en lo que salga      |
| `levels`       | no          | int                                    | `1`             | Los niveles de experiencia que cuesta el trabajo, 1 como mínimo                                                                                                                                                                                                                  |
| `enchantments` | no          | objeto de nombre de encantamiento a nivel | ninguno      | Con qué vuelve el ítem. Un nivel que ya tiene a esa altura o más se deja como está, y si no hay nada que subir el yunque no ofrece nada, salvo que `grants` esté definido                                                                                                        |
| `grants`       | no          | `namespace:path`                       | ninguno         | Un progreso que se consigue al sacar el trabajo. Inclúyelo en `advancements/` con un criterio `impossible`, para que nada más lo otorgue                                                                                                                                          |
| `locks`        | no          | boolean                                | `false`         | Hasta que el jugador tenga `grants`, el ítem no sirve para golpear, usar ni excavar; se le avisa de qué espera al ponérselo en la mano. Meterlo en el yunque sigue permitido, que es como se desbloquea                                                                           |

Las reparaciones y combinaciones propias del yunque no se tocan: esto solo responde cuando la izquierda tiene un ítem indicado y la derecha tiene su `with`.

Un mob con `collectsExperience` también gasta aquí sus niveles. Mientras tenga `item` en la mano principal y `with` en la secundaria y tenga `levels` que pagar, camina hasta un yunque a menos de 16 bloques y lo trabaja una vez que está a menos de 3 bloques: los niveles se le descuentan como a un jugador, `with` se consume, el yunque se desgasta como con un jugador, y el resultado acaba en su mano principal. Al pasar sobre un ítem caído que algún trabajo de yunque nombra en `with`, lo recoge en la mano secundaria. `grants` y `locks` solo afectan a los jugadores, así que un mob no obtiene nada de `grants` y ningún bloqueo lo frena.

## Drops de bloques

*fabricación, botín y comercio*

`<namespace>/block_drops/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

Los bloques de vanilla 1.12 no tienen tablas de botín, así que un pack podía añadir cosas a lo que sueltan sus propios bloques, pero no tocar la piedra, un mineral ni el bloque de otro mod. Esto sí lo permite: una regla nombra un bloque y lo que suelta un jugador que lo recolecta además de los drops habituales, o en lugar de ellos.

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

| Clave         | Obligatorio | Valor             | Predeterminado | Qué hace                                                                                                                         |
| ------------- | ----------- | ----------------- | -------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| `block`       | sí          | id de bloque      |                | El bloque que vigila la regla                                                                                                    |
| `meta`        | no          | int               | `-1`           | Solo este metadato del bloque; `-1` es cualquier estado                                                                          |
| `replace`     | no          | boolean           | `false`        | Si los drops habituales se descartan antes de tirar estos                                                                        |
| `advancement` | no          | `namespace:path`  | ninguno        | La regla cuenta solo para un jugador que tenga ese progreso, de modo que el mismo bloque puede soltar una cosa antes y otra después |
| `drops`       | sí          | lista de drops    |                | Cada uno se tira por separado cuando un jugador rompe el bloque                                                                  |

Cada drop:

| Clave        | Obligatorio              | Valor                       | Predeterminado | Qué hace                                                                                                                                  |
| ------------ | ------------------------ | --------------------------- | -------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| `item`       | sí, salvo `experience`   | id de ítem                  |                | Lo que suelta, con metadatos como `minecraft:dye:4`                                                                                       |
| `experience` | no                       | número o `low-high`         |                | En lugar de un ítem, esa cantidad de experiencia en orbes, tirada de forma uniforme dentro del rango. `chance` y `silkTouch` se aplican como con un ítem |
| `count`      | no                       | número o `low-high`         | `1`            | Cuántos, tirado de forma uniforme dentro del rango                                                                                        |
| `chance`     | no                       | float                       | `1.0`          | La probabilidad de que el drop ocurra, siendo `0.05` una rotura de cada veinte                                                            |
| `fortune`    | no                       | int                         | `0`            | Hasta tantos extra por cada nivel de Fortuna de la herramienta                                                                            |
| `silkTouch`  | no                       | `either`, `only` o `never`  | `either`       | Si el drop necesita una herramienta con Toque de seda, la rechaza, o le da igual                                                          |

Las reglas solo ven la recolección de un jugador; las explosiones, los pistones y el vandalismo de los mobs no tiran nada. Varias reglas para un mismo bloque se aplican todas, y un `replace` en cualquiera de ellas elimina primero los drops habituales.

## Botín de jugadores

*fabricación, botín y comercio*

`<namespace>/player_loot/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

Vanilla 1.12 no da a los jugadores ninguna tabla de botín: al morir solo suelta el inventario, y no hay un nombre de tabla que un pack pueda sobrescribir. RDPL añade una, que se tira cuando muere un jugador:

```json
{
  "table": "mypack:entities/player",
  "mode": "add",
  "rollOnKeepInventory": false,
  "dropLoose": false
}
```

| Clave                 | Obligatorio | Valor                | Predeterminado | Qué hace                                                                              |
| --------------------- | ----------- | -------------------- | -------------- | ------------------------------------------------------------------------------------- |
| `table`               | sí          | nombre de tabla      |                | La tabla de botín que se tira cuando muere un jugador                                 |
| `mode`                | no          | `add` o `replace`    | `add`          | Si los ítems de la tabla se suman al inventario o lo sustituyen                       |
| `rollOnKeepInventory` | no          | boolean              | `false`        | Si la tabla se tira siquiera en una muerte que conservó el inventario                 |
| `dropLoose`           | no          | boolean              | `false`        | Si los ítems se colocan directamente en el suelo en lugar de sumarse a los drops de la muerte |

`add` suelta los ítems de la tabla junto al inventario: úsalo para recompensas por matar. `replace` descarta el inventario y suelta solo lo que tira la tabla.

Con `rollOnKeepInventory` desactivado, las muertes con `keepInventory` (y las de espectador, que siempre conservan el inventario) no tiran nada. Activarlo mantiene la muerte costosa en mundos con keep-inventory.

Varios archivos se acumulan, cada uno evaluado por separado. Si alguna entrada aplicable es `replace`, el inventario se vacía una sola vez antes de tirar, de modo que una entrada `add` junto a ella igualmente se aplica.

La tabla es una tabla de botín corriente buscada por nombre: puede estar en el pack en `loot_tables/entities/player.json`, ser cualquier tabla de vanilla o de un mod, y alcanzarse mediante `loot_injections`. Contexto del botín: el jugador que muere es la entidad saqueada, el asesino (si lo hay) es el jugador que mata, y la fuente de daño está definida; `killed_by_player`, `entity_properties`, `random_chance_with_looting`, `looting_enchant` y `quality` se comportan con normalidad.

Una función de botín es propia de RDPL, utilizable en cualquier tabla con una entidad saqueada: `rdpl:killed_name` da al ítem soltado el nombre de la víctima. `format` da forma al nombre mostrado (`%s` es la víctima, por defecto solo el nombre), y `tag` escribe en su lugar el nombre sin formato en una clave de cadena NBT para ítems que lo leen por sí mismos.

```json
{ "item": "mypack:human_skull", "weight": 1,
  "functions": [ { "function": "rdpl:killed_name", "format": "%s's Skull" } ] }
```

**Mods de tumbas.** Los ítems tirados se suman a los drops de muerte habituales antes de que ningún mod de tumbas los lea, así que acaban en la tumba con todo lo demás (`replace` pone en la tumba el contenido de la tabla en lugar del inventario). Vale para Gravestone, GraveStone Mod, Corail Tombstone y cualquier otro que trabaje a partir de la lista de drops de la muerte. No requiere configuración.

`dropLoose` se salta por completo la lista de drops: los ítems se colocan directamente en el mundo, de modo que los mods de tumbas nunca los ven: el inventario va a la tumba y los ítems de la tabla quedan en el suelo para el asesino. Úsalo para botines que pertenecen al asesino y no a la tumba de la víctima. Sin un mod de tumbas cambia poco. Advertencia: los ítems existen antes de que nada posterior pudiera cancelar los drops, así que las entradas que no deban sobrevivir a una muerte cancelada deberían dejarlo desactivado.

Pon `playerLoot` en la categoría `data` de la configuración a `false` para desactivar la carpeta por completo.

## Aldeanos y comercio

*fabricación, botín y comercio*

`<namespace>/villagers/*.json`

La ruta del archivo es el nombre de registro de la profesión, así que `mypack/villagers/jeweller.json` registra `mypack:jeweller`, que un comercio nombra después en `profession`.

```json
{
  "careers": ["gem_cutter", "appraiser"],
  "texture": "mypack:textures/entity/villager/jeweller.png",
  "zombieTexture": "mypack:textures/entity/zombie_villager/jeweller.png"
}
```

| Clave           | Obligatorio | Valor                | Predeterminado                | Qué hace                                                                     |
| --------------- | ----------- | -------------------- | ----------------------------- | ---------------------------------------------------------------------------- |
| `careers`       | sí          | lista de nombres     | ninguno                       | Las carreras que ofrece esta profesión. Una profesión sin ninguna se rechaza |
| `texture`       | no          | ruta de textura      | el aldeano de vanilla         | Qué aspecto tiene el aldeano                                                 |
| `zombieTexture` | no          | ruta de textura      | el aldeano zombi de vanilla   | Qué aspecto tiene una vez zombificado                                        |

### Comercio

*aldeanos y comercio*

`<namespace>/trades/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

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

| Clave        | Obligatorio | Valor                | Predeterminado | Qué hace                                  |
| ------------ | ----------- | -------------------- | -------------- | ----------------------------------------- |
| `profession` | sí          | nombre de profesión  |                | De quién es este comercio                 |
| `career`     | sí          | nombre de carrera    |                | Qué carrera dentro de ella                |
| `level`      | no          | int                  | `1`            | En qué nivel de comercio aparece          |
| `maxUses`    | no          | int                  | `12`           | Veces que se puede usar antes de bloquearse |

Una pila es `item` con `min` (`1`) y `max` (`min`), de modo que un precio fijo es solo `min`.

---

# Criaturas y peligros

## Variantes de entidades

*criaturas y peligros*

`<namespace>/entities/*.json`

La ruta del archivo es el nombre de registro de la variante, así que `mypack/entities/angry_cow.json` registra `mypack:angry_cow`, que es a lo que se refieren `becomes`, un huevo generador y el guardado de un mundo.

Un archivo aquí crea una entidad nueva a partir de una que ya existe. Es una entidad real por derecho propio, con su propio nombre de registro, su propio nombre en el mundo, su propio huevo generador y una tabla de botín propia si le das una, construida sobre el comportamiento de otra entidad sin sustituirla. Nada de la entidad que copia cambia.

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita.

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

### Identidad

*variantes de entidades*

| Clave           | Obligatorio | Valor                                | Predeterminado | Qué hace                                                                                                                                                                                              |
| --------------- | ----------- | ------------------------------------ | -------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `entity`        | sí          | `namespace:name`                     | ninguno        | La entidad sobre la que construir. La de cualquier mod, siempre que admita un constructor de mundo simple                                                                                             |
| `name`          | no          | string                               | ninguno        | El nombre que lleva en el mundo, en los mensajes de muerte y en su huevo                                                                                                                              |
| `showName`      | no          | boolean                              | `false`        | Mostrar el nombre sin mirarla                                                                                                                                                                         |
| `egg`           | no          | boolean u objeto                     | `true`         | Un huevo generador, con los colores del huevo de la entidad que copia. `{ "primary": "AABBCC", "secondary": "112233" }` elige tus propios colores, `false` omite el huevo                              |
| `becomes`       | no          | lista                                | ninguno        | Otras variantes en las que esta puede convertirse al aparecer, según su peso. Véase más abajo                                                                                                         |
| `baby`          | no          | boolean o 0.0 a 1.0                  | `false`        | Con qué frecuencia aparece una cría, y así se queda. `true` es siempre, un número es esa proporción de ellas                                                                                          |
| `keepsBaseBaby` | no          | boolean                              | `false`        | Si también se ejecuta la tirada de cría de la base. Sin ella, una variante basada en el zombi solo aparece como cría según diga `baby`, sin hijo por `zombieBabyChance` de Forge ni jinete de pollo   |
| `profession`    | no          | `namespace:name`                     | aleatoria      | Para un aldeano, el oficio que ejerce                                                                                                                                                                 |
| `career`        | no          | int                                  | aleatoria      | Qué carrera dentro de esa profesión, de 1 en adelante                                                                                                                                                 |
| `requires`      | no          | lista de ids de mod o espacios de nombres de pack | ninguno | La variante se omite a menos que estén presentes todos                                                                                                                                         |

Una variante es una clase propia, así que un mundo que contenga una depende del pack que la creó, igual que depende de un mod. Si quitas el archivo, las criaturas de ese mundo desaparecen con él.

**Un huevo o spawner que da una mezcla.** Una variante es una clase propia, así que por sí sola siempre genera exactamente lo que dice. `becomes` es la forma en que un pack rompe eso: una lista de variantes en las que esta puede convertirse al aparecer, cada una con un peso, decidido por criatura.

```json
{
  "becomes": [
    { "variant": "mypack:walker", "weight": 95 },
    { "variant": "mypack:little_walker", "weight": 5 }
  ]
}
```

Nombrarse a sí misma es la forma de seguir como está, y los pesos son las probabilidades. Pon eso en `mypack:walker` y un huevo y una entrada de aparición dan sobre todo walkers con algún pequeño ocasional, como un huevo de zombi te da algún bebé de vez en cuando. Ocurre al entrar la criatura en el mundo, así que vale para huevos, `/summon` y aparición natural por igual, y la criatura que llega es una real de la variante elegida con todo lo que esa variante dice. Un spawner es más estricto: solo tira entre las variantes del mismo mob base y del mismo equipo que la variante a la que está configurado, así que un spawner de zombis da los zombis de ese equipo y sus crías, y nunca una criatura de otro tipo o de otro equipo, igual que un spawner de zombis de vanilla sigue siendo un spawner de zombis. Una variante alcanzada así no vuelve a transformarse, de modo que dos variantes pueden nombrarse entre sí sin entrar en bucle.

**Dónde encaja `baby`.** El juego no tiene un zombi bebé propio: hay un zombi que tira si es una cría al aparecer. `baby` dice con qué frecuencia, así que `"baby": 0.05` es la costumbre de vanilla y `"baby": true` es siempre. Una variante no toma además la tirada propia del zombi, de modo que no aparece ninguna cría ni jinete de pollo que `baby` no haya pedido; `keepsBaseBaby` devuelve esa tirada. Entre ambas son dos formas de lograr lo mismo, y cuál usar depende de la diferencia que quieras: `baby` solo da una variante que a veces es cría, `becomes` da varias variantes que difieren en lo que quieras, y una mezcla de ambas es válida.

### Aspecto

*variantes de entidades*

| Clave        | Obligatorio | Valor                                  | Predeterminado | Qué hace                                                                                                                 |
| ------------ | ----------- | -------------------------------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------ |
| `texture`    | no          | `namespace:textures/entity/<file>.png` | ninguno        | Una skin propia, con la misma disposición que la de la entidad que copia                                                 |
| `tint`       | no          | color hexadecimal                      | ninguno        | Colorea la entidad al dibujarla                                                                                          |
| `tintParts`  | no          | lista de `body`, `armor`, `held`       | `["body"]`     | A qué partes llega el tinte                                                                                              |
| `scale`      | no          | float                                  | `1.0`          | Cuán grande se dibuja, y cuán grande es su hitbox                                                                        |
| `angryScale` | no          | float                                  | `scale`        | El tamaño al que se hincha mientras tiene algo que atacar, y durante tres segundos después de perderlo                   |
| `width`      | no          | float                                  | el de la base  | El ancho de su hitbox, antes de aplicar `scale`                                                                          |
| `height`     | no          | float                                  | el de la base  | El alto de su hitbox, antes de aplicar `scale`                                                                           |
| `glowing`    | no          | boolean                                | `false`        | Con contorno a través de las paredes                                                                                     |
| `bright`     | no          | boolean                                | `false`        | Dibujada con luz máxima dondequiera que esté, como bajo el sol de mediodía, de modo que nunca la oscurecen la noche, la sombra ni una cueva |
| `invisible`  | no          | boolean                                | `false`        | No se dibuja, aunque su equipo sí                                                                                        |
| `hideArmor`  | no          | boolean                                | `false`        | Lleva su armadura sin que se dibuje                                                                                      |
| `hideHeld`   | no          | boolean                                | `false`        | Lo mismo para lo que sostiene                                                                                            |
| `leftHanded` | no          | boolean                                | `false`        | Sostiene su arma en la otra mano                                                                                         |

`scale` cambia tanto el modelo como la hitbox en ambos lados, así que lo que ves es lo que puedes golpear. Una criatura que cambia su propio tamaño, un animal que crece o un zombi que es una cría, se escala en torno al tamaño que haya elegido, de modo que ambos no chocan. `angryScale` la hincha mientras tiene un objetivo y la devuelve a `scale` cuando lo pierde. Como al cliente nunca se le dice qué está cazando una criatura, el indicador de sprint lleva esa noticia: se activa en una variante que usa `angryScale` y en nada más, así que un mod que lea el sprint en tus variantes lo verá cambiar. Crecer bajo un techo bajo es posible, igual que cuando crece un slime, así que mantén la diferencia moderada.

Una `texture` se enlaza en lugar de la que la entidad usaría normalmente, sea cual sea el renderizador que herede, así que funciona tanto con entidades de mods como con las de vanilla. Tiene que coincidir con el modelo sobre el que se dibuja, ya que el modelo es el de la entidad base: una skin, no una forma nueva. Las capas conservan sus propias texturas, así que la armadura sigue viéndose como armadura en un zombi con otra skin.

La armadura solo se dibuja en una entidad cuyo renderizador tiene una capa de armadura, que en esta versión son los mobs humanoides y los aldeanos. Una variante de vaca o de araña puede llevar armadura y recibe su protección, pero nada la dibuja, así que `armor` dentro de `attributes` suele ser la forma más limpia de hacer resistente a una criatura así. `hideArmor` es para el otro caso: un humanoide que debe conservar la armadura en sus ranuras, por la protección o por un mod que las lea, sin que se vea.

### Sus sonidos

*variantes de entidades*

| Clave         | Obligatorio | Valor   | Predeterminado | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| ------------- | ----------- | ------- | -------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `sounds`      | no          | objeto  | los de la base | `ambient`, `hurt` y `death`, cada uno un evento de sonido registrado. Otros tres para los que no tiene sonido base: `target` suena una vez cada vez que fija un objetivo, y `explode` es cómo suena su explosión en lugar de la del juego, ya sea que estalle ella misma con `explodes` o lance TNT con `throws`. `throw` suena al lanzar cualquier cosa con `throws`, en lugar del lanzamiento de bola de nieve, o del siseo de la mecha para el TNT. `targetVaries` desplaza cada reproducción de `target` hacia arriba o hacia abajo una cantidad aleatoria dentro de esos semitonos, así que `3` oscila un cuarto de octava a cada lado; `0` lo reproduce tal cual |
| `soundVolume` | no          | número  | `1.0`          | Cuán fuertes son esos sonidos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `soundPitch`  | no          | número  | `1.0`          | Cuán agudos suenan. Menos de 1 es más grave, más de 1 es más chillón                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `silent`      | no          | boolean | `false`        | No hace ningún sonido                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |

### Salud, daño y efectos

*variantes de entidades*

| Clave               | Obligatorio | Valor                                           | Predeterminado     | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                       |
| ------------------- | ----------- | ----------------------------------------------- | ------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `attributes`        | no          | objeto                                          | ninguno            | `maxHealth`, `movementSpeed`, `attackDamage`, `attackSpeed`, `knockbackResistance`, `followRange`, `armor`. Un atributo que la entidad no tiene normalmente se le concede. `attackSpeed` son golpes por segundo para un luchador cuerpo a cuerpo, `1` como lo tiene el juego, así que `2` golpea el doble de a menudo. Vale también cualquier nombre que la base ya lleve, `zombie.spawnReinforcements`, `horse.jumpStrength`. `attackDamage` en una base que dispara es lo que dañan sus flechas |
| `absorption`        | no          | float                                           | `0`                | Corazones extra por encima de su salud                                                                                                                                                                                                                                                                                                                                                                         |
| `invulnerable`      | no          | boolean                                         | `false`            | No recibe daño de nada salvo del vacío y del modo creativo                                                                                                                                                                                                                                                                                                                                                     |
| `fireproof`         | no          | boolean                                         | `false`            | Nunca se prende fuego, así que el fuego y la lava nunca la dañan y nunca arde a la luz del día                                                                                                                                                                                                                                                                                                                 |
| `immuneTo`          | no          | lista de tipos de daño                          | ninguno            | Daño que ignora: `fall`, `drown`, `explosion`, `magic`, `cactus`, `lava`, `wither`, `starve`, `anvil`, `inWall` y los demás                                                                                                                                                                                                                                                                                    |
| `fallDamage`        | no          | float                                           | `1.0`              | Multiplica el daño que causa una caída. `0` elimina el daño por caída                                                                                                                                                                                                                                                                                                                                          |
| `hurtResistance`    | no          | int, ticks                                      | el de la base, `20` | Cuánto tiempo después de un golpe no se le puede herir de nuevo. Los golpes más rápidos que la mitad de esto se pierden, así que un atacante rápido necesita un objetivo con menos                                                                                                                                                                                                                              |
| `effects`           | no          | lista de objetos                                | ninguno            | Efectos que siempre tiene: `{ "potion": "minecraft:strength", "amplifier": 1 }`                                                                                                                                                                                                                                                                                                                                |
| `ignoresEffects`    | no          | lista de ids de poción, o `all`                 | ninguno            | Efectos que nunca le afectan, sea quien sea o lo que sea que los aplique: un golpe, una poción arrojadiza, un faro, una flecha, `/effect`. `all` rechaza todos los efectos, de modo que una variante empieza como una pizarra en blanco. Sus propios `effects` se le siguen aplicando                                                                                                                           |
| `creatureAttribute` | no          | `undefined`, `undead`, `arthropod` o `illager`  | el de la base      | Qué cuenta como, para que Perdición de los no muertos y las pociones curativas la traten en consecuencia                                                                                                                                                                                                                                                                                                       |

### Movimiento

*variantes de entidades*

| Clave            | Obligatorio | Valor           | Predeterminado | Qué hace                                                                                                                                       |
| ---------------- | ----------- | --------------- | -------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| `jumpMultiplier` | no          | float           | `1.0`          | Cuánto más alto salta que la entidad que copia                                                                                                 |
| `stepHeight`     | no          | float, bloques  | el de la base  | Qué altura de saliente sube sin saltar. La mayoría de las criaturas suben `0.6`, un zombi `1.0`                                                |
| `maxFallHeight`  | no          | int             | el de la base  | Cuánto se dejará caer al calcular rutas                                                                                                        |
| `climbs`         | no          | boolean         | el de la base  | Trepa por las paredes como una araña, y calcula rutas por ellas; `false` deja a una araña en el suelo                                          |
| `teleports`      | no          | boolean         | `true`         | Si un enderman o un shulker puede teletransportarse. Desactivado, se queda donde está, también a la luz del día y en el agua                   |
| `walks`          | no          | boolean         | `false`        | Un conejo camina como los demás animales en lugar de moverse a saltos. Solo lo lee un conejo                                                   |
| `pathPriorities` | no          | objeto          | ninguno        | Por dónde andará, como `WATER`, `LAVA`, `DANGER_FIRE`, `DOOR_WOOD_CLOSED` y los demás, cada uno un número donde uno negativo significa nunca   |
| `leashable`      | no          | boolean         | `false`        | Se puede llevar con una rienda, aunque la entidad que copia nunca pudiera                                                                      |
| `steerable`      | no          | boolean         | `false`        | Se puede dirigir al montarla                                                                                                                   |
| `noAI`           | no          | boolean         | `false`        | Se queda donde la ponen y no hace nada                                                                                                         |

### Agua

*variantes de entidades*

| Clave                | Obligatorio | Valor   | Predeterminado | Qué hace                                                                                                                                                                                                                                                            |
| -------------------- | ----------- | ------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `breathesUnderwater` | no          | boolean | `false`        | Nunca se ahoga, y se hunde para caminar por el fondo en lugar de nadar hacia la superficie. Sigue orientándose por el suelo, así que el agua profunda de la que no pueda salir caminando la retendrá                                                                |
| `swims`              | no          | boolean | `false`        | Se mueve por el agua como un calamar o un guardián, y nunca se ahoga. Se orienta por el agua en lugar de por el suelo, así que su sitio es el agua y fuera de ella queda varada                                                                                      |
| `amphibious`         | no          | boolean | `false`        | Camina en tierra y nada bien en el agua, cambiando cómo se orienta al entrar y salir del agua. Nunca se ahoga. Olvida lo que perseguía en la orilla del agua, así que titubea un momento cada vez que la cruza                                                        |
| `waterSlowdown`      | no          | float   | `0.8`          | Cuánto la frena el agua. Más alto es más rápido                                                                                                                                                                                                                     |

### Combate

*variantes de entidades*

| Clave           | Obligatorio | Valor                   | Predeterminado       | Qué hace                                                                                                                                                                                                                                                                                                                                                       |
| --------------- | ----------- | ----------------------- | -------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `hostile`       | no          | boolean                 | `false`              | Ataca lo que alcanza, y se defiende cuando le hieren. Una variante hostil cuenta como monstruo para el juego sea cual sea su base, así que el límite de monstruos la contiene y el modo pacífico la elimina, y pierde las tareas de animal con las que venía su base: reproducirse, dejarse tentar, seguir a un progenitor, a un dueño o a los de su especie, sentarse |
| `passive`       | no          | boolean                 | `false`              | Le impide atacar a nada, se comporte como se comporte normalmente                                                                                                                                                                                                                                                                                              |
| `targets`       | no          | lista de nombres de entidad | el jugador       | Lo que va buscando mientras es hostil. `minecraft:player` se entiende aunque el jugador no sea una entidad registrada                                                                                                                                                                                                                                          |
| `attackReach`   | no          | float, bloques          | su tamaño            | Hasta dónde llega un golpe cuerpo a cuerpo. El juego alcanza el doble del ancho, por lo que una criatura ampliada golpea desde más lejos; esto lo fija directamente                                                                                                                                                                                             |
| `knockback`     | no          | float                   | el de la base, `0.4` | Cuán fuerte empujan sus golpes. `0` no empuja en absoluto                                                                                                                                                                                                                                                                                                      |
| `hitEffects`    | no          | boolean                 | `true`               | Si aplica a lo que golpea el efecto que aplica la entidad que copia: el wither de un esqueleto wither, el veneno de una araña de cueva, el hambre de un zombi momificado. Desactivado, golpea solo con daño                                                                                                                                                      |
| `hitFire`       | no          | boolean                 | `true`               | Si prende fuego a lo que golpea cuando lo haría la entidad que copia: un zombi en llamas, la bola de fuego de un blaze. Desactivado, nada de lo que hace inicia un fuego en su objetivo                                                                                                                                                                          |
| `threatLeast`   | no          | int                     | `0`                  | La banda de amenaza más baja en la que debe estar un jugador u otro portador a menos de 128 bloques para que la variante aparezca de forma natural. `0` aparece como de costumbre                                                                                                                                                                                |
| `threatHostile` | no          | int                     | `0`                  | La banda de amenaza más baja en la que debe estar un jugador para que la variante vaya a por él por su cuenta. Por debajo, la variante es dócil con ese jugador, aunque se defiende si le golpean. `0` ataca como de costumbre                                                                                                                                   |

`hostile` también elimina el comportamiento que hacía huir a la criatura: un animal que evitaba a los jugadores o entraba en pánico al ser herido no hace ninguna de las dos cosas una vez es hostil, pues de lo contrario huiría de aquello a lo que debe atacar. Necesita una entidad que ande por el suelo, ya que usa el mismo comportamiento de ataque que vanilla da a sus propios mobs. Una base voladora o nadadora se registra en el log y se deja como está. `passive` funciona más ampliamente, pero solo alcanza el comportamiento construido como lo construye vanilla; un mod cuya hostilidad está escrita en su propio código de tick o de daño no es algo de lo que un pack pueda disuadirlo.

### Equipo, drops y experiencia

*variantes de entidades*

| Clave                | Obligatorio | Valor                       | Predeterminado | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| -------------------- | ----------- | --------------------------- | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `equipment`          | no          | objeto                      | ninguno        | `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`, cada uno un nombre de ítem                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `dropChance`         | no          | 0 a 1                       | `0`            | Con qué probabilidad suelta cada pieza de equipo                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `picksUpLoot`        | no          | boolean                     | `false`        | Recoge lo que pisa                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `lootTable`          | no          | `namespace:entities/<name>` | el de la base  | Lo que suelta. Sin esto suelta lo que suelte la entidad que copia                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `experience`         | no          | int                         | el de la base  | Cuánta experiencia suelta                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `collectsExperience` | no          | boolean                     | `false`        | Reúne experiencia como un jugador: los orbes a menos de ocho bloques se desplazan hacia él y los toma al tocarlos, Reparación en su equipo se aplica primero, y los puntos forman niveles según la curva del propio jugador, conservados en el mob a través de un guardado. Lo que mata suelta su experiencia como si lo hubiera matado un jugador, un bloque que rompe su tarea `digs` suelta la experiencia propia del bloque, y una tirada de experiencia de `block_drops` también se aplica para él. Al morir suelta siete por nivel hasta cien, salvo que `keepInventory` esté activado. Los objetivos con el criterio `xp` o `level` llevan su total y su nivel en una fila con el nombre de su UUID, de modo que una función los lee con `score_<objective>_min`. Gasta sus niveles en trabajos de yunque como un jugador, véase [Trabajo en el yunque](#trabajo-en-el-yunque) |

Una variante suelta lo que suelte la entidad que copia, porque la tabla de botín está fijada en el propio código de esa entidad en lugar de buscarse por nombre. `lootTable` la apunta a una tabla propia, que luego aportas en `loot_tables/entities/<name>.json` como cualquier otra.

### Comportamientos especiales

*variantes de entidades*

| Clave            | Obligatorio | Valor          | Predeterminado    | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| ---------------- | ----------- | -------------- | ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `throws`         | no          | boolean        | `false`           | Lanza lo que sostiene a su objetivo desde la distancia, y si es TNT lo enciende y se aleja. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `throwAmmo`      | no          | int            | ninguno           | Cuántos tiene para lanzar. Si se omite, nunca se queda sin ellos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `throwReload`    | no          | int, segundos  | `explosionFuse`   | Cuánto tiempo tiene la mano vacía antes de sacar otro                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `throwRetreat`   | no          | int, segundos  | `explosionFuse`   | Cuánto tiempo se mantiene alejado tras un lanzamiento antes de volver                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `throwPower`     | no          | float          | `1.0`             | Con qué fuerza lanza. Duplicarlo duplica aproximadamente el alcance                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `throwArc`       | no          | float          | `0.35`            | A qué altura lanza en parábola. Más alto se mantiene más tiempo en el aire, cerca de cero es un lanzamiento plano, por debajo de cero lanza hacia abajo                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `throwReturns`   | no          | boolean        | `false`           | Lo que lanza vuela como un tridente: golpea con el `attackDamage` de la variante, u 8 en una base sin él, y luego vuelve volando a su mano como Lealtad devuelve un tridente. Nunca se agota y se apunta al objetivo como apunta un esqueleto, más rápido con `throwPower` y con menos dispersión en dificultades más altas, y el lanzador se mantiene firme mientras vuela, así que `throwAmmo`, `throwReload`, `throwRetreat` y `throwArc` no se le aplican. El TNT se lanza como siempre                                                                                                                                                                                                                                                                                       |
| `explodes`       | no          | boolean        | `false`           | Estalla junto a su objetivo, como un creeper. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `explosionPower` | no          | número         | `3.0`             | Cuán grande es la explosión. Un creeper es 3, el TNT es 4. En una base de creeper es también la explosión propia del creeper, y en un ghast la de la bola de fuego                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `explosionFuse`  | no          | int, ticks     | `30`              | Cuánto tiempo sisea antes de estallar. En una base de creeper es también la mecha propia del creeper                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `explosionFire`  | no          | boolean        | `false`           | Deja fuegos tras de sí                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `charges`        | no          | boolean        | `false`           | Se lanza hacia su objetivo desde la distancia y golpea con un fuerte empuje al contacto, como un devastador, y luego descansa antes de la siguiente embestida. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `pounces`        | no          | boolean        | `false`           | Se agacha y luego salta sobre su objetivo en arco y golpea al aterrizar, como un zorro. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `sniffs`         | no          | int, bloques   | `0`               | Oye a los jugadores que se mueven a esa cantidad de bloques, haya paredes o no, y camina hasta donde los oyó; un jugador que se agacha o está quieto no se oye, y uno que luego ve se convierte en su objetivo. `0` no escucha. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `fleesWhenHurt`  | no          | 0.0 a 1.0      | `0`               | Rompe el combate y huye de aquel con quien pelea mientras su salud esté por debajo de esa fracción, y vuelve cuando la supera. `0` nunca huye. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `sleepsByDay`    | no          | boolean        | `false`           | Busca sombra de día y se queda quieto allí hasta la noche o hasta que algo lo ataca. Mientras descansa se tumba de lado                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `home`           | no          | int, bloques   | `0`               | Se mantiene a esa cantidad de bloques alrededor del punto donde estuvo por primera vez, vagando dentro y volviendo cuando se aleja. `0` deambula libremente                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `patrols`        | no          | boolean        | `false`           | Recorre la tierra en largos tramos con otros de su especie siguiendo a un líder, como una patrulla de saqueadores. Un grupo que aparece junto elige un líder; los demás se mantienen a pocos bloques de él, y cuando el líder fija un objetivo lo hacen todos. Un seguidor que pierde a su líder toma el mando él mismo. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `swoops`         | no          | boolean        | `false`           | Da vueltas sobre su objetivo y se lanza en picado a través de él, golpeando en la pasada, como un fantasma. A la variante se le da un ayudante volador, así que vuela mientras caza y se posa en el suelo cuando está inactiva; necesita una base que sea una criatura, un loro por ejemplo, y un murciélago no lo es. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `gusts`          | no          | boolean        | `false`           | Se prepara y suelta una ráfaga de viento contra su objetivo desde la distancia, lanzando todo lo que hay cerca del objetivo hacia atrás y hacia arriba, como la carga de viento de una brisa. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `gustPower`      | no          | float          | `1.5`             | Con qué fuerza lanza una ráfaga. Un golpe de un mob es 0.4, un encantamiento fuerte de retroceso alrededor de 1                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `digs`           | no          | boolean        | `false`           | Excava lo que se interponga entre él y su objetivo, con la herramienta que lleva en la mano: una pala en tierra, arena y grava, un pico en piedra, un hacha en madera, y solo lo que el material de esa herramienta pueda romper, así que un pico de madera nunca abre mineral de hierro y nada abre obsidiana salvo el diamante. Un bloque tarda lo que tardaría con un jugador con esa herramienta, suelta lo que soltaría, y desgasta la herramienta. Dale la herramienta con `equipment`; con las manos vacías no excava nada, y no excava nada donde `mobGriefing` está desactivado. Nunca busca un rodeo: con un objetivo va directo a él y excava lo que haya en medio, y donde la herramienta no puede abrir el bloque se queda empujando. Necesita `hostile`. Fija sus objetivos sin necesitar verlos, ya que aquello hacia lo que excava está por naturaleza detrás de algo |

**Lanzar en lugar de cargar.** `explodes` envía a una criatura a estallar sobre su objetivo. `throws` es el otro temperamento: mantiene la distancia, lanza lo que tenga en la mano principal contra aquello con lo que lucha, y si resulta ser TNT lo enciende, lo lanza y se aparta mientras arde.

```json
{
  "hostile": true,
  "throws": true,
  "explosionFuse": 50,
  "equipment": { "mainhand": "minecraft:tnt" }
}
```

Al lanzar se queda con la mano vacía, porque lanzó la cosa. Luego se mantiene alejado durante `throwRetreat`, saca otro tras `throwReload`, y vuelve hacia su objetivo: un ciclo de lanzar, retroceder, recargar, acercarse. Dale un `throwAmmo` y ese ciclo termina cuando se acaba la cuenta, quedando su mano vacía para siempre y retomando su ataque normal. Omite `throwAmmo` y nunca se queda sin munición.

La cuenta se escribe en la criatura, así que no se rellena porque un chunk se descargara y volviera a cargarse. Todo lo que no sea TNT vuela como un ítem y cae, lo que hace tan fácil un zapador que lance piedras o carne podrida como uno que lance explosivos.

`explosionFuse` sigue siendo la mecha del TNT lanzado, y sustituye a cualquiera de los dos temporizadores que omitas, así que una variante escrita antes de estas claves se comporta exactamente como antes.

Cómo vuela el lanzamiento en sí lo deciden `throwPower` y `throwArc`. El primero es un multiplicador del empujón, y como el empujón ya crece con la distancia, subirlo alarga el alcance sin cambiar cuánto tiempo se mantiene el lanzamiento en el aire. El segundo es la elevación, y cambia la forma: alto y lanza por encima de un muro y se toma su tiempo, cerca de cero y se arroja plano y cae casi al instante, por debajo de cero y se lanza hacia abajo a algo que está debajo. Ambos dejan la mecha en paz, así que una carga lanzada en parábola y una plana estallan el mismo número de segundos después de salir de la mano, que es lo que decide si una revienta en lo alto o cae primero y espera. Desde qué distancia lanzará es su `followRange`, y se acerca como de costumbre cuando estás a menos de tres bloques, así que es peligroso a distancia y corriente cara a cara.

### Tareas

*variantes de entidades*

**Cualquier tarea que tenga el juego.** Las claves anteriores son comportamientos propios de RDPL. `tasks` llega más allá de ellas hasta todas las tareas que usa la propia vanilla, en cualquier base: una entrada es un objeto que nombra la `task` y su `priority`, más lo que lea esa tarea; un nombre tras un `-` elimina todas las tareas de ese tipo con las que venía la base. Las prioridades van de 0 como primera, y vanilla conserva las suyas entre 1 y 8, así que una tarea en 0 gana sobre todo lo que hace la base y una en 9 solo se ejecuta cuando nada más lo quiere.

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

| Clave   | Obligatorio | Valor | Predeterminado | Qué hace                                                                                                                                                  |
| ------- | ----------- | ----- | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `tasks` | no          | lista | ninguno        | Cualquier tarea que tenga el juego, añadida a la variante por nombre con la prioridad que elijas, o quitada de lo que su base traía. La lista, más abajo |

La lista se aplica después de que `hostile`, `passive` y los comportamientos anteriores hayan hecho su trabajo, así que tiene la última palabra. Las tareas que mueven el cuerpo se bloquean entre sí: una solo se ejecuta cuando nada por delante de ella en prioridad está moviendo a la criatura, y el ataque con el que viene un monstruo está en 2, así que un salto o una huida en un zombi necesita prioridad 1 o nunca le llega el turno; la araña y el lobo mantienen su salto por delante de su ataque por la misma razón. Una tarea que la base ya ejecuta se añade una segunda vez en lugar de sustituirse; elimina primero la antigua. Algunas tareas solo tienen sentido en una base que tenga aquello que manejan: una pelea con arco necesita una base que dispare, sentarse necesita una base que se pueda domar, y comerciar necesita un aldeano. Pide una en una base que no pueda llevarla y el log dice qué base necesita, y la variante prescinde de ella.

| Clave       | Tipo                   | Predeterminado     | Qué hace                                                                                                                                                               |
| ----------- | ---------------------- | ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `priority`  | int                    | obligatorio        | Dónde se sitúa entre las tareas de la base. Menor se ejecuta primero                                                                                                   |
| `speed`     | número                 | el habitual de la tarea | Con qué rapidez se mueve mientras se ejecuta la tarea, como multiplicador de su velocidad al caminar                                                              |
| `nearSpeed` | número                 | `1.2`              | `avoidEntity`: el multiplicador una vez que lo que evita está cerca                                                                                                    |
| `distance`  | número, bloques        | el habitual de la tarea | Hasta dónde mira, sigue, dispara o se mantiene alejado                                                                                                            |
| `near`      | número, bloques        | el habitual de la tarea | `follow`, `followOwner`, `followOwnerFlying`: cuánto se acerca antes de detenerse                                                                                 |
| `chance`    | número                 | el habitual de la tarea | `wander`: una tirada cada tantos ticks; `wanderAvoidWater`: la probabilidad, de 0 a 1, de salir de la cobertura; `watchClosest`, `watchClosest2`: la probabilidad, de 0 a 1, de mirar en cada tick |
| `leap`      | número                 | `0.4`              | `leapAtTarget`: a qué altura llega el salto                                                                                                                            |
| `cooldown`  | int, ticks             | `20`               | `attackRanged`, `attackRangedBow`: ticks entre disparos                                                                                                                |
| `entity`    | nombre de entidad      | ninguno            | Qué entidad busca la tarea, evita, observa o con cuál se reproduce. `minecraft:player` se entiende                                                                     |
| `items`     | lista de nombres de ítem | ninguno          | `tempt`: lo que ofrece un jugador                                                                                                                                      |
| `sight`     | boolean                | `true`             | `nearestAttackableTarget`, `targetNonTamed`: solo lo que puede ver                                                                                                     |
| `nearby`    | boolean                | `false`            | `nearestAttackableTarget`: solo lo que está dentro de su propio rango de seguimiento                                                                                   |
| `help`      | boolean                | `false`            | `hurtByTarget`: otros de su especie cercanos se unen                                                                                                                   |
| `memory`    | boolean                | `false`            | `attackMelee`, `zombieAttack`: sigue tras un objetivo que perdió de vista                                                                                              |
| `close`     | boolean                | `false`            | `openDoor`: cierra la puerta tras de sí                                                                                                                                |
| `nocturnal` | boolean                | `false`            | `moveThroughVillage`: solo de noche                                                                                                                                    |
| `scared`    | boolean                | `false`            | `tempt`: un jugador que se mueve demasiado rápido rompe el hechizo                                                                                                     |

La columna `List` dice dónde vive la tarea. `tasks` es lo que hace la criatura; `targets` es cómo elige a qué ir, y una tarea de objetivo sin un ataque a juego no hace nada por sí sola.

| Tarea                     | Necesita                       | Lista     | Lee                                        | Qué hace                                                                       |
| ------------------------- | ------------------------------ | --------- | ------------------------------------------ | ------------------------------------------------------------------------------ |
| `attackMelee`             | una criatura terrestre         | `tasks`   | `speed`, `memory`                          | Camina hasta su objetivo y lo golpea                                           |
| `attackRanged`            | una base que dispara           | `tasks`   | `speed`, `cooldown`, `distance`            | Mantiene la distancia y dispara lo que dispare su base                         |
| `attackRangedBow`         | un monstruo que dispara        | `tasks`   | `speed`, `cooldown`, `distance`            | La pelea con arco del esqueleto: se desplaza lateralmente, tensa y suelta      |
| `avoidEntity`             | una criatura terrestre         | `tasks`   | `entity`, `distance`, `speed`, `nearSpeed` | Huye de la entidad nombrada cuando se acerca a menos de `distance`             |
| `beg`                     | un lobo                        | `tasks`   | `distance`                                 | Pide comida a un jugador que se la ofrece                                      |
| `breakDoor`               | cualquier base                 | `tasks`   |                                            | Rompe las puertas de madera que le estorban, en dificultad difícil             |
| `creeperSwell`            | un creeper                     | `tasks`   |                                            | Sisea y estalla junto a su objetivo                                            |
| `defendVillage`           | un gólem de hierro             | `targets` |                                            | Va a por quien atacó a un aldeano                                              |
| `eatGrass`                | cualquier base                 | `tasks`   |                                            | Come hierba, como una oveja                                                    |
| `findEntityNearest`       | cualquier base                 | `targets` | `entity`                                   | Fija como objetivo a la más cercana de la entidad nombrada, como un slime o un ghast |
| `findEntityNearestPlayer` | cualquier base                 | `targets` |                                            | Fija como objetivo al jugador más cercano que pueda alcanzar                   |
| `fleeSun`                 | una criatura terrestre         | `tasks`   | `speed`                                    | Busca sombra cuando le da el sol                                               |
| `follow`                  | cualquier base                 | `tasks`   | `speed`, `near`, `distance`                | Sigue a otros de su propia especie                                             |
| `followGolem`             | un aldeano                     | `tasks`   |                                            | Sigue a un gólem de hierro que ofrece una amapola                              |
| `followOwner`             | una base domesticable          | `tasks`   | `speed`, `near`, `distance`                | Sigue a su dueño, y se teletransporta tras él cuando se queda muy atrás        |
| `followOwnerFlying`       | una base domesticable          | `tasks`   | `speed`, `near`, `distance`                | Lo mismo, volando                                                              |
| `followParent`            | un animal                      | `tasks`   | `speed`                                    | Una cría se mantiene cerca de un adulto de su especie                          |
| `harvestFarmland`         | un aldeano                     | `tasks`   | `speed`                                    | Cosecha cultivos maduros y los vuelve a plantar                                |
| `hurtByTarget`            | una criatura terrestre         | `targets` | `help`                                     | Se defiende de lo que le golpeó                                                |
| `landOnOwnersShoulder`    | un loro                        | `tasks`   |                                            | Se sube al hombro de su dueño                                                  |
| `leapAtTarget`            | cualquier base                 | `tasks`   | `leap`                                     | Salta sobre su objetivo desde cerca                                            |
| `llamaFollowCaravan`      | una llama                      | `tasks`   | `speed`                                    | Se coloca detrás de una llama llevada con rienda                               |
| `lookAtTradePlayer`       | un aldeano                     | `tasks`   |                                            | Mira al jugador con el que comercia                                            |
| `lookAtVillager`          | un gólem de hierro             | `tasks`   |                                            | Mira a los aldeanos                                                            |
| `lookIdle`                | cualquier base                 | `tasks`   |                                            | Mira a su alrededor de vez en cuando                                           |
| `mate`                    | un animal                      | `tasks`   | `speed`, `entity`                          | Se reproduce cuando está en celo, con los de su especie o con la `entity` nombrada |
| `moveIndoors`             | una criatura terrestre         | `tasks`   |                                            | Entra en una casa de la aldea al anochecer                                     |
| `moveThroughVillage`      | una criatura terrestre         | `tasks`   | `speed`, `nocturnal`                       | Recorre los caminos de la aldea de puerta en puerta                            |
| `moveTowardsRestriction`  | una criatura terrestre         | `tasks`   | `speed`                                    | Vuelve hacia su punto de origen cuando se aleja                                |
| `moveTowardsTarget`       | una criatura terrestre         | `tasks`   | `speed`, `distance`                        | Se acerca a un objetivo que está lejos                                         |
| `nearestAttackableTarget` | una criatura terrestre         | `targets` | `entity`, `sight`, `nearby`                | Fija como objetivo a la más cercana de la entidad nombrada                     |
| `ocelotAttack`            | cualquier base                 | `tasks`   |                                            | El acecho y el salto del gato                                                  |
| `ocelotSit`               | un ocelote                     | `tasks`   | `speed`                                    | Se sienta sobre cofres, camas y hornos encendidos                              |
| `openDoor`                | cualquier base                 | `tasks`   | `close`                                    | Abre las puertas de madera que atraviesa                                       |
| `ownerHurtByTarget`       | una base domesticable          | `targets` |                                            | Va a por lo que golpeó a su dueño                                              |
| `ownerHurtTarget`         | una base domesticable          | `targets` |                                            | Va a por lo que golpeó su dueño                                                |
| `panic`                   | una criatura terrestre         | `tasks`   | `speed`                                    | Corre cuando le hieren o está en llamas                                        |
| `play`                    | un aldeano                     | `tasks`   | `speed`                                    | Los niños juegan a pillarse entre ellos                                        |
| `restrictOpenDoor`        | una criatura terrestre         | `tasks`   |                                            | Se queda dentro de las puertas de la aldea por la noche                        |
| `restrictSun`             | una criatura terrestre         | `tasks`   |                                            | Se mantiene a la sombra de día                                                 |
| `runAroundLikeCrazy`      | un caballo, burro, mula o llama | `tasks`  | `speed`                                    | Se encabrita contra un jinete en quien aún no confía                           |
| `sit`                     | una base domesticable          | `tasks`   |                                            | Se sienta cuando se le ordena                                                  |
| `skeletonRiders`          | un caballo esqueleto           | `tasks`   |                                            | Llama a jinetes esqueleto cuando se acerca un jugador, el caballo trampa       |
| `swimming`                | cualquier base                 | `tasks`   |                                            | Mantiene la cabeza fuera del agua                                              |
| `targetNonTamed`          | una base domesticable          | `targets` | `entity`, `sight`                          | Fija como objetivo a la entidad nombrada mientras aún no está domesticada      |
| `tempt`                   | una criatura terrestre         | `tasks`   | `items`, `speed`, `scared`                 | Sigue a un jugador que ofrece uno de los `items`                               |
| `tradePlayer`             | un aldeano                     | `tasks`   |                                            | Se queda quieto mientras comercia                                              |
| `villagerInteract`        | un aldeano                     | `tasks`   |                                            | Charla con otros aldeanos                                                      |
| `villagerMate`            | un aldeano                     | `tasks`   |                                            | Se reproduce cuando la aldea tiene sitio                                       |
| `wander`                  | una criatura terrestre         | `tasks`   | `speed`, `chance`                          | Deambula                                                                       |
| `wanderAvoidWater`        | una criatura terrestre         | `tasks`   | `speed`, `chance`                          | Deambula, evitando el agua                                                     |
| `wanderAvoidWaterFlying`  | una criatura terrestre         | `tasks`   | `speed`                                    | Deambula por el aire y se posa en los árboles                                  |
| `watchClosest`            | cualquier base                 | `tasks`   | `entity`, `distance`, `chance`             | Mira a la más cercana de la entidad nombrada, al jugador si no se nombra ninguna |
| `watchClosest2`           | cualquier base                 | `tasks`   | `entity`, `distance`, `chance`             | Lo mismo, mantenido mientras se ejecuta otra tarea                             |
| `zombieAttack`            | un zombi                       | `tasks`   | `speed`, `memory`                          | El ataque del zombi, con los brazos en alto                                    |

### Aparición y desaparición

*variantes de entidades*

| Clave                 | Obligatorio | Valor                    | Predeterminado     | Qué hace                                                                        |
| ------------------- | -------- | ------------------------ | ----------- | ----------------------------------------------------------------------------------- |
| `spawns`            | no       | lista de objetos         | ninguno     | `creatureType`, `weight`, `min` y `max`, con la misma forma que usa un bioma        |
| `biomes`            | no       | lista de nombres de bioma | todos los biomas | Dónde se añaden esas apariciones                                               |
| `biomeTypes`        | no       | lista de tipos del diccionario | ninguno | Lo mismo, por tipo                                                             |
| `ignoresSpawnRules` | no       | booleano                 | `false`     | Aparece donde se le coloque, ignorando las reglas heredadas                         |
| `despawns`          | no       | booleano                 | `true`      | Desactivado, se queda aunque normalmente se eliminaría                              |
| `despawnAfter`      | no       | int, segundos            | ninguno     | Se va sin hacer ruido cuando lleva este tiempo en el mundo, por lejos que esté cualquier jugador |
| `persistent`        | no       | booleano                 | `false`     | Nunca desaparece                                                                    |

**Una criatura con fecha de caducidad.** `despawnAfter` cuenta en segundos desde el momento en que una criatura entra por primera vez en el mundo y la retira sin ruido cuando se acaba el tiempo: sin muerte, sin drops, sin sonido, exactamente como si se hubiera ido por su cuenta y la hubieran eliminado. El reloj se guarda en la propia criatura, así que sigue corriendo al guardar y volver a cargar el mundo, en lugar de reiniciarse cada vez que un chunk vuelve a cargarse.

Es algo independiente, no un ajuste de las reglas que gobiernan `despawns` y `persistent`. Esas dos deciden si el juego puede eliminar a una criatura por estar lejos de todo el mundo; esta es una promesa de que se irá en un momento fijo, pase lo que pase. Una criatura puede ser `persistent` y tener aun así fecha de caducidad, que es justo lo que quieres para algo invocado para un combate o un evento y que no debe sobrevivirle.

El reloj corre con el tiempo del mundo, así que se detiene cuando nadie está jugando y no cuenta los minutos que un chunk pasó descargado.

### Red

*variantes de entidades*

| Clave                 | Obligatorio | Valor   | Predeterminado | Qué hace                                                                         |
| ------------------- | -------- | ------- | ------- | ------------------------------------------------------------------------------------ |
| `trackingRange`     | no       | int     | `80`    | A qué distancia se informa al cliente de su existencia                               |
| `trackVelocity`     | no       | booleano | `true` | Envía su velocidad además de su posición. Desactivado, ahorra tráfico en lo que apenas se mueve |
| `trackingFrequency` | no       | int     | `3`     | Cada cuánto, en ticks                                                                |

### Almacenamiento

*variantes de entidades*

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

`storage` da a una variante de cualquier entidad ranuras de ítems, un depósito de fluido y un búfer de energía, cada uno solo cuando se escribe su objeto. Cada uno se ofrece como la capacidad de ítems, fluido o energía de Forge de la entidad, de modo que cualquier cosa que mueva ítems, fluido o energía hacia una entidad llega hasta él. Allí donde la entidad base responde por sí misma a esa capacidad, como hace un mob con sus manos y su armadura o un caballo o una vagoneta con cofre con su inventario, el almacenamiento del pack responde en su lugar, por todos los lados. Un jugador abre la pantalla agachándose y haciendo clic derecho sobre la entidad. El contenido se guarda con la entidad.

| Clave            | Obligatorio | Valor                       | Predeterminado | Qué hace                                                                                                                                                                                                                                                                                                                                              |
| -------------- | -------- | --------------------------- | ------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `items`        | no       | objeto                      | ninguno | Da a la entidad ranuras de ítems. El área son tres filas de 9: cada barra de fluido o de energía ocupa una fila y las ranuras se quedan con el resto, así que 1x9 con ambas barras, 2x9 con una y 3x9 sin ninguna. Sin `items`, solo se muestran las barras                                                                                              |
| `fluid`        | no       | objeto                      | ninguno | Un depósito de fluido                                                                                                                                                                                                                                                                                                                                     |
| `energy`       | no       | objeto                      | ninguno | Un búfer de Forge Energy                                                                                                                                                                                                                                                                                                                                  |
| `dropsOnDeath` | no       | booleano                    | `true`  | Los ítems almacenados se esparcen como ítems sueltos donde muere la entidad. `false` los pierde. El fluido y la energía se pierden en ambos casos                                                                                                                                                                                                          |
| `runsDry`      | no       | `stops`, `slows` o `hurts`  | `stops` | Qué ocurre mientras no puede pagar un segundo completo de `use`. `stops`: deja de pensar y se queda donde está, con sus tareas, objetivos y comportamientos inactivos hasta que se rellene, aunque sigue cayendo y se le puede empujar. `slows`: se mueve a media velocidad. `hurts`: recibe 1 de daño cada segundo, como por inanición, así que `immuneTo` con `starve` se lo ahorra |

**Funcionar con lo que lleva encima.** Un `use` en el depósito o en el búfer es un coste continuo: una vez por segundo la entidad toma esa cantidad de él, sin tener en cuenta `transfer`, `buckets` ni los filtros. Cuando cualquiera de los dos contiene menos que un segundo completo de `use`, la entidad se ha quedado sin recursos: no se toma nada más, `runsDry` decide qué ocurre y todo vuelve a la normalidad en cuanto se rellena. Solo gasta una criatura; en una base que no está viva, como una vagoneta, `use` no hace nada.

`items`:

| Clave      | Obligatorio | Valor                  | Predeterminado | Qué hace                                           |
| -------- | -------- | ---------------------- | ------- | ------------------------------------------------------ |
| `filter` | no       | lista de entradas de filtro | ninguno | Lo que aceptan las ranuras. Sin él, aceptan cualquier cosa |

`fluid`:

| Clave        | Obligatorio | Valor                  | Predeterminado | Qué hace                                                                                                                                                              |
| ---------- | -------- | ---------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `capacity` | sí       | int, mB                | ninguno | Cuánto cabe en el depósito                                                                                                                                                |
| `filter`   | no       | lista de entradas de filtro | ninguno | Qué fluidos acepta el depósito. Sin él, acepta cualquiera                                                                                                            |
| `buckets`  | no       | booleano               | `false` | Un clic derecho con un cubo u otro recipiente de fluido, sin agacharse, lo vacía en el depósito o lo llena desde el depósito. Un clic que no mueve fluido se deja a la entidad |
| `use`      | no       | int, mB por segundo    | `0`     | Cuánto consume la entidad del depósito cada segundo que está viva. `0` no cuesta nada                                                                                     |

`energy`:

| Clave        | Obligatorio | Valor              | Predeterminado  | Qué hace                                                          |
| ---------- | -------- | ------------------ | -------- | --------------------------------------------------------------------- |
| `capacity` | sí       | int, FE            | ninguno  | Cuánta energía almacena                                               |
| `transfer` | no       | int, FE            | sin límite | La máxima energía que entra o sale en una operación                 |
| `use`      | no       | int, FE por segundo | `0`     | Cuánta energía consume cada segundo que está viva. `0` no cuesta nada |

Una entrada de filtro. Decide la primera entrada que coincida, y se rechaza todo lo que no coincida con ninguna:

| Clave       | Obligatorio         | Valor      | Predeterminado | Qué hace                                                                               |
| --------- | ---------------- | ---------- | ------- | ------------------------------------------------------------------------------------------ |
| `item`    | una de las tres  | nombre de ítem | ninguno | Un ítem, como `namespace:name` o `namespace:name:meta`                                 |
| `oreDict` | una de las tres  | nombre de mineral | ninguno | Todos los ítems bajo ese nombre del diccionario de minerales                        |
| `fluid`   | una de las tres  | nombre de fluido | ninguno | Un fluido por su nombre registrado, como `water`. Solo lo lee un filtro de fluidos   |
| `max`     | no               | int        | `0`     | La cantidad máxima que se guarda a la vez, contando todas las ranuras, o en mB para un fluido. `0` es sin límite |

### Cohetes de Galacticraft

*variantes de entidades*

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

El ítem que lo coloca, en `<namespace>/items/supply_rocket.json`:

```json
{
  "type": "rocket",
  "rocket": "mypack:supply_rocket",
  "variants": {
    "supply_rocket": { "meta": 0 }
  }
}
```

Una variante de un cohete de Galacticraft (`galacticraftcore:rocket_t1`, `galacticraftplanets:rocket_t2`, `galacticraftplanets:rocket_t3`) lee un objeto `galacticraft`. En cualquier otra entidad el objeto se ignora, y el registro lo indica. El cohete se coloca en una plataforma de lanzamiento con un ítem de tipo `rocket`. Al romper el cohete, o al aterrizar con él en otro cuerpo, se devuelve ese ítem con la carga y el combustible dentro.

| Clave               | Obligatorio | Valor                  | Predeterminado           | Qué hace                                                                                                                                      |
| ----------------- | -------- | ---------------------- | ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------- |
| `tier`            | no       | int                    | el del cohete base | El nivel del cohete, que decide los cuerpos a los que llega                                                                                      |
| `fuelTank`        | no       | int                    | el del cohete base | Tamaño del depósito de combustible. Un cohete de nivel 1 tiene `1000`                                                                            |
| `cargoSlots`      | no       | `0`, `27`, `36`, `54`  | `0`               | Los tamaños de carga de Galacticraft. `27` da 18 ranuras, como en los cohetes propios de Galacticraft                                             |
| `cargo`           | no       | lista de entradas de filtro | ninguno      | Lo que pueden meter las ranuras de carga y un Cargo Loader. Sin ella, aceptan cualquier cosa. Las entradas son las de `storage`, con `item` u `oreDict` |
| `requiredPayload` | no       | lista                  | ninguno           | Lo que debe haber en la carga antes de que despegue el cohete. Cada entrada es `item` u `oreDict` y un `count`, por defecto `1`                   |
| `payload`         | no       | lista                  | ninguno           | Lo que lleva un ítem de cohete recién fabricado, cargado en la carga cuando se coloca por primera vez. Cada entrada es `item` y un `count`, por defecto `1` |

## Órdenes de trabajo

*criaturas y peligros*

`<namespace>/orders/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta y varios archivos se suman. Un archivo contiene una lista `orders` y, junto a ella, tres ajustes para todo el paquete; el primer archivo que fija uno lo decide.

Una orden es un trabajo que las variantes de entidad nombradas como sus encargados hacen para un jugador: extraen los bloques que nombra dentro de su área, o traen sus ítems desde otros cofres, y llevan lo obtenido a su cofre. Todas las formas de abrir una funcionan desde un cliente vanilla:

- **Un cartel.** Coloca un cartel sobre un cofre o junto a él y escribe la palabra `sign` de la orden en la primera línea. La orden se abre en ese cofre, y la segunda línea del cartel muestra cuántos ítems faltan. Romper el cartel, o el cofre, la cancela.
- **Una herramienta.** Haz clic derecho sobre un trabajador, sin agacharte, con una herramienta del tipo `tool` de la orden. El trabajador toma la herramienta, la que tenía vuelve a ti, y trabaja la orden en el cofre más cercano dentro de su área.
- **Un cofre de existencias.** Renombra un cofre a `stockName` en un yunque y colócalo. Cada ítem de su primera fila se mantiene lleno hasta un montón completo: los trabajadores lo traen de otros cofres dentro del área de la primera orden `haul` del paquete, con los encargados, la prioridad y el área de esa orden.

```json
{
  "stockName": "Stock",
  "hire": "minecraft:emerald",
  "spawnCap": 1,
  "orders": [
    {
      "job": "mine",
      "blocks": "oreIron",
      "sign": "Mine",
      "tool": "pickaxe",
      "areaByTier": [8, 16, 24, 32],
      "limit": 64,
      "workers": 2,
      "priority": 1,
      "speed": 1.5,
      "takers": ["mypack:miner", "team:Red", "tag:digger"]
    },
    {
      "job": "farm",
      "blocks": "cropWheat",
      "sign": "Farm",
      "tool": "hoe",
      "standing": 64,
      "takers": ["mypack:farmer"]
    },
    {
      "job": "haul",
      "blocks": "logWood",
      "sign": "Haul",
      "deliver": "chest",
      "takers": ["mypack:porter"]
    }
  ]
}
```

Ajustes junto a `orders`:

| Clave       | Obligatorio | Valor          | Predeterminado | Qué hace                                                                                                                                                                                                              |
| ----------- | ----------- | -------------- | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `stockName` | no          | cadena         | `Stock`        | El nombre que convierte un cofre colocado en cofre de existencias, sin distinguir mayúsculas. Vacío desactiva los cofres de existencias                                                                               |
| `hire`      | no          | nombre de ítem | ninguno        | Contrata a un trabajador libre: haz clic derecho sobre él con este ítem, sin agacharte, y será tuyo y el ítem se gasta. Un trabajador libre no pertenece a ningún jugador ni equipo. Metadatos como `minecraft:dye:4` |
| `spawnCap`  | no          | int            | `1`            | Cuántos trabajadores puede generar una orden en su cofre cuando no acude ninguno. `0` no genera ninguno                                                                                                               |

Claves de la orden:

| Clave        | Obligatorio         | Valor                               | Predeterminado | Qué hace                                                                                                                                                                                                                                                                                                                                                                    |
| ------------ | ------------------- | ----------------------------------- | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `job`        | sí                  | `gather`, `mine`, `farm` o `haul`   |                | `gather` y `mine` extraen los bloques que nombra `blocks`, como el bloque mismo o como lo que suelta, siempre que un lado dé al aire o a un bloque que no sea un cubo completo. `farm` cosecha los cultivos maduros y vuelve a plantar cada uno con sus propias semillas. `haul` lleva los ítems que nombra `blocks` desde otros cofres y contenedores al cofre de la orden |
| `blocks`     | sí, salvo en `farm` | nombre del diccionario de minerales | ninguno        | Lo que quiere la orden, como `oreIron`, `logWood` o `cropWheat`, sin distinguir mayúsculas. En una orden `farm` limita la cosecha a los cultivos que lo sueltan                                                                                                                                                                                                             |
| `area`       | no                  | int                                 | `16`           | Hasta dónde llega el trabajo desde el cofre, en bloques por cada eje                                                                                                                                                                                                                                                                                                        |
| `areaByTier` | no                  | lista de int                        | ninguno        | El alcance según el nivel de extracción de la herramienta del trabajador: la primera entrada sin herramienta o con nivel 0, luego una entrada por nivel, y la última para cada nivel más allá del final. Sustituye a `area`                                                                                                                                                 |
| `deliver`    | no                  | `chest` o `self`                    | `chest`        | `chest` lleva lo recogido al cofre de la orden. `self` lo guarda en el almacenamiento propio del trabajador, y la orden lo cuenta al recogerlo                                                                                                                                                                                                                              |
| `limit`      | no                  | int                                 | `64`           | Cuántos ítems quiere la orden antes de cerrarse                                                                                                                                                                                                                                                                                                                             |
| `standing`   | no                  | int                                 | `0`            | Por encima de `0` la orden nunca se cierra: mantiene esa cantidad de sus ítems en el cofre y trabaja cuando hay menos. `limit` entonces no se lee                                                                                                                                                                                                                           |
| `workers`    | no                  | int                                 | `1`            | El máximo de trabajadores en la orden a la vez                                                                                                                                                                                                                                                                                                                              |
| `priority`   | no                  | int                                 | `0`            | Un trabajador ocioso toma primero la orden abierta de mayor prioridad, luego la más cercana                                                                                                                                                                                                                                                                                 |
| `takers`     | sí                  | lista                               |                | Quién puede trabajarla: un nombre de variante, `team:<team>` para cualquier miembro de un equipo del marcador, o `tag:<tag>` para cualquier entidad con esa etiqueta                                                                                                                                                                                                        |
| `sign`       | no                  | cadena                              | ninguno        | La palabra de la primera línea de un cartel que abre la orden, sin distinguir mayúsculas                                                                                                                                                                                                                                                                                    |
| `speed`      | no                  | número                              | `1`            | Multiplica la velocidad de extracción del trabajador. `2` extrae el doble de rápido que un jugador con la misma herramienta                                                                                                                                                                                                                                                 |
| `tool`       | no                  | tipo de herramienta                 | ninguno        | El tipo de herramienta, como `pickaxe`, `axe`, `shovel` o `hoe`, que da esta orden a un trabajador con clic derecho. Un trabajador cuya herramienta no puede extraer un bloque vuelve al cofre por una de este tipo                                                                                                                                                         |

**Cómo trabaja un trabajador.** Una variante que nombra un encargado, o todas las variantes cuando algún encargado es un equipo o una etiqueta, busca trabajo una vez por segundo mientras está ociosa. Toma la mejor orden abierta cuyo cofre esté dentro de su área más 32 bloques, la sostiene con un arriendo que caduca si deja de trabajar y conserva el arriendo al guardar y volver a cargar. Extraer tarda lo mismo que para un jugador con la herramienta del trabajador, Eficiencia incluida, y Fortuna cuenta para lo que suelta; Toque de seda no. Lo que extrae va a sus ítems de `storage` si la variante los tiene, si no a su mano secundaria, nunca al suelo. Cuando no puede cargar más, camina al cofre y lo deja todo dentro. Cada bloque desgasta la herramienta, y un trabajador cuya herramienta se rompe vuelve al cofre por otra del mismo tipo, o descansa un minuto si no la hay. Al morir, un trabajador suelta su herramienta y lo que tiene en las manos, y su almacenamiento se vacía una vez, como siempre lo hace el almacenamiento. `mobGriefing` no lo detiene, porque un jugador pidió el trabajo.

**Quién trabaja para quién.** Si el jugador que abre una orden está en un equipo del marcador, solo la toman los trabajadores de ese equipo. Si no, la orden pertenece a ese jugador, y un trabajador pertenece al primer jugador que le da un trabajo, con una herramienta, con `hire` o al tomar una orden de ese jugador; desde entonces solo trabaja las órdenes de ese jugador. Un trabajador libre contratado o al que da una herramienta un jugador de un equipo se une a ese equipo.

**Aparición en el cofre.** Una orden que lleva diez segundos sin trabajador genera a uno de sus encargados variante junto a su cofre, hasta `spawnCap` para esa orden. El trabajador generado pertenece al equipo o al jugador de la orden.

## Exposiciones

*criaturas y peligros*

`<namespace>/exposures/*.json`

La ruta del archivo es el nombre del peligro, y su mensaje de muerte sale de la clave de lang `death.attack.rdpl.<file name>`.

Un peligro definido por el pack: bloques, ítems y dimensiones con nombre exponen a los jugadores que están cerca de esos bloques, que llevan esos ítems o que permanecen en esas dimensiones, en niveles, y cada nivel aplica efectos y daño periódico. Un peligro también puede contagiarse desde mobs y jugadores cercanos, o caer con la lluvia ([Contagio y clima](#contagio-y-clima)). Un archivo define un peligro; varios funcionan a la vez. Los valores por defecto de cada clave son los números que usa la radiación de Immersive World.

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

| Clave                   | Obligatorio         | Valor                            | Predeterminado | Qué hace                                                                                    |
| --------------------- | ---------------- | -------------------------------- | ------- | ----------------------------------------------------------------------------------------------- |
| `blocks`              | una de las cinco | lista de `block` o `block=level` |         | Bloques que exponen a un jugador que está cerca de ellos. Sin nivel, es 1                       |
| `items`               | una de las cinco | lista de `item` o `item=level`   |         | Ítems que exponen a un jugador que los lleva o los viste                                        |
| `dimensions`          | una de las cinco | lista de `dim` o `dim=level`     |         | Ids numéricos de dimensión que exponen a cualquier jugador que esté en ellas                    |
| `levels`              | sí               | lista de niveles                 |         | La escala de gravedad, la primera entrada es el nivel 1. Un jugador recibe el nivel más alto que alcance cualquier fuente |
| `immunity`            | no               | nombre de poción                 | ninguno | Un efecto cuyo portador no queda expuesto en absoluto                                           |
| `scanInterval`        | no               | ticks                            | `20`    | Cada cuánto se comprueban el entorno y el inventario                                            |
| `range`               | no               | bloques                          | `10`    | Hasta dónde llega la exposición de un bloque, en forma de esfera                                |
| `sourcesForNextLevel` | no               | int                              | `0`     | Esta cantidad de fuentes cercanas de un mismo nivel lo sube un nivel más. `0` lo desactiva      |
| `skipsCreative`       | no               | booleano                         | `true`  | Se deja en paz a los jugadores en creativo y en espectador                                      |

### Niveles

*exposiciones*

Cada nivel:

| Clave              | Obligatorio | Valor           | Predeterminado | Qué hace                                                                                                                 |
| ---------------- | -------- | --------------- | ------- | ---------------------------------------------------------------------------------------------------------------------------- |
| `effect`         | sí       | nombre de poción |        | El efecto que marca el nivel en el jugador. Su presencia activa el daño, así que debería ser uno que el pack defina para esto |
| `damage`         | no       | medios corazones | `0`    | Daño infligido cada `damageInterval` ticks mientras se mantiene el nivel. Ignora la armadura                                 |
| `damageInterval` | no       | ticks           | `160`   | Cada cuánto se aplica ese daño                                                                                               |
| `effects`        | no       | lista de efectos | ninguno | Efectos adicionales aplicados a la vez, con la misma forma que usan los tipos de poción. Sin `duration`, siguen la ventana de escaneo |

Los efectos del nivel duran un poco más allá del siguiente escaneo, así que al alejarse se agotan por sí solos. La muerte por daño de exposición lee su mensaje de `death.attack.rdpl.<file name>`, que aportan los archivos de lang del pack.

### Contagio y clima

*exposiciones*

Dos fuentes más, escritas en el mismo archivo. Los portadores y los afectados expuestos transmiten el peligro a los receptores que tienen alrededor, y la lluvia o la tormenta exponen a los jugadores sobre los que caen.

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

| Clave                 | Obligatorio        | Valor                              | Predeterminado            | Qué hace                                                                                                            |
| ------------------- | --------------- | ---------------------------------- | ------------------ | ----------------------------------------------------------------------------------------------------------------------- |
| `carriers`          | una de las cinco | lista de `entity` o `entity=level` |                    | Mobs, o `minecraft:player`, que siempre transmiten el peligro a ese nivel. Sin nivel, es 1                              |
| `contagious`        | no              | booleano                           | `false`            | Cualquiera que esté expuesto transmite el peligro al nivel que tenga                                                    |
| `catchers`          | no              | lista de nombres de entidad        | `minecraft:player` | Quién puede contagiarse. Un mob solo se contagia de portadores y afectados, nunca de bloques, ítems o clima             |
| `contagionRange`    | no              | bloques                            | `4`                | Hasta dónde llega un portador o afectado, en forma de esfera                                                            |
| `contagionChance`   | no              | `0` a `1`                          | `0.1`              | La probabilidad, en cada escaneo del portador o afectado, de que cada receptor al alcance se contagie                   |
| `contagionDuration` | no              | ticks                              | `1200`             | Cuánto tiempo mantiene un peligro contagiado el nivel recibido. Volver a contagiarse reinicia el tiempo                 |
| `weather`           | una de las cinco | lista de `kind` o `kind=level`     |                    | `rain` expone a un jugador sobre el que cae la lluvia: cielo abierto encima, en un bioma donde llueve. `thunder` cuenta durante una tormenta |
| `weatherDimensions` | no              | lista de `dim`                     | todas las dimensiones | Ids numéricos de dimensión donde el clima expone                                                                     |

Un peligro contagiado cuenta como una fuente más en el escaneo, y gana el nivel más alto, como con cualquier otra, y `immunity` también protege contra él. Nada se propaga a menos que `contagionRange` y `contagionChance` estén por encima de `0` y el archivo nombre `carriers` o establezca `contagious`, y solo se examina a los mobs cuando algún archivo lo requiere.

---

# El mundo

## Plantillas de mundo

*el mundo*

`<namespace>/worldtemplates/*.json`

La ruta del archivo es el nombre de la plantilla, que la opción de configuración `worldTemplate` puede nombrar para elegirla directamente.

Reúne la forma de un mundo en un solo archivo, de modo que un pack entrega un mundo completo de una vez en lugar de pedir al jugador que establezca una docena de opciones de configuración.

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

| Clave          | Obligatorio | Valor                                               | Predeterminado         | Qué hace                                            |
| ------------ | -------- | --------------------------------------------------- | --------------- | ------------------------------------------------------- |
| `name`       | no       | cadena                                              | el nombre del archivo | Se muestra en el registro y en los informes       |
| `default`    | no       | nombre de bioma o `void`                            | `void`          | Lo que llena un bioma que el bloqueo ha eliminado       |
| `roles`      | no       | objeto de rol a bioma                               | ninguno         | Biomas que cubren funciones concretas, como océano o río |
| `structures` | no       | objeto de [nombre de estructura](#listas-de-valores) a booleano | ninguno | Estructuras de vanilla activadas o desactivadas         |
| `settings`   | no       | objeto                                              | ninguno         | Valores de configuración que establece la plantilla     |
| `dimensions` | no       | lista de ints                                       | todas las dimensiones | A qué dimensiones se aplica                       |

`settings` usa los mismos nombres de clave que la configuración, así que no hay tabla de traducción que aprender.

Qué plantilla está activa lo decide la opción de configuración `worldTemplate`. Con `auto`, gana el pack de mayor prioridad que incluya una, con el mismo orden que sigue todo lo demás. Nombrar una plantilla ahí la elige directamente.

## Reglas del juego

*el mundo*

`<namespace>/gamerules/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

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

Cada clave es el id del mundo al que pertenecen las reglas: `0` para el Overworld, `-1` para el Nether, `1` para el End, y lo que use un mod para los suyos. Los valores son cadenas, como en el comando `/gamerule`, así que `"false"` y no `false`. Se aplican a los mundos nuevos. Un archivo de dimensión lleva las mismas reglas en un bloque `gameRules`, que solo se aplica a ese mundo.

## Biomas

*el mundo*

`<namespace>/biomes/*.json`

La ruta del archivo es el nombre de registro del bioma, así que `mypack/biomes/ruby_forest.json` registra `mypack:ruby_forest`. `name` es solo lo que se muestra al jugador.

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita.

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

### El bioma

*biomas*

| Clave         | Obligatorio | Valor                              | Predeterminado          | Qué hace                                                                                                                                                                                                       |
| ----------- | -------- | ---------------------------------- | ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `name`      | no       | cadena                             | el nombre del archivo | Nombre que se muestra al jugador                                                                                                                                                                              |
| `id`        | no       | int                                | asignado automáticamente | Id de bioma fijo. Establécelo solo si necesitas que sea estable                                                                                                                                        |
| `types`     | no       | lista de tipos del diccionario     | deducido         | Registra el bioma bajo estos tipos, como `FOREST`, `COLD`, `WET` o `NETHER`, para que otros mods lo encuentren. Si se omite, Forge los deduce a ojo a partir del número de árboles, la altura, la temperatura, la lluvia y el bloque del suelo del bioma |
| `baseBiome` | no       | nombre de bioma                    | ninguno          | Un bioma existente del que copiar los ajustes                                                                                                                                                                      |
| `requires`  | no       | lista de ids de mod o espacios de nombres de pack | ninguno | El archivo se omite a menos que estén presentes todos                                                                                                                                                       |

### Clima

*biomas*

| Clave           | Obligatorio | Valor         | Predeterminado | Qué hace                              |
| ------------- | -------- | ------------- | ------- | ----------------------------------------- |
| `temperature` | no       | float         | `0.5`   | Por debajo de 0.15 nieva, por encima de 1.0 hace calor de desierto |
| `rainfall`    | no       | float, 0 a 1  | `0.5`   | Lo húmedo que es                          |
| `rain`        | no       | booleano      | `true`  | Si hay clima en absoluto                  |
| `snow`        | no       | booleano      | `false` | Si la lluvia cae como nieve               |

### Suelo y colores

*biomas*

| Clave               | Obligatorio | Valor      | Predeterminado          | Qué hace                                                          |
| ----------------- | -------- | ---------- | ---------------- | --------------------------------------------------------------------- |
| `baseHeight`      | no       | float      | `0.1`            | Altura del terreno. El nivel del mar es 0, las llanuras 0.125         |
| `heightVariation` | no       | float      | `0.2`            | Lo accidentado que es                                                 |
| `topBlock`        | no       | nombre de bloque | hierba     | El bloque de la superficie                                            |
| `fillerBlock`     | no       | nombre de bloque | tierra     | Justo debajo de la superficie                                         |
| `stoneBlock`      | no       | nombre de bloque | piedra     | El grueso del suelo                                                   |
| `waterColor`      | no       | color hex  | `FFFFFF`         | Tinte del agua                                                        |
| `grassColor`      | no       | color hex  | según el clima   | Tinte de la hierba, en lugar del color que darían la temperatura y la lluvia |
| `foliageColor`    | no       | color hex  | según el clima   | Tinte de las hojas, del mismo modo                                    |
| `snowColor`       | no       | color hex  | el de la dimensión | Tinte de la nieve del suelo, por encima del `snowColor` de la dimensión |

### Decoración y apariciones

*biomas*

| Clave                 | Obligatorio | Valor                                                                                        | Predeterminado        | Qué hace                                                                                                                                                                                                                                                                                                                                  |
| ------------------- | -------- | -------------------------------------------------------------------------------------------- | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `decoration`        | no       | objeto                                                                                       | los recuentos de vanilla | Recuentos por chunk. Los nombres que lee son `trees`, `flowers`, `grass`, `deadbush`, `mushrooms`, `bigmushrooms`, `reeds`, `cacti`, `sand`, `gravel`, `clay` y `waterlily`, además de `falls`, donde un valor mayor que cero hace que se generen lagos y manantiales, y `extratreechance`, una probabilidad porcentual de un árbol más. Cualquier otro nombre se registra y se ignora |
| `spawns`            | no       | lista de objetos                                                                             | la lista de vanilla | Véase más abajo                                                                                                                                                                                                                                                                                                                       |
| `keepDefaultSpawns` | no       | booleano                                                                                     | `false`        | Mantiene la lista de vanilla junto a la tuya                                                                                                                                                                                                                                                                                                  |
| `spawnChance`       | no       | float, menor que 1                                                                           | `0.1`          | Qué probabilidad hay de que se coloque otra manada al crearse el terreno por primera vez. El juego sigue tirando mientras tenga éxito, así que 1 no se detiene nunca y llena el mundo hasta quedarse sin espacio. Todo valor igual o superior a 0.99 se rechaza y se usa 0.99                                                                   |
| `spawnRates`        | no       | objeto de `surfaceDay`, `surfaceNight`, `undergroundDay`, `undergroundNight` a un multiplicador | ninguno     | Con qué frecuencia aparecen aquí los mobs hostiles, en lugar de los ajustes globales. Véase más abajo                                                                                                                                                                                                                                        |

Una entrada de aparición admite `entity` (obligatoria), `type` (`creature`, uno de los [tipos de criatura](#listas-de-valores)), `weight` (`10`), `min` (`1`) y `max` (`min`).

`spawnRates` se refiere solo a los mobs hostiles, y a nada más. Admite cuatro claves y ninguna otra: `surfaceDay` y `surfaceNight` para donde se ve el cielo, `undergroundDay` y `undergroundNight` para donde no. Cada una es un multiplicador de la frecuencia con la que se permite que aparezca un mob hostil: `1` es la tasa normal, `0` los detiene por completo, por debajo de 1 se descartan algunos intentos, y por encima de 1 se dejan pasar intentos que el juego habría rechazado, así que `2` es el doble. Una clave omitida significa que el bioma no decide, y se usa en su lugar el ajuste global para ese momento y ese lugar. Cualquier otra cosa escrita aquí no es una clave y se ignora, así que una tasa con el nombre de un tipo de criatura no hace absolutamente nada.

### Dónde se genera

*biomas*

| Clave           | Obligatorio | Valor                                    | Predeterminado | Qué hace                                                                                   |
| ------------- | -------- | ---------------------------------------- | ------- | ---------------------------------------------------------------------------------------------- |
| `placement`   | no       | objeto                                   | ninguno | Dónde se genera. Véase más abajo                                                               |
| `villageType` | no       | `oak`, `sandstone`, `acacia` o `spruce`  | ninguno | De qué se construye una aldea situada aquí. Vacío, construye con roble, como lo haría sin la clave |

`placement`:

| Clave            | Obligatorio | Valor   | Predeterminado | Qué hace                                 |
| -------------- | -------- | ------- | ------- | -------------------------------------------- |
| `climate`      | no       | cadena  | ninguno | A qué grupo climático de vanilla pertenece   |
| `weight`       | no       | int     | `10`    | Con qué frecuencia se elige frente a sus vecinos |
| `villages`     | no       | booleano | `false` | Pueden generarse aldeas                     |
| `villageSpawn` | no       | booleano | `true` | Pueden aparecer aldeanos en ellas            |
| `strongholds`  | no       | booleano | `false` | Pueden generarse fortalezas                 |
| `playerSpawn`  | no       | booleano | `false` | El punto de aparición del mundo puede colocarse aquí |

### Franjas de altura e islas flotantes

*biomas*

| Clave            | Obligatorio | Valor               | Predeterminado           | Qué hace                                                                                                                                                                                                                                                                                       |
| -------------- | -------- | ------------------- | ----------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `minHeight`    | no       | int                 | ninguno           | La y más baja desde la que este bioma toma el control como bioma 3D. Establecer cualquiera de las dos alturas convierte el bioma en una franja: la columna conserva su propio bioma fuera de ella, y dentro de ella cada celda de 4 por 4 por 4 del mundo informa de este. Solo en mundos Rubic, y se aplica al crearse el terreno, así que el terreno existente conserva lo que tenía |
| `maxHeight`    | no       | int                 | ninguno           | La y más alta de esa franja                                                                                                                                                                                                                                                                        |
| `replaces`     | no       | lista de nombres de bioma | todos los biomas | Restringe la franja a las columnas cuyo propio bioma se nombra aquí, de modo que una franja alpina pueda situarse sobre las montañas y nada más                                                                                                                                         |
| `skyStone`     | no       | nombre de bloque    | el ajuste del mundo | El bloque del que se hacen las islas flotantes bajo su superficie donde se aplica este bioma. En una franja, `topBlock` y `fillerBlock` pintan con él la superficie de la isla, así que una franja es la forma de dar a un tramo de cielo sus propias islas                                      |
| `skyIslands`   | no       | float, `-1` a `1`   | el ajuste del mundo | El umbral de las islas donde se aplica este bioma. Cuanto más bajo, más tierra reúne                                                                                                                                                                                                              |
| `skyThickness` | no       | float, `0` o más    | el ajuste del mundo | Lo sólidas que son las islas donde se aplica este bioma                                                                                                                                                                                                                                           |

### Temperatura según la altura

*biomas*

**Temperatura según la altura.** Un bioma se enfría a medida que sube, que es lo que pone nieve en las cimas de las montañas y detiene la lluvia por encima de una línea. Tres claves de `terrain` desplazan esa curva, lo que importa en un mundo Rubic donde el suelo puede estar muy por encima o por debajo de la altura que el juego da por supuesta. Los valores por defecto son lo que hace el juego, así que un pack que no los toque no cambia nada.

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

| Clave                            | Valor | Predeterminado     | Qué hace                                                                                                                                   |
| ------------------------------ | ----- | ----------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| `biomeTemperatureCenterY`      | int   | `64`        | La altura desde la que se mide la curva. A esa altura o por debajo, un bioma informa de su propia `temperature` sin tocar                      |
| `biomeTemperatureHeightFactor` | float | `-0.001667` | Cuánto varía la temperatura por bloque por encima de esa altura, los 0.05 del propio juego a lo largo de 30 bloques. Negativo enfría con la altitud, positivo calienta |
| `biomeTemperatureScaleMaxY`    | int   | `256`       | La altura en la que se detiene la curva, para que un mundo más alto que el del propio juego no siga enfriándose hasta su techo                |

## Dimensiones

*el mundo*

`<namespace>/dimensions/*.json`

La ruta del archivo da nombre a la dimensión para `suffix`, cuyo valor por defecto es `DIM_<name>`. La dimensión en sí se encuentra por su `id`, así que ese es el número al que remite todo lo demás.

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita.

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
    "rain": { "particle": "droplet", "sound": "minecraft:weather.rain", "volume": 0.2, "interval": 3, "color": "#88AAFF", "snowColor": "#FFFFFF", "angle": 30, "heading": 90 },
    "wind": { "gust": 15, "every": [200, 600], "swing": 30 }
  },
  "ambience": { "music": "mypack:music.ruby", "musicDelay": [1200, 3600], "loopSound": "mypack:ambient.ruby_wind", "ambientSound": "minecraft:ambient.cave", "soundChance": 0.0111, "particle": "reddust", "particleChance": 0.00625, "particleColor": "#FF4060" },
  "gameRules": { "doMobSpawning": "false" }
}
```

### Nivel superior

*dimensiones*

| Clave          | Obligatorio | Valor                              | Predeterminado      | Qué hace                                      |
| ------------ | -------- | ---------------------------------- | ------------ | ------------------------------------------------- |
| `id`         | sí       | int                                |              | El id de la dimensión. No debe chocar con el de otro mod |
| `suffix`     | no       | cadena                             | `DIM_<name>` | La carpeta de guardado                            |
| `keepLoaded` | no       | booleano                           | `false`      | La mantiene cargada cuando no hay nadie en ella   |
| `gameRules`  | no       | objeto                             | ninguno      | Reglas que se aplican solo aquí                   |
| `requires`   | no       | lista de ids de mod o espacios de nombres de pack | ninguno | El archivo se omite a menos que estén presentes todos |

### El bloque `terrain`

*dimensiones*

| Clave                | Obligatorio | Valor                                        | Predeterminado     | Qué hace                                     |
| ------------------ | -------- | -------------------------------------------- | ----------- | ------------------------------------------------ |
| `type`             | no       | `overworld`, `flat`, `void`, `nether`, `end` | `overworld` | Qué generador la construye                       |
| `generatorOptions` | no       | cadena                                       | ninguno     | La cadena del generador, como la usa un preset de mundo superplano |
| `structures`       | no       | booleano                                     | `true`      | Si se generan las estructuras de vanilla         |

### El bloque `biomes`

*dimensiones*

| Clave      | Obligatorio      | Valor               | Predeterminado            | Qué hace                                                            |
| -------- | ------------- | ------------------- | ------------------ | ----------------------------------------------------------------------- |
| `source` | no            | `inherit`, `single` | `inherit`          | `inherit` usa el mapa de biomas normal, `single` usa un solo bioma en todas partes |
| `biome`  | con `single`  | nombre de bioma     | `minecraft:plains` | Cuál es ese bioma                                                       |

### El bloque `sky`

*dimensiones*

| Clave                | Obligatorio | Valor                 | Predeterminado | Qué hace                                                                                                         |
| ------------------ | -------- | --------------------- | ------- | -------------------------------------------------------------------------------------------------------------------- |
| `hasSkyLight`      | no       | booleano              | `true`  | Si la luz del día llega hasta ella                                                                                   |
| `surfaceWorld`     | no       | booleano              | `true`  | Si los mapas y las brújulas se comportan como en el Overworld                                                        |
| `respawn`          | no       | booleano              | `true`  | Si los jugadores reaparecen aquí                                                                                     |
| `respawnDimension` | no       | int                   | ninguno | Dónde reaparecen en su lugar                                                                                         |
| `spawning`         | no       | booleano              | `true`  | Si aparecen mobs                                                                                                     |
| `nether`           | no       | booleano              | `false` | Se trata como el Nether para los portales y los techos                                                               |
| `beds`             | no       | booleano              | `true`  | Desactivado, las camas explotan                                                                                      |
| `waterVaporizes`   | no       | booleano              | `false` | El agua se evapora                                                                                                   |
| `cloudHeight`      | no       | int                   | `128`   | Dónde se sitúan las nubes                                                                                            |
| `cloudColor`       | no       | color hex             | ninguno | Tinte de las nubes                                                                                                   |
| `cloudSpeed`       | no       | float                 | `1.0`   | A qué velocidad se desplazan las nubes. `0` las deja quietas, un valor negativo las invierte                         |
| `cloudLayers`      | no       | lista de objetos      | ninguno | Varias capas de nubes. Véase [Niebla, luz, nubes y calor](#niebla-luz-nubes-y-calor)                                 |
| `groundLevel`      | no       | int                   | `63`    | Nivel del mar, usado para el horizonte y las búsquedas de punto de aparición                                         |
| `movementFactor`   | no       | float                 | `1.0`   | Relación de distancia con el Overworld. El Nether usa 8                                                              |
| `fogColor`         | no       | color hex o `sample`  | ninguno | Tinte de la niebla al mediodía. Se oscurece de noche como la niebla de vanilla. `sample` mezcla el cielo con el suelo que rodea al jugador |
| `showFog`          | no       | booleano              | `false` | Niebla espesa, como en el Nether                                                                                     |
| `fogDensity`       | no       | float, 0 a 1          | `0.0`   | Lo espesa que es la niebla. `0` mantiene la distancia de vanilla, `1` la cierra hasta 8 bloques                      |
| `fogGroundWeight`  | no       | float, 0 a 1          | `0.5`   | Con `fogColor: sample`, cuánto cuenta el suelo frente al cielo                                                       |
| `skyColor`         | no       | color hex             | ninguno | Tinte del cielo al mediodía. Se oscurece de noche y se agrisa con la lluvia y las tormentas como el cielo de vanilla |
| `fixedTime`        | no       | int, ticks            | ninguno | Fija la hora del día                                                                                                 |
| `sunriseColors`    | no       | booleano              | `true`  | Si el amanecer y el atardecer están teñidos                                                                          |
| `ambientLight`     | no       | float, 0 a 1          | `0.0`   | Luz mínima en todas partes                                                                                           |
| `lightSkyColor`    | no       | color hex             | ninguno | Tinte de la luz del día sobre bloques y mobs                                                                         |
| `lightBlockColor`  | no       | color hex             | ninguno | Tinte de la luz de las antorchas y de otras luces de bloque                                                          |
| `skyFactor`        | no       | float, 0 a 1          | `1.0`   | Lo brillante que parece la luz del día. Se dibuja solo en el cliente, así que la aparición de mobs no cambia         |
| `starBrightness`   | no       | float, 0 a 1          | ninguno | Lo brillantes que son las estrellas                                                                                  |
| `sunBrightness`    | no       | float, 0 a 1          | `1.0`   | Lo brillante que se dibuja el sol                                                                                    |
| `moonBrightness`   | no       | float, 0 a 1          | `1.0`   | Lo brillante que se dibuja la luna y, con `bodies`, todos los cuerpos salvo el sol                                   |
| `heat`             | no       | objeto                | ninguno | Un espejismo de calor sobre la vista. Véase [Niebla, luz, nubes y calor](#niebla-luz-nubes-y-calor)                  |
| `renderSky`        | no       | booleano              | `true`  | Desactivado, nada dibuja el cielo, el sol, la luna ni las estrellas, y solo queda el color de la niebla              |
| `renderClouds`     | no       | booleano              | `true`  | Desactivado, no se dibuja ninguna nube                                                                               |
| `renderWeather`    | no       | booleano              | `true`  | Desactivado, no se dibuja lluvia ni nieve                                                                            |
| `sun`              | no       | objeto                | ninguno | Tu propio sol. Véase [El renderizador del cielo](#el-renderizador-del-cielo)                                         |
| `bodies`           | no       | lista de objetos      | ninguno | Planetas y lunas colgados en el cielo. Véase [El renderizador del cielo](#el-renderizador-del-cielo)                 |
| `stars`            | no       | objeto                | ninguno | Tu propio campo de estrellas. Véase [El renderizador del cielo](#el-renderizador-del-cielo)                          |

### El renderizador del cielo

*dimensiones*

Establecer cualquiera de `sun`, `bodies` o `stars` cambia el cielo de vanilla por el propio de RDPL, que dibuja la misma cúpula, el mismo resplandor del amanecer y el mismo vacío que vanilla, pero toma el sol, los demás cuerpos y las estrellas del pack. Se dibuja solo en el cliente, y un servidor dedicado nunca lo carga. `renderSky: false` sigue ganando y no dibuja nada, y `renderClouds: false` es la forma de conseguir un cielo sin nubes.

Sin `bodies` se mantienen la luna de vanilla y sus fases. Con `bodies`, la lista es todo lo que no sea el sol, así que una lista vacía es un cielo sin luna.

| Clave                    | Obligatorio | Valor          | Predeterminado         | Qué hace                                                                                                                                                                                     |
| ---------------------- | -------- | -------------- | --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `sun.texture`          | no       | ruta de textura | el sol de vanilla | La imagen del sol                                                                                                                                                                              |
| `sun.size`             | no       | float          | `30`            | La mitad del ancho del sol a una distancia de 100. `0` lo oculta                                                                                                                                 |
| `bodies[].texture`     | sí       | ruta de textura |                | La imagen del cuerpo                                                                                                                                                                             |
| `bodies[].size`        | no       | float          | `20`            | La mitad de su ancho a una distancia de 100. La luna de vanilla es `20`                                                                                                                          |
| `bodies[].angle`       | no       | float, grados  | `180`           | Cuánto avanza por el recorrido del sol por detrás de este. `180` es donde está la luna de vanilla. Con `followsTime` desactivado se mide desde justo encima, así que `0` es el cénit y `90` el horizonte |
| `bodies[].tilt`        | no       | float, grados  | `0`             | Cuánto se aparta del recorrido del sol, hacia el norte o el sur                                                                                                                                  |
| `bodies[].followsTime` | no       | booleano       | `true`          | Desactivado, permanece inmóvil en el cielo en lugar de girar con el sol                                                                                                                          |
| `stars.count`          | no       | int            | `1500`          | Cuántas estrellas                                                                                                                                                                                |
| `stars.size`           | no       | float          | `0.15`          | La estrella más pequeña; la mayor es dos tercios más grande                                                                                                                                      |

### Niebla, luz, nubes y calor

*dimensiones*

Estas claves van en el bloque `sky` junto a las más antiguas, que siguen funcionando como antes. Todas se dibujan solo en el cliente, así que un servidor dedicado las ignora y ningún guardado cambia.

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

`fogColor: "sample"` lee los bloques superiores de un cuadrado de 33 por 33 bloques alrededor del jugador una vez por segundo, ilumina sus colores de mapa según la hora del día y los mezcla con el color del cielo. La niebla se acerca poco a poco a cada nueva muestra. `fogDensity` funciona con un color de niebla muestreado, con uno fijo o sin ninguno. Bajo el agua, en la lava y mientras se está cegado se mantiene la niebla de vanilla.

`lightSkyColor` y `lightBlockColor` tiñen el mapa de luz, así que cada bloque iluminado y cada mob adoptan el tinte. `skyFactor` escala lo brillante que parece la luz del día, mientras que el nivel de luz que el servidor cuenta para la aparición de mobs y los cultivos sigue igual.

Sin `cloudLayers`, `cloudSpeed` cambia la velocidad de la única capa de vanilla situada en `cloudHeight`. Con `cloudLayers`, cada entrada es una capa propia, y `cloudHeight`, `cloudSpeed` y `cloudColor` rellenan lo que una entrada deje sin indicar. `renderClouds: false` sigue sin dibujar ninguna.

`sunBrightness` y `moonBrightness` atenúan el sol y la luna además del desvanecimiento por lluvia de vanilla, tanto en el cielo de vanilla como en el tuyo de [El renderizador del cielo](#el-renderizador-del-cielo).

`heat` extiende un espejismo ondulante sobre la vista mientras el jugador está en un bioma al menos tan cálido como `minTemperature`. Un desierto es 2.0 y las llanuras 0.8. El espejismo aparece y se desvanece a lo largo de unos segundos y no se muestra bajo el agua. Necesita compatibilidad con shaders de la tarjeta gráfica y se desactiva mientras haya otro shader de pantalla completa activo, como la vista de espectador.

`mode` decide dónde se aplica el espejismo. `screen` deforma una franja fija en la parte inferior de la pantalla, mire donde mire el jugador. `world` sigue el terreno: lo que queda a menos de `startDistance` bloques se mantiene nítido, el espejismo va creciendo hacia el borde lejano de la distancia de renderizado, donde se cierra la niebla, y el cielo nunca se ve afectado, tanto si el jugador mira hacia abajo, al frente o hacia arriba.

| Clave                    | Obligatorio | Valor         | Predeterminado       | Qué hace                                                         |
| ---------------------- | -------- | ------------- | ------------- | -------------------------------------------------------------------- |
| `cloudLayers[].height` | no       | float         | `cloudHeight` | Dónde se sitúa la capa                                               |
| `cloudLayers[].speed`  | no       | float         | `cloudSpeed`  | A qué velocidad se desplaza. `0` la deja quieta, un valor negativo la invierte |
| `cloudLayers[].color`  | no       | color hex     | `cloudColor`  | Su tinte                                                             |
| `heat.strength`        | no       | float, 0 a 1  | `0.1`         | Lo intenso que es el espejismo                                       |
| `heat.minTemperature`  | no       | float         | `1.5`         | La temperatura de bioma más baja que produce espejismo               |
| `heat.dayOnly`         | no       | booleano      | `true`        | Activado, el espejismo se desvanece con la luz del día y desaparece de noche |
| `heat.mode`            | no       | string        | `screen`      | Dónde se aplica el espejismo, `screen` o `world` |
| `heat.startDistance`   | no       | float         | `32`          | En el modo `world`, a cuántos bloques de distancia empieza el espejismo |

### Nieve, fluidos, estrellas y rayos

*dimensiones*

Estas claves también van en el bloque `sky` y también se dibujan solo en el cliente.

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

`snowColor` tiñe las capas de nieve y los bloques de nieve del suelo. El `snowColor` propio de un bioma tiene prioridad sobre el de la dimensión, y los colores se funden en los bordes entre biomas como lo hace la hierba.

`waterFogColor` y `lavaFogColor` sustituyen el color de la niebla que ve la cámara bajo el agua o dentro de la lava. La noche, la profundidad y la visión nocturna lo siguen oscureciendo o aclarando como hacen con el color de vanilla.

`starColor` tiñe las estrellas, tanto en el cielo de vanilla como en el tuyo de [El renderizador del cielo](#el-renderizador-del-cielo). `starTwinkle` las hace titilar: las estrellas se reparten en ocho grupos que se apagan y se avivan cada uno a su ritmo, y el valor indica cuánto se apagan; con `1` un grupo desaparece del todo en su punto más bajo.

`lightningColor` tiñe los rayos.

| Clave            | Obligatorio | Valor        | Por defecto | Qué hace |
| ---------------- | ----------- | ------------ | ----------- | -------- |
| `snowColor`      | no          | color hex    | blanco      | Tinte de las capas y los bloques de nieve |
| `waterFogColor`  | no          | color hex    | `050533`    | Color de la niebla bajo el agua |
| `lavaFogColor`   | no          | color hex    | `991A00`    | Color de la niebla en la lava |
| `starColor`      | no          | color hex    | blanco      | Tinte de las estrellas |
| `starTwinkle`    | no          | float, 0 a 1 | `0.0`       | Cuánto se apagan las estrellas al titilar. `0` las deja fijas |
| `lightningColor` | no          | color hex    | `737380`    | Tinte de los rayos |

### Skybox, aurora y arcoíris

*dimensions*

Estas claves también van en el bloque `sky`, y también se dibujan solo en el cliente.

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

`skybox` pinta tus propias imágenes en el cielo, detrás del resplandor del amanecer, el sol, la luna y las estrellas. Indica las seis caras de un cubo como rutas de textura completas, dispuestas como el cubo desplegado: `up` toca el borde superior de `north`, `down` su borde inferior, `west` queda a su izquierda y `east` a su derecha, con `south` después de `east`. O bien indica solo `panorama`, una imagen 2:1 que envuelve todo el cielo: su borde izquierdo mira al norte y recorre el cielo en el sentido de las agujas del reloj por el este, el sur y el oeste; su fila superior queda justo encima y la inferior justo debajo. Una skybox a la que le falta una cara y no tiene panorama se omite, con un error en el registro.

`aurora` cuelga cortinas luminosas bajas sobre el cielo del norte durante la noche. Ondean despacio, aparecen al ponerse el sol y desaparecen de día y con lluvia. `color` es el color de su base y `topColor` aquel en el que se desvanecen arriba.

`rainbow` muestra un arcoíris frente al sol cuando la lluvia para de día. Se desvanece en los dos minutos siguientes al final de la lluvia, y un nuevo chaparrón lo borra.

Ninguna clave de vanilla da a una dimensión sus propias imágenes de cielo, una aurora o un arcoíris, así que estas claves funcionan igual en todas las versiones.

| Clave | Obligatorio | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `skybox.<face>` | no | ruta de textura | ninguna | Una cara del cubo: `up`, `down`, `north`, `east`, `south` o `west`. Hacen falta las seis |
| `skybox.panorama` | no | ruta de textura | ninguna | Una imagen que envuelve todo el cielo, en lugar de las caras |
| `aurora.color` | no | color hexadecimal | `40FF90` | Color en la base de las cortinas |
| `aurora.topColor` | no | color hexadecimal | `8040FF` | Color arriba, donde las cortinas se desvanecen |
| `rainbow` | no | booleano | `false` | Mostrar un arcoíris después de la lluvia |

### El bloque `physics`

*dimensiones*

| Clave            | Obligatorio | Valor          | Predeterminado           | Qué hace                                                            |
| -------------- | -------- | -------------- | ----------------- | ----------------------------------------------------------------------- |
| `gravity`      | no       | float, mayor que 0 | `1.0`         | Aceleración de caída aquí, como multiplicador de vanilla. `0.17` es como la de la Luna |
| `fallDamage`   | no       | float, mayor que 0 | `1.0`         | Daño por caída aquí, como multiplicador                                 |
| `arrowGravity` | no       | float, mayor que 0 | sigue a `gravity` | Con qué rapidez caen aquí las flechas, como multiplicador            |

Son los mismos multiplicadores que en [Física del mundo](#física-del-mundo), establecidos en la dimensión. La línea `dimension=value` de una plantilla de mundo para esta dimensión sigue ganando; un valor suelto de la plantilla de mundo cubre solo las dimensiones que no establecen nada propio. En un [cuerpo de Galacticraft](#cuerpos-de-galacticraft) los aplica Galacticraft.

### El bloque `time`

*dimensiones*

| Clave         | Obligatorio | Valor      | Predeterminado | Qué hace                                                                             |
| ----------- | -------- | ---------- | ------- | ---------------------------------------------------------------------------------------- |
| `dayLength` | no       | int, ticks | `24000` | Cuánto dura aquí un día con su noche. La fase de la luna sigue dando una vuelta cada 24000 ticks |

### El bloque `weather`

*dimensiones*

| Clave                     | Obligatorio | Valor                  | Predeterminado                  | Qué hace                                                                                                     |
| ----------------------- | -------- | ---------------------- | ------------------------ | ---------------------------------------------------------------------------------------------------------------- |
| `precipitation`         | no       | booleano               | `true`                   | Desactivado, aquí nunca llueve, nieva ni hay tormentas                                                           |
| `lightning`             | no       | booleano               | `true`                   | Desactivado, la lluvia y las tormentas llegan sin rayos                                                          |
| `snow`                  | no       | booleano               | `true`                   | Desactivado, la nieve nunca se acumula                                                                           |
| `freeze`                | no       | booleano               | `true`                   | Desactivado, el agua nunca se congela                                                                            |
| `cycle.rainTicks`       | no       | int o `[min, max]`     | `[1000, 4600]`           | Cuánto dura un chaparrón                                                                                         |
| `cycle.clearTicks`      | no       | int o `[min, max]`     | `[1000, 3000]`           | Cuánto dura el periodo seco entre chaparrones                                                                    |
| `cycle.maxStrength`     | no       | float, mayor que 0 hasta 1 | `0.6`                | Lo fuerte que llega a ser un chaparrón. Cada chaparrón oscila entre una cuarta parte de este valor y su totalidad |
| `cycle.thunderTicks`    | no       | int o `[min, max]`     | ninguno                  | Cuánto dura una tormenta eléctrica. Sin él, el ciclo nunca produce tormentas                                     |
| `cycle.calmTicks`       | no       | int o `[min, max]`     | `[12000, 180000]`        | Cuánto dura la calma entre tormentas                                                                             |
| `cycle.thunderStrength` | no       | float, mayor que 0 hasta 1 | `1`                  | Lo oscura que se pone una tormenta. Los rayos caen solo por encima de `0.9`                                      |
| `rain.particle`         | no       | nombre de partícula    | `droplet`                | Lo que salpica donde cae la lluvia                                                                               |
| `rain.sound`            | no       | nombre de sonido       | `minecraft:weather.rain` | El sonido de la lluvia                                                                                           |
| `rain.volume`           | no       | float                  | `0.2`                    | Su volumen, reducido a la mitad cuando la lluvia cae sobre ti                                                    |
| `rain.interval`         | no       | int                    | `3`                      | Lo espaciado que suena; más alto es más escaso, `0` lo reproduce en cada ocasión                                 |
| `rain.color`            | no       | color hex              | `#FFFFFF`                | Tinte de la lluvia al caer                                                                                       |
| `rain.snowColor`        | no       | color hex              | `#FFFFFF`                | Tinte de la nieve al caer                                                                                        |
| `rain.angle`            | no       | float, 0 a 180         | `0`                      | Grados respecto a la vertical hacia abajo: `90` sopla de lado, `180` sube en vertical. Se dibuja inclinada 75 grados como máximo |
| `rain.heading`          | no       | float, grados          | `0`                      | Hacia dónde sopla: `0` sur, `90` oeste, `180` norte, `270` este                                                  |
| `rain.splashUpward`     | no       | booleano               | `false`                  | Activado, la lluvia que sube (`angle` mayor que `90`) sigue salpicando el suelo y sonando                        |
| `wind.gust`             | no       | float, 0 a 90          | `15`                     | Grados que una ráfaga suma a `angle` en su punto máximo, sin pasar nunca de la horizontal                        |
| `wind.every`            | no       | int o `[min, max]`     | `[200, 600]`             | Ticks entre una ráfaga y la siguiente                                                                            |
| `wind.swing`            | no       | float, 0 a 180         | `30`                     | Grados que una ráfaga desvía `heading` hacia un lado                                                             |

Un bloque `wind` vuelve racheada la lluvia. De vez en cuando, una ráfaga la inclina hasta `gust` grados más y desvía su rumbo hasta `swing` grados hacia un lado; crece y se apaga en menos de cuatro segundos, y las ráfagas llegan cada `every` ticks. La lluvia y la nieve se inclinan con ella, y las partículas ambientales de la dimensión derivan hacia donde se inclina la lluvia, haya ráfagas o no. Un bloque `wind` sin bloque `rain` deja la lluvia con sus valores por defecto.

Las demás dimensiones comparten la lluvia del Overworld. Un `cycle` da a esta un clima propio: los chaparrones vienen y van con los tiempos indicados arriba, haga lo que haga el Overworld. Con `thunderTicks` también hay tormentas, con tiempos propios; una tormenta que coincide con un chaparrón lleva el chaparrón a toda su fuerza, oscurece el cielo y, con `lightning` activado, trae rayos. `weatherCeiling` en una [plantilla de mundo](#plantillas-de-mundo) sigue limitando hasta dónde llega la lluvia.

Un bloque `rain` cambia el aspecto y el sonido de la lluvia y la nieve aquí, con o sin `cycle`; sin él tienen el aspecto y el sonido de vanilla. Funciona igual en un planeta o una luna de Galacticraft.

### El bloque `ambience`

*dimensiones*

| Clave            | Obligatorio | Valor               | Predeterminado   | Qué hace                                                                                                                                                                                                                      |
| ---------------- | ----------- | ------------------- | ---------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `music`          | no          | nombre de sonido    | ninguno          | Música que suena aquí en lugar de las pistas habituales, también en creativo. Al llegar, corta la pista que suena                                                                                                             |
| `musicDelay`     | no          | int o `[min, max]`  | `[12000, 24000]` | Ticks de silencio entre dos pistas                                                                                                                                                                                            |
| `loopSound`      | no          | nombre de sonido    | ninguno          | Un sonido que se repite en bucle mientras estás aquí; sube al llegar y se apaga al salir                                                                                                                                      |
| `ambientSound`   | no          | nombre de sonido    | ninguno          | Un sonido que se reproduce de vez en cuando, como los biomas modernos añaden los suyos. El servidor lo envía solo a ese jugador                                                                                               |
| `soundChance`    | no          | 0.0 a 1.0           | `0.0111`         | La probabilidad, en cada tick, de que se reproduzca `ambientSound`                                                                                                                                                            |
| `particle`       | no          | nombre de partícula | ninguno          | Una partícula que flota en el aire a tu alrededor, uno de los nombres de partícula del juego, como `depthsuspend`, `townaura`, `reddust` o `mobSpellAmbient`                                                                  |
| `particleChance` | no          | 0.0 a 1.0           | `0.00625`        | Su densidad, contada como en los biomas modernos: en cada tick se prueban unos 667 puntos a menos de 16 bloques y otros 667 a menos de 32, y cada uno que no es un bloque completo muestra la partícula con esta probabilidad |
| `particleColor`  | no          | color hex           | ninguno          | El tinte de una partícula que lo admite: `reddust`, `mobSpell` y `mobSpellAmbient`                                                                                                                                            |

Un bloque `ambience` da a la dimensión música, sonidos y partículas flotantes propios, como los tiene un bioma moderno. El ajuste de vídeo «Partículas» las reduce igual que las de vanilla, y los sonidos de cueva del juego siguen sonando.

## Cuerpos de Galacticraft

*el mundo*

`<namespace>/celestial/*.json` y el bloque `galacticraft` de `<namespace>/dimensions/*.json`

Con Galacticraft instalado, un pack puede poner sus propios sistemas estelares, planetas, lunas, cinturones de asteroides y estaciones espaciales en el mapa estelar de Galacticraft, y hacer que una dimensión del pack sea un lugar al que vuela un cohete. Sin Galacticraft se omite todo: los archivos de `celestial/` se ignoran y una dimensión con un bloque `galacticraft` no se registra, y el registro lo indica.

El nombre de un cuerpo en el mapa sale del archivo de lang del pack, bajo la clave que usa Galacticraft: `solarsystem.<name>`, `star.<name>`, `planet.<name>`, `moon.<name>` o, para una estación, `satellite.<name>`. Un cinturón de asteroides usa `planet.<name>` alrededor de una estrella y `moon.<name>` alrededor de un planeta.

### Sistemas estelares y cuerpos solo de mapa

*cuerpos de galacticraft*

`<namespace>/celestial/*.json`

Un archivo aquí es un sistema estelar o un planeta o una luna que figura en el mapa sin ningún lugar donde aterrizar.

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

| Clave           | Obligatorio     | Valor                              | Predeterminado     | Qué hace                               |
| ------------- | ------------ | ---------------------------------- | ----------- | ------------------------------------------ |
| `kind`        | sí           | `system`, `planet`, `moon`         |             | Qué crea el archivo                        |
| `galaxy`      | no           | cadena                             | `milky_way` | La galaxia de un sistema                   |
| `mapPosition` | en un sistema | `[x, y]` o `[x, y, z]`            |             | Dónde se sitúa el sistema en el mapa de la galaxia |
| `star`        | no           | objeto de [claves de mapa](#claves-de-mapa) |    | La estrella del sistema, dibujada en su centro |
| `requires`    | no           | lista de ids de mod o espacios de nombres de pack | ninguno | El archivo se omite a menos que estén presentes todos |

Un planeta o una luna aquí admite las [claves de mapa](#claves-de-mapa) con un `tier` por defecto de `0`. Los planetas y las lunas se colocan en el mapa después de todos los sistemas, así que un planeta puede orbitar un sistema del mismo pack.

### Claves de mapa

*cuerpos de galacticraft*

Todo cuerpo, ya sea un archivo de `celestial/`, la `star` de un sistema o el bloque `galacticraft` de una dimensión, se sitúa en el mapa con estas claves.

| Clave              | Obligatorio              | Valor          | Predeterminado                                                        | Qué hace                                                                                                                                                                                                                                     |
| ---------------- | --------------------- | -------------- | -------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `name`           | no                    | cadena         | el nombre del archivo                                          | El nombre del cuerpo, que usan su clave de lang y todo `parent`                                                                                                                                                                                  |
| `parent`         | en una luna o estación | nombre        | `sol` para un planeta o cinturón                               | El sistema que orbita un planeta, el planeta que orbita una luna, el planeta o la luna que orbita una estación, o un sistema o un planeta para un cinturón de asteroides                                                                         |
| `icon`           | no                    | ruta de textura | el icono de Marte, la Luna, el Sol, asteroide o estación de Galacticraft | La imagen en el mapa                                                                                                                                                                                                          |
| `relativeSize`   | no                    | float          | `1.0`, una luna o estación `0.2667`                            | Su tamaño en el mapa                                                                                                                                                                                                                             |
| `distance`       | no                    | float          | `1.0`, una luna `13`, una estación `9`                         | A qué distancia orbita                                                                                                                                                                                                                           |
| `scaledDistance` | no                    | float          | `distance`                                                     | La distancia usada en el mapa ampliado                                                                                                                                                                                                           |
| `orbitTime`      | no                    | float, años    | `1.0`, una luna `100`, una estación `20`                       | Cuánto tarda una órbita en el mapa. Un valor negativo va hacia atrás                                                                                                                                                                             |
| `phaseShift`     | no                    | float, radianes | `0`, una estación separada de las demás                       | Dónde empieza a lo largo de su órbita. Una estación sin ella empieza 2.4 radianes después de la última estación alrededor del mismo planeta, incluidas las de sus lunas, de modo que no haya dos en el mismo punto                               |
| `ringColor`      | no                    | color hex      | `19E599`                                                       | La línea de la órbita, donde el mapa dibuja una                                                                                                                                                                                                  |
| `tier`           | no                    | int            | `1` para una dimensión, el de su padre para una estación, `0` en los demás casos | El nivel de cohete que el mapa muestra como necesario. Con GalaxySpace, el mapa de AsmodeusCore calcula el nivel a partir de la distancia, y un cuerpo alrededor de otra estrella necesita el nivel más alto, salvo que `enableNewTierSystem` esté desactivado en `config/AsmodeusCore/core.conf` |

### El bloque `galacticraft`

*cuerpos de galacticraft*

`<namespace>/dimensions/*.json`

Un bloque `galacticraft` en un archivo de dimensión convierte esa dimensión en un planeta o una luna propios, o en un [cinturón de asteroides](#cinturones-de-asteroides) o una [estación espacial](#estaciones-espaciales). Todo lo que queda fuera del bloque, el cielo, la física, el tiempo y el clima, sigue siendo de la dimensión y funciona igual con o sin Galacticraft; el bloque contiene solo lo que lee Galacticraft.

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

El bloque admite las [claves de mapa](#claves-de-mapa), además de:

| Clave               | Obligatorio | Valor                                    | Predeterminado                      | Qué hace                                                                                                                                 |
| ----------------- | -------- | ---------------------------------------- | ---------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| `kind`            | no       | `planet`, `moon`, `asteroids`, `station` | `planet`                     | Qué es la dimensión                                                                                                                          |
| `reachable`       | no       | booleano                                 | `true`                       | Desactivado, figura en el mapa pero ningún cohete va allí                                                                                    |
| `minTier`         | no       | int                                      | `tier`                       | El nivel de cohete más bajo que puede aterrizar                                                                                              |
| `landing`         | no       | `lander`, `parachute`, `balloons`        | `lander`                     | Cómo desciende un jugador. `balloons` necesita Galacticraft Planets y es un módulo de aterrizaje sin él. `disableLander` de Galacticraft fuerza `parachute` |
| `landingHeight`   | no       | float                                    | `900`, un paracaídas `250`   | La altura a la que llega un jugador                                                                                                          |
| `arrival`         | no       | `departure`, `spawn`                     | `departure`                  | Aterrizar sobre el punto de despegue del cohete o sobre el punto de aparición de esta dimensión                                              |
| `exitHeight`      | no       | float                                    | `1200`                       | La altura a la que un cohete que sale de esta dimensión la abandona                                                                          |
| `rocketGui`       | no       | ruta de textura                          | la del Overworld de Galacticraft | La pantalla de vuelo                                                                                                                     |
| `checklist`       | no       | lista de cadenas                         | ninguno                      | Claves de la lista de comprobación de Galacticraft mostradas antes del despegue                                                              |
| `meteorFrequency` | no       | float                                    | según `density`              | Lo poco frecuentes que son las caídas de meteoritos, cerca de cada jugador una vez cada tantas veces 750 ticks. `0` las detiene             |
| `fuelMultiplier`  | no       | float                                    | `1.0`                        | Combustible que quema un cohete al partir de aquí                                                                                            |
| `soundReduction`  | no       | float                                    | según `density`              | Cuánto más bajo suena el sonido en este aire                                                                                                 |
| `solarEnergy`     | no       | float                                    | `1.0`                        | Producción de los paneles solares aquí                                                                                                       |
| `netherPortals`   | no       | booleano                                 | `false`                      | Si los portales del Nether se encienden aquí                                                                                                 |

| Clave de `atmosphere` | Obligatorio | Valor                                                                                  | Predeterminado                       | Qué hace                                                                               |
| ---------------- | -------- | -------------------------------------------------------------------------------------- | ----------------------------- | ------------------------------------------------------------------------------------------ |
| `gases`          | no       | lista de `NITROGEN`, `OXYGEN`, `CO2`, `WATER`, `METHANE`, `HYDROGEN`, `HELIUM`, `ARGON` | ninguno                      | El aire. Ninguno es el vacío, y el fuego necesita `OXYGEN`                                 |
| `breathable`     | no       | booleano                                                                               | oxígeno y sin CO2             | Si los jugadores respiran sin traje                                                        |
| `corrosive`      | no       | booleano                                                                               | `false`                       | Corroe la armadura sin un controlador de escudo                                            |
| `temperature`    | no       | float                                                                                  | `0`                           | El nivel térmico de Galacticraft. Por debajo de 0 es frío y por encima es calor; el relleno térmico lo contrarresta |
| `wind`           | no       | float                                                                                  | `1.0` con gases, `0` sin ellos | Mueve las banderas y alimenta la energía eólica                                           |
| `density`        | no       | float                                                                                  | `1.0`                         | Lo denso que es el aire. Determina los valores por defecto de meteoritos y sonido          |

| Clave de `dungeon` | Obligatorio | Valor       | Predeterminado | Qué hace                                        |
| ------------- | -------- | ----------- | ------- | --------------------------------------------------- |
| `spacing`     | no       | int, bloques | `0`    | Distancia entre mazmorras de Galacticraft. `0` es ninguna |
| `chest`       | no       | tabla de botín | ninguno | El botín de sus cofres                           |

Galacticraft solo construye sus mazmorras en su propio terreno, así que `dungeon` importa solo donde algo ejecuta el generador de mazmorras de Galacticraft; el terreno de RDPL no lo hace.

El aspecto y el sonido de la lluvia en un planeta son el propio bloque [`weather.rain`](#el-bloque-weather) de la dimensión, como en el ejemplo de arriba.

**Quién registra la dimensión.** La dimensión de un cuerpo accesible la registra Galacticraft, de modo que los cohetes y los clientes multijugador la ven; una con `reachable: false` la registra RDPL. Si el cuerpo no se puede colocar, porque su padre es desconocido o su nombre ya está en uso, la dimensión no se registra, y el registro indica por qué.

### Cinturones de asteroides

*cuerpos de galacticraft*

`<namespace>/dimensions/*.json`

Una dimensión cuyo bloque `galacticraft` tiene `kind: "asteroids"` es un cinturón de asteroides: el propio campo de asteroides de Galacticraft, generado en un vacío vacío. Necesita Galacticraft Planets y un `terrain` de tipo `void`; sin cualquiera de los dos la dimensión no se registra, y el registro indica por qué.

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

El `parent` de un cinturón es un sistema estelar o un planeta. Alrededor de un sistema figura en el mapa como un planeta, y alrededor de un planeta como una luna. Admite todas las claves de [el bloque `galacticraft`](#el-bloque-galacticraft) salvo estas, que un cinturón ignora:

| Clave             | En un cinturón                                                                               |
| --------------- | --------------------------------------------------------------------------------------- |
| `landing`       | Un jugador llega en una cápsula de entrada sobre el asteroide más cercano, como en el cinturón propio de Galacticraft |
| `landingHeight` | La cápsula de entrada fija su propia altura                                             |
| `arrival`       | La cápsula de entrada elige el asteroide                                                |
| `dungeon`       | Un cinturón del pack no tiene bases abandonadas                                         |

El cielo, la gravedad, el tiempo y el clima son las claves propias de la dimensión, como en cualquier planeta de un pack. Cinco de ellas tienen un valor por defecto distinto en un cinturón, para coincidir con las propias de Galacticraft:

| Clave                                  | Sin ella                                                                                  |
| ------------------------------------ | ------------------------------------------------------------------------------------------- |
| `sky.fogColor`                       | `000000`, así que la niebla y el horizonte son negros                                       |
| `sky.renderClouds`                   | `false`                                                                                     |
| `sky.sunriseColors`                  | `false`, un cinturón no tiene atardecer                                                     |
| `sky.sun`, `sky.bodies`, `sky.stars` | Se dibuja el cielo de asteroides propio de Galacticraft: un pequeño sol blanco, sin luna y un denso campo de estrellas |
| `time.dayLength`                     | Sin día: el sol permanece inmóvil en el horizonte y siempre es de día                       |

Establecer cualquiera de `sun`, `bodies` o `stars` dibuja en su lugar el cielo del pack, y `sky.renderSky` desactivado sigue sin dibujar ninguno. Establecer `time.dayLength`, aunque sea a `24000`, o `sky.fixedTime` da al cinturón un día propio.

### Estaciones espaciales

*cuerpos de galacticraft*

`<namespace>/dimensions/*.json`

Un archivo de dimensión cuyo bloque `galacticraft` tiene `kind: "station"` permite a los jugadores construir una estación espacial en órbita alrededor de un planeta o una luna del pack, mediante el propio botón del mapa de Galacticraft y su propia dimensión de órbita. El archivo es el tipo de estación, no una estación: cada estación que construye un jugador es una dimensión propia, que Galacticraft crea y conserva.

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

| Clave         | Obligatorio | Valor                          | Predeterminado                           | Qué hace                                                                                                                                                                   |
| ----------- | -------- | ------------------------------ | --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `kind`      | sí       | `station`                      |                                   | Convierte el archivo en una estación                                                                                                                                           |
| `parent`    | sí       | nombre                         |                                   | El planeta o la luna que orbita: uno accesible que cree una dimensión del pack. La estación de una luna figura en el mapa junto a la luna, alrededor del planeta de la luna  |
| `tier`      | no       | int                            | el de su padre                    | El nivel de cohete que llega a la estación                                                                                                                                     |
| `showName`  | no       | booleano                       | `false`                           | Activado, el mapa enumera cada estación bajo el `name` de este archivo donde Galacticraft escribe `Station: <owner>`; la línea del propietario se mantiene. Una estación a la que su propietario cambió el nombre conserva el nombre del propietario |
| `recipe`    | no       | objeto de ingrediente y cantidad | la receta de estación propia de Galacticraft | Lo que cuesta construir una. Un ingrediente es un nombre del diccionario de minerales, `modid:item` o `modid:item:meta`                                              |
| `checklist` | no       | lista de cadenas               | ninguno                           | Claves de la lista de comprobación de Galacticraft mostradas antes del despegue                                                                                                |

Las demás [claves de mapa](#claves-de-mapa) sitúan la estación en el mapa. Nada más del bloque `galacticraft` se aplica: una estación tiene el aire, la gravedad y la llegada propios de las estaciones de Galacticraft.

Fuera del bloque, una estación lee estas claves de dimensión y ninguna otra:

| Clave                                                      | Sin ella                                              |
| -------------------------------------------------------- | ------------------------------------------------------- |
| `id`                                                     | Obligatoria. La estación toma este id y el siguiente    |
| `sky.skyColor`, `sky.fogColor`                           | Los colores de órbita de Galacticraft                   |
| `sky.starBrightness`                                     | Las estrellas de órbita de Galacticraft                 |
| `sky.sun`, `sky.bodies`, `sky.stars`                     | El cielo de órbita de Galacticraft, con su padre debajo |
| `sky.renderSky`, `sky.renderClouds`, `sky.renderWeather` | `true`                                                  |
| `time.dayLength`                                         | `24000`                                                 |

**Ids.** `id` e `id + 1` son ids de tipo de dimensión, no la dimensión de la estación; ambos deben estar libres. Cada estación construida recibe el siguiente id de dimensión libre en el momento de construirse, y Galacticraft guarda ese id, el propietario y el nombre de la estación en el guardado del mundo, así que la estación vuelve con el mismo id tras un reinicio. Su carpeta de guardado es la de Galacticraft, `DIM_SPACESTATION<id>`.

**Un tipo de estación por cuerpo.** Un planeta o una luna que ya tiene una estación, de otro pack o de otro mod, la conserva: la segunda no se registra, y el registro lo indica.

### GalaxySpace y ExtraPlanets

*cuerpos de galacticraft*

`<namespace>/celestial/*.json` y el bloque `galacticraft` de `<namespace>/dimensions/*.json`

Dos complementos de Galacticraft leen más información de un cuerpo que Galacticraft. Un objeto `galaxyspace`, en un planeta o una luna de `celestial/`, en la `star` de un sistema o en el bloque `galacticraft` de una dimensión, surte efecto cuando GalaxySpace está instalado. Un objeto `extraplanets`, solo en el bloque `galacticraft` de una dimensión, surte efecto cuando ExtraPlanets está instalado. Sin el complemento, su objeto no hace nada y el registro lo indica una vez para el cuerpo, que se crea igualmente como un cuerpo de Galacticraft normal. Un planeta puede orbitar un sistema de GalaxySpace, como `tauceti`, `barnards`, `acentauri` o `proxima`, con o sin estos objetos.

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

La `gravity` y la `dayLength` de una dimensión son también lo que GalaxySpace muestra para el cuerpo y usa con él; no necesitan una clave propia.

| Clave de `galaxyspace` | Obligatorio | Valor                                                                                         | Predeterminado                 | Qué hace                                                                                                                                                                      |
| ------------------- | -------- | ------------------------------------------------------------------------------------------------ | ----------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `pressure`          | no       | float                                                                                            | `0`                     | Presión del aire. Por encima de `10` provoca náuseas, por encima de `25` lentitud, por encima de `35` ceguera y por encima de `45` daño, salvo que lo impida la armadura de GalaxySpace o su configuración |
| `radiation`         | no       | booleano                                                                                         | `false`                 | Radiación solar, que se acumula en un jugador que está a la luz del día bajo cielo abierto salvo que su armadura lo proteja                                                       |
| `class`             | no       | `selena`, `desert`, `terra`, `oceanide`, `gasgiant`, `icegiant`, `asteroid`, `titan`, `iceworld` | ninguno                | La clase de planeta que muestran el mapa de GalaxySpace y su libro guía                                                                                                           |
| `orbitEccentricity` | no       | `[x, y]`                                                                                         | `[0, 0]`                | Estira la órbita que dibuja el mapa de GalaxySpace a lo largo de cada eje. `0` o menos deja ese eje redondo                                                                       |
| `orbitOffset`       | no       | `[x, y]`                                                                                         | `[0, 0]`                | Desplaza el centro de esa órbita                                                                                                                                                  |
| `freezeBlocks`      | no       | booleano                                                                                         | `true`                  | Si el metano líquido y el helio-hidrógeno de GalaxySpace colocados fuera de aire sellado se convierten en fuego con calor intenso o se desvanecen con frío intenso; el agua nunca cambia |
| `thermalVariation`  | no       | float                                                                                            | `0`                     | Cuánto oscila el nivel térmico entre el mediodía y la medianoche, como proporción de `atmosphere.temperature`, o en esta cantidad cuando esta es `0`. Necesita el sistema térmico avanzado de GalaxySpace |
| `solarWind`         | no       | float                                                                                            | el tamaño de la estrella al cuadrado | Producción de los paneles de viento solar de GalaxySpace aquí                                                                                       |
| `weather`           | no       | `dust_storm`, `frozen_storm`, `lightning_storm`, `meteoric_rain`                                 | ninguno                 | Una tormenta que viene y va. Una tormenta de polvo hiere a quien está bajo cielo abierto, la lluvia meteórica deja caer meteoritos, una tormenta eléctrica lanza rayos             |
| `weatherFrequency`  | no       | float                                                                                            | `1`                     | Con qué frecuencia golpea una tormenta eléctrica                                                                                                                                  |
| `starType`          | no       | `subdwarf`, `dwarf`, `subgiant`, `giant`, `supergiant`, `hypergiant`, `blackhole`                | ninguno                 | El tipo de una estrella, mostrado en el mapa de GalaxySpace                                                                                                                       |
| `starColor`         | no       | `brown`, `red`, `orange`, `yellow`, `white`, `lightblue`, `blue`, o una clase `M1` a `O3`        | ninguno                 | La clase de color de una estrella, mostrada en el mapa de GalaxySpace                                                                                                             |
| `habitableZone`     | no       | `[distance, width]`                                                                              | `[0, 0]`                | La banda alrededor de una estrella que el mapa de GalaxySpace marca como habitable                                                                                                |

`starType`, `starColor` y `habitableZone` se leen solo en la `star` de un sistema; el resto, solo en un planeta o una luna. GalaxySpace dibuja sus tormentas por planeta, así que la tormenta de un pack actúa pero no muestra un cielo propio.

| Clave de `extraplanets` | Obligatorio | Valor                                                          | Predeterminado                                 | Qué hace                                                                                                                                     |
| ------------------ | -------- | -------------------------------------------------------------- | --------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| `pressure`         | no       | int, `0` a `100`                                               | ninguno                                 | Presión de ExtraPlanets. Por encima de `0` hiere a un jugador sin traje espacial de ExtraPlanets, y se muestra en su HUD                         |
| `radiation`        | no       | int, `0` a `100`                                               | el valor por defecto de ExtraPlanets para otros complementos | Radiación de ExtraPlanets, que se acumula en un jugador cuyo nivel de traje no la cubre                                                    |
| `temperatureDay`   | no       | float                                                          | `atmosphere.temperature`                | El nivel térmico de día, en la escala de ExtraPlanets, que va de aproximadamente `-140` a `100`. Necesita la opción de relleno térmico de nivel 3 y 4 de ExtraPlanets |
| `temperatureNight` | no       | float                                                          | `temperatureDay`                        | El nivel térmico de noche                                                                                                                        |
| `lander`           | no       | `general`, `jupiter`, `saturn`, `mercury`, `neptune`, `uranus` | el módulo de aterrizaje de Galacticraft | El módulo de aterrizaje de ExtraPlanets en el que desciende un jugador, donde `landing` es `lander`                                              |

## Portales y puertas dimensionales

*el mundo*

`<namespace>/blocks/*.json`

Un portal es una definición de bloque corriente, así que rige la misma regla de rutas: la ruta del archivo es el nombre de registro del bloque.

Un bloque `portal` lleva una sección `portal`:

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

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| ----------------- | -------- | ---------- | ---------------------- | ----------------------------------------------------------------------------------------------------- |
| `dimension`       | sí       | int        |                        | A dónde te envía                                                                                      |
| `returnDimension` | no       | int        | `0`                    | A dónde te devuelve                                                                                   |
| `gate`            | no       | nombre de puerta | ninguno          | Una puerta dimensional que debe estar abierta para pasar                                              |
| `cooldown`        | no       | int, ticks | `60`                   | Tiempo hasta que el mismo jugador pueda volver a usarlo                                               |
| `platform`        | no       | boolean    | `true`                 | Construye una plataforma de aterrizaje al llegar                                                      |
| `platformBlock`   | no       | nombre de bloque | el marco del propio portal | De qué está hecha esa plataforma                                                                |
| `sound`           | no       | nombre de sonido | ninguno          | Se reproduce al pasar                                                                                 |
| `owned`           | no       | boolean    | `true`                 | Solo quien lo construyó, y quienes este autorice, pueden usarlo. Un portal con propietario también es inmune a las explosiones |
| `walkIn`          | no       | boolean    | `false`                | Entrar caminando en el bloque te transporta, como hace un portal del Nether. Desactivado, se usa a mano |

### Marcos de portal

*portales y puertas dimensionales*

`<namespace>/portalframes/*.json`

La ruta del archivo es el nombre de registro del marco, que una dimensión nombra después en `frames`.

Un marco es el dibujo de lo que el jugador tiene que construir y nada más: indica qué bloques forman el borde y dónde está el hueco, y no dice nada de adónde lleva el portal. Es deliberado, porque una dimensión reclama un marco en lugar de poseerlo, y dos dimensiones pueden reclamar el mismo.

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

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| ----------- | -------- | ---------------------------------- | ------------- | ------------------------------------------------------------------------------------------- |
| `name`      | no       | string                             | el nombre del archivo | El nombre que se usa en el registro                                                 |
| `axis`      | no       | `vertical`, `horizontal` o `both`  | `vertical`    | Si está de pie como un portal del Nether, tumbado como un portal del End, o puede ser de cualquiera de las dos formas |
| `legend`    | sí       | objeto de un carácter a un bloque  | ninguno       | Los bloques que pueden usar las filas. Un nombre de bloque con estados se lee igual que en cualquier otro sitio |
| `rows`      | sí       | lista de strings                   | ninguno       | El dibujo, empezando por la fila superior                                                   |
| `maxWidth`  | no       | int                                | `21`          | El hueco más ancho al que puede estirarse un `*`                                            |
| `maxHeight` | no       | int                                | `21`          | El hueco más alto al que puede estirarse un `*`                                             |

Tres caracteres no son bloques. `.` es el hueco en el que se sitúa el portal, y un marco sin él se rechaza. Un espacio es una casilla que al marco no le importa, de modo que un contorno en forma de L se dibuja dejando las esquinas en blanco. `*` repite: una fila que sea solo `*` repite la fila anterior tantas veces como haya construido el jugador, y un `*` dentro de una fila repite de la misma manera el carácter que lo precede. Puede repetir cero veces, así que el dibujo leído tachando todos los `*` es lo más pequeño que se encenderá, y los máximos de arriba son lo más grande. Un dibujo sin ningún `*` es exacto, y el jugador debe construir eso y nada más.

Un marco vertical se encuentra en cualquiera de los dos ejes horizontales y orientado en cualquier sentido, así que da igual hacia dónde mirara quien lo construyó. Uno horizontal se encuentra en los cuatro giros.

**Qué tamaño puede tener lo decide el pack.** `maxWidth` y `maxHeight` son el hueco más grande al que se estirará un `*`, y se acepta cualquier cosa menor hasta el mínimo, de modo que un pack decide si su puerta llega como máximo a los 21 de vanilla o a 4. El mínimo lo marca un jugador: un marco de pie se rechaza salvo que su hueco pueda tener al menos 1 de ancho y 2 de alto, uno plano al menos 1 por 1, y un dibujo que nunca pueda alcanzarlo se rechaza al cargar con una línea en el registro, en vez de ser un marco por el que nadie puede pasar.

**Un marco cuesta más de buscar cuanto más puede estirarse.** Tener a la vez un `*` de fila y un `*` de columna significa que se prueba cada combinación hasta los dos máximos, así que un marco que se estira en ambos sentidos hasta 21 son 441 dibujos. La búsqueda se rinde en lugar de bloquearse y lo dice en el registro, lo que indica que hay que bajar un máximo o quitar uno de los estiramientos.

**Nada impide que un marco sea de obsidiana encendida con mechero, pero tiene prioridad.** Un marco se busca antes de que el objeto haga su propio trabajo, así que un marco así abre la dimensión del pack allí donde habría estado un portal del Nether. Elige otro bloque u otro encendedor para dejar en paz el portal de vanilla.

### Abrir una dimensión con un marco

*portales y puertas dimensionales*

`<namespace>/dimensions/*.json`

Una dimensión se abre mediante un marco si lleva una sección `portal`. El marco y lo que lo enciende, juntos, son lo que elige la dimensión, de modo que una misma forma de marco puede llevar a varios lugares según con qué se encendió.

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

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| --------------- | -------- | --------------------------- | --------------------------- | ------------------------------------------------------------------ |
| `frames`        | sí       | lista de nombres de marco   | ninguno                     | Los marcos que abren esta dimensión                                |
| `ignitedBy`     | no       | nombre de objeto            | `minecraft:flint_and_steel` | Lo que sostiene un jugador para encender uno                       |
| `color`         | no       | color hexadecimal           | blanco                      | El color con el que se dibuja el portal                            |
| `return`        | no       | `built`, `player` o `none`  | `built`                     | Si se proporciona un camino de vuelta, lo construye el jugador, o no hay ninguno |
| `gate`          | no       | nombre de puerta            | ninguno                     | Una puerta dimensional que debe estar abierta para pasar           |
| `cooldown`      | no       | int, ticks                  | `60`                        | Tiempo hasta que el mismo jugador pueda volver a pasar             |
| `platform`      | no       | boolean                     | `true`                      | Construye una plataforma de aterrizaje al llegar                   |
| `platformBlock` | no       | nombre de bloque            | piedra                      | De qué está hecha esa plataforma                                   |
| `sound`         | no       | nombre de sonido            | ninguno                     | Se reproduce al pasar                                              |
| `owned`         | no       | boolean                     | `false`                     | Solo quien lo encendió, y quienes este autorice, pueden usarlo     |

El pack no escribe el bloque que ocupa el hueco. A una dimensión con una sección `portal` se le da uno propio, llamado `<namespace>:portal_<dimension>`, dibujado con la textura de portal del propio juego bajo `color`, que se atraviesa caminando en lugar de usarse a mano, e irrompible. El color multiplica la textura, igual que hace un `tintindex`, de modo que `#C77DFF` conserva el violeta del Nether y `#4CFFB0` lo vuelve venenoso. Para un portal que no use en absoluto la textura de vanilla, escribe un bloque `portal` corriente propio, con su propio modelo y una textura dibujada como [mapa de píxeles](#texturas-escritas-como-mapas-de-píxeles), donde `tint` puede degradar entre dos colores.

`return` decide qué ocurre al otro lado. `built` levanta el mismo marco, del tamaño que construyó el jugador, y lo enciende, que es como se comporta vanilla. `player` no construye nada pero permite encender allí el mismo marco, así que el camino a casa hay que encontrarlo y hacerlo. `none` se niega a encender el marco en esa dimensión, y el viaje es solo de ida.

**Un marco, varias dimensiones.** La pareja formada por un marco y el objeto que lo enciende es lo que elige la dimensión, así que el mismo `standing_gate` encendido con mechero y encendido con un encendedor propio del pack abre dos lugares distintos, cada uno con su propio color. Que dos dimensiones reclamen el mismo marco *y* el mismo objeto es un error del pack: la segunda se rechaza y lo dice en el registro, en vez de que una de ellas gane en silencio.

Romper cualquier bloque del marco apaga el portal, igual que en vanilla.

### Portales

*portales y puertas dimensionales*

`<namespace>/gates/*.json`

La ruta del archivo es el nombre de registro de la puerta dimensional, que un portal nombra después en `gate`.

Todas las claves a la vez. Un archivo real escribe solo las que necesita.

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

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| ----------------- | -------- | ---------------------------------- | -------------------------------- | ------------------------------------------------------------------------------------------------ |
| `dimension`       | sí       | int                                |                                  | La dimensión que protege                                                                         |
| `name`            | no       | string                             | el nombre del archivo            | Se muestra al jugador                                                                            |
| `scope`           | no       | `player`, `global`                 | `player`                         | Un jugador cada vez, o todo el mundo a la vez                                                    |
| `open`            | no       | boolean                            | `false`                          | Si empieza abierta                                                                               |
| `unlock`          | no       | objeto                             |                                  | Lo que la abre. Véase más abajo                                                                  |
| `unlockedMessage` | no       | string                             | `%dim% is now open`              | Se muestra cuando se abre                                                                        |
| `blockedMessage`  | no       | string                             | `You need %item% to enter %dim%` | Se muestra cuando se niega el paso                                                               |
| `safeReturn`      | no       | boolean                            | `false`                          | Un regreso bloqueado aun así aterriza en un lugar seguro en vez de ser rechazado                 |
| `requires`        | no       | lista de ids de mod o espacios de nombres de pack | ninguno           | La puerta se omite salvo que estén presentes todos                                               |
| `portalBlocks`    | no       | lista de nombres de bloque         | todos los portales               | Limita la puerta a estos bloques de portal, de modo que una dimensión pueda tener una entrada protegida y otra abierta |

`unlock` admite `hold` (un objeto que debe sostenerse), `consume` con `consumeCount` (`1`), `craft` (un objeto que debe haberse fabricado), `advancement` y `killed` (un nombre de entidad; la puerta se abre para quien mate una, de modo que un jefe puede guardar la llave de un mundo) con `killedCount` (`1`) cuando una no basta, contabilizado por jugador o para todo el mundo según indique el ámbito. Añadir `killedDrops` (un nombre de objeto) hace que las muertes contadas dejen ese objeto a los pies de quien mató en lugar de abrir la puerta, y reinicia la cuenta, de modo que una llave puede volver a ganarse y entregarse a alguien que nunca peleó por ella; condiciona la puerta con `hold` o `consume` del mismo objeto para convertirlo en la llave. `%item%`, `%mob%` y `%dim%` se rellenan por ti. Una llave que suelta un mob no necesita nada especial aquí: dale el botín al mob y condiciona la puerta con `hold` o `consume`.

## Mundos Rubic

*el mundo*

`rubicWorld` en los ajustes de `terrain` reconstruye el mundo de una dimensión con cubos de 16×16×16 en lugar de columnas de 256 bloques, de modo que su suelo y su techo pueden situarse donde el pack quiera. La generación del terreno en sí no cambia: el generador de vanilla y la worldgen de otros mods funcionan como siempre y producen el mismo paisaje; simplemente hay mundo por encima y por debajo.

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

Todas las claves están en el grupo `terrain`, en el bloque `settings` de una plantilla de mundo como las demás:

| Clave | Valor | Predeterminado | Qué hace |
| ---------------------------------- | -------------------------------- | ------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `rubicWorld`                       | boolean                          | `false` | Activa los mundos rubic                                                                                                                                                                                                                           |
| `worldMinHeight`                   | int, múltiplo de 16              | `-64`   | El suelo del mundo                                                                                                                                                                                                                                |
| `worldMaxHeight`                   | int, múltiplo de 16              | `320`   | El techo del mundo                                                                                                                                                                                                                                |
| `rubicWorldDimensions`             | lista de ints                    | vacía   | Qué dimensiones pasan a ser rubic. Vacía significa todas las dimensiones                                                                                                                                                                          |
| `rubicWorldDimensionsAreBlacklist` | boolean                          | `false` | Trata la lista como las dimensiones que se dejan en paz                                                                                                                                                                                           |
| `terrainOffset`                    | int, múltiplo de 16 no negativo  | `0`     | Desplaza hacia arriba toda la ventana de terreno de vanilla. Para presets de capas simples: un mundo plano con `272` tiene su superficie cerca de y 275, por encima del techo de vanilla. Las decoraciones y estructuras que pide un preset siguen generándose a sus alturas sin desplazar |

**Alturas.** `worldMinHeight` debe ser menor que `worldMaxHeight`, ambos múltiplos de 16, y ambos dentro del alcance que permite `rubicHeightLimit` en la configuración (`4096` bloques en cada sentido de forma predeterminada; solo configuración, nunca una clave de pack). Cualquier otra cosa se rechaza con una línea en el registro y el mundo se crea de `-64` a `320`. La altura cuesta espacio: cada 16 bloques es un cubo más en cada columna, así que la memoria, el disco y el tiempo de pregeneración crecen con ella; el comentario de la configuración sobre `rubicHeightLimit` trae las cifras.

**La ventana de terreno.** El generador propio de la dimensión conserva su propia altura, 256 bloques en el overworld, y esa ventana es lo que desliza `terrainOffset`. `worldMinHeight` y `worldMaxHeight` añaden espacio alrededor de la ventana, nunca dentro de ella. Subir el techo no sube el terreno, añade cielo; bajar el suelo no profundiza las cuevas que talló el generador, añade mundo profundo. El nivel del mar también está dentro de la ventana, así que se desplaza con `terrainOffset` y proviene del tipo de mundo, no de ninguna clave rubic. Para poner la superficie más alta en el mundo, sube `terrainOffset`. Para dar más espacio por encima o por debajo, mueve las alturas. Cada cubo fuera de la ventana se sigue generando e iluminando en cada columna, de modo que un techo más alto cuesta tiempo de pregeneración haya o no algo que lo llene, y cuesta además memoria y disco cuando lo llena `skyStone`.

**Qué hace una ventana desplazada a otros mods.** La población se ejecuta sobre la columna, así que cada generador que registra un mod sigue ejecutándose una vez por chunk, sin traducir ninguna coordenada. Lo que cambia es dónde cae su propia matemática. Un generador que pregunta al mundo dónde está el suelo, mediante el bloque sólido más alto o la altura de precipitación, sigue el terreno desplazado: ambos son conscientes de rubic, lo que cubre árboles, flores y la mayor parte de la decoración. Un generador que calcula una altura absoluta, entre ellos el patrón habitual de menas de una coordenada y aleatoria por debajo de 64, sigue escribiendo a esa altura, que tras un desplazamiento es el relleno o el mundo profundo muy por debajo del terreno. El nivel del mar tampoco se desplaza, así que un generador que lo compare lee el número sin desplazar. Esas escrituras además caen fuera de los cubos que la población mantiene cargados, y arrastran cubos propios mientras se puebla una columna. Un `terrainOffset` grande conviene a un pack que describe su propia generación, no a uno apilado sobre la worldgen de otro pack. Para ganar solo espacio, la profundidad es la dirección más barata: bajo la ventana hay un generador completo con su propia piedra, cuevas, vetas, acuíferos y mazmorras, y deja la superficie a las alturas que suponen todos los demás generadores, mientras que el espacio sobre la ventana es decorado que el pack tiene que amueblar por su cuenta.

**Se decide por partida, una sola vez.** Si una dimensión es rubic y cuáles son sus alturas se escribe en su guardado la primera vez que carga, y rige desde entonces: un mundo rubic sigue siendo rubic aunque se quite el pack, y sus alturas no pueden cambiarse después. Las dimensiones distintas del overworld toman las alturas del overworld. La tierra Anvil existente no se convierte: rubic guarda su tierra en archivos `region2d`/`region3d` propios, así que una dimensión que ya se generó como Anvil empieza su terreno de cero. Actívalo en mundos nuevos.

**Excluir dimensiones.** Una dimensión que se deja fuera de `rubicWorldDimensions` conserva su mundo Anvil normal, en la misma partida: las dimensiones rubic y Anvil se mezclan libremente. Es lo apropiado para dimensiones cuyos generadores escriben en las entrañas del chunk en lugar de pasar por el ciclo de población ordinario. Con independencia de la lista, se omite un mundo cuyas clases de servidor ha sustituido otro mod, con una línea en el registro que lo indica.

**Espacio fuera de la ventana.** El rango propio del generador conserva su forma habitual, y el espacio que un mundo rubic añade a su alrededor se rellena con el bloque con el que termina ese rango: piedra bajo el overworld, aire sobre él. Una dimensión cuyo techo está sellado con roca madre, el Nether por encima de todas, cuenta como cerrada, así que el espacio sobre ella se deja vacío en lugar de rellenarlo con la netherrack que hay bajo su techo. El techo en sí no se toca. `deepStone` nombra el bloque para el espacio bajo la ventana, `skyStone` el bloque para el espacio por encima.

**CubicChunks.** No se admite ejecutar ambos. Con CubicChunks instalado y un pack que pida `rubicWorld`, la carga se detiene con un mensaje: quita CubicChunks, o quita `rubicWorld` del pack y deja que CubicChunks cree los mundos.

### Pasar un mundo a CubicChunks y volver

*mundos rubic*

**Nombres de archivo.** Un mundo rubic guarda sus columnas en `region2d/<x>.<z>.2rdr` y sus cubos en `region3d/<x>.<y>.<z>.3rdr`. Una entrada demasiado grande para su archivo de región va a una carpeta junto a él, con el nombre del archivo al que se añade `.ext`. Los mundos creados antes de que existieran estos nombres usaban `.2dr` y `.3dr`: el mod renombra esos archivos y carpetas por sí mismo cuando carga una dimensión, con una línea de registro por dimensión, así que un mundo antiguo no necesita que se haga nada a mano.

**Abrir un mundo de CubicChunks.** Cuando un pack pide `rubicWorld` y el mundo que se abre está marcado como mundo de CubicChunks, el mod pregunta antes de hacer nada, igual que Forge pregunta por las entradas de registro que faltan: una pantalla de confirmación en un jugador, y en un servidor dedicado un mensaje de consola al que se responde con `/fml confirm` o `/fml cancel`, o por adelantado con `-Dfml.queryResult=confirm`. Si se acepta, se escribe la copia de seguridad del mundo de Forge como zip en la carpeta de partidas y el mundo se convierte en un mundo rubic en el sitio, y luego continúa la carga. Si se rechaza, la carga se detiene y no se cambia nada del mundo.

**El conversor.** La misma conversión, y el camino de vuelta, puede ejecutarse fuera del juego. El repositorio incluye [`scripts/convert_rubic_world.py`](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/tree/1.12.2-1.0-Release/scripts), que convierte un mundo rubic en un mundo de CubicChunks, o un mundo de CubicChunks en uno rubic. Necesita Python 3 y nada más. Cierra el juego y haz antes una copia de seguridad del mundo, luego mira la simulación antes de ejecutarlo de verdad.

```
python3 scripts/convert_rubic_world.py to-cubic "saves/My World" --dry-run
python3 scripts/convert_rubic_world.py to-cubic "saves/My World"
```

| Argumento | Qué hace |
| ---------------- | ----------------------------------------------- |
| `to-cubic`       | Un mundo rubic pasa a ser un mundo de CubicChunks |
| `to-rubic`       | Un mundo de CubicChunks pasa a ser un mundo rubic |
| `<world folder>` | La carpeta de la partida, la que contiene `level.dat` |
| `--dry-run`      | Muestra cada cambio y no hace ninguno           |

**Qué cambia.** En cada dimensión, los archivos de región y sus carpetas `.ext` toman los nombres del otro lado (`.2rdr` y `.3rdr` para rubic, `.2dr` y `.3dr` para CubicChunks), y `data/rdplRubicData.dat` pasa a ser `data/cubicChunksData.dat` o al revés, conservando las alturas y nombrando el formato de almacenamiento y el generador de compatibilidad como los nombra el otro mod. Por último, la marca de `level.dat` y `level.dat_old` se cambia entre `isRubicWorld` e `isCubicWorld`. Los cubos y las columnas en sí no se reescriben.

**Qué rechaza.** Un mundo cuya marca de `level.dat` no coincide con la dirección pedida, un formato de almacenamiento o un generador de compatibilidad que el otro mod no tiene, y un renombrado cuyo destino ya existe. Un rechazo no cambia nada, y una ejecución interrumpida puede repetirse.

**Qué no se traslada.** Un mundo convertido contiene solo lo que ambos mods entienden. Los bloques y dimensiones que definió un pack no existen bajo CubicChunks a secas, y cada mod vuelve a calcular la luz de los cubos que guardó el otro.

### Transmisión de cubos

*mundos rubic*

**Transmisión de cubos.** Cuatro claves de `chunks` deciden cómo llegan los cubos a un jugador y cuándo se sueltan de nuevo. Solo hacen algo en un mundo rubic, y los valores predeterminados son los números con los que se afinó el subsistema, de modo que un pack que los deja en paz no paga nada.

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

| Clave | Valor | Predeterminado | Qué hace |
| -------------------------- | ----------------- | ------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `verticalCubeLoadDistance` | int, cubos        | `8`     | Cuántos cubos por encima y por debajo de un jugador mantiene un ticket de carga de chunks. El control deslizante de ajustes de vídeo del mismo nombre es la distancia de visión propia del cliente, fijada por quien juega y no por un pack |
| `cubesSentPerTick`         | int, cubos        | `649`   | Cuántos cubos se pueden enviar a un jugador en un tick. Subirlo llena antes una burbuja de visión y hace mayores los paquetes de cada tick; un paquete se sigue dividiendo a 1024 cubos o 512 KB, lo que ocurra primero |
| `cubeGenMillisPerRound`    | int, milisegundos | `50`    | Cuánto puede dedicar un tick a generar los cubos que esperan los jugadores                                                                                                                          |
| `cubeGCInterval`           | int, ticks        | `200`   | Cada cuánto se sueltan los cubos que nadie está mirando                                                                                                                                             |

**Cliente.** Los ajustes de vídeo ganan un control deslizante de distancia de renderizado vertical, el análogo vertical de la distancia de renderizado (`verticalCubeLoadDistance` en la configuración, que pertenece a quien juega). Todo lo demás del grupo `terrain` (pregeneración, física del mundo, aparición, borde) se aplica sin cambios a los mundos rubic.

## El mundo profundo

*el mundo*

Nueve claves más de `terrain` rellenan con generación de estilo moderno el espacio que un mundo rubic abre alrededor de la ventana de terreno de vanilla. Solo hacen algo en un mundo rubic:

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

| Clave | Valor | Predeterminado | Qué hace |
| -------------- | ----------------------------------------- | --------- | ---- |
| `deepStone`    | `namespace:block`, meta como `@meta`      | ninguno   | El bloque del que está hecho el mundo bajo la ventana, como la pizarra abisal propia de un pack. Se funde con la piedra de la ventana a lo largo de las ocho capas más bajas de esta, igual que las versiones modernas funden la pizarra abisal |
| `skyStone`     | `namespace:block`, meta como `@meta`      | ninguno   | El bloque del que está hecho el mundo sobre la ventana bajo su superficie, modelado en tierra flotante por el mismo ruido que talla el mundo profundo de abajo, de modo que lo que allí abajo es cueva aquí arriba es isla. Vacío deja el espacio sobre la ventana vacío, como siempre ha estado. La tierra lleva la superficie propia de la columna, el bloque superior y los tres de debajo tomados del bioma, de modo que una isla del overworld se lee como hierba sobre tierra sobre este bloque. Un bioma o una región de cuevas puede nombrar su propio `skyStone`, `skyIslands` y `skyThickness`, así que una banda o una región lleva islas propias, resueltas por columna con la región imponiéndose a la banda y la banda al bioma. Las islas se decoran por derecho propio: cada cubo sobre la ventana ejecuta las características propias del bioma contra la superficie dentro de ese cubo, de modo que árboles, hierba, flores, setas, cañas y manchas caen sobre la isla en lugar de repartirse por la columna como las coloca vanilla, y una banda de bioma allá arriba decora con sus propias cantidades. Las adiciones propias de un bioma se ejecutan allí también, no solo las compartidas: pozos del desierto, melones de la jungla, el dosel denso y las setas de un bosque oscuro, rocas de la taiga, pinchos de hielo en los biomas helados, y las flores altas y hierbas que coloca cada bioma. Una isla nunca está hecha de un bloque que cae: donde la superficie de un bioma sería arena o grava, la isla usa arenisca, o su propio `skyStone` para cualquier otra cosa, ya que nada sostiene en el aire un bloque que cae. Las manadas se colocan de la misma manera, por cubo, así que los animales empiezan en las islas al crearse la tierra. La profundidad de la superficie varía de uno a cuatro bloques de relleno con el ruido, de modo que el borde de una isla no es una corteza uniforme, y la corteza se mide a lo largo de la pendiente en lugar de recta hacia abajo, así que una cara empinada conserva su suelo en vez de reducirse a nada. La superficie sigue el bioma que informe el propio cielo: primero la región de cuevas, luego un bioma por bandas de altura, luego la columna de debajo, de modo que nombrar `minecraft:mesa` en una región del cielo da islas de arcilla en bandas a cualquier altura, las mismas bandas que tiene el suelo, y nombrar un desierto da su arena, convertida en arenisca porque nada sostiene en el aire un bloque que cae. Los animales se asientan en ella, algo que `skyAnimals` del grupo `spawning` desactiva. Cuánto del cielo se convierte en tierra lo marca `skyIslands`, y su valor predeterminado de 0.5 es un archipiélago: en un mundo generado deja vacíos aproximadamente siete cubos de cada ocho sobre la ventana y su capa más llena ronda un tercio, de modo que el cielo se sobrevuela en lugar de caminarse. Bájalo hacia 0.2 y la banda se cierra en un techo ondulado con colinas encima, sólido en torno a cuatro quintos por su mitad, algo sobre lo que construir pero que ya no son islas. Las islas se detienen ocho bloques antes de `worldMaxHeight`, de modo que una cima nunca queda cortada en plano contra el techo y los árboles y plantas tienen espacio por encima; `caves` rellena hasta el techo como antes. Cada dimensión rubic tiene su propia ventana, así que esto llena el espacio sobre cada una: en el Nether, cuya ventana mide 128 de alto, es el espacio sobre el techo de roca madre, y una costura que abra el techo despeja el propio techo |
| `skyShape`     | `islands` o `caves`                       | `islands` | En qué se modela el mundo sobre la ventana. `islands` es tierra flotante. `caves` es roca sólida con cuevas talladas, el tratamiento propio del mundo profundo vuelto hacia arriba, que sale aproximadamente un 86 por ciento sólido, la misma proporción de roca y cueva que tiene el mundo profundo. En ninguno de los casos se inunda nada, ya que sobre la ventana no se consulta ningún acuífero. Solo se lee cuando `skyStone` nombra un bloque |
| `skyIslands`   | número, de `-1` a `1`                     | `0.5`     | Con qué facilidad se reúne el cielo en islas. Más bajo reparte islas por más cielo y profundiza la sombra bajo ellas, más alto deja menos piezas y más pequeñas. El valor predeterminado deja vacíos aproximadamente siete cubos de cada ocho y alcanza su máximo en torno a un tercio; alrededor de `0.2` la banda se cierra en un techo con colinas, sólido en torno a cuatro quintos por su mitad. Solo se lee cuando `skyStone` nombra un bloque y `skyShape` es `islands` |
| `skyThickness` | número, `0` o más                         | `2.0`     | Cuán sólida es una isla. Más alto rellena las islas, más bajo las ahueca y adelgaza sus bordes hasta la nada. Solo se lee cuando `skyStone` nombra un bloque y `skyShape` es `islands` |
| `skyHeights`   | dos ints, el más bajo y luego el más alto | ninguno   | El bloque más bajo y el más alto al que puede llegar una isla, contados desde el fondo de la ventana igual que las alturas de `oreVeins`. Vacío llena todo el mundo sobre la ventana, que en un mundo alto es muchísimo cielo. Solo se lee cuando `skyStone` nombra un bloque |
| `noiseCaves`   | `off`, `deep`, `world`                    | `off`     | Cuevas de ruido de estilo moderno: cavernas de queso, túneles de espagueti, bocas de cueva cerca de la superficie y pilares en las salas grandes. `deep` talla solo bajo la ventana, `world` talla todo el mundo |
| `deepRavines`  | boolean                                   | `false`   | Corta barrancos al estilo vanilla por el mundo bajo la ventana, cañones largos y empinados. Un barranco toma los fluidos propios del mundo profundo donde los atraviesa, llenándose de lava bajo la línea de lava y conservando el agua de un acuífero o su pared de presión por encima, de modo que nunca drena lo que corta. Las versiones modernas tallan sus cañones solo dentro de la ventana, así que el mundo profundo no tiene ninguno salvo que esto esté activado |
| `oreVeins`     | lista de `ore,extra,filler,lowest,highest` | ninguno  | Grandes vetas de mena en bandas, sobre todo del bloque `filler` con la `ore` repartida por él y una rara probabilidad de `extra`, que puede dejarse vacío. Las alturas se cuentan desde el fondo de la ventana, de modo que los negativos llegan al mundo profundo |

Allá abajo el agua y la lava se comportan. La lava a granel llena las capas más bajas, y las cuevas de encima llevan acuíferos locales (el mismo esquema de puntos de muestreo y presión que usan las versiones modernas, portado de 26.1.2), de modo que bolsas de agua quieta se sitúan a sus propios niveles, con paredes de la piedra profunda modeladas por ruido allí donde se encuentran dos niveles o el agua se encuentra con la lava. Bajo los océanos las cuevas se inundan hacia el nivel del mar, igual que las versiones modernas atan sus acuíferos a la superficie.

**Por dimensión.** `deepStone`, `noiseCaves`, `skyStone`, `skyShape`, `deepRavines` y `oreVeins` admiten cada uno un único valor o una lista, y una entrada de lista escrita como `dimension=value` se aplica solo a esa dimensión. Donde cualquier entrada nombra una dimensión, esas entradas deciden por completo y las que no la nombran se ignoran allí, así que `"1="` sin nada detrás desactiva la clave para esa dimensión. Un valor sin dimensión llega a todas las dimensiones rubic salvo el End, que sigue siendo vacío a menos que un pack lo nombre: llenar el End sería el fin del End, y un End lleno además deja ciega la búsqueda de portal de vanilla, que retrocede desde 1024 bloques mientras siga encontrando chunks con bloques. Nómbralo y obtendrás lo que pediste.

Con `noiseCaves` activado, el mundo profundo también genera salas de monstruos de estilo moderno (unos cuatro intentos por columna de chunk bajo la ventana, ninguno a menos de seis bloques del suelo del mundo), de modo que los spawners de mazmorra y su botín en cofres aparecen en las cuevas profundas como en las versiones modernas.

El ámbito `world` también retira dos restos de vanilla que chocarían con las cuevas rehechas. La lava que vanilla vierte en sus cuevas por debajo de y 10 la juzga en cambio el acuífero, así que desaparece la antigua ventana de lava, y los lagos de agua enterrados de vanilla, estanques de superficie incluidos, dejan de generarse, como las versiones modernas los abandonaron; en su lugar están las propias charcas del acuífero.

El ámbito `deep` deja la banda de vanilla como está, ventana de lava incluida, y solo sella la costura donde ambas se encuentran. La lava o el agua en la capa más baja de la ventana con una cueva profunda abierta justo debajo se convierte en la piedra profunda, de modo que la ventana no pueda drenarse hacia las cuevas de abajo.

## Regiones de cuevas

*el mundo*

`<namespace>/caveregions/*.json`

La ruta del archivo es el nombre de la región, que una entrada de worldgen nombra después en `caveRegions`. Un nombre simple allí toma el espacio de nombres de esa misma entrada.

Pinta regiones con nombre sobre el subsuelo, la contrapartida en packs de los biomas de cueva modernos. El subsuelo se divide en celdas redondeadas (`caveRegionCells` bloques de ancho y `caveRegionCellsY` de alto, ambas claves de `terrain`) y cada celda sortea una región, o ninguna, según su peso. Todo lo que hace una región sale de forma determinista de la semilla, de modo que los chunks concuerdan entre sí sin escribir nunca a través de un borde.

### Archivos de región

*regiones de cuevas*

Todas las claves a la vez. Un archivo real escribe solo las que necesita.

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

| Clave | Valor | Predeterminado | Qué hace |
| ------------------- | ---------------- | ------------------- | ---- |
| `weight`            | int              | `1`                 | Parte de las celdas que gana esta región. `0` la desactiva |
| `minHeight`         | int              | el suelo del mundo  | Fondo de la banda en la que existe la región |
| `maxHeight`         | int              | `48`                | Techo de esa banda. Una celda cuyo centro queda fuera de la banda nunca elige la región |
| `dimensions`        | lista de ints    | todas               | En qué dimensiones aparece la región |
| `floorCover`        | bloque           | ninguno             | Sustituye el bloque superior de los suelos de cueva dentro de la región |
| `floorChance`       | 0.0 a 1.0        | `1.0`               | Cuánto del suelo queda cubierto |
| `ceilingCover`      | bloque           | ninguno             | Sustituye los bloques del techo de la cueva dentro de la región |
| `ceilingChance`     | 0.0 a 1.0        | `1.0`               | Cuánto del techo |
| `coverReplace`      | lista de bloques | cualquier cosa de tipo piedra | Qué pueden sustituir las coberturas |
| `waterLevel`        | int              | ninguno             | Fija el nivel de agua de cada punto de muestreo de acuífero dentro de la región, de modo que sus cuevas se inunden hasta esta altura. Las paredes donde la región se encuentra con cuevas secas las modela el mismo ruido de presión que en los acuíferos modernos, y el agua nunca toca el suelo de lava. Necesita `noiseCaves` activado |
| `spawns`            | lista            | ninguno             | Mobs que aparecen dentro de la región, las mismas entradas que admite `spawns` de un bioma: `entity`, `type` (monster, creature, ambient o water), `weight`, `min` y `max` para el tamaño del grupo. Bajo la ventana de terreno, un lugar que ve el cielo se deja al bioma, igual que las coberturas; sobre la ventana, donde la única tierra es la generación del cielo, la lista se aplica también al aire libre |
| `keepDefaultSpawns` | boolean          | `false`             | Conserva la lista de apariciones propia del bioma junto a la de la región. Desactivado, la lista de la región la sustituye por completo dentro de la región |
| `structures`        | lista            | ninguno             | Una estructura colocada una vez por celda de región, en el corazón de la celda, ajustada a un suelo de cueva, como las versiones modernas dan a un bioma de cueva su punto de referencia. Las entradas son plantillas `namespace:name`, o `{ "structure": "...", "weight": 3 }` para elegir entre varias |
| `structureChance`   | 0.0 a 1.0        | `1.0`               | La probabilidad de que cada celda de la región llegue a tener su estructura |
| `structureLoot`     | `namespace:path` | ninguno             | La tabla de botín de la que se rellena cada cofre dentro de una estructura colocada la primera vez que se abre |
| `biome`             | nombre de bioma  | ninguno             | El bioma que la región indica dentro de su volumen, escrito en el cubo como bioma 3D. Da a la región su propio follaje, y colores de hierba y agua, música y sonidos ambientales, y deja que el sorteo de apariciones de vanilla lo lea. La superficie de arriba no se toca, ya que solo se escriben las celdas que ocupa la región |
| `skyStone`          | bloque           | el ajuste del mundo | El bloque del que están hechas las islas del cielo bajo su superficie dentro de esta región, de modo que una región lleva islas propias |
| `skyIslands`        | `-1` a `1`       | el ajuste del mundo | El umbral de islas dentro de la región. Más bajo reúne más tierra |
| `skyThickness`      | `0` o más        | el ajuste del mundo | Cuán sólidas son las islas de la región |
| `ambientSound`      | nombre de sonido | ninguno             | Un sonido que se reproduce de vez en cuando a un jugador que está dentro de la región, como los biomas modernos añaden sus propios sonidos de cueva. El servidor lo envía solo a ese jugador |
| `soundChance`       | 0.0 a 1.0        | `0.0111`            | La probabilidad, en cada tick, de que se reproduzca `ambientSound` |
| `particle`          | nombre de partícula | ninguno          | Una partícula mostrada alrededor de un jugador dentro de la región, uno de los nombres de partícula del juego como `dripWater`, `happyVillager` o `depthsuspend`. Solo la muestra el aire dentro de la región |
| `particleChance`    | 0.0 a 1.0        | `0.00625`           | La densidad de partículas de los biomas modernos: en cada tick se prueban unos 667 puntos a menos de 16 bloques, y cada uno muestra la partícula con esta probabilidad |

### Celdas

*regiones de cuevas*

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

| Ajuste | Tipo | Predeterminado | Qué hace |
| ----------------------- | ----------- | ------- | ---- |
| `caveRegionCells`       | int, bloques | `128`  | Cuán ancha es una celda de región |
| `caveRegionCellsY`      | int, bloques | `64`   | Cuán alta es una celda de región |
| `caveRegionPlainWeight` | int         | `4`     | El peso del subsuelo llano, sin región, en el sorteo de cada celda. Más alto deja más subsuelo sin ninguna región: con una única región de peso 1, a una quinta parte de las celdas le toca |

Cuánto del subsuelo se queda llano lo marca la clave `terrain` `caveRegionPlainWeight`, de valor predeterminado `4`: con una única región de peso 1, a una quinta parte de las celdas le toca la región. Las coberturas se aplican bajo un techo, así que una región que llegue por encima del suelo nunca se ve en la superficie; sobre la ventana de terreno se aplican también al aire libre, ya que todo lo de allí arriba es tierra que creó la generación del cielo. Las coberturas funcionan en cualquier cueva, la haya tallado el generador que sea; `waterLevel` es la única clave que necesita las cuevas de ruido, porque la inundación se coloca mientras se tallan.

### Características en una región

*regiones de cuevas*

Las características se enlazan mediante dos claves de las [entradas de worldgen](#entradas-de-worldgen) corrientes. `caveRegions` enumera las regiones en las que una entrada puede generarse, comprobadas en la posición colocada, de modo que setas, cristales o cualquier otra cosa aparecen solo dentro de su región. `snap` mueve primero cada intento en vertical hasta la superficie de cueva más cercana: `floor` para lo que se apoya, `ceiling` para lo que cuelga. Una región de tipo espeleotema no necesita formas nuevas:

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

El `replace` de `minecraft:air` importa: aquello sobre lo que escribe una forma colocada se comprueba contra `replace`, cuyo valor predeterminado es piedra, así que todo lo que se construya en espacio abierto de cueva necesita que se enumere el aire. La misma entrada con `"snap": "floor"` y sin `hanging` hace crecer las estalagmitas a juego. El filtro de región funciona con cada forma colocada; `belt` y `field` colocan según sus propias reglas y lo ignoran.

---

# Generación del mundo

## Entradas de worldgen

*entradas de worldgen*

`<namespace>/worldgen/*.json`

La ruta del archivo nombra la entrada, y las formas `belt` y `field` toman de ella la semilla de su ruido, así que renombrar un archivo mueve lo que genera.

Describe algo que se genera. Cada entrada es una **forma** colocada por una **dispersión**, filtrada por dónde se le permite estar.

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

Solo `block` es obligatorio; todo lo demás puede omitirse y toma su valor predeterminado. `blocks` sustituye a `block` cuando uno no basta y tiene su propio ejemplo más abajo.

### Qué coloca

*entradas de worldgen*

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| ---------- | -------- | ------------------------------ | ----------------------- | ----------------------------------------------------------------------------------------------------- |
| `block`    | sí       | nombre de bloque               |                         | Lo que se coloca                                                                                      |
| `meta`     | no       | int                            | `0`                     | Qué variante de ese bloque                                                                            |
| `blocks`   | no       | lista de objetos               | ninguno                 | Una lista ponderada, que se usa en lugar de un solo bloque. Véase más abajo                           |
| `size`     | no       | int o rango                    | `8`                     | Cuántos bloques coloca un intento, o cuán grande es una forma con radio                               |
| `attempts` | no       | int o rango                    | `8`                     | Cuántas veces lo intenta por chunk                                                                    |
| `sparse`   | no       | boolean                        | `false`                 | Reparte los bloques en lugar de apretarlos juntos                                                     |
| `shape`    | no       | objeto                         | `{ "type": "cluster" }` | La forma que adopta. Véase [Formas](#formas)                                                          |
| `spread`   | no       | objeto                         | `{ "type": "even" }`    | Dónde se coloca. Véase [Dispersiones](#dispersiones)                                                  |
| `replace`  | no       | lista de nombres de bloque u objetos | `["minecraft:stone"]` | Qué puede sustituir. Véase más abajo                                                               |
| `adjacent` | no       | lista de nombres de bloque u objetos | ninguno           | Solo coloca donde uno de estos esté entre los 26 bloques que tocan el punto. Las mismas tres formas que `replace` |

### Dónde puede generarse

*entradas de worldgen*

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| ------------------------ | -------- | ------------------------ | --------------- | ---- |
| `minHeight`              | no       | int                      | `0`             | La y más baja en la que colocará |
| `maxHeight`              | no       | int                      | `64`            | La y más alta en la que colocará |
| `snap`                   | no       | `floor` o `ceiling`      | ninguno         | Mueve primero cada intento en vertical hasta el suelo o techo de cueva más cercano |
| `snapDepth`              | no       | int                      | `0`             | Cuánto se adentra `snap` más allá de la superficie, hacia abajo desde un suelo y hacia arriba desde un techo. `0` se queda en el espacio abierto contra la superficie, `1` es el propio bloque de la superficie, `2` el que hay tras él. Lo que puede sobrescribir lo sigue regulando `replace`, así que así es como un pack coloca en bandas un bloque justo bajo el suelo en lugar de encima |
| `dimensions`             | no       | lista de ints            | todas las dimensiones | En qué dimensiones se ejecuta |
| `dimensionsAreBlacklist` | no       | boolean                  | `false`         | Convierte esa lista en las que hay que evitar |
| `biomes`                 | no       | lista de nombres de bioma | todos los biomas | En qué biomas se ejecuta |
| `biomeTypes`             | no       | lista de tipos del diccionario | ninguno   | Biomas por tipo, como `FOREST` o `NETHER` |
| `biomesAreBlacklist`     | no       | boolean                  | `false`         | Convierte esas listas en las que hay que evitar |
| `minTemperature`         | no       | float                    | `-100.0`        | El bioma más frío en el que se generará |
| `maxTemperature`         | no       | float                    | `100.0`         | El bioma más cálido en el que se generará |
| `minRainfall`            | no       | float                    | `-100.0`        | El bioma más seco en el que se generará |
| `maxRainfall`            | no       | float                    | `100.0`         | El bioma más húmedo en el que se generará |
| `minDistanceFromSpawn`   | no       | int, bloques             | `0`             | A qué distancia del punto de aparición del mundo empieza |
| `caveRegions`            | no       | lista de nombres de región | ninguno       | Solo se genera dentro de estas [regiones de cuevas](#regiones-de-cuevas) |

### Señales de superficie y seguidores

*entradas de worldgen*

| Clave | Obligatoria | Valor | Predeterminado | Qué hace |
| ----------------- | -------- | -------------------------------- | ------------------ | ---- |
| `indicators`      | no       | lista de `block=weight`          | ninguno            | Bloques repartidos por la superficie sobre una veta que se generó, para que un jugador pueda saber qué hay bajo el suelo; elígelos para que coincidan con el contenido de la veta. `empty=weight` deja un punto desnudo |
| `indicatorCount`  | no       | int o rango                      | `1`                | Cuántos puntos de superficie recibe cada veta generada |
| `indicatorSpread` | no       | int, bloques                     | `0`                | A qué distancia más allá de la huella de la veta puede caer un indicador |
| `then`            | no       | lista de `name=weight` u objetos | ninguno            | Entradas de worldgen que crecen a partir de esta justo después de generarse, unidas a ella: el origen del seguidor se fija justo fuera del borde de esta veta, en la dirección que dan `thenSpread` y `thenDepth`, de modo que ambas se tocan. Una entrada es `name=weight`, o un objeto con `name`, `weight` y su propio `spread` y `depth` (int o rango) que anulan los de la veta solo para ese seguidor, de modo que una lista puede enviar la punta de un diamante hacia abajo y una rama hacia un lado. Un nombre simple se lee en el espacio de nombres de este pack, `empty=weight` no pone nada en cola. Un seguidor conserva su propia forma, bloques, tamaño y `replace` pero omite sus propios intentos, probabilidad, banda de alturas y filtros de bioma, y puede llevar él mismo `then`, tan profundo como quiera el pack; una entrada que ya se generó en la misma cadena la detiene |
| `thenCount`       | no       | int o rango                      | `1`                | Cuántos seguidores distintos se eligen de esa lista por veta generada, cada entrada como mucho una vez, de modo que una cuenta igual a la longitud de la lista hace crecer a todos ellos |
| `thenSpread`      | no       | int, bloques                     | el radio de la forma | Cuánto puede inclinarse hacia los lados la dirección en la que crece un seguidor, sorteado de menos esto a más esto |
| `thenDepth`       | no       | int o rango                      | `0`                | Cuánto se inclina la dirección hacia abajo (negativo) o hacia arriba. `0` sin inclinación lateral cuelga al seguidor recto hacia abajo |
| `prospectAs`      | no       | string                           | el nombre del archivo | Cómo nombra esta entrada un objeto de prospección en su lectura, p. ej. `Hematite` |

### Retrogen y requisitos

*entradas de worldgen*

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `retrogen`    | no | booleano | `false` | También genera en chunks que ya existen |
| `retrogenKey` | no | cadena | la clave de la configuración | Sustituye la clave de retrogen solo para esta entrada |
| `requires`    | no | lista de ids de mods o espacios de nombres de packs | ninguno | La entrada se omite a menos que estén presentes todos |

### Bloques con peso

*entradas de worldgen*

`blocks` sustituye a `block` cuando una sola entrada no basta. Los pesos son relativos, así que 80 y 20 es cuatro a uno.

```json
{
  "blocks": [
    { "block": "minecraft:wool", "meta": 2, "weight": 80 },
    { "block": "minecraft:wool", "weight": 20, "properties": { "color": "lime" } }
  ]
}
```

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `block`      | sí | nombre de bloque |  | Lo que se coloca |
| `meta`       | no | int | `0` | Qué variante |
| `weight`     | no | int | `1` | Con qué frecuencia se elige este frente a los demás |
| `properties` | no | objeto de propiedad a valor | ninguno | Propiedades del estado del bloque por nombre, para estados sin metadatos propios |

`block` y `meta` siguen siendo obligatorios en el nivel superior del archivo aunque se use `blocks`; la primera entrada es un buen valor para ponerlos.

### Objetivos de sustitución

*entradas de worldgen*

`replace` es una lista, y cada entrada admite una de tres formas.

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

| Forma | Ejemplo | Qué coincide |
| --- | --- | --- |
| Nombre | `"minecraft:stone"` | Todos los estados de ese bloque |
| Nombre y metadatos | `"minecraft:stone:3"` | Solo esos metadatos, aquí diorita |
| Objeto | `{ "block": "minecraft:stone", "properties": { "variant": "andesite" } }` | Solo ese estado |

La forma de objeto también admite `meta` en lugar de `properties`, lo cual equivale a la forma con dos puntos. Usa `"minecraft:air"` para generar en espacio abierto.

### Bloques adyacentes

*entradas de worldgen*

`adjacent` admite las mismas tres formas que `replace` y añade una segunda condición: el punto solo se usa cuando al menos uno de los 26 bloques que lo tocan, caras, aristas y esquinas, coincide con la lista. Si se omite, no se comprueba nada.

```json
{
  "block": "mypack:sulfur_ore",
  "replace": ["minecraft:sandstone"],
  "adjacent": ["minecraft:air"]
}
```

Eso coloca azufre en la arenisca solo donde ya está abierta a una cueva o a la superficie, y deja en paz la arenisca enterrada. Los vecinos en chunks que aún no existen se tratan como no coincidentes en lugar de leerse, así que la comprobación nunca provoca que se genere un chunk.

Todas las formas lo respetan, ya que forma parte de decidir si se puede tomar un bloque concreto. Una `geode` nombra por separado su corteza y su relleno, y esos dos se colocan sin la comprobación.

Una entrada que nombra solo bloques no registrados se omite con un error en lugar de generarse en todas partes.

### Entradas de seguidores

*entradas de worldgen*

Una entrada en la lista `then` de una entrada de worldgen es un nombre con un peso, o un objeto cuando ese seguidor necesita una dirección propia.

```json
{
  "then": [
    "mypack:quartz_halo=2",
    "empty=1",
    { "name": "mypack:side_branch", "weight": 1, "spread": 4, "depth": 0 }
  ]
}
```

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `name`   | sí | nombre de entrada |  | La entrada de worldgen que crece a partir de esta. Un nombre sin espacio de nombres se lee en el espacio de nombres de este pack |
| `weight` | no | int | `1` | Con qué frecuencia se elige este seguidor frente a los demás de la lista |
| `spread` | no | int, bloques | el `thenSpread` de la entrada | Cuánto puede desviarse lateralmente la dirección de este seguidor, solo para esta entrada |
| `depth`  | no | int o rango | el `thenDepth` de la entrada | Cuánto se inclina la dirección de este seguidor hacia abajo, negativo, o hacia arriba, solo para esta entrada |

`name=weight` es la forma abreviada de un objeto con solo esos dos, y `empty=weight` no pone nada en cola. Como `spread` y `depth` son por entrada, una misma lista puede enviar la punta de un diamante recto hacia abajo y una rama hacia un lado desde la misma veta.

## Formas

*generación del mundo*

Un bloque `shape` con un `type`. Las claves que no figuran para un tipo son ignoradas por él.

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita. Una clave marcada para un tipo solo la lee ese tipo.

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

| Tipo | Qué crea |
| --- | --- |
| `cluster`    | El blob por defecto, una veta de mineral. Usa `size` |
| `largevein`  | Una veta larga y sinuosa con ramas. Usa `size` |
| `plate`      | Un disco plano |
| `geode`      | Una bolsa hueca con una corteza |
| `decoration` | Dispersión en superficie, como flores o setas. Usa `size` |
| `tree`       | Un árbol completo |
| `vines`      | Enredaderas sobre lo que ya hay. Usa `size` |
| `basin`      | Un cuenco que se hace más profundo hacia el centro |
| `spire`      | Una columna que se estrecha |
| `nodule`     | Una bola irregular |
| `vent`       | Una columna estrecha que se detiene al chocar con algo |
| `imprint`    | Una de tus plantillas `.nbt`. Una que cabe dentro de un chunk se desplaza para que caiga entera en el chunk que se está construyendo, en lugar de invadir un vecino que aún no se ha creado, sea cual sea su giro; una mayor que un chunk solo se coloca donde el terreno circundante ya existe |
| `belt`       | Un cluster que abarca varios chunks, para regiones de piedra |
| `field`      | Vetas calculadas para todos los bloques a la vez, que comparten su forma con los grupos de dureza |
| `vein`       | Un depósito calculado como un campo de ruido con semilla alrededor de un origen, a la manera de Immersive Geology: cada chunk escribe su propia porción de toda veta cuyo alcance de 24 bloques lo toca, así que nada se encadena, y `/rdplserver vein` puede indicar dónde estará una veta antes de que exista el terreno. Usa `size`, `attempts`, `rarity` y la franja de altura; `pattern` elige el aspecto |
| `spring`     | Un fluido que mana de la pared de una cueva: se coloca donde hay roca arriba, abajo y en tres lados con un lado abierto, y se deja fluyendo |

### Tamaño y forma

*formas*

| Clave | Usada por | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `type`          | todas | una de las formas anteriores | `cluster` | Qué forma |
| `radius`        | plate, geode, basin, spire, nodule, vent | int o rango | `6` | Su anchura |
| `height`        | plate, geode, basin, spire, vent, tree | int o rango | `1`, `8` para geode, `5` para tree | Su altura o grosor |
| `width`         | geode | int o rango | `12` | La extensión total de la bolsa |
| `plane`         | plate, basin, spire, vent | `circle`, `square` | `circle` | Su huella |
| `slim`          | plate, largevein, nodule | booleano | `false` | Plate: una capa más fina. Largevein: ramas de un solo bloque. Nodule: cáscara hueca |
| `hanging`       | spire, vent | booleano | `false` | Crece hacia abajo desde un techo en lugar de hacia arriba desde un suelo |
| `taper`         | spire | `straight`, `bell`, `needle` | `straight` | Cómo se reduce la anchura hacia la punta. `straight` se estrecha de forma uniforme, `bell` mantiene su anchura abajo y luego cae, `needle` se afina de golpe hasta una punta larga |
| `outline`       | geode | nombre de bloque | ninguno | El bloque de la corteza |
| `fill`          | geode | nombre de bloque | ninguno | Con qué se rellena el centro. Si se omite, el centro queda hueco |
| `middle`        | geode | nombre de bloque | ninguno | Una cáscara entre el cuerpo y `outline`, la calcita de una geoda de amatista moderna |
| `budding`       | geode | nombre de bloque | ninguno | Se coloca en lugar de los bloques del cuerpo que dan al centro hueco, como hace la amatista en brote. Necesita `fill` |
| `buddingChance` | geode | 0.0 a 1.0 | `0.083` | Cuántos de esos bloques del cuerpo brotan |
| `crystal`       | geode | nombre de bloque | ninguno | Crece en el hueco junto a un bloque `budding`, como un cúmulo de amatista |
| `crystalChance` | geode | 0.0 a 1.0 | `0.35` | En cuántos de esos puntos crece uno |
| `crack`         | geode | 0.0 a 1.0 | `0` | La probabilidad de que una geoda esté abierta: un tubo desde el centro hacia fuera a través de todas las capas por un lado, relleno con `fill`. Las geodas de amatista modernas usan `0.95` |

### Colocación

*formas*

| Clave | Usada por | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `surface`          | decoration, tree | lista de nombres de bloque | ninguno | Sobre qué se asentará |
| `seeSky`           | decoration | booleano | `true` | Colocar solo donde el cielo es visible |
| `checkStay`        | decoration | booleano | `true` | Colocar solo donde el bloque sobreviviría |
| `stackHeight`      | decoration | int o rango | `1` | Cuántos apilar unos sobre otros |
| `scatterX`         | decoration, tree | int | `8` | Cuánto se desvía hacia los lados |
| `scatterY`         | decoration, tree | int | `4` | Cuánto se desvía en vertical |
| `scatterZ`         | decoration, tree | int | `8` | Cuánto se desvía hacia los lados |
| `rarity`           | cualquiera | int | ninguno (`400` para belt) | Una colocación por cada tantos chunks. En un belt esto separa los belts entre sí; en cualquier otra forma limita toda la entrada, de modo que solo un chunk de tantos tira sus `attempts`. `field` lo ignora |
| `rarityIsPerChunk` | cualquiera | booleano | `false` | Convierte `rarity` en cuántas colocaciones recibe cada chunk |

### Árboles

*formas*

```json
{
  "shape": { "type": "tree", "log": "mypack:ruby_log", "leaves": "mypack:ruby_leaves", "height": { "min": 4, "max": 7 }, "surface": ["minecraft:grass"] }
}
```

Un `tree` sin `log` ni `leaves` no genera nada, y lo indica en el registro. Si se nombra una `structure`, o varias en `structures`, se planta esa plantilla en cada punto en lugar de hacer crecer un árbol, y entonces no hacen falta `log` ni `leaves`; un árbol con plantilla lee `turns`, `mirrors`, `integrity`, `lootTable` y `locateAs` exactamente igual que un `imprint`.

| Clave | Usada por | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `log`    | tree | nombre de bloque | ninguno | El bloque del tronco |
| `leaves` | tree | nombre de bloque | ninguno | El bloque de las hojas |
| `vines`  | tree | booleano | `false` | Cuelga enredaderas de las hojas |

### Colocación de plantillas

*formas*

| Clave | Usada por | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `structure`  | imprint, tree | `namespace:name` | ninguno | La plantilla que se coloca |
| `integrity`  | imprint, tree | 1 a 100 | `100` | Porcentaje de los bloques de la plantilla que aparecen realmente |
| `lootTable`  | imprint, tree | `namespace:path` | ninguno | La tabla de botín con la que se rellena cada cofre de la plantilla colocada la primera vez que se abre, y cualquier otro contenedor que admita una, como un shulker box o el crate de un mod. Cubre `structure` y cada entrada de `structures`; cada cofre tira su propia semilla |
| `structures` | imprint, tree | lista | ninguno | Varias plantillas entre las que elegir, una por colocación. Cada entrada es `{ "structure": "namespace:name", "weight": 3 }`, o un nombre sin más para probabilidades iguales. Sustituye a `structure` |
| `turns`      | imprint, tree | lista | cualquiera | En qué sentido puede colocarse: `none`, `quarter`, `half`, `threequarter`. Las entradas pueden llevar un `weight`. Si se omite, las cuatro son igual de probables |
| `mirrors`    | imprint, tree | lista | ninguno | Además la voltea: `none`, `leftright`, `frontback`, con `weight` opcional. Una entrada que indica su propio peso se escribe `{ "mirror": "leftright", "weight": 2 }`, y una entrada de `turns` igual con `turn` |
| `at`         | imprint | dos ints, x y z | ninguno | Coloca exactamente una vez en esas coordenadas de bloque sobre la superficie, cuando se genera ese chunk, en lugar de al azar. Véase [Estructuras en lugares exactos](#estructuras-en-lugares-exactos) |
| `locateAs`   | imprint, tree | cadena | ninguno | Registra cada estructura que coloca esta entrada con ese nombre, para que `/locate <name>` encuentre la más cercana. Véase [Localizar estructuras colocadas](#localizar-estructuras-colocadas) |

Para una forma que ningún tipo integrado cubre, `imprint` es el camino: constrúyela como plantilla `.nbt` y colócala, con `structures` para variarla, `turns` y `mirrors` para girarla, e `integrity` para deshacerla en algo más tosco que el archivo que dibujaste.

### Estructuras en lugares exactos

*formas*

Las estructuras vanilla se fijan a puntos exactos con `structureAt` en los ajustes de `terrain`, como entradas `structure=x,z`, una por línea: `"structureAt": ["villages=1000,-500"]`. **La x y la z son coordenadas de bloque, no de chunk**, y la estructura se genera en el chunk que contiene ese bloque; el pozo de una aldea queda en ese mismo bloque, mientras que otras estructuras empiezan donde el juego las empezaría en ese chunk. Una entrada por instancia deseada. Su espaciado, separación, distancia mínima de aparición y comprobaciones de terreno llano se apartan, así que el punto es responsabilidad del pack, y dos fijaciones a menos de un chunk de distancia ponen dos estructuras en el mismo chunk. Una vez fundada, la estructura se asienta en el suelo de su chunk según las reglas habituales.

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `structureAt` | lista de `structure=x,z` | ninguno | Fija una estructura vanilla a un punto exacto, una entrada por instancia deseada. La x y la z son coordenadas de bloque, y la estructura se genera en el chunk que contiene ese bloque; su espaciado, separación, distancia mínima de aparición y comprobaciones de terreno llano se apartan |

Una entrada `imprint` se fija igual con `"at": [x, z]` en su forma, colocándose exactamente una vez en esas coordenadas sobre la superficie cuando se genera ese chunk, en lugar de al azar. Se combina con `locateAs`, de modo que una estructura fijada también puede encontrarse con /locate.

### Localizar estructuras colocadas

*formas*

Una entrada `imprint` con `"locateAs": "Crypt"` registra cada estructura que coloca con ese nombre, y `/locate Crypt` señala entonces la más cercana, con el nombre ofrecido en el autocompletado. Solo se pueden encontrar estructuras que ya se han generado, ya que las estructuras de los packs se colocan al azar a medida que se crean los chunks, no en una cuadrícula que el juego pudiera predecir. Los nombres viven en el guardado del mundo, así que sobreviven a los reinicios y funcionan en servidores. A un nombre registrado de este modo también se le puede dar su propio permiso con `gotoPlaceLevels`, de forma que un pack decida por separado quién puede ser llevado a sus propias estructuras y quién a las vanilla.

### Claves de campos y vetas

*formas*

| Clave | Usada por | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `field`     | field | objeto | `{ "type": "speckle" }` | Cómo se calcula el campo. Las mismas claves que el `field` de un grupo de dureza, descrito en [El campo](#el-campo): `speckle` con `chances` y `spread`, o `seeded` con `cell`, `seeds`, `reach`, `arms` y `armReach` |
| `threshold` | field, vein | 0.0 a 1.0 | `0.5` (`0.4` para vein) | Qué intensidad debe tener el campo en un bloque para colocarlo. Más bajo rellena más |
| `fade`      | field | int | `0` | Salpica la parte superior de la franja en lugar de terminarla en plano: en esa cantidad de bloques de lo alto del rango de altura, las probabilidades de colocación de cada bloque se reducen paso a paso, el mismo aspecto que el motor da a `deepStone` donde se une con el mundo de arriba |
| `pattern`   | vein | `default`, `banded` o `tube` | `default` | El aspecto del depósito: un blob deformado, capas apiladas cada pocos bloques, o tubos huecos que serpentean por la roca |
| `density`   | vein | 0.0 a 1.0 | `1.0` | La proporción de bloques que cumplen los requisitos y se colocan de verdad, una moneda por bloque |
| `rich`      | vein | nombre de bloque | ninguno | Se coloca en el quinto superior del rango del campo por encima de `threshold`, el corazón del depósito, en lugar de los bloques de la entrada |
| `poor`      | vein | nombre de bloque | ninguno | Se coloca en los dos quintos inferiores de ese rango, el margen, en lugar de los bloques de la entrada; el medio son los bloques propios de la entrada. Si se omite cualquiera de los dos niveles, se colocan ahí los bloques de la entrada |
| `richAt`    | vein | 0.0 a 1.0 | `0.88` | Dónde empieza el nivel rico en ese rango: `0.88` limita el bloque rico al octavo más fuerte del depósito, un número menor engorda el núcleo rico, `1.0` no deja ningún bloque rico |
| `poorAt`    | vein | 0.0 a 1.0 | `0.4` | Dónde empiezan los bloques propios de la entrada: por debajo se coloca el bloque `poor`, así que `0.4` da un margen de los dos quintos inferiores y `0.0` no deja margen pobre. Se limita a `richAt` |

### Cinturones

*formas*

Un `belt` es una bola mucho mayor que un chunk, usada para regiones de piedra y no para vetas de mineral. Su `radius` es el tamaño de la bola, y cada chunk calcula por sí mismo dónde empiezan las bolas cercanas, a partir de la semilla del mundo y del nombre de la propia entrada, de modo que un belt sale entero se generen como se generen los chunks y nunca se escribe nada en un chunk vecino.

```json
{
  "shape": { "type": "belt", "radius": 32, "rarity": 400 }
}
```

Un belt ignora `attempts` y `spread`, ya que se coloca por chunk y no por intento. `minHeight` y `maxHeight` son la franja en que se sitúan los centros, y la bola llega `radius` más allá de esa franja. `replace` decide qué se come, `biomes` y los límites de temperatura y lluvia se comprueban en el centro, así que un belt aparece entero o no aparece, en lugar de quedar cortado en el borde de un bioma.

El coste crece con el cubo de `radius`, y un `rarity` bajo lo multiplica, así que empieza con los valores por defecto y sube el radio poco a poco.

### Campos

*formas*

Un `field` no coloca nada en un punto y lo coloca todo a la vez. En lugar de elegir un punto y construir una forma a su alrededor, hace una pregunta a cada bloque del chunk, entre `minHeight` y `maxHeight`, y coloca donde la respuesta es al menos `threshold`. La pregunta es la misma que hacen los grupos de dureza, así que ambos describen las mismas vetas, y un pack puede crear un grupo y una entrada que coincidan.

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

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `threshold` | no | 0.0 a 1.0 | `0.5` | Qué intensidad debe tener el campo antes de colocar un bloque |
| `field`     | sí | objeto | ninguno | El mismo objeto que admite un grupo de dureza, con los mismos tipos `speckle` y `seeded` |

Un `threshold` bajo toma la mayor parte del campo y da filones anchos; uno alto toma solo el centro de cada mancha y da pequeñas bolsas dispersas. Con `speckle` obtienes muchas motas diminutas; con `seeded`, parches más redondos o, cuando tiene brazos, nudos con zarcillos que se extienden entre ellos.

Como un belt, un field ignora `attempts` y `spread`, ya que se consulta por chunk y no por intento, y nunca escribe en un chunk vecino. Se calcula a partir de la semilla del mundo y del nombre de la propia entrada, así que la misma semilla da siempre las mismas vetas, y dos entradas con nombres distintos nunca coinciden. `replace`, `adjacent`, `biomes` y los límites de clima se aplican como de costumbre.

Una veta `field` es la única forma que se describe en lugar de elegirse. Usa la misma retícula que los grupos de dureza, así que `seeded` con unos pocos brazos da nudos con zarcillos que se extienden hacia sus vecinos, lo cual es una veta y no un blob, y `threshold` decide qué parte es lo bastante sólida para colocarse:

```json
{
  "shape": {
    "type": "field",
    "threshold": 0.4,
    "field": { "type": "seeded", "cell": 10, "reach": 6.0, "arms": 3, "armReach": 5.0 }
  }
}
```

Las claves van en un objeto `field` propio, no junto a `type`, ya que el `type` de la forma ya dice `field`.

## Dispersiones

*generación del mundo*

Un bloque `spread` con un `type`.

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita. Una clave marcada para un tipo solo la lee ese tipo.

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

| Tipo | Dónde coloca las cosas |
| --- | --- |
| `even`      | En cualquier punto entre las alturas, de forma uniforme. El valor por defecto |
| `centered`  | Ponderado hacia una altura, disminuyendo con la distancia |
| `sprawl`    | Vetas fractales que abarcan un rango de altura |
| `terrain`   | Siguiendo la superficie |
| `cavern`    | En suelos de cueva, o en techos |
| `submerged` | Bajo el agua u otro fluido |

| Clave | Usada por | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `type`              | todas | una de las dispersiones anteriores | `even` | Qué dispersión |
| `center`            | centered | int | punto medio del rango de altura | La altura en torno a la que se agrupa |
| `range`             | centered | int | la mitad del rango de altura | A qué distancia de esa altura llega |
| `smoothness`        | centered | 1 a 8 | `2` | Cuántas tiradas se promedian. Más alto es una franja más estrecha |
| `veinHeight`        | sprawl | int | el rango de altura | Qué altura tiene una veta |
| `veinDiameter`      | sprawl | int | `12` | Qué anchura tiene una veta |
| `verticalDensity`   | sprawl | 1 a 100 | `16` | Qué sólida es en vertical |
| `horizontalDensity` | sprawl | 1 a 100 | `32` | Qué sólida es en horizontal |
| `offsetMin`         | terrain | int | `0` | Desplazamiento mínimo desde la superficie |
| `offsetMax`         | terrain | int | `offsetMin` | Desplazamiento máximo desde la superficie |
| `ceiling`           | cavern | booleano | `false` | Se adhiere al techo de la cueva en lugar de al suelo |

## Mapas de estructuras

*generación del mundo*

Un mapa de estructuras compone plantillas en un único edificio con nombre sobre una cuadrícula, mucho más allá del límite de 32 bloques de un solo archivo `.nbt`. Cada capa se dibuja como filas de caracteres sueltos, un carácter por celda, y se apila una altura de celda por encima de la capa anterior. Como máximo 8 capas de 8 por 8 celdas, que con la celda por defecto de 32 son 256 bloques por lado, la altura de construcción de vanilla.

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

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `name`       | texto | el nombre del archivo | Cómo se llama el mapa en los registros |
| `cell`       | número | `32` | El paso de la cuadrícula en bloques, hasta 48. Una plantilla menor que la celda se sitúa en la esquina de la celda, de modo que las piezas de tamaño completo encajan sin costuras |
| `ground`     | número | `0` | Qué capa hace de suelo en la superficie del terreno. Las capas anteriores excavan hacia abajo, que es como un edificio consigue sótanos |
| `at`         | dos números | ninguno | Fija una copia en coordenadas de bloque exactas, igual que `structureAt` fija una aldea |
| `spacing`    | número | `0` | Dispersa copias en una cuadrícula separada por esta cantidad de chunks, con variación según la semilla del mundo. `0` no dispersa ninguna, así que un mapa con solo `at` se construye exactamente una vez |
| `chance`     | número | `100` | El porcentaje de puntos de la cuadrícula que construyen una copia |
| `dimensions` | lista | todas | Ids de dimensiones en las que puede construirse el mapa |
| `layers`     | lista | ninguno | Las capas, de abajo arriba, cada una con una `palette` y un `map` |

Una paleta nombra plantillas por clave de registro del `<namespace>/structures/` de un pack.

| Valor | Qué hace |
| --- | --- |
| `"a": "mypack:keep"` | Cada celda `a` de esa capa coloca esta plantilla |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | Cada celda `a` tira la lista por peso, a partir de la semilla del mundo y del punto de la celda, así que dos copias del edificio difieren pero el mismo mundo siempre construye el mismo |
| `.` | Una celda vacía, no se coloca nada |

Cada copia tira una de las cuatro orientaciones a partir de la semilla del mundo y todo el edificio gira junto, plantillas incluidas, de modo que los muros que se encuentran entre celdas siguen encontrándose. La capa de suelo se asienta en la superficie del terreno muestreada bajo el centro del edificio. Cada chunk construye solo su propia porción de la cuadrícula, así que un edificio que abarca muchos chunks llega sin generación en cascada, sea cual sea el orden en que se carguen los chunks. Una [parcela de aldea](#parcelas-de-aldea) de tipo `template` también puede nombrar un mapa como su `structure`, lo que convierte el compuesto en un edificio de aldea.

## Parcelas de aldea

*generación del mundo*

`<namespace>/villages/*.json`

La ruta del archivo es el nombre de la parcela, que `villagePieces` puede nombrar después para conservarla o descartarla.

Un archivo aquí añade una pieza que las aldeas pueden construir, junto a las de vanilla. Dos tipos, elegidos con `type`.

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita. Una clave marcada para un tipo solo la lee ese tipo.

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

### Cada parcela

*parcelas de aldea*

| Clave | Usada por | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `type`       | todas | `farm` o `template` | `farm` | Qué tipo de parcela |
| `weight`     | todas | int | `3` | Con qué frecuencia se elige esta parcela frente a las demás del pack |
| `leastCount` | todas | int | `1` | Mínimo por aldea, antes de sumar el tamaño de la aldea |
| `mostCount`  | todas | int | `4` | Máximo por aldea, antes de sumar el tamaño de la aldea |
| `width`      | todas | int | `7` | Tamaño a lo ancho del camino |
| `height`     | todas | int | `4` | Altura despejada sobre el suelo |
| `depth`      | todas | int | `9` | Tamaño en dirección contraria al camino |
| `apron`      | todas | int | `2` | Cuánto puede diferir el suelo del nivel del camino bajo la parcela antes de rechazarla o deslizarla a lo largo de su camino: esa cantidad de bloques de relleno debajo, o de corte en una elevación por encima, y no más que eso entre su esquina más alta y la más baja. Una parcela ancha en colinas necesita más. Si lo pones alto, la parcela se aterraza directamente en una pendiente, lo que en el lugar equivocado se come una montaña |
| `ground`     | todas | nombre de bloque | `minecraft:dirt` | Lo que se compacta debajo en una pendiente |
| `requires`   | todas | lista de ids de mods o espacios de nombres de packs | ninguno | La parcela se deja fuera a menos que estén presentes todos |

Cada parcela de pack se ofrece a las aldeas como una sola entrada, así que `weight` decide cuál de tus parcelas se elige cuando una aldea pide una. La parcela que usó una colocación se escribe en los datos propios de la aldea, de modo que se reconstruye correctamente al cargar.

### Granjas

*parcelas de aldea*

Una `farm` es el campo de vanilla, descrito en lugar de programado: una parcela del tamaño que pidas, bordeada con un bloque, rellena con hileras de tierra separadas por canales de agua, plantada con un cultivo elegido por bloque de tu lista.

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

| Clave | Usada por | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `crops`    | farm | lista de nombres de bloque | trigo | Se planta uno por bloque, en una fase de crecimiento aleatoria |
| `edge`     | farm | nombre de bloque | `minecraft:log` | El marco alrededor de la parcela |
| `soil`     | farm | nombre de bloque | `minecraft:farmland` | De qué están hechas las hileras |
| `water`    | farm | booleano | `true` | Pone un canal de agua entre las hileras |
| `rowWidth` | farm | int | `2` | Qué ancha es cada hilera de tierra |

### Construido a partir de plantillas

*parcelas de aldea*

Una `template` coloca en su lugar una de tus estructuras `.nbt`, girada para mirar al camino de la aldea.

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

Una `template` cuyo `structure` nombra uno de tus [mapas de estructuras](#mapas-de-estructuras) coloca todo el compuesto como parcela. El tamaño de la parcela sale entonces del mapa, su huella y capas apiladas por la celda, así que `width`, `height`, `depth` e `integrity` no se leen. Las capas anteriores al `ground` del mapa excavan hacia abajo como sótanos, y las celdas de paleta con peso siguen tirándose por edificio, de modo que dos torres del mismo mapa pueden diferir.

```json
{
  "type": "template",
  "weight": 2,
  "structure": "mypack:castle",
  "villagers": 4
}
```

| Clave | Usada por | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `structure`      | template | `namespace:name` | ninguno | La plantilla que se coloca, o uno de tus mapas de estructuras, que entonces fija el tamaño de la parcela |
| `integrity`      | template | 1 a 100 | `100` | Porcentaje de los bloques de la plantilla que aparecen |
| `lootTable`      | template | `namespace:path` | ninguno | La tabla de botín con la que se rellena cada cofre de la plantilla colocada la primera vez que se abre. Una parcela que nombra un mapa de estructuras queda intacta |
| `villagers`      | todas | int | `0` | Cuántas personas genera la parcela |
| `villagerEntity` | todas | `namespace:name` | un aldeano | Quién vive allí, por ejemplo una variante de entidad propia |
| `villagerX`      | todas | int | `1` | Dónde aparecen, a lo ancho de la parcela |
| `villagerY`      | todas | int | `1` | Dónde aparecen, sobre el suelo |
| `villagerZ`      | todas | int | `1` | Dónde aparecen, hacia dentro de la parcela |

## Mapas de trazado de ciudades

*generación del mundo*

Un mapa de ciudad dibuja el plano de calles de una aldea sobre una cuadrícula, un carácter por celda, y la aldea se dispone a partir del dibujo en lugar de crecer. Las calles, plazas y parcelas salen como las mismas piezas que usa una aldea que crece, de modo que todas las opciones de camino, puente, embarcadero, callejón sin salida, farola, cul-de-sac y pieza central de plaza se aplican sin cambios. La plantilla de mundo nombra el mapa en `villageLayout`.

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

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `name`     | texto | el nombre del archivo | Cómo se llama el mapa en los registros |
| `cell`     | número | `48` | El paso de la cuadrícula en bloques, de 8 a 128. Los caminos discurren por el centro de sus celdas con la anchura de camino del pack y las parcelas se centran en las suyas, así que una celda necesita la parcela más ancha más espacio para dar a la calle |
| `palette`  | objeto | ninguno | Qué coloca cada carácter, listado más abajo |
| `map`      | lista | ninguno | Las filas, hasta 64 por 64 celdas. Una fila más corta que la más ancha queda abierta más allá de su final |
| `settings` | objeto | ninguno | Ajustes de aldea solo para este mapa, con los nombres que usa una plantilla de mundo, como `villagePathCenterBlock`. Prevalecen sobre los de la plantilla, y los ajustes de aldea propios de un bioma siguen prevaleciendo sobre ellos |

| Valor | Qué hace |
| --- | --- |
| `"#": "street"` | Una serie de celdas de calle a lo largo de una fila o columna se convierte en una caja de camino con la anchura de camino del pack. Donde una serie de fila cruza una serie de columna, el cruce se pinta como cualquier otro. Una celda de calle suelta sin serie en ninguno de los ejes se coloca como un tramo corto a lo largo de la fila |
| `"+": "plaza"` | Un pozo con su anillo de plaza. Las series pasan por las celdas de plaza, así que las calles se encuentran en el pozo, y una plaza en un cruce sitúa su pozo, o su pieza central `villageWellStructure`, en medio del cruce como una rotonda. La primera plaza del archivo es el pozo propio de la aldea, que fija el mapa donde se funda la aldea; un mapa sin ninguna se centra allí |
| `"a": "alley"` | Una serie estrecha. Los edificios dan a ella, pero no conecta nada, la regla del callejón como siempre |
| `"J": "junction"` | Una celda de calle trazada en ambas direcciones, de modo que haya un cruce allí aunque el dibujo solo la atraviese en un sentido. El brazo que la cruza mide una celda |
| `"b": "bulb"` | Una celda de calle que termina en un cul-de-sac. Cuando un mapa tiene una celda bulb, solo los extremos de camino situados en celdas bulb reciben un bulbo, y todos los que tienen espacio lo reciben; un mapa sin ninguna conserva tres de cada cuatro extremos |
| `"E": { "kind": "elevated", "height": 8 }` | Una celda de calle elevada sobre una plataforma de `height` bloques, de 2 a 64, por encima del suelo más alto bajo su tramo de celdas elevadas unidas, con una rampa de un bloque por fila en cada extremo. Un cruce de calles dentro del tramo se eleva con él. Un tramo cuya plataforma o rampas llegarían a una fila que una vía férrea o un pozo mantiene a su propio nivel se queda a nivel del suelo, con una línea en el registro. Cualquier valor puede escribirse como objeto de este modo, con `kind` nombrando la palabra |
| `"W": { "kind": "street", "settings": { "villagePathExtraWidth": 8 } }` | Una calle trazada y pavimentada con sus propias claves de camino, que prevalecen sobre las del mapa y la plantilla. Su anchura sigue su propio `villagePathExtraWidth`, `villagePathSidewalkWidth` y línea, y su superficie, líneas y aceras siguen sus propias claves de bloque, así que una avenida o un callejón se dibuja con una marca propia. Una serie toma las claves de su primera celda que establezca alguna. Sea ancha o estrecha, una calle dibujada sigue siendo una calle: nunca se toma por un callejón ni por un cul-de-sac |
| `"T": "mypack:tower"` | Una celda de parcela, trazada a partir de esa definición de parcela, centrada en la celda y orientada a la calle más cercana |
| `"T": ["mypack:a=3", "mypack:b=1"]` | Lo mismo, tirado por peso a partir de la semilla del mundo y del punto de la celda, de modo que el mismo mundo siempre traza allí la misma parcela |
| `"g": "grow"` | Se deja al crecimiento. Con `villagePlotsLeast` definido, los distritos que crecen y el relleno de calles ocupan esas celdas y se extienden hacia fuera desde el mapa; sin él, la celda queda abierta |
| `.` | Terreno abierto, no se traza nada |

Cada mapa tira una de las cuatro orientaciones a partir de la semilla del mundo y gira entero, así que un plano se lee igual desde cualquier lado. Los caminos se trazan primero, de modo que una parcela que se solaparía con un camino u otra parcela se deja abierta con una línea en el registro, y un nombre de parcela que ningún pack proporciona deja su celda abierta del mismo modo. El mapa no cambia cómo se visten las piezas: las claves de camino, `villageBlocks`, las lámparas y la sustitución del pozo se leen como en una aldea que crece. Nada crece a partir de un mapa dibujado: no se rellenan callejones junto a sus calles, y los extremos de sus caminos reciben sus bulbos, tres de cada cuatro como de costumbre o según digan sus celdas bulb, pero no casas a lo largo de ellos.

## Ciudad continua

*generación del mundo*

Una ciudad continua no tiene borde. Con `villageCitySpacing` en `1`, cada distrito del mundo es una ciudad propia: una plaza con el pozo en su centro y calles que salen de ella y enlazan con las calles de los distritos de alrededor. La ciudad sigue generándose allá donde vayan los jugadores, sin campo abierto entre ciudades.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "terrainAdaptation": true,
    "villageCitySpacing": 1,
    "villageBlockSizes": ["32=3", "64=1"],
    "villagePlotsMost": 0,
    "villagePlotsBackRow": true
  }
}
```

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `terrainAdaptation` | booleano | `false` | Traza las calles de ciudad propias de RDPL. Una ciudad continua lo necesita activado |
| `villageCitySpacing` | entero, de 0 a 256 | `0` | `1` hace de cada distrito una ciudad, y eso es lo que vuelve continua la ciudad. En 1.12.2 solo `1` cambia algo: `0` y cualquier otro número siguen sembrando las aldeas según `structureSpacing` como antes |
| `villageBlockSizes` | lista de `size=weight` | vacío | Qué profundidad tienen las manzanas entre calles paralelas, sorteada una vez por distrito, de modo que la ciudad mezcla cuadrículas finas y gruesas |
| `villagePlotsMost` | entero, 0 o más | `0` | El máximo de parcelas que asienta un distrito. 0 no pone techo |
| `villagePlotsBackRow` | booleano | `true` | Asienta una parcela detrás de cada parcela que da a una calle, para que también se edifique el interior de cada manzana |

Un distrito es un cuadrado del mundo el doble de ancho que la parcela más grande, más una plaza y una calle a cada lado, redondeado a 16 bloques y nunca por debajo de 96. Todos los demás ajustes de calles, desde los bloques de la calzada hasta farolas, puentes, túneles y alcantarillas, visten una ciudad continua como visten cualquier otra. `villagePlotsLeast` no tiene efecto, porque cada distrito ya es una ciudad entera. Un distrito cuyo plano contiene dos o menos parcelas y pozos queda vacío, como cualquier ciudad así de pequeña.

**Cómo se encuentran las calles.** Cada distrito funda su pozo en el mismo chunk del distrito y lleva su cruce principal, las dos calles que se cruzan en su pozo, hasta el borde del distrito. Por eso las calles del cruce de distritos vecinos se alinean en avenidas rectas que siguen por todo el mundo, y se encuentran en la costura entre dos distritos sin que ninguno mire al otro. Nada de lo que traza un distrito pasa de su borde, así que un distrito sale igual sea cual sea el vecino que se generó primero, y su pozo nunca se mueve.

**Lo que cuesta.** Cada chunk de una ciudad continua está edificado, así que el terreno nuevo cuesta en todas partes lo que cuesta el centro de una gran ciudad: colocar los edificios e iluminarlos. Un jugador que vuela deprisa adelanta a la generación, y la tasa de ticks baja mientras se generan distritos nuevos. Pregenerar el terreno donde empiezan los jugadores, con `pregenOnNewWorld` o `/rdplserver pregen`, y una distancia de visión moderada lo mantienen a raya, pero los TPS siguen más bajos que en un mundo normal mientras los jugadores exploran terreno nuevo. La partida guardada crece con el terreno explorado, como cualquier terreno edificado. El juego guarda en memoria solo las aldeas a 96 chunks o menos de un jugador o del punto de aparición, aparta las demás junto al registro de aldeas de la partida y vuelve a leer una, exactamente como era, cuando se genera de nuevo el terreno cercano.

## Retrogen

*generación del mundo*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "retrogen": true,
    "adoptExistingChunks": false
  }
}
```

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `retrogen`            | booleano | `false` | Pone al día los chunks guardados antes de que existiera una entrada en cada entrada de worldgen marcada con `"retrogen": true`. Desactivado, los chunks que ya existen se dejan tal cual |
| `adoptExistingChunks` | booleano | `false` | Qué ocurre la primera vez que se ve un chunk antiguo: activado, se sella como si este pack ya lo hubiera generado y nunca se pone al día; desactivado, se pone al día como cualquier otro. Para rellenar un mundo existente, activa `retrogen` y desactiva esto |

Una entrada con `"retrogen": true` se genera en chunks que se guardaron antes de que la añadieras. Cada chunk registra lo que ha tenido, así que nada se hace dos veces.

La marca de la entrada solo la señala como apta. Ponerse al día se activa con el ajuste `retrogen`, que un pack puede definir en su bloque `settings` o un jugador en la configuración, y está desactivado por defecto. Junto a él, `adoptExistingChunks` decide qué ocurre la primera vez que se ve un chunk antiguo: activado, el chunk se sella como si este pack ya lo hubiera generado y nunca se pone al día; desactivado, se pone al día como cualquier otro. Activar `retrogen` mientras `adoptExistingChunks` también está activado no hace nada, porque todos los chunks antiguos se dan por buenos antes de poder ponerse en cola. Para rellenar un mundo existente, activa `retrogen` y desactiva `adoptExistingChunks` a la vez.

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

Cambiar `retrogenKey` en la configuración hace que todos los chunks vuelvan a ser aptos, lo que añade las vetas nuevas sobre las antiguas, de modo que la densidad se duplica. Es deliberado, y por eso la clave es manual.

## Pregeneración

*generación del mundo*

Crear el terreno de un mundo por adelantado, para que nadie genere chunks mientras juega: sin lag de chunks, un tamaño en disco conocido, y una sola espera al principio en lugar de una primera hora a trompicones.

Los primeros 12 chunks alrededor del punto de aparición siempre se toman a cargo, digan lo que digan un pack o la configuración, porque el juego crea exactamente tanto por sí mismo antes de que nadie se una. Si se deja así, ese terreno llega sin iluminar y se va vistiendo chunk a chunk según el jugador lo recorre; adoptado, se termina de una pasada y el jugador aterriza en un terreno ya acabado. `pregenOnNewWorld` fija hasta dónde llegar más allá, y el comando ejecuta uno a mano.

`/rdplserver pregen <radius>` crea todos los chunks dentro de esa cantidad de chunks desde donde se ejecuta. `status` indica cuánto lleva, `stop` lo termina, y `<radius> relight` ejecuta solo la pasada de iluminación sobre terreno que ya existe, vistiendo las costuras a las que la ejecución no pudo llegar y dejando en paz lo que nunca se creó.

Mientras una ejecución está en marcha, todos quedan retenidos: convertidos en espectadores, inmóviles, con una línea pulsante a media pantalla y el mundo en pausa a su alrededor. El modo con el que llegó cada jugador se escribe en el jugador cuando se le retiene, de modo que un guardado hecho a mitad de ejecución, un cierre inesperado o una reconexión nunca dejan a nadie varado como espectador; al terminar, la ejecución devuelve exactamente el modo que tomó, o el `worldGameMode` del pack cuando hay uno. El progreso se anuncia cada décima parte, cada ejecución vuelve a iluminar su propio cuadrado al terminar, y cuando todo está hecho se libera a los jugadores y se les da la bienvenida. Hasta dónde se creó cada dimensión se guarda en el mundo, así que un mundo terminado nunca vuelve a ejecutarse, salvo que falte del disco cualquiera de los archivos en que vive el terreno de una dimensión, lo cual se detecta y rehace solo esa dimensión.

En un pack, estos van en el bloque `settings` de una [plantilla de mundo](#plantillas-de-mundo), como cualquier otra clave de `chunks`. Todas ellas mostradas, con `pregenBorderLimit` como única ausencia, ya que solo la contiene la configuración:

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

### Qué se crea

*pregeneración*

| Clave | Qué hace | Por qué la definirías |
| --- | --- | --- |
| `pregenOnNewWorld`            | Radio en chunks que se crea alrededor del punto de aparición antes de que nadie juegue. 12 es el mínimo y 0 significa ese mínimo y no nada, ya que el juego crea de todos modos 12 chunks alrededor del punto de aparición: la ejecución adopta ese terreno y lo ilumina de una pasada en lugar de dejar que llegue a goteo detrás del jugador. Súbelo para llegar más lejos que el juego | Fija hasta dónde llega un pack más allá del terreno que el juego ya crea |
| `pregenDimensions`            | Qué dimensiones se crean, en orden, cada una alrededor de su propio punto de aparición | Añade el Nether, el End o tus propias dimensiones |
| `pregenAllDimensions`         | Todas las dimensiones registradas en lugar de una lista, empezando por el mundo principal | Packs con muchas dimensiones. Cuentan las dimensiones de todos los mods, así que ojo con el tamaño |
| `pregenDimensionsWhenEntered` | Estas se crean la primera vez que alguien pisa en ellas, reteniendo a todos de nuevo hasta terminar | Dimensiones que la mayoría de los jugadores no visita; quienes nunca van no pagan nada |
| `pregenToBorder`              | Rellena cada dimensión hasta su borde del mundo en lugar de un radio | Mundos acotados |
| `pregenBorderLimit`           | Hasta dónde puede llegar un borde antes de que se rechace la ejecución. Solo configuración, nunca una clave de pack | Una protección contra una ejecución descontrolada; súbelo solo sabiendo el tiempo y el disco que permite |

### Cómo se comporta una partida

*pregeneración*

| Clave | Qué hace | Por qué la definirías |
| --- | --- | --- |
| `pregenResume`         | Una ejecución detenida o interrumpida continúa donde se quedó. La dimensión, el centro y el radio de la ejecución se escriben en el guardado al empezar, así que un cierre inesperado, un corte de luz o salir a mitad de ejecución se reanudan en unos diez segundos desde donde acabaron en la siguiente carga. Una ejecución detenida a propósito, por comando o por el watchdog, permanece detenida | Ejecuciones largas en servidores; las pequeñas se reinician con poco coste sin esto |
| `pregenKeepLoaded`     | Chunks que se mantienen cargados tras la ejecución para que los vecinos de un chunk estén a mano cuando se viste y se ilumina | Súbelo si la reiluminación informa de muchos chunks pendientes; cuesta memoria |
| `pregenPauseAbove`     | La ejecución descansa cuando hay tantos chunks esperando a ser escritos | Bájalo para un disco lento |
| `pregenMillisPerRound` | Cuánto tiempo puede dedicar cada tick a crear terreno | Súbelo en un mundo vacío, bájalo en un servidor donde la gente está jugando |

La pregeneración tiene su propia vía rápida para la iluminación, y se aparta cuando hay instalado un motor de luz como Alfheim o Phosphor, dejando que ese motor haga el trabajo. De un modo u otro acabas con terreno terminado y totalmente iluminado.

Ejecútala tú mismo antes de distribuir, con el radio que se va a distribuir, de principio a fin. Los chunks crecen con el cuadrado del radio: 63 en cada sentido son dieciséis mil chunks, 500 son más de un millón, a unos diez kilobytes cada uno, así que la carpeta de regiones de tu mundo de prueba y el tiempo real son los números honestos que presentar a los jugadores. No distribuyas un radio que nunca se ejecutó.

### Qué ven los jugadores

*pregeneración*

| Clave | Qué hace | Por qué la definirías |
| --- | --- | --- |
| `pregenRunningSays`, `pregenRelightSays`, `pregenFinishedSays`, `pregenStoppedSays` | Los mensajes de chat de cada fase. Los dos primeros pueden contener `%d` para el porcentaje y, después, `%s` para el nombre de la dimensión, o `%1$d` y `%2$s` para ponerlos en cualquier orden, y siempre terminan con ` - ETA 00:00:00` para esa pasada, que no es un ajuste. Terminado y detenido se dicen una vez, cuando todo lo pedido está hecho, terminando con ` - Total time 00:00:00` para el conjunto, que tampoco es un ajuste | Reescríbelos con la voz de tu pack, nombra la dimensión cuando se crean varias, o siléncialos |
| `pregenSpectatingSays` | La línea de retención a media pantalla mientras se crea el terreno. Con su valor por defecto habla en el idioma de cada jugador; vacía no muestra nada | Mantenla por debajo de unos treinta y cinco caracteres o las ventanas pequeñas la recortan |
| `pregenLogo` | Dónde se sitúa el logo cuando termina la pregeneración: `left`, `center` o `right`, encima del texto de media pantalla, mostrado unos segundos y luego desvaneciéndose con la niebla | Siempre se muestra; una palabra desconocida se lee como `center` |
| `welcomeSays` | El saludo verde, mostrado en cada inicio de sesión y después de la pregeneración. Una entrada sin más es la línea para todas partes; una entrada `dimension=message` la sustituye para esa dimensión y además saluda a cada llegada allí, p. ej. `"-1=Welcome to the Nether!"`. Un mensaje vacío tras el `=` silencia esa dimensión; una lista vacía no muestra nada. Con su valor por defecto habla en el idioma de cada jugador | Una línea sin más nombra tu pack; añade líneas por dimensión para dar tema a cada mundo. Mantén las líneas por debajo de unos treinta y cinco caracteres |
| `saysCard` | Muestra las líneas que dice este mod, la bienvenida, el progreso de la pregeneración y las líneas de amenaza, como una tarjeta en la esquina inferior derecha en lugar de en el chat. La tarjeta se desliza, permanece ocho segundos y se desvanece, y se muestra también sobre una pantalla abierta | Actívalo cuando el chat esté saturado o las líneas deban leerse como parte del mundo y no como charla |
| `saysIcon` | Un ítem dibujado en la tarjeta, p. ej. `minecraft:compass`. Vacío no dibuja ninguno | Dale a la tarjeta el emblema de tu pack |
| `saysColor` | El color de fondo de la tarjeta en hexadecimal, p. ej. `1E2630`. Vacío usa un gris pizarra oscuro | Combínalo con la paleta de tu pack |
| `saysImage` | Un PNG de los assets de cliente del pack, p. ej. `rubyworld:textures/gui/card.png`, estirado sobre la tarjeta como fondo y dibujado sobre el color. Vacío no dibuja ninguno | Dale a la tarjeta un panel pintado; mantén la imagen ancha y baja, se estira a lo que necesite el texto |
| `saysBackground` | Dibuja el panel de la tarjeta, su borde y la franja de color, y el fondo oscuro tras la bienvenida y las notas en medio de la pantalla mientras se retiene a un jugador. Desactivado deja solo el texto, que conserva su sombra, y `saysImage` si hay uno definido | Deja que las líneas floten sobre el mundo, o que un `saysImage` pintado se sostenga por sí solo |
| `saysFont` | La fuente en que se dibuja el texto de la tarjeta, nombrada como `namespace:name`, p. ej. `rubyworld:runes`. Vacío usa la fuente de RDPL, `resourcedatapackloader:rdpl`. El archivo que nombra se describe en Tarjetas | Dale a la tarjeta la tipografía propia de tu pack |
| `toasts` | Cuáles de los toasts del juego, las ventanas emergentes de la esquina superior derecha, se muestran. `true` los muestra todos y `false` ninguno; una lista muestra solo los tipos que nombra: `advancements`, `recipes` para recetas desbloqueadas, `tutorial` para las pistas del tutorial, `system` para los avisos propios del juego, y `other` para todo toast que los demás no cubren, como los de otros mods. Por defecto no muestra ninguno. El cliente de un jugador toma el valor al unirse | Conserva `["advancements"]` cuando tu pack guía a los jugadores con logros y los demás estorban |

### Copia de seguridad y reinicio del mapa

*pregeneración*

| Clave | Qué hace | Por qué la definirías |
| --- | --- | --- |
| `pregenBackup`          | Copia el mundo a una copia de seguridad intacta cuando termina la pregeneración, mientras los jugadores siguen retenidos. La generación se paga entonces una sola vez: un reinicio posterior, o un mundo nuevo con el mismo pack y semilla, restaura la copia en lugar de generar de nuevo, lo cual es mucho más rápido que pregenerar dos veces. La copia se guarda fuera del guardado, en `rdpl-pristine/<world>` a su lado, para que las copias de otros mods no la barran y no aparezca en una carpeta que ellos gestionen. Una copia cuyos packs ya no coinciden con los cargados se descarta y se vuelve a guardar a partir del mundo actual, así que un cambio de pack nunca restablece el mapa de otro | `false` |
| `pregenBackupSays`      | La línea de media pantalla que se muestra a los jugadores mientras se hace esa copia, con el porcentaje detrás. Vacía no muestra nada y la copia se hace en silencio | `Pack requested world backup` |
| `resetSays`             | La línea de media pantalla que se muestra a los jugadores mientras `/rdplserver reset` o el final de una ronda restablece el mapa. Vacía reinicia en silencio | `Pack requested map reset` |
| `resetSendsTo`          | Dónde se coloca a los jugadores tras un reinicio: `spawn`, una posición como `x,y,z`, o `dimension:x,y,z` para enviarlos a otro mundo, que es como un reinicio deja a todos en un vestíbulo en lugar de devolverlos a la arena | `spawn` |
| `resetRuns`             | Una función que se ejecuta después de que un reinicio haya limpiado el mapa, nombrada `namespace:path`. Esto es lo que vuelve a construir la arena, ya que un pack que hizo su mapa con una función puede simplemente ejecutarla una segunda vez. Vacía no ejecuta nada | vacío |
| `resetClearsEntities`   | Elimina toda entidad que no sea un jugador. Mobs, ítems soltados y experiencia desaparecen, que es lo que deja el mapa como empezó | `true` |
| `resetClearsScores`     | Pone a cero todo objetivo que mantiene el pack, para que una partida nueva empiece desde cero. Los equipos en sí se conservan | `true` |
| `resetClearsInventory`  | Vacía el inventario de cada jugador, armadura y mano secundaria incluidas, para que una ronda empiece con lo que reparte el mapa y no con lo que dejó la anterior. El `gives` de un bando se entrega de nuevo justo después | `false` |
| `resetClearsExperience` | Devuelve la experiencia de cada jugador al nivel cero | `false` |

---

# Modos de juego

## Introducción al mundo

*modos de juego*

`<namespace>/worldintro/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta. Se ejecuta toda introducción que distribuya un pack, en el orden de los packs.

Muestra una serie de páginas cuando un jugador entra en el mundo, antes de que tome el control. Texto que se desplaza sobre una imagen, una pantalla de título, una presentación de diapositivas, o las tres cosas seguidas.

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

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `pages`    | sí | lista de páginas | ninguno | Se muestran en orden. Un archivo sin páginas se rechaza con un error |
| `once`     | no | booleano | `false` | Se reproduce una vez por jugador y mundo en lugar de en cada unión |
| `music`    | no | nombre de evento de sonido | ninguno | Una pista para toda la serie, que empieza con la primera página |
| `requires` | no | lista de ids de mods o espacios de nombres de packs | ninguno | La introducción se omite a menos que estén presentes todos |

### Páginas

*introducción al mundo*

Cada entrada de `pages`:

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `mode`        | no | `scroll` o `static` | `scroll` | Texto que se mueve, o texto que se queda quieto hasta que el jugador avanza |
| `text`        | no | ruta a un archivo `.txt` | ninguno | Las palabras. Omítelo para una página que son solo imágenes |
| `background`  | no | ruta de textura | el fondo de tierra en mosaico | Un fondo |
| `backgrounds` | no | lista de rutas de textura | ninguno | Varios, que se alternan. Se suma a `background` si das ambos |
| `interval`    | no | segundos | `5.0` | Cuánto se mantiene cada fondo, cuando hay más de uno |
| `time`        | no | segundos | calculado a partir del texto | Cuánto tarda una página con desplazamiento, de principio a fin. En una página fija, o en la última página de cualquier tipo, es el tiempo hasta que la página avanza sola, y sin él esperan al botón |
| `direction`   | no | `up` o `down` | `up` | En qué sentido se desplaza el texto |
| `textScale`   | no | número | `1.0` | Multiplica el tamaño de la fuente. Una página `static` ajusta su texto al ancho de la pantalla, menos un margen a cada lado, y cuando aun así quedaría bajo los botones su texto se dibuja más pequeño, hasta la mitad, hasta que cabe |
| `settle`      | no | booleano | `false` | Termina con la última línea centrada en lugar de salirse del todo de la pantalla |

### Texto y tiempos

*introducción al mundo*

Los archivos de texto van en `<namespace>/texts/*.txt`. Texto plano, un párrafo por línea, y las líneas en blanco se conservan como líneas en blanco. Un archivo `.md` se lee igual, y ambos tipos admiten el formato descrito más abajo. `PLAYERNAME` se sustituye por el nombre del jugador, la misma sustitución que usa el poema final de vanilla.

`time` fija cuánto dura la página, de modo que la misma página tarda lo mismo tanto si contiene una línea como veinte. Ajusta la velocidad de lectura según cuánto pongas en la página. Si omites `time`, la página avanza a la misma velocidad que los créditos de vanilla, donde más texto simplemente tarda más.

### Formato de texto

*introducción al mundo*

Los textos de introducción admiten Markdown. Un archivo sin marcas se muestra exactamente como texto plano.

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

| Marca | Se escribe como | Se muestra como |
| --- | --- | --- |
| Encabezado | `# `, `## `, `### ` al principio de una línea | Negrita y más grande: el doble, una vez y media y una vez y cuarto del tamaño del texto, alineado como el texto del cuerpo |
| Negrita | `**text**` | La variante en negrita de la fuente |
| Cursiva | `*text*` | La variante en cursiva de la fuente |
| Negrita cursiva | `***text***` | La variante en negrita, inclinada |
| Tachado | `~~text~~` | Con una línea que lo tacha |
| Código | `` `text` `` | Teñido de aguamarina |
| Enlace | `[text](url)` | Solo el texto, subrayado; no se puede pulsar |
| Rúnico | `{runic}text{/runic}` | El texto en el cifrado de runas, `resourcedatapackloader:rdpl_runic`, mientras el resto de la línea conserva su fuente; la negrita y la cursiva dentro de él usan las variantes en negrita y cursiva del cifrado. Funciona en encabezados, elementos de lista y citas, y un `{runic}` sin cerrar se muestra tal como está escrito |
| Viñeta | `- ` o `* ` al principio de una línea | Una viñeta, con las líneas ajustadas sangradas bajo el texto; dos espacios antes de la marca la anidan un nivel |
| Numerado | `1. ` al principio de una línea | El número tal como está escrito, con la misma sangría |
| Cita | `> ` al principio de una línea | Sangrada y atenuada |
| Regla | `---` en una línea propia | Una línea horizontal a lo ancho del texto |
| Imagen | `![alt](namespace:textures/....png)` en una línea propia | La imagen, reducida al ancho del texto y conservando su proporción; el texto alternativo se muestra si no se puede leer |
| Escape | `\` antes de una marca, p. ej. `\*` | La marca como un carácter normal |

Las tablas y los bloques de código delimitados (entre líneas ```) se dibujan como texto plano, con todas sus marcas. El tiempo calculado de una página con desplazamiento y el ajuste de tamaño de una página fija cuentan ambos la altura ya maquetada, imágenes incluidas. Los títulos y líneas de las tarjetas, los mensajes de Says y las notas de bienvenida y de espera admiten las marcas en línea, desde la negrita hasta el rúnico, una línea cada uno.

### Cómo se juega

*introducción al mundo*

Una página con desplazamiento pasa a la siguiente cuando se acaba su tiempo. La última página nunca avanza sola, espera. En la parte inferior aparecen **Next Page** y **Skip All**, o un único **Continue to World** en la última página. Escape hace lo mismo que Skip All. Las páginas estáticas centran todas las líneas. Las páginas con desplazamiento se mantienen en una columna fija, como los créditos.

En un jugador el mundo se pausa detrás de la introducción, de modo que nada se acerca sigilosamente al jugador mientras lee. La única excepción es el terreno que aún se está creando cuando se abre la introducción: entonces la creación continúa detrás de las páginas, y el jugador permanece retenido como espectador hasta que continúa hacia el mundo, aunque la ejecución termine antes. En un servidor el mundo sigue en marcha, y un cliente de vanilla nunca ve la introducción y entra con normalidad. El saludo de bienvenida espera a que se cierren las páginas, para no perderse detrás de ellas.

`once` se recuerda en los datos guardados del jugador y sobrevive a la muerte. `/rdplserver intro` lo borra para quien lo ejecuta, de modo que la introducción se reproduce de nuevo la próxima vez que entre. No se reproduce en el momento, lo que evita que sea una vía de regreso a la secuencia de entrada en mitad de una partida.

Los fondos se estiran para llenar la ventana, así que una imagen 16:9 va bien en una ventana 16:9 y una cuadrada se ve aplastada. Recorta la imagen a la forma adecuada en lugar de confiar en el ajuste. `music` admite cualquier evento de sonido registrado, de vanilla o uno que tu propio pack añada mediante `sounds`. No se repite en bucle, así que una pista corta termina y deja silencio tras ella.

Si más de un pack incluye una introducción, sus páginas se reproducen una tras otra en el orden de los packs en lugar de que una gane. Restríngelas con `requires` si solo quieres una.

## Equipos

*modos de juego*

`<namespace>/teams/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan. Cada archivo es un bando.

Un bando es un equipo real en el marcador propio del juego, así que `/scoreboard teams list` lo ve, conserva a sus miembros tras guardar y recargar, y un cliente sin este mod muestra los colores y las etiquetas de nombre igual que con cualquier equipo de vanilla. La pertenencia es por nombre, así que cualquier cosa con un nombre o un UUID puede estar en un bando: un jugador, un zombi, un aldeano, un soporte para armadura.

Un bando solo se pone en juego donde un pack lo pide: sin ninguna carpeta `teams`, el mod no añade ningún equipo, no escucha nada y no ofrece el comando. Un operador de servidor que edite un archivo puede ejecutar `/rdpl reload` para aplicar el cambio al mundo en marcha sin reiniciar.

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

### El bando

*equipos*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `name` | texto | el nombre del archivo | El nombre del equipo en el marcador, de 1 a 16 caracteres. Es lo que usan `/scoreboard` y los demás archivos |
| `displayName` | texto | el nombre | Lo que se muestra a los jugadores en lugar del nombre |
| `color` | texto | `white` | Uno de los dieciséis colores de texto. Tiñe la etiqueta de nombre y es la clave de los espacios de barra lateral por equipo |
| `prefix` | texto | vacío | Se antepone al nombre de un miembro, después del color |
| `suffix` | texto | vacío | Se añade después del nombre de un miembro |
| `scoreboard` | booleano | `true` | Si el bando existe como equipo en el marcador del juego. Desactivado, no se pone ningún equipo en juego: sus mobs llevan el color del bando en el nombre, nada les impide pelear entre sí y no se les anota ningún punto, ya que la puntuación va por equipo |

### Combate y visibilidad

*equipos*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `friendlyFire` | booleano | `false` | Si los miembros pueden hacerse daño entre sí. Es también el valor por defecto de `mobFriendlyFire` |
| `mobFriendlyFire` | booleano | `friendlyFire` | Si los mobs de un bando pueden dañar a su propio bando con explosiones y TNT lanzada, algo que el juego por sí solo nunca impide. Desactivado protege al bando; activado lo deja como lo tiene el juego |
| `seeFriendlyInvisibles` | booleano | `true` | Si los miembros se ven entre sí mientras están invisibles |
| `nameTags` | texto | `always` | `always`, `never`, `hideForOtherTeams` o `hideForOwnTeam` |
| `deathMessages` | texto | `always` | Las mismas cuatro palabras, para quién recibe el aviso cuando muere un miembro |
| `collision` | texto | `always` | `always`, `never`, `pushOtherTeams` o `pushOwnTeam` |

### Quién se une

*equipos*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `entities` | lista | vacío | Ids de entidad cuyas apariciones se unen todas a este bando, como `minecraft:zombie` o una propia |
| `players` | lista | vacío | Nombres de jugador que se unen a este bando al iniciar sesión |
| `spawnBox` | lista | ninguno | Seis números enteros, x y z a x y z. Todo lo que aparezca dentro se une, y las esquinas pueden darse en cualquier orden |
| `joinable` | booleano | `true` | Si un jugador puede unirse con `/rdpl team join`. Ponlo en false para un bando que es solo de mobs |
| `balance` | booleano | `false` | Si `/rdpl team join` sin nombre puede poner aquí a un jugador. Entre los bandos que lo permiten, se elige el que tenga menos jugadores |
| `picks` | número | `0` | Cuántos miembros sortea este bando al azar. En cada ronda abierta, el bando deja volver a donde estaban a los últimos sorteados y sortea de nuevo entre todo lo que nombra `picksFrom`; entre sorteos, un inicio de sesión o una aparición de ese grupo ocupa de inmediato un puesto vacío. Para lo que sirve es para un jugador entre todos, en un bando propio |
| `picksFrom` | lista | vacío | De qué se hace el sorteo: `players` para todos los conectados, e ids de entidad para cada mob vivo de ese tipo |
| `standIn` | objeto | ninguno | Un mob que ocupa el bando mientras no haya ningún jugador en él: `{ "entity": "mypack:herobrine", "at": "23,31,0" }` mantiene vivo uno de esa entidad en ese punto del Overworld, invocándolo cuando falta, y lo elimina en cuanto un jugador se une al bando, de modo que una partida se juega contra la IA hasta que un jugador toma el papel. Se comprueba cada cinco segundos; el punto debe estar en terreno cargado. En una partida con vestíbulo (`opens.by: leader`), un sustituto solo se invoca mientras el vestíbulo espera y cuando se abre la ronda, así que uno que caiga se queda fuera durante el resto de la ronda y su final hasta que todos vuelvan al vestíbulo; sin vestíbulo, un sustituto caído no se repone mientras se juega una ronda que termina con `ends.lastStanding` |

Hay tres formas de unirse, y un bando puede usarlas todas. `entities` nombra ids de entidad, y cualquier cosa de ese tipo se une al aparecer, que es como un pack da bandos a los mobs sin tocar los mobs. `spawnBox` reclama una esquina del mundo, y todo lo que aparezca dentro se une, lo que conviene a una arena donde ambos bandos usan el mismo mob. `players` nombra jugadores de forma directa. Además de eso, un jugador puede unirse con `/rdpl team join <name>` a menos que el bando ponga `joinable` en false, y salir con `/rdpl team leave`.

### Kit inicial y punto de aparición

*equipos*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `gives` | lista | vacío | Ítems que se ponen en el inventario de un jugador al unirse al bando, un nombre de ítem para uno o `{ "item", "count", "unbreakable" }` para más, o para uno que nunca se desgasta, en cualquier ranura libre y soltados a sus pies cuando no hay ninguna. Se entregan de nuevo tras un reinicio que vacía los inventarios (`resetClearsInventory`) |
| `spawn` | texto | ninguno | `x,y,z` en el Overworld donde se coloca a los jugadores del bando cuando se abre una ronda, de modo que cada bando empiece en su propio terreno; sin él se quedan donde los dejó el reinicio o el vestíbulo |

### El líder

*equipos*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `lead` | texto | `none` | Cómo se elige al líder del bando: `none`, `first` para quien se unió antes al bando entre los conectados, de modo que pasa por el orden de ingreso mientras uno está ausente y vuelve con él; se les avisa al llegar, tras la introducción y cualquier espera, y de nuevo cuando pasa a ellos, `topScore` para quien tenga la puntuación más alta en el objetivo que nombra `leadOn`, `appointed` para el jugador que nombra `leadIs`, `vote` para quien voten los miembros, o `claim` para quien lo reclame primero. Un líder es una etiqueta y un color y nada más: no concede ningún poder, así que un líder que cierra sesión no rompe nada |
| `leadOn` | texto | vacío | Con `topScore`, el objetivo por el que se clasifica a los miembros. Se calcula de nuevo cada vez que se lee, así que sigue a la puntuación |
| `leadIs` | texto | vacío | Con `appointed`, el jugador que lidera |
| `leadSays` | texto | `You are the current round leader` | Lo que se dice a un jugador cuando el liderazgo llega a él: al llegar a un bando que lidera, al reclamarlo o cuando un liderazgo `first` pasa a él, en cuyo caso indica quién se fue. `{side}` es el nombre visible del bando; vacío no dice nada |
| `leadRuns` | texto | vacío | Una función, `namespace:path`, que se ejecuta una vez cada vez que el liderazgo pasa a un jugador: el primer líder y cada relevo posterior. Se ejecuta como el líder, en su posición, con el permiso que tiene una función que concede un progreso, de modo que `@s` es el líder. Se comprueba cada segundo; si un líder está desconectado, se ejecuta para él la próxima vez que esté conectado. Un reinicio decide el liderazgo de nuevo |

## Puntuación

*modos de juego*

`<namespace>/scoring/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan. Cada archivo es un objetivo.

Un objetivo es un objetivo real en el marcador propio del juego, así que `/scoreboard players list` lo lee y conserva sus puntuaciones al guardar. `criterion` es lo que el juego cuenta por sí mismo: `dummy` para una puntuación que solo mueve este pack, o `deathCount`, `playerKillCount`, `totalKillCount`, `health`, o cualquier nombre `stat.` o `achievement.` que el juego conozca.

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

### El objetivo

*puntuación*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `name` | texto | el nombre del archivo | El nombre del objetivo en el marcador, de 1 a 16 caracteres |
| `displayName` | texto | el nombre | Lo que se muestra a los jugadores en lugar del nombre |
| `criterion` | texto | `dummy` | Lo que el juego cuenta por sí mismo. Uno desconocido se rechaza con una línea que lo indica |
| `display` | texto | vacío | `sidebar`, `list`, `belowName` o `sidebar.team.<color>`. Vacío no lo muestra en ningún sitio; no hay una pantalla de marcador que abrir |
| `render` | texto | el propio del criterio | `integer` o `hearts` |
| `teamTotals` | booleano | `true` | Los puntos se anotan en una fila con el nombre del equipo del miembro |
| `individuals` | booleano | `false` | Los puntos se anotan también en una fila del propio miembro |
| `carries` | booleano | `false` | El objetivo sobrevive a un reinicio del mapa en lugar de borrarse con él. Un recuento de rondas ganadas de una partida es un ejemplo |
| `awardsTo` | texto | vacío | Otro objetivo al que este concede un punto cuando termina, para el bando que iba en cabeza. Una clasificación igualada no concede nada |
| `tiebreak` | booleano | `false` | Una ronda que termina con empate en cabeza sortea uno de los bandos empatados con el azar del mundo, registra el sorteo y lo premia como de costumbre. Una partida, un objetivo sin `awardsTo`, sortea igual y nombra al bando sorteado al principio de sus resultados |

### Puntos

*puntuación*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `points.kill` | objeto | vacío | Id de entidad a puntos, acreditados al bando del autor de la muerte. `minecraft:player` puntúa la muerte de un jugador |
| `points.death` | int | `0` | Puntos cada vez que muere un miembro, muera como muera. Puede ser negativo |
| `points.ownKill` | int | `0` | Puntos por matar a alguien del propio bando del autor, en lugar del valor de `kill`. 0 no puntúa nada por ello; un número negativo es una penalización |

`points` es lo que este mod añade a lo que cuenta el juego, alimentado en el mismo objetivo para que `/scoreboard` lo siga leyendo. `kill` vale tantos puntos por cada id de entidad abatida, acreditados al bando del autor; `death` vale tantos cada vez que muere un miembro de un bando, y puede ser negativo. Con `teamTotals` los puntos se anotan en una fila con el nombre del equipo, que es lo que permite que la barra lateral muestre cuatro bandos en lugar de una fila por cada mob. `individuals` añade además una fila por miembro, y está desactivado por defecto porque una fila por UUID de mob resulta ruido.

### Cómo termina una ronda

*puntuación*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `ends.atScore` | int | `0` | La partida termina en el momento en que un bando alcanza esto. 0 nunca termina por puntuación |
| `ends.afterMinutes` | int | `0` | La partida termina pasados estos minutos. 0 nunca termina por tiempo |
| `ends.afterRounds` | int | `0` | Para un objetivo al que otro concede con `awardsTo`: la partida termina cuando se han concedido en total tantas rondas, las haya ganado quien sea. 0 nunca termina por rondas |
| `ends.lastStanding` | booleano | `false` | La ronda termina cuando solo queda en pie un bando. Los bandos en juego son los que tienen un jugador o un mob vivo cuando se abre la ronda, dos como mínimo; un jugador que muere queda fuera, de vuelta como espectador hasta que acabe la ronda, y un bando cuyos jugadores están todos fuera o ausentes y cuyos mobs están todos muertos ha caído. El bando que queda en pie se lleva la ronda, y `awardsTo` la registra para ese bando sea cual sea la puntuación. Con `resets` y `opens.by: leader` la partida vuelve entonces al vestíbulo. El `standIn` de un bando no se invoca de nuevo mientras se juega una ronda así |
| `ends.outSays` | texto | `You are out until the round ends` | Lo que se dice a un jugador eliminado. Vacío no dice nada |
| `ends.locksTeams` | booleano | `true` | Unirse a un bando mientras se juega una ronda espera hasta que acabe la ronda, de modo que nadie entra a mitad de una ronda puntuada |

`ends` termina la partida, ya sea en el momento en que un bando alcanza `atScore` o una vez pasados `afterMinutes`. La clasificación se muestra entonces, ordenada por el propio juego: como chat, o como tarjeta si `results` la pide. A un jugador sin este mod se le dice la misma clasificación en líneas de chat, así que nadie se queda sin resultado. Con `resets`, ese final es el de una ronda: la clasificación permanece durante `intermissionSeconds` mientras una cuenta atrás se muestra en la barra de acción, el mapa se reinicia al estado de bienvenida y la siguiente ronda se abre tras una cuenta de cinco segundos. `awardsTo` entrega la ronda al bando que iba en cabeza, en un objetivo que `carries` a través del reinicio. Un objetivo arrastrado puede terminar por sí solo -- `atScore` para un al mejor de, `afterRounds` para una cantidad fija -- y su clasificación se borra en el reinicio posterior, de modo que se abre una partida nueva.

### Entre rondas

*puntuación*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `ends.resets` | booleano | `false` | Al terminar la ronda se reinicia el mapa, como describen `resetSays` y los demás ajustes de reinicio, y luego se abre una ronda nueva |
| `ends.intermissionSeconds` | int | `10` | Cuánto permanece la clasificación entre el final y el reinicio |
| `ends.intermissionSays` | texto | `Round cooldown {seconds}` | Se muestra en la barra de acción cada segundo del intermedio tras terminar una ronda, con `{seconds}` contando hacia atrás hasta el reinicio. Vacío no muestra nada |
| `ends.startsSays` | texto | `Round starting in {seconds}` | Se muestra en la barra de acción durante la cuenta de cinco segundos que abre la siguiente ronda tras el reinicio, con `{seconds}` contando hacia atrás. Vacío no muestra nada |

### El vestíbulo

*puntuación*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `opens.by` | texto | `auto` | `auto` abre la siguiente ronda por sí solo, cinco segundos después del reinicio. `leader` mantiene la partida en un vestíbulo: tras el reinicio, y cuando el mundo se carga por primera vez, no se puntúa nada y no corre ningún reloj, los bandos pueden unirse y abandonarse libremente, y la ronda solo se abre cuando el líder de un bando, o un operador, ejecuta `/rdpl round start`, y no mientras alguien siga leyendo la introducción al mundo; entonces corre la cuenta de cinco segundos, se hacen los sorteos y cada bando se coloca en su `spawn`. Mientras el mundo espera, hasta que termina la cuenta de cinco segundos, los jugadores se quedan donde están y no pueden romper, colocar, usar, golpear ni soltar nada ni reciben daño, y se les muestra la línea de espera cuando lo intentan, y todo lo demás que está vivo permanece quieto: sin IA, sin movimiento. Los comandos siguen funcionando, así que se pueden unir bandos y empezar la ronda |
| `opens.says` | texto | `Waiting for {leader} to start the round` | Se muestra a media pantalla, como la bienvenida, a cada jugador que no lidera: cuando llegan al vestíbulo tras la introducción, una vez que se ha mostrado la bienvenida; cuando el vestíbulo se abre de nuevo tras una ronda; siempre que cambia, al llegar o irse un líder; y cuando intentan algo que el vestíbulo rechaza. `{leader}` son los líderes de todos los bandos, o `a leader` mientras nadie lidera. Vacío no muestra nada |
| `opens.leaderSays` | texto | `Type /rdpl round start` | Se muestra del mismo modo y en los mismos momentos a un jugador que lidera un bando, en lugar de `opens.says`. Vacío no muestra nada |
| `opens.lobby` | texto | ninguno | `x,y,z` en el Overworld, o `dimension:x,y,z` en otro mundo, como `-1:0,64,0`, donde todos esperan mientras el vestíbulo está activo: todos los jugadores, y todos los mobs vivos de un bando, se colocan en un anillo alrededor de ese punto, cada uno mirando a su centro, de modo que se quedan mirándose unos a otros. A cada uno se le da un arco tan ancho como él más dos bloques, de modo que ninguno se solapa con otro, y el anillo crece según llegan más; se vuelve a disponer siempre que alguien se une a él o lo deja. La altura es el suelo sobre el que están, buscado en un margen de tres bloques arriba o abajo. Los jugadores y los mobs pasan a ese mundo y vuelven directamente, sin construir ningún portal. Cuando se abre la ronda, los jugadores van al `spawn` de su bando, y un mob que sigue en pie vuelve a donde estaba, en su propio mundo |
| `opens.lobbyJoins` | booleano | `false` | Pone a un jugador que inicia sesión a mitad de ronda en el vestíbulo como espectador hasta que acabe la ronda, en lugar de donde cerró sesión. Requiere `opens.lobby` |
| `opens.joinsSays` | texto | `Round is in progress, you can join after it ends` | Lo que se les dice. Vacío no dice nada |

### Reiniciar una ronda

*puntuación*

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

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `reset.lead` | texto | `none` | Lo que hace `/rdpl round reset` por el líder de un bando mientras se juega una ronda. `now` termina la ronda al instante y reinicia el mapa; `vote` convoca una votación en su lugar; `none` no da al líder voz propia, así que el líder convoca una votación como cualquier otro jugador donde `players` lo permita. Un operador siempre reinicia al instante |
| `reset.players` | texto | `none` | `vote` deja que un jugador de cualquier bando convoque una votación con `/rdpl round reset`. `none` deja el reinicio en manos del líder |
| `reset.teams` | lista | vacío | Los bandos cuyos jugadores pueden convocar una votación. Vacío es todos los bandos |
| `reset.passPercent` | int | `51` | La proporción de votantes, de 1 a 100, que deben votar sí para que se reinicie la ronda. `51` es más de la mitad, `100` es todos |
| `reset.voteSeconds` | int | `30` | Cuánto dura una votación, cinco segundos como mínimo. Se cierra antes en cuanto el resultado es seguro |
| `reset.cooldownSeconds` | int | `60` | Cuánto tiempo tras una votación fallida hasta que se puede convocar otra. Un líder con `now` no se ve frenado por ello |
| `reset.leadSays` | texto | `{player} reset the round` | Se dice a todos cuando la ronda se reinicia al instante, siendo `{player}` quien la reinició. Vacío no dice nada |
| `reset.voteSays` | texto | `{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds` | Se dice a todos cuando se convoca una votación, siendo `{player}` quien la convocó. Vacío no dice nada |
| `reset.tallySays` | texto | `Reset the round? {yes} yes, {no} no, {seconds}` | Se muestra en la barra de acción cada segundo de una votación, con `{seconds}` contando hacia atrás. Vacío no muestra nada |
| `reset.passSays` | texto | `The vote passed, so the round is reset` | Se dice a todos cuando una votación se aprueba. Vacío no dice nada |
| `reset.failSays` | texto | `The vote failed, so the round goes on` | Se dice a todos cuando una votación fracasa. Vacío no dice nada |

Un reinicio corta la ronda por donde va. La clasificación se muestra bajo `The round was reset`, nadie recibe la ronda, el intermedio cuenta hacia atrás y el mapa se reinicia como si la ronda hubiera terminado con `ends.resets`, de vuelta al vestíbulo donde `opens.by` es `leader`. Funciona tanto si la ronda llegaría a terminar por sí sola como si no, pero no en el vestíbulo, ni durante la cuenta que abre una ronda, ni una vez que la ronda ha terminado y su reinicio está en camino; una votación que siga en curso entonces se descarta.

Todos los jugadores conectados de un bando votan, sea cual sea su bando, con `/rdpl round vote yes` o `no`, y pueden cambiar su voto mientras dura. Quien convoca la votación ha votado sí, y un jugador que no ha votado cuando se acaba el tiempo cuenta como no. En un pack con bandos, un jugador sin ninguno ni convoca ni vota; en un pack sin bandos, lo hacen todos los jugadores conectados. Se usa el primer archivo de puntuación cuyo `reset` permite reiniciar a alguien.

### Resultados

*puntuación*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `results.card` | booleano | `false` | Muestra la clasificación como una tarjeta en lugar de como chat |
| `results.title` | texto | el nombre y `results` | El encabezado de la tarjeta |
| `results.icon` | texto | vacío | Un ítem dibujado en la tarjeta, p. ej. `minecraft:tnt` |
| `results.image` | texto | vacío | Una imagen dibujada en la tarjeta en lugar de un ítem |
| `results.background` | texto | un gris pizarra oscuro | El color de fondo de la tarjeta |
| `results.seconds` | int | `8` | Cuánto permanece la tarjeta, al menos un segundo |

### Turnos

*puntuación*

```json
{
  "name": "duel",
  "displayName": "Duel",
  "turns": {
    "order": "lowestFirst",
    "seconds": 45,
    "gapSeconds": 3,
    "held": "frozen",
    "endsAtScore": 10,
    "cycles": 5,
    "mobTypes": ["minecraft:zombie"]
  }
}
```

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `turns.order` | texto | `fixed` | Quién juega cuándo, fijado al empezar cada vuelta. `fixed` mantiene el orden en que se vieron los bandos por primera vez, `random` baraja cada vuelta, `lowestFirst` empieza por la puntuación más baja en este objetivo y `lastWinner` empieza por el bando que ganó la última ronda, el resto en orden fijo |
| `turns.seconds` | int | `60` | Cuánto dura un turno, al menos un segundo |
| `turns.gapSeconds` | int | `3` | Una pausa entre turnos mientras todos los bandos esperan. 0 sigue sin pausa |
| `turns.held` | texto | `frozen` | Cómo espera un jugador su turno: `frozen` lo deja quieto y le impide golpear, picar, construir, usar o soltar, como hace el vestíbulo; `spectator` o `adventure` lo pone en ese modo de juego hasta su turno y luego le devuelve el suyo |
| `turns.endsAtScore` | int | `0` | Los turnos terminan, y la ronda con ellos, en cuanto un bando alcanza esta puntuación en el objetivo. 0 nunca termina por puntuación |
| `turns.cycles` | int | `0` | Los turnos terminan, y la ronda con ellos, tras este número de vueltas; una vuelta es un turno para cada bando en juego. 0 sigue hasta que otra cosa termine la ronda |
| `turns.mobTags` | lista | vacío | Etiquetas de marcador que meten en los turnos a un mob sin equipo, un grupo por etiqueta |
| `turns.mobTypes` | lista | vacío | IDs de entidad que meten en los turnos a un mob sin equipo, un grupo por ID |

`turns` hace que los bandos jueguen por turnos mientras dura una ronda. Cada equipo es un bando; sin equipos, cada jugador es un bando propio, y un mob de un equipo va con él. Mientras un bando tiene su turno, todos los demás esperan: sus jugadores como indique `held`, sus mobs congelados igual que los retiene el vestíbulo. Se salta un bando sin nadie en juego o con todos sus jugadores eliminados.

Cada turno se anuncia en el chat y su reloj cuenta atrás en la barra de acción. Al bando que tiene el turno se le avisa en el chat con un sonido cuando quedan 10 segundos y otra vez a los 3. `/rdplserver game pass`, o un clic derecho con un ítem que tenga `passesTurn`, termina un turno antes de tiempo: un jugador solo pasa el turno de su propio bando, y un bloque de comandos o la consola pasan el de quien esté jugando.

Terminar por `endsAtScore` o `cycles` termina la ronda como cualquier otro final: se muestra la clasificación, se aplican `awardsTo` y `tiebreak`, y `ends.resets` reinicia el mapa. Solo el primer archivo de puntuación con `turns` juega por turnos. Los textos son las claves `turn`, `turnclock`, `turnwarn`, `turnout`, `turnpass`, `turngap`, `notturn` y `noturns`, que el `says` de un archivo de dados puede cambiar.

## Asaltos

*modos de juego*

`<namespace>/raids/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan. Cada archivo es un asalto.

Un asalto es el que tiene el juego a partir de la 1.14, construido sobre las aldeas que la 1.12.2 ya mantiene. Comienza cuando un jugador con el efecto `omen` está dentro de una aldea: se le quita el efecto y aparece una barra de jefe para todos los jugadores a menos de `reach` del centro de la aldea. Tras `waveDelay` ticks llega la primera oleada en un anillo alrededor de la aldea y avanza hacia el centro, atacando jugadores, aldeanos y gólems de hierro por el camino. Los saqueadores nunca se hieren ni se fijan como objetivo entre sí, así que una flecha perdida o un golpe entre dos de ellos no hace nada. La barra muestra la salud que le queda a la oleada, y cuenta los saqueadores cuando quedan dos o menos. Cuando una oleada desaparece, la siguiente espera `waveDelay` ticks. Cuando desaparece la última oleada y nada ha vuelto en dos segundos, el asalto se gana; cuando todos los aldeanos han muerto o la propia aldea ha desaparecido tras llegar una oleada, se pierde. En cualquier caso la barra lo indica durante treinta segundos, y la función correspondiente se ejecuta como cada jugador al alcance.

Un asalto en curso se guarda con el mundo, y sus saqueadores retoman la marcha tras una recarga. Se detiene sin final en pacífico, tras `timeout` ticks, o cuando ningún punto alrededor de la aldea puede acoger una oleada. Una aldea solo cuenta una vez que un aldeano ha encontrado sus puertas, así que un asalto necesita una aldea que el juego haya detectado.

Mientras una oleada está sobre la aldea, sus aldeanos corren a refugiarse en la puerta más cercana que conoce la aldea y se quedan allí. Los saqueadores derriban las puertas de madera que encuentran en su camino para llegar a ellos, doce segundos por puerta, en dificultad normal y difícil mientras `mobGriefing` esté activado; las puertas de hierro resisten. Un bloque de tipo `bell` es una campana de aldea esté donde esté, y suena como describe [Campanas](#campanas); el `bell` del asalto nombra cualquier otro bloque que haya que hacer sonar como tal. Todas las campanas de la aldea suenan cuando llega una oleada, y un bloque nombrado suena también cuando un jugador lo usa: los aldeanos a menos de 48 bloques se esconden durante quince segundos y los saqueadores a menos de 48 bloques brillan durante tres. Nada genera una campana. Un pack que quiera una define el bloque, lo incluye en una estructura NBT y coloca esa estructura en la aldea, como parcela o como sustituto del pozo, de modo que la campana esté donde viven los aldeanos.

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

### El asalto

*asaltos*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `omen` | nombre de efecto | ninguno, obligatorio | El efecto que inicia el asalto cuando su portador está dentro de una aldea. Sirve cualquier efecto registrado, incluida una poción propia del pack |
| `name` | texto | `Raid` | El título de la barra de jefe |
| `color` | texto | `red` | El color de la barra: `pink`, `blue`, `red`, `green`, `yellow`, `purple` o `white` |
| `waves` | lista de oleadas | ninguno, obligatorio | Cada oleada es una lista de grupos, y las oleadas llegan en orden |
| `waveDelay` | int | `300` | Ticks antes de la primera oleada, y entre el final de una oleada y la siguiente |
| `spawnDistance` | int | `32` | A qué distancia del centro de la aldea llega una oleada. Los primeros intentos son al doble de esto, luego a esta distancia y luego dentro de la aldea |
| `reach` | int | `96` | Los jugadores a esta cantidad de bloques del centro o menos ven la barra, y la función final se ejecuta como ellos. Un saqueador que se aleje dieciséis bloques más allá deja el asalto |
| `timeout` | int | `48000` | Ticks tras los cuales un asalto sin terminar se detiene sin final. `0` nunca lo detiene |
| `sound` | nombre de sonido | ninguno | Se reproduce a cada jugador al alcance, desde el lado por el que llega la oleada, cuando llega cada oleada |
| `wins` | función | ninguno | Se ejecuta como cada jugador al alcance cuando se gana el asalto |
| `loses` | función | ninguno | Se ejecuta como cada jugador al alcance cuando se pierde el asalto |
| `bell` | nombre de bloque o lista | ninguno | Otros bloques que suenan como una campana, cuando un jugador los usa y siempre que llega una oleada. Un bloque de tipo `bell` suena sin necesidad de nombrarlo. Colócalo en la aldea mediante una estructura NBT, ya que nada lo genera |

### Un grupo

*asaltos*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `entity` | nombre de entidad | ninguno, obligatorio | Lo que viene. Una variante de entidad conserva todo su comportamiento y gana la marcha |
| `count` | int o `{ "min", "max" }` | `1` | Cuántos vienen |

---

## Tarjetas

*modos de juego*

`<namespace>/cards/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan. Cada archivo es una regla, y su id es `<namespace>:<file name>`. Una regla espera a un disparador, comprueba su `when` y muestra una tarjeta a su audiencia; también puede ejecutar una función. No hace falta nada en el cliente: un jugador sin el mod recibe una tarjeta de esquina como líneas de chat y una tarjeta central como título.

Cada mensaje que dice este mod por sí mismo es una regla integrada, listada más abajo. Un pack cambia una escribiendo un archivo con ese id, `rdpl/cards/<name>.json`, que no necesita disparador: lo que omita se queda como está hoy, y `{text}` representa el mensaje que habría dicho el mod. Un pack que no escribe ninguna ve todos los mensajes como antes.

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

El segundo archivo, guardado como `rdpl/cards/gate_blocked.json`, convierte la línea roja de la barra de acción que muestra un portal cerrado en una tarjeta con un icono y una segunda línea, y la muestra como máximo una vez cada treinta segundos.

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

El tercero saluda a un jugador en su primera entrada con una tarjeta central sin panel detrás, solo su texto y la sombra del texto, dibujados con la fuente propia del pack.

### Disparadores

*tarjetas*

| Disparador | Necesita | Se activa cuando |
| --- | --- | --- |
| `command` | nada | Se ejecuta `/rdplserver card <rule> [players]`. El comando omite `when`, `repeat` y `cooldown`, y sigue ejecutando `runs`. Cualquier regla puede mostrarse así, sea cual sea su disparador |
| `first_join` | nada | Un jugador entra en el mundo por primera vez |
| `dimension_enter` | `dimension` | Un jugador llega a esa dimensión |
| `biome_enter` | `biomes` | Un jugador entra en uno de esos biomas viniendo de otro sitio |
| `structure_enter` | `structures` | Un jugador entra en una de esas estructuras desde fuera de ella |
| `advancement` | `advancement` | Un jugador consigue ese progreso |
| `time_of_day` | `time` | El reloj del día pasa por ese tick, de `0` a `23999`, mientras hay jugadores en la dimensión. Un reloj cambiado con un comando o una cama no cuenta |
| `day` | nada, o `day` | Empieza un día nuevo en la dimensión; con `day`, solo ese día |
| `craft` | `item` | Un jugador fabrica ese ítem |
| `pickup` | `item` | Un jugador recoge ese ítem |
| `kill` | `entity` | Un jugador mata a esa entidad, o a la `count`-ésima de ellas |
| `respawn` | nada | Un jugador reaparece tras morir |
| `death` | nada | Un jugador muere |
| `y_level` | `below` o `above` | Un jugador baja por debajo o sube por encima de esa altura |
| `play_time` | `minutes` | El tiempo de un jugador en el mundo alcanza esa cantidad de minutos, contados desde que se cierra la introducción al mundo, o desde la entrada cuando no se le muestra ninguna introducción |
| `score` | `objective` | La puntuación de un jugador en ese objetivo alcanza `score` |

El bioma, la estructura, la altura, el tiempo de juego y la puntuación se comprueban una vez por segundo para cada jugador, y se activan con el cambio de fuera a dentro, nunca en la primera comprobación tras una entrada. Una regla `time_of_day` o `day` cuya audiencia no es `player` se activa una vez para la dimensión en lugar de una vez por cada jugador en ella.

Una tarjeta que se activa mientras un jugador aún tiene abierta la introducción al mundo espera y se muestra cuando la introducción se cierra, sea cual sea su disparador, incluido el `command`. Se descarta si el jugador se va antes.

### Ajustes de disparadores

*tarjetas*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `trigger` | texto | ninguno, obligatorio | Uno de los disparadores anteriores. Una regla integrada no lleva ninguno |
| `dimension` | texto | ninguno | Un id de dimensión como `-1`, o su nombre como `the_nether`. Para `dimension_enter` es aquella en la que se entra; para cualquier otro disparador limita la regla a los jugadores de esa dimensión |
| `biomes` | lista | ninguno | Nombres de bioma como `minecraft:desert`, o `#TYPE` para un tipo de bioma de Forge como `#SNOWY` |
| `structures` | lista | ninguno | `Village`, `Temple`, `Mansion`, `Monument`, `Mineshaft`, `Stronghold`, `Fortress` o `EndCity`, o el nombre de una estructura que un pack coloca mediante `structures`, que cuenta dentro de `radius` de donde se colocó |
| `radius` | int | `32` | Qué cercanía cuenta como estar dentro de una estructura propia de un pack |
| `advancement` | texto | ninguno | El id del progreso |
| `item` | texto | ninguno | El ítem, escrito como en el resto de un pack, como `minecraft:diamond_sword` |
| `entity` | texto | ninguno | El id de la entidad, como `minecraft:zombie` |
| `count` | int | `1` | Para `kill`: cuántas muertes hacen falta. La cuenta vuelve a empezar después de que se active la regla |
| `below`, `above` | int | ninguno | Para `y_level`: la altura por debajo o por encima de la cual ir |
| `time` | int | `0` | Para `time_of_day`: el tick del día |
| `day` | int | ninguno | Para `day`: el único día en que se activa. Sin él, todos los días |
| `minutes` | int | ninguno | Para `play_time` |
| `objective`, `score` | texto, int | ninguno, `1` | Para `score`: el objetivo y el valor que hay que alcanzar |
| `requires` | lista de ids de mod o espacios de nombres de pack | ninguno | El archivo se omite a menos que estén presentes todos |

### Cuándo

*tarjetas*

`when` contiene condiciones que deben cumplirse todas en el momento en que se activa el disparador.

| Ajuste | Tipo | Qué comprueba |
| --- | --- | --- |
| `biomes` | lista | El jugador está en uno de estos biomas, escritos como para el disparador |
| `timeFrom`, `timeTo` | int | El reloj del día está dentro de esta ventana, que puede pasar de medianoche, como `13000` a `1000` |
| `dayAtLeast` | int | El número de día es al menos este |
| `advancement` | texto | El jugador tiene este progreso |
| `gameMode` | texto | El jugador está en este modo de juego: `survival`, `creative`, `adventure` o `spectator` |
| `team` | texto | El jugador está en este equipo del marcador |
| `objective`, `scoreAtLeast` | texto, int | La puntuación del jugador en el objetivo es al menos esta |

### La tarjeta

*tarjetas*

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `title` | texto | ninguno | La primera línea, dibujada más grande en una tarjeta central |
| `lines` | lista | ninguno | Hasta dieciséis líneas. Una regla necesita un título o líneas, salvo una integrada. Se rellenan `{player}`, `{dim}`, `{biome}` y `{day}`; `{text}` es el mensaje integrado, y en una línea propia da todas las líneas de este |
| `style` | texto | `corner` | `corner` es la tarjeta en la esquina inferior derecha que muestra `saysCard`; `center` es una tarjeta en medio de la pantalla; `chat` son líneas de chat; `bar` es la barra de acción |
| `icon` | texto | `saysIcon` | Un ítem dibujado en una tarjeta de esquina. Vacío no dibuja ninguno |
| `color` | texto | `saysColor` | El color de fondo de la tarjeta en hexadecimal |
| `image` | texto | `saysImage` | Un PNG de los recursos de cliente del pack, estirado sobre la tarjeta como fondo |
| `background` | booleano | `saysBackground` | `false` elimina el panel, su borde y la franja de color; el texto conserva su sombra, y una `image` se sigue dibujando |
| `font` | texto | `saysFont` | La fuente en la que se dibuja el texto de la tarjeta, como `namespace:name`. Vacío usa la fuente de RDPL |
| `ticks` | int | `160` | Cuánto permanece la tarjeta, con el desvanecimiento incluido |
| `audience` | texto | `player` | Quién la ve: `player`, `everyone`, `dimension` (todos en la dimensión del jugador) o `team` (el equipo del marcador del jugador) |
| `repeat` | texto | `always` | `always`, `once_per_player`, `once_per_world` o `once_per_session` (de nuevo cuando el jugador vuelve a iniciar sesión) |
| `cooldown` | int | `0` | Segundos antes de que la regla se active de nuevo para el mismo jugador |
| `runs` | texto | ninguno | Una función que se ejecuta como el jugador cuando se activa la regla |

Una tarjeta de esquina va al chat cuando `saysCard` está desactivado. Lo que se ha mostrado a un jugador se guarda con el jugador, así que sobrevive a la muerte y a los cambios de dimensión; `once_per_world` se guarda con el mundo.

La fuente de RDPL, `resourcedatapackloader:rdpl`, es la predeterminada para todo el texto: tarjetas, mensajes de Says, las notas de bienvenida y de espera, la introducción al mundo y los propios menús, chat, HUD y descripciones emergentes del juego. Sus variantes en negrita y cursiva son `resourcedatapackloader:rdpl_bold` y `resourcedatapackloader:rdpl_italic`. Las letras de la mesa de encantamientos siguen siendo las del juego.

RDPL incluye estas fuentes y caracteres. El `font` de una tarjeta, nota o introducción puede nombrar una fuente de RDPL por su nombre corto o por su id completo:

| Nombre | Qué dibuja |
| --- | --- |
| `rdpl` (o `resourcedatapackloader:rdpl`) | La fuente de RDPL, con cirílico (U+0400 a U+04FF) y el alfabeto rúnico (U+16A0 a U+16F8) |
| `rdpl_runic` (o `resourcedatapackloader:rdpl_runic`) | Un cifrado de runas: las letras A a Z y a a z se dibujan como runas, y cualquier otro carácter se dibuja con la fuente de RDPL. Una pasada en negrita se dibuja en `rdpl_runic_bold` y una en cursiva en `rdpl_runic_italic` |
| Runas, U+16A0 a U+16F8 | Escritas como los propios caracteres rúnicos (ᚠ ᚢ ᚦ ᚨ ᚱ ᚲ), en cualquier texto que dibuje la fuente de RDPL, chat incluido; las pasadas en negrita y cursiva conservan su variante |

Una fuente de tarjeta es un PNG en `assets/<namespace>/textures/font/<name>.png`, una cuadrícula de 16 por 16 glifos dispuestos como en el propio `ascii.png` del juego, y la tarjeta se dimensiona según los anchos de glifo leídos de él. Un `<name>_cyrillic.png` a su lado, con la misma cuadrícula que contiene Unicode U+0400 a U+04FF, dibuja cirílico; sin él, el cirílico viene de las páginas propias del juego. Un `<name>_runes.png`, la misma cuadrícula con U+1600 a U+16FF, dibuja runas del mismo modo. Las versiones 1.20.1 y 1.21.1 leen en su lugar una definición de fuente en `assets/<namespace>/font/<name>.json`, cuyo proveedor `bitmap` puede apuntar al mismo PNG, de modo que un pack que incluye ambos archivos dibuja las mismas letras en las tres versiones. `minecraft:default` nombra la fuente del juego. Una fuente que ningún pack contiene recurre a la fuente del juego, con un aviso en `rdpl.log`.

Un pack cambia la fuente de RDPL incluyendo su propio `assets/resourcedatapackloader/textures/font/rdpl.png` (y `rdpl_bold.png`, `rdpl_italic.png` y sus hojas `_cyrillic.png` y `_runes.png`), que la sustituye en todas partes, incluido el texto del juego. Un pack que incluye `assets/minecraft/textures/font/ascii.png`, una copia de la de vanilla por ejemplo, da en cambio al texto propio del juego esa fuente, con el cirílico y las runas de las páginas del juego; el texto propio de RDPL conserva la fuente de RDPL salvo que `saysFont` sea `minecraft:default`.

Los títulos y líneas de las tarjetas, los mensajes de Says y las notas de bienvenida y de espera admiten las marcas en línea de la tabla en Introducción al mundo, Formato de texto: negrita, cursiva, negrita cursiva, tachado, código, enlaces y tramos rúnicos. Una pasada en negrita se dibuja con la variante `_bold` de la fuente y una en cursiva con su variante `_italic`; para una fuente sin esa variante, la pasada toma el estilo de negrita o cursiva del juego, y la tarjeta se dimensiona según las pasadas tal como se dibujan. Los jugadores sin el mod reciben las mismas marcas como formato de chat, y un tramo rúnico como sus letras normales.

### Reglas integradas

*tarjetas*

| Id | El mensaje | De dónde sale su texto |
| --- | --- | --- |
| `rdpl:gate_unlocked` | Un portal se abre | `unlockedMessage` en [Portales](#portales) |
| `rdpl:gate_blocked` | Un portal cerrado hace volver a un jugador, en la barra de acción | `blockedMessage` en [Portales](#portales) |
| `rdpl:team_joined` | Un jugador se une a un bando | el `displayName` del bando |
| `rdpl:team_lead` | El liderazgo de un bando llega a un jugador | `leadSays` en [Equipos](#equipos) |
| `rdpl:team_picked` | Un jugador es elegido para un bando | el `displayName` del bando |
| `rdpl:team_round_ended` | La ronda terminó, así que se mueve a un jugador a un bando | el `displayName` del bando |
| `rdpl:lobby_joins` | Un jugador que inicia sesión a mitad de ronda es enviado al vestíbulo | `opens.joinsSays` en [El vestíbulo](#el-vestíbulo) |
| `rdpl:lobby_note` | La línea del vestíbulo a media pantalla | `opens.says`, `opens.leaderSays` en [El vestíbulo](#el-vestíbulo) |
| `rdpl:scoring_results` | La clasificación al final de una ronda, para cada jugador | `results.card`, `results.title`, `results.icon`, `results.image`, `results.background`, `results.seconds` en [Resultados](#resultados) |
| `rdpl:scoring_out` | Un jugador eliminado | `ends.outSays` en [Cómo termina una ronda](#cómo-termina-una-ronda) |
| `rdpl:reset_lead` | El líder reinicia la ronda | `reset.leadSays` en [Reiniciar una ronda](#reiniciar-una-ronda) |
| `rdpl:reset_vote` | Se convoca una votación de reinicio | `reset.voteSays` |
| `rdpl:reset_pass` | La votación se aprueba | `reset.passSays` |
| `rdpl:reset_fail` | La votación fracasa | `reset.failSays` |
| `rdpl:anvil_waits` | El trabajo de un yunque espera a un progreso | [Trabajo en el yunque](#trabajo-en-el-yunque) |
| `rdpl:threat` | Cambia la franja de amenaza de un jugador | `threatSays` |
| `rdpl:prospect` | Cada línea que informa un hallazgo de prospección | el hallazgo |
| `rdpl:prospect_none` | La prospección no encontró nada | el archivo de idioma |
| `rdpl:board_result` | Una partida de tablero termina | el archivo de idioma |
| `rdpl:pregen_ended` | La pregeneración termina o se detiene | `pregenFinishedSays`, `pregenStoppedSays` en [Pregeneración](#pregeneración) |
| `rdpl:pregen_running` | La línea de progreso que ve un jugador al entrar durante la pregeneración | `pregenRunningSays` |

`welcomeSays` no es una regla y conserva su logotipo; una regla `first_join` o `dimension_enter` se suma a él. Las cuentas atrás y los recuentos de una ronda en la barra de acción se quedan como los dejan sus ajustes.

## Dados y mazos

*modos de juego*

`<namespace>/dice/*.json`

El nombre del archivo lo eliges tú, y varios archivos se suman. Un archivo nombra dados cuyas caras llevan pesos, mazos de cartas de los que se roba sin devolver, quién oye una tirada por defecto y la redacción de los resultados. Las tiradas se hacen con [`/rdplserver game`](#juegos) y con cualquier ítem que tenga [`rolls`](#claves-de-archivo-de-ítems).

```json
{
  "audience": "all",
  "dice": {
    "fate": { "plus": 1, "blank": 2, "minus": 1 }
  },
  "decks": {
    "mobs": ["Creeper", "Zombie", "Skeleton", "Enderman"]
  },
  "says": {
    "coin": "{player} tosses the old coin: {result}"
  }
}
```

| Clave | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `audience` | texto | `all` | Quién oye una tirada que no nombra la suya: `self`, `team`, `all`, `radius <bloques>` o `silent`. Vale el primer pack que lo fija; uno posterior queda en el registro |
| `dice` | objeto | vacío | Nombre del dado a un objeto de cara y peso. Una cara de peso 2 sale el doble que una de peso 1. Los pesos son números enteros desde 1 |
| `decks` | objeto | vacío | Nombre del mazo a su lista de cartas, o a un objeto `{ "cards": [...], "reshuffle": false }`. `reshuffle` es `true` si no se indica: robar de un mazo vacío baraja todas las cartas y roba. Con `false` el mazo sigue vacío hasta `game deck shuffle` |
| `says` | objeto | vacío | Clave de redacción a texto, que sustituye la redacción propia del mod en todos los idiomas. Las claves y sus marcadores están abajo |

Un nombre de dado o de mazo pertenece al primer pack que lo carga. Otro pack con el mismo nombre, o con el nombre `coin`, queda fuera con un error en el registro. Un dado del pack se tira con `game die <name>` y muestra su cara; guardado como puntuación, cuenta como el puesto de la cara en el archivo, empezando en 1.

Un mazo es una pila que se va agotando. `game deck draw <name>` roba una carta al azar de lo que queda, y nada vuelve hasta que el mazo vacío se baraja solo en la siguiente carta o `game deck shuffle <name>` devuelve todas las cartas. La pila se guarda con el mundo, así que un reinicio no la baraja.

Cada tirada usa el azar propio del mundo y se escribe en el registro con quién la hizo, qué hizo y qué salió. `game last` muestra las más recientes. Los resultados salen como líneas de chat normales construidas en el servidor, así que también los lee un jugador sin el mod. La notación de dados se lee como cantidad, `d`, caras y un modificador opcional: `3d8-2` son tres dados de ocho caras sumados, menos 2, y `d20` es un dado de veinte caras. El chat y el registro escriben la tirada con palabras, como «Boss tira 3 dados de ocho caras, menos 2: [2, 6, 7] = 13», y `{dice}` contiene esa redacción.

| Clave de redacción | Marcadores |
| --- | --- |
| `coin`, `pickplayer`, `pickteam` | `{player}`, `{result}` |
| `heads`, `tails`, `lastnone`, `nobody`, `notallowed`, `usage` | ninguno |
| `die` | `{player}`, `{dice}`, `{sides}`, `{result}` |
| `packdie` | `{player}`, `{die}`, `{result}` |
| `dice` | `{player}`, `{dice}`, `{rolls}`, `{result}` |
| `advantage`, `disadvantage` | `{player}`, `{dice}`, `{first}`, `{second}`, `{result}` |
| `pickmember` | `{player}`, `{team}`, `{result}` |
| `draw`, `reshuffled` | `{player}`, `{deck}`, `{result}`, `{left}` |
| `shuffle` | `{player}`, `{deck}`, `{left}` |
| `left`, `empty`, `nodeck` | `{deck}`, y `{left}` en `left` |
| `teamroll` | `{member}`, `{dice}`, `{result}` |
| `teamrollwin` | `{result}`, `{score}` |
| `tiebreak` | `{objective}`, `{sides}`, `{result}` |
| `notie`, `noobjective` | `{objective}` |
| `turn`, `turnclock`, `turnwarn` | `{group}`, `{seconds}` |
| `turnout`, `turnpass` | `{group}` |
| `turngap` | `{seconds}` |
| `notturn`, `noturns` | ninguno |
| `badsides`, `badroll`, `badaudience`, `nodie`, `noteam` | `{sides}`, `{roll}`, `{audience}`, `{name}`, `{team}` por orden |

La redacción propia del mod está en sus archivos de idioma como `rdpl.game.<key>`, así que un paquete de recursos también puede cambiarla idioma por idioma.

## Juegos de tablero

*modos de juego*

`<namespace>/games/*.json`

Un archivo es un juego de tablero, con el nombre de su archivo. Fija el tablero, los dos bandos, las piezas y cómo se mueven, la posición inicial y lo que paga un resultado. `game board start <game> <board>` monta un tablero donde está quien lo envía, o en la posición indicada: las casillas se colocan un bloque más abajo, las columnas van hacia el este y las filas hacia el sur, y cada pieza es una criatura que elige el pack, quieta, muda e invulnerable, con el nombre de su bando. Una pieza capturada queda al lado del tablero.

```json
{
  "name": "Chess",
  "board": { "files": 8, "ranks": 8, "light": "minecraft:quartz_block", "dark": "minecraft:coal_block" },
  "sides": [
    { "name": "White", "color": "white" },
    { "name": "Black", "color": "dark_gray" }
  ],
  "pieces": {
    "pawn": {
      "letter": "p", "value": 1, "mobs": ["minecraft:snowman", "minecraft:zombie"],
      "moves": [
        { "steps": [[0, 1]], "captures": "never", "firstRange": 2 },
        { "steps": [[-1, 1], [1, 1]], "captures": "only" }
      ],
      "enPassant": true, "promotes": ["queen", "rook", "bishop", "knight"]
    },
    "knight": {
      "letter": "n", "value": 3, "mobs": ["minecraft:horse", "minecraft:skeleton_horse"],
      "moves": [{ "steps": [[1, 2], [2, 1], [2, -1], [1, -2], [-1, -2], [-2, -1], [-2, 1], [-1, 2]] }]
    },
    "bishop": {
      "letter": "b", "value": 3, "mobs": ["minecraft:villager", "minecraft:witch"],
      "moves": [{ "steps": [[1, 1], [1, -1], [-1, -1], [-1, 1]], "slides": true }]
    },
    "rook": {
      "letter": "r", "value": 5, "mobs": ["minecraft:villager_golem", "minecraft:wither_skeleton"],
      "moves": [{ "steps": [[1, 0], [0, 1], [-1, 0], [0, -1]], "slides": true }]
    },
    "queen": {
      "letter": "q", "value": 9, "mobs": ["minecraft:polar_bear", "minecraft:blaze"],
      "moves": [{ "steps": [[1, 0], [0, 1], [-1, 0], [0, -1], [1, 1], [1, -1], [-1, -1], [-1, 1]], "slides": true }]
    },
    "king": {
      "letter": "k", "value": 0, "royal": true, "castles": "rook", "mobs": ["minecraft:evocation_illager", "minecraft:vindication_illager"],
      "moves": [{ "steps": [[1, 0], [0, 1], [-1, 0], [0, -1], [1, 1], [1, -1], [-1, -1], [-1, 1]] }]
    }
  },
  "setup": ["RNBQKBNR", "PPPPPPPP", "........", "........", "........", "........", "pppppppp", "rnbqkbnr"],
  "rules": { "quietDraw": 100, "repeatDraw": 3 },
  "clock": { "minutes": 10, "addSeconds": 2 },
  "ai": 2,
  "result": { "objective": "boardwins", "win": 3, "draw": 1, "loss": -1 }
}
```

| Clave | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `name` | texto | el nombre del archivo | El nombre que se ve en el chat y en el resultado |
| `board` | objeto | 8 por 8 | `files` y `ranks`, de 2 a 16 cada uno, y los estados de bloque `light` y `dark` que se ponen bajo las casillas. Si faltan, el suelo queda como está |
| `sides` | lista | `White`, `Black` | Dos objetos de `name` y `color`, un color de chat. El primer bando mueve primero y se coloca con las letras mayúsculas |
| `pieces` | objeto | ninguno | Nombre de pieza a un objeto con las claves de pieza de abajo |
| `setup` | lista | ninguno | Un texto por fila, la fila 1 primero. La letra de una pieza la coloca, en mayúscula para el primer bando y en minúscula para el segundo, y `.` deja la casilla vacía |
| `rules` | objeto | ninguno | `mustCapture`, `chainCaptures`, `quietDraw` y `repeatDraw`, abajo |
| `clock` | objeto | ninguno | `minutes` para cada bando y `addSeconds` que se suman tras cada jugada. Pierde el bando al que se le acaba el tiempo |
| `ai` | número | `2` | El nivel del ordenador, de 1 a 4, para un bando que nadie tiene |
| `result` | objeto | ninguno | El `objective` que se paga al acabar la partida, con los puntos `win`, `draw` y `loss`, 1, 0 y 0 si faltan |

| Clave de pieza | Tipo | Qué hace |
| --- | --- | --- |
| `letter` | texto | La letra que se usa en `setup` y en `game board show` |
| `value` | número | Lo que vale para el ordenador |
| `mobs` | lista | La criatura de cada bando, el primer bando primero. `mob` da una para ambos |
| `moves` | lista | Pasos como objetos: `steps`, una lista de desplazamientos `[file, rank]` vistos desde el lado propio del bando; `slides`, para seguir hasta que algo lo impida; `captures`, `both` (por defecto), `never`, `only` o `hop`, que salta una pieza del otro bando a la casilla vacía de detrás y la captura; y `firstRange`, cuántos pasos puede dar en su primera jugada |
| `royal` | verdadero o falso | El bando pierde cuando esta pieza recibe jaque mate, y ninguna jugada puede dejarla atacada. Sin pieza real, pierde el bando que se queda sin jugadas |
| `enPassant` | verdadero o falso | Puede capturar una pieza que acaba de pasar dos casillas a su lado |
| `castles` | texto | Una pieza compañera: esta pieza se mueve dos casillas hacia una compañera sin mover, que salta por encima de ella |
| `promotes` | lista | En qué puede convertirse en la última fila. Se usa la primera salvo que la jugada nombre otra |

| Regla | Qué hace |
| --- | --- |
| `mustCapture` | Un bando que puede capturar debe capturar |
| `chainCaptures` | Tras un salto que captura, la misma pieza sigue capturando mientras pueda |
| `quietDraw` | Cuántas jugadas seguidas, contando ambos bandos, pueden pasar sin una captura ni una jugada de una pieza que puede promocionar antes de que la partida quede en tablas. 0 nunca da tablas |
| `repeatDraw` | La partida queda en tablas cuando la misma posición, con el mismo bando por mover, aparece este número de veces; 3 es la triple repetición del ajedrez. Solo cuentan las posiciones desde la última captura o jugada de una pieza que puede promocionar. 0 nunca da tablas |

Un bando pertenece al primer jugador que hace clic derecho en una de sus piezas, o a su equipo del marcador si está en uno, de modo que cualquier miembro puede jugarlo después. Un bando que nadie tiene lo juega el ordenador en cuanto se toma el otro bando, o enseguida cuando `game board ai` le fija un nivel; el ordenador piensa fuera del hilo del servidor, y su jugada se hace en él. Haz clic derecho en una pieza para ver adónde puede ir, y luego clic derecho en una casilla o en una pieza del otro bando para moverla allí. `game board move` hace lo mismo con nombres de casilla, como `e2 e4`, con un nombre de pieza al final para una promoción. Cuando una jugada promociona y `promotes` nombra más de una pieza, el jugador elige entre opciones del chat en las que se puede hacer clic; si no elige en 10 segundos o hace clic en el tablero, se juega la primera. El ordenador elige la que considera mejor.

`game board resign` abandona la partida. `game board draw` ofrece tablas, que el otro bando acepta con el mismo comando; el ordenador las rechaza cuando va mejor. `game board takeback` pide deshacer la última jugada de quien lo pide, y el otro bando acepta con el mismo comando; contra el ordenador se deshace al momento.

Un tablero se guarda en los datos guardados del mundo como su lista de jugadas. Cuando el mundo carga o el pack se recarga, la posición se reproduce desde esa lista, las piezas se vuelven a colocar según ella y se quita cualquier pieza anterior del tablero.

Al final, `result` paga al titular de cada bando en su objetivo, como fila de jugador o de equipo. El resultado llega a los jugadores cerca del tablero y a quienes tienen un bando, como la tarjeta `rdpl:board_result` cuando un archivo de tarjetas fija ese id y como una línea de chat si no. La redacción está en los archivos de idioma del mod como `rdpl.game.board.<key>`.

---

# Control

## La capa de control

*control*

Todo lo que detiene o cambia la generación está agrupado, y cada grupo tiene una clave en la categoría `control` de la configuración con tres valores:

| Valor | Qué significa |
| --- | --- |
| `default` | Decide el pack. Los valores de la configuración son el respaldo |
| `global` | Gana la configuración. Se ignoran las secciones del pack |
| `off` | El grupo se desactiva por completo y ningún pack puede activarlo |

Los grupos son `ores`, `biomes`, `generators`, `structures`, `spawning`, `bedrock`, `voidWorld`, `recipes`, `terrain`, `entities`, `chunks`, `commands` y `server`.

Los ajustes se resuelven **bioma → plantilla de mundo → configuración**. El bloque `settings` de una plantilla de mundo usa los mismos nombres de clave que la configuración, así que un pack los fija igual que lo harías tú:

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

Con el control de un grupo en `default` estos ganan, en `global` se ignoran, y en `off` todo el grupo no hace nada digan lo que digan los packs.

## Qué hace cada grupo

*control*

### Minerales

*qué hace cada grupo*

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

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `blockOres` | booleano | `false` | Impide que todos los mods y Minecraft generen minerales, salvo los mods nombrados en `oreWhitelist`. Solo se puede alcanzar la generación que pasa por el evento de generación de minerales de Forge, que es la de Minecraft y la de la mayoría de los mods, pero no la de todos |
| `oreWhitelist` | lista de ids de mod | `["minecraft"]` | Los mods a los que se sigue permitiendo generar minerales mientras `blockOres` está activado |
| `oreTypes` | lista de tipos de mineral | ninguno | A qué tipos de mineral se aplica el bloqueo, escritos como nombres de Forge, `COAL`, `IRON`. Vacío significa todos los tipos |
| `oreTypesAreBlacklist` | booleano | `true` | Activado, los tipos de `oreTypes` son los bloqueados. Desactivado, solo se generan esos tipos |
| `blockOreDimensions` | lista de ints | ninguno | Las dimensiones a las que se aplica el bloqueo de minerales, vacío significando todas. Una dimensión fuera del alcance no se toca en absoluto, así que los minerales de otro mod se generan allí mientras el Overworld sigue bloqueado |
| `blockOreDimensionsAreBlacklist` | booleano | `false` | Activado, las dimensiones listadas son las que se dejan en paz |
| `prospectItems` | lista de `item=entries` | ninguno | Ítems que prospectan entradas de worldgen de forma `vein` cuando un jugador agachado rompe un bloque con uno en la mano. La forma se explica en el párrafo siguiente |
| `prospectItemsAreBlacklist` | booleano | `false` | Activado, la lista de cada ítem son las entradas que no lee |
| `prospectWear` | int | `2` | Cuántas veces el desgaste normal cuesta a la herramienta una rotura de prospección. `2`, el doble, es lo mínimo permitido, y un ítem sin durabilidad no paga nada |
| `prospectSlow` | int | `2` | Cuántas veces más tarda un jugador agachado con un ítem etiquetado en romper un bloque. `1` es velocidad normal |
| `prospectDrops` | booleano | `false` | Activado, un bloque roto en modo prospección sigue soltando drops y dando experiencia. Desactivado, la muestra se consume |

**La lectura.** Una entrada de `prospectItems` se escribe `item=entry|entry[,radius in chunks]`, o `item=*[,radius]` para cada entrada de veta, con el radio por defecto en 8. A un jugador agachado que rompe un bloque con uno de estos ítems en la mano se le dice, por cada entrada que lee, `Possible hit on <ore> <direction> of this location, <deeper down | higher up | at about this depth>` — uno de ocho puntos cardinales desde el bloque roto hasta la veta sembrada más cercana, y nunca una posición; `at this location` cuando el bloque ya está dentro del alcance de la veta, y `No sign of anything here` cuando no hay nada sembrado en el radio. El mineral se nombra con el `prospectAs` de la entrada, o si no con el nombre de su archivo. La lectura repite las mismas tiradas que hace la generación, así que acierta sobre terreno que aún no se ha creado, y un ítem etiquetado dice en su descripción emergente qué prospecta.

### Biomas

*qué hace cada grupo*

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

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `blockBiomes` | booleano | `false` | Impide que se genere cualquier bioma salvo los de los mods de `biomeWhitelist`. Los biomas bloqueados se sustituyen en el mapa de biomas terminado, la única forma de alcanzar océanos, islas de hongos, variantes de mesa, jungla, colinas y costas. Bloquea todos y el Overworld se convierte por sí solo en un mundo vacío |
| `biomeWhitelist` | lista de ids de mod | `minecraft` | Los mods cuyos biomas se siguen generando mientras `blockBiomes` está activado. Un bioma de un pack usa el espacio de nombres del pack |
| `biomeNames` | lista de nombres de bioma | ninguno | Biomas a los que esto se aplica por nombre, sea quien sea su propietario y diga lo que diga la lista blanca. Un nombre amigable como `Birch Forest` o un nombre de registro |
| `biomeNamesAreBlacklist` | booleano | `true` | Activado, los nombres de `biomeNames` se bloquean. Desactivado, solo se generan esos nombres |
| `blockBiomeDimensions` | lista de ints | `0`, el Overworld | Las dimensiones a las que se aplica el bloqueo de biomas. Vacío significa todas |
| `blockBiomeDimensionsAreBlacklist` | booleano | `false` | Activado, el bloqueo omite las dimensiones listadas. Desactivado, se aplica solo a ellas |

`blockBiomes` y `biomeWhitelist` funcionan por mod, y `biomeNames` con `biomeNamesAreBlacklist` por nombre. Los biomas bloqueados se sustituyen en el mapa de biomas terminado, que es la única forma de alcanzar océanos, islas de hongos, variantes de mesa, jungla, colinas y costas, que se eligen fuera de las listas que un mod puede editar. Bloquea todos los biomas y el Overworld se convierte por sí solo en un mundo vacío. `blockBiomeDimensions` limita todo ello a ciertas dimensiones, vacío significando todas, y `blockBiomeDimensionsAreBlacklist` convierte esa lista en una exclusión.

### Generadores

*qué hace cada grupo*

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

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `blockWorldGenerators` | booleano | `false` | Impide que otros mods generen a través de sus propios generadores de mundo, que es como los mods añaden lo que los eventos de Forge nunca ven: islas de slimes, cristales de cueva y cosas así. La generación de los packs de este mod nunca se bloquea |
| `generatorWhitelist` | lista de ids de mod | `minecraft` | Los mods que aún pueden ejecutar sus propios generadores |
| `blockedGenerators` | lista de ids de mod o fragmentos de nombre de clase | ninguno | Generadores concretos bloqueados sin más, diga lo que diga la lista blanca |
| `blockGeneratorDimensions` | lista de enteros | `0`, el Mundo principal | Las dimensiones a las que se aplica. Vacía significa todas |
| `blockGeneratorDimensionsAreBlacklist` | booleano | `false` | Activado, el bloqueo omite las dimensiones indicadas. Desactivado, se aplica solo a ellas |
| `generatorTypes` | lista de tipos | ninguno | Bloquea por lo que produce un generador en lugar de por quién lo posee: `ores`, `structures`, `flora`, `lakes`, `terrain`, o `unknown` para los que no coincidieron con nada |
| `generatorTypesAreBlacklist` | booleano | `true` | Activado, se bloquean los tipos indicados. Desactivado, solo se generan esos tipos |
| `generatorTypeMap` | lista de `pattern=type` | ninguno | Tipos para generadores cuyo nombre de clase no los describe, siendo el patrón un id de mod o parte del nombre de clase de un generador. Las entradas asignadas se comprueban antes que las palabras integradas, así que también corrigen un generador que las palabras interpretan mal |
| `logBlockedGenerators` | booleano | `true` | Registra cada generador con el tipo que se le asignó la primera vez que se bloquea. `/rdplserver generators` muestra los totales acumulados por mod y tipo |

`blockWorldGenerators` impide que otros mods generen a través de sus propios generadores de mundo, que es como los mods añaden lo que los eventos de Forge nunca ven: islas de slimes, cristales de cueva y cosas así. `generatorWhitelist` mantiene los mods indicados, `blockedGenerators` nombra generadores concretos, y la generación de los packs de este mod nunca se bloquea. `blockGeneratorDimensions` lo limita a ciertas dimensiones, y `blockGeneratorDimensionsAreBlacklist` invierte la lista.

`generatorTypes` bloquea por lo que produce un generador en lugar de por el mod que lo posee: `ores`, `structures`, `flora`, `lakes`, `terrain`, o `unknown` para los que no coincidieron con nada. `generatorTypesAreBlacklist` decide el sentido: activado, se bloquean los tipos indicados; desactivado, solo se generan los tipos indicados. Un tipo bloquea diga lo que diga la lista blanca, igual que `oreTypes`, de modo que puedes impedir que cualquier mod añada minerales sin tocar sus mazmorras ni sus árboles.

El tipo sale del nombre de clase del generador, comparado con una lista integrada de palabras por tipo. Eso interpreta bien la mayoría de los mods: `NetherOreGenerator` es de minerales, `SlimeIslandGenerator` es de estructuras, pero un generador con un nombre que no alude a nada en particular, como el `SimpleGenHandler` de ProjectRed o el `DEWorldGenHandler` de Draconic Evolution, resulta ser `unknown`. `generatorTypeMap` lo corrige a mano, un `pattern=type` por línea, donde el patrón es un id de mod o parte del nombre de clase de un generador:

```
mrtjpcore=ores
deworldgenhandler=structures
```

Las entradas asignadas se comprueban antes que las palabras integradas, así que también corrigen un generador que las palabras interpretan mal. Activa `logBlockedGenerators` y cada generador se registra con el tipo que se le asignó la primera vez que se bloquea, y `/rdplserver generators` muestra los totales acumulados por mod y tipo.

### Sustituciones

*qué hace cada grupo*

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

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `blockReplacements` | lista de `block=block` | ninguno | Bloques que se cambian en chunks que ya existen, con un meta opcional a cada lado. Cada chunk se procesa una sola vez al cargarse y se marca en sus propios datos, así que nunca se hace dos veces |
| `blockReplacementDimensions` | lista de enteros | ninguno | Las dimensiones a las que se aplica. Vacía significa todas |
| `blockReplacementDimensionsAreBlacklist` | booleano | `false` | Activado, la sustitución omite las dimensiones indicadas. Desactivado, se aplica solo a ellas |
| `blockReplacementMinHeight` | int | `0` | La y más baja que examina |
| `blockReplacementMaxHeight` | int | `255` | La y más alta que examina |
| `blockReplacementKey` | cadena | `0000` | Cámbiala y todos los chunks pasan de nuevo por la sustitución |

`blockReplacements` cambia bloques en chunks que ya existen, un `block=block` por línea, con un meta opcional a cada lado:

```
bigreactors:oreyellorite=minecraft:stone
mekanism:oreblock:0=minecraft:stone
tconstruct:ore:0=minecraft:netherrack
```

Cada chunk se procesa una sola vez, al cargarse desde el disco, y se marca en los datos del propio chunk para que nunca se haga dos veces. Un chunk que se genera por primera vez se limpia la siguiente vez que se carga y no de inmediato, porque los chunks vecinos siguen escribiendo en él mientras se genera. Un chunk en el borde de la tierra explorada se limpia pero no se marca, de modo que se vuelve a limpiar cuando existe el terreno que lo rodea. `blockReplacementDimensions` y `blockReplacementDimensionsAreBlacklist` eligen dónde, `blockReplacementMinHeight` y `blockReplacementMaxHeight` eligen la franja del mundo que se examina, y `blockReplacementKey` es una cadena que cambias para que todos los chunks pasen por el proceso otra vez. Se ejecuta esté `retrogen` activado o no, ya que un mundo que necesita limpieza suele ser uno al que no quieres añadir vetas nuevas. Solo cambia bloques: algo que un mod generó como estructura no puede retirarse así, porque el terreno al que sustituyó nunca se registró.

### Aldeas

*qué hace cada grupo*

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

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villageBlocks` | lista de `original=replacement` | ninguno | Los bloques con los que se construyen las piezas de aldea, aplicados después de que todos los demás mods hayan dicho lo suyo. Un par puede llevar una probabilidad y una condición, y entonces es una regla; los campos están en la tabla de más abajo |
| `villagePieces` | lista de nombres de pieza | ninguno | Piezas de aldea vanilla nombradas una por línea: `house1`, `house2`, `house3`, `house4garden`, `church`, `woodhut`, `hall`, `field1`, `field2`. Una parcela de pack se nombra por su propia plantilla, y también las piezas que añaden otros mods |
| `villagePiecesAreBlacklist` | booleano | `true` | Activado, se bloquean las piezas indicadas. Desactivado, solo se generan esas piezas, y una lista blanca solo retira las piezas propias de vanilla |
| `villagePlotsLeast` | int | `0` | Las menos parcelas construidas con las que se conforma una aldea, contando casas, campos y parcelas de pack pero nunca caminos, antorchas ni el pozo. Una aldea que se traza más pequeña se vuelve a hacer crecer unas cuantas veces y gana el trazado más grande. `0` mantiene vanilla |
| `villagePlotsBackRow` | booleano | `true` | Una vez que la aldea ha crecido, una segunda pasada sienta una parcela justo detrás de cada parcela que da a una calle, girada para mirar hacia ella, con la misma tirada y la misma prueba de espacio, de modo que el interior de una manzana entre dos calles se construye en lugar de quedar vacío |
| `villagePlotsMost` | int | `0` | Las más que puede tener; al llegar al máximo deja de crecer por completo, sin más edificios ni más caminos. `0` mantiene vanilla |
| `villageTieStreets` | booleano | `true` | Activado, un distrito que no puede hacer crecer sus calles hasta la aldea ya existente tiende una calle de enlace recta hasta la calle más cercana con la que se alinea. Desactivado, ese distrito se deshace |
| `villageCitySpacing` | entero, de 0 a 256 | `0` | `1`, con `terrainAdaptation` activado, hace de cada distrito del mundo una ciudad, la ciudad continua. En 1.12.2 solo `1` cambia algo: `0` y cualquier otro número siguen sembrando las aldeas según `structureSpacing` |
| `villageBlockSizes` | lista de `size=weight` | ninguno | Cuánto de profundas son las manzanas entre las calles paralelas de una ciudad, tirado una vez por distrito a partir de la posición de su plaza. Vacía, dimensiona todas las manzanas según la parcela más grande que incluye el pack |
| `villageLayout` | cadena | vacía | Nombra un [mapa de trazado de ciudad](#mapas-de-trazado-de-ciudades) que dispone la aldea a partir de un plano de calles dibujado en lugar de hacerla crecer |

Las aldeas usan las mismas listas `structure=value` que cualquier otra estructura, bajo el nombre `villages`, de modo que `structureSpacing`, `structureMinDistanceFromSpawn`, `structureBiomes` y `structureBiomesAreBlacklist` las alcanzan todas. Una lista `structureBiomes` que no sea lista negra también añade cualquier bioma nombrado que la lista propia de la estructura nunca contuvo, de modo que las aldeas pueden enviarse a las montañas; nómbralos por su nombre de registro para eso, ya que solo los nombres de registro pueden añadir. Su separación tiene un mínimo de 9, porque vanilla le resta 8. `villagePieces` pertenece al mismo grupo, así que un solo interruptor cubre todo lo relativo a dónde van las aldeas y con qué se construyen, mientras que el grupo `villages` cubre únicamente las parcelas que añade un pack.

`villageBlocks` sustituye los bloques con los que se construye una aldea, como pares `original=replacement`: `minecraft:cobblestone=mypack:ruby_brick`. Se aplica después de que todos los demás mods hayan dicho lo suyo, así que un pack siempre gana, incluso frente a mods que cambian los materiales de aldea por bioma. Ambos lados aceptan un nombre de bloque simple o un nombre con estados. Los caminos se nombran aparte con `villagePathBlock` y sus hermanos.

Un par puede llevar detrás una probabilidad y una condición, escritas como campos separados por comas, y entonces es una regla y no un simple cambio. `minecraft:cobblestone=minecraft:mossy_cobblestone,20` desgasta una quinta parte de los adoquines que coloca una aldea; `minecraft:planks=minecraft:sandstone,100,under=minecraft:sand` cambia el suelo solo donde una casa se levanta sobre arena. Los campos que siguen al par pueden darse en cualquier orden, y una entrada que nombre un campo que no pueda leerse se rechaza entera en lugar de aplicarse a medias.

| Campo | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| chance | int, de 1 a 100 | `100` | Con qué frecuencia se aplica la regla, sobre cien |
| `at=` | nombre de bloque | ninguno | Solo donde este bloque ya está en el lugar sobre el que se construye |
| `under=` | nombre de bloque | ninguno | Solo donde este bloque queda justo debajo del lugar |

Un par simple se responde cuando una pieza le pregunta al juego con qué debe construir, así que cambia todos los muros de ese bloque a la vez. Una regla se pondera donde el bloque se coloca realmente, un lugar cada vez, que es lo que permite que una probabilidad y una condición signifiquen algo, y ve el bloque tal como está a punto de colocarse, después de que cualquier par simple haya dicho lo suyo. Los lugares sobre los que cae una probabilidad se calculan a partir de la semilla del mundo y del propio lugar, de modo que el mismo mundo siempre desgasta los mismos bloques, por muchas veces que se genere.

Los caminos nunca se rigen por reglas, para que las pendientes, los puentes y los diseños de cruce sigan leyendo el camino que trazaron. Una parcela de plantilla coloca su propio archivo `.nbt` en lugar de construir a la manera del juego, así que las reglas no llegan a su interior; sus bloques son los del archivo. Tanto los pares simples como las reglas funcionan esté `terrainAdaptation` activado o no.

`villagePieces` nombra piezas de aldea vanilla: `house1`, `house2`, `house3`, `house4garden`, `church`, `woodhut`, `hall`, `field1` y `field2`, y `villagePiecesAreBlacklist` decide el sentido, de modo que puedes quitar los campos de trigo de vanilla y dejar las casas, o listar las únicas piezas que quieres. Una parcela de pack se nombra por su propia plantilla: el nombre completo, `mypack:big_house`, o solo `big_house`, o el nombre propio de la parcela si lo prefieres. Así, un pack puede incluir diez parcelas y una plantilla de mundo puede quitar una de ellas sin tocar las otras nueve. Lo mismo vale para las piezas que añaden otros mods, entre ellas las casas de Tektopia o las parcelas de Recurrent Complex: una lista blanca solo retira las piezas propias de vanilla, así que listar las de vanilla que quieres no borrará en silencio las de otro. Para quitar una pieza de un mod, usa una lista negra y nómbrala, `tekhouse2` y similares.

`villagePlotsLeast` y `villagePlotsMost` acotan con cuántas parcelas se construye una aldea, contando casas, campos y parcelas de pack, nunca caminos, antorchas ni el pozo. Una aldea que se traza por debajo del mínimo se vuelve a hacer crecer a un tamaño mayor, unos cuantos intentos, y gana el trazado más grande, así que un terreno angosto aún puede quedarse corto. Al llegar al máximo la aldea deja de crecer por completo: ni más edificios ni más caminos. `0` en cualquiera de los extremos mantiene ahí el comportamiento vanilla.

`villageTieStreets`, activado salvo que se indique lo contrario, tiende una calle de enlace para un distrito que no puede hacer crecer sus calles hasta la aldea ya existente: una calle recta de anchura completa desde uno de los extremos de calle del distrito hasta la calle existente más cercana con la que se alinea, cuando esa línea es más larga que la anchura de un camino, no pasa de 112 filas, está libre de toda pieza, no queda junto a una calle paralela, no atraviesa un túnel y es lo bastante llana para caminar. Sin ella, ese distrito se deshace, que es lo que mantiene una ciudad en terreno accidentado reducida a su plaza y cuatro calles; con ella, el distrito se une y sigue creciendo. Una ciudad en terreno llano rara vez la necesita, y está activada por defecto para que un pack que pide una ciudad grande la obtenga; desactívala para mantener pequeña una ciudad en terreno accidentado.

`villageBlockSizes` establece cuánto de profundas son las manzanas entre las calles paralelas de una ciudad, como entradas ponderadas `size=weight`: `32=3` y `64=1` trazan tres distritos de cada cuatro con manzanas de 32 de profundidad y el resto con 64. Cada distrito tira su tamaño una vez a partir de la posición de su plaza, así que el mismo mundo siempre obtiene la misma mezcla. Sus calles ramifican calles laterales a lo largo de su longitud cada dos manzanas más una anchura de camino, de modo que un distrito de 16 es una cuadrícula fina y uno de 64 una gruesa, y dos calles paralelas conservan entre sí esa cantidad de manzanas más las del distrito vecino, así que un distrito de 32 junto a uno de 64 deja 96 entre ellas. Solo se construyen a lo largo de las calles de un distrito las parcelas que caben en la profundidad, y entre las que caben, la probabilidad de una parcela es su peso por su anchura, de modo que las manzanas profundas favorecen los edificios que las llenan y una parcela de 64 de ancho nunca da a una manzana de 32 de profundidad. La longitud de las calles y la separación de las plazas siguen la parcela más grande que incluye el pack, que es lo que permite que todos los distritos lleguen a la ciudad. Vacía, dimensiona todas las manzanas según esa parcela más grande y ramifica las calles solo en sus extremos, como antes.

`villageLayout` nombra un [mapa de trazado de ciudad](#mapas-de-trazado-de-ciudades) que dispone la aldea a partir de un plano de calles dibujado en lugar de hacerla crecer; vacía, crece como de costumbre.

#### Caminos de aldea

*aldeas*

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

Todo lo que sigue solo hace algo mientras `terrainAdaptation` está activado. Todos ellos están vacíos o a cero por defecto, lo que deja los caminos de vanilla exactamente como estaban.

**Mezclar bloques.** Algunos ajustes de bloque admiten una mezcla en lugar de un solo bloque: bloques separados por comas, cada uno seguido de un espacio y un peso, como en `"minecraft:stonebrick 3, minecraft:cobblestone 1"`. Un bloque sin peso cuenta una vez. Cada bloque colocado tira la mezcla a partir de la semilla del mundo y de su lugar, de modo que el mismo mundo siempre construye el mismo patrón. Los ajustes que admiten una mezcla son `villagePathVergeBlock`, `villagePathVergeWaterBlock`, `villagePathTunnelBlock`, `villagePathBridgeFrameBlock`, `villagePathBridgeFrameTopBlock`, `villageRailTunnelBlock`, `villageRailDeckBlock`, `villageRailSupportBlock`, `villageRailBarrierBlock`, `villageRailBridgeFrameBlock`, `villageRailBridgeFrameTopBlock`, `villageSubwayTunnelBlock`, `villageSubwayPlatformBlock`, `villageSubwayRailingBlock`, `villageSubwayBenchEndBlock` y `villageSewerMossBlock`. Todos los demás ajustes de bloque usan en todo momento el primer bloque de una mezcla.

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villagePathBlock` | bloque | vacío | La superficie del camino. Vacío mantiene el bloque que usaría el bioma: arenisca sobre arena, arcilla endurecida sobre mesa, senda de hierba sobre tierra |
| `villagePathVergeBlock` | bloque | vacío | El bloque con el que se rellena el suelo junto a un camino y bajo una parcela donde la aldea tiene que crear terreno. Vacío sigue el terreno, colocando el relleno propio del bioma con hierba encima donde sería tierra |
| `villagePathVergeWaterBlock` | bloque | `minecraft:planks` | En qué se convierte ese relleno donde queda sobre agua, para que un arcén llevado sobre un lago no sea una columna de tierra. También reviste un umbral de piedra que quede sobre el agua |
| `villagePathCenterBlock` | bloque | vacío | Una línea central por el medio del camino. Vacío no dibuja ninguna |
| `villagePathCenterDash` | número | `0` | Discontinúa esa línea: N bloques de línea y luego uno de camino. Anclada a las coordenadas del mundo, de modo que los trazos de una pieza de camino continúan en la siguiente. `0` la mantiene continua |
| `villagePathLineBlock` | bloque | vacío | Líneas de borde entre el camino y la acera. Vacío no dibuja ninguna |
| `villagePathSidewalkBlock` | bloque | vacío | Aceras, a nivel con el camino fuera de las líneas de borde. Vacío no coloca ninguna |
| `villagePathSidewalkWidth` | número | `2` | Cuánto de ancha es cada acera, una vez establecido `villagePathSidewalkBlock` |
| `villagePathExtraWidth` | número | `0` | Bloques extra de camino a cada lado más allá de los 3 de vanilla. Ensancha las propias piezas de camino, de modo que las casas quedan más retiradas de una calle ancha |
| `villagePathMinimumWidth` | número | `0` | El camino más estrecho que merece la pena trazar. Un tramo que no cabe con todo su acabado se reduce a un callejón desnudo de 3 de ancho; por debajo de esta anchura no se traza en absoluto y la aldea se dispone a su alrededor. `0` nunca lo rechaza |
| `villagePathAlleyBlock` | bloque | vacío | La superficie de un callejón, un camino demasiado estrecho para llevar líneas y aceras. Un callejón discurre entre las aceras de las calles con las que se cruza y no lleva ninguna propia, y no se pinta ningún paso donde se encuentra con una calle. Vacío traza los callejones con el bloque del camino |
| `villagePathAlleyChance` | número | `0` | La probabilidad en porcentaje de que un camino se trace como callejón en lugar de ensancharse a calle completa. `0` traza un callejón solo donde no cabe una calle completa, lo que en la práctica es solo el primer distrito, el abarrotado. Subirla cambia qué caminos se trazan y por tanto remodela todo el grafo de calles; medido en 50, costó siete cruces divididos más, así que súbela y comprueba el resultado |
| `villagePathFlatRun` | número | `6` | Cuántos bloques mantiene un camino una misma altura antes de escalonarse. Anclado a las coordenadas del mundo para que las piezas vecinas coincidan. `0` escalona cada bloque, como hacen las pendientes de vanilla |
| `villagePathIntersects` | lista | ninguno | Diseños pintados en los cruces, nombrados por clave de registro desde `<namespace>/pathintersects/` de un pack. Una entrada pinta todos los cruces por igual; si hay varias, se elige una por cruce según su peso |
| `villagePathDeadEnds` | lista | ninguno | Cómo se cierra un camino que acaba en un fondo de saco, cuando no le creció una calle sin salida, descritos más abajo. Una entrada cierra todos los extremos por igual; si hay varias, se tira una por extremo a partir de la semilla del mundo. Vacía deja los extremos abiertos |
| `villagePathLampBlock` | bloque o bloque con datos | `minecraft:oak_fence` | El bloque con el que se construye un poste de farola junto a un camino, apilado sobre el bordillo. Vacío no levanta postes de farola |
| `villagePathLampHeight` | número | `3` | Cuántos bloques de alto se alza el poste antes de su cabeza |
| `villagePathLampTopBlock` | bloque o bloque con datos | `minecraft:wool:15` | La cabeza sobre el poste. Vacío la deja desnuda |
| `villagePathLampSideBlock` | bloque o bloque con datos | `minecraft:torch` | La luz colgada a cada lado de la cabeza, mirando hacia fuera. Vacío no cuelga ninguna |
| `villagePathLampStructure` | texto | vacío | Un archivo de estructura colocado como farola completa en lugar de apilar los tres bloques de farola, nombrado `mypack:street_lamp` y leído de la carpeta `structures` de ese pack. Se centra en el lugar de la farola con su capa más baja sobre el bordillo, y los bloques que coloca quedan fijados para que nada más los sobrescriba. Vacío apila los bloques |
| `villageWellStructure` | lista | ninguno | Archivos de estructura colocados como pieza central de la plaza en lugar del pozo, como entradas ponderadas `name=weight` como `mypack:plaza_spire=3`, leídos de la carpeta `structures` de ese pack y tirados una vez por pozo a partir de su posición, de modo que el mismo pozo siempre obtiene la misma. Una entrada `empty=weight` conserva el pozo para esa proporción. La elegida se centra en la huella de seis por seis del pozo con su capa más baja sobre el suelo de la plaza, ese suelo se pavimenta bajo ella, y los bloques que coloca quedan fijados para que el acabado de la plaza no los toque. Una estructura más ancha se extiende por el anillo de la plaza. Sin entradas, se construye el pozo |

Un camino se acaba de dentro hacia fuera: línea central, luego camino, luego líneas de borde, luego aceras. Las anchuras que no caben se reducen en lugar de desbordarse, así que un tramo estrecho pierde su acera sin avisar antes de perder su camino.

`villagePathBlock` y sus hermanos ganan a `villageBlocks`. Un bloque de camino nombrado se usa tal cual, mientras que el mapa solo toca lo que el camino habría elegido por sí mismo. Déjalos vacíos y decide el mapa, que es como un pack conserva la superficie fiel al bioma y aun así la recolorea.

**Los bloques de farola llevan datos.** Los tres bloques de farola admiten un nombre simple, un nombre con metadatos o un nombre con datos de entidad de bloque entre llaves, `minecraft:skull:1{SkullType:3}`. Las llaves se leen como NBT y se aplican a la entidad de bloque después de colocar el bloque, que es como una farola de otro mod conserva los ajustes que necesita. Un NBT incorrecto se notifica y se ignora en lugar de impedir que se construya la farola.

**Extremos sin salida.** Un camino que acaba sin que le crezca una calle sin salida se cierra con `villagePathDeadEnds`, un estilo tirado por extremo a partir de la semilla del mundo. Un estilo cuyo bloque no está definido queda fuera de la tirada, así que `barrier` no cierra nada hasta que `villagePathBridgeBarrierBlock` nombra un bloque, y el extremo de un callejón nunca toma `sidewalk`.

| Valor | Qué hace |
| --- | --- |
| `sidewalk` | Pavimenta la fila del extremo con el bloque de acera |
| `barrier` | Levanta el bloque de barrera a lo largo de la fila del extremo, de `villagePathBridgeBarrierHeight` de alto |

**Diseños de cruce.** `villagePathIntersects` nombra archivos que incluye un pack, cada uno un pequeño dibujo de lo que hay que pintar donde se encuentran dos caminos, trazado como filas de caracteres sueltos, un carácter por bloque.

`<namespace>/pathintersects/*.json`

La ruta del archivo es la clave de registro del diseño, que `villagePathIntersects` nombra a continuación.

```json
{
  "name": "Crosswalk",
  "weight": 3,
  "legend": { "w": "minecraft:quartz_block", "y": "minecraft:wool@4" },
  "mouth": ["wwww", "....", "wwww"],
  "corner": ["yy.", "y..", "..."]
}
```

| Clave | Valor | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `name` | cadena | el nombre del archivo | El nombre usado en el registro |
| `weight` | int, 1 o más | `1` | Proporción de cruces que gana este diseño cuando hay varios listados |
| `legend` | objeto de un carácter a un bloque | ninguno | Los caracteres que pueden usar las filas además de los roles de abajo. Un carácter que ya es un rol se rechaza con una línea de registro |
| `mouth` | lista de cadenas | ninguno | Filas pintadas en cada acceso, fuera del camino que se cruza. La primera fila es la más cercana al cruce y las demás avanzan hacia fuera. Los caracteres recorren el camino a lo ancho y se repiten donde una fila es más corta que la anchura del camino |
| `corner` | lista de cadenas | ninguno | Filas pintadas dentro del propio cruce. La primera fila es la más cercana al borde del camino que se cruza, y dentro de una fila el primer carácter es el más cercano al borde del propio camino, avanzando hacia dentro. Una celda que el dibujo no alcanza se deja como está |

Cinco caracteres son roles y no bloques, así que siguen lo que ya lleve puesto el camino: `r` es la superficie del camino, `l` la línea de borde, `s` la acera, `.` deja el bloque exactamente como estaba, y `c` está reservado y pinta la superficie del camino. Un rol cuyo bloque el pack nunca definió recurre a la superficie del camino, y cualquier otro carácter se busca en `legend`, recurriendo también a la superficie del camino.

Qué diseño obtiene un cruce se calcula a partir de la semilla del mundo y de la posición del propio cruce, de modo que el mismo mundo siempre pinta los mismos cruces. Un diseño se pinta solo donde se encuentran tres o más calles, en un cruce o en la plaza de un pozo; dos calles que se encuentran se representan como un simple codo.

#### Puentes y embarcaderos de aldea

*aldeas*

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villagePathSupportBlock` | bloque | vacío | La propia superficie donde el suelo es roca desnuda, y los pilares y patas bajo un camino sobre agua. Vacío mantiene la grava de vanilla, arenisca en las aldeas del desierto |
| `villagePathBridgeBlock` | bloque | vacío | Con qué cruza un camino el agua. Vacío mantiene los tablones de vanilla |
| `villagePathBridgeBarrierBlock` | bloque | vacío | Barreras apiladas a lo largo de ambos bordes de la plataforma de un puente. Vacío no construye ninguna |
| `villagePathBridgeBarrierHeight` | número | `1` | Cuántos bloques de alto se alzan esas barreras |
| `villagePathBridgeSidewalkBlock` | bloque | vacío | Reviste la acera donde un camino cruza el agua. Vacío lleva el bloque de acera normal al otro lado |
| `villagePathBridgeDrop` | número | `0` | A cuánta distancia del suelo debe quedar la rasante de un camino para que el desnivel bajo él se tienda con un puente en lugar de rellenarse con bloques macizos. `0` mantiene los caminos sobre el suelo: tienden puentes sobre el agua y nada más. `3` es la regla que sigue un viaducto ferroviario. Esto mueve la rasante, no solo el acabado |
| `villagePathBridgeFrameBlock` | bloque | vacío | Un pórtico sobre un puente largo: un poste a cada lado de la plataforma y una viga por encima. Cada pórtico lleva un pilar hasta el suelo bajo la plataforma, y no se levanta ningún poste de farola en la fila donde se alza. Vacío no construye ninguno |
| `villagePathBridgeFrameTopBlock` | bloque | vacío | La viga que cruza la parte superior de ese pórtico. Vacío usa `villagePathBridgeFrameBlock` |
| `villagePathBridgeFrameHeight` | número | `4` | Cuántos bloques de altura libre deja el pórtico sobre la plataforma, quedando la viga un bloque por encima |
| `villagePathBridgeFrameRun` | número | `24` | A cuántas filas de distancia se alzan los pórticos cuando un puente es lo bastante largo para varios |
| `villagePathBridgeFrameLeast` | número | `24` | El tramo tendido más corto que obtiene un pórtico. Un puente más corto se deja liso |
| `villagePathPiers` | lista | ninguno | Estilos de embarcadero para un camino que termina sin salida sobre el agua, descritos más abajo. La cola tendida pasa a ser un embarcadero; con varias entradas se tira un estilo por embarcadero. Vacía deja ese puente como un puente normal |
| `villagePathPierCargo` | lista | ninguno | Carga colocada a lo largo del interior de las barandillas de un embarcadero, como entradas ponderadas descritas más abajo. Cada dos filas de cada embarcadero tira la lista a cada lado, así que los pesos deciden cuán abarrotado se ve un embarcadero. Vacía deja los embarcaderos desnudos |
| `villagePathPierLoot` | texto | `resourcedatapackloader:chests/pier_cargo` | La tabla de botín con la que se rellena la carga que tiene inventario, tirada la primera vez que se abre. Vacía deja vacía esa carga |

**Una plataforma a nivel.** Todo puente se tiende a una sola altura de extremo a extremo, estén como estén sus dos orillas; el camino a ambos lados se inclina para encontrarse con ella.

**Un desnivel seco.** Un camino rellena un hueco con bloques macizos y tiende puentes solo sobre el agua, a menos que `villagePathBridgeDrop` indique una altura: una fila cuya rasante queda a más de esa cantidad de bloques del suelo se cubre entonces con una plataforma sobre patas, a la manera en que un viaducto ferroviario cruza un barranco. Cambia la rasante y no solo el acabado, así que una aldea trazada con él no coincide con una trazada sin él.

**Pórticos elevados.** Un tramo tendido de `villagePathBridgeFrameLeast` filas o más lleva pórticos sobre la plataforma una vez que `villagePathBridgeFrameBlock` nombra un bloque: un poste a cada lado y una viga por encima, con `villagePathBridgeFrameHeight` bloques de altura libre bajo ella. Varios se alzan en un puente largo, a `villagePathBridgeFrameRun` filas de distancia y repartidos simétricamente respecto al centro del tramo, de modo que el mismo puente siempre lleva los mismos pórticos. Una fila donde otro camino cruza el puente se deja abierta, y un embarcadero no lleva ningún pórtico: un muelle no es un puente.

**Embarcaderos.** Un camino que se adentra en el agua y acaba en nada se convierte en un embarcadero en lugar de un puente a ninguna parte, una vez que `villagePathPiers` nombra al menos un estilo. Con varias entradas se tira un estilo por embarcadero, a partir de la semilla del mundo y del extremo del embarcadero, de modo que el mismo mundo siempre construye el mismo embarcadero. Todo embarcadero se apoya en pilotes del bloque de soporte, hincados hasta el lecho en ambos bordes de la plataforma cada cuarta fila, sea cual sea su estilo. La plataforma es el bloque de puente, las barandillas y los postes son el bloque de barrera, y los pilotes son el bloque de soporte.

| Valor | Qué hace |
| --- | --- |
| `railed` | Mantiene la plataforma completa, lisa y sin líneas ni franja de acera, y cierra el extremo lejano con el bloque de barrera |
| `pilings` | Abre las barreras laterales en postes cada cuarta fila, sobre esos mismos soportes |
| `boardwalk` | Estrecha la plataforma al ancho del núcleo del camino |

**Carga del embarcadero.** `villagePathPierCargo` coloca carga en un embarcadero. Cada dos filas tira la lista una vez a cada lado, una columna hacia dentro desde las barandillas, lo que deja libre el centro de la plataforma para caminar, nunca satura la fila del extremo con barandilla y evita que dos cargas queden jamás una junto a otra, ya que dos cofres puestos en contacto se unirían en un cofre doble. Una pila se coloca solo donde cabe cada bloque de ella, y nombrar el mismo bloque dos veces a distintas alturas es cómo un embarcadero obtiene montones de tamaños mezclados.

| Valor | Qué hace |
| --- | --- |
| `<block>=<weight>` | Un bloque y su proporción de los lugares, colocado a un bloque de alto |
| `<block>=<weight>,<height>` | Lo mismo, apilado esa cantidad de bloques de alto, de 1 a 8 |
| `empty=<weight>` | La proporción de plataforma que se deja libre |

Un bloque que lleva un inventario de botín, un cofre entre ellos, se rellena desde `villagePathPierLoot`, tirado la primera vez que un jugador lo abre como se hace con un cofre de vanilla. La tabla integrada es salvamento marino fácil de reunir. Un pack la sustituye incluyendo su propio `loot_tables/chests/pier_cargo.json` bajo el espacio de nombres `resourcedatapackloader`, o nombrando una tabla propia.

#### Túneles de aldea

*aldeas*

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villagePathTunnelBlock` | bloque | vacío | Reviste un camino donde atraviesa una colina en lugar de abrirla a cielo abierto: los muros a ambos lados de la perforación y el techo sobre ella. Vacío no perfora túneles, y un camino corta la colina como antes |
| `villagePathTunnelDepth` | número | `10` | Cuánto terreno tiene que haber sobre la superficie del camino para que un tramo se perfore en lugar de cortarse. Una elevación enterrada a esa profundidad durante doce filas o más se mantiene a nivel y se perfora, y sus accesos menos profundos se cortan a cielo abierto; un bulto más corto se corta como antes. Solo cuenta una vez que `villagePathTunnelBlock` nombra un bloque |
| `villagePathTunnelLightBlock` | bloque | vacío | Una luz incrustada en el techo del túnel a lo largo de su línea central. Vacío no ilumina nada |
| `villagePathTunnelLightRun` | número | `8` | A cuántos bloques de distancia se colocan esas luces. Ancladas a las coordenadas del mundo, de modo que las luces de una pieza de camino continúan en la siguiente; un túnel demasiado corto para alcanzar uno de esos lugares se ilumina una vez, en su centro |

**Túneles.** Sin un bloque de túnel, un camino que se topa con una colina la sube, un bloque por fila como máximo, y no corta más de dos bloques de profundidad en una elevación corta. Una vez que `villagePathTunnelBlock` nombra un bloque, una elevación que queda `villagePathTunnelDepth` o más sobre el camino durante al menos doce filas se perfora: el camino mantiene el nivel del lado más alto a lo largo de toda la elevación, cada fila con tanto terreno encima recibe una perforación de cuatro bloques de alto con el bloque de revestimiento para muros y techo, y las filas menos profundas antes de los portales se cortan a cielo abierto como acceso. Un camino que se topa con la pared de una montaña y no con una colina que pueda ver por encima tampoco se sube: mantiene el nivel al que llega y busca el otro lado, hasta 98 filas más allá de donde habría terminado la pieza. Si lo encuentra dentro de ese alcance, con el terreno intermedio libre de otras piezas, la pieza se alarga para salir por el portal lejano, de modo que un túnel siempre atraviesa. Si no lo encuentra, el camino se detiene al pie de la montaña y nunca entra en ella. Toda la calle atraviesa, carriles, líneas y aceras por igual, iluminada desde el techo por `villagePathTunnelLightBlock` cada `villagePathTunnelLightRun` bloques, mientras que los postes de farola y la decoración del arcén se detienen en los portales. Un cruce nunca se perfora, así que una calle que cruza siempre se encuentra con el camino a cielo abierto. No se asienta ninguna parcela a lo largo de un tramo que el camino va a perforar ni se ramifica ninguna calle desde él, de modo que una casa nunca da a un túnel y ningún cruce se corta en uno; un distrito que no encuentra espacio para sus parcelas en otro sitio traza allí menos calles.

#### Alcantarillas de aldea

*aldeas*

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villageSewerBlock` | nombre de bloque | ninguno | El bloque con el que se reviste una alcantarilla bajo las calles y callejones de una aldea: su suelo, sus dos muros y su techo. Vacío no excava alcantarillas |
| `villageSewerDepth` | número | `8` | A qué profundidad bajo la propia superficie de una calle queda el suelo de la alcantarilla. La alcantarilla sigue la calle bajo la que discurre, así que una calle que sube lleva una alcantarilla que sube |
| `villageSewerHeight` | número | `3` | Cuántos bloques de altura libre hay sobre el pasillo |
| `villageSewerWidth` | número | `5` | Cuánto de ancha es la alcantarilla, contada a lo ancho incluidos sus dos muros. Un número par se redondea hacia arriba para que el canal conserve el centro |
| `villageSewerWaterBlock` | nombre de bloque | `minecraft:water` | Lo que llena el canal del centro. Vacío deja el canal seco |
| `villageSewerWalkBlock` | nombre de bloque | ninguno | Con qué se reviste la superficie de los pasillos a ambos lados del canal. Vacío camina sobre el bloque de revestimiento |
| `villageSewerLightBlock` | nombre de bloque | ninguno | El bloque incrustado en el techo sobre el canal como luz. Vacío no ilumina nada |
| `villageSewerLightRun` | número | `8` | A cuántos bloques de distancia se colocan esas luces. Ancladas a las coordenadas del mundo, de modo que las luces de una pieza de camino continúan en la siguiente |
| `villageSewerLadderBlock` | nombre de bloque | ninguno | El bloque por el que se sube por el pozo de una boca de alcantarilla, colocado por el pozo desde la calle hasta el pasillo de la alcantarilla. Vacío deja el pozo abierto |
| `villageSewerCoverBlock` | nombre de bloque | ninguno | El bloque que cubre una boca de alcantarilla, colocado a ras en una calle este-oeste dondequiera que una calle o callejón se encuentra con ella, y en la plaza donde esa calle cruza el anillo de la alcantarilla. Una trampilla de madera es la elección habitual: una de hierro admite una señal de redstone y ningún jugador puede abrirla a mano, lo que les cierra la alcantarilla. Vacío deja abierta la boca del pozo |
| `villageSewerMossBlock` | nombre de bloque | ninguno | Un segundo bloque mezclado aquí y allá en el revestimiento, por ejemplo piedra musgosa entre piedra lisa. Vacío reviste la alcantarilla con un solo bloque en todo su recorrido |
| `villageSewerMossChance` | de 0 a 100 | `25` | Qué porcentaje de los bloques de revestimiento salen como ese segundo bloque. Tirado por posición de bloque a partir de la semilla del mundo, de modo que la misma alcantarilla siempre sale igual |
| `villageSewerVineBlock` | nombre de bloque | ninguno | Un bloque colgado aquí y allá en el interior de los muros de la alcantarilla, por ejemplo enredaderas. Se adhiere a la pared contra la que se encuentra. Vacío no cuelga nada |
| `villageSewerVineChance` | de 0 a 100 | `20` | Qué porcentaje de las celdas junto a un muro lo llevan. Tirado por posición de bloque a partir de la semilla del mundo, de modo que la misma alcantarilla siempre cuelga lo mismo |
| `villageSewerWellEntrance` | booleano | `true` | Un anillo de alcantarilla bajo el anillo de la plaza alrededor del pozo, con la alcantarilla de cada calle atravesándolo, y una boca de alcantarilla en la plaza que baja hasta el anillo a cada lado donde una calle este-oeste lo cruza, de modo que las alcantarillas son un sistema conectado con una entrada en el centro del pueblo. Desactivado, la alcantarilla de cada calle termina en el pozo y la plaza no tiene acceso hacia abajo |

**Alcantarillas.** Nombrar `villageSewerBlock` excava una alcantarilla bajo cada calle y callejón, a `villageSewerDepth` bloques bajo la propia superficie de esa calle. No es una red independiente: sigue los caminos, así que adondequiera que van las calles va la alcantarilla, gira donde ellas giran, sube donde ellas suben, y dos alcantarillas se encuentran bajo un cruce porque las calles que tienen encima se encuentran; donde una calle o callejón acaba contra otro camino, su alcantarilla sigue bajo ese camino para unirse a la del otro. Una calle sin salida con ensanche final y una fila llevada sobre un puente no llevan ninguna. La sección es un suelo revestido, un canal por el medio lleno de `villageSewerWaterBlock`, un pasillo a cada lado revestido con `villageSewerWalkBlock`, `villageSewerHeight` bloques de altura libre y un techo revestido, de `villageSewerWidth` de ancho incluidos sus dos muros. `villageSewerLightBlock` incrusta una luz en el techo sobre el canal cada `villageSewerLightRun` bloques. Una alcantarilla nunca sube lo bastante como para perturbar la calle que tiene encima, y un tramo sin espacio entre el camino y el fondo del mundo se omite en lugar de comprimirse.

#### Ferrocarriles de aldea

*aldeas*

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

Una línea ferroviaria es un tramo recto de vía que cruza toda la aldea sobre un eje y sigue más allá de su última pieza en ambos extremos. Se traza antes que la primera calle, así que el pueblo crece a su alrededor: ninguna casa se levanta sobre la línea, una calle solo puede cruzarla en recto y nada se ramifica desde ella. Como los caminos, necesita `terrainAdaptation`. `villageRailLines` es `0` por defecto, lo que no traza ninguna y deja una aldea exactamente como estaba.

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villageRailLines` | número | `0` | Cuántas líneas atraviesan cada aldea. `0` no traza ninguna |
| `villageRailSpacing` | número | `48` | Los menos bloques de terreno libre entre el balasto de una línea y el de la siguiente de la misma aldea. `1` las traza a un bloque de distancia, que es como un pack construye una playa de vías paralelas |
| `villageRailDirection` | texto | `any` | Hacia dónde corren las líneas: `ew` de este a oeste, `ns` de norte a sur, `any` lo tira por aldea. `e`, `w`, `n` y `s` se leen del mismo modo |
| `villageRailWidth` | número | `3` | Lo mínimo que mide el lecho ferroviario. `3` lleva una vía por el centro y `5` lleva dos; un lecho al que se piden más vías de las que caben se ensancha para alojarlas |
| `villageRailTracks` | número | `0` | Cuántas vías lleva un mismo lecho, una junto a otra y separadas por `villageRailTrackGap`. **El lecho se ensancha para alojarlas todas**, así que tres vías comparten un mismo lecho en lugar de convertirse en tres líneas. `0` traza una vía en un lecho de menos de cinco de ancho y dos en uno más ancho |
| `villageRailTrackGap` | número | `2` | A cuántos bloques de distancia se sitúan las vías de un lecho, de centro a centro. `2`, el mínimo permitido, deja un bloque de lecho entre ellas, que es lo que evita que se curven una hacia otra como hacen los raíles que se tocan |
| `villageRailBlock` | bloque | vacío | La vía. Vacío coloca raíles de vanilla, por los que ruedan las vagonetas; cualquier otro bloque se coloca tal cual |
| `villageRailTrackSeat` | `auto`, `on` o `in` | `auto` | Dónde se asienta la vía. `auto` asienta un bloque de raíl sobre el lecho y incrusta a ras de la superficie del lecho cualquier otro bloque; `on` siempre lo coloca sobre el lecho; `in` siempre lo incrusta en el lecho. Una vía incrustada en el lecho es como un pack logra un aspecto de raíl con bloques de hierro o losas en lugar de raíles de vagoneta, y un paso a nivel discurre entonces a ras por el pavimento |
| `villageRailBedBlock` | bloque | vacío | El lecho bajo la vía. Vacío coloca grava |
| `villageRailTieBlock` | bloque | vacío | La traviesa colocada a través del lecho cada `villageRailTieRun` filas. Vacío coloca tablones |
| `villageRailTieRun` | número | `2` | A cuántas filas de distancia se sitúan las traviesas |
| `villageRailShoulderBlock` | bloque | vacío | Reviste las columnas más externas del lecho, un camino de mantenimiento junto a la vía y la respuesta del ferrocarril a la acera de un camino. Vacío no coloca ninguno |
| `villageRailShoulderWidth` | número | `1` | Cuántas columnas de ancho tiene ese arcén a cada lado, añadidas fuera de `villageRailWidth` |
| `villageRailPowerBlock` | bloque | vacío | La vía propulsada incrustada en la línea cada `villageRailPowerRun` filas. Vacío usa un raíl propulsor de vanilla; un bloque que no sea un raíl simplemente se coloca ahí |
| `villageRailPowerBase` | bloque | vacío | Lo que se coloca bajo una vía propulsada para alimentarla. Vacío usa un bloque de redstone |
| `villageRailPowerRun` | número | `0` | Cada tantas filas se incrusta un raíl propulsor sobre un bloque de redstone en una vía de raíl de vanilla, para que la vagoneta siga rodando. `0` no propulsa ninguna, y cualquier vía que no sean raíles de vanilla lo ignora |
| `villageRailSupportBlock` | bloque | vacío | Los postes bajo un viaducto. Vacío usa troncos |
| `villageRailDeckBlock` | bloque | vacío | La plataforma sobre la que un viaducto lleva el lecho. Vacío usa tablones |
| `villageRailBarrierBlock` | bloque | vacío | Barreras a lo largo de ambos bordes de la plataforma de un viaducto. Vacío no coloca ninguna |
| `villageRailBridgeFrameBlock` | bloque | vacío | Un pórtico sobre un viaducto largo: un poste a cada lado de la plataforma y una viga por encima. Toda fila que lleva uno lleva también sus postes de soporte hasta el lecho. Vacío no construye ninguno |
| `villageRailBridgeFrameTopBlock` | bloque | vacío | La viga que cruza la parte superior de ese pórtico. Vacío usa `villageRailBridgeFrameBlock` |
| `villageRailBridgeFrameHeight` | número | `4` | Cuántos bloques de altura libre deja el pórtico sobre la plataforma, quedando la viga un bloque por encima |
| `villageRailBridgeFrameRun` | número | `24` | A cuántas filas de distancia se alzan los pórticos cuando un viaducto es lo bastante largo para varios |
| `villageRailBridgeFrameLeast` | número | `24` | El viaducto más corto que obtiene un pórtico. Un viaducto más corto se deja liso |
| `villageRailTunnelBlock` | bloque | vacío | Reviste los muros y el techo donde la línea atraviesa una colina. Vacío no perfora túneles y corta toda colina a cielo abierto |
| `villageRailTunnelDepth` | número | `6` | Cuánto terreno debe haber sobre el lecho para que un tramo se perfore en lugar de cortarse. Necesita `villageRailTunnelBlock` |
| `villageRailTunnelLightBlock` | bloque | vacío | Una luz incrustada en el techo de un túnel ferroviario a lo largo de su línea central. Vacío no ilumina nada |
| `villageRailTunnelLightRun` | número | `8` | A cuántos bloques de distancia se sitúan esas luces del túnel, ancladas a las coordenadas del mundo para que las piezas coincidan |
| `villageRailClimb` | número | `8` | Cuántas filas corre la línea a nivel por cada bloque que sube o baja. `1` la inclina tanto como un camino |
| `villageRailTail` | número | `48` | Cuánto sigue la línea más allá de la última pieza de la aldea en ambos extremos |

**Por dónde va una línea.** Las líneas corren en paralelo, sobre el eje que nombra `villageRailDirection`, y se reparten a partir de la plaza del pozo por turnos, primero a un lado y luego al otro, cada una manteniendo al menos `villageRailSpacing` bloques de terreno entre su lecho y el de la siguiente. Una línea nunca atraviesa la plaza ni una parcela: se traza antes que la primera calle, así que todas las calles y casas de la aldea se colocan a su alrededor, y se recorta a la aldea ya crecida más `villageRailTail` en ambos extremos una vez trazada la aldea.

**Rasante.** Un ferrocarril no sube como un camino. Su lecho sigue el terreno suavizado a lo largo de un tramo largo y cambia de nivel un bloque como máximo cada `villageRailClimb` filas. Donde el terreno cae más de tres bloques, la línea corre sobre un viaducto, `villageRailDeckBlock` sobre postes `villageRailSupportBlock` cada cuatro filas, tanto sobre el agua como sobre un barranco. Donde el terreno sube, la línea se corta a cielo abierto, o se perfora con `villageRailTunnelBlock` una vez que el terreno sobre el lecho alcanza `villageRailTunnelDepth` de profundidad durante doce filas o más. Se mantienen libres cuatro bloques sobre el lecho a lo largo de toda la línea. Un viaducto queda a una sola altura de extremo a extremo, y el lecho a ambos lados se inclina para encontrarse con esa altura; donde mantener un viaducto a nivel y el ritmo de ascenso discrepan, gana el nivel y la rampa contigua puede escalonarse antes de lo que dice `villageRailClimb`. Un viaducto de `villageRailBridgeFrameLeast` filas o más lleva pórticos elevados una vez que `villageRailBridgeFrameBlock` nombra un bloque, a `villageRailBridgeFrameRun` filas de distancia y repartidos simétricamente respecto al centro del viaducto, y toda fila que lleva uno lleva con él sus postes de soporte hasta el lecho. Una fila donde un camino cruza la línea se deja abierta.

**Cruces.** Una calle cruza una línea en recto y sigue libre más allá de ambos bordes del lecho siete bloques o más. Una calle que acabaría en la línea o dentro de esos siete bloques se prolonga al otro lado cuando su rasante lo permite, y si no se detiene siete bloques antes del lecho; una calle que empezaría en la línea o correría a lo largo de ella se rechaza. En un cruce la calle se adapta a la línea, nunca al revés, y se inclina hasta ese nivel con su propia pendiente transitable a ambos lados. El pavimento conserva la superficie y la vía lo cruza un bloque más arriba, de modo que una vagoneta cruza el camino y un aldeano cruza la vía. Una línea perforada a través de una colina no se cruza en absoluto: la calle pasa por encima del túnel.

**Umbrales.** Con `terrainAdaptation` activado, ningún edificio de aldea coloca un bloque de escaleras fuera de su propia caja: los escalones de umbral que vanilla pone ante una puerta se omiten, ya que la fachada del camino y el delantal de la parcela llevan el terreno hasta la puerta por sí mismos.

**Vía.** Con `villageRailBlock` vacío, la vía es raíl de vanilla orientado a lo largo de la línea, y `villageRailPowerRun` coloca un raíl propulsor sobre un bloque de redstone cada tantas filas para que una vagoneta recorra toda la línea. Un pack que quiera bloques de hierro, barrotes o cualquier otra cosa los nombra en su lugar, y la línea se acaba con ese bloque tal cual.

#### Metros de aldea

*aldeas*

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villageSubwayLines` | número | `0` | Cuántas líneas de ferrocarril subterráneo excava una aldea. 0 no excava ninguna y no tira nada, así que la aldea se traza exactamente como se trazaría sin ellas |
| `villageSubwayDepth` | número | `24` | A qué profundidad bajo la superficie queda el lecho. La línea se nivela a partir del terreno que tiene encima, así que sigue el terreno a esa profundidad en lugar de correr a nivel |
| `villageSubwaySpacing` | número | `64` | A qué distancia se mantienen entre sí las líneas de metro de una aldea |
| `villageSubwayDirection` | cadena | `any` | Hacia dónde corren las líneas de metro: `ew` de este a oeste, `ns` de norte a sur, o `any` para tirarlo por aldea |
| `villageSubwayWidth` | número | `3` | Cuánto de ancho es el lecho, antes de los arcenes |
| `villageSubwayTracks` | número | `0` | Cuántas vías paralelas lleva el lecho. 0 toma tantas como permita la anchura |
| `villageSubwayTrackGap` | número | `2` | A qué distancia se sitúan las vías paralelas |
| `villageSubwayBlock` | bloque | vacío | El bloque de vía. Vacío coloca raíl de vanilla |
| `villageSubwayTrackSeat` | cadena | `auto` | Si la vía se asienta sobre el lecho, en él, o `auto` para que lo decida el bloque |
| `villageSubwayBedBlock` | bloque | vacío | El bloque del que está hecho el lecho. Vacío usa grava |
| `villageSubwayTieBlock` | bloque | vacío | El bloque colocado a través del lecho como traviesas. Vacío usa tablones |
| `villageSubwayTieRun` | número | `2` | A cuántos bloques de distancia se sitúan las traviesas |
| `villageSubwayShoulderBlock` | bloque | vacío | El bloque a cada lado del lecho. Vacío no deja arcén |
| `villageSubwayShoulderWidth` | número | `1` | Cuánto de ancho es ese arcén |
| `villageSubwayPowerBlock` | bloque | vacío | El bloque de vía propulsada. Vacío usa raíl propulsor de vanilla |
| `villageSubwayPowerBase` | bloque | vacío | El bloque colocado bajo una vía propulsada para accionarla. Vacío usa un bloque de redstone |
| `villageSubwayPowerRun` | número | `0` | A cuántos bloques de distancia se sitúan las vías propulsadas. 0 no coloca ninguna |
| `villageSubwayTunnelBlock` | bloque | vacío | El bloque con el que se reviste la perforación: los muros a ambos lados y el techo sobre ella. Vacío excava la perforación y sus estaciones sin revestir |
| `villageSubwayTunnelLightBlock` | bloque | vacío | El bloque incrustado en el techo del túnel como luz. Vacío no ilumina nada |
| `villageSubwayTunnelLightRun` | número | `8` | A cuántos bloques de distancia se sitúan esas luces, ancladas a las coordenadas del mundo para que las piezas coincidan |
| `villageSubwayClimb` | número | `8` | Cuántos bloques corre una línea antes de poder subir o bajar un bloque |
| `villageSubwayTail` | número | `48` | Cuánto sigue una línea más allá de las propias piezas de la aldea antes de detenerse |
| `villageSubwaySurfaces` | número | `25` | La probabilidad sobre cien de que una línea de metro suba a la superficie por un extremo y siga desde allí como un ferrocarril ordinario, con el túnel detrás y la vía a cielo abierto delante. `0` mantiene todo metro enterrado en toda su longitud |

**Salir a la superficie.** `villageSubwaySurfaces` es la probabilidad sobre cien de que una línea, en lugar de permanecer enterrada de extremo a extremo, suba a la superficie por un extremo y siga desde allí como un ferrocarril ordinario: túnel detrás, vía a cielo abierto delante. La subida obedece a `villageSubwayClimb`, un bloque por esa cantidad de filas, así que una línea de `villageSubwayDepth` de profundidad gasta profundidad por ascenso filas solo en la rampa y necesita un buen tramo más allá para merecer el nombre; una línea sin espacio para ambos se queda simplemente bajo tierra. En una línea corta que lleva una estación, subir `villageSubwayClimb` es lo que deja espacio para ambos.

#### Estaciones de metro

*aldeas*

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villageSubwayStationLength` | número | `0` | Cuántos bloques de largo tiene la cámara de una estación, centrada en la fila donde la línea pasa más cerca del pozo. 0 no construye ninguna estación |
| `villageSubwayStationRun` | número | `0` | A cuántos bloques de distancia se sitúan más estaciones a lo largo de la línea, además de la del pozo. Cada una se desliza un poco a lo largo para encontrar terreno que la admita y se omite donde ninguno lo hace. 0 construye solo esa |
| `villageSubwayStationRepeat` | número | `12` | Cuántas capas de la construcción de una estación se repiten, para que una sola construcción sirva a cualquier profundidad: el pozo crece por copias enteras de esta banda y el corredor absorbe lo que sobra. Debe ser una vuelta entera de las escaleras o los tramos no se unirán. `0` nunca hace crecer la construcción |
| `villageSubwayStationFoot` | número | `4` | Cuántas capas en la base de la construcción de una estación se colocan una sola vez, antes de la parte que se repite. Aquí viven el suelo y la puerta hacia el andén |
| `villageSubwayPlatformWidth` | número | `3` | Cuánto se abre la cámara a cada lado del lecho para formar un andén |
| `villageSubwayPlatformBlock` | bloque | vacío | El bloque con el que se pavimenta el andén. Vacío lo pavimenta con el revestimiento del túnel |
| `villageSubwayStation` | texto | vacío | El archivo de estructura con el que se construye cada estación, nombrado `mypack:subway_station` y leído de la carpeta `structures` de ese pack. Sus bloques se colocan tal como se construyeron, con esponja en lugar del revestimiento del túnel, y sus celdas de aire se excavan, de modo que lo que se alza bajo tierra es la construcción y no una descripción de ella. Una línea obtiene estaciones solo cuando esto nombra una construcción que se carga: vacío, o un nombre que no puede cargarse, no construye ninguna estación |
| `villageSubwayRailingBlock` | bloque | `minecraft:iron_bars` | El bloque que rodea con barandilla la cabecera de las escaleras de una estación donde se abren a la calle, para que nadie caiga al pozo. Vacío deja la cabecera sin barandilla |
| `villageSubwayBenchBlock` | bloque | `minecraft:oak_stairs` | El asiento de los bancos colocados en el andén de una estación y junto a la cabecera de la escalera. Un bloque de escaleras se orienta de espaldas a la línea y se lee como un banco; sirve cualquier bloque. Vacío deja los bancos fuera |
| `villageSubwayBenchEndBlock` | bloque | `minecraft:log` | Los brazos en cada extremo de un banco de estación. Vacío deja el asiento desnudo en ambos extremos |
| `villageSubwayBenchLength` | número | `5` | Cuánto de largo es un banco de estación, brazos incluidos. `0` deja los bancos fuera |

**Estaciones.** Una línea de metro obtiene una estación donde pasa más cerca del pozo una vez que `villageSubwayStationLength` está definido y `villageSubwayStation` nombra una construcción que se carga, y otras cada `villageSubwayStationRun` bloques a lo largo de ella. Cada una de ellas se desliza unos bloques en cualquier sentido para encontrar un lugar que el terreno admita, se mantiene alejada de las estaciones ya reclamadas y simplemente se omite donde no hay nada viable cerca, de modo que una línea nunca lleva una cámara sin acceso. La cámara es el lecho abierto `villageSubwayPlatformWidth` a cada lado, pavimentada con `villageSubwayPlatformBlock`, con muros y techo en el revestimiento del túnel, e iluminada con el propio `villageSubwayTunnelLightBlock` y `villageSubwayTunnelLightRun` del túnel. Desde el andén un corredor lleva a una caja de escaleras que sube a la calle junto al camino, nunca bajo él, y nunca por la plaza del pozo ni por una casa; donde la subida es demasiado larga para ir en recto, el corredor gira primero de vuelta a lo largo de la cámara. Una barandilla de `villageSubwayRailingBlock` rodea la cabecera de la escalera a nivel de calle con el extremo cercano abierto como entrada, y un banco de `villageSubwayBenchBlock` con brazos de `villageSubwayBenchEndBlock`, de `villageSubwayBenchLength` de largo, se alza en el andén y de nuevo junto a la cabecera de la escalera.

**Construir la estación a mano.** `villageSubwayStation` nombra el archivo de estructura con el que se construye cada estación, y sin él no se construye ninguna. Es como un pack incluye una forma que alguien construyó en lugar de una descrita en ajustes. Constrúyela en un mundo, marca la estructura con cualquier bloque, expórtala y colócala con el pack: sus bloques se colocan tal como se construyeron, con esponja en lugar del revestimiento del túnel, y sus celdas de aire se excavan. Una sola construcción sirve a cualquier profundidad porque su parte central se repite: `villageSubwayStationFoot` capas se colocan una vez abajo, con el suelo y la puerta hacia el andén, y luego se apilan copias enteras de las siguientes `villageSubwayStationRepeat` capas hasta que la construcción llega a la calle. Esa banda debe ser una vuelta entera de las escaleras o los tramos no se encontrarán donde se unen dos copias. La construcción lleva su propia abertura a la calle.

#### Enlaces ferroviarios

*aldeas*

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

Los enlaces ferroviarios unen aldeas vecinas en una sola red. Las aldeas se fundan una por celda de la cuadrícula de aldeas (`structureSpacing`), y un enlace corre a lo largo de la junta entre dos celdas: la primera línea de cada aldea sigue más allá de su cola como ramal, recto hasta la junta, y se encuentra con un tronco tendido a lo largo de la junta en ángulo recto. El tronco va de un ramal al otro y nunca más allá de ninguno. Necesita `villageRailLines`, o `villageSubwayLines` en un pack sin líneas de superficie, y está desactivado por defecto.

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villageRailLinks` | true/false | `false` | Enlaza aldeas vecinas cuyas primeras líneas se miran a través de una junta |
| `villageRailLinkLeast` | número | `128` | El enlace más corto que se traza, ramal más tronco más ramal, en bloques |
| `villageRailLinkMost` | número | `1024` | El enlace más largo que se traza, ramal más tronco más ramal, en bloques |
| `villageRailLinkBridgeMost` | número | `96` | El puente más largo que puede necesitar un enlace. Un enlace sobre agua más ancha o un desnivel más profundo no se traza |
| `villageRailLinkTunnelMost` | número | `192` | El túnel más largo que puede necesitar un enlace donde `villageRailTunnelBlock` perfora túneles. Un enlace que tendría que perforar más no se traza |
| `villageRailLinkStation` | texto | `both` | La estación en cada ramal justo antes del tronco: `both` coloca un andén a cada lado de la línea, `one` un único andén a la izquierda de un tren que llega al tronco, `none` no construye ninguno |
| `villageRailLinkStationLength` | número | `16` | Cuántas filas de largo tienen los andenes de la estación. `0` no construye estaciones |
| `villageRailLinkPlatformWidth` | número | `3` | Cuántos bloques de ancho tiene cada andén |
| `villageRailLinkPlatformBlock` | bloque | vacío | El bloque con el que se construyen los andenes. Vacío usa ladrillos de piedra |

**Qué aldeas se enlazan.** Dos aldeas se enlazan solo cuando están en celdas vecinas, sus primeras líneas corren sobre el eje que cruza la junta entre ellas, y el enlace completo, medido de pozo a pozo a lo largo de la vía, mide entre `villageRailLinkLeast` y `villageRailLinkMost` bloques. Cada parte de la decisión se calcula a partir de la semilla y de los dos emplazamientos de aldea, de modo que sale igual se genere primero la aldea o el chunk que se genere. Un enlace que no puede construirse entero no se traza en absoluto, nunca a medias: uno que necesitaría un puente o un túnel más largo de lo que permiten los ajustes, llegaría más allá del borde del mundo, chocaría con una mansión del bosque, dejaría dos aldeas más cerca de lo que permite `structureSeparation`, o acercaría demasiado una unión a una esquina de las celdas. Un tronco solo se tiende hacia una aldea que se fundó realmente: cuando un tope como `structureMost` detiene a la vecina, o esta crece demasiado poco para conservarse, no se construye ninguna mitad del tronco ni el ramal más allá de la propia cola de la aldea. Las aldeas fijadas se enlazan del mismo modo, una por celda; una celda con dos fijadas no enlaza ninguna. Otras aldeas se mantienen apartadas del ramal y el tronco de un enlace a medida que crecen, como se mantienen apartadas unas de otras.

**Rasante.** Los ramales y troncos son líneas ferroviarias y se nivelan, tienden sobre puentes, perforan en túneles y cruzan exactamente como una línea de aldea, con `villageRailClimb` y los ajustes de viaducto y túnel de más arriba. Donde un ramal se encuentra con el tronco ambos quedan a nivel, y también la estación contigua.

**El empalme.** Un ramal se une solo a la vía cercana del tronco. Esa vía se interrumpe donde el centro del ramal se encuentra con ella, la vía izquierda del ramal gira a la izquierda hacia ella y su vía derecha gira a la derecha, y la vía lejana sigue recta. Con dos vías, el tronco por arriba y el ramal subiendo desde abajo:

```
xxxxxxx
ooooooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

`x` es lecho y `o` es vía. Donde dos ramales llegarían a pocos bloques uno del otro, la primera línea de la segunda aldea se desplaza para alinearse con la primera, y ambos se encuentran en cambio en un cruce: cada ramal se funde solo con su propia vía cercana exactamente como arriba, ambas vías del tronco se interrumpen en el centro del ramal, y ningún raíl cruza otro:

```
 xoxox
xxoxoxx
oooxooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

Un tronco de vía única no tiene una segunda vía que dar al otro ramal, así que un enlace cuyos ramales se encontrarían de frente sobre una vía única no se traza. Con una sola vía, la vía del ramal se curva hacia la vía del tronco hacia la izquierda, y la vía del tronco más allá de esa curva termina contra ella. Las curvas se colocan con su forma fija, de modo que el raíl de vanilla gira donde se dibuja el empalme y en ningún otro sitio.

**Estaciones.** Las últimas filas de un ramal antes del empalme son una estación: andenes de `villageRailLinkPlatformBlock` a nivel con el raíl, con barandilla a lo largo del borde exterior con `villageSubwayRailingBlock`, y con un banco de `villageSubwayBenchBlock` a mitad de cada andén.

**Metros.** En un pack con solo líneas de metro, el enlace lleva la primera línea de metro de una aldea. La línea sale del terreno hacia el tronco, con una rampa de `villageSubwayDepth` por `villageSubwayClimb` filas de largo, y llega a la estación y al empalme en la superficie; una aldea de este tipo se enlaza solo por un lado, el del enlace más corto, y el tronco es un ferrocarril de superficie construido con los ajustes `villageRail`. Donde un ramal no tiene espacio para esa rampa y su estación, el tronco baja en cambio al metro: todo el enlace, ramales y tronco, permanece bajo tierra a `villageSubwayDepth`, se construye con los ajustes `villageSubway` y se encuentra en el mismo empalme sin estación.

#### Decoración de aldea

*aldeas*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageDecor": ["mypack:street_flowers=2", "mypack:street_tree=1", "empty=3"]
  }
}
```

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villageDecor` | lista de `name=weight` | ninguno | Reparte la generación propia de este pack por los arcenes de los caminos de aldea. El nombre es una clave de registro de generación, el peso es la proporción de lugares de esa entrada, y `empty=weight` es la proporción de lugares que se dejan desnudos |

`villageDecor` reparte la generación de un pack por los arcenes de los caminos de aldea, que es lo que evita que una aldea parezca casas en un terreno de hierba pelada. Cada entrada es `name=weight`: el nombre es una clave de registro de generación, `mypack:street_flowers`, y el peso es la proporción de lugares de esa entrada. El nombre `empty` es la proporción de lugares que se dejan desnudos, y es el que hay que acertar, porque una lista sin él llena todos los lugares de todos los arcenes y la aldea resulta un vivero en lugar de una calle.

Cada tercer bloque a lo largo de cada lado de un camino es un lugar, contado a partir de las coordenadas del mundo para que el espaciado continúe de una pieza de camino a la siguiente. Un lugar se omite donde cae dentro de cualquier pieza de la aldea, sobre el propio camino, frente a una puerta, o donde el suelo no es aire libre apoyado en algo sólido. Lo que crece en un lugar se calcula a partir de la semilla del mundo y del propio lugar, de modo que el mismo mundo siempre reparte de la misma manera.

El nombre apunta a una entrada de generación corriente de `<namespace>/worldgen/*.json`, así que sirven una `decoration`, un `tree` o un `imprint`, y cada una conserva sus propios bloques, tamaños y dispersión. Aquí solo se usa la forma de esa entrada: sus biomas, dimensiones, alturas y rareza son cómo se siembra por el mundo en general, y la aldea no los consulta, así que una entrada pensada para el arcén es mejor escribirla para nada más. Un arcén es aire libre sobre el suelo, así que una entrada así necesita `replace` con el valor `minecraft:air`; una que nunca nombra `replace` recibe el valor predeterminado habitual `minecraft:stone` y no coloca nada aquí sin avisar.

Mientras `terrainAdaptation` está activado, lo que crece en un lugar queda protegido de la limpieza propia de la aldea, de modo que un árbol en un arcén no se talla de nuevo al acabar el siguiente chunk. Con él desactivado no hay limpieza contra la que protegerlo, y la dispersión es la misma.

#### Ajustes de aldea por bioma

*aldeas*

**Un bioma puede construir de forma distinta.** Un objeto `biomes` dentro de `settings` contiene ajustes de aldea propios para un bioma nombrado, de modo que una aldea de desierto traza calles de arenisca donde una de llanuras traza hormigón sin que ninguna sea un pack aparte. Nombra un bioma por su id, `minecraft:desert`, o por un tipo de bioma de Forge, `SANDY`, `SNOWY`, `MESA`, `JUNGLE` y los demás; un id exacto se mira antes que los tipos, así que una regla general puede anularse para un bioma. Todo lo que no se nombre dentro de una sección recurre al ajuste simple que hay sobre ella.

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

Todo ajuste de bloque que admite un camino, un puente, un ferrocarril, un metro, una estación o una alcantarilla responde a esto, y la sintaxis de mezcla ponderada funciona dentro de una sección igual que fuera. El bioma se lee al construir una pieza, y los bloques se toman de nuevo dondequiera que el terreno cambia de bioma, así que un camino o un ferrocarril que sale de un desierto cambia de material en la propia frontera. Una línea de registro al cargar el mundo indica cuántas secciones incluyó un pack y las nombra, y con la depuración activada cada bioma dice qué sección tomó, o que no tomó ninguna y a qué habría respondido.

### Blast Plaster

*qué hace cada grupo*

Qué ocurre después de una explosión, a partir de `<namespace>/blastplaster/*.json`. `default` deja que decidan los packs, `global` ignora los archivos de los packs y deja los valores por defecto de este mod sobre la configuración de Blast Plaster, y `off` devuelve Blast Plaster por completo a su propia configuración.

### Estructuras

*qué hace cada grupo*

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

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `structureSpacing` | lista de `structure=chunks` | vanilla | A qué distancia unas de otras se siembran las estructuras. Alcanza a templos, monumentos, mansiones, ciudades del End y fortalezas; en `mineshafts` el número significa un chunk de cada tantos, porque así las coloca vanilla |
| `structureSeparation` | lista de `structure=chunks` | vanilla | Lo más cerca que pueden estar dos de una misma estructura. Alcanza a monumentos, mansiones, ciudades del End, fortalezas y aldeas, para las que es el mínimo de chunks entre una aldea y la siguiente, aunque la cuadrícula permitiera menos |
| `structureMinDistanceFromSpawn` | lista de `structure=blocks` | vanilla | A qué distancia del punto de aparición del mundo empieza a generarse una estructura |
| `structureBiomes` | lista de `structure=biome,biome` | vanilla | En qué biomas se genera una estructura, por nombre de registro o por tipo del diccionario de biomas. Alcanza a todas las estructuras salvo las ciudades del End, ya que en esta versión el End es un único bioma |
| `structureBiomesAreBlacklist` | lista de `structure=true` o `structure=false` | `false` | El sentido de la lista de biomas de cada estructura |
| `structureSpawns` | lista de `structure=entity:weight:least:most` | vanilla | Los mobs que genera una estructura diga lo que diga el bioma que la rodea. Solo los templos, los monumentos y las fortalezas del Nether tienen una lista así; una línea vacía tras el signo igual impide que esa estructura genere nada propio |
| `structureSpawners` | lista de `structure=entity` | vanilla | Qué genera el spawner de mobs dentro de una estructura vanilla, separado por comas para elegir al azar en cada spawner. Las cuatro que colocan uno son las mazmorras, las minas abandonadas, las fortalezas del Nether y las fortalezas |
| `structureAt` | lista de `structure=x,z` | ninguno | Fija una estructura en un punto exacto. Consulta [Estructuras en lugares exactos](#estructuras-en-lugares-exactos) |
| `structureMost` | lista de `structure=count` | ninguno | El máximo de una estructura que puede contener una dimensión. Solo lo leen las aldeas, y una fijada con `structureAt` se funda de todos modos |

Estructuras vanilla desactivadas por nombre, por dimensión. La colocación se controla con cuatro listas escritas como `structure=value`, una por línea: `structureSpacing` para la distancia a la que se siembran, `structureSeparation` para lo cerca que pueden estar dos, `structureMinDistanceFromSpawn` para lo lejos que empiezan y `structureBiomes` con `structureBiomesAreBlacklist` para dónde se permiten.

```
temples=24
monuments=40
mineshafts=200
```

```
temples=minecraft:desert,SANDY
monuments=minecraft:deep_ocean
```

No todas las estructuras entienden todos los ajustes. La separación en cuadrícula (spacing) alcanza a templos, monumentos, mansiones, ciudades del End y fortalezas; en `mineshafts` el número significa un chunk de cada tantos en lugar de una cuadrícula, porque así las coloca vanilla. La separación mínima (separation) alcanza a monumentos, mansiones, ciudades del End, fortalezas y aldeas, para las que es el mínimo de chunks entre una aldea y la siguiente, aunque la cuadrícula permitiera menos. `structureMost` limita cuántas de una estructura puede contener una dimensión, `villages=100`: una vez fundadas tantas, no se funda ninguna más, la ponga donde la ponga la cuadrícula, mientras que una aldea fijada con `structureAt` se funda de todos modos. Solo las aldeas lo leen. Los biomas alcanzan a todas las estructuras salvo las ciudades del End, porque el End es un único bioma en esta versión y no hay nada entre lo que elegir. Las ciudades del End siguen escogiendo su propio lugar dentro de la cuadrícula: solo se asientan en una isla exterior cuya superficie llegue a y60, de modo que aumentar su separación las hace más escasas pero no puede colocar ninguna sobre el vacío. Las fortalezas del Nether se asientan sobre una cuadrícula fija que vanilla no expone, así que solo les alcanzan las listas de biomas y de distancia al punto de aparición. Las aldeas conservan sus propios `villageSpacing`, `villageBiomes` y el resto.

`structureSpawns` sustituye los mobs que genera una estructura diga lo que diga el bioma que la rodea, escrito como `structure=namespace:entity:weight:least:most`, separado por comas:

```
netherbridges=minecraft:blaze:10:2:3,minecraft:wither_skeleton:8:5:5
temples=minecraft:witch:1:1:1
monuments=
```

En esta versión solo los templos, los monumentos y las fortalezas del Nether tienen una lista así; las aldeas colocan a sus aldeanos a partir de las propias piezas, y las minas abandonadas, las fortalezas y las ciudades del End usan spawners y mobs colocados en su lugar. Dejar la línea vacía tras el signo igual, como con los monumentos arriba, impide que esa estructura genere nada propio.

`structureSpawners` indica qué genera el spawner de mobs dentro de una estructura vanilla, escrito como `structure=namespace:entity`, separado por comas para elegir al azar en cada spawner:

```
dungeons=minecraft:zombie,minecraft:husk
mineshafts=minecraft:cave_spider
netherbridges=minecraft:wither_skeleton
strongholds=minecraft:silverfish
```

Cuatro estructuras vanilla colocan un spawner: la sala de la mazmorra, el corredor de la mina abandonada, el trono de la fortaleza del Nether y la sala del portal de la fortaleza. Cada una se alcanza por separado, así que los spawners colocados por otros mods nunca se tocan. Las mazmorras suelen elegir de la lista a la que los mods añaden a través de Forge, de modo que nombrarlas aquí también sustituye esa elección.

La separación en cuadrícula decide dónde se siembra una estructura, por lo que cambiarla en un mundo que ya existe deja lo que hay y coloca las nuevas en una cuadrícula distinta.

### Aparición

*qué hace cada grupo*

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

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `surfaceDayMonsterRate` | float | `1.0` | Multiplicador de la aparición de hostiles en la superficie de día, siendo `1.0` el valor vanilla, de modo que la aparición en superficie a plena luz se puede desactivar sin tocar las cuevas |
| `surfaceNightMonsterRate` | float | `1.0` | Lo mismo para la superficie de noche |
| `undergroundDayMonsterRate` | float | `1.0` | Lo mismo bajo tierra de día |
| `undergroundNightMonsterRate` | float | `1.0` | Lo mismo bajo tierra de noche |
| `monsterCap` | int | `-1` | Cuántos hostiles pueden estar cargados a la vez. En vanilla es 70, y `-1` lo deja como está |
| `creatureCap` | int | `-1` | Lo mismo para los animales pasivos. En vanilla es 10 |
| `ambientCap` | int | `-1` | Lo mismo para los murciélagos y similares. En vanilla es 15 |
| `waterCreatureCap` | int | `-1` | Lo mismo para los calamares. En vanilla es 5 |
| `monsterSpawnLight` | int | `-1` | La mayor luz de bloque que tolera la aparición de un hostil, además de las comprobaciones de vanilla. `0` es la regla moderna, en la que una antorcha protege por completo una cueva; `-1` mantiene los dados de vanilla |
| `skyAnimals` | boolean | `true` | Si los mobs pasivos se asientan en el terreno que un mundo rubic genera sobre su ventana de terreno, sobre todo las islas flotantes. Desactivado, mantiene a los animales y a los murciélagos en el suelo de abajo. Los spawners ignoran ambos |
| `threatItems` | lista de `item=level,count` | ninguno | Los ítems que elevan la puntuación de amenaza de un portador, con un `,each` o `,batch` opcional: `each`, el valor por defecto, suma el nivel por cada uno que se lleve hasta `count`; `batch` lo suma una vez por cada `count` completo que se lleve |
| `threatLevels` | lista de ints | ninguno | Las puntuaciones a las que se entra en cada franja, en orden ascendente, de modo que `10, 25, 50` crea tres franjas. Vacío desactiva el nivel de amenaza |
| `threatMost` | int | `-1` | Limita la puntuación. `-1` la deja sin límite |
| `threatSpawnRate` | float | `1.0` | Escala la aparición de hostiles a menos de 128 bloques de un portador en la franja más alta, además de las otras tasas, y las franjas inferiores reciben una parte proporcional |
| `threatNotice` | float, bloques | `0.0` | A cuántos bloques más lejos ven los mobs hostiles, incluidos los de vanilla, a un portador en la franja más alta, repartido de nuevo entre las franjas inferiores |
| `threatSays` | lista de `band=message` | ninguno | Las líneas que se muestran en amarillo cuando cambia la franja de un jugador, siendo la franja `0` la línea para cuando baja de la primera franja |

Tasas y límites de aparición de mobs, por bioma. La aparición de hostiles se escala con `surfaceDayMonsterRate`, `surfaceNightMonsterRate`, `undergroundDayMonsterRate` y `undergroundNightMonsterRate`, cada uno un multiplicador donde `1.0` es vanilla, de modo que la aparición en superficie a plena luz se puede desactivar sin tocar las cuevas. Los límites son `monsterCap`, `creatureCap` para los animales pasivos, `ambientCap` para los murciélagos y similares, y `waterCreatureCap` para los calamares; los de vanilla son 70, 10, 15 y 5, y `-1` deja uno como está. `monsterSpawnLight` limita la luz de bloque que tolera la aparición de un hostil además de las comprobaciones de vanilla: `0` es la regla moderna, en la que una antorcha protege por completo una cueva, y `-1`, el valor por defecto, mantiene los dados de vanilla. `skyAnimals` decide si los mobs pasivos se asientan en el terreno que un mundo rubic genera sobre su ventana de terreno, sobre todo las islas flotantes: `true`, el valor por defecto, deja las manadas de vanilla donde esté el bloque superior, y `false` mantiene a los animales y a los murciélagos en el suelo de abajo. Los spawners ignoran ambos.

El nivel de amenaza puntúa lo que lleva cada jugador y deja que el mundo responda. `threatItems` enumera los ítems que cuentan, como entradas `item=level,count` con un `,each` o `,batch` opcional al final: `each`, el valor por defecto, suma el nivel por cada uno que se lleve, contando como máximo `count` de ellos, y `batch` suma el nivel una vez por cada `count` que se lleve, solo lotes completos. Un `count` superior al tamaño de pila del ítem se recorta al tamaño de pila, de modo que una pila completa es lo máximo que puede contar una entrada, y el ítem puede llevar metadatos como `minecraft:dye:4`. Toda entidad cargada que tenga ítems es un portador, no solo los jugadores: el inventario principal, la armadura y la mano secundaria de un jugador, una pila soltada, cualquier cosa con un inventario de ítems como una mula con cofre o una vagoneta con cofre, y los ítems en mano y la armadura de cualquier otro mob, de modo que una zona sigue siendo peligrosa en torno a lo que yace, cabalga o camina por ella. Los jugadores en creativo y en espectador no puntúan nada. `threatLevels` son las puntuaciones a las que se entra en cada franja, en orden ascendente, de modo que `[10, 25, 50]` crea tres franjas, y `threatMost` limita la puntuación, dejándola sin límite con `-1`. La puntuación se toma cada cinco segundos. `threatSpawnRate` escala la aparición de hostiles a menos de 128 bloques de un portador en la franja más alta, además de las otras tasas, y las franjas inferiores reciben una parte proporcional del cambio: `2.0` la duplica en la más alta y suma un tercio en la franja uno de tres. `threatNotice` es a cuántos bloques más lejos ven los mobs hostiles, incluidos los de vanilla, a un portador en la franja más alta, repartido de nuevo de forma proporcional entre las franjas inferiores. `threatSays` son las líneas que se muestran en amarillo cuando cambia la franja de un jugador, como entradas `band=message`, siendo la franja `0` la línea para cuando baja de la primera franja. Una variante de entidad puede fijar `threatLeast` para aparecer de forma natural solo mientras un portador a menos de 128 bloques esté en esa franja o en una superior. Dejar cualquiera de las dos listas vacía desactiva el nivel de amenaza.

### Asentamiento de estructuras

*qué hace cada grupo*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "structureAdaptation": ["villages=beard_thin", "mansions=bury", "monuments=none"]
  }
}
```

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `structureAdaptation` | lista de `structure=mode` | aldeas y mansiones `beard_thin`, el resto `none` | A qué estructuras se adapta el terreno y cómo, entre aldeas, fortalezas, minas abandonadas, monumentos y mansiones. Los modos son `none`, `bury`, `beard_thin`, `beard_box` y `encapsulate` |

`structureAdaptation` decide a qué estructuras se adapta el terreno y cómo, como entradas `structure=mode`, `"mansions=bury"`, `"monuments=none"`, entre aldeas, fortalezas, minas abandonadas, monumentos y mansiones, con los cinco modos que usan las versiones modernas: `none`, `bury`, `beard_thin`, `beard_box` y `encapsulate`. Las aldeas y las mansiones son `beard_thin` salvo que se sobrescriba, y todo lo demás es `none` salvo que se nombre. Los templos todavía no se pueden nombrar, porque se colocan a sí mismos solo a medida que se construyen, de modo que no hay nada a lo que el terreno pueda adaptarse a tiempo.

### Asentamiento de aldeas

*qué hace cada grupo*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "terrainAdaptation": true
  }
}
```

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `terrainAdaptation` | boolean | `false` | Rehace cómo eligen las aldeas su terreno y cómo se asientan en él: caminos nivelados, edificios asentados, anillos con talud y todo lo demás que describe esta sección. Lo que deja puesto es permanente |

**Lo que deja puesto es permanente.** Remodela el terreno a medida que se crea el mundo, de modo que lo que ponga en un guardado se queda ahí. Una aldea generada por una compilación anterior nunca la revisa ni la repara una más nueva, así que dos mundos creados con la misma semilla en dos versiones distintas del mod no coincidirán, y las aldeas de un mundo son una instantánea del día en que se generaron esos chunks.

`terrainAdaptation` rehace cómo eligen las aldeas su terreno y cómo se asientan en él, portado en espíritu de cómo asientan sus estructuras las versiones modernas y llevado después más lejos. Una aldea solo se funda en un chunk cuyo terreno varíe como mucho diez bloques, y nunca a menos de ocho chunks de otra aldea; las regiones que no ofrecen ningún chunk así no fundan nada. El pozo se asienta en el terreno más bajo que toca su propia huella, y toda la aldea se desplaza con él, de modo que todo lo demás se nivela a partir de ahí.

Los caminos se nivelan a medida que se trazan: la superficie sigue el terreno natural más bajo a lo ancho del camino, se cortan los bultos, se rellenan los hoyos, la pendiente nunca supera un bloque por escalón y los barrancos cortos se salvan con tablones. La superficie del camino sigue el terreno que cruza: caminos de hierba sobre tierra, arenisca sobre arena, arcilla endurecida en la mesa, grava sobre piedra y sobre grava, tablones sobre el agua, de modo que una aldea del desierto tiene calles de arenisca en lugar de un sendero de tierra y los caminos ya no desaparecen donde el suelo no es de hierba. Donde dos caminos se cruzan, se encuentran a la menor de las dos cotas, ya que un nivel que ambos pueden alcanzar es el único que no deja escalón entre ellos.

Cada edificio se asienta un bloque por encima del camino al que da, leído del camino ya trazado o previsto a partir del terreno sobre el que se nivelará el camino cuando todavía no se ha construido, de modo que las escaleras de su umbral descansan sobre la superficie del camino y su puerta queda detrás de ellas. Un edificio cuya huella necesitaría más de dos bloques de terreno aportado bajo cualquier parte de ella no se construye allí: se desliza hasta doce bloques a lo largo de su camino buscando el asiento más somero, y se descarta por completo si no encuentra ninguno, de modo que las aldeas en terreno accidentado salen más dispersas en lugar de encaramadas. El anillo alrededor de un edificio se eleva con talud en el lado cuesta abajo y se recorta en el lado cuesta arriba, un bloque menos de nuevo en cada anillo más alejado.

Las granjas conservan el nivel de terreno propio de vanilla. Las farolas se alzan a la cota del camino que iluminan en lugar de la del arcén contiguo, con terreno rellenado bajo ellas donde el camino queda por encima del borde, y los postes de antorcha propios de vanilla se omiten del trazado porque estos los sustituyen. Se rellena el terreno bajo cada edificio hasta la superficie de apoyo más cercana con el mismo material sobre el que descansa, se abren muros y puertas en las laderas, se retira la tierra de los tejados, y cualquier árbol que esté dentro de una estructura se tala entero, llevándose consigo sus hojas junto con su madera, mientras que cada hoja que aún pertenece a una rama en pie se deja intacta. Las mansiones y las características dispersas (templos, chozas, iglús) se someten al mismo criterio de terreno llano antes de poder colocarse.

Remodela el propio terreno a medida que se crea, de modo que un mundo generado con esto activado difiere de uno generado sin ello, la misma advertencia que llevan las versiones modernas, y está desactivado salvo que un pack o la configuración lo pidan.

### Lecho de roca

*qué hace cada grupo*

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

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `flatBedrock` | boolean | `false` | Sustituye el lecho de roca irregular del fondo del mundo por capas planas. Solo en chunks nuevos, salvo que `flatBedrockRetrogen` esté activado |
| `flatBedrockRetrogen` | boolean | `false` | Aplana también el lecho de roca de los chunks que ya existen. Cada chunk se procesa una sola vez y lo recuerda, y no se puede deshacer: el patrón original no se guarda en ningún sitio |
| `bedrockLayers` | int | `1` | Cuántas capas de lecho de roca se conservan |
| `flatBedrockRoof` | boolean | `false` | Aplana también el techo de lecho de roca, donde una dimensión lo tiene, como el techo del Nether |
| `flatBedrockFiller` | block | vacío | Lo que sustituye al lecho de roca que se retira. Vacío elige según la dimensión: piedra, netherrack, piedra del End |
| `flatBedrockFillers` | lista de `dimension=block` | los del Nether y el End por defecto | Un relleno por dimensión, que sobrescribe `flatBedrockFiller` para las dimensiones nombradas |
| `flatBedrockDimensions` | lista de ints | `0`, el Overworld | Las dimensiones en las que aplanar. Vacío significa todas |
| `flatBedrockDimensionsAreBlacklist` | boolean | `false` | Activado, el aplanado omite las dimensiones listadas. Desactivado, se aplica solo a ellas |
| `flatBedrockBiomes` | lista de nombres de bioma | ninguno | Los biomas en los que aplanar, por nombre amistoso o de registro. Vacío significa todos los biomas |
| `flatBedrockBiomeTypes` | lista de tipos del diccionario | ninguno | Tipos del diccionario de biomas en los que aplanar, junto con `flatBedrockBiomes`. `OCEAN`, `RIVER`, `MOUNTAIN` y el resto |
| `flatBedrockBiomesAreBlacklist` | boolean | `false` | Activado, el aplanado omite los biomas listados. Desactivado, se aplica solo a ellos |

`flatBedrock` sustituye la capa irregular por capas planas, por dimensión y por bioma, con un bloque de relleno a tu elección. `flatBedrockRetrogen` lo hace en chunks que ya existen. No se puede deshacer, el patrón original no se guarda en ningún sitio. `bedrockLayers` fija cuántas capas se conservan, `flatBedrockRoof` hace también el techo donde una dimensión lo tiene, y `flatBedrockFiller` es lo que sustituye al lecho de roca retirado, dejándolo vacío para elegir según la dimensión, con `flatBedrockFillers` nombrando uno por dimensión en su lugar. A qué dimensiones y biomas alcanza lo deciden `flatBedrockDimensions`, `flatBedrockBiomes` y `flatBedrockBiomeTypes`, con `flatBedrockDimensionsAreBlacklist` y `flatBedrockBiomesAreBlacklist` convirtiendo esas listas en exclusiones.

### Ticks lentos a distancia

*qué hace cada grupo*

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

Las entidades cuestan a un servidor más que cualquier otra cosa, y la mayoría están lejos de cualquier jugador. `slowDistantEntities` da a un chunk sin ningún jugador a menos de `slowDistance` bloques un tick de cada `slowRate`, de modo que lo que hay en él sigue moviéndose, flotando, ardiendo y desapareciendo, solo que a un ritmo más lento. Nada se deja nunca sin procesar.

| Clave | Obligatoria | Valor | Valor por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `slowDistantEntities` | no | boolean | `true` | Si se ralentiza algo en absoluto |
| `slowedKinds` | no | lista de `items`, `experience`, `projectiles` | `{items, experience}` | A qué clases se les dan menos ticks. Todo lo que piensa por sí mismo se ralentiza siempre de otra manera y no se nombra aquí. Las máquinas nunca se ralentizan |
| `slowDistance` | no | int, 64 o más | `192` | A qué distancia del jugador más cercano se ralentiza un chunk |
| `slowRate` | no | int, de 1 a 20 | `4` | A un chunk ralentizado se le da un tick de cada tantos. `1` no ralentiza nada |
| `neverSlowed` | no | lista de nombres de entidad | ninguno | Se dejan como están por muy lejos que estén |
| `slowRecheck` | no | int, de 1 a 100 | `20` | Cada cuánto se vuelve a calcular la distancia al jugador más cercano |

Todo lo que piensa por sí mismo, cada mob, animal, aldeano y gólem, venga del mod que venga, recibe un trato distinto del resto, y no se nombra en absoluto en `slowedKinds`. Nunca se le dan menos ticks, porque un jugador puede verlo caminar. En su lugar se deja con un tick en cada tick y se hace que piense con menos frecuencia: la parte de su mente que decide qué hacer a continuación, que es también la parte cara, se consulta una vez de cada `slowRate` en lugar de cada tres ticks. Sigue moviéndose, cayendo, ahogándose, ardiendo y siguiendo rutas exactamente igual, y simplemente cambia de idea con menos frecuencia mientras nadie está cerca. No hay nada que ver, ni pasos entrecortados ni recuperación, y uno al que se acerca un jugador vuelve a su ser normal antes de entrar en su campo de visión. Como no se puede notar, no es una opción: ocurre dondequiera que la ralentización esté activada.

Lo que recibe menos ticks sigue envejeciendo al ritmo normal. Un ítem soltado y una esfera de experiencia llevan cada uno su propio contador que decide cuándo desaparece, y en un tick que un chunk ralentizado no toma, ese contador avanza de todos modos. Así que un ítem sigue en el suelo durante cinco minutos y no veinte. Solo se reduce lo que hace en cada tick, nunca cuánto dura.

Un chunk que algo mantiene cargado deliberadamente nunca se ralentiza, por lejos que esté. Son los chunks que mantiene un cargador de chunks, y el objetivo de mantener uno es que lo que hay en él siga funcionando, de modo que una granja que se deja trabajando mientras su dueño está en otra parte trabaja al ritmo para el que se construyó. Los chunks alrededor del punto de aparición de un mundo no son de estos, ya que nada los pidió, así que se ralentizan como cualquier otro.

Un chunk entero se ralentiza o no se ralentiza en conjunto, de modo que lo que hay dentro se comporta como debe: los ítems caen en el mismo montón, un mob sigue al que tiene al lado. Cada jugador cuenta por sí mismo, así que quien anda por su cuenta tiene un espacio tranquilo a su alrededor esté donde esté. Algo montado, con nombre, domado, atado con correa, brillante, que no puede desaparecer, bajo un efecto o que ya persigue a un jugador se deja como está por lejos que esté, igual que todas las máquinas. Se aplica a todos los mundos, incluidos los que añade un mod.

### Vigilar el trabajo de chunks

*qué hace cada grupo*

Con `worldgenDebug` activado, cada cien rondas una línea indica cómo está gastando el mundo su trabajo de chunks: cuántos chunks se crearon de cero, cuántos hubo que recuperar tras haberlos soltado, cuántos de estos salieron del disco en lugar de la cola que aún espera a ser escrita, cuántos archivos de región se abrieron y con qué frecuencia se cerraron todos a la vez, y el máximo de chunks retenidos y de escrituras pendientes en cualquier momento. Está pensada para averiguar si generar terreno cuesta tiempo en la generación o en recuperar el mismo terreno, así que conviene activarla antes de una pregeneración grande y desactivarla después.

Le siguen tres líneas más: una para escribir los chunks de vuelta al almacenamiento, otra para iluminarlos, y otra que divide la creación del propio terreno en el suelo, el adorno que le pone el juego y el adorno que le pone cada mod, nombrando a los cinco peores. Así, un mundo lento se puede leer como cuatro costes separados en lugar de uno, y el mod responsable se nombra en lugar de adivinarse.

### Crear terreno por adelantado

*qué hace cada grupo*

Es lo bastante grande como para tener su propia sección, consulta [Pregeneración](#pregeneración).

### Bloques a la espera de su turno

*qué hace cada grupo*

El agua que se extiende, la lava que se enfría y los cultivos que crecen son todos bloques que esperan un rato antes de hacer algo, y el juego guarda todos ellos en un único montón. Cada vez que se escribe un chunk, recorre ese montón entero de punta a punta buscando los pocos que le pertenecen, de modo que cuantos más tiene un mundo, más lenta se vuelve cada escritura, tenga o no alguno el chunk que se está escribiendo. Se ordenan según el chunk en el que se encuentran, y esa ordenación se descarta y se rehace en cuanto el montón cambia o la ronda avanza, de modo que escribir un chunk solo mira los pocos que le atañen.

### Más espacio para los bloques de un chunk

*qué hace cada grupo*

Un chunk se guarda en secciones, y cada sección contiene una lista de los tipos de bloque que hay en ella, empezando con espacio para dieciséis. Pasar de dieciséis significa crear una lista más grande y copiar a ella cada uno de los cuatro mil bloques de la sección, y luego otra vez a treinta y dos, y otra vez a sesenta y cuatro. El terreno con unos cuantos tipos de piedra y mineral los supera todos, así que se hace cuatro veces por un poco de espacio. Ahora pasa directamente al mayor de esos tamaños la primera vez que se queda sin espacio, lo que supone una copia en lugar de cuatro y cuesta unos pocos kilobytes por sección que se está usando en cuestión de instantes de todos modos.

### Preparar los chunks para escribirlos

*qué hace cada grupo*

Antes de poder escribir un chunk se convierte a la forma que va al disco, lo que recorre cada uno de sus bloques y busca cada uno en una tabla por nombre. El terreno viene en largas tiradas de lo mismo, de modo que se hace la misma búsqueda miles de veces para la misma piedra, y la respuesta de la última simplemente se conserva y se reutiliza cuando el siguiente bloque es igual. No se puede desactivar, ya que no hay nada que sopesar: la respuesta es la misma de cualquier modo.

### Escritura de chunks

*qué hace cada grupo*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "hurryWritesAbove": 100
  }
}
```

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `hurryWritesAbove` | int, chunks | `100` | Cuántos chunks terminados pueden estar esperando a ser escritos antes de que el escritor deje de descansar una centésima de segundo tras cada uno y escriba simplemente tan rápido como pueda. `0` lo deja descansando siempre, como hace el juego |

El juego escribe los chunks terminados en un hilo propio, uno a uno, descansando una centésima de segundo tras cada uno. Eso lo limita a unos cien chunks por segundo sin importar lo rápido que sea el disco, lo cual sobra mientras alguien juega y se queda muy corto mientras se crea terreno en masa, de modo que los chunks sin escribir se acumulan en memoria. `hurryWritesAbove` indica cuántos pueden estar esperando antes de que deje de descansar y escriba simplemente tan rápido como pueda. `100` es el valor por defecto y coincide con el punto en el que el propio juego empieza a frenar la generación; `0` lo deja descansando siempre, como hace el juego. No cambia nada mientras el número de los que esperan es pequeño, que es cada momento normal de juego.

Cada vez que se ejecuta la limpieza se escribe una línea en el momento, que nombra qué barredor se ejecutó, cuánto tardó, qué se retenía antes y después, y cuánto espacio tenía el juego en ese momento. Si ese espacio cambia, se indica, porque el crecimiento del espacio es en sí lo que causa las más largas de estas pausas: un juego iniciado con menos espacio del que acaba necesitando se detendrá para ampliarlo, repetidamente, en momentos que nada tienen que ver con lo que está haciendo. Iniciarlo con todo el espacio que se le permite evita eso por completo.

Una última línea indica cuánto desecho de trabajo se tiró desde la última vez, cuánto tardó su limpieza y cuántos barridos fueron, y cuánto del espacio que se le permite tiene retenido el juego en ese momento. Crear terreno tira muchísimo por su propia naturaleza, ya que cada chunk se convierte en matrices nuevas antes de escribirse, y esa limpieza ocurre entre rondas y no durante ellas, de modo que se nota como un tirón y no como tiempo en ninguno de los recuentos anteriores.

### Chunks de aparición

*qué hace cada grupo*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "spawnChunkRadius": 128,
    "spawnChunkRadii": ["0=64", "7=0"]
  }
}
```

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `spawnChunkRadius` | int, bloques | `128` | A qué distancia del punto de aparición de un mundo, en bloques, se mantienen cargados los chunks haya alguien o no. B bloques mantienen `r = (B + 8) / 16` chunks en cada dirección desde el chunk de aparición, `(2r+1)²` en total, y el mundo prepara `(2r+9)²` chunks a su alrededor al arrancar. `128` es lo que hace el juego, con 289 retenidos y 625 preparados, y `0` no mantiene ni prepara ninguno |
| `spawnChunkRadii` | lista de `dimension=blocks` | ninguno | Un radio para una dimensión cada vez, que sobrescribe `spawnChunkRadius` para las dimensiones nombradas |

El juego mantiene cargados los chunks alrededor del punto de aparición de un mundo haya alguien o no, para que los mods tengan un lugar que siempre hace tick. Son 128 bloques en todas direcciones, unos 289 chunks, y no se puede ajustar en el juego. `spawnChunkRadius` fija esa distancia. `128` es lo que hace el juego y es el valor por defecto, un número menor mantiene un ancla más pequeña, y `0` no mantiene ninguna, de modo que la zona de aparición se descarga como cualquier otra. `spawnChunkRadii` fija un radio para una dimensión cada vez, escrito como `dimension=blocks`, uno por línea, y sobrescribe `spawnChunkRadius` para las dimensiones nombradas.

Solo una dimensión que se registró para mantener su punto de aparición conserva uno, que en el propio juego es únicamente el Overworld; el Nether y el End nunca lo mantuvieron, así que configurarlo para ellos no cambia nada. Una dimensión que añade un mod mantiene uno solo si ese mod lo pidió, y un mod que lo hizo suele arrastrar otros 289 chunks que un pack nunca quiso. Que un mundo se mantenga cargado en absoluto es otra cosa que esto no toca: una dimensión que un mod marcó como siempre cargada sigue cargada con `0`, simplemente deja de mantener chunks. La mayoría de los mods que usan el punto de aparición como ancla quieren que haya algo ahí en lugar de 289 chunks, de modo que un número pequeño suele mantenerlos funcionando mientras que un `0` no.

### Mundo vacío

*qué hace cada grupo*

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

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `voidWorld` | boolean | `false` | Genera un mundo vacío con una plataforma en el punto de aparición, e impide que se generen allí mobs, animales, estructuras y todo lo que un mod generaría de otro modo |
| `voidPlatformBlock` | block | `minecraft:stone` | De qué está hecha la plataforma |
| `voidPlatformSize` | int, bloques | `9` | Qué anchura tiene la plataforma, redondeada hacia abajo a un número impar para que quede centrada en el punto de aparición |
| `voidPlatformHeight` | int | `64` | A qué altura sobre el fondo del mundo está la plataforma |
| `voidWorldDimensions` | lista de ints | `0`, el Overworld | Qué dimensiones se vacían. Solo el Overworld recibe una plataforma |
| `voidWorldDimensionsAreBlacklist` | boolean | `false` | Activado, las dimensiones listadas son las que se dejan como están |

`voidWorld` genera un mundo vacío con una plataforma en el punto de aparición, e impide que se generen allí mobs, animales, estructuras y todo lo que un mod generaría de otro modo. El bloque, el tamaño y la altura de la plataforma son `voidPlatformBlock`, `voidPlatformSize` y `voidPlatformHeight`; el tamaño se redondea hacia abajo a un número impar de bloques para que la plataforma quede centrada en el punto de aparición. `voidWorldDimensions` elige qué mundos se vacían, solo el Overworld por defecto, y `voidWorldDimensionsAreBlacklist` convierte esa lista en las que se deben dejar como están. El Nether y el End se vacían igual que el Overworld, ya sean los que construye esta versión o aquellos por los que un mod los ha sustituido. Solo el Overworld recibe una plataforma, así que una vía de entrada a un Nether o un End vacíos es algo que aporta el propio pack. Un End vacío tampoco tiene dragón, ni cristales, ni fuente de lecho de roca, ya que el combate que los construye no llega a iniciarse.

### El dragón

*qué hace cada grupo*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "dragonFight": true
  }
}
```

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `dragonFight` | boolean | `true` | Si ocurre todo en absoluto: el dragón, su barra, los cristales, la fuente sobre la que se alza y la reaparición que un jugador iniciaría con cristales del End. Pertenece al grupo `structures` |

`dragonFight` pertenece al grupo `structures` y decide si ocurre todo en absoluto: el dragón, su barra, los cristales, la fuente sobre la que se alza y la reaparición que un jugador iniciaría con cristales del End. Un End vacío lo omite salvo que un pack lo pida, y un End normal lo tiene salvo que un pack diga lo contrario, de modo que merece la pena configurar `dragonFight` en un sentido u otro.

### Terreno

*qué hace cada grupo*

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

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `worldType` | string | vacío | El tipo de mundo con el que se crea cada mundo nuevo, sea cual sea el elegido en la pantalla donde se creó: `default`, `largebiomes`, `amplified`, `customized`, o uno que añada un mod. Un servidor dedicado lo escribe en `server.properties` como `level-type` antes de que carguen los mundos, salvo que `level-type` ya nombre uno de los `worldTypeExceptions`. Vacío deja la elección a quien crea el mundo |
| `worldTypeExceptions` | lista de tipos de mundo | `flat`, `debug_all_block_states` | Las opciones que `worldType` deja en pie |
| `worldSeed` | string | vacío | La semilla con la que se crea cada mundo nuevo, escrita como se tecleará: un número se usa tal cual, y cualquier otra cosa se convierte en uno como hace el juego. Un servidor dedicado la escribe en `server.properties` como `level-seed` antes de que carguen los mundos |
| `terrainWorldTypes` | lista de tipos de mundo | ninguno | A qué tipos de mundo se aplican en absoluto los ajustes de terreno. Vacío significa todos |
| `terrainWorldTypesAreBlacklist` | boolean | `false` | Activado, los tipos de mundo listados son los que se dejan como están |

`worldType` decide qué clase de mundo es un mundo nuevo, sea cual sea el elegido en la pantalla donde se creó, `default`, `largebiomes`, `amplified`, `customized`, o uno que añada un mod como `biomesop` o `realistic`. Un pack construido en torno a un tipo de mundo lo nombra aquí y todo mundo nuevo se crea así. Vacío, el valor por defecto, deja la elección a quien crea el mundo. Un mundo que ya existe conserva el tipo con el que se creó, y un nombre que nada aporta se registra en el log y se ignora. `worldTypeExceptions` nombra las opciones que se dejan en pie, plano y el mundo de depuración para empezar, ya que un pack que quiere un solo tipo de mundo rara vez pretende quitarle el superplano a quien está haciendo pruebas, y a quien crea un mundo se le avisa en el chat, una sola vez al entrar, de que el pack eligió su tipo. Ese mensaje lo decide el archivo de configuración con `tellWorldType`, no un pack, de modo que quien juega puede desactivarlo para sí mismo y ningún pack puede volver a activarlo. Los ajustes con los que se creó el mundo se descartan cuando se cambia el tipo, ya que se escribieron para el tipo que se eligió.

`worldSeed` decide la semilla con la que se crea cada mundo nuevo, sea lo que sea lo que se tecleó en la pantalla donde se creó. Se escribe de la misma manera que se teclearía: un número se usa tal cual, y cualquier otra cosa se convierte en un número como hace el juego con una palabra, de modo que `Hollow Ridge` y `-4172144997902289642` están permitidos y dan siempre el mismo mundo. Vacío, el valor por defecto, deja la elección a quien crea el mundo. Un mundo que ya existe conserva la semilla con la que se creó, así que esto solo decide qué recibe uno nuevo. Un pack construido en torno a un mapa nombra aquí su semilla y todo mundo creado con ese pack es ese mapa.

`generatorOptions` da forma al propio Overworld, nivel del mar, océanos de lava y cada ruido del terreno, en el mismo formato que escribe el tipo de mundo personalizado. Se aplica a un mundo al crearlo y nunca después, de modo que un mundo que ya existe se queda exactamente como estaba. Un mundo que ya lleva opciones propias las conserva, y el log nombra la cadena que usó. Un servidor dedicado las escribe en `server.properties` como `generator-settings` antes de que carguen los mundos.

Un tipo de mundo que lleva sus propios ajustes y nunca mira los del mundo, como el realistic de Quark, recibe los ajustes del pack fusionados con los suyos, de modo que la forma para la que se construyó se mantiene salvo que un pack pida otra cosa.

`terrainWorldTypes` nombra los tipos de mundo a los que se aplican los ajustes en absoluto, `default`, `customized`, `biomesop`, `realistic` y así sucesivamente, y `terrainWorldTypesAreBlacklist` lo convierte en la lista de los que se dejan como están. Vacío, que es el valor por defecto, significa todos los tipos de mundo. Un pack que da forma al mundo ordinario pero quiere que el tipo de mundo de un mod quede exactamente como ese mod lo hizo lo nombra aquí y listo: no se fusiona nada, no se entrega nada y la pantalla de personalización del propio mod sigue abierta. Los nombres se comparan con el tipo de mundo con el que se creó un mundo, de modo que nombrar uno que nada de aquí proporciona simplemente nunca coincide y no cuesta nada.

Todo lo que sigue sobre Biomes O' Plenty solo ocurre cuando ese mod está instalado, ya que el trabajo lo hace una compatibilidad que solo se carga cuando está presente. Sin él no hay tipo de mundo `biomesop` que elegir, y un pack que nombra uno se queda con el tipo de mundo con el que realmente se creó el mundo.

En un mundo de Biomes O' Plenty, los mismos ajustes se convierten en las palabras que lee ese mod, de modo que un pack no necesita una segunda copia. `biomeSize` se convierte en uno de sus cinco tamaños, los ajustes de ruido y escala pasan tal cual, y todo lo que nunca lee se omite con una línea en el log que lo indica. Ese mod lee mucho menos que el tipo de mundo personalizado, y nunca lee del todo el nivel del mar, las cuevas, los lagos ni los interruptores de estructuras de sus ajustes, así que se le entregan directamente, y un pack los configura igual que lo haría para cualquier otro mundo.

Hay dos cosas que decide por sí mismo. Los ríos salen de sus propias capas y no tienen ajuste, así que `riverSize` no significa nada ahí. Y dónde están realmente los océanos, las montañas y las regiones también lo deciden sus capas, alcanzables solo mediante `landScheme`, `tempScheme`, `rainScheme` y `biomeSize`, de modo que un pack da forma a ese mundo en los términos de ese mod y no en los del tipo de mundo personalizado. Un mundo de un solo bioma sigue pudiendo crearlo un pack: bloquea todos los biomas y nombra el que quieras como `default` de la plantilla, lo que funciona igual en su tipo de mundo que en cualquier otro.

Todo lo demás que hace un pack, bloquear biomas y minerales, sustituir bloques, lecho de roca plano, colocación de estructuras, su propio worldgen, nunca pasó por esa cadena, y funciona igual en cualquier tipo de mundo.

### Servidor

*qué hace cada grupo*

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

`control.server` decide este grupo: las líneas de `server.properties` que un pack puede fijar, con el modo de juego, la dificultad y los comandos en un mundo abierto a LAN. En un servidor dedicado, cada valor que un pack fija aquí se escribe en `server.properties` al arrancar el servidor, de modo que el archivo indica lo que está en vigor, y los que el servidor ya ha leído también se fijan en él. Un mundo de un jugador toma lo que tiene un servidor integrado, como indica cada fila. Vacío, o `-1` en un número, deja el valor del propio servidor, y con `control.server` en `off` cada línea se queda como la tiene el servidor.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `worldGameMode` | `survival`, `hardcore`, `creative`, `adventure` o `spectator` | vacío | El modo en el que se inicia cada mundo nuevo, aplicado solo al crearlo en un jugador. Un servidor dedicado pone cada mundo en el modo de su `server.properties` en cada arranque, así que allí el modo del pack se escribe en `server.properties` (`gamemode` y `hardcore`) antes de que carguen los mundos. `hardcore` es supervivencia más la marca de hardcore de todo el guardado, y `creative` también activa los trucos |
| `worldLanCommands` | boolean | `true` | Si un jugador que abre un mundo de un jugador a LAN puede activar los comandos para todos los que se unan. `false` atenúa el botón Permitir trucos de la pantalla Abrir a red local y lo mantiene desactivado, y el mundo se abre sin comandos pase lo que pase, `/publish` incluido |
| `worldDifficulty` | lista | ninguno | Bloquea la dificultad en `peaceful`, `easy`, `normal` o `hard`. Una dificultad sola cubre todas las dimensiones, y una línea `dimension=difficulty` la sobrescribe para esa dimensión. Un servidor dedicado escribe la dificultad del Overworld en `server.properties` como `difficulty` |
| `worldForceGameMode` | boolean | vacío | Si un jugador que se une vuelve a ponerse en el modo de juego del servidor cada vez, la línea `force-gamemode`. Un mundo de un jugador abierto a LAN también lo toma |
| `worldPvp` | boolean | vacío | Si los jugadores pueden hacerse daño entre sí, la línea `pvp`. Un mundo de un jugador también lo toma |
| `worldFlight` | boolean | vacío | Si a un jugador que vuela en supervivencia se le deja en paz en lugar de expulsarlo, la línea `allow-flight`. Un mundo de un jugador también lo toma |
| `worldSpawnProtection` | int, -1 o más | `-1` | Cuántos bloques alrededor del punto de aparición solo pueden construir los operadores, la línea `spawn-protection`, 0 para ninguno. Solo un servidor dedicado protege su punto de aparición |
| `worldNether` | boolean | vacío | Si se puede entrar al Nether, la línea `allow-nether`. `false` lo cierra también en un mundo de un jugador |
| `worldCommandBlocks` | boolean | vacío | Si los bloques de comandos se ejecutan, la línea `enable-command-block`. Un mundo de un jugador ya los ejecuta, y `false` los desactiva también ahí |
| `worldIdleTimeout` | int, -1 o más | `-1` | Cuántos minutos puede estar inactivo un jugador antes de ser expulsado, la línea `player-idle-timeout`, 0 para nunca. Un mundo de un jugador también lo toma |
| `worldMotd` | texto | vacío | La línea que se muestra bajo el nombre del servidor en la lista de servidores, la línea `motd`. Un mundo de un jugador abierto a LAN la muestra en lugar del propietario y el nombre del mundo |
| `worldMaxSize` | int, de -1 a 29999984 | `-1` | Lo más lejos, en bloques desde el centro, que puede llegar un borde de mundo, la línea `max-world-size`. Un mundo de un jugador también lo toma |
| `worldStructures` | boolean | vacío | Si un mundo nuevo genera estructuras, la línea `generate-structures` y la opción Generar estructuras de la pantalla del mundo. Solo se aplica a un mundo al crearlo |
| `worldSpawnMonsters` | boolean | vacío | Si aparecen mobs hostiles, la línea `spawn-monsters`. `false` los detiene también en un mundo de un jugador |
| `worldSpawnAnimals` | boolean | vacío | Si aparecen animales, la línea `spawn-animals`. Un mundo de un jugador también lo toma |
| `worldSpawnNpcs` | boolean | vacío | Si aparecen aldeanos, la línea `spawn-npcs`. Un mundo de un jugador también lo toma |
| `worldViewDistance` | int, de -1 a 32 | `-1` | A cuántos chunks de distancia envía un servidor dedicado el mundo a cada jugador, la línea `view-distance`. Un mundo de un jugador sigue en su lugar la distancia de renderizado |
| `worldBuildHeight` | int, de -1 a 256 | `-1` | La y más alta a la que se puede colocar un bloque, la línea `max-build-height`, redondeada a un múltiplo de 16 entre 64 y 256. Un mundo de un jugador también lo toma |

**`worldGameMode`** (grupo `server`): `survival`, `hardcore`, `creative`, `adventure` o `spectator`. Se aplica solo al crear el mundo; los mundos existentes no se tocan, y cambiar el modo más tarde se deja como está. `hardcore` es supervivencia más la marca de hardcore de todo el guardado de vanilla; `creative` también activa los trucos, como haría la casilla de la pantalla de creación. La pantalla de creación se abre con el modo (y la semilla del pack) preseleccionados; un jugador puede cambiarlo ahí, pero el pack lo vuelve a fijar al crear. `adventure` y `spectator` no se ofrecen en esa pantalla y se aplican al crear el mundo.

**`worldLanCommands`** (grupo `server`): `true` (valor por defecto) deja la pantalla Abrir a red local como la tiene vanilla. `false` atenúa su botón Permitir trucos y lo mantiene desactivado, de modo que un jugador que abre el mundo a LAN no puede dar comandos a todos los que se unan. El mundo se abre también sin comandos en el lado del servidor, lo pida quien lo pida, `/publish` incluido. Solo rige la apertura a LAN; un servidor dedicado no se ve afectado.

**`worldDifficulty`** (grupo `server`): `peaceful`, `easy`, `normal` o `hard`. Un valor solo cubre todas las dimensiones; las líneas `dimension=difficulty` (`-1=hard`) sobrescriben por dimensión. El bloqueo se mantiene frente al menú de pausa. Vacío (valor por defecto) deja la dificultad al jugador.

### Registro

*qué hace cada grupo*

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

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `logBlockedOres` | boolean | `true` | Registra la primera vez que se rechaza cada mod y tipo de mineral |
| `logBlockedBiomes` | boolean | `true` | Registra un recuento por mod de qué biomas se bloquearon |
| `logBlockedGenerators` | boolean | `true` | Registra la primera vez que se bloquea cada mod y generador |
| `logBlockedRecipes` | boolean | `true` | Registra un recuento por mod de lo que se bloqueó |
| `logBlockReplacements` | boolean | `true` | Registra la primera vez que se hace cada sustitución, y un total cuando un mundo se pone al día |

`logBlockedOres`, `logBlockedBiomes`, `logBlockedRecipes` y `logBlockReplacements` registran cada uno la primera vez que se rechaza algo, de modo que puedes ver lo que una regla de bloqueo realmente atrapó en lugar de adivinarlo por lo que falta. Son lo primero que hay que activar cuando una regla parece no hacer nada, o hacer demasiado.

### Recetas

*qué hace cada grupo*

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

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `blockRecipes` | boolean | `false` | Elimina todas las recetas de fabricación salvo las de los mods de `recipeWhitelist`. Nada está exento por defecto, así que lista el espacio de nombres de tu propio pack para conservar sus recetas. Las adiciones de CraftTweaker y GroovyScript sobreviven siempre |
| `recipeWhitelist` | lista de ids de mod | `minecraft` | Los mods cuyas recetas de fabricación sobreviven |
| `blockedRecipeMods` | lista de ids de mod | ninguno | Mods cuyas recetas de fabricación se eliminan de plano, diga lo que diga la lista blanca |
| `blockFurnaceRecipes` | boolean | `false` | Lo mismo para las recetas de horno, leyéndose el mod a partir del ítem producido |
| `furnaceWhitelist` | lista de ids de mod | `minecraft` | Los mods cuyas recetas de horno sobreviven |
| `blockedFurnaceMods` | lista de ids de mod | ninguno | Mods cuyas recetas de horno se eliminan de plano |
| `recipeMatch` | `recipe`, `output` o `both` | `recipe` | De dónde se lee el id del mod cuando se bloquean recetas de fabricación: el nombre de la propia receta, el ítem que fabrica, o cualquiera de los dos, que bloquea cuando coincide cualquiera y exime cuando cualquiera está en la lista blanca |

`blockRecipes` y `blockFurnaceRecipes` eliminan todo salvo los mods de sus listas blancas. Nada está exento por defecto, así que lista el espacio de nombres de tu propio pack para conservar sus recetas. Las adiciones de CraftTweaker y GroovyScript sobreviven siempre, diga lo que diga la lista blanca. Las listas blancas son `recipeWhitelist` y `furnaceWhitelist`; `blockedRecipeMods` y `blockedFurnaceMods` van en sentido contrario y eliminan las recetas de un mod nombrado diga lo que diga la lista blanca. `recipeMatch` decide de dónde se lee el id del mod cuando se bloquean recetas de fabricación: `recipe`, el valor por defecto, usa el nombre de la propia receta, `output` usa el ítem que fabrica, y `both` bloquea cuando coincide cualquiera y exime cuando cualquiera está en la lista blanca.

---

# Otros mods

## Universal Tweaks

*otros mods*

Universal Tweaks se solapa con varios de los ajustes de vanilla de este mod. Donde se solapan, este mod se aparta (se registra cada vez, nombrando lo que se omitió) en lugar de que dos mods editen el mismo método.

| Qué se solapa | Cuándo se aparta este mod |
| --- | --- |
| `promptLeafDecay` | Universal Tweaks tiene `Fast Leaf Decay` activado |
| `lenientPaths` | Universal Tweaks tiene `Lenient Paths` activado |
| `cactusMaxHeight` | Universal Tweaks está instalado |
| `caneMaxHeight` | Universal Tweaks está instalado |
| Retorno del portal del Nether | Universal Tweaks está instalado |

Los dos primeros leen los propios interruptores de Universal Tweaks de `config/Universal Tweaks - Tweaks.cfg`, de modo que desactivar uno allí devuelve ese trabajo a este mod. El par de alturas no tiene tal interruptor que leer, solo `Cactus Size` y `Sugar Cane Size`, así que este mod se aparta siempre que Universal Tweaks esté presente y la altura se fija allí.

**Retorno del portal del Nether**: este mod registra dónde entraste al Nether y te devuelve allí, en lugar de la búsqueda del portal más cercano de vanilla. Universal Tweaks tiene su propio tratamiento, así que se omite por completo cuando está instalado.

**Nada de esto toca un pack.** Todo lo anterior trata del cactus, la caña, las hojas, los caminos y los portales propios de Minecraft. Los bloques que define tu pack llevan su propio comportamiento, y los portales de pack bajo `portals/*.json` son un sistema aparte que Universal Tweaks nunca ve.

## Mo' Villages

*otros mods*

Mo' Villages añade biomas de aldea y cambia los materiales de las aldeas, ambas cosas que los packs también pueden fijar. A diferencia de los solapamientos con Universal Tweaks, aquí el pack se queda con la última palabra.

| Qué se solapa | Qué ocurre |
| --- | --- |
| `structureSpacing` para aldeas | Mo' Villages fija su propia separación a partir de `villageDistance` después de que este mod la haya pedido. Si un pack nombró una separación, este mod devuelve su número y lo dice una vez en el log |
| `villageBlocks` | Mo' Villages cambia los materiales de las aldeas por bioma y marca el cambio como final. El mapa de un pack se aplica después de eso, así que el pack gana |
| `structureBiomes` para aldeas | Mo' Villages añade sus biomas a la lista propia del juego. La lista blanca de un pack sigue decidiendo lo que sobrevive |

Nada de esto necesita activarse. Si un pack no indica separación ni mapa de bloques, Mo' Villages se deja a su aire.

Dos cosas que conviene saber cuando ambos están instalados. Mo' Villages fija también `minTownSeparation`, que no hace absolutamente nada en 1.12: el campo se escribe una vez y nunca se lee, ni por el juego ni por este mod. Y los bloques de una aldea los decide Mo' Villages por bioma antes de que se ejecute `villageBlocks`, de modo que mapear tanto el bloque original como el bloque al que Mo' Villages lo cambió abarca una aldea de cualquiera de las dos maneras, `minecraft:cobblestone=...` y `minecraft:brick_block=...` juntos.

## CoFH World

*otros mods*

Los mods que requieren CoFH World cargan sin él, el requisito se elimina automáticamente, salvo los mods que realmente llaman a su API y se cerrarían con un error.

Su propia generación entonces no ocurre, porque CoFH World es lo que lee sus `assets/<modid>/world/*.json`. Se espera que un pack lo cubra.

Si no, `readCofhWorldFiles` lee esos archivos directamente de los jars de los mods y los genera a través de este mod. Está desactivado por defecto, y se aparta cuando está instalado el CoFH World real, que entonces genera con normalidad. Se convierte cada generador y distribución de CoFH que produzca algo, mapeado a las formas y dispersiones de arriba. Las formas son la geometría propia de este mod, así que un lago o una aguja no se verán idénticos. Las listas de estructuras con peso, las tablas de rotación y espejo, las listas de bloques ignorados y el estrechamiento de las estalagmitas se trasladan todos. El estrechamiento se iguala por forma y no por fórmula, de modo que el contorno de una aguja es parecido pero no idéntico.

Traducir los archivos a un pack es la vía admitida, y la única manera de cambiar lo que generan.

## Lost Cities

*otros mods*

Lost Cities sustituye el generador del Overworld por uno propio, de modo que todo lo conectado al generador ordinario dejaría de funcionar en sus mundos. Una compatibilidad que solo se carga cuando Lost Cities está instalado traslada tres cosas:

- `generatorOptions` da forma al terreno entre las ciudades y bajo ellas. Lost Cities lee solo los ajustes de ruido, de modo que el nivel del suelo, el nivel del agua, las cuevas, los lagos y los interruptores de estructuras salen de sus propios perfiles, y `seaLevel` no hace nada en sus mundos; el resumen del log lo indica. `terrainWorldTypes` lo controla como cualquier otro tipo, coincidiendo como `lostcities`.
- Un mundo vacío funciona, incluido el que provoca una lista de biomas totalmente bloqueada. Las ciudades y el terreno desaparecen ambos, y la plataforma y el punto de aparición se comportan igual que en cualquier otro sitio.
- El `stoneBlock` de un bioma de pack sustituye a la piedra bajo él, en cada tipo de paisaje que tiene Lost Cities, normal, flotante, espacial y de caverna.

Las ciudades en sí no son algo que este mod deba cambiar. Su tamaño y frecuencia, de qué están hechos los edificios, el nivel del suelo y del agua, todo vive en los propios archivos de perfil de Lost Cities bajo `config/lostcities`, y su JSON de edificios pasa por su propio ajuste `assets` en el mismo lugar. Un pack que distribuye un mundo de Lost Cities distribuye esos archivos junto con él, igual que distribuye la configuración de cualquier otro mod.

`worldType` fijado en `lostcities` hace que todo mundo nuevo sea un mundo de Lost Cities, igual que lo hace `biomesop` o `realistic`. Forzar un tipo descarta los ajustes que habría llevado el mundo, de modo que el mundo acaba en el perfil por defecto de Lost Cities, y `defaultProfile` en `config/lostcities/general.cfg` nombra cuál es. A la inversa, un pack que fuerza un tipo distinto le quita Lost Cities a un jugador que lo eligió, de modo que un pack que pretende dejar esa elección abierta añade `lostcities` a `worldTypeExceptions`.

Todo lo demás nunca pasó por el generador y funciona igual que en cualquier otro sitio: el worldgen del pack, el bloqueo de minerales y biomas, la separación de estructuras y los spawners, el lecho de roca plano, el retrogen, la pregeneración, y sus dos tablas de botín de cofres se sobrescriben e inyectan como cualesquiera otras.

## Integración con Blast Plaster

*otros mods*

`<namespace>/blastplaster/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se apilan.

Blast Plaster (una dependencia de este mod) se encarga del comportamiento posterior a una explosión: curar cráteres bloque a bloque, tala consciente de los árboles, control de drops. Por sí solo lee una única configuración global. Dirigido desde un pack responde **por dimensión**, y el pack aporta la decisión en lugar de pedir a los jugadores que editen una configuración. La tala de árboles de las aldeas también reutiliza su geometría de árboles, que es la razón por la que un árbol sobre un camino nuevo cae entero. Sin archivos de pack, Blast Plaster se comporta exactamente como si estuviera instalado solo.

Las claves escritas en el nivel superior del archivo se aplican en todas partes; un bloque `dimensions` las sobrescribe para una dimensión por id. Todo lo que un pack nunca nombra conserva lo que diga la propia configuración de Blast Plaster, de modo que un pack fija las pocas que le importan y deja el resto en paz.

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita.

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

`explosionMode` es el interruptor principal: `HEAL` restaura el cráter con el tiempo, `EJECT_DROPS` deja el agujero y suelta aproximadamente un tercio de los bloques (comportamiento vanilla), `VISUAL_TOSS` deja el agujero y no suelta nada. Cuando lo dirige un pack, el valor por defecto es `EJECT_DROPS` (no el `HEAL` de Blast Plaster), de modo que una instalación sin configurar se comporta como vanilla.

| Clave | Valor | Qué hace |
| --- | --- | --- |
| `explosionMode` | `HEAL`, `EJECT_DROPS`, `VISUAL_TOSS` | Qué ocurre después del estallido |
| `healCreepers`, `healNonPlayerTNT`, `healWither`, `healAll` | true o false | Qué explosiones se tratan en absoluto |
| `processPlayerIgnitedTNT` | true o false | Si el TNT que encendió un jugador se trata junto con el resto |
| `customEntitiesToHeal` | lista de nombres de entidad | Explosiones de otros mods, nombradas como `modid:entity` |
| `healFullTrees` | true o false | Un árbol alcanzado por una explosión se lleva o se restaura entero, en lugar de cortarse a medias |
| `maxTreeSize` | número | El máximo de bloques que puede reclamar un árbol antes de dejarse en paz |
| `minimumTicksBeforeHeal`, `randomTickVar` | números | Cuánto tarda en empezar la reparación, y cuán irregular es su ritmo |
| `overrideBlocks` | true o false | Si la reparación sobrescribe lo que se ha construido desde entonces en el agujero |
| `enableFakeTossedBlocks` | true o false | Los escombros que salen volando de la explosión |
| `enableExplosionFlash` | true o false | El destello brillante en el momento de la explosión |
| `explosionFlashDuration`, `explosionFlashLightLevel`, `explosionFlashParticleCount`, `explosionFlashPulses` | números | Cuánto dura el destello, con qué intensidad arde, cuántas partículas lanza y cuántas veces pulsa |
| `enableExplosionSmoke` | true o false | La columna de humo posterior |
| `explosionSmokeDuration`, `explosionSmokeParticleCount` | números | Cuánto persiste el humo y cuán denso se alza |
| `playerTNTAlwaysDrops`, `playerTNTDropFullBlocks` | true o false | Qué deja tras de sí el TNT de un jugador |
| `enableDropSuppression`, `dtSpecialDrops` | true o false | Los drops dentro de una explosión, y los drops propios de Dynamic Trees |
| `preventMobDrops` | true o false | Si los mobs muertos por una explosión sueltan drops de todos modos |
| `blockConversions` | lista de reglas | En qué se convierte un bloque alcanzado en lugar de volver como era, de modo que una construcción se desgasta un paso por explosión |

`blockConversions` decide en qué se convierte un bloque alcanzado en lugar de volver como era. Una regla se lee `<source>=<result>[@chance]`: el origen es un id de bloque, un id de bloque con un meta (`minecraft:log:1`), o un nombre del diccionario de minerales con un `#` delante; el resultado es un id de bloque, un id de bloque con un meta, o `nothing` para dejar el espacio vacío; la probabilidad va de 0.0 a 1.0 y por defecto es 1.0. La primera regla que coincide gana, así que las reglas específicas van encima de las generales, y un bloque que ya es el resultado de alguna regla nunca se vuelve a convertir: un muro cede un paso por explosión en lugar de desgastarse hasta la nada.

**Aspecto totalmente vanilla:** `EJECT_DROPS` más `healFullTrees`, `enableFakeTossedBlocks`, `enableExplosionFlash`, `enableExplosionSmoke`, `preventMobDrops` y `playerTNTAlwaysDrops`, todos desactivados. Cada clave admite configuración por dimensión.

**Los clientes vanilla** no ven nada fuera de lo normal. El destello es la única característica que coloca un bloque, así que con `vanillaClients` activado se fuerza a desactivado; todo lo demás son partículas e ítems que un cliente normal entiende.

No son claves de pack: el registro de depuración de Blast Plaster y su emparejamiento de troncos con hojas (la identificación de árboles debe ser una única respuesta en todo el juego). Ambos permanecen en la propia configuración de Blast Plaster.

## Mods de tumbas

*otros mods*

No hace falta configuración. Los ítems de `player_loot` se unen a los drops de muerte ordinarios antes de que cualquier mod de tumbas los lea, de modo que acaban en la tumba con el inventario: funciona con Gravestone, GraveStone Mod, Corail Tombstone y cualquier otro que lea la lista de drops de la muerte. Por entrada, `dropLoose` evita la lista de drops para que los ítems queden en el suelo para el asesino en lugar de ir a la tumba. Claves y la salvedad de `dropLoose`: [Botín de jugadores](#botín-de-jugadores).

---

# Referencia

## Listas de valores

*referencia*

Estos son los nombres que acepta el analizador dondequiera que las tablas de arriba dicen «uno de los materiales», y así sucesivamente. Todo lo que no se reconoce se registra en el log y se sustituye por el valor por defecto.

### Nombres aceptados

*listas de valores*

**Materiales de bloque.** `air`, `grass`, `ground`, `wood`, `rock`, `iron`, `anvil`, `water`, `lava`, `leaves`, `plants`, `vine`, `sponge`, `cloth`, `fire`, `sand`, `circuits`, `carpet`, `glass`, `redstone_light`, `tnt`, `coral`, `ice`, `packed_ice`, `snow`, `crafted_snow`, `cactus`, `clay`, `gourd`, `dragon_egg`, `portal`, `cake`, `web`, `piston`, `barrier`, `structure_void`.

**Tipos de sonido.** `wood`, `ground`, `plant`, `stone`, `metal`, `glass`, `cloth`, `sand`, `snow`, `ladder`, `anvil`, `slime`.

**Colores de mapa.** `air`, `grass`, `sand`, `cloth`, `tnt`, `ice`, `iron`, `foliage`, `snow`, `clay`, `dirt`, `stone`, `water`, `wood`, `quartz`, `adobe`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `silver`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, `black`, `gold`, `diamond`, `lapis`, `emerald`, `obsidian`, `netherrack`.

**Capas de renderizado.** `solid`, `cutout`, `cutout_mipped`, `translucent`. Si se deja vacío, el bloque elige una adecuada a su tipo.

**Rarezas.** `common`, `uncommon`, `rare`, `epic`.

**Partículas de antorcha.** `none`, `flame`, `colored`. `colored` usa `particleColor`.

**Clases de herramienta.** `pickaxe`, `axe`, `shovel`, `sword`.

**Ranuras de armadura.** `head` o `helmet`, `chest` o `chestplate`, `legs` o `leggings`, `feet` o `boots`.

**Tintes.** `biome`, `none`, o un color hexadecimal de seis dígitos. Los colores en cualquier parte de una definición son hexadecimales, con o sin un `#` inicial.

**Comportamientos** para `behavesAs`. `till`, `path`, `bush`, `animals`.

**Estructuras** para una plantilla de mundo, y para las propias listas del grupo `structures`. `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges`, `endcities`, `caves`, `ravines`, y `reccomplex`, que desactiva todo lo que Recurrent Complex genera por sí mismo, sus estructuras naturales y sus sustitutos de decoración, dejando intacto lo que ya está en pie en el mundo. Otras ocho nombran lo que coloca el paso de población en lugar de un generador de estructuras: `dungeons`, `waterlakes`, `lavalakes`, `netherlava`, `fire`, `glowstone`, `ice` y `animals`.

**Tipos de criatura** para apariciones y tasas de bioma. `creature`, `monster`, `ambient`, `water_creature`.

**Roles** para los `roles` de una plantilla de mundo. `ocean`, `river`, `beach`, `mushroom`, `swamp`, `hills`, `mountain`, `jungle`, `forest`, `savanna`, `sandy`, `mesa`, `snowy`, `wasteland`, `plains`, `water`. Cada uno nombra un bioma que cumple ese papel una vez que el bloqueo ha eliminado los que lo habrían hecho.

**Tipos de mineral** para `oreTypes`. `COAL`, `IRON`, `GOLD`, `REDSTONE`, `DIAMOND`, `LAPIS`, `EMERALD`, `QUARTZ`, `DIRT`, `GRAVEL`, `DIORITE`, `GRANITE`, `ANDESITE`, `SILVERFISH`, `CUSTOM`.

### Ajustes del mundo

*listas de valores*

Las claves de `terrain` de abajo, juntas en el bloque `settings` de una plantilla de mundo:

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

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `worldName` | string | vacío | Rellena de antemano el cuadro de nombre de la pantalla de crear mundo, y la carpeta del guardado se deduce de él. Solo rellena el cuadro mientras este siga con el valor por defecto del juego y, a diferencia de la semilla y el modo de juego, no se vuelve a aplicar después |
| `worldSpawn` | `x,z` o `x,y,z` | vacío | Dónde aparece cada mundo nuevo, aplicado solo al crear. Sin una y se usa la superficie al nivel del suelo del tipo de mundo |
| `worldBorder` | int, bloques | `0` | El diámetro del borde que recibe cada mundo nuevo, la cifra que toma `/worldborder set`. `0` deja el borde como está |
| `worldTime` | int, ticks | `-1` | La hora del día a la que empieza cada mundo nuevo. `-1` la deja como está |
| `weatherCeiling` | lista de `dimension=y` | ninguno | La y más alta a la que llegan la lluvia y la nieve. Un número solo cubre todas las dimensiones |
| `cloudHeight` | lista de `dimension=y` | ninguno | La y a la que se dibujan las nubes. Un número solo cubre todas las dimensiones, y vacío conserva la altura propia del juego |

**`worldName`** (grupo `terrain`) rellena de antemano el cuadro de nombre de la pantalla de crear mundo; la carpeta del guardado se deduce de él como de costumbre. Solo rellena el cuadro mientras este siga con el valor por defecto del juego, de modo que un nombre tecleado por el jugador nunca se sobrescribe, y a diferencia de la semilla y el modo de juego no se vuelve a aplicar después: lo que haya en el cuadro al crear es el nombre.

**`worldSpawn`** (grupo `terrain`): `x,z` o `x,y,z`. Se aplica solo al crear. Sin una y se usa la superficie al nivel del suelo del tipo de mundo. Las entradas no enteras se notifican y se ignoran. Es relevante sobre todo en superplano: la búsqueda de aparición de vanilla busca hierba al nivel del mar, nunca la encuentra en una pila de capas y puede vagar cientos de bloques; `worldSpawn` lo fija.

**`worldBorder`** (grupo `terrain`): diámetro del borde en bloques, la cifra que toma `/worldborder set`. Se aplica al crear; `0` (valor por defecto) deja el borde como está; se puede seguir moviendo por comando después. `worldBorderLimit` en la configuración limita lo que un pack puede pedir: un pack que pide más es rechazado y registrado, no recortado, de modo que un pack no puede imponer a un servidor un borde con el que el operador no estuvo de acuerdo.

**`worldTime`** (grupo `terrain`): un valor en ticks como el que toma `/time set` (`18000` medianoche, `6000` mediodía). Bloquea el reloj del Overworld; todo lo que lee la hora del día (aparición de mobs, dormir) ve el valor bloqueado. `-1` (valor por defecto) deja correr el tiempo. El análogo para el Overworld del `fixedTime` de una dimensión personalizada, e independiente de `doDaylightCycle`.

**`cloudHeight`** (grupo `terrain`): la y a la que se dibujan las nubes. Un número solo cubre todas las dimensiones; las líneas `dimension=y` (`0=384`) sobrescriben por dimensión. Es lo que fija un pack con edificios altos para que el horizonte urbano quede bajo las nubes en lugar de atravesarlas, y en un mundo rubic es una y absoluta, así que un techo elevado es el lugar para poner las nubes por encima. Vacío (valor por defecto) conserva la altura propia del juego, 128 en el Overworld, desplazada hacia arriba con `terrainOffset` en un mundo rubic.

**`weatherCeiling`** (grupo `terrain`): la y más alta a la que llegan la lluvia y la nieve. Un número solo cubre todas las dimensiones; las líneas `dimension=y` (`0=128`) sobrescriben por dimensión. Por encima de ella la lluvia no cae, la nieve no se asienta, los calderos no se llenan, los rayos no caen y no se dibuja ninguna precipitación; por debajo, el clima no cambia. Vacío (valor por defecto) significa sin techo. El hielo es temperatura y no precipitación, así que sigue formándose por encima de la línea.

### Física del mundo

*listas de valores*

**Física del mundo**: cuatro claves de `terrain`, cada una un multiplicador del valor vanilla (`1.0` = sin cambios), cada una admitiendo un valor solo para todas las dimensiones o sobrescrituras `dimension=value`:

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

| Ajuste | Escala | Notas |
| --- | --- | --- |
| `worldGravity` | La aceleración de caída de jugadores, mobs, ítems soltados, bloques en caída, flechas, entidades lanzadas, TNT y esferas de XP | `0.17` es similar a la Luna; los arcos de salto y los alcances de los proyectiles se ajustan automáticamente |
| `worldFallDamage` | El daño por caída | Una dimensión de baja gravedad suele querer que este coincida |
| `worldJumpStrength` | La velocidad de salto | Se aplica además del cambio de gravedad |
| `worldTerminalVelocity` | La velocidad máxima de caída, como proporción del límite vanilla | El vuelo con elytra no se toca |

Con los cuatro vacíos (valor por defecto) se mantiene la física vanilla. En las dimensiones de Galacticraft, la clave de gravedad escala la propia gravedad de Galacticraft.

### Costuras del mundo

*listas de valores*

**Costuras del mundo**: apilan dimensiones en vertical. Al salir de un mundo por el suelo o por el techo, la entidad pasa a la dimensión de abajo o de arriba, con las mismas x y z.

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

| Ajuste | Valor | Por defecto | Qué hace |
| ------------------- | ---------------------------------------------------------- | ------- | --------------------------------------------------------------------------------------------------------- |
| `worldBelow` | líneas `dimension=destino`, o un id suelto para todas las dimensiones | ninguno | Dimensión a la que se entra al caer por debajo del suelo del mundo |
| `worldAbove` | igual | ninguno | Dimensión a la que se entra al subir por encima del límite generado, es decir, el techo del Nether y no su límite de construcción |
| `worldSeamEntities` | booleano | `true` | Si cruzan también ítems, mobs y otras entidades, o solo los jugadores |
| `worldSeamBedrock` | booleano | `false` | Conserva el lecho de roca en el límite de la costura. Desactivado, el límite no genera ninguno y se puede excavar para pasar |

Si ambas listas están vacías (lo predeterminado), todos los mundos permanecen cerrados. La capa de bloques más externa de un mundo es su umbral: entrar en la capa inferior te lleva hacia abajo, entrar en la superior te devuelve hacia arriba. Las llegadas aterrizan fuera de ella, tres capas hacia dentro al bajar y una al subir, de modo que nada rebota de vuelta de inmediato. Al bajar también se abren las capas situadas sobre el punto de llegada hasta el umbral, de forma que la entrada sigue siendo visible desde abajo y sirve de camino de vuelta.

Si rompes un bloque en una capa de umbral, el mundo del otro lado se ve a través de ella: el cielo de la dimensión inferior aparece bajo el suelo y el de la superior aparece sobre el techo. Esto se dibuja solo en el cliente, dentro de la distancia de renderizado, y no cambia nada del mundo en sí. El impulso se conserva al cruzar.

Los cruces de un jugador se recuerdan. Al bajar se marca el agujero, y al volver a subir cerca de él aterrizas donde ese agujero te dejó la última vez, de modo que un pozo que usas a menudo te devuelve siempre al mismo lugar conocido y no a uno nuevo. La primera vuelta calcula el aterrizaje: ese lugar si tiene suelo debajo; si no, el espacio más cercano donde se pueda estar de pie, buscando hacia fuera desde la costura a una altura cada vez; y si tampoco, un hueco excavado en el borde justo al lado del agujero, ya que un pozo cavado en vertical aún no tiene ningún saliente propio. Si subes por un sitio sin ningún agujero tuyo cerca, simplemente se crea un nuevo aterrizaje allí. Si llegas desde abajo y no hay ningún lugar transitable cerca, se recurre a la superficie de esa columna. Los pies y la cabeza se despejan si el lugar está dentro de la roca, rompiendo esos bloques como es debido para que suelten su botín, contenedores incluidos.

Las cadenas se apilan dando a cada dimensión sus propias líneas, y jinetes y monturas cruzan por separado.

Los portales se aplican a los jugadores. Un jugador que no ha desbloqueado el destino recibe el mensaje de rechazo del portal y es devuelto al último suelo en el que estuvo, o a un saliente cerca de la costura; las costuras no colocan bloques, así que un pozo bloqueado no se puede aprovechar cayendo por él. Los ítems y los mobs no tienen portal propio: con `worldSeamEntities` activado cruzan sin importar quién los perdió, y desactivado caen por un suelo abierto y se pierden como en cualquier agujero. `worldSeamBedrock` sella el suelo en su lugar, y un pack que conserva su lecho de roca aporta él mismo el paso, normalmente con una [sobrescritura de propiedades](#sobrescritura-de-propiedades) que da a `minecraft:bedrock` una `hardness` positiva. Los chunks generados antes de la costura conservan el lecho de roca que ya tienen.

**Mundos Rubic**: `rubicWorld`, `worldMinHeight`, `worldMaxHeight`, `rubicWorldDimensions`, `rubicWorldDimensionsAreBlacklist` y `terrainOffset` también son claves de `terrain`: véase [Mundos Rubic](#mundos-rubic).

## Lista de carpetas

*referencia*

Todas las carpetas, con su ruta completa y un enlace a la sección que la describe, están en [Dónde van los archivos](#dónde-van-los-archivos).

## Comandos

*referencia*

### Tus propios comandos

*comandos*

`/rdpl` se ejecuta en tu propio equipo y no necesita permisos, porque todo lo que toca es tuyo. Una recarga vuelve a escanear la carpeta que posees, reaplica tus [sobrescrituras de propiedades](#sobrescritura-de-propiedades) a tu propia copia de los bloques y los ítems, y refresca tus propios recursos; no llega a ningún servidor, así que la copia del servidor se recarga con `/rdplserver reload`. En un jugador las dos son la misma máquina, por lo que `/rdpl reload` también recarga las tablas de botín, los avances y las funciones del servidor integrado, igual que la recarga propia de vanilla. Funciona en cualquier servidor, tenga o no el mod.

| Comando | Nivel | Qué hace |
| ------------------------------------------------------------------------------------- | ------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/rdpl list` | ninguno | Todos los packs cargados, su prioridad y lo que contienen. Haz clic en un pack para buscar un archivo en él |
| `/rdpl which <namespace:path>` | ninguno | Qué pack proporciona un archivo dado y a qué packs tapa |
| `/rdpl reload` | ninguno | Vuelve a escanear la carpeta y recarga todo |
| `/rdpl reload <group>` | ninguno | Recarga solo un tipo: `textures`, `models`, `languages`, `sounds` o `shaders` |
| `/rdpl unused` | ninguno | Archivos de tus packs que nadie ha pedido todavía, normalmente por un error al escribir una ruta |
| `/rdpl config unused` | ninguno | Archivos de opciones en `rdploader/config` que ningún pack instalado define ya |
| `/rdpl config prune` | ninguno | Elimina esos archivos |
| `/rdpl pixelmap <namespace:path>` | ninguno | En qué resultó un [mapa de píxeles](#texturas-escritas-como-mapas-de-píxeles), carácter por carácter |
| `/rdpl biome list` | ninguno | Todos los biomas que pueden generarse y su id |
| `/rdpl biome here` | ninguno | El bioma en el que estás |
| `/rdpl biome find <name>` | el del servidor | Enlazado. Se pasa a `/rdplserver biome find`, el único lado que conoce la semilla |
| `/rdpl team` | ninguno | Los bandos que ha puesto en juego un pack, cada uno con su color, a cuál perteneces y quién lidera cada uno |
| `/rdpl team join [name]` | ninguno | Únete a un bando. Solo se ofrecen los bandos que un pack deja abiertos; no puedes entrar en un bando de mobs. Si omites el nombre, se te asigna al bando con menos jugadores entre los que admiten jugadores por `balance` |
| `/rdpl team leave` | ninguno | Abandona el bando en el que estás |
| `/rdpl team vote <player>` | ninguno | Vota por quién lidera tu bando, cuando el pack elige a su líder por votación. Un empate deja el bando sin líder |
| `/rdpl team claim` | ninguno | Toma el liderazgo de tu bando, cuando el pack permite reclamarlo y nadie en el bando lo ejerce |
| `/rdpl round start` | ninguno | Inicia la ronda, cuando el pack la mantiene en un vestíbulo (`opens.by`). Para el líder de un bando o un operador |
| `/rdpl round reset` | ninguno | Reinicia la ronda en curso, o convoca una votación para ello, según lo que permita el `reset` del pack. Para el líder de un bando, un jugador de un bando al que el pack permita convocar una votación, o un operador |
| `/rdpl round vote yes`, `no` | ninguno | Vota en una votación en curso para reiniciar la ronda. Para un jugador que pertenezca a un bando |
| `/rdpl oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro`, `goto`, `vein`, `game` | el del servidor | Enlazados. Se pasan palabra por palabra a `/rdplserver`, que decide, así que consulta la tabla siguiente |

**Qué subcomandos del servidor están enlazados y por qué los demás no.** Un subcomando del servidor recibe un paso directo exactamente cuando el cliente no tiene ningún significado propio para ese nombre: `oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro`, `goto`, `vein`, `team` y `game` solo pueden significar lo que significan en el servidor, así que `/rdpl` se los entrega. Los seis que el cliente también tiene, `reload`, `list`, `which`, `unused`, `config` y `biome`, conservan su propio significado sobre tus packs y tu cliente, y reenviarlos se lo quitaría. `biome find` es la única parte de un nombre compartido que pertenece al servidor de todos modos, ya que solo el servidor conoce la semilla del mundo, así que esa única forma se pasa mientras que `biome list` y `biome here` se quedan contigo. Eso también resuelve el permiso: lo decide la propia comprobación de operador del servidor, y un cliente no puede ni burlarla ni recibir una respuesta falsificada.

**`/rdpl` también llega al comando del servidor.** Todo lo que `/rdpl` no gestiona por sí mismo, `oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro`, `goto`, `vein`, `team` y `game`, se pasa directamente a `/rdplserver` y se ofrece en el autocompletado con tabulador, de modo que hay un único comando que escribir en un jugador. Se pasa palabra por palabra y el servidor decide como siempre, permisos incluidos, así que escribir el nombre más corto no abre nada. Los subcomandos que tienen ambos, `reload`, `list`, `which`, `unused`, `biome` y `config`, se quedan con `/rdpl` y significan los packs propios del cliente. `biome find` es la única excepción dentro de un nombre compartido: solo el servidor conoce la semilla del mundo, así que esa forma se pasa mientras que `biome list` y `biome here` responden desde tu propio cliente.

**Edición del día a día:** `/rdpl reload textures` es mucho más rápido que F3+T en un modpack grande. F3+T sigue funcionando y lo recarga todo. Usa un simple `/rdpl reload` cuando *añadas* o *elimines* un archivo, ya que eso cambia lo que contiene la carpeta.

### Comandos de servidor

*comandos*

En un servidor dedicado, `/rdplserver` hace lo mismo con la copia de la carpeta del propio servidor. La columna Nivel es el nivel de permiso que necesita quien lo envía: `3` es un operador, `2` admite también bloques de comandos, `0` es cualquier jugador y `4` está por encima de operador y no alcanza a nadie. Solo `intro`, `team`, `card` y las tres formas de `goto` están abiertos por debajo de operador, y `goto` es el que un pack puede modificar.

#### Packs y archivos

*comandos de servidor*

| Comando | Nivel | Qué hace |
| ------------------------------------ | ----- | -------------------------------------------------------------------------- |
| `/rdplserver reload` | 3 | Vuelve a escanear la carpeta del servidor y recarga todo |
| `/rdplserver list` | 3 | Todos los packs que cargó el servidor, su prioridad y lo que contienen |
| `/rdplserver which <namespace:path>` | 3 | Qué pack proporciona un archivo dado y a qué packs tapa |
| `/rdplserver unused` | 3 | Archivos de los packs del servidor que nadie ha pedido |
| `/rdplserver config unused` | 3 | Archivos de opciones en `rdploader/config` que ningún pack instalado define ya |
| `/rdplserver config prune` | 3 | Elimina esos archivos |

#### Mundo y generación

*comandos de servidor*

| Comando | Nivel | Qué hace |
| ----------------------------------- | ----- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/rdplserver oregen` | 3 | Totales acumulados de la generación de minerales que se bloqueó, por mod y tipo |
| `/rdplserver generators` | 3 | Totales acumulados de los generadores de mundo que se bloquearon, por mod y tipo |
| `/rdplserver biome` | 3 | Todos los biomas que pueden generarse en el servidor |
| `/rdplserver biome list [all]` | 3 | Lo mismo con el id de cada bioma; `all` incluye los que nada puede generar |
| `/rdplserver biome here` | 3 | El bioma en el que estás. La consola no está en ninguna parte, así que desde ella se pide un jugador en su lugar |
| `/rdplserver biome here <player>` | 3 | El bioma en el que está ese jugador, que es la forma que quieren la consola y los scripts |
| `/rdplserver biome find <name>` | 3 | El lugar más cercano donde se genera un bioma, sin generar chunks para buscarlo |
| `/rdplserver dimensions` | 3 | Todas las dimensiones, incluidas las que añadieron los packs |
| `/rdplserver vein <entry> [radius]` | 3 | Dónde tiene sembradas sus vetas una entrada de worldgen de forma `vein` dentro de ese número de chunks (8 por defecto) desde donde se ejecuta, empezando por las más cercanas, existan ya o no esos chunks. `/rdpl vein` lo reenvía a este |

#### Comandos de portales

*comandos de servidor*

| Comando | Nivel | Qué hace |
| ----------------------------------------- | ----- | --------------------------------- |
| `/rdplserver gate list` | 3 | Todos los portales y si están abiertos |
| `/rdplserver gate check <player>` | 3 | Qué portales ha superado un jugador |
| `/rdplserver gate grant <player> <gate>` | 3 | Abre un portal para un jugador |
| `/rdplserver gate revoke <player> <gate>` | 3 | Vuelve a cerrarlo |

#### Comandos de pregeneración

*comandos de servidor*

| Comando | Nivel | Qué hace |
| ------------------------------------- | ----- | ------------------------------------------------------------------------------------------------ |
| `/rdplserver pregen <radius>` | 3 | Crea todos los chunks dentro de ese número de chunks desde donde se ejecuta. Véase [Pregeneración](#pregeneración) |
| `/rdplserver pregen <radius> relight` | 3 | Ejecuta solo la pasada de iluminación sobre el terreno que ya existe |
| `/rdplserver pregen status` | 3 | Cuánto lleva avanzada una ejecución |
| `/rdplserver pregen stop` | 3 | La termina |

#### Jugadores, equipos y rondas

*comandos de servidor*

| Comando | Nivel | Qué hace |
| ---------------------------------------------------------------------------------------- | ----- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/rdplserver intro` | 0 | Hace que la introducción al mundo se reproduzca de nuevo la próxima vez que te unas. Cualquier jugador puede ejecutarlo y solo borra lo suyo |
| `/rdplserver team`, `team join [name]`, `team leave`, `team vote <player>`, `team claim` | 0 | Lo mismo que las formas de `/rdpl team` de arriba, que se pasan a estas |
| `/rdplserver round start` | 0 | Lo mismo que `/rdpl round start`, que se pasa a este |
| `/rdplserver round reset`, `round vote yes`, `round vote no` | 0 | Lo mismo que las formas de `/rdpl round` de arriba, que se pasan a estas |
| `/rdplserver card <rule> [players]` | 2 | Muestra una [regla de tarjeta](#tarjetas) a los jugadores indicados, o a ti mismo, por su id o nombre de archivo. `when`, `repeat` y `cooldown` se omiten |
| `/rdplserver reset` | 3 | Deja el mapa como lo hace el final de una ronda: todos quedan retenidos, se barren las entidades, se borran las puntuaciones, se ejecutan los `resetRuns`, los jugadores se llevan a `resetSendsTo` y se liberan, y se abre una ronda con el recuento inicial, como describen los ajustes de reinicio de [Pregeneración](#pregeneración). No se pasa desde `/rdpl` |

#### Ir a lugares

*comandos de servidor*

| Comando | Nivel | Qué hace |
| ----------------------------------- | -------------------- | --------------------------------------------------------------------------------------------------------------------- |
| `/rdplserver goto <structure>` | `gotoLevel`, `3` | Te lleva a la más cercana a la que nadie ha ido todavía, buscando sin generar el terreno por el camino |
| `/rdplserver goto <structure> next` | `gotoNextLevel`, `3` | Te lleva a la siguiente más cercana a la que no te han llevado en esta sesión, haya sido visitada antes o no |
| `/rdplserver goto <structure> back` | `gotoBackLevel`, `3` | Te lleva a la anterior, retrocediendo por los lugares a los que te ha enviado esta sesión |

#### Juegos

*comandos de servidor*

| Comando | Nivel | Qué hace |
| --- | --- | --- |
| `/rdplserver game coin` | 0 | Lanzar una moneda. Cara cuenta como 1, cruz como 0 |
| `/rdplserver game die <sides>` | 0 | Tirar un dado de 2 a 1000 caras |
| `/rdplserver game die <name>` | 0 | Tirar un [dado del pack](#dados-y-mazos) según sus pesos |
| `/rdplserver game dice <roll>` | 0 | Tirar hasta 100 dados y sumarlos, como `2d6`, `d20` o `3d8-2`. Se muestra cada dado |
| `/rdplserver game advantage [roll]`, `disadvantage [roll]` | 0 | Tirar dos veces y quedarse con el total mayor, o el menor. Sin indicarla, la tirada es `1d20` |
| `/rdplserver game pick player` | 0 | Elegir al azar un jugador conectado |
| `/rdplserver game pick team [team]` | 0 | Elegir al azar un equipo del marcador, o un miembro conectado del equipo nombrado |
| `/rdplserver game deck draw <name>` | 0 | Robar una carta de lo que queda de un mazo del pack |
| `/rdplserver game deck left <name>` | 0 | Cuántas cartas le quedan al mazo |
| `/rdplserver game deck shuffle <name>` | 2 | Devolver todas las cartas |
| `/rdplserver game teamroll [roll]` | 0 | Todos en el bando del remitente tiran y gana el mayor, con el empate sorteado. Sin equipos, el remitente tira solo |
| `/rdplserver game tiebreak [objective]` | 2 | Sortear uno de los bandos empatados en cabeza de un objetivo: el nombrado, si no el primer objetivo de puntuación con `tiebreak`, si no el primero |
| `/rdplserver game board list` | 0 | Cada tablero del mundo, con su juego, su posición y su estado |
| `/rdplserver game board start <game> <board> [x y z]` | 2 | Montar un tablero donde está quien lo envía, o en la posición indicada, y colocar sus piezas |
| `/rdplserver game board end <board>` | 2 | Quitar un tablero y sus piezas |
| `/rdplserver game board show <board>` | 0 | La posición en letras, fila por fila, con quién tiene cada bando, su tiempo y a quién le toca |
| `/rdplserver game board move <board> <from> <to> [piece]` | 0 | Mover una pieza por nombres de casilla, como `e2 e4`, con la pieza en que se convierte una promoción |
| `/rdplserver game board resign <board>` | 0 | Abandonar la partida |
| `/rdplserver game board draw <board>` | 0 | Ofrecer tablas, o aceptar la oferta del otro bando |
| `/rdplserver game board takeback <board>` | 0 | Pedir deshacer la última jugada, o aceptar la petición del otro bando |
| `/rdplserver game board ai <board> <side> <level>` | 2 | Hacer que el ordenador juegue un bando a un nivel de 1 a 4, o devolverlo con 0 |
| `/rdplserver game last [count]` | 0 | Las últimas tiradas, la más reciente primero: 10, o la cantidad indicada hasta 50 |
| `/rdplserver game pass` | 0 | Termina antes de tiempo el turno del bando de quien lo envía cuando un archivo de puntuación juega por turnos. Desde un bloque de comandos o la consola termina el turno de quien esté jugando |

Cualquier tirada puede terminar con `store <objective>`, que escribe su número en la puntuación propia del remitente en ese objetivo, y con `audience <quién>`, que sustituye el valor por defecto del pack: `self`, `team` (el bando del remitente, o solo el remitente sin equipos), `all`, `radius <bloques>` (jugadores del mismo mundo a esa distancia) o `silent`, que solo guarda. `/rdpl game` se pasa a este comando.

### Quién puede usar goto

*comandos*

**Abrir `goto` a más gente.** Todo `/rdplserver` necesita un operador, nivel 3, salvo `intro` y `team`, que son comandos propios del jugador y siempre de nivel 0, y `card`, que es de nivel 2 para que un bloque de comandos pueda mostrar una tarjeta, y `game`, cuyas partes llevan [niveles propios](#quién-puede-usar-game). Las tres formas de `goto` son lo único que decide un pack: cada una lleva un nivel de permiso propio que un pack o la configuración pueden bajar, por separado de las otras dos y del resto del comando.

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

| Ajuste | Qué gobierna |
| ----------------- | ----------------------------------- |
| `gotoLevel` | `goto <structure>` |
| `gotoNextLevel` | `goto <structure> next` |
| `gotoBackLevel` | `goto <structure> back` |
| `gotoPlaceLevels` | Un lugar con nombre, en las tres formas |

El valor es el nivel de permiso que necesita quien lo envía. `3` (operador) es el predeterminado. `2` admite también bloques de comandos, de modo que un pack puede poner un salto en un botón o una placa de presión sin exponer el resto de `/rdplserver`. `0` lo abre a cualquier jugador. Los tres ajustes son independientes: por ejemplo, `next` abierto a los bloques de comandos para una visita guiada a las aldeas mientras `back` sigue reservado a operadores.

Como `intro` está abierto a todos, cualquier jugador llega a `/rdplserver` en sí, así que cada uno de los demás subcomandos comprueba por su cuenta que se es operador y lo rechaza con un mensaje. El autocompletado con tabulador coincide: a quien no es operador se le ofrece `intro`, `team` cuando un pack pone en juego un bando, y también `goto` en cuanto un nivel le permite usarlo.

`gotoPlaceLevels` sustituye a los tres ajustes para lugares concretos, como entradas `nombre=nivel`, igual que en el ejemplo anterior. El nombre es lo que escribirías después de `goto`: uno de vanilla como `Village` o `Mansion`, o un nombre registrado con `locateAs` en una entrada `imprint`. La comparación no distingue mayúsculas de minúsculas. Un nivel de `4` está por encima de operador y cierra ese lugar a todo el mundo: es la forma de ocultar un lugar mientras el resto de `goto` está abierto.

Una entrada fija un nivel para las tres formas de ese lugar. Un lugar que no figura en la lista recurre a los tres ajustes anteriores, y un nombre no registrado nunca coincide.

El autocompletado con tabulador sigue las mismas reglas, así que tras `goto` a quien lo envía solo se le ofrecen los lugares a los que realmente puede ser llevado.

Estos ajustes están en el grupo `commands`, así que `control.commands` en la configuración decide si un pack puede establecerlos siquiera, y `off` ahí mantiene todo en operador, pida lo que pida un pack.

### Quién puede usar game

*comandos*

Cada parte de `game` tiene su propio nivel: 0 para cada tirada, y 2 para `deck shuffle`, `tiebreak`, `board start`, `board end` y `board ai`. `gameLevels` cambia cualquiera de ellos, como entradas `parte=nivel`, donde la parte es lo que sigue a `game`.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "gameLevels": ["coin=0", "deck draw=0", "deck shuffle=3", "tiebreak=4"]
  }
}
```

| Ajuste | Qué rige |
| --- | --- |
| `gameLevels` | Una parte de `game`: `coin`, `die`, `dice`, `advantage`, `disadvantage`, `pick`, `deck draw`, `deck shuffle`, `deck left`, `teamroll`, `tiebreak`, `last`, `pass` o `board` con su acción, como `board move` |

La escala es la de `goto`, y `4` cierra una parte a todos. El autocompletado con tabulador ofrece solo las partes que un remitente puede usar. `gameLevels` está en el grupo `commands` junto a los ajustes de `goto`.

## Conviene saber

*referencia*

- CraftTweaker y GroovyScript se ejecutan después de RDPL, así que sus cambios siguen ganando.
- Las recetas solo se cargan al iniciar, por lo que los cambios en ellas requieren un reinicio y no una recarga.
- Las funciones guardadas en la propia carpeta de datos de un mundo siguen teniendo prioridad sobre una función de un pack, y lo mismo ocurre con los avances de ese mundo.
- Una estructura que ya se ha generado permanece cargada hasta que sales del mundo.
- Las mayúsculas y minúsculas de los nombres de archivo importan. Si la capitalización de tu archivo no coincide con lo que pidió el juego, RDPL lo carga igualmente pero te avisa, porque en Linux no se encontraría en absoluto.
- Pon un `pack.png` en `rdploader` para dar un icono al pack. Sin él se muestra el icono de RDPL.
- La carpeta se puede mover o renombrar con la opción `rootDirectory` de `config/mct_resourcedatapackloader_mixin.cfg`. También funciona una ruta absoluta, y requiere un reinicio.
- Los blockstates que nombran un modelo de vanilla sin más heredan también las texturas de vanilla. Los modelos padre como `cube_all` y `cross` toman sus texturas del blockstate y no dan problemas.
- `forge_marker: 1` no admite multipart, así que los blockstates de las enredaderas tienen que ser multipart de vanilla normal, con las texturas integradas en el modelo.
- La pantalla de carga de Forge se dibuja con colores oscuros, con el logotipo de este mod en lugar del de Forge. `darkSplash` en la categoría `client` de la configuración devuelve los colores propios de Forge; los colores fijados a mano en `config/splash.properties` se dejan como están en ambos casos, y requiere un reinicio.

## Cuando algo no funciona

*referencia*

**Revisa primero `logs/rdpl.log`.** Todo lo que hace RDPL va ahí y no al registro principal. Los avances, las tablas de botín, las recetas, las funciones, las estructuras y cada pieza de contenido se registran con el pack del que proceden, y todo lo que está mal formado se registra con el motivo.

**Las texturas y otros recursos son distintos.** Se piden con demasiada frecuencia como para registrarlos uno a uno, así que en su lugar `/rdpl unused` enumera los archivos de tus packs que nadie ha pedido. Ejecútalo cuando el juego haya terminado de cargar. Un archivo con la ruta correcta siempre se pide, así que lo que aparezca en la lista suele ser un error al escribir, pero ten en cuenta que algunos archivos solo se cargan cuando hacen falta, como los idiomas distintos del que juegas.

**Un zip sin un directorio `assets` dentro se omite,** y lo mismo ocurre con cualquier carpeta en `rdploader`, y el registro lo indica.

**`/rdpl which minecraft:textures/blocks/stone.png`** te dice exactamente qué pack sirve un archivo y a qué está tapando.

## Extra: ajustes de vanilla

*referencia*

Pequeños cambios en el comportamiento de vanilla, cada uno activable en la categoría `tweaks` de la configuración.

| Opción | Por defecto | Qué hace |
| ---------------------- | ------- | ------------------------------------------------------------------------------------ |
| `promptLeafDecay` | activado | Las hojas que pierden su árbol desaparecen en menos de un segundo en lugar de esperar a los ticks aleatorios |
| `lenientPaths` | activado | Se pueden hacer caminos de hierba bajo un bloque y se mantienen cuando se coloca uno encima |
| `unbreakableSpawners` | desactivado | Los spawners de mobs no se pueden minar ni volar |
| `modernChestPlacement` | activado | Los cofres se emparejan como a partir de 1.13 |

Otras tres están en la categoría `content` y no en `tweaks`:

| Opción | Por defecto | Qué hace |
| ----------------- | ------- | ----------------------------------------------------------------------------------- |
| `cactusMaxHeight` | `3` | Qué altura alcanza el cactus de vanilla |
| `caneMaxHeight` | `3` | Qué altura alcanza la caña de azúcar de vanilla |
| `shovelPaths` | activado | Una pala convierte en camino los bloques marcados con `behavesAs` path, y agacharse revierte uno |

**Estas ceden ante Universal Tweaks**, que cambia los mismos bloques de vanilla. Consulta [Universal Tweaks](#universal-tweaks) para saber exactamente cuándo.

**Nada de esto llega a un pack.** Estas opciones solo cambian el cactus, la caña, las hojas y los caminos propios de Minecraft. Un bloque que tu pack define con `"type": "cane"` lleva su propia sección `growth` y crece hasta la altura que le hayas dado, sea lo que sea que haya instalado. `lenientPaths` también levanta la misma restricción para los bloques de pack que usan `behavesAs`, algo que Universal Tweaks no toca, así que esa mitad sigue activa en ambos casos.

### Spawners irrompibles

*extra: ajustes de vanilla*

`unbreakableSpawners` da al bloque de spawner de mobs los valores del lecho de roca: una dureza irrompible y una resistencia a las explosiones que nada sobrevive. Un jugador no puede minar uno por buena que sea la pala, y ni los creepers, ni el TNT, ni una entidad de pack que `explodes` se llevarán uno por delante. El modo creativo sigue eliminándolos, igual que sigue eliminando el lecho de roca, para que un autor de packs nunca quede bloqueado en su propia construcción. Requiere un reinicio, ya que los valores se fijan una sola vez cuando el juego termina de cargar.

**Es el bloque, no el spawner.** No hay un interruptor por spawner. La opción cambia `minecraft:mob_spawner` en sí, así que alcanza a todos los spawners del mundo a la vez: las cuatro estructuras de vanilla que colocan uno, los que coloque cualquier mod y los que coloquen tus propios packs.

Esto último es la respuesta para una estructura personalizada. Un spawner dentro de una de tus plantillas `.nbt`, colocado por una entrada `imprint`, es un bloque de spawner de mobs normal con su propia entidad de bloque, así que queda cubierto en cuanto la opción está activada. Construye la estructura con un spawner dentro de la forma habitual, define lo que genera en los datos de entidad de bloque de la plantilla, activa `unbreakableSpawners`, y el de tu mazmorra será tan irrompible como el de vanilla. No hay que poner nada en el pack para esto, y no hay forma de proteger solo los tuyos dejando que se puedan romper los del resto del mundo.

### Colocación de cofres

*extra: ajustes de vanilla*

`modernChestPlacement` coloca cofres y cofres trampa como lo hacen 1.13 y posteriores.

- Un cofre se une a un cofre simple situado directamente a su izquierda o derecha, y solo cuando ambos miran en la misma dirección. Un cofre delante o detrás de otro nunca se une a él.
- Agacharse mantiene el cofre nuevo simple, salvo que se haga clic en el lateral de un cofre simple: entonces se une a ese cofre y se orienta en su misma dirección.
- Un cofre puede estar junto a un cofre doble, donde se mantiene simple, de modo que es posible una hilera de cofres a lo largo de una pared.

Cada cofre recuerda a su pareja, por lo que lo colocado sigue emparejado o simple tras una recarga, una tolva o tubería llena solo el cofre que toca, y romper una mitad deja la otra simple. Los cofres colocados antes de activar la opción, o por la generación del mundo y las estructuras, se emparejan como siempre lo hizo 1.12. Un cliente sin RDPL sigue dibujando dos cofres simples que se tocan como un cofre doble, aunque los abre por separado.

## Extra: arreglo de conflictos de plugins de JEI

*referencia*

Algunos mods consultan el registro de recetas de JEI antes de que los mods que lo proporcionan hayan terminado de inicializarse, lo que inunda los registros con cientos de errores inofensivos pero molestos y puede romper en silencio la integración de un mod con JEI. RDPL lo detecta automáticamente y corrige el orden de las notificaciones. Funciona con Just Enough Items y con Had Enough Items. Si no hay ninguno instalado, no ocurre nada.

## Extra: menos errores al iniciar

*referencia*

- Las recetas que hacen referencia a un ítem que ningún mod registró realmente, normalmente contenido desactivado en la configuración propia de un mod, se omiten en lugar de lanzar un error de análisis. El recuento se registra una sola vez. (`skipMissingItems`)
- Los avances que recompensan una receta que un script ha eliminado después se cargan igualmente, en lugar de fallar. Simplemente nunca desbloquean esa receta, y todo el conjunto se resume en una sola línea. (`tolerateMissingInAdvancements`)

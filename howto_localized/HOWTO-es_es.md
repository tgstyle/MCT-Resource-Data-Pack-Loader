# Resource Data Pack Loader

**Una carpeta que sobrescribe cualquier cosa que aporten Minecraft o un mod, define contenido nuevo a partir de JSON y controla qué se genera, en todos los mundos, en clientes y servidores, sin que los jugadores tengan que activar nada.**

Un ejemplo funcional. Colócalo directamente en `rdploader` y fíjate en cómo está escrito cada archivo.

- [RDPLExamplePack.zip](../example/RDPLExamplePack.zip) usa casi todos los tipos de archivo que lee el loader: bloques, ítems, un fluido, una pestaña creativa, biomas, una plantilla de mundo, una dimensión tras un portal, worldgen, una poción y su elaboración, un aldeano y sus comercios, recetas, botín, sobrescrituras de elementos de vanilla, un sonido, un logro y una función. Su readme indica qué comprobar en el juego.

Esta guía es para la versión 26.x, que abarca 26.1.2, 26.2 y 26.3; las tres leen los mismos packs.

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
- [Packs escritos para 1.12.2](#packs-escritos-para-1122)

**Bloques e ítems**
- [Bloques](#bloques)
- [Contenedores](#contenedores)
- [Campanas](#campanas)
- [Modelos, blockstates y texturas](#modelos-blockstates-y-texturas)
- [Hacer que vanilla trate bien tu bloque](#hacer-que-vanilla-trate-bien-tu-bloque)
- [Ítems](#ítems)
- [Fluidos](#fluidos)
- [Materiales, pestañas, sonidos, etiquetas](#materiales-pestañas-sonidos-etiquetas)
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
- [Exposiciones](#exposiciones)

**El mundo**
- [Plantillas de mundo](#plantillas-de-mundo)
- [Reglas del juego](#reglas-del-juego)
- [Biomas](#biomas)
- [Dimensiones](#dimensiones)
- [Portales y puertas dimensionales](#portales-y-puertas-dimensionales)
- [El mundo profundo](#el-mundo-profundo)
- [Regiones de cuevas](#regiones-de-cuevas)

**Generación del mundo**
- [Entradas de worldgen](#entradas-de-worldgen)
- [Formas](#formas)
- [Dispersiones](#dispersiones)
- [Mapas de estructuras](#mapas-de-estructuras)
- [Parcelas de aldea](#parcelas-de-aldea)
- [Mapas de trazado de ciudades](#mapas-de-trazado-de-ciudades)
- [Retrogen](#retrogen)
- [Pregeneración](#pregeneración)

**Modos de juego**
- [Introducción al mundo](#introducción-al-mundo)
- [Equipos](#equipos)
- [Puntuación](#puntuación)
- [Asaltos](#asaltos)
- [Tarjetas](#tarjetas)

**Control**
- [La capa de control](#la-capa-de-control)
- [Qué hace cada grupo](#qué-hace-cada-grupo)

**Otros mods**
- [Integración con Blast Plaster](#integración-con-blast-plaster)

**Referencia**
- [Listas de valores](#listas-de-valores)
- [Lista de carpetas](#lista-de-carpetas)
- [Comandos](#comandos)
- [Conviene saber](#conviene-saber)
- [Cuando algo no funciona](#cuando-algo-no-funciona)
- [Extra: ajustes de vanilla](#extra-ajustes-de-vanilla)
- [Claves que no se mantuvieron](#claves-que-no-se-mantuvieron)

---

# Primeros pasos

## Qué es

*primeros pasos*

Resource Data Pack Loader (RDPL) lee una única carpeta, `rdploader`, y hace tres trabajos:

- **Sobrescrituras.** Un archivo de la carpeta sustituye al que el juego o un mod habría cargado. Sin interruptores, sin configuración por mundo, sin nada que los jugadores deban activar.
- **Contenido nuevo.** Las definiciones JSON registran bloques, ítems, fluidos, biomas, dimensiones, pociones y aldeanos. Sin Java, sin jar.
- **Control.** Bloquea la generación de minerales, biomas, estructuras o recetas, aplana el lecho de roca, ajusta las tasas de aparición, vacía el overworld y establece los valores por defecto del mundo.

## Dónde van los archivos

*primeros pasos*

Un pack tiene dos raíces, las mismas dos que tiene un pack de vanilla. `assets/` contiene lo que el cliente dibuja y oye: modelos, blockstates, texturas, archivos de idioma, sonidos y los textos de la introducción. `data/` contiene todo lo demás: cada definición que lee este mod y los archivos de datos de vanilla que un pack sustituye. Todas las rutas de esta guía se escriben a partir del espacio de nombres, así que `<namespace>/blocks/*.json` es `data/mypack/blocks/ruby_ore.json` en disco para un pack cuyo espacio de nombres sea `mypack`, y `<namespace>/models/` es `assets/mypack/models/`. Cada sección repite su propia ruta bajo el encabezado.

Bajo `data/`:

| Ruta | Qué contiene |
| --- | --- |
| `<namespace>/blocks/*.json` | Definiciones de bloques. [Bloques](#bloques) |
| `<namespace>/items/*.json` | Definiciones de ítems. [Ítems](#ítems) |
| `<namespace>/fluids/*.json` | Fluidos, con un bloque y un cubo. [Fluidos](#fluidos) |
| `<namespace>/materials/*.json` | Materiales de herramientas y armaduras. [Materiales, pestañas, sonidos, etiquetas](#materiales-pestañas-sonidos-etiquetas) |
| `<namespace>/tabs/*.json` | Pestañas creativas. [Materiales, pestañas, sonidos, etiquetas](#materiales-pestañas-sonidos-etiquetas) |
| `<namespace>/sounds/*.json` | Eventos de sonido. [Materiales, pestañas, sonidos, etiquetas](#materiales-pestañas-sonidos-etiquetas) |
| `<namespace>/biomes/*.json` | Definiciones de biomas. [Biomas](#biomas) |
| `<namespace>/worldgen/*.json` | Qué se genera y dónde. [Entradas de worldgen](#entradas-de-worldgen) |
| `<namespace>/caveregions/*.json` | Regiones con nombre pintadas sobre el subsuelo. [Regiones de cuevas](#regiones-de-cuevas) |
| `<namespace>/dimensions/*.json` | Definiciones de dimensiones. [Dimensiones](#dimensiones) |
| `<namespace>/worldtemplates/*.json` | Todos los ajustes de un mundo en un solo archivo. [Plantillas de mundo](#plantillas-de-mundo) |
| `<namespace>/worldintro/*.json` | Páginas que se muestran cuando un jugador entra en el mundo. [Introducción al mundo](#introducción-al-mundo) |
| `<namespace>/gates/*.json` | Condiciones de portales y dimensiones. [Portales y puertas dimensionales](#portales-y-puertas-dimensionales) |
| `<namespace>/gamerules/*.json` | Reglas del juego para mundos nuevos. [Reglas del juego](#reglas-del-juego) |
| `<namespace>/teams/*.json` | Bandos del marcador de vanilla y qué los une. [Equipos](#equipos) |
| `<namespace>/scoring/*.json` | Objetivos, puntos y cómo termina una partida. [Puntuación](#puntuación) |
| `<namespace>/raids/*.json` | Oleadas que atacan una aldea cuando un jugador lleva un augurio a ella. [Asaltos](#asaltos) |
| `<namespace>/entities/*.json` | Variantes de entidades construidas sobre entidades que ya existen. [Variantes de entidades](#variantes-de-entidades) |
| `<namespace>/hardness/*.json` | Tiempo de minado y multiplicadores de explosión para grupos de bloques. [Grupos de dureza](#grupos-de-dureza) |
| `<namespace>/anvils/*.json` | Encantamientos que un yunque pone a un ítem con nombre, un logro que otorga y un bloqueo hasta entonces. [Trabajo en el yunque](#trabajo-en-el-yunque) |
| `<namespace>/cards/*.json` | Tarjetas en pantalla mostradas por un disparador y los mensajes que dice el propio mod. [Tarjetas](#tarjetas) |
| `<namespace>/exposures/*.json` | Peligros que exponen a los jugadores cerca de bloques con nombre, al llevar ítems con nombre o en dimensiones con nombre. [Exposiciones](#exposiciones) |
| `<namespace>/overrides/<target>/<name>.json` | Propiedades de bloques, ítems y tipos de poción existentes, modificadas en el sitio. [Sobrescritura de propiedades](#sobrescritura-de-propiedades) |
| `<namespace>/villages/*.json` | Parcelas que puede construir una ciudad o aldea. [Parcelas de aldea](#parcelas-de-aldea) |
| `<namespace>/pathintersects/*.json` | Diseños pintados donde se cruzan los caminos de aldea. [Caminos de aldea](#caminos-de-aldea) |
| `<namespace>/structuremaps/*.json` | Plantillas compuestas en un único edificio grande sobre una cuadrícula. [Mapas de estructuras](#mapas-de-estructuras) |
| `<namespace>/citymaps/*.json` | Un plano de calles dibujado a partir del cual se traza una ciudad en lugar de generar uno al azar. [Mapas de trazado de ciudades](#mapas-de-trazado-de-ciudades) |
| `<namespace>/portalframes/*.json` | Marcos que un jugador puede construir y encender. [Marcos de portal](#marcos-de-portal) |
| `<namespace>/blastplaster/*.json` | Qué hace Blast Plaster tras una explosión, por dimensión. [Integración con Blast Plaster](#integración-con-blast-plaster) |
| `<namespace>/structures/*.nbt` | Plantillas, para brotes, `imprint` y sobrescrituras de mods. [Qué puedes sobrescribir](#qué-puedes-sobrescribir) |
| `<namespace>/recipes/*.json` | Recetas de fabricación, añadidas o sustituidas. [Qué puedes sobrescribir](#qué-puedes-sobrescribir) |
| `<namespace>/recipe_removals/*.json` | Recetas eliminadas por nombre, espacio de nombres o resultado. [Qué puedes sobrescribir](#qué-puedes-sobrescribir) |
| `<namespace>/disabled/*.json` | Bloques e ítems retirados del juego. [Bloques e ítems desactivados](#bloques-e-ítems-desactivados) |
| `<namespace>/furnace/*.json` | Recetas de horno añadidas y eliminadas. [Recetas de horno y combustibles](#recetas-de-horno-y-combustibles) |
| `<namespace>/fuels/*.json` | Tiempos de combustión. [Recetas de horno y combustibles](#recetas-de-horno-y-combustibles) |
| `<namespace>/brewing/*.json` | Recetas del soporte para pociones. [Pociones, tipos de poción y elaboración](#pociones-tipos-de-poción-y-elaboración) |
| `<namespace>/potions/*.json` | Efectos de poción. [Pociones, tipos de poción y elaboración](#pociones-tipos-de-poción-y-elaboración) |
| `<namespace>/potion_types/*.json` | Pociones embotelladas construidas a partir de esos efectos. [Pociones, tipos de poción y elaboración](#pociones-tipos-de-poción-y-elaboración) |
| `<namespace>/villagers/*.json` | Profesiones de aldeanos. [Aldeanos y comercio](#aldeanos-y-comercio) |
| `<namespace>/trades/*.json` | Lo que las profesiones compran y venden. [Aldeanos y comercio](#aldeanos-y-comercio) |
| `<namespace>/loot_tables/*.json` | Tablas de botín, sustituidas. [Qué puedes sobrescribir](#qué-puedes-sobrescribir) |
| `<namespace>/loot_injections/*.json` | Una reserva añadida a una tabla que ya existe. [Qué puedes sobrescribir](#qué-puedes-sobrescribir) |
| `<namespace>/block_drops/*.json` | Drops adicionales o sustitutivos para bloques que un pack no posee. [Drops de bloques](#drops-de-bloques) |
| `<namespace>/player_loot/*.json` | Una tabla de botín que se tira cuando muere un jugador. [Botín de jugadores](#botín-de-jugadores) |
| `<namespace>/advancements/*.json` | Logros. [Qué puedes sobrescribir](#qué-puedes-sobrescribir) |
| `<namespace>/functions/*.mcfunction` | Archivos de función. [Qué puedes sobrescribir](#qué-puedes-sobrescribir) |
| `<namespace>/tags/<kind>/*.json` | Etiquetas, en el formato propio del juego. [Materiales, pestañas, sonidos, etiquetas](#materiales-pestañas-sonidos-etiquetas) |
| `<namespace>/registry_remap/*.json` | Nombres antiguos asignados a nombres nuevos. [Renombrados de registro](#renombrados-de-registro) |

Bajo `assets/`:

| Ruta | Qué contiene |
| --- | --- |
| `<namespace>/models/`, `<namespace>/blockstates/`, `<namespace>/textures/`, `<namespace>/lang/` | Las carpetas de assets habituales. [Modelos, blockstates y texturas](#modelos-blockstates-y-texturas) |
| `<namespace>/sounds.json` | El índice de sonidos que lee el juego, junto a las definiciones de `sounds/` bajo `data/` |
| `<namespace>/texts/*.txt` | Archivos de texto plano, usados por la introducción al mundo. [Introducción al mundo](#introducción-al-mundo) |

**1.21.1** nombra las carpetas de datos de vanilla en singular: `loot_table/`, `recipe/`, `advancement/`, `function/`, `structure/`, `tags/item/`, `tags/block/`. Un pack puede usar cualquiera de las dos grafías ahí; los nombres en plural de arriba se leen como sus gemelos en singular, de modo que un mismo pack sirve para ambas versiones.

## Cómo leer las tablas

*primeros pasos*

Todos los archivos son JSON estándar. Una entrada de worldgen representativa:

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

Las tablas de claves de este documento indican si una clave es obligatoria, qué contiene y cuál es el valor por defecto cuando se omite. Los valores no reconocidos se registran en el log y se sustituyen por el valor por defecto; no hacen que el juego se cierre. Tipos de valor usados en todo el documento:

| Cuando una tabla dice | Escribes |
| --- | --- |
| int | `8` |
| int, ticks | `100` (20 ticks = 1 segundo) |
| int o rango | `8`, o `{ "min": 4, "max": 12 }` para elegir al azar entre ambos |
| 0 a 15, 1 a 100 y similares | un int dentro de esos límites |
| float | `0.5` |
| boolean | `true` o `false` |
| string | `"palabras entre comillas"` |
| nombre de bloque, nombre de ítem | `"minecraft:stone"`. Un estado de bloque es el nombre con `properties` al lado: `{ "block": "minecraft:oak_log", "properties": { "axis": "x" } }` |
| `namespace:name` | `"mypack:ruby_ore"` |
| nombre de bioma, nombre de sonido, nombre de pestaña | la misma forma `namespace:name` entre comillas |
| id de dimensión | `"minecraft:overworld"`, `"minecraft:the_nether"`, `"minecraft:the_end"` o la propia `"mypack:verdant"` de un pack. Los números `0`, `-1` y `1` de 1.12.2 se siguen tomando como esas tres |
| color hexadecimal | seis dígitos hexadecimales, `"A0C8FF"`, `#` opcional |
| ruta de textura | `"mypack:block/ruby_ore"` |
| lista de ints | `[4, 12]` |
| lista de nombres de bloque | `["minecraft:stone", "minecraft:andesite"]` |
| lista de nombres de bioma | `["minecraft:windswept_hills", "mypack:ruby_hills"]` |
| lista de tipos de bioma | `["mountain", "forest"]`, las palabras de tipo que figuran en [Listas de valores](#listas-de-valores), cada una equivalente a una etiqueta de bioma |
| lista de ids de mod o espacios de nombres de pack | `["quark", "mypack"]` |
| lista de objetos | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`, con las claves según la tabla propia de ese objeto |
| objeto | `{ "type": "cluster" }`, con las claves según su propia tabla |
| objeto de rol a bioma, de nombre de variante a variante | las claves son lo primero y los valores lo segundo: `{ "ocean": "mypack:ruby_ocean" }` |

La mayoría de las definiciones aceptan también `requires`, una lista de ids de mod o espacios de nombres de pack que deben estar presentes; si no, el archivo se omite.

## La única regla

*primeros pasos*

Abre el jar, busca el archivo que quieres cambiar y copia su ruta desde `assets` o `data` en adelante:

```
assets/minecraft/textures/block/iron_ore.png                 (in the Minecraft jar)
rdploader/assets/minecraft/textures/block/iron_ore.png       (your override)

data/minecraft/loot_tables/blocks/iron_ore.json              (in the Minecraft jar)
rdploader/data/minecraft/loot_tables/blocks/iron_ore.json    (your override)
```

La ruta posterior a `assets` o `data` es siempre idéntica a la ruta dentro del jar. Nada se renombra ni se mueve.

## Organizar los packs

*primeros pasos*

Los archivos sueltos funcionan bajo `rdploader/assets/<namespace>/` y `rdploader/data/<namespace>/`. Agruparlos también funciona, en un zip. Una carpeta dentro de `rdploader` nunca es un pack: se omite con una advertencia en el log, así que comprime un pack en zip antes de ponerlo ahí. Al comprimir, selecciona el contenido y comprime eso, no la carpeta que lo contiene: un zip cuyo nivel superior es una carpeta que envuelve `assets` o `data` se omite, y el log lo indica.

```
rdploader/assets/minecraft/textures/block/iron_ore.png
rdploader/MyTextures.zip
```

**Packs en la carpeta equivocada.** Al arrancar, antes de leer `rdploader`, RDPL revisa la carpeta `resourcepacks` del juego y la carpeta `datapacks` de cada mundo (en un servidor dedicado, el mundo que nombra `level-name`) y mueve a `rdploader` todo zip de pack RDPL que encuentre. Un zip es un pack RDPL cuando contiene archivos de definición de RDPL, como `data/<namespace>/blocks/` o, en un pack de 1.12.2, `assets/<namespace>/blocks/`. Un pack de recursos o de datos normal se queda donde está. Un zip cuyo nombre ya existe en `rdploader` se deja en su sitio, y también un pack RDPL dentro de una carpeta; ambos casos generan una advertencia. Cada movimiento se escribe en `logs/rdpl.log`. El juego retira por sí solo el pack movido de la lista de packs de recursos o de los packs de datos del mundo, y RDPL lo carga desde `rdploader` a partir de entonces.

**Prioridad.** Cuando dos packs contienen el mismo archivo, antepón a los nombres `RDPL` y un número; los números más altos se cargan después y ganan:

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

No distingue mayúsculas de minúsculas; un espacio, guion o guion bajo tras el número es opcional; el prefijo se oculta del nombre mostrado. Un pack sin prefijo se carga primero y pierde frente a cualquier pack numerado. La prioridad también ordena las entradas de worldgen, lo que importa cuando un pack coloca bloques que otro sustituye.

**Desactiva un pack** añadiendo `.disabled` a su nombre.

**Un zip para todas las versiones.** Un zip puede llevar una carpeta `versions/<version>/` por cada versión de Minecraft a la que sirve: `versions/1.12.2/`, `versions/1.20.1/`, `versions/1.21.1/` y, en 26.x, la versión exacta que ejecuta: `versions/26.1.2/`, `versions/26.2/` o `versions/26.3/`. Cada una está organizada como la raíz de un pack para esa versión, `pack.mcmeta` incluido. Un archivo bajo la carpeta de la versión en ejecución se lee en lugar de la misma ruta en la raíz; la raíz es común a todas las versiones, y la carpeta de otra versión nunca se lee. Pon en la raíz lo que todas las versiones leen igual y en una carpeta de versión solo lo que difiere, y un solo zip se carga en las cuatro.

**Los packs se convierten entre versiones.** Al cargar un pack escrito para otra línea, se convierte la primera vez, de la misma manera, escribiendo lo que cambió en su propia carpeta `versions/<version>/` como se ha descrito; esto ocurre automáticamente al cargar, incluida la carga que desencadena un `/rdpl reload` o `/rdplserver reload`, nunca mediante un comando propio. Cada pareja de versiones se convierte en ambos sentidos, de modo que un pack de 1.12.2, 1.20.1, 1.21.1 o 26.x se carga en cualquiera de las otras. Un pack de 26.3 se carga también en todas las líneas, leído con el formato de 26.2 en las más antiguas, y un pack más antiguo se carga en 26.3. Lo que un lado tiene y el otro no puede contener se descarta, y el log nombra cada descarte: al bajar desde 26.3, entre otras cosas, las funciones de densidad de depuración, la exclusión de acuíferos y el nivel de superficie propios de un pack, y los comandos que 26.2 no tiene; al subir a 26.3, la `depth` y el `offset` de un objetivo de aparición y las claves de vetas de mineral del enrutador de ruido. [Claves que no se mantuvieron](#claves-que-no-se-mantuvieron) las enumera todas.

Un `pack.mcmeta` en la raíz del zip es bienvenido pero no necesario: el mod presenta cada pack al juego bajo una entrada propia, con el formato de pack que el juego espera, de modo que un pack nunca queda obsoleto por un número de formato. Pon un `pack.png` a su lado para dar un icono a la entrada de la carpeta. Sin él, la entrada muestra el icono de RDPL.

## Packs de recursos: quién gana

*primeros pasos*

Por defecto, los archivos de RDPL quedan por encima de los packs de recursos que selecciona un jugador, de modo que un pack de recursos no puede sobrescribirlos. Añade `O` o `N` tras el prefijo `RDPL` para decidirlo pack por pack:

```
rdploader/RDPLO Branding        always wins; resource packs cannot touch it
rdploader/RDPLN BaseTextures    a resource pack can override it
rdploader/RDPL1O Seasonal       priority and override combined
```

Los packs sin letra siguen la opción de configuración `overrideResourcePacks`. `/rdpl list` marca los packs que sobrescriben. La letra debe cerrar el prefijo (seguida de un espacio, guion, guion bajo o nada), así que `RDPLOverhaul` es un pack llamado `Overhaul`, no un indicador `O`.

Los mismos niveles valen para los packs de datos. Un pack marcado con `N` queda por debajo de los packs de datos que un mundo lleva en su propia carpeta `datapacks`, y uno marcado con `O` queda por encima.

---

# Cómo funcionan los packs

## Cómo funcionan las definiciones

*cómo funcionan los packs*

Junto a las carpetas que sobrescriben archivos, hay carpetas que describen cosas nuevas. Un archivo de definición agrupa una o más cosas de un tipo bajo `variants`, y cada clave dentro de `variants` es un nombre de registro: `data/mypack/blocks/ore.json` con una variante llamada `ruby_ore` registra `mypack:ruby_ore`. El nombre del propio archivo es una agrupación y nada más; un archivo puede contener un bloque o una docena que comparten sus ajustes.

El registro se hace con la prioridad más baja que ofrece el loader, así que si un mod real registra el mismo nombre, gana el mod y tu archivo se ignora. Nada de esto puede sustituir a un mod.

**Dónde está el límite.** Todo lo que necesite una entidad de bloque propia, una pantalla, un inventario o una lógica propia por tick requiere un mod de verdad, con una excepción: el tipo [contenedor](#contenedores), que lleva un inventario y una pantalla propios. Todo lo que no llegue a eso es terreno libre.

### Tu espacio de nombres es tu mod

*cómo funcionan las definiciones*

El espacio de nombres que elijas es, a todos los efectos prácticos, un id de mod. Nada se carga como mod y nunca aparece en la lista de mods, pero todo lo que lee un id de mod lee el tuyo:

- Los nombres de registro son `mypack:ruby_ore`, exactamente como lo serían los de un mod, y se escriben en cada mundo guardado que los contenga.
- Las listas blancas de minerales, biomas y recetas de la configuración lo reconocen, de modo que `oreWhitelist = mypack` conserva tus minerales y bloquea los de todos los demás.
- `/rdpl which`, `/rdplserver oregen` y los informes agrupan todos por él.
- JEI, las etiquetas y las consultas de otros mods lo ven de la misma manera.

Así que elige un nombre al principio y no lo cambies nunca. Renombrar un espacio de nombres deja huérfano todo lo ya colocado en un mundo, igual que si un mod cambiara su id; para repararlo existe `registry_remap`.

Esto funciona en ambos sentidos: `requires` acepta un espacio de nombres de pack igual que un id de mod instalado, de modo que un pack puede depender de otro y omitirse cuando este no está instalado.

Un mod o un pack nombrado en `requires` que no esté instalado hace que se omita la definición: se escribe una línea en `logs/rdpl.log` con lo que faltaba y el juego sigue. Si un bloque que esperabas no está en la pestaña creativa, esa línea del log es el primer sitio donde mirar.

`requires` solo admite ids simples. No hay sintaxis de rangos de versión, así que puede indicar que un mod debe estar presente pero no qué versión.

El id propio del mod, `resourcedatapackloader`, está reservado. Definir contenido bajo él se ignora y se registra en el log, porque se atribuiría la propiedad de cosas que registra este mod. Sobrescribir los assets propios de este mod sigue permitido; solo registrar contenido ahí no lo está.

Cada tabla de abajo sigue las convenciones de [Cómo leer las tablas](#cómo-leer-las-tablas).

La mayoría de las definiciones aceptan también `requires`, una lista de ids de mod o espacios de nombres de pack que deben estar presentes; si no, el archivo se omite.

### Opciones del pack

*cómo funcionan las definiciones*

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
| un nombre de opción | sí | boolean u objeto | | `true` o `false` es el valor por defecto de la opción. Un objeto lleva las tres claves siguientes |
| `hide` en el nivel superior | no | boolean | `false` | Mantiene las opciones de este pack fuera de la pantalla de opciones y del archivo generado por completo, mientras siguen condicionando el contenido con sus valores por defecto |
| `default` | sí | boolean | | El valor de la opción hasta que el usuario lo cambie. Un objeto sin un `default` booleano se ignora, con una advertencia |
| `hide` dentro de una opción | no | boolean | `false` | Oculta solo esa opción, de modo que no se puede cambiar y se queda en su valor por defecto |
| `description` | no | string | ninguna | Se muestra bajo el nombre de la opción en la pantalla de opciones |

Un pack puede llevar una carpeta `config` junto a sus `assets` y `data`, con archivos JSON de opciones verdadero/falso y sus valores por defecto:

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

Un archivo con `"hide": true` en el nivel superior mantiene las opciones de ese pack fuera de la pantalla de opciones y del archivo generado por completo, mientras las opciones siguen condicionando el contenido con sus valores por defecto. Dos casos lo piden: el contenido que aún no está listo para publicarse y los packs de plantilla, donde las opciones son maquinaria que mantiene unidas las definiciones en lugar de una elección que deba hacer nadie. Quita la clave para publicarlos. Lo mismo funciona por opción: `"hide": true` dentro del objeto de una opción oculta solo esa, de modo que un pack terminado puede llevar un interruptor para contenido inacabado, o una puerta de plantilla, sin que ninguno de los dos se vea:

    { "enablePackB": { "default": false, "hide": true } }

Como una opción oculta no se puede cambiar, una oculta con su valor por defecto en true queda efectivamente forzada, para contenido que debe seguir conectado a través de la maquinaria de opciones pero no es una elección.

Una opción también puede ser un objeto con una descripción, que se muestra bajo su nombre en la pantalla de opciones:

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

Al iniciar, los archivos de opciones del pack se convierten en un archivo de configuración real que pertenece al usuario, con el nombre del pack, `rdploader/config/PackA.json`, creado con los valores por defecto del pack y fusionado en las actualizaciones del pack para que las opciones nuevas lleguen sin tocar lo que el usuario ya haya establecido. Los cambios se aplican en el siguiente inicio del juego, y el botón Pack Options de las pantallas de selección de mundo y de creación de mundo es donde un jugador las cambia. Las opciones pertenecen solo a packs con nombre, es decir, zips, ya que el archivo generado lleva el nombre del pack; los archivos sueltos bajo `rdploader/assets` y `rdploader/data` no tienen nombre de pack y no llevan opciones, así que comprime el contenido suelto en un pack con nombre si necesita un interruptor.

La lista `requires` de cualquier definición puede entonces nombrar una opción con una entrada `config:`: `"requires": ["config:enableTestingContent"]` registra ese contenido solo mientras la opción sea true, exactamente como lo omitiría un mod ausente. Un nombre simple comprueba el archivo de todos los packs y todos los packs que lo definen deben coincidir; `"config:PackA:enableTestingContent"` nombra un solo pack. Una opción que ningún pack define cuenta como false y se avisa de ello una vez.

Una opción que condiciona algo con lo que se creó un mundo queda recordada por ese mundo, y se vuelve a registrar cada vez que el mundo se guarda. Si la cambias y abres el mundo de nuevo, y el cambio deja contenido que el mundo contiene sin registrar, primero se hace una copia de seguridad del mundo, en la carpeta `backups` propia del juego, exactamente como hace la pantalla Edit World; si esa copia falla, el mundo no se abre. Al primer jugador que entra en el overworld se le informa de qué opciones cambiaron y, cuando fue así, de que se hizo la copia.

Una entrada `file:` condiciona según la existencia de un archivo o carpeta bajo la carpeta del juego, para acoplar contenido a algo ajeno a los packs propios de RDPL, como el pack de recursos de otro mod: `"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` registra el contenido solo mientras ese archivo exacto esté instalado. La ruta es relativa a la carpeta del juego, siempre con barras normales, y no puede contener `..`.

### Herencia de definiciones

*cómo funcionan las definiciones*

Una definición de bloque o de ítem puede partir de otra del mismo tipo con `"inherits"`, nombrando el nombre de registro de cualquier variante, y luego sobrescribir lo que difiera:

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "hardness": 4.0 } } }

El hijo copia todas las estadísticas del archivo del padre y de la variante nombrada, el orden de los archivos nunca importa, las cadenas se resuelven empezando por el padre, y un círculo o un padre ausente se registra en el log y deja al hijo tal como está escrito. Los campos que escribe el hijo sustituyen al valor heredado; las propiedades anidadas de las variantes se sobrescriben una a una, pero las listas como `requires` se sustituyen enteras, así que escribe la lista completa que quieras. Los bloques solo heredan de bloques y los ítems solo de ítems.

### Plantillas de bloques e ítems

*cómo funcionan las definiciones*

Un padre puede ser una plantilla pura que nunca entra en el juego, ya que la herencia lee los propios archivos de definición, no lo que se registró. Condiciona la plantilla a una opción oculta forzada a desactivada, y no registra nada mientras sus estadísticas siguen siendo heredables:

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

La plantilla nunca se registra, mientras que `jacks_ore` se registra con el material, el sonido, la herramienta, la pestaña, los drops de experiencia y la resistencia de la plantilla, sobrescribiendo solo la dureza. El hijo debe escribir su propio `requires`, aquí vaciado a una lista vacía, porque de lo contrario hereda el del padre y desaparecería con él.

## Qué puedes sobrescribir

*cómo funcionan los packs*

- **Cualquier cosa de la carpeta de assets de un mod**: texturas, modelos, blockstates, archivos de idioma, sonidos, fuentes, textos de splash, libros de guía, manuales
- **Logros, tablas de botín, etiquetas y funciones**, del lado del servidor, de modo que funcionan también en servidores dedicados
- **Recetas**: sustituye la receta de un mod o añade la tuya
- **Plantillas de estructuras**: los archivos `.nbt` que usan los mods para los edificios generados, bajo `<namespace>/structures/`
- **Renombrados de registro**: mantienen funcionando los mundos antiguos cuando un mod renombra un bloque o un ítem
- **Eliminación de recetas**: borra una receta de fabricación por nombre, espacio de nombres o resultado
- **Bloques e ítems desactivados**: retira del juego cualquier bloque o ítem, véase [Bloques e ítems desactivados](#bloques-e-ítems-desactivados)
- **Inyecciones de botín**: añaden una reserva a una tabla de botín en lugar de sustituirla entera
- **Drops de bloques**: añaden o sustituyen lo que suelta cualquier bloque al romperse, experiencia incluida
- **Botín de jugadores**: tira una tabla de botín cuando muere un jugador, además de lo que llevaba encima o en su lugar
- **Propiedades de bloques, ítems y pociones existentes**: dureza, luz, tamaños de pila, comida en cualquier cosa, los efectos de una poción, véase [Sobrescritura de propiedades](#sobrescritura-de-propiedades)
- **Recetas de horno, tiempos de combustión de combustibles, pestañas creativas y eventos de sonido**

Lo que suelta un bloque es su tabla de botín en esta versión: para cambiar lo que suelta la piedra, incluye `data/minecraft/loot_tables/blocks/stone.json`, y para añadirle algo sin sustituirla, una inyección de botín o una regla de [drops de bloques](#drops-de-bloques), que también puede dar experiencia.

RDPL sirve para sustituir una o dos recetas, y las recetas de tu propio contenido deben añadirse en el pack junto a él. Para un control total de las recetas en un modpack, KubeJS y CraftTweaker son mejores opciones, y un archivo de aquí sigue sustituyendo al original por completo, así que para cambiar un ingrediente o quitar una entrada de botín, usa esos.

## Packs del lado del servidor

*cómo funcionan los packs*

Un pack puede vivir solo en el servidor, con jugadores en clientes de vanilla puro, bajo una restricción: **nada en él puede registrar nada**. El mod acepta cualquier remoto; el pack decide. Un cliente de vanilla juega con los registros con los que vino, así que un pack que añada a ellos debe estar en ambos lados.

| Basta con el servidor | Necesita el pack también en el cliente |
| --- | --- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures`, `structuremaps`, `citymaps`, `villages`, `pathintersects`, `caveregions`, `biomes`, `dimensions` | `blocks`, `items`, `fluids`, `materials`, `containers` |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `anvils`, `tags`, `disabled` | `potions`, `potion_types`, `sounds`, `tabs`, `exposures` |
| `loot_tables`, `loot_injections`, `player_loot`, `advancements`, `functions` | `entities`, `villagers`, `portalframes` |
| `gates`, `cards`, `trades`, `registry_remap`, `teams`, `scoring`, `raids`, `hardness`, `blastplaster` | `models`, `blockstates`, `textures`, `lang`, `worldintro`, `overrides` (carpetas de cliente: sin cliente, déjalas fuera) |
| toda la capa de control, los ajustes y la pregeneración | |

La columna de la derecha es un límite estricto: los bloques, ítems, tipos de entidad, sonidos y efectos de poción que un cliente de vanilla no tiene no se le pueden describir, y el portal propio de una dimensión es uno de los bloques del pack; las exposiciones se cargan solo junto a ese contenido. La columna de la izquierda funciona porque todo lo que contiene o se ejecuta íntegramente del lado del servidor, o llega al cliente como entradas de data pack que vanilla ya lee (biomas, regiones de cuevas, tipos de dimensión), o llega por paquetes que vanilla ya habla (casilla de resultado de fabricación rellenada por el servidor, paquetes de logros ordinarios, rechazos de portal mediante mensajes de estado y una retención de pregeneración hecha con paquetes de vanilla de modo de juego, título y teletransporte).

Configuración:

1. Activa `vanillaClients` en la configuración (categoría `content`, requiere reinicio). Hace cumplir la columna de la derecha: esas carpetas se omiten al cargar y cada archivo omitido se nombra en el log, de modo que un archivo de bloque que se haya colado se convierte en una línea de log en lugar de una conexión rechazada.
2. Mantén igualmente las definiciones fuera de las carpetas de la derecha; los archivos omitidos son peso muerto. Cuando el pack haga referencia a ítems (el `hold` de un portal, `killedDrops`, resultados de recetas, comercios), nombra solo ítems que aporte vanilla o los otros mods de ambos lados del servidor. Un bioma que nombra los bloques de suelo propios del pack conserva el suelo del bioma base, y una dimensión que se abre con su propio portal necesita ese bloque de portal, así que envía a los jugadores allí por comando.
3. Las variantes de entidades son tipos de entidad propios en esta versión, de modo que pertenecen a la columna de la derecha: con `vanillaClients` activado se omiten, y sus apariciones con ellas, y el log las nombra.
4. Instala en el servidor como de costumbre, con Blast Plaster, que el mod requiere y que tampoco registra nada. No va nada en las máquinas de los jugadores; `/rdpl` no existirá para ellos.
5. Prueba con una única conexión limpia de un cliente de vanilla de la misma versión. Los fallos son ruidosos: la conexión se rechaza en la puerta, no se rompe en silencio más tarde.
6. Dos carencias cosméticas aceptadas: las recetas añadidas por el servidor se fabrican pero no aparecen en el libro de recetas, y la retención mientras se crea el terreno es una retención de espectador simple con el progreso en la barra de acciones, sin la niebla ni el logo que dibuja el cliente del propio mod.

## Renombrados de registro

*cómo funcionan los packs*

`<namespace>/registry_remap/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

Cuando un mod renombra uno de sus bloques o ítems, los mundos guardados antes del cambio los pierden. Coloca aquí un archivo para asignar el nombre antiguo al nuevo:

```json
{
  "registry": "minecraft:item",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

El registro es aquel al que pertenece la entrada, nombrado como lo nombra el juego: `minecraft:item`, `minecraft:block`, `minecraft:entity_type` y así sucesivamente. Los renombrados se encadenan, de modo que asignar A a B y más tarde B a C lleva A directamente a C.

## API de mods

*cómo funcionan los packs*

Un mod puede incluir contenido de RDPL dentro de su propio jar, de modo que no necesita un pack aparte. Pon una carpeta llamada `rdploader` en la raíz del jar y organízala exactamente como un pack:

```
thatmod.jar
  META-INF/mods.toml                (1.21.1: META-INF/neoforge.mods.toml)
  rdploader/data/thatmod/blocks/ruby_ore.json
  rdploader/assets/thatmod/textures/block/ruby_ore.png
```

Lo que aporta un mod es un valor por defecto, no una sobrescritura. Se carga por debajo de todos los packs de la carpeta de packs, así que cualquier cosa que escriba el autor de un pack gana sobre ello, y un mod solo puede aportar archivos bajo un espacio de nombres que declare en su propio archivo de mods. Los archivos bajo cualquier otro espacio de nombres se ignoran con una advertencia, y también una carpeta `rdploader` anidada dentro de un espacio de nombres, de modo que un mod no puede redefinir en silencio el contenido de otro mod ni el de un autor de packs.

Cada mod que incluye una obtiene una entrada en `rdploader/config/mods.json` la primera vez que se detecta:

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

El pack de un mod nunca entra en el nivel de sobrescritura de packs de recursos, diga lo que diga `overrideResourcePacks`, ya que solo un autor de packs puede pedirlo con la letra `O`. El log marca los packs de mods y enumera los packs de menor a mayor, de modo que nada se carga sin verse.

## Packs escritos para 1.12.2

*cómo funcionan los packs*

Un pack hecho para la línea 1.12.2 se carga tal cual. El loader reconoce uno por el formato de su `pack.mcmeta`, por carpetas de definición bajo `assets/` sin `data/` al lado, o por un archivo `.lang`, y lo lleva hacia delante. Un zip se convierte una sola vez, dentro de sí mismo: cada archivo que esta versión lee de forma distinta se escribe en la carpeta `versions/1.20.1/` del zip (1.21.1: `versions/1.21.1/`) con todo lo que sigue ya hecho, y los archivos de 1.12.2 de la raíz se quedan como estaban, de modo que el mismo zip sigue cargándose en 1.12.2, como describe [un zip para todas las versiones](#organizar-los-packs). La conversión marca la carpeta que escribe con un archivo `port.stamp` que contiene la versión de RDPL. Un zip que ya tiene la carpeta de esta versión se lee a través de ella; cuando su `port.stamp` nombra otra versión de RDPL, la conversión vuelve a escribir la carpeta, sustituyendo todos sus archivos y nombrando cada uno en el log, y una carpeta sin `port.stamp`, como una que escribió el autor del pack, no se vuelve a convertir nunca. Los archivos de 1.12.2 de la raíz que la conversión sustituyó, como las definiciones bajo `assets/`, los archivos `.lang`, los blockstates y modelos de 1.12.2 y las texturas bajo `textures/blocks/` y `textures/items/`, no se leen en esta versión; solo se siguen leyendo los archivos de la raíz que la conversión deja pasar sin cambios, como los sonidos. El zip se escribe primero en un archivo temporal y sustituye al original solo cuando está completo. Los archivos sueltos bajo `rdploader/assets` no se reescriben; se leen a través de la misma conversión cada vez que se escanea la carpeta.

- Las carpetas de definición pasan de `assets/<namespace>/` a `data/<namespace>/`, y con ellas las carpetas de datos de vanilla: recetas, tablas de botín, inyecciones de botín, logros, funciones y estructuras, renombradas a los nombres en singular que lee 1.21.1 (`recipe`, `loot_table`, `advancement`, `function`, `structure`, `tags/item`).
- `textures/blocks/` y `textures/items/` se sirven como `textures/block/` y `textures/item/`, en los modelos, en los mapas de píxeles y en los propios archivos. Un modelo de ítem en `models/item/<file>/<variant>.json` se sirve como `models/item/<variant>.json`.
- Todo id con metadatos, `minecraft:wool:14` o `minecraft:dye:4`, pasa por los data fixers del propio juego, el mismo código que actualiza un mundo de 1.12.2, de modo que sale como el bloque o ítem en que se convirtió: `minecraft:red_wool`, `minecraft:lapis_lazuli`. Un estado de bloque que sobrevivió a la flattening como propiedad, `minecraft:log:1` a `minecraft:oak_log` con `axis=y`, sale como un objeto `properties`. Los ids propios del pack se resuelven mediante sus propias definiciones: `mypack:materials:5` se convierte en la variante cuyo `meta` era 5, y `mypack:ruby_ore` en la primera variante del archivo, ya que cada variante es aquí un bloque propio. Los nombres de entidades y biomas se corrigen igual, y los números de dimensión se convierten en ids.
- Las `variants` conservan sus claves; `meta` se descarta y `oreDict` se convierte en `tags` mediante la correspondencia del diccionario de minerales con las etiquetas de convención. Un archivo `oredict/*.json` se convierte en un archivo de etiqueta de ítems por cada nombre al que añade o quita: una eliminación `-name` va a la lista `remove` de la etiqueta, y quitar `*` sustituye la etiqueta. Un `creativeTab` simple toma el espacio de nombres del pack, y una etiqueta de pestaña de vanilla de 1.12.2 como `misc` se convierte en la pestaña de vanilla más cercana.
- Un archivo `.lang` se sirve como el `.json` que lee el juego, con `tile.mypack:file.variant.name` como `block.mypack.variant`, `item.` de la misma manera, `itemGroup.x` como `itemGroup.mypack.x`, `fluid.x` como ambas claves de fluido, y todo lo demás tal como está escrito.
- Un blockstate de 1.12.2 no se sirve en absoluto. En su lugar se leen sus texturas y se sirven con los nombres que busca el generador, `textures/block/<variant>.png` con `_top` y `_bottom` donde el blockstate tenía `end`, `top` o `bottom`, de modo que el blockstate y los modelos se generan para cada variante como se harían para un pack escrito aquí.
- Las recetas pierden su `data` y ganan ids aplanados, `forge:ore_shaped` se convierte en `minecraft:crafting_shaped` con los ingredientes `ore` como `tag`, las tablas de botín pierden `set_data` del mismo modo, y el `item` con `data` de un logro se convierte en `items`. El `background` de un logro pasa de `textures/blocks/` a `textures/block/`, y su icono nombra el ítem como `id`.
- También se traslada el vocabulario Forge de 1.12.2 de una receta: un tipo de ingrediente `forge:ore_dict` o `minecraft:item` se descarta, un ingrediente `minecraft:item_nbt` se convierte en `forge:nbt` (1.21.1: `neoforge:components`) con su nbt pasado por los data fixers, `minecraft:item_exists` se convierte en `forge:item_exists` (1.21.1: cada condición toma su nombre `neoforge`, bajo `neoforge:conditions`), un ítem sin espacio de nombres toma el de la receta, y `data` 32767 se convierte en una lista de todas las variantes. Una `#CONSTANT` del `_constants.json` de un mod no puede acompañar, y el log la nombra. Dondequiera que una lista nombre ítems, `name:*` se convierte en todas las variantes que tenía el ítem, y un valor único toma la primera; eso alcanza a los resultados de `recipe_removals` y a las eliminaciones de `furnace`, que también se trasladan como ítems. El `item` o `with` de un yunque escrito como `name:*` se convierte en una lista de todas las variantes, y responde cualquiera de ellas.
- Las tablas de botín renombradas desde 1.12.2 se renombran dondequiera que un pack nombre una: el objetivo de un `loot_injections`, una tabla de `player_loot` y una entrada `loot_table` (1.21.1: su `value`), de modo que `minecraft:entities/zombie_pigman` se convierte en `minecraft:entities/zombified_piglin`. `killed_by_player` con `inverse` se convierte en una condición `inverted`, `entity_properties` con `on_fire` se convierte en un predicado `flags`, y los nombres de `set_attributes` como `generic.maxHealth` pasan a `generic.max_health` (1.21.1: la operación toma su nombre nuevo y el `name` del modificador pasa a ser su `id`).
- Una sobrescritura de un bloque de 1.12.2 que la flattening dividió, como `overrides/minecraft/wool.json`, se lee como sobrescritura de todos los bloques en que se convirtió, las dieciséis lanas, ya que 1.12.2 cambiaba todas las variantes a la vez.
- El `gameLoopFunction` de un archivo de reglas del juego se convierte en la etiqueta de función `#minecraft:tick`, escrita como `data/minecraft/tags/function/tick.json`, ya que la regla del juego ya no existe. Los números de dimensión propios del pack se leen a través de sus archivos `dimensions`, de modo que `"id": 7` en `dimensions/verdant.json` hace que 7 sea `mypack:verdant` dondequiera que el pack lo nombre. Ambos lados de un par de `villageBlocks` se corrigen, conservando la probabilidad, y un archivo `registry_remap` puede conservar los plurales `minecraft:blocks` y `minecraft:items` de 1.12.2.
- Una plantilla de mundo que desactiva todas las estructuras que 1.12.2 tenía en una dimensión desactiva también allí las estructuras que solo tiene esta versión: `ancient_cities`, `buried_treasures`, `ocean_ruins`, `pillager_outposts`, `ruined_portals`, `shipwrecks`, `trail_ruins` y `trial_chambers` en el overworld y `nether_fossils` en el Nether. Si dejas activado uno de los nombres de 1.12.2, se dejan en paz. Las claves de control del generador, `blockWorldGenerators` y sus compañeras, se omiten de una plantilla convertida con una línea en el log, ya que nada aquí las lee. En las capas de `generatorOptions` de una plantilla plana, los nombres de bloque se corrigen igual, de modo que `minecraft:grass` se convierte en `minecraft:grass_block`.
- Las funciones se reescriben línea a línea a la sintaxis de comandos de esta versión. Los ids con valores de datos pasan por los mismos data fixers, de modo que `give @p minecraft:wool 1 14` se convierte en `give @p minecraft:red_wool 1` y `give @p mypack:materials 1 5` da la variante cuyo `meta` era 5, y el nbt de ítems, entidades y bloques se corrige como se haría con el de un mundo (1.21.1: el nbt de un ítem se convierte en sus componentes). `testforblock`, `testfor` y `scoreboard players test` se convierten en `execute if`, `execute <entity> <x> <y> <z> [detect ...]` se convierte en `execute as ... at @s [positioned ...] [if block ...] run`, `effect` toma `give` y `clear`, `blockdata`, `entitydata` y `replaceitem` se convierten en `data merge` y `item replace`, y `scoreboard teams` y `scoreboard players tag` se convierten en `team` y `tag`. Los selectores cambian `score_X_min` y `score_X` por `scores`, `r` y `rm` por `distance`, `l` y `lm` por `level`, `m` por `gamemode`, `c` por `limit` y `sort`, y `rx` y `ry` por `x_rotation` y `y_rotation`. También se trasladan los números de encantamientos y efectos, los nombres de partículas y sonidos, los números de modo de juego y dificultad, la duración en segundos de un `weather` y un `tp` relativo a otra entidad. Una línea que la conversión no puede trasladar se conserva tal como está escrita y el log nombra el archivo, la línea y el motivo; una función que contiene una línea así no se carga hasta que se corrige a mano. `block_drops` se traslada tal cual, con su `meta` incorporado al nombre del bloque o a sus `properties`.
- El suelo de un mundo plano de 1.12.2 estaba en y 0, y esta versión construye un mundo plano desde su fondo en y -64, así que la conversión baja las alturas en consecuencia. Cuando el `worldType` de una plantilla de mundo es `flat` o `superflat`, su `worldSpawn` y `resetSendsTo` bajan 64 (hasta su `worldMinHeight` cuando lo nombra), y también las alturas del `spawn` de un equipo, del `at` de `standIn` y de `spawnBox` y el `opens.lobby` de un archivo de puntuación cuando todas las plantillas de mundo que incluye el pack son planas. Una dimensión de pack con terreno `flat` baja su `groundLevel`, y cualquier posición que nombre esa dimensión, igual (según su `minHeight` cuando lo tiene). Las funciones no dicen dónde se ejecutan, así que cuando el overworld del pack es plano toda y absoluta de sus funciones baja 64, la `y` de un selector incluida, y el log lo indica una vez; las alturas con `~` y `^` se dejan en paz. Un mundo con terreno normal conserva todas las coordenadas, ya que su superficie sigue estando al nivel del mar.

El log lleva una línea de resumen por pack convertido y una línea por cada archivo que movió, omitió o no pudo trasladar, y cada clave que esta versión ya no lee la nombra igualmente el parser que la encuentra. La conversión es un mejor esfuerzo, no un pack terminado: lee esas líneas y termina a mano lo que nombran, empezando por cualquier línea de comando que conservó tal como estaba escrita y cualquier textura para la que no pudo encontrar nombre. Haz esas correcciones en la raíz del pack o en un zip aparte, nunca en la carpeta `versions/` que escribió la conversión: otra versión de RDPL escribe esa carpeta de nuevo.

---

# Bloques e ítems

## Bloques

*bloques e ítems*

`<namespace>/blocks/*.json`

Cada clave dentro de `variants` es un bloque, registrado bajo el espacio de nombres del pack: un archivo que contenga `ruby_ore` y `deep_ruby_ore` registra `mypack:ruby_ore` y `mypack:deep_ruby_ore`, que comparten todos los ajustes que el archivo escribe fuera de `variants`. El nombre del propio archivo es solo una agrupación.

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita. Una clave marcada para un tipo solo la lee ese tipo.

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
| `door` | Dos bloques de alto, se abre a mano y responde a la redstone |
| `trapdoor` | Una trampilla con bisagra en la parte superior o inferior de un bloque, que se abre a mano o con redstone |
| `fence_gate` | Una puerta en una línea de vallas, que se abre a mano o con redstone y baja donde se encuentra con un muro |
| `banner` | Un estandarte sobre un poste o contra una pared, con dieciséis rotaciones de pie, que lleva tu propio diseño |
| `ladder` | Se puede trepar, colocada contra una pared |
| `torch` | Colocación en pared y en suelo, con una partícula. Emite la `light` de la variante tal como está escrita, así que una antorcha con `0` no da ninguna |
| `bell` | Una campana como la que tienen las aldeas del juego: suena al usarla por un lado, con redstone o cuando un proyectil la golpea, se balancea en su marco y hace brillar a los saqueadores cercanos. Su blockstate lleva la orientación y cómo cuelga |
| `log` | Gira hacia la cara contra la que la colocas y lleva la etiqueta `minecraft:logs`, de modo que la tala de árboles y Blast Plaster la traten como un tronco |
| `leaves` | Se descompone, se cizalla, se tiñe y suelta un brote, y lleva la etiqueta `minecraft:leaves`. Si se dejan `opaque`, se dibujan sólidas, como las hojas rápidas; pon `"opaque": false` para ver a través de ellas |
| `sapling` | Crece hasta convertirse en un árbol o en una de tus estructuras |
| `crop` | Crece por etapas, suelta una semilla y un ítem de cosecha, semillas de trigo y trigo para el que el archivo deje sin indicar |
| `flower` | Una planta de un bloque que se sostiene sobre tierra |
| `cane` | Crece hacia arriba en columna, como las cañas o el cactus |
| `vine` | Trepa y cuelga de los lados de los bloques. Con `growth` crece hacia abajo hasta `maxHeight` y, con `spread`, llega hacia los lados a las paredes vecinas; sin ello, se queda como se colocó |
| `portal` | Envía a otra dimensión a lo que entra en él |
| `container` | Contiene un inventario que un jugador puede abrir, de cualquier tamaño, y puede llenarse solo desde una tabla de botín la primera vez que se abre. Se dibuja como un bloque normal o como un cofre, según lo que pida el pack. Sin un objeto `container` contiene tres filas de nueve |

### Claves de archivo

*bloques*

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `variants` | sí | objeto de nombre de variante a variante | | Un bloque por entrada. La clave es su nombre de registro y nombra su blockstate, sus modelos, sus texturas y su clave de lang |
| `type` | no | uno de los tipos de arriba | `basic` | Qué forma toma el bloque |
| `material` | no | uno de los [materiales de bloque](#listas-de-valores) | `rock` | El bloque hace lo que hacía ese material en 1.12.2: si suelta algo al romperlo a mano, cómo lo tratan los pistones, si la lava lo incendia, si el líquido que fluye lo arrastra y si un bloque colocado lo sustituye. Un `log` es siempre `wood`, `leaves` siempre `leaves`, una `vine` `vine`, una `torch` o `ladder` `circuits`, un `crop` `plants`, y `stairs` y un `wall` se comportan como su `modelBlock` |
| `soundType` | no | uno de los [tipos de sonido](#listas-de-valores) | `stone`; `wood` para un `log`, `plant` para `leaves` y un `crop`, el del `modelBlock` para `stairs` y un `wall` | Pasos, rotura y colocación |
| `mapColor` | no | uno de los [colores de mapa](#listas-de-valores) | el del material | Cómo se ve en un mapa |
| `harvestTool` | no | `pickaxe`, `axe`, `shovel`, `hoe`, `sword` | `pickaxe` | Qué herramienta lo recolecta, escrito por ti en las etiquetas `mineable` del juego; `sword` va a `resourcedatapackloader:mineable/sword`, que minan las herramientas `sword` propias de un pack. Para los drops solo importa en un material que necesita herramienta. Como en 1.12.2, un pico también mina `rock`, `iron` y `anvil` a máxima velocidad, y un hacha `wood`, `plants` y `vine`. Cualquier otro nombre, como `shears`, se registra en el log y se deja fuera |
| `harvestToolLevel` | no | 0 a 4 | `0` | 0 madera, 1 piedra, 2 hierro, 3 diamante, 4 netherita, escrito por ti en las etiquetas `needs_*_tool`. Un bloque `basic`, `ore`, `container`, `portal`, `fence`, `pane`, `log`, `falling`, `slab` o `wall` lo ignora y toma el `harvestLevel` de la variante, como en 1.12.2 |
| `silkHarvest` | no | boolean | `true` | Si el toque de seda devuelve el propio bloque |
| `opensWith` | no | id de ítem | ninguno | Convierte el bloque en una caja con cerradura: al romperlo suelta el propio bloque, y al hacer clic derecho con el ítem nombrado se consume uno, suena el sonido de rotura del bloque, se entrega la lista `drops` de la variante y se elimina el bloque. Cualquier otro clic muestra en la barra de acciones la línea `block.<pack>.<block>.locked` de los archivos de idioma |
| `openSound` | no | nombre de sonido | el sonido de rotura | Lo que reproduce una caja con cerradura al abrirse en lugar de su sonido de rotura. Un nombre de 1.12.2 sigue leyéndose, véanse los [nombres de sonido](#listas-de-valores) |
| `expDrop` | no | objeto con `min` y `max` | ninguno | Experiencia que se suelta cuando un jugador rompe el bloque, o cuando un mob de un pack que recoge experiencia lo excava; los pistones, el agua y las explosiones no sueltan ninguna. El toque de seda la quita solo cuando `silkHarvest` está activado |
| `creativeTab` | no | nombre de pestaña | ninguno | La pestaña en la que aparece, véase [Pestañas creativas](#pestañas-creativas) |
| `renderLayer` | no | `solid`, `cutout`, `cutout_mipped`, `translucent` | según el tipo | Cómo se dibuja. En 26.x solo `translucent` tiene algún efecto; `solid`, `cutout` y `cutout_mipped` se aceptan pero se dibujan igual |
| `opaque` | no | boolean | `true` | Si bloquea por completo la vista y la luz |
| `fullCube` | no | boolean | igual que `opaque` | Si llena todo su espacio |
| `lightOpacity` | no | 0 a 255 | `255` cuando es opaco, si no `0` | Cuánta luz absorbe: 15 o más la detiene toda, y `0` deja pasar la luz del sol directamente. Un `slab` conserva el propio del juego, y un contenedor con modelo de cofre deja pasar la luz |
| `slipperiness` | no | float | `0.6` | El hielo es `0.98` |
| `flammability` | no | int | `0` | Con qué facilidad lo consume el fuego |
| `fireSpread` | no | int | `0` | Con qué facilidad se propaga el fuego desde él |
| `explosionResistanceDivisor` | no | float | `1.0` | Divide la `resistance` de cada variante frente a las explosiones |
| `modelBlock` | no | nombre de bloque | `minecraft:stone` | Bloque cuyo modelo se toma prestado cuando el tuyo no incluye textura ni modelo propio |
| `itemModel` | no | `state`, `item` | `state` | `state` sigue el blockstate, `item` busca su propio archivo, `models/item/<name>.json` |
| `tint` | no | `biome`, `none` o un color hexadecimal | ninguno | Necesita un `tintindex` en el modelo para verse |
| `plantTypes` | no | lista de [tipos de planta](#listas-de-valores) | ninguno | Qué se puede plantar sobre él |
| `behavesAs` | no | lista de `till`, `path`, `bush`, `animals` | ninguno | Comportamientos de vanilla que adopta |
| `bounds` | no | lista de seis números, 0 a 1 | bloque completo | La caja de colisión, como `[x1, y1, z1, x2, y2, z2]` |
| `requires` | no | lista de ids de mod o espacios de nombres de pack | ninguno | El archivo se omite a menos que estén presentes todos |
| `particle` | solo torch | `none`, `flame`, `colored` | `flame` | La partícula sobre una antorcha |
| `particleColor` | solo torch | color hexadecimal | `FFFFFF` | Se usa cuando `particle` es `colored` |
| `smoke` | solo torch | boolean | `true` | Si echa humo |
| `leafSapling` | solo leaves | nombre de bloque | ninguno | El brote que sueltan |
| `leafSaplingChance` | solo leaves | int | `5` | Una de cada N hojas suelta uno |
| `seed` | solo crop | nombre de ítem | `minecraft:wheat_seeds` | El ítem que lo planta y lo que suelta un cultivo sin madurar |
| `produce` | solo crop | nombre de ítem | `minecraft:wheat` | Lo que da la cosecha |
| `maxAge` | solo crop | int | `7` | Cuántas etapas de crecimiento |
| `growth` | solo plantas | objeto | ninguno | Véase [Crecimiento](#crecimiento) |
| `sapling` | solo sapling | objeto | ninguno | Véase [Brotes](#brotes) |
| `portal` | solo portal | objeto | ninguno | Véase [Portales y puertas dimensionales](#portales-y-puertas-dimensionales) |
| `container` | solo container | objeto | ninguno | Véase [Contenedores](#contenedores) |
| `bell` | solo bell | objeto | ninguno | Véase [Campanas](#campanas) |

### Claves de variantes

*bloques*

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `hardness` | no | float | `1.0` | Cuánto se tarda en romperlo. La obsidiana es `50`, `-1` es irrompible |
| `resistance` | no | float | `5.0` | Resistencia a las explosiones como la lee 1.12.2: el bloque conserva tres quintos de la cifra, así que `10` da el `6` de la piedra |
| `light` | no | 0 a 15 | `0` | Luz emitida |
| `harvestLevel` | no | 0 a 4 | `0` | El nivel de herramienta de un bloque `basic`, `ore`, `container`, `portal`, `fence`, `pane`, `log`, `falling`, `slab` o `wall`, en lugar del `harvestToolLevel` del archivo. Los demás tipos siguen el valor del archivo |
| `rarity` | no | `common`, `uncommon`, `rare`, `epic` | `common` | Color del nombre en el tooltip |
| `maxSize` | no | 1 a 64 | `64` | Tamaño de pila |
| `tags` | no | lista de ids de etiqueta | ninguno | Etiquetas de bloque e ítem en las que se escribe esta variante, como `forge:ores/ruby` en 1.20.1 o `c:ores/ruby` en 1.21.1. Los archivos de etiquetas se generan por ti |
| `drops` | no | lista de drops | se suelta a sí mismo | Lo que da al romperlo |
| `portal` | solo portal | objeto | el del archivo | El portal propio de esta variante en lugar del del archivo, escrito como en [Portales y puertas dimensionales](#portales-y-puertas-dimensionales). El archivo sigue necesitando uno propio |

**Los nombres son permanentes.** La clave de una variante se escribe en cada mundo guardado que la contenga. Renombrarla más tarde convierte los bloques colocados en aire, a menos que un [renombrado de registro](#renombrados-de-registro) asigne el nombre antiguo al nuevo. Un archivo puede contener tantas variantes como quiera; cada una es un bloque propio, y una clave `meta` de un pack de 1.12.2 se ignora con una nota en el log.

### Drops

*bloques*

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

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `block` | una de las dos | nombre de bloque o ítem | | Lo que se suelta |
| `entity` | una de las dos | nombre de entidad | | Una entidad liberada cuando el bloque se rompe, en lugar de un ítem |
| `amount` | no | int o rango | `1` | Cuántos |
| `chance` | no | 0 a 100 | `100`, o `0` cuando `guaranteed` está desactivado | Con qué frecuencia ocurre el drop |
| `weight` | no | int | `0` | Por encima de cero, la entrada se une a una reserva que da exactamente un drop. Véase más abajo |
| `bonusChance` | no | lista de ints | ninguno | Drops extra por nivel de fortuna, una entrada por nivel |
| `guaranteed` | no | boolean | `true` | Abreviatura heredada de `chance`. Activado es `100`, desactivado es `0` |

Cada entrada sin `weight` se decide por separado, así que un bloque con tres de ellas puede soltar las tres, o ninguna. Dale un `weight` a las entradas y dejan de ser independientes: forman una reserva, de la que se elige exactamente una cada vez que se rompe el bloque, con probabilidades proporcionales a los pesos. Arriba, el diamante y la esmeralda comparten una reserva en proporción de uno a cuatro, así que siempre sale uno de los dos y es la esmeralda cuatro veces de cada cinco, mientras que el rubí y el carbón se deciden por separado y el lepisma es otra cosa distinta. Los ítems y las entidades se agrupan por separado, de modo que un ítem con peso y una entidad con peso no compiten.

Una entrada que nombra una `entity` libera una donde estaba el bloque, mirando en una dirección aleatoria, y a un mob se le da su tratamiento de aparición habitual según la dificultad local, de modo que llega con el equipo y los efectos que habría tenido. `amount` decide cuántas, `chance` con qué frecuencia, y `weight` la mete en la reserva de entidades. Ocurre al romperse el bloque, sea como sea que se rompa, así que una explosión o un pistón las suelta igual que lo hace un pico. `bonusChance` y la fortuna no significan nada para una entidad y se ignoran.

Un drop que nombra a la vez un `block` y una `entity` usa la entidad y lo indica en el log.

Los drops se escriben en una tabla de botín generada, `loot_tables/blocks/<name>.json` bajo el espacio de nombres del pack, a menos que el pack incluya una propia en esa ruta, en cuyo caso el bloque usa el archivo del pack y `drops` no se lee.

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

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `stages`          | no       | int                 | `16`             | Etapas de crecimiento hasta completarse. Una enredadera intenta crecer en uno de cada tantos random ticks |
| `growth`          | no       | int                 |                  | Probabilidad de 1 entre N por random tick de avanzar |
| `spread`          | no       | int                 | `0`              | Hasta dónde se extiende a los bloques vecinos. Una enredadera deja de extenderse hacia los lados cuando hay tantas enredaderas como este valor a dos bloques o menos de ella |
| `maxHeight`       | no       | int                 | `3`              | Caña y enredadera. Hasta qué altura crece la columna, o cuánto cuelga una enredadera; una enredadera con `1` ni crece ni se extiende |
| `soil`            | no       | lista de nombres de bloque | el habitual del tipo | Sobre qué se asienta |
| `drop`            | no       | nombre de ítem      | ninguno          | Caña y enredadera. Qué suelta al romperse; una flor se suelta a sí misma |
| `dropCount`       | no       | int                 | `1`              | Caña y enredadera. Cuántos |
| `needsSky`        | no       | boolean             | `false`          | Solo crece donde se ve el cielo |
| `needsWater`      | no       | boolean             | `false`          | Solo crece cerca del agua |
| `waterRange`      | no       | int                 | `1`              | A qué distancia puede estar esa agua |
| `damage`          | no       | boolean             | `false`          | Hace daño a lo que lo toca |
| `damageAmount`    | no       | float, medios corazones | `1.0`        | Cuánto daño hace |
| `breaksNeighbors` | no       | boolean             | `false`          | Rompe los bloques colocados a su lado, como el cactus |

### Brotes

*bloques*

Todas las claves a la vez. Un archivo real solo escribe las que necesita.

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

Una `structure` sustituye el árbol generado por una de tus plantillas, que es la forma de construir algo que un generador no puede, y no hace falta escribir nada más en el bloque. Si en su lugar nombras varias en `structures`, el brote elige una cada vez que crece, de modo que un bosque no sea el mismo árbol una y otra vez:

```json
{
  "sapling": { "structure": "mypack:ruby_tree" }
}
```

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `soil`       | no       | lista de nombres de bloque | ninguno           | Sobre qué crecerá |
| `stages`     | no       | int                 | `2`                    | Etapas de crecimiento hasta convertirse en árbol |
| `chance`     | no       | int                 | `7`                    | Una entre N por random tick |
| `light`      | no       | 0 a 15              | `9`                    | Nivel de luz necesario |
| `log`        | no       | nombre de bloque    | `minecraft:oak_log`    | Bloque del tronco |
| `leaves`     | no       | nombre de bloque    | `minecraft:oak_leaves` | Bloque de las hojas |
| `height`     | no       | int                 | `4`                    | Altura del tronco |
| `vines`      | no       | boolean             | `false`                | Cuelga enredaderas de las hojas |
| `structure`  | no       | `namespace:name`    | ninguno                | Crece como esta plantilla en lugar de como un árbol generado |
| `structures` | no       | lista               | ninguno                | Varias plantillas en las que crecer, una elegida cada vez que crece. Cada entrada es `{ "structure": "namespace:name", "weight": 3 }`, o un nombre suelto para probabilidades iguales. Sustituye a `structure` |

## Contenedores

*bloques e ítems*

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

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `rows`       | int             | `3`     | Cuántas filas de ranuras, de 1 a 9 |
| `columns`    | int             | `9`     | Cuántas ranuras por fila, de 1 a 12 |
| `lootTable`  | texto           | vacío   | Una loot table que se sortea dentro del bloque la primera vez que algo accede a su contenido, ya sea un jugador al abrirlo, una tolva, un comparador o al romperlo, exactamente como se llena un cofre de mazmorra. Un contenedor que coloca un jugador nunca la sortea. Vacío lo deja empezando vacío |
| `chestModel` | boolean o texto | `false` | Se dibuja como un cofre con una tapa que se abre, en lugar de como un bloque normal con tu propia textura. `true` usa el aspecto del cofre de vanilla; un nombre de textura como `mypack:entity/chest/strongbox` usa en su lugar tu propia hoja de cofre, tanto para el bloque colocado como para el ítem. Un bloque con modelo de cofre también pone `opaque` a `false` por defecto, como un cofre de vanilla, de modo que la luz no se corta en el bloque y el cofre no se dibuja oscuro |
| `guiTexture` | texto           | vacío   | Tu propia imagen de fondo para la pantalla. Vacío dibuja una a partir de la pantalla del cofre de vanilla, con el tamaño que necesiten las filas y columnas |
| `guiWidth`   | int             | ninguno | Cuánto mide de ancho la pantalla, dibujada desde la esquina superior izquierda de esa imagen leída como una hoja de 256 por 256, obligatoria con `guiTexture` |
| `guiHeight`  | int             | ninguno | Cuánto mide de alto la pantalla, obligatoria con `guiTexture` |
| `curioSlot`  | texto           | vacío   | Solo para ítems: la ranura de Curios en la que se puede llevar puesto, `back`, `belt`, `body`, `charm`, `head`, `necklace`, `ring` o cualquier ranura que añada otro mod. Una mochila suele usar `back`. Se ignora, sin que el resto del ítem deje de funcionar, cuando Curios no está instalado. La clave de 1.12.2 `bauble` se lee como esta y acepta los nombres de Baubles: `amulet` pasa a ser `necklace`, `ring` da dos ranuras de anillo, `belt`, `head`, `body` y `charm` conservan su nombre, y `trinket` encaja en todas esas ranuras. Cualquier otro nombre deja el ítem sin poder llevarse, con una línea de error |

**Nueve filas por doce es el límite**, lo máximo que cabe en una pantalla. Un pack que pida más se recorta a ese límite con una línea de error que lo indica. Una advertencia sobre la más alta: una pantalla de nueve filas mide 276 píxeles, y una pantalla de 1080 con la escala de GUI en `auto` ofrece 270, de modo que la parte superior e inferior se recortan tres píxeles cada una; con escala 3 se ve entera.

**La pantalla se dibuja, no se distribuye.** Un contenedor de nueve columnas o menos y seis filas o menos usa la pantalla del cofre de vanilla tal cual, así que se ve exactamente como un cofre de ese tamaño. Cualquier cosa mayor se ensambla a partir de la misma imagen al dibujar: el borde superior, una fila de ranuras repetida hasta ajustarse y la parte inferior con el inventario del propio jugador, de modo que un pack puede pedir tamaños que ninguna pantalla de vanilla cubre sin distribuir una imagen propia. `guiTexture` anula todo eso cuando un pack quiere su propio aspecto, y entonces `guiWidth` y `guiHeight` deben indicar su tamaño o se usa la dibujada y una línea de error lo dice.

**Qué hace el bloque.** Conserva su contenido al guardar y recargar, lo suelta al romperse, responde a un comparador según lo lleno que esté y conserva las filas y columnas con las que se creó, de modo que cambiarlas después en el pack deja los contenedores que ya hay en un mundo como estaban. Un ítem contenedor o un bloque contenedor no se puede meter dentro de un contenedor. `chestModel` también le da el sonido de apertura del cofre y la animación de la tapa; si se deja desactivado, el bloque se dibuja con su propia textura como cualquier otro bloque, así que sirven igual una caja, un barril o un armario.

**Colorear un cofre.** La hoja del cofre es una textura normal, de modo que un mapa de píxeles puede recolorear la de vanilla sin dibujar un solo píxel: haz que la `extends` y dale un `tint`, y luego nombra ese mapa en `chestModel`.

```json
{
  "extends": "minecraft:textures/entity/chest/normal",
  "tint": {
    "from": "#241A12",
    "to": "#D8BC80"
  }
}
```

**Un ítem contenedor es una bolsa**, un ítem de tipo `container` que lleva el mismo bloque `container` con `rows` y `columns`; se abre con clic derecho y conserva su contenido al cambiar de manos. Sin el objeto `container` tiene una sola fila de nueve. Dale `curioSlot` y, donde Curios esté instalado, va en esa ranura y una tecla lo abre sin quitárselo, `V` por defecto, reasignable en Resource Data Pack Loader dentro de los controles. Al pulsarla de nuevo, con un contenedor puesto ya abierto, pasa al siguiente que lleves y da la vuelta al llegar al final, de modo que se pueda acceder a varios llevados a la vez. La tecla solo aparece cuando Curios está presente, y todo lo demás del ítem, el clic derecho y su inventario, funciona esté o no. La posibilidad de llevar la bolsa puesta se escribe por ti en la etiqueta de Curios de esa ranura.

**La loot table se llena en el primer uso**, no al colocar el bloque, que es lo que la hace útil en una estructura: quien lo abra primero recibe el sorteo, y una tolva o un comparador que lo alcance antes lo sortea igual. La misma tabla puede usarse con `lootTable` en una forma de impronta o en una parcela de aldea, de modo que un pack puede colocar estos contenedores mediante worldgen y abastecerlos del mismo modo.

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
    "sound": "minecraft:block.note_block.bell",
    "resonateSound": "minecraft:block.note_block.chime"
  },
  "variants": { "village_bell": { "hardness": 5.0, "resistance": 30 } }
}
```

Y su blockstate, `assets/mypack/blockstates/village_bell.json`, con claves `attachment` y `facing`:

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

| Ajuste | Tipo | Por defecto | Qué hace |
| --- | --- | --- | --- |
| `swing`         | boolean    | `true`                             | Dibuja la parte oscilante a partir de un archivo de modelo propio y la hace oscilar cuando suena la campana. `false` dibuja la campana entera a partir de los modelos del blockstate, sin nada animado |
| `sound`         | nombre de sonido | `minecraft:block.note_block.bell`  | Se reproduce cuando suena la campana. Vacío la hace sonar en silencio |
| `resonateSound` | nombre de sonido | `minecraft:block.note_block.chime` | Se reproduce cuando la campana resuena porque hay saqueadores cerca. Vacío la hace resonar en silencio |

**Cuelga como la campana del propio juego.** Colocada sobre un bloque, se apoya en el suelo, girada hacia donde miras; bajo un bloque, cuelga del techo; contra una pared, cuelga de esa pared, y entre dos paredes cuando el lado opuesto también es sólido. Se cae cuando desaparece lo que la sostiene, y una campana entre dos paredes pasa a ser una campana de una sola pared cuando una de ellas desaparece. Su hitbox sigue la de vanilla en cada uno de los cuatro casos, de modo que `bounds` no se lee.

**Qué la hace sonar.** Usarla por el lateral del cuerpo, bajo la viga: una campana de suelo por las dos caras que recorre su viga, una campana de pared por las dos caras junto a la pared, una campana de techo por cualquier lado. La parte superior, la inferior y cualquier cosa por encima del cuerpo no hacen nada. Una señal de redstone la hace sonar una vez al activarse, y una flecha, una bola de nieve o cualquier otro proyectil la hace sonar al golpear un lado que una mano podría alcanzar. El cuerpo oscila alejándose del lado por el que lo golpearon durante dos segundos y medio; la redstone lo hace oscilar en la dirección hacia la que mira la campana.

**Qué hace un toque.** Los aldeanos a menos de 32 bloques lo oyen y corren a casa a esconderse durante quince segundos, como los manda la campana del propio juego. Cuando hay un saqueador a menos de 32 bloques, la campana resuena un cuarto de segundo después del toque, y dos segundos más tarde todos los saqueadores a menos de 48 bloques brillan durante tres segundos, con partículas de colores junto a la campana en el lado donde se encuentra cada uno. Es saqueador todo lo que envía un [asalto](#asaltos), y los illagers y brujas del propio juego. Una campana de este tipo es una campana de aldea para todos los asaltos: suena al llegar cada oleada sin que se nombre en el `bell` del asalto.

**Los modelos.** Una campana se identifica por `attachment` y `facing`, dieciséis estados en total, y `powered` se deja fuera de las claves. Con `swing` activado, esos modelos dibujan solo el marco, y la parte oscilante es un archivo de modelo propio, `<namespace>:block/<name>_body`: el mod lo carga por esa ruta y el renderizador lo dibuja, así que el nombre no lo eliges tú y nunca aparece en el blockstate. Se modela en el espacio del bloque donde reposa el cuerpo, y se inclina alrededor del punto situado medio bloque hacia dentro y tres cuartos de bloque hacia arriba, como en vanilla. Con `swing` desactivado no hay modelo de cuerpo, y los dieciséis modelos de marco dibujan la campana entera. El modelo de ítem escrito para la mano dibuja el marco y el cuerpo juntos, así que la campana se ve entera en la mano; distribuye `models/item/<name>.json` para dibujarla de cualquier otra forma.

**El cliente dibuja la oscilación.** Un toque llega a los jugadores como un evento de bloque, de modo que un servidor dedicado hace oscilar la campana para todos los que tengan este mod, y un jugador sin él solo oye la campana. Los sonidos, la resonancia y el brillo ocurren todos en el servidor.

## Modelos, blockstates y texturas

*bloques e ítems*

Definir un bloque o un ítem lo registra. Su *aspecto* es un conjunto de archivos de recursos en las mismas carpetas y el mismo formato que usa el juego, bajo tu propio espacio de nombres, y en esta versión la mayoría se escriben por ti.

```
assets/mypack/textures/block/ruby_ore.png
assets/mypack/textures/item/ruby.png
assets/mypack/lang/en_us.json
```

**Distribuye una textura y el resto se genera.** Para cada bloque cuyo blockstate no distribuye el pack, el mod escribe el blockstate y los modelos que el tipo necesita, apuntando a `textures/block/<name>.png`, donde `<name>` es la clave de la variante, y para cada ítem que no tiene `models/item/<name>.json`, un modelo de ítem que apunta a `textures/item/<name>.png`. Un bloque sin textura ni modelo propio toma prestado el aspecto de `modelBlock`, piedra por defecto, de modo que nada se muestra nunca como el cuadrado morado y negro. Distribuye un `blockstates/<name>.json` propio y el mod no genera nada para ese bloque y usa el tuyo; lo mismo con `models/item/<name>.json`.

| Tipo | Archivos de textura que busca | Se genera a partir de |
| --- | --- | --- |
| `basic`, `ore`, `falling`                                          | `<name>`, con `<name>_top` y `<name>_bottom` para las caras superior e inferior cuando el pack las distribuye | `cube_all`, o `cube_bottom_top` cuando hay una textura superior o inferior |
| `flower`, `sapling`, `cane`, `leaves`, `container` sin cofre | `<name>`                                                                                               | `cube_all`, `cross` o `leaves` |
| `log`                                                              | `<name>` para el lateral, `<name>_top` para los extremos                                                       | `cube_column` |
| `slab`                                                             | `<name>`                                                                                               | `slab`, `slab_top` y un doble `cube_all` |
| `stairs`                                                           | `<name>`                                                                                               | `stairs`, `inner_stairs`, `outer_stairs`, con los cuarenta estados escritos |
| `fence`                                                            | `<name>`                                                                                               | `fence_post` y `fence_side` como multipart, y `fence_inventory` para la mano |
| `wall`                                                             | `<name>`                                                                                               | las plantillas de poste y lateral de muro como multipart, y `wall_inventory` para la mano |
| `pane`                                                             | `<name>` para el panel, `<name>_top` para el borde                                                       | las cinco plantillas de panel de cristal como multipart |
| `door`                                                             | `<name>_top` y `<name>_bottom`, o `<name>` para ambas                                                 | los ocho modelos de puerta y sus treinta y dos estados |
| `trapdoor`                                                         | `<name>`                                                                                               | los tres modelos orientables de trampilla |
| `fence_gate`                                                       | `<name>`                                                                                               | los cuatro modelos de puerta de valla, cerrada y abierta, en un muro y fuera de él |
| `ladder`, `vine`, `torch`                                          | `<name>`                                                                                               | la plantilla del propio juego para cada uno |
| `bell`                                                             | `<name>`                                                                                               | los marcos de campana del propio juego como `<name>_floor`, `<name>_ceiling`, `<name>_wall` y `<name>_between_walls`, el `pack_bell_body` del mod como `<name>_body`, y un blockstate de dieciséis estados de `attachment` y `facing` sobre ellos |
| `crop`                                                             | `<name>_stage0` hasta `<name>_stage<maxAge>`, o `<name>` para todas                                      | un modelo `crop` por etapa, con `age=0` a `7` asignados a ellos |
| `portal`                                                           | `<name>`, o la del portal del Nether                                                                       | `cube_all` para un bloque `fullCube`, como es un bloque de portal en 1.12.2; tres losas de portal, una por eje, para uno que no lo es, como el portal de marco de una dimensión |
| `banner`                                                           | su propia hoja, véase [Estandartes](#estandartes)                                                                         | el modelo de estandarte del juego |
| `container` con `chestModel`                                      | la hoja de cofre nombrada en `chestModel`                                                                  | el modelo `pack_chest` del mod |

Cada textura se busca en `textures/block/`, y el nombre es la clave de la variante, de modo que un bloque registrado como `ruby_ore` quiere `textures/block/ruby_ore.png` y no hace falta escribir nada más. Un bloque cuyo ítem se dibuja plano, una puerta, una escalera de mano, una antorcha, un brote, una flor, una caña, una enredadera o un panel, toma `textures/item/<name>.png` para la mano cuando existe y la textura de su bloque cuando no.

**Los ítems** toman `textures/item/<name>.png` y un modelo `item/generated` generado, o `item/handheld` para una herramienta. Distribuye `models/item/<name>.json` para dibujarlo de cualquier otra forma.

**Los fluidos** no necesitan ningún modelo; se genera uno a partir de las texturas `still` y `flow`.

**Un bloque con varias variantes son varios bloques.** Cada clave bajo `variants` se registra por separado, de modo que cada una tiene su propio blockstate, sus propios modelos y sus propias texturas, con el nombre de la clave. No hay un blockstate compartido con una propiedad `blocks`, y nada en un blockstate necesita indicar de qué variante se trata: `blockstates/ruby_ore.json` es el del mineral de rubí, y `blockstates/deep_ruby_ore.json` es el del profundo.

### Escribir los tuyos

*modelos, blockstates y texturas*

Todo lo generado se puede sustituir. Un blockstate que distribuye el pack se usa tal cual, en el formato del propio juego: los `variants` de vanilla con las propiedades del bloque como claves, o `multipart`. Las propiedades son las del propio juego para cada tipo: `axis` en un tronco, `type` en una losa, `facing`, `half` y `shape` en unas escaleras, `facing`, `half`, `hinge` y `open` en una puerta, `facing`, `half` y `open` en una trampilla, `facing`, `in_wall` y `open` en una puerta de valla, `age` en un cultivo y una caña, `stage` en un brote, `north`, `east`, `south`, `west` en una valla o un panel, con `up` añadido en un muro y una enredadera, `rotation` en un estandarte de pie y `facing` en uno de pared, `axis` en un portal, `attachment` y `facing` en una campana con su `powered` fuera de las claves. Un bloque `basic`, `ore`, `falling`, `leaves`, `flower` o `container` tiene un solo estado, con la clave `""`.

Apunta los modelos a los padres que aceptan texturas, no a los de vanilla ya terminados: `cube_all` toma un `all`; `cube_column` un `end` y un `side`; `cross` un `cross`; los padres de escaleras `bottom`, `top` y `side`; `fence_post` y `fence_side` una `texture`; `template_wall_post` y `template_wall_side` un `wall`; las plantillas de panel de cristal un `pane` y un `edge`; los padres de puerta un `top` y un `bottom`; `template_orientable_trapdoor_*` y `template_fence_gate*` una `texture`; `template_torch` un `torch`; `crop` un `crop`; `vine` y `ladder` su propio nombre. Un modelo que nombra un modelo de vanilla ya terminado, como `oak_door_bottom_left`, hereda con él las texturas de vanilla, diga lo que diga el blockstate.

### Estandartes

*modelos, blockstates y texturas*

Un estandarte es el único tipo en el que la forma del bloque y la forma del modelo van por caminos distintos, así que merece explicarse por completo.

**Registra dos bloques.** Una definición te da el estandarte de pie con tu propio nombre y un segundo bloque llamado `<name>_wall` para el colgado. Ambos necesitan un blockstate; solo el de pie recibe un ítem, y ese ítem decide cuál de los dos coloca: de pie cuando haces clic en la parte superior de un bloque y de pared cuando lo haces en un lateral. Nunca colocas directamente el bloque de pared y no necesita ítem propio.

**El de pie gira en dieciseisavos.** Su propiedad es `rotation`, de `0` a `15`, porque un estandarte gira en dieciseisavos y no en cuartos. La `y` de un blockstate solo acepta 0, 90, 180 y 270, de modo que cada rotación apunta a un pequeño modelo propio que toma tu modelo de estandarte como padre y lo gira con un `transform`:

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

…y así hasta `15`, cada uno `-22.5` grados más allá. El signo coincide con los estandartes del propio juego, que giran en menos la rotación. Construye el modelo mirando al sur, ya que ahí acaba apuntando un estandarte colocado por un jugador que mira al sur. El bloque de pared es un blockstate normal con las cuatro entradas habituales de `facing` a 0, 90, 180 y 270, ya que no tiene nada de fraccionario. El blockstate de Forge de un pack de 1.12.2 se convierte exactamente en esto al convertir el pack.

**El modelo mide casi dos bloques de alto.** Un estandarte ocupa un bloque para la colocación y la colisión, pero se dibuja muy por fuera de él, y un modelo que se detiene en la parte superior de su propio bloque parece raquítico. Conviene copiar exactamente las proporciones de vanilla, en dieciseisavos de bloque:

| Parte | Desde | Hasta |
| --- | --- | --- |
| Poste | `0` | `28` |
| Travesaño | `28` | `29.33` |
| Tela | `2.67` | `29.33` |
| Ancho de la tela | `1.33` | `14.67` |
| Tela de pared | `-13` | `13.67` |

Así que un estandarte de pie llega hasta `29.33`, casi dos bloques, y un estandarte de pared cuelga trece dieciseisavos *por debajo* del bloque que lo sostiene. Los elementos del modelo pueden ir de `-16` a `32`, de modo que ambos caben. La forma de pared no tiene poste ni travesaño, solo tela.

**La tela es el doble de alta que de ancha, y tu textura también tiene que serlo.** Esa cara mide `13.33` por `26.67`. Si asignas una textura cuadrada, el diseño se comprime a la mitad de su altura. Las texturas de bloque no pueden ser el doble de altas que de anchas, ya que todo lo que no es cuadrado se lee como una animación, así que el modo de evitarlo es una hoja cuadrada más grande con la tela en una parte: un archivo de 32×32 que contiene la tela como una región de 16×32, direccionada como `"uv": [0, 0, 8, 16]`, con las tiras del poste y el travesaño en el espacio contiguo. Las coordenadas UV siempre van de 0 a 16 sea cual sea la resolución del archivo, así que los mismos números sirven con cualquier tamaño.

**Su ítem quiere un modelo propio.** Un ítem que hereda un modelo tan alto se saldrá de su ranura a la escala de bloque habitual, así que da a `models/item/<name>.json` un bloque `display` propio con la escala reducida y todo el conjunto desplazado de vuelta al encuadre.

**Sin blockstate se dibuja a partir de una hoja.** Un estandarte cuyo pack no distribuye blockstate recibe uno generado, y el renderizador de estandartes del juego lo dibuja con la forma del estandarte de vanilla a partir de la hoja en `textures/entity/banner/<name>.png`, dispuesta como la del estandarte de vanilla. Esto es una incorporación de esta versión; los bloques de pie y de pared lo deciden cada uno por su propio blockstate.

**No tiene colores ni patrones.** Un estandarte de pack no lleva una lista de capas como los estandartes de vanilla. El diseño es la textura, igual que el aspecto de una puerta es su textura, y una definición es un estandarte. Teñirlo y apilarle patrones no es algo a lo que un pack pueda llegar.

**Toma el `material` que le des.** Un estandarte de piedra se pica con un pico como la piedra que dice ser.

### Texturas escritas como mapas de píxeles

*modelos, blockstates y texturas*

Una textura puede ser un archivo JSON en lugar de un PNG. Ponla donde habría ido el PNG con `.json` al final del nombre completo, de modo que `textures/block/panel.png.json` responde a todas las peticiones de `textures/block/panel.png`. Nada más cambia: los modelos apuntan a `mypack:block/panel` como siempre, y el atlas, los mipmaps y un `.mcmeta` de animación funcionan, porque lo que recibe el juego sigue siendo un PNG. El pack de ejemplo no distribuye ni un solo PNG; todas sus texturas son mapas.

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

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `size`    | sí, o heredada | `anchoxalto`              |         | Cuántos píxeles de ancho y de alto |
| `rows`    | sí, o heredada | lista de texto            |         | Una cadena por fila de píxeles, un carácter por píxel, de arriba abajo |
| `palette` | sí, o heredada | objeto                    |         | Un carácter para cada color, `#RRGGBB` o `#AARRGGBB` |
| `extends` | no                | otro mapa de píxeles      |         | El mapa del que parte este |
| `tint`    | no                | objeto con `from` y `to`  |         | Recolorea todo lo heredado a lo largo de una rampa entre dos colores |
| `notes`   | no                | objeto                    |         | Un carácter para una línea que dice para qué sirve, heredada y nunca dibujada |

**No hay ningún nombre que declarar.** La ruta del propio archivo es su nombre, exactamente como la de un PNG, de modo que un mapa en `assets/mypack/textures/block/panel.png.json` es `mypack:block/panel` en un modelo y un mapa en `assets/mypack/textures/item/gem.png.json` es `mypack:item/gem` en un modelo de ítem. Nada apunta a un mapa de píxeles de forma especial; un bloque o un ítem nombra su textura como siempre y nunca llega a saber cuál de los dos recibió. Eso significa también que las carpetas de bloques e ítems siguen separadas, como con los PNG: `textures/block/gem.png.json` y `textures/item/gem.png.json` son dos texturas distintas y se guardan en caché como dos archivos distintos.

**Del tamaño que quieras**, hasta 4096 por lado, y los dos lados no tienen por qué coincidir. `16x16` es una cara de bloque normal, `16x32` es el tipo de tira alta que quiere la mitad de una puerta o una animación. El tamaño se comprueba en lugar de adivinarse: da una fila por cada línea de píxeles y un carácter por cada píxel de ancho, o el mapa se rechaza y el registro indica la fila y lo que encontró. Un carácter sin color en la paleta se deja transparente, así que `.` o un espacio es un hueco.

**Las plantillas son la clave de todo.** `extends` nombra otro mapa de píxeles, como `namespace:path` o una ruta sin más dentro del mismo pack, y el archivo que lo extiende hereda su `size`, sus `rows` y su `palette`. Lo que nombre por sí mismo prevalece, y no necesita nombrarlo todo, de modo que una variante entera puede ser un puñado de colores:

```json
{
  "extends": "mypack:textures/block/panel.png",
  "palette": { "s": "#AA7EB1", "d": "#8B6292", "e": "#6B4A72", "p": "#C5A1CB" }
}
```

Eso es una segunda textura completa: la misma forma en púrpura, y si algún día se redibuja la forma en la plantilla, todas las variantes la siguen. Una variante puede en cambio dar sus propias `rows` y conservar la paleta de la plantilla, que es lo inverso: los mismos colores en un patrón distinto. La herencia llega hasta ocho niveles, un bucle se detecta y se notifica, y un mapa que nombra una plantilla que nada proporciona se notifica en lugar de dibujarse en blanco.

**Cuál de dos texturas es la plantilla** se decide por cuál contiene más distinciones, no por cuál se dibujó primero. Una variante da un color a cada carácter, de modo que todos los píxeles que la plantilla llama con el mismo carácter salen del mismo color en la variante. Por eso un mineral dibujado sobre piedra no puede heredar las `rows` de la piedra: la piedra llama piedra normal a las posiciones de las motas, y nada que pueda escribir una variante separa un carácter en dos. Dale la vuelta y funciona. Haz que el mineral sea la plantilla, de modo que los tonos de la piedra y los del mineral tengan cada uno sus propios caracteres, y un segundo mineral son cuatro colores:

```json
{
  "extends": "mypack:textures/block/ore_template",
  "palette": { "4": "#768291", "5": "#5E6977", "6": "#66717F", "7": "#848F9D" }
}
```

Una variante que de verdad quiere un patrón distinto da sus propias `rows`, como antes, y entonces hereda solo la paleta. Merece la pena cuando los colores son lo importante y la forma es secundaria; cuando lo importante es la forma, pon la forma en la plantilla y deja que las variantes nombren colores.

**Una plantilla no tiene por qué ser una textura.** Un mapa solo se sirve al juego cuando su ruta termina en `.png`, de modo que una plantilla en `textures/block/ore_template.json` es invisible para el juego y existe únicamente para ser extendida, mientras que una en `textures/block/ore_template.png.json` también respondería a peticiones de `ore_template.png`. Nombra una forma compartida sin el `.png` y nada podrá pedirla por accidente.

**Una plantilla puede ser una imagen real en lugar de un mapa.** Apunta `extends` a un PNG que proporcione cualquier pack o el propio juego y la paleta cambia de significado: las claves pasan a ser los colores que ya hay en esa imagen, y los valores, los colores que se ponen en su lugar. No se traza nada y no se escriben `rows`, de modo que un pack puede recolorear una textura de vanilla o de un mod donde está:

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

Eso es un mineral de rubí en la piedra propia de vanilla: se cambian los cuatro tonos de las motas y todos los demás píxeles se dejan como estaban. Un color que la imagen no contiene simplemente nunca coincide, y el tamaño sale de la imagen a menos que nombres uno, que entonces debe coincidir.

`extends` da preferencia a un mapa de píxeles: busca primero el mapa en esa ruta y solo recurre a la imagen cuando ningún pack proporciona uno. Un nombre que no es ninguna de las dos cosas se notifica en lugar de dibujarse en blanco. Construir sobre una imagen es trabajo del lado del cliente, ya que se leen los recursos del propio juego, así que un servidor dedicado nunca lo hace.

**Una plantilla se puede teñir en lugar de repintar.** `tint` nombra dos colores y recolorea todo lo que el mapa hereda a lo largo de la rampa entre ellos. El brillo de cada color heredado es su posición en esa rampa, de modo que el negro cae en `from`, el blanco cae en `to` y cada tono intermedio se mezcla en proporción. La transparencia no se toca. Así, una plantilla en escala de grises más dos colores es una variante completa:

```json
{
  "extends": "mypack:textures/item/materials/ingot.png",
  "tint": { "from": "#626669", "to": "#DBDFE2" }
}
```

`from` puede omitirse, en cuyo caso es negro y el tinte se convierte en una multiplicación normal, con la misma forma que un `tintindex` en tiempo de renderizado. La diferencia es que este se dibuja una vez en el PNG y se guarda en caché, de modo que no cuesta nada por fotograma y llega a una textura que nada tiñe, pero tampoco puede seguir a un bioma como lo hacen `grass` o `foliage`.

La plantilla sigue siendo un mapa normal: ábrelo, míralo y se dibuja con el gris que es. Ambos colores aceptan `#RRGGBB`, `#AARRGGBB` o un `0x` inicial, y un valor que no sea ninguno de ellos deja el mapa sin dibujar en lugar de dibujarlo con el color equivocado. Un tinte se hereda como todo lo demás y gana el primero de la cadena, de modo que el tinte propio de una variante prevalece sobre el del mapa que extiende. También funciona con una plantilla de imagen, donde se aplica después de los cambios de color de la paleta.

**Un tinte es una rampa entre dos colores**, así que solo sirve para una textura cuyos tonos estén en una. Una forma con dos regiones sin relación, la piedra de un mineral frente a sus motas, no es el caso, y requiere que se escriba su paleta completa.

**Saber qué significan los caracteres de una plantilla** es la parte incómoda de extender una, y para eso está el bloque `notes` de arriba: un carácter para una línea breve, heredada igual que la paleta y nunca dibujada. Etiqueta los caracteres de una plantilla y quien la extienda sabrá cuáles sobrescribir.

`/rdpl pixelmap <namespace:path>` informa entonces de en qué ha resultado realmente un mapa, que es la forma fiable de escribir una variante sin abrir todos los archivos de la cadena:

```
oretest:textures/block/ruby_ore.png is 16x16
  built from oretest:textures/block/ruby_ore.png.json
  built from oretest:textures/block/gem_ore.png.json
  rows come from oretest:textures/block/gem_ore.png.json
  1  #C4353F  17 pixel(s)  set by ruby_ore.png.json  ore body, most of every lump
  2  #8E2029   8 pixel(s)  set by ruby_ore.png.json  ore shadow, the darkest tone
  a  #747474  86 pixel(s)  set by gem_ore.png.json   stone, the commonest tone
```

Cada carácter aparece con su color, cuántos píxeles cubre, qué archivo de la cadena lo definió y para qué dice ese archivo que sirve. La ruta puede darse abreviada, `mypack:block/panel`, o completa. Un carácter con 0 píxeles es uno que la paleta nombra y las filas nunca usan, lo que suele ser una errata en una fila.

**Las imágenes dibujadas se guardan en disco** en `rdploader/pixelmap-cache`, en una carpeta por espacio de nombres y con el nombre de la textura seguido de un hash de su origen. El hash abarca toda la cadena, el propio mapa y cada plantilla por encima de él, de modo que editar una plantilla cambia la marca de todas las variantes que heredan de ella y todas se redibujan. Cuando se redibuja un mapa, sus archivos anteriores se eliminan.

La carpeta también se revisa cada vez que se escanean los packs, y cualquier imagen cuyo mapa ya no proporcione ningún pack se elimina, junto con cualquier carpeta que quede vacía. Renombra una textura, quita un pack, borra un mapa, y su imagen en caché se va con él en lugar de quedarse ahí para siempre. Borrar la carpeta entera no cuesta más que el tiempo de volver a dibujarlas, y se omite cuando se escanean los packs, de modo que nunca se confunde con un pack.

Un PNG siempre gana. Si existen tanto `panel.png` como `panel.png.json`, se sirve el PNG y el mapa nunca se dibuja, de modo que una textura generada puede sustituirse más adelante por una pintada sin cambiar nada de lo que apunta a ella.

**Nadie tiene que escribir estos archivos a mano.** El repositorio incluye scripts para todo el recorrido de ida y vuelta en `pixelmap/`: `png_to_pixelmap.py` convierte un PNG en un mapa, `convert_pack.py` lo hace con todas las texturas que contiene un pack, y `verify_pack.py` dibuja los mapas de un pack convertido y los compara con los PNG de los que salieron, de modo que se pueda confiar en una conversión antes de apartar los originales.

### Trampas que conviene conocer

*modelos, blockstates y texturas*

**Un modelo que nombra un modelo de vanilla ya terminado hereda también las texturas de vanilla.** `torch`, `ladder`, `oak_door_bottom_left` y `wheat_stage0` llevan todos sus propias texturas, de modo que un modelo que apunta a uno de ellos obtiene el aspecto de vanilla pongas lo que pongas a su lado. Los modelos padre como `cube_all`, `cross` y `crop` toman sus texturas del modelo que los nombra y se comportan bien, y lo mismo ocurre con las plantillas de puerta, trampilla y puerta de valla.

**Los nombres salen del archivo de idioma.** Un bloque o un ítem muestra una clave en bruto hasta que `lang/en_us.json` le da un nombre, y las claves son las del propio juego: `block.mypack.ruby_ore` para un bloque y el ítem que lo coloca, `item.mypack.ruby` para un ítem, `itemGroup.mypack.tab` para una pestaña creativa, `fluid_type.mypack.molten_ruby` y `fluid.mypack.molten_ruby` para un fluido, `effect.mypack.ruby_sight` para un efecto de poción, `entity.mypack.angry_cow` para una variante de entidad, `biome.mypack.ruby_forest` para un bioma. Un nombre para cada uno; nada en esta versión quiere una clave escrita dos veces.

## Hacer que vanilla trate bien tu bloque

*bloques e ítems*

Vanilla comprueba por identidad cuáles son sus propios bloques en una docena de sitios, así que un bloque de un pack que obviamente debería funcionar a menudo no lo hace. Dos claves lo resuelven.

```json
{
  "material": "ground",
  "plantTypes": ["plains", "crop"],
  "behavesAs": ["till", "path"],
  "variants": { "ruby_grass": { "hardness": 0.6 } }
}
```

**`plantTypes`** enumera los tipos de planta que admite tu bloque, de modo que se puedan plantar en él brotes, cultivos y flores: `plains`, `desert`, `beach`, `cave`, `water`, `nether` y `crop`. **1.21.1** no tiene tipos de planta en absoluto; allí `bush` en `behavesAs` hace lo que hacía `plains`, la lista de suelos de la propia planta decide el resto, y la clave se lee y se ignora.

**`behavesAs`** hace que vanilla trate tu bloque como uno de los suyos:

| Valor | Qué hace |
| --- | --- |
| `till`    | Una azada lo convierte en tierra de cultivo, o en lo que nombre `hoeTillsInto` en la configuración |
| `path`    | Una pala lo convierte en un camino de tierra, o en lo que nombre `shovelPathBecomes` |
| `bush`    | Se pueden plantar en él flores, hierba y brotes, y se mantienen en él, como en la tierra. Equivale a `plains` en `plantTypes` |
| `animals` | Los animales aparecen en él con luz, como lo hacen en la hierba |

## Ítems

*bloques e ítems*

`<namespace>/items/*.json`

Cada clave dentro de `variants` es un ítem, registrado bajo el espacio de nombres del pack, de modo que un archivo con `ruby_apple` y `dried_ruby_apple` registra `mypack:ruby_apple` y `mypack:dried_ruby_apple`; el nombre del propio archivo es solo una agrupación. El modelo de cada uno se genera a partir de `textures/item/<name>.png` a menos que el pack distribuya `models/item/<name>.json`.

Todas las claves a la vez. Un archivo real solo escribe las que necesita. Una clave marcada para un tipo solo la lee ese tipo.

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

### Tipos de ítem

*ítems*

| Tipo | Qué obtienes |
| --- | --- |
| `basic`         | Un ítem normal. Se usa cuando falta `type` |
| `food`          | Se come, con hambre y saturación |
| `drink`         | Se bebe en lugar de comerse, y devuelve un recipiente vacío |
| `tool`          | Pico, hacha, pala, azada o espada a partir de un material |
| `armor`         | Casco, peto, pantalones o botas a partir de un material |
| `seed`          | Planta uno de tus cultivos |
| `potion`        | Aplica tus efectos de poción al usarse |
| `potion_bottle` | Contiene tus tipos de poción y los muestra en una pestaña creativa |
| `container`     | Una bolsa: un inventario que se lleva en la mano o puesto, véase [Contenedores](#contenedores) |

Un `potion_bottle` enumera lo que puede contener con `potionTypes`, una lista de nombres de tipos de poción como `["mypack:ruby_tonic"]`. Uno con una lista vacía no registra nada, y el registro lo indica.

### Claves de archivo de ítems

*ítems*

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `variants`     | sí          | objeto de nombre de variante a variante | | Un ítem por entrada. La clave es su nombre de registro, y da nombre a su modelo, su textura y su clave de idioma |
| `type`         | no          | uno de los tipos anteriores         | `basic`                | Qué tipo toma el ítem |
| `creativeTab`  | no          | nombre de pestaña                   | ninguna                | La pestaña en la que aparece, véase [Pestañas creativas](#pestañas-creativas) |
| `material`     | tool, armor | nombre de material                  | ninguno                | De cuál de tus materiales está hecho |
| `toolClass`    | tool        | `pickaxe`, `axe`, `shovel`, `sword` | ninguna                | Qué herramienta es. Una `sword` es una herramienta de 1.12.2 y no una espada de vanilla: golpea con 3 más el `damage` del material, pica los bloques cuyo `harvestTool` es `sword`, admite encantamientos de minería y ni barre ni corta telarañas |
| `slot`         | armor       | `head`, `chest`, `legs`, `feet`     | ninguno                | Dónde se lleva puesta. `helmet`, `chestplate`, `leggings` y `boots` también sirven |
| `eat`          | food        | boolean                             | `false`                | Usa la animación de comer |
| `alwaysEdible` | food        | boolean                             | `false`                | Se puede comer con la barra de hambre llena |
| `useDuration`  | no          | int, ticks                          | `32`                   | Cuánto tarda en usarse |
| `attackSpeed`  | no          | float                               | el adecuado a la clase de herramienta | Para `tool`, el atributo de velocidad de ataque, como el `-2.4` de una espada |
| `cooldown`     | no          | int, ticks                          | `0`                    | Para `food`, `drink` y `potion`, cuánto tiempo rechaza el ítem volver a usarse tras consumirse |
| `container`    | drink       | nombre de ítem                      | ninguno                | Lo que queda atrás, como una botella. En un ítem `container` esta clave son en cambio los ajustes propios de la bolsa, véase [Contenedores](#contenedores) |
| `crop`         | seed        | nombre de bloque                    | ninguno                | El cultivo que planta |
| `soil`         | seed        | nombre de bloque                    | `minecraft:farmland`   | Sobre qué se puede plantar |
| `requires`     | no          | lista de ids de mods o espacios de nombres de packs | ninguno | El archivo se omite a menos que estén todos presentes |

### Claves de variantes de ítems

*ítems*

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `maxSize`    | no          | 1 a 64                               | `64`     | Tamaño de la pila |
| `rarity`     | no          | `common`, `uncommon`, `rare`, `epic` | `common` | Color del nombre en la descripción emergente |
| `healAmount` | food        | int, medios muslos                   | `0`      | Hambre que restaura |
| `saturation` | food        | float                                | `0.0`    | Saturación que restaura |
| `tags`       | no          | lista de ids de etiquetas            | ninguno  | Etiquetas de ítem en las que se escribe esta variante; los archivos de etiquetas se generan por ti |
| `potion`     | food, drink | `potion,duration,amplifier`          | ninguno  | Un efecto que se aplica al comer o beber la variante. Una cuarta parte, `true`, lo hace ambiental. Un efecto beneficioso se nombra en verde en la descripción emergente, seguido del amplificador en números romanos cuando es mayor que 0, y ningún efecto que dé muestra partículas |

## Fluidos

*bloques e ítems*

`<namespace>/fluids/*.json`

La ruta del archivo es el nombre de registro del bloque del fluido. `name` da nombre al propio fluido, a su cubo y a sus claves de idioma, y es la ruta del archivo a menos que el archivo lo establezca.

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

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `name`        | no       | string                             | el nombre del archivo | El nombre de registro del fluido y de su cubo (`<name>_bucket`); el bloque conserva la ruta del archivo |
| `still`       | no       | ruta de textura                    | agua quieta de vanilla | Textura del fluido quieto |
| `flow`        | no       | ruta de textura                    | agua en movimiento de vanilla | Textura del fluido en movimiento |
| `color`       | no       | color hexadecimal                  | ninguno               | Tinte aplicado a esas texturas. En las texturas de agua por defecto se multiplica por el azul del agua de 1.12.2, de modo que un color elegido para 1.12.2 se ve igual aquí |
| `bucket`      | no       | boolean                            | `true`                | Registra un cubo para él |
| `luminosity`  | no       | 0 a 15                             | `0`                   | Luz que emite |
| `density`     | no       | int                                | `1000`                | Negativa flota hacia arriba, como un gas |
| `temperature` | no       | int, kelvin                        | `300`                 | El agua es 300, la lava 1300 |
| `viscosity`   | no       | int                                | `1000`                | Con qué lentitud fluye: el fluido avanza una vez cada viscosity / 200 ticks. El agua es 1000, la lava 6000 |
| `gaseous`     | no       | boolean                            | `false`               | Se trata como un gas |
| `creativeTab` | no       | nombre de pestaña                  | ninguna               | La pestaña en la que aparece el cubo |
| `block`       | no       | objeto                             |                       | El bloque del fluido. `material` (`water`): `water` permite nadar y ahogarse en él, hace flotar los botes, apaga a las criaturas en llamas, mantiene húmeda la tierra de cultivo y se evapora al verterse en el Nether; `lava` prende fuego a lo que se encuentre en ella, no permite nadar ni ahogarse en ella y usa los sonidos del cubo de lava; cualquier otro material no hace nada de esto. `flammability` (`0`) y `fireSpread` (`0`): con qué facilidad lo consume el fuego y se propaga desde él. `quantaPerBlock` (`0`, leído como 8): hasta dónde corre desde una fuente, un bloque menos que el número como en 1.12.2; los fluidos aquí llegan a 1, 2, 3 o 7 bloques, de modo que 4 y 5 corren 3 bloques y 6 o más corren 7. `potions` (ninguno, una lista de efectos que se dan a lo que se encuentre en él, cada uno escrito `potion,duration,amplifier` con una cuarta parte opcional `true` para uno ambiental) |
| `requires`    | no       | lista de ids de mods o espacios de nombres de packs | ninguno | El archivo se omite a menos que estén todos presentes |

## Materiales, pestañas, sonidos, etiquetas

*bloques e ítems*

`<namespace>/materials/*.json`

La ruta del archivo es el nombre del material, que un ítem de herramienta o armadura nombra después en `material`.

En 26.x, solo un material llamado `minecraft:leather`, `minecraft:chainmail`, `minecraft:iron`, `minecraft:gold`, `minecraft:diamond`, `minecraft:turtle`, `minecraft:netherite` o `minecraft:armadillo` toma prestados los valores de un material de armadura existente; cualquier otro nombre, incluido uno que coincida con el material propio de otro mod, construye en cambio un material personalizado a partir de este archivo.

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

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `harvestLevel`   | no       | 0 a 4             | `1`                     | Nivel de la herramienta. 0 madera, 1 piedra, 2 hierro, 3 diamante, 4 netherita |
| `durability`     | no       | int               | `250`                   | Usos antes de romperse |
| `efficiency`     | no       | float             | `6.0`                   | Velocidad de minado. El diamante es 8 |
| `damage`         | no       | float             | `2.0`                   | Bonificación de daño de ataque |
| `enchantability` | no       | int               | `14`                    | Qué tan buenos son los encantamientos. El oro es 22 |
| `repairItem`     | no       | nombre de ítem    | ninguno                 | Lo que repara en un yunque una herramienta hecha con él. La armadura hecha con él no se repara así, como en 1.12.2 |
| `reduction`      | no       | lista de cuatro ints |                      | Puntos de armadura, en el orden botas, pantalones, peto, casco |
| `toughness`      | no       | float             | `0.0`                   | Dureza de armadura, como la tiene el diamante |
| `equipSound`     | no       | nombre de sonido  | `item.armor.equip_iron` | Sonido al ponerse la armadura |
| `armorTexture`   | no       | prefijo de textura | el nombre del archivo  | La textura de la armadura puesta, leída de `textures/models/armor/<name>_layer_1.png` y `_layer_2.png` bajo ese espacio de nombres |

### Pestañas creativas

*materiales, pestañas, sonidos, etiquetas*

`<namespace>/tabs/*.json`

Los bloques, ítems y cubos nombran su pestaña en `creativeTab`. Un id completo, como `mypack:rubypack` o `minecraft:combat`, se usa tal como está escrito. Un nombre suelto se lee como 1.12.2 leía la etiqueta de una pestaña: `buildingBlocks` va a `minecraft:building_blocks`, `decorations` a `minecraft:functional_blocks`, `redstone` a `minecraft:redstone_blocks`, `transportation` y `tools` a `minecraft:tools_and_utilities`, `misc` y `materials` a `minecraft:ingredients`, `food` y `brewing` a `minecraft:food_and_drinks`, y `combat` a `minecraft:combat`, y cualquier otro nombre suelto es la pestaña `<namespace>:<name>` en el espacio de nombres del archivo que la nombra. Una pestaña que ningún archivo declara se crea por ti, con el título tomado de `itemGroup.<namespace>.<name>` y mostrando el primer ítem que contiene. La ruta de un archivo de pestaña es el nombre de la pestaña a menos que `label` la sustituya.

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `label` | no       | string    | el nombre del archivo | El id de la pestaña: los bloques y los ítems la nombran en `creativeTab`, y el nombre mostrado sale de `itemGroup.<namespace>.<label>` en los archivos de idioma |
| `icon`  | no       | nombre de ítem | ninguno          | El ítem mostrado en la pestaña |

### Sonidos

*materiales, pestañas, sonidos, etiquetas*

`<namespace>/sounds/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

El formato `sounds.json` de vanilla, de modo que un pack pueda distribuir su propio audio. Un archivo aquí registra los eventos de sonido; el cliente sigue leyendo el audio a través del `assets/<namespace>/sounds.json` del propio pack, así que distribuye ambos: el índice bajo `assets` y los eventos bajo `data`.

### Etiquetas

*materiales, pestañas, sonidos, etiquetas*

`<namespace>/tags/<kind>/*.json`

Las etiquetas tienen el formato del propio juego en la carpeta del propio juego, y un pack las distribuye como lo haría en un data pack: `tags/items/ores/ruby.json` (1.21.1: `tags/item/`) con `{ "values": ["mypack:ruby_ore"] }` pone el mineral en `mypack:ores/ruby`, y un archivo en `data/forge/tags/items/ores/ruby.json` (1.21.1: `data/c/...`) añade a la etiqueta de convención compartida que lee todo mod. Los bloques e ítems propios de un pack nombran las suyas en las `tags` de la variante, y los archivos se escriben por ti; un `harvestTool` y un `harvestToolLevel` escriben del mismo modo las etiquetas `mineable` y `needs_*_tool`.

El diccionario de minerales de 1.12.2 es lo que las etiquetas sustituyeron. Sus nombres se corresponden con las etiquetas de convención: `oreRuby` es `forge:ores/ruby` en 1.20.1 y `c:ores/ruby` en 1.21.1, `ingotCopper` es `ingots/copper`, `gemRuby` es `gems/ruby`, `dustX` es `dusts/x`, `nuggetX` es `nuggets/x`, `blockX` es `storage_blocks/x`, y `logWood`, `plankWood` y `stickWood` son los `minecraft:logs` y `minecraft:planks` del propio juego y el `rods/wooden` de convención. El `"remove": [...]` de un archivo de etiqueta quita entradas sueltas de una etiqueta, y `"replace": true` con un `"values": []` vacío la vacía, de modo que una etiqueta de un pack inferior o de un mod puede recortarse o vaciarse. Para quitar ítems de todas las etiquetas a la vez, y fuera de juego, usa [Bloques e ítems desactivados](#bloques-e-ítems-desactivados).

## Sobrescritura de propiedades

*bloques e ítems*

`<namespace>/overrides/<target>/<name>.json`

La ruta nombra el objetivo: todo lo que sigue a `overrides/` es el espacio de nombres y el nombre del bloque, ítem o tipo de poción que se modifica.

En todos los demás sitios, un pack sustituye un archivo o añade uno. Una sobrescritura no hace ninguna de las dos cosas: cambia las propiedades de un bloque, ítem o tipo de poción que ya existe, de vanilla o de un mod, sin tocar ninguno de sus archivos. La ruta nombra el objetivo, de modo que `overrides/minecraft/stone.json` cambia `minecraft:stone`, y `overrides/tconstruct/<name>.json` cambia del mismo modo el bloque de ese mod.

Todas las claves a la vez. Un archivo real solo escribe las que necesita.

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

Todas las claves son opcionales y un archivo solo cambia lo que nombra, de modo que un archivo en `overrides/minecraft/stone.json` con solo `hardness`, `light` y `soundType` hace que la piedra se pique casi al instante, brille y suene como el cristal. Un archivo lleva juntas las claves de bloque, ítem y poción. Estas se aplican cuando el objetivo es un bloque:

| Clave | Valor | Qué hace |
| --- | --- | --- |
| `hardness`     | float                                        | Tiempo de minado, la misma cifra que acepta una definición de bloque. Sin `resistance`, también sube la resistencia a las explosiones al menos a esa misma cifra, como hace 1.12.2 |
| `resistance`   | float                                        | Resistencia a las explosiones como la lee 1.12.2: el bloque conserva tres quintos de la cifra, de modo que `10` da el `6` de la piedra |
| `slipperiness` | float                                        | `0.6` es suelo normal, `0.98` es hielo |
| `light`        | `0` a `15`                                   | Luz que emite |
| `lightOpacity` | `0` a `15`                                   | Cuánta luz detiene el bloque |
| `soundType`    | uno de los tipos de sonido                   | Sonidos de pisada, colocación y rotura |
| `harvestTool`  | `pickaxe`, `axe`, `shovel`, `hoe` o `sword` | Qué lo pica rápido, escrito en las etiquetas de herramientas; `harvestToolLevel`, `0` por defecto, fija el nivel: 1 piedra, 2 hierro, 3 diamante, 4 y superiores netherita. Si el bloque suelta ítems sin la herramienta adecuada se queda como lo tenga el bloque |
| `flammability` | int                                          | Con qué facilidad se consume al arder; `fireSpread`, `5` por defecto, con qué facilidad le llega el fuego |

### Propiedades de ítems

*sobrescritura de propiedades*

| Clave | Valor | Qué hace |
| --- | --- | --- |
| `maxStackSize`  | `1` a `64`  | Tamaño de la pila |
| `maxDamage`     | int         | Durabilidad |
| `containerItem` | nombre de ítem | Se queda en la cuadrícula de fabricación, como ocurre con un cubo |
| `food`          | objeto      | Hace el ítem comestible, véase más abajo |

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

En `overrides/minecraft/oak_planks.json` eso hace que los tablones se rompan casi tan rápido como la tierra y que se puedan comer. `food` acepta `heal` (`1`), `saturation` (`0.6`), `alwaysEdible` (`false`; `true` permite comer con la barra de hambre llena) y `effects`, cuyas entradas se escriben exactamente como las de un tipo de poción. Un ítem que ya es comida acepta nuevos `heal`, `saturation` y `alwaysEdible`; `effects` en uno de esos no está soportado, y el registro lo indica. Cuando el ítem comestible coloca un bloque, apunta al cielo para comer, ya que apuntar a un bloque lo coloca: ese es el orden de uso de vanilla, no un fallo.

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

En `overrides/minecraft/swiftness.json`, la poción de velocidad ahora concede levitación. Cada entrada acepta `potion` (obligatoria), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) y `showParticles` (`true`), igual que en `potion_types/`, y la lista no puede estar vacía.

### Otros mods, recargas y límites

*sobrescritura de propiedades*

Un objetivo que pertenece a otro mod debería llevar ese mod en `requires`, de modo que el archivo se omita en silencio cuando el mod no está instalado en lugar de notificarse como un objetivo ausente:

```json
{
  "requires": ["tconstruct"],
  "hardness": 1.0
}
```

Las sobrescrituras son en vivo. Los valores originales se recuerdan antes del primer cambio, de modo que al desactivar el pack y ejecutar `/rdplserver reload` todo vuelve a lo que era, sin reiniciar; lo mismo ocurre en cada entrada a un mundo. Un archivo por objetivo: cuando dos packs sobrescriben lo mismo, el archivo del pack posterior sustituye por completo al anterior, y el registro lo indica.

Dos límites que conviene conocer. Un bloque o ítem cuyo propio código calcula una propiedad ignora el campo que hay detrás, de modo que la sobrescritura se aplica pero no cambia nada; vanilla solo hace esto con la resistencia a las explosiones de las escaleras, pero los mods son libres de hacerlo en cualquier sitio. Y los ítems hechos comestibles solo funcionan con ítems sin comportamiento propio al hacer clic derecho: un ítem que ya hace algo al usarse sigue haciéndolo.

Las sobrescrituras necesitan el pack tanto en el cliente como en el servidor, ya que la velocidad de minado, la luz y el comer ocurren todos en la pantalla del jugador, así que no son para packs del lado del servidor. `overrides` en la categoría de configuración `content` desactiva la carpeta por completo.

## Grupos de dureza

*bloques e ítems*

`<namespace>/hardness/*.json`

La ruta del archivo nombra el grupo en el registro y nada más la lee, de modo que varios archivos se acumulan.

Da a un grupo de bloques un multiplicador del tiempo de minado, sorteado por posición de bloque. El bloque en sí nunca cambia: no se registra nada, no se escribe nada en el mundo, y un mundo abierto sin el pack es vanilla normal.

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

### Minería y voladuras

*grupos de dureza*

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `blocks`          | sí       | lista de nombres de bloque u objetos   |                    | El grupo. Las mismas formas que un `replace` de worldgen |
| `except`          | no       | lista de nombres de bloque u objetos   | ninguno            | Se saca del grupo, diga lo que diga `blocks` |
| `miningTime`      | no       | número, u objeto con `min` y `max`     | `1.0`              | Cuántas veces más tarda en romperse el bloque, tanto para un jugador como para un mob con `digs` |
| `blastResistance` | no       | número, u objeto con `min` y `max`     | `1.0`              | Multiplica la resistencia a las explosiones del bloque |
| `buckets`         | no       | 1 a 256                                | `10`               | En cuántos pasos se divide el rango |
| `minHeight`       | no       | int                                    | el fondo del mundo | Por debajo de esto el sorteo da el paso más duro |
| `maxHeight`       | no       | int                                    | la cima del mundo  | Por encima de esto el sorteo da el paso más duro |
| `field`           | no       | objeto                                 | véase más abajo    | La forma en la que se agrupa el sorteo |
| `requires`        | no       | lista de ids de mods o espacios de nombres de packs | ninguno | El archivo se omite a menos que estén todos presentes |

Un solo número da a todos los bloques del grupo el mismo multiplicador, y no se sortea nada. Un `min` y un `max` sortean por posición: `max` donde el campo está vacío, `min` en el centro de un grupo, y los pasos intermedios los decide `buckets`.

### Minería de aventura y desbloqueos

*grupos de dureza*

| Clave | Obligatoria | Valor | Por defecto | Qué hace |
| --- | --- | --- | --- | --- |
| `keeps`       | no       | boolean          | `false` | El bloque se queda donde está cuando se pica: los drops, la experiencia, el desgaste de la herramienta y el sonido de rotura ocurren y el bloque sigue ahí para volver a picarlo, de modo que el grupo es una veta interminable al ritmo que fije `miningTime`. El modo creativo lo elimina como siempre |
| `adventure`   | no       | objeto           | ninguno | Quién puede romper el grupo en modo aventura, donde de otro modo no se rompe nada. `tools` enumera los ítems de los cuales uno debe estar en la mano, vacío para cualquier cosa que se sostenga; `teams`, `players` y `entities` dicen quién, un equipo por su nombre, un jugador por su nombre, un mob por su id de entidad para la tarea `digs`, y los tres vacíos significan cualquiera con la herramienta. Supervivencia y creativo no se tocan |
| `advancement` | no       | `namespace:path` | ninguno | El grupo cuenta para un jugador solo una vez que tiene ese logro. Dos grupos pueden nombrar el mismo bloque, uno con un logro y otro sin él, y gana el desbloqueado; un jugador sin él recibe el grupo normal, o vanilla si no hay ninguno. Los mobs no tienen logros, de modo que un grupo restringido nunca llega a una tarea `digs`, y la resistencia a las explosiones y el sorteo de textura, que no pertenecen a ningún jugador, salen del grupo normal |
| `becomes`     | no       | objeto           | ninguno | Los bloques del grupo se convierten en otro bloque, en todo el mundo, en el momento en que cualquier jugador consigue `advancement`: `{ "advancement": "mypack:deep_miner", "block": "mypack:rich_ore" }`. Se barren de golpe todos los chunks cargados, y un chunk cargado o creado más tarde se barre al entrar, de modo que el bloque antiguo desaparece para siempre. Dale al nuevo bloque un grupo propio para cambiar cómo se pica |

### El campo

*grupos de dureza*

La tirada no se hace para cada bloque de forma totalmente independiente, o lo duro y lo blando serían puro ruido sin forma alguna. `field` decide qué forma adopta, y `type` elige entre dos maneras de conseguirla.

```json
{
  "field": { "type": "speckle" }
}
```

| Clave  | Obligatorio | Valor                 | Predeterminado | Qué hace                         |
| ------ | ----------- | --------------------- | -------------- | -------------------------------- |
| `type` | no          | `speckle` o `seeded`  | `speckle`      | Cuál de los dos siguientes se usa |

#### speckle

*el campo*

Cada bloque sortea su propio escalón, y un bloque contiguo por una cara puede transmitirle un escalón más débil. El resultado son motas densas y de grano fino, la mayoría de un solo bloque, con alguna mancha mayor allí donde se juntan. De los dos, es el que más se acerca a la sensación de minar en el mod del que se toma la idea.

```json
{
  "field": {
    "type": "speckle",
    "chances": [30, 30, 20, 20, 10, 10, 10, 10, 50],
    "spread": 0.15
  }
}
```

| Clave     | Obligatorio | Valor                      | Predeterminado                         | Qué hace                                                                                          |
| --------- | ----------- | -------------------------- | -------------------------------------- | ------------------------------------------------------------------------------------------------- |
| `chances` | no          | lista de enteros, por mil  | `[30, 30, 20, 20, 10, 10, 10, 10, 50]` | Con qué frecuencia un bloque empieza en cada escalón, el más blando al final. Lo que sobra es el escalón más duro |
| `spread`  | no          | 0.0 a 1.0                  | `0.15`                                 | Con qué frecuencia un escalón pasa al bloque contiguo, un escalón más débil o tres                |

La lista se lee con el más blando al final, de modo que la última entrada es el escalón más blando y la primera es una por encima del más duro. Con los números anteriores, unos siete bloques de cada diez son del escalón más duro y el resto se reparte entre ellos.

#### seeded

*el campo*

Las semillas se sitúan en una retícula calculada a partir del mundo y la posición, y el escalón de un bloque depende de lo cerca que esté de la más próxima. El resultado son manchas menos numerosas, mayores y más redondeadas que se funden entre sí, y pueden echar brazos.

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

| Clave      | Obligatorio | Valor            | Predeterminado | Qué hace                                  |
| ---------- | ----------- | ---------------- | -------------- | ----------------------------------------- |
| `cell`     | no          | entero, bloques  | `8`            | A qué distancia están las semillas        |
| `seeds`    | no          | 1 a 4            | `1`            | Semillas en cada celda                    |
| `reach`    | no          | float, bloques   | `3.0`          | Hasta dónde llega la influencia de una semilla |
| `arms`     | no          | 0 a 6            | `0`            | Brazos que irradian de cada semilla       |
| `armReach` | no          | float, bloques   | `0.0`          | Hasta dónde llegan los brazos             |

Si se omite `arms`, las manchas son redondas. Dar brazos a una semilla la convierte en un nudo con zarcillos, y los brazos de nudos vecinos se alcanzan entre sí, lo que forma una veta en lugar de una mancha. Mantén `reach` por encima de la mitad de `cell` o las manchas no podrán tocarse y obtendrás bolas sueltas sin nada entre ellas.

### Mostrarlo

*grupos de dureza*

El multiplicador es invisible por sí solo. Para que el jugador vea qué bloques son duros, dale al bloque un blockstate con una variante por cada cubo, todas con el mismo peso, listadas empezando por la más dura:

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

Minecraft ya elige una variante según la posición del bloque, y un grupo de dureza le entrega el cubo en su lugar, de modo que la textura y el multiplicador siempre coinciden. El pack de ejemplo hace exactamente esto con su piedra de rubí.

Tres cosas tienen que estar bien, y ninguna avisa cuando falla.

**Exactamente `buckets` entradas, todas con el mismo peso.** El cubo se usa como posición en la lista, así que una lista de otra longitud, o una cuyos pesos difieran, apunta en silencio a la textura equivocada.

**Un nombre de modelo con `block/` delante.** En esta versión un blockstate nombra el archivo del modelo completo, así que `"model": "mypack:block/stone_step0"` lee `models/block/stone_step0.json`; un `mypack:stone_step0` a secas busca `models/stone_step0.json`, que no existe, y la entrada se descarta sin decir nada.

**La misma clave que pide el juego.** Un bloque con un solo estado se identifica con `""`, y la piedra de vanilla es uno de ellos. Un bloque con propiedades se identifica con todas ellas, de modo que una sobrescritura para un tronco necesita `axis=x`, `axis=y` y `axis=z`, cada uno con su propia lista.

Activa `worldgenDebug` y cada grupo de dureza se comprueba contra su modelo ya compilado al entrar en un mundo, indicando el blockstate, cuántas variantes sobrevivieron, qué textura acabó teniendo cada una y qué packs fusionó el juego para llegar ahí. Es la forma más rápida de encontrar cualquiera de las tres anteriores, y además avisa cuando sobrescribir un blockstate compartido ha cambiado un estado que el grupo nunca nombró.

### Lo que no alcanza

*grupos de dureza*

Solo cambia la minería del propio jugador. Las máquinas que rompen bloques leen la dureza del bloque directamente y no se ven afectadas. Los bloques que coloca un jugador se sortean igual que cualquier otro, ya que la tirada pertenece al lugar y no al bloque, y un bloque transportado a otro sitio adopta lo que diga su nuevo lugar.

---

# Fabricación, botín y comercio

## Bloques e ítems desactivados

*fabricación, botín y comercio*

`<namespace>/disabled/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

Saca bloques e ítems del juego sin quitarles el registro, de modo que los mundos conservan sus ids y borrar el archivo lo devuelve todo. El contenido de vanilla, de mods y de packs se trata por igual, incluidos los bloques e ítems del propio pack, y un bloque desactivado desactiva su ítem igual que un ítem desactivado desactiva su bloque.

```json
{
  "requires": ["thermal"],
  "names": ["thermal:tin_ore", "thermal:deepslate_tin_ore", "mekanism:salt*"],
  "namespaces": ["bigreactors"],
  "tags": ["forge:ores/tin"]
}
```

| Clave        | Obligatorio | Valor                              | Predeterminado | Qué hace                                                                                                                                                              |
| ------------ | ----------- | ---------------------------------- | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `names`      | no          | lista de nombres de bloques e ítems | ninguno        | Qué se desactiva. Un nombre que termina en `*` coincide con todos los nombres que empiecen por el resto                                                               |
| `namespaces` | no          | lista de ids de mods               | ninguno        | Todos los bloques e ítems del mod                                                                                                                                     |
| `tags`       | no          | lista de nombres de etiquetas      | ninguno        | Todos los ítems de la etiqueta de ítems y todos los bloques de la etiqueta de bloques con ese nombre, y ambas etiquetas quedan vacías. Se admite un `#` inicial        |
| `requires`   | no          | lista de ids de mods               | ninguno        | El archivo se omite salvo que estén cargados todos. Las entradas `config:` y `file:` funcionan como en el resto de sitios                                              |

Un bloque o ítem desactivado:

- desaparece de todas las pestañas creativas y de la pestaña de búsqueda, y queda oculto en JEI
- no tiene ninguna receta que lo fabrique o lo use: se van todas las recetas de cualquier tipo que lo tengan como resultado, ya sean de fabricación, cocción, cortapiedras o herrería, y también toda receta con una ranura que solo él pueda ocupar. Una ranura que admite además otra cosa conserva su receta, y una ranura de etiqueta simplemente la pierde junto con la etiqueta
- se retira de todas las etiquetas de ítems y de bloques, y figura en su lugar en `resourcedatapackloader:disabled`
- se elimina de toda tirada de botín, ya sea de cofres, mobs o pesca, de los drops de bloques y de los comercios de aldeanos y del comerciante errante, y un stack suyo que se suelte se desvanece
- no se puede colocar, usar, blandir ni recoger, y el stack que se lleva en la mano se elimina cuando un jugador lo intenta
- se elimina allí donde aparezca un stack suyo: del inventario y del cofre de Ender de un jugador al iniciar sesión y cada segundo después, de cualquier contenedor cuando un jugador lo abre, y de cofres y otros inventarios cuando se carga su chunk
- se retira del mundo allí donde esté colocado: cada bloque suyo se convierte en aire, con su entidad de bloque, cuando se carga su chunk

Para cambiar los bloques colocados por otra cosa en lugar de eliminarlos, dales una línea `blockReplacements` como `thermal:tin_ore=minecraft:stone` en la plantilla de mundo, consulta [Sustituciones](#sustituciones). Un bloque que el proceso de sustitución cambia queda a cargo de este. Las recetas que otro mod guarda dentro de sus propias máquinas pertenecen a ese mod y no se alcanzan. Ocultar de las pestañas creativas y de JEI ocurre en el cliente, así que un cliente vanilla sigue mostrando el ítem. `content.disabled` en la configuración desactiva la carpeta.

Para vaciar una etiqueta mientras sus ítems siguen en juego, usa en su lugar un archivo de etiqueta con `"replace": true` y un `"values": []` vacío, consulta [Etiquetas](#etiquetas).

## Recetas de horno y combustibles

*fabricación, botín y comercio*

`<namespace>/furnace/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

Añade y elimina recetas de horno. Una eliminación quita también las recetas correspondientes del alto horno, el ahumador y la hoguera, ya que 1.12.2 guardaba todas las recetas de cocción en una única lista del horno; una adición es solo una receta de horno.

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

| Clave        | Obligatorio | Valor           | Predeterminado | Qué hace                                                                                                                                                                  |
| ------------ | ----------- | --------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `input`      | sí          | nombre de ítem  | ninguno        | Lo que entra                                                                                                                                                              |
| `output`     | sí          | nombre de ítem  | ninguno        | Lo que sale                                                                                                                                                               |
| `count`      | no          | entero          | `1`            | Cuántos salen                                                                                                                                                             |
| `experience` | no          | número          | `0.0`          | Experiencia por ítem fundido, como máximo uno: 1.0 o más da un punto por cada ítem retirado, así que un `count` de 2 da dos. El mineral de hierro da 0.7                    |

Una adición cuyo input ya funde otra cosa se ignora y el registro nombra la receta que lo impide, como hace 1.12.2; elimina esa receta en el mismo archivo o en uno anterior para sustituirla.

Las entradas de `remove` son o bien un nombre de ítem a secas, que elimina todas las recetas que lo producen, o bien un objeto que nombra `input`, `result` o ambos para acotar. Una eliminación que no nombra ninguno de los dos se omite y el registro lo indica.

Los archivos se aplican en orden de carga, con las eliminaciones de cada archivo antes que sus adiciones. Por tanto, una eliminación en un archivo posterior también quita una adición hecha por uno anterior, pero nunca alcanza una adición que venga después. Una adición cuenta como receta de horno del mod al que pertenece su output, así que `blockFurnaceRecipes` y `blockedFurnaceMods` la bloquean como a cualquier otra.

`<namespace>/fuels/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

```json
{
  "fuels": [
    { "item": "mypack:ruby_coal", "burnTime": 2400 },
    { "tag": "forge:gems/ruby", "burnTime": 800 }
  ]
}
```

| Clave      | Obligatorio      | Valor           | Predeterminado | Qué hace                              |
| ---------- | ---------------- | --------------- | -------------- | ------------------------------------- |
| `item`     | uno de los dos   | nombre de ítem  | ninguno        | El ítem que arde                      |
| `tag`      | uno de los dos   | id de etiqueta  | ninguno        | Todo lo que hay en esa etiqueta arde  |
| `burnTime` | sí               | entero, ticks   | `0`            | El carbón son 1600, una tabla 300     |

## Pociones, tipos de poción y elaboración

*fabricación, botín y comercio*

`<namespace>/potions/*.json`

La ruta del archivo es el nombre de registro del efecto, así que `mypack/potions/ruby_sight.json` registra `mypack:ruby_sight`, que luego nombra un tipo de poción.

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

| Clave           | Obligatorio | Valor                 | Predeterminado                                    | Qué hace                                                                                                                                      |
| --------------- | ----------- | --------------------- | ------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`          | no          | clave de traducción   | `effect.<namespace>.<name>`                       | Lo que ve el jugador                                                                                                                          |
| `color`         | no          | color hexadecimal     | `FFFFFF`                                          | Color de las partículas                                                                                                                       |
| `badEffect`     | no          | booleano              | `false`                                           | Cuenta como dañino, así que un ojo de araña fermentado lo invierte                                                                            |
| `beneficial`    | no          | booleano              | `false`                                           | Se muestra como un efecto bueno                                                                                                               |
| `instant`       | no          | booleano              | `false`                                           | Se aplica una sola vez en lugar de con el tiempo                                                                                              |
| `effectiveness` | no          | float                 | `0.5`                                             | Se lee para que se cargue un archivo de 1.12.2; ni 1.12.2 ni esta versión actúan según él                                                    |
| `attributes`    | no          | lista de objetos      | ninguno                                           | `attribute` (el id del juego, como `minecraft:generic.movement_speed`), `uuid`, `amount` (`0.0`), `operation` (`0`)                           |
| `icon`          | no          | objeto                | ninguno                                           | `x` e `y`, la columna y la fila del icono en la hoja de estados de 1.12.2, cada una `0` si se omite. Solo se lee sin `iconTexture`            |
| `iconTexture`   | no          | ruta de textura       | el icono de RDPL, o ninguno si se indica `icon`   | Una imagen que aporta un pack, como `mypack:textures/effect/rage.png`, dibujada entera como icono                                             |

El icono del efecto es la textura `assets/<namespace>/textures/mob_effect/<name>.png`, de 18 por 18 como las del propio juego, y un pack que la aporte ahí siempre gana. En caso contrario, `iconTexture` nombra una imagen que aporta un pack, que se copia allí entera; `icon` por sí solo elige el icono de un efecto de vanilla según su posición en la hoja de estados de 1.12.2, `x` en horizontal e `y` en vertical a partir de 0, como `{ "x": 2, "y": 1 }` para el salto mejorado; y un efecto que no nombra ninguno muestra el icono de RDPL.

### Tipos de poción

*pociones, tipos de poción y elaboración*

`<namespace>/potion_types/*.json`

La ruta del archivo es el nombre de registro del tipo de poción, que luego nombra un ítem `potion_bottle` en `potionTypes`.

```json
{
  "baseName": "ruby_sight",
  "effects": [
    { "potion": "mypack:ruby_sight", "duration": 3600, "amplifier": 0, "ambient": false, "showParticles": true }
  ]
}
```

| Clave      | Obligatorio | Valor            | Predeterminado                  | Qué hace                                                                                                                                                                                                                                                                                                                                               |
| ---------- | ----------- | ---------------- | ------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `baseName` | no          | cadena           | el espacio de nombres y el nombre | Nombra la poción: la clave de idioma `item.minecraft.potion.effect.<baseName>`, con `splash_potion`, `lingering_potion` o `tipped_arrow` en lugar de `potion` para las otras formas. Una `potion_bottle` que la contenga muestra el mismo nombre, y las claves `potion.effect.<baseName>` de un pack de 1.12.2 se convierten                              |
| `effects`  | sí          | lista de objetos |                                 | Véase más abajo                                                                                                                                                                                                                                                                                                                                        |

Cada efecto admite `potion` (obligatorio), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) y `showParticles` (`true`).

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

Cada entrada es o bien `input`, `ingredient` y `output`, que elabora un ítem a partir de otro, o bien `from`, `ingredient` y `to`, que convierte un tipo de poción en otro. `ingredient` es obligatorio en ambos casos, y una entrada admite además `requires`, de modo que se pueda omitir una receta sin omitir el archivo.

## Trabajo en el yunque

*fabricación, botín y comercio*

`<namespace>/anvils/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan. Cada archivo es un trabajo.

Coloca el ítem indicado en la ranura izquierda de un yunque y su ítem `with` en la derecha, y el yunque ofrece de vuelta el de la izquierda con los encantamientos indicados, o su `result`, por los niveles señalados; se gasta uno de cada salvo que un recuento pida más, y el resto de cualquiera de los stacks se queda en el yunque. Sacarlo puede además otorgar un logro, y el ítem puede quedar bloqueado para su uso hasta que se consiga ese logro: una espada que solo se puede blandir una vez trabajada.

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

| Clave          | Obligatorio | Valor                                                       | Predeterminado     | Qué hace                                                                                                                                                                                                                                                                                              |
| -------------- | ----------- | ----------------------------------------------------------- | ------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `item`         | sí          | nombre de ítem, una lista de ellos o `{ "item", "count" }`  |                    | Lo que va en la ranura izquierda, cualquier ítem de una lista, y cuántos de ellos necesita un trabajo, uno por defecto; el resto del stack queda para el siguiente. `{ "item": "minecraft:coal", "count": 8 }` con un `result` de diamante son ocho carbones por un diamante                              |
| `with`         | sí          | nombre de ítem, una lista de ellos o `{ "item", "count" }`  |                    | Lo que va en la ranura derecha, cualquier ítem de una lista, y cuántos se gastan, uno por defecto: `{ "item": "minecraft:coal", "count": 10 }` pide un stack de al menos diez y toma diez. Un yunque nunca responde ante un ítem solitario, así que todo trabajo es una pareja                           |
| `result`       | no          | nombre de ítem, o `{ "item", "count" }`                     | el ítem izquierdo  | Lo que sale en lugar del ítem izquierdo, y cuántos, uno por defecto, conservando las etiquetas del ítem izquierdo, de modo que un pico de hierro irrompible y diez carbones pueden volver como uno de diamante irrompible. Los encantamientos se aplican a lo que salga                                 |
| `levels`       | no          | entero                                                      | `1`                | Los niveles de experiencia que cuesta el trabajo, 1 como mínimo                                                                                                                                                                                                                                       |
| `enchantments` | no          | objeto de nombre de encantamiento a nivel                   | ninguno            | Con lo que vuelve el ítem. Un nivel que ya tiene a esa altura o por encima se deja como está, y si no hay nada que subir el yunque no ofrece nada, salvo que se indique `grants`                                                                                                                       |
| `grants`       | no          | `namespace:path`                                            | ninguno            | Un logro que se obtiene al sacar el trabajo. Inclúyelo en `advancements/` con un criterio `impossible`, para que nada más lo consiga                                                                                                                                                                  |
| `locks`        | no          | booleano                                                    | `false`            | Hasta que el jugador tenga `grants`, el ítem no se puede blandir contra nada, usar ni emplear para excavar; se le informa de qué espera cuando le llega a la mano. Ponerlo en el yunque sigue estando permitido, que es como se desbloquea                                                               |

Las reparaciones y combinaciones propias del yunque no se tocan: esto solo responde cuando la izquierda contiene un ítem indicado y la derecha contiene su `with`.

Un mob con `collectsExperience` también gasta aquí sus niveles. Mientras tenga `item` en la mano principal y `with` en la secundaria y tenga `levels` que pagar, va hasta un yunque, un yunque astillado o un yunque dañado situado a 16 bloques en horizontal y 4 arriba o abajo, y lo trabaja una vez que está a 3 bloques: los niveles se descuentan de los suyos igual que de los de un jugador, `with` se consume, el yunque se desgasta como con un jugador, y el trabajo acaba en su mano principal. Al pasar por encima de un ítem soltado que algún trabajo de yunque nombre en `with`, lo recoge a su mano secundaria. `grants` y `locks` solo afectan a los jugadores, así que un mob no obtiene nada de `grants` y ningún bloqueo lo frena.

## Drops de bloques

*fabricación, botín y comercio*

`<namespace>/block_drops/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

La tabla de botín de un bloque decide qué suelta, pero aportar una se apodera de toda la tabla, y una tabla de botín no puede dar experiencia. Una regla de aquí nombra un bloque y lo que suelta al romperlo además de los drops habituales, o en lugar de ellos, experiencia incluida, y deja en paz la tabla propia del bloque.

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

| Clave         | Obligatorio | Valor                           | Predeterminado | Qué hace                                                                                                                                      |
| ------------- | ----------- | ------------------------------- | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| `block`       | sí          | id de bloque                    |                | El bloque que vigila la regla                                                                                                                 |
| `properties`  | no          | objeto de propiedad a valor     | ninguno        | Solo los estados con estos valores, como `{ "axis": "x" }` en un tronco; sin ello, todos los estados. Un `meta` de 1.12.2 no se lee           |
| `replace`     | no          | booleano                        | `false`        | Si los drops habituales se descartan antes de sortear estos                                                                                   |
| `advancement` | no          | `namespace:path`                | ninguno        | La regla cuenta solo para un jugador que tenga ese logro, así que el mismo bloque puede soltar una cosa antes y otra después                  |
| `drops`       | sí          | lista de drops                  |                | Cada uno se sortea por separado cuando se rompe el bloque                                                                                     |

Cada drop:

| Clave        | Obligatorio                | Valor                             | Predeterminado | Qué hace                                                                                                                                                                                                                                                                                                                                       |
| ------------ | -------------------------- | --------------------------------- | -------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `item`       | sí, salvo con `experience` | id de ítem                        |                | Lo que suelta                                                                                                                                                                                                                                                                                                                                  |
| `experience` | no                         | número o `low-high`               |                | En lugar de un ítem, esa cantidad de experiencia en forma de orbes, sorteada de manera uniforme dentro del rango. `chance` y `silkTouch` se aplican igual que con un ítem                                                                                                                                                                       |
| `count`      | no                         | número o `low-high`               | `1`            | Cuántos, sorteados de manera uniforme dentro del rango                                                                                                                                                                                                                                                                                         |
| `chance`     | no                         | float                             | `1.0`          | La probabilidad de que el drop se produzca, siendo `0.05` una rotura de cada veinte                                                                                                                                                                                                                                                            |
| `fortune`    | no                         | entero                            | `0`            | Hasta tantos extra por nivel de Fortuna de la herramienta                                                                                                                                                                                                                                                                                      |
| `silkTouch`  | no                         | `either`, `only` o `never`        | `either`       | Si el drop necesita una recolección con Toque de seda, la rechaza o le da igual. Es una cuando un jugador rompe, con una herramienta con Toque de seda, un bloque que se puede recolectar con toque de seda como lo decidía 1.12.2: un bloque completo sin entidad de bloque, o paneles de cristal, barrotes de hierro, telarañas y cofres de Ender |

Las reglas ven toda rotura que suelta el botín del bloque, como hacía 1.12.2: la de un jugador, y las explosiones, los pistones, el agua que fluye, los mobs y un mob del pack que cave a través del bloque, que sortean toda regla sin `advancement`. Cuando una explosión reduce los drops propios del bloque, cada ítem sorteado sobrevive con las mismas probabilidades; la experiencia no se reduce. Varias reglas para un mismo bloque se aplican todas, y un `replace` en cualquiera de ellas borra primero los drops habituales.

## Botín de jugadores

*fabricación, botín y comercio*

`<namespace>/player_loot/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

El juego no da a los jugadores ninguna tabla de botín propia: al morir solo se sueltan los ítems del inventario, y no existe un nombre de tabla que un pack pueda sobrescribir. RDPL añade una, que se sortea cuando muere un jugador:

```json
{
  "table": "mypack:entities/player",
  "mode": "add",
  "rollOnKeepInventory": false,
  "dropLoose": false
}
```

| Clave                 | Obligatorio | Valor              | Predeterminado | Qué hace                                                                              |
| --------------------- | ----------- | ------------------ | -------------- | ------------------------------------------------------------------------------------- |
| `table`               | sí          | nombre de tabla    |                | La tabla de botín que se sortea cuando muere un jugador                               |
| `mode`                | no          | `add` o `replace`  | `add`          | Si los ítems de la tabla se suman al inventario o lo sustituyen                       |
| `rollOnKeepInventory` | no          | booleano           | `false`        | Si la tabla se sortea siquiera en una muerte que conservó el inventario               |
| `dropLoose`           | no          | booleano           | `false`        | Si los ítems se dejan directamente en el suelo en lugar de sumarse a los drops de la muerte |

`add` suelta los ítems de la tabla junto al inventario, lo que resulta adecuado para recompensas por matar. `replace` descarta el inventario y suelta solo lo que sortee la tabla.

Con `rollOnKeepInventory` desactivado, las muertes bajo `keepInventory` (y las muertes de espectador, que siempre conservan el inventario) no sortean nada. Activarlo mantiene las muertes costosas en los mundos con conservación de inventario.

Varios archivos se acumulan, cada uno evaluado por separado. Si alguna entrada aplicable es `replace`, el inventario se vacía una sola vez antes de sortear, de modo que una entrada `add` junto a ella sigue surtiendo efecto.

La tabla es una tabla de botín corriente que se busca por nombre: puede estar en el pack en `loot_tables/entities/player.json`, ser cualquier tabla de vanilla o de un mod, y ser alcanzada por `loot_injections`. Contexto de botín: el jugador que muere es la entidad que se saquea, el asesino (si lo hay) es el jugador que mata, y la fuente de daño está definida, de modo que `killed_by_player`, `entity_properties`, `random_chance_with_looting` y el resto se comportan con normalidad.

Una función de botín es propia de RDPL y se puede usar en cualquier tabla con una entidad saqueada: `rdpl:killed_name` pone a un ítem soltado el nombre de la víctima. `format` da forma al nombre mostrado (`%s` es la víctima, por defecto solo el nombre), y `tag` en cambio escribe el nombre simple en una clave de cadena NBT para los ítems que lo leen por sí mismos.

```json
{ "item": "mypack:human_skull", "weight": 1,
  "functions": [ { "function": "rdpl:killed_name", "format": "%s's Skull" } ] }
```

**Mods de tumbas.** Los ítems sorteados se suman a los drops de muerte habituales antes de que cualquier mod de tumbas los lea, así que acaban en la tumba con todo lo demás (`replace` pone el contenido de la tabla en la tumba en lugar del inventario). No requiere configuración.

`dropLoose` se salta por completo la lista de drops: los ítems se colocan directamente en el mundo, así que los mods de tumbas nunca los ven; el inventario va a la tumba, y los ítems de la tabla quedan en el suelo para el asesino. Úsalo para botines que pertenecen al asesino y no a la tumba de la víctima. Sin un mod de tumbas cambia poco. Advertencia: los ítems existen antes de que nada posterior pudiera cancelar los drops, así que las entradas que no deban sobrevivir a una muerte cancelada deberían dejarlo desactivado.

Pon `playerLoot` en la categoría de configuración `data` a `false` para desactivar la carpeta por completo.

## Aldeanos y comercio

*fabricación, botín y comercio*

`<namespace>/villagers/*.json`

La ruta del archivo es el nombre de registro de la profesión, así que `mypack/villagers/jeweller.json` registra `mypack:jeweller`, que luego nombra un comercio en `profession`.

```json
{
  "jobSite": "mypack:gem_bench",
  "workSound": "minecraft:entity.villager.work_toolsmith"
}
```

| Clave       | Obligatorio | Valor             | Predeterminado | Qué hace                                                                                                                                                                                                                                                                                                                        |
| ----------- | ----------- | ----------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `jobSite`   | no          | nombre de bloque  | ninguno        | El bloque que un aldeano reclama para asumir esta profesión, como la mesa de herrería hace a un herrero de herramientas. Sin él, ningún bloque otorga la profesión: como en 1.12.2, un aldeano que aparece o se cría la recibe al azar, la conserva y, sin bloque en el que trabajar, nunca repone sus existencias                 |
| `workSound` | no          | nombre de sonido  | ninguno        | Lo que reproduce mientras trabaja en ese bloque                                                                                                                                                                                                                                                                                 |

Las carreras son una idea de 1.12.2 que el juego ya no tiene: una profesión es un único conjunto de comercios, así que un pack que tenía dos carreras aporta dos archivos de aldeano. El aspecto del aldeano es una textura corriente, aportada en `assets/<namespace>/textures/entity/villager/profession/<name>.png` y `textures/entity/zombie_villager/profession/<name>.png`, exactamente donde el juego guarda las suyas. Un comercio que nombra una profesión de vanilla de 1.12.2 junto con su `career`, como `minecraft:smith` con `armor`, pasa a la profesión en que se convirtió esa carrera, aquí `minecraft:armorer`. Los `texture` y `zombieTexture` de un archivo de 1.12.2, una piel completa que aporta un pack, se copian a esas dos rutas cuando el pack no tiene nada allí, y la piel se dibuja sobre la propia del aldeano.

### Comercio

*aldeanos y comercio*

`<namespace>/trades/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

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

| Clave        | Obligatorio | Valor                  | Predeterminado | Qué hace                                                                                                      |
| ------------ | ----------- | ---------------------- | -------------- | ------------------------------------------------------------------------------------------------------------- |
| `profession` | sí          | nombre de profesión    |                | De quién es este comercio. También se lee un nombre de vanilla de 1.12.2 con su `career`, véase más arriba    |
| `level`      | no          | entero                 | `1`            | En qué nivel de comercio aparece, de 1 a 5. Un nivel mayor se suma al nivel 5, el más alto que alcanza un aldeano |
| `maxUses`    | no          | entero                 | `12`           | Veces que se puede usar antes de bloquearse                                                                   |
| `xp`         | no          | entero                 | `2`            | Experiencia que gana el aldeano por comercio hacia su siguiente nivel                                         |

Un stack es `item` con `min` (`1`) y `max` (`min`), de modo que un precio fijo es simplemente `min`.

---

# Criaturas y peligros

## Variantes de entidades

*criaturas y peligros*

`<namespace>/entities/*.json`

La ruta del archivo es el nombre de registro de la variante, así que `mypack/entities/angry_cow.json` registra `mypack:angry_cow`, que es a lo que se refieren `becomes`, un huevo generador y un guardado de mundo.

Un archivo de aquí crea una entidad nueva a partir de otra que ya existe. Es una entidad real por derecho propio, con su propio nombre de registro, su propio nombre en el mundo, su propio huevo generador y una tabla de botín propia si le das una, construida sobre el comportamiento de otra entidad en lugar de sustituirla. Nada de la entidad que copia cambia.

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita.

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

### Identidad

*variantes de entidades*

| Clave           | Obligatorio | Valor                                | Predeterminado | Qué hace                                                                                                                                                                                                                     |
| --------------- | ----------- | ------------------------------------ | -------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `entity`        | sí          | `namespace:name`                     | ninguno        | La entidad sobre la que construir. La de cualquier mod, siempre que admita un constructor de mundo simple                                                                                                                    |
| `name`          | no          | cadena                               | ninguno        | El nombre que lleva en el mundo, en los mensajes de muerte y en su huevo                                                                                                                                                     |
| `showName`      | no          | booleano                             | `false`        | Muestra el nombre sin necesidad de mirarla                                                                                                                                                                                   |
| `profession`    | no          | `namespace:name`                     | al azar        | Para un aldeano, el oficio que ejerce                                                                                                                                                                                        |
| `baby`          | no          | booleano o 0.0 a 1.0                 | `false`        | Con qué frecuencia aparece una cría, y se queda así. `true` es siempre, un número es esa proporción de ellas                                                                                                                 |
| `becomes`       | no          | lista                                | ninguno        | Otras variantes en las que esta puede convertirse al aparecer, según su peso. Véase más abajo                                                                                                                                |
| `egg`           | no          | booleano u objeto                    | `true`         | Un huevo generador, coloreado como el huevo de la entidad que copia. `{ "primary": "AABBCC", "secondary": "112233" }` elige tus propios colores, `false` omite el huevo. En 26.x un huevo sin colores del pack conserva su propia textura; uno con colores del pack se dibuja en cambio mediante una textura de plantilla                                                       |
| `keepsBaseBaby` | no          | booleano                             | `false`        | Si también se ejecuta la tirada de cría de la base. Sin ella, una variante basada en el zombi solo genera crías como indica `baby`, sin hijo de la tirada propia del zombi y sin jinete de gallina                            |
| `requires`      | no          | lista de ids de mods o espacios de nombres de packs | ninguno | La variante se omite salvo que estén presentes todos                                                                                                                                                                         |

Una variante es una clase por derecho propio, así que un mundo que contenga una depende del pack que la creó, igual que depende de un mod. Si quitas el archivo, las criaturas de ese mundo desaparecen con él.

**Un huevo o generador que da una mezcla.** Una variante es una clase por derecho propio, así que por sí sola siempre genera exactamente lo que dice. `becomes` es la manera en que un pack rompe eso: una lista de variantes en las que esta puede convertirse al aparecer, cada una con un peso, decidido por criatura.

```json
{
  "becomes": [
    { "variant": "mypack:walker", "weight": 95 },
    { "variant": "mypack:little_walker", "weight": 5 }
  ]
}
```

Nombrarse a sí misma es la forma de seguir siendo como es, y los pesos son las probabilidades. Pon eso en `mypack:walker` y un huevo y una entrada de aparición darán sobre todo caminantes con algún pequeño ocasional, como un huevo de zombi te da de vez en cuando un bebé. Ocurre cuando la criatura entra en el mundo, así que vale por igual para huevos, `/summon` y aparición natural, y la criatura que llega es una real de la variante elegida con todo lo que esa variante dice. Un generador es más estricto: sortea solo entre las variantes del mismo mob base y del mismo equipo que la variante a la que está asignado, de modo que un generador de zombis da los zombis de ese equipo y sus crías, y nunca una criatura de otra clase ni de otro equipo, igual que un generador de zombis de vanilla sigue siendo un generador de zombis. Una variante alcanzada de este modo no vuelve a transformarse, así que dos variantes pueden nombrarse mutuamente sin entrar en bucle.

**Dónde encaja `baby`.** El juego no tiene un zombi bebé propio: hay un único zombi que sortea si es un niño al aparecer. `baby` dice con qué frecuencia, así que `"baby": 0.05` es el hábito de vanilla y `"baby": true` es siempre. Una variante no toma además la tirada propia del zombi, de modo que no aparece ningún niño ni jinete de gallina que `baby` no haya pedido; `keepsBaseBaby` devuelve esa tirada. Entre ambas son dos maneras de llegar a lo mismo, y cuál elegir depende de la diferencia que busques: `baby` por sí solo da una variante que a veces es cría, `becomes` da varias variantes que se distinguen en lo que quieras, y una mezcla de ambas es válida.

### Aspecto

*variantes de entidades*

| Clave        | Obligatorio | Valor                                  | Predeterminado | Qué hace                                                                                                          |
| ------------ | ----------- | -------------------------------------- | -------------- | ----------------------------------------------------------------------------------------------------------------- |
| `texture`    | no          | `namespace:textures/entity/<file>.png` | ninguno        | Una piel propia, con la misma disposición que la de la entidad que copia                                          |
| `leftHanded` | no          | booleano                               | `false`        | Sostiene su arma en la otra mano                                                                                  |
| `glowing`    | no          | booleano                               | `false`        | Con contorno visible a través de las paredes                                                                      |
| `invisible`  | no          | booleano                               | `false`        | No se dibuja, aunque su equipo sí                                                                                 |
| `scale`      | no          | float                                  | `1.0`          | Con qué tamaño se dibuja, y qué tamaño tiene su hitbox                                                            |
| `angryScale` | no          | float                                  | `scale`        | El tamaño al que se hincha mientras tiene algo que atacar, y durante tres segundos tras perderlo                  |
| `width`      | no          | float                                  | el de la base  | Su hitbox a lo ancho, antes de aplicar `scale`                                                                    |
| `height`     | no          | float                                  | el de la base  | Su hitbox a lo alto, antes de aplicar `scale`                                                                     |
| `bright`     | no          | booleano                               | `false`        | Se dibuja con luz plena dondequiera que esté, como bajo el sol de mediodía, de modo que nunca lo atenúan la noche, la sombra ni una cueva |
| `hideArmor`  | no          | booleano                               | `false`        | Lleva su armadura sin que se dibuje                                                                               |
| `hideHeld`   | no          | booleano                               | `false`        | Lo mismo para lo que sostiene                                                                                     |
| `tint`       | no          | color hexadecimal                      | ninguno        | Colorea la entidad al dibujarla                                                                                   |
| `tintParts`  | no          | lista de `body`, `armor`, `held`       | `["body"]`     | A qué partes llega el tinte                                                                                       |

`scale` cambia tanto el modelo como la hitbox en ambos lados, de modo que lo que ves es lo que puedes golpear. Una criatura que cambia su propio tamaño, un animal que crece o un zombi que es un niño, se escala en torno al tamaño que haya elegido, de modo que ambos no se peleen. `angryScale` la hincha mientras tiene un objetivo y la devuelve a `scale` cuando lo pierde. Como el cliente nunca sabe qué está cazando una criatura, el indicador de carrera lleva esa noticia: se activa en una variante que usa `angryScale` y en nada más, de modo que un mod que lea la carrera en tus variantes la verá cambiar. Crecer bajo un techo bajo es posible, igual que cuando crece un slime, así que mantén la diferencia modesta.

Una `texture` se enlaza en lugar de la que la entidad usaría normalmente, sea cual sea el renderizador que herede, de modo que funciona tanto con entidades de mods como con las de vanilla. Tiene que coincidir con el modelo sobre el que se dibuja, ya que el modelo es el de la entidad base, una piel, no una forma nueva. Las capas conservan sus propias texturas, así que la armadura sigue pareciendo armadura en un zombi con otra piel.

La armadura solo se dibuja en una entidad cuyo renderizador tenga capa de armadura, es decir, los mobs humanoides y los aldeanos. Una variante de vaca o de araña puede llevar armadura y obtiene su protección, pero nada la dibuja, así que `armor` bajo `attributes` suele ser la forma más limpia de hacer resistente a una criatura así. `hideArmor` es para el otro caso: un humanoide que debe conservar la armadura en sus ranuras, por la protección o por un mod que las lee, sin que se vea.

### Sus sonidos

*variantes de entidades*

| Clave         | Obligatorio | Valor    | Predeterminado | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ------------- | ----------- | -------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `sounds`      | no          | objeto   | los de la base | `ambient`, `hurt` y `death`, cada uno un evento de sonido registrado, y un nombre de 1.12.2 todavía se lee, véase [nombres de sonido](#listas-de-valores). Otros tres para los que no tiene sonido base: `target` se reproduce una vez cada vez que fija un objetivo, y `explode` es cómo suena su explosión, ya sea que explote ella misma con `explodes` o lance TNT con `throws`. `throw` se reproduce cuando lanza cualquier cosa con `throws`, en lugar del lanzamiento de bola de nieve, o del siseo de la mecha para el TNT. `targetVaries` desplaza cada reproducción de `target` hacia arriba o hacia abajo una cantidad aleatoria dentro de esos semitonos, así que `3` oscila un cuarto de octava hacia cada lado; `0` la reproduce tal cual. El sonido de la explosión se emite en lugar del propio del juego |
| `soundVolume` | no          | número   | `1.0`          | Qué volumen tienen esos sonidos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `soundPitch`  | no          | número   | `1.0`          | Qué tono tienen. Menos de 1 es más grave, más de 1 es más chillón                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `silent`      | no          | booleano | `false`        | No hace ningún sonido                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |

### Salud, daño y efectos

*variantes de entidades*

| Clave               | Obligatorio | Valor                                               | Predeterminado    | Qué hace                                                                                                                                                                                                                                                                                                       |
| ------------------- | ----------- | --------------------------------------------------- | ----------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `immuneTo`          | no          | lista de tipos de daño                              | ninguno           | Daño que ignora, por los nombres que usaba 1.12.2, `fall`, `drown`, `explosion`, `explosion.player`, `magic`, `indirectMagic`, `mob`, `player`, `inWall` y el resto, o por un id de tipo de daño. Véase [tipos de daño](#listas-de-valores)                                                                       |
| `fallDamage`        | no          | float                                               | `1.0`             | Multiplica el daño que causa una caída. `0` elimina el daño por caída                                                                                                                                                                                                                                          |
| `absorption`        | no          | float                                               | `0`               | Corazones extra sobre su salud                                                                                                                                                                                                                                                                                 |
| `creatureAttribute` | no          | `undefined`, `undead`, `arthropod` o `illager`      | el de la base     | Qué cuenta como, de modo que Aspecto sagrado y las pociones de curación lo traten en consecuencia                                                                                                                                                                                                              |
| `effects`           | no          | lista de objetos                                    | ninguno           | Efectos que tiene siempre: `{ "potion": "minecraft:strength", "amplifier": 1 }`                                                                                                                                                                                                                                |
| `fireproof`         | no          | booleano                                            | `false`           | Nunca se prende fuego en absoluto, así que el fuego y la lava nunca le hacen daño y nunca arde a la luz del día                                                                                                                                                                                                |
| `invulnerable`      | no          | booleano                                            | `false`           | No recibe daño de nada salvo del vacío y del modo creativo                                                                                                                                                                                                                                                     |
| `attributes`        | no          | objeto                                              | ninguno           | `maxHealth`, `movementSpeed`, `attackDamage`, `attackSpeed`, `knockbackResistance`, `followRange`, `armor`. Un atributo que la entidad no tiene normalmente se le concede. `attackSpeed` son golpes por segundo para un luchador cuerpo a cuerpo, `1` como lo tiene el juego, así que `2` golpea el doble de a menudo |
| `hurtResistance`    | no          | entero, ticks                                       | los `20` del juego | Cuánto tiempo después de un golpe no se le puede volver a herir. Los golpes más rápidos que la mitad de esto se pierden, así que un atacante rápido necesita un objetivo con menos                                                                                                                               |
| `ignoresEffects`    | no          | lista de nombres de efectos                         | ninguno           | Efectos que nunca le afectan, lo aplique quien o lo que lo aplique: un golpe, una salpicadura, un faro, una flecha, `/effect`. `all` rechaza todos los efectos, de modo que una variante parte de una pizarra en blanco. Sus propios `effects` se le siguen aplicando                                             |

### Movimiento

*variantes de entidades*

| Clave            | Obligatorio | Valor            | Predeterminado | Qué hace                                                                                                                                                                                                                                                                                                                                      |
| ---------------- | ----------- | ---------------- | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `jumpMultiplier` | no          | float            | `1.0`          | Cuánto más alto salta que la entidad que copia                                                                                                                                                                                                                                                                                                |
| `maxFallHeight`  | no          | entero           | el de la base  | Cuánto caerá al trazar rutas                                                                                                                                                                                                                                                                                                                  |
| `noAI`           | no          | booleano         | `false`        | Se queda donde se la deja y no hace nada                                                                                                                                                                                                                                                                                                      |
| `leashable`      | no          | booleano         | `false`        | Se puede llevar con una correa, aunque la entidad que copia nunca pudiera                                                                                                                                                                                                                                                                     |
| `steerable`      | no          | booleano         | `false`        | Se puede dirigir mientras se la monta                                                                                                                                                                                                                                                                                                         |
| `pathPriorities` | no          | objeto           | ninguno        | Por dónde caminará, como `WATER`, `LAVA`, `DANGER_FIRE`, `DOOR_WOOD_CLOSED` y el resto de los tipos de ruta del juego, cada uno un número en el que un valor negativo significa nunca. Los `DANGER_CACTUS` y `DAMAGE_CACTUS` de 1.12.2 se leen como `DANGER_OTHER` y `DAMAGE_OTHER`, donde esta versión clasifica el cactus junto con los arbustos de bayas dulces |
| `stepHeight`     | no          | float, bloques   | el de la base  | Qué altura de desnivel sube caminando sin saltar                                                                                                                                                                                                                                                                                              |
| `climbs`         | no          | booleano         | el de la base  | Activado, trepa por cualquier pared contra la que camine, como una araña, sea cual sea su base. Desactivado, no trepa nada, ni siquiera una escalera, y una araña se queda en el suelo                                                                                                                                                         |
| `teleports`      | no          | booleano         | `true`         | Si un enderman o un shulker puede teletransportarse. Desactivado, se queda donde está, también a la luz del día y en el agua                                                                                                                                                                                                                  |
| `walks`          | no          | booleano         | `false`        | Un conejo camina como los demás animales en lugar de moverse a saltos. Solo lo lee un conejo                                                                                                                                                                                                                                                  |

### Agua

*variantes de entidades*

| Clave                | Obligatorio | Valor    | Predeterminado | Qué hace                                                                                                                                                                                                                                              |
| -------------------- | ----------- | -------- | -------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `breathesUnderwater` | no          | booleano | `false`        | Nunca se ahoga, y se hunde para caminar por el fondo en lugar de nadar hacia la superficie. Sigue orientándose por el suelo, así que el agua profunda de la que no pueda salir caminando la retendrá                                                  |
| `swims`              | no          | booleano | `false`        | Se mueve por el agua como un calamar o un guardián, y nunca se ahoga. Se orienta por el agua en lugar de por el suelo, así que su sitio está en el agua y queda varada fuera de ella                                                                   |
| `amphibious`         | no          | booleano | `false`        | Camina por tierra y nada con soltura en el agua, cambiando su forma de orientarse al entrar y salir del agua. Nunca se ahoga. Lo que estuviera persiguiendo se olvida en la orilla, así que duda un instante cada vez que la cruza                      |
| `waterSlowdown`      | no          | float    | `0.8`          | Cuánto la frena el agua. Más alto es más rápido                                                                                                                                                                                                       |

### Combate

*variantes de entidades*

| Clave           | Obligatorio | Valor                          | Predeterminado         | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| --------------- | ----------- | ------------------------------ | ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `hostile`       | no          | booleano                       | `false`                | Ataca lo que alcanza y se defiende cuando la hieren. Una variante hostil cuenta como monstruo para el juego sea cual sea su base, así que el límite de monstruos la contiene. El modo pacífico la elimina solo cuando su base es un monstruo; cualquier otra base se queda, incapaz de herir a un jugador allí. Descarta las tareas de animal con las que venía su base: criar, dejarse tentar, seguir a un progenitor, a un dueño o a los de su especie, sentarse                          |
| `targets`       | no          | lista de nombres de entidades  | el jugador             | Lo que sale a buscar mientras es hostil. `minecraft:player` se entiende aunque el jugador no sea una entidad registrada                                                                                                                                                                                                                                                                                                                                                                    |
| `attackReach`   | no          | float, bloques                 | su tamaño              | Hasta dónde llega un golpe cuerpo a cuerpo. El juego alcanza el doble del ancho, por lo que una criatura ampliada golpea desde más lejos; esto lo fija de forma directa                                                                                                                                                                                                                                                                                                                    |
| `knockback`     | no          | float                          | el de la base, `0.4`   | Con qué fuerza empujan sus golpes. `0` no empuja en absoluto                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `hitEffects`    | no          | booleano                       | `true`                 | Si aplica a lo que golpea el efecto que aplica la entidad que copia: el wither de un esqueleto wither, el veneno de una araña de las cuevas, el hambre de un husk. Desactivado, golpea solo con daño                                                                                                                                                                                                                                                                                       |
| `hitFire`       | no          | booleano                       | `true`                 | Si prende fuego a lo que golpea cuando lo haría la entidad que copia: un zombi en llamas, la bola de fuego de un blaze. Desactivado, nada de lo que hace inicia un fuego en su objetivo                                                                                                                                                                                                                                                                                                    |
| `passive`       | no          | booleano                       | `false`                | Le impide atacar a nada, sea cual sea su comportamiento normal                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `threatLeast`   | no          | entero                         | `0`                    | La franja de amenaza más baja en la que debe encontrarse un jugador u otro portador a menos de 128 bloques para que la variante aparezca de forma natural. `0` aparece como de costumbre                                                                                                                                                                                                                                                                                                   |
| `threatHostile` | no          | entero                         | `0`                    | La franja de amenaza más baja en la que debe encontrarse un jugador para que la variante vaya a por él por su cuenta. Por debajo, la variante es dócil con ese jugador, aunque se defiende si la golpean. `0` ataca como de costumbre                                                                                                                                                                                                                                                      |

`hostile` también elimina el comportamiento que hacía huir a la criatura: un animal que evitaba a los jugadores o entraba en pánico al ser herido no hace ninguna de las dos cosas una vez es hostil, ya que de lo contrario huiría de aquello a lo que debe atacar. Necesita una entidad que camine por el suelo, ya que usa el mismo comportamiento de ataque que vanilla da a sus propios mobs. Una base voladora o nadadora se registra y se deja como está. `passive` funciona de forma más amplia, pero solo alcanza el comportamiento construido como lo construye vanilla; un mod cuya hostilidad está escrita en su propio código de tick o de daño no es algo de lo que un pack pueda disuadirla.

### Equipo, drops y experiencia

*variantes de entidades*

| Clave                | Obligatorio | Valor                       | Predeterminado | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| -------------------- | ----------- | --------------------------- | -------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `lootTable`          | no          | `namespace:entities/<name>` | el de la base  | Lo que suelta. Sin esto suelta lo que suelte la entidad que copia                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `experience`         | no          | entero                      | el de la base  | Cuánta experiencia suelta                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `collectsExperience` | no          | booleano                    | `false`        | Recoge experiencia como lo hace un jugador: los orbes a ocho bloques o menos van hacia ella y se toman al contacto, el Remendado de su equipo se repara primero, y los puntos forman niveles según la propia curva del jugador, conservados en el mob a través de un guardado. Lo que mata suelta su experiencia como si lo hubiera matado un jugador, un bloque que rompe su tarea `digs` suelta la experiencia propia del bloque, y una tirada de experiencia de `block_drops` también le llega. Al morir suelta siete por nivel hasta cien, salvo que `keepInventory` esté activado. Los objetivos con el criterio `xp` o `level` llevan su total y su nivel en una fila con el nombre de su UUID, de modo que una función los lee con `execute if score` o con un selector `scores={<objective>=N..}`. Gasta sus niveles en trabajos de yunque como lo hace un jugador, véase [Trabajo en el yunque](#trabajo-en-el-yunque) |
| `dropChance`         | no          | 0 a 1                       | `0`            | Con qué probabilidad suelta cada pieza de equipo                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `picksUpLoot`        | no          | booleano                    | `false`        | Recoge lo que pisa                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `equipment`          | no          | objeto                      | ninguno        | `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`, cada uno un nombre de ítem. En 26.x, las crías llevan su propio modelo de armadura; cuando la armadura de un pack no incluye capas para crías, RDPL redibuja la armadura de adulto del pack sobre el modelo de cría                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |

Una variante suelta lo que suelte la entidad que copia, porque la tabla de botín está fijada en el código de esa entidad en lugar de buscarse por nombre. `lootTable` la apunta a una tabla propia, que luego aportas en `loot_tables/entities/<name>.json` como cualquier otra.

### Comportamientos especiales

*variantes de entidades*

| Clave            | Obligatorio | Valor              | Predeterminado         | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| ---------------- | ----------- | ------------------ | ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `digs`           | no          | booleano           | `false`                | Cava a través de lo que se interponga entre ella y su objetivo, con la herramienta que lleva en la mano: una pala a través de tierra, arena y grava, un pico a través de piedra, un hacha a través de madera, y solo lo que el material de esa herramienta pueda romper, de modo que un pico de madera nunca abre mineral de hierro y nada abre la obsidiana sin diamante. Un bloque tarda lo que tardaría con un jugador que usara esa herramienta, suelta lo que soltaría y desgasta la herramienta. Dale la herramienta con `equipment`; con las manos vacías no cava nada, y no cava nada donde `mobGriefing` esté desactivado. Nunca busca un camino alternativo: con un objetivo va directa hacia él y cava lo que se interponga, y donde la herramienta no puede abrir el bloque se queda empujando. Necesita `hostile`. Toma sus objetivos sin necesidad de verlos, ya que aquello hacia lo que cava está por naturaleza detrás de algo |
| `throws`         | no          | booleano           | `false`                | Lanza lo que sostiene contra su objetivo desde la distancia, y si es TNT lo enciende y se aparta. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `throwAmmo`      | no          | entero             | ninguno                | Cuántos tiene para lanzar. Si se omite, nunca se queda corta                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `throwReload`    | no          | entero, segundos   | `explosionFuse`        | Cuánto tiempo permanece vacía su mano antes de sacar otro                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `throwRetreat`   | no          | entero, segundos   | `explosionFuse`        | Cuánto tiempo se mantiene alejada tras un lanzamiento antes de volver                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `throwPower`     | no          | float              | `1.0`                  | Con qué fuerza lanza. Duplicarlo duplica aproximadamente el alcance                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `throwArc`       | no          | float              | `0.35`                 | A qué altura lanza en parábola. Más alto, más tiempo en el aire; casi cero es un lanzamiento plano; por debajo de cero lanza hacia abajo                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `throwReturns`   | no          | booleano           | `false`                | Lo que lanza vuela como un tridente: golpea con el `attackDamage` de la variante, o 8 en una base que no lo tenga, y luego vuelve volando a su mano como Lealtad devuelve un tridente. Nunca se agota y se apunta al objetivo como lo hace un esqueleto, más rápido con `throwPower` y con menos dispersión en dificultades más altas, y el lanzador se mantiene firme mientras vuela, así que `throwAmmo`, `throwReload`, `throwRetreat` y `throwArc` no se le aplican. El TNT se lanza como siempre                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `explodes`       | no          | booleano           | `false`                | Estalla junto a su objetivo, como un creeper. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `explosionPower` | no          | número             | `3.0`                  | Qué tamaño tiene la explosión. Un creeper es 3, el TNT es 4. En una base de creeper o ghast, escribir esto o `explosionFuse` fija también la explosión propia de la base sin `explodes`: el tamaño de la explosión y la mecha de un creeper, la bola de fuego de un ghast, cada uno en números enteros, de modo que un ghast al que solo se da `explosionFuse` explota con 3 en lugar de su 1 propio                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `explosionFuse`  | no          | entero, ticks      | `30`                   | Cuánto tiempo sisea antes de estallar, y la mecha propia de una base de creeper                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `explosionFire`  | no          | booleano           | `false`                | Deja fuegos tras de sí                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `charges`        | no          | booleano           | `false`                | Se lanza contra su objetivo desde la distancia y golpea con un fuerte empuje al contacto, como un ravager, y luego descansa antes de la siguiente embestida. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `pounces`        | no          | booleano           | `false`                | Se agazapa y luego salta sobre su objetivo en arco y golpea al aterrizar, como un zorro. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `sniffs`         | no          | entero, bloques    | `0`                    | Oye a los jugadores que se mueven a esa cantidad de bloques, haya paredes o no, y camina hasta donde los oyó; un jugador que va agachado o parado no se oye, y uno que vea después se convierte en su objetivo. `0` no escucha. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `fleesWhenHurt`  | no          | 0.0 a 1.0          | `0`                    | Interrumpe el combate y huye de aquel con quien lucha mientras su salud esté por debajo de esa fracción, y vuelve cuando la supera. `0` nunca huye. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `sleepsByDay`    | no          | booleano           | `false`                | Busca sombra de día y se queda quieta allí hasta la noche o hasta que algo la ataque. Mientras descansa, se tumba de lado                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `home`           | no          | entero, bloques    | `0`                    | Se mantiene a esa cantidad de bloques en torno al punto donde estuvo por primera vez, deambulando dentro de él y volviendo cuando se aleja. `0` vaga libremente                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `patrols`        | no          | booleano           | `false`                | Recorre el terreno en largos tramos con otros de su especie que siguen a un líder, como una patrulla de pillagers. Un grupo que aparece junto elige un líder; el resto se mantiene a pocos bloques de él, y cuando el líder fija un objetivo lo hacen todos. Un seguidor que pierde a su líder asume el mando. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `swoops`         | no          | booleano           | `false`                | Da vueltas sobre su objetivo y se lanza en picado a través de él, golpeando en la pasada, como un phantom. La variante recibe un ayudante volador, así que vuela mientras caza y se posa en el suelo cuando está inactiva; necesita una base que sea una criatura, un loro por ejemplo, y un murciélago no lo es. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `gusts`          | no          | booleano           | `false`                | Se prepara y lanza una ráfaga de viento contra su objetivo desde la distancia, arrojando hacia atrás y hacia arriba todo lo que rodea al objetivo, como la carga de viento de un breeze. Necesita `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `gustPower`      | no          | float              | `1.5`                  | Con qué fuerza arroja una ráfaga. Un golpe de un mob es 0.4, un encantamiento de retroceso fuerte, alrededor de 1                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |

**Lanzar en lugar de embestir.** `explodes` envía a una criatura a estallar contra su objetivo. `throws` es el otro temperamento: mantiene la distancia, lanza lo que lleva en la mano principal contra aquello con lo que lucha, y si resulta ser TNT lo enciende, lo lanza y se aparta mientras arde.

```json
{
  "hostile": true,
  "throws": true,
  "explosionFuse": 50,
  "equipment": { "mainhand": "minecraft:tnt" }
}
```

Lanzar vacía su mano, porque lanzó lo que tenía. Luego se mantiene alejada durante `throwRetreat`, saca otro tras `throwReload` y vuelve a por su objetivo: un ciclo de lanzar, replegarse, recargar y acercarse. Dale un `throwAmmo` y ese ciclo termina cuando se agota la cuenta, con la mano vacía para siempre y su ataque corriente tomando el relevo. Omite `throwAmmo` y nunca se queda corta.

La cuenta se escribe en la criatura, así que no se rellena porque se descargue y se vuelva a cargar un chunk. Todo lo que no sea TNT vuela como un ítem y cae, lo que hace que un zapador que lance piedras o carne podrida sea tan fácil como uno que lance explosivos.

`explosionFuse` sigue siendo la mecha del TNT lanzado, y sustituye a cualquiera de los dos temporizadores que omitas, de modo que una variante escrita antes de estas claves se comporta exactamente como antes.

Cómo vuela el lanzamiento en sí lo determinan `throwPower` y `throwArc`. El primero es un multiplicador del empujón, y como el empujón ya crece con la distancia, aumentarlo alarga el alcance sin cambiar cuánto tiempo permanece el lanzamiento en el aire. El segundo es la elevación, y cambia la forma: alto, y lanza en parábola sobre un muro y se toma su tiempo; casi cero, y se arroja plano y aterriza casi al instante; por debajo de cero, y se lanza hacia abajo contra algo que está debajo. Ambos dejan la mecha en paz, así que una carga lanzada en parábola y una plana estallan el mismo número de segundos después de salir de la mano, que es lo que decide si una revienta por encima o aterriza primero y espera. Desde qué distancia lanzará es su `followRange`, y se acerca como de costumbre una vez que estás a menos de tres bloques, así que es peligrosa a distancia y normal cara a cara.

### Tareas

*variantes de entidades*

| Clave   | Obligatorio | Valor | Por defecto | Qué hace |
| ------- | ----------- | ----- | ----------- | -------- |
| `tasks` | no          | lista | ninguno     | Cualquier tarea que tenga el juego, añadida a la variante por su nombre con la prioridad que elijas, o retirada de lo que traía su base. La lista, más abajo |

**Cualquier tarea que tenga el juego.** Las claves anteriores son comportamientos propios de RDPL. `tasks` llega más allá, hasta todas las tareas que usa el propio vanilla, sobre cualquier base: una entrada es un objeto que nombra la `task` y su `priority`, además de lo que lea esa tarea; un nombre precedido de `-` elimina todas las tareas de ese tipo que traía la base. Las prioridades empiezan en 0, y vanilla conserva las suyas entre 1 y 8, de modo que una tarea con 0 se impone a todo lo que hace la base y una con 9 solo se ejecuta cuando nada más quiere hacerlo.

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

La lista se aplica después de que `hostile`, `passive` y los comportamientos anteriores hayan hecho su trabajo, así que tiene la última palabra. Las tareas que mueven el cuerpo se bloquean entre sí: una solo se ejecuta cuando nada con mejor prioridad está moviendo a la criatura, y el ataque con el que viene un monstruo está en 2, así que un salto o una huida en un zombi necesita prioridad 1 o nunca le llega el turno; la araña y el lobo mantienen su salto por delante de su ataque por la misma razón. Una tarea que la base ya ejecuta se añade por segunda vez en lugar de sustituirse; elimina primero la antigua. Algunas tareas solo tienen sentido en una base que tenga aquello que manejan: un combate con arco necesita una base que dispare, sentarse necesita una base que se pueda domar y comerciar necesita un aldeano. Si pides una en una base que no puede llevarla, el registro indica qué base necesita y la variante prescinde de ella. En esta versión un aldeano funciona con el cerebro del juego y no con tareas, así que las filas de aldeano de más abajo solo alcanzan lo que el cerebro deja a las tareas.

| Clave       | Tipo               | Por defecto      | Qué hace |
| ----------- | ------------------ | ---------------- | -------- |
| `priority`  | int                | obligatorio      | Su posición entre las tareas de la base. Menor se ejecuta antes |
| `speed`     | número             | la habitual de la tarea | Con qué rapidez se mueve mientras se ejecuta la tarea, como multiplicador de su velocidad al caminar |
| `nearSpeed` | número             | `1.2`            | `avoidEntity`: el multiplicador cuando aquello que evita está cerca |
| `distance`  | número, bloques    | la habitual de la tarea | Hasta dónde mira, sigue, dispara o se mantiene alejado |
| `near`      | número, bloques    | la habitual de la tarea | `follow`, `followOwner`, `followOwnerFlying`: cuánto se acerca antes de detenerse |
| `chance`    | número             | la habitual de la tarea | `wander`: una tirada cada tantos ticks; `wanderAvoidWater`: la probabilidad, de 0 a 1, de salir del abrigo; `watchClosest`, `watchClosest2`: la probabilidad, de 0 a 1, de mirar en cada tick |
| `leap`      | número             | `0.4`            | `leapAtTarget`: la altura del salto |
| `cooldown`  | int, ticks         | `20`             | `attackRanged`, `attackRangedBow`: ticks entre disparos |
| `entity`    | nombre de entidad  | ninguno          | Qué entidad busca, evita, observa o con cuál se reproduce la tarea. Se entiende `minecraft:player` |
| `items`     | lista de nombres de ítem | ninguno    | `tempt`: lo que ofrece un jugador en la mano |
| `sight`     | boolean            | `true`           | `nearestAttackableTarget`, `targetNonTamed`: solo lo que puede ver |
| `nearby`    | boolean            | `false`          | `nearestAttackableTarget`: solo lo que está dentro de su propio rango de seguimiento |
| `help`      | boolean            | `false`          | `hurtByTarget`: otros de su especie cercanos se unen |
| `memory`    | boolean            | `false`          | `attackMelee`, `zombieAttack`: persigue a un objetivo que ha perdido de vista |
| `close`     | boolean            | `false`          | `openDoor`: cierra la puerta tras de sí |
| `nocturnal` | boolean            | `false`          | `moveThroughVillage`: solo de noche |
| `scared`    | boolean            | `false`          | `tempt`: un jugador que se mueve demasiado rápido rompe el hechizo |

La columna `Lista` indica dónde vive la tarea. `tasks` es lo que hace la criatura; `targets` es cómo elige a qué atacar, y una tarea de objetivo sin un ataque que le corresponda no hace nada por sí sola.

| Tarea                     | Necesita                       | Lista     | Lee                                        | Qué hace |
| ------------------------- | ------------------------------ | --------- | ------------------------------------------ | -------- |
| `attackMelee`             | una criatura que camina        | `tasks`   | `speed`, `memory`                          | Se acerca a su objetivo y lo golpea |
| `attackRanged`            | una base que dispara           | `tasks`   | `speed`, `cooldown`, `distance`            | Mantiene la distancia y dispara lo que dispare su base |
| `attackRangedBow`         | un monstruo que dispara        | `tasks`   | `speed`, `cooldown`, `distance`            | El combate con arco del esqueleto: se desplaza lateralmente, tensa y suelta |
| `avoidEntity`             | una criatura que camina        | `tasks`   | `entity`, `distance`, `speed`, `nearSpeed` | Huye de la entidad indicada cuando se acerca a menos de `distance` |
| `beg`                     | un lobo                        | `tasks`   | `distance`                                 | Pide comida a un jugador que la lleva en la mano |
| `breakDoor`               | cualquier base                 | `tasks`   |                                            | Rompe las puertas de madera que le estorban, en dificultad difícil |
| `creeperSwell`            | un creeper                     | `tasks`   |                                            | Sisea y explota junto a su objetivo |
| `defendVillage`           | un gólem de hierro             | `targets` |                                            | Va a por quien haya atacado a un aldeano |
| `eatGrass`                | cualquier base                 | `tasks`   |                                            | Come hierba, como hace una oveja |
| `findEntityNearest`       | cualquier base                 | `targets` | `entity`                                   | Elige como objetivo la entidad indicada más cercana, como hacen un slime o un ghast |
| `findEntityNearestPlayer` | cualquier base                 | `targets` |                                            | Elige como objetivo al jugador más cercano al que pueda llegar |
| `fleeSun`                 | una criatura que camina        | `tasks`   | `speed`                                    | Busca sombra cuando le da el sol |
| `follow`                  | cualquier base                 | `tasks`   | `speed`, `near`, `distance`                | Sigue a otros de su misma especie |
| `followGolem`             | un aldeano                     | `tasks`   |                                            | Sigue a un gólem de hierro que ofrece una amapola |
| `followOwner`             | una base domable               | `tasks`   | `speed`, `near`, `distance`                | Sigue a su dueño y se teletransporta hasta él cuando se queda muy atrás |
| `followOwnerFlying`       | una base domable               | `tasks`   | `speed`, `near`, `distance`                | Lo mismo, volando |
| `followParent`            | un animal                      | `tasks`   | `speed`                                    | Una cría se mantiene cerca de un adulto de su especie |
| `harvestFarmland`         | un aldeano                     | `tasks`   | `speed`                                    | Cosecha los cultivos maduros y los vuelve a plantar |
| `hurtByTarget`            | una criatura que camina        | `targets` | `help`                                     | Se defiende de lo que le haya golpeado |
| `landOnOwnersShoulder`    | un loro                        | `tasks`   |                                            | Se sube al hombro de su dueño |
| `leapAtTarget`            | cualquier base                 | `tasks`   | `leap`                                     | Salta sobre su objetivo desde cerca |
| `llamaFollowCaravan`      | una llama                      | `tasks`   | `speed`                                    | Se coloca detrás de una llama guiada |
| `lookAtTradePlayer`       | un aldeano                     | `tasks`   |                                            | Mira al jugador con el que está comerciando |
| `lookAtVillager`          | un gólem de hierro             | `tasks`   |                                            | De vez en cuando ofrece una amapola a un aldeano y lo mira |
| `lookIdle`                | cualquier base                 | `tasks`   |                                            | De vez en cuando mira a su alrededor |
| `mate`                    | un animal                      | `tasks`   | `speed`, `entity`                          | Se reproduce cuando está en modo amor, con los de su especie o con la `entity` indicada |
| `moveIndoors`             | una criatura que camina        | `tasks`   |                                            | Entra en una casa de la aldea al anochecer |
| `moveThroughVillage`      | una criatura que camina        | `tasks`   | `speed`, `nocturnal`                       | Recorre los caminos de la aldea de puerta en puerta |
| `moveTowardsRestriction`  | una criatura que camina        | `tasks`   | `speed`                                    | Vuelve hacia su lugar de origen cuando se aleja |
| `moveTowardsTarget`       | una criatura que camina        | `tasks`   | `speed`, `distance`                        | Se acerca a un objetivo que está lejos |
| `nearestAttackableTarget` | una criatura que camina        | `targets` | `entity`, `sight`, `nearby`                | Elige como objetivo la entidad indicada más cercana |
| `ocelotAttack`            | cualquier base                 | `tasks`   |                                            | El acecho y el salto del gato |
| `ocelotSit`               | un gato                        | `tasks`   | `speed`                                    | Se sienta en cofres, camas y hornos encendidos. Los ocelotes domados pasaron a ser gatos, así que esta tarea requiere una base de gato |
| `openDoor`                | cualquier base                 | `tasks`   | `close`                                    | Abre las puertas de madera por las que pasa |
| `ownerHurtByTarget`       | una base domable               | `targets` |                                            | Va a por lo que haya golpeado a su dueño |
| `ownerHurtTarget`         | una base domable               | `targets` |                                            | Va a por lo que haya golpeado su dueño |
| `panic`                   | una criatura que camina        | `tasks`   | `speed`                                    | Huye cuando le hacen daño o está en llamas |
| `play`                    | un aldeano                     | `tasks`   | `speed`                                    | Los niños juegan al pilla-pilla entre ellos |
| `restrictOpenDoor`        | una criatura que camina        | `tasks`   |                                            | Se queda dentro de las puertas de la aldea por la noche |
| `restrictSun`             | una criatura que camina        | `tasks`   |                                            | Se mantiene a la sombra de día |
| `runAroundLikeCrazy`      | un caballo, burro, mula o llama | `tasks`  | `speed`                                    | Desmonta a un jinete en el que aún no confía |
| `sit`                     | una base domable               | `tasks`   |                                            | Se sienta cuando se lo ordenan |
| `skeletonRiders`          | un caballo esqueleto           | `tasks`   |                                            | Invoca jinetes esqueleto cuando se acerca un jugador, el caballo trampa |
| `swimming`                | cualquier base                 | `tasks`   |                                            | Mantiene la cabeza fuera del agua |
| `targetNonTamed`          | una base domable               | `targets` | `entity`, `sight`                          | Elige como objetivo la entidad indicada mientras aún no está domado |
| `tempt`                   | una criatura que camina        | `tasks`   | `items`, `speed`, `scared`                 | Sigue a un jugador que ofrece uno de los `items` |
| `tradePlayer`             | un aldeano                     | `tasks`   |                                            | Se queda quieto mientras comercia |
| `villagerInteract`        | un aldeano                     | `tasks`   |                                            | Charla con otros aldeanos |
| `villagerMate`            | un aldeano                     | `tasks`   |                                            | Se reproduce cuando la aldea tiene sitio |
| `wander`                  | una criatura que camina        | `tasks`   | `speed`, `chance`                          | Deambula |
| `wanderAvoidWater`        | una criatura que camina        | `tasks`   | `speed`, `chance`                          | Deambula evitando el agua |
| `wanderAvoidWaterFlying`  | una criatura que camina        | `tasks`   | `speed`                                    | Deambula por el aire y se posa en los árboles |
| `watchClosest`            | cualquier base                 | `tasks`   | `entity`, `distance`, `chance`             | Mira a la entidad indicada más cercana, al jugador si no se indica ninguna |
| `watchClosest2`           | cualquier base                 | `tasks`   | `entity`, `distance`, `chance`             | Lo mismo, mantenido mientras se ejecuta otra tarea |
| `zombieAttack`            | un zombi                       | `tasks`   | `speed`, `memory`                          | El ataque del zombi, con los brazos en alto |

### Aparición y desaparición

*variantes de entidades*

| Clave               | Obligatorio | Valor                   | Por defecto  | Qué hace |
| ------------------- | ----------- | ----------------------- | ------------ | -------- |
| `despawns`          | no          | boolean                 | `true`       | Desactivado, se queda incluso cuando normalmente se eliminaría |
| `despawnAfter`      | no          | int, segundos           | ninguno      | Se va sin hacer ruido cuando lleva este tiempo en el mundo, por lejos que esté cualquiera |
| `persistent`        | no          | boolean                 | `false`      | Nunca desaparece |
| `ignoresSpawnRules` | no          | boolean                 | `false`      | Aparece donde se la coloque, ignorando las reglas heredadas |
| `spawns`            | no          | lista de objetos        | ninguno      | `creatureType`, `weight`, `min` y `max`, con la misma forma que usa un bioma. `creatureType` es uno de los [tipos de criatura](#listas-de-valores), `creature` si se omite, y elige la lista de apariciones a la que se une la entrada; una entrada con un tipo que el juego no conoce no añade nada |
| `biomes`            | no          | lista de nombres de bioma | todos los biomas | Dónde se añaden esas apariciones, por id de bioma o por el nombre que 1.12.2 mostraba para un bioma de vanilla, como `Extreme Hills`. Sin esta clave ni `biomeTypes`, todos los biomas las reciben, el Nether y el End incluidos, y un bioma que coincida con ambas listas recibe cada aparición una sola vez |
| `biomeTypes`        | no          | lista de tipos de bioma | ninguno      | Lo mismo, por palabra de tipo |

**Una criatura con fecha de caducidad.** `despawnAfter` cuenta en segundos desde el momento en que una criatura entra por primera vez en el mundo y la retira sin hacer ruido cuando se acaba el tiempo: sin muerte, sin drops, sin sonido, exactamente como si se hubiera alejado y la hubieran eliminado. El reloj queda escrito en la propia criatura, así que sigue corriendo a través de un guardado y una recarga en lugar de empezar de nuevo cada vez que un chunk vuelve.

Es algo independiente, no un empujón a las reglas que gobiernan `despawns` y `persistent`. Esas dos deciden si el juego puede eliminar a una criatura por estar lejos de todos; esta es una promesa de que se irá a una hora fija pase lo que pase. Una criatura puede ser `persistent` y aun así tener fecha de caducidad, que es lo que quieres para algo invocado para un combate o un evento que no debería sobrevivirle.

El reloj corre con el tiempo del mundo, así que se detiene cuando nadie está jugando y no cuenta los minutos que un chunk pasó descargado.

### Red

*variantes de entidades*

| Clave               | Obligatorio | Valor   | Por defecto | Qué hace |
| ------------------- | ----------- | ------- | ----------- | -------- |
| `trackingRange`     | no          | int     | `80`        | Desde qué distancia se informa al cliente de su existencia |
| `trackVelocity`     | no          | boolean | `true`      | Envía su velocidad además de su posición. Desactivado ahorra tráfico en cosas que apenas se mueven |
| `trackingFrequency` | no          | int     | `3`         | Cada cuánto, en ticks |

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
        { "tag": "c:ingots/iron" }
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

`storage` da a una variante de cualquier entidad ranuras de ítems, un tanque de fluido y un búfer de energía, cada uno solo cuando se escribe su objeto. Cada uno se ofrece como la capacidad de ítems, fluido o energía de la entidad, de modo que todo lo que mueva ítems, fluido o energía hacia una entidad la alcanza. Allí donde la entidad base responde por sí misma a esa capacidad, como hace un mob con sus manos y su armadura o un caballo o una vagoneta con cofre con su inventario, el almacenamiento del pack responde en su lugar, por todos los lados. Un jugador abre la pantalla agachándose y haciendo clic derecho sobre la entidad. El contenido se guarda con la entidad.

| Clave          | Obligatorio | Valor                       | Por defecto | Qué hace |
| -------------- | ----------- | --------------------------- | ----------- | -------- |
| `items`        | no          | objeto                      | ninguno     | Da a la entidad ranuras de ítems. El área son tres filas de 9: cada barra de fluido o de energía ocupa una fila y las ranuras ocupan el resto, así que 1x9 con ambas barras, 2x9 con una y 3x9 con ninguna. Sin `items`, solo se muestran las barras |
| `fluid`        | no          | objeto                      | ninguno     | Un tanque de fluido |
| `energy`       | no          | objeto                      | ninguno     | Un búfer de Forge Energy |
| `dropsOnDeath` | no          | boolean                     | `true`      | Los ítems almacenados se esparcen como ítems sueltos donde muere la entidad. `false` los pierde. El fluido y la energía se pierden en ambos casos |
| `runsDry`      | no          | `stops`, `slows` o `hurts`  | `stops`     | Qué ocurre mientras no puede pagar un segundo completo de `use`. `stops`: deja de pensar y se queda donde está, con sus tareas, objetivos y comportamientos inactivos hasta que se le rellene, aunque sigue cayendo y se le puede empujar. `slows`: se mueve a la mitad de velocidad. `hurts`: recibe 1 de daño cada segundo, como por inanición, así que `immuneTo` con `starve` se lo ahorra |

**Funcionar con lo que lleva.** Un `use` en el tanque o en el búfer es un coste de funcionamiento: una vez por segundo la entidad toma esa cantidad, por encima de `transfer`, `buckets` y los filtros por igual. Cuando cualquiera de los dos contiene menos de un segundo completo de `use`, la entidad se ha quedado sin recursos: ya no se toma nada, `runsDry` decide qué ocurre, y vuelve a la normalidad en cuanto se rellena. Solo gasta una criatura; en una base que no está viva, como una vagoneta, `use` no hace nada.

`items`:

| Clave    | Obligatorio | Valor                    | Por defecto | Qué hace |
| -------- | ----------- | ------------------------ | ----------- | -------- |
| `filter` | no          | lista de entradas de filtro | ninguno  | Lo que aceptan las ranuras. Sin ella aceptan cualquier cosa |

`fluid`:

| Clave      | Obligatorio | Valor                    | Por defecto | Qué hace |
| ---------- | ----------- | ------------------------ | ----------- | -------- |
| `capacity` | sí          | int, mB                  | ninguno     | Cuánto cabe en el tanque |
| `filter`   | no          | lista de entradas de filtro | ninguno  | Qué fluidos acepta el tanque. Sin ella acepta cualquiera |
| `buckets`  | no          | boolean                  | `false`     | Un clic derecho con un cubo u otro recipiente de fluido, sin agacharse, lo vacía en el tanque o lo llena desde el tanque. Un clic que no mueve fluido se deja a la entidad |
| `use`      | no          | int, mB por segundo      | `0`         | Cuánto gasta la entidad del tanque cada segundo que está viva. `0` no cuesta nada |

`energy`:

| Clave      | Obligatorio | Valor              | Por defecto | Qué hace |
| ---------- | ----------- | ------------------ | ----------- | -------- |
| `capacity` | sí          | int, FE            | ninguno     | Cuánta energía contiene |
| `transfer` | no          | int, FE            | sin límite  | La máxima energía que entra o sale en una operación |
| `use`      | no          | int, FE por segundo | `0`        | Cuánta energía gasta cada segundo que está viva. `0` no cuesta nada |

Una entrada de filtro. Decide la primera entrada que coincide, y todo lo que no coincida con ninguna se rechaza:

| Clave   | Obligatorio      | Valor           | Por defecto | Qué hace |
| ------- | ---------------- | --------------- | ----------- | -------- |
| `item`  | una de las tres  | id de ítem      | ninguno     | Un ítem, como `namespace:name` |
| `tag`   | una de las tres  | id de etiqueta de ítem | ninguno | Todos los ítems de esa etiqueta, como `c:ingots/iron` |
| `fluid` | una de las tres  | id de fluido    | ninguno     | Un fluido por su id, como `minecraft:water`. Solo lo lee un filtro de fluido |
| `max`   | no               | int             | `0`         | La mayor cantidad que se guarda a la vez, contada en todas las ranuras, o en mB para un fluido. `0` es sin límite |

## Exposiciones

*criaturas y peligros*

`<namespace>/exposures/*.json`

La ruta del archivo es el nombre del peligro, y su mensaje de muerte sale de la clave de lang `death.attack.rdpl.<file name>`. Las exposiciones solo se cargan mientras `load` está activado y `vanillaClients` desactivado.

Un peligro definido por el pack: bloques, ítems y dimensiones con nombre exponen a los jugadores que están cerca de esos bloques, llevan esos ítems o permanecen en esas dimensiones, en niveles, y cada nivel aplica efectos y daño periódico. Un peligro también puede contagiarse a partir de mobs y jugadores cercanos, o caer con la lluvia ([Contagio y clima](#contagio-y-clima)). Un archivo define un peligro; varios funcionan en paralelo.

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

| Clave                 | Obligatorio      | Valor                            | Por defecto | Qué hace |
| --------------------- | ---------------- | -------------------------------- | ----------- | -------- |
| `blocks`              | una de las cinco | lista de `block` o `block=level` |             | Bloques que exponen a un jugador que está cerca de ellos. Sin nivel significa 1 |
| `items`               | una de las cinco | lista de `item` o `item=level`   |             | Ítems que exponen a un jugador que los lleva encima o puestos |
| `dimensions`          | una de las cinco | lista de `dim` o `dim=level`     |             | Ids de dimensión que exponen a cualquier jugador que esté en ellas |
| `levels`              | sí               | lista de niveles                 |             | La escala de gravedad, la primera entrada es el nivel 1. Un jugador recibe el nivel más alto que alcance cualquier fuente |
| `immunity`            | no               | nombre de poción                 | ninguno     | Un efecto cuyo portador no queda expuesto en absoluto |
| `scanInterval`        | no               | ticks                            | `20`        | Cada cuánto se comprueban el entorno y el inventario |
| `range`               | no               | bloques                          | `10`        | Hasta dónde llega la exposición de un bloque, como una esfera |
| `sourcesForNextLevel` | no               | int                              | `0`         | Esta cantidad de fuentes cercanas de un nivel lo empuja un nivel más. `0` lo desactiva |
| `skipsCreative`       | no               | boolean                          | `true`      | Los jugadores en creativo y en espectador se dejan en paz |

### Niveles

*exposiciones*

| Clave            | Obligatorio | Valor              | Por defecto | Qué hace |
| ---------------- | ----------- | ------------------ | ----------- | -------- |
| `effect`         | sí          | nombre de poción   |             | El efecto que marca el nivel en el jugador. Su presencia activa el daño, así que debería ser uno que defina el pack para esto |
| `damage`         | no          | medios corazones   | `0`         | Daño que se inflige cada `damageInterval` ticks mientras el nivel se mantiene. Ignora la armadura |
| `damageInterval` | no          | ticks              | `160`       | Cada cuánto se aplica ese daño |
| `effects`        | no          | lista de efectos   | ninguno     | Efectos adicionales aplicados a la vez, con la misma forma que usan los tipos de poción. Sin `duration` siguen la ventana de escaneo |

Los efectos del nivel duran un poco más que el siguiente escaneo, de modo que al alejarse se agotan por sí solos. La muerte por daño de exposición lee su mensaje de `death.attack.rdpl.<file name>`, que aportan los archivos de lang del pack.

### Contagio y clima

*exposiciones*

Dos fuentes más, escritas en el mismo archivo. Los portadores y los afectados expuestos transmiten el peligro a los receptores que tienen alrededor, y la lluvia o las tormentas exponen a los jugadores sobre los que caen.

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

| Clave               | Obligatorio      | Valor                              | Por defecto        | Qué hace |
| ------------------- | ---------------- | ---------------------------------- | ------------------ | -------- |
| `carriers`          | una de las cinco | lista de `entity` o `entity=level` |                    | Mobs, o `minecraft:player`, que siempre transmiten el peligro con ese nivel. Sin nivel significa 1 |
| `contagious`        | no               | boolean                            | `false`            | Cualquiera que esté expuesto transmite el peligro con el nivel que tenga |
| `catchers`          | no               | lista de nombres de entidad        | `minecraft:player` | Quién puede contagiarse. Un mob solo se contagia de portadores y afectados, nunca de bloques, ítems o clima |
| `contagionRange`    | no               | bloques                            | `4`                | Hasta dónde llega un portador o afectado, como una esfera |
| `contagionChance`   | no               | `0` a `1`                          | `0.1`              | La probabilidad, en cada escaneo del portador o afectado, de que cada receptor a su alcance se contagie |
| `contagionDuration` | no               | ticks                              | `1200`             | Cuánto tiempo un peligro contraído mantiene el nivel contraído. Contagiarse de nuevo reinicia el tiempo |
| `weather`           | una de las cinco | lista de `kind` o `kind=level`     |                    | `rain` expone a un jugador sobre el que cae la lluvia: cielo abierto sobre él en un bioma donde llueve. `thunder` cuenta durante una tormenta |
| `weatherDimensions` | no               | lista de `dim`                     | todas las dimensiones | Ids de dimensión donde el clima expone |

Un peligro contraído cuenta como una fuente más en el escaneo, y gana el nivel más alto como con cualquier otra, y `immunity` también protege contra él. Nada se propaga a menos que `contagionRange` y `contagionChance` sean superiores a `0` y el archivo nombre `carriers` o establezca `contagious`, y los mobs solo se tienen en cuenta cuando algún archivo lo hace.

---

# El mundo

## Plantillas de mundo

*el mundo*

`<namespace>/worldtemplates/*.json`

La ruta del archivo es el nombre de la plantilla, que la opción de configuración `worldTemplate` puede nombrar para elegirla directamente.

Reúne la forma de un mundo en un solo archivo, de modo que un pack incluye un mundo entero de una vez en lugar de pedir al jugador que ajuste una docena de opciones de configuración.

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

| Clave        | Obligatorio | Valor                                               | Por defecto         | Qué hace |
| ------------ | ----------- | --------------------------------------------------- | ------------------- | -------- |
| `name`       | no          | string                                              | el nombre del archivo | Se muestra en el registro y en los informes |
| `default`    | no          | nombre de bioma o `void`                            | `void`              | Qué llena un bioma que el bloqueo ha eliminado. `void` deja ahí el bioma vacío. Un bioma que no está registrado se anota en el registro y se usa el vacío. `fallback` es la misma clave con otro nombre |
| `roles`      | no          | objeto de rol a bioma                               | ninguno             | Biomas que cubren funciones concretas: `ocean`, `river`, `beach`, `mushroom`, `swamp`, `hills`, `mountain`, `jungle`, `forest`, `savanna`, `sandy`, `mesa`, `snowy`, `wasteland`, `plains` y `water`, consultadas en ese orden sea cual sea el orden en que las escriba el archivo, de modo que un bioma bloqueado que es a la vez océano y nevado toma el rol de océano. Un rol que nombra `void` o un bioma que no está registrado pasa al siguiente. Los roles se aplican solo en las `dimensions` de la plantilla; en el resto, un bioma bloqueado se convierte en el vacío |
| `structures` | no          | objeto de [nombre de estructura](#listas-de-valores) a boolean | ninguno  | Estructuras de vanilla activadas o desactivadas |
| `settings`   | no          | objeto                                              | ninguno             | Valores de configuración que establece la plantilla |
| `dimensions` | no          | lista de ids de dimensión                           | todas las dimensiones | A qué dimensiones se aplica |
| `requires`   | no          | lista de ids de mod o espacios de nombres de pack   | ninguno             | La plantilla se omite a menos que estén presentes todos |

`settings` usa los mismos nombres de clave que la configuración, así que no hay tabla de traducción que aprender.

Qué plantilla está activa lo decide la opción de configuración `worldTemplate`. Con el valor `auto`, gana el pack de mayor prioridad que incluya una, el mismo orden que sigue todo lo demás; cuando más de un pack incluye una plantilla, el registro las nombra todas y señala la que está en vigor, ya que las demás no hacen nada, ajustes incluidos. Nombrar una plantilla ahí la elige directamente. Hay cinco integradas que se pueden nombrar así: `void`, `vanilla` (océanos, ríos, playas, campos de champiñones, pantanos y colinas como los del propio juego, llanuras en todo lo demás), `ocean` (ríos y playas conservados, océano en todo lo demás), `plains` y `desert`. `auto` nunca elige una integrada.

**Un bioma puede construir de otra forma.** Un objeto `biomes` dentro de `settings` contiene ajustes de aldea propios para un bioma con nombre, de modo que una aldea del desierto pone calles de arenisca donde una de llanura pone hormigón sin que cada una sea un pack distinto. Nombra un bioma por su id, `minecraft:desert`, por una de las palabras de tipo que este mod asigna a etiquetas de bioma (`sandy`, `snowy`, `desert`, `forest`, `jungle`, `mountain`, `ocean`, `swamp`, `hot`, `cold` y el resto), o por una etiqueta escrita completa, `#minecraft:is_forest`; un id exacto se consulta antes que los tipos, así que una regla general puede sobrescribirse para un solo bioma. Todo lo que no se nombra dentro de una sección recurre al ajuste simple que tiene encima.

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

Todo ajuste de bloque que toma un camino, un puente, un ferrocarril, un metro, una estación o una alcantarilla responde a esto, y la sintaxis de mezcla con pesos funciona dentro de una sección igual que fuera. El bioma se lee a medida que se construye una pieza, y los bloques se toman de nuevo allí donde el terreno cambia de bioma, así que un camino o un ferrocarril que sale de un desierto cambia de material en el propio límite. Una línea de registro al cargar el mundo indica cuántas secciones incluía un pack y las nombra, y con la depuración activada cada bioma dice qué sección tomó, o que no tomó ninguna y a qué habría respondido.

## Reglas del juego

*el mundo*

`<namespace>/gamerules/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

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

Cada clave es el id del mundo al que pertenecen las reglas, `minecraft:overworld`, `minecraft:the_nether`, `minecraft:the_end`, uno propio del pack o el que use un mod; los números de 1.12.2 `0`, `-1` y `1` se siguen aceptando como los tres de vanilla. Los valores son cadenas, como en el comando `/gamerule`, así que `"false"` y no `false`. Se aplican a los mundos nuevos. Una regla que un archivo omite toma el valor por defecto del propio juego en ese mundo, no el valor que use el resto del guardado, y un cliente con el pack lee las mismas reglas. Un archivo de dimensión lleva las mismas reglas en un bloque `gameRules` en su lugar, que solo se aplica a ese mundo.

## Biomas

*el mundo*

`<namespace>/biomes/*.json`

La ruta del archivo es el nombre de registro del bioma, así que `mypack/biomes/ruby_forest.json` registra `mypack:ruby_forest`. `name` es solo lo que se muestra al jugador, y `biome.mypack.ruby_forest` en los archivos de lang lo dice en cada idioma.

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita.

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

### El bioma

*biomas*

| Clave       | Obligatorio | Valor                              | Por defecto        | Qué hace |
| ----------- | ----------- | ---------------------------------- | ------------------ | -------- |
| `name`      | no          | string                             | el nombre del archivo | Nombre que se muestra al jugador |
| `types`     | no          | lista de tipos de bioma            | deducidos          | Escribe el bioma en las etiquetas que representan esas palabras de tipo, como `forest`, `cold`, `wet` o `nether`, para que otros mods lo encuentren. Si se omite, los tipos se deducen del bioma como los deducía el juego: `forest` o `jungle` a partir de tres árboles o más, `plains` en caso contrario, `hot`, `cold`, `wet` y `dry` según la temperatura y la lluvia, `sparse` o `dense` según el número de árboles, `snowy` según `snow`, y `sandy`, `mushroom` o `mesa` según un suelo de arena, micelio o terracota |
| `baseBiome` | no          | nombre de bioma                    | `minecraft:plains` | Un bioma existente del que copiar ajustes. Si no es un bioma que traiga el juego o un mod, se anota en el registro y se usa la llanura |
| `requires`  | no          | lista de ids de mod o espacios de nombres de pack | ninguno | El archivo se omite a menos que estén presentes todos |

Un bioma es una entrada de data pack en esta versión, escrita por ti bajo `worldgen/biome/`, y el terreno que hay debajo es el de los ajustes de ruido y no el del bioma, por lo que no hay `baseHeight` ni `heightVariation`: la forma del terreno viene de dónde coloca el clima al bioma, como ocurre con los del propio juego. Un `id` de 1.12.2 se lee y se ignora.

### Clima

*biomas*

| Clave         | Obligatorio | Valor         | Por defecto | Qué hace |
| ------------- | ----------- | ------------- | ----------- | -------- |
| `temperature` | no          | float         | `0.5`       | Por debajo de 0.15 nieva, por encima de 1.0 hace calor de desierto |
| `rainfall`    | no          | float, 0 a 1  | `0.5`       | Cuánta humedad hay |
| `rain`        | no          | boolean       | `true`      | Si hay clima en absoluto |
| `snow`        | no          | boolean       | `false`     | Si la lluvia cae como nieve. La nieve solo cae donde la temperatura es inferior a 0.15, y esto no cambia la temperatura, así que un bioma más cálido sigue lloviendo |

### Suelo y colores

*biomas*

| Clave          | Obligatorio | Valor            | Por defecto       | Qué hace |
| -------------- | ----------- | ---------------- | ----------------- | -------- |
| `topBlock`     | no          | nombre de bloque | hierba            | El bloque de la superficie |
| `fillerBlock`  | no          | nombre de bloque | tierra            | Justo debajo de la superficie |
| `stoneBlock`   | no          | nombre de bloque | piedra            | El grueso del suelo |
| `waterColor`   | no          | color hexadecimal | `FFFFFF`         | Tinte del agua |
| `grassColor`   | no          | color hexadecimal | según el clima   | Tinte de la hierba, en lugar del color que darían la temperatura y la lluvia |
| `foliageColor` | no          | color hexadecimal | según el clima   | Tinte de las hojas, del mismo modo |
| `snowColor`    | no          | color hexadecimal | el de la dimensión | Tinte de la nieve del suelo, por encima del `snowColor` de la dimensión |

### Decoración y apariciones

*biomas*

| Clave               | Obligatorio | Valor                                                                                        | Por defecto      | Qué hace |
| ------------------- | ----------- | -------------------------------------------------------------------------------------------- | ---------------- | -------- |
| `decoration`        | no          | objeto                                                                                       | el del bioma base | Recuentos por chunk, que modifican lo que el bioma base ya coloca. Los nombres que lee son `trees`, `flowers`, `grass`, `deadbush`, `mushrooms`, `bigmushrooms`, `reeds`, `cacti`, `sand`, `gravel`, `clay` y `waterlily`, además de los interruptores `falls` (lagos y manantiales), `pumpkins`, `desertwells`, `ice` (pinchos y parches de hielo), `fossils` y `rocks` (rocas de bosque), donde cualquier valor superior a cero mantiene la tasa propia del bioma base y cero o menos elimina la característica, y `extratreechance`, un porcentaje de probabilidad de un árbol más, donde `0` también elimina el árbol extra que tira el bioma base. Un recuento sobre un tipo que el bioma base no coloca no añade nada; para eso escribe una entrada de worldgen. Cualquier otro nombre se anota en el registro y se ignora |
| `spawns`            | no          | lista de objetos                                                                             | la lista de vanilla | Véase más abajo |
| `keepDefaultSpawns` | no          | boolean                                                                                      | `false`          | Conserva la lista de vanilla junto a la tuya |
| `spawnChance`       | no          | float, menor que 1                                                                           | `0.1`            | Con qué probabilidad se coloca otra manada al crearse el terreno por primera vez. El juego sigue tirando mientras tenga éxito, así que 1 no se detiene nunca y llena el mundo hasta quedarse sin sitio. Cualquier valor igual o superior a 0.99 se rechaza y se usa 0.99 |
| `spawnRates`        | no          | objeto de `surfaceDay`, `surfaceNight`, `undergroundDay`, `undergroundNight` a un multiplicador | ninguno      | Con qué frecuencia aparecen aquí los mobs hostiles, en lugar de los ajustes globales. Véase más abajo |

Una entrada de aparición admite `entity` (obligatoria), `type` (`creature`, uno de `monster`, `creature`, `ambient` o `water`, con los guiones bajos de un nombre como `water_creature` opcionales), `weight` (`10`), `min` (`1`) y `max` (`min`).

`spawnRates` trata solo de los mobs hostiles, y de nada más. Admite cuatro claves y ninguna otra: `surfaceDay` y `surfaceNight` para donde se ve el cielo, `undergroundDay` y `undergroundNight` para donde no. Cada una es un multiplicador de la frecuencia con la que se permite aparecer a un mob hostil, `1` es la tasa normal, `0` los detiene por completo, por debajo de 1 se rechazan algunos intentos, y por encima de 1 se dejan pasar intentos que el juego habría rechazado, así que `2` es el doble. Una clave omitida significa que el bioma no decide, y se usa en su lugar el ajuste global para ese momento y lugar. Cualquier otra cosa escrita aquí no es una clave y se ignora, así que una tasa con el nombre de un tipo de criatura no hace nada en absoluto.

### Dónde se genera

*biomas*

| Clave         | Obligatorio | Valor                                    | Por defecto | Qué hace |
| ------------- | ----------- | ---------------------------------------- | ----------- | -------- |
| `placement`   | no          | objeto                                   | ninguno     | Dónde se genera. Véase más abajo |
| `villageType` | no          | `oak`, `sandstone`, `acacia` o `spruce`  | ninguno     | De qué se construye una aldea que se levante aquí: la aldea de llanura, de desierto, de sabana o de taiga. Vacío construye la de llanura, como haría sin la clave |

| Clave         | Obligatorio | Valor                                       | Por defecto | Qué hace |
| ------------- | ----------- | ------------------------------------------- | ----------- | -------- |
| `climate`     | no          | `icy`, `cool`, `medium`, `warm` o `desert`  | ninguno     | A qué franja climática se une, las mismas cinco por las que se reparten los biomas del propio overworld. Si se omite, se deja con un `weight` de 0 o nombra un clima que no figura aquí, el bioma se registra pero nunca se coloca a menos que lo pida el `roles` de una plantilla, el `biome` de una dimensión o una franja de altura |
| `weight`      | no          | int                                         | `10`        | Con qué frecuencia se elige frente a sus vecinos en esa franja |
| `villages`    | no          | boolean                                     | `false`     | Pueden generarse aldeas |
| `strongholds` | no          | boolean                                     | `false`     | Pueden generarse fortalezas |
| `playerSpawn` | no          | boolean                                     | `false`     | El punto de aparición del mundo puede colocarse aquí |

### Franjas de altura

*biomas*

| Clave       | Obligatorio | Valor                     | Por defecto      | Qué hace |
| ----------- | ----------- | ------------------------- | ---------------- | -------- |
| `minHeight` | no          | int                       | ninguno          | La y más baja desde la que este bioma toma el control como bioma 3D. Establecer cualquiera de las dos alturas convierte el bioma en una franja: la columna conserva su propio bioma fuera de ella, y dentro cada celda de 4 por 4 por 4 del mundo informa de este |
| `maxHeight` | no          | int                       | ninguno          | La y más alta de esa franja |
| `replaces`  | no          | lista de nombres de bioma | todos los biomas | Restringe la franja a las columnas cuyo propio bioma figura aquí, de modo que una franja alpina pueda quedar sobre las montañas y nada más. El bioma propio de la columna es el de su superficie. Sin `minHeight` ni `maxHeight` no hace nada |

### Temperatura según la altura

*biomas*

**Temperatura según la altura.** Un bioma se enfría a medida que sube, que es lo que pone nieve en las cimas de las montañas y detiene la lluvia por encima de una línea. Tres claves de `terrain` mueven esa curva, lo que importa en una dimensión cuyo suelo está muy por encima o por debajo de la altura que supone el juego. Sin establecer, se mantiene la curva propia del juego, así que un pack que las deja en paz no cambia nada.

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

| Clave                          | Valor | Por defecto | Qué hace |
| ------------------------------ | ----- | ----------- | -------- |
| `biomeTemperatureCenterY`      | int   | `80`        | La altura desde la que se mide la curva. A esa altura o por debajo, un bioma informa de su propia `temperature` sin tocar |
| `biomeTemperatureHeightFactor` | float | `-0.00125`  | Cuánto se mueve la temperatura por bloque por encima de esa altura, los 0.05 cada 40 bloques del propio juego. Negativo enfría con la altitud, positivo calienta |
| `biomeTemperatureScaleMaxY`    | int   | ninguno     | La altura en la que se detiene la curva, para que un mundo más alto que el del juego no siga enfriándose hasta su techo. Sin establecer, la curva llega hasta lo alto del mundo |

## Dimensiones

*el mundo*

`<namespace>/dimensions/*.json`

La ruta del archivo es el id de la dimensión, así que `mypack/dimensions/verdant.json` es `mypack:verdant`, que es lo que nombran un portal, una puerta dimensional, un archivo de reglas del juego y `/execute in`. En esta versión no hay id numérico, y un `id` o `suffix` de 1.12.2 se lee y se ignora.

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita.

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

### Nivel superior

*dimensiones*

| Clave       | Obligatorio | Valor                              | Por defecto | Qué hace |
| ----------- | ----------- | ---------------------------------- | ----------- | -------- |
| `gameRules` | no          | objeto                             | ninguno     | Reglas que se aplican solo aquí |
| `portal`    | no          | objeto                             | ninguno     | Un marco que abre esta dimensión. Véase [Abrir una dimensión con un marco](#abrir-una-dimensión-con-un-marco) |
| `requires`  | no          | lista de ids de mod o espacios de nombres de pack | ninguno | El archivo se omite a menos que estén presentes todos |

Una dimensión es una entrada de data pack en esta versión: el tipo de dimensión y los ajustes de ruido se escriben por ti bajo el espacio de nombres del pack, de modo que a un cliente vanilla se le informa de ella al unirse y viaja allí como a cualquier otra. La dimensión conserva su propia carpeta de guardado dentro del mundo, con el nombre de su id, y se carga mientras alguien está en ella, o mientras un `forceload` mantiene un chunk.

### El bloque `terrain`

*dimensiones*

| Clave              | Obligatorio | Valor                                        | Por defecto       | Qué hace |
| ------------------ | ----------- | -------------------------------------------- | ----------------- | -------- |
| `type`             | no          | `overworld`, `flat`, `void`, `nether`, `end` | `overworld`       | Cuál de los generadores del juego la construye, con sus ajustes de ruido copiados y modificados por las claves siguientes |
| `minHeight`        | no          | int, múltiplo de 16                          | el propio del tipo | El suelo de la dimensión. Más bajo que el del tipo crea un mundo profundo bajo el terreno, véase [El mundo profundo](#el-mundo-profundo) |
| `maxHeight`        | no          | int, múltiplo de 16                          | el propio del tipo | El bloque por encima de su techo |
| `generatorOptions` | no          | objeto, texto o una lista                    | ninguno           | Para `overworld` y los demás, un objeto, o el texto de uno tal como lo escribía 1.12.2, con `seaLevel`, `useLavaOceans`, y `useCaves`, `useRavines`, `useDungeons`, `useLavaLakes`, `useStrongholds`, `useVillages`, `useMineShafts`, `useTemples`, `useMonuments` y `useMansions` en false para dejarlos fuera de esta dimensión. Para `flat`, las capas, de abajo arriba, como `"minecraft:bedrock"`, `"59*minecraft:stone"`, `"3*minecraft:dirt"`, `"minecraft:grass_block"`, que es también el suelo por defecto, o el texto de superplano de 1.12.2, cuyo número de bioma establece el bioma y cuyos nombres `decoration`, `lava_lake` y de estructuras se leen como los lee el `generatorOptions` del overworld |
| `structures`       | no          | boolean                                      | `true`            | Si se generan las estructuras de vanilla |

### El bloque `biomes`

*dimensiones*

| Clave    | Obligatorio   | Valor               | Por defecto        | Qué hace |
| -------- | ------------- | ------------------- | ------------------ | -------- |
| `source` | no            | `inherit`, `single` | `inherit`          | `inherit` usa el mapa de biomas del propio overworld sea cual sea el tipo de terreno, de modo que una dimensión `nether` o `end` recibe los biomas del overworld sobre su propio suelo; `single` usa un solo bioma en todas partes. Una dimensión `flat` contiene un solo bioma en ambos casos, el de `single` o el de su texto de superplano |
| `biome`  | con `single`  | nombre de bioma     | `minecraft:plains` | Cuál es ese bioma |

### El bloque `sky`

*dimensiones*

| Clave              | Obligatorio | Valor                 | Por defecto | Qué hace |
| ------------------ | ----------- | --------------------- | ----------- | -------- |
| `hasSkyLight`      | no          | boolean               | `true`      | Si le llega la luz del día |
| `surfaceWorld`     | no          | boolean               | `true`      | Si los mapas y las brújulas se comportan como en el overworld |
| `respawn`          | no          | boolean               | `true`      | Si los jugadores reaparecen aquí |
| `respawnDimension` | no          | id de dimensión       | ninguno     | Dónde reaparecen en su lugar |
| `spawning`         | no          | boolean               | `true`      | Si aparecen mobs. Desactivado detiene toda aparición, spawners incluidos, diga lo que diga el grupo `spawning` |
| `nether`           | no          | boolean               | `false`     | Se trata como el Nether para portales y techos |
| `beds`             | no          | boolean               | `true`      | Desactivado, las camas explotan |
| `waterVaporizes`   | no          | boolean               | `false`     | El agua se evapora |
| `cloudHeight`      | no          | int                   | `128`       | Dónde están las nubes. Un ajuste `cloudHeight` que nombre esta dimensión, o uno sin nombre, se impone a este |
| `cloudColor`       | no          | color hexadecimal     | ninguno     | Tinte de las nubes |
| `cloudSpeed`       | no          | float                 | `1.0`       | Con qué rapidez se desplazan las nubes. `0` las mantiene quietas, un valor negativo las invierte |
| `groundLevel`      | no          | int                   | `63`        | Nivel del mar, usado para el horizonte, para las búsquedas de aparición y para donde aterriza una llegada por puerta dimensional o una caída sobre el vacío |
| `movementFactor`   | no          | float                 | `1.0`       | Proporción de distancia respecto al overworld. El Nether usa 8 |
| `fogColor`         | no          | color hexadecimal o `sample` | ninguno | Tinte de la niebla al mediodía. Se oscurece de noche como la niebla de vanilla. `sample` mezcla el cielo con el suelo que rodea al jugador |
| `showFog`          | no          | boolean               | `false`     | Niebla espesa, como en el Nether |
| `fogDensity`       | no          | float, 0 a 1          | `0.0`       | Cuán espesa es la niebla. `0` mantiene la distancia de vanilla, `1` la cierra hasta 8 bloques |
| `fogGroundWeight`  | no          | float, 0 a 1          | `0.5`       | Con `fogColor: sample`, cuánto cuenta el suelo frente al cielo |
| `skyColor`         | no          | color hexadecimal     | ninguno     | Tinte del cielo al mediodía. Se oscurece de noche y se vuelve gris con lluvia y tormenta como el cielo de vanilla |
| `fixedTime`        | no          | int, ticks            | ninguno     | Fija la hora del día. En 26.x un tipo de dimensión con hora fija se convierte en una dimensión sin ciclo de día en absoluto; `time query` responde ahí que no hay reloj por defecto, y eso es lo correcto |
| `sunriseColors`    | no          | boolean               | `true`      | Si el amanecer y el atardecer se tiñen |
| `ambientLight`     | no          | float, 0 a 1          | `0.0`       | Luz mínima en todas partes |
| `lightSkyColor`    | no          | color hexadecimal     | ninguno     | Tinte de la luz del día sobre bloques y mobs |
| `lightBlockColor`  | no          | color hexadecimal     | ninguno     | Tinte de la luz de las antorchas y otras luces de bloque |
| `skyFactor`        | no          | float, 0 a 1          | `1.0`       | Cuán brillante parece la luz del día. Se dibuja solo en el cliente, así que la aparición de mobs no cambia |
| `starBrightness`   | no          | float, 0 a 1          | ninguno     | Cuán brillantes son las estrellas |
| `sunBrightness`    | no          | float, 0 a 1          | `1.0`       | Cuán brillante se dibuja el sol |
| `moonBrightness`   | no          | float, 0 a 1          | `1.0`       | Cuán brillante se dibuja la luna, y con `bodies` todos los cuerpos salvo el sol |
| `renderSky`        | no          | boolean               | `true`      | Desactivado, nada dibuja el cielo, el sol, la luna ni las estrellas, y queda el color de la niebla |
| `renderClouds`     | no          | boolean               | `true`      | Desactivado, no se dibujan nubes |
| `renderWeather`    | no          | boolean               | `true`      | Desactivado, no se dibuja lluvia ni nieve |
| `sun`              | no          | objeto                | ninguno     | Tu propio sol. Véase [El renderizador del cielo](#el-renderizador-del-cielo) |
| `bodies`           | no          | lista de objetos      | ninguno     | Planetas y lunas colgados en el cielo. Véase [El renderizador del cielo](#el-renderizador-del-cielo) |
| `stars`            | no          | objeto                | ninguno     | Tu propio campo de estrellas. Véase [El renderizador del cielo](#el-renderizador-del-cielo) |

### El renderizador del cielo

*dimensiones*

Establecer cualquiera de `sun`, `bodies` o `stars` cambia el cielo de vanilla por el propio de RDPL, que dibuja la misma bóveda, el mismo resplandor del amanecer y el mismo vacío que vanilla pero toma el sol, los demás cuerpos y las estrellas del pack. Se dibuja solo en el cliente, y un servidor dedicado nunca lo carga. `renderSky: false` sigue imponiéndose y no dibuja nada, y `renderClouds: false` es como se consigue un cielo sin nubes.

Sin `bodies` se conservan la luna de vanilla y sus fases. Con `bodies`, la lista es todo lo que hay además del sol, así que una lista vacía es un cielo sin luna.

| Clave                  | Obligatorio | Valor              | Por defecto       | Qué hace |
| ---------------------- | ----------- | ------------------ | ----------------- | -------- |
| `sun.texture`          | no          | ruta de textura    | el sol de vanilla | La imagen del sol |
| `sun.size`             | no          | float              | `30`              | La mitad del ancho del sol a una distancia de 100. `0` lo oculta |
| `bodies[].texture`     | sí          | ruta de textura    |                   | La imagen del cuerpo |
| `bodies[].size`        | no          | float              | `20`              | La mitad de su ancho a una distancia de 100. La luna de vanilla es `20` |
| `bodies[].angle`       | no          | float, grados      | `180`             | Cuánto se adelanta a lo largo de la trayectoria del sol, por detrás de este. `180` es donde está la luna de vanilla. Con `followsTime` desactivado se mide desde justo encima, así que `0` es el cenit y `90` el horizonte |
| `bodies[].tilt`        | no          | float, grados      | `0`               | Cuánto se aparta de la trayectoria del sol, al norte o al sur |
| `bodies[].followsTime` | no          | boolean            | `true`            | Desactivado, queda quieto en el cielo en lugar de girar con el sol |
| `stars.count`          | no          | int                | `1500`            | Cuántas estrellas |
| `stars.size`           | no          | float              | `0.15`            | La estrella más pequeña; la mayor es dos tercios más grande |

### Niebla, luz y nubes

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

`fogColor: "sample"` lee una vez por segundo los bloques superiores de un cuadrado de 33 por 33 bloques alrededor del jugador, ilumina sus colores de mapa según la hora del día y los mezcla con el color del cielo. La niebla se va acercando a cada nueva muestra. `fogDensity` funciona con un color de niebla muestreado, uno fijo o ninguno. Bajo el agua, en la lava y mientras se está cegado se mantiene la niebla de vanilla.

`lightSkyColor` y `lightBlockColor` tiñen el mapa de luz, de modo que todo bloque iluminado y todo mob toma el tinte. `skyFactor` escala cuán brillante parece la luz del día, mientras que el nivel de luz que cuenta el servidor para la aparición de mobs y los cultivos sigue igual.

Sin `cloudLayers`, `cloudSpeed` cambia la velocidad de la única capa de vanilla en `cloudHeight`. Con `cloudLayers`, cada entrada es una capa propia, y `cloudHeight`, `cloudSpeed` y `cloudColor` completan lo que omite una entrada. `renderClouds: false` sigue sin dibujar ninguna.

`sunBrightness` y `moonBrightness` atenúan el sol y la luna además del desvanecimiento por lluvia de vanilla, tanto en el cielo de vanilla como en el tuyo de [El renderizador del cielo](#el-renderizador-del-cielo).

`heat` extiende un espejismo ondulante sobre la vista mientras el jugador está en un bioma al menos tan cálido como `minTemperature`. Un desierto es 2.0 y las llanuras 0.8. El espejismo aparece y desaparece poco a poco en unos segundos, se mantiene apagado bajo el agua y nunca deforma el ítem en la mano ni el HUD.

`mode` decide dónde se aplica el espejismo. `screen` deforma una franja fija en la parte inferior de la pantalla, mire donde mire el jugador. `world` sigue el terreno: lo que queda a menos de `startDistance` bloques se mantiene nítido, el espejismo va creciendo hacia el borde lejano de la distancia de renderizado, donde se cierra la niebla, y el cielo nunca se ve afectado, tanto si el jugador mira hacia abajo, al frente o hacia arriba.

| Clave                  | Obligatorio | Valor         | Por defecto   | Qué hace |
| ---------------------- | ----------- | ------------- | ------------- | -------- |
| `cloudLayers[].height` | no          | float         | `cloudHeight` | Dónde está la capa |
| `cloudLayers[].speed`  | no          | float         | `cloudSpeed`  | Con qué rapidez se desplaza. `0` la mantiene quieta, un valor negativo la invierte |
| `cloudLayers[].color`  | no          | color hexadecimal | `cloudColor` | Su tinte |
| `heat.strength`        | no          | float, 0 a 1  | `0.1`         | Cuán fuerte es el espejismo |
| `heat.minTemperature`  | no          | float         | `1.5`         | La temperatura de bioma más baja que produce espejismo |
| `heat.dayOnly`         | no          | boolean       | `true`        | Activado, el espejismo se desvanece con la luz del día y desaparece de noche |
| `heat.mode`            | no          | string        | `screen`      | Dónde se aplica el espejismo, `screen` o `world` |
| `heat.startDistance`   | no          | float         | `32`          | En el modo `world`, a cuántos bloques de distancia empieza el espejismo |

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

En 26.x, `waterFogColor` pasa a ser el atributo de entorno `visual/water_fog_color` de la dimensión, así que un bioma que define el suyo, como el pantano, lo conserva. Un JSON de bioma de vanilla con `visual/water_fog_color` en sus `attributes` también funciona.

`starColor` tiñe las estrellas, tanto en el cielo de vanilla como en el tuyo de [El renderizador del cielo](#el-renderizador-del-cielo). `starTwinkle` las hace titilar: las estrellas se reparten en ocho grupos que se apagan y se avivan cada uno a su ritmo, y el valor indica cuánto se apagan; con `1` un grupo desaparece del todo en su punto más bajo.

`lightningColor` tiñe los rayos.

| Clave            | Obligatorio | Valor             | Por defecto  | Qué hace |
| ---------------- | ----------- | ----------------- | ------------ | -------- |
| `snowColor`      | no          | color hexadecimal | blanco       | Tinte de las capas y los bloques de nieve |
| `waterFogColor`  | no          | color hexadecimal | el del bioma | Color de la niebla bajo el agua |
| `lavaFogColor`   | no          | color hexadecimal | `991A00`     | Color de la niebla en la lava |
| `starColor`      | no          | color hexadecimal | blanco       | Tinte de las estrellas |
| `starTwinkle`    | no          | float, 0 a 1      | `0.0`        | Cuánto se apagan las estrellas al titilar. `0` las deja fijas |
| `lightningColor` | no          | color hexadecimal | `737380`     | Tinte de los rayos |

### El bloque `physics`

*dimensiones*

| Clave          | Obligatorio | Valor            | Por defecto            | Qué hace |
| -------------- | ----------- | ---------------- | ---------------------- | -------- |
| `gravity`      | no          | float, mayor que 0 | `1.0`                | Aceleración de caída aquí, como multiplicador de vanilla. `0.17` es como la Luna |
| `fallDamage`   | no          | float, mayor que 0 | `1.0`                | Daño por caída aquí, como multiplicador |
| `arrowGravity` | no          | float, mayor que 0 | sigue a `gravity`    | Con qué rapidez caen aquí las flechas, como multiplicador |

Son los mismos multiplicadores que las claves de plantilla de mundo `worldGravity` y `worldFallDamage`, establecidos en la dimensión. La línea `dimension=value` de una plantilla de mundo para esta dimensión sigue imponiéndose; un valor de plantilla de mundo sin nombre cubre solo las dimensiones que no establecen nada propio.

### El bloque `time`

*dimensiones*

| Clave       | Obligatoria | Valor      | Por defecto | Qué hace                                                                                         |
| ----------- | ----------- | ---------- | ----------- | ------------------------------------------------------------------------------------------------ |
| `dayLength` | no          | int, ticks | `24000`     | Cuánto dura aquí un día con su noche. La fase lunar sigue completando un ciclo cada 24000 ticks |

### El bloque `weather`

*dimensiones*

| Clave                   | Obligatoria | Valor                  | Por defecto              | Qué hace                                                                                                                      |
| ----------------------- | ----------- | ---------------------- | ------------------------ | ----------------------------------------------------------------------------------------------------------------------------- |
| `precipitation`         | no          | boolean                | `true`                   | Desactivada, aquí nunca llueve, nieva ni hay tormentas                                                                        |
| `lightning`             | no          | boolean                | `true`                   | Desactivada, la lluvia y las tormentas llegan sin rayos                                                                       |
| `snow`                  | no          | boolean                | `true`                   | Desactivada, la nieve nunca se acumula                                                                                        |
| `freeze`                | no          | boolean                | `true`                   | Desactivada, el agua nunca se congela                                                                                         |
| `cycle.rainTicks`       | no          | int o `[min, max]`     | `[1000, 4600]`           | Cuánto dura un chubasco                                                                                                       |
| `cycle.clearTicks`      | no          | int o `[min, max]`     | `[1000, 3000]`           | Cuánto dura el periodo seco entre chubascos                                                                                   |
| `cycle.maxStrength`     | no          | float, mayor que 0 hasta 1 | `0.6`                | La mayor intensidad que alcanza un chubasco. Cada chubasco oscila entre una cuarta parte de este valor y su totalidad         |
| `cycle.thunderTicks`    | no          | int o `[min, max]`     | ninguno                  | Cuánto dura una tormenta eléctrica. Sin esta clave, el ciclo nunca genera tormentas                                           |
| `cycle.calmTicks`       | no          | int o `[min, max]`     | `[12000, 180000]`        | Cuánto dura la calma entre tormentas                                                                                          |
| `cycle.thunderStrength` | no          | float, mayor que 0 hasta 1 | `1`                  | Cuánto se oscurece el cielo en una tormenta. Los rayos solo caen por encima de `0.9`                                          |
| `rain.particle`         | no          | id de partícula        | `minecraft:rain`         | Lo que salpica donde cae la lluvia                                                                                            |
| `rain.sound`            | no          | nombre de sonido       | `minecraft:weather.rain` | El sonido de la lluvia                                                                                                        |
| `rain.volume`           | no          | float                  | `0.2`                    | Su volumen, que se reduce a la mitad cuando la lluvia cae justo encima de ti                                                  |
| `rain.interval`         | no          | int                    | `3`                      | Con qué poca frecuencia suena; cuanto mayor, más espaciado, y con `0` suena en cada oportunidad                               |
| `rain.color`            | no          | color hexadecimal      | `#FFFFFF`                | Tinte de la lluvia al caer                                                                                                    |
| `rain.snowColor`        | no          | color hexadecimal      | `#FFFFFF`                | Tinte de la nieve al caer                                                                                                     |
| `rain.angle`            | no          | float, de 0 a 180      | `0`                      | Grados respecto a la vertical descendente: `90` sopla de lado, `180` sube en vertical. Se dibuja inclinada 75 grados como máximo |
| `rain.heading`          | no          | float, grados          | `0`                      | Hacia dónde sopla: `0` sur, `90` oeste, `180` norte, `270` este                                                               |
| `wind.gust`             | no          | float, de 0 a 90       | `15`                     | Grados que una ráfaga suma a `angle` en su punto máximo, sin pasar nunca de la horizontal                                     |
| `wind.every`            | no          | int o `[min, max]`     | `[200, 600]`             | Ticks entre una ráfaga y la siguiente                                                                                         |
| `wind.swing`            | no          | float, de 0 a 180      | `30`                     | Grados que una ráfaga desvía `heading` hacia un lado                                                                          |

Un bloque `wind` vuelve racheada la lluvia. De vez en cuando, una ráfaga la inclina hasta `gust` grados más y desvía su rumbo hasta `swing` grados hacia un lado; crece y se apaga en menos de cuatro segundos, y las ráfagas llegan cada `every` ticks. La lluvia y la nieve se inclinan con ella, y las partículas ambientales de la dimensión derivan hacia donde se inclina la lluvia, haya ráfagas o no. Un bloque `wind` sin bloque `rain` deja la lluvia con sus valores por defecto.

Las demás dimensiones comparten la lluvia de la superficie. Un `cycle` da a esta dimensión un clima propio: los chubascos van y vienen según los tiempos indicados arriba, haga lo que haga la superficie. Con `thunderTicks` también hay tormentas, con tiempos propios; una tormenta que coincide con un chubasco lo lleva a su máxima intensidad, oscurece el cielo y, con `lightning` activado, trae rayos. `weatherCeiling` en una [plantilla de mundo](#plantillas-de-mundo) sigue limitando hasta qué altura llega la lluvia.

Un bloque `rain` cambia el aspecto y el sonido de la lluvia y la nieve aquí, con o sin `cycle`; sin él se ven y suenan como en vanilla.

### El bloque `ambience`

*dimensiones*

| Clave            | Obligatoria | Valor              | Por defecto      | Qué hace                                                                                                                                                                                                                      |
| ---------------- | ----------- | ------------------ | ---------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `music`          | no          | nombre de sonido   | ninguno          | Música que suena aquí en lugar de las pistas habituales, también en creativo. Al llegar, corta la pista que suena                                                                                                             |
| `musicDelay`     | no          | int o `[min, max]` | `[12000, 24000]` | Ticks de silencio entre dos pistas                                                                                                                                                                                            |
| `loopSound`      | no          | nombre de sonido   | ninguno          | Un sonido que se repite en bucle mientras estás aquí; sube al llegar y se apaga al salir                                                                                                                                      |
| `ambientSound`   | no          | nombre de sonido   | ninguno          | Un sonido que se reproduce de vez en cuando, como el sonido adicional (additions) de un bioma                                                                                                                                 |
| `soundChance`    | no          | 0.0 a 1.0          | `0.0111`         | La probabilidad, en cada tick, de que se reproduzca `ambientSound`                                                                                                                                                            |
| `particle`       | no          | id de partícula    | ninguno          | Una partícula que flota en el aire a tu alrededor, como `minecraft:ash`, `minecraft:white_ash`, `minecraft:crimson_spore` o `minecraft:dust`                                                                                  |
| `particleChance` | no          | 0.0 a 1.0          | `0.00625`        | Su densidad, contada como en los biomas modernos: en cada tick se prueban unos 667 puntos a menos de 16 bloques y otros 667 a menos de 32, y cada uno que no es un bloque completo muestra la partícula con esta probabilidad |
| `particleColor`  | no          | color hex          | ninguno          | El tinte de una partícula que lo admite: `minecraft:dust` y `minecraft:entity_effect`                                                                                                                                         |

Un bloque `ambience` da a la dimensión música, sonidos y partículas flotantes propios. Las claves pasan a ser los atributos de entorno de la dimensión, `minecraft:audio/background_music`, `minecraft:audio/ambient_sounds` y `minecraft:visual/ambient_particles`, puestos por encima de sus biomas, así que suenan y se ven como los de vanilla, y el ajuste «Partículas» las reduce del mismo modo. Si falta una clave, cada bioma conserva lo suyo, así que un JSON de bioma vanilla con sus atributos también funciona.

## Portales y puertas dimensionales

*el mundo*

`<namespace>/blocks/*.json`

Un portal es una definición de bloque corriente, así que se aplica la misma regla de rutas y cada variante es un bloque de portal.

Un bloque `portal` lleva una sección `portal`:

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

| Clave             | Obligatoria | Valor        | Por defecto            | Qué hace                                                                                                       |
| ----------------- | ----------- | ------------ | ---------------------- | -------------------------------------------------------------------------------------------------------------- |
| `dimension`       | sí          | id de dimensión |                     | A dónde te envía                                                                                               |
| `returnDimension` | no          | id de dimensión | `minecraft:overworld` | A dónde te devuelve                                                                                            |
| `gate`            | no          | nombre de puerta | ninguno             | Una puerta que debe estar abierta para pasar                                                                   |
| `cooldown`        | no          | int, ticks   | `60`                   | Tiempo antes de que el mismo jugador pueda volver a usarlo                                                     |
| `platform`        | no          | boolean      | `true`                 | Construye una plataforma de aterrizaje al llegar                                                               |
| `platformBlock`   | no          | nombre de bloque | el marco del propio portal | De qué está hecha esa plataforma                                                                          |
| `sound`           | no          | nombre de sonido | ninguno            | Suena al pasar. Consulta los [nombres de sonido](#listas-de-valores)                                           |
| `owned`           | no          | boolean      | `true`                 | Solo puede usarlo quien lo construyó y quienes este autorice. Un portal con propietario también es inmune a las explosiones |
| `walkIn`          | no          | boolean      | `false`                | Entrar caminando en el bloque te transporta, como en un portal del Nether. Desactivada, se usa a mano          |

### Marcos de portal

*portales y puertas dimensionales*

`<namespace>/portalframes/*.json`

La ruta del archivo es el nombre de registro del marco, que luego una dimensión nombra en `frames`.

Un marco es un dibujo de lo que el jugador tiene que construir, y nada más: indica qué bloques forman el borde y dónde está el hueco, y no dice nada de adónde lleva el portal. Es deliberado, porque una dimensión reclama un marco en lugar de poseerlo, y dos dimensiones pueden reclamar el mismo.

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

| Clave       | Obligatoria | Valor                              | Por defecto          | Qué hace                                                                                                      |
| ----------- | ----------- | ---------------------------------- | -------------------- | ------------------------------------------------------------------------------------------------------------- |
| `name`      | no          | string                             | el nombre del archivo | El nombre que se usa en el registro                                                                          |
| `axis`      | no          | `vertical`, `horizontal` o `both`  | `vertical`           | Si se alza como un portal del Nether, yace plano como un portal del End, o puede ser de cualquiera de las dos formas |
| `legend`    | sí          | objeto de un carácter a un bloque  | ninguno              | Los bloques que pueden usar las filas. Un nombre de bloque con estados se interpreta igual que en cualquier otro sitio |
| `rows`      | sí          | lista de strings                   | ninguno              | El dibujo, empezando por la fila superior                                                                     |
| `maxWidth`  | no          | int                                | `21`                 | El hueco más ancho al que puede extenderse un `*`                                                             |
| `maxHeight` | no          | int                                | `21`                 | El hueco más alto al que puede extenderse un `*`                                                              |

Tres caracteres no son bloques. `.` es el hueco donde se sitúa el portal, y un marco sin él se rechaza. Un espacio es una celda que al marco le da igual, así que un contorno en forma de L se dibuja dejando las esquinas en blanco. `*` repite: una fila formada solo por `*` repite la fila anterior tantas veces como haya construido el jugador, y un `*` dentro de una fila repite del mismo modo el carácter que lo precede. Puede repetirse cero veces, de modo que el dibujo leído tachando todos los `*` es lo más pequeño que se encenderá, y los máximos de arriba son lo más grande. Un dibujo sin ningún `*` es exacto, y el jugador debe construir eso y nada más.

Un marco vertical se encuentra en cualquiera de los dos ejes horizontales y en cualquier sentido, así que da igual hacia dónde mirara quien lo construyó. Uno horizontal se encuentra en los cuatro giros.

**El tamaño máximo lo decide el pack.** `maxWidth` y `maxHeight` son el hueco más grande al que se extenderá un `*`, y se acepta cualquier tamaño menor hasta el mínimo, de modo que un pack decide si su portal llega como máximo a los 21 de vanilla o a 4. El mínimo es un jugador: un marco vertical se rechaza salvo que su hueco pueda tener al menos 1 de ancho y 2 de alto, y uno horizontal al menos 1 por 1; un dibujo que nunca puede alcanzarlo se rechaza al cargar con una línea en el registro, en lugar de ser un marco que nadie puede atravesar.

**Un marco cuesta más de buscar cuanto más puede estirarse.** Si hay tanto un `*` de fila como un `*` de columna, se prueban todas las combinaciones hasta los dos máximos, así que un marco que se estira en ambos sentidos hasta 21 son 441 dibujos. La búsqueda se rinde en lugar de colgarse y lo indica en el registro, que es la señal para bajar un máximo o quitar uno de los estiramientos.

**Nada impide que un marco sea de obsidiana encendido con pedernal y acero, pero tiene prioridad.** Un marco se busca antes de que el objeto haga su propio trabajo, así que ese marco abre la dimensión del pack donde habría estado un portal del Nether. Elige otro bloque u otro encendedor para dejar en paz el portal de vanilla.

### Abrir una dimensión con un marco

*portales y puertas dimensionales*

`<namespace>/dimensions/*.json`

Una dimensión se abre mediante un marco si lleva una sección `portal`. El marco y lo que lo enciende, juntos, son lo que elige la dimensión, así que una misma forma de marco puede llevar a varios sitios según con qué se encendió.

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

| Clave           | Obligatoria | Valor                       | Por defecto                 | Qué hace                                                                    |
| --------------- | ----------- | --------------------------- | --------------------------- | --------------------------------------------------------------------------- |
| `frames`        | sí          | lista de nombres de marco   | ninguno                     | Los marcos que abren esta dimensión                                         |
| `ignitedBy`     | no          | nombre de objeto            | `minecraft:flint_and_steel` | Lo que sostiene un jugador para encender uno                                |
| `color`         | no          | color hexadecimal           | blanco                      | El color con el que se dibuja el portal                                     |
| `return`        | no          | `built`, `player` o `none`  | `built`                     | Si se ofrece un camino de vuelta, lo construye el jugador, o no hay ninguno |
| `gate`          | no          | nombre de puerta            | ninguno                     | Una puerta que debe estar abierta para pasar                                |
| `cooldown`      | no          | int, ticks                  | `60`                        | Tiempo antes de que el mismo jugador pueda volver a pasar                   |
| `platform`      | no          | boolean                     | `true`                      | Construye una plataforma de aterrizaje al llegar                            |
| `platformBlock` | no          | nombre de bloque            | piedra                      | De qué está hecha esa plataforma                                            |
| `sound`         | no          | nombre de sonido            | ninguno                     | Suena al pasar. Consulta los [nombres de sonido](#listas-de-valores)        |
| `owned`         | no          | boolean                     | `false`                     | Solo puede usarlo quien lo encendió y quienes este autorice                 |

El bloque que ocupa el hueco no lo escribe el pack. A una dimensión con una sección `portal` se le da uno propio, dibujado con la textura de portal del propio juego bajo `color`, que se atraviesa caminando en lugar de usarse a mano y que es irrompible. El color multiplica la textura, igual que un `tintindex`, de modo que `#C77DFF` conserva el violeta del Nether y `#4CFFB0` lo vuelve venenoso. Para un portal que no use en absoluto la textura de vanilla, escribe un bloque `portal` corriente propio con su propia textura, dibujada como [mapa de píxeles](#texturas-escritas-como-mapas-de-píxeles) si quieres, donde `tint` puede degradar entre dos colores.

`return` decide qué ocurre al otro lado. `built` levanta el mismo marco, del tamaño que construyó el jugador, y lo enciende, que es como se comporta vanilla. `player` no construye nada pero permite encender allí el mismo marco, de modo que el camino de vuelta hay que encontrarlo y hacerlo. `none` se niega por completo a encender el marco en esa dimensión, y el viaje es solo de ida.

**Un marco, varias dimensiones.** El par formado por un marco y el objeto que lo enciende es lo que elige la dimensión, así que el mismo `standing_gate` encendido con pedernal y acero y encendido con un encendedor propio del pack abre dos lugares distintos, cada uno con su color. Que dos dimensiones reclamen el mismo marco *y* el mismo objeto es un error del pack: la segunda se rechaza y lo dice en el registro, en lugar de que una de ellas gane en silencio.

Romper cualquier bloque del marco apaga el portal, como en vanilla.

### Portales

*portales y puertas dimensionales*

`<namespace>/gates/*.json`

La ruta del archivo es el nombre de registro de la puerta, que luego un portal nombra en `gate`.

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita.

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

| Clave             | Obligatoria | Valor                                  | Por defecto                      | Qué hace                                                                                                                                                                                                  |
| ----------------- | ----------- | -------------------------------------- | -------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `dimension`       | sí          | id de dimensión                        |                                  | La dimensión que protege                                                                                                                                                                                  |
| `name`            | no          | string                                 | el nombre del archivo            | Se muestra al jugador                                                                                                                                                                                     |
| `scope`           | no          | `player`, `global`                     | `player`                         | Un jugador cada vez, o todo el mundo a la vez                                                                                                                                                             |
| `open`            | no          | boolean                                | `false`                          | Si empieza abierta                                                                                                                                                                                        |
| `unlock`          | no          | objeto                                 |                                  | Lo que la abre. Ver abajo                                                                                                                                                                                 |
| `unlockedMessage` | no          | string                                 | `%dim% is now open`              | Se muestra cuando se abre                                                                                                                                                                                 |
| `blockedMessage`  | no          | string                                 | `You need %item% to enter %dim%` | Se muestra cuando la puerta rechaza el paso                                                                                                                                                               |
| `safeReturn`      | no          | boolean                                | `false`                          | Un jugador al que se hace volver se deja en un lugar seguro del mundo que intentó abandonar: junto a su cama o ancla de reaparición cargada allí si siguen en pie, y si no, en el punto de aparición de ese mundo |
| `requires`        | no          | lista de ids de mod o espacios de nombres de pack | ninguno               | La puerta se omite salvo que estén presentes todos                                                                                                                                                        |
| `portalBlocks`    | no          | lista de nombres de bloque             | todos los portales               | Limita la puerta a estos bloques de portal, de modo que una dimensión pueda tener una entrada protegida y otra abierta                                                                                    |

`unlock` admite `hold` (un objeto que se debe sostener), `consume` con `consumeCount` (`1`), `craft` (un objeto que debe haberse fabricado), `advancement` y `killed` (un nombre de entidad; la puerta se abre para quien mate una, de modo que un jefe puede guardar la llave de un mundo) con `killedCount` (`1`) cuando una no basta, contabilizado por jugador o para todo el mundo según indique el ámbito. Añadir `killedDrops` (un nombre de objeto) hace que las muertes contadas dejen ese objeto a los pies de quien mató en lugar de abrir la puerta, y reinicia la cuenta, de modo que una llave puede volver a ganarse y entregarse a alguien que nunca peleó por ella; condiciona la puerta a `hold` o `consume` de ese mismo objeto para convertirlo en la llave. `%item%`, `%mob%` y `%dim%` se rellenan por ti. Una llave que suelta un mob no necesita nada especial aquí: haz que el mob suelte el objeto y condiciona la puerta a `hold` o `consume`.

Las puertas también protegen las dimensiones del propio juego: una puerta cuya `dimension` sea `minecraft:the_nether` se interpone ante todos los portales del Nether.

## El mundo profundo

*el mundo*

La superficie puede ser más alta o más profunda de lo que el juego permite, y el espacio que se abre bajo el terreno se llena con una generación propia. Lo hacen cuatro claves de `terrain`, en el bloque `settings` de una plantilla de mundo como las demás; una dimensión de pack hace lo mismo con `minHeight` y `maxHeight` en su propio `terrain`.

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

| Clave            | Valor                                                                  | Por defecto | Qué hace                                                                                                                                                                                                                                                                                                                                                                                              |
| ---------------- | ---------------------------------------------------------------------- | ----------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `worldMinHeight` | int, múltiplo de 16, hasta -2032                                       | `-64`       | El bloque más bajo de la superficie. El fondo del propio juego es -64; un valor menor crea un mundo profundo bajo el terreno de vanilla, de piedra maciza hasta que la capa de worldgen la excava o `noiseCaves` lleva las cuevas del juego hacia abajo. Solo se aplica mediante el preset generado, así que un mundo creado antes del pack conserva su altura                                          |
| `worldMaxHeight` | int, múltiplo de 16, hasta 2032 y como máximo 4064 sobre el fondo      | `320`       | El bloque por encima del techo de la superficie. El techo del propio juego es 320; un valor mayor deja cielo abierto sobre el terreno de vanilla                                                                                                                                                                                                                                                      |
| `deepStone`      | nombre de bloque                                                       | ninguno     | El bloque del que está hecho el mundo bajo el terreno de vanilla cuando el fondo baja de -64, como la propia pizarra abismal de un pack. Se funde con la pizarra abismal a lo largo de las ocho capas bajo -64, igual que la pizarra abismal se funde con la piedra. Vacío conserva la piedra                                                                                                                 |
| `noiseCaves`     | `off`, `deep` o `world`                                                | `off`       | Hasta dónde continúan las cuevas, túneles, tallarines y acuíferos del juego cuando el fondo baja de -64: `off` mantiene el mundo bajo el terreno de vanilla como piedra profunda maciza para que la capa de worldgen la excave, `deep` los lleva hasta el fondo con los lagos de lava trasladados a sus diez capas inferiores, `world` significa lo mismo en esta versión porque el terreno de vanilla ya los tiene |

El mundo profundo es donde hacen su trabajo las entradas de worldgen, las regiones de cuevas y los grupos de dureza de un pack: `minHeight` y `maxHeight` en una entrada llegan tan abajo como llegue el fondo. Las características colocadas del propio juego se ciñen al terreno de vanilla: una cuya altura se cuenta desde el fondo del mundo, entre ellas los diamantes y la redstone inferior del juego, sigue contando desde -64, y una colocación que quedaría por debajo de -64 se omite, como ocurre en un mundo cuyo fondo es -64. Las claves de cielo de 1.12.2, `deepRavines`, `oreVeins`, `terrainOffset` y el propio mundo rubic no tienen equivalente aquí, ya que la generación de este motor ya abarca desde el fondo hasta el techo.

## Regiones de cuevas

*el mundo*

`<namespace>/caveregions/*.json`

La ruta del archivo es el nombre de la región, que luego una entrada de worldgen nombra en `caveRegions`. Un nombre sin espacio de nombres allí toma el de esa entrada.

Pinta regiones con nombre sobre el subsuelo, la contrapartida en packs de los biomas de cueva del juego. El subsuelo se divide en celdas redondeadas, de `caveRegionCells` bloques de ancho y `caveRegionCellsY` de alto, ambas claves de `terrain`, y cada celda sortea una región, o ninguna, según su peso. Todo lo que hace una región sale de forma determinista de la semilla, de modo que los chunks coinciden entre sí sin escribir nunca a través de un borde.

### Archivos de región

*regiones de cuevas*

Todas las claves, mostradas a la vez. Un archivo real escribe solo las que necesita.

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

| Clave               | Valor                                  | Por defecto                                                                                          | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ------------------- | -------------------------------------- | ---------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `weight`            | int                                    | `1`                                                                                                  | Proporción de celdas que gana esta región. `0` la desactiva                                                                                                                                                                                                                                                                                                                                                                    |
| `minHeight`         | int                                    | el fondo del mundo                                                                                   | Límite inferior de la franja en la que existe la región                                                                                                                                                                                                                                                                                                                                                                        |
| `maxHeight`         | int                                    | `48`                                                                                                 | Límite superior de esa franja. Una celda cuyo centro quede fuera de la franja nunca elige la región                                                                                                                                                                                                                                                                                                                            |
| `waterLevel`        | int                                    | ninguno                                                                                              | Fija el nivel freático dentro de la región a esta altura, en lugar de los acuíferos de allí. Se mantiene por debajo del nivel del mar y al menos dos bloques por encima de la lava profunda                                                                                                                                                                                                                                      |
| `dimensions`        | lista de ids de dimensión              | todas                                                                                                | En qué dimensiones aparece la región, incluidas las propias de un pack. Una región con `biome` solo lo muestra donde los biomas se colocan por clima: la superficie, el Nether y una dimensión de pack que hereda los biomas de la superficie                                                                                                                                                                                  |
| `floorCover`        | bloque                                 | ninguno                                                                                              | Sustituye el bloque superior de los suelos de cueva dentro de la región                                                                                                                                                                                                                                                                                                                                                        |
| `floorChance`       | 0.0 a 1.0                              | `1.0`                                                                                                | Qué parte del suelo queda cubierta                                                                                                                                                                                                                                                                                                                                                                                             |
| `ceilingCover`      | bloque                                 | ninguno                                                                                              | Sustituye los bloques del techo de cueva dentro de la región                                                                                                                                                                                                                                                                                                                                                                   |
| `ceilingChance`     | 0.0 a 1.0                              | `1.0`                                                                                                | Qué parte del techo                                                                                                                                                                                                                                                                                                                                                                                                            |
| `coverReplace`      | lista de bloques                       | los bloques base del juego de piedra, mena, adoquín, arenisca, terracota, piedra del End y obsidiana | Lo que pueden sustituir las coberturas. Un bloque de la lista solo coincide consigo mismo: `minecraft:stone` no cubre también andesita, pizarra abismal ni toba, así que enumera todas las piedras que pueda tener el suelo                                                                                                                                                                                                      |
| `spawns`            | lista                                  | ninguno                                                                                              | Mobs que aparecen dentro de la región, las mismas entradas que admite el `spawns` de un bioma: `entity`, `type` (monster, creature, ambient o water), `weight` (`8`), `min` (`1`) y `max` (`4`) para el tamaño del grupo. Un punto desde el que se ve el cielo se deja al bioma, igual que las coberturas                                                                                                                         |
| `keepDefaultSpawns` | boolean                                | `false`                                                                                              | Conserva la lista de apariciones del propio bioma junto a la de la región. Desactivada, la lista de la región la sustituye por completo dentro de la región                                                                                                                                                                                                                                                                     |
| `structures`        | lista                                  | ninguno                                                                                              | Una estructura colocada una vez por celda de región, en el corazón de la celda, ajustada al suelo de una cueva, como el juego da su hito a un bioma de cueva. Las entradas son plantillas `namespace:name`, o `{ "structure": "...", "weight": 3 }` para elegir entre varias                                                                                                                                                  |
| `structureChance`   | 0.0 a 1.0                              | `1.0`                                                                                                | La probabilidad de que cada celda de la región reciba realmente su estructura                                                                                                                                                                                                                                                                                                                                                  |
| `structureLoot`     | `namespace:path`                       | ninguno                                                                                              | La tabla de botín con la que se llena cada cofre de una estructura colocada la primera vez que se abre                                                                                                                                                                                                                                                                                                                          |
| `biome`             | nombre de bioma                        | ninguno                                                                                              | El bioma que la región informa dentro de su volumen, escrito como bioma 3D. Da a la región su propio color de follaje, hierba y agua, música y sonidos ambientales, y permite que la ponderación de apariciones de vanilla lo lea. La superficie de arriba no se toca, ya que solo se escriben las celdas que ocupa la región. Si se omite, la región conserva el bioma que la rodea y aun así coloca sus coberturas, estructuras y apariciones |
| `requires`          | lista de ids de mod o espacios de nombres de pack | ninguno                                                                                   | La región se omite salvo que estén presentes todos                                                                                                                                                                                                                                                                                                                                                                             |
| `ambientSound`      | nombre de sonido                       | ninguno                                                                                              | Un sonido que se reproduce de vez en cuando para un jugador dentro de la región, como los biomas del juego añaden sus propios sonidos de cueva. El servidor lo envía solo a ese jugador                                                                                                                                                                                                                                          |
| `soundChance`       | 0.0 a 1.0                              | `0.0111`                                                                                             | La probabilidad por tick de que suene `ambientSound`                                                                                                                                                                                                                                                                                                                                                                           |
| `particle`          | nombre de partícula                    | ninguno                                                                                              | Una partícula que se muestra alrededor de un jugador dentro de la región, una de las partículas del juego que no admiten ajustes propios, como `minecraft:dripping_water`, `minecraft:happy_villager` o `minecraft:underwater`. Un nombre de 1.12.2 como `dripWater` se convierte junto con su pack. Solo la muestra el aire dentro de la región                                                                                  |
| `particleChance`    | 0.0 a 1.0                              | `0.00625`                                                                                            | La densidad de partículas propia de los biomas: en cada tick se prueban unos 667 puntos a menos de 16 bloques, y cada uno muestra la partícula con esta probabilidad                                                                                                                                                                                                                                                            |

### Celdas

*regiones de cuevas*

| Ajuste                  | Tipo           | Por defecto | Qué hace                                                                                                                                                                                                                  |
| ----------------------- | -------------- | ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `caveRegionCells`       | int, bloques   | `128`       | Cuánto mide de ancho una celda de región                                                                                                                                                                                  |
| `caveRegionCellsY`      | int, bloques   | `64`        | Cuánto mide de alto una celda de región                                                                                                                                                                                   |
| `caveRegionPlainWeight` | int            | `4`         | El peso del subsuelo llano, sin región, en el sorteo de cada celda. Un valor mayor deja más subsuelo sin ninguna región: con una sola región de peso 1, aproximadamente una quinta parte de las celdas la reciben |

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

Cuánto subsuelo permanece llano lo decide la clave de `terrain` `caveRegionPlainWeight`, por defecto `4`: con una sola región de peso 1, aproximadamente una quinta parte de las celdas reciben la región. Las coberturas se aplican bajo un techo, así que una región que llegue por encima del suelo nunca se ve en la superficie. Las coberturas funcionan en todas las cuevas, sea cual sea el generador que las excavó.

### Características en una región

*regiones de cuevas*

Las características se integran mediante dos claves de las [entradas de worldgen](#entradas-de-worldgen) corrientes. `caveRegions` enumera las regiones en las que puede generarse una entrada, comprobado en la posición colocada, de modo que setas, cristales o cualquier otra cosa aparecen solo dentro de su región. `snap` primero mueve cada intento en vertical hasta la superficie de cueva más cercana: `floor` para lo que se apoya, `ceiling` para lo que cuelga. Una región al estilo de la espeleotema no necesita formas nuevas:

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

El `replace` de `minecraft:air` importa: aquello sobre lo que escribe una forma colocada se comprueba con `replace`, cuyo valor por defecto es piedra, así que todo lo que se construya en el espacio abierto de una cueva necesita que se enumere el aire. La misma entrada con `"snap": "floor"` y sin `hanging` hace crecer las estalagmitas a juego. El filtro de región funciona con todas las formas colocadas; `belt` y `field` se colocan según sus propias reglas y lo ignoran.

---

# Generación del mundo

## Entradas de worldgen

*generación del mundo*

`<namespace>/worldgen/*.json`

La ruta del archivo da nombre a la entrada, y las formas `belt`, `field` y `vein` siembran su ruido a partir de ella, de modo que renombrar un archivo cambia lo que genera.

Describe algo que se genera. Cada entrada es una **forma** colocada mediante una **dispersión**, filtrada por dónde se le permite estar.

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

Solo `block` es obligatoria; todo lo demás puede omitirse y toma su valor por defecto. `blocks` sustituye a `block` cuando uno no basta y tiene su propio ejemplo más abajo.

### Qué coloca

*entradas de worldgen*

| Clave      | Obligatoria | Valor                                | Por defecto             | Qué hace                                                                                                    |
| ---------- | ----------- | ------------------------------------ | ----------------------- | ----------------------------------------------------------------------------------------------------------- |
| `block`    | sí          | nombre de bloque                     |                         | Lo que se coloca                                                                                            |
| `blocks`   | no          | lista de objetos                     | ninguno                 | Una lista con pesos, que se usa en lugar de un solo bloque. Ver abajo                                       |
| `size`     | no          | int o rango                          | `8`                     | Cuántos bloques coloca un intento, o cómo de grande es una forma con radio                                  |
| `attempts` | no          | int o rango                          | `8`                     | Cuántas veces lo intenta por chunk                                                                          |
| `replace`  | no          | lista de nombres de bloque u objetos | `["minecraft:stone"]`   | Lo que puede sustituir. Ver abajo                                                                           |
| `adjacent` | no          | lista de nombres de bloque u objetos | ninguno                 | Solo coloca donde uno de estos esté entre los 26 bloques que tocan el punto. Mismas formas que `replace`    |
| `sparse`   | no          | boolean                              | `false`                 | Dispersa los bloques en lugar de apretarlos juntos                                                          |
| `shape`    | no          | objeto                               | `{ "type": "cluster" }` | La forma que adopta. Consulta [Formas](#formas)                                                             |
| `spread`   | no          | objeto                               | `{ "type": "even" }`    | Dónde se coloca. Consulta [Dispersiones](#dispersiones)                                                     |

### Dónde puede generarse

*entradas de worldgen*

| Clave                    | Obligatoria | Valor                     | Por defecto        | Qué hace                                                                                                                                                                                                                                                                                                                                                                      |
| ------------------------ | ----------- | ------------------------- | ------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `minHeight`              | no          | int                       | `0`                | La y más baja en la que colocará                                                                                                                                                                                                                                                                                                                                              |
| `maxHeight`              | no          | int                       | `64`               | La y más alta en la que colocará                                                                                                                                                                                                                                                                                                                                              |
| `dimensions`             | no          | lista de ids de dimensión | todas las dimensiones | En qué dimensiones se ejecuta                                                                                                                                                                                                                                                                                                                                              |
| `dimensionsAreBlacklist` | no          | boolean                   | `false`            | Convierte esa lista en la de las que hay que evitar                                                                                                                                                                                                                                                                                                                           |
| `biomes`                 | no          | lista de nombres de bioma | todos los biomas   | En qué biomas se ejecuta                                                                                                                                                                                                                                                                                                                                                      |
| `biomeTypes`             | no          | lista de tipos de bioma   | ninguno            | Biomas por palabra de tipo, como `forest` o `nether`                                                                                                                                                                                                                                                                                                                          |
| `biomesAreBlacklist`     | no          | boolean                   | `false`            | Convierte esas listas en las que hay que evitar                                                                                                                                                                                                                                                                                                                               |
| `minTemperature`         | no          | float                     | `-100.0`           | El bioma más frío en el que se generará                                                                                                                                                                                                                                                                                                                                       |
| `maxTemperature`         | no          | float                     | `100.0`            | El bioma más cálido en el que se generará                                                                                                                                                                                                                                                                                                                                     |
| `minRainfall`            | no          | float                     | `-100.0`           | El bioma más seco en el que se generará                                                                                                                                                                                                                                                                                                                                       |
| `maxRainfall`            | no          | float                     | `100.0`            | El bioma más húmedo en el que se generará                                                                                                                                                                                                                                                                                                                                     |
| `minDistanceFromSpawn`   | no          | int, bloques              | `0`                | A qué distancia del punto de aparición del mundo empieza a generarse                                                                                                                                                                                                                                                                                                          |
| `caveRegions`            | no          | lista de nombres de región | ninguno           | Solo se genera dentro de estas [regiones de cuevas](#regiones-de-cuevas)                                                                                                                                                                                                                                                                                                      |
| `snap`                   | no          | `floor` o `ceiling`       | ninguno            | Antes mueve cada intento en vertical hasta el suelo o techo de cueva más cercano                                                                                                                                                                                                                                                                                              |
| `snapDepth`              | no          | int                       | `0`                | Cuánto más allá de la superficie se mueve `snap`, hacia abajo desde un suelo y hacia arriba desde un techo. `0` se queda en el espacio abierto contra la superficie, `1` es el propio bloque de la superficie, `2` el que hay detrás. Lo que puede sobrescribir sigue rigiéndose por `replace`, así que así es como un pack coloca un bloque en bandas justo bajo el suelo en lugar de encima |

### Señales de superficie y seguidores

*entradas de worldgen*

| Clave             | Obligatoria | Valor                                | Por defecto             | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ----------------- | ----------- | ------------------------------------ | ----------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `indicators`      | no          | lista de `block=weight`              | ninguno                 | Bloques dejados dispersos en la superficie sobre una veta que se generó, para que un jugador pueda saber qué hay bajo tierra; elígelos para que coincidan con el contenido de la veta. `empty=weight` deja un punto vacío. Una entrada sin peso, o con un peso menor que 1, se anota en el registro y se omite, y no se deja ninguna en las calles y edificios de una aldea o una ciudad                                                                                                                                                                                                                                                                                                                                                                                                       |
| `indicatorCount`  | no          | int o rango                          | `1`                     | Cuántos puntos de superficie recibe cada veta generada                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `indicatorSpread` | no          | int, bloques                         | `0`                     | Cuánto más allá de la huella de la veta puede caer un indicador                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `then`            | no          | lista de `name=weight` u objetos     | ninguno                 | Entradas de worldgen que crecen a partir de esta justo después de generarse, unidas a ella: el origen del seguidor se fija justo fuera del borde de esta veta, en la dirección que indican `thenSpread` y `thenDepth`, de modo que ambas se tocan. Una entrada es `name=weight`, o un objeto con `name`, `weight` y sus propios `spread` y `depth` (int o rango) que sustituyen los de la veta solo para ese seguidor, de modo que una lista puede enviar una punta de diamante hacia abajo y una rama hacia un lado. Un nombre sin espacio de nombres se lee en el de este pack, `empty=weight` no pone nada en cola. Un seguidor conserva su propia forma, bloques, tamaño y `replace` pero se salta sus propios intentos, probabilidad, franja de altura y filtros de bioma, y puede llevar él mismo `then`, tan profundo como quiera el pack; una entrada que ya se generó en la misma cadena la detiene |
| `thenCount`       | no          | int o rango                          | `1`                     | Cuántos seguidores distintos se eligen de esa lista por cada veta generada, cada entrada como mucho una vez, de modo que un número igual a la longitud de la lista hace crecer a todos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `thenSpread`      | no          | int, bloques                         | el radio de la forma    | Cuánto puede inclinarse hacia los lados la dirección en la que crece un seguidor, sorteado de menos este valor a más este valor                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `thenDepth`       | no          | int o rango                          | `0`                     | Cuánto se inclina la dirección hacia abajo (negativo) o hacia arriba. `0` sin inclinación lateral hace que el seguidor cuelgue recto hacia abajo                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `prospectAs`      | no          | string                               | el nombre del archivo   | Cómo nombra esta entrada un objeto de prospección en su lectura, p. ej. `Hematite`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |

### Retrogen y requisitos

*entradas de worldgen*

| Clave         | Obligatoria | Valor                                              | Por defecto            | Qué hace                                               |
| ------------- | ----------- | -------------------------------------------------- | ---------------------- | ------------------------------------------------------ |
| `retrogen`    | no          | boolean                                            | `false`                | También genera en chunks que ya existen                |
| `retrogenKey` | no          | string                                             | la clave de la config  | Sustituye la clave de retrogen solo para esta entrada  |
| `requires`    | no          | lista de ids de mod o espacios de nombres de pack  | ninguno                | La entrada se omite salvo que estén presentes todos    |

### Bloques con peso

*entradas de worldgen*

`blocks` sustituye a `block` cuando una sola entrada no basta. Los pesos son relativos, así que 80 y 20 es cuatro a uno.

```json
{
  "blocks": [
    { "block": "minecraft:magenta_wool", "weight": 80 },
    { "block": "minecraft:oak_log", "weight": 20, "properties": { "axis": "x" } }
  ]
}
```

| Clave        | Obligatoria | Valor                            | Por defecto | Qué hace                                                                                  |
| ------------ | ----------- | -------------------------------- | ----------- | ----------------------------------------------------------------------------------------- |
| `block`      | sí          | nombre de bloque                 |             | Lo que se coloca                                                                          |
| `weight`     | no          | int                              | `1`         | Con qué frecuencia se elige este frente a los demás                                       |
| `properties` | no          | objeto de propiedad a valor      | ninguno     | Propiedades del estado del bloque por nombre, para un estado distinto del predeterminado del bloque |

`block` sigue siendo obligatorio en el nivel superior del archivo incluso cuando se usa `blocks`; la primera entrada es un buen valor para poner ahí.

### Objetivos de sustitución

*entradas de worldgen*

`replace` es una lista, y cada entrada admite una de dos formas.

```json
{
  "replace": [
    "minecraft:stone",
    { "block": "minecraft:oak_log", "properties": { "axis": "y" } }
  ]
}
```

| Forma  | Ejemplo                                                           | Con qué coincide              |
| ------ | ----------------------------------------------------------------- | ----------------------------- |
| Nombre | `"minecraft:stone"`                                               | Todos los estados de ese bloque |
| Objeto | `{ "block": "minecraft:oak_log", "properties": { "axis": "y" } }` | Solo ese estado               |

Un nombre de 1.12.2 con metadatos al final, `minecraft:stone:3`, coincide con todos los estados del bloque y lo dice en el registro, ya que los bloques que llevaban metadatos son ahora bloques distintos: escribe `minecraft:diorite`. Usa `"minecraft:air"` para generar en espacio abierto.

### Bloques adyacentes

*entradas de worldgen*

`adjacent` admite las mismas formas que `replace` y añade una segunda condición además de esta: el punto solo se usa cuando al menos uno de los 26 bloques que lo tocan, caras, aristas y esquinas, coincide con la lista. Si se omite, no se comprueba nada.

```json
{
  "block": "mypack:sulfur_ore",
  "replace": ["minecraft:sandstone"],
  "adjacent": ["minecraft:air"]
}
```

Eso coloca azufre en la arenisca solo donde ya está abierta a una cueva o a la superficie, y deja en paz la arenisca enterrada. Los vecinos en chunks que aún no existen se tratan como si no coincidieran en lugar de leerse, de modo que la comprobación nunca provoca que se genere un chunk.

Todas las formas lo respetan, ya que forma parte de decidir si se puede tomar un solo bloque. Un `geode` nombra su corteza y su relleno por separado, y esos dos se colocan sin la comprobación.

Una entrada que nombra solo bloques no registrados se omite con un error en lugar de generarse en todas partes.

### Entradas de seguidores

*entradas de worldgen*

Una entrada de la lista `then` de una entrada de worldgen es un nombre con un peso, o un objeto cuando ese seguidor necesita una dirección propia.

```json
{
  "then": [
    "mypack:quartz_halo=2",
    "empty=1",
    { "name": "mypack:side_branch", "weight": 1, "spread": 4, "depth": 0 }
  ]
}
```

| Clave    | Obligatoria | Valor          | Por defecto                 | Qué hace                                                                                                          |
| -------- | ----------- | -------------- | --------------------------- | ----------------------------------------------------------------------------------------------------------------- |
| `name`   | sí          | nombre de entrada |                          | La entrada de worldgen que crece a partir de esta. Un nombre sin espacio de nombres se lee en el de este pack     |
| `weight` | no          | int            | `1`                         | Con qué frecuencia se elige este seguidor frente a los demás de la lista                                          |
| `spread` | no          | int, bloques   | el `thenSpread` de la entrada | Cuánto puede inclinarse hacia los lados la dirección de este seguidor, solo para esta entrada                   |
| `depth`  | no          | int o rango    | el `thenDepth` de la entrada | Cuánto se inclina hacia abajo, en negativo, o hacia arriba la dirección de este seguidor, solo para esta entrada |

`name=weight` es la forma abreviada de un objeto con solo esos dos, y `empty=weight` no pone nada en cola. Como `spread` y `depth` son por entrada, una lista puede enviar una punta de diamante recta hacia abajo y una rama hacia un lado desde la misma veta.

## Formas

*generación del mundo*

Un bloque `shape` con un `type`. Las claves que no figuran para un tipo las ignora.

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

| Tipo         | Qué crea                                                                                                                                                                                                                                                                                                                                                                                                         |
| ------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `cluster`    | La masa por defecto, una veta de mena. Usa `size`                                                                                                                                                                                                                                                                                                                                                                |
| `largevein`  | Una veta larga y serpenteante con ramas. Usa `size`                                                                                                                                                                                                                                                                                                                                                              |
| `plate`      | Un disco plano                                                                                                                                                                                                                                                                                                                                                                                                   |
| `geode`      | Una bolsa hueca con corteza                                                                                                                                                                                                                                                                                                                                                                                      |
| `decoration` | Dispersión en la superficie, como flores o setas. Usa `size`                                                                                                                                                                                                                                                                                                                                                     |
| `tree`       | Un árbol entero                                                                                                                                                                                                                                                                                                                                                                                                  |
| `vines`      | Enredaderas sobre lo que ya hay. Usa `size`                                                                                                                                                                                                                                                                                                                                                                      |
| `basin`      | Un cuenco que se hace más profundo hacia el centro                                                                                                                                                                                                                                                                                                                                                               |
| `spire`      | Una columna que se estrecha                                                                                                                                                                                                                                                                                                                                                                                      |
| `nodule`     | Una bola irregular                                                                                                                                                                                                                                                                                                                                                                                               |
| `vent`       | Una columna estrecha que se detiene al chocar con algo                                                                                                                                                                                                                                                                                                                                                           |
| `imprint`    | Una de tus plantillas `.nbt`. Una que cabe dentro de un chunk se desplaza para que aterrice entera en el chunk que se está construyendo en lugar de invadir un vecino que aún no se ha creado, sea cual sea el giro; una mayor que un chunk solo se coloca donde ya existe el terreno a su alrededor                                                                                                              |
| `belt`       | Un cluster que abarca varios chunks, para regiones de piedra                                                                                                                                                                                                                                                                                                                                                     |
| `field`      | Vetas calculadas para todos los bloques a la vez, que comparten su forma con los grupos de dureza                                                                                                                                                                                                                                                                                                                |
| `vein`       | Un yacimiento calculado como un campo de ruido con semilla alrededor de un origen, como lo hace Immersive Geology: cada chunk escribe su propia porción de cada veta cuyo alcance de 24 bloques lo toca, de modo que nada se propaga en cascada, y `/rdplserver vein` puede decir dónde estará una veta antes de que se cree el terreno. Usa `size`, `attempts`, `rarity` y la franja de altura; `pattern` elige el aspecto |
| `spring`     | Un fluido que mana de la pared de una cueva: se coloca donde hay roca arriba, abajo y en tres lados con un lado abierto, y se deja fluyendo                                                                                                                                                                                                                                                                       |

### Tamaño y forma

*formas*

| Clave           | Usada por                                | Valor                        | Por defecto                          | Qué hace                                                                                                                                                                         |
| --------------- | ---------------------------------------- | ---------------------------- | ------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `type`          | todos                                    | una de las formas anteriores | `cluster`                            | Qué forma                                                                                                                                                                        |
| `radius`        | plate, geode, basin, spire, nodule, vent | int o rango                  | `6`                                  | Cuán ancha es                                                                                                                                                                    |
| `height`        | plate, geode, basin, spire, vent, tree   | int o rango                  | `1`, `8` para geode, `5` para tree   | Cuán alta o gruesa es                                                                                                                                                            |
| `width`         | geode                                    | int o rango                  | `12`                                 | La extensión total de la bolsa                                                                                                                                                   |
| `plane`         | plate, basin, spire, vent                | `circle`, `square`           | `circle`                             | Su huella                                                                                                                                                                        |
| `slim`          | plate, largevein, nodule                 | boolean                      | `false`                              | Plate: una capa más fina. Largevein: ramas de un solo bloque. Nodule: cáscara hueca                                                                                              |
| `hanging`       | spire, vent                              | boolean                      | `false`                              | Crece hacia abajo desde un techo en lugar de hacia arriba desde un suelo                                                                                                         |
| `taper`         | spire                                    | `straight`, `bell`, `needle` | `straight`                           | Cómo se reduce la anchura hacia la punta. `straight` se estrecha de manera uniforme, `bell` mantiene su anchura abajo y luego cae, `needle` se afina de inmediato hasta una punta larga |
| `outline`       | geode                                    | nombre de bloque             | ninguno                              | El bloque de la corteza                                                                                                                                                          |
| `fill`          | geode                                    | nombre de bloque             | ninguno                              | Lo que llena el centro. Si se omite, el centro queda hueco                                                                                                                       |
| `middle`        | geode                                    | nombre de bloque             | ninguno                              | Una cáscara entre el cuerpo y `outline`, la calcita de la geoda de amatista del juego                                                                                            |
| `budding`       | geode                                    | nombre de bloque             | ninguno                              | Se coloca en lugar de los bloques del cuerpo que dan al centro hueco, como lo hace la amatista en brote. Necesita `fill`                                                         |
| `buddingChance` | geode                                    | 0.0 a 1.0                    | `0.083`                              | Cuántos de esos bloques del cuerpo brotan                                                                                                                                        |
| `crystal`       | geode                                    | nombre de bloque             | ninguno                              | Crece en el hueco junto a un bloque `budding`, como un cúmulo de amatista                                                                                                        |
| `crystalChance` | geode                                    | 0.0 a 1.0                    | `0.35`                               | En cuántos de esos puntos crece uno                                                                                                                                              |
| `crack`         | geode                                    | 0.0 a 1.0                    | `0`                                  | La probabilidad de que una geoda esté abierta: un tubo desde el centro hacia fuera a través de todas las capas por un lado, relleno con `fill`. Las geodas de amatista del juego usan `0.95` |

### Colocación

*formas*

| Clave              | Usada por        | Valor                    | Por defecto               | Qué hace                                                                                                                                                                                                                            |
| ------------------ | ---------------- | ------------------------ | ------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `surface`          | decoration, tree | lista de nombres de bloque | ninguno                 | Sobre qué se asentará                                                                                                                                                                                                               |
| `seeSky`           | decoration       | boolean                  | `true`                    | Solo coloca donde el cielo es visible                                                                                                                                                                                               |
| `checkStay`        | decoration       | boolean                  | `true`                    | Solo coloca donde el bloque sobreviviría                                                                                                                                                                                            |
| `stackHeight`      | decoration       | int o rango              | `1`                       | Cuántos apilar unos encima de otros                                                                                                                                                                                                 |
| `scatterX`         | decoration, tree | int                      | `8`                       | Cuánto se desplaza hacia los lados                                                                                                                                                                                                  |
| `scatterY`         | decoration, tree | int                      | `4`                       | Cuánto se desplaza en vertical                                                                                                                                                                                                      |
| `scatterZ`         | decoration, tree | int                      | `8`                       | Cuánto se desplaza hacia los lados                                                                                                                                                                                                  |
| `rarity`           | cualquiera       | int                      | ninguno (`400` para belt) | Una colocación por cada tantos chunks. En un belt separa los belts entre sí; en cualquier otra forma condiciona toda la entrada, de modo que solo un chunk de cada tantos sortea siquiera sus `attempts`. `field` la ignora         |
| `rarityIsPerChunk` | cualquiera       | boolean                  | `false`                   | Convierte `rarity` en cuántas colocaciones recibe cada chunk                                                                                                                                                                        |

### Árboles

*formas*

| Clave    | Usada por | Valor            | Por defecto | Qué hace                      |
| -------- | --------- | ---------------- | ----------- | ----------------------------- |
| `log`    | tree      | nombre de bloque | ninguno     | El bloque del tronco          |
| `leaves` | tree      | nombre de bloque | ninguno     | El bloque de las hojas        |
| `vines`  | tree      | boolean          | `false`     | Cuelga enredaderas de las hojas |

Un `tree` sin `log` ni `leaves` no genera nada, y lo dice en el registro. Nombrar un `structure`, o varios en `structures`, planta esa plantilla en cada punto en lugar de hacer crecer un árbol, y entonces no hace falta `log` ni `leaves`; un árbol de plantilla lee `turns`, `mirrors`, `integrity`, `lootTable` y `locateAs` exactamente igual que un `imprint`.

### Colocación de plantillas

*formas*

| Clave        | Usada por     | Valor                | Por defecto | Qué hace                                                                                                                                                                                                                                                                                       |
| ------------ | ------------- | -------------------- | ----------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `structure`  | imprint, tree | `namespace:name`     | ninguno     | La plantilla que se coloca                                                                                                                                                                                                                                                                     |
| `integrity`  | imprint, tree | 1 a 100              | `100`       | Porcentaje de los bloques de la plantilla que aparecen realmente                                                                                                                                                                                                                               |
| `lootTable`  | imprint, tree | `namespace:path`     | ninguno     | La tabla de botín con la que se llena cada cofre de la plantilla colocada la primera vez que se abre, y cualquier otro contenedor que admita una, entre ellos una caja de shulker o el cajón de un mod. Cubre `structure` y cada entrada de `structures`; cada cofre sortea su propia semilla |
| `structures` | imprint, tree | lista                | ninguno     | Varias plantillas entre las que elegir, una colocada cada vez. Cada entrada es `{ "structure": "namespace:name", "weight": 3 }`, o un nombre sin más para probabilidades iguales. Sustituye a `structure`                                                                                       |
| `turns`      | imprint, tree | lista                | cualquiera  | En qué sentido puede colocarse: `none`, `quarter`, `half`, `threequarter`. Las entradas pueden llevar un `weight`. Si se omite, los cuatro son igual de probables                                                                                                                               |
| `mirrors`    | imprint, tree | lista                | ninguno     | Voltearla además: `none`, `leftright`, `frontback`, con `weight` opcional. Una entrada que nombra su propio peso se escribe `{ "mirror": "leftright", "weight": 2 }`, y una entrada de `turns` igual con `turn`                                                                                  |
| `at`         | imprint       | dos ints, x y z      | ninguno     | Coloca exactamente una vez en esas coordenadas de bloque sobre la superficie, cuando se genera ese chunk, en lugar de por azar. Consulta [Estructuras en lugares exactos](#estructuras-en-lugares-exactos)                                                                                      |
| `locateAs`   | imprint, tree | string               | ninguno     | Registra cada estructura que coloca esta entrada con ese nombre, de modo que `/rdplserver locate <name>` encuentre la más cercana. Consulta [Localizar estructuras colocadas](#localizar-estructuras-colocadas)                                                                                 |

Para una forma que ningún tipo integrado cubre, `imprint` es el camino: constrúyela como plantilla `.nbt` y colócala, con `structures` para variarla, `turns` y `mirrors` para girarla y `integrity` para disolverla en algo más tosco que el archivo que dibujaste.

### Estructuras en lugares exactos

*formas*

Las estructuras de vanilla se fijan a puntos exactos con `structureAt` en los ajustes de `terrain`, como entradas `structure=x,z`, una por línea: `"structureAt": ["villages=1000,-500"]`. **La x y la z son coordenadas de bloque, no de chunk**, y la estructura se genera en el chunk que contiene ese bloque. Con `terrainAdaptation` trazando las aldeas como calles de ciudad, el pozo de una aldea fijada se alza en ese mismo bloque, o lo más cerca que permita su distrito cuando el bloque queda a pocos bloques del borde del distrito; las demás estructuras, y las aldeas trazadas sin ella, empiezan donde el juego las empezaría en ese chunk. Una entrada por cada instancia deseada. Su espaciado, separación, distancia mínima de aparición y comprobaciones de terreno llano quedan todos al margen, así que el punto es responsabilidad del pack, y dos fijaciones a menos de un chunk de distancia colocan dos estructuras en el mismo chunk. La estructura se asienta en el suelo de su chunk según las reglas habituales una vez fundada.

| Ajuste        | Tipo                    | Por defecto | Qué hace                                                                                                                                                                                                                                                                                         |
| ------------- | ----------------------- | ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `structureAt` | lista de `structure=x,z` | ninguno    | Fija una estructura de vanilla a un punto exacto, una entrada por cada instancia deseada. La x y la z son coordenadas de bloque, y la estructura se genera en el chunk que contiene ese bloque; su espaciado, separación, distancia mínima de aparición y comprobaciones de terreno llano quedan todos al margen |

Una entrada `imprint` se fija del mismo modo con `"at": [x, z]` en su forma, colocando exactamente una vez en esas coordenadas sobre la superficie cuando se genera ese chunk, en lugar de por azar. Se combina con `locateAs`, de modo que una estructura fijada también puede encontrarse.

### Localizar estructuras colocadas

*formas*

Una entrada `imprint` con `"locateAs": "Crypt"` registra con ese nombre cada estructura que coloca, y `/rdplserver locate Crypt` señala entonces la más cercana, con el nombre ofrecido en el autocompletado con tabulador; `/rdplserver goto Crypt` te lleva hasta allí. Solo pueden encontrarse las estructuras que ya se han generado, ya que las estructuras de pack se colocan por azar a medida que se crean los chunks y no sobre una cuadrícula que el juego pueda predecir. Los nombres viven en el guardado del mundo, así que sobreviven a los reinicios y funcionan en servidores. A un nombre registrado así también se le puede dar su propio permiso con `gotoPlaceLevels`, de modo que un pack decida quién puede ser llevado a sus propias estructuras por separado de las de vanilla.

### Claves de campos y vetas

*formas*

| Clave       | Usada por   | Valor                         | Por defecto             | Qué hace                                                                                                                                                                                                                                                                       |
| ----------- | ----------- | ----------------------------- | ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `field`     | field       | objeto                        | `{ "type": "speckle" }` | Cómo se calcula el campo. Mismas claves que el `field` de un grupo de dureza, descritas en [El campo](#el-campo): `speckle` con `chances` y `spread`, o `seeded` con `cell`, `seeds`, `reach`, `arms` y `armReach`                                                                |
| `threshold` | field, vein | 0.0 a 1.0                     | `0.5` (`0.4` para vein) | Cuán fuerte debe ser el campo en un bloque para que se coloque. Más bajo llena más                                                                                                                                                                                             |
| `fade`      | field       | int                           | `0`                     | Difumina en moteado la parte superior de la franja en lugar de terminarla de golpe: sobre esta cantidad de bloques de la parte alta del rango de altura, las probabilidades de colocación de cada bloque disminuyen paso a paso, el mismo aspecto que el motor da a `deepStone` donde se encuentra con el mundo de arriba |
| `pattern`   | vein        | `default`, `banded` o `tube`  | `default`               | El aspecto del yacimiento: una masa deformada, capas apiladas cada pocos bloques, o tubos huecos que serpentean por la roca                                                                                                                                                     |
| `density`   | vein        | 0.0 a 1.0                     | `1.0`                   | La proporción de bloques que cumplen los requisitos y que se colocan realmente, una moneda por bloque                                                                                                                                                                          |
| `rich`      | vein        | nombre de bloque              | ninguno                 | Se coloca desde `richAt` hacia arriba en el rango del campo por encima de `threshold`, el corazón del yacimiento, en lugar de los bloques de la entrada                                                                                                                         |
| `poor`      | vein        | nombre de bloque              | ninguno                 | Se coloca en las dos quintas partes inferiores de ese rango, la periferia, en lugar de los bloques de la entrada; el medio son los bloques propios de la entrada. Si se omite cualquiera de los niveles, allí se colocan los bloques de la entrada                              |
| `richAt`    | vein        | 0.0 a 1.0                     | `0.88`                  | Dónde empieza el nivel rico en ese rango: `0.88` limita el bloque rico a la octava parte más fuerte del yacimiento, un número menor engorda el núcleo rico, `1.0` no deja ningún bloque rico                                                                                     |
| `poorAt`    | vein        | 0.0 a 1.0                     | `0.4`                   | Dónde empiezan los bloques propios de la entrada: por debajo de este valor se coloca el bloque `poor`, así que `0.4` da una periferia de las dos quintas partes inferiores y `0.0` no deja periferia pobre. Se limita a `richAt`                                                 |

Una veta `field` es la única forma que se describe en lugar de elegirse. Ejecuta la misma retícula que usan los grupos de dureza, así que `seeded` con unos pocos brazos da nudos con zarcillos que se extienden hacia sus vecinos, lo que es una veta y no una masa, y `threshold` decide cuánto de ella es lo bastante sólido para colocarse:

```json
{
  "shape": {
    "type": "field",
    "threshold": 0.4,
    "field": { "type": "seeded", "cell": 10, "reach": 6.0, "arms": 3, "armReach": 5.0 }
  }
}
```

Las claves van en un objeto `field` propio, no junto a `type`, ya que `type` en la forma ya dice `field`.

### Cinturones

*formas*

Un `belt` es una bola mucho mayor que un chunk, que se usa para regiones de piedra y no para vetas de mena. Su `radius` es el tamaño de la bola, y cada chunk calcula por sí mismo dónde empiezan las bolas cercanas, a partir de la semilla del mundo y del nombre de la propia entrada, de modo que un belt sale entero se generen como se generen los chunks y nunca se escribe nada en un chunk vecino.

```json
{
  "shape": { "type": "belt", "radius": 32, "rarity": 400 }
}
```

Un belt ignora `attempts` y `spread`, ya que se coloca por chunk y no por intento. `minHeight` y `maxHeight` son la franja en la que se sitúan los centros, y la bola llega `radius` más allá de esa franja. `replace` decide lo que se come, `biomes` y los límites de temperatura y lluvia se comprueban en el centro, de modo que un belt aparece entero o no aparece en absoluto en lugar de quedar cortado en el borde de un bioma.

El coste crece con el cubo de `radius`, y un `rarity` bajo lo multiplica, así que empieza con los valores por defecto y sube el radio poco a poco.

### Campos

*formas*

Un `field` no coloca nada en un punto y lo coloca todo a la vez. En lugar de elegir un lugar y construir una forma a su alrededor, le hace una pregunta a cada bloque del chunk, entre `minHeight` y `maxHeight`, y coloca allí donde la respuesta es al menos `threshold`. La pregunta es la misma que se hacen los grupos de dureza, de modo que ambos describen las mismas vetas y un pack puede crear un grupo y una entrada que coincidan.

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

| Clave       | Obligatoria | Valor      | Por defecto | Qué hace                                                                            |
| ----------- | ----------- | ---------- | ----------- | ----------------------------------------------------------------------------------- |
| `threshold` | no          | 0.0 a 1.0  | `0.5`       | Cuánta intensidad debe tener el campo para que se coloque un bloque                 |
| `field`     | sí          | objeto     | ninguno     | El mismo objeto que admite un grupo de dureza, con los mismos tipos `speckle` y `seeded` |

Un `threshold` bajo toma la mayor parte del campo y da vetas amplias; uno alto toma solo el centro de cada mancha y da bolsas pequeñas y dispersas. Con `speckle` obtienes muchas motas diminutas; con `seeded`, parches más redondeados o, cuando tiene brazos, nudos con zarcillos que se extienden entre ellos.

Como un cinturón, un campo ignora `attempts` y `spread`, porque se consulta por chunk y no por intento, y nunca escribe en un chunk vecino. Se calcula a partir de la semilla del mundo y del nombre de la propia entrada, así que la misma semilla da siempre las mismas vetas y dos entradas con nombres distintos nunca coinciden. `replace`, `adjacent`, `biomes` y los límites de clima se aplican como de costumbre.

## Dispersiones

*generación del mundo*

Un bloque `spread` con un `type`.

Se muestran todas las claves a la vez. Un archivo real escribe solo las que necesita. Una clave marcada para un tipo solo la lee ese tipo.

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

| Tipo        | Dónde lo coloca                                              |
| ----------- | ------------------------------------------------------------ |
| `even`      | En cualquier punto entre las alturas, de forma uniforme. Es el valor por defecto |
| `centered`  | Con más peso en una altura, y menos a medida que se aleja    |
| `sprawl`    | Vetas fractales que abarcan un rango de alturas              |
| `terrain`   | Siguiendo la superficie                                      |
| `cavern`    | En el suelo o el techo de las cuevas                         |
| `submerged` | Bajo el agua u otro fluido                                   |

| Clave               | Usada por | Valor                    | Por defecto                      | Qué hace                                                      |
| ------------------- | --------- | ------------------------ | -------------------------------- | ------------------------------------------------------------- |
| `type`              | todos     | una de las dispersiones anteriores | `even`                 | Qué dispersión                                                |
| `center`            | centered  | int                      | punto medio del rango de alturas | La altura en torno a la que se agrupa                         |
| `range`             | centered  | int                      | la mitad del rango de alturas    | Hasta dónde llega desde esa altura                            |
| `smoothness`        | centered  | 1 a 8                    | `2`                              | Cuántas tiradas se promedian. Cuanto más alto, más estrecha la franja |
| `veinHeight`        | sprawl    | int                      | el rango de alturas              | Cuánto mide de alto una veta                                  |
| `veinDiameter`      | sprawl    | int                      | `12`                             | Cuánto mide de ancho una veta                                 |
| `verticalDensity`   | sprawl    | 1 a 100                  | `16`                             | Cuán maciza es en vertical                                    |
| `horizontalDensity` | sprawl    | 1 a 100                  | `32`                             | Cuán maciza es en horizontal                                  |
| `offsetMin`         | terrain   | int                      | `0`                              | Desplazamiento mínimo respecto a la superficie                |
| `offsetMax`         | terrain   | int                      | `offsetMin`                      | Desplazamiento máximo respecto a la superficie                |
| `ceiling`           | cavern    | boolean                  | `false`                          | Se pega al techo de la cueva en lugar de al suelo             |

## Mapas de estructuras

*generación del mundo*

Un mapa de estructuras compone plantillas en un único edificio con nombre sobre una cuadrícula, muy por encima del límite de 48 bloques de un solo archivo `.nbt`. Cada capa se dibuja como filas de caracteres sueltos, un carácter por celda, y se apila a una altura de celda por encima de la capa anterior. Como máximo 8 capas de 8 por 8 celdas, lo que con la celda por defecto de 32 son 256 bloques por lado.

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

| Ajuste       | Tipo                  | Por defecto | Qué hace                                                                                                                                       |
| ------------ | --------------------- | ----------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| `cell`       | número                | `32`        | El paso de la cuadrícula en bloques, hasta 48. Una plantilla menor que la celda se sitúa en la esquina de la celda, de modo que las piezas de tamaño completo encajan sin costuras |
| `ground`     | número                | `0`         | Qué capa apoya en la superficie del terreno. Las capas anteriores excavan hacia abajo, que es como un edificio obtiene sótanos                 |
| `at`         | dos números           | ninguno     | Fija una copia en coordenadas de bloque exactas, igual que `structureAt` fija una aldea                                                        |
| `spacing`    | número                | `0`         | Dispersa copias en una cuadrícula con esta separación en chunks, con variación según la semilla del mundo. `0` no dispersa ninguna, así que un mapa con solo `at` se construye exactamente una vez |
| `chance`     | número                | `100`       | El porcentaje de puntos de la cuadrícula que construyen una copia                                                                              |
| `dimensions` | lista de ids de dimensión | todas   | Dónde puede construirse el mapa, incluidas las dimensiones propias de un pack                                                                  |
| `layers`     | lista                 | ninguno     | Las capas, de abajo arriba, cada una con una `palette` y un `map`                                                                              |

Una paleta nombra plantillas por su clave de registro, tomadas de `<namespace>/structures/` de un pack.

| Valor                                       | Qué hace                                                                                                                                                            |
| ------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `"a": "mypack:keep"`                        | Cada celda `a` de esa capa coloca esta plantilla                                                                                                                    |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | Cada celda `a` sortea la lista según el peso, a partir de la semilla del mundo y la posición de la celda, de modo que dos copias del edificio difieren pero el mismo mundo siempre construye el mismo |
| `.`                                         | Una celda vacía, no se coloca nada                                                                                                                                  |

Cada copia sortea una de las cuatro orientaciones a partir de la semilla del mundo y todo el edificio gira a la vez, plantillas incluidas, así que los muros que se encuentran entre celdas siguen encontrándose; un mapa gira, pero nunca se refleja. La capa del suelo apoya en la superficie del terreno muestreada bajo el centro del edificio, y todo el mapa comparte esa única altura. Un mapa disperso es una estructura propia para el juego, colocada mediante un conjunto de estructuras escrito por ti, de modo que cada chunk construye solo su porción de la cuadrícula y un edificio que abarca muchos chunks llega sin generación en cascada, sea cual sea el orden en que se carguen los chunks. Una [parcela de aldea](#parcelas-de-aldea) de tipo `template` también puede nombrar un mapa como su `structure`, lo que convierte el compuesto en un edificio de ciudad.

## Parcelas de aldea

*generación del mundo*

`<namespace>/villages/*.json`

La ruta del archivo es el nombre de la parcela, que `villagePieces` puede nombrar para conservarla o descartarla.

Un archivo aquí añade una pieza que pueden construir las ciudades y aldeas del pack. Hay dos clases, que se eligen con `type`.

Se muestran todas las claves a la vez. Un archivo real escribe solo las que necesita. Una clave marcada para un tipo solo la lee ese tipo.

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

### Cada parcela

*parcelas de aldea*

| Clave        | Usada por | Valor                              | Por defecto      | Qué hace                                                                                                                                                                                                                                                                                                                                                                          |
| ------------ | --------- | ---------------------------------- | ---------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `type`       | todos     | `farm` o `template`                | `farm`           | Qué clase de parcela                                                                                                                                                                                                                                                                                                                                                              |
| `weight`     | todos     | int                                | `3`              | Con qué frecuencia se elige esta parcela frente a las demás del pack                                                                                                                                                                                                                                                                                                              |
| `leastCount` | todos     | int                                | `1`              | Mínimo por distrito: un distrito sitúa parcelas a lo largo de sus calles hasta un tope sorteado entre el menor `leastCount` y el mayor `mostCount` de las parcelas que puede construir, ambos aumentados en 16, o en una treintaidosava parte de `villagePlotsLeast` cuando esta sea mayor, mientras una ciudad crece. Las parcelas situadas detrás de otras parcelas no cuentan |
| `mostCount`  | todos     | int                                | `4`              | El límite superior de ese sorteo                                                                                                                                                                                                                                                                                                                                                  |
| `width`      | todos     | int                                | `7`              | Tamaño a lo ancho de la calle                                                                                                                                                                                                                                                                                                                                                     |
| `height`     | todos     | int                                | `4`              | Altura despejada sobre el suelo                                                                                                                                                                                                                                                                                                                                                   |
| `depth`      | todos     | int                                | `9`              | Tamaño en dirección opuesta a la calle                                                                                                                                                                                                                                                                                                                                            |
| `apron`      | todos     | int                                | `2`              | Cuánto puede desviarse el suelo respecto al nivel de la calle bajo la parcela antes de que se rechace o se deslice a lo largo de su calle: esa cantidad de bloques de relleno bajo ella, o de corte en una elevación sobre ella, y no más que eso entre su esquina más alta y la más baja. Una parcela ancha en colinas necesita más. Si lo pones alto, la parcela se aterraza directamente en una pendiente, lo que en el lugar equivocado se come una montaña |
| `ground`     | todos     | nombre de bloque                   | `minecraft:dirt` | Lo que se compacta debajo en una pendiente                                                                                                                                                                                                                                                                                                                                        |
| `requires`   | todos     | lista de ids de mod o espacios de nombres de pack | ninguno | La parcela se omite salvo que estén presentes todos                                                                                                                                                                                                                                                                                                                       |

Las parcelas son lo que construyen las ciudades propias del pack a lo largo de sus calles, y cada parcela de plantilla se une además a las aldeas del propio juego como una de sus casas, con la entrada en el centro de su fachada. Sin ningún archivo de parcelas, una ciudad construye las casas de aldea del propio juego para el tipo de aldea del bioma de cada distrito. `weight` decide cuál de tus parcelas se elige cuando una calle pide una, y `villagePieces` en los ajustes de `villages` nombra las parcelas que conserva una plantilla, como `mypack:smithy`, `smithy` o la estructura que construye la parcela. Cómo se trazan, decoran, tienden sobre puentes, excavan en túneles y dotan de vías las propias calles lo definen los ajustes `village*` de [Qué hace cada grupo](#qué-hace-cada-grupo).

### Granjas

*parcelas de aldea*

Una `farm` es un campo descrito en lugar de programado: una parcela del tamaño que pidas, bordeada con un bloque, rellena con filas de tierra separadas por canales de agua y plantada con un cultivo elegido por bloque de tu lista.

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

| Clave      | Usada por | Valor                    | Por defecto          | Qué hace                                        |
| ---------- | --------- | ------------------------ | -------------------- | ----------------------------------------------- |
| `crops`    | farm      | lista de nombres de bloque | trigo              | Se planta uno por bloque, en una fase de crecimiento aleatoria |
| `edge`     | farm      | nombre de bloque         | `minecraft:oak_log`  | El marco alrededor de la parcela                |
| `soil`     | farm      | nombre de bloque         | `minecraft:farmland` | De qué están hechas las filas                   |
| `water`    | farm      | boolean                  | `true`               | Pone un canal de agua entre las filas           |
| `rowWidth` | farm      | int                      | `2`                  | Cuánto mide de ancho cada fila de tierra        |

### Construido a partir de plantillas

*parcelas de aldea*

Una `template` coloca en su lugar una de tus estructuras `.nbt`, girada para mirar hacia la calle.

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

Una `template` cuya `structure` nombra uno de tus [mapas de estructuras](#mapas-de-estructuras) coloca todo el compuesto como la parcela. El tamaño de la parcela proviene entonces del mapa, su huella y sus capas apiladas multiplicadas por la celda, así que `width`, `height`, `depth` e `integrity` no se leen. Las capas anteriores al `ground` del mapa excavan hacia abajo como sótanos, y las celdas de paleta con peso siguen sorteándose por edificio, de modo que dos torres del mismo mapa pueden diferir.

```json
{
  "type": "template",
  "weight": 2,
  "structure": "mypack:castle",
  "villagers": 4
}
```

| Clave            | Usada por | Valor            | Por defecto    | Qué hace                                                                                                                                          |
| ---------------- | --------- | ---------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------- |
| `structure`      | template  | `namespace:name` | ninguno        | La plantilla que se coloca, o uno de tus mapas de estructuras, que entonces fija el tamaño de la parcela                                          |
| `integrity`      | template  | 1 a 100          | `100`          | Porcentaje de los bloques de la plantilla que aparecen                                                                                            |
| `lootTable`      | template  | `namespace:path` | ninguno        | La tabla de botín con la que se rellena cada cofre de la plantilla colocada la primera vez que se abre. Una parcela que nombra un mapa de estructuras se deja tal cual |
| `villagers`      | todos     | int              | `0`            | Cuántos habitantes genera la parcela                                                                                                              |
| `villagerEntity` | todos     | `namespace:name` | un aldeano     | Quién vive allí, como una variante de entidad propia                                                                                              |
| `villagerX`      | todos     | int              | `1`            | Dónde aparecen, a lo ancho de la parcela                                                                                                          |
| `villagerY`      | todos     | int              | `1`            | Dónde aparecen, sobre el suelo                                                                                                                    |
| `villagerZ`      | todos     | int              | `1`            | Dónde aparecen, hacia el interior de la parcela                                                                                                   |

## Mapas de trazado de ciudades

*generación del mundo*

Un mapa de ciudad dibuja el plano de calles de una ciudad sobre una cuadrícula, un carácter por celda, y la ciudad se traza a partir del dibujo en lugar de sortear uno. Las calles, plazas y parcelas salen como las mismas piezas que usa una ciudad sorteada, así que toda opción de calles, puente, túnel, metro, alcantarilla, farola y pieza central de plaza se aplica sin cambios. La plantilla de mundo nombra el mapa en `villageLayout`.

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

| Ajuste     | Tipo   | Por defecto | Qué hace                                                                                                                                                                                                  |
| ---------- | ------ | ----------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `cell`     | número | `48`        | El paso de la cuadrícula en bloques, de 8 a 128. Las calles discurren por el centro de sus celdas con el ancho de calle del pack y las parcelas se centran en las suyas, así que una celda necesita la parcela más ancha más espacio para dar a la calle |
| `palette`  | objeto | ninguno     | Qué traza cada carácter, detallado más abajo                                                                                                                                                              |
| `map`      | lista  | ninguno     | Las filas, hasta 64 por 64 celdas. Una fila más corta que la más ancha queda abierta pasado su final                                                                                                      |
| `settings` | objeto | ninguno     | Ajustes de ciudad solo para este mapa, con los nombres que usa una plantilla de mundo, como `villagePathCenterBlock`. Prevalecen sobre los de la plantilla, y los ajustes propios de un bioma siguen prevaleciendo sobre ellos |

| Valor                                                                   | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| ----------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `"#": "street"`                                                         | Una serie de celdas de calle a lo largo de una fila o columna se convierte en una sola calle con el ancho del pack. Donde una serie de fila cruza una de columna, el cruce se pinta como cualquier otro. Una celda de calle aislada, sin serie en ninguno de los ejes, se traza como un tramo corto a lo largo de la fila                                                                                                                                              |
| `"+": "plaza"`                                                          | Una plaza con su pieza central. Las series atraviesan las celdas de plaza, así que las calles confluyen en la plaza, y una plaza en un cruce levanta su pieza central `villageWellStructure` en medio del cruce como una rotonda. La primera plaza del archivo es el centro de la propia ciudad, lo que ancla el mapa al lugar donde se funda la ciudad; un mapa sin plaza se centra allí                                                                              |
| `"a": "alley"`                                                          | Una serie estrecha. Los edificios dan a ella, pero no conecta nada, la regla del callejón de siempre                                                                                                                                                                                                                                                                                                                                                                   |
| `"J": "junction"`                                                       | Una celda de calle trazada en ambas direcciones, de modo que allí se levanta un cruce incluso donde el dibujo la atraviesa en un solo sentido. El brazo que la cruza mide una celda                                                                                                                                                                                                                                                                                    |
| `"b": "bulb"`                                                           | Una celda de calle que termina en un fondo de saco. Cuando un mapa tiene una celda bulb, solo los extremos de calle situados en celdas bulb obtienen uno; un mapa sin ninguna da fondo de saco a tres de cada cuatro extremos muertos, sorteados según la semilla del mundo. Un fondo de saco solo se asienta donde no haya parcela, otra calle, vía férrea ni pieza central de plaza a su alcance: se encoge para ajustarse, hasta un poco más ancho que la calle, y un extremo sin espacio en ningún tamaño sigue siendo un extremo normal |
| `"E": { "kind": "elevated", "height": 8 }`                              | Una celda de calle elevada sobre una plataforma `height` bloques, de 2 a 64, por encima del terreno más alto bajo su tramo de celdas elevadas contiguas, con una rampa de un bloque por fila en cada extremo. Un cruce de calles dentro del tramo se eleva con él, y las parcelas a lo largo de este permanecen en el suelo. Un tramo cuya plataforma o rampas alcanzarían una vía férrea o la pieza central de la plaza se queda a nivel, con una línea en el registro. Cualquier valor puede escribirse como objeto de este modo, con `kind` nombrando la palabra |
| `"W": { "kind": "street", "settings": { "villagePathExtraWidth": 8 } }` | Una calle trazada y pavimentada con sus propias claves de calle, que prevalecen sobre las del mapa y las de la plantilla. Su ancho sigue a su propio `villagePathExtraWidth`, `villagePathSidewalkWidth` y línea, y su superficie, líneas y aceras siguen a sus propias claves de bloque, de modo que una avenida o un camino se dibuja con una marca propia. Una serie toma las claves de su primera celda que defina alguna. Por ancha o estrecha que sea, una calle dibujada sigue siendo una calle: nunca se toma por un callejón |
| `"T": "mypack:tower"`                                                   | Una celda de parcela, trazada a partir de esa definición de parcela, centrada en la celda y orientada hacia la calle más cercana                                                                                                                                                                                                                                                                                                                                       |
| `"T": ["mypack:a=3", "mypack:b=1"]`                                     | Lo mismo, sorteado por peso a partir de la semilla del mundo y la posición de la celda, de modo que el mismo mundo traza siempre la misma parcela allí                                                                                                                                                                                                                                                                                                                 |
| `"g": "grow"`                                                           | Se deja al trazado sorteado, que rellena esas celdas y se extiende hacia fuera desde el mapa                                                                                                                                                                                                                                                                                                                                                                           |
| `.` o `open`                                                            | Terreno abierto, no se traza nada                                                                                                                                                                                                                                                                                                                                                                                                                                      |

Cada mapa sortea una de las cuatro orientaciones a partir de la semilla del mundo y gira entero, así que un plano se lee igual desde cualquier lado. Las calles se trazan primero, de modo que una parcela que se solaparía con una calle u otra parcela se deja abierta con una línea en el registro, y un nombre de parcela que ningún pack proporciona deja su celda abierta del mismo modo. El mapa no cambia cómo se decoran las piezas: las claves de calle, `villageBlocks`, las farolas y la pieza central de la plaza se leen igual que en una ciudad sorteada.

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

| Ajuste                | Tipo    | Por defecto | Qué hace                                                                                                                                                                                                                                                          |
| --------------------- | ------- | ----------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `retrogen`            | boolean | `false`     | Pone al día los chunks guardados antes de que existiera una entrada, para cada entrada de worldgen marcada con `"retrogen": true`. Desactivado, los chunks que ya existen se dejan como están. Los chunks se marcan al generarse en ambos casos, así que activarlo más tarde solo afecta a chunks anteriores al pack |
| `adoptExistingChunks` | boolean | `false`     | Qué ocurre la primera vez que se ve un chunk antiguo: activado, se sella como si este pack ya lo hubiera generado y nunca se pone al día; desactivado, se pone al día como cualquier otro. Para rellenar un mundo existente, activa `retrogen` y desactiva esto |

Una entrada con `"retrogen": true` se genera en los chunks que se guardaron antes de añadirla. Cada chunk registra lo que ha recibido, así que nada se hace dos veces.

La marca de la entrada solo indica que una entrada es apta. Ponerse al día se activa con el ajuste `retrogen`, que un pack puede fijar en su bloque `settings` o un jugador puede fijar en la configuración, y está desactivado por defecto. Junto a él, `adoptExistingChunks` decide qué ocurre la primera vez que se ve un chunk antiguo: activado, el chunk se sella como si este pack ya lo hubiera generado y nunca se pone al día; desactivado, se pone al día como cualquier otro. Activar `retrogen` mientras `adoptExistingChunks` también está activado no hace nada, porque cada chunk antiguo se da por cubierto antes de poder entrar en la cola. Para rellenar un mundo existente, activa `retrogen` y desactiva `adoptExistingChunks` a la vez. `retrogenChunksPerTick` en la configuración, por defecto `2`, es cuántos chunks antiguos se ponen al día en cada tick.

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

Cambiar `retrogenKey` en la configuración hace que todos los chunks vuelvan a ser aptos, lo que añade las nuevas vetas encima de las antiguas, de modo que la densidad se duplica. Es deliberado, y por eso la clave es manual.

## Pregeneración

*generación del mundo*

Crear el terreno de un mundo por adelantado, para que nadie genere chunks mientras juega: sin lag de chunks, con un tamaño en disco conocido y una sola espera al principio en lugar de una primera hora a trompicones.

Los primeros 12 chunks alrededor del punto de aparición siempre se encargan, digan lo que digan un pack o la configuración, porque el juego crea exactamente esa cantidad por sí mismo antes de que entre nadie. `pregenOnNewWorld` fija hasta dónde llegar más allá, y el comando ejecuta una a mano.

`/rdplserver pregen <radius>` crea todos los chunks a esa distancia en chunks del lugar donde se ejecuta. `status` dice cuánto lleva y `stop` la termina. El terreno se pide al propio sistema de chunks del juego, `pregenChunksInFlight` chunks a la vez, un archivo de región de 32 por 32 chunks a la vez, con las regiones tomadas en anillos desde el centro y los chunks de una región entera a lo largo de una curva de Hilbert, terminando cada región antes de empezar la siguiente, y vuelve iluminado y terminado, así que no hay que ejecutar después ningún paso de iluminación.

Mientras una ejecución está en marcha todos quedan retenidos: convertidos en espectadores, inmovilizados, con una línea pulsante a media pantalla y el progreso en la barra de acción, el cielo quieto a su alrededor, toda criatura y máquina de todas las dimensiones congeladas, y la hora y el clima de la dimensión que se está creando detenidos donde estaban. El modo con el que llegó cada jugador se escribe en el jugador al retenerlo, así que una partida guardada a mitad de ejecución, un cierre inesperado o una reconexión nunca deja a nadie atrapado como espectador; al terminar la ejecución se devuelve exactamente el modo que se tomó, o el `worldGameMode` del pack cuando hay uno, supervivencia para `hardcore`. Un cliente con el mod ve la vista con niebla mientras está retenido y el logotipo aparece con un fundido después; un cliente vanilla ve la retención sencilla. Hasta dónde se ha creado cada dimensión se guarda en el mundo, así que un mundo terminado nunca vuelve a ejecutarse. El final se comunica a todos antes de hacer la copia de seguridad del mundo, y una ejecución iniciada desde la consola o un bloque de comandos informa de sus recuentos allí mismo.

En un pack estos van en el bloque `settings` de una [plantilla de mundo](#plantillas-de-mundo), como cualquier otra clave de `chunks`. Se muestran todas, con `pregenBorderLimit` como única ausencia, porque solo la contiene la configuración:

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

### Qué se crea

*pregeneración*

| Clave                         | Qué hace                                                                                                                                                                                                                                 | Por qué fijarla                                                                  |
| ----------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------- |
| `pregenOnNewWorld`            | Radio en chunks que se crea alrededor del punto de aparición antes de que juegue nadie. 12 es el mínimo y 0 significa ese mínimo y no nada, ya que el juego crea de todos modos 12 chunks alrededor del punto de aparición por su cuenta. Súbelo para llegar más lejos que el juego | Fija hasta dónde llega un pack más allá del terreno que el juego ya crea         |
| `pregenDimensions`            | Qué dimensiones se crean, por id, en orden, cada una alrededor de su propio punto de aparición                                                                                                                                           | Añade el Nether, el End o tus propias dimensiones                                |
| `pregenAllDimensions`         | Todas las dimensiones que tiene el servidor en lugar de una lista, primero el overworld y el resto por orden de id                                                                                                                       | Packs con muchas dimensiones. Cuentan las dimensiones de todos los mods, así que ojo con el tamaño |
| `pregenDimensionsWhenEntered` | Estas se crean la primera vez que alguien pisa en ellas, reteniendo de nuevo a todos hasta terminar                                                                                                                                      | Dimensiones que la mayoría de los jugadores no visita; quienes nunca van no pagan nada |
| `pregenToBorder`              | Rellena cada dimensión hasta su borde del mundo en lugar de un radio, centrado en el borde                                                                                                                                               | Mundos acotados                                                                  |
| `pregenBorderLimit`           | Hasta dónde puede llegar un borde, en chunks en cada sentido, antes de que se rechace la ejecución. Solo en la configuración, nunca una clave de pack                                                                                    | Una protección contra una ejecución descontrolada; súbelo solo sabiendo el tiempo y el disco que permite |

Ejecútala tú mismo antes de publicar, con el radio que vas a publicar, de principio a fin. Los chunks crecen con el cuadrado del radio, 63 en cada sentido son dieciséis mil chunks, 500 son más de un millón, así que la carpeta de regiones y el tiempo real de tu mundo de prueba son las cifras honestas que poner delante de los jugadores. No publiques un radio que nunca se ha ejecutado.

### Cómo se comporta una partida

*pregeneración*

| Clave                  | Qué hace                                                                                                                                                                                                                                                                                                                                                                          | Por qué fijarla                                                  |
| ---------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------- |
| `pregenResume`         | Una ejecución detenida o interrumpida continúa donde se quedó. La dimensión, el centro y el radio de la ejecución se escriben en la partida al empezar, y el recuento hasta el momento cada diez segundos, así que un cierre inesperado, un corte de luz o una salida a mitad de ejecución se reanudan a unos diez segundos de donde terminaron en la siguiente carga. Una ejecución detenida a propósito, por comando o por el perro guardián de bloqueos, se queda detenida | Ejecuciones largas en servidores; las pequeñas se reinician barato sin esto |
| `pregenChunksInFlight` | Cuántos chunks pide la ejecución al juego a la vez. Más mantiene más ocupados los hilos de generación y al servidor menos receptivo para quien está retenido mirando                                                                                                                                                                                                              | Súbelo en un servidor vacío, bájalo en uno en el que se juega    |

### Qué ven los jugadores

*pregeneración*

| Clave                                                          | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          | Por qué fijarla                                                                                                       |
| -------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| `pregenRunningSays`, `pregenFinishedSays`, `pregenStoppedSays` | Los mensajes de cada fase. El primero puede contener `%d` para el porcentaje y, después, `%s` para el nombre de la dimensión, o `%1$d` y `%2$s` para colocarlos en cualquier orden. Con sus valores por defecto hablan el idioma de cada jugador                                                                                                                                                                                                                                                                                  | Reescríbelos con la voz de tu pack, nombra la dimensión cuando se crean varias, o silénciales                         |
| `pregenSpectatingSays`                                         | La línea de retención a media pantalla mientras se crea el terreno. Con su valor por defecto habla el idioma de cada jugador; vacía no muestra nada                                                                                                                                                                                                                                                                                                                                                                               | Mantenla por debajo de unos treinta y cinco caracteres o las ventanas pequeñas la recortan                            |
| `pregenLogo`                                                   | Dónde se coloca el logotipo cuando termina la pregeneración: `left`, `center` o `right`, sobre el texto de media pantalla, visible unos segundos y luego desvaneciéndose con la niebla                                                                                                                                                                                                                                                                                                                                             | Siempre se muestra; una palabra desconocida se lee como `center`                                                      |
| `welcomeSays`                                                  | El saludo verde, mostrado en cada inicio de sesión y tras la pregeneración. Una entrada simple es la línea para todas partes; una entrada `dimension=message` la sustituye para esa dimensión y además saluda a cada llegada allí, p. ej. `"minecraft:the_nether=Welcome to the Nether!"`. La dimensión también puede escribirse como el `0`, `-1` o `1` de 1.12.2, y no se saluda a nadie que llegue mientras se crea el terreno. Un mensaje vacío tras el `=` silencia esa dimensión; una lista vacía no muestra nada. Con su valor por defecto habla el idioma de cada jugador | Una línea simple nombra tu pack; añade líneas de dimensión para dar tema a cada mundo. Mantén las líneas por debajo de unos treinta y cinco caracteres |
| `saysCard`                                                     | Muestra las líneas que dice este mod, la bienvenida, la nota de creación de terreno que recibe un jugador que entra a mitad de ejecución y el final de la ejecución (el progreso en curso sigue en la barra de acción), y las líneas de amenaza, como una tarjeta en la esquina inferior derecha en lugar de en el chat. La tarjeta se desliza al entrar, permanece ocho segundos y se desvanece, y se muestra también sobre una pantalla abierta                                                                                    | Actívalo cuando el chat esté saturado o las líneas deban leerse como parte del mundo y no como charla                 |
| `saysIcon`                                                     | Un ítem dibujado en la tarjeta, p. ej. `minecraft:compass`. Vacío no dibuja ninguno                                                                                                                                                                                                                                                                                                                                                                                                                                               | Dale a la tarjeta el emblema de tu pack                                                                               |
| `saysColor`                                                    | El color de fondo de la tarjeta en hexadecimal, p. ej. `1E2630`. Vacío usa un gris pizarra oscuro                                                                                                                                                                                                                                                                                                                                                                                                                                 | Combínalo con la paleta de tu pack                                                                                    |
| `saysImage`                                                    | Un PNG de los recursos de cliente del pack, p. ej. `rubyworld:textures/gui/card.png`, estirado sobre la tarjeta como fondo y dibujado sobre el color. Vacío no dibuja ninguno                                                                                                                                                                                                                                                                                                                                                      | Dale a la tarjeta un panel pintado; mantén la imagen ancha y baja, se estira a lo que necesite el texto               |
| `saysBackground`                                               | Dibuja el panel de la tarjeta, su borde y la franja de color, y el fondo oscuro tras la bienvenida y las notas en medio de la pantalla mientras un jugador está retenido. Desactivado deja solo el texto, que conserva su sombra, y `saysImage` si hay uno                                                                                                                                                                                                                                                                         | Deja que las líneas floten sobre el mundo, o que un `saysImage` pintado se sostenga solo                              |
| `saysFont`                                                     | La fuente en la que se dibuja el texto de la tarjeta, indicada como `namespace:name`, p. ej. `rubyworld:runes`. Vacío usa la fuente de RDPL, `resourcedatapackloader:rdpl`. El archivo que nombra se describe en Tarjetas                                                                                                                                                                                                                                                                                                          | Dale a la tarjeta la tipografía propia de tu pack                                                                     |
| `toasts`                                                       | Cuáles de los avisos del juego, las ventanas emergentes de la esquina superior derecha, se muestran. `true` los muestra todos y `false` ninguno; una lista muestra solo las clases que nombra: `advancements`, `recipes` para las recetas desbloqueadas, `tutorial` para las pistas del tutorial, `system` para los avisos propios del juego y `other` para todo aviso que los demás no cubran, como los de otros mods. Por defecto no muestra ninguno. El cliente de un jugador toma el valor al entrar                              | Conserva `["advancements"]` cuando tu pack guía a los jugadores con logros y el resto estorba                         |

### Copia de seguridad y reinicio del mapa

*pregeneración*

| Clave                   | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | Por qué fijarla                                                                  |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------- |
| `pregenBackup`          | Copia el mundo a una copia de seguridad intacta cuando termina la pregeneración, mientras los jugadores siguen retenidos. La generación se paga entonces una sola vez: un reinicio posterior, o un mundo nuevo con el mismo pack y semilla, restaura la copia en lugar de generar de nuevo, lo que es mucho más rápido que pregenerar dos veces. La copia se guarda fuera de la partida, en `rdpl-pristine/<world>` a su lado, para que las copias de otros mods no la barran y no aparezca en una carpeta que ellos gestionen. Una copia cuyos packs ya no coinciden con los cargados se descarta y se vuelve a guardar a partir del mundo actual, así que un cambio de pack nunca reinicia al mapa de otro | `false`                                                                          |
| `pregenBackupSays`      | La línea a media pantalla que ven los jugadores mientras se hace esa copia, con el porcentaje a continuación. Vacía no muestra nada y la copia se hace en silencio                                                                                                                                                                                                                                                                                                                                                                                                                                                       | `Pack requested world backup`                                                    |
| `resetSays`             | La línea a media pantalla que ven los jugadores mientras `/rdplserver reset` o el final de una ronda devuelve el mapa a su estado original. Vacía reinicia en silencio                                                                                                                                                                                                                                                                                                                                                                                                                                                    | `Pack requested map reset`                                                       |
| `resetSendsTo`          | Adónde se lleva a los jugadores en un reinicio: `spawn`, una posición como `x,y,z` o `x,z`, donde la altura es una por encima del nivel del mar, o cualquiera de ellas tras `dimension:` para enviarlos a otro mundo, la dimensión por id o como el `0`, `-1` o `1` de 1.12.2, que es como un reinicio deja a todos en un vestíbulo en lugar de de vuelta en la arena                                                                                                                                                                                                                                                      | `spawn`                                                                          |
| `resetRuns`             | Una función que se ejecuta tras un reinicio que ha limpiado el mapa, nombrada `namespace:path`. Esto es lo que vuelve a construir la arena, ya que un pack que creó su mapa con una función puede simplemente ejecutarla por segunda vez. Vacía no ejecuta nada                                                                                                                                                                                                                                                                                                                                                           | vacío                                                                            |
| `resetClearsEntities`   | Elimina toda entidad que no sea un jugador. Mobs, ítems soltados y experiencia desaparecen, lo que deja el mapa como empezó                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              | `true`                                                                           |
| `resetClearsScores`     | Devuelve a cero todo objetivo que guarda el pack, para que una partida nueva empiece desde cero. Los propios equipos se conservan                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | `true`                                                                           |
| `resetClearsInventory`  | Vacía el inventario de cada jugador, armadura y mano secundaria incluidas, para que una ronda empiece con lo que reparte el mapa y no con lo que dejó la anterior. El `gives` de un bando se reparte de nuevo justo después                                                                                                                                                                                                                                                                                                                                                                                               | `false`                                                                          |
| `resetClearsExperience` | Devuelve la experiencia de cada jugador al nivel cero                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | `false`                                                                          |
| `spawnChunkRadius`      | A qué distancia del punto de aparición, en bloques, se mantienen cargados los chunks haya o no un jugador allí, redondeado a chunks enteros: `(blocks + 8) / 16` en cada sentido, así que el `128` por defecto mantiene 8. Al iniciarse un mundo, el overworld prepara un cuadrado 4 chunks más ancho en cada sentido que eso antes de que el servidor esté listo. `0` no prepara ni mantiene ninguno. RDPL los mantiene con sus propios tickets de chunk, así que en 1.21.1 la regla de juego `spawnChunkRadius` no hace nada mientras esta clave esté en vigor                                                                | Mantener en marcha una máquina o una granja en el punto de aparición, o desactivar los chunks de aparición con `0` |
| `spawnChunkRadii`       | Un radio para el overworld escrito como `dimension=blocks`, como en `minecraft:overworld=64`, que sustituye a `spawnChunkRadius`. Solo el overworld tiene chunks de aparición, así que una entrada para cualquier otra dimensión no cambia nada                                                                                                                                                                                                                                                                                                                                                                           | Dimensionar la zona de aparición en un pack que fija sus radios por dimensión    |

---

# Modos de juego

## Introducción al mundo

*modos de juego*

`<namespace>/worldintro/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta. Se ejecuta toda introducción que incluya un pack, en el orden de los packs.

Muestra una serie de páginas cuando un jugador entra en el mundo, antes de que tome el control. Texto que se desplaza sobre una imagen, una pantalla de título, una presentación de imágenes, o las tres seguidas.

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

| Clave      | Obligatoria | Valor                              | Por defecto | Qué hace                                                      |
| ---------- | ----------- | ---------------------------------- | ----------- | ------------------------------------------------------------- |
| `pages`    | sí          | lista de páginas                   | ninguno     | Se muestran en orden. Un archivo sin páginas se rechaza con un error |
| `once`     | no          | boolean                            | `false`     | Se reproduce una vez por jugador y mundo en lugar de en cada entrada |
| `music`    | no          | nombre de evento de sonido         | ninguno     | Una pista para toda la secuencia, iniciada con la primera página |
| `requires` | no          | lista de ids de mod o espacios de nombres de pack | ninguno | La introducción se omite salvo que estén presentes todos |

### Páginas

*introducción al mundo*

| Clave         | Obligatoria | Valor                 | Por defecto                      | Qué hace                                                                                                                                                                                                             |
| ------------- | ----------- | --------------------- | -------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `mode`        | no          | `scroll` o `static`   | `scroll`                         | Texto que se mueve, o texto que permanece quieto hasta que el jugador avanza                                                                                                                                         |
| `text`        | no          | ruta a un archivo `.txt` | ninguno                       | Las palabras. Omítelo para una página que son solo imágenes                                                                                                                                                          |
| `background`  | no          | ruta de textura       | el fondo de tierra en mosaico    | Un fondo                                                                                                                                                                                                             |
| `backgrounds` | no          | lista de rutas de textura | ninguno                      | Varios, en ciclo. Se suma a `background` si das ambos                                                                                                                                                                |
| `interval`    | no          | segundos              | `5.0`                            | Cuánto se mantiene cada fondo, cuando hay más de uno                                                                                                                                                                 |
| `time`        | no          | segundos              | calculado a partir del texto     | Cuánto dura una página con desplazamiento, de principio a fin. En una página fija, o en la última página de cualquier clase, es el tiempo hasta que la página avanza sola, y sin él esperan al botón                  |
| `direction`   | no          | `up` o `down`         | `up`                             | En qué sentido se desplaza el texto                                                                                                                                                                                  |
| `textScale`   | no          | número                | `1.0`                            | Multiplica el tamaño de la fuente. Una página `static` ajusta su texto al ancho de la pantalla, menos un margen a cada lado, y cuando aun así pasaría por debajo de los botones su texto se dibuja más pequeño, hasta la mitad, hasta que cabe |
| `settle`      | no          | boolean               | `false`                          | Termina con la última línea centrada en lugar de salir del todo de la pantalla                                                                                                                                       |

### Texto y tiempos

*introducción al mundo*

Los archivos de texto van en `assets/<namespace>/texts/*.txt`. Texto sin formato, un párrafo por línea, y las líneas en blanco se conservan como líneas en blanco. Un archivo `.md` se lee igual, y ambas clases admiten el formato de abajo. `PLAYERNAME` se sustituye por el nombre del jugador, la misma sustitución que usa el poema del End de vanilla.

`time` fija cuánto dura la página, así que la misma página tarda lo mismo tenga una línea o veinte. Ajusta la velocidad de lectura según cuánto pongas en la página. Si omites `time`, la página avanza a la misma velocidad que los créditos de vanilla, donde más texto simplemente tarda más.

### Formato de texto

*introducción al mundo*

Los textos de introducción admiten Markdown. Un archivo sin marcas se muestra exactamente como el texto sin formato.

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

| Marca             | Se escribe como                                           | Se muestra como                                                                                                                                                                                                                                                        |
| ----------------- | --------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Encabezado        | `# `, `## `, `### ` al principio de una línea             | En negrita y más grande: el doble, una vez y media y una vez y cuarto el tamaño del texto, alineado como el texto del cuerpo                                                                                                                                           |
| Negrita           | `**text**`                                                | El corte en negrita de la fuente                                                                                                                                                                                                                                       |
| Cursiva           | `*text*`                                                  | El corte en cursiva de la fuente                                                                                                                                                                                                                                       |
| Negrita cursiva   | `***text***`                                              | El corte en negrita, inclinado                                                                                                                                                                                                                                         |
| Tachado           | `~~text~~`                                                | Con una línea que lo atraviesa                                                                                                                                                                                                                                         |
| Código            | `` `text` ``                                              | Teñido de aguamarina                                                                                                                                                                                                                                                   |
| Enlace            | `[text](url)`                                             | Solo el texto, subrayado; no se puede pulsar                                                                                                                                                                                                                           |
| Rúnico            | `{runic}text{/runic}`                                     | El texto en el cifrado rúnico, `resourcedatapackloader:rdpl_runic`, mientras el resto de la línea conserva su fuente; la negrita y la cursiva dentro de él toman los cortes en negrita y cursiva del cifrado. Funciona en encabezados, elementos de lista y citas, y un `{runic}` sin cerrar se muestra tal como está escrito |
| Viñeta            | `- ` o `* ` al principio de una línea                     | Una viñeta, con las líneas ajustadas sangradas bajo el texto; dos espacios antes de la marca la anidan un nivel                                                                                                                                                        |
| Numerado          | `1. ` al principio de una línea                           | El número tal como está escrito, sangrado del mismo modo                                                                                                                                                                                                               |
| Cita              | `> ` al principio de una línea                            | Sangrada y atenuada                                                                                                                                                                                                                                                    |
| Línea horizontal  | `---` en una línea propia                                 | Una línea horizontal a lo ancho del texto                                                                                                                                                                                                                              |
| Imagen            | `![alt](namespace:textures/....png)` en una línea propia  | La imagen, reducida al ancho del texto y conservando su proporción; el texto alternativo se muestra si no se puede leer                                                                                                                                                |
| Escape            | `\` antes de una marca, p. ej. `\*`                       | La marca como carácter normal                                                                                                                                                                                                                                          |

Las tablas y los bloques de código delimitados (entre líneas de ```) se dibujan como texto sin formato, con sus marcas y todo. Tanto el tiempo calculado de una página con desplazamiento como el ajuste de tamaño de una página fija cuentan la altura ya maquetada, imágenes incluidas. Los títulos y líneas de las tarjetas, los mensajes Says y las notas de bienvenida y de retención admiten las marcas en línea desde la negrita hasta el rúnico, una línea cada uno.

### Cómo se juega

*introducción al mundo*

Una página con desplazamiento pasa a la siguiente cuando se acaba su tiempo. La última página nunca avanza sola, espera. En la parte inferior hay **Next Page** y **Skip All**, o un solo **Continue to World** en la última página. Escape hace lo mismo que Skip All. Las páginas fijas centran todas las líneas. Las páginas con desplazamiento mantienen una columna fija, como hacen los créditos.

En un jugador, el mundo se pausa tras la introducción, así que nada se acerca sigilosamente al jugador mientras lee. La única excepción es el terreno que aún se está creando cuando se abre la introducción: entonces la creación continúa tras las páginas, y el jugador permanece retenido como espectador hasta que continúa al mundo, aunque la ejecución termine antes. En un servidor el mundo sigue en marcha, y un cliente vanilla nunca ve la introducción y entra con normalidad. El saludo de bienvenida espera hasta que se cierran las páginas, para que no se pierda tras ellas.

`once` se recuerda en los datos guardados del jugador y sobrevive a la muerte. `/rdplserver intro` lo borra para quien lo ejecuta, de modo que la introducción se reproduce de nuevo la próxima vez que entre. No se repite en el acto, lo que evita que sea una vía de vuelta a la secuencia de entrada en mitad de una partida.

Los fondos se estiran para llenar la ventana, así que una imagen 16:9 sirve para una ventana 16:9 y una cuadrada se ve aplastada. Recorta la imagen a la forma en lugar de confiar en el ajuste. `music` admite cualquier evento de sonido registrado, de vanilla o uno que tu propio pack añada mediante `sounds`. No se repite en bucle, así que una pista corta termina y deja silencio tras ella.

Si más de un pack incluye una introducción, sus páginas se ejecutan una tras otra en el orden de los packs en lugar de ganar una. Restríngelas con `requires` si solo quieres una.

## Equipos

*modos de juego*

`<namespace>/teams/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se apilan. Cada archivo es un bando.

Un bando es un equipo real en el marcador del propio juego, así que `/team list` lo ve, conserva a sus miembros a través de un guardado y una recarga, y un cliente sin este mod muestra los colores y las etiquetas de nombre igual que con cualquier equipo vanilla. La pertenencia es por nombre, de modo que cualquier cosa con nombre o UUID puede estar en un bando: un jugador, un zombi, un aldeano, un soporte para armadura.

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

| Ajuste        | Tipo    | Por defecto       | Qué hace                                                                                                                                                                                                                                           |
| ------------- | ------- | ----------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`        | texto   | el nombre del archivo | El nombre del equipo en el marcador, de 1 a 16 caracteres. Es lo que usan `/team` y los demás archivos                                                                                                                                         |
| `displayName` | texto   | el nombre         | Lo que se muestra a los jugadores en lugar del nombre                                                                                                                                                                                              |
| `color`       | texto   | `white`           | Uno de los dieciséis colores de texto. Tiñe la etiqueta de nombre y es de lo que dependen las ranuras de barra lateral por equipo                                                                                                                  |
| `prefix`      | texto   | vacío             | Se antepone al nombre de un miembro, después del color                                                                                                                                                                                             |
| `suffix`      | texto   | vacío             | Se pospone al nombre de un miembro                                                                                                                                                                                                                 |
| `scoreboard`  | boolean | `true`            | Si el bando figura como equipo en el marcador del juego. Desactivado, no crea ningún equipo: sus mobs llevan el color del bando en el nombre, nada les impide pelear entre sí y no se le asignan puntos, ya que la puntuación va por equipo       |

Un bando solo se pone en juego donde un pack lo pide: sin carpeta `teams` en ninguna parte el mod no añade ningún equipo, no escucha nada y no ofrece el comando. Un operador de servidor que edita un archivo puede ejecutar `/rdplserver reload` para poner el cambio en el mundo en marcha sin reiniciar.

### Combate y visibilidad

*equipos*

| Ajuste                  | Tipo    | Por defecto    | Qué hace                                                                                                                                                             |
| ----------------------- | ------- | -------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `friendlyFire`          | boolean | `false`        | Si los miembros pueden hacerse daño entre sí. También es el valor por defecto de `mobFriendlyFire`                                                                   |
| `mobFriendlyFire`       | boolean | `friendlyFire` | Si los mobs de un bando pueden dañar a su propio bando con explosiones y TNT lanzado, algo que el juego por sí solo nunca impide. Desactivado protege al bando; activado lo deja como lo tiene el juego |
| `seeFriendlyInvisibles` | boolean | `true`         | Si los miembros se ven entre sí estando invisibles                                                                                                                   |
| `nameTags`              | texto   | `always`       | `always`, `never`, `hideForOtherTeams` o `hideForOwnTeam`, con cualquier combinación de mayúsculas y minúsculas                                                      |
| `deathMessages`         | texto   | `always`       | Las mismas cuatro palabras, para quién recibe el aviso cuando muere un miembro                                                                                       |
| `collision`             | texto   | `always`       | `always`, `never`, `pushOtherTeams` o `pushOwnTeam`, con cualquier combinación de mayúsculas y minúsculas                                                            |

### Quién se une

*equipos*

| Ajuste      | Tipo    | Por defecto | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| ----------- | ------- | ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `entities`  | lista   | vacío       | Ids de entidad cuyas apariciones se unen todas a este bando, como `minecraft:zombie` o una de las tuyas                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `players`   | lista   | vacío       | Nombres de jugador que se unen a este bando al iniciar sesión                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `spawnBox`  | lista   | ninguno     | Seis números enteros, x y z a x y z. Todo lo que aparezca dentro se une, y las esquinas pueden darse en cualquier orden                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `joinable`  | boolean | `true`      | Si un jugador puede unirse con `/rdplserver team join`. Ponlo en false para un bando que es solo de mobs                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `balance`   | boolean | `false`     | Si `/rdplserver team join` sin nombre puede poner aquí a un jugador. Entre los bandos que lo permiten se elige el que tenga menos jugadores                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `picks`     | número  | `0`         | Cuántos miembros elige este bando al azar. En cada ronda abierta el bando deja volver a donde estaban a quienes eligió la última vez y elige de nuevo entre todo lo que nombra `picksFrom`; entre sorteos, un inicio de sesión o una aparición de ese conjunto ocupa de inmediato un puesto vacío. Para eso sirve: un jugador entre todos, en un bando propio                                                                                                                                                                                                                                                                                                                                                                      |
| `picksFrom` | lista   | vacío       | De qué se hace el sorteo: `players` para todos los conectados, e ids de entidad para cada mob vivo de esa clase                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `standIn`   | objeto  | ninguno     | Un mob que ocupa el bando mientras no haya ningún jugador en él: `{ "entity": "mypack:herobrine", "at": "23,31,0" }` mantiene vivo uno de esa entidad en ese punto del overworld, invocándolo cuando falta, y lo retira en el momento en que un jugador se une al bando, de modo que una partida se juega contra la IA hasta que un jugador toma el papel. Se comprueba cada cinco segundos; el punto debe estar en terreno cargado. En una partida con vestíbulo (`opens.by: leader`) un sustituto solo se invoca mientras el vestíbulo espera y al abrirse la ronda, así que uno que cae permanece ausente durante el resto de la ronda y su final hasta que todos vuelven al vestíbulo; sin vestíbulo, un sustituto caído no se reemplaza mientras se desarrolla una ronda que termina con `ends.lastStanding` |

Hay tres formas de unirse, y un bando puede usar las tres. `entities` nombra ids de entidad, y todo lo de ese tipo se une al aparecer, que es como un pack da bandos a los mobs sin tocar los mobs. `spawnBox` reclama un rincón del mundo, y todo lo que aparezca dentro se une, lo que conviene a una arena donde ambos bandos usan el mismo mob. `players` nombra jugadores directamente. Además de esas, un jugador puede unirse con `/rdplserver team join <name>` salvo que el bando ponga `joinable` en false, y salir con `/rdplserver team leave`.

### Kit inicial y punto de aparición

*equipos*

| Ajuste  | Tipo  | Por defecto | Qué hace                                                                                                                                                                                                                                                                                                |
| ------- | ----- | ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `gives` | lista | vacío       | Ítems que se colocan en el inventario de un jugador al unirse al bando, un nombre de ítem para uno o `{ "item", "count", "unbreakable" }` para más, o para uno que nunca se desgasta, en cualquier ranura libre y soltados a sus pies cuando no hay ninguna. Se reparten de nuevo tras un reinicio que vacía los inventarios (`resetClearsInventory`) |
| `spawn` | texto | ninguno     | `x,y,z` en el overworld donde se coloca a los jugadores del bando al abrirse una ronda, para que cada bando empiece en su propio terreno; sin él se quedan donde los dejó el reinicio o el vestíbulo                                                                                                    |

### El líder

*equipos*

| Ajuste     | Tipo  | Por defecto                        | Qué hace                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| ---------- | ----- | ---------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `lead`     | texto | `none`                             | Cómo se elige al líder del bando: `none`, `first` para quien se unió antes al bando entre los conectados, de modo que pasa siguiendo el orden de incorporación mientras uno está ausente y vuelve con él; se les avisa al llegar, tras la introducción y cualquier retención, y de nuevo cuando pasa a ellos, `topScore` para quien esté más alto en el objetivo que nombra `leadOn`, `appointed` para el jugador que nombra `leadIs`, `vote` para quien voten los miembros, o `claim` para quien lo reclame primero. Un líder es una etiqueta y un color y nada más: no concede ningún poder, así que un líder que se desconecta no rompe nada |
| `leadOn`   | texto | vacío                              | Con `topScore`, el objetivo por el que se clasifica a los miembros. Se calcula de nuevo cada vez que se lee, así que sigue a la puntuación                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `leadIs`   | texto | vacío                              | Con `appointed`, el jugador que lidera                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `leadSays` | texto | `You are the current round leader` | Se le dice a un jugador cuando el liderazgo le llega: al llegar a un bando que lidera, al reclamarlo, o cuando un liderazgo `first` pasa a él, en cuyo caso indica quién se fue. `{side}` es el nombre visible del bando; vacío no avisa de nada                                                                                                                                                                                                                                                                                                                                                  |
| `leadRuns` | texto | vacío                              | Una función, `namespace:path`, que se ejecuta una vez cada vez que el liderazgo pasa a un jugador: el primer líder y cada traspaso posterior. Se ejecuta como el líder, en su posición, con el permiso que tiene una función que otorga un logro, de modo que `@s` es el líder. Se comprueba cada segundo; si el líder está desconectado, se ejecuta para él la próxima vez que se conecte. Un reinicio decide el liderazgo de nuevo                                                                                                                                                                  |

## Puntuación

*modos de juego*

`<namespace>/scoring/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se apilan. Cada archivo es un objetivo.

Un objetivo es un objetivo real en el marcador del propio juego, así que `/scoreboard players list` lo lee y conserva sus puntuaciones a través de un guardado. `criterion` es lo que el juego cuenta por sí mismo: `dummy` para una puntuación que solo mueve este pack, o `deathCount`, `playerKillCount`, `totalKillCount`, `health`, `air`, `armor`, `food`, `level`, `xp`, `trigger`, o cualquier estadística escrita como la admite `/scoreboard`, como `minecraft.custom:minecraft.jump`. Una estadística de 1.12.2 como `stat.jump` se lee como aquella en la que se convirtió.

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

### El objetivo

*puntuación*

| Ajuste        | Tipo    | Por defecto             | Qué hace                                                                                                                                            |
| ------------- | ------- | ----------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`        | texto   | el nombre del archivo   | El nombre del objetivo en el marcador, de 1 a 16 caracteres                                                                                         |
| `displayName` | texto   | el nombre               | Lo que se muestra a los jugadores en lugar del nombre                                                                                               |
| `criterion`   | texto   | `dummy`                 | Lo que el juego cuenta por sí mismo. Uno desconocido se rechaza con una línea que lo indica                                                         |
| `display`     | texto   | vacío                   | `sidebar`, `list`, `belowName` (también se admite `below_name`) o `sidebar.team.<color>`. Vacío no lo muestra en ningún sitio; no hay pantalla de marcador que abrir |
| `render`      | texto   | el propio del criterio  | `integer` o `hearts`                                                                                                                                |
| `teamTotals`  | boolean | `true`                  | Los puntos se anotan en una fila con el nombre del equipo del miembro                                                                               |
| `individuals` | boolean | `false`                 | Los puntos se anotan también en una fila del propio miembro                                                                                         |
| `carries`     | boolean | `false`                 | El objetivo sobrevive a un reinicio del mapa en lugar de borrarse con él. Un recuento de rondas ganadas de una partida es un ejemplo               |
| `awardsTo`    | texto   | vacío                   | Otro objetivo al que este concede un punto al terminar, al bando que iba en cabeza. La clasificación por niveles no concede nada                    |

### Puntos

*puntuación*

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `points.kill`    | objeto | vacío   | Id de entidad a puntos, que se acreditan al bando del autor de la muerte. `minecraft:player` puntúa la muerte de un jugador |
| `points.death`   | entero    | `0`     | Puntos cada vez que muere un miembro, muera como muera. Pueden ser negativos |
| `points.ownKill` | entero    | `0`     | Puntos por una muerte causada a un miembro del propio bando del autor, en lugar del valor de `kill`. Con 0 no puntúa nada; un número negativo es una penalización |

`points` es lo que este mod suma a lo que cuenta el juego, y se vuelca en el mismo objetivo para que `/scoreboard` lo siga leyendo. `kill` vale tantos puntos por cada id de entidad abatida, acreditados al bando del autor; `death` vale tantos cada vez que muere un miembro de un bando, y puede ser negativo. Con `teamTotals` los puntos van a una fila con el nombre del equipo, lo que permite que la barra lateral muestre cuatro bandos en lugar de una fila por cada mob. `individuals` añade además una fila por miembro, y está desactivado por defecto porque una fila por cada UUID de mob solo hace ruido.

### Cómo termina una ronda

*puntuación*

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `ends.atScore`      | entero     | `0`                                | La partida termina en el momento en que un bando alcanza esta puntuación. Con 0 nunca termina por puntuación |
| `ends.afterMinutes` | entero     | `0`                                | La partida termina pasados estos minutos. Con 0 nunca termina por tiempo |
| `ends.afterRounds`  | entero     | `0`                                | Para un objetivo que otro concede con `awardsTo`: la partida termina cuando se han concedido tantas rondas en total, las haya ganado quien las haya ganado. Con 0 nunca termina por rondas |
| `ends.lastStanding` | booleano | `false`                            | La ronda termina cuando solo queda un bando en pie. Los bandos en juego son los que tienen un jugador o un mob vivo cuando se abre la ronda, dos como mínimo; un jugador que muere queda eliminado y vuelve como espectador hasta que acaba la ronda, y un bando cuyos jugadores están todos eliminados o ausentes y cuyos mobs están todos muertos ha caído. El bando que queda en pie se lleva la ronda, y `awardsTo` la registra para ese bando sea cual sea la puntuación. Con `resets` y `opens.by: leader` el juego vuelve entonces al vestíbulo. El `standIn` de un bando no se vuelve a invocar mientras dure una ronda así |
| `ends.outSays`      | texto    | `You are out until the round ends` | Lo que se le dice a un jugador eliminado. Vacío no dice nada |
| `ends.locksTeams`   | booleano | `true`                             | Unirse a un bando mientras hay una ronda en curso espera a que la ronda termine, de modo que nadie se incorpora a una ronda puntuada ya empezada |

`ends` pone fin a la partida, ya sea en el momento en que un bando alcanza `atScore` o cuando han pasado `afterMinutes`. Entonces se muestra la clasificación, ordenada por el propio juego: como chat o, si `results` lo pide, como tarjeta. A un jugador sin este mod se le dice la misma clasificación en líneas de chat, así que nadie se queda sin resultado. Con `resets`, ese final es el de una ronda: la clasificación permanece durante `intermissionSeconds` mientras una cuenta atrás corre en la barra de acción, el mapa se reinicia al estado de la bienvenida y la siguiente ronda se abre tras una cuenta de cinco segundos. `awardsTo` concede la ronda al bando que iba en cabeza, en un objetivo que se `carries` a través del reinicio. Un objetivo acumulado puede terminar por sí solo (`atScore` para un mejor de varias, `afterRounds` para un número fijo) y su clasificación se borra en el reinicio siguiente, de modo que se abre una partida nueva.

### Entre rondas

*puntuación*

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `ends.resets`              | booleano | `false`                       | Al terminar la ronda se reinicia el mapa, tal como describen `resetSays` y los demás ajustes de reinicio de [Plantillas de mundo](#plantillas-de-mundo), y luego se abre una ronda nueva |
| `ends.intermissionSeconds` | entero     | `10`                          | Cuánto tiempo permanece la clasificación entre el final y el reinicio |
| `ends.intermissionSays`    | texto    | `Round cooldown {seconds}`    | Se muestra en la barra de acción cada segundo del descanso posterior al final de una ronda, con `{seconds}` contando hacia atrás hasta el reinicio. Vacío no muestra nada |
| `ends.startsSays`          | texto    | `Round starting in {seconds}` | Se muestra en la barra de acción durante la cuenta de cinco segundos que abre la ronda siguiente tras el reinicio, con `{seconds}` contando hacia atrás. Vacío no muestra nada |

### El vestíbulo

*puntuación*

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `opens.by`         | texto    | `auto`                                             | `auto` abre la ronda siguiente por sí solo, cinco segundos después del reinicio. `leader` mantiene el juego en un vestíbulo: tras el reinicio, y cuando el mundo se carga por primera vez, no se puntúa nada y no corre ningún reloj, los bandos pueden unirse y abandonarse libremente, y la ronda solo se abre cuando el líder de un bando, o un operador, ejecuta `/rdplserver round start`, y no mientras alguien siga leyendo la introducción del mundo; entonces corre la cuenta de cinco segundos, se hacen los sorteos y cada bando es colocado en su `spawn`. Mientras el mundo espera, hasta que termina la cuenta de cinco segundos, los jugadores se quedan donde están y no pueden romper, colocar, usar, golpear ni soltar nada ni reciben daño, y se les muestra la línea de espera cuando lo intentan, y todo lo demás que está vivo permanece quieto: sin IA, sin movimiento. Los comandos siguen funcionando, de modo que se puede uno unir a los bandos y empezar la ronda |
| `opens.says`       | texto    | `Waiting for {leader} to start the round`          | Se muestra en mitad de la pantalla, como la bienvenida, a cada jugador que no es líder: al llegar al vestíbulo tras la introducción, una vez mostrada la bienvenida; cuando el vestíbulo se abre de nuevo tras una ronda; cada vez que cambia, al llegar o irse un líder; y cuando intentan algo que el vestíbulo rechaza. `{leader}` son los líderes de todos los bandos, o `a leader` mientras nadie lidera. Vacío no muestra nada |
| `opens.leaderSays` | texto    | `Type /rdpl round start`                           | Se muestra del mismo modo y en los mismos momentos a un jugador que lidera un bando, en lugar de `opens.says`. Vacío no muestra nada |
| `opens.lobby`      | texto    | ninguno                                            | `x,y,z` en el Overworld, o `dimensión:x,y,z` en otro mundo, como `minecraft:the_nether:0,64,0`, donde todos esperan mientras el vestíbulo está activo: cada jugador, y cada mob vivo de un bando, se coloca en un anillo alrededor de ese punto, mirando hacia su centro, de modo que quedan mirándose unos a otros. A cada uno se le da un arco tan ancho como él más dos bloques, para que ninguno se solape con otro, y el anillo crece a medida que llegan más; se vuelve a trazar cada vez que alguien se une a él o lo abandona. La altura es la del suelo sobre el que se colocan, buscado en un margen de tres bloques arriba o abajo. Jugadores y mobs pasan a ese mundo y vuelven directamente, sin construir ningún portal. Cuando se abre la ronda, los jugadores van al `spawn` de su bando, y un mob que sigue en pie es devuelto a donde estaba, en su propio mundo |
| `opens.lobbyJoins` | booleano | `false`                                            | Coloca en el vestíbulo, como espectador, a un jugador que inicia sesión a mitad de ronda hasta que la ronda termina, en lugar de donde cerró sesión. Requiere `opens.lobby` |
| `opens.joinsSays`  | texto    | `Round is in progress, you can join after it ends` | Lo que se les dice. Vacío no dice nada |

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

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `reset.lead`            | texto | `none`                                                                                    | Lo que hace `/rdplserver round reset` por el líder de un bando mientras hay una ronda en curso. `now` termina la ronda al instante y reinicia el mapa; `vote` convoca una votación; `none` no da al líder voz propia, de modo que el líder convoca una votación como cualquier otro jugador allí donde `players` lo permite. Un operador siempre reinicia al instante |
| `reset.players`         | texto | `none`                                                                                    | `vote` permite que un jugador de cualquier bando convoque una votación con `/rdplserver round reset`. `none` deja el reinicio en manos del líder |
| `reset.teams`           | lista | vacío                                                                                     | Los bandos cuyos jugadores pueden convocar una votación. Vacío significa todos los bandos |
| `reset.passPercent`     | entero  | `51`                                                                                      | La proporción de votantes, de 1 a 100, que deben votar sí para que se reinicie la ronda. `51` es más de la mitad, `100` es todos |
| `reset.voteSeconds`     | entero  | `30`                                                                                      | Cuánto dura una votación, cinco segundos como mínimo. Se cierra antes en cuanto el resultado es seguro |
| `reset.cooldownSeconds` | entero  | `60`                                                                                      | Cuánto tiempo tras una votación fallida hasta que se puede convocar otra. Un líder con `now` no se ve frenado por ello |
| `reset.leadSays`        | texto | `{player} reset the round`                                                                | Se dice a todos cuando la ronda se reinicia al instante, siendo `{player}` quien la reinició. Vacío no dice nada |
| `reset.voteSays`        | texto | `{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds` | Se dice a todos cuando se convoca una votación, siendo `{player}` quien la convocó. Vacío no dice nada |
| `reset.tallySays`       | texto | `Reset the round? {yes} yes, {no} no, {seconds}`                                          | Se muestra en la barra de acción cada segundo de una votación, con `{seconds}` contando hacia atrás. Vacío no muestra nada |
| `reset.passSays`        | texto | `The vote passed, so the round is reset`                                                  | Se dice a todos cuando una votación sale adelante. Vacío no dice nada |
| `reset.failSays`        | texto | `The vote failed, so the round goes on`                                                   | Se dice a todos cuando una votación fracasa. Vacío no dice nada |

Un reinicio corta la ronda donde esté. La clasificación se muestra bajo `The round was reset`, nadie se lleva la ronda, el descanso cuenta hacia atrás y el mapa se reinicia como si la ronda hubiera terminado con `ends.resets`, de vuelta al vestíbulo donde `opens.by` es `leader`. Funciona tanto si la ronda terminaría por sí sola como si no, pero no en el vestíbulo, ni durante la cuenta que abre una ronda, ni una vez terminada la ronda y con su reinicio en camino; una votación que siga en curso entonces se descarta.

Todos los jugadores conectados de un bando votan, sea cual sea su bando, con `/rdplserver round vote yes` o `no`, y pueden cambiar su voto mientras dura la votación. Quien convoca la votación ha votado sí, y un jugador que no ha votado cuando se acaba el tiempo cuenta como no. En un pack con bandos, un jugador que no está en ninguno ni convoca ni vota; en un pack sin bandos, lo hacen todos los jugadores conectados. Se usa el primer archivo de puntuación cuyo `reset` permita reiniciar a alguien.

### Resultados

*puntuación*

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `results.card`       | booleano | `false`                | Muestra la clasificación como tarjeta en lugar de como chat |
| `results.title`      | texto    | el nombre y `results`  | El encabezado de la tarjeta |
| `results.icon`       | texto    | vacío                  | Un ítem dibujado en la tarjeta, p. ej. `minecraft:tnt` |
| `results.image`      | texto    | vacío                  | Una imagen dibujada en la tarjeta en lugar de un ítem |
| `results.background` | texto    | gris pizarra oscuro    | El color de fondo de la tarjeta |
| `results.seconds`    | entero     | `8`                    | Cuánto tiempo permanece la tarjeta, al menos un segundo |

## Asaltos

*modos de juego*

`<namespace>/raids/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se apilan. Cada archivo es un asalto.

Un asalto es el tipo que el juego tiene desde la 1.14, librado sobre una de las aldeas del propio juego. Empieza cuando un jugador con el efecto `omen` está dentro de una aldea: el efecto se retira y aparece una barra de jefe para todos los jugadores a menos de `reach` del centro de la aldea. Pasados `waveDelay` ticks llega la primera oleada en un anillo alrededor de la aldea y avanza hacia el centro, atacando por el camino a jugadores, aldeanos y golems de hierro. Los asaltantes nunca se hieren ni se fijan como objetivo entre sí, así que una flecha perdida o un golpe entre dos de ellos no hace nada. La barra muestra la salud que le queda a la oleada, y cuenta los asaltantes cuando quedan dos o menos. Cuando una oleada ha desaparecido, la siguiente espera `waveDelay` ticks. Cuando ha desaparecido la última oleada y nada ha vuelto en dos segundos, el asalto se gana; cuando todos los aldeanos han muerto o la propia aldea ha desaparecido tras haber llegado una oleada, se pierde. En ambos casos la barra lo anuncia durante treinta segundos, y la función correspondiente se ejecuta como cada jugador al alcance.

Un asalto en curso se guarda con el mundo, y sus asaltantes retoman la marcha tras una recarga. Se detiene sin final en modo pacífico, pasados `timeout` ticks, o cuando no hay ningún punto alrededor de la aldea que pueda recibir una oleada. Una aldea es donde el juego guarda sus puntos de aldea, las camas, los puestos de trabajo y las campanas que reclaman los aldeanos: su centro es el punto medio de esos puntos, alcanza como mínimo 32 bloques a su alrededor, y sus aldeanos son los que están dentro de ese alcance y a cuatro bloques de la altura del centro. El `minecraft:bad_omen` del propio juego inicia primero el asalto del propio juego, por lo que un asalto nombra un efecto propio de su pack.

Mientras una oleada está sobre la aldea, sus aldeanos corren a casa y se quedan allí, como cuando suena la campana del juego. Los asaltantes derriban las puertas de madera que encuentran en su camino para llegar hasta ellos, doce segundos por puerta, en dificultad normal y difícil mientras `mobGriefing` esté activado; las puertas de hierro resisten. Un bloque de tipo `bell` es una campana de aldea esté donde esté, y suena como describe [Campanas](#campanas); el `bell` del asalto nombra cualquier otro bloque que suene como una. Todas las campanas de la aldea suenan cuando llega una oleada, y un bloque nombrado también suena cuando un jugador lo usa: los aldeanos a menos de 48 bloques se esconden durante quince segundos y los asaltantes a menos de 48 bloques brillan durante tres. `minecraft:bell` ya está en las aldeas del juego y se puede nombrar, y además sigue sonando a la manera del juego; el bloque de campana propio de un pack se coloca en la aldea mediante una estructura NBT, como parcela o como pieza central de la plaza.

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

### El asalto

*asaltos*

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `omen`          | nombre de efecto               | ninguno, obligatorio | El efecto que inicia el asalto cuando su portador está dentro de una aldea. Sirve cualquier efecto registrado, incluida una poción propia de un pack |
| `name`          | texto               | `Raid`         | El título de la barra de jefe |
| `color`         | texto               | `red`          | El color de la barra: `pink`, `blue`, `red`, `green`, `yellow`, `purple` o `white` |
| `waves`         | lista de oleadas      | ninguno, obligatorio | Cada oleada es una lista de grupos, y las oleadas llegan por orden |
| `waveDelay`     | entero                | `300`          | Ticks antes de la primera oleada, y entre el final de una oleada y la siguiente |
| `spawnDistance` | entero                | `32`           | A qué distancia del centro de la aldea llega una oleada. Los primeros intentos son al doble de esta distancia, luego a esta, luego dentro de la aldea |
| `reach`         | entero                | `96`           | Los jugadores a esta cantidad de bloques del centro o menos ven la barra, y la función final se ejecuta como ellos. Un asaltante que se desvía dieciséis bloques más allá abandona el asalto |
| `timeout`       | entero                | `48000`        | Ticks pasados los cuales un asalto sin terminar se detiene sin final. `0` nunca lo detiene |
| `sound`         | nombre de sonido         | ninguno           | Se reproduce a cada jugador al alcance, desde el lado por el que llega la oleada, cada vez que llega una oleada |
| `wins`          | función           | ninguna           | Se ejecuta como cada jugador al alcance cuando se gana el asalto |
| `loses`         | función           | ninguna           | Se ejecuta como cada jugador al alcance cuando se pierde el asalto |
| `bell`          | nombre de bloque o lista | ninguno           | Otros bloques que suenan como una campana, cuando un jugador los usa y cada vez que llega una oleada. Un bloque de tipo `bell` suena sin necesidad de nombrarlo |

### Un grupo

*asaltos*

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `entity` | nombre de entidad               | ninguno, obligatorio | Lo que llega. Una variante de entidad conserva todo su comportamiento propio y gana la marcha |
| `count`  | entero o `{ "min", "max" }` | `1`            | Cuántos llegan |

---

## Tarjetas

*modos de juego*

`<namespace>/cards/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se apilan. Cada archivo es una regla, y su id es `<namespace>:<nombre del archivo>`. Una regla espera a un disparador, comprueba su `when` y muestra una tarjeta a su audiencia; también puede ejecutar una función. Nada hace falta en el cliente: un jugador sin el mod recibe una tarjeta de esquina como líneas de chat y una tarjeta central como título.

Cada mensaje que dice el propio mod es una regla integrada, listada más abajo. Un pack cambia una escribiendo un archivo con ese id, `rdpl/cards/<nombre>.json`, que no necesita disparador: lo que omite se queda como está hoy, y `{text}` representa el mensaje que habría dicho el mod. Un pack que no escribe ninguna de ellas ve todos los mensajes como antes.

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

El segundo archivo, guardado como `rdpl/cards/gate_blocked.json`, convierte la línea roja de la barra de acción que muestra un portal cerrado en una tarjeta con un icono y una segunda línea, y la muestra como mucho una vez cada treinta segundos.

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

El tercero saluda a un jugador la primera vez que entra con una tarjeta central sin panel detrás, solo con su texto y la sombra del texto, dibujados con la fuente propia del pack.

### Disparadores

*tarjetas*

| Disparador | Requiere | Se activa cuando |
| --- | --- | --- |
| `command`         | nada              | Se ejecuta `/rdplserver card <rule> [players]`. El comando se salta `when`, `repeat` y `cooldown`, y aun así ejecuta `runs`. Cualquier regla se puede mostrar así, sea cual sea su disparador |
| `first_join`      | nada              | Un jugador entra en el mundo por primera vez |
| `dimension_enter` | `dimension`        | Un jugador llega a esa dimensión |
| `biome_enter`     | `biomes`           | Un jugador entra en uno de esos biomas viniendo de otro sitio |
| `structure_enter` | `structures`       | Un jugador entra en una de esas estructuras desde fuera de ella |
| `advancement`     | `advancement`      | Un jugador consigue ese logro |
| `time_of_day`     | `time`             | El reloj del día pasa por ese tick, de `0` a `23999`, mientras hay jugadores en la dimensión. Un reloj cambiado con un comando o con una cama no cuenta |
| `day`             | nada, o `day`  | Empieza un día nuevo en la dimensión; con `day`, solo ese día |
| `craft`           | `item`             | Un jugador fabrica ese ítem |
| `pickup`          | `item`             | Un jugador recoge ese ítem |
| `kill`            | `entity`           | Un jugador mata a esa entidad, o a la `count`-ésima de ellas |
| `respawn`         | nada              | Un jugador reaparece tras morir |
| `death`           | nada              | Un jugador muere |
| `y_level`         | `below` o `above` | Un jugador baja de esa altura o la supera |
| `play_time`       | `minutes`          | El tiempo de un jugador en el mundo alcanza esa cantidad de minutos, contados desde que se cierra la introducción del mundo, o desde que entra cuando no se le muestra introducción |
| `score`           | `objective`        | La puntuación de un jugador en ese objetivo alcanza `score` |

Bioma, estructura, altura, tiempo de juego y puntuación se comprueban una vez por segundo para cada jugador, y se activan en el cambio de fuera a dentro, nunca en la primera comprobación tras entrar. Una regla `time_of_day` o `day` cuya audiencia no sea `player` se activa una vez para la dimensión en lugar de una vez por cada jugador en ella.

Una tarjeta que se activa mientras un jugador aún tiene abierta la introducción del mundo espera y se muestra cuando la introducción se cierra, sea cual sea su disparador, incluido `command`. Se descarta si el jugador se va antes.

### Ajustes de disparadores

*tarjetas*

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `trigger`            | texto                               | ninguno, obligatorio | Uno de los disparadores de arriba. Una regla integrada no lleva ninguno |
| `dimension`          | texto                               | ninguno           | Un id de dimensión como `minecraft:the_nether`; un id sin espacio de nombres se lee como `minecraft:`. Para `dimension_enter` es la dimensión en la que se entra; para cualquier otro disparador limita la regla a los jugadores de esa dimensión |
| `biomes`             | lista                               | ninguno           | Ids de bioma como `minecraft:desert`, o `#tag` para una etiqueta de bioma como `#minecraft:is_ocean` |
| `structures`         | lista                               | ninguno           | Ids de estructura como `minecraft:village_plains`, o `#tag` para una etiqueta de estructura como `#minecraft:village`, que cuentan mientras el jugador está dentro de una pieza de ellas; o el nombre de una estructura que un pack coloca mediante `structures`, que cuenta dentro de `radius` del lugar donde se colocó |
| `radius`             | entero                                | `32`           | A qué distancia cuenta como dentro de una estructura propia de un pack |
| `advancement`        | texto                               | ninguno           | El id del logro |
| `item`               | texto                               | ninguno           | El ítem, escrito como en el resto de un pack, como `minecraft:diamond_sword` |
| `entity`             | texto                               | ninguno           | El id de la entidad, como `minecraft:zombie` |
| `count`              | entero                                | `1`            | Para `kill`: cuántas muertes hacen falta. La cuenta empieza de nuevo después de que se active la regla |
| `below`, `above`     | entero                                | ninguno           | Para `y_level`: la altura que hay que bajar o superar |
| `time`               | entero                                | `0`            | Para `time_of_day`: el tick del día |
| `day`                | entero                                | ninguno           | Para `day`: el único día en el que se activa. Sin él, todos los días |
| `minutes`            | entero                                | ninguno           | Para `play_time` |
| `objective`, `score` | texto, entero                         | ninguno, `1`      | Para `score`: el objetivo y el valor que hay que alcanzar |
| `requires`           | lista de ids de mod o espacios de nombres de pack | ninguno           | El archivo se omite a menos que estén presentes todos |

### Cuándo

*tarjetas*

`when` contiene condiciones que deben cumplirse todas en el momento en que se activa el disparador.

| Ajuste | Tipo | Qué comprueba |
| --- | --- | --- |
| `biomes`                    | lista      | El jugador está en uno de estos biomas, escritos como para el disparador |
| `timeFrom`, `timeTo`        | entero       | El reloj del día está dentro de esta ventana, que puede pasar de medianoche, como `13000` a `1000` |
| `dayAtLeast`                | entero       | El número de día es al menos este |
| `advancement`               | texto      | El jugador tiene este logro |
| `gameMode`                  | texto      | El jugador está en este modo de juego: `survival`, `creative`, `adventure` o `spectator` |
| `team`                      | texto      | El jugador está en este equipo del marcador |
| `objective`, `scoreAtLeast` | texto, entero | La puntuación del jugador en el objetivo es al menos esta |

### La tarjeta

*tarjetas*

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `title`      | texto    | ninguno             | La primera línea, dibujada más grande en una tarjeta central |
| `lines`      | lista    | ninguno             | Hasta dieciséis líneas. Una regla necesita título o líneas, salvo una integrada. Se rellenan `{player}`, `{dim}`, `{biome}` y `{day}`; `{text}` es el mensaje integrado, y en una línea propia da todas las líneas de él |
| `style`      | texto    | `corner`         | `corner` es la tarjeta de la esquina inferior derecha que muestra `saysCard`; `center` es una tarjeta en medio de la pantalla; `chat` son líneas de chat; `bar` es la barra de acción |
| `icon`       | texto    | `saysIcon`       | Un ítem dibujado en una tarjeta de esquina. Vacío no dibuja ninguno |
| `color`      | texto    | `saysColor`      | El color de fondo de la tarjeta en hexadecimal |
| `image`      | texto    | `saysImage`      | Un PNG de los recursos de cliente del pack, estirado sobre la tarjeta como fondo |
| `background` | booleano | `saysBackground` | `false` suprime el panel, su borde y la franja de color; el texto conserva su sombra, y una `image` se sigue dibujando |
| `font`       | texto    | `saysFont`       | La fuente con la que se dibuja el texto de la tarjeta, como `namespace:name`. Vacío usa la fuente RDPL |
| `ticks`      | entero     | `160`            | Cuánto tiempo permanece la tarjeta, desvanecimiento incluido |
| `audience`   | texto    | `player`         | Quién la ve: `player`, `everyone`, `dimension` (todos los de la dimensión del jugador) o `team` (el equipo del marcador del jugador) |
| `repeat`     | texto    | `always`         | `always`, `once_per_player`, `once_per_world` o `once_per_session` (de nuevo después de que el jugador vuelva a iniciar sesión) |
| `cooldown`   | entero     | `0`              | Segundos antes de que la regla se active de nuevo para el mismo jugador |
| `runs`       | texto    | ninguno             | Una función que se ejecuta como el jugador cuando se activa la regla |

Una tarjeta de esquina va al chat cuando `saysCard` está desactivado. Lo que se ha mostrado a un jugador se guarda con el jugador, así que sobrevive a la muerte y al cambio de dimensión; `once_per_world` se guarda con el mundo.

La fuente RDPL, `resourcedatapackloader:rdpl`, es la predeterminada para todo el texto: tarjetas, mensajes Says, las notas de bienvenida y de espera, la introducción del mundo, y los propios menús, chat, HUD y descripciones emergentes del juego. Sus variantes en negrita y cursiva son `resourcedatapackloader:rdpl_bold` y `resourcedatapackloader:rdpl_italic`. La escritura de la mesa de encantamientos sigue siendo la del juego.

RDPL incluye estas fuentes y caracteres. El `font` de una tarjeta, nota o introducción puede nombrar una fuente RDPL por su nombre corto o por su id completo:

| Nombre | Qué dibuja |
| --- | --- |
| `rdpl` (o `resourcedatapackloader:rdpl`)             | La fuente RDPL, con cirílico (U+0400 a U+04FF) y el alfabeto rúnico (U+16A0 a U+16F8) |
| `rdpl_runic` (o `resourcedatapackloader:rdpl_runic`) | Un cifrado de runas: las letras de la A a la Z y de la a a la z se dibujan como runas, y cualquier otro carácter se dibuja con la fuente RDPL. Un fragmento en negrita se dibuja en `rdpl_runic_bold` y uno en cursiva en `rdpl_runic_italic` |
| Runas, U+16A0 a U+16F8                               | Escritas como los propios caracteres rúnicos (ᚠ ᚢ ᚦ ᚨ ᚱ ᚲ), en cualquier texto que dibuje la fuente RDPL, chat incluido; los fragmentos en negrita y cursiva conservan su variante |

La fuente de una tarjeta es una definición de fuente en `assets/<namespace>/font/<name>.json`, en el mismo formato que las fuentes del propio juego, y la tarjeta se dimensiona según los anchos de esa fuente. Un proveedor `bitmap` cuyo `file` es `<namespace>:font/<name>.png` y cuyos `chars` son las dieciséis filas del `ascii.png` del juego lee el mismo PNG que usa la versión 1.12.2 en `assets/<namespace>/textures/font/<name>.png`, de modo que un pack dibuja la misma tipografía en las tres versiones. `minecraft:default` nombra la fuente del juego. Una fuente que ningún pack contiene recurre a la fuente del juego, con un aviso en `rdpl.log`.

Un pack cambia la fuente RDPL incluyendo su propio `assets/resourcedatapackloader/font/rdpl.json`, o los PNG de `assets/resourcedatapackloader/textures/font/` de los que se dibuja; cualquiera de los dos la sustituye en todas partes, texto del juego incluido. El texto del juego usa la fuente RDPL porque el mod incluye `assets/minecraft/font/default.json` con la fuente RDPL primero y las fuentes del propio juego después para todos los demás caracteres. El `assets/minecraft/font/default.json` de un pack se lee antes que el del mod, así que una copia del de vanilla devuelve al texto del juego su propia fuente; el texto propio de RDPL conserva la fuente RDPL a menos que `saysFont` sea `minecraft:default`.

Los títulos y líneas de las tarjetas, los mensajes Says y las notas de bienvenida y de espera admiten las marcas en línea de la tabla de Introducción al mundo, Formato de texto: negrita, cursiva, negrita cursiva, tachado, código, enlaces y fragmentos rúnicos. Un fragmento en negrita se dibuja con la variante `_bold` de la fuente y uno en cursiva con su variante `_italic`; para una fuente sin esa variante el fragmento toma el estilo de negrita o cursiva del juego, y la tarjeta se dimensiona según los fragmentos tal como se dibujan. Los jugadores sin el mod reciben las mismas marcas como formato de chat, y un fragmento rúnico como sus letras normales.

### Reglas integradas

*tarjetas*

| Id | El mensaje | De dónde sale su texto |
| --- | --- | --- |
| `rdpl:gate_unlocked`    | Se abre un portal                                           | `unlockedMessage` en [Portales](#portales) |
| `rdpl:gate_blocked`     | Un portal cerrado hace retroceder a un jugador, en la barra de acción            | `blockedMessage` en [Portales](#portales) |
| `rdpl:team_joined`      | Un jugador se une a un bando                                           | el `displayName` del bando |
| `rdpl:team_lead`        | El liderazgo de un bando pasa a un jugador                                        | `leadSays` en [Equipos](#equipos) |
| `rdpl:team_picked`      | Un jugador es elegido para un bando                                           | el `displayName` del bando |
| `rdpl:team_round_ended` | La ronda terminó, así que un jugador es movido a un bando                 | el `displayName` del bando |
| `rdpl:lobby_joins`      | Un jugador que inicia sesión a mitad de ronda es enviado al vestíbulo             | `opens.joinsSays` en [El vestíbulo](#el-vestíbulo) |
| `rdpl:lobby_note`       | La línea del vestíbulo en mitad de la pantalla                                       | `opens.says`, `opens.leaderSays` en [El vestíbulo](#el-vestíbulo) |
| `rdpl:scoring_results`  | La clasificación al final de una ronda, para cada jugador             | `results.card`, `results.title`, `results.icon`, `results.image`, `results.background`, `results.seconds` en [Resultados](#resultados) |
| `rdpl:scoring_out`      | Un jugador eliminado                                           | `ends.outSays` en [Cómo termina una ronda](#cómo-termina-una-ronda) |
| `rdpl:reset_lead`       | El líder reinicia la ronda                                       | `reset.leadSays` en [Reiniciar una ronda](#reiniciar-una-ronda) |
| `rdpl:reset_vote`       | Se convoca una votación de reinicio                                          | `reset.voteSays` |
| `rdpl:reset_pass`       | La votación sale adelante                                       | `reset.passSays` |
| `rdpl:reset_fail`       | La votación fracasa                                         | `reset.failSays` |
| `rdpl:anvil_waits`      | El trabajo de un yunque espera a un logro                         | [Trabajo en el yunque](#trabajo-en-el-yunque) |
| `rdpl:threat`           | Cambia la franja de amenaza de un jugador                                  | `threatSays` |
| `rdpl:prospect`         | Cada línea que informa un hallazgo de prospección                            | el hallazgo |
| `rdpl:prospect_none`    | La prospección no encontró nada                                       | el archivo de idioma |
| `rdpl:pregen_ended`     | La pregeneración termina o se detiene                                 | `pregenFinishedSays`, `pregenStoppedSays` en [Pregeneración](#pregeneración) |
| `rdpl:pregen_running`   | La línea de progreso que ve un jugador al entrar durante la pregeneración | `pregenRunningSays` |

`welcomeSays` no es una regla y conserva su logotipo; una regla `first_join` o `dimension_enter` se suma a ella. Las cuentas atrás y los recuentos de la barra de acción de una ronda se quedan como los dejan sus ajustes.

---

# Control

## La capa de control

*control*

Todo lo que detiene o cambia la generación está agrupado, y cada grupo tiene una clave en la categoría `control` de la configuración con tres valores:

| Valor | Qué significa |
| --- | --- |
| `default` | Decide el pack. Los valores de la configuración son el respaldo |
| `global`  | Gana la configuración. Las secciones del pack se ignoran |
| `off`     | El grupo se desactiva por completo y ningún pack puede activarlo |

Los grupos son `ores`, `biomes`, `structures`, `spawning`, `bedrock`, `voidWorld`, `recipes`, `terrain`, `replacements`, `villages`, `entities`, `chunks`, `blastPlaster`, `commands` y `server`.

Los ajustes se resuelven **sección de bioma → plantilla de mundo → configuración**. El bloque `settings` de una plantilla de mundo usa los mismos nombres de clave que la configuración, de modo que un pack los establece igual que lo harías tú:

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

Con el control de un grupo en `default`, estos prevalecen; en `global` se ignoran; y en `off` todo el grupo no hace nada digan lo que digan los packs. Una clave que una plantilla nombra y que nada lee se avisa una vez en el registro, y lo mismo una clave en una sección `biomes` que no es un ajuste de aldea.

El archivo de configuración es `config/resourcedatapackloader-common.toml`. Cada clave de abajo lleva el mismo nombre allí, bajo su categoría, y una lista se escribe como la lista TOML que usa el formato de configuración del juego.

## Qué hace cada grupo

*control*

Cada ajuste de abajo se lee a través de su grupo, así que la clave `control` del grupo decide si la última palabra la tiene un pack o la configuración. Un ajuste que un pack puede establecer aparece en el bloque `settings` de una plantilla de mundo con el mismo nombre; uno marcado como **solo configuración** se lee únicamente de la configuración, y si un pack lo escribe se avisa y se ignora. Los valores predeterminados son los de la configuración.

### Minerales

*qué hace cada grupo*

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

`control.ores` decide este grupo. Bloqueo de la generación de minerales por mod y por tipo de mineral.

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `blockOres`                      | booleano         | `false`         | Impide que todos los mods, y el propio Minecraft, generen minerales. Solo siguen generando los mods de oreWhitelist. Un mineral es una característica colocada construida sobre la característica de mineral o de mineral disperso del juego, aquellas para las que 1.12.2 lanzaba su evento de minerales, lo que abarca los minerales de Minecraft y de la mayoría de los mods, la tierra, la grava y las variedades de piedra. Las entradas de worldgen propias de un pack nunca se bloquean, ni por esto ni por oreTypes |
| `logBlockedOres`                 | booleano         | `true`          | Registra la primera vez que se rechaza cada mod y tipo de mineral |
| `oreWhitelist`                   | lista de ids de mod | `["minecraft"]` | Los mods a los que se sigue permitiendo generar minerales mientras `blockOres` está activado |
| `prospectItems`                  | lista            | vacío           | Ítems que prospectan entradas de worldgen en forma de veta cuando un jugador agachado rompe un bloque con uno, como item=entrada\|entrada[,radio en chunks] o item=*[,radio], p. ej. minecraft:compass=iron_vein\|coal_seam o mypack:rod=*,12. La lectura nombra el mineral y un punto cardinal |
| `prospectItemsAreBlacklist`      | booleano         | `false`         | Activado, la lista de cada ítem son las entradas que no lee |
| `prospectDrops`                  | booleano         | `false`         | Activado, un bloque roto en modo prospección sigue soltando drops y dando experiencia. Desactivado, la muestra se consume |
| `prospectSlow`                   | entero, de 1 a 100   | `2`             | Cuántas veces más tarda en romper un bloque un jugador agachado con un ítem etiquetado. `1` es velocidad normal |
| `prospectWear`                   | entero, de 2 a 1000  | `2`             | Cuántas veces el desgaste normal le cuesta a la herramienta una rotura de prospección. `2`, el doble, es lo mínimo permitido, y un ítem sin durabilidad no paga nada |
| `oreTypes`                       | lista            | vacío           | Tipos de mineral a los que se aplica, los genere quien los genere y diga lo que diga la lista blanca. Tipos conocidos: COAL, IRON, COPPER, GOLD, REDSTONE, DIAMOND, LAPIS, EMERALD, QUARTZ, DIRT, GRAVEL, DIORITE, GRANITE, ANDESITE, TUFF, CLAY, SILVERFISH, CUSTOM para cualquier otro mineral |
| `oreTypesAreBlacklist`           | booleano         | `true`          | Activado, los tipos de `oreTypes` son los bloqueados. Desactivado, solo generan esos tipos |
| `blockOreDimensions`             | lista            | vacío           | Las dimensiones a las que se aplica el bloqueo de minerales, y vacío significa todas. Una dimensión fuera del ámbito no se toca en absoluto, de modo que los minerales de otro mod se generan allí mientras el Overworld sigue bloqueado |
| `blockOreDimensionsAreBlacklist` | booleano         | `false`         | Activado, las dimensiones listadas son las que se dejan en paz |

### Biomas

*qué hace cada grupo*

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

`control.biomes` decide este grupo. Bloqueo de biomas por mod y por nombre, y qué los sustituye.

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `logBlockedBiomes`                 | booleano         | `true`                    | Registra un recuento por mod de qué biomas se bloquearon |
| `blockBiomes`                      | booleano         | `false`                   | Impide que se genere cualquier bioma salvo los de los mods de biomeWhitelist. Los biomas bloqueados pasan a ser el bioma vacío, o lo que nombren los roles y el respaldo de la plantilla de mundo. Bloquear todos los biomas mientras la plantilla de mundo es vacía, sin roles y con un valor predeterminado vacío, convierte las voidWorldDimensions en un mundo vacío |
| `biomeWhitelist`                   | lista de ids de mod | `["minecraft"]`           | Los mods cuyos biomas siguen generándose mientras `blockBiomes` está activado. Un bioma de pack usa el espacio de nombres del pack |
| `biomeNames`                       | lista            | vacío                     | Biomas a los que se aplica, por id como minecraft:birch_forest o por el nombre que muestra el juego, como Bosque de abedules. Como lista negra se bloquean los tenga quien los tenga. Como lista blanca, un bioma listado sigue necesitando que su mod esté en biomeWhitelist mientras blockBiomes está activado |
| `biomeNamesAreBlacklist`           | booleano         | `true`                    | Activado, los nombres de `biomeNames` se bloquean. Desactivado, solo se generan esos nombres |
| `blockBiomeDimensions`             | lista            | `["minecraft:overworld"]` | Las dimensiones a las que se aplica el bloqueo de biomas. Vacío significa todas |
| `blockBiomeDimensionsAreBlacklist` | booleano         | `false`                   | Activado, el bloqueo se salta las dimensiones listadas. Desactivado, se aplica solo a ellas |

### Generadores

*qué hace cada grupo*

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

`control.generators` decide este grupo. Bloqueo de la generación del mundo de otros mods por mod y por lo que crea. Un generador es una característica colocada, propiedad del espacio de nombres de su id; las características del propio Minecraft, las de este mod y las entradas de worldgen de un pack nunca se bloquean.

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `blockWorldGenerators`                 | booleano                     | `false`                   | Impide que todos los mods generen mediante sus propias características colocadas, que es como los mods añaden islas de slime, cristales de cueva y cosas así. Solo siguen generando los mods de generatorWhitelist |
| `generatorWhitelist`                   | lista de ids de mod             | `["minecraft"]`           | Los mods a los que se sigue permitiendo generar mientras `blockWorldGenerators` está activado |
| `blockedGenerators`                    | lista de ids de mod o partes de id | vacío                     | Generadores concretos bloqueados sin más, diga lo que diga la lista blanca, por id de mod o por parte del id de una característica colocada |
| `blockGeneratorDimensions`             | lista                        | `["minecraft:overworld"]` | Las dimensiones a las que se aplica el bloqueo de generadores. Vacío significa todas |
| `blockGeneratorDimensionsAreBlacklist` | booleano                      | `false`                   | Activado, el bloqueo se salta las dimensiones listadas. Desactivado, se aplica solo a ellas |
| `generatorTypes`                       | lista                        | vacío                     | Tipos a los que se aplica, sea quien sea el propietario del generador y diga lo que diga la lista blanca: `ores`, `structures`, `flora`, `lakes`, `terrain`, o `unknown` para los que no coincidieron con nada. El tipo sale de palabras del id de la característica, así que `crystal_ore` es ores y `slime_island` es structures |
| `generatorTypesAreBlacklist`           | booleano                     | `true`                    | Activado, los tipos de `generatorTypes` son los bloqueados. Desactivado, solo generan esos tipos |
| `generatorTypeMap`                     | lista de `pattern=type`      | vacío                     | Tipos para los generadores que el id no describe, siendo el patrón un id de mod o parte del id de una característica, p. ej. mymod=ores. Las entradas asignadas se comprueban antes que las palabras integradas, así que también corrigen una que las palabras lean al revés |
| `logBlockedGenerators`                 | booleano                     | `true`                    | Registra cada generador con el tipo que se le dio la primera vez que se bloquea. `/rdplserver generators` muestra los totales acumulados por mod y tipo |

### Sustituciones

*qué hace cada grupo*

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

`control.replacements` decide este grupo. Sustitución de bloques en chunks que ya existen.

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `blockReplacements`                      | lista               | vacío   | Bloques que se cambian en los chunks al cargarse, escritos como bloque=bloque con un estado opcional a cada lado, como minecraft:andesite=minecraft:stone o minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]. Cada chunk se procesa una vez, los nuevos incluidos |
| `blockReplacementDimensions`             | lista               | vacío   | Las dimensiones a las que se aplica. Vacío significa todas |
| `blockReplacementDimensionsAreBlacklist` | booleano            | `false` | Activado, la sustitución se salta las dimensiones listadas. Desactivado, se aplica solo a ellas |
| `blockReplacementMinHeight`              | entero, de -2032 a 2031 | `-64`   | La y más baja que examina |
| `blockReplacementMaxHeight`              | entero, de -2032 a 2031 | `319`   | La y más alta que examina |
| `blockReplacementKey`                    | cadena              | `0000`  | Cámbiala y todos los chunks vuelven a pasar por la sustitución |
| `logBlockReplacements`                   | booleano            | `true`  | Registra la primera vez que se hace cada sustitución, y un total cuando un mundo se pone al día |

### Aldeas y ciudades

*qué hace cada grupo*

`control.villages` decide este grupo. Las calles de ciudad y de aldea que traza un pack: su forma, su ornamento, puentes, túneles, vías, parcelas y plaza.

#### Caminos de aldea

*aldeas y ciudades*

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

**Mezclar bloques.** Algunos ajustes de bloque admiten una mezcla en lugar de un solo bloque: bloques separados por comas, cada uno seguido de un espacio y un peso, como en `"minecraft:stone_bricks 3, minecraft:cobblestone 1"`. Un bloque sin peso cuenta una vez. Cada bloque colocado sortea la mezcla a partir de la semilla del mundo y de su posición, de modo que el mismo mundo siempre construye el mismo patrón. Los ajustes que admiten una mezcla son `villagePathVergeBlock`, `villagePathVergeWaterBlock`, `villagePathTunnelBlock`, `villagePathBridgeFrameBlock`, `villagePathBridgeFrameTopBlock`, `villageRailTunnelBlock`, `villageRailDeckBlock`, `villageRailSupportBlock`, `villageRailBarrierBlock`, `villageRailBridgeFrameBlock`, `villageRailBridgeFrameTopBlock`, `villageSubwayTunnelBlock`, `villageSubwayPlatformBlock`, `villageSubwayRailingBlock`, `villageSubwayBenchEndBlock` y `villageSewerMossBlock`. Todos los demás ajustes de bloque usan el primer bloque de una mezcla en todo momento.

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villagePathBlock`         | texto                  | vacío                  | La superficie de la calzada cuando terrainAdaptation traza calles de ciudad. Vacío mantiene el bloque que usaría el bioma: arenisca sobre arena, terracota sobre badlands, camino de tierra sobre tierra |
| `villagePathExtraWidth`    | entero, 0 o más        | `0`                    | Bloques extra de ancho de calzada a cada lado además de los 3 habituales, cuando terrainAdaptation traza las calzadas. Ensancha las propias calles, de modo que los bloques entre ellas se retiran de las calzadas anchas |
| `villageBlockSizes`        | lista de `size=weight` | vacío                  | Qué profundidad tienen las manzanas entre las calles paralelas de una ciudad, sorteada una vez por distrito a partir de la posición de su plaza. Vacío da a cada manzana el tamaño de la mayor parcela que incluye el pack |
| `villageCitySpacing`       | entero, de 0 a 256         | `16`                   | A qué distancia unos de otros se siembran los distritos de ciudad, en distritos dimensionados a partir de las parcelas (el doble de la mayor parcela, más una plaza y una calle a cada lado, redondeado hacia arriba a 16 bloques, al menos 96): un distrito de cada cuadrado de este tamaño lleva una ciudad, y con 1 cada distrito es una, una plaza con el pozo en su centro y calles que salen de ella y enlazan con las del distrito siguiente. 0 no siembra ninguna. Cuando el pack no lo establece, lo fija `structureSpacing` villages=chunks, de al menos 9 chunks. Un cuadrado funda su ciudad en su distrito más llano, con no más de 10 bloques de desnivel, en un bioma de aldea (`structureBiomes` villages= elige cuáles), y no donde pudiera empezar una mansión del bosque; `structureSeparation`, `structureMinDistanceFromSpawn` y `structureMost` villages= mantienen separadas las ciudades como mantenían las aldeas, y `structureAt` villages=x,z funda ciudades solo donde las fija. Una ciudad crece solo hacia distritos al alcance de su primer pozo cuyo terreno suba no más de 6 bloques y quede por encima de la línea de agua, y no se construye una ciudad de dos o menos parcelas y pozos |
| `villagePathAlleyBlock`    | bloque                 | vacío                  | La superficie de un callejón, una calzada demasiado estrecha para llevar líneas y aceras. Un callejón discurre entre las aceras de las calles con las que se cruza y no lleva ninguna propia, y no se pinta ningún paso de peatones donde se encuentra con una calle. Vacío traza los callejones con el bloque de calzada |
| `villagePathAlleyChance`   | entero, 0 o más        | `0`                    | La probabilidad en porcentaje de que una calle se trace como callejón en lugar de con su ancho completo. 0 no traza callejones |
| `villagePathMinimumWidth`  | entero, 0 o más        | `0`                    | La calle más estrecha permitida. Una calle que se trazaría más estrecha que esto no se traza en absoluto, y el distrito se organiza alrededor del hueco. 0 nunca la rechaza |
| `villagePathFlatRun`       | entero, 0 o más        | `6`                    | Las calles mantienen cada pendiente durante al menos esta cantidad de bloques antes de escalonarse, anclado a las coordenadas del mundo para que los tramos coincidan entre piezas. 0 o 1 deja que una calle se escalone cada bloque |
| `villagePlotsLeast`        | entero, 0 o más        | `0`                    | A cuántas parcelas crece una ciudad: se añaden distritos anillo a anillo alrededor de su centro hasta que contienen al menos esta cantidad, sin superar nunca villagePlotsMost. 0 traza solo el distrito central |
| `villagePlotsMost`         | entero, 0 o más        | `0`                    | El máximo de parcelas que puede tener una ciudad: el crecimiento se detiene antes del distrito que lo superaría y ningún distrito asienta más, y un distrito que lo alcanza omite los callejones a los que no da ninguna parcela. 0 no fija techo |
| `villagePlotsBackRow`      | booleano               | `true`                 | Una vez que la aldea ha crecido, una segunda pasada asienta una parcela justo detrás de cada parcela que da a una calle, girada para mirarla, con el mismo sorteo y la misma prueba de espacio, de modo que el interior de una manzana entre dos calles se construye en lugar de quedar vacío |
| `villageTieStreets`        | booleano               | `true`                 | Activado, un distrito que no puede hacer crecer sus calles hasta la aldea ya existente recibe una calle de enlace recta trazada hasta la calle más cercana con la que se alinea. Desactivado, ese distrito se desmonta |
| `villageLayout`            | texto                  | vacío                  | Un mapa de ciudad trazado en lugar de planificar el distrito, nombrado como mypack:downtown y leído de la carpeta citymaps de ese pack. Vacío planifica el distrito como de costumbre |
| `villagePathCenterBlock`   | bloque                 | vacío                  | Una línea central por el medio de la calzada. Vacío no dibuja ninguna |
| `villagePathCenterDash`    | entero, 0 o más        | `0`                    | Hace discontinua esa línea: N bloques de línea, luego uno de calzada. Anclado a las coordenadas del mundo, así que los trazos de una pieza de calzada continúan en la siguiente. `0` la mantiene continua |
| `villagePathLineBlock`     | bloque                 | vacío                  | Líneas de borde entre la calzada y la acera. Vacío no dibuja ninguna |
| `villagePathSidewalkBlock` | bloque                 | vacío                  | Aceras, trazadas a nivel con la calzada fuera de las líneas de borde. Vacío no traza ninguna |
| `villagePathSidewalkWidth` | entero, 0 o más        | `2`                    | Qué ancho tiene cada acera, una vez establecido `villagePathSidewalkBlock` |
| `villagePathLampBlock`     | texto                  | `minecraft:oak_fence`  | El bloque con el que se construye una farola a lo largo de una calle, apilado villagePathLampHeight de alto sobre el bordillo. Una calle o callejón levanta una en cada extremo, una donde se le une otra calle y una cada 7 a 12 bloques entre medias, en su lado más bajo y en el otro solo donde allí no hay sitio, y una calle sin salida rodea su borde con ellas. Ninguna se levanta en un puente, en un túnel ni a menos de dos bloques de una puerta. Vacío no levanta farolas |
| `villagePathLampHeight`    | entero, 1 o más        | `3`                    | Cuántos bloques de alto se levanta el poste antes de su cabeza |
| `villagePathLampTopBlock`  | bloque                 | `minecraft:black_wool` | La cabeza sobre el poste. Vacío la deja al descubierto |
| `villagePathLampSideBlock` | bloque                 | `minecraft:torch`      | La luz colgada a cada lado de la cabeza, mirando hacia fuera. Vacío no cuelga ninguna |
| `villagePathLampStructure` | texto                  | vacío                  | Un archivo de estructura colocado como la farola entera en lugar de apilar los tres bloques de farola, nombrado `mypack:street_lamp` y leído de la carpeta `structures` de ese pack. Se centra en el punto de la farola con su capa más baja sobre el bordillo, y los bloques que traza quedan retenidos para que nada más los sobrescriba. Vacío apila los bloques |
| `villageWellStructure`     | lista                  | vacío                  | Archivos de estructura colocados como pieza central de cada plaza, una entrada con peso por línea escrita nombre=peso como mypack:plaza_spire=3, sorteada una vez por plaza. Se centra en un cuadrado de seis bloques despejado y pavimentado con villagePathBlock, con su capa más baja sobre ese suelo. Vacío, la parte vacía, o una estructura que no se puede cargar construye allí el pozo del propio juego. Una entrada que no está escrita nombre=peso se omite |
| `villagePathDeadEnds`      | lista                  | vacío                  | Cómo se cierra una calle que termina sin salida, una entrada por línea, sorteada por extremo: sidewalk pavimenta la fila final con el bloque de acera y barrier levanta villagePathBridgeBarrierBlock a lo largo de ella villagePathBridgeBarrierHeight de alto; cualquier otra entrada se ignora. Cierran solo un extremo que no ha hecho crecer una calle sin salida, un estilo cuyo bloque no está establecido queda fuera del sorteo, y el extremo de un callejón admite solo barrier. Vacío deja esos extremos abiertos |
| `villagePathIntersects`    | lista                  | vacío                  | Diseños pintados en los cruces, nombrados por clave de registro de la carpeta `<namespace>/pathintersects/` de un pack. Una entrada pinta todos los cruces igual; varias se eligen por cruce según su peso |

#### Puentes y embarcaderos de aldea

*aldeas y ciudades*

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

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villagePathSupportBlock`        | texto           | vacío                                      | La propia superficie donde el suelo es roca desnuda, y los pilares y patas bajo una calle sobre el agua. Vacío mantiene la grava de vanilla, arenisca en las ciudades del desierto |
| `villagePathBridgeBlock`         | texto           | vacío                                      | El bloque con el que una calle o embarcadero cruza el agua. Vacío lo entarima con tablones de la madera de la aldea: acacia en una aldea de sabana, abeto en una aldea de taiga, roble en el resto |
| `villagePathBridgeSidewalkBlock` | bloque          | vacío                                      | Entarima la acera donde una calzada cruza el agua. Vacío lleva el bloque de acera normal al otro lado |
| `villagePathBridgeBarrierBlock`  | bloque          | vacío                                      | Barreras apiladas a lo largo de ambos bordes de la cubierta de un puente. Ninguna se levanta donde la cubierta descansa sobre el suelo. Vacío no construye ninguna |
| `villagePathBridgeBarrierHeight` | entero, 1 o más | `1`                                        | Cuántos bloques de alto se levantan esas barreras |
| `villagePathBridgeDrop`          | entero, 0 o más | `0`                                        | Cuánto debe quedar la rasante de una calzada por encima del suelo para que el hueco bajo ella se salve con un puente en lugar de rellenarse con material macizo. `0` mantiene las calzadas sobre el suelo: salvan el agua y nada más. `3` es la regla que sigue un viaducto ferroviario. Esto mueve la rasante, no solo el ornamento |
| `villagePathVergeBlock`          | texto           | vacío                                      | El bloque con el que se rellena el terreno junto a una calle y bajo una parcela allí donde la ciudad tiene que crear tierra: las hendiduras entre parcelas y el relleno hasta una calle a través de un hueco, que en cambio se entarima con villagePathBridgeBlock allí donde la calle es un puente. Vacío sigue el suelo sobre el que está: arena, terracota, grava o tierra con hierba encima donde sería tierra |
| `villagePathVergeWaterBlock`     | bloque          | `minecraft:oak_planks`                     | En qué se convierte ese relleno donde queda sobre el agua, para que un arcén llevado sobre un lago no sea una columna de tierra. También reviste un escalón de piedra que haya quedado sobre el agua |
| `villagePathBridgeFrameBlock`    | bloque          | vacío                                      | Un marco aéreo sobre un puente largo: un poste a cada lado de la cubierta y una viga por arriba. Cada marco lleva un pilar hasta el suelo bajo la cubierta, y no se levanta ninguna farola en la fila donde está. Vacío no construye ninguno |
| `villagePathBridgeFrameTopBlock` | bloque          | vacío                                      | La viga que cruza la parte superior de ese marco. Vacío usa `villagePathBridgeFrameBlock` |
| `villagePathBridgeFrameHeight`   | entero, 1 o más | `4`                                        | Cuántos bloques de altura libre deja el marco sobre la cubierta, quedando la viga un bloque por encima |
| `villagePathBridgeFrameRun`      | entero, 1 o más | `24`                                       | A cuántas filas de distancia se levantan los marcos cuando un puente es lo bastante largo para varios. Se reparten simétricamente respecto al centro del tramo salvado |
| `villagePathBridgeFrameLeast`    | entero, 1 o más | `24`                                       | El tramo salvado más corto que recibe un marco. Un puente más corto se deja liso |
| `villagePathPiers`               | lista           | vacío                                      | Estilos de embarcadero para una calle que termina sin salida sobre el agua: la cola salvada se convierte en un embarcadero en lugar de un puente a ninguna parte. Los estilos son railed, pilings y boardwalk; con varias entradas se sortea una por embarcadero. Vacío deja esa cola como un puente liso |
| `villagePathPierCargo`           | lista           | vacío                                      | Carga colocada a lo largo del interior de las barandillas de un embarcadero, como entradas bloque=peso, bloque=peso,altura para apilarla, o empty=peso para la parte que se deja libre. Un bloque puede llevar su estado entre corchetes, y uno con orientación se gira hacia el centro del embarcadero. Una altura que no sea de 1 a 8 apila un bloque. Una de cada dos filas sortea la lista a cada lado. Vacío deja los embarcaderos desnudos |
| `villagePathPierLoot`            | texto           | `resourcedatapackloader:chests/pier_cargo` | La tabla de botín con la que se llenan los bloques de carga con inventario, sorteada la primera vez que se abre uno. Un pack puede sustituir la tabla integrada incluyendo su propia tabla de botín con ese nombre. Vacío los deja vacíos |

#### Túneles de aldea

*aldeas y ciudades*

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

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villagePathTunnelBlock`      | texto           | vacío   | El bloque con el que se reviste una calle donde perfora una colina en lugar de abrirla en corte: las paredes a ambos lados de la perforación y el techo sobre ella. Vacío no perfora túneles y deja que la calle suba la colina |
| `villagePathTunnelDepth`      | entero, 1 o más | `10`    | Cuánto terreno tiene que haber sobre la superficie de la calzada antes de que un tramo se perfore en lugar de abrirse en corte. Una elevación enterrada a esa profundidad durante doce filas o más se mantiene a nivel y se perfora, y sus accesos menos profundos se abren en corte; un montículo más corto se corta como antes. Solo cuenta una vez que `villagePathTunnelBlock` nombra un bloque |
| `villagePathTunnelLightBlock` | bloque          | vacío   | Una luz incrustada en el techo del túnel a lo largo de su línea central. Vacío no ilumina ninguno |
| `villagePathTunnelLightRun`   | entero, 1 o más | `8`     | A cuántos bloques de distancia están esas luces. Anclado a las coordenadas del mundo, así que las luces de una pieza de calzada continúan en la siguiente; un túnel demasiado corto para llegar a uno de esos puntos se ilumina una vez, en su centro |

#### Alcantarillas de aldea

*aldeas y ciudades*

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

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villageSewerBlock`        | nombre de bloque     | vacío             | El bloque con el que se reviste una alcantarilla bajo las calles y callejones de una aldea: su suelo, sus dos paredes y su techo. Vacío no excava alcantarillas |
| `villageSewerDepth`        | entero, 4 o más | `8`               | A qué profundidad bajo la propia superficie de una calle está el suelo de la alcantarilla. La alcantarilla sigue la calle bajo la que discurre, de modo que una calle que sube lleva una alcantarilla que sube. Requiere `villageSewerBlock` |
| `villageSewerHeight`       | entero, 2 o más | `3`               | Cuántos bloques de altura libre hay sobre la pasarela |
| `villageSewerWidth`        | entero, 3 o más | `5`               | Qué ancho tiene la alcantarilla, contado de lado a lado incluyendo sus dos paredes. Un número par se redondea hacia arriba para que el canal conserve el centro |
| `villageSewerWaterBlock`   | nombre de bloque     | `minecraft:water` | Lo que llena el canal central. Vacío deja el canal seco |
| `villageSewerWalkBlock`    | nombre de bloque     | vacío             | Con lo que se revisten las pasarelas a ambos lados del canal. Vacío camina sobre el bloque de revestimiento |
| `villageSewerLightBlock`   | nombre de bloque     | vacío             | El bloque incrustado en el techo sobre el canal como luz. Vacío no ilumina ninguno |
| `villageSewerLightRun`     | entero, 1 o más | `8`               | A cuántos bloques de distancia están esas luces. Anclado a las coordenadas del mundo, así que las luces de una pieza de calzada continúan en la siguiente |
| `villageSewerLadderBlock`  | texto           | vacío             | El bloque por el que se sube un pozo de registro, colocado a lo largo del pozo desde la calle hasta el techo de la alcantarilla. Vacío deja el pozo abierto |
| `villageSewerCoverBlock`   | nombre de bloque     | vacío             | El bloque que cubre una boca de registro, colocado a ras en una calle este-oeste allí donde se le une una calle o callejón, y en la plaza donde esa calle cruza el bucle de la alcantarilla. Una trampilla de madera es la opción habitual: una de hierro recibe señal de redstone y ningún jugador puede abrirla a mano, lo que les cierra la alcantarilla. Vacío deja abierta la boca del pozo |
| `villageSewerMossBlock`    | nombre de bloque     | vacío             | Un segundo bloque mezclado aquí y allá en el revestimiento, por ejemplo piedra musgosa entre piedra lisa. Vacío reviste la alcantarilla con un solo bloque en todo su recorrido |
| `villageSewerMossChance`   | 0 a 100       | `25`              | Qué porcentaje de los bloques del revestimiento salen como ese segundo bloque. Se sortea por posición de bloque a partir de la semilla del mundo, de modo que la misma alcantarilla siempre sale igual |
| `villageSewerVineBlock`    | nombre de bloque     | vacío             | Un bloque colgado aquí y allá en el interior de las paredes de la alcantarilla, por ejemplo enredaderas. Se adhiere a la pared contra la que está. Vacío no cuelga nada |
| `villageSewerVineChance`   | 0 a 100       | `20`              | Qué porcentaje de las celdas junto a una pared lo llevan. Se sortea por posición de bloque a partir de la semilla del mundo, de modo que la misma alcantarilla siempre cuelga igual |
| `villageSewerWellEntrance` | booleano        | `true`            | Un bucle de alcantarilla bajo el anillo de la plaza alrededor del pozo, con la alcantarilla de cada calle pasando por él, y una boca de registro en la plaza que baja al bucle a cada lado donde lo cruza una calle este-oeste, de modo que las alcantarillas son un sistema conectado con una entrada en el centro del pueblo. Desactivado, la alcantarilla de cada calle termina en el pozo y la plaza no tiene bajada |

**Alcantarillas.** Nombrar `villageSewerBlock` excava una alcantarilla bajo cada calle y callejón, `villageSewerDepth` bloques por debajo de la propia superficie de esa calle. No es una red propia: sigue las calles, de modo que adonde van ellas va la alcantarilla, sube donde ellas suben, y dos alcantarillas se encuentran bajo un cruce porque las calles sobre ellas se encuentran; allí donde una calle o callejón termina contra otra, su alcantarilla sigue por debajo de esa para unirse a ella. Una calle sin salida y un tramo llevado sobre un puente no llevan ninguna. La sección es un suelo revestido, un canal por el medio lleno de `villageSewerWaterBlock`, una pasarela a cada lado revestida con `villageSewerWalkBlock`, `villageSewerHeight` bloques de altura libre y un techo revestido, de `villageSewerWidth` de ancho de lado a lado incluyendo sus dos paredes, y `villageSewerLightBlock` incrusta una luz en el techo sobre el canal cada `villageSewerLightRun` bloques. Allí donde la perforación de un metro atraviesa la profundidad de la alcantarilla, bajo la calle o a su lado, la alcantarilla se tapia con material macizo a través de ella y cualquier vía que haya allí se deja intacta. La alcantarilla de una calle se detiene en el bucle alrededor del pozo cuando `villageSewerWellEntrance` está activado, y en el propio pozo cuando está desactivado. Una alcantarilla nunca sube lo bastante como para perturbar la calle que tiene encima, y un tramo sin espacio entre la calle y el suelo del mundo se omite en lugar de comprimirse.

#### Ferrocarriles de aldea

*aldeas y ciudades*

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

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villageRailLines`               | entero, 0 o más       | `0`     | Cuántas líneas de ferrocarril atraviesan una ciudad, trazadas antes que cualquier calle para que la población crezca a su alrededor, cada una recorriendo toda la longitud de la ciudad. Con villageCitySpacing 1 cada distrito es una ciudad y lleva la suya. 0 no traza ninguna |
| `villageRailSpacing`             | entero, 1 o más       | `48`    | Los menos bloques de terreno libre entre el lecho de una línea ferroviaria y el de la siguiente de la misma ciudad. 1 las traza a un bloque de distancia, que es como un pack construye una playa de vías de líneas paralelas |
| `villageRailDirection`           | texto                 | `any`   | En qué dirección corren las líneas: `ew` de este a oeste, `ns` de norte a sur, `any` lo sortea por ciudad. `e`, `w`, `n` y `s` se leen igual |
| `villageRailWidth`               | entero, 3 o más       | `3`     | Lo mínimo que mide el lecho de la vía. `3` lleva una vía por el centro y `5` lleva dos; un lecho al que se piden más vías de las que caben se ensancha para contenerlas |
| `villageRailBlock`               | bloque                | vacío   | La vía. Vacío traza raíles de vanilla, por los que ruedan las vagonetas; cualquier otro bloque se coloca tal cual |
| `villageRailTrackSeat`           | `auto`, `on` o `in` | `auto`  | Dónde se asienta la vía. `auto` asienta un bloque de raíl sobre el lecho y incrusta a ras en la superficie del lecho cualquier otro bloque; `on` siempre lo coloca sobre el lecho; `in` siempre lo incrusta en el lecho. Una vía incrustada en el lecho es como un pack consigue un aspecto de raíl hecho con bloques o losas de hierro en lugar de raíles de vagoneta, y un paso a nivel entonces discurre a ras por el pavimento |
| `villageRailBedBlock`            | bloque                | vacío   | El lecho bajo la vía. Vacío coloca grava |
| `villageRailTieBlock`            | texto                 | vacío   | La traviesa colocada de lado a lado del lecho cada villageRailTieRun filas. Vacío coloca tablones de roble |
| `villageRailTieRun`              | entero, 1 o más       | `2`     | A cuántas filas de distancia están las traviesas |
| `villageRailTracks`              | entero, 0 o más       | `0`     | Cuántas vías lleva un mismo lecho, lado a lado y a `villageRailTrackGap` de distancia. **El lecho se ensancha para contenerlas todas**, de modo que tres vías comparten un solo lecho en lugar de convertirse en tres líneas. `0` coloca una vía en un lecho de menos de cinco de ancho y dos en uno más ancho |
| `villageRailTrackGap`            | entero, 2 o más       | `2`     | A cuántos bloques de distancia están las vías de un lecho, de centro a centro. `2`, lo mínimo permitido, deja un bloque de lecho entre ellas, lo que evita que se curven una hacia otra como hacen los raíles que se tocan |
| `villageRailShoulderBlock`       | bloque                | vacío   | Reviste las columnas más externas del lecho, un camino de mantenimiento junto a la vía y la respuesta del ferrocarril a la acera de una calzada. Vacío no coloca ninguno |
| `villageRailShoulderWidth`       | entero, 0 o más       | `1`     | Cuántas columnas de ancho tiene ese arcén a cada lado, añadidas fuera de `villageRailWidth`. Requiere `villageRailShoulderBlock` |
| `villageRailPowerBlock`          | bloque                | vacío   | La vía propulsada incrustada en la línea cada `villageRailPowerRun` filas. Vacío usa un raíl propulsado de vanilla; un bloque que no es un raíl simplemente se coloca allí |
| `villageRailPowerBase`           | bloque                | vacío   | Lo que hay bajo una vía propulsada para alimentarla. Vacío usa un bloque de redstone |
| `villageRailPowerRun`            | entero, 0 o más       | `0`     | Cada tantas filas se incrusta un raíl propulsado sobre un bloque de redstone en una vía de raíles de vanilla, para que la vagoneta siga rodando. `0` no propulsa ninguna, y cualquier vía que no sea de raíles de vanilla lo ignora |
| `villageRailClimb`               | entero, 1 o más       | `8`     | Cuántas filas corre la línea a nivel por cada bloque que sube o baja. `1` la nivela tan empinada como una calzada |
| `villageRailTail`                | entero, 0 o más       | `48`    | Hasta dónde sigue una línea ferroviaria más allá del último distrito de la ciudad en cada extremo |
| `villageRailSupportBlock`        | texto                 | vacío   | El bloque de poste bajo un viaducto, donde la línea pasa sobre agua o un desnivel. Vacío usa troncos de roble |
| `villageRailDeckBlock`           | texto                 | vacío   | La cubierta sobre la que un viaducto lleva el lecho. Vacío usa tablones de roble |
| `villageRailBarrierBlock`        | bloque                | vacío   | Barreras a lo largo de ambos bordes de la cubierta de un viaducto. Vacío no levanta ninguna |
| `villageRailBridgeFrameBlock`    | bloque                | vacío   | Un marco aéreo sobre un viaducto largo: un poste a cada lado de la cubierta y una viga por arriba. Cada fila que lleva uno lleva también sus postes de apoyo hasta el lecho. Vacío no construye ninguno |
| `villageRailBridgeFrameTopBlock` | bloque                | vacío   | La viga que cruza la parte superior de ese marco. Vacío usa `villageRailBridgeFrameBlock` |
| `villageRailBridgeFrameHeight`   | entero, 2 o más       | `4`     | Cuántos bloques de altura libre deja el marco sobre la cubierta, quedando la viga un bloque por encima |
| `villageRailBridgeFrameRun`      | entero, 2 o más       | `24`    | A cuántas filas de distancia se levantan los marcos cuando un viaducto es lo bastante largo para varios |
| `villageRailBridgeFrameLeast`    | entero, 2 o más       | `24`    | El viaducto más corto que recibe un marco. Un viaducto más corto se deja liso |
| `villageRailTunnelBlock`         | texto                 | vacío   | El bloque con el que se reviste una línea ferroviaria donde perfora una colina en lugar de subirla. Vacío no perfora túneles |
| `villageRailTunnelDepth`         | entero, 1 o más       | `6`     | Cuánto terreno debe haber sobre el lecho antes de que un tramo se perfore en lugar de abrirse en corte. Requiere `villageRailTunnelBlock` |
| `villageRailTunnelLightBlock`    | bloque                | vacío   | Una luz incrustada en el techo de un túnel ferroviario a lo largo de su línea central. Vacío no ilumina ninguno |
| `villageRailTunnelLightRun`      | entero, 1 o más       | `8`     | A cuántos bloques de distancia están esas luces de túnel, ancladas a las coordenadas del mundo para que las piezas coincidan |

**Por dónde va una línea.** Las líneas corren en paralelo, sobre el eje que nombra `villageRailDirection`, y se espacian a partir del primer pozo de la ciudad por turnos, primero un lado y luego el otro, manteniendo cada una al menos `villageRailSpacing` bloques de terreno entre su lecho y el de la siguiente línea. Un ferrocarril arranca despejado de la plaza y de las parcelas a su alrededor y se aparta allí donde discurriría a lo largo de una calle; un metro arranca en la fila del pozo y se desplaza hasta la calle más cercana dentro de `villageSubwaySpacing`, de modo que corre bajo una calzada. Una línea se traza antes que las parcelas, de modo que ninguna parcela se levanta sobre vía abierta, y recorre toda la ciudad y sigue `villageRailTail` más allá de su último distrito en cada extremo, deteniéndose siete bloques antes de cualquier otra ciudad que se interponga. Cada distrito que atraviesa traza su propio tramo, haya crecido allí la ciudad o no.

**Rasante.** Un ferrocarril no sube como una calle. Su lecho sigue el terreno suavizado a lo largo de un tramo largo y cambia de nivel un bloque como máximo cada `villageRailClimb` filas; un metro sigue el terreno `villageSubwayDepth` por debajo de él, y una línea que no puede mantener esa profundidad en ningún punto de su recorrido, con los seis bloques de espacio que necesita su revestimiento sobre el suelo del mundo, no se traza en absoluto. Donde el terreno cae más de tres bloques, o lo cubre el agua, la línea corre sobre un viaducto: una cubierta de `villageRailDeckBlock` con la vía sobre ella o incrustada en ella y sin traviesas ni arcén, sobre postes de `villageRailSupportBlock` bajo ambos bordes cada cuatro filas, bajando cada poste hasta terreno sólido a 24 bloques como máximo. Donde el terreno sube, la línea se abre en corte, o se perfora con `villageRailTunnelBlock` una vez que el terreno sobre el lecho alcanza `villageRailTunnelDepth` de profundidad durante doce filas o más; la perforación continúa mientras un bloque de terreno siga cubriéndola. Un corte abierto con agua a menos de tres bloques se tapia con el revestimiento del túnel hasta el agua, y el terreno junto al lecho se rellena donde cae. Se mantienen despejados cuatro bloques sobre el lecho a lo largo de toda la línea. Un viaducto se tiende a una sola altura de extremo a extremo, y el lecho a ambos lados rampea para encontrarse con esa altura; donde mantener un viaducto a nivel y el ritmo de ascenso discrepan, gana el nivel y la rampa junto a él puede escalonarse antes de lo que dice `villageRailClimb`. Un viaducto de `villageRailBridgeFrameLeast` filas o más lleva marcos aéreos una vez que `villageRailBridgeFrameBlock` nombra un bloque, a `villageRailBridgeFrameRun` filas de distancia y repartidos simétricamente respecto al centro del viaducto, y cada fila que lleva uno lleva también sus postes de apoyo. Una fila donde una calle cruza la línea se deja sin marco.

**Cruces.** Una calle cruza una línea en recto. En un cruce la línea se mantiene a nivel a través de la calle y una fila más allá a cada lado, y la calle se nivela a la línea, nunca al revés, rampeando hasta ese nivel con su propia pendiente. El pavimento conserva la superficie y la vía lo cruza un bloque más arriba, o a ras de él cuando `villageRailTrackSeat` incrusta la vía en el lecho, de modo que una vagoneta cruza la calle y un aldeano cruza la vía. Una línea perforada bajo una calle que se alza seis bloques o más sobre ella no se cruza en absoluto: la calle mantiene su propia rasante y pasa sobre el túnel. Esa altura se lee del terreno de la calle suavizado para subir como máximo un bloque por fila, antes de que ningún cruce, pozo o ferrocarril lo fije.

**Escalones de puerta.** El escalón ante cada puerta de una parcela se rellena con tierra donde el terreno cae, y un escalón de piedra que quede sobre el agua se reviste con `villagePathVergeWaterBlock`.

**Vía.** Con `villageRailBlock` vacío la vía es raíl de vanilla girado a lo largo de la línea, y `villageRailPowerRun` coloca un raíl propulsado, activado, sobre un bloque de redstone cada tantas filas para que una vagoneta recorra toda la línea; una vía incrustada en el lecho no lleva raíles propulsados. Un pack que quiera bloques de hierro, barras o cualquier otra cosa los nombra en su lugar: un bloque con eje, un tronco por ejemplo, se gira a lo largo de la línea, y cualquier otro se coloca tal cual. Cada bloque de raíl, metro, estación y alcantarilla puede llevar su estado entre corchetes, y Mezclar bloques más arriba nombra los ajustes de los que se extrae una mezcla con peso bloque a bloque.

#### Metros de aldea

*aldeas y ciudades*

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

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villageSubwayLines`            | entero, 0 o más | `0`     | Cuántas líneas de ferrocarril subterráneo excava una ciudad. 0 no excava ninguna y no sortea nada, de modo que la ciudad se traza exactamente como sin ellas |
| `villageSubwayDepth`            | entero, 6 o más | `24`    | A qué profundidad bajo la superficie está el lecho. La línea se nivela a partir del terreno que tiene encima, de modo que sigue el terreno a esa profundidad en lugar de correr a nivel |
| `villageSubwaySpacing`          | entero, 1 o más | `64`    | A qué distancia unas de otras se mantienen las líneas de metro de una ciudad |
| `villageSubwayDirection`        | texto           | `any`   | En qué dirección corren las líneas de metro: ew de este a oeste, ns de norte a sur, o any para sortear por ciudad |
| `villageSubwayWidth`            | entero, 3 o más | `3`     | Qué ancho tiene el lecho, antes de los arcenes |
| `villageSubwayBlock`            | bloque          | vacío   | El bloque de vía. Vacío coloca raíl de vanilla |
| `villageSubwayTrackSeat`        | cadena         | `auto`  | Si la vía se asienta sobre el lecho, en él, o `auto` para dejar que decida el bloque |
| `villageSubwayBedBlock`         | bloque          | vacío   | El bloque del que se hace el lecho. Vacío usa grava |
| `villageSubwayTieBlock`         | bloque          | vacío   | El bloque colocado de lado a lado del lecho como traviesas. Vacío usa tablones |
| `villageSubwayTieRun`           | entero, 1 o más | `2`     | A cuántos bloques de distancia están las traviesas |
| `villageSubwayTracks`           | entero, 0 o más | `0`     | Cuántas vías paralelas lleva el lecho. 0 toma tantas como permita el ancho |
| `villageSubwayTrackGap`         | entero, 2 o más | `2`     | A qué distancia están las vías paralelas |
| `villageSubwayShoulderBlock`    | bloque          | vacío   | El bloque a cada lado del lecho. Vacío no deja arcén |
| `villageSubwayShoulderWidth`    | entero, 0 o más | `1`     | Qué ancho tiene ese arcén |
| `villageSubwayPowerBlock`       | bloque          | vacío   | El bloque de vía propulsada. Vacío usa raíl propulsado de vanilla |
| `villageSubwayPowerBase`        | bloque          | vacío   | El bloque colocado bajo una vía propulsada para accionarla. Vacío usa un bloque de redstone |
| `villageSubwayPowerRun`         | entero, 0 o más | `0`     | A cuántos bloques de distancia están las vías propulsadas. 0 no coloca ninguna |
| `villageSubwayTunnelBlock`      | texto           | vacío   | El bloque con el que se reviste la perforación: las paredes a ambos lados y el techo sobre ella. Vacío excava la perforación y sus estaciones sin revestir |
| `villageSubwayTunnelLightBlock` | bloque          | vacío   | El bloque incrustado en el techo del túnel como luz. Vacío no ilumina ninguno |
| `villageSubwayTunnelLightRun`   | entero, 1 o más | `8`     | A cuántos bloques de distancia están esas luces, ancladas a las coordenadas del mundo para que las piezas coincidan |
| `villageSubwayClimb`            | entero, 1 o más | `8`     | Cuántos bloques corre una línea antes de poder subir o bajar un bloque |
| `villageSubwayTail`             | entero, 0 o más | `48`    | Hasta dónde corre una línea más allá de las piezas propias de la ciudad antes de detenerse |
| `villageSubwaySurfaces`         | entero, de 0 a 100  | `25`    | La probabilidad sobre cien de que una línea de metro suba a la superficie por un extremo y siga desde allí como un ferrocarril corriente, con túnel detrás y vía abierta delante. La subida lleva villageSubwayClimb filas por bloque, de modo que una línea profunda emplea un largo tramo en salir. 0 mantiene todos los metros enterrados en toda su longitud |

**Salir a la superficie.** `villageSubwaySurfaces` es la probabilidad sobre cien de que una línea, en lugar de permanecer enterrada de extremo a extremo, suba a la superficie por un extremo y siga desde allí como un ferrocarril corriente: túnel detrás, vía abierta delante. La subida obedece a `villageSubwayClimb`, un bloque por esa cantidad de filas, de modo que una línea a `villageSubwayDepth` de profundidad emplea profundidad por ascenso (`villageSubwayDepth` por `villageSubwayClimb`) filas solo en la rampa y necesita un buen tramo más allá para que valga la pena; una línea sin sitio para ambas simplemente se queda bajo tierra. La subida no empieza más cerca que el extremo lejano de las calles bajo las que corre la línea, de modo que sale pasadas las calles de la ciudad en lugar de a través de ellas, y una parcela situada en el tramo que sube le cede el sitio. Las estaciones se reclaman una vez decidida la subida y se mantienen alejadas de la rampa. Con `villageSubwayTunnelBlock` vacío, la perforación, sus estaciones y sus escaleras se excavan sin revestir.

#### Estaciones de metro

*aldeas y ciudades*

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

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villageSubwayStationLength` | entero, 0 o más | `0`                    | Cuántos bloques de largo tiene la cámara de una estación, centrada en la fila donde la línea pasa más cerca del pozo. 0 no construye ninguna estación |
| `villageSubwayStationRun`    | entero, 0 o más | `0`                    | A cuántos bloques de distancia se sitúan más estaciones a lo largo de una línea, pasada la más cercana al primer pozo de la ciudad. 0 construye solo la del pozo |
| `villageSubwayPlatformWidth` | entero, 0 o más | `3`                    | Cuánto se abre la cámara a cada lado del lecho para formar un andén |
| `villageSubwayPlatformBlock` | bloque          | vacío                  | El bloque con el que se pavimenta el andén. Vacío lo pavimenta con el revestimiento del túnel |
| `villageSubwayRailingBlock`  | bloque          | `minecraft:iron_bars`  | El bloque con el que se rodea con barandilla la cabecera de las escaleras de una estación donde desembocan en la calle, para que nadie caiga en el pozo. Vacío deja la cabecera sin barandilla |
| `villageSubwayBenchBlock`    | bloque          | `minecraft:oak_stairs` | El asiento de los bancos colocados en el andén de una estación y junto a la cabecera de sus escaleras. Un bloque de escalera se gira para mirar en sentido contrario a la línea y se lee como un banco; sirve cualquier bloque. Vacío omite los bancos |
| `villageSubwayBenchEndBlock` | bloque          | `minecraft:oak_log`    | Los brazos en cada extremo de un banco de estación. Vacío deja el asiento desnudo por ambos extremos |
| `villageSubwayBenchLength`   | entero, de 0 a 32   | `5`                    | Cuánto mide de largo un banco de estación, brazos incluidos. `0` omite los bancos |
| `villageSubwayStation`       | texto           | vacío                  | Una estructura de la carpeta structures de un pack usada como estación: su pozo, sus escaleras y su acceso desde la calle. Extrae una de un mundo construido a mano con #scripts/rdpl-grab-template.py: sus celdas macizas se colocan y sus celdas de aire se excavan, de modo que la forma es la propia construcción y no una descripción de ella. Vacío no construye ninguna estación, y un nombre que no se puede cargar registra un error y no construye ninguna |
| `villageSubwayStationFoot`   | entero, de 0 a 64   | `4`                    | Cuántas capas al pie de una construcción de estación se colocan una vez, antes de la parte que se repite. El suelo y la puerta hacia el andén están aquí |
| `villageSubwayStationRepeat` | entero, de 0 a 64   | `12`                   | Cuántas capas de una construcción de estación se repiten, de modo que una construcción sirve para cualquier profundidad: el pozo crece por copias enteras de esta banda y el corredor absorbe lo que sobra. Debe ser un giro completo de la escalera o los tramos no se unirán. `0` nunca hace crecer la construcción |

**Estaciones.** Una línea de metro recibe estaciones solo cuando `villageSubwayStation` nombra una construcción que se carga: con él vacío no hay cámara, ni escaleras, ni acceso, y un nombre que no se puede cargar registra un error y no construye ninguna. Con una construcción nombrada, una línea recibe una estación en la fila del primer pozo de la ciudad una vez establecidos `villageSubwayStationLength` y `villageSubwayPlatformWidth`, y otras cada `villageSubwayStationRun` bloques a lo largo de ella. Cada una se desplaza hasta 48 bloques en cualquier dirección para encontrar un lugar para su construcción junto a una calle que corra a lo largo de la línea durante toda la longitud de ese lugar, fuera de toda calle, pozo y plaza, y como mínimo a la longitud de la estación más siete bloques de una estación ya reclamada; una parcela situada en ese lugar le cede el sitio, y una estación sin tal lugar se omite. Una línea que no conserva ninguna estación no abre ninguna cámara, de modo que nunca lleva una sin acceso. La cámara se mantiene a nivel a lo largo de su longitud: el lecho abierto `villageSubwayPlatformWidth` a cada lado, pavimentado con `villageSubwayPlatformBlock`, con paredes y techo del revestimiento del túnel, iluminada desde los propios `villageSubwayTunnelLightBlock` y `villageSubwayTunnelLightRun` del túnel, y tapiada a través de la perforación en ambos extremos. Desde el andén un corredor lleva a la construcción de la estación, que sube a la calle junto a la carretera, nunca bajo ella; la construcción sale a la rasante de la calle más cercana a menos de ocho bloques, la rasante que esa calle mantiene sobre el suelo y no la de ninguna cubierta o rampa levantada sobre ella, o a la altura del terreno donde no hay calle, y se omite cuando la ciudad se construye donde eso queda a menos de tres bloques sobre el andén, donde la construcción no puede hacer la subida ni siquiera ampliada, o donde su corredor hasta el andén pasaría de 32 bloques; el registro dice cuál. El terreno entre esa calle y la construcción se lleva a la misma altura, rellenado donde cae y despejado por encima, de modo que se entra en la estación desde la carretera. Un banco de `villageSubwayBenchBlock` con brazos de `villageSubwayBenchEndBlock`, de `villageSubwayBenchLength` de largo, se levanta en el andén.

**Construir la estación a mano.** `villageSubwayStation` nombra un archivo de estructura usado como estación, que es como un pack incluye una forma que alguien construyó en lugar de una descrita en ajustes. Constrúyela en un mundo, extráela con #scripts/rdpl-grab-template.py y colócala con el pack: sus bloques se colocan como se construyeron, las celdas de esponja se convierten en el revestimiento del túnel, sus celdas de aire se excavan, y cualquier cosa que haya dentro, una vagoneta o un soporte para armadura, viene con ella. Se asienta desde la esquina del lugar de la estación. Una construcción sirve para cualquier profundidad porque la parte central se repite: `villageSubwayStationFoot` capas se colocan una vez abajo, con el suelo y la puerta al andén, y luego se apilan copias enteras de las siguientes `villageSubwayStationRepeat` capas hasta que la construcción llega a la calle. Esa banda debe ser un giro completo de la escalera o los tramos no se encontrarán donde se unen dos copias. Un corredor de dos bloques de alto va desde la puerta hasta el andén, volviendo a lo largo de la cámara donde el desnivel es demasiado largo para ir en recto; el terreno sobre la cabeza de la construcción se despeja ocho bloques hacia arriba, una barandilla de `villageSubwayRailingBlock` rodea la abertura a nivel de calle y un banco se levanta junto a ella. Una construcción que no se puede cargar no construye ninguna estación en ninguna parte y registra un error; una estación a la que su construcción no puede hacer llegar a la calle se omite cuando se planifica la ciudad, sin cámara, y registra por qué. La construcción lleva su propia abertura a la calle.

#### Enlaces ferroviarios

*aldeas y ciudades*

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

| Ajuste | Tipo | Predeterminado | Qué hace |
| --- | --- | --- | --- |
| `villageRailLinks`             | true/false     | `false` | Enlaza ciudades vecinas cuyas primeras líneas se miran a través de una costura. Requiere `villageRailLines`, o `villageSubwayLines` en un pack sin líneas de superficie |
| `villageRailLinkLeast`         | entero, 0 o más | `128`   | El enlace más corto que se traza, ramal más tronco más ramal, en bloques |
| `villageRailLinkMost`          | entero, 0 o más | `1024`  | El enlace más largo que se traza, ramal más tronco más ramal, en bloques |
| `villageRailLinkBridgeMost`    | entero, 0 o más | `96`    | El puente más largo que puede necesitar un enlace. Un enlace sobre agua más ancha o un desnivel más profundo no se traza |
| `villageRailLinkTunnelMost`    | entero, 0 o más | `192`   | El túnel más largo que puede necesitar un enlace donde `villageRailTunnelBlock` perfora túneles. Un enlace que tendría que perforar más lejos no se traza |
| `villageRailLinkStation`       | texto           | `both`  | La estación de cada ramal justo antes del tronco: `both` coloca un andén a cada lado de la línea, `one` un único andén a la izquierda de un tren que llega al tronco, `none` no construye ninguna |
| `villageRailLinkStationLength` | entero, 0 o más | `16`    | Cuántas filas de largo tienen los andenes de la estación. `0` no construye estaciones |
| `villageRailLinkPlatformWidth` | entero, 0 o más | `3`     | Cuántos bloques de ancho tiene cada andén |
| `villageRailLinkPlatformBlock` | bloque          | vacío   | El bloque con el que se construyen los andenes. Vacío usa ladrillos de piedra |

**Qué es un enlace.** Los enlaces ferroviarios unen ciudades vecinas en una sola red. Las ciudades se fundan una por celda de la cuadrícula de ciudades (`villageCitySpacing`), y un enlace discurre a lo largo de la costura entre dos celdas: la primera línea de cada ciudad continúa más allá de su cola como un ramal, recto hasta la costura, y se encuentra con un tronco trazado a lo largo de la costura en ángulo recto. El tronco va de un ramal al otro y nunca más allá de ninguno. Requiere `villageRailLines`, o `villageSubwayLines` en un pack sin líneas de superficie, y está desactivado por defecto.

**Qué ciudades se enlazan.** Dos ciudades se enlazan solo cuando están en celdas vecinas, sus primeras líneas corren sobre el eje que cruza la costura entre ellas, y todo el enlace, medido de pozo a pozo a lo largo de la vía, mide entre `villageRailLinkLeast` y `villageRailLinkMost` bloques. Cada parte de la decisión se calcula a partir de la semilla y de los dos emplazamientos de ciudad, de modo que sale igual se haga primero la ciudad o el chunk que se haga. Un enlace que no se puede construir entero no se traza en absoluto, nunca a medias: uno que necesitaría un puente o túnel más largo de lo que permiten los ajustes, alcanzaría más allá del borde del mundo, chocaría con una mansión del bosque, dejaría a dos ciudades más cerca de lo que permite `structureSeparation`, o llevaría una unión demasiado cerca de una esquina de las celdas. Un tronco solo se traza hacia una ciudad que realmente se fundó: cuando un tope como `structureMost` detiene a la vecina, o esta crece demasiado poco para conservarse, no se construye ninguna mitad del tronco ni el ramal más allá de la cola de la propia ciudad. Las ciudades fijadas se enlazan del mismo modo, una por celda; una celda con dos fijadas no enlaza ninguna. Las demás ciudades se mantienen alejadas del ramal y del tronco de un enlace a medida que crecen, como se mantienen alejadas unas de otras.

**Rasante.** Los ramales y troncos son líneas ferroviarias y se nivelan, se salvan con puentes, se perforan y se cruzan exactamente como una línea de ciudad, con `villageRailClimb` y los ajustes de viaducto y túnel de arriba. Donde un ramal se une al tronco ambos quedan a nivel, y también la estación junto a él.

**El empalme.** Un ramal se une solo a la vía cercana del tronco. Esa vía se interrumpe donde el centro del ramal se encuentra con ella, la vía izquierda del ramal gira a la izquierda hacia ella y su vía derecha gira a la derecha, y la vía lejana sigue recta. Con dos vías, el tronco por arriba y el ramal subiendo desde abajo:

```
xxxxxxx
ooooooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

`x` es lecho y `o` es vía. Donde los dos ramales llegarían a pocos bloques el uno del otro, la primera línea de la segunda ciudad se desplaza para alinearse con la primera, y las dos se encuentran en un cruce en su lugar: cada ramal se funde solo con su propia vía cercana exactamente como arriba, ambas vías del tronco se interrumpen en el centro del ramal, y ningún raíl cruza otro:

```
 xoxox
xxoxoxx
oooxooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

Un tronco de una sola vía no tiene una segunda vía que dar al otro ramal, de modo que un enlace cuyos ramales se encontrarían de frente sobre una sola vía no se traza. Con una sola vía, la vía del ramal gira hacia la vía del tronco hacia la izquierda, y la vía del tronco más allá de esa curva termina contra ella. Las curvas se colocan con sus formas fijas, de modo que el raíl de vanilla gira donde se dibuja el empalme y en ningún otro sitio.

**Estaciones.** Las últimas filas de un ramal antes del empalme son una estación: andenes de `villageRailLinkPlatformBlock` a nivel con el raíl, con barandilla a lo largo del borde exterior con `villageSubwayRailingBlock`, y un banco de `villageSubwayBenchBlock` a mitad de cada andén.

**Metros.** En un pack con solo líneas de metro, el enlace lleva la primera línea de metro de una ciudad. La línea sale del suelo hacia el tronco, con la rampa de `villageSubwayDepth` por `villageSubwayClimb` filas de largo, y llega a la estación y al empalme en la superficie; una ciudad de este tipo se enlaza por un solo lado, el del enlace más corto, y el tronco es un ferrocarril de superficie construido con los ajustes `villageRail`. Donde un ramal no tiene sitio para esa rampa y su estación, el tronco baja en cambio al metro: todo el enlace, ramales y tronco, permanece bajo tierra a `villageSubwayDepth`, se construye con los ajustes `villageSubway`, y se encuentra en el mismo empalme sin estación.

#### Decoración de aldea

*aldeas y ciudades*

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
| `villageDecor` | lista | vacío   | Decoración esparcida a lo largo de las calles de la ciudad, como pares nombre=peso que nombran worldgen de un pack, mypack:street_flowers=2. El nombre empty es la parte de puntos que se deja desnuda, y una entrada que no está escrita nombre=peso se omite. Cada tercer bloque de arcén a cada lado de una calle sortea la lista, sobre el suelo que haya allí a la altura que esté, pero no en un túnel, ni bajo una parcela, ni en una plaza, ni a menos de dos bloques de una puerta. Vacío no esparce nada |

### Estructuras

*qué hace cada grupo*

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

`control.structures` decide este grupo. Estructuras vanilla desactivadas, su espaciado, separación, distancia de aparición, biomas, apariciones de mobs, fijaciones y adaptación del terreno.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `structureSpacing` | lista | vacía | A qué distancia se siembran las estructuras vanilla, en chunks, como entradas estructura=chunks: los nombres de 1.12.2 temples, monuments, mansions, mineshafts, strongholds, netherbridges, endcities y villages, o cualquier id de conjunto de estructuras, como pillager_outposts. En mineshafts el número indica un chunk de cada tantos; en strongholds es la distancia entre anillos. Las fortalezas del Nether conservan su propia cuadrícula, a la que el espaciado de netherbridges no llega |
| `structureSeparation` | lista | vacía | Lo más cerca que pueden estar dos estructuras del mismo tipo, en chunks, como entradas estructura=chunks; en strongholds es la dispersión de los anillos. Los templos, las minas abandonadas y netherbridges conservan su propia separación, a la que esto no llega. En monuments, una separación igual de grande que el espaciado se reduce a uno menos que el espaciado, y el registro lo indica |
| `structureMost` | lista | vacía | El máximo de aldeas que puede contener una dimensión, como villages=cantidad (por ejemplo villages=100); las demás estructuras no tienen tope: una vez fundadas tantas, ningún chunk funda otra, salvo los chunks fijados con structureAt. 0 o una entrada ausente no establece ningún límite |
| `structureSpawners` | lista de `structure=entity` | vacía | Qué genera el spawner de una estructura vanilla, separado por comas para una elección aleatoria por spawner. Las cuatro que colocan uno son dungeons, mineshafts, nether fortresses y strongholds |
| `structureMinDistanceFromSpawn` | lista | vacía | A qué distancia del punto de aparición del mundo empieza una estructura, en bloques, como entradas estructura=bloques. Se mide desde el punto de aparición del mundo; mientras un mundo nuevo aún lo está eligiendo, desde el worldSpawn del pack si hay uno, y si no desde el origen del mundo |
| `structureBiomes` | lista | vacía | Dónde puede generarse una estructura, como entradas estructura=bioma,bioma con ids de bioma, los nombres que muestra el juego como Birch Forest, nombres vanilla sin espacio de nombres como desert, o tipos de bioma como SANDY |
| `structureBiomesAreBlacklist` | lista de `structure=true` o `structure=false` | vacía | El sentido de la lista de biomas de cada estructura |
| `structureSpawns` | lista | vacía | Los mobs que genera una estructura diga lo que diga el bioma, como entradas estructura=espaciodenombres:entidad:peso:mínimo:máximo, separadas por comas. La lista sustituye por completo la lista de mobs propia de la estructura, sea cual sea el tipo de cada mob; una lista vacía tras el = no genera nada |
| `structureAt` | lista de `structure=x,z` | vacía | Fija una estructura en un punto exacto. Consulta [Estructuras en lugares exactos](#estructuras-en-lugares-exactos) |
| `structureAdaptation` | lista | las mansiones `beard_thin`; las demás estructuras conservan su adaptación vanilla | Cómo se adapta el terreno a una estructura, como entradas estructura=modo con los modos none, bury, beard_thin, beard_box y encapsulate |
| `terrainAdaptation` | booleano | `false` | Traza las calles de ciudad propias de RDPL, asentadas en el terreno en lugar de levantarse sobre pilotes en cada hondonada, y lee con ellas las opciones villagePath y villageRail. Cambia el terreno, así que un mundo creado con esto activado es distinto de uno creado sin ello. Las ciudades se siembran según indica villageCitySpacing, y con 0 no hay ninguna |
| `villagePieces` | lista | vacía | Parcelas de aldea nombradas aquí, una por línea, por el id completo de un archivo de villages como mypack:smithy, por su nombre simple o por el nombre de la estructura que construye una parcela de plantilla. Mientras villagePiecesAreBlacklist esté activado, una estructura nombrada aquí también se deja vacía allí donde el juego la cargue, incluidas las casas de aldea del propio juego, como minecraft:village/plains/houses/plains_small_house_1 |
| `villagePiecesAreBlacklist` | booleano | `true` | Activado, las parcelas de villagePieces quedan bloqueadas. Desactivado, solo se construyen esas parcelas |
| `villageBlocks` | lista | vacía | Bloques con los que se construyen las parcelas de aldea, como pares original=sustituto, minecraft:cobblestone=mypack:ruby_brick. Cualquiera de los dos lados puede llevar un estado entre corchetes, y entonces el original debe coincidir exactamente. Un par puede añadir una probabilidad sobre 100, minecraft:cobblestone=minecraft:mossy_cobblestone,20, calculada a partir de la semilla del mundo en el punto donde se coloca el bloque, at=bloque para actuar solo donde se encuentre ese bloque, y under=bloque solo encima de él. Los pares sin probabilidad ni condición se aplican primero, de modo que un par condicional puede degradar su resultado. Rige las granjas y las casas de aldea del propio juego que construye una ciudad; las calles, los pozos, las farolas y las estructuras de tus parcelas de plantilla nunca se rigen por él. Vacío deja cada bloque tal como se coloca |

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
    "threatItems": ["minecraft:diamond_sword=5,1", "minecraft:diamond=1,16,batch"],
    "threatLevels": [10, 25, 50],
    "threatMost": -1,
    "threatSpawnRate": 2.0,
    "threatNotice": 16.0,
    "threatSays": ["1=Something out there has taken notice of you.", "0=The world loses interest in you."]
  }
}
```

`control.spawning` decide este grupo. Límites de aparición de mobs, tasas de aparición hostil y el tope de luz.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `surfaceDayMonsterRate` | número, de 0.0 a 4.0 | `1.0` | Multiplicador de la aparición hostil en la superficie de día, siendo `1.0` el valor vanilla, de modo que se puede desactivar la aparición diurna en la superficie sin tocar las cuevas |
| `surfaceNightMonsterRate` | número, de 0.0 a 4.0 | `1.0` | Lo mismo para la superficie de noche |
| `undergroundDayMonsterRate` | número, de 0.0 a 4.0 | `1.0` | Lo mismo bajo tierra de día |
| `undergroundNightMonsterRate` | número, de 0.0 a 4.0 | `1.0` | Lo mismo bajo tierra de noche |
| `monsterCap` | int, de -1 a 1000 | `-1` | Cuántos hostiles pueden estar cargados a la vez. En vanilla son 70, y `-1` lo deja como está |
| `creatureCap` | int, de -1 a 1000 | `-1` | Lo mismo para los animales pasivos. En vanilla son 10 |
| `ambientCap` | int, de -1 a 1000 | `-1` | Lo mismo para los murciélagos y similares. En vanilla son 15 |
| `waterCreatureCap` | int, de -1 a 1000 | `-1` | Lo mismo para los calamares. En vanilla son 5 |
| `monsterSpawnLight` | int, de -1 a 15 | `-1` | La luz de bloque más intensa en la que un mob hostil aún puede aparecer, además de las comprobaciones vanilla. -1 mantiene solo la regla vanilla. Los spawners no se ven afectados |
| `threatItems` | lista | vacía | Ítems que aumentan el nivel de amenaza de un jugador, como entradas ítem=nivel,cantidad con un ,each o ,batch opcional al final, p. ej. minecraft:diamond_sword=5,1 o minecraft:diamond=1,16,batch. Each, el valor por defecto, suma el nivel por cada uno que se lleve, contando como máximo la cantidad indicada; batch suma el nivel una vez por cada tanda de esa cantidad. Una cantidad superior al tamaño de pila del ítem se recorta al tamaño de pila. Toda entidad cargada que lleve ítems es portadora: el inventario principal, la armadura y la mano secundaria de un jugador, una pila soltada, cualquier cosa con inventario de ítems como una mula con cofre o una vagoneta con cofre, y los ítems en mano y la armadura de otros mobs. Vacío desactiva el nivel de amenaza. Con `control.spawning` en `off`, los ajustes de amenaza de la configuración siguen aplicándose |
| `threatLevels` | lista | vacía | Las puntuaciones a las que se entra en cada franja, en orden ascendente, de modo que `10, 25, 50` crea tres franjas. Vacío desactiva el nivel de amenaza |
| `threatMost` | int, de -1 a 100000 | `-1` | Limita la puntuación. `-1` la deja sin límite |
| `threatSpawnRate` | número, de 0.0 a 8.0 | `1.0` | Escala la aparición hostil a menos de 128 bloques de un portador de la franja superior, además de las demás tasas, y las franjas inferiores reciben una parte proporcional |
| `threatNotice` | número, de 0.0 a 64.0 | `0.0` | Cuántos bloques más lejos ven a un portador de la franja superior los mobs hostiles, incluidos los vanilla, repartido igualmente entre las franjas inferiores |
| `threatSays` | lista de `band=message` | vacía | Las líneas que se muestran en amarillo cuando cambia la franja de un jugador, siendo la franja `0` la línea para cuando vuelve a bajar de la primera franja |

### Lecho de roca

*qué hace cada grupo*

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

`control.bedrock` decide este grupo. El lecho de roca plano y sus listas de dimensiones y biomas.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `flatBedrock` | booleano | `false` | Sustituye el lecho de roca irregular del fondo del mundo por capas planas. Solo en chunks nuevos, salvo que `flatBedrockRetrogen` esté activado |
| `flatBedrockDimensions` | lista | `["minecraft:overworld"]` | Las dimensiones en las que aplanar. Vacío significa todas |
| `flatBedrockDimensionsAreBlacklist` | booleano | `false` | Activado, el aplanado omite las dimensiones de la lista. Desactivado, solo se aplica a ellas |
| `bedrockLayers` | int, de 1 a 5 | `1` | Cuántas capas de lecho de roca se conservan |
| `flatBedrockBiomes` | lista de nombres de bioma | vacía | Los biomas en los que aplanar, por nombre descriptivo o de registro. Vacío significa todos los biomas |
| `flatBedrockBiomesAreBlacklist` | booleano | `false` | Activado, el aplanado omite los biomas de la lista. Desactivado, solo se aplica a ellos |
| `flatBedrockRoof` | booleano | `false` | Aplana también el techo de lecho de roca, donde una dimensión lo tiene, como el techo del Nether |
| `flatBedrockFiller` | bloque | vacío | Lo que sustituye al lecho de roca que se retira. Vacío elige según la dimensión: piedra, netherrack, piedra del End |
| `flatBedrockFillers` | lista de `dimension=block` | `["minecraft:the_nether=minecraft:netherrack", "minecraft:the_end=minecraft:end_stone"]` | Un relleno por dimensión, que prevalece sobre `flatBedrockFiller` en las dimensiones nombradas |
| `flatBedrockBiomeTypes` | lista | vacía | Tipos de bioma en los que aplanar el lecho de roca, junto con flatBedrockBiomes, por etiqueta de bioma como minecraft:is_ocean o por nombre de tipo de 1.12.2 como OCEAN. flatBedrockBiomesAreBlacklist también los abarca |
| `flatBedrockRetrogen` | booleano | `false` | Aplana también el lecho de roca de los chunks que ya existen. Cada chunk se procesa una vez y lo recuerda, y no se puede deshacer: el patrón original no se guarda en ningún sitio |
| `flatBedrockRetrogenKey` | texto | `0000` | Cámbiala para que todos los chunks vuelvan a ser aptos para el aplanado del lecho de roca |

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

`control.entities` decide este grupo. El ritmo más lento de las entidades lejos de todos los jugadores.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `slowDistantEntities` | booleano | `true` | Procesa con menos frecuencia las entidades lejos de todos los jugadores. Nunca se deja nada sin procesar, solo se procesa a un ritmo más lento |
| `slowedKinds` | lista | `["items", "experience"]` | Qué tipos reciben menos ticks: items, experience, projectiles, siendo estos últimos flechas, tridentes, bolas de nieve, huevos, pociones, frascos de experiencia y perlas de ender lanzados, y escupitajos de llama. Todo lo que piensa por sí mismo se ralentiza siempre, sin nombrarlo aquí: elige qué hacer a continuación con menos frecuencia, y aun así se mueve en cada tick. Las máquinas nunca se ralentizan |
| `slowDistance` | int, de 64 a 4096 | `192` | A qué distancia del jugador más cercano, en bloques, se ralentiza un chunk. El juego deja de informar a un jugador de la mayoría de las entidades más allá de 64, así que nada por debajo de eso |
| `slowRate` | int, de 1 a 20 | `4` | Un tick de cada tantos se da a un chunk ralentizado. 1 es no ralentizar en absoluto, 20 es una vez por segundo |
| `neverSlowed` | lista | vacía | Entidades que se dejan en paz por lejos que estén, como espaciodenombres:nombre |
| `slowRecheck` | int, de 1 a 100 | `20` | Cada cuántos ticks se vuelve a calcular la distancia al jugador más cercano. Cada jugador cuenta por sí mismo, así que alguien solo y lejos sigue teniendo su propio espacio tranquilo a su alrededor |

### Terreno, reservas y lo que dice el mod

*qué hace cada grupo*

`control.chunks` decide este grupo. El radio de chunks de aparición, la pregeneración, el retrogen y el reinicio, las líneas de bienvenida, la tarjeta de mensajes y los avisos emergentes del juego.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `retrogen` | booleano | `false` | Pone al día los chunks existentes con las entradas de worldgen que tengan \"retrogen\": true. Desactivado, los chunks que ya existen se dejan como están. Los chunks se marcan al generarse en ambos casos, así que activarlo más adelante solo afecta a los chunks anteriores al pack |
| `adoptExistingChunks` | booleano | `false` | Trata los chunks que ya existen como si este pack los hubiera generado, marcándolos en lugar de dejarlos para el retrogen. Actívalo al sustituir un mod que ya generaba el mismo mineral, para que el retrogen nunca lo duplique. Las entradas de worldgen añadidas más tarde siguen aplicándose por retrogen en ellos |
| `saysCard` | booleano | `false` | Muestra las líneas que dice este mod, la bienvenida, el aviso de creación de terreno que recibe un jugador que se une a mitad de una partida y el final de la partida (el progreso en curso se queda en la barra de acción), y las líneas de amenaza, como una tarjeta en la esquina inferior derecha en lugar de en el chat. La tarjeta se desliza al entrar, permanece ocho segundos y se desvanece, y se muestra también sobre una pantalla abierta |
| `saysIcon` | texto | vacío | Un ítem dibujado en la tarjeta, p. ej. minecraft:compass. Vacío no dibuja ninguno |
| `saysColor` | texto | vacío | El color de fondo de la tarjeta en hexadecimal, p. ej. 1E2630. Vacío usa un gris pizarra oscuro |
| `saysImage` | texto | vacío | Un PNG de los assets de cliente del pack estirado sobre la tarjeta como fondo, p. ej. rubyworld:textures/gui/card.png, dibujado sobre el color. Vacío no dibuja ninguno |
| `saysBackground` | booleano | `true` | Dibuja el panel, el borde y la franja de color de la tarjeta, y el fondo oscuro tras la bienvenida y las notas de retención. Desactivado deja solo el texto, que conserva su sombra, y saysImage si hay una definida |
| `saysFont` | texto | vacío | Una fuente para el texto de la tarjeta, nombrada como espaciodenombres:nombre, p. ej. rubyworld:runes para el assets/rubyworld/font/runes.json del pack. Vacío usa la fuente de RDPL |
| `toasts` | booleano | `false` | Muestra los avisos emergentes del juego, los pop-ups de la esquina superior derecha para logros, recetas desbloqueadas, consejos del tutorial y avisos del sistema, incluidos los de otros mods. Desactivado no muestra ninguno. Surte efecto en el siguiente mundo o servidor al que te unas. Una plantilla de mundo puede listar en su lugar los tipos que mostrar |
| `pregenOnNewWorld` | int, de 0 a 8192 | `0` | A qué distancia alrededor del punto de aparición, en chunks, se crea el terreno de un mundo antes de que nadie lo juegue. El juego crea 12 chunks alrededor del punto de aparición por su cuenta, así que 12 es el mínimo y 0 significa ese mínimo y no nada: el terreno que el juego iba a crear de todos modos se adopta e ilumina en una sola pasada organizada en lugar de llegar a cuentagotas. Auméntalo para llegar más lejos que el juego |
| `pregenToBorder` | booleano | `false` | Si el terreno de un mundo nuevo se crea hasta su borde del mundo en lugar de un número fijo de chunks, centrado en el borde en lugar de en el punto de aparición. Un mundo cuyo borde nunca se ha acercado no tiene borde al que llegar y se omite |
| `pregenAllDimensions` | booleano | `false` | Crea el terreno de todas las dimensiones que contiene el servidor, incluidas las de mods, primero el mundo principal y el resto por orden de id, en lugar de solo las de pregenDimensions. Las nombradas en pregenDimensionsWhenEntered siguen quedando para su primer visitante |
| `pregenResume` | booleano | `false` | Si una ejecución detenida o interrumpida continúa donde se quedó la próxima vez que se cargue el mundo, en lugar de empezar de nuevo |
| `pregenChunksInFlight` | int, de 1 a 512 | `32` | Cuántos chunks pide a la vez al juego una ejecución de creación de terreno. Más mantiene más ocupados los hilos de generación y el servidor responde peor a quien esté retenido mirando |
| `pregenBackup` | booleano | `false` | Copia el mundo a una copia de seguridad limpia cuando termina la pregeneración, mientras los jugadores siguen retenidos. La copia es lo que restauraría un reinicio, y una copia cuyos packs ya no coinciden se descarta y se vuelve a hacer |
| `resetClearsEntities` | booleano | `true` | Elimina toda entidad que no sea un jugador cuando se reinicia el mapa |
| `resetClearsScores` | booleano | `true` | Devuelve a cero todos los objetivos que guarda el pack cuando se reinicia el mapa, de modo que una nueva partida empiece desde cero. Los equipos en sí se conservan |
| `resetClearsInventory` | booleano | `false` | Vacía el inventario de cada jugador, armadura y mano secundaria incluidas, cuando se reinicia el mapa |
| `resetClearsExperience` | booleano | `false` | Devuelve la experiencia de cada jugador al nivel cero cuando se reinicia el mapa |
| `spawnChunkRadius` | int, de 0 a 1024 | `128` | A qué distancia del punto de aparición, en bloques, se mantienen cargados los chunks haya o no un jugador allí, redondeado a chunks enteros como (bloques + 8) / 16 en cada dirección, de modo que 128 mantiene 8 chunks en cada dirección. Al iniciarse un mundo, el mundo principal prepara un cuadrado 4 chunks más ancho en cada dirección antes de que el servidor esté listo, 25 por 25 chunks con 128. 0 no prepara ni mantiene ninguno, así que la zona de aparición se descarga como cualquier otra. RDPL mantiene los chunks con sus propios tickets, así que en 1.21.1 la regla de juego spawnChunkRadius no hace nada mientras esta clave esté en vigor |
| `spawnChunkRadii` | lista | vacía | Un radio para el mundo principal escrito como dimensión=bloques, como en minecraft:overworld=64, que prevalece sobre spawnChunkRadius. Solo el mundo principal tiene chunks de aparición, así que una entrada para cualquier otra dimensión no cambia nada |
| `welcomeSays` | lista | `[WELCOME]` | Líneas de bienvenida, mostradas en verde en cada inicio de sesión y tras la pregeneración. Una entrada simple es la línea para todas partes; una entrada dimensión=mensaje la sustituye para esa dimensión y además saluda a cada llegada allí, p. ej. minecraft:the_nether=Welcome to the Nether!. Un mensaje vacío tras el = silencia esa dimensión; una lista vacía no muestra nada. Con este valor por defecto habla en el idioma de cada jugador |
| `pregenBorderLimit` | int, de 1 a 1875000 | `8192` | Lo más lejos que puede llegar un borde, en chunks en cada dirección, antes de que se rechace crear terreno hasta él. Está para evitar que un error se ejecute durante semanas, no para subirlo, y un pack no puede modificarlo. Un cuadrado de 8192 contiene 268 millones de chunks **Solo configuración.** |
| `pregenDimensions` | lista | `["minecraft:overworld"]` | En qué dimensiones se crea el terreno de un mundo nuevo, por id, en el orden indicado, una tras otra |
| `pregenDimensionsWhenEntered` | lista | vacía | Dimensiones cuyo terreno no se crea de antemano sino la primera vez que alguien pone un pie en ellas, con el mismo alcance, reteniendo a todos del mismo modo hasta que termina. Una nombrada aquí y en pregenDimensions simplemente se crea de antemano |
| `pregenRunningSays` | texto | `World pregeneration running, %d%% done` | El mensaje de progreso que ven los jugadores mientras se genera el mundo, donde %d es el porcentaje y un segundo %s la dimensión. Vacío no les dice nada. Con este valor por defecto habla en el idioma de cada jugador |
| `pregenFinishedSays` | texto | `World pregeneration finished` | El mensaje que ven los jugadores cuando termina la generación. Vacío no les dice nada. Con este valor por defecto habla en el idioma de cada jugador |
| `pregenStoppedSays` | texto | `World pregeneration stopped` | El mensaje que ven los jugadores cuando la generación se detiene antes de tiempo. Vacío no les dice nada. Con este valor por defecto habla en el idioma de cada jugador |
| `pregenSpectatingSays` | texto | `Spectating until the world is ready` | El mensaje en mitad de la pantalla que ven los jugadores mientras están retenidos en espectador durante la generación del mundo. Vacío no muestra nada. Con este valor por defecto habla en el idioma de cada jugador |
| `pregenLogo` | texto | `center` | Dónde se sitúa el logotipo cuando termina la pregeneración: left, center o right, encima del texto de mitad de pantalla. Siempre se muestra; una palabra desconocida se lee como center |
| `pregenBackupSays` | texto | `Pack requested world backup` | El mensaje en mitad de la pantalla que ven los jugadores mientras se copia esa copia de seguridad. Vacío no muestra nada |
| `resetSays` | texto | `Pack requested map reset` | La línea en mitad de la pantalla que se muestra a los jugadores mientras /rdpl reset restablece el mapa. Vacío reinicia en silencio |
| `resetSendsTo` | texto | `spawn` | Adónde se lleva a los jugadores tras un reinicio: spawn, una posición como x,y,z, o dimensión:x,y,z para enviarlos a otro mundo |
| `resetRuns` | texto | vacío | Una función que se ejecuta después de que un reinicio haya limpiado el mapa, nombrada espaciodenombres:ruta. Vacío no ejecuta nada |

### Mundo vacío

*qué hace cada grupo*

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

`control.voidWorld` decide este grupo. La generación de mundos vacíos y su plataforma.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `voidWorld` | booleano | `false` | Genera las dimensiones listadas como espacio vacío con una plataforma en el punto de aparición y nada vivo, mediante el preajuste generado para las tres vanilla y mediante los archivos de dimensión propios de un pack; cualquier otra dimensión listada se vacía a medida que se crea su terreno. La elección se guarda con el mundo cuando se crea, así que activarlo o desactivarlo más tarde deja un mundo existente como estaba |
| `voidWorldDimensions` | lista | `["minecraft:overworld"]` | Qué dimensiones se hacen vacías, por id. Vacío significa ninguna, o todas las dimensiones cuando voidWorldDimensionsAreBlacklist está activado |
| `voidWorldDimensionsAreBlacklist` | booleano | `false` | Activado, las dimensiones listadas son las que se dejan en paz |
| `voidPlatformBlock` | bloque | `minecraft:stone` | De qué está hecha la plataforma |
| `voidPlatformHeight` | int, de -2032 a 2031 | `64` | La y a la que se sitúa la plataforma del mundo vacío |
| `voidPlatformSize` | int, 1 o más | `9` | Qué ancho tiene la plataforma, redondeado hacia abajo a un número impar para que quede centrada en el punto de aparición |
| `voidWorld` | texto | `default` | Generación de mundos vacíos y su plataforma [default\|global\|off] |

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
| `dragonFight` | booleano | `true` | Si ocurre todo el conjunto o no: el dragón, su barra, los cristales, la fuente sobre la que se alza y la reaparición que iniciaría un jugador con cristales del End. Pertenece al grupo `structures` |

`dragonFight` pertenece al grupo `structures` y decide si ocurre todo el conjunto o no: el dragón, su barra, los cristales, la fuente sobre la que se alza y la reaparición que iniciaría un jugador con cristales del End. Un End vaciado lo omite salvo que un pack lo pida, y un End normal lo tiene salvo que un pack diga lo contrario, así que conviene definir `dragonFight` en un sentido o en otro.

### Terreno

*qué hace cada grupo*

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

`control.terrain` decide este grupo. El nombre y la semilla del mundo al crearlo, generatorOptions, las regiones de cuevas, la altura de las nubes y las costuras entre mundos.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `worldSeed` | cadena | vacía | La semilla con la que se crea cada mundo nuevo, escrita tal como se teclearía: un número se usa tal cual, y cualquier otra cosa se convierte en uno como hace el juego. Un servidor dedicado crea también su mundo con ella y la escribe en `server.properties` como `level-seed`. Vacío deja la elección como está |
| `worldName` | texto | vacío | Cómo se llama un mundo nuevo cuando se abre la pantalla para crearlo. Vacío lo deja con el nombre que le da el juego |
| `worldType` | texto | vacío | El tipo de mundo sobre el que se construye el mundo con forma, uno de default, largebiomes, amplified o flat, con los nombres de 1.12.2 customized y default_1_1 leídos como default; flat es un mundo principal superplano construido a partir de las capas de generatorOptions, con las ciudades del pack encima. La forma que aparece más abajo (alturas, piedra profunda, nivel del mar, lecho de roca, vacío) se genera como un preajuste de mundo propio, listado en Tipo de mundo en la pantalla del mundo y elegido ahí sea cual sea el que se haya escogido. Un servidor dedicado lo escribe en `server.properties` como `level-type`, nombrando ese preajuste, o el preajuste propio del juego cuando no se da forma a nada, salvo que `level-type` ya nombre uno de los worldTypeExceptions. Vacío construye sobre default |
| `worldTypeExceptions` | lista | `["flat", "debug_all_block_states"]` | Tipos de mundo que elige un jugador y que el preajuste generado deja en paz, como flat o debug_all_block_states. Vacío significa que se sustituye toda elección |
| `generatorOptions` | texto | vacío | Los ajustes de terreno del mundo principal como un objeto JSON, las claves que escribía el tipo de mundo customized de 1.12.2. Se leen aquí: seaLevel, useLavaOceans, fixedBiome, y useCaves, useRavines, useDungeons, useLavaLakes, useStrongholds, useVillages, useMineShafts, useTemples, useMonuments y useMansions puestos en false. Con worldType flat son en cambio las capas, de abajo arriba, como el texto superplano de 1.12.2 3;minecraft:bedrock,59*minecraft:stone,4*minecraft:dirt,minecraft:grass_block;1;village o una lista de capas; el número tras las capas es el bioma, y village, biome_1, mineshaft, stronghold, oceanmonument, lava_lake y decoration después activan esas opciones. Solo se aplica a un mundo cuando se crea. Un servidor dedicado lo escribe en `server.properties` como `generator-settings`, las capas planas como el JSON plano del propio juego, salvo que `level-type` ya nombre uno de los worldTypeExceptions. Vacío deja el terreno como lo hace el tipo de mundo |
| `worldMinHeight` | int, de -2032 a 2016 | `-64` | El bloque más bajo del mundo principal, un múltiplo de 16 hasta -2032. El fondo del propio juego es -64; más bajo crea un mundo profundo bajo el terreno vanilla, de piedra maciza hasta que la capa de worldgen la excava o noiseCaves hace descender las cuevas del juego. Solo se aplica mediante el preajuste generado |
| `worldMaxHeight` | int, de -2016 a 2032 | `320` | El bloque por encima de la cima del mundo principal, un múltiplo de 16 hasta 2032, como máximo 4064 por encima de worldMinHeight. La cima del propio juego es 320; más alto deja cielo abierto sobre el terreno vanilla |
| `deepStone` | texto | vacío | El bloque del que está hecho el mundo bajo el terreno vanilla cuando worldMinHeight baja de -64, como la deepslate propia de un pack. Se funde con la deepslate a lo largo de las ocho capas bajo -64 igual que la deepslate se funde con la piedra. Vacío mantiene la piedra |
| `noiseCaves` | texto | `off` | Hasta dónde continúan las cuevas, túneles, fideos y acuíferos del juego cuando worldMinHeight baja de -64: off mantiene el mundo bajo el terreno vanilla como piedra profunda maciza para que la capa de worldgen la excave, deep los hace descender hasta el fondo con los lagos de lava trasladados a sus diez capas inferiores, world significa lo mismo en esta versión porque el terreno vanilla ya los tiene |
| `worldSpawn` | texto | vacío | Dónde aparece cada mundo nuevo, escrito como x,z o x,y,z. Sin y se usa el nivel medio del suelo del mundo, uno por encima del nivel del mar, o la cima de las capas en un mundo plano, y el juego busca entonces un punto seguro donde apoyarse como hace con cualquier aparición. Solo se aplica a un mundo cuando se crea. Vacío deja la elección al juego |
| `worldBorder` | int, de 0 a 60000000 | `0` | Qué ancho tiene, en bloques, el borde del mundo en cada mundo nuevo. Solo se aplica a un mundo cuando se crea. 0 deja el borde donde lo pone el juego |
| `worldTime` | int, de -1 a 23999 | `-1` | Bloquea la hora del día del mundo principal, en ticks, la misma cifra que toma /time set, de modo que 18000 es medianoche. El reloj se detiene y no se mueve nunca: /time set no puede moverlo, el día sigue contándose por debajo, y quitar el ajuste devuelve esa hora. -1 deja correr el tiempo |
| `caveRegionPlainWeight` | int, 0 o más | `4` | El peso del subsuelo simple, sin región, frente a los pesos propios de las regiones de cuevas. Más alto deja más subsuelo sin ninguna región |
| `caveRegionCells` | int, 16 o más | `128` | Qué ancho tiene en bloques una celda de región de cuevas. Las regiones de cuevas de los packs se pintan sobre el subsuelo en celdas de aproximadamente este tamaño |
| `caveRegionCellsY` | int, 16 o más | `64` | Qué alto tiene en bloques una celda de región de cuevas |
| `worldGravity` | lista | vacía | Escala la gravedad, como multiplicador del valor vanilla donde 1.0 es sin cambios y 0.17 es similar a la Luna, para todas las entidades. Un valor simple abarca todas las dimensiones, y una entrada escrita como dimensión=valor abarca solo esa dimensión y prevalece sobre el valor simple. Vacío deja la gravedad como está |
| `worldFallDamage` | lista | vacía | Escala el daño por caída del mismo modo, 0.5 lo reduce a la mitad y 2.0 lo duplica |
| `worldJumpStrength` | lista | vacía | Escala la fuerza del salto del mismo modo, 1.5 salta una vez y media más alto |
| `worldTerminalVelocity` | lista | vacía | Escala lo más rápido que cae un mob o jugador del mismo modo, 0.5 cae a la mitad de la velocidad máxima vanilla |
| `weatherCeiling` | lista | vacía | La y más alta a la que llegan la lluvia y la nieve, como entradas dimensión=y. Un número simple abarca todas las dimensiones. Por encima no cae lluvia, no cuaja la nieve, no se llenan los calderos, no caen rayos y no se dibuja ninguna precipitación; por debajo el clima no cambia. El hielo depende de la temperatura y no de la precipitación, así que sigue formándose por encima de la línea. Vacío significa sin techo |
| `cloudHeight` | lista | vacía | La y a la que se dibujan las nubes, como entradas dimensión=y. Un número simple abarca todas las dimensiones. Prevalece sobre el `cloudHeight` propio de una dimensión de pack. Vacío mantiene la altura de nubes del propio juego, 192 en el mundo principal |
| `worldBelow` | lista | vacía | Apila otra dimensión bajo esta: al caer por el fondo del mundo te lleva a la dimensión nombrada, llegando bajo su techo en las mismas x y z, aún en caída. Las entradas se escriben como dimensión=destino, como minecraft:overworld=minecraft:the_nether para colgar el Nether bajo el mundo principal; un id simple abarca todas las dimensiones. Atravesarlo excavando requiere que el lecho de roca del suelo se omita, lo cual decide worldSeamBedrock. Vacío significa que el suelo sigue siendo el suelo |
| `worldAbove` | lista | vacía | Lo mismo para el techo: al ascender por encima de la cima del mundo te lleva a la dimensión nombrada, llegando sobre su suelo. Se escribe igual que worldBelow |
| `worldSeamEntities` | booleano | `true` | Si los ítems soltados, los mobs y otras entidades cruzan también las costuras del mundo, o solo los jugadores. Los jinetes y las monturas cruzan de uno en uno |
| `worldSeamBedrock` | booleano | `false` | Conserva de todos modos el lecho de roca en el límite de una costura. Desactivado, una dimensión cuyo suelo o techo lleva una costura worldBelow o worldAbove no genera lecho de roca allí, de modo que se pueda excavar el paso. Los chunks ya generados conservan lo que tengan |

### Servidor

*qué hace cada grupo*

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

`control.server` decide este grupo: las líneas de `server.properties` que un pack puede establecer, con el modo de juego, la dificultad y los comandos en un mundo abierto a LAN. En un servidor dedicado, cada valor que un pack fije aquí se escribe en `server.properties` al arrancar el servidor, de modo que el archivo refleja lo que está en vigor, y los que el servidor ya ha leído también se fijan en él. Un mundo de un jugador toma lo que tiene un servidor integrado, como indica cada fila. Vacío, o `-1` en un número, deja el valor propio del servidor, y con `control.server` en `off` cada línea queda tal como la tiene el servidor.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `worldGameMode` | texto | vacío | De qué modo se inicia cada mundo nuevo: survival, hardcore, creative, adventure o spectator. Hardcore es supervivencia en la que la muerte termina el mundo, para todo el mundo guardado, igual que la opción de la pantalla de mundo. Vacío lo deja como lo eligiera quien creó el mundo. La pantalla de mundo solo ofrece survival, hardcore y creative, así que adventure y spectator se establecen al crear el mundo. Un servidor dedicado pone cada mundo en el modo de su server.properties en cada arranque, así que allí el modo del pack se escribe en server.properties (gamemode y hardcore) antes de cargar el mundo |
| `privacy` | booleano | `true` | Si la telemetría y los informes de chat del juego están desactivados: no se envía ningún evento de telemetría, el cliente no firma ningún mensaje de chat, el servidor no guarda sesión de chat ni la exige, así que ningún mensaje puede denunciarse. Un pack que lo omite recibe la opción de configuración `privacy` de la categoría `tweaks`. Surte efecto en el siguiente mundo o servidor al que se entre |
| `worldLanCommands` | booleano | `true` | Si un jugador que abre un mundo de un jugador a LAN puede activar los comandos para todos los que se unan. `false` atenúa el botón Permitir comandos de la pantalla Abrir a LAN y lo mantiene desactivado, y el mundo se abre sin comandos se pidan como se pidan, `/publish` incluido |
| `worldDifficulty` | lista | vacío | Bloquea la dificultad, una de peaceful, easy, normal o hard. Una dificultad suelta abarca todas las dimensiones, y una entrada escrita como dimensión=dificultad, como minecraft:the_nether=hard, abarca solo esa dimensión y prevalece sobre la suelta. El ajuste propio del mundo se deja como estaba y vuelve cuando se quita la entrada. Un servidor dedicado escribe la dificultad del mundo principal en `server.properties` como `difficulty`. Vacío la deja como se eligió |
| `worldForceGameMode` | booleano | vacío | Si un jugador que se une vuelve a ponerse en el modo de juego del servidor cada vez, la línea `force-gamemode`. Un mundo abierto a LAN ya lo hace, y `false` lo detiene también allí |
| `worldPvp` | booleano | vacío | Si los jugadores pueden hacerse daño entre sí, la línea `pvp`. Un mundo de un jugador también la toma |
| `worldFlight` | booleano | vacío | Si un jugador que vuela en supervivencia se deja en paz en lugar de ser expulsado, la línea `allow-flight`. Un mundo de un jugador también la toma |
| `worldSpawnProtection` | int, -1 o más | `-1` | Cuántos bloques alrededor del punto de aparición solo pueden construir los operadores, la línea `spawn-protection`, 0 para ninguno. Solo un servidor dedicado protege su punto de aparición |
| `worldNether` | booleano | vacío | Si se puede entrar en el Nether, la línea `allow-nether`. `false` lo cierra también en un mundo de un jugador |
| `worldCommandBlocks` | booleano | vacío | Si los bloques de comandos se ejecutan, la línea `enable-command-block`. Un mundo de un jugador ya los ejecuta, y `false` los desactiva también allí |
| `worldIdleTimeout` | int, -1 o más | `-1` | Cuántos minutos puede un jugador estar inactivo antes de ser expulsado, la línea `player-idle-timeout`, 0 para nunca. Un mundo de un jugador también la toma |
| `worldMotd` | texto | vacío | La línea que se muestra bajo el nombre del servidor en la lista de servidores, la línea `motd`. Un mundo de un jugador abierto a LAN la muestra en lugar del propietario y el nombre del mundo |
| `worldMaxSize` | int, -1 a 29999984 | `-1` | Lo más lejos, en bloques desde el centro, que puede llegar jamás el borde del mundo, la línea `max-world-size`. Un mundo de un jugador también la toma |
| `worldStructures` | booleano | vacío | Si un mundo nuevo genera estructuras, la línea `generate-structures` y la opción Generar estructuras de la pantalla de mundo. Solo se aplica a un mundo al crearse |
| `worldSpawnMonsters` | booleano | vacío | Si aparecen mobs hostiles, la línea `spawn-monsters`. `false` los detiene también en un mundo de un jugador |
| `worldSpawnAnimals` | booleano | vacío | Si aparecen animales, la línea `spawn-animals`. `false` los detiene también en un mundo de un jugador |
| `worldSpawnNpcs` | booleano | vacío | Si aparecen aldeanos, la línea `spawn-npcs`. `false` los detiene también en un mundo de un jugador |
| `worldViewDistance` | int, -1 a 32 | `-1` | A cuántos chunks de distancia envía un servidor dedicado el mundo a cada jugador, la línea `view-distance`. Un mundo de un jugador sigue en su lugar la distancia de renderizado |
| `worldSimulationDistance` | int, -1 a 32 | `-1` | A cuántos chunks de distancia mantiene un servidor dedicado el mundo en funcionamiento alrededor de cada jugador, la línea `simulation-distance`. Un mundo de un jugador sigue en su lugar su propio ajuste |

### Recetas

*qué hace cada grupo*

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

`control.recipes` decide este grupo. Bloqueo de recetas de fabricación y de horno y sus listas blancas. El bloqueo de hornos arrastra consigo las recetas del alto horno, el ahumador y la hoguera, ya que 1.12.2 guardaba todas las recetas de cocción en una única lista de horno. Las recetas de la cortapiedras y de herrería nunca se bloquean, ya que 1.12.2 no tenía ninguna.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `blockRecipes` | booleano | `false` | Elimina todas las recetas de fabricación excepto las de los mods de `recipeWhitelist`. Por defecto nada está exento, así que indica el espacio de nombres de tu propio pack para conservar sus recetas |
| `recipeWhitelist` | lista de ids de mod | `["minecraft"]` | Los mods cuyas recetas de fabricación se conservan |
| `blockedRecipeMods` | lista de ids de mod | vacío | Mods cuyas recetas de fabricación se eliminan sin más, diga lo que diga la lista blanca |
| `recipeMatch` | `recipe`, `output` o `both` | `recipe` | De dónde se lee el id del mod al bloquear recetas de fabricación: el nombre de la propia receta, el ítem que crea, o cualquiera de los dos, que bloquea cuando coincide uno y exime cuando uno está en la lista blanca |
| `blockFurnaceRecipes` | booleano | `false` | Lo mismo para las recetas de horno, leyendo el mod a partir del ítem producido |
| `furnaceWhitelist` | lista de ids de mod | `["minecraft"]` | Los mods cuyas recetas de horno se conservan |
| `blockedFurnaceMods` | lista de ids de mod | vacío | Mods cuyas recetas de horno se eliminan sin más |
| `logBlockedRecipes` | booleano | `true` | Registra un recuento por mod de lo que se bloqueó |
| `furnace` | booleano | `true` | Aplica los archivos furnace/*.json, que añaden y eliminan recetas de fundición del horno **Solo configuración.** |
| `removals` | booleano | `true` | Aplica los archivos recipe_removals/*.json, que eliminan recetas de fabricación por nombre, espacio de nombres o resultado **Solo configuración.** |
| `skipMissingItems` | booleano | `true` | Omite las recetas que usan un ítem que no está registrado, en lugar de dejar que fallen. El recuento se registra una vez **Solo configuración.** |

### Comandos

*qué hace cada grupo*

`control.commands` decide este grupo. Quién puede ejecutar los comandos propios del mod: los niveles de permiso de goto.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `gotoLevel` | int, 0 a 4 | `3` | El nivel de permiso necesario para /rdplserver goto <nombre>, que lleva a quien lo envía a la estructura más cercana. 3 es un operador, el nivel en el que está el resto del comando. 2 permite también que lo ejecute un bloque de comandos, de modo que un pack puede poner el salto en un botón o una placa de presión sin dar a nadie el resto del comando. 0 deja que cualquier jugador lo escriba. Las demás partes de /rdplserver siguen en 3 diga esto lo que diga |
| `gotoNextLevel` | int, 0 a 4 | `3` | El nivel de permiso para /rdplserver goto <nombre> next, que pasa por alto la última a la que llevó a quien lo envía y busca otra. Misma escala que gotoLevel |
| `gotoBackLevel` | int, 0 a 4 | `3` | El nivel de permiso para /rdplserver goto <nombre> back, que devuelve a quien lo envía a la anterior. Misma escala que gotoLevel |
| `gotoPlaceLevels` | lista | vacío | Niveles de permiso para lugares concretos, como entradas nombre=nivel, una por línea, que sustituyen a los tres ajustes anteriores solo para ese lugar y en sus tres formas. El nombre es lo que escribirías tras goto, así que uno de vanilla como Village o Mansion, o un nombre que un pack registró para sus propias estructuras con locateAs. Misma escala: 3 un operador, 2 también un bloque de comandos, 0 cualquiera. Así un pack puede abrir el camino a sus propias ruinas mientras todas las estructuras de vanilla siguen cerradas, o al revés. Un nombre que nada ha registrado se ignora con una nota en el registro |

### Worldgen, solo configuración

*qué hace cada grupo*

Estas claves de `worldgen` no pertenecen a ningún grupo y son exclusivas de la configuración.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `worldgenDebug` | booleano | `false` | Escribe en logs/rdpl.log las líneas de depuración a las que remiten otros mensajes, como qué pack sirvió un archivo y qué hizo cada comando. Muy detallado |
| `worldTemplate` | texto | `auto` | Qué ajustes de plantilla de mundo se aplican. Un pack añade una en worldtemplates/*.json y tú la nombras aquí como namespace:name. 'auto' elige la plantilla del pack de mayor prioridad. Vacío no usa ninguna |
| `tellWorldType` | booleano | `true` | Avisa en el chat a un jugador, al unirse a un mundo creado con el preajuste generado, de qué plantilla le dio forma. Un pack no puede establecerlo |
| `worldBorderLimit` | int, 1 a 60000000 | `60000000` | El borde más ancho que un pack puede pedir mediante worldBorder. Un pack que pide más es rechazado y el borde se deja donde lo pone el juego. Un pack no puede establecerlo |
| `retrogenKey` | texto | `0000` | Cámbialo para que todos los chunks vuelvan a ser aptos para retrogen, en todas las entradas de worldgen. Las vetas nuevas se añaden encima de lo que ya hay |
| `retrogenChunksPerTick` | int, 1 o más | `2` | Cuántos chunks ya generados se actualizan por tick. Más es más rápido pero provoca más tirones |

### La categoría `packs`

*qué hace cada grupo*

Cómo se encuentran y se sirven las carpetas de packs. Solo configuración.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `rootDirectory` | texto | `rdploader` | Carpeta de la que se cargan los packs, relativa al directorio .minecraft. También funciona una ruta absoluta. Requiere reiniciar |
| `overrideResourcePacks` | booleano | `true` | Inserta el pack de assets por encima de los packs de recursos seleccionados por el jugador y de los packs de datos propios del mundo. Un pack llamado RDPLO... siempre prevalece, RDPLN... nunca |
| `warnOnCaseMismatch` | booleano | `true` | Avisa cuando un archivo solo coincide porque el sistema de archivos no distingue mayúsculas. Esos packs fallan en Linux |
| `logContents` | booleano | `false` | Registra cada pack encontrado y cuántos archivos aporta |
| `traceUnresolvedVariables` | booleano | `false` | Registra un seguimiento de pila la primera vez que se pide un archivo con un '#' en el nombre, indicando qué lo pidió |

### La categoría `content`

*qué hace cada grupo*

Bloques, ítems, fluidos y todo lo demás que definen los packs. Solo configuración.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `load` | booleano | `true` | Registra los bloques, ítems, fluidos, materiales y pestañas creativas que definen los packs, y carga sus exposiciones. Requiere reiniciar |
| `vanillaClients` | booleano | `false` | Atiende a clientes vanilla sin modificar: no se registra nada de ningún pack, ni bloques, ítems, fluidos o pestañas creativas, y no se carga ninguna exposición, de modo que un cliente sin el mod puede unirse. Todo lo que vive solo en el servidor sigue aplicándose. Requiere reiniciar |
| `sounds` | booleano | `true` | Registra los eventos de sonido nombrados en sounds/*.json, para que los packs puedan incluir su propio audio |
| `fuels` | booleano | `true` | Aplica los archivos fuels/*.json, que dan a los ítems un tiempo de combustión en el horno |
| `potions` | booleano | `true` | Registra los efectos de poción y los tipos de poción descritos en potions/*.json y potion_types/*.json de los packs. Requiere reiniciar |
| `brewing` | booleano | `true` | Aplica los archivos brewing/*.json, que añaden recetas del soporte para pociones |
| `villagers` | booleano | `true` | Registra las profesiones de aldeano descritas en villagers/*.json y aplica los intercambios de trades/*.json. Requiere reiniciar |
| `entities` | booleano | `true` | Registra las variantes de entidad descritas en entities/*.json de los packs. Requiere reiniciar |
| `overrides` | booleano | `true` | Aplica los archivos overrides/<namespace>/<name>.json, que cambian propiedades de bloques, ítems y tipos de poción que ya existen, de vanilla o de mods |
| `disabled` | booleano | `true` | Aplica los archivos disabled/*.json, que sacan bloques e ítems del juego: sin pestaña creativa, entrada de JEI, receta, botín, intercambio, etiqueta, colocación, uso ni recogida, y los stacks de ellos se eliminan |
| `hardness` | booleano | `true` | Aplica los archivos hardness/*.json, que dan a un grupo de bloques un multiplicador de tiempo de minado y de resistencia a las explosiones, tirado por posición de bloque |
| `shovelPaths` | booleano | `true` | Deja que una pala convierta en camino los bloques marcados con behavesAs path, y que revierta un camino al agacharse |
| `shovelPathBecomes` | texto | vacío | En qué convierte la pala esos bloques. Vacío usa el camino de tierra |
| `shovelPathReverts` | texto | vacío | En qué devuelve un camino agacharse con una pala. Vacío usa tierra |
| `hoeTilling` | booleano | `true` | Deja que una azada labre los bloques marcados con behavesAs till |
| `hoeTillsInto` | texto | vacío | En qué convierte la azada esos bloques. Vacío usa tierra de cultivo |
| `caneMaxHeight` | int, 1 a 255 | `3` | Hasta qué altura crece la caña de azúcar de vanilla. En vanilla es 3. Los bloques de caña definidos por packs usan su propia sección growth e ignoran esto |
| `cactusMaxHeight` | int, 1 a 255 | `3` | Lo mismo para el cactus de vanilla |

### La categoría `data`

*qué hace cada grupo*

Botín, funciones y nombres de registro. Solo configuración.

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `lootInjections` | booleano | `true` | Aplica los archivos loot_injections/*.json, que añaden pools a tablas de botín que ya existen en lugar de sustituir la tabla entera |
| `playerLoot` | booleano | `true` | Aplica los archivos player_loot/*.json, que tiran una tabla de botín cuando muere un jugador y sueltan lo que produce, además del inventario o en lugar de él |
| `registryRemaps` | booleano | `true` | Aplica los archivos registry_remap, que renombran una entrada de registro para que los mundos guardados antes del cambio de nombre conserven sus bloques e ítems en lugar de perderlos |
| `anvils` | booleano | `true` | Aplica los archivos anvils/*.json, que permiten que un yunque ponga encantamientos con nombre en un ítem a cambio de un coste en niveles, consiga un logro al retirarlo y retenga un ítem sin uso hasta entonces |
| `blockDrops` | booleano | `true` | Aplica los archivos block_drops/*.json, que añaden o sustituyen lo que suelta un bloque cada vez que se rompe, experiencia incluida, para bloques que un pack no posee |
| `functions` | booleano | `true` | Carga los archivos .mcfunction de los packs, para que funcionen en todos los mundos |

### La categoría `tweaks`

*qué hace cada grupo*

Pequeños cambios en el comportamiento de vanilla. Solo configuración, salvo `privacy`, que un pack también puede establecer; consulta [Extra: ajustes de vanilla](#extra-ajustes-de-vanilla).

| Ajuste | Tipo | Valor por defecto | Qué hace |
| --- | --- | --- | --- |
| `promptLeafDecay` | booleano | `true` | Las hojas que pierden su árbol se desintegran en menos de un segundo en lugar de esperar a los ticks aleatorios |
| `lenientPaths` | booleano | `true` | Se pueden hacer caminos bajo un bloque y se mantienen cuando se coloca uno encima |
| `unbreakableSpawners` | booleano | `false` | Los spawners de mobs no se pueden minar ni volar. El modo creativo sigue quitándolos. Requiere reiniciar |
| `experimentalWarning` | booleano | `false` | Muestra el aviso de ajustes experimentales del juego al crear o abrir un mundo. Desactivado lo responde como si hubieras pulsado continuar |
| `privacy` | booleano | `true` | Desactiva la telemetría y los informes de chat del juego: no se envía ni se registra ningún evento de telemetría, el cliente no firma ningún mensaje de chat, el servidor no guarda sesión de chat ni la exige, así que ningún mensaje de nadie puede denunciarse, y el cliente no muestra el aviso emergente de que un servidor no aplica el chat seguro. Un pack puede establecerlo como `privacy` en los `settings` de una plantilla de mundo, dentro del grupo [Servidor](#servidor). Surte efecto en el siguiente mundo o servidor al que se entre |
| `darkSplash` | booleano | `true` | Dibuja la pantalla de carga oscura con el logotipo del cargador de packs en lugar del del juego: el logotipo se cambia al crearse la pantalla, y la opción Logotipo monocromo del juego se activa si aún está desactivada, lo que surte efecto en el siguiente inicio. Desactivado deja la opción como está |

---

# Otros mods

## Integración con Blast Plaster

*otros mods*

`<namespace>/blastplaster/*.json`

El nombre del archivo lo eliges tú, solo se lee la carpeta, y varios archivos se acumulan.

Blast Plaster gestiona el comportamiento posterior a una explosión: reparar cráteres bloque a bloque, talado consciente de los árboles, control de drops. Por sí solo lee una única configuración global. Dirigido desde un pack responde **por dimensión**, y es el pack quien aporta la decisión en lugar de pedir a los jugadores que editen una configuración. Sin archivos de pack, o sin Blast Plaster instalado, nada de esto hace nada, y la carpeta se omite con una línea en el registro.

Las claves escritas en el nivel superior del archivo se aplican en todas partes; un bloque `dimensions` las sustituye para una dimensión por su id. Todo lo que un pack nunca nombra conserva lo que diga la propia configuración de Blast Plaster, de modo que un pack fija las pocas que le importan y deja el resto en paz.

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
  "blockConversions": ["minecraft:stone=minecraft:cobblestone@0.75", "#minecraft:logs=minecraft:stripped_oak_log@0.5"],
  "dimensions": {
    "minecraft:the_nether": { "explosionMode": "HEAL", "minimumTicksBeforeHeal": 200 },
    "minecraft:the_end": { "enableExplosionSmoke": false }
  }
}
```

`explosionMode` es el interruptor principal: `HEAL` restaura el cráter con el tiempo, `EJECT_DROPS` deja el agujero y suelta aproximadamente un tercio de los bloques (comportamiento de vanilla), `VISUAL_TOSS` deja el agujero y no suelta nada. Cuando lo dirige un pack, el valor por defecto es `EJECT_DROPS` (no el `HEAL` de Blast Plaster), de modo que una instalación sin configurar se comporta como vanilla.

| Clave | Valor | Qué hace |
| --- | --- | --- |
| `explosionMode` | `HEAL`, `EJECT_DROPS`, `VISUAL_TOSS` | Qué ocurre después del estallido |
| `healCreepers`, `healNonPlayerTNT`, `healWither`, `healAll` | true o false | Qué explosiones se gestionan |
| `processPlayerIgnitedTNT` | true o false | Si el TNT que encendió un jugador se gestiona con el resto |
| `customEntitiesToHeal` | lista de nombres de entidad | Explosiones de otros mods, nombradas como `modid:entity` |
| `healFullTrees` | true o false | Un árbol alcanzado por una explosión se retira o se restaura entero, en lugar de cortarse a medias |
| `maxTreeSize` | número | El máximo de bloques que puede reclamar un árbol antes de dejarlo en paz |
| `minimumTicksBeforeHeal`, `randomTickVar` | números | Cuánto tarda en empezar la reparación y cuán irregular es su ritmo |
| `overrideBlocks` | true o false | Si la reparación sobrescribe lo que se haya construido desde entonces en el agujero |
| `enableFakeTossedBlocks` | true o false | Los escombros que salen despedidos de la explosión |
| `enableExplosionFlash` | true o false | El destello brillante en el momento de la explosión |
| `explosionFlashDuration`, `explosionFlashLightLevel`, `explosionFlashParticleCount`, `explosionFlashPulses` | números | Cuánto dura el destello, cuánto brilla, cuántas partículas lanza y cuántas veces pulsa |
| `enableExplosionSmoke` | true o false | La columna de humo posterior |
| `explosionSmokeDuration`, `explosionSmokeParticleCount` | números | Cuánto persiste el humo y cuán denso es |
| `playerTNTAlwaysDrops`, `playerTNTDropFullBlocks` | true o false | Lo que deja tras de sí el TNT de un jugador |
| `enableDropSuppression`, `dtSpecialDrops` | true o false | Los drops dentro de una explosión, y los drops propios de Dynamic Trees |
| `preventMobDrops` | true o false | Si los mobs muertos por una explosión siguen soltando drops |
| `blockConversions` | lista de reglas | En qué se convierte un bloque volado en lugar de volver como era, de modo que una construcción se desgasta un paso por explosión |

`blockConversions` decide en qué se convierte un bloque volado en lugar de volver como era. Una regla se lee `<source>=<result>[@chance]`: el origen es un id de bloque, o una etiqueta de bloque con un `#` inicial; el resultado es un id de bloque, o `nothing` para dejar el espacio vacío; la probabilidad va de 0.0 a 1.0 y por defecto es 1.0. Gana la primera regla que coincide, así que las reglas específicas van por encima de las generales, y un bloque que ya es el resultado de alguna regla nunca se vuelve a convertir: un muro cede un paso por explosión en lugar de desgastarse hasta desaparecer.

**Aspecto totalmente vanilla:** `EJECT_DROPS` más `healFullTrees`, `enableFakeTossedBlocks`, `enableExplosionFlash`, `enableExplosionSmoke`, `preventMobDrops` y `playerTNTAlwaysDrops`, todos desactivados. Cada clave admite configuración por dimensión.

**Los clientes vanilla** no notan nada fuera de lo normal. El destello es la única función que coloca un bloque, así que con `vanillaClients` activado se fuerza a desactivado; todo lo demás son partículas e ítems que un cliente normal entiende.

No son claves de pack: el registro de depuración de Blast Plaster y su emparejamiento de tronco y hojas (la identificación de árboles debe tener una única respuesta en todo el juego). Ambos se quedan en la propia configuración de Blast Plaster.

---

# Referencia

## Listas de valores

*referencia*

### Nombres aceptados

*listas de valores*

Estos son los nombres que acepta el analizador siempre que las tablas anteriores dicen "uno de los materiales", y así sucesivamente. Todo lo que no se reconoce se registra y se sustituye por el valor por defecto.

**Materiales de bloque.** `air`, `grass`, `ground`, `wood`, `rock`, `iron`, `anvil`, `water`, `lava`, `leaves`, `plants`, `vine`, `sponge`, `cloth`, `fire`, `sand`, `circuits`, `carpet`, `glass`, `redstone_light`, `tnt`, `coral`, `ice`, `packed_ice`, `snow`, `crafted_snow`, `cactus`, `clay`, `gourd`, `dragon_egg`, `portal`, `cake`, `web`, `piston`, `barrier`, `structure_void`. El juego en sí ya no tiene materiales; cada nombre hace lo que hacía ese material en 1.12.2: fija el color del mapa, si el bloque necesita una herramienta para soltar algo, cómo lo tratan los pistones, si la lava lo prende, si el líquido que fluye lo arrastra y si un bloque colocado lo sustituye.

**Tipos de sonido.** `wood`, `ground`, `plant`, `stone`, `metal`, `glass`, `cloth`, `sand`, `snow`, `ladder`, `anvil`, `slime`.

**Colores de mapa.** `air`, `grass`, `sand`, `cloth`, `tnt`, `ice`, `iron`, `foliage`, `snow`, `clay`, `dirt`, `stone`, `water`, `wood`, `quartz`, `adobe`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `silver`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, `black`, `gold`, `diamond`, `lapis`, `emerald`, `obsidian`, `netherrack`.

**Capas de renderizado.** `solid`, `cutout`, `cutout_mipped`, `translucent`. Si se deja vacío, el bloque elige una adecuada a su tipo.

**Rarezas.** `common`, `uncommon`, `rare`, `epic`.

**Partículas de antorcha.** `none`, `flame`, `colored`. `colored` usa `particleColor`.

**Clases de herramienta.** `pickaxe`, `axe`, `shovel`, `hoe`, `sword`.

**Ranuras de armadura.** `head` o `helmet`, `chest` o `chestplate`, `legs` o `leggings`, `feet` o `boots`.

**Tintes.** `biome`, `none`, o un color hexadecimal de seis dígitos. Los colores en cualquier parte de una definición son hexadecimales, con o sin un `#` inicial.

**Comportamientos** para `behavesAs`. `till`, `path`, `bush`, `animals`.

**Tipos de planta** para `plantTypes`. `plains`, `desert`, `beach`, `cave`, `water`, `nether`, `crop`. Solo en 1.20.1; 1.21.1 lee la clave y la ignora.

**Tipos de bioma**, las palabras que representan una etiqueta de bioma siempre que una tabla dice "lista de tipos de bioma", en `biomeTypes`, en los `types` de un bioma, en los `roles` de una plantilla y en una sección `biomes`: `ocean`, `deepocean`, `beach`, `river`, `mountain`, `mesa`, `hills`, `coniferous`, `jungle`, `forest`, `savanna`, `overworld`, `nether`, `end`, `hot`, `cold`, `sparse`, `dense`, `wet`, `dry`, `spooky`, `dead`, `lush`, `mushroom`, `magical`, `rare`, `plateau`, `modified`, `water`, `desert`, `plains`, `swamp`, `sandy`, `snowy`, `wasteland`, `void`. Las palabras de vanilla se corresponden con las etiquetas `minecraft:is_*` y el resto con las etiquetas de convención, `forge:is_*` en 1.20.1 y `c:is_*` en 1.21.1. Una etiqueta escrita completa, `minecraft:is_forest` o `#minecraft:is_forest`, se toma tal cual. No se distinguen mayúsculas, así que el `FOREST` de 1.12.2 sigue leyéndose.

**Roles** para los `roles` de una plantilla de mundo. Cualquier palabra de tipo de bioma de las anteriores: cada una nombra un bioma que ocupa los biomas con esa etiqueta una vez que el bloqueo los ha eliminado, de modo que `"ocean": "mypack:ruby_ocean"` pone el océano de rubí allí donde se bloqueó un océano.

**Estructuras** para las `structures` de una plantilla de mundo y para las listas propias del grupo `structures`: los nombres de 1.12.2 `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges` y `endcities`, o cualquier conjunto de estructuras que traiga el juego o un mod, como `pillager_outposts`, `ancient_cities`, `trail_ruins`, `shipwrecks`, `ocean_ruins`, `ruined_portals`, `nether_fossils`, `buried_treasures`, `desert_pyramids`, `jungle_temples`, `igloos`, `swamp_huts`, `woodland_mansions`, `ocean_monuments`, `nether_complexes`, `end_cities`. Un nombre de 1.12.2 se lee como los conjuntos que representaba, así que `temples` son las pirámides, los templos de la jungla, los iglús y las chozas de pantano juntos. También se leen los nombres de populate de 1.12.2, como las partes del mundo de esta versión que representan: `caves` los tallados de cuevas (las cuevas de ruido son `noiseCaves`), `ravines` los cañones, `dungeons` las salas de monstruos, `lavalakes` los lagos de lava, `netherlava` los manantiales de lava abiertos del Nether, `fire` los parches de fuego del Nether, `glowstone` su piedra luminosa, `ice` la capa superior helada y `animals` los animales colocados al crearse un chunk. `waterlakes` se acepta y no hace nada, ya que esta versión no tiene lagos de agua.

**Tipos de criatura** para las apariciones y tasas de los biomas. `creature`, `monster`, `ambient`, `water`. La aparición de una variante de entidad también admite por nombre las demás listas de esta versión, como `water_ambient` o `underground_water_creature`.

**Tipos de mineral** para `oreTypes`. `COAL`, `IRON`, `COPPER`, `GOLD`, `REDSTONE`, `DIAMOND`, `LAPIS`, `EMERALD`, `QUARTZ`, `DIRT`, `GRAVEL`, `DIORITE`, `GRANITE`, `ANDESITE`, `TUFF`, `CLAY`, `SILVERFISH`, `CUSTOM` para cualquier otro mineral.

**Tipos de daño** para los `immuneTo` de una variante de entidad, en cualquier caja y con o sin guiones bajos. Los nombres de 1.12.2, cada uno abarcando lo que abarcaba allí: `inFire` (una hoguera también), `onFire` (una bola de fuego que nadie disparó también), `lava`, `hotFloor`, `inWall` (el borde del mundo también), `cramming`, `drown`, `starve`, `cactus`, `fall`, `flyIntoWall`, `outOfWorld` (`/kill` también), `generic`, `magic`, `indirectMagic`, `wither`, `anvil`, `fallingBlock`, `dragonBreath`, `fireworks`, `lightningBolt`, `thorns`, `arrow`, `fireball`, `thrown`, `mob` para el golpe de una criatura, el escupitajo de una llama, la bala de un shulker o la calavera de un wither, `player` para el golpe de un jugador, `explosion` para una explosión que nadie provocó, como la de una cama, y `explosion.player` para una explosión que provocó una criatura o un jugador, un creeper o TNT encendido. El daño propio de esta versión también tiene nombres: `fire` para cualquiera de los dos tipos de quemadura, `lightning`, `void`, `freeze`, `dryOut` y `sweetBerry`. Cualquier otra cosa se lee como un id de tipo de daño, `minecraft:sonic_boom` o uno propio de un mod.

**Nombres de sonido** para los `sounds` de una variante de entidad, el `openSound` de un cofre con cerradura y el `sound` de un portal: cualquier evento de sonido registrado, del juego, de un mod o uno que un pack añade mediante `sounds`. Un nombre de 1.12.2 se lee como el nombre que tiene ahora ese sonido, así que `entity.endermen.scream` reproduce `entity.enderman.scream`, `block.cloth.step` reproduce `block.wool.step`, `entity.small_slime.squish` reproduce `entity.slime.squish_small` y `record.cat` reproduce `music_disc.cat`. Las cuatro imitaciones de loro que esta versión eliminó, del enderman, el oso polar, el lobo y el piglin zombificado, no reproducen nada.

## Lista de carpetas

*referencia*

Todas las carpetas, con su ruta completa y un enlace a la sección que las describe, están en [Dónde van los archivos](#dónde-van-los-archivos).

## Comandos

*referencia*

### Tus propios comandos

*comandos*

`/rdpl` se ejecuta en tu propio equipo y no necesita permisos, porque todo lo que toca es tuyo. Una recarga vuelve a examinar la carpeta que posees y actualiza tus propios recursos; no llega a ningún servidor, así que la copia del servidor se recarga con `/rdplserver reload`. En un jugador las dos son una misma máquina, así que `/rdpl reload` es también lo que vuelve a aplicar tus [sobrescrituras de propiedades](#sobrescritura-de-propiedades) y vuelve a formar tus equipos.

| Comando | Nivel | Qué hace |
| --- | --- | --- |
| `/rdpl list` | ninguno | Todos los packs cargados, su prioridad y lo que contienen. Haz clic en un pack para buscar un archivo en él |
| `/rdpl which <namespace:path>` | ninguno | Qué pack aporta un archivo dado y a qué packs eclipsa |
| `/rdpl reload` | ninguno | Vuelve a examinar la carpeta y recarga todo, incluida la recarga de recursos del propio juego |
| `/rdpl unused` | ninguno | Archivos de tus packs que nadie ha pedido todavía, normalmente una errata en una ruta |
| `/rdpl config unused` | ninguno | Archivos de opciones de `rdploader/config` que ya no define ningún pack instalado |
| `/rdpl config prune` | ninguno | Elimina esos archivos |
| `/rdpl pixelmap <namespace:path>` | ninguno | En qué resultó un [mapa de píxeles](#texturas-escritas-como-mapas-de-píxeles), carácter por carácter |
| `/rdpl biome`, `biome list [all]` | ninguno | Todos los biomas que pueden generarse y su id; `all` incluye los que nada puede generar |
| `/rdpl biome here` | ninguno | El bioma en el que estás: su nombre, id y número |
| `/rdpl biome find <name>` | el del servidor | Enlazado. Se pasa palabra por palabra a `/rdplserver`, que decide, así que consulta la tabla de abajo |
| `/rdpl locate`, `goto`, `vein`, `gate`, `pregen`, `intro`, `team`, `round`, `dimensions`, `oregen` | el del servidor | Enlazado. Se pasa palabra por palabra a `/rdplserver`, que decide, así que consulta la tabla de abajo |

**Qué subcomandos del servidor están enlazados, y por qué el resto no.** `locate`, `goto`, `vein`, `gate`, `pregen`, `intro`, `team`, `round`, `dimensions` y `oregen` solo pueden referirse al servidor, ya que solo el servidor conoce el mundo, sus jugadores y sus rondas, así que `/rdpl` se los traspasa. En un jugador, el autocompletado tras uno de ellos ofrece lo que ofrecería `/rdplserver`; en un servidor, `goto` ofrece los nombres de estructuras de vanilla. El resto, `reload`, `list`, `which`, `unused`, `config`, `pixelmap` y `biome`, conservan su propio significado sobre tus packs y tu cliente. La comprobación de permisos del propio servidor decide un comando enlazado, de modo que un cliente no puede ni burlarlo ni recibir una respuesta fabricada.

**Edición del día a día:** F3+T recarga texturas, modelos y archivos de idioma, y `/reload` los datos del servidor. Usa `/rdpl reload` cuando *añadas* o *elimines* un archivo, ya que eso cambia lo que contiene la carpeta.

### Comandos de servidor

*comandos*

En un servidor dedicado, `/rdplserver` hace lo mismo con la copia propia del servidor de la carpeta. La columna Nivel es el nivel de permiso que necesita quien lo envía: `3` es un operador, `2` admite también bloques de comandos, `0` es cualquier jugador, y `4` está por encima de operador y no alcanza a nadie.

#### Packs y archivos

*comandos de servidor*

| Comando | Nivel | Qué hace |
| --- | --- | --- |
| `/rdplserver reload` | 3 | Vuelve a examinar la carpeta del servidor y recarga todo, y después vuelve a formar los equipos y los objetivos |
| `/rdplserver list` | 3 | Todos los packs que cargó el servidor, su prioridad y lo que contienen |
| `/rdplserver which <namespace:path>` | 3 | Qué pack aporta un archivo dado y a qué packs eclipsa |
| `/rdplserver unused` | 3 | Archivos de los packs del servidor que nadie ha pedido |
| `/rdplserver config unused` | 3 | Archivos de opciones de `rdploader/config` que ya no define ningún pack instalado |
| `/rdplserver config prune` | 3 | Elimina esos archivos |
| `/rdplserver pixelmap <namespace:path>` | 3 | En qué resultó un mapa de píxeles |

#### Mundo y generación

*comandos de servidor*

| Comando | Nivel | Qué hace |
| --- | --- | --- |
| `/rdplserver oregen` | 3 | Totales acumulados de la generación de minerales que se bloqueó, por mod y tipo |
| `/rdplserver generators` | 3 | Totales acumulados de los generadores de mundo que se bloquearon, por mod y tipo |
| `/rdplserver biome list [all]` | 3 | Todos los biomas que pueden generarse en el servidor, con su número, id y nombre; `all` incluye los que nada puede generar |
| `/rdplserver biome` | 3 | El bioma en el que estás y lo que el pack hace con él: su id, número y nombre, si `blockBiomes` está activado y qué plantilla de mundo está activa, y el suelo, el bloque bajo él y la piedra en y 40 |
| `/rdplserver biome here [player]` | 3 | El bioma en el que estás tú, o el jugador indicado: su nombre, id y número. La consola nombra a un jugador |
| `/rdplserver biome find <name>` | 3 | El lugar más cercano, a menos de 6400 bloques, donde se genera un bioma que coincida con ese id o nombre mostrado: sus coordenadas y la distancia desde donde se ejecuta. Lo dice cuando no hay ninguno tan cerca, o cuando el nombre no coincide con ningún bioma. `/rdpl biome find` le reenvía |
| `/rdplserver dimensions` | 3 | Todas las dimensiones, incluidas las que añadieron los packs |
| `/rdplserver vein <entry> [radius]` | 3 | Dónde tiene sembradas sus vetas una entrada de worldgen de forma `vein` dentro de ese número de chunks (por defecto 8) desde donde se ejecuta, la más cercana primero, existan ya o no esos chunks. `/rdpl vein` le reenvía |

#### Comandos de portales

*comandos de servidor*

| Comando | Nivel | Qué hace |
| --- | --- | --- |
| `/rdplserver gate`, `gate list` | 3 | Todos los portales, su dimensión, su ámbito y si está abierto |
| `/rdplserver gate check <player>` | 3 | Qué portales ha cruzado un jugador |
| `/rdplserver gate grant <player> <gate>` | 3 | Abre un portal para un jugador |
| `/rdplserver gate revoke <player> <gate>` | 3 | Lo cierra de nuevo |

#### Comandos de pregeneración

*comandos de servidor*

| Comando | Nivel | Qué hace |
| --- | --- | --- |
| `/rdplserver pregen <radius>` | 3 | Crea todos los chunks dentro de ese número de chunks desde donde se ejecuta. Consulta [Pregeneración](#pregeneración) |
| `/rdplserver pregen status` | 3 | Cuánto lleva avanzada una ejecución |
| `/rdplserver pregen stop` | 3 | La termina |

#### Jugadores, equipos y rondas

*comandos de servidor*

| Comando | Nivel | Qué hace |
| --- | --- | --- |
| `/rdplserver intro` | 0 | Deja que la introducción al mundo vuelva a reproducirse en tu próxima entrada. Cualquier jugador puede ejecutarlo, y solo borra la suya; se rechaza cuando ningún pack tiene introducción |
| `/rdplserver team`, `team join [name]`, `team leave`, `team vote <player>`, `team claim` | 0 | Los bandos que un pack ha formado y las vías para entrar y salir de ellos, ofrecido solo mientras un pack forma bandos, consulta [Equipos](#equipos) |
| `/rdplserver round start`, `round reset`, `round vote yes`, `round vote no` | 0 | Inicia una ronda que el pack mantiene en un vestíbulo, reinicia una ronda en curso o convoca una votación para ello, y vota en una, según lo permita la puntuación del pack, y solo un jugador inicia una; ofrecido a los jugadores solo mientras un pack lleva puntuación, consulta [Puntuación](#puntuación) |
| `/rdplserver card <rule> [players]` | 2 | Muestra una [regla de tarjeta](#tarjetas) a los jugadores indicados, o a ti mismo, por su id o nombre de archivo. `when`, `repeat` y `cooldown` se omiten |
| `/rdplserver reset` | 3 | Deja el mapa como lo hace el final de una ronda: todos quedan retenidos, las entidades se barren, las puntuaciones se borran, se ejecuta `resetRuns`, los jugadores se colocan en `resetSendsTo` y se liberan, y se abre una ronda con el recuento inicial, como describen los ajustes de reinicio de [Pregeneración](#pregeneración). No se traspasa desde `/rdpl` |

#### Ir a lugares

*comandos de servidor*

| Comando | Nivel | Qué hace |
| --- | --- | --- |
| `/rdplserver locate <name>` | 3 | La estructura más cercana que un pack colocó con ese nombre `locateAs` |
| `/rdplserver goto <structure>` | `gotoLevel`, `3` | Te lleva a la más cercana a la que nadie ha ido aún, buscando sin generar el terreno por el camino. Un lugar que un pack registró con `locateAs` es el más cercano colocado, visitado o no. `temple` significa todas las características dispersas: templos del desierto y de la jungla, chozas de bruja e iglús. Se rechaza mientras se está creando terreno |
| `/rdplserver goto <structure> next` | `gotoNextLevel`, `3` | Te lleva más allá, a la más cercana a la que no te han llevado en esta sesión, haya sido visitada antes o no. Una a menos de ocho chunks de ti se pasa por alto; para el lugar de un pack es la más cercana a más de 128 bloques |
| `/rdplserver goto <structure> back` | `gotoBackLevel`, `3` | Te lleva a la anterior, retrocediendo por los lugares a los que te ha enviado esta sesión |

### Quién puede usar goto

*comandos*

**Abrir `goto`.** Cada parte de `/rdplserver` necesita un operador, nivel 3, excepto `intro`, `team` y `round`, que puede ejecutar cualquier jugador, como los tiene 1.12.2. Las tres formas de `goto` son lo único que decide un pack: cada una lleva un nivel de permiso propio que un pack o la configuración pueden bajar, por separado de las otras dos y del resto del comando. Un pack que quiere que `reset` esté al alcance de los jugadores lo pone en un bloque de comandos o en una función, que se ejecuta con nivel 3.

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

| Ajuste | Qué rige |
| --- | --- |
| `gotoLevel` | `goto <structure>` |
| `gotoNextLevel` | `goto <structure> next` |
| `gotoBackLevel` | `goto <structure> back` |
| `gotoPlaceLevels` | Un lugar con nombre, en las tres formas |

El valor es el nivel de permiso que necesita quien lo envía. `3` (operador) es el valor por defecto. `2` admite también bloques de comandos, de modo que un pack puede poner un salto en un botón o una placa de presión sin exponer el resto de `/rdplserver`. `0` lo abre a cualquier jugador. Los tres ajustes son independientes: por ejemplo, `next` abierto a los bloques de comandos para una visita guiada a una aldea mientras `back` sigue siendo solo para operadores. Un valor inferior a 0 cuenta como 0 y uno superior a 4 como 4, y a un operador siempre se le ofrece el propio `goto`, digan lo que digan los ajustes.

`gotoPlaceLevels` sustituye a los tres ajustes para lugares concretos, como entradas `name=level`, como en el ejemplo anterior. El nombre es lo que escribirías tras `goto`: uno de vanilla como `village` o `mansion`, o un nombre registrado con `locateAs` en una entrada `imprint`. La comparación ignora las mayúsculas. Un nivel de `4` está por encima de operador y cierra ese lugar a todo el mundo, la manera de ocultar un lugar mientras el resto de `goto` está abierto.

Una entrada fija un nivel para las tres formas de ese lugar. Un lugar que no figura recurre a los tres ajustes anteriores, y un nombre no registrado nunca coincide. El autocompletado sigue las mismas reglas, así que tras `goto` a quien lo envía solo se le ofrecen los lugares a los que realmente se le puede llevar.

Estos están en el grupo `commands`, así que `control.commands` en la configuración decide si un pack puede establecerlos siquiera, y `off` allí mantiene todo en operador pida lo que pida un pack.

## Conviene saber

*referencia*

- KubeJS y CraftTweaker se ejecutan después de RDPL, así que sus cambios siguen prevaleciendo.
- Las recetas, tablas de botín, logros y funciones son aquí los archivos de datos propios del juego, así que `/reload` recoge una edición y `/rdpl reload` un archivo nuevo.
- Una estructura que ya se ha generado permanece cargada hasta que sales del mundo.
- Las mayúsculas en los nombres de archivo importan. Si las mayúsculas de tu archivo no coinciden con lo que pidió el juego, RDPL lo carga igualmente pero te avisa, porque en Linux no se encontraría en absoluto.
- Pon un `pack.png` en `rdploader` para darle un icono al pack. Sin él se muestra el icono de RDPL.
- La carpeta se puede mover o renombrar con la opción `rootDirectory` de `config/resourcedatapackloader-common.toml`. También funciona una ruta absoluta, y requiere reiniciar.
- Un modelo que nombra un modelo de vanilla ya terminado hereda también las texturas de vanilla. Los modelos padre como `cube_all` y `cross` toman sus texturas del modelo que los nombra y no hay problema.
- La telemetría y los informes de chat del juego están desactivados mientras `privacy` de la categoría `tweaks` esté activado, que lo está por defecto: no se envía nada, no se firma ningún mensaje de chat, un servidor que ejecuta el mod no guarda sesión de chat para nadie, y unirse a un servidor que no aplica el chat seguro no muestra ningún aviso emergente.
- Una opción de pack cambiada la recuerda el mundo al que afecta; cuando el cambio deja sin registrar contenido que el mundo contiene, se hace una copia de seguridad del mundo en la carpeta `backups` del juego antes de volver a abrirlo, y se queda cerrado si esa copia falla.

## Cuando algo no funciona

*referencia*

**Revisa primero `logs/rdpl.log`.** Todo lo que hace RDPL va allí en lugar de al registro principal. Los logros, tablas de botín, recetas, funciones, estructuras y cada pieza de contenido se registran con el pack del que vinieron, y todo lo que esté mal formado se registra con el motivo.

**Las texturas y otros assets son distintos.** Se piden con demasiada frecuencia para registrarlos uno por uno, así que en su lugar `/rdpl unused` lista los archivos de tus packs que nadie ha pedido. Ejecútalo cuando el juego haya terminado de cargar. Un archivo con la ruta correcta siempre se pide, así que lo que aparezca en la lista suele ser una errata, pero ten en cuenta que algunos archivos solo se cargan cuando se necesitan, como los idiomas distintos del que juegas.

**Un zip sin un directorio `assets` o `data` dentro se omite,** y también cualquier carpeta de `rdploader`, y el registro lo indica. Un zip cuyo nivel superior es una carpeta que los envuelve se omite igual.

**`/rdpl which minecraft:textures/block/stone.png`** te dice exactamente qué pack está sirviendo un archivo y a qué está eclipsando.

**Un pack escrito para 1.12.2 se lee a través del port hacia adelante.** Consulta [Packs escritos para 1.12.2](#packs-escritos-para-1122); el registro nombra cada archivo que movió, omitió o no pudo trasladar.

## Extra: ajustes de vanilla

*referencia*

Pequeños cambios en el comportamiento de vanilla, cada uno activable en la categoría de configuración `tweaks`.

| Opción | Valor por defecto | Qué hace |
| --- | --- | --- |
| `promptLeafDecay` | activado | Las hojas que pierden su árbol se desintegran en menos de un segundo en lugar de esperar a los ticks aleatorios |
| `lenientPaths` | activado | Se pueden hacer caminos bajo un bloque y se mantienen cuando se coloca uno encima |
| `unbreakableSpawners` | desactivado | Los spawners de mobs no se pueden minar ni volar. El modo creativo sigue quitándolos. Necesita reiniciar |
| `experimentalWarning` | desactivado | Muestra el aviso de ajustes experimentales del juego al crear o abrir un mundo. Desactivado lo responde como si hubieras pulsado continuar |
| `privacy` | activado | Desactiva la telemetría y los informes de chat del juego; consulta [Conviene saber](#conviene-saber) |
| `darkSplash` | activado | Pantalla de carga oscura con el logotipo del cargador de packs; la opción Logotipo monocromo del juego se activa para el siguiente inicio |

Otras cuatro están en la categoría `content` y no en `tweaks`:

| Opción | Valor por defecto | Qué hace |
| --- | --- | --- |
| `cactusMaxHeight` | `3` | Hasta qué altura crece el cactus de vanilla |
| `caneMaxHeight` | `3` | Hasta qué altura crece la caña de azúcar de vanilla |
| `shovelPaths` | activado | Una pala convierte en camino los bloques marcados con `behavesAs` path, y agacharse revierte uno |
| `hoeTilling` | activado | Una azada labra los bloques marcados con `behavesAs` till |

**Nada de esto afecta a un pack.** Estas opciones solo cambian el cactus, la caña, las hojas y los caminos propios de Minecraft. Un bloque que tu pack define con `"type": "cane"` lleva su propia sección `growth` y crece hasta la altura que le hayas dado, sea lo que sea lo que haya instalado.

### Spawners irrompibles

*extra: ajustes de vanilla*

`unbreakableSpawners` da al bloque de spawner de mobs los números del lecho de roca, una dureza irrompible y una resistencia a las explosiones que nada supera. Un jugador no puede minar uno por buena que sea la pica, y ni los creepers, ni el TNT, ni una entidad de pack que `explodes` se llevarán uno. El modo creativo sigue quitándolos, exactamente como sigue quitando el lecho de roca, así que un autor de packs nunca queda excluido de su propia construcción. Requiere reiniciar, ya que los números del bloque se fijan al registrarse.

**Es el bloque, no el spawner.** No hay un interruptor por spawner. La opción cambia `minecraft:spawner` en sí, así que alcanza a todos los spawners del mundo a la vez: las estructuras de vanilla que colocan uno, los que coloca cualquier mod y los que colocan tus propios packs. Un spawner dentro de una de tus plantillas `.nbt`, colocado por una entrada `imprint`, es un bloque de spawner corriente con su propia entidad de bloque, así que queda cubierto en cuanto la opción está activada.

## Claves que no se mantuvieron

*referencia*

Lo que un pack de 1.12.2 puede escribir y esta versión no lee, y por qué. Un pack que escribe una sigue cargando; la clave no hace nada. Una fila que dice *aún no trasladada* se añadió a 1.12.2 después de portar esa parte y aún está por llegar. Todas las demás filas no pueden, o no necesitan, pasar a este motor. Una fila marcada *desde 26.3* es lo que un pack de 26.3 puede escribir y el formato anterior no puede contener, y una marcada *hasta 26.3* es lo que un pack anterior puede escribir y 26.3 no puede contener; el registro nombra cada una al descartarse. La tabla se mantiene al día a medida que se mueven las líneas.

| Clave | Dónde | Por qué |
| --- | --- | --- |
| `meta` | bloques, ítems, worldgen, drops de bloques | Los ids no llevan metadatos desde la aplanación. El port pasa cada `name:meta` por los reparadores de datos del juego y descarta la clave |
| `oreDict` | bloques, ítems, horno, combustibles, filtros de almacenamiento de entidades | El diccionario de minerales ya no existe. El port lo convierte en `tags` de las etiquetas de convención, y el de un combustible o de una entrada de filtro de almacenamiento en `tag` |
| `galacticraft` | variantes de entidad | No hay Galacticraft para esta versión, así que una variante no tiene nivel de cohete, depósito de combustible, carga ni carga útil que establecer |
| `oreDictionary` | ajustes | El diccionario de minerales ya no existe, así que no quedan archivos de diccionario de minerales que desactivar. Las etiquetas hacen su trabajo, y el port las escribe a partir del `oreDict` de un pack |
| `modelMeta` | bloques | Los modelos se generan por variante, así que no hay metadatos por los que asignarlos |
| `disableOverrides`, `tolerateMissingInAdvancements` | ajustes | Un pack de datos sustituye una receta o un logro de vanilla incluyendo uno con el mismo nombre |
| nombres de ítem `#CONSTANT` | recetas | Las constantes de recetas vivían en el `_constants.json` de un mod de 1.12.2, que ningún pack lleva y ningún mod de esta versión tiene. Nombra el ítem o la etiqueta que representaba la constante |
| `harvestTool` distinto de `pickaxe`, `axe`, `shovel`, `hoe` y `sword` | sobrescrituras de propiedades | Aquí las herramientas minan por etiquetas de bloque, y solo esas cinco tienen una. Una clase de herramienta inventada por un mod de 1.12.2 no tiene etiqueta que escribir, así que las herramientas del bloque se dejan como están y el registro lo dice |
| `careers` | aldeanos | No hay carreras desde 1.14, así que cada carrera se convierte en un archivo de aldeano propio |
| `career` | intercambios, entidades | Sin carreras; nombra la profesión en sí. Un intercambio que nombra una profesión de vanilla de 1.12.2 con su carrera pasa a la profesión en que se convirtió esa carrera |
| `gameLoopFunction` | reglas del juego | La regla del juego ya no existe; la etiqueta de función `#minecraft:tick` ejecuta una función cada tick, y el port escribe una |
| `id` | biomas, dimensiones | Los biomas y las dimensiones se conocen por su ubicación de recurso, nunca por un número |
| `suffix`, `keepLoaded` | dimensiones | La carpeta de guardado sigue el nombre de la dimensión. Solo el mundo principal tiene chunks de aparición, así que una dimensión que debe permanecer cargada toma un `forceload` |
| `baseHeight`, `heightVariation` | biomas | La altura del terreno pertenece a los ajustes de ruido, no al bioma |
| `placement.villageSpawn` | biomas | Los aldeanos vienen con la propia estructura de la aldea |
| `rubicWorld`, `rubicWorldDimensions`, `rubicWorldDimensionsAreBlacklist`, `verticalCubeLoadDistance`, `cubeGCInterval`, `cubeGenMillisPerRound`, `cubesSentPerTick` | mundos rubic | El mundo rubic era la manera de 1.12.2 de superar un mundo de 256 bloques. Aquí, el `minHeight` y el `maxHeight` de una dimensión fijan su tamaño y el sistema de chunks lo transmite |
| `rubicHeightLimit` | mundos rubic | El techo de altura del mundo rubic se fue con él; en su lugar, el `minHeight` y el `maxHeight` de una dimensión fijan su tamaño, sin un techo aparte que subir |
| `regionCacheLimit` | mundos rubic | Su caché de los archivos de región abiertos de un mundo rubic se fue con el mundo rubic; el sistema de chunks de aquí gestiona sus propios archivos |
| `skyStone`, `skyShape`, `skyIslands`, `skyThickness`, `skyHeights`, `skyAnimals` | el mundo profundo, biomas, regiones de cuevas | El terreno flotante vivía por encima de la ventana de terreno de un mundo rubic, y no hay mundo rubic que lo contenga |
| `deepRavines`, `oreVeins` | el mundo profundo | El terreno propio del motor ya funciona por debajo de y 0, con barrancos y grandes vetas de mineral propios |
| `terrainOffset` | ajustes | No hay una ventana fija de vanilla que desplazar; el `minHeight` y el `maxHeight` de una dimensión fijan su suelo y su techo |
| `terrainWorldTypes`, `terrainWorldTypesAreBlacklist` | ajustes | Los tipos de mundo son aquí preajustes de mundo, y una plantilla de mundo nombra los suyos |
| `biomeSize`, `riverSize`, `dungeonChance`, `waterLakeChance`, `lavaLakeChance`, las claves de tamaño, cantidad y altura de minerales y las claves de escala de ruido de un `generatorOptions` personalizado | ajustes, dimensiones | El tamaño de los biomas y la forma del terreno pertenecen a los ajustes de ruido, y la frecuencia con que se coloca una característica o un mineral a su propia característica colocada, así que ningún número único los alcanza. Un `fixedBiome` numerado por encima de 39 nombra un bioma que esta versión no puede asignar |
| `useWaterLakes`, y `lake` y `dungeon` en un texto de superplano | ajustes, dimensiones | El juego no tiene lagos de agua desde 1.18. Las mazmorras de un mundo plano vienen con `decoration` y no por sí solas |
| `inherit` como origen de `biomes` de una dimensión `flat` o `void` | dimensiones | Un generador plano contiene un único bioma, así que una dimensión así toma el que nombra su texto de superplano, o llanura |
| la dimensión de otro mod en `flatBedrockDimensions` o `voidWorldDimensions` | ajustes | El lecho de roca y el vacío se escriben en el preajuste de mundo generado y en los archivos de dimensión del propio pack; una dimensión que crea otro mod se construye con archivos propios |
| el tipo de mundo de un mod, o `debug_all_block_states`, como `worldType` | ajustes | Los tipos de mundo son ahora preajustes de mundo, y el mundo moldeado se construye sobre el ruido por defecto del propio juego, biomas grandes, amplificado o plano. Un mundo de depuración no tiene terreno que moldear |
| `pregenKeepLoaded`, `pregenPauseAbove`, `pregenMillisPerRound`, `pregenRelightSays`, `hurryWritesAbove` | pregeneración | Ajustaban el escritor de chunks y el paso de reiluminación de 1.12.2. El sistema de chunks de aquí ilumina el terreno al crearlo y escribe con su propio calendario |
| `readCofhWorldFiles` | ajustes | CoFH World y su propio formato de archivo no existen en este motor. Traducir esos archivos a un pack, como ya recomendaba 1.12.2, sigue siendo el camino |
| `name:meta` en `villagePathLamp*` | aldeas | Los ids no llevan metadatos, así que un bloque de farol se escribe con su estado entre corchetes, y los datos de bloque entre llaves siguen leyéndose |
| `harvestTool` que nombra `shears` o una clase de herramienta de un mod | bloques | Aquí una herramienta lee etiquetas de bloque, no un nombre de clase, así que nada responde a uno. Nombra la propia etiqueta de bloque de la herramienta del mod en las `tags` de la variante |
| la etiqueta de pestaña de otro mod en `creativeTab` | bloques, ítems, fluidos | Ahora una pestaña se conoce por su id, así que una etiqueta suelta se lee como una pestaña del propio pack. Nombra la pestaña del mod por su id, como `modid:main` |
| `/rdpl reload <group>` | comandos | El juego recarga todos los recursos en una sola pasada, así que las texturas, modelos, idiomas, sonidos y shaders no pueden recargarse por separado. `/rdpl reload` o F3+T los recarga todos |
| `modernChestPlacement` | ajustes de vanilla | El juego empareja los cofres así desde 1.13: un cofre se une a un cofre simple contiguo solo cuando ambos miran en la misma dirección, y agacharse lo mantiene simple |
| `loadingScreenPercent` | ajustes | La propia pantalla de carga del mundo del juego ya muestra cuánto del área de aparición está listo |
| `disableOptimizations` | ajustes | Desactivaba las optimizaciones de pregeneración y generación de 1.12.2, escritas para ese motor y sin equivalente aquí |
| `fixTinkersModelErrors` | ajustes | Silenciaba los errores de modelo que las versiones de 1.12.2 de Tinkers' Construct y Construct's Armory registraban para cada herramienta, pieza y pieza de armadura. El arreglo se metía en esas versiones, así que aquí no hay nada sobre lo que actuar |
| criterios `achievement.` | puntuación, funciones | Los logros pasaron a ser avances, que no llevan puntuación que contar. Un criterio `stat.` se lee como la estadística en que se convirtió |
| `toggledownfall`, `stats` | funciones | Los comandos ya no existen. `weather` nombra el clima que establecer, y `execute store` guarda el resultado de un comando |
| `gamerule gameLoopFunction`, y una regla del juego inventada por un pack | funciones | El juego decide qué reglas del juego existen. El port convierte el `gameLoopFunction` de un archivo de reglas del juego en la etiqueta `#minecraft:tick`, pero una línea de comando que lo establece se conserva para que la quites tú |
| un nombre con el valor de datos `-1` o `*` que se convirtió en varios bloques o ítems | funciones: `clear`, `testforblock`, `execute ... detect`, `fill ... replace`, `clone ... filtered` | Ahora una comprobación nombra un solo bloque o ítem. Nombra el que se quería, o una etiqueta como `#minecraft:wool` |
| un estado de bloque escrito como pares `name=value` que no es el estado completo del bloque en 1.12.2, y un valor de datos en un bloque o ítem de otro mod | funciones | Los reparadores de datos solo conocen estados completos de 1.12.2, y los metadatos de otro mod no tienen un nombre aplanado al que ir |
| las partículas `footstep` y `take`, `locate Temple`, `spreadplayers` con más de un objetivo | funciones | Las partículas ya no existen, un templo son ahora cuatro estructuras, y `spreadplayers` toma un solo argumento de objetivo |
| `debug_functions` | ajustes de ruido, *desde 26.3* | 26.2 y anteriores no tienen funciones de densidad de depuración, así que la lista se descarta |
| `exclusion` y `surface_level` en `aquifers` | ajustes de ruido, *desde 26.3* | 26.2 y anteriores incorporan ambos de serie, así que los del propio pack se descartan |
| una entrada `spawn_target` que nombra algo que no sea un eje climático del `noise_router` | ajustes de ruido, *desde 26.3* | Un objetivo de aparición anterior cubre solo los cinco ejes climáticos, así que esa entrada se descarta |
| vetas de mineral colocadas mediante `material_rule` | ajustes de ruido, *desde 26.3* | 26.2 no puede colocar vetas de mineral mediante una regla de material, así que se descartan |
| el `count`, `thickness`, `weird_thickness_bias` o `start_vertical_radius_multiplier` propios de un tallador de cuevas | talladores, *desde 26.3* | 26.2 no puede establecerlos, así que se usa la forma de cueva de vanilla del mundo principal o del Nether |
| `creature_world_gen_spawn_probability` como modificador, y un atributo que se añade al valor de su dimensión | biomas, *desde 26.3* | 26.2 toma un valor simple: se conserva la probabilidad de aparición por defecto, y el valor propio del bioma sustituye al de la dimensión |
| desplazamientos x y z distintos, y `normalize` establecido en false | características colocadas, ruidos, *desde 26.3* | 26.2 dispersa ambos ejes en la misma cantidad, así que ambos usan el desplazamiento x, y siempre normaliza un ruido |
| un tipo de característica que 26.2 no tiene, o una característica de 26.3 construida con bloques que no tiene | características, biomas, *desde 26.3* | Una característica así no coloca nada, y un bioma omite una característica colocada de 26.3 sin equivalente en 26.2 |
| `straw_bed_rule`, y `destroy_on_leave` en una regla de cama | atributos de entorno, *desde 26.3* | 26.2 no tiene ninguna de las dos reglas, así que ambas se descartan |
| un ítem con cantidad o componentes entre las decoraciones de vasija | archivos de datos, funciones, *desde 26.3* | 26.2 lee un ítem simple, así que la cantidad y los componentes se descartan |
| `compute`, `posteffect`, `item fill`, `item override` y `execute if slots`, y la animación de `swing` | funciones, *desde 26.3* | 26.2 no tiene ninguno de estos comandos, así que la línea se descarta; una línea `swing` se conserva y balancea de forma simple |
| `shade_direction_override` distinto de `up`, y `trim_overrides` | modelos, assets de equipamiento, *desde 26.3* | 26.2 sombrea normalmente un elemento así, y lee las paletas de adorno por equipamiento del `override_armor_assets` del material de adorno |
| componentes en un ítem elaborado, y una etiqueta o lista de ítems de la que elaborar | recetas de elaboración de pociones, *desde 26.3* | Una receta de elaboración de 26.2 no puede nombrar ninguna de las dos cosas: los componentes se descartan, y una receta que elabora a partir de una etiqueta o lista se descarta entera |
| un proveedor de números para el que 26.2 no tiene forma, y un valor de reserva distinto de 0 | tablas de botín, predicados, modificadores de ítems, *desde 26.3* | El proveedor pasa a ser 0, y una puntuación o un número almacenado ausente recurre a 0 |
| una prueba de condición de bloque distinta de `blocks` y `state`, un conjunto de ids donde 26.2 lee uno, una etiqueta de predicados o de modificadores de ítems, un mapa de explorador a estructuras con nombre | tablas de botín, predicados, modificadores de ítems, *desde 26.3* | La prueba se descarta, solo se conserva el primer id, una etiqueta se lee como el único id que nombra, y el mapa lleva a estructuras de tesoro |
| `depth` y `offset` en `spawn_target`, y un objetivo de aparición dirigido a un eje de `noise_router` escrito en línea | ajustes de ruido, *hasta 26.3* | Un objetivo de aparición de 26.3 no puede contener ninguno de los dos, así que se descartan |
| `ore_veins_enabled`, y `vein_toggle`, `vein_ridged` y `vein_gap` en `noise_router` | ajustes de ruido, *hasta 26.3* | 26.3 no genera vetas de mineral mediante el noise router, así que se descartan |
| `replaceable` y `lava_level` | carvers, *hasta 26.3* | 26.3 deja ambos a los ajustes de ruido, así que se descartan los del propio carver |
| un desplazamiento de un ruido a lo largo de y, y una amplitud de octava negativa | funciones de densidad, ruidos, *hasta 26.3* | El ruido de 26.3 no puede desplazarse a lo largo de y, y usa en su lugar el tamaño de la amplitud |
| `override_armor_assets`, y un tinte `map_color` | materiales de ornamento, modelos de ítem, *hasta 26.3* | 26.3 lee las paletas de ornamento por equipo de los `trim_overrides` de cada asset de equipo, y eliminó `map_color`, así que ese tinte usa su color por defecto |

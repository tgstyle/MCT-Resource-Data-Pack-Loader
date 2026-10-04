Resource Data Pack Loader
=========================

Todo lo que pongas en esta carpeta sustituye lo que aporta un mod o el propio
Minecraft. Se aplica a todos los mundos, en un jugador y en servidores, y no
hay nada que activar.


MÁS QUE SUSTITUCIONES
---------------------

Los packs de aquí también pueden definir bloques, ítems, biomas y dimensiones
enteras a partir de archivos JSON, decidir qué se genera y dónde, bloquear
dimensiones tras una llave o un mob que hay que matar, y generar de antemano
la tierra de un mundo para que nadie espere nunca por un chunk. HOWTO.md, que
se entrega junto al mod, lo cubre todo.


CÓMO AÑADIR UN ARCHIVO
----------------------

Abre el jar del mod, busca el archivo que quieres cambiar y copia su ruta desde
'assets' o 'data' en adelante.

Para sustituir la textura del mineral de hierro, el archivo dentro del jar de
Minecraft es:

    assets/minecraft/textures/block/iron_ore.png

así que tu versión va aquí:

    rdploader/assets/minecraft/textures/block/iron_ore.png

Una tabla de botín vive en cambio bajo data, y se hace igual:

    data/minecraft/loot_tables/blocks/iron_ore.json
    rdploader/data/minecraft/loot_tables/blocks/iron_ore.json

Esa es toda la regla. La ruta después de 'assets' o 'data' es siempre la misma
que dentro del jar, así que nunca hace falta renombrar ni mover nada.


MANTENER EL ORDEN
-----------------

Puedes agrupar archivos en un pack con nombre, como un zip:

    rdploader/MyTextures.zip        (con 'assets' o 'data' en el nivel superior del zip)

Al comprimir, selecciona el contenido y comprime eso, no la carpeta que lo
contiene. Un zip cuyo nivel superior es una sola carpeta que envuelve 'assets'
o 'data' se omite, y el registro lo dice.

Una carpeta en rdploader no es un pack y se omite, y el registro lo dice. Deja
los archivos sueltos bajo assets o data, y comprime un pack en zip antes de
ponerlo aquí.

Si el mismo archivo existe en dos sitios, un pack con nombre gana a los
archivos sueltos, y /rdpl which te dice cuál ganó.


PRIORIDAD DE LOS PACKS
----------------------

Si dos packs con nombre contienen el mismo archivo, controla cuál gana
anteponiendo RDPL y un número al nombre del zip. RDPL0 se carga primero, los
números más altos se cargan después, y gana el pack que se carga el último:

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures.zip

Valen mayúsculas y minúsculas, y un espacio, guion o guion bajo después del
número es opcional. El prefijo se quita del nombre del pack en el registro y en
/rdpl list, así que RDPL1 SeasonalTextures aparece como SeasonalTextures.


API PARA MODS
-------------

Un mod puede llevar contenido de RDPL dentro de su propio jar, en una carpeta
llamada rdploader con la misma estructura que un pack:

    thatmod.jar
      META-INF/mods.toml
      rdploader/assets/thatmod/textures/block/ruby_ore.png

Son valores por defecto, no sustituciones. El pack de un mod se carga por debajo
de todos los packs de esta carpeta, así que lo que pongas aquí gana, y un mod
solo puede aportar archivos bajo un espacio de nombres que declare en su propio
mods.toml. Todo lo demás se ignora con un aviso, de modo que ningún mod
puede redefinir en silencio el contenido de otro ni el tuyo.

Todo mod que incluye uno aparece en config/mods.json la primera vez que se ve:

    {
      "thatmod": {
        "enabled": true,
        "priority": -1
      }
    }

Pon enabled en false para desactivar el contenido de ese mod. Deja priority en
-1 para mantenerlo por debajo de todo, o dale un número y ocupará su lugar en
el orden anterior, junto a los packs numerados. Los packs se listan de menor a
mayor en el registro y el propio de un mod aparece marcado, así que no se carga
nada que no puedas ver.


PACKS DE RECURSOS
-----------------

Por defecto, los archivos de aquí quedan por encima de los packs de recursos que
el jugador elige en la pantalla de opciones, así que un pack de recursos no
puede sustituirlos. Eso es lo correcto para cosas como el logo de un modpack, y
lo contrario para texturas que quieres que la gente pueda retexturizar.

Añade O o N después del prefijo RDPL para decidirlo por pack:

    rdploader/RDPLO Branding          siempre gana, los packs de recursos no lo tocan
    rdploader/RDPLN BaseTextures      un pack de recursos puede sustituirlo
    rdploader/RDPL1O Seasonal         prioridad y siempre gana, las dos cosas

Los packs sin letra siguen la opción overrideResourcePacks de la configuración,
y /rdpl list marca los que sustituyen.

La misma regla vale para los packs de datos. Un pack marcado con N queda por
debajo de los packs de datos que un mundo lleva en su propia carpeta datapacks,
y uno marcado con O queda por encima.

Los packs sin prefijo se cargan antes que todos los numerados, en orden
alfabético, así que un pack numerado siempre gana a uno sin número.

Para desactivar un pack sin borrarlo, añade .disabled al final de su nombre:

    rdploader/RDPL1 SeasonalTextures.zip.disabled

El pack se omite y el registro lo dice. Quita el sufijo para volver a activarlo.


LO QUE PUEDES CAMBIAR
---------------------

Texturas, modelos, estados de bloque, archivos de idioma, sonidos, fuentes,
textos de splash y cualquier otra cosa que un mod guarde en su carpeta assets,
como libros de guía o manuales.

Logros, tablas de botín, recetas, etiquetas, funciones, plantillas de
estructuras y cualquier otra cosa que un mod guarde en su carpeta data. Son del
lado del servidor, así que también funcionan en un servidor dedicado, y un
cambio en ellos surte efecto con /reload.

También se pueden quitar cosas. Un archivo en recipe_removals elimina recetas
por nombre, espacio de nombres o resultado, y un archivo en disabled saca
bloques e ítems del juego sin darlos de baja, de modo que los mundos conservan
sus ids y borrar el archivo lo devuelve todo. Una inyección de botín añade un
grupo a una tabla de botín que ya existe en lugar de sustituirla, y block_drops
añade o sustituye lo que suelta un bloque que no es tuyo. Las recetas de horno,
los tiempos de combustión, las pestañas creativas y los eventos de sonido
también son archivos propios.

A un yunque se le puede enseñar trabajo nuevo. Un archivo en anvils nombra un
ítem, el ítem que le acompaña en la ranura derecha y los encantamientos o el
resultado que el yunque ofrece por los niveles indicados. Sacarlo puede dar un
logro, y el ítem puede quedar inutilizable hasta ganar ese logro.

Un archivo hardness fija el tiempo de minado y la resistencia a explosiones de
un grupo de bloques, y un archivo exposures define un peligro como la
radiación: afecta a los jugadores cerca de bloques con nombre, que lleven
ítems con nombre o estén en dimensiones con nombre, en niveles que aplican cada
uno efectos y daño, y puede contagiarse de mobs y jugadores cercanos o caer con
la lluvia.


AÑADIR CONTENIDO NUEVO
----------------------

Un pack también puede añadir bloques, ítems y fluidos propios, descritos como
JSON. No necesitas escribir ni compilar un mod para esto.

Las definiciones están bajo data, una carpeta para cada tipo de cosa. Cada clave
dentro de "variants" es un nombre, así que un archivo en

    rdploader/data/mypack/blocks/ores.json

que contenga una variante llamada ruby_ore registra mypack:ruby_ore. El nombre
del archivo solo agrupa. Si un mod real ya registra ese nombre, gana el mod y
tu variante se omite.

El bloque más sencillo son unas pocas líneas:

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "ruby_ore": { "hardness": 3.0, "harvestLevel": 1 }
      }
    }

Sigues aportando el modelo, el estado de bloque, la textura y la entrada de
idioma bajo assets, igual que cualquier otro archivo de esta carpeta.

Cada una de estas es una carpeta bajo data/<yourpack>:

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

Los bloques vienen en estas formas, fijadas por el campo "type":

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

y los ítems en estas:

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   container

Un tipo de poción se nombra con la clave de idioma
item.minecraft.potion.effect.<baseName>, con splash_potion, lingering_potion o
tipped_arrow en lugar de potion para las demás formas. Un ítem potion_bottle es
tu propio contenedor para ellas: lleva un creativeTab como cualquier otro ítem y
guarda los tipos de poción que nombres en potionTypes.

Un archivo villagers/<name>.json define una profesión. Un archivo trades/*.json
añade tratos a cualquier profesión, sea tuya o de Minecraft, nombrando la
profesión y el nivel en el que aparece el trato.

Un archivo entities/<name>.json crea una entidad nueva a partir de una que ya
existe. Nombra la entidad en la que se basa y qué es distinto en ella: su
nombre, su aspecto, cuánta vida y daño tiene, cómo se mueve, cómo lucha y qué
suelta. Es una entidad propia, con su propio huevo generador y su propia tabla
de botín, y aquella en la que se basa queda intacta. También puede llevar un
almacenamiento propio, ranuras de ítems, un depósito de fluido y un búfer de
energía a los que llegan tuberías y cables y que un jugador abre agachándose y
haciendo clic derecho sobre ella.

Un archivo villages/<name>.json añade una parcela que una ciudad o aldea puede
construir a partir de una de tus plantillas .nbt, y un archivo raids/<name>.json
envía oleadas contra una aldea cuando un jugador lleva un presagio a ella.

Un archivo cards/<name>.json muestra una tarjeta en pantalla cuando ocurre algo,
como que un jugador entre en un bioma, y un archivo de la misma carpeta con el
nombre de uno de los mensajes propios del mod cambia lo que ese mensaje dice.

Un archivo worldintro/<name>.json reproduce una serie de páginas cuando alguien
entra en el mundo, antes de que tome el control. Las palabras son archivos .txt
normales bajo assets/<yourpack>/texts. Puede reproducirse una vez por jugador o
en cada entrada.

Un archivo teams/<name>.json pone un bando en el marcador del juego y dice qué
se une a él, y un archivo scoring/<name>.json es un objetivo que da puntos a
esos bandos y decide cómo termina una partida.


MUNDOS ENTEROS
--------------

Un pack no se limita a cosas sueltas. dimensions/<name>.json registra una
dimensión con su propio terreno, biomas y cielo. gates/<name>.json pone una
condición para llegar a una, como tener o gastar un ítem. Un bloque de tipo
portal envía a otra dimensión a quien lo cruza, y portalframes deja que un
jugador construya y encienda un marco propio.

worldtemplates/<name>.json reúne los ajustes de un mundo en un solo archivo,
para que un pack pueda entregar de una vez la forma de un mundo entero en lugar
de pedir una docena de cambios de configuración. También puede dar forma a la
propia superficie, como su nivel del mar y si sus océanos son de lava.

Una dimensión también puede fijar cómo se ve y se comporta sobre tu cabeza. Su
niebla, el tinte de la luz, el oscurecimiento del sol y de la luna, las capas de
nubes y el calor ondulante se dibujan en tu propia pantalla, y su clima decide
si llueve, nieva o hay tormentas, cuánto duran los chubascos y el color, las
partículas, el sonido y el ángulo de la lluvia.

worldgen es más que mineral. Una entrada coloca una forma, desde un pequeño
grumo de tu bloque hasta una de tus propias plantillas .nbt, y decide con qué
frecuencia, a qué altura y en qué biomas aparece.

Un archivo biomes/<name>.json define un bioma: su clima y sus colores, los
bloques de que está hecho, qué lo decora, qué aparece en él y dónde se genera.


DONDE ESTO SE ACABA
-------------------

Esto describe lo que es una cosa, no lo que hace con el tiempo. Todo lo que
necesite una entidad de bloque, una pantalla o código ejecutándose en cada tick
sigue necesitando un mod de verdad, con dos excepciones: un bloque de tipo
container guarda un inventario con una pantalla propia, y una variante de
entidad puede llevar almacenamiento. Una máquina está fuera de alcance; un
mineral, una valla, una comida o un fluido no.


PACKS DE OTRAS VERSIONES
------------------------

Un pack hecho para la línea 1.12.2 de este mod se carga aquí tal cual. Un zip se
convierte una sola vez, dentro de sí mismo, en una carpeta versions para esta
versión, y los archivos de 1.12.2 se quedan como estaban, así que el mismo zip
sigue funcionando en todas las versiones.


VER TUS CAMBIOS
---------------

Pulsa F3+T para recargar texturas, modelos, archivos de idioma y todo lo demás
bajo assets. En un servidor, o para cualquier cosa bajo data, escribe /reload.

Si añades un archivo nuevo o borras uno, usa /rdpl reload en su lugar. Editar un
archivo que ya estaba solo necesita F3+T o /reload.

/rdpl list muestra todos los packs cargados y lo que hay en ellos. Pasa el
ratón sobre un pack para verlo.

/rdpl which minecraft:textures/block/stone.png muestra qué pack sirve un archivo
y qué packs quedan tapados por debajo.

/rdpl config unused lista los archivos de opciones de rdploader/config que ya no
define ningún pack instalado, y /rdpl config prune los borra. /rdpl pixelmap
muestra en qué quedó un mapa de píxeles, y /rdpl biome list y here te informan
sobre los biomas.

Estos funcionan sin ser operador, porque solo leen archivos de tu propio
ordenador. En un servidor dedicado, /rdplserver reload vuelve a escanear la copia
del servidor, y /rdplserver list, which y unused responden por ella.


SI ALGO NO FUNCIONA
-------------------

Mira primero el registro. logs/rdpl.log lista cada pack que se cargó y cada uno
que se omitió, con el motivo, y todo lo que va mal se registra como aviso
diciendo por qué.

/rdpl unused lista cualquier archivo de tus packs que aún nadie ha pedido, lo
que normalmente significa una errata en la ruta. Ejecútalo cuando el juego haya
terminado de cargar, y ten en cuenta que algunos archivos solo se cargan cuando
hacen falta, como los idiomas distintos del que juegas.

Las mayúsculas importan. Si tu archivo es Stone.png y el juego pidió stone.png,
se carga igualmente, pero un aviso te dice que lo renombres. Hazlo, porque en
cualquier sitio que no sea este mod el archivo no se encontrará en absoluto. Los
archivos de idioma son lo que más confunde: son en_us.json, no en_US.json.

Comprueba que tus archivos estén dentro de una carpeta 'assets' o 'data'. Un zip
sin ninguna de las dos se omite, y el registro lo dice.


LOGROS Y RECETAS
----------------

Una receta que añade o sustituye un script se conoce por el nombre que le da el
script. Para que un logro la desbloquee, deja aquí un archivo de logro que
nombre esa receta, y el logro vuelve a funcionar de principio a fin.

Dale a esa receta un nombre fijo en el script. Un nombre inventado por el
sistema puede cambiar en cuanto edites la receta, así que no es seguro apuntar
un logro a él.


La propia carpeta rdploader se puede mover o renombrar con la opción
rootDirectory de config/resourcedatapackloader-common.toml. También vale una
ruta absoluta, y hace falta reiniciar.

Pon un pack.png junto a este archivo para dar un icono al pack.

Este archivo lo escribe el mod y se actualiza cada vez que cambia, así que todo
lo que escribas en él se sustituye la próxima vez que arranque el juego.

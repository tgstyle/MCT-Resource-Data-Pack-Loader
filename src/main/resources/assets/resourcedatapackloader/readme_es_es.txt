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
'assets' en adelante.

Para sustituir la textura del mineral de hierro, el archivo dentro del jar de
Minecraft es:

    assets/minecraft/textures/blocks/iron_ore.png

así que tu versión va aquí:

    rdploader/assets/minecraft/textures/blocks/iron_ore.png

Esa es toda la regla. La ruta después de 'assets' es siempre la misma que dentro
del jar, así que nunca hace falta renombrar ni mover nada.


MANTENER EL ORDEN
-----------------

Puedes agrupar archivos en un pack con nombre, como un zip:

    rdploader/MyTextures.zip        (con 'assets' en el nivel superior del zip)

Al comprimir, selecciona el contenido y comprime eso, no la carpeta que lo
contiene. Un zip cuyo nivel superior es una sola carpeta que envuelve 'assets'
se omite, y el registro lo dice.

Una carpeta en rdploader no es un pack y se omite, y el registro lo dice. Deja
los archivos sueltos bajo assets, y comprime un pack en zip antes de ponerlo
aquí.

Si el mismo archivo existe en dos sitios, un pack con nombre gana a los
archivos sueltos. El registro nombra el pack del que vino cada archivo, así que
siempre puedes ver cuál ganó.


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
      mcmod.info
      rdploader/assets/thatmod/blocks/ruby_ore.json

Son valores por defecto, no sustituciones. El pack de un mod se carga por debajo
de todos los packs de esta carpeta, así que lo que pongas aquí gana, y un mod
solo puede aportar archivos bajo un espacio de nombres que declare en su propio
mcmod.info. Todo lo demás se ignora con un aviso, de modo que ningún mod puede
redefinir en silencio el contenido de otro ni el tuyo.

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

Logros y tablas de botín. Son del lado del servidor, así que también funcionan
en un servidor dedicado.

Plantillas de estructuras, los archivos .nbt bajo assets/<modid>/structures. Una
estructura guardada en la propia carpeta structures del mundo sigue ganando a un
archivo de aquí, y una estructura que ya se ha colocado sigue cargada hasta que
sales del mundo.

Recetas, incluido sustituir la receta de un mod o añadir una propia. Las recetas
solo se cargan al arrancar el juego, así que un cambio aquí necesita un reinicio
y no una recarga.

Funciones, los archivos .mcfunction bajo assets/<modid>/functions. Minecraft
solo los lee de la carpeta data del propio mundo, así que ponerlos aquí hace que
funcionen en todos los mundos. Una función guardada en el mundo sigue ganando a
un archivo de aquí.

Cambios de nombre de registro, para que un mundo guardado antes de que un mod
renombrara uno de sus bloques conserve ese bloque en lugar de perderlo. Pon un
archivo en assets/<modid>/registry_remap:

    {
      "registry": "minecraft:items",
      "mapping": { "oldmod:old_name": "newmod:new_name" }
    }

El registro es aquel al que pertenece la entrada, normalmente minecraft:items o
minecraft:blocks. Los cambios se encadenan, así que asignar A a B y luego B a C
envía A a C.

Propiedades de cosas que ya existen, sin tocar sus archivos. Un archivo en
assets/<yourpack>/overrides nombra su objetivo por la ruta, así que
overrides/minecraft/stone.json cambia minecraft:stone, sea de vanilla o de un
mod. Los bloques admiten dureza, resistencia a explosiones, luz, opacidad a la
luz, resbalosidad, sonido, herramienta y nivel de cosecha, e inflamabilidad. Los
ítems admiten tamaño de pila, durabilidad y un ítem contenedor, y cualquier ítem
puede hacerse comestible, con valores de comida y efectos. Los efectos de una
poción se pueden reescribir. Todo esto es en vivo: desactiva el pack y ejecuta
/rdpl reload, y cada valor vuelve a lo que era, sin reiniciar. Pon el id del mod
propietario en "requires" para que el archivo se omita en silencio cuando ese
mod no esté instalado.

Botín de jugadores, algo para lo que el juego no tiene nombre. Los jugadores
sueltan su inventario y nada más, así que un archivo en
assets/<modid>/player_loot les da una tabla de botín propia:

    {
      "table": "mypack:entities/player",
      "mode": "add",
      "rollOnKeepInventory": false
    }

"add" suelta lo que la tabla tire además de todo lo que llevaban, "replace" lo
suelta en lugar de su inventario, y rollOnKeepInventory decide si la tabla se
tira siquiera en un mundo donde se conservan los inventarios.

Un estandarte es la excepción. Una definición registra dos bloques, el tuyo y un
segundo llamado <name>_wall, y solo el de pie recibe un ítem, que elige entre
ambos al colocarlo. Su modelo llega mucho más allá de su propio bloque, hasta 29,33
de 16 de pie y 13 por debajo del bloque en una pared, y el estado de bloque de
pie necesita el formato de Forge para girar en dieciseisavos. Las guías tienen
las medidas completas.

Los drops de un bloque pueden ser aleatorios y no tienen por qué ser ítems. Las
entradas de su lista drops se deciden cada una por su cuenta, salvo que les des
un peso, en cuyo caso comparten un solo bote y sale exactamente una de ellas.
Una entrada que nombra una entidad en lugar de un bloque suelta esa entidad
donde estaba el bloque.

Una textura puede escribirse como JSON en lugar de dibujarse. Pon al archivo el
nombre del PNG con .json al final, textures/blocks/panel.png.json, y dale un
tamaño como 16x16 o 16x32, una paleta de un carácter por color y filas de esos
caracteres de arriba abajo. Otro archivo así puede extenderlo y nombrar solo los
colores que quiere distintos, de modo que una misma forma se puede recolorear
tantas veces como quieras sin un solo archivo de imagen. Lo que dibujan se
guarda en pixelmap-cache aquí y se vuelve a dibujar cada vez que cambia un mapa
o su plantilla.

También se pueden quitar cosas. Un archivo en recipe_removals elimina recetas
por nombre, espacio de nombres o resultado, y un archivo en disabled saca
bloques e ítems del juego sin darlos de baja, de modo que los mundos conservan
sus ids y borrar el archivo lo devuelve todo. Una inyección de botín añade un
grupo a una tabla de botín que ya existe en lugar de sustituirla, y block_drops
añade o sustituye lo que suelta un bloque que no es tuyo. Las recetas de horno,
los tiempos de combustión, los nombres del diccionario de minerales, las
pestañas creativas y los eventos de sonido también son archivos propios.

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

CraftTweaker y GroovyScript siguen funcionando exactamente como antes. Se
ejecutan después de este mod, así que lo que tus scripts eliminen o cambien gana
a un archivo de aquí.

RDPL sirve para sustituir una o dos recetas, y las recetas de tu propio
contenido pertenecen al pack que lo acompaña. Para un control total de recetas
en un modpack, CraftTweaker y GroovyScript son mejores opciones. Un archivo de
aquí sustituye el original por completo, así que para cambiar un ingrediente o
quitar una entrada de botín, usa esos.


AÑADIR CONTENIDO NUEVO
----------------------

Un pack también puede añadir bloques, ítems y fluidos propios, descritos como
JSON. No necesitas escribir ni compilar un mod para esto.

La ruta del archivo es su nombre. Un bloque en

    rdploader/MyPack/assets/mypack/blocks/copper_ore.json

se registra como mypack:copper_ore. No hay campo de nombre que rellenar ni que
escribir mal. Si un mod real ya registra ese nombre, gana el mod y tu archivo se
omite.

El bloque más sencillo son unas pocas líneas:

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "copper_ore": { "meta": 0, "hardness": 3.0, "harvestLevel": 1 }
      }
    }

Sigues aportando el modelo, el estado de bloque, la textura y la entrada de
idioma igual que cualquier otro archivo de esta carpeta.

Cada una de estas es una carpeta bajo assets/<yourpack>:

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

Los bloques vienen en estas formas, fijadas por el campo "type":

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

y los ítems en estas:

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   rocket

Un tipo de poción siempre aparece en la poción de vanilla, la poción
arrojadiza, la poción persistente y la flecha con efecto, que viven en las
pestañas de Pociones y Combate. La pestaña es una propiedad del ítem, no del
tipo de poción, así que no hay forma de pasarlas a una pestaña propia. Un ítem
potion_bottle es en cambio tu propio contenedor: lleva un creativeTab como
cualquier otro ítem, lista los tipos de poción que nombres en potionTypes, y el
soporte para pociones lo acepta allí donde sirve una botella de cristal.

Un archivo villagers/<name>.json define una profesión y las carreras que ofrece.
Un archivo trades/*.json añade tratos a cualquier carrera, sea tuya o de
Minecraft, nombrando la profesión, la carrera y el nivel en el que aparece el
trato. Nombra una carrera que no existe y el registro lista las que sí.

Un archivo entities/<name>.json crea una entidad nueva a partir de una que ya
existe. Nombra la entidad en la que se basa y qué es distinto en ella: su
nombre, su piel, cuánta vida y daño tiene, a qué velocidad se mueve y cuánto
salta, de qué tamaño se dibuja, qué lleva puesto, qué caza y qué ignora, y si
sigue obedeciendo las reglas de aparición de la entidad de la que se hizo. Es
una entidad propia, con su propio huevo generador y su propia tabla de botín, y
aquella en la que se basa queda intacta. Una parcela de aldea puede recibir la
orden de alojar una en lugar de un aldeano. También puede llevar un
almacenamiento propio, ranuras de ítems, un depósito de fluido y un búfer de
energía a los que llegan tuberías y cables y que un jugador abre agachándose y
haciendo clic derecho sobre ella, y un cohete de Galacticraft puede variarse de
la misma forma y colocarse en una plataforma de lanzamiento con un ítem de tipo
rocket.

Un archivo villages/<name>.json añade una parcela que las aldeas pueden
construir, sea una granja que describes o una de tus plantillas .nbt. Los mismos
ajustes eligen qué piezas de vanilla siguen apareciendo, a qué distancia se
siembran las aldeas entre sí y en qué biomas se permiten.

Un archivo worldintro/<name>.json reproduce una serie de páginas cuando alguien
entra en el mundo, antes de que tome el control: texto que se desplaza sobre una
imagen, una tarjeta de título, una presentación, con música de fondo si quieres.
Las palabras son archivos .txt normales bajo assets/<yourpack>/texts. Puede
reproducirse una vez por jugador o en cada entrada.

Un archivo teams/<name>.json pone un bando en el marcador del juego al que se
unen, al aparecer, mobs, jugadores o cualquier cosa que surja en un rincón del
mundo, y un archivo scoring/<name>.json es un objetivo que puntúa muertes y
bajas a esos bandos, termina una ronda por puntuación o por tiempo, muestra la
clasificación como chat o como tarjeta, y puede reiniciar el mapa para la
siguiente ronda.

Un archivo raids/<name>.json envía oleadas contra una aldea cuando un jugador
con el efecto omen entra en ella. Una barra de jefe muestra cuántos saqueadores
quedan, los aldeanos corren a refugiarse dentro, los saqueadores derriban las
puertas de madera, y todas las campanas de la aldea suenan cuando llega una
oleada.

Un archivo cards/<name>.json muestra una tarjeta en pantalla cuando ocurre algo,
como que un jugador entre en un bioma, y un archivo de la misma carpeta con el
nombre de uno de los mensajes propios del mod cambia lo que ese mensaje dice.


MUNDOS ENTEROS
--------------

Un pack no se limita a cosas sueltas. dimensions/<name>.json registra una
dimensión con su propio terreno, biomas y cielo. gates/<name>.json pone una
condición para llegar a una, como tener o gastar un ítem. Un bloque de tipo
portal envía a quien entra y recuerda quién lo construyó.

Una plantilla de mundo también puede dar forma a la propia superficie,
fijando el nivel del mar, los océanos de lava y el ruido del terreno. Eso se
aplica al crear un mundo y nunca después, así que un mundo que ya existe se
queda como estaba.

worldtemplates/<name>.json reúne los ajustes de un mundo en un solo archivo,
para que un pack pueda entregar de una vez la forma de un mundo entero en lugar
de pedir una docena de cambios de configuración. Cada grupo que puede fijar
responde también a la categoría control de la configuración, que decide si
manda el pack, manda la configuración, o el grupo está apagado del todo y
ningún pack puede encenderlo.

Una dimensión también puede fijar cómo se ve y se comporta sobre tu cabeza. Su
niebla, el tinte de la luz, el oscurecimiento del sol y de la luna, las capas de
nubes y el calor ondulante se dibujan en tu propia pantalla, y su clima decide
si llueve, nieva o hay tormentas, cuánto duran los chubascos y el color, las
partículas, el sonido y el ángulo de la lluvia.

Con Galacticraft instalado, celestial/<name>.json coloca los sistemas
estelares, planetas, lunas, cinturones de asteroides y estaciones espaciales de
un pack en el mapa estelar de Galacticraft, y una dimensión con un bloque
galacticraft pasa a ser un lugar al que vuela un cohete.

worldgen es más que mineral. Una entrada es una forma colocada por una
distribución: grumos, vetas largas, placas, geodas, cuencos, agujas, nódulos,
respiraderos, decoración de superficie, árboles enteros, lianas, cinturones que
abarcan varios chunks o una de tus propias plantillas .nbt, repartidas
uniformemente, en torno a una altura, de forma fractal, siguiendo el terreno, en
suelos o techos de cuevas, o bajo el agua.


Un archivo biomes/<name>.json define un bioma: su clima y sus colores, los
bloques de que está hecho, qué lo decora, qué aparece en él y dónde se genera.
Su número se elige por ti y se escribe en cada mundo la primera vez que ese
mundo se carga, así que se queda fijo después, sin importar qué más instales.
Pon "id" solo cuando un bioma tenga que conservar un número que algo más ya
usaba, como cuando un pack sustituye un mod que se retira. Renombrar o borrar un
bioma que un mundo ya contiene lo pierde, igual que renumerar un bloque, así que
usa registry_remap para un cambio de nombre.

El nombre que muestra un aldeano es la clave de idioma
entity.Villager.<career>, con el nombre de la carrera exactamente como lo
escribiste y nada más. Ese espacio de claves lo comparten Minecraft y todos los
demás packs, así que pon tu espacio de nombres en el nombre de la carrera, como
en rdpltest.prospector. Solo se afecta el nombre: un aldeano guarda su carrera
como un número, así que renombrar una cambia cómo se llama a los aldeanos
existentes, y reordenar la lista de carreras cambia qué carrera tienen.

Un tipo de poción se nombra de la misma forma por su archivo, y su nombre
mostrado viene de la clave de idioma potion.effect.<namespace>.<name>, con
splash_potion.effect., lingering_potion.effect. y tipped_arrow.effect. para las
otras tres formas.


MUNDOS RUBIC
------------

Una plantilla de mundo puede pedir un mundo construido con cubos en lugar de
columnas de 256 bloques, y entonces el mundo crece en ambos sentidos: un suelo
muy por debajo de cero, un techo muy por encima de 255, con terreno, cuevas y
minerales en todo él. Jugar en uno es lo normal. Cavas, construyes, iluminas y
viajas igual, y la ventana de generación de vanilla conserva su forma habitual
dentro del mundo más alto, así que los mods que generan terreno lo colocan donde
siempre.

Lo que obtiene un pack: una altura de mundo propia, fijada una vez al crear el
mundo; un mundo profundo bajo la ventana de vanilla, con cuevas de ruido,
acuíferos y vetas de mineral en bandas en una piedra que nombras; regiones de
cueva, la respuesta de los packs a los biomas de cueva, pintadas por el
subsuelo en tres dimensiones con sus propios suelos, techos, nivel del agua,
mobs y estructuras; dimensiones apiladas unas sobre otras, de modo que caer por
el fondo de un mundo te lleva al siguiente más abajo y salir por arriba te
devuelve; y cualquier dimensión omitida, que conserva su mundo corriente en el
mismo guardado.

Por debajo, un mundo se almacena como cubos de 16 por 16 por 16 en sus propios
archivos de región junto a los de vanilla, que se generan, cargan y guardan por
su cuenta, con un motor de luz escrito para esa forma. La suposición de vanilla
de que un mundo tiene 256 bloques de alto está corregida allí donde carga peso,
desde los límites de construcción y los planos de muerte hasta el pathfinding,
los portales, las balizas, los mapas y el renderizador. Los generadores de otros
mods siguen viendo una ventana de aspecto normal de 256 bloques, y por eso su
terreno funciona.

HOWTO.md tiene los ajustes, las alturas que puede tomar un mundo y los mods
junto a los que esto no funcionará.


UNA ADVERTENCIA SOBRE META
--------------------------

Cada variante tiene un número meta, y ese número es lo que guarda el archivo del
mundo. Renumerar una variante que la gente ya tiene en un mundo convierte sus
bloques en otra cosa. Añade variantes nuevas al final y nunca renumeres una
antigua.

Un bloque admite 16 variantes, porque eso es lo que permiten cuatro bits de
metadatos. Las losas tienen 8, ya que un bit dice arriba o abajo, y las
escaleras, escaleras de mano, antorchas y cultivos tienen 1, porque la
orientación o la edad usan el resto. Los ítems no están tan ajustados y pueden
saltarse números.


DONDE ESTO SE ACABA
-------------------

Esto describe lo que es una cosa, no lo que hace con el tiempo. Todo lo que
necesite una tile entity, una GUI o código ejecutándose en cada tick sigue
necesitando un mod de verdad, con dos excepciones: un bloque de tipo container
guarda un inventario con una pantalla propia, y una variante de entidad puede
llevar almacenamiento. Una máquina está fuera de alcance; un mineral, una
valla, una comida o un fluido no.


PACKS DE OTRAS VERSIONES
------------------------

Un pack hecho para la línea 1.20.1, 1.21.1 o 26.x de este mod se carga aquí
también. Un zip se convierte una sola vez, dentro de sí mismo, en una carpeta
versions/1.12.2, y los archivos modernos se quedan como estaban, así que el
mismo zip sigue funcionando en todas las versiones.


VER TUS CAMBIOS
---------------

Pulsa F3+T para recargar texturas, modelos, archivos de idioma, logros y tablas
de botín. En un servidor, escribe /reload para lo mismo. Las recetas son la
excepción, como arriba: solo se cargan al arrancar, así que un cambio de recetas
necesita un reinicio.

Si añades un archivo nuevo o borras uno, usa /rdpl reload en su lugar. Editar un
archivo que ya estaba solo necesita F3+T.

/rdpl reload textures recarga solo las texturas, que es mucho más rápido que
F3+T en un pack grande. models, languages, sounds y shaders funcionan igual.
Deja el nombre fuera para volver a escanear la carpeta y recargarlo todo.

/rdpl list muestra todos los packs cargados y lo que hay en ellos. Haz clic en
un pack para verlo.

/rdpl which minecraft:textures/blocks/stone.png muestra qué pack sirve un
archivo y qué packs quedan tapados por debajo.

/rdpl config unused lista los archivos de opciones de rdploader/config que ya no
define ningún pack instalado, y /rdpl config prune los borra. /rdpl pixelmap
muestra en qué quedó un mapa de píxeles, /rdpl biome list y here te informan
sobre los biomas, y /rdpl team y /rdpl round sirven para los bandos y las rondas
que define un pack.

Estos funcionan sin ser operador, porque solo leen archivos de tu propio
ordenador. En un servidor dedicado, /rdplserver reload vuelve a escanear la copia
del servidor.


SI ALGO NO FUNCIONA
-------------------

Mira primero el registro. Los logros, las tablas de botín, las recetas, las
funciones y las estructuras se registran con el pack del que vienen, y todo lo
que va mal se registra como aviso diciendo por qué.

Para texturas y otros recursos, /rdpl unused lista cualquier archivo de tus
packs que aún nadie ha pedido, lo que normalmente significa una errata en la
ruta. Ejecútalo cuando el juego haya terminado de cargar, y ten en cuenta que
algunos archivos solo se cargan cuando hacen falta, como los idiomas distintos
del que juegas.

Las mayúsculas importan. Si tu archivo es Stone.png y el juego pidió stone.png,
se carga igualmente, pero un aviso te dice que lo renombres. Hazlo, porque en
cualquier sitio que no sea este mod el archivo no se encontrará en absoluto. Los
archivos de idioma son lo que más confunde: son en_us.lang, no en_US.lang.

Comprueba que tus archivos estén dentro de una carpeta 'assets'. Un zip sin ella
se omite, y el registro lo dice.



LOGROS Y RECETAS
----------------

Si tus scripts eliminan una receta, cualquier logro que la desbloqueaba sigue
funcionando en lugar de romperse. Solo que ya no tiene receta que darte, y el
registro la nombra una vez.

Si sustituiste esa receta por una nueva y quieres que el logro desbloquee la
nueva, dale un nombre a la receta nueva en tu script:

    recipes.addShaped("rail", <minecraft:rail> * 16, [[...]]);

Eso la registra como crafttweaker:rail. Luego deja aquí un archivo de logro que
apunte a ese nombre, y el logro vuelve a funcionar de principio a fin.

Sin nombre, se llamará algo como crafttweaker:ct_shaped-1834729103, un hash de
la propia receta. Eso cambia en cuanto editas la receta, y puede moverse si se
añade otra receta antes, así que no es seguro apuntar un logro a él.


La propia carpeta rdploader se puede mover o renombrar con la opción
rootDirectory de config/mct_resourcedatapackloader_mixin.cfg. También vale una
ruta absoluta, y hace falta reiniciar.

Pon un pack.png junto a este archivo para dar un icono al pack.

Este archivo lo escribe el mod y se actualiza cada vez que cambia, así que todo
lo que escribas en él se sustituye la próxima vez que arranque el juego.

Resource Data Pack Loader
=========================

Tudo o que colocares nesta pasta substitui o que um mod ou o próprio Minecraft
fornece. Aplica-se a todos os mundos, em modo um jogador e em servidores, e não
há nada para ativar.


MAIS DO QUE SUBSTITUIÇÕES
-------------------------

Os pacotes aqui colocados também podem definir novos blocos, itens, biomas e
dimensões inteiras a partir de ficheiros JSON, decidir o que é gerado e onde,
trancar dimensões atrás de uma chave ou de um mob que tem de ser morto, e gerar
com antecedência a terra de um mundo para que ninguém espere por um chunk. O
HOWTO.md, que acompanha o mod, cobre tudo isto.


COMO ADICIONAR UM FICHEIRO
--------------------------

Abre o jar do mod, encontra o ficheiro que queres alterar e copia o caminho dele
a partir de 'assets'.

Para substituir a textura do minério de ferro, o ficheiro dentro do jar do
Minecraft é:

    assets/minecraft/textures/blocks/iron_ore.png

por isso a tua versão fica aqui:

    rdploader/assets/minecraft/textures/blocks/iron_ore.png

Essa é a regra toda. O caminho depois de 'assets' é sempre igual ao caminho
dentro do jar, por isso nunca é preciso mudar nomes nem mover nada.


MANTER TUDO ORGANIZADO
----------------------

Podes agrupar ficheiros num pacote com nome, como um zip:

    rdploader/MyTextures.zip        (com 'assets' no nível superior do zip)

Ao comprimir, seleciona o conteúdo e comprime-o, não a pasta que o contém. Um
zip cujo nível superior é uma única pasta a envolver 'assets' é ignorado, e o
registo avisa.

Uma pasta em rdploader não é um pacote e é ignorada, e o registo avisa. Deixa os
ficheiros soltos em assets, e comprime um pacote em zip antes de o colocares
aqui.

Se o mesmo ficheiro existir em dois sítios, um pacote com nome ganha aos
ficheiros soltos. O registo indica o pacote de onde veio cada ficheiro, por isso
podes sempre ver qual ganhou.


PRIORIDADE DOS PACOTES
----------------------

Se dois pacotes com nome tiverem o mesmo ficheiro, controla qual ganha
colocando RDPL e um número antes do nome do zip. O RDPL0 carrega primeiro, os
números maiores carregam depois, e o pacote carregado em último ganha:

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures.zip

Maiúsculas ou minúsculas funcionam, e um espaço, hífen ou sublinhado depois do
número é opcional. O prefixo é retirado do nome do pacote no registo e em
/rdpl list, por isso RDPL1 SeasonalTextures aparece como SeasonalTextures.


API DE MODS
-----------

Um mod pode trazer conteúdo do RDPL dentro do próprio jar, numa pasta chamada
rdploader organizada exatamente como um pacote:

    thatmod.jar
      mcmod.info
      rdploader/assets/thatmod/blocks/ruby_ore.json

Estes são valores predefinidos, não substituições. O pacote de um mod carrega
abaixo de todos os pacotes desta pasta, por isso o que colocares aqui ganha, e
um mod só pode fornecer ficheiros num namespace que declare no próprio
mcmod.info. Tudo o resto é ignorado com um aviso, para que nenhum mod redefina
em silêncio o conteúdo de outro ou o teu.

Todos os mods que trazem um destes ficam registados em config/mods.json na
primeira vez que são vistos:

    {
      "thatmod": {
        "enabled": true,
        "priority": -1
      }
    }

Define enabled como false para desligar o conteúdo desse mod. Deixa priority em
-1 para o manter por baixo de tudo, ou indica um número e ele ocupa o seu lugar
na ordem acima, junto dos pacotes numerados. Os pacotes aparecem no registo do
menor para o maior e o de um mod vem assinalado, por isso nada carrega sem que
o vejas.


PACOTES DE RECURSOS
-------------------

Por predefinição, os ficheiros colocados aqui ficam acima dos pacotes de
recursos que o jogador escolhe no ecrã de opções, por isso um pacote de recursos
não os consegue substituir. Isso é correto para coisas como o logótipo de um
modpack e errado para texturas que gostarias que as pessoas pudessem
retexturizar.

Adiciona O ou N depois do prefixo RDPL para decidir pacote a pacote:

    rdploader/RDPLO Branding          ganha sempre, os de recursos não o tocam
    rdploader/RDPLN BaseTextures      um pacote de recursos pode substituí-lo
    rdploader/RDPL1O Seasonal         prioridade e ganha sempre, as duas coisas

Os pacotes sem letra seguem a opção overrideResourcePacks da configuração, e
/rdpl list assinala os que substituem.

Os pacotes sem prefixo carregam antes de todos os numerados, por ordem
alfabética, por isso um pacote numerado ganha sempre a um sem número.

Para desligar um pacote sem o apagar, adiciona .disabled ao fim do nome:

    rdploader/RDPL1 SeasonalTextures.zip.disabled

O pacote é ignorado e o registo avisa. Remove o sufixo para o voltar a ligar.


O QUE PODES SUBSTITUIR
----------------------

Texturas, modelos, estados de bloco, ficheiros de idioma, sons, tipos de letra,
textos de splash e qualquer outra coisa que um mod guarde na pasta assets, como
livros-guia ou manuais.

Progressos e tabelas de saque. Isto é do lado do servidor, por isso funciona
também num servidor dedicado.

Modelos de estruturas, os ficheiros .nbt em assets/<modid>/structures. Uma
estrutura guardada na própria pasta structures do mundo continua a ganhar a um
ficheiro daqui, e uma estrutura que já foi colocada continua carregada até
saíres do mundo.

Receitas, incluindo substituir a receita de um mod ou adicionar uma tua. As
receitas só carregam quando o jogo arranca, por isso uma alteração aqui precisa
de um reinício, não de uma recarga.

Funções, os ficheiros .mcfunction em assets/<modid>/functions. O Minecraft só
os lê da pasta data do próprio mundo, por isso, ao colocá-los aqui, funcionam em
todos os mundos. Uma função guardada no mundo continua a ganhar a um ficheiro
daqui.

Renomeações de registo, para que um mundo guardado antes de um mod renomear um
dos seus blocos mantenha esse bloco em vez de o perder. Coloca um ficheiro em
assets/<modid>/registry_remap:

    {
      "registry": "minecraft:items",
      "mapping": { "oldmod:old_name": "newmod:new_name" }
    }

O registo é aquele a que a entrada pertence, normalmente minecraft:items ou
minecraft:blocks. As renomeações encadeiam-se, por isso mapear A para B e depois
B para C leva A a C.

Propriedades de coisas que já existem, sem mexer nos ficheiros delas. Um
ficheiro em assets/<yourpack>/overrides indica o alvo pelo caminho, por isso
overrides/minecraft/stone.json altera minecraft:stone, seja vanilla ou de um
mod. Os blocos aceitam dureza, resistência a explosões, luz, opacidade à luz,
escorregadia, som, ferramenta e nível de coleta, e inflamabilidade. Os itens
aceitam tamanho da pilha, durabilidade e um item de recipiente, e qualquer item
pode tornar-se comestível, com valores de comida e efeitos. Os efeitos de uma
poção podem ser reescritos. Tudo isto é em direto: desativa o pacote e executa
/rdpl reload, e cada valor volta ao que era, sem reiniciar. Coloca o id do mod
dono em "requires" para que o ficheiro seja ignorado em silêncio quando esse
mod não estiver instalado.

Saque de jogadores, algo para o qual o jogo não tem nome. Os jogadores largam o
inventário e mais nada, por isso um ficheiro em assets/<modid>/player_loot
dá-lhes uma tabela de saque própria:

    {
      "table": "mypack:entities/player",
      "mode": "add",
      "rollOnKeepInventory": false
    }

"add" larga o que a tabela sortear juntamente com tudo o que eles traziam,
"replace" larga isso no lugar do inventário, e rollOnKeepInventory decide se a
tabela chega a ser sorteada num mundo em que os inventários são mantidos.

Um estandarte é a exceção. Uma definição regista dois blocos, o teu e um
segundo chamado <name>_wall, e só o de pé recebe um item, que escolhe entre eles
ao ser colocado. O modelo vai muito além do próprio bloco, até 29,33 de 16 de pé
e 13 abaixo do bloco na parede, e o estado de bloco de pé precisa do formato do
Forge para rodar em dezasseis avos. Os guias têm as medidas completas.

Os drops de um bloco podem ser aleatórios e não precisam de ser itens. As
entradas da lista drops são decididas cada uma por si, a menos que lhes dês um
peso, e então partilham um único pote e exatamente uma delas sai. Uma entrada
que indique uma entidade em vez de um bloco solta essa entidade onde o bloco
estava.

Uma textura pode ser escrita em JSON em vez de desenhada. Dá ao ficheiro o nome
do PNG com .json no fim, textures/blocks/panel.png.json, e indica-lhe um
tamanho como 16x16 ou 16x32, uma paleta de um caráter para uma cor e linhas
desses carateres de cima para baixo. Outro ficheiro deste tipo pode estendê-lo e
indicar só as cores que quer diferentes, de modo que uma forma pode ser
recolorida as vezes que quiseres sem um único ficheiro de imagem. O que eles
desenham fica em pixelmap-cache aqui e é redesenhado sempre que um mapa ou o seu
modelo muda.

Também é possível retirar coisas. Um ficheiro em recipe_removals apaga receitas
por nome, namespace ou resultado, e um ficheiro em disabled retira blocos e
itens do jogo sem os desregistar, por isso os mundos mantêm os seus ids e apagar
o ficheiro traz tudo de volta. Uma injeção de saque adiciona um pool a uma
tabela de saque que já existe em vez de a substituir, e block_drops adiciona ou
substitui o que um bloco que não é teu larga. Receitas de fornalha, tempos de
queima de combustível, nomes do dicionário de minérios, separadores criativos e
eventos de som também são ficheiros próprios.

Uma bigorna pode aprender trabalho novo. Um ficheiro em anvils indica um item,
o item que o acompanha na ranhura da direita e os encantamentos ou o resultado
que a bigorna oferece pelos níveis indicados. Retirá-lo pode render um
progresso, e o item pode ficar inutilizável até esse progresso ser obtido.

Um ficheiro em tooltips acrescenta linhas cinzentas de dica a itens e blocos e
uma descrição sob o nome de um encantamento.

Um ficheiro hardness define o tempo de mineração e a resistência a explosões de
um grupo de blocos, e um ficheiro exposures define um perigo como a radiação:
afeta jogadores perto de blocos indicados, que transportem itens indicados ou
estejam em dimensões indicadas, em níveis que aplicam cada um efeitos e dano, e
pode ser apanhado de mobs e jogadores por perto ou cair com a chuva.

O CraftTweaker e o GroovyScript continuam a funcionar exatamente como antes.
Correm depois deste mod, por isso o que os teus scripts removerem ou alterarem
ganha a um ficheiro daqui.

O RDPL é bom para substituir uma ou duas receitas, e as receitas do teu próprio
conteúdo pertencem ao pacote que o acompanha. Para controlo total das receitas
num modpack, o CraftTweaker e o GroovyScript são melhores opções. Um ficheiro
daqui substitui o original por completo, por isso, para alterar um ingrediente
ou retirar uma entrada de saque, usa esses.


ADICIONAR CONTEÚDO NOVO
-----------------------

Um pacote também pode adicionar blocos, itens e fluidos próprios, descritos em
JSON. Não precisas de escrever nem compilar um mod para isso.

O caminho do ficheiro é o nome dele. Um bloco em

    rdploader/MyPack/assets/mypack/blocks/copper_ore.json

regista-se como mypack:copper_ore. Não há campo de nome para preencher nem para
errar. Se um mod a sério já registar esse nome, o mod ganha e o teu ficheiro é
ignorado.

O bloco mais simples são poucas linhas:

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "copper_ore": { "meta": 0, "hardness": 3.0, "harvestLevel": 1 }
      }
    }

Continuas a fornecer o modelo, o estado de bloco, a textura e a entrada de
idioma da mesma forma que qualquer outro ficheiro desta pasta.

Cada um destes é uma pasta em assets/<yourpack>:

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
    celestial        raids            tooltips
    celestial        raids            harvests

Os blocos existem nestas formas, definidas pelo campo "type":

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

e os itens nestas:

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   rocket

Um tipo de poção aparece sempre na poção vanilla, na poção de arremesso, na
poção persistente e na flecha com efeito, que ficam nos separadores de Poções e
Combate. O separador é uma propriedade do item, não do tipo de poção, por isso
não há maneira de as mover para um separador teu. Um item potion_bottle é, em
vez disso, o teu próprio recipiente: recebe um creativeTab como qualquer outro
item, lista os tipos de poção que indicares em potionTypes, e o posto de poções
aceita-o onde quer que uma garrafa de vidro sirva.

Um ficheiro villagers/<name>.json define uma profissão e as carreiras que ela
oferece. Um ficheiro trades/*.json adiciona trocas a qualquer carreira, tua ou
do Minecraft, indicando a profissão, a carreira e o nível em que a troca
aparece. Indica uma carreira que não existe e o registo lista as que existem.

Um ficheiro entities/<name>.json cria uma entidade nova a partir de uma que já
existe. Indica a entidade em que se baseia e o que muda nela: o nome, a skin,
quanta vida e dano tem, a velocidade a que se move e a altura a que salta, o
tamanho com que é desenhada, o que veste, o que caça e o que ignora, e se ainda
obedece às regras de spawn da entidade de que foi feita. É uma entidade própria,
com o seu próprio ovo de spawn e a sua própria tabela de saque, e aquela em que
foi baseada fica intacta. Um terreno de aldeia pode ser instruído a abrigar uma
no lugar de um aldeão. Também pode levar um armazenamento próprio, ranhuras de
itens, um tanque de fluido e um buffer de energia que tubos e cabos alcançam e
que o jogador abre ao agachar-se e clicar com o botão direito nela, e um
foguetão do Galacticraft pode variar do mesmo modo e ser colocado numa
plataforma de lançamento por um item do tipo rocket.

Um ficheiro villages/<name>.json adiciona um terreno que as aldeias podem
construir, seja uma quinta que descreves ou um dos teus modelos .nbt. As mesmas
definições escolhem quais as peças vanilla que ainda aparecem, a que
distância as aldeias são semeadas umas das outras e em que biomas são
permitidas.

Um ficheiro worldintro/<name>.json apresenta uma série de páginas quando alguém
entra no mundo, antes de assumir o controlo: texto a rolar sobre uma imagem, um
cartão de título, uma apresentação de diapositivos, com música de fundo se
quiseres. As palavras são ficheiros .txt simples em assets/<yourpack>/texts.
Pode ser apresentado uma vez por jogador ou a cada entrada.

Um ficheiro teams/<name>.json coloca um lado na tabela de pontuação do jogo
em que mobs, jogadores ou qualquer coisa que surja num canto do mundo entram
ao chegar, e um ficheiro scoring/<name>.json é um objetivo que pontua abates e
mortes a esses lados, termina uma ronda por pontuação ou por tempo, mostra a
classificação no chat ou num cartão, e pode repor o mapa para a ronda seguinte.

Um ficheiro raids/<name>.json envia vagas contra uma aldeia quando um jogador
com o efeito omen entra nela. Uma barra de chefe mostra quantos saqueadores
restam, os aldeões correm para dentro, os saqueadores deitam abaixo portas de
madeira, e todos os sinos da aldeia tocam quando chega uma vaga.

Um ficheiro cards/<name>.json mostra um cartão no ecrã quando algo acontece,
como um jogador entrar num bioma, e um ficheiro na mesma pasta com o nome de uma
das mensagens do próprio mod altera o que essa mensagem diz.

MUNDOS INTEIROS
---------------

Um pacote não se limita a coisas avulsas. dimensions/<name>.json regista uma
dimensão com terreno, biomas e céu próprios. gates/<name>.json impõe uma
condição para chegar a uma, como segurar ou gastar um item. Um bloco do tipo
portal leva quem entra nele e lembra-se de quem o construiu.

Um modelo de mundo também pode moldar a própria superfície, definindo o nível do
mar, oceanos de lava e o ruído do terreno. Isto é aplicado quando um mundo é
criado e nunca depois, por isso um mundo que já existe fica como estava.

worldtemplates/<name>.json reúne as definições de um mundo num só ficheiro,
para que um pacote entregue de uma vez a forma de um mundo inteiro em vez de
pedir uma dúzia de alterações de configuração. Todos os grupos que ele pode
definir respondem também à categoria control da configuração, que decide se o
pacote manda, se a configuração manda, ou se o grupo está totalmente desligado
e nenhum pacote o consegue ligar.

Uma dimensão também pode definir o aspeto e o comportamento por cima da tua
cabeça. O nevoeiro, a tonalidade da luz, o escurecimento do sol e da lua, as
camadas de nuvens e a ondulação de calor são desenhados no teu próprio ecrã, e o
clima decide se chove, neva ou há tempestades, quanto duram os aguaceiros e a
cor, as partículas, o som e o ângulo da chuva.

Com o Galacticraft instalado, celestial/<name>.json coloca os sistemas
estelares, planetas, luas, cinturões de asteroides e estações espaciais de um
pacote no mapa estelar do Galacticraft, e uma dimensão com um bloco galacticraft
passa a ser um lugar para onde um foguetão voa.

worldgen é mais do que minério. Uma entrada é uma forma colocada por uma
distribuição: aglomerados, filões longos, placas, geodos, bacias, agulhas,
nódulos, respiradouros, decoração de superfície, árvores inteiras, trepadeiras,
cinturões que abrangem vários chunks ou um dos teus próprios modelos .nbt,
espalhados de forma uniforme, em torno de uma altura, de forma fractal, a
acompanhar o terreno, em chãos ou tetos de cavernas, ou debaixo de água.


Um ficheiro biomes/<name>.json define um bioma: o clima e as cores, os blocos de
que é feito, o que o decora, o que nasce nele e onde é gerado. O número dele é
escolhido por ti e gravado em cada mundo na primeira vez que esse mundo
carrega, por isso fica fixo depois, não importa o que mais instalares. Define
"id" só quando um bioma precisar de manter um número que outra coisa já usava,
como quando um pacote substitui um mod que está a ser retirado. Renomear ou
apagar um bioma que um mundo já contém perde-o, tal como renumerar um bloco,
por isso usa registry_remap para uma renomeação.

O nome apresentado de um aldeão é a chave de idioma entity.Villager.<career>,
com o nome da carreira exatamente como o escreveste e mais nada. Esse espaço de
chaves é partilhado com o Minecraft e com todos os outros pacotes, por isso
coloca o teu namespace no nome da carreira, como em rdpltest.prospector. Só o
nome é afetado: um aldeão guarda a carreira como um número, por isso renomear
uma muda o nome dos aldeões existentes, e reordenar a lista de carreiras muda
a carreira que eles têm.

Um tipo de poção é nomeado pelo ficheiro do mesmo modo, e o nome apresentado
vem da chave de idioma potion.effect.<namespace>.<name>, com
splash_potion.effect., lingering_potion.effect. e tipped_arrow.effect. para as
outras três formas.


MUNDOS RUBIC
------------

Um modelo de mundo pode pedir um mundo construído com cubos em vez de colunas de
256 blocos, e então o mundo cresce nos dois sentidos: um piso bem abaixo de
zero, um teto bem acima de 255, com terreno, cavernas e minérios em tudo. Jogar
num deles é normal. Escavas, constróis, iluminas e viajas do mesmo modo, e a
janela de geração do vanilla mantém o seu formato habitual dentro do mundo mais
alto, por isso os mods que geram terreno colocam-no onde sempre o colocaram.

O que um pacote ganha: uma altura de mundo própria, definida uma vez quando o
mundo é criado; um mundo profundo sob a janela do vanilla, com cavernas de
ruído, aquíferos e filões de minério em faixas numa pedra que indicas; regiões
de caverna, a resposta dos pacotes aos biomas de caverna, pintadas pelo subsolo
em três dimensões com os seus próprios pisos, tetos, nível da água, mobs e
estruturas; dimensões empilhadas umas sobre as outras, de modo que cair pelo
fundo de um mundo leva ao seguinte mais abaixo e sair pelo topo traz de volta;
e qualquer dimensão deixada de fora, mantendo o seu mundo comum na mesma
gravação.

Por baixo, um mundo é guardado como cubos de 16 por 16 por 16 em ficheiros de
região próprios ao lado dos do vanilla, gerados, carregados e guardados por
conta própria, com um motor de luz escrito para esse formato. A suposição do
vanilla de que um mundo tem 256 blocos de altura está corrigida onde quer que
pese, dos limites de construção e planos de morte à pesquisa de caminhos,
portais, sinalizadores, mapas e ao renderizador. Os geradores de outros mods
continuam a ver uma janela de aspeto normal de 256 blocos, e é por isso que o
terreno deles funciona.

O HOWTO.md tem as definições, as alturas que um mundo pode ter e os mods ao
lado dos quais isto não vai funcionar.


UM AVISO SOBRE META
-------------------

Todas as variantes têm um número meta, e esse número é o que o ficheiro do mundo
guarda. Renumerar uma variante que as pessoas já têm num mundo transforma os
blocos delas noutra coisa. Adiciona variantes novas no fim e nunca renumeres
uma antiga.

Um bloco comporta 16 variantes, porque é o que quatro bits de metadados
permitem. As lajes têm 8, já que um bit diz em cima ou em baixo, e as escadas,
as escadas de mão, as tochas e as plantações têm 1, porque a orientação ou a
idade usam o resto. Os itens não são tão apertados e podem saltar números.


ONDE ISTO TERMINA
-----------------

Isto descreve o que uma coisa é, não o que ela faz ao longo do tempo. Tudo o
que precise de uma tile entity, de uma GUI ou de código a correr a cada tick
continua a precisar de um mod a sério, com duas exceções: um bloco do tipo
container guarda um inventário com ecrã próprio, e uma variante de entidade pode
levar armazenamento. Uma máquina está fora do alcance; um minério, uma cerca,
uma comida ou um fluido não.


PACOTES DE OUTRAS VERSÕES
-------------------------

Um pacote feito para a linha 1.20.1, 1.21.1 ou 26.x deste mod também carrega
aqui. Um zip é convertido uma única vez, dentro de si mesmo, numa pasta
versions/1.12.2, e os ficheiros modernos ficam como estavam, por isso o mesmo
zip continua a funcionar em todas as versões.


VER AS TUAS ALTERAÇÕES
----------------------

Prime F3+T para recarregar texturas, modelos, ficheiros de idioma, progressos e
tabelas de saque. Num servidor, escreve /reload para o mesmo efeito. As receitas
são a exceção, como acima: só carregam no arranque, por isso uma alteração de
receita precisa de um reinício.

Se adicionares um ficheiro novo ou apagares um, usa /rdpl reload. Editar um
ficheiro que já existia só precisa de F3+T.

/rdpl reload textures recarrega só as texturas, o que é muito mais rápido do que
F3+T num pacote grande. models, languages, sounds e shaders funcionam do mesmo
modo. Deixa o nome de fora para examinar a pasta de novo e recarregar tudo.

/rdpl list mostra todos os pacotes carregados e o que há neles. Clica num
pacote para o ver.

/rdpl which minecraft:textures/blocks/stone.png mostra qual o pacote que serve
um ficheiro e quais os pacotes que ficam encobertos por baixo dele.

/rdpl config unused lista os ficheiros de opções em rdploader/config que nenhum
pacote instalado define já, e /rdpl config prune apaga-os. /rdpl pixelmap mostra
como ficou um mapa de píxeis, /rdpl biome list e here informam sobre os biomas,
e /rdpl team e /rdpl round servem para os lados e as rondas que um pacote
define.

Estes funcionam sem seres operador, porque só leem ficheiros do teu próprio
computador. Num servidor dedicado, /rdplserver reload examina de novo a cópia do
servidor.


SE ALGO NÃO FUNCIONAR
---------------------

Vê primeiro o registo. Progressos, tabelas de saque, receitas, funções e
estruturas são registados com o pacote de onde vieram, e tudo o que estiver
errado é registado como aviso a dizer porquê.

Para texturas e outros recursos, /rdpl unused lista qualquer ficheiro dos teus
pacotes que ainda não foi pedido por nada, o que costuma indicar um erro de
digitação no caminho. Executa-o depois de o jogo acabar de carregar, e lembra-te
de que alguns ficheiros só carregam quando são necessários, como idiomas
diferentes daquele em que jogas.

As maiúsculas importam. Se o teu ficheiro é Stone.png e o jogo pediu stone.png,
ainda carrega, mas um aviso manda-te renomeá-lo. Renomeia-o, porque fora deste
mod o ficheiro não será encontrado de todo. Os ficheiros de idioma são os que
mais apanham as pessoas: são en_us.lang, não en_US.lang.

Confirma que os teus ficheiros estão dentro de uma pasta 'assets'. Um zip sem
ela é ignorado, e o registo avisa.



PROGRESSOS E RECEITAS
---------------------

Se os teus scripts removem uma receita, qualquer progresso que a desbloqueava
continua a funcionar em vez de se estragar. Só que já não tem receita para dar,
e o registo nomeia-a uma vez.

Se substituíste essa receita por uma nova e queres que o progresso desbloqueie a
nova, dá um nome à receita nova no teu script:

    recipes.addShaped("rail", <minecraft:rail> * 16, [[...]]);

Isto regista-a como crafttweaker:rail. Depois coloca aqui um ficheiro de
progresso a apontar para esse nome, e o progresso volta a funcionar de ponta a
ponta.

Sem nome, ela vai chamar-se algo como crafttweaker:ct_shaped-1834729103, um hash
da própria receita. Isso muda assim que editas a receita, e pode deslocar-se se
for adicionada outra receita antes dela, por isso não é seguro apontar um
progresso para ele.


A própria pasta rdploader pode ser movida ou renomeada com a opção rootDirectory
em config/mct_resourcedatapackloader_mixin.cfg. Um caminho absoluto também
funciona, e é preciso reiniciar.

Coloca um pack.png ao lado deste ficheiro para dar um ícone ao pacote.

Este ficheiro é escrito pelo mod e atualizado sempre que muda, por isso tudo o
que escreveres nele é substituído da próxima vez que o jogo arrancar.

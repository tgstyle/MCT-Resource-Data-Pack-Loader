Resource Data Pack Loader
=========================

Tudo o que colocares nesta pasta substitui o que um mod ou o próprio Minecraft
fornece. Aplica-se a todos os mundos, num só jogador e em servidores, e não há
nada para ativar.


MAIS DO QUE SUBSTITUIÇÕES
-------------------------

Os pacotes aqui colocados também podem definir novos blocos, itens, biomas e
dimensões inteiras a partir de ficheiros JSON, decidir o que é gerado e onde,
trancar dimensões atrás de uma chave ou de um mob que tem de ser morto, e gerar
com antecedência a terra de um mundo para que ninguém espere por um chunk. O
HOWTO.md, que acompanha o mod, abrange tudo isto.


COMO ADICIONAR UM FICHEIRO
--------------------------

Abre o jar do mod, encontra o ficheiro que queres alterar e copia o caminho dele
a partir de 'assets' ou 'data'.

Para substituir a textura do minério de ferro, o ficheiro dentro do jar do
Minecraft é:

    assets/minecraft/textures/block/iron_ore.png

por isso a tua versão fica aqui:

    rdploader/assets/minecraft/textures/block/iron_ore.png

Uma tabela de saque fica em data e segue a mesma regra:

    data/minecraft/loot_table/blocks/iron_ore.json
    rdploader/data/minecraft/loot_table/blocks/iron_ore.json

Esta é a regra toda. O caminho a seguir a 'assets' ou 'data' é sempre igual ao
caminho dentro do jar, por isso nunca é preciso renomear nem mover nada.


MANTER TUDO ORGANIZADO
----------------------

Podes agrupar ficheiros num pacote com nome, como um zip:

    rdploader/MyTextures.zip (com 'assets' ou 'data' no nível superior do zip)

Ao comprimir, seleciona o conteúdo e comprime-o, não a pasta que o contém. Um
zip cujo nível superior é uma única pasta a envolver 'assets' ou 'data' é
ignorado, e o registo avisa.

Uma pasta em rdploader não é um pacote e é ignorada, e o registo avisa. Deixa os
ficheiros soltos em assets ou data, e comprime um pacote em zip antes de o
colocares aqui.

Se o mesmo ficheiro existir em dois sítios, um pacote com nome vence os
ficheiros soltos, e o /rdpl which diz-te qual venceu.


PRIORIDADE DOS PACOTES
----------------------

Se dois pacotes com nome tiverem o mesmo ficheiro, controla qual vence
acrescentando RDPL e um número antes do nome do zip. O RDPL0 carrega primeiro,
os números maiores carregam depois, e o pacote carregado em último vence:

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures.zip

Maiúsculas ou minúsculas funcionam, e um espaço, hífen ou sublinhado a seguir
ao número é opcional. O prefixo é retirado do nome do pacote no registo e no
/rdpl list, por isso RDPL1 SeasonalTextures aparece como SeasonalTextures.


API DE MODS
-----------

Um mod pode trazer conteúdo do RDPL dentro do próprio jar, numa pasta chamada
rdploader organizada exatamente como um pacote:

    thatmod.jar
      META-INF/neoforge.mods.toml
      rdploader/assets/thatmod/textures/block/ruby_ore.png

Estes são valores predefinidos, não substituições. O pacote de um mod carrega
abaixo de todos os pacotes desta pasta, por isso o que colocares aqui vence, e
um mod só pode fornecer ficheiros num namespace que declare no próprio
neoforge.mods.toml. Tudo o resto é ignorado com um aviso, para que nenhum mod
redefina em silêncio o conteúdo de outro nem o teu.

Todo o mod que traga um destes é listado em config/mods.json da primeira vez
que é visto:

    {
      "thatmod": {
        "enabled": true,
        "priority": -1
      }
    }

Define enabled como false para desligar o conteúdo desse mod. Deixa priority em
-1 para o manter por baixo de tudo, ou indica um número e ele ocupa o seu lugar
na ordem acima, ao lado dos pacotes numerados. Os pacotes aparecem do menor para
o maior no registo, e o de um mod vem assinalado, por isso nada carrega sem que
o vejas.


PACOTES DE RECURSOS
-------------------

Por predefinição, os ficheiros aqui ficam acima dos pacotes de recursos que o
jogador escolhe no ecrã de opções, por isso um pacote de recursos não os
consegue substituir. Isso é certo para coisas como o logótipo de um modpack e
errado para texturas que gostarias que as pessoas pudessem alterar.

Acrescenta O ou N a seguir ao prefixo RDPL para decidir pacote a pacote:

    rdploader/RDPLO Branding      vence sempre, pacotes de recursos não o mudam
    rdploader/RDPLN BaseTextures  um pacote de recursos pode substituí-lo
    rdploader/RDPL1O Seasonal     prioridade e vence sempre, as duas coisas

Os pacotes sem letra seguem a opção overrideResourcePacks da config, e o
/rdpl list assinala os que substituem.

A mesma regra vale para os pacotes de dados. Um pacote marcado com N fica abaixo
dos pacotes de dados que um mundo traz na sua própria pasta datapacks, e um
marcado com O fica acima deles.

Os pacotes sem prefixo carregam antes de todos os numerados, por ordem
alfabética, por isso um pacote numerado vence sempre um sem número.

Para desligar um pacote sem o apagar, acrescenta .disabled ao fim do nome:

    rdploader/RDPL1 SeasonalTextures.zip.disabled

O pacote é ignorado e o registo avisa. Remove o sufixo para o voltar a ligar.


O QUE PODES ALTERAR
-------------------

Texturas, modelos, blockstates, ficheiros de idioma, sons, tipos de letra,
textos de splash e tudo o mais que um mod guarde na pasta assets, como livros de
guia ou manuais.

Progressos, tabelas de saque, receitas, tags, funções, modelos de estruturas e
tudo o mais que um mod guarde na pasta data. Isto é do lado do servidor, por
isso funciona também num servidor dedicado, e uma alteração aqui tem efeito com
/reload.

Também é possível retirar coisas. Um ficheiro em recipe_removals apaga receitas
por nome, namespace ou resultado, e um ficheiro em disabled tira blocos e itens
de jogo sem os desregistar, por isso os mundos mantêm os seus ids e apagar o
ficheiro traz tudo de volta. Uma injeção de saque acrescenta uma pool a uma
tabela de saque que já existe em vez de a substituir, e block_drops acrescenta
ou substitui o que larga um bloco que não é teu. As receitas de fornalha, os
tempos de queima, os separadores criativos e os eventos de som também são
ficheiros próprios.

Uma bigorna pode aprender trabalho novo. Um ficheiro em anvils nomeia um item, o
item que o acompanha na ranhura da direita e os encantamentos ou o resultado que
a bigorna oferece pelos níveis indicados. Retirá-lo pode render um progresso, e
o item pode ficar inutilizável até esse progresso ser conseguido.

Um ficheiro em tooltips acrescenta linhas cinzentas de dica a itens e blocos e
uma descrição sob o nome de um encantamento.

Um ficheiro hardness define o tempo de mineração e a resistência a explosões de
um grupo de blocos, e um ficheiro exposures define um perigo como a radiação:
afeta jogadores perto de blocos indicados, a transportar itens indicados ou em
dimensões indicadas, em níveis que aplicam cada um efeitos e dano, e pode ser
apanhado de mobs e jogadores por perto ou cair com a chuva.


ADICIONAR CONTEÚDO NOVO
-----------------------

Um pacote também pode acrescentar blocos, itens e fluidos próprios, descritos em
JSON. Não precisas de escrever nem de compilar um mod para isso.

As definições ficam em data, uma pasta para cada tipo de coisa. Cada chave
dentro de "variants" é um nome, por isso um ficheiro em

    rdploader/data/mypack/blocks/ores.json

com uma variante chamada ruby_ore regista mypack:ruby_ore. O nome do ficheiro só
serve para agrupar. Se um mod a sério já registar esse nome, o mod vence e a tua
variante é ignorada.

O bloco mais simples são poucas linhas:

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "ruby_ore": { "hardness": 3.0, "harvestLevel": 1 }
      }
    }

Continuas a fornecer o modelo, o blockstate, a textura e a entrada de idioma em
assets, da mesma forma que qualquer outro ficheiro desta pasta.

Cada um destes é uma pasta em data/<yourpack>:

    blocks           items            fluids           materials
    tabs             sounds           biomes           worldgen
    caveregions      dimensions       worldtemplates   worldintro
    gates            gamerules        teams            scoring
    raids            entities         hardness         anvils
    exposures        overrides        villages         pathintersects
    structuremaps    citymaps         portalframes     blastplaster
    structure        recipe           recipe_removals  furnace
    fuels            brewing          potions          potion_types
    villagers        trades           loot_table       loot_injections
    block_drops      player_loot      advancement      function
    tags             registry_remap   cards            disabled
    tooltips
    harvests
    carries

Os blocos vêm nestas formas, definidas pelo campo "type":

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

e os itens nestas:

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   container

Um tipo de poção é nomeado pela chave de idioma
item.minecraft.potion.effect.<baseName>, com splash_potion, lingering_potion ou
tipped_arrow no lugar de potion para as outras formas. Um item potion_bottle é o
teu próprio recipiente para elas: aceita um creativeTab como qualquer outro item
e guarda os tipos de poção que indicares em potionTypes.

Um ficheiro villagers/<name>.json define uma profissão. Um ficheiro
trades/*.json acrescenta trocas a qualquer profissão, tua ou do Minecraft, a
indicar a profissão e o nível em que a troca aparece.

Um ficheiro entities/<name>.json cria uma entidade nova a partir de uma que já
existe. Indica a entidade em que se baseia e o que muda nela: o nome, o aspeto,
a vida e o dano que tem, como se move, como luta e o que larga. É uma entidade
própria, com o seu próprio ovo de spawn e a sua própria tabela de saque, e a
que lhe serviu de base fica intacta. Pode também levar um armazenamento próprio,
ranhuras de itens, um depósito de fluido e um buffer de energia a que canos e
cabos chegam e que o jogador abre ao agachar-se e clicar com o botão direito.

Um ficheiro villages/<name>.json acrescenta uma parcela que uma cidade ou aldeia
pode construir a partir de um dos teus modelos .nbt, e um ficheiro
raids/<name>.json envia vagas contra uma aldeia quando um jogador lhe leva um
presságio.

Um ficheiro cards/<name>.json mostra uma carta no ecrã quando algo acontece,
como um jogador entrar num bioma, e um ficheiro na mesma pasta com o nome de uma
das mensagens do próprio mod altera o que essa mensagem diz.

Um ficheiro worldintro/<name>.json apresenta uma série de páginas quando alguém
entra no mundo, antes de assumir o controlo. As palavras são ficheiros .txt
simples em assets/<yourpack>/texts. Pode ser apresentado uma vez por jogador ou
a cada entrada.

Um ficheiro teams/<name>.json coloca um lado no marcador do jogo e diz o que
entra nele, e um ficheiro scoring/<name>.json é um objetivo que atribui pontos
a esses lados e decide como termina uma partida.


MUNDOS INTEIROS
---------------

Um pacote não se limita a coisas avulsas. dimensions/<name>.json regista uma
dimensão com terreno, biomas e céu próprios. gates/<name>.json impõe uma
condição para chegar a uma, como segurar ou gastar um item. Um bloco do tipo
portal leva a outra dimensão quem entra nele, e portalframes deixa o jogador
construir e acender uma moldura própria.

worldtemplates/<name>.json reúne as definições de um mundo num só ficheiro, para
que um pacote entregue de uma vez a forma de um mundo inteiro em vez de pedir
uma dúzia de alterações de config. Também pode moldar o próprio mundo normal,
como o nível do mar e se os oceanos são de lava.

Uma dimensão também pode definir o aspeto e o comportamento por cima da tua
cabeça. O nevoeiro, a tonalidade da luz, o escurecimento do sol e da lua, as
camadas de nuvens e a ondulação do calor são desenhados no teu próprio ecrã, e a
meteorologia decide se chove, neva ou há tempestades, quanto duram os aguaceiros
e a cor, as partículas, o som e o ângulo da chuva.

worldgen é mais do que minério. Uma entrada coloca uma forma, desde um pequeno
aglomerado do teu bloco até um dos teus próprios modelos .nbt, e decide com que
frequência, a que altura e em que biomas aparece.

Um ficheiro biomes/<name>.json define um bioma: o clima e as cores, os blocos de
que é feito, o que o decora, o que nasce nele e onde é gerado.


ONDE ISTO TERMINA
-----------------

Isto descreve o que uma coisa é, não o que faz ao longo do tempo. Tudo o que
precise de uma entidade de bloco, de um ecrã ou de código a correr a cada tick
continua a precisar de um mod a sério, com duas exceções: um bloco do tipo
container guarda um inventário com ecrã próprio, e uma variante de entidade pode
levar armazenamento. Uma máquina está fora de alcance; um minério, uma cerca,
uma comida ou um fluido não.


PACOTES DE OUTRAS VERSÕES
-------------------------

Um pacote feito para a linha 1.12.2 deste mod carrega aqui tal como está. Um zip
é convertido uma única vez, dentro de si mesmo, numa pasta versions para esta
versão, e os ficheiros do 1.12.2 ficam como estavam, por isso o mesmo zip
continua a funcionar em todas as versões.


VER AS TUAS ALTERAÇÕES
----------------------

Prime F3+T para recarregar texturas, modelos, ficheiros de idioma e tudo o mais
em assets. Num servidor, ou para qualquer coisa em data, escreve /reload.

Se acrescentares um ficheiro novo ou apagares um, usa /rdpl reload. Editar um
ficheiro que já existia só precisa de F3+T ou /reload.

/rdpl list mostra todos os pacotes carregados e o que há neles. Passa o rato por
cima de um pacote para o ver.

/rdpl which minecraft:textures/block/stone.png mostra que pacote serve um
ficheiro e que pacotes ficam encobertos por baixo dele.

/rdpl config unused lista os ficheiros de opções em rdploader/config que nenhum
pacote instalado define já, e /rdpl config prune apaga-os. /rdpl pixelmap mostra
como ficou um mapa de píxeis, e /rdpl biome list e here dão informação sobre os
biomas.

Estes comandos funcionam sem seres operador, porque só leem ficheiros do teu
próprio computador. Num servidor dedicado, /rdplserver reload examina de novo a
cópia do servidor, e /rdplserver list, which e unused respondem por ela.


SE ALGO NÃO FUNCIONAR
---------------------

Vê primeiro o registo. O logs/rdpl.log lista cada pacote carregado e cada um que
foi ignorado, com o motivo, e tudo o que estiver errado é registado como aviso a
dizer porquê.

/rdpl unused lista qualquer ficheiro dos teus pacotes que ainda ninguém pediu,
o que costuma indicar um erro de escrita no caminho. Executa-o depois de o jogo
acabar de carregar, e tem presente que alguns ficheiros só carregam quando são
necessários, como idiomas diferentes daquele em que jogas.

As maiúsculas contam. Se o teu ficheiro é Stone.png e o jogo pediu stone.png,
carrega na mesma, mas um aviso diz-te para o renomear. Renomeia-o, porque fora
deste mod o ficheiro não será encontrado de todo. Os ficheiros de idioma são os
que mais baralham as pessoas: são en_us.json, não en_US.json.

Confirma que os teus ficheiros estão dentro de uma pasta 'assets' ou 'data'. Um
zip sem nenhuma das duas é ignorado, e o registo avisa.


PROGRESSOS E RECEITAS
---------------------

Uma receita que um script acrescenta ou substitui é conhecida pelo nome que o
script lhe dá. Para que um progresso a desbloqueie, coloca aqui um ficheiro de
progresso que nomeie essa receita, e o progresso volta a funcionar de ponta a
ponta.

Dá a essa receita um nome fixo no script. Um nome gerado
automaticamente pode mudar assim que editares a receita, por isso não é seguro
apontar um progresso para ele.


A própria pasta rdploader pode ser movida ou renomeada com a opção rootDirectory
em config/resourcedatapackloader-common.toml. Um caminho absoluto também
funciona, e é preciso reiniciar.

Coloca um pack.png ao lado deste ficheiro para dar um ícone ao pacote.

Este ficheiro é escrito pelo mod e atualizado sempre que muda, por isso tudo o
que escreveres nele é substituído da próxima vez que o jogo arrancar.

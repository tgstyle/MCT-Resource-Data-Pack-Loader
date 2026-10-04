Resource Data Pack Loader
=========================

Tudo que você colocar nesta pasta substitui o que um mod ou o próprio Minecraft
fornece. Vale para todos os mundos, no modo solo e em servidores, e não há nada
para ativar.


MAIS QUE SUBSTITUIÇÕES
----------------------

Os pacotes daqui também podem definir novos blocos, itens, biomas e dimensões
inteiras a partir de arquivos JSON, decidir o que é gerado e onde, trancar
dimensões atrás de uma chave ou de um mob que precisa ser morto, e gerar com
antecedência a terra de um mundo para que ninguém espere por um chunk. O
HOWTO.md, que acompanha o mod, cobre tudo isso.


COMO ADICIONAR UM ARQUIVO
-------------------------

Abra o jar do mod, encontre o arquivo que você quer mudar e copie o caminho dele
a partir de 'assets' ou 'data'.

Para substituir a textura do minério de ferro, o arquivo dentro do jar do
Minecraft é:

    assets/minecraft/textures/block/iron_ore.png

então a sua versão vai aqui:

    rdploader/assets/minecraft/textures/block/iron_ore.png

Uma tabela de saque fica em data, e funciona do mesmo jeito:

    data/minecraft/loot_table/blocks/iron_ore.json
    rdploader/data/minecraft/loot_table/blocks/iron_ore.json

Essa é a regra toda. O caminho depois de 'assets' ou 'data' é sempre igual ao
caminho dentro do jar, então nada precisa ser renomeado nem movido.


MANTENDO TUDO ORGANIZADO
------------------------

Você pode agrupar arquivos em um pacote nomeado, como um zip:

    rdploader/MyTextures.zip        (com 'assets' ou 'data' no nível superior do zip)

Ao compactar, selecione o conteúdo e compacte isso, não a pasta que o contém.
Um zip cujo nível superior é uma única pasta envolvendo 'assets' ou 'data' é
ignorado, e o log avisa.

Uma pasta em rdploader não é um pacote e é ignorada, e o log avisa. Deixe os
arquivos soltos em assets ou data, e compacte um pacote em zip antes de
colocá-lo aqui.

Se o mesmo arquivo existe em dois lugares, um pacote nomeado vence os arquivos
soltos, e o /rdpl which diz qual venceu.


PRIORIDADE DOS PACOTES
----------------------

Se dois pacotes nomeados têm o mesmo arquivo, controle qual vence colocando RDPL
e um número antes do nome do zip. O RDPL0 carrega primeiro, números maiores
carregam depois, e o pacote carregado por último vence:

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures.zip

Maiúsculas ou minúsculas funcionam, e um espaço, hífen ou sublinhado depois do
número é opcional. O prefixo é removido do nome do pacote no log e no
/rdpl list, então RDPL1 SeasonalTextures aparece como SeasonalTextures.


API PARA MODS
-------------

Um mod pode levar conteúdo do RDPL dentro do próprio jar, em uma pasta chamada
rdploader organizada exatamente como um pacote:

    thatmod.jar
      META-INF/neoforge.mods.toml
      rdploader/assets/thatmod/textures/block/ruby_ore.png

Esses são padrões, não substituições. O pacote de um mod carrega abaixo de todos
os pacotes desta pasta, então o que você colocar aqui vence, e um mod só pode
fornecer arquivos em um namespace que declare no próprio neoforge.mods.toml.
Todo o resto é ignorado com um aviso, para que nenhum mod redefina em silêncio o
conteúdo de outro nem o seu.

Todo mod que traz um desses entra em config/mods.json na primeira vez que é
visto:

    {
      "thatmod": {
        "enabled": true,
        "priority": -1
      }
    }

Defina enabled como false para desligar o conteúdo desse mod. Deixe priority em
-1 para mantê-lo abaixo de tudo, ou dê um número e ele ocupa seu lugar na ordem
acima, junto dos pacotes numerados. Os pacotes aparecem do menor para o maior no
log, e o de um mod vem marcado, então nada carrega sem que você veja.


PACOTES DE RECURSOS
-------------------

Por padrão, os arquivos daqui ficam acima dos pacotes de recursos que o jogador
escolhe na tela de opções, então um pacote de recursos não consegue substituí-los.
Isso é certo para coisas como o logo de um modpack e errado para texturas que
você gostaria que as pessoas pudessem retexturizar.

Adicione O ou N depois do prefixo RDPL para decidir pacote por pacote:

    rdploader/RDPLO Branding          sempre vence, pacotes de recursos não o tocam
    rdploader/RDPLN BaseTextures      um pacote de recursos pode substituí-lo
    rdploader/RDPL1O Seasonal         prioridade e sempre vence, as duas coisas

Pacotes sem letra seguem a opção overrideResourcePacks da configuração, e o
/rdpl list marca os que substituem.

A mesma regra vale para data packs. Um pacote marcado com N fica abaixo dos data
packs que um mundo traz na própria pasta datapacks, e um marcado com O fica
acima deles.

Pacotes sem prefixo carregam antes de todos os numerados, em ordem alfabética,
então um pacote numerado sempre vence um sem número.

Para desligar um pacote sem apagá-lo, adicione .disabled ao fim do nome:

    rdploader/RDPL1 SeasonalTextures.zip.disabled

O pacote é ignorado e o log avisa. Remova o sufixo para ligá-lo de novo.


O QUE VOCÊ PODE MUDAR
---------------------

Texturas, modelos, estados de bloco, arquivos de idioma, sons, fontes, textos de
splash e qualquer outra coisa que um mod guarde na pasta assets, como livros de
guia ou manuais.

Conquistas, tabelas de saque, receitas, tags, funções, modelos de estruturas e
qualquer outra coisa que um mod guarde na pasta data. Isso é do lado do
servidor, então funciona também em um servidor dedicado, e uma mudança aqui tem
efeito com /reload.

Dá também para tirar coisas. Um arquivo em recipe_removals apaga receitas por
nome, namespace ou resultado, e um arquivo em disabled tira blocos e itens do
jogo sem desregistrá-los, então os mundos mantêm seus ids e apagar o arquivo
traz tudo de volta. Uma injeção de saque adiciona um pool a uma tabela de saque
que já existe em vez de substituí-la, e block_drops adiciona ou substitui o que
um bloco que não é seu solta. Receitas de fornalha, tempos de queima, abas
criativas e eventos de som também são arquivos próprios.

Uma bigorna pode aprender trabalho novo. Um arquivo em anvils nomeia um item, o
item que o acompanha no espaço da direita e os encantamentos ou o resultado que
a bigorna oferece pelos níveis indicados. Retirá-lo pode render uma conquista, e
o item pode ficar inutilizável até que essa conquista seja obtida.

Um arquivo hardness define o tempo de mineração e a resistência a explosões de
um grupo de blocos, e um arquivo exposures define um perigo como radiação: ele
atinge jogadores perto de blocos nomeados, carregando itens nomeados ou em
dimensões nomeadas, em níveis que aplicam cada um efeitos e dano, e pode ser
pego de mobs e jogadores por perto ou cair com a chuva.


ADICIONANDO CONTEÚDO NOVO
-------------------------

Um pacote também pode adicionar blocos, itens e fluidos próprios, descritos em
JSON. Você não precisa escrever nem compilar um mod para isso.

As definições ficam em data, uma pasta para cada tipo de coisa. Cada chave dentro
de "variants" é um nome, então um arquivo em

    rdploader/data/mypack/blocks/ores.json

com uma variante chamada ruby_ore registra mypack:ruby_ore. O nome do arquivo só
agrupa. Se um mod de verdade já registra esse nome, o mod vence e a sua variante
é ignorada.

O bloco mais simples são poucas linhas:

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "ruby_ore": { "hardness": 3.0, "harvestLevel": 1 }
      }
    }

Você ainda fornece o modelo, o estado de bloco, a textura e a entrada de idioma
em assets, do mesmo jeito que qualquer outro arquivo desta pasta.

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
seu próprio recipiente para elas: recebe um creativeTab como qualquer outro item
e guarda os tipos de poção que você nomear em potionTypes.

Um arquivo villagers/<name>.json define uma profissão. Um arquivo trades/*.json
adiciona trocas a qualquer profissão, sua ou do Minecraft, nomeando a profissão
e o nível em que a troca aparece.

Um arquivo entities/<name>.json cria uma entidade nova a partir de uma que já
existe. Ele nomeia a entidade em que se baseia e o que muda nela: nome, aparência,
quanta vida e dano tem, como se move, como luta e o que solta. É uma entidade
própria, com seu próprio ovo de geração e sua própria tabela de saque, e aquela
em que foi baseada fica intacta. Ela também pode levar um armazenamento próprio,
espaços de itens, um tanque de fluido e um buffer de energia que canos e cabos
alcançam e que o jogador abre agachando e clicando com o botão direito nela.

Um arquivo villages/<name>.json adiciona um terreno que uma cidade ou vila pode
construir a partir de um dos seus modelos .nbt, e um arquivo raids/<name>.json
manda ondas contra uma vila quando um jogador leva um presságio até ela.

Um arquivo cards/<name>.json mostra uma carta na tela quando algo acontece, como
um jogador entrar em um bioma, e um arquivo na mesma pasta com o nome de uma das
mensagens do próprio mod muda o que essa mensagem diz.

Um arquivo worldintro/<name>.json exibe uma série de páginas quando alguém entra
no mundo, antes de assumir o controle. As palavras são arquivos .txt comuns em
assets/<yourpack>/texts. Pode tocar uma vez por jogador ou a cada entrada.

Um arquivo teams/<name>.json coloca um lado no placar do jogo e diz o que entra
nele, e um arquivo scoring/<name>.json é um objetivo que dá pontos a esses lados
e decide como uma partida termina.


MUNDOS INTEIROS
---------------

Um pacote não se limita a coisas avulsas. dimensions/<name>.json registra uma
dimensão com terreno, biomas e céu próprios. gates/<name>.json coloca uma
condição para chegar a uma, como segurar ou gastar um item. Um bloco do tipo
portal leva a outra dimensão quem entra nele, e portalframes deixa o jogador
construir e acender uma moldura própria.

worldtemplates/<name>.json reúne as configurações de um mundo em um só arquivo,
para que um pacote entregue de uma vez a forma de um mundo inteiro em vez de
pedir uma dúzia de mudanças de configuração. Ele também pode moldar a própria
superfície, como o nível do mar e se os oceanos são de lava.

Uma dimensão também pode definir como ela parece e se comporta acima da sua
cabeça. Neblina, matiz da luz, escurecimento do sol e da lua, camadas de nuvens
e ondulação de calor são desenhados na sua própria tela, e o clima decide se
chove, neva ou há tempestades, quanto duram as pancadas de chuva e a cor, as
partículas, o som e o ângulo da chuva.

worldgen é mais que minério. Uma entrada coloca uma forma, de um pequeno
amontoado do seu bloco até um dos seus próprios modelos .nbt, e decide com que
frequência, em que altura e em quais biomas aparece.

Um arquivo biomes/<name>.json define um bioma: o clima e as cores, os blocos de
que é feito, o que o decora, o que nasce nele e onde é gerado.


ONDE ISSO TERMINA
-----------------

Isto descreve o que uma coisa é, não o que ela faz ao longo do tempo. Tudo que
precisar de uma entidade de bloco, de uma tela ou de código rodando a cada tick
ainda precisa de um mod de verdade, com duas exceções: um bloco do tipo
container guarda um inventário com tela própria, e uma variante de entidade pode
levar armazenamento. Uma máquina está fora de alcance; um minério, uma cerca,
uma comida ou um fluido não.


PACOTES DE OUTRAS VERSÕES
-------------------------

Um pacote feito para a linha 1.12.2 deste mod carrega aqui como está. Um zip é
convertido uma única vez, dentro dele mesmo, em uma pasta versions para esta
versão, e os arquivos do 1.12.2 ficam como estavam, então o mesmo zip continua
funcionando em todas as versões.


VENDO SUAS MUDANÇAS
-------------------

Pressione F3+T para recarregar texturas, modelos, arquivos de idioma e todo o
resto em assets. Em um servidor, ou para qualquer coisa em data, digite /reload.

Se você adicionar um arquivo novo ou apagar um, use /rdpl reload. Editar um
arquivo que já existia só precisa de F3+T ou /reload.

/rdpl list mostra todos os pacotes carregados e o que há neles. Passe o mouse
sobre um pacote para vê-lo.

/rdpl which minecraft:textures/block/stone.png mostra qual pacote serve um
arquivo e quais pacotes ficam encobertos por baixo.

/rdpl config unused lista os arquivos de opções em rdploader/config que nenhum
pacote instalado define mais, e /rdpl config prune apaga todos eles. /rdpl
pixelmap mostra como ficou um mapa de pixels, e /rdpl biome list e here
informam sobre os biomas.

Eles funcionam sem ser operador, porque só leem arquivos do seu próprio
computador. Em um servidor dedicado, /rdplserver reload examina de novo a cópia
do servidor, e /rdplserver list, which e unused respondem por ela.


SE ALGO NÃO FUNCIONAR
---------------------

Veja o log primeiro. O logs/rdpl.log lista cada pacote carregado e cada um que
foi ignorado, com o motivo, e tudo que estiver errado é registrado como aviso
dizendo por quê.

/rdpl unused lista qualquer arquivo dos seus pacotes que ainda não foi pedido
por nada, o que costuma indicar um erro de digitação no caminho. Rode depois que
o jogo terminar de carregar, e lembre que alguns arquivos só carregam quando são
necessários, como idiomas diferentes do que você joga.

Maiúsculas importam. Se o seu arquivo é Stone.png e o jogo pediu stone.png, ele
ainda carrega, mas um aviso manda você renomeá-lo. Renomeie, porque fora deste
mod o arquivo não será encontrado de jeito nenhum. Os arquivos de idioma são os
que mais pegam as pessoas: são en_us.json, não en_US.json.

Confira se os seus arquivos estão dentro de uma pasta 'assets' ou 'data'. Um zip
sem nenhuma das duas é ignorado, e o log avisa.


CONQUISTAS E RECEITAS
---------------------

Uma receita que um script adiciona ou substitui é conhecida pelo nome que o
script dá a ela. Para que uma conquista a desbloqueie, coloque aqui um arquivo de
conquista que nomeie essa receita, e a conquista volta a funcionar de ponta a
ponta.

Dê a essa receita um nome fixo no script. Um nome inventado pelo sistema pode
mudar assim que você editar a receita, então não é seguro apontar uma conquista
para ele.


A própria pasta rdploader pode ser movida ou renomeada com a opção rootDirectory
em config/resourcedatapackloader-common.toml. Um caminho absoluto também
funciona, e é preciso reiniciar.

Coloque um pack.png ao lado deste arquivo para dar um ícone ao pacote.

Este arquivo é escrito pelo mod e atualizado sempre que muda, então tudo que
você digitar nele é substituído na próxima vez que o jogo iniciar.

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
a partir de 'assets'.

Para substituir a textura do minério de ferro, o arquivo dentro do jar do
Minecraft é:

    assets/minecraft/textures/blocks/iron_ore.png

então a sua versão vai aqui:

    rdploader/assets/minecraft/textures/blocks/iron_ore.png

Essa é a regra toda. O caminho depois de 'assets' é sempre igual ao caminho
dentro do jar, então nada precisa ser renomeado nem movido.


MANTENDO TUDO ORGANIZADO
------------------------

Você pode agrupar arquivos em um pacote nomeado, como um zip:

    rdploader/MyTextures.zip        (com 'assets' no nível superior do zip)

Ao compactar, selecione o conteúdo e compacte isso, não a pasta que o contém.
Um zip cujo nível superior é uma única pasta envolvendo 'assets' é ignorado, e o
log avisa.

Uma pasta em rdploader não é um pacote e é ignorada, e o log avisa. Deixe os
arquivos soltos em assets, e compacte um pacote em zip antes de colocá-lo aqui.

Se o mesmo arquivo existe em dois lugares, um pacote nomeado vence os arquivos
soltos. O log nomeia o pacote de onde veio cada arquivo, então você sempre pode
ver qual venceu.


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
      mcmod.info
      rdploader/assets/thatmod/blocks/ruby_ore.json

Esses são padrões, não substituições. O pacote de um mod carrega abaixo de todos
os pacotes desta pasta, então o que você colocar aqui vence, e um mod só pode
fornecer arquivos em um namespace que declare no próprio mcmod.info. Todo o
resto é ignorado com um aviso, para que nenhum mod redefina em silêncio o
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

Conquistas e tabelas de saque. Isso é do lado do servidor, então funciona também
em um servidor dedicado.

Modelos de estruturas, os arquivos .nbt em assets/<modid>/structures. Uma
estrutura salva na própria pasta structures do mundo ainda vence um arquivo
daqui, e uma estrutura que já foi colocada continua carregada até você sair do
mundo.

Receitas, incluindo substituir a receita de um mod ou adicionar uma sua. As
receitas só carregam quando o jogo inicia, então uma mudança aqui precisa de
reinício, não de recarga.

Funções, os arquivos .mcfunction em assets/<modid>/functions. O Minecraft só os
lê da pasta data do próprio mundo, então colocá-los aqui faz com que funcionem
em todos os mundos. Uma função salva no mundo ainda vence um arquivo daqui.

Renomeações de registro, para que um mundo salvo antes de um mod renomear um dos
seus blocos mantenha esse bloco em vez de perdê-lo. Coloque um arquivo em
assets/<modid>/registry_remap:

    {
      "registry": "minecraft:items",
      "mapping": { "oldmod:old_name": "newmod:new_name" }
    }

O registro é aquele a que a entrada pertence, normalmente minecraft:items ou
minecraft:blocks. As renomeações se encadeiam, então mapear A para B e depois B
para C leva A a C.

Propriedades de coisas que já existem, sem mexer nos arquivos delas. Um arquivo
em assets/<yourpack>/overrides nomeia o alvo pelo caminho, então
overrides/minecraft/stone.json muda minecraft:stone, seja vanilla ou de um mod.
Os blocos aceitam dureza, resistência a explosões, luz, opacidade à luz,
escorregadia, som, ferramenta e nível de coleta, e inflamabilidade. Os itens
aceitam tamanho de pilha, durabilidade e um item de recipiente, e qualquer item
pode se tornar comestível, com valores de comida e efeitos. Os efeitos de uma
poção podem ser reescritos. Tudo isso é ao vivo: desative o pacote e rode
/rdpl reload, e cada valor volta ao que era, sem reiniciar. Coloque o id do mod
dono em "requires" para que o arquivo seja ignorado em silêncio quando esse mod
não estiver instalado.

Saque de jogadores, algo para o qual o jogo não tem nome. Os jogadores soltam o
inventário e mais nada, então um arquivo em assets/<modid>/player_loot dá a eles
uma tabela de saque própria:

    {
      "table": "mypack:entities/player",
      "mode": "add",
      "rollOnKeepInventory": false
    }

"add" solta o que a tabela sortear junto com tudo que eles carregavam, "replace"
solta isso no lugar do inventário, e rollOnKeepInventory decide se a tabela
chega a ser sorteada em um mundo onde os inventários são mantidos.

Um estandarte é a exceção. Uma definição registra dois blocos, o seu e um
segundo chamado <name>_wall, e só o em pé recebe um item, que escolhe entre eles
ao ser colocado. O modelo vai bem além do próprio bloco, até 29,33 de 16 em pé e
13 abaixo do bloco na parede, e o estado de bloco em pé precisa do formato do
Forge para girar em dezesseis avos. Os guias têm as medidas completas.

Os drops de um bloco podem ser aleatórios e não precisam ser itens. As entradas
da lista drops são decididas cada uma por si, a menos que você dê um peso a
elas, e então dividem um único pote e exatamente uma delas sai. Uma entrada que
nomeia uma entidade em vez de um bloco solta essa entidade onde o bloco estava.

Uma textura pode ser escrita em JSON em vez de desenhada. Dê ao arquivo o nome
do PNG com .json no fim, textures/blocks/panel.png.json, e dê a ele um tamanho
como 16x16 ou 16x32, uma paleta de um caractere para uma cor e linhas desses
caracteres de cima para baixo. Outro arquivo assim pode estendê-lo e nomear só
as cores que quer diferentes, de modo que uma forma pode ser recolorida quantas
vezes você quiser sem um único arquivo de imagem. O que eles desenham fica em
pixelmap-cache aqui e é redesenhado sempre que um mapa ou seu modelo muda.

Dá também para tirar coisas. Um arquivo em recipe_removals apaga receitas por
nome, namespace ou resultado, e um arquivo em disabled tira blocos e itens do
jogo sem desregistrá-los, então os mundos mantêm seus ids e apagar o arquivo
traz tudo de volta. Uma injeção de saque adiciona um pool a uma tabela de saque
que já existe em vez de substituí-la, e block_drops adiciona ou substitui o que
um bloco que não é seu solta. Receitas de fornalha, tempos de queima, nomes do
dicionário de minérios, abas criativas e eventos de som também são arquivos
próprios.

Uma bigorna pode aprender trabalho novo. Um arquivo em anvils nomeia um item, o
item que o acompanha no espaço da direita e os encantamentos ou o resultado que
a bigorna oferece pelos níveis indicados. Retirá-lo pode render uma conquista, e
o item pode ficar inutilizável até que essa conquista seja obtida.

Um arquivo hardness define o tempo de mineração e a resistência a explosões de
um grupo de blocos, e um arquivo exposures define um perigo como radiação: ele
atinge jogadores perto de blocos nomeados, carregando itens nomeados ou em
dimensões nomeadas, em níveis que aplicam cada um efeitos e dano, e pode ser
pego de mobs e jogadores por perto ou cair com a chuva.

CraftTweaker e GroovyScript continuam funcionando exatamente como antes. Eles
rodam depois deste mod, então o que seus scripts removerem ou mudarem vence um
arquivo daqui.

O RDPL é bom para substituir uma ou duas receitas, e as receitas do seu próprio
conteúdo pertencem ao pacote que o acompanha. Para controle total de receitas em
um modpack, CraftTweaker e GroovyScript são opções melhores. Um arquivo daqui
substitui o original por completo, então para mudar um ingrediente ou tirar uma
entrada de saque, use esses.


ADICIONANDO CONTEÚDO NOVO
-------------------------

Um pacote também pode adicionar blocos, itens e fluidos próprios, descritos em
JSON. Você não precisa escrever nem compilar um mod para isso.

O caminho do arquivo é o nome dele. Um bloco em

    rdploader/MyPack/assets/mypack/blocks/copper_ore.json

se registra como mypack:copper_ore. Não há campo de nome para preencher nem para
errar. Se um mod de verdade já registra esse nome, o mod vence e o seu arquivo é
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

Você ainda fornece o modelo, o estado de bloco, a textura e a entrada de idioma
do mesmo jeito que qualquer outro arquivo desta pasta.

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
    celestial        raids

Os blocos vêm nestas formas, definidas pelo campo "type":

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

e os itens nestas:

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   rocket

Um tipo de poção sempre aparece na poção vanilla, na poção arremessável, na
poção persistente e na flecha com efeito, que ficam nas abas de Poções e
Combate. A aba é uma propriedade do item, não do tipo de poção, então não há
como movê-las para uma aba sua. Um item potion_bottle é, em vez disso, o seu
próprio recipiente: recebe um creativeTab como qualquer outro item, lista os
tipos de poção que você nomear em potionTypes, e o suporte de poções o aceita
onde quer que um frasco de vidro sirva.

Um arquivo villagers/<name>.json define uma profissão e as carreiras que ela
oferece. Um arquivo trades/*.json adiciona trocas a qualquer carreira, sua ou do
Minecraft, nomeando a profissão, a carreira e o nível em que a troca aparece.
Nomeie uma carreira que não existe e o log lista as que existem.

Um arquivo entities/<name>.json cria uma entidade nova a partir de uma que já
existe. Ele nomeia a entidade em que se baseia e o que muda nela: nome, skin,
quanta vida e dano tem, a velocidade com que se move e a altura que pula, o
tamanho com que é desenhada, o que veste, o que caça e o que ignora, e se ainda
obedece às regras de surgimento da entidade de que foi feita. É uma entidade
própria, com seu próprio ovo de geração e sua própria tabela de saque, e aquela
em que foi baseada fica intacta. Um terreno de vila pode ser instruído a abrigar
uma no lugar de um aldeão. Ela também pode levar um armazenamento próprio,
espaços de itens, um tanque de fluido e um buffer de energia que canos e cabos
alcançam e que o jogador abre agachando e clicando com o botão direito nela, e
um foguete do Galacticraft pode variar do mesmo jeito e ser colocado em uma
plataforma de lançamento por um item do tipo rocket.

Um arquivo villages/<name>.json adiciona um terreno que as vilas podem
construir, seja uma fazenda que você descreve ou um dos seus modelos .nbt. As
mesmas configurações escolhem quais peças vanilla ainda aparecem, a que
distância as vilas são semeadas umas das outras e em quais biomas são
permitidas.

Um arquivo worldintro/<name>.json exibe uma série de páginas quando alguém entra
no mundo, antes de assumir o controle: texto rolando sobre uma imagem, um cartão
de título, uma apresentação de slides, com música ao fundo se você quiser. As
palavras são arquivos .txt comuns em assets/<yourpack>/texts. Pode tocar uma vez
por jogador ou a cada entrada.

Um arquivo teams/<name>.json coloca um lado no placar do jogo em que mobs,
jogadores ou qualquer coisa que surja em um canto do mundo entram ao chegar, e
um arquivo scoring/<name>.json é um objetivo que pontua abates e mortes para
esses lados, encerra uma rodada por pontuação ou por tempo, mostra a
classificação no chat ou em uma carta, e pode reiniciar o mapa para a próxima
rodada.

Um arquivo raids/<name>.json manda ondas contra uma vila quando um jogador com o
efeito omen entra nela. Uma barra de chefe mostra quantos invasores restam, os
aldeões correm para dentro, os invasores derrubam portas de madeira, e todo sino
da vila toca quando uma onda chega.

Um arquivo cards/<name>.json mostra uma carta na tela quando algo acontece, como
um jogador entrar em um bioma, e um arquivo na mesma pasta com o nome de uma das
mensagens do próprio mod muda o que essa mensagem diz.


MUNDOS INTEIROS
---------------

Um pacote não se limita a coisas avulsas. dimensions/<name>.json registra uma
dimensão com terreno, biomas e céu próprios. gates/<name>.json coloca uma
condição para chegar a uma, como segurar ou gastar um item. Um bloco do tipo
portal leva quem entra nele e lembra quem o construiu.

Um modelo de mundo também pode moldar a própria superfície, definindo o nível do
mar, oceanos de lava e o ruído do terreno. Isso é aplicado quando um mundo é
criado e nunca depois, então um mundo que já existe fica como estava.

worldtemplates/<name>.json reúne as configurações de um mundo em um só arquivo,
para que um pacote entregue de uma vez a forma de um mundo inteiro em vez de
pedir uma dúzia de mudanças de configuração. Todo grupo que ele pode definir
responde também à categoria control da configuração, que decide se o pacote
manda, se a configuração manda, ou se o grupo está desligado por completo e
nenhum pacote consegue ligá-lo.

Uma dimensão também pode definir como ela parece e se comporta acima da sua
cabeça. Neblina, matiz da luz, escurecimento do sol e da lua, camadas de nuvens
e ondulação de calor são desenhados na sua própria tela, e o clima decide se
chove, neva ou há tempestades, quanto duram as pancadas de chuva e a cor, as
partículas, o som e o ângulo da chuva.

Com o Galacticraft instalado, celestial/<name>.json coloca os sistemas
estelares, planetas, luas, cinturões de asteroides e estações espaciais de um
pacote no mapa estelar do Galacticraft, e uma dimensão com um bloco galacticraft
passa a ser um lugar para onde um foguete voa.

worldgen é mais que minério. Uma entrada é uma forma colocada por uma
distribuição: amontoados, veios longos, placas, geodos, bacias, agulhas,
nódulos, respiradouros, decoração de superfície, árvores inteiras, trepadeiras,
cinturões que abrangem vários chunks ou um dos seus próprios modelos .nbt,
espalhados de forma uniforme, em torno de uma altura, de forma fractal,
acompanhando o terreno, em chãos ou tetos de cavernas, ou debaixo d'água.


Um arquivo biomes/<name>.json define um bioma: o clima e as cores, os blocos de
que é feito, o que o decora, o que nasce nele e onde é gerado. O número dele é
escolhido para você e gravado em cada mundo na primeira vez que esse mundo
carrega, então fica fixo depois, não importa o que mais você instale. Defina
"id" só quando um bioma precisar manter um número que outra coisa já usava,
como quando um pacote substitui um mod que está sendo aposentado. Renomear ou
apagar um bioma que um mundo já contém o perde, o mesmo que renumerar um bloco,
então use registry_remap para uma renomeação.

O nome exibido de um aldeão é a chave de idioma entity.Villager.<career>, com o
nome da carreira exatamente como você escreveu e mais nada. Esse espaço de
chaves é compartilhado com o Minecraft e todos os outros pacotes, então coloque
o seu namespace no nome da carreira, como em rdpltest.prospector. Só o nome é
afetado: um aldeão guarda a carreira como um número, então renomear uma muda
como os aldeões existentes são chamados, e reordenar a lista de carreiras muda
qual carreira eles têm.

Um tipo de poção é nomeado pelo arquivo do mesmo jeito, e o nome exibido vem da
chave de idioma potion.effect.<namespace>.<name>, com splash_potion.effect.,
lingering_potion.effect. e tipped_arrow.effect. para as outras três formas.


MUNDOS RUBIC
------------

Um modelo de mundo pode pedir um mundo construído com cubos em vez de colunas de
256 blocos, e então o mundo cresce nos dois sentidos: um piso bem abaixo de
zero, um teto bem acima de 255, com terreno, cavernas e minérios em tudo. Jogar
em um é normal. Você cava, constrói, ilumina e viaja do mesmo jeito, e a janela
de geração do vanilla mantém seu formato de sempre dentro do mundo mais alto,
então os mods que geram terreno o colocam onde sempre colocaram.

O que um pacote ganha: uma altura de mundo própria, definida uma vez quando o
mundo é criado; um mundo profundo sob a janela do vanilla, com cavernas de
ruído, aquíferos e veios de minério em faixas em uma pedra que você nomeia;
regiões de caverna, a resposta dos pacotes aos biomas de caverna, pintadas pelo
subsolo em três dimensões com seus próprios pisos, tetos, nível da água, mobs e
estruturas; dimensões empilhadas umas sobre as outras, de modo que cair pelo
fundo de um mundo leva ao próximo mais abaixo e sair pelo topo traz de volta; e
qualquer dimensão deixada de fora, mantendo seu mundo comum no mesmo save.

Por baixo, um mundo é guardado como cubos de 16 por 16 por 16 em arquivos de
região próprios ao lado dos do vanilla, gerados, carregados e salvos por conta
própria, com um motor de luz escrito para esse formato. A suposição do vanilla
de que um mundo tem 256 blocos de altura está corrigida onde quer que pese,
dos limites de construção e planos de morte ao pathfinding, portais, faróis,
mapas e o renderizador. Os geradores de outros mods continuam vendo uma janela
de aparência normal de 256 blocos, e é por isso que o terreno deles funciona.

O HOWTO.md tem as configurações, as alturas que um mundo pode ter e os mods ao
lado dos quais isto não vai rodar.


UM AVISO SOBRE META
-------------------

Toda variante tem um número meta, e esse número é o que o arquivo do mundo
guarda. Renumerar uma variante que as pessoas já têm em um mundo transforma os
blocos delas em outra coisa. Adicione variantes novas no fim e nunca renumere
uma antiga.

Um bloco comporta 16 variantes, porque é o que quatro bits de metadados
permitem. As lajes têm 8, já que um bit diz em cima ou embaixo, e escadas,
escadas de mão, tochas e plantações têm 1, porque a orientação ou a idade usam o
resto. Os itens não são tão apertados e podem pular números.


ONDE ISSO TERMINA
-----------------

Isto descreve o que uma coisa é, não o que ela faz ao longo do tempo. Tudo que
precisar de uma tile entity, de uma GUI ou de código rodando a cada tick ainda
precisa de um mod de verdade, com duas exceções: um bloco do tipo container
guarda um inventário com tela própria, e uma variante de entidade pode levar
armazenamento. Uma máquina está fora de alcance; um minério, uma cerca, uma
comida ou um fluido não.


PACOTES DE OUTRAS VERSÕES
-------------------------

Um pacote feito para a linha 1.20.1, 1.21.1 ou 26.x deste mod carrega aqui
também. Um zip é convertido uma única vez, dentro dele mesmo, em uma pasta
versions/1.12.2, e os arquivos modernos ficam como estavam, então o mesmo zip
continua funcionando em todas as versões.


VENDO SUAS MUDANÇAS
-------------------

Pressione F3+T para recarregar texturas, modelos, arquivos de idioma, conquistas
e tabelas de saque. Em um servidor, digite /reload para o mesmo efeito. As
receitas são a exceção, como acima: só carregam na inicialização, então uma
mudança de receita precisa de reinício.

Se você adicionar um arquivo novo ou apagar um, use /rdpl reload. Editar um
arquivo que já existia só precisa de F3+T.

/rdpl reload textures recarrega só as texturas, o que é muito mais rápido que
F3+T em um pacote grande. models, languages, sounds e shaders funcionam do mesmo
jeito. Deixe o nome de fora para examinar a pasta de novo e recarregar tudo.

/rdpl list mostra todos os pacotes carregados e o que há neles. Clique em um
pacote para vê-lo.

/rdpl which minecraft:textures/blocks/stone.png mostra qual pacote serve um
arquivo e quais pacotes ficam encobertos por baixo.

/rdpl config unused lista os arquivos de opções em rdploader/config que nenhum
pacote instalado define mais, e /rdpl config prune apaga todos eles. /rdpl
pixelmap mostra como ficou um mapa de pixels, /rdpl biome list e here informam
sobre os biomas, e /rdpl team e /rdpl round servem para os lados e as rodadas
que um pacote define.

Eles funcionam sem ser operador, porque só leem arquivos do seu próprio
computador. Em um servidor dedicado, /rdplserver reload examina de novo a cópia
do servidor.


SE ALGO NÃO FUNCIONAR
---------------------

Veja o log primeiro. Conquistas, tabelas de saque, receitas, funções e
estruturas são registradas com o pacote de onde vieram, e tudo que estiver
errado é registrado como aviso dizendo por quê.

Para texturas e outros recursos, /rdpl unused lista qualquer arquivo dos seus
pacotes que ainda não foi pedido por nada, o que costuma indicar um erro de
digitação no caminho. Rode depois que o jogo terminar de carregar, e lembre que
alguns arquivos só carregam quando são necessários, como idiomas diferentes do
que você joga.

Maiúsculas importam. Se o seu arquivo é Stone.png e o jogo pediu stone.png, ele
ainda carrega, mas um aviso manda você renomeá-lo. Renomeie, porque fora deste
mod o arquivo não será encontrado de jeito nenhum. Os arquivos de idioma são os
que mais pegam as pessoas: são en_us.lang, não en_US.lang.

Confira se os seus arquivos estão dentro de uma pasta 'assets'. Um zip sem ela é
ignorado, e o log avisa.



CONQUISTAS E RECEITAS
---------------------

Se os seus scripts removem uma receita, qualquer conquista que a desbloqueava
continua funcionando em vez de quebrar. Só que ela não tem mais receita para
dar, e o log a nomeia uma vez.

Se você substituiu essa receita por uma nova e quer que a conquista desbloqueie
a nova, dê um nome à receita nova no seu script:

    recipes.addShaped("rail", <minecraft:rail> * 16, [[...]]);

Isso a registra como crafttweaker:rail. Depois coloque aqui um arquivo de
conquista apontando para esse nome, e a conquista volta a funcionar de ponta a
ponta.

Sem nome, ela vai se chamar algo como crafttweaker:ct_shaped-1834729103, um hash
da própria receita. Isso muda assim que você edita a receita, e pode se deslocar
se outra receita for adicionada antes dela, então não é seguro apontar uma
conquista para ele.


A própria pasta rdploader pode ser movida ou renomeada com a opção rootDirectory
em config/mct_resourcedatapackloader_mixin.cfg. Um caminho absoluto também
funciona, e é preciso reiniciar.

Coloque um pack.png ao lado deste arquivo para dar um ícone ao pacote.

Este arquivo é escrito pelo mod e atualizado sempre que muda, então tudo que
você digitar nele é substituído na próxima vez que o jogo iniciar.
